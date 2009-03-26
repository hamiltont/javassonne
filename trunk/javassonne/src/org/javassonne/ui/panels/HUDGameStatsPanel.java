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

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import org.javassonne.messaging.Notification;
import org.javassonne.messaging.NotificationManager;
import org.javassonne.model.Player;
import org.javassonne.ui.JKeyListener;
import org.javassonne.ui.controls.JMeepleCount;

public class HUDGameStatsPanel extends AbstractHUDPanel {

	List<Player> players_;
	int currentPlayer_;

	ArrayList<JLabel> names_;
	ArrayList<JLabel> scores_;
	ArrayList<JMeepleCount> meeple_;

	JLabel focus_;
	
	public HUDGameStatsPanel(List<Player> players) {
		super();

		setVisible(true);
		setSize(380, 27 + 28 * players.size());
		setLayout(null);
		setFocusable(false);
		setBackgroundImagePath("images/hud_stats_background.jpg");
		setBackgroundScaleToFit(false);

		// store the array of players locally
		players_ = players;
		currentPlayer_ = 0;
		names_ = new ArrayList<JLabel>();
		scores_ = new ArrayList<JLabel>();
		meeple_ = new ArrayList<JMeepleCount>();
		
		// create the labels and stuff for each of the player's stats
		int y = 28;
		for (Player p : players_) {
			JLabel name = new JLabel(p.getName());
			name.setLocation(6, y);
			name.setSize(135, 22);
			add(name);
			names_.add(name);

			JLabel score = new JLabel(String.valueOf(p.getScore()));
			score.setLocation(144, y);
			score.setSize(65, 22);
			add(score);
			scores_.add(score);

			JMeepleCount meeple = new JMeepleCount(p.getMeepleColor().value);
			meeple.setLocation(214, y);
			meeple.setSize(170, 24);
			add(meeple);
			meeple.setCount(7);
			y += 28;
			meeple_.add(meeple);
		}
		// create the focus thing that makes the selected player blue
		focus_ = new JLabel(new ImageIcon("images/hud_stats_focus_background.png"));
		focus_.setSize(380, 26);
		focus_.setLocation(0, 26);
		focus_.setOpaque(false);
		add(focus_);
		
		// Subscribe for notifications from the controller so we know when to
		// update ourselves!
		NotificationManager.getInstance().addObserver(Notification.END_GAME,
				this, "endGame");
		NotificationManager.getInstance().addObserver(Notification.END_TURN,
				this, "endTurn");
		NotificationManager.getInstance().addObserver(Notification.BEGIN_TURN,
				this, "beginTurn");
	}

	public void endGame(Notification n) {
		// reset the players array to make sure it is not references
		players_ = null;
		close();
	}

	public void endTurn(Notification n) {
		// sent from the confirmPlacement panel when the user presses end turn.
		// We want to advance the turn and change current player.
		currentPlayer_ = (currentPlayer_ + 1) % players_.size();
		
		// move the selected player focus background
		focus_.setLocation(0, 27 + currentPlayer_ * 28);	
	}
	
	public void beginTurn(Notification n) {
		// iterate through all the players and make sure their scores are valid
		if (scores_.size() == players_.size())
			for (int ii = 0; ii < players_.size(); ii++)
				scores_.get(ii).setText(String.valueOf(players_.get(ii).getScore()));
	}

	// Overloaded the add() method to bind a key listener to any elements placed
	// within the JPanel
	public Component add(Component comp) {
		super.add(comp);
		comp.addKeyListener(JKeyListener.getInstance());
		return comp;
	}
}
