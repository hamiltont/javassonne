/**
 * Javassonne 
 *  http://code.google.com/p/javassonne/
 * 
 * @author [Add Name Here]
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

import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionListener;

import org.javassonne.model.TileBoard;
import org.javassonne.model.TileBoardIterator;

public class WorldCanvas extends Canvas {
	private TileBoard board_;

	public WorldCanvas(TileBoard board) {
		board_ = board;
	}

	public void paint(Graphics g) {
		Graphics2D g2;

		g2 = (Graphics2D) g;
		TileBoardIterator iter = board_.homeTile();

		g2.drawImage(iter.current().getImage(), 0, 0, 300, 300, 50, 50, 300,
				300, null);
	}

	public void redraw() {
		// Redraw the board
	}

	public void setActionListener(ActionListener a) {
		// Register event listener
	}

}
