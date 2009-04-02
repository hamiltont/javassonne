/**
 * Javassonne 
 *  http://code.google.com/p/javassonne/
 * 
 * @author Brian Salisbury
 * @date Mar 22, 2009
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

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

public class passwordPopUpPanel extends AbstractHUDPanel {

	//Action Commands
	public static final String OK = "ok";
	public static final String CANCEL = "cancel";
	
	private JPasswordField pwBox_;
	private JPanel pwMain_;
	
	public passwordPopUpPanel(){
		
		super();
		
		this.setVisible(true);
		
		setSize(300, 200);
		
		pwBox_ = new JPasswordField();
		pwBox_.setSize(266, 20);
		pwBox_.setLocation(100,150);
		
		JButton ok = new JButton(new ImageIcon("images/host_join.png"));
		ok.addActionListener((ActionListener) this);
		ok.setActionCommand(OK);
		ok.setLocation(new Point(100,200));
		ok.setSize(128, 48);
		pwMain_.add(ok);
		
		JButton cancel = new JButton(new ImageIcon("images/host_cancel.png"));
		cancel.addActionListener((ActionListener) this);
		cancel.setActionCommand(CANCEL);
		cancel.setLocation(new Point(238,200));
		cancel.setSize(128, 48);
		pwMain_.add(cancel);
		add(pwMain_);
	}	
	
	public String actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("OK")){
			return pwBox_.getPassword().toString();
		}
		else if(e.getActionCommand().equals("CANCEL")){
			return null;
		}
		else{
			return null;
		}
	}
	public static void main(String args[])
	{
		passwordPopUpPanel pw = new passwordPopUpPanel();
		pw.setVisible(true);
	}

}
