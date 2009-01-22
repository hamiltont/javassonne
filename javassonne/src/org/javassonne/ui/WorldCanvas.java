/**
 * Javassonne 
 *  http://code.google.com/p/javassonne/
 * 
 * @author [Add Name Here]
 * @date Jan 20, 2009
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

package org.javassonne.ui;

import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionListener;

import org.javassonne.model.TileBoard;
import org.javassonne.model.TileBoardIterator;

public class WorldCanvas extends Canvas {
	private TileBoard board_;
	private Graphics2D canvas_;
	private int tileHeight_;
	private int tileWidth_;
	private int rows_ =8;
	private int cols_ =8;
	
	public WorldCanvas(TileBoard board) {
		board_ = board;
	}

	public void paint(Graphics g) {
		canvas_ = (Graphics2D) g;
		redraw();
	}
	
	// Redraw the board
	public void redraw() {
		TileBoardIterator iter = board_.homeTile();
		
		// Draw grid lines
	    int width = getSize().width;
	    int height = getSize().height;

	    tileHeight_ = height / rows_;
	    tileWidth_ = width  / cols_;
	    
	    for (int k = 0; k < rows_; k++)
	    	canvas_.drawLine(0, k * tileHeight_ , width, k * tileHeight_ );
	    
	    for (int k = 0; k < cols_; k++)
	    	canvas_.drawLine(k*tileWidth_ , 0, k*tileWidth_ , height);
	    
	    //Place tile images
		try {
			int i,x,y;
			i=x=y=0;
			while(i < (cols_*rows_) && (iter.current() != null || iter.nextRow() != null )){
				i++;
				if(iter.current() != null){
					canvas_.drawImage(iter.current().getImage(),x,y,tileWidth_,tileHeight_,null);
				}
				
				iter.right();
				x += tileWidth_;
				
				if(i%cols_==0){
					//Next row
					x=0;
					y += tileHeight_;
				}
			}
			
		}catch (Exception e) {
			System.out.println("Error displaying a tile image.");
		}
	}

	public void setActionListener(ActionListener a) {
		// Register event listener
	}

}
