package stringflow.rta.gen2;

import stringflow.rta.BaseGame;
import stringflow.rta.libgambatte.IInjectCallback;

public abstract class Gen2Game extends BaseGame {
	
	private IInjectCallback primaryInjection;
	private IInjectCallback menuInjection;
	
	public Gen2Game(String symFilePath, int hJoyPressed, int hJoyDown, int hJoypadDown, int hRandomAdd, int hRandomSub) {
		super(symFilePath, "./data/gen2/species_map.txt", "GameTime", hRandomAdd, hRandomSub);
		primaryInjection = (gb, joypad) -> {
			gb.write(hJoyPressed, joypad);
			gb.write(hJoyDown, joypad);
		};
		menuInjection = (gb, joypad) -> gb.write(hJoypadDown, joypad);
	}
	
	public IInjectCallback getPrimaryInjection() {
		return primaryInjection;
	}
	
	public IInjectCallback getMenuInjection() {
		return menuInjection;
	}
}
