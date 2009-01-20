/**
 * Javassonne 
 *  http://code.google.com/p/javassonne/
 * 
 * @author Kyle Prete
 * @date Jan 14, 2009
 * 
 * Copyright 2009 Javasonne Team
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

import java.util.HashMap;


public class TileMapBoard implements TileBoard {
	
	private HashMap<IntPair, Tile> data_;

	public TileBoardIterator homeTile() {
		return new TileBoardIterator(this,new IntPair(0,0));
	}
	
	public boolean positionFilled(TileBoardIterator iter)
	{
		return data_.containsKey(iter.getLocation());
	}

	//Adds Tile at iter location
	//Throws to-be-implemented exception if position is filled
	public void addTile(TileBoardIterator iter, Tile tile) throws Exception {
		if(!positionFilled(iter))
			throw new Exception();
		else
			data_.put(iter.getLocation(), tile);
	}
	
	//Removes Tile at iter location
	//Returns null if position is empty
	public Tile removeTile(TileBoardIterator iter) {
		return data_.remove(iter.getLocation());
	}
	
	//Returns Tile at iter location, maintaining it in Container
	public Tile getTile(TileBoardIterator iter) {
		return data_.get(iter.getLocation());
	}

}
