/**
 * Javassonne 
 *  http://code.google.com/p/javassonne/
 * 
 * @author Kyle Prete
 * @date Apr 1, 2009
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

package org.javassonne.algorithms;

import java.awt.Point;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.javassonne.model.Meeple;
import org.javassonne.model.Tile;
import org.javassonne.model.TileBoardGenIterator;
import org.javassonne.model.TileBoardIterator;
import org.javassonne.model.Tile.Quadrant;
import org.javassonne.ui.GameState;

/**
 * Calculates Quadrant scores
 * 
 * A QuadCalc should be thrown away if Tiles are being added to board because it
 * does not update cached data
 * 
 * TODO: Enforce this by listening for board_changed events?
 */
public class QuadCalc {

	public QuadCalc() {
		// Initialize data structures
		numCastles_ = new HashMap<Point, EnumMap<Tile.Quadrant, Integer>>();
		globalMeep_ = new HashMap<Point, EnumMap<Tile.Quadrant, List<Meeple>>>();
	}

	/*
	 * External function for calculating farm values for given quadrant and
	 * connecting quadrants. Delegates most of calculation to private recursive
	 * function.
	 */
	public void traverseQuadrant(TileBoardIterator iter, Tile.Quadrant quad) {
		// Initialize recursion variables for compounding list of
		// tile-quadrants, etc.
		HashMap<Point, ArrayList<Tile.Quadrant>> list = new HashMap<Point, ArrayList<Tile.Quadrant>>();
		ArrayList<Meeple> meeps = new ArrayList<Meeple>();
		// Call recursive function
		traverseQuadrant(iter, quad, meeps, list);

		int total = 0;
		// This is ugly, but we have to calculate how many contiguous regions
		// are connected to this quadrant area.
		RegionsCalc c = new RegionsCalc();

		// For each Tile in our Quadrant area
		for (Point p : list.keySet()) {
			// Make an iterator to it
			TileBoardIterator iterNew = new TileBoardGenIterator(GameState
					.getInstance().getBoard(), p);
			// For each quadrant on this Tile in our Quadrant area
			for (Tile.Quadrant q : list.get(p)) {

				for (Tile.Region r : Tile.Region.values()) {
					/*
					 * If the region is not null and it would get points from a
					 * farm and we have not yet visited it with our RegionsCalc
					 * (to stop double-counting of regions) and it is adjacent
					 * to our current quadrant, traverse it in the RegionsCalc
					 * ...
					 */
					if (iterNew.current().featureInRegion(r) != null
							&& iterNew.current().featureInRegion(r).farmPointValue != 0
							&& c.getScoreOfRegion(p, r) == -1
							&& r.isAdjacentTo(q)) {
						c.traverseRegion(iterNew, r);
						// ... and add its points if it is complete
						if (c.getRegionCompletion(p, r))
							total += iterNew.current().featureInRegion(r).farmPointValue;
					}
				}
			}
		}

		/*
		 * Now we update our global data structures This update will do nothing
		 * if we've already touched the entrance quadrant because the recursive
		 * function will have returned without modifying list or meeps
		 */
		for (Point p : list.keySet()) {
			// Make sure we don't try to put in a null data structure
			if (numCastles_.get(p) == null)
				numCastles_.put(p, new EnumMap<Tile.Quadrant, Integer>(
						Tile.Quadrant.class));
			if (globalMeep_.get(p) == null)
				globalMeep_.put(p, new EnumMap<Tile.Quadrant, List<Meeple>>(
						Tile.Quadrant.class));

			for (Tile.Quadrant q : list.get(p)) {
				numCastles_.get(p).put(q, total);
				globalMeep_.get(p).put(q, meeps);
			}
		}
		return;

	}

