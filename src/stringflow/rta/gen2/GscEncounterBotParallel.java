package stringflow.rta.gen2;

import stringflow.rta.*;
import stringflow.rta.astar.AStar;
import stringflow.rta.encounterigt.EncounterIGTMap;
import stringflow.rta.libgambatte.Gb;
import stringflow.rta.libgambatte.LoadFlags;
import stringflow.rta.ow.OverworldAction;
import stringflow.rta.ow.OverworldEdge;
import stringflow.rta.ow.OverworldState;
import stringflow.rta.ow.OverworldTile;
import stringflow.rta.parallel.ParallelIGTChecker;
import stringflow.rta.util.IGTTimeStamp;
import stringflow.rta.util.IO;

import java.io.File;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

public class GscEncounterBotParallel {
	
	private static Checkpoint checkpoints[];
	private static PrintWriter partialManips;
	private static PrintWriter foundManips;
	private static Gen2Game game;
	
	private static ByteBuffer initalSaves[];
	private static OverworldTile savePos;
	private static HashSet<String> seenStates = new HashSet<>();
	private static final int numThreads = 3;
	private static final int flags = GscIGTChecker.CREATE_SAVE_STATES;
	
	private static Gb gbs[];
	private static GscIGTChecker igtCheckers[];
	private static ParallelIGTChecker igtChecker;
	
	static {
		checkpoints = new Checkpoint[] {//new Checkpoint(0x1803, 0x26, 0x10, 0, 0, 59),
				new Checkpoint(Map.ROUTE_30.getId(), 0xE, 0x1C, 0, 0, 60),};
//		new Checkpoint(0x1803, 0x26, 0x10, 0, 0, 60),};
	}

//	static {
//		checkpoints = new Checkpoint[] {new Checkpoint(0x1803, 0x15, 0x4, 0, 0, 60),
//				//new Checkpoint(Map.ROUTE_30.getId(), 0xD, 0xD, 0, 0, 57),
//				new Checkpoint(Map.ROUTE_30.getId(), 0x11, 0xB, 0, 0, 60),};
//	}
	
