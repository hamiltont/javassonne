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

package org.javassonne.networking;

import java.net.UnknownHostException;
import java.rmi.RemoteException;

import javax.jmdns.ServiceInfo;

import org.javassonne.messaging.Notification;
import org.javassonne.networking.impl.RemotingUtils;

/**
 * The implementation of our client. Note that although the functions here can
 * be called by the code running on this machine, only the functions declared in
 * the Client interface can be called remotely.
 * 
 * @author Hamilton Turner
 */
public class Client implements RemoteClient {
	
	// Lets this client know if it is
	// currently connected to a host
	private boolean connected_ = false; 
	private ServiceInfo service_; // The serviceInfo associated with this
	// clients RMI service
	private RemoteHost host_; // The host we are currently connected to,
	// if any
	private String clientURI_; // The URI of this client
	private String name_; // The player name of the client

	/**
	 * Create the RMI service
	 * 
	 * @param localHostURI
	 *            The URI of the host local to this machine, in case this
	 *            machine ends up hosting a game
	 * @param name
	 *            The name the player would like to have
	 */
	public Client(String name) {
		name_ = name;

		// Create the RMI service
		try {
			// TODO If we want to run multiplayer by using networking, ensure
			// that
			// there are no duplicate names
			service_ = RemotingUtils.exportRMIService(this, RemoteClient.class,
					RemoteClient.SERVICENAME + "_" + name);
		} catch (RemoteException e) {
			log("A RemoteException occurred while creating the RMI");
			System.out.println(e.getMessage());
			e.printStackTrace();
		} catch (UnknownHostException e) {
			log("A UnknownHostException occurrec while creating the RMI");
			System.out.println(e.getMessage());
			e.printStackTrace();
		}

		// TODO rather than using the ServiceInfo wrapper provided, we should
		// probably just do this manually. the call to service_.getHostAddr
		// will fail b/c we have not registered this service with JmDNS,
		// which we have no real desire/need to do
		clientURI_ = "rmi://" + RemotingUtils.LOCAL_HOST + ":"
				+ service_.getPort() + "/" + service_.getName();

		
	}

	/**
	 * Receive a message from the host of the game
	 */
	public void receiveMessageFromHost(String msg) {
		log("Received msg - " + msg);
	}
	
	public void receiveNotificationFromHost(Notification n) {
		log("Received notification - " + n.identifier());
	}

	/**
	 * Send a message to the host of the game
	 */
	public void sendMessageToHost(String msg) {
		if (connected_ == false)
			throw new IllegalArgumentException();
		log("Sending message " + msg + " to host");
		host_.receiveMessage(msg, clientURI_);
	}
	
	public void sendNotificationToHost(Notification n) {
		if (connected_ == false)
			throw new IllegalArgumentException();
		log("Sending notification " + n.identifier() + " to host");
		host_.receiveNotification(n, clientURI_);
	}

	/**
	 * Attempt to connect to a specified host
	 * 
	 * @param hostURI
	 *            The host to try and connect to
	 */
	public void connectToHost(String hostURI) {
		if (connected_)
			throw new IllegalArgumentException();

		// should probably verify that host exists, and
		// then safely attempt to connect
		host_ = (RemoteHost) RemotingUtils.lookupRMIService(hostURI, RemoteHost.class);

		host_.addClient(clientURI_);

		connected_ = true;
	}

	/**
	 * Return the URI that this client can be reached at
	 */
	public String getURI() {
		return clientURI_;
	}

	/**
	 * Function called by this clients host that allows this client to connect
	 * to the local host. This effectively helps us start a game that we are
	 * hosting ourselves
	 */
	public void connectToLocalHost() {
		// TODO - this call could fail 
		connectToHost(HostMonitor.getInstance().getLocalHostURI());
	}

	/**
	 * A very simple logger
	 * 
	 * @param msg
	 */
	private void log(String msg) {
		System.out.println("Client " + name_ + ": " + msg);
	}

	/**
	 * Return the name this player is using in the game
	 */
	public String getName() {
		return name_;
	}

}
