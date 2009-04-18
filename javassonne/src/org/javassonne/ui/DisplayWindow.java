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

import java.awt.Component;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import org.javassonne.ui.controls.DisplayDesktopPane;

/**
 * @author Hamilton Turner
 * 
 *         The Display serves only the purpose of wrapper-ing the
 *         LayeredDisplayPane so that it can be set to full-screen mode. It
 *         takes the responsibility of creating the layeredDisplayPane, which
 *         creates both the map and the mapScrollEdges.
 */
public class DisplayWindow extends JFrame implements WindowListener {

	private static final String ICON_IMAGE = "images/icon.png";
	private static final String WINDOW_TITLE = "Javassonne";
	private static boolean FULL_SCREEN = true;

	private DisplayDesktopPane displayPane_;

	/**
	 * Constructor. Sets application to full screen mode, adds the JLayeredPane
	 * for display, and begins rendering (setVisible true)
	 */
	public DisplayWindow() {
		super();
		
		ImageIcon img = new ImageIcon(ICON_IMAGE);
		setIconImage(img.getImage());
		
		GraphicsDevice device = GraphicsEnvironment
				.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		boolean fakeFullscreen = false;

		// determine whether we need to fake full screen.
		try {
			String os = System.getProperty("os.name");
			if (os.equals("Mac OS X"))
				fakeFullscreen = true;

		} catch (Exception e) {
			// who cares?
		}

		// Set a few basic properties of our window
		setTitle(WINDOW_TITLE);

		if (FULL_SCREEN) {
			if (fakeFullscreen) {
				setSize(device.getDisplayMode().getWidth(), device
						.getDisplayMode().getHeight() - 20);
			} else
				setSize(device.getDisplayMode().getWidth(), device
						.getDisplayMode().getHeight());

		} else {
			setSize(1024, 768);
			setLocation((device.getDisplayMode().getWidth() - 1024) / 2,
					(device.getDisplayMode().getHeight() - 768) / 2);
		}

		// Hide the bar at the top of the window and the window border
		this.setUndecorated(true);

		// Add the JLayeredPane to our contents
		displayPane_ = new DisplayDesktopPane(this.getSize());
		getContentPane().add(displayPane_);

		// Add the key listener
		this.addKeyListener(JKeyListener.getInstance());

		// Add state listener
		this.addWindowListener(this);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Show the window
		setVisible(true);

		// Make the window fullscreen
		if ((FULL_SCREEN) && (!fakeFullscreen))
			device.setFullScreenWindow(this);
	}

	public DisplayDesktopPane getDisplayDesktopPane() {
		return displayPane_;
	}

	/*
	 * Window State Listeners
	 */

	/*
	 * Called when the game window is minimized
	 */
	public void windowIconified(WindowEvent e) {
		// Restore the game window to full screen when dialog is being displayed
		// Workaround for bug with JVM 1.6.0_12-b04 that
		// auto minimizes for JOptionPane method calls

		boolean restore = false;
		Component[] onScreen = DisplayHelper.getInstance().getComponents();

		for (int i = 0; i < onScreen.length; i++)
			if ("JPopUp".equalsIgnoreCase(onScreen[i].getName()))
				restore = true;

		if (restore == true)
			this.setState(NORMAL);
	}

	public void windowActivated(WindowEvent e) {
	}

	public void windowClosed(WindowEvent e) {
	}

	public void windowClosing(WindowEvent e) {
	}

	public void windowDeactivated(WindowEvent e) {
	}

	public void windowDeiconified(WindowEvent e) {
	}

	public void windowOpened(WindowEvent e) {
	}
}