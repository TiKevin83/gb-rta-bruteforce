package stringflow.rta;

import stringflow.rta.libgambatte.Gb;
import stringflow.rta.libgambatte.display.Bitmap;
import stringflow.rta.libgambatte.display.IDisplayUpdateCallback;
import stringflow.rta.libgambatte.display.RenderContext;

public class PathDisplay implements IDisplayUpdateCallback {
	
	private int pathNumber;
	
	public PathDisplay(int pathNumber) {
		this.pathNumber = pathNumber;
	}
	
	public void onUpdate(Gb gb, RenderContext target, int width, int height) {
		target.drawString("Frame " + pathNumber, -1.1f, -0.95f, 43.0f / 256.0f, 54.0f / 256.0f, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF);
	}
	
	public void setPathNumber(int pathNumber) {
		this.pathNumber = pathNumber;
	}
}
