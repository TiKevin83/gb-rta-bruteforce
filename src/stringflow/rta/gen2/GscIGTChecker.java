package stringflow.rta.gen2;

import stringflow.rta.Address;
import stringflow.rta.Failure;
import stringflow.rta.IGTState;
import stringflow.rta.encounterigt.EncounterIGTMap;
import stringflow.rta.libgambatte.Gb;
import stringflow.rta.ow.OverworldAction;
import stringflow.rta.util.IGTTimeStamp;
import stringflow.rta.util.IO;

import java.util.ArrayList;
import java.util.Collection;

import static stringflow.rta.Joypad.B;
import static stringflow.rta.Joypad.START;

public class GscIGTChecker {
	
	public static final int NONE = 0;
	public static final int CREATE_SAVE_STATES = 1;
	public static final int ADVANCE_TO_DVS = 2;
	
	private Gb gb;
	private Gen2Game game;
	private long flags;
	
	public GscIGTChecker(Gb gb) {
		this.gb = gb;
		this.game = (Gen2Game) gb.getGame();
	}
	
	public EncounterIGTMap checkIgt0(Collection<IGTState> initialSaves, String path, long flags) {
		this.flags = flags;
		EncounterIGTMap igtmap = new EncounterIGTMap();
		String actions[] = path.split(" ");
		outer:
		for(IGTState state : initialSaves) {
			IGTTimeStamp igt = state.getIgt();
			byte data[] = state.getState();
			if(data == null) {
				addIGTResult(igtmap, igt, true, false);
				continue;
			}
			gb.loadState(data);
			OverworldAction firstAction = OverworldAction.fromString(actions[0]);
			if(firstAction != OverworldAction.START_B) {
				gb.hold(firstAction.getJoypad());
				gb.runUntil("owplayerinput");
			}
			boolean enc = false;
			boolean hitSpinner = false;
			for(int j = 0; j < actions.length; j++) {
				Failure result = execute(OverworldAction.fromString(actions[j]));
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
	
	private void addIGTResult(EncounterIGTMap map, IGTTimeStamp igt, boolean encounter, boolean hitSpinner) {
		map.addResult(igt, gb.read("wMapGroup", 2), gb.read("wXCoord"), gb.read("wYCoord"), gb.getRandomAdd(), gb.getRandomSub(), gb.getRdiv(), !encounter && (flags & CREATE_SAVE_STATES) != 0 ? gb.saveState() : null, (flags & ADVANCE_TO_DVS) != 0 ? gb.getGame().getSpecies(gb.read("wEnemyMonSpecies")) : gb.getGame().getSpecies(encounter ? 1 : 0), gb.read("wEnemyMonLevel"), gb.read("wEnemyMonDVs"), gb.read(gb.getGame().getAddress("wEnemyMonDVs").getAddress() + 1), hitSpinner);
	}
	
	private Failure execute(OverworldAction action) {
		switch(action) {
			case UP:
			case LEFT:
			case RIGHT:
			case DOWN:
			case UP_A:
			case LEFT_A:
			case RIGHT_A:
			case DOWN_A:
				int input = action.getJoypad();
				gb.hold(input);
				Address result = gb.runUntil("countstep", "ChooseWildEncounter.startwildbattle", "ButtonSound");
				boolean turnFrameEncounter = true;
				if(result.equals("ButtonSound")) {
					return Failure.HIT_SPINNER;
				}
				if(result.equals("countStep")) {
					result = gb.runUntil("owplayerinput", "ChooseWildEncounter.startwildbattle", "ButtonSound");
					if(result.equals("ButtonSound")) {
						System.out.println("!");
						return Failure.HIT_SPINNER;
					}
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
				gb.runUntil("joypadCall");
				gb.hold(B, game.getMenuInjection());
				gb.frameAdvance();
				gb.runUntil("owplayerinput");
				return Failure.NO_FAILURE;
			default:
				return Failure.WRONG_ACTION;
		}
	}
}