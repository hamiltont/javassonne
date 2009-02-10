package org.javassonne.networking;

import net.jxta.platform.NetworkManager;
import java.text.MessageFormat;

/**
 * A example of starting and stopping JXTA
 */
public class HelloWorld {
	/**
	 * Main method
	 * 
	 * @param args
	 *            none defined
	 */
	public static void main(String args[]) {
		NetworkManager manager = null;
		try {
			manager = new NetworkManager(NetworkManager.ConfigMode.EDGE,
					"HelloWorld");
			System.out.println("Starting JXTA");
			manager.startNetwork();
			System.out.println("JXTA Started");
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
		System.out.println("Waiting for a rendezvous connection");
		boolean connected = manager.waitForRendezvousConnection(12000);
		System.out.println(MessageFormat.format("Connected :{0}", connected));
		System.out.println("Stopping JXTA");
		manager.stopNetwork();
	}
}
