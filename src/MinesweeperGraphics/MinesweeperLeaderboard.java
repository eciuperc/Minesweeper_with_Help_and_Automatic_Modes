package MinesweeperGraphics;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;

// Class used for handling hiscores, i.e. the best times that have been made for each difficulty. Inspired from https://www.youtube.com/watch?v=-hzqb32w95o&ab_channel=FaTalCubez
public class MinesweeperLeaderboard {

	
	private static MinesweeperLeaderboard lBoard;				// private static instance of the class. This is the only instance of the class that can be used, in order to ensure that only one instance of the class will be called
	private String filePath;									// path to the folder where the time are stored
	private String hiScores;									// path to the file where the time are stored
	
	private int size = 50;										// Number of scores stored for every difficulty
	private ArrayList<String> NamesBeginner;					// List of names for beginner difficulty
	private ArrayList<Double> topTimesBeginner;					// List of times for beginner difficulty
	private ArrayList<String> NamesIntermediate;				// List of names for intermediate difficulty
	private ArrayList<Double> topTimesIntermediate;				// List of times for intermediate difficulty
	private ArrayList<String> NamesExpert;						// List of names for expert difficulty
	private ArrayList<Double> topTimesExpert;					// List of times for expert difficulty
	private ArrayList<String> NamesDemon;						// List of names for demon difficulty
	private ArrayList<Double> topTimesDemon;					// List of times for demon difficulty
	
	// Private constructor
	private MinesweeperLeaderboard() {
		
		filePath = new File("").getAbsolutePath();
		hiScores = "Scores.txt";
		
		NamesBeginner = new ArrayList<String>();
		topTimesBeginner = new ArrayList<Double>();
		NamesIntermediate = new ArrayList<String>();
		topTimesIntermediate = new ArrayList<Double>();
		NamesExpert = new ArrayList<String>();
		topTimesExpert = new ArrayList<Double>();
		NamesDemon = new ArrayList<String>();
		topTimesDemon = new ArrayList<Double>();
	}
	
	// Public function, calling constructor only if no instance of the class has been created
	public static MinesweeperLeaderboard getInstance() {
		
		if(lBoard == null) {
			lBoard = new MinesweeperLeaderboard();
		}
		return lBoard;
	}
	
	// Adds a time to the leaderboard, depending on the difficulty of the game played
	// Returns the ranking of the time added. If the time is not good enough for being added, returns -1.
	public int AddTime(int difficulty, double time) {
		
		// Runs trough the scores to see if the time is good enough
		for(int i = 0; i<size; ++i)
		{
			switch(difficulty) {
			case(1):{
				if(time < topTimesBeginner.get(i))
				{
					topTimesBeginner.add(i, time);
					topTimesBeginner.remove(size);
					return i;
				}
				break;
			}
			case(2):{
				if(time < topTimesIntermediate.get(i))
				{
					topTimesIntermediate.add(i, time);
					topTimesIntermediate.remove(size);
					return i;
				}
				break;
			}
			case(3):{
				if(time < topTimesExpert.get(i))
				{
					topTimesExpert.add(i, time);
					topTimesExpert.remove(size);
					return i;
				}
				break;
			}
			case(4):{
				if(time < topTimesDemon.get(i))
				{
					topTimesDemon.add(i, time);
					topTimesDemon.remove(size);
					return i;
				}
				break;
			}
			default: break;
			}
		}
		return -1;
	}
	
	// Adds a name at a given ranking, given by the previous method
	public void AddName(int difficulty, int ranking, String name) {
		
		switch(difficulty) {
		case(1): NamesBeginner.add(ranking, name); NamesBeginner.remove(size); break;
		case(2): NamesIntermediate.add(ranking, name); NamesIntermediate.remove(size); break;
		case(3): NamesExpert.add(ranking, name); NamesExpert.remove(size); break;
		case(4): NamesDemon.add(ranking, name); NamesDemon.remove(size); break;
		default: break;
		}
	}
	
