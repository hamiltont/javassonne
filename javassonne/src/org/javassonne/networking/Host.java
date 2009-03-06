/**
 * Javassonne 
 *  http://code.google.com/p/javassonne/
 * 
 * @author [Add Name Here]
 * @date Mar 5, 2009
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

public interface Host {

	/**
	 * Add a client to a list of internal "connected" clients. 
	 * @param clientURI the URI where this client can be reached
	 */
	public void addClient(String clientURI);
	
	/**
	 * Returns true if all clients have connected, 
	 * false otherwise
	 */
	public boolean canGameStart();
	/**
	 * Receives a message from a specified client, and 
	 * automatically sends it to all clients (besides the 
	 * one it was just contacted by)
	 */
	public void receiveMessage(String msg, String clientURI);
}
