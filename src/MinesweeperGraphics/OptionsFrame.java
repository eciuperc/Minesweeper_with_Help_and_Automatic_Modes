package MinesweeperGraphics;

import java.awt.*;
import java.awt.event.*;
import java.text.NumberFormat;
import java.text.ParseException;

import javax.swing.*;
import javax.swing.text.NumberFormatter;

public class OptionsFrame extends JDialog implements ComponentListener, ActionListener {
	
	int difficulty;										// Difficulty chosen
	boolean customLevel;								// True iff custom level is chosen
	int horizontalSize;									// Game's horizontal size chosen
	int verticalSize;									// Game's vertical size chosen
	int mines;											// Game's Number of mines chosen
	int helpParameter;									// Number representing which game mode has been chosen.

	MinesweeperPanel MainPanel;							// Main Panel, containing all the others elements
		JPanel MoveablePanel;							// Panel used for keeping the elements centered if we change frame's size
			// Panel for defining the difficulty, and the game's dimensions if custom mode.
			JPanel DifficultyPanel;
				JLabel TitleLabel;
				JLabel DiffifcultyLabel;
				JComboBox DifficultyComboBox;			// For choosing difficulty
				// For defining custom game
				JLabel CustomLabel;
				JRadioButton CustomButton;
				JLabel HSizeLabel;
				JLabel VSizeLabel;
				JLabel MinesLabel;
				JFormattedTextField HSizeTextField;
				JFormattedTextField VSizeTextField;
				JFormattedTextField MinesTextField;
			// Panel for defining game mode
			JPanel HelpParametersPanel;
				// Classical mode
				JLabel ClassicalLabel;
				JRadioButton ClassicalButton;
				// Use unblocker
				JLabel UnblockerLabel;
				JRadioButton UnblockerButton;
				// Use help
				JLabel HelpLabel;
				JRadioButton HelpButton;
				// Automatic mode
				JLabel AutomaticLabel;
				boolean automaticSelected;
				JRadioButton AutomaticButton;
				ButtonGroup ComboBoxGroup;
				// Step by step
				JToggleButton StepbyStepButton;
				// Let it play
				JToggleButton LetItPlayButton;
				ButtonGroup AutomaticGroup;				// To avoid StepByStepButton and LetItPlayButton to be clicked at the same time
			// Submit Button
			JPanel ButtonPanel;
				JButton SubmitButton;
	
