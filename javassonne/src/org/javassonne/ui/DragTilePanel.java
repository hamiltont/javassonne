/**
 * Javassonne 
 *  http://code.google.com/p/javassonne/
 * 
 * @author [Add Name Here]
 * @date Feb 15, 2009
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
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.Timer;
import java.util.TimerTask;

import org.javassonne.messaging.Notification;
import org.javassonne.messaging.NotificationManager;
import org.javassonne.model.Tile;

public class DragTilePanel extends AbstractHUDPanel implements MouseListener,
		MouseMotionListener {

	private Point mouseOffset_ = null;
	private Point resetLocation_ = null;
	private Timer resetTimer_;

	private Boolean respondToClick_ = true;

	public DragTilePanel(Tile t) {
		setSize(120, 120);
		setTile(t);
		setOpaque(false);

		addMouseListener(this);
		addMouseMotionListener(this);
	}

	public void setTile(Tile t) {
		setBackgroundImage(t.getImage());
	}

	public void mouseDragged(MouseEvent e) {
		setBackgroundAlpha(0.6f);
		
		if (respondToClick_){
			if (mouseOffset_ == null)
				mouseOffset_ = new Point(e.getX(), e.getY());
			else {
				Point c = this.getLocation();
				this.setLocation(c.x + e.getX() - mouseOffset_.x, c.y + e.getY()
						- mouseOffset_.y);
			}
		}
	}

	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void mousePressed(MouseEvent e) {
		if (respondToClick_){
			if (resetTimer_ != null) resetTimer_.cancel();
			resetLocation_ = this.getLocation();
			repaint();
		}
	}

	public void mouseReleased(MouseEvent e) {
		if (respondToClick_){
			// see if we can place the tile on the map.
			Point clickLocation = this.getLocation();
			clickLocation.x += this.getWidth() / 2;
			clickLocation.y += this.getHeight() / 2;
	
			// slide the tile back to it's starting location on the sidebar
			if (resetTimer_ != null) resetTimer_.cancel();
			resetTimer_ = new Timer();
			resetTimer_.scheduleAtFixedRate(new ResetSlideTask(), 0, 5);
			respondToClick_ = false;
			
			NotificationManager.getInstance().sendNotification(
					Notification.TILE_DROPPED, clickLocation);
		}
	}

	protected class ResetSlideTask extends TimerTask {
		private double dx;
		private double dy;
		private double x;
		private double y;

		public ResetSlideTask() {
			dx = (double) (resetLocation_.x - getLocation().x) / 40.0;
			dy = (double) (resetLocation_.y - getLocation().y) / 40.0;
			x = getLocation().x;
			y = getLocation().y;
		}

		public void run() {
			x += dx;
			y += dy;
			setLocation(new Point((int) x, (int) y));

			// if we are now in the starting location, make us opaque again and
			// stop the timer from firing. Also set the location to the exact
			// one, just in case.
			if (x <= resetLocation_.x || y <= resetLocation_.y) {
				setLocation(resetLocation_);
				setBackgroundAlpha(1.0f);
				
				resetTimer_.cancel();
				respondToClick_ = true;
			}
		}
	}
}
