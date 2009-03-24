/**
 * Javassonne 
 *  http://code.google.com/p/javassonne/
 * 
 * @author Brian Salisbury
 * @date Jan 14, 2009
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

package org.javassonne.ui.panels;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.javassonne.messaging.Notification;
import org.javassonne.messaging.NotificationManager;
import org.javassonne.model.Tile;
import org.javassonne.model.TileDeck;
import org.javassonne.ui.DisplayHelper;
import org.javassonne.ui.JKeyListener;
import org.javassonne.ui.DisplayHelper.Layer;

/**
 * The primary JPanel in the HUD
 * 
 * @author bengotow
 * 
 */
public class HUDPanel extends AbstractHUDPanel implements ActionListener {

	private static final long serialVersionUID = 1L;

	private JButton rotateRight_;
	private JButton rotateLeft_;
	private DragTilePanel tilePanel_;

	public HUDPanel() {
		super();
		
		setBackgroundImagePath("images/hud_background.jpg");
		setVisible(true);
		setSize(144, 181);
		setLayout(null);
		
		rotateRight_ = new JButton(new ImageIcon("images/hud_rotate_right.jpg"));
		rotateRight_.setActionCommand(Notification.TILE_ROTATE_RIGHT);
		rotateRight_.addActionListener(this);
		rotateRight_.setLocation(74, 133);
		rotateRight_.setSize(58,38);
		add(rotateRight_);
		
		rotateLeft_ = new JButton(new ImageIcon("images/hud_rotate_left.jpg"));
		rotateLeft_.setActionCommand(Notification.TILE_ROTATE_LEFT);
		rotateLeft_.addActionListener(this);
		rotateLeft_.setLocation(12, 133);
		rotateLeft_.setSize(58,38);
		add(rotateLeft_);
		
		// Subscribe for notifications from the controller so we know when to
		// update ourselves!
		NotificationManager.getInstance().addObserver(
				Notification.TILE_IN_HAND_CHANGED, this, "updateTileInHand");
		NotificationManager.getInstance().addObserver(
				Notification.END_GAME, this, "endGame");
	}

	public void endGame(Notification n) {
		// remove our tilePanel from the displayHelper
		DisplayHelper.getInstance().remove(tilePanel_);
		
		// close ourselves
		close();
	}
	
	/*
	 * Whenever a button is pressed in our panel, we want to send a notification
	 * so the button press can be handled by the HUDController (and possibly
	 * elsewhere - the BoardController handles zoom in and zoom out). We've
	 * wired things up so that each button sends a notification with the same
	 * name as it's actionCommand.
	 */
	public void actionPerformed(ActionEvent e) {
			NotificationManager.getInstance()
					.sendNotification(e.getActionCommand());
	}

	// Notification Handlers

	public void updateTileInHand(Notification n) {
		Tile t = (Tile) n.argument();
		
		if (tilePanel_ == null){
			tilePanel_ = new DragTilePanel(t);
			DisplayHelper.getInstance().add(tilePanel_, DisplayHelper.Layer.DRAG, new Point(this.getX()+12, this.getY()+9));	
		}
		tilePanel_.setTile(t);
		
		this.invalidate();
	}

	// Overloaded the add() method to bind a key listener to any elements placed
	// within the JPanel
	public Component add(Component comp) {
		super.add(comp);
		comp.addKeyListener(JKeyListener.getInstance());
		return comp;
	}
}