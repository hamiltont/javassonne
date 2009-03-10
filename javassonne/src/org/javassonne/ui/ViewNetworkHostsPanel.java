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

package org.javassonne.ui;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.OverlayLayout;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.javassonne.ui.control.JKeyListener;

public class ViewNetworkHostsPanel extends AbstractHUDPanel implements
		ActionListener, ListSelectionListener {
	private JPanel hostsList_;

	public ViewNetworkHostsPanel() {
		super();

		addKeyListener(JKeyListener.getInstance());
		setBackgroundImagePath("images/menu_background.jpg");
		setVisible(true);
		setSize(800, 600);
		setLayout(new OverlayLayout(this));
		setAlignmentY(CENTER_ALIGNMENT);

		hostsList_ = new JPanel();
		hostsList_.setOpaque(false);
		hostsList_.setLayout(null);
		hostsList_.setAlignmentX(CENTER_ALIGNMENT);

		add(hostsList_);

		// Add list items to the hostsList_ panel
		String[] listItems = { "one", "two", "three" };
		addListToPanel(new Point(100,180), listItems, hostsList_);
		
	}

	private void addListToPanel(Point location, String[] items, JPanel panel) {
		JList l = new JList();
		l.addListSelectionListener(this);
		l.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		l.setLocation(location);
		l.setListData(items);
		l.setSize(600, 350);
		panel.add(l);
	}

	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void valueChanged(ListSelectionEvent arg0) {
		// TODO Auto-generated method stub

	}

}
