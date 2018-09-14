package stringflow.rta.ow;

import stringflow.rta.Checkpoint;
import stringflow.rta.IGTState;

import java.util.ArrayList;
import java.util.HashSet;

public class OverworldState {
	
	private String str;
	private OverworldTile pos;
	private ArrayList<IGTState> states;
	private Checkpoint currentTarget;
	private int numStartPresses;
	private int numAPresses;
	private int aPress;
	private boolean startPress;
	private int wastedFrames;
	private int overworldFrames;
	private int hra;
	private int rdiv;
	
	public OverworldState(String str, OverworldTile pos, ArrayList<IGTState> states, Checkpoint currentTarget, int aPress, int numStartPresses, int numAPresses, boolean startPress, int wastedFrames, int overworldFrames) {
		this.str = str;
		this.pos = pos;
		this.aPress = aPress;
		this.currentTarget = currentTarget;
		this.states = states;
		this.numStartPresses = numStartPresses;
		this.numAPresses = numAPresses;
		this.startPress = startPress;
		this.wastedFrames = wastedFrames;
		this.overworldFrames = overworldFrames;
		this.hra = -1;
		this.rdiv = -1;
	}
	
	public OverworldState(String str, OverworldTile pos, ArrayList<IGTState> states, Checkpoint currentTarget, int aPress, int numStartPresses, int numAPresses, boolean startPress, int wastedFrames, int overworldFrames, int hra, int rdiv) {
		this.str = str;
		this.pos = pos;
		this.aPress = aPress;
		this.currentTarget = currentTarget;
		this.states = states;
		this.numStartPresses = numStartPresses;
		this.numAPresses = numAPresses;
		this.startPress = startPress;
		this.wastedFrames = wastedFrames;
		this.overworldFrames = overworldFrames;
		this.hra = hra;
		this.rdiv = rdiv;
	}
	
	public int getOverworldFrames() {
		return overworldFrames;
	}
	
	public int getWastedFrames() {
		return wastedFrames;
	}
	
	public int getMap() {
		return pos.getMap();
	}
	
	public int getX() {
		return pos.getX();
	}
	
	public int getY() {
		return pos.getY();
	}
	
	public int aPressCounter() {
		return aPress;
	}
	
	public boolean canPressStart() {
		return startPress;
	}
	
	public OverworldTile getPos() {
		return pos;
	}
	
	public ArrayList<IGTState> getStates() {
		return states;
	}
	
	public int getNumStartPresses() {
		return numStartPresses;
	}
	
	public Checkpoint getCurrentTarget() {
		return currentTarget;
	}
	
	public int getNumAPresses() {
		return numAPresses;
	}
	
	public int getHra() {
		return hra;
	}
	
	public int getRdiv() {
		return rdiv;
	}
	
	public String getUniqId() {
		return pos.getMap() + "#" + pos.getX() + "," + pos.getY() + "-" + hra + "-" + rdiv;
	}
	
	public void addSimiliarStates(HashSet<String> set, int hraErrorMargin) {
		for(int i = -hraErrorMargin; i <= hraErrorMargin; i++) {
			set.add(pos.getMap() + "#" + pos.getX() + "," + pos.getY() + "-" + (hra + i) + "-" + rdiv);
		}
	}
	
	public int getNumValidSaves() {
		int result = 0;
		for(int i = 0; i < states.size(); i++) {
			if(states.get(0).getState() != null) {
				result++;
			}
		}
		return result;
	}
	
	@Override
	public String toString() {
		return str;
	}
	
	@Override
	public boolean equals(Object other) {
		OverworldState o = (OverworldState) other;
		return this.getMap() == o.getMap() && this.getX() == o.getX() && this.getY() == o.getY() && this.getHra() == o.getHra() && this.getRdiv() == o.getRdiv();
	}
	
	@Override
	public int hashCode() {
		return this.getMap() + 7 * this.getX() + 13 * this.getY() + hra * 17 + rdiv * 19;
	}
}