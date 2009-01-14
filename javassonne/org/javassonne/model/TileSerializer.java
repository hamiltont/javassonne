package org.javassonne.model;

import com.thoughtworks.xstream.*;

public class TileSerializer {

	private XStream xstream;
	
	public TileSerializer()
	{
		xstream = new XStream();
		xstream.alias("tile", Tile.class);
	}

	public String serializeTile(Tile tile)
	{
		String xml = xstream.toXML(tile);
		return xml;
	}
}
