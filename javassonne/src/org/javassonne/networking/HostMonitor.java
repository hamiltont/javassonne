/**
 * Javassonne 
 *  http://code.google.com/p/javassonne/
 * 
 * @author [Add Name Here]
 * @date Mar 9, 2009
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.jmdns.JmDNS;

import org.javassonne.logger.LogSender;
import org.javassonne.messaging.Notification;
import org.javassonne.messaging.NotificationManager;
import org.javassonne.networking.impl.CachedHost;
import org.javassonne.networking.impl.HostMonitorListener;
import org.javassonne.networking.impl.HostResolver;
import org.javassonne.networking.impl.ThreadPool;
import org.javassonne.networking.impl.JmDNSSingleton;
import org.javassonne.networking.impl.RequestResolveMe;
import org.javassonne.networking.impl.ShareHost;

import com.thoughtworks.xstream.XStream;

/**
 * Keeps track of all host comings and goings, and handles ensuring there is 2
 * way line-of-sight (LOS). Also plays nicely with peers, by sharing all 'new'
 * hosts to all of our immediate peers.
 * 
 * Because this guy keeps track of all known hosts, he is also responsible for
 * sending global chat messages to them.
 * 
 * All resolving of hosts is done in separate threads, using RequestResolveMe,
 * ShareHost, and HostResolver
 */
public class HostMonitor {
	private List<CachedHost> cachedHosts_;
	private HashMap<String, CachedHost> pendingHosts_;
	private static HostMonitor instance_ = null;
	private XStream xStream_;

	// TODO - remove me!
	private long time_;

	private HostMonitor() {
		// Using service discovery service
		JmDNS jmdns_ = JmDNSSingleton.getJmDNS();
		HostMonitorListener hml = new HostMonitorListener();
		LogSender.sendInfo("HostMonitor - Created HML in thread "
				+ Thread.currentThread().getName());

		jmdns_.addServiceListener("_rmi._tcp.local.", hml);

		cachedHosts_ = new ArrayList<CachedHost>();
		pendingHosts_ = new HashMap<String, CachedHost>();
		xStream_ = new XStream();

		// We will handle sending chats to all known hosts
		NotificationManager.getInstance().addObserver(
				Notification.SEND_GLOBAL_CHAT, this, "sendOutGlobalChat");

	}

	// Singleton for our HostMonitor instance.
	private static HostMonitor getInstance() {
		if (instance_ == null)
			instance_ = new HostMonitor();
		return instance_;
	}

	/**
	 * @return the list of currently connected hosts
	 */
	public static List<CachedHost> getHosts() {
		return getInstance()._getHosts();
	}

	public static List<CachedHost> getHostsCopy() {
		return getInstance().deepCopyCachedHosts();
	}

	private List<CachedHost> _getHosts() {
		return cachedHosts_;
	}


	/**
	 * Allows the NotificationManager to let us know a SEND_GLOBAL_CHAT message
	 * was send
	 * 
	 * @param sendMessage
	 */
	public static void sendOutGlobalChat(Notification sendMessage) {
		getInstance()._sendOutGlobalChat(sendMessage);
	}

	private void _sendOutGlobalChat(Notification sendMessage) {
		// Convert the SEND_GLOBAL_CHAT to at RECV_GLOBAL_CHAT,
		// and send to all the known remote hosts
		Notification recvMessage = new Notification(
				Notification.RECV_GLOBAL_CHAT, sendMessage.argument());
		String serializedNotification = xStream_.toXML(recvMessage);

		for (Iterator<CachedHost> it = cachedHosts_.iterator(); it.hasNext();)
			it.next().receiveNotification(serializedNotification);

		// Also send to ourselves for classes listening for RECV_GLOBAL_CHAT
		NotificationManager.getInstance().sendNotification(
				Notification.RECV_GLOBAL_CHAT, sendMessage.argument());
	}

	/**
	 * Returns the number of hosts that we have verified as having two way line
	 * of sight
	 * 
	 * @return
	 */
	public static int numberOfHosts() {
		return getInstance()._numberOfHosts();
	}

	private int _numberOfHosts() {
		return cachedHosts_.size();
	}

	/**
	 * Used for another Host to ask us to resolve them. We will send them an ACK
	 * if we can see them (informing them of 2 way LOS). Also, we will notify
	 * all of our current peers that we found a new host
	 * 
	 * @param hostURI
	 */
	public static void resolveHost(String hostURI) {
		getInstance()._resolveHost(hostURI);
	}

	private void _resolveHost(String hostURI) {
		LogSender.sendInfo("HostMonitor: resolveHost: " + hostURI);

		if (isKnownHost(hostURI)) {
			LogSender.sendInfo("HostMonitor: resolveHost: Host " + hostURI
					+ " already known");
			return;
		}

		ThreadPool.execute(new HostResolver(hostURI));

		ThreadPool.execute(new ShareHost(hostURI, deepCopyCachedHosts()));

		LogSender.sendInfo("HostMonitor: resolveHost exiting");
	}

	/**
	 * Called by one of our current peers to inform us of a host that they
	 * believe to be new/available. We will attempt to resolve the host
	 * 
	 * @param hostURI
	 */
	public static void shareHost(String hostURI) {
		getInstance()._shareHost(hostURI);
	}

	private void _shareHost(String hostURI) {
		LogSender.sendInfo("HostMonitor: shareHost: " + hostURI);

		if (isKnownHost(hostURI)) {
			LogSender.sendInfo("HostMonitor: shareHost: " + hostURI
					+ " already known");
			return;
		}

		// Request they add us
		ThreadPool.execute(new RequestResolveMe(LocalHost.getURI()));

		LogSender.sendInfo("HostMonitor: shareHost exiting");
	}

