package stringflow.rta;

public class Location {

	public int map;
	public int x;
	public int y;

	public Location(int x, int y) {
		this(0, x, y);
	}
	
	public Location(int map, int x, int y) {
		this.map = map;
		this.x = x;
		this.y = y;
	}

	public double getDistance(Location other) {
		double dx = x - other.x;
		double dy = y - other.y;
		return Math.sqrt(dx * dx + dy * dy);
	}
	
	public boolean equals(Object obj) {
		Location other = (Location) obj;
		return x == other.x && y == other.y && map == other.map;
	}
}