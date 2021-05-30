package model;

import java.awt.Dimension;
import java.awt.Rectangle;

import view.GameIcon;

public class Item  extends GamePiece{
	private final static int WIDTH = 50;
	private final static int HEIGHT = 75;
	private final static GameIcon ROOM_ICON = new GameIcon("src/icons/anhk_key.png", WIDTH, HEIGHT);
	private final static GameIcon ITEM_PANEL_ICON = new GameIcon("src/icons/key_item_icon.png", 75);
	private Rectangle myIconArea;
	
	public Item() {
		super();
		myIconArea = null;
	}
	
	public Item(PiecePoint thePoint) {
		super();
		myIconArea = new Rectangle(thePoint, new Dimension(WIDTH,HEIGHT));
	}

	/**
	 * @return the ROOM_ICON
	 */
	public GameIcon getRoomIcon() {
		return ROOM_ICON;
	}
	
	/**
	 * @return the ITEM_PANEL_ICON
	 */
	public GameIcon getItemPanelIcon() {
		return ITEM_PANEL_ICON;
	}

	/**
	 * @return the myIconArea
	 */
	public Rectangle getIconArea() {
		return myIconArea;
	}

	/**
	 * @return the width
	 */
	public static int getWidth() {
		return WIDTH;
	}

	/**
	 * @return the height
	 */
	public static int getHeight() {
		return HEIGHT;
	}
}
