/**
 * Javassonne 
 *  http://code.google.com/p/javassonne/
 * 
 * @author [Add Name Here]
 * @date Feb 19, 2009
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

import java.net.URI;
import java.net.URISyntaxException;

import net.jxta.document.AdvertisementFactory;
import net.jxta.id.IDFactory;
import net.jxta.pipe.PipeID;
import net.jxta.pipe.PipeService;
import net.jxta.protocol.PipeAdvertisement;

public class PipeAdvertisementCreator {
	private static final String PIPEIDSTR = "urn:jxta:uuid-59616261646162614E50472050325033C0C1DE89719B456691A596B983BA0E1004";
	
	/**
	 * Creates the pipe advertisement pipe ID
	 * 
	 * @return the predefined Pipe Advertisement
	 */
	public static PipeAdvertisement getPipeAdvertisement() {
		PipeID pipeID = null;
		try {
			pipeID = (PipeID) IDFactory.fromURI(new URI(PIPEIDSTR));
		} catch (URISyntaxException use) {
			use.printStackTrace();
		}
		PipeAdvertisement advertisement = (PipeAdvertisement) AdvertisementFactory
				.newAdvertisement(PipeAdvertisement.getAdvertisementType());
		advertisement.setPipeID(pipeID);
		advertisement.setType(PipeService.UnicastType);
		advertisement.setName("JavassonnePipe");
		return advertisement;
	}

}
