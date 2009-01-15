/**
 * Javasonne 
 *  http://code.google.com/p/javassonne/
 * 
 * @author David Leinweber
 * @date Jan 14, 2009
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

package org.javassonne.ui;

public class BasicWindowModel {

	int maxTiles_ = 100;
	int myTiles_[];
	int curPos_;

	public BasicWindowModel() {
		myTiles_ = new int[maxTiles_];
		curPos_ = 0;

		for (int i = 0; i < maxTiles_; i++) {
			myTiles_[i] = i;
		}
	}

	public void next() {
		curPos_++;
	}

	public boolean isEmpty() {
		return curPos_ == (myTiles_.length - 1);
	}

	public int read() {
		return myTiles_[curPos_];
	}
}
