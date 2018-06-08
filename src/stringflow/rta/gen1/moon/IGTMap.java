package stringflow.rta.gen1.moon;

import stringflow.rta.GBWrapper;

import java.util.ArrayList;

public class IGTMap {

	private IGTResult resultMap[];
	
	public IGTMap(int size) {
		resultMap = new IGTResult[size * 60];
	}
	
	public void addResult(GBWrapper wrap, int index, ArrayList<Integer>[] npcTimers) {
		String npcTimersString = "";
		for(int i = 1; i < npcTimers.length; i++) {
			long timerId = 0x00000000;
			int expireCounter = 0;
			for(int j = 1; j < npcTimers[i].size(); j++) {
				int previousTimer = npcTimers[i].get(j - 1);
				int currentTimer = npcTimers[i].get(j);
				if(currentTimer > previousTimer) {
					timerId |= j << expireCounter++ * 8;
				}
			}
			npcTimersString += String.format("%08X", timerId);
			if(i != npcTimers.length - 1) {
				npcTimersString += "_";
			}
		}
		int hRandom = (wrap.getRandomAdd() << 8) | wrap.getRandomSub();
		resultMap[index] = new IGTResult(wrap.read("wCurMap"), wrap.read("wXCoord"), wrap.read("wYCoord"), hRandom, npcTimersString, wrap.read("wEnemyMonSpecies"),
												wrap.read("wEnemyMonLevel"), (wrap.read("wEnemyMonDVs") << 8) | (wrap.read(wrap.getAddress("wEnemyMonDVs") + 1)),
											    false, false, false, false);
	}
	
	public IGTResult getResult(int index) {
		return resultMap[index];
	}
	
	public int getSize() {
		return resultMap.length;
	}
}