package org.javassonne.ui;
// Needed for Container and Color
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;


public class BasicWindow extends JFrame implements ActionListener {

	private Container contentPane_;
	public BasicWindow (){
		this("Window",Color.gray);
	}

	public BasicWindow (String title, Color background)
	{
		super();
		contentPane_ = getContentPane();
		
		setSize(300, 300);
		setTitle(title);
		
		
		JButton toggle = new JButton("Change BG Color");
		toggle.addActionListener(this);
		
		contentPane_.setBackground(background);
		contentPane_.setLayout(new FlowLayout());
		
		contentPane_.add(toggle);
				
		addWindowListener(new WindowDestroyer());		
	}

	public void actionPerformed(ActionEvent e) {
		if (contentPane_.getBackground() == Color.gray)
			contentPane_.setBackground(Color.red);
		else
			contentPane_.setBackground(Color.gray);
	}
}
