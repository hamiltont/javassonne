/**
 * Javassonne 
 *  http://code.google.com/p/javassonne/
 * 
 * @author Hamilton Turner
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

package org.javassonne.ui;


public class Main {

	public static void main(String args[]) {
		// create the application controller. This will handle starting a new game, etc...
		GameController controller = new GameController();
		
		// create the game window and the log window
		DisplayWindow window = new DisplayWindow();
		window.setVisible(true);
		
		LogWindow log = new LogWindow();
		log.setVisible(true);
		
		// set the display helper's layeredPane so that other controllers can add JPanels to 
		// the window really easily.
		DisplayHelper.getInstance().setLayeredPane(window.getDisplayLayeredPane());
	}
}
