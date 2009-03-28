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

import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;

import javax.swing.SwingUtilities;

import org.javassonne.messaging.Notification;
import org.javassonne.messaging.NotificationManager;
import org.javassonne.networking.HostMonitor;
import org.springframework.remoting.RemoteLookupFailureException;

import com.thoughtworks.xstream.XStream;

/**
 * The implementation of our local host. Note that the LocalHost class is an
 * easy way to call through to all of these classes
 * 
 * Internally this keeps a small local cache of client URI's, which allows us to
 * prevent many redundant network calls
 * 
 * @author Hamilton Turner
 */
public class LocalHostImpl implements RemoteHost {

	private List<CachedClient> connectedClients_;

	private RemoteHost.MODE currentMode_;
	private static LocalHostImpl instance_ = null;

	protected String URI_;
	private String realName_;
	protected String rmiSafeName_;
	// A flag that indicates whether or not
	// this host can be connected to
	private boolean clientsCanConnect_;

	// protected boolean isLocalHostStarted_;
	private XStream xStream_;

	public static LocalHostImpl getInstance() {
		if (instance_ == null)
			instance_ = new LocalHostImpl();
		return instance_;
	}

	// TODO - this needs to get a hostName from the preferences manager
	private LocalHostImpl() {
		connectedClients_ = new ArrayList<CachedClient>();
		clientsCanConnect_ = false;
		URI_ = null;
		currentMode_ = RemoteHost.MODE.IN_LOBBY;
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
	public void addClient(String clientURI) {
		// Check we are open for connections
		if (clientsCanConnect_ == false) {

			String info = "LocalHostImpl: Client " + clientURI
					+ " attempted  to "
					+ "connect to our host while our host was not "
					+ "accepting connections";

			NotificationManager.getInstance().sendNotification(
					Notification.LOG_ERROR, info);
		}

		RemoteClient rc = attemptToResolveClient(clientURI);
		if (rc == null)
			return;

		CachedClient cc = new CachedClient(rc);
		connectedClients_.add(cc);

		String info = "LocalHostImpl: Client '" + cc.getName() + "' connected";

		NotificationManager.getInstance().sendNotification(
				Notification.LOG_INFO, info);
	}

	public RemoteClient attemptToResolveClient(String clientURI) {
		RemoteClient rc = null;
		try {
			rc = (RemoteClient) RemotingUtils.lookupRMIService(clientURI,
					RemoteClient.class);
		} catch (RemoteLookupFailureException e) {
			String info = "LocalHostImpl could not resolve client at uri: "
					+ clientURI;

			NotificationManager.getInstance().sendNotification(
					Notification.LOG_INFO, info);
			return null;
		} catch (RuntimeException e) {
			String err = "LocalHostImpl - A RuntimeException occurred while adding "
					+ "a client at URI: " + clientURI;
			err += "\n" + e.getMessage();
			err += "\nStack Trace: \n";
			for (int i = 0; i < e.getStackTrace().length; i++)
				err += e.getStackTrace()[i] + "\n";

			NotificationManager.getInstance().sendNotification(
					Notification.LOG_ERROR, err);
			return null;
		}
		return rc;
	}

	/**
	 * @see org.javassonne.networking.impl.RemoteHost
	 */
	public void removeClient(String clientURI) {
		for (Iterator<CachedClient> it = connectedClients_.iterator(); it
				.hasNext();) {
			CachedClient next = it.next();
			// TODO - insure this works
			if (next.getURI().equals(clientURI)) {
				connectedClients_.remove(next);
				break;
			}
		}
	}

	/**
	 * @see org.javassonne.networking.impl.RemoteHost
	 */
	public boolean canClientsConnect() {
		return clientsCanConnect_;
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
	// TODO- cleanup function
	public String getURI() {
		if (URI_ == null) {
			HostStarter hs = new HostStarter();
			try {
				SwingUtilities.invokeAndWait(hs);
			} catch (InterruptedException e) {
				String err = "LocalHostImpl: interrupted while waiting on invokeAndWait()";
				NotificationManager.getInstance().sendNotification(
						Notification.LOG_ERROR, err);
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				String err = "LocalHostImpl: exception thrown from run()";
				NotificationManager.getInstance().sendNotification(
						Notification.LOG_ERROR, err);
				e.printStackTrace();
			}
		}
		return URI_;
	}

	/**
	 * Useful for other classes to know if the localhost is ready to go
	 * 
	 * @return true if the localhost is started, false otherwise
	 */
	// public boolean isLocalHostStarted() {
	// return isLocalHostStarted_;
	// }
	private boolean isClientConnected(String clientURI) {
		for (Iterator<CachedClient> it = connectedClients_.iterator(); it
				.hasNext();)
			if (it.next().getURI().equals(clientURI))
				return true;

		// If we make it through all clients without finding...
		return false;
	}

	/**
	 * @see org.javassonne.networking.impl.RemoteHost
	 */
	public void receiveNotificationFromClient(final String serializedNotification,
			final String clientURI) {

		// If the client is not connected to us, we don't care
		if (isClientConnected(clientURI) == false) {
			String info = "LocalHostImpl: Client '" + clientURI
					+ "' tried to send us a notification, "
					+ "but was not connected to us. Notification ignored";

			NotificationManager.getInstance().sendNotification(
					Notification.LOG_INFO, info);

			return;
		}

		// Retransmit to all other clients
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				for (Iterator<CachedClient> it = connectedClients_.iterator(); it
						.hasNext();) {
					CachedClient cc = it.next();

					// Don't send it back to the client it
					// originated from
					if (cc.getURI().equals(clientURI))
						continue;

					cc.receiveNotificationFromHost(serializedNotification);
				}

			}
		});
	}

	/**
	 * @see org.javassonne.networking.impl.RemoteHost
	 */
	// TODO - make this work
	public MODE getStatus() {
		return this.currentMode_;
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
		HostMonitor.getInstance().resolveHost(hostURI);
	}

	/**
	 * @see org.javassonne.networking.impl.RemoteHost
	 */
	public void shareHost(String hostURI) {
		HostMonitor.getInstance().shareHost(hostURI);
	}

	/**
	 * @see org.javassonne.networking.impl.RemoteHost
	 */
	public void receiveACK(String hostURI) {
		HostMonitor.getInstance().receiveACK(hostURI);
	}

}
