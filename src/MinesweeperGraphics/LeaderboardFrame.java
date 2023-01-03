package MinesweeperGraphics;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

// Frame for displaying leaderboards. Uses the MinesweeperLeaderboard class.
public class LeaderboardFrame extends JDialog implements ComponentListener {
	
	int size;			   // Size of the leaderboard
	int maxSize = 10;      // maximum number of scores that can be displayed on the screen
	int difficulty;		   // Difficulty of the times currently displayed
	
	MinesweeperPanel MainPanel;						// Main Panel, containing all the other elements
		JPanel MoveablePanel;						// Panel used for keeping the elements centered if we change frame's size
			// Panel at the top of the frame
			JPanel TopPanel;
				JLabel TitleTop;
				JLabel DecoLabel1;
				JLabel DecoLabel2;
				JLabel DifTop;
				JComboBox ComboBox;
				JLabel NumberTop;
				JLabel NameTop;
				JLabel TimeTop;
			// Permits scrolling if many scores are displayed
			JScrollPane ScrollPane;
				JPanel ScoresPanel;
					JLabel[] NumberLabels;			 // Numbers at the left
					JLabel[] NameLabels;			 // Names of the players
					JLabel[] TimeLabels;			 // Corresponding times
	
	// Constructor. The arguments are the difficulty and the ranking of the game that has just been played, so it is displayed in blue
	LeaderboardFrame(MinesweeperFrame Frame, int difficulty, int currentRanking){
		
		super(Frame,false);
		
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setIconImage(new ImageIcon("sprites\\Minesweeper_Icon.png").getImage());
		this.setLayout(new BorderLayout());
		this.setTitle("Leaderboard");
		
		
		
		MinesweeperLeaderboard leaderboard = Frame.Leaderboard;
		this.size = leaderboard.getSize();
		this.difficulty = difficulty;
		
		// Main Panel, containing all the others elements
		MainPanel = new MinesweeperPanel(MinesweeperFrame.LightColor, MinesweeperFrame.DarkColor, 6);	
		MainPanel.centerPanel.setPreferredSize(new Dimension(600,40*this.maxSize+18+180));
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
		
				// Panel at the top of the frame
				TopPanel = new JPanel();
				TopPanel.setPreferredSize(new Dimension(600,180));
				TopPanel.setBackground(MinesweeperFrame.BackgroundColor);
				TopPanel.setOpaque(true);
				TopPanel.setLayout(null);
			MoveablePanel.add(TopPanel,BorderLayout.NORTH);
			
					String[] difficulties = {"Beginner", "Intermediate", "Expert", "Demon"};
			
					TitleTop = new JLabel();
					TitleTop.setBounds(100,15,400,50);
					TitleTop.setText("Leaderboard");
					TitleTop.setFont(new Font("Verdana",Font.PLAIN,40));
					TitleTop.setHorizontalAlignment(JLabel.CENTER);
					TitleTop.setVerticalAlignment(JLabel.CENTER);
				TopPanel.add(TitleTop);
				
					DecoLabel1 = new JLabel();
					DecoLabel1.setBounds(70,20,60,45);
					DecoLabel1.setIcon(new ImageIcon(new ImageIcon("sprites\\\\Minesweeper_Icon.png").getImage().getScaledInstance(65, 40, Image.SCALE_SMOOTH)));
				TopPanel.add(DecoLabel1);
				
					DecoLabel2 = new JLabel();
					DecoLabel2.setBounds(470,20,60,45);
					DecoLabel2.setIcon(new ImageIcon(new ImageIcon("sprites\\\\Minesweeper_Icon.png").getImage().getScaledInstance(65, 40, Image.SCALE_SMOOTH)));
				TopPanel.add(DecoLabel2);
				
					DifTop = new JLabel();
					DifTop.setBounds(200,75,200,40);
					DifTop.setText(difficulties[difficulty-1]);
					DifTop.setFont(new Font("Verdana",Font.PLAIN,30));
					switch(difficulty)
					{
					case(1): DifTop.setForeground(new Color(0,106,0)); break;
					case(2): DifTop.setForeground(new Color(0,0,155)); break;
					case(3): DifTop.setForeground(Color.red); break;
					case(4): DifTop.setForeground(new Color(114,11,7)); break;
					}
					DifTop.setHorizontalAlignment(JLabel.CENTER);
					DifTop.setVerticalAlignment(JLabel.CENTER);
				TopPanel.add(DifTop);
				
					ComboBox = new JComboBox<>(difficulties);
	//				ComboBox.setRenderer(new DefaultListCellRenderer() {
	//				    @Override
	//				    public void paint(Graphics g) {
	//				        setBackground(Color.WHITE);
	//				        setForeground(Color.BLACK);
	//				        super.paint(g);
	//				    }
	//				});
					ComboBox.setSelectedIndex(difficulty-1);
					ComboBox.setBounds(50,85,100,20);
					ComboBox.setBackground(Color.white);
					ComboBox.setOpaque(true);
				TopPanel.add(ComboBox);
				
					Font topFont = new Font("Verdana",Font.BOLD,15);
				
					NumberTop = new JLabel();
					NumberTop.setBounds(25,140,150,40);
					NumberTop.setText("Rank");
					NumberTop.setFont(topFont);
					NumberTop.setHorizontalAlignment(JLabel.CENTER);
					NumberTop.setVerticalAlignment(JLabel.CENTER);
				TopPanel.add(NumberTop);
					
					NameTop = new JLabel();
					NameTop.setBounds(200,140,200,40);
					NameTop.setText("Name");
					NameTop.setFont(topFont);
					NameTop.setHorizontalAlignment(JLabel.CENTER);
					NameTop.setVerticalAlignment(JLabel.CENTER);
				TopPanel.add(NameTop);
					
					TimeTop = new JLabel();
					TimeTop.setBounds(425,140,150,40);
					TimeTop.setText("Time");
					TimeTop.setFont(topFont);
					TimeTop.setHorizontalAlignment(JLabel.CENTER);
					TimeTop.setVerticalAlignment(JLabel.CENTER);
				TopPanel.add(TimeTop);
			
				// Permits scrolling if many scores are displayed
				ScrollPane = new JScrollPane();
				ScrollPane.setPreferredSize(new Dimension(600,40*this.maxSize+18));
			
					ScoresPanel = new JPanel();
					ScoresPanel.setPreferredSize(new Dimension(500,40*this.size));
					ScoresPanel.setBackground(MinesweeperFrame.BackgroundColor);
					ScoresPanel.setOpaque(true);
					ScoresPanel.setLayout(null);
					
					Font numbersFont = new Font("Arial",Font.BOLD,15);
					Font namesFont = new Font("Calibri",Font.BOLD,15);
					Font timesFont = new Font("Arial",Font.BOLD,15);
					
					// Numbers at the left
					NumberLabels = new JLabel[this.size];
					// Names of the players
					NameLabels = new JLabel[this.size];		
					// Corresponding times
					TimeLabels = new JLabel[this.size];		
					
						for(int i = 0; i<size; ++i)
						{						
								NumberLabels[i] = new JLabel();
								NumberLabels[i].setBounds(25,40*i,150,40);
								NumberLabels[i].setText(Integer.toString(i+1));
								NumberLabels[i].setFont(timesFont);
								NumberLabels[i].setHorizontalAlignment(JLabel.CENTER);
								NumberLabels[i].setVerticalAlignment(JLabel.CENTER);
								ScoresPanel.add(NumberLabels[i]);
								
							double time = leaderboard.getIndividualTime(difficulty, i);
							
							if(time < Double.MAX_VALUE)
							{
								
								NameLabels[i] = new JLabel();
								NameLabels[i].setBounds(200,40*i,200,40);
								NameLabels[i].setText(leaderboard.getIndividualName(difficulty, i));
								NameLabels[i].setFont(namesFont);
								NameLabels[i].setHorizontalAlignment(JLabel.CENTER);
								NameLabels[i].setVerticalAlignment(JLabel.CENTER);
								ScoresPanel.add(NameLabels[i]);
								
								TimeLabels[i] = new JLabel();
								TimeLabels[i].setBounds(425,40*i,150,40);
								TimeLabels[i].setText(Double.toString(time));
								TimeLabels[i].setFont(timesFont);
								TimeLabels[i].setHorizontalAlignment(JLabel.CENTER);
								TimeLabels[i].setVerticalAlignment(JLabel.CENTER);
								ScoresPanel.add(TimeLabels[i]);
								
								if(i == currentRanking)
								{
									NumberLabels[i].setForeground(Color.blue);
									NameLabels[i].setForeground(Color.blue);
									TimeLabels[i].setForeground(Color.blue);
								}
							}
						}
						
					ScrollPane.setViewportView(ScoresPanel);
				
			MoveablePanel.add(ScrollPane,BorderLayout.SOUTH);
		
		
		
		this.pack();
		this.addComponentListener(this);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}
	
	// Creates an empty frame. This is useful for the ComboBox to be instantiated
	LeaderboardFrame(){
		
		ComboBox = new JComboBox<>();
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
