/**
 * Javassonne 
 *  http://code.google.com/p/javassonne/
 * 
 * @author Hamilton Turner
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

package org.javassonne.ui;

import org.javassonne.model.TileBoard;
import org.javassonne.model.TileBoardIterator;
import org.javassonne.model.TileDeck;
import org.javassonne.model.TileMapBoard;
import org.javassonne.model.TileSerializer;
import org.javassonne.model.TileSet;

public class SwingDemos {

	public static void main(String args[]) {
		
		// Load all possible tiles
		TileSerializer s = new TileSerializer();
		
		TileSet set = s.loadTileSet("tilesets/standard.xml");
		set.setTileImagesFolder("tilesets/standard");
		
		// Populate the set into the deck
		// 	Note: you can have multiple copies of a tile in a deck,
		//		  but not in a set 
		TileDeck deck = new TileDeck();
		deck.addTileSet(set);
		
		TileBoard model = new TileMapBoard(deck.popRandomTile());
		TileBoardIterator iter = model.homeTile();
		
		for (int i = 0; i < 3; i++){
			if (i%3 == 0)
				iter.nextRow();
			try{
				model.addTile(iter.right(), deck.popRandomTile());
			} catch (Exception e) {}
		}
		
		
		GameWindow view = new GameWindow(model);
		GameWindowController controller = new GameWindowController(view, model);
		view.setController(controller);

	}
}
