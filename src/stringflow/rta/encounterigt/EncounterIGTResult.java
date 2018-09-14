package stringflow.rta.encounterigt;

import stringflow.rta.DVs;
import stringflow.rta.Gender;
import stringflow.rta.Species;
import stringflow.rta.util.IGTTimeStamp;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class EncounterIGTResult implements Comparable<EncounterIGTResult> {
	
	private IGTTimeStamp igt;
	private int map;
	private int x;
	private int y;
	private int hra;
	private int hrs;
	private int rdiv;
	private String npcTimers;
	private byte save[];
	private ArrayList<Integer> enterMapCalls;
	private ArrayList<Integer> itemPickups;
	private Species species;
	private Gender gender;
	private int level;
	private DVs dvs;
	private boolean yoloball;
	private boolean selectYoloball;
	private boolean redbarYoloball;
	private boolean redbarSelectYoloball;
	private boolean hitSpinner;
	
	public EncounterIGTResult(IGTTimeStamp igt, int map, int x, int y, int hra, int hrs, int rdiv, String npcTimers, byte save[], ArrayList<Integer> enterMapCalls, ArrayList<Integer> itemPickups, Species species, Gender gender, int level, int dvs, boolean yoloball, boolean selectYoloball, boolean redbarYoloball, boolean redbarSelectYoloball, boolean hitSpinner) {
		this.igt = igt;
		this.map = map;
		this.x = x;
		this.y = y;
		this.hra = hra;
		this.hrs = hrs;
		this.rdiv = rdiv;
		this.npcTimers = npcTimers;
		this.save = save;
		this.enterMapCalls = enterMapCalls;
		this.itemPickups = itemPickups;
		this.species = species;
		this.gender = gender;
		this.level = level;
		this.dvs = new DVs(dvs);
		this.yoloball = yoloball;
		this.selectYoloball = selectYoloball;
		this.redbarYoloball = redbarYoloball;
		this.redbarSelectYoloball = redbarSelectYoloball;
		this.hitSpinner = hitSpinner;
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
	
	public int getHra() {
		return hra;
	}
	
	public int getHrs() {
		return hrs;
	}
	
	public int getRdiv() {
		return rdiv;
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
	
	public boolean getHitSpinner() {
		return hitSpinner;
	}
	
	public ArrayList<Integer> getEnterMapCalls() {
		return enterMapCalls;
	}
	
	public ArrayList<Integer> getItemPickups() {
		return itemPickups;
	}
	
	public String getMapTransitions() {
		ArrayList<Integer> mapTransitions = new ArrayList<>();
		for(int i = 0; i < enterMapCalls.size() - 1; i++) {
			int first = enterMapCalls.get(i + 1);
			int second = enterMapCalls.get(i);
			mapTransitions.add(first - second);
		}
		return "[" + mapTransitions.stream().map(String::valueOf).collect(Collectors.joining(", ")) + "]";
	}
	
	public String getRNG() {
		return String.format("0x%02X%02X%02X", rdiv, hra, hrs);
	}
	
	public int compareTo(EncounterIGTResult o) {
		return igt.getTotalFrames() - o.igt.getTotalFrames();
	}
}