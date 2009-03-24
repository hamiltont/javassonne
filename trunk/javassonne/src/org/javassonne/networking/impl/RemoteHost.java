/**
 * Javassonne 
 *  http://code.google.com/p/javassonne/
 * 
 * @author Hamilton Turner
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

package org.javassonne.networking.impl;


/**
 * This is the networking only interface of the host. Only put functions here
 * that a remote host or client should be able to call.
 * 
 * @author Hamilton Turner
 */
public interface RemoteHost {
	public static final String SERVICENAME = "JavassonneHost";

	public static enum MODE {
		PLAYING_LOCAL_GAME, PLAYING_NETWORK_GAME, IN_LOBBY, IDLE
		// Paused game, or currently playing the game but stepped out
	}

	/**
	 * Add a client to a list of internal "connected" clients. Note that
	 * internally this method should ensure that the client is reachable 
	 * before it adds them.
	 * 
	 * @param clientURI
	 *            The URI of the client to add
	 */
	public void addClient(String clientURI);

	/**
	 * If the client is currently considered to be connected to this host, this
	 * will remove them. Otherwise this call does nothing.
	 * 
	 * @param clientURI
	 *            The URI of the client to remove
	 */
	public void removeClient(String clientURI);

	/**
	 * Asks the host if it is ready to accept client connections
	 * 
	 * @return true if clients can connect, false otherwise
	 */
	public boolean canClientsConnect();

	/**
	 * Gets the name of this Host
	 * 
	 * @return hostname
	 */
	public String getName();

	/**
	 * Gets the URI of this host
	 * 
	 * @return host URI (address)
	 */
	public String getURI();

	/**
	 * Gets the status of this host
	 * 
	 * @return host status
	 */
	public RemoteHost.MODE getStatus();

	/**
	 * Used for clients to talk to the host. The host internally chooses to
	 * either ignore these notifications, or to redistribute them to all other
	 * clients
	 */
	public void receiveNotificationFromClient(String serializedNotification, String clientURI);

	/**
	 * Used when other hosts would like to send notifications to this host.
	 * Typically just global chat messages. Internally this host can either
	 * ignore or act on these.
	 */
	public void receiveNotification(String serializedNotification);
}
