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

package org.javassonne.ui.panels;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;

import org.javassonne.messaging.Notification;
import org.javassonne.messaging.NotificationManager;
import org.javassonne.model.Player;
import org.javassonne.ui.GameState;
import org.javassonne.ui.controls.JMeepleCount;

public class GameOverPanel extends AbstractHUDPanel implements ActionListener {
	
	public GameOverPanel() {
		super();
		
		ArrayList<Player> players = GameState.getInstance().getPlayers();
		
		setVisible(true);
		setFocusable(false);
		setSize(655, 512);
		setLayout(null);
		setBackgroundImagePath("images/game_over_background_flat.png");
		
		// find player(s) with highest score
		int highest = -1;
		ArrayList<Player> highestPlayers = new ArrayList<Player>();
		for (Player p :players){
			if (p.getScore() > highest){
				highestPlayers.clear();
				highestPlayers.add(p);
				highest = p.getScore();
			}
			if (p.getScore() == highest){
				highestPlayers.add(p);
			}	
		}
		
		// create the labels and stuff for each of the player's stats
		int y = 125+24;
		for (Player p : players){
			JLabel name = new JLabel(p.getName());
			name.setLocation(146+27, y);
			name.setSize(135, 22);
			add(name);

			JLabel score = new JLabel(String.valueOf(p.getScore()));
			score.setLocation(440, y);
			score.setSize(65, 22);
			add(score);

			JMeepleCount meeple = new JMeepleCount(p.getMeepleColor().value);
			meeple.setLocation(146, y);
			meeple.setSize(24, 24);
			add(meeple);
			meeple.setCount(1);
			
			if (highestPlayers.contains(p)){
				JLabel focus = new JLabel(new ImageIcon("images/hud_stats_focus_background.png"));
				focus.setSize(362, 26);
				focus.setLocation(145, y);
				focus.setOpaque(false);
				add(focus);
			}
			
			y += 28;
		}
		
		// add jpanel behind the player stats so we get a pretty background
		AbstractHUDPanel panel = new AbstractHUDPanel();
		panel.setLocation(145,123);
		panel.setSize(362, 25 + 28 * GameState.getInstance().getPlayers().size());
		panel.setLayout(null);
		panel.setFocusable(false);
		panel.setBackgroundImagePath("images/game_over_stats_background.jpg");
		panel.setBackgroundScaleToFit(false);
		add(panel);
		
		// add the buttons to the general buttons panel
		JButton b = new JButton(new ImageIcon("images/menu_exit.jpg"));
		b.setLocation(192, 434);
		b.addActionListener(this);
		b.setActionCommand("Exit");
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
		if (e.getActionCommand().equals("Exit"))
			close();
			GameState.getInstance().resetGameState();
			NotificationManager.getInstance().sendNotification(
				Notification.END_GAME);
	}
	
}
