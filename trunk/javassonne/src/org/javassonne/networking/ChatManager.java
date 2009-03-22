/**
 * Javassonne 
 *  http://code.google.com/p/javassonne/
 * 
 * @author [Add Name Here]
 * @date Mar 20, 2009
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

package org.javassonne.networking;

import java.util.ArrayList;
import java.util.Iterator;

/*
 * Handles keeping track of who is interested in chat, and sending messages
 */

// TODO - change all instances of LocalHost.getName() to some sort of Preferences.getName()
public class ChatManager {

	private ArrayList<ChatParticipant> globalObservers_;
	private ArrayList<ChatParticipant> privateObservers_;
	private static ChatManager instance_ = null;

	private ChatManager() {
		globalObservers_ = new ArrayList<ChatParticipant>();
		privateObservers_ = new ArrayList<ChatParticipant>();
	}

	private static ChatManager getInstance() {
		if (instance_ == null)
			instance_ = new ChatManager();
		return instance_;
	}

	public static void addGlobalChatListener(ChatParticipant c) {
		getInstance()._addGCListener(c);
	}

	private void _addGCListener(ChatParticipant c) {
		if (globalObservers_.contains(c) == false)
			globalObservers_.add(c);
	}

	public static void removeGlobalChatListener(ChatParticipant c) {
		getInstance()._removeGCListener(c);
	}

	private void _removeGCListener(ChatParticipant c) {
		if (globalObservers_.contains(c) == true)
			globalObservers_.remove(c);
	}

	public static void sendGlobalChat(String msg) {
		getInstance()._sendGChat(msg);
	}

	private void _sendGChat(String msg) {
		for (Iterator<ChatParticipant> it = globalObservers_.iterator(); it
				.hasNext();)
			it.next().receiveGlobalChat(msg, LocalHost.getName());
	}

	// ========================================
	// Private Chat Functions
	// ========================================

	public static void sendPrivateGameChat(String msg) {
		getInstance()._sendPGChat(msg);
	}

	private void _sendPGChat(String msg) {
		for (Iterator<ChatParticipant> it = privateObservers_.iterator(); it
				.hasNext();)
			it.next().receivePrivateGameChat(msg, LocalHost.getName());
	}

	public static void addPrivateGameChatListener(ChatParticipant c) {
		getInstance()._addPGListener(c);
	}

	private void _addPGListener(ChatParticipant c) {
		if (privateObservers_.contains(c) == false)
			privateObservers_.add(c);
	}

	public static void removePrivateGameChatListener(ChatParticipant c) {
		getInstance()._removePGListener(c);
	}

	private void _removePGListener(ChatParticipant c) {
		if (privateObservers_.contains(c) == true)
			privateObservers_.remove(c);
	}
}
