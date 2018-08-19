package stringflow.rta.gen2;

import stringflow.rta.AspectTest;
import stringflow.rta.InputDisplay;
import stringflow.rta.encounterigt.EncounterIGTMap;
import stringflow.rta.libgambatte.Gb;
import stringflow.rta.libgambatte.LoadFlags;
import stringflow.rta.util.Comparison;
import stringflow.rta.util.GSRUtils;
import stringflow.rta.util.IO;

import static stringflow.rta.Joypad.A;
import static stringflow.rta.Joypad.START;

public class EncounterIGT0Checker {
	
	private static Gb gb;
	private static Gen2Game game;
	
	public static void main(String args[]) throws Exception {
		game = new PokeGoldSilver();
		
		String path = "L L L L L L L L L L L L D D D D D D S_B L L L L L";
//		path += "L L L L L U L L L L L L L L L L L L L L L L L L L L U L L L L L L S_B L L U U L L L U U U U U U U U U U R R R R S_B R R U U U U U U U U U U U U U U U R U U U U U U U U U L L U U L U U U U U R U U U U";
		int waitTime = 1;
//		int menuAccount = 1;
		
		gb = new Gb();
		gb.loadBios("roms/gbc_bios.bin");
		gb.loadRom("roms/pokegold.gbc", game, LoadFlags.CGB_MODE | LoadFlags.GBA_FLAG | LoadFlags.READONLY_SAV);
		gb.setWarnOnZero(true);
		gb.createRenderContext(2);
		gb.setOnDisplayUpdate(new InputDisplay());
		
		gb.hold(START);
		gb.advanceTo(0x100);
		byte saveState[] = gb.saveState();
		byte sram[] = new byte[0x8000];
		
		GSRUtils.decodeSAV(saveState, sram);
		GSRUtils.writeRTC(saveState, 0x9B2F, 570);
//		sram[0x2006] = (byte)0x00;
		sram[0x2044] = (byte)0x00;
		sram[0x2045] = (byte)0x0A;
		sram[0x2046] = (byte)0x39;
		sram[0x2047] = (byte)0x00;
		
		byte[][] initialSaves = new byte[60][];
		for(int j = 0; j < 1; j++) {
			for(int i = 0; i < 60; i++) {
				sram[0x2056] = (byte)j;
				sram[0x2057] = (byte)i;
				writeChecksum(sram);
				GSRUtils.encodeSAV(sram, saveState);
				gb.loadState(saveState);
				gb.hold(START);
				gb.advanceTo("joypadCall");
				gb.frameAdvance();
				
				gb.hold(START);
				gb.advanceTo("joypadCall");
				gb.frameAdvance();
				
				gb.hold(START | A);
				gb.advanceTo("joypadCall");
				gb.frameAdvance();
				
				gb.hold(START);
				gb.advanceTo("joypadCall");
				gb.hold(START);
				gb.frameAdvance(waitTime);
				gb.press(A);
				initialSaves[i + j * 60] = gb.saveState();
			}
		}
		System.out.println("saves done");
		EncounterIGTMap resultMap = GenericEncounterChecker.checkIGT0(gb, initialSaves, path, GenericEncounterChecker.ADVANCE_TO_DVS);
		EncounterIGTMap filteredMap = resultMap.filterSpecies(Comparison.EQUAL, 0);
		resultMap.print(System.out, false, false);
		System.out.println(filteredMap.length() + "/60");
		gb.destroy();
	}
	
	private static void writeChecksum(byte sram[]) throws Exception {
		int checksum = 0;
		for(int i = 0x2009; i < 0x2D69; i++) {
			checksum += (sram[i] & 0xFF);
		}
		sram[0x2D69] = (byte)((checksum >> 0) & 0xFF);
		sram[0x2D6A] = (byte)((checksum >> 8) & 0xFF);
	}
}