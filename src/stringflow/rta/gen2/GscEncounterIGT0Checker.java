package stringflow.rta.gen2;

import stringflow.rta.StateBuffer;
import stringflow.rta.encounterigt.EncounterIGTMap;
import stringflow.rta.libgambatte.Gb;
import stringflow.rta.libgambatte.LoadFlags;
import stringflow.rta.util.GSRUtils;
import stringflow.rta.util.IGTTimeStamp;

import java.util.ArrayList;

import static stringflow.rta.Joypad.A;
import static stringflow.rta.Joypad.START;

public class GscEncounterIGT0Checker {
	
	private static Gb gb;
	private static Gen2Game game;
	
	public static void main(String args[]) throws Exception {
		game = new PokeGoldSilver();
		
		
		String path = "L S_B R S_B L R S_B L R L R L R L U R L R L R L R L R L R L R L R L R L R L R L R L R L R L R L R L R L R L R L R L R L R L R L R L R L R L R L R L R L R L R L R L D R L R L R L R L R L R L R L R L R L R L R L U D R L R L R L R L R L R L R L R L R L R L R L R L R L R L R L R L R L R L R L R L R L R U L R L D R L R L R L R L R L R L R L R L R L U R L R L R L R L R L R L R L R L R L R L R L R L D R L R L R L R L R L R L R L R L R L R L R L R L R L R L R L R L R L R L R L R L R L R L R U L R L R L R L R L R L R D D";
		int waitTime = 1;
		
		gb = new Gb();
		gb.loadBios("roms/gbc_bios.bin");
		gb.loadRom("roms/pokegold.gbc", game, LoadFlags.CGB_MODE | LoadFlags.GBA_FLAG | LoadFlags.READONLY_SAV);
		gb.setWarnOnZero(true);
//		gb.createRenderContext(2);
//		gb.setOnDisplayUpdate(new InputDisplay());
		
		gb.hold(START);
		gb.runUntil(0x100);
		byte saveState[] = gb.saveState();
		byte sram[] = new byte[0x8000];
		
		GSRUtils.decodeSAV(saveState, sram);
		GSRUtils.writeRTC(saveState, 0x9B2F, 1290);
		sram[0x2044] = (byte)0x00;
		sram[0x2045] = (byte)0x0A;
		sram[0x2046] = (byte)0x39;
		sram[0x2047] = (byte)0x00;
		sram[0x2825] = (byte)(sram[0x2825] + 0x1);
		
		ArrayList<StateBuffer> initialStates = new ArrayList<>();
		for(int j = 0; j < 1; j++) {
			for(int i = 7; i < 8; i++) {
				sram[0x2056] = (byte)j;
				sram[0x2057] = (byte)i;
				writeChecksum(sram);
				GSRUtils.encodeSAV(sram, saveState);
				gb.loadState(saveState);
				gb.hold(START);
				gb.runUntil("joypadCall");
				gb.frameAdvance();
				
				gb.hold(START);
				gb.runUntil("joypadCall");
				gb.frameAdvance();
				
				gb.hold(START | A);
				gb.runUntil("joypadCall");
				gb.frameAdvance();
				
				gb.hold(START);
				gb.runUntil("joypadCall");
				gb.hold(START);
				gb.frameAdvance(waitTime);
				gb.press(A);
				initialStates.add(new StateBuffer(new IGTTimeStamp(0, 0, j, i), gb.saveState()));
			}
		}
		System.out.println("saves done");
		EncounterIGTMap resultMap = GscIGTChecker.checkIgt0(gb, initialStates, path, GscIGTChecker.ADVANCE_TO_DVS);
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