package org.javassonne.model;

public interface TileContainerIterator {

	//Returns current Tile
	public Tile current();
	
	//Advances up
	public void up();
	
	//Advances right
	public void right();
	
	//Advances left
	public void left();
	
	//Advances down
	public void down();
}
