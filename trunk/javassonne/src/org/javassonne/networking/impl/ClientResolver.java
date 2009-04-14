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

import org.javassonne.logger.LogSender;
import org.springframework.remoting.RemoteLookupFailureException;

public class ClientResolver {

	public static RemoteClient attemptToResolveClient(String clientURI) {
		RemoteClient rc = null;
		try {
			rc = (RemoteClient) RemotingUtils.lookupRMIService(clientURI,
					RemoteClient.class);
		} catch (RemoteLookupFailureException e) {

			LogSender
					.sendInfo("ClientResolver could not resolve client at uri: "
							+ clientURI);
			return null;

		} catch (RuntimeException e) {
			String err = "LocalHostImpl - A RuntimeException occurred while adding "
					+ "a client at URI: " + clientURI;
			err += "\n" + e.getMessage();
			err += "\nStack Trace: \n";
			for (int i = 0; i < e.getStackTrace().length; i++)
				err += e.getStackTrace()[i] + "\n";

			LogSender.sendErr(err);
			
			return null;
		}
		return rc;
	}

}
