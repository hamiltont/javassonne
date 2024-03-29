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

import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

/**
 * The Tile class is used to represent a single tile in the game. Tiles are
 * collected into tile sets, which are in turn collected into tile decks. Tiles
 * may then be popped from the "deck" and placed on the tile board.
 * 
 * Tiles have two "layers" of features. The bottom layer (farms) is divided into
 * quadrants. Each quadrant is assigned a number that indicates what farm it is
 * part of. The top layer (features) is divided into five regions. Each of these
 * five regions is bound to a TileFeature that represents the content of that
 * part of the tile - a road, cloister, or fort, for example.
 * 
 * The Tile also has an image property, that is set when the tile is loaded into
 * the tile set. The Tile itself does not know where its image is saved. You can
 * think of images being "cached" in the tile objects at runtime.
 * 
 * For more, see http://code.google.com/p/javassonne/wiki/TileObject
 * 
 * @author bengotow
 * 
 */
public class Tile {

	/**
	 * The Region enum allows regions of the Tile to be addressed more easily.
	 * Ex: {@code tile_.featureIdentifierInRegion(Region.Left);}
	 * 
	 * @author bengotow
	 * 
	 */
	public enum Region {
		Left(0, 30, 91), Top(1, 91, 30), Right(2, 152, 91), Bottom(3, 91, 152), Center(
				4, 91, 91);
		private final int index;

		// HACK: In order to actually display a guy on top of a region,
		// we need to know where on the tile the region is. This has to be
		// hardcoded somewhere, so it seemed like this was a good place.
		// Used in TilePlacementSprite.
		public final int x;
		public final int y;

		Region(int i, int x, int y) {
			this.index = i;
			this.x = x;
			this.y = y;
		}
		
		public boolean isAdjacentTo(Tile.Quadrant quad){
			if(this.equals(Tile.Region.Left))
				return quad.equals(Tile.Quadrant.BottomLeft) || quad.equals(Tile.Quadrant.TopLeft);
			else if(this.equals(Tile.Region.Top))
				return quad.equals(Tile.Quadrant.TopLeft) || quad.equals(Tile.Quadrant.TopRight);
			else if(this.equals(Tile.Region.Right))
				return quad.equals(Tile.Quadrant.BottomRight) || quad.equals(Tile.Quadrant.TopRight);
			else if(this.equals(Tile.Region.Bottom))
				return quad.equals(Tile.Quadrant.BottomLeft) || quad.equals(Tile.Quadrant.BottomRight);
			else
				return false;				
		}
		
	}

	/**
	 * The Quadrant enum allows quadrants of the Tile to be addressed more
	 * easily. Ex: {@code tile_.farmInQuadrant(Quadrant.TopLeft);}
	 * 
	 * @author bengotow
	 * 
	 */
	public enum Quadrant {
		TopLeft(0, new Rectangle2D.Double(0, 0, 91, 91)), 
		TopRight(1, new Rectangle2D.Double(91, 0, 91, 91)), 
		BottomRight(2, new Rectangle2D.Double(91, 91, 91, 91)), 
		BottomLeft(3, new Rectangle2D.Double(0, 91, 91, 91));
		
		private final int index;
		public final Rectangle2D rect;

		// HACK: In order to actually display a guy on top of a region,
		// we need to know where on the tile the region is. This has to be
		// hardcoded somewhere, so it seemed like this was a good place.
		// Used in TilePlacementSprite.

		Quadrant(int i, Rectangle2D rect) {
			this.index = i;
			this.rect = rect;
		}
		
		public boolean isAdjacentTo(Tile.Region reg){
			if(reg.equals(Tile.Region.Left))
				return this.equals(Tile.Quadrant.BottomLeft) || this.equals(Tile.Quadrant.TopLeft);
			else if(reg.equals(Tile.Region.Top))
				return this.equals(Tile.Quadrant.TopLeft) || this.equals(Tile.Quadrant.TopRight);
			else if(reg.equals(Tile.Region.Right))
				return this.equals(Tile.Quadrant.BottomRight) || this.equals(Tile.Quadrant.TopRight);
			else if(reg.equals(Tile.Region.Bottom))
				return this.equals(Tile.Quadrant.BottomLeft) || this.equals(Tile.Quadrant.BottomRight);
			else
				return false;				
		}
	}

	private ArrayList<TileFeature> featureObjs_;
	private String[] features_ = new String[5];
	
	private int[] farms_ = new int[4];
	private boolean[] farmWalls_ = new boolean[4];
	private String uniqueIdentifier_;
	private String imageFolder_;
	
	// this is set by the TileSet when the image is loaded. We want to
	// cache this, so the image is only loaded once from a file
	private BufferedImage image_;
	
	// We don't need to keep rotation unless the game is saved and loaded
	// from a file. In that case, we need to know how many times the tile has 
	// been rotated so we can restore the image.
	private int rotation_ = 0;
	
	// Meeple can be attached to tiles in regions or quadrants, but only
	// one meeple will ever be on the tile at once. IMPORTANT:
	// The meeple knows which region or quadrant it is on.
	private Meeple meeple_;
	
	// Constructor

	public Tile() {
		uniqueIdentifier_ = "not assigned";
		featureObjs_ = new ArrayList<TileFeature>(5);
	}

	// FARMS: Getter and Setter Functionality

