/**
 * Javassonne 
 *  http://code.google.com/p/javassonne/
 * 
 * @author [Add Name Here]
 * @date Feb 16, 2009
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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import org.javassonne.messaging.Notification;
import org.javassonne.messaging.NotificationManager;
import org.javassonne.ui.control.JKeyListener;

public class HUDButtonsPanel extends AbstractHUDPanel implements ActionListener {

	private static final long serialVersionUID = 1L;

	private JButton menuButton_;
	private JButton zoomInButton_;
	private JButton zoomOutButton_;

	public HUDButtonsPanel() {
		super();

		setOpaque(false);
		setVisible(true);
		setSize(144, 25);
		setLayout(null);
		
		menuButton_ = new JButton(new ImageIcon("images/hud_main_menu.jpg"));
		menuButton_.setActionCommand(Notification.TOGGLE_MAIN_MENU);
		menuButton_.addActionListener(this);
		menuButton_.setSize(65, 25);
		menuButton_.setLocation(0, 0);
		add(menuButton_);

		zoomOutButton_ = new JButton(new ImageIcon("images/hud_zoom_out.jpg"));
		zoomOutButton_.setActionCommand(Notification.ZOOM_OUT);
		zoomOutButton_.addActionListener(this);
		zoomOutButton_.setSize(35, 25);
		zoomOutButton_.setLocation(72, 0);
		add(zoomOutButton_);

		zoomInButton_ = new JButton(new ImageIcon("images/hud_zoom_in.jpg"));
		zoomInButton_.setActionCommand(Notification.ZOOM_IN);
		zoomInButton_.addActionListener(this);
		zoomInButton_.setSize(35, 25);
		zoomInButton_.setLocation(109, 0);
		add(zoomInButton_);
		
		// Subscribe for notifications from the controller so we know when to
		// update ourselves!
		NotificationManager.getInstance().addObserver(
				Notification.ZOOM_CHANGED, this, "updateZoomButtons");
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

	public void updateZoomButtons(Notification n) {
		MapLayer m = (MapLayer) n.argument();

		if (m.zoomedMax()) {
			zoomInButton_.setEnabled(false);
			zoomOutButton_.setEnabled(true);
		} else if (m.zoomedMin()) {
			zoomInButton_.setEnabled(true);
			zoomOutButton_.setEnabled(false);
		} else {
			zoomInButton_.setEnabled(true);
			zoomOutButton_.setEnabled(true);
		}
	}

	// Overloaded the add() method to bind a key listener to any elements placed
	// within the JPanel
	public Component add(Component comp) {
		super.add(comp);
		comp.addKeyListener(JKeyListener.getInstance());
		return comp;
	}
}
