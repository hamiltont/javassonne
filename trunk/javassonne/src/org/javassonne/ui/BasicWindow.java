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

// Needed for Container and Color
import java.awt.Color;
import java.awt.Container;
import java.awt.GridLayout;

import javax.swing.JFrame;

public class BasicWindow extends JFrame {

	private static final String WINDOW_TITLE = "Javassonne";
	private static final Color WINDOW_BG_COLOR = Color.gray;

	private Container contentPane_;
	private BasicWindowModel model_;
	private BasicWindowController controller_;

	public BasicWindow(BasicWindowModel model, BasicWindowController controller) {
		// Do initialization for JFrame
		super();

		// Syntax hack
		contentPane_ = getContentPane();

		controller_ = controller;
		model_ = model;
		setTitle(WINDOW_TITLE);
		contentPane_.setBackground(WINDOW_BG_COLOR);

		// Set up the layout of the window
		contentPane_.setLayout(new GridLayout());
	}
}