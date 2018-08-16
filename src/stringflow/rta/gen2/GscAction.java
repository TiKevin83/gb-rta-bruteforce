package stringflow.rta.gen2;

public enum GscAction {
	
	RIGHT("R"), LEFT("L"), UP("U"), DOWN("D"), START_B("S_B");
	
	private String logStr;
	
	private GscAction(String logStr) {
		this.logStr = logStr;
	}
	
	public String getLogStr() {
		return logStr;
	}
	
	public int getJoypad() {
		return 16 * (int)(Math.pow(2.0, (ordinal())));
	}
	
	public static GscAction fromString(String str) {
		if(str.equalsIgnoreCase("U")) {
			return UP;
		} else if(str.equalsIgnoreCase("D")) {
			return DOWN;
		} else if(str.equalsIgnoreCase("L")) {
			return LEFT;
		} else if(str.equalsIgnoreCase("R")) {
			return RIGHT;
		} else if(str.equalsIgnoreCase("S_B")) {
			return START_B;
		} else {
			throw new RuntimeException("Unknown edge: " + str);
		}
	}
}
