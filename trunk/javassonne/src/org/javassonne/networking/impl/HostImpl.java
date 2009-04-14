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

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Timer;

import org.javassonne.logger.LogSender;
import org.javassonne.messaging.Notification;
import org.javassonne.messaging.NotificationManager;
import org.javassonne.networking.HostMonitor;
import org.javassonne.ui.GameState;
import org.javassonne.ui.GameState.Mode;

import com.thoughtworks.xstream.XStream;

/**
 * The implementation of our local host. Note that the LocalHost class is an
 * easy way to call through to all of these classes
 * 
 * Note that this class seriously needs work. It works, but not really well and
 * not the way we would like it to
 * 
 * @author Hamilton Turner
 */
public class HostImpl implements RemoteHost {

	private ArrayList<CachedClient> connectedClients_;
	private static HostImpl instance_ = null;

	protected String myURI_;
	private String realName_;
	protected String rmiSafeName_;
	private XStream xStream_;

	public static HostImpl getInstance() {
		if (instance_ == null)
			instance_ = new HostImpl();
		return instance_;
	}

	// TODO - this needs to get a name from the preferences manager
	private HostImpl() {
		connectedClients_ = new ArrayList<CachedClient>();
		myURI_ = null;
		xStream_ = new XStream();

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
	}

	/**
	 * @see org.javassonne.networking.impl.RemoteHost
	 */
	public void addClient(final String clientURI) {
		// Check if we are waiting for clients
		if (GameState.getInstance().getMode() != Mode.WAITING) {
			ThreadPool.execute(new Runnable() {
				public void run() {
					RemoteClient c = ClientResolver
							.attemptToResolveClient(clientURI);
					if (c == null)
						return;

					// Refuse their connection
					c.addClientNAK();

					LogSender.sendInfo("HostImpl: Client '" + clientURI
							+ "' refused");
				}

			});
		} else {
			RemoteClient c = ClientResolver.attemptToResolveClient(clientURI);
			if (c == null)
				return;

			// Let them know we accepted them
			c.addClientACK();

			// Add them to our list
			CachedClient cc = new CachedClient(c);
			synchronized (connectedClients_) {
				connectedClients_.add(cc);
			}

			LogSender.sendInfo("HostImpl: Client '" + cc.getName()
					+ "' connected");
		}
	}

	/**
	 * @see org.javassonne.networking.impl.RemoteHost
	 */
	public void removeClient(String clientURI) {
		// TODO - implement me
	}

	/**
	 * @see org.javassonne.networking.impl.RemoteHost
	 */
	public String getName() {
		return realName_;
	}

	/**
	 * @see org.javassonne.networking.impl.RemoteHost
	 */
	// TODO- cleanup function by referencing the Preferences and building the
	// URI we should have
	public String getURI() {
		return myURI_;
	}

	/**
	 * Helper function that lets us know from a URI if a client is connected
	 * 
	 * @param clientURI
	 * @return
	 */
	private boolean isClientConnected(String clientURI) {
		synchronized (connectedClients_) {
			for (Iterator<CachedClient> it = connectedClients_.iterator(); it
					.hasNext();)
				if (it.next().getURI().equals(clientURI))
					return true;
		}

		// If we make it through all clients without finding...
		return false;
	}

	/**
	 * @see org.javassonne.networking.impl.RemoteHost
	 */
	public void receiveNotificationFromClient(
			final String serializedNotification, final String clientURI) {

		// If the client is not connected to us, we don't care
		if (isClientConnected(clientURI) == false) {
			LogSender.sendInfo("HostImpl: Client '" + clientURI
					+ "' tried to send us a notification, "
					+ "but was not connected to us. Notification ignored");
			return;
		}

		// Retransmit to all other clients
		// Should probably put this into a class, and pass a deep-copied
		// connectedClients
		ThreadPool.execute(new Runnable() {
			public void run() {
				synchronized (connectedClients_) {
					for (Iterator<CachedClient> it = connectedClients_
							.iterator(); it.hasNext();) {
						CachedClient cc = it.next();

						// Don't send it back to the client it
						// originated from
						if (cc.getURI().equals(clientURI))
							continue;

						cc.receiveNotificationFromHost(serializedNotification);
					}
				}
			}
		});
	}

	/**
	 * @see org.javassonne.networking.impl.RemoteHost
	 */
	public Mode getStatus() {
		return GameState.getInstance().getMode();
	}

	/**
	 * @see org.javassonne.networking.impl.RemoteHost
	 */
	// TODO - list the not allowed notifications
	public void receiveNotification(String serializedNotification) {
		Notification n = (Notification) xStream_
				.fromXML(serializedNotification);
		String id = n.identifier();

		if ((id != Notification.SEND_GLOBAL_CHAT)
				&& (id != Notification.SEND_PRIVATE_CHAT))
			NotificationManager.getInstance().sendNotification(n.identifier(),
					n.argument());
	}

	/**
	 * @see org.javassonne.networking.impl.RemoteHost
	 */
	public void resolveHost(String hostURI) {
		HostMonitor.resolveHost(hostURI);
	}

	/**
	 * @see org.javassonne.networking.impl.RemoteHost
	 */
	public void shareHost(String hostURI) {
		HostMonitor.shareHost(hostURI);
	}

	/**
	 * @see org.javassonne.networking.impl.RemoteHost
	 */
	public void receiveACK(String hostURI) {
		HostMonitor.receiveACK(hostURI);
	}
}
