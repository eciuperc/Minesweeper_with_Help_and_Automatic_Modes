package Minesweeper;

import java.util.ArrayList;

// Class used by the ComputeMinePossibilitiesAccurate function of the MinesweeperSolver class.
// This function is used when the solver doesn't detect a tile he is 100% sure of. It computes the probability of each tile to be a mine, and chooses the one that has the smallest one.
// To do so, he computes every possible disposition of mines. To have a precise computation of the probabilities, we must separate these possibilities depending on the number of mines they contains.
// That's what this class is used for.
public class MinesweeperPossibilities {
	
	MinesweeperGame Game;								// Game we're trying to solve
	int size;											// Size of the three lists below
	ArrayList<Integer> minesNumberList;					// All the possible mines number
	ArrayList<Long> totalPossibilitiesList;				// For each mines number, total number of possibilities
	ArrayList<long[][]> minePossibilitiesList;			// For each mines number, array containing, for each candidate tile (i.e. for each unknown tile that is next to an uncovered tile), the number of possibilities where this tile is a mine

	// Constructor. The arguments are the game, and a boolean, indicating if the instance we will create is empty.
	public MinesweeperPossibilities(MinesweeperGame Game, boolean empty) {
		
		this.Game = Game;
		size = 0;
		minesNumberList = new ArrayList<Integer>();
		totalPossibilitiesList = new ArrayList<Long>();
		minePossibilitiesList = new ArrayList<>();
		
		// If the instance we create is not empty, we add one possibility with no mines.
		if(!empty)
		{
			size++;
			minesNumberList.add(0);
			totalPossibilitiesList.add(1L);
			long[][] newMinePossibilities = new long[Game.horizontalSize][Game.verticalSize];
			for(int i = 0; i<Game.horizontalSize; ++i)
			{
				for(int j = 0; j<Game.verticalSize; ++j)
				{
					newMinePossibilities[i][j] = 0L;
				}
			}
			minePossibilitiesList.add(newMinePossibilities);
		}
	}
	
	// Constructor. The arguments are the game, and a TileGroup. Computes all the possibilities that this group defines.
	// Used for a base case of the ComputeMinePossibilitiesAccurate function.
	public MinesweeperPossibilities(MinesweeperGame Game, TileGroup group) {
		
		this.Game = Game;
		size = 0;
		minesNumberList = new ArrayList<Integer>();
		totalPossibilitiesList = new ArrayList<Long>();
		minePossibilitiesList = new ArrayList<>();
		
		// We compute all the possibilities this group defines.
		// We iterate over the number of mines this group can have. For each mine number, we can explicit a formula for the number of possibilities.
		for(int mine = group.minMines; mine<=group.maxMines; ++mine)
		{
			size++;
			minesNumberList.add(mine);
			// Total number of possibilities
			totalPossibilitiesList.add((long) MinesweeperSolver.pascalTriangle[group.size][mine]);
			// Total number of possibilities where each tile is a mine.
			long[][] newMinePossibilities = new long[Game.horizontalSize][Game.verticalSize];
			for(int i = 0; i<Game.horizontalSize; ++i)
			{
				for(int j = 0; j<Game.verticalSize; ++j)
				{
					newMinePossibilities[i][j] = 0;
				}
			}
			if(mine != 0)
			{
				for(int ind = 0; ind<group.size; ++ind)
				{
					newMinePossibilities[group.iArray[ind]][group.jArray[ind]] = MinesweeperSolver.pascalTriangle[group.size-1][mine-1];
				}
			}
			minePossibilitiesList.add(newMinePossibilities);
			
		}
	}
	
	// We add the total number of possibilities to a list of tiles, representing the tiles that were assumed to be mined.
	public void UpdateMinedTiles(TileList minedTilesList) {
		
		// For each tile assumed to have a mine, we add the total number of possibilities
		for(int ind = 0; ind<size; ++ind)
		{
			// Each tile in minedTilesList increases by one the number of mines in each possibility
			minesNumberList.set(ind, minesNumberList.get(ind)+minedTilesList.size);
			// We add the total number of mines for each mined tile
			for(int tileInd = 0; tileInd<minedTilesList.size; ++tileInd)
			{
				minePossibilitiesList.get(ind)[minedTilesList.iList.get(tileInd)][minedTilesList.jList.get(tileInd)] += totalPossibilitiesList.get(ind);
			}
		}
	}
	
