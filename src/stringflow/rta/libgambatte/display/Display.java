package stringflow.rta.libgambatte.display;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

public class Display extends Canvas {

	private JFrame frame;
	private RenderContext renderContext;
	private BufferedImage displayImage;
	private byte[] displayComponents;
	private BufferStrategy bufferStrategy;
	private Graphics graphics;
	
	public Display(int width, int height, int scale, String title) {
		Dimension size = new Dimension(width * scale, height * scale);
		setMinimumSize(size);
		setPreferredSize(size);
		setMinimumSize(size);
		
		renderContext = new RenderContext(width, height);
		displayImage = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
		displayComponents = ((DataBufferByte) displayImage.getRaster().getDataBuffer()).getData();
		
		frame = new JFrame();
		frame.add(this);
		frame.pack();
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setTitle(title);
		frame.setVisible(true);
		
		createBufferStrategy(1);
		bufferStrategy = getBufferStrategy();
		graphics = bufferStrategy.getDrawGraphics();
	}
	
	public void close() {
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
	}
	
	public RenderContext getRenderContext() {
		return renderContext;
	}
	
	public void swapBuffers() {
		renderContext.copyToByteArray(displayComponents);
		graphics.drawImage(displayImage, 0, 0, getWidth(), getHeight(), null);
		bufferStrategy.show();
	}
}