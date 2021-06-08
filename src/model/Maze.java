package model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Random;

import controller.SerializeGame;
import view.GameIcon;

/**
 * The maze has all the rooms in a 2D array with a buffer of size 1 around the border.
 * Maze uses a singleton pattern.
 * @author Ken Smith, Heather Finch, Katelynn Oleson 
 * @version 5.14.21
 */
public class Maze implements Serializable{
	/**
     * 
     */
    private static final long serialVersionUID = 7714896058615502865L;

    // fields
    
    //Data map for serialization
    private static HashMap<String, Object> myDataMap;

	// The 2D array that stores each room
	private static Room[][] myMaze;
	
// cant be final for serialization
	// The player
	private static Player myPlayer; 
	
	// The room our Player is currently in
	private static Room myCurrentRoom;

	// The room the player starts in
	private static Room myStartRoom;
	
	// The room that contains the key (used to unlock chest in win room)
	private static Room myKeyRoom;

	// The room the player must get to so that they can win
	private static Room myWinRoom;
	
	// Provides information about whether we can access the win room
	private static boolean myCanAccessWinRoom;
	
	// Provides information about whether we can access the key room
	private static boolean myCanAccessKeyRoom;
	
	// The number of rows in the maze that store rooms
	private final int LENGTH = 7;
	
	// The number of columns in the maze that store rooms
	private final int WIDTH = 7;
	
	// The border around the entire room is 1 space wide on each side.
	// This is 2 to account for the buffer on both sides.
	private final int BORDER_BUFFER = 2;
	
//  cant be final if gonna serialize 
	// Creates the maze.
    private final static Maze THISMAZE = new Maze(); 

    /**
     * A private constructor due to singleton pattern.
     */
    private Maze() {
		
		// Initialize with row-major: Room[rows][columns]
		myMaze = new Room[LENGTH+BORDER_BUFFER][WIDTH+BORDER_BUFFER];
		
		// TODO: Allow option for skin type input
		myPlayer = new Player(SkinType.ALIEN);
		
		// Fills out the 2d array, myMaze, with rooms
		addRooms();
		DoorFactory inFactory = new DoorFactory(myMaze); // fill rooms with doors
        myMaze = inFactory.getRooms();
        blockBorderRooms(); // create "border wall" of completely blocked rooms surounding map
        myCanAccessWinRoom = true;
        myCanAccessKeyRoom = true;
        
        setUpNameObjectMap();
  
	}
	
	/**
	 * Getter for the maze instance.
	 * @return
	 */
	public static Maze getInstance() {
		return THISMAZE;
    }
	

	/**
	 * Creates and adds rooms to myMaze. 
	 */
	private void addRooms() {
//<<<<<<< HEAD
//		for(int r = 0; r < LENGTH+BORDER_BUFFER; r++) {
//			for(int c = 0; c < WIDTH+BORDER_BUFFER; c++) {
//				myMaze[r][c] = new Room(myPlainRoomIcon, myPlainRoomIcon, r, c);
//=======
		for(int row = 0; row < LENGTH+BORDER_BUFFER; row++) {
			for(int col = 0; col < WIDTH+BORDER_BUFFER; col++) {
				myMaze[row][col] = new Room(row, col);
			}
		}
		designateWinStartKeyRooms();
		myCurrentRoom = myStartRoom;
	}

   /**
    * Blocks all doors along the border to simulate a wall.
    */
   private void blockBorderRooms() {
       for(int row = 0; row < LENGTH+BORDER_BUFFER; row++) { 
           blockDoors(myMaze[row][0]);
           blockDoors(myMaze[row][WIDTH+BORDER_BUFFER - 1]);
       }     
       for(int col = 1; col < WIDTH+BORDER_BUFFER; col++) {
           blockDoors(myMaze[0][col]);
           blockDoors(myMaze[LENGTH+BORDER_BUFFER - 1][col]);
       }
    }
	
   /**
    * Block all doors in a room. 
    */
   private void blockDoors(Room theRoom) {
       for(Door d : theRoom.getDoors().values()) {
           d.setBlocked();
       }
   }
   
