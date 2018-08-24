package stringflow.rta.gen2;

import stringflow.rta.Address;
import stringflow.rta.Failure;
import stringflow.rta.IGTState;
import stringflow.rta.encounterigt.EncounterIGTMap;
import stringflow.rta.libgambatte.Gb;
import stringflow.rta.util.IGTTimeStamp;

import java.util.ArrayList;

import static stringflow.rta.Joypad.B;
import static stringflow.rta.Joypad.START;

public class GscIGTChecker {
	
	public static final int NONE = 0;
	public static final int CREATE_SAVE_STATES = 1;
	public static final int ADVANCE_TO_DVS = 2;
	
	private static Gb gb;
	private static long flags;
	
	//TODO: Make this also work for Crystal
	public static EncounterIGTMap checkIgt0(Gb gb, ArrayList<IGTState> initialSaves, String path, long flags) {
		GscIGTChecker.gb = gb;
		GscIGTChecker.flags = flags;
		EncounterIGTMap igtmap = new EncounterIGTMap();
		String actions[] = path.split(" ");
		outer:
		for(IGTState state : initialSaves) {
			IGTTimeStamp igt = state.getIgt();
			byte data[] = state.getData();
			if(state == null) {
				addIGTResult(igtmap, igt, true, false);
				continue;
			}
			gb.loadState(data);
			GscAction firstAction = GscAction.fromString(actions[0]);
			if(firstAction != GscAction.START_B) {
				gb.hold(firstAction.getJoypad());
				gb.runUntil("owplayerinput");
			}
			boolean enc = false;
			boolean hitSpinner = false;
			for(int j = 0; j < actions.length; j++) {
				Failure result = execute(GscAction.fromString(actions[j]));
				if(result == Failure.ENCOUNTER) {
					enc = true;
				}
				if(result == Failure.HIT_SPINNER) {
					hitSpinner = true;
				}
				if(result != Failure.NO_FAILURE) {
					break;
				}
			}
			addIGTResult(igtmap, igt, enc, hitSpinner);
		}
		return igtmap;
	}
	
	public static int rngBandCheck(Gb gb, byte initialSave[], String path, long flags) {
		GscIGTChecker.gb = gb;
		GscIGTChecker.flags = flags;
		String actions[] = path.split(" ");
		gb.loadState(initialSave);
		GscAction firstAction = GscAction.fromString(actions[0]);
		if(firstAction != GscAction.START_B) {
			gb.hold(firstAction.getJoypad());
			gb.runUntil("owplayerinput");
		}
		for(int j = 0; j < actions.length; j++) {
			if(execute(GscAction.fromString(actions[j])) != Failure.NO_FAILURE) {
				return -1;
			}
		}
		return gb.getRandomState();
	}
	
	private static void addIGTResult(EncounterIGTMap map, IGTTimeStamp igt, boolean encounter, boolean hitSpinner) {
		map.addResult(igt, gb.read("wMap", 2), gb.read("wXCoord"), gb.read("wYCoord"), gb.getRandomState(), !encounter && (flags & CREATE_SAVE_STATES) != 0 ? gb.saveState() : null, (flags & ADVANCE_TO_DVS) != 0 ? gb.getGame().getSpecies(gb.read("wEnemyMonSpecies")) : gb.getGame().getSpecies(encounter ? 1 : 0), gb.read("wEnemyMonLevel"), gb.read("wEnemyMonDVs"), gb.read(gb.getGame().getAddress("wEnemyMonDVs").getAddress() + 1), hitSpinner);
	}
	
	private static Failure execute(GscAction action) {
		switch(action) {
			case UP:
			case LEFT:
			case RIGHT:
			case DOWN:
				int input = 16 * (int)(Math.pow(2.0, (action.ordinal())));
				gb.hold(input);
				gb.write(0xFFA9, input);
				gb.write(0xFFAA, input);
				Address result = gb.runUntil("countstep", "ChooseWildEncounter.startwildbattle", "ButtonSound");
				boolean turnFrameEncounter = true;
				if(result.equals("ButtonSound")) {
					return Failure.HIT_SPINNER;
				}
				if(result.equals("countStep")) {
					result = gb.runUntil("owplayerinput", "ChooseWildEncounter.startwildbattle");
					turnFrameEncounter = false;
				}
				if(result.equals("ChooseWildEncounter.startwildbattle") || turnFrameEncounter) {
					if((flags & ADVANCE_TO_DVS) != 0) {
						gb.runUntil("LoadEnemyMon.UpdateDVs");
						gb.frameAdvance(2);
					}
					return Failure.ENCOUNTER;
				} else {
					return Failure.NO_FAILURE;
				}
			case START_B:
				gb.hold(START);
				gb.write(0xFFA9, START);
				gb.write(0xFFAA, START);
				gb.runUntil("joypadCall");
				gb.write(0xFFA6, B);
				gb.hold(B);
				gb.frameAdvance();
				gb.runUntil("owplayerinput");
				return Failure.NO_FAILURE;
			default:
				return Failure.WRONG_ACTION;
		}
	}
}