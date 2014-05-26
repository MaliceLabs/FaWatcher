/*
 *  FA Watcher - Mass-watch FurAffinity users
    Copyright (C) 2014  TheEqualizer

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
