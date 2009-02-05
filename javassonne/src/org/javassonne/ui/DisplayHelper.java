/**
 * Javassonne 
 *  http://code.google.com/p/javassonne/
 * 
 * @author Hamilton Turner
 * @date Jan 29, 2009
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
import java.awt.Point;

import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JLayeredPane;

/**
 * @author Hamilton Turner
 * 
 *         DisplayHelper exists so other developers do not have to know or
 *         understand the majority of the drawing interface. It allows a
 *         developer to simply request to add an element to the screen, and with
 *         a show() call, make it visible on top of all other elements in that
 *         layer. DisplayHelper is a singleton, so an appropriate usage would
 *         look similar to
 *         DisplayHelper.getInstance().show(this,DisplayHelper.Layer.DRAG)
 * 
 *         Developers should at a minimum attempt to place items on the correct
 *         layer, in order to 'play nicely' with all other developer's code.
 * 
 *         Doc's Excerpt from JLayeredPane DEFAULT_LAYER The standard
 *         layer,where most components go. This the bottommost layer. This is
 *         purposefully hidden from the other developers using this class, and
 *         should solely be used by the MapLayer. PALETTE_LAYER The palette
 *         layer sits over the default layer. Useful for floating toolbars and
 *         palettes, so they can be positioned above other components.
 *         MODAL_LAYER The layer used for modal dialogs. They will appear on top
 *         of any toolbars, palettes, or standard components in the container.
 *         POPUP_LAYER The popup layer displays above dialogs. That way, the
 *         popup windows associated with combo boxes, tooltips, and other help
 *         text will appear above the component, palette, or dialog that
 *         generated them. DRAG_LAYER When dragging a component, reassigning it
 *         to the drag layer ensures that it is positioned over every other
 *         component in the container. When finished dragging, it can be
 *         reassigned to its normal layer.
 */
public class DisplayHelper {

	private static DisplayHelper INSTANCE;
	private JDesktopPane desktopPane_ = null;

	/**
	 * Predefined constants for Layers
	 */
	public static enum Layer {
		PALETTE(JLayeredPane.PALETTE_LAYER), MODAL(JLayeredPane.MODAL_LAYER), POPUP(
				JLayeredPane.POPUP_LAYER), DRAG(JLayeredPane.DRAG_LAYER);
		private final int index;

		Layer(int i) {
			index = i;
		}
	}

	/**
	 * Predefined constants for Layers
	 */
	public static enum Positioning {
		TOP_LEFT, TOP_RIGHT, CENTER;
	}

	/**
	 * Private singleton constructor.
	 */
	private DisplayHelper() {
	}

	/**
	 * Standard Singleton access method.
	 * 
	 * @return the single DisplayHelper instance
	 */
	public static DisplayHelper getInstance() {
		if (INSTANCE == null)
			INSTANCE = new DisplayHelper();
		return INSTANCE;
	}

	/**
	 * Sets the JLayeredPane that the DisplayHelper instance is 'helping' with.
	 * This should only be called by the program setup, and there is a flag to
	 * only allow it to be called once.
	 * 
	 * @param layeredDisplayPane
	 *            The JLayeredPane to be operated on.
	 */
	protected void setDesktopPane(JDesktopPane layeredDisplayPane) {
		// Only allow layered pane to be set once
		if (!isLayeredPaneInvalid())
			return;

		desktopPane_ = layeredDisplayPane;
	}

	public Dimension getScreenSize() {
		if (!isLayeredPaneInvalid())
			return null;
		return desktopPane_.getSize();
	}

	/**
	 * Allows for easy addition of an object to the screen, by passing in the
	 * JPanel they want displayed on screen, and by setting the layer they would
	 * like it to be displayed on. The object will automatically be assigned the
	 * highest position in it's layer. That is, it will automatically be the
	 * most visible component in it's layer and will be set visible.
	 * 
	 * @param displayMe
	 *            The object to be displayed on screen.
	 * @param displayLayer
	 *            The appropriate layer to display this object on.
	 * @param position
	 *            The area of the screen where you would like the object to
	 *            appear.
	 */
	public void add(JComponent displayMe, DisplayHelper.Layer displayLayer,
			DisplayHelper.Positioning position) {

		if (position == Positioning.TOP_LEFT)
			add(displayMe, displayLayer, new Point(10, 10));
		else if (position == Positioning.TOP_RIGHT)
			add(displayMe, displayLayer, new Point(desktopPane_.getWidth() - 10
					- displayMe.getWidth(), 10));
		else
			add(displayMe, displayLayer, new Point(
					(desktopPane_.getWidth() - displayMe.getWidth()) / 2,
					(desktopPane_.getHeight() - displayMe.getHeight()) / 2));
	}

	/**
	 * Allows for easy addition of an object to the screen, by passing in the
	 * JPanel they want displayed on screen, and by setting the layer they would
	 * like it to be displayed on. The object will automatically be assigned the
	 * highest position in it's layer. That is, it will automatically be the
	 * most visible component in it's layer and will be set visible.
	 * 
	 * @param displayMe
	 *            The object to be displayed on screen.
	 * @param displayLayer
	 *            The appropriate layer to display this object on.
	 * @param location
	 *            The location where the component will be placed.
	 * 
	 */
	public void add(JComponent displayMe, DisplayHelper.Layer displayLayer,
			Point location) {

		// Check that the layered pane is present
		if (isLayeredPaneInvalid())
			return;

		// Check that the item is not already present
		int checkAlreadyPresent = desktopPane_.getIndexOf(displayMe);
		if (checkAlreadyPresent != -1) {
			updateLayer(displayMe, displayLayer);
			updateLocation(displayMe, location);
		} else {
			desktopPane_.add(displayMe, JLayeredPane.PALETTE_LAYER);

			showOnTop(displayMe);
			displayMe.setLocation(location);
			displayMe.setVisible(true);
		}
	}

	public void addAsInternalFrame(JComponent displayMe,
			DisplayHelper.Layer displayLayer, Point startingLocation) {

		JInternalFrame f = new JInternalFrame("", false, false, false, false);
		f.setContentPane(displayMe);
		f.pack();

		add(f, displayLayer, startingLocation);
	}

	public void remove(JComponent removeMe) {
		desktopPane_.remove(removeMe);
	}

	public void updateLayer(JComponent updateMe,
			DisplayHelper.Layer displayLayer) {
		// TODO implement function
	}

	public void updateLocation(JComponent updateMe, Point newLocation) {
		// TODO implement function
	}

	public void showOnTop(JComponent showMe) {
		desktopPane_.moveToFront(showMe);
	}

	private boolean isLayeredPaneInvalid() {
		boolean equalsNull = (desktopPane_ == null);
		return equalsNull;
	}
}
