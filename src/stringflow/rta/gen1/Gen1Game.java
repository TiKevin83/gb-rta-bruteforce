package stringflow.rta.gen1;

import stringflow.rta.BaseGame;
import stringflow.rta.libgambatte.IInjectCallback;

public abstract class Gen1Game extends BaseGame {
	
	private IInjectCallback primaryInjection;
	
	public Gen1Game(String symFilePath, int hJoypad, int hRandomAdd, int hRandomSub) {
		super(symFilePath, "./data/gen1/species_map.txt", "PlayTime", hRandomAdd, hRandomSub);
		primaryInjection = (gb, joypad) -> gb.write(hJoypad, joypad);
	}
	
	public IInjectCallback getPrimaryInjection() {
		return primaryInjection;
	}
}
