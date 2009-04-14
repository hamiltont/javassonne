/**
 * Javassonne 
 *  http://code.google.com/p/javassonne/
 * 
 * @author Hamilton Turner
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

import java.util.Timer;
import java.util.TimerTask;

import org.javassonne.ui.GameState.Mode;

/**
 * Represents a RemoteHost. Handles delivering all method calls in a new thread,
 * making it safe to call in a synchronous manner (which it is NOT safe to do on
 * a RemoteHost, any calls can block).
 * 
 * @author Hamilton Turner
 * 
 */
public class CachedHost implements RemoteHost {
	private String name_;
	private String uri_;
	private Mode status_;

	public CachedHost(RemoteHost host) {
		name_ = host.getName();
		uri_ = host.getURI();
		status_ = host.getStatus();

		// Start a timer to update this hosts status
		ThreadPool.execute(new Runnable() {
			public void run() {
				Timer t = new Timer("Cached Host Updater - " + uri_);
				t.scheduleAtFixedRate(new TimerTask() {
					public void run() {
						update();
					}
				}, 10000, 1000);
			}
		});
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
	 * Polls the host every so often to update the status. Note that this is
	 * already called from inside the ThreadPool, so there is no need to call
	 * ThreadPool.execute inside of it, even though we are resolving a host
	 */
	public void update() {
		RemoteHost me = HostResolver.attemptToResolveHost(uri_);
		if (me == null)
			return;

		status_ = me.getStatus();
	}

	/**
	 * This is not guaranteed to get the exactly correct MODE, just the last
	 * mode that we know about
	 * 
	 * @return the mode_
	 */
	public Mode getStatus() {
		return status_;
	}

	/**
	 * @see org.javassonne.networking.impl.RemoteHost
	 */
	public void addClient(final String clientURI) {
		ThreadPool.execute(new Runnable() {

			public void run() {
				RemoteHost me = HostResolver.attemptToResolveHost(uri_);
				if (me == null)
					return;

				me.addClient(clientURI);
			}

		});
	}

	/**
	 * @see org.javassonne.networking.impl.RemoteHost
	 */
	public void resolveHost(final String hostURI) {
		ThreadPool.execute(new Runnable() {
			public void run() {
				RemoteHost me = HostResolver.attemptToResolveHost(uri_);
				if (me == null)
					return;

				me.resolveHost(hostURI);
			}
		});
	}

	/**
	 * @see org.javassonne.networking.impl.RemoteHost
	 */
	public void shareHost(final String hostURI) {
		ThreadPool.execute(new Runnable() {
			public void run() {
				RemoteHost me = HostResolver.attemptToResolveHost(uri_);
				if (me == null)
					return;

				me.shareHost(hostURI);
			}
		});
	}

	/**
	 * @see org.javassonne.networking.impl.RemoteHost
	 */
	public void receiveNotification(final String serializedNotification) {
		ThreadPool.execute(new Runnable() {
			public void run() {
				RemoteHost me = HostResolver.attemptToResolveHost(uri_);
				if (me == null)
					return;

				me.receiveNotification(serializedNotification);
			}
		});
	}

	/**
	 * @see org.javassonne.networking.impl.RemoteHost
	 */
	public void receiveNotificationFromClient(
			final String serializedNotification, final String clientURI) {
		ThreadPool.execute(new Runnable() {
			public void run() {
				RemoteHost me = HostResolver.attemptToResolveHost(uri_);
				if (me == null)
					return;

				me.receiveNotificationFromClient(serializedNotification,
						clientURI);
			}
		});
	}

	/**
	 * @see org.javassonne.networking.impl.RemoteHost
	 */
	public void removeClient(final String clientURI) {
		ThreadPool.execute(new Runnable() {
			public void run() {
				RemoteHost me = HostResolver.attemptToResolveHost(uri_);
				if (me == null)
					return;

				me.removeClient(clientURI);
			}
		});
	}

	/**
	 * @see org.javassonne.networking.impl.RemoteHost
	 */
	public void receiveACK(final String hostURI) {
		ThreadPool.execute(new Runnable() {
			public void run() {
				RemoteHost me = HostResolver.attemptToResolveHost(uri_);
				if (me == null)
					return;

				me.receiveACK(hostURI);
			}
		});
	}
}
