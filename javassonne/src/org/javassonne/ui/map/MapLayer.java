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

package org.javassonne.ui.map;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import org.javassonne.messaging.Notification;
import org.javassonne.messaging.NotificationManager;
import org.javassonne.model.Meeple;
import org.javassonne.model.Tile;
import org.javassonne.model.TileBoard;
import org.javassonne.model.TileBoardGenIterator;
import org.javassonne.model.TileBoardIterator;
import org.javassonne.networking.ChatManager;
import org.javassonne.ui.GameState;
import org.javassonne.ui.JKeyListener;

/**
 * The default panel, displayed below all others. This panel is responsible for
 * rendering the map.
 */
public class MapLayer extends JPanel implements MouseListener,
		MouseMotionListener {
	private BufferedImage backgroundTile_;

	private BufferedImage boardBuffer_ = null;
	private HashMap<Object, ArrayList<MapSprite>> sprites_ = null;

	private Timer updateTimer_ = null;
	private long updateLastMilliseconds_ = 0;
	private int updateFPS_ = 0;

	private Point paintOffset_ = new Point(0, 0);
	private Point renderOffset_ = new Point(0, 0);
	private Point renderCenteringOffset_ = new Point(0, 0);

	// To improve performance, the offscreen buffer image containing the map is
	// larger than the map view. That way, when you scroll, you can go a certain
	// distance in any direction before the map needs to be redrawn into the
	// buffer.
	private static final int BUFFER_MAX_OFFSET_X = 500;
	private static final int BUFFER_MAX_OFFSET_Y = 500;

	// The background color is drawn into the buffer_ and used as a background
	// for the JPanel.
	private static final Color BACKGROUND_COLOR = Color.darkGray;
	private static final int MAP_SHIFT_SPEED = 10;

	// The scale the tiles are drawn at
	private static double scale_ = 0.7;

	// For scrolling hotspots:
	private Rectangle2D navLeft_;
	private Rectangle2D navRight_;
	private Rectangle2D navTop_;
	private Rectangle2D navBottom_;
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
		setFocusable(true);
		setFocusCycleRoot(true);
		requestFocus();
		
		// Listen for notification setting our board model
		NotificationManager n = NotificationManager.getInstance();
		n.addObserver(Notification.UPDATED_GAME_IN_PROGRESS, this, "updatedBoard");
		n.addObserver(Notification.UPDATED_BOARD, this, "updatedBoard");
		// Listen for end-game so we know we need to clear the board.
		n.addObserver(Notification.END_GAME, this, "endGame");
		// Listen for notifications changing the zoom
		n.addObserver(Notification.ZOOM_IN, this, "zoomIn");
		n.addObserver(Notification.ZOOM_OUT, this, "zoomOut");
		// Listen for notifications for adding and removing sprites
		n.addObserver(Notification.MAP_ADD_SPRITE, this, "addSprite");
		n.addObserver(Notification.MAP_REMOVE_SPRITE, this, "removeSprite");
		n.addObserver(Notification.MAP_REMOVE_SPRITE_GROUP, this, "removeSpriteGroup");
		n.addObserver(Notification.MAP_REDRAW, this, "repaint");
		// Listen for a notification from the tile or a meeple being dragged
		n.addObserver(Notification.TILE_DROPPED, this, "tileDropped");
		n.addObserver(Notification.MEEPLE_VILLAGER_DROPPED, this, "meepleVillagerDropped");
		n.addObserver(Notification.MEEPLE_FARMER_DROPPED, this, "meepleFarmerDropped");
		n.addObserver(Notification.SHIFT_BOARD, this, "shiftBoard");
		n.addObserver(Notification.CHAT_TEXT_CHANGED, this, "repaint");
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
		this.addKeyListener(JKeyListener.getInstance());

		// create the buffers we need for drawing
		int bufferWidth = this.getWidth() + BUFFER_MAX_OFFSET_X * 2;
		int bufferHeight = this.getHeight() + BUFFER_MAX_OFFSET_Y * 2;
		boardBuffer_ = new BufferedImage(bufferWidth, bufferHeight,
				BufferedImage.TYPE_INT_RGB);
		renderBoard();

		// create the sprites array
		sprites_ = new HashMap<Object, ArrayList<MapSprite>>();
	}

	// ------------------------------------------------------------------------
	// SPRITE MANAGEMENT AND BOARD ANIMATION
	// ------------------------------------------------------------------------

	public void addSprite(MapSprite s, Object group) {
		ArrayList<MapSprite> g = sprites_.get(group);
		if (g == null) g = new ArrayList<MapSprite>();
		sprites_.put(group, g);
		
		s.setGroup(group);
		g.add(s);
		
		recalculateUpdateFrequency();
	}

	public void addSprite(Notification n) {
		MapSprite s = (MapSprite) n.argument();
		this.addSprite(s, s.getGroup());
		s.addedToMap(this);
	}

	public void removeSprite(MapSprite s) {
		Object groupKey = s.getGroup();
		ArrayList<MapSprite> group = sprites_.get(groupKey);
		if (group != null){
			group.remove(s);
			if (group.size() == 0)
				sprites_.remove(groupKey);
		}
		recalculateUpdateFrequency();
	}

	public void removeSprite(Notification n) {
		this.removeSprite((MapSprite) n.argument());
	}
	
	public void removeSpriteGroup(Object group) {
		sprites_.remove(group);
	}
	
	public void removeSpriteGroup(Notification n) {
		this.removeSpriteGroup((Object)n.argument());
	}

	protected void update() {
		// we need to iterate through the list backwards in case
		// any of the sprites remove themselves.
		for (Object group : sprites_.keySet()) {
			ArrayList<MapSprite> s = sprites_.get(group);
			for (int ii = s.size() - 1; ii >= 0; ii--) {
				s.get(ii).update(this);
			}
		}

		if (mapShift_ != null)
			shiftView(mapShift_);

		long m = Calendar.getInstance().getTimeInMillis();
		updateFPS_ = (int) (1000 / (m - updateLastMilliseconds_ + 1));
		updateLastMilliseconds_ = m;

		repaint();
	}

	public void recalculateUpdateFrequency() {
		if (updateTimer_ != null) {
			updateTimer_.cancel();
			updateTimer_ = null;
		}

		TileBoard board = GameState.getInstance().getBoard();
		if (board == null)
			return;

		// determine if the timer should be firing or not. If any of our sprites
		// need to be animated, or if the map is shifting, we want 20FPS.
		// Otherwise
		// 2 FPS will do.

		Boolean spriteActive = false;
		for (Object group : sprites_.keySet()) {
			for (MapSprite s : sprites_.get(group)) {
				if (s.isAnimating()) {
					spriteActive = true;
					break;
				}
			}
		}

		if (spriteActive || (mapShift_ != null)) {
			updateTimer_ = new Timer("MapLayer UpdateTask");
			updateTimer_.scheduleAtFixedRate(new UpdateTask(), 50, 50); // 20
			// FPS
		} else {
			updateTimer_ = new Timer("MapLayer UpdateTask");
			updateTimer_.scheduleAtFixedRate(new UpdateTask(), 50, 500); // 2
			// FPS
		}
	}

	protected class UpdateTask extends TimerTask {
		public void run() {
			update();
		}
	}

	// ------------------------------------------------------------------------
	// FOLLOWING GAME STATE
	// ------------------------------------------------------------------------

	public void updatedBoard(Notification n) {
		requestFocus();
		renderBoard();
		repaint();
	}

	public void endGame(Notification n) {
		// let go of the board. It should not be used once this notification
		// is received and setting to null allows us to make sure this is
		// followed.
		sprites_.clear();

		// reset the board scroll offset, in case they start a new game
		paintOffset_ = new Point(0, 0);
		renderOffset_ = new Point(0, 0);

		renderBoard();
		repaint();
	}

	public void tileDropped(Notification n) {
		TileBoard board = GameState.getInstance().getBoard();
		if (board == null)
			return;

		Point p = this.getTileAtScreenPoint((Point) n.argument());
		NotificationManager.getInstance().sendNotification(
				Notification.PLACE_TILE, p);
	}

	public void meepleVillagerDropped(Notification n)
	{
		TileBoard board = GameState.getInstance().getBoard();
		if (board == null)
			return;

		Point dropPoint = (Point) n.argument();
		Point tileLocation = this.getTileAtScreenPoint((Point)dropPoint.clone());
		Point offset = this.getOffsetWithinTileAtScreenPoint((Point)dropPoint.clone());

		// determine which region the meeple was closest to
		Tile.Region lowest = null;
		double lowestDist = 10000;

		// compute pythagorean distance to all the feature centers, and see
		// which center we're closest too. This is the feature that the user
		// probably wanted.
		for (Tile.Region r : Tile.Region.values()) {
			double dist = Math.sqrt(Math.pow(r.x - offset.x, 2)
					+ Math.pow(r.y - offset.y, 2));
			if (dist < lowestDist) {
				lowest = r;
				lowestDist = dist;
			}
		}

		Meeple m = new Meeple();
		m.setParentTileLocation(tileLocation);
		m.setParentTile(board.getTile(new TileBoardGenIterator(board,
				tileLocation)));
		m.setRegionOnTile(lowest);

		NotificationManager.getInstance().sendNotification(Notification.PLACE_VILLAGER_MEEPLE, m);
	}
	
	public void meepleFarmerDropped(Notification n)
	{
		TileBoard board = GameState.getInstance().getBoard();
		if (board == null)
			return;

		Point dropPoint = (Point) n.argument();
		Point tileLocation = this.getTileAtScreenPoint((Point)dropPoint.clone());
		Point offset = this.getOffsetWithinTileAtScreenPoint((Point)dropPoint.clone());

		Meeple m = new Meeple();
		m.setParentTileLocation(tileLocation);
		m.setParentTile(board.getTile(new TileBoardGenIterator(board,
				tileLocation)));
		for (Tile.Quadrant q : Tile.Quadrant.values()) {
			if (q.rect.contains(offset))
				m.setQuadrantOnTile(q);
		}

		NotificationManager.getInstance().sendNotification(Notification.PLACE_FARMER_MEEPLE, m);
	}

	// ------------------------------------------------------------------------
	// ZOOM AND PAN OPERATIONS
	// ------------------------------------------------------------------------

	/**
	 * Handles the ZOOM_IN notification and re-renders the board at a higher
	 * zoom level.
	 * 
	 * @param n
	 */
	public void zoomIn(Notification n) {
		if (!zoomedMax()) {
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
		if (!zoomedMin()) {
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
		return (scale_ < .4);
	}

	void setShift(int dx, int dy) {
		Point newShift = null;
		if (dx != 0 || dy != 0)
			newShift = new Point(dx, dy);

		mapShift_ = newShift;
		recalculateUpdateFrequency();
	}
	
	public static double getScale(){
		return scale_;
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
		TileBoard board = GameState.getInstance().getBoard();
		// paint the board background from the top left to the bottom
		// right
		Point topLeft = board.getUpperLeftCorner().getLocation();
		Point bottomRight = board.getLowerRightCorner().getLocation();

		// Compute the size of the board, size of the tiles, etc...
		// and determine if we can actually move.
		int tileSize = (int) (backgroundTile_.getWidth() * scale_);

		int maxScrollX = (int) (((bottomRight.getX() - topLeft.getX()) * tileSize) / 2);
		int maxScrollY = (int) (((bottomRight.getY() - topLeft.getY()) * tileSize) / 2);

		int possibleX = paintOffset_.x + renderOffset_.x + amount.x;
		int possibleY = paintOffset_.y + renderOffset_.y + amount.y;

		if ((amount.x < 0 && -maxScrollX < possibleX)
				|| (amount.x > 0 && maxScrollX > possibleX))
			paintOffset_.x += amount.x;

		if ((amount.y > 0 && -maxScrollY > possibleY)
				|| (amount.y < 0 && maxScrollY < possibleY))
			paintOffset_.y += amount.y;

		// If we've scrolled far enough to reach part of the map not drawn into
		// the buffer image, we need to push the paintOffset into the
		// renderOffset and then re-render the board.
		if ((Math.abs(paintOffset_.x) > BUFFER_MAX_OFFSET_X)
				|| (Math.abs(paintOffset_.y) > BUFFER_MAX_OFFSET_Y)) {

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
	}

	// ------------------------------------------------------------------------
	// RENDERING AND DISPLAY
	// ------------------------------------------------------------------------

	public synchronized void paint(Graphics gra) {

		TileBoard board = GameState.getInstance().getBoard();
		
		int w = this.getWidth();
		int h = this.getHeight();

		// if we've rendered the board into the buffer image, draw the buffer
		// image onto the screen using the "paintOffset_" to determine where it
		// goes.
		int bufferRegionX = (int) ((paintOffset_.x + BUFFER_MAX_OFFSET_X));
		int bufferRegionY = (int) ((paintOffset_.y + BUFFER_MAX_OFFSET_Y));

		int bufferRegionWidth = (int) ((w + paintOffset_.x + BUFFER_MAX_OFFSET_X));
		int bufferRegionHeight = (int) ((h + paintOffset_.y + BUFFER_MAX_OFFSET_Y));

		if (boardBuffer_ != null) {
			gra.drawImage(boardBuffer_, 0, 0, w, h, bufferRegionX,
					bufferRegionY, bufferRegionWidth, bufferRegionHeight, null);
		}

		// now draw all of the sprites
		if (board != null){
			if (sprites_.size() > 0) {
				Point offset = getScreenPointFromTileLocation(board.homeTile()
						.getLocation());
				
				for (Object group : sprites_.keySet()) {
					for (MapSprite s : sprites_.get(group)) {
						s.draw(gra, offset, scale_);
					}
				}
			}
			
			// draw on the fps
			gra.setColor(Color.BLACK);
			//gra.drawString(String.format("%d FPS", updateFPS_), 10, 240);
		
			// draw the chat messages
			Iterator<String> iter = ChatManager.getIterator();
			int ii = 0;
			while (iter.hasNext()){
				String s = iter.next();
				gra.setColor(Color.BLACK);
				gra.drawString(s, this.getWidth() - 549, this.getHeight()-109+ii*20);
				gra.setColor(Color.WHITE);
				gra.drawString(s, this.getWidth() - 550, this.getHeight()-110+ii*20);
				ii++;
			}
		}
	}

	/**
	 * renderBoard does the heavy lifting involved in redrawing the buffer
	 * image. The buffer image contains the map graphics, and is drawn to the
	 * screen repeatedly (without iterating through the tiles) when the user
	 * scrolls. It must be re-rendered when a new tile is added, etc..
	 */
	public void renderBoard() {
		TileBoard board = GameState.getInstance().getBoard();
		
		if (board == null) {
			Graphics gra = boardBuffer_.getGraphics();

			// paint a solid background color
			gra.setColor(BACKGROUND_COLOR);
			gra.fillRect(0, 0, boardBuffer_.getWidth(), boardBuffer_
					.getHeight());
		}

		try {
			// paint the board background from the top left to the bottom
			// right
			Point topLeft = board.getUpperLeftCorner().getLocation();
			Point bottomRight = board.getLowerRightCorner().getLocation();

			// Compute the size of the board, size of the tiles, etc...
			int boardWidth = (int) (Math.abs(bottomRight.getX()
					- topLeft.getX()) + 1);
			int boardHeight = (int) (Math.abs(topLeft.getY()
					- bottomRight.getY()) + 1);

			int tileWidth = (int) (backgroundTile_.getWidth() * scale_);
			int tileHeight = (int) (backgroundTile_.getHeight() * scale_);

			int bufferWidth = this.getWidth() + BUFFER_MAX_OFFSET_X * 2;
			int bufferHeight = this.getHeight() + BUFFER_MAX_OFFSET_Y * 2;

			// determine what the offset should be to center the game board in
			// the image we'll create.
			renderCenteringOffset_.x = (bufferWidth - boardWidth * tileWidth) / 2;
			renderCenteringOffset_.y = (bufferHeight - boardHeight * tileHeight) / 2;

			// clear the buffer to a dark gray
			Graphics gra = boardBuffer_.getGraphics();
			gra.setColor(BACKGROUND_COLOR);
			gra.fillRect(0, 0, bufferWidth, bufferHeight);

			// get the tile iterator.
			TileBoardIterator iter = board.getUpperLeftCorner();

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

	// ------------------------------------------------------------------------
	// CONVENIENCE FUNCTIONS
	// ------------------------------------------------------------------------

	public Point getTileAtScreenPoint(Point p) {

		TileBoard board = GameState.getInstance().getBoard();
		// determine which tile was clicked! First, get the width and height of
		// a tile.
		int tileSize = (int) (backgroundTile_.getWidth() * scale_);

		// find out what the absolute location of the click was. This involves
		// modifying the coordinates we received so that the location is
		// relative to the buffer image. Once we know which pixel of the
		// buffered image was clicked, we can get a tile.
		p.x += -renderCenteringOffset_.x + BUFFER_MAX_OFFSET_X;
		p.x += paintOffset_.x + renderOffset_.x;
		p.y += -renderCenteringOffset_.y + BUFFER_MAX_OFFSET_Y;
		p.y += paintOffset_.y + renderOffset_.y;

		// get the tile index by dividing by the tile width and height
		int tileX = (int) Math.floor(p.x / tileSize);
		int tileY = (int) Math.floor(p.y / tileSize);

		// that tile index is relative to the top left. We need to make the
		// index relative to the home tile for it to be useful.
		tileX += board.getUpperLeftCorner().getLocation().getX();
		tileY = (int) (board.getUpperLeftCorner().getLocation().getY() - tileY);

		// send a notification!
		String text = String.format("You clicked tile %d,%d", tileX, tileY);
		NotificationManager.getInstance().sendNotification(
				Notification.LOG_WARNING, text);

		Point location = new Point(tileX, tileY);
		return location;
	}

	public Point getOffsetWithinTileAtScreenPoint(Point p) {
		Point tileStart = this.getScreenPointFromTileLocation(this
				.getTileAtScreenPoint((Point) p.clone()));
		Point offset = new Point(p.x - tileStart.x, p.y - tileStart.y);
		
		offset.x /= scale_;
		offset.y /= scale_;
		
		return offset;
	}

	public Point getBoardPointFromTileLocation(Point p) {
		int tileSize = (int) (backgroundTile_.getWidth());
		int X = (int) (p.x * tileSize);
		int Y = (int) -(p.y * tileSize);

		return new Point(X, Y);
	}

	public Point getScreenPointFromTileLocation(Point p) {
		TileBoard board = GameState.getInstance().getBoard();
		
		int topLeftX = renderCenteringOffset_.x - renderOffset_.x
				- paintOffset_.x - BUFFER_MAX_OFFSET_X;
		int topLeftY = renderCenteringOffset_.y - renderOffset_.y
				- paintOffset_.y - BUFFER_MAX_OFFSET_Y;

		int tileSize = (int) (backgroundTile_.getWidth() * scale_);
		int extraX = (int) ((-board.getUpperLeftCorner().getLocation().getX() + p.x) * tileSize);
		int extraY = (int) ((board.getUpperLeftCorner().getLocation().getY() - p.y) * tileSize);

		return new Point(topLeftX + extraX, topLeftY + extraY);
	}

	// ------------------------------------------------------------------------
	// LISTENERS
	// ------------------------------------------------------------------------

	public void mouseClicked(MouseEvent e) {

		TileBoard board = GameState.getInstance().getBoard();
		if (board == null)
			return;

		this.requestFocusInWindow();

		Point p = this.getTileAtScreenPoint(e.getPoint());
		NotificationManager.getInstance().sendNotification(
				Notification.PLACE_TILE, p);
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

		// are we inside one of the directional containers?
		if (navRight_.contains(current))
			dx = MAP_SHIFT_SPEED;
		if (navLeft_.contains(current))
			dx = -MAP_SHIFT_SPEED;
		if (navTop_.contains(current))
			dy = -MAP_SHIFT_SPEED;
		if (navBottom_.contains(current))
			dy = MAP_SHIFT_SPEED;

		setShift(dx, dy);
	}

	public void shiftBoard(Notification n) {
		int dx = 0, dy = 0;
		if (n.argument() != null){
			int code = (Integer) n.argument();
			
			// is the user pressing a direction key?
			if (code == KeyEvent.VK_RIGHT)
				dx = MAP_SHIFT_SPEED;
			if (code == KeyEvent.VK_LEFT)
				dx = -MAP_SHIFT_SPEED;
			if (code == KeyEvent.VK_UP)
				dy = -MAP_SHIFT_SPEED;
			if (code == KeyEvent.VK_DOWN)
				dy = MAP_SHIFT_SPEED;
			
			setShift(dx, dy);
		} else {
			setShift(0, 0);
		}
	}

}
