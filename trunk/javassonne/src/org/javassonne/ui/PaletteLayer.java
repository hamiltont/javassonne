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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JPanel;

/**
 * Holds all information needed to draw the palette layer on the WorldCanvas
 */
class PaletteLayer extends JPanel implements MouseMotionListener, MouseListener {
	private Graphics layer_;
	private Rectangle2D navTopLeft_;
	private Rectangle2D navTopRight_;
	private Rectangle2D navBottomLeft_;
	private Rectangle2D navBottomRight_;
	private WorldCanvas mapController_;
	private Dimension screenSize_; // Used to maintain this class' size
	private Timer mapShiftTimer_;
	
	/**
	 * Constructor
	 * @param screenSize
	 * 				The dimensions of the amount of screen realestate 
	 * 				alotted to the world canvas
	 * @param mapController
	 * 				The interface that allows this class to interact
	 * 				and request actions on the map
	 */
	public PaletteLayer(Dimension screenSize, WorldCanvas mapController) {
		setOpaque(false);
		screenSize_ = screenSize;
		int width = screenSize_.width;
		int height = screenSize_.height;

		mapController_ = mapController;
		mapShiftTimer_ = null;
		
		navTopLeft_ = new Rectangle2D.Double(0, 0, 40, 40);
		navTopRight_ = new Rectangle2D.Double(width - 40, 0, 50, 50);
		navBottomLeft_ = new Rectangle2D.Double(0, height - 40, 40, 40);
		navBottomRight_ = new Rectangle2D.Double(width - 40, height - 40,
				40, 40);
		
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
	}

	/**
	 * Override of the default JPanel function to 
	 * 		render the palette layer controls to the 
	 * 		world canvas
	 */
	public void paintComponent(Graphics g) {
		layer_ = g;

		Graphics2D g2 = (Graphics2D) g;
		g2.setPaint(Color.GREEN);
		g2.fill(navTopLeft_);
		g2.fill(navTopRight_);
		g2.fill(navBottomLeft_);
		g2.fill(navBottomRight_);

	}

	/**
	 * Redraw this layer
	 */
	public void redraw() {
		// Redraw the board
	}

	/**
	 * After setting this, any action events this class generates will be sent
	 * to a
	 * 
	 * @param a
	 *            The ActionListener events should be sent to
	 */
	
	public void setActionListener(ActionListener a) {
		// Register event listener
	}
	
	public void mouseDragged(MouseEvent e){
	}
	
	public void mouseReleased(MouseEvent e)
	{
	}
	
	public void mousePressed(MouseEvent e)
	{
	}
	
	public void mouseClicked(MouseEvent e)
	{
	}
	
	public void mouseEntered(MouseEvent e)
	{
	}
	
	public void mouseExited(MouseEvent e)
	{
	}
	
	public void mouseMoved(MouseEvent e)
	{
		Point current = e.getPoint();
		Point offset = null;
		
		int delta = 2;

		// are we inside one of the directional containers?
		if (navBottomRight_.contains(current))
			offset = new Point(delta, delta);
		else if (navBottomLeft_.contains(current))
			offset = new Point(-delta, delta);
		else if (navTopRight_.contains(current))
			offset = new Point(delta, -delta);
		else if (navTopLeft_.contains(current))
			offset = new Point(-delta, -delta);

		// if we are within a container and we're not scrolling, create a 
		// task to fire the "shift" event over and over again, until we leave the container
		if (offset != null){
			if (mapShiftTimer_ == null)
			{
				ShiftTask task = new ShiftTask();
				task.setOffset(offset);
				
				mapShiftTimer_ = new Timer();
				mapShiftTimer_.schedule(task, 0, 2);
			}
		} else if (mapShiftTimer_ != null){
			// if we have left a directional container and the timer still exists,
			// cancel it so we stop sending "shift" events to the map view.
			mapShiftTimer_.cancel();
			mapShiftTimer_ = null;
		}
	}
	
	class ShiftTask extends TimerTask {
		private Point p_;
		public void setOffset(Point p)
		{
			this.p_ = p;
		}
	    
		public void run() {
	    	mapController_.shiftView(this.p_);
	    }
	  }
}
