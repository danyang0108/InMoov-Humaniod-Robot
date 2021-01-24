package core;

public class Transform
{
	private Transform parent;
	private Matrix4f parentMatrix;
	
	private Point3Df pos;
	private Quaternion rot;		// specifies the orientation of this transform, using a quaternion
	private Vector3f scale;
	
	private Point3Df oldPos;
	private Quaternion oldRot;		// using quaternions for all rotations
	private Vector3f oldScale;
	
	public Transform()
	{
		pos = new Point3Df(0,0,0);
		rot = new Quaternion(1,0,0,0);	// null rotation
		scale = new Vector3f(1, 1, 1);
		
		parentMatrix = new Matrix4f().setIdentity();
		update();
	}
	
	public void update()
	{
		if(oldPos != null)
		{
			oldPos.set(pos);
			oldRot.set(rot);
			oldScale.set(scale);
		}else
		{
			oldPos = new Point3Df(pos);
			oldRot = rot;
			//oldRot = new Quaternion(0,0,0,0).set(rot).mul(0.5f);
			oldScale = new Vector3f(0,0,0).set(scale).add(1.0f);
		}
	}
	
	/**
	 * Calculates a quaternion that represents a rotation about a given axis and stores the new quaternion in this class' rot variable
	 * @param axis
	 * @param angle
	 */
	public void rotate(Vector3f axis, float angle)
	{
		rot = new Quaternion(axis, angle).mul(rot).normalized();
	}
	
	/**
	 * Sets this object lookAt point
	 * @param point	- point at which this object is to look at (in world coords)
	 * @param up	- up vector
	 */
	public void lookAt(Point3Df point, Vector3f up)
	{
		rot = getLookAtDirection(point, up);
	}
	
	/**
	 * Returns a quaternion representing the rotation that aligns the current coord system with lookAt.  
	 * The vector will point from the lookAt point to the eye or camera.
	 * @param point
	 * @param up
	 * @return
	 */
	public Quaternion getLookAtDirection(Point3Df point, Vector3f up)
	{
		return new Quaternion(new Matrix4f().initRotation(pos.sub(point).normalized(), up));
		
	}
	
	public boolean hasChanged()
	{
		if(parent != null && parent.hasChanged())
			return true;	
		
		if(!pos.equals(oldPos))
			return true;
		
		if(!rot.equals(oldRot))
			return true;
			
		if(!scale.equals(oldScale))
			return true;
		
		return false;
	}
	
	public Quaternion getRot() {
		return rot;
	}
	
	public void setRot(Quaternion rot) {
		this.rot = rot;
	}

	/**
	 * 
	 * @return returns a (model/camera to ) world matrix
	 */
	public Matrix4f getTransformation()
	{
		Matrix4f translationMatrix = new Matrix4f().initTranslation(pos.getX(), pos.getY(), pos.getZ());
		Matrix4f rotationMatrix = rot.toRotationMatrix();
		Matrix4f scaleMatrix = new Matrix4f().initScale(scale.getX(), scale.getY(), scale.getZ());	
		
		return getParentMatrix().mul(translationMatrix.mul(rotationMatrix.mul(scaleMatrix)));
	}
	
	private Matrix4f getParentMatrix()
	{
		if(parent != null){
			if(parent.hasChanged())
				parentMatrix = parent.getTransformation();
		}else
			parentMatrix = new Matrix4f().setIdentity();
				
		
		return parentMatrix;
	}
	
	public void setParent(Transform parent)
	{
		this.parent = parent;
	}
	
	public Matrix4f getProjectedTransformation()
	{
		return null;
	}
	
	public Point3Df getTransformedPos()
	{
		return getParentMatrix().transform(pos);
	}
	
	/*
	 * getTransformedRot() - returns the quaternion representation of the rotation portion of this transform
	 */
	public Quaternion getTransformedRot()
	{
		Quaternion parentRotation = new Quaternion(1,0,0,0);	// quaternion that represents no rotation
		
		if(parent != null) {
			parentRotation = parent.getTransformedRot();
		}
		//else
		//	System.out.println("Transform.getTransformedRot(): parent = NULL");
		return parentRotation.mul(rot);
	}

	public Point3Df getPos() {
		return pos;
	}

	public void setPos(Point3Df pos) {
		this.pos = pos;
	}


	public Vector3f getScale() {
		return scale;
	}
	
	public void setScale(float x, float y, float z) {
		this.scale = new Vector3f(x, y, z);
	}
	
	public void setScale(Vector3f scale) {
		this.scale = scale;
	}
	
}