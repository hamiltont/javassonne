/**
 * Javassonne 
 *  http://code.google.com/p/javassonne/
 * 
 * @author Hamilton Turner
 * @date Jan 24, 2009
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

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JPanel;

class MapScrollEdges extends JPanel implements MouseMotionListener {
	private static final long serialVersionUID = 1L;

	private Rectangle2D navLeft_;
	private Rectangle2D navRight_;
	private Rectangle2D navTop_;
	private Rectangle2D navBottom_;
	private MapLayer map_;

	private Timer mapShiftTimer_;
	private Point mapShift_;

	/**
	 * Constructor
	 * 
	 * @param map
	 *            The mapLayer that this MapScrollEdges instance should be
	 *            responsible for scrolling. The mapScrollEdges will be placed
	 *            directly on top of the given map.
	 */
	public MapScrollEdges(MapLayer map) {
		setOpaque(false);
		setVisible(true);
		setSize(map.getSize());

		map_ = map;
		mapShiftTimer_ = null;

		navLeft_ = new Rectangle2D.Double(0, 0, 10, map_.getHeight());
		navRight_ = new Rectangle2D.Double(map_.getWidth() - 10, 0, 10, map_
				.getHeight());
		navTop_ = new Rectangle2D.Double(0, 0, map_.getWidth(), 10);
		navBottom_ = new Rectangle2D.Double(0, map_.getHeight() - 10, map_
				.getWidth(), 10);

		// our layer completely covers the map layer, so in order for the map to
		// be clickable, we have to pass the click events through ourselves.
		this.addMouseListener(map);
		
		// add the mouse motion listener to enable the scroll hotspots
		this.addMouseMotionListener(this);
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseDragged(MouseEvent e) {
	}

	public void mouseMoved(MouseEvent e) {
		Point current = e.getPoint();
		int dx = 0;
		int dy = 0;
		int delta = 1;

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
			map_.shiftView(this.p_);
		}
	}
}
