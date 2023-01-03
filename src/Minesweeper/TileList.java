package Minesweeper;

import java.util.ArrayList;

// Class representing a list of tiles. Used to speed-up computations
public class TileList {

	int size;							// Size of the list
	ArrayList<Integer> iList;			// First coordinates of the tiles
	ArrayList<Integer> jList;			// Second coordinates of the tiles
	
	// Constructor. Defines an empty list
	TileList() {
		
		size = 0;
		iList = new ArrayList<Integer>();
		jList = new ArrayList<Integer>();
	}
	
	// Add the tile (i, j) to the list
	void Add(int i, int j)
	{
		size++;
		iList.add(i);
		jList.add(j);
	}
}
