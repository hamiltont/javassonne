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

import java.util.HashMap;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

import org.javassonne.messaging.Notification;
import org.javassonne.messaging.NotificationManager;

public class Main {

	private static final String QUIT_GAME = "Quit";
	private static final String LOAD_SAVE_GAME = "Load Saved Game";
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
		DisplayHelper.getInstance().setDesktopPane(
				window.getDisplayDesktopPane());

		// Display the log panel. This is for debugging purposes.
		LogPanel.getInstance().setVisible(false);

		// Display a prompt to determine if a new game should be started or if
		// one should be loaded from a saved game file
		Object[] options = { START_NEW_GAME, QUIT_GAME };// , LOAD_SAVE_GAME };
		// Load Game not ready yet

		JOptionPane p = new JOptionPane();
		p.setOptions(options);
		p.setIcon(new ImageIcon("images/logo.jpg", ""));
		p.setMessageType(JOptionPane.QUESTION_MESSAGE);
		p.setMessage(null);
		p.setSize(420, 170);
		p.validate();

		JDialog dialog = p
				.createDialog(window.getDisplayDesktopPane(), WELCOME);
		dialog.setAlwaysOnTop(true);
		dialog.show();
		Object ans = p.getValue();

		// Disable the "Are You Sure?" dialog if they exit at startup
		HashMap config = new HashMap();
		config.put("hideConfirm", true);

		if (ans == START_NEW_GAME) {
			NotificationManager.getInstance().sendNotification(
					Notification.NEW_GAME);
		} else if (ans == QUIT_GAME) {
			NotificationManager.getInstance().sendNotification(
					Notification.EXIT_GAME, config);
		} else {
			// Default action -> Exit Game
			NotificationManager.getInstance().sendNotification(
					Notification.EXIT_GAME, config);

		}
	}
}
