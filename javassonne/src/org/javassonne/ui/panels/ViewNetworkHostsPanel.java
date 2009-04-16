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

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.OverlayLayout;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.javassonne.messaging.Notification;
import org.javassonne.messaging.NotificationManager;
import org.javassonne.model.AvailableNetworkHosts;
import org.javassonne.networking.HostMonitor;
import org.javassonne.networking.LocalHost;
import org.javassonne.networking.impl.ChatMessage;
import org.javassonne.ui.DisplayHelper;

// TODO - add a button that will allow you to join a game to the joinGamePanel
//			Do a check that that person has a status that will allow others to join
//			their game, and do not allow them to be selected if they do not have this 
//			status
// TODO - use the LocalHost.isLocalHostStarted() function to provide some visual feedback
//			to the user about whether or not the multiplayer lobby is set up
public class ViewNetworkHostsPanel extends AbstractHUDPanel implements
		ActionListener, KeyListener, ListSelectionListener, TableModelListener {

	private JPanel main_;

	/**
	 * Shown when a user is thinking of joining another hosts game
	 */
	private JPanel joinGamePanel_;

	/**
	 * Shown when the user is hosting their own game
	 */
	private JPanel hostGamePanel_;

	private JTable availHostsTable_;
	private JTable connectedHostsTable_;

	private JTextArea passwordText_;

	private JTextArea chatArea_;
	private JTextArea talkArea_;
	
	private JTextArea enterIP_;

	// Action commands for buttons
	private static String CANCEL = "Cancel";
	private static String SHOW_JOIN_PANEL = "Back";
	private static String SHOW_HOST_PANEL = "Host_Game";
	private static String JOIN_GAME = "Join_Game";
	private static String SET_PASS = "Set_Password";
	private static String ENTER_IP = "Enter_new_IP";

	private boolean gameHasPassword_ = false;

	public ViewNetworkHostsPanel() {
		super();

		setBackgroundImagePath("images/multiplayer_lobby_background.jpg");
		setVisible(true);
		setLayout(new OverlayLayout(this));
		setSize(800, 600);
		setAlignmentY(CENTER_ALIGNMENT);

		main_ = new JPanel();
		main_.setOpaque(false);
		main_.setLayout(null);
		main_.setAlignmentX(CENTER_ALIGNMENT);

		// Add components to main that should be present for both
		setupCommonComponents();

		NotificationManager.getInstance()
				.addObserver(Notification.RECV_GLOBAL_CHAT, this,
						"receiveGlobalChatMessage");

		setupJoinPanel();

		setupHostPanel();
		hostGamePanel_.setVisible(false);

		// ============================================
		// Setup Password Panel
		// ============================================

		main_.add(hostGamePanel_);
		main_.add(joinGamePanel_);

		add(main_);
	}

	private void setupCommonComponents() {
		// ============================================
		// Setup Main Panel
		// ============================================

		// Add Buttons that will be present for both
		// the join game panel, and the host game
		addButtonToPanel("images/host_cancel.png", CANCEL, new Point(55, 543),
				main_);

		// Setup the Global chat, which will be available for both host_game and
		// join_game, and therefore gets added to the main
		chatArea_ = new JTextArea();
		JScrollPane chatScroll_ = new JScrollPane(chatArea_);
		chatScroll_.setLocation(new Point(410, 160));
		chatScroll_.setSize(350, 300);
		chatArea_.setEditable(false);
		chatArea_.setWrapStyleWord(true);
		chatArea_.setLineWrap(true);
		chatArea_.setCaretPosition(chatArea_.getDocument().getLength());
		main_.add(chatScroll_);

		// Add a chat label
		JLabel clabel = new JLabel("Global Chat");
		clabel.setLocation(new Point(415, 130));
		clabel.setFont(new Font("Serif", Font.BOLD, 16));
		clabel.setSize(200, 20);
		main_.add(clabel);

		// Add a chat text input area
		talkArea_ = new JTextArea("<Type here to chat to other players>");
		JScrollPane talkScroll_ = new JScrollPane(talkArea_);
		talkScroll_.setLocation(new Point(410, 461));
		talkScroll_.setSize(349, 50);
		talkArea_.setWrapStyleWord(true);
		talkArea_.setLineWrap(true);
		talkArea_.addKeyListener(this);
		talkArea_.addMouseListener(this);
		talkArea_.setCaretPosition(talkArea_.getDocument().getLength());
		main_.add(talkScroll_);
	}

	/**
	 * Shown when a user is thinking of joining another hosts game
	 */
	private void setupJoinPanel() {

		joinGamePanel_ = new JPanel();
		joinGamePanel_.setOpaque(false);
		joinGamePanel_.setLayout(null);
		joinGamePanel_.setAlignmentX(CENTER_ALIGNMENT);
		joinGamePanel_.setSize(getWidth(), getHeight());

		// Create the hosts table
		AvailableNetworkHosts tableModel = new AvailableNetworkHosts();
		availHostsTable_ = new JTable();
		availHostsTable_.setModel(tableModel);
		availHostsTable_.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		availHostsTable_.repaint();
		tableModel.addTableModelListener(this);
		
		// Make it scroll able
		JScrollPane hostsScroll = new JScrollPane(availHostsTable_);
		hostsScroll.setSize(350, 320);
		hostsScroll.setLocation(new Point(40, 160));
		availHostsTable_.setSize(hostsScroll.getWidth(), hostsScroll
				.getHeight());
		joinGamePanel_.add(hostsScroll);

		// Add a text area for them to enter in an IP
		enterIP_ = new JTextArea();
		enterIP_.setSize(300,30);
		enterIP_.setLocation(new Point(40,480));
		enterIP_.setText("<enter an IP you would like to find>");
		joinGamePanel_.add(enterIP_);
	
		// Add a button for the IP entry
		JButton setPw_ = new JButton("GO");
		setPw_.setActionCommand(ENTER_IP);
		setPw_.addActionListener(this);
		setPw_.setLocation(new Point(340, 480));
		setPw_.setSize(75, 30);
		joinGamePanel_.add(setPw_);
		
		// Add a label
		JLabel label = new JLabel("Connected Hosts");
		label.setLocation(new Point(45, 130));
		label.setFont(new Font("Serif", Font.BOLD, 16));
		label.setSize(200, 20);
		joinGamePanel_.add(label);

		// Add the Join game button
		addButtonToPanel("images/join_game.png", JOIN_GAME,
				new Point(600, 543), joinGamePanel_);

		// Add the Host game button
		addButtonToPanel("images/host_game.png", SHOW_HOST_PANEL, new Point(
				455, 543), joinGamePanel_);

	}

	/**
	 * Shown when a user is hosting a game
	 */
	private void setupHostPanel() {

		hostGamePanel_ = new JPanel();
		hostGamePanel_.setOpaque(false);
		hostGamePanel_.setLayout(null);
		hostGamePanel_.setAlignmentX(CENTER_ALIGNMENT);
		hostGamePanel_.setSize(getWidth(), getHeight());

		// TODO - create a model for the HostGame
		// Create the hosts table
		AvailableNetworkHosts change_me = new AvailableNetworkHosts();
		connectedHostsTable_ = new JTable();
		connectedHostsTable_.setModel(change_me);
		connectedHostsTable_
				.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		connectedHostsTable_.repaint();
		change_me.addTableModelListener(this);

		// Make it scroll able
		JScrollPane connectedScroll = new JScrollPane(connectedHostsTable_);
		connectedScroll.setSize(350, 200);
		connectedScroll.setLocation(new Point(40, 160));
		connectedHostsTable_.setSize(connectedScroll.getWidth(),
				connectedScroll.getHeight());
		hostGamePanel_.add(connectedScroll);

		// Add a label
		JLabel label1 = new JLabel("Connected Clients");
		label1.setLocation(new Point(45, 130));
		label1.setFont(new Font("Serif", Font.BOLD, 16));
		label1.setSize(200, 20);
		hostGamePanel_.add(label1);

		JLabel enterPasswordLabel_ = new JLabel();
		enterPasswordLabel_.setText("Set a password (optional):");
		enterPasswordLabel_.setSize(266, 20);
		enterPasswordLabel_.setLocation(75, 370);
		hostGamePanel_.add(enterPasswordLabel_);

		JButton setPw_ = new JButton(new ImageIcon("images/set_pw.png"));
		setPw_.addActionListener(this);
		setPw_.setLocation(new Point(300, 395));
		setPw_.setSize(75, 30);
		hostGamePanel_.add(setPw_);

		passwordText_ = new JTextArea();
		passwordText_.setSize(220,20);
		passwordText_.setLocation(75, 400);
		hostGamePanel_.add(passwordText_);
		
		// Add some other options
		// TODO - put game password option here
		// TODO - put Boot Player button here
		// Add the back button
		addButtonToPanel("images/host_back.png", SHOW_JOIN_PANEL, new Point(
				455, 543), hostGamePanel_);

	}

	private void addButtonToPanel(String imgPath, String notification,
			Point location, JPanel panel) {
		JButton b = new JButton(new ImageIcon(imgPath));
		b.addActionListener(this);
		b.setActionCommand(notification);
		b.setLocation(location);
		b.setSize(128, 48);
		panel.add(b);
	}

	public void actionPerformed(ActionEvent e) {

		if (e.getActionCommand().equals(SHOW_HOST_PANEL)) {
			// Hide the join game options, and show host
			joinGamePanel_.setVisible(false);
			hostGamePanel_.setVisible(true);
		} else if (e.getActionCommand().equals(SHOW_JOIN_PANEL)) {
			// Hide the host options, and show the join
			hostGamePanel_.setVisible(false);
			joinGamePanel_.setVisible(true);
		} else if (e.getActionCommand().equals(CANCEL)) {
			DisplayHelper.getInstance().remove(this);
			NotificationManager.getInstance().removeObserver(this);
		} else if (e.getActionCommand().equals(JOIN_GAME)) {

			if (gameHasPassword_ == true) {
				joinGamePanel_.setVisible(false);
				hostGamePanel_.setVisible(false);
				// pwMain_.setVisible(true);
				// if
				// (pwBoxJoin_.getPassword().toString().equals(this.password_))
				// {
				// join game
				// }
			} else {
				// again, join game.
			}
		} else if (e.getActionCommand().equals(SET_PASS)) {
			// Set the password
		} else if (e.getActionCommand().equals(ENTER_IP)) {
			HostMonitor.resolveNewHost(enterIP_.getText());
			
		} else
			NotificationManager.getInstance().sendNotification(
					e.getActionCommand(), this);

	}

	public void valueChanged(ListSelectionEvent e) {
		// TODO Auto-generated method stub

	}

	public void tableChanged(TableModelEvent e) {
		// We need the checks for != null just in case we have not fully started yet
		if (joinGamePanel_.isVisible() == true)
			if (availHostsTable_ != null)
				availHostsTable_.repaint();
		else if (connectedHostsTable_ != null)
				connectedHostsTable_.repaint();
	}

	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {

			ChatMessage cm = new ChatMessage(talkArea_.getText(), LocalHost
					.getName());

			// Send the message for the benefit of everyone else
			NotificationManager.getInstance().sendNotification(
					Notification.SEND_GLOBAL_CHAT, cm);

			e.consume();
			talkArea_.selectAll();
			talkArea_.replaceSelection("");
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
		if (talkArea_.getText().equals("<Type here to chat to other players>")) {
			talkArea_.selectAll();
			talkArea_.replaceSelection("");
			talkArea_.removeMouseListener(this);
		}
	}
}