	// Constructor
	OptionsFrame(MinesweeperFrame frame){
		
		super(frame, false);
		
		difficulty = frame.difficulty;
		customLevel = frame.customLevel;
		horizontalSize = frame.horizontalSize;
		verticalSize = frame.verticalSize;
		mines = frame.mines;
		helpParameter = frame.helpParameter;
		
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		this.setIconImage(new ImageIcon("sprites\\Minesweeper_Icon.png").getImage());
		this.setLayout(new BorderLayout());
		this.setTitle("Options");
		
		
		
		// Main Panel, containing all the other elements
		MainPanel = new MinesweeperPanel(MinesweeperFrame.LightColor, MinesweeperFrame.DarkColor, 3);
		MainPanel.centerPanel.setPreferredSize(new Dimension(400,450));
		MainPanel.centerPanel.setBackground(MinesweeperFrame.BackgroundColor);
		MainPanel.centerPanel.setOpaque(true);
		this.add(MainPanel);
		
			// Panel used for keeping the elements centered if we change frame's size
			MoveablePanel = new JPanel();
			MoveablePanel.setLocation(0,0);
			MoveablePanel.setSize((int) MainPanel.centerPanel.getPreferredSize().getWidth(),(int) MainPanel.centerPanel.getPreferredSize().getHeight());
			MoveablePanel.setLayout(new BorderLayout());
			MoveablePanel.setBackground(MinesweeperFrame.BackgroundColor);
			MoveablePanel.setOpaque(true);
		MainPanel.centerPanel.add(MoveablePanel);
		
				// Panel for defining the difficulty, and the game's dimensions if custom mode.
				DifficultyPanel = new JPanel();
				DifficultyPanel.setPreferredSize(new Dimension(400,170));
				DifficultyPanel.setBackground(MinesweeperFrame.BackgroundColor);
				DifficultyPanel.setOpaque(true);
				DifficultyPanel.setLayout(null);
			MoveablePanel.add(DifficultyPanel,BorderLayout.NORTH);
			
					TitleLabel = new JLabel();
					TitleLabel.setBounds(100,15,200,50);
					TitleLabel.setText("Options");
					TitleLabel.setFont(new Font("Verdana",Font.BOLD,40));
					TitleLabel.setHorizontalAlignment(JLabel.CENTER);
					TitleLabel.setVerticalAlignment(JLabel.CENTER);
				DifficultyPanel.add(TitleLabel);
				
					Font labelFont = new Font("Calibri",Font.BOLD,17);
			
					DiffifcultyLabel = new JLabel();
					DiffifcultyLabel.setBounds(50,100,200,20);
					DiffifcultyLabel.setText("Select difficulty");
					DiffifcultyLabel.setFont(labelFont);
					DiffifcultyLabel.setHorizontalAlignment(JLabel.CENTER);
					DiffifcultyLabel.setVerticalAlignment(JLabel.CENTER);
				DifficultyPanel.add(DiffifcultyLabel);
				
					String[] difficulties = {"Beginner", "Intermediate", "Expert", "Demon"};
				
					DifficultyComboBox = new JComboBox(difficulties);
					DifficultyComboBox.setSelectedIndex(0);
					DifficultyComboBox.setBounds(250,100,100,20);
					DifficultyComboBox.addActionListener(this);
					DifficultyComboBox.setBackground(Color.white);
					DifficultyComboBox.setOpaque(true);
				DifficultyPanel.add(DifficultyComboBox);
				
					// For defining custom game
					CustomLabel = new JLabel();
					CustomLabel.setBounds(50,145,200,20);
					CustomLabel.setText("Custom level");
					CustomLabel.setFont(labelFont);
					CustomLabel.setHorizontalAlignment(JLabel.CENTER);
					CustomLabel.setVerticalAlignment(JLabel.CENTER);
				DifficultyPanel.add(CustomLabel);
				
					CustomButton = new JRadioButton();
					CustomButton.setBounds(290,145,20,20);
					CustomButton.addActionListener(this);
					CustomButton.setOpaque(false);
				DifficultyPanel.add(CustomButton);
				
					HSizeLabel = new JLabel();
					HSizeLabel.setBounds(50,190,200,25);
					Font customTextFont = new Font("Verdana",Font.PLAIN,13);
					HSizeLabel.setText("Game width");
					HSizeLabel.setFont(customTextFont);
					HSizeLabel.setHorizontalAlignment(JLabel.CENTER);
					HSizeLabel.setVerticalAlignment(JLabel.CENTER);
					HSizeLabel.setEnabled(false);
					HSizeLabel.setVisible(false);
				DifficultyPanel.add(HSizeLabel);
				
					VSizeLabel = new JLabel();
					VSizeLabel.setBounds(50,225,200,25);
					VSizeLabel.setHorizontalAlignment(JLabel.CENTER);
					VSizeLabel.setVerticalAlignment(JLabel.CENTER);
					VSizeLabel.setText("Game height");
					VSizeLabel.setFont(customTextFont);
					VSizeLabel.setEnabled(false);
					VSizeLabel.setVisible(false);
				DifficultyPanel.add(VSizeLabel);
				
					MinesLabel = new JLabel();
					MinesLabel.setBounds(50,260,200,25);
					MinesLabel.setHorizontalAlignment(JLabel.CENTER);
					MinesLabel.setVerticalAlignment(JLabel.CENTER);
					MinesLabel.setText("Number of mines");
					MinesLabel.setFont(customTextFont);
					MinesLabel.setEnabled(false);
					MinesLabel.setVisible(false);
				DifficultyPanel.add(MinesLabel);
				
				
					NumberFormat format = NumberFormat.getInstance();
				    NumberFormatter formatterHSize = new NumberFormatter(format){
				        @Override
				        public Object stringToValue(String text) throws ParseException {
				            if (text.length() == 0)
				                return null;
				            return super.stringToValue(text);
				        }
				    };
				    // Maximum width for the game, defined depending on the screen's dimensions to get a game that enters the screen
				    int MaxWidth = frame.maxHorizontalSize;
				    formatterHSize.setValueClass(Integer.class);
				    formatterHSize.setMinimum(0);
				    formatterHSize.setMaximum(MaxWidth);
				    formatterHSize.setAllowsInvalid(false);
				    formatterHSize.setCommitsOnValidEdit(true);
				    
				    // TextField that can only have integers between 0 and MaxWidth
				    HSizeTextField = new JFormattedTextField(formatterHSize);
					HSizeTextField.setBounds(270,190,100,25);
					Font textFieldFont = new Font("Arial",Font.PLAIN,15);
					HSizeTextField.setFont(textFieldFont);
					HSizeTextField.setText(Integer.toString(horizontalSize));
					HSizeTextField.setEnabled(false);
					HSizeTextField.setVisible(false);
				DifficultyPanel.add(HSizeTextField);
				
			    	NumberFormatter formatterVSize = new NumberFormatter(format){
				        @Override
				        public Object stringToValue(String text) throws ParseException {
				            if (text.length() == 0)
				                return null;
				            return super.stringToValue(text);
				        }
				    };
				    // Maximum height for the game, defined depending on the screen's dimensions to get a game that enters the screen
				    int MaxHeight = frame.maxVerticalSize;
				    formatterVSize.setValueClass(Integer.class);
				    formatterVSize.setMinimum(0);
				    formatterVSize.setMaximum(MaxHeight);
				    formatterVSize.setAllowsInvalid(false);
				    formatterVSize.setCommitsOnValidEdit(true);
				
				    // TextField that can only have integers between 0 and MaxHeight
					VSizeTextField = new JFormattedTextField(formatterVSize);
					VSizeTextField.setBounds(270,225,100,25);
					VSizeTextField.setFont(textFieldFont);
					VSizeTextField.setText(Integer.toString(verticalSize));
					VSizeTextField.setEnabled(false);
					VSizeTextField.setVisible(false);
				DifficultyPanel.add(VSizeTextField);
				
			    	NumberFormatter formatterMines = new NumberFormatter(format){
				        @Override
				        public Object stringToValue(String text) throws ParseException {
				            if (text.length() == 0)
				                return null;
				            return super.stringToValue(text);
				        }
				    };
			    	formatterMines.setValueClass(Integer.class);
			    	formatterMines.setMinimum(0);
			    	formatterMines.setMaximum(9999);
			    	formatterMines.setAllowsInvalid(false);
				    formatterMines.setCommitsOnValidEdit(true);
				
					MinesTextField = new JFormattedTextField(formatterMines);
					MinesTextField.setBounds(270,260,100,25);
					MinesTextField.setFont(textFieldFont);
					MinesTextField.setText(Integer.toString(mines));
					MinesTextField.setEnabled(false);
					MinesTextField.setVisible(false);
				DifficultyPanel.add(MinesTextField);
				
				// Panel for defining game mode
				HelpParametersPanel = new JPanel();
				HelpParametersPanel.setPreferredSize(new Dimension(400,230));
				HelpParametersPanel.setBackground(MinesweeperFrame.BackgroundColor);
				HelpParametersPanel.setOpaque(true);
				HelpParametersPanel.setLayout(null);
			MoveablePanel.add(HelpParametersPanel,BorderLayout.CENTER);
			
					// Classical mode
					ClassicalLabel = new JLabel();
					ClassicalLabel.setBounds(50,35,200,40);
					ClassicalLabel.setText("Classical mode");
					ClassicalLabel.setFont(labelFont);
					ClassicalLabel.setHorizontalAlignment(JLabel.CENTER);
					ClassicalLabel.setVerticalAlignment(JLabel.CENTER);
				HelpParametersPanel.add(ClassicalLabel);
				
					ClassicalButton = new JRadioButton();
					ClassicalButton.setBounds(290,45,20,20);
					ClassicalButton.setOpaque(false);
					ClassicalButton.addActionListener(this);
				HelpParametersPanel.add(ClassicalButton);
			
					// Use unblocker
					UnblockerLabel = new JLabel();
					UnblockerLabel.setBounds(50,80,200,40);
					UnblockerLabel.setText("Use unblocker");
					UnblockerLabel.setFont(labelFont);
					UnblockerLabel.setHorizontalAlignment(JLabel.CENTER);
					UnblockerLabel.setVerticalAlignment(JLabel.CENTER);
				HelpParametersPanel.add(UnblockerLabel);
				
					UnblockerButton = new JRadioButton();
					UnblockerButton.setBounds(290,90,20,20);
					UnblockerButton.setOpaque(false);
					UnblockerButton.addActionListener(this);
				HelpParametersPanel.add(UnblockerButton);
				
					// Use help
					HelpLabel = new JLabel();
					HelpLabel.setBounds(50,125,200,40);
					HelpLabel.setText("Use help");
					HelpLabel.setFont(labelFont);
					HelpLabel.setHorizontalAlignment(JLabel.CENTER);
					HelpLabel.setVerticalAlignment(JLabel.CENTER);
				HelpParametersPanel.add(HelpLabel);
				
					HelpButton = new JRadioButton();
					HelpButton.setBounds(290,135,20,20);
					HelpButton.setOpaque(false);
					HelpButton.addActionListener(this);
				HelpParametersPanel.add(HelpButton);
				
					// Automatic mode
					AutomaticLabel = new JLabel();
					AutomaticLabel.setBounds(50,170,200,40);
					AutomaticLabel.setText("Watch bot play");
					AutomaticLabel.setFont(labelFont);
					AutomaticLabel.setHorizontalAlignment(JLabel.CENTER);
					AutomaticLabel.setVerticalAlignment(JLabel.CENTER);
				HelpParametersPanel.add(AutomaticLabel);
				
					automaticSelected = false;
					AutomaticButton = new JRadioButton();
					AutomaticButton.setBounds(290,180,20,20);
					AutomaticButton.addActionListener(this);
					AutomaticButton.setOpaque(false);
				HelpParametersPanel.add(AutomaticButton);
				
					ComboBoxGroup = new ButtonGroup();
					ComboBoxGroup.add(ClassicalButton);
					ComboBoxGroup.add(UnblockerButton);
					ComboBoxGroup.add(HelpButton);
					ComboBoxGroup.add(AutomaticButton);
					
					// Step by step mode
					StepbyStepButton = new JToggleButton("Step by step");
					StepbyStepButton.setBounds(30,225,150,35);
					StepbyStepButton.setFont(new Font("Arial",Font.PLAIN,15));
					StepbyStepButton.setEnabled(false);
					StepbyStepButton.setVisible(false);
					StepbyStepButton.addActionListener(this);
				HelpParametersPanel.add(StepbyStepButton);
					
					// Let it play mode
					LetItPlayButton = new JToggleButton("Let it play");
					LetItPlayButton.setBounds(220,225,150,35);
					LetItPlayButton.setFont(new Font("Arial",Font.PLAIN,15));
					LetItPlayButton.setEnabled(false);
					LetItPlayButton.setVisible(false);
					LetItPlayButton.addActionListener(this);
				HelpParametersPanel.add(LetItPlayButton);
					
					// To avoid StepByStepButton and LetItPlayButton to be clicked at the same time
				    AutomaticGroup = new ButtonGroup();
				    AutomaticGroup.add(StepbyStepButton);
				    AutomaticGroup.add(LetItPlayButton);
					
				SubmitButton = new JButton();
				
				ButtonPanel = new JPanel();
				ButtonPanel.setPreferredSize(new Dimension(400,50));
				ButtonPanel.setBackground(MinesweeperFrame.BackgroundColor);
				ButtonPanel.setOpaque(true);
				ButtonPanel.setLayout(null);
			MoveablePanel.add(ButtonPanel,BorderLayout.SOUTH);
			
					SubmitButton = new JButton("Submit");
					SubmitButton.setBounds(150,10,100,30);
				ButtonPanel.add(SubmitButton);
			
				
			
		// Click the buttons that correspond to the parameters of the current game
		if(difficulty != 0)
			DifficultyComboBox.setSelectedIndex(difficulty-1);
		else
			DifficultyComboBox.setSelectedIndex(1);
		if(customLevel)
			CustomButton.doClick();
		
		if(helpParameter == 0)
			ClassicalButton.doClick();
		if(helpParameter == 1)
			UnblockerButton.doClick();
		if(helpParameter == 2)
			HelpButton.doClick();
		if(helpParameter == 3 || helpParameter == 4)
		{
			AutomaticButton.doClick();
			if(helpParameter == 3)
				StepbyStepButton.doClick();
			if(helpParameter == 4)
				LetItPlayButton.doClick();
		}
		
		
		
		this.pack();
		this.addComponentListener(this);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
		
	}
	
