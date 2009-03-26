/**
 * Javassonne 
 *  http://code.google.com/p/javassonne/
 * 
 * @author [Add Name Here]
 * @date Mar 10, 2009
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

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.jmdns.JmDNS;

public class JmDNSSingleton {

	private static JmDNS jmdns_ = null;

	private JmDNSSingleton() {}

	public static JmDNS getJmDNS() {
		if (jmdns_ == null) {
			try {
				
				try {
					InetAddress localHost = InetAddress.getLocalHost();
					jmdns_ = JmDNS.create(localHost);
				} catch (UnknownHostException e1) {
					// TODO - cleanup
					e1.printStackTrace();
					jmdns_ = JmDNS.create();
				}
				
			} catch (IOException e) {
				System.out
						.println("An IOException occurred when creating jmdns");
				System.out.println(e.getMessage());
				e.printStackTrace();
			}
		}

		return jmdns_;
	}
}
