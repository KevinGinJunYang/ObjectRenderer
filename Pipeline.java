package renderer;

import java.awt.Color;
import java.util.List;

import renderer.Scene.Polygon;

/**
 * The Pipeline class has method stubs for all the major components of the
 * rendering pipeline, for you to fill in.
 * 
 * Some of these methods can get quite long, in which case you should strongly
 * consider moving them out into their own file. You'll need to update the
 * imports in the test suite if you do.
 */
public class Pipeline {

	/**
	 * Returns true if the given polygon is facing away from the camera (and so
	 * should be hidden), and false otherwise.
	 */
	public static boolean isHidden(Polygon poly) {
		Vector3D[] vertices = poly.getVertices();
		Vector3D x = vertices[0];
		Vector3D y = vertices[1];
		Vector3D z = vertices[2];

		return (y.x - x.x) * (z.y - y.y) > (y.y - x.y) * (z.x - y.x);

	}

	/**
	 * Computes the colour of a polygon on the screen, once the lights, their
	 * angles relative to the polygon's face, and the reflectance of the polygon
	 * have been accounted for.
	 * 
	 * @param lightDirection
	 *            The Vector3D pointing to the directional light read in from
	 *            the file.
	 * @param lightColor
	 *            The color of that directional light.
	 * @param ambientLight
	 *            The ambient light in the scene, i.e. light that doesn't depend
	 *            on the direction.
	 */
	public static Color getShading(Polygon poly, Vector3D lightDirection, Color lightColor, Color ambientLight) {
		Vector3D[] vertices = poly.getVertices();
		Vector3D a1 = vertices[1].minus(vertices[0]);
		Vector3D b2 = vertices[2].minus(vertices[1]);
		Vector3D normal = a1.crossProduct(b2);

		double cosAngle = normal.cosTheta(lightDirection);

		int r = ambientLight.getRed();
		int g = ambientLight.getGreen();
		int b = ambientLight.getBlue();

		// check angle of light 
		if (cosAngle > 0) {
			r += lightColor.getRed() * cosAngle;
			g += lightColor.getGreen() * cosAngle;
			b += lightColor.getBlue() * cosAngle;
		}

		g *= poly.reflectance.getGreen() / 255.0f;
		b *= poly.reflectance.getBlue() / 255.0f;
		r *= poly.reflectance.getRed() / 255.0f;


		//restrict values 
		if (r > 255) {
			r = 255;
		}
		if (g > 255) {
			g = 255;
		}
		if (b > 255) {
			b = 255;
		}
		if (r < 0) {
			r = 0;
		}
		if (g < 0) {
			g = 0;
		}
		if (b < 0) {
			b = 0;
		}

		return new Color(r, g, b);


	}

	/**
	 * This method should rotate the polygons and light such that the viewer is
	 * looking down the Z-axis. The idea is that it returns an entirely new
	 * Scene object, filled with new Polygons, that have been rotated.
	 * 
	 * @param scene
	 *            The original Scene.
	 * @param xRot
	 *            An angle describing the viewer's rotation in the YZ-plane (i.e
	 *            around the X-axis).
	 * @param yRot
	 *            An angle describing the viewer's rotation in the XZ-plane (i.e
	 *            around the Y-axis).
	 * @return A new Scene where all the polygons and the light source have been
	 *         rotated accordingly.
	 */
	public static Scene rotateScene(Scene scene, float xRot, float yRot) {
		List<Polygon> poly = scene.getPolygons();
		//rotate
		Transform xRotation = Transform.newXRotation(xRot);
		Transform yRotation = Transform.newYRotation(yRot);
		Transform rotation = xRotation.compose(yRotation);
		//transform
		for(Polygon polygon : poly){			
			for(int i = 0; i < polygon.getVertices().length; i++){
				polygon.getVertices()[i] = rotation.multiply(polygon.getVertices()[i]);;
			}
		}
		return new Scene(poly, scene.getLight());
	}

