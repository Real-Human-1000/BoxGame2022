import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.VolatileImage;

import javax.swing.ImageIcon;

public class Cell
{
	// ----- static variables.... These belong to the class as a whole; all Cells have access to these individual variables.
	public static final int CELL_SIZE = 8;
	private static Font cellFont = new Font("Times New Roman",Font.BOLD,CELL_SIZE*3/4);
	private static Image[] colorImages; // these will be filled with the images in the following files.
	private static Image[][] scaledColorImages;
	private static String[] filenames = {"graphics.png", "GreenChip.png", "PurpleChip.png", "RedChip.png", "YellowChip.png"};
	private static String[] cellColors = {"Blue","Green","Purple","Red","Yellow"};
	Color color = Color.BLACK;
	
	private int colorID; // which background color should be displayed?
	private int x,y; // screen coordinates of the top left corner
	private String marker; // optional character (typically a letter or number) to show on this cell
	private boolean displayMarker; // whether to show the cell label or not.
	private boolean isLive; // whether the cell should appear at all.

	private CellFlipManager flipThread;
	private boolean colorChanged = false;
	GraphicsConfiguration myGC = null;

	private VolatileImage myDrip; //the cell's current appearance, buffered for performance
	private double millisSinceLastFlip = 0;//keeps cells from flipping too often.

	int lastTileStatus = 0;



	//=====================  CONSTRUCTORS =============================
	public Cell()
	{
		colorID = 1;
		isLive = true;
		// The following is a sneaky trick I am using to initialize a static variable - the first constructor that 
		// gets to it when it hasn't yet been defined will be the one to load up these variables. All of the cell
		// instances will share the same five pictures! This way, we can have hundreds of cells, but they don't all
		// have to load the images.
		marker = "";
		displayMarker = false;
		if (colorImages == null)
		{
			colorImages = new Image[filenames.length];
			for (int i =0; i<filenames.length; i++) {
				colorImages[i] = (new ImageIcon(filenames[i])).getImage();


				/*
				BufferedImage bimage = new BufferedImage(CELL_SIZE,
						CELL_SIZE, BufferedImage.TYPE_INT_RGB);

				// Copy non-RGB image to the RGB buffered image
				Graphics2D g = bimage.createGraphics();
				g.drawImage(colorImages[i], 0, 0,CELL_SIZE,CELL_SIZE,null);
				g.dispose();

				RescaleOp op = new RescaleOp(0.3f, 0, null);
				bimage = op.filter(bimage,null);

				colorImages[i] = bimage;*/

			}

			scaledColorImages = new Image[10][filenames.length];
			for (int i = 0; i < filenames.length; i++) {
				for (int j = 0; j < 10; j++) {
					try {
						scaledColorImages[(j)][i] = colorImages[i].getScaledInstance(
								CELL_SIZE,
								(int) (CELL_SIZE * Math.sin(j * Math.PI / 18)), Image.SCALE_DEFAULT);


//						scaledColorImages[(j)][i] = colorImages[i].getScaledInstance(
//								colorImages[i].getWidth(null),
//								(int) (colorImages[i].getHeight(null) * Math.sin(j * Math.PI / 18)), Image.SCALE_DEFAULT);
					}catch (IllegalArgumentException e){
						scaledColorImages[j][i] = colorImages[i];
					}
				}
			}
		}
	}
	
	public Cell(int cid)
	{
		this();
		colorID = cid;
	}
	
	public Cell(int inRow, int inCol)
	{
		this((int)(Math.random()*filenames.length));
		y = inRow*CELL_SIZE;
		x = inCol*CELL_SIZE;

	}
	
	public Cell(int cid, int inRow, int inCol, String inMarker, boolean disp)
	{
		this(inRow,inCol);
		colorID = cid;
		marker = inMarker;
		displayMarker = disp;

	}
	//=====================  ACCESSORS/MODIFIERS =============================
	public int getColorID()
	{
		return colorID;
	}

	public void setColorID(Color color) {
		if(getGreatestColorDifference(color,this.color)>10){//getColorDifferenceSQ(color,this.color)>2500){
			if (colorChanged == false) {
				flipThread = new CellFlipManager(this, this.color);
				colorChanged = true;

			}
			millisSinceLastFlip = 0;
			this.color = color;
			//if(!performanceMode)
			//createDrip();
		}
		//this.colorID = colorID;
	}

	public Color getMyColor() {
		return color;
	}

	public int getX()
	{
		return x;
	}

	public Color getColor() {
		return color;
	}

	public void setX(int x)
	{
		this.x = x;
	}

	public int getY()
	{
		return y;
	}

	public void setY(int y)
	{
		this.y = y;
	}

	public void setMyGC(GraphicsConfiguration myGC) {
		this.myGC = myGC;
	}

	public String getMarker()
	{
		return marker;
	}

