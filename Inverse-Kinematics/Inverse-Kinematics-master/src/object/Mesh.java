package object;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import core.Camera;
import core.Matrix4f;
import core.Point3Df;
import core.Vector3f;
import ik.Bone;

/**
 * @author radulovic
 *
 */
public class Mesh {
	protected ArrayList<Point3Df> vertices;
	protected ArrayList<Vector3f> normals;
	protected ArrayList<ArrayList<int[]>> objects; // each object contains vertex indices and normal indices, interleaved
	
	// stores max and min values of this mesh object
	protected float max_x = Float.MIN_VALUE;
	protected float min_x = Float.MAX_VALUE;
	protected float scaleFactor_x = 1;
	
	public Mesh(String fileName)
	{
		Scanner scan;
		try {
			scan = new Scanner(new File("Resources/Models/" + fileName));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}
		
		ArrayList<String> lines;
		objects = new ArrayList();		// contains an array of indices for one face
		normals = new ArrayList();
		vertices = new ArrayList();

		while(scan.hasNextLine())
		{
			String line = scan.nextLine();
			if(line.charAt(0) == 'o')	// new object begins
			{

				objects.add(new ArrayList());
			}else if(line.charAt(0) == 'v' && line.charAt(1) == ' ')	// add new vertex to this object
			{
				String[] str = line.split(" ");
				Point3Df p = new Point3Df(Float.parseFloat(str[1]), Float.parseFloat(str[2]),Float.parseFloat(str[3]));
				if(p.getX() > max_x)
					max_x = p.getX();
				else if(p.getX() < max_x)
					min_x = p.getX();
				vertices.add(p);
			}else if(line.charAt(0) == 'v' && line.charAt(1) == 'n')
			{
				String[] str = line.split(" ");
				Vector3f n = new Vector3f(Float.parseFloat(str[1]), Float.parseFloat(str[2]),Float.parseFloat(str[3]));
				normals.add(n);
			}else if(line.charAt(0) == 'f')
			{
				String[] str = line.split(" ");
				int[] face = new int[str.length-1];
				int[] normal = new int[str.length-1];
				for(int i=1; i<str.length; i++)
				{
					String vtn = str[i];
					String[] vtnString = vtn.split("/");
					face[i-1] = Integer.parseInt(vtnString[0]);
					normal[i-1] = Integer.parseInt(vtnString[2]);
				}
				objects.get(objects.size()-1).add(face);
				objects.get(objects.size()-1).add(normal);
			}
		}
		//System.out.println((objects.get(0).get(0))[0]);
		scan.close();
		calcNormals();
	}

	// calculates normals for each vertex, assuming a triangular mesh
	public void calcNormals()		
	{
		for(int i=0; i<objects.size(); i++)
		{	
			for(int f=0; f<objects.get(i).size(); f+=2)
			{
//				for(int j=0; j<objects.get(i).get(f).length; j++)
//				{
					Point3Df p0 = vertices.get((objects.get(i).get(f))[0]-1);
					Point3Df p1 = vertices.get((objects.get(i).get(f))[1]-1);
					Point3Df p2 = vertices.get((objects.get(i).get(f))[2]-1);

					Vector3f v1 = p1.sub(p0);
					Vector3f v2 = p2.sub(p0);
					Vector3f newNormal = v1.cross(v2).normalize();

					Vector3f normal = normals.get((objects.get(i).get(f+1))[0]-1);
					normal = normal.add(newNormal);
					normals.set((objects.get(i).get(f+1))[0]-1, normal);
					normal = normals.get((objects.get(i).get(f+1))[1]-1);
					normal = normal.add(newNormal);
					normals.set((objects.get(i).get(f+1))[1]-1, normal);
					normal = normals.get((objects.get(i).get(f+1))[2]-1);
					normal = normal.add(newNormal);
					normals.set((objects.get(i).get(f+1))[2]-1, normal);
//				}
			}
		}
		// finally normalize all normals
		for(int i=0; i<normals.size(); i++)
		{
			normals.set(i, normals.get(i).normalize());
		}
	}
	
	public void draw(Camera cam)
	{
		for(int o=0; o<objects.size(); o++)
		{
			for(int f=0; f<objects.get(o).size(); f+=2)
			{
				GL11.glBegin(GL11.GL_POLYGON);
				for(int i=0; i<objects.get(o).get(f).length; i++)
				{
					Point3Df p = vertices.get((objects.get(o).get(f))[i]-1);
					p = p.mul(10);

					GL11.glVertex3f(p.getX(),p.getY(), p.getZ());
					Vector3f n = normals.get((objects.get(o).get(f+1))[i]-1);
					GL11.glNormal3f(n.getX(), n.getY(), n.getZ());
				}
				GL11.glEnd();
			}
		}
	}
	
	public void draw(Bone b, Camera cam)
	{
		GL20.glPushMatrix();
		GL20.glLoadIdentity();
		Matrix4f m = b.getTransform().getTransformation();
		
		float mvmatrix[] = cam.getViewMatrix().mul(m).getOpenGlMatrix();
        GL20.glLoadMatrixf(mvmatrix);	// set the current openGL matrix
		
		
		for(int o=0; o<objects.size(); o++)
		{

			for(int f=0; f<objects.get(o).size(); f+=2)
			{
				GL11.glBegin(GL11.GL_POLYGON);
				for(int i=0; i<objects.get(o).get(f).length; i++)
				{
					Point3Df p = vertices.get((objects.get(o).get(f))[i]-1);
					if(o!=0)		
						p = p.mul(scaleFactor_x, 1/2.0f, 1/2.0f);
					else		// draw ball undistorted
						p = p.mul(scaleFactor_x, scaleFactor_x, scaleFactor_x);

					Vector3f n = normals.get((objects.get(o).get(f+1))[i]-1);
					GL11.glNormal3f(n.getX(), n.getY(), n.getZ());
					GL11.glVertex3f(p.getX(),p.getY(), p.getZ());
					
				}
				GL11.glEnd();
			}

		}
		GL20.glPopMatrix();
	}
	
	// returns the largest x value of this mesh
	public float getMaxX()
	{
		return max_x;
	}
	
	// returns the smallest x value of this mesh
	public float getMinX()
	{
		return min_x;
	}
	
	// returns the length of this mesh object in the x direction by subtracting min_x from max_x
	public float getLengthX()
	{
		return max_x - min_x; 
	}
	
	public void setScaleFactorX(float val)
	{
		scaleFactor_x = val/getLengthX();
	}
	
	public float getScaleFactorX()
	{
		return scaleFactor_x;
	}
	
}
