/**
 * Javassonne 
 *  http://code.google.com/p/javassonne/
 * 
 * @author Hamilton Turner
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

import java.rmi.RemoteException;

import org.javassonne.logger.LogWatcher;
import org.javassonne.messaging.Notification;
import org.javassonne.messaging.NotificationManager;
import org.javassonne.networking.HostMonitor;
import org.javassonne.networking.LocalHost;
import org.javassonne.networking.impl.JmDNSSingleton;
import org.javassonne.networking.impl.RemotingUtils;
import org.javassonne.ui.controllers.GameController;
import org.javassonne.ui.panels.LogPanel;
import org.springframework.context.ApplicationEvent;

public class Main {

	public static void main(String args[]) {
		// Create our LogWatcher
		LogWatcher lw = new LogWatcher();

		// Make sure our HostMonitor singleton is started
		HostMonitor.getInstance();

		// create the application controller. This will handle starting a new
		// game, etc...
		GameController controller = new GameController();

		// create the game window
		DisplayWindow window = new DisplayWindow();
		window.setVisible(true);

		// set the display helper's layeredPane so that other controllers can
		// add JPanels to the window really easily.
		DisplayHelper.getInstance().setDesktopPane(
				window.getDisplayDesktopPane());

		// Display the log panel. This is for debugging purposes.
		LogPanel.getInstance().setVisible(false);

		// Send a notification to the GameController to show the main menu
		NotificationManager.getInstance().sendNotification(
				Notification.TOGGLE_MAIN_MENU);

		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				JmDNSSingleton.getJmDNS().close();
				try {
					RemotingUtils.shutdownService(LocalHost.getName());
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		});
	}
}
