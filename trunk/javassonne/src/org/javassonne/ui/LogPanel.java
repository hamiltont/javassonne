/**
 * Javassonne 
 *  http://code.google.com/p/javassonne/
 * 
 * @author [Add Name Here]
 * @date Jan 28, 2009
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

import java.awt.BorderLayout;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Date;

import javax.swing.JPanel;

import org.javassonne.messaging.Notification;
import org.javassonne.messaging.NotificationManager;
import org.javassonne.ui.control.JKeyListener;

public class LogPanel extends JPanel implements KeyListener {

	private TextArea area_ = null;
	private TextField field_ = null;
	private StringBuilder areaString_ = null;
	private static LogPanel instance_;
	
	protected LogPanel() {
		setVisible(false);
		setLayout(new BorderLayout());
		setSize(500, 200);
		
		// create the scrolling text box
		area_ = new TextArea();
		area_.setSize(500, 200);
		area_.setFocusable(false);

		// add the area to our JPanel
		add(area_, BorderLayout.NORTH);

		// create the send notification text box
		field_ = new TextField();
		field_.setSize(500, 20);
		field_.addKeyListener(this);
		field_.setFocusable(false);

		// add the area to our JPanel
		add(field_, BorderLayout.SOUTH);

		// create a stringBuilder to represent the contents
		areaString_ = new StringBuilder();

		// add ourselves as an observer to the notification system
		NotificationManager.getInstance().addObserver(Notification.LOG_WARNING,
				this, "log");
		NotificationManager.getInstance().addObserver(Notification.LOG_ERROR,
				this, "log");

		// post a notification so something is printed
		NotificationManager.getInstance().sendNotification(
				Notification.LOG_WARNING,
				"Log window listening for notifications...");
	}
	
	// Provide access to singleton
	public static LogPanel getInstance() {
		if (instance_ == null) {
			instance_ = new LogPanel();
		}
		return instance_;
	}
	
	public void log(Notification n) {
		areaString_.append(String.format("\r%s: %s", new Date().toString(), n
				.argument().toString())
				+ "\n");
		area_.setText(areaString_.toString());
		DisplayHelper.getInstance().add(this, DisplayHelper.Layer.PALETTE,
				DisplayHelper.Positioning.TOP_RIGHT);
		setVisible(true);
	}
	


	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			NotificationManager.getInstance()
					.sendNotification(field_.getText());
			field_.setText("");
		}
	}

	public void keyReleased(KeyEvent e) {

	}

	public void keyTyped(KeyEvent e) {

	}
}
