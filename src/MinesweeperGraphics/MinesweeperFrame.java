package MinesweeperGraphics;
import Minesweeper.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;


public class MinesweeperFrame extends JFrame implements ComponentListener, ActionListener {
	
	// Game's attributes
	MinesweeperGame Game;												// Game we're currently playing
	int difficulty;														// Difficulty of the game. Equals 0 if game is custom.
	boolean customLevel;												// True iff game is custom
	int horizontalSize;													// Used for defining the horizontal size of the game
	int verticalSize;													// Used for defining the vertical size of the game
	int mines;															// Used for defining the number of mines of the game
	int helpParameter;													// Game mode we're currently playing (0 = classical mode, 1 = with unblocker, 2 = with help, 3 = automatic step by step, 4 = automatic let it play)
	MinesweeperLeaderboard Leaderboard;									// Leaderboard
	int ranking;														// Ranking of the score in the leaderboards
	int LframeRanking;													// Ranking of the game that has just finished and that is currently displayed in blue in the leaderboard
	int LframeDifficulty;												// Difficulty of the game that has just finished and that is currently displayed in blue in the leaderboard
	
	
	
	// Displaying attributes
	int screenWidth;													// Screen's width
	int screenHeight;													// Screen's height
	int maxHorizontalSize;												// Maximum horizontal size we can enter for the game in custom mode. Based on screen's dimensions
	int maxVerticalSize;												// Maximum vertical size we can enter for the game in custom mode. Based on screen's dimensions
	int scalingFactor;													// Scaling factor by which we multiply the dimensions of all the graphical elements.
	boolean fullSize;													// True iff window is full size, so when we reset the game, it remains so
	int minInfosWidth;													// Minimum width of the Info Panel
	static final Color BackgroundColor = new Color(198,198,198);		// Color of the background
	static final Color LightColor = new Color(255,255,255);				// Color for the borders
	static final Color DarkColor = new Color(128,128,128);				// Color for the borders
	
	
	
	// Menu Bar
	JMenuBar MenuBar;
		// Option menu
		JMenu OptionMenu;
			OptionsFrame OFrame;										// Options menu frame
				LetItPlayChooserFrame LIPFrame;							// Frame when using let it play mode
				LetItPlayProgressBarFrame LIPPBFrame;					// Progress bar for let it play mode
				int autoGamesNumber;									// Number of games that will be played in let it play mode
				int autoGamesCount;										// Number of games that have already been played in let it play mode
				UpdateProgressBarTask FirstAutoTask;					// Thread for updating progress bar
				AutomaticPlayTask SecondAutoTask;						// Thread for playing games in let it play mode
				GameDisplayTask ThirdAutoTask;							// Thread for displaying games in let it play mode
		// Leaderboard menu
		JMenu LeaderboardMenu;
			LeaderboardFrame LFrame;									// Leaderboard frame
			HiscoreFrame HiscoreFrame;									// Frame when player does a hiscore
	
	MinesweeperPanel MainPanel;											// Main Panel, containing all the others elements
	JPanel MoveablePanel;												// Panel used for keeping the elements centered if we change frame's size
	MinesweeperPanel InfosPanel;										// Info panel on top of the game, containing several informations about the game
	// Panel containing the number of mines left
	MinesweeperPanel MinePanel;
		String minesString;
		JPanel m1Panel;
			JLabel m1Label;
				ImageIcon m1Icon;
		JPanel m2Panel;
			JLabel m2Label;
				ImageIcon m2Icon;
		JPanel m3Panel;
			JLabel m3Label;
				ImageIcon m3Icon;
	MinesweeperPressablePanel SmileyPanel;								// Smiley Panel, indicating the state of the game, i.e. if it is lost, won, or still playing
	// Panel containing time the passed since the beginning of the game
	MinesweeperPanel TimePanel;
		Timer timer;													// Timer
		String timeString;
		JPanel t1Panel;
			JLabel t1Label;
				ImageIcon t1Icon;
		JPanel t2Panel;
			JLabel t2Label;
				ImageIcon t2Icon;
		JPanel t3Panel;
			JLabel t3Label;
				ImageIcon t3Icon;
	// Panel used for help mode, for requiring help
	MinesweeperPressablePanel HelpPanel;
		int remainingHelp;												// Number of help remaining
		JLabel remainingHelpLabel;
			ImageIcon HelpIcon;
	// Test Panel
	MinesweeperPressablePanel Test1Panel;
		JLabel Test1Label;
//	// Test Panel
//	MinesweeperPressablePanel Test2Panel;
//		JLabel Test2Label;
	MinesweeperPressablePanel ProbabilitiesPanel;						// Panel used for step by step mode, for displaying the probability of each tile to be a mine
		boolean probabilitiesDisplayed;									// True iff the probabilities are displayed
	MinesweeperPressablePanel NextBotStepPanel;							// Panel used for step by step mode, for playing next bot's step
	MinesweeperPressablePanel FinishBotPanel;							// Panel used for step by step mode. When pressed, bot plays moves until the game is finished.
	// Game's panel, containing all the tiles
	MinesweeperPanel GamePanel;
		MouseListener GameML;											// Mouse Listener
		Point GameClickPt;												// Point where we clicked the mouse
		boolean[][] rescaleGrid;										// Used when we reset the game, for knowing which tiles we must reset. For displaying speed.
		// Tiles
		JLabel[] TileLabels;											
			ImageIcon[] TileIcons;
			
			

	MinesweeperHelper Helper;											// Helper, used for unblocker mode and help mode
	MinesweeperSolver Solver;											// Solver, used for automatic modes (i.e. step by step mode and let it play mode)
	
	// For debugging
	boolean stopSolver;													// If we want to stop the solver at a precise moment, for example when it has to use probabilities computation functions
	int guessingTileCount;												// For counting how many tiles were determined by guessing (i.e. by using probabilities computation functions)
	ArrayList<Float> probabilitiesList;									// For retaining the probabilities of all the tiles that have been guessed
	ArrayList<Float> randomClickProbabilitiesList;						// For retaining the probabilities if we do a random click, in order to compare to the above probability					
	ArrayList<Boolean> goodGuessList;									// Retains if each guess was a mine or not, to check that the probabilities' computation are accurate (i.e. we check if mean probability corresponds to mean number of mines)
//	double displayedInfluence;											// Parameter for the guessing. Deprecated.
	
