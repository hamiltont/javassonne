/**
 * Javassonne 
 *  http://code.google.com/p/javassonne/
 * 
 * @author David Leinweber
 * @date Feb 7, 2009
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

package org.javassonne.model;

import java.awt.Color;

public class Player {

	private static final String DEFAULT_NAME = "Guest";
	private static final Color DEFAULT_MEEPLE_COLOR = Color.BLUE;
	private static final int DEFAULT_MEEPLE_REMAINING = 7;

	private String name_;
	private Color meepleColor_;
	private int meepleRemaining_;
	private int turnNumber_;
	private int score_;

	public Player() {
		name_ = DEFAULT_NAME;
		meepleColor_ = DEFAULT_MEEPLE_COLOR;
		meepleRemaining_ = DEFAULT_MEEPLE_REMAINING;
		turnNumber_ = 0;
		score_ = 0;
	}

	public Player(String name, Color meepleColor, int turnNumber) {
		name_ = name;
		meepleColor_ = meepleColor;
		meepleRemaining_ = DEFAULT_MEEPLE_REMAINING;
		turnNumber_ = turnNumber;
		score_ = 0;
	}

	// Getters and Setters
	public String getName() {
		return name_;
	}

	public void setName(String name) {
		this.name_ = name;
	}

	public Color getMeepleColor() {
		return meepleColor_;
	}

	public void setMeepleColor(Color meepleColor) {
		this.meepleColor_ = meepleColor;
	}

	public int getMeepleRemaining() {
		return meepleRemaining_;
	}

	public void setMeepleRemaining(int meepleRemaining) {
		this.meepleRemaining_ = meepleRemaining;
	}

	public int getTurnNumber() {
		return turnNumber_;
	}

	public void setTurnNumber_(int turnNumber) {
		this.turnNumber_ = turnNumber;
	}

	public int getScore() {
		return score_;
	}

	public void setScore(int score) {
		this.score_ = score;
	}

}