	/**
	 * Randomly sets the WinRoom and StartRoom coordinates 
	 * so that they are not on the edge of the maze. Adds chest fixture to win room
	 */
	private void designateWinStartKeyRooms() {
	    int inStartRow = generateRandom(BORDER_BUFFER, LENGTH - 2);
	    int inStartCol = generateRandom(BORDER_BUFFER, WIDTH - 2);
	    
		myStartRoom = this.getRoom(inStartRow, inStartCol);
		myStartRoom.designateStartRoom();
		
		int inWinRow = 0, inWinCol = 0;
		// TODO: win and start room must be 1/3 of the maze away - helper method? 
		while(inWinRow == 0 || Math.abs(inWinRow - inStartRow) < LENGTH / 3) {
		    inWinRow = generateRandom(BORDER_BUFFER, LENGTH - 2);
		    
		}
	    while(inWinCol == 0 || Math.abs(inWinCol - inStartCol) < WIDTH / 3) {
	            inWinCol = generateRandom(BORDER_BUFFER, WIDTH - 2);
	    }
	    
		myWinRoom = this.getRoom(inWinRow, inWinCol);
		myWinRoom.designateWinRoom(true);
		myWinRoom.setFixture(new Fixture(175, 200, FixtureType.CHEST)); // Add chest
		placeItems(inStartRow, inStartCol, inWinRow, inWinCol);
	}
	
	/**
	 * A helper method. Places a key item in a room.
	 */
	private void placeItems(int theStartRow, int theStartCol, int theWinRow, int theWinCol) {
		PiecePoint randomCoordinates = PiecePoint.randomPoint(Room.getSize()-Item.getMaxSize(), Room.getSize()-Item.getMaxSize());
		int keyRow = theStartRow;
		int keyCol = theStartCol;
		while(keyRow == theStartRow || keyRow == theWinRow) {
			keyRow = generateRandom(BORDER_BUFFER, LENGTH - 2);
		}
		while(keyCol == theStartCol || keyCol == theWinCol) {
			keyCol = generateRandom(BORDER_BUFFER, LENGTH - 2);
		}
		myMaze[keyRow][keyCol].addItem(new Item(randomCoordinates, ItemType.KEY));
		myKeyRoom = myMaze[keyRow][keyCol];
		myKeyRoom.setSmallIcon(Room.getKeyMapIcon());

	}


	 // TODO: make this into a utility, it also exists in Riddle Factory
     /**
	  * Generates a random index between two numbers (min val, max val)
	  */
	 private int generateRandom(int theMin, int theMax) {
	     Random rand = new Random();
	     return rand.nextInt(theMax - theMin + 1) + theMin;
	 }
	 
//    /*
//     * Generates a random index between two numbers (min val, max val) 
//     */
//     private static int generateRandom(int theMin, int theMax) {
//         Random rand = new Random();
//         return rand.nextInt(theMax - theMin + 1) + theMin;
//         // highest val is ((theMax - theMin + 1) - 1) + theMin = theMax
//         // lowest val is (0) + theMin = theMin
//     }
     
	/**
	 * Check if moving in a certain direction is valid.
	 * Returns true if the user is not only the border of the maze AND if the door is not blocked
	 * Does not check if the door is locked or unlocked
	 * @param theDirection
	 * @return boolean canMove
	 */
	public boolean isValidTraversal(Direction theDirection, Room theRoom) {
	    RoomIndex currIndex = myCurrentRoom.getIndex();
        int row = currIndex.getRow();
        int col = currIndex.getCol();
        
	    return (theDirection.getLabel().equals("N") && row >= BORDER_BUFFER/2 + 1 && !theRoom.getDoors().get(Direction.NORTH).isBlocked())||  // Go North
        (theDirection.getLabel().equals("S") && row < LENGTH && !theRoom.getDoors().get(Direction.SOUTH).isBlocked()) ||  // Go South
        (theDirection.getLabel().equals("E") && col < WIDTH && !theRoom.getDoors().get(Direction.EAST).isBlocked()) ||   // Go East
        (theDirection.getLabel().equals("W") && col >= BORDER_BUFFER/2 + 1 && !theRoom.getDoors().get(Direction.WEST).isBlocked());    // Go West
	
	}
	
	
	// TODO: Right now move in Maze uses Direction - we may want to make a custom direction class
	// TODO: add exception handeling to move method
	/**
	 * Move in a direction through the maze.
	 * @param theDirection
	 */
	public void traverseMaze(Direction theDirection) {
		Room tempCurrentRoom = myCurrentRoom;
		RoomIndex currIndex = myCurrentRoom.getIndex();
		int inRow = currIndex.getRow();
		int inCol = currIndex.getCol();

		// Checks if the move is valid and the door is unlocked
		if(!(isValidTraversal(theDirection, myCurrentRoom) || !myCurrentRoom.getDoors().get(theDirection).isUnlocked())) {
			throw new IllegalArgumentException("You cannot move that way");
	    }
		
		if(theDirection.getLabel().equals("N")) { // Go North
            myCurrentRoom = myMaze[inRow-1][inCol];

        } else if(theDirection.getLabel().equals("S")) {  // Go South
            myCurrentRoom = myMaze[inRow+1][inCol];

        } else if(theDirection.getLabel().equals("E")) {   // Go East
            myCurrentRoom = myMaze[inRow][inCol+1];

        } else if(theDirection.getLabel().equals("W")) {    // Go West
            myCurrentRoom = myMaze[inRow][inCol-1];  
		}
		
		tempCurrentRoom.setCurrentRoom(false);
		tempCurrentRoom.setPlayer(null);
		
		myCurrentRoom.setCurrentRoom(true);
		myCurrentRoom.setPlayer(myPlayer); //TODO use add player instead when we have doors
	}
	
