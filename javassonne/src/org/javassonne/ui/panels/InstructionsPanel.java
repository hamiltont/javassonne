/**
 * Javassonne 
 *  http://code.google.com/p/javassonne/
 * 
 * @author Adam Albright
 * @date Mar 22, 2009
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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.javassonne.messaging.Notification;
import org.javassonne.messaging.NotificationManager;
import org.javassonne.ui.DisplayHelper;
import org.javassonne.ui.controls.JScrollablePicture;

public class InstructionsPanel extends JPanel implements MouseListener {
	private static final String GAME_INSTRUCTIONS_IMAGE = "images/game_instructions.jpg";

	public InstructionsPanel() {
		setOpaque(false);
		setVisible(true);

		// Get the image to use.
		ImageIcon image = new ImageIcon(GAME_INSTRUCTIONS_IMAGE);

		// Set up the scroll pane.
		JScrollablePicture picture = new JScrollablePicture(image, 15);

		JScrollPane pictureScrollPane = new JScrollPane(picture);
		pictureScrollPane.setPreferredSize(new Dimension(500, 500));
		pictureScrollPane.setLocation(new Point(20, 20));

		setLayout(new BorderLayout());
		setOpaque(true);
		setSize(image.getIconWidth() + 18, GraphicsEnvironment
				.getLocalGraphicsEnvironment().getDefaultScreenDevice()
				.getDisplayMode().getHeight() - 100);

		// Create an exit button
		JButton b = new JButton("CLOSE");
		b.addMouseListener(this);
		b.setLocation(new Point(0, 0));

		// Add the close button and instructions image
		add(b, BorderLayout.NORTH);
		add(pictureScrollPane);

		NotificationManager.getInstance().addObserver(Notification.END_GAME,
				this, "endGame");

	}

	public void endGame(Notification n) {
		// close ourselves
		DisplayHelper.getInstance().remove(this);
	}

	/*
	 * Mouse Listeners to capture the exit button click
	 */
	public void mouseClicked(MouseEvent arg0) {
		NotificationManager.getInstance().sendNotification(
				Notification.TOGGLE_INSTRUCTIONS);
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}
}
