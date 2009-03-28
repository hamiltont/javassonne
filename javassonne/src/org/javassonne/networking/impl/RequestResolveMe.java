/**
 * Javassonne 
 *  http://code.google.com/p/javassonne/
 * 
 * @author [Add Name Here]
 * @date Mar 27, 2009
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
import org.javassonne.networking.LocalHost;

/**
 * Ask other clients to attempt to resolve us, and send us an ACK if they
 * succeed Calls host.resolveHost(myURI)
 * 
 * Called by resolveNewHost, to initiate a connection with a host Called by
 * shareHost, to initiate a connection with a host
 * 
 */

public class RequestResolveMe implements Runnable {
	private String hostURI_;

	/**
	 * Ctor
	 * 
	 * @param remoteHostURI
	 *            the URI of the remote host that we would like to attempt to
	 *            resolve us
	 */
	public RequestResolveMe(String remoteHostURI) {
		hostURI_ = remoteHostURI;
	}

	public void run() {
		resolveMe();
	}

	private void resolveMe() {
		System.out.println("RequestResolveMe: trying to resolve host "
				+ hostURI_);
		RemoteHost h = HostMonitor.getInstance().attemptToResolveHost(hostURI_);

		// We cannot resolve them, give up
		if (h == null) {
			System.out.println("RequestResolveMe: resolve failed for "
					+ hostURI_);
			return;
		}

		// Let them know we can see them
		// TODO - put a timer here somehow!
		h.resolveHost(LocalHost.getURI());

		// Add them to our hostMonitor
		CachedHost ch = new CachedHost(h);
		HostMonitor.getInstance().addToCachedHostList(ch);

		System.out.println("RequestResolveMe: resolve succeeded for "
				+ hostURI_);
	}
}
