/**
 * Javassonne 
 *  http://code.google.com/p/javassonne/
 * 
 * @author [Add Name Here]
 * @date Jan 21, 2009
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
import org.javassonne.model.TileBoardGenIterator;
import org.javassonne.model.TileDeck;
import org.javassonne.model.TileMapBoard;

public class TileMapBoardTests extends TestCase {

	TileMapBoard board_;
	TileDeck td = new TileDeck();
	Tile homeTile = new Tile();
	Tile leftTile = new Tile();
	Tile rightTile = new Tile();
	Tile upTile = new Tile();
	Tile downTile = new Tile();

	protected void setUp() throws Exception {
		super.setUp();
		//sets hometile
		td.addTile(homeTile, 1);
		board_ = new TileMapBoard(td);
		TileBoardGenIterator home = (TileBoardGenIterator) board_.homeTile();
		board_.addTile(home.leftCopy(), leftTile);
		board_.addTile(home.rightCopy(), rightTile);
		board_.addTile(home.upCopy(), upTile);
		board_.addTile(home.downCopy(), downTile);
	}
	
	public void testIterCopy(){
		TileBoardGenIterator home = (TileBoardGenIterator) board_.homeTile();
		TileBoardGenIterator right = (TileBoardGenIterator) home.rightCopy();
		assertTrue(!home.equals(right));
		assertTrue(home.equals((TileBoardGenIterator)right.left()));
		
		TileBoardGenIterator left = (TileBoardGenIterator) home.leftCopy();
		assertTrue(!home.equals(left));
		assertTrue(home.equals((TileBoardGenIterator)left.right()));
		
		TileBoardGenIterator up = (TileBoardGenIterator) home.upCopy();
		assertTrue(!home.equals(up));
		assertTrue(home.equals((TileBoardGenIterator)up.down()));
		
		TileBoardGenIterator down = (TileBoardGenIterator) home.downCopy();
		assertTrue(!home.equals(down));
		assertTrue(home.equals((TileBoardGenIterator)down.up()));
		
	}

	public void testIterator() {
		TileBoardGenIterator iter = (TileBoardGenIterator) board_.homeTile();
		assertTrue(upTile.equals(iter.up().current()));
		// iter should now point up
		assertTrue(homeTile.equals(iter.down().current()));
		// iter should now point home
		assertTrue(downTile.equals(iter.down().current()));
		// iter should now point down
		assertTrue(leftTile.equals(((TileBoardGenIterator) iter.up()).left()
				.current()));
		// iter should now point left
		assertTrue(rightTile.equals(((TileBoardGenIterator) iter.right())
				.right().current()));
		// iter should now point right
	}

	public void testCorners() {
		TileBoardGenIterator iter = (TileBoardGenIterator) board_
				.getUpperLeftCorner();
		// iter should point left 2 and up 1 from upTile
		assertTrue(upTile
				.equals(((TileBoardGenIterator) ((TileBoardGenIterator) iter
						.rightCopy()).right()).down().current()));
		// iter should still point left 2 and up 1 from upTile
		// alternatively, it should point left 1 and up 2 from leftTile
		assertTrue(leftTile
				.equals(((TileBoardGenIterator) ((TileBoardGenIterator) iter
						.right()).down()).down().current()));

		TileBoardGenIterator iter2 = (TileBoardGenIterator) board_
				.getLowerRightCorner();
		// iter2 should point right 2 and down 1 from downTile
		assertTrue(downTile
				.equals(((TileBoardGenIterator) ((TileBoardGenIterator) iter2
						.leftCopy()).left()).up().current()));
		// iter2 should still point right 2 and down 1 from upTile
		// alternatively, it should point right 1 and down 2 from leftTile
		assertTrue(rightTile
				.equals(((TileBoardGenIterator) ((TileBoardGenIterator) iter2
						.left()).up()).up().current()));

	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

}
