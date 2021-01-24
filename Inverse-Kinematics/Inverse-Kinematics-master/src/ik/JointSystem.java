package ik;

import core.Camera;
import core.Matrix;
import core.Point3Df;
import core.Vector3f;

public class JointSystem {
	private Bone root;
	private Matrix J;					// Jacobian of this system
	private Matrix delta_jointAngles;
	private Matrix delta_jointPositions;
	
	private String METHOD = "DLS";		// METHOD can be either J+ (pseudo-inverse), JT or DLS (damped least square) 
	
	//private float[] deltaTheta;		// these are the changes in the joint angles during each iteration
	private Point3Df endEffector;		// desired end effector  position
	
	private double THRESHOLD = 0.1;	// if the distance between iterations is less than TRHESHOLD, we stop iterating
	
	public JointSystem(Bone root)
	{
		this.root = root;
		delta_jointAngles = new Matrix(new float[getNumJoints()]);
		delta_jointPositions = new Matrix(new float[3][getNumJoints()]);
		
		J = new Matrix(3, getNumJoints());
	}
	
	public int getNumJoints()
	{
		return root.getNumJoints();
	}
	
	public void setDesiredEndEffectorPos(Point3Df p)
	{
		endEffector = p;
	}
	
	public Point3Df getEndEffectorPos()
	{
		return root.getEndEffector();
	}
	
	// returns the maximum reach of this system of bones assuming they are all placed end to end
	public float getMaxReach()
	{
		Bone b = root;
		float reach = 0;
		while(b!=null)
		{
			reach += b.getLength();
			b = b.getNext();		
		}
		return reach;
	}
	
	// return the ith bone of this system (with the first bone having an index = 0)
	public Bone get(int i)
	{
		Bone b = root;
		int j = 0;
		if (i>getNumJoints())
			return null;
		if(i == j)
			return root;
		while(j<i)
		{
			b = b.getNext();
			j++;
		}
		return b;
	}
	
	public void computeJacobian()
	{
		Vector3f column;
		Bone b = root;
		Point3Df endEffector = root.getEndEffector();
		
		for(int j=0; j<getNumJoints(); j++)
		{
			Point3Df jointPos = this.getJointPos(j);
			column = b.getAxisOfRotation().cross(endEffector.sub(this.getJointPos(j)));
			J.setCol(j, column);
			b = b.getNext();
		}
	}
	
	// Run Jacobian inverse routine to move end effector toward target
	public void moveToTarget()
	{
		Point3Df target = endEffector;
		Point3Df currentEndEffector = getEndEffectorPos();
		if(target.sub(currentEndEffector).length() > THRESHOLD)
		{
			if(Float.isNaN(currentEndEffector.getX())) {
				System.out.println("ERROR in JointSystem.moveToTarget():  NaN produced in getEndEffetor() ");
				System.out.println("End Effector position = " + currentEndEffector);
			}
			float distPerUpdate = 0.1f * getMaxReach();
			Vector3f targetVector = target.sub(currentEndEffector).normalized();
			Vector3f deltaR = targetVector.mul(distPerUpdate);
			delta_jointPositions = new Matrix(deltaR);

			// recompute Jacobian
			computeJacobian();
			if(METHOD.equals("J+"))
			{
				// Inverse Kinematics to determine the change in angles of the joints so that the end effector moves toward target
				delta_jointAngles = J.inverse().mul(delta_jointPositions);
				if(Float.isNaN(delta_jointAngles.get(0, 0)))
				{
					System.out.println("Jacobian = \n" + J);
					System.out.println("Jacobian inverse = \n" + J.inverse());
					System.out.println("delta Joint Angles = \n" + delta_jointAngles);
				}
			}else if(METHOD.equals("DLS"))
			{
				float lambda = 1;
				Matrix J_T_J = J.transpose().mul(J);
				Matrix lamb2_I = new Matrix(J.getNumRows(), J.getNumCols()).mul(lambda*lambda);
				Matrix J_DLS = J_T_J.add(lamb2_I).inverse().mul(J.transpose());
				delta_jointAngles = J_DLS.mul(delta_jointPositions);
			}
			updateAngles(delta_jointAngles);

			// Forward Kinematics to determine the new locations of the joint positions
			//delta_jointPositions = J.mul(delta_jointAngles);
		}
	}
	
	public void draw(double delta_T, Camera cam)
	{
		//root.draw(cam);
		root.drawMesh(cam);
	}
	
	public void updateAngles(Matrix deltaAngles)
	{
		System.out.println("Delta angles = " + deltaAngles);
		for(int i=0; i<getNumJoints(); i++)
		{
			get(i).updateAngle(deltaAngles.get(i, 0));
			System.out.println("Bone " + i + " angle = " + get(i).getJointAngle());
		}
	}
	
	// returns the ith joint position
	public Point3Df getJointPos(int i)
	{
		if(i>getNumJoints())
		{
			System.out.println("ERROR in JointSystem.getJointPos(): Joint " + i + " does not exist.");
			return new Point3Df(0,0,0);
		}
			
		Bone b = root;
		for(int j=0; j< i; j++)
		{
			b = b.getNext();
		}
		return b.getJointPos();
	}
	
	// returns the ith joint angle
	public float getJointAngle(int i)
	{
		if(i>getNumJoints())
		{
			System.out.println("ERROR in JointSystem.getJointAngle(): Joint " + i + " does not exist.");
			return 0;
		}

		Bone b = root;
		for(int j=1; j< i; j++)
		{
			b = b.getNext();
		}
		return b.getJointAngle();
	}
}
