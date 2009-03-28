/*******************************************************************************
 * Copyright (c) 2007 Jules White. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Common Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors: Jules White - initial API and implementation
 * 				 Hamilton Turner - refactoring to fit Javassonne better
 ******************************************************************************/
package org.javassonne.networking.impl;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.server.RMISocketFactory;
import java.util.HashMap;
import java.util.Map;

import javax.jmdns.ServiceInfo;

import org.springframework.remoting.rmi.RmiProxyFactoryBean;
import org.springframework.remoting.rmi.RmiServiceExporter;

public class RemotingUtils {

	public static String LOCAL_HOST;

	static {
		try {
			LOCAL_HOST = InetAddress.getLocalHost().getHostAddress();
		} catch (Exception e) {
		}
	}

	private static Map<String, RmiServiceExporter> serviceMap_ = new HashMap<String, RmiServiceExporter>();

	public static final int DEFAULT_PORT = 5099;

	/**
	 * This method takes an object and exposes it as a remove RMI service. The
	 * class that is passed should be the interface that you want the service
	 * exposed as. The name will be used to create the url for the service. e.g.
	 * if you provide the name "foo", and your ip address is 123.45.67.89, the
	 * following url will access the service: rmi://123.45.67.89:1099/foo
	 * 
	 * @param svc
	 *            - the object that is to be exposed as a remote RMI service
	 * @param svcinterface
	 *            - the interface on the object to expose
	 * @param name
	 *            - the name of the service for the url
	 * @return - the url of the service (can be passed to others to connect to)
	 * 
	 * @throws RemoteException
	 * @throws UnknownHostException
	 */
	public static ServiceInfo exportRMIService(Object svc, Class svcinterface,
			String name) throws RemoteException, UnknownHostException {
		// TODO - this will fail if the default RMI port is being
		// used. we need to try and implement a smart export
		return exportRMIService(svc, svcinterface, DEFAULT_PORT, name);
	}

	/**
	 * This method takes an object and exposes it as a remove RMI service. The
	 * class that is passed should be the interface that you want the service
	 * exposed as. The name and port will be used to create the url for the
	 * service. e.g. if you provide the name "foo", port 5555, and your ip
	 * address is 123.45.67.89, the following url will access the service:
	 * rmi://123.45.67.89:5555/foo
	 * 
	 * @param svc
	 *            - the object that is to be exposed as a remote RMI service
	 * @param svcinterface
	 *            - the interface on the object to expose
	 * @param name
	 *            - the name of the service for the url
	 * @return - the url of the service (can be passed to others to connect to)
	 * 
	 * @throws RemoteException
	 * @throws UnknownHostException
	 */
	public static synchronized ServiceInfo exportRMIService(Object svc, Class svcinterface,
			int port, String name) throws RemoteException, UnknownHostException {
		RmiServiceExporter exporter = new RmiServiceExporter();
		exporter.setRegistryPort(port);
		exporter.setServiceInterface(svcinterface);
		exporter.setService(svc);
		exporter.setServiceName(name);
		exporter.afterPropertiesSet();
		exporter.prepare();
		serviceMap_.put(name, exporter);

		ServiceInfo info = ServiceInfo.create("_rmi._tcp.local.", name, port,
				"path=index.html");
		
		// return "rmi://"+LOCAL_HOST+":"+port+"/"+name;
		return info;
	}

	/**
	 * Stops the specified named service.
	 * 
	 * @param name
	 *            - the name of the service to stop
	 * @throws RemoteException
	 */
	public static void shutdownService(String name) throws RemoteException {
		RmiServiceExporter exporter = serviceMap_.get(name);
		if (exporter != null) {
			exporter.destroy();
		}
	}

	/**
	 * This method connects to a remote object exposed through RMI and returns a
	 * handle to it. The object can be used just like any ordinary Java object.
	 * The uri should be the uri that the remote service is exposed under. The
	 * svcinterface should be the interface that you expect the object to have.
	 * 
	 * 
	 * @param uri
	 * @param svcinterface
	 * @return
	 */
	public static Object lookupRMIService(String uri, Class svcinterface) {
		RmiProxyFactoryBean proxy = new RmiProxyFactoryBean();
		proxy.setServiceUrl(uri);
		proxy.setServiceInterface(svcinterface);
		proxy.afterPropertiesSet();
		return proxy.getObject();
	}

	/**
	 * Same as above but allows for the host and port to be specified
	 * separately.
	 * 
	 * @param host
	 * @param port
	 * @param svcname
	 * @param svcinterface
	 * @return
	 */
	public static Object lookupRMIService(String host, int port,
			String svcname, Class svcinterface) {
		String uri = "rmi://" + host + ":" + port + "/" + svcname;
		return lookupRMIService(uri, svcinterface);
	}

	/**
	 * Same as above but uses a default port of 1099
	 * 
	 * 
	 * @param host
	 * @param port
	 * @param svcname
	 * @param svcinterface
	 * @return
	 */
	public static Object lookupRMIService(String host, String svcname,
			Class svcinterface) {
		return lookupRMIService(host, DEFAULT_PORT, svcname, svcinterface);
	}

	/**
	 * Same as above but defaults to localhost and uses a default port of 1099
	 * 
	 * 
	 * @param host
	 * @param port
	 * @param svcname
	 * @param svcinterface
	 * @return
	 */
	public static Object lookupLocalRMIService(String svcname,
			Class svcinterface) {
		return lookupRMIService(LOCAL_HOST, DEFAULT_PORT, svcname, svcinterface);
	}
}
