package Minesweeper;
import java.util.Random;

// Class representing a minesweeper game
public class MinesweeperGame {
	
	public int horizontalSize;              	// Horizontal dimension of the grid
	public int verticalSize;                    // Vertical dimension of the grid
	public int minesLeft;                       // Number of mines left to discover
	public long startTime;                     	// The time when we started the game (ie. when we clicked on the first tile). It is expressed in nanoseconds, using System.nanoTime()
	public long timePassed;                     // The time that passed since the beginning of the game, in nanoseconds              
	public boolean gameLost;                    // Indicates if we lost the game
	public int losti;							// Indicates on which tile we clicked when we lost the game. If game isn't lost, set to -1
	public int lostj;							// Indicates on which tile we clicked when we lost the game. If game isn't lost, set to -1
	public boolean gameWon;						// Indicates if we won the game
	public int[][] grid;                        // Game's grid. An entry equals -1 if the corresponding tile has a mine, and equals the number of adjacent mines of the corresponding tile otherwise
	public int[][] guessGrid;					// Each entry of this array represents if we put a flag or an interrogation point on the corresponding tile. If there is nothing, entry is 0, if there is a flag, entry is 1, if there is an interrogation point, entry is 2
	public boolean plottedEmpty;                // True iff. we already clicked on an empty tile (used for the solver)
	public boolean[][] plottedGrid;             // Each entry of this array represents whether we already discovered the corresponding tile or not
	public int plotNumber;                      // Counts how many tiles we discovered
	public int plotGoal;						// The number of tiles we have do discover in order to win
	public boolean firstClick;					// True iff. we did the first click
	
	// Constructor. The arguments are the game's dimensions and the number of mines
	public MinesweeperGame(int x, int y, int mines){
		
		horizontalSize = x;
		verticalSize = y;
		minesLeft = mines;
		startTime = System.nanoTime();
		timePassed = 0;
		gameLost = false;
		losti = -1;
		lostj = -1;
		gameWon = false;
		grid = new int[horizontalSize][verticalSize];
		for(int i = 0; i<horizontalSize; ++i)
		{
			for(int j = 0; j<verticalSize; ++j)
			{
				grid[i][j] = 0;
			}
		}
		guessGrid = new int[horizontalSize][verticalSize];
		for(int i = 0; i<horizontalSize; ++i)
		{
			for(int j = 0; j<verticalSize; ++j)
			{
				guessGrid[i][j] = 0;
			}
		}
		plottedEmpty = false;
		plottedGrid = new boolean[horizontalSize][verticalSize];
		plottedGrid = new boolean[horizontalSize][verticalSize];
		for(int i = 0; i<horizontalSize; ++i)
		{
			for(int j = 0; j<verticalSize; ++j)
			{
				plottedGrid[i][j] = false;
			}
		}
		plotNumber = 0;
		plotGoal = horizontalSize*verticalSize-minesLeft;
		firstClick = true;
		
	}
	
	// Resets the grid. The arguments are game's dimensions and the number of mines
	public void Reset(int x, int y, int mines) {
	
		horizontalSize = x;
		verticalSize = y;
		minesLeft = mines;
		startTime = System.nanoTime();
		timePassed = 0;
		gameLost = false;
		losti = -1;
		lostj = -1;
		gameWon = false;
		grid = new int[horizontalSize][verticalSize];
		for(int i = 0; i<horizontalSize; ++i)
		{
			for(int j = 0; j<verticalSize; ++j)
			{
				grid[i][j] = 0;
			}
		}
		guessGrid = new int[horizontalSize][verticalSize];
		for(int i = 0; i<horizontalSize; ++i)
		{
			for(int j = 0; j<verticalSize; ++j)
			{
				guessGrid[i][j] = 0;
			}
		}
		plottedEmpty = false;
		plottedGrid = new boolean[horizontalSize][verticalSize];
		for(int i = 0; i<horizontalSize; ++i)
		{
			for(int j = 0; j<verticalSize; ++j)
			{
				plottedGrid[i][j] = false;
			}
		}
		plotNumber = 0;
		plotGoal = horizontalSize*verticalSize-minesLeft;
		firstClick = true;
		
	}
	
