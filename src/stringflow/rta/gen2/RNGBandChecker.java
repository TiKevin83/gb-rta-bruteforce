package stringflow.rta.gen2;

import stringflow.rta.libgambatte.Gb;
import stringflow.rta.libgambatte.LoadFlags;
import stringflow.rta.util.GSRUtils;
import stringflow.rta.util.IO;
import stringflow.rta.util.TextFile;

import java.util.HashMap;

import static stringflow.rta.Joypad.A;
import static stringflow.rta.Joypad.START;

public class RNGBandChecker {
	
	private static Gb gb;
	private static Gen2Game game;
	
	public static void main(String args[]) throws Exception {
		game = new PokeGoldSilver();
		
		int waitTime = 3;
		gb = new Gb();
		gb.loadBios("roms/gbc_bios.bin");
		gb.loadRom("roms/pokegold.gbc", game, LoadFlags.CGB_MODE | LoadFlags.GBA_FLAG | LoadFlags.READONLY_SAV);
		gb.setWarnOnZero(true);
//		gb.createRenderContext(2);
		
		gb.hold(START);
		gb.runUntil(0x100);
		byte saveState[] = gb.saveState();
		byte sram[] = new byte[0x8000];
		
		GSRUtils.decodeSAV(saveState, sram);
		GSRUtils.writeRTC(saveState, 0x9B2F, 570);
		sram[0x2044] = (byte)0x00;
		sram[0x2045] = (byte)0x0A;
		sram[0x2046] = (byte)0x39;
		sram[0x2047] = (byte)0x00;
		
		byte[][] initialSaves = new byte[60][];
		
		for(int i = 0; i < 60; i++) {
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
			initialSaves[i] = gb.saveState();
		}
		int errorMargin = 5;
		TextFile file = IO.readText("4.txt");
		pathLoop:
		for(String line : file) {
//			if(!line.endsWith("R R R U R R U U U U L L L L L L L L L L L L L U L L U L L L L D D D L L L D, cost: 3, owFrames: 1278 - 58/60")) {
//				continue;
//			}
			String path = line.substring(line.indexOf(":") + 2, line.indexOf(", cost"));
			path += " L L L L L U L L ";
			HashMap<Integer, Integer> rngBandCounter = new HashMap<Integer, Integer>();
			igtLoop:
			for(int i = 0; i < 60; i++) {
				int result = GscIGTChecker.rngBandCheck(gb, initialSaves[i], path, 0);
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