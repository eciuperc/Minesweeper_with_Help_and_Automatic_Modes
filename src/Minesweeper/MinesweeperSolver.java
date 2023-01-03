package Minesweeper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class MinesweeperSolver {
	
	MinesweeperGame Game;                                     // Game that has to be solved
	
	// Tile groups
	ArrayList<TileGroup> TileGroups;                          // List of tile groups created to solve the game
	int baseGroupsNumber;									  // Number of tile groups created before applying SeparateTileGroups function
	int groupsNumber;										  // Length of TileGroups
	int groupsNumberBackup;									  // Retains the number of tile groups before each iteration of SeparateTileGroups. Useful for computing groupsProcess.				  
	ArrayList<Integer>[][] groupsPerTile;					  // Each entry has the numbers of the groups that contain the corresponding tiles
	ArrayList<int[]> groupsProcess;							  // Retains every pair of groups that interact, and which groups they give. Useful for computing SubMinesweeperSolver efficiently.
	boolean[][] separatedGroups;							  // Array describing which groups will interact
	
	// Arrays containing 100% sure tiles
	boolean[][] detectedSafe;								  // Each entry is true iff. the solver detected there is no mine at the corresponding tile
	boolean[][] detectedMines;								  // Each entry is true iff. the solver detected there is a mine at the corresponding tile
	
	// Miscellaneous arrays, containing various informations. Most of them are used by functions called when we have no 100% sure tiles.
	int[][] distanceFromTiles;								  // Array containing the distance of the closest uncovered tile (including diagonal movements). Used for beginning of the game, when there is no empty tile plotted yet.
	public boolean[][] candidateTiles;						  // Each entry is true iff. the corresponding tile is unknown (see MinesweeperGame.Unkwon function) and is next to a uncovered tile
	boolean[][] assumedSafeTiles;							  // Contains the tiles we assume to be safe. Used for computing the minePossibilities array.
	boolean[][] assumedMinedTiles;							  // Contains the tiles we assume to have a mine. Used for computing the minePossibilities array.
	int[][] discoveredTiles;								  // Each entry contains the number of tile we uncover if we assume that the corresponding tile is safe, and if we assume that the tiles given by assumedSafeTiles (resp. assumedMinedTiles) are safe (resp. have a mine)	
	int[][] connectedComponents;							  // Computes connected components of candidate tiles. Useful for greatly improving speed of computation of minePossibilities, since each connected component are independent from the others
	int connectedComponentsNumber;		 					  // Contains the number of connected components
	static int[][] pascalTriangle = GetPascalTriangle(8);     // Pascal triangle with integer from 0 to 8, included. Used computing minePossibilities for the case where there is a single group.
	
	// Arrays used for computing the tile that has the less chance of having a mine, assuming that we have no 100% sure tile. Used by ComputeMinePossibilities, which has been replaced by ComputeMinePossibilitiesAccurate.
	long totalPossibilities;								  // Number of total possibilities given a game, and given which tiles have already been uncovered
	long[][] minePossibilities;							 	  // Each entry contains the number of total possibilities, assuming the corresponding tile has a mine. The smaller the number of possibilities, the less chance of having a mine at this tile.
	
	// Used by the "MinesweeperFrame" class once the solver finished its calculations
	int solvingStep;										  // Increment each time the solver performs an actions. Used for "step by step" mode, to display alternatively the safe tiles and the mined tiles
	int[][] actions;										  // Array containing the actions that will have to be played, once the solver finished making computations
	public int actionsNumber;										  // How many actions we will play
	
//	For debugging
	BufferedWriter writer;									  // Used to write stuff on a .txt file, to follow what the programs is computing
	
	
	
	
	
	// -----------------------------Instantiation-functions-------------------------------
	
	
	
	// Constructor, which argument is the minesweeper game we are playing
	public MinesweeperSolver(MinesweeperGame Game) {
		
		this.Game = Game;
		
		// Tile groups
		TileGroups = new ArrayList<TileGroup>();
		baseGroupsNumber = 0;
		groupsNumber = 0;
		groupsNumberBackup = -1;
		groupsPerTile = new ArrayList[Game.horizontalSize][Game.verticalSize];
		for(int i = 0; i<Game.horizontalSize; ++i)
		{
			for(int j = 0; j<Game.verticalSize; ++j)
			{
				groupsPerTile[i][j] = new ArrayList<Integer>();
			}
		}
		groupsProcess = new ArrayList<int[]>();
		
		// Arrays containing 100% sure tiles
		detectedSafe = new boolean[Game.horizontalSize][Game.verticalSize];
		for(int i = 0; i<Game.horizontalSize; ++i)
		{
			for(int j = 0; j<Game.verticalSize; ++j)
			{
				detectedSafe[i][j] = false;
			}
		}
		detectedMines = new boolean[Game.horizontalSize][Game.verticalSize];
		for(int i = 0; i<Game.horizontalSize; ++i)
		{
			for(int j = 0; j<Game.verticalSize; ++j)
			{
				detectedMines[i][j] = false;
			}
		}
		
		// Miscellaneous arrays, containing various informations. Most of them are used by functions called when we have no 100% sure tiles.
		distanceFromTiles = new int[Game.horizontalSize][Game.verticalSize];
		for(int i = 0; i<Game.horizontalSize; ++i)
		{
			for(int j = 0; j<Game.verticalSize; ++j)
			{
				distanceFromTiles[i][j] = 0;
			}
		}
		candidateTiles = new boolean[Game.horizontalSize][Game.verticalSize];
		for(int i = 0; i<Game.horizontalSize; ++i)
		{
			for(int j = 0; j<Game.verticalSize; ++j)
			{
				candidateTiles[i][j] = false;
			}
		}
		assumedSafeTiles = new boolean[Game.horizontalSize][Game.verticalSize];
		for(int i = 0; i<Game.horizontalSize; ++i)
		{
			for(int j = 0; j<Game.verticalSize; ++j)
			{
				assumedSafeTiles[i][j] = false;
			}
		}
		assumedMinedTiles = new boolean[Game.horizontalSize][Game.verticalSize];
		for(int i = 0; i<Game.horizontalSize; ++i)
		{
			for(int j = 0; j<Game.verticalSize; ++j)
			{
				assumedMinedTiles[i][j] = false;
			}
		}
		discoveredTiles = new int[Game.horizontalSize][Game.verticalSize];
		for(int i = 0; i<Game.horizontalSize; ++i)
		{
			for(int j = 0; j<Game.verticalSize; ++j)
			{
				discoveredTiles[i][j] = 0;
			}
		}
		connectedComponents = new int[Game.horizontalSize][Game.verticalSize];
		for(int i = 0; i<Game.horizontalSize; ++i)
		{
			for(int j = 0; j<Game.verticalSize; ++j)
			{
				connectedComponents[i][j] = 0;
			}
		}
		connectedComponentsNumber = 0;
		
		// Arrays used for computing the tile that has the less chance of having a mine, assuming that we have no 100% sure tile
		totalPossibilities = 0;
		minePossibilities = new long[Game.horizontalSize][Game.verticalSize];
		for(int i = 0; i<Game.horizontalSize; ++i)
		{
			for(int j = 0; j<Game.verticalSize; ++j)
			{
				minePossibilities[i][j] = 0;
			}
		}
		
		// Used by the "MinesweeperFrame" class once the solver finished its calculations
		solvingStep = 0;
		actions = new int[Game.horizontalSize][Game.verticalSize];		
		for(int i = 0; i<Game.horizontalSize; ++i)
		{
			for(int j = 0; j<Game.verticalSize; ++j)
			{
				actions[i][j] = 0;
			}
		}
		actionsNumber = 0;
	}
	
	// Empty constructor, used by the SubMinesweeperSolver class
	public MinesweeperSolver() {
		
	}
	
	// Resets the solver. Called after "MinesweeperFrame" has finished executing previous actions, in order to compute the next actions, or in order to compute the first action of the next game.
	// The argument resetWholeGame indicates in which of the two cases above we are
	public void Reset(MinesweeperGame Game, boolean resetWholeGame) {
		
		this.Game = Game;
		
		// Tile groups
		TileGroups = new ArrayList<TileGroup>();
		baseGroupsNumber = 0;
		groupsNumber = 0;
		groupsNumberBackup = -1;
		groupsPerTile = new ArrayList[Game.horizontalSize][Game.verticalSize];
		for(int i = 0; i<Game.horizontalSize; ++i)
		{
			for(int j = 0; j<Game.verticalSize; ++j)
			{
				groupsPerTile[i][j] = new ArrayList<Integer>();
			}
		}
		groupsProcess = new ArrayList<int[]>();
		
		// Arrays containing 100% sure tiles
		detectedSafe = new boolean[Game.horizontalSize][Game.verticalSize];
		for(int i = 0; i<Game.horizontalSize; ++i)
		{
			for(int j = 0; j<Game.verticalSize; ++j)
			{
				detectedSafe[i][j] = false;
			}
		}
		detectedMines = new boolean[Game.horizontalSize][Game.verticalSize];
		for(int i = 0; i<Game.horizontalSize; ++i)
		{
			for(int j = 0; j<Game.verticalSize; ++j)
			{
				detectedMines[i][j] = false;
			}
		}
		
		// Miscellaneous arrays, containing various informations. Most of them are used by functions called when we have no 100% sure tiles.
		distanceFromTiles = new int[Game.horizontalSize][Game.verticalSize];
		for(int i = 0; i<Game.horizontalSize; ++i)
		{
			for(int j = 0; j<Game.verticalSize; ++j)
			{
				distanceFromTiles[i][j] = 0;
			}
		}
		candidateTiles = new boolean[Game.horizontalSize][Game.verticalSize];
		for(int i = 0; i<Game.horizontalSize; ++i)
		{
			for(int j = 0; j<Game.verticalSize; ++j)
			{
				candidateTiles[i][j] = false;
			}
		}
		assumedSafeTiles = new boolean[Game.horizontalSize][Game.verticalSize];
		for(int i = 0; i<Game.horizontalSize; ++i)
		{
			for(int j = 0; j<Game.verticalSize; ++j)
			{
				assumedSafeTiles[i][j] = false;
			}
		}
		assumedMinedTiles = new boolean[Game.horizontalSize][Game.verticalSize];
		for(int i = 0; i<Game.horizontalSize; ++i)
		{
			for(int j = 0; j<Game.verticalSize; ++j)
			{
				assumedMinedTiles[i][j] = false;
			}
		}
		discoveredTiles = new int[Game.horizontalSize][Game.verticalSize];
		for(int i = 0; i<Game.horizontalSize; ++i)
		{
			for(int j = 0; j<Game.verticalSize; ++j)
			{
				discoveredTiles[i][j] = 0;
			}
		}
		connectedComponents = new int[Game.horizontalSize][Game.verticalSize];
		for(int i = 0; i<Game.horizontalSize; ++i)
		{
			for(int j = 0; j<Game.verticalSize; ++j)
			{
				connectedComponents[i][j] = 0;
			}
		}
		connectedComponentsNumber = 0;
		// pascalTriangle not reseted since it doesn't depend on game's dimensions
		
		// Used by the "MinesweeperFrame" class once the solver finished its calculations
		totalPossibilities = 0;
		minePossibilities = new long[Game.horizontalSize][Game.verticalSize];
		for(int i = 0; i<Game.horizontalSize; ++i)
		{
			for(int j = 0; j<Game.verticalSize; ++j)
			{
				minePossibilities[i][j] = 0;
			}
		}
		
		// Used by the "MinesweeperFrame" class once the solver finished its calculations
		// If reset the whole game, we reset solvingStep to 0
		if(resetWholeGame)
			solvingStep = 0;
		actions = new int[Game.horizontalSize][Game.verticalSize];		
		for(int i = 0; i<Game.horizontalSize; ++i)
		{
			for(int j = 0; j<Game.verticalSize; ++j)
			{
				actions[i][j] = 0;
			}
		}
		actionsNumber = 0;
	}
	
	// Returns the Pascal Triangle of number between 0 and n. Used for computing pascalTriangle.
	public static int[][] GetPascalTriangle(int n)
	{
		int[][] ans = new int[n+1][n+1];
		
		// ans[i][j] = j combination i
		for(int i = 0; i<=n; ++i)
		{
			ans[0][i] = 0;
			ans[i][0] = 1;
		}
		for(int i = 1; i<=n; ++i)
		{
			for(int j = 1; j<=n; ++j)
			{
				ans[i][j] = ans[i-1][j-1] + ans[i-1][j];
			}
		}
		
		return ans;
	}
	
	
	
	// --------------------------------Getters-and-setters--------------------------------
	
	
	
	// getter for solvingStep
	public int getSolvingStep() {
		return solvingStep;
	}
	
	// getter for groupsNumber
	public int getGroupsNumber() {
		return groupsNumber;
	}
	
	
	
	//--------------------------------Beginning-of-the-game-------------------------------
	
	
	
	// Calculates the distance of the closest uncovered tile for the whole grid, i.e. the smallest number of steps (N-S-E-W or diagonally) we have to do to get to an uncovered tile.
	// Then artificially adds the maximum distance (i.e. the max between the game width and game height) to the border tiles that have a distance greater than 3, so we choose a tile at the border and increase chance of having a tile equals to 0.
	// We only choose the tiles that have a distance greater than 3, because those who don't have a neighbor in common with another uncovered tile, so it doesn't make sense to choose it, and it's better to choose a tile in the middle.
	// The tile with the greatest distance will then be chosen by the next function
	public void ComputeDistanceFromTiles() {
		
		// Instantiates the array to the maximum integer value
		for(int i = 0; i<Game.horizontalSize; ++i)
		{
			for(int j = 0 ; j<Game.verticalSize; ++j)
			{
				distanceFromTiles[i][j] = Integer.MAX_VALUE; 
			}
		}
		
		// For each uncovered tile, updates the whole distanceFromTiles array
		for(int ti = 0; ti<Game.horizontalSize; ++ti)
		{
			for(int tj = 0; tj<Game.verticalSize; ++tj)
			{
				if(!Game.Unkown(ti,  tj))
				{
					// Counts the number of unknown neighbors the tile has. If it has no unknown tiles, it's useless updating the distanceFromTiles array. Increases computational speed.
					int neighbourUnkownTiles = 0;
					for(int ati = Math.max(ti-1,0); ati <= Math.min(ti+1, Game.horizontalSize-1); ++ati)
					{
						for(int atj = Math.max(tj-1,0); atj <= Math.min(tj+1, Game.verticalSize-1); ++atj) {
							if(Game.Unkown(ati,atj))
							{
								neighbourUnkownTiles++;
							}
						}
					}
					
					// If the tile has at least one neighbor that is unknown, updates the whole distanceFromTiles array
					if(neighbourUnkownTiles > 0)
					{
						for(int i = 0; i<Game.horizontalSize; ++i)
						{
							for(int j = 0; j<Game.verticalSize; ++j)
							{
								distanceFromTiles[i][j] = Math.min(distanceFromTiles[i][j], Math.max(Math.abs(j-tj), Math.abs(i-ti)));
							}
						}
					}
					//Otherwise, we just change the distanceFromTiles array at the position (ti,tj)
					else
					{
						distanceFromTiles[ti][tj] = 0;
					}
				}
			}
		}
		
		// A number greater than the maximum distance we can have using the method above
		int maxDistance = Math.max(Game.horizontalSize, Game.verticalSize);
		// For the tiles that are on the border and which distance are greater than 3, adds maxDistance to their distance, so these tiles are chosen in priority
		// If distance is smaller than 3, then it will have a common neighbor with another uncovered tile so it doesn't make sense to choose it.
		for(int i = 0; i<Game.horizontalSize; ++i)
		{
			if(distanceFromTiles[i][0] >= 3)
				distanceFromTiles[i][0] += maxDistance;
			if(distanceFromTiles[i][Game.verticalSize-1] >= 3)
				distanceFromTiles[i][Game.verticalSize-1] += maxDistance;
		}
		for(int j = 0; j<Game.verticalSize; ++j)
		{
			if(distanceFromTiles[0][j] >= 3)
				distanceFromTiles[0][j] += maxDistance;
			if(distanceFromTiles[Game.horizontalSize-1][j] >= 3)
				distanceFromTiles[Game.horizontalSize-1][j] += maxDistance;
		}
	}
	
	// Search for an empty tile (i.e. that has no neighboring mine), beginning with the tiles that are in the corner to get maximum chances
	// Once all the empty tiles are uncovered, search for an empty tile on the border, using the previous function
	// Once an empty tile is uncovered, this function is never called again until the end of the game
	public int[] SearchEmptyTile() {
		
		int[] chosenPos = new int[2];
		
		// First try
		if(Game.Unkown(0,0))
		{
			chosenPos[0] = 0; 
			chosenPos[1] = 0;
		}
		// Second try
		else if(Game.Unkown(0,Game.verticalSize-1))
		{
			chosenPos[0] = 0; 
			chosenPos[1] = Game.verticalSize-1;
		}
		// Third try
		else if(Game.Unkown(Game.horizontalSize-1,0))
		{
			chosenPos[0] = Game.horizontalSize-1; 
			chosenPos[1] = 0;	
		}
		// Fourth try
		else if(Game.Unkown(Game.horizontalSize-1,Game.verticalSize-1))
		{
			chosenPos[0] = Game.horizontalSize-1; 
			chosenPos[1] = Game.verticalSize-1;	
		}
		// If no corner gives an empty tile, tries the tiles on the border, using previous function to choose the tile that has the greatest distance
		else
		{
			// Computes the distance from uncovered tiles
			ComputeDistanceFromTiles();
			
			// Chooses a maximal distance tile. If several tiles have the maximum distance, one of them is randomly chosen (so it's not always the same tiles that are chosen, so it feels more "natural")
			double max = -1;
			int argmaxSize = 0;
			int iChosen = -1;
			int jChosen = -1;
			
			for(int i = 0; i<Game.horizontalSize; ++i)
			{
				for(int j = 0; j<Game.verticalSize; ++j)
				{
					if(distanceFromTiles[i][j] > max)
					{
						max = distanceFromTiles[i][j];
						argmaxSize = 1;
						iChosen = i;
						jChosen = j;
					}
					else if(distanceFromTiles[i][j] == max)
					{
						argmaxSize++;
					}
				}
			}
			
			Random random = new Random();
			
			int chosenInd = random.nextInt(argmaxSize);
			int ind = 0;
			
			if(argmaxSize == 1)
			{
				chosenPos[0] = iChosen;
				chosenPos[1] = jChosen;
			}
			// Chooses a maximum distance tile randomly, if there are several of them
			else
			{
				for(int i = 0; i<Game.horizontalSize; ++i)
				{
					for(int j = 0; j<Game.verticalSize; ++j)
					{
						if(distanceFromTiles[i][j] == max)
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
			}
		}
		
		return chosenPos;
	}
	
	
	
	// ------------------------------Computing-tile-groups--------------------------------
	
	
	
	// Auxiliary function used to retain how each group has been created. Used for next function	
	// method represents if the created group corresponds to secGroup1, secGroup2, or secGroup3 (see SeparateTwoGroups function)
	public void AddProcess(int groupInd1, int groupInd2, int index, int method){
		
		if(groupInd1 >= groupsNumberBackup || groupInd2 >= groupsNumberBackup) 
		{
			// "groups number groupInd1 and groupInd2 created group number index, using the method number method"
			int[] process = {groupInd1, groupInd2, index, method};
			groupsProcess.add(process);
		}
	}
	
	// Adds a tile group passed in argument to TileGroups. 
	// The group is added iff it didn't existed previously in TileGroups (i.e. if no group had the same tiles), OR if it existed but we are adding information to it with the new group (ie. minMines is greater, or maxMines is smaller)
	// The function returns true iff the group is indeed added to TileGroups
	// groupInd1 and groupInd2 are the indexes of the groups from which we created the group we are trying to add, and method represents if it corresponds to secGroup1, secGroup2, or secGroup3 (see SeparateTwoGroups function). Used for retaining how each group was created, to speed up SubMinesweeperSolver computation.
	public boolean AddGroup(TileGroup Group, int groupInd1, int groupInd2, int method)
	{
		
		// If group is empty, we add nothing to TileGroups, but we still specify it in groupsProcess
		if(Group.size == 0)
		{
			AddProcess(groupInd1, groupInd2, -1, method);
			return false;
		}
		else
		{
			// Index to the group in TileGroups that equals (i.e. has the same tiles) our argument group. Equals -1 if there is none.
			int matchingIndex = -1;
			
			for(Integer gInd: groupsPerTile[Group.iArray[0]][Group.jArray[0]])
			{
				TileGroup candGroup = TileGroups.get(gInd);
				
				// True if group and candGroup have the same elements, false otherwise
				boolean sameElements = true;
				
				// Compute sameElements
				if(Group.size != candGroup.size)
				{
					sameElements = false;
				}
				else
				{
					for(int ind = 0; ind<Group.size; ++ind)
					{
						if(!candGroup.Contains(Group.iArray[ind], Group.jArray[ind]))
						{
							sameElements = false;
						}
					}
				}
				
				if(sameElements)
				{
					matchingIndex = gInd;
				}
			}
			
			// If there is a group that equals our group passed in argument, we check whether we are adding information to it with the new group (ie. minMines is greater, or maxMines is smaller).
			if(matchingIndex >= 0)
			{
				AddProcess(groupInd1, groupInd2, matchingIndex, method);
				TileGroup matchingGroup = TileGroups.get(matchingIndex);
				
				if(Group.minMines <= matchingGroup.minMines && Group.maxMines >= matchingGroup.maxMines) {
					return false;
				}
				else
				{
					matchingGroup.minMines = Math.max(matchingGroup.minMines, Group.minMines);
					matchingGroup.maxMines = Math.min(matchingGroup.maxMines, Group.maxMines);
					return true;
				}
			}
			// If no group equals the new group, we add it to TileGroups.
			else
			{
				TileGroups.add(groupsNumber, Group);
				AddProcess(groupInd1, groupInd2, groupsNumber, method);
				for(int ind = 0; ind<Group.size; ++ind)
				{
					groupsPerTile[Group.iArray[ind]][Group.jArray[ind]].add(groupsNumber);
				}
				groupsNumber++;
				return true;
			}
		}
		
	}
	
	// Computes the tile groups that come from tiles that are uncovered
	public void ComputeBaseTileGroups() {
		
		TileGroups = new ArrayList<TileGroup>();
		groupsNumber = 0;
		for(int i = 0; i<Game.horizontalSize; ++i)
		{
			for(int j = 0; j<Game.verticalSize; ++j)
			{
				if(Game.plottedGrid[i][j] && Game.grid[i][j] >= 1)
				{
					// If tiles are assumed to be safe or mined, doesn't add them into created groups
					TileGroup group = new TileGroup(Game, i, j, assumedSafeTiles, assumedMinedTiles);
					if(group.size>0)
					{
						// Group is "created by groups -1 and -1", to say that they are created from nothing
						if(AddGroup(group, -1, -1, 0))
							baseGroupsNumber++;
					}
				}
			}
		}
		
	}
	
	// Separates two groups, creating 3 new groups, that will possibly be added to TileGroups
	// Returns an int corresponding to the number of groups added to TileGroups
	public int SeparateTwoTileGroups(TileGroup Group1, TileGroup Group2, int indGroup1, int indGroup2)
	{
		// Number of groups added
		int addedGroups = 0;
		
		// Tiles that are in group1 but not group2
		TileGroup secGroup1 = new TileGroup();
		// Tiles that are in both groups
		TileGroup secGroup2 = new TileGroup();
		// Tiles that are in group2 but not group1
		TileGroup secGroup3 = new TileGroup();
		
		for(int ind = 0; ind<Group1.size; ++ind)
		{
			if(!Group2.Contains(Group1.iArray[ind], Group1.jArray[ind]))
			{
				secGroup1.Add(Group1.iArray[ind], Group1.jArray[ind]);
			}
			else
			{
				secGroup2.Add(Group1.iArray[ind], Group1.jArray[ind]);
			}
		}
		for(int ind = 0; ind<Group2.size; ++ind)
		{
			if(!Group1.Contains(Group2.iArray[ind],Group2.jArray[ind]))
			{
				secGroup3.Add(Group2.iArray[ind], Group2.jArray[ind]);
			}
		}
		
		// Computes minimum and maximum number of mines for each group, using the informations we have
		secGroup2.minMines = Math.max(Math.max(Group1.minMines-secGroup1.size, Group2.minMines-secGroup3.size), 0);
		secGroup2.maxMines = Math.min(Math.min(Group1.maxMines, Group2.maxMines), secGroup2.size);
		secGroup1.minMines = Math.max(Group1.minMines-secGroup2.maxMines,0);
		secGroup1.maxMines = Math.min(Group1.maxMines-secGroup2.minMines,secGroup1.size);
		secGroup3.minMines = Math.max(Group2.minMines-secGroup2.maxMines,0);
		secGroup3.maxMines = Math.min(Group2.maxMines-secGroup2.minMines,secGroup3.size);
		

		if(AddGroup(secGroup2, indGroup1, indGroup2, 2))
		{
			addedGroups++;
		}
		if(AddGroup(secGroup1, indGroup1, indGroup2, 1))
		{
			addedGroups++;
		}
		if(AddGroup(secGroup3, indGroup1, indGroup2, 3))
		{
			addedGroups++;
		}
		
		return addedGroups;
		
	}
	
	// Takes every group in TileGroups, and separates them, using the above function
	// Returns the number of added groups during the process
	// This function is repeated until no new group is created
	public int SeparateTileGroups()
	{
		// Total number of added groups
		int totalAddedGroups = 0;
		
		// SeparatedGroups[gi][gj] is true iff groups with index gi and gj in TileGroups have tiles in common, in which case they will be separated. For avoiding separating groups that have no tiles in common, for computational speed.
		separatedGroups = new boolean[groupsNumber][groupsNumber];
		for(int gi = 0; gi<groupsNumber; ++gi)
		{
			for(int gj = 0; gj<groupsNumber; ++gj)
			{
				separatedGroups[gi][gj] = false;
			}
		}
		for(int gi = 0; gi<groupsNumber; ++gi)
		{
			TileGroup group = TileGroups.get(gi);
			// Uses groupsPerTile to know which groups have tiles in common, for computational speed.
			for(int ind = 0; ind<group.size; ++ind)
			{
				for(Integer gj: groupsPerTile[group.iArray[ind]][group.jArray[ind]])
				{
					if(gj>gi)
					{
						separatedGroups[gi][gj] = true;
					}
				}
			}
		}
		
		// Remember the number of groups before separation, to compute groupsProcess faster
		int retainedGroupsNumber = groupsNumber;
		
		// Separates groups
		for(int gi = 0; gi<retainedGroupsNumber; ++gi) {
			TileGroup group1 = TileGroups.get(gi);
			for(int gj = gi+1; gj<retainedGroupsNumber; ++gj)
			{
				if(separatedGroups[gi][gj])
				{
					TileGroup group2 = TileGroups.get(gj);
					int createdGroups = SeparateTwoTileGroups(group1, group2, gi, gj);
					totalAddedGroups += createdGroups;
				}
			}
		}
		
		groupsNumberBackup = retainedGroupsNumber;
		
		return totalAddedGroups;
	}
	
	
	
	// --------------------------------Deducing-actions-----------------------------------
	
	
	
	// Once the groups are computed, deduces the tiles that are safe
	public void ComputeDetectedSafe() {
		for(int i = 0; i<Game.horizontalSize; ++i)
		{
			for(int j = 0; j<Game.verticalSize; ++j)
			{
				detectedSafe[i][j] = false;
			}
		}
		// If a group is detected to have no mines, each tile of this group is safe
		for(TileGroup group: TileGroups)
		{
			if(group.maxMines == 0)
			{
				for(int ind = 0; ind<group.size; ++ind)
				{
					detectedSafe[group.iArray[ind]][group.jArray[ind]] = true;
				}
			}
		}
	}
	
	// Once the groups are computed, deduces the tiles that have a mine
	public void ComputeDetectedMines() {
		
		for(int i = 0; i<Game.horizontalSize; ++i)
		{
			for(int j = 0; j<Game.verticalSize; ++j)
			{
				detectedMines[i][j] = false;
			}
		}
		// If the minimum number of mines is greater than the sie of the group, every tile of the group has a mine
		for(TileGroup group: TileGroups)
		{
			if(group.minMines >= group.size) 
			{
				for(int ind = 0; ind<group.size; ++ind)
				{
					detectedMines[group.iArray[ind]][group.jArray[ind]] = true;
				}
			}
		}
	}
	
	// Computes the actions that have to be played, and returns the number of actions played
	public void ComputeActions(){
		
		// Resets the solver
		Reset(Game, false);
		solvingStep++;
		
		// Computes the base groups
		ComputeBaseTileGroups();
		int createdGroups = 0;
		// Separates the groups, until no new group is added
		do {
			createdGroups = SeparateTileGroups();
		} while(createdGroups>0);
		// Deduces the safe and mined tiles
		ComputeDetectedSafe();
		ComputeDetectedMines();
		
		actionsNumber = 0;
		
		// Deduces actions that will have to be done. 1 means we will uncover the tile, 2 means we will set a flag on it, 0 means we will do nothing.
		for(int i = 0; i<Game.horizontalSize; ++i)
		{
			for(int j = 0; j<Game.verticalSize; ++j)
			{
				if(detectedSafe[i][j])
				{
					actions[i][j] = 1;
					actionsNumber++;
				}
				else if(detectedMines[i][j])
				{
					actions[i][j] = 2;
					actionsNumber++;
				}
				else
					actions[i][j] = 0;
			}
		}
	}
	
	// Same function as above, with tiles assumed to be empty or mined. Used computing minePossibilities
	public void ComputeActions(boolean[][] safeTiles, boolean[][] minedTiles){
		
		// Resets the solver
		Reset(Game, false);
		// Sets assumedSafeTiles and assumedMinedTiles
		for(int i = 0; i<Game.horizontalSize; ++i)
		{
			for(int j = 0; j<Game.verticalSize; ++j)
			{
				assumedSafeTiles[i][j] = safeTiles[i][j];
			}
		}
		for(int i = 0; i<Game.horizontalSize; ++i)
		{
			for(int j = 0; j<Game.verticalSize; ++j)
			{
				assumedMinedTiles[i][j] = minedTiles[i][j];
			}
		}
		solvingStep++;
		
		// Computes the base groups
		ComputeBaseTileGroups();
		int createdGroups = 0;
		// Separates the groups, until no new group is added
		do {
			createdGroups = SeparateTileGroups();
		} while(createdGroups>0);
		// Deduces the safe and mined tiles
		ComputeDetectedSafe();
		ComputeDetectedMines();
		
		actionsNumber = 0;
		
		// Deduces actions that will have to be done. 1 means we will uncover the tile, 2 means we will set a flag on it, 0 means we will do nothing.
		for(int i = 0; i<Game.horizontalSize; ++i)
		{
			for(int j = 0; j<Game.verticalSize; ++j)
			{
				if(detectedSafe[i][j])
				{
					actions[i][j] = 1;
					actionsNumber++;
				}
				else if(detectedMines[i][j])
				{
					actions[i][j] = 2;
					actionsNumber++;
				}
				else
					actions[i][j] = 0;
			}
		}
	}
	
	// Computes actions and returns it to "MinesweeperFrame" class
	public int[][] GetActions(){
		
		ComputeActions();
		return actions;
	}
	
	
	
	// --------------------------------If-no-actions-returned-----------------------------
	
	
	
	// If no actions are returned by the process above, we will compute the probability each tile has to be a mine and press the tile that has the less chances of being a mine.
	
	
	// Computes the tiles that are candidates (i.e. that are neighbor to uncovered tiles)
	public void ComputeCandidateTiles() {
		
		for(int i = 0; i<Game.horizontalSize; ++i)
		{
			for(int j = 0; j<Game.verticalSize; ++j)
			{
				candidateTiles[i][j] = false;
			}
		}
		
		for(TileGroup group: TileGroups)
		{
			for(int ind = 0; ind < group.size; ++ind)
			{
				candidateTiles[group.iArray[ind]][group.jArray[ind]] = true;
			}
		}
	}
	
	// Computes the number of tiles we find out if we assume that each candidate tile is empty
	public void ComputeDiscoveredTiles(){
		
		ComputeCandidateTiles();
		
		for(int i = 0; i<Game.horizontalSize; ++i)
		{
			for(int j = 0; j<Game.verticalSize; ++j)
			{
				discoveredTiles[i][j] = 0;
			}
		}
		
		MinesweeperSolver SecondarySolver = new MinesweeperSolver(Game);
		
		for(int i = 0; i<Game.horizontalSize; ++i)
		{
			for(int j = 0; j<Game.verticalSize; ++j)
			{
				if(candidateTiles[i][j])
				{
					SubMinesweeperSolver SubSolver = new SubMinesweeperSolver(this, i, j);
					if(!SubSolver.error)
					{
						SubSolver.ComputeActions();
						discoveredTiles[i][j] = SubSolver.actionsNumber;
					}
				}
			}
		}
	}
	
	// Assumes some safe tiles and some mined tiles
	public void AssumeTiles(boolean[][] safeTiles, boolean[][] minedTiles) {
		
		for(int i = 0; i<Game.horizontalSize; ++i)
		{
			for(int j = 0 ; j<Game.verticalSize; ++j)
			{
				assumedSafeTiles[i][j] = safeTiles[i][j];
				assumedMinedTiles[i][j] = minedTiles[i][j];
			}
		}
	}
	
	// Compute connected components of the tile groups. Greatly improves performance when computing every possibilities, since each connected components is independent from the others.
	public void ComputeConnectedComponents() {
		
		for(int i = 0; i<Game.horizontalSize; ++i)
		{
			for(int j = 0; j<Game.verticalSize; ++j)
			{
				connectedComponents[i][j] = 0;
			}
		}
		
		// List of lists. Each list is a connected component.
		ArrayList<ArrayList<int[]>> connectedComponentsList = new ArrayList<ArrayList<int[]>>();
		// For each group, we update the connected components list. We only use the base groups, since all the other groups are created using the same tiles.
		for(int groupInd = 0; groupInd<baseGroupsNumber; ++groupInd)
		{
			TileGroup group = TileGroups.get(groupInd);
			// True iff the group is connected to another group
			boolean connected = false;
			// List of tiles that will be added to the connected component. We do not add a tile that is already in a connected component.
			ArrayList<int[]> tilesList = new ArrayList<int[]>();
			// List of the indexes of connected components to which is connected the group
			ArrayList<Integer> matchingIndexes = new ArrayList<Integer>();
			// For every tile of the group, we check if it is already in a connected component
			for(int ind = 0; ind<group.size; ++ind)
			{
				int[] candTile = {group.iArray[ind], group.jArray[ind]};
				// True iff the tile is connected to another connected component
				boolean contained = false;
				int componentIndex = 0;
				// We check if the tile is already in a connected component, and if it is the case, we add the corresponding index to matchingIndexes
				for(ArrayList<int[]> connectedComponent : connectedComponentsList)
				{
					for(int[] tile: connectedComponent)
					{
						if(candTile[0] == tile[0] && candTile[1] == tile[1])
						{
							contained = true;
							if(!matchingIndexes.contains(componentIndex))
								matchingIndexes.add(componentIndex);
						}
					}
					componentIndex++;
				}
				// If the tile is connected to another connected component, it means the whole group is
				if(contained)
					connected = true;
				// Other wise, we add it to tilesList
				else
					tilesList.add(candTile);
			}
			Collections.sort(matchingIndexes);
			// If the group is connected to other connected components, then all of these connected components are merged into a single one, to which we will also add our group.
			if(connected)
			{
				connectedComponentsList.get(matchingIndexes.get(0)).addAll(tilesList);
				for(int k = matchingIndexes.size()-1; k>0; k--)
				{
					int matchingIndex = matchingIndexes.get(k);
					connectedComponentsList.get(matchingIndexes.get(0)).addAll(connectedComponentsList.get(matchingIndex));
					connectedComponentsList.remove(matchingIndex);
				}
			}
			// Otherwise, our group creates a new connected component
			else
			{
				connectedComponentsList.add(tilesList);
			}
		}
		
		// We finally compute the values of the connectedComponents array, using the connectedComponentsList list
		int componentIndex = 1;
		for(ArrayList<int[]> connectedComponent : connectedComponentsList)
		{
			for(int[] tile : connectedComponent)
			{
				connectedComponents[tile[0]][tile[1]] = componentIndex;
			}
			componentIndex++;
		}
		// We also retain the connectedComponentsNumber
		connectedComponentsNumber = connectedComponentsList.size();
	}
	
	// [/!\ FIRST VERSION OF THE FUNCTION, HAS BEEN REPLACED WITH THE ComputeMinePossibilitiesAccurate FUNCTION! /!\]
	// Recursive function, computing the number of possibilities where each candidate tile is a mine, and the total number of possibilities
	// We assume there are safe tiles and mined tiles, given by safeTiles and minedTiles
	// Every call of the function has a different safeTiles and minedTiles, in a way that every possibility is covered, in an efficient way.
	// Locally modifies passedMinePossibilities and returns the total number of possibilities
	// depth and uniqueConnectedComponents are arguments useful for computational speed, and guessingTileCount is used to follow what the function does step by step.
	// There is commented code all along the function, that can be uncommented to follow what the function does step by step, thanks to information written on .txt files in the "Solver_Debug" folder
	public long ComputeMinePossibilities(boolean[][] safeTiles, boolean[][] minedTiles, int depth, boolean uniqueConnectedComponent, long[][] passedMinePossibilities, int guessingTileCount){
		
		// Returns the total number of possibilities
		long returnedTotalPossibilities = 0;
		
		// Second solver, used all along the function.
		MinesweeperSolver SecondarySolver;
		// If depth = 0, i.e. if it is the main call of the function, then this solver is set to this and the minePossibilities argument is set to 0 everywhere.
		if(depth == 0)
		{
			for(int i = 0; i<Game.horizontalSize; ++i)
			{
				for(int j = 0; j<Game.verticalSize; ++j)
				{
					passedMinePossibilities[i][j] = 0;
				}
			}
			SecondarySolver = this;		
		}
		// Otherwise, we create a new solver, and set its assumed safe and mined tiles
		else {
			SecondarySolver = new MinesweeperSolver(Game);
			SecondarySolver.ComputeActions(safeTiles,minedTiles);
		}
		// uniqueConnectedComponent is a boolean that is true if we already know that there is a single connected component in the tile groups
		// If it is true, we know that we don't need to compute the connected components
		if(!uniqueConnectedComponent)
		{
//			try {
//				writer.write("We will compute connected components of solver with groups:");
//				writer.newLine();
//				SecondarySolver.WriteTileGroups(writer);
//				writer.newLine();
//				writer.newLine();
//				writer.newLine();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			// Update uniqueConnectedComponent if we find out there is only one connected component
			if(SecondarySolver.groupsNumber > 0)
				SecondarySolver.ComputeConnectedComponents();
			if(SecondarySolver.connectedComponentsNumber == 1)
				uniqueConnectedComponent = true;
			else
				uniqueConnectedComponent = false;
		}
		
		// First base case. If there is no group, then we have only one possibility, in which no tile has a mine.
		if(SecondarySolver.groupsNumber == 0)
		{
//			try {
//				writer.write("Integrating an empty group...");
//				writer.newLine();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			returnedTotalPossibilities = 1;
		}
		// Second base case. If we have a single group, then we can compute the number of possibility each tile has to be a mine. We use for that the pascal triangle we computed when we instantiated the solver.
		else if(SecondarySolver.groupsNumber == 1)
		{
//			try {
//				writer.write("Integrating a single group...:");
//				writer.newLine();
//				SecondarySolver.WriteTileGroups(writer);
//				writer.newLine();
//				writer.newLine();
//				writer.newLine();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			TileGroup group = SecondarySolver.TileGroups.get(0);
			
			int totalMinePos = 0;
			int minePosPerTile = 0;
			
			// We compute, for each tile, the number of possibilities where this tile has a mine
			// We also compute the total number of possibilities
			for(int mine = group.minMines; mine<=group.maxMines; ++mine)
			{
				totalMinePos += pascalTriangle[group.size][mine];
				if(mine != 0)
					minePosPerTile += pascalTriangle[group.size-1][mine-1];
			}
			returnedTotalPossibilities = totalMinePos;
			
			// For each tile of the group, we add the number of possibilities
			for(int ind = 0; ind<group.size; ++ind)
			{
				passedMinePossibilities[group.iArray[ind]][group.jArray[ind]] += minePosPerTile;
			}
			
		}
		
		// General case
		else {
			
			// If we don't have a unique connected component
			if(!uniqueConnectedComponent)
			{	
				
//			try {
//				writer.write("Integrating groups with several connected components...:");
//				writer.newLine();
//				SecondarySolver.WriteTileGroups(writer);
//				writer.newLine();
//				writer.newLine();
//				writer.newLine();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
				// Array containing the total number of possibilities for each connected component
				long[] totalPossiblitiesArray = new long[SecondarySolver.connectedComponentsNumber];
				
				// Same role as passedMinePossibilities, passed as an argument for every connected component and then used to deduce passedMinePossiblities for all the tiles
				long[][] tempPassedMinePossibilities = new long[Game.horizontalSize][Game.verticalSize];
				for(int i = 0; i<Game.horizontalSize; ++i)
				{
					for(int j = 0; j<Game.verticalSize; ++j)
					{
						tempPassedMinePossibilities[i][j] = 0;
					}
				}
				
				// We assume all the tiles that are not in the first connected components to be safe (i.e. we ignore them) 
				for(int i = 0; i<Game.horizontalSize; ++i)
				{
					for(int j = 0; j<Game.verticalSize; ++j)
					{
						if(SecondarySolver.connectedComponents[i][j] != 0 && SecondarySolver.connectedComponents[i][j] != 1)
						{
							safeTiles[i][j] = true;
						}
					}
				}
				
				for(int compNumber = 1; compNumber<=SecondarySolver.connectedComponentsNumber; ++compNumber)
				{
					
					// We compute the total possibilities for each connected component
					totalPossiblitiesArray[compNumber-1] = ComputeMinePossibilities(safeTiles, minedTiles, depth+1, true, tempPassedMinePossibilities, guessingTileCount);
//					try {
//						writer.write("Back to integrating groups with several connected components...");
//						writer.newLine();
//						SecondarySolver.WriteTileGroups(writer);
//						writer.newLine();
//						writer.newLine();
//						writer.newLine();
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
					
					// We change the tiles that are assumed to be safe, to be able to move on to the next connected component
					for(int i = 0; i<Game.horizontalSize; ++i)
					{
						for(int j = 0; j<Game.verticalSize; ++j)
						{
							if(SecondarySolver.connectedComponents[i][j] == compNumber)
							{
								safeTiles[i][j] = true;
							}
							if(SecondarySolver.connectedComponents[i][j] == compNumber+1)
							{
								safeTiles[i][j] = false;
							}
						}
					}
				}
				
				// We set safe tiles back to how it was before
				for(int i = 0; i<Game.horizontalSize; ++i)
				{
					for(int j = 0; j<Game.verticalSize; ++j)
					{
						if(SecondarySolver.connectedComponents[i][j] != 0)
						{
							safeTiles[i][j] = false;
						}
					}
				}
				
				returnedTotalPossibilities = 1;
				
				// We deduce the total number of possibilities
				for(int compNumber = 1; compNumber<=SecondarySolver.connectedComponentsNumber; ++compNumber)
				{
					returnedTotalPossibilities *= totalPossiblitiesArray[compNumber-1];
				}
				// We deduce the number of possibilities for each tile
				for(int i = 0; i<Game.horizontalSize; ++i)
				{
					for(int j = 0; j<Game.verticalSize; ++j)
					{
						// We update the number of possibilities for the tiles that are assumed to be mined
//						if(minedTiles[i][j])
//							tempPassedMinePossibilities[i][j] = returnedTotalPossibilities;
//						// We update the number of possibilities for all the candidate tiles
//						else
//						{
							int connectedComp = SecondarySolver.connectedComponents[i][j];
							if(connectedComp != 0)
							{
								for(int compNumber = 1; compNumber<=SecondarySolver.connectedComponentsNumber; ++compNumber)
								{
									if(compNumber != connectedComp)
									{
										tempPassedMinePossibilities[i][j] *= totalPossiblitiesArray[compNumber-1];
									}
								}
							}
//						}
					}
				}
				
				// We add the previous changes to passedMinePossibilities
				for(int i = 0; i<Game.horizontalSize; ++i)
				{
					for(int j = 0; j<Game.verticalSize; ++j)
					{
						passedMinePossibilities[i][j] += tempPassedMinePossibilities[i][j];
					}
				}
			}
			
			// If we have a single connected component
			else
			{
//				try {
//					writer.write("Integrating groups with a single connected component...");
//					writer.newLine();
//					SecondarySolver.WriteTileGroups(writer);
//					writer.newLine();
//					writer.newLine();
//					writer.newLine();
//					writer.write("Computing discovered tiles...");
//					writer.newLine();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
				
				SecondarySolver.ComputeDiscoveredTiles();

				int index = 0;
				int chosenInd = -1;
				int minPos = 257;
				int maxScore = -1;
				
//				try {
//					writer.write("Choosing group which tiles will be supposed...");
//					writer.newLine();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
				
				// We chose the best group to study, i.e. the group that has the smallest amount of tiles possibilities
				// And the group that gives us the most information when we click it
				for(TileGroup group: SecondarySolver.TileGroups)
				{
					int pos = 0;
					for(int mine = group.minMines; mine<=group.maxMines; ++mine)
					{
						pos += pascalTriangle[group.size][mine];
					}
					int score = 0;
					for(int ind = 0; ind<group.size; ++ind)
					{
						score += SecondarySolver.discoveredTiles[group.iArray[ind]][group.jArray[ind]];
					}
					if(pos < minPos)
					{
						chosenInd = index;
						minPos = pos;
						maxScore = score;
					}
					else if(pos == minPos && score > maxScore)
					{
						chosenInd = index;
						maxScore = score;
					}
					index++;
				}
				
				// Once we chose this group, we enumerate the possible mines it can have. We then recall the function for each possibility.
				TileGroup chosenGroup = SecondarySolver.TileGroups.get(chosenInd);
				ArrayList<boolean[]> possibilities = chosenGroup.EnumeratePossibilities();
				
				// Contains the safe tiles for each possible disposition of the chosen group
				boolean[][] subSafeTiles = new boolean[Game.horizontalSize][Game.verticalSize];
				for(int i = 0; i<Game.horizontalSize; ++i)
				{
					for(int j = 0; j<Game.verticalSize; ++j)
					{
						subSafeTiles[i][j] = false;
					}
				}
				// Contains the mined tiles for each possible disposition of the chosen group
				boolean[][] subMinedTiles = new boolean[Game.horizontalSize][Game.verticalSize];
				for(int i = 0; i<Game.horizontalSize; ++i)
				{
					for(int j = 0; j<Game.verticalSize; ++j)
					{
						subMinedTiles[i][j] = false;
					}
				}
//				try {
//					writer.write("Reiterating for different possibilities of the chosen group...");
//					writer.newLine();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
				
				// We iterate over the mine dispositions this group can have
				for(boolean[] possibility : possibilities)
				{
//					try {
//						writer.write("Assuming ");
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
					for(int ind = 0; ind<chosenGroup.size; ++ind)
					{
						if(!possibility[ind])
						{
//							try {
//								writer.write("tile (" + chosenGroup.iArray[ind] + "," + chosenGroup.jArray[ind] + ") is empty");
//							} catch (IOException e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							}
							subSafeTiles[chosenGroup.iArray[ind]][chosenGroup.jArray[ind]] = true;
							subMinedTiles[chosenGroup.iArray[ind]][chosenGroup.jArray[ind]] = false;
							safeTiles[chosenGroup.iArray[ind]][chosenGroup.jArray[ind]] = true;
						}
						else
						{
//							try {
//								writer.write("tile (" + chosenGroup.iArray[ind] + "," + chosenGroup.jArray[ind] + ") has a mine");
//							} catch (IOException e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							}
							subSafeTiles[chosenGroup.iArray[ind]][chosenGroup.jArray[ind]] = false;
							subMinedTiles[chosenGroup.iArray[ind]][chosenGroup.jArray[ind]] = true;
							minedTiles[chosenGroup.iArray[ind]][chosenGroup.jArray[ind]] = true;
						}
//						if(ind<chosenGroup.size-1)
//						{
//							try {
//								writer.write(" and ");
//							} catch (IOException e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							}
//						}
					}
//					try {
//						writer.newLine();
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
					
					// Sub-solver equal to SecondarySolver, with the tiles' assumptions given by subSafeTiles and subMinedTiles
					SubMinesweeperSolver SubSolver = new SubMinesweeperSolver(SecondarySolver, subSafeTiles, subMinedTiles);
//					try {
//						writer.write("Computing actions of sub-solver with groups:");
//						writer.newLine();
//						SubSolver.WriteTileGroups(writer);
//						writer.newLine();
//						writer.newLine();
//						writer.newLine();
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
					
					// If we get an error from SubSolver, it means that the disposition we are currently trying is impossible
					if(!SubSolver.error)
					{
						// We solve SubSolver and update SafeTiles and MinedTiles based on the solution we get
						SubSolver.ComputeActions();
						int[][] subSolverActions = SubSolver.actions;
//						try {
//							writer.write("Assumed empty tiles:");
//							writer.newLine();
//							WriteArray(writer, subEmptyTiles);
//							writer.write("Assumed mined tiles:");
//							writer.newLine();
//							WriteArray(writer, subMinedTiles);
//							writer.write("Obtained actions:");
//							writer.newLine();
//							WriteArray(writer, subSolverActions);
//							writer.newLine();
//							writer.newLine();
//							writer.newLine();
//						} catch (IOException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
						
						for(int i = 0; i<Game.horizontalSize; ++i)
						{
							for(int j = 0; j<Game.verticalSize; ++j)
							{
								if(subSolverActions[i][j] == 1)
									safeTiles[i][j] = true;
								else if(subSolverActions[i][j] == 2)
									minedTiles[i][j] = true;
							}
						}
						
						// We call the function with the new safeTiles and minedTiles
						long addedTotalPossibilities = ComputeMinePossibilities(safeTiles, minedTiles, depth+1, false, passedMinePossibilities, guessingTileCount);
						returnedTotalPossibilities += addedTotalPossibilities;
						// For each tile assumed to have a mine, we add the total number of possibilities 
						for(int i = 0; i<Game.horizontalSize; ++i)
						{
							for(int j = 0; j<Game.verticalSize; ++j)
							{
								if(subMinedTiles[i][j] || subSolverActions[i][j] == 2)
								{
									passedMinePossibilities[i][j] += addedTotalPossibilities;
								}
							}
						}
						
//						try {
//							writer.write("Back to groups with a single connected component...");
//							writer.newLine();
//							SecondarySolver.WriteTileGroups(writer);
//							writer.newLine();
//							writer.newLine();
//							writer.newLine();
//						} catch (IOException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
						
						// We set safeTiles and minedTiles as it was before
						for(int i = 0; i<Game.horizontalSize; ++i)
						{
							for(int j = 0; j<Game.verticalSize; ++j)
							{
								if(subSafeTiles[i][j])
									safeTiles[i][j] = false;
								if(subMinedTiles[i][j])
									minedTiles[i][j] = false;
								
								if(subSolverActions[i][j] == 1)
									safeTiles[i][j] = false;
								else if(subSolverActions[i][j] == 2)
									minedTiles[i][j] = false;
							}
						}
					}
					else
					{
//						try {
//							writer.write("This disposition is impossible!");
//							writer.newLine();
//						} catch (IOException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
						for(int i = 0; i<Game.horizontalSize; ++i)
						{
							for(int j = 0; j<Game.verticalSize; ++j)
							{
								if(subSafeTiles[i][j])
									safeTiles[i][j] = false;
								if(subMinedTiles[i][j])
									minedTiles[i][j] = false;
							}
						}
					}
				}
			}
		}
		
		return returnedTotalPossibilities;
	}
	
	// [IMPROVED VERSION OF THE ComputeMinePossibilitiesAccurate FUNCTION!]
	// Recursive function, computing the number of possibilities where each candidate tile is a mine, and the total number of possibilities.
	// This function separates the possibilities, depending on the number of mines they contain, which permits an exact computation of the probability of each tile to be a mine.
	// Indeed, there are different number of possibilities for the tiles outside the candidateTiles tiles according to how many mines we have in candidateTiles, and it is necessary to take this into account.
	// Returns the result using the MinesweeperPossibilities class.
	// We assume there are safe tiles and mined tiles, given by safeTiles and minedTiles.
	// Every call of the function has a different safeTiles and minedTiles, in a way that every possibility is covered, in an efficient way.
	// depth and uniqueConnectedComponents are arguments useful for computational speed, and guessingTileCount is used to follow what the function does step by step.
	// There is commented code all along the function, that can be uncommented to follow what the function does step by step, thanks to information written on .txt files in the "Solver_Debug" folder.
	public MinesweeperPossibilities ComputeMinePossibilitiesAccurate(boolean[][] safeTiles, boolean[][] minedTiles, int depth, boolean uniqueConnectedComponent, int guessingTileCount){
		
		// Second solver, used all along the function.
		MinesweeperSolver SecondarySolver;
		// If depth = 0, i.e. if it is the main call of the function, then this solver is set to this.
		if(depth == 0)
		{
			SecondarySolver = this;		
		}
		// Otherwise, we create a new solver, and set its assumed safe and mined tiles
		else {
			SecondarySolver = new MinesweeperSolver(Game);
			SecondarySolver.ComputeActions(safeTiles,minedTiles);		
		}
		// uniqueConnectedComponent is a boolean that is true if we already know that there is a single connected component in the tile groups
		// If it is true, we know that we don't need to compute the connected components
		if(!uniqueConnectedComponent)
		{
//			try {
//				writer.write("We will compute connected components of solver with groups:");
//				writer.newLine();
//				SecondarySolver.WriteTileGroups(writer);
//				writer.newLine();
//				writer.newLine();
//				writer.newLine();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			// Update uniqueConnectedComponent if we find out there is only one connected component
			if(SecondarySolver.groupsNumber > 0)
				SecondarySolver.ComputeConnectedComponents();
			if(SecondarySolver.connectedComponentsNumber == 1)
				uniqueConnectedComponent = true;
			else
				uniqueConnectedComponent = false;
		}
		
		// First base case. If there is no group, then there is only one possibility, with no tile being a mine.
		if(SecondarySolver.groupsNumber == 0)
		{
//			try {
//				writer.write("Integrating an empty group...");
//				writer.newLine();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			MinesweeperPossibilities computedPossibilities = new MinesweeperPossibilities(Game,false);
			
			return computedPossibilities;
		}
		// Second base case. If we have a single group, then we can compute the possibilities this group defines.
		else if(SecondarySolver.groupsNumber == 1)
		{
//			try {
//				writer.write("Integrating a single group...:");
//				writer.newLine();
//				SecondarySolver.WriteTileGroups(writer);
//				writer.newLine();
//				writer.newLine();
//				writer.newLine();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			TileGroup group = SecondarySolver.TileGroups.get(0);
			
			MinesweeperPossibilities computedPossibilities = new MinesweeperPossibilities(Game, group);
			
			return computedPossibilities;
			
		}
		
		// General case
		else {
			
			// If we don't have a unique connected component
			if(!uniqueConnectedComponent)
			{			
//			try {
//				writer.write("Integrating groups with several connected components...:");
//				writer.newLine();
//				SecondarySolver.WriteTileGroups(writer);
//				writer.newLine();
//				writer.newLine();
//				writer.newLine();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
				
				// We assume all the tiles that are not in the first connected components to be safe (i.e. we ignore them) 
				for(int i = 0; i<Game.horizontalSize; ++i)
				{
					for(int j = 0; j<Game.verticalSize; ++j)
					{
						if(SecondarySolver.connectedComponents[i][j] != 0 && SecondarySolver.connectedComponents[i][j] != 1)
						{
							safeTiles[i][j] = true;
						}
					}
				}
				// MinesweeperPossibilities that will be returned
				MinesweeperPossibilities computedPossibilities = new MinesweeperPossibilities(Game,false);
				
				for(int compNumber = 1; compNumber<=SecondarySolver.connectedComponentsNumber; ++compNumber)
				{
					
					// We compute the possibilities for each connected component
					MinesweeperPossibilities computedSubPossibilities = ComputeMinePossibilitiesAccurate(safeTiles, minedTiles, depth+1, true, guessingTileCount);
					// We "multiply" them with the possibilities that have already been computed
					computedPossibilities.MultiplyWith(computedSubPossibilities);
//					try {
//						writer.write("Back to integrating groups with several connected components...");
//						writer.newLine();
//						SecondarySolver.WriteTileGroups(writer);
//						writer.newLine();
//						writer.newLine();
//						writer.newLine();
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
					
					// We change the tiles that are assumed to be safe, to be able to move on to the next connected component
					for(int i = 0; i<Game.horizontalSize; ++i)
					{
						for(int j = 0; j<Game.verticalSize; ++j)
						{
							if(SecondarySolver.connectedComponents[i][j] == compNumber)
							{
								safeTiles[i][j] = true;
							}
							if(SecondarySolver.connectedComponents[i][j] == compNumber+1)
							{
								safeTiles[i][j] = false;
							}
						}
					}
				}
				
				// We set safe tiles back to how it was before
				for(int i = 0; i<Game.horizontalSize; ++i)
				{
					for(int j = 0; j<Game.verticalSize; ++j)
					{
						if(SecondarySolver.connectedComponents[i][j] != 0)
						{
							safeTiles[i][j] = false;
						}
					}
				}
				
				return computedPossibilities;
			}
			
			// If we have a single connected component
			else
			{
				
//				try {
//					writer.write("Integrating groups with a single connected component...");
//					writer.newLine();
//					SecondarySolver.WriteTileGroups(writer);
//					writer.newLine();
//					writer.newLine();
//					writer.newLine();
//					writer.write("Computing discovered tiles...");
//					writer.newLine();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}

				SecondarySolver.ComputeDiscoveredTiles();

				int index = 0;
				int chosenInd = -1;
				int minPos = 257;
				int maxScore = -1;
				
//				try {
//					writer.write("Choosing group which tiles will be supposed...");
//					writer.newLine();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
				
				// We chose the best group to study, ie. the group that has the smallest amount of tiles possibilities
				// And the group that gives us the most information when we click it
				for(TileGroup group: SecondarySolver.TileGroups)
				{
					int pos = 0;
					for(int mine = group.minMines; mine<=group.maxMines; ++mine)
					{
						pos += pascalTriangle[group.size][mine];
					}
					int score = 0;
					for(int ind = 0; ind<group.size; ++ind)
					{
						score += SecondarySolver.discoveredTiles[group.iArray[ind]][group.jArray[ind]];
					}
					if(pos < minPos)
					{
						chosenInd = index;
						minPos = pos;
						maxScore = score;
					}
					else if(pos == minPos && score > maxScore)
					{
						chosenInd = index;
						maxScore = score;
					}
					index++;
				}
				
				// Once we chose this group, we enumerate the possible mines it can have. We then recall the function for each possibility.
				TileGroup chosenGroup = SecondarySolver.TileGroups.get(chosenInd);
				ArrayList<boolean[]> possibilities = chosenGroup.EnumeratePossibilities();
				
				// Contains the safe tiles for each possible disposition of the chosen group
				boolean[][] subSafeTiles = new boolean[Game.horizontalSize][Game.verticalSize];
				for(int i = 0; i<Game.horizontalSize; ++i)
				{
					for(int j = 0; j<Game.verticalSize; ++j)
					{
						subSafeTiles[i][j] = false;
					}
				}
				// Contains the mined tiles for each possible disposition of the chosen group
				boolean[][] subMinedTiles = new boolean[Game.horizontalSize][Game.verticalSize];
				for(int i = 0; i<Game.horizontalSize; ++i)
				{
					for(int j = 0; j<Game.verticalSize; ++j)
					{
						subMinedTiles[i][j] = false;
					}
				}
				// MinesweeperPossibilities that will be returned
				MinesweeperPossibilities computedPossibilities = new MinesweeperPossibilities(Game,true);
//				try {
//					writer.write("Reiterating for different possibilities of the chosen group...");
//					writer.newLine();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
				
				// We iterate over the mine dispositions the group can have
				for(boolean[] possibility : possibilities)
				{
//					try {
//						writer.write("Assuming ");
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
					
					TileList minedTilesList = new TileList();
					
					// We update the safe and mined tiles for each disposition of the group
					for(int ind = 0; ind<chosenGroup.size; ++ind)
					{
						if(!possibility[ind])
						{
//							try {
//								writer.write("tile (" + chosenGroup.iArray[ind] + "," + chosenGroup.jArray[ind] + ") is empty");
//							} catch (IOException e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							}
							subSafeTiles[chosenGroup.iArray[ind]][chosenGroup.jArray[ind]] = true;
							subMinedTiles[chosenGroup.iArray[ind]][chosenGroup.jArray[ind]] = false;
							safeTiles[chosenGroup.iArray[ind]][chosenGroup.jArray[ind]] = true;
						}
						else
						{
//							try {
//								writer.write("tile (" + chosenGroup.iArray[ind] + "," + chosenGroup.jArray[ind] + ") has a mine");
//							} catch (IOException e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							}
							subSafeTiles[chosenGroup.iArray[ind]][chosenGroup.jArray[ind]] = false;
							subMinedTiles[chosenGroup.iArray[ind]][chosenGroup.jArray[ind]] = true;
							minedTilesList.Add(chosenGroup.iArray[ind], chosenGroup.jArray[ind]);
							minedTiles[chosenGroup.iArray[ind]][chosenGroup.jArray[ind]] = true;
						}
//						if(ind<chosenGroup.size-1)
//						{
//							try {
//								writer.write(" and ");
//							} catch (IOException e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							}
//						}
					}
//					try {
//						writer.newLine();
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
					
					// Sub-solver equal to SecondarySolver, with the tiles' assumptions given by subSafeTiles and subMinedTiles
					SubMinesweeperSolver SubSolver = new SubMinesweeperSolver(SecondarySolver, subSafeTiles, subMinedTiles);
//					try {
//						writer.write("Computing actions of sub-solver with groups:");
//						writer.newLine();
//						SubSolver.WriteTileGroups(writer);
//						writer.newLine();
//						writer.newLine();
//						writer.newLine();
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
					
					// If we get an error from SubSolver, it means that the disposition we are currently trying is impossible
					if(!SubSolver.error)
					{
						// We solve SubSolver and update SafeTiles and MinedTiles based on the solution we get
						SubSolver.ComputeActions();
						int[][] subSolverActions = SubSolver.actions;
//						try {
//							writer.write("Assumed empty tiles:");
//							writer.newLine();
//							WriteArray(writer, subSafeTiles);
//							writer.write("Assumed mined tiles:");
//							writer.newLine();
//							WriteArray(writer, subMinedTiles);
//							writer.write("Obtained actions:");
//							writer.newLine();
//							WriteArray(writer, subSolverActions);
//							writer.newLine();
//							writer.newLine();
//							writer.newLine();
//						} catch (IOException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
						
						for(int i = 0; i<Game.horizontalSize; ++i)
						{
							for(int j = 0; j<Game.verticalSize; ++j)
							{
								if(subSolverActions[i][j] == 1)
									safeTiles[i][j] = true;
								else if(subSolverActions[i][j] == 2)
								{
									minedTiles[i][j] = true;
									minedTilesList.Add(i, j);
								}
							}
						}
						
						// We call the function with the new safeTiles and minedTiles
						
						
						
						
						// We compute the possibilities for each disposition of the group
						MinesweeperPossibilities computedSubPossibilities = ComputeMinePossibilitiesAccurate(safeTiles, minedTiles, depth+1, false, guessingTileCount);
						// We add the total number of possibilities to the tiles that have been assumed to be mined in this disposition
						computedSubPossibilities.UpdateMinedTiles(minedTilesList);
						// We "Add" them to the possibilities that have already been computed
						computedPossibilities.Add(computedSubPossibilities);
						
						
						
						
//						try {
//							writer.write("Back to groups with a single connected component...");
//							writer.newLine();
//							SecondarySolver.WriteTileGroups(writer);
//							writer.newLine();
//							writer.newLine();
//							writer.newLine();
//						} catch (IOException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
						
						// We set safeTiles and minedTiles as it was before
						for(int i = 0; i<Game.horizontalSize; ++i)
						{
							for(int j = 0; j<Game.verticalSize; ++j)
							{
								if(subSafeTiles[i][j])
									safeTiles[i][j] = false;
								if(subMinedTiles[i][j])
									minedTiles[i][j] = false;
								
								if(subSolverActions[i][j] == 1)
									safeTiles[i][j] = false;
								else if(subSolverActions[i][j] == 2)
									minedTiles[i][j] = false;
							}
						}
					}
					else
					{
//						try {
//							writer.write("This disposition is impossible!");
//							writer.newLine();
//						} catch (IOException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
						for(int i = 0; i<Game.horizontalSize; ++i)
						{
							for(int j = 0; j<Game.verticalSize; ++j)
							{
								if(subSafeTiles[i][j])
									safeTiles[i][j] = false;
								if(subMinedTiles[i][j])
									minedTiles[i][j] = false;
							}
						}
					}
				}				
				
				return computedPossibilities;
				
			}
		}
	}
	
	// Using the possibilities found in the above function, deduces the probability for each candidate tile to be a mine.
	// notCandidateNumber is the number of tiles that are unknown and that are not candidate (i.e. that are not next to an uncovered tile).
	// minesLeft is the number of mines left.
	// The probability will depend on these two values.
	public double[][] DeduceProbabilitiesAccurate(MinesweeperPossibilities computedPossibilities, int notCandidateNumber, int minesLeft)
	{
		// Array that will be returned
		double[][] probabilities = new double[Game.horizontalSize][Game.verticalSize];
		
		// Each "element" of the MinesweeperPossibilities (i.e. each triplet of mines+totalPossibilities+minePossibilities corresponding to a mine number) will be weighted by a factor
		// That's because if we have m mines, minesLeft mines left and notCandidateNumber tiles that are empty and not candidate, then the number of possibilities for these tiles is notCandidateTiles choose minesLeft-m
		// The factor of multiplication is deduced from this, in a way not to get a value that is too big
		int smallestMinesNumber = computedPossibilities.minesNumberList.get(0);
		int biggestMinesNumber = computedPossibilities.minesNumberList.get(computedPossibilities.minesNumberList.size()-1);
		// Factors of multiplication. We use BigInteger because the numbers we compute can get big, larger than the long maximum value.
		BigInteger[] factors = new BigInteger[biggestMinesNumber-smallestMinesNumber+1];
		for(int minesNumber = smallestMinesNumber; minesNumber<=biggestMinesNumber; ++minesNumber)
		{
			if(minesNumber>minesLeft)
			{
				factors[minesNumber-smallestMinesNumber] = BigInteger.ZERO;
			}
			else
			{
				BigInteger factor = BigInteger.ONE;
				for(int k = smallestMinesNumber; k<minesNumber; ++k)
				{
					factor = factor.multiply(BigInteger.valueOf(minesLeft-k));
				}
				for(int k = minesNumber+1; k<=biggestMinesNumber; ++k)
				{
					factor = factor.multiply(BigInteger.valueOf(notCandidateNumber - minesLeft + k));
				}
				factors[minesNumber-smallestMinesNumber] = factor;
			}
		}
		// Deduce the probabilities for each candidate tile to be a mine
		MathContext mc = new MathContext(10);
		for(int i = 0; i<Game.horizontalSize; ++i)
		{
			for(int j = 0; j<Game.verticalSize; ++j)
			{
				if(candidateTiles[i][j])
				{
					// Numerator of the division, which is the sum of the possibilities for each number of mines, multiplied by the corresponding factor
					BigInteger numerator = BigInteger.ZERO;
					// Denominator of the division, which is the sum of the total possibilities for each number of mines, multiplied by the corresponding factor
					BigInteger denominator = BigInteger.ZERO;
					for(int ind = 0; ind<computedPossibilities.size; ++ind)
					{
						int minesNumber = computedPossibilities.minesNumberList.get(ind);
						BigInteger addedToNumerator = BigInteger.valueOf(computedPossibilities.minePossibilitiesList.get(ind)[i][j]);
						// To check bugs
						if(computedPossibilities.minePossibilitiesList.get(ind)[i][j]<0)
							System.out.println("VALUE BIGGER THAN MAX VALUE!!");
						BigInteger addedToDenominator = BigInteger.valueOf(computedPossibilities.totalPossibilitiesList.get(ind));
						if(computedPossibilities.totalPossibilitiesList.get(ind) < 0)
							System.out.println("VALUE BIGGER THAN MAX VALUE!");
						numerator = numerator.add(addedToNumerator.multiply(factors[minesNumber-smallestMinesNumber]));
						denominator = denominator.add(addedToDenominator.multiply(factors[minesNumber-smallestMinesNumber]));
					}
					BigDecimal decNumerator = new BigDecimal(numerator);
					BigDecimal decDenominator = new BigDecimal(denominator);
					// Deduce the probability
					BigDecimal prob = decNumerator.divide(decDenominator,mc);
					probabilities[i][j] = prob.doubleValue();
					// To check bugs
					if(probabilities[i][j]<0)
					{
						System.out.println("Negative probability of " + probabilities[i][j] + " with numerator = " + decNumerator + " and denominator = " + decDenominator);
						System.out.print("Mines between " + smallestMinesNumber + " and " + biggestMinesNumber + " factors:");
						for(int minesNumber = smallestMinesNumber; minesNumber<=biggestMinesNumber; ++minesNumber)
						{
							System.out.print(factors[minesNumber-smallestMinesNumber] + " ");
						}
						System.out.println();
					}
				}
			}
		}
//		double[] factors = new double[biggestMinesNumber-smallestMinesNumber+1];
//		factors[0] = 1;
//		for(int minesNumber = smallestMinesNumber; minesNumber<=biggestMinesNumber; ++minesNumber)
//		{
//			if(minesLeft-minesNumber>notCandidateNumber)
//			{
//				factors[0] = 0;
//			}
//			else if(minesLeft-minesNumber == notCandidateNumber)
//			{
//				factors[0] = 1;
//			}
//			else if(minesNumber >= smallestMinesNumber+1)
//			{
//				double factor = (double) (minesLeft-minesNumber+1)/(notCandidateNumber-minesLeft+minesNumber);
//				factors[minesNumber-smallestMinesNumber] = factors[minesNumber-smallestMinesNumber-1]*factor;
//			}
//		}
//		double denominator = 0;
//		for(int ind = 0; ind<computedPossibilities.size; ++ind)
//		{
//			int minesNumber = computedPossibilities.minesNumberList.get(ind);
//			denominator += factors[minesNumber-smallestMinesNumber]*computedPossibilities.totalPossibilitiesList.get(ind);
//		}
//		for(int i = 0; i<Game.horizontalSize; ++i)
//		{
//			for(int j = 0; j<Game.verticalSize; ++j)
//			{
//				if(candidateTiles[i][j])
//				{
//					double numerator = 0;
//					for(int ind = 0; ind<computedPossibilities.size; ++ind)
//					{
//						int minesNumber = computedPossibilities.minesNumberList.get(ind);
//						if(computedPossibilities.minePossibilitiesList.get(ind)[i][j]<0)
//							System.out.println("VALUE BIGGER THAN MAX VALUE!!");
//						if(computedPossibilities.totalPossibilitiesList.get(ind) < 0)
//							System.out.println("VALUE BIGGER THAN MAX VALUE!");
//						numerator += factors[minesNumber-smallestMinesNumber]*computedPossibilities.minePossibilitiesList.get(ind)[i][j];
//					}
//					probabilities[i][j] = numerator/denominator;
//					if(probabilities[i][j]<0)
//					{
//						System.out.println("NEGATIVE PROBABILITY");
//					}
//				}
//			}
//		}
		
		return probabilities;
	}

	// [/!\ FIRST VERSION OF THE FUNCTION, HAS BEEN REPLACED WITH THE GuessNextMineAccurate FUNCTION! /!\]
	// Chooses the tile to click, based on the computations of the ComputeMinePossibilities function
	// Also returns the corresponding computed probability, for debugging purposes
	// Note: The chosen tile was supposed to be determined based on two criteria: The number of tiles you uncover when you click it, and the probability the tile has to be a mine.
	// The weight of the two criteria is defined by the displayedInfluence argument. However, it turned out that the solution that gave the biggest victory rate was to decide only on the probability that the tile has to be a mine.
	// As in the previous function, guessingTileCount is used to follow what the function does step by step.
	public double[] GuessNextMine(int guessingTileCount, double displayedInfluence) {
		
		// If there is no group, we recall the SearchEmptyTile function. This can happen in very rare cases, when an unknown tile is surrounded by mines.
		if(this.groupsNumber == 0)
		{
			int[] chosenPos =  SearchEmptyTile();
			// Number of mines that are unknown and not candidate
		    int notCandidateNumber = 0;
		    for(int i = 0; i<Game.horizontalSize; ++i)
		    {
		    	for(int j = 0; j<Game.verticalSize; ++j)
		    	{
		    		if(Game.Unkown(i,j) && !candidateTiles[i][j])
		    		{
		    			notCandidateNumber++;
		    		}
		    	}
		    }
			double[] nextMine = {chosenPos[0],chosenPos[1],(double) Game.minesLeft/notCandidateNumber};
			return nextMine;
		}
		else
		{
			ComputeDiscoveredTiles();
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
			
//			FileWriter output = null;		
//			try {
//				
//				File f = new File(new File("").getAbsolutePath() + "\\Solver_Debug", Integer.toString(guessingTileCount) + ".txt");
//				output = new FileWriter(f);
//				writer = new BufferedWriter(output);
//			}
//			catch(Exception e) {
//				e.printStackTrace();
//			}
			
//			try {
//				writer.write("Studied game:");
//				writer.newLine();
//				WriteArrayNice(writer, gameState);
//				writer.newLine();
//				writer.newLine();
//				writer.newLine();
//			} catch (IOException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
			
			// For each tile, we calculate the number of possibilities where this tile has a mine, and the total number of possibilities. We use previous function to do that.
			totalPossibilities = ComputeMinePossibilities(emptyTiles, minedTiles, 0, false, minePossibilities, guessingTileCount);
			
//			try {
//				writer.close();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			
			// We deduce the probability that each mine has a tile.
			double[][] mineProbabilities = new double[Game.horizontalSize][Game.verticalSize];
			for(int i = 0; i<Game.horizontalSize; ++i)
			{
				for(int j = 0; j<Game.verticalSize; ++j)
				{
					mineProbabilities[i][j] = (double) minePossibilities[i][j]/totalPossibilities;
				}
			}
			
			double max = -2;
			int argmaxSize = 0;
			int iChosen = -1;
			int jChosen = -1;
			
			// We compute the score for each tile, based on how many tiles we uncover if we click this tile, and the probability of it having a mine.
			// The parameter displayedInfluence represents the balance between the two quantities
			// We then choose the tile with the biggest score
			double[][] scoreArray = new double[Game.horizontalSize][Game.verticalSize];
			for(int i = 0; i<Game.horizontalSize; ++i)
			{
				for(int j = 0; j<Game.verticalSize; ++j)
				{
					if(candidateTiles[i][j])
					{
						scoreArray[i][j] = displayedInfluence*discoveredTiles[i][j] - mineProbabilities[i][j];
						if(scoreArray[i][j] > max)
						{
							max = scoreArray[i][j];
							argmaxSize = 1;
							iChosen = i;
							jChosen = j;
						}
						else if(scoreArray[i][j] == max)
						{
							argmaxSize++;
						}
					}
				}
			}
			
			// We choose a maximum score tile randomly, if there are several of them
			Random random = new Random();
			
			int chosenInd = random.nextInt(argmaxSize);
			int ind = 0;
			int[] chosenPos = new int[2];
			
			if(argmaxSize == 1)
			{
				chosenPos[0] = iChosen;
				chosenPos[1] = jChosen;
			}
			else
			{
				for(int i = 0; i<Game.horizontalSize; ++i)
				{
					for(int j = 0; j<Game.verticalSize; ++j)
					{
						if(candidateTiles[i][j] && scoreArray[i][j] == max)
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
			}
			
			double[] nextMine = {chosenPos[0],chosenPos[1],mineProbabilities[chosenPos[0]][chosenPos[1]]};
			return nextMine;
		}
	}
	
	// [IMPROVED VERSION OF THE GuessNextMine FUNCTION!]
	// Chooses the tile to click, based on the computations of the ComputeMinePossibilitiesAccurate function
	// Also returns the corresponding computed probability, for debugging purposes
	// Note: The chosen tile was supposed to be determined based on two criteria: The number of tiles you uncover when you click it, and the probability the tile has to be a mine.
	// The weight of the two criteria is defined by the displayedInfluence argument. However, it turned out that the solution that gave the biggest victory rate was to decide only on the probability that the tile has to be a mine.
	// As in the previous function, guessingTileCount is used to follow what the function does step by step.
	public double[] GuessNextMineAccurate(int guessingTileCount, double displayedInfluence) {
		
		// If there is no group, we recall the SearchEmptyTile function. This can happen in very rare cases, when an unknown tile is surrounded by mines.
		if(this.groupsNumber == 0)
		{
			int[] chosenPos =  SearchEmptyTile();
			// Number of mines that are unknown and not candidate
		    int notCandidateNumber = 0;
		    for(int i = 0; i<Game.horizontalSize; ++i)
		    {
		    	for(int j = 0; j<Game.verticalSize; ++j)
		    	{
		    		if(Game.Unkown(i,j) && !candidateTiles[i][j])
		    		{
		    			notCandidateNumber++;
		    		}
		    	}
		    }
			double[] nextMine = {chosenPos[0],chosenPos[1],(double) Game.minesLeft/notCandidateNumber};
			return nextMine;
		}
		else
		{
			ComputeDiscoveredTiles();
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
			
//			FileWriter output = null;		
//			try {
//				
//				File f = new File(new File("").getAbsolutePath() + "\\Solver_Debug", Integer.toString(guessingTileCount) + ".txt");
//				output = new FileWriter(f);
//				writer = new BufferedWriter(output);
//			}
//			catch(Exception e) {
//				e.printStackTrace();
//			}
			
//			try {
//				writer.write("Studied game:");
//				writer.newLine();
//				WriteArrayNice(writer, gameState);
//				writer.newLine();
//				writer.newLine();
//				writer.newLine();
//			} catch (IOException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
			
			// We computed the number of all the mine disposition, separating them depending on the number of mines they have.
			MinesweeperPossibilities accuratePossibilities = ComputeMinePossibilitiesAccurate(emptyTiles, minedTiles, 0, false, guessingTileCount);
			// Number of mines that are unknown and not candidate
			int notCandidateNumber = 0;
		    for(int i = 0; i<Game.horizontalSize; ++i)
		    {
		    	for(int j = 0; j<Game.verticalSize; ++j)
		    	{
		    		if(Game.Unkown(i,j) && !candidateTiles[i][j])
		    		{
		    			notCandidateNumber++;
		    		}
		    	}
		    }
		    // We deduce the probability
		    double[][] mineProbabilities = DeduceProbabilitiesAccurate(accuratePossibilities,notCandidateNumber,Game.minesLeft);
			
//			try {
//				writer.close();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			
			double max = -2;
			int argmaxSize = 0;
			int iChosen = -1;
			int jChosen = -1;
			
			// We compute the score for each tile, based on how many tiles we uncover if we click this tile, and the probability of it having a mine.
			// The parameter displayedInfluence represents the balance between the two quantities
			// We then choose the tile with the biggest score
			double[][] scoreArray = new double[Game.horizontalSize][Game.verticalSize];
			for(int i = 0; i<Game.horizontalSize; ++i)
			{
				for(int j = 0; j<Game.verticalSize; ++j)
				{
					if(candidateTiles[i][j])
					{
						scoreArray[i][j] = displayedInfluence*discoveredTiles[i][j] - mineProbabilities[i][j];
						if(scoreArray[i][j] > max)
						{
							max = scoreArray[i][j];
							argmaxSize = 1;
							iChosen = i;
							jChosen = j;
						}
						else if(scoreArray[i][j] == max)
						{
							argmaxSize++;
						}
					}
				}
			}
			
			// We choose a maximum score tile randomly, if there are several of them
			Random random = new Random();
			
			int chosenInd = random.nextInt(argmaxSize);
			int ind = 0;
			int[] chosenPos = new int[2];
			
			if(argmaxSize == 1)
			{
				chosenPos[0] = iChosen;
				chosenPos[1] = jChosen;
			}
			else
			{
				for(int i = 0; i<Game.horizontalSize; ++i)
				{
					for(int j = 0; j<Game.verticalSize; ++j)
					{
						if(candidateTiles[i][j] && scoreArray[i][j] == max)
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
			}
			
			double[] nextMine = {chosenPos[0],chosenPos[1],mineProbabilities[chosenPos[0]][chosenPos[1]]};
			return nextMine;
		}
	}
	
	
	
	
	
	// ----------------------------------Debug-functions-----------------------------------
	
	
	
	// First test function
	public void Test1() {
		
		// Computes and displays the probabilities with the first method
		
        //Start time
        long begin = System.nanoTime();
        
        ComputeActions();
		
		ComputeDiscoveredTiles();
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
		totalPossibilities = ComputeMinePossibilities(emptyTiles, minedTiles, 0, false, minePossibilities, 0);
		
	    //End time
	    long end = System.nanoTime();          
	    long time = end-begin;
	    System.out.println();
		
	    System.out.println("Total number of possibilities = " + totalPossibilities);
	    System.out.println("Possibilities per tile:");
		DisplayArray(minePossibilities);
	    System.out.println("Elapsed Time for calculating mine possibilities (ms): " + (double) time/1000000);
		
	}
	
	// Second test function
	public void Test2() {
		
		// Computes and displays the probabilities with the Accurate methods
		
		//Start time
		long begin = System.nanoTime();
		
        ComputeActions();
		
		ComputeDiscoveredTiles();
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
		
//		FileWriter output = null;		
//		try {
//			
//			File f = new File(new File("").getAbsolutePath() + "\\Solver_Debug", Integer.toString(101) + ".txt");
//			output = new FileWriter(f);
//			writer = new BufferedWriter(output);
//		}
//		catch(Exception e) {
//			e.printStackTrace();
//		}
		
		MinesweeperPossibilities accuratePossibilities = ComputeMinePossibilitiesAccurate(emptyTiles, minedTiles, 0, false, 101);
		System.out.println("Accurate possibilities:");
		accuratePossibilities.DisplayPossibilities();
		
//		try {
//			writer.close();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
	    //End time
	    long end = System.nanoTime();          
	    long time = end-begin;
	    System.out.println();
	    System.out.println("Elapsed Time for calculating accurate mine possibilities (ms): " + (double) time/1000000);
	    
	    int notCandidateNumber = 0;
	    for(int i = 0; i<Game.horizontalSize; ++i)
	    {
	    	for(int j = 0; j<Game.verticalSize; ++j)
	    	{
	    		if(Game.Unkown(i,j) && !candidateTiles[i][j])
	    		{
	    			notCandidateNumber++;
	    		}
	    	}
	    }
	    double[][] probabilities = DeduceProbabilitiesAccurate(accuratePossibilities,notCandidateNumber,Game.minesLeft);
	    System.out.println("Deduced probabilities:");
	    DisplayArray(probabilities);
	}

	// Displays the game
	public void DisplayGame() {
		
		for(int i = 0; i<Game.horizontalSize; ++i)
		{
			for(int j = 0; j<Game.verticalSize; ++j)
			{
				System.out.print(Game.grid[j][i] + " ");
			}
			System.out.println();
		}
	}
	
	// Displays the tile groups
	public void DisplayTileGroups() {
		
		int ind = 0;
		for(TileGroup group: TileGroups)
		{
			group.DisplayGroup(ind);
			ind++;
		}
	}
	
	// Writes the tile group in a file
	public void WriteTileGroups(String fileName) {
		
		int ind = 0;
		FileWriter output = null;
			
		try {
			
			File f = new File(new File("").getAbsolutePath(), fileName);
			output = new FileWriter(f);
			BufferedWriter writer = new BufferedWriter(output);
			
			for(TileGroup group: TileGroups)
			{
				writer.write("The positions of the group number " + ind + " are ");
				for(int i = 0; i<8; ++i)
				{
					writer.write("(" + group.iArray[i] + ", " + group.jArray[i] + ")");
				}
				writer.write(". It has a number of mines between " + group.minMines + " and " + group.maxMines);
				writer.write(". It has a size of " + group.size);
				writer.newLine();
				ind++;
			}
			writer.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	// Writes the tile group in a file
	public void WriteTileGroups(BufferedWriter writer) {
		
		int ind = 0;
			
		try {
			
			for(TileGroup group: TileGroups)
			{
				if(ind<= 9)
					writer.write("Group number  " + ind + ": Tiles: ");
				else
					writer.write("Group number " + ind + ": Tiles: ");
				for(int i = 0; i<8; ++i)
				{
					if((group.iArray[i] >= 0 && group.iArray[i] <= 9) && (group.jArray[i] >= 0 && group.jArray[i] <= 9))
						writer.write("( " + group.iArray[i] + ",  " + group.jArray[i] + ")");
					if((group.iArray[i] >= 0 && group.iArray[i] <= 9) && !(group.jArray[i] >= 0 && group.jArray[i] <= 9))
						writer.write("( " + group.iArray[i] + ", " + group.jArray[i] + ")");
					if(!(group.iArray[i] >= 0 && group.iArray[i] <= 9) && (group.jArray[i] >= 0 && group.jArray[i] <= 9))
						writer.write("(" + group.iArray[i] + ",  " + group.jArray[i] + ")");
					if(!(group.iArray[i] >= 0 && group.iArray[i] <= 9) && !(group.jArray[i] >= 0 && group.jArray[i] <= 9))
						writer.write("(" + group.iArray[i] + ", " + group.jArray[i] + ")");
				}
				writer.write(". Mines: [" + group.minMines + ", " + group.maxMines + "]");
				writer.write(". Size: " + group.size);
				writer.newLine();
				ind++;
				if(group.minMines > group.maxMines)
					System.out.println("There is an error!");
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	// Displays an array
	public void DisplayArray(boolean[][] array) {
		for(int i = 0; i<Game.verticalSize; ++i)
		{
			for(int j = 0; j<Game.horizontalSize; ++j)
			{
				if(array[j][i])
					System.out.print(1 + " ");
				else
					System.out.print(0 + " ");
			}
			System.out.println();
		}
	}
	
	// Displays an array
	public void DisplayArray(int[][] array) {
		for(int i = 0; i<Game.verticalSize; ++i)
		{
			for(int j = 0; j<Game.horizontalSize; ++j)
			{
				System.out.print(array[j][i] + " ");
			}
			System.out.println();
		}
	}
	
	// Displays an array
	public void DisplayArray(long[][] array) {
		for(int i = 0; i<Game.verticalSize; ++i)
		{
			for(int j = 0; j<Game.horizontalSize; ++j)
			{
				System.out.print(array[j][i] + " ");
			}
			System.out.println();
		}
	}
	
	// Displays an array
	public void DisplayArray(double[][] array) {
		for(int i = 0; i<Game.verticalSize; ++i)
		{
			for(int j = 0; j<Game.horizontalSize; ++j)
			{
				System.out.print(array[j][i] + " ");
			}
			System.out.println();
		}
	}
	
	// Writes an array in a file
	public void WriteArray(BufferedWriter writer, boolean[][] array) {
		try {
			
			for(int i = 0; i<Game.verticalSize; ++i)
			{
				for(int j = 0; j<Game.horizontalSize; ++j)
				{
					if(array[j][i])
						writer.write(1 + " ");
					else
						writer.write(0 + " ");
				}
				writer.newLine();
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	// Writes an array in a file
	public void WriteArray(BufferedWriter writer, int[][] array) {
		try {
			
			for(int i = 0; i<Game.verticalSize; ++i)
			{
				for(int j = 0; j<Game.horizontalSize; ++j)
				{
					writer.write(array[j][i] + " ");
				}
				writer.newLine();
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	// Writes an array in a file
	public void WriteArrayNice(BufferedWriter writer, int[][] array) {
		try {
			
			for(int i = 0; i<Game.verticalSize; ++i)
			{
				for(int j = 0; j<Game.horizontalSize; ++j)
				{
					if(array[j][i] >= 0 && array[j][i] <= 9)
						writer.write(" " + array[j][i] + " ");
					else
						writer.write(array[j][i] + " ");
				}
				writer.newLine();
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	// Writes an array in a file
	public void WriteArray(BufferedWriter writer, long[][] array) {
		try {
			
			for(int i = 0; i<Game.verticalSize; ++i)
			{
				for(int j = 0; j<Game.horizontalSize; ++j)
				{
					writer.write(array[j][i] + " ");
				}
				writer.newLine();
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	// Writes an array in a file
	public void WriteArray(BufferedWriter writer, double[][] array) {
		try {
			
			for(int i = 0; i<Game.verticalSize; ++i)
			{
				for(int j = 0; j<Game.horizontalSize; ++j)
				{
					writer.write(array[j][i] + " ");
				}
				writer.newLine();
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	// Displays the process used to create the groups
	public void DisplayGroupsProcess() {
		
		int ind = 0;
		FileWriter output = null;
			
		try {
			
			File f = new File(new File("").getAbsolutePath(), "Infos.txt");
			output = new FileWriter(f);
			BufferedWriter writer = new BufferedWriter(output);
			
			for(int[] process: groupsProcess)
			{
				writer.write("Groups number " + process[0] + " and " + process[1] + " created the group number " + process[2] + ", using method " + process[3]);
				writer.newLine();
			}
			writer.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	// Displays the safe tiles detected
	public void DisplayDetectedSafe() {
		
		for(int i = 0; i<Game.horizontalSize; ++i)
		{
			for(int j = 0; j<Game.verticalSize; ++j)
			{
				if(detectedSafe[i][j])
				{
					System.out.println("There is no mine at (" + i + ", " + j + ")");
				}
			}
		}
	}
	
	// Displays the mined tiles detected
	public void DisplayDetectedMines() {

		for(int i = 0; i<Game.horizontalSize; ++i)
		{
			for(int j = 0; j<Game.verticalSize; ++j)
			{
				if(detectedMines[i][j])
				{
					System.out.println("There is a mine at (" + i + ", " + j + ")");
				}
			}
		}
	}

}
