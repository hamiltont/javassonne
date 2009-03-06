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


public class ClientImpl implements Client {
	private boolean connected_ = false;			// Lets this client know if it is
												//   currently connected to a host
	
	
	public void receiveMessageFromHost(String msg) {
		// TODO Auto-generated method stub

	}

	
	public void sendMessageToHost(String msg) {
		// TODO Auto-generated method stub

	}
	
	/**
	 * Attempt to connect to a specified host
	 * @param uri The host to try and connect to
	 */
	public void connectToHost(String uri) {
		// should proabbly verify that host exists, and
		// then safely attempt to connect	
	}

}
