package stringflow.rta;

import stringflow.rta.gen1.PokeRedBlue;
import stringflow.rta.gen2.Gen2Game;
import stringflow.rta.gen2.PokeCrystal;
import stringflow.rta.libgambatte.Gb;
import stringflow.rta.libgambatte.LoadFlags;
import stringflow.rta.util.IGTTimeStamp;
import stringflow.rta.util.IO;
import stringflow.rta.util.TextFile;

import java.util.ArrayList;

public class SpeedTest {
	
	private static Gb gb;
	private static Gen2Game game;
	
	public static void main(String args[]) {
		
		gb = new Gb();
		gb.loadBios("roms/gbc_bios.bin");
		gb.loadRom("roms/pokered.gbc", new PokeRedBlue(), LoadFlags.CGB_MODE | LoadFlags.GBA_FLAG | LoadFlags.READONLY_SAV);
		gb.createRenderContext(3);
//		gb.setOnDisplayUpdate(new InputDisplay());
		
		TextFile textFile = IO.readText("lorelei_swap.txt");
		ArrayList<Integer> buttons = new ArrayList<>();
		for(int i = 0; i < textFile.size(); i++) {
			int joypad = 0;
			if(textFile.get(i).contains("U")) {
				joypad |= Joypad.UP;
			}
			if(textFile.get(i).contains("S")) {
				joypad |= Joypad.START;
			}
			if(textFile.get(i).contains("A")) {
				joypad |= Joypad.A;
			}
			if(textFile.get(i).contains("B")) {
				joypad |= Joypad.B;
			}
			if(textFile.get(i).contains("D")) {
				joypad |= Joypad.DOWN;
			}
			if(textFile.get(i).contains("R")) {
				joypad |= Joypad.RIGHT;
			}
			if(textFile.get(i).contains("x")) {
				int index = textFile.get(i).indexOf("x") + 1;
				int number = Integer.valueOf(textFile.get(i).substring(index, textFile.get(i).length()));
				for(int j = 0; j < number; j++) {
					buttons.add(joypad);
				}
			} else {
				buttons.add(joypad);
			}
		}
		
		byte save[] = IO.readBin("pokered_1.gqs");
		gb.loadState(save);
		
		long lastTime = System.nanoTime();
		double nsPerFrameAdvance = 1_000_000_000D / 6000D;
		int frames = 0;
		long lastTimer = System.currentTimeMillis();
		double delta = 0;
		int buttonPointer = 0;
		while(buttonPointer < buttons.size()) {
			long now = System.nanoTime();
			delta += (now - lastTime) / nsPerFrameAdvance;
			lastTime = now;
			while(delta >= 1) {
				if(buttonPointer >= buttons.size()) {
					break;
				}
				gb.press(buttons.get(buttonPointer++));
				frames++;
				delta--;
			}
			if(System.currentTimeMillis() - lastTimer >= 1000) {
				lastTimer += 1000;
				System.out.println(frames + " frames");
				frames = 0;
			}
		}
		System.out.println(new IGTTimeStamp(gb).getTotalFrames());
//		gb.destroy();
	}
}