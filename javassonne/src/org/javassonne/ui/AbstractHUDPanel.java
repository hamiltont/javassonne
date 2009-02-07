/**
 * Javassonne 
 *  http://code.google.com/p/javassonne/
 * 
 * @author Ben Gotow
 * @date Feb 5, 2009
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
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import org.javassonne.ui.control.JKeyListener;

/**
 * The AbstractHUDPanel provides basic functionality used in the game's HUD
 * panels. Right now, that means it implements MouseListener to prevent clicks
 * from passing through to the map behind it, and allows you to set a background
 * image that is drawn onto the panel automatically.
 * 
 * @author bengotow
 * 
 */
public class AbstractHUDPanel extends JPanel {

	private BufferedImage background_ = null;

	public AbstractHUDPanel() {
		super();
		
		addKeyListener(JKeyListener.getInstance());
	}

	public void setBackgroundImagePath(String s) {
		try {
			this.setBackgroundImage(ImageIO.read(new File(s)));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setBackgroundImage(BufferedImage img) {
		background_ = img;
	}

	/*
	 * This function is responsible for painting the background image we have.
	 */
	public void paintComponent(Graphics g) {
		if (background_ != null) {
			Graphics2D g2 = (Graphics2D) g;
			g2
					.drawImage(background_, 0, 0, this.getWidth(), this
							.getHeight(), 0, 0, background_.getWidth(),
							background_.getHeight(), null);
		}
	}

}
