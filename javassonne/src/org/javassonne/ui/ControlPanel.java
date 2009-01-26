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

import org.javassonne.ui.control.JImagePanel;

public class ControlPanel extends JPanel {

	private static final String DRAW_NEXT_TILE = "Draw";
	private static final String ZOOM_OUT = "Zoom Out";
	private static final String ZOOM_IN = "Zoom In";
	private static final String EXIT_GAME = "Exit Game";
	private static final String LOAD_GAME = "Load Game";
	private static final String NEW_GAME = "New Game";

	private JButton newGameButton_;
	private JButton loadGameButton_;
	private JButton exitGameButton_;
	private JButton zoomInButton_;
	private JButton zoomOutButton_;
	private JButton drawTile_;
	private JButton rotateRight_;
	private JButton rotateLeft_;

	private JImagePanel curTileImage_;

	private BufferedImage background_;

	public ControlPanel() {
		setVisible(true);

		add(new JLabel("Player 1's Turn "));

		newGameButton_ = new JButton(NEW_GAME);
		loadGameButton_ = new JButton(LOAD_GAME);
		exitGameButton_ = new JButton(EXIT_GAME);

		zoomInButton_ = new JButton(ZOOM_IN);
		zoomOutButton_ = new JButton(ZOOM_OUT);

		drawTile_ = new JButton(DRAW_NEXT_TILE);

		rotateRight_ = new JButton("=>");
		rotateLeft_ = new JButton("<=");

		newGameButton_.setActionCommand("new_game");
		loadGameButton_.setActionCommand("load_game");
		exitGameButton_.setActionCommand("exit_game");
		drawTile_.setActionCommand("draw_tile");
		rotateRight_.setActionCommand("rotate_right");
		rotateLeft_.setActionCommand("rotate_left");

		add(newGameButton_);
		add(loadGameButton_);
		add(exitGameButton_);

		add(zoomInButton_);
		add(zoomOutButton_);

		// getting image for current tile (temporarily reading directly from
		// file)
		BufferedImage image = null;

		try {
			image = ImageIO.read(new File(
					"tilesets/standard/tile_standard_1.jpg"));
		} catch (IOException e) {
		}

		curTileImage_ = new JImagePanel(image, 50, 50);

		add(rotateLeft_);
		add(curTileImage_);
		add(new JLabel("             "));
		add(rotateRight_);

		add(new JButton("Pan Up"));
		add(new JButton("Pan Down"));
		add(new JButton("Pan Left"));
		add(new JButton("Pan Right"));

		add(drawTile_);

	}

	public void setActionListener(ActionListener listener) {
		newGameButton_.addActionListener(listener);
		loadGameButton_.addActionListener(listener);
		exitGameButton_.addActionListener(listener);
		drawTile_.addActionListener(listener);
		rotateLeft_.addActionListener(listener);
		rotateRight_.addActionListener(listener);
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