	public Tile(Tile t) {
		farms_ = t.farms_;
		features_ = t.features_;
		featureObjs_ = t.featureObjs_;
		
		farmWalls_ = t.farmWalls_;
		uniqueIdentifier_ = new String(t.getUniqueIdentifier());
		imageFolder_ = t.imageFolder_;
		
		if (t.getImage() != null) {
			BufferedImage other = t.getImage();
			image_ = other.getSubimage(0, 0, other.getWidth(), other
					.getHeight());
		}
	}

	public String getImageFolder() {
		return imageFolder_;
	}
	
	public void setImageFolder(String tileImagesFolder_) {
		imageFolder_ = tileImagesFolder_;
	}
	
	/**
	 * @param q
	 *            The qudadrant you want the farm value for
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
	public TileFeature featureInRegion(Region r) {
		return featureObjs_.get(r.index);
	}

	/**
	 * @param r
	 *            The region
	 * @param feature
	 *            The tile feature that you want to be present in this region of
	 *            the tile.
	 */
	public void setFeatureInRegion(Region r, TileFeature feature) {
		if (featureObjs_ == null)
			featureObjs_ = new ArrayList<TileFeature>(5);
		featureObjs_.add(r.index, feature);
	}

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
	 * @param feature
	 *            The tile feature that you want to be present in this region of
	 *            the tile.
	 */
	public void setFeatureIdentifierInRegion(Region r, String feature) {
		features_[r.index] = feature;
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
		if (image_ != null)
			return image_;
		else{
			try {
				String path = String.format("%s%s.jpg", imageFolder_, uniqueIdentifier_);
				image_ = ImageIO.read(new File(path));
				for (int ii = 0; ii < rotation_; ii++){
					this.rotateImage(1);
				}
			} catch (IOException e) {
				System.err.println("The tile image could not be loaded.");
			}
			return image_;
		}
	}

	/**
	 * @param image
	 *            The BufferedImage to cache for the tile image
	 */
	public void setImage(BufferedImage image) {
		image_ = image;
	}

	public Meeple getMeeple()
	{
		return meeple_;
	}
	
	public void setMeeple(Meeple m)
	{
		meeple_ = m;
	}
	
	// Convenience Functions
	
	/**
	 * This function returns a meeple object if the Tile has a meeple in the 
	 * region r, and null if no meeple exists there.
	 * 
	 * r The region you want to test for a meeple on
	 * @return Returns the meeple at location r, or null.
	 */
	public Meeple meepleInRegion(Tile.Region r)
	{
		if ((meeple_ != null) && (meeple_.getRegionOnTile() == r))
			return meeple_;
		return null;
	}
	
	/**
	 * This function returns a meeple object if the Tile has a meeple in the 
	 * quadrant q, and null if no meeple exists there.
	 * 
	 * q The quadrant you want to test for a meeple on
	 * @return Returns the meeple at location q, or null.
	 */
	public Meeple meepleInQuadrant(Tile.Quadrant q)
	{
		if ((meeple_ != null) && (meeple_.getQuadrantOnTile() == q))
			return meeple_;
		return null;
	}
	
	/**
	 * @return Returns a string that visually represents the nine areas of the
	 *         tile identified
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

	/**
	 * Rotates the tile left. It modifies the tile image, changes all of the
	 * features_, farms_ and farmWall_ values, etc...
	 */
	public void rotateLeft() {
		this.rotate(-1);
	}

	/**
	 * Rotates the tile right. It modifies the tile image, changes all of the
	 * features_, farms_ and farmWall_ values, etc...
	 */
	public void rotateRight() {
		this.rotate(1);
	}

	/**
	 * @param direction
	 *            Direction that properties of the tile should be shifted
	 */
	private void rotate(int direction) {
		// Make tempDir positive by rotating 360 until it is
		while (direction < 0)
			direction += 4;

		// Make tempArrays so contents aren't overwritten
		ArrayList<TileFeature> tempFeaturesObj = (ArrayList<TileFeature>) featureObjs_.clone();
		String[] tempFeatures = features_.clone();
		int[] tempFarms = farms_.clone();
		boolean[] tempWalls = farmWalls_.clone();

		// move all of the features, farms, and farmWalls one position to the
		// right by shifting them within their respective arrays

		for (int i = 0; i < 4; i++) {
			tempFeaturesObj.set((i + direction) % 4, featureObjs_.get(i));
			tempFeatures[(i + direction) % 4] = features_[i];
			tempFarms[(i + direction) % 4] = farms_[i];
			tempWalls[(i + direction) % 4] = farmWalls_[i];
		}
		featureObjs_ = tempFeaturesObj;
		features_ = tempFeatures;
		farms_ = tempFarms;
		farmWalls_ = tempWalls;
		rotation_ = (rotation_ + direction) % 4;
		
		rotateImage(direction);
	}

	public void rotateImage(int direction) {
		int angle = direction * 90;

		// rotate our image, if it exists
		if (image_ != null) {
			AffineTransform test = new AffineTransform();
			int w = image_.getWidth();
			int h = image_.getHeight();
			test.rotate(Math.toRadians(angle), w / 2, h / 2);
			AffineTransformOp op = new AffineTransformOp(test,
					AffineTransformOp.TYPE_BILINEAR);
			image_ = op.filter(image_, null);
		}
	}
	
	@Override
	public boolean equals(Object other) {
		if (other.getClass().equals(Tile.class)) {
			Tile otherTile = (Tile) other;
			return this.getUniqueIdentifier().equals(
					otherTile.getUniqueIdentifier());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return this.getUniqueIdentifier().hashCode();
	}

}