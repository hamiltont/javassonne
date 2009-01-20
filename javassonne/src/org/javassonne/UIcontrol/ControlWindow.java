/**
 * Javassonne 
 *  http://code.google.com/p/javassonne/
 * 
 * @author Brian Salisbury
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

package javassonne.src.org.javassonne.UIcontrol;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;


public class ControlWindow extends JFrame implements ActionListener {
	
	private Container ctrlContentPane_ = this.getContentPane();
	
	this.setVisible(true);
	this.setSize(500, 150);
	
	ctrlContentPane_.setBackground(Color.gray);
	ctrlContentPane_.setLayout(new FlowLayout()); 
    
	ctrlContentPane_.add(JButton newGameButton_ = new JButton("New Game"));
    ctrlContentPane_.add(JButton loadGameButton_ = new JButton("Load Game"));
    ctrlContentPane_.add(JButton exitGameButton_ = new JButton("Exit Game"));
    
	newGameButton_.addActionListner(createNewGame newGame_ = new createNewGame());
	loadGameButton_.addActionListner(loadExistingGame loadGame_ = new loadExistingGame());
	exitGameButton_.addActionListner(this);
	
    public void actionPerformed(ActionEvent e) {
    	this.dispose();
    	System.exit(0);
	}

}


public static void main(String [ ] args)
{
      
}


