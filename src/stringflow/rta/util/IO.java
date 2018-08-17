package stringflow.rta.util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.ByteBuffer;

public class IO {
	
	public static TextFile readText(String path) {
		return new TextFile(path, "r");
	}
	
	public static byte[] readBin(String path) {
		try {
			File fh = new File(path);
			if(!fh.exists() || !fh.isFile() || !fh.canRead()) {
				throw new FileNotFoundException(path);
			}
			long fileSize = fh.length();
			if(fileSize > Integer.MAX_VALUE) {
				throw new IOException(path + " is too long to read in as a byte-array.");
			}
			FileInputStream fis = new FileInputStream(path);
			byte[] result = new byte[fis.available()];
			fis.read(result);
			fis.close();
			return result;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void writeBin(String path, ByteBuffer buffer) {
		writeBin(path, buffer.array());
	}
	
	public static void writeBin(String path, byte data[]) {
		try {
			FileOutputStream fos = new FileOutputStream(path);
			fos.write(data);
			fos.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static BufferedImage readImage(String path) {
		try {
			return ImageIO.read(new File(path));
		} catch(IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void writeImage(String path, BufferedImage image) {
		try {
			String extension = "";
			int i = path.lastIndexOf('.');
			if(i > 0) {
				extension = path.substring(i + 1);
			}
			ImageIO.write(image, extension, new File(path));
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
