package stringflow.rta.libgambatte.display;

public class RenderContext extends Bitmap {
	
	public RenderContext(int width, int height) {
		super(width, height);
	}
	
	public void drawBuffer(int data[]) {
		for(int i = 0; i < data.length; i++)  {
			int pixel = data[i];
			byte r = (byte)((pixel >> 16) & 0xFF);
			byte g = (byte)((pixel >> 8) & 0xFF);
			byte b = (byte)((pixel >> 0) & 0xFF);
			setComponent(i * 4 + 0, r);
			setComponent(i * 4 + 1, g);
			setComponent(i * 4 + 2, b);
		}
	}
}
