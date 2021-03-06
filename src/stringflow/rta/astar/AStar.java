package stringflow.rta.astar;

import stringflow.rta.Location;
import stringflow.rta.Map;
import stringflow.rta.MapDestination;
import stringflow.rta.Tile;
import stringflow.rta.ow.OverworldAction;
import stringflow.rta.ow.OverworldEdge;
import stringflow.rta.ow.OverworldTile;
import stringflow.rta.ow.OverworldTilePath;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class AStar {
	
	public static final int BASIC_COLLISION = 1;
	public static final int DIRECTIONAL_COLLISION = 2;
	
	private static NodeSorter nodeSorter = new NodeSorter();
	
	public static OverworldTile[][] initTiles(Map targetMap, int numFramesPerStep, int sbcost, boolean gen1, MapDestination... destinations) {
		OverworldTile[][] tiles = new OverworldTile[targetMap.getWidthInTiles() + 1][targetMap.getHeightInTiles() + 1];
		int startX = targetMap.getPokeworldOffsetX();
		int startY = targetMap.getPokeworldOffsetY();
		int width = targetMap.getWidthInTiles();
		int height = targetMap.getHeightInTiles();
		HashMap<Map, MapDestination> dests = new HashMap<Map, MapDestination>();
		HashMap<OverworldTile, List<Node>> paths = new HashMap<OverworldTile, List<Node>>();
		for(MapDestination dest : destinations) {
			dests.put(dest.getMap(), dest);
		}
		for(int i = 0; i <= width; i++) {
			for(int j = 0; j <= height; j++) {
				int pwX = i + startX;
				int pwY = j + startY;
				Map map = Map.getMapByPosition(pwX, pwY);
				int tileX = pwX - map.getPokeworldOffsetX();
				int tileY = pwY - map.getPokeworldOffsetY();
				OverworldTile baseTile = map.getOverworldTile(tileX, tileY);
				if(baseTile != null) {
					tiles[i][j] = new OverworldTile(baseTile.getMap(), baseTile.getX(), baseTile.getY(), baseTile.getClosestGrassTile());
				}
			}
		}
		for(int i = 0; i <= width; i++) {
			for(int j = 0; j <= height; j++) {
				if(tiles[i][j] == null) {
					continue;
				}
				Map map = Map.getMapByID(tiles[i][j].getMap());
				MapDestination dest = dests.get(map);
				if(dest == null) {
					dest = new MapDestination(map);
					dests.put(map, dest);
				}
				OverworldTilePath path = new OverworldTilePath(tiles[i][j], dest);
				if(path.getShortestPath() == null) {
					continue;
				}
				List<Node> shortestPath = path.getShortestPath();
				tiles[i][j].setMinStepsToGrass(shortestPath.size());
				paths.put(tiles[i][j], shortestPath);
			}
		}
		for(int i = 0; i <= width; i++) {
			for(int j = 0; j <= height; j++) {
				if(tiles[i][j] == null) {
					continue;
				}
				Map map = Map.getMapByID(tiles[i][j].getMap());
				MapDestination dest = dests.get(map);
				List<Node> path = paths.get(tiles[i][j]);
				if(path == null) {
					continue;
				}
				if(dest.getMode() == MapDestination.WEST_CONNECTION && path.get(path.size() - 1).getPosition().x == 0) {
					Map destMap = map.getWestConnection();
					int tileX = destMap.getWidthInTiles() - 1;
					int tileY = path.get(path.size() - 1).getPosition().y + map.getPokeworldOffsetY() - destMap.getPokeworldOffsetY();
					tiles[i][j].setMinStepsToGrass(tiles[i][j].getMinStepsToGrass() + destMap.getOverworldTile(tileX, tileY).getMinStepsToGrass());
				}
				if(dest.getMode() == MapDestination.EAST_CONNECTION && path.get(path.size() - 1).getPosition().x == map.getWidthInTiles() - 1) {
					Map destMap = map.getEastConnection();
					int tileX = destMap.getWidthInTiles() + 1;
					int tileY = path.get(path.size() - 1).getPosition().y + map.getPokeworldOffsetY() - destMap.getPokeworldOffsetY();
					tiles[i][j].setMinStepsToGrass(tiles[i][j].getMinStepsToGrass() + destMap.getOverworldTile(tileX, tileY).getMinStepsToGrass());
				}
				if(dest.getMode() == MapDestination.SOUTH_CONNECTION && path.get(path.size() - 1).getPosition().y == map.getHeightInTiles() - 1) {
					Map destMap = map.getSouthConnection();
					int tileX = path.get(path.size() - 1).getPosition().x + map.getPokeworldOffsetX() - destMap.getPokeworldOffsetX();
					int tileY = destMap.getHeightInTiles() + 1;
					tiles[i][j].setMinStepsToGrass(tiles[i][j].getMinStepsToGrass() + destMap.getOverworldTile(tileX, tileY).getMinStepsToGrass());
				}
				if(dest.getMode() == MapDestination.NORTH_CONNECTION && path.get(path.size() - 1).getPosition().y == 0) {
					Map destMap = map.getNorthConnection();
					int tileX = path.get(path.size() - 1).getPosition().x + map.getPokeworldOffsetX() - destMap.getPokeworldOffsetX();
					int tileY = destMap.getHeightInTiles() - 1;
					tiles[i][j].setMinStepsToGrass(tiles[i][j].getMinStepsToGrass() + destMap.getOverworldTile(tileX, tileY).getMinStepsToGrass());
				}
			}
		}
		for(int i = 0; i <= width; i++) {
			outer:
			for(int j = 0; j <= height; j++) {
				if(tiles[i][j] == null) {
					continue;
				}
				Map map = Map.getMapByID(tiles[i][j].getMap());
				Tile tile = map.getTile(tiles[i][j].getX(), tiles[i][j].getY());
				if(tile.canMoveLeft()) {
					if(i != 0) {
						OverworldTile destTile = tiles[i - 1][j];
						if(destTile != null) {
							int cost = Math.abs(numFramesPerStep * (destTile.getMinStepsToGrass() - tiles[i][j].getMinStepsToGrass() + 1));
							tiles[i][j].addEdge(new OverworldEdge(OverworldAction.LEFT, cost, numFramesPerStep, destTile));
							if(!gen1) {
								tiles[i][j].addEdge(new OverworldEdge(OverworldAction.LEFT_A, cost, numFramesPerStep, destTile));
							}
						}
					}
				}
				if(tile.canMoveRight()) {
					if(i != width) {
						OverworldTile destTile = tiles[i + 1][j];
						if(destTile != null) {
							int cost = Math.abs(numFramesPerStep * (destTile.getMinStepsToGrass() - tiles[i][j].getMinStepsToGrass() + 1));
							tiles[i][j].addEdge(new OverworldEdge(OverworldAction.RIGHT, cost, numFramesPerStep, destTile));
							if(!gen1) {
								tiles[i][j].addEdge(new OverworldEdge(OverworldAction.RIGHT_A, cost, numFramesPerStep, destTile));
							}
						}
					}
				}
				if(tile.canMoveUp()) {
					if(j != 0) {
						OverworldTile destTile = tiles[i][j - 1];
						if(destTile != null) {
							int cost = Math.abs(numFramesPerStep * (destTile.getMinStepsToGrass() - tiles[i][j].getMinStepsToGrass() + 1));
							tiles[i][j].addEdge(new OverworldEdge(OverworldAction.UP, cost, numFramesPerStep, destTile));
							if(!gen1) {
								tiles[i][j].addEdge(new OverworldEdge(OverworldAction.UP_A, cost, numFramesPerStep, destTile));
							}
						}
					}
				}
				if(tile.canMoveDown()) {
					if(j != height) {
						OverworldTile destTile = tiles[i][j + 1];
						if(destTile != null) {
							int cost = Math.abs(numFramesPerStep * (destTile.getMinStepsToGrass() - tiles[i][j].getMinStepsToGrass() + 1));
							tiles[i][j].addEdge(new OverworldEdge(OverworldAction.DOWN, cost, numFramesPerStep, destTile));
							if(!gen1) {
								tiles[i][j].addEdge(new OverworldEdge(OverworldAction.DOWN_A, cost, numFramesPerStep, destTile));
							}
						}
					}
				}
				if(gen1) {
					tiles[i][j].addEdge(new OverworldEdge(OverworldAction.A, 2, 2, tiles[i][j]));
				}
				tiles[i][j].addEdge(new OverworldEdge(OverworldAction.START_B, sbcost, sbcost, tiles[i][j]));
				tiles[i][j].addEdge(new OverworldEdge(OverworldAction.S_A_B_S, sbcost + 30, sbcost + 30, tiles[i][j]));
				tiles[i][j].addEdge(new OverworldEdge(OverworldAction.S_A_B_A_B_S, sbcost + 60, sbcost + 60, tiles[i][j]));
				Collections.sort(tiles[i][j].getEdgeList());
			}
		}
		if(targetMap.getId() == Map.ROUTE_30.getId())
			System.out.println(paths.get(tiles[0x7][0x35]));
		return tiles;
	}
	
	public static List<Node> findPath(Map map, Location start, Location end, boolean ableToWalkThroughGrass, int collisionDetection) {
		List<Node> openList = new ArrayList<Node>();
		List<Node> closedList = new ArrayList<Node>();
		Node current = new Node(start, null, 0, start.getDistance(end));
		openList.add(current);
		while(openList.size() > 0) {
			Collections.sort(openList, nodeSorter);
			current = openList.get(0);
			if(current.getPosition().equals(end)) {
				List<Node> path = new ArrayList<Node>();
				path.add(current);
				while(current.getParent() != null) {
					path.add(current);
					current = current.getParent();
				}
				openList.clear();
				closedList.clear();
				Collections.reverse(path);
				return path;
			}
			openList.remove(current);
			closedList.add(current);
			// check tiles in this format
			// 0 1 2
			// 3 4 5
			// 6 7 8
			for(int i = 0; i < 9; i++) {
				// don't check against diagonal tiles or against itself, it just
				// happens to be that every tile you don't want to check is even
				if(i % 2 == 0) {
					continue;
				}
				int x = current.getPosition().x;
				int y = current.getPosition().y;
				int xa = (i % 3) - 1;
				int ya = (i / 3) - 1;
				Location position = new Location(x + xa, y + ya);
				Tile tile = map.getTile(x + xa, y + ya);
				if(tile == null) {
					continue;
				}
				if(tile.isOccupiedByNPC()) {
					continue;
				}
				if(collisionDetection == BASIC_COLLISION) {
					if(tile.isSolid()) {
						continue;
					}
				} else if(collisionDetection == DIRECTIONAL_COLLISION) {
					Tile currentTile = map.getTile(current.getPosition().x, current.getPosition().y);
					if(i == 1 && !currentTile.canMoveUp()) {
						continue;
					}
					if(i == 3 && !currentTile.canMoveLeft()) {
						continue;
					}
					if(i == 5 && !currentTile.canMoveRight()) {
						continue;
					}
					if(i == 7 && !currentTile.canMoveDown()) {
						continue;
					}
				}
				if(tile.isGrassTile() && ableToWalkThroughGrass) {
					continue;
				}
				double gCost = current.getGCost() + current.getPosition().getDistance(position);
				double hCost = position.getDistance(end);
				Node node = new Node(position, current, gCost, hCost);
				if(isPositionInList(closedList, position) && gCost >= current.getGCost()) {
					continue;
				}
				if(!isPositionInList(openList, position) || gCost <= current.getGCost()) {
					openList.add(node);
				}
			}
		}
		closedList.clear();
		return null;
		
	}
	
	private static boolean isPositionInList(List<Node> list, Location position) {
		for(Node node : list) {
			if(node.getPosition().equals(position)) {
				return true;
			}
		}
		return false;
	}
}