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

package org.javassonne.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;

import org.javassonne.messaging.Notification;
import org.javassonne.messaging.NotificationManager;
import org.javassonne.networking.ChatManager;
import org.javassonne.ui.panels.LogPanel;

public class JKeyListener extends JFrame implements KeyListener, ActionListener {

	/**
	 * Class functions to listen for key presses on any component it is attached
	 * to. Key presses of significance will notify observers via the messaging
	 * system
	 */
	private static final long serialVersionUID = 1L;
	private static JKeyListener instance_ = null;

	protected JKeyListener() {
	}

	// Provide access to singleton
	public static JKeyListener getInstance() {
		if (instance_ == null) {
			instance_ = new JKeyListener();
		}
		return instance_;
	}

	/** Capture key event */
	public void keyTyped(KeyEvent e) {
	}

	/** Capture key event */
	public void keyPressed(KeyEvent e) {
		keyCheck(e, "press");
		ChatManager.KeyPressed(e);
	}

	/** Capture key event */
	public void keyReleased(KeyEvent e) {
		keyCheck(e, "release");
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
		String keyString;
		if (e.getID() == KeyEvent.KEY_TYPED) {
			keyString = Character.toString(e.getKeyChar());
		} else {
			keyString = KeyEvent.getKeyText(e.getKeyCode());
		}
		
		int code = e.getKeyCode();

		// Notify the system
		// ESCAPE
		if (code == KeyEvent.VK_ESCAPE && keyStatus == "press") {
			// Close the the log panel if it's open
			if (LogPanel.getInstance().isVisible() == true) {
				LogPanel.getInstance().setVisible(false);
			}

			// User is trying to stop game play
			else {
				NotificationManager.getInstance().sendNotification(
						Notification.TOGGLE_MAIN_MENU);
			}
		}
		
		
		// +/= (Zoom In) 
		else if(code == KeyEvent.VK_EQUALS && keyStatus == "press"){
			NotificationManager.getInstance().sendNotification(
					Notification.ZOOM_IN);
		}
		
		
		// = (Zoom Out) 
		else if(code == KeyEvent.VK_MINUS && keyStatus == "press"){
			NotificationManager.getInstance().sendNotification(
					Notification.ZOOM_OUT);
		} else if (code == KeyEvent.VK_F1 && keyStatus == "press"){
			DisplayHelper.getInstance().add(LogPanel.getInstance(), DisplayHelper.Layer.PALETTE,
					DisplayHelper.Positioning.BOTTOM_LEFT);
			LogPanel.getInstance().setVisible(true);
		}
		
		else if (code == KeyEvent.VK_RIGHT || code == KeyEvent.VK_LEFT || 
				code == KeyEvent.VK_UP || code == KeyEvent.VK_DOWN)
		{
			if (keyStatus == "press")
				NotificationManager.getInstance().sendNotification(Notification.SHIFT_BOARD, code);
			else if (keyStatus == "release")
				NotificationManager.getInstance().sendNotification(Notification.SHIFT_BOARD, null);
			
		}
	}
}
