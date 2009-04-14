/**
 * Javassonne 
 *  http://code.google.com/p/javassonne/
 * 
 * @author Hamilton Turner
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

import org.javassonne.logger.LogSender;
import org.javassonne.networking.HostMonitor;
import org.javassonne.networking.LocalHost;
import org.springframework.remoting.RemoteLookupFailureException;

/**
 * Try to resolve and acknowledge an unknown host who has contacted us. On
 * success, send host.sendACK() to confirm to them they are open for connections
 * 
 * Called by resolveHost to handle resolution and ACK when another host requests
 * we add them
 * 
 * @author Hamilton Turner
 */
public class HostResolver implements Runnable {
	private String hostURI_;

	// Resolve host,
	// send ACK,
	// create cached host,
	// HostMonitor.add(cachedHost)
	// 
	public HostResolver(String hostURI) {
		hostURI_ = hostURI;
	}

	public void run() {
		resolveNewHost();
	}

	// Resolve host, send ACK, create cached host, HostMonitor.add(cachedHost)
	// No matter the outcome, when we are done remove from pending hosts
	private void resolveNewHost() {
		RemoteHost h = HostResolver.attemptToResolveHost(hostURI_);
		LogSender.sendInfo("HostResolver: trying to resolve host " + hostURI_);

		// We cannot resolve them, give up
		if (h == null) {
			LogSender.sendErr("HostResolver: resolve failed for " + hostURI_);
			return;
		}

		// Let them know we can see them
		h.receiveACK(LocalHost.getURI());

		// Add them to our hostMonitor
		CachedHost ch = new CachedHost(h);
		HostMonitor.addToCachedHostList(ch);

		LogSender.sendInfo("HostResolver: resolve succeeded for " + hostURI_);
	}

	public static RemoteHost attemptToResolveHost(String hostURI) {
		RemoteHost h = null;
		try {
			h = (RemoteHost) RemotingUtils.lookupRMIService(hostURI,
					RemoteHost.class);

		} catch (RemoteLookupFailureException e) {
			LogSender.sendInfo("HostResolver could not resolve host at uri: "
					+ hostURI);

			return null;
		}

		return h;
	}

}
