package stringflow.rta;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;

public class Util {
	
	public static byte[] readBytesFromFile(String fileName) throws IOException {
		File fh = new File(fileName);
		if(!fh.exists() || !fh.isFile() || !fh.canRead()) {
			throw new FileNotFoundException(fileName);
		}
		long fileSize = fh.length();
		if(fileSize > Integer.MAX_VALUE) {
			throw new IOException(fileName + " is too long to read in as a byte-array.");
		}
		FileInputStream fis = new FileInputStream(fileName);
		byte[] result = new byte[fis.available()];
		fis.read(result);
		fis.close();
		return result;
	}
	
	public static void writeBytesToFile(String fileName, byte[] data) throws IOException {
		FileOutputStream fos = new FileOutputStream(fileName);
		fos.write(data);
		fos.close();
	}
	
	public static void writeBytesToFile(String fileName, ByteBuffer buffer) throws IOException {
		FileChannel channel = new FileOutputStream(fileName).getChannel();
		channel.write(buffer);
		channel.close();
	}
	
	public static ByteBuffer loadByteBufferFromFile(String filename) throws IOException {
		byte[] byteArray = readBytesFromFile(filename);
		ByteBuffer res = ByteBuffer.allocateDirect(byteArray.length).order(ByteOrder.nativeOrder());
		for(int i = 0; i < byteArray.length; i++) {
			res.put(byteArray[i]);
		}
		return res;
	}
	
	public static String readTextFile(String fileName) throws IOException {
		File file = new File(fileName);
		if(!file.exists()) {
			throw new IOException(fileName + " does not exist.");
		}
		StringBuilder builder = new StringBuilder();
		String currentLine;
		BufferedReader reader = new BufferedReader(new FileReader(file));
		while((currentLine = reader.readLine()) != null) {
			builder.append(currentLine).append("\n");
		}
		reader.close();
		return builder.toString();
	}
	
	public static String getSpriteAddressIndexString(int addressIndex) {
		String result = addressIndex == 0 ? "Player" : String.valueOf(addressIndex);
		return result.length() == 1 ? "0" + result : result;
	}
	
	public static int writeByte(byte[] dest, int pointer, byte value) {
		dest[pointer++] = value;
		return pointer;
	}
	
	public static int writeByteArray(byte dest[], int pointer, byte src[]) {
		for(int i = 0; i < src.length; i++) {
			dest[pointer++] = src[i];
		}
		return pointer;
	}
	
	public static int writeShort(byte[] dest, int pointer, short value) {
		dest[pointer++] = (byte)((value >> 8) & 0xff);
		dest[pointer++] = (byte)((value >> 0) & 0xff);
		return pointer;
	}
	
	public static int writeInt(byte[] dest, int pointer, int value) {
		dest[pointer++] = (byte)((value >> 24) & 0xff);
		dest[pointer++] = (byte)((value >> 16) & 0xff);
		dest[pointer++] = (byte)((value >> 8) & 0xff);
		dest[pointer++] = (byte)((value >> 0) & 0xff);
		return pointer;
	}
	
	public static int writeIntArray(byte[] dest, int pointer, int[] src) {
		for(int i = 0; i < src.length; i++) {
			pointer = writeInt(dest, pointer, src[i]);
		}
		return pointer;
	}
	
	public static int writeBoolean(byte[] dest, int pointer, boolean value) {
		dest[pointer++] = (byte)(value ? 1 : 0);
		return pointer;
	}
}