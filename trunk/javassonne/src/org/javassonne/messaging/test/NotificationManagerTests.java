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

package org.javassonne.messaging.test;

import org.javassonne.messaging.Notification;
import org.javassonne.messaging.NotificationManager;

import junit.framework.TestCase;

public class NotificationManagerTests extends TestCase {

	private static final String NOTIFICATION_NAME = "testNotification1";
	private static final String NOTIFICATION_NAME_NOARG = "testNotification2";
	private static final String NOTIFICATION_NO_OBSERVERS = "testNotification3";
	private static final String FUNCTION_DNE = "FUNCTION_DNE";

	private Boolean success_ = false;
	private Boolean logged_ = false;

	protected void setUp() throws Exception {
		NotificationManager m = NotificationManager.getInstance();

		m.addObserver(NOTIFICATION_NAME, this, "notificationSuccess");
		m.addObserver(NOTIFICATION_NAME_NOARG, this, "notificationSuccessNoArgument");
		m.addObserver(Notification.LOG_ERROR, this, "notificationLogged");
		m.addObserver(Notification.LOG_WARNING, this, "notificationLogged");

		super.setUp();
	}

	public void testSendReceiveNotification() {
		NotificationManager.getInstance().sendNotification(NOTIFICATION_NAME, null);
		assertTrue(success_);
	}

	public void testSendReceiveNotificationNoArgs() {
		NotificationManager.getInstance().sendNotification(NOTIFICATION_NAME);
		assertTrue(success_);
	}

	public void testSendReceiveNotificationWithSimpleCallback() {
		NotificationManager.getInstance().sendNotification(NOTIFICATION_NAME_NOARG, null);
		assertTrue(success_);
	}

	public void testNoObserversWarning()
	{
		NotificationManager.getInstance().sendNotification(NOTIFICATION_NO_OBSERVERS);
		assertFalse(success_);
		assertTrue(logged_);
	}
	
	public void testDoubleObserveWarning()
	{
		NotificationManager.getInstance().addObserver(NOTIFICATION_NAME, this, "notificationSuccess");
		NotificationManager.getInstance().addObserver(NOTIFICATION_NAME, this, "notificationSuccess");
		assertFalse(success_);
		assertTrue(logged_);
	}	
	
	public void testObserveError() {
		NotificationManager.getInstance().addObserver(NOTIFICATION_NAME, this, FUNCTION_DNE);
		assertFalse(success_);
		assertTrue(logged_);
	}

	// Callback Functions
	
	public void notificationSuccess(Notification n) {
		success_ = true;
	}

	public void notificationSuccessNoArgument() {
		success_ = true;
	}

	public void notificationLogged(Notification n) {
		logged_ = true;
	}

	protected void tearDown() throws Exception {
		NotificationManager.getInstance().removeObserver(this);
		super.tearDown();
	}

}
