package org.javassonne.ui;
import java.awt.*;
import java.awt.event.*;


public class WindowDestroyer extends WindowAdapter {
	
	@Override
	public void windowClosing(WindowEvent e) {
		// TODO Auto-generated method stub
		super.windowClosed(e);
		
		System.exit(0);
	}
}
