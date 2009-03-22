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
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;

import org.javassonne.messaging.Notification;
import org.javassonne.networking.impl.RemotingUtils;

/**
 * The implementation of our local host. Note that this is a singleton, and
 * therefore can be easily used by other classes to access the local host object
 * 
 * @author Hamilton Turner
 */
public class Host implements RemoteHost {

	private List<RemoteClient> connectedClients_;
	private RemoteHost.MODE currentMode_;
	private JmDNS jmdns_;
	private String URI_; // The URI this host can be reached at
	private String realName_; // The name this host goes by
	private String rmiSafeName_; // The RMI safe name of the host
	private boolean clientsCanConnect_; // A flag that indicates whether or not
	// this host can be connected to
	private static Host instance_ = null;
	private boolean isLocalHostStarted_;

	public static Host getInstance() {
		if (instance_ == null)
			instance_ = new Host();
		return instance_;
	}

	/**
	 * Creates the RMI host service, then broadcasts it using JmDNS
	 */
	// TODO - this needs to get a hostName from the preferences manager
	private Host() {
		connectedClients_ = new ArrayList<RemoteClient>();
		clientsCanConnect_ = false;
		URI_ = null;
		currentMode_ = RemoteHost.MODE.IN_LOBBY;
		isLocalHostStarted_ = false;

		InetAddress addr = null;
		try {
			addr = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		realName_ = addr.getHostName();
		rmiSafeName_ = realName_.replace(' ', '_');

		Timer t = new Timer("Host Starter");
		t.schedule(new HostStarter(), 0);
		
		//TODO come up with a nice way to throw away this timer
	}

	/**
	 * A client can request that the host adds it to the list of connected
	 * clients
	 */
	public void addClient(String clientURI) {
		if (clientsCanConnect_) {
			RemoteClient c = (RemoteClient) RemotingUtils.lookupRMIService(
					clientURI, RemoteClient.class);
			connectedClients_.add(c);
			log("Client " + c.getName() + " connected");
		} else {
			log("Client " + clientURI + " attempted  to "
					+ "connect to our host in an unsafe manner");
		}
	}

	/**
	 * Return whether or not clients can currently connect
	 */
	public boolean canClientsConnect() {
		return clientsCanConnect_;
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
		if (URI_ == null) {
			HostStarter hs = new HostStarter();
			hs.run();
		}
		return URI_;
	}
	
	/**
	 * Useful for other classes to know if the localhost is ready to go
	 * @return true if the localhost is started, false otherwise
	 */
	public boolean isLocalHostStarted() {
		return isLocalHostStarted_;
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
		for (Iterator<RemoteClient> i = connectedClients_.iterator(); i
				.hasNext();) {
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
		for (Iterator<RemoteClient> it = connectedClients_.iterator(); it
				.hasNext();) {
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
		for (Iterator<RemoteClient> i = connectedClients_.iterator(); i
				.hasNext();) {
			RemoteClient curClient = i.next();

			// Do not send the message back out to the client that sent it to us
			if (curClient.equals(c))
				continue;

			curClient.receiveNotificationFromHost(n);
		}
	}
	
	/**
	 * A very simple logger
	 * 
	 * @param msg
	 */
	private void log(String msg) {
		System.out.println("Host: " + msg);
	}

	// TODO - make this work
	// TODO - need to listen for a semi complex pattern of notifications so that
	// we know if we can accept connections and what-not
	public MODE getStatus() {
		return this.currentMode_;
	}

	private class HostStarter extends TimerTask {

		private boolean called_ = false;

		public void run() {
			// Prevent this from accidentally being called twice
			if (called_)
				return;
			called_ = true;
			isLocalHostStarted_ = true;

			// Create the RMI service
			ServiceInfo info = null;
			try {
				info = RemotingUtils.exportRMIService(Host.getInstance(), RemoteHost.class,
						RemoteHost.SERVICENAME + "_" + rmiSafeName_);
			} catch (RemoteException e) {
				log("A RemoteException occurred when exporting host RMI");
				System.out.println(e.getMessage());
				e.printStackTrace();
			} catch (UnknownHostException e) {
				log("An UnknownHostException occurred when exporting host RMI");
				System.out.println(e.getMessage());
				e.printStackTrace();
			} catch (Exception e) {
				log("Something bad happened internally in RMI");
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

			URI_ = "rmi://" + info.getHostAddress() + ":" + info.getPort()
					+ "/" + info.getName();

			log("Clients can connect to: " + URI_);
		}

	}

}
