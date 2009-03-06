/**
 * Javassonne 
 *  http://code.google.com/p/javassonne/
 * 
 * @author [Add Name Here]
 * @date Mar 5, 2009
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

import java.net.UnknownHostException;
import java.rmi.RemoteException;

import javax.jmdns.ServiceInfo;

import org.javassonne.networking.impl.RemotingUtils;


public class ClientImpl implements Client {
	private static final String SERVICENAME = "JavassonneClient";
	private boolean connected_ = false;			// Lets this client know if it is
												//   currently connected to a host
	private String localHost_;					// The address of the local host. Used
												//   for when this computer is the host
	private ServiceInfo service_;				// The serviceInfo associated with this 
												//    clients RMI service
	private Host host_;							// The host we are currently connected to, 
												//    if any
	private String clientURI_;					// The URI of this client
	
	private ClientImpl() {}
	
	
	
	public ClientImpl(String localHost) {
		localHost_ = localHost;
		
		// Create the RMI service
		try {
			service_ = RemotingUtils.exportRMIService(this, Client.class, SERVICENAME);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		// TODO rather than using the ServiceInfo wrapper provided, we should
		//      probably just do this manually. the call to service_.getHostAddr
		//		will fail b/c we have not registered this service with JmDNS,
		//		which we have no real desire/need to do
		clientURI_ = "rmi://"+ RemotingUtils.LOCAL_HOST + ":" 
			+ service_.getPort() + "/" + service_.getName();
		
	}
	
	public void receiveMessageFromHost(String msg) {
		// TODO Auto-generated method stub

	}

	
	public void sendMessageToHost(String msg) {
		if (connected_ == false)
			throw new IllegalArgumentException();
		System.out.println("Client: sending message " + msg + " to host");
		host_.receiveMessage(msg, clientURI_);
	}
	
	/**
	 * Attempt to connect to a specified host
	 * @param hostURI The host to try and connect to
	 */
	public void connectToHost(String hostURI) {
		if (connected_)
			throw new IllegalArgumentException();
		
		// should probably verify that host exists, and
		// then safely attempt to connect
		host_ = (Host)RemotingUtils.lookupRMIService(hostURI, Host.class);
		
		host_.addClient(clientURI_);
		
		connected_ = true;
	}
	
	/**
	 * Function called by this clients host that allows
	 * this client to connect to the local host. This effectively
	 * helps us start a game that we are hosting ourselves
	 */
	public void connectToLocalHost() {
		connectToHost(localHost_);
	}
	
	/**
	 * List all hosts that this client could 
	 * potentially connect to. Filter out the 
	 * local host
	 * @return list of host URI's
	 */
	public String[] listAllHosts() {
		return null;
	}

}
