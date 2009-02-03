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

import java.awt.TextArea;
import java.util.Date;

import javax.swing.JFrame;

import org.javassonne.messaging.Notification;
import org.javassonne.messaging.NotificationManager;
import org.javassonne.ui.control.JKeyListener;

public class LogWindow extends JFrame {

	private TextArea area_ = null;
	private StringBuilder areaString_ = null;
	
	public LogWindow() {
		setTitle("Log");
		setSize(400, 200);
		setVisible(true);
		
		// create the scrolling text box
		area_ = new TextArea();
		area_.setBounds(0, 0, this.getWidth(), this.getHeight());
		area_.addKeyListener(new JKeyListener());
		
		// create a stringBuilder to represent the contents
		areaString_ = new StringBuilder();
		
		// add the area to our jframe
		add(area_);

		// add ourselves as an observer to the notification system
		NotificationManager.getInstance().addObserver(Notification.LOG_WARNING, this, "log");
		NotificationManager.getInstance().addObserver(Notification.LOG_ERROR, this, "log");

		// post a notification so something is printed
		NotificationManager.getInstance().sendNotification(Notification.LOG_WARNING, 
				"Log window listening for notifications...");
	}

	public void log(Notification n)
	{
		areaString_.append(String.format("\r%s: %s", new Date().toString(), n.argument().toString()) + "\n");
		area_.setText(areaString_.toString());
	}
}
