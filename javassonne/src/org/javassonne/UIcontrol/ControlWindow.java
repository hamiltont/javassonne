import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class ctrlWindow extends JFrame implements ActionListener {
	
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
