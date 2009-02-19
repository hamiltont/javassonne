package org.javassonne.networking;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;

import net.jxta.document.AdvertisementFactory;
import net.jxta.endpoint.Message;
import net.jxta.endpoint.StringMessageElement;
import net.jxta.id.IDFactory;
import net.jxta.pipe.OutputPipe;
import net.jxta.pipe.OutputPipeEvent;
import net.jxta.pipe.OutputPipeListener;
import net.jxta.pipe.PipeID;
import net.jxta.pipe.PipeService;
import net.jxta.platform.NetworkManager;
import net.jxta.protocol.PipeAdvertisement;

/**
 * This tutorial illustrates the use of JXTA Pipes to exchange messages.
 * <p/>
 * This peer is the pipe "client". It opens the pipe for output and when it
 * resolves (finds a listening peer) it sends a message to the "server".
 */
public class PipeClient implements OutputPipeListener {
	/**
	 * The tutorial message name space
	 */
	public final static String MESSAGE_NAME_SPACE = "PipeTutorial";
	private boolean waitForRendezvous = false;
	private PipeService pipeService;
	private PipeAdvertisement pipeAdv;
	private OutputPipe outputPipe;
	private final Object lock = new String("lock");
	/**
	 * Network is JXTA platform wrapper used to configure, start, and stop the
	 * the JXTA platform
	 */
	private NetworkManager manager;
	/**
	 * A pre-baked PipeID string
	 */
	public final static String PIPEIDSTR = "urn:jxta:uuid-59616261646162614E50472050325033C0C1DE89719B456691A596B983BA0E1004";

	/**
	 * Create this instance and starts the JXTA platform
	 * 
	 * @param waitForRendezvous
	 *            indicates whether to wait for a rendezvous connection
	 */
	public PipeClient(boolean waitForRendezvous) {
		this.waitForRendezvous = waitForRendezvous;
		try {
			manager = new net.jxta.platform.NetworkManager(
					NetworkManager.ConfigMode.ADHOC, "PipeClient", new File(
							new File(".cache"), "PipeClient").toURI());
			manager.startNetwork();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
		// get the pipe service, and discovery
		pipeService = manager.getNetPeerGroup().getPipeService();
		// create the pipe advertisement
		//pipeAdv = getPipeAdvertisement();
	}

	/**
	 * main
	 * 
	 * @param args
	 *            command line arguments
	 */
	public static void main(String args[]) {
		// by setting this property it will trigger a wait for a rendezvous
		// connection prior to attempting to resolve the pipe
		String value = System.getProperty("RDVWAIT", "false");
		boolean waitForRendezvous = Boolean.valueOf(value);
		PipeClient client = new PipeClient(waitForRendezvous);
		client.start();
	}

	
	/**
	 * the thread which creates (resolves) the output pipe and sends a message
	 * once it's resolved
	 */
	public synchronized void start() {
		try {
			if (waitForRendezvous) {
				System.out.println("Waiting for Rendezvous Connection");
				// wait indefinitely until connected to a rendezvous
				manager.waitForRendezvousConnection(0);
				System.out
						.println("Connected to Rendezvous, attempting to create a OutputPipe");
			}
			// issue a pipe resolution asynchronously. outputPipeEvent() is
			// called
			// once the pipe has resolved
			pipeService.createOutputPipe(pipeAdv, this);
			try {
				synchronized (lock) {
					lock.wait();
				}
			} catch (InterruptedException e) {
				System.out.println("Thread interrupted");
			}
		} catch (IOException e) {
			System.out.println("OutputPipe creation failure");
			e.printStackTrace();
			System.exit(-1);
		}
	}

	/**
	 * by implementing OutputPipeListener we must define this method which is
	 * called when the output pipe is created
	 * 
	 * @param event
	 *            event object from which to get output pipe object
	 */
	public void outputPipeEvent(OutputPipeEvent event) {
		System.out.println("Received the output pipe resolution event");
		// get the output pipe object
		outputPipe = event.getOutputPipe();
		Message msg;
		try {
			System.out.println("Sending message");
			// create the message
			msg = new Message();
			Date date = new Date(System.currentTimeMillis());
			// add a string message element with the current date
			StringMessageElement sme = new StringMessageElement(
					MESSAGE_NAME_SPACE, date.toString(), null);
			msg.addMessageElement(null, sme);
			// send the message
			outputPipe.send(msg);
			System.out.println("message sent");
		} catch (IOException e) {
			System.out.println("failed to send message");
			e.printStackTrace();
			System.exit(-1);
		}
		stop();
	}

	/**
	 * Closes the output pipe and stops the platform
	 */
	public void stop() {
		// Close the output pipe
		outputPipe.close();
		// Stop JXTA
		manager.stopNetwork();
		synchronized (lock) {
			// done.
			lock.notify();
		}
	}
}