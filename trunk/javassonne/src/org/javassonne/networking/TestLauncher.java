/**
 * Javassonne 
 *  http://code.google.com/p/javassonne/
 * 
 * @author Hamilton Turner
 * @date Mar 6, 2009
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


/** 
 * A bootstrapper used to very simply test the networking
 * 
 * @author Hamilton Turner
 */
public class TestLauncher {

	
	public static void main(String[] args) {
		
		HostImpl h = new HostImpl();
		String localHostURI = h.getURI();
		h.startAcceptingConnections();
		
		ClientImpl cl = new ClientImpl(localHostURI, "a");
		cl.connectToLocalHost();
		cl.sendMessageToHost("hello from a");
		
		ClientImpl clb = new ClientImpl(localHostURI, "b");
		clb.connectToLocalHost();
		clb.sendMessageToHost("hello from b");
	}
}
