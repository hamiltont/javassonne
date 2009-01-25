/**
 * Javassonne 
 *  http://code.google.com/p/javassonne/
 * 
 * @author Hamilton Turner
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
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;

import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JViewport;

import org.javassonne.model.TileBoard;
import org.javassonne.model.TileBoardGenIterator;
import org.javassonne.model.TileBoardIterator;

/**
 * WorldCanvas acts as the central controller for anyone wanting to interact
 * with the map, and as a logical container to hold all of the layers that are
 * displayed on the screen
 * 
 * Note that each layer of the JlayeredPane will be the same size, and the
 * JLayeredPane itself will also be that size. Calling setSize inappropriately
 * on this class, or any layer class may result in the map being scaled down or 
 * controls not rendering correctly.
 */

/**
 * @author Administrator
 * 
 */
public class WorldCanvas extends JLayeredPane {
	private TileBoard board_;
	private MapLayer default_;
	private PaletteLayer palette_;

	/**
	 * Constructor
	 * 
	 * @param board
	 *            The model to be used internally when displaying the tiles.
	 * @param screenSize
	 *            The amount of the screen that the map is allowed to use for
	 *            rendering itself.
	 */
	public WorldCanvas(TileBoard board, Dimension screenSize) {
		board_ = board;

		// First create the map, allowing it to
		// determine how large it needs to be
		default_ = new MapLayer(screenSize);

		// Initialize all other layers with the screensize,
		// so they can render relative to the screen,
		// and not the entire map
		palette_ = new PaletteLayer(screenSize, this);

		// Retrieve the map size, and setSize all
		// layers(including this) to the map size.
		// If this is not done, then the other objects will have a
		// width/height of 0/0 and will not be rendered
		// If you setSize to the screen size, instead of the mapSize,
		// then the map layer will be scaled
		Dimension mapSize = default_.getMapSize();
		setSize(mapSize);
		palette_.setSize(mapSize);

		add(default_, JLayeredPane.DEFAULT_LAYER);
		add(palette_, JLayeredPane.PALETTE_LAYER);
	}

	/**
	 * Shifts the internal JViewport so a different portion of the map is shown
	 * 
	 * @param amount
	 *            The amount the view should be shifted. Values should be
	 *            relative to position 0,0 on the normal cartesian plane. Ex:
	 *            x/y of 3,-3 would shift the map to the right 3 units, and down
	 *            3 units.
	 */
	public void shiftView(Point amount) {
		default_.shiftView(amount);
	}

	/**
	 * Redraw anything currently contailed within the WorldCanvas
	 */
	public void redraw() {
		// Redraw the map, and the palette

	}

	/**
	 * Sets the default action listener for anything contained in WorldCanvas
	 * 
	 * @param controller
	 *            default action listener
	 */
	public void setDefaultActionListener(GameWindowController controller) {
		// TODO Auto-generated method stub

	}

	/**
	 * The default panel, displayed below all others. This panel contains the
	 * JViewport that displays the map, and the grid that is displayed
	 */
	private class MapLayer extends JPanel {
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
			setSize(getMapSize());

			viewport_ = new JViewport();
			viewport_.setExtentSize(screenSize);
			viewport_.setView(map_);

			// This layout allows the viewport to expand and take
			// all available space
			setLayout(new BorderLayout());
			add(viewport_, BorderLayout.CENTER);

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
				TileBoardIterator iter = board_.getUpperLeftCorner();
				BufferedImage tileImage = board_.homeTile().current().getImage();

				// get the dimensions of the tile image.
				int tileWidth = (int) (tileImage.getWidth() * scale_);
				int tileHeight = (int) (tileImage.getHeight() * scale_);

				// Compute how many rows and columns are visible. We add 1 so
				// that
				// tiles are drawn right up to the edge of the screen at the
				// edges.
				int rows = this.getHeight() / tileHeight + 1;
				int cols = this.getWidth() / tileWidth + 1;


				for (int k = 0; k < rows; k++)
					gra.drawLine(0, k * tileHeight, tileWidth, k * tileHeight);

				for (int k = 0; k < cols; k++)
					gra.drawLine(k * tileWidth, 0, k * tileWidth, tileHeight);

				// Place tile images by iterating through the board and wrapping
				// when we
				// reach
				// the end of a row.
				try {
					int i, x, y;
					i = x = y = 0;
					while (i < (rows * cols)
					// && (iter.current() != null || iter.nextRow() != null)) {
					) {
						i++;
						if (iter.current() != null) {
							gra.drawImage(iter.current().getImage(), x, y,
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
		} // End Map
	} // End MapLayer

} // End WorldCanvas