/**
 * Javassonne 
 *  http://code.google.com/p/javassonne/
 * 
 * @author [Add Name Here]
 * @date Mar 10, 2009
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

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.lang.reflect.Method;

import javax.swing.JOptionPane;

import org.javassonne.messaging.Notification;
import org.javassonne.messaging.NotificationManager;
import org.javassonne.ui.DisplayHelper;

public class HUDShowInstructionsPanel extends AbstractHUDPanel implements MouseListener {

	public HUDShowInstructionsPanel() {
		setOpaque(false);
		setVisible(true);
		setSize(448, 44);
		addMouseListener(this);
		
		// Listen for a notification from the tile being dragged. If we 
		// receive this, we will close the instructions panel.
		NotificationManager.getInstance().addObserver(Notification.TILE_DROPPED,
				this, "tileDropped");

		// Subscribe for notifications from the controller so we know when to
		// update ourselves!
		NotificationManager.getInstance().addObserver(
				Notification.END_GAME, this, "endGame");
		
		setBackgroundImagePath("images/hud_show_instructions.png");
	}
	
	public void tileDropped(Notification n)
	{
		fadeOut();
		
		// Unsubscribe from notifications once the game has ended
		NotificationManager.getInstance().removeObserver(this);
	}
	public void endGame(Notification n) {
		// Unsubscribe from notifications once the game has ended
		NotificationManager.getInstance().removeObserver(this);
	
		// remove ourselves from the displayHelper
		DisplayHelper.getInstance().remove(this);
	}
	
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		openURL("http://www.fwtwr.com/fwtwr/carcassonne/rules.asp");
	}

	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		setBackgroundImagePath("images/hud_show_instructions_down.png");
		repaint();

	}

	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		setBackgroundImagePath("images/hud_show_instructions.png");
		repaint();

	}

	// ///////////////////////////////////////////////////////
	// Bare Bones Browser Launch //
	// Version 1.5 (December 10, 2005) //
	// By Dem Pilafian //
	// Supports: Mac OS X, GNU/Linux, Unix, Windows XP //
	// Example Usage: //
	// String url = "http://www.centerkey.com/"; //
	// BareBonesBrowserLaunch.openURL(url); //
	// Public Domain Software -- Free to Use as You Like //
	// ///////////////////////////////////////////////////////

	private static final String errMsg = "Error attempting to launch web browser";

	public static void openURL(String url) {
		String osName = System.getProperty("os.name");
		try {
			if (osName.startsWith("Mac OS")) {
				Class fileMgr = Class.forName("com.apple.eio.FileManager");
				Method openURL = fileMgr.getDeclaredMethod("openURL",
						new Class[] { String.class });
				openURL.invoke(null, new Object[] { url });
			} else if (osName.startsWith("Windows"))
				Runtime.getRuntime().exec(
						"rundll32 url.dll,FileProtocolHandler " + url);
			else { // assume Unix or Linux
				String[] browsers = { "firefox", "opera", "konqueror",
						"epiphany", "mozilla", "netscape" };
				String browser = null;
				for (int count = 0; count < browsers.length && browser == null; count++)
					if (Runtime.getRuntime().exec(
							new String[] { "which", browsers[count] })
							.waitFor() == 0)
						browser = browsers[count];
				if (browser == null)
					throw new Exception("Could not find web browser");
				else
					Runtime.getRuntime().exec(new String[] { browser, url });
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, errMsg + ":\n"
					+ e.getLocalizedMessage());
		}
	}
}
