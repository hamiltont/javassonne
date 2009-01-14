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
