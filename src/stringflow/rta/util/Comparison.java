package stringflow.rta.util;

public enum Comparison {
	
	EQUAL, GREATER, LESS, GREATER_OR_EQUAL, LESS_OR_EQUAL, EQUAL_IGNORE_CASE, NOT_EQUAL;
	
	public boolean compare(int a, int b) {
		switch(this) {
			case EQUAL:
				return a == b;
			case GREATER:
				return a > b;
			case LESS:
				return a < b;
			case GREATER_OR_EQUAL:
				return a >= b;
			case LESS_OR_EQUAL:
				return a <= b;
			case NOT_EQUAL:
				return a != b;
			default:
				return false;
		}
	}
	
	public boolean compare(String a, String b) {
		switch(this) {
			case EQUAL:
				return a.equals(b);
			case EQUAL_IGNORE_CASE:
				return a.equalsIgnoreCase(b);
			default:
				return false;
		}
	}
	
	public boolean compare(boolean a, boolean b) {
		switch(this) {
			case EQUAL:
				return a == b;
			case NOT_EQUAL:
				return a != b;
			default:
				return false;
		}
	}
}