	/**
	 * This should translate the scene by the appropriate amount.
	 * 
	 * @param scene
	 * @return
	 */
	public static Scene translateScene(Scene scene) {
		List<Polygon> poly = scene.getPolygons();
		//translate
		Transform translate = Transform.newTranslation(scene.getboundaryBox().getX(), scene.getboundaryBox().getY(), 0);
		//transform
		for(Polygon polygon : poly){			
			for(int i = 0; i < polygon.getVertices().length; i++){
				polygon.getVertices()[i] = translate.multiply(polygon.getVertices()[i]);
			}
		}
		return new Scene(poly, scene.getLight());
	}

	/**
	 * This should scale the scene.
	 * 
	 * @param scene
	 * @return
	 */
	public static Scene scaleScene(Scene scene) {
		List<Polygon> poly = scene.getPolygons();
		//scale
		float scaleRatio = scene.getboundaryBox().getScale();
		Transform scale = Transform.newScale(scaleRatio, scaleRatio, scaleRatio);
		//transform
		for(Polygon polygon : poly){
			for(int i = 0; i < polygon.getVertices().length; i++){
				polygon.getVertices()[i] = scale.multiply(polygon.getVertices()[i]);
			}
		}
		return new Scene(poly, scene.getLight());
	}

	/**
	 * Computes the edgelist of a single provided polygon, as per the lecture
	 * slides.
	 */
	public static EdgeList computeEdgeList(Polygon poly) {
		int startY = (int) Math.min(Math.min(poly.vertices[0].y, poly.vertices[1].y), poly.vertices[2].y);
		int endY = (int) Math.max(Math.max(poly.vertices[0].y, poly.vertices[1].y), poly.vertices[2].y);

		EdgeList edgeList = new EdgeList(startY, endY);
		Vector3D[] vertices = poly.vertices;

		for (int i = 0; i < vertices.length; i++) {
			Vector3D up = vertices[i];
			Vector3D down = vertices[(i + 1) % 3]; // checks index for boundary validation 

			// checks both up and down by doing this 
			if (up.y > down.y) {
				up = down;
				down = vertices[i];
			}

			float slopeX = (down.x - up.x) / (down.y - up.y);
			float slopeZ = (down.z - up.z) / (down.y - up.y);

			float x = up.x;
			float z = up.z;
			int y = (int) up.y;
			int yEnd = (int) down.y;

			while(y < yEnd) {
				edgeList.addRow(y - startY, x, z);
				y++; x += slopeX; z += slopeZ;
			}
		}

		return edgeList;
	}

	/**
	 * Fills a zbuffer with the contents of a single edge list according to the
	 * lecture slides.
	 * 
	 * The idea here is to make zbuffer and zdepth arrays in your main loop, and
	 * pass them into the method to be modified.
	 * 
	 * @param zbuffer
	 *            A double array of colours representing the Color at each pixel
	 *            so far.
	 * @param zdepth
	 *            A double array of floats storing the z-value of each pixel
	 *            that has been coloured in so far.
	 * @param polyEdgeList
	 *            The edgelist of the polygon to add into the zbuffer.
	 * @param polyColor
	 *            The colour of the polygon to add into the zbuffer.
	 */
	public static void computeZBuffer(Color[][] zbuffer, float[][] zdepth, EdgeList polyEdgeList, Color polyColor) {
		int dist = polyEdgeList.getEndY() - polyEdgeList.getStartY();  

		for (int y = 0; y < dist; y++) {

			int leftX = (int) polyEdgeList.getLeftX(y);
			float leftZ = polyEdgeList.getLeftZ(y);
			int rightX = (int) polyEdgeList.getRightX(y);        
			float rightZ = polyEdgeList.getRightZ(y);
			float slope = (rightZ - leftZ) / (rightX - leftX);

			while (leftX < rightX) {
				if (leftX < 0 || leftX >= zbuffer.length) {
					leftZ += slope;
					leftX++;
					continue;
				}

				if (leftX < zdepth.length && y < zdepth[0].length && leftZ < zdepth[leftX][y]) {
					zdepth[leftX][y + polyEdgeList.getStartY()] = leftZ;
					zbuffer[leftX][y + polyEdgeList.getStartY()] = polyColor;
				}
				leftZ += slope;
				leftX++;
			}

		}

	}
}

// code for comp261 assignments
