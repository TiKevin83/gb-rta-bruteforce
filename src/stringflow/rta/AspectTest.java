package stringflow.rta;

import stringflow.rta.libgambatte.Gb;
import stringflow.rta.libgambatte.display.Bitmap;
import stringflow.rta.libgambatte.display.IDisplayUpdateCallback;
import stringflow.rta.libgambatte.display.RenderContext;

public class AspectTest implements IDisplayUpdateCallback {
	
	Bitmap test = new Bitmap("assets/test.png");
	
	public void onUpdate(Gb gb, RenderContext target, int width, int height) {
		target.drawImage(test, 0, 0, 0.5f, 0.5f, RenderContext.TRANSPARENCY_NONE);
	}
}
