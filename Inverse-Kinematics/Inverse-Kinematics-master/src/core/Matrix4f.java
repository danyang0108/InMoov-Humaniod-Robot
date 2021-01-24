package core;


public class Matrix4f {
	private float[][] m;

	public static void main(String[] args)
	{
		
		float m[][] = new float[4][4];
		m[0][0] = 9;	m[0][1] = 12;	m[0][2] = 11;	m[0][3] = 5;
		m[1][0] = 10;	m[1][1] = 2;	m[1][2] = 13;	m[1][3] = 6;
		m[2][0] = 14;	m[2][1] = 15;	m[2][2] = 5;	m[2][3] = 7;
		m[3][0] = 3;	m[3][1] = -3;	m[3][2] = -1;	m[3][3] = 1;
		
		Matrix4f M = new Matrix4f(m);
		
		Matrix4f M_inv = new Matrix4f(M.getCopyOfM()).inverse();
		System.out.println("INVERSE = " + M_inv);
		System.out.println("M * M_inv = " + M.mul(M_inv));
	}
	
	public Matrix4f() {
		m = new float[4][4];
	}
	
	public Matrix4f(float [][] r)
	{
		m = new float[4][4];
		for(int i=0; i<4;i++)
		{
			for(int j=0; j<4;j++)
			{
				m[i][j] = r[i][j];
			}
		}
	}
	
	public Matrix4f(float[] openGLMatrix)
	{
		m = new float[4][4];
		m[0][0] = openGLMatrix[0];	m[0][1] = openGLMatrix[4];	m[0][2] = openGLMatrix[8];	m[0][3] = openGLMatrix[12];
		m[1][0] = openGLMatrix[1];	m[1][1] = openGLMatrix[5];	m[1][2] = openGLMatrix[9];	m[1][3] = openGLMatrix[13];
		m[2][0] = openGLMatrix[2];	m[2][1] = openGLMatrix[6];	m[2][2] = openGLMatrix[10];	m[2][3] = openGLMatrix[14];
		m[3][0] = openGLMatrix[3];	m[3][1] = openGLMatrix[7];	m[3][2] = openGLMatrix[11];	m[3][3] = openGLMatrix[15];
	}
	
	// get openGL version of this matrix
	public float[] getOpenGlMatrix()
	{
		float[] M = new float[16];
		M[0] = m[0][0];				M[4] = m[0][1];				M[8] = m[0][2];				M[12] = m[0][3];
		M[1] = m[1][0];				M[5] = m[1][1];				M[9] = m[1][2];				M[13] = m[1][3];
		M[2] = m[2][0];				M[6] = m[2][1];				M[10] = m[2][2];			M[14] = m[2][3];
		M[3] = m[3][0];				M[7] = m[3][1];				M[11] = m[3][2];			M[15] = m[3][3];
		return M;
	}
	
	
	public Matrix4f inverse()
	{
		float pivot;
		float inverse[][] = new Matrix4f().setIdentity().getM();
		for(int row=0; row<m.length; row++)
		{
			pivot = m[row][row];
			int pivotRow = row;
			int pivotCol = row;
			float largestPivot = pivot;
			if(pivot == 0)	// swap with another row with largest non-zero pivot
			{
				// first find row to swap
				for(int r=0; r<m.length; r++)
				{
					if(m[r][row] > pivot)
					{
						pivotRow = r;
						pivotCol = row;
						largestPivot = m[r][row];
					}
				}
				// now swap pivotRow with row m[col]
				for(int c=0; c<m[row].length; c++)
				{
					float temp = m[row][c];
					m[row][c] = m[pivotRow][c];
					m[pivotRow][c] = temp;
					// Now do the same for the augmented identity matrix
					temp = inverse[row][c];
					inverse[row][c] = inverse[pivotRow][c];
					inverse[pivotRow][c] = temp;
				}
			}
			//System.out.print(this);
			//System.out.println(new Matrix4f(inverse));
			// Now that we have the pivot, divide the pivot row by pivot value
			divideRow(pivotRow, largestPivot, inverse);
			
			// zero all other values above or below the pivot
			for(int r = 0; r<m.length; r++)
			{
				if(pivotRow != r)	
				{	
					float factor = m[r][row];
					for(int c = 0; c<m[r].length; c++)
					{
						
						m[r][c] = m[r][c] - m[row][c]*factor;
						inverse[r][c] = inverse[r][c] - inverse[row][c]*factor;
					}
				}
			}
		}
		return new Matrix4f(inverse);
	}
	
