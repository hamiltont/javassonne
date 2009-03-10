/**
 * Javassonne 
 *  http://code.google.com/p/javassonne/
 * 
 * @author Ben Gotow
 * @date Feb 5, 2009
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.OverlayLayout;

import org.javassonne.messaging.Notification;
import org.javassonne.messaging.NotificationManager;
import org.javassonne.ui.control.JKeyListener;

/**
 * This panel shows the Javassonne Logo and the buttons to start a new game,
 * save a game, etc... Once an option is selected, this window closes.
 * 
 * @author bengotow
 * 
 */
public class MenuPanel extends AbstractHUDPanel implements ActionListener {

	private JPanel generalButtons_;
	private JPanel inGameButtons_;

	public MenuPanel() {
		super();

		addKeyListener(JKeyListener.getInstance());
		setBackgroundImagePath("images/menu_background.jpg");
		setVisible(true);
		setSize(500, 300);
		setLayout(new OverlayLayout(this));

		generalButtons_ = new JPanel();
		generalButtons_.setOpaque(false);
		generalButtons_.setLayout(null);
		inGameButtons_ = new JPanel();
		inGameButtons_.setOpaque(false);
		inGameButtons_.setLayout(null);

		add(generalButtons_);
		add(inGameButtons_);

		// add the buttons to the general buttons panel
		addButtonToPanel("images/menu_new_game.jpg", Notification.NEW_GAME,
				new Point(100, 95), generalButtons_);
		addButtonToPanel("images/menu_new_multiplayer_game.jpg", Notification.NEW_NW_GAME,
				new Point(100, 140), generalButtons_);
		addButtonToPanel("images/menu_load_game.jpg", Notification.LOAD_GAME,
				new Point(100, 185), generalButtons_);
		addButtonToPanel("images/menu_quit.jpg", Notification.QUIT,
				new Point(100, 245), generalButtons_);

		// add the buttons to the inGame buttons panel
		addButtonToPanel("images/menu_save_game.jpg", Notification.SAVE_GAME,
				new Point(100, 105), inGameButtons_);
		addButtonToPanel("images/menu_end_game.jpg", Notification.ATTEMPT_END_GAME,
				new Point(100, 150), inGameButtons_);
		addButtonToPanel("images/menu_resume_game.jpg", null, new Point(100,
				240), inGameButtons_);

		setGameInProgress(false);
	}

	private void addButtonToPanel(String path, String notification,
			Point location, JPanel panel) {
		JButton b = new JButton(new ImageIcon(path));
		b.addActionListener(this);
		b.setActionCommand(notification);
		b.setLocation(location);
		b.setSize(279, 38);
		panel.add(b);
	}

	/*
	 * Whenever a button is pressed in our panel, we want to send a notification
	 * so the button press can be handled by the HUDController (and possibly
	 * elsewhere - the BoardController handles zoom in and zoom out). We've
	 * wired things up so that each button sends a notification with the same
	 * name as it's actionCommand.
	 */
	public void actionPerformed(ActionEvent e) {

		NotificationManager.getInstance().sendNotification(
				Notification.TOGGLE_MAIN_MENU);
		if (e.getActionCommand().length() > 0)
			NotificationManager.getInstance()
				.sendNotification(e.getActionCommand());
	}

	public void setGameInProgress(Boolean inProgress) {
		inGameButtons_.setVisible(inProgress);
		generalButtons_.setVisible(!inProgress);
	}
}
