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

package org.javassonne.ui.controllers;

import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;

import org.javassonne.algorithms.QuadCalc;
import org.javassonne.algorithms.RegionsCalc;
import org.javassonne.messaging.Notification;
import org.javassonne.messaging.NotificationManager;
import org.javassonne.model.BoardPositionFilledException;
import org.javassonne.model.Meeple;
import org.javassonne.model.NotValidPlacementException;
import org.javassonne.model.Tile;
import org.javassonne.model.TileBoard;
import org.javassonne.model.TileBoardGenIterator;
import org.javassonne.model.TileBoardIterator;
import org.javassonne.model.Player.MeepleColor;
import org.javassonne.ui.DisplayHelper;
import org.javassonne.ui.GameState;
import org.javassonne.ui.map.MeepleSprite;
import org.javassonne.ui.map.TilePlacementSprite;
import org.javassonne.ui.panels.HUDConfirmPlacementPanel;

public class BoardController {

	private static final String tempTileImagesFolder_ = "images";
	private static final String litTileIdentifier_ = "background_tile_highlighted.jpg";

	Tile tempLitTile_;
	Tile tempPlacedTile_;
	MeepleSprite tempPlacedMeeple_;

	TilePlacementSprite tempPlacementSprite_;
	TileBoardGenIterator tempLocationIter_;

	List<Tile.Region> currentRegionOptions_;
	List<Tile.Quadrant> currentQuadrantOptions_;

	/**
	 * The BoardController will handle interaction between the board model and
	 * board views in the interface. For instance, clicking the board, placing
	 * meeple, zooming in and out will be handled here.
	 * 
	 * @param b
	 *            The TileBoard. This will never be changed once the game has
	 *            begun.
	 * @param players_
	 */
	public BoardController() {

		tempLitTile_ = new Tile();
		try {
			tempLitTile_.setImage(ImageIO.read(new File(String.format("%s/%s",
					tempTileImagesFolder_, litTileIdentifier_))));
		} catch (IOException ex) {
			// TODO: Fix this
			ex.printStackTrace();
		}

		NotificationManager n = NotificationManager.getInstance();
		n.addObserver(Notification.PLACE_TILE, this, "placeTile");
		n.addObserver(Notification.MEEPLE_FARMER_DRAG_STARTED, this,
				"dragFarmer");
		n.addObserver(Notification.PLACE_FARMER_MEEPLE, this, "placeFarmer");
		n.addObserver(Notification.MEEPLE_VILLAGER_DRAG_STARTED, this,
				"dragVillager");
		n
				.addObserver(Notification.PLACE_VILLAGER_MEEPLE, this,
						"placeVillager");
		n.addObserver(Notification.UNDO_PLACE_TILE, this, "undoPlaceTile");
		n.addObserver(Notification.END_GAME, this, "endGame");
		n.addObserver(Notification.END_TURN, this, "endTurn");
		n.addObserver(Notification.UPDATED_TILE_IN_HAND, this,
				"updateTileInHand");
	}

	public void endGame(Notification n) {
		// Unsubscribe from notifications once the game has ended
		NotificationManager.getInstance().removeObserver(this);
	}

