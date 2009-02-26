/**
 * Javassonne 
 *  http://code.google.com/p/javassonne/
 * 
 * @author David Leinweber
 * @date Feb 17, 2009
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
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.OverlayLayout;

import org.javassonne.messaging.Notification;
import org.javassonne.messaging.NotificationManager;
import org.javassonne.ui.control.JKeyListener;

public class InputPlayerDataPanel extends AbstractHUDPanel implements
		ActionListener {
	private JPanel generalButtons_;
	private JPanel textFields_;

	public InputPlayerDataPanel() {
		super();

		addKeyListener(JKeyListener.getInstance());
		setBackgroundImagePath("images/menu_background.jpg");
		setVisible(true);
		setSize(800, 600);
		setLayout(new OverlayLayout(this));
		setAlignmentY(CENTER_ALIGNMENT);

		generalButtons_ = new JPanel();
		generalButtons_.setOpaque(false);
		generalButtons_.setLayout(null);
		generalButtons_.setAlignmentY(CENTER_ALIGNMENT);

		textFields_ = new JPanel();
		textFields_.setOpaque(false);
		textFields_.setLayout(null);
		textFields_.setAlignmentY(CENTER_ALIGNMENT);

		add(textFields_);
		add(generalButtons_);

		// add the buttons to the generalButtons_ panel
		addButtonToPanel("Start Game", Notification.START_GAME, new Point(250,
				420), generalButtons_);
		addButtonToPanel("Reset Fields", Notification.PLAYER_DATA_RESET,
				new Point(250, 465), generalButtons_);
		addButtonToPanel("Quit and Exit", Notification.EXIT_GAME, new Point(
				250, 510), generalButtons_);

		// add the text boxes to the textFields_ panel
		addTextBoxToPanel(new Point(250, 150), textFields_);
		addTextBoxToPanel(new Point(250, 195), textFields_);
		addTextBoxToPanel(new Point(250, 240), textFields_);
		addTextBoxToPanel(new Point(250, 285), textFields_);
		addTextBoxToPanel(new Point(250, 330), textFields_);
		addTextBoxToPanel(new Point(250, 375), textFields_);
	}

	private void addButtonToPanel(String path, String notification,
			Point location, JPanel panel) {
		JButton b = new JButton(path);
		b.addActionListener(this);
		b.setActionCommand(notification);
		b.setLocation(location);
		b.setSize(279, 38);
		panel.add(b);
	}

	private void addTextBoxToPanel(Point location, JPanel panel) {
		JTextField text = new JTextField();
		// text.addActionListener(this);
		// text.setActionCommand(notification);
		text.setLocation(location);
		text.setSize(279, 38);
		panel.add(text);
	}
	
	private void addLabelToPanel(Point location, String text, JPanel panel)
	{
		JLabel label = new JLabel(text);
		label.setLocation(location);
		panel.add(label);
	}

	public List<String> getPlayerData() {
		List<String> list = new ArrayList<String>();

		// for each Component/JTextField in textFields_ add the text in the text
		// box to the list
		for (Component c : textFields_.getComponents()) {
			list.add(((JTextField) c).getText());
		}

		return list;
	}

	/*
	 * Whenever a button is pressed in our panel, we want to send a notification
	 * so the button press can be handled by the HUDController (and possibly
	 * elsewhere - the BoardController handles zoom in and zoom out). We've
	 * wired things up so that each button sends a notification with the same
	 * name as it's actionCommand.
	 */
	public void actionPerformed(ActionEvent e) {

		if (e.getActionCommand().length() > 0)
			NotificationManager.getInstance().sendNotification(
					e.getActionCommand(), this);
	}

}
