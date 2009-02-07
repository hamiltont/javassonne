/**
 * Javassonne 
 *  http://code.google.com/p/javassonne/
 * 
 * @author pretekr
 * @date Feb 5, 2009
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

/**
 * 
 */

package org.javassonne.model;

import java.awt.Point;

/**
 * @author pretekr
 * 
 */
@SuppressWarnings("serial")
public class NotValidPlacementException extends Exception {
	private Point location_;

	public NotValidPlacementException(Point location) {
		super("The selected position is not valid for placement");
		location_ = location;
	}

	public NotValidPlacementException() {
		super("The selected position is not valid for placement");
	}

}
