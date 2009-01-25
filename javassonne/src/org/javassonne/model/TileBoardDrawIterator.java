/**
 * Javassonne 
 *  http://code.google.com/p/javassonne/
 * 
 * @author Kyle Prete
 * @date Jan 25, 2009
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

//THIS IS NOT DONE!!!
// TODO: add comments, and test

public class TileBoardDrawIterator implements TileBoardIterator  {
	private TileBoard data_;
	private IntPair location_;

	public TileBoardDrawIterator(TileBoard data, IntPair intPair) {
		data_ = data;
		location_ = intPair;
	}
	
	public TileBoardDrawIterator(TileBoardIterator old) {
		data_ = old.getData();
		location_ = old.getLocation();
	}

	public Tile current() {
		return data_.getTile(this);
	}

	public IntPair getLocation() {
		return location_;
	}

	public boolean outOfBounds() {
		if (location_.car() < data_.getUpperLeftCorner().getLocation().car()
				|| location_.cdr() < data_.getUpperLeftCorner().getLocation().cdr()
				|| location_.car() > data_.getUpperLeftCorner().getLocation().car()
				|| location_.cdr() > data_.getUpperLeftCorner().getLocation().cdr())
			return true;
		return false;
	}

	// Advances iterator to start of next row
	public TileBoardDrawIterator nextRow() {
//		previousLocation_ = location_;
		location_ = new IntPair(location_.car() + 1,
				data_.getUpperLeftCorner().getLocation().cdr());
		return this;
		
	}

	public TileBoard getData() {
		return data_;
	}

	public TileBoardIterator right() {
		// TODO Auto-generated method stub
		//THIS SHOULD NOT BE HERE>>>
		return null;
	}
}
