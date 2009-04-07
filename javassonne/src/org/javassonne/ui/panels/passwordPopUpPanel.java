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
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.OverlayLayout;

public class passwordPopUpPanel extends JFrame implements
ActionListener{

	//Action Commands
	public static final String OK = "ok";
	public static final String CANCEL = "cancel";
	
	private AbstractHUDPanel pwMain_ = new AbstractHUDPanel();
	private JPasswordField  pwBox_;
	
	private String pw;
	
	public passwordPopUpPanel(){
		
		super();
		
		//setBackgroundImagePath("images/password_background.jpg");
		//setVisible(true);
		//setLayout(new OverlayLayout(this));
		setSize(300, 200);
		//setAlignmentY(CENTER_ALIGNMENT);
		//setAlignmentX(CENTER_ALIGNMENT);
		
		pwBox_ = new JPasswordField();
		pwBox_.setSize(266, 20);
		pwBox_.setLocation(100,150);
		pwMain_.add(pwBox_);
		
		JButton ok = new JButton(new ImageIcon("images/join_game.png"));
		ok.addActionListener(this);
		ok.setActionCommand(OK);
		ok.setLocation(new Point(100,200));
		ok.setSize(128, 48);
		pwMain_.add(ok);
		
		JButton cancel = new JButton(new ImageIcon("images/host_cancel.png"));
		cancel.addActionListener(this);
		cancel.setActionCommand(CANCEL);
		cancel.setLocation(new Point(238,200));
		cancel.setSize(128, 48);
		pwMain_.add(cancel);
		
		this.add(pwMain_);
		this.pack();
		this.setVisible(true);
	}	
	
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("OK")){
			pw = pwBox_.getPassword().toString();
		}
		else if(e.getActionCommand().equals("CANCEL")){
			pw = null;
		}
		else{
			pw = null;
		}
	}
	
	public String getPassword(){
		return pw;
	}
	
	public static void main(String args[])
	{
		passwordPopUpPanel pw = new passwordPopUpPanel();
		pw.setVisible(true);
	}

}