	public void setMarker(String marker)
	{
		this.marker = marker;
	}

	public boolean shouldDisplayMarker()
	{
		return displayMarker;
	}

	public void setDisplayMarker(boolean displayMarker)
	{
		this.displayMarker = displayMarker;
	}

	public Image getMyImage(){return colorImages[colorID];}

	public Image getMyScaledImage(int index){return scaledColorImages[index][colorID];}

	public Image getImageAtIndex(int i){return colorImages[i];}

	public Image getScaledImageAtIndex(int i, int scaleIndex){return scaledColorImages[scaleIndex][i];}

	public void setColorChanged(boolean colorChanged) {
		this.colorChanged = colorChanged;
	}

	public boolean isColorChanged() {
		return colorChanged;
	}


	public boolean isLive()
	{
		return isLive;
	}
	
	public void setIsLive(boolean b)
	{
		isLive = b;
	}
	// =============================   DRAW SELF ================================
	public void createDrip(){
		myDrip = myGC.createCompatibleVolatileImage(CELL_SIZE,CELL_SIZE);
		restoreDrip();
	}

	private void restoreDrip(){
		do{
			if(myDrip.validate(myGC)==VolatileImage.IMAGE_INCOMPATIBLE){
				myDrip = myGC.createCompatibleVolatileImage(CELL_SIZE,CELL_SIZE);
			}
			Graphics2D G = myDrip.createGraphics();
			G.setColor(Color.BLACK);
			G.fillRect(0,0,CELL_SIZE,CELL_SIZE);
			G.setColor(color);
			G.fillRoundRect(0, 0, CELL_SIZE - 1, CELL_SIZE - 1, 1, 1);
			Shape scg = new ShadedCellGraphics(CELL_SIZE, CELL_SIZE);
			G.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			G.setColor(color.brighter());
			G.draw(scg);
			G.setColor(color.brighter().darker());
			G.transform(AffineTransform.getRotateInstance(Math.toRadians(180),
				((double) CELL_SIZE - 1) / 2.0, ((double) CELL_SIZE - 1) / 2.0));
			G.draw(scg);

			G.dispose();
		}while (myDrip.contentsLost());
	}

	private void drawMyDrip(Graphics g){
		boolean dripRestored = false;
		int attemptsToRestore = 2;
		if(myDrip == null)
			myDrip = myGC.createCompatibleVolatileImage(CELL_SIZE,CELL_SIZE);
		do {
			int returnCode = myDrip.validate(myGC);
			if (returnCode == VolatileImage.IMAGE_RESTORED) {
				// Contents need to be restored
				restoreDrip();// restore contents
				dripRestored = true;
				attemptsToRestore+=1;
			} else if (returnCode == VolatileImage.IMAGE_INCOMPATIBLE) {
				// old vImg doesn't work with new GraphicsConfig; re-create it
				myDrip = myGC.createCompatibleVolatileImage(CELL_SIZE,CELL_SIZE);
				restoreDrip();
				dripRestored = true;
				attemptsToRestore+=1;
			}
			if(dripRestored){
				g.setColor(color);
				g.fillRoundRect(x,y,CELL_SIZE,CELL_SIZE,2,2);}
			else
				g.drawImage(myDrip, x, y, null);
		} while (myDrip.contentsLost()&&attemptsToRestore<30);
	}


