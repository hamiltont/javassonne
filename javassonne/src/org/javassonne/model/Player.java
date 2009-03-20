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


public class Player {

	public enum MeepleColor {
		Yellow(0), Green(1), Blue(2), Red(3), Black(4), Pink(5);
		public final int value;

		MeepleColor(int i) {
			this.value = i;
		}
	}
	
	private static final String DEFAULT_NAME = "Guest";
	private static final MeepleColor DEFAULT_MEEPLE_COLOR = MeepleColor.Yellow;
	private static final int DEFAULT_MEEPLE_REMAINING = 7;

	private String name_;
	private MeepleColor meepleColor_;
	private int meepleRemaining_;
	private int turnNumber_;
	private int score_;
	
	// The game interface needs to know if this player is on the local machine or not.
	// (So we can allow them to place a tile during their turn or not) This may have
	// a more complex representation here later, but this is all we need for now.
	private Boolean isLocal_;
	
	//ctors
	public Player() {
		name_ = DEFAULT_NAME;
		meepleColor_ = DEFAULT_MEEPLE_COLOR;
		meepleRemaining_ = DEFAULT_MEEPLE_REMAINING;
		isLocal_ = true;
		turnNumber_ = 0;
		score_ = 0;
	}

	public Player(String name, MeepleColor meepleColor, int turnNumber) {
		name_ = name;
		meepleColor_ = meepleColor;
		meepleRemaining_ = DEFAULT_MEEPLE_REMAINING;
		turnNumber_ = turnNumber;
		isLocal_ = true;
		score_ = 0;
	}
	
	public Player(String name)
	{
		name_ = name;
		meepleColor_ = DEFAULT_MEEPLE_COLOR;
		meepleRemaining_ = DEFAULT_MEEPLE_REMAINING;
		turnNumber_ = 0;
		isLocal_ = true;
		score_ = 0;
	}

	// Getters and Setters
	public String getName() {
		return name_;
	}

	public void setName(String name) {
		this.name_ = name;
	}

	public MeepleColor getMeepleColor() {
		return meepleColor_;
	}

	public void setMeepleColor(MeepleColor meepleColor) {
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
	
	public Boolean getIsLocal(){
		return isLocal_;
	}
	
	public void setIsLocal(Boolean local){
		this.isLocal_ = local;
	}
}