	// Creates an empty frame. This is useful for the SubmitButton to be instantiated
	OptionsFrame(){
		
		SubmitButton = new JButton();
	}
		
	// Closes the frame. Only called when clicking submit button. Parameters are update depending on what we chose.
    public void Close(){
    	
    	difficulty = DifficultyComboBox.getSelectedIndex() + 1;
    	if(CustomButton.isSelected())
    	{
    		difficulty = 0;
    		customLevel = true;
			horizontalSize = Integer.parseInt(HSizeTextField.getText().replaceAll("" + (char) 8239, "")); 
			verticalSize = Integer.parseInt(VSizeTextField.getText().replaceAll("" + (char) 8239, ""));
			mines = Math.min(Integer.parseInt(MinesTextField.getText().replace("" + (char) 8239,"")),this.horizontalSize*this.verticalSize-1);
    	}
    	else
    		customLevel = false;
		if(ClassicalButton.isSelected()) {
			helpParameter = 0;
		}
		else if(UnblockerButton.isSelected()) {
			helpParameter = 1;
		}
		else if(HelpButton.isSelected())
		{
			helpParameter = 2;
		}
		else if(StepbyStepButton.isSelected())
		{
			helpParameter = 3;
		}
		else if(LetItPlayButton.isSelected())
		{
			helpParameter = 4;
		}
		else
		{
			helpParameter = 0;
		}
        
        this.setModal(true);
        this.getOwner().setEnabled(true);
        this.dispose();
      }
        
