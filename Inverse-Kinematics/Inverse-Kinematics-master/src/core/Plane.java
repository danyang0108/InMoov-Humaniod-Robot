package core;

public class Plane {
	// line defined by Ax + By + Cz + D = 0  (scalar equation of plane, where n = (A, B, C)
	private float A;
	private float B;
	private float C;
	private float D;
	
	public Plane(float a, float b, float c, float d)
	{
		A=a;
		B=b;
		C=c;
		D=d;
	}
	
	public void set(float a, float b, float c, float d)
	{
		A=a;
		B=b;
		C=c;
		D=d;
	}

	public float getA() {
		return A;
	}

	public void setA(float a) {
		A = a;
	}

	public float getB() {
		return B;
	}

	public void setB(float b) {
		B = b;
	}

	public float getC() {
		return C;
	}

	public void setC(float c) {
		C = c;
	}

	public float getD() {
		return D;
	}

	public void setD(float d) {
		D = d;
	}
	

}
