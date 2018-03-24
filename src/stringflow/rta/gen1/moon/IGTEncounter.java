package stringflow.rta.gen1.moon;

import stringflow.rta.gen1.data.Species;

import java.util.Locale;

public class IGTEncounter {

    private int x;
    private int y;
    private int map;
    private int species;
    private int level;
    private boolean turnframe;

    public IGTEncounter(int x, int y, int map, int species, int level, boolean turnframe) {
        this.x = x;
        this.y = y;
        this.map = map;
        this.species = species;
        this.level = level;
        this.turnframe = turnframe;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getMap() {
        return map;
    }

    public void setMap(int map) {
        this.map = map;
    }

    public int getSpecies() {
        return species;
    }

    public void setSpecies(int species) {
        this.species = species;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public boolean isTurnframe() {
        return turnframe;
    }

    public void setTurnframe(boolean turnframe) {
        this.turnframe = turnframe;
    }

    public boolean equals(Object o) {
        if(o instanceof IGTEncounter) {
            IGTEncounter other = (IGTEncounter) o;
            return x == other.x && y == other.y &&
                    map == other.map && species == other.species &&
                    level == other.level;
        }
        return super.equals(o);
    }

    public String log(int dividend, int divisor) {
        return String.format(Locale.ENGLISH, "%d/%d L%d %s at %s %s", dividend, divisor, level, Species.getSpeciesByIndexNumber(species).getName(), "http://pokeworld.herokuapp.com/rb/" + map + "#" + x + "," + y, turnframe ? "[turnframe]" : "");
    }
}
