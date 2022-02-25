import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class GridDemoPanel extends JPanel implements MouseListener, KeyListener
{
	private Cell[][] theGrid;
	private double[][] terrainMap;
	public final static int NUM_ROWS = 70;
	public final static int NUM_COLS = 70;
	public GridDemoFrame myParent;
	public TerrainController terrainController;
	public int score;
	public double deltaTime = 0;
	public boolean performanceMode;
	private int palette = 0;
	// 0 = direct, 1 = classic, 2 = meat,
	// 3 = binary sediment, 4 = binary terrain, 5 = binary water
	// 6 = coast, 7 = slope
	
	public GridDemoPanel(GridDemoFrame parent)
	{
		super();
		resetCells();
		setBackground(Color.BLACK);
		addMouseListener(this);
		//parent.addKeyListener(this); // activate this if you wish to listen to the keyboard. 
		myParent = parent;
		terrainController = new TerrainController(NUM_ROWS,NUM_COLS);
		terrainMap = terrainController.getTerrain();
	}
	
	/**
	 * makes a new board with random colors, completely filled in, and resets the score to zero.
	 */
	public void resetCells()
	{
		theGrid = new Cell[NUM_ROWS][NUM_COLS];
		for (int r =0; r<NUM_ROWS; r++)
			for (int c=0; c<NUM_COLS; c++){
				theGrid[r][c] = new Cell(r,c);
				theGrid[r][c].setMyGC(getGraphicsConfiguration());}
		score = 0;
	}

	public int capColor(double in) {
		return (int)Math.max(0, Math.min(in, 255));
	}
	
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);

		// Loop through every cell and set its color
		for (int r =0; r<NUM_ROWS; r++)
			for (int c=0; c<NUM_COLS; c++) {
				theGrid[r][c].setMyGC(getGraphicsConfiguration());

				if (palette == 0) {
					// Direct - directly maps sediment to red, terrain to green, and water to blue
					theGrid[r][c].setColorID(new Color (
							capColor(terrainController.getFluidEarthAt(c, r) * 255),
							capColor(terrainMap[r][c] * 255),
							capColor(terrainController.getFluidAt(c, r) * 255 * 10)
					));
				}

				if (palette == 1) {
					// Mud - the color palette I (H) designed to show nice water and mud colors
					if (terrainMap[r][c] >= terrainController.getSeaLevel()) {
						theGrid[r][c].setColorID(new Color(64, capColor(255 * terrainMap[r][c]), 64));

					} else {
						if (terrainController.getFluidAt(c, r) > 0) {
							double earth = terrainController.getFluidEarthAt(c, r);
							double fluid = terrainController.getFluidAt(c, r);

							Color mudColor = new Color(capColor(earth * 140 + 60 - fluid * (earth * 50 + 64)),
									capColor(160 * (1 - fluid) - 0.6 * 160 * (1 - fluid) * Math.pow(earth - 0.9, 2)),
									capColor(220 - earth * 140 - fluid * (140 - earth * 76)));
							theGrid[r][c].setColorID(mudColor);

						} else {
							theGrid[r][c].setColorID(new Color(capColor(160 * terrainMap[r][c] + 48),
									capColor(128 * terrainMap[r][c] + 32),
									capColor(64 * terrainMap[r][c] + 16)));
						}
					}

				}

				if (palette == 2) {
					// Meat
					if (terrainController.getFluidAt(c, r) > 0 && terrainMap[r][c] < terrainController.getSeaLevel()) {
						theGrid[r][c].setColorID(new Color(capColor(255 * terrainController.getFluidEarthAt(c, r)),
								capColor(5500 * terrainController.getFluidAt(c, r) + 200),
								0));
					} else {
						theGrid[r][c].setColorID(new Color(capColor(Math.sqrt(55225 * terrainMap[r][c])),
								capColor(227 * Math.pow(terrainMap[r][c], 2) + 10),
								capColor(180 * Math.pow(terrainMap[r][c], 2) + 20)));
					}
				}

				if (palette == 3) {
					// Binary Sediment - red if there's suspended sediment (aka fluid earth), black if not
					theGrid[r][c].setColorID(new Color(capColor(terrainController.getFluidEarthAt(c, r) * Math.pow(10,100)), 0, 0));
				}

				if (palette == 4) {
					// Binary Terrain - green if there's terrain, black if not
					theGrid[r][c].setColorID(new Color(0, capColor(terrainMap[r][c] * Math.pow(10,100)), 0));
				}

				if (palette == 5) {
					// Binary Water - blue if there's water, black if not
					theGrid[r][c].setColorID(new Color(0, 0, capColor(terrainController.getFluidAt(c, r) * Math.pow(10,100))));
				}

				if (palette == 6) {
					// Coast - shows whether a tile is above sea level (green) or below (blue)
					if (terrainMap[r][c] > terrainController.getSeaLevel()) {
						theGrid[r][c].setColorID(new Color(0, capColor(terrainMap[r][c] * 128 + 128), 0));
					} else {
						theGrid[r][c].setColorID(new Color(0, 0, capColor(terrainMap[r][c] * 128 + 128)));
					}
				}

				if (palette == 7) {
					// Slope - shows estimated slope of tiles, either X and Y or magnitude
					double[] slope = terrainController.getSlope(c, r);
					// System.out.println(slope[0] + " " + slope[1]);
					theGrid[r][c].setColorID(new Color(capColor(slope[0]*128 + 128), capColor(slope[1]*128 + 128), 128));
					//theGrid[r][c].setColorID(new Color(capColor(Math.sqrt(slope[0]*slope[0] + slope[1]*slope[1]) * 1024), 64, 64));
				}

				theGrid[r][c].drawSelf(g, deltaTime, true); // remember to change this back to performanceMode
			}
	}
	
	/**
	 * the mouse listener has detected a click, and it has happened on the cell in theGrid at row, col
	 * @param row
	 * @param col
	 */
	public void userClickedCell(int row, int col)
	{
		System.out.println("("+row+", "+col+")");
	}
	
	
	
	
	/**
	 * Here's an example of a simple dialog box with a message.
	 */
	public void makeGameOverDialog()
	{
		JOptionPane.showMessageDialog(this, "Game Over.");
		
	}
	
	//============================ Mouse Listener Overrides ==========================

	@Override
	// mouse was just released within about 1 pixel of where it was pressed.
	// NOTE: this is actually kind of obnoxious because if the mouse moved much at all between press
	// and release, it won't register as a click. You may be happier with mouseReleased, instead.
	public void mouseClicked(MouseEvent e)
	{
		// TODO Auto-generated method stub
		// mouse location is at e.getX() , e.getY().
		// if you wish to convert to the rows and columns, you can integer-divide by the cell size.
//		int col = e.getX()/Cell.CELL_SIZE;
//		int row = e.getY()/Cell.CELL_SIZE;
//		userClickedCell(row,col);
		if (palette == 0) {
			palette = 7;
		}
		else if (palette == 7) {
			palette = 0;
		}
		repaint();
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
		// TODO Auto-generated method stub
		// mouse location is at e.getX() , e.getY().
		// if you wish to convert to the rows and columns, you can integer-divide by the cell size.
				
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		// TODO Auto-generated method stub
		// mouse location is at e.getX() , e.getY().
		// if you wish to convert to the rows and columns, you can integer-divide by the cell size.
		
	}

	@Override
	// mouse just entered this window
	public void mouseEntered(MouseEvent e)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	// mouse just left this window
	public void mouseExited(MouseEvent e)
	{
		// TODO Auto-generated method stub
		
	}
	//============================ Key Listener Overrides ==========================

	@Override
	/**
	 * user just pressed and released a key. (May also be triggered by autorepeat, if key is held down?
	 * @param e
	 */
	public void keyTyped(KeyEvent e)
	{
		char whichKey = e.getKeyChar();
		myParent.updateMessage("User just typed \""+whichKey+"\"" );
		System.out.println(whichKey);
	}

	@Override
	public void keyPressed(KeyEvent e)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent e)
	{
		// TODO Auto-generated method stub
		
	}
	// ============================= animation stuff ======================================
	/**
	 * if you wish to have animation, you need to call this method from the GridDemoFrame AFTER you set the window visibility.
	 */
	public void initiateAnimationLoop()
	{
		Thread aniThread = new Thread( new AnimationThread(10)); // the number here is the number of milliseconds between steps.
		aniThread.start();
	}
	
	/**
	 * Modify this method to do what you want to have happen periodically.
	 * This method will be called on a regular basis, determined by the delay set in the thread.
	 * Note: By default, this will NOT get called unless you uncomment the code in the GridDemoFrame's constructor
	 * that creates a thread.
	 *
	 */
	public void animationStep(long millisecondsSinceLastStep)
	{
		//theGrid[0][0].cycleColorIDBackward();
		//System.out.println("step");
		deltaTime = millisecondsSinceLastStep;

		terrainController.stepAndUpdate();
		performanceMode = false;
		if (deltaTime>=12){
			performanceMode = true;
		}
		repaint();
	}
	// ------------------------------- animation thread - internal class -------------------
	public class AnimationThread implements Runnable
	{
		long start;
		long timestep;
		public AnimationThread(long t)
		{
			timestep = t;
			start = System.currentTimeMillis();
		}
		@Override
		public void run()
		{
			long difference;
			while (true)
			{
				difference = System.currentTimeMillis() - start;
				if (difference >= timestep)
				{
					animationStep(difference);
					start = System.currentTimeMillis();
				}
				try
				{	Thread.sleep(1);
				}
				catch (InterruptedException iExp)
				{
					System.out.println(iExp.getMessage());
					break;
				}
			}
		}
	}
}