	/**
	 * Getter for the current room the player is in
	 * @return
	 */
	public Room getCurrRoom() {
	    return myCurrentRoom;
	}
	
	/**
	 * Getter for the Player
	 * @return
	 */
	public Player getPlayer() {
	    return myPlayer;
	}
	
	/**
	 * Returns the room at the provided row and column
	 * @param theRow
	 * @param theColumn
	 * @return
	 */
	public Room getRoom(int theRow, int theColumn) {
		if(theRow < 0 || theColumn < 0) throw new 
				IllegalArgumentException("getRoom error: The index of the rooms cannot be negative");
		else if(theRow > (LENGTH) || theColumn > (WIDTH)) 
		    throw new IllegalArgumentException("getRoom error: The index of the rooms cannot be greater than the size of the maze");
		
		// Using the line below does not return the correct room for testing purposes.
		// Not sure if this method will be needed anywhere else. 
		// Logic is so the client does not know about the buffer rooms and cannot access the buffer rooms
		//return myMaze[theRow + BORDER_BUFFER/2][theColumn + BORDER_BUFFER/2];
	
		return myMaze[theRow][theColumn];
	}
	
	/**
	 * 
	 * @return
	 */
	public Room getMyStartRoom() {
		return myStartRoom;
	}

	/**
	 * 
	 * @param theStartRoom
	 */
	public void setMyStartRoom(Room theStartRoom) {
		this.myStartRoom = theStartRoom;
	}
	
	/**
	 * 
	 * @return
	 */
	public Room getMyCurrentRoom() {
		return myCurrentRoom;
	}
	
	/**
	 * 
	 * @param myCurrentRoom
	 */
	public void setMyCurrentRoom(Room myCurrentRoom) {
		this.myCurrentRoom = myCurrentRoom;
	}
	
	/**
	 * 
	 * @return
	 */
	public Room getMyWinRoom() {
		return myWinRoom;
	}

	/**
	 * 
	 * @param theWinRoom
	 */
	public void setMyWinRoom(Room theWinRoom) {
		this.myWinRoom = theWinRoom;
	}
	
	/**
	 * Returns the length of this mazes 2D Room array 
	 * @return
	 */
	public int getLength() {
	    return LENGTH;
	}
	
	/**
     * Returns the length of this mazes 2D Room array 
     * @return
     */
	public int getWidth() {
        return WIDTH;
    }
	

	// Returns true if the row and column are valid, and false otherwise
	// TODO: containsRoom method is redundant unless we decide to make some spaces in the grid not exist as rooms.
	public boolean containsRoom(int theRow, int theColumn) {
	    if(theRow < 0 || theColumn < 0) return false;
	    else if(theRow >= (LENGTH) || theColumn >= (WIDTH)) return false;
		else return true;
	}
	
	 
	
