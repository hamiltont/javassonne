/**
 * Javassonne 
 *  http://code.google.com/p/javassonne/
 * 
 * @author [Add Name Here]
 * @date Jan 29, 2009
 * 
 * Copyright 2009 Javassonne Team
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License. 
 *  You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software 
 *  distributed under the License is distributed on an "AS IS" BASIS, 
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or 
 *  implied. See the License for the specific language governing 
 *  permissions and limitations under the License. 
 */

package org.javassonne.ui;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import org.javassonne.messaging.Notification;
import org.javassonne.messaging.NotificationManager;
import org.javassonne.model.Tile;
import org.javassonne.model.TileBoard;
import org.javassonne.model.TileDeck;
import org.javassonne.model.TileMapBoard;
import org.javassonne.model.TileSerializer;
import org.javassonne.model.TileSet;

/**
 * The GameController is the primary controller in the application. It is
 * created from the Main function and handles broad events - such as displaying
 * menus, starting new games, etc.. When a game starts, it creates
 * "subcontrollers" to handle the heavy lifting involved in the user interface,
 * etc..
 * 
 * @author bengotow
 */
public class GameController {
	private static final String EXIT_GAME = "Exit Game";

	private static final String YES_PLEASE = "Yes, please";

	private static final String CANCEL = "Cancel!";

	private static final String EXIT_WITHOUT_SAVING = "Exit without saving?";

	private static final long serialVersionUID = 1L;

	private BoardController boardController_;
	private HUDController hudController_;

	private Tile tileInHand_;
	private HUDPanel panel_;

	/**
	 * The default constructor takes no arguments and assumes a GameWindow has
	 * been created and is onscreen. It adds itself to the notification manager
	 * and waits for the user to click something in the menu.
	 */
	public GameController() {

		// register to receive events from the game window
		NotificationManager.getInstance().addObserver(Notification.NEW_GAME,
				this, "newGame");
		NotificationManager.getInstance().addObserver(Notification.EXIT_GAME,
				this, "exitGame");
	}

	/**
	 * Called when a NEW_GAME notification is received.
	 * 
	 * @param n
	 *            The notification object sent from the NotificationManager.
	 */
	public void newGame(Notification n) {

		// Load all possible tiles
		TileSerializer s = new TileSerializer();
		TileSet set = s.loadTileSet("tilesets/standard.xml");
		if (set == null) {
			System.err.println("Tile set could not be found.");
			System.exit(0);
		}

		// Populate the set into the deck. Note: you can have multiple copies of
		// a tile in a deck, but not in a set
		TileDeck deck = new TileDeck();
		deck.addTileSet(set);
		deck.addTileSet(set);

		TileBoard board = new TileMapBoard(deck.popRandomTile());

		// Create an HUDPanel that will provide the end user with a control GUI
		panel_ = new HUDPanel();

		// Create a BoardController to do the heavy lifting during gameplay.
		// These two objects handle notifications from the UI (like rotate
		// tile).
		boardController_ = new BoardController(deck, board);
		hudController_ = new HUDController(deck, board);

		// Add HUD to main canvas
		DisplayHelper.getInstance().add(panel_, DisplayHelper.Layer.PALETTE,
				DisplayHelper.Positioning.TOP_LEFT);
	}

	public void exitGame(Notification n) {

		// TODO: Make the interface disappear and show the menu here.

		// The board controller and HUD controller that we created during
		// gameplay should be deleted. We'll create two more if we start another
		// game.
		boardController_ = null;
		hudController_ = null;
		
		Object[] options = { YES_PLEASE, CANCEL };
		
		JOptionPane p = new JOptionPane();
		p.setOptions(options);
		p.setMessageType(JOptionPane.QUESTION_MESSAGE);
		p.setMessage(EXIT_WITHOUT_SAVING);
		p.setOptionType(JOptionPane.YES_NO_OPTION);
		p.setSize(420, 170);
		p.validate();
		
		JDialog dialog = p.createDialog(null, EXIT_GAME);
		dialog.setAlwaysOnTop(true);
		dialog.show();
	    Object ans = p.getValue();

		if (ans == YES_PLEASE) {
			System.exit(0);
		}
	}
}
