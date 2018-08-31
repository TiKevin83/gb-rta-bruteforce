package stringflow.rta.gen2;

import stringflow.rta.IGTState;
import stringflow.rta.InputDisplay;
import stringflow.rta.libgambatte.Gb;
import stringflow.rta.libgambatte.LoadFlags;
import stringflow.rta.util.GSRUtils;
import stringflow.rta.util.IGTTimeStamp;
import stringflow.rta.util.IO;
import stringflow.rta.util.TextFile;

import java.util.ArrayList;
import java.util.HashMap;

import static stringflow.rta.Joypad.A;
import static stringflow.rta.Joypad.START;

public class RNGBandChecker {
	
	private static Gb gb;
	private static Gen2Game game;
	
	public static void main(String args[]) throws Exception {
		game = new PokeCrystal();
		
//		path += "L L L L L L L L L L L L L L L L L L L U L L L L L L L L U U L L L U U U U U U U U U U R R S_B R R R U U U S_B U U U U";
		int waitTime = 3;
		
		gb = new Gb();
		gb.loadBios("roms/gbc_bios.bin");
		gb.loadRom("roms/crystal_dvcheck.gbc", game, LoadFlags.CGB_MODE | LoadFlags.GBA_FLAG | LoadFlags.READONLY_SAV);
//		gb.setWarnOnZero(true);
//		gb.createRenderContext(2);
//		gb.setOnDisplayUpdate(new InputDisplay());
		
		gb.hold(START);
		gb.runUntil(0x100);
		byte saveState[] = gb.saveState();
		byte sram[] = new byte[0x8000];
		
		GSRUtils.decodeSAV(saveState, sram);
		GSRUtils.writeRTC(saveState,600);
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
		int errorMargin = 5;
		TextFile file = IO.readText("4.txt");
		pathLoop:
		for(String line : file) {
//			if(!line.endsWith("R R R U R R U U U U L L L L L L L L L L L L L U L L U L L L L D D D L L L D, cost: 3, owFrames: 1278 - 58/60")) {
//				continue;
//			}
			String path = "L " + line.substring(line.indexOf(":") + 2, line.indexOf(", cost")) + " L L L L L U L";
			HashMap<Integer, Integer> rngBandCounter = new HashMap<Integer, Integer>();
			igtLoop:
			for(int i = 0; i < 60; i++) {
				int result = GscIGTChecker.rngBandCheck(gb, initialStates.get(i).getState(), path, 0);
				if(result == -1) {
					continue;
				}
				int upperResult = (result >> 8) & 0xFF;
				int lowerResult = result & 0xFF;
				for(Integer rngBand : rngBandCounter.keySet()) {
					int upperCompare = (rngBand >> 8) & 0xFF;
					int lowerCompare = rngBand & 0xFF;
					if(upperResult >= upperCompare - errorMargin && upperResult <= upperCompare + errorMargin && lowerResult >= lowerCompare - errorMargin && lowerResult <= lowerCompare + errorMargin) {
						rngBandCounter.put(rngBand, rngBandCounter.get(rngBand) + 1);
						continue igtLoop;
					}
				}
				rngBandCounter.put(result, 1);
				if(rngBandCounter.size() > 1) {
					continue pathLoop;
				}
			}
			if(rngBandCounter.size() == 1) {
				System.out.println(line);
			}
		}
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