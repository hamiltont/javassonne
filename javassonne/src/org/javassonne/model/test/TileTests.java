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
import org.javassonne.model.TileFeature;
import org.javassonne.model.Tile.Quadrant;
import org.javassonne.model.Tile.Region;

public class TileTests extends TestCase {

	Tile tile_;

	protected void setUp() throws Exception {
		super.setUp();

		tile_ = new Tile();
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

		tile_.setFeatureIdentifierInRegion(Region.Left, f.identifier);
		assertTrue(tile_.featureIdentifierInRegion(Region.Left) == f.identifier);

		tile_.setFeatureInRegion(Region.Right, f);
		assertTrue(tile_.featureIdentifierInRegion(Region.Right) == f.identifier);
	}

	public void testRotateLeft() {
		tile_.setFarmInQuadrant(Quadrant.TopLeft, 1);
		tile_.setFarmWallInRegion(Region.Left, true);
		tile_.setFeatureIdentifierInRegion(Region.Left, "f");

		tile_.rotateLeft();

		assertTrue(tile_.farmInQuadrant(Quadrant.BottomLeft) == 1);
		assertTrue(tile_.farmWallInRegion(Region.Bottom));
		assertTrue(tile_.featureIdentifierInRegion(Region.Bottom) == "f");
	}

	public void testRotateRight() {
		tile_.setFarmInQuadrant(Quadrant.TopLeft, 1);
		tile_.setFarmWallInRegion(Region.Left, true);
		tile_.setFeatureIdentifierInRegion(Region.Left, "f");

		tile_.rotateRight();

		assertTrue(tile_.farmInQuadrant(Quadrant.TopRight) == 1);
		assertTrue(tile_.farmWallInRegion(Region.Top));
		assertTrue(tile_.featureIdentifierInRegion(Region.Top) == "f");
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
