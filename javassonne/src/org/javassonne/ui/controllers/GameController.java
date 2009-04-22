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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.javassonne.algorithms.RegionsCalc;
import org.javassonne.logger.LogSender;
import org.javassonne.messaging.Notification;
import org.javassonne.messaging.NotificationManager;
import org.javassonne.model.Meeple;
import org.javassonne.model.Player;
import org.javassonne.model.Tile;
import org.javassonne.model.TileBoard;
import org.javassonne.model.TileBoardGenIterator;
import org.javassonne.model.TileBoardIterator;
import org.javassonne.model.TileDeck;
import org.javassonne.model.TileMapBoard;
import org.javassonne.model.TileSerializer;
import org.javassonne.model.TileSet;
import org.javassonne.networking.LocalHost;
import org.javassonne.networking.impl.JmDNSSingleton;
import org.javassonne.networking.impl.RemotingUtils;
import org.javassonne.networking.impl.ThreadPool;
import org.javassonne.ui.DisplayHelper;
import org.javassonne.ui.GameState;
import org.javassonne.ui.GameState.Mode;
import org.javassonne.ui.controls.JPopUp;
import org.javassonne.ui.map.MeepleSprite;
import org.javassonne.ui.panels.InputPlayerDataPanel;
import org.javassonne.ui.panels.InstructionsPanel;
import org.javassonne.ui.panels.MenuPanel;
import org.javassonne.ui.panels.ViewNetworkHostsPanel;

