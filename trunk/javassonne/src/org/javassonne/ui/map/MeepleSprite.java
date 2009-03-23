/**
 * Javassonne 
 *  http://code.google.com/p/javassonne/
 * 
 * @author [Add Name Here]
 * @date Mar 23, 2009
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

package org.javassonne.ui.map;

import java.awt.Point;
import java.util.ArrayList;

import org.javassonne.model.Meeple;
import org.javassonne.model.Tile;
import org.javassonne.model.Player.MeepleColor;

public class MeepleSprite extends MapSprite {
	
	Meeple m_;
	MeepleColor color_;

	public MeepleSprite(Meeple m, MeepleColor meepleColor) {
		super(0, 0);
		
		m_ = m;
		color_ = meepleColor;
		
		setAnimating(false);
		setImage(String.format("images/meeple_%d.png", meepleColor.value));
	}
	
	public void addedToMap(MapLayer m) {
		// determine where we should be located based on the tile we want to be
		// on top of.
		Point p = m.getBoardPointFromTileLocation(m_.getParentTileLocation());
		p.x += m_.getRegionOnTile().x - this.getImage().getWidth() / 2;
		p.y += m_.getRegionOnTile().y - this.getImage().getHeight() / 2;
		
		setLocation(p);
	}
}