	public MinesweeperFrame(int difficulty, boolean customLevel, int customHorizontalSize, int customVerticalSize, int customMines, int argHelpParameter){
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle("Minesweeper");
		this.setIconImage(new ImageIcon("sprites\\Minesweeper_Icon.png").getImage());
		this.setLayout(new BorderLayout());
		this.setEnabled(true);
		
		
				
		
		// Define game's dimensions and number of mines
		this.customLevel = customLevel;
		if(!customLevel) {
			this.difficulty = difficulty;
			switch(difficulty) {
			// Beginner difficulty
			case(1): this.horizontalSize = 9; this.verticalSize = 9; this.mines = 10; break;
			// Intermediate difficulty
			case(2): this.horizontalSize = 16; this.verticalSize = 16; this.mines = 40; break;
			// Expert difficulty
			case(3): this.horizontalSize = 30; this.verticalSize = 16; this.mines = 99; break;
			// Demon difficulty
			case(4): this.horizontalSize = 75; this.verticalSize = 35; this.mines = 600; break;
			}
		}
		// If custom level, dimensions and number of mines are user-defined
		else {
			this.difficulty = 0;
			this.horizontalSize = customHorizontalSize;
			this.verticalSize = customVerticalSize;
			// Mines' has a maximal value, which is the number of tiles - 1
			this.mines = Math.min(customMines,customHorizontalSize*customVerticalSize-1);
		}
		// Instantiate the game
		this.Game = new MinesweeperGame(horizontalSize, verticalSize, mines);
		// Define help parameter
		this.helpParameter = argHelpParameter;
		// Leaderboard
		Leaderboard = MinesweeperLeaderboard.getInstance();
		Leaderboard.loadData();
		// Ranking
		ranking = -1;
		// Ranking of the game that has just finished and that is currently displayed in blue in the leaderboard
		LframeRanking = -1;
		// Difficulty of the game that has just finished and that is currently displayed in blue in the leaderboard
		LframeDifficulty = 0;

		// Get screen's dimensions
		Rectangle winSize = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
		screenWidth = (int) winSize.getWidth();
		screenHeight = (int) winSize.getHeight()-35;
	    // Deduce maximum dimensions a game can have (for custom mode)
	    maxHorizontalSize = (screenWidth-24)/16;
		maxVerticalSize = (screenHeight-65)/16;
		// Scaling factor by which we multiply the dimensions of all the graphical elements.
		scalingFactor = Math.min(Math.max(Math.min((int) screenWidth/(16*this.horizontalSize + 24), screenHeight/(16*this.verticalSize + 65)), 1),Math.min((int) screenWidth/(16*9 + 24), screenHeight/(16*9 + 65)));
		// True iff window is full size, so when we reset the game, it remains so
		fullSize = false;
		// Minimum width of the Info Panel
		minInfosWidth = 9;
		
		
   
		// Mouse-Listener for the Game panel
	    GameML = new MouseListener() {
	    	@Override
	    	public void mouseClicked(MouseEvent e) {
	    		// TODO Auto-generated method stub
	    		
	    		
	    	}

	    	// When mouse is pressed
	    	@Override
	    	public void mousePressed(MouseEvent e) {
	    		// TODO Auto-generated method stub

	    		// We can't click on the game Panel when playing in automatic mode 
	    		if(helpParameter != 3 && helpParameter != 4)
	    		{
	    			// Get tile where we clicked
		    		GameClickPt = e.getPoint();
		    		int i = (int) GameClickPt.getX()/(16*scalingFactor);
		    		int j = (int) GameClickPt.getY()/(16*scalingFactor);
		    		
		    		if(!Game.gameLost && !Game.gameWon)
		    		{
	//		    			if(e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1)
	//		    			{
	//		    				DoubleClick(i,j);
	//		    				
	//		    				ComputeAntiBlock();
	//		    				
	//			    			game.CheckWin();
	//			    			if(game.gameWon) {
	//			    				Won(true);
	//			    			}
	//		    			}
		    			
		    			// Left click
		    			if(e.getButton() == MouseEvent.BUTTON1)
			    		{
		    				// Restart the timer if first click
			    			if(Game.firstClick)
			    			{
			    				timer.restart();
			    				Game.startTime = System.nanoTime();
			    			}
			    			// Generate the grid if first click
			    			Game.GenerateGrid(i, j, mines);
			    			// If tile is uncovered, we click it
			    			if(!Game.plottedGrid[i][j])
			    				PressTile(i,j,true);
			    			// If it is not, we chord, i.e. we check if the tile's number correspond to the number of adjacent flags, and if it is so, then we display all the adjacent tiles.
			    			else
			    				Chord(i,j);
			    			
			    			// We use the unblocker, if we are in unblocker mode
			    			ComputeAntiBlock();
	
			    			// We check if game has been won
			    			Game.CheckWin();
			    			if(Game.gameWon) {
			    				Won(true);
			    			}
			    		}
		    			// Right click
			    		else if(e.getButton() == MouseEvent.BUTTON3 && !Game.plottedGrid[i][j])
			    		{
			    			// Actions performed when right-clicking
			    			RightClick(i,j,false,true);
			    			
			    			// We use the unblocker, if we are in unblocker mode
			    			ComputeAntiBlock();
			    		}
		    		}
	    		}

	    	}

	    	@Override
	    	public void mouseReleased(MouseEvent e) {
	    		// TODO Auto-generated method stub

	    	}

	    	@Override
	    	public void mouseEntered(MouseEvent e) {
	    		// TODO Auto-generated method stub

	    	}

	    	@Override
	    	public void mouseExited(MouseEvent e) {
	    		// TODO Auto-generated method stub
	    		
	    	}
	    };
	    
	    // Menu bar
		MenuBar = new JMenuBar();
		// Options menu
		OptionMenu = new JMenu("Options");
		OptionMenu.setMnemonic(KeyEvent.VK_O);
		OptionMenu.setMaximumSize(OptionMenu.getPreferredSize());
		OptionMenu.addMenuListener(new MenuListener() {
            @Override
            public void menuSelected(MenuEvent e) {
            	// Actions performed when clicking options menu
                DisplayOptionMenu();

            }

            @Override
            public void menuDeselected(MenuEvent e) {
            	
            }

            @Override
            public void menuCanceled(MenuEvent e) {

            }
        });
		MenuBar.add(OptionMenu);
		// Options menu
		OFrame = new OptionsFrame();
		// Frame when using let it play mode
		LIPFrame = new LetItPlayChooserFrame();
		// Progress bar for let it play mode
		LIPPBFrame = new LetItPlayProgressBarFrame();
		// Number of games that will be played in let it play mode
		autoGamesNumber = 0;
		// Number of games that have already been played in let it play mode
		autoGamesCount = 0;
		
		// Leaderboard menu
		LeaderboardMenu = new JMenu("Leaderboards");
		LeaderboardMenu.setMnemonic(KeyEvent.VK_L);
		LeaderboardMenu.setMaximumSize(LeaderboardMenu.getPreferredSize());
		LeaderboardMenu.addMenuListener(new MenuListener() {
            @Override
            public void menuSelected(MenuEvent e) {
            	// Actions performed when clicking leaderboard menu
                DisplayLeaderboard();

            }

            @Override
            public void menuDeselected(MenuEvent e) {
            	
            }

            @Override
            public void menuCanceled(MenuEvent e) {

            }
        });
		MenuBar.add(LeaderboardMenu);
		// Frame when player does a hiscore
		HiscoreFrame = new HiscoreFrame();
		// Leaderboard frame
		LFrame = new LeaderboardFrame();
		
		this.setJMenuBar(MenuBar);
		
		
		
		// Main Panel, containing all the other elements
		MainPanel = new MinesweeperPanel(LightColor,DarkColor,3);
		MainPanel.setPreferredSize(new Dimension(Math.max(16*Game.horizontalSize + 24,16*minInfosWidth+24), 16*Game.verticalSize + 65));
		MainPanel.setBoundsandRetain(0,0,Math.max(16*Game.horizontalSize + 24,16*minInfosWidth+24), 16*Game.verticalSize + 65);
		MainPanel.centerPanel.setBackground(BackgroundColor);
		MainPanel.centerPanel.setOpaque(true);
		this.add(MainPanel);
		this.pack();
		
		// Panel used for keeping the elements centered if we change frame's size
		MoveablePanel = new JPanel();
		MoveablePanel.setBounds(0,0,Math.max(16*Game.horizontalSize + 24-6,16*minInfosWidth+24-6),16*Game.verticalSize + 65-6);
		MoveablePanel.setLayout(null);
		MoveablePanel.setBackground(BackgroundColor);
		MoveablePanel.setOpaque(true);
		MainPanel.centerPanel.add(MoveablePanel);
		
		// Info panel on top of the game, containing several informations about the game
		InfosPanel = new MinesweeperPanel(DarkColor,LightColor,3);
		InfosPanel.setBoundsandRetain(6,5,Math.max(16*Game.horizontalSize + 6,16*minInfosWidth+6), 38);
		InfosPanel.centerPanel.setBackground(BackgroundColor);
		InfosPanel.centerPanel.setOpaque(true);
		
			
			// Mine panel, with the number of mines left
			MinePanel = new MinesweeperPanel(DarkColor,LightColor,1);
			MinePanel.setBoundsandRetain(3,3,41,25);
		InfosPanel.centerPanel.add(MinePanel);
		
				// First number
				m1Panel = new JPanel();
				m1Panel.setBounds(0,0,13,23);
				m1Panel.setLayout(null);
					m1Label = new JLabel();
					m1Label.setBounds(0,0,13,23);
				m1Panel.add(m1Label);
			MinePanel.centerPanel.add(m1Panel);
				// Second number
				m2Panel = new JPanel();
				m2Panel.setBounds(13,0,13,23);
				m2Panel.setLayout(null);
					m2Label = new JLabel();
					m2Label.setBounds(0,0,13,23);
				m2Panel.add(m2Label);
			MinePanel.centerPanel.add(m2Panel);
				// Third number
				m3Panel = new JPanel();
				m3Panel.setBounds(26,0,13,23);
				m3Panel.setLayout(null);
					m3Label = new JLabel();
					m3Label.setBounds(0,0,13,23);
				m3Panel.add(m3Label);
			MinePanel.centerPanel.add(m3Panel);
		
		
			// Smiley panel, with the well-known smiley from minesweeper
			SmileyPanel = new MinesweeperPressablePanel(DarkColor, DarkColor, 1, 1, new ImageIcon("sprites\\Smile.png"), new ImageIcon("sprites\\Pressed_Smile.png"))
			 {
			    @Override
			    public void ActionWhenPressed() {
			    	if(helpParameter != 4)
			    		ResetGame(true,true);
			    }
			};
			SmileyPanel.setBoundsandRetain(Math.max(8*Game.horizontalSize - 13,8*minInfosWidth-13),3,26,26);
		InfosPanel.centerPanel.add(SmileyPanel);
			

			// Time panel, with the time left
			TimePanel = new MinesweeperPanel(DarkColor,LightColor,1);
			TimePanel.setBoundsandRetain(Math.max(16*Game.horizontalSize - 44,16*minInfosWidth-44),3,41,25);
		InfosPanel.centerPanel.add(TimePanel);
		
				// Timer
				timer = new Timer(1000,this);
				timer.start();
		
				// First number
				t1Panel = new JPanel();
				t1Panel.setBounds(0,0,13,23);
				t1Panel.setLayout(null);
					t1Label = new JLabel();
					t1Label.setBounds(0,0,13,23);
				t1Panel.add(t1Label);
			TimePanel.centerPanel.add(t1Panel);
				// Second number
				t2Panel = new JPanel();
				t2Panel.setBounds(13,0,13,23);
				t2Panel.setLayout(null);
					t2Label = new JLabel();
					t2Label.setBounds(0,0,13,23);
				t2Panel.add(t2Label);
			TimePanel.centerPanel.add(t2Panel);
				// Third number
				t3Panel = new JPanel();
				t3Panel.setBounds(26,0,13,23);
				t3Panel.setLayout(null);
					t3Label = new JLabel();
					t3Label.setBounds(0,0,13,23);
				t3Panel.add(t3Label);
			TimePanel.centerPanel.add(t3Panel);
			
			// Panel used for help mode, for requiring help
			HelpPanel = new MinesweeperPressablePanel(DarkColor, DarkColor, 1, 1, new ImageIcon("sprites\\Help_Button.png"), new ImageIcon("sprites\\Pressed_Help_Button.png"))
			 {
			    @Override
			    public void ActionWhenPressed() {
			    	// Actions performed when requiring help
					GetHelp();
					// Update displayed number of remaining help
					remainingHelpLabel.setText(Integer.toString(remainingHelp));
			    }
			};
			HelpPanel.setBoundsandRetain(Math.max(12*Game.horizontalSize - 29,12*minInfosWidth-29),3,26,26);
			if(helpParameter != 2)
				HelpPanel.setVisible(false);
			else
				HelpPanel.setVisible(false);
			
					remainingHelpLabel = new JLabel();
					remainingHelpLabel.setBounds(3,2,18,8);
					remainingHelpLabel.setHorizontalAlignment(JLabel.RIGHT);
					remainingHelpLabel.setVerticalAlignment(JLabel.CENTER);
				HelpPanel.Label.add(remainingHelpLabel);
				
		InfosPanel.centerPanel.add(HelpPanel);
		
			// Spacing between panels in bot mode
		
			int botPanelSpacing = Math.max((8*Game.horizontalSize-109)/3,(8*minInfosWidth-109)/3);
			
			Font testFont = new Font("Arial", Font.PLAIN, 7);
		
			// Test Panel
			Test1Panel = new MinesweeperPressablePanel(DarkColor, DarkColor, 1, 1, new ImageIcon("sprites\\Empty_Button.png"), new ImageIcon("sprites\\Pressed_Empty_Button.png"))
			 {
			    @Override
			    public void ActionWhenPressed() {
			    	// Actions performed when pressing Test1Panel
			    	Test1();
			    }
			};
			Test1Panel.setBoundsandRetain(Math.max(44 + botPanelSpacing,44 + botPanelSpacing),3,26,26);
			
				Test1Label = new JLabel();
				Test1Label.setBounds(2,7,20,10);
				Test1Label.setFont(testFont);
				Test1Label.setForeground(Color.black);
				Test1Label.setText("Test 1");
				Test1Label.setHorizontalAlignment(JLabel.CENTER);
				Test1Label.setVerticalAlignment(JLabel.CENTER);
			Test1Panel.Label.add(Test1Label);
			if(helpParameter != 3)
				Test1Panel.setVisible(false);
			else
				Test1Panel.setVisible(false);
//		InfosPanel.centerPanel.add(Test1Panel);
		
//		// Test Panel
//		Test2Panel = new MinesweeperPressablePanel(DarkColor, DarkColor, 1, 1, new ImageIcon("sprites\\Empty_Button.png"), new ImageIcon("sprites\\Pressed_Empty_Button.png"))
//		 {
//		    @Override
//		    public void ActionWhenPressed() {
//		    	// Actions performed when pressing Test2Panel
//		    	Test2();
//		    }
//		};
//		Test2Panel.setBoundsandRetain(Math.max(8*Game.horizontalSize - 39 - botPanelSpacing,8*minInfosWidth - 39 - botPanelSpacing),3,26,26);
//		
//			Test2Label = new JLabel();
//			Test2Label.setBounds(2,7,20,10);
//			Test2Label.setFont(testFont);
//			Test2Label.setForeground(Color.black);
//			Test2Label.setText("Test 2");
//			Test2Label.setHorizontalAlignment(JLabel.CENTER);
//			Test2Label.setVerticalAlignment(JLabel.CENTER);
//		Test2Panel.Label.add(Test2Label);
//		if(helpParameter != 3)
//			Test2Panel.setVisible(false);
//		else
//			Test2Panel.setVisible(false);
//	InfosPanel.centerPanel.add(Test2Panel);
			
			// Panel for displaying the probabilities of each tile to be a mine
			ProbabilitiesPanel = new MinesweeperPressablePanel(DarkColor, DarkColor, 1, 1, new ImageIcon("sprites\\Probabilities_Button.png"), new ImageIcon("sprites\\Pressed_Probabilities_Button.png"))
			 {
			    @Override
			    public void ActionWhenPressed() {
			    	// Actions performed when pressing Test2Panel
			    	DisplayProbabilities();
			    }
			};
			ProbabilitiesPanel.setBoundsandRetain(Math.max(8*Game.horizontalSize - 39 - botPanelSpacing,8*minInfosWidth - 39 - botPanelSpacing),3,26,26);

			if(helpParameter != 3)
				ProbabilitiesPanel.setVisible(false);
			else
				ProbabilitiesPanel.setVisible(false);
			InfosPanel.centerPanel.add(ProbabilitiesPanel);
			probabilitiesDisplayed = false;
			
			// Panel used for step by step mode, for playing next bot's step
			NextBotStepPanel = new MinesweeperPressablePanel(DarkColor, DarkColor, 1, 1, new ImageIcon("sprites\\Next_Bot_Step_Button.png"), new ImageIcon("sprites\\Pressed_Next_Bot_Step_Button.png"))
			 {
			    @Override
			    public void ActionWhenPressed() {
			    	PlayNextBotStep(true,true);
			    }
			};
			NextBotStepPanel.setBoundsandRetain(Math.max(8*Game.horizontalSize + 13 + botPanelSpacing,8*minInfosWidth + 13 + botPanelSpacing),3,26,26);
			if(helpParameter != 3)
				NextBotStepPanel.setVisible(false);
			else
				NextBotStepPanel.setVisible(false);
		InfosPanel.centerPanel.add(NextBotStepPanel);
		
			// Panel used for step by step mode. When pressed, bot plays moves until the game is finished.
			FinishBotPanel = new MinesweeperPressablePanel(DarkColor, DarkColor, 1, 1, new ImageIcon("sprites\\Bot_Finish_Button.png"), new ImageIcon("sprites\\Pressed_Bot_Finish_Button.png"))
			 {
			    @Override
			    public void ActionWhenPressed() {
			    	BotFinishGame(true);
			    }
			};
			FinishBotPanel.setBoundsandRetain(Math.max(16*Game.horizontalSize - 70 - botPanelSpacing,16*minInfosWidth - 70 - botPanelSpacing),3,26,26);
			if(helpParameter != 3)
				FinishBotPanel.setVisible(false);
			else
				FinishBotPanel.setVisible(false);
		InfosPanel.centerPanel.add(FinishBotPanel);
	
		MoveablePanel.add(InfosPanel);
		
		
			
		// Game's Panel, containing all the tiles
		GamePanel = new MinesweeperPanel(DarkColor,LightColor,3);
		GamePanel.setBoundsandRetain(6+InfosPanel.BaseWidth/2-(16*Game.horizontalSize+6)/2,48,16*Game.horizontalSize + 6,16*Game.verticalSize + 6);
		GamePanel.centerPanel.setBackground(BackgroundColor);
		GamePanel.centerPanel.setOpaque(true);
		GamePanel.centerPanel.addMouseListener(GameML);
		MoveablePanel.add(GamePanel);
		
			// Point where we clicked the mouse
			GameClickPt = new Point();
		
		
			// Used when we reset the game, for knowing which tiles we must reset. For displaying speed.
			rescaleGrid = new boolean[Game.horizontalSize][Game.verticalSize];
			for(int i = 0; i<Game.horizontalSize; ++i)
			{
				for(int j = 0; j<Game.verticalSize; ++j)
				{
					rescaleGrid[i][j] = true;
				}
			}
			// Display every tile
			TileLabels = new JLabel[Game.horizontalSize*Game.verticalSize];
			TileIcons = new ImageIcon[Game.horizontalSize*Game.verticalSize];
			for(int i = 0; i<horizontalSize;++i)
			{
				for(int j = 0; j<verticalSize; ++j)
				{
					TileLabels[j*Game.horizontalSize+i] = new JLabel();
					TileLabels[j*Game.horizontalSize+i].setBounds(16*i,16*j,16,16);
					TileIcons[j*Game.horizontalSize+i] = new ImageIcon("sprites\\Tile.png");
					TileLabels[j*Game.horizontalSize+i].setIcon(TileIcons[j*Game.horizontalSize+i]);
					// For displaying probabilities of each tile to be a mine
					TileLabels[j*Game.horizontalSize+i].setFont(new Font("Mangal", Font.BOLD, Math.min((int) (6*scalingFactor), 18)));
					TileLabels[j*Game.horizontalSize+i].setForeground(Color.black);
					TileLabels[j*Game.horizontalSize+i].setHorizontalTextPosition(JLabel.CENTER);
					TileLabels[j*Game.horizontalSize+i].setVerticalTextPosition(JLabel.CENTER);
					GamePanel.centerPanel.add(TileLabels[j*Game.horizontalSize+i]);
				}
			}
			
		// Leaderboard frame
		LFrame = new LeaderboardFrame();
		
		
		
		// Create the game
		CreateGame();
		// Instantiate the helper, used for unblocker and help modes
		Helper = new MinesweeperHelper(Game);
		// Instantiate the solver, used for step by step and let it play modes
		Solver = new MinesweeperSolver(Game);
		
				
		this.pack();
		this.addComponentListener(this);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
		
		
	}
	
	
	
