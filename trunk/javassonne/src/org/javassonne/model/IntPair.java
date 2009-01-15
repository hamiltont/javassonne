/**
 * Javassonne 
 *  http://code.google.com/p/javassonne/
 * 
 * @author Kyle Prete
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

package org.javassonne.model;

public class IntPair {
	private int data1_;
	private int data2_;

	IntPair(int first, int second) {
		data1_ = first;
		data2_ = second;
	}

	IntPair(IntPair old) {
		data1_ = old.data1_;
		data2_ = old.data2_;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof IntPair))
			return false;
		else {
			IntPair tmp = (IntPair) obj;
			if (this.data1_ == tmp.data1_ && this.data2_ == tmp.data2_)
				return true;
			else
				return false;
		}
	}

	@Override
	public String toString() {
		return String.valueOf(this.data1_) + String.valueOf(this.data2_);
	}

	public int car() {
		return data1_;
	}
	
	public int cdr() {
		return data2_;
	}
/*
	public void car(int data1_) {
		this.data1_ = data1_;
	}

	public void cdr(int data2_) {
		this.data2_ = data2_;
	}
*/
}
