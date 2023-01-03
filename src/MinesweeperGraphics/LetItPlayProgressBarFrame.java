package MinesweeperGraphics;

import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.text.NumberFormat;
import java.text.ParseException;

import javax.swing.*;
import javax.swing.text.NumberFormatter;

// Frame displaying a progress bar indicating the advancement of the games that are being played by the bot
public class LetItPlayProgressBarFrame extends JDialog implements ComponentListener {

	MinesweeperPanel MainPanel;						// Main Panel, containing all the others elements
	JPanel MoveablePanel;							// Panel used for keeping the elements centered if we change frame's size
		int gamesNumber;							// Total number of games that have to be played
		int gamesCount;								// Number of games that have already been played
		JProgressBar GamesProgressBar;				// Progress bar
		JButton StopButton;							// Stop button
		JButton CloseButton;						// Close button
	
	// Constructor
	public LetItPlayProgressBarFrame(MinesweeperFrame Frame, int gamesNumber){
		
		super(Frame,false);
		
		this.setTitle("Automatic mode progress bar");
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setIconImage(new ImageIcon("sprites\\Minesweeper_Icon.png").getImage());
		this.setLayout(new BorderLayout());
		
		
		
		// Main Panel, containing all the other elements
		MainPanel = new MinesweeperPanel(MinesweeperFrame.LightColor,MinesweeperFrame.DarkColor,3);
		MainPanel.centerPanel.setPreferredSize(new Dimension(500,150));
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
			    
				// Progress bar
				this.gamesNumber = gamesNumber;
			    GamesProgressBar = new JProgressBar();
			    GamesProgressBar.setValue(0);
			    GamesProgressBar.setMaximum(gamesNumber);
			    GamesProgressBar.setString("Playing game number: " + 0 + "/" + gamesNumber);
			    GamesProgressBar.setBounds(25,25,450,50);
			    GamesProgressBar.setStringPainted(true);
			MoveablePanel.add(GamesProgressBar);
			    
				// Stop button
				StopButton = new JButton("Stop");
				StopButton.setBounds(200,100,100,30);
			MoveablePanel.add(StopButton);
			
				// Close button
				CloseButton = new JButton("Close");
				CloseButton.setBounds(200,100,100,30);
				CloseButton.setVisible(false);
				CloseButton.setEnabled(false);
			MoveablePanel.add(CloseButton);
		
		this.pack();
		this.addComponentListener(this);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}
	
	// Creates an empty frame. This is useful for the StopButton and CloseButton to be instantiated
	LetItPlayProgressBarFrame(){
		
		StopButton = new JButton();
		CloseButton = new JButton();
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
