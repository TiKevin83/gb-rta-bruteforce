package stringflow.rta.ow;

public enum OverworldAction {
	RIGHT("R"), LEFT("L"), UP("U"), DOWN("D"), A("A"), START_B("S_B"), S_A_B_S("S_A_B_S"), S_A_B_A_B_S("S_A_B_A_B_S"), RIGHT_A("R+A"), LEFT_A("L+A"), UP_A("U+A"), DOWN_A("D+A");
	
	OverworldAction(String str) {
		this.str = str;
	}
	
	public String logStr() {
		return str;
	}
	
	public int getJoypad() {
		if(isGen2APress()) {
			return 1 | 16 * (int)(Math.pow(2.0, (ordinal() - 8)));
		} else {
			return 16 * (int)(Math.pow(2.0, (ordinal())));
		}
	}
	
	public boolean isGen2APress() {
		return str.contains("+A");
	}
	
	public static boolean isDpad(OverworldAction action) {
		return (action == RIGHT || action == LEFT || action == UP || action == DOWN);
	}
	
	public static OverworldAction fromString(String str) {
		if(str.equals("R")) {
			return RIGHT;
		} else if(str.equals("L")) {
			return LEFT;
		} else if(str.equals("U")) {
			return UP;
		} else if(str.equals("D")) {
			return DOWN;
		} else if(str.equals("A")) {
			return A;
		} else if(str.equals("S_B")) {
			return START_B;
		} else if(str.equals("S_A_B_S")) {
			return S_A_B_S;
		} else if(str.equals("S_A_B_A_B_S")) {
			return S_A_B_A_B_S;
		} else if(str.equals("R+A")) {
			return RIGHT_A;
		} else if(str.equals("L+A")) {
			return LEFT_A;
		} else if(str.equals("U+A")) {
			return UP_A;
		} else if(str.equals("D+A")) {
			return DOWN_A;
		} else {
			System.out.println(str + " ???");
			throw new IllegalArgumentException(str);
		}
	}
	
	private String str;
}
