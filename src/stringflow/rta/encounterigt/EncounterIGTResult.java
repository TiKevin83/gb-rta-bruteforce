package stringflow.rta.encounterigt;

import stringflow.rta.DVs;
import stringflow.rta.Gender;
import stringflow.rta.Species;
import stringflow.rta.util.IGTTimeStamp;

import java.nio.ByteBuffer;

public class EncounterIGTResult {
	
	private IGTTimeStamp igt;
	private int map;
	private int x;
	private int y;
	private int rng;
	private String npcTimers;
	private byte save[];
	private Species species;
	private Gender gender;
	private int level;
	private DVs dvs;
	private boolean yoloball;
	private boolean selectYoloball;
	private boolean redbarYoloball;
	private boolean redbarSelectYoloball;
	
	public EncounterIGTResult(IGTTimeStamp igt, int map, int x, int y, int rng, String npcTimers, byte save[], Species species, Gender gender, int level, int dvs, boolean yoloball, boolean selectYoloball, boolean redbarYoloball, boolean redbarSelectYoloball) {
		this.igt = igt;
		this.map = map;
		this.x = x;
		this.y = y;
		this.rng = rng;
		this.npcTimers = npcTimers;
		this.save = save;
		this.species = species;
		this.gender = gender;
		this.level = level;
		this.dvs = new DVs(dvs);
		this.yoloball = yoloball;
		this.selectYoloball = selectYoloball;
		this.redbarYoloball = redbarYoloball;
		this.redbarSelectYoloball = redbarSelectYoloball;
	}
	
	public IGTTimeStamp getIgt() {
		return igt;
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
	
	public byte[] getSave() {
		return save;
	}
	
	public void setSave(byte save[]) {
		this.save = save;
	}
	
	public int getSpecies() {
		return species.getIndexNumber();
	}
	
	public String getSpeciesName() {
		return species.getName();
	}
	
	public int getLevel() {
		return level;
	}
	
	public DVs getDVs() {
		return dvs;
	}
	
	public int getHexDVs() {
		return dvs.getHexDVs();
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
	
	public Gender getGender() {
		return gender;
	}
}