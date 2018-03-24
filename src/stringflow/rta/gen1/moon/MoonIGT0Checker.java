package stringflow.rta.gen1.moon;

import mrwint.gbtasgen.Gb;
import stringflow.rta.GBWrapper;
import stringflow.rta.LibgambatteBuilder;
import stringflow.rta.Location;
import stringflow.rta.Util;
import stringflow.rta.gen1.Itemball;
import stringflow.rta.gen1.OverworldAction;
import stringflow.rta.gen1.data.Species;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import static stringflow.rta.gen1.PokeYellow.*;
import static stringflow.rta.Joypad.*;

public class MoonIGT0Checker {

    public static final int NUM_NPCS = 15;
    public static final Itemball WATER_GUN = new Itemball(0xC, new Location(59, 0x5, 0x1F), new Location(59, 0x6, 0x20));
    public static final Itemball RARE_CANDY = new Itemball(0xA, new Location(59, 0x23, 0x20), new Location(59, 0x22, 0x1F));
    public static final Itemball ESCAPE_ROPE = new Itemball(0xB, new Location(59, 0x24, 0x18));
    public static final Itemball MOON_STONE = new Itemball(0x9, new Location(59, 0x3, 0x2), new Location(59, 0x2, 0x3));
    public static final Itemball MEGA_PUNCH = new Itemball(0x9, new Location(61, 0x1C, 0x5));

    private static String gameName;
    private static String path;
    private static Itemball itemballs[];

    private static ArrayList<Integer> npcTimers[] = new ArrayList[NUM_NPCS];
    private static HashMap<IGTEncounter, Integer> igtEncounters = new HashMap<IGTEncounter, Integer>();
    private static Gb gb;
    private static GBWrapper wrap;

    static {
        gameName = "yellow";
        /* YELLOW MOON */  path = "U S_B U U U U U U U U U U U R R R R R R R U U U U U U U R R R D D D D D D D D D D R D D D A D D D D A R R R R R R R R U R R U U U U A U U U U U U U L U U U U U U U U U L A L L U U U U U U U U A L L A L L L L L D L L L A L L L L L D D D D D A D D D A R D D D D L D L L L L L A L L A L L L U L L L L U U U U U U U U A U U U U U R R R D D R R D D D D A D D D A D D A D D D R R R R R R R R R R R A R R R U A R R A U U R R R D S_B D R R R R R R R U U R R R D D D D A D D D D L L L L D D D A D D D D D D A L L L L L L L L A L L A L L L L L A L L A L L A L L U U U U U U A U U U A U U A U U ";
        /* RED TEST */ //path = "U U S_B U A U U U S_B U";
        itemballs = new Itemball[]{RARE_CANDY, MOON_STONE};
    }

