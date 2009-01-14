package org.javassonne.tilesetcreator;

import org.javassonne.model.Tile;
import org.javassonne.model.TileSerializer;

public class TileSetCreator {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Tile t = new Tile();
		
		t.setLandTop(Tile.LandType.Fort);
		t.setLandBottom(Tile.LandType.Fort);
		t.setLandLeft(Tile.LandType.Fort);
		t.setLandRight(Tile.LandType.Fort);
		t.setLandCenter(Tile.LandType.Fort);
	
		TileSerializer serializer = new TileSerializer();
		System.out.println(serializer.serializeTile(t));
	}

}
