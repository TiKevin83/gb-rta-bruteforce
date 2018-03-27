package stringflow.rta.gen1;

import stringflow.rta.gen1.moon.Checkpoint;

import java.nio.ByteBuffer;

public class OverworldState {

	private String str;
	private OverworldTile pos;
	private ByteBuffer[] saves;
	private Checkpoint currentTarget;
	private int numStartPresses;
	private int numAPresses;
	private int aPress;
	private boolean startPress;
	private int wastedFrames;
	private int overworldFrames;

	public OverworldState(String str, OverworldTile pos, ByteBuffer[] saves, Checkpoint currentTarget, int aPress, int numStartPresses, int numAPresses, boolean startPress, int wastedFrames, int overworldFrames) {
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

	public ByteBuffer[] getSaves() {
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

	@Override
	public String toString() {
		return str;
	}

	@Override
	public boolean equals(Object other) {
		OverworldState o = (OverworldState) other;
		return this.getMap() == o.getMap() && this.getX() == o.getX() && this.getY() == o.getY();
	}

	@Override
	public int hashCode() {
		return this.getMap() + 2 * this.getX() + 3 * this.getY();
	}

	public String getUniqId() {
		return "" + pos.getMap() + "#" + pos.getX() + "," + pos.getY() + "-";
	}
	
    public long getFfefUniqId() {
        int baseX = (pos.getMap() == 1) ? 40 : 0;
        int baseY = (pos.getMap() == 1) ? 162 : 170;
        long pwX = baseX + pos.getX();
        long pwY = baseY + pos.getY();
        return (pwX << 56) + (pwY << 48);
    }
}