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

import java.awt.Point;

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
	
	Tile tileInHand_;

	RemainingTilesPanel hudRemainingTiles_;
	HUDPanel hudPanel_;
	HUDButtonsPanel	hudButtons_;
	/**
	 * The HUDController is created from the GameController when a new game is
	 * started. The GameController passes the model objects so we can manipulate
	 * them in response to changes in the view.
	 * 
	 * @param d
	 *            The TileDeck. This will never be changed once the game has
	 *            begun.
	 */
	public HUDController(TileDeck d) {
		deck_ = d;
		
		// Draw the first tile!
		tileInHand_ = deck_.popRandomTile();

		hudRemainingTiles_ = new RemainingTilesPanel();
		hudButtons_ = new HUDButtonsPanel();
		hudPanel_ = new HUDPanel();
		
		// Attach the remaining tiles panel to the top right
		DisplayHelper.getInstance().add(hudRemainingTiles_,
				DisplayHelper.Layer.PALETTE,
				DisplayHelper.Positioning.TOP_RIGHT);

		// Attach the buttons (Menu, zoom in ,zoom out) to the top left
		DisplayHelper.getInstance().add(hudButtons_,
				DisplayHelper.Layer.PALETTE,
				new Point(10,10));

		// Attach the tile drawing panel to the top left
		DisplayHelper.getInstance().add(hudPanel_,
				DisplayHelper.Layer.PALETTE,
				new Point(10,40));

		// Send notification that we've modified the deck
		NotificationManager.getInstance().sendNotification(
				Notification.DECK_CHANGED, deck_);

		// Send notifications to attach our tileInHand to the view
		NotificationManager.getInstance().sendNotification(
				Notification.TILE_IN_HAND_CHANGED, tileInHand_);

		// register to receive events from the HUD views
		NotificationManager.getInstance().addObserver(
				Notification.TILE_ROTATE_LEFT, this, "rotateTileInHandLeft");
		NotificationManager.getInstance().addObserver(
				Notification.TILE_ROTATE_RIGHT, this, "rotateTileInHandRight");
		NotificationManager.getInstance().addObserver(Notification.DRAW_TILE,
				this, "drawTile");
		NotificationManager.getInstance().addObserver(
				Notification.TILE_IN_HAND_CHANGED, this, "updateTileInHand");
		
		NotificationManager.getInstance().addObserver(
				Notification.END_GAME, this, "endGame");
	}

	public void endGame(Notification n)
	{
		// the panels all respond to this notification, and they remove themselves
		// from the view.
		hudRemainingTiles_ = null;
		hudButtons_ = null;
		hudPanel_ = null;
		
		// let go of local variables related to game state. They should not be 
		// used once this notification is received and setting to null allows
		// us to make sure this is followed.
		deck_ = null;
		tileInHand_ = null;
	}
	
	/**
	 * This function is called when a TILE_ROTATE_LEFT notification is received.
	 * We want to rotate the tile, and then send a notification back letting the
	 * views know that they should redraw the tile onscreen.
	 */
	public void rotateTileInHandLeft() {
		if (tileInHand_ != null) {
			tileInHand_.rotateLeft();
			NotificationManager.getInstance().sendNotification(
					Notification.TILE_IN_HAND_CHANGED, tileInHand_);
		}
	}

	/**
	 * This function is called when a TILE_ROTATE_RIGHT notification is
	 * received. We want to rotate the tile, and then send a notification back
	 * letting the views know that they should redraw the tile onscreen.
	 */
	public void rotateTileInHandRight() {
		if (tileInHand_ != null) {
			tileInHand_.rotateRight();
			NotificationManager.getInstance().sendNotification(
					Notification.TILE_IN_HAND_CHANGED, tileInHand_);
		}
	}

	/**
	 * This function is called when a DRAW_TILE notification is received. We
	 * want to draw a tile, update the turn indicator and then send a
	 * notification back letting the views know that they should redraw the tile
	 * onscreen.
	 */
	public void drawTile() {
		// Draw a tile
		tileInHand_ = deck_.popRandomTile();

		// Send notification that we've modified the deck
		NotificationManager.getInstance().sendNotification(
				Notification.DECK_CHANGED, deck_);

		// Send notification that we've changed the tile in hand
		NotificationManager.getInstance().sendNotification(
				Notification.TILE_IN_HAND_CHANGED, tileInHand_);
	}

	public void updateTileInHand(Notification n) {
		tileInHand_ = (Tile) n.argument();

	}
}
