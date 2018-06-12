package stringflow.rta.gen2;

public class Totodile {
	
	private static final int HP_BASE = 50;
	private static final int ATK_BASE = 65;
	private static final int DEF_BASE = 64;
	private static final int SPATK_BASE = 44;
	private static final int SPDEF_BASE = 48;
	private static final int SPD_BASE = 43;
	
	private int dvs;
	
	public Totodile(int dvs) {
		this.dvs = dvs;
	}
	
	public int getDVs() {
		return dvs;
	}
	
	public String toString() {
		return String.format("%04X", dvs);
	}
	
	public int getHP() {
		int dv = (((dvs >> 12) & 1) << 3) | (((dvs >> 8) & 1) << 2) | (((dvs >> 4) & 1) << 1) | (dvs) & 1;
		return (int)Math.floor((2 * HP_BASE + dv) * 5 / 100 + 5 + 10);
	}
	
	public int getAttack() {
		return calcStat(ATK_BASE, (dvs >> 12) & 0xF);
	}
	
	public int getDefense() {
		return calcStat(DEF_BASE, (dvs >> 8) & 0xF);
	}
	
	public int getSpecialAttack() {
		return calcStat(SPATK_BASE, (dvs >> 0) & 0xF);
	}
	
	public int getSpecialDefense() {
		return calcStat(SPDEF_BASE, (dvs >> 0) & 0xF);
	}
	
	public int getSpeed() {
		return calcStat(SPD_BASE, (dvs >> 4) & 0xF);
	}
	
	private int calcStat(int base, int dv) {
		return (int)Math.floor((2 * base + dv) * 5 / 100 + 5);
	}
	
	public int hashCode() {
		return dvs * 31;
	}
	
	public boolean equals(Object obj) {
		return ((Totodile)obj).dvs == dvs;
	}
}
