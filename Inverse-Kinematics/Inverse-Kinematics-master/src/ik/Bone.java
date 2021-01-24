package ik;

//import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.*;

import core.Camera;
import core.Matrix4f;
import core.Point3Df;
import core.Quaternion;
import core.Transform;
import core.Vector3f;
import object.Mesh;

public class Bone {
	private String name;
	private float length;
	private Quaternion orientationAngle;	// not currently used
	
	private float thickness = 20;
	
	private float angle;		// this may be replaced with the quaternion
	private Vector3f axisOfRotation;
	private float max_angle;
	private float min_angle;
	
	private Transform transform;
	
	private Bone prev;
	private Bone next;
	
	private Mesh mesh;
	
	private int DOF;	// degrees of freedom (defines the number of rows in the Jacobian)  For example, if we are only interested in position of
						// the end effector then DOF = 3.  If we want to include the orientation of the end effector as well then DOF = 6
	
	public Bone(float len, float a, Vector3f axis)
	{
		length = len;
		angle = a;
		axisOfRotation = axis;
		DOF = 3;
		mesh = new Mesh("Bone.obj");
		mesh.setScaleFactorX(length);
		min_angle = 0;
		max_angle = 180;
	}
	
	// Constructor for root bone
	public Bone(Point3Df p, float len, float angle, Vector3f axis)	// angle is taken to be around (0,0,1)
	{
		this(len, angle, axis);
		//orientationAngle = new Quaternion(new Vector3f(0,0,1), (float)(Math.toRadians(angle)));
		transform = new Transform();
		transform.setPos(p);
		transform.rotate(axisOfRotation, (float)Math.toRadians(angle));
	}
	
	public Bone(Bone prev, float len, float angle, Vector3f axis)
	{
		this(len, angle, axis);
		transform = new Transform();
		setParentBone(prev);
		prev.setNextBone(this);
		transform.setPos(new Point3Df(prev.length, 0, 0));
		transform.rotate(axisOfRotation, (float)Math.toRadians(angle));
	}
	
	public void setMesh(Mesh m)
	{
		mesh = m;
		m.setScaleFactorX(length);
	}
	
	public Bone getParent()
	{
		return prev;
	}
	
	public int getDOF() {
		return DOF;
	}
	
	public float getLength()
	{
		return length;
	}
	
	public void setNextBone(Bone b)
	{
		next = b;
	}
	
	public Bone getNext()
	{
		return next;
	}
	
	public Vector3f getAxisOfRotation()
	{
		return axisOfRotation;
	}
	
	public float getJointAngle()
	{
		return angle;
	}
	
	public Point3Df getJointPos()
	{
		if(prev == null) // this is the root bone
			return transform.getPos();
		
		return prev.transform.getTransformation().transform(new Point3Df(prev.length, 0, 0));
	}
	
	public void updateAngle(float angle)
	{
		float newAngle = this.angle + angle;
		if(newAngle <= max_angle && newAngle >= min_angle)
		{
			transform.rotate(axisOfRotation, (float)Math.toRadians(angle));
			this.angle = newAngle;
		}

	}
	
	public void setMaxAngle(float max)
	{
		max_angle = max;
	}
	
	public void setMinAngle(float min)
	{
		min_angle = min;
	}
	
	public void setAngleConstraint(float min, float max)
	{
		max_angle = max;
		min_angle = min;
	}
	
	public float getMaxAngle()
	{
		return max_angle;
	}
	
	public float getMinAngle()
	{
		return min_angle;
	}
	
	public void setParentBone(Bone b)
	{
		prev = b;
		transform.setParent(prev.getTransform());
	}
	
	public Point3Df getEndEffector()
	{
		Bone b = next;
		while(b!=null)
			return b.getEndEffector();
		Point3Df end = transform.getTransformation().transform(new Point3Df(length, 0, 0));
		if(Float.isNaN(end.getX())) {
			System.out.println("ERROR in Bone.getEndEffector():  NaN produced in Bone " + getName());
			System.out.println("End Effector position = " + end);
		}
		return end;

	}
	
