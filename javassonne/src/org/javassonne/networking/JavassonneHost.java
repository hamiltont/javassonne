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

import net.jxta.discovery.DiscoveryEvent;
import net.jxta.discovery.DiscoveryListener;
import net.jxta.discovery.DiscoveryService;
import net.jxta.document.Advertisement;
import net.jxta.document.AdvertisementFactory;
import net.jxta.id.ID;
import net.jxta.id.IDFactory;
import net.jxta.peergroup.PeerGroup;
import net.jxta.peergroup.PeerGroupID;
import net.jxta.pipe.PipeService;
import net.jxta.platform.NetworkManager;
import net.jxta.protocol.DiscoveryResponseMsg;
import net.jxta.protocol.PipeAdvertisement;
import net.jxta.impl.util.BidirectionalPipeService;
import net.jxta.impl.util.*;
import java.io.File;
import java.util.Enumeration;


public class JavassonneHost {
	public boolean activelyPlaying_;
	public ID[] clientIds_; // the JXTA unique ids

	public void setID(){}

	public void setIP(String ip){}

	public void connect(){
	        NetworkManager manager = new NetworkManager(NetworkManager.ConfigMode.EDGE,
	                        "JavassonneHost", new File(new File(".cache"),
	                                "JavassonneHost").toURI());
	          
	        manager.startNetwork();
	        PeerGroup defaultPeerGroup = manager.getNetPeerGroup();
	        BidirectionalPipeService bps = new BidirectionalPipeService(defaultPeerGroup);
	        BidirectionalPipeService.AcceptPipe acceptPipe = bps.bind("hpipe");
	        PipeAdvertisement adv = acceptPipe.getAdvertisement();
	        BidirectionalPipeService.Pipe hpipe = acceptPipe.accept(30 * 1000);
	    }

}