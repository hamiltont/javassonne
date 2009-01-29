/**
 * Javassonne 
 *  http://code.google.com/p/javassonne/
 * 
 * @author [Add Name Here]
 * @date Jan 28, 2009
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

public class Notification {

	public static final String LOG_WARNING = "NotificationLogWarning";
	public static final String LOG_ERROR = "NotificationLogError";
	
	private String identifier_;
	private Object argument_;
	
	public Notification(String identifier, Object arg) {
		identifier_ = identifier;
		argument_ = arg;
	}
	
	public String identifier()
	{
		return identifier_;
	}
	
	public Object argument()
	{
		return argument_;
	}

}
