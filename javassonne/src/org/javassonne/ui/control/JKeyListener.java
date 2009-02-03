/**
 * Javassonne 
 *  http://code.google.com/p/javassonne/
 * 
 * @author Adam Albright
 * @date Feb 2, 2009
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

package org.javassonne.ui.control;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;

import org.javassonne.messaging.Notification;
import org.javassonne.messaging.NotificationManager;

public class JKeyListener extends JFrame implements KeyListener, ActionListener {

	/**
	 * Class functions to listen for key presses on any component it is attached
	 * to. Key presses of significance will notify observers via the messaging
	 * system
	 */
	private static final long serialVersionUID = 1L;

	public JKeyListener() {
	}

	/** Capture key event */
	public void keyTyped(KeyEvent e) {
	}

	/** Capture key event */
	public void keyPressed(KeyEvent e) {
		keyCheck(e, "press");
	}

	/** Capture key event */
	public void keyReleased(KeyEvent e) {
	}

	/** Capture key event */
	public void actionPerformed(ActionEvent e) {
	}

	/**
	 * Handler for the various types of key/button events.
	 * 
	 * @param: e (KeyEvent) - event object keyStatus (String) - indicate
	 *         position of key (ie: up, down)
	 */
	private void keyCheck(KeyEvent e, String keyStatus) {

		// Determine what type of event occurred
		int id = e.getID();
		String keyString;
		if (id == KeyEvent.KEY_TYPED) {
			keyString = Character.toString(e.getKeyChar());
		} else {
			keyString = KeyEvent.getKeyText(e.getKeyCode());
		}

		// Notify the system
		if (keyString.equalsIgnoreCase("Escape")) {
			NotificationManager.getInstance().sendNotification(
					Notification.LOG_WARNING, "Exit Detected");
			NotificationManager.getInstance().sendNotification(
					Notification.EXIT_GAME);
		}

	}
}
