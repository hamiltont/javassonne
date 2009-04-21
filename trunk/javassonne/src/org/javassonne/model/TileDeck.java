/**
 * Javassonne 
 *  http://code.google.com/p/javassonne/
 * 
 * @author [Add Name Here]
 * @date Jan 20, 2009
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

package org.javassonne.model;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import javax.imageio.ImageIO;

/**
 * The Tile Deck class represents a collection of tiles used during gameplay. A
 * tile deck is different from a tile set, because it may contain multiple
 * copies of each tile object. A tile deck may contain tiles from multiple tile
 * sets, if a user is playing with tiles from two sources (the original game,
 * and an expansion pack - for example).
 * 
 * @author bengotow
 */
public class TileDeck {

	private ArrayList<Tile> tiles_;
	private HashMap<Tile, Integer> tilesRemaining_;
	private TileFeatureBindings tileFeatureBindings_;
	private Tile homeTile_;

	/**
	 * A general purpose constructor that initializes an empty tile deck.
	 */
	public TileDeck() {
		tiles_ = new ArrayList<Tile>();
		tilesRemaining_ = new HashMap<Tile, Integer>();
		tileFeatureBindings_ = new TileFeatureBindings();
		homeTile_ = null;
	}

	// Adding Tiles

	/**
	 * @param set
	 *            The set of tiles you'd like to add to the tile deck. A tile
	 *            deck may contain multiple sets of tiles, since a user might
	 *            play with the original tiles and the tiles from an expansion
	 *            pack.
	 */
	public void addTileSet(TileSet set) {
		for (int ii = 0; ii < set.tileCount(); ii++) {
			addTile(set.tileAtIndex(ii), set.tileCountAtIndex(ii));
		}
		tileFeatureBindings_.addFeatureBindings(set.tileFeatureBindings());
		tileFeatureBindings_.addCompletionMultipliers(set.tileFeatureBindings());
		
		if (homeTile_ == null)
			homeTile_ = set.homeTile();
	}

	/**
	 * This function adds a single tile to the deck. In general, tiles should be
	 * arranged in sets and added to tile decks by set.
	 * 
	 * @param t
	 *            The individual tile to add to the deck.
	 * @param count
	 *            The number of copies of that tile that should be in the deck.
	 */
	public void addTile(Tile t, int count) {
		for (int ii = 0; ii < count; ii++)
			tiles_.add(new Tile(t));

		Integer existing = tilesRemaining_.get(t);
		if (existing == null)
			existing = 0;
		tilesRemaining_.put(t, existing += count);
		
		if (homeTile_ == null)
			homeTile_ = t;
	}

	// Pulling Tiles

	/**
	 * @return Returns a random tile from the remaining tiles in the deck and
	 *         removes it from the deck.
	 */
	public Tile popRandomTile() {
		if (tiles_.size() == 0)
			return null;
		Random generator = new Random();
		int index = generator.nextInt(tiles_.size());

		Tile t = tiles_.remove(index);
		tilesRemaining_.put(t, tilesRemaining_.get(t) - 1);

		return t;
	}

	public void removeTileWithIdentifier(String uniqueIdentifier) {
		Tile t = null;
		for (int ii = 0; ii < tiles_.size(); ii++){
			if (tiles_.get(ii).getUniqueIdentifier().equals(uniqueIdentifier)){
				t = tiles_.get(ii);
				tiles_.remove(ii);
				break;
			}
		}
		if (t != null)
			tilesRemaining_.put(t, tilesRemaining_.get(t) - 1);
	}
	
	/**
	 * @return The number of tiles remaining in the deck. Should decrement when
	 *         popRandomTile() is called.
	 */
	public int tilesRemaining() {
		return tiles_.size();
	}

	public HashMap<Tile, Integer> tilesRemainingByType() {
		return tilesRemaining_;
	}

	public TileFeatureBindings tileFeatureBindings() {
		return tileFeatureBindings_;
	}

	public Tile homeTile() {
		return homeTile_;
	}

}
