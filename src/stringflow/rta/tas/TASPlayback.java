package stringflow.rta.tas;

import stringflow.rta.InputDisplay;
import stringflow.rta.gen1.PokeYellow;
import stringflow.rta.libgambatte.Gb;
import stringflow.rta.libgambatte.LoadFlags;
import stringflow.rta.util.IO;
import stringflow.rta.util.TextFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

import static stringflow.rta.Joypad.*;

public class TASPlayback {

	public static void main(String args[]) throws Exception {
		final int joypadFlags[] = { UP, DOWN, LEFT, RIGHT, START, SELECT, B, A };
		
		TextFile textFile = IO.readText("tasInputs/yellow_glitchless.txt");
		byte buttons[] = new byte[textFile.size() - 3];
		for(int frameNumber = 2; frameNumber < textFile.size() - 1; frameNumber++) {
			for(int joypadIndex = 0; joypadIndex < joypadFlags.length; joypadIndex++) {
				if(textFile.get(frameNumber).charAt(joypadIndex + 1) != '.')  {
					buttons[frameNumber - 2] |= joypadFlags[joypadIndex];
				}
			}
		}
		
		Gb gb = new Gb();
		gb.loadBios("roms/gbc_bios.bin");
		gb.loadRom("roms/pokeyellow.gbc", new PokeYellow(), LoadFlags.CGB_MODE | LoadFlags.GBA_FLAG | LoadFlags.READONLY_SAV);
		gb.setInjectInputs(false);
		gb.createRenderContext(2);
//		gb.setOnDisplayUpdate(new InputDisplay());
		for(int i = 0; i < buttons.length; i++) {
			gb.press(buttons[i]);
		}
//		gb.destroy();
	}
}
