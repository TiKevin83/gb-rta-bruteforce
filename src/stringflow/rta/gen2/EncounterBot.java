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
import stringflow.rta.util.Comparison;
import stringflow.rta.util.GSRUtils;
import stringflow.rta.util.IO;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.util.HashSet;

import static stringflow.rta.Joypad.*;

public class EncounterBot {
	
	private static final Checkpoint checkpoints[];
	private static PrintWriter partialManips;
	private static PrintWriter foundManips;
	private static Gb gb;
	private static Gen2Game game;
	
	private static ByteBuffer initalSaves[];
	private static OverworldTile savePos;
	private static HashSet<String> seenStates = new HashSet<>();
	
	static {
		checkpoints = new Checkpoint[] {//new Checkpoint(0x1803, 0x26, 0x10, 0, 0, 59),
				new Checkpoint(0x1803, 0xE, 0x8, 3, 0, 60),};
	}
	
	public static void main(String[] args) throws IOException, InterruptedException {
		game = new PokeGoldSilver();
		
		int waitTime = 1;
		gb = new Gb();
		gb.loadBios("roms/gbc_bios.bin");
		gb.loadRom("roms/pokegold.gbc", game, LoadFlags.CGB_MODE | LoadFlags.GBA_FLAG | LoadFlags.READONLY_SAV);
		gb.setWarnOnZero(true);
		//gb.createRenderContext(2);
		
		gb.hold(START);
		gb.advanceTo(0x100);
		byte saveState[] = gb.saveState();
		byte sram[] = new byte[0x8000];
		
		GSRUtils.decodeSAV(saveState, sram);
		GSRUtils.writeRTC(saveState, 0x9B2F, 300);
		sram[0x2044] = (byte) 0x00;
		sram[0x2045] = (byte) 0x0A;
		sram[0x2046] = (byte) 0x39;
		sram[0x2047] = (byte) 0x00;
		
		byte[][] initialSaves = new byte[60][];
		
		for(int i = 0; i < 60; i++) {
			sram[0x2057] = (byte) i;
			writeChecksum(sram);
			GSRUtils.encodeSAV(sram, saveState);
			gb.loadState(saveState);
			gb.hold(START);
			gb.advanceTo("joypadCall");
			gb.frameAdvance();
			
			gb.hold(START);
			gb.advanceTo("joypadCall");
			gb.frameAdvance();
			
			gb.hold(START | A);
			gb.advanceTo("joypadCall");
			gb.frameAdvance();
			
			gb.hold(START);
			gb.advanceTo("joypadCall");
			gb.hold(START);
			gb.frameAdvance(waitTime);
			gb.press(A);
			initialSaves[i] = gb.saveState();
		}
		
		foundManips = new PrintWriter(new File(game.getClass().getName() + "_foundManips.txt"));
		partialManips = new PrintWriter(new File(game.getClass().getName() + "_partial_moon_paths.txt"));
		
		OverworldTile[][] owTiles1 = AStar.initTiles(Map.ROUTE_29, 17, 3, false, new MapDestination(Map.ROUTE_29, new Location(0xE, 0x8)));
		
		OverworldTile savePos = owTiles1[gb.read("wXCoord")][gb.read("wYCoord")];
		OverworldState owState = new OverworldState(savePos.toString() + ":", savePos, initialSaves, checkpoints[0], 1, 0, 0, true, 0, 0, gb.getRandomAdd(), gb.getRandomSub());
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
		if(!seenStates.add(ow.getUniqId())) {
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
			EncounterIGTMap result = GenericEncounterChecker.checkIGT0(gb, ow.getSaves(), edgeAction.logStr(), GenericEncounterChecker.CREATE_SAVE_STATES);
			byte newSaves[][] = new byte[60][];
			for(int i = 0; i < 60; i++) {
				newSaves[i] = result.getResult(i).getSave();
			}
			int igt0 = result.filterSpecies(Comparison.EQUAL, 0).length();
			partialManips.println(ow.toString() + " " + edgeAction.logStr() + ", cost: " + (ow.getWastedFrames() + edgeCost) + ", owFrames: " + (owFrames) + " - " + igt0 + "/60 ");
			partialManips.flush();
			if(igt0 < ow.getCurrentTarget().getMinConsistency()) {
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
					if(edge.getNextPos().getX() == lastCheckpoint.getX() && edge.getNextPos().getY() == lastCheckpoint.getY() && edge.getNextPos().getMap() == lastCheckpoint.getMap() && igt0 >= lastCheckpoint.getMinConsistency()) {
						foundManips.println(ow.toString() + " " + edgeAction.logStr() + ", cost: " + (ow.getWastedFrames() + edgeCost) + ", owFrames: " + (owFrames) + " - " + igt0 + "/60");
						foundManips.flush();
						break;
					}
					newState = new OverworldState(ow.toString() + " " + edgeAction.logStr(), edge.getNextPos(), newSaves, newCheckpoint, Math.max(0, ow.aPressCounter() - 1), ow.getNumStartPresses(), ow.getNumAPresses(), true, ow.getWastedFrames() + edgeCost, ow.getOverworldFrames() + edge.getFrames(), gb.getRandomAdd(), gb.getRandomSub());
					overworldSearch(newState);
					break;
				case START_B:
					newState = new OverworldState(ow.toString() + " " + edgeAction.logStr(), edge.getNextPos(), newSaves, ow.getCurrentTarget(), 1, ow.getNumStartPresses() + 1, ow.getNumAPresses(), true, ow.getWastedFrames() + edgeCost, ow.getOverworldFrames() + edgeCost, gb.getRandomAdd(), gb.getRandomSub());
					overworldSearch(newState);
					break;
				default:
					break;
			}
		}
	}
	
	private static void writeChecksum(byte sram[]) {
		int checksum = 0;
		for(int i = 0x2009; i < 0x2D69; i++) {
			checksum += (sram[i] & 0xFF);
		}
		sram[0x2D69] = (byte)((checksum >> 0) & 0xFF);
		sram[0x2D6A] = (byte)((checksum >> 8) & 0xFF);
	}
}