	/**
	 * Searches for a path from the current room to the win room 
	 * to see if the user can win the game
	 * @return
	 */
	public boolean canWin() {
		boolean [][] visited = new boolean[LENGTH + BORDER_BUFFER][WIDTH + BORDER_BUFFER];
		
		// TODO NOTE if we add more items we will need to use .contains to see if the inventory contains the items we are checking for
		// check if key has been picked up
		if(myPlayer.getInventory().size() == 0) {
			// check if we can access the room with the key
			myCanAccessKeyRoom = false;
	        depthFirstSearchMaze(myCurrentRoom.getIndex().getRow(),  myCurrentRoom.getIndex().getCol(), visited, "key", myKeyRoom);  	        
	        if(myCanAccessKeyRoom == false) return false;
		}
		
		// check if we can access the win room
		visited = new boolean[LENGTH + BORDER_BUFFER][WIDTH + BORDER_BUFFER];
		myCanAccessWinRoom = false;
        depthFirstSearchMaze(myCurrentRoom.getIndex().getRow(),  myCurrentRoom.getIndex().getCol(), visited, "win", myWinRoom);     
  
        return myCanAccessWinRoom;
	}
	
	
	// Helper method for canWin. Uses depth first search to see if the win room is accessible
	private void depthFirstSearchMaze(int theRow, int theColumn, boolean[][] theVisitedRooms, String theRoom, Room theGoalRoom) {				
		
		if(theRoom.equals("key") && myCanAccessKeyRoom == true) return;
		else if(theRoom.equals("win") && myCanAccessWinRoom == true) return;		
		
		// return if we've hit the end of the maze.
	    if (theRow <= 0 || theColumn <= 0 || theRow > LENGTH - 1 || theColumn > WIDTH - 1 || theVisitedRooms[theRow][theColumn]) {
	    	return;
	    }
	    
	    // return if this room doesn't exist
	    if(!this.containsRoom(theRow, theColumn)){
	    	return;
	    }
	    
	    Room currentRoom = myMaze[theRow][theColumn];
	    
	    // return if we've found the win room
	    // TODO, should I use a "break" to exit the depthFirstSearchMaze method once the winRoom has been found?
	    // Otherwise the DFS could recurse further.
	    if(currentRoom == theGoalRoom) {
	    	if(theRoom.equals("key")) myCanAccessKeyRoom = true;
	    	else myCanAccessWinRoom = true;
	    	return;
	    } else {
	
		    //mark the cell visited
		    theVisitedRooms[theRow][theColumn] = true;
		    
		    // if the right door is unlocked:
		    if(!currentRoom.getDoors().get(Direction.EAST).isBlocked()) depthFirstSearchMaze(theRow, theColumn + 1, theVisitedRooms, theRoom, theGoalRoom); // go right
		    		    
		    // if the left door is unlocked:
		    if(!currentRoom.getDoors().get(Direction.WEST).isBlocked()) depthFirstSearchMaze(theRow, theColumn - 1, theVisitedRooms, theRoom, theGoalRoom); //go left
		    
		    // if the bottom door is unlocked:
		    if(!currentRoom.getDoors().get(Direction.SOUTH).isBlocked())	depthFirstSearchMaze(theRow + 1, theColumn, theVisitedRooms, theRoom, theGoalRoom); //go down
		    
		    // if the top door is unlocked:
		    if(!currentRoom.getDoors().get(Direction.NORTH).isBlocked()) depthFirstSearchMaze(theRow - 1, theColumn, theVisitedRooms, theRoom, theGoalRoom); // go up
		}
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(int row = 1; row <= LENGTH; row++) {
			for(int col = 1; col <= WIDTH; col++) {
				Room currRoom = getRoom(row, col);

				if(getRoom(row, col).equals(myCurrentRoom)) {
					sb.append(" current");
				} else if (currRoom == myWinRoom) {
					sb.append("  win   ");
				} else sb.append("   x    ");
			}
			sb.append("\n");
		}
		
		sb.append("\n" + myCurrentRoom.toString()); //for testing serialization
		
		return sb.toString();
	}

	// CHEAT METHOD
	// Unlocks every door in the maze except doors that lead to border rooms
	public void unlockAllDoors() {
		
		// Unlocks all doors in the middle group of rooms
		for(int i = 2; i < LENGTH; i++) {
			for(int j = 2; j < WIDTH; j++) {
				Room currRoom = myMaze[i][j];
				currRoom.setPlayer(myPlayer);
				for(Door currDoor : currRoom.getDoors().values()) {
					currDoor.setUnlocked();
				}
			}
		}
		
		// TODO consolidate cheat method with helper methods?
		// Unlocks north/south doors in the first and last col (not incl border rooms).  
		for(int i = 2; i < LENGTH; i++) {
			Room firstCol = myMaze[i][1];
			Room lastCol = myMaze[i][WIDTH];
			firstCol.setPlayer(myPlayer);
			lastCol.setPlayer(myPlayer);
			
			firstCol.getDoors().get(Direction.NORTH).setUnlocked();
			
			lastCol.getDoors().get(Direction.NORTH).setUnlocked();

			firstCol.getDoors().get(Direction.SOUTH).setUnlocked();

			lastCol.getDoors().get(Direction.SOUTH).setUnlocked();
		}
		
		// Unlocks east/west doors in the first and last row (not incl border rooms).  
		for(int i = 2; i < WIDTH; i++) {
			Room firstRow = myMaze[1][i];
			Room lastRow= myMaze[LENGTH][i];
			firstRow.setPlayer(myPlayer);
			lastRow.setPlayer(myPlayer);
			
			firstRow.getDoors().get(Direction.EAST).setUnlocked();
			
			lastRow.getDoors().get(Direction.EAST).setUnlocked();
			
			firstRow.getDoors().get(Direction.WEST).setUnlocked();

			lastRow.getDoors().get(Direction.WEST).setUnlocked();
		}
		
		getRoom(1, 1).setPlayer(myPlayer);
		getRoom(1, WIDTH).setPlayer(myPlayer);
		getRoom(LENGTH, 1).setPlayer(myPlayer);
		getRoom(LENGTH, WIDTH).setPlayer(myPlayer);
	}
	
	
	// serialize stuff
	
