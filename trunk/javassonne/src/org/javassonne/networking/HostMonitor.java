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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;
import javax.swing.SwingUtilities;

import org.javassonne.networking.impl.RemotingUtils;

public class HostMonitor {

	private class ServiceRequestor implements Runnable {
		private ServiceEvent event_;

		public ServiceRequestor(ServiceEvent e) {
			event_ = e;
		}

		public void run() {
			jmdns_.requestServiceInfo(event_.getType(), event_.getName());
		}
	}

	private class HostMonitorListener implements ServiceListener {
		// TODO bad practice that I am accessing private parent members...
		public void serviceAdded(ServiceEvent e) {
			log("found service " + e.getName());

			if (e.getName().equals(HostImpl.SERVICENAME)) {
				SwingUtilities.invokeLater(new ServiceRequestor(e));
			}
		}

		public void serviceRemoved(ServiceEvent e) {
			log("service " + e.getName() + " removed");
		}

		public void serviceResolved(ServiceEvent e) {
			log("service finally resolved");
			if (e.getName().equals(HostImpl.SERVICENAME)) {

				ServiceInfo info = e.getInfo();
				if (info == null)
					return;
				String hostURI = "rmi://" + info.getHostAddress() + ":"
						+ info.getPort() + "/" + info.getName();
				log("Found uri of " + hostURI);
				Host h = (Host) RemotingUtils.lookupRMIService(hostURI,
						Host.class);
				hostList_.add(h);
			}
		}

		/**
		 * A very simple logger
		 * 
		 * @param msg
		 */
		private void log(String msg) {
			System.out.println("HostMonitorListener : " + msg);
		}

	}

	private JmDNS jmdns_; // Instance of JmDNS
	private List<Host> hostList_;

	public HostMonitor() {
		// Create service discovery service
		try {
			jmdns_ = JmDNS.create();
		} catch (IOException e) {
			log("An IOException occurred when creating jmdns");
			System.out.println(e.getMessage());
			e.printStackTrace();
		}

		hostList_ = new ArrayList<Host>();

		jmdns_
				.addServiceListener("_rmi._tcp.local.",
						new HostMonitorListener());
	}

	public String[] getHostNames() {
		ArrayList al = new ArrayList();
		for (Iterator<Host> it = hostList_.iterator(); it.hasNext();) {
			al.add(it.next().getName());
		}
		String str[] = (String[]) al.toArray(new String[al.size()]);
		return str;
	}

	/**
	 * A very simple logger
	 * 
	 * @param msg
	 */
	private void log(String msg) {
		System.out.println("HostMonitor : " + msg);
	}

}
