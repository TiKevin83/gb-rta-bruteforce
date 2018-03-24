package stringflow.rta.gen1;

import stringflow.rta.Location;

import java.util.ArrayList;

public class Checkpoint {

    private int map;
    private int x;
    private int y;
    private int maxCost;
    private int maxStartFlashes;
    private int minConsistency;
    private int maxAPresses;
    private ArrayList<Integer> rngs;

    public Checkpoint(int map, int x, int y, int maxCost, int maxStartFlashes, int minConsistency) {
        this(map, x, y, maxCost, maxStartFlashes, minConsistency, 256);
    }

    public Checkpoint(int map, int x, int y, int maxCost, int maxStartFlashes, int minConsistency, int maxAPresses) {
        this.map = map;
        this.x = x;
        this.y = y;
        this.maxCost = maxCost;
        this.maxStartFlashes = maxStartFlashes;
        this.minConsistency = minConsistency;
        this.maxAPresses = maxAPresses;
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

    public int getMaxAPresses() {
        return maxAPresses;
    }

    public ArrayList<Integer> getRngs() {
        return rngs;
    }
}