	// Private recursive function for calculating Quadrant values
	private void traverseQuadrant(TileBoardIterator iter, Quadrant quad,
			ArrayList<Meeple> meeps, HashMap<Point, ArrayList<Quadrant>> list) {
		// Base case 1: off the edge of the board
		if (iter.current() == null)
			return;
		// Base case 2: we've already touched this quadrant
		if (getNumCastles(iter.getLocation(), quad) != -1)
			return;

		// else

		// "touch" this quadrant globally...
		if (numCastles_.get(iter.getLocation()) == null)
			numCastles_.put(iter.getLocation(),
					new EnumMap<Tile.Quadrant, Integer>(Tile.Quadrant.class));
		numCastles_.get(iter.getLocation()).put(quad, 0);
		// ... and locally
		if (list.get(iter.getLocation()) == null)
			list.put(iter.getLocation(), new ArrayList<Tile.Quadrant>());
		list.get(iter.getLocation()).add(quad);

		// If there's a merson here, add it to our local list
		Meeple current = iter.current().meepleInQuadrant(quad);
		if (current != null)
			meeps.add(current);

		// Temp store walls on the sides of this Tile - see Tile docs for more
		// info
		boolean leftWall = iter.current().farmWallInRegion(Tile.Region.Left);
		boolean upWall = iter.current().farmWallInRegion(Tile.Region.Top);
		boolean rightWall = iter.current().farmWallInRegion(Tile.Region.Right);
		boolean downWall = iter.current().farmWallInRegion(Tile.Region.Bottom);
		// traverse to next Tile(s) from edges of current quadrant
		if (quad.equals(Tile.Quadrant.TopLeft)) {
			if (!leftWall)
				traverseQuadrant(((TileBoardGenIterator) iter).leftCopy(),
						Tile.Quadrant.TopRight, meeps, list);
			if (!upWall)
				traverseQuadrant(((TileBoardGenIterator) iter).upCopy(),
						Tile.Quadrant.BottomLeft, meeps, list);
		} else if (quad.equals(Tile.Quadrant.TopRight)) {
			if (!rightWall)
				traverseQuadrant(((TileBoardGenIterator) iter).rightCopy(),
						Tile.Quadrant.TopLeft, meeps, list);
			if (!upWall)
				traverseQuadrant(((TileBoardGenIterator) iter).upCopy(),
						Tile.Quadrant.BottomRight, meeps, list);
		} else if (quad.equals(Tile.Quadrant.BottomLeft)) {
			if (!leftWall)
				traverseQuadrant(((TileBoardGenIterator) iter).leftCopy(),
						Tile.Quadrant.BottomRight, meeps, list);
			if (!downWall)
				traverseQuadrant(((TileBoardGenIterator) iter).downCopy(),
						Tile.Quadrant.TopLeft, meeps, list);
		} else if (quad.equals(Tile.Quadrant.BottomRight)) {
			if (!rightWall)
				traverseQuadrant(((TileBoardGenIterator) iter).rightCopy(),
						Tile.Quadrant.BottomLeft, meeps, list);
			if (!downWall)
				traverseQuadrant(((TileBoardGenIterator) iter).downCopy(),
						Tile.Quadrant.TopRight, meeps, list);
		}

		// traverse to other quadrants on this Tile that connect to the current
		// (See Tile doc for info on quadrant connectivity)
		int currentQuad = iter.current().farmInQuadrant(quad);
		for (Tile.Quadrant q : Tile.Quadrant.values()) {
			if (iter.current().farmInQuadrant(q) == currentQuad)
				traverseQuadrant(iter, q, meeps, list);
		}

		return;
	}

	/*
	 * If traverseQuadrant has touched given Quadrant of Tile at given location,
	 * this function returns the num of regions "fed" by the Quadrant area,
	 * else, returns -1
	 */
	public Integer getNumCastles(Point loc, Tile.Quadrant quad) {
		Map<Tile.Quadrant, Integer> tileQuadrants = numCastles_.get(loc);
		if (tileQuadrants == null)
			return -1;

		Integer temp = tileQuadrants.get(quad);
		if (temp == null)
			return -1;

		return temp;
	}

	/*
	 * If traverseQuadrant has touched given Quadrant of Tile at given location
	 * and it has a nonempty meeple list claiming it, this function returns the
	 * list of meeple, else returns empty list
	 */
	public List<Meeple> getMeepleList(Point loc, Tile.Quadrant quad) {
		ArrayList<Meeple> returnVal = new ArrayList<Meeple>();
		Map<Tile.Quadrant, List<Meeple>> tileQuadrants = globalMeep_.get(loc);
		if (tileQuadrants == null)
			return returnVal;

		List<Meeple> temp = tileQuadrants.get(quad);
		if (temp == null)
			return returnVal;

		returnVal.addAll(temp);
		return returnVal;
	}

	/*
	 * Keeps track of touched locations. These store data collected when
	 * traversing and make it available to the accessors. Also, the recursive
	 * function can quit if we've already traversed this quadrant with this
	 * calculator - this saves time if the function is accidentally called twice
	 * (i.e. easier code to traverse all), but causes problems if the data is
	 * dirty. Therefore, a QuadCalc should be thrown away if Tiles are being
	 * added to board.
	 */
	private HashMap<Point, EnumMap<Tile.Quadrant, Integer>> numCastles_;
	private HashMap<Point, EnumMap<Tile.Quadrant, List<Meeple>>> globalMeep_;

}
