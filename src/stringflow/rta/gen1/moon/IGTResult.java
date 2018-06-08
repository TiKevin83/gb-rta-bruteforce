package stringflow.rta.gen1.moon;

import stringflow.rta.gen1.data.Species;

public class IGTResult {

	private int map;
	private int x;
	private int y;
	private int rng;
	private String npcTimers;
	private int species;
	private int level;
	private int dvs;
	private boolean yoloball;
	private boolean selectYoloball;
	private boolean redbarYoloball;
	private boolean redbarSelectYoloball;
	
	public IGTResult(int map, int x, int y, int rng, String npcTimers, int species, int level, int dvs, boolean yoloball, boolean selectYoloball, boolean redbarYoloball, boolean redbarSelectYoloball) {
		this.map = map;
		this.x = x;
		this.y = y;
		this.rng = rng;
		this.npcTimers = npcTimers;
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