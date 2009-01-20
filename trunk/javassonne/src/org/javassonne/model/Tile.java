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

public class Tile {

	public enum Region {
		Left(0), Right(1), Top(2), Bottom(3), Center(4);
		private final int index;

		Region(int i) {
			this.index = i;
		}
	}

	public enum Quadrant {
		TopLeft(0), TopRight(1), BottomLeft(2), BottomRight(3);
		private final int index;

		Quadrant(int i) {
			this.index = i;
		}
	}

	private String[] features_ = new String[5];
	private int[] farms_ = new int[4];
	private boolean[] farmWalls_ = new boolean[4];
	private String uniqueIdentifier_;

	// this is set by the TileSet when the image is loaded. We want to
	// cache this, so the image is only loaded once from a file
	private BufferedImage image_;

	// Constructor

	public Tile() {
	}

	// FARMS: Getter and Setter Functionality

	/**
	 * @param q
	 *            The qudardant you want the farm value for
	 * @return The farm value
	 */
	public int farmInQuadrant(Quadrant q) {
		return farms_[q.index];
	}

	/**
	 * @param q
	 *            The quadrant you want to set the farm value for
	 * @param farmValue
	 *            The value of the farm in that quadrant
	 */
	public void setFarmInQuadrant(Quadrant q, int farmValue) {
		farms_[q.index] = farmValue;
	}

	/**
	 * @param r
	 *            The region
	 * @return True if the edge in the region r is walled off.
	 */
	public boolean farmWallInRegion(Region r) {
		return farmWalls_[r.index];
	}

	/**
	 * @param r
	 *            The region
	 * @param present
	 *            True if the edge in the region r should be walled off
	 */
	public void setFarmWallInRegion(Region r, boolean present) {
		farmWalls_[r.index] = present;
	}

	// FEATURES: Getter and Setter Functionality

	/**
	 * @param r
	 *            The region
	 * @return The identifier of the feature present in this region of the tile.
	 */
	public String featureIdentifierInRegion(Region r) {
		return features_[r.index];
	}

	/**
	 * @param r
	 *            The region
	 * @param identifier
	 *            The identifier for the feature you want in that region.
	 */
	public void setFeatureIdentifierInRegion(Region r, String identifier) {
		features_[r.index] = identifier;
	}

	/**
	 * @param r
	 *            The region
	 * @param feature
	 *            The tile feature that you want to be present in this region of
	 *            the tile.
	 */
	public void setFeatureInRegion(Region r, TileFeature feature) {
		features_[r.index] = feature.identifier;
	}

	// UNIQUE ID: Getter and Setter Functionality

	/**
	 * @return The unique identifier of this tile, which is used for loading the
	 *         tile image.
	 */
	public String getUniqueIdentifier() {
		return uniqueIdentifier_;
	}

	/**
	 * @param uniqueIdentifier_
	 *            The unique identifier to use for the Tile. Unique identifiers
	 *            are used for loading the tile image.
	 */
	public void setUniqueIdentifier(String uniqueIdentifier_) {
		this.uniqueIdentifier_ = uniqueIdentifier_;
	}

	// IMAGE: Getter and Setter Functionality

	/**
	 * @return The currently cached image for this tile, if available
	 */
	public BufferedImage getImage() {
		return image_;
	}

	/**
	 * @param image
	 *            The BufferedImage to cache for the tile image
	 */
	public void setImage(BufferedImage image) {
		image_ = image;
	}

	// Convenience Functions

	/**
	 * @return Returns a square box with the nine areas of the tile identified
	 */
	public String description() {
		// Print out a nice box with the nine areas labeled. Like the Tile page
		// on the wiki.
		return String
				.format(
						"---------\r| %d %s %d |  %s\r| %s %s %s |\r| %d %s %d |\r---------",
						farms_[Quadrant.TopLeft.index],
						features_[Region.Top.index],
						farms_[Quadrant.TopRight.index], uniqueIdentifier_,
						features_[Region.Left.index],
						features_[Region.Center.index],
						features_[Region.Right.index],
						farms_[Quadrant.BottomLeft.index],
						features_[Region.Bottom.index],
						farms_[Quadrant.BottomRight.index]);
	}

}