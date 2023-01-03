package Minesweeper;

import java.io.BufferedWriter;
import java.util.ArrayList;

// A tile group is a group of tiles used by the class "MinesweeperSolver" to compute solutions
public class TileGroup {
	
	int[] iArray = new int[8];						// Array containing the first coordinates of all the tiles
	int[] jArray = new int[8];						// Array containing the second coordinates of all the tiles
	int minMines;								    // The minimum number of mines the group can have
	int maxMines;				 				 	// The maximum number of mines the group can have
	int size;										// The size of the group
	
	// Creates a tile group from a game and the tile at position (ti, tj). The group contains all the adjacent tiles, and the minimum and maximum number of tiles are the tile's value
	TileGroup(MinesweeperGame Game, int ti, int tj) {
		
		minMines = Game.grid[ti][tj];
		maxMines = Game.grid[ti][tj];
		
		for(int i = 0; i<8; ++i)
		{
			iArray[i] = -1;
		}
		for(int j = 0; j<8; ++j)
		{
			jArray[j] = -1;
		}
		int ind = 0;
		for(int ati = Math.max(ti-1,0); ati <= Math.min(ti+1, Game.horizontalSize-1); ++ati)
		{
			for(int atj = Math.max(tj-1,0); atj <= Math.min(tj+1, Game.verticalSize-1); ++atj)
			{
				if((ati != ti || atj != tj) && Game.Unkown(ati,atj))
				{
					iArray[ind] = ati;
					jArray[ind] = atj;
					ind++;
				}
				else if(Game.guessGrid[ati][atj] == 1)
				{
					minMines -= 1;
					maxMines -= 1;
				}
			}
		}
		size = ind;
	}
	
	// Creates a tile group from a game and the tile at position (ti, tj), and assuming there are some tiles that are safe or mined.
	// The group contains all the adjacent tiles, and the minimum and maximum number of tiles are the tile's value.
	TileGroup(MinesweeperGame Game, int ti, int tj, boolean[][] safeTiles, boolean[][] minedTiles) {
		
		minMines = Game.grid[ti][tj];
		maxMines = Game.grid[ti][tj];
		
		for(int i = 0; i<8; ++i)
		{
			iArray[i] = -1;
		}
		for(int j = 0; j<8; ++j)
		{
			jArray[j] = -1;
		}
		int ind = 0;
		for(int ati = Math.max(ti-1,0); ati <= Math.min(ti+1, Game.horizontalSize-1); ++ati)
		{
			for(int atj = Math.max(tj-1,0); atj <= Math.min(tj+1, Game.verticalSize-1); ++atj)
			{
				if(!safeTiles[ati][atj])
				{
					if((ati != ti || atj != tj) && Game.Unkown(ati,atj) && !minedTiles[ati][atj])
					{
						iArray[ind] = ati;
						jArray[ind] = atj;
						ind++;
					}
					else if(Game.guessGrid[ati][atj] == 1 || minedTiles[ati][atj])
					{
						minMines -= 1;
						maxMines -= 1;
					}
				}
			}
		}
		size = ind;
	}
	
	//Creates an empty TileGroup
	TileGroup(){
		
		for(int i = 0; i<8; ++i)
		{
			iArray[i] = -1;
		}
		for(int j = 0; j<8; ++j)
		{
			jArray[j] = -1;
		}
		minMines = 0;
		maxMines = 0;
		size = 0;
	}
	
	// Adds a tile to the group
	public void Add(int i, int j){
		
		iArray[size] = i;
		jArray[size] = j;
		size++;
	}
	
	// Removes a tile to the group (if it is in the group). Also removes a mine if the argument mine is true.
	public void Remove(int i, int j, boolean mine) {
		int ind = 0;
		while(ind<8 && (iArray[ind] != i || jArray[ind] != j))
			ind++;
		for(int secInd = ind; secInd<7; ++secInd)
		{
			iArray[secInd] = iArray[secInd+1];
			jArray[secInd] = jArray[secInd+1];
		}
		iArray[7] = -1;
		jArray[7] = -1;
		size--;
		if(mine)
		{
			minMines--;
			maxMines--;
		}
		minMines = Math.max(Math.min(minMines, size),0);
//		minMines = Math.min(minMines, size);
		maxMines = Math.min(maxMines, size);
	}
	
	// True iff the group contains tile (i, j)
	public boolean Contains(int i, int j)
	{
		// true iff tile of index ind1 is in candGroup
		boolean answer = false;
		for(int ind = 0; ind<size; ++ind)
		{
			if(i == iArray[ind] && j == jArray[ind])
			{
				answer = true;
			}
		}
		
		return answer;
	}
	
	// Copied from https://www.baeldung.com/java-combinations-algorithm
	private void helper(ArrayList<int[]> combinations, int data[], int start, int end, int index) {
	    if (index == data.length) {
	        int[] combination = data.clone();
	        combinations.add(combination);
	    } else if (start <= end) {
	        data[index] = start;
	        helper(combinations, data, start + 1, end, index + 1);
	        helper(combinations, data, start + 1, end, index);
	    }
	}
	
	// Generates the combinations of r numbers between 0 and n-1
	public ArrayList<int[]> generate(int n, int r) {
	    ArrayList<int[]> combinations = new ArrayList<>();
	    helper(combinations, new int[r], 0, n-1, 0);
	    return combinations;
	}
	
	// Enumerates all the dispositions the group can have (ex: first tile has mine, second doesn't, third has, etc...)
	public ArrayList<boolean[]> EnumeratePossibilities(){
		
		ArrayList<boolean[]> ans = new ArrayList<>();

		for(int mines = minMines; mines<=maxMines; ++mines)
		{
			ArrayList<int[]> combinations = generate(size, mines);
			for(int[] comb : combinations)
			{
				boolean[] possibility = new boolean[size];
				for(int ind = 0; ind<size; ++ind)
					possibility[ind] = false;
				for(int combInd = 0; combInd<mines; ++combInd)
					possibility[comb[combInd]] = true;
				ans.add(possibility);
			}
		}
		
		return ans;
		
		
	}
	
	// Returns a clone of the group
	public TileGroup Clone() {
		
		TileGroup CloneGroup = new TileGroup();
		CloneGroup.iArray = iArray.clone();
		CloneGroup.jArray = jArray.clone();
		CloneGroup.minMines = minMines;
		CloneGroup.maxMines = maxMines;
		CloneGroup.size = size;
		
		return CloneGroup;
	}
	
	// Displays the group
	public void DisplayGroup(int ind) {
		
		System.out.print("The positions of the group number " + ind + " are ");
		for(int i = 0; i<8; ++i)
		{
			System.out.print("(" + iArray[i] + ", " + jArray[i] + ")");
		}
		System.out.print(". It has a number of mines between " + minMines + " and " + maxMines);
		System.out.print(". It has a size of " + size);
		System.out.println();
	}

}
