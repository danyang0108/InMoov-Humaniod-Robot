package core;

public class Quaternion {
	private float t;
	private float x;
	private float y;
	private float z;
	private Matrix2c m;

	public static void main(String[] args)
	{
		//		Quaternion q1 = new Quaternion(3.06f, 1, 1, 2);
		//		Quaternion q2 = new Quaternion(0.7f, 3, -1, 2);
		//		System.out.println("q1 = \n" + q1);
		//		System.out.println("q2 = \n" + q2);
		//		System.out.println("q2q1 = \n" + q2.mul(q1));

		//		Quaternion q = new Quaternion(1, 0, 0, 1);		// rotate 90 degrees about vector (0,0,1) 
		//		Quaternion p = new Quaternion(0, 1, 1, 0);
		//Quaternion p_rot = q.rotate(p, (float)(Math.toRadians(90))); //rotate p by 90 degrees about q

		//System.out.println("p_rot = " + p_rot.getV());

		// the following three lines rotate the vector v1 by 90 degrees in 3 different ways
		Vector3f v1 = new Vector3f(1,1,0);
		//System.out.println("Rotated v1 = " + v1.rotate(new Quaternion(1, 0, 0, 1)));
		System.out.println("Rotated v1 = " + v1.rotate(new Vector3f(0,0,1), (float)(Math.toRadians(45))));	
		Quaternion q1 = new Quaternion(new Vector3f(0,0,1), (float)(Math.toRadians(-45)));	// calc the normalized quaternion needed to give the desired rotation
		System.out.println("Quaternion to rotate -45 degrees = " + q1);
		Quaternion q2 = new Quaternion(1, 0, 0, 1);		// rotate 90 degrees about vector (0,0,1) 
		Quaternion q1q2 = q1.mul(q2).normalize();		// combined rotations should always be normalized (this one represents +45 degrees)
		System.out.println("Rotated v1 = " + q1q2.rotate(v1));		// use a prebuilt quaternion to rotate v1
		System.out.println("Rotated v1 = " + v1.rotate(q1));		// use a prebuilt quaternion to rotate v1 by -45 degrees

		Quaternion p = new Quaternion(0, 1, 1, 0);
		Quaternion p_ = q1.mul(p).mul(q1.conjugate().mul(1.0f/q1.quadrance()));
		System.out.println("Rotated p = " + p_.getV());
		
		System.out.println("____________________________________________________________");
		
		Matrix4f m = new Matrix4f().setIdentity();
		m.set(0, 0, -1); 
		m.set(1, 1, 0.89442719f); m.set(1, 2, -0.4472136f);
		m.set(2, 0, 0 ); m.set(2, 1, 0.4472136f);  m.set(2, 2, 0.89442719f);
		System.out.println("matrix = \n" + m);
		
		Quaternion q = new Quaternion(m);
		System.out.println("...converted to quaternion " + q);
		System.out.println("LookAt Dir = " + new Vector3f(0,0,1).rotate(q));
		m = q.toRotationMatrix();
		System.out.println("...and back to matrix: \n" + m);

		q = new Quaternion(m);
		System.out.println("...and back to quaternion " + q);
		System.out.println("...and back to matrix: " + q.toRotationMatrix());
		System.out.println("____________________________________________________________");
		
		m = new Matrix4f().setIdentity();
		m.set(0, 0, 0); m.set(0, 1, 1);
		m.set(1, 0, -1); m.set(1, 1, 0);
		System.out.println("Matrix m = \n" + m);
		q = new Quaternion(m);
		System.out.println("...converted to quaternion = " + q);
		m = q.toRotationMatrix();
		System.out.println("...and converted back to matrix: \n" + m);
		System.out.println("...and back to quaternion " + new Quaternion(m));
		System.out.println("____________________________________________________________");
		
		q = new Quaternion(0.7071f/2,0.7071f/2,0,0);
		System.out.println("quaternion = " + q);
		System.out.println("forward = " + q.getForward());
		System.out.println("up = " + q.getUp());
		System.out.println("right = " + q.getRight());
		System.out.println("Quaternion "+ q + " converted to matrix = \n" + q.toRotationMatrix());

	}

	public Quaternion()
	{
		this(1,0,0,0);
	}

