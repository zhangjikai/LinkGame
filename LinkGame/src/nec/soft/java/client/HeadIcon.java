package nec.soft.java.client;

import java.net.URL;

import javax.swing.ImageIcon;

//Í·Ïñ£¬new Í¼Æ¬
public class HeadIcon {
	static final int NUM = 20;
	public static ImageIcon[] headIcon = new ImageIcon[NUM];

	static {
		for (int i = 0; i < NUM; i++) {
			URL url = HeadIcon.class.getResource("/nec/soft/java/images/head/" + (i + 1) + ".jpg");
			headIcon[i] = new ImageIcon(url);
		}
	}
}
