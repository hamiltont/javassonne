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

package org.javassonne.ui.controllers;

import java.awt.Point;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.javassonne.algorithms.RegionsCalc;
import org.javassonne.messaging.Notification;
import org.javassonne.messaging.NotificationManager;
import org.javassonne.model.Meeple;
import org.javassonne.model.Player;
import org.javassonne.model.Tile;
import org.javassonne.model.TileBoard;
import org.javassonne.model.TileBoardGenIterator;
import org.javassonne.model.TileBoardIterator;
import org.javassonne.model.TileDeck;
import org.javassonne.model.TileFeature;
import org.javassonne.model.TileMapBoard;
import org.javassonne.model.TileSerializer;
import org.javassonne.model.TileSet;
import org.javassonne.networking.LocalHost;
import org.javassonne.ui.DisplayHelper;
import org.javassonne.ui.controls.JPopUp;
import org.javassonne.ui.panels.InputPlayerDataPanel;
import org.javassonne.ui.panels.InstructionsPanel;
import org.javassonne.ui.panels.MenuPanel;
import org.javassonne.ui.panels.ViewNetworkHostsPanel;

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

	private static final String END_WITHOUT_SAVING = "End Without Saving";

	private static final String RETURN_TO_GAME = "Return to Game";

	private static final long serialVersionUID = 1L;

	private BoardController boardController_;
	private HUDController hudController_;

	private Boolean gameInProgress_ = false;

	private MenuPanel menu_;
	private InstructionsPanel instructions_;

	private InputPlayerDataPanel playerData_;
	private List<Player> players_;
	private int currentPlayer_;

	private TileDeck deck_;

	/**
	 * The default constructor takes no arguments and assumes a GameWindow has
	 * been created and is onscreen. It adds itself to the notification manager
	 * and waits for the user to click something in the menu.
	 */
	public GameController() {

		// Turn off annoying RMI logging
		Logger.getLogger("org.springframework").setLevel(Level.SEVERE);

		// Does nothing but force the LocalHost to
		// be created sometime soon
		LocalHost.getName();

		// create the menu panel
		menu_ = new MenuPanel();
		instructions_ = new InstructionsPanel();

		// register to receive events from the game window
		NotificationManager n = NotificationManager.getInstance();
		n.addObserver(Notification.NEW_GAME, this, "newGame");
		n.addObserver(Notification.NEW_NW_GAME, this, "newNetworkGame");
		n.addObserver(Notification.START_GAME, this, "startGame");
		n.addObserver(Notification.ATTEMPT_END_GAME, this, "attemptEndGame");
		n.addObserver(Notification.END_GAME, this, "endGame");
		n.addObserver(Notification.LOAD_GAME, this, "loadGame");
		n.addObserver(Notification.SAVE_GAME, this, "saveGame");
		n.addObserver(Notification.END_TURN, this, "endTurn");
		n.addObserver(Notification.SCORE_TURN, this, "scoreTurn");
		n.addObserver(Notification.QUIT, this, "quitGame");
		n.addObserver(Notification.TOGGLE_MAIN_MENU, this, "toggleMainMenu");
		n.addObserver(Notification.PLAYER_DATA_RESET, this, "playerDataReset");
		n.addObserver(Notification.TILE_UNUSABLE, this, "beginTurn");
		n.addObserver(Notification.TOGGLE_INSTRUCTIONS, this,
				"toggleInstructions");
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

		// Client mainClient = new Client("mainClient");

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
		currentPlayer_ = 0;
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
		deck_ = new TileDeck();
		deck_.addTileSet(set);
		TileBoard board = new TileMapBoard(deck_);

		// Create a BoardController to do the heavy lifting during gameplay.
		// These two objects handle notifications from the UI (like rotate
		// tile).
		boardController_ = new BoardController(board, deck_
				.tileFeatureBindings(), players_);
		hudController_ = new HUDController(deck_, players_);

		// See if the first person is playing on this computer. If they are,
		// send the begin turn notification to activate the interface for them.
		beginTurn();
	}

	/**
	 * Called when an END_GAME notification is received. This notification used
	 * to be called EXIT_GAME, but it is now possible to end a game without
	 * quitting the app, so we've broken EXIT_GAME into END_GAME and QUIT. The
	 * game controller should clean up any resources related to the game to make
	 * sure they are destroyed.
	 * 
	 * @param n
	 *            The notification object sent from the NotificationManager.
	 */
	public void endGame(Notification n) {
		// reset game-related state variables. The board controller and hud
		// controller also receive this notification, and they will remove their
		// views from the screen.
		boardController_ = null;
		hudController_ = null;
		gameInProgress_ = false;

		playerData_ = null;
		players_ = null;

		Properties config = (Properties) n.argument();
		if (config == null || !config.containsKey("hideMainMenu"))
			// show the main menu again, but this time with gameInProgress =
			// false
			toggleMainMenu(null);
	}

	/*
	 * Game is currently in progress. Verify that the user wants to exit
	 */
	public void attemptEndGame() {
		String[] options = { RETURN_TO_GAME, END_WITHOUT_SAVING };

		JPopUp dialogBox = new JPopUp("A game is currently in progress!");
		String ans = dialogBox.promptUser(options);

		if (ans == END_WITHOUT_SAVING)
			NotificationManager.getInstance().sendNotification(
					Notification.END_GAME);
	}

	public void endTurn(Notification n) {
		// sent from the confirmPlacement panel when the user presses end turn.
		// We want to advance the turn and change current player.
		currentPlayer_ = (currentPlayer_ + 1) % players_.size();
		NotificationManager.getInstance().sendNotification(
				Notification.SET_CURRENT_PLAYER, currentPlayer_);

	}

	public void scoreTurn(Notification n) {
		TileBoardIterator iter = (TileBoardIterator) n.argument();
		Point p = iter.getLocation();

		// Score completed features on this tile
		RegionsCalc c = new RegionsCalc(deck_.tileFeatureBindings());
		for (Tile.Region r : Tile.Region.values()) {
			c.traverseRegion(iter, r);
			if (c.getRegionCompletion(iter.getLocation(), r)) {
				scoreFeature(c.getsizeOfRegion(p, r), c.getMeepleList(p, r),
						iter.current().featureInRegion(r));
			}
		}
		// Score cloisters - go right then move clockwise
		// Note: do not need to recheck iter because it was checked above
		TileBoardGenIterator temp = new TileBoardGenIterator(iter);
		c.traverseRegion(temp.right(), Tile.Region.Center);
		//TODO: Change these to get the correct score (algorithm should do it)
		if (c.getRegionCompletion(temp.getLocation(), Tile.Region.Center)) {
			scoreFeature(9, c.getMeepleList(temp.getLocation(),
					Tile.Region.Center), temp.current().featureInRegion(
					Tile.Region.Center));
		}
		c.traverseRegion(temp.down(), Tile.Region.Center);
		if (c.getRegionCompletion(temp.getLocation(), Tile.Region.Center)) {
			scoreFeature(9, c.getMeepleList(temp.getLocation(),
					Tile.Region.Center), temp.current().featureInRegion(
					Tile.Region.Center));
		}
		c.traverseRegion(temp.left(), Tile.Region.Center);
		if (c.getRegionCompletion(temp.getLocation(), Tile.Region.Center)) {
			scoreFeature(9, c.getMeepleList(temp.getLocation(),
					Tile.Region.Center), temp.current().featureInRegion(
					Tile.Region.Center));
		}
		c.traverseRegion(temp.left(), Tile.Region.Center);
		if (c.getRegionCompletion(temp.getLocation(), Tile.Region.Center)) {
			scoreFeature(9, c.getMeepleList(temp.getLocation(),
					Tile.Region.Center), temp.current().featureInRegion(
					Tile.Region.Center));
		}
		c.traverseRegion(temp.up(), Tile.Region.Center);
		if (c.getRegionCompletion(temp.getLocation(), Tile.Region.Center)) {
			scoreFeature(9, c.getMeepleList(temp.getLocation(),
					Tile.Region.Center), temp.current().featureInRegion(
					Tile.Region.Center));
		}
		c.traverseRegion(temp.up(), Tile.Region.Center);
		if (c.getRegionCompletion(temp.getLocation(), Tile.Region.Center)) {
			scoreFeature(9, c.getMeepleList(temp.getLocation(),
					Tile.Region.Center), temp.current().featureInRegion(
					Tile.Region.Center));
		}
		c.traverseRegion(temp.right(), Tile.Region.Center);
		if (c.getRegionCompletion(temp.getLocation(), Tile.Region.Center)) {
			scoreFeature(9, c.getMeepleList(temp.getLocation(),
					Tile.Region.Center), temp.current().featureInRegion(
					Tile.Region.Center));
		}
		c.traverseRegion(temp.right(), Tile.Region.Center);
		if (c.getRegionCompletion(temp.getLocation(), Tile.Region.Center)) {
			scoreFeature(9, c.getMeepleList(temp.getLocation(),
					Tile.Region.Center), temp.current().featureInRegion(
					Tile.Region.Center));
		}

		beginTurn();
	}

	private void scoreFeature(Integer regionSize, List<Meeple> regionMeeple,
			TileFeature regionFeatureType) {

		int counts[] = new int[players_.size()];
		int maxCount = 0;
		for (Meeple m : regionMeeple) {
			counts[m.getPlayer()] += 1;
			maxCount = Math.max(maxCount, counts[m.getPlayer()]);
		}

		// only score if someone claimed it
		if (maxCount > 0) {
			for (int ii = 0; ii < players_.size(); ii++) {
				if (counts[ii] == maxCount) {

					// add to their score
					players_.get(ii).shiftScore(regionSize);

				}
			}
		}
	}

	/**
	 * Called when a turn is ended, and may be called again if the
	 * BoardController finds the drawn tile unusable.
	 * 
	 */
	public void beginTurn() {
		// if the current player is playing on this machine, we need to enable
		// the interface so they can place a tile. We do that by passing another
		// notification
		Player p = players_.get(currentPlayer_);

		if (p.getIsLocal() == true) {
			// Draw the another tile!
			Tile t = deck_.popRandomTile();

			// Send notification that we've modified the deck
			NotificationManager.getInstance().sendNotification(
					Notification.DECK_SET, deck_);

			// Send notifications to attach our tileInHand to the view
			NotificationManager.getInstance().sendNotification(
					Notification.TILE_IN_HAND_CHANGED, t);

			NotificationManager.getInstance().sendNotification(
					Notification.BEGIN_TURN, p);
		}
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

	public void toggleInstructions() {
		if (instructions_.isShowing()) {
			DisplayHelper.getInstance().remove(instructions_);
		} else
			DisplayHelper.getInstance()
					.add(instructions_, DisplayHelper.Layer.MODAL,
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

	public void loadGame(Notification n) {
		JPopUp p = new JPopUp("", "Select your saved game file...");
		File f = p.openFileDialog();
	}

	public void saveGame(Notification n) {
		JPopUp p = new JPopUp("", "Select a location to save your game...");
		File f = p.saveFileDialog();
	}

	private void loadPlayerData() {
		players_ = new ArrayList<Player>();

		// TODO: player colors should be passed in from the
		// InputPlayerDataPanel.
		// In the future, maybe this entire function should be in the panel, and
		// then the gameController can just get a list of player objects?
		int playerCount = 0;

		for (String s : playerData_.getPlayerNames()) {
			if (s.length() > 0) {
				Player player = new Player(s);
				player.setMeepleColor(playerData_.getPlayerColors().get(
						playerCount));
				players_.add(player);
				playerCount++;
			} else if (playerCount < 2) {
				Player player = new Player();
				player.setMeepleColor(playerData_.getPlayerColors().get(
						playerCount));
				players_.add(player);
				playerCount++;
			}
		}
	}

	public void playerDataReset(Notification n) {
		players_.clear();
	}

}
