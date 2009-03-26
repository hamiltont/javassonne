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
import org.javassonne.networking.impl.JmDNSSingleton;
import org.javassonne.networking.impl.RemoteHost;
import org.javassonne.networking.impl.RemotingUtils;
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

	public boolean addHostNoConfirmation(String hostURI) {
		System.out.println("HostMonitor: addHostNoConf(" + hostURI + ")");
		if (isKnownHost(hostURI))
			return true;

		RemoteHost h = attemptToResolveHost(hostURI);
		if (h == null)
			return false;

		// Add them for ourselves
		addToCachedHostList(new CachedHost(h));
		System.out.println("HostMonitor: host added");
		return true;
	}

	public boolean addHostNoPropagation(String hostURI) {
		System.out.println("HostMonitor: addHostNoProp(" + hostURI + ")");
		if (isKnownHost(hostURI))
			return true;

		final RemoteHost host = attemptToResolveHost(hostURI);
		if (host == null)
			return false;

		// Request they add us without confirming
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				boolean canTheySeeMe = host.addHostNoConfirmation(LocalHost
						.getURI());
				if (canTheySeeMe == false)
					sendFirewallNotification();
			}
		});

		// Add them for ourselves
		addToCachedHostList(new CachedHost(host));
		System.out.println("HostMonitor: host added");
		return true;
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

	private synchronized void addToCachedHostList(CachedHost h) {
		if (isKnownHost(h.getURI()))
			return;
		cachedHostList_.add(h);
	}

	public boolean addHost(final String hostURI) {
		System.out.println("HostMonitor: addHost(" + hostURI + ")");
		if (isKnownHost(hostURI))
			return true;

		// Check if it is the localhost
		if (hostURI.equals(LocalHost.getURI())) {
			String info = "HostMonitor: Found localhost broadcast";

			NotificationManager.getInstance().sendNotification(
					Notification.LOG_INFO, info);

			return true;
		}

		// Try to resolve host
		final RemoteHost h = attemptToResolveHost(hostURI);
		if (h == null)
			return false;

		// Add them for ourselves
		final CachedHost host = new CachedHost(h);
		addToCachedHostList(new CachedHost(h));
		System.out.println("HostMonitor: host added");

		// Request they add us without confirming
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				boolean canTheySeeMe = host.addHostNoConfirmation(LocalHost.getURI());
				if (canTheySeeMe == false)
					sendFirewallNotification();
			}
		});

		// Request all our known hosts to add them,
		// without propagating the message farther
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				for (Iterator<CachedHost> it = cachedHostList_.iterator(); it
						.hasNext();) {
					CachedHost next = it.next();

					// Forward the URL to everyone but the host
					// it came from
					// We are only interested if they can see us, not
					// hostURI, so we ignore the return here
					if (next.getURI().equals(hostURI) == false)
						next.addHostNoPropagation(hostURI);
				}
			}
		});
		return true;
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

	public void removeHost(String name) {
		for (Iterator<CachedHost> it = cachedHostList_.iterator(); it.hasNext();) {
			CachedHost next = it.next();
			if (name.contains(next.getName())) {
				cachedHostList_.remove(next);
				break;
			}
		}
	}
}
