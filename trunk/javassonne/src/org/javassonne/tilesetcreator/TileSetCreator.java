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

public class TileSetCreator {

	public static void main(String[] args) {
        
		Scanner in = new Scanner(System.in);
		TileSerializer serializer = new TileSerializer();
		TileSet set = null;
        
		try {
				
			// prompt the user to either create or load an existing tile set
			System.out.println("Would you like to (1) Create a new tile set or (2) Load an existing one?");
			int response = in.nextInt();

			// fetch the tile set
			if (response == 1)
			{
				set = new TileSet("Untitled Set");
			}
			else 
			{
				System.out.println("Please enter the name of the existing set:");
				String name = in.nextLine();
				set = serializer.loadTileSet(name);
			}
		
			// present the main menu
			while (true)
			{
				System.out.println();
				System.out.println();
				System.out.println("------------------------- TILESET CREATOR --------------------");
				System.out.println(String.format("Editing '%s' with %d tiles", set.getName(), set.tileCount()));
				System.out.println("--------------------------------------------------------------");
				
				System.out.println("1) Add Tile");
				System.out.println("2) View Existing Tiles");
				System.out.println("3) Edit Existing Tile");
				System.out.println();
			
				int option = in.nextInt();
				System.out.println();
				
				if (option == 1){
					System.out.println("----- ADDING TILE ----------------------------------------------");
					
					Tile t = new Tile();
					promptForTileProperties(t, in);

					System.out.println("How many of this tile should be included in the set?");
					int count = in.nextInt();
					set.addTile(t, count);
					
				} else if (option == 2) {
					System.out.println("----- EXISTING TILES --------------------------------------------");
					
					for (int ii = 0; ii < set.tileCount(); ii++)
					{
						Tile t = set.tileAtIndex(ii);
						System.out.println(String.format("%d: %s", ii, t.description()));
					}
					
					System.out.println("----------------------------------------------------------------");
				
				} else if (option == 3){
					System.out.println("Please enter tile index:");
					int index = in.nextInt();
					
					Tile t = set.tileAtIndex(index);
					promptForTileProperties(t, in);
				}
			}	
		} catch (IOException e){
			System.err.println("An IO error occurred.");
		}
	}
	
	public static Tile.LandType getTileType(Scanner in) throws IOException
	{
		System.out.println("(F=Fort, FC=FortCap, R=River, C=Cloister, N=Nothing)");
		String response;
		
		while (true){
			response = in.nextLine();
			if (response.equals("F"))
				return Tile.LandType.Fort;
			if (response.equals("FC"))
				return Tile.LandType.FortCap;
			if (response.equals("R"))
				return Tile.LandType.River;
			if (response.equals("C"))
				return Tile.LandType.Cloister;
			if (response.equals("N"))
				return Tile.LandType.Nothing;
			System.out.println("Please enter a valid land type!");
		}
	}
	
	public static void promptForTileProperties(Tile t, Scanner in) throws IOException
	{
		System.out.println(String.format("Tile Land Top: (Currently %s)", t.getLandTop()));
		t.setLandTop(getTileType(in));
		
		System.out.println(String.format("Tile Land Bottom: (Currently %s)", t.getLandBottom()));
		t.setLandBottom(getTileType(in));
		
		System.out.println(String.format("Tile Land Left: (Currently %s)", t.getLandLeft()));
		t.setLandLeft(getTileType(in));
		
		System.out.println(String.format("Tile Land Right: (Currently %s)", t.getLandRight()));
		t.setLandRight(getTileType(in));
		
		System.out.println(String.format("Tile Land Center: (Currently %s)", t.getLandCenter()));
		t.setLandCenter(getTileType(in));
	
		System.out.println(String.format("Tile Unique Identifier: (Currently %s)", t.getUniqueIdentifier()));
		t.setUniqueIdentifier(in.nextLine());
	}
}
