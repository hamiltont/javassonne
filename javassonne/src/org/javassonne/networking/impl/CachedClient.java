/**
 * Javassonne 
 *  http://code.google.com/p/javassonne/
 * 
 * @author [Add Name Here]
 * @date Mar 25, 2009
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

public class CachedClient implements RemoteClient {
	private String name_;
	private String uri_;
	
	public CachedClient(RemoteClient client) {
		name_ = client.getName();
		uri_ = client.getURI();
	}

	public String getName() {
		return name_;
	}

	public String getURI() {
		return uri_;
	}

	public void receiveNotificationFromHost(final String serializedNotification) {
		ThreadPool.execute(new Runnable() {
			public void run() {
				RemoteClient me = ClientResolver.attemptToResolveClient(uri_);
				if (me == null)
					return;
				
				me.receiveNotificationFromHost(serializedNotification);
			}
		});
	}

	public void addClientACK() {
		ThreadPool.execute(new Runnable() {
			public void run() {
				RemoteClient me = ClientResolver.attemptToResolveClient(uri_);
				if (me == null)
					return;
				
				me.addClientACK();
			}
		});		
	}

	public void addClientNAK() {
		ThreadPool.execute(new Runnable() {
			public void run() {
				RemoteClient me = ClientResolver.attemptToResolveClient(uri_);
				if (me == null)
					return;
				
				me.addClientNAK();
			}
		});		
	}

}
