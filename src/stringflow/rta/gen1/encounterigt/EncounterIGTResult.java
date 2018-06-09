package stringflow.rta.gen1.encounterigt;

import stringflow.rta.gen1.data.Species;

import java.nio.ByteBuffer;

public class EncounterIGTResult {
	
	public static final int SIZE = 1 + 1 + 1 + 2 + (4 * 14) + 1 + 1 + 2 + 1 + 1 + 1 + 1;

	private int map;
	private int x;
	private int y;
	private int rng;
	private String npcTimers;
	private ByteBuffer save;
	private int species;
	private int level;
	private int dvs;
	private boolean yoloball;
	private boolean selectYoloball;
	private boolean redbarYoloball;
	private boolean redbarSelectYoloball;
	
	public EncounterIGTResult(int map, int x, int y, int rng, String npcTimers, ByteBuffer save, int species, int level, int dvs, boolean yoloball, boolean selectYoloball, boolean redbarYoloball, boolean redbarSelectYoloball) {
		this.map = map;
		this.x = x;
		this.y = y;
		this.rng = rng;
		this.npcTimers = npcTimers;
		this.save = save;
		this.species = species;
		this.level = level;
		this.dvs = dvs;
		this.yoloball = yoloball;
		this.selectYoloball = selectYoloball;
		this.redbarYoloball = redbarYoloball;
		this.redbarSelectYoloball = redbarSelectYoloball;
	}
	
	public int getMap() {
		return map;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getRNG() {
		return rng;
	}
	
	public String getNpcTimers() {
		return npcTimers;
	}
	
	public int[] getNpcTimersAsIntArray() {
		String splitArray[] = npcTimers.split("_");
		int result[] = new int[splitArray.length];
		for(int i = 0; i < splitArray.length; i++) {
			result[i] = Integer.decode("0x" + splitArray[i]);
		}
		return result;
	}
	
	public ByteBuffer getSave() {
		return save;
	}
	
	public void setSave(ByteBuffer save) {
		this.save = save;
	}
	
	public int getSpecies() {
		return species;
	}
	
	public String getSpeciesName() {
		return Species.getSpeciesByIndexNumber(species).getName();
	}
	
	public int getLevel() {
		return level;
	}
	
	public int getDvs() {
		return dvs;
	}
	
	public boolean getYoloball() {
		return yoloball;
	}
	
	public boolean getSelectYoloball() {
		return selectYoloball;
	}
	
	public boolean getRedbarYoloball() {
		return redbarYoloball;
	}
	
	public boolean getRedbarSelectYoloball() {
		return redbarSelectYoloball;
	}
}