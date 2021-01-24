package core;

public class Matrix {
	private float[][] m;

	public static void main(String[] args)
	{
		
		float m[][] = new float[4][4];
		m[0][0] = 9;	m[0][1] = 12;	m[0][2] = 11;	m[0][3] = 5;
		m[1][0] = 10;	m[1][1] = 2;	m[1][2] = 13;	m[1][3] = 6;
		m[2][0] = 14;	m[2][1] = 15;	m[2][2] = 5;	m[2][3] = 7;
		m[3][0] = 3;	m[3][1] = -3;	m[3][2] = -1;	m[3][3] = 1;
		
		float m1[][] = new float[3][3];
		m1[0][0] = 2;	m1[0][1] = 1;	m1[0][2] = 3;
		m1[1][0] = -1;	m1[1][1] = 4;	m1[1][2] = 0;
		m1[2][0] = 1;	m1[2][1] = -2;	m1[2][2] = 1;
		// inverse of the above matrix m1 should be:
		//  1.333333	-2.333333	-4
		//  0.333333	-0.333333	-1
		// -0.666666	 1.666666	 3
		
		float m2[][] = new float[3][3];
		m2[0][0] = 1;	m2[0][1] = 2;	m2[0][2] = 3;
		m2[1][0] = 1;	m2[1][1] = 3;	m2[1][2] = 1;
		m2[2][0] = -2;	m2[2][1] = 0;	m2[2][2] = 2;
		
		// m1 * m2 should be
		// -3	 7	13
		//  3	 10	 1
		// -3	-4	 3
		
		float m3[][] = new float[3][3];
		m3[0][0] = -256.48352f;	m3[0][1] = -186.44748f;	m3[0][2] = -186.31305f;
		m3[1][0] = 98.66391f;	m3[1][1] = 27.284973f;	m3[1][2] = -72.714935f;
		m3[2][0] = 0;			m3[2][1] = 0;			m3[2][2] = 0;
		
		
		System.out.println(new Matrix(m1).mul(new Matrix(m2)));
		System.out.println();
		Matrix m1_inv = new Matrix(m1).inverse();
		System.out.println("m1 inverse = ");
		System.out.println(m1_inv);
		System.out.println("m1 * m1_inv = ");
		System.out.println(new Matrix(m1).mul(m1_inv));
		
		System.out.println("m3_inv = ");
		Matrix m3_inv = new Matrix(m3).inverse();
		System.out.println(new Matrix(m1).mul(m3_inv));
		
		
		System.out.println();
		Matrix M = new Matrix(m);
		
		float[] N = {2,1,1,1};
		System.out.println(M.mul(new Matrix(N)));
		
		Matrix M_inv = new Matrix(M.getCopyOfM()).inverse();
		System.out.println("INVERSE = " + M_inv);
		System.out.println("M * M_inv = " + M.mul(M_inv));
		System.out.println("Pseudo Inverse = " + M.pseudoInverse());
	}
	
	public Matrix() {
		m = new float[4][4];
		setIdentity();
	}
	
	public Matrix(int row, int col) {
		m = new float[row][col];
		if(row==col)
			setIdentity();
	}
	
	public Matrix(float [][] r)
	{
		m = r;
//		int rows = r.length;
//		int cols = r[0].length;
//		m = new float[rows][cols];
//		for(int i=0; i<rows;i++)
//		{
//			for(int j=0; j<cols;j++)
//			{
//				m[i][j] = r[i][j];
//			}
//		}
	}
	
	// Matrix constructor for a single column
	public Matrix(float [] r)
	{
		int rows = r.length;
		
		m = new float[rows][1];
		for(int i=0; i<rows;i++)
		{
			m[i][0] = r[i];
		}
	}
	
	// Matrix constructor for a single column
		public Matrix(Vector3f v)
		{
			int rows = v.size();
			
			m = new float[rows][1];
			for(int i=0; i<rows;i++)
			{
				m[i][0] = v.get(i);
			}
		}
	
	public int getNumCols()
	{
		return m[0].length;
	}
	
	public int getNumRows()
	{
		return m.length;
	}
	
	public Matrix add(Matrix M2)
	{
		Matrix result = new Matrix(this.getNumRows(), this.getNumCols());
		for(int row=0; row<getNumRows(); row++)
		{
			for(int col=0; col<getNumCols(); col++)
			{
				result.set(row, col, m[row][col] + M2.get(row, col));
			}
		}
		return result;
	}
	
	// returns the pseudoInverse of this matrix as (J_transpose * J)^-1 * J_transpose * J
	public Matrix pseudoInverse()
	{
		if(!isUnderDetermined())
			return inverse();
		return transpose().mul(this).inverse().mul(transpose());
	}
	
	public boolean isUnderDetermined()
	{
		int numNonZeroRows = 0;
		for(int row=0; row<getNumRows(); row++)
		{
			int zeroEntries = 0;
			for(int col=0; col<getNumCols(); col++)
			{
				if(m[row][col] == 0)
				{
					zeroEntries++;
				}
			}
			if(zeroEntries != getNumCols())
				numNonZeroRows++;
		}
		if(getNumCols() > numNonZeroRows)
			return true;
		return false;
	}
	
