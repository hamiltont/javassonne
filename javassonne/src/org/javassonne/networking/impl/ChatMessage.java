/**
 * Javassonne 
 *  http://code.google.com/p/javassonne/
 * 
 * @author [Add Name Here]
 * @date Mar 22, 2009
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

package org.javassonne.networking.impl;

public class ChatMessage {
	
	private String message_;
	private String senderName_;
	
	public ChatMessage(String message, String senderName) {
		message_ = message;
		senderName_ = senderName;
	}
	
	public String getMessage(){
		return message_;
	}
	public String getSenderName() {
		return senderName_;
	}
}
