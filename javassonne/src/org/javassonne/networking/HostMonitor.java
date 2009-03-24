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
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;
import javax.swing.SwingUtilities;

import org.javassonne.messaging.Notification;
import org.javassonne.messaging.NotificationManager;
import org.javassonne.networking.impl.JmDNSSingleton;
import org.javassonne.networking.impl.LocalHostImpl;
import org.javassonne.networking.impl.RemoteHost;
import org.javassonne.networking.impl.RemotingUtils;
import org.springframework.remoting.RemoteLookupFailureException;

import com.thoughtworks.xstream.XStream;

/**
 * Because this guy keeps track of all known hosts, he is also responsible for
 * sending global chat messages to them. HostMonitor keeps an internal cache of
 * hostURI's that allow it to save many network queries.
 * 
 * HostMonitor is guaranteed to add hosts only if it can resolve those hosts.
 */
public class HostMonitor {
	// Keep a local cache of URI's so we do not have to
	// continually query all the hosts
	private List<String> hostURIs_;
	private List<RemoteHost> hostList_;
	private static HostMonitor instance_ = null;
	private XStream xStream_;

	private HostMonitor() {
		// Using service discovery service
		JmDNS jmdns_ = JmDNSSingleton.getJmDNS();
		jmdns_
				.addServiceListener("_rmi._tcp.local.",
						new HostMonitorListener());

		hostList_ = new ArrayList<RemoteHost>();
		hostURIs_ = new ArrayList<String>();
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

	public List<RemoteHost> getHosts() {
		return hostList_;
	}

	public void sendOutGlobalChat(Notification sendMessage) {
		// Convert the SEND_GLOBAL_CHAT to at RECV_GLOBAL_CHAT,
		// and send to all the known remote hosts
		for (Iterator<RemoteHost> it = hostList_.iterator(); it.hasNext();) {
			RemoteHost next = it.next();
			Notification recvMessage = new Notification(
					Notification.RECV_GLOBAL_CHAT, sendMessage.argument());
			String serializedNotification = xStream_.toXML(recvMessage);
			next.receiveNotification(serializedNotification);
		}
	}

	public int numberOfHosts() {
		return hostList_.size();
	}

	public void addHost(String hostURI) {
		// Avoid expensive host resolution if we already know this
		// host
		if (hostURIs_.contains(hostURI))
			return;

		// Try to resolve host
		RemoteHost h = null;
		try {
			h = (RemoteHost) RemotingUtils.lookupRMIService(hostURI,
					RemoteHost.class);
		} catch (RemoteLookupFailureException e) {
			String info = "HostMonitor could not resolve host at uri: "
					+ hostURI;

			NotificationManager.getInstance().sendNotification(
					Notification.LOG_INFO, info);
			return;
		} catch (RuntimeException e) {
			String err = "HostMonitor: A RuntimeException occurred while adding a host";
			err += "\n" + e.getMessage();
			err += "\nStack Trace: \n";
			for (int i = 0; i < e.getStackTrace().length; i++)
				err += e.getStackTrace()[i] + "\n";

			NotificationManager.getInstance().sendNotification(
					Notification.LOG_ERROR, err);
		}

		if (hostURI.equals(LocalHost.getURI())) {
			String info = "HostMonitor: Found localhost broadcast at "
					+ hostURI;

			NotificationManager.getInstance().sendNotification(
					Notification.LOG_INFO, info);
		} else {
			hostList_.add(h);
			hostURIs_.add(hostURI);
		}
	}

	protected void removeHost(String hostURI) {
		if (hostURIs_.contains(hostURI) == false)
			return;

		// Remove from hostList
		for (Iterator<RemoteHost> it = hostList_.iterator(); it.hasNext();) {
			RemoteHost next = it.next();
			if (next.getURI().equals(hostURI)) {
				hostList_.remove(next);
				break;
			}
		}

		// Remote from hostURIs
		hostURIs_.remove(hostURI);
	}

	private class HostMonitorListener implements ServiceListener {
		public void serviceAdded(ServiceEvent e) {
			String info = "HostMonitorListener: Found service " + e.getName();

			NotificationManager.getInstance().sendNotification(
					Notification.LOG_INFO, info);

			// We only care if this is a Javassonne host
			if (e.getName().contains(LocalHostImpl.SERVICENAME) == false)
				return;

			// Cut out instances of
			// rmi://some.ip.here:port/JavassonneHost_name (2)
			// These are 'old' instances of the current host that are still
			// alive in multicast, but will not be there when you try to make
			// calls on them
			if (e.getName().matches(".+\\(\\d+\\)") == true) {
				String info2 = "HostMonitorListener: Determined that service '"
						+ e.getName() + "' is a duplicate, ignoring";

				NotificationManager.getInstance().sendNotification(
						Notification.LOG_INFO, info2);

				return;
			}

			SwingUtilities.invokeLater(new ServiceRequestor(e));
		}

		public void serviceRemoved(ServiceEvent e) {
			String info = "HostMonitorListener: Service '" + e.getName()
					+ "' removed";

			NotificationManager.getInstance().sendNotification(
					Notification.LOG_INFO, info);
		}

		public void serviceResolved(ServiceEvent e) {
			String rinfo = "HostMonitorListener: Service '" + e.getName()
					+ "' resolved";

			NotificationManager.getInstance().sendNotification(
					Notification.LOG_INFO, rinfo);

			// We only care if this is a Javassonne host
			if (e.getName().contains(LocalHostImpl.SERVICENAME) == false)
				return;

			ServiceInfo info = e.getInfo();
			if (info == null) {
				String sinfo = "HostMonitorListener: Service '" + e.getName()
						+ "' failed to get info on";

				NotificationManager.getInstance().sendNotification(
						Notification.LOG_INFO, sinfo);
				return;
			}

			String hostURI = "rmi://" + info.getHostAddress() + ":"
					+ info.getPort() + "/" + info.getName();

			String hinfo = "HostMonitorListener: Service '" + e.getName()
					+ "' has URI of: " + hostURI;

			NotificationManager.getInstance().sendNotification(
					Notification.LOG_INFO, hinfo);

			addHost(hostURI);
		}
	}

	private class ServiceRequestor implements Runnable {
		private ServiceEvent event_;

		public ServiceRequestor(ServiceEvent e) {
			event_ = e;
		}

		public void run() {
			JmDNSSingleton.getJmDNS().requestServiceInfo(event_.getType(),
					event_.getName());
		}
	}

}
