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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class NotificationCallback {

	private Object target_ = null;
	private Method method_ = null;

	NotificationCallback(Object target, Method method) {
		target_ = target;
		method_ = method;
	}

	public void fire(Notification n) {
		try {
			if (method_.getParameterTypes().length == 0)
				method_.invoke(target_);
			else
				method_.invoke(target_, n);
		} catch (IllegalArgumentException e) {
			String err = String.format("An exception occurred while sending "
					+ "the notification %s to an observer of %s", n
					.identifier(), target_.getClass().toString());
			err += "\nThe callback function called does not take a Notification parameter";
			NotificationManager.getInstance().sendNotification(
					Notification.LOG_ERROR, err);
		} catch (IllegalAccessException e) {

			String err = String.format("An exception occurred while sending "
					+ "the notification %s to an observer of %s", n
					.identifier(), target_.getClass().toString());
			err += "\nNotificationCallback cannot access that function";
			NotificationManager.getInstance().sendNotification(
					Notification.LOG_ERROR, err);
		} catch (InvocationTargetException e) {
			String err = String.format("An exception occurred while sending "
					+ "the notification %s to an observer of %s", n
					.identifier(), target_.getClass().toString());
			err += "\nAn exception of type '" + e.getTargetException()
					+ "' was thrown in the callback function.\n"
					+ "Stack Trace: \n";
			for (int i = 0; i < e.getTargetException().getStackTrace().length; i++) {
				err += e.getTargetException().getStackTrace()[i] + "\n";
			}

			NotificationManager.getInstance().sendNotification(
					Notification.LOG_ERROR, err);
		}

	}

	public boolean equals(Object obj) {
		if (obj.getClass() != NotificationCallback.class)
			return false;
		NotificationCallback other = (NotificationCallback) obj;

		return ((other.target_.equals(target_)) && (other.method_
				.equals(method_)));
	}

	public Object target() {
		return target_;
	}

	public Method method() {
		return method_;
	}

}