package stringflow.rta.util;

public class StringUtils {
	
	public static String getSpriteAddressIndexString(int addressIndex) {
		String result = addressIndex == 0 ? "Player" : String.valueOf(addressIndex);
		return result.length() == 1 ? "0" + result : result;
	}
}
