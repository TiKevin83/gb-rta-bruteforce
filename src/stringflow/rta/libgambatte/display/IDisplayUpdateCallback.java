package stringflow.rta.libgambatte.display;

import stringflow.rta.libgambatte.Gb;

public interface IDisplayUpdateCallback {
	
	void onUpdate(Gb gb, RenderContext target, int width, int height);
}
