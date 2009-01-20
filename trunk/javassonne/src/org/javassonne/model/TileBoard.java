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

public interface TileBoard {

	//Returns "home" square - the tile the game began with.
	public TileBoardIterator homeTile();
	
	//Adds tile to the specified TileBoardIterator location
	public void addTile(TileBoardIterator iter, Tile tile) throws Exception;
	//TODO: implement Exception
	
	//Not Needed?
	//Removes tile from the specified TileBoardIterator location
	//public Tile removeTile(TileBoardIterator iter);

	//Returns tile at specified TileBoardIterator location
	//without removing Tile from the Board
	public Tile getTile(TileBoardIterator iter);
	
	//Returns upper left corner iterator
	public TileBoardIterator getUpperLeftCorner();
	
	//Returns lower right corner iterator
	public TileBoardIterator getLowerRightCorner();
	
	
}
