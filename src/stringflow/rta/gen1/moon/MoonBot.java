package stringflow.rta.gen1.moon;

import mrwint.gbtasgen.Gb;
import stringflow.rta.*;
import stringflow.rta.gen1.*;
import stringflow.rta.gen1.astar.AStar;
import stringflow.rta.gen1.data.Map;
import stringflow.rta.gen1.data.MapDestination;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

import static stringflow.rta.Joypad.*;
import static stringflow.rta.gen1.PokeRedBlue.*;

public class MoonBot {

    private static final int NUM_NPCS = 15;
    private static ArrayList<Integer> npcTimers[][] = new ArrayList[60][NUM_NPCS];

    private static final String gameName;
    private static Checkpoint checkpoints[];
    private static Itemball itemballs[];
    private static ArrayList<Integer> ignoreFrames = new ArrayList<Integer>();
    private static PrintWriter partialManips;
    private static PrintWriter foundManips;
    private static Gb gb;
    private static GBWrapper wrap;

    private static final Location start = new Location(14, 34);
    private static long lastTimingInfoPrintTime = 0;
    private static long seenStates = 0;

    private static ByteBuffer initalSaves[];

    static {
        gameName = "red";
        checkpoints = new Checkpoint[]{new Checkpoint(59, 5, 31, 4, 0, 60),
                                    new Checkpoint(59, 34, 31, 4, 0, 60),
                                    //new Checkpoint(59, 36, 24, 2, 0, 60),
                                     //new Checkpoint(59, 17, 10, 4, 0, 60),
        };
        itemballs = new Itemball[]{Itemball.WATER_GUN, Itemball.RARE_CANDY, Itemball.ESCAPE_ROPE, Itemball.MEGA_PUNCH, Itemball.MOON_STONE};
        ignoreFrames.add(36);
        ignoreFrames.add(37);
    }