	// Loads the data from the file storing the hiscores
	public void loadData() {
		
		try{
			File f = new File(filePath, hiScores);
			if(!f.isFile()) 
			{
//				createSaveData();
			}
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
			
			NamesBeginner.clear();
			topTimesBeginner.clear();
			NamesIntermediate.clear();
			topTimesIntermediate.clear();
			NamesExpert.clear();
			topTimesExpert.clear();
			NamesDemon.clear();
			topTimesDemon.clear();
			
			String[] beginnerN = reader.readLine().split("-");
			String[] beginnerT = reader.readLine().split("-");
			String[] intermediateN = reader.readLine().split("-");
			String[] intermediateT = reader.readLine().split("-");
			String[] expertN = reader.readLine().split("-");
			String[] expertT = reader.readLine().split("-");
			String[] demonN = reader.readLine().split("-");
			String[] demonT = reader.readLine().split("-");
			
			for(int i = 0; i < size; ++i)
			{
				NamesBeginner.add(beginnerN[i]);
			}
			for(int i = 0; i < size; ++i)
			{
				topTimesBeginner.add(Double.parseDouble(beginnerT[i]));
			}
			for(int i = 0; i < size; ++i)
			{
				NamesIntermediate.add(intermediateN[i]);
			}
			for(int i = 0; i < size; ++i)
			{
				topTimesIntermediate.add(Double.parseDouble(intermediateT[i]));
			}
			for(int i = 0; i < size; ++i)
			{
				NamesExpert.add(expertN[i]);
			}
			for(int i = 0; i < size; ++i)
			{
				topTimesExpert.add(Double.parseDouble(expertT[i]));
			}
			for(int i = 0; i < size; ++i)
			{
				NamesDemon.add(demonN[i]);
			}
			for(int i = 0; i < size; ++i)
			{
				topTimesDemon.add(Double.parseDouble(demonT[i]));
			}
			
			reader.close();
			
		}
		catch(Exception e) {
			
		}
	}
	
