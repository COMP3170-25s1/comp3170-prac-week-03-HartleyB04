package comp3170.week3;

import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL41.*;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import comp3170.OpenGLException;
import comp3170.IWindowListener;
import comp3170.Shader;
import comp3170.ShaderLibrary;
import comp3170.Window;

public class Week3 implements IWindowListener {

	private Window window;
	private Shader shader;
	
	final private File DIRECTORY = new File("src/comp3170/week3"); 
	
	private int width = 800;
	private int height = 800;
	private Scene scene;
	
	private long oldTime;
	
	public Week3() throws OpenGLException  {
		
		// create window with title, size, and a listener (this)
		window = new Window("Week 3 prac", width, height, this);
		
		// sets the window as resizable
		window.setResizable(true);
		// start running the window
		window.run();
	}
	
	@Override
	public void init() {
		
		new ShaderLibrary(DIRECTORY);
		// set the background colour to white
		glClearColor(1.0f, 1.0f, 1.0f, 1.0f);	
		
		// create the scene
		scene = new Scene();
		
		oldTime = System.currentTimeMillis();
		
	}
	
//	private void update() {
//		long time = System.currentTimeMillis();
//		float deltaTime = (time - oldTime) / 1000f;
//		oldTime = time;
//		System.out.println("update: dt = " + deltaTime + "s");
//
//		scene.update(deltaTime);
//	}

	public void update() {
		long time = System.currentTimeMillis();
		float deltaTime = (time - oldTime) / 1000f;
		oldTime = time;
		scene.update(deltaTime);
	}


	@Override
	public void draw() {
		update();

        // clear the colour buffer
		glClear(GL_COLOR_BUFFER_BIT);	
		
		scene.draw();
		
//		// restrict the framerate by sleeping between frames
//				try {
//					TimeUnit.MILLISECONDS.sleep(1000 / frameRate);
//				} catch (InterruptedException e) {
//				}

	    
	}

	@Override
	public void resize(int width, int height) {
		this.width = width;
		this.height = height;
		glViewport(0,0,width,height);
		
	}
	
	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}

	public static void main(String[] args) throws IOException, OpenGLException{
		new Week3();
	}

	
	
}
