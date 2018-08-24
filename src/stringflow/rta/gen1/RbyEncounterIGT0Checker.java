package stringflow.rta.gen1;

import stringflow.rta.IGTState;
import stringflow.rta.InputDisplay;
import stringflow.rta.encounterigt.EncounterIGTMap;
import stringflow.rta.encounterigt.EncounterIGTResult;
import stringflow.rta.libgambatte.Gb;
import stringflow.rta.libgambatte.LoadFlags;
import stringflow.rta.util.IGTTimeStamp;
import stringflow.rta.util.IO;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;

public class RbyEncounterIGT0Checker {
	
	public static void main(String args[]) throws Exception {
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
					  RbyIGTChecker.SELECT_YOLOBALL | game.getSpecies("PARAS").getIndexNumber();
		String path = "R R D D D D D D D D A D D D R R R R R R R R R R R A R R R D R R U U U R R R R D D R R R R R R U U R R R A D D D D D D D D L L L L D D D D D D D D A D L L L L L L L L L L L L L L L L L L L L ";
		path += "L L L U U U R U U U U U U U U U U U U ";
		PrintStream target = System.out;
		
		Gb gb = new Gb();
		gb.loadBios("roms/gbc_bios.bin");
		gb.loadRom("roms/pokeblue.gbc", game, LoadFlags.CGB_MODE | LoadFlags.GBA_FLAG | LoadFlags.READONLY_SAV);
		
		if(game instanceof PokeRedBlue) {
			game.getStrat("pal").execute(gb);
		}
		game.getStrat("gfSkip").execute(gb);
		game.getStrat("intro0").execute(gb);
		game.getStrat("title").execute(gb);
		gb.runUntil("igtInject");
		byte igtState[] = gb.saveState();
		ArrayList<IGTState> initialStates = new ArrayList<>();
		for(int second = 0; second < maxSecond; second++) {
			for(int frame = 0; frame < 60; frame++) {
				gb.loadState(igtState);
				gb.write("wPlayTimeSeconds", second);
				gb.write("wPlayTimeFrames", frame);
				game.getStrat("cont").execute(gb);
				game.getStrat("cont").execute(gb);
				gb.runUntil("joypadOverworld");
				initialStates.add(new IGTState(new IGTTimeStamp(0, 0, second, frame), gb.saveState()));
			}
		}
		EncounterIGTMap map = RbyIGTChecker.checkIGT0(gb, initialStates, path, params);
		EncounterIGTMap successMap = map.filter(EncounterIGTResult::getSelectYoloball);
//		for(File file : new File("states").listFiles()) {
//			file.delete();
//		}
//		successMap.visitAll(result -> {
//			IO.writeBin("states/" + result.getIgt().getFrames() + ".gqs", result.getSave());
//		});
		map.print(target, true, true);
		System.out.println(successMap.size() + "/60");
		gb.destroy();
	}
}
