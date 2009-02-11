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
	 * @param iter
	 *            - where tile will be inserted in TileBoard
	 * @param tile
	 *            - what will be inserted in TileBoard
	 * @throws BoardPositionFilledException
	 *             - thrown if iter position is filled
	 * @throws NotValidPlacementException
	 *             - thrown if tile at iter is not a valid placement
	 */
	public void addTile(TileBoardIterator iter, Tile tile)
			throws BoardPositionFilledException, NotValidPlacementException;

	/**
	 * @param iter
	 *            - where tile will be inserted in TileBoard
	 * @param tile
	 *            - what will be inserted in TileBoard
	 * @param checkValid
	 *            - false to override valid position check
	 * @throws BoardPositionFilledException
	 *             - thrown if iter position is filled
	 * @throws NotValidPlacementException
	 *             - thrown if tile at iter is not a valid placement
	 */
	public void addTile(TileBoardIterator iter, Tile tile, boolean checkValid)
			throws BoardPositionFilledException, NotValidPlacementException;

	// Removes tile from the specified TileBoardIterator location
	// public Tile removeTile(TileBoardIterator iter);

	/**
	 * @param iter
	 *            - position where temp tile is to be added
	 * @param tile
	 *            - temp tile to be added
	 * @throws NotValidPlacementException 
	 */
	public void addTemp(TileBoardIterator iter, Tile tile)
			throws BoardPositionFilledException, NotValidPlacementException;

	/**
	 * removes all temp tiles from board
	 */
	public void removeTemps();

	/**
	 * @param iter
	 *            - location at which tile should be made permanent
	 */
	public void removeTempStatus(TileBoardIterator iter);

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
