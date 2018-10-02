package renderer;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import renderer.Scene.Polygon;

public class Renderer extends GUI {

	private Vector3D lightCoord;
	private List<Polygon> polygon = new ArrayList<>();
	private Scene scene;
	private float rotateDistance = 0.2f;


	@Override
	protected void onLoad(File file) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line = reader.readLine();
			String[] points = line.split(" ");
			float x = Float.parseFloat(points[0]);
			float y = Float.parseFloat(points[1]);
			float z = Float.parseFloat(points[2]);
			this.lightCoord = new Vector3D(x, y, z);

			while ((line = reader.readLine()) != null) {
				points = line.split(" ");
				float x1 = Float.parseFloat(points[0]);
				float x2 = Float.parseFloat(points[1]);
				float x3 = Float.parseFloat(points[2]);
				float y1 = Float.parseFloat(points[3]);
				float y2 = Float.parseFloat(points[4]);
				float y3 = Float.parseFloat(points[5]);
				float z1 = Float.parseFloat(points[6]);
				float z2 = Float.parseFloat(points[7]);
				float z3 = Float.parseFloat(points[8]);

				Vector3D xCoords = new Vector3D(x1,x2,x3);
				Vector3D yCoords = new Vector3D(y1,y2,y3);
				Vector3D zCoords = new Vector3D(z1,z2,z3);

				int r = Integer.parseInt(points[9]);
				int g = Integer.parseInt(points[10]);
				int b = Integer.parseInt(points[11]);

				Color color = new Color(r,g,b);
				// new polygon 
				Polygon poly = new Polygon(xCoords,yCoords,zCoords,color);
				polygon.add(poly);

			}
			//add to current scene 
			this.scene = new Scene(polygon, lightCoord);
			reader.close();
			scaleImage();
			translateImage();
			
		}catch(IOException e ) {
			e.printStackTrace();
		}

	}

	@Override
	protected void onKeyPress(KeyEvent ev) {
		if (ev.getKeyCode() == KeyEvent.VK_LEFT	|| Character.toUpperCase(ev.getKeyChar()) == 'A'){
			rotateImageLeft();
			translateImage();
		}
		else if (ev.getKeyCode() == KeyEvent.VK_RIGHT || Character.toUpperCase(ev.getKeyChar()) == 'D'){
			rotateImageRight();
			translateImage();
		}
		else if (ev.getKeyCode() == KeyEvent.VK_UP || Character.toUpperCase(ev.getKeyChar()) == 'W'){
			rotateImageUp();
			translateImage();
		}
		else if(ev.getKeyCode() == KeyEvent.VK_DOWN || Character.toUpperCase(ev.getKeyChar()) == 'S'){
			rotateImageDown();
			translateImage();
		}
	}

	@Override
	protected BufferedImage render() {

		// check scene 
		if (scene == null) {
			return null;
		}

		float[][] zdepth = new float[CANVAS_WIDTH][CANVAS_HEIGHT];
		Color[][] zbuff = new Color[CANVAS_WIDTH][CANVAS_HEIGHT];


		//Set background 
		Color background = new Color(255, 255, 255);
		for (int i = 0; i < zbuff.length; i++) {
			for (int j = 0; j < zbuff[i].length; j++) {
				zbuff[i][j] = background;
			}
		}	    

		//init zbuff
		for (int i = 0; i < zdepth.length; i++) {
			for (int j = 0; j < zdepth[i].length; j++) {
				zdepth[i][j] = Integer.MAX_VALUE;
			}
		}

		// update polygon upon changes 
		Color lightLevel = new Color(200,200,200); // light intensity 
		Color ambientLight = new Color(getAmbientLight()[0], getAmbientLight()[1], getAmbientLight()[2]);
		Vector3D lightVector = scene.getLight();
		List<Polygon> polygons = scene.getPolygons();
		for (Polygon polygon : polygons) {
			if (Pipeline.isHidden(polygon)) {
				continue;
			}
			Color shade = Pipeline.getShading(polygon, lightVector, lightLevel, ambientLight);
			EdgeList edgeList = Pipeline.computeEdgeList(polygon);
			Pipeline.computeZBuffer(zbuff, zdepth, edgeList, shade);
		}

		return convertBitmapToImage(zbuff);
	}

	/**
	 * Converts a 2D array of Colors to a BufferedImage. Assumes that bitmap is
	 * indexed by column then row and has imageHeight rows and imageWidth
	 * columns. Note that image.setRGB requires x (col) and y (row) are given in
	 * that order.
	 */
	private BufferedImage convertBitmapToImage(Color[][] bitmap) {
		BufferedImage image = new BufferedImage(CANVAS_WIDTH, CANVAS_HEIGHT, BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < CANVAS_WIDTH; x++) {
			for (int y = 0; y < CANVAS_HEIGHT; y++) {
				image.setRGB(x, y, bitmap[x][y].getRGB());
			}
		}
		return image;
	}

	public static void main(String[] args) {
		new Renderer();
	}

	public void translateImage() {
		scene.boundaryBox();
		Pipeline.translateScene(scene);
	}

	public void rotateImageLeft() {
		scene.boundaryBox();
		Pipeline.rotateScene(scene, 0, rotateDistance);
	}
	
	public void rotateImageRight() {
		scene.boundaryBox();
		Pipeline.rotateScene(scene, 0, -rotateDistance);
	}
	
	public void rotateImageUp() {
		scene.boundaryBox();
		Pipeline.rotateScene(scene, -rotateDistance, 0);
	}
	
	public void rotateImageDown() {
		scene.boundaryBox();
		Pipeline.rotateScene(scene, rotateDistance, 0);
	}

	public void scaleImage() {
		scene.boundaryBox();
		Pipeline.scaleScene(scene);
	}


}

// code for comp261 assignments