	// divide a given row by a value
	public void divideRow(int row, float val)
	{
		for(int i=0; i<m[row].length; i++)
			m[row][i] /= val;
	}
	
	// divide a given row by a value
	public void divideRow(int row, float val, float[][] inverse)
	{
		for(int i=0; i<m[row].length; i++)
		{
			m[row][i] /= val;
			inverse[row][i] /= val;
		}
	}
	
	public Matrix4f setIdentity()
	{
		m[0][0] = 1;	m[0][1] = 0;	m[0][2] = 0;	m[0][3] = 0;
		m[1][0] = 0;	m[1][1] = 1;	m[1][2] = 0;	m[1][3] = 0;
		m[2][0] = 0;	m[2][1] = 0;	m[2][2] = 1;	m[2][3] = 0;
		m[3][0] = 0;	m[3][1] = 0;	m[3][2] = 0;	m[3][3] = 1;
		
		return this;
	}
	
	public Matrix4f initTranslation(float x, float y, float z)
	{
		m[0][0] = 1;	m[0][1] = 0;	m[0][2] = 0;	m[0][3] = x;
		m[1][0] = 0;	m[1][1] = 1;	m[1][2] = 0;	m[1][3] = y;
		m[2][0] = 0;	m[2][1] = 0;	m[2][2] = 1;	m[2][3] = z;
		m[3][0] = 0;	m[3][1] = 0;	m[3][2] = 0;	m[3][3] = 1;
		
		return this;
	}
	
	public Matrix4f initRotation(float x, float y, float z)
	{
		Matrix4f rx = new Matrix4f();
		Matrix4f ry = new Matrix4f();
		Matrix4f rz = new Matrix4f();
		
		x = (float)Math.toRadians(x);
		y = (float)Math.toRadians(y);
		z = (float)Math.toRadians(z);
		
		rz.m[0][0] = (float)Math.cos(z);	rz.m[0][1] = -(float)Math.sin(z);	rz.m[0][2] = 0;						rz.m[0][3] = 0;
		rz.m[1][0] = (float)Math.sin(z);	rz.m[1][1] = (float)Math.cos(z);	rz.m[1][2] = 0;						rz.m[1][3] = 0;
		rz.m[2][0] = 0;						rz.m[2][1] = 0;						rz.m[2][2] = 1;						rz.m[2][3] = 0;
		rz.m[3][0] = 0;						rz.m[3][1] = 0;						rz.m[3][2] = 0;						rz.m[3][3] = 1;
		
		rx.m[0][0] = 1;						rx.m[0][1] = 0;						rx.m[0][2] = 0;						rx.m[0][3] = 0;
		rx.m[1][0] = 0;						rx.m[1][1] = (float)Math.cos(x);	rx.m[1][2] = -(float)Math.sin(x);	rx.m[1][3] = 0;
		rx.m[2][0] = 0;						rx.m[2][1] = (float)Math.sin(x);	rx.m[2][2] = (float)Math.cos(x);	rx.m[2][3] = 0;
		rx.m[3][0] = 0;						rx.m[3][1] = 0;						rx.m[3][2] = 0;						rx.m[3][3] = 1;
		
		ry.m[0][0] = (float)Math.cos(y);	ry.m[0][1] = 0;						ry.m[0][2] = -(float)Math.sin(y);	ry.m[0][3] = 0;
		ry.m[1][0] = 0;						ry.m[1][1] = 1;						ry.m[1][2] = 0;						ry.m[1][3] = 0;
		ry.m[2][0] = (float)Math.sin(y);	ry.m[2][1] = 0;						ry.m[2][2] = (float)Math.cos(y);	ry.m[2][3] = 0;
		ry.m[3][0] = 0;						ry.m[3][1] = 0;						ry.m[3][2] = 0;						ry.m[3][3] = 1;
		
		m = rz.mul(ry.mul(rx)).getM();
		return this;
	}
	
