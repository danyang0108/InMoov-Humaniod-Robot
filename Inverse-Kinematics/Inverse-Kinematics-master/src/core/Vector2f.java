package core;

public class Vector2f {
	
	private float x;
	private float y;
	public Vector2f(float x, float y) {
		super();
		this.x = x;
		this.y = y;
	}
	
	public float length()
	{
		return (float)Math.sqrt(x*x+y*y);
	}
	
	public float max()
	{
		return Math.max(x, y);
	}
	
	public float dot(Vector2f v)
	{
		return x*v.getX() + y*v.getY();
	}
	
	public Vector2f normalize()		// this is the only mutable method! 
	{
		float length = length();
		
		x /= length;
		y /= length;
		
		return this;
	}
	
	public float cross(Vector2f v)
	{
		return x * v.getY() - y * v.getX();
	}
	
	public Vector2f lerp(Vector2f dest, float lerpFactor)
	{
		return dest.sub(this).mul(lerpFactor).add(this);
	}
	
	public Vector2f rotate(float angle)
	{
		double rad = Math.toRadians(angle);
		double cos = Math.cos(rad);
		double sin = Math.sin(rad);
		
		return new Vector2f((float)(x*cos - y*sin), (float)(x*sin + y*cos));
	}
	
	public Vector2f add(Vector2f v)
	{
		return new Vector2f(x+v.getX(), y+v.getY());
	}
	
	public Vector2f sub(Vector2f v)
	{
		return new Vector2f(x-v.getX(), y-v.getY());
	}
	
	public Vector2f mul(float s)
	{
		return new Vector2f(s*x, s*y);
	}
	
	public Vector2f div(float s)
	{
		return new Vector2f(x/s, y/s);
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
	
	public Vector2f set(float x, float y) 
	{ 
		this.x = x; this.y = y; return this;
	}
	
	public Vector2f set(Vector2f v)
	{
		set(v.getX(), v.getY()); return this;
	}
	
	public String toString()
	{
		return "(" + x + ", " + y + ")";
	}
	
	public boolean equals(Vector2f v)
	{
		return x == v.getX() && y == v.getY();
	}

}

