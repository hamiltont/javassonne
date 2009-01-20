package org.javassonne.model;

public class TileFeature {
	public String name;
	public String identifier;
	public boolean actsAsWall;
	
	public TileFeature()
	{
		this.name = "Untitled";
		this.identifier = "U";
		this.actsAsWall = false;
	}
	
	public TileFeature(String name, String identifier, boolean actsAsWall)
	{
		this.name = name;
		this.identifier = identifier;
		this.actsAsWall = actsAsWall;
	}
	
	public String description()
	{
		return String.format("%s (Identifier: %s Wall: %b)", this.name, this.identifier, this.actsAsWall);
	}
}
