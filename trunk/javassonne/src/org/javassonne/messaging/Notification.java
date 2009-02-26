/**
 * Javassonne 
 *  http://code.google.com/p/javassonne/
 * 
 * @author Ben Gotow
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

package org.javassonne.messaging;

/**
 * The Notification class represents a single notification sent using the
 * NotificationManager. Each notification has a few important properties:
 * 
 * argument: an object that was passed using sendNotification
 * 
 * identifier: in class, we referred to this as the EventType. It should be one
 * of the static final values below.
 * 
 * @author bengotow
 * 
 */
public class Notification {

	public static final String LOG_WARNING = "NotificationLogWarning";
	public static final String LOG_ERROR = "NotificationLogError";

	public static final String TILE_IN_HAND_CHANGED = "NotificationGameDrawTile";
	public static final String TILE_ROTATE_LEFT = "NotificationGameTileRotateLeft";
	public static final String TILE_ROTATE_RIGHT = "NotificationGameTileRotateRight";
	public static final String ZOOM_IN = "NotificationGameZoomIn";
	public static final String ZOOM_OUT = "NotificationGameZoomOut";
	public static final String ZOOM_CHANGED = "NotificationGameZoomChanged";
	public static final String DRAW_TILE = "NotificationDrawTile";

	public static final String NEW_GAME = "NotificationNewGame";
	public static final String START_GAME = "NotificationStartGame";
	public static final String LOAD_GAME = "NotificationLoadGame";
	public static final String SAVE_GAME = "NotificationSaveGame";
	public static final String EXIT_GAME = "NotificationExitGame";
	public static final String DECK_CHANGED = "NotificationDeckChanged";

	public static final String BOARD_SET = "NotificationSetBoard";
	public static final String SHUTDOWN = "NotificationShutdown";
	
	public static final String TOGGLE_MAIN_MENU = "NotificationToggleMainMenu";
	public static final String CLICK_ADD_TILE = "NotificationClickAddTile";
	public static final String TILE_DROPPED = "NotificationTileDropped";
	
	public static final String PLAYER_DATA_CHANGED = "NotificationPlayerDataChanged";
	public static final String PLAYER_DATA_RESET = "NotificationPlayerDataReset";

	private String identifier_;
	private Object argument_;

	public Notification(String identifier, Object arg) {
		identifier_ = identifier;
		argument_ = arg;
	}

	public String identifier() {
		return identifier_;
	}

	public Object argument() {
		return argument_;
	}

}
