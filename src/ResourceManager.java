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

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import javax.imageio.ImageIO;

public class ResourceManager {
	private HashMap<File, BufferedImage> imgMap;
	private static final String[] jarImgs = {
		"close_dark.png",
		"close_default.png",
		"close_light.png",
		"minimize_dark.png",
		"minimize_default.png",
		"minimize_light.png",
		"bg.png",
		"watcher_head.png"
	};
	ResourceManager() {
		imgMap = new HashMap<File, BufferedImage>();
		try {
			for (String s : jarImgs) {
				InputStream is = WatcherApp.class.getResourceAsStream(s);
				imgMap.put(new File(s), ImageIO.read(is));
				is.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * This method is a proxy to the file version of this method
	 * @param srcFile location of the file to load
	 * @param filterType Specifies a specific operation to perform when loading
	 */
	public void addImage(String srcFile, int filterType) {
		addImage(new File(srcFile), filterType);
	}
	
	/**
	 * Attempts to add an image to the store.
	 * Valid filter type values:
	 * 0 - No filter
	 * 1 - Convert black pixels to transparent
	 * @param srcFile
	 * @param filterType
	 */
	public void addImage(File srcFile, int filterType) {	
		try {
			//attempt to delegate file temp renaming bs
			File imgFile = srcFile;
			//File tmpFile = new File(imgFile.toString().replaceAll(".pbm", ".bmp"));
			/*rename it maybe
			if (EditorApp.imageExtension.equals(".pbm"))
				if (!imgFile.renameTo(tmpFile))
					throw new IOException("File rename failure");
			*/
			if (imgMap.containsKey(imgFile))
				return;
			FileInputStream is = new FileInputStream(srcFile);
			switch (filterType) {
			case 0: //just load the image
				imgMap.put(srcFile, ImageIO.read(is));
				break;
			case 1: //black2Trans
				BufferedImage img = ImageIO.read(is);
				if (srcFile.getName().endsWith(".png")) { //$NON-NLS-1$
					imgMap.put(srcFile,	magenta2Trans(img));
				} else {
					imgMap.put(srcFile, black2Trans(img));
				}
			}
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * This method is a proxy to the file version of this method
	 * @param srcFile location of the file to load
	 * @param filterType Specifies a specific operation to perform when loading
	 */
	public void reloadImage(String src, int filterType) {
		reloadImage(new File(src), filterType);
	}
	
	/**
	 * If the file exists in the repository, replace. If not, load it anyway
	 * @param srcFile
	 * @param filterType
	 */
	public void reloadImage(File srcFile, int filterType) {
		if (imgMap.containsKey(srcFile)) {
			imgMap.get(srcFile).flush();
			imgMap.remove(srcFile);
		}
		addImage(srcFile, filterType);
	}
	
	public java.awt.Graphics getImgGraphics(File key) {
		if (imgMap.containsKey(key))
			return imgMap.get(key).getGraphics();
		System.err.println("Key not found for getImgGraphics"); //$NON-NLS-1$
		System.err.println(key);
		return null;			
	}
	
	public BufferedImage getImg(File key) {
		if (imgMap.containsKey(key))
			return imgMap.get(key);
		System.err.println("Key not found for getImgGraphics"); //$NON-NLS-1$
		System.err.println(key);
		return null;	
	}
	public BufferedImage getImg(String key) {
		return getImg(new File(key));
	}
	public int getImgH(File key) {
		if (imgMap.containsKey(key))
			return imgMap.get(key).getHeight();
		System.err.println("Key not found for getImgGraphics"); //$NON-NLS-1$
		System.err.println(key);
		return -1;	
	}
	public int getImgW(File key) {
		if (imgMap.containsKey(key))
			return imgMap.get(key).getWidth();
		System.err.println("Key not found for getImgGraphics"); //$NON-NLS-1$
		System.err.println(key);
		return -1;	
	}
	
	private BufferedImage black2Trans(BufferedImage src)
	{
		BufferedImage dest = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_ARGB_PRE);
		for (int y = 0; y < src.getHeight(); y++)
			for (int x = 0; x < src.getWidth(); x++)
			{
				int px = src.getRGB(x, y);
				if (px == -16777216) //argb black full opaque
					dest.setRGB(x, y, 0);
				else
					dest.setRGB(x, y, px);
			}
		
		return dest;
	}
	
	private BufferedImage magenta2Trans(BufferedImage src) {
		BufferedImage dest = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_ARGB_PRE);
		for (int y = 0; y < src.getHeight(); y++)
			for (int x = 0; x < src.getWidth(); x++)
			{
				int px = src.getRGB(x, y);
				if (px == 0xFFFF00FF) //argb black full opaque
					dest.setRGB(x, y, 0);
				else
					dest.setRGB(x, y, px);
			}
		
		return dest;
	}
	

	public void purge() {
		// TODO Auto-generated method stub
		for (BufferedImage b : imgMap.values()) {
			b.flush();
		}
		imgMap.clear();
		//load default rsrsc
		try {
			for (String s : jarImgs) {
				InputStream is = WatcherApp.class.getResourceAsStream(s);
				imgMap.put(new File(s), ImageIO.read(is));
				is.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
