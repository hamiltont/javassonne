/**
 * Javassonne 
 *  http://code.google.com/p/javassonne/
 * 
 * @author [Add Name Here]
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

package org.javassonne.model;

import java.awt.Point;

public class Meeple {

	private Tile parentTile_;
	private Point parentTileLocation_;

	private Tile.Quadrant quadrantOnTile_;
	private Tile.Region regionOnTile_;

	private int player_;

	/**
	 * The default constructor for a meeple. It takes a lot of parameters, but
	 * this information is needed for optimization later.
	 * 
	 * @param player
	 *            The number of the player whose meeple this is
	 * @param tile
	 *            The tile the meeple has been placed on
	 * @param tileLocation
	 *            The location of the tile (so we can draw fast)
	 * @param quadrant
	 *            The quadrant the meeple is on, if applicable.
	 * @param region
	 *            The region the meeple is on, if applicable.
	 */
	public Meeple(int player, Tile tile, Point tileLocation, Tile.Quadrant q,
			Tile.Region r) {

		parentTileLocation_ = tileLocation;
		parentTile_ = tile;
		player_ = player;

		quadrantOnTile_ = q;
		regionOnTile_ = r;

	}

	public Meeple() {
	}

	public Tile getParentTile() {
		return parentTile_;
	}

	public Point getParentTileLocation() {
		return parentTileLocation_;
	}

	public int getPlayer() {
		return player_;
	}

	public Tile.Region getRegionOnTile() {
		return regionOnTile_;
	}

	public Tile.Quadrant getQuadrantOnTile() {
		return quadrantOnTile_;
	}

	public void setParentTile(Tile parentTile) {
		this.parentTile_ = parentTile;
	}

	public void setParentTileLocation(Point parentTileLocation) {
		this.parentTileLocation_ = parentTileLocation;
	}

	public void setPlayer(int player) {
		this.player_ = player;
	}

	public void setRegionOnTile(Tile.Region region) {
		this.regionOnTile_ = region;
	}

	public void setQuadrantOnTile(Tile.Quadrant quadrant) {
		this.quadrantOnTile_ = quadrant;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		else if (obj.getClass() != this.getClass())
			return false;
		else {
			Meeple obj2 = (Meeple) obj;
			return this.getParentTileLocation() == obj2.getParentTileLocation()
					&& this.getRegionOnTile() == obj2.getRegionOnTile()
					&& this.getQuadrantOnTile() == obj2.getQuadrantOnTile();
		}

	}

}