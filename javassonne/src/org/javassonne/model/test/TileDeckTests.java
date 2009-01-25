/**
 * Javassonne 
 *  http://code.google.com/p/javassonne/
 * 
 * @author [Add Name Here]
 * @date Jan 25, 2009
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

package org.javassonne.model.test;

import junit.framework.TestCase;

import org.javassonne.model.Tile;
import org.javassonne.model.TileDeck;

public class TileDeckTests extends TestCase {

	public void testAddTiles()
	{
		TileDeck d = new TileDeck();
		
		d.addTile(new Tile(), 1);
		
		assertTrue(d.tilesRemaining() == 1);
		
		d.addTile(new Tile(), 2);
		
		assertTrue(d.tilesRemaining() == 3);
	}
	
	public void testPopTile()
	{
		TileDeck d = new TileDeck();
		Tile t = new Tile();
		d.addTile(t, 1);
		
		assertTrue(d.popRandomTile() == t);
		assertTrue(d.tilesRemaining() == 0);
	}
	
	public void testUnderflow()
	{
		TileDeck d = new TileDeck();
		
		assertNull(d.popRandomTile());
		
		d.addTile(new Tile(), 1);
		
		assertNotNull(d.popRandomTile());
		assertNull(d.popRandomTile());
	}
}
