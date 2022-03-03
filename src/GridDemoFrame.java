import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class GridDemoFrame extends JFrame implements ActionListener, ChangeListener
{
	GridDemoPanel thePanel;
	Box uiPanel;
	JLabel scoreLabel, messageLabel;
	JButton regenerateButton, addEarthButton, addWaterButton;
	JTextField numRowsField, numColsField;
	JToggleButton disableFlipButton, pModeButton;
	JComboBox paletteSelector;
	JSlider speedSlider;
	public GridDemoFrame()
	{
		super("Grid Demo");
		
		setSize(Cell.CELL_SIZE*GridDemoPanel.NUM_COLS,Cell.CELL_SIZE*GridDemoPanel.NUM_ROWS+70);

		this.getContentPane().setLayout(new BorderLayout());
		thePanel = new GridDemoPanel(this);
		scoreLabel = new JLabel("Score: 0");
		messageLabel = new JLabel("");
		//Box southPanel = Box.createHorizontalBox();


		uiPanel = buildUIPanel();
		this.getContentPane().add(uiPanel,BorderLayout.NORTH);
		this.getContentPane().add(thePanel,BorderLayout.CENTER);
		//this.getContentPane().add(southPanel, BorderLayout.SOUTH);
		//southPanel.add(Box.createHorizontalStrut(10));
		//southPanel.add(scoreLabel, BorderLayout.SOUTH);
		//southPanel.add(Box.createGlue());
		//southPanel.add(messageLabel, BorderLayout.SOUTH);
		//southPanel.add(Box.createHorizontalStrut(10));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		thePanel.initiateAnimationLoop(); // uncomment this line if your program uses animation.
	}
	
	public void updateMessage(String message)
	{
		messageLabel.setText(message);
		messageLabel.repaint();
	}
	
	public void updateScore(int score)
	{
		scoreLabel.setText("Score: "+score);
		scoreLabel.repaint();
	}

	private Box buildUIPanel()
	{
		Box uiPanel = Box.createHorizontalBox();
		uiPanel.setAlignmentX(Box.LEFT_ALIGNMENT);

		uiPanel.add(buildGenerationBox());
		uiPanel.add(buildMouseModeBox());
		uiPanel.add(buildGraphicsBox());

		uiPanel.add(Box.createHorizontalGlue());

		return uiPanel;
	}

	private Box buildGenerationBox(){
		Box genBox = Box.createVerticalBox();

		regenerateButton = new JButton("Re-Generate Terrain");
		regenerateButton.addActionListener(this);
		genBox.add(regenerateButton);

		numRowsField = new JTextField(3);
		JLabel rowsLabel = new JLabel("Set Size");
		rowsLabel.setLabelFor(numRowsField);
		//numColsField = new JTextField(3);
		//JLabel colsLabel = new JLabel("Set Number of Columns");
		//colsLabel.setLabelFor(numColsField);
		genBox.add(rowsLabel);
		genBox.add(numRowsField);

		//no support for not square dimensions
		//genBox.add(colsLabel);
		//genBox.add(numColsField);

		speedSlider = new JSlider(0,200,100);
		speedSlider.addChangeListener(this);
		JLabel speedLabel = new JLabel("Set Simulation Speed");
		speedLabel.setLabelFor(speedSlider);
		genBox.add(speedLabel);
		genBox.add(speedSlider);


		genBox.setBorder(BorderFactory.createTitledBorder("Generation & Simulation"));
		//genBox.add(Box.createVerticalGlue());

		return genBox;
	}


	private int getNewDimension(JTextField field,int fallBackValue){
		int n;
		try{
			n = Integer.parseInt(field.getText());
		}catch(NumberFormatException e){
			n = fallBackValue;
		}
		return n;
	}

	private Box buildMouseModeBox(){
		Box mModeBox = Box.createVerticalBox();

		addEarthButton = new JButton("Add Earth");
		addWaterButton = new JButton("Add Water");
		ButtonGroup bg = new ButtonGroup();
		addWaterButton.setSelected(true);

		bg.add(addEarthButton);
		bg.add(addWaterButton);

		mModeBox.add(addEarthButton);
		mModeBox.add(addWaterButton);

		addEarthButton.addActionListener(this);
		addWaterButton.addActionListener(this);

		mModeBox.setBorder(BorderFactory.createTitledBorder("Mouse Mode"));
		mModeBox.add(Box.createVerticalGlue());

		return mModeBox;
	}



	private Box buildGraphicsBox(){
		Box graphicsBox = Box.createVerticalBox();

		graphicsBox.add(new JLabel("Select Palette"));

		String[] items = new String[]{"RGB Direct","Detailed (Classic)","Meat","Binary Sediment",
		"Binary Terrain","Binary Water","Coast","Slopes","Speed"};
		paletteSelector = new JComboBox(items);
		paletteSelector.addActionListener(this);
		graphicsBox.add(paletteSelector);

		pModeButton = new JToggleButton("Force Performance Mode");
		pModeButton.setSelected(false);
		pModeButton.addActionListener(this);
		graphicsBox.add(pModeButton);

		disableFlipButton = new JToggleButton("Disable Flipping");
		disableFlipButton.setSelected(false);
		disableFlipButton.addActionListener(this);
		graphicsBox.add(disableFlipButton);

		graphicsBox.setBorder(BorderFactory.createTitledBorder("Graphics"));
		//graphicsBox.add(Box.createVerticalGlue());

		return graphicsBox;
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == regenerateButton) {
			GridDemoPanel.setDimensions(
					getNewDimension(numRowsField,GridDemoPanel.NUM_ROWS)
					//,getNewDimension(numColsField,GridDemoPanel.NUM_COLS)
			);
			setSize(Cell.CELL_SIZE*GridDemoPanel.NUM_COLS,
					Cell.CELL_SIZE*GridDemoPanel.NUM_ROWS+uiPanel.getHeight());

			thePanel.regenerateTerrain();
		}
		if (e.getSource() == addEarthButton)
			GridDemoPanel.setAddMode(false);
		if (e.getSource() == addWaterButton)
			GridDemoPanel.setAddMode(true);
		if (e.getSource() == paletteSelector)
			GridDemoPanel.setPalette(paletteSelector.getSelectedIndex());
		if (e.getSource() == disableFlipButton)
			GridDemoPanel.setDoFlipAnims(!disableFlipButton.isSelected());
		if (e.getSource() == pModeButton)
			GridDemoPanel.setForcePerformanceMode(pModeButton.isSelected());

	}

	@Override
	public void stateChanged(ChangeEvent e) {
		if (e.getSource() == speedSlider){
			GridDemoPanel.setSpeedMultiplier(speedSlider.getValue());
		}
	}
}
