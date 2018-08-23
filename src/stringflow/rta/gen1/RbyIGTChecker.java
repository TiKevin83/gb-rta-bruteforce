package stringflow.rta.gen1;

import stringflow.rta.*;
import stringflow.rta.encounterigt.EncounterIGTMap;
import stringflow.rta.libgambatte.Gb;
import stringflow.rta.ow.OverworldAction;
import stringflow.rta.util.IGTTimeStamp;
import stringflow.rta.util.StringUtils;

import java.util.ArrayList;

import static stringflow.rta.Joypad.*;

public class RbyIGTChecker {
	
	public static final int NONE = 0;
	public static final int PICKUP_RARE_CANDY = 256;
	public static final int PICKUP_ESCAPE_ROPE = 512;
	public static final int PICKUP_MEGA_PUNCH = 1024;
	public static final int PICKUP_MOON_STONE = 2048;
	public static final int PICKUP_WATER_GUN = 4096;
	public static final int YOLOBALL = 8192;
	public static final int SELECT_YOLOBALL = 16384;
	public static final int REDBAR_YOLOBALL = 32768;
	public static final int REDBAR_SELECT_YOLOBALL = 65536;
	public static final int MONITOR_NPC_TIMERS = 131072;
	public static final int CREATE_SAVE_STATES = 262144;
	
	private static final int NUM_NPCS = 15;
	private static final Itemball WATER_GUN = new Itemball(0xD, new Location(59, 0x5, 0x1F), new Location(59, 0x6, 0x20));
	private static final Itemball RARE_CANDY = new Itemball(0xA, new Location(59, 0x23, 0x20), new Location(59, 0x22, 0x1F));
	private static final Itemball ESCAPE_ROPE = new Itemball(0xB, new Location(59, 0x24, 0x18), new Location(59, 0x23, 0x17));
	private static final Itemball MOON_STONE = new Itemball(0x9, new Location(59, 0x3, 0x2), new Location(59, 0x2, 0x3));
	private static final Itemball MEGA_PUNCH = new Itemball(0x9, new Location(61, 0x1C, 0x5));
	
	private static Gb gb;
	private static long params;
	private static boolean yoloballs[];
	
	public static EncounterIGTMap checkIGT0(Gb gb, ArrayList<StateBuffer> initalStates, String path, long params) {
		RbyIGTChecker.gb = gb;
		RbyIGTChecker.params = params;
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
		ArrayList<Integer>[] npcTimers = new ArrayList[NUM_NPCS];
		EncounterIGTMap igtmap = new EncounterIGTMap();
		String actions[] = path.split(" ");
		for(StateBuffer state : initalStates) {
			IGTTimeStamp igt = state.getIgt();
			byte data[] = state.getData();
			if(state == null) {
//				addIGTResult(igtmap, igt, true, false);
				continue;
			}
			gb.loadState(data);
			gb.runUntil("joypadOverworld");
			for(int i = 0; i < NUM_NPCS; i++) {
				npcTimers[i] = new ArrayList<>();
			}
			yoloballs = new boolean[4];
			if((params & MONITOR_NPC_TIMERS) != 0) {
				updateNPCTimers(gb, npcTimers);
			}
			for(String action : actions) {
				if(action.trim().isEmpty()) {
					continue;
				}
				if(!execute(OverworldAction.fromString(action), itemballs)) {
					break;
				}
				if((params & MONITOR_NPC_TIMERS) != 0) {
					updateNPCTimers(gb, npcTimers);
				}
			}
			igtmap.addResult(gb, igt, npcTimers, (params & CREATE_SAVE_STATES) != 0 ? RbyIGTChecker.gb.saveState() : null, yoloballs);
		}
		return igtmap;
	}
	
