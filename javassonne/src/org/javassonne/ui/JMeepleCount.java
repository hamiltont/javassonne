/**
 * Javassonne 
 *  http://code.google.com/p/javassonne/
 * 
 * @author [Add Name Here]
 * @date Mar 9, 2009
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

package org.javassonne.ui;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class JMeepleCount extends JPanel {

	int color_;
	int count_;

	JMeepleCount(int color) {
		super();
		setOpaque(false);
		color_ = color;
	}

	public void setCount(int count) {
		count_ = count;
		repaint();
	}

	public void paintComponent(Graphics g) {
		// pull in the meeple image
		BufferedImage image = null;
		try {
			image = ImageIO.read(new File(String.format(
					"images/hud_stats_meeple_%d.png", color_)));
		} catch (Exception e) {
			// whatever...
		}

		// now paint the meeple image as many times as we need
		int x = 0;
		for (int ii = 0; ii < count_; ii++) {
			g.drawImage(image, x, 1, null);
			x += image.getWidth();
		}
	}

}
