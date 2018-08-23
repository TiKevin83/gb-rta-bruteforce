package stringflow.rta.gen1;

import stringflow.rta.BaseGame;

public abstract class Gen1Game extends BaseGame {
	
	public Gen1Game(String symFilePath, int hJoypad, int hRandomAdd, int hRandomSub) {
		super(symFilePath, "./data/gen1/species_map.txt", "PlayTime", hJoypad, hRandomAdd, hRandomSub);
	}
}
