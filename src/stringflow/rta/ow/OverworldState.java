package stringflow.rta.ow;

import stringflow.rta.Checkpoint;

import java.nio.ByteBuffer;

public class OverworldState {
	
	private String str;
	private OverworldTile pos;
	private byte[][] saves;
	private Checkpoint currentTarget;
	private int numStartPresses;
	private int numAPresses;
	private int aPress;
	private boolean startPress;
	private int wastedFrames;
	private int overworldFrames;
	private int hra;
	private int hrs;
	
	public OverworldState(String str, OverworldTile pos, byte[][] saves, Checkpoint currentTarget, int aPress, int numStartPresses, int numAPresses, boolean startPress, int wastedFrames, int overworldFrames) {
		this.str = str;
		this.pos = pos;
		this.aPress = aPress;
		this.currentTarget = currentTarget;
		this.saves = saves;
		this.numStartPresses = numStartPresses;
		this.numAPresses = numAPresses;
		this.startPress = startPress;
		this.wastedFrames = wastedFrames;
		this.overworldFrames = overworldFrames;
		this.hra = -1;
		this.hrs = -1;
	}
	
	public OverworldState(String str, OverworldTile pos, byte[][] saves, Checkpoint currentTarget, int aPress, int numStartPresses, int numAPresses, boolean startPress, int wastedFrames, int overworldFrames, int hra, int hrs) {
		this.str = str;
		this.pos = pos;
		this.aPress = aPress;
		this.currentTarget = currentTarget;
		this.saves = saves;
		this.numStartPresses = numStartPresses;
		this.numAPresses = numAPresses;
		this.startPress = startPress;
		this.wastedFrames = wastedFrames;
		this.overworldFrames = overworldFrames;
		this.hra = hra;
		this.hrs = hrs;
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
	
	public byte[][] getSaves() {
		return saves;
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
	
	public int getHrs() {
		return hrs;
	}
	
	public String getUniqId() {
		return pos.getMap() + "#" + pos.getX() + "," + pos.getY() + "-" + hra + "-" + hrs + "-" + getNumValidSaves();
	}
	
	public int getNumValidSaves() {
		int result = 0;
		for(int i = 0; i < saves.length; i++) {
			if(saves[i] != null) {
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
		return this.getMap() == o.getMap() && this.getX() == o.getX() && this.getY() == o.getY() && (hrs == -1 || ( this.getHra() == o.getHra() && this.getHrs() == o.getHrs())) && this.getNumValidSaves() == o.getNumValidSaves();
	}
	
	@Override
	public int hashCode() {
		return this.getMap() + 7 * this.getX() + 13 * this.getY() + (hra != -1 ? hra * 17 + hrs * 19 : 0) + 27 * this.getNumValidSaves();
	}
}