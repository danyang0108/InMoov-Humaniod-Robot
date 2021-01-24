package core;

public class Point3Df {
	private float x, y, z;
	
	public Point3Df()
	{
		x=0;
		y=0;
		z=0;
	}
	
	public Point3Df(float x, float y, float z)
	{
		this.x=x;
		this.y=y;
		this.z=z;
	}
	
	public Point3Df(Point3Df p)
	{
		this.x = p.x;
		this.y = p.y;
		this.z = p.z;
	}
	
	public Vector3f sub(Point3Df p)
	{
		return new Vector3f(x-p.getX(), y-p.getY(), z-p.getZ());
	}
	public Point3Df add(Vector3f v)
	{
		return new Point3Df(x + v.getX(), y + v.getY(), z + v.getZ());
	}
	
	public Point3Df sub(Vector3f v)
	{
		return new Point3Df(x - v.getX(), y - v.getY(), z - v.getZ());
	}
	
	public Point3Df mul(float s)
	{
		return new Point3Df(s*x, s*y, s*z);
	}
	
	public Point3Df mul(float xs, float ys, float zs)
	{
		return new Point3Df(xs*x, ys*y, zs*z);
	}
	
	public float getX()
	{
		return x;
	}
	public float getY()
	{
		return y;
	}
	public float getZ()
	{
		return z;
	}
	public void setX(float x)
	{
		this.x=x;
	}
	public void setY(float x)
	{
		this.y=y;
	}
	
	public void setZ(float x)
	{
		this.z=z;
	}
	public void set(float x, float y, float z)
	{
		this.x=x;
		this.y=y;
		this.z=z;
	}
	
	public void set(Point3Df p)
	{
		this.x = p.x;
		this.y = p.y;
		this.z = p.z;
	}
	
	public String toString()
	{
		String str = "[";
		return str+x+", "+y+ ", "+z+"]";
	}
}
