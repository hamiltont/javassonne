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

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import org.javassonne.messaging.Notification;
import org.javassonne.messaging.NotificationManager;
import org.javassonne.model.Player.MeepleColor;
import org.javassonne.ui.DisplayHelper;

public class HUDConfirmPlacementPanel extends AbstractHUDPanel implements
		ActionListener {

	JButton endTurnButton_;
	JButton cancelButton_;

	DragMeeplePanel placeFarmer_ = null;
	DragMeeplePanel placeVillager_ = null;
	
	MeepleColor color_;
	
	public HUDConfirmPlacementPanel(MeepleColor c) {
		super();
		setLayout(null);
		setSize(490, 100);
		setVisible(true);
		setOpaque(false);
		setBackgroundImagePath("images/hud_confirm_placement_background.jpg");

		color_ = c;
		
		// add the end turn and cancel buttons
		endTurnButton_ = new JButton(new ImageIcon("images/end_turn.jpg"));
		endTurnButton_.setActionCommand(Notification.END_TURN);
		endTurnButton_.addActionListener(this);
		endTurnButton_.setSize(103, 38);
		endTurnButton_.setLocation(375, 51);
		add(endTurnButton_);

		cancelButton_ = new JButton(new ImageIcon("images/cancel.jpg"));
		cancelButton_.setActionCommand(Notification.UNDO_PLACE_TILE);
		cancelButton_.addActionListener(this);
		cancelButton_.setSize(103, 38);
		cancelButton_.setLocation(375, 10);
		add(cancelButton_);
		
		// listen for the endGame notice in case we need to remove ourselves
		NotificationManager.getInstance().addObserver(Notification.END_GAME,
				this, "endGame");
	}

	public void endGame()
	{
		close();
	}
	
	public void attachMeeplePanels()
	{	
		// create drag meeple panels
		placeFarmer_ = new DragMeeplePanel(color_);
		Point location1 = new Point(this.getLocation().x + 50,this.getLocation().y + 16);
		DisplayHelper.getInstance().add(placeFarmer_, DisplayHelper.Layer.DRAG, location1);

		placeVillager_ = new DragMeeplePanel(color_);
		Point location2 = new Point(this.getLocation().x + 255,this.getLocation().y + 16);
		DisplayHelper.getInstance().add(placeVillager_, DisplayHelper.Layer.DRAG, location2);
	}
	
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (e.getActionCommand() != "")
			NotificationManager.getInstance().sendNotification(
					e.getActionCommand());
		
		close();
	}
	
	@Override
	public void close()
	{
		super.close();
		placeFarmer_.close();
		placeVillager_.close();
	}
}
