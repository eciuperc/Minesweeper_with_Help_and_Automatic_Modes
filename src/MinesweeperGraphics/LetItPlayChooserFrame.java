package MinesweeperGraphics;

import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.text.NumberFormat;
import java.text.ParseException;

import javax.swing.*;
import javax.swing.text.NumberFormatter;

public class LetItPlayChooserFrame extends JDialog implements ComponentListener {

	MinesweeperPanel MainPanel;								// Main Panel, containing all the others elements
	JPanel MoveablePanel;									// Panel used for keeping the elements centered if we change frame's size
		JLabel InstructionsLabel;							// Text Panel
		JFormattedTextField GamesNumberTextField;			// TextField to enter the number of games that will be played
		JButton SubmitButton;								// Submit button
	
	// Constructor
	public LetItPlayChooserFrame(MinesweeperFrame Frame){
		
		super(Frame,false);
		
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setIconImage(new ImageIcon("sprites\\Minesweeper_Icon.png").getImage());
		this.setLayout(new BorderLayout());
		
		
		
		// Main Panel, containing all the other elements
		MainPanel = new MinesweeperPanel(MinesweeperFrame.LightColor,MinesweeperFrame.DarkColor,3);
		MainPanel.centerPanel.setPreferredSize(new Dimension(350,210));
		MainPanel.centerPanel.setBackground(MinesweeperFrame.BackgroundColor);
		MainPanel.centerPanel.setOpaque(true);
		this.add(MainPanel);
		
		
		
			// Panel used for keeping the elements centered if we change frame's size
			MoveablePanel = new JPanel();
			MoveablePanel.setLocation(0,0);
			MoveablePanel.setSize((int) MainPanel.centerPanel.getPreferredSize().getWidth(),(int) MainPanel.centerPanel.getPreferredSize().getHeight());
			MoveablePanel.setLayout(null);
			MoveablePanel.setBackground(MinesweeperFrame.BackgroundColor);
			MoveablePanel.setOpaque(true);
		MainPanel.centerPanel.add(MoveablePanel);
		
				// Text Panel
				InstructionsLabel = new JLabel();
				InstructionsLabel.setBounds(25,20,300,60);
				InstructionsLabel.setText("<HTML> <div style='text-align:center'> Choose the number of games that <p style='margin-top:-5'> <br> will be played: </div> </HTML>");
				InstructionsLabel.setFont(new Font("Arial",Font.BOLD,15));
				InstructionsLabel.setHorizontalAlignment(JLabel.CENTER);
				InstructionsLabel.setVerticalAlignment(JLabel.CENTER);
			MoveablePanel.add(InstructionsLabel);
			
			
				NumberFormat format = NumberFormat.getInstance();
			    NumberFormatter textFormatter = new NumberFormatter(format){
			        @Override
			        public Object stringToValue(String text) throws ParseException {
			            if (text.length() == 0)
			                return null;
			            return super.stringToValue(text);
			        }
			    };
			    textFormatter.setValueClass(Integer.class);
			    textFormatter.setMinimum(0);
			    textFormatter.setMaximum(9999999);
			    textFormatter.setAllowsInvalid(false);
			    textFormatter.setCommitsOnValidEdit(true);
			    
			    // TextField to enter the number of games that will be played
			    GamesNumberTextField = new JFormattedTextField(textFormatter);
			    GamesNumberTextField.setBounds(75,95,200,30);
				Font textFieldFont = new Font("Arial",Font.PLAIN,15);
				GamesNumberTextField.setFont(textFieldFont);
			MoveablePanel.add(GamesNumberTextField);
			
				// Submit button
				SubmitButton = new JButton("Submit");
			SubmitButton.setBounds(125,145,100,30);
			MoveablePanel.add(SubmitButton);
		
		
		
		this.pack();
		this.addComponentListener(this);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}
	
	// Creates an empty frame. This is useful for the SubmitButton to be instantiated
	LetItPlayChooserFrame(){
		
		SubmitButton = new JButton();
	}
	
	// Closes the frame
	public void Close(){
	    
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
	
}
