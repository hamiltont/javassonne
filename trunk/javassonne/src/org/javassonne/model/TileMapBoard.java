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

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;

import org.javassonne.model.Tile.Region;

/**
 * @author pretekr
 * 
 */
public class TileMapBoard implements TileBoard {

	private HashMap<Point, Tile> data_;
	private TileBoardGenIterator upperLeft_;
	private TileBoardGenIterator lowerRight_;
	private ArrayList<Point> tempTileLocations_;

	/**
	 * @param homeTile
	 *            - Tile to insert at home location to start board
	 */
	public TileMapBoard(Tile homeTile) {
		data_ = new HashMap<Point, Tile>();
		upperLeft_ = new TileBoardGenIterator(this, new Point(-1, 1));
		lowerRight_ = new TileBoardGenIterator(this, new Point(1, -1));
		data_.put(new Point(0, 0), homeTile);
		tempTileLocations_ = new ArrayList<Point>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.javassonne.model.TileBoard#homeTile()
	 */
	public TileBoardIterator homeTile() {
		return new TileBoardGenIterator(this, new Point(0, 0));
	}

	/**
	 * @param iter
	 *            - TileBoardIterator pointing to location queried
	 * @return - true if TileBoardIterator location is filled in TileBoard
	 */
	public boolean positionFilled(TileBoardIterator iter) {
		return data_.containsKey(iter.getLocation());
	}

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
			throws BoardPositionFilledException, NotValidPlacementException {
		addTile(iter, tile, true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.javassonne.model.TileBoard#addTile(org.javassonne.model.TileBoardIterator
	 * , org.javassonne.model.Tile)
	 */
	public void addTile(TileBoardIterator iter, Tile tile, boolean checkValid)
			throws BoardPositionFilledException, NotValidPlacementException {
		if (positionFilled(iter))
			throw new BoardPositionFilledException(iter.getLocation());
		else if (checkValid && !isValidPlacement(iter, tile))
			throw new NotValidPlacementException(iter.getLocation());
		else {
			data_.put(iter.getLocation(), tile);
			// Check for bounds extension
			if (iter.getLocation().getX() == upperLeft_.getLocation().getX())
				upperLeft_.left();
			else if (iter.getLocation().getY() == upperLeft_.getLocation()
					.getY())
				upperLeft_.up();
			else if (iter.getLocation().getX() == lowerRight_.getLocation()
					.getX())
				lowerRight_.right();
			else if (iter.getLocation().getY() == lowerRight_.getLocation()
					.getY())
				lowerRight_.down();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.javassonne.model.TileBoard#getTile(org.javassonne.model.TileBoardIterator
	 * )
	 */
	public Tile getTile(TileBoardIterator iter) {
		return data_.get(iter.getLocation());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.javassonne.model.TileBoard#getLowerRightCorner()
	 */
	public TileBoardIterator getLowerRightCorner() {
		return new TileBoardGenIterator(lowerRight_);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.javassonne.model.TileBoard#getUpperLeftCorner()
	 */
	public TileBoardIterator getUpperLeftCorner() {
		return new TileBoardGenIterator(upperLeft_);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.javassonne.model.TileBoard#isValidPlacement(org.javassonne.model.
	 * TileBoardIterator, org.javassonne.model.Tile)
	 */
	public boolean isValidPlacement(TileBoardIterator iter, Tile tile) {
		if (iter.outOfBounds())
			return false;
		else if (positionFilled(iter))
			return false;
		else {
			TileBoardGenIterator localIter = new TileBoardGenIterator(iter);
			Tile left = localIter.leftCopy().current();
			if (left != null
					&& left.featureIdentifierInRegion(Region.Right) != tile
							.featureIdentifierInRegion(Region.Left))
				return false;
			Tile top = localIter.upCopy().current();
			if (top != null
					&& top.featureIdentifierInRegion(Region.Bottom) != tile
							.featureIdentifierInRegion(Region.Top))
				return false;
			Tile right = localIter.rightCopy().current();
			if (right != null
					&& right.featureIdentifierInRegion(Region.Left) != tile
							.featureIdentifierInRegion(Region.Right))
				return false;
			Tile bottom = localIter.downCopy().current();
			if (bottom != null
					&& bottom.featureIdentifierInRegion(Region.Top) != tile
							.featureIdentifierInRegion(Region.Bottom))
				return false;
			// else
			return true;
		}
	}

	
	/**
	 * @param iter
	 * @param tile
	 * @param checkValid
	 * @throws BoardPositionFilledException
	 * @throws NotValidPlacementException
	 */
	public void addTemp(TileBoardIterator iter, Tile tile, boolean checkValid)
			throws BoardPositionFilledException, NotValidPlacementException {

		addTile(iter, tile, checkValid);
		tempTileLocations_.add(iter.getLocation());

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.javassonne.model.TileBoard#addTemp(org.javassonne.model.TileBoardIterator
	 * , org.javassonne.model.Tile)
	 */
	public void addTemp(TileBoardIterator iter, Tile tile)
			throws BoardPositionFilledException, NotValidPlacementException {
		addTemp(iter, tile, true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.javassonne.model.TileBoard#removeTemps()
	 */
	public void removeTemps() {
		for (Point i : tempTileLocations_)
			data_.remove(i);
		tempTileLocations_.clear();
		fixBoundaries();
	}

	private void fixBoundaries() {
		fixLeft();
		fixTop();
		fixBot();
		fixRight();
	}

	private void fixRight() {
		TileBoardGenIterator temp = (TileBoardGenIterator) lowerRight_
				.leftCopy();
		boolean notFound = true;
		// look for a tile, stop if you reach bottom edge
		while (notFound
				&& temp.getLocation().getY() < upperLeft_.getLocation().getY()) {
			// First place will not have tile
			if (temp.up().current() != null)
				// found one
				notFound = false;
		}
		if (notFound)
			upperLeft_.left();
	}

	private void fixBot() {
		TileBoardGenIterator temp = (TileBoardGenIterator) lowerRight_.upCopy();
		boolean notFound = true;
		// look for a tile, stop if you reach bottom edge
		while (notFound
				&& temp.getLocation().getX() > upperLeft_.getLocation().getX()) {
			// First place will not have tile
			if (temp.left().current() != null)
				// found one
				notFound = false;
		}
		if (notFound)
			upperLeft_.up();

	}

	private void fixTop() {
		TileBoardGenIterator temp = (TileBoardGenIterator) upperLeft_
				.downCopy();
		boolean notFound = true;
		// look for a tile, stop if you reach right edge
		while (notFound
				&& temp.getLocation().getX() < lowerRight_.getLocation().getX()) {
			// First place will not have tile
			if (temp.right().current() != null)
				// found one
				notFound = false;
		}
		if (notFound)
			upperLeft_.down();
	}

	private void fixLeft() {
		TileBoardGenIterator temp = (TileBoardGenIterator) upperLeft_
				.rightCopy();
		boolean notFound = true;
		// look for a tile, stop if you reach bottom edge
		while (notFound
				&& temp.getLocation().getY() > lowerRight_.getLocation().getY()) {
			// First place will not have tile
			if (temp.down().current() != null)
				// found one
				notFound = false;
		}
		if (notFound)
			upperLeft_.right();
	}

	public void removeTempStatus(TileBoardIterator iter) {
		tempTileLocations_.remove(iter.getLocation());
	}

}
