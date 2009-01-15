/**
 * Javassonne 
 *  http://code.google.com/p/javassonne/
 * 
 * @author Ben Gotow
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

package org.javassonne.model;

public class Tile {
    public enum LandType { Road, Fort, FortCap, River, Cloister, Nothing }

	private LandType landLeft_;
	private LandType landRight_;
	private LandType landTop_;
	private LandType landBottom_;
	private LandType landCenter_;
	
	private int[][] farms_ = new int[2][2];

	private String uniqueIdentifier_;
	
	// Constructor
	
	public Tile()
	{
	}
	
	// Helpful Functions
	
	public String description()
	{
		return String.format("%s Land: [T:%s C:%s B:%s L:%s R:%s], Farms: [%d %d | %d %d]", 
				uniqueIdentifier_, landTop_, landCenter_, landBottom_, landLeft_, landRight_, 
				farms_[0][0],farms_[1][0],farms_[0][1],farms_[1][1]);
	}
	
	// Getter and Setter Functionality
	
	public void setFarmAtLocation(int farmValue, int x, int y)
	{
		farms_[x][y] = farmValue;
	}
	
	public int farmAtLocation(int x, int y)
	{
		return farms_[x][y];
	}
	
	public void setLandCenter(LandType landCenter_) {
		this.landCenter_ = landCenter_;
	}

	public LandType getLandCenter() {
		return landCenter_;
	}

	public void setLandBottom(LandType landBottom_) {
		this.landBottom_ = landBottom_;
	}

	public LandType getLandBottom() {
		return landBottom_;
	}

	public void setLandTop(LandType landTop_) {
		this.landTop_ = landTop_;
	}

	public LandType getLandTop() {
		return landTop_;
	}

	public void setLandRight(LandType landRight_) {
		this.landRight_ = landRight_;
	}

	public LandType getLandRight() {
		return landRight_;
	}

	public void setLandLeft(LandType landLeft_) {
		this.landLeft_ = landLeft_;
	}

	public LandType getLandLeft() {
		return landLeft_;
	}

	public void setUniqueIdentifier(String uniqueIdentifier_) {
		this.uniqueIdentifier_ = uniqueIdentifier_;
	}

	public String getUniqueIdentifier() {
		return uniqueIdentifier_;
	}
	
	
}