	public Matrix4f initScale(float x, float y, float z)
	{
		m[0][0] = x;	m[0][1] = 0;	m[0][2] = 0;	m[0][3] = 0;
		m[1][0] = 0;	m[1][1] = y;	m[1][2] = 0;	m[1][3] = 0;
		m[2][0] = 0;	m[2][1] = 0;	m[2][2] = y;	m[2][3] = 0;
		m[3][0] = 0;	m[3][1] = 0;	m[3][2] = 0;	m[3][3] = 1;
		
		return this;
	}
	
	/**
	 * Create a perspective transformation matrix looking down the positive z-axis, which amounts to a left-handed coord system.  Multiplication by this 
	 * matrix results in an NDC system from -1 to +1 in all dimensions.
	 * Note: Up until the perspective projection transformation, OpenGL works with a Right-Handed System.
	 * @param fov vertical angle in radians 
	 * @param aspectRatio
	 * @param zNear				// zNear and zFar are positive values but the construction of this matrix will treat them as negative values internally
	 * @param zFar 
	 * @return
	 */
	
//	public Matrix4f initPerspective(float fov, float aspectRatio, float zNear, float zFar)	// perspective projection matrix, fov is in radians
//	{
//		float tanHalfFOV = (float)Math.tan(fov/2);
//		float zRange = zNear - zFar;
//		
//		m[0][0] = 1.0f/(tanHalfFOV*aspectRatio);	m[0][1] = 0;						m[0][2] = 0;						m[0][3] = 0;
//		m[1][0] = 0;								m[1][1] = 1.0f/(tanHalfFOV);		m[1][2] = 0;						m[1][3] = 0;
//		m[2][0] = 0;								m[2][1] = 0;						m[2][2] = -(zNear + zFar)/zRange;	m[2][3] = 2*zFar*zNear/zRange;
//		m[3][0] = 0;								m[3][1] = 0;						m[3][2] = 1;						m[3][3] = 0;
//		return this;
//	}
	
	/*
	 * creates a perspective matrix that transforms points in view (or camera) space using a RHS to CCS in a LHS
	 * @param fov - field of view in radians
	 * @param aspectRatio - ratio of width to height of image plane
	 * @param zNear - z locaton of image plane (always positive)
	 * @param zFar - z location of far clipping plane (always positive)
	 */
	public Matrix4f initPerspective(float fov, float aspectRatio, float zNear, float zFar)	// perspective projection matrix, fov is in radians
	{
		float tanHalfFOV = (float)Math.tan(fov/2);
		float top = zNear*tanHalfFOV;
		float bottom = -top;
		float right = top*aspectRatio;
		float left = -right;
		
		return initPerspective(left, right, bottom, top, zNear, zFar);
	}
	
	/*
	 * returns a matrix that transforms points in view (or camera) space using a RHS to CCS in a LHS
	 */
	public Matrix4f initPerspective(float left, float right, float bottom, float top, float near, float far)	// perspective projection matrix
	{
		float width = right-left;
		float height = top - bottom;
		float zRange = far - near;
		
		// note: m[3][2] originally was set to +1
		
		m[0][0] = 2.0f*near/width;			m[0][1] = 0;						m[0][2] = (right+left)/width;		m[0][3] = 0;
		m[1][0] = 0;						m[1][1] = 2.0f*near/height;			m[1][2] = (top+bottom)/height;		m[1][3] = 0;
		m[2][0] = 0;						m[2][1] = 0;						m[2][2] = (-near - far)/zRange;		m[2][3] = -2*far*near/zRange;
		m[3][0] = 0;						m[3][1] = 0;						m[3][2] = -1;						m[3][3] = 0;
		return this;
	}
	
