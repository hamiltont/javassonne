/**
 * Javassonne 
 *  http://code.google.com/p/javassonne/
 * 
 * @author David Leinweber
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

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BasicWindowController implements ActionListener {

	BasicWindow myWindow_;
	BasicWindowModel myModel_;

	public BasicWindowController(BasicWindow window, BasicWindowModel model) {
		myWindow_ = window;
		myModel_ = model;
	}

	public void actionPerformed(ActionEvent e) {
		if (myWindow_.getContentPane().getBackground() == Color.gray)
			myWindow_.getContentPane().setBackground(Color.red);
		else
			myWindow_.getContentPane().setBackground(Color.gray);
	}
}
