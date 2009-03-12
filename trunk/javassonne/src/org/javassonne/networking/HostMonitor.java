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

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;
import javax.swing.SwingUtilities;

import org.javassonne.networking.impl.RemotingUtils;

// TODO This should be changed into a singleton, and rather than
//  	being given the localhost URI it should be able to dynamically 
//		find it
public class HostMonitor {
	private JmDNS jmdns_;
	private List<RemoteHost> hostList_;
	private String localhostURI_ = null;
	private static HostMonitor instance_ = null;
	// Used to discover if a service is the local service
	private String localIP_;
	
	private HostMonitor() {
		// Using service discovery service
		jmdns_ = JmDNSSingleton.getJmDNS();

		hostList_ = new ArrayList<RemoteHost>();

		jmdns_
				.addServiceListener("_rmi._tcp.local.",
						new HostMonitorListener());
		
		try {
			InetAddress addr = InetAddress.getLocalHost();
			localIP_ = addr.getHostAddress();
			log("Found localip to be " + localIP_);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	//Singleton for our HostMonitor instance.
	public static HostMonitor getInstance() {
		if (instance_ == null)
			instance_ = new HostMonitor();
		return instance_;
	}

	//Returns an array of name for all hosts.
	public String[] getHostNames() {
		// Convert internal ArrayList to String[]
		ArrayList<String> al = new ArrayList<String>();
		for (Iterator<RemoteHost> it = hostList_.iterator(); it.hasNext();) {
			al.add(it.next().getName());
		}
		String str[] = (String[]) al.toArray(new String[al.size()]);
		return str;
	}
	
	// TODO make this call more fail safe
	public String getLocalHostURI() {
		if (localhostURI_ == null)
			return null;
		return localhostURI_;
	}
	
	public void setLocalHostURI(String uri) {
		localhostURI_ = uri;
	}

	protected void addHost(String hostURI) {
		RemoteHost h = (RemoteHost) RemotingUtils.lookupRMIService(hostURI,
				RemoteHost.class);
		
		if (hostURI.contains(localIP_)) {
			localhostURI_ = hostURI;
			log("found host uri to be " + hostURI);
			// TODO error - we should not be adding this,m just for testing!
			hostList_.add(h);
		}
		else
			hostList_.add(h);
	}

	protected void removeHost(String hostURI) {
		boolean removed = false;
		for(int i = 0; i < hostList_.size(); i++)
		{
			if(hostList_.get(i).getName().equals(hostURI))
			{
				hostList_.remove(i);
				removed = true;
				break;
			}
		}
		if(!removed)
		{
			//host name not found.
		}
	}

	/**
	 * A very simple logger
	 * 
	 * @param msg
	 */
	private void log(String msg) {
		System.out.println("HostMonitor : " + msg);
	}

	private class HostMonitorListener implements ServiceListener {
		public void serviceAdded(ServiceEvent e) {
			log("found service " + e.getName());

			// We only care if this is a Javassonne host
			if (e.getName().contains(Host.SERVICENAME) == false)
				return;

			SwingUtilities.invokeLater(new ServiceRequestor(e));
		}

		public void serviceRemoved(ServiceEvent e) {
			log("service " + e.getName() + " removed");
		}

		public void serviceResolved(ServiceEvent e) {
			log("service finally resolved");

			// We only care if this is a Javassonne host
			if (e.getName().contains(Host.SERVICENAME) == false)
				return;

			ServiceInfo info = e.getInfo();
			if (info == null) {
				log("getting service info failed");
				return;
			}

			String hostURI = "rmi://" + info.getHostAddress() + ":"
					+ info.getPort() + "/" + info.getName();

			log("Found uri of " + hostURI);

			addHost(hostURI);
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
