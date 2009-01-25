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

public class JImagePanel extends JPanel {
	BufferedImage image_;

	public JImagePanel(BufferedImage image) {
		this.image_ = image;
	}

	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		// Center the image
		int x = (getWidth() - image_.getWidth()) / 2;
		int y = (getHeight() - image_.getHeight()) / 2;
		this.setSize(image_.getWidth(), image_.getHeight());
		g.drawImage(image_,x,y,null);
	}
}
