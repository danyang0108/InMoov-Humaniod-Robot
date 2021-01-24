package core;

public class Math3D {
	
	// returns the intersection point of a given  line and a given plane
	public static Point3Df intersect(Line line, Plane plane)
	{
		float t = (-(plane.getA()*line.getPoint().getX() + plane.getB()*line.getPoint().getY() + plane.getC()*line.getPoint().getZ() + plane.getD()))/
					(plane.getA()*line.getVector().getX() + plane.getB()*line.getVector().getY() + plane.getC()*line.getVector().getZ());
		
		Point3Df intersect = line.getPoint().add(line.getVector().mul(t));
		return intersect;
	}

}
