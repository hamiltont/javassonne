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
	public static final String PICKUP = "sounds/pickup.wav";
	public static final String ROTATE = "sounds/rotate.wav";
	public static final String PLACE = "sounds/place.wav";
	public static final String ERROR = "sounds/error.wav";
	public static final String VICTORY = "sounds/victory.wav";
	public static final String CONFIRM = "sounds/confirm.wav";
	public static final String CANCEL = "sounds/cancel.wav";

	private Properties registered_ = new Properties();

	/**
	 * Class functions play sounds through the client's speakers
	 */

	private static final long serialVersionUID = 1L;
	private static JSoundManager instance_ = null;

	// Sounds can observe notifications and thus be played when the associated
	// notification is triggered
	protected JSoundManager() {
		// List of sounds to bind with notification on game start
		register_sound(Notification.START_GAME, TADA);
		register_sound(Notification.GAME_OVER, VICTORY);
		register_sound(Notification.ERROR, ERROR);
		register_sound(Notification.PLACE_TILE, PLACE);
		register_sound(Notification.MEEPLE_VILLAGER_DROPPED, PLACE);
		register_sound(Notification.MEEPLE_FARMER_DROPPED, PLACE);
		register_sound(Notification.MEEPLE_FARMER_DRAG_STARTED, PICKUP);
		register_sound(Notification.MEEPLE_VILLAGER_DRAG_STARTED, PICKUP);
		register_sound(Notification.END_TURN, CONFIRM);
		register_sound(Notification.UNDO_PLACE_TILE, CANCEL);
	}

	// Provide access to singleton
	public static JSoundManager getInstance() {
		if (instance_ == null) {
			instance_ = new JSoundManager();
		}
		return instance_;
	}

	/*
	 * Args: name : (string) the location of the sound file ie:
	 * JSoundManager.START_UP
	 */
	public void play(String name) {
		try {
			InputStream in = new FileInputStream(name);
			AudioStream as = new AudioStream(in);
			AudioPlayer.player.start(as);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * This function provides the callback method for the Pub-Sub notification
	 */
	public void play_notification(Notification n) {
		play(registered_.getProperty(n.identifier()));
	}

	/*
	 * Bind a sound to a notification by adding it as an observer
	 */
	public void register_sound(String notification_name, String sound_name) {
		registered_.setProperty(notification_name, sound_name);
		NotificationManager.getInstance().addObserver(notification_name, this,
				"play_notification");
	}
}