	//NOTE: issues with serializing imageIcons
	
	private void setUpNameObjectMap() {
	    myDataMap = new HashMap<String, Object>();
	    
	    myDataMap.put("Maze", myMaze);
	    myDataMap.put("Player", myPlayer);    
	    myDataMap.put("StartRoom", myStartRoom);
	    myDataMap.put("WinRoom", myWinRoom);
	    myDataMap.put("KeyRoom", myKeyRoom);
	    myDataMap.put("CurrentRoom", myCurrentRoom);
	    myDataMap.put("CanWin", myCanAccessWinRoom);
	    myDataMap.put("CanKey", myCanAccessKeyRoom);  
	}
	
	public static void serializeMyObjects() {
	    if(myDataMap != null) {
    	    for(String fileName : myDataMap.keySet()) {
    	        SerializeGame.serializeMe(myDataMap.get(fileName), fileName);
    	    }
	    }else
	        System.out.println("Maze Data map has not been initialized yet!");
	}
	
	// not the best... 
	public static void deserializeMyObjects() {
	    if(myDataMap != null) {

                myMaze = (Room[][]) SerializeGame.deserializeMe(myDataMap.get("Maze"), "Maze");
                myPlayer = (Player) SerializeGame.deserializeMe(myDataMap.get("Player"), "Player");    
                myStartRoom = (Room) SerializeGame.deserializeMe(myDataMap.get("StartRoom"), "StartRoom");
                myWinRoom = (Room) SerializeGame.deserializeMe(myDataMap.get("WinRoom"), "WinRoom");
                myKeyRoom = (Room) SerializeGame.deserializeMe(myDataMap.get("KeyRoom"),"KeyRoom");
                myCurrentRoom = (Room) SerializeGame.deserializeMe(myDataMap.get("CurrentRoom"), "CurrentRoom");
                myCanAccessWinRoom = (boolean) SerializeGame.deserializeMe(myDataMap.get("CanWin"), "CanWin");
                myCanAccessKeyRoom = (boolean) SerializeGame.deserializeMe(myDataMap.get("CanKey"), "CanKey"); 
                
            
        }else
            System.out.println("Maze Data map has not been initialized yet!");
        
    }
//	
//	private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
//	    ois.defaultReadObject();
//	    THISMAZE = this;
//	}
//
//	private Object readResolve()  {
//	    return THISMAZE;
//	}
//	
//	public static void serializeMaze() {
//	    System.out.println(THISMAZE.toString()); 
//	    
//	    String filename = "savedGame.txt";
//	    //Saving of object in a file             
//	    FileOutputStream file;
//        try {
//            file = new FileOutputStream(filename);
//            ObjectOutputStream out = new ObjectOutputStream(file); 
//            
//            out.writeObject(Maze.getInstance());
//            
//            out.close();             
//            file.close(); 
//        } catch (IOException e2) {
//            // TODO Auto-generated catch block
//            System.out.println("Error in Maze serializeMaze method! ");
//            e2.printStackTrace();
//       }             
//	                	    
//	}
//	
//	public void deserializeMaze() {
//	       ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
//	        ObjectOutputStream oos;
//	        
//	        try {
//	            oos = new java.io.ObjectOutputStream(baos);
//	            oos.writeObject(getInstance());
//	            oos.close();
//	            
//	        } catch (IOException e) {
//	            System.out.println("Error in Maze serializeMaze method! ");
//	            e.printStackTrace();
//	        }
//	        
//	   // ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
//	    InputStream is = new ByteArrayInputStream(baos.toByteArray());
//	    ObjectInputStream ois;
//        try {
//            ois = new ObjectInputStream(is);
//            Maze deserialized = (Maze) ois.readObject();
//            System.out.println(deserialized.toString()); 
//        } catch (IOException | ClassNotFoundException e) {
//            // TODO Auto-generated catch block
//            System.out.println("Error in Maze deserializeMaze method! ");
//            e.printStackTrace();
//        }
//	   
//	   
//	}


}
