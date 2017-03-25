package searchpractice;

import robocode.control.BattleSpecification;
import robocode.control.BattlefieldSpecification;
import robocode.control.RobocodeEngine;
import robocode.control.RobotSetup;
import robocode.control.RobotSpecification;

public class RouteFinder {

	private static final String ROBOCODEPATH = "/home/frizio/robocode";
	
	
	public static void main(String[] args) {
		
		/******************************** Inizialize Robocode *************s************************/
		
		// Create the RobocodeEngine
		RobocodeEngine engine = new RobocodeEngine(new java.io.File(ROBOCODEPATH));
		
		// Show the Robocode battle view
		engine.setVisible(true);
		
		/*******************************************************************************************/
		
		/******************************* Create the battlefield ************************************/
		
		// Create the battlefield (a grid of 800 x 600) ???????????????
		int NumPixelRows= 800; //8 * 64;
		int NumPixelCols= 600; //8 * 64;
		BattlefieldSpecification battlefield = new BattlefieldSpecification(NumPixelRows, NumPixelCols);
		
		/*******************************************************************************************/
		
		/******************************* Setup battle parameters ***********************************/

		// Parameters
		int numberOfRounds = 5;
		long inactivityTime = 10000000;
		double gunCoolingRate = 1.0;
		int sentryBorderSize = 50;
		boolean hideEnemyNames = false;
		
		// TODO : calculate them
		int NumObstacles = 2; 
		
		/******************************* Create obstacles *********************************/
		// Create obstacles and place them at random,
		// so that no pair of obstacles are at the same position
		
		
		// ????????????? ask to prof
		System.out.println("Robot creation...");
		RobotSpecification[] modelRobots = 
				engine.getLocalRepository("sample.SittingDuck.searchpractice.RouteBot*");
		System.out.println("Robot created!");
		System.out.println(modelRobots.toString());
		
		
		RobotSpecification[] existingRobots = new RobotSpecification[NumObstacles+1];
		
		RobotSetup[] robotSetups = new RobotSetup[NumObstacles+1];
		
		for ( int NdxObstacle=0; NdxObstacle < NumObstacles; NdxObstacle++ ) {
			
			// TODO : something
			
			double InitialObstacleRow = 100.00;  // 0 + (int) (Math.random() * NumPixelRows); 
			double InitialObstacleCol = 100.00; // 0 + (int) (Math.random() * NumPixelCols);

			existingRobots[NdxObstacle] = modelRobots[0];
			robotSetups[NdxObstacle] = new RobotSetup(InitialObstacleRow, InitialObstacleCol, 0.0);
		}
		
		/********************************** Create the agent ************************************/
		// Create the agent and place it in a random position without obstacle

		existingRobots[NumObstacles] = modelRobots[NumObstacles + 1];
		
		// TODO : place it in random position ...
		double InitialAgentRow  = 5.0; 
		double InitialAgentCol = 5.0; 
		
		// ??????????????????? ask to prof
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
