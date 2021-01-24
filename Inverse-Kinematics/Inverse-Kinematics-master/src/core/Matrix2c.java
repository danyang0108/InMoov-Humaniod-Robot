package core;

public class Matrix2c {
	private Complex[][] m;

	public Matrix2c() {
		m = new Complex[2][2];
	}
	
	public Matrix2c(Complex c00, Complex c01, Complex c10, Complex c11) {
		m = new Complex[2][2];
		m[0][0] = c00;
		m[0][1] = c01;
		m[1][0] = c10;
		m[1][1] = c11;
	}
	
	public Complex get(int i, int j)
	{
		return m[i][j];
	}
	
	public void set(int i, int j, Complex val)
	{
		m[i][j] = val;
	}
	
	public Matrix2c mul(Matrix2c n)
	{
		Matrix2c M = new Matrix2c();
		M.set(0, 0, m[0][0].mul(n.get(0, 0)).add(m[0][1].mul(n.get(1, 0))));
		M.set(0, 1, m[0][0].mul(n.get(0, 1)).add(m[0][1].mul(n.get(1, 1))));
		M.set(1, 0, m[1][0].mul(n.get(0, 0)).add(m[1][1].mul(n.get(1, 0))));
		M.set(1, 1, m[1][0].mul(n.get(0, 1)).add(m[1][1].mul(n.get(1, 1))));
		
		return M;
	}
	
	public Matrix2c transpose()
	{
		Matrix2c n = new Matrix2c(m[0][0], m[1][0], m[0][1], m[1][1]);
		return n;
	}
	
}

