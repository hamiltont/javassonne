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

import java.io.IOException;
import java.net.UnknownHostException;
import java.rmi.RemoteException;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;

import org.javassonne.networking.impl.RemotingUtils;



public class HostImpl implements Host {
	private static final String SERVICENAME = "JavassonneHost";
	
	/**
	 * Attempts to create the RMI host service, and then
	 * to broadcast it using jmdns
	 * TODO - this will fail if the default RMI port is being
	 * used. we need to try and implement a smart export
	 */
	public String start() {
		ServiceInfo info = null;
		
		// Create the RMI service
		try {
			info = RemotingUtils.exportRMIService(this, Host.class, SERVICENAME);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		// Broadcast the created service
		System.out.println("Opening JmDNS");
        JmDNS jmdns = null;
		try {
			jmdns = JmDNS.create();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
   
        System.out.println("Opened JmDNS");
        try {
			jmdns.registerService(info);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String uri = "rmi://"+ info.getHostAddress() + ":" 
				+ info.getPort() + "/" + info.getName();
		System.out.println("Clients can connect to: " + uri);
        return uri;
	}
	
	public void addClient(String clientURI) {
		// TODO Auto-generated method stub

	}

	
	public boolean canGameStart() {
		// TODO Auto-generated method stub
		return false;
	}

	
	public void receiveMessage(String msg, String clientURI_) {
		System.out.println("Host: Received message " + msg);
		
		// TODO Check that that client is in list of conencted cleitns,
		//		then send the message out to everyone else but that guy

	}

}
