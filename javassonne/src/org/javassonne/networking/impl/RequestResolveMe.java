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

/**
 * 
 * Asks another host to attempt to resolve us, and send us an ACK if they
 * succeed.
 * 
 * Called by HostMonitor - resolveNewHost when we discover a new host......
 * Called by HostMonitor - shareHost when we are passed a possible new peer's
 * URI
 * 
 * Note that this is only called when we learn of an previously unknown host,
 * and would like to let them know we are here. This is not called if some
 * previously unknown host contacts us
 * 
 * @author Hamilton Turner
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
		LogSender.sendInfo("RequestResolveMe: trying to resolve host "
				+ hostURI_);

		RemoteHost h = HostResolver.attemptToResolveHost(hostURI_);

		// We cannot resolve them, give up
		if (h == null) {
			LogSender.sendInfo("RequestResolveMe: resolve failed for "
					+ hostURI_);
			return;
		}

		// Let them know we can see them
		h.resolveHost(LocalHost.getURI());

		// Add them to the pending hosts list!
		HostMonitor.addToPendingHosts(new CachedHost(h));
		
		// NOTE: If they ACK us, then they will be added to the cached host list,
		// 		 but that is the handled in HostMonitor

		LogSender.sendInfo("RequestResolveMe: resolve succeeded for "
				+ hostURI_ + ", added to pending hosts");
	}

}
