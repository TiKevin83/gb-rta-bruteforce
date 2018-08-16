package stringflow.rta.libgambatte.display;

import java.util.Arrays;

public class Bitmap {

	private int width;
	private int height;
	private byte components[];
	
	public Bitmap(int width, int height) {
		this.width = width;
		this.height = height;
		this.components = new byte[width * height * 4];
	}
	
	public void clear(byte shade) {
		Arrays.fill(components, shade);
	}
	
	public void setComponent(int index, byte value) {
		components[index] = value;
	}
	
	public void copyToByteArray(byte dest[]) {
		for(int i = 0; i < width * height; i++) {
			dest[i * 3 + 0] = components[i * 4 + 2];
			dest[i * 3 + 1] = components[i * 4 + 1];
			dest[i * 3 + 2] = components[i * 4 + 0];
		}
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
}