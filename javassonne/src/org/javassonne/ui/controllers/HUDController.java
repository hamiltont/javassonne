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

package org.javassonne.ui.controllers;

import java.awt.Point;
import java.util.Properties;

import org.javassonne.messaging.Notification;
import org.javassonne.messaging.NotificationManager;
import org.javassonne.model.Tile;
import org.javassonne.ui.DisplayHelper;
import org.javassonne.ui.GameState;
import org.javassonne.ui.panels.GameOverPanel;
import org.javassonne.ui.panels.HUDButtonsPanel;
import org.javassonne.ui.panels.HUDGameStatsPanel;
import org.javassonne.ui.panels.HUDPanel;
import org.javassonne.ui.panels.HUDShowInstructionsPanel;
import org.javassonne.ui.panels.RemainingTilesPanel;

/**
 * The HUDController manages interaction between the model and the HUD user
 * interface. For instance, when the user draws and rotates their tile, those
 * notifications should be handled here.
 * 
 * @author bengotow
 * 
 */
public class HUDController {

	RemainingTilesPanel hudRemainingTiles_;
	HUDPanel hudPanel_;
	HUDButtonsPanel hudButtons_;
	HUDGameStatsPanel hudGameStats_;
	HUDShowInstructionsPanel hudInstructions_;

	/**
	 * The HUDController is created from the GameController when a new game is
	 * started. The GameController passes the model objects so we can manipulate
	 * them in response to changes in the view.
	 */
	public HUDController() {

		hudRemainingTiles_ = new RemainingTilesPanel();
		hudButtons_ = new HUDButtonsPanel();
		hudPanel_ = new HUDPanel();
		hudGameStats_ = new HUDGameStatsPanel();
		hudInstructions_ = new HUDShowInstructionsPanel();

		// Attach the remaining tiles panel to the top right
		DisplayHelper.getInstance().add(hudRemainingTiles_,
				DisplayHelper.Layer.PALETTE,
				DisplayHelper.Positioning.TOP_RIGHT);

		// Attach the buttons (Menu, zoom in ,zoom out) to the top left
		DisplayHelper.getInstance().add(hudButtons_,
				DisplayHelper.Layer.PALETTE, new Point(10, 10));

		// Attach the instructions pane
		DisplayHelper.getInstance().add(hudInstructions_,
				DisplayHelper.Layer.PALETTE,
				DisplayHelper.Positioning.TOP_CENTER);

		// Attach the tile drawing panel to the top left
		DisplayHelper.getInstance().add(hudPanel_, DisplayHelper.Layer.PALETTE,
				new Point(10, 40));

		// Attach the stats panel to the bottom left
		DisplayHelper.getInstance().add(hudGameStats_,
				DisplayHelper.Layer.PALETTE,
				DisplayHelper.Positioning.BOTTOM_LEFT);

		// register to receive events from the HUD views
		NotificationManager.getInstance().addObserver(
				Notification.TILE_ROTATE_LEFT, this, "rotateTileInHandLeft");
		NotificationManager.getInstance().addObserver(
				Notification.TILE_ROTATE_RIGHT, this, "rotateTileInHandRight");
		NotificationManager.getInstance().addObserver(Notification.END_GAME,
				this, "endGame");
		NotificationManager.getInstance().addObserver(
				Notification.END_TURN, this, "gameOver");
	}

	public void endGame(Notification n) {
		// unregister ourselves so we no longer get notifications. A new
		// HUDController will be created if a new game is started!
		NotificationManager.getInstance().removeObserver(this);

		// the panels all respond to this notification, and they remove
		// themselves from the view.
		hudRemainingTiles_ = null;
		hudButtons_ = null;
		hudPanel_ = null;
		hudGameStats_ = null;
	}

	/**
	 * This function is called when a TILE_ROTATE_LEFT notification is received.
	 * We want to rotate the tile, and then send a notification back letting the
	 * views know that they should redraw the tile onscreen.
	 */
	public void rotateTileInHandLeft() {
		Tile t = GameState.getInstance().getTileInHand();
		if (t != null) {
			t.rotateLeft();
			GameState.getInstance().setTileInHand(t);
		}
	}

	/**
	 * This function is called when a TILE_ROTATE_RIGHT notification is
	 * received. We want to rotate the tile, and then send a notification back
	 * letting the views know that they should redraw the tile onscreen.
	 */
	public void rotateTileInHandRight() {
		Tile t = GameState.getInstance().getTileInHand();
		if (t != null) {
			t.rotateRight();
			GameState.getInstance().setTileInHand(t);
		}
	}

	/*
	 * This method monitors the status of the game to determine if it's over.
	 * The game has been completed! We must now finish scoring and end the game
	 */
	public void gameOver(Notification n) {

		// Game Over Conditions: Tile End is Empty
		if (GameState.getInstance().getDeck().tilesRemaining() != 0)
			return;

		System.out.print("#");
		// TODO: Finish scoring
		GameOverPanel g = new GameOverPanel();

		Properties config = new Properties();
		config.setProperty("hideMainMenu", "true");
		NotificationManager.getInstance().sendNotification(Notification.END_GAME, config);

		DisplayHelper.getInstance().add(g, DisplayHelper.Layer.MODAL,
				DisplayHelper.Positioning.CENTER);
	}
}
