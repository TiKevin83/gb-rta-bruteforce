package stringflow.rta.tas;

import stringflow.rta.IGTState;
import stringflow.rta.InputDisplay;
import stringflow.rta.Location;
import stringflow.rta.Map;
import stringflow.rta.MapDestination;
import stringflow.rta.astar.AStar;
import stringflow.rta.encounterigt.EncounterIGTMap;
import stringflow.rta.encounterigt.EncounterIGTResult;
import stringflow.rta.gen1.Gen1Game;
import stringflow.rta.gen1.PokeRedBlue;
import stringflow.rta.gen1.RbyIGTChecker;
import stringflow.rta.libgambatte.Gb;
import stringflow.rta.libgambatte.LoadFlags;
import stringflow.rta.ow.OverworldAction;
import stringflow.rta.ow.OverworldEdge;
import stringflow.rta.ow.OverworldState;
import stringflow.rta.ow.OverworldTile;
import stringflow.rta.util.IGTTimeStamp;
import stringflow.rta.util.IO;
import stringflow.rta.util.TextFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static stringflow.rta.Joypad.*;

public class TASPlaybackBlueGlitchless {
	private static HashSet<String> seenStates = new HashSet<>();
	private static Gb gb = new Gb();
	private static RbyIGTChecker igtChecker = new RbyIGTChecker(gb);
	private static PrintWriter partialManips;
	private static PrintWriter nidoManips;
	private static PrintWriter godNidoManips;
	private static Gen1Game pokeRedBlue = new PokeRedBlue();
	private static final long flags = RbyIGTChecker.CREATE_SAVE_STATES | pokeRedBlue.getSpecies(2).getIndexNumber();


	public static void main(String args[]) {
		
		final int joypadFlags[] = { UP, DOWN, LEFT, RIGHT, START, SELECT, B, A };
		try {
			partialManips = new PrintWriter(new File("tas_partialManips.txt"));
			nidoManips = new PrintWriter(new File("tas_nidoManips.txt"));
			godNidoManips = new PrintWriter(new File("tas_godNidoManips.txt"));
		} catch (FileNotFoundException e) {
		}
		
		TextFile textFile = IO.readText("tasInputs/bluenido.txt");
		List<String> frames = textFile.subList(2, textFile.size() - 1);
		byte buttons[] = new byte[frames.size()];
		for(int frameNumber = 0; frameNumber < frames.size(); frameNumber++) {
			for(int joypadIndex = 0; joypadIndex < joypadFlags.length; joypadIndex++) {
				if(frames.get(frameNumber).charAt(joypadIndex + 1) != '.')  {
					buttons[frameNumber] |= joypadFlags[joypadIndex];
				}
			}
		}
		gb.loadBios("roms/gbc_bios.bin");
		gb.loadRom("roms/pokeblue.gb", pokeRedBlue, LoadFlags.CGB_MODE | LoadFlags.GBA_FLAG | LoadFlags.READONLY_SAV);
		gb.setInjectInputs(false);
		gb.setOnDisplayUpdate(new InputDisplay());
		for(int i = 0; i < buttons.length; i++) {
			gb.press(buttons[i]);
		}
		gb.write("wEnemyMonSpecies", 0);
		byte startState[] = gb.saveState();
		OverworldTile[][] owTiles1 = AStar.initTiles(Map.VIRIDIAN_CITY, 17, 50, true, new MapDestination(Map.VIRIDIAN_CITY, MapDestination.WEST_CONNECTION));
		OverworldTile[][] owTiles2 = AStar.initTiles(Map.ROUTE_22, 17, 50, true, new MapDestination(Map.ROUTE_22, new Location(33, 11)));

		owTiles1[0][14].addEdge(new OverworldEdge(OverworldAction.LEFT, 0, 17, owTiles2[39][9]));
		owTiles1[0][15].addEdge(new OverworldEdge(OverworldAction.LEFT, 0, 17, owTiles2[39][9]));
		owTiles1[0][16].addEdge(new OverworldEdge(OverworldAction.LEFT, 0, 17, owTiles2[39][9]));
		owTiles1[0][17].addEdge(new OverworldEdge(OverworldAction.LEFT, 0, 17, owTiles2[39][9]));
		Collections.sort(owTiles1[0][14].getEdgeList());
		Collections.sort(owTiles1[0][15].getEdgeList());
		Collections.sort(owTiles1[0][16].getEdgeList());
		Collections.sort(owTiles1[0][17].getEdgeList());
		OverworldTile savePos = owTiles1[gb.read("wXCoord")][gb.read("wYCoord")];
		
		ArrayList<IGTState> initialStates = new ArrayList<>(Arrays.asList(new IGTState(new IGTTimeStamp(0,0,0,0), startState)));
		OverworldState owStates = new OverworldState(savePos.toString() + ":", savePos, initialStates, null, 1, 0, 0, true, 0, 0, gb.getRandomAdd(), gb.getRandomSub());
		
		overworldSearch(owStates);
	}
	
