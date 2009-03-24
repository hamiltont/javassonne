/**
 * Javassonne 
 *  http://code.google.com/p/javassonne/
 * 
 * @author Ben Gotow
 * @date Jan 22, 2009
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

package org.javassonne.model.test;

import junit.framework.TestCase;

import org.javassonne.model.Tile;
import org.javassonne.model.TileDeck;
import org.javassonne.model.TileFeature;
import org.javassonne.model.TileSerializer;
import org.javassonne.model.TileSet;
import org.javassonne.model.Tile.Quadrant;
import org.javassonne.model.Tile.Region;

public class TileTests extends TestCase {

	Tile tile_;
	TileDeck tileDeck_;
	Tile tile2_;

	protected void setUp() throws Exception {
		super.setUp();

		tile_ = new Tile();
		TileSerializer s = new TileSerializer();
		TileSet set = s.loadTileSet("tilesets/standard.xml");
		if (set == null) {
			System.err.println("Tile set could not be found.");
			System.exit(0);
		}
		tileDeck_ = new TileDeck();
		tileDeck_.addTileSet(set);
	}

	public void testRotate() {
		while ((tile2_ = tileDeck_.popRandomTile()) != null) {
			Tile temp = new Tile(tile2_);
			tile2_.rotateRight();
			tile2_.rotateRight();
			tile2_.rotateRight();
			tile2_.rotateRight();
			assertTrue(checkEqual(tile2_, temp));
			tile2_.rotateLeft();
			tile2_.rotateLeft();
			tile2_.rotateLeft();
			tile2_.rotateLeft();
			assertTrue(checkEqual(tile2_, temp));
		}

	}

	private boolean checkEqual(Tile tile1, Tile tile2) {
		return (tile1.featureInRegion(Region.Bottom) == tile2
						.featureInRegion(Region.Bottom)
				&& tile1.featureInRegion(Region.Left) == tile2
						.featureInRegion(Region.Left)
				&& tile1.featureInRegion(Region.Top) == tile2
						.featureInRegion(Region.Top) 
				&& tile1.featureInRegion(Region.Right) == tile2
						.featureInRegion(Region.Right));
	}

	public void testSetFarm() {
		tile_.setFarmInQuadrant(Quadrant.TopLeft, 1);
		assertTrue(tile_.farmInQuadrant(Quadrant.TopLeft) == 1);
	}

	public void testSetFarmWall() {
		tile_.setFarmWallInRegion(Region.Left, true);
		assertTrue(tile_.farmWallInRegion(Region.Left));
	}

	public void testSetUniqueIdentifier() {
		tile_.setUniqueIdentifier("sample");
		assertTrue(tile_.getUniqueIdentifier() == "sample");
	}

	public void testSetFeature() {
		TileFeature f = new TileFeature();

		tile_.setFeatureInRegion(Region.Left, f);
		assertTrue(tile_.featureInRegion(Region.Left) == f);

		tile_.setFeatureInRegion(Region.Right, f);
		assertTrue(tile_.featureInRegion(Region.Right) == f);
	}

	public void testRotateLeft() {
		TileFeature f = new TileFeature();
		
		tile_.setFarmInQuadrant(Quadrant.TopLeft, 1);
		tile_.setFarmWallInRegion(Region.Left, true);
		tile_.setFeatureInRegion(Region.Left, f);

		tile_.rotateLeft();

		assertTrue(tile_.farmInQuadrant(Quadrant.BottomLeft) == 1);
		assertTrue(tile_.farmWallInRegion(Region.Bottom));
		assertTrue(tile_.featureInRegion(Region.Bottom) == f);
	}

	public void testRotateRight() {
		TileFeature f = new TileFeature();
		
		tile_.setFarmInQuadrant(Quadrant.TopLeft, 1);
		tile_.setFarmWallInRegion(Region.Left, true);
		tile_.setFeatureInRegion(Region.Left, f);

		tile_.rotateRight();

		assertTrue(tile_.farmInQuadrant(Quadrant.TopRight) == 1);
		assertTrue(tile_.farmWallInRegion(Region.Top));
		assertTrue(tile_.featureInRegion(Region.Top) == f);
	}

	public void testCopyConstructor() {
		Tile t = new Tile(tile_);
		assertTrue(tile_.equals(t));
	}

	public void testEquals() {
		Tile t1 = new Tile();
		t1.setUniqueIdentifier("sample1");

		Tile t2 = new Tile();
		t1.setUniqueIdentifier("sample2");

		assertTrue(t1.equals(t1));
		assertFalse(t1.equals(t2));
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

}