	private static boolean execute(OverworldAction owAction, ArrayList<Itemball> itemballs) {
		Address res;
		switch(owAction) {
			case LEFT:
			case UP:
			case RIGHT:
			case DOWN:
				int joypadOverworld2 = gb.getGame().getAddress("joypadOverworld").getAddress() + 1;
				int input = 16 * (int)(Math.pow(2.0, (owAction.ordinal())));
				Location dest = getDestination(gb, input);
				gb.hold(input);
				gb.runUntil(joypadOverworld2);
				Address result = gb.runUntil("joypadOverworld", "newBattle", "manualTextScroll");
				if(result.equals("manualTextScroll")) {
					return false;
				}
                /*if(Map.getMapByID(dest.map).getTile(dest.x, dest.y).isWarp()) {
                    gb.runUntil("enterMap");
                    transitionTimes.add(readIGT());
                }*/
				while(gb.read("wXCoord") != dest.x || gb.read("wYCoord") != dest.y) {
					if(result.equals("newBattle")) {
						Address result2 = gb.runUntil("encounterTest", "joypadOverworld");
						if(result2.equals("encounterTest")) {
							int hra = gb.getRandomAdd();
							if(hra < gb.read("wGrassRate")) {
								gb.frameAdvance(3);
								if((params & 0xFF) == gb.read("wEnemyMonSpecies")) {
									simulateYoloball();
								}
								return false;
							}
						}
					}
					gb.hold(0);
					gb.runUntil("joypadOverworld");
					gb.hold(input);
					gb.runUntil(joypadOverworld2);
					result = gb.runUntil("newBattle", "joypadOverworld");
				}
				gb.hold(0);
				Address result2 = gb.runUntil("encounterTest", "joypadOverworld", "manualTextScroll");
				if(result2.equals("manualTextScroll")) {
					return false;
				}
				if(result2.equals("encounterTest")) {
					int hra = gb.getRandomAdd();
					if(hra < gb.read("wGrassRate")) {
						gb.frameAdvance(3);
						if((params & 0xFF) == gb.read("wEnemyMonSpecies")) {
							simulateYoloball();
						}
						return false;
					}
					gb.hold(0);
					gb.runUntil("joypadOverworld");
					if(timeToPickUpItem(gb, itemballs)) {
						gb.press(A);
						gb.hold(A);
						gb.runUntil("TextCommand0B");
						gb.hold(0);
						gb.runUntil("joypadOverworld");
					}
				}
				return true;
			case A:
				gb.hold(A);
				gb.frameAdvance();
				res = gb.runUntil("joypadOverworld", "printLetterDelay", "manualTextScroll");
				if(res.equals("manualTextScroll")) {
					return false;
				}
				if(res.equals("joypadOverworld")) {
					return true;
				} else {
					return false;
				}
			case START_B:
				gb.hold(START);
				gb.frameAdvance();
				gb.runUntil("joypad");
				gb.hold(B);
				gb.frameAdvance();
				gb.runUntil("joypadOverworld");
				return true;
			case S_A_B_S:
				gb.hold(START);
				gb.frameAdvance();
				gb.runUntil("joypad");
				gb.hold(A);
				gb.frameAdvance();
				gb.runUntil("joypad");
				gb.hold(B);
				gb.frameAdvance();
				gb.runUntil("joypad");
				gb.hold(START);
				gb.frameAdvance();
				gb.runUntil("joypadOverworld");
				return true;
			case S_A_B_A_B_S:
				gb.hold(START);
				gb.frameAdvance();
				gb.runUntil("joypad");
				gb.hold(A);
				gb.frameAdvance();
				gb.runUntil("joypad");
				gb.hold(B);
				gb.frameAdvance();
				gb.runUntil("joypad");
				gb.hold(A);
				gb.frameAdvance();
				gb.runUntil("joypad");
				gb.hold(B);
				gb.frameAdvance();
				gb.runUntil("joypad");
				gb.hold(START);
				gb.frameAdvance();
				gb.runUntil("joypadOverworld");
				return true;
			default:
				return false;
		}
	}
	
	
	private static void simulateYoloball() {
		gb.runUntil("manualTextScroll");
		byte hpSave[] = gb.saveState();
		int currentHPAddress = gb.getGame().getAddress("wPartyMon1HP").getAddress() + 1;
		if((params & YOLOBALL) != 0 || (params & SELECT_YOLOBALL) != 0) {
			gb.write(currentHPAddress, gb.read(gb.getGame().getAddress("wPartyMon1Stats").getAddress() + 1));
			executeYoloball((params & YOLOBALL) != 0, (params & SELECT_YOLOBALL) != 0, 0);
		}
		if((params & REDBAR_YOLOBALL) != 0 || (params & REDBAR_SELECT_YOLOBALL) != 0) {
			gb.loadState(hpSave);
			gb.write(currentHPAddress, 1);
			executeYoloball((params & REDBAR_YOLOBALL) != 0, (params & REDBAR_SELECT_YOLOBALL) != 0, 2);
		}
	}
	
