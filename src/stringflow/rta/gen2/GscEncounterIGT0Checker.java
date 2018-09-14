package stringflow.rta.gen2;

import stringflow.rta.IGTState;
import stringflow.rta.InputDisplay;
import stringflow.rta.encounterigt.EncounterIGTMap;
import stringflow.rta.libgambatte.Gb;
import stringflow.rta.libgambatte.LoadFlags;
import stringflow.rta.util.GSRUtils;
import stringflow.rta.util.IGTTimeStamp;
import stringflow.rta.util.IO;

import java.io.File;
import java.util.ArrayList;

import static stringflow.rta.Joypad.A;
import static stringflow.rta.Joypad.START;

public class GscEncounterIGT0Checker {

	private static Gb gb;
	private static Gen2Game game;

	public static void main(String args[]) {
		game = new PokeCrystal();


		/* 13 */ String path = "L L+A L L L L L L L L L L D D D D D D L L L L L D L "; int waitTime = 13;
		/* 14 */ //String path = "L L+A L L L L L L L L L L D D D D D D L L L L L D L L L L L L U L L U U U U U R R R R R U U U U L L L L L L L L L L L L L U L L U L L L L L D D L+A L L L L L L L L L L L L L L L L D L L L L L L L L U U "; int waitTime = 14;
		/* 15 */ //String path =   "L L+A L L L L L L L L L L D D D D D D L L L L L D L L L L L L L L U U U U U U R R R R R U U U L L L L L L L L L L L L L U U L L U L L L L L D D L L L L L+A L L L L L L L L D L L L L L L L L L L L U L L L L+A L U U L+A L L L L L L L L U U U"; int waitTime = 15;

		gb = new Gb();
		gb.loadBios("roms/gbc_bios.bin");
		gb.loadRom("roms/crystal_dvcheck.gbc", game, LoadFlags.CGB_MODE | LoadFlags.GBA_FLAG | LoadFlags.READONLY_SAV);
//		gb.setWarnOnZero(true);
//		gb.createRenderContext(1);
//		gb.setOnDisplayUpdate(new InputDisplay());

		gb.hold(START);
		gb.runUntil(0x100);
		byte saveState[] = gb.saveState();
		byte sram[] = new byte[0x8000];

		GSRUtils.decodeSAV(saveState, sram);
		GSRUtils.writeRTC(saveState,200);
		sram[0x2045] = (byte)0x11; // StartHour
		sram[0x2046] = (byte)0x3B; // StartMinute
		sram[0x2047] = (byte)0x00;  // StartSecond

		ArrayList<IGTState> initialStates = new ArrayList<>();
		gb.setInjectInputs(false);
		for(int j = 0; j < 1; j++) {
			for(int i = 0; i < 60; i++) {
				sram[0x2055] = (byte)j;
				sram[0x2056] = (byte)i;
				game.writeChecksum(sram);
				GSRUtils.encodeSAV(sram, saveState);
				gb.loadState(saveState);
				gb.hold(START);
				gb.runUntil("joypadCall");
				gb.frameAdvance();

				gb.runUntil("joypadCall");
				gb.frameAdvance();

				gb.hold(A);
				gb.runUntil("joypadCall");
				gb.frameAdvance();

				gb.hold(0);
				gb.runUntil("joypadCall");
				gb.frameAdvance(waitTime);
				gb.press(A);

				initialStates.add(new IGTState(new IGTTimeStamp(0, 0, j, i), gb.saveState()));
			}
		}
		gb.setInjectInputs(true);
		GscIGTChecker igtChecker = new GscIGTChecker(gb);
		EncounterIGTMap resultMap = igtChecker.checkIgt0(initialStates, path, GscIGTChecker.ADVANCE_TO_DVS | GscIGTChecker.CREATE_SAVE_STATES);
		EncounterIGTMap filteredMap = resultMap.filter(result -> result.getSpecies() == 0);
		for(File file : new  File("states").listFiles()) {
			file.delete();
		}
		filteredMap.forEach(result -> {
			IO.writeBin("states/" + result.getIgt().getFrames() + ".gqs", result.getSave());
		});
		resultMap.print(System.out, false, false);
		System.out.println(filteredMap.size() + "/60");
//		gb.destroy();
	}
}