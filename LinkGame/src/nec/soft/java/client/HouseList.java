package nec.soft.java.client;

import javax.swing.*;
import java.awt.*;
import javax.imageio.*;

import nec.soft.java.protocal.*;

import java.io.*;
import java.awt.image.*;
import java.awt.event.*;
import java.util.*;

import static nec.soft.java.protocal.MyProtocol.*;

//未进入游戏房间列表
class HouseList extends JPanel {
	// 组件的大小
	//public final int PANEL_WIDTH = 591;
	//public final int PANEL_HEIGHT = 360;
	// 房间列表的分布
	public static final int HOUSE_ROW = 2;
	public static final int HOUSE_COL = 3;
	// 每个列表房间数
	private int houseNum = HOUSE_ROW * HOUSE_COL;
	// 总房间数,默认18个,可增加
	public int allHouserNum = 18;
	// 每个房间可以容纳人数
	private static final int perNum = 4;
	private BufferedImage deskImage;
	private BufferedImage highDeskImage;
	//private BufferedImage backImage;
	private BufferedImage gameImage;
	// private BufferedImage selectJoin;
	
	// 表示当前页
	private int crrentPage = 0;
	// 选择的房间
	int selectHouse = -1;
	int selectRow = -1;
	int selectCol = -1;
	
	private boolean dClicked = false;

	// 保存每个房间是否有人的信息
	private java.util.List<Integer> someoneInfo = new java.util.ArrayList<Integer>();

	private java.util.List<Integer> gameInfo = new java.util.ArrayList<Integer>();

	// 当前房间列表向下翻一页
	public void nextPage() {
		if (crrentPage < allHouserNum / houseNum - 1) {
			crrentPage++;
			System.out.println("crrentPage:" + crrentPage);
			repaint();
		}
	}

	// 当前房间列表向前翻一页
	public void prePage() {
		if (crrentPage > 0) {
			crrentPage--;
			repaint();
		}
	}

	// 初始化块
	public HouseList() {
		setOpaque(false);
		try {
			deskImage = ImageIO.read(new File("image/GameHall/back2.png"));
			highDeskImage = ImageIO.read(new File("image/GameHall/back3.png"));
			gameImage = ImageIO.read(new File("image/GameHall/game_ing.jpg"));
		} catch (IOException ex) {
			// ex.printStrack
		}

		reSetHouseInfo();
	}

	public void initGameInfo(java.util.List<Integer> gameInfo) {
		this.gameInfo = gameInfo;
	}

	public void setGameInfo(int HouseNum) {
		gameInfo.add(HouseNum);
	}

	public void removeGameInfo(Integer HouseNum) {
		gameInfo.remove(HouseNum);
	}

	public void reSetHouseInfo() {
		someoneInfo.clear();
		// 初始化房间是否有人,如果有,则将房号保存起来
		for (UserInfo user : Client.onlineUser) {
			someoneInfo.add(user.getHouseNum());
		}

	}

	// 处理鼠标移动的方法
	public void mousedMove(MouseEvent e) {
		
		// 将坐标转换为相对左边
		selectCol = e.getX() / (this.getWidth()/3);
		selectRow = e.getY() / (this.getHeight()/2);
		selectHouse = selectRow * HOUSE_COL + selectCol;
		//System.out.println("gaegegredzsgvrdhbrthg");
		//System.out.println(selectRow + " " +selectCol);
		//System.out.println(selectHouse);
		if (selectHouse >= 0 && selectHouse <= houseNum) {
			repaint();
		}
	}
	

	public void doubleClick() {
		// 实际房号
		int realHouserNum = crrentPage * houseNum + selectHouse;
		System.out.println(realHouserNum);
		System.out.println("             gaegegredzsgvrdhbrthg");
		// 向服务器发送加入请求
		Client.ps.println(JOIN_HOUSE + realHouserNum + JOIN_HOUSE);

		System.out.println(realHouserNum);
		System.out.println("             gaegegredzsgvrdhbrthg");
		dClicked = true;
		repaint();
	}

	public void paint(Graphics g) {
		int houseName = crrentPage * 6;
		//g.drawImage(backImage, 0, 0, this.getWidth(), this.getHeight(), null);
		// 绘制房间
		for (int i = 0; i < HOUSE_ROW; i++) {
			for (int j = 0; j < HOUSE_COL; j++) {
				if (i == selectRow && j == selectCol) {
					g.drawImage(highDeskImage, j * (this.getWidth()/3), i
							* (this.getHeight()/2), this.getWidth() / 3,
							this.getHeight() / 2, null);
				} else {
					g.drawImage(deskImage, j * (this.getWidth()/3), i * (this.getHeight()/2),
							this.getWidth() / 3, this.getHeight() / 2, null);
				}
				// 绘制房间名
				g.setFont(new Font("宋体", Font.BOLD, 15));
				g.setColor(new Color(255, 0, 0));
				g.drawString("房间" + houseName, j * (this.getWidth()/3) + 10, i
						* (this.getHeight()/2) + 20);

				String houseInfo = "";
				// 房间没有人
				if (someoneInfo.indexOf(houseName) == -1) {
					houseInfo = "空房";
				} else {
					houseInfo = "有人";
				}
				g.setFont(new Font("宋体", Font.PLAIN, 12));
				g.drawString("状态:" + houseInfo, j * (this.getWidth()/3) + 100, i
						* (this.getHeight()/2) + 20);
				houseName++;
			}
		}

		houseName = crrentPage * 6;

		// 绘制头像
		drawsHeadIcon(g);
	}

	


	// 绘制头像
	public void drawsHeadIcon(Graphics g) {
		int relativeNum = -1;
		int seat = -1;
		int index = 0;
		// 偏移量
		int offset_x = 0;
		int col = -1;
		int row = -1;
		int x = 0;
		int y = 0;
		
		int width=this.getWidth()/3;
		int height=this.getHeight()/2;
		//System.out.println(width+"  "+height);

		for (UserInfo user : Client.onlineUser) {
			// 如果房号座号不为空且房号在当前面页面
			if (user.getHouseNum() != -1 && user.getSeat() != -1
					&& user.getHouseNum() / houseNum == crrentPage) {
				// 相对房号 = 实际房号%房间数
				relativeNum = user.getHouseNum() % houseNum;
				offset_x = user.getSeat() * (width/6+7);
				row = relativeNum / HOUSE_COL;
				col = relativeNum - row * HOUSE_COL;

				index = user.getIconIndex();
				//x = col * HOUSE_WIDTH + head_x + offset_x;
				//y = row * HOUSE_HEIGHT + head_y;

				x = col * width + (width/6-10)+offset_x;
				y = row * height + height/3+height/6;
				
				g.drawImage(HeadIcon.headIcon[index].getImage(), x, y, width/6, height/5,
						null);
			}
		}
	}
}
