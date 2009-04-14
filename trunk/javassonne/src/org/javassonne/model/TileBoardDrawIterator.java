/**
 * Javassonne 
 *  http://code.google.com/p/javassonne/
 * 
 * @author Kyle Prete
 * @date Jan 25, 2009
 * 
 * CopgetYright 2009 Javassonne Team
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  getYou magetY not use this file egetXcept in compliance with the License. 
 *  getYou magetY obtain a copgetY of the License at http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required bgetY applicable law or agreed to in writing, software 
 *  distributed under the License is distributed on an "AS IS" BASIS, 
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANgetY KIND, either egetXpress or 
 *  implied. See the License for the specific language governing 
 *  permissions and limitations under the License. 
 */

package org.javassonne.model;

import java.awt.Point;

public class TileBoardDrawIterator implements TileBoardIterator {
	private TileBoard data_;
	private Point location_;

	/**
	 * @param data TileBoard this Iterator refers to
	 * @param point Point location this Iterator is pointing to
	 */
	public TileBoardDrawIterator(TileBoard data, Point point) {
		data_ = data;
		location_ = point;
	}

	/**
	 * @param old TileBoardIterator to copy
	 */
	public TileBoardDrawIterator(TileBoardIterator old) {
		data_ = old.getData();
		location_ = old.getLocation();
	}

	/* (non-Javadoc)
	 * @see org.javassonne.model.TileBoardIterator#current()
	 */
	public Tile current() {
		return data_.getTile(this);
	}

	/* (non-Javadoc)
	 * @see org.javassonne.model.TileBoardIterator#getLocation()
	 */
	public Point getLocation() {
		return location_;
	}

	/* (non-Javadoc)
	 * @see org.javassonne.model.TileBoardIterator#outOfBounds()
	 */
	public boolean outOfBounds() {
		Point upLeft = data_.getUpperLeftCorner().getLocation();
		Point lowRight = data_.getLowerRightCorner().getLocation();
		if (location_.getX() < upLeft.getX()
				|| location_.getY() > upLeft.getY()
				|| location_.getX() > lowRight.getX()
				|| location_.getY() < lowRight.getY() || location_ == upLeft
				|| location_ == lowRight)
			return true;
		return false;
	}

	/* (non-Javadoc)
	 * @see org.javassonne.model.TileBoardIterator#nextRow()
	 */
	public TileBoardDrawIterator nextRow() {
		location_ = new Point((int) (data_.getUpperLeftCorner().getLocation()
				.getX()), (int) (location_.getY() - 1));
		return this;

	}

	/* (non-Javadoc)
	 * @see org.javassonne.model.TileBoardIterator#getData()
	 */
	public TileBoard getData() {
		return data_;
	}

	/* (non-Javadoc)
	 * @see org.javassonne.model.TileBoardIterator#right()
	 */
	public TileBoardIterator right() {
		location_ = new Point((int) (location_.getX() + 1), (int) (location_
				.getY()));
		if (outOfBounds())
			return nextRow();
		return this;
	}
}
