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
import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

import javax.swing.JFrame;

import org.javassonne.model.TileBoard;

public class GameWindow extends JFrame {

	private static final String WINDOW_TITLE = "Javassonne";
	private static final Color WINDOW_BG_COLOR = Color.gray;

	private WorldCanvas worldCanvas_;
	private HUDPanel controlPanel_;

	public GameWindow() {
		// Do JFrame initialization
		super();
		
		// set up some default properties
		setTitle(WINDOW_TITLE);
		this.getContentPane().setBackground(WINDOW_BG_COLOR);
		
		// Switching to full screen mode
		GraphicsDevice d = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		this.setUndecorated(true);

		this.setVisible(false);
		this.setVisible(true);
		
		DisplayMode mode = d.getDisplayMode();
		this.setSize(mode.getWidth(), mode.getHeight()-20);		
			
		// We need to force the control panel to render, so we can get the height
		// perhaps I could use controlPanel_.pack here instead?
		controlPanel_ = new HUDPanel();
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(controlPanel_, BorderLayout.PAGE_END);
		
		
		int controlPanelHeight = controlPanel_.getHeight();
		int height = getHeight() - controlPanelHeight;
		int width = getWidth();
		worldCanvas_ = new WorldCanvas(new Dimension(width, height));
		this.getContentPane().add(worldCanvas_, BorderLayout.CENTER);
	}
}