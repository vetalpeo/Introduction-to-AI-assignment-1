package searchpractice;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import robocode.control.BattleSpecification;
import robocode.control.BattlefieldSpecification;
import robocode.control.RobocodeEngine;
import robocode.control.RobotSetup;
import robocode.control.RobotSpecification;

public class RouteFinder {

	private static final String ROBOCODEPATH = "/home/frizio/robocode";
	
	public static List<Integer> generateAllPositions(int n){

		List<Integer> list = new ArrayList<Integer>(n);
	    for (int i = 0; i < n; i++) {
	        list.add(i);
	    }
	    return list;
	}
	
	
	public static void main(String[] args) {
		
		/******************************** Inizialize Robocode *************s************************/
		// Create the RobocodeEngine
		RobocodeEngine engine = new RobocodeEngine(new java.io.File(ROBOCODEPATH));
		// Show the Robocode battle view
		engine.setVisible(true);
		
		/******************************* Create the battlefield ************************************/		
		// Create the battlefield (a grid of 800 x 600)
		int NumPixelRows= 800;
		int NumPixelCols= 600;
		
		int VerticalOffset = NumPixelCols % 64;
		
		BattlefieldSpecification battlefield = new BattlefieldSpecification(NumPixelRows, NumPixelCols);
		
		
		/******************************* Setup battle parameters ***********************************/
		// Parameters
		int numberOfRounds = 5;
		long inactivityTime = 10000000;
		double gunCoolingRate = 1.0;
		int sentryBorderSize = 50;
		boolean hideEnemyNames = false;
		
		// Size of tile and half tile
		int TileSize = 64;
		int HalfTile = TileSize/2;
				
		// Calculate the number of tiles on battlefield
		int NumTileRows = (int) (NumPixelRows / TileSize); //if number of rows not an integer, truncate it
		int NumTileCols = (int) (NumPixelCols / TileSize); //if number of columns not an integer, truncate it
				
		// Calculate the number of obstacles
		double SittingDuckPercentage = 0.30;
		int NumObstacles = (int) (NumTileRows * NumTileCols * SittingDuckPercentage) ; 
		System.out.println("Number of obstacles: " + NumObstacles); 

		
		/******************************* Create obstacles *********************************/
		// Create obstacles and place them at random, so that no pair of obstacles are at the same position
		
		RobotSpecification[] modelRobots = engine.getLocalRepository("sample.SittingDuck");
		//engine.getLocalRepository("sample.SittingDuck.searchpractice.RouteBot*");
		
		RobotSpecification[] existingRobots = new RobotSpecification[NumObstacles+1];
		
		RobotSetup[] robotSetups = new RobotSetup[NumObstacles+1];
		
		// Create random number generator and set the initial seed (to repeat the same experiment)
		Random randomGenerator = new Random();
	    randomGenerator.setSeed(0);
		
	    // Initial list of all possible initial position in the matrix
	    // To semplicity I use a list of linear index, and after select, convert them to a (row,col) indexes.
		List<Integer> remainingPossiblePositions = generateAllPositions(NumTileRows*NumTileCols);
		
		for ( int NdxObstacle=0; NdxObstacle < NumObstacles; NdxObstacle++ ) {
			
			// Select a random tile between the remaining position:
			
			// Generate a random linear index between the possible position
			int idx = randomGenerator.nextInt(remainingPossiblePositions.size());
			int position = remainingPossiblePositions.remove(idx);
			
			// Convert linear index to (row, column) indexes.
			int InitialTileRow = position % NumTileRows;  
			int InitialTileCol = position / NumTileRows;
			
			// Convert its coordinates to pixels
			double InitialObstacleRow = InitialTileRow * TileSize + HalfTile ;
			double InitialObstacleCol = InitialTileCol * TileSize + HalfTile + VerticalOffset;
			
			existingRobots[NdxObstacle] = modelRobots[0];
			robotSetups[NdxObstacle] = new RobotSetup(InitialObstacleRow, InitialObstacleCol, 0.0);
		}
		
		/********************************** Create the agent ************************************/
		// Create the agent and place it in a random position without obstacle

		// ????????
		existingRobots[NumObstacles] = modelRobots[0];  
		// existingRobots[NumObstacles] = modelRobots[NumObstacles + 1]; // it didn't work,
		// I guess it should be modelRobots[1], not modelRobots[NumObstacles + 1]

		// As previous
		int idx = randomGenerator.nextInt(remainingPossiblePositions.size());
		int position = remainingPossiblePositions.remove(idx);
		
		int InitialTileRow = position % NumTileRows;   
		int InitialTileCol = position / NumTileRows;
		System.out.println("Agent Initial position: " + InitialTileRow + " " + InitialTileRow);
		
		double InitialAgentRow = InitialTileRow * TileSize + HalfTile; 
		double InitialAgentCol = InitialTileCol * TileSize + HalfTile + VerticalOffset; 
		
		robotSetups[NumObstacles] = new RobotSetup(InitialAgentRow, InitialAgentCol, 0.0);

		/************************* Create and run the battle *************************************/
		
		// Create the battle
		BattleSpecification battleSpec = new BattleSpecification(battlefield,
																 numberOfRounds,
																 inactivityTime,
																 gunCoolingRate,
																 sentryBorderSize,
																 hideEnemyNames,
																 existingRobots,
																 robotSetups);
		
		// Run our specified battle and let it run till it is over
		engine.runBattle(battleSpec, true); 	// waits till the battle finishes
		
		/******************************** Finalize Robocode *************s************************/
		
		// Cleanup our RobocodeEngine
		engine.close();

		// Make sure that the Java VM is shut down properly
		System.exit(0);
		
	}
	
}
