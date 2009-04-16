/**
 * Javassonne 
 *  http://code.google.com/p/javassonne/
 * 
 * @author [Add Name Here]
 * @date Mar 27, 2009
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

package org.javassonne.networking.impl;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.javassonne.logger.LogSender;
import org.javassonne.messaging.Notification;
import org.javassonne.messaging.NotificationManager;

public class ThreadPool {
	private Executor executor_;
	private static ThreadPool instance_ = null;

	private ThreadPool() {
		executor_ = Executors.newCachedThreadPool();
		NotificationManager.getInstance().addObserver(Notification.QUIT, this,
				"shutdown");
	}

	public void shutdown(Notification n) {
		LogSender.sendInfo("ThreadPool - notifying all of shutdown");
		executor_.notifyAll();
	}

	private static ThreadPool getInstance() {
		if (instance_ == null)
			instance_ = new ThreadPool();
		return instance_;
	}

	public static void execute(Runnable r) {
		getInstance()._execute(r);
	}

	private void _execute(Runnable r) {
		executor_.execute(r);
	}
}
