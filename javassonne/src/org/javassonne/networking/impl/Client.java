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

import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.Timer;
import java.util.TimerTask;

import javax.jmdns.ServiceInfo;

import org.javassonne.messaging.Notification;
import org.javassonne.messaging.NotificationManager;
import org.javassonne.networking.LocalHost;

/**
 * The implementation of our client. Note that although the functions here can
 * be called by the code running on this machine, only the functions declared in
 * the Client interface can be called remotely.
 * 
 * @author Hamilton Turner
 */
public class Client implements RemoteClient {

	private boolean connectedToHost_;
	private RemoteHost connectedHost_;
	private String clientURI_;
	private Client instance_;

	public Client() {
		connectedToHost_ = false;
		connectedHost_ = null;
		clientURI_ = null;

		Timer t = new Timer("Client Starter");
		t.schedule(new ClientStarter(this), 0);

		// We handle giving private chat messages to the host
		NotificationManager.getInstance().addObserver(
				Notification.SEND_PRIVATE_CHAT, this, "sendChatMessageToHost");
	}

	public Client getInstance() {
		if (instance_ == null)
			instance_ = new Client();
		return instance_;
	}

	// TODO - list notifications that are illegal to send
	// TODO - no one currently listens for RECV_PRIVATE_CHAT
	public void receiveNotificationFromHost(Notification n) {
		String info = "Received notification - " + n.identifier();

		NotificationManager.getInstance().sendNotification(
				Notification.LOG_INFO, info);

		NotificationManager.getInstance().sendNotification(n.identifier(),
				n.argument());
	}

	/**
	 * Handle the SEND_PRIVATE_CHAT notification by converting it to a
	 * RECV_PRIVATE_CHAT and forwarding it to the Host
	 */
	public void sendChatMessageToHost(Notification sendPrivChat) {
		ChatMessage cm = (ChatMessage) sendPrivChat.argument();
		String info = "Sending message " + cm.getMessage() + " to host";

		NotificationManager.getInstance().sendNotification(
				Notification.LOG_INFO, info);

		Notification recvPrivChat = new Notification(
				Notification.RECV_PRIVATE_CHAT, cm);

		sendNotificationToHost(recvPrivChat);
	}

	public void sendNotificationToHost(Notification n) {
		if (connectedToHost_ == false)
			throw new RuntimeException("Client.java tried to send a "
					+ " notification to a host, but it is not connected");

		String info = "Sending notification " + n.identifier() + " to host";

		NotificationManager.getInstance().sendNotification(
				Notification.LOG_INFO, info);

		connectedHost_.receiveNotificationFromClient(n, clientURI_);
	}

	/**
	 * Attempt to connect to a specified host
	 * 
	 * @param hostURI
	 *            The host to try and connect to
	 */
	public void connectToHost(String hostURI) {
		if (connectedToHost_)
			throw new RuntimeException("Client.java tried to connect to"
					+ " a host, but it is already connected");

		connectedHost_ = (RemoteHost) RemotingUtils.lookupRMIService(hostURI,
				RemoteHost.class);

		connectedHost_.addClient(clientURI_);

		connectedToHost_ = true;
	}

	public void disconnectFromHost() {
		if (connectedToHost_ == false)
			throw new RuntimeException("Client.java tried to disconnect from"
					+ " a host, but it is not connected");

		connectedHost_.removeClient(getURI());
		connectedToHost_ = false;
	}

	public String getURI() {
		if (clientURI_ == null) {
			ClientStarter cs = new ClientStarter(this);
			cs.run();
		}
		return clientURI_;
	}

	// TODO - get this from a preferences file somewhere
	public String getName() {
		return LocalHost.getName();
	}

	private class ClientStarter extends TimerTask {

		private Client clientToStart_;
		private boolean hasBeenCalled_;

		public ClientStarter(Client c) {
			clientToStart_ = c;
			hasBeenCalled_ = false;
		}

		public void run() {
			// Just in case
			if (hasBeenCalled_)
				return;
			hasBeenCalled_ = false;

			// Create the RMI service
			ServiceInfo service = null;
			try {
				service = RemotingUtils.exportRMIService(clientToStart_,
						RemoteClient.class, RemoteClient.SERVICENAME + "_"
								+ clientToStart_.getName());
			} catch (RemoteException e) {
				String err = "A RemoteException occurred while creating the RMI";
				err += "\n" + e.getMessage();
				err += "\nStack Trace: \n";
				for (int i = 0; i < e.getStackTrace().length; i++)
					err += e.getStackTrace()[i] + "\n";

				NotificationManager.getInstance().sendNotification(
						Notification.LOG_ERROR, err);
			} catch (UnknownHostException e) {
				String err = "A UnknownHostException occurred while creating the RMI";
				err += "\n" + e.getMessage();
				err += "\nStack Trace: \n";
				for (int i = 0; i < e.getStackTrace().length; i++)
					err += e.getStackTrace()[i] + "\n";

				NotificationManager.getInstance().sendNotification(
						Notification.LOG_ERROR, err);
			}

			// TODO rather than using the ServiceInfo wrapper provided, we
			// should probably just do this manually. the call to
			// service_.getHostAddr will fail b/c we have not registered this
			// service with JmDNS, which we have no real desire/need to do
			clientURI_ = "rmi://" + RemotingUtils.LOCAL_HOST + ":"
					+ service.getPort() + "/" + service.getName();

			// Ensure this never runs again
			cancel();
		}
	}
}
