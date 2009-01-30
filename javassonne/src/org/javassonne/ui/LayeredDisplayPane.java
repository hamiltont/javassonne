/**
 * Javassonne 
 *  http://code.google.com/p/javassonne/
 * 
 * @author Hamilton Turner
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

import java.awt.Dimension;

import javax.swing.JLayeredPane;

/**
 * @author Hamilton Turner
 * 
 *         LayeredDisplayPane acts as a logical container to hold all of the
 *         layers that are displayed on the screen. Typically classes interface
 *         to this using the DisplayHelper, which allows them to show and place
 *         JPanels on the screen.
 * 
 */
public class LayeredDisplayPane extends JLayeredPane {

	/**
	 * Constructor. Creates the MapLayer, passing it the screenSize. Also adds
	 * the MapLayer to the default layer of the JLayeredPane, effectively making
	 * it the only object on the default layer (because DisplayHelper does not
	 * provide functionality to add items to the default layer)
	 * 
	 * @param screenSize
	 *            The amount of the screen that the map is allowed to use for
	 *            rendering itself.
	 */
	public LayeredDisplayPane(Dimension screenSize) {
		// Create the map layer
		MapLayer map = new MapLayer(screenSize);

		// Add the mapLayer. The DisplayHelper does
		// not have functionality to draw on the
		// default layer, so the MapLayer should be the
		// only item on the Default Layer
		add(map, JLayeredPane.DEFAULT_LAYER);
	}
} // End WorldCanvas