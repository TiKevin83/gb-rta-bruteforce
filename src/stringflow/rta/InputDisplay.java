package stringflow.rta;

import stringflow.rta.libgambatte.Gb;
import stringflow.rta.libgambatte.display.Bitmap;
import stringflow.rta.libgambatte.display.IDisplayUpdateCallback;
import stringflow.rta.libgambatte.display.RenderContext;
import static stringflow.rta.Joypad.*;

public class InputDisplay implements IDisplayUpdateCallback {
	
	private Bitmap buttonFalse = new Bitmap("assets/button_false.png");
	private Bitmap buttonTrue = new Bitmap("assets/button_true.png");
	
	private Bitmap buttonSmallFalse = new Bitmap("assets/button_small_false.png");
	private Bitmap buttonSmallTrue = new Bitmap("assets/button_small_true.png");
	
	private Bitmap dpadFalse = new Bitmap("assets/dpad_false.png");
	private Bitmap dpadUp = new Bitmap("assets/dpad_up.png");
	private Bitmap dpadLeft = new Bitmap("assets/dpad_left.png");
	private Bitmap dpadRight = new Bitmap("assets/dpad_right.png");
	private Bitmap dpadDown = new Bitmap("assets/dpad_down.png");
	
	public void onUpdate(Gb gb, RenderContext target, int width, int height) {
		float baseXStart = 0.05f;
		float baseYStart = 0.6f;
		float baseXEnd = baseXStart + 0.125f;
		float baseYEnd = baseYStart + 0.125f;
		
		target.drawImage((gb.getCurrentJoypad() & B) != 0 ? buttonTrue : buttonFalse, baseXStart, baseYStart + 0.05f, baseXEnd, baseYEnd + 0.05f, RenderContext.TRANSPARENCY_FULL);
		target.drawImage((gb.getCurrentJoypad() & A) != 0 ? buttonTrue : buttonFalse, baseXStart + 0.15f, baseYStart, baseXEnd + 0.15f, baseYEnd, RenderContext.TRANSPARENCY_FULL);
		
		Bitmap dpad = dpadFalse;
		if((gb.getCurrentJoypad() & LEFT) != 0) {
			dpad = dpadLeft;
		} else if((gb.getCurrentJoypad() & RIGHT) != 0) {
			dpad = dpadRight;
		} else if((gb.getCurrentJoypad() & UP) != 0) {
			dpad = dpadUp;
		} else if((gb.getCurrentJoypad() & DOWN) != 0) {
			dpad = dpadDown;
		}
		
		target.drawImage(dpad, baseXStart - 0.35f, baseYStart, baseXEnd - 0.35f + 0.08f, baseYEnd + 0.082f, RenderContext.TRANSPARENCY_FULL);
		target.drawImage((gb.getCurrentJoypad() & START) != 0 ? buttonSmallTrue : buttonSmallFalse, baseXStart - 0.075f, baseYStart + 0.2f, baseXEnd- 0.075f, baseYEnd + 0.2f, RenderContext.TRANSPARENCY_FULL);
		target.drawImage((gb.getCurrentJoypad() & SELECT) != 0 ? buttonSmallTrue : buttonSmallFalse, baseXStart - 0.175f, baseYStart + 0.2f, baseXEnd- 0.175f, baseYEnd + 0.2f, RenderContext.TRANSPARENCY_FULL);
//		target.drawString(gb.getCurrentJoypad() + "", -1f, 0.8f, 50.0f / 256.0f, (byte)0x00, (byte)0x00, (byte)0xFF);
	}
}
