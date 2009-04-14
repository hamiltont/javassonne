/**
 * Javassonne 
 *  http://code.google.com/p/javassonne/
 * 
 * @author Hamilton Turner
 * @date Mar 21, 2009
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

import org.javassonne.networking.impl.HostImpl;
import org.javassonne.ui.GameState.Mode;

/**
 * Lets other developers very easily connect to the Local Host object
 * 
 * This is just a callthru to LocalHostImpl
 * 
 * @author Hamilton Turner
 */
public class LocalHost {

	/**
	 * @ see org.javassonne.networking.impl.RemoteHost
	 */
	public static void addClient(String clientURI) {
		HostImpl.getInstance().addClient(clientURI);
	}

	/**
	 * @ see org.javassonne.networking.impl.RemoteHost
	 */
	public static String getName() {
		return HostImpl.getInstance().getName();
	}

	/**
	 * @ see org.javassonne.networking.impl.RemoteHost
	 */
	public static Mode getStatus() {
		return HostImpl.getInstance().getStatus();
	}

	/**
	 * @ see org.javassonne.networking.impl.RemoteHost
	 */
	public static String getURI() {
		return HostImpl.getInstance().getURI();
	}

	/**
	 * @ see org.javassonne.networking.impl.RemoteHost
	 */
	public static void receiveNotification(String serializedNotification,
			String clientURI) {
		HostImpl.getInstance().receiveNotificationFromClient(
				serializedNotification, clientURI);
	}
}
