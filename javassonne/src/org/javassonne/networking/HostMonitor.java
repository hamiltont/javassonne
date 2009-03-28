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
import java.util.Iterator;
import java.util.List;

import javax.jmdns.JmDNS;
import javax.swing.SwingUtilities;

import org.javassonne.messaging.Notification;
import org.javassonne.messaging.NotificationManager;
import org.javassonne.networking.impl.CachedHost;
import org.javassonne.networking.impl.HostMonitorListener;
import org.javassonne.networking.impl.HostResolver;
import org.javassonne.networking.impl.HostResolverThreadPool;
import org.javassonne.networking.impl.JmDNSSingleton;
import org.javassonne.networking.impl.RemoteHost;
import org.javassonne.networking.impl.RemotingUtils;
import org.javassonne.networking.impl.RequestResolveMe;
import org.javassonne.networking.impl.ShareHost;
import org.springframework.remoting.RemoteLookupFailureException;

import com.thoughtworks.xstream.XStream;

/**
 * Because this guy keeps track of all known hosts, he is also responsible for
 * sending global chat messages to them.
 * 
 * HostMonitor is guaranteed to add hosts only if it can resolve those hosts.
 */
public class HostMonitor {
	private List<CachedHost> cachedHostList_;
	private static HostMonitor instance_ = null;
	private XStream xStream_;

	private HostMonitor() {
		// Using service discovery service
		JmDNS jmdns_ = JmDNSSingleton.getJmDNS();
		jmdns_
				.addServiceListener("_rmi._tcp.local.",
						new HostMonitorListener());

		cachedHostList_ = new ArrayList<CachedHost>();
		xStream_ = new XStream();

		// We will handle sending chats to all known hosts
		NotificationManager.getInstance().addObserver(
				Notification.SEND_GLOBAL_CHAT, this, "sendOutGlobalChat");

	}

	// Singleton for our HostMonitor instance.
	public static HostMonitor getInstance() {
		if (instance_ == null)
			instance_ = new HostMonitor();
		return instance_;
	}

	public List<CachedHost> getHosts() {
		return cachedHostList_;
	}

	public void sendOutGlobalChat(Notification sendMessage) {
		// Convert the SEND_GLOBAL_CHAT to at RECV_GLOBAL_CHAT,
		// and send to all the known remote hosts
		Notification recvMessage = new Notification(
				Notification.RECV_GLOBAL_CHAT, sendMessage.argument());
		final String serializedNotification = xStream_.toXML(recvMessage);

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				for (Iterator<CachedHost> it = cachedHostList_.iterator(); it
						.hasNext();)
					it.next().receiveNotification(serializedNotification);
			}
		});

		// Also send to ourselves so anyone listening for RECV_GLOBAL_CHAT will
		// get this
		NotificationManager.getInstance().sendNotification(
				Notification.RECV_GLOBAL_CHAT, sendMessage.argument());
	}

	public int numberOfHosts() {
		return cachedHostList_.size();
	}

	public void resolveHost(String hostURI) {
		System.out.println("HostMonitor: resolveHost: " + hostURI);
		if (isKnownHost(hostURI)) {
			System.out.println("HostMonitor: resolveHost: Host " + hostURI
					+ " already known");
			return;
		}

		HostResolverThreadPool.execute(new HostResolver(hostURI));
		System.out.println("HostMonitor: resolveHost exiting");
	}

	public void shareHost(String hostURI) {
		System.out.println("HostMonitor: shareHost: " + hostURI);
		if (isKnownHost(hostURI)) {
			System.out.println("HostMonitor: shareHost: " + hostURI
					+ " already known");
			return;
		}

		// Request they add us
		HostResolverThreadPool
				.execute(new RequestResolveMe(LocalHost.getURI()));
		System.out.println("HostMonitor: shareHost exiting");
	}

	// TODO asdf
	private void sendFirewallNotification() {
		System.out.println("HostMonitor: possible firewill detected");
	}

	private boolean isKnownHost(String hostURI) {
		for (Iterator<CachedHost> it = cachedHostList_.iterator(); it.hasNext();)
			if (it.next().getURI().equals(hostURI))
				return true;

		return false;
	}

	public synchronized void addToCachedHostList(CachedHost h) {
		if (isKnownHost(h.getURI()))
			return;
		cachedHostList_.add(h);
	}

	/**
	 * Local call only! Remote hosts cannot call this method
	 * @param hostURI
	 */
	public void resolveNewHost(String hostURI) {
		System.out.println("HostMonitor: resolveNewHost: " + hostURI);
		if (isKnownHost(hostURI)) {
			System.out.println("HostMonitor: resolveNewHost: " + hostURI
					+ " already known");
			return;
		}

		// Check if it is the localhost
		if (hostURI.equals(LocalHost.getURI())) {
			String info = "HostMonitor: Found localhost broadcast";

			NotificationManager.getInstance().sendNotification(
					Notification.LOG_INFO, info);

			return;
		}

		// TODO - here we should record their URI and set a timer for
		// them to call us back (for firewall detection)
		// TODO - or we could do this entirely in the RequestResolveMe thread?
		// perhaps with a wait call!?

		// Request that they add us
		HostResolverThreadPool.execute(new RequestResolveMe(hostURI));

		// Share the discovered service with our peers
		HostResolverThreadPool.execute(new ShareHost(hostURI,
				deepCopyCachedHosts()));
	}

	public RemoteHost attemptToResolveHost(String hostURI) {
		RemoteHost h = null;
		try {
			h = (RemoteHost) RemotingUtils.lookupRMIService(hostURI,
					RemoteHost.class);
		} catch (RemoteLookupFailureException e) {
			String info = "HostMonitor could not resolve host at uri: "
					+ hostURI;

			NotificationManager.getInstance().sendNotification(
					Notification.LOG_INFO, info);
			return null;
		} catch (RuntimeException e) {
			String err = "HostMonitor: A RuntimeException occurred while adding a host";
			err += "\n" + e.getMessage();
			err += "\nStack Trace: \n";
			for (int i = 0; i < e.getStackTrace().length; i++)
				err += e.getStackTrace()[i] + "\n";

			NotificationManager.getInstance().sendNotification(
					Notification.LOG_ERROR, err);
			return null;
		}
		return h;
	}

	private ArrayList<CachedHost> deepCopyCachedHosts() {
		String deepCopy = xStream_.toXML(cachedHostList_);
		return (ArrayList<CachedHost>) xStream_.fromXML(deepCopy);
	}

	public void removeHost(String name) {
		for (Iterator<CachedHost> it = cachedHostList_.iterator(); it.hasNext();) {
			CachedHost next = it.next();
			// TODO - change to name.equals(JavassonneHost_ + next.getName() )
			// and verify
			if (name.contains(next.getName())) {
				cachedHostList_.remove(next);
				break;
			}
		}
	}
	
	// TODO - implement
	public void receiveACK(String hostURI) {
		System.out.println("HostMonitor: ACK received from " + hostURI);
	}
}
