/**
 * Javassonne 
 *  http://code.google.com/p/javassonne/
 * 
 * @author Ben Gotow
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

import java.util.ArrayList;

public class TileSet {

	private String name_;
	private ArrayList<Tile> tiles_;
	private ArrayList<Integer> tileCounts_;
	
	// Constructor
	
	public TileSet(String name)
	{
		name_ = name;
		tiles_ = new ArrayList<Tile>();
		tileCounts_ = new ArrayList<Integer>();
	}
	
	// Getter and Setter Functionality
	
	public void setName(String name_) {
		this.name_ = name_;
	}
	
	public String getName() {
		return name_;
	}
	
	public int tileCount(){
		return tiles_.size();
	}
	
	// Adding and Removing Tiles
	
	public void addTile(Tile t, int count)
	{
		tiles_.add(t);
		tileCounts_.add(count);
	}
	
	public Tile tileAtIndex(int index)
	{
		return tiles_.get(index);
	}
	
	public int tileCountAtIndex(int index)
	{
		return tileCounts_.get(index);
	}
}
