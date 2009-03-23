/**
 * Javassonne 
 *  http://code.google.com/p/javassonne/
 * 
 * @author [Add Name Here]
 * @date Mar 23, 2009
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

package org.javassonne.messaging;

import java.io.Serializable;

import com.thoughtworks.xstream.XStream;

public class NetworkSafeNotification implements Serializable {

	private static final long serialVersionUID = -5960703285863904273L;
	private String identifier_;
	private String serializedArgument_;
	
	public NetworkSafeNotification(Notification n) {
		identifier_ = n.identifier();
		serializedArgument_ = new XStream().toXML(n.argument());
	}
	
	public String identifier() {
		return identifier_;
	}
	
	public Object argument() {
		return new XStream().fromXML(serializedArgument_);
	}
}
