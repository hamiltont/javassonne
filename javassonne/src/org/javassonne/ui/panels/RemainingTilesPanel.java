/**
 * Javassonne 
 *  http://code.google.com/p/javassonne/
 * 
 * @author [Add Name Here]
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

package org.javassonne.ui.panels;

import java.awt.Color;
import java.awt.Component;
import java.awt.Image;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import org.javassonne.messaging.Notification;
import org.javassonne.messaging.NotificationManager;
import org.javassonne.model.Tile;
import org.javassonne.model.TileDeck;
import org.javassonne.ui.DisplayHelper;
import org.javassonne.ui.GameState;
import org.javassonne.ui.JKeyListener;

public class RemainingTilesPanel extends AbstractHUDPanel{

	private ArrayList<JLabel> tileCountLabels_;
	private ArrayList<JLabel> tileImageLabels_;

	private static final int T_SIZE = 40;
	private static final int T_PADDING = 5;
	private static final int COLS = 3;

	private static final int UNIT_HEIGHT = (T_SIZE + T_PADDING * 3 + 12);
	private static final int UNIT_WIDTH = (T_SIZE + T_PADDING * 2);

	public RemainingTilesPanel() {
		super();
		
		setVisible(true);
		setBackgroundImagePath("images/hud_tiles_remaining_background.jpg");
		setSize(UNIT_WIDTH * COLS, 5);
		setLayout(null);

		tileCountLabels_ = new ArrayList<JLabel>();
		tileImageLabels_ = new ArrayList<JLabel>();

		NotificationManager.getInstance().addObserver(
				Notification.UPDATED_DECK, this, "updatedDeck");
		NotificationManager.getInstance().addObserver(
				Notification.END_GAME, this, "endGame");
		
		if (GameState.getInstance().getDeck() != null){
			updatedDeck(GameState.getInstance().getDeck());
		}
	}

	public void endGame(Notification n) {
		// close ourselves
		close();
	}
	
	public void updatedDeck(Notification n) {
		TileDeck deck = (TileDeck) n.argument();
		if (deck != null)
			updatedDeck(deck);
	}

	private void updatedDeck(TileDeck d) {
		HashMap<Tile, Integer> remaining = d.tilesRemainingByType();

		int ii = 0;

		for (Tile t : remaining.keySet()) {
			JLabel imageLabel;
			JLabel countLabel;

			if (tileCountLabels_.size() > ii) {
				imageLabel = tileImageLabels_.get(ii);
				countLabel = tileCountLabels_.get(ii);
			} else {
				imageLabel = new JLabel();
				countLabel = new JLabel();

				int x = ii % COLS * UNIT_WIDTH;
				int y = (int) Math.floor(ii / COLS) * UNIT_HEIGHT;

				imageLabel.setLocation(x + T_PADDING, y + T_PADDING);
				imageLabel.setSize(T_SIZE, T_SIZE);
				countLabel.setLocation(x + T_PADDING, y + T_SIZE + T_PADDING);
				countLabel.setSize(T_SIZE, 17);
				countLabel.setHorizontalAlignment(JLabel.CENTER);
				countLabel.setOpaque(true);
				countLabel.setBackground(new Color(149, 133, 107));

				add(imageLabel);
				add(countLabel);

				tileImageLabels_.add(imageLabel);
				tileCountLabels_.add(countLabel);
			}

			Image img = t.getImage().getScaledInstance(T_SIZE, T_SIZE, 0);
			imageLabel.setIcon(new ImageIcon(img));
			countLabel.setText(remaining.get(t).toString());

			ii++;
		}

		this.setSize(COLS * UNIT_WIDTH, (int) (Math.ceil((double) ii
				/ (double) COLS) * UNIT_HEIGHT));
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
