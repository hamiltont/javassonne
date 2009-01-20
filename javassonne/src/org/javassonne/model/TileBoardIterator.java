/**
 * Javassonne 
 *  http://code.google.com/p/javassonne/
 * 
 * @author Kyle Prete
 * @date Jan 14, 2009
 * 
 * Copyright 2009 Javasonne Team
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


public class TileBoardIterator  {
	
	private TileMapBoard data_;
	private IntPair location_;

	public TileBoardIterator(TileMapBoard tileMapContainer, IntPair intPair ) {
		data_ = tileMapContainer;
		location_ = intPair;
	}

	public Tile current() {
		return data_.getTile(this);
	}

	public void down() {
		location_ = new IntPair(location_.car() + 1, location_.cdr());
		//bounds check?
	}

	public void left() {
		location_ = new IntPair(location_.car(), location_.cdr() - 1);
		//bounds check?
	}

	public void right() {
		location_ = new IntPair(location_.car(), location_.cdr() + 1);
		//bounds check?
	}

	public void up() {
		location_ = new IntPair(location_.car() - 1, location_.cdr());
		//bounds check?
	}

	public IntPair getLocation() {
		return location_;
	}

	public void setLocation(IntPair location) {
		this.location_ = location;
	}

}
