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

package org.javassonne.model;

import java.util.ArrayList;
import java.util.Random;

public class TileDeck {

	private ArrayList<Tile> tiles_;

	public TileDeck() {
		tiles_ = new ArrayList<Tile>();
	}

	// Adding Tiles
	
	public void addTileSet(TileSet set)
	{
		for (int ii = 0; ii < set.tileCount(); ii++){
			addTile(set.tileAtIndex(ii), set.tileCountAtIndex(ii));
		}
	}
	
	public void addTile(Tile t, int count)
	{
		for (int ii = 0; ii < count; ii++)
			tiles_.add(t);
	}
	
	// Pulling Tiles
	
	public Tile popRandomTile()
	{
		 Random generator = new Random( 19580427 );
		 int index = generator.nextInt(tiles_.size());
		 
		 Tile t = tiles_.get(index);
		 tiles_.remove(index);
		 
		 return t;
	}
	
}
