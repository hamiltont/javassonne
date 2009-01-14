package org.javassonne.model;

import com.thoughtworks.xstream.*;

public class TileSerializer {

	private XStream xstream;
	
	public TileSerializer()
	{
		xstream = new XStream();
		xstream.alias("tile", Tile.class);
	}

	public TileSet loadTileSet(String filename)
	{
		return null;
	}
	
	public String saveTile(Tile tile)
	{
		String xml = xstream.toXML(tile);
		return xml;
	}
}
