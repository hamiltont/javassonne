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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.javassonne.logger.LogSender;

public class ThreadPool {
	private static ThreadPool instance_ = null;
	private boolean started_;
	private boolean killOwner_;
	private ExecutorService executor_;

	private ThreadPool() {
		started_ = false;
		killOwner_ = false;

		// Start the executor in it's own thread
		new Owner("Thread Pool Owner");

		while (started_ == false) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	private void _shutdown() {
		LogSender.sendInfo("Asking owner to die with thread "
				+ Thread.currentThread().getName());
		killOwner_ = true;
		
		// Sleep long enough for the Owner to wake and notifyAll
		try {
			Thread.sleep(101);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static void shutdown() {
		getInstance()._shutdown();
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
		synchronized (executor_) {
			executor_.execute(r);
		}
	}

	public class Owner extends Thread {

		public Owner(String name) {
			super(name);
			start();
		}

		public void run() {
			LogSender.sendInfo("Starting thread pool with thread "
					+ Thread.currentThread().getName());
			
			executor_ = Executors.newCachedThreadPool();
			
			started_ = true;

			// Wakeup to check the flag
			while (killOwner_ == false) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			LogSender.sendInfo("noticed flag is true with Thread "
					+ Thread.currentThread().getName());
			// Kill the thread
			shutdown();
		}

		public void shutdown() {
			LogSender
					.sendInfo("ThreadPool - notifying all of shutdown from Thread "
							+ Thread.currentThread().getName());
			
			// Put the executor in a synchronized block to guarantee that the
			// current thread is the owner
			synchronized (executor_) {
				executor_.shutdownNow();
			}
			LogSender
					.sendInfo("ThreadPool - done notifying all of shutdown from Thread "
							+ Thread.currentThread().getName());
		}
	}
}
