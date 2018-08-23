package stringflow.rta.gen2;

import stringflow.rta.BaseGame;

public abstract class Gen2Game extends BaseGame {
	
	public Gen2Game(String symFilePath, int hRandomAdd, int hRandomSub) {
		super(symFilePath, "./data/gen2/species_map.txt", "GameTime", -1, hRandomAdd, hRandomSub);
	}
}
