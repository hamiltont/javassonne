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
	public static final String LOG_INFO = "NotificationLogInfo";

	public static final String MAX = "NotificationMaximize";

	public static final String TILE_ROTATE_LEFT = "NotificationGameTileRotateLeft";
	public static final String TILE_ROTATE_RIGHT = "NotificationGameTileRotateRight";
	public static final String TILE_DROPPED = "NotificationTileDropped";
	public static final String MEEPLE_VILLAGER_DRAG_STARTED = "MeepleVillagerDragStart";
	public static final String MEEPLE_VILLAGER_DROPPED = "NotificationVillagerDropped";
	public static final String MEEPLE_FARMER_DRAG_STARTED = "MeepleFarmerDragStart";
	public static final String MEEPLE_FARMER_DROPPED = "NotificationFarmerDropped";
	public static final String TILE_UNUSABLE = "NotificationTileUnusable";

	public static final String ZOOM_IN = "NotificationGameZoomIn";
	public static final String ZOOM_OUT = "NotificationGameZoomOut";
	public static final String ZOOM_CHANGED = "NotificationGameZoomChanged";
	public static final String SHIFT_BOARD = "NotificationBoardShift";

	public static final String NEW_GAME = "NotificationNewGame";
	public static final String NEW_NW_GAME = "NotificationNewNetworkGame";
	public static final String START_GAME = "NotificationStartGame";
	public static final String LOAD_GAME = "NotificationLoadGame";
	public static final String SAVE_GAME = "NotificationSaveGame";
	public static final String ATTEMPT_END_GAME = "NotificationAttemptEndGame";
	public static final String END_GAME = "NotificationEndGame";
	public static final String GAME_OVER = "NotificationGameOver";
	public static final String QUIT = "NotificationQuit";
	public static final String TOGGLE_MAIN_MENU = "NotificationToggleMainMenu";
	public static final String TOGGLE_INSTRUCTIONS = "NotificationToggleInstructions";

	// Signals that 1) on the host, we would like the clients to be started
	// and 2) on the clients, start yourself with the passed data
	public static final String START_NETWORK_GAME = "NotificationStartNetworkGame";
	public static final String END_NETWORK_TURN = "NotificationEndNetworkTurn";

	// Used to signal that the mode we are in updated
	public static final String GAME_MODE_CHANGED = "GameModeChanged";

	public static final String UPDATED_DECK = "NotificationUpdatedDeck";
	public static final String UPDATED_BOARD = "NotificationUpdatedBoard";
	public static final String UPDATED_CURRENT_PLAYER = "NotificationUpdatedCurrentPlayer";
	public static final String UPDATED_TILE_IN_HAND = "NotificationUpdatedTileInHand";
	public static final String UPDATED_GAME_IN_PROGRESS = "NotificationUpdatedGameInProgress";
	public static final String UPDATED_PLAYERS = "NotificationUpdatedPlayers";
	public static final String UPDATED_GLOBAL_MEEPLE_SET = "NotificationUpdatedMeepleSet";
	
	public static final String PLACE_TILE = "NotificationClickAddTile";
	public static final String PLACE_VILLAGER_MEEPLE = "PlaceVillagerMeeple";
	public static final String PLACE_FARMER_MEEPLE = "PlaceFarmerMeeple";
	public static final String UNDO_PLACE_TILE = "NotificationCancelTilePlacement";

	public static final String MAP_ADD_SPRITE = "NotificationMapAddSprite";
	public static final String MAP_REMOVE_SPRITE = "NotificationMapRemoveSprite";
	public static final String MAP_REMOVE_SPRITE_GROUP = "NotificationMapRemoveSpriteGroup";
	public static final String MAP_REDRAW = "NotificationMapRedraw";
	public static final String BEGIN_TURN = "NotificationBeginTurn";
	public static final String END_TURN = "NotificationEndTurn";
	public static final String SCORE_TURN = "NotificationScoreTurn";

	// Note that the RECV notifications will fire for local chat messages
	public static final String SEND_PRIVATE_CHAT = "SendPrivateChat";
	public static final String RECV_PRIVATE_CHAT = "RecvPrivateChat";

	public static final String SEND_GLOBAL_CHAT = "SendGlobalChat";
	public static final String RECV_GLOBAL_CHAT = "RecvGlobalChat";

	// Used to signal that the chat area on the map should be redrawn
	public static final String CHAT_TEXT_CHANGED = "ChatTextChanged";

	// Local only:
	public static final String DRAG_PANEL_RESET = "ResetDragPanel";
	public static final String SCORE_UPDATE = "NotificationScoreUpdate";

	// System Notifications
	public static final String ERROR = "Error";

	// Notifications that will be sent from Host to client, and from client to
	// host
	// TODO - allow private chatting! (Don't think this will allow that)
	public static String[] networkSafeNotifications = {
			Notification.START_NETWORK_GAME,
			Notification.END_NETWORK_TURN
	};

	private String identifier_ = "Undefined";
	private Object argument_ = null;
	private Boolean receivedFromHost_ = false;
	
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

	@Override
	public String toString() {
		return identifier_;
	}

	public void setReceivedFromHost(Boolean receivedFromHost_) {
		this.receivedFromHost_ = receivedFromHost_;
	}

	public Boolean receivedFromHost() {
		return receivedFromHost_;
	}

}
