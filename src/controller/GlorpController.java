package controller;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;

import model.Direction;
import model.Door;
import model.Fixture;
import model.Item;
import model.Maze;
import model.Player;
import model.Riddle;
import model.Room;
import model.Skin;
import model.SkinType;
import view.GameIcon;
import view.GlorpGUI;
import view.RiddlePanel;

/**
 * The controller, mediates the maze model and GUI. 
 * @authors Heather Finch, Katelynn Oleson, Ken Smith
 * @version
 */
public class GlorpController implements KeyListener{
	private static final HashMap<Direction, Point> myPositionChange = new HashMap<Direction, Point>();
    // fields
	private Maze myMaze;
	// TODO: remover ref to player piece ? can get from maze
	private Player myPlayer;
	private GlorpGUI myWindow; 
	private final Set<Integer> myPressedKeys = new HashSet<Integer>();
	
	/**
	 * Default constructor for GlorpController
	 */
	public GlorpController(){
		myMaze = Maze.getInstance();
		myPlayer = myMaze.getPlayer();		
		myWindow = new GlorpGUI();
        myWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        myWindow.setVisible(true);
        myWindow.setTitle("GLORP");
        myWindow.addKeyListener(this);    
        myWindow.repaint();
        
        // set up hashmap
        // TODO: Fix magic numbers! & make this a helper?
        myPositionChange.put(Direction.EAST, new Point(20, 200));
        myPositionChange.put(Direction.WEST, new Point(380, 200));
        myPositionChange.put(Direction.NORTH, new Point(200, 380));
        myPositionChange.put(Direction.SOUTH, new Point(200, 20));
    }
	
	/**
	 * Adds the pressed key's KeyCode to the set of pressed keys. Generates and validates a direction from the set,
	 * then moves the player.
	 */
	@Override
    public void keyPressed(KeyEvent e) {
		int k = e.getKeyCode();
        myPressedKeys.add(k);
        
        Direction inDirection = Direction.generateDirection(myPressedKeys);
        Direction validDirection = null;
		
        try {
			validDirection = myMaze.getCurrRoom().validateDirection(myPlayer, inDirection);
			myPlayer.move(validDirection);
		} catch (CloneNotSupportedException e1) {
			e1.printStackTrace();
		}
        checkInteractions();
        myWindow.repaint();
    }

	/**
	 * A helper method, checks for item, fixture, and door interactions after a key event.
	 */
    private void checkInteractions() {
        checkItems();
    	checkFixtures();
    	
    	Direction inDir = checkDoorZones(); 
    	Door inCurrDoor = myMaze.getCurrRoom().getDoors().get(inDir);
    	
    	if(inDir != null) { //if near a door
    	    if(myMaze.isValidMove(inDir, myMaze.getCurrRoom())) { // If valid to attempt to move in that direction
                if(inCurrDoor.isUnlocked()) { // If the door is unlocked, move that direction
                    move(inDir); //update map & player
                }else {
                    openRiddleThreads(inDir); //open producer and consumer threads to watch for riddle activity
                }
    	    }
    	}
	}

    private void checkItems() {
        // check items
        Item inItem = myMaze.getCurrRoom().getItem();
        if(inItem != null && myPlayer.getIconArea().intersects(inItem.getIconArea())) {
            myPlayer.getInventory().add(myMaze.getCurrRoom().getItem());
            myMaze.getCurrRoom().setItem(null);
            myMaze.getCurrRoom().setCurrentRoom(true);
            myWindow.updateItemPanel(myPlayer);
        }
    }
    
    private void checkFixtures() {
     // check fixtures
        Fixture inFixture = myMaze.getCurrRoom().getFixture();
        if(inFixture != null && inFixture.getInteractionZone() != null && myPlayer.getIconArea().intersects(inFixture.getInteractionZone())) {
            if(!myPlayer.getInventory().isEmpty()) {
                System.out.println("you win");
                //myPlayer.getInventory().remove(0);
                //myWindow.updateItemPanel(myPlayer);
                inFixture.setBase(new Rectangle(new Dimension(0,0)));
                inFixture.setIconArea(new Rectangle(new Dimension(0,0)));
                inFixture.setInteractionZone(new Rectangle(new Dimension(0,0)));
                inFixture.setMyYCoordinate(inFixture.getMyYCoordinate()-50);
                TimerTask task = new TimerTask() {
                    int i = 0;
                    @Override
                    public void run() {
                        if (i <= 15) {
                            inFixture.setIcon(new GameIcon("src/icons/explosion/frame_" + i + ".png", 200, 250));
                            myWindow.repaint();
                            i++;
                        }
                        else {
                            cancel();
                        }
                    }
                };

                Timer timer = new Timer();
                timer.scheduleAtFixedRate(task, 0, 100);
                
            }
        }
    }
    
