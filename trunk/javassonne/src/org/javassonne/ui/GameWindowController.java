/**
 * Javassonne 
 *  http://code.google.com/p/javassonne/
 * 
 * @author David Leinweber
 * @date Jan 14, 2009
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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.javassonne.model.TileBoard;

public class GameWindowController implements ActionListener {

	private DisplayWindow view_;
	private TileBoard model_;
	private DrawTile drawTile_;
	private RotateTile rotateTile_;

	public GameWindowController(DisplayWindow window, TileBoard model) {
		view_ = window;
		model_ = model;
	}

	public void actionPerformed(ActionEvent e) {
		// Must handle actions from both WorldCanvas, and from
		// Control Panel
		String action = e.getActionCommand();
		
		if (action.equals("exit_game"))
			System.exit(0);
		if (action.equals("draw_tile"));
		{
			drawTile_ = new DrawTile();
			drawTile_.actionPerformed(e);
		}
		if (action.equals("rotate_right"))
		{
			rotateTile_ = new RotateTile();
			rotateTile_.actionPerformed(e);
		}
	}
}
