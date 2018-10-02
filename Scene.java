package renderer;

import java.awt.Color;
import java.util.List;

/**
 * The Scene class is where we store data about a 3D model and light source
 * inside our renderer. It also contains a static inner class that represents one
 * single polygon.
 * 
 * Method stubs have been provided, but you'll need to fill them in.
 * 
 * If you were to implement more fancy rendering, e.g. Phong shading, you'd want
 * to store more information in this class.
 */
public class Scene {

	private List<Polygon> polygons;
	private Vector3D lightPos;
	private boundaryBox boundaryBox;
	private float leftX, rightX, botY, topY;

	/**
	 * Scene of the polygon 
	 * @param polygons
	 * @param lightPos
	 */
	public Scene(List<Polygon> polygons, Vector3D lightPos) {
		this.polygons = polygons;
		this.lightPos = lightPos;
	}

	/**
	 * Returns Light Position 
	 * @return
	 */
	public Vector3D getLight() {
		return lightPos;
	}

	/**
	 * Returns polygons 
	 * @return
	 */
	public List<Polygon> getPolygons() {
		return polygons;
	}

	/**
	 * Gets the vertices values to compute boundary box area to get optimum size 
	 */
	public void boundaryBox(){
		leftX =  Float.POSITIVE_INFINITY;
		rightX =  Float.NEGATIVE_INFINITY;
		botY =  Float.POSITIVE_INFINITY;
		topY =  Float.NEGATIVE_INFINITY;
		for(Polygon polygon: polygons) {
			for(Vector3D v : polygon.getVertices()) {
				if(v.y < botY)
					botY = v.y;	
				else if(v.x > rightX)
					rightX = v.x;
				else if(v.x < leftX)
					leftX = v.x;
				else if(v.y > topY)
					topY = v.y;	
			}
		}
		this.boundaryBox = new boundaryBox(leftX, rightX, botY, topY);
	}

	/**
	 * Returns boundary box 
	 * @return
	 */
	public boundaryBox getboundaryBox(){
		return boundaryBox;
	}

	/**
	 * Polygon stores data about a single polygon in a scene, keeping track of
	 * (at least!) its three vertices and its reflectance.
	 *
	 * This class has been done for you.
	 */
	public static class Polygon {
		Vector3D[] vertices;
		Color reflectance;

		/**
		 * @param points
		 *            An array of floats with 9 elements, corresponding to the
		 *            (x,y,z) coordinates of the three vertices that make up
		 *            this polygon. If the three vertices are A, B, C then the
		 *            array should be [A_x, A_y, A_z, B_x, B_y, B_z, C_x, C_y,
		 *            C_z].
		 * @param color
		 *            An array of three ints corresponding to the RGB values of
		 *            the polygon, i.e. [r, g, b] where all values are between 0
		 *            and 255.
		 */
		public Polygon(float[] points, int[] color) {
			this.vertices = new Vector3D[3];

			float x, y, z;
			for (int i = 0; i < 3; i++) {
				x = points[i * 3];
				y = points[i * 3 + 1];
				z = points[i * 3 + 2];
				this.vertices[i] = new Vector3D(x, y, z);
			}

			int r = color[0];
			int g = color[1];
			int b = color[2];
			this.reflectance = new Color(r, g, b);
		}

		/**
		 * An alternative constructor that directly takes three Vector3D objects
		 * and a Color object.
		 */
		public Polygon(Vector3D a, Vector3D b, Vector3D c, Color color) {
			this.vertices = new Vector3D[] { a, b, c };
			this.reflectance = color;
		}
		public Vector3D getNormal(){
			Vector3D vec0_1 = vertices[1].minus(vertices[0]);
			Vector3D vec1_2 = vertices[2].minus(vertices[1]);
			return vec0_1.crossProduct(vec1_2);
		}

		public Vector3D[] getVertices() {
			return vertices;
		}

		public Color getReflectance() {
			return reflectance;
		}

		@Override
		public String toString() {
			String str = "polygon:";

			for (Vector3D p : vertices)
				str += "\n  " + p.toString();

			str += "\n  " + reflectance.toString();

			return str;
		}
	}
	public class boundaryBox {

		public float leftX, rightX, botY, topY;

		/**
		 * Limits the polygon to the area of the boundary box 
		 * @param leftX
		 * @param rightX
		 * @param botY
		 * @param topY
		 */
		public boundaryBox(float leftX, float rightX, float botY, float topY){
			this.leftX = leftX;
			this.rightX = rightX;
			this.botY = botY;
			this.topY = topY;			

		}

		/**
		 * Returns Scale ratio 
		 * @return
		 */
		public float getScale(){			
			return (GUI.CANVAS_WIDTH/3f) / Math.round(rightX - leftX);

		}

		/**
		 * Returns X Pos of box 
		 * @return
		 */
		public int getX(){
			float midX = leftX + Math.round(rightX - leftX)/2;
			return (int) Math.round((GUI.CANVAS_WIDTH / 2) - midX);	
		}

		/**
		 * Returns Y Pos of box 
		 * @return
		 */
		public int getY(){
			float midY = botY + Math.round(topY - botY)/2;
			return (int) Math.round((GUI.CANVAS_HEIGHT / 2) - midY);	
		}


	}

}

// code for COMP261 assignments
