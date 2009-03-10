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

import java.io.IOException;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;

import org.javassonne.networking.impl.RemotingUtils;

/**
 * The implementation of our host. Note that although the functions here can be
 * called by the code running on this machine, only the functions declared in
 * the Host interface can be called remotely.
 * 
 * @author Hamilton Turner
 */
public class HostImpl implements Host {
	private static final String SERVICENAME = "JavassonneHost";

	/**
	 * The possible modes the host is currently in. Anyone using the HostImpl is
	 * responsible for manually setting the mode they would like the host to be
	 * in. OPEN The host is not currently hosting a game, and is simply on the
	 * network and available. Note that the host may be currently hosting a
	 * game, and it is up to the client to ask before attempting to connect
	 * CONNECTING The host has been chosen to host a game, and is currently
	 * accepting connections
	 */
	private static enum Mode {
		OPEN, CONNECTING
	};

	private List<Client> clients_ = new ArrayList<Client>(); // The URI's of the
	// currently
	// connected clients
	private Mode currentMode_ = Mode.OPEN; // The current mode of the host
	private JmDNS jmdns_; // Instance of JmDNS
	private String uri_; // The URI the host can be reached at

	/**
	 * Attempts to create the RMI host service, and then to broadcast it using
	 * JmDNS
	 */
	public HostImpl() {
		ServiceInfo info = null;

		// Create the RMI service
		try {
			info = RemotingUtils
					.exportRMIService(this, Host.class, SERVICENAME);
		} catch (RemoteException e) {
			log("A RemoteException occurred when exporting host RMI");
			System.out.println(e.getMessage());
			e.printStackTrace();
		} catch (UnknownHostException e) {
			log("An UnknownHostException occurred when exporting host RMI");
			System.out.println(e.getMessage());
			e.printStackTrace();
		}

		// Broadcast the created service
		try {
			jmdns_ = JmDNS.create();
		} catch (IOException e) {
			log("An IOException occurred when creating jmdns");
			System.out.println(e.getMessage());
			e.printStackTrace();
		}

		try {
			jmdns_.registerService(info);
		} catch (IOException e) {
			log("An IOException occurred when registering a service with jmdns");
			System.out.println(e.getMessage());
			e.printStackTrace();
		}

		uri_ = "rmi://" + info.getHostAddress() + ":" + info.getPort() + "/"
				+ info.getName();
		log("Clients can connect to: " + uri_);
	}

	/**
	 * Ask for the URI where the host can be reached
	 * 
	 * @return host URI
	 */
	public String getURI() {
		return uri_;
	}

	/**
	 * Indicate to the host that we are about to host a game
	 */
	public void startAcceptingConnections() {
		currentMode_ = Mode.CONNECTING;
	}

	/**
	 * A client can request that the host adds it to the list of connected
	 * clients
	 */
	public boolean addClient(String clientURI) {
		if (currentMode_ == Mode.CONNECTING) {
			Client c = (Client) RemotingUtils.lookupRMIService(clientURI,
					Client.class);
			clients_.add(c);
			log("Client " + c.getName() + " connected");
			return true;
		}

		// Do not throw an exception - a stray client
		// should not be able to bring a host down
		log("Client " + clientURI + " attempted  to "
				+ "connect to our host in an unsafe manner");
		return false;
	}

	/**
	 * Accepts a message from a client and propagates it to all other clients
	 */
	public void receiveMessage(String msg, String clientURI) {
		log("Received message '" + msg + "'");

		// If the client sending us a message is unknown to us,
		// ignore the message
		boolean knownClient = false;
		Client c = null;
		for (Iterator<Client> it = clients_.iterator(); it.hasNext();) {
			Client curClient = it.next();
			if (curClient.getURI().equals(clientURI)) {
				knownClient = true;
				c = curClient;
				break;
			}
		}
		if (knownClient == false) {
			log("Received message from unknown client");
			return;
		}

		// Send the message out to all other clients
		for (Iterator<Client> i = clients_.iterator(); i.hasNext();) {
			Client curClient = i.next();

			// Do not send the message back out to the client that sent it to us
			if (curClient.equals(c))
				continue;

			curClient.receiveMessageFromHost(msg);
		}
	}

	/**
	 * Return whether or not clients can currently connect
	 */
	public boolean canClientsConnect() {
		return (currentMode_ == Mode.CONNECTING);
	}

	public boolean canGameStart() {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * A very simple logger
	 * 
	 * @param msg
	 */
	private void log(String msg) {
		System.out.println("Host: " + msg);
	}

}
