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

import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

public class ControlPanel extends JPanel {
	
	private JButton newGameButton_;
	private JButton loadGameButton_;
	private JButton exitGameButton_;
	
	public ControlPanel() {
		setSize(300, 150);
		setVisible(true);

		newGameButton_ = new JButton("New Game");
		loadGameButton_ = new JButton("Load Game");
		exitGameButton_ = new JButton("Exit Game");
	}

	public void setActionListener(ActionListener listener) {
		newGameButton_.addActionListener(listener);
		loadGameButton_.addActionListener(listener);
	}
	
	public void redraw() {
		// re-read the model
	}
}