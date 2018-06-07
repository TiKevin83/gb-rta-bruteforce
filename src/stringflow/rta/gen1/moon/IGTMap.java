package stringflow.rta.gen1.moon;

import stringflow.rta.GBWrapper;

public class IGTMap {

	private IGTResult resultMap[];
	
	public IGTMap(int size) {
		resultMap = new IGTResult[size * 60];
	}
	
	public void addResult(GBWrapper wrap, int index) {
		int hRandom = (wrap.getRandomAdd() << 8) | wrap.getRandomSub();
		resultMap[index] = new IGTResult(wrap.read("wCurMap"), wrap.read("wXCoord"), wrap.read("wYCoord"), hRandom, wrap.read("wEnemyMonSpecies"),
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