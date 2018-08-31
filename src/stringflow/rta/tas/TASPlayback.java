package stringflow.rta.tas;

import stringflow.rta.InputDisplay;
import stringflow.rta.gen1.PokeRedBlue;
import stringflow.rta.gen1.PokeYellow;
import stringflow.rta.libgambatte.Gb;
import stringflow.rta.libgambatte.LoadFlags;
import stringflow.rta.util.IO;
import stringflow.rta.util.TextFile;

import java.util.List;

import java.util.List;

import static stringflow.rta.Joypad.*;

public class TASPlayback {

	public static void main(String args[]) {
		final int joypadFlags[] = { UP, DOWN, LEFT, RIGHT, START, SELECT, B, A };
		
		TextFile textFile = IO.readText("tasInputs/yellow_gdqx.txt");
		List<String> frames = textFile.subList(2, textFile.size() - 1);
		byte buttons[] = new byte[frames.size()];
		for(int frameNumber = 0; frameNumber < frames.size(); frameNumber++) {
			for(int joypadIndex = 0; joypadIndex < joypadFlags.length; joypadIndex++) {
				if(frames.get(frameNumber).charAt(joypadIndex + 1) != '.')  {
					buttons[frameNumber] |= joypadFlags[joypadIndex];
				}
			}
		}
		Gb gb = new Gb();
		gb.loadBios("roms/gbc_bios.bin");
		gb.loadRom("roms/pokeyellow.gbc", new PokeYellow(), LoadFlags.CGB_MODE | LoadFlags.GBA_FLAG | LoadFlags.READONLY_SAV);
		gb.setInjectInputs(false);
		gb.createRenderContext(2);
		gb.setOnDisplayUpdate(new InputDisplay());
		for(int i = 0; i < buttons.length; i++) {
			gb.press(buttons[i]);
		}
//		gb.destroy();
	}
}
