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

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

import javax.swing.JFrame;

import org.javassonne.ui.control.JKeyListener;

/**
 * @author Hamilton Turner
 * 
 *         The Display serves only the purpose of wrapper-ing the
 *         LayeredDisplayPane so that it can be set to full-screen mode.
 *         It takes the responsibility of creating the layeredDisplayPane, 
 *         which creates both the map and the mapScrollEdges. 
 */
public class DisplayWindow extends JFrame {

	private static final String WINDOW_TITLE = "Javassonne";
	private static boolean FULL_SCREEN = true;
	
	private DisplayDesktopPane displayPane_;

	/**
	 * Constructor. Sets application to full screen mode, adds the JLayeredPane
	 * for display, and begins rendering (setVisible true)
	 */
	public DisplayWindow() {
		super();
		
		GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		boolean fakeFullscreen = false;
		
		// determine whether we need to fake full screen.
		 try{
			   String os = System.getProperty("os.name");
			   if (os.equals("Mac OS X"))
				   fakeFullscreen = true;
			   
		 }catch (Exception e) {
			 // who cares?
		 }
		
		// Set a few basic properties of our window
		setTitle(WINDOW_TITLE);
		
		if (FULL_SCREEN){
			if (fakeFullscreen){
				setSize(device.getDisplayMode().getWidth(), device.getDisplayMode().getHeight()-20);
			}else
				setSize(device.getDisplayMode().getWidth(), device.getDisplayMode().getHeight());
			
		} else {
			setSize(1024, 768);
			setLocation((device.getDisplayMode().getWidth()-1024)/2, (device.getDisplayMode().getHeight()-768)/2);
		}	
		
		// Hide the bar at the top of the window and the window border
		this.setUndecorated(true);
		
		// Add the JLayeredPane to our contents
		displayPane_ = new DisplayDesktopPane(this.getSize());
		getContentPane().add(displayPane_);
		
		// Add the key listener
		this.addKeyListener(JKeyListener.getInstance());
		
		// Show the window
		setVisible(true);
		
		// Make the window fullscreen
		if ((FULL_SCREEN) && (!fakeFullscreen)) device.setFullScreenWindow(this);
	}

	public DisplayDesktopPane getDisplayDesktopPane() {
		return displayPane_;
	}
}