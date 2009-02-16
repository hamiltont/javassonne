/**
 * Javassonne 
 *  http://code.google.com/p/javassonne/
 * 
 * @author Hamilton. Modified by Ben
 * @date Feb 4, 2009
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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import org.javassonne.messaging.Notification;
import org.javassonne.messaging.NotificationManager;
import org.javassonne.model.TileBoard;
import org.javassonne.model.TileBoardIterator;

/**
 * The default panel, displayed below all others. This panel is responsible for
 * rendering the map.
 */
public class MapLayer extends JPanel implements MouseListener,
		MouseMotionListener {
	private TileBoard board_;
	private BufferedImage backgroundTile_;
	private BufferedImage buffer_ = null;
	private Point paintOffset_ = new Point(0, 0);
	private Point renderOffset_ = new Point(0, 0);
	private Point renderCenteringOffset_ = new Point(0, 0);

	// To improve performance, the offscreen buffer image containing the map is
	// larger than the map view. That way, when you scroll, you can go a certain
	// distance in any direction before the map needs to be redrawn into the
	// buffer.
	private static final int bufferMaxOffsetX_ = 500;
	private static final int bufferMaxOffsetY_ = 500;

	// The background color is drawn into the buffer_ and used as a background
	// for the JPanel.
	private static final Color backgroundColor_ = Color.darkGray;

	// The scale the tiles are drawn at
	private double scale_ = 0.7;

	// For scrolling hotspots:
	private Rectangle2D navLeft_;
	private Rectangle2D navRight_;
	private Rectangle2D navTop_;
	private Rectangle2D navBottom_;
	private Timer mapShiftTimer_;
	private Point mapShift_;

	/**
	 * Constructor
	 * 
	 * @param screenSize
	 *            The size of the MapLayer.
	 */
	public MapLayer(Dimension screenSize) {

		setSize(screenSize);
		setDoubleBuffered(false);

		// Listen for notification setting our board model
		NotificationManager.getInstance().addObserver(Notification.BOARD_SET,
				this, "setBoard");

		// Listen for notifications changing the zoom
		NotificationManager.getInstance().addObserver(Notification.ZOOM_IN,
				this, "zoomIn");
		NotificationManager.getInstance().addObserver(Notification.ZOOM_OUT,
				this, "zoomOut");

		// Listen for a notification from the tile being dragged
		NotificationManager.getInstance().addObserver(Notification.TILE_DROPPED,
				this, "tileDropped");
		
		// Load the background image from disk
		try {
			backgroundTile_ = ImageIO.read(new File(
					"images/background_tile.jpg"));
		} catch (Exception e) {
			NotificationManager.getInstance().sendNotification(
					Notification.LOG_ERROR,
					"The backgound tile could not be loaded from disk");
		}

		// Setup the scroll hotspots
		navLeft_ = new Rectangle2D.Double(0, 0, 10, screenSize.getHeight());
		navRight_ = new Rectangle2D.Double(screenSize.getWidth() - 10, 0, 10,
				screenSize.getHeight());
		navTop_ = new Rectangle2D.Double(0, 0, screenSize.getWidth(), 10);
		navBottom_ = new Rectangle2D.Double(0, screenSize.getHeight() - 10,
				screenSize.getWidth(), 10);

		// add the mouse motion listener to enable the scroll hotspots and the
		// click listener so we can detect clicks.
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
	}

	public void setBoard(Notification n) {
		board_ = (TileBoard) n.argument();
		renderBoard();
		repaint();
	}

	/**
	 * ShiftView allows you to pan the map by passing a scroll amount. This
	 * function is used by the MapScrollEdges to move the map when the user
	 * moves the mouse into a hotspot.
	 * 
	 * @param amount
	 *            Positive values for X and Y indicate left and down,
	 *            respectively.
	 */
	public void shiftView(Point amount) {
		paintOffset_.y += amount.y;
		paintOffset_.x += amount.x;

		// If we've scrolled far enough to reach part of the map not drawn into
		// the buffer image, we need to push the paintOffset into the
		// renderOffset and then re-render the board.
		if ((paintOffset_.x < -bufferMaxOffsetX_)
				|| (paintOffset_.x > bufferMaxOffsetX_)
				|| (paintOffset_.y < -bufferMaxOffsetY_)
				|| (paintOffset_.y > bufferMaxOffsetY_)) {

			// Add the current paintOffset to the renderOffset.
			renderOffset_.x += paintOffset_.x;
			renderOffset_.y += paintOffset_.y;
			paintOffset_ = new Point(0, 0);

			// Notify the user that we've redrawn (for dev, anyway)
			NotificationManager.getInstance().sendNotification(
					Notification.LOG_WARNING,
					"Rerendering the board into offscreen buffer.");

			// Rerender the board to "commit" or changes to the offset.
			renderBoard();
		}

		// Paint the buffer to the screen at the pointOffset.
		repaint();
	}

	/**
	 * Handles the ZOOM_IN notification and re-renders the board at a higher
	 * zoom level.
	 * 
	 * @param n
	 */
	public void zoomIn(Notification n) {
		if (scale_ < 1) {
			scale_ += 0.1;
			renderBoard();
			repaint();
			NotificationManager.getInstance().sendNotification(
					Notification.ZOOM_CHANGED, this);
		}
	}

	/**
	 * Handles the ZOOM_OUT notification and re-renders the board at a lower
	 * zoom level.
	 * 
	 * @param n
	 */
	public void zoomOut(Notification n) {
		if (scale_ > 0.5) {
			scale_ -= 0.1;
			renderBoard();
			repaint();
			NotificationManager.getInstance().sendNotification(
					Notification.ZOOM_CHANGED, this);
		}
	}

	// Zoomed all the way in?
	public boolean zoomedMax() {
		return (scale_ > 1);
	}

	// Zoomed all the way out?
	public boolean zoomedMin() {
		return (scale_ < .6);
	}

	public void paint(Graphics gra) {

		int w = this.getWidth();
		int h = this.getHeight();

		// paint a solid background color
		gra.setColor(backgroundColor_);
		gra.fillRect(0, 0, w, h);

		// if we've rendered the board into the buffer image, draw the buffer
		// image onto the screen using the "paintOffset_" to determine where it
		// goes.
		if (buffer_ != null) {
			gra.drawImage(buffer_, 0, 0, w, h, paintOffset_.x
					+ bufferMaxOffsetX_, paintOffset_.y + bufferMaxOffsetY_, w
					+ paintOffset_.x + bufferMaxOffsetX_, h + paintOffset_.y
					+ bufferMaxOffsetY_, null);
		}
	}

	/**
	 * renderBoard does the heavy lifting involved in redrawing the buffer
	 * image. The buffer image contains the map graphics, and is drawn to the
	 * screen repeatedly (without iterating through the tiles) when the user
	 * scrolls. It must be re-rendered when a new tile is added, etc..
	 */
	public void renderBoard() {
		if (board_ == null)
			return;

		try {
			// paint the board background from the top left to the bottom
			// right
			Point topLeft = board_.getUpperLeftCorner().getLocation();
			Point bottomRight = board_.getLowerRightCorner().getLocation();

			// Compute the size of the board, size of the tiles, etc...
			int boardWidth = (int) (Math.abs(bottomRight.getX() - topLeft.getX()) + 1);
			int boardHeight = (int) (Math.abs(topLeft.getY() - bottomRight.getY()) + 1);

			int tileWidth = (int) (backgroundTile_.getWidth() * scale_);
			int tileHeight = (int) (backgroundTile_.getHeight() * scale_);

			int bufferWidth = this.getWidth() + bufferMaxOffsetX_ * 2;
			int bufferHeight = this.getHeight() + bufferMaxOffsetY_ * 2;

			// determine what the offset should be to center the game board in
			// the image we'll create.
			renderCenteringOffset_.x = (bufferWidth - boardWidth * tileWidth) / 2;
			renderCenteringOffset_.y = (bufferHeight - boardHeight * tileHeight) / 2;

			// create the buffered image if it doesn't already exist.
			if (buffer_ == null) {
				buffer_ = new BufferedImage(bufferWidth, bufferHeight,
						BufferedImage.TYPE_INT_ARGB);
			}

			// clear the buffer to a dark gray
			Graphics gra = buffer_.getGraphics();
			gra.setColor(Color.darkGray);
			gra.fillRect(0, 0, bufferWidth, bufferHeight);

			// get the tile iterator.
			TileBoardIterator iter = board_.getUpperLeftCorner();

			// TODO: Right now, we're drawing every tile on the map, even if
			// they are off to one side and not actually visible in the buffer.
			// This is OK I think, but it is NOT efficient.

			for (int y = 0; y < boardHeight; y++) {
				for (int x = 0; x < boardWidth; x++) {

					// determine where the tile should be drawn, taking into
					// account the tile's position on the map, the portion of
					// the map that is being rendered into the buffer, and any
					// movement we're doing to center it.
					int drawX = x * tileWidth - renderOffset_.x
							+ renderCenteringOffset_.x;
					int drawY = y * tileHeight - renderOffset_.y
							+ renderCenteringOffset_.y;

					// paint the tile if it exists. Otherwise paint the
					// background image
					if (iter.current() != null) {
						gra.drawImage(iter.current().getImage(), drawX, drawY,
								tileWidth, tileHeight, null);
					} else {
						gra.drawImage(backgroundTile_, drawX, drawY, tileWidth,
								tileHeight, null);
					}
					iter.right();
				}
				iter.nextRow();
			}

		} catch (Exception e) {
			System.out.println("Error displaying a tile image.");
		}
	}

	public Point getTileAtPoint(Point p)
	{
		
		// determine which tile was clicked! First, get the width and height of
		// a tile.
		int tileWidth = (int) (backgroundTile_.getWidth() * scale_);
		int tileHeight = (int) (backgroundTile_.getHeight() * scale_);

		// find out what the absolute location of the click was. This involves
		// modifying the coordinates we received so that the location is
		// relative to the buffer image. Once we know which pixel of the
		// buffered image was clicked, we can get a tile.
		p.x += -renderCenteringOffset_.x + bufferMaxOffsetX_;
		p.x += paintOffset_.x + renderOffset_.x;
		p.y += -renderCenteringOffset_.y + bufferMaxOffsetY_;
		p.y += paintOffset_.y + renderOffset_.y;

		// get the tile index by dividing by the tile width and height
		int tileX = (int) Math.floor(p.x / tileWidth);
		int tileY = (int) Math.floor(p.y / tileHeight);

		// that tile index is relative to the top left. We need to make the
		// index relative to the home tile for it to be useful.
		tileX += board_.getUpperLeftCorner().getLocation().getX();
		tileY = (int) (board_.getUpperLeftCorner().getLocation().getY() - tileY);

		// send a notification!
		String text = String.format("You clicked tile %d,%d", tileX, tileY);
		NotificationManager.getInstance().sendNotification(
				Notification.LOG_WARNING, text);
		
		Point location = new Point(tileX, tileY);
		return location;
	}
	
	public void tileDropped(Notification n) {
		if (board_ == null)
			return;
		
		Point p = this.getTileAtPoint((Point)n.argument());
		NotificationManager.getInstance().sendNotification(
				Notification.CLICK_ADD_TILE, p);
	}
	
	public void mouseClicked(MouseEvent e) {

		if (board_ == null)
			return;
		
		Point p = this.getTileAtPoint(e.getPoint());
		NotificationManager.getInstance().sendNotification(
				Notification.CLICK_ADD_TILE, p);
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseDragged(MouseEvent e) {
	}

	public void mouseMoved(MouseEvent e) {
		Point current = e.getPoint();
		int dx = 0;
		int dy = 0;
		int delta = 2;

		// are we inside one of the directional containers?
		if (navRight_.contains(current))
			dx = delta;
		if (navLeft_.contains(current))
			dx = -delta;
		if (navTop_.contains(current))
			dy = -delta;
		if (navBottom_.contains(current))
			dy = delta;

		// if we are within a container and we're not scrolling, create a
		// task to fire the "shift" event over and over again, until we leave
		// the container
		if ((dx != 0) || (dy != 0)) {
			Point newShift = new Point(dx, dy);

			if ((mapShiftTimer_ == null) || (mapShift_ != newShift)) {
				WorldScrollTask task = new WorldScrollTask();
				task.setOffset(newShift);

				mapShift_ = newShift;

				if (mapShiftTimer_ != null)
					mapShiftTimer_.cancel();
				mapShiftTimer_ = new Timer();
				mapShiftTimer_.schedule(task, 0, 5);
			}

		} else if (mapShiftTimer_ != null) {
			// if we have left a directional container and the timer still
			// exists, cancel it so we stop sending "shift" events to the map
			// view.
			mapShiftTimer_.cancel();
			mapShiftTimer_ = null;
		}
	}

	class WorldScrollTask extends TimerTask {
		private Point p_;

		public void setOffset(Point p) {
			this.p_ = p;
		}

		public void run() {
			shiftView(this.p_);
		}
	}
}
