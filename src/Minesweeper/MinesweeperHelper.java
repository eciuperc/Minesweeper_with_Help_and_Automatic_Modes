package Minesweeper;

import java.util.ArrayList;
import java.util.Random;

public class MinesweeperHelper {
	
	MinesweeperGame Game; 									// Game in which we require help
	public boolean[][] helpedTiles;							// Array containing all the tiles that have been given by the help function. Used to prevent player from removing them.
	public boolean[][] correctedTiles;						// Array containing all the tiles that have been corrected by the helper. Used to prevent correcting the same tile twice.
	
	// Constructor
	public MinesweeperHelper(MinesweeperGame Game){
		
		this.Game = Game;
		helpedTiles = new boolean[Game.horizontalSize][Game.verticalSize];
		for(int i = 0; i<Game.horizontalSize; ++i)
		{
			for(int j = 0; j<Game.verticalSize; ++j)
			{
				helpedTiles[i][j] = false;
			}
		}
		correctedTiles = new boolean[Game.horizontalSize][Game.verticalSize];
		for(int i = 0; i<Game.horizontalSize; ++i)
		{
			for(int j = 0; j<Game.verticalSize; ++j)
			{
				correctedTiles[i][j] = false;
			}
		}
	}
	
	// Reset method
	public void Reset(MinesweeperGame Game){
		
		this.Game = Game;
		helpedTiles = new boolean[Game.horizontalSize][Game.verticalSize];
		for(int i = 0; i<Game.horizontalSize; ++i)
		{
			for(int j = 0; j<Game.verticalSize; ++j)
			{
				helpedTiles[i][j] = false;
			}
		}
		correctedTiles = new boolean[Game.horizontalSize][Game.verticalSize];
		for(int i = 0; i<Game.horizontalSize; ++i)
		{
			for(int j = 0; j<Game.verticalSize; ++j)
			{
				correctedTiles[i][j] = false;
			}
		}
	}
	
	// Simulates how many tiles we uncover if we click at (i, j), taking into account the fact that empty tiles uncover adjacent tiles. We assume that there is no mine at (i, j).
	// Uses as arguments clones of Game.grid, Game.guessGrid and Game.plottedGrid. Retains the tiles that are uncovered thanks to clonePlottedGrid. 
	// Used by next function.
	public int SimulateClick(int[][] cloneGrid, boolean[][] clonePlottedGrid, int[][] cloneGuessGrid, int i, int j) {
		
		int ans = 0;
		
		if(cloneGuessGrid[i][j] != 1)
		{
			if(!clonePlottedGrid[i][j])
			{
				// We change clonePlottedGrid[i][j] to prevent taking the same tile into account twice.
				clonePlottedGrid[i][j] = true;
				ans++;
				cloneGuessGrid[i][j] = 0;
				// If tile has zero mines around it, then all the adjacent tiles are uncovered.
				if(cloneGrid[i][j] == 0)
				{
					for(int ati = Math.max(i-1,0); ati <= Math.min(i+1, Game.horizontalSize-1); ++ati)
					{
						for(int atj = Math.max(j-1,0); atj <= Math.min(j+1, Game.verticalSize-1); ++atj) {
							{
								ans += SimulateClick(cloneGrid, clonePlottedGrid, cloneGuessGrid, ati,atj);
							}
						}
					}
				}
			}
		}
		
		return ans;
	}
	
	// Simulates how many tiles we uncover if we do the actions in argument (actions have the same format as the actions attribute of MinesweeperSolver)
	public int DisplayedTilesNumber(int[][] actions)
	{
		
		int ans = 0;
		
		// Clones Game.grid
		int[][] cloneGrid = new int[Game.horizontalSize][Game.verticalSize];
		for(int i = 0; i<Game.horizontalSize; ++i)
		{
			for(int j = 0; j<Game.verticalSize; ++j)
			{
				cloneGrid[i][j] = Game.grid[i][j]; 
			}
		}
		// Clones Game.plottedGrid
		boolean[][] clonePlottedGrid = new boolean[Game.horizontalSize][Game.verticalSize];
		for(int i = 0; i<Game.horizontalSize; ++i)
		{
			for(int j = 0; j<Game.verticalSize; ++j)
			{
				clonePlottedGrid[i][j] = Game.plottedGrid[i][j]; 
			}
		}
		// Clones Game.guessGrid
		int[][] cloneGuessGrid = new int[Game.horizontalSize][Game.verticalSize];
		for(int i = 0; i<Game.horizontalSize; ++i)
		{
			for(int j = 0; j<Game.verticalSize; ++j)
			{
				cloneGuessGrid[i][j] = Game.guessGrid[i][j]; 
			}
		}
		
		// Computes the number of uncovered tiles. We never take into account the same tile twice, since the uncovered tiles are retained in clonePlottedGrid.
		for(int i = 0; i<Game.horizontalSize; ++i)
		{
			for(int j = 0; j<Game.verticalSize; ++j)
			{
				if(actions[i][j] == 1)
				{
					ans += SimulateClick(cloneGrid, clonePlottedGrid, cloneGuessGrid, i, j);
				}
				else if(actions[i][j] == 2)
				{
					ans++;
					cloneGuessGrid[i][j] = 1;
				}
					
			}
		}
		
		return ans;
	}
	
