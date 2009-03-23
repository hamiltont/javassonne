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

/**
 * 
 */

package org.javassonne.algorithms;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.javassonne.model.Meeple;
import org.javassonne.model.Tile;
import org.javassonne.model.TileBoardGenIterator;
import org.javassonne.model.TileBoardIterator;
import org.javassonne.model.TileFeatureBindings;

/**
 * @author Kyle Prete Note: If the board this RegionsCalc is unofficially
 *         attached to is changed, the RegionsCalc must be destroyed as all its
 *         data is corrupt.
 * 
 *         May enforce this be listening for board_changed events.
 */
public class RegionsCalc {

	public RegionsCalc(TileFeatureBindings tfbRef) {
		tileFeatureBindings_ = tfbRef;
		marked_ = new HashMap<Point, HashMap<Tile.Region, Integer>>();
	}

	public int traverseRegion(TileBoardIterator iter, Tile.Region reg,
			List<Meeple> meeps) {
		HashMap<Point, ArrayList<Tile.Region>> list = new HashMap<Point, ArrayList<Tile.Region>>();
		traverseRegion(iter, reg, meeps, list);
		int total = list.keySet().size();
		for (Point p : list.keySet()) {
			for (Tile.Region r : list.get(p)) {
				marked_.get(p).put(r, total);
			}
		}
		// Given region on iter's Tile has no adjacent Tiles
		// on which it continues; therefore size = 1
		if (total == 0)
			++total;
		return total;

	}

	private void traverseRegion(TileBoardIterator iter, Tile.Region reg,
			List<Meeple> meeps, HashMap<Point, ArrayList<Tile.Region>> list) {
		if (iter.current() == null)
			return;
		if (sizeOfRegion(iter.getLocation(), reg) != -1)
			return;
		if (marked_.get(iter.getLocation()) == null)
			marked_.put(iter.getLocation(),
							new HashMap<Tile.Region, Integer>());
		marked_.get(iter.getLocation()).put(reg, 0);
		if (list.get(iter.getLocation()) == null)
			list.put(iter.getLocation(), new ArrayList<Tile.Region>());
		list.get(iter.getLocation()).add(reg);
		Meeple current = iter.current().meepleInRegion(reg);
		if (current != null)
			meeps.add(current);

		Tile.Region newReg;
		// traverse to next tile
		if (reg.compareTo(Tile.Region.Left) == 0) {
			newReg = Tile.Region.Right;
			traverseRegion(((TileBoardGenIterator) iter).leftCopy(), newReg,
					meeps, list);
		} else if (reg.compareTo(Tile.Region.Right) == 0) {
			newReg = Tile.Region.Left;
			traverseRegion(((TileBoardGenIterator) iter).rightCopy(), newReg,
					meeps, list);
		} else if (reg.compareTo(Tile.Region.Top) == 0) {
			newReg = Tile.Region.Bottom;
			traverseRegion(((TileBoardGenIterator) iter).upCopy(), newReg,
					meeps, list);
		} else if (reg.compareTo(Tile.Region.Bottom) == 0) {
			newReg = Tile.Region.Top;
			traverseRegion(((TileBoardGenIterator) iter).downCopy(), newReg,
					meeps, list);
		}
		// traverse to other regions in Tile
		for (Tile.Region r : Tile.Region.values()) {
			if (tileFeatureBindings_.featuresBind(iter.current()
					.featureIdentifierInRegion(r), iter.current()
					.featureIdentifierInRegion(reg))) {
				traverseRegion(iter, r, meeps, list);
			}
		}

		return;

	}

	// If traverseRegion has touched given region of Tile at given location
	// This function returns the size of the region, else, returns -1
	public Integer sizeOfRegion(Point loc, Tile.Region reg) {
		Integer temp = marked_.get(loc).get(reg);
		if (temp == null)
			return -1;
		return temp;
	}

	// Keeps track of touched locations
	private HashMap<Point, HashMap<Tile.Region, Integer>> marked_;

	private TileFeatureBindings tileFeatureBindings_;

}
