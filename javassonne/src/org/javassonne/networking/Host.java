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

import org.javassonne.messaging.Notification;
import org.javassonne.networking.impl.RemotingUtils;

/**
 * The implementation of our host. Note that although the functions here can be
 * called by the code running on this machine, only the functions declared in
 * the Host interface can be called remotely.
 * 
 * @author Hamilton Turner
 */
public class Host implements RemoteHost {

	/**
	 * The possible modes the host is currently in. OPEN The host is not
	 * currently hosting a game, and is simply on the network and available.
	 * CONNECTING The host has chosen to host a game, and is currently accepting
	 * connections
	 */
	private static enum Mode {
		OPEN, CONNECTING
	};

	// Currently connected clients
	private List<RemoteClient> clients_ = new ArrayList<RemoteClient>();

	// The current mode of the host
	private Mode currentMode_ = Mode.OPEN;

	private JmDNS jmdns_;

	// The URI this host can be reached at
	private String hostURI_;

	// The name this host goes by
	private String realName_;
	
	// The RMI safe name of the host
	private String rmiSafeName_;

	/**
	 * Creates the RMI host service, then broadcasts it using JmDNS
	 */
	public Host(String hostName) {
		ServiceInfo info = null;
		realName_ = hostName;
		rmiSafeName_ = realName_.replace(' ', '_');
		
		// Create the RMI service
		try {
			info = RemotingUtils.exportRMIService(this, RemoteHost.class,
					RemoteHost.SERVICENAME + "_" + rmiSafeName_);
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
		jmdns_ = JmDNSSingleton.getJmDNS();
		try {
			jmdns_.registerService(info);
		} catch (IOException e) {
			log("An IOException occurred when registering a service with jmdns");
			System.out.println(e.getMessage());
			e.printStackTrace();
		}

		hostURI_ = "rmi://" + info.getHostAddress() + ":" + info.getPort()
				+ "/" + info.getName();

		log("Clients can connect to: " + hostURI_);
	}

	/**
	 * A client can request that the host adds it to the list of connected
	 * clients
	 */
	public boolean addClient(String clientURI) {
		if (currentMode_ == Mode.CONNECTING) {
			RemoteClient c = (RemoteClient) RemotingUtils.lookupRMIService(
					clientURI, RemoteClient.class);
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
	 * Get the name of this host
	 * 
	 * @return the host name
	 */
	public String getName() {
		return realName_;
	}

	/**
	 * Ask for the URI where the host can be reached
	 * 
	 * @return host URI
	 */
	public String getURI() {
		return hostURI_;
	}

	/**
	 * Accepts a message from a client and propagates it to all other clients
	 */
	public void receiveMessage(String msg, String clientURI) {
		log("Received message '" + msg + "'");

		RemoteClient c = isClientConnected(clientURI); 
		if (c == null)
			return;
		
		// Send the message out to all other clients
		for (Iterator<RemoteClient> i = clients_.iterator(); i.hasNext();) {
			RemoteClient curClient = i.next();

			// Do not send the message back out to the client that sent it to us
			if (curClient.equals(c))
				continue;

			curClient.receiveMessageFromHost(msg);
		}
	}

	private RemoteClient isClientConnected(String clientURI) {
		// If the client sending us a message is unknown to us,
		// ignore the message
		boolean knownClient = false;
		RemoteClient c = null;
		for (Iterator<RemoteClient> it = clients_.iterator(); it.hasNext();) {
			RemoteClient curClient = it.next();
			if (curClient.getURI().equals(clientURI)) {
				knownClient = true;
				c = curClient;
				break;
			}
		}
		if (knownClient == false) {
			log("Received message from unknown client");
			return null;
		}
		
		return c;
	}
	
	/**
	 * Accepts a message from a client and propagates it to all other clients
	 */
	public void receiveNotification(Notification n, String clientURI) {
		log("Received message '" + n.identifier() + "'");

		RemoteClient c = isClientConnected(clientURI); 
		if (c == null)
			return;

		// Send the message out to all other clients
		for (Iterator<RemoteClient> i = clients_.iterator(); i.hasNext();) {
			RemoteClient curClient = i.next();

			// Do not send the message back out to the client that sent it to us
			if (curClient.equals(c))
				continue;

			curClient.receiveNotificationFromHost(n);
		}
	}
	

	/**
	 * Start accepting connections when we are about to host a game
	 */
	public void startAcceptingConnections() {
		currentMode_ = Mode.CONNECTING;
	}

	/**
	 * Stop accepting connections if we are not about to host a game
	 */
	public void stopAcceptingConnections() {
		currentMode_ = Mode.OPEN;
	}

	/**
	 * A very simple logger
	 * 
	 * @param msg
	 */
	private void log(String msg) {
		System.out.println("Host: " + msg);
	}

	public boolean currentlyPlayingLocalGame() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean currentlyPlayingNetworkGame() {
		// TODO Auto-generated method stub
		return false;
	}

}
