package stringflow.rta.gen1.moon;

import mrwint.gbtasgen.Gb;
import stringflow.rta.GBWrapper;
import stringflow.rta.LibgambatteBuilder;
import stringflow.rta.Location;
import stringflow.rta.Util;
import stringflow.rta.gen1.*;
import stringflow.rta.gen1.astar.AStar;
import stringflow.rta.gen1.data.Map;
import stringflow.rta.gen1.data.MapDestination;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashSet;

import static stringflow.rta.gen1.PokeYellow.*;

public class MoonBot {
	
	private static final String gameName;
	private static final Checkpoint checkpoints[];
	private static final ArrayList<Integer> ignoreFrames = new ArrayList<Integer>();
	private static final long params;
	private static PrintWriter partialManips;
	private static PrintWriter foundManips;
	private static Gb gb;
	private static GBWrapper wrap;
	
	private static ByteBuffer initalSaves[];
	private static OverworldTile savePos;
	
	static {
		gameName = "yellow";
		params = MoonIGT0Checker.PICKUP_RARE_CANDY |
				 MoonIGT0Checker.PICKUP_MOON_STONE |
				 MoonIGT0Checker.MONITOR_NPC_TIMERS |
				 MoonIGT0Checker.CREATE_SAVE_STATES;
		checkpoints = new Checkpoint[] {
				new Checkpoint(59, 34, 31, 8, 0, 60),
		};
		ignoreFrames.add(33);
		ignoreFrames.add(34);
	}
	
	public static void main(String[] args) throws IOException, InterruptedException {
		LibgambatteBuilder.buildGambatte(false, 100);
		if(!new File("roms").exists()) {
			new File("roms").mkdir();
			System.err.println("I need ROMs to simulate!");
			System.exit(0);
		}
		if(!new File("states").exists()) {
			new File("states").mkdir();
			System.err.println("I need state files to simulate!");
			System.exit(0);
		}
		if(new File("states").list().length == 0) {
			System.err.println("I need state files to simulate!");
			System.exit(0);
		}
		if(!new File("roms/poke" + gameName + ".gbc").exists()) {
			System.err.println("Could not find poke" + gameName + ".gbc in roms directory!");
			System.exit(0);
		}
		initalSaves = new ByteBuffer[60];
		for(int i = 0; i < 60; i++) {
			if(!(new File("./states/" + i + ".state").exists())) {
				continue;
			}
			initalSaves[i] = Util.loadByteBufferFromFile("./states/" + i + ".state");
		}
		foundManips = new PrintWriter(new File(gameName + "_foundManips.txt"));
		partialManips = new PrintWriter(new File(gameName + "_partial_moon_paths.txt"));
		
		long startTime = System.currentTimeMillis();
		OverworldTile[][] owTiles1 = AStar.initTiles(Map.MT_MOON_1, 17, 3, new MapDestination(Map.MT_MOON_1, new Location(34, 31)));
		
		long endTime = System.currentTimeMillis();
		System.out.println("Generic edge generation time: " + (endTime - startTime) + " ms");
		
		Gb.loadGambatte(1);
		gb = new Gb(0, false);
		gb.startEmulator("roms/poke" + gameName + ".gbc");
		wrap = new GBWrapper(gb, "roms/poke" + gameName + ".sym", hJoypad, hRandomAdd, hRandomSub);
		wrap.advanceTo("joypad");
		gb.loadState(initalSaves[0]);
		wrap.advance(2);
		savePos = owTiles1[wrap.read("wXCoord")][wrap.read("wYCoord")];
		OverworldState owState = new OverworldState(savePos.toString() + ":", savePos, initalSaves, checkpoints[0], 1, 0, 0, true, 0, 0);
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
			wrap.hold(0);
			IGTMap igtMap = MoonIGT0Checker.checkIGT0(wrap, gameName, ow.getSaves(), edgeAction.logStr(), params);
			int encounterIgt0 = 60;
			HashSet<String> npcs = new HashSet<>();
			ByteBuffer newSaves[] = new ByteBuffer[60];
			for(int i = 0; i < 60; i++) {
				if(igtMap.getResult(i) == null) {
					encounterIgt0--;
					continue;
				} else {
					newSaves[i] = igtMap.getResult(i).getSave();
				}
				if(igtMap.getResult(i).getSpecies() != 0) {
					encounterIgt0--;
				}
				if(!ignoreFrames.contains(i)) {
					npcs.add(igtMap.getResult(i).getNpcTimers());
				}
			}
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
						foundManips.println(ow.toString() + " " + edgeAction.logStr() + ", cost: " + (ow.getWastedFrames() + edgeCost) + ", owFrames: " + (owFrames) + " - " + encounterIgt0 + "/60 hra=" + wrap.read(hRandomAdd) + " timer1=" + wrap.read("wSprite" + Util.getSpriteAddressIndexString(4) + "MovementDelay") + " timer2=" + wrap.read("wSprite" + Util.getSpriteAddressIndexString(10) + "MovementDelay"));
						foundManips.flush();
						break;
					}
					newState = new OverworldState(ow.toString() + " " + edgeAction.logStr(), edge.getNextPos(), newSaves, newCheckpoint, Math.max(0, ow.aPressCounter() - 1), ow.getNumStartPresses(), ow.getNumAPresses(), true, ow.getWastedFrames() + edgeCost, ow.getOverworldFrames() + edge.getFrames());
					overworldSearch(newState);
					break;
				case A:
					newState = new OverworldState(ow.toString() + " " + edgeAction.logStr(), edge.getNextPos(), newSaves, ow.getCurrentTarget(), 2, ow.getNumStartPresses(), ow.getNumAPresses() + 1, true, ow.getWastedFrames() + 2, ow.getOverworldFrames() + 2);
					overworldSearch(newState);
					break;
				case START_B:
					newState = new OverworldState(ow.toString() + " " + edgeAction.logStr(), edge.getNextPos(), newSaves, ow.getCurrentTarget(), 1, ow.getNumStartPresses() + 1, ow.getNumAPresses(), true, ow.getWastedFrames() + edgeCost, ow.getOverworldFrames() + edgeCost);
					overworldSearch(newState);
					break;
				default:
					break;
			}
		}
	}
}
