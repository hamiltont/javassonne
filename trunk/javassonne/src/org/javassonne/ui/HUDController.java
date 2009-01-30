/**
 * Javassonne 
 *  http://code.google.com/p/javassonne/
 * 
 * @author [Add Name Here]
 * @date Jan 30, 2009
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

import org.javassonne.messaging.Notification;
import org.javassonne.messaging.NotificationManager;
import org.javassonne.model.Tile;
import org.javassonne.model.TileBoard;
import org.javassonne.model.TileDeck;

/**
 * The HUDController manages interaction between the model and the HUD user
 * interface. For instance, when the user draws and rotates their tile, those
 * notifications should be handled here.
 * 
 * @author bengotow
 * 
 */
public class HUDController {

	TileDeck deck_;
	TileBoard board_;
	Tile tileInHand_;

	/**
	 * The HUDController is created from the GameController when a new game is
	 * started. The GameController passes the model objects so we can manipulate
	 * them in response to changes in the view.
	 *
	 * @param d
	 *            The TileDeck. This will never be changed once the game has
	 *            begun.
	 * @param b
	 *            The TileBoard. This will never be changed once the game has
	 *            begun.
	 */
	public HUDController(TileDeck d, TileBoard b) {
		deck_ = d;
		board_ = b;

		// Draw the first tile!
		tileInHand_ = deck_.popRandomTile();

		// Send notifications to attach our tileInHand to the view
		NotificationManager.getInstance().sendNotification(
				Notification.TILE_IN_HAND_CHANGED, tileInHand_);

		// register to receive events from the HUD views
		NotificationManager.getInstance().addObserver(
				Notification.TILE_ROTATE_LEFT, this, "rotateTileInHandLeft");
		NotificationManager.getInstance().addObserver(
				Notification.TILE_ROTATE_RIGHT, this, "rotateTileInHandRight");
	}

	/**
	 * This function is called when a TILE_ROTATE_LEFT notification is received.
	 * We want to rotate the tile, and then send a notification back letting the
	 * views know that they should redraw the tile onscreen.
	 */
	public void rotateTileInHandLeft() {
		tileInHand_.rotateLeft();
		NotificationManager.getInstance().sendNotification(
				Notification.TILE_IN_HAND_CHANGED, tileInHand_);
	}

	/**
	 * This function is called when a TILE_ROTATE_RIGHT notification is
	 * received. We want to rotate the tile, and then send a notification back
	 * letting the views know that they should redraw the tile onscreen.
	 */
	public void rotateTileInHandRight() {
		tileInHand_.rotateRight();
		NotificationManager.getInstance().sendNotification(
				Notification.TILE_IN_HAND_CHANGED, tileInHand_);
	}
}
