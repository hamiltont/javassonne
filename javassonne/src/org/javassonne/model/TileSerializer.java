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
