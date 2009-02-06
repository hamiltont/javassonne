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

package org.javassonne.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.javassonne.messaging.Notification;
import org.javassonne.messaging.NotificationManager;
import org.javassonne.model.Tile;
import org.javassonne.model.TileDeck;

public class RemainingTilesPanel extends JPanel implements MouseListener {

	private ArrayList<JLabel> tileCountLabels_;
	private ArrayList<JLabel> tileImageLabels_;

	private BufferedImage background_;

	private static final int T_SIZE = 40;
	private static final int T_PADDING = 5;
	private static final int ROWS = 3;

	private static final int UNIT_HEIGHT = (T_SIZE + T_PADDING * 3 + 12);
	private static final int UNIT_WIDTH = (T_SIZE + T_PADDING * 2);

	public RemainingTilesPanel() {
		setVisible(true);
		setSize(UNIT_WIDTH * ROWS, 5);
		setLayout(null);
		addMouseListener(this);

		tileCountLabels_ = new ArrayList<JLabel>();
		tileImageLabels_ = new ArrayList<JLabel>();

		NotificationManager.getInstance().addObserver(
				Notification.DECK_CHANGED, this, "updateDeck");
	}

	public void updateDeck(Notification n) {
		TileDeck deck = (TileDeck) n.argument();
		HashMap<Tile, Integer> remaining = deck.tilesRemainingByType();

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

				int x = ii % ROWS * UNIT_WIDTH;
				int y = (int) Math.floor(ii / ROWS) * UNIT_HEIGHT;

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

		this.setSize(ROWS * UNIT_WIDTH, (int) (Math.ceil((double) ii
				/ (double) ROWS) * UNIT_HEIGHT));
		this.invalidate();
	}

	/*
	 * This function is responsible for painting the background image we have.
	 */
	public void paintComponent(Graphics g) {
		if (background_ == null) {
			try {
				background_ = ImageIO.read(new File(
						"images/hud_tiles_remaining_background.jpg"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		Graphics2D g2 = (Graphics2D) g;
		g2.drawImage(background_, 0, 0, this.getWidth(), this.getHeight(), 0,
				0, background_.getWidth(), background_.getHeight(), null);
	}

	// We just want to implement these so the events don't pass through the
	// panel.
	public void mouseClicked(MouseEvent e) {
		return;

	}

	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}
}