	// Computes the "sum" between two MinesweeperPossibilities. 
	// Used when we enumerate the different mine dispositions a chosen group can have, in ComputeMinePossibilitiesAccurate, and compute the MinesweeperPossibilities for each disposition, to sum all these dispositions
	public void Add(MinesweeperPossibilities SecondPossibilities)
	{
		// We iterate over the "elements" (i.e. the elements that correspond to a mine number) of SecondPossibilities to add each of these to this
		for(int secondInd = 0; secondInd<SecondPossibilities.size; ++secondInd)
		{
			// If this is empty, we simply add this element
			if(size == 0)
			{
				size++;
				minesNumberList.add(SecondPossibilities.minesNumberList.get(secondInd));
				totalPossibilitiesList.add(SecondPossibilities.totalPossibilitiesList.get(secondInd));
				minePossibilitiesList.add(SecondPossibilities.minePossibilitiesList.get(secondInd));
			}
			// If this is not empty, we add the element such that the number of mines are kept in increasing order
			else
			{
				int secondMinesNumber = SecondPossibilities.minesNumberList.get(secondInd);
				if(secondMinesNumber < minesNumberList.get(0))
				{
					size++;
					minesNumberList.add(0,SecondPossibilities.minesNumberList.get(secondInd));
					totalPossibilitiesList.add(0,SecondPossibilities.totalPossibilitiesList.get(secondInd));
					minePossibilitiesList.add(0,SecondPossibilities.minePossibilitiesList.get(secondInd));
				}
				else
				{
					int ind = 0;
					boolean added = false;
					while(!added)
					{
						if(secondMinesNumber >= minesNumberList.get(ind))
						{
							// If the number of mines already exists in this, we add the total possibilities and the possibilities for each tile
							if(secondMinesNumber == minesNumberList.get(ind))
							{
								totalPossibilitiesList.set(ind, totalPossibilitiesList.get(ind)+SecondPossibilities.totalPossibilitiesList.get(secondInd));
								for(int i = 0; i<Game.horizontalSize; ++i)
								{
									for(int j = 0; j<Game.verticalSize; ++j)
									{
										minePossibilitiesList.get(ind)[i][j] += SecondPossibilities.minePossibilitiesList.get(secondInd)[i][j];
									}
								}
								added = true;
							}
							else
							{
								// Otherwise, we insert the element in this.
								if(ind<size-1)
								{
									if(secondMinesNumber < minesNumberList.get(ind+1))
									{
										size++;
										minesNumberList.add(ind+1,SecondPossibilities.minesNumberList.get(secondInd));
										totalPossibilitiesList.add(ind+1,SecondPossibilities.totalPossibilitiesList.get(secondInd));
										minePossibilitiesList.add(ind+1,SecondPossibilities.minePossibilitiesList.get(secondInd));
										added = true;
									}
								}
								else if(ind == size-1)
								{
									size++;
									minesNumberList.add(SecondPossibilities.minesNumberList.get(secondInd));
									totalPossibilitiesList.add(SecondPossibilities.totalPossibilitiesList.get(secondInd));
									minePossibilitiesList.add(SecondPossibilities.minePossibilitiesList.get(secondInd));
									added = true;
								}
							}
						}
						ind++;
					}
				}
			}
		}
	}
	