import com.thoughtworks.xstream.XStream;

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

	private InstructionsPanel instructions_;
	private MenuPanel menu_;

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
		n.addObserver(Notification.SCORE_TURN, this, "scoreTurn");
		n.addObserver(Notification.QUIT, this, "quitGame");
		n.addObserver(Notification.TOGGLE_MAIN_MENU, this, "toggleMainMenu");
		n.addObserver(Notification.TILE_UNUSABLE, this, "beginTurn");
		n.addObserver(Notification.BEGIN_TURN, this, "beginTurn");
		n.addObserver(Notification.TOGGLE_INSTRUCTIONS, this,
				"toggleInstructions");
		n.addObserver(Notification.START_NETWORK_GAME, this,
				"startGameAsNetworkClient");
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
		ArrayList<Player> players_ = (ArrayList<Player>) n.argument();
		GameState.getInstance().startGameWithPlayers(players_);

		NotificationManager.getInstance().sendNotification(
				Notification.TOGGLE_MAIN_MENU);

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
		// Uncomment to Test the Game Over functionality
		// while (deck.tilesRemaining() > 10)
		// deck.popRandomTile();

		GameState.getInstance().setDeck(deck);

		TileBoard board = new TileMapBoard();
		GameState.getInstance().setBoard(board);
		GameState.getInstance().setMode(GameState.Mode.PLAYING_LOCAL_GAME);

		// Create a BoardController to do the heavy lifting during gameplay.
		// These two objects handle notifications from the UI (like rotate
		// tile).
		boardController_ = new BoardController();
		hudController_ = new HUDController();

		// See if the first person is playing on this computer. If they are,
		// send the begin turn notification to activate the interface for
		// them.
		beginTurn();
	}

	public void startGameAsNetworkClient(Notification n) {
		if (GameState.getInstance().getMode() != Mode.IN_LOBBY)
			return;

		HashMap<String, Object> gameData = (HashMap<String, Object>) n
				.argument();

		GameState.getInstance().setDeck((TileDeck) gameData.get("deck"));

		TileBoard board = (TileBoard) gameData.get("board");
		board.removeTemps();
		GameState.getInstance().setBoard(board);

		ArrayList<Player> players = (ArrayList<Player>) gameData.get("players");

		// close the main menu
		menu_.close();

		// start the game!
		GameState.getInstance().setMode(Mode.PLAYING_NW_GAME);
		GameState.getInstance().startGameWithPlayers(players);

		boardController_ = new BoardController();
		hudController_ = new HUDController();

		beginTurn();
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
		Player p = GameState.getInstance().getCurrentPlayer();

		if (p.getIsLocal() == true) {
			// Draw the another tile!
			TileDeck d = GameState.getInstance().getDeck();
			Tile t = d.popRandomTile();
			GameState.getInstance().setDeck(d);

			// Send notifications to attach our tileInHand to the view
			GameState.getInstance().setTileInHand(t);
		}

		// Toggle an update of the score board
		NotificationManager.getInstance().sendNotification(
				Notification.SCORE_UPDATE);
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
		if (GameState.getInstance().getMode() != Mode.PLAYING_NW_GAME) {
			String[] options = { RETURN_TO_GAME, END_WITHOUT_SAVING };

			JPopUp dialogBox = new JPopUp("A game is currently in progress!");
			String ans = dialogBox.promptUser(options);

			if (ans == END_WITHOUT_SAVING) {
				GameState.getInstance().resetGameState();
				NotificationManager.getInstance().sendNotification(
						Notification.END_GAME);
			}
		} else {
			String[] options = { "Yes, Exit", "Back to Game" };

			JPopUp dialogBox = new JPopUp(
					"A game is currently in progress! Are you sure?");
			String ans = dialogBox.promptUser(options);

			if (ans == "Yes, Exit") {
				GameState.getInstance().resetGameState();
				NotificationManager.getInstance().sendNotification(
						Notification.END_GAME);
			}

		}
	}

	public void scoreTurn(Notification n) {
		HashMap<String, Object> data = (HashMap<String, Object>) n.argument();
		Point p = (Point) data.get("location");

		TileBoardIterator iter = new TileBoardGenIterator(GameState
				.getInstance().getBoard(), p);

		Set<Meeple> meeple = new HashSet<Meeple>();

		// Score completed features on this tile
		RegionsCalc c = new RegionsCalc();
		for (Tile.Region r : Tile.Region.values()) {
			c.traverseRegion(iter, r);
		}
		for (Tile.Region r : Tile.Region.values()) {
			if (c.getRegionCompletion(iter.getLocation(), r)) {
				if (!meeple.containsAll(c.getMeepleList(p, r))) {
					scoreFeature(c.getScoreOfRegion(p, r), c
							.getMeepleList(p, r));
					meeple.addAll(c.getMeepleList(p, r));
				}
			}
		}

		// Score cloisters - go right then move clockwise
		// Note: do not need to recheck iter because it was checked above
		TileBoardGenIterator temp = new TileBoardGenIterator(iter);
		c.traverseRegion(temp.right(), Tile.Region.Center);
		// TODO: Change these to get the correct score (algorithm should do it)
		if (c.getRegionCompletion(temp.getLocation(), Tile.Region.Center)) {
			scoreFeature(c.getScoreOfRegion(temp.getLocation(),
					Tile.Region.Center), c.getMeepleList(temp.getLocation(),
					Tile.Region.Center));
		}
		c.traverseRegion(temp.down(), Tile.Region.Center);
		if (c.getRegionCompletion(temp.getLocation(), Tile.Region.Center)) {
			scoreFeature(c.getScoreOfRegion(temp.getLocation(),
					Tile.Region.Center), c.getMeepleList(temp.getLocation(),
					Tile.Region.Center));
		}
		c.traverseRegion(temp.left(), Tile.Region.Center);
		if (c.getRegionCompletion(temp.getLocation(), Tile.Region.Center)) {
			scoreFeature(c.getScoreOfRegion(temp.getLocation(),Tile.Region.Center), c.getMeepleList(temp.getLocation(),
					Tile.Region.Center));
		}
		c.traverseRegion(temp.left(), Tile.Region.Center);
		if (c.getRegionCompletion(temp.getLocation(), Tile.Region.Center)) {
			scoreFeature(c.getScoreOfRegion(temp.getLocation(),Tile.Region.Center), c.getMeepleList(temp.getLocation(),
					Tile.Region.Center));
		}
		c.traverseRegion(temp.up(), Tile.Region.Center);
		if (c.getRegionCompletion(temp.getLocation(), Tile.Region.Center)) {
			scoreFeature(c.getScoreOfRegion(temp.getLocation(),Tile.Region.Center), c.getMeepleList(temp.getLocation(),
					Tile.Region.Center));
		}
		c.traverseRegion(temp.up(), Tile.Region.Center);
		if (c.getRegionCompletion(temp.getLocation(), Tile.Region.Center)) {
			scoreFeature(c.getScoreOfRegion(temp.getLocation(),Tile.Region.Center), c.getMeepleList(temp.getLocation(),
					Tile.Region.Center));
		}
		c.traverseRegion(temp.right(), Tile.Region.Center);
		if (c.getRegionCompletion(temp.getLocation(), Tile.Region.Center)) {
			scoreFeature(c.getScoreOfRegion(temp.getLocation(),Tile.Region.Center), c.getMeepleList(temp.getLocation(),
					Tile.Region.Center));
		}
		c.traverseRegion(temp.right(), Tile.Region.Center);
		if (c.getRegionCompletion(temp.getLocation(), Tile.Region.Center)) {
			scoreFeature(c.getScoreOfRegion(temp.getLocation(),Tile.Region.Center), c.getMeepleList(temp.getLocation(),
					Tile.Region.Center));
		}

		if (GameState.getInstance().getDeck().tilesRemaining() == 0)
			NotificationManager.getInstance().sendNotification(
					Notification.GAME_OVER);
		else {
			GameState.getInstance().advanceCurrentPlayer();
			beginTurn();
		}
	}

	private void scoreFeature(Integer regionSize, List<Meeple> regionMeeple) {

		ArrayList<Player> players_ = GameState.getInstance().getPlayers();

		int counts[] = new int[players_.size()];
		int maxCount = 0;
		for (Meeple m : regionMeeple) {
			counts[m.getPlayer()] += 1;
			maxCount = Math.max(maxCount, counts[m.getPlayer()]);

			// Give meeple back
			players_.get(m.getPlayer()).shiftMeepleRemaining(1);

			// Remove the meeple from the map by deleting the "group" of
			// sprites that the meeple is attached to.
			NotificationManager.getInstance().sendNotification(
					Notification.MAP_REMOVE_SPRITE_GROUP, m);

			GameState.getInstance().removeMeepleFromGlobalMeepleSet(m);
			m.getParentTile().setMeeple(null);
			m.setParentTile(null);
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

	public void toggleMainMenu(Notification n) {
		// Determine whether the game is currently in progress

		if (menu_.isShowing()) {
			if (GameState.getInstance().getGameInProgress())
				menu_.close();

		} else {
			menu_
					.setGameInProgress(GameState.getInstance()
							.getGameInProgress());

			// Make sure the instructions menu is hidden
			NotificationManager.getInstance().sendNotification(
					Notification.TOGGLE_INSTRUCTIONS, 0);

			DisplayHelper.getInstance().add(menu_, DisplayHelper.Layer.MODAL,
					DisplayHelper.Positioning.CENTER);
		}
	}

	/*
	 * Notification n: optional Integer 0: hide, 1:show
	 */
	public void toggleInstructions(Notification n) {
		if (instructions_.isShowing()
				|| (n.argument() != null && ((Integer) n.argument()).equals(0))) {
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

		DisplayHelper.getInstance().removeAll();
		ThreadPool.shutdown();

		Thread t = new Thread(new Runnable() {
			public void run() {
				JmDNSSingleton.getJmDNS().close();
			}
		}, "JmDNS Reaper");
		t.start();

		try {
			RemotingUtils.shutdownService(LocalHost.getName());
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
		// Exit
		System.exit(0);
	}

	public void loadGame(Notification n) {
		JPopUp p = new JPopUp("", "Select your saved game file...");
		File f = p.openFileDialog();

		// if the user does not select a file, cancel
		if (f == null)
			return;

		// read the file into a string
		StringBuilder contents = new StringBuilder();
		try {
			BufferedReader input = new BufferedReader(new FileReader(f));
			try {
				String line = null;
				while ((line = input.readLine()) != null) {
					contents.append(line);
				}
			} finally {
				input.close();
			}
		} catch (Exception e) {

		}

		// close the main menu
		menu_.close();

		// unserialize the string
		XStream x = new XStream();
		x.omitField(Tile.class, "image_");
		HashMap<String, Object> map = (HashMap<String, Object>) x
				.fromXML(contents.toString());

		// Set gameState properties
		GameState state = GameState.getInstance();
		TileDeck deck = (TileDeck) map.get("deck");
		TileBoard board = (TileBoard) map.get("board");

		ArrayList<Player> players = (ArrayList<Player>) map.get("players");

		state.setPlayers(players);
		state.setCurrentPlayer((Integer) map.get("currentPlayer"));
		state.setTileInHand((Tile) map.get("tileInHand"));
		state.setGlobalMeepleSet((List<Meeple>) map.get("meeple"));
		state.setGameInProgress(true);
		state.setBoard(board);
		state.setDeck(deck);

		// Create a BoardController to do the heavy lifting during gameplay.
		// These two objects handle notifications from the UI (like rotate
		// tile).
		boardController_ = new BoardController();
		hudController_ = new HUDController();

		// re-attach meeple sprites on the map where they belong
		for (Meeple s : state.globalMeepleSet()) {
			MeepleSprite sprite = new MeepleSprite(s, players
					.get(s.getPlayer()).getMeepleColor());
			NotificationManager.getInstance().sendNotification(
					Notification.MAP_ADD_SPRITE, sprite);
		}

		// See if the first person is playing on this computer. If they are,
		// send the begin turn notification to activate the interface for
		// them.
		beginTurn();
	}

	public void saveGame(Notification n) {
		if (GameState.getInstance().getMode() == Mode.PLAYING_NW_GAME) {
			String[] options = { "OK" };
			JPopUp dialogBox = new JPopUp(
					"Sorry, you cannot save a network game...");
			dialogBox.promptUser(options);
		}

		JPopUp p = new JPopUp("", "Select a location to save your game...");
		File f = p.saveFileDialog();

		// make sure we have the file extension
		if (!f.getName().endsWith(".javassonne"))
			f = new File(f.getAbsolutePath() + ".javassonne");

		// serialize shit!
		GameState state = GameState.getInstance();
		HashMap<String, Object> map = new HashMap<String, Object>();

		map.put("board", state.getBoard());
		map.put("deck", state.getDeck());
		map.put("players", state.getPlayers());
		map.put("tileInHand", state.getTileInHand());
		map.put("currentPlayer", state.getCurrentPlayerIndex());
		map.put("meeple", state.globalMeepleSet());

		XStream x = new XStream();
		x.omitField(Tile.class, "image_");

		FileWriter fw;
		try {
			fw = new FileWriter(f);
			x.toXML(map, fw);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
