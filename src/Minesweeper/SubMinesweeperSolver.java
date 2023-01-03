package Minesweeper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

// This class is a subclass of MinesweeperSolver. 
// Each instance of the class is a "copy" of a MinesweeperSolver, where we assumed one or several tiles to be empty or mined.
// The interest of the class is to make computations faster, using what has already been computed by the original solver, to do new computations in an efficient way.
public class SubMinesweeperSolver extends MinesweeperSolver {
	
	MinesweeperSolver Solver;					// The solver from which the sub-solver is a copy
	boolean error;								// Variable indicating if there's been an error during the calculations, which would mean that the disposition we are trying is impossible
	
	// Constructor, where we assume that tile (ti, tj) is safe
	public SubMinesweeperSolver(MinesweeperSolver Solver, int ti, int tj)
	{
		this.Solver = Solver;
		this.error = false;
		
		// We don't define every attribute of the original solver, just the ones we need
		this.Game = Solver.Game;
		// We copy the tile groups
		this.TileGroups = new ArrayList<TileGroup>();
		for(TileGroup group : Solver.TileGroups) 
		{
			this.TileGroups.add(group.Clone());
		}
		// We copy groupsPerTile
		this.groupsPerTile = new ArrayList[Solver.Game.horizontalSize][Solver.Game.verticalSize];
		for(int i = 0; i<Solver.Game.horizontalSize; ++i)
		{
			for(int j = 0; j<Solver.Game.verticalSize; ++j)
			{
				if(i==ti && j == tj)
					groupsPerTile[i][j] = new ArrayList<>(Solver.groupsPerTile[i][j]);
			}
		}
		
		this.detectedSafe = new boolean[Solver.Game.horizontalSize][Solver.Game.verticalSize];
		for(int i = 0; i<Game.horizontalSize; ++i)
		{
			for(int j = 0; j<Game.verticalSize; ++j)
			{
				detectedSafe[i][j] = false;
			}
		}
		this.detectedMines = new boolean[Solver.Game.horizontalSize][Solver.Game.verticalSize];
		for(int i = 0; i<Game.horizontalSize; ++i)
		{
			for(int j = 0; j<Game.verticalSize; ++j)
			{
				detectedMines[i][j] = false;
			}
		}
		
		this.actions = new int[Solver.Game.horizontalSize][Solver.Game.verticalSize];
	
		// We remove tile (ti, tj) from all the groups
		for(Integer groupInd : this.groupsPerTile[ti][tj])
		{
			TileGroup group = this.TileGroups.get(groupInd);
			group.Remove(ti, tj, false);
			this.TileGroups.set(groupInd, group);
		}
		
		int updatedMines;
		
		// We then update the mines from all the groups.
		// If this leads to an error, then this means that our assumptions lead to a mistake, so we stop our computations
//		int loop = 0;
		do {
			updatedMines = UpdateMines();
			if(updatedMines == -1)
				this.error = true;
//			if(loop == 0)
//				WriteTileGroups("Third_Solver.txt");
//			else if(loop == 1)
//				WriteTileGroups("Third_Solver_One_Loop.txt");
//			loop++;
		} while(updatedMines > 0 && !this.error);
		
	}
	
	// Constructor, where we assume that some tiles are safe and some are mined
	public SubMinesweeperSolver(MinesweeperSolver Solver, boolean[][] safeTiles, boolean[][] minedTiles)
	{
		this.Solver = Solver;
		this.error = false;
		
		// We don't define every attribute of the original solver, just the ones we need
		this.Game = Solver.Game;
		// We copy the tile groups
		this.TileGroups = new ArrayList<TileGroup>();
		for(TileGroup group : Solver.TileGroups) 
		{
			this.TileGroups.add(group.Clone());
		}
		this.groupsPerTile = new ArrayList[Solver.Game.horizontalSize][Solver.Game.verticalSize];
		// We remove the safe and the mined tiles from the groups, and copy groupsPerTile
		for(int i = 0; i<Solver.Game.horizontalSize; ++i)
		{
			for(int j = 0; j<Solver.Game.verticalSize; ++j)
			{
				if(safeTiles[i][j])
				{
					groupsPerTile[i][j] = new ArrayList<>(Solver.groupsPerTile[i][j]);
					for(Integer groupInd : this.groupsPerTile[i][j])
					{
						TileGroup group = this.TileGroups.get(groupInd);
						group.Remove(i, j, false);
						this.TileGroups.set(groupInd, group);
					}
				}
				if(minedTiles[i][j])
				{
					groupsPerTile[i][j] = new ArrayList<>(Solver.groupsPerTile[i][j]);
					for(Integer groupInd : this.groupsPerTile[i][j])
					{
						TileGroup group = this.TileGroups.get(groupInd);
						group.Remove(i, j, true);
						this.TileGroups.set(groupInd, group);
					}
				}
			}
		}
		this.detectedSafe = new boolean[Solver.Game.horizontalSize][Solver.Game.verticalSize];
		for(int i = 0; i<Game.horizontalSize; ++i)
		{
			for(int j = 0; j<Game.verticalSize; ++j)
			{
				detectedSafe[i][j] = false;
			}
		}
		this.detectedMines = new boolean[Solver.Game.horizontalSize][Solver.Game.verticalSize];
		for(int i = 0; i<Game.horizontalSize; ++i)
		{
			for(int j = 0; j<Game.verticalSize; ++j)
			{
				detectedMines[i][j] = false;
			}
		}
		
		this.actions = new int[Solver.Game.horizontalSize][Solver.Game.verticalSize];
		
		// We then update the mines from all the groups.
		// If this leads to an error, then this means that our assumptions lead to a mistake, so we stop our computations
		int updatedMines;
		do {
			updatedMines = UpdateMines();
			if(updatedMines == -1)
				this.error = true;
		} while(updatedMines > 0 && !this.error);
		
	}
		
