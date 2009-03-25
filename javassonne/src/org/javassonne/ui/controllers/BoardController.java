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

import org.javassonne.algorithms.RegionsCalc;
import org.javassonne.messaging.Notification;
import org.javassonne.messaging.NotificationManager;
import org.javassonne.model.BoardPositionFilledException;
import org.javassonne.model.Meeple;
import org.javassonne.model.NotValidPlacementException;
import org.javassonne.model.Player;
import org.javassonne.model.Tile;
import org.javassonne.model.TileBoard;
import org.javassonne.model.TileBoardGenIterator;
import org.javassonne.model.TileBoardIterator;
import org.javassonne.model.TileFeatureBindings;
import org.javassonne.model.Player.MeepleColor;
import org.javassonne.ui.DisplayHelper;
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

	Tile tileInHand_;
	TileBoard board_;

	List<Tile.Region> currentRegionOptions_;
	List<Tile.Quadrant> currentQuadrantOptions_;
	
	TileFeatureBindings bindings_;
	List<Player> players_;
	int currentPlayer_ = 0;

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
	public BoardController(TileBoard b, TileFeatureBindings bindings,
			List<Player> players) {

		board_ = b;
		bindings_ = bindings;
		players_ = players;

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
		n.addObserver(Notification.PLACE_MEEPLE, this, "placeMeeple");
		n.addObserver(Notification.UNDO_PLACE_TILE, this, "undoPlaceTile");
		n.addObserver(Notification.END_GAME, this, "endGame");
		n.addObserver(Notification.END_TURN, this, "endTurn");
		n.addObserver(Notification.TILE_IN_HAND_CHANGED, this,
				"updateTileInHand");
		n
				.addObserver(Notification.SET_CURRENT_PLAYER, this,
						"setCurrentPlayer");

		// Now that we have a board object, we want to update the interface to
		// show the board. Share our board_ object in a notification so the
		// views can get it and display it.
		NotificationManager.getInstance().sendNotification(
				Notification.BOARD_SET, board_);

	}

	public void setCurrentPlayer(Notification n) {
		currentPlayer_ = (Integer) n.argument();
	}

	public void endGame(Notification n) {
		// Unsubscribe from notifications once the game has ended
		NotificationManager.getInstance().removeObserver(this);

		// let go of the board and the tileInhand. They should not be used
		// once this notification is received and setting to null allows
		// us to make sure this is followed.
		board_ = null;
		tileInHand_ = null;
	}

	public void endTurn(Notification n) {
		// update the tile's status so that it is now permanent
		try {
			board_.removeTempStatus(tempLocationIter_);
		} catch (NotValidPlacementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// remove the placement sprite if it exists
		if (tempPlacementSprite_ != null)
			NotificationManager.getInstance().sendNotification(
					Notification.MAP_REMOVE_SPRITE, tempPlacementSprite_);

		NotificationManager.getInstance().sendNotification(Notification.SCORE_TURN, tempLocationIter_);
		
		tempPlacedMeeple_ = null;
		tempPlacementSprite_ = null;
		tempPlacedTile_ = null;
		tempLocationIter_ = null;
	}

	public void placeTile(Notification n) {
		if (tileInHand_ != null) {
			Point here = (Point) (n.argument());
			tempLocationIter_ = new TileBoardGenIterator(board_, here);

			try {
				if (board_.isValidPlacement(tempLocationIter_, tileInHand_)) {

					// remove the tile from our hand
					tempPlacedTile_ = tileInHand_;
					NotificationManager.getInstance().sendNotification(
							Notification.TILE_IN_HAND_CHANGED, null);

					// add the tile to the board
					board_.removeTemps();
					board_.addTemp(tempLocationIter_, tempPlacedTile_);

					// show the confirm placement panel
					MeepleColor c = players_.get(currentPlayer_).getMeepleColor();
					HUDConfirmPlacementPanel confirmPanel = new HUDConfirmPlacementPanel(c);
					DisplayHelper.getInstance().add(confirmPanel,
							DisplayHelper.Layer.PALETTE,
							DisplayHelper.Positioning.TOP_CENTER);
					confirmPanel.attachMeeplePanels();

					// trigger an update of the board so the board is
					// re-rendered with the new temp-tiles in place.
					NotificationManager.getInstance().sendNotification(
							Notification.BOARD_SET, board_);

					// highlight the tile on the map and show placement options
					tempPlacementSprite_ = new TilePlacementSprite(here);

					// determine what regions of the tile are valid placements
					RegionsCalc r = new RegionsCalc(bindings_);
					currentRegionOptions_ = new ArrayList<Tile.Region>();

					for (Tile.Region region : Tile.Region.values()) {
						List<Meeple> result;
						r.traverseRegion(tempLocationIter_, region);
						result = r.getMeepleList(tempLocationIter_.getLocation(), region);
						if ((result.size() == 0) && 
								(tempPlacedTile_.featureInRegion(region) != null))
							currentRegionOptions_.add(region);
					}

					// TODO: Determine which spots on the tile are valid meeple
					// locations and populate the options arrays. This is dummy
					// code:
					tempPlacementSprite_.setRegionOptions(currentRegionOptions_);

					// Add the placement indicator to the map
					NotificationManager.getInstance().sendNotification(
							Notification.MAP_ADD_SPRITE, tempPlacementSprite_);
				}

			} catch (BoardPositionFilledException ex) {
				// Bury this exception?
				NotificationManager.getInstance().logError(
						new Notification("PositionFilled", here.toString()
								+ " is filled"));
				return;
			}
		}
	}

	public void placeMeeple(Notification n) {
		
		// the meeple is created in the map layer, because the map layer
		// has more intimate knowledege of which region the drag ended on.
		// (It can convert the pixel to the tile, and then to a region)
		// Region / Quadrant is set. We just set everything else.
		Meeple m = (Meeple) n.argument();
		
		if ((currentRegionOptions_.contains(m.getRegionOnTile())) &&
			(m.getParentTile() == tempPlacedTile_)){
			
			// if they've already tried placing a meeple, remove it before
			// allowing them to place another.
			if (tempPlacedMeeple_ != null){
				NotificationManager.getInstance().sendNotification(
						Notification.MAP_REMOVE_SPRITE, tempPlacedMeeple_);
			}
			
			m.setPlayer(currentPlayer_);
	
			// add the meeple to the tile
			tempPlacedTile_.setMeeple(m);
	
			// add the meeple sprite to the map layer so the guy is visible
			tempPlacedMeeple_ = new MeepleSprite(m, players_.get(currentPlayer_)
					.getMeepleColor());
	
			NotificationManager.getInstance().sendNotification(
					Notification.MAP_ADD_SPRITE, tempPlacedMeeple_);
			NotificationManager.getInstance().sendNotification(
					Notification.DRAG_PANEL_RESET);
		}
	}

	public void undoPlaceTile(Notification n) {
		board_.removeTemps();
		NotificationManager.getInstance().sendNotification(
				Notification.TILE_IN_HAND_CHANGED, tempPlacedTile_);
		NotificationManager.getInstance().sendNotification(
				Notification.MAP_REMOVE_SPRITE, tempPlacementSprite_);

		if (tempPlacedMeeple_ != null){
			NotificationManager.getInstance().sendNotification(
					Notification.MAP_REMOVE_SPRITE, tempPlacedMeeple_);
		}
		
		tempPlacementSprite_ = null;
		tempPlacedTile_ = null;
		tempLocationIter_ = null;
	}

	public void updateTileInHand(Notification n) {
		Boolean shouldPopulateLocations = false;
		Tile t = (Tile) n.argument();

		shouldPopulateLocations = ((tileInHand_ == null) || ((t != null) && (t
				.getUniqueIdentifier() != tileInHand_.getUniqueIdentifier())));

		tileInHand_ = t;

		// Do we need to populate possible locations?
		if (shouldPopulateLocations) {
			try {
				Set<TileBoardIterator> temp = board_.possiblePlacements(t);

				// If there are none, throw out TileInHand
				if (temp.isEmpty()) {
					NotificationManager.getInstance().sendNotification(
							Notification.LOG_WARNING,
							"Tile does not fit on board; drawing new Tile");
					NotificationManager.getInstance().sendNotification(
							Notification.TILE_UNUSABLE);

					// Otherwise, add the possibles
				} else {
					board_.addTemps(temp, tempLitTile_);
					NotificationManager.getInstance().sendNotification(
							Notification.BOARD_SET, board_);
				}
			} catch (BoardPositionFilledException e) {
				// we really shouldn't get here if possible placements works
				// correctly
				e.printStackTrace();
			}
		}
	}
}
