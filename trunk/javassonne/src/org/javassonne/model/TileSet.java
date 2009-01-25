/**
 * Javassonne 
 *  http://code.google.com/p/javassonne/
 * 
 * @author Ben Gotow
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

package org.javassonne.model;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

/**
 * @author bengotow The tile set class represents a collection of tiles, and
 *         stores the number of times each one may be drawn during the game.
 *         Tile sets may be loaded from disk using the TileSerializer class, and
 *         should be created using the TileSetCreator. Tile sets are different
 *         from tile decks. A tile set represents a single collection of tiles -
 *         for example, the tiles from an expansion pack. A tile deck may
 *         contain multiple tile sets, and represents the pool of tiles the user
 *         is actually playing with. Additionally, a tile set contains a single
 *         copy of each tile object, while the tile deck may contain multiple.
 */
public class TileSet {

	private String name_;
	private ArrayList<Tile> tiles_;
	private ArrayList<Integer> tileCounts_;
	private ArrayList<TileFeature> tileFeatures_;

	private String tileImagesFolder_;

	/**
	 * @param name
	 *            The name of the tile set being created.
	 */
	public TileSet(String name) {
		name_ = name;
		tiles_ = new ArrayList<Tile>();
		tileCounts_ = new ArrayList<Integer>();
		tileFeatures_ = new ArrayList<TileFeature>();
	}

	// Getter and Setter Functionality

	public void setName(String name_) {
		this.name_ = name_;
	}

	public String getName() {
		return name_;
	}

	// Convenience Functions

	public int tileCount() {
		return tiles_.size();
	}

	// Adding and Removing Tiles

	public void addTile(Tile t, int count) {
		tiles_.add(t);
		tileCounts_.add(count);
	}

	public Tile tileAtIndex(int index) {
		return tiles_.get(index);
	}

	public Tile tileWithUniqueIdentifier(String identifier) {
		for (Tile t : tiles_) {
			if (identifier.equals(t.getUniqueIdentifier()))
				return t;
		}
		return null;
	}

	public int tileCountAtIndex(int index) {
		return tileCounts_.get(index);
	}

	// Tile Images

	public String tileImagesFolder() {
		return tileImagesFolder_;
	}

	public void setTileImagesFolder(String folder) {
		this.tileImagesFolder_ = folder;
	}

	/**
	 * This function should be called before gameplay starts. It loads images for each tile
	 * from the tileImagesFolder_ and caches them in the tile objects.
	 */
	public void loadTileImages() {
		try {
			for (Tile t : this.tiles_) {
				String identifier = t.getUniqueIdentifier();
				BufferedImage img = ImageIO.read(new File(String.format(
						"%s/%s.jpg", tileImagesFolder_, identifier)));
				t.setImage(img);
			}
		} catch (IOException e) {
			System.err
					.println("The tile images could not be loaded. One or more tiles may not have images.");
		}
	}

	// Adding and Removing Tile Features

	public int tileFeatureCount() {
		return tileFeatures_.size();
	}

	public void addTileFeature(TileFeature f) {
		tileFeatures_.add(f);
	}

	public TileFeature tileFeatureAtIndex(int index) {
		return tileFeatures_.get(index);
	}

	public TileFeature tileFeatureWithIdentifier(String identifier) {
		for (TileFeature f : tileFeatures_)
			if (f.identifier.equals(identifier))
				return f;
		return null;
	}
}
