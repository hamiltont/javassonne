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

package org.javassonne.tilesetcreator;

import java.io.*;
import java.util.Scanner;
import org.javassonne.model.*;
import org.javassonne.model.Tile.Quadrant;
import org.javassonne.model.Tile.Region;

public class TileSetCreator {

	public static TileSet currentSet = null;
	public static String  currentSetPath = null;
	
	public static void main(String[] args) {
        
		Scanner in = new Scanner(System.in);
		TileSerializer serializer = new TileSerializer();
		
		try {
				
			// prompt the user to either create or load an existing tile set
			System.out.println("Welcome to the Javassonne Tile Set Creator!");
			
			// load a tile set into currentSet
			while (currentSet == null)
			{
				System.out.println("Would you like to (1) Create a new tile set or (2) Load an existing one?");
				
				int response = in.nextInt();
				
				// fetch the tile set
				if (response == 1)
				{
					currentSet = new TileSet("Untitled Set");
				}
				else 
				{
					System.out.println("Select an existing tile set or type a path:");
					currentSetPath = getTileSetPathInFolder("tilesets/", in);
					if (currentSetPath != null){
						currentSet = serializer.loadTileSet(currentSetPath);
						
						if (currentSet == null){
							System.out.println("The tile set could not be found.");
						}
					}
				}
			}
			
			// present the main menu
			while (true)
			{
				System.out.println();
				System.out.println();
				System.out.println("------------------------- TILESET CREATOR --------------------");
				if (currentSet != null) 
					System.out.println(String.format("Editing '%s' with %d tiles and %d types of features.",
						currentSet.getName(), currentSet.tileCount(), currentSet.tileFeatureCount()));
				System.out.println("--------------------------------------------------------------");
	
				System.out.println("1) Add Tile");
				System.out.println("2) View Tiles");
				System.out.println("3) Edit Tile");
				System.out.println();
				System.out.println("4) Add Tile Feature");
				System.out.println("5) View Tile Features");
				System.out.println("6) Edit Tile Feature");
				System.out.println();
				System.out.println("7) Save Tile Set");
				System.out.println("8) Change Tile Set Name");
				System.out.println();
				System.out.println("9) Exit (Without Saving)");
				System.out.println();
				System.out.print(":");
				
				int option = in.nextInt();
				System.out.println();
				
				if (option == 1){
					System.out.println("----- ADD TILE -------------------------------------------------");
					
					Tile t = new Tile();
					if (promptForTileProperties(t, in)){
						System.out.println("How many of this tile should be included in the set?");
						int count = in.nextInt();
						currentSet.addTile(t, count);
					}
					
				} else if (option == 2) {
					System.out.println("----- VIEW TILES -----------------------------------------------");
					
					for (int ii = 0; ii < currentSet.tileCount(); ii++)
					{
						Tile t = currentSet.tileAtIndex(ii);
						System.out.println(String.format("%d\r%s", ii, t.description()));
					}
					System.out.println("----------------------------------------------------------------");
				
				} else if (option == 3){
					System.out.println("----- EDIT TILE ------------------------------------------------");
					System.out.println("Please enter tile index:");
					int index = in.nextInt();
					
					Tile t = currentSet.tileAtIndex(index);
					promptForTileProperties(t, in);
					
				} else if (option == 4){
					System.out.println("----- ADD TILE FEATURE -----------------------------------------");
					TileFeature f = new TileFeature();
					promptForTileFeatureProperties(f, in);
					currentSet.addTileFeature(f);
					
				} else if (option == 5){
					System.out.println("----- VIEW TILE FEATURES ---------------------------------------");

					for (int ii = 0; ii < currentSet.tileFeatureCount(); ii++)
					{
						TileFeature f = currentSet.tileFeatureAtIndex(ii);
						System.out.println(String.format("%d: %s", ii, f.description()));
					}
					System.out.println("----------------------------------------------------------------");

				} else if (option == 6){
					System.out.println("----- EDIT TILE FEATURE ----------------------------------------");
					System.out.println("Please enter tile feature index:");
					int index = in.nextInt();
					
					TileFeature t = currentSet.tileFeatureAtIndex(index);
					promptForTileFeatureProperties(t, in);
					
				} else if (option == 7){
					System.out.println("----- SAVE TILE SET --------------------------------------------");

					if (currentSetPath != null) {
						System.out.println("Enter a file path, or 1 to use "+currentSetPath+":");
					} else {
						System.out.println("Enter a file path:");
					}
					String newSetPath = in.next();
					if (Integer.valueOf(newSetPath) != 1)
						currentSetPath = newSetPath;
					
					serializer.saveTileSet(currentSet, currentSetPath);
					System.out.println("Tile Set saved successfully!");

				} else if (option == 8){
						System.out.println("Enter a new name for the set:");
						currentSet.setName(in.next());
						in.nextLine();
						
				} else if( option == 9){
					System.out.println("Goodbye!");
					System.exit(0);
				}
			}	
		} catch (IOException e){
			System.err.println("An IO error occurred: " + e);
		}
	}
	
