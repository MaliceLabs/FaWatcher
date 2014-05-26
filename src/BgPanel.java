

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.LayoutManager;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class BgPanel extends JPanel {
	private static final long serialVersionUID = 7785362247392120967L;
	
	BufferedImage background;
	public BgPanel(BufferedImage bg) {
		super();
		background = bg;
	}
	
	public BgPanel(LayoutManager l, BufferedImage bg) {
		super(l);
		background = bg;
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (background != null) {
			Dimension d = this.getSize();
			for (int x = 0; x < d.width; x += background.getWidth()) {
				for (int y = 0; y < d.height; y += background.getHeight()) {
					g.drawImage(background, x, y, this);
				}
			}		
		}
	}
}
