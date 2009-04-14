/**
 * Javassonne 
 *  http://code.google.com/p/javassonne/
 * 
 * @author Kyle Prete
 * @date Mar 22, 2009
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
import org.javassonne.model.TileFeatureBindings;
import org.javassonne.model.Tile.Region;
import org.javassonne.ui.GameState;

/**
 * Calculates Region Scores
 * 
 * Note: If the board this RegionsCalc is indirectly attached to is changed, the
 * RegionsCalc must be destroyed as all its data is corrupt - cached data is not
 * updated.
 * 
 * TODO: Enforce this by listening for board_changed events?
 */
public class RegionsCalc {

	public RegionsCalc() {
		// Initialize data structures
		scoreOfReg_ = new HashMap<Point, EnumMap<Tile.Region, Integer>>();
		globalMeep_ = new HashMap<Point, EnumMap<Tile.Region, List<Meeple>>>();
		isComplete_ = new HashMap<Point, EnumMap<Tile.Region, Boolean>>();

	}

	/*
	 * External function for calculating region values for given region and
	 * connecting regions. Delegates most of calculation to private recursive
	 * function.
	 */
	public void traverseRegion(TileBoardIterator iter, Tile.Region reg) {
		// Initialize recursion variables for compounding list of
		// tile-regions, etc.
		Map<Point, ArrayList<Tile.Region>> list = new HashMap<Point, ArrayList<Tile.Region>>();
		List<Meeple> meeps = new ArrayList<Meeple>();
		int tempScore = 0;

		// Call recursive function - returnVal is whether or not region is
		// completed.
		boolean returnVal = traverseRegion(iter, reg, meeps, list, true);

		// Calculate point value of region
		for (Point p : list.keySet()) {
			Tile.Region r = list.get(p).get(0);
			TileBoardIterator iterator = new TileBoardGenIterator(iter
					.getData(), p);
			// all similar regions on a Tile should have the same feature/point
			// value
			// This is NOT enforced by the model.
			tempScore += iterator.current().featureInRegion(r).pointValue;
		}

		// If current region is complete, multiply score by completion
		// multiplier
		if (returnVal && iter.current().featureInRegion(reg) != null) {
			tempScore *= GameState.getInstance().getDeck()
					.tileFeatureBindings().completionMultiplierForFeature(
							iter.current().featureIdentifierInRegion(reg));
		}

		/*
		 * Now we update our global data structures This update will do nothing
		 * if we've already touched the entrance quadrant because the recursive
		 * function will have returned without modifying list or meeps
		 */
		for (Point p : list.keySet()) {
			// Make sure we don't try to put in a null data structure
			if (scoreOfReg_.get(p) == null)
				scoreOfReg_.put(p, new EnumMap<Tile.Region, Integer>(
						Tile.Region.class));
			if (globalMeep_.get(p) == null)
				globalMeep_.put(p, new EnumMap<Tile.Region, List<Meeple>>(
						Tile.Region.class));
			if (isComplete_.get(p) == null)
				isComplete_.put(p, new EnumMap<Tile.Region, Boolean>(
						Tile.Region.class));

			for (Tile.Region r : list.get(p)) {
				scoreOfReg_.get(p).put(r, tempScore);
				globalMeep_.get(p).put(r, meeps);
				isComplete_.get(p).put(r, returnVal);

			}
		}
		return;
	}

	// Private recursive function for calculating Region values
	private boolean traverseRegion(TileBoardIterator iter, Tile.Region reg,
			List<Meeple> meeps, Map<Point, ArrayList<Tile.Region>> list,
			boolean returnVal) {
		// Base case 1: off edge of board
		if (iter.current() == null)
			return false;
		// Base case 2: region is null (poor input)
		if (iter.current().featureInRegion(reg) == null)
			return returnVal;
		// Base case 3: already touched this region
		if (getScoreOfRegion(iter.getLocation(), reg) != -1)
			return returnVal;
		/*
		 * Base case 4: "traversing" a center feature - these don't go past Tile
		 * boundaries but are based on Tiles surrounding current
		 */
		if (reg.equals(Tile.Region.Center)) {
			return traverseCenter(iter, reg, meeps, list, returnVal)
					&& returnVal;
		}
		// else

		TileFeatureBindings bindings_ = GameState.getInstance().getDeck()
				.tileFeatureBindings();

		// "touch" this Region globally ...
		if (scoreOfReg_.get(iter.getLocation()) == null) {
			scoreOfReg_.put(iter.getLocation(),
					new EnumMap<Tile.Region, Integer>(Tile.Region.class));
		}
		scoreOfReg_.get(iter.getLocation()).put(reg, 0);
		// ... and locally
		if (list.get(iter.getLocation()) == null)
			list.put(iter.getLocation(), new ArrayList<Tile.Region>());
		list.get(iter.getLocation()).add(reg);

		// If there's a merson here, add it to our local list
		Meeple current = iter.current().meepleInRegion(reg);
		if (current != null)
			meeps.add(current);

		// traverse to next tile
		if (reg.equals(Tile.Region.Left)) {
			returnVal = traverseRegion(
					((TileBoardGenIterator) iter).leftCopy(),
					Tile.Region.Right, meeps, list, returnVal)
					&& returnVal;
		} else if (reg.equals(Tile.Region.Right)) {
			returnVal = traverseRegion(((TileBoardGenIterator) iter)
					.rightCopy(), Tile.Region.Left, meeps, list, returnVal)
					&& returnVal;
		} else if (reg.equals(Tile.Region.Top)) {
			returnVal = traverseRegion(((TileBoardGenIterator) iter).upCopy(),
					Tile.Region.Bottom, meeps, list, returnVal)
					&& returnVal;
		} else if (reg.equals(Tile.Region.Bottom)) {
			returnVal = traverseRegion(
					((TileBoardGenIterator) iter).downCopy(), Tile.Region.Top,
					meeps, list, returnVal)
					&& returnVal;
		}

		// if feature does not end traversal
		// traverse to other regions in Tile (except center)

		if (!iter.current().featureInRegion(reg).endsTraversal) {
			for (Tile.Region r : Tile.Region.values()) {
				if (bindings_.featuresBind(iter.current()
						.featureIdentifierInRegion(r), iter.current()
						.featureIdentifierInRegion(reg))
						&& !r.equals(Tile.Region.Center)) {
					returnVal = traverseRegion(iter, r, meeps, list, returnVal)
							&& returnVal;
				}
			}
		}

		return returnVal;

	}

