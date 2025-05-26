package comp3170.week3;

import static org.lwjgl.opengl.GL11.GL_FILL;
import static org.lwjgl.opengl.GL11.GL_FRONT_AND_BACK;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL11.glPolygonMode;
import static org.lwjgl.opengl.GL15.glBindBuffer;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import comp3170.GLBuffers;
import comp3170.Shader;
import comp3170.ShaderLibrary;
import static comp3170.Math.TAU;

public class Plane {
	
	final private String VERTEX_SHADER = "vertex.glsl";
	final private String FRAGMENT_SHADER = "fragment.glsl";

	private Vector4f[] vertices;
	private int vertexBuffer;
	private int[] indices;
	private int indexBuffer;
	private Vector3f[] colours;
	private int colourBuffer;

	private Matrix4f modelMatrix = new Matrix4f(); // create object matrix
	private Matrix4f transMatrix = new Matrix4f();
	private Matrix4f rotMatrix = new Matrix4f();
	private Matrix4f scalMatrix = new Matrix4f();
	
	final private Vector3f OFFSET = new Vector3f(0.78f, 0.0f, 0.0f);
	final private float MOVEMENT_SPEED = 7.0f;
	final private float SCALE = 0.1f;
	final private float ROTATION_RATE = TAU/7;
	

	private Shader shader;
	
	public Plane() {

		shader = ShaderLibrary.instance.compileShader(VERTEX_SHADER, FRAGMENT_SHADER);

		// @formatter:off
			//          (0,1)
			//           /|\
			//          / | \
			//         /  |  \
			//        / (0,0) \
			//       /   / \   \
			//      /  /     \  \
			//     / /         \ \		
			//    //             \\
			//(-1,-1)           (1,-1)
			//
	 		
		vertices = new Vector4f[] {
			new Vector4f( 0, 0, 0, 1),
			new Vector4f( 0, 1, 0, 1),
			new Vector4f(-1,-1, 0, 1),
			new Vector4f( 1,-1, 0, 1),
		};
			
			// @formatter:on
		vertexBuffer = GLBuffers.createBuffer(vertices);

		// @formatter:off
		colours = new Vector3f[] {
			new Vector3f(1,0,1),	// MAGENTA
			new Vector3f(1,0,1),	// MAGENTA
			new Vector3f(1,0,0),	// RED
			new Vector3f(0,0,1),	// BLUE
		};
			// @formatter:on

		colourBuffer = GLBuffers.createBuffer(colours);

		// @formatter:off
		indices = new int[] {  
			0, 1, 2, // left triangle
			0, 1, 3, // right triangle
			};
			// @formatter:on

		indexBuffer = GLBuffers.createIndexBuffer(indices);
		
//		old model where scale would mess it up 
//		Matrix4f temp = new Matrix4f(); // create matrix object to pass through
//		modelMatrix = new Matrix4f(); // matrix object
//		modelMatrix.mul(translationMatrix(-0.75f, 0.7f, temp)); // for each transformation matrix 
//		modelMatrix.mul(rotationMatrix(0.75f, temp));
//		modelMatrix.mul(scaleMatrix(0.4f, 0.4f, temp));
		
	

//		Vector3f offset = new Vector3f(0.25f, 0.0f, 0.0f);
//		float scale = 0.1f;
		float rotation = TAU/1;
//		
//		good model
//		modelMatrix.translate(offset).rotateZ(rotation).scale(scale);
		
//		spinning house around 
//		modelMatrix.translate(OFFSET).scale(SCALE);
		
		
		// T R S Order Trans Rotate Scale
		Vector2f offset = new Vector2f(0.78f, 0.0f);
		Vector2f scale = new Vector2f(0.1f, 0.1f);
		
		translationMatrix(offset.x, offset.y, transMatrix);
		rotationMatrix(rotation, rotMatrix);
		scaleMatrix(scale.x, scale.y, scalMatrix);
		
		modelMatrix.mul(transMatrix).mul(rotMatrix).mul(scalMatrix);
		
		
		
	}
	
	public void update(float deltaTime) {
	    
		float movement = MOVEMENT_SPEED * deltaTime;
		float rotation = (ROTATION_RATE * deltaTime);
		modelMatrix.translate(0, movement, 0).rotateZ(rotation);
		
	}

	public void draw() {
		
		shader.enable();
		// set the attributes
		shader.setAttribute("a_position", vertexBuffer);
		shader.setAttribute("a_colour", colourBuffer);

		shader.setUniform("u_modelMatrix", modelMatrix); // set uniform in draw
		
		// draw using index buffer
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBuffer);
		
		glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
		glDrawElements(GL_TRIANGLES, indices.length, GL_UNSIGNED_INT, 0);

	}

	/**
	 * Set the destination matrix to a translation matrix. Note the destination
	 * matrix must already be allocated.
	 * 
	 * @param tx   Offset in the x direction
	 * @param ty   Offset in the y direction
	 * @param dest Destination matrix to write into
	 * @return
	 */

	public static Matrix4f translationMatrix(float tx, float ty, Matrix4f dest) {
		// clear the matrix to the identity matrix
		dest.identity();

		//     [ 1 0 0 tx ]
		// T = [ 0 1 0 ty ]
	    //     [ 0 0 0 0  ]
		//     [ 0 0 0 1  ]

		    // i          j          T
		// (1, 0, 0,   0, 1, 0,  tx, ty, 1)
		
		// Perform operations on only the x and y values of the T vec. 
		// Leaves the z value alone, as we are only doing 2D transformations.
		
		dest.m30(tx);
		dest.m31(ty);

		return dest;
	}

	/**
	 * Set the destination matrix to a rotation matrix. Note the destination matrix
	 * must already be allocated.
	 *
	 * @param angle Angle of rotation (in radians)
	 * @param dest  Destination matrix to write into
	 * @return
	 */

	public static Matrix4f rotationMatrix(float angle, Matrix4f dest) {	
		// clear the matrix to the rotation matrix 
		dest.identity();
		//
		
		// Set rotation components for Z-axis rotation
	    // For a Z-axis rotation in a right-handed coordinate system:
	    // [ cos(θ)  -sin(θ)  0  0 ]
	    // [ sin(θ)   cos(θ)  0  0 ]
	    // [   0        0     1  0 ]
	    // [   0        0     0  1 ]
		
		dest.m00((float)Math.cos(angle));
	    dest.m10((float)Math.sin(angle) * -1);
	    dest.m01((float)Math.sin(angle));
	    dest.m11((float)Math.cos(angle));
		

		return dest;
	}

	/**
	 * Set the destination matrix to a scale matrix. Note the destination matrix
	 * must already be allocated.
	 *
	 * @param sx   Scale factor in x direction
	 * @param sy   Scale factor in y direction
	 * @param dest Destination matrix to write into
	 * @return
	 */

	public static Matrix4f scaleMatrix(float sx, float sy, Matrix4f dest) {
		// clear the matrix to the rotation matrix 
		dest.identity();
		
		// S(Sx, Sy) = 
		// [ Sx	0	0	0]
		// [ 0	Sy	0	0]
		// [ 0	0	1	0]
		// [ 0	0	0	1]
		
		dest.m00(sx);
		dest.m11(sy);
		
		return dest;
	}


}
