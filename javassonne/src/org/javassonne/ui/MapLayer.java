/**
 * Javassonne 
 *  http://code.google.com/p/javassonne/
 * 
 * @author [Add Name Here]
 * @date Jan 30, 2009
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
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;

import javax.swing.JPanel;
import javax.swing.JViewport;

/**
 * The default panel, displayed below all others. This panel contains the
 * JViewport that displays the map, and the grid that is displayed
 */
public class MapLayer extends JPanel {
	private JViewport viewport_;
	private Map map_;

	/**
	 * Constructor
	 * 
	 * @param screenSize
	 *            The amount of screen the JViewport will have to display
	 *            the map
	 */
	public MapLayer(Dimension screenSize) {
		map_ = new Map(screenSize);

		setSize(screenSize);

		viewport_ = new JViewport();
		viewport_.setExtentSize(screenSize);
		viewport_.setLayout(null);
		viewport_.setView(map_);

		// This layout allows the viewport to expand and take
		// all available space
		setLayout(new BorderLayout());
		add(viewport_);// , BorderLayout.CENTER);

	}

	/**
	 * Helper function that allows other layers to know the amount of room
	 * needed to render the map. This allows the other layers to setSize
	 * without accidentally rescaling the map
	 * 
	 * @return The width and height needed to render the full map (including
	 *         any offscreen portion)
	 */
	public Dimension getMapSize() {
		return map_.getSize();
	}

	/**
	 * Helper function for setting the upper left and lower right tile
	 * positions. Note that this is in the board coord's, so an upper left
	 * value of -3,0 would indicate the current upper left tile on the board
	 * was 3 left, and 0 up. This allows the viewport to not scroll beyone
	 * the currently visible map.
	 */
	public void setBoardConstraints(Point upperLeftTile,
			Point lowerRightTile) {
		// TODO Implement setBoardConstraints function
	}

	/**
	 * Shifts the internal JViewport so a different portion of the map is
	 * shown
	 * 
	 * @param amount
	 *            The amount the view should be shifted. Values should be
	 *            relative to position 0,0 on the normal cartesian plane.
	 *            Ex: x/y of 3,-3 would shift the map to the right 3 units,
	 *            and down 3 units.
	 */
	public void shiftView(Point amount) {
		Point current = viewport_.getViewPosition();
		current.y += amount.y;
		current.x += amount.x;
		viewport_.setViewPosition(current);
	}

	/**
	 * Because the JViewport needs to wrapper something, this class is a
	 * logical construct that simply displays/renders the map. Currently no
	 * optimization is provided. Map decides how much size is needed to
	 * render the map.
	 */
	private class Map extends JPanel {
		private double scale_ = 0.3;

		/**
		 * Constructor
		 * 
		 * @param screenSize
		 *            Useful in determining the initial size of the map.
		 *            This param may be removed later.
		 */
		public Map(Dimension screenSize) {

			// Note that you should never actually
			// modify the value of screenSize here.
			// Because is it pass-by-ref, this will
			// propogate up and interfere with other classes
			setSize(screenSize.width * 2, screenSize.height * 2);
		}

		/**
		 * Override of the default JPanel function to render the map, and
		 * any grid.
		 */
		public void paintComponent(Graphics gra) {

			// clear the graphics layer
			gra.clearRect(0, 0, this.getWidth(), this.getHeight());

			// get the starting tile
			// TileBoardIterator iter = board_.getUpperLeftCorner();
			// BufferedImage tileImage = board_.homeTile().current()
			// .getImage();

			// get the dimensions of the tile image.
			int tileWidth = 30; // (int) (tileImage.getWidth() * scale_);
			int tileHeight = 30; // (int) (tileImage.getHeight() * scale_);

			// Compute how many rows and columns are visible. We add 1 so
			// that
			// tiles are drawn right up to the edge of the screen at the
			// edges.
			int rows = this.getHeight() / tileHeight + 1;
			int cols = this.getWidth() / tileWidth + 1;

			for (int k = 0; k <= rows + 1; k++)
				gra.drawLine(0, k * tileHeight, this.getWidth(), k
						* tileHeight);

			for (int k = 0; k <= cols + 1; k++)
				gra.drawLine(k * tileWidth, 0, k * tileWidth, this
						.getHeight());

			// Place tile images by iterating through the board and wrapping
			// when we
			// reach
			// the end of a row.
			/*
			 * try { int i, x, y; i = x = y = 0; while (i < (rows cols) //
			 * && (iter.current() != null || iter.nextRow() != null)) { ) {
			 * i++; if (iter.current() != null) {
			 * gra.drawImage(iter.current().getImage(), x, y, tileWidth,
			 * tileHeight, null); } x += tileWidth; if (i % cols == 0) { //
			 * Next row x = 0; y += tileHeight; iter.nextRow(); }
			 * iter.right();
			 * 
			 * }
			 * 
			 * } catch (Exception e) {
			 * System.out.println("Error displaying a tile image."); }
			 */

		}
	} // End Map
} // End MapLayer

