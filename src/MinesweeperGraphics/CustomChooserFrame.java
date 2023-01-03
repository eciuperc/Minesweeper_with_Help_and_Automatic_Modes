package MinesweeperGraphics;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.text.NumberFormat;
import java.text.ParseException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.text.NumberFormatter;

// [/!\ DEPRECATED CLASS. NOW CONTAINED IN OptionsFrame CLASS /!\]
// Frame for defining a custom game
public class CustomChooserFrame extends JDialog implements ComponentListener {
	
	MinesweeperPanel MainPanel;									// Main Panel, containing all the others elements
		JPanel MoveablePanel;									// Panel used for keeping the elements centered if we change frame's size
			// Labels
			JLabel TitleLabel;									
			JLabel HSizeLabel;
			JLabel VSizeLabel;
			JLabel MinesLabel;
			// TextField for defining game
			JFormattedTextField HSizeTextField;
			JFormattedTextField VSizeTextField;
			JFormattedTextField MinesTextField;
			JButton SubmitButton;								// Submit Button

	// Constructor
	CustomChooserFrame(MinesweeperFrame Frame){
		
		super(Frame,false);
		
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setIconImage(new ImageIcon("sprites\\Minesweeper_Icon.png").getImage());
		this.setLayout(new BorderLayout());
		this.setTitle("Custom level");
		
		
		
		// Main Panel, containing all the other elements
		MainPanel = new MinesweeperPanel(MinesweeperFrame.LightColor,MinesweeperFrame.DarkColor,3);
		MainPanel.centerPanel.setPreferredSize(new Dimension(350,310));
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
		
				
				// Labels
				TitleLabel = new JLabel();
				TitleLabel.setBounds(0, 15, 360, 50);
				TitleLabel.setText("Custom level");
				TitleLabel.setFont(new Font("Arial",Font.BOLD, 20));
				TitleLabel.setHorizontalAlignment(JLabel.CENTER);
				TitleLabel.setVerticalAlignment(JLabel.CENTER);
			MoveablePanel.add(TitleLabel);
			
			
				HSizeLabel = new JLabel();
				HSizeLabel.setBounds(0,80,200,30);
				Font textFont = new Font("Calibri",Font.BOLD,15);
				HSizeLabel.setText("Game width");
				HSizeLabel.setFont(textFont);
				HSizeLabel.setHorizontalAlignment(JLabel.CENTER);
				HSizeLabel.setVerticalAlignment(JLabel.CENTER);
			MoveablePanel.add(HSizeLabel);
			
				VSizeLabel = new JLabel();
				VSizeLabel.setBounds(0,130,200,30);
				VSizeLabel.setHorizontalAlignment(JLabel.CENTER);
				VSizeLabel.setVerticalAlignment(JLabel.CENTER);
				VSizeLabel.setText("Game height");
				VSizeLabel.setFont(textFont);
			MoveablePanel.add(VSizeLabel);
			
				MinesLabel = new JLabel();
				MinesLabel.setBounds(0,180,200,30);
				MinesLabel.setHorizontalAlignment(JLabel.CENTER);
				MinesLabel.setVerticalAlignment(JLabel.CENTER);
				MinesLabel.setText("Number of mines");
				MinesLabel.setFont(textFont);
			MoveablePanel.add(MinesLabel);
			
			
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
			    int MaxWidth = Frame.maxHorizontalSize;
			    formatterHSize.setValueClass(Integer.class);
			    formatterHSize.setMinimum(0);
			    formatterHSize.setMaximum(MaxWidth);
			    formatterHSize.setAllowsInvalid(false);
			    formatterHSize.setCommitsOnValidEdit(true);
			    
			    // TextField that can only have integers between 0 and MaxWidth
			    HSizeTextField = new JFormattedTextField(formatterHSize);
				HSizeTextField.setBounds(200,80,100,30);
				Font textFieldFont = new Font("Arial",Font.PLAIN,15);
				HSizeTextField.setFont(textFieldFont);
			MoveablePanel.add(HSizeTextField);
			
		    	NumberFormatter formatterVSize = new NumberFormatter(format){
			        @Override
			        public Object stringToValue(String text) throws ParseException {
			            if (text.length() == 0)
			                return null;
			            return super.stringToValue(text);
			        }
			    };
			    // Maximum height for the game, defined depending on the screen's dimensions to get a game that enters the screen
			    int MaxHeight = Frame.maxVerticalSize;
			    formatterVSize.setValueClass(Integer.class);
			    formatterVSize.setMinimum(0);
			    formatterVSize.setMaximum(MaxHeight);
			    formatterVSize.setAllowsInvalid(false);
			    formatterVSize.setCommitsOnValidEdit(true);
			
			    // TextField that can only have integers between 0 and MaxHeight
				VSizeTextField = new JFormattedTextField(formatterVSize);
				VSizeTextField.setBounds(200,130,100,30);
				VSizeTextField.setFont(textFieldFont);
			MoveablePanel.add(VSizeTextField);
			
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
			
			    // TextField that can only have integers between 0 and 9999
				MinesTextField = new JFormattedTextField(formatterMines);
				MinesTextField.setBounds(200,180,100,30);
				MinesTextField.setFont(textFieldFont);
			MoveablePanel.add(MinesTextField);
			
				// Submit button
				SubmitButton = new JButton("Submit");
			SubmitButton.setBounds(105,245,150,30);
			MoveablePanel.add(SubmitButton);
		
		
		
		this.pack();
		this.addComponentListener(this);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}
	
	// Creates an empty frame. This is useful for the SubmitButton to be instantiated
	CustomChooserFrame(){
		
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