	// Calculate and return the inverse of this Matrix
	// This method handles all types of matrices (non square, not invertible, etc)
	public Matrix inverse()
	{
		Matrix inverse;
		if(!isUnderDetermined()) {
			inverse = inverseSquare();
//			System.out.println("Jacobian = \n" + this);
//			System.out.println("Jacobian Inverse= \n" + inverse);
//			System.out.println("Multiplying the Jacobian with its inverse: \n" + this.mul(inverse));
			return inverse;
		}
		return pseudoInverse();
	}
	
	// Returns the inverse of this matrix iff it is a square matrix
	// It does NOT alter the original matrix
	public Matrix inverseSquare()
	{
		float pivot;
		float inverse[][] = new Matrix(new float[getNumRows()][getNumCols()]).setIdentity().getM();
		float[][] mc = this.getCopyOfM();
		for(int row=0; row<m.length; row++)
		{
			pivot = m[row][row];
			int pivotRow = row;
			int pivotCol = row;
			float largestPivot = pivot;
			if(pivot == 0)	// swap with another row with largest non-zero pivot
			{
				if(row<m.length)
				{
					// first find row to swap that is below this row
					for(int r=row+1; r<m.length; r++)
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
			}
			//System.out.print(this);
			//System.out.println(new Matrix(inverse));
			// Now that we have the pivot, divide the pivot row by pivot value
			if(largestPivot != 0)
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
		m = mc;
		return new Matrix(inverse);
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
	
	public Matrix setIdentity()
	{
		for(int r = 0; r<m.length; r++)
			for(int c = 0; c<m[0].length; c++)
			{
				if(r == c)
					m[r][c] = 1;
				else
					m[r][c] = 0;
			}
		
		return this;
	}
	
	public Matrix mul(float scalar)
	{
		Matrix result = new Matrix(this.getCopyOfM());
		for(int r = 0; r<m.length; r++)
			for(int c = 0; c<m[0].length; c++)
			{
				result.getM()[r][c] *= scalar;
			}
		return this;
	}
	
	public float[] mul(float[] deltaThetas)
	{
		if(getNumCols() != deltaThetas.length)
		{
			System.out.println("ERROR in Matrix.mul() : size of Matrix does not match size of deltaTheta list");
			return null;
		}
		float[] changeInEndEffector = new float[3];
		for(int i=0; i<getNumRows(); i++)
		{
			for(int j=0; j<getNumCols(); j++)
			{
				changeInEndEffector[i] += m[i][j]*deltaThetas[j];
			}
		}
		
		return changeInEndEffector;
	}
	
	// multiplies this matrix M by another matrix n to give Mn (if M->width != n->height retun null)
	public Matrix mul(Matrix n)
	{
		if(getNumCols() != n.getNumRows())
		{
			System.out.println("ERROR in Matrix.mul(Matrix M): matrix sizes are incompatible.");
			return null;
		}
		
		Matrix result = new Matrix(new float[this.getNumRows()][n.getNumCols()]);
		
		for(int i=0; i<getNumRows(); i++) // for each row
		{
			
			for(int j=0; j<n.getNumCols(); j++)	// for each column
			{
				float val=0;
				for(int k=0; k<getNumCols(); k++)
					val += m[i][k] * n.get(k, j);
				
				result.set(i, j, val);
			}
			
		}
		
		return result;
	}
	
	public Matrix transpose()
	{
		float[][] t = new float[getNumCols()][getNumRows()];
		for(int i=0; i<getNumRows();i++)
		{
			for(int j=0; j<getNumCols();j++)
			{
				t[j][i] = m[i][j];
			}
		}
		return new Matrix(t);
	}
	
	/*
	 * trace() - returns the sum of the entries along the main diagonal 
	 */
	public float trace()
	{
		if(m.length != m[0].length)
		{
			System.out.println("ERROR in Matrix:trace: matrix is not a square.");
			return 0;
		}
		float sum=0;
		for(int i=0; i<m.length; i++)
			sum += m[i][i];
		return sum;
	}

	public float[][] getM() {
		return m;
	}
	
	public float[][] getCopyOfM()
	{
		float[][] result = new float[m.length][m[0].length];
		
		for(int i=0; i<m.length; i++)
			for(int j=0; j<m[0].length; j++)
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
	
	// set column col of this Jacobian to the vector v
		public void setCol(int col, Vector3f v)
		{
			if(m.length != v.size())
			{
				System.out.println("ERROR in Jacobian.seCol(): Vector v size = " + v.size() + ", but should be " + m.length);
				return;
			}
			for(int i=0; i<3; i++)
			{
				m[i][col] = v.get(i);
			}
		}
		
		// sets a column col in this Jacobian to be equal to an array of V
		public void setCol(int col, float[] v)
		{
			if(m.length != v.length)
			{
				System.out.println("ERROR in Jacobian.setCol(): Vector v size = " + v.length + ", but should be " + m.length);
				return;
			}
			for(int i=0; i<v.length; i++)
			{
				m[i][col] = v[i];
			}
		}
	
	public String toString()
	{
		String str = "";
		for(int i=0; i<m.length; i++) 
		{
			for(int j=0; j<m[0].length; j++)
				str += m[i][j] + "\t\t";
			str += "\n";
		}
		
		return str;
	}

}


