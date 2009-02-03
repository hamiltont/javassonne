/**
 * Javassonne 
 *  http://code.google.com/p/javassonne/
 * 
 * @author Hamilton Turner
 * @date Jan 14, 2009
 * 
 * @updated David Leinweber 
 * @date Jan 20, 2009
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

import java.awt.Color;
import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

import javax.swing.JFrame;

import org.javassonne.ui.control.JKeyListener;

/**
 * @author Hamilton Turner
 * 
 *         The Display serves only the purpose of wrapper-ing the
 *         LayeredDisplayPane so that it can be set to full-screen mode.
 */
public class DisplayWindow extends JFrame {

	private static final String WINDOW_TITLE = "Javassonne";
	private static final Color WINDOW_BG_COLOR = Color.WHITE;

	private DisplayLayeredPane displayPane_;

	/**
	 * Constructor. Sets application to full screen mode, adds the JLayeredPane
	 * for display, and begins rendering (setVisible true)
	 */
	public DisplayWindow() {
		// Do JFrame initialization
		super();

		// Set a few basic properties of our window
		setTitle(WINDOW_TITLE);
		getContentPane().setBackground(WINDOW_BG_COLOR);

		// Switching to full screen mode
		// TODO read fullscreen javadocs to make this multiOS friendly
		// http://java.sun.com/docs/books/tutorial/extra/fullscreen/index.html
		GraphicsDevice d = GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getDefaultScreenDevice();

		// Make our window full screen
		DisplayMode mode = d.getDisplayMode();
		this.setSize(mode.getWidth(), mode.getHeight() - 20);

		// Hide the bar at the top of the window and the window border
		this.setUndecorated(true);

		// Add the JLayeredPane to our contents
		displayPane_ = new DisplayLayeredPane(this.getSize());
		getContentPane().add(displayPane_);
		
		// Add the key listener
		this.addKeyListener(JKeyListener.getInstance());
		
		// Show the window
		setVisible(true);
	}
	
	public DisplayLayeredPane getDisplayLayeredPane()
	{
		return displayPane_;
	}
}