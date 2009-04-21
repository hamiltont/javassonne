/**
 * Javassonne 
 *  http://code.google.com/p/javassonne/
 * 
 * @author Adam Albright
 * @date Feb 18, 2009
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

import java.awt.GraphicsEnvironment;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import org.javassonne.ui.controls.JScrollablePicture;

public class JSplashScreen extends JFrame {

	private static final String SPLASH_IMAGE = "images/splash_screen.jpg";
	private static final String ICON_IMAGE = "images/icon.png";
	
	public JSplashScreen() {
		super("Javassonne");
		
		//Set Program Icon
		ImageIcon icon = new ImageIcon(ICON_IMAGE);
		setIconImage(icon.getImage());
		
		//Load the splash screen image
		ImageIcon img = new ImageIcon(SPLASH_IMAGE);
		setUndecorated(true);

		int width = img.getIconWidth();
		int height = img.getIconHeight();

		int swidth = GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getDefaultScreenDevice().getDisplayMode().getWidth();
		int sheight = GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getDefaultScreenDevice().getDisplayMode().getHeight();

		setSize(width, height);
		setLocation((swidth - width) / 2, (sheight - height) / 2);

		add(new JScrollablePicture(img, 15));

		setVisible(true);
		toFront();

		try {
			Thread.sleep(2000);
		} catch (Exception e) {
		}

		setVisible(false);
	}
}