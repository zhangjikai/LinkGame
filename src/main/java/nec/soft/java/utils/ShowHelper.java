package nec.soft.java.utils;

import java.awt.*;

public class ShowHelper {
	
	private ShowHelper(){}
	
	/** 使窗口居中显示 */
	public static void showCenter(Window window) {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = window.getSize();
		if (frameSize.height > screenSize.height)
			frameSize.height = screenSize.height;
		if (frameSize.width > screenSize.width)
			frameSize.width = screenSize.width;
		window.setLocation((screenSize.width - frameSize.width) / 2,
				(screenSize.height - frameSize.height) / 2);	
		
	}
}
