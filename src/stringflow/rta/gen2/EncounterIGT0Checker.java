package stringflow.rta.gen2;

import stringflow.rta.encounterigt.EncounterIGTMap;
import stringflow.rta.libgambatte.Gb;
import stringflow.rta.libgambatte.LoadFlags;
import stringflow.rta.util.GSRUtils;
import stringflow.rta.util.IO;

import static stringflow.rta.Joypad.*;

public class EncounterIGT0Checker {
	
	private static Gb gb;
	private static Gen2Game game;
	
	public static void main(String args[]) throws Exception {
		game = new PokeGoldSilver();
		String path = "L L L L L L L L L L L L L ";
		
		int waitTime = 2;
		gb = new Gb();
		gb.loadBios("roms/gbc_bios.bin");
		gb.loadRom("roms/pokegold.gbc", game, LoadFlags.CGB_MODE | LoadFlags.GBA_FLAG | LoadFlags.READONLY_SAV);
		gb.createRenderContext(2);
		gb.setWarnOnZero(true);
		
		gb.hold(START);
		gb.advanceTo(0x100);
		byte saveState[] = gb.saveState();
		byte sram[] = new byte[0x8000];
		
		GSRUtils.decodeSAV(saveState, sram);
		GSRUtils.writeRTC(saveState, 0x9B2F, 300);
		sram[0x2044] = (byte) 0x00;
		sram[0x2045] = (byte) 0x0A;
		sram[0x2046] = (byte) 0x39;
		sram[0x2047] = (byte) 0x00;
		
		byte[][] initialSaves = new byte[60][];
		
		for(int i = 0; i < 1; i++) {
			sram[0x2057] = (byte) i;
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
			System.out.println(gb.getGbpTime());
			IO.writeBin("listless.gqs", gb.saveState());
			initialSaves[i] = gb.saveState();
		}
		EncounterIGTMap resultMap = GenericEncounterChecker.checkIGT0(gb, initialSaves, path, GenericEncounterChecker.ADVANCE_TO_DVS);
		resultMap.print(System.out, false, false);
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