    public static void main(String args[]) throws Exception {
        if(!new File("logs").exists()) {
            new File("logs").mkdir();
        }
        if(!new File("roms").exists()) {
            new File("roms").mkdir();
            System.err.println("I need ROMs to simulate!");
            System.exit(0);
        }
        if(!new File("roms/poke" + gameName + ".gbc").exists()) {
            System.err.println("Could not find poke" + gameName + ".gbc in roms directory!");
            System.exit(0);
        }
        if(!new File("roms/poke" + gameName + ".sym").exists()) {
            System.err.println("Could not find poke" + gameName + ".sym in roms directory!");
            System.exit(0);
        }
        for(int i = 0; i < NUM_NPCS; i++) {
            npcTimers[i] = new ArrayList<Integer>();
        }
        LibgambatteBuilder.buildGambatte(false, 100);
        Gb.loadGambatte(1);
        gb = new Gb(0, false);
        gb.startEmulator("roms/poke" + gameName + ".gbc");
        wrap = new GBWrapper(gb, "roms/poke" + gameName + ".sym", hJoypad);
        gfSkip.execute(wrap);
        intro0.execute(wrap);
        title.execute(wrap);
        wrap.advanceTo(igtInjectAddr);
        ByteBuffer save = gb.saveState();
        String actions[] = path.split(" ");
        int successes = 60 * 60;
        for(int second = 0; second < 60; second++) {
            outer:
            for(int frame = 0; frame < 60; frame++) {
                for(int i = 1; i < NUM_NPCS; i++) {
                    npcTimers[i].clear();
                }
                gb.loadState(save);
                wrap.write("wPlayTimeSeconds", second);
                wrap.write("wPlayTimeFrames", frame);
                cont.execute(wrap);
                cont.execute(wrap);
                wrap.advanceTo("joypadOverworld");
                if(actions.length > 1) {
                    for(int j = 0; j < actions.length; j++) {
                        OverworldAction owAction = OverworldAction.fromString(actions[j]);
                        if(!execute(owAction, second, frame)) {
                            successes--;
                            continue outer;
                        }
                        if(owAction != OverworldAction.A && owAction != OverworldAction.START_B) {
                            for(int i = 1; i < NUM_NPCS; i++) {
                                ArrayList<Integer> timer = npcTimers[i];
                                int readTimer = wrap.read("wSprite" + Util.getSpriteAddressIndexString(i) + "MovementDelay");
                                if(readTimer != 0) {
                                    if(timer.size() > 0) {
                                        int last = timer.get(timer.size() - 1);
                                        if(readTimer != last) {
                                            timer.add(readTimer);
                                        }
                                    } else {
                                        timer.add(readTimer);
                                    }
                                }
                            }
                        }
                    }
                    String npcString = "[";
                    for(int i = 1; i < NUM_NPCS; i++) {
                        ArrayList<Integer> timer = npcTimers[i];
                        if(!timer.isEmpty()) {
                            for(int j = 0; j < timer.size(); j++) {
                                if(timer.get(j) <= 8) {
                                    int index = j + 1;
                                    if(timer.size() > index) {
                                        npcString += "Timer " + i + " expired at " + index + ", ";
                                    }
                                }
                            }
                        }
                    }
                    npcString = npcString.equals("[") ? "" : npcString.substring(0, npcString.length() - 2) + "]";
                    System.out.printf("S%d F%d [S] No encounter at map %d x %d y %d %s\n", second, frame, wrap.read("wCurMap"), wrap.read("wXCoord"), wrap.read("wYCoord"), npcString);
                }
            }
        } System.out.println(successes + "/60");
        System.out.println("-------------------------------------------------");
        for(IGTEncounter encounter : igtEncounters.keySet()) {
            System.out.printf("%d/%d level %d %s at %s\n", igtEncounters.get(encounter), 60, encounter.getLevel(), Species.getSpeciesByIndexNumber(encounter.getSpecies()).getName(), "http://pokeworld.herokuapp.com/rb/" + encounter.getMap() + "#" + encounter.getX() + "," + encounter.getY());
        }
    }

