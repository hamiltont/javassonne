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

	/**
	 * @return - TileBoardIterator pointing to "home" square - the tile the game
	 *         began with.
	 */
	public TileBoardIterator homeTile();
	

	/**
	 * @param iter location to insert
	 * @param tile to be inserted
	 * @throws BoardPositionFilledException
	 */
	public void addTemp(TileBoardIterator iter, Tile tile)
			throws BoardPositionFilledException;

	/**
	 * removes all temp tiles from board
	 */
	public void removeTemps();

	/**
	 * @param iter
	 *            - location at which tile should be made permanent
	 * @throws NotValidPlacementException 
	 */
	public void removeTempStatus(TileBoardIterator iter) throws NotValidPlacementException;

	/**
	 * @param iter
	 *            - where the tile is to be placed
	 * @param tile
	 *            - what will be placed
	 * @return - whether or not the placement is valid
	 */
	public boolean isValidPlacement(TileBoardIterator iter, Tile tile);

	/**
	 * @param iter
	 *            - where to grab a tile
	 * @return - Tile at iter location (or null)
	 */
	public Tile getTile(TileBoardIterator iter);

	/**
	 * @return - TileBoardIterator pointing to upper left corner of board
	 */
	public TileBoardIterator getUpperLeftCorner();

	/**
	 * @return - TileBoardIterator pointing to lower right corner of board
	 */
	public TileBoardIterator getLowerRightCorner();

}
