package searchpractice;

import java.util.ArrayList;
import java.util.Stack;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;

import robocode.Robot;


public class routeBot___v2  {

	
    // we store f, g and h as needed in the tiles of the battlefield, also if the tile is closed or not
	public class Tile{  
        int h = 0; //Heuristic estimation to end position
        int g = 0; //Cost to get here
        int f = 0; //G+H
        boolean closed = false;
        int x, y;  //tile coordinates in battlefield
        Tile parent; 
        
        Tile(int x, int y){
            this.x = x;
            this.y = y; 
        }
        
    }

//}
 

	
	//SUPPORT FUNCTIONS	
//generate a list of consecutive integers for the random generation of battlefield positions
	public static List<Integer> generateAllPositions(int n){
		List<Integer> list = new ArrayList<Integer>(n);
		for (int x = 0; x < n; x++) {
			list.add(x);
		}
		return list;
	}

//mark tile as blocked
    public  void setBlocked(Tile[][]battlefield, int x, int y){
        battlefield[x][y] = null;
    }	

    /////////////////////A* ALGORITHM ///////////////////////////////
    
	//we only allow horizontal or vertical moves with equal costs
	public final int MovementCost = 1; 
	
    //Open Set is a priority queue
	static PriorityQueue<Tile> openSet;
	
	//Closed Set is a boolean matrix
    //static boolean closed[][];
    
    //Start and end positions
    static int startX, startY;
    static int endX, endY;
 
    //The heuristic function is the Manhattan distance
    private int ManhattanDistance(Tile tile){
    	return (Math.abs(tile.x-endX)+Math.abs(tile.y-endY));
    }
    
    // update values of adjacent Tiles
     void updateCost(Tile currentTile, Tile nextTile){
    
    	 //if the next tile is a sitting duck or has been closed do nothing
    	if(nextTile == null || nextTile.closed) 
    		return;
    	
    	// we calculate the heuristic cost of the next tile
    	nextTile.h = ManhattanDistance(nextTile);
 
    	// we calculate the final cost of the next tile
    	int nextTile_f = nextTile.h + currentTile.g + MovementCost; //cost to get to nextTile + heuristic

        //the next tile is in Open Set
        boolean inOpenSet = openSet.contains(nextTile);
        
        //if nextTile is not in the openSet set or the stored value is greater than the one we have just calculated
        //we update the values in nexTile 
        if(!inOpenSet || nextTile_f<nextTile.f){
            nextTile.f = nextTile_f;
            nextTile.g = currentTile.g + MovementCost;
            nextTile.parent = currentTile;
            if(!inOpenSet)openSet.add(nextTile);
        }
    }

