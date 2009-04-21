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

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

import org.javassonne.logger.LogSender;
import org.javassonne.messaging.Notification;
import org.javassonne.messaging.NotificationManager;
import org.javassonne.networking.LocalHost;

import com.thoughtworks.xstream.XStream;

/**
 * The implementation of our client. Note that although the functions here can
 * be called by the code running on this machine, only the functions declared in
 * the Client interface can be called remotely.
 * 
 * Also note that the LocalClient class is simply a callthru to this class
 * 
 * @author Hamilton Turner
 */
public class ClientImpl implements RemoteClient {

	private AtomicBoolean connectedToHost_;
	private AtomicBoolean connectingToHost_;
	private CachedHost host_;
	private String myURI_;
	private Timer connectionTimer_;
	private XStream xStream_;
	private static ClientImpl instance_ = null;

	private ClientImpl() {
		connectedToHost_ = new AtomicBoolean(false);
		connectingToHost_ = new AtomicBoolean(false);
		host_ = null;
		myURI_ = null;
		connectionTimer_ = new Timer("Client Connection Timer", true);
		xStream_ = new XStream();

		Timer t = new Timer("Client Starter", true);
		t.schedule(new ClientStarter(this), 0);

		// We handle giving private chat messages to the host
		NotificationManager.getInstance().addObserver(
				Notification.SEND_PRIVATE_CHAT, this, "sendChatMessageToHost");
	}

	public static ClientImpl getInstance() {
		if (instance_ == null)
			instance_ = new ClientImpl();
		return instance_;
	}

	/**
	 * @see org.javassonne.networking.impl.RemoteClient
	 */
	public void addClientACK() {
		// Update the flags
		connectedToHost_.set(true);
		connectingToHost_.set(false);

		// TODO - fire connected notification
		LogSender.sendInfo("ClientImpl - Connected to " + host_.getURI());
	}

	/**
	 * @see org.javassonne.networking.impl.RemoteClient
	 */
	public void addClientNAK() {
		// Update the flags
		connectedToHost_.set(false);
		connectingToHost_.set(false);

		// TODO - Fire rejected notification
		LogSender.sendInfo("ClientImpl - Rejected Connection to "
				+ host_.getURI());
	}

	/**
	 * Attempt to connect to a specified host
	 * 
	 * @param hostURI
	 *            The host to try and connect to
	 */
	public void connectToHost(final String hostURI) {
		if ((connectedToHost_.get() == true)
				|| (connectingToHost_.get() == true))
			return;

		ThreadPool.execute(new Runnable() {

			public void run() {
				RemoteHost rh = HostResolver.attemptToResolveHost(hostURI);
				if (rh == null)
					return;

				// Update our variables
				connectedToHost_.set(false);
				connectingToHost_.set(true);

				// Save them for later
				if (host_ == null)
					host_ = new CachedHost(rh);
				else
					synchronized (host_) {
						host_ = new CachedHost(rh);
					}

				// Ask them to add us
				rh.addClient(myURI_);

				// Start the timer
				synchronized (connectionTimer_) {
					connectionTimer_.schedule(new TimerTask() {
						public void run() {
							if (connectedToHost_.get() == false) {
								LogSender.sendWarn("ClientImpl"
										+ " - failed to receive "
										+ "connection ACK in time");
								// TODO - Implement a few more connection
								// attempts here
							}

							// Cancel the timer
							cancel();
						}
					}, 5000);
				}
			}
		});
	}

	public void disconnectFromHost() {
		if (connectedToHost_.get() == false)
			return;

		ThreadPool.execute(new Runnable() {
			public void run() {
				RemoteHost rh = HostResolver.attemptToResolveHost(host_
						.getURI());
				if (rh == null)
					return;

				// Ask them to remove us
				rh.removeClient(myURI_);
				host_ = null;

				// Update our variables
				connectedToHost_.set(false);
				connectingToHost_.set(false);
			}
		});
	}

	// TODO - get this from a preferences folder
	public String getURI() {
		return myURI_;
	}

	// TODO - get this from a preferences file somewhere
	public String getName() {
		return LocalHost.getName();
	}

	// TODO - list notifications that are illegal to send
	public void receiveNotificationFromHost(String serializedNotification) {
		if (connectedToHost_.get() == false)
			return;

		Notification n = (Notification) xStream_
				.fromXML(serializedNotification);
		n.setReceivedFromHost(true);
		
		LogSender.sendInfo("ClientImpl - Received notification - "
				+ n.identifier());

		System.out.println(n.identifier());
		
		boolean allowedNotification = false;
		for (String notif : Notification.networkSafeNotifications) {
			if (n.identifier().equals(notif)) {
				allowedNotification = true;
				break;
			}
		}

		if (allowedNotification == false) {
			LogSender.sendInfo("ClientImpl - Notification not allowed, ignoring");
			return;
		}

		NotificationManager.getInstance().sendNotification(n);
	}

	/**
	 * Handle the SEND_PRIVATE_CHAT notification by converting it to a
	 * RECV_PRIVATE_CHAT and forwarding it to the Host
	 */
	public void sendChatMessageToHost(Notification sendPrivChat) {
		if (connectedToHost_.get() == false)
			return;

		ChatMessage cm = (ChatMessage) sendPrivChat.argument();

		LogSender.sendInfo("ClientImpl - Sending message " + cm.getMessage()
				+ " to host");

		Notification recvPrivChat = new Notification(
				Notification.RECV_PRIVATE_CHAT, cm);

		sendNotificationToHost(recvPrivChat);
	}

	public void sendNotificationToHost(Notification n) {
		if (connectedToHost_.get() == false)
			return;

		// Don't send back notifications that the host sent us!
		if (n.receivedFromHost() == true)
			return;
		
		LogSender.sendInfo("Sending notification " + n.identifier()
				+ " to host");

		String serializedNotification = xStream_.toXML(n);
		host_.receiveNotificationFromClient(serializedNotification, myURI_);
	}

	/**
	 * Called by ClientStarter to initiate the client's URI
	 * 
	 * @param uri
	 */
	public void start(String uri) {
		myURI_ = uri;
	}

}
