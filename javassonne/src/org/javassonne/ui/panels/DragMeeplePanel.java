/**
 * Javassonne 
 *  http://code.google.com/p/javassonne/
 * 
 * @author [Add Name Here]
 * @date Mar 13, 2009
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

package org.javassonne.ui.panels;

import org.javassonne.messaging.Notification;
import org.javassonne.model.Player.MeepleColor;

public class DragMeeplePanel extends AbstractHUDPanel {

	public DragMeeplePanel(MeepleColor c, String meepleType)
	{
		setSize(48, 52);
		setOpaque(false);
		if (meepleType.equals("farmer")){
			setBackgroundImagePath(String.format("images/meeple_flat_%d.png", c.value));
			setDropNotification(Notification.MEEPLE_FARMER_DROPPED);
			setDragNotification(Notification.MEEPLE_FARMER_DRAG_STARTED);
		}else{
			setBackgroundImagePath(String.format("images/meeple_%d.png", c.value));
			setDropNotification(Notification.MEEPLE_VILLAGER_DROPPED);
			setDragNotification(Notification.MEEPLE_VILLAGER_DRAG_STARTED);
		}
		setBackgroundScaleToFit(true);
		
		// make it so the user can drag and drop the tile
		setDraggable(true);
	}
}