	// Helper function for calculating Center score
	private boolean traverseCenter(TileBoardIterator iter, Region reg,
			List<Meeple> meeps, Map<Point, ArrayList<Region>> list,
			boolean returnVal) {
		// test for not null
		if (iter.current().featureInRegion(reg) != null) {
			// meeple?
			Meeple meep1 = iter.current().meepleInRegion(reg);
			if (meep1 != null)
				meeps.add(meep1);

			// add a point for each non-null Tile surrounding this one
			// (including itself)
			TileBoardGenIterator temp = new TileBoardGenIterator(iter);
			int tempScore = 1;
			if (temp.right().current() != null) {
				++tempScore;
			} else if (temp.down().current() != null) {
				++tempScore;
			} else if (temp.left().current() != null) {
				++tempScore;
			} else if (temp.left().current() != null) {
				++tempScore;
			} else if (temp.up().current() != null) {
				++tempScore;
			} else if (temp.up().current() != null) {
				++tempScore;
			} else if (temp.right().current() != null) {
				++tempScore;
			} else if (temp.right().current() != null) {
				++tempScore;
			}

			// Touch globally
			if (scoreOfReg_.get(iter.getLocation()) == null)
				scoreOfReg_.put(iter.getLocation(),
						new EnumMap<Tile.Region, Integer>(Tile.Region.class));
			scoreOfReg_.get(iter.getLocation()).put(reg, tempScore);

			// If completely surrounded, it is complete
			if (tempScore == 9)
				return true;
			else
				return false;
		}
		// center feature is null - pass back returnVal
		return returnVal;
	}

	// If traverseRegion has touched given region of Tile at given location
	// This function returns the size of the region, else, returns -1
	public Integer getScoreOfRegion(Point loc, Tile.Region reg) {
		Map<Tile.Region, Integer> tileRegions = scoreOfReg_.get(loc);
		if (tileRegions == null)
			return -1;

		Integer temp = tileRegions.get(reg);
		if (temp == null)
			return -1;

		return temp;
	}

	/*
	 * If traverseRegion has touched given Region of Tile at given location and
	 * it has a nonempty meeple list claiming it, this function returns the list
	 * of meeple, else returns empty list
	 */
	public List<Meeple> getMeepleList(Point loc, Tile.Region reg) {
		ArrayList<Meeple> returnVal = new ArrayList<Meeple>();
		Map<Tile.Region, List<Meeple>> tileRegions = globalMeep_.get(loc);
		if (tileRegions == null)
			return returnVal;

		List<Meeple> temp = tileRegions.get(reg);
		if (temp == null)
			return returnVal;

		returnVal.addAll(temp);
		return returnVal;
	}

	/*
	 * If traverseRegion has touched given Region of Tile at given location,
	 * this function returns whether or not the Region is complete, else returns
	 * false
	 */
	public boolean getRegionCompletion(Point loc, Tile.Region reg) {
		Map<Tile.Region, Boolean> tileRegions = isComplete_.get(loc);
		if (tileRegions == null)
			return false;
		Boolean temp = tileRegions.get(reg);
		if (temp == null)
			return false;
		return temp;
	}

	/*
	 * Keeps track of touched locations. These store data collected when
	 * traversing and make it available to the accessors. Also, the recursive
	 * function can quit if we've already traversed this region with this
	 * calculator - this saves time if the function is accidentally called twice
	 * (i.e. easier code to traverse all), but causes problems if the data is
	 * dirty. Therefore, a RegionsCalc should be thrown away if Tiles are being
	 * added to board.
	 */
	private HashMap<Point, EnumMap<Tile.Region, Integer>> scoreOfReg_;
	private HashMap<Point, EnumMap<Tile.Region, List<Meeple>>> globalMeep_;
	private HashMap<Point, EnumMap<Tile.Region, Boolean>> isComplete_;

}
