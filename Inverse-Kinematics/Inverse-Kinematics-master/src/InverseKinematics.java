import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;


import core.Camera;
import core.Line;
import core.Math3D;
import core.Plane;
import core.Point3Df;
import core.Vector2f;
import core.Vector3f;
import ik.Bone;
import ik.JointSystem;
import object.Mesh;

import java.nio.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;


public class InverseKinematics {

	// The window handle
	private long window;
	
	private int WINDOW_WIDTH = 800;
	private int WINDOW_HEIGHT = 600;
	
	String title = "Kinematics V1.3 -- LWJGL ";
	
	Bone rootBone;
	Point3Df end_effector_goal;		// this is the desired location of the end effector (obtained by reading in mouse position)
	private boolean runSimulation = false;
	
	Camera camera;
	
	private boolean leftMouseButtonPressed = false;
	private boolean mouseMove = false;				// keeps track of whether mouse is moving
	private Vector2f currentMousePos;
	private Vector2f deltaPos;
	private Vector2f centerPosition;
	private float sensitivity = 0.1f;			// camera rotation sensitivity
	private float speed = 4;						// speed of camera translational movement
	
	private Point3Df cursor3D;
	

	public void run() {
		System.out.println(title + Version.getVersion());

		init();
		loop();

		// Free the window callbacks and destroy the window
		glfwFreeCallbacks(window);
		glfwDestroyWindow(window);

		// Terminate GLFW and free the error callback
		glfwTerminate();
		glfwSetErrorCallback(null).free();
	}