	public Quaternion(float t, float x, float y, float z) {
		this.t = t;
		this.x = x;
		this.y = y;
		this.z = z;
		m = new Matrix2c(new Complex(t, x), new Complex(y, z),
				new Complex(-y, z), new Complex(t, -x));
	}

	public Quaternion(Vector3f v)	// create a Quaternion from a 3D point
	{
		this(0, v);

	}
	public Quaternion(float t, Vector3f v)
	{
		this.t = t;
		x = v.getX();
		y = v.getY();
		z = v.getZ();
		m = new Matrix2c(new Complex(t, x), new Complex(y, z),
				new Complex(-y, z), new Complex(t, -x));
	}

	/**
	 * Defines a new quaternion that represents a rotation about a given axis
	 * @param axis a vector representing the axis about which the rotation is defined
	 * @param angle the angle about the axis (in radians)
	 */
	public Quaternion(Vector3f axis, float angle)
	{
		float sinHalfAngle = (float)Math.sin(angle / 2);
		float cosHalfAngle = (float)Math.cos(angle / 2);

		this.x = axis.getX() * sinHalfAngle;
		this.y = axis.getY() * sinHalfAngle;
		this.z = axis.getZ() * sinHalfAngle;
		this.t = cosHalfAngle;
	}

	public Quaternion(Matrix2c m)
	{
		this.m = m;
		t = m.get(0, 0).getA();
		x = m.get(0, 0).getB();
		y = m.get(0, 1).getA();
		z = m.get(0, 1).getB();
	}

	public Quaternion add(Quaternion q)
	{
		return new Quaternion(t+ q.getT(), x+q.getX(), y+q.getY(), z+q.getZ());
	}

	public Quaternion sub(Quaternion q)
	{
		return new Quaternion(t- q.getT(), x-q.getX(), y-q.getY(), z-q.getZ());
	}

	//	public Quaternion initRotation(Vector3f axis, float angle)		// return quaternion to rotate angle radians about axis
	//	{
	//		float sinHalfAngle = (float)Math.sin(angle / 2);
	//		float cosHalfAngle = (float)Math.cos(angle / 2);
	//
	//		float x_ = axis.getX() * sinHalfAngle;
	//		float y_ = axis.getY() * sinHalfAngle;
	//		float z_ = axis.getZ() * sinHalfAngle;
	//		float t_ = cosHalfAngle;
	//		
	//		return new Quaternion(t_, new Vector3f(x_, y_, z_));
	//	}

	//	public Quaternion initRotation(Vector3f axis, float angle)		// return quaternion to rotate angle radians about axis
	//	{
	//		//Vector3f v = axis.normalize();
	//		float sinAngle = (float)Math.sin(angle);
	//		float cosAngle = (float)Math.cos(angle);
	//		double t;
	////		if(angle < Math.PI)
	//			t = Math.sin(angle)/(1 + Math.cos(angle));
	////		else
	////			t = Math.sin(angle)/(1 - Math.cos(angle));
	//		return new Quaternion((float)t, axis.normalize());
	//	}

	private float quadratic(float a, float b, float c, boolean root)	// returns the more positive quadratic root if root == true
	{
		if(root)
			return (float)(-b + Math.sqrt(b*b - 4 * a * c))/ (2 * a);  	// using quadratic formula
		else
			return (float)(-b - Math.sqrt(b*b - 4 * a * c))/ (2 * a);
	}

	public String toString()
	{
		String s = "[ ";
		s = s + t + " " + x + " " + y + " " + z + "]";
		return s;
	}

	public float getT() {
		return t;
	}

	public Vector3f getV()
	{
		return new Vector3f(x, y, z);
	}

