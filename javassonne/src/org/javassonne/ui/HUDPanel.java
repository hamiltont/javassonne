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

import java.awt.Component;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.javassonne.messaging.Notification;
import org.javassonne.messaging.NotificationManager;
import org.javassonne.model.Tile;
import org.javassonne.model.TileDeck;
import org.javassonne.ui.control.JKeyListener;

/**
 * The primary JPanel in the HUD
 * 
 * @author bengotow
 * 
 */
public class HUDPanel extends AbstractHUDPanel implements ActionListener {

	private static final long serialVersionUID = 1L;

	private static final String DRAW_NEXT_TILE = "Draw";
	private static final String ZOOM_OUT = "Zoom Out";
	private static final String ZOOM_IN = "Zoom In";
	private static final String TILES_LEFT = "Tiles Left";

	private JButton zoomInButton_;
	private JButton zoomOutButton_;
	private JButton drawTile_;
	private JButton rotateRight_;
	private JButton rotateLeft_;
	private JLabel tilesLeft_;
	private JLabel imageHolder_;
	private JPanel rotateButtonsPanel_;
	private TurnIndicator playerTurn_;

	public HUDPanel() {
		super();
		
		setBackgroundImagePath("images/bottom_bar_background.jpg");
		setVisible(true);
		setSize(130, 250);

		// Create all of the components that will be shown

		zoomInButton_ = new JButton(ZOOM_IN);
		zoomInButton_.setActionCommand(Notification.ZOOM_IN);
		zoomInButton_.addActionListener(this);

		zoomOutButton_ = new JButton(ZOOM_OUT);
		zoomOutButton_.setActionCommand(Notification.ZOOM_OUT);
		zoomOutButton_.addActionListener(this);

		drawTile_ = new JButton(DRAW_NEXT_TILE);
		drawTile_.setActionCommand(Notification.DRAW_TILE);
		drawTile_.addActionListener(this);

		tilesLeft_ = new JLabel("0 " + TILES_LEFT);

		rotateRight_ = new JButton("=>");
		rotateRight_.setActionCommand(Notification.TILE_ROTATE_RIGHT);
		rotateRight_.addActionListener(this);
		rotateRight_.addKeyListener(JKeyListener.getInstance());

		rotateLeft_ = new JButton("<=");
		rotateLeft_.setActionCommand(Notification.TILE_ROTATE_LEFT);
		rotateLeft_.addActionListener(this);
		rotateLeft_.addKeyListener(JKeyListener.getInstance());

		playerTurn_ = new TurnIndicator("Player " + 1 + "'s Turn");

		imageHolder_ = new JLabel(new ImageIcon());

		rotateButtonsPanel_ = new JPanel();
		rotateButtonsPanel_.setOpaque(false);
		rotateButtonsPanel_.add(rotateLeft_);
		rotateButtonsPanel_.add(rotateRight_);

		// attach all of the components to the JFrame

		add(playerTurn_);
		add(zoomInButton_);
		add(zoomOutButton_);
		add(imageHolder_);
		add(rotateButtonsPanel_);
		add(drawTile_);
		add(tilesLeft_);

		// Subscribe for notifications from the controller so we know when to
		// update ourselves!
		NotificationManager.getInstance().addObserver(
				Notification.TILE_IN_HAND_CHANGED, this, "updateTileInHand");
		NotificationManager.getInstance().addObserver(
				Notification.DECK_CHANGED, this, "updateDeck");
		NotificationManager.getInstance().addObserver(
				Notification.ZOOM_CHANGED, this, "updateZoomButtons");
	}

	/*
	 * Whenever a button is pressed in our panel, we want to send a notification
	 * so the button press can be handled by the HUDController (and possibly
	 * elsewhere - the BoardController handles zoom in and zoom out). We've
	 * wired things up so that each button sends a notification with the same
	 * name as it's actionCommand.
	 */
	public void actionPerformed(ActionEvent e) {
		NotificationManager.getInstance()
				.sendNotification(e.getActionCommand());
	}

	// Notification Handlers

	public void updateTileInHand(Notification n) {
		Tile t = (Tile) n.argument();
		if(t != null){
			Image temp = t.getImage().getScaledInstance(50, 50, 0);
			imageHolder_.setIcon(new ImageIcon(temp));
		} else {
			imageHolder_.setIcon(null);
		}
		this.invalidate();
		
	}

	public void updateDeck(Notification n) {
		TileDeck deck = (TileDeck) n.argument();
		tilesLeft_.setText(deck.tilesRemaining() + " " + TILES_LEFT);

		playerTurn_.nextPlayer();
		playerTurn_
				.setText("Player " + playerTurn_.getPlayerTurn() + "'s Turn");

		this.invalidate();
	}

	public void updateZoomButtons(Notification n) {
		MapLayer m = (MapLayer) n.argument();

		if (m.zoomedMax()) {
			zoomInButton_.setEnabled(false);
			zoomOutButton_.setEnabled(true);
		} else if (m.zoomedMin()) {
			zoomInButton_.setEnabled(true);
			zoomOutButton_.setEnabled(false);
		} else {
			zoomInButton_.setEnabled(true);
			zoomOutButton_.setEnabled(true);
		}
	}

	// Overloaded the add() method to bind a key listener to any elements placed
	// within the JPanel
	public Component add(Component comp) {
		super.add(comp);
		comp.addKeyListener(JKeyListener.getInstance());
		return comp;
	}
}