	/**
	 * This method transforms vectors from View Space in RHS into Ortho Projected Space in the LHS..
	 * @param left
	 * @param right
	 * @param bottom
	 * @param top
	 * @param near
	 * @param far
	 * @return
	 */
	public Matrix4f initOrthographic(float left, float right, float bottom, float top, float near, float far)
	{
		//near = -near;		// <---- this was changed!!
		//far = -far;

				
		float width = right - left;
		float height = top - bottom;
		float depth = far - near;
		
		m[0][0] = 2/width;	m[0][1] = 0;		m[0][2] = 0;		m[0][3] = -(right + left)/width;
		m[1][0] = 0;		m[1][1] = 2/height;	m[1][2] = 0;		m[1][3] = -(top + bottom)/height;
		m[2][0] = 0;		m[2][1] = 0;		m[2][2] = -2/depth;	m[2][3] = -(far + near)/depth;
		m[3][0] = 0;		m[3][1] = 0;		m[3][2] = 0;		m[3][3] = 1;
		
		return this;
	}
	
	/*
	 * initRotation - creates a rotation matrix from 2 inputs: forward and up vectors...the third vector that is needed is computed as forward X up.
	 * note that this returns the world to camera matrix (ie. local camera coords)
	 */
//	public Matrix4f initRotation(Vector3f forward, Vector3f up) 
//	{
//		Vector3f f = forward.normalized();
//		
//		Vector3f u = up.normalize();
//		u = up.sub(f.mul(up.dot(f))).normalize();
//		Vector3f r = f.cross(u).normalize();	// (RHS?) 
//		
//		return initRotation(f,u,r);
//	}
	
	
	/*
	 * initRotation - creates a rotation matrix from 2 inputs: forward and up vectors...the third vector that is needed is computed as forward X up.
	 * note that this returns the world to camera matrix using a RHS (ie. local camera coords)
	 */
	public Matrix4f initRotation(Vector3f forward, Vector3f up) 
	{
		Vector3f f = forward.normalized();
		
		
		Vector3f r = up.cross(f).normalize();	// for RHS (looking down -x-axis)
		//Vector3f r = f.cross(up).normalize();	// for LHS (looking down x-axis)
		r.normalize();
		
		Vector3f u = f.cross(r);
		
		return initRotation(f,u,r);
	}
	
	/*
	 * initRotation - creates a rotation matrix from 3 inputs: forward, up and right orthogonal normalized vectors
	 * note that this returns the world to camera matrix (ie. in local coords)
	 * The result is in local camera coords using a RHS.  (The inverse (transpose) should be taken if world camera
	 * coords are needed.
	 */
	public Matrix4f initRotation(Vector3f forward, Vector3f up, Vector3f right)
	{
		Vector3f f = forward;
		Vector3f r = right;
		Vector3f u = up;

		m[0][0] = r.getX();	m[0][1] = u.getX();	m[0][2] = f.getX();	m[0][3] = 0;
		m[1][0] = r.getY();	m[1][1] = u.getY();	m[1][2] = f.getY();	m[1][3] = 0;
		m[2][0] = r.getZ();	m[2][1] = u.getZ();	m[2][2] = f.getZ();	m[2][3] = 0;
		m[3][0] = 0;		m[3][1] = 0;		m[3][2] = 0;		m[3][3] = 1;
		
//		m[0][0] = r.getX();	m[0][1] = r.getY();	m[0][2] = r.getZ();	m[0][3] = 0;
//		m[1][0] = u.getX();	m[1][1] = u.getY();	m[1][2] = u.getZ();	m[1][3] = 0;
//		m[2][0] = f.getX();	m[2][1] = f.getY();	m[2][2] = f.getZ();	m[2][3] = 0;
//		m[3][0] = 0;		m[3][1] = 0;		m[3][2] = 0;		m[3][3] = 1;

		return this;
	}
	
