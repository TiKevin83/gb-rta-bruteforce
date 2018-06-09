package stringflow.rta.gen1.encounterigt;

import stringflow.rta.Location;

public class Checkpoint {
	
	private int map;
	private int x;
	private int y;
	private int maxCost;
	private int maxStartFlashes;
	private int minConsistency;
	
	public Checkpoint(int map, int x, int y, int maxCost, int maxStartFlashes, int minConsistency) {
		this.map = map;
		this.x = x;
		this.y = y;
		this.maxCost = maxCost;
		this.maxStartFlashes = maxStartFlashes;
		this.minConsistency = minConsistency;
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
	
	public int getMaxCost() {
		return maxCost;
	}
	
	public int getMinConsistency() {
		return minConsistency;
	}
	
	public Location toLocation() {
		return new Location(x, y);
	}
	
	public int getMaxStartFlashes() {
		return maxStartFlashes;
	}
}