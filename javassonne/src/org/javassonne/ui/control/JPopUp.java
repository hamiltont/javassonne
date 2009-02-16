/**
 * Javassonne 
 *  http://code.google.com/p/javassonne/
 * 
 * @author Adam Albright
 * @date Feb 15, 2009
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

package org.javassonne.ui.control;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.javassonne.ui.DisplayHelper;

/**
 * @author Adam Albright
 * 
 *         This class displays a PopUp message to communicate with the user
 * 
 */
public class JPopUp {
	private String message_;
	private String title_;

	/**
	 * @param message
	 *            (String) : text to display to user
	 */
	public JPopUp(String message) {
		message_ = message;
		title_ = "Javassonne";
	}

	/**
	 * 
	 * @param message
	 *            (String) : text to display to user
	 * @param title
	 *            (String) : text to use as title of PopUp box
	 */
	public JPopUp(String message, String title) {
		message_ = message;
		title_ = title;
	}

	/**
	 * 
	 * @param options
	 *            (String[]) : button text for the choices
	 * @return String corresponding to user's choice (NULL if no choice)
	 */
	public String promptUser(String[] options) {

		JPanel p = new JPanel();

		DisplayHelper.getInstance().add(p, DisplayHelper.Layer.MODAL,
				DisplayHelper.Positioning.CENTER);

		int ans = JOptionPane.showOptionDialog(p, message_, title_,
				JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
				options, options[0]);

		DisplayHelper.getInstance().remove(p);
		
		if(ans == JOptionPane.CLOSED_OPTION)
			return null;
		else
			return options[ans];
	}

	/**
	 * Displays a popup to the user with the message set via CTOR. Uses an
	 * information icon
	 */
	public void showMsg() {
		showMsg(JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * 
	 * @param JOptionPaneType (int) : Type of icon to use 
	 *           examples:
	 *            JOptionPane.INFORMATION_MESSAGE, 
	 *            JOptionPane.ERROR_MESSAGE,
	 *            JOptionPane.PLAIN_MESSAGE, 
	 *            JOptionPane.QUESTION_MESSAGE,
	 *            JOptionPane.WARNING_MESSAGE
	 */
	public void showMsg(int JOptionPaneType) {

		JPanel p = new JPanel();

		DisplayHelper.getInstance().add(p, DisplayHelper.Layer.MODAL,
				DisplayHelper.Positioning.CENTER);

		JOptionPane.showMessageDialog(p, message_, title_, JOptionPaneType);

		DisplayHelper.getInstance().remove(p);
	}
}
