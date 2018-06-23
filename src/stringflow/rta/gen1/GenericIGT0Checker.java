package stringflow.rta.gen1;

import mrwint.gbtasgen.Gb;
import stringflow.rta.GBWrapper;
import stringflow.rta.Util;
import stringflow.rta.gen1.data.Species;
import stringflow.rta.gen1.encounterigt.EncounterIGT0Checker;
import stringflow.rta.gen1.encounterigt.EncounterIGTMap;
import stringflow.rta.gen1.encounterigt.EncounterIGTResult;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.ByteBuffer;

import static stringflow.rta.gen1.PokeRedBlue.*;

public class GenericIGT0Checker {
	
	public static void main(String args[]) throws Exception {
		int maxSecond = 1;
		long params = EncounterIGT0Checker.YOLOBALL |
					  EncounterIGT0Checker.SELECT_YOLOBALL |
					  EncounterIGT0Checker.REDBAR_YOLOBALL |
					  EncounterIGT0Checker.REDBAR_SELECT_YOLOBALL |
					  Species.NIDORAN_MALE.getIndexNumber();
		boolean printNPCTimers = false;
		boolean writeStates = false;
		//String gameName = "yellow";
		//String path = "U R A R U";
		String gameName = "blue";
		String path = "L L L L L D S_A_B_S D S_B A D L L L U";
		PrintStream target = System.out;
		
		if(!new File("roms").exists()) {
			new File("roms").mkdir();
			System.err.println("I need ROMs to simulate!");
			System.exit(0);
		}
		if(!new File("states").exists()) {
			new File("states").mkdir();
		}
		if(!new File("roms/poke" + gameName + ".gbc").exists()) {
			System.err.println("Could not find poke" + gameName + ".gbc in roms directory!");
			System.exit(0);
		}
		if(!new File("roms/poke" + gameName + ".sym").exists()) {
			System.err.println("Could not find poke" + gameName + ".sym in roms directory!");
			System.exit(0);
		}
		if(writeStates) {
			for(File file : new File("states").listFiles()) {
				file.delete();
			}
		}
		Gb.loadGambatte(1);
		Gb gb = new Gb(0, false);
		gb.startEmulator("roms/poke" + gameName + ".gbc", true, 100);
		GBWrapper wrap = new GBWrapper(gb, "roms/poke" + gameName + ".sym", new int[] { hJoypad }, hRandomAdd, hRandomSub);
		if(!gameName.equalsIgnoreCase("yellow")) {
			PokeRedBlue.nopal.execute(wrap);
		}
		gfSkip.execute(wrap);
		intro0.execute(wrap);
		title.execute(wrap);
		wrap.advanceTo(igtInjectAddr);
		ByteBuffer igtState = gb.saveState();
		ByteBuffer initalStates[] = new ByteBuffer[maxSecond * 60];
		for(int second = 0; second < maxSecond; second++) {
			for(int frame = 0; frame < 60; frame++) {
				gb.loadState(igtState);
				wrap.write("wPlayTimeSeconds", second);
				wrap.write("wPlayTimeFrames", frame);
				cont.execute(wrap);
				cont.execute(wrap);
				wrap.advanceTo("joypadOverworld");
				initalStates[second * 60 + frame] = gb.saveState();
			}
		}
		//wrap.setSleepTime(5);
		EncounterIGTMap map = EncounterIGT0Checker.checkIGT0(wrap, initalStates, path, params);
		int successes = initalStates.length;
		for(int i = 0; i < map.getSize(); i++) {
			int second = i / 60;
			int frame = i % 60;
			EncounterIGTResult result = map.getResult(i);
			String rng = String.format("0x%4s", Integer.toHexString(result.getRNG()).toUpperCase()).replace(' ', '0');
			if(result.getSpecies() == 0) {
				target.printf("[%d][%d] No encounter at [%d#%d,%d]; rng %s %s\n", second, frame, result.getMap(), result.getX(), result.getY(), rng, printNPCTimers ? "npctimers " + result.getNpcTimers() : "");
				if(writeStates) {
					if(result.getSave() == null) {
						System.err.println("Write state files is enabled, however no save file has been saved for frame " + i);
					} else {
						Util.writeBytesToFile("./states/" + i + ".state", result.getSave());
					}
				}
			} else {
				target.printf("[%d][%d] Encounter at [%d#%d,%d]: %s lv%d DVs %04X rng %s %b %b %b %b %s\n", second, frame, result.getMap(), result.getX(), result.getY(), result.getSpeciesName(), result.getLevel(), result.getDvs(), rng, result.getYoloball(), result.getSelectYoloball(), result.getRedbarYoloball(), result.getSelectYoloball(), printNPCTimers ? "npctimers " + result.getNpcTimers() : "");
			}
			target.flush();
		}
		System.out.println(successes + "/" + initalStates.length);
	}
}