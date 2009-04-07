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

import org.javassonne.model.TileFeature;

public class TileFeatureTests extends TestCase {

	public void testTileFeatureConstructor()
	{
		// create tile feature with some properties, and test that the 
		// constructor works as advertised.
		TileFeature f = new TileFeature("name","identifier", true, 2);
		
		assertTrue(f.name == "name");
		assertTrue(f.identifier == "identifier");
		assertTrue(f.actsAsWall == true);
		//assertTrue(f.multiplier == 2);
	}
	
	public void testTileFeatureDescription()
	{
		// make sure the tileFeature correctly returns a description of itself.
		TileFeature f = new TileFeature();
		assertTrue(f.description().length() > 0);
	}
}
