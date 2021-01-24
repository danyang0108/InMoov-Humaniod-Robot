package ik;

import core.Vector3f;
import core.Matrix;


// this Jacobian assumes 3 rows (x,y,z)  (will be expanded to include any number of rows...for perhaps orientation, constraints, etc)
public class Jacobian {
	private Matrix J;
	
	// creates a new Jacobian matrix of size 3 X N, where N = the number of joints
	public Jacobian(int M, int N)
	{
		J = new Matrix(M,N);
	}
	
	public Jacobian(Matrix m)
	{
		J = m;
	}
	
	// Calculate and return the inverse of this Jacobian
	public Matrix inverse()
	{
		Matrix inverse;

		if(!J.isUnderDetermined()) {
			inverse = J.inverse();
			System.out.println("Jacobian = \n" + J);
			System.out.println("Jacobian Inverse= \n" + inverse);
			System.out.println("Multiplying the Jacobian with its inverse: \n" + J.mul(inverse));
			return inverse;
		}
		return J.pseudoInverse();
	}
	
	public Matrix transpose()
	{
		return J.transpose();
	}
	
	// set column col of this Jacobian to the vector v
	public void setCol(int col, Vector3f v)
	{
		J.setCol(col, v);
	}
	
	// sets a column col in this Jacobian to be equal to an array of V
	public void setCol(int col, float[] v)
	{
		J.setCol(col, v);
	}
	
	public int getNumCols()
	{
		return J.getNumCols();
	}
	
	public int getNumRows()
	{
		return J.getNumRows();
	}
	
	public String toString()
	{
		return J.toString();
	}
}
