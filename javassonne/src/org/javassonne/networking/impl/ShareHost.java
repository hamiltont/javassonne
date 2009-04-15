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

import java.util.ArrayList;
import java.util.Iterator;

import org.javassonne.logger.LogSender;

/**
 * Passes along the URI of a new host we learned of, either by discovering them
 * ourselves, or by being contacted by them. We iterate over myHosts, calling
 * myHost.shareHost(host) so that each host attempts to resolve the 'new' host.
 * Note that we never try to resolve the host here, that is not our
 * responsibility. We are just passing it along
 * 
 * Called by local HostMonitor when we find a new host, to let our peers know we
 * see a host. (aka we found the new host first, and are sharing it)
 * 
 * Called by resolveHost to let our peers know about a host that sees us. (aka
 * the new host contacted us, so we are letting everyone else know about them)
 * 
 * @author Hamilton Turner
 */
public class ShareHost implements Runnable {

	private String hostURI_;
	private ArrayList<CachedHost> otherHosts_;

	public ShareHost(String hostURI, ArrayList<CachedHost> otherHosts) {
		hostURI_ = hostURI;
		otherHosts_ = otherHosts;
	}

	public void run() {

		for (Iterator<CachedHost> it = otherHosts_.iterator(); it.hasNext();) {
			CachedHost nextCH = it.next();

			// Do not accidentally send themselves back to them
			if (nextCH.getURI().equals(hostURI_))
				continue;

			nextCH.shareHost(hostURI_);
		}

		LogSender.sendInfo("ShareHost: Shared " + hostURI_ + " with "
				+ otherHosts_.size() + " others");
	}
}
