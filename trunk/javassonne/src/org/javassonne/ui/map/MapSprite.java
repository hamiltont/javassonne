/**
 * Javassonne 
 *  http://code.google.com/p/javassonne/
 * 
 * @author [Add Name Here]
 * @date Mar 10, 2009
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

import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class MapSprite {

	protected int x_;
	protected int y_;
	protected BufferedImage image_;
	protected Boolean shouldScaleImage_ = true;
	protected Boolean animating_ = false;
	protected Object group_ = "Unset";
	
	public MapSprite(int x, int y) {
		x_ = x;
		y_ = y;
	}

	public BufferedImage getImage()
	{
		return image_;
	}
	
	public void setImage(String path) {
		try {
			image_ = ImageIO.read(new File(path));
		} catch (IOException e) {
			image_ = null;
			e.printStackTrace();
		}
	}

	public void setLocation(Point location) {
		x_ = location.x;
		y_ = location.y;
	}
	
	public Boolean isAnimating()
	{
		return animating_;
	}
	
	public void setAnimating(Boolean a)
	{
		animating_ = a;
	}

	public void addedToMap(MapLayer m)
	{
		
	}
	
	public void draw(Graphics g, Point offset, double scale) {
		// default implementations draws image if set
		if (image_ != null) {
			if (shouldScaleImage_)
				g.drawImage(image_, (int)(x_ * scale + offset.x), (int)(y_ * scale + offset.y), (int)(image_
						.getWidth() * scale), (int)(image_.getHeight() * scale), null);
			else
				g.drawImage(image_, (int)(x_ * scale + offset.x), (int)(y_ * scale + offset.y), null);
		}
	}

	public void update(MapLayer mapLayer) {
		// default implementations do nothing
	}

	public Object getGroup() {
		return group_;
	}
	
	public void setGroup(Object group) {
		group_ = group;	
	}

	
}