    public static void main(String[] args) throws IOException, InterruptedException {
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
        for(int i = 0; i < 60; i++) {
            for(int j = 0; j < NUM_NPCS; j++) {
                npcTimers[i][j] = new ArrayList<Integer>();
            }
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
        seenStates = 0;
        ArrayList<IntroSequence> introSequences = new ArrayList<>();
        introSequences.add(new IntroSequence(nopal, gfSkip, intro0, title, cont, cont));
        Collections.sort(introSequences);
        System.out.println("Number of intro sequences: " + introSequences.size());

        long startTime = System.currentTimeMillis();
        OverworldTile[][] owTiles1 = AStar.initTiles(Map.MT_MOON_1, 17, 3, new MapDestination(Map.MT_MOON_1, new Location(5, 31)));
        OverworldTile[][] owTiles2 = AStar.initTiles(Map.MT_MOON_1, 17, 3, new MapDestination(Map.MT_MOON_1, new Location(34, 31)));
        long endTime = System.currentTimeMillis();
        System.out.println("Generic edge generation time: " + (endTime - startTime) + " ms");

        owTiles1[6][31].getEdgeList().remove(owTiles1[6][31].getEdge(OverworldAction.LEFT));
        owTiles1[34][32].getEdgeList().remove(owTiles1[34][32].getEdge(OverworldAction.UP));

        owTiles1[5][30].getEdge(OverworldAction.DOWN).setNextPos(owTiles2[5][31]);

        OverworldTile savePos = owTiles1[start.x][start.y];
        LibgambatteBuilder.buildGambatte(false, 100);
        Gb.loadGambatte(1);
        gb = new Gb(0, false);
        gb.startEmulator("roms/poke" + gameName + ".gbc");
        wrap = new GBWrapper(gb, "roms/poke" + gameName + ".sym", hJoypad);
        wrap.advanceTo("joypad");
        IntroSequence intro = new IntroSequence(nopal, gfSkip, intro0, title, cont, cont);
        lastTimingInfoPrintTime = System.currentTimeMillis();
        OverworldState owState = new OverworldState(savePos.toString() + " - " + intro.toString() + ":", savePos, initalSaves, checkpoints[0], 1, 0, 0, true, 0, 0);
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
        seenStates++;
        if(seenStates % 1000 == 0) {
            long time = System.currentTimeMillis();
            System.out.printf(Locale.ENGLISH, "Seen states: %d (%d ms per state)\n", seenStates, (time - lastTimingInfoPrintTime) / 1000);
            lastTimingInfoPrintTime = time;
        }
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
            ByteBuffer[] newSaves = checkIGT0(ow, edgeAction);
            partialManips.println(ow.toString() + " " + edgeAction.logStr() + ", cost: " + (ow.getWastedFrames() + edgeCost) + ", owFrames: " + (owFrames) + " - " + encounterIgt0 + "/60 ");
            partialManips.flush();
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
                        String path = ow.toString().substring(ow.toString().indexOf(":") + 2);
                        String npcPaths[] = checkNPCs(path);
                        ArrayList<String> seenNPCPaths = new ArrayList<String>();
                        for(int i = 0; i < 60; i++) {
                            if(npcPaths[i] != null) {
                                if(!ignoreFrames.contains(i) && !seenNPCPaths.contains(npcPaths[i])) {
                                    seenNPCPaths.add(npcPaths[i]);
                                }
                            }
                        }
                        if(seenNPCPaths.size() != 1) {
                            continue;
                        }
                        if(ow.getCurrentTarget() != lastCheckpoint) {
                            newCheckpoint = checkpoints[currentCheckpointIndex + 1];
                        }
                    }
                    if(edge.getNextPos().getX() == lastCheckpoint.getX() && edge.getNextPos().getY() == lastCheckpoint.getY() && edge.getNextPos().getMap() == lastCheckpoint.getMap() && encounterIgt0 >= lastCheckpoint.getMinConsistency()) {
                        foundManips.println(ow.toString() + " " + edgeAction.logStr() + ", cost: " + (ow.getWastedFrames() + edgeCost) + ", owFrames: " + (owFrames) + " - " + encounterIgt0 + "/60 ");
                        foundManips.flush();
                        break;
                    }
                    newState = new OverworldState(ow.toString() + " " + edgeAction.logStr(), edge.getNextPos(), newSaves, newCheckpoint, Math.max(0, ow.aPressCounter() - 1), ow.getNumAPresses(), ow.getNumStartPresses(), true, ow.getWastedFrames() + edgeCost, ow.getOverworldFrames() + edge.getFrames());
                    overworldSearch(newState);
                    break;
                case A:
                    newState = new OverworldState(ow.toString() + " " + edgeAction.logStr(), edge.getNextPos(), newSaves, ow.getCurrentTarget(), 2, ow.getNumAPresses() + 1, ow.getNumStartPresses(), true, ow.getWastedFrames() + 2, ow.getOverworldFrames() + 2);
                    overworldSearch(newState);
                    break;
                case START_B:
                    newState = new OverworldState(ow.toString() + " " + edgeAction.logStr(), edge.getNextPos(), newSaves, ow.getCurrentTarget(), 1, ow.getNumAPresses(), ow.getNumStartPresses() + 1, true, ow.getWastedFrames() + 2, ow.getOverworldFrames() + 2);
                    overworldSearch(newState);
                    break;
                default:
                    break;
            }
        }
    }

    private static int encounterIgt0;

    private static int getLastSuccess(ByteBuffer[] resultSaves, int currentIndex) {
        for(int i = currentIndex; i >= 0; i--) {
            if(resultSaves[i] != null) {
                return i;
            }
        }
        return -1;
    }

    private static ByteBuffer[] checkIGT0(OverworldState state, OverworldAction action) {
        encounterIgt0 = 60;
        ByteBuffer[] resultSaves = new ByteBuffer[60];
        for(int i = 0; i < 60; i++) {
            if(state.getSaves()[i] == null) {
                resultSaves[i] = null;
                encounterIgt0--;
                continue;
            }
            gb.loadState(state.getSaves()[i]);
            wrap.advanceTo("joypadOverworld");
            boolean success = execute(action);
            if(!success) {
                resultSaves[i] = null;
                encounterIgt0--;
            } else {
                resultSaves[i] = gb.saveState();
            }
        }
        return resultSaves;
    }

    private static String[] checkNPCs(String path) {
        String result[] = new String[60];
        String actions[] = path.split(" ");
        for(int i = 0; i < 60; i++) {
            if(initalSaves[i] == null) {
                continue;
            }
            for(int j = 1; j < NUM_NPCS; j++) {
                npcTimers[i][j].clear();
            }
            gb.loadState(initalSaves[i]);
            wrap.advanceTo("joypadOverworld");
            for(int j = 0; j < actions.length; j++) {
                OverworldAction owAction = OverworldAction.fromString(actions[j]);
                boolean success = execute(owAction);
                if(!success) {
                    result[i] = null;
                }
                wrap.hold(0);
                if(owAction != OverworldAction.A && owAction != OverworldAction.START_B) {
                    for(int k = 1; k < NUM_NPCS; k++) {
                        ArrayList<Integer> timer = npcTimers[i][k];
                        int readTimer = wrap.read("wSprite" + Util.getSpriteAddressIndexString(k) + "MovementDelay");
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
            for(int k = 1; k < NUM_NPCS; k++) {
                ArrayList<Integer> timer = npcTimers[i][k];
                if(!timer.isEmpty()) {
                    for(int j = 0; j < timer.size(); j++) {
                        if(timer.get(j) <= 8) {
                            int index = j + 1;
                            if(timer.size() > index) {
                                npcString += "Timer " + k + " expired at " + index + ", ";
                            }
                        }
                    }
                }
            }
            npcString = npcString.equals("[") ? "" : npcString.substring(0, npcString.length() - 2) + "]";
            result[i] = npcString;
        }
        return result;
    }

    private static boolean execute(OverworldAction owAction) {
        Address res;
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
                Address result = wrap.advanceTo("joypadOverworld", "newBattle", "manualTextScroll");
                if(result.equals("manualTextScroll")) {
                    System.out.println("TEXTBOX HIT AT " + wrap.read("wXCoord") + " " + wrap.read("wYCoord"));
                    return false;
                }
                while(wrap.read("wXCoord") != dest.x || wrap.read("wYCoord") != dest.y) {
                    if(result.equals("newBattle")) {
                        Address result2 = wrap.advanceTo(encounterTest, "joypadOverworld");
                        if(result2.equals(encounterTest)) {
                            int hra = wrap.read(hRandomAdd);
                            int hrs = wrap.read(hRandomSub);
                            int rdiv = gb.getDivState();
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
                    System.out.println("TEXTBOX HIT AT " + wrap.read("wXCoord") + " " + wrap.read("wYCoord"));
                    return false;
                }
                if(result2.equals(encounterTest)) {
                    int hra = wrap.read(hRandomAdd);
                    int hrs = wrap.read(hRandomSub);
                    int rdiv = gb.getDivState();
                    if(hra < wrap.read("wGrassRate")) {
                        wrap.advanceFrame();
                        wrap.advanceFrame();
                        wrap.advanceFrame();
                        return false;
                    }
                    wrap.hold(0);
                    wrap.advanceTo("joypadOverworld");
                    if(timeToPickUpItem()) {
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
                    System.out.println("TEXTBOX HIT AT " + wrap.read("wXCoord") + " " + wrap.read("wYCoord"));
                    return false;
                }
                if(res.equals("joypadOverworld")) {
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
