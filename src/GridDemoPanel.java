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
	private boolean meatMode = false;
	
	public GridDemoPanel(GridDemoFrame parent)
	{
		super();
		resetCells();
//		theGrid[2][2].setMarker("A");
//		theGrid[2][2].setDisplayMarker(true);
//		theGrid[3][3].setIsLive(false);
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
		//g.clearRect(0,0,getWidth(),getHeight());
		for (int r =0; r<NUM_ROWS; r++)
			for (int c=0; c<NUM_COLS; c++) {
				theGrid[r][c].setMyGC(getGraphicsConfiguration());

				if (!meatMode) {
					if (terrainMap[r][c] >= terrainController.getSeaLevel()) {
						theGrid[r][c].setColorID(new Color(64, capColor(255 * terrainMap[r][c]), 64));

					} else {
						// theGrid[r][c].setColorID(new Color(0,50,Math.min((int)(terrainController.getFluidAt(c,r)*25500), 255)));

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

				} else {
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

				theGrid[r][c].drawSelf(g, deltaTime, true);
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
		if (!theGrid[row][col].isLive())
			return;
		score += theGrid[row][col].getColorID();
		myParent.updateScore(score);
		
		theGrid[row][col].cycleColorIDForward();
		repaint();
		
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
		int col = e.getX()/Cell.CELL_SIZE;
		int row = e.getY()/Cell.CELL_SIZE;
		userClickedCell(row,col);
		
		
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
