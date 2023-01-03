package MinesweeperGraphics;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

// Defines a Panel having borders in minesweeper style
public class MinesweeperPanel extends JPanel {
	
	JPanel centerPanel;									// Center Panel
	// Borders
	JPanel panel1;
	JPanel panel2;
	JPanel panel3;
	JPanel panel4;
	JPanel panel5;
		JLabel label5;
	JPanel panel6;
		JLabel label6;
	ImageIcon imageIcon5;
	ImageIcon imageIcon6;
	Image imageCorner;									// Used for the corners

	int BaseX;											// X Position before rescaling the  MinesweeperFrame
	int BaseY;											// Y Position before rescaling the MinesweeperFrame
	int BaseWidth;										// Width before rescaling the MinesweeperFrame
	int BaseHeight;										// Height before rescaling the MinesweeperFrame
	int BaseBorderSize;									// Borders' size before rescaling the MinesweeperFrame
	
	// Constructor. The arguments are the colors and size of the borders.
	MinesweeperPanel(Color TopLeftColor, Color BottomRightColor, int BorderSize){
		
		this.setLayout(new BorderLayout());
		this.setBackground(new Color(198,198,198));
		this.setOpaque(true);
		
		
		// Center panel
		centerPanel = new JPanel();
		// Borders
		panel1 = new JPanel();
		panel2 = new JPanel();
		panel3 = new JPanel();
		panel4 = new JPanel();
		panel5 = new JPanel();
		panel6 = new JPanel();
		
		centerPanel.setLayout(null);
		
		panel1.setBackground(TopLeftColor);
		panel2.setBackground(BottomRightColor);
		panel3.setBackground(BottomRightColor);
		panel4.setBackground(TopLeftColor);
		
		panel1.setPreferredSize(new Dimension(BorderSize,BorderSize));
		panel2.setPreferredSize(new Dimension(BorderSize,BorderSize));
		panel3.setPreferredSize(new Dimension(BorderSize,BorderSize));
		panel4.setPreferredSize(new Dimension(BorderSize,BorderSize));
		
		// Corners
		panel1.setLayout(new BorderLayout());
			// Label for the bottom left corner
			imageIcon5 = new ImageIcon();
			// Defines an array representing the pixels in the corners
			int[] pixels5 = getCornersPixelArray(TopLeftColor, BottomRightColor, BorderSize);
			// Defines corner's image from previous array
			imageCorner = getImageFromArray(pixels5,BorderSize,BorderSize);
			imageIcon5.setImage(imageCorner);
			label5 = new JLabel();
			label5.setIcon(imageIcon5);
		panel5.setLayout(new BorderLayout());
		panel5.setPreferredSize(new Dimension(BorderSize,BorderSize));
		panel5.add(label5,BorderLayout.NORTH);
		panel1.add(panel5,BorderLayout.EAST);
		panel3.setLayout(new BorderLayout());
			// Label for the top right corner
			imageIcon6 = new ImageIcon();
			// Defines corner's image from imageCorner array
			imageIcon6.setImage(imageCorner);
			label6 = new JLabel();
			label6.setIcon(imageIcon6);
		panel6.setLayout(new BorderLayout());
		panel6.setPreferredSize(new Dimension(BorderSize, BorderSize));
		panel6.add(label6,BorderLayout.SOUTH);
		panel3.add(panel6,BorderLayout.WEST);
		
		this.add(panel1,BorderLayout.NORTH);
		this.add(panel2,BorderLayout.EAST);
		this.add(panel3,BorderLayout.SOUTH);
		this.add(panel4,BorderLayout.WEST);
		this.add(centerPanel,BorderLayout.CENTER);
		
		// Retains borders' size
		BaseBorderSize = BorderSize;
		
	}
	
	// Set the bounds and retains them before rescaling the MinesweeperFrame
	public void setBoundsandRetain(int x, int y, int width, int length) {
		
		super.setBounds(x,y,width,length);
		BaseX = x;
		BaseY = y;
		BaseWidth = width;
		BaseHeight = length;
		
	}
	
	// Rescales and repositions the panel (i.e. the center panel and the borders).
	public void Scale(int scale) {
		
		this.setBounds(scale*BaseX, scale*BaseY, scale*BaseWidth, scale*BaseHeight);
		panel1.setPreferredSize(new Dimension(scale*BaseBorderSize, scale*BaseBorderSize));
		panel2.setPreferredSize(new Dimension(scale*BaseBorderSize, scale*BaseBorderSize));
		panel3.setPreferredSize(new Dimension(scale*BaseBorderSize, scale*BaseBorderSize));
		panel4.setPreferredSize(new Dimension(scale*BaseBorderSize, scale*BaseBorderSize));
		imageCorner = imageCorner.getScaledInstance(scale*BaseBorderSize, scale*BaseBorderSize, Image.SCALE_SMOOTH);
		panel5.setPreferredSize(new Dimension(scale*BaseBorderSize, scale*BaseBorderSize));
		panel6.setPreferredSize(new Dimension(scale*BaseBorderSize, scale*BaseBorderSize));
		imageIcon5.setImage(imageCorner);
		imageIcon6.setImage(imageCorner);
	}
	
	// Defines an array representing the pixels in the corners
	public static int[] getCornersPixelArray(Color TopLeftColor, Color BottomRightColor, int BorderSize) {
		
		int width = BorderSize;
		int height = BorderSize;
		int[] pixels = new int[width*height];
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                if (i < height-j) {
                    pixels[j*width + i] = TopLeftColor.getRGB();
                }
                else {
                    pixels[j*width + i] = BottomRightColor.getRGB();
                }
            }
        }
        
        return pixels;
	}
	
	// Defines an image from the array defined by the above method
	public static Image getImageFromArray(int[] pixels, int width, int height) {

	    BufferedImage pixelImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);    
	    pixelImage.setRGB(0, 0, width, height, pixels, 0, width);
	    
	    return pixelImage;
    }

}
