/**
 * Javassonne 
 *  http://code.google.com/p/javassonne/
 * 
 * @author Ben Gotow
 * @date Feb 5, 2009
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

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import org.javassonne.ui.DisplayHelper;
import org.javassonne.ui.JKeyListener;

/**
 * The AbstractHUDPanel provides basic functionality used in the game's HUD
 * panels. Right now, that means it implements MouseListener to prevent clicks
 * from passing through to the map behind it, and allows you to set a background
 * image that is drawn onto the panel automatically.
 * 
 * @author bengotow
 * 
 */
public class AbstractHUDPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private BufferedImage backgroundOriginal_ = null;
	private BufferedImage background_ = null;
	private Timer timer_;
	private float alpha_ = 1.0f;
	
	private boolean scaleToFit_ = true;
	
	public AbstractHUDPanel() {
		super();

		addKeyListener(JKeyListener.getInstance());
	}

	public void setBackgroundScaleToFit(Boolean scale) {
		scaleToFit_ = scale;
		repaint();
	}

	public void setBackgroundImagePath(String s) {
		try {
			this.setBackgroundImage(ImageIO.read(new File(s)));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setBackgroundImage(BufferedImage img) {
		backgroundOriginal_ = img;
		background_ = img;
		
		repaint();
	}

	public void setBackgroundAlpha(float alpha) {
		int w = backgroundOriginal_.getWidth();
		int h = backgroundOriginal_.getHeight();
		
		// create a semi-transparent version of the image
		BufferedImage mask = new BufferedImage(w, h,
				BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D maskG = mask.createGraphics();
		maskG.setColor(new Color(0f, 0f, 0f, alpha));
		maskG.fillRect(0, 0, w, h);
		maskG.dispose();

		background_ = new BufferedImage(w, h,
				BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g2 = background_.createGraphics();
		g2.drawImage(backgroundOriginal_, 0, 0, null);
		AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.DST_IN,
				1.0F);
		g2.setComposite(ac);
		g2.drawImage(mask, 0, 0, null);
		g2.dispose();
		
		repaint();
	}
	/*
	 * This function is responsible for painting the background image we have.
	 */
	public void paintComponent(Graphics g) {
		if (background_ != null) {
			Graphics2D g2 = (Graphics2D) g;
			if (scaleToFit_)
				g2.drawImage(background_, 0, 0, this.getWidth(), this
						.getHeight(), 0, 0, background_.getWidth(), background_
						.getHeight(), null);
			else
				g2.drawImage(background_, 0, 0, background_.getWidth(),
						background_.getHeight(), 0, 0, background_.getWidth(),
						background_.getHeight(), null);

		}
	}
	
	/*
	 * Convenience functions for animating the panel
	 */
	public void close(){
		DisplayHelper.getInstance().remove(this);
		if (timer_ != null)
			timer_.cancel();
	}
	
	public void fadeOut(){
		if (timer_ != null)
			timer_.cancel();
		timer_ = new Timer();
		timer_.schedule(new FadeTimer(-0.05f), 0, 20);	
	}
	
	public void fadeIn(){
		setBackgroundAlpha(0.0f);
		if (timer_ != null)
			timer_.cancel();
		timer_ = new Timer();
		timer_.schedule(new FadeTimer(0.05f), 0, 20);
	}
	// TIMERS 
	// ---------------------------------------------------------------------
	class FadeTimer extends TimerTask {
		float delta_ = 0.0f;
		
		FadeTimer(float delta){
			super();
			delta_ = delta;
		}
		public void run() {
			alpha_ = Math.max(alpha_ + delta_, 0.0f);
			setBackgroundAlpha(alpha_);
		
			if ((alpha_ == 0.0f) || (alpha_ == 1.0f)){
				cancel();
			}
		}
	}
}
