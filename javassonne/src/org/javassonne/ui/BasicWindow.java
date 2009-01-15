/**
 * Javasonne 
 *  http://code.google.com/p/javassonne/
 * 
 * @author Hamilton Turner
 * @date Jan 14, 2009
 * 
 * Copyright 2009 Javasonne Team
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
// Needed for Container and Color
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;


public class BasicWindow extends JFrame implements ActionListener {

	private Container contentPane_;
	public BasicWindow (){
		this("Window",Color.gray);
	}

	public BasicWindow (String title, Color background)
	{
		super();
		contentPane_ = getContentPane();
		
		setSize(300, 300);
		setTitle(title);
		
		
		JButton toggle = new JButton("Change BG Color");
		toggle.addActionListener(this);
		
		contentPane_.setBackground(background);
		contentPane_.setLayout(new FlowLayout());
		
		contentPane_.add(toggle);
				
		addWindowListener(new WindowDestroyer());		
	}

	public void actionPerformed(ActionEvent e) {
		if (contentPane_.getBackground() == Color.gray)
			contentPane_.setBackground(Color.red);
		else
			contentPane_.setBackground(Color.gray);
	}
}
