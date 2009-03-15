/**
 * Javassonne 
 *  http://code.google.com/p/javassonne/
 * 
 * @author [Add Name Here]
 * @date Mar 11, 2009
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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;

import org.javassonne.model.Tile;

public class TilePlacementSprite extends MapSprite {

	public Point tileIndex_;
	private float hue_;

	private ArrayList<Tile.Region> regionOptions_;
	private ArrayList<Tile.Quadrant> quadrantOptions_;

	public TilePlacementSprite(Point t) {
		super(0, 0);

		tileIndex_ = t;
		regionOptions_ = new ArrayList<Tile.Region>();
		quadrantOptions_ = new ArrayList<Tile.Quadrant>();
		
		setAnimating(false);
		setImage("images/white_placement_star.png");
	}

	@Override
	public void addedToMap(MapLayer m) {
		// determine where we should be located based on the tile we want to be
		// on top of.
		setLocation(m.getPointFromTileLocation(tileIndex_));
	}

	public void setRegionOptions(ArrayList<Tile.Region> a)
	{
		regionOptions_ = a;
	}
	
	public void setQuadrantOptions(ArrayList<Tile.Quadrant> a)
	{
		quadrantOptions_ = a;
	}
	
	@Override
	public void draw(Graphics g, Point offset, double scale) {
		// default implementations draws image if set
		int w = image_.getWidth();
		int h = image_.getHeight();

		for (Tile.Region r : regionOptions_) {
			int x = (int) (x_ + offset.x + (r.x - w / 2) * scale);
			int y = (int) (y_ + offset.y + (r.y - h / 2) * scale);
			g.drawImage(image_, x, y, (int) (w * scale), (int) (h * scale),
					null);
		}

		g.setColor(new Color(1, 1, 1, 0.5f));
		for (Tile.Quadrant q : quadrantOptions_) {
			int x = (int) (x_ + offset.x + q.rect.getX() * scale);
			int y = (int) (y_ + offset.y + q.rect.getY() * scale);
			g.fillRect(x, y, (int) (q.rect.getWidth() * scale), (int) (q.rect
					.getHeight() * scale));
		}

	}

	@Override
	public void update(MapLayer mapLayer) {
		// animate ourselves?
		hue_ += 0.01;
	}
}
