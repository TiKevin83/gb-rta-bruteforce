package stringflow.rta.libgambatte.display;

import stringflow.rta.util.MathHelper;

public class RenderContext extends Bitmap {
	
	public static final int TRANSPARENCY_NONE = 0;
	public static final int TRANSPARENCY_BASIC = 1;
	public static final int TRANSPARENCY_FULL = 2;
	
	private Bitmap font;
	private Bitmap m_fontColor;
	
	public RenderContext(int width, int height) {
		super(width, height);
		font = new Bitmap("assets/monospace.png");
		m_fontColor = new Bitmap(1, 1);
	}
	
	public void drawBuffer(int data[]) {
		for(int i = 0; i < data.length; i++) {
			int pixel = data[i];
			byte r = (byte)((pixel >> 0) & 0xFF);
			byte g = (byte)((pixel >> 8) & 0xFF);
			byte b = (byte)((pixel >> 16) & 0xFF);
			setComponent(i * 4 + 1, r);
			setComponent(i * 4 + 2, g);
			setComponent(i * 4 + 3, b);
		}
	}
	
	public void drawString(String text, float x, float y, float size, byte b, byte g, byte r) {
		float spacingFactor = font.getAspectRatio();
		m_fontColor.drawPixel(0, 0, (byte)0x00, b, g, r);
		float currentPosX = x;
		float currentPosY = y;
		float sizeX = size;
		float sizeY = size;
		for(int i = 0; i < text.length(); i++) {
			char current = text.charAt(i);
			int imgX = current & 0x0F;
			int imgY = (current >> 4) & 0x0F;
			float imgXStart = (float)imgX / 16.0f;
			float imgYStart = (float)imgY / 16.0f + 0.01f;
			float xStart = currentPosX;
			float yStart = currentPosY;
			float xEnd = currentPosX + sizeX;
			float yEnd = currentPosY + sizeY;
			drawImageDispatcher(font, m_fontColor, xStart, yStart, xEnd, yEnd, imgXStart, imgYStart, (spacingFactor) / 16.0f, 1.0f / 16.0f, TRANSPARENCY_BASIC);
			currentPosX += sizeX * spacingFactor;
		}
	}
	
	private void drawImageDispatcher(Bitmap bitmap, Bitmap source, float startX, float startY, float endX, float endY, float imageStartX, float imageStartY, float scaleStepX, float scaleStepY, int transparencyType) {
		float aspect = getAspectRatio();
		float halfWidth = getWidth() / 2.0f;
		float halfHeight = getHeight() / 2.0f;
		
		startX /= aspect;
		endX /= aspect;
		
		float imageYStep = scaleStepY / (((endY * halfHeight) + halfHeight) - ((startY * halfHeight) + halfHeight));
		float imageXStep = scaleStepX / (((endX * halfWidth) + halfWidth) - ((startX * halfWidth) + halfWidth));
		
		if(startX < -1.0f) {
			imageStartX = -((startX + 1.0f) / (endX - startX));
			startX = -1.0f;
		}
		if(startX > 1.0f) {
			imageStartX = -((startX + 1.0f) / (endX - startX));
			startX = 1.0f;
		}
		if(startY < -1.0f) {
			imageStartY = -((startY + 1.0f) / (endY - startY));
			startY = -1.0f;
		}
		if(startY > 1.0f) {
			imageStartY = -((startY + 1.0f) / (endY - startY));
			startY = 1.0f;
		}
		
		endX = MathHelper.clamp(endX, -1.0f, 1.0f);
		endY = MathHelper.clamp(endY, -1.0f, 1.0f);
		
		startX = (startX * halfWidth) + halfWidth;
		startY = (startY * halfHeight) + halfHeight;
		endX = (endX * halfWidth) + halfWidth;
		endY = (endY * halfHeight) + halfHeight;
		
		switch(transparencyType) {
			case TRANSPARENCY_NONE:
				drawImageInternal(bitmap, (int)startX, (int)startY, (int)endX, (int)endY, imageStartX, imageStartY, imageXStep, imageYStep);
				break;
			case TRANSPARENCY_BASIC:
				drawImageBasicTransparencyInternal(bitmap, source, (int)startX, (int)startY, (int)endX, (int)endY, imageStartX, imageStartY, imageXStep, imageYStep);
				break;
			case TRANSPARENCY_FULL:
				drawImageAlphaBlendedInternal(bitmap, (int)startX, (int)startY, (int)endX, (int)endY, imageStartX, imageStartY, imageXStep, imageYStep);
				break;
			default:
				System.err.println("You used an invalid transparency value >:(");
				System.exit(1);
		}
		
	}
	
	public void drawImage(Bitmap bitmap, float startX, float startY, float endX, float endY, int transparencyType) {
		drawImageDispatcher(bitmap, bitmap, startX, startY, endX, endY, 0.0f, 0.0f, 1.0f, 1.0f, transparencyType);
	}
	
