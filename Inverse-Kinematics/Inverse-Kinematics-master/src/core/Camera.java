package core;

import core.Matrix4f;
import core.Point3Df;
import core.Quaternion;
import core.Vector3f;


// Camera class needs to have the ability to have an orthographic projection

public class Camera 
{
	public static final Vector3f yAxis = new Vector3f(0,1,0);	// world up vector
	
	private Matrix4f projection;
	Transform transform;
	// Perspective parameters
	private float fov;
	private float aspect;
	private float zNear;
	private float zFar;
	// orthographic parameters
	private float left;
	private float right;
	private float bottom;
	private float top;
//	private float near;
//	private float far;

	private int type;		// 0 = orthographic,  1 = perspective
	
	
	public Camera(Matrix4f projection)
	{
		this.projection = projection;
		transform = new Transform();
	}
	
	/*
	 * Creates and returns a camera centered matrix in RHS.  (assumes absolute values for near and far)
	 */
	
	public Camera(float left, float right, float bottom, float top, float near, float far)
	{
		this.left = left;
		this.right = right;
		this.bottom = bottom;
		this.top = top;
		this.zNear = near;
		this.zFar = far;
		aspect = (right-left)/(top-bottom);
		fov = (float)Math.atan((top-bottom)/2/near);
		type = 1;
		transform = new Transform();
		
		//this.projection = new Matrix4f().initOrthographic(left, right, bottom, top, near, far);
		this.projection = new Matrix4f().initPerspective(left, right, bottom, top, near, far);
	}
		
	public Camera(float fov, float aspect, float zNear, float zFar)		// note: fov is in radians
	{
		this.fov = fov;
		this.aspect = aspect;
		this.zNear = zNear;
		this.zFar = zFar;
		top = (float)(zNear*Math.sin(fov/2));
		bottom = -top;
		right = aspect*(top - bottom);
		left = -right;
		type = 1;
		transform = new Transform();
		this.projection = new Matrix4f().initPerspective(fov, aspect, zNear, zFar);

	}
	
	public Transform getTransform()
	{
		return transform;
	}
	
	public void setPos(Point3Df p)
	{
		transform.setPos(p);
	}
	
	/*
	 * Returns MVP
	 */
	public Matrix4f getViewProjection()
	{
		return projection.mul(getModelViewMatrix());
	}
	
	public Matrix4f getModelViewMatrix() {
		Matrix4f cameraRotation = transform.getTransformedRot().conjugate().toRotationMatrix();
		Point3Df cameraPos = transform.getTransformedPos().mul(-1);

		Matrix4f cameraTranslation = new Matrix4f().initTranslation(cameraPos.getX(), cameraPos.getY(), cameraPos.getZ());

		return cameraRotation.mul(cameraTranslation);
	}
	
	/*
	 * Returns M_CAM - from camera coords to world coords
	 */
	public Matrix4f getCameraMatrix() {
		return transform.getTransformation();
	}
	
	/*
	 * Returns M_CAM inverse - from world coords to camera coords
	 */
	public Matrix4f getViewMatrix() {
		Matrix4f cameraRotation = transform.getTransformedRot().conjugate().toRotationMatrix();
		Point3Df cameraPos = transform.getTransformedPos().mul(-1);

		Matrix4f cameraTranslation = new Matrix4f().initTranslation(cameraPos.getX(), cameraPos.getY(), cameraPos.getZ());

		return cameraRotation.mul(cameraTranslation);
	}
	
	public Matrix4f getTranslationMatrix()
	{
		Point3Df cameraPos = transform.getTransformedPos();
		Matrix4f cameraTranslation = new Matrix4f().initTranslation(cameraPos.getX(), cameraPos.getY(), cameraPos.getZ());	// create new inverted translation matrix 
		return cameraTranslation;
	}
	
	public Matrix4f getRotationMatrix()
	{
		Quaternion q = transform.getTransformedRot();
		Matrix4f cameraRotation = q.toRotationMatrix();
		return cameraRotation;
	}
	
	public Matrix4f getInverseTranslationMatrix()
	{
		Point3Df cameraPos = transform.getTransformedPos().mul(-1);	// invert translation
		Matrix4f cameraTranslationInverse = new Matrix4f().initTranslation(cameraPos.getX(), cameraPos.getY(), cameraPos.getZ());	// create new inverted translation matrix 
		return cameraTranslationInverse;
	}
	
	public Matrix4f getInverseRotationMatrix()
	{
		Quaternion q = transform.getTransformedRot();
		Matrix4f cameraRotationInverse = q.toRotationMatrix().transpose();	// invert rotation
		return cameraRotationInverse;
	}
	
	/**
	 * 
	 * @return projection matrix 
	 */
	public Matrix4f getProjection()
	{
		return projection;
	}

	public float getFov() {
		return fov;
	}

	public float getAspect() {
		return aspect;
	}

	public float getzNear() {
		return zNear;
	}

	public float getzFar() {
		return zFar;
	}
	
	public float getLeft()
	{
		return left;
	}
	
	public float getRight()
	{
		return right;
	}
	
	public float getTop()
	{
		return top;
	}
	
	public float getBottom()
	{
		return bottom;
	}

	public int getType() {
		return type;
	}
}