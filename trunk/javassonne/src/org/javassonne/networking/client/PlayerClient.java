/**
 * Javassonne 
 *  http://code.google.com/p/javassonne/
 * 
 * @author [Add Name Here]
 * @date Feb 18, 2009
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

package org.javassonne.networking.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import net.jxta.document.MimeMediaType;
import net.jxta.document.StructuredDocumentFactory;
import net.jxta.document.XMLDocument;
import net.jxta.endpoint.Message;
import net.jxta.endpoint.StringMessageElement;
import net.jxta.id.ID;
import net.jxta.peergroup.PeerGroup;
import net.jxta.pipe.PipeService;
import net.jxta.platform.NetworkManager;
import net.jxta.protocol.PipeAdvertisement;
import net.jxta.util.JxtaBiDiPipe;

import org.javassonne.messaging.Notification;
import org.javassonne.networking.PipeAdvertisementCreator;

public class PlayerClient {
	private boolean currentlyConnected_;
	private ID hostId_; // the JXTA unique id
	private PeerGroup peerGroup_;
	private NetworkManager manager_;
	private PipeService pipeService_;
	private JxtaBiDiPipe biDiPipe_;
	private static String TAG = "element";
	private PipeAdvertisement pipeAdv_;

	public static void main(String[] args) {
		PlayerClient client = new PlayerClient();
		client.start();
	}

	public PlayerClient() {
		try {
			manager_ = new net.jxta.platform.NetworkManager(
					NetworkManager.ConfigMode.ADHOC, "JavassonnePlayerClient", new File(
							new File(".cache"), "JavassonnePlayerClient").toURI());
			manager_.startNetwork();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}

		peerGroup_ = manager_.getNetPeerGroup();
		pipeService_ = peerGroup_.getPipeService();
		pipeAdv_ = PipeAdvertisementCreator.getPipeAdvertisement();
		try {
			biDiPipe_ = new JxtaBiDiPipe();
			biDiPipe_.connect(peerGroup_, null, pipeAdv_, 180000, null);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void start() {
		Message msg = new Message();
		StringMessageElement sme = new StringMessageElement(TAG, "Hello, javassonneHost", null);
		msg.addMessageElement(sme);
		try {
			biDiPipe_.sendMessage(msg);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String[] getHosts() {
		return null;
	}

	public boolean joinHostGame(String hostName) {
		return false;
	}

	public boolean sendNotification(Notification n) {
		return false;
	}

	// Called whenever a response is received
	public boolean receiveNotification(Notification n) {
		return false;
	}

}