	public void drawSelf(Graphics g,double deltaTime,boolean performanceMode)
	{
		if (!isLive)
			return;
		Graphics2D g2 = (Graphics2D)g.create();
		millisSinceLastFlip+= deltaTime;
		if(colorChanged){
			flipThread.setG(g);
			flipThread.updateAnim(deltaTime,performanceMode);
			//colorChanged = false;
		}else{
				flipThread = null;
				//g2.drawImage(colorImages[colorID], x, y, CELL_SIZE, CELL_SIZE, null);
				//g2.drawImage(colorImages[colorID].getScaledInstance(12,12,2), x, y, CELL_SIZE - 2, CELL_SIZE - 2, null);


				g2.setColor(color);
				//g2.fillRect(x,y,CELL_SIZE,CELL_SIZE);


				if (performanceMode == false) {
					//g2.drawImage(myDrip,x,y,CELL_SIZE,CELL_SIZE,null);
					drawMyDrip(g2);
//					AffineTransform graphicsTransform = g2.getTransform();
//					g2.fillRoundRect(x, y, CELL_SIZE - 1, CELL_SIZE - 1, 1, 1);
//					g2.translate(x, y);
//					Shape scg = new ShadedCellGraphics(CELL_SIZE, CELL_SIZE);
//					g2.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
//					g2.setColor(color.brighter());
//					g2.draw(scg);
//					g2.setColor(color.brighter().darker());
//					g2.transform(AffineTransform.getRotateInstance(Math.toRadians(180),
//							((double) CELL_SIZE - 1) / 2.0, ((double) CELL_SIZE - 1) / 2.0));
//					//scg = new ShadedCellGraphics(CELL_SIZE,CELL_SIZE,true);
//					g2.draw(scg);
//					//g2.setTransform(graphicsTransform);
				} else {
					g2.fillRect(x, y, CELL_SIZE, CELL_SIZE);
					// System.out.println(deltaTime);
				}

//			g2.setColor(new Color(52,180,235));
//			g2.setStroke(new BasicStroke(2,BasicStroke.CAP_ROUND,BasicStroke.JOIN_BEVEL));
//			g2.fillRoundRect(x+1, y+1, CELL_SIZE-1, CELL_SIZE-1, 1, 1);
//
//			g2.setColor(new Color(52,180,235));
//			g2.setStroke(new BasicStroke(2,BasicStroke.CAP_ROUND,BasicStroke.JOIN_BEVEL));
//			g2.drawRoundRect(x+1, y+1, CELL_SIZE-2, CELL_SIZE-2, 1, 1);

//			g2.setColor(mixColor(new Color(52,180,235),Color.BLACK,0.5));
//			g2.setStroke(new BasicStroke(2,BasicStroke.CAP_ROUND,BasicStroke.JOIN_BEVEL));
//			g2.drawRoundRect(x , y, CELL_SIZE - 0, CELL_SIZE - 0, 3, 3);


			//g2.setColor(new Color(192, 192, 192));
			//g2.setStroke(new BasicStroke(3));
			//g2.drawRoundRect(x + 1, y + 1, CELL_SIZE - 4, CELL_SIZE - 4, 8, 8);

			//g2.setColor(new Color(64, 64, 64));
			//g2.setStroke(new BasicStroke(2));
			//g2.drawRoundRect(x + 1, y + 1, CELL_SIZE - 4, CELL_SIZE - 4, 8, 8);
		}


		if (displayMarker)
		{
			g2.setFont(cellFont);
			g2.setColor(Color.WHITE);
			g2.drawString(marker, x+CELL_SIZE/2-6, y+CELL_SIZE/2+7);  //You'll likely want to tinker with these numbers.
			   
			g2.setColor(Color.BLACK);
			g2.drawString(marker, x+CELL_SIZE/2-7, y+CELL_SIZE/2+6);
		}
		g2.dispose();
	}

	public void drawDebug(Graphics g, Color waterCol, Color terrainCol, double vx, double vy){
		if (!isLive)
			return;
		Graphics2D g2 = (Graphics2D) g;

		g2.setColor(color);
		g2.fillRect(x,y,CELL_SIZE/4,CELL_SIZE/4);
		g2.setColor(waterCol);
		g2.fillRect(x,y+CELL_SIZE/4,CELL_SIZE/4,CELL_SIZE/4);
		g2.setColor(terrainCol);
		g2.fillRect(x+CELL_SIZE/4,y,CELL_SIZE/4,CELL_SIZE/4);
		g2.setColor(Color.red);
		double w = Math.sqrt(vx*vx+vy*vy);
		g2.drawLine(x,y,x+2*(int)(vx/w),y+2*(int)(vy/w));

	}



// ===================================  OVERRIDDEN OBJECT METHODS ==============================
	public boolean equals(Object other)
	{
		if (other instanceof Cell)
			if ((((Cell) other).colorID == this.colorID) && 
			   (((Cell) other).marker   == this.marker))
			return true;
		return false;
	}

	public Color mixColor(Color startColor, Color modifier, double percent){
		int r = (int)(startColor.getRed()*(1.0-percent) + modifier.getRed()*(percent));
		int g = (int)(startColor.getGreen()*(1.0-percent) + modifier.getGreen()*(percent));
		int b = (int)(startColor.getBlue()*(1.0-percent) + modifier.getBlue()*(percent));

		Color output = new Color(r,g,b);
		return output;
	}

	public String toString()
	{
		return "Cell: "+marker+": color:"+cellColors[colorID];
	}
	
	// a good habit for us to get into, but we probably won't need this for the current project.
	public int hashCode()
	{
		int result = colorID * 137;
		if (marker!=null) result += marker.hashCode();
		return result;
	}

	//used in the color change
	public int getColorDifferenceSQ(Color c1, Color c2){
		return (int) (Math.pow(c1.getRed()-c2.getRed(),2)+
						Math.pow(c1.getBlue()-c2.getBlue(),2)+
						Math.pow(c1.getGreen()-c2.getGreen(),2));
	}
	public int getGreatestColorDifference(Color c1, Color c2){
		int greatestDiff = Math.abs(c1.getBlue()-c2.getBlue());
		greatestDiff = Math.max(Math.abs(c1.getRed()-c2.getRed()),greatestDiff);
		greatestDiff = Math.max(Math.abs(c1.getGreen()-c2.getGreen()),greatestDiff);
		return greatestDiff;
	}

}
