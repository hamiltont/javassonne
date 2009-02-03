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

import javax.swing.JOptionPane;

import org.javassonne.messaging.Notification;
import org.javassonne.messaging.NotificationManager;

public class Main {

	private static final String LOAD_SAVE_GAME = "Load Save Game";
	private static final String START_NEW_GAME = "Start New Game";
	private static final String WELCOME = "Welcome to Javassonne!";

	public static void main(String args[]) {
		// create the application controller. This will handle starting a new
		// game, etc...
		GameController controller = new GameController();

		// create the game window
		DisplayWindow window = new DisplayWindow();
		window.setVisible(true);

		// set the display helper's layeredPane so that other controllers can
		// add JPanels to
		// the window really easily.
		DisplayHelper.getInstance().setLayeredPane(
				window.getDisplayLayeredPane());

		// Display a prompt to determine if a new game should be started or if
		// one should be loaded from a saved game file
		Object[] options = { START_NEW_GAME, LOAD_SAVE_GAME };

		int ans = JOptionPane.showOptionDialog(null, WELCOME, "Javassonne",
				JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
				options, options[0]);

		if (ans == JOptionPane.YES_OPTION || ans == JOptionPane.CLOSED_OPTION) {
			NotificationManager.getInstance().sendNotification(
					Notification.NEW_GAME);
		} else if (ans == JOptionPane.NO_OPTION) {
			NotificationManager.getInstance().sendNotification(
					Notification.LOAD_GAME);
		}

		// create the log window
		LogWindow log = new LogWindow();
		log.setVisible(true);
	}
}
