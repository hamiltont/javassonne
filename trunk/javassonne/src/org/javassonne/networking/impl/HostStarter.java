/**
 * Javassonne 
 *  http://code.google.com/p/javassonne/
 * 
 * @author [Add Name Here]
 * @date Mar 24, 2009
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
import java.rmi.RemoteException;
import java.util.TimerTask;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;

import org.javassonne.messaging.Notification;
import org.javassonne.messaging.NotificationManager;

public class HostStarter extends TimerTask {

	private boolean called_ = false;

	public void run() {
		// Prevent this from accidentally being called twice
		if (called_)
			return;
		called_ = true;
		LocalHostImpl.getInstance().isLocalHostStarted_ = true;

		// Create the RMI service
		ServiceInfo info = createRMI();

		// For some reason the info returned has no
		// local addr, so we need to provide one
		String local_host = null;
		try {
			local_host = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		LocalHostImpl.getInstance().URI_ = "rmi://" + local_host + ":" + info.getPort()
				+ "/" + info.getName();

		// As long as we detect that we are a duplicate,
		// try to force the old original to close and
		// give us our name back
		while (LocalHostImpl.getInstance().URI_.matches(".+\\(\\d+\\)") == true) {

			// Shutdown any previous service
			try {
				RemotingUtils.shutdownService(RemoteHost.SERVICENAME + "_"
						+ LocalHostImpl.getInstance().rmiSafeName_);
			} catch (RemoteException e) {
				String err = "HostStarter in LocalHostImpl: A RemoteException occurred while starting";
				err += "\n" + e.getMessage();
				err += "\nStack Trace: \n";
				for (int i = 0; i < e.getStackTrace().length; i++)
					err += e.getStackTrace()[i] + "\n";

				NotificationManager.getInstance().sendNotification(
						Notification.LOG_ERROR, err);
			}

			// Try to re-create our RMI
			info = createRMI();
			LocalHostImpl.getInstance().URI_ = "rmi://" + info.getHostAddress() + ":" + info.getPort()
					+ "/" + info.getName();
		}

		// Broadcast the created service
		JmDNS jmdns_ = JmDNSSingleton.getJmDNS();
		try {
			jmdns_.registerService(info);
		} catch (IOException e) {
			String err = "An IOException occurred when registering a service with jmdns";

			err += "\n" + e.getMessage();
			err += "\nStack Trace: \n";
			for (int i = 0; i < e.getStackTrace().length; i++)
				err += e.getStackTrace()[i] + "\n";

			NotificationManager.getInstance().sendNotification(
					Notification.LOG_ERROR, err);
		}

		String info2 = "HostStarter: Clients can connect to: " + LocalHostImpl.getInstance().URI_;

		NotificationManager.getInstance().sendNotification(
				Notification.LOG_INFO, info2);

		// Cancel this timer task
		cancel();
	}

	private ServiceInfo createRMI() {
		ServiceInfo info = null;
		try {
			info = RemotingUtils.exportRMIService(LocalHostImpl
					.getInstance(), RemoteHost.class,
					RemoteHost.SERVICENAME + "_" + LocalHostImpl.getInstance().rmiSafeName_);
		} catch (RemoteException e) {
			String err = "A RemoteException occurred when exporting host RMI";
			err += "\n" + e.getMessage();
			err += "\nStack Trace: \n";
			for (int i = 0; i < e.getStackTrace().length; i++)
				err += e.getStackTrace()[i] + "\n";

			NotificationManager.getInstance().sendNotification(
					Notification.LOG_ERROR, err);

		} catch (UnknownHostException e) {
			String err = "An UnknownHostException occurred when exporting host RMI";
			err += "\n" + e.getMessage();
			err += "\nStack Trace: \n";
			for (int i = 0; i < e.getStackTrace().length; i++)
				err += e.getStackTrace()[i] + "\n";

			NotificationManager.getInstance().sendNotification(
					Notification.LOG_ERROR, err);
		} catch (Exception e) {
			String err = "Something bad happened internally in RMI";
			err += "\n" + e.getMessage();
			err += "\nStack Trace: \n";
			for (int i = 0; i < e.getStackTrace().length; i++)
				err += e.getStackTrace()[i] + "\n";

			NotificationManager.getInstance().sendNotification(
					Notification.LOG_ERROR, err);
		}
		return info;
	}
}
