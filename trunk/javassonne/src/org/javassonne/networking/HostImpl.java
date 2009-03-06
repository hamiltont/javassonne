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
	
	/**
	 * Attempts to create the RMI host service, and then
	 * to broadcast it using jmdns
	 * TODO - this will fail if the default RMI port is being
	 * used. we need to try and implement a smart export
	 */
	public String start() {
		HostImpl server = new HostImpl();
		String uri = null;
		
		// Create the RMI service
		try {
			uri = RemotingUtils.exportRMIService(server, Host.class, "Jsonne_Host");
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		System.out.println("Clients can connect to: "+uri);
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
        ServiceInfo info = ServiceInfo.create("_rmi._tcp.local.", "JavassonneHost", 1268, 0, 0, "path=index.html");
        try {
			jmdns.registerService(info);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        
        System.out.println("Registered Service as "+info);
        System.out.println("ServiceInfo info = ServiceInfo.create(\"_http._tcp.local.\", \"JSonne_Host\", 1268, 0, 0, \"path=index.html\");");
        System.out.println("hostaddress:" + info.getHostAddress());
        System.out.println("name:" + info.getName());
        System.out.println("nicetextstring:" + info.getNiceTextString());
        System.out.println("port:" + info.getPort());
        System.out.println("priority:" + info.getPriority());
        System.out.println("qualified_name:" + info.getQualifiedName());
        System.out.println("server:" + info.getServer());
        System.out.println("textstring:" + info.getTextString());
        System.out.println("type:" + info.getType());
        System.out.println("url:" + info.getURL());
        System.out.println("path property string:" + info.getPropertyString("path"));
        
        
        return uri;
	}
	
	public void acceptClientConnection() {
		// TODO Auto-generated method stub

	}

	
	public boolean canGameStart() {
		// TODO Auto-generated method stub
		return false;
	}

	
	public void receiveMessage() {
		// TODO Auto-generated method stub

	}

}
