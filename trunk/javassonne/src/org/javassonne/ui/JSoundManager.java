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

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.javassonne.messaging.Notification;
import org.javassonne.messaging.NotificationManager;

import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

public class JSoundManager {

	public static final String TADA = "sounds/tada.wav";
	public static final String START_UP = "sounds/startup.wav";
	
	private Properties registered_ = new Properties();
	
	/**
	 * Class functions to listen for key presses on any component it is attached
	 * to. Key presses of significance will notify observers via the messaging
	 * system
	 */
	
	private static final long serialVersionUID = 1L;
	private static JSoundManager instance_ = null;

	protected JSoundManager() {
		// Attach sounds to notifications
		register_sound(Notification.START_GAME, TADA);
	}

	// Provide access to singleton
	public static JSoundManager getInstance() {
		if (instance_ == null) {
			instance_ = new JSoundManager();
		}
		return instance_;
	}

	public void play(String name) {
		try {
			InputStream in = new FileInputStream(name);
			AudioStream as = new AudioStream(in);
			AudioPlayer.player.start(as);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void play_notification(Notification n){
		play(registered_.getProperty(n.identifier()));
	}
	
	public void register_sound(String notification_name, String sound_name){
		registered_.setProperty(notification_name, sound_name);
		NotificationManager.getInstance().addObserver(notification_name, this, "play_notification");
	}
}
