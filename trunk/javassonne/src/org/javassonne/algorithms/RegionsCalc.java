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

import org.javassonne.model.Tile;
import org.javassonne.model.TileBoardGenIterator;
import org.javassonne.model.TileBoardIterator;
import org.javassonne.model.TileFeatureBindings;

/**
 * @author Kyle Prete
 * 
 */
public class RegionsCalc {

	public RegionsCalc(TileFeatureBindings tfbRef) {
		tileFeatureBindings_ = tfbRef;
		marked_ = new HashMap<Point, HashMap<Tile.Region, Integer>>();
	}

	public int traverseRegion(TileBoardIterator iter, Tile.Region reg) {
		HashMap<Point, ArrayList<Tile.Region>> list = new HashMap<Point, ArrayList<Tile.Region>>();
		int total = traverseRegion(iter, reg, list);
		for (Point p : list.keySet()) {
			for (Tile.Region r : list.get(p)) {
				marked_.get(p).put(r, total);
			}
		}
		return total;

	}

	public int traverseRegion(TileBoardIterator iter, Tile.Region reg,
			HashMap<Point, ArrayList<Tile.Region>> list) {
		if (iter.current() == null)
			return 0;
		if (isMarked(iter.getLocation(), reg) != -1)
			return isMarked(iter.getLocation(), reg);
		int returnVal = 0;
		if (marked_.get(iter.getLocation()) == null)
			marked_.put(iter.getLocation(),
							new HashMap<Tile.Region, Integer>());
		marked_.get(iter.getLocation()).put(reg, 0);
		if (list.get(iter.getLocation()) == null)
			list.put(iter.getLocation(), new ArrayList<Tile.Region>());
		list.get(iter.getLocation()).add(reg);

		Tile.Region newReg;
		// traverse to next tile
		if (reg.compareTo(Tile.Region.Left) == 0) {
			newReg = Tile.Region.Right;
			returnVal += traverseRegion(((TileBoardGenIterator) iter)
					.leftCopy(), newReg, list);
		} else if (reg.compareTo(Tile.Region.Right) == 0) {
			newReg = Tile.Region.Left;
			returnVal += traverseRegion(((TileBoardGenIterator) iter)
					.rightCopy(), newReg, list);
		} else if (reg.compareTo(Tile.Region.Top) == 0) {
			newReg = Tile.Region.Bottom;
			returnVal += traverseRegion(((TileBoardGenIterator) iter).upCopy(),
					newReg, list);
		} else if (reg.compareTo(Tile.Region.Bottom) == 0) {
			newReg = Tile.Region.Top;
			returnVal += traverseRegion(((TileBoardGenIterator) iter)
					.downCopy(), newReg, list);
		}
		// traverse to other regions in Tile
		for (Tile.Region r : Tile.Region.values()) {
			if (tileFeatureBindings_.featuresBind(iter.current()
					.featureIdentifierInRegion(r), iter.current()
					.featureIdentifierInRegion(reg))) {
				returnVal += traverseRegion(iter, r, list);
			}
		}

		return returnVal;

	}

	public Integer isMarked(Point loc, Tile.Region reg) {
		Integer temp = marked_.get(loc).get(reg);
		if (temp == null)
			return -1;
		return temp;
	}

	private HashMap<Point, HashMap<Tile.Region, Integer>> marked_;
	private TileFeatureBindings tileFeatureBindings_;

}