	private void drawImageAlphaBlendedInternal(Bitmap bitmap, int startX, int startY, int endX, int endY, float texStartX, float texStartY, float srcStepX, float srcStepY) {
		int destIndex = (startX + startY * getWidth()) * 4;
		int destStepY = (getWidth() - (endX - startX)) * 4;
		
		float srcY = texStartY;
		float srcIndexFloatStep = (srcStepX * (float)(bitmap.getWidth() - 1));
		for(int j = startY; j < endY; j++) {
			// float srcX = texStartX;
			float srcIndexFloat = ((texStartX * (bitmap.getWidth() - 1)) + (int)(srcY * (bitmap.getHeight() - 1)) * bitmap.getWidth());
			
			for(int i = startX; i < endX; i++) {
				int srcIndex = (int)(srcIndexFloat) * 4;
				
				// The destIndex logic is equivalent to this
				// int destIndex = (i+j*GetWidth())*4;
				
				// //The srcIndex logic is equivalent to this
				// int srcIndex = ((int)(srcX * (bitmap.GetWidth()-1))
				// +(int)(srcY * (bitmap.GetHeight()-1))*bitmap.GetWidth())*4;
				
				int a = bitmap.getComponent(srcIndex + 0) & 0xFF;
				int b = bitmap.getComponent(srcIndex + 1) & 0xFF;
				int g = bitmap.getComponent(srcIndex + 2) & 0xFF;
				int r = bitmap.getComponent(srcIndex + 3) & 0xFF;
				
				int thisB = getComponent(destIndex + 1) & 0xFF;
				int thisG = getComponent(destIndex + 2) & 0xFF;
				int thisR = getComponent(destIndex + 3) & 0xFF;
				
				// This is performed using 0.8 fixed point mulitplication
				// rather than floating point.
				int otherAmt = a;
				int thisAmt = 255 - a;
				byte newB = (byte)((thisB * thisAmt + b * otherAmt) >> 8);
				byte newG = (byte)((thisG * thisAmt + g * otherAmt) >> 8);
				byte newR = (byte)((thisR * thisAmt + r * otherAmt) >> 8);
				
				setComponent(destIndex + 1, newB);
				setComponent(destIndex + 2, newG);
				setComponent(destIndex + 3, newR);
				
				destIndex += 4;
				srcIndexFloat += srcIndexFloatStep;
				// srcX += srcXStep;
			}
			srcY += srcStepY;
			destIndex += destStepY;
		}
	}
	
	private void drawImageBasicTransparencyInternal(Bitmap bitmap, Bitmap source, int startX, int startY, int endX, int endY, float texStartX, float texStartY, float srcStepX, float srcStepY) {
		// Note: The two bitmaps/srcIndices are a trick to reuse this function
		// for drawing fonts. Under normal usage, the same bitmap should be
		// given to both. However, when drawing fonts, the font bitmap should be
		// supplied as "bitmap," and the font color bitmap should be supplied as
		// "source."
		int destIndex = (startX + startY * getWidth()) * 4;
		int destYInc = (getWidth() - (endX - startX)) * 4;
		
		float srcY = texStartY;
		float srcIndexFloatStep1 = (srcStepX * (float)(source.getWidth() - 1));
		float srcIndexFloatStep2 = (srcStepX * (float)(bitmap.getWidth() - 1));
		for(int j = startY; j < endY; j++) {
			// float srcX = texStartX;
			float srcIndexFloat1 = ((texStartX * (source.getWidth() - 1)) + (int)(srcY * (source.getHeight() - 1)) * source.getWidth());
			float srcIndexFloat2 = ((texStartX * (bitmap.getWidth() - 1)) + (int)(srcY * (bitmap.getHeight() - 1)) * bitmap.getWidth());
			
			for(int i = startX; i < endX; i++) {
				int srcIndex1 = (int)(srcIndexFloat1) * 4;
				int srcIndex2 = (int)(srcIndexFloat2) * 4;
				
				// The destIndex logic is equivalent to this
				// int destIndex = (i+j*GetWidth())*4;
				
				// //The srcIndex logic is equivalent to this
				// int srcIndex2 = ((int)(srcX * (bitmap.GetWidth()-1))
				// +(int)(srcY * (bitmap.GetHeight()-1))*bitmap.GetWidth())*4;
				
				byte a = bitmap.getComponent(srcIndex2 + 0);
				
				if(a < (byte)0) {
					setComponent(destIndex + 1, source.getComponent(srcIndex1 + 1));
					setComponent(destIndex + 2, source.getComponent(srcIndex1 + 2));
					setComponent(destIndex + 3, source.getComponent(srcIndex1 + 3));
				}
				
				destIndex += 4;
				srcIndexFloat1 += srcIndexFloatStep1;
				srcIndexFloat2 += srcIndexFloatStep2;
				// srcX += srcXStep;
			}
			srcY += srcStepY;
			destIndex += destYInc;
		}
	}
	
	private void drawImageInternal(Bitmap bitmap, int startX, int startY, int endX, int endY, float texStartX, float texStartY, float srcStepX, float srcStepY) {
		float srcY = texStartY;
		for(int j = startY; j < endY; j++) {
			float srcX = texStartX;
			for(int i = startX; i < endX; i++) {
				bitmap.copyNearest(this, i, j, srcX, srcY);
				srcX += srcStepX;
			}
			srcY += srcStepY;
		}
	}
}
