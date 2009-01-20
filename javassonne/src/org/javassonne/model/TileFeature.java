package org.javassonne.model;

public class TileFeature {
	public String name;
	public String identifier;
	public int multiplier;
	public boolean actsAsWall;
	
	public TileFeature()
	{
		this.name = "Untitled";
		this.identifier = "U";
		this.actsAsWall = false;
		this.multiplier = 1;
	}
	
	public TileFeature(String name, String identifier, boolean actsAsWall, int multiplier)
	{
		this.name = name;
		this.identifier = identifier;
		this.actsAsWall = actsAsWall;
		this.multiplier = multiplier;
	}
	
	public String description()
	{
		return String.format("%s (Identifier: %s Wall: %b)", this.name, this.identifier, this.actsAsWall);
	}
}
