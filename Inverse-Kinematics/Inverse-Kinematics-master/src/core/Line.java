package core;

public class Line {
	// line is defined by a point and a vector
	Point3Df p;
	Vector3f v;
	
	public Line(Point3Df p, Vector3f v)
	{
		this.p = p;
		this.v = v;
	}
	
	public Point3Df getPoint()
	{
		return p;
	}
	
	public Vector3f getVector()
	{
		return v;
	}
	
	public void setPoint(Point3Df p)
	{
		this.p = p;
	}
	
	public void setVector(Vector3f v)
	{
		this.v = v;
	}

}
