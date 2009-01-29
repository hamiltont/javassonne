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

		} catch (Exception e) {
			NotificationManager.getInstance().sendNotification(Notification.LOG_ERROR, e.getStackTrace());
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj.getClass() != NotificationCallback.class)
			return false;
		NotificationCallback other = (NotificationCallback) obj;

		return ((other.target_.equals(target_)) && (other.method_.equals(method_)));
	}

	public Object target() {
		return target_;
	}
	
	public Method method() {
		return method_;
	}

}