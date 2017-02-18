package nec.soft.java.client;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.imageio.*;
import javax.swing.*;

import nec.soft.java.utils.ImagesFactory;

//动画类
class Animation {
	private final int WAIT_NUM = 4;
	private final int PRE_NUM = 1;
	private final int BOMB_NUM = 13;

	public static int WAIT = 151;
	public static int PREPARE = 331;
	public static int BOMB = 443;

	private int style;
	// 等待
	public Image[] waitImage = new Image[WAIT_NUM];
	// 准备
	private Image[] preImage = new Image[WAIT_NUM];
	// 爆竹效果图片
	private Image[] bombImage = new Image[BOMB_NUM];
	// 图片的索引
	private int waitIndex = 0;
	private int preIndex = 0;
	private int bombIndex = 0;
	// private int currentNum = 0;
	private javax.swing.Timer time;
	private JPanel table = null;

	public Animation(JPanel table) {
		this.table = table;
	}

	{
		try {
			for (int i = 0; i < 12; i++) {
				bombImage[i] = ImagesFactory.getImage(i + 21);
			}

			for (int i = 0; i < WAIT_NUM; i++) {
				waitImage[i] = ImageIO.read(new File("image/donghua/wait" + (i + 1) + ".png"));
			}

			for (int i = 0; i < PRE_NUM; i++) {
				preImage[i] = ImageIO.read(new File("image/donghua/pre" + (i + 1) + ".png"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		ActionListener taskPerformer = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (style == WAIT + PREPARE) {
					waitIndex++;
					preIndex++;
					if (waitIndex == WAIT_NUM) {
						waitIndex = 0;
					}
					if (preIndex == WAIT_NUM) {
						preIndex = 0;
					}
				}

				if (style == BOMB) {
					bombIndex++;
					if (bombIndex == BOMB_NUM) {
						bombIndex = 0;
						LinkGame.isLinked = false;
						time.stop();
						table.repaint();
						return;
					}
				}
				table.repaint();
			}
		};
		time = new javax.swing.Timer(50, taskPerformer);
	}

	public void start(int style) {
		this.style = style;
		if (time.isRunning()) {
			time.stop();
		}
		if (style == BOMB_NUM || style == BOMB) {
			time.setDelay(50);
		} else {
			
			time.setDelay(150);
		}
		time.start();
	}

	public void stop() {
		time.stop();
	}

	public Image getBomb() {
		return bombImage[bombIndex];
	}

	public Image getPreImage() {
		return preImage[preIndex];
	}

	public Image getWaitImage() {
		return waitImage[waitIndex];
	}

}