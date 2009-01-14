package org.javassonne.model;

public interface TileContainer {

	//Returns "home" square - the tile the game began with.
	public Tile homeTile();
	
	//Adds tile to the specified TileContainerIterator location
	public void addTile(TileContainerIterator iter, Tile tile);
	
	//Removes tile from the specified TileContainerIterator location
	public Tile removeTile(TileContainerIterator iter);
	
	
}
