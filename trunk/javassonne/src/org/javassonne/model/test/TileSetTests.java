/**
 * Javassonne 
 *  http://code.google.com/p/javassonne/
 * 
 * @author [Add Name Here]
 * @date Jan 25, 2009
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
import org.javassonne.model.TileSet;

public class TileSetTests extends TestCase {

	TileSet testSet_ = null;

	protected void setUp() throws Exception {
		super.setUp();

		testSet_ = new TileSet("test");
	}

	public void testAddTile() {
		int startTileCount = testSet_.tileCount();

		Tile t = new Tile();
		t.setUniqueIdentifier("identifier");

		testSet_.addTile(t, 4);

		assertNotNull(testSet_.tileWithUniqueIdentifier("identifier"));
		assertTrue(testSet_.tileCount() == startTileCount + 1);
		assertTrue(testSet_.tileCountAtIndex(0) == 4);
	}

	public void testAddTileFeature() {
		int startFeatureCount = testSet_.tileFeatureCount();

		TileFeature f = new TileFeature("name", "identifier", false, 0);
		testSet_.addTileFeature(f);

		assertNotNull(testSet_.tileFeatureWithIdentifier("identifier"));
		assertTrue(testSet_.tileFeatureCount() == startFeatureCount + 1);
	}

	public void testGetTileByIdentifier() {
		Tile t = new Tile();
		t.setUniqueIdentifier("test");
		testSet_.addTile(t, 1);

		assertTrue(testSet_.tileWithUniqueIdentifier(t.getUniqueIdentifier()) == t);
	}

	public void testSetName() {
		String name = "test";
		testSet_.setName(name);
		assertTrue(testSet_.getName() == name);
	}

	public void testSetImagesFolder() {
		String folder = "folder";
		testSet_.setTileImagesFolder(folder);
		assertTrue(testSet_.tileImagesFolder() == folder);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

}