	//---------------------------------------Displaying---------------------------------------
	
	
	
	// Display number of mines left
	public void DisplayminesLeft() {
		
		// Number of mines left, with good format
		minesString = String.format("%03d",Game.minesLeft);
		// First number
		m1Icon = new ImageIcon("sprites\\"+minesString.charAt(0)+".png");
		m1Icon = new ImageIcon(m1Icon.getImage().getScaledInstance(scalingFactor*13,scalingFactor*23, Image.SCALE_SMOOTH));
		m1Label.setIcon(m1Icon);
		// Second number
		m2Icon = new ImageIcon("sprites\\"+minesString.charAt(1)+".png");
		m2Icon = new ImageIcon(m2Icon.getImage().getScaledInstance(scalingFactor*13,scalingFactor*23, Image.SCALE_SMOOTH));
		m2Label.setIcon(m2Icon);
		// Third number
		m3Icon = new ImageIcon("sprites\\"+minesString.charAt(2)+".png");
		m3Icon = new ImageIcon(m3Icon.getImage().getScaledInstance(scalingFactor*13,scalingFactor*23, Image.SCALE_SMOOTH));
		m3Label.setIcon(m3Icon);
	}

	// Display time passed
	public void DisplayTimePassed() {
		
		// Time passed, with good format
		timeString = String.format("%03d",Game.timePassed);
		// First number
		t1Icon = new ImageIcon("sprites\\"+timeString.charAt(0)+".png");
		t1Icon = new ImageIcon(t1Icon.getImage().getScaledInstance(scalingFactor*13,scalingFactor*23, Image.SCALE_SMOOTH));
		t1Label.setIcon(t1Icon);
		// Second number
		t2Icon = new ImageIcon("sprites\\"+timeString.charAt(1)+".png");
		t2Icon = new ImageIcon(t2Icon.getImage().getScaledInstance(scalingFactor*13,scalingFactor*23, Image.SCALE_SMOOTH));
		t2Label.setIcon(t2Icon);
		// Third number
		t3Icon = new ImageIcon("sprites\\"+timeString.charAt(2)+".png");
		t3Icon = new ImageIcon(t3Icon.getImage().getScaledInstance(scalingFactor*13,scalingFactor*23, Image.SCALE_SMOOTH));
		t3Label.setIcon(t3Icon);
		
	}
	
	// Center everything when window is resized
	public void Center() {
		
		MoveablePanel.setLocation(MainPanel.centerPanel.getWidth()/2-MoveablePanel.getWidth()/2, MainPanel.centerPanel.getHeight()/2-MoveablePanel.getHeight()/2);
		
	}
	
