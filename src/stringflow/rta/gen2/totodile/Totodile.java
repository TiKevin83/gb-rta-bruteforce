package stringflow.rta.gen2.totodile;

public class Totodile {
	
	private int dvs;
	private int hp;
	private int attack;
	private int defense;
	private int specialAttack;
	private int specialDefense;
	private int speed;
	
	public Totodile(int dvs, int hp, int attack, int defense, int specialAttack, int specialDefense, int speed) {
		this.dvs = dvs;
		this.hp = hp;
		this.attack = attack;
		this.defense = defense;
		this.specialAttack = specialAttack;
		this.specialDefense = specialDefense;
		this.speed = speed;
	}
	
	public int getHp() {
		return hp;
	}
	
	public int getAttack() {
		return attack;
	}
	
	public int getDefense() {
		return defense;
	}
	
	public int getSpecialAttack() {
		return specialAttack;
	}
	
	public int getSpecialDefense() {
		return specialDefense;
	}
	
	public int getSpeed() {
		return speed;
	}
	
	public int getDVs() {
		return dvs;
	}
	
	public String toString() {
		return String.format("%04X", dvs);
	}
	
	public int hashCode() {
		return dvs * 31;
	}
	
	public boolean equals(Object obj) {
		return ((Totodile)obj).dvs == dvs;
	}
}
