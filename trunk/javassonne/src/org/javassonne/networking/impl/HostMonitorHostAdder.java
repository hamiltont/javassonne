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

import java.util.Iterator;
import java.util.List;
import java.util.TimerTask;

import org.javassonne.networking.HostMonitor;

/**
 * Used by the host to request that sometime in the background, the HostMonitor
 * tries to add all of these hosts
 * 
 * @author Hamilton Turner
 * 
 */
public class HostMonitorHostAdder extends TimerTask {

	private List<String> hostURIs_;
	private RemoteHost host_;

	public HostMonitorHostAdder(List<String> hostURIs, RemoteHost h) {
		hostURIs_ = hostURIs;
		host_ = h;
	}

	public void run() {
		// Attempt to add the hosts
		host_.addHosts(hostURIs_);

		// Ensure this timer task does not run again
		cancel();

	}

}