    /**
     * returns the direction of the door in region of or null. ... not the best, but whatev
     * 
     * return inner class obj, boolean hasDoor, doorDirection? 
     * 
     * 
     */
    private Direction checkDoorZones() {
        // duplicate code, create doorZone class/ hashmap with rectangle, Direction
        // east door zone
        if(myPlayer.getIconArea().intersects(Room.getEastDoorZone())) {
            return Direction.EAST; 
        }
        // west door zone
        else if(myPlayer.getIconArea().intersects(Room.getWestDoorZone())) {
            return Direction.WEST;   
        }
        // north door zone
        else if(myPlayer.getIconArea().intersects(Room.getNorthDoorZone())) {
            return Direction.NORTH; 
        }
        // south door zone
        else if(myPlayer.getIconArea().intersects(Room.getSouthDoorZone())) {
            return Direction.SOUTH; 
        }else
            return null;
    }
    
    /**
     * Moves the player in the given direction 
     * and updates player location on map & room panels
     */
    private void move(Direction theDirection) {
        myMaze.move(theDirection); 
        
        myPlayer.getCoordinate().setLocation(myPositionChange.get(theDirection)); 
        myPlayer.updateRectangles();  
    }
    
    /**
     * Open Producer and Consumer Threads for Riddle Panel 
     * 
     */
    private void openRiddleThreads(Direction theDirection) {
        // open producer
        Riddle currRiddle = myMaze.getCurrRoom().getDoors().get(theDirection).getMyRiddle();
        
        RiddlePanel inRiddlePanel = myWindow.getRunnableRiddlePanel(currRiddle);
                
        Thread inRiddleProducer = new Thread(inRiddlePanel); 
        inRiddleProducer.start(); // show riddle prompt and wait for message
        
        // open consumer
        Thread inConsumer = new RiddleConsumer(inRiddlePanel, inRiddleProducer);
        inConsumer.start();
        
    }
    
    /**
     * Removes the KeyCode from the set of pressed keys. Sets stride to 0 if no keys are pressed.
     */
	@Override
    public void keyReleased(KeyEvent e) {
		int inKey = e.getKeyCode();
		myPressedKeys.remove(inKey);
		
		if(myPressedKeys.isEmpty()) {
			myPlayer.setStride(0);
			myPlayer.setSkipFrame(false);
		}
		myWindow.repaint();
	}

	public void actionPerformed(ActionEvent e) {
		// TODO: Auto-generated method stub	
	}

	public void keyTyped(KeyEvent e) {
		// TODO: Auto-generated method stub
		
	}
	
	private class RiddleConsumer extends Thread{
	    private RiddlePanel myRiddlePanel;
	    private Thread myProducer;
	    private boolean hasMessage;
	    
	    public RiddleConsumer(RiddlePanel thePanel, Thread theProducer) {
	        myRiddlePanel = thePanel;
	        myProducer = theProducer;
	        hasMessage = false;
	    }
	    
	    /*
	     * Returns true if the message response equals the answer in the riddle
	     */
	    private boolean answerCorrect() {
	        return myRiddlePanel.getResponse().equals(myRiddlePanel.getRiddle().getAnswer());
	    }
	    
	    /**
         * Wait to receive message, or "run away"
         */
        @Override
        public void run() {
          //while no message || player still in door region 
            while(!((hasMessage) || checkDoorZones() != null)){ 
//                try {
//                    Thread.sleep(50);
//                } catch (InterruptedException e) {
//                    // TODO Auto-generated catch block
//                    System.out.println("Error in GlorpController run method!");
//                    e.printStackTrace();
//                }
            }
            
            Direction inDir = checkDoorZones();
            
            if(hasMessage && inDir != null) {
                if(answerCorrect()) {
                    move(inDir);
                }
            }

            // terminate this thread & producer thread 
            myRiddlePanel.shutDown(); 
  
        }
	}

}