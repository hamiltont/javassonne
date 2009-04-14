/**
 * Javassonne 
 *  http://code.google.com/p/javassonne/
 * 
 * @author [Add Name Here]
 * @date Apr 14, 2009
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

import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.TimerTask;

import javax.jmdns.ServiceInfo;

import org.javassonne.logger.LogSender;

public class ClientStarter extends TimerTask {

	private ClientImpl clientToStart_;

	public ClientStarter(ClientImpl c) {
		clientToStart_ = c;
	}

	public void run() {

		// Create the RMI service
		ServiceInfo service = null;
		try {
			service = RemotingUtils.exportRMIService(clientToStart_,
					RemoteClient.class, RemoteClient.SERVICENAME + "_"
							+ clientToStart_.getName());
		} catch (RemoteException e) {
			String err = "A RemoteException occurred while creating the RMI";
			err += "\n" + e.getMessage();
			err += "\nStack Trace: \n";
			for (int i = 0; i < e.getStackTrace().length; i++)
				err += e.getStackTrace()[i] + "\n";

			LogSender.sendErr(err);
		} catch (UnknownHostException e) {
			String err = "A UnknownHostException occurred while creating the RMI";
			err += "\n" + e.getMessage();
			err += "\nStack Trace: \n";
			for (int i = 0; i < e.getStackTrace().length; i++)
				err += e.getStackTrace()[i] + "\n";

			LogSender.sendErr(err);
		}

		// TODO rather than using the ServiceInfo wrapper provided, we
		// should probably just do this manually. the call to
		// service_.getHostAddr will fail b/c we have not registered this
		// service with JmDNS, which we have no real desire/need to do
		clientToStart_.start("rmi://" + RemotingUtils.LOCAL_HOST + ":"
				+ service.getPort() + "/" + service.getName());

		// Ensure this never runs again
		cancel();
	}
}
