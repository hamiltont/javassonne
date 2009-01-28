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

	/**
	 * @param data - reference to TileBoard where data is stored
	 * @param intPair - IntPair location of current location
	 */
	public TileBoardGenIterator(TileBoard data, IntPair intPair) {
		data_ = data;
		location_ = intPair;
	}

	/**
	 * @param old - TileBoardIterator to copy from
	 */
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

	/**
	 * @return - moves iterator down in board and returns reference to itself 
	 */
	public TileBoardIterator down() {
		// previousLocation_ = location_;
		location_ = new IntPair(location_.car() + 1, location_.cdr());
		return this;
	}

	/**
	 * @return - returns reference to copy of iterator, moved down
	 */
	public TileBoardIterator downCopy() {
		return (new TileBoardGenIterator(this)).down();
	}

	/**
	 * @return - moves iterator left in board and returns reference to itself 
	 */
	public TileBoardIterator left() {
		// previousLocation_ = location_;
		location_ = new IntPair(location_.car(), location_.cdr() - 1);
		return this;
	}

	/**
	 * @return - returns reference to copy of iterator, moved left
	 */
	public TileBoardIterator leftCopy() {
		return (new TileBoardGenIterator(this)).left();
	}

	/* (non-Javadoc)
	 * @see org.javassonne.model.TileBoardIterator#right()
	 */
	public TileBoardIterator right() {
		// previousLocation_ = location_;
		location_ = new IntPair(location_.car(), location_.cdr() + 1);
		return this;
	}

	/**
	 * @return - returns reference to copy of iterator, moved right
	 */
	public TileBoardIterator rightCopy() {
		return (new TileBoardGenIterator(this)).right();
	}

	/**
	 * @return - moves iterator up in board and returns reference to itself
	 */
	public TileBoardIterator up() {
		// previousLocation_ = location_;
		location_ = new IntPair(location_.car() - 1, location_.cdr());
		return this;
	}

	/**
	 * @return - returns reference to copy of iterator, moved up
	 */
	public TileBoardIterator upCopy() {
		return (new TileBoardGenIterator(this)).up();
	}

	
	
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
				|| location_.cdr() < data_.getUpperLeftCorner().getLocation().cdr()
				|| location_.car() > data_.getLowerRightCorner().getLocation().car()
				|| location_.cdr() > data_.getLowerRightCorner().getLocation().cdr()
				|| this == data_.getUpperLeftCorner() 
				|| this == data_.getLowerRightCorner())
			return true;
		return false;
	}

	// Advances iterator to start of next row
	/* (non-Javadoc)
	 * @see org.javassonne.model.TileBoardIterator#nextRow()
	 */
	public TileBoardIterator nextRow() {
		// previousLocation_ = location_;
		location_ = new IntPair(location_.car() + 1, data_.getUpperLeftCorner()
				.getLocation().cdr());
		return this;
	}

	/**
	 * @return - returns reference to copy of iterator, moved to start of next row
	 */
	public TileBoardIterator nextRowCopy() {
		return (new TileBoardGenIterator(this)).nextRow();
	}

	/* (non-Javadoc)
	 * @see org.javassonne.model.TileBoardIterator#getData()
	 */
	public TileBoard getData() {
		return data_;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof TileBoardIterator))
			return false;
		else
			return (this.data_.equals(((TileBoardIterator) obj).getData()) && 
					this.location_.equals(((TileBoardIterator) obj).getLocation()));
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return this.toString().hashCode();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("%s:%s",data_.toString(),location_.toString());
	}

}
