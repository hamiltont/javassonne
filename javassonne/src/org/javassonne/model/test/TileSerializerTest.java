/**
 * Javassonne 
 *  http://code.google.com/p/javassonne/
 * 
 * @author Adam Albright
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

import java.io.File;

import junit.framework.TestCase;

import org.javassonne.model.Tile;
import org.javassonne.model.TileSerializer;
import org.javassonne.model.TileSet;

public class TileSerializerTest extends TestCase {
	TileSerializer serial_;

	protected void setUp() throws Exception {
		super.setUp();

		serial_ = new TileSerializer();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testLoadTileSet() {
		TileSet tileSet = serial_.loadTileSet("tilesets/standard.xml");
		assertTrue(tileSet != null
				&& tileSet.getName().toLowerCase().equalsIgnoreCase("standard"));
	}

	public void testSaveTileSet() {
		TileSet tileSet = new TileSet("Untitled");
		Tile t = new Tile();
		tileSet.addTile(t, 5);
		String path = "tilesets/test.xml";

		try {
			serial_.saveTileSet(tileSet, path);

			File f = new File(path);
			assertTrue(f.exists() && f.canRead() && f.length() != 0);
			f.delete();
		} catch (Exception e) {
			fail("Failed to save TileSet");
		}

	}

}