	private static void executeYoloball(boolean regular, boolean select, int indexOffset) {
		boolean isYellow = gb.getGame() instanceof PokeYellow;
		gb.press(A);
		gb.runUntil(isYellow && gb.read("wPartySpecies") == gb.getGame().getSpecies("PIKACHU").getIndexNumber() ? "PlayPikachuSoundClip" : "playCry");
		byte ballToss[] = gb.saveState();
		if(regular) {
			if(isYellow) {
				gb.runUntil("joypad");
				gb.press(DOWN);
				gb.runUntil("joypad");
				gb.press(A);
				gb.runUntil("joypad");
				gb.hold(A | B);
				yoloballs[indexOffset] = gb.runUntil("catchSuccess", "catchFailure").equals("catchSuccess");
			} else {
				gb.hold(DOWN | A);
				gb.runUntil("displayListMenuId");
				gb.hold(A | RIGHT);
				yoloballs[indexOffset] = gb.runUntil("catchSuccess", "catchFailure").equals("catchSuccess");
			}
		}
		if(select) {
			gb.loadState(ballToss);
			if(isYellow) {
				gb.runUntil("joypad");
				gb.press(DOWN);
				gb.runUntil("joypad");
				gb.press(A);
				gb.runUntil("joypad");
				gb.press(SELECT);
				gb.hold(A);
				yoloballs[indexOffset + 1] = gb.runUntil("catchSuccess", "catchFailure").equals("catchSuccess");
			} else {
				gb.hold(DOWN | A);
				gb.runUntil("displayListMenuId");
				gb.hold(0);
				gb.runUntil("joypad");
				gb.press(SELECT);
				gb.hold(A);
				yoloballs[indexOffset + 1] = gb.runUntil("catchSuccess", "catchFailure").equals("catchSuccess");
			}
		}
	}
	
	private static void updateNPCTimers(Gb gb, ArrayList<Integer> timers[]) {
		for(int index = 1; index < NUM_NPCS; index++) {
			String addressPrefix = "wSprite" + StringUtils.getSpriteAddressIndexString(index);
			if(gb.read(addressPrefix + "SpriteImageIdx") != 0xFF) {
				timers[index].add(gb.read(addressPrefix + "MovementDelay"));
			}
		}
	}
	
	private static boolean timeToPickUpItem(Gb gb, ArrayList<Itemball> itemballs) {
		for(Itemball itemball : itemballs) {
			if(itemball.canBePickedUp(gb) && !itemball.isPickedUp(gb)) {
				return true;
			}
		}
		return false;
	}
	
	private static Location getDestination(Gb gb, int input) {
		int map = gb.read("wCurMap");
		int x = gb.read("wXCoord");
		int y = gb.read("wYCoord");
		if(input == LEFT) {
			return new Location(map, x == 0 ? 255 : x - 1, y);
		} else if(input == RIGHT) {
			return new Location(map, x + 1, y);
		} else if(input == UP) {
			return new Location(map, x, y == 0 ? 255 : y - 1);
		} else if(input == DOWN) {
			return new Location(map, x, y + 1);
		} else {
			return new Location(map, x, y);
		}
	}
}