	/**
	 * Initializes a NDCS matrix - mainly useful for testing
	 * @param left
	 * @param right
	 * @param top
	 * @param bottom
	 * @param near
	 * @param far
	 * @return
	 */
	public Matrix4f initNormalizedDeviceCoords(float left, float right, float top, float bottom, float near, float far )
	{
		float width = right-left;
		float height = top-bottom;
		
		m[0][0] = width/2;	m[0][1] = 0;		m[0][2] =0;				m[0][3] = (left+right)/2;
		m[1][0] = 0;		m[1][1] = height/2;	m[1][2] = 0;			m[1][3] = (top+bottom)/2;
		m[2][0] = 0;		m[2][1] = 0;		m[2][2] = (far-near)/2;	m[2][3] = (far+near)/2;
		m[3][0] = 0;		m[3][1] = 0;		m[3][2] = 0;			m[3][3] = 1;
		
		return this;
	}
	
	// transform a given point pos by this transformation matrix (ie. returns T*pos)
	public Point3Df transform(Point3Df pos)
	{
		return new Point3Df(m[0][0] * pos.getX() + m[0][1] * pos.getY() + m[0][2] * pos.getZ() + m[0][3],
							m[1][0] * pos.getX() + m[1][1] * pos.getY() + m[1][2] * pos.getZ() + m[1][3],
							m[2][0] * pos.getX() + m[2][1] * pos.getY() + m[2][2] * pos.getZ() + m[2][3]);
	}
	
	// transform a given point pos by this transformation matrix (ie. returns T*v)
	public Vector3f transform(Vector3f v)
	{
		return new Vector3f(m[0][0] * v.getX() + m[0][1] * v.getY() + m[0][2] * v.getZ() + m[0][3],
				m[1][0] * v.getX() + m[1][1] * v.getY() + m[1][2] * v.getZ() + m[1][3],
				m[2][0] * v.getX() + m[2][1] * v.getY() + m[2][2] * v.getZ() + m[2][3]);
	}
	
	// multiplies this matrix M by another matrix n to give Mn
	public Matrix4f mul(Matrix4f n)
	{
		Matrix4f result = new Matrix4f();
		
		for(int i=0; i<4; i++)
		{
			for(int j=0; j<4; j++)
			{
				result.set(i, j, m[i][0] * n.get(0, j) +
								 m[i][1] * n.get(1, j) +
								 m[i][2] * n.get(2, j) +
								 m[i][3] * n.get(3, j));
			}
		}
		
		return result;
	}
	
	public Matrix4f transpose()
	{
		float[][] t = new float[4][4];
		for(int i=0; i<4;i++)
		{
			for(int j=0; j<4;j++)
			{
				t[j][i] = m[i][j];
			}
		}
		return new Matrix4f(t);
	}
	
	/*
	 * trace() - returns the sum of the entries along the main diagonal 
	 */
	public float trace()
	{
		return m[0][0]+m[1][1]+m[2][2]+m[3][3];
	}

	public float[][] getM() {
		return m;
	}
	
	public float[][] getCopyOfM()
	{
		float[][] result = new float[4][4];
		
		for(int i=0; i<4; i++)
			for(int j=0; j<4; j++)
				result[i][j] = m[i][j];
		
		return result;
	}

	public void setM(float[][] m) {
		this.m = m;
	}
	
	public float get(int i, int j)
	{
		return m[i][j];
	}
	
	public void set(int i, int j, float value)
	{
		m[i][j] = value;
	}
	
	public String toString()
	{
		String str = "";
		for(int i=0; i<4; i++) 
		{
			for(int j=0; j<4; j++)
				str += m[i][j] + "\t\t";
			str += "\n";
		}
		
		return str;
	}

}