	private void init() {
		end_effector_goal = new Point3Df();
		
		// Setup an error callback. The default implementation
		// will print the error message in System.err.
		GLFWErrorCallback.createPrint(System.err).set();

		// Initialize GLFW. Most GLFW functions will not work before doing this.
		if ( !glfwInit() )
			throw new IllegalStateException("Unable to initialize GLFW");

		// Configure GLFW
		glfwDefaultWindowHints(); // optional, the current window hints are already the default
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

		// Create the window
		window = glfwCreateWindow(WINDOW_WIDTH, WINDOW_HEIGHT, title + Version.getVersion(), NULL, NULL);
		if ( window == NULL )
			throw new RuntimeException("Failed to create the GLFW window");
		
		glfwSetInputMode(window, GLFW_STICKY_KEYS, GLFW_TRUE);

		// Setup a key callback. It will be called every time a key is pressed, repeated or released.
		glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
			if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
				glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
		});
		
		// setup a mouse position callback
		GLFWCursorPosCallback posCallback;
		glfwSetCursorPosCallback(window, posCallback = new GLFWCursorPosCallback() {
			@Override
			public void invoke(long window, double xpos, double ypos) {
				centerPosition = new Vector2f(WINDOW_WIDTH/2.0f, WINDOW_HEIGHT/2.0f);
				if(leftMouseButtonPressed && mouseMove)
				{
					currentMousePos = new Vector2f((float)xpos, (float)ypos);
					deltaPos = currentMousePos.sub(centerPosition);
					//System.out.println(deltaPos);
					boolean rotY = deltaPos.getX() != 0;
					boolean rotX = deltaPos.getY() != 0;
					
					if(rotY)
					{
						camera.getTransform().rotate(camera.yAxis, (float)Math.toRadians(deltaPos.getX() * sensitivity));
						//System.out.println("FreeLook.input(): rotating about yAxis, looking in direction " + getTransform().getRot().getForward());
					}
					if(rotX) 
					{
						camera.getTransform().rotate(camera.getTransform().getRot().getRight(), (float)Math.toRadians(-deltaPos.getY() * sensitivity));
						//System.out.println("FreeLook.input(): rotating about xAxis, looking in direction " + getTransform().getRot().getForward());
					}
					if(rotY || rotX)
						currentMousePos = new Vector2f(WINDOW_WIDTH/2.0f, WINDOW_HEIGHT/2.0f);
					
					glfwSetCursorPos(window, centerPosition.getX(), centerPosition.getY());
				}else if(leftMouseButtonPressed)
				{
					mouseMove = true;
					
				}else	// mouse buttons are not pressed
				{
					mouseMove = false;
					currentMousePos = new Vector2f((float)xpos, (float)ypos);
					float xOnNearPlane = camera.getLeft() + currentMousePos.getX()*(camera.getRight()-camera.getLeft())/(WINDOW_WIDTH);
					float yOnNearPlane = camera.getBottom() + currentMousePos.getY()*(camera.getTop()-camera.getBottom())/(WINDOW_HEIGHT);
					System.out.print("xOnNearPlane = " + xOnNearPlane);
					System.out.println("\tyOnNearPlane = " + yOnNearPlane);
					Point3Df pointOnPlane = new Point3Df(xOnNearPlane, yOnNearPlane, camera.getzNear());
					System.out.println("pointOnPlane (in Camera coords) = " + pointOnPlane);
					System.out.println("Camera Left = " + camera.getLeft());
					//transform pointOnPlane to world coords
					pointOnPlane = camera.getCameraMatrix().transform(pointOnPlane);
					System.out.println("pointOnPlane (in World coords)= " + pointOnPlane);
					Vector3f dir = pointOnPlane.sub(camera.getTransform().getPos());
					System.out.println("dir = " + dir);
					//float t = -camera.getTransform().getPos().getY()/dir.getY();
					System.out.println("Camera position in world coords = " + camera.getTransform().getPos());
					cursor3D = Math3D.intersect(new Line(camera.getTransform().getPos(),  dir), new Plane(0,1,0,0));
					//cursor3D.setX(-cursor3D.getX());
					System.out.println("3D Cursor device coords: " + currentMousePos); 
					System.out.println("3D Cursor at pos: " + cursor3D); 
				}
				//System.out.println("cursor moved to (x, y) = (" + xpos + ", " + (WINDOW_HEIGHT-ypos) + ")");
			}
		});
		
		// setup a key pressed callback
		GLFWKeyCallback keyCallback;
		glfwSetKeyCallback(window, keyCallback = new GLFWKeyCallback() {
			@Override
			public void invoke(long window, int key, int scancode, int action, int mods) {
				System.out.println("Key " + key + " was pressed.");
				float movAmt = speed * 1/60.0f;
				if(key == 265)	// UP arrow key pressed
				{
					move(camera.getTransform().getRot().getForward(), movAmt);
				}else if (key == 264) // down arrow key pressed
				{
					move(camera.getTransform().getRot().getBack(), movAmt);
				}
				else if (key == 262) // right arrow key pressed
				{
					move(camera.getTransform().getRot().getRight(), movAmt);
				}else if (key == 263) // left arrow key pressed
				{
					move(camera.getTransform().getRot().getLeft(), movAmt);	
				}else if (key == 32) // space key pressed
				{
					runSimulation = !runSimulation;
				}
				
			}
		});
		
		// Mouse button callback
		GLFWMouseButtonCallback mouseButtonCallback;
		glfwSetMouseButtonCallback(window, mouseButtonCallback = new GLFWMouseButtonCallback() {

		    @Override
		    public void invoke(long window, int button, int action, int mods) {
		    	DoubleBuffer xpos = BufferUtils.createDoubleBuffer(1);
	            DoubleBuffer ypos = BufferUtils.createDoubleBuffer(1);
		        if(action == GLFW_PRESS)
		        {
		        	if (button == GLFW_MOUSE_BUTTON_1)	// left mouse button
		        	{
		        		leftMouseButtonPressed = true;
		        		// hide cursor
						glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
		        		centerPosition = new Vector2f(WINDOW_WIDTH/2.0f, WINDOW_HEIGHT/2.0f);
		        		glfwSetCursorPos(window, centerPosition.getX(), centerPosition.getY());
		        	}
		        	if (button == GLFW_MOUSE_BUTTON_2)	// right mouse button
		        	{
		        		glfwGetCursorPos(window, xpos, ypos);
		        		end_effector_goal.set(cursor3D);
		        	}
		        }else if(action == GLFW_RELEASE)
		        {
		        	if (button == GLFW_MOUSE_BUTTON_1)	// left mouse button released
		        	{
		        		leftMouseButtonPressed = false;
		        		// make cursor visible again
		        		glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
		        	}
		        }
		    }
		});

		// Get the thread stack and push a new frame
		try ( MemoryStack stack = stackPush() ) {
			IntBuffer pWidth = stack.mallocInt(1); // int*
			IntBuffer pHeight = stack.mallocInt(1); // int*

			// Get the window size passed to glfwCreateWindow
			glfwGetWindowSize(window, pWidth, pHeight);

			// Get the resolution of the primary monitor
			GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

			// Center the window
			glfwSetWindowPos(
				window,
				(vidmode.width() - pWidth.get(0)) / 2,
				(vidmode.height() - pHeight.get(0)) / 2
			);
		} // the stack frame is popped automatically

		// Make the OpenGL context current
		glfwMakeContextCurrent(window);
		// Enable v-sync
		glfwSwapInterval(1);

		// Make the window visible
		glfwShowWindow(window);
	}
	
	private void loop() {
		// This line is critical for LWJGL's interoperation with GLFW's
		// OpenGL context, or any context that is managed externally.
		// LWJGL detects the context that is current in the current thread,
		// creates the GLCapabilities instance and makes the OpenGL
		// bindings available for use.
		GL.createCapabilities();
		
		cursor3D = new Point3Df();
		
		camera = new Camera((float)(Math.toRadians(70)), (float)WINDOW_WIDTH/WINDOW_HEIGHT, .4f, 100);
	    //camera = new Camera(-250, 250, -250, 250, 0.1f, 100);
		camera.setPos(new Point3Df(0,5,5));
	    camera.getTransform().lookAt(new Point3Df(0,0.0f,0), new Vector3f(0,1,0));
	    System.out.println("Camera Left = " + camera.getLeft());
	    System.out.println("Camera Right = " + camera.getRight());
	    
	    // create a few bones to test
	    rootBone = new Bone(new Point3Df(0,0,0), 2, 0, new Vector3f(0,1,0));
	    rootBone.setName("root");
	    //rootBone.setAngleConstraint(0,180);
	    Bone b1 = new Bone(rootBone, 3, 0, new Vector3f(0,1,0));
	    b1.setName("bicep");
	    Bone b2 = new Bone(b1, 2, 0, new Vector3f(0,1,0));
	    b2.setName("forearm");
	    System.out.println("End effector = " + rootBone.getEndEffector());
	    
	    Bone rootPrism = new Bone(new Point3Df(0,0,0), 100, 0, new Vector3f(0,0,1));
	    
	    Mesh boneMesh = new Mesh("Bone.obj");
	    rootBone.setMesh(boneMesh);
	    
//	    Mesh testPrism = new Mesh("testPyramid.obj");
//	    rootBone.setMesh(testPrism);
	    
	    JointSystem RobotArm = new JointSystem(rootBone);
	    
	    glViewport (0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);

		// Set the clear color
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);	
		glColorMaterial ( GL_FRONT, GL_AMBIENT_AND_DIFFUSE ) ;
		float mat_specular[] = { 1.0f, 1.0f, 1.0f, 1.0f };
		//float mat_shininess[] = { 50.0f };
		float light_position[] = { 10.0f, 10.0f, 10.0f, 0.0f };
		
		glMaterialfv(GL_FRONT, GL_SPECULAR, mat_specular);
		//glMaterialfv(GL_FRONT, GL_SHININESS, mat_shininess);
		glLightfv(GL_LIGHT0, GL_POSITION, light_position);
		
		glEnable(GL_LIGHTING);
	    glEnable(GL_LIGHT0);
	    glEnable(GL_DEPTH_TEST);

		// Run the rendering loop until the user has attempted to close
		// the window or has pressed the ESCAPE key.
		while ( !glfwWindowShouldClose(window) ) {
			
			Vector2f centerPosition = new Vector2f(WINDOW_WIDTH/2, WINDOW_HEIGHT/2);
			
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
			
			// set the color of the quad (R,G,B,A)
			glShadeModel(GL_SMOOTH);
	        //GL11.glColor3f(0.5f,0.5f,1.0f);
			
			GL11.glMatrixMode(GL11.GL_PROJECTION);
			GL11.glLoadMatrixf(camera.getProjection().getOpenGlMatrix());
		    
	             
			GL20.glMatrixMode(GL20.GL_MODELVIEW);
	        GL11.glLoadMatrixf(camera.getViewMatrix().getOpenGlMatrix());
	        
	        drawGrid(10, 10, 10);
	        
	        // draw xyz-axis
	        glLineWidth(3);
	        GL11.glColor3f(1.0f, 0, 0);
	        GL11.glBegin(GL_LINES);
	        GL11.glVertex3f(0, 0, 0);
	        GL11.glVertex3f(5, 0, 0);
	        GL11.glEnd();
	        GL11.glColor3f(0, 1.0f, 0);
	        GL11.glBegin(GL_LINES);
	        GL11.glVertex3f(0, 0, 0);
	        GL11.glVertex3f(0, 5, 0);
	        GL11.glEnd();
	        GL11.glColor3f(0, 0, 1.0f);
	        GL11.glBegin(GL_LINES);
	        GL11.glVertex3f(0, 0, 0);
	        GL11.glVertex3f(0, 0, 5);
	        GL11.glEnd();
	        glLineWidth(5);
	        
	        glDisable(GL_LIGHTING);
	        glColor3f(0f, 1.0f, 0f);
	        glPointSize(5);
	        GL11.glBegin(GL_POINTS);
	       		GL11.glVertex3f(cursor3D.getX(), cursor3D.getY(), cursor3D.getZ());
	        GL11.glEnd();
	        glLineWidth(1);
	        
	        glColor3f(1.0f, 0f, 0f);
	        glPointSize(5);
	        GL11.glBegin(GL_POINTS);
	       		GL11.glVertex3f(end_effector_goal.getX(), end_effector_goal.getY(), end_effector_goal.getZ());
	        GL11.glEnd();
	        glLineWidth(1);
	        
	        glEnable(GL_LIGHTING);
	        RobotArm.setDesiredEndEffectorPos(end_effector_goal);
	        if(runSimulation)
	        	RobotArm.moveToTarget();
	        RobotArm.draw(0.01, camera);
	        //testPrism.draw(camera);
	        
			glfwSwapBuffers(window); // swap the color buffers

			// Poll for window events. The key callback above will only be
			// invoked during this call.
			glfwPollEvents();

		}
	}

	public static void main(String[] args) {
		new InverseKinematics().run();
	}
	
	private void sync(double loopStartTime) {
		   float loopSlot = 1f / 50;
		   double endTime = loopStartTime + loopSlot; 
		   while(getTime() < endTime) {
		       try {
		           Thread.sleep(1);
		       } catch (InterruptedException ie) {}
		   }
		}
	
	public double getTime()
	{
		return GLFW.glfwGetTime();	// return current time in seconds
	}
	
	public void drawGrid(float width, float length, int numDivisions)
	{
		float deltaX = width/numDivisions;
		float deltaZ = length/numDivisions;
		glEnable(GL_COLOR_MATERIAL); // sets your shapes material information to be that of the colour you assigned to it
		GL11.glColor3f(1.0f, 1.0f, 1.0f);
		GL11.glBegin(GL_LINES);
		for(int i=0; i<=numDivisions; i++)
		{
			GL11.glVertex3f(-width/2, 0, length/2-i*deltaZ);
			GL11.glVertex3f(width/2, 0, length/2-i*deltaZ);
			GL11.glVertex3f(width/2-i*deltaX, 0, -length/2);
			GL11.glVertex3f(width/2-i*deltaX, 0, length/2);
		}
        GL11.glEnd();
	}
	
	// move camera in direction dir by amount amt
	public void move(Vector3f dir, float amt)
	{
		camera.getTransform().setPos(camera.getTransform().getPos().sub(dir.mul(amt)));	// subtracting dir because we are in WCS here, 
		//System.out.println("FreeMove.input(): moving in direction " + dir);
	}

}