	// Rescale everything. Used when game's dimensions are changed
	public void Rescale(boolean sameSize) {
		
		// We retain the bounds of some elements so we know how to scale them for the next times
		MainPanel.setPreferredSize(new Dimension(Math.max(16*Game.horizontalSize + 24,16*minInfosWidth+24), 16*Game.verticalSize + 65));
		MainPanel.setBoundsandRetain(0,0,Math.max(16*Game.horizontalSize + 24,16*minInfosWidth+24), 16*Game.verticalSize + 65);
		InfosPanel.setBoundsandRetain(6,5,Math.max(16*Game.horizontalSize + 6,16*minInfosWidth+6), 38);
		SmileyPanel.setBoundsandRetain(Math.max(8*Game.horizontalSize - 13,8*minInfosWidth-13),3,26,26);
		TimePanel.setBoundsandRetain(Math.max(16*Game.horizontalSize - 44,16*minInfosWidth-44),3,41,25);
		HelpPanel.setBoundsandRetain(Math.max(12*Game.horizontalSize - 29,12*minInfosWidth-29),3,26,26);
		int botPanelSpacing = Math.max((8*Game.horizontalSize-109)/3,(8*minInfosWidth-109)/3);
		Test1Panel.setBoundsandRetain(Math.max(44 + botPanelSpacing,44 + botPanelSpacing),3,26,26);
//		Test2Panel.setBoundsandRetain(Math.max(8*Game.horizontalSize - 39 - botPanelSpacing,8*minInfosWidth - 39 - botPanelSpacing),3,26,26);
		ProbabilitiesPanel.setBoundsandRetain(Math.max(8*Game.horizontalSize - 39 - botPanelSpacing,8*minInfosWidth - 39 - botPanelSpacing),3,26,26);
		NextBotStepPanel.setBoundsandRetain(Math.max(8*Game.horizontalSize + 13 + botPanelSpacing,8*minInfosWidth + 13 + botPanelSpacing),3,26,26);
		FinishBotPanel.setBoundsandRetain(Math.max(16*Game.horizontalSize - 70 - botPanelSpacing,16*minInfosWidth - 70 - botPanelSpacing),3,26,26);
		GamePanel.setBoundsandRetain(6+InfosPanel.BaseWidth/2-(16*Game.horizontalSize+6)/2,48,16*Game.horizontalSize + 6,16*Game.verticalSize + 6);
		
		

		// We rescale everything
		MainPanel.Scale(scalingFactor);
		MoveablePanel.setBounds(0,0,Math.max(scalingFactor*(16*Game.horizontalSize + 24-6),scalingFactor*(16*minInfosWidth + 24-6)),scalingFactor*(16*Game.verticalSize + 65-6));
		InfosPanel.Scale(scalingFactor);
		MinePanel.Scale(scalingFactor);
			m1Panel.setBounds(scalingFactor*0, scalingFactor*0, scalingFactor*13, scalingFactor*23);
				m1Label.setBounds(scalingFactor*0, scalingFactor*0, scalingFactor*13, scalingFactor*23);
				m1Label.setIcon(m1Icon);
			m2Panel.setBounds(scalingFactor*13, scalingFactor*0, scalingFactor*13, scalingFactor*23);
				m2Label.setBounds(scalingFactor*0, scalingFactor*0, scalingFactor*13, scalingFactor*23);
				m2Label.setIcon(m2Icon);
			m3Panel.setBounds(scalingFactor*26, scalingFactor*0, scalingFactor*13, scalingFactor*23);
				m3Label.setBounds(scalingFactor*0, scalingFactor*0, scalingFactor*13, scalingFactor*23);
				m3Label.setIcon(m3Icon);
		SmileyPanel.Scale(scalingFactor);
		TimePanel.Scale(scalingFactor);
			t1Panel.setBounds(scalingFactor*0, scalingFactor*0, scalingFactor*13, scalingFactor*23);
				t1Label.setBounds(scalingFactor*0, scalingFactor*0, scalingFactor*13, scalingFactor*23);
				t1Label.setIcon(t1Icon);
			t2Panel.setBounds(scalingFactor*13, scalingFactor*0, scalingFactor*13, scalingFactor*23);
				t2Label.setBounds(scalingFactor*0, scalingFactor*0, scalingFactor*13, scalingFactor*23);
				t2Label.setIcon(t2Icon);
			t3Panel.setBounds(scalingFactor*26, scalingFactor*0, scalingFactor*13, scalingFactor*23);
				t3Label.setBounds(scalingFactor*0, scalingFactor*0, scalingFactor*13, scalingFactor*23);
				t3Label.setIcon(t3Icon);
		HelpPanel.Scale(scalingFactor);
			remainingHelpLabel.setBounds(scalingFactor*3,scalingFactor*2,scalingFactor*18,scalingFactor*8);
			remainingHelpLabel.setFont(new Font("Verdana",Font.BOLD,8*scalingFactor));
		Font testFont = new Font("Arial", Font.PLAIN, 7*scalingFactor);
		Test1Panel.Scale(scalingFactor);
			Test1Label.setBounds(scalingFactor*2,scalingFactor*7,scalingFactor*20,scalingFactor*10);
			Test1Label.setFont(testFont);
//		Test2Panel.Scale(scalingFactor);
//			Test2Label.setBounds(scalingFactor*2,scalingFactor*7,scalingFactor*20,scalingFactor*10);
//			Test2Label.setFont(testFont);
		ProbabilitiesPanel.Scale(scalingFactor);
		NextBotStepPanel.Scale(scalingFactor);
		FinishBotPanel.Scale(scalingFactor);
		GamePanel.Scale(scalingFactor);
			for(int i = 0; i<Game.horizontalSize; ++i)
			{
				for(int j = 0; j<Game.verticalSize; ++j)
				{
					TileLabels[j*Game.horizontalSize+i].setFont(new Font("Mangal", Font.BOLD, Math.min((int) (6*scalingFactor), 18)));
					TileLabels[j*Game.horizontalSize+i].setForeground(Color.black);
					TileLabels[j*Game.horizontalSize+i].setHorizontalTextPosition(JLabel.CENTER);
					TileLabels[j*Game.horizontalSize+i].setVerticalTextPosition(JLabel.CENTER);
					if(rescaleGrid[i][j]) 
					{
						TileLabels[j*Game.horizontalSize+i].setBounds(scalingFactor*16*i, scalingFactor*16*j, scalingFactor*16, scalingFactor*16);
						TileIcons[j*Game.horizontalSize+i] = new ImageIcon(TileIcons[j*Game.horizontalSize+i].getImage().getScaledInstance(scalingFactor*TileIcons[j*Game.horizontalSize+i].getIconWidth(),scalingFactor*TileIcons[j*Game.horizontalSize+i].getIconHeight(), Image.SCALE_SMOOTH));
						TileLabels[j*Game.horizontalSize+i].setIcon(TileIcons[j*Game.horizontalSize+i]);
					}
				}
			}
			
		MainPanel.setPreferredSize(new Dimension(MainPanel.getWidth(), MainPanel.getHeight()));
	
		// If window is full-screen, it remains full-screen. Otherwise, we resize it.
		if(this.getExtendedState() != JFrame.MAXIMIZED_BOTH)
		{
			this.pack();
			// We center the window only if we changed the size.
			if(!sameSize)
				this.setLocationRelativeTo(null);
		}
	    this.setVisible(true);
	}
	
	// Displays a game. Used for let it play mode.
	public void DisplayGame(MinesweeperGame Game) {
		
		// Number of mines left, with good format
		String minesString = String.format("%03d",Game.minesLeft);
		// First number
		m1Icon = new ImageIcon("sprites\\"+minesString.charAt(0)+".png");
		m1Icon = new ImageIcon(m1Icon.getImage().getScaledInstance(scalingFactor*13,scalingFactor*23, Image.SCALE_SMOOTH));
		m1Label.setIcon(m1Icon);
		// Second number
		m2Icon = new ImageIcon("sprites\\"+minesString.charAt(1)+".png");
		m2Icon = new ImageIcon(m2Icon.getImage().getScaledInstance(scalingFactor*13,scalingFactor*23, Image.SCALE_SMOOTH));
		m2Label.setIcon(m2Icon);
		// Third number
		m3Icon = new ImageIcon("sprites\\"+minesString.charAt(2)+".png");
		m3Icon = new ImageIcon(m3Icon.getImage().getScaledInstance(scalingFactor*13,scalingFactor*23, Image.SCALE_SMOOTH));
		m3Label.setIcon(m3Icon);
		// Update smiley
		if(Game.gameLost) 
		{
			SmileyPanel.ChangeUnpressedImageIcon(new ImageIcon("sprites\\Dead.png"), scalingFactor);
			SmileyPanel.ChangePressedImageIcon(new ImageIcon("sprites\\Pressed_Dead.png"), scalingFactor);
		}
		else if(Game.gameWon)
		{
			SmileyPanel.ChangeUnpressedImageIcon(new ImageIcon("sprites\\Win.png"), scalingFactor);
			SmileyPanel.ChangePressedImageIcon(new ImageIcon("sprites\\Pressed_Win.png"), scalingFactor);
		}
		else
		{
			SmileyPanel.ChangeUnpressedImageIcon(new ImageIcon("sprites\\Smile.png"), scalingFactor);
			SmileyPanel.ChangePressedImageIcon(new ImageIcon("sprites\\Pressed_Smile.png"), scalingFactor);
		}
		// Time passed, with good format
		String timeString = String.format("%03d",Game.timePassed);
		// First number
		t1Icon = new ImageIcon("sprites\\"+timeString.charAt(0)+".png");
		t1Icon = new ImageIcon(t1Icon.getImage().getScaledInstance(scalingFactor*13,scalingFactor*23, Image.SCALE_SMOOTH));
		t1Label.setIcon(t1Icon);
		// Second number
		t2Icon = new ImageIcon("sprites\\"+timeString.charAt(1)+".png");
		t2Icon = new ImageIcon(t2Icon.getImage().getScaledInstance(scalingFactor*13,scalingFactor*23, Image.SCALE_SMOOTH));
		t2Label.setIcon(t2Icon);
		// Third number
		t3Icon = new ImageIcon("sprites\\"+timeString.charAt(2)+".png");
		t3Icon = new ImageIcon(t3Icon.getImage().getScaledInstance(scalingFactor*13,scalingFactor*23, Image.SCALE_SMOOTH));
		t3Label.setIcon(t3Icon);
		
		// Update tiles
		for(int i = 0; i<Game.horizontalSize; ++i)
		{
			for(int j = 0; j<Game.verticalSize; ++j)
			{
				// If tile is plotted and is not a mine, we display its value
				if(Game.plottedGrid[i][j] && Game.grid[i][j] != -1)
				{
					switch(Game.grid[i][j])
					{
						case(0): TileIcons[j*Game.horizontalSize+i] = new ImageIcon("sprites\\Pressed_tile.png"); break;
						case(1): TileIcons[j*Game.horizontalSize+i] = new ImageIcon("sprites\\Mine_1.png"); break;
						case(2): TileIcons[j*Game.horizontalSize+i] = new ImageIcon("sprites\\Mine_2.png"); break;
						case(3): TileIcons[j*Game.horizontalSize+i] = new ImageIcon("sprites\\Mine_3.png"); break;
						case(4): TileIcons[j*Game.horizontalSize+i] = new ImageIcon("sprites\\Mine_4.png"); break;
						case(5): TileIcons[j*Game.horizontalSize+i] = new ImageIcon("sprites\\Mine_5.png"); break;
						case(6): TileIcons[j*Game.horizontalSize+i] = new ImageIcon("sprites\\Mine_6.png"); break;
						case(7): TileIcons[j*Game.horizontalSize+i] = new ImageIcon("sprites\\Mine_7.png"); break;
						case(8): TileIcons[j*Game.horizontalSize+i] = new ImageIcon("sprites\\Mine_8.png"); break;
						default: break;
					}
				}
				// We display the flags
				else if(Game.guessGrid[i][j] == 1 || (Game.gameWon && Game.grid[i][j] == -1))
				{
					TileIcons[j*Game.horizontalSize+i] = new ImageIcon("sprites\\Flag.png");
				}
				// We display the mines
				else if(Game.gameLost && Game.grid[i][j] == -1)
				{
					// If it is the mine that killed us, we display a killing mine
					if(i == Game.losti && j == Game.lostj)
						TileIcons[j*Game.horizontalSize+i] = new ImageIcon("sprites\\Killing_Mine.png");
					// Otherwise, we display of normal mine
					else
						TileIcons[j*Game.horizontalSize+i] = new ImageIcon("sprites\\Mine.png");
				}
				// If there is nothing, we display nothing
				else
				{
					TileIcons[j*Game.horizontalSize+i] = new ImageIcon("sprites\\Tile.png");
				}
				TileIcons[j*Game.horizontalSize+i] = new ImageIcon(TileIcons[j*Game.horizontalSize+i].getImage().getScaledInstance(scalingFactor*TileIcons[j*Game.horizontalSize+i].getIconWidth(),scalingFactor*TileIcons[j*Game.horizontalSize+i].getIconHeight(), Image.SCALE_SMOOTH));
				TileLabels[j*Game.horizontalSize+i].setIcon(TileIcons[j*Game.horizontalSize+i]);
			}
		}
	}
	
