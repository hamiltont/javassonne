/**
 * Javassonne 
 *  http://code.google.com/p/javassonne/
 * 
 * @author [Add Name Here]
 * @date Mar 24, 2009
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

import org.javassonne.networking.HostMonitor;

public class CachedHost implements RemoteHost {
	private String name_;
	private String uri_;
	private MODE status_;
	
	public CachedHost(RemoteHost host) {
		name_ = host.getName();
		uri_ = host.getURI();
		status_ = host.getStatus();
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name_;
	}

	/**
	 * @return the desc_
	 */
	public String getURI() {
		return uri_;
	}

	/**
	 * @return the mode_
	 */
	public MODE getStatus() {
		return status_;
	}

	public boolean addClient(String clientURI) {
		RemoteHost me = HostMonitor.getInstance().attemptToResolveHost(uri_);
		if (me == null)
			return false;
		
		return me.addClient(clientURI);
	}

	public boolean addHost(String hostURI) {
		RemoteHost me = HostMonitor.getInstance().attemptToResolveHost(uri_);
		if (me == null)
			return false;
		
		return me.addHost(hostURI);	
	}

	public boolean addHostNoConfirmation(String hostURI) {
		RemoteHost me = HostMonitor.getInstance().attemptToResolveHost(uri_);
		if (me == null)
			return false;
		
		return me.addHostNoConfirmation(hostURI);
	}

	public boolean addHostNoPropagation(String hostURI) {
		RemoteHost me = HostMonitor.getInstance().attemptToResolveHost(uri_);
		if (me == null)
			return false;
		
		return me.addHostNoPropagation(hostURI);
	}

	public boolean canClientsConnect() {
		RemoteHost me = HostMonitor.getInstance().attemptToResolveHost(uri_);
		if (me == null)
			return false;
		
		return me.canClientsConnect();
	}

	public void receiveNotification(String serializedNotification) {
		RemoteHost me = HostMonitor.getInstance().attemptToResolveHost(uri_);
		if (me == null)
			return;
		
		me.receiveNotification(serializedNotification);
	}

	public void receiveNotificationFromClient(String serializedNotification,
			String clientURI) {
		RemoteHost me = HostMonitor.getInstance().attemptToResolveHost(uri_);
		if (me == null)
			return;
		
		me.receiveNotificationFromClient(serializedNotification, clientURI);	
	}

	public void removeClient(String clientURI) {
		RemoteHost me = HostMonitor.getInstance().attemptToResolveHost(uri_);
		if (me == null)
			return;
		
		me.removeClient(clientURI);
	}
}
