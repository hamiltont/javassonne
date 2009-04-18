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
import org.javassonne.ui.JSoundManager;
import org.javassonne.ui.controls.JPopUp;

public class InputPlayerDataPanel extends AbstractHUDPanel implements
		ActionListener {
	private static final String NEW_GAME_START_IMAGE = "images/new_game_start.jpg";
	private static final String NEW_GAME_CANCEL_IMAGE = "images/new_game_cancel.jpg";
	private static final String NEW_GAME_IMAGE = "images/new_game_background.jpg";

	private ArrayList<JTextField> textFields_ = new ArrayList<JTextField>();
	private ArrayList<JComboBox> comboBoxes_= new ArrayList<JComboBox>();

	public InputPlayerDataPanel() {
		super();

		addKeyListener(JKeyListener.getInstance());
		// TODO: Externalize string
		setBackgroundImagePath(NEW_GAME_IMAGE);
		setVisible(true);
		setSize(800, 600);
		setLayout(null);
		setAlignmentY(CENTER_ALIGNMENT);

		// TODO: Use Java default layout organizers to eliminate ugly integers
		// add the buttons to the generalButtons_ panel
		addButtonToPanel(NEW_GAME_CANCEL_IMAGE, "", new Point(25, 543));
		addButtonToPanel(NEW_GAME_START_IMAGE, Notification.START_GAME,
				new Point(492, 543));

		// add the labels to labels_ panel
		int offset = 165;
		addLabelToPanel(new Point(200, 0 + offset), "Player 1");
		addLabelToPanel(new Point(200, 45 + offset), "Player 2");
		addLabelToPanel(new Point(200, 90 + offset), "Player 3");
		addLabelToPanel(new Point(200, 135 + offset), "Player 4");
		addLabelToPanel(new Point(200, 180 + offset), "Player 5");
		addLabelToPanel(new Point(200, 225 + offset), "Player 6");

		// add text boxes to the textFields_ panel
		addTextBoxToPanel(new Point(255, 0 + offset));
		addTextBoxToPanel(new Point(255, 45 + offset));
		addTextBoxToPanel(new Point(255, 90 + offset));
		addTextBoxToPanel(new Point(255, 135 + offset));
		addTextBoxToPanel(new Point(255, 180 + offset));
		addTextBoxToPanel(new Point(255, 225 + offset));

		// place MeepleColors into ImageIcon array
		ImageIcon[] colors = new ImageIcon[MeepleColor.values().length];
		int i = 0;
		for (MeepleColor color : MeepleColor.values()) {
			colors[i] = color.image;
			i++;
		}

		// add combo boxes to comboBoxes_ panel
		addComboBoxToPanel(new Point(545, 0 + offset), colors, 0);
		addComboBoxToPanel(new Point(545, 45 + offset), colors, 1);
		addComboBoxToPanel(new Point(545, 90 + offset), colors, 2);
		addComboBoxToPanel(new Point(545, 135 + offset), colors, 3);
		addComboBoxToPanel(new Point(545, 180 + offset), colors, 4);
		addComboBoxToPanel(new Point(545, 225 + offset), colors, 5);
	}

	private void addButtonToPanel(String imagePath, String notification,
			Point location) {
		JButton b = new JButton(new ImageIcon(imagePath));
		b.addActionListener(this);
		b.setActionCommand(notification);
		b.setLocation(location);
		// TODO: explain this weird size?
		b.setSize(279, 38);
		add(b);
	}

	private void addTextBoxToPanel(Point location) {
		JTextField text = new JTextField();
		text.setLocation(location);
		text.setSize(279, 38);
		add(text);
		textFields_.add(text);
	}

	private void addLabelToPanel(Point location, String text) {
		JLabel label = new JLabel(text);
		label.setLocation(location);
		label.setSize(50, 38);
		add(label);
	}

	private void addComboBoxToPanel(Point location, ImageIcon[] colors,
			int selectedIndex) {
		JComboBox comboBox = new JComboBox(colors);
		comboBox.setSelectedIndex(selectedIndex);
		comboBox.setLocation(location);
		// determine whether we need to fake full screen.
		comboBox.setSize(50, 38);

		comboBoxes_.add(comboBox);
		
		try {
			String os = System.getProperty("os.name");
			if (os.equals("Mac OS X"))
				comboBox.setSize(65, 38);
		} catch (Exception e) {
			// who cares?
		}
		add(comboBox);
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
			ArrayList<Player> players = this.getPlayers();
			
			Boolean namesOK = true;
			Boolean colorsOK = true;
			
			for (Player p : players){
				for (Player q : players){
					if (p != q){
						if (p.getMeepleColor() == q.getMeepleColor())
							colorsOK = false;
						if (p.getName().equals(q.getName()))
							namesOK = false;
					}
				}
			}

			if (players.size() < 2){
				// Play error sound
				NotificationManager.getInstance().sendNotification(
						Notification.ERROR);			
				JPopUp warning = new JPopUp(
						"At least two player names must be entered");
				warning.showMsg();
				
			} else if (!colorsOK) {
				// Play error sound
				NotificationManager.getInstance().sendNotification(
						Notification.ERROR);
				JPopUp warning = new JPopUp(
						"Each player must have a unique color");
				warning.showMsg();

			} else if (!namesOK){
				// Play error sound
				NotificationManager.getInstance().sendNotification(
						Notification.ERROR);
				JPopUp warning = new JPopUp(
						"Each player must have a unique name");
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


	// This function used to be in the GameController, but since the
	// InputPlayerDataPanel has direct access to the information, it makes
	// more sense for the logic of creating Player objects to happen here.
	public ArrayList<Player> getPlayers() {
		ArrayList<Player> players_ = new ArrayList<Player>();
		
		for (int ii = 0; ii < 6; ii ++){
			if (textFields_.get(ii).getText().length() > 0) {
				Player player = new Player(textFields_.get(ii).getText());
				player.setMeepleColor(MeepleColor.values()[comboBoxes_.get(ii).getSelectedIndex()]);
				players_.add(player);
			}
		}
		return players_;
	}

}
