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

package org.javassonne.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.javassonne.model.Tile;

public class RotateTile implements ActionListener {

	private Tile tile_;
	
	public void actionPerformed(ActionEvent e) {
		/* rotate tile here */
		
		String action = e.getActionCommand();
		
		if(action.equals("rotate_right"))
		{
			tile_.rotateRight();
		}
		else
		{
			tile_.rotateLeft();
		}
	}

}