	public void endTurn(Notification n) {
		// update the tile's status so that it is now permanent
		try {
			GameState.getInstance().getBoard().removeTempStatus(
					tempLocationIter_);
		} catch (NotValidPlacementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Meeple placed = tempLocationIter_.current().getMeeple();
		GameState.getInstance().globalMeepleSet().add(placed);

		// remove the placement sprite if it exists
		if (tempPlacementSprite_ != null)
			NotificationManager.getInstance().sendNotification(
					Notification.MAP_REMOVE_SPRITE, tempPlacementSprite_);
		
			NotificationManager.getInstance().sendNotification(
					Notification.SCORE_TURN, tempLocationIter_);
		
		GameState.getInstance().advanceCurrentPlayer();

		tempPlacedMeeple_ = null;
		tempPlacementSprite_ = null;
		tempPlacedTile_ = null;
		tempLocationIter_ = null;
	}

	public void placeTile(Notification n) {
		Tile tileInHand = GameState.getInstance().getTileInHand();
		TileBoard board = GameState.getInstance().getBoard();

		if (tileInHand != null) {
			Point here = (Point) (n.argument());
			tempLocationIter_ = new TileBoardGenIterator(board, here);

			try {
				if (board.isValidPlacement(tempLocationIter_, tileInHand)) {

					// remove the tile from our hand
					tempPlacedTile_ = tileInHand;
					GameState.getInstance().setTileInHand(null);

					// add the tile to the board
					board.removeTemps();
					board.addTemp(tempLocationIter_, tempPlacedTile_);
					GameState.getInstance().setBoard(board);

					// show the confirm placement panel
					MeepleColor c = GameState.getInstance().getCurrentPlayer()
							.getMeepleColor();
					HUDConfirmPlacementPanel confirmPanel = new HUDConfirmPlacementPanel(
							c);
					DisplayHelper.getInstance().add(confirmPanel,
							DisplayHelper.Layer.PALETTE,
							DisplayHelper.Positioning.TOP_CENTER);
					confirmPanel.attachMeeplePanels();

					// highlight the tile on the map and show placement options
					tempPlacementSprite_ = new TilePlacementSprite(here);

					// determine what regions of the tile are valid placements
					RegionsCalc r = new RegionsCalc();
					currentRegionOptions_ = new ArrayList<Tile.Region>();

					for (Tile.Region region : Tile.Region.values()) {
						r.traverseRegion(tempLocationIter_, region);
						List<Meeple> result = r.getMeepleList(tempLocationIter_
								.getLocation(), region);
						if ((result.size() == 0)
								&& (tempPlacedTile_.featureInRegion(region) != null))
							currentRegionOptions_.add(region);
					}

					// determine what quadrants of the tile are valid placements
					QuadCalc q = new QuadCalc();
					currentQuadrantOptions_ = new ArrayList<Tile.Quadrant>();

					for (Tile.Quadrant quad : Tile.Quadrant.values()) {
						q.traverseQuadrant(tempLocationIter_, quad);
						List<Meeple> l = q.getMeepleList(tempLocationIter_
								.getLocation(), quad);
						if (l.size() == 0)
							currentQuadrantOptions_.add(quad);
					}

					// Add the placement indicator to the map
					NotificationManager.getInstance().sendNotification(
							Notification.MAP_ADD_SPRITE, tempPlacementSprite_);
				}

			} catch (BoardPositionFilledException ex) {
				// Bury this exception?
				String err = "PositionFilled" + here.toString() + " is filled";
				NotificationManager.getInstance().sendNotification(
						Notification.LOG_ERROR, err);
				return;
			}
		}
	}

	public void dragVillager(Notification n) {

		// if they've already tried placing a meeple, remove it before
		// allowing them to place another.
		if (tempPlacedMeeple_ != null) {
			resetTempPlacedMeeple();
		}

		tempPlacementSprite_.showRegionOptions(currentRegionOptions_);
		NotificationManager.getInstance().sendNotification(
				Notification.MAP_REDRAW);

	}

	public void placeVillager(Notification n) {

		// the meeple is created in the map layer, because the map layer
		// has more intimate knowledege of which region the drag ended on.
		// (It can convert the pixel to the tile, and then to a region)
		// Region / Quadrant is set. We just set everything else.
		Meeple m = (Meeple) n.argument();

		if ((currentRegionOptions_.contains(m.getRegionOnTile()))
				&& (m.getParentTile() == tempPlacedTile_)) {

			// if they've already tried placing a meeple, remove it before
			// allowing them to place another.
			if (tempPlacedMeeple_ != null) {
				NotificationManager.getInstance().sendNotification(
						Notification.MAP_REMOVE_SPRITE, tempPlacedMeeple_);
			}

			m.setPlayer(GameState.getInstance().getCurrentPlayerIndex());

			// add the meeple to the tile
			tempPlacedTile_.setMeeple(m);

			// add the meeple sprite to the map layer so the guy is visible
			MeepleColor c = GameState.getInstance().getCurrentPlayer()
					.getMeepleColor();
			tempPlacedMeeple_ = new MeepleSprite(m, c);

			// decrement the player's meeple count
			GameState.getInstance().getCurrentPlayer().shiftMeepleRemaining(-1);

			// tell the scoreboard to update based on the new meeple count
			NotificationManager.getInstance().sendNotification(
					Notification.SCORE_UPDATE);

			NotificationManager.getInstance().sendNotification(
					Notification.MAP_ADD_SPRITE, tempPlacedMeeple_);
		}
		NotificationManager.getInstance().sendNotification(
				Notification.DRAG_PANEL_RESET);
	}

	public void dragFarmer(Notification n) {

		// if they've already tried placing a meeple, remove it before
		// allowing them to place another.
		if (tempPlacedMeeple_ != null) {
			resetTempPlacedMeeple();
		}

		tempPlacementSprite_.showQuadrantOptions(currentQuadrantOptions_);
		NotificationManager.getInstance().sendNotification(
				Notification.MAP_REDRAW);

	}

	public void placeFarmer(Notification n) {

		// the meeple is created in the map layer, because the map layer
		// has more intimate knowledege of which region the drag ended on.
		// (It can convert the pixel to the tile, and then to a region)
		// Region / Quadrant is set. We just set everything else.
		Meeple m = (Meeple) n.argument();

		if ((currentQuadrantOptions_.contains(m.getQuadrantOnTile()))
				&& (m.getParentTile() == tempPlacedTile_)) {

			// if they've already tried placing a meeple, remove it before
			// allowing them to place another.
			if (tempPlacedMeeple_ != null) {
				NotificationManager.getInstance().sendNotification(
						Notification.MAP_REMOVE_SPRITE, tempPlacedMeeple_);
			}

			m.setPlayer(GameState.getInstance().getCurrentPlayerIndex());

			// add the meeple to the tile
			tempPlacedTile_.setMeeple(m);

			// add the meeple sprite to the map layer so the guy is visible
			MeepleColor c = GameState.getInstance().getCurrentPlayer()
					.getMeepleColor();
			tempPlacedMeeple_ = new MeepleSprite(m, c);

			// decrement the player's meeple count
			GameState.getInstance().getCurrentPlayer().shiftMeepleRemaining(-1);

			// tell the scoreboard to update based on the new meeple count
			NotificationManager.getInstance().sendNotification(
					Notification.SCORE_UPDATE);

			NotificationManager.getInstance().sendNotification(
					Notification.MAP_ADD_SPRITE, tempPlacedMeeple_);
		}
		NotificationManager.getInstance().sendNotification(
				Notification.DRAG_PANEL_RESET);
	}

	public void undoPlaceTile(Notification n) {
		GameState.getInstance().getBoard().removeTemps();
		GameState.getInstance().setTileInHand(tempPlacedTile_);

		NotificationManager.getInstance().sendNotification(
				Notification.MAP_REMOVE_SPRITE, tempPlacementSprite_);

		if (tempPlacedMeeple_ != null) {
			resetTempPlacedMeeple();
		}

		tempPlacementSprite_ = null;
		tempPlacedTile_ = null;
		tempLocationIter_ = null;
	}

	public void updateTileInHand(Notification n) {
		Boolean shouldPopulateLocations = false;

		TileBoard board = GameState.getInstance().getBoard();
		Tile tileInHand = GameState.getInstance().getTileInHand();

		Boolean isLocal = GameState.getInstance().getCurrentPlayer()
				.getIsLocal();
		shouldPopulateLocations = ((tileInHand != null) && isLocal);

		// Do we need to populate possible locations?
		if (shouldPopulateLocations) {
			try {
				board.removeTemps();
				Set<TileBoardIterator> temp = board
						.possiblePlacements(tileInHand);

				// If there are none, throw out TileInHand
				if (temp.isEmpty()) {
					NotificationManager.getInstance().sendNotification(
							Notification.LOG_WARNING,
							"Tile does not fit on board; drawing new Tile");
					NotificationManager.getInstance().sendNotification(
							Notification.TILE_UNUSABLE);

					// Otherwise, add the possibles
				} else {
					board.addTemps(temp, tempLitTile_);
					GameState.getInstance().setBoard(board);
				}
			} catch (BoardPositionFilledException e) {
				// we really shouldn't get here if possible placements works
				// correctly
				e.printStackTrace();
			}
		}
	}

	private void resetTempPlacedMeeple() {
		NotificationManager.getInstance().sendNotification(
				Notification.MAP_REMOVE_SPRITE, tempPlacedMeeple_);

		tempPlacedTile_.getMeeple().setParentTile(null);
		tempPlacedTile_.setMeeple(null);
		tempPlacedMeeple_ = null;

		// increment the player's meeple count
		GameState.getInstance().getCurrentPlayer().shiftMeepleRemaining(1);

		// tell the scoreboard to update based on the new meeple count
		NotificationManager.getInstance().sendNotification(
				Notification.SCORE_UPDATE);
	}
}
