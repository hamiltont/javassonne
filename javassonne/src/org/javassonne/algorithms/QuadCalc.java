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

public class QuadCalc {

	public QuadCalc() {

		numCastles_ = new HashMap<Point, EnumMap<Tile.Quadrant, Integer>>();
		globalMeep_ = new HashMap<Point, EnumMap<Tile.Quadrant, List<Meeple>>>();

	}

	public void traverseQuadrant(TileBoardIterator iter, Tile.Quadrant quad) {
		HashMap<Point, ArrayList<Tile.Quadrant>> list = new HashMap<Point, ArrayList<Tile.Quadrant>>();
		ArrayList<Meeple> meeps = new ArrayList<Meeple>();
		traverseQuadrant(iter, quad, meeps, list);
		// TODO: Get total as # completed castles
		int total = 0;
		RegionsCalc c = new RegionsCalc();
		for (Point p : list.keySet()) {
			TileBoardIterator iterNew = new TileBoardGenIterator(GameState
					.getInstance().getBoard(), p);
			for (Tile.Quadrant q : list.get(p)) {
				for (Tile.Region r : Tile.Region.values()) {
					if (iterNew.current().featureInRegion(r) != null
							&& iterNew.current().featureInRegion(r).farmPointValue != 0
							&& c.getScoreOfRegion(p, r) == -1
							&& r.isAdjacentTo(q)) {
						c.traverseRegion(iterNew, r);
						if (c.getRegionCompletion(p, r))
							total += iterNew.current().featureInRegion(r).farmPointValue;
					}
				}
			}
		}
		for (Point p : list.keySet()) {
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

	private void traverseQuadrant(TileBoardIterator iter, Quadrant quad,
			ArrayList<Meeple> meeps, HashMap<Point, ArrayList<Quadrant>> list) {
		if (iter.current() == null)
			return;
		if (getNumCastles(iter.getLocation(), quad) != -1)
			return;

		// else
		if (numCastles_.get(iter.getLocation()) == null)
			numCastles_.put(iter.getLocation(),
					new EnumMap<Tile.Quadrant, Integer>(Tile.Quadrant.class));
		numCastles_.get(iter.getLocation()).put(quad, 0);
		if (list.get(iter.getLocation()) == null)
			list.put(iter.getLocation(), new ArrayList<Tile.Quadrant>());
		list.get(iter.getLocation()).add(quad);
		Meeple current = iter.current().meepleInQuadrant(quad);
		if (current != null)
			meeps.add(current);

		boolean leftWall = iter.current().farmWallInRegion(Tile.Region.Left);
		boolean upWall = iter.current().farmWallInRegion(Tile.Region.Top);
		boolean rightWall = iter.current().farmWallInRegion(Tile.Region.Right);
		boolean downWall = iter.current().farmWallInRegion(Tile.Region.Bottom);
		// traverse to next Tile(s)
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
		// traverse to other quadrants that connect to current
		int currentQuad = iter.current().farmInQuadrant(quad);
		for (Tile.Quadrant q : Tile.Quadrant.values()) {
			if (iter.current().farmInQuadrant(q) == currentQuad)
				traverseQuadrant(iter, q, meeps, list);
		}

		return;
	}

	// If traverseQuadrant has touched given Quadrant of Tile at given location
	// This function returns the size of the Quadrant, else, returns -1
	public Integer getNumCastles(Point loc, Tile.Quadrant quad) {
		Map<Tile.Quadrant, Integer> tileQuadrants = numCastles_.get(loc);
		if (tileQuadrants == null)
			return -1;

		Integer temp = tileQuadrants.get(quad);
		if (temp == null)
			return -1;

		return temp;
	}

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

	// Keeps track of touched locations
	private HashMap<Point, EnumMap<Tile.Quadrant, Integer>> numCastles_;
	private HashMap<Point, EnumMap<Tile.Quadrant, List<Meeple>>> globalMeep_;

}
