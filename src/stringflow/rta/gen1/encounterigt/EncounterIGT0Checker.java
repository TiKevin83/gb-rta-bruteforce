package stringflow.rta.gen1.encounterigt;

import stringflow.rta.*;
import stringflow.rta.gen1.Itemball;
import stringflow.rta.gen1.OverworldAction;
import stringflow.rta.gen1.PokeRedBlue;
import stringflow.rta.gen1.PokeYellow;
import stringflow.rta.gen1.data.Species;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import static stringflow.rta.Joypad.*;

public class EncounterIGT0Checker {
	
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
	private static final Itemball WATER_GUN = new Itemball(0xC, new Location(59, 0x5, 0x1F), new Location(59, 0x6, 0x20));
	private static final Itemball RARE_CANDY = new Itemball(0xA, new Location(59, 0x23, 0x20), new Location(59, 0x22, 0x1F));
	private static final Itemball ESCAPE_ROPE = new Itemball(0xB, new Location(59, 0x24, 0x18));
	private static final Itemball MOON_STONE = new Itemball(0x9, new Location(59, 0x3, 0x2), new Location(59, 0x2, 0x3));
	private static final Itemball MEGA_PUNCH = new Itemball(0x9, new Location(61, 0x1C, 0x5));
	
	private static GBWrapper wrap;
	private static long params;
	private static boolean yoloballs[];
	
	public static EncounterIGTMap checkIGT0(GBWrapper wrap, ByteBuffer initalStates[], String path, long params) {
		EncounterIGT0Checker.wrap = wrap;
		EncounterIGT0Checker.params = params;
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
		EncounterIGTMap igtmap = new EncounterIGTMap(maxSecond);
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
				yoloballs = new boolean[4];
				if((params & MONITOR_NPC_TIMERS) != 0) {
					updateNPCTimers(wrap, npcTimers);
				}
				for(String action : actions) {
					if(action.trim().isEmpty()) {
						continue;
					}
					if(!execute(OverworldAction.fromString(action), itemballs)) {
						break;
					}
					if((params & MONITOR_NPC_TIMERS) != 0) {
						updateNPCTimers(wrap, npcTimers);
					}
				}
				igtmap.addResult(wrap, index, npcTimers, (params & CREATE_SAVE_STATES) != 0 ? wrap.saveState() : null, yoloballs);
			}
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
								if((params & 0xFF) == wrap.read("wEnemyMonSpecies")) {
									simulateYoloball();
								}
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
						if((params & 0xFF) == wrap.read("wEnemyMonSpecies")) {
							simulateYoloball();
						}
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
	
	private static void simulateYoloball() {
		wrap.advanceTo("manualTextScroll");
		ByteBuffer hpSave = wrap.saveState();
		int currentHPAddress = wrap.getAddress("wPartyMon1HP") + 1;
		if((params & YOLOBALL) != 0 || (params & SELECT_YOLOBALL) != 0) {
			wrap.write(currentHPAddress, wrap.read(wrap.getAddress("wPartyMon1Stats") + 1));
			executeYoloball((params & YOLOBALL) != 0, (params & SELECT_YOLOBALL) != 0, 0);
		}
		if((params & REDBAR_YOLOBALL) != 0 || (params & REDBAR_SELECT_YOLOBALL) != 0) {
			wrap.loadState(hpSave);
			wrap.write(currentHPAddress, 1);
			executeYoloball((params & REDBAR_YOLOBALL) != 0, (params & REDBAR_SELECT_YOLOBALL) != 0, 2);
		}
	}
	
	private static void executeYoloball(boolean regular, boolean select, int indexOffset) {
		wrap.hold(0);
		boolean isYellow = wrap.getGameName().contains("YELLOW");
		wrap.press(A);
		wrap.advanceTo(isYellow && wrap.read("wPartySpecies") == Species.PIKACHU.getIndexNumber() ? "PlayPikachuSoundClip" : "playCry");
		ByteBuffer ballToss = wrap.saveState();
		if(regular) {
			if(isYellow) {
				wrap.advanceTo("joypad");
				wrap.press(DOWN);
				wrap.advanceTo("joypad");
				wrap.press(A);
				wrap.advanceTo("joypad");
				wrap.hold(A | B);
				yoloballs[indexOffset] = wrap.advanceTo(PokeYellow.catchSuccess, PokeYellow.catchFailure).getAddress() == PokeYellow.catchSuccess;
			} else {
				wrap.hold(DOWN | A);
				wrap.advanceTo("displayListMenuId");
				wrap.hold(A | RIGHT);
				yoloballs[indexOffset] = wrap.advanceTo(PokeRedBlue.catchSuccess, PokeRedBlue.catchFailure).getAddress() == PokeRedBlue.catchSuccess;
			}
		}
		if(select) {
			wrap.loadState(ballToss);
			if(isYellow) {
				wrap.advanceTo("joypad");
				wrap.press(DOWN);
				wrap.advanceTo("joypad");
				wrap.press(A);
				wrap.advanceTo("joypad");
				wrap.press(SELECT);
				wrap.hold(A);
				yoloballs[indexOffset + 1] = wrap.advanceTo(PokeYellow.catchSuccess, PokeYellow.catchFailure).getAddress() == PokeYellow.catchSuccess;
			} else {
				wrap.hold(DOWN | A);
				wrap.advanceTo("displayListMenuId");
				wrap.hold(0);
				wrap.advanceTo("joypad");
				wrap.press(SELECT);
				wrap.hold(A);
				yoloballs[indexOffset + 1] = wrap.advanceTo(PokeRedBlue.catchSuccess, PokeRedBlue.catchFailure).getAddress() == PokeRedBlue.catchSuccess;
			}
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
