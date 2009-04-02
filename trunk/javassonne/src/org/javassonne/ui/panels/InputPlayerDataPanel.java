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

package org.javassonne.ui.panels;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.OverlayLayout;

import org.javassonne.messaging.Notification;
import org.javassonne.messaging.NotificationManager;
import org.javassonne.model.Player;
import org.javassonne.model.Player.MeepleColor;
import org.javassonne.ui.DisplayHelper;
import org.javassonne.ui.JKeyListener;
import org.javassonne.ui.controls.JPopUp;

public class InputPlayerDataPanel extends AbstractHUDPanel implements
		ActionListener {
	private static final String NEW_GAME_START_IMAGE = "images/new_game_start.jpg";
	private static final String NEW_GAME_CANCEL_IMAGE = "images/new_game_cancel.jpg";
	private static final String NEW_GAME_IMAGE = "images/new_game_background.jpg";

	private JPanel generalButtons_;
	private JPanel textFields_;
	private JPanel labels_;
	private JPanel comboBoxes_;

	public InputPlayerDataPanel() {
		super();

		addKeyListener(JKeyListener.getInstance());
		// TODO: Externalize string
		setBackgroundImagePath(NEW_GAME_IMAGE);
		setVisible(true);
		setSize(800, 600);
		setLayout(new OverlayLayout(this));
		setAlignmentY(CENTER_ALIGNMENT);

		// TODO: Use BoxLayout instead of null layout
		generalButtons_ = new JPanel();
		generalButtons_.setOpaque(false);
		generalButtons_.setLayout(null);
		generalButtons_.setAlignmentY(CENTER_ALIGNMENT);

		textFields_ = new JPanel();
		textFields_.setOpaque(false);
		textFields_.setLayout(null);
		textFields_.setAlignmentY(CENTER_ALIGNMENT);

		labels_ = new JPanel();
		labels_.setOpaque(false);
		labels_.setLayout(null);
		labels_.setAlignmentY(CENTER_ALIGNMENT);

		comboBoxes_ = new JPanel();
		comboBoxes_.setOpaque(false);
		comboBoxes_.setLayout(null);
		comboBoxes_.setAlignmentY(CENTER_ALIGNMENT);

		// TODO: Use Java default layout organizers to eliminate ugly integers
		// add the buttons to the generalButtons_ panel
		addButtonToPanel(NEW_GAME_CANCEL_IMAGE, "", new Point(25, 543),
				generalButtons_);
		addButtonToPanel(NEW_GAME_START_IMAGE, Notification.START_GAME,
				new Point(492, 543), generalButtons_);

		// add the labels to labels_ panel
		addLabelToPanel(new Point(200, 150), "Player 1", labels_);
		addLabelToPanel(new Point(200, 195), "Player 2", labels_);
		addLabelToPanel(new Point(200, 240), "Player 3", labels_);
		addLabelToPanel(new Point(200, 285), "Player 4", labels_);
		addLabelToPanel(new Point(200, 330), "Player 5", labels_);
		addLabelToPanel(new Point(200, 375), "Player 6", labels_);

		// add text boxes to the textFields_ panel
		addTextBoxToPanel(new Point(250, 150), textFields_);
		addTextBoxToPanel(new Point(250, 195), textFields_);
		addTextBoxToPanel(new Point(250, 240), textFields_);
		addTextBoxToPanel(new Point(250, 285), textFields_);
		addTextBoxToPanel(new Point(250, 330), textFields_);
		addTextBoxToPanel(new Point(250, 375), textFields_);

		// place MeepleColors into ImageIcon array
		ImageIcon[] colors = new ImageIcon[MeepleColor.values().length];
		int i = 0;
		for (MeepleColor color : MeepleColor.values()) {
			colors[i] = color.image;
			i++;
		}

		// add combo boxes to comboBoxes_ panel
		addComboBoxToPanel(new Point(550, 150), colors, 0, comboBoxes_);
		addComboBoxToPanel(new Point(550, 195), colors, 1, comboBoxes_);
		addComboBoxToPanel(new Point(550, 240), colors, 2, comboBoxes_);
		addComboBoxToPanel(new Point(550, 285), colors, 3, comboBoxes_);
		addComboBoxToPanel(new Point(550, 330), colors, 4, comboBoxes_);
		addComboBoxToPanel(new Point(550, 375), colors, 5, comboBoxes_);

		// add panels
		add(textFields_);
		add(generalButtons_);
		add(labels_);
		add(comboBoxes_);
	}

	private void addButtonToPanel(String imagePath, String notification,
			Point location, JPanel panel) {
		JButton b = new JButton(new ImageIcon(imagePath));
		b.addActionListener(this);
		b.setActionCommand(notification);
		b.setLocation(location);
		// TODO: explain this weird size?
		b.setSize(279, 38);
		panel.add(b);
	}

	private void addTextBoxToPanel(Point location, JPanel panel) {
		JTextField text = new JTextField();
		text.setLocation(location);
		text.setSize(279, 38);
		panel.add(text);
	}

	private void addLabelToPanel(Point location, String text, JPanel panel) {
		JLabel label = new JLabel(text);
		label.setLocation(location);
		label.setSize(50, 38);
		panel.add(label);
	}

	private void addComboBoxToPanel(Point location, ImageIcon[] colors,
			int selectedIndex, JPanel panel) {
		JComboBox comboBox = new JComboBox(colors);
		comboBox.setSelectedIndex(selectedIndex);
		comboBox.addActionListener(this);
		comboBox.setLocation(location);
		comboBox.setSize(50, 38);
		panel.add(comboBox);
	}

	public List<String> getPlayerNames() {
		List<String> list = new ArrayList<String>();

		// for each Component/JTextField in textFields_ add the text in the text
		// box to the list
		for (Component c : textFields_.getComponents()) {
			list.add(((JTextField) c).getText());
		}
		// TODO: Does this return empty text too?
		return list;
	}

	public List<MeepleColor> getPlayerColors() {
		List<MeepleColor> list = new ArrayList<MeepleColor>();

		// for each Component/JComboBox in comboBoxes add the color selected to
		// the list
		for (Component c : comboBoxes_.getComponents()) {
			list.add(MeepleColor.values()[((JComboBox) c).getSelectedIndex()]);
		}

		return list;
	}

	/*
	 * Whenever a button is pressed in our panel, we want to send a notification
	 * so the button press can be handled by the the controller (and possibly
	 * elsewhere - the BoardController handles zoom in and zoom out). We've
	 * wired things up so that each button sends a notification with the same
	 * name as it's actionCommand.
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().length() > 0) {
			// see if the input in the panel is valid:
			if (!validatePlayerNames()) {
				JPopUp warning = new JPopUp(
						"At least two player names must be entered");
				warning.showMsg();
			} else if (!validateColors()) {
				JPopUp warning = new JPopUp(
						"Each player must have a unique color");
				warning.showMsg();
			} else {
				NotificationManager.getInstance().sendNotification(
						e.getActionCommand(), this);
			}
		} else {
			// user pressed cancel
			DisplayHelper.getInstance().remove(this);
			NotificationManager.getInstance().removeObserver(this);
		}
	}
	

	public boolean validatePlayerNames() {
		List<String> names = this.getPlayerNames();
		int playerCount = 0;

		for (String s : names) {
			if (s.length() > 0) {
				playerCount++;
			}
		}

		if (playerCount < 2) {
			return false;
		}

		return true;
	}

	public boolean validateColors() {
		int playerCount = 0;
		List<MeepleColor> colors = this.getPlayerColors();
		List<MeepleColor> temp = colors;

		for (String s : getPlayerNames()) {
			if (s.length() > 0 || playerCount < 2) {
				playerCount++;
			}
		}

		for (int i = 0; i < playerCount; i++) {
			int count = 0;
			for (int j = 0; j < playerCount; j++) {
				if (colors.get(i).value == temp.get(j).value)
					count++;
			}

			if (count > 1) {
				return false;
			}
		}

		return true;
	}

	// This function used to be in the GameController, but since the
	// InputPlayerDataPanel
	// has direct access to the information, it makes more sense for the logic
	// of
	// creating Player objects to happen here.
	public ArrayList<Player> getPlayers() {
		ArrayList<Player> players_ = new ArrayList<Player>();

		int playerCount = 0;
		for (String s : this.getPlayerNames()) {
			if (s.length() > 0) {
				Player player = new Player(s);
				player.setMeepleColor(getPlayerColors().get(playerCount));
				players_.add(player);
				playerCount++;
			} else if (playerCount < 2) {
				Player player = new Player();
				player.setMeepleColor(getPlayerColors().get(playerCount));
				players_.add(player);
				playerCount++;
			}
		}
		return players_;
	}

}
