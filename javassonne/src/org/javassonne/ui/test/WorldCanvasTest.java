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

package org.javassonne.ui.test;

import java.awt.Dimension;

import junit.framework.TestCase;

import org.javassonne.model.Tile;
import org.javassonne.model.TileBoard;
import org.javassonne.model.TileMapBoard;
import org.javassonne.model.TileSerializer;
import org.javassonne.model.TileSet;
import org.javassonne.ui.WorldCanvas;

/**
 * Unit tests for WorldCanvas class
 * @author Administrator
 *
 */
public class WorldCanvasTest extends TestCase {
	WorldCanvas wc_;

	protected void setUp() throws Exception {
		super.setUp();

		// Build a TileBoard to pass to default constructor
		TileSerializer s = new TileSerializer();
		TileSet set = s.loadTileSet("tilesets/standard.xml");
		Tile t = set.tileWithUniqueIdentifier("tile_standard_1");
		TileBoard model = new TileMapBoard(t);

		wc_ = new WorldCanvas(new Dimension(100, 100));
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testRedraw() {
		wc_.redraw();
	}

}
