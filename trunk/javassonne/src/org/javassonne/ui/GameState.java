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
import org.javassonne.model.Player;
import org.javassonne.model.Tile;
import org.javassonne.model.TileBoard;
import org.javassonne.model.TileDeck;
import org.javassonne.model.Player.MeepleColor;

public class GameState {

	private static final long serialVersionUID = 1L;
	private static GameState instance_ = null;

	private ArrayList<Player> players_;
	private int currentPlayer_;
	private TileDeck deck_;
	private Boolean gameInProgress_ = false;
	private Tile tileInHand_;
	private TileBoard board_;

	// Singelton implementation
	// --------------------------------------------------------
	protected GameState() {
	}

	// Provide access to singleton
	public static GameState getInstance() {
		if (instance_ == null) {
			instance_ = new GameState();
		}
		return instance_;
	}

	// Convenience Functions
	// --------------------------------------------------------

	public void startGameWithPlayers(ArrayList<Player> players) {
		players_ = players;
		currentPlayer_ = 0;
		gameInProgress_ = true;

	}

	public void resetGameState() {
		board_ = null;
		deck_ = null;
		tileInHand_ = null;
		gameInProgress_ = false;
		currentPlayer_ = 0;
		players_ = null;
	}

	public void advanceCurrentPlayer() {
		currentPlayer_ = (currentPlayer_ + 1) % players_.size();
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
				Notification.UPDATED_CURRENT_PLAYER, getCurrentPlayer());
	}

	public boolean getGameInProgress() {
		return gameInProgress_;
	}

	public void setGameInProgress(boolean b) {
		gameInProgress_ = b;

		NotificationManager.getInstance().sendNotification(
				Notification.UPDATED_GAME_IN_PROGRESS, getCurrentPlayer());
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

	public ArrayList<Player> getPlayers() {
		return players_;
	}

	public void setPlayers(ArrayList<Player> players) {
		players_ = players;
		

		NotificationManager.getInstance().sendNotification(
				Notification.UPDATED_PLAYERS, players);
	}

	public Tile getTileInHand() {
		return tileInHand_;
	}

	public void setTileInHand(Tile t) {
		tileInHand_ = t;

		NotificationManager.getInstance().sendNotification(
				Notification.UPDATED_TILE_IN_HAND, t);
	}

}
