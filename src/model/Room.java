package model;

import java.awt.Point;
import java.util.ArrayList;

import javax.swing.ImageIcon;

/**
 * A room can has 4 doors an item, and contains flags indicating 
 * player presence and whether or not the room has been visited.
 * @author
 * @version
 */
//TODO: create subclasses (start and end rooms)
public class Room {
	private int MAX_DOORS = 4;
	
    private Door[] myDoors; // for now... 4 doors each 
	private ArrayList<Piece> myPieces; // array list, no max pieces
	private final ImageIcon myLargeIcon;
	private final ImageIcon mySmallIcon;
	
	//Map<Door, Room> myDoors= new TreeMap<>();
	//private boolean containsPlayer; //will be tracked by map (current room) 
	//private boolean visitedFlag; // will be tracked by map? 
	
    public Room() {
        myPieces = new ArrayList<Piece>();
        myDoors = new Door[MAX_DOORS];
        myLargeIcon = null; 
        mySmallIcon = null;
        createDoors(getRiddles());
    }
    
	public Room(ImageIcon theLargeIcon, ImageIcon theSmallIcon) { // how will rooms get their icons? And riddles? 
	    myPieces = new ArrayList<Piece>();
	    myDoors = new Door[MAX_DOORS];
	    myLargeIcon = theLargeIcon; 
	    mySmallIcon = theSmallIcon;
	    createDoors(getRiddles()); 
	}
	
	/*
	 * Retrieves Riddles from Game 
	 */
	private Riddle[] getRiddles() {
	    // scanner? 
        // Game Model or Maze has file name for data base of riddles 
        // When Room is created, room prompts the Maze MAX_DOORS times for number of needed riddles (SCANNER)
        // Maze reads 
        // Injector type stuff? - riddles are a dependencie? 
	    return new Riddle[MAX_DOORS];
	}
	
	/*
	 * Can rewrite to enable more doors
	 */
	private void createDoors(Riddle[] theRiddles) {
		// TODO Auto-generated method stub
		
	    // create doors with coordinates relative to rooms 
	}

	/**
	 * Get the item from this room.
	 * @return the myItem
	 */
	public Piece removePiece(Point theCoordinates) throws IllegalArgumentException{ // pass in player coordinates? or player object?
	    //if not passing in piece itself, create a dummy piece 
	    Piece inDummy = new Alien(); // doesnt really matter what implementation the piece is
	    
	    for(Piece p : myPieces) {
	        if (((Alien) p).compareTo(inDummy) == 0) { // will need to 
	            inDummy = p;
	        }
	    }
	    
	    if(!(myPieces.remove(inDummy))) { // if dummy was set to p, it will contain it and skip the below statement
	        throw new IllegalArgumentException("Error: no piece at these coordinates.");
	    }
	    
		return inDummy; 
	}
	
	   /**
     * Get the item from this room.
     * @return Boolean indicator if the add was a success
     */
    public Boolean addCharacter(Character theCharacter, Door theUnlockedDoor) throws IllegalArgumentException{ // pass in player coordinates? or player object?
        Boolean inSuccess = false;
        if(theUnlockedDoor.isUnlocked()) { //first check if unlockedDoor is even unlocked
            for(Door d : myDoors) {
                if(theUnlockedDoor.equals(d)) {
                    
                    inSuccess = true;
                }
            }
        }
        return inSuccess;
       
    }

	/**
	 * Place an item in this room
	 * @param myItem the myItem to set
	 */
	public void addPiece(Piece thePiece) throws NullPointerException{
	    if(thePiece == null) {
	        throw new NullPointerException("Piece cannot be null.");
	    }
		myPieces.add(thePiece);
	}
	
	public ImageIcon getLargeIcon() {
	    return null;
	}
	
	public ImageIcon getSmallIcon() {
        return null;
    }
	
	public Point[] getDoorCoordinates() {
	    Point[] inCoordinates = new Point[MAX_DOORS];
	    for(int i = 0; i < MAX_DOORS; i++) {
	        inCoordinates[i] = myDoors[i].getCoordinate(); //will any of the doors ever be null? 
	    }
	    return inCoordinates;
	}
	
}
