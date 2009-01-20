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

public class TileBoardIterator {

	private TileBoard data_;
	private IntPair location_;
	private IntPair previousLocation_;

	public TileBoardIterator(TileMapBoard tileMapContainer, IntPair intPair) {
		data_ = tileMapContainer;
		location_ = intPair;
	}

	public TileBoardIterator(TileBoardIterator old) {
		data_ = old.data_;
		location_ = old.location_;
		previousLocation_ = old.previousLocation_;
	}

	public Tile current() {
		return data_.getTile(this);
	}

	public void down() {
		previousLocation_ = location_;
		location_ = new IntPair(location_.car() + 1, location_.cdr());
	}

	public void left() {
		previousLocation_ = location_;
		location_ = new IntPair(location_.car(), location_.cdr() - 1);
	}

	public void right() {
		previousLocation_ = location_;
		location_ = new IntPair(location_.car(), location_.cdr() + 1);
	}

	public void up() {
		previousLocation_ = location_;
		location_ = new IntPair(location_.car() - 1, location_.cdr());
	}

	// Moves iterator to previous location (history size of 1)
	public void back() throws Exception {
		if (previousLocation_ != null) {
			location_ = previousLocation_;
			previousLocation_ = null;
		} else
			throw new Exception();
		// TODO: implement Exception
	}

	public IntPair getLocation() {
		return location_;
	}

	public boolean outOfBounds() {
		if (location_.car() < data_.getUpperLeftCorner().location_.car()
				|| location_.cdr() < data_.getUpperLeftCorner().location_.cdr()
				|| location_.car() > data_.getUpperLeftCorner().location_.car()
				|| location_.cdr() > data_.getUpperLeftCorner().location_.cdr())
			return true;
		return false;
	}

	// Advances iterator to start of next row
	public void nextRow() {
		location_ = new IntPair(location_.car() + 1,
				data_.getUpperLeftCorner().location_.cdr());
	}

}