    public void AStar(Tile[][]battlefield, int startX, int startY, int endX, int endY){ 
  
    	//Open Set is a priority queue ordered with the value of f (lower first)
    	openSet = new PriorityQueue<Tile>
    		((Tile t1, Tile t2) -> {
    			if (t1.f<t2.f) return -1;
    			else if (t1.f>t2.f) return 1;
    			else return 0;
    		});

    	//add the start location to openSet list.    	
    	openSet.add(new Tile(startX,startY));

    	//closed is a boolean matrix of NumTileRows x NumTileCols elements (one per battlefield tile) 
    	//closed= new boolean[battlefield.length][battlefield[0].length];
    	
    	Tile current;
        
        while(true){ 
            //We take the first element of the Open Set
        	current = openSet.poll();
            //If there are no more nodes we end
        	if(current==null)break;
            //we close the position
        	current.closed=true; 
            // we are in the end position 
            if(current.x==endX && current.y == endY){
                return; 
            } 

            Tile next;
            //the tile to the left of current is on the battlefield
            if(current.x-1>=0){
                next = battlefield[current.x-1][current.y];
                updateCost(current, next); 
            } 
            //the tile to the right of current is on the battlefield
            if(current.x+1<battlefield.length){
                next = battlefield[current.x+1][current.y];
                updateCost(current, next);
            }
            //the tile up from current is on the battlefield
            if(current.y+1<battlefield[0].length){
                next = battlefield[current.x][current.y+1];
                updateCost(current, next); 
            }
            //the tile down from current is on the battlefield
            if(current.y-1>=0){
                next = battlefield[current.x][current.y-1];
                updateCost(current, next); 
            }       
        } 
    }
 
    
    public class routeBot extends Robot {
	public void run() {

	//GENERATE THE MAP AGAIN
	// Create the battlefield (a battlefield of 800 x 600) 
	int NumPixelRows= 800;
	int NumPixelCols= 600;
	int TileSize=64;
	
	//calculate the number of tiles on battlefield
	int NumTileRows = (int) (NumPixelRows / TileSize); //if number of rows not an integer, truncate it
	int NumTileCols = (int) (NumPixelCols / TileSize); //if number of columns not an integer, truncate it

///////////////////////////////////CREATE THE BATTLEFIELD//////////////////////////////////777
	Tile[][] battlefield = new Tile[NumTileRows][NumTileCols];
	
    // Initial list of all possible initial position in the matrix
    // To semplicity x use a list of linear index, and after select, convert them to a (row,col) indexes.
	List<Integer> remainingPossiblePositions = generateAllPositions(NumTileRows*NumTileCols);
	
	double SittingDuckPercentage = 0.30;
	int NumObstacles = (int) (NumTileRows * NumTileCols * SittingDuckPercentage) ; 

///////////////////////////PLACE THE SITTING DUCKS ON BATTLEFIELD///////////////////////////////////	
	// Create random number generator and set the initial seed (to repeat the same experiment)
	Random randomGenerator = new Random();
    randomGenerator.setSeed(9); // set this seed as it generates a map without unaccessible positions
	
	for ( int NdxObstacle=0; NdxObstacle < NumObstacles; NdxObstacle++ ) {
					
		// Generate a random linear index between the possible position
		int idx = randomGenerator.nextInt(remainingPossiblePositions.size());
		int position = remainingPossiblePositions.remove(idx);
		
		// Convert linear index to (row, column) indexes.
		int InitialTileRow = position % NumTileRows;  
		int InitialTileCol = position / NumTileRows;
	
		//Block the tile on the battlefield matrix
		setBlocked(battlefield, InitialTileRow, InitialTileCol);
	}
/////////////////////////GENERATE THE INITIAL POSITON OF THE AGENT////////////////////////////////
		int idx = randomGenerator.nextInt(remainingPossiblePositions.size());
		int position = remainingPossiblePositions.remove(idx);
			
		// Convert linear index to (row, column) indexes.
		int InitialTileRow = position % NumTileRows;  
		int InitialTileCol = position / NumTileRows;
		
		//Set initial position for Astar algorithm
		startX = InitialTileRow;
		startY = InitialTileCol;
		
		System.out.println("Agent Initial position: " + InitialTileRow + " " + InitialTileCol);
		
/////////////////////////GENERATE THE FINAL POSITION TO REACH///////////////////////////////
		idx = randomGenerator.nextInt(remainingPossiblePositions.size());
		position = remainingPossiblePositions.remove(idx);
			
		// Convert linear index to (row, column) indexes.
		InitialTileRow = position % NumTileRows;  
		InitialTileCol = position / NumTileRows;
		
		//Set final position for Astar algorithm
		endX = InitialTileRow;
		endY = InitialTileCol;	
		System.out.println("Agent destination: " + InitialTileRow + " " + InitialTileCol);
	    
///////////////////////////////// CALL A* ALGORITHM ///////////////////////////////////
		AStar(battlefield,startX,startY,endX,endY);
		
		//If we have reached the destination
		if(battlefield[endX][endY].closed){
            //Trace back the path; we do this pushing the movements starting from the destination into a stack
			// so they can be later pulled in the correct order by the agent. 
			//The agent heading on each step should be 90 x path.pop   
			Stack < Integer > path = new Stack < Integer > ();       
			Tile current = battlefield[endX][endY];
			while(current.parent!=null){
            	  if (current.y > current.parent.y) // last movement was UP
            	  	path.push(0);
            	  else if (current.x>current.parent.x) // last movement was RIGHT
            		  path.push(1);
            	  else if (current.y<current.parent.y) // last movement was DOWN
            		  path.push(2);
            	  else	// last movement was LEFT
            		  path.push(3);

            	  //continue with the previous tile on the path
            	  current=current.parent;
			}
         }else System.out.println("No path found");
		
	}
  }
}
