package stringflow.rta.util;

public class GSRUtils {
	
	public static final byte VERSION = 0x02;
	private static final int HEADER_SIZE = 3;
	private static final int SIZE_SIZE = 3;
	private static final String INVALID_FORMAT = "Invalid GSR save state format";
	
	public static void decodeSAV(byte fileData[], byte data[]) {
		if((fileData[0] & 0xFF) != 0xFF || (fileData[1] & 0xFF) != VERSION) {
			throw new RuntimeException(INVALID_FORMAT);
		}
		System.arraycopy(fileData, getSAVOffset(fileData), data, 0, 0x8000);
	}
	
	public static void encodeSAV(byte data[], byte fileData[]) {
		System.arraycopy(data, 0, fileData, getSAVOffset(fileData), 0x8000);
	}
	
	public static void writeRTC(byte data[], int offset, int value) {
		data[offset + 0] = (byte)((value >> 24) & 0xFF);
		data[offset + 1] = (byte)((value >> 16) & 0xFF);
		data[offset + 2] = (byte)((value >> 8) & 0xFF);
		data[offset + 3] = (byte)((value) & 0xFF);
	}
	
	private static int getSAVOffset(byte fileData[]) {
		return getOffset(fileData, "sram");
	}
	
	private static String readString(byte fileData[], int offset) {
		String str = "";
		while(fileData[offset] != 0x00) {
			str += (char)fileData[offset++];
		}
		return str;
	}
	
	private static int readSize(byte fileData[], int offset) {
		return (fileData[offset + 0] << 16) | (fileData[offset + 1] << 8) | fileData[offset + 2];
	}
	
	private static int getOffset(byte fileData[], String searchKey) {
		int offset = HEADER_SIZE;
		int size = readSize(fileData, offset);
		offset += SIZE_SIZE + size;
		while(true) {
			String key = readString(fileData, offset);
			offset += key.length() + 1;
			size = readSize(fileData, offset);
			offset += SIZE_SIZE;
			if(key.equals(searchKey)) {
				return offset;
			}
			offset += size;
		}
	}
}