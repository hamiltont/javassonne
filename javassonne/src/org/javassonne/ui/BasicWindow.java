/**
 * Javassonne 
 *  http://code.google.com/p/javassonne/
 * 
 * @author Hamilton Turner
 * @date Jan 14, 2009
 * 
 * @updated David Leinweber 
 * @date Jan 20, 2009
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
// Needed for Container and Color
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;


public class BasicWindow extends JFrame {

	private Container contentPane_;
	private BasicWindowModel model_;
	
	public BasicWindow (){
		this("Window",Color.gray,new BasicWindowModel());
	}

	public BasicWindow (String title, Color background, BasicWindowModel model)
	{
		super();
		contentPane_ = getContentPane();
		
		model_ = model;
		
		setSize(300, 300);
		setTitle(title);
		
		BasicWindowController controller = new BasicWindowController(this, model);
		
		// add worldCanvas
		WorldCanvas c = new WorldCanvas();
		c.setSize(300, 300);
		contentPane_.add(c);

		// add button
		JButton toggle = new JButton("Change BG Color");
		toggle.addActionListener(controller);
		
		contentPane_.setBackground(background);
		contentPane_.setLayout(new FlowLayout());
		
		contentPane_.add(toggle);
				
		addWindowListener(new WindowDestroyer());		
	}
}