/**
 * Javassonne 
 *  http://code.google.com/p/javassonne/
 * 
 * @author [Add Name Here]
 * @date Mar 7, 2009
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

import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.OverlayLayout;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.javassonne.messaging.NotificationManager;
import org.javassonne.model.NetworkHosts;
import org.javassonne.ui.DisplayHelper;

public class ViewNetworkHostsPanel extends AbstractHUDPanel implements
		ActionListener, ListSelectionListener, TableModelListener {
	private JPanel main_;
	private JPanel join_game_;
	private JTable hosts_table_;
	private JPanel host_game_;

	// Action commands for buttons
	private static String CANCEL = "Cancel";
	private static String HIDE_HOST_PANEL = "Back";
	private static String SHOW_HOST_PANEL = "Host_Game";

	public ViewNetworkHostsPanel() {
		super();

		// addKeyListener(JKeyListener.getInstance());
		setBackgroundImagePath("images/multiplayer_lobby_background.jpg");
		setVisible(true);
		setLayout(new OverlayLayout(this));
		setSize(800, 600);
		setAlignmentY(CENTER_ALIGNMENT);

		main_ = new JPanel();
		main_.setOpaque(false);
		main_.setLayout(null);
		main_.setAlignmentX(CENTER_ALIGNMENT);

		join_game_ = new JPanel();
		join_game_.setOpaque(false);
		join_game_.setLayout(null);
		join_game_.setAlignmentX(CENTER_ALIGNMENT);

		// Add Buttons that will be present for both
		// the join game panel, and the host game
		addButtonToPanel("images/new_game_cancel.jpg", CANCEL, new Point(55,
				543), main_);

		// Setup the Global chat, which will be available for both host_game and
		// join_game, and therefore gets added to the main
		JTextField chatArea = new JTextField();
		chatArea.setEditable(false);
		chatArea.setLocation(new Point(410,160));
		chatArea.setSize(350, 300);
		main_.add(chatArea);

		// Add a chat label
		JLabel clabel = new JLabel("Global Chat");
		clabel.setLocation(new Point(415, 130));
		clabel.setFont(new Font("Serif", Font.BOLD, 16));
		clabel.setSize(200, 20);
		join_game_.add(clabel);
		
		// Add a chat text input area
		JTextField talkArea = new JTextField("<Type here to chat to other players>");
		talkArea.setLocation(new Point(410,460));
		talkArea.setSize(350, 50);
		main_.add(talkArea);



		// Create the hosts table
		NetworkHosts tableModel = new NetworkHosts();
		tableModel.addTableModelListener(this);
		hosts_table_ = new JTable();
		hosts_table_.setModel(tableModel);
		hosts_table_.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		//hosts_table_.setSize(600, 360);

		// Make it scroll able
		JScrollPane hostsScroll = new JScrollPane(hosts_table_);
		hostsScroll.setSize(350, 350);
		hostsScroll.setLocation(new Point(40, 160));
		hosts_table_.setFillsViewportHeight(true);

		// Add a label
		JLabel label = new JLabel("Hosts Table");
		label.setLocation(new Point(45, 130));
		label.setFont(new Font("Serif", Font.BOLD, 16));
		label.setSize(200, 20);
		join_game_.add(label);

		// Add stuff to the join game
		join_game_.add(hostsScroll);
		join_game_.setSize(getWidth(), getHeight());

		main_.add(join_game_);

		add(main_);
	}

	private void addButtonToPanel(String imgPath, String notification,
			Point location, JPanel panel) {
		JButton b = new JButton(new ImageIcon(imgPath));
		b.addActionListener(this);
		b.setActionCommand(notification);
		b.setLocation(location);
		b.setSize(279, 38);
		panel.add(b);
	}

	private void addTextBoxToPanel(Point location, JPanel panel) {
		JTextField text = new JTextField();
		// text.addActionListener(this);
		// text.setActionCommand(notification);
		text.setLocation(location);
		text.setSize(279, 38);
		panel.add(text);
	}

	public void actionPerformed(ActionEvent e) {

		if (e.getActionCommand().equals(SHOW_HOST_PANEL)) {
			// Hide the join game options, and show host

		} else if (e.getActionCommand().equals(HIDE_HOST_PANEL)) {
			// Hide the host options, and show the join

		} else if (e.getActionCommand().equals(CANCEL)) {
			// user pressed cancel
			DisplayHelper.getInstance().remove(this);
			NotificationManager.getInstance().removeObserver(this);
		} else
			NotificationManager.getInstance().sendNotification(
					e.getActionCommand(), this);

	}

	public void valueChanged(ListSelectionEvent e) {
		// TODO Auto-generated method stub

	}

	public void tableChanged(TableModelEvent arg0) {
		hosts_table_.repaint();
	}
}
