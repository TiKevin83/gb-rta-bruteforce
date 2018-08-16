package stringflow.rta;

public enum GenderRatio {
	
	GENDER_UNKNOWN(0x00),
	GENDER_F0(0x00),
	GENDER_F12_5(0x1F),
	GENDER_F25(0x3F),
	GENDER_F50(0x7F),
	GENDER_F75(0xBF),
	GENDER_F100(0xFE);
	
	private int compareVal;
	
	private GenderRatio(int compareVal) {
		this.compareVal = compareVal;
	}
	
	public int getCompareVal() {
		return compareVal;
	}
}
