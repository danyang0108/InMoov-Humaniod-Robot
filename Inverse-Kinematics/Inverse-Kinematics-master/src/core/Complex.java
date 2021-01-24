package core;

/**
 * Complex.java
 * Class to represent complex numbers of the form a + bi
 * @author radulov
 *
 */
public class Complex {
	private float a;	// rational (or real) part
	private float b;	// coeff of imaginary part
	
	public float getA() {
		return a;
	}

	public void setA(float a) {
		this.a = a;
	}

	public float getB() {
		return b;
	}

	public void setB(float b) {
		this.b = b;
	}

	public Complex(float a, float b)
	{
		this.a = a;
		this.b = b;
	}
	
	public Complex add(Complex v)
	{
		return new Complex(a+v.getA(), b+v.getB());
	}
	
	public Complex mul(Complex v)
	{
		return new Complex(a*v.getA() - b*v.getB(), a*v.getB() + b*v.getA());
	}
	
	public Complex mul(float s)
	{
		return new Complex(s*a, s*b);
	}
	
}

