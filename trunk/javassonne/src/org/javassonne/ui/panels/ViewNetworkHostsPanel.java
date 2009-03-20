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

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
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
	private JTable hosts_table_;

	public ViewNetworkHostsPanel() {
		super();

		//addKeyListener(JKeyListener.getInstance());
		setBackgroundImagePath("images/multiplayer_lobby_background.jpg");
		setVisible(true);
		setLayout(new OverlayLayout(this));
		setSize(800, 600);
		setAlignmentY(CENTER_ALIGNMENT);

		main_ = new JPanel();
		main_.setOpaque(false);
		main_.setLayout(null);
		main_.setAlignmentX(CENTER_ALIGNMENT);

		
		// add the buttons to the general buttons panel
		addButtonToPanel("images/new_game_cancel.jpg", "", 
				new Point(275, 543), main_);


		// Create the hosts table
		NetworkHosts tableModel = new NetworkHosts(); 
		tableModel.addTableModelListener(this);
		hosts_table_ = new JTable();
		hosts_table_.setModel(tableModel);
		hosts_table_.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		hosts_table_.setSize(600, 360);
		
		
		// Make it scroll able
		JScrollPane hostsScroll = new JScrollPane(hosts_table_);
		hostsScroll.setLocation(new Point(105,143));
		hostsScroll.setSize(600, 360);
		hosts_table_.setFillsViewportHeight(true);
		
		main_.add(hostsScroll);
		
		add(main_);
	}
	
	private void addButtonToPanel(String path, String notification,
			Point location, JPanel panel) {
		JButton b = new JButton("Close");
		b.addActionListener(this);
		b.setActionCommand(notification);
		b.setLocation(location);
		b.setSize(279, 38);
		panel.add(b);
	}

	public void actionPerformed(ActionEvent e) {

		if (e.getActionCommand().length() > 0)
			NotificationManager.getInstance().sendNotification(
					e.getActionCommand(), this);
		else{
			// user pressed cancel
			DisplayHelper.getInstance().remove(this);
			NotificationManager.getInstance().removeObserver(this);
		}
	}


	public void valueChanged(ListSelectionEvent arg0) {
		// TODO Auto-generated method stub

	}
	
	public void tableChanged(TableModelEvent arg0) {
		System.out.println("Table changed!");
		hosts_table_.repaint();
	}
}
