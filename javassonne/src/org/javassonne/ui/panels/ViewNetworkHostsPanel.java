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
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.javassonne.messaging.Notification;
import org.javassonne.messaging.NotificationManager;
import org.javassonne.model.ConnectedClients;
import org.javassonne.model.ConnectedHosts;
import org.javassonne.model.Player;
import org.javassonne.model.Player.MeepleColor;
import org.javassonne.networking.HostMonitor;
import org.javassonne.networking.LocalClient;
import org.javassonne.networking.LocalHost;
import org.javassonne.networking.impl.CachedClient;
import org.javassonne.networking.impl.ChatMessage;
import org.javassonne.networking.impl.HostImpl;
import org.javassonne.ui.DisplayHelper;
import org.javassonne.ui.GameState;
import org.javassonne.ui.GameState.Mode;

// TODO - add a button that will allow you to join a game to the joinGamePanel
//			Do a check that that person has a status that will allow others to join
//			their game, and do not allow them to be selected if they do not have this 
//			status
// TODO - use the LocalHost.isLocalHostStarted() function to provide some visual feedback
//			to the user about whether or not the multiplayer lobby is set up
public class ViewNetworkHostsPanel extends AbstractHUDPanel implements
		ActionListener, KeyListener, ListSelectionListener, TableModelListener {

	// Subpanels
	private JPanel joinGamePanel_;
	private JPanel hostGamePanel_;

	// Tables
	private JTable availHostsTable_;
	private JTable connectedClientsTable_;

	// Fields
	private JTextArea chatArea_;
	private JTextField chatMessageField_;
	private JTextField ipaddressField_;

	// Models
	final ConnectedHosts hostsModel = new ConnectedHosts();
	final ConnectedClients clientsModel = new ConnectedClients();

	// Action commands for buttons
	private static String CANCEL = "Cancel";
	private static String SHOW_JOIN_PANEL = "Cancel_Host_Game";
	private static String SHOW_HOST_PANEL = "Host_Game";
	private static String JOIN_GAME = "Join_Game";
	private static String START_GAME = "Start_Game";
	private static String ENTER_IP = "Enter_new_IP";

	public ViewNetworkHostsPanel() {
		super();

		setBackgroundImagePath("images/multiplayer_lobby_background.jpg");
		setVisible(true);
		setLayout(null);
		setSize(800, 600);

		// Add components to main that should be present for both
		setupCommonComponents();

		NotificationManager n = NotificationManager.getInstance();
		n.addObserver(Notification.RECV_GLOBAL_CHAT, this,
				"receiveGlobalChatMessage");

		setupJoinPanel();
		setupHostPanel();

		// Notify the AbstractHUD Panel that we do not need any of the timer
		// functionality for dragging
		setDraggable(false);

		hostGamePanel_.setVisible(false);
	}

	private void setupCommonComponents() {
		// Add Buttons that will be present for both
		// the join game panel, and the host game
		addButtonToPanel("images/host_cancel.png", CANCEL, new Point(40, 547),
				this);

		// Add your IP address
		JLabel ipAddy = new JLabel(LocalHost.getURI());
		ipAddy.setLocation(new Point(200, 557));
		ipAddy.setFont(new Font("Serif", Font.BOLD, 12));
		ipAddy.setSize(500, 25);
		add(ipAddy);

		// Setup the Global chat, which will be available for both host_game and
		// join_game, and therefore gets added to the main
		chatArea_ = new JTextArea();
		chatArea_.setEditable(false);
		chatArea_.setWrapStyleWord(true);
		chatArea_.setLineWrap(true);
		chatArea_.setCaretPosition(chatArea_.getDocument().getLength());

		JScrollPane chatScroll_ = new JScrollPane(chatArea_);
		chatScroll_.setLocation(new Point(40, 363));
		chatScroll_.setSize(720, 138);
		add(chatScroll_);

		// Add a chat label
		JLabel clabel = new JLabel("Global Chat");
		clabel.setLocation(new Point(44, 342));
		clabel.setFont(new Font("Serif", Font.BOLD, 20));
		clabel.setSize(200, 25);
		add(clabel);

		// Add a chat text input area
		chatMessageField_ = new JTextField(
				"<Type here to chat to other players>");
		chatMessageField_.addKeyListener(this);
		chatMessageField_.addMouseListener(this);
		chatMessageField_.setCaretPosition(chatMessageField_.getDocument()
				.getLength());
		chatMessageField_.setLocation(40, 506);
		chatMessageField_.setSize(720, 33);
		add(chatMessageField_);
	}

	/**
	 * Shown when a user is thinking of joining another hosts game
	 */
	private void setupJoinPanel() {

		joinGamePanel_ = new JPanel();
		joinGamePanel_.setOpaque(false);
		joinGamePanel_.setLayout(null);
		joinGamePanel_.setSize(720, 220);
		joinGamePanel_.setLocation(40, 122);
		add(joinGamePanel_);

		// Create the hosts table
		availHostsTable_ = new JTable();
		availHostsTable_.setModel(hostsModel);
		availHostsTable_.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		availHostsTable_.setColumnSelectionAllowed(false);
		availHostsTable_.setRowSelectionAllowed(true);
		hostsModel.addTableModelListener(this);

		// Make it scrollable
		JScrollPane availHostsTableContainer = new JScrollPane(availHostsTable_);
		availHostsTableContainer.setLocation(0, 20);
		availHostsTableContainer.setSize(720, 150);
		availHostsTable_.setSize(availHostsTableContainer.getWidth(),
				availHostsTableContainer.getHeight());
		joinGamePanel_.add(availHostsTableContainer);

		// Add a label
		JLabel ipLabel = new JLabel("Add by IP Address:");
		ipLabel.setLocation(new Point(4, 182));
		ipLabel.setFont(new Font("Serif", Font.BOLD, 14));
		ipLabel.setSize(200, 25);
		joinGamePanel_.add(ipLabel);

		// Add a text area for them to enter in an IP
		ipaddressField_ = new JTextField();
		ipaddressField_.setSize(202, 30);
		ipaddressField_.setLocation(126, 179);
		ipaddressField_.setText("<enter an IP you would like to find>");
		ipaddressField_.addMouseListener(this);
		joinGamePanel_.add(ipaddressField_);

		// Add a button for the IP entry
		JButton ipAddressGo = new JButton(new ImageIcon(
				"images/host_add_by_ip.jpg"));
		ipAddressGo.setActionCommand(ENTER_IP);
		ipAddressGo.addActionListener(this);
		ipAddressGo.setLocation(new Point(328, 182));
		ipAddressGo.setSize(39, 23);
		joinGamePanel_.add(ipAddressGo);

		// Add a label
		JLabel label = new JLabel("Available Players");
		label.setLocation(new Point(5, 0));
		label.setFont(new Font("Serif", Font.BOLD, 20));
		label.setSize(200, 25);
		joinGamePanel_.add(label);

		// Add the Join game button
		addButtonToPanel("images/join_game.jpg", JOIN_GAME,
				new Point(477, 175), joinGamePanel_);

		// Add the Host game button
		addButtonToPanel("images/host_game.png", SHOW_HOST_PANEL, new Point(
				602, 175), joinGamePanel_);
	}

	/**
	 * Shown when a user is hosting a game
	 */
	private void setupHostPanel() {

		hostGamePanel_ = new JPanel();
		hostGamePanel_.setOpaque(false);
		hostGamePanel_.setLayout(null);
		hostGamePanel_.setSize(720, 220);
		hostGamePanel_.setLocation(40, 122);
		add(hostGamePanel_);

		// Create the hosts table
		connectedClientsTable_ = new JTable();
		connectedClientsTable_.setModel(clientsModel);
		connectedClientsTable_
				.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		connectedClientsTable_.repaint();
		connectedClientsTable_.setColumnSelectionAllowed(false);
		connectedClientsTable_.setRowSelectionAllowed(true);
		clientsModel.addTableModelListener(this);

		// Make it scrollable
		JScrollPane container = new JScrollPane(connectedClientsTable_);
		container.setLocation(0, 20);
		container.setSize(720, 150);
		availHostsTable_.setSize(container.getWidth(), container.getHeight());
		hostGamePanel_.add(container);

		// Add a label
		JLabel label = new JLabel("Players in Game");
		label.setLocation(new Point(5, 0));
		label.setFont(new Font("Serif", Font.BOLD, 20));
		label.setSize(200, 25);
		hostGamePanel_.add(label);

		// Add the cancel game button
		addButtonToPanel("images/host_back.png", SHOW_JOIN_PANEL, new Point(0,
				175), hostGamePanel_);

		// Add the go button
		addButtonToPanel("images/host_start_game.jpg", START_GAME, new Point(
				602, 175), hostGamePanel_);
	}

	private void addButtonToPanel(String imgPath, String notification,
			Point location, JPanel panel) {
		JButton b = new JButton(new ImageIcon(imgPath));
		b.addActionListener(this);
		b.setActionCommand(notification);
		b.setLocation(location);
		b.setSize(118, 38);
		panel.add(b);
	}

	public void actionPerformed(ActionEvent e) {

		if (e.getActionCommand().equals(SHOW_HOST_PANEL)) {
			// Hide the join game options, and show host
			joinGamePanel_.setVisible(false);
			hostGamePanel_.setVisible(true);

			// We are now waiting for players
			GameState.getInstance().setMode(Mode.WAITING);
		} else if (e.getActionCommand().equals(SHOW_JOIN_PANEL)) {
			// Hide the host options, and show the join
			hostGamePanel_.setVisible(false);
			joinGamePanel_.setVisible(true);

			// Change our state
			GameState.getInstance().setMode(Mode.IN_LOBBY);
		} else if (e.getActionCommand().equals(CANCEL)) {
			DisplayHelper.getInstance().remove(this);
			hostsModel.removeTableModelListener(this);
			hostsModel.cancel();
			clientsModel.removeTableModelListener(this);
			clientsModel.cancel();
			NotificationManager.getInstance().removeObserver(this);
		} else if (e.getActionCommand().equals(JOIN_GAME)) {
			int selected = availHostsTable_.getSelectedRow();
			// TODO - make this dynamic! (once selected works)
			String hostURI = (String) availHostsTable_.getModel().getValueAt(0,
					1);
			LocalClient.connectToHost(hostURI);

		} else if (e.getActionCommand().equals(ENTER_IP)) {
			HostMonitor.resolveNewHost(ipaddressField_.getText());

		} else if (e.getActionCommand().equals(START_GAME)) {
			// Do everything to destroy this panel
			DisplayHelper.getInstance().remove(this);
			hostsModel.removeTableModelListener(this);
			hostsModel.cancel();
			clientsModel.removeTableModelListener(this);
			clientsModel.cancel();
			NotificationManager.getInstance().removeObserver(this);

			ArrayList<Player> players = new ArrayList<Player>();
			int color = 0;
			
			// add ourselves
			Player p = new Player(HostImpl.getInstance().getName());
			players.add(p);
			
			// add other players to the game
			for (CachedClient c : LocalHost.getConnectedClients()) {
				p = new Player(c.getName());
				p.setIsLocal(false);
				p.setMeepleColor(MeepleColor.values()[color]);
				players.add(p);
				
				color++;
			}

			// start the game locally
			NotificationManager.getInstance().sendNotification(
					Notification.START_GAME, players);
				// this calls setBoard, setDeck... notifications
			
			// enable notification sending
			
			// send notification START_NETWORK_GAME to clients with data
			// from our game? We don't want to run this ourselves.
			
		} else
			NotificationManager.getInstance().sendNotification(
					e.getActionCommand(), this);

	}

	public void valueChanged(ListSelectionEvent e) {
		// TODO Auto-generated method stub

	}

	public void tableChanged(TableModelEvent e) {
		// We need the checks for != null just in case we have not fully started
		// yet

		JTable current = null;
		if (joinGamePanel_.isVisible() == true)
			current = availHostsTable_;
		else
			current = connectedClientsTable_;

		int selection = current.getSelectedRow();
		if (selection != -1) {
			current.clearSelection();
			current.setRowSelectionInterval(selection, selection);
		}

	}

	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {

			ChatMessage cm = new ChatMessage(chatMessageField_.getText(),
					LocalHost.getName());

			// Send the message for the benefit of everyone else
			NotificationManager.getInstance().sendNotification(
					Notification.SEND_GLOBAL_CHAT, cm);

			e.consume();
			chatMessageField_.selectAll();
			chatMessageField_.replaceSelection("");
		}
	}

	public void keyReleased(KeyEvent e) {
	}

	public void keyTyped(KeyEvent e) {
	}

	public void receiveGlobalChatMessage(Notification recvGlobalChat) {
		ChatMessage cm = (ChatMessage) recvGlobalChat.argument();
		chatArea_.setText(chatArea_.getText() + "\n" + cm.getSenderName()
				+ ": " + cm.getMessage());
	}

	public void mouseClicked(MouseEvent m) {
		if (m.getSource().getClass() == JTextField.class) {
			JTextField field = (JTextField) m.getSource();
			if (field.getText().substring(0, 1).equals("<")) {
				field.selectAll();
				field.replaceSelection("");
				field.removeMouseListener(this);
			}
		}
	}
}