	public static void main(String[] args) throws Exception {
		System.out.println("crystal encounter bot " + numThreads + " threads");
//		List<String> vars = IO.readText("vars.txt").getContentAsList();
//		int waitTime = Integer.valueOf(vars.get(0));
//		int numSB = Integer.valueOf(vars.get(1));
//		int consistency = Integer.valueOf(vars.get(2));
//		checkpoints = new Checkpoint[] {//new Checkpoint(0x1803, 0x26, 0x10, 0, 0, 59),
//				new Checkpoint(0x1803, 0xE, 0x8, numSB * 3, numSB, consistency),};
		
		game = new PokeCrystal();
		
		gbs = new Gb[numThreads];
		igtCheckers = new GscIGTChecker[numThreads];
		for(int i = 0; i < numThreads; i++) {
			gbs[i] = new Gb();
//			gbs[i].createRenderContext(2);
			gbs[i].loadBios("roms/gbc_bios.bin");
			gbs[i].loadRom("roms/pokecrystal.gbc", game, LoadFlags.CGB_MODE | LoadFlags.GBA_FLAG | LoadFlags.READONLY_SAV);
			igtCheckers[i] = new GscIGTChecker(gbs[i]);
		}
		igtChecker = new ParallelIGTChecker(numThreads, (threadIndex, state, path, flags) -> igtCheckers[threadIndex].checkIgt0(Collections.singleton(state), path, flags));
		
		ArrayList<IGTState> initialStates = new ArrayList<>();
		for(int j = 0; j < 60; j++) {
			if(new File("states/" + j + ".gqs").exists()) {
				initialStates.add(new IGTState(new IGTTimeStamp(0, 0, 0, j), IO.readBin("states/" + j + ".gqs")));
			}
		}
		
		foundManips = new PrintWriter(new File(game.getClass().getName() + "_foundManips.txt"));
		partialManips = new PrintWriter(new File(game.getClass().getName() + "_partial_moon_paths.txt"));
		
		OverworldTile[][] owTiles1 = AStar.initTiles(Map.ROUTE_29, 17, 3, false, new MapDestination(Map.ROUTE_29, new Location(0x9, 0x6)));
		OverworldTile[][] owTiles2 = AStar.initTiles(Map.ROUTE_29, 17, 3, false, new MapDestination(Map.ROUTE_29, new Location(0x0, 0x7)));
		OverworldTile[][] owTiles3 = AStar.initTiles(Map.CHERRY_GROVE, 17, 3, false, new MapDestination(Map.CHERRY_GROVE, new Location(0x11, 0x0)));
		OverworldTile[][] owTiles4 = AStar.initTiles(Map.ROUTE_30, 17, 3, false, new MapDestination(Map.ROUTE_30, new Location(0xE, 0x1C)));

//
//		owTiles1[0x0][0x6].addEdge(new OverworldEdge(OverworldAction.LEFT, 0, 17, owTiles2[0x27][0x6]));
		owTiles1[0x9][0x6].addEdge(new OverworldEdge(OverworldAction.LEFT, 0, 17, owTiles2[0x7][0x6]));
		owTiles2[0x0][0x7].addEdge(new OverworldEdge(OverworldAction.LEFT, 0, 17, owTiles3[0x27][0x7]));
		owTiles2[0x0][0x7].addEdge(new OverworldEdge(OverworldAction.LEFT_A, 0, 17, owTiles3[0x27][0x7]));
		owTiles3[0x27][0x7].print();
		Collections.sort(owTiles1[0x0][0x6].getEdgeList());
		Collections.sort(owTiles1[0x9][0x6].getEdgeList());
		Collections.sort(owTiles2[0x0][0x6].getEdgeList());
		
		owTiles3[0x1D][0x5].removeEdge(OverworldAction.UP_A);
		owTiles3[0x1E][0x5].removeEdge(OverworldAction.UP_A);
		owTiles3[0x1F][0x5].removeEdge(OverworldAction.UP_A);
		owTiles3[0x1D][0x6].removeEdge(OverworldAction.UP_A);
		owTiles3[0x1E][0x6].removeEdge(OverworldAction.UP_A);
		owTiles3[0x1F][0x6].removeEdge(OverworldAction.UP_A);
		
		
		owTiles3[24][0x4].removeEdge(OverworldAction.LEFT_A);
		owTiles3[0x1F][0x4].removeEdge(OverworldAction.LEFT_A);
		owTiles3[0x1E][0x4].removeEdge(OverworldAction.LEFT_A);
		
		owTiles3[0x17][0x5].removeEdge(OverworldAction.UP_A);
		owTiles3[0x18][0x5].removeEdge(OverworldAction.UP_A);
		owTiles3[0x19][0x5].removeEdge(OverworldAction.UP_A);
		owTiles3[0x17][0x6].removeEdge(OverworldAction.UP_A);
		owTiles3[0x18][0x6].removeEdge(OverworldAction.UP_A);
		owTiles3[0x19][0x6].removeEdge(OverworldAction.UP_A);
		
		owTiles3[0x11][0x0].addEdge(new OverworldEdge(OverworldAction.UP_A, 0, 17, owTiles4[0x7][0x35]));
		owTiles3[0x11][0x0].addEdge(new OverworldEdge(OverworldAction.UP, 0, 17, owTiles4[0x7][0x35]));
		Collections.sort(owTiles3[0x11][0x0].getEdgeList());
		
		for(int i = 0x31; i <= 0x35; i++) {
			owTiles1[i][0x9].removeEdge(OverworldAction.DOWN);
			owTiles1[i][0x9].removeEdge(OverworldAction.DOWN_A);
		}
		
		for(int x = 0xB; x <= 0xF; x++) {
			for(int y = 0x1C; y <= 0x1F; y++) {
				if(owTiles4[x][y] == null) {
					continue;
				}
				owTiles4[x][y].removeEdge(OverworldAction.UP_A);
				owTiles4[x][y].removeEdge(OverworldAction.LEFT_A);
			}
		}
//		OverworldTile[][] owTiles1 = AStar.initTiles(Map.ROUTE_29, 17, 3, false, new MapDestination(Map.ROUTE_29, new Location(0x00, 0x7)));
//		OverworldTile[][] owTiles2 = AStar.initTiles(Map.CHERRY_GROVE, 17, 3, false, new MapDestination(Map.CHERRY_GROVE, new Location(0x11, 0x0)));
//		OverworldTile[][] owTiles3 = AStar.initTiles(Map.ROUTE_30, 17, 3, false, new MapDestination(Map.ROUTE_30, new Location(checkpoints[1].getX(), checkpoints[1].getY())));
//		owTiles1[0x0][0x7].addEdge(new OverworldEdge(OverworldAction.LEFT, 0, 17, owTiles2[0x27][0x7]));
//		owTiles2[0x11][0x0].addEdge(new OverworldEdge(OverworldAction.UP, 0, 17, owTiles3[0x7][0x35]));
//		owTiles2[0x11][0x0].addEdge(new OverworldEdge(OverworldAction.UP, 0, 17, owTiles3[0x7][0x35]));
//
//		owTiles3[0x6][0x1C].removeEdge(OverworldAction.LEFT);
//		owTiles3[0x6][0x1B].removeEdge(OverworldAction.LEFT);
//
//		owTiles2[0x1E][0x5].removeEdge(OverworldAction.UP_A);
//		owTiles2[0x18][0x5].removeEdge(OverworldAction.UP_A);
//		owTiles2[0x1A][0x7].removeEdge(OverworldAction.LEFT_A);
//
//		owTiles3[0xD][0x1F].removeEdge(OverworldAction.UP_A);
//
//		Collections.sort(owTiles1[0x0][0x7].getEdgeList());
//		Collections.sort(owTiles2[0x11][0x0].getEdgeList());
//		Collections.sort(owTiles3[0x7][0x34].getEdgeList());

//		owTiles3[0x9][0x1E].getEdge(OverworldAction.LEFT).setCost(0);
//		owTiles3[0x9][0x1D].getEdge(OverworldAction.LEFT).setCost(0);
//		owTiles3[0x9][0x1C].getEdge(OverworldAction.LEFT).setCost(0);
//
//		owTiles3[0x8][0x1E].getEdge(OverworldAction.LEFT).setCost(0);
//		owTiles3[0x8][0x1D].getEdge(OverworldAction.LEFT).setCost(0);
//		owTiles3[0x8][0x1C].getEdge(OverworldAction.LEFT).setCost(0);
//
//		owTiles3[0x7][0x1E].getEdge(OverworldAction.LEFT).setCost(0);
//		owTiles3[0x7][0x1C].getEdge(OverworldAction.LEFT).setCost(0);
//
//		owTiles3[0x6][0x1C].getEdge(OverworldAction.LEFT).setCost(0);
//
//		for(int i = 0; i < owTiles3.length; i++) {
//			for(int j = 0; j < owTiles3[0].length; j++) {
//				if(owTiles3[i][j] != null) {
//					Collections.sort(owTiles3[i][j].getEdgeList());
//				}
//			}
//		}
//
//		owTiles1[46][9].print();
		
		gbs[0].loadState(initialStates.get(0).getState());
		gbs[0].frameAdvance(10);
		OverworldTile savePos = owTiles1[gbs[0].read("wXCoord")][gbs[0].read("wYCoord")];
		OverworldState owState = new OverworldState(savePos.toString() + ":", savePos, initialStates, checkpoints[0], 1, 0, 0, true, 0, 0, gbs[0].getRandomAdd(), gbs[0].getRdiv());
		overworldSearch(owState);
	}
	
