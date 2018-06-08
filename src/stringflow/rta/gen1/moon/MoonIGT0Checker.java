package stringflow.rta.gen1.moon;

import mrwint.gbtasgen.Gb;
import stringflow.rta.*;
import stringflow.rta.gen1.Itemball;
import stringflow.rta.gen1.OverworldAction;
import stringflow.rta.gen1.PokeRedBlue;

import java.io.File;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import static stringflow.rta.Joypad.*;
import static stringflow.rta.gen1.PokeYellow.*;

public class MoonIGT0Checker {
	
	public static final int NONE = 0;
	public static final int PICKUP_RARE_CANDY = 1;
	public static final int PICKUP_ESCAPE_ROPE = 2;
	public static final int PICKUP_MEGA_PUNCH = 4;
	public static final int PICKUP_MOON_STONE = 8;
	public static final int PICKUP_WATER_GUN = 16;
	public static final int YOLOBALL_PARAS = 32;
	public static final int SELECT_YOLOBALL_PARAS = 64;
	public static final int MONITOR_NPC_TIMERS = 128;
	public static final int CREATE_SAVE_STATES = 256;
	
	private static final int NUM_NPCS = 15;
	private static final Itemball WATER_GUN = new Itemball(0xC, new Location(59, 0x5, 0x1F), new Location(59, 0x6, 0x20));
	private static final Itemball RARE_CANDY = new Itemball(0xA, new Location(59, 0x23, 0x20), new Location(59, 0x22, 0x1F));
	private static final Itemball ESCAPE_ROPE = new Itemball(0xB, new Location(59, 0x24, 0x18));
	private static final Itemball MOON_STONE = new Itemball(0x9, new Location(59, 0x3, 0x2), new Location(59, 0x2, 0x3));
	private static final Itemball MEGA_PUNCH = new Itemball(0x9, new Location(61, 0x1C, 0x5));
	
	public static void main(String args[]) throws Exception {
		
		LibgambatteBuilder.buildGambatte(true, 100);
		int maxSecond = 1;
		long params = PICKUP_RARE_CANDY | PICKUP_MOON_STONE | MONITOR_NPC_TIMERS | CREATE_SAVE_STATES;
		boolean printNPCTimers = true;
		boolean writeStates = true;
		String gameName = "yellow";
		String path = "";
		PrintStream target = System.out;
		
		if(!new File("roms").exists()) {
			new File("roms").mkdir();
			System.err.println("I need ROMs to simulate!");
			System.exit(0);
		}
		if(!new File("states").exists()) {
			new File("states").mkdir();
		}
		if(!new File("roms/poke" + gameName + ".gbc").exists()) {
			System.err.println("Could not find poke" + gameName + ".gbc in roms directory!");
			System.exit(0);
		}
		if(!new File("roms/poke" + gameName + ".sym").exists()) {
			System.err.println("Could not find poke" + gameName + ".sym in roms directory!");
			System.exit(0);
		}
		if(writeStates) {
			for(File file : new File("states").listFiles()) {
				file.delete();
			}
		}
		
		Gb.loadGambatte(1);
		Gb gb = new Gb(0, false);
		gb.startEmulator("roms/poke" + gameName + ".gbc");
		GBWrapper wrap = new GBWrapper(gb, "roms/poke" + gameName + ".sym", hJoypad, hRandomAdd, hRandomSub);
		if(!gameName.equalsIgnoreCase("yellow")) {
			PokeRedBlue.nopal.execute(wrap);
		}
		gfSkip.execute(wrap);
		intro0.execute(wrap);
		title.execute(wrap);
		wrap.advanceTo(igtInjectAddr);
		ByteBuffer igtState = gb.saveState();
		ByteBuffer initalStates[] = new ByteBuffer[maxSecond * 60];
		for(int second = 39; second < 40; second++) {
			for(int frame = 0; frame < 60; frame++) {
				gb.loadState(igtState);
				wrap.write("wPlayTimeSeconds", second);
				wrap.write("wPlayTimeFrames", frame);
				cont.execute(wrap);
				cont.execute(wrap);
				wrap.advanceTo("joypadOverworld");
				initalStates[0 * 60 + frame] = gb.saveState();
			}
		}
		IGTMap map = checkIGT0(wrap, gameName, initalStates, path, params);
		map.save("./igtmap.bin");
		for(int i = 0; i < map.getSize(); i++) {
			int second = i / 60;
			int frame = i % 60;
			IGTResult result = map.getResult(i);
			String rng = String.format("0x%4s", Integer.toHexString(result.getRNG()).toUpperCase()).replace(' ', '0');
			if(result.getSpecies() == 0) {
				target.printf("[%d][%d] No encounter at [%d#%d,%d]; rng %s %s\n", second, frame, result.getMap(), result.getX(), result.getY(), rng, printNPCTimers ? "npctimers " + result.getNpcTimers() : "");
				if(writeStates) {
					if(result.getSave() == null) {
						System.err.println("Write state files is enabled, however no save file has been saved for frame " + i);
					} else {
						Util.writeBytesToFile("./states/" + i + ".state", result.getSave());
					}
				}
			} else {
				target.printf("[%d][%d] Encounter at [%d#%d,%d]: %s lv%d DVs %04X rng %s %s\n", second, frame, result.getMap(), result.getX(), result.getY(), result.getSpeciesName(), result.getLevel(), result.getDvs(), rng, printNPCTimers ? "npctimers " + result.getNpcTimers() : "");
			}
			target.flush();
		}
	}
	
