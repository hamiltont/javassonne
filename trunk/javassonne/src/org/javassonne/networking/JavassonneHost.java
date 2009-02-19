/**
 * Javassonne 
 *  http://code.google.com/p/javassonne/
 * 
 * @author Brian Salisbury, Hamilton Turner
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
package org.javassonne.networking;

import java.io.File;
import java.io.IOException;
import java.net.SocketException;

import net.jxta.endpoint.Message;
import net.jxta.endpoint.MessageElement;
import net.jxta.id.ID;
import net.jxta.peergroup.PeerGroup;
import net.jxta.pipe.PipeService;
import net.jxta.platform.NetworkManager;
import net.jxta.protocol.PipeAdvertisement;
import net.jxta.util.JxtaBiDiPipe;
import net.jxta.util.JxtaServerPipe;


public class JavassonneHost {
	public boolean activelyPlaying_;
	public ID[] clientIds_; // the JXTA unique ids
	private NetworkManager manager_;
	private PeerGroup peerGroup_;
	private PipeService pipeService_;
	private JxtaServerPipe pipeServer_; 
	private PipeAdvertisement pipeAdv_;
	private static String TAG = "element";
	
	public static void main(String[] args) {
		JavassonneHost jh = new JavassonneHost();
		jh.start();
	}
	
	public JavassonneHost() {
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
			pipeServer_ = new JxtaServerPipe(peerGroup_,pipeAdv_);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		try {
			// Leave the communication channel open
			pipeServer_.setPipeTimeout(0);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void start(){
		JxtaBiDiPipe biDiPipe = null;
		try {
			biDiPipe = pipeServer_.accept();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Message m = null;
		try {
			m = biDiPipe.getMessage(0);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		MessageElement me = m.getMessageElement(TAG);
		System.out.println("Host received: " + me.toString());
	}

}