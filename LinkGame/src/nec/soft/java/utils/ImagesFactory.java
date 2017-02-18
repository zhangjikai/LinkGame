package nec.soft.java.utils;

import java.awt.Image;
import java.net.URL;

import javax.swing.ImageIcon;

/** ªÒµ√Õº∆¨ */
public class ImagesFactory {

private static Image[] images = new Image[100];
	
	public static Image getImage(int index) {
		if(index < 0) {
			index = 36+index;
		}
		if (images[index] == null) {
			URL url = ImagesFactory.class.getResource("/nec/soft/java/images/" + index + ".png");
			ImageIcon icon = new ImageIcon(url);
			images[index] = icon.getImage();
		}
		return images[index];
	}
}
