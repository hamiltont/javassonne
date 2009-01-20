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

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class TileSet {

	private String name_;
	private ArrayList<Tile> tiles_;
	private ArrayList<Integer> tileCounts_;
	private ArrayList<TileFeature> tileFeatures_;

	private String tileImagesFolder_;

	// Constructor

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

	public void loadTileImages() {
		try {
			for (Tile t : this.tiles_) {
				String identifier = t.getUniqueIdentifier();
				BufferedImage img = ImageIO.read(new File(String.format(
						"%s/%s.jpg", tileImagesFolder_, identifier)));
				t.setImage(img);
			}
		} catch (IOException e) {
			// do something here!
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
