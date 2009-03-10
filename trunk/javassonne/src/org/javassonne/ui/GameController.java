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

import java.util.ArrayList;
import java.util.List;

import org.javassonne.messaging.Notification;
import org.javassonne.messaging.NotificationManager;
import org.javassonne.model.Player;
import org.javassonne.model.Tile;
import org.javassonne.model.TileBoard;
import org.javassonne.model.TileDeck;
import org.javassonne.model.TileMapBoard;
import org.javassonne.model.TileSerializer;
import org.javassonne.model.TileSet;
import org.javassonne.model.Player.MeepleColor;

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

	private static final long serialVersionUID = 1L;

	private BoardController boardController_;
	private HUDController hudController_;

	private Boolean gameInProgress_ = false;

	private MenuPanel menu_;
	private Tile tileInHand_;

	private InputPlayerDataPanel playerData_;
	private List<Player> players_;

	/**
	 * The default constructor takes no arguments and assumes a GameWindow has
	 * been created and is onscreen. It adds itself to the notification manager
	 * and waits for the user to click something in the menu.
	 */
	public GameController() {

		// create the menu panel
		menu_ = new MenuPanel();

		// register to receive events from the game window
		NotificationManager.getInstance().addObserver(Notification.NEW_GAME,
				this, "newGame");
		NotificationManager.getInstance().addObserver(Notification.NEW_NW_GAME,
				this, "newNetworkGame");
		NotificationManager.getInstance().addObserver(Notification.START_GAME,
				this, "startGame");
		NotificationManager.getInstance().addObserver(Notification.END_GAME,
				this, "endGame");
		NotificationManager.getInstance().addObserver(Notification.QUIT,
				this, "quitGame");
		NotificationManager.getInstance().addObserver(
				Notification.TOGGLE_MAIN_MENU, this, "toggleMainMenu");
		NotificationManager.getInstance().addObserver(
				Notification.PLAYER_DATA_RESET, this, "playerDataReset");
	}

	/**
	 * Called when a NEW_GAME notification is received.
	 * 
	 * @param n
	 *            The notification object sent from the NotificationManager.
	 */
	public void newGame(Notification n) {

		InputPlayerDataPanel p = new InputPlayerDataPanel();
		DisplayHelper.getInstance().add(p, DisplayHelper.Layer.MODAL,
				DisplayHelper.Positioning.CENTER);
	}
	
	/**
	 * Called when a NEW_NW_GAME notification is received.
	 * 
	 * @param n
	 *            The notification object sent from the NotificationManager.
	 */
	public void newNetworkGame(Notification n) {

		ViewNetworkHostsPanel p = new ViewNetworkHostsPanel();
		DisplayHelper.getInstance().add(p, DisplayHelper.Layer.MODAL,
				DisplayHelper.Positioning.CENTER);
	}

	/**
	 * Called when a START_GAME notification is received.
	 * 
	 * @param n
	 *            The notification object sent from the NotificationManager.
	 */
	public void startGame(Notification n) {
		// Game in progress!
		gameInProgress_ = true;
		NotificationManager.getInstance().sendNotification(
				Notification.TOGGLE_MAIN_MENU);

		playerData_ = (InputPlayerDataPanel) n.argument();
		loadPlayerData();
		
		DisplayHelper.getInstance().remove(playerData_);

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

		TileBoard board = new TileMapBoard(deck);

		// Create a BoardController to do the heavy lifting during gameplay.
		// These two objects handle notifications from the UI (like rotate
		// tile).
		boardController_ = new BoardController(board);
		hudController_ = new HUDController(deck, players_);
	}
	
	/**
	 * Called when an END_GAME notification is received. This notification used to
	 * be called EXIT_GAME, but it is now possible to end a game without quitting the
	 * app, so we've broken EXIT_GAME into END_GAME and QUIT. The game controller
	 * should clean up any resources related to the game to make sure they are destroyed.
	 * 
	 * @param n
	 *            The notification object sent from the NotificationManager.
	 */
	public void endGame(Notification n) {
		// reset game-related state variables. The board controller and hud controller
		// also receive this notification, and they will remove their views from the screen.
		boardController_ = null;
		hudController_ = null;
		gameInProgress_ = false;
		tileInHand_ = null;
		
		playerData_ = null;
		players_ = null;

		// show the main menu again, but this time with gameInProgress = false
		toggleMainMenu(null);
	}
	
	
	public void toggleMainMenu(Notification n) {
		// Determine whether the game is currently in progress
		menu_.setGameInProgress(gameInProgress_);

		if (menu_.isShowing()) {
			if (gameInProgress_)
				DisplayHelper.getInstance().remove(menu_);
		} else
			DisplayHelper.getInstance().add(menu_, DisplayHelper.Layer.MODAL,
					DisplayHelper.Positioning.CENTER);
	}

	public void quitGame(Notification n) {
		// The board controller and HUD controller that we created during
		// game play should be deleted.
		boardController_ = null;
		hudController_ = null;
		gameInProgress_ = false;

		// Return control to final shutdown process
		System.exit(0);
	}

	private void loadPlayerData() {
		players_ = new ArrayList<Player>();

		// TODO: player colors should be passed in from the InputPlayerDataPanel.
		// In the future, maybe this entire function should be in the panel, and
		// then the gameController can just get a list of player objects?
		int ii = 0;
		for (String s : playerData_.getPlayerData()) {
			if (s.length() > 0){
				Player player = new Player(s);
				player.setMeepleColor(MeepleColor.values()[ii++]);
				players_.add(player);
			}
		}
	}

	public void playerDataReset(Notification n) {
		players_.clear();
	}
}
