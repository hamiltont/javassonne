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
import java.io.*;

public class TileSerializer {

	private XStream xstream;
	
	public TileSerializer()
	{	
		xstream = new XStream();
	}

	public TileSet loadTileSet(String filename) throws IOException {
		File f = new File(filename);
		if(!f.exists())
			throw new IOException("File '" + filename +"' does not exist");

		try{
			//Read file into a variable
			BufferedReader reader = new BufferedReader(new FileReader(filename));
			StringBuilder xml = new StringBuilder();
			String line;
			while( ( line = reader.readLine()) != null)
				xml.append(line + "\n");
			
			TileSet set = (TileSet)xstream.fromXML(xml.toString());
			return set;
		}catch(Exception e){
			System.out.println("Error loading tileset: "+e);
			throw new IOException();
		}
	}
	
	public String saveTile(Tile tile)
	{
		String xml = xstream.toXML(tile);
		return xml;
	}
	
	//Saves tileset to file
	public void saveTileSet(TileSet set, String filename) throws IOException{
		try{
			BufferedWriter out = new BufferedWriter( new FileWriter(filename, true));
			out.write(xstream.toXML(set));
            out.close();
		}catch(Exception e){
			System.out.println("Error saving tileset: "+ e);
			throw new IOException();
		}
	}
	
}
