/**
 * Javassonne 
 *  http://code.google.com/p/javassonne/
 * 
 * @author Ben Gotow
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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * The NotificationManager is a singleton that allows you to quickly and
 * efficiently pass messages around the app. To send a notification, call
 * sendNotification() and pass a notification "identifier" (EventType).
 * 
 * To receive notifications, you need to register your object as an observer for
 * certain types of events. For instance, the HUDController wants to know when
 * the user clicks the "Rotate Tile" button, so it listens for the identifier
 * "Notification.TILE_ROTATE_LEFT". To add yourself as an observer, call the
 * addObserver function. See details on that below.
 * 
 * @author bengotow
 * 
 */
public class NotificationManager {

	private static NotificationManager instance_ = null;
	private HashMap<String, ArrayList<NotificationCallback>> bindings_ = null;

	// Singleton implementation
	protected NotificationManager() {
		bindings_ = new HashMap<String, ArrayList<NotificationCallback>>();

		this.addObserver(Notification.LOG_ERROR, this, "logError");
		this.addObserver(Notification.LOG_WARNING, this, "logWarning");
	}

	public static NotificationManager getInstance() {
		if (instance_ == null) {
			instance_ = new NotificationManager();
		}
		return instance_;
	}

	public void addObserver(String identifier, Object o, String callbackMethod) {
		Method m = null;

		// first, try to find a matching method that takes a Notification object
		// as a parameter. This is the default and preferred callback signature.
		try {
			m = o.getClass().getMethod(callbackMethod, Notification.class);
		} catch (Exception e) {
		}

		// If we couldn't find the method, try looking for one without any
		// parameters. While this gives the receiver less information about the
		// notification, it shouldn't be illegal.
		if (m == null) {
			try {
				m = o.getClass().getMethod(callbackMethod);
			} catch (Exception e) {
			}
		}

		// If we successfully found a matching method, add it as an observer.
		// Otherwise, send an error notification so we let the developer know
		// their desired callback doesn't exist!
		if (m != null)
			this.addObserver(identifier, o, m);
		else {
			String message = String.format("%s attempted to observe "
					+ "notification '%s', but the callback function '%s' "
					+ " could not be found.", o.getClass().getSimpleName(),
					identifier, callbackMethod);

			NotificationManager.getInstance().sendNotification(
					Notification.LOG_ERROR, message);
		}
	}

	public void addObserver(String identifier, Object o, Method callbackMethod) {
		if (!bindings_.containsKey(identifier))
			bindings_.put(identifier, new ArrayList<NotificationCallback>());

		ArrayList<NotificationCallback> observers = bindings_.get(identifier);
		NotificationCallback c = new NotificationCallback(o, callbackMethod);

		// look to see if the observers list already contains our callback
		if (observers.contains(c)) {
			String msg = String.format("%s wants to observe '%s', "
					+ "but it is already an observer. Ignoring second "
					+ "request to observe.", o.getClass().getSimpleName(),
					identifier);

			this.sendNotification(Notification.LOG_WARNING, msg);
		} else {
			observers.add(c);
		}
	}

	public void removeObserver(Object o) {
		Collection<ArrayList<NotificationCallback>> notifications = bindings_
				.values();
		for (ArrayList<NotificationCallback> callbacks : notifications) {
			for (int ii = callbacks.size() - 1; ii >= 0; ii--) {
				NotificationCallback callback = callbacks.get(ii);
				if (callback.target().equals(o))
					callbacks.remove(ii);
			}
		}
	}

	public void sendNotification(String identifier) {
		this.sendNotification(identifier, null);
	}

	public void sendNotification(String identifier, Object obj) {
		Notification n = new Notification(identifier, obj);
		ArrayList<NotificationCallback> observers = bindings_.get(identifier);

		if (observers != null) {
			// we have to do it this way in case any of the observers remove
			// themselves from the observers list as we're iterating through the 
			// array. The nice clean for loops require that the list is not 
			// modified during iteration.
			for (int ii = observers.size()-1; ii>=0; ii--) {
				observers.get(ii).fire(n);
			}
		} else {
			String msg = String.format("Notification with "
					+ "identifier %s sent to zero observers.", identifier);

			this.sendNotification(Notification.LOG_WARNING, msg);
		}
	}

	public void logError(Notification n) {
		System.err.println(n.argument().toString());
	}

	public void logWarning(Notification n) {
		System.out.println(n.argument().toString());
	}
}
