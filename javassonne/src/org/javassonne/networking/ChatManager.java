/**
 * Javassonne 
 *  http://code.google.com/p/javassonne/
 * 
 * @author [Add Name Here]
 * @date Mar 25, 2009
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

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Iterator;

import org.javassonne.messaging.Notification;
import org.javassonne.messaging.NotificationManager;
import org.javassonne.networking.impl.ChatMessage;

public class ChatManager implements Iterable<String> {
	private static ChatManager instance_ = null;
	private StringBuffer currentMessage_;
	private ArrayList<String> privateMessages_;
	private ArrayList<String> globalMessages_;
	private boolean addedCurrentMessage_;

	private static enum Mode {
		GLOBAL, PRIVATE
	};

	private static final int NUMBER_GLOBAL_MESSAGES = 4;
	private static final int NUMBER_PRIVATE_MESSAGES = 4;

	private static final String PREFIX_GLOBAL = "Global> ";
	private static final String PREFIX_PRIVATE = "Private> ";

	private Mode mode_;

	// Currently contains only alphanumeric, should prob add symbols
	private static String allowed_ = "abcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()[]{}<>,.?/\\|;':\"~` ";

	private ChatManager() {
		currentMessage_ = new StringBuffer();
		mode_ = Mode.GLOBAL;

		privateMessages_ = new ArrayList<String>();
		globalMessages_ = new ArrayList<String>();

		addedCurrentMessage_ = false;

		NotificationManager.getInstance().addObserver(
				Notification.RECV_GLOBAL_CHAT, this, "chatReceivedGlobal");
		NotificationManager.getInstance().addObserver(
				Notification.RECV_PRIVATE_CHAT, this, "chatReceivedPrivate");

	}

	private static ChatManager getInstance() {
		if (instance_ == null)
			instance_ = new ChatManager();
		return instance_;
	}

	public void chatReceivedGlobal(Notification n) {
		ChatMessage cm = (ChatMessage) n.argument();
		String message = cm.getSenderName() + ": " + cm.getMessage();
		globalMessages_.add(0, message);
		verifyMessageLists();
		fireChatTextChanged();
	}

	public void chatReceivedPrivate(Notification n) {
		ChatMessage cm = (ChatMessage) n.argument();
		String message = cm.getSenderName() + ": " + cm.getMessage();
		privateMessages_.add(0, message);
		verifyMessageLists();
		fireChatTextChanged();
	}

	private void verifyMessageLists() {
		if (globalMessages_.size() > NUMBER_GLOBAL_MESSAGES)
			globalMessages_.remove(NUMBER_GLOBAL_MESSAGES);
		if (privateMessages_.size() > NUMBER_PRIVATE_MESSAGES)
			privateMessages_.remove(NUMBER_PRIVATE_MESSAGES);
	}

	public static void KeyPressed(KeyEvent e) {
		getInstance()._KeyPressed(e);
	}

	private void _KeyPressed(KeyEvent e) {
		String s = String.valueOf(e.getKeyChar());
		
		// Detect enter key
		if (e.getKeyCode() == KeyEvent.VK_ENTER)
			handleEnter();

		// Detect backspace key
		else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE)
			handleBackspace();

		// Detect other valid key
		else if (allowed_.contains(s.toLowerCase())) {
			currentMessage_.append(s);
			updateCurrentMessage();
		}
	}

	private void handleEnter() {
		// TODO - remove ourselves from the ArrayList before we call this,
		// anticipating that we will immediately be re-added?
		fireChatMessageSent();
		addedCurrentMessage_ = false;
		currentMessage_.delete(0, currentMessage_.length());
		updateCurrentMessage();
	}

	private void handleBackspace() {
		if (currentMessage_.length() > 0) {
			currentMessage_.delete(currentMessage_.length() - 1,
					currentMessage_.length());
			updateCurrentMessage();
		}
	}

	private void updateCurrentMessage() {
		if (mode_ == Mode.GLOBAL) {
			if (addedCurrentMessage_)
				globalMessages_.remove(0);
			globalMessages_.add(0, PREFIX_GLOBAL + currentMessage_.toString());
		} else {
			if (addedCurrentMessage_)
				privateMessages_.remove(0);
			privateMessages_
					.add(0, PREFIX_PRIVATE + currentMessage_.toString());
		}

		addedCurrentMessage_ = true;
		verifyMessageLists();
		fireChatTextChanged();
	}

	private void fireChatTextChanged() {
		NotificationManager.getInstance().sendNotification(
				Notification.CHAT_TEXT_CHANGED);
	}

	private void fireChatMessageSent() {

		// Send the message
		if (mode_ == Mode.GLOBAL) {
			ChatMessage cm = new ChatMessage(currentMessage_.toString(),
					LocalHost.getName());
			NotificationManager.getInstance().sendNotification(
					Notification.SEND_GLOBAL_CHAT, cm);

		} else {
			currentMessage_.delete(0, PREFIX_PRIVATE.length());
			ChatMessage cm = new ChatMessage(currentMessage_.toString(),
					LocalHost.getName());
			NotificationManager.getInstance().sendNotification(
					Notification.SEND_PRIVATE_CHAT, cm);

		}
	}

	public static Iterator<String> getIterator() {
		return getInstance().iterator();
	}
	
	public Iterator<String> iterator() {
		if (instance_.mode_ == Mode.GLOBAL)
			return instance_.globalMessages_.iterator();
		return instance_.privateMessages_.iterator();
	}
	
	
}