    // If frame is resized
  	@Override
  	public void componentResized(ComponentEvent e) {
  		// TODO Auto-generated method stub
  		
  		MoveablePanel.setLocation(MainPanel.centerPanel.getWidth()/2-MoveablePanel.getWidth()/2, MainPanel.centerPanel.getHeight()/2-MoveablePanel.getHeight()/2);

  	}

  	@Override
  	public void componentMoved(ComponentEvent e) {
  		// TODO Auto-generated method stub
  		
  	}

  	@Override
  	public void componentShown(ComponentEvent e) {
  		// TODO Auto-generated method stub
  		
  	}

  	@Override
  	public void componentHidden(ComponentEvent e) {
  		// TODO Auto-generated method stub
  		
  	}

  	// Display or hide some elements depending on which options are selected
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
		// Displays the elements for defining custom game if custom mode is selected
		if(e.getSource() == CustomButton && CustomButton.isSelected())
		{
			MainPanel.centerPanel.setPreferredSize(new Dimension(400,(int) MainPanel.centerPanel.getPreferredSize().getHeight() + 125));
			MoveablePanel.setSize((int) MainPanel.centerPanel.getPreferredSize().getWidth(),(int) MainPanel.centerPanel.getPreferredSize().getHeight());
			DifficultyPanel.setPreferredSize(new Dimension(400,(int) DifficultyPanel.getPreferredSize().getHeight() + 125));
			
			HSizeLabel.setEnabled(true);
			VSizeLabel.setEnabled(true);
			MinesLabel.setEnabled(true);
			HSizeTextField.setEnabled(true);
			VSizeTextField.setEnabled(true);
			MinesTextField.setEnabled(true);
			HSizeLabel.setVisible(true);
			VSizeLabel.setVisible(true);
			MinesLabel.setVisible(true);
			HSizeTextField.setVisible(true);
			VSizeTextField.setVisible(true);
			MinesTextField.setVisible(true);
			
			this.pack();
			this.setLocationRelativeTo(null);
			this.setVisible(true);
		}
		// Hides the elements for defining custom game if custom mode is de-selected
		if(e.getSource() == CustomButton && !CustomButton.isSelected())
		{
			MainPanel.centerPanel.setPreferredSize(new Dimension(400,(int) MainPanel.centerPanel.getPreferredSize().getHeight() - 125));
			MoveablePanel.setSize((int) MainPanel.centerPanel.getPreferredSize().getWidth(),(int) MainPanel.centerPanel.getPreferredSize().getHeight());
			DifficultyPanel.setPreferredSize(new Dimension(400,(int) DifficultyPanel.getPreferredSize().getHeight() - 125));
			
			HSizeLabel.setEnabled(false);
			VSizeLabel.setEnabled(false);
			MinesLabel.setEnabled(false);
			HSizeTextField.setEnabled(false);
			VSizeTextField.setEnabled(false);
			MinesTextField.setEnabled(false);
			HSizeLabel.setVisible(false);
			VSizeLabel.setVisible(false);
			MinesLabel.setVisible(false);
			HSizeTextField.setVisible(false);
			VSizeTextField.setVisible(false);
			MinesTextField.setVisible(false);
			
			this.pack();
			this.setLocationRelativeTo(null);
			this.setVisible(true);
		}
		// Uselects the custom mode if we choose a difficulty
		if(e.getSource() == DifficultyComboBox)
		{
			if(CustomButton.isSelected())
				CustomButton.doClick();
		}
		// Displays the elements for automatic mode if automatic mode is selected
		if(e.getSource() == AutomaticButton && !automaticSelected)
		{
			automaticSelected = true;
			MainPanel.centerPanel.setPreferredSize(new Dimension(400,(int) MainPanel.centerPanel.getPreferredSize().getHeight() + 55));
			MoveablePanel.setSize((int) MainPanel.centerPanel.getPreferredSize().getWidth(),(int) MainPanel.centerPanel.getPreferredSize().getHeight());
			HelpParametersPanel.setPreferredSize(new Dimension(400,(int) HelpParametersPanel.getPreferredSize().getHeight() + 55));
			
			StepbyStepButton.setEnabled(true);
			LetItPlayButton.setEnabled(true);
			StepbyStepButton.setVisible(true);
			LetItPlayButton.setVisible(true);
			
			this.pack();
			this.setLocationRelativeTo(null);
			this.setVisible(true);
		}
		// If mode that is not automatic is selected, StepByStepButton and LetItPlayButton are unclicked and hidden
		if((e.getSource() == ClassicalButton || e.getSource() == UnblockerButton || e.getSource() == HelpButton) && automaticSelected)
		{
			automaticSelected = false;
			MainPanel.centerPanel.setPreferredSize(new Dimension(400,(int) MainPanel.centerPanel.getPreferredSize().getHeight() - 55));
			MoveablePanel.setSize((int) MainPanel.centerPanel.getPreferredSize().getWidth(),(int) MainPanel.centerPanel.getPreferredSize().getHeight());
			HelpParametersPanel.setPreferredSize(new Dimension(400,(int) HelpParametersPanel.getPreferredSize().getHeight() - 55));
			
			AutomaticGroup.clearSelection();
			StepbyStepButton.setEnabled(false);
			LetItPlayButton.setEnabled(false);
			StepbyStepButton.setVisible(false);
			LetItPlayButton.setVisible(false);
			
			this.pack();
			this.setLocationRelativeTo(null);
			this.setVisible(true);
		}
		
	}
	
