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
	private TileBoardGenIterator upperLeft_;
	private TileBoardGenIterator lowerRight_;
	
	/**
	 * @param homeTile - Tile to insert at home location to start board
	 */
	public TileMapBoard(Tile homeTile) {
		data_ = new HashMap<IntPair, Tile>();
		upperLeft_ = new TileBoardGenIterator(this, new IntPair(-1, -1));
		lowerRight_ = new TileBoardGenIterator(this, new IntPair(1, 1));
		data_.put(new IntPair(0,0), homeTile);
	}

	/* (non-Javadoc)
	 * @see org.javassonne.model.TileBoard#homeTile()
	 */
	public TileBoardIterator homeTile() {
		return new TileBoardGenIterator(this, new IntPair(0, 0));
	}

	/**
	 * @param iter - TileBoardIterator pointing to location queried
	 * @return - true if TileBoardIterator location is filled in TileBoard
	 */
	public boolean positionFilled(TileBoardIterator iter) {
		return data_.containsKey(iter.getLocation());
	}

	/* (non-Javadoc)
	 * @see org.javassonne.model.TileBoard#addTile(org.javassonne.model.TileBoardIterator, org.javassonne.model.Tile)
	 */
	public void addTile(TileBoardIterator iter, Tile tile) throws Exception {
		if (positionFilled(iter))
			throw new Exception();
		else
		{
			data_.put(iter.getLocation(), tile);
			//Check for bounds extension
			if (iter.getLocation().car() == upperLeft_.getLocation().car())
				upperLeft_.up();
			else if (iter.getLocation().cdr() == upperLeft_.getLocation().cdr())
				upperLeft_.left();
			else if (iter.getLocation().car() == lowerRight_.getLocation().car())
				lowerRight_.down();
			else if (iter.getLocation().cdr() == lowerRight_.getLocation().cdr())
				lowerRight_.right();
		}
	}

	/* (non-Javadoc)
	 * @see org.javassonne.model.TileBoard#getTile(org.javassonne.model.TileBoardIterator)
	 */
	public Tile getTile(TileBoardIterator iter) {
		return data_.get(iter.getLocation());
	}

	/* (non-Javadoc)
	 * @see org.javassonne.model.TileBoard#getLowerRightCorner()
	 */
	public TileBoardIterator getLowerRightCorner() {
		return new TileBoardGenIterator(lowerRight_);
	}

	/* (non-Javadoc)
	 * @see org.javassonne.model.TileBoard#getUpperLeftCorner()
	 */
	public TileBoardIterator getUpperLeftCorner() {
		return new TileBoardGenIterator(upperLeft_);
	}

}
