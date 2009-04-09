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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.javassonne.model.Tile.Region;
import org.javassonne.ui.GameState;

/**
 * @author pretekr
 * 
 */
// TODO A class description is really required here. It would also be REALLY
// useful to have some example code here, or how you expect this class to be
// used
public class TileMapBoard implements TileBoard {

	// TODO I changed the format here to make this easier to read
	// Some descriptions on the members would be helpful (what is
	// tempTileLocations for, without reading the code?)
	private HashMap<Point, Tile> data_;
	private TileBoardGenIterator upperLeft_;
	private TileBoardGenIterator lowerRight_;
	private HashSet<Point> tempTileLocations_;

	public TileMapBoard() {
		upperLeft_ = new TileBoardGenIterator(this, new Point(-1, 1));
		lowerRight_ = new TileBoardGenIterator(this, new Point(1, -1));
		tempTileLocations_ = new HashSet<Point>();

		data_ = new HashMap<Point, Tile>();
		data_.put(new Point(0, 0), GameState.getInstance().getDeck().homeTile());
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
	 *            TileBoardIterator pointing to location queried
	 * @return true if TileBoardIterator location is filled in TileBoard
	 */
	public boolean positionFilled(TileBoardIterator iter) {
		return data_.containsKey(iter.getLocation());
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
		else {
			TileBoardGenIterator localIter = new TileBoardGenIterator(iter);
			// iterate left and check if features match
			Tile left = localIter.leftCopy().current();
			Tile top = localIter.upCopy().current();
			Tile right = localIter.rightCopy().current();
			Tile bottom = localIter.downCopy().current();
			
			// we want to make sure the tiles we're looking at in the adjacent
			// squares are not temp. tiles. This could lead to illeigal "feature
			// bindings" and this function returning false unnecessarily.
			if (tempTileLocations_.contains(localIter.leftCopy().getLocation()))
				left = null;
			if (tempTileLocations_.contains(localIter.rightCopy().getLocation()))
				right = null;
			if (tempTileLocations_.contains(localIter.upCopy().getLocation()))
				top = null;
			if (tempTileLocations_.contains(localIter.downCopy().getLocation()))
				bottom = null;
		
			TileFeatureBindings b = GameState.getInstance().getDeck().tileFeatureBindings();
			
			if (left != null && !b.featuresBind(left
							.featureInRegion(Region.Right), tile
							.featureInRegion(Region.Left)))
				return false;
			// iterate up and check if features match
			if (top != null && !b.featuresBind(top
							.featureInRegion(Region.Bottom), tile
							.featureInRegion(Region.Top)))
				return false;
			// iterate right and check if features match
			if (right != null && !b.featuresBind(right
							.featureInRegion(Region.Left), tile
							.featureInRegion(Region.Right)))
				return false;
			// iterate down and check if features match
			if (bottom != null && !b.featuresBind(bottom
							.featureInRegion(Region.Top), tile
							.featureInRegion(Region.Bottom)))
				return false;
			// make sure at least one of the adjacent tiles exists
			if (left == null && top == null && right == null && bottom == null)
				return false;
			// else
			return true;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.javassonne.model.TileBoard#addTemp(org.javassonne.model.TileBoardIterator
	 * , org.javassonne.model.Tile, boolean)
	 */
	public void addTemp(TileBoardIterator iter, Tile tile)
			throws BoardPositionFilledException {

		if (positionFilled(iter))
			throw new BoardPositionFilledException(iter.getLocation());
		else {
			tempTileLocations_.add(iter.getLocation());
			data_.put(iter.getLocation(), tile);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.javassonne.model.TileBoard#addTemps(java.util.Set,
	 * org.javassonne.model.Tile)
	 */
	public void addTemps(Set<TileBoardIterator> iters, Tile t)
			throws BoardPositionFilledException {
		// Check for conflicts first
		for (TileBoardIterator iter : iters) {
			if (positionFilled(iter))
				throw new BoardPositionFilledException(iter.getLocation());
		}
		// now add temps
		for (TileBoardIterator iter : iters) {
			tempTileLocations_.add(iter.getLocation());
			data_.put(iter.getLocation(), t);
		}

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
		// fixBoundaries();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.javassonne.model.TileBoard#removeTempStatus(org.javassonne.model.
	 * TileBoardIterator)
	 */
	public void removeTempStatus(TileBoardIterator iter)
			throws NotValidPlacementException {
		if (iter.current() == null)
			return;
		if (!isValidPlacement(iter, iter.current()))
			throw new NotValidPlacementException(iter.getLocation());
		tempTileLocations_.remove(iter.getLocation());
		// Check for bounds extension
		if (iter.getLocation().getX() == upperLeft_.getLocation().getX())
			upperLeft_.left();
		else if (iter.getLocation().getY() == upperLeft_.getLocation().getY())
			upperLeft_.up();
		else if (iter.getLocation().getX() == lowerRight_.getLocation().getX())
			lowerRight_.right();
		else if (iter.getLocation().getY() == lowerRight_.getLocation().getY())
			lowerRight_.down();
		// else, no bounds extension occurs
	}

	public Set<TileBoardIterator> possiblePlacements(Tile t) {
		Set<TileBoardIterator> locations = new HashSet<TileBoardIterator>();
		TileBoardDrawIterator current;
		Tile local = new Tile(t);
		// 4 rotations
		for (int i = 0; i < 4; ++i) {
			current = new TileBoardDrawIterator(this.upperLeft_.rightCopy());
			while (!current.outOfBounds()) {
				if (!positionFilled(current)
						&& isValidPlacement(current, local))
					locations.add(new TileBoardDrawIterator(current));
				current.right();
			}
			local.rotateRight();
		}
		return locations;
	}

	public void removeTempAtLocation(TileBoardIterator iter) {
		//if it exists as a temp location, remove it, else do nothing
		if (tempTileLocations_.remove(iter.getLocation())) 
			data_.remove(iter.getLocation());
	}

}