	// Returns the helping tile, chosen among every candidate tile that have a mine as the one that uncovers the biggest part of the grid. Uses previous functions.
	public int[] GetHelpMine() {
		
		MinesweeperSolver SecondarySolver = new MinesweeperSolver(Game);
		
		// Computes the tiles that are candidate for being returned as a help
		// These tiles are the tiles that have a mine, and that are adjacent to an uncovered tile.
		boolean[][] possibleMines = new boolean[Game.horizontalSize][Game.verticalSize];
		for(int i = 0; i<Game.horizontalSize; ++i)
		{
			for(int j = 0; j<Game.verticalSize; ++j)
			{
				// The tile is a candidate only if it has a mine and if we didn't guess it yet
				if(Game.grid[i][j] == -1 && Game.guessGrid[i][j] != 1) 
				{
					possibleMines[i][j] = false;
					// We consider the tile as a candidate only if it has a neighbor that is plotted
					for(int ati = Math.max(i-1,0); ati <= Math.min(i+1, Game.horizontalSize-1); ++ati)
					{
						for(int atj = Math.max(j-1,0); atj <= Math.min(j+1, Game.verticalSize-1); ++atj) 
						{
							if(Game.plottedGrid[ati][atj])
							{
								possibleMines[i][j] = true;
							}
						}
					}
				}
				else
				{
					possibleMines[i][j] = false;
				}
			}
		}
		
		int[][] DisplayedTiles = new int[Game.horizontalSize][Game.verticalSize];
		for(int i = 0; i < Game.horizontalSize; ++i)
		{
			for(int j = 0; j < Game.verticalSize; ++j)
			{
				DisplayedTiles[i][j] = -1;
			}
		}
		int max = -1;
		int argmaxSize = 0;
		
		// Computes the number of uncovered tiles for every possible mine
		for(int i = 0; i<Game.horizontalSize; ++i)
		{
			for(int j = 0; j<Game.verticalSize; ++j)
			{
				if(possibleMines[i][j])
				{
					int trueGuessGrid = Game.guessGrid[i][j];
					Game.guessGrid[i][j] = 1;
					
					SecondarySolver.GetActions();
					DisplayedTiles[i][j] = DisplayedTilesNumber(SecondarySolver.actions);
					if(DisplayedTiles[i][j] > max)
					{
						max = DisplayedTiles[i][j];
						argmaxSize = 1;
					}
					else if(DisplayedTiles[i][j] == max)
					{
						argmaxSize++;
					}
					
					Game.guessGrid[i][j] = trueGuessGrid;
				}
			}
		}
		
		// We choose a maximum information tile randomly, if there are several of them
		Random random = new Random();
		
		int chosenInd = random.nextInt(argmaxSize);
		int ind = 0;
		// We initiate the value of the chosen position to (-1, -1), so if there are no possible mines, the returned value is (-1, -1).
		int[] chosenPos = {-1,-1};
		for(int i = 0; i<Game.horizontalSize; ++i)
		{
			for(int j = 0; j<Game.verticalSize; ++j)
			{
				if(DisplayedTiles[i][j] == max)
				{
					if(ind == chosenInd)
					{
						chosenPos[0] = i; 
						chosenPos[1] = j;
					}
					ind++;
				}
			}
		}
		// We retain that we gave help for this tile
		helpedTiles[chosenPos[0]][chosenPos[1]] = true;
		
		return chosenPos;
	
	}
	
	// Used only if there is a mistake in the game. Instead of returning a help mine, returns a tile marked as a mine that is not actually a mine.
	public int[] CorrectMistake() {
		
		// List containing all the wrong tiles that have not been corrected yet.
		ArrayList<int[]> wrongTiles = new ArrayList<>();
		
		for(int i = 0; i<Game.horizontalSize; ++i)
		{
			for(int j = 0; j<Game.verticalSize; ++j)
			{
				if(Game.guessGrid[i][j] == 1 && Game.grid[i][j] != -1 && !correctedTiles[i][j])
				{
					int[] tile = {i,j};
					wrongTiles.add(tile);
				}
			}
		}
		
		// If there are no candidates (i.e. all the wrong tiles have already been corrected), we return (-1, -1)
		if(wrongTiles.size() == 0)
		{
			int[] chosenPos = {-1, -1};
			
			return chosenPos;
		}
		// Otherwise, we return a randomly chosen wrong tile
		else
		{
			Random random = new Random();
			int chosenInd = random.nextInt(wrongTiles.size());
			int[] chosenPos = wrongTiles.get(chosenInd);
			// We retain that this tile has been corrected
			correctedTiles[chosenPos[0]][chosenPos[1]] = true;
			
			return chosenPos;
		}
	}

}
