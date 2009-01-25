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

public class TileBoardGenIterator implements TileBoardIterator {

	private TileBoard data_;
	private IntPair location_;

	// private IntPair previousLocation_;

	
	public TileBoardGenIterator(TileBoard data, IntPair intPair) {
		data_ = data;
		location_ = intPair;
	}

	public TileBoardGenIterator(TileBoardIterator old) {
		data_ = old.getData();
		location_ = old.getLocation();
		// previousLocation_ = old.previousLocation_;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.javassonne.model.TileBoardIterator#current()
	 */
	public Tile current() {
		return data_.getTile(this);
	}

	public TileBoardIterator down() {
		// previousLocation_ = location_;
		location_ = new IntPair(location_.car() + 1, location_.cdr());
		return this;
	}

	public TileBoardIterator downCopy() {
		return new TileBoardGenIterator(data_, new IntPair(location_.car() + 1,
				location_.cdr()));
	}

	public TileBoardIterator left() {
		// previousLocation_ = location_;
		location_ = new IntPair(location_.car(), location_.cdr() - 1);
		return this;
	}

	public TileBoardIterator leftCopy() {
		return new TileBoardGenIterator(data_, new IntPair(location_.car(),
				location_.cdr() - 1));
	}

	public TileBoardIterator right() {
		// previousLocation_ = location_;
		location_ = new IntPair(location_.car(), location_.cdr() + 1);
		return this;
	}

	public TileBoardIterator rightCopy() {
		return new TileBoardGenIterator(data_, new IntPair(location_.car(),
				location_.cdr() + 1));
	}

	public TileBoardIterator up() {
		// previousLocation_ = location_;
		location_ = new IntPair(location_.car() - 1, location_.cdr());
		return this;
	}

	public TileBoardIterator upCopy() {
		return new TileBoardGenIterator(data_, new IntPair(location_.car() - 1,
				location_.cdr()));
	}

	/*
	 * // Moves iterator to previous location (history size of 1) public
	 * TileBoardIterator back() throws Exception { if (previousLocation_ !=
	 * null) { location_ = previousLocation_; previousLocation_ = null; return
	 * this; } else throw new Exception(); // TODO: implement Exception }
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.javassonne.model.TileBoardIterator#getLocation()
	 */
	public IntPair getLocation() {
		return location_;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.javassonne.model.TileBoardIterator#outOfBounds()
	 */
	public boolean outOfBounds() {
		if (location_.car() < data_.getUpperLeftCorner().getLocation().car()
				|| location_.cdr() < data_.getUpperLeftCorner().getLocation()
						.cdr()
				|| location_.car() > data_.getUpperLeftCorner().getLocation()
						.car()
				|| location_.cdr() > data_.getUpperLeftCorner().getLocation()
						.cdr())
			return true;
		return false;
	}

	// Advances iterator to start of next row
	public TileBoardIterator nextRow() {
		// previousLocation_ = location_;
		location_ = new IntPair(location_.car() + 1, data_.getUpperLeftCorner()
				.getLocation().cdr());
		return this;
	}

	// Advances iterator to start of next row
	public TileBoardIterator nextRowCopy() {
		return new TileBoardGenIterator(data_, new IntPair(location_.car() + 1,
				data_.getUpperLeftCorner().getLocation().cdr()));
	}

	public TileBoard getData() {
		return data_;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof TileBoardIterator))
			return false;
		else
			return (this.data_.equals(((TileBoardIterator) obj).getData()) && 
					this.location_.equals(((TileBoardIterator) obj).getLocation()));
	}

	@Override
	public int hashCode() {
		return this.toString().hashCode();
	}

	@Override
	public String toString() {
		return String.format("%s:%s",data_.toString(),location_.toString());
	}

}
