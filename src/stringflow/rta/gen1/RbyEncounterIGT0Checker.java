package stringflow.rta.gen1;

import stringflow.rta.Gender;
import stringflow.rta.IGTState;
import stringflow.rta.encounterigt.EncounterIGTMap;
import stringflow.rta.libgambatte.Gb;
import stringflow.rta.libgambatte.LoadFlags;
import stringflow.rta.util.IGTTimeStamp;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;

public class RbyEncounterIGT0Checker {
	
	public static void main(String args[]) {
		Gen1Game game = new PokeRedBlue();
		int maxSecond = 1;
		long params = RbyIGTChecker.MONITOR_NPC_TIMERS |
				RbyIGTChecker.PICKUP_ESCAPE_ROPE |
				RbyIGTChecker.PICKUP_MEGA_PUNCH |
				RbyIGTChecker.PICKUP_RARE_CANDY |
				RbyIGTChecker.PICKUP_MOON_STONE |
				RbyIGTChecker.PICKUP_WATER_GUN |
				RbyIGTChecker.MONITOR_NPC_TIMERS |
				RbyIGTChecker.CREATE_SAVE_STATES |
				RbyIGTChecker.YOLOBALL | 109;
		String path = "";

//		path += "R R D D D D D D D D A D D D R R R R R R R R R R R A R R R D ";
//		path += "R R U U U R R R R D D R R R R R R U U R R R A D D D D D D D D L L L L D D D D D D D D D L L L A L L L L L L L L L L L L L L L L L L ";
//		path += "L U U U A U U A U U U U U U U U D U";
		
		PrintStream target = System.out;
		
		Gb gb = new Gb();
		gb.loadBios("roms/gbc_bios.bin");
		gb.loadRom("roms/pokered.gbc", game, LoadFlags.CGB_MODE | LoadFlags.GBA_FLAG | LoadFlags.READONLY_SAV);
		gb.createRenderContext(2);
		
		if(game instanceof PokeRedBlue) {
			game.getStrat("holdpal").execute(gb);
		}
		game.getStrat("gfSkip").execute(gb);
		game.getStrat("intro0").execute(gb);
		game.getStrat("title").execute(gb);
		gb.runUntil("igtInject");
		byte igtState[] = gb.saveState();
		ArrayList<IGTState> initialStates = new ArrayList<>();
		for(int second = 0; second < maxSecond; second++) {
			for(int frame = 0; frame < 1; frame++) {
				gb.loadState(igtState);
				gb.write("wPlayTimeSeconds", second);
				gb.write("wPlayTimeFrames", frame);
				game.getStrat("cont").execute(gb);
				game.getStrat("cont").execute(gb);
				gb.runUntil("enterMap");
				gb.runUntil("joypadOverworld");
				initialStates.add(new IGTState(new IGTTimeStamp(0, 0, second, frame), gb.saveState()));
			}
		}
		RbyIGTChecker igtChecker = new RbyIGTChecker(gb);
		EncounterIGTMap map = igtChecker.checkIGT0(initialStates, path, params);
		EncounterIGTMap successMap = map.filter(result -> result.getSpecies() == 0);
		map.print(target, false, true);
		System.out.println(successMap.size() + "/60");
//		gb.destroy();
	}
}
