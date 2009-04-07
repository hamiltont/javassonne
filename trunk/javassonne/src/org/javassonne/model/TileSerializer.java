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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.thoughtworks.xstream.XStream;

/**
 * The TileSerializer is a general purpose class for reading and writing
 * TileSets to disk. It uses the XStream library to automatically serialize Java
 * objects, so it is not necessary to directly work with XML. The TileSerializer
 * should always be used to load tile sets, because its underlying
 * implementation may be changed in the future.
 * 
 * @author bengotow
 */
public class TileSerializer {

	private XStream xstream_;

	/**
	 * sets up the xstream serializer with a few customizations to make the XML
	 * cleaner in case we have to read it by hand
	 */
	public TileSerializer() {
		xstream_ = new XStream();
		xstream_.alias("Tile", Tile.class);
		xstream_.alias("TileSet", TileSet.class);
		xstream_.alias("TileFeature", TileFeature.class);
		xstream_.alias("TileFeatureBindings", TileFeatureBindings.class);

		// we don't want to serialize the images that have been cached in the
		// tiles.
		// These are stored separately in a folder full of JPEGs.
		xstream_.omitField(Tile.class, "image_");
	}

	/**
	 * @param path
	 *            The absolute or relative path to the tile set .xml file.
	 * @return An initialized tile set (with images loaded) or null if the file
	 *         could not be found.
	 */
	public TileSet loadTileSet(String path) {
		File f = new File(path);

		if (!f.exists())
			return null;

		try {
			// Read file into a variable
			BufferedReader reader = new BufferedReader(new FileReader(path));
			StringBuilder xml = new StringBuilder();
			String line;

			while ((line = reader.readLine()) != null)
				xml.append(line + "\n");

			TileSet set = (TileSet) xstream_.fromXML(xml.toString());
			set.associateFeaturesWithTiles();
			set.loadTileImages();
			return set;

		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * @param set
	 *            The set to be saved to disk.
	 * @param path
	 *            The destination file path. Should include the file name, and
	 *            extension (.xml)
	 * @throws IOException
	 */
	public void saveTileSet(TileSet set, String path) throws IOException {
		try {
			// delete the existing file, if there is one
			File existingFile = new File(path);
			if (existingFile.exists())
				existingFile.delete();

			// create the new file
			BufferedWriter out = new BufferedWriter(new FileWriter(path, true));
			out.write(xstream_.toXML(set));
			out.close();

		} catch (Exception e) {
			System.out.println("Error saving tileset: " + e);
			throw new IOException();
		}
	}

}
