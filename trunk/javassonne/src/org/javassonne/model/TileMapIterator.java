/**
 * Javasonne 
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


public class TileMapIterator implements TileContainerIterator {
	
	private TileMapContainer data_;
	private IntPair location_;

	public TileMapIterator(TileMapContainer tileMapContainer, IntPair intPair ) {
		data_ = tileMapContainer;
		location_ = intPair;
	}

	public Tile current() {
		return null;
	}

	public void down() {
		// TODO Auto-generated method stub

	}

	public void left() {
		// TODO Auto-generated method stub

	}

	public void right() {
		// TODO Auto-generated method stub

	}

	public void up() {
		// TODO Auto-generated method stub

	}

}
