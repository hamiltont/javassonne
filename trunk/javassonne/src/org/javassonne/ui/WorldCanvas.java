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
import java.awt.image.BufferedImage;

import org.javassonne.model.TileBoard;
import org.javassonne.model.TileBoardIterator;

public class WorldCanvas extends Canvas {
	private TileBoard board_;
	private Graphics2D canvas_;
	private double scale_ = 0.3;

	public WorldCanvas(TileBoard board) {
		board_ = board;
	}

	public void paint(Graphics g) {
		canvas_ = (Graphics2D) g;
		redraw();
	}

	// Redraw the board
	public void redraw() {
		// get the starting tile
		TileBoardIterator iter = board_.homeTile();
		BufferedImage tileImage = iter.current().getImage();

		// get the dimensions of the tile image.
		int tileWidth = (int) (tileImage.getWidth() * scale_);
		int tileHeight = (int) (tileImage.getHeight() * scale_);

		// Compute how many rows and columns are visible. We add 1 so that
		// tiles are drawn right up to the edge of the screen at the edges.
		int rows = this.getHeight() / tileHeight + 1;
		int cols = this.getWidth() / tileWidth + 1;

		System.out.println(rows);

		for (int k = 0; k < rows; k++)
			canvas_.drawLine(0, k * tileHeight, tileWidth, k * tileHeight);

		for (int k = 0; k < cols; k++)
			canvas_.drawLine(k * tileWidth, 0, k * tileWidth, tileHeight);

		// Place tile images by iterating through the board and wrapping when we
		// reach
		// the end of a row.
		try {
			int i, x, y;
			i = x = y = 0;
			while (i < (rows * cols)
					&& (iter.current() != null || iter.nextRow() != null)) {
				i++;
				if (iter.current() != null) {
					canvas_.drawImage(iter.current().getImage(), x, y,
							tileWidth, tileHeight, null);
				}
				x += tileWidth;
				if (i % cols == 0) {
					// Next row
					x = 0;
					y += tileHeight;
					iter.nextRow();
				}
				iter.right();
				

				
			}

		} catch (Exception e) {
			System.out.println("Error displaying a tile image.");
		}
	}

	public void setActionListener(ActionListener a) {
		// Register event listener
	}

}
