package stringflow.rta.gen2;

import stringflow.rta.Address;
import stringflow.rta.encounterigt.EncounterIGTMap;
import stringflow.rta.libgambatte.Gb;
import stringflow.rta.util.IGTTimeStamp;
import stringflow.rta.util.IO;

import static stringflow.rta.Joypad.B;
import static stringflow.rta.Joypad.START;

public class GenericEncounterChecker {
	
	public static final int NONE = 0;
	public static final int CREATE_SAVE_STATES = 1;
	public static final int ADVANCE_TO_DVS = 2;
	
	private static Gb gb;
	private static long params;
	
	//TODO: Make this also work for Crystal
	public static EncounterIGTMap checkIGT0(Gb gb, byte initialSaves[][], String path, long params) {
		GenericEncounterChecker.gb = gb;
		GenericEncounterChecker.params = params;
		EncounterIGTMap igtmap = new EncounterIGTMap();
		String actions[] = path.split(" ");
		outer:
		for(int second = 0; second < 1; second++) {
			for(int frame = 0; frame < 60; frame++) {
				int index = frame + second * 60;
				if(initialSaves[index] == null) {
					addIGTResult(igtmap, second, frame, true);
					continue;
				}
				gb.loadState(initialSaves[index]);
				GscAction firstAction = GscAction.fromString(actions[0]);
				if(firstAction != GscAction.START_B) {
					gb.hold(firstAction.getJoypad());
					gb.advanceTo("owplayerinput");
				}
				boolean enc = false;
				for(int j = 0; j < actions.length; j++) {
					if(!execute(GscAction.fromString(actions[j]))) {
						enc = true;
						break;
					}
				}
				addIGTResult(igtmap, second, frame, enc);
			}
		}
		return igtmap;
	}
	
	public static int rngBandCheck(Gb gb, byte initialSave[], String path, long params) {
		GenericEncounterChecker.gb = gb;
		GenericEncounterChecker.params = params;
		String actions[] = path.split(" ");
		gb.loadState(initialSave);
		GscAction firstAction = GscAction.fromString(actions[0]);
		if(firstAction != GscAction.START_B) {
			gb.hold(firstAction.getJoypad());
			gb.advanceTo("owplayerinput");
		}
		for(int j = 0; j < actions.length; j++) {
			if(!execute(GscAction.fromString(actions[j]))) {
				return -1;
			}
		}
		return gb.getRandomState();
	}
	
	private static void addIGTResult(EncounterIGTMap map, int second, int frame, boolean encounter) {
		map.addResult(new IGTTimeStamp(0, 0, second, frame), gb.read("wMap", 2), gb.read("wXCoord"), gb.read("wYCoord"), gb.getRandomState(), !encounter && (params & CREATE_SAVE_STATES) != 0 ? gb.saveState() : null, (params & ADVANCE_TO_DVS) != 0 ? gb.getGame().getSpecies(gb.read("wEnemyMonSpecies")) : gb.getGame().getSpecies(encounter ? 1 : 0), gb.read("wEnemyMonLevel"), gb.read("wEnemyMonDVs"), gb.read(gb.getGame().getAddress("wEnemyMonDVs").getAddress() + 1));
	}
	
	private static boolean execute(GscAction action) {
		switch(action) {
			case UP:
			case LEFT:
			case RIGHT:
			case DOWN:
				int input = 16 * (int)(Math.pow(2.0, (action.ordinal())));
				gb.hold(input);
				gb.write(0xFFA9, input);
				gb.write(0xFFAA, input);
				Address result = gb.advanceTo("countstep", "ChooseWildEncounter.startwildbattle");
				boolean turnFrameEncounter = true;
				if(result.equals("countStep")) {
					result = gb.advanceTo("owplayerinput", "ChooseWildEncounter.startwildbattle");
					turnFrameEncounter = false;
				}
				if(result.equals("ChooseWildEncounter.startwildbattle") || turnFrameEncounter) {
					if((params & ADVANCE_TO_DVS) != 0) {
						gb.advanceTo("LoadEnemyMon.UpdateDVs");
						gb.frameAdvance(2);
					}
					return false;
				} else {
					return true;
				}
			case START_B:
				gb.hold(START);
				gb.write(0xFFA9, START);
				gb.write(0xFFAA, START);
				gb.advanceTo("joypadCall");
				gb.write(0xFFA6, B);
				gb.hold(B);
				gb.frameAdvance();
				gb.advanceTo("owplayerinput");
				return true;
			default:
				return false;
		}
	}
}