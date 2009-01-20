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
import java.lang.reflect.Field;

import javax.swing.*;
import javassonne.src.org.javassonne.ui.*;


public class ControlPanel extends JPanel implements ActionListener {

	public ControlPanel (){

		//***Need field changed to public if we want to do it this way***
		Field BscWindow_ = javassonne.src.org.javassonne.ui.BasicWindow.class.getClass().getDeclaredField("contentPane_");
		Container MyWindow_ = (Container) BscWindow_.getGenericType();
		
		this.setBackground(MyWindow_.getBackground());
		this.setLayout(MyWindow_.getLayout());
		this.setVisible(true);
		this.setSize(300, 150);
		
		JButton newGameButton_ = new JButton("New Game");
		JButton loadGameButton_ = new JButton("Load Game");
		JButton exitGameButton_ = new JButton("Exit Game");
    
		NewGame newGame_ = new NewGame();
		LoadGame loadGame_ = new LoadGame();
    
		newGameButton_.addActionListener(newGame_);
		loadGameButton_.addActionListener(loadGame_);
		exitGameButton_.addActionListener(this);
		
		MyWindow_.add(this);
	}
	
    public void actionPerformed(ActionEvent e){
    	//this.dispose();
    	System.exit(0);
	}
}




//Possible trash:

//private Container ctrlContentPane_;

//ctrlContentPane_ = getContentPane();

/*
ctrlContentPane_.setBackground(Color.gray);
ctrlContentPane_.setLayout(new FlowLayout()); 

ctrlContentPane_.add(newGameButton_);
ctrlContentPane_.add(loadGameButton_);
ctrlContentPane_.add(exitGameButton_);
*/