	public static IGTMap checkIGT0(GBWrapper wrap, String gameName, ByteBuffer initalStates[], String path, long params) {
		ArrayList<Itemball> itemballs = new ArrayList<>();
		if((params & PICKUP_RARE_CANDY) != 0) {
			itemballs.add(RARE_CANDY);
		}
		if((params & PICKUP_ESCAPE_ROPE) != 0) {
			itemballs.add(ESCAPE_ROPE);
		}
		if((params & PICKUP_MEGA_PUNCH) != 0) {
			itemballs.add(MEGA_PUNCH);
		}
		if((params & PICKUP_MOON_STONE) != 0) {
			itemballs.add(MOON_STONE);
		}
		if((params & PICKUP_WATER_GUN) != 0) {
			itemballs.add(WATER_GUN);
		}
		int maxSecond = (int)Math.ceil(initalStates.length / 60);
		ArrayList<Integer>[] npcTimers = new ArrayList[NUM_NPCS];
		IGTMap igtmap = new IGTMap(maxSecond);
		String actions[] = path.split(" ");
		for(int second = 0; second < maxSecond; second++) {
			for(int frame = 0; frame < 60; frame++) {
				int index = second * 60 + frame;
				if(initalStates[index] == null) {
					continue;
				}
				wrap.loadState(initalStates[index]);
				wrap.advanceTo("joypadOverworld");
				for(int i = 0; i < NUM_NPCS; i++) {
					npcTimers[i] = new ArrayList<>();
				}
				if((params & MONITOR_NPC_TIMERS) != 0) {
					updateNPCTimers(wrap, npcTimers);
				}
				for(String action : actions) {
					if(action.trim().isEmpty()) {
						continue;
					}
					if(!execute(wrap, OverworldAction.fromString(action), itemballs, params)) {
						break;
					}
					if((params & MONITOR_NPC_TIMERS) != 0) {
						updateNPCTimers(wrap, npcTimers);
					}
				}
				igtmap.addResult(wrap, index, npcTimers, (params & CREATE_SAVE_STATES) != 0 ? wrap.saveState() : null);
			}
		}
		return igtmap;
	}
	
