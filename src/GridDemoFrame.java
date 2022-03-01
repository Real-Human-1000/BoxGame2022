import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class GridDemoFrame extends JFrame implements ActionListener
{
	GridDemoPanel thePanel;
	JLabel scoreLabel, messageLabel;
	JButton regenerateButton, addEarthButton, addWaterButton;
	JToggleButton disableFlipButton, pModeButton;
	JComboBox paletteSelector;
	public GridDemoFrame()
	{
		super("Grid Demo");
		
		setSize(600,600+24+16);
		
		this.getContentPane().setLayout(new BorderLayout());
		thePanel = new GridDemoPanel(this);
		scoreLabel = new JLabel("Score: 0");
		messageLabel = new JLabel("");
		Box southPanel = Box.createHorizontalBox();


		this.getContentPane().add(buildUIPanel(),BorderLayout.NORTH);
		this.getContentPane().add(thePanel,BorderLayout.CENTER);
		this.getContentPane().add(southPanel, BorderLayout.SOUTH);
		southPanel.add(Box.createHorizontalStrut(10));
		southPanel.add(scoreLabel, BorderLayout.SOUTH);
		southPanel.add(Box.createGlue());
		southPanel.add(messageLabel, BorderLayout.SOUTH);
		southPanel.add(Box.createHorizontalStrut(10));
		
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
		//uiPanel.add(generateDotBox());
		uiPanel.add(Box.createHorizontalGlue());

		return uiPanel;
	}

	private Box buildGenerationBox(){
		Box genBox = Box.createVerticalBox();

		regenerateButton = new JButton("Re-Generate Terrain");
		regenerateButton.addActionListener(this);
		genBox.add(regenerateButton);

		genBox.setBorder(BorderFactory.createTitledBorder("Generation"));

		return genBox;
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
		"Binary Terrain","Binary Water","Coast","Slopes"};
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
		graphicsBox.add(Box.createVerticalGlue());

		return graphicsBox;
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == regenerateButton)
			thePanel.regenerateTerrain();
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
}
