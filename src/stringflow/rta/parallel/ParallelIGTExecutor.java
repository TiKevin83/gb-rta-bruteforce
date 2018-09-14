package stringflow.rta.parallel;

import stringflow.rta.IGTState;
import stringflow.rta.encounterigt.EncounterIGTMap;

public interface ParallelIGTExecutor {

	EncounterIGTMap checkIGTFrame(int threadIndex, IGTState state, String path, long flags);
}