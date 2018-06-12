package stringflow.rta.gen2;

import mrwint.gbtasgen.Gb;
import stringflow.rta.GBWrapper;
import stringflow.rta.LibgambatteBuilder;
import stringflow.rta.Util;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.LinkedHashMap;

import static stringflow.rta.Joypad.*;

public class TotodileChecker {
	
	public static final int SOUND_MONO = 0xC1;
	public static final int SOUND_STEREO = 0xE1;
	
	public static void main(String args[]) throws Exception {
		String gameName = "gold";
		int rtcOffset = 100;
		int startSecond = 0;
		int sound = SOUND_STEREO;
		int minFrame = 163;
		int maxFrame = 300;
		boolean female = true;
		boolean writeToFile = true;
		int verbose = 1;
		
		String srcFile;
		int sGameData;
		int sGameDataEnd;
		int sChecksum;
		int wGameData;
		int wStartSecond;
		int wGameTimeFrames;
		int wOptions;
		int wPartyMon1DVs;
		int joypad;
		int manualTextScroll;
		int hJoypad;
		int hRandomAdd;
		int hRandomSub;
		if(gameName.equalsIgnoreCase("gold") || gameName.equalsIgnoreCase("silver")) {
			srcFile = "baseSaves/gs_totodile.sav";
			sGameData = 0x2009;
			sGameDataEnd = 0x2D69;
			sChecksum = 0x2D69;
			wGameData = 0xD1A1;
			wStartSecond = 0xD1DF;
			wGameTimeFrames = 0xD1EF;
			wOptions = 0xD198;
			wPartyMon1DVs = 0xDA3F;
			joypad = 0x0940;
			manualTextScroll = 0x0A60;
			hJoypad = 0xFFA6;
			hRandomAdd = 0xFFE3;
			hRandomSub = 0xFFE4;
		} else if(gameName.equalsIgnoreCase("crystal")) {
			srcFile = female ? "baseSaves/c_totodile_f.sav" : "baseSaves/c_totodile_m.sav";
			sGameData = 0x2009;
			sGameDataEnd = 0x2B83;
			sChecksum = 0x2D0D;
			wGameData = 0xD47B;
			wStartSecond = 0xD4B9;
			wGameTimeFrames = 0xD4C8;
			wOptions = 0xD472;
			wPartyMon1DVs = 0xDCF4;
			joypad = 0x098F;
			manualTextScroll = 0x0AAF;
			hJoypad = 0xFFA4;
			hRandomAdd = 0xFFE1;
			hRandomSub = 0xFFE2;
		} else {
			throw new RuntimeException("Unknown game: " + gameName);
		}
		
		LibgambatteBuilder.buildGambatte(false, rtcOffset);
		Gb.loadGambatte(1);
		Gb gb;
		GBWrapper wrap;
		String name = String.format("%s rtc%d ss%d %s %d-%d %s\n", gameName, rtcOffset, startSecond, sound == SOUND_MONO ? "mono" : "stereo", minFrame, maxFrame - 1, gameName.equalsIgnoreCase("crystal") ? (female ? "female" : "male") : "").trim();
		PrintStream target = writeToFile ? new PrintStream("./" + name.replace(" ", "_") + ".txt") : System.out;
		target.println(name);
		for(int advanceFrame = minFrame; advanceFrame < maxFrame; advanceFrame++) {
			HashMap<Totodile, Integer> totodileMap = new HashMap<Totodile, Integer>();
			Totodile igtMap[] = new Totodile[60];
			for(int igtFrame = 0; igtFrame < 60; igtFrame++) {
				makeSave(srcFile, "roms/poke" + gameName + ".sav", sGameData, sGameDataEnd, sChecksum, wGameData, wStartSecond, wGameTimeFrames, wOptions, startSecond, igtFrame, sound);
				gb = new Gb(0, false);
				gb.startEmulator("roms/poke" + gameName + ".gbc");
				wrap = new GBWrapper(gb, "", hJoypad, hRandomAdd, hRandomSub);
				wrap.hold(SELECT);
				wrap.advanceTo(joypad);
				wrap.hold(START);
				wrap.advanceFrame();
				wrap.advanceTo(joypad);
				wrap.advanceFrame();
				wrap.advanceTo(joypad);
				wrap.hold(SELECT);
				wrap.press(A);
				wrap.advanceTo(joypad);
				wrap.advance(advanceFrame);
				wrap.hold(A);
				wrap.advance(65);
				wrap.hold(B);
				wrap.advance(120);
				wrap.hold(A);
				wrap.advanceTo(manualTextScroll);
				wrap.press(B);
				wrap.hold(B);
				wrap.advance(23);
				wrap.press(A);
				wrap.hold(A);
				wrap.advanceTo(manualTextScroll);
				wrap.press(B);
				wrap.hold(B);
				wrap.advanceTo(manualTextScroll);
				wrap.press(A);
				wrap.hold(A);
				wrap.advanceTo(manualTextScroll);
				wrap.press(B);
				wrap.hold(B);
				wrap.advanceTo(manualTextScroll);
				Totodile totodile = new Totodile((wrap.read(wPartyMon1DVs) << 8) | wrap.read(wPartyMon1DVs + 1));
				igtMap[igtFrame] = totodile;
				totodileMap.put(totodile, (totodileMap.containsKey(totodile) ? totodileMap.get(totodile) : 0) + 1);
			}
			if(verbose == 0) {
				target.print("[" + advanceFrame + "] ");
				target.println(totodileMap);
			} else if(verbose == 1) {
				LinkedHashMap <Totodile, Integer> sortedMap = new LinkedHashMap<Totodile, Integer>(Util.sortByValue(totodileMap, true));
				Totodile mostCommon = sortedMap.entrySet().iterator().next().getKey();
				String igtString = new String(new char[(int)(Math.log10(advanceFrame)+1) + 3]).replace('\0', ' ') + "IGT: ";
				for(Totodile totodile : sortedMap.keySet()) {
					igtString += String.format("%d/60=%04X, ", sortedMap.get(totodile), totodile.getDVs());
				}
				target.print("[" + advanceFrame + "] ");
				target.printf("DVs: 0x%04X (%d/%d/%d/%d/%d)\n", mostCommon.getDVs(), mostCommon.getHP(), mostCommon.getAttack(), mostCommon.getDefense(), mostCommon.getSpecialAttack(), mostCommon.getSpecialDefense(), mostCommon.getSpeed());
				target.println(igtString.substring(0, igtString.length() - 2));
			} else if(verbose >= 1) {
				target.println("FRAME " + advanceFrame);
				for(int i = 0; i < 60; i++) {
					target.printf("[%d] Totodile: 0x%04X (%d/%d/%d/%d/%d)\n", i, igtMap[i].getDVs(), igtMap[i].getHP(), igtMap[i].getAttack(), igtMap[i].getDefense(), igtMap[i].getSpecialAttack(), igtMap[i].getSpecialDefense(), igtMap[i].getSpeed());
				}
			}
			target.flush();
		}
	}
	
	private static void makeSave(String src, String dest, int sGameData, int sGameDataEnd, int sChecksum, int wGameData, int wStartSecond, int wGameTimeFrames, int wOptions, int startSecond, int igtFrame, int options) throws Exception {
		int offset = wGameData - sGameData;
		byte baseSave[] = Util.readBytesFromFile(src);
		baseSave[wStartSecond - offset] = (byte)startSecond;
		baseSave[wGameTimeFrames - offset] = (byte)igtFrame;
		baseSave[wOptions - offset] = (byte)options;
		int checksum = 0;
		for(int i = sGameData; i < sGameDataEnd; i++) {
			checksum += (baseSave[i] & 0xFF);
		}
		baseSave[sChecksum + 0] = (byte)((checksum >> 0) & 0xFF);
		baseSave[sChecksum + 1] = (byte)((checksum >> 8) & 0xFF);
		Util.writeBytesToFile(dest, baseSave);
	}
}
