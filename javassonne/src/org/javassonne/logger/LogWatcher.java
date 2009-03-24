/**
 * Javassonne 
 *  http://code.google.com/p/javassonne/
 * 
 * @author [Add Name Here]
 * @date Mar 24, 2009
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

package org.javassonne.logger;

import org.javassonne.messaging.Notification;
import org.javassonne.messaging.NotificationManager;

public class LogWatcher {

	public LogWatcher() {
		NotificationManager.getInstance().addObserver(Notification.LOG_ERROR,
				this, "errorLog");
		NotificationManager.getInstance().addObserver(Notification.LOG_INFO,
				this, "infoLog");
		NotificationManager.getInstance().addObserver(Notification.LOG_WARNING,
				this, "warnLog");
	}
	
	public void errorLog(Notification n) {
		String message = (String)n.argument();
		System.out.println("ERROR: " + message);
	}
	
	public void infoLog(Notification n) {
		String message = (String)n.argument();
		System.out.println("INFO: " + message);
	}
	
	public void warnLog(Notification n) {
		String message = (String)n.argument();
		System.out.println("WARN: " + message);
	}
}