	// Display option's menu
	public void DisplayOptionMenu() {
		
		OFrame = new OptionsFrame(this);
		this.setEnabled(false);
		OFrame.addWindowListener(new java.awt.event.WindowAdapter() {
		    @Override
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
		        OFrame.Close();
		    }
		});
		OFrame.SubmitButton.addActionListener(this);
	}
	
	// Display leaderboards
	public void DisplayLeaderboard() {
		
		LFrame = new LeaderboardFrame(this, difficulty, ranking);
		this.setEnabled(false);
		LFrame.addWindowListener(new java.awt.event.WindowAdapter() {
		    @Override
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
		        LFrame.Close();
		        ranking = -1;
		    }
		});
		LFrame.ComboBox.addActionListener(this);
	}
	
	
	
	//---------------------------------------Clicking-----------------------------------------
	
	
	
	// Displays a tile, depending on its value
	// In many of the following functions, there will be a display argument. It determines whether we must display thins at the screen or not. 
	// It is useful for the let it play mode, where we don't display anything.
	public void DisplayTile(int i, int j, boolean display) {
		
		Game.plottedGrid[i][j] = true;
		Game.plotNumber++;
		Game.guessGrid[i][j] = 0;
		
		if(display)
		{
			switch(Game.grid[i][j])
			{
				case(-1): TileIcons[j*Game.horizontalSize+i] = new ImageIcon("sprites\\Mine.png"); break;
				case(0): TileIcons[j*Game.horizontalSize+i] = new ImageIcon("sprites\\Pressed_tile.png"); break;
				case(1): TileIcons[j*Game.horizontalSize+i] = new ImageIcon("sprites\\Mine_1.png"); break;
				case(2): TileIcons[j*Game.horizontalSize+i] = new ImageIcon("sprites\\Mine_2.png"); break;
				case(3): TileIcons[j*Game.horizontalSize+i] = new ImageIcon("sprites\\Mine_3.png"); break;
				case(4): TileIcons[j*Game.horizontalSize+i] = new ImageIcon("sprites\\Mine_4.png"); break;
				case(5): TileIcons[j*Game.horizontalSize+i] = new ImageIcon("sprites\\Mine_5.png"); break;
				case(6): TileIcons[j*Game.horizontalSize+i] = new ImageIcon("sprites\\Mine_6.png"); break;
				case(7): TileIcons[j*Game.horizontalSize+i] = new ImageIcon("sprites\\Mine_7.png"); break;
				case(8): TileIcons[j*Game.horizontalSize+i] = new ImageIcon("sprites\\Mine_8.png"); break;
				default: break;
			}
			
			TileIcons[j*Game.horizontalSize+i] = new ImageIcon(TileIcons[j*Game.horizontalSize+i].getImage().getScaledInstance(scalingFactor*TileIcons[j*Game.horizontalSize+i].getIconWidth(),scalingFactor*TileIcons[j*Game.horizontalSize+i].getIconHeight(), Image.SCALE_SMOOTH));
			TileLabels[j*Game.horizontalSize+i].setIcon(TileIcons[j*Game.horizontalSize+i]);
		}
		
	}
	
	// List of actions done when the player presses a tile. Is recursive to display neighboring tiles if he hits a tile with zero nearby mines
	public void PressTile(int i, int j, boolean display)
	{
		if(Game.guessGrid[i][j] != 1)
		{
			// If the tile is a mine, then the game is lost
			if(Game.grid[i][j] == -1) {
				Lost(i,j,display);
			}
			// Otherwise, we call the previous function
			else if(!Game.plottedGrid[i][j])
			{
				Game.firstClick = false;
				DisplayTile(i,j,display);
				// If tile is empty, we display all neighboring tiles
				if(Game.grid[i][j] == 0)
				{
					Game.plottedEmpty = true;
					for(int ati = Math.max(i-1,0); ati <= Math.min(i+1, Game.horizontalSize-1); ++ati)
					{
						for(int atj = Math.max(j-1,0); atj <= Math.min(j+1, Game.verticalSize-1); ++atj) {
							{
								PressTile(ati,atj,display);
							}
						}
					}
				}
			}
		}
		
	}
	
	// When a player clicks an uncovered tile, to uncover all adjacent tiles
	public void Chord(int i, int j) {
		
		if(Game.guessGrid[i][j] != 1)
		{
			// Count the number of neighbor tiles that have a flag
			int GuessedTiles = 0;
			for(int ati = Math.max(i-1,0); ati <= Math.min(i+1, Game.horizontalSize-1); ++ati)
			{
				for(int atj = Math.max(j-1,0); atj <= Math.min(j+1, Game.verticalSize-1); ++atj) {
					{
						if(!(ati == i && atj == j) && Game.guessGrid[ati][atj] == 1)
						{
							GuessedTiles++;
						}
					}
				}
			}
			// If this number corresponds to the tile's value, then we display all the neighboring tiles.
			if(GuessedTiles == Game.grid[i][j])
			{
				PressTile(i,j,true);
				for(int ati = Math.max(i-1,0); ati <= Math.min(i+1, Game.horizontalSize-1); ++ati)
				{
					for(int atj = Math.max(j-1,0); atj <= Math.min(j+1, Game.verticalSize-1); ++atj) {
						{
							if(!(ati == i && atj == j) && Game.guessGrid[ati][atj] != 1)
							{
								PressTile(ati,atj,true);
							}
						}
					}
				}
			}
		}
	}
	
	// When the player does a right-click
	public void RightClick(int i, int j, boolean botClick, boolean display) {
		
		// If empty tile, we set a flag
		if(Game.guessGrid[i][j] == 0)
		{
			Game.guessGrid[i][j] = 1; 
			Game.minesLeft--;
			if(display)
			{
				if(!botClick)
					TileIcons[j*Game.horizontalSize+i] = new ImageIcon("sprites\\Flag.png");
				else
					TileIcons[j*Game.horizontalSize+i] = new ImageIcon("sprites\\Flag_bot.png");
				TileIcons[j*Game.horizontalSize+i] = new ImageIcon(TileIcons[j*Game.horizontalSize+i].getImage().getScaledInstance(scalingFactor*16,scalingFactor*16, Image.SCALE_SMOOTH));
				TileLabels[j*Game.horizontalSize+i].setIcon(TileIcons[j*Game.horizontalSize+i]);
				DisplayminesLeft();
			}
		}
		// If there is a flag, we set an interrogation mark
		else if(Game.guessGrid[i][j] == 1 && !Helper.helpedTiles[i][j])
		{
			Game.guessGrid[i][j] = 2;
			Game.minesLeft++;
			Helper.correctedTiles[i][j] = false;
			if(display)
			{
				TileIcons[j*Game.horizontalSize+i] = new ImageIcon("sprites\\Interrogation.png");
				TileIcons[j*Game.horizontalSize+i] = new ImageIcon(TileIcons[j*Game.horizontalSize+i].getImage().getScaledInstance(scalingFactor*16,scalingFactor*16, Image.SCALE_SMOOTH));
				TileLabels[j*Game.horizontalSize+i].setIcon(TileIcons[j*Game.horizontalSize+i]);
				DisplayminesLeft();
			}
		}
		// If there is an interrogation mark, we set it empty
		else if(Game.guessGrid[i][j] == 2)
		{
			Game.guessGrid[i][j] = 0;
			if(display)
			{
				TileIcons[j*Game.horizontalSize+i] = new ImageIcon("sprites\\Tile.png");
				TileIcons[j*Game.horizontalSize+i] = new ImageIcon(TileIcons[j*Game.horizontalSize+i].getImage().getScaledInstance(scalingFactor*16,scalingFactor*16, Image.SCALE_SMOOTH));
				TileLabels[j*Game.horizontalSize+i].setIcon(TileIcons[j*Game.horizontalSize+i]);
			}
		}
	}
	
	// Forces a tile to get a flag, for when the player wins
	public void ForceFlag(int i, int j, boolean display)
	{
		Game.guessGrid[i][j] = 1; 
		Game.minesLeft--;
		if(display)
		{
			TileIcons[j*Game.horizontalSize+i] = new ImageIcon("sprites\\Flag.png");
			TileIcons[j*Game.horizontalSize+i] = new ImageIcon(TileIcons[j*Game.horizontalSize+i].getImage().getScaledInstance(scalingFactor*16,scalingFactor*16, Image.SCALE_SMOOTH));
			TileLabels[j*Game.horizontalSize+i].setIcon(TileIcons[j*Game.horizontalSize+i]);
			DisplayminesLeft();
		}
	}
	
	
	
	// -------------------------------Helper-and-Solver-Methods--------------------------
	
	
	
	// Gives help to the player if solver detects no 100% sure tile. Only used in unblocker mode.
	public void ComputeAntiBlock() {
		
		if(helpParameter == 1)
		{
			// Only works if the player made no mistake (i.e. didn't mark a safe tile with a flag), and after at least 5 tiles have been uncovered
			if(Game.plotNumber>=5 && !Game.HasMistake())
			{
				Solver.ComputeActions();
				// If no 100% sure tile is detected, then the helper helps the player, with a tile that gives him the most informations
				while(Solver.getGroupsNumber() > 0 && Solver.actionsNumber == 0)
				{
					int[] chosenTile = Helper.GetHelpMine();
					RightClick(chosenTile[0], chosenTile[1], true, true);
					Solver.ComputeActions();
				}
			}
		}
	}
	
	// Gives the player some help if he requires it. Only used in help mode.
	public void GetHelp() {
		
		// Checks first if there is help remaining, and if it is the player's first click
		if(remainingHelp > 0 && !Game.firstClick)
		{
			// If the game has no mistake, then the helper helps the player, with a tile that gives him the most informations
			if(!Game.HasMistake())
			{
				Solver.ComputeActions();
				int[] chosenTile = Helper.GetHelpMine();
				if(Solver.getGroupsNumber() > 0 && chosenTile[0] != -1)
				{
					remainingHelp--;
					RightClick(chosenTile[0], chosenTile[1], true, true);
				}
			}
			// If there is a mistake, then the helper corrects one mistake randomly.
			else
			{
				int[] chosenTile = Helper.CorrectMistake();
				// If every mistake has already been corrected, does nothing
				if(chosenTile[0] != -1)
				{
					remainingHelp--;
					TileIcons[chosenTile[1]*Game.horizontalSize+chosenTile[0]] = new ImageIcon("sprites\\Corrected_Flag.png");
					TileIcons[chosenTile[1]*Game.horizontalSize+chosenTile[0]] = new ImageIcon(TileIcons[chosenTile[1]*Game.horizontalSize+chosenTile[0]].getImage().getScaledInstance(scalingFactor*16,scalingFactor*16, Image.SCALE_SMOOTH));
					TileLabels[chosenTile[1]*Game.horizontalSize+chosenTile[0]].setIcon(TileIcons[chosenTile[1]*Game.horizontalSize+chosenTile[0]]);
				}
			}
		}
	}
	
	// Do the actions computed by the solver
	public void DoActions(int[][] actions, boolean stepByStep, boolean display) {
		
		for(int ti = 0; ti<horizontalSize; ++ti)
		{
			for(int tj = 0; tj<verticalSize; ++tj)
			{
				if(actions[ti][tj] == 1)
				{
					Game.GenerateGrid(ti, tj, mines);
					PressTile(ti,tj,display);
				}
				else if(actions[ti][tj] == 2)
				{
					RightClick(ti,tj,false,display);
				}
			}
		}
	}
	
	public void DisplayProbabilities() {
		
		probabilitiesDisplayed = true;
		if(!Game.gameWon && !Game.gameLost)
		{
			// We compute the probabilities of each tile to be a mine, just a in MinesweeperSolver's GuessNextMineAccurate method
			Solver.ComputeActions();
			Solver.ComputeDiscoveredTiles();
			boolean[][] emptyTiles = new boolean[Game.horizontalSize][Game.verticalSize];
			for(int i = 0; i<Game.horizontalSize; ++i)
			{
				for(int j= 0; j<Game.verticalSize; ++j)
				{
					emptyTiles[i][j] = false;
				}
			}
			boolean[][] minedTiles = new boolean[Game.horizontalSize][Game.verticalSize];
			for(int i = 0; i<Game.horizontalSize; ++i)
			{
				for(int j= 0; j<Game.verticalSize; ++j)
				{
					minedTiles[i][j] = false;
				}
			}
			
			// We computed the number of all the mine disposition, separating them depending on the number of mines they have.
			MinesweeperPossibilities accuratePossibilities = Solver.ComputeMinePossibilitiesAccurate(emptyTiles, minedTiles, 0, false, guessingTileCount);
			// Number of mines that are unknown and not candidate
			int notCandidateNumber = 0;
		    for(int i = 0; i<Game.horizontalSize; ++i)
		    {
		    	for(int j = 0; j<Game.verticalSize; ++j)
		    	{
		    		if(Game.Unkown(i,j) && !Solver.candidateTiles[i][j])
		    		{
		    			notCandidateNumber++;
		    		}
		    	}
		    }
		    // We deduce the probability
		    double[][] mineProbabilities = Solver.DeduceProbabilitiesAccurate(accuratePossibilities,notCandidateNumber,Game.minesLeft);
			// We display all of these probabilities
			for(int i = 0; i<Game.horizontalSize; ++i)
			{
				for(int j = 0; j<Game.verticalSize; ++j)
				{
					if(Solver.candidateTiles[i][j])
					{
						double proba = 100*mineProbabilities[i][j];
						// If the value we display is an integer, we display it without comma
						if(Math.abs(proba-Math.round(proba)) <= Math.pow(10,-6))
							TileLabels[j*Game.horizontalSize+i].setText(String.format("%.0f",proba));
						else
							TileLabels[j*Game.horizontalSize+i].setText(String.format("%.1f",proba));
						if(mineProbabilities[i][j] == 0)
						{
							TileLabels[j*Game.horizontalSize+i].setForeground(new Color(45,159,45));
						}
						if(mineProbabilities[i][j] == 1)
						{
							TileLabels[j*Game.horizontalSize+i].setForeground(Color.red);
						}
					}
				}
			}
		}
	}
	
	// Removes the probabilities
	public void RemoveProbabilities() {
		
		if(probabilitiesDisplayed)
		{
			probabilitiesDisplayed = false;
			for(int i = 0; i<Game.horizontalSize; ++i)
			{
				for(int j = 0; j<Game.verticalSize; ++j)
				{
					if(Solver.candidateTiles[i][j])
					{
						TileLabels[j*Game.horizontalSize+i].setText("");
						TileLabels[j*Game.horizontalSize+i].setForeground(Color.black);
					}
				}
			}
		}
	}
	
	// Bot performs its next move. Used when playing automatic mode (i.e. step by step mode, or let it play mode).
	public void PlayNextBotStep(boolean stepByStep,boolean display) {
		
//		long begin = System.nanoTime();
		// If we displayed the probabilities, we remove them
		RemoveProbabilities();
		if(!Game.gameLost && !Game.gameWon)
		{
			// If no empty tile has been uncovered yet, looks for one
			if(!Game.plottedEmpty)
			{
				// The biggest chances if finding an empty tile are by clicking on a corner
				// Then if we clicked all the corner, we click on a border, as far away for where we clicked as possible
				int[] nextTile = Solver.SearchEmptyTile();
				if(Game.firstClick)
				{
//					System.out.println("Pressing first tile...");
					timer.restart();
					Game.startTime = System.nanoTime();
					Game.GenerateGrid(nextTile[0], nextTile[1], mines);

				}
				else
				{
//					System.out.println("Trying to get an empty tile...");
				}
				PressTile(nextTile[0], nextTile[1],display);
			}
			// If an empty tile has already been uncovered
			else
			{
				// Solver computes actions
				int[][] actions = Solver.GetActions();
				// If no actions found
				if(Solver.actionsNumber == 0)
				{
					guessingTileCount++;
//						System.out.println("Guessing tile..." + guessingTileCount);
					// We compute the tile that have the less chances of being a mine
					double[] nextTile = Solver.GuessNextMineAccurate(guessingTileCount,0);
					// Number of unknown tiles
					int remainingUnkown = 0;
					for(int i = 0; i<horizontalSize; ++i)
					{
						for(int j = 0; j<verticalSize; ++j)
						{
							if(Game.Unkown(i, j))
								remainingUnkown++;
						}
					}
					// We click it
					PressTile((int) nextTile[0], (int) nextTile[1],display);
					if(!display)
					{
						// [For debugging] We add the probability it has to be a mine, and the probability it had if we clicked randomly.
						probabilitiesList.add((float) nextTile[2]);
						randomClickProbabilitiesList.add((float) Game.minesLeft/remainingUnkown);
						// [For debugging] We add a true iff it is indeed a mine. These permit to see if the predicted probability is correct.
						if(Game.grid[(int) nextTile[0]][(int) nextTile[1]] != -1)
							goodGuessList.add(true);
						else
							goodGuessList.add(false);
					}
				}
				// If we actions detected, we perform them.
				else
				{
//						System.out.println("Pressing 100% sure tiles...");
					DoActions(actions, stepByStep,display);
				}
			}
		}
		
		// Checks win
		Game.CheckWin();
		if(Game.gameWon) {
			Won(display);
		}
//		long end = System.nanoTime();
//		System.out.println("Computation time (ms): " + (double)(end-begin)/1000000);
	}
	
	// Bot finishes game, i.e. calls previous function until game is finished. Used when playing automatic mode.
	public void BotFinishGame(boolean display) {
		
		guessingTileCount = 0;
		stopSolver = false;
		while(!Game.gameLost && !Game.gameWon)
		{
			PlayNextBotStep(false,display);
		}
//		do
//		{
//			PlayNextBotStep(false);
//		} while(!game.gameLost && !game.gameWon && !(Solver.GetLeftClickNumber() == 0 && Solver.GetRightClickNumber() == 0));
	}
	
	// Test function. Called when pressing Test1Panel.
	public void Test1() {
		
		Solver.Test1();
	}
	
	// Test function. Called when pressing Test2Panel.
	public void Test2() {
		
		Solver.Test2();
	}
	
	
	
	//-------------------------------Actions for the whole game-------------------------------
	
	
	
	// When the game is won
	public void Won(boolean display) {
		if(display)
			DisplayTimePassed();
		timer.stop();
		// Change smiley
		if(display)
		{
			SmileyPanel.ChangeUnpressedImageIcon(new ImageIcon("sprites\\Win.png"), scalingFactor);
			SmileyPanel.ChangePressedImageIcon(new ImageIcon("sprites\\Pressed_Win.png"), scalingFactor);
		}
		// Put a flag where there are mines and no flag
		for(int i = 0; i<Game.horizontalSize; ++i)
		{
			for(int j = 0; j<Game.verticalSize; ++j)
			{
				if(Game.grid[i][j] == -1 && Game.guessGrid[i][j] != 1)
				{
					ForceFlag(i,j,display);
				}
			}
		}
		// If we play in classical mode, or in unblocker mode with demon difficulty, we check if we made a hiscore
		if((!customLevel && helpParameter == 0) || (helpParameter == 1 && difficulty == 4))
		{
			// If we made a hiscore, ranking is the position. Otherwise, it is -1.
			ranking = Leaderboard.AddTime(difficulty, (double) (System.nanoTime()-Game.startTime)/1000000000);
			// If we made a hiscore.
			if(ranking != -1) 
			{
				// We display hiscore frame.
				HiscoreFrame = new HiscoreFrame(this);
				this.setEnabled(false);
				HiscoreFrame.addWindowListener(new java.awt.event.WindowAdapter() {
				    @Override
				    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
				    	// Hiscore frame opens leaderboard when closed.
				        HiscoreFrame.SubmitButton.doClick();
				    }
				});
				HiscoreFrame.SubmitButton.addActionListener(this);
			}
			Leaderboard.SaveData();
		}
	}
	
	// When the game is lost after clicking at position (i,j)
	public void Lost(int i, int j, boolean display) {
		
		// Retain the game is lost and the position we clicked when we lost it
		Game.gameLost = true;
		Game.losti = i;
		Game.lostj = j;
		
		Game.plottedGrid[i][j] = true;
		// Display mine and change smiley
		if(display)
		{
			TileIcons[j*Game.horizontalSize+i] = new ImageIcon("sprites\\Killing_mine.png");
			TileIcons[j*Game.horizontalSize+i] = new ImageIcon(TileIcons[j*Game.horizontalSize+i].getImage().getScaledInstance(scalingFactor*TileIcons[j*Game.horizontalSize+i].getIconWidth(),scalingFactor*TileIcons[j*Game.horizontalSize+i].getIconHeight(), Image.SCALE_SMOOTH));
			TileLabels[j*Game.horizontalSize+i].setIcon(TileIcons[j*Game.horizontalSize+i]);
			
			SmileyPanel.ChangeUnpressedImageIcon(new ImageIcon("sprites\\Dead.png"), scalingFactor);
			SmileyPanel.ChangePressedImageIcon(new ImageIcon("sprites\\Pressed_Dead.png"), scalingFactor);
		}
		
		timer.stop();
		
		for(int ti = 0; ti<horizontalSize; ++ti)
		{
			for(int tj = 0; tj<verticalSize; ++tj)
			{
				// Display every other mines
				if(Game.grid[ti][tj] == -1 && Game.guessGrid[ti][tj] != 1 && (ti != i || tj != j))
				{
					Game.plottedGrid[ti][tj] = true;
					if(display)
					{
						TileIcons[tj*Game.horizontalSize+ti] = new ImageIcon("sprites\\Mine.png");
						TileIcons[tj*Game.horizontalSize+ti] = new ImageIcon(TileIcons[tj*Game.horizontalSize+ti].getImage().getScaledInstance(scalingFactor*TileIcons[tj*Game.horizontalSize+ti].getIconWidth(),scalingFactor*TileIcons[tj*Game.horizontalSize+ti].getIconHeight(), Image.SCALE_SMOOTH));
						TileLabels[tj*Game.horizontalSize+ti].setIcon(TileIcons[tj*Game.horizontalSize+ti]);
					}
				}
				// Display where we put a flag where there were no mines
				else if(Game.guessGrid[ti][tj] == 1 && Game.grid[ti][tj] != -1)
				{
					Game.plottedGrid[ti][tj] = true;
					if(display)
					{
						TileIcons[tj*Game.horizontalSize+ti] = new ImageIcon("sprites\\Fake_mine.png");
						TileIcons[tj*Game.horizontalSize+ti] = new ImageIcon(TileIcons[tj*Game.horizontalSize+ti].getImage().getScaledInstance(scalingFactor*TileIcons[tj*Game.horizontalSize+ti].getIconWidth(),scalingFactor*TileIcons[tj*Game.horizontalSize+ti].getIconHeight(), Image.SCALE_SMOOTH));
						TileLabels[tj*Game.horizontalSize+ti].setIcon(TileIcons[tj*Game.horizontalSize+ti]);
					}
				}
			}
		}
	}
	
	// Displays the game at the instantiation of this
	public void CreateGame() {
		
		Game.Reset(horizontalSize, verticalSize, mines);
		timer.restart();
		timer.stop();
		
		DisplayminesLeft();
		DisplayTimePassed();
		
		Rescale(false);
		Center();
	}
	
	// Resets the game
	// sameSize indicates whether the game keeps the same size. If so, it speeds up the display.
	public void ResetGame(boolean sameSize, boolean display) {
		
		// We remove the probabilities
		RemoveProbabilities();
		
		if(display)
		{
			// If sameSize is true, then to improve computational speed, we don't display the tiles that are already empty.
			if(sameSize)
			{
				for(int i = 0; i<horizontalSize;++i)
				{
					for(int j = 0; j<verticalSize; ++j)
					{
						if(Game.plottedGrid[i][j] || Game.guessGrid[i][j] != 0)
						{
							rescaleGrid[i][j] = true;
							TileIcons[j*horizontalSize+i] = new ImageIcon("sprites\\Tile.png");
							TileLabels[j*horizontalSize+i].setIcon(TileIcons[j*horizontalSize+i]);
						}
						else
						{
							rescaleGrid[i][j] = false;
						}
					}
				}
			}
			// If sameSize is false, then we display everything.
			else
			{
				GamePanel.centerPanel.removeAll();
				rescaleGrid = new boolean[horizontalSize][verticalSize];
				for(int i = 0; i<horizontalSize; ++i)
				{
					for(int j = 0; j<verticalSize; ++j)
					{
						rescaleGrid[i][j] = true;
					}
				}
				TileLabels = new JLabel[horizontalSize*verticalSize];
				TileIcons = new ImageIcon[horizontalSize*verticalSize];
				for(int i = 0; i<horizontalSize;++i)
				{
					for(int j = 0; j<verticalSize; ++j)
					{
						TileLabels[j*horizontalSize+i] = new JLabel();
						TileLabels[j*horizontalSize+i].setBounds(16*i,16*j,16,16);
						TileIcons[j*horizontalSize+i] = new ImageIcon("sprites\\Tile.png");
						TileLabels[j*horizontalSize+i].setIcon(TileIcons[j*horizontalSize+i]);
						GamePanel.centerPanel.add(TileLabels[j*horizontalSize+i]);
					}
				}
			}
		}
		
		// Reset Game, Helper, Solver, and the timer
		Game.Reset(horizontalSize, verticalSize, mines);
		Helper.Reset(Game);
		Solver.Reset(Game, true);
		timer.restart();
		timer.stop();
		
		// Change scalignFactor and minInfosWidth
	    scalingFactor = Math.min(Math.max(Math.min((int) screenWidth/(16*horizontalSize + 24), screenHeight/(16*verticalSize + 65)), 1),Math.min((int) screenWidth/(16*9 + 24), screenHeight/(16*9 + 65)));
	    if(helpParameter == 2 || helpParameter == 4)
	    	minInfosWidth = 13;
	    else if(helpParameter == 3)
	    	minInfosWidth = 19;
	    else
	    	minInfosWidth = 9;
	    
	    // Compute remaining number of help if we play in help mode.
		if(helpParameter != 2)
		{
			remainingHelp = 0;
		}
		else
		{
			switch(difficulty)
			{
			// If we play custom, we have a formula for the remaining number of help, that has been determined by interpolation with the other difficulties, to guarantee a good difficulty.
			case(0): {
				int tilesNumber = horizontalSize*verticalSize;
				double d1 = -1.812*Math.log(tilesNumber)+12.343;
				double c3 = 0.0000000034158390763;
				double c2 = -0.000010978539087;
				double c1 = 0.0054532540169;
				double c0 = -0.50326158704;
				double d2;
				if(tilesNumber<=480)
				{
					d2 = c3*Math.pow(tilesNumber, 3)+c2*tilesNumber*tilesNumber+c1*tilesNumber+c0;
				}
				else
				{
					double const1 = c3*Math.pow(480, 3)+c2*480*480+c1*480+c0;
					double const2 = c3*Math.pow(2625, 3)+c2*2625*2625+c1*2625+c0;
					d2 = ((const2-const1)/(2625-480))*(tilesNumber-480)+const1;
				}
				double helpFor100 = Math.exp(d1+d2);
				System.out.println(helpFor100);
				remainingHelp = (int) Math.round(helpFor100*Math.pow((double) mines/100, 2));
				break;
			}
			// Beginner
			case(1): remainingHelp = 1; break;
			// Intermediate
			case(2): remainingHelp = 2; break;
			// Expert
			case(3): remainingHelp = 3; break;
			// Demon
			case(4): remainingHelp = 5; break;
			}
		}
		// Reset all the panels
	    if(display)
	    {
			DisplayminesLeft();
			SmileyPanel.ChangeUnpressedImageIcon(new ImageIcon("sprites\\Smile.png"), scalingFactor);
			SmileyPanel.ChangePressedImageIcon(new ImageIcon("sprites\\Pressed_Smile.png"), scalingFactor);
			HelpPanel.ResetImageIcon();
			Test1Panel.ResetImageIcon();
//			Test2Panel.ResetImageIcon();
			ProbabilitiesPanel.ResetImageIcon();
			NextBotStepPanel.ResetImageIcon();
			FinishBotPanel.ResetImageIcon();
			if(helpParameter != 2)
				HelpPanel.setVisible(false);
			else
				HelpPanel.setVisible(true);
			
			if(helpParameter != 3)
			{
				Test1Panel.setVisible(false);
//				Test2Panel.setVisible(false);
				ProbabilitiesPanel.setVisible(false);
				NextBotStepPanel.setVisible(false);
				FinishBotPanel.setVisible(false);
			}
			else
			{
				Test1Panel.setVisible(true);
//				Test2Panel.setVisible(true);
				ProbabilitiesPanel.setVisible(true);
				NextBotStepPanel.setVisible(true);
				FinishBotPanel.setVisible(true);
			}
			remainingHelpLabel.setText(Integer.toString(remainingHelp));
			remainingHelpLabel.setFont(new Font("Verdana",Font.BOLD,8*scalingFactor));
			DisplayTimePassed();
		    
			Rescale(sameSize);
			Center();
	    }
	}
	
	
	
	//------------------------ComponentListener and ActionListener methods--------------------
	
	
	
	@Override
	public void componentResized(ComponentEvent e) {
		// TODO Auto-generated method stub
		
		// We center everything
		Center();
		// If window is full-screen, then we retain it
		if(this.getExtendedState() == JFrame.MAXIMIZED_BOTH)
		{
			fullSize = true;
		}
		// If it was full-screen, and it is reduced, we retain it and we set the window to the center.
		if(fullSize && this.getExtendedState() != JFrame.MAXIMIZED_BOTH)
		{
			fullSize = false;
			this.pack();
			this.setLocationRelativeTo(null);
		}
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
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
		// When timer passes a second, we display the time passed.
		if(e.getSource() == timer)
		{
			if(!Game.firstClick && helpParameter != 4) 
			{
				Game.timePassed = (int) Math.min(Math.round((double) (System.nanoTime()-Game.startTime)/1000000000),999);
				DisplayTimePassed();
			}
		}
		// When we pressed hiscore frame's submit button
		if(e.getSource() == HiscoreFrame.SubmitButton)
		{
			// We close it
			HiscoreFrame.Close();
			// We add it to the leaderboard
			if(HiscoreFrame.NameTextField.getText().equals(""))
				// Default name is "Player"
				Leaderboard.AddName(difficulty, ranking, "Player");
			else
				Leaderboard.AddName(difficulty, ranking, HiscoreFrame.NameTextField.getText());
			Leaderboard.SaveData();
			// We display leaderboard, with the new hiscore in blue
			LFrame = new LeaderboardFrame(this, difficulty, ranking);
			this.setEnabled(false);
			LframeRanking = ranking;
			LframeDifficulty = difficulty;
			LFrame.addWindowListener(new java.awt.event.WindowAdapter() {
			    @Override
			    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
			    	LFrame.Close();
			        ranking = -1;
					LframeRanking = -1;
					LframeDifficulty = 0;
			    }
			});
			LFrame.ComboBox.addActionListener(this);
		}
		// When we change difficulty in leaderboard frame, displays the corresponding leaderboar
		if(e.getSource() == LFrame.ComboBox)
		{
			int selectedDif = LFrame.ComboBox.getSelectedIndex()+1;
			if(selectedDif != LFrame.difficulty)
			{
				LFrame.dispose();
				if(selectedDif == LframeDifficulty)
				{
					LFrame = new LeaderboardFrame(this, selectedDif, ranking);
				}
				else
				{
					LFrame = new LeaderboardFrame(this, selectedDif, -1);
				}
				this.setEnabled(false);
				LFrame.addWindowListener(new java.awt.event.WindowAdapter() {
				    @Override
				    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
				    	LFrame.Close();
				        ranking = -1;
				    }
				});
				LFrame.ComboBox.addActionListener(this);
			}
		}
		// We when click Option frame's submit button, changes the game accordingly
		if(e.getSource() == OFrame.SubmitButton) 
		{
			// This if statement prevents doing actions when custom mode is selected and one of the TextFields is empty, or when we selected automatic mode but didn't select a button
			if(!(OFrame.HSizeTextField.getText().equals("") || OFrame.VSizeTextField.getText().equals("") || OFrame.MinesTextField.getText().equals("") || (OFrame.AutomaticButton.isSelected() && !OFrame.StepbyStepButton.isSelected() && !OFrame.LetItPlayButton.isSelected())))
			{
				OFrame.Close();
				
				// Parameters are updated depending on what we chose
				// Some parameters are retained as they were before doing changes
				int oldDifficulty = difficulty;
				this.difficulty = OFrame.difficulty;
				this.customLevel = OFrame.customLevel;
				int oldHorizontalSize = horizontalSize;
				int oldVerticalSize = verticalSize;
				this.horizontalSize = OFrame.horizontalSize;
				this.verticalSize = OFrame.verticalSize;
				int oldMines = mines;
				this.mines = OFrame.mines;
				int oldHelpParameter = helpParameter;
				this.helpParameter = OFrame.helpParameter;
				
				switch(difficulty)
				{
				// Beginner
				case(1):
				{
					this.horizontalSize = 9; 
					this.verticalSize = 9; 
					this.mines = 10;
					this.difficulty = 1;
					break;
				}
				// Intermediate
				case(2):
				{
					this.horizontalSize = 16; 
					this.verticalSize = 16; 
					this.mines = 40;
					this.difficulty = 2;
					break;
				}
				// Expert
				case(3):
				{
					this.horizontalSize = 30; 
					this.verticalSize = 16; 
					this.mines = 99;
					this.difficulty = 3;
					break;
				}
				// Demon
				case(4):
				{
					this.horizontalSize = 75; 
					this.verticalSize = 35; 
					this.mines = 600;
					this.difficulty = 4;
					break;
				}
				}
				
				// If nothing changes, we do nothing
				if(horizontalSize != oldHorizontalSize || verticalSize != oldVerticalSize || mines != oldMines || helpParameter != oldHelpParameter)
				{
					// If same size, sameSize argument of ResetGame is set to true
					if(horizontalSize == oldHorizontalSize && verticalSize == oldVerticalSize)
					{
						ResetGame(true,true);
					}
					// If not same size, sameSize argument if ResetGame is set to false
					else
					{
						ResetGame(false,true);
					}
				}
				
				// If we chose let it play mode, let it play frame is opened
				if(helpParameter == 4)
				{
					LIPFrame = new LetItPlayChooserFrame(this);
					this.setEnabled(false);
					LIPFrame.addWindowListener(new java.awt.event.WindowAdapter() {
					    @Override
					    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
					        LIPFrame.Close();
					    }
					});
					LIPFrame.SubmitButton.addActionListener(this);
				}
			}
		}
		// When we click let it play frame's submit button
		if(e.getSource() == LIPFrame.SubmitButton)
		{
			// We retain the number of games entered by the player
			autoGamesNumber = Integer.parseInt(LIPFrame.GamesNumberTextField.getText().replaceAll("" + (char) 8239, ""));
			// We close the frame and open the progress bar frame
			LIPFrame.Close();
			LIPPBFrame = new LetItPlayProgressBarFrame(this,autoGamesNumber);
			this.setEnabled(false);
			LIPPBFrame.addWindowListener(new java.awt.event.WindowAdapter() {
			    @Override
			    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
			        LIPPBFrame.Close();
			    }
			});
			LIPPBFrame.StopButton.addActionListener(this);
			LIPPBFrame.CloseButton.addActionListener(this);
			// We launch all the threads
			FirstAutoTask = new UpdateProgressBarTask();
			FirstAutoTask.start();
			SecondAutoTask = new AutomaticPlayTask();
			SecondAutoTask.start();
			ThirdAutoTask = new GameDisplayTask();
			ThirdAutoTask.start();
			

		}
		// When we click let it play progress bar's stop button
		if(e.getSource() == LIPPBFrame.StopButton)
		{
			//We stop the threads
			FirstAutoTask.Stop();
			SecondAutoTask.Stop();
			// We replace the stop button with the close button
			LIPPBFrame.StopButton.setVisible(false);
			LIPPBFrame.StopButton.setEnabled(false);
			LIPPBFrame.CloseButton.setVisible(true);
			LIPPBFrame.CloseButton.setEnabled(true);
		}
		// When we click let it play progress bar's close button
		if(e.getSource() == LIPPBFrame.CloseButton)
		{
			LIPPBFrame.Close();
		}
	}
	
	// Thread that updates the progress bar as the games go along.
	public class UpdateProgressBarTask extends Thread{
		
		boolean gamesPlaying;
		boolean stopped = false;
		
		@Override
	    public void run() {
			gamesPlaying = true;
			// Displays the number of the game we are currently playing
			while(gamesPlaying && !stopped)
			{
				LIPPBFrame.GamesProgressBar.setValue(autoGamesCount);
				LIPPBFrame.GamesProgressBar.setString("Playing game number: " + autoGamesCount + " out of " + autoGamesNumber);
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			// When it is stopped during execution
			if(stopped)
			{
				LIPPBFrame.GamesProgressBar.setString("Stopped after " + autoGamesCount + " games. Games won: " + SecondAutoTask.gamesWon + ". Win rate: " + String.format("%.2f",100*(double) SecondAutoTask.gamesWon/autoGamesCount) + "%.");
			}
			// When execution is done
			else
			{
				LIPPBFrame.GamesProgressBar.setValue(autoGamesCount);
				LIPPBFrame.GamesProgressBar.setString("Done! Games played: " + autoGamesNumber + ". Games won: " + SecondAutoTask.gamesWon + ". Win rate: " + String.format("%.2f",100*(double) SecondAutoTask.gamesWon/autoGamesNumber) + "%.");
			}
	    }

		// When stopped
		public void Stop() {
			stopped = true;
		}
	}
	
	// Thread executing all the computations
	public class AutomaticPlayTask extends Thread{
		
		int gamesWon;
		int gamesLost;
		boolean stopped = false;
		boolean gameSent = false;
		
		@Override
		public void run() {
			
			gamesWon = 0;
			gamesLost = 0;
			autoGamesCount = 1;
			int plottedEmptyCount = 0;
			float mediumWinGuessingTilesCount = 0;
			// [For debugging] List containing the probabilities of all the tiles computed by probability computation functions to be a tile
			probabilitiesList = new ArrayList<Float>();
			// [For debugging] List containing the probabilities we had to get a mine if we clicked a random tile, every time we click a tile thanks to a probability computation function
			randomClickProbabilitiesList = new ArrayList<Float>();
			// [For debugging] List containing one boolean for each tile clicked by a probability computation function, indicating whether it was a mine or not
			goodGuessList = new ArrayList<Boolean>();
			long begin = System.nanoTime();
			// Play the games
			while(autoGamesCount <= autoGamesNumber && !stopped)
			{
				// Do all the actions
				BotFinishGame(false);
				// If game is won
				if(Game.gameWon)
				{
					mediumWinGuessingTilesCount += guessingTileCount;
					gamesWon++;
				}
				// If game is lost
				else if(Game.gameLost)
				{
					gamesLost++;
				}
				// [For debugging] If we didn't plot an empty tile
				if(Game.plottedEmpty)
				{
					plottedEmptyCount++;
				}
				// If the GameDisplayTask is available, then we pass it the game we just played so it displays it
				if(ThirdAutoTask.avaliable) 
				{
					gameSent = false;
					ThirdAutoTask.GiveGame(Game);
					// Thread waits that the game finished sending to continue [Note: It may be good to do this with wait() and notify() methods instead]
					while(!gameSent)
					{
						
					}
				}
				// We reset the game
				if(autoGamesCount<autoGamesNumber)
					ResetGame(true,false);
				autoGamesCount++;
			}
			FirstAutoTask.gamesPlaying = false;
			LIPPBFrame.StopButton.setVisible(false);
			LIPPBFrame.StopButton.setEnabled(false);
			LIPPBFrame.CloseButton.setVisible(true);
			LIPPBFrame.CloseButton.setEnabled(true);
			long end = System.nanoTime();
			long time = end-begin;
			// [For debugging] We display several informations about the games we played. It is very useful to compare performances between different methods.
			System.out.println("Games won: " + gamesWon + ". Games lost: " + gamesLost + ". Win rate: " + 100*(double) gamesWon/(Math.min(autoGamesCount,autoGamesNumber)) + "%.");
			// [For debugging] Very useful line to decide which strategy to adopt for the beginning of the game
			System.out.println("Games where we plotted an empty tile: " + plottedEmptyCount + " (" + 100*(double) plottedEmptyCount/autoGamesCount + "%). Win rate amongst these: " + 100*(double) gamesWon/plottedEmptyCount + "%.");
			System.out.println("Computation time (s): " + (double) time/1000000000);
			int guessNumber = probabilitiesList.size();
			// [For debugging] Compute mean probability of tiles computed by probability computation functions to be a tile
			float meanProba = 0.f;
			for(Float prob : probabilitiesList)
			{
				meanProba += prob;
			}
			meanProba /= guessNumber;
			// [For debugging] Compute mean probability of losing if we clicked a random tile every time we used a probability computation function
			float meanRandomProba = 0.f;
			for(Float randProb : randomClickProbabilitiesList)
			{
				meanRandomProba += randProb;
			}
			meanRandomProba /= guessNumber;
			// [For debugging] Compute the proportion of tiles computed with a probability computation function that were mines
			float meanBadGuess = 0.f;
			for(Boolean guess : goodGuessList)
			{
				if(!guess)
				{
					meanBadGuess++;
				}
			}
			meanBadGuess /= guessNumber;
			mediumWinGuessingTilesCount /= gamesWon;
			
			// [For debugging] Very useful lines to check if the probability computation functions work. If the mean probability computed equals the mean error rate, then it means that the functions work. 
			System.out.println("Medium probabilitiy: " + 100*meanProba + "% among " + probabilitiesList.size() + " guesses");
			System.out.println("Medium error rate: " + 100*meanBadGuess + "% among " + goodGuessList.size() + " guesses");
			System.out.println("Medium probability if random click: " + 100*meanRandomProba + "% among " + randomClickProbabilitiesList.size() + " guesses");
			System.out.println("Medium guessings number " + mediumWinGuessingTilesCount);
		}
		
		// If stopped
		public void Stop() {
			
			stopped = true;
		}
	}
	
	// Thread for displaying games all along the execution (purely esthetic)
	public class GameDisplayTask extends Thread{
		
		// available is an indicator for the AutomaticPlayTask thread to know if it can give a game to this thread
		boolean avaliable = true;
		MinesweeperGame Game;
		
		@Override
		public void run() {
			
			while(SecondAutoTask.isAlive())
			{
				// We enter here only if we have just been given a game
				if(!avaliable)
				{
					// We display the game
					DisplayGame(Game);
					// We sleep for a period of time, determined by the game's size
					try {
						Thread.sleep(Math.max(Game.horizontalSize*Game.verticalSize*11,2000));
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					// After that, this thread is set back to available
					avaliable = true;
				}
			}
		}
		
		// The AutomaticPlayTask thread gives this thread a game to display
		public void GiveGame(MinesweeperGame Game) {
			 
			// We copy the game so there won't be pointer issues when the AutomaticPlayTask thread continues
			this.Game = Game.GetCopy();
			// We set available to false so we can begin the loop in run
			avaliable = false;
			// We notify the AutomaticPlayTask it can continue [Note: It may be good to do this with wait() and notify() methods instead]
			SecondAutoTask.gameSent = true;
		}
	}
	
}
