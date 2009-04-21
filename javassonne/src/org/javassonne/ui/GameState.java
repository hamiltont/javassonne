/**
 * Javassonne 
 *  http://code.google.com/p/javassonne/
 * 
 * @author [Add Name Here]
 * @date Apr 1, 2009
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
import org.javassonne.model.Meeple;
import org.javassonne.model.Player;
import org.javassonne.model.Tile;
import org.javassonne.model.TileBoard;
import org.javassonne.model.TileDeck;

public class GameState {

	private static final long serialVersionUID = 1L;
	private static GameState instance_ = null;

	private ArrayList<Player> players_;
	private int currentPlayer_;
	private TileDeck deck_;
	private Boolean gameInProgress_ = false;
	private Tile tileInHand_;
	private TileBoard board_;
	private List<Meeple> globalMeepleSet_ = new ArrayList<Meeple>();
	private Mode mode_;

	// TODO - Make GameState listeners for appropriate Notifications, so that
	// the Mode is automatically updated
	public static enum Mode {
		PLAYING_LOCAL_GAME("Playing Local Game"), PLAYING_NW_GAME(
				"Playing Network Game"), IDLE("Idle"), IN_LOBBY("In the lobby"), WAITING(
				"Waiting for players");
		// Paused game, or currently playing the game but stepped out
		public final String text;

		Mode(String s) {
			text = s;
		}
	}

	// Singelton implementation
	// --------------------------------------------------------
	protected GameState() {
		mode_ = Mode.IN_LOBBY;
		
		// listen for the notifications we send. THis may seem backwards,
		// but we want to receive them if they come from the network so
		// we can update our data respectively.
		NotificationManager n = NotificationManager.getInstance();
		n.addObserver(Notification.UPDATED_BOARD, this, "updatedBoard");
		n.addObserver(Notification.UPDATED_CURRENT_PLAYER, this, "updatedCurrentPlayer");
		n.addObserver(Notification.UPDATED_DECK, this, "updatedDeck");
		n.addObserver(Notification.UPDATED_GAME_IN_PROGRESS, this, "updatedGameInProgress");
		n.addObserver(Notification.UPDATED_TILE_IN_HAND, this, "updatedTileInHand");
		n.addObserver(Notification.UPDATED_PLAYERS, this, "updatedPlayers");
		n.addObserver(Notification.UPDATED_GLOBAL_MEEPLE_SET, this, "updatedGlobalMeepleSet");
	}

	// Provide access to singleton
	public static GameState getInstance() {
		if (instance_ == null) {
			instance_ = new GameState();
		}
		return instance_;
	}

	// Network Receive Functions
	// --------------------------------------------------------

	public void updatedBoard(Notification n){
		if (n.receivedFromHost() == true){
			board_ = (TileBoard)n.argument();		
		}
	}
	public void updatedCurrentPlayer(Notification n){
		if (n.receivedFromHost() == true){
			currentPlayer_ = (Integer)n.argument();
		}
	}
	public void updatedDeck(Notification n){
		if (n.receivedFromHost() == true){
			deck_ = (TileDeck)n.argument();		
		}
	}
	public void updatedGameInProgress(Notification n){
		if (n.receivedFromHost() == true){
			gameInProgress_ = (Boolean)n.argument();		
		}
	}
	public void updatedTileInHand(Notification n){
		if (n.receivedFromHost() == true){
			tileInHand_ = (Tile)n.argument();		
		}
	}
	public void updatedPlayers(Notification n){
		if (n.receivedFromHost() == true){
			players_ = (ArrayList<Player>)n.argument();		
		}
	}
    public void updatedGlobalMeepleSet(Notification n){
		if (n.receivedFromHost() == true){
			globalMeepleSet_ = (List<Meeple>)n.argument();		
		}
	}
	// Convenience Functions
	// --------------------------------------------------------

	public void startGameWithPlayers(ArrayList<Player> players) {
		// Update our mode
		setMode(Mode.PLAYING_LOCAL_GAME);

		players_ = players;
		currentPlayer_ = 0;
		gameInProgress_ = true;

	}

	public void resetGameState() {
		// Update our mode
		setMode(Mode.IN_LOBBY);

		board_ = null;
		deck_ = null;
		tileInHand_ = null;
		gameInProgress_ = false;
		currentPlayer_ = 0;
		players_ = null;
	}

	public void advanceCurrentPlayer() {
		currentPlayer_ = (currentPlayer_ + 1) % players_.size();

		NotificationManager.getInstance().sendNotification(
				Notification.UPDATED_CURRENT_PLAYER, currentPlayer_);
	}

	// Getters and Setter Functions
	// --------------------------------------------------------

	public Player getCurrentPlayer() {
		return players_.get(currentPlayer_);
	}

	public int getCurrentPlayerIndex() {
		return currentPlayer_;
	}

	public void setCurrentPlayer(int i) {
		currentPlayer_ = i;

		NotificationManager.getInstance().sendNotification(
				Notification.UPDATED_CURRENT_PLAYER, currentPlayer_);
	}

	public boolean getGameInProgress() {
		return gameInProgress_;
	}

	public void setGameInProgress(boolean b) {
		gameInProgress_ = b;

		if (b)
			setMode(Mode.PLAYING_LOCAL_GAME);
		else
			setMode(Mode.IN_LOBBY);

		NotificationManager.getInstance().sendNotification(
				Notification.UPDATED_GAME_IN_PROGRESS, gameInProgress_);
	}

	public TileDeck getDeck() {
		return deck_;
	}

	public void setDeck(TileDeck deck) {
		deck_ = deck;

		NotificationManager.getInstance().sendNotification(
				Notification.UPDATED_DECK, deck);
	}

	public TileBoard getBoard() {
		return board_;
	}

	public void setBoard(TileBoard board) {
		board_ = board;

		NotificationManager.getInstance().sendNotification(
				Notification.UPDATED_BOARD, board);
	}

	public List<Meeple> globalMeepleSet() {
		return globalMeepleSet_;
	}

	public void setGlobalMeepleSet(List<Meeple> list) {
		globalMeepleSet_ = list;
        
		NotificationManager.getInstance().sendNotification(
				Notification.UPDATED_GLOBAL_MEEPLE_SET, globalMeepleSet_);
	}

	public void addMeepleToGlobalMeepleSet(Meeple meeple) {
		globalMeepleSet_.add(meeple);

		NotificationManager.getInstance().sendNotification(
				Notification.UPDATED_GLOBAL_MEEPLE_SET, globalMeepleSet_);
	}

	public void removeMeepleFromGlobalMeepleSet(Meeple meeple) {
		globalMeepleSet_.remove(meeple);
        
		NotificationManager.getInstance().sendNotification(
				Notification.UPDATED_GLOBAL_MEEPLE_SET, globalMeepleSet_);
	}

	public ArrayList<Player> getPlayers() {
		return players_;
	}

	public void setPlayers(ArrayList<Player> players) {
		players_ = players;

		NotificationManager.getInstance().sendNotification(
				Notification.UPDATED_PLAYERS, players_);
	}

	public Tile getTileInHand() {
		return tileInHand_;
	}

	public void setTileInHand(Tile t) {
		tileInHand_ = t;

		NotificationManager.getInstance().sendNotification(
				Notification.UPDATED_TILE_IN_HAND, t);
	}

	public Mode getMode() {
		return mode_;
	}

	public void setMode(Mode m) {
		if (m != mode_)
			NotificationManager.getInstance().sendNotification(
					Notification.GAME_MODE_CHANGED);
		mode_ = m;
	}

}
