import MinesweeperGraphics.*;

public class Main_Minesweeper {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		// Beginner: 9*9, 10 mines
		// Intermediate: 16*16, 40 mines
		// Expert: 30*16, 99 mines
		// Demon (mine): 75*35, 600 mines
		// Default settings of the game
		int difficulty = 2;
		// For custom mode
		boolean customLevel = false;
		int customHorizontalSize = 16;
		int customVerticalSize = 16;
		int customMines = 20;
		// Game mode
		int helpParameter = 0;
		
//		// To reset leaderboards
//		MinesweeperLeaderboard Leaderboard = MinesweeperLeaderboard.getInstance();
//		Leaderboard.createSaveData();
		// Display the frame
		MinesweeperFrame frame = new MinesweeperFrame(difficulty, customLevel, customHorizontalSize, customVerticalSize, customMines, helpParameter);
	}

}