	private static int indexOf(Checkpoint checkpoint) {
		for(int i = 0; i < checkpoints.length; i++) {
			if(checkpoints[i] == checkpoint) {
				return i;
			}
		}
		return -1;
	}
	
	private static int calcMaxCost(Checkpoint checkpoint) {
		int index = indexOf(checkpoint);
		int sum = checkpoint.getMaxCost();
		for(int i = 0; i < index; i++) {
			sum += checkpoints[i].getMaxCost();
		}
		return sum;
	}
	
	private static int calcMaxStartFlashes(Checkpoint checkpoint) {
		int index = indexOf(checkpoint);
		int sum = checkpoint.getMaxStartFlashes();
		for(int i = 0; i < index; i++) {
			sum += checkpoints[i].getMaxStartFlashes();
		}
		return sum;
	}
	
	private static long c = 0;
	private static long lastProfileTimer = System.currentTimeMillis();
	
	private static void overworldSearch(OverworldState ow) throws Exception {
		int maxCost = calcMaxCost(ow.getCurrentTarget());
		int maxStartFlashes = calcMaxStartFlashes(ow.getCurrentTarget());
		int currentCheckpointIndex = indexOf(ow.getCurrentTarget());
		Checkpoint lastCheckpoint = checkpoints[checkpoints.length - 1];
		if(ow.getWastedFrames() > maxCost) {
			return;
		}
		if(!seenStates.add(ow.getUniqId())) {
			return;
		}
		ow.addSimiliarStates(seenStates, 1);
		edgeLoop:
		for(OverworldEdge edge : ow.getPos().getEdgeList()) {
			OverworldAction edgeAction = edge.getAction();
			if(ow.aPressCounter() > 0 && (edgeAction == OverworldAction.A || edgeAction == OverworldAction.START_B || edgeAction == OverworldAction.S_A_B_S || edgeAction == OverworldAction.S_A_B_A_B_S)) {
				continue;
			}
			if(!ow.canPressStart() && (edgeAction == OverworldAction.START_B || edgeAction == OverworldAction.S_A_B_S || edgeAction == OverworldAction.S_A_B_A_B_S)) {
				continue;
			}
			if((edgeAction == OverworldAction.START_B || edgeAction == OverworldAction.S_A_B_S || edgeAction == OverworldAction.S_A_B_A_B_S) && ow.getNumStartPresses() >= maxStartFlashes) {
				continue;
			}
			int edgeCost = edge.getCost();
			if(ow.getWastedFrames() + edgeCost > maxCost) {
				continue;
			}
			OverworldState newState;
			int owFrames = ow.getOverworldFrames() + edge.getFrames();
			for(int i = 0; i < ow.getStates().size(); i++) {
				igtChecker.checkIGTFrame(ow.getStates().get(i), edgeAction.logStr(), flags);
			}
			EncounterIGTMap result = igtChecker.flush();
			ArrayList<IGTState> newStates = new ArrayList<>();
			for(int i = 0; i < result.size(); i++) {
				newStates.add(new IGTState(new IGTTimeStamp(0, 0, 0, i), result.get(i).getSave()));
			}
			int igt0 = result.filter(igt -> igt.getSpecies() == 0).size();
			partialManips.println(ow.toString() + " " + edgeAction.logStr() + ", cost: " + (ow.getWastedFrames() + edgeCost) + ", owFrames: " + (owFrames) + " - " + igt0 + "/60 ");
			partialManips.flush();
			c++;
			if(c % 10 == 0) {
				System.out.println(System.currentTimeMillis() - lastProfileTimer);
				lastProfileTimer = System.currentTimeMillis();
			}
			if(igt0 < ow.getCurrentTarget().getMinConsistency()) {
				continue;
			}
			switch(edgeAction) {
				case LEFT:
				case UP:
				case RIGHT:
				case DOWN:
				case LEFT_A:
				case UP_A:
				case RIGHT_A:
				case DOWN_A:
					Checkpoint newCheckpoint = ow.getCurrentTarget();
					if(edge.getNextPos().getX() == newCheckpoint.getX() && edge.getNextPos().getY() == newCheckpoint.getY()) {
						if(ow.getCurrentTarget() != lastCheckpoint) {
							newCheckpoint = checkpoints[currentCheckpointIndex + 1];
						}
					}
					if(edge.getNextPos().getX() == lastCheckpoint.getX() && edge.getNextPos().getY() == lastCheckpoint.getY() && edge.getNextPos().getMap() == lastCheckpoint.getMap()) {
						foundManips.println(ow.toString() + " " + edgeAction.logStr() + ", cost: " + (ow.getWastedFrames() + edgeCost) + ", owFrames: " + (owFrames) + " - " + igt0 + "/60");
						foundManips.flush();
						break;
					}
					newState = new OverworldState(ow.toString() + " " + edgeAction.logStr(), edge.getNextPos(), newStates, newCheckpoint, edgeAction.isGen2APress() ? 2 : Math.max(0, ow.aPressCounter() - 1), ow.getNumStartPresses(), 0, true, ow.getWastedFrames() + edgeCost, ow.getOverworldFrames() + edge.getFrames(), result.get(0).getHra(), result.get(0).getRdiv());
					overworldSearch(newState);
					break;
				case START_B:
					newState = new OverworldState(ow.toString() + " " + edgeAction.logStr(), edge.getNextPos(), newStates, ow.getCurrentTarget(), 1, ow.getNumStartPresses() + 1, ow.getNumAPresses(), true, ow.getWastedFrames() + edgeCost, ow.getOverworldFrames() + edgeCost, result.get(0).getHra(), result.get(0).getRdiv());
					overworldSearch(newState);
					break;
				default:
					break;
			}
		}
	}
}