	// Function for updating mines, after we assume one or several tiles to be safe/mined. 
	// Uses the groupsProcess list we calculated while computing the tile groups of the original solver, to be able to go through every interactions between the tile groups, without needing to recompute them.
	public int UpdateMines() {
		
		int updatedMines = 0;
		int processInd = 0;
		
		while (processInd<Solver.groupsProcess.size())
		{
			int[] process2 = Solver.groupsProcess.get(processInd);
			
			int groupInd1 = process2[0];
			int groupInd2 = process2[1];
			int separatedGroup2 = process2[2];
			
			// If the group were created from nothing, we skip it
			if(groupInd1==-1)
			{
				processInd++;
			}
			// Otherwise, we re-calculate its mines number, using the same formulas as in the MinesweeperSolver class
			// We needed to be able to remember which method was used to create each group in order to apply these formulas
			else
			{
				int[] process1 = Solver.groupsProcess.get(processInd+1);
				int[] process3 = Solver.groupsProcess.get(processInd+2);
				
				int separatedGroup1 = process1[2];
				int separatedGroup3 = process3[2];
				
				int group1Min = TileGroups.get(groupInd1).minMines;
				int group1Max = TileGroups.get(groupInd1).maxMines;
				int group2Min = TileGroups.get(groupInd2).minMines;
				int group2Max = TileGroups.get(groupInd2).maxMines;
				int separatedGroup2Min = TileGroups.get(separatedGroup2).minMines;
				int separatedGroup2Max = TileGroups.get(separatedGroup2).maxMines;
				int separatedGroup2Size = TileGroups.get(separatedGroup2).size;
				int separatedGroup1Size;
				if(separatedGroup1 != -1)
					separatedGroup1Size = TileGroups.get(separatedGroup1).size;
				else
					separatedGroup1Size = 0;
				int separatedGroup3Size;
				if(separatedGroup3 != -1)
					separatedGroup3Size = TileGroups.get(separatedGroup3).size;
				else
					separatedGroup3Size = 0;
				
				int candSeparatedGroup2Min = Math.max(Math.max(group1Min-separatedGroup1Size, group2Min-separatedGroup3Size), 0);
				int candSeparatedGroup2Max = Math.min(Math.min(group1Max, group2Max), separatedGroup2Size);
				if(candSeparatedGroup2Min > separatedGroup2Min)
				{
					TileGroups.get(separatedGroup2).minMines = candSeparatedGroup2Min;
					updatedMines++;
				}
				if(candSeparatedGroup2Max < separatedGroup2Max)
				{
					TileGroups.get(separatedGroup2).maxMines = candSeparatedGroup2Max;
					updatedMines++;
				}
				if(candSeparatedGroup2Min > candSeparatedGroup2Max)
					return -1;
				if(separatedGroup1 != -1)
				{
					int candSeparatedGroup1Min = Math.max(group1Min-separatedGroup2Max,0);
					if(candSeparatedGroup1Min > TileGroups.get(separatedGroup1).minMines)
					{
						TileGroups.get(separatedGroup1).minMines = candSeparatedGroup1Min;
						updatedMines++;
					}
					int candSeparatedGroup1Max = Math.min(group1Max-separatedGroup2Min,separatedGroup1Size);
					if(candSeparatedGroup1Max < TileGroups.get(separatedGroup1).maxMines)
					{
						TileGroups.get(separatedGroup1).maxMines = candSeparatedGroup1Max;
						updatedMines++;
					}
					if(candSeparatedGroup1Min > candSeparatedGroup1Max)
						return -1;
				}
				if(separatedGroup3 != -1)
				{
					int candSeparatedGroup3Min = Math.max(group2Min-separatedGroup2Max,0);
					if(candSeparatedGroup3Min > TileGroups.get(separatedGroup3).minMines)
					{
						TileGroups.get(separatedGroup3).minMines = candSeparatedGroup3Min;
						updatedMines++;
					}
					int candSeparatedGroup3Max = Math.min(group2Max-separatedGroup2Min,separatedGroup3Size);
					if(candSeparatedGroup3Max < TileGroups.get(separatedGroup3).maxMines)
					{
						TileGroups.get(separatedGroup3).maxMines = candSeparatedGroup3Max;
						updatedMines++;
					}
					if(candSeparatedGroup3Min > candSeparatedGroup3Max)
						return -1;
				}
				
				processInd += 3;
			}
		}
		
		return updatedMines;
	}
	
	@ Override
	public void ComputeActions() {
		
		ComputeDetectedSafe();
		ComputeDetectedMines();
		
		actionsNumber = 0;
		
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
}