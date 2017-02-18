package com.zhangjikai.linkgame.frame;

import java.awt.BorderLayout;

import javax.swing.JFrame;

import com.zhangjikai.linkgame.ui.MenuPanel;
import com.zhangjikai.linkgame.utils.ShowHelper;

public class MenuFrame extends JFrame{

	private static final long serialVersionUID = -8951931840832150238L;
	private static MenuFrame frame;

	public MenuFrame() {
		init();
	}
	
	private void init() {
		MenuPanel panel = new MenuPanel();
		add(panel, BorderLayout.CENTER);
	}
	
	public static void open() {
		frame = new MenuFrame();
		frame.setTitle("欢迎来到秦时明月--连连看");
		frame.setSize(750, 500);
		ShowHelper.showCenter(frame);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	
	public static void close() {
		if(frame != null)
			frame.dispose();
		frame = null;
	}
}
