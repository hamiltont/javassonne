/**
 * Javassonne 
 *  http://code.google.com/p/javassonne/
 * 
 * @author [Add Name Here]
 * @date Apr 7, 2009
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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.javassonne.messaging.Notification;
import org.javassonne.messaging.NotificationManager;

/**
 * This class should definitely go away. I am just using it now while the
 * Notification Manager syntax is still really clumsy
 * 
 * @author hamiltont
 * 
 */
public class LogSender {
	private static boolean printLogsDirectedToFile_ = false;
	private static boolean showInfoLogs = false;
	
	private static File logInfo = new File("LogInfo-Javassonne.txt");
	private static FileWriter output;
	static {
		try {
			output = new FileWriter(logInfo);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void sendInfo(String info) {
		if (showInfoLogs)
			NotificationManager.getInstance().sendNotification(
				Notification.LOG_INFO, info);
	}

	public static void sendErr(String err) {
		NotificationManager.getInstance().sendNotification(
				Notification.LOG_ERROR, err);

	}

	public static void sendWarn(String warn) {
		NotificationManager.getInstance().sendNotification(
				Notification.LOG_WARNING, warn);

	}

	public static void sendInfoToFile(String info) {
		try {
			if (!logInfo.exists())
				logInfo.createNewFile();
			
			output.append(info);

		} catch (Exception e) {
			e.printStackTrace();
		}

		if (printLogsDirectedToFile_)
			sendInfo(info);
	}
}
