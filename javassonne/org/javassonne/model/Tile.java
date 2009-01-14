package org.javassonne.model;

public class Tile {
    public enum LandType { Road, Fort, FortCap, River, Cloister, Nothing }

	private LandType landLeft_;
	private LandType landRight_;
	private LandType landTop_;
	private LandType landBottom_;
	private LandType landCenter_;
	
	private int[][] farms_ = new int[2][2];

	private int uniqueIdentifier_;
	
	public Tile()
	{
	}
	
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

	public void setUniqueIdentifier(int uniqueIdentifier_) {
		this.uniqueIdentifier_ = uniqueIdentifier_;
	}

	public int getUniqueIdentifier() {
		return uniqueIdentifier_;
	}
	
	
}