	// Returns an independent copy of the game
	public MinesweeperGame GetCopy() {
		
		MinesweeperGame CopiedGame = new MinesweeperGame(horizontalSize,verticalSize,minesLeft);
		
		CopiedGame.startTime = startTime;
		CopiedGame.timePassed = timePassed;
		CopiedGame.gameLost = gameLost;
		CopiedGame.losti = losti;
		CopiedGame.lostj = lostj;
		CopiedGame.gameWon = gameWon;
		CopiedGame.grid = new int[horizontalSize][verticalSize];
		for(int i = 0; i<horizontalSize; ++i)
		{
			for(int j = 0; j<verticalSize; ++j)
				{
					CopiedGame.grid[i][j] = grid[i][j];
				}
		}
		for(int i = 0; i<horizontalSize; ++i)
		{
			for(int j = 0; j<verticalSize; ++j)
			{
				CopiedGame.guessGrid[i][j] = guessGrid[i][j];
			}
		}
		CopiedGame.plottedEmpty = plottedEmpty;
		for(int i = 0; i<horizontalSize; ++i)
		{
			for(int j = 0; j<verticalSize; ++j)
			{
				CopiedGame.plottedGrid[i][j] = plottedGrid[i][j];
			}
		}
		CopiedGame.plotNumber = plotNumber;
		CopiedGame.plotGoal = plotGoal;
		CopiedGame.firstClick = firstClick;
		
		return CopiedGame;
	}
	
	// Generates the grid. Called when we do the first click, at position (i, j). The mines are set randomly on the whole grid, except at position (i, j).
	// Once the mines are generated, we compute the number of adjacent mines for each safe tile.
	public void GenerateGrid(int i, int j, int mines) {
		
		if(firstClick) {
			
			// Generate the mines randomly on the whole grid, except at position (i, j)
			Random random = new Random();
			
			// If we have less the half mines, we generate them normally
			if(mines<=(horizontalSize*verticalSize)/2)
			{
				for(int k = 0; k < mines; ++k)
				{
					int posi;
					int posj;
					
					do
					{
						posi = random.nextInt(horizontalSize);
						posj = random.nextInt(verticalSize);
					} while((posi == i && posj == j) || (grid[posi][posj] == -1));
					
					grid[posi][posj] = -1;
				}
			}
			// If we have more than half mines, we generate instead the tiles where we have no mines. For computational speed.
			else
			{
				grid[i][j] = 1;
				for(int k = 0; k < horizontalSize*verticalSize-mines-1; ++k)
				{
					int posi;
					int posj;
					
					do
					{
						posi = random.nextInt(horizontalSize);
						posj = random.nextInt(verticalSize);
					} while(grid[posi][posj] == 1);
					
					grid[posi][posj] = 1;
				}
				for(int ti = 0; ti<horizontalSize; ++ti)
				{
					for(int tj = 0; tj<verticalSize; ++tj)
					{
						grid[ti][tj]--;
					}
				}
			}
			
			// Calculate the number of adjacent mines for every tile
			for(int ti = 0; ti<horizontalSize; ++ti)
			{
				for(int tj = 0; tj < verticalSize; ++tj)
				{
					if(grid[ti][tj] == -1)
					{
						for(int ati = Math.max(ti-1,0); ati <= Math.min(ti+1, horizontalSize-1); ++ati)
						{
							for(int atj = Math.max(tj-1,0); atj <= Math.min(tj+1, verticalSize-1); ++atj) {
								if(grid[ati][atj] != -1)
								{
									grid[ati][atj] += 1;
								}
							}
						}
					}
				}
			}
			
		}
		
		firstClick = false;
	}
	
	// Checks if the game is won and sets gameWon to true if it is the case
	public void CheckWin() {
		
		if(plotNumber == plotGoal && !gameLost) {
			gameWon = true;
		}
	}
	
	// True iff. the (i, j) tile is unknown, i.e. if it is not plotted and we didn't set a flag on it.
	public boolean Unkown(int i, int j) {
		
		return (!plottedGrid[i][j] && guessGrid[i][j] != 1);
	}
	
	// Checks if there is a mistake in the game, i.e. if the player set a flag if the wrong place. Used for "help mines" and unblocker, which activate only if there is no mistake to prevent bugs.
	public boolean HasMistake() {
		
		for(int i = 0; i<horizontalSize; ++i)
		{
			for(int j = 0; j<verticalSize; ++j)
			{
				if(guessGrid[i][j] == 1 && grid[i][j] != -1)
					return true;
			}
		}
		return false;
	}
	
	// Displays the grid. For debugging.
	public void DisplayGrid() {
		
		for(int i = 0; i<horizontalSize; ++i)
		{
			for(int j = 0; j<verticalSize; ++j)
			{
				System.out.print(grid[j][i]+ " ");
			}
			System.out.println();
		}
		System.out.println();
	}
}
