package comp3170.week3;

public class Scene {
	
	private Plane plane;
	
	public Scene() {
		
		plane = new Plane();
	}
	
	public void init() {
	}
	
	public void update(float deltaTime) {
		plane.update(deltaTime);
	}
	
	public void draw() {
		plane.draw();
	}
		

}