	public int getNumJoints()
	{
		Bone b = next;
		int i = 1;
		while(b!=null)
		{
			b = b.next;
			i += 1;
		}
		return i;
			
	}
	
	// returns an array of absolute joint positions from the current bone (including the current bone)
	public Point3Df[] getJointPositions()
	{
		Point3Df[] positions = new Point3Df[getNumJoints()];
		positions[0] = transform.getPos();
		Bone b = next;
		int i = 1;
		while(b!=null)
		{
			//positions[i] = b.getTransform().getPos();
			positions[i++] = b.transform.getTransformation().transform(new Point3Df(b.length, 0, 0));
			b = b.next;
		}
		return positions;
	}
	
	// Returns a set of polygons describing the bone in object coords
	// type = 0: rectangular bones
	// type = 1: prism/ball joints bones
	
	public Point3Df[][] objCoords(int type)
	{
		Point3Df[][] points = new Point3Df[0][0];
		if(type == 0)
		{
			points = new Point3Df[1][4];	// one polygon with 4 vertices
			points[0][0] = new Point3Df(0,thickness/2, 0);
			points[0][1] = points[0][0].add(new Vector3f(length, 0, 0));
			points[0][2] = points[0][1].add(new Vector3f(0, -thickness, 0));
			points[0][3] = points[0][2].add(new Vector3f(-length, 0, 0));
		}else if(type == 1)
		{
			points = new Point3Df[1][3];	// 4 polygons each with 3 vertices
			// near side
			points[0][0] = new Point3Df(0,-thickness/2, 0);
			points[0][1] = new Point3Df(0,thickness/2, 0);
			points[0][2] = new Point3Df(length, 0, 0);
//			// far side
//			points[1][0] = new Point3Df(0,thickness/2, thickness);
//			points[1][1] = points[1][0].add(new Vector3f(length, -thickness/20, 0));
//			points[1][2] = points[1][1].add(new Vector3f(-length, -thickness, 0));
//			// near side
//			points[2][0] = new Point3Df(0,thickness/2, -thickness/2);
//			points[2][1] = points[2][0].add(new Vector3f(0, 0, thickness));
//			points[2][2] = points[2][1].add(new Vector3f(length, -thickness/2, -thickness/2));
//			// far side
//			points[3][0] = new Point3Df(0,-thickness/2, -thickness/2);
//			points[3][1] = points[3][0].add(new Vector3f(0, 0, thickness));
//			points[3][2] = points[3][1].add(new Vector3f(length, thickness/2, -thickness/2));
		}else
			System.out.println("ERROR in Bone.objCoords(): invalid type = " + type + " given.");
		
		return points;
	}
	
	public void drawMesh(Camera cam)
	{
		mesh.draw(this, cam);
		if(next != null)
        	next.drawMesh(cam);
	}
	
	public void draw(Camera cam)
	{
		GL20.glMatrixMode(GL20.GL_MODELVIEW);
		
		Point3Df[][] points = objCoords(0);
		
		for(int poly = 0; poly<points.length; poly++)
		{
			for(int p = 0; p<points[poly].length; p++)
			{
				Matrix4f m = getTransform().getTransformation();
				Matrix4f camViewMatrix = cam.getViewMatrix();
				points[poly][p] = camViewMatrix.mul(m).transform(points[poly][p]);
			}
		}

		// draw quad
        
        for(int poly = 0; poly<points.length; poly++)
		{
        	GL11.glBegin(GL11.GL_POLYGON);
        	for(int p = 0; p<points[poly].length; p++)
			{
        		GL11.glVertex3f(points[poly][p].getX(), points[poly][p].getY(), points[poly][p].getZ());
			}
        	GL11.glEnd();
		}
        
        if(next != null)
        	next.draw(cam);
	}

	public Transform getTransform() {
		return transform;
	}

	public void setTransform(Transform transform) {
		this.transform = transform;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
}
