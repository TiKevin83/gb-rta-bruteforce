package stringflow.rta.gen1.moon;

import java.util.ArrayList;

import stringflow.rta.Location;

public class Checkpoint {

	private int map;
	private int x;
	private int y;
	private int maxCost;
	private int maxStartFlashes;
	private int minConsistency;
	private ArrayList<Integer> rngs;
	
	public Checkpoint(int map, int x, int y, int maxCost, int maxStartFlashes, int minConsistency) {
		this.map = map;
		this.x = x;
		this.y = y;
		this.maxCost = maxCost;
		this.maxStartFlashes = maxStartFlashes;
		this.minConsistency = minConsistency;
		this.rngs = new ArrayList<Integer>();
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

	public ArrayList<Integer> getRngs() {
		return rngs;
	}
}