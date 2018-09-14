package stringflow.rta.gen1;

import stringflow.rta.IGTState;
import stringflow.rta.encounterigt.EncounterIGTMap;
import stringflow.rta.libgambatte.Gb;
import stringflow.rta.libgambatte.LoadFlags;
import stringflow.rta.parallel.ParallelIGTChecker;
import stringflow.rta.util.IGTTimeStamp;

import java.util.Collections;

public class RbyEncounterIGT0CheckerParallel {
	
	public static void main(String args[]) throws Exception {
		int numThreads = 3;
		Gen1Game game = new PokeRedBlue();
		long params = RbyIGTChecker.CREATE_SAVE_STATES | RbyIGTChecker.MONITOR_NPC_TIMERS | RbyIGTChecker.PICKUP_ESCAPE_ROPE | RbyIGTChecker.PICKUP_MEGA_PUNCH | RbyIGTChecker.PICKUP_RARE_CANDY | RbyIGTChecker.PICKUP_MOON_STONE | RbyIGTChecker.PICKUP_WATER_GUN | RbyIGTChecker.YOLOBALL | game.getSpecies("PARAS").getIndexNumber();
		String path = "R R R R R R R R U R R U U U U U A R R R R R R R R R R R R D D D D D R R R R R R R A R U U R R U U U U U U U U U ";
		path += "U R R R R U U U U U U U U U U R R R R R U ";
		path += "U U U U U U L L L L L A L L L L D D R R R R U U R R R A R R U U U U U U U R R R R R R R A U U U U U U U R R R D R D D D D D D D A D D D D D D D D A D R R R R R U R R R R U U U U U U U U R U L U U U U U A U U U U U U L L L U U U U U U U U L L L L L L D D L A L L L L L L L D D D D D D ";
		path += "L A L L A L L A L L A L D D ";
		path += "R R R U U U L A U R D D A D L A L L A D ";
		path += "R A R R A R R A R R A R U U ";
		path += "D D L D D D D L L L L L L L U L U U U U U L U U U U U U U U L L L U L D A D D R A R ";
		path += "D R R D D D D D D D D D D R R R A R R R R R R R R R R D R ";
		path += "R R U U U R A R R R D D R R R R R U A R U R A R R D D D D D D D D A L L L L D D D D D D D A D D L L L A L L L L L L L L L L L L L L L L A L L U U U U A U U A L U U U U U U U U ";
		
		Gb gbs[] = new Gb[numThreads];
		RbyIGTChecker igtCheckers[] = new RbyIGTChecker[numThreads];
		for(int i = 0; i < numThreads; i++) {
			gbs[i] = new Gb();
//			gbs[i].createRenderContext(2);
			gbs[i].loadBios("roms/gbc_bios.bin");
			gbs[i].loadRom("roms/pokered.gbc", game, LoadFlags.CGB_MODE | LoadFlags.GBA_FLAG | LoadFlags.READONLY_SAV);
			igtCheckers[i] = new RbyIGTChecker(gbs[i]);
		}
		
		// use the first thread to advance to the igt injection
		if(game instanceof PokeRedBlue) {
			game.getStrat("holdpal").execute(gbs[0]);
		}
		game.getStrat("gfSkip").execute(gbs[0]);
		game.getStrat("intro0").execute(gbs[0]);
		game.getStrat("title").execute(gbs[0]);
		gbs[0].runUntil("igtInject");
		byte igtInjectionState[] = gbs[0].saveState();
		
		ParallelIGTChecker igtChecker = new ParallelIGTChecker(numThreads, ((threadIndex, state, p, flags) -> {
			gbs[threadIndex].loadState(igtInjectionState);
			gbs[threadIndex].write("wPlayTimeSeconds", state.getIgt().getSeconds());
			gbs[threadIndex].write("wPlayTimeFrames", state.getIgt().getFrames());
			game.getStrat("cont").execute(gbs[threadIndex]);
			game.getStrat("cont").execute(gbs[threadIndex]);
			gbs[threadIndex].runUntil("enterMap");
			gbs[threadIndex].runUntil("joypadOverworld");
			
			// set the state after IGT injection
			state.setState(gbs[threadIndex].saveState());
			return igtCheckers[threadIndex].checkIGT0(Collections.singleton(state), p, flags);
		}));
		
		long startTime = System.currentTimeMillis();
		for(int second = 0; second < 1; second++) {
			for(int frame = 0; frame < 60; frame++) {
				IGTTimeStamp timeStamp = new IGTTimeStamp(0, 0, second, frame);
				// pass over null since the state will be set during the method call, we just care about the IGT for now
				igtChecker.checkIGTFrame(new IGTState(timeStamp, null), path, params);
			}
		}
		EncounterIGTMap result = igtChecker.flush();
		long endTime = System.currentTimeMillis();
		result.print(System.out, false, false);
		
		System.out.println("done in " + (endTime - startTime) + " ms");
		for(int i = 0; i < numThreads; i++) {
			gbs[i].destroy();
		}
	}
}
