package stringflow.rta.tas;

import stringflow.rta.Joypad;
import stringflow.rta.gen1.PokeYellow;
import stringflow.rta.libgambatte.Gb;
import stringflow.rta.libgambatte.LoadFlags;
import stringflow.rta.util.ArrayUtils;

import java.util.Arrays;

public class TIDFinder {
	
	private static final int target[] = {0x26D1, 0x26F1};
	
	private static int maxCost = 20;
	private static int numIntros = 4;
	
	private static Gb gb;
	
	public static void main(String args[]) {
		gb = new Gb();
		gb.loadBios("roms/gbc_bios.bin");
		gb.loadRom("roms/pokeyellow.gbc", new PokeYellow(), LoadFlags.CGB_MODE | LoadFlags.GBA_FLAG | LoadFlags.READONLY_SAV);
		System.out.println(Math.pow(maxCost, numIntros) + " tids");
		search(0, new int[numIntros], new byte[numIntros][]);
	}
	
	public static void search(int index, int frames[], byte saves[][]) {
		for(int frame = 0; frame < maxCost; frame++) {
			if(frame == 0) {
				gb.runUntil("joypad");
				saves[index] = gb.saveState();
			}
			gb.loadState(saves[index]);
			gb.frameAdvance(frame);
			gb.runUntil("joypad");
			gb.press(index % 2 == 0 ? Joypad.START : Joypad.A);
			frames[index] = frame;
			if(index == numIntros - 1) {
				gb.frameAdvance(29);
				int tid = gb.read("wPlayerId", 2);
				if(ArrayUtils.arrayContains(target, tid)) {
					System.out.printf("%s: %04X\n", Arrays.toString(frames), tid);
				}
			} else {
				search(index + 1, frames, saves);
			}
		}
	}
}
