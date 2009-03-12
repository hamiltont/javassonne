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
import java.awt.image.BufferedImage;

import org.javassonne.model.Tile;

public class TilePlacementSprite extends MapSprite {

	public Tile tile_;
	
	public TilePlacementSprite(int x, int y, Tile t) {	
		super(x, y);

		tile_ = t;
	}

	@Override
	public void draw(BufferedImage target)
	{
		// draw something
		Graphics g = target.getGraphics();
		g.setColor(Color.BLUE);
		g.fillOval(x_, y_, 100, 100);
	}
	
	@Override
	public void update(MapLayer mapLayer) 
	{
		// animate ourselves?
	}
}
