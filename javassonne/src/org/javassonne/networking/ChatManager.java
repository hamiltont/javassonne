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
	private boolean currentlyTyping_;
	private Mode mode_;

	private static enum Mode {
		GLOBAL, PRIVATE
	};

	private static final int NUMBER_GLOBAL_MESSAGES = 6;
	private static final int NUMBER_PRIVATE_MESSAGES = 6;

	private static final String PREFIX_GLOBAL = "Global> ";
	private static final String PREFIX_PRIVATE = "Private> ";

	// Currently contains only alphanumeric, should prob add symbols
	private static String allowed_ = "abcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()[]{}<>,.?/\\|;':\"~` ";

	private ChatManager() {
		currentMessage_ = new StringBuffer();
		mode_ = Mode.GLOBAL;
		currentlyTyping_ = false;

		privateMessages_ = new ArrayList<String>();
		globalMessages_ = new ArrayList<String>();
		
		globalMessages_.add(0, "Type to chat");
		globalMessages_.add(1, "Hit shift + space to switch chat rooms");

		fireChatTextChanged();
		
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

		// Determine where to add the message
		if (currentlyTyping_ && mode_ == Mode.GLOBAL)
			globalMessages_.add(1, message);
		else
			globalMessages_.add(0, message);

		verifyMaxMessages();

		// Should we fire a notification?
		if (mode_ == Mode.GLOBAL)
			fireChatTextChanged();
	}

	public void chatReceivedPrivate(Notification n) {
		ChatMessage cm = (ChatMessage) n.argument();
		String message = cm.getSenderName() + ": " + cm.getMessage();

		// Determine where to add the message
		if (currentlyTyping_ && mode_ == Mode.PRIVATE)
			privateMessages_.add(1, message);
		else
			privateMessages_.add(0, message);

		verifyMaxMessages();

		// Should we fire a notification?
		if (mode_ == Mode.PRIVATE)
			fireChatTextChanged();
	}

	private void verifyMaxMessages() {
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
			handleBackspace(); // be sure to update currentlyTyping in here

		// Detect Mode switch
		else if ((e.getKeyCode() == KeyEvent.VK_SPACE) && (e.isShiftDown()))
			toggleMode();
		
		// Detect other valid key
		else if (allowed_.contains(s.toLowerCase())) {
			updateCurrentMessage(s);
		}
	}

	private void handleEnter() {
		// If we do not have a message to send, quit
		if (currentlyTyping_ == false)
			return;

		// Remember that Notification are synchronous, so we need to update our
		// control variable before we get called back!
		currentlyTyping_ = false;

		// Send the message
		if (mode_ == Mode.GLOBAL) {
			ChatMessage cm = new ChatMessage(currentMessage_.toString(),
					LocalHost.getName());

			// Remove the temporary message before we send
			globalMessages_.remove(0);

			NotificationManager.getInstance().sendNotification(
					Notification.SEND_GLOBAL_CHAT, cm);

		} else {
			ChatMessage cm = new ChatMessage(currentMessage_.toString(),
					LocalHost.getName());

			// Remove the temporary message before we send
			privateMessages_.remove(0);

			NotificationManager.getInstance().sendNotification(
					Notification.SEND_PRIVATE_CHAT, cm);

		}

		// Remove the message
		currentMessage_.delete(0, currentMessage_.length());
	}

	private void handleBackspace() {
		// If we have nothing to update, then return
		if (currentlyTyping_ == false)
			return;

		currentMessage_.delete(currentMessage_.length() - 1, currentMessage_
				.length());

		// Remove the old message
		if (mode_ == Mode.GLOBAL)
			globalMessages_.remove(0);
		else
			privateMessages_.remove(0);

		// If we have no more current message, we are not typing any more
		// if we do have some more, add the new message
		if (currentMessage_.length() == 0) {
			currentlyTyping_ = false;
		} else {
			// Remove the old message
			if (mode_ == Mode.GLOBAL)
				globalMessages_.add(0, PREFIX_GLOBAL + currentMessage_.toString());
			else
				privateMessages_.add(0, PREFIX_PRIVATE + currentMessage_.toString());
		}

		// We changed something
		fireChatTextChanged();
	}

	private void updateCurrentMessage(String key) {
		currentMessage_.append(key);

		if (mode_ == Mode.GLOBAL) {
			// If they are typing, remove the old message
			// If not, set them typing
			if (currentlyTyping_)
				globalMessages_.remove(0);
			else
				currentlyTyping_ = true;

			// Add the message
			globalMessages_.add(0, PREFIX_GLOBAL + currentMessage_.toString());
		} else {
			// If they are typing, remove the old message
			// If not, set them typing
			if (currentlyTyping_)
				privateMessages_.remove(0);
			else
				currentlyTyping_ = true;

			// Add the message
			privateMessages_
					.add(0, PREFIX_PRIVATE + currentMessage_.toString());
		}

		// Verify that we have not overstepped our limit
		verifyMaxMessages();

		// Redraw, please
		fireChatTextChanged();
	}
	
	// TODO - need to move the current message from private to global and vice versa, and debug
	private void toggleMode() {
		
		// Determine where to add the message
		
		if (mode_ == Mode.GLOBAL && currentlyTyping_) {
			mode_ = Mode.PRIVATE;
			String message = "Chat room changed to private";
			
			globalMessages_.remove(0);
			privateMessages_.add(0, PREFIX_PRIVATE + currentMessage_.toString());
			privateMessages_.add(1, message);
		}
		else if (mode_ == Mode.GLOBAL) 
		{
			mode_ = Mode.PRIVATE;
			String message = "Chat room changed to private";
			
			privateMessages_.add(0, message);
		}
		else if (mode_ == Mode.PRIVATE && currentlyTyping_)
		{
			mode_ = Mode.GLOBAL;
			String message = "Chat room changed to global";
			
			
			privateMessages_.remove(0);
			globalMessages_.add(0, PREFIX_GLOBAL + currentMessage_.toString());
			globalMessages_.add(1, message);	
		}
		else if (mode_ == Mode.PRIVATE)
		{
			mode_ = Mode.GLOBAL;
			String message = "Chat room changed to global";
			
			globalMessages_.add(0, message);
		}
		

		verifyMaxMessages();

		
		fireChatTextChanged();
	}
	
	private void fireChatTextChanged() {
		NotificationManager.getInstance().sendNotification(
				Notification.CHAT_TEXT_CHANGED);
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
