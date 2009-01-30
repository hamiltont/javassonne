/**
 * Javassonne 
 *  http://code.google.com/p/javassonne/
 * 
 * @author Adam Albright
 * @date Jan 25, 2009
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

package org.javassonne.ui.control;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

/**
 * This class forms a container to easily place images onto JPanels
 * 
 * @author Adam Albright
 * 
 */
public class JImagePanel extends JPanel {
	BufferedImage image_;
	int width_;
	int height_;

	// Default constructor
	// @params: image (BufferedImage) - the image to place into the container
	// width (int) - target width of the image and container
	// height (int) - target height of the image and container
	public JImagePanel(BufferedImage image, int width, int height) {
		image_ = image;
		width_ = width;
		height_ = height;
	}

	// Protected method for drawing the image onto the JPanel
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		this.setSize(width_, height_);
		g.drawImage(image_, 0, 0, width_, height_, null);
	}
	
	public void setImage(BufferedImage image) {
		image_ = image;
		this.repaint();
	}
}
