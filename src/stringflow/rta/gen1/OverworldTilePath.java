package stringflow.rta.gen1;

import stringflow.rta.gen1.astar.AStar;
import stringflow.rta.Location;
import stringflow.rta.gen1.astar.Node;
import stringflow.rta.gen1.data.Map;
import stringflow.rta.gen1.data.MapDestination;

import java.util.HashMap;
import java.util.List;

public class OverworldTilePath {

	private Map map;
	private OverworldTile src;
	private HashMap<OverworldTile, List<Node>> possiblePaths;

	public OverworldTilePath(OverworldTile src, MapDestination dest) {
		this.src = src;
		this.map = Map.getMapByID(src.getMap());
		this.possiblePaths = new HashMap<OverworldTile, List<Node>>();
		for(Location location : dest.getDestinationTiles()) {
			if((dest.getMode() == MapDestination.WEST_CONNECTION && map.getTile(location.x, location.y).canMoveLeft()) ||
			   (dest.getMode() == MapDestination.NORTH_CONNECTION && map.getTile(location.x, location.y).canMoveUp()) ||
			   (dest.getMode() == MapDestination.EAST_CONNECTION && map.getTile(location.x, location.y).canMoveRight()) ||
			   (dest.getMode() == MapDestination.SOUTH_CONNECTION && map.getTile(location.x, location.y).canMoveDown()) ||
			   (dest.getMode() == MapDestination.GRASS_PATCHES) || (dest.getMode() == MapDestination.CUSTOM)) {
				possiblePaths.put(map.getOverworldTile(location.x, location.y), AStar.findPath(map, new Location(src.getX(), src.getY()), location, false, AStar.DIRECTIONAL_COLLISION));
			}
		}
	}

	public Map getMap() {
		return map;
	}

	public OverworldTile getSrc() {
		return src;
	}

	public List<Node> getShortestPath() {
		List<Node> result = null;
		for (List<Node> path : possiblePaths.values()) {
			if (result == null) {
				result = path;
				continue;
			}
			if (path == null) {
				continue;
			}
			if (result.size() > path.size()) {
				result = path;
			}
		}
		return result;
	}
}