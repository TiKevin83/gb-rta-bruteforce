package stringflow.rta.gen1;

import stringflow.rta.*;
import stringflow.rta.astar.AStar;
import stringflow.rta.encounterigt.EncounterIGTMap;
import stringflow.rta.encounterigt.EncounterIGTResult;
import stringflow.rta.libgambatte.Gb;
import stringflow.rta.libgambatte.LoadFlags;
import stringflow.rta.ow.OverworldAction;
import stringflow.rta.ow.OverworldEdge;
import stringflow.rta.ow.OverworldState;
import stringflow.rta.ow.OverworldTile;
import stringflow.rta.util.IGTTimeStamp;
import stringflow.rta.util.IO;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;

public class RbyMoonBot {
	
	private static final String gameName;
	private static final Checkpoint checkpoints[];
	private static final ArrayList<Integer> ignoreFrames = new ArrayList<Integer>();
	private static final long flags;
	private static PrintWriter partialManips;
	private static PrintWriter foundManips;
	private static Gb gb;
	
	private static ArrayList<IGTState> initialStates;
	private static OverworldTile savePos;
	
	static {
		gameName = "yellow";
		flags = RbyIGTChecker.MONITOR_NPC_TIMERS | RbyIGTChecker.CREATE_SAVE_STATES;
		checkpoints = new Checkpoint[] {new Checkpoint(61, 12, 31, 4, 0, 55),};
		ignoreFrames.add(0);
		ignoreFrames.add(1);
		ignoreFrames.add(30);
		ignoreFrames.add(36);
		ignoreFrames.add(37);
	}
	
	public static void main(String[] args) throws IOException, InterruptedException {
		initialStates = new ArrayList<>();
		for(int i = 0; i < 60; i++) {
			if(!(new File("states/" + i + ".gqs").exists())) {
				continue;
			}
			initialStates.add(new IGTState(new IGTTimeStamp(0, 0, 0, i), IO.readBin("states/" + i + ".gqs")));
		}
		foundManips = new PrintWriter(new File(gameName + "_foundManips.txt"));
		partialManips = new PrintWriter(new File(gameName + "_partial_moon_paths.txt"));
		
		long startTime = System.currentTimeMillis();
		OverworldTile[][] owTiles1 = AStar.initTiles(Map.MT_MOON_3, 17, 3, true, new MapDestination(Map.MT_MOON_3, new Location(12, 31)));
		owTiles1[11][20].removeEdge(OverworldAction.UP);
		
		long endTime = System.currentTimeMillis();
		System.out.println("Generic edge generation time: " + (endTime - startTime) + " ms");
		
		gb = new Gb();
		gb.loadBios("roms/gbc_bios.bin");
		gb.loadRom("roms/pokeblue.gbc", new PokeRedBlue(), LoadFlags.CGB_MODE | LoadFlags.GBA_FLAG | LoadFlags.READONLY_SAV);
//		gb.createRenderContext(2);
		gb.loadState(initialStates.get(0).getData());
		gb.frameAdvance(2);
		savePos = owTiles1[gb.read("wXCoord")][gb.read("wYCoord")];
		OverworldState owState = new OverworldState(savePos.toString() + ":", savePos, initialStates, checkpoints[0], 1, 0, 0, true, 0, 0);
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
	
	private static void overworldSearch(OverworldState ow) {
		int maxCost = calcMaxCost(ow.getCurrentTarget());
		int maxStartFlashes = calcMaxStartFlashes(ow.getCurrentTarget());
		int currentCheckpointIndex = indexOf(ow.getCurrentTarget());
		Checkpoint lastCheckpoint = checkpoints[checkpoints.length - 1];
		if(ow.getWastedFrames() > maxCost) {
			return;
		}
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
			gb.hold(0);
			EncounterIGTMap igtMap = RbyIGTChecker.checkIGT0(gb, ow.getStates(), edgeAction.logStr(), flags);
			HashSet<String> npcs = new HashSet<>();
			ArrayList<IGTState> newStates = new ArrayList<>();
			for(int i = 0; i < igtMap.size(); i++) {
				EncounterIGTResult result = igtMap.get(i);
				newStates.add(new IGTState(result.getIgt(), result.getSave()));
				if(!ignoreFrames.contains(result.getIgt().getFrames())) {
					npcs.add(igtMap.get(i).getNpcTimers());
				}
			}
			int encounterIgt0 = igtMap.filter(igt -> igt.getSpecies() == 0).size();
			partialManips.println(ow.toString() + " " + edgeAction.logStr() + ", cost: " + (ow.getWastedFrames() + edgeCost) + ", owFrames: " + (owFrames) + " - " + encounterIgt0 + "/60 " + npcs.size() + " differences");
			partialManips.flush();
			if(npcs.size() > 1) {
				continue;
			}
			if(encounterIgt0 < ow.getCurrentTarget().getMinConsistency()) {
				continue;
			}
			switch(edgeAction) {
				case LEFT:
				case UP:
				case RIGHT:
				case DOWN:
					Checkpoint newCheckpoint = ow.getCurrentTarget();
					if(edge.getNextPos().getX() == newCheckpoint.getX() && edge.getNextPos().getY() == newCheckpoint.getY()) {
						if(ow.getCurrentTarget() != lastCheckpoint) {
							newCheckpoint = checkpoints[currentCheckpointIndex + 1];
						}
					}
					if(edge.getNextPos().getX() == lastCheckpoint.getX() && edge.getNextPos().getY() == lastCheckpoint.getY() && edge.getNextPos().getMap() == lastCheckpoint.getMap() && encounterIgt0 >= lastCheckpoint.getMinConsistency()) {
						foundManips.println(ow.toString() + " " + edgeAction.logStr() + ", cost: " + (ow.getWastedFrames() + edgeCost) + ", owFrames: " + (owFrames) + " - " + encounterIgt0 + "/60 hra=" + gb.getRandomAdd());
						foundManips.flush();
						break;
					}
					newState = new OverworldState(ow.toString() + " " + edgeAction.logStr(), edge.getNextPos(), newStates, newCheckpoint, Math.max(0, ow.aPressCounter() - 1), ow.getNumStartPresses(), ow.getNumAPresses(), true, ow.getWastedFrames() + edgeCost, ow.getOverworldFrames() + edge.getFrames());
					overworldSearch(newState);
					break;
				case A:
					newState = new OverworldState(ow.toString() + " " + edgeAction.logStr(), edge.getNextPos(), newStates, ow.getCurrentTarget(), 2, ow.getNumStartPresses(), ow.getNumAPresses() + 1, true, ow.getWastedFrames() + 2, ow.getOverworldFrames() + 2);
					overworldSearch(newState);
					break;
				case START_B:
					newState = new OverworldState(ow.toString() + " " + edgeAction.logStr(), edge.getNextPos(), newStates, ow.getCurrentTarget(), 1, ow.getNumStartPresses() + 1, ow.getNumAPresses(), true, ow.getWastedFrames() + edgeCost, ow.getOverworldFrames() + edgeCost);
					overworldSearch(newState);
					break;
				default:
					break;
			}
		}
	}
}