    private static boolean execute(OverworldAction owAction, int second, int frame) {
        int res;
        switch(owAction) {
            case LEFT:
            case UP:
            case RIGHT:
            case DOWN:
                int encounterTest = wrap.getAddress("TryDoWildEncounter") + 0x54;
                int input = 16 * (int) (Math.pow(2.0, (owAction.ordinal())));
                Location dest = getDestination(input);
                wrap.hold(input);
                wrap.advanceTo(wrap.getAddress("joypadOverworld") + 1);
                int result = wrap.advanceTo("joypadOverworld", "newBattle", "manualTextScroll");
                if(result == wrap.getAddress("manualTextScroll")) {
                    System.out.println("TEXTBOX HIT AT " + wrap.read("wXCoord") + " " + wrap.read("wYCoord"));
                    return false;
                }
                while(wrap.read("wXCoord") != dest.x || wrap.read("wYCoord") != dest.y) {
                    if(result == wrap.getAddress("newBattle")) {
                        int result2 = wrap.advanceTo(encounterTest, wrap.getAddress("joypadOverworld"));
                        if(result2 == encounterTest) {
                            int hra = wrap.read(hRandomAdd);
                            if(hra < wrap.read("wGrassRate")) {
                                wrap.advanceFrame();
                                wrap.advanceFrame();
                                wrap.advanceFrame();
                                System.out.printf("S%d F%d [F] Encounter at map %d x %d y %d Species %d Level %d DVs %04X [turnframe]\n", second, frame, wrap.read("wCurMap"), wrap.read("wXCoord"), wrap.read("wYCoord"), wrap.read("wEnemyMonSpecies"), wrap.read("wEnemyMonLevel"), wrap.read("wEnemyMonDVs"));
                                addIGTEncounter(new IGTEncounter(wrap.read("wXCoord"), wrap.read("wYCoord"), wrap.read("wCurMap"), wrap.read("wEnemyMonSpecies"), wrap.read("wEnemyMonLevel"), true));
                                return false;
                            }
                        }
                    }
                    wrap.advanceTo("joypadOverworld");
                    wrap.advanceTo(wrap.getAddress("joypadOverworld") + 1);
                    result = wrap.advanceTo("newBattle", "joypadOverworld");
                }
                int result2 = wrap.advanceTo(encounterTest, wrap.getAddress("joypadOverworld"), wrap.getAddress("manualTextScroll"));
                if(result == wrap.getAddress("manualTextScroll")) {
                    System.out.println("TEXTBOX HIT AT " + wrap.read("wXCoord") + " " + wrap.read("wYCoord"));
                    return false;
                }
                if(result2 == encounterTest) {
                    int hra = wrap.read(hRandomAdd);
                    if(hra < wrap.read("wGrassRate")) {
                        wrap.advanceFrame();
                        wrap.advanceFrame();
                        wrap.advanceFrame();
                        System.out.printf("S%d F%d [F] Encounter at map %d x %d y %d Species %d Level %d DVs %04X\n", second, frame, wrap.read("wCurMap"), wrap.read("wXCoord"), wrap.read("wYCoord"), wrap.read("wEnemyMonSpecies"), wrap.read("wEnemyMonLevel"), wrap.read("wEnemyMonDVs"));
                        addIGTEncounter(new IGTEncounter(wrap.read("wXCoord"), wrap.read("wYCoord"), wrap.read("wCurMap"), wrap.read("wEnemyMonSpecies"), wrap.read("wEnemyMonLevel"), false));
                        return false;
                    }
                    wrap.advanceTo("joypadOverworld");
                    if(timeToPickUpItem()) {
                        wrap.hold(0);
                        wrap.press(A);
                        wrap.advanceTo("TextCommand0B");
                        wrap.advanceTo("joypadOverworld");
                    }
                }
                return true;
            case A:
                wrap.hold(A);
                wrap.advanceFrame();
                res = wrap.advanceTo("joypadOverworld", "printLetterDelay", "manualTextScroll");
                if(res == wrap.getAddress("manualTextScroll")) {
                    System.out.println("TEXTBOX HIT AT " + wrap.read("wXCoord") + " " + wrap.read("wYCoord"));
                    return false;
                }
                if(res == wrap.getAddress("joypadOverworld")) {
                    return true;
                } else {
                    System.out.println("REACHED PRINTLETTERDELAY");
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

    private static void addIGTEncounter(IGTEncounter enc) {
        for(IGTEncounter enc2 : igtEncounters.keySet()) {
            if(enc.equals(enc2)) {
                igtEncounters.put(enc2, igtEncounters.get(enc2) + 1);
                return;
            }
        }
        igtEncounters.put(enc, 1);
    }

    private static boolean timeToPickUpItem() {
        for(Itemball itemball : itemballs) {
            if(itemball.canBePickedUp(wrap) && !itemball.isPickedUp(wrap)) {
                return true;
            }
        }
        return false;
    }

    private static Location getDestination(int input) {
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
