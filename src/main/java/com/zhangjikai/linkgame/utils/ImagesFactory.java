package com.zhangjikai.linkgame.utils;

import java.awt.Image;

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


			ImageIcon icon = null;
			icon = new ImageIcon(classLoader.getResource("images/" + index + ".png"));
			images[index] = icon.getImage();

		}
		return images[index];
	}
}