	/**
	 * Quick way to check if a host is already known
	 * 
	 * @param hostURI
	 * @return true = host known, false = host unknown
	 */
	private boolean isKnownHost(String hostURI) {
		for (Iterator<CachedHost> it = cachedHosts_.iterator(); it.hasNext();)
			if (it.next().getURI().equals(hostURI))
				return true;

		return false;
	}

	/**
	 * Called by HostResolver when we know there is 2 way LOS between us and the
	 * host who contacted us, requesting we resolve them.
	 * 
	 * Because we can be resolving many hosts at one time, this is synchronized.
	 * 
	 * @param ch
	 *            The CachedHost to add
	 */
	public static void addToCachedHostList(CachedHost ch) {
		getInstance()._addToCachedHostList(ch);
	}

	private synchronized void _addToCachedHostList(CachedHost ch) {
		if (isKnownHost(ch.getURI()))
			return;
		cachedHosts_.add(ch);

		LogSender.sendInfo("HostMonitor - Added CachedHost:" + ch.getName()
				+ ", " + ch.getStatus());
	}

	/**
	 * Called by RequestResolveMe when we know we can resolve a host, but we are
	 * not sure they can resolve us
	 * 
	 * @param hostURI
	 */
	public static void addToPendingHosts(CachedHost h) {
		getInstance()._addToPendingHosts(h);
	}

	private void _addToPendingHosts(CachedHost h) {
		synchronized (pendingHosts_) {
			if (pendingHosts_.containsKey(h.getURI()) == true)
				return;

			pendingHosts_.put(h.getURI(), h);
		}

		// TODO - remove me
		time_ = System.currentTimeMillis();

		// Schedule to check if we have received an ACK from them
		// TODO - make sure 5 seconds is a decent time
		// TODO - create firewall notification

		final String hostURI = h.getURI();
		Timer t = new Timer("Pending Host - " + h.getURI(), true);
		t.schedule(new TimerTask() {
			public void run() {
				// If they have not ACK'ed us yet, something is amiss
				synchronized (pendingHosts_) {
					if (pendingHosts_.containsKey(hostURI))
						LogSender.sendErr("FIREWALL: from " + hostURI);
				}
				// Kill this timer
				cancel();
			}

		}, 5000);
	}

	/**
	 * Called by the HostMonitorListener, when it discovers a new host and we
	 * should attempt to contact that host
	 * 
	 * @param hostURI
	 */
	public static void resolveNewHost(String hostURI) {
		getInstance()._resolveNewHost(hostURI);
	}

	private void _resolveNewHost(String hostURI) {
		LogSender.sendInfo("HostMonitor: resolveNewHost: " + hostURI);

		// Check if it is the localhost
		if (hostURI.equals(LocalHost.getURI())) {
			LogSender.sendInfo("HostMonitor: Found localhost broadcast");

			return;
		}

		if (isKnownHost(hostURI)) {
			LogSender.sendInfo("HostMonitor: resolveNewHost: " + hostURI
					+ " already known");
			return;
		}

		// Request that they add us
		ThreadPool.execute(new RequestResolveMe(hostURI));

		// Share the discovered service with our peers
		ThreadPool.execute(new ShareHost(hostURI, deepCopyCachedHosts()));

		LogSender.sendInfo("HostMonitor: resolveNewHost: " + hostURI
				+ " exiting");
	}

	/**
	 * Used to pass the internal CachedHost list to our asynchronous share
	 * requests. If we do not deep copy them, then they might be iterating over
	 * the internal array while someone else is adding or removing elements
	 * 
	 * @return
	 */
	private ArrayList<CachedHost> deepCopyCachedHosts() {
		ArrayList<CachedHost> result = new ArrayList<CachedHost>();
		for (CachedHost h : this.cachedHosts_)
			result.add(new CachedHost(h, false));
		return result;
	}

	/**
	 * Used for HostMonitorListener to inform us that a service was removed
	 * 
	 * @param name
	 */
	public static void removeHost(String uriOrName) {
		getInstance()._removeHost(uriOrName);
	}

	private void _removeHost(String uriOrName) {
		for (Iterator<CachedHost> it = cachedHosts_.iterator(); it.hasNext();) {
			CachedHost next = it.next();
			// TODO - change to name.equals(JavassonneHost_ + next.getName() )
			// and verify

			if (uriOrName.equals(next.getURI())
					|| uriOrName.equals(next.getName())) {
				LogSender.sendInfo("HostMonitor: removeHost: Host '"
						+ uriOrName + "' removed");

				synchronized (pendingHosts_) {
					pendingHosts_.remove(next.getURI());
				}
				cachedHosts_.remove(next);
				break;
			}
		}

	}

	/**
	 * A remote host calls this to let us know that it can resolve us. This
	 * allows us to detect if we have a firewall blocking others from connecting
	 * to us
	 * 
	 * @param hostURI
	 */
	public static void receiveACK(String hostURI) {
		getInstance()._receiveACK(hostURI);
	}

	private void _receiveACK(String hostURI) {
		LogSender.sendInfo("HostMonitor: receiveACK: Received from " + hostURI);
		// TODO - make this work, and then record some sample connection times
		// . after we have some samples, we can add a timer above in
		// resolveNewHost

		CachedHost ch = null;
		synchronized (pendingHosts_) {
			ch = pendingHosts_.remove(hostURI);
		}

		if (ch == null) {
			LogSender
					.sendErr("HostMonitor - receiveACK - host was not in pending hosts, ignoring ACK");
			return;
		}

		// TODO - remove me
		time_ = System.currentTimeMillis() - time_;
		LogSender.sendInfo("HostMonitor - time found to be " + time_);

		cachedHosts_.add(ch);
		LogSender.sendInfo("HostMonitor: receiveACK: Added " + hostURI);
	}
}