	// Saves the data to the file storing the hiscores
	public void SaveData() {
		
		FileWriter output = null;
		
		try {
			
			File f = new File(filePath, hiScores);
			output = new FileWriter(f);
			BufferedWriter writer = new BufferedWriter(output);
			
			for(int i = 0; i<size - 1; ++i)
			{
				writer.write(NamesBeginner.get(i) + "-");
			}
			writer.write(NamesBeginner.get(size-1) + "");
			writer.newLine();
			for(int i = 0; i<size - 1; ++i)
			{
				writer.write(topTimesBeginner.get(i) + "-");
			}
			writer.write(topTimesBeginner.get(size - 1) + "");
			writer.newLine();
			for(int i = 0; i<size - 1; ++i)
			{
				writer.write(NamesIntermediate.get(i) + "-");
			}
			writer.write(NamesIntermediate.get(size-1) + "");
			writer.newLine();
			for(int i = 0; i<size - 1; ++i)
			{
				writer.write(topTimesIntermediate.get(i) + "-");
			}
			writer.write(topTimesIntermediate.get(size - 1) + "");
			writer.newLine();
			for(int i = 0; i<size - 1; ++i)
			{
				writer.write(NamesExpert.get(i) + "-");
			}
			writer.write(NamesExpert.get(size-1) + "");
			writer.newLine();
			for(int i = 0; i<size - 1; ++i)
			{
				writer.write(topTimesExpert.get(i) + "-");
			}
			writer.write(topTimesExpert.get(size - 1) + "");
			writer.newLine();
			for(int i = 0; i<size - 1; ++i)
			{
				writer.write(NamesDemon.get(i) + "-");
			}
			writer.write(NamesDemon.get(size-1) + "");
			writer.newLine();
			for(int i = 0; i<size - 1; ++i)
			{
				writer.write(topTimesDemon.get(i) + "-");
			}
			writer.write(topTimesDemon.get(size - 1) + "");
			writer.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	// Resets the data of the file storing the hiscores
	public void createSaveData() {
		
		FileWriter output = null;
		
		try {
			
			File f = new File(filePath, hiScores);
			output = new FileWriter(f);
			BufferedWriter writer = new BufferedWriter(output);
			
			for(int i = 0; i<size - 1; ++i)
			{
				writer.write(" -");
			}
			writer.write(" ");
			writer.newLine();
			for(int i = 0; i<size - 1; ++i)
			{
				writer.write(Double.MAX_VALUE + "-");
			}
			writer.write(Double.MAX_VALUE + "");
			writer.newLine();
			for(int i = 0; i<size - 1; ++i)
			{
				writer.write(" -");
			}
			writer.write(" ");
			writer.newLine();
			for(int i = 0; i<size - 1; ++i)
			{
				writer.write(Double.MAX_VALUE + "-");
			}
			writer.write(Double.MAX_VALUE + "");
			writer.newLine();
			for(int i = 0; i<size - 1; ++i)
			{
				writer.write(" -");
			}
			writer.write(" ");
			writer.newLine();
			for(int i = 0; i<size - 1; ++i)
			{
				writer.write(Double.MAX_VALUE + "-");
			}
			writer.write(Double.MAX_VALUE + "");
			writer.newLine();
			for(int i = 0; i<size - 1; ++i)
			{
				writer.write(" -");
			}
			writer.write(" ");
			writer.newLine();
			for(int i = 0; i<size - 1; ++i)
			{
				writer.write(Double.MAX_VALUE + "-");
			}
			writer.write(Double.MAX_VALUE + "");
			writer.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	// Returns the number of times stored for every difficulty
	public int getSize() {
		
		return size;
	}
	
	// Returns the name corresponding to a given ranking, for a given difficulty
	public String getIndividualName(int difficulty, int i) {
		
		switch(difficulty)
		{
			case(1): return NamesBeginner.get(i);
			case(2): return NamesIntermediate.get(i);
			case(3): return NamesExpert.get(i);
			case(4): return NamesDemon.get(i);
			default: return " ";
		}
	}
	
	// Returns the time corresponding to a given ranking, for a given difficulty
	public double getIndividualTime(int difficulty, int i) {
		
		switch(difficulty)
		{
			case(1): return topTimesBeginner.get(i);
			case(2): return topTimesIntermediate.get(i);
			case(3): return topTimesExpert.get(i);
			case(4): return topTimesDemon.get(i);
			default: return Double.MAX_VALUE;
		}
	}
	
	// Displays the data. For debugging.
	public void DisplayData() {
		
		for(int i = 0; i<size - 1; ++i)
		{
			System.out.print(NamesBeginner.get(i) + "-");
		}
		System.out.println(NamesBeginner.get(size - 1));
		for(int i = 0; i<size - 1; ++i)
		{
			System.out.print(topTimesBeginner.get(i) + "-");
		}
		System.out.println(topTimesBeginner.get(size - 1));
		for(int i = 0; i<size - 1; ++i)
		{
			System.out.print(NamesIntermediate.get(i) + "-");
		}
		System.out.println(NamesIntermediate.get(size - 1));
		for(int i = 0; i<size - 1; ++i)
		{
			System.out.print(topTimesIntermediate.get(i) + "-");
		}
		System.out.println(topTimesIntermediate.get(size - 1));
		for(int i = 0; i<size - 1; ++i)
		{
			System.out.print(NamesExpert.get(i) + "-");
		}
		System.out.println(NamesExpert.get(size - 1));
		for(int i = 0; i<size - 1; ++i)
		{
			System.out.print(topTimesExpert.get(i) + "-");
		}
		System.out.println(topTimesExpert.get(size - 1));
		for(int i = 0; i<size - 1; ++i)
		{
			System.out.print(NamesDemon.get(i) + "-");
		}
		System.out.println(NamesDemon.get(size - 1));
		for(int i = 0; i<size - 1; ++i)
		{
			System.out.print(topTimesDemon.get(i) + "-");
		}
		System.out.println(topTimesDemon.get(size - 1));
	}
}