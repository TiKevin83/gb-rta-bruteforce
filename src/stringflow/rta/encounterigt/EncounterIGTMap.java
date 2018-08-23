package stringflow.rta.encounterigt;

import stringflow.rta.Gender;
import stringflow.rta.GenderRatio;
import stringflow.rta.Species;
import stringflow.rta.libgambatte.Gb;
import stringflow.rta.util.IGTTimeStamp;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class EncounterIGTMap {
	
	private ArrayList<EncounterIGTResult> resultList;
	
	public EncounterIGTMap() {
		resultList = new ArrayList<>();
	}
	
	public EncounterIGTMap(Collection<EncounterIGTResult> collection) {
		resultList = new ArrayList<>(collection);
	}
	
	public void addResult(Gb gb, IGTTimeStamp timeStamp, ArrayList<Integer>[] npcTimers, byte save[], boolean yoloballs[]) {
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
		int hRandom = (gb.getRandomAdd() << 8) | gb.getRandomSub();
		resultList.add(new EncounterIGTResult(timeStamp, gb.read("wCurMap"), gb.read("wXCoord"), gb.read("wYCoord"), hRandom, npcTimersString, save, gb.getGame().getSpecies(gb.read("wEnemyMonSpecies")), Gender.GENDERLESS, gb.read("wEnemyMonLevel"), gb.read("wEnemyMonDVs", 2), yoloballs[0], yoloballs[1], yoloballs[2], yoloballs[3], false));
	}
	
	// gen 2 constructor
	public void addResult(IGTTimeStamp igt, int map, int x, int y, int rng, byte save[], Species species, int level, int dvUpper, int dvLower, boolean hitSpinner) {
		int genderDVs = (dvUpper & 0xF0) | (dvLower >> 4);
		Gender gender;
		if(species.getGenderRatio() == GenderRatio.GENDER_UNKNOWN)
			gender = Gender.GENDERLESS;
		else if(species.getGenderRatio() == GenderRatio.GENDER_F0)
			gender = Gender.MALE;
		else if(species.getGenderRatio() == GenderRatio.GENDER_F100)
			gender = Gender.FEMALE;
		else if(species.getGenderRatio().getCompareVal() < genderDVs)
			gender = Gender.MALE;
		else
			gender = Gender.FEMALE;
		resultList.add(new EncounterIGTResult(igt, map, x, y, rng, "", save, species, gender, level, (dvUpper << 8) | dvLower, false, false, false, false, hitSpinner));
	}
	
	public void addResult(EncounterIGTResult result) {
		resultList.add(result);
	}
	
	public void print(PrintStream target, boolean writeNpcTimers, boolean writeYoloballs) {
		visitAll((result) -> {
			int second = result.getIgt().getSeconds();
			int frame = result.getIgt().getFrames();
			if(result.getSpecies() == 0) {
				if(result.getHitSpinner()) {
					target.printf("[%d][%d] Hit spinner at [%d#%d,%d]; rng 0x%04X %s\n", second, frame, result.getMap(), result.getX(), result.getY(), result.getRNG(), writeNpcTimers ? "npctimers " + result.getNpcTimers() : "");
				} else {
					target.printf("[%d][%d] No encounter at [%d#%d,%d]; rng 0x%04X %s\n", second, frame, result.getMap(), result.getX(), result.getY(), result.getRNG(), writeNpcTimers ? "npctimers " + result.getNpcTimers() : "");
				}
			} else {
				target.printf("[%d][%d] Encounter at [%d#%d,%d]: %s%s lv%d DVs %04X rng 0x%04X %s %s %s %s %s\n", second, frame, result.getMap(), result.getX(), result.getY(), result.getGender() == Gender.GENDERLESS ? "" : result.getGender().getName() + " ", result.getSpeciesName(), result.getLevel(), result.getHexDVs(), result.getRNG(), writeYoloballs ? String.valueOf(result.getYoloball()) : "", writeYoloballs ? String.valueOf(result.getSelectYoloball()) : "", writeYoloballs ? String.valueOf(result.getRedbarYoloball()) : "", writeYoloballs ? String.valueOf(result.getRedbarYoloball()) : "", writeNpcTimers ? "npctimers " + result.getNpcTimers() : "");
			}
			target.flush();
		});
	}
	
	public EncounterIGTMap filter(Predicate<EncounterIGTResult> visitor) {
		return new EncounterIGTMap(resultList.stream().filter(visitor).collect(Collectors.toList()));
	}
	
	public int numResultsWithSaves() {
		return filter(result -> result.getSave() != null).size();
	}
	
	public int size() {
		return resultList.size();
	}
	
	public EncounterIGTResult getResult(int index) {
		return resultList.get(index);
	}
	
	public void visitAll(IEncounterIGTVisitor visitor) {
		for(EncounterIGTResult result : resultList) {
			visitor.onVisit(result);
		}
	}
}