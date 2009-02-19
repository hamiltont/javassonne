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

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
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
	
	private BufferedImage translucentImage_;
	private BufferedImage opaqueImage_;

	public DragTilePanel(Tile t) {
		setSize(120, 120);
		setTile(t);
		setOpaque(false);

		addMouseListener(this);
		addMouseMotionListener(this);
	}

	public void setTile(Tile t) {
		opaqueImage_ = t.getImage();

		int w = opaqueImage_.getWidth();
		int h = opaqueImage_.getHeight();

		// create a semi-transparent version of the image that we'll use when we
		// are dragging.
		BufferedImage mask = new BufferedImage(w, h,
				BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D maskG = mask.createGraphics();
		maskG.setColor(new Color(0f, 0f, 0f, 0.6f));
		maskG.fillRect(0, 0, w, h);
		maskG.dispose();

		translucentImage_ = new BufferedImage(w, h,
				BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g2 = translucentImage_.createGraphics();
		g2.drawImage(t.getImage(), 0, 0, null);
		AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.DST_IN,
				1.0F);
		g2.setComposite(ac);
		g2.drawImage(mask, 0, 0, null);
		g2.dispose();

		setBackgroundImage(opaqueImage_);
		repaint();
	}

	public void mouseDragged(MouseEvent e) {
		setBackgroundImage(translucentImage_);
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
				setBackgroundImage(opaqueImage_);
				resetTimer_.cancel();
				
				respondToClick_ = true;
			}
		}
	}
}