	// Computes the "product" between two MinesweeperPossibilities. 
	// Used in ComputeMinePossibilitiesAccurate, when we split the game between its different connected components, and compute the possibilities for each connected component, to multiply all of these possibilities.
	public void MultiplyWith(MinesweeperPossibilities SecondPossibilities)
	{
		// MinesweeperPossibilities where the result is stored
		MinesweeperPossibilities multipliedPossibilities = new MinesweeperPossibilities(Game,true);
		
		// We iterate over each pair of "elements" (i.e. the elements that correspond to a mine number) of the two MinesweeperPossibilities that have to be multiplied
		for(int ind = 0; ind<size; ++ind)
		{
			for(int secondInd = 0; secondInd<SecondPossibilities.size; ++secondInd)
			{
				// We add the number of mines of the two elements
				int addedMinesNumber = minesNumberList.get(ind) + SecondPossibilities.minesNumberList.get(secondInd);
				// We compute the total number of possibilities
				long addedTotalPossibilities = totalPossibilitiesList.get(ind)*SecondPossibilities.totalPossibilitiesList.get(secondInd);
				// For each tile, we compute the number of possibilities where it is a mine.
				long[][] addedMinePossibilities = new long[Game.horizontalSize][Game.verticalSize];
				for(int i = 0; i<Game.horizontalSize; ++i)
				{
					for(int j = 0; j<Game.verticalSize; ++j)
					{
						addedMinePossibilities[i][j] = SecondPossibilities.totalPossibilitiesList.get(secondInd)*minePossibilitiesList.get(ind)[i][j] + totalPossibilitiesList.get(ind)*SecondPossibilities.minePossibilitiesList.get(secondInd)[i][j];
					}
				}
				// We add the result, in a similar way as in the Add function.
				if(multipliedPossibilities.size == 0)
				{
					multipliedPossibilities.size++;
					multipliedPossibilities.minesNumberList.add(addedMinesNumber);
					multipliedPossibilities.totalPossibilitiesList.add(addedTotalPossibilities);
					multipliedPossibilities.minePossibilitiesList.add(addedMinePossibilities);
				}
				else
				{
					if(addedMinesNumber < multipliedPossibilities.minesNumberList.get(0))
					{
						multipliedPossibilities.size++;
						multipliedPossibilities.minesNumberList.add(0,addedMinesNumber);
						multipliedPossibilities.totalPossibilitiesList.add(0,addedTotalPossibilities);
						multipliedPossibilities.minePossibilitiesList.add(0,addedMinePossibilities);
					}
					else
					{
						int addInd = 0;
						boolean added = false;
						while(!added)
						{
							if(addedMinesNumber >= multipliedPossibilities.minesNumberList.get(addInd))
							{
								if(addedMinesNumber == multipliedPossibilities.minesNumberList.get(addInd))
								{
									multipliedPossibilities.totalPossibilitiesList.set(addInd, multipliedPossibilities.totalPossibilitiesList.get(addInd)+addedTotalPossibilities);
									for(int i = 0; i<Game.horizontalSize; ++i)
									{
										for(int j = 0; j<Game.verticalSize; ++j)
										{
											multipliedPossibilities.minePossibilitiesList.get(addInd)[i][j] += addedMinePossibilities[i][j];
										}
									}
									added = true;
								}
								else
								{
									if(addInd<multipliedPossibilities.size-1)
									{
										if(addedMinesNumber < multipliedPossibilities.minesNumberList.get(addInd+1))
										{
											multipliedPossibilities.size++;
											multipliedPossibilities.minesNumberList.add(addInd+1,addedMinesNumber);
											multipliedPossibilities.totalPossibilitiesList.add(addInd+1,addedTotalPossibilities);
											multipliedPossibilities.minePossibilitiesList.add(addInd+1,addedMinePossibilities);
											added = true;
										}
									}
									else if(addInd == multipliedPossibilities.size-1)
									{
										multipliedPossibilities.size++;
										multipliedPossibilities.minesNumberList.add(addedMinesNumber);
										multipliedPossibilities.totalPossibilitiesList.add(addedTotalPossibilities);
										multipliedPossibilities.minePossibilitiesList.add(addedMinePossibilities);
										added = true;
									}
								}
							}
							addInd++;
						}
					}
				}
			}
		}
		
		this.size = multipliedPossibilities.size;
		this.minesNumberList = multipliedPossibilities.minesNumberList;
		this.totalPossibilitiesList = multipliedPossibilities.totalPossibilitiesList;
		this.minePossibilitiesList = multipliedPossibilities.minePossibilitiesList;
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
	
	// Displays all the possibilities. For debugging.
	public void DisplayPossibilities() {
		
		System.out.println("Size = " + size);
		for(int ind = 0; ind<size; ++ind)
		{
			System.out.println("With " + minesNumberList.get(ind) + " mines:");
			System.out.println("Total number of possibilities: " + totalPossibilitiesList.get(ind));
			System.out.println("Possibilities per tile:");
			DisplayArray(minePossibilitiesList.get(ind));
		}
		System.out.println();
	}

}