	public void setT(float t) {
		this.t = t;
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getZ() {
		return z;
	}

	public void setZ(float z) {
		this.z = z;
	}

	public Matrix2c getM() {
		return m;
	}

	public void setM(Matrix2c m) {
		this.m = m;
	}

	//	public Quaternion mul(Quaternion q)
	//	{
	//		Matrix2c n = m.mul(q.getM());
	//		return new Quaternion(n);
	//	}

	/*
	 * returns the product of two quaternions
	 */
	public Quaternion mul(Quaternion q)
	{
		Vector3f v = new Vector3f(x, y, z);
		return new Quaternion(t*q.getT() - v.dot(q.getV()), 
				q.getV().mul(t).add(v.mul(q.getT())).add(v.cross(q.getV())));
	}

//		public Quaternion mul(Quaternion r)
//		{
//			float w_ = t * r.getT() - x * r.getX() - y * r.getY() - z * r.getZ();
//			float x_ = x * r.getT() + t * r.getX() + y * r.getZ() - z * r.getY();
//			float y_ = y * r.getT() + t * r.getY() + z * r.getX() - x * r.getZ();
//			float z_ = z * r.getT() + t * r.getZ() + x * r.getY() - y * r.getX();
//			
//			return new Quaternion(w_, x_, y_, z_);
//		}

	public Quaternion mul(Vector3f v)
	{
		return new Quaternion( - getV().dot(v), 
				v.mul(t).add(getV().cross(v)));
	}
	
	public Quaternion mul(float s)
	{
		return new Quaternion(s*t, s*x, s*y, s*z);
	}

	public Quaternion normalize()
	{
		float len = length();
		t = t/len;
		x = x/len;
		y = y/len;
		z = z/len;
		return this;
	}

	public Quaternion normalized()
	{
		float len = length();
		return new Quaternion(t/len, x/len, y/len, z/len);
	}

	public Quaternion conjugate()
	{
		return new Quaternion(t, -x, -y, -z);
	}

	public float dot(Quaternion q)
	{
		return t*q.getT() + x*q.getX() + y*q.getY() + z*q.getZ();
	}

	public float quadrance() 
	{
		return t*t + x*x + y*y + z*z;
	}

	public Quaternion inverse()
	{
		return conjugate().mul(1.0f/quadrance());
	}

	//	public Quaternion rotate(Quaternion q, float angle)	// rotation of quaternion q by angle radians about this axis (x, y, z)
	//	{
	////		System.out.println("Rotating quaterniom q " + q + " " + Math.toDegrees(angle) + " degrees about axis " + this.getV());
	////		System.out.println("h = " + getHalfTurn(angle));
	//		t = length()/getHalfTurn(angle);
	////		System.out.println("t = " + t);
	////		System.out.println("rotation of q under quaternion " + this);
	//		Quaternion zq = this.mul(q);
	////		System.out.println("zq = " + zq);
	//		Quaternion zqz = zq.mul(this.conjugate());
	////		System.out.println("zqzbar = " + zqz);
	//		//return zqz.mul(1.0f/this.quadrance());
	//		return zqz;
	//	}

	
	public Vector3f rotate(Vector3f v)		// rotate vector v by this rotation and return the rotated vector
	{
		Quaternion q_ = new Quaternion(0, v).normalize();	// convert vector to pure quaternion
		Quaternion result = this.mul(q_).mul(inverse());
		return result.getV();
	}

	public float getHalfTurn(float angle)	// return the half-turn equivalent of angle in radians
	{
		return (float)((1 - Math.sqrt(1 - Math.sin(angle)*Math.sin(angle)))/Math.sin(angle));
	}

	public float length()
	{
		return (float)Math.sqrt(t*t + x*x + y*y + z*z);
	}

	// This conversion can be verified at http://www.euclideanspace.com/maths/geometry/rotations/conversions/quaternionToMatrix/
	/*
	 * This converts the current quaternion to a LHS rotation matrix (in local coordinates).  To convert to a RHS, take the inverse,
	 * which is its transpose (since we build an orthogonal matrix)
	 */
	public Matrix4f toRotationMatrix()
	{
		Vector3f right = new Vector3f(1.0f - 2.0f * (y * y + z * z), 2.0f * (x * y - t * z), 2.0f * (x * z + t * y));
		Vector3f up = new Vector3f(2.0f * (x * y + t * z), 1.0f - 2.0f * (x * x + z * z), 2.0f * (y * z - t * x));
		Vector3f forward =  new Vector3f(2.0f * (x * z - t * y), 2.0f * (y * z + t * x), 1.0f - 2.0f * (x * x + y * y));
		
		return new Matrix4f().initRotation(forward, up, right).transpose(); // RHS
	}
	
//	public Matrix4f toRotationMatrix()
//	{
//		Vector3f right = new Vector3f(1.0f - 2.0f * (y * y + z * z), 2.0f * (x * y + t * z), 2.0f * (x * z - t * y));
//		Vector3f up = new Vector3f(2.0f * (x * y - t * z), 1.0f - 2.0f * (x * x + z * z), 2.0f * (y * z + t * x));
//		Vector3f forward =  new Vector3f(2.0f * (x * z + t * y), 2.0f * (y * z - t * x), 1.0f - 2.0f * (x * x + y * y));
//		return new Matrix4f().initRotation(forward, up, right);		// should this be transposed?  
//	}
	
//	public Matrix4f toRotationMatrix()
//	{
//		//Vector3f right = new Vector3f(2.0f * (t*t + x*x)-1, 2.0f * (x * y + t * z), 2.0f * (x * z - t * y));
//		Vector3f up = new Vector3f(2.0f * (x * y - t * z), 2.0f * (t * t + y * y)-1, 2.0f * (y * z + t * x));
//		Vector3f forward =  new Vector3f(2.0f * (x * z + t * y), 2.0f * (y * z - t * x), 2.0f * (t * t + z * z)-1);
//		Vector3f right = forward.cross(up);		// changed this to a RHS
//		Matrix4f m = new Matrix4f().initRotation(forward, up, right);		// should this be transposed?  
//		return m;
//	}

	public Vector3f getForward()
	{
		return new Vector3f(0,0,1).rotate(this);	// we are moving forward in the negative z-axis when in CCS
		//return new Vector3f(2.0f * (x*z - t*y), 2.0f * (y*t + t*x), 1.0f - 2.0f * (x*x + y*y));
	}

	public Vector3f getBack()
	{
		return new Vector3f(0,0,-1).rotate(this);
		//return new Vector3f(-2.0f * (x*z - t*y), -2.0f * (y*t + t*x), -(1.0f - 2.0f * (x*x + y*y)));
	}

	public Vector3f getUp()
	{
		return new Vector3f(0,1,0).rotate(this);
		//return new Vector3f(2.0f * (x*y - t*z), 1.0f - 2.0f * (x*x + z*z), 2.0f * (y*z + t*x));
	}

	public Vector3f getDown()
	{
		return new Vector3f(0,-1,0).rotate(this);
	}

	public Vector3f getRight()
	{
		return new Vector3f(1,0,0).rotate(this);
	}

	public Vector3f getLeft()
	{
		return new Vector3f(-1,0,0).rotate(this);
	}

	public Quaternion set(float t, float x, float y, float z) 
	{ 
		this.t = t; this.x = x; this.y = y; this.z = z; return this;
	}

	public Quaternion set(Quaternion q)
	{
		set(q.getT(), q.getX(), q.getY(), q.getZ()); return this;
	}

	public boolean equals(Quaternion q)
	{
		return x == q.getX() && y == q.getY() && z == q.getZ() && t == q.getT();
	}

	public Quaternion NLerp(Quaternion dest, float lerpFactor, boolean shortest)
	{
		Quaternion correctedDest = dest;

		if(shortest && this.dot(dest) < 0)
			correctedDest = new Quaternion(-dest.getT(), -dest.getX(), -dest.getY(), -dest.getZ());

		return correctedDest.sub(this).mul(lerpFactor).add(this).normalized();
	}

	public Quaternion SLerp(Quaternion dest, float lerpFactor, boolean shortest)
	{
		final float EPSILON = 1e3f;

		float cos = this.dot(dest);
		Quaternion correctedDest = dest;

		if(shortest && cos < 0)
		{
			cos = -cos;
			correctedDest = new Quaternion(-dest.getT(), -dest.getX(), -dest.getY(), -dest.getZ());
		}

		if(Math.abs(cos) >= 1 - EPSILON)
			return NLerp(correctedDest, lerpFactor, false);

		float sin = (float)Math.sqrt(1.0f - cos * cos);
		float angle = (float)Math.atan2(sin, cos);
		float invSin =  1.0f/sin;

		float srcFactor = (float)Math.sin((1.0f - lerpFactor) * angle) * invSin;
		float destFactor = (float)Math.sin((lerpFactor) * angle) * invSin;

		return this.mul(srcFactor).add(correctedDest.mul(destFactor));
	}
	
	public Quaternion(Matrix4f rot)
	{
		float trace = rot.get(0, 0) + rot.get(1, 1) + rot.get(2, 2) + 1;

		float s = 0.5f*(float)Math.sqrt(trace);
		if(s>0.001)
		{
			t = s;
			x = 0.25f/s*(rot.get(2, 1)-rot.get(1, 2));
			y = 0.25f/s*(rot.get(0, 2)-rot.get(2, 0));
			z = 0.25f/s*(rot.get(1, 0)-rot.get(0, 1));
		}
		else
		{	
			System.out.println("Quaternion constructor:  Holy shit!");
			x = 0.5f * (float)Math.sqrt(1.0f + rot.get(0, 0) - rot.get(1, 1) - rot.get(2, 2));
			y = 0.5f * (float)Math.sqrt(1.0f - rot.get(0, 0) + rot.get(1, 1) - rot.get(2, 2));
			z = 0.5f * (float)Math.sqrt(1.0f - rot.get(0, 0) - rot.get(1, 1) + rot.get(2, 2));
			
			if(x > y && x > z)
			{
				s = 0.25f/x;
				y = s*(rot.get(1, 0)+rot.get(0, 1));
				z = s*(rot.get(0, 2)+rot.get(2, 0));
				t = s*(rot.get(2, 1)-rot.get(1, 2));
			}else if(y > x && y > z)
			{
				s = 0.25f/y;
				x = s*(rot.get(0, 1)+rot.get(1, 0));
				z = s*(rot.get(1, 2)+rot.get(2, 1));
				t = s*(rot.get(0, 2)-rot.get(2, 0));
			}else if(z > y && z > x)
			{
				s = 0.25f/z;
				x = s*(rot.get(0, 2)+rot.get(2, 0));
				y = s*(rot.get(1, 2)+rot.get(2, 1));
				t = s*(rot.get(1, 0)-rot.get(0, 1));
			}
		}
		
		normalize();
	}

	//From Ken Shoemake's "Quaternion Calculus and Fast Animation" article
//	public Quaternion(Matrix4f rot)
//	{
//		//rot = rot.transpose();
//		float trace = rot.get(0, 0) + rot.get(1, 1) + rot.get(2, 2);
//
//		if(trace > 0)
//		{
//			float s = 0.5f / (float)Math.sqrt(trace+1);
//			t = 0.25f / s;
//			// assumes that a matrix is in a right-hand coordinate system and that matrix rotation is represented in a column major format (for example, OpenGL compatible)
//			x = (rot.get(1, 2) - rot.get(2, 1)) * s;
//			y = (rot.get(2, 0) - rot.get(0, 2)) * s;
//			z = (rot.get(0, 1) - rot.get(1, 0)) * s;
//			
//		}
//		else
//		{
//			if(rot.get(0, 0) > rot.get(1, 1) && rot.get(0, 0) > rot.get(2, 2))
//			{
//				float s = 2.0f * (float)Math.sqrt(1.0f + rot.get(0, 0) - rot.get(1, 1) - rot.get(2, 2));
//				t = (rot.get(1, 2) + rot.get(2, 1)) / s;
//				x = 0.5f / s;
//				y = (rot.get(1, 0) + rot.get(0, 1)) / s;
//				z = (rot.get(2, 0) + rot.get(0, 2)) / s;
//			}
//			else if(rot.get(1, 1) > rot.get(2, 2) && rot.get(1, 1) > rot.get(0, 0))
//			{
//				float s = 2.0f * (float)Math.sqrt(1.0f + rot.get(1, 1) - rot.get(0, 0) - rot.get(2, 2));
//				t = (rot.get(2, 0) + rot.get(0, 2)) / s;
//				x = (rot.get(1, 0) + rot.get(0, 1)) / s;
//				y = 0.5f / s;
//				z = (rot.get(2, 1) + rot.get(1, 2)) / s;
//			}
//			else
//			{
//				float s = 2.0f * (float)Math.sqrt(1.0f + rot.get(2, 2) - rot.get(0, 0) - rot.get(1, 1));
//				t = (rot.get(0, 1) + rot.get(1, 0) ) / s;
//				x = (rot.get(2, 0) + rot.get(0, 2) ) / s;
//				y = (rot.get(1, 2) + rot.get(2, 1) ) / s;
//				z = 0.5f / s;
//			}
//		}
//
//		float length = (float)Math.sqrt(x * x + y * y + z * z + t * t);
//		x /= length;
//		y /= length;
//		z /= length;
//		t /= length;
//	}

}