	private static boolean execute(GBWrapper wrap, OverworldAction owAction, ArrayList<Itemball> itemballs, long params) {
		Address res;
		switch(owAction) {
			case LEFT:
			case UP:
			case RIGHT:
			case DOWN:
				int encounterTest = wrap.getAddress("TryDoWildEncounter") + 0x54;
				int input = 16 * (int)(Math.pow(2.0, (owAction.ordinal())));
				Location dest = getDestination(wrap, input);
				wrap.hold(input);
				wrap.advanceTo(wrap.getAddress("joypadOverworld") + 1);
				Address result = wrap.advanceTo("joypadOverworld", "newBattle", "manualTextScroll");
				if(result.equals("manualTextScroll")) {
					return false;
				}
                /*if(Map.getMapByID(dest.map).getTile(dest.x, dest.y).isWarp()) {
                    wrap.advanceTo("enterMap");
                    transitionTimes.add(readIGT());
                }*/
				while(wrap.read("wXCoord") != dest.x || wrap.read("wYCoord") != dest.y) {
					if(result.equals("newBattle")) {
						Address result2 = wrap.advanceTo(encounterTest, "joypadOverworld");
						if(result2.equals(encounterTest)) {
							int hra = wrap.getRandomAdd();
							if(hra < wrap.read("wGrassRate")) {
								wrap.advanceFrame();
								wrap.advanceFrame();
								wrap.advanceFrame();
								return false;
							}
						}
					}
					wrap.hold(0);
					wrap.advanceTo("joypadOverworld");
					wrap.hold(input);
					wrap.advanceTo(wrap.getAddress("joypadOverworld") + 1);
					result = wrap.advanceTo("newBattle", "joypadOverworld");
				}
				wrap.hold(0);
				Address result2 = wrap.advanceTo(encounterTest, "joypadOverworld", "manualTextScroll");
				if(result2.equals("manualTextScroll")) {
					return false;
				}
				if(result2.equals(encounterTest)) {
					int hra = wrap.getRandomAdd();
					if(hra < wrap.read("wGrassRate")) {
						wrap.advanceFrame();
						wrap.advanceFrame();
						wrap.advanceFrame();
						return false;
					}
					wrap.hold(0);
					wrap.advanceTo("joypadOverworld");
					if(timeToPickUpItem(wrap, itemballs)) {
						wrap.press(A);
						wrap.hold(A);
						wrap.advanceTo("TextCommand0B");
						wrap.hold(0);
						wrap.advanceTo("joypadOverworld");
					}
				}
				return true;
			case A:
				wrap.hold(A);
				wrap.advanceFrame();
				res = wrap.advanceTo("joypadOverworld", "printLetterDelay", "manualTextScroll");
				if(res.equals("manualTextScroll")) {
					return false;
				}
				if(res.equals("joypadOverworld")) {
					return true;
				} else {
					return false;
				}
			case START_B:
				wrap.hold(START);
				wrap.advanceFrame();
				wrap.advanceTo("joypad");
				wrap.hold(B);
				wrap.advanceFrame();
				wrap.advanceTo("joypadOverworld");
				return true;
			case S_A_B_S:
				wrap.hold(START);
				wrap.advanceFrame();
				wrap.advanceTo("joypad");
				wrap.hold(A);
				wrap.advanceFrame();
				wrap.advanceTo("joypad");
				wrap.hold(B);
				wrap.advanceFrame();
				wrap.advanceTo("joypad");
				wrap.hold(START);
				wrap.advanceFrame();
				wrap.advanceTo("joypadOverworld");
				return true;
			case S_A_B_A_B_S:
				wrap.hold(START);
				wrap.advanceFrame();
				wrap.advanceTo("joypad");
				wrap.hold(A);
				wrap.advanceFrame();
				wrap.advanceTo("joypad");
				wrap.hold(B);
				wrap.advanceFrame();
				wrap.advanceTo("joypad");
				wrap.hold(A);
				wrap.advanceFrame();
				wrap.advanceTo("joypad");
				wrap.hold(B);
				wrap.advanceFrame();
				wrap.advanceTo("joypad");
				wrap.hold(START);
				wrap.advanceFrame();
				wrap.advanceTo("joypadOverworld");
				return true;
			default:
				return false;
		}
	}
	
	private static void updateNPCTimers(GBWrapper wrap, ArrayList<Integer> timers[]) {
		for(int index = 1; index < NUM_NPCS; index++) {
			String addressPrefix = "wSprite" + Util.getSpriteAddressIndexString(index);
			if(wrap.read(addressPrefix + "SpriteImageIdx") != 0xFF) {
				timers[index].add(wrap.read(addressPrefix + "MovementDelay"));
			}
		}
	}
	
	private static boolean timeToPickUpItem(GBWrapper wrap, ArrayList<Itemball> itemballs) {
		for(Itemball itemball : itemballs) {
			if(itemball.canBePickedUp(wrap) && !itemball.isPickedUp(wrap)) {
				return true;
			}
		}
		return false;
	}
	
	private static Location getDestination(GBWrapper wrap, int input) {
		int map = wrap.read("wCurMap");
		int x = wrap.read("wXCoord");
		int y = wrap.read("wYCoord");
		if(input == LEFT) {
			return new Location(map, x - 1, y);
		} else if(input == RIGHT) {
			return new Location(map, x + 1, y);
		} else if(input == UP) {
			return new Location(map, x, y - 1);
		} else if(input == DOWN) {
			return new Location(map, x, y + 1);
		} else {
			return new Location(map, x, y);
		}
	}
}
