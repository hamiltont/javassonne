/**
 * Javassonne 
 *  http://code.google.com/p/javassonne/
 * 
 * @author David Leinweber
 * @date Jan 14, 2009
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

import java.awt.Point;

import org.javassonne.messaging.Notification;
import org.javassonne.messaging.NotificationManager;
import org.javassonne.model.BoardPositionFilledException;
import org.javassonne.model.NotValidPlacementException;
import org.javassonne.model.Tile;
import org.javassonne.model.TileBoard;
import org.javassonne.model.TileBoardGenIterator;
import org.javassonne.model.TileDeck;

public class BoardController {

	TileDeck deck_;
	TileBoard board_;
	Tile tileInHandRef_;

	/**
	 * The BoardController will handle interaction between the board model and
	 * board views in the interface. For instance, clicking the board, placing
	 * meeple, zooming in and out will be handled here.
	 * 
	 * @param d
	 *            The TileDeck. This will never be changed once the game has
	 *            begun.
	 * @param b
	 *            The TileBoard. This will never be changed once the game has
	 *            begun.
	 */
	public BoardController(TileDeck d, TileBoard b) {
		deck_ = d;
		board_ = b;

		NotificationManager.getInstance().addObserver(
				Notification.CLICK_ADD_TILE, this, "addTile");
		NotificationManager.getInstance().addObserver(
				Notification.TILE_IN_HAND_CHANGED, this, "updateTileInHandRef");

		// Now that we have a board object, we want to update the interface to
		// show the board. Share our board_ object in a notification so the
		// views can get it and display it.
		NotificationManager.getInstance().sendNotification(
				Notification.BOARD_SET, board_);

	}

	public void addTile(Notification n) {
		Point here = (Point) (n.argument());
		TileBoardGenIterator iter = new TileBoardGenIterator(board_, here);
		try {
			board_.addTemp(iter, tileInHandRef_);
		} catch (BoardPositionFilledException ex) {
			// Bury this exception?
		} catch (NotValidPlacementException ex) {
			// Bury this exception?
		}
		// Some kind of confirmation of click?
		board_.removeTempStatus(iter);
		NotificationManager.getInstance().sendNotification(
				Notification.BOARD_SET, board_);

	}

	public void updateTileInHandRef(Notification n) {
		tileInHandRef_ = (Tile) n.argument();
	}

}
