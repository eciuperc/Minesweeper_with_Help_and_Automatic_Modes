package MinesweeperGraphics;

import java.awt.Color;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

// MinesweeperPanel that can be pressed. Its instances are the buttons that are in the InfosPanel in MinesweeperFrame.
public class MinesweeperPressablePanel extends MinesweeperPanel {
	
	int scalingFactor; 							// Scaling factor
	boolean mouseEntered;						// True iff mouse is in the panel
	MouseListener ML;							// MouseListener
	JLabel Label;								// Image Label
		ImageIcon UnpressedIcon;				// ImageIcon if the Panel is not pressed
		ImageIcon PressedIcon;					// ImageIcon if the Panel is pressed

	// Constructor. The arguments are the colors and the size of the borders, the scaling factor and the ImageIcons.
	MinesweeperPressablePanel(Color TopLeftColor, Color BottomRightColor, int BorderSize, int scalingFactor, ImageIcon aUnpressedIcon, ImageIcon aPressedIcon){
		
		super(TopLeftColor, BottomRightColor, BorderSize);
		
		mouseEntered = false;
		
		// Defines label and images
		Label = new JLabel();
		Label.setBounds(0,0,24,24);
			UnpressedIcon = aUnpressedIcon;
			PressedIcon = aPressedIcon;
		Label.setIcon(UnpressedIcon);
		this.centerPanel.add(Label);
		
		// MouseListener
	    ML = new MouseListener() {
	    	@Override
	    	public void mouseClicked(MouseEvent e) {
	    		// TODO Auto-generated method stub
	    		
	    	}

	    	@Override
	    	public void mousePressed(MouseEvent e) {
	    		// TODO Auto-generated method stub
	    		
	    		// When mouse is pressed, we change the ImageIcon
	    		if(e.getButton() == MouseEvent.BUTTON1)
	    		{
		    		mouseEntered = true;
					Label.setIcon(PressedIcon);
	    		}
	    		
	    	}

	    	@Override
	    	public void mouseReleased(MouseEvent e) {
	    		// TODO Auto-generated method stub
	    		
	    		// When mouse is released, we re-change the ImageIcon and perform the actions we want to
	    		if(mouseEntered && e.getButton() == MouseEvent.BUTTON1)
	    		{
					Label.setIcon(UnpressedIcon);
					ActionWhenPressed();
	    		}
	    	}

	    	@Override
	    	public void mouseEntered(MouseEvent e) {
	    		// TODO Auto-generated method stub
	    		
	    		mouseEntered = true;
	    	}

	    	@Override
	    	public void mouseExited(MouseEvent e) {
	    		// TODO Auto-generated method stub
	    		
	    		// When mouse exits the panel, the ImageIcon gets back to its unpressed state.
	    		mouseEntered = false;
	    		Label.setIcon(UnpressedIcon);
	    	}
	    };
	    
	    this.centerPanel.addMouseListener(ML);
	}
	
	// Scales the panel
	public void Scale(int scalingFactor) {
		
		super.Scale(scalingFactor);
		Label.setBounds(scalingFactor*0, scalingFactor*0, scalingFactor*24, scalingFactor*24);
		UnpressedIcon = new ImageIcon(UnpressedIcon.getImage().getScaledInstance(scalingFactor*24,scalingFactor*24, Image.SCALE_SMOOTH));
		PressedIcon = new ImageIcon(PressedIcon.getImage().getScaledInstance(scalingFactor*24,scalingFactor*24, Image.SCALE_SMOOTH));
		Label.setIcon(UnpressedIcon);
	}
	
	// Changes UnpressedIcon
	public void ChangeUnpressedImageIcon(ImageIcon newUnpressedIcon, int scalingFactor) {
		
		this.scalingFactor = scalingFactor;
		
		UnpressedIcon = newUnpressedIcon;
		UnpressedIcon = new ImageIcon(UnpressedIcon.getImage().getScaledInstance(scalingFactor*24,scalingFactor*24, Image.SCALE_SMOOTH));
		Label.setIcon(UnpressedIcon);
	}
	
	// Changes PressedIcon
	public void ChangePressedImageIcon(ImageIcon newPressedIcon, int scalingFactor) {
		
		this.scalingFactor = scalingFactor;
		
		PressedIcon = newPressedIcon;
		PressedIcon = new ImageIcon(PressedIcon.getImage().getScaledInstance(scalingFactor*24,scalingFactor*24, Image.SCALE_SMOOTH));
	}
	
	// Reset the Label's icon to UnpressedIcon
	public void ResetImageIcon() {
		
		Label.setIcon(UnpressedIcon);
	}
	
	// Actions made when pressing the panel. Defined in MinesweeperFrame for each instance.
	public void ActionWhenPressed() {
		
	}
}
