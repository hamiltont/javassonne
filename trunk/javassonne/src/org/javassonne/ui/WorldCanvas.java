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

package org.javassonne.ui;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import org.javassonne.model.Tile;
import org.javassonne.model.TileSerializer;
import org.javassonne.model.TileSet;

public class WorldCanvas extends Canvas {

	public WorldCanvas() {
		setBackground(Color.yellow);
	}

	public void paint(Graphics g) {
		Graphics2D g2;
		int Height;

		TileSerializer s = new TileSerializer();
		TileSet set = s.loadTileSet("tilesets/standard.xml");
		Tile t = set.tileWithUniqueIdentifier("tile_standard_1");
		
		g2 = (Graphics2D) g;
		g2.drawImage(t.getImage(), 0, 0, 300, 300, 50, 50, 300, 300, null);
	}
}
