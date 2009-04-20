/**
 * Javassonne 
 *  http://code.google.com/p/javassonne/
 * 
 * @author [Add Name Here]
 * @date Apr 13, 2009
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

import org.javassonne.networking.impl.ClientImpl;


public class LocalClient  {

	public static void addClientACK() {
		ClientImpl.getInstance().addClientACK();
	}

	public static void addClientNAK() {
		ClientImpl.getInstance().addClientNAK();
	}

	public static String getName() {
		return ClientImpl.getInstance().getName();
	}

	public static String getURI() {
		return ClientImpl.getInstance().getURI();
	}

	public static void receiveNotificationFromHost(String serializedNotification) {
		ClientImpl.getInstance().receiveNotificationFromHost(serializedNotification);
	}
	
	public static void connectToHost(String hostURI){
		ClientImpl.getInstance().connectToHost(hostURI);
	}

	public static void disconnectFromHost() {
		ClientImpl.getInstance().disconnectFromHost();
	}

}
