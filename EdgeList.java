package renderer;

/**
 * EdgeList should store the data for the edge list of a single polygon in your
 * scene. A few method stubs have been provided so that it can be tested, but
 * you'll need to fill in all the details.
 *
 * You'll probably want to add some setters as well as getters or, for example,
 * an addRow(y, xLeft, xRight, zLeft, zRight) method.
 */
public class EdgeList {
	private float[] leftX, rightX, leftZ, rightZ;
	private int startY, endY;
	private int edge;

	/**
	 * Gets the edgeList of the polygon 
	 * @param startY
	 * @param endY
	 */
	public EdgeList(int startY, int endY) {
		this.startY = startY;
		this.endY = endY;
		this.edge = endY - startY;
		
		//init for edge pos
		leftX = new float[edge];
        rightX = new float[edge];
        leftZ = new float[edge];
        rightZ = new float[edge];
        
        // init edges so it doesnt get get overwritten for addrow
        for (int i = 0; i < edge; i++) {
            leftX[i] = Float.POSITIVE_INFINITY;
            rightX[i] = Float.NEGATIVE_INFINITY; 
            leftZ[i] = Float.POSITIVE_INFINITY;
            rightZ[i] = Float.POSITIVE_INFINITY;
        }
        
        
        
	}
	/**
	 * Returns start Y
	 * @return
	 */
	public int getStartY() {
		return startY;
	}
	
	/**
	 * Returns end Y 
	 * @return
	 */
	public int getEndY() {
		return endY;
	}
	
	/**
	 * Gets Left X edge 
	 * @param y
	 * @return
	 */
	public float getLeftX(int y) {
		return leftX[y];
	}
	
	/**
	 * Gets Right X edge 
	 * @param y
	 * @return
	 */
	public float getRightX(int y) {
		return rightX[y];
	}
	
	/**
	 * gets Left Z edge 
	 * @param y
	 * @return
	 */
	public float getLeftZ(int y) {
		return leftZ[y];
	}
	
	/**
	 * Gets Right Z edge; 
	 * @param y
	 * @return
	 */
	public float getRightZ(int y) {
		return rightZ[y];
	}
	
	/**
	 * Adds the row to edgeList if value can be changed 
	 * @param y
	 * @param x
	 * @param z
	 */
	public void addRow(int y, float x, float z) {
        
		if (x >= this.rightX[y]) {
            this.rightX[y] = x;
            this.rightZ[y] = z;
        }
		
        if (x <= this.leftX[y]) {
            this.leftX[y] = x;
            this.leftZ[y] = z;
        }

        
	}
}

// code for comp261 assignments
