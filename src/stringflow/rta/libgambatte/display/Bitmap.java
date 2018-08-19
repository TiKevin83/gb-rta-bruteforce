package stringflow.rta.libgambatte.display;

import stringflow.rta.util.IO;

import java.awt.image.BufferedImage;
import java.util.Arrays;

public class Bitmap {
	
	private final int width;
	private final int height;
	protected final byte components[];
	
	public Bitmap(int width, int height) {
		this.width = width;
		this.height = height;
		this.components = new byte[width * height * 4];
	}
	
	public Bitmap(String fileName) {
		BufferedImage image = IO.readImage(fileName);
		width = image.getWidth();
		height = image.getHeight();
		int[] pixels = new int[width * height];
		image.getRGB(0, 0, width, height, pixels, 0, width);
		components = new byte[width * height * 4];
		for(int i = 0; i < width * height; i++) {
			components[i * 4] = (byte)((pixels[i] >> 24) & 0xFF);
			components[i * 4 + 1] = (byte)((pixels[i] >> 0) & 0xFF);
			components[i * 4 + 2] = (byte)((pixels[i] >> 8) & 0xFF);
			components[i * 4 + 3] = (byte)((pixels[i] >> 16) & 0xFF);
		}
	}
	
	public void clear(byte shade) {
		Arrays.fill(components, shade);
	}
	
	public void drawPixel(int x, int y, byte a, byte b, byte g, byte r) {
		int index = (x + y * width) * 4;
		components[index] = a;
		components[index + 1] = b;
		components[index + 2] = g;
		components[index + 3] = r;
	}
	
	public void copyToByteArray(byte dest[]) {
		for(int i = 0; i < width * height; i++) {
			dest[i * 3] = components[i * 4 + 1];
			dest[i * 3 + 1] = components[i * 4 + 2];
			dest[i * 3 + 2] = components[i * 4 + 3];
		}
	}
	
	public void copyNearest(Bitmap dest, int destX, int destY, float srcXFloat, float srcYFloat) {
		int srcX = (int)(srcXFloat * (getWidth()));
		int srcY = (int)(srcYFloat * (getHeight()));
		int destIndex = (destX + destY * dest.getWidth()) * 4;
		int srcIndex = (srcX + srcY * getWidth()) * 4;
		dest.setComponent(destIndex, components[srcIndex]);
		dest.setComponent(destIndex + 1, components[srcIndex + 1]);
		dest.setComponent(destIndex + 2, components[srcIndex + 2]);
		dest.setComponent(destIndex + 3, components[srcIndex + 3]);
	}
	
	public byte getNearestComponent(float srcXFloat, float srcYFloat, int component) {
		int srcX = (int)(srcXFloat * (getWidth()));
		int srcY = (int)(srcYFloat * (getHeight()));
		int srcIndex = (srcX + srcY * getWidth()) * 4;
		return components[srcIndex + component];
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public float getAspectRatio() {
		return (float)width / (float)height;
	}
	
	public byte getComponent(int location) {
		return components[location];
	}
	
	public void setComponent(int location, byte value) {
		components[location] = value;
	}
}