	private static void overworldSearch(OverworldState ow) {
		if(!seenStates.add(ow.getUniqId())) {
			return;
		}
		int maxCost = 400;
		int maxStartFlashes = 0;
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
			EncounterIGTMap igtMap = igtChecker.checkIGT0(ow.getStates(), edgeAction.logStr(), flags);
			ArrayList<IGTState> newStates = new ArrayList<IGTState>();
			newStates.add(new IGTState(igtMap.get(0).getIgt(), igtMap.get(0).getSave()));
			partialManips.println(ow.toString() + " " + edgeAction.logStr() + ", cost: " + (ow.getWastedFrames() + edgeCost) + ", owFrames: " + (owFrames));
			partialManips.flush();
			
		    EncounterIGTResult encounter = igtMap.get(0);
            if(encounter.getSpecies() == 3 && encounter.getLevel() == 3) {
            	String nidoString = ow.toString() + " " + edgeAction.logStr() + " " + encounter.getSpeciesName() + " " + encounter.getLevel() + " " + encounter.getDVs().toHexString() + ", cost: " + (ow.getWastedFrames() + edgeCost);
                if(encounter.getDVs().getAttack() == 15 && encounter.getDVs().getDefense()%2 == 0 && encounter.getDVs().getDefense() < 9 && encounter.getDVs().getSpeed() == 14 && encounter.getDVs().getSpecial() == 15) {
                	godNidoManips.println(nidoString);
                    godNidoManips.flush();
                } else {
                	nidoManips.println(nidoString);
                    nidoManips.flush();
                }
                continue;
            } else if (encounter.getSpecies() != 0) {
            	continue;
            }
			switch(edgeAction) {
				case LEFT:
				case UP:
				case RIGHT:
				case DOWN:
					newState = new OverworldState(ow.toString() + " " + edgeAction.logStr(), edge.getNextPos(), newStates, ow.getCurrentTarget(), Math.max(0, ow.aPressCounter() - 1), ow.getNumStartPresses(), ow.getNumAPresses(), true, ow.getWastedFrames() + edgeCost, ow.getOverworldFrames() + edge.getFrames(), gb.getRandomAdd(), gb.getRandomSub());
					overworldSearch(newState);
					break;
				case A:
					newState = new OverworldState(ow.toString() + " " + edgeAction.logStr(), edge.getNextPos(), newStates, ow.getCurrentTarget(), 2, ow.getNumStartPresses(), ow.getNumAPresses() + 1, true, ow.getWastedFrames() + 2, ow.getOverworldFrames() + 2, gb.getRandomAdd(), gb.getRandomSub());
					overworldSearch(newState);
					break;
				case START_B:
					newState = new OverworldState(ow.toString() + " " + edgeAction.logStr(), edge.getNextPos(), newStates, ow.getCurrentTarget(), 1, ow.getNumStartPresses() + 1, ow.getNumAPresses(), true, ow.getWastedFrames() + edgeCost, ow.getOverworldFrames() + edgeCost, gb.getRandomAdd(), gb.getRandomSub());
					overworldSearch(newState);
					break;
				default:
					break;
			}
		}
	}
}
