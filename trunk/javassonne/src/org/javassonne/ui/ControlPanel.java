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

package org.javassonne.ui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ControlPanel extends JPanel {

	private JButton newGameButton_;
	private JButton loadGameButton_;
	private JButton exitGameButton_;
	private JButton zoomInButton_;
	private JButton zoomOutButton_;
	private BufferedImage background_;

	public ControlPanel() {
		setVisible(true);

		newGameButton_ = new JButton("New Game");
		loadGameButton_ = new JButton("Load Game");
		exitGameButton_ = new JButton("Exit Game");

		zoomInButton_ = new JButton("Zoom In");
		zoomOutButton_ = new JButton("Zoom Out");

		newGameButton_.setActionCommand("new_game");
		loadGameButton_.setActionCommand("load_game");
		exitGameButton_.setActionCommand("exit_game");

		add(newGameButton_);
		add(loadGameButton_);
		add(exitGameButton_);

		add(new JLabel("                    "));
		add(new JLabel("                    "));
		add(zoomInButton_);
		add(zoomOutButton_);
		add(new JLabel("                    "));
		add(new JLabel("                    "));

		add(new JButton("Pan Up"));
		add(new JButton("Pan Down"));
		add(new JButton("Pan Left"));
		add(new JButton("Pan Right"));

	}

	public void setActionListener(ActionListener listener) {
		newGameButton_.addActionListener(listener);
		loadGameButton_.addActionListener(listener);
		exitGameButton_.addActionListener(listener);
	}

	public void paintComponent(Graphics g) {
		if (background_ == null) {
			try {
				background_ = ImageIO.read(new File(
						"images/bottom_bar_background.jpg"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		Graphics2D g2 = (Graphics2D) g;
		g2.drawImage(background_, 0, 0, this.getWidth(), this.getHeight(), 0,
				0, background_.getWidth(), background_.getHeight(), null);
	}

	public void redraw() {
		// re-read the model
	}
}