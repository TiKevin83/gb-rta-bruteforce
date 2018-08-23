package stringflow.rta.gen1;

import stringflow.rta.StateBuffer;
import stringflow.rta.encounterigt.EncounterIGTMap;
import stringflow.rta.encounterigt.EncounterIGTResult;
import stringflow.rta.libgambatte.Gb;
import stringflow.rta.libgambatte.LoadFlags;
import stringflow.rta.util.IGTTimeStamp;
import stringflow.rta.util.IO;

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
					  RbyIGTChecker.SELECT_YOLOBALL | game.getSpecies("PARAS").getIndexNumber();
		String path = "U A U U U U U L L L L L L L L A L D D R U U U U U R R R R U R U U R U R R R R R R R R U U U U U U U R R R R D D R D D D D D A D D D D D D D D D D R R R R R R U R R R U U U U U U U U R U U U U U U U U U U U L U U U U U L L L U U U U L L L L L L D D L A L L L D D L L L L D D D D L L A L L D A D L A L L A L R R R U U U L U R D D D D L L L U R A R R A R R A R R A R U D D A D D D A L D L L L L A L U U L A L U U U U U U U U L L U A U U L U U L L L D A D R A R D D A D D R R D D D D D D D D D R R R R R R R R R R R R R R R R U U U R A R R R D D R R R R R R U U R R A R D D D D D D D D L L L L D D D D D D D D D L L L L L L L L L L L L L L L L L L L L L L U U U U U U U U U U U A U U U";
		PrintStream target = System.out;
		
		Gb gb = new Gb();
		gb.loadBios("roms/gbc_bios.bin");
		gb.loadRom("roms/pokered.gbc", game, LoadFlags.CGB_MODE | LoadFlags.GBA_FLAG | LoadFlags.READONLY_SAV);
//		gb.createRenderContext(2);
//		gb.setOnDisplayUpdate(new InputDisplay());
		
		if(game instanceof PokeRedBlue) {
			game.getStrat("abss").execute(gb);
		}
		game.getStrat("gfSkip").execute(gb);
		game.getStrat("intro0").execute(gb);
		game.getStrat("title").execute(gb);
		gb.runUntil("igtInject");
		byte igtState[] = gb.saveState();
		ArrayList<StateBuffer> initialStates = new ArrayList<>();
		for(int second = 0; second < maxSecond; second++) {
			for(int frame = 0; frame < 60; frame++) {
				gb.loadState(igtState);
				gb.write("wPlayTimeSeconds", second);
				gb.write("wPlayTimeFrames", frame);
				game.getStrat("cont").execute(gb);
				game.getStrat("cont").execute(gb);
				IO.writeBin("testsav.gqs", gb.saveState());
				gb.runUntil("joypadOverworld");
				initialStates.add(new StateBuffer(new IGTTimeStamp(0, 0, second, frame), gb.saveState()));
			}
		}
		EncounterIGTMap map = RbyIGTChecker.checkIGT0(gb, initialStates, path, params);
		EncounterIGTMap successMap = map.filter(EncounterIGTResult::getSelectYoloball);
		map.print(target, false, true);
		System.out.println(successMap.size() + "/60");
		gb.destroy();
	}
}