	public static TileFeature getFeature(Scanner in)
	{
		TileFeature f = null;
		
		// print out the options as a string
		if (currentSet.tileFeatureCount() > 0){
			StringBuilder s = new StringBuilder();
			for (int ii = 0; ii < currentSet.tileFeatureCount(); ii++)
			{
				TileFeature feature = currentSet.tileFeatureAtIndex(ii);
				s = s.append(String.format("%s = %s, ", feature.identifier, feature.name));
			}
			System.out.println(s.toString());
			
		} else {
			System.out.println("There are no available features! Create a feature before creating tiles.");
			return null;
		}
		
		// see what the user selects (repeat until their selection is valid)
		do {
			String response = in.next();
			f = currentSet.tileFeatureWithIdentifier(response);
		} while (f == null);
	
		return f;
	}
	
	public static String getTileSetPathInFolder(String folderPath, Scanner in)
	{
		File folder = new File(folderPath);
	    
	    // This filter removes directories and .svn files
	    FileFilter fileFilter = new FileFilter() {
	        public boolean accept(File file) {
	            return ((file.isDirectory() == false) && (file.getName().startsWith(".") == false));
	        }
	    };
	    File[] files = folder.listFiles(fileFilter);
	    
	    for (int ii = 0; ii < files.length; ii++)
	    	System.out.println(String.format("%d: %s", ii+1, files[ii].getName()));
	    
	    String r = in.next();
	    Integer r_int = Integer.valueOf(r);
	    
	    if (r_int != 0){
	    	return files[r_int-1].getAbsolutePath();
	    } else {
	    	return r;
	    }
	}
	
	public static boolean promptForTileProperties(Tile t, Scanner in)
	{
		// first, prompt for the features in all five regions
		for (Region r : Region.values())
		{
			System.out.println(String.format("Enter Tile %s Feature (Currently %s):", r, t.featureIdentifierInRegion(r)));
			TileFeature feature = getFeature(in);
			if (feature == null) return false;
			t.setFeatureInRegion(r, feature);
		}
		
		// next, prompt for the four farm values
		for (Quadrant q : Quadrant.values())
		{
			System.out.println(String.format("Enter Tile %s Quadrant (Currently %d):", q, t.farmInQuadrant(q)));
			t.setFarmInQuadrant(q, in.nextInt());
		}
		
		// next, prompt for the unique tile identifier
		System.out.println(String.format("Enter Tile Unique Identifier (Currently %s):", t.getUniqueIdentifier()));
		t.setUniqueIdentifier(in.next());
		
		// wow - that was cool.
		return true;
	}
	
	public static void promptForTileFeatureProperties(TileFeature f, Scanner in)
	{
		// prompt for the name
		System.out.println(String.format("Enter Feature Name (Currently %s):", f.name));
		f.name = in.next();
		
		// prompt for the identifier
		System.out.println(String.format("Enter Feature Identifier (Currently %s):", f.identifier));
		f.identifier = in.next();
		
		// prompt for actsAsWall
		System.out.println(String.format("Should the new feature act as a wall for farms? (Currently %b):", f.actsAsWall));
		f.actsAsWall = in.nextBoolean();
	}
}
