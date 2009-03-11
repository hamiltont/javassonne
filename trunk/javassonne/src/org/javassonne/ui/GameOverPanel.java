/**
 * Javassonne 
 *  http://code.google.com/p/javassonne/
 * 
 * @author [Add Name Here]
 * @date Mar 9, 2009
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
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.javassonne.messaging.Notification;
import org.javassonne.messaging.NotificationManager;
import org.javassonne.model.Player;
import org.javassonne.ui.control.JKeyListener;

public class GameOverPanel extends AbstractHUDPanel implements ActionListener {
	
	public GameOverPanel(List<Player> players) {
		super();
		
		removeKeyListener(getKeyListeners()[0]);
		
		setVisible(true);
		setSize(380, 300 + 28 * players.size());
		setLayout(null);
		setBackgroundImagePath("images/menu_background.jpg");
		setBackgroundScaleToFit(false);
		JLabel go = new JLabel("Game Over!");
		go.setLocation(100,80);
		go.setSize(100, 20);
		add(go);
		
		// create the labels and stuff for each of the player's stats
		int y = 100;
		for (Player p : players){
			JLabel name = new JLabel(p.getName());
			name.setLocation(6, y);
			name.setSize(135, 22);
			add(name);
			
			JLabel score = new JLabel(String.valueOf(p.getScore()));
			score.setLocation(144, y);
			score.setSize(65, 22);
			add(score);
			
			y+= 28;
		}
		
		// add the buttons to the general buttons panel
			JButton b = new JButton(new ImageIcon("images/menu_new_game.jpg"));
			b.setLocation(new Point(50, y + 10));
			b.addActionListener(this);
			b.setActionCommand(Notification.END_GAME);
			b.setSize(279, 38);
			add(b);
	}

	/*
	 * Whenever a button is pressed in our panel, we want to send a notification
	 * so the button press can be handled by the HUDController (and possibly
	 * elsewhere - the BoardController handles zoom in and zoom out). We've
	 * wired things up so that each button sends a notification with the same
	 * name as it's actionCommand.
	 */
	public void actionPerformed(ActionEvent e) {
		this.setVisible(false);
		if (e.getActionCommand().length() > 0)
			NotificationManager.getInstance()
				.sendNotification(e.getActionCommand());
	}
	
}
