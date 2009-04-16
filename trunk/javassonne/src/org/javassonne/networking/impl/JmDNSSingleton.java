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
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;
import javax.jmdns.ServiceTypeListener;

import org.javassonne.logger.LogSender;

public class JmDNSSingleton {

	private static JmDNS jmdns_ = null;
	private static jmdnsCallThru callThru_ = null;

	private static boolean DEBUG = true;

	private JmDNSSingleton() {
	}

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
		callThru_ = new jmdnsCallThru();
		return callThru_;
	}

	private static class jmdnsCallThru extends JmDNS {

		public void addServiceListener(String arg0, ServiceListener arg1) {
			if (DEBUG)
				LogSender.sendInfoToFile("JmDNS CallThru - Thread "
						+ Thread.currentThread().getName()
						+ " accessing addServiceListener");
			jmdns_.addServiceListener(arg0, arg1);
			if (DEBUG)
				LogSender.sendInfoToFile("JmDNS CallThru - Thread "
						+ Thread.currentThread().getName()
						+ " leaving addServiceListener");
		}

		public void addServiceTypeListener(ServiceTypeListener arg0)
				throws IOException {
			if (DEBUG)
				LogSender.sendInfoToFile("JmDNS CallThru - Thread "
						+ Thread.currentThread().getName()
						+ " accessing addServiceTypeListener");
			jmdns_.addServiceTypeListener(arg0);
			if (DEBUG)
				LogSender.sendInfoToFile("JmDNS CallThru - Thread "
						+ Thread.currentThread().getName()
						+ " leaving addServiceTypeListener");
		}

		public void close() {
			if (DEBUG)
				LogSender
						.sendInfoToFile("JmDNS CallThru - Thread "
								+ Thread.currentThread().getName()
								+ " accessing close");
			jmdns_.close();
			if (DEBUG)
				LogSender.sendInfoToFile("JmDNS CallThru - Thread "
						+ Thread.currentThread().getName() + " leaving close");
		}

		public String getHostName() {
			if (DEBUG)
				LogSender.sendInfoToFile("JmDNS CallThru - Thread "
						+ Thread.currentThread().getName()
						+ " accessing getHostName");
			String s = jmdns_.getHostName();
			if (DEBUG)
				LogSender.sendInfoToFile("JmDNS CallThru - Thread "
						+ Thread.currentThread().getName()
						+ " leaving getHostName");
			return s;
		}

		public InetAddress getInterface() throws IOException {
			if (DEBUG)
				LogSender.sendInfoToFile("JmDNS CallThru - Thread "
						+ Thread.currentThread().getName()
						+ " accessing getInterface");

			InetAddress a = jmdns_.getInterface();

			if (DEBUG)
				LogSender.sendInfoToFile("JmDNS CallThru - Thread "
						+ Thread.currentThread().getName()
						+ " leaving getInterface");
			return a;
		}

		public ServiceInfo getServiceInfo(String arg0, String arg1) {
			if (DEBUG)
				LogSender.sendInfoToFile("JmDNS CallThru - Thread "
						+ Thread.currentThread().getName()
						+ " accessing getServiceInfo");
			ServiceInfo a = jmdns_.getServiceInfo(arg0, arg1);
			if (DEBUG)
				LogSender.sendInfoToFile("JmDNS CallThru - Thread "
						+ Thread.currentThread().getName()
						+ " leaving getServiceInfo");
			return a;
		}

		public ServiceInfo getServiceInfo(String arg0, String arg1, int arg2) {
			if (DEBUG)
				LogSender.sendInfoToFile("JmDNS CallThru - Thread "
						+ Thread.currentThread().getName()
						+ " accessing getServiceInfo (with timout)");
			ServiceInfo a = jmdns_.getServiceInfo(arg0, arg1, arg2);
			if (DEBUG)
				LogSender.sendInfoToFile("JmDNS CallThru - Thread "
						+ Thread.currentThread().getName()
						+ " leaving getServiceInfo (with timeout)");
			return a;
		}

		public ServiceInfo[] list(String arg0) {
			if (DEBUG)
				LogSender.sendInfoToFile("JmDNS CallThru - Thread "
						+ Thread.currentThread().getName() + " accessing list");
			ServiceInfo[] a = jmdns_.list(arg0);
			if (DEBUG)
				LogSender.sendInfoToFile("JmDNS CallThru - Thread "
						+ Thread.currentThread().getName() + " leaving list");
			return a;
		}

		public void printServices() {
			if (DEBUG)
				LogSender.sendInfoToFile("JmDNS CallThru - Thread "
						+ Thread.currentThread().getName()
						+ " accessing printServices");
			jmdns_.printServices();
			if (DEBUG)
				LogSender.sendInfoToFile("JmDNS CallThru - Thread "
						+ Thread.currentThread().getName()
						+ " leaving printServices");
		}

		public void registerService(ServiceInfo arg0) throws IOException {
			if (DEBUG)
				LogSender.sendInfoToFile("JmDNS CallThru - Thread "
						+ Thread.currentThread().getName()
						+ " accessing registerService");
			jmdns_.registerService(arg0);
			if (DEBUG)
				LogSender.sendInfoToFile("JmDNS CallThru - Thread "
						+ Thread.currentThread().getName()
						+ " leaving registerService");
		}

		public void registerServiceType(String arg0) {
			if (DEBUG)
				LogSender.sendInfoToFile("JmDNS CallThru - Thread "
						+ Thread.currentThread().getName()
						+ " accessing registerServiceType");
			jmdns_.registerServiceType(arg0);

			if (DEBUG)
				LogSender.sendInfoToFile("JmDNS CallThru - Thread "
						+ Thread.currentThread().getName()
						+ " leaving registerServiceType");
		}

		public void removeServiceListener(String arg0, ServiceListener arg1) {
			if (DEBUG)
				LogSender.sendInfoToFile("JmDNS CallThru - Thread "
						+ Thread.currentThread().getName()
						+ " accessing removeServiceListener");
			jmdns_.removeServiceListener(arg0, arg1);
			if (DEBUG)
				LogSender.sendInfoToFile("JmDNS CallThru - Thread "
						+ Thread.currentThread().getName()
						+ " leaving removeServiceListener");
		}

		public void removeServiceTypeListener(ServiceTypeListener arg0) {
			if (DEBUG)
				LogSender.sendInfoToFile("JmDNS CallThru - Thread "
						+ Thread.currentThread().getName()
						+ " accessing removeServiceTypeListener");
			jmdns_.removeServiceTypeListener(arg0);
			if (DEBUG)
				LogSender.sendInfoToFile("JmDNS CallThru - Thread "
						+ Thread.currentThread().getName()
						+ " leaving removeServiceTypeListener");
		}

		public void requestServiceInfo(String arg0, String arg1) {
			if (DEBUG)
				LogSender.sendInfoToFile("JmDNS CallThru - Thread "
						+ Thread.currentThread().getName()
						+ " accessing requestServiceInfo");
			jmdns_.requestServiceInfo(arg0, arg1);
			if (DEBUG)
				LogSender.sendInfoToFile("JmDNS CallThru - Thread "
						+ Thread.currentThread().getName()
						+ " leaving requestServiceInfo");
		}

		public void requestServiceInfo(String arg0, String arg1, int arg2) {
			if (DEBUG)
				LogSender.sendInfoToFile("JmDNS CallThru - Thread "
						+ Thread.currentThread().getName()
						+ " accessing requestServiceInfo (with timeout)");
			jmdns_.requestServiceInfo(arg0, arg1, arg2);
			if (DEBUG)
				LogSender.sendInfoToFile("JmDNS CallThru - Thread "
						+ Thread.currentThread().getName()
						+ " leaving requestServiceInfo (with timeout)");
		}

		public void unregisterAllServices() {
			if (DEBUG)
				LogSender.sendInfoToFile("JmDNS CallThru - Thread "
						+ Thread.currentThread().getName()
						+ " accessing unregisterAllServices");
			jmdns_.unregisterAllServices();
			if (DEBUG)
				LogSender.sendInfoToFile("JmDNS CallThru - Thread "
						+ Thread.currentThread().getName()
						+ " leaving unregisterAllServices");
		}

		public void unregisterService(ServiceInfo arg0) {
			if (DEBUG)
				LogSender.sendInfoToFile("JmDNS CallThru - Thread "
						+ Thread.currentThread().getName()
						+ " accessing unregisterService");
			jmdns_.unregisterService(arg0);
			if (DEBUG)
				LogSender.sendInfoToFile("JmDNS CallThru - Thread "
						+ Thread.currentThread().getName()
						+ " leaving unregisterService");
		}

	}
}
