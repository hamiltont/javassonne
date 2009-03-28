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

import java.util.ArrayList;
import java.util.Iterator;

import org.javassonne.networking.HostMonitor;

/**
 * Iterate over myHosts, calling myHost.shareHost(host)
 * 
 * Called by local resolveNewHost to let our peers know we see a host Called by
 * resolveHost to let our peers know about a host that sees us
 */

public class ShareHost implements Runnable {

	private String hostURI_;
	private RemoteHost host_;
	private ArrayList<CachedHost> otherHosts_;

	public ShareHost(String hostURI, ArrayList<CachedHost> otherHosts) {
		hostURI_ = hostURI;
		otherHosts_ = otherHosts;
	}

	public void run() {
		host_ = HostMonitor.getInstance().attemptToResolveHost(hostURI_);
		System.out.println("ShareHost: trying to resolve host " + hostURI_);

		// We cannot resolve them, give up
		if (host_ == null) {
			System.out.println("ShareHost: resolve failed for " + hostURI_);
			return;
		}

		CachedHost ch = new CachedHost(host_);
		HostMonitor.getInstance().addToCachedHostList(ch);

		RemoteHost nextRH;
		for (Iterator<CachedHost> it = otherHosts_.iterator(); it.hasNext();) {
			CachedHost nextCH = it.next();
			nextRH = HostMonitor.getInstance().attemptToResolveHost(
					nextCH.getURI());

			// Do not accidentally prop themselves back to them
			if (nextRH == null)
				continue;
			else if (nextCH.getURI().equals(hostURI_)) {
				System.out.println("ShareHost: Found " + hostURI_
						+ " already in the otherHosts (pending hosts needed!)");
			}

			nextRH.shareHost(hostURI_);
		}

		System.out.println("ShareHost: Shared " + hostURI_);
	}

	private void addHostWithoutPropagating() {
		RemoteHost nextRH;
		for (Iterator<CachedHost> it = otherHosts_.iterator(); it.hasNext();) {
			CachedHost nextCH = it.next();
			nextRH = HostMonitor.getInstance().attemptToResolveHost(
					nextCH.getURI());

			// Do not accidentally prop themselves back to them
			if ((nextRH == null) || nextCH.getURI().equals(hostURI_))
				continue;

			nextRH.shareHost(hostURI_);
		}
	}

}
