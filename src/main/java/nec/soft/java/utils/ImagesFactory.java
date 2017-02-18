package nec.soft.java.utils;

import java.awt.Image;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.ImageIcon;


public class ImagesFactory {

private static Image[] images = new Image[100];
	
	public static Image getImage(int index) {
		if(index < 0) {
			index = 36+index;
		}
		if (images[index] == null) {
			//URL url = ImagesFactory.class.getResource("images/" + index + ".png");
			ClassLoader classLoader = FileHelper.class.getClassLoader();
			File file = new File(classLoader.getResource("images/" + index + ".png").getFile());
			ImageIcon icon = null;
			try {
				icon = new ImageIcon(file.toURI().toURL());
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
			images[index] = icon.getImage();
		}
		return images[index];
	}
}
