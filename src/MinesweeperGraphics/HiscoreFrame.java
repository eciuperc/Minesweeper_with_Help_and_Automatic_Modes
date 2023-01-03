package MinesweeperGraphics;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

// Frame that appears when the player makes a new hiscore.
// Displays the ranking, and permits the player to enter its name.
public class HiscoreFrame extends JDialog implements ComponentListener {
	
	MinesweeperPanel MainPanel;								// Main Panel, containing all the others elements
		JPanel MoveablePanel;								// Panel used for keeping the elements centered if we change frame's size
			JLabel TextLabel;								// Text Label
			JTextField NameTextField;						// TextField for entering player's name	
			JButton SubmitButton;							// Submit Button
	
	HiscoreFrame(MinesweeperFrame Frame){
		
		super(Frame,false);
		
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setIconImage(new ImageIcon("sprites\\Minesweeper_Icon.png").getImage());
		this.setLayout(new BorderLayout());

		
		
		// Main Panel, containing all the others elements
		MainPanel = new MinesweeperPanel(MinesweeperFrame.LightColor, MinesweeperFrame.DarkColor, 3);
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
		
				// Text Label
				TextLabel = new JLabel();
				TextLabel.setBounds(25,20,300,60);
				TextLabel.setFont(new Font("Arial",Font.BOLD,15));
				String humanranking = String.valueOf(Frame.ranking+1);
				TextLabel.setText("<HTML><div style='text-align:center'> New high score! You are rank " + humanranking + "! <p style='margin-top:-5'> <br> Please enter your name: </div></HTML>");
				TextLabel.setHorizontalAlignment(JLabel.CENTER);
				TextLabel.setVerticalAlignment(JLabel.CENTER);
			MoveablePanel.add(TextLabel);
			
				// TextField for entering player's name
				NameTextField = new JTextField();
				NameTextField.setBounds(75,95,200,30);
				NameTextField.setFont(new Font("Calibri",Font.PLAIN,14));
				NameTextField.setHorizontalAlignment(JLabel.CENTER);
			MoveablePanel.add(NameTextField);
			
				// Submit Button
				SubmitButton = new JButton("Submit");
				SubmitButton.setBounds(125,145,100,30);
			MoveablePanel.add(SubmitButton);
		
		
		
		this.pack();
		this.addComponentListener(this);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}
	
	// Creates an empty frame. This is useful for the SubmitButton to be instantiated
	HiscoreFrame(){
		
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