////	 For displaying infos about the game modes
//	public class InfoLabel extends JLabel{
//		
//		int x;
//		int y;
//		int width;
//		int height;
//		
//		InfoLabel(int x, int y, int width, int height){
//			
//			this.x = x;
//			this.y = y;
//			this.width = width;
//			this.x = x;
//		}
//		
//		JLabel pnlCircle = new JLabel() {
//	        public void paintComponent(Graphics g) {
//	    		Graphics2D g2D = (Graphics2D) g;
//	    		g2D.setStroke(new BasicStroke(10));
//	            int X=100;
//	            int Y=150;
//	            int d1=200;
//	            int d2=100;
//	            g2D.drawOval(X, Y, d1, d2);
//	            // Get the FontMetrics
//	            Font font = new Font("Arial",Font.PLAIN,50);
//	            FontMetrics metrics = g.getFontMetrics(font);
//	            String text = "i";
//	            // Determine the X coordinate for the text
//	            int x = X + (d1 - metrics.stringWidth("i")) / 2;
//	            // Determine the Y coordinate for the text (note we add the ascent, as in java 2d 0 is top of the screen)
//	            int y = Y + ((d2 - metrics.getHeight()) / 2) + metrics.getAscent();
//	            // Set the font
//	            g.setFont(font);
//	            // Draw the String
//	            g.drawString(text, x, y);
//	        }
//		};
//	}

}
