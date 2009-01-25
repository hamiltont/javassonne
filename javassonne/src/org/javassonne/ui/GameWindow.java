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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;

import javax.swing.JFrame;

import org.javassonne.model.TileBoard;

public class GameWindow extends JFrame {

	private static final String WINDOW_TITLE = "Javassonne";
	private static final Color WINDOW_BG_COLOR = Color.gray;

	private Container contentPane_;
	private TileBoard boardModel_;
	private GameWindowController controller_;
	private WorldCanvas worldCanvas_;
	private ControlPanel controlPanel_;

	public GameWindow(TileBoard model) {
		// Do JFrame initialization
		super();
		
		// Syntax hack
		contentPane_ = getContentPane();

		boardModel_ = model;
		setTitle(WINDOW_TITLE);
		contentPane_.setBackground(WINDOW_BG_COLOR);

		// Remove the border
		setUndecorated(true);
		
		// Switching to full screen mode
		GraphicsEnvironment.getLocalGraphicsEnvironment().
			getDefaultScreenDevice().setFullScreenWindow(this);
		
		
		// We need to force the control panel to render, so 
		//		we can get the height
		// perhaps I could use controlPanel_.pack here instead?
		contentPane_.setLayout(new BorderLayout());
		controlPanel_ = new ControlPanel();
		contentPane_.add(controlPanel_, BorderLayout.PAGE_END);
		setVisible(true);
		
		// Set up the layout of the window
		
		int controlPanelHeight = controlPanel_.getHeight();
		int height = getHeight() - controlPanelHeight;
		int width = getWidth();
		worldCanvas_ = new WorldCanvas(boardModel_, new Dimension(width, height));
		contentPane_.add(worldCanvas_, BorderLayout.CENTER);
		
		setSize(getWidth(), getHeight());
		
		// Force a redraw to show the new components
		setVisible(false);
		setVisible(true);
	}

	public void update() {
		// Pass the update on to the sub views
		worldCanvas_.redraw();
		controlPanel_.redraw();
		
	}
	
	public void setController(GameWindowController controller) {
		controller_ = controller;
		
		// Add action listeners
		worldCanvas_.setDefaultActionListener(controller);
		controlPanel_.setActionListener(controller);
	}
}