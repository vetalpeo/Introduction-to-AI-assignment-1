package mainprogram;

import java.util.Arrays;

import robocode.control.BattleSpecification;
import robocode.control.BattlefieldSpecification;
import robocode.control.RobocodeEngine;
import robocode.control.RobotSetup;
import robocode.control.RobotSpecification;

public class RouteFinder {

	private static final String ROBOCODEPATH = "/robocode";
	
	
	public static void main(String[] args) {
		
		/******************************** Inizialize Robocode *************s************************/
		
		// Create the RobocodeEngine
		RobocodeEngine engine = new RobocodeEngine(new java.io.File(ROBOCODEPATH));
		
		// Show the Robocode battle view
		engine.setVisible(true);
		
		/*******************************************************************************************/
		
		/******************************* Create the battlefield ************************************/
		
		// Create the battlefield (a grid of 800 x 600) ???????????????
		int NumPixelRows= 800; //12.5 * 64;
		int NumPixelCols= 600; //9.375 * 64;

		BattlefieldSpecification battlefield = new BattlefieldSpecification(NumPixelRows, NumPixelCols);
		
		/*******************************************************************************************/
		
		/******************************* Setup battle parameters ***********************************/

		// Parameters
		int numberOfRounds = 5;
		long inactivityTime = 10000000;
		double gunCoolingRate = 1.0;
		int sentryBorderSize = 50;
		boolean hideEnemyNames = false;

		//size of tile and half tile
		int TileSize=64;
		int HalfTile=TileSize/2;
		
		//calculate the number of tiles on battlefield
		int NumTileRows = (int) (NumPixelRows / TileSize); //if number of rows not an integer, truncate it
		int NumTileCols = (int) (NumPixelCols / TileSize); //if number of columns not an integer, truncate it
		
		// calculate the number of obstacles
		double SittingDuckPercentage = .30;
		int NumObstacles = (int) (NumTileRows * NumTileCols * SittingDuckPercentage) ; //if Number of obstacles not an integer, truncate it 
		
		/******************************* Create obstacles *********************************/
		// Create obstacles and place them at random,
		// so that no pair of obstacles are at the same position
		
		
		// ????????????? ask to prof
		System.out.println("Robot creation...");
		RobotSpecification[] modelRobots = engine.getLocalRepository("sample.SittingDuck.searchpractice.RouteBot*");
		System.out.println("Robot created!");
		System.out.println(modelRobots.toString());
		
		
		RobotSpecification[] existingRobots = new RobotSpecification[NumObstacles+1];
		
		RobotSetup[] robotSetups = new RobotSetup[NumObstacles+1];
		
		for ( int NdxObstacle=0; NdxObstacle < NumObstacles; NdxObstacle++ ) {
			
			// TODO : something
			RobotSetup TempValue;
			do {
				// we select a random tile...
				int InitialTileRow = (int) (Math.random() * NumTileRows); //Tile row 
				int InitialTileCol = (int) (Math.random() * NumTileCols); //Tile column
				
				// ...and convert its coordinates to pixels
				double InitialObstacleRow = InitialTileRow * TileSize + HalfTile ;
				double InitialObstacleCol = InitialTileCol * TileSize + HalfTile ;
				
				//store it on a temp RobotSetup object
				TempValue= new RobotSetup(InitialObstacleRow, InitialObstacleCol, 0.0);

			} while (Arrays.asList(robotSetups).contains(TempValue));  // we check if the position has already been used before
			
			existingRobots[NdxObstacle] = modelRobots[0];
			robotSetups[NdxObstacle] = TempValue; //new RobotSetup(InitialObstacleRow, InitialObstacleCol, 0.0);
			
		}
			
			
		
		/********************************** Create the agent ************************************/
		// Create the agent and place it in a random position without obstacle

		existingRobots[NumObstacles] = modelRobots[NumObstacles + 1];
		
		
		// place it in random position ...
		RobotSetup TempValue;
		do{
			// 	we select a random tile...
			int InitialTileRow = (int) (Math.random() * NumTileRows); //Tile row 
			int InitialTileCol = (int) (Math.random() * NumTileCols); //Tile column
		
			// ...and convert its coordinates to pixels
			double InitialAgentRow = InitialTileRow * TileSize + HalfTile ; 
			double InitialAgentCol = InitialTileCol * TileSize + HalfTile ; 

			//store it on a temp RobotSetup object
			TempValue= new RobotSetup(InitialAgentRow, InitialAgentCol, 0.0);

		} while (Arrays.asList(robotSetups).contains(TempValue));  // we check if the position has already been used before		
		
		//This positions the agent in the initial position
		robotSetups[NumObstacles] = TempValue; /*new RobotSetup(InitialAgentRow, InitialAgentCol, 0.0);*/
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
