package nec.soft.java.client;

import static nec.soft.java.protocal.MyProtocol.EXIT_HOUSE;
import static nec.soft.java.protocal.MyProtocol.GAME_PREPARE;
import static nec.soft.java.protocal.MyProtocol.LINK_COOR;
import static nec.soft.java.protocal.MyProtocol.SPLIT_SIGN;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.FeatureDescriptor;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingConstants;

import nec.soft.java.protocal.UserInfo;
import nec.soft.java.share.SharedVar;
import nec.soft.java.utils.Checker;
import nec.soft.java.utils.Constants;
import nec.soft.java.utils.EffectSound;
import nec.soft.java.utils.ImagesFactory;
import nec.soft.java.utils.Mode;
import nec.soft.java.utils.ShowHelper;


// 进入房间后的界面
public class LinkGame {
	// 状态:开始,结束,等待
	private final int START = 356;
	private boolean end = false;
	private final int WAITING = 206;
	// 默认为等待状态
	private int status = WAITING;
	// 定义桌面大小
	private final int TABLE_WIDTH = 800;
	private final int TABLE_HEIGHT = 600;
	// 定义一个10行10列的地图数组
	public static final int ROW = 10;
	public static final int COL = 16;
	public static final int BLOCKS = 160;
	/*
	  实际障碍物数量为8行8列，只是为了方便计算，
	  数组外围的元素值实际没有赋值，即默认值为0.
	*/
	/*private int blocksNum = (ROW - 2) * (COL - 2);*/
	private int[][] blocks = new int[ROW][COL];
	/*// 保存地图块数,初始为36
	private int blockCounts = 160;*/
	// 每块的障碍物的大小
	private final int BLOCK_SIZE = 50;

	// 障碍物图形有9种
	private final int BLOCK_NUM = 20;
	// 保存障碍无图片
	private Image[] blockImage = new Image[BLOCK_NUM + 1];
	private Image backGroup;

	// 对方玩家缩略桌面大小,数量
	private final int RIVAL_WIDTH = 155;
	// 缩略图中的障碍物大小
	private final int RIVAL_SIZE = 10;
	private final int RIVAL_NUM = 3;
	// 保存对方玩家的图片索引
	private java.util.List<int[][]> rivalBlock = new java.util.LinkedList<int[][]>();
	// 缩略图起始坐标
	private int rival_x = 50;
	private int rival_y = (ROW - 1) * BLOCK_SIZE + 40;
	private int info_y = rival_y + ROW * RIVAL_SIZE + 10;
	// 保存对方玩家的信息
	private ChummeryInfo chummeryInfo;

	// 保存第一次选中的障碍物的数组坐标
	private int fristRow;
	private int fristCol;
	private int secondRow;
	private int secondCol;

	// 选中的障碍物的个数
	private int selectCount;

	private JFrame f = new JFrame("连连看");
	private MyTable myTable = new MyTable();
	private Object[] headTile = { "头像", "姓名", "性别", "积分" };
	// 定义一个连线类
	private LinkLine lkLine = new LinkLine(blocks);
	// 是否可以连线
	static boolean isLinked;
	// 用于接收折点
	private int[] points;

	// 聊天面板
	private ChatPanel chatPanel = new ChatPanel(ChatPanel.GAME_MODEL);
	// 用户列表
	private IconTable usrTable;
	private ExtendedTableModel model;

	// 按钮板块
	private JSplitPane verSlitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true);
	// 进度条
	private JProgressBar progressBar = new JProgressBar(SwingConstants.HORIZONTAL, 0, 112);
	// 保存当前进度
	private int crrentPro = 0;
	private String crrentName = null;
	private Animation animation;
	// 保存结束信息
	private String[] endInfo;
	// 结束信息起始位置
	private final int END_X = 100;
	private final int END_Y = 150;

	public LinkGame() {
		backGroup = ImagesFactory.getImage(65);
		// 获取障碍物图片
		for (int i = 1; i < BLOCK_NUM + 1; i++) {
			blockImage[i] = ImagesFactory.getImage(i);
		}
	}

	// 因为要重复使用本类对象,该方法用于重复初始参数
	public void init() {
		crrentName = Client.crrentUser.getName();
		fristRow = -1;
		fristCol = -1;
		secondRow = -1;
		secondCol = -1;
		selectCount = 0;
		isLinked = false;
		crrentPro = 0;
		progressBar.setValue(crrentPro);
		animation = new Animation(myTable);
		endInfo = null;
		chummeryInfo = Client.chummery;
		chatPanel.clearText();
	}

	public void reSetUserTable() {
		java.util.List<UserInfo> houseUser = new java.util.ArrayList<UserInfo>();
		for (int i = 0; i < ChummeryInfo.USER_NUM; i++) {
			if (!"null".equals(chummeryInfo.getName(i)) && chummeryInfo.getName(i) != null) {
				houseUser.add(chummeryInfo.getUserInfo(i));
			}
		}
		System.out.println(houseUser.size());
		model.reSetTable(headTile, houseUser);
		usrTable.init();
		usrTable.updateUI();
	}

	public void addRow(UserInfo user) {
		model.addRow(user);
		usrTable.updateUI();
	}

	public void setScore(int score, String name) {
		// 更新分数
		model.updateScore(score, name);
		usrTable.updateUI();
	}

	public void removeUI(int index) {
		model.removeRow(index);
		usrTable.updateUI();
	}

	public void removeUI(String exitUser) {
		// 用户表移除一行
		model.removeRow(exitUser);
		usrTable.updateUI();
	}

	public void initMap(String[] lines) {
		int index = 1;
		for (int i = 0; i < blocks.length; i++) {
			for (int j = 0; j < blocks[0].length; j++) {
				blocks[i][j] = Integer.parseInt(lines[index++]);
			}
		}

		// 初始化同房间其他玩家的障物
		if (chummeryInfo != null) {
			for (int i = 0; i < ChummeryInfo.USER_NUM; i++) {
				if (crrentName.equals(chummeryInfo.getName(i))) {
					continue;
				}
				if (!"null".equals(chummeryInfo.getName(i)) && chummeryInfo.getName(i) != null) {
					chummeryInfo.initBlock(i, blocks);
				}
			}
		}
		myTable.repaint();
	}

	// 初始画地图
	public void initMap() {
		int[][] block = Checker.newGame(14, 8, 16, Mode.classic);
		for (int i = 0; i < block.length; i++) {
			for (int j = 0; j < block[0].length; j++) {
				blocks[i][j] = block[i][j];
			}
		}
		// 初始化同房间其他玩家的障物
		if (chummeryInfo != null) {
			for (int i = 0; i < ChummeryInfo.USER_NUM; i++) {
				if (crrentName.equals(chummeryInfo.getName(i))) {
					continue;
				}
				if (!"null".equals(chummeryInfo.getName(i)) && chummeryInfo.getName(i) != null) {
					chummeryInfo.initBlock(i, blocks);
				}
			}
		}
		myTable.repaint();
	}

	// 初始化组件
	public void initComponent() {
		model = new ExtendedTableModel();
		usrTable = new IconTable(model, 0, IconTable.GENERAL);

		myTable.setPreferredSize(new Dimension(TABLE_WIDTH, TABLE_HEIGHT));
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(chatPanel.getPanel());
		JButton button = new JButton("开始");
		button.setPreferredSize(new Dimension(30,30));
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				prepare();
			}
		});
		panel.add(button, BorderLayout.SOUTH);

		JPanel topPanel = new JPanel();
		topPanel.setLayout(new BorderLayout());
		topPanel.add(new JScrollPane(usrTable));

		verSlitPane.add(topPanel);
		verSlitPane.add(panel);
		verSlitPane.setDividerSize(1);
		verSlitPane.setDividerLocation(300);

		// 设置该组件的最小大小
		verSlitPane.setMinimumSize(new Dimension(300, TABLE_HEIGHT));
		verSlitPane.setPreferredSize(new Dimension(300, TABLE_HEIGHT));

		progressBar.setStringPainted(true);
		progressBar.setBackground(new Color(100, 255, 236));
		progressBar.setPreferredSize(new Dimension(100, 20));
		f.setLayout(new BorderLayout());
		f.add(verSlitPane, BorderLayout.EAST);
		JPanel panel2 = new JPanel();
		panel2.setLayout(new BorderLayout());
		panel2.add(myTable, BorderLayout.CENTER);
		panel2.add(progressBar, BorderLayout.NORTH);
		panel2.setPreferredSize(new Dimension(TABLE_WIDTH, TABLE_HEIGHT + 20));
		f.add(panel2, BorderLayout.CENTER);
		f.pack();
		f.setResizable(false);
		ShowHelper.showCenter(f);
		f.setVisible(false);

		f.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				// 通知服务器退出房间
				Client.ps.println(EXIT_HOUSE + Client.crrentUser.getHouseNum() + SPLIT_SIGN
						+ Client.crrentUser.getSeat() + EXIT_HOUSE);
				animation.stop();
				f.setVisible(false);
			}
		});

		// 游戏桌面鼠标按下事件
		myTable.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (status == WAITING) {
					return;
				}
				// 将桌面坐标转换为数组坐标
				int xPos = e.getX() / BLOCK_SIZE;
				int yPos = e.getY() / BLOCK_SIZE;
				// 如果超出地图范围,直接退出方法
				if (xPos < 1 || xPos > COL - 2 || yPos < 1 || yPos > ROW - 2) {
					return;
				}
				selectCount++;
				switch (selectCount) {
				case 1: // 第一次选中
					fristRow = yPos;
					fristCol = xPos;
					break;
				case 2: // 第二次选中
					secondRow = yPos;
					secondCol = xPos;
					break;
				default:
					break;
				}

				if (selectCount == 2) {
					// 判断选中的障碍物是否可以连线
					isLinked = lkLine.linkAble(fristCol, fristRow, secondCol, secondRow);
					// 如果连线成功
					if (isLinked) {
						// 清楚障碍物
						blocks[fristRow][fristCol] = 0;
						blocks[secondRow][secondCol] = 0;
						crrentPro += 2;
						progressBar.setValue(crrentPro);
						// 获取折点
						points = lkLine.getPoints();
						// 启动爆竹效果
						animation.start(Animation.BOMB);
						EffectSound.playAudio(EffectSound.BOMB);
						// 向服务器发送坐标
						Client.ps.println(LINK_COOR + fristRow + SPLIT_SIGN + fristCol + SPLIT_SIGN
								+ secondRow + SPLIT_SIGN + secondCol + SPLIT_SIGN
								+ progressBar.getString() + LINK_COOR);
					}
				}
				myTable.repaint();
			}

			// 游戏桌面鼠标松开事件
			public void mouseReleased(MouseEvent e) {
				if (status == WAITING) {
					return;
				}
				// 重置
				if (selectCount == 2) {
					selectCount = 0;
				}
				myTable.repaint();
			}
		});
	}

	public void setWaiting() {
		status = WAITING;
		end = false;
		// 启动等待和准备动画
		animation.start(Animation.WAIT + Animation.PREPARE);
		f.revalidate();
		f.setVisible(true);
	}

	// 准备
	public void prepare() {
		// 如果在游戏中则不能准备
		if (status == START) {
			return;
		}
		int seat = Client.crrentUser.getSeat();
		// 如果已经准备则不能再次准备
		if (chummeryInfo.getState(seat)) {
			return;
		}
		// 通知服务器准备完毕
		Client.ps.println(GAME_PREPARE + Client.crrentUser.getHouseNum() + SPLIT_SIGN
				+ Client.crrentUser.getSeat() + GAME_PREPARE);
	}

	public void start() {
		status = START;
		myTable.repaint();
	}

	public void setEnd(String[] splitMsg) {
		end = true;
		endInfo = splitMsg;
		animation.start(Animation.WAIT + Animation.PREPARE);
		myTable.repaint();
	}

	public ChatPanel getChat() {
		return chatPanel;
	}

	public void repaint() {
		myTable.repaint();
	}

	public void drawSelected(Graphics g) {
		int fristX;
		int fristY;
		int secondX;
		int secondY;

		// 第一次选中或第2次选中
		if (selectCount == 1 || selectCount == 2) {
			g.drawImage(ImagesFactory.getImage(39), fristCol * BLOCK_SIZE, fristRow * BLOCK_SIZE,
					BLOCK_SIZE, BLOCK_SIZE, null);
		}
		// 第2次选中
		if (selectCount == 2) {
			g.drawImage(ImagesFactory.getImage(39), secondCol * BLOCK_SIZE, secondRow * BLOCK_SIZE,
					BLOCK_SIZE, BLOCK_SIZE, null);
			// 如果连接成功
			if (isLinked) {
				// 如果连接点为空直接返回
				if (points == null) {
					return;
				}
				// 绘制连接线
				int index = 0;
				fristX = points[index++];
				fristY = points[index++];

				for (int i = index; i < points.length;) {
					secondX = fristX;
					secondY = fristY;
					fristX = points[i++];
					fristY = points[i++];
					Graphics2D g2d = (Graphics2D) g;
					g2d.setStroke(new BasicStroke(5));
					g2d.setColor(Color.red);
					g.drawLine(fristX * BLOCK_SIZE + 25, fristY * BLOCK_SIZE + 25, secondX
							* BLOCK_SIZE + 25, secondY * BLOCK_SIZE + 25);
				}
			}
		}
	}

	public void drawBomb(Graphics g) {
		// 绘制爆竹效果
		if (isLinked && blocks[fristRow][fristCol] == 0 && blocks[secondRow][secondCol] == 0) {
			g.drawImage(animation.getBomb(), fristCol * BLOCK_SIZE, fristRow * BLOCK_SIZE,
					BLOCK_SIZE, BLOCK_SIZE, null);
			g.drawImage(animation.getBomb(), secondCol * BLOCK_SIZE, secondRow * BLOCK_SIZE,
					BLOCK_SIZE, BLOCK_SIZE, null);
		}
	}

	// 绘制障碍物
	public void drawBlock(Graphics g) {
		if (end) {
			return;
		}
		// 绘制障碍物
		for (int i = 0; i < ROW; i++) {
			for (int j = 0; j < COL; j++) {
				// 是否有障碍物
				if (blocks[i][j] != 0) {
					drawBomb(g);
					int index = blocks[i][j];
					g.drawImage(blockImage[index], j * BLOCK_SIZE, i * BLOCK_SIZE, BLOCK_SIZE,
							BLOCK_SIZE, null);
				}
			}
		}
	}

	class MyTable extends JPanel {

		private static final long serialVersionUID = 3315904850681547972L;

		public void paint(Graphics g) {
			// 背景
			g.drawImage(backGroup, 0, 0, TABLE_WIDTH + 10, TABLE_HEIGHT + 10, null);

			// 绘制障碍物
			if (status == START) {
				drawBlock(g);
			}

			if (SharedVar.game_mode == Constants.MODE_FIGHT) {
				// 绘制缩略桌面
				for (int i = 0; i < RIVAL_NUM; i++) {
					int x = rival_x + i * RIVAL_WIDTH + i * 50;
					g.drawRect(x, rival_y, 160, 100);
				}
			}
			// 绘制对方玩家的障碍物
			if (status == START) {
				drowRivalBlock(g);
			}
			if (status == WAITING) {
				// 绘制其他玩家的等待信息
				drawWaiting(g);
			}
			// 绘制结束状态
			if (end) {
				drawEndInfo(g);
			}
			// 绘制选中点的红框
			if (status == START) {
				drawSelected(g);
			}
		}
	}

	public void drawWaiting(Graphics g) {
		// 偏移量
		int off_x = 0;
		for (int i = 0; i < ChummeryInfo.USER_NUM; i++) {
			// 如果是当前玩家
			if (crrentName.equals(chummeryInfo.getName(i))) {
				// 是否准备
				if (chummeryInfo.getState(i)) {
					g.drawString("准备..", 350, 300);
					g.drawImage(animation.getPreImage(), 390, 270, null);
				}
				continue;
			}

			if ("null".equals(chummeryInfo.getName(i)) || chummeryInfo.getName(i) == null) {
				int x = rival_x + off_x * RIVAL_WIDTH + off_x * 40 + 30;
				int y = rival_y + 20;
				g.drawString("空座位,等待玩家...", x, y);
				x = rival_x + off_x * RIVAL_WIDTH + off_x * 40 + 50;
				y = y + 10;
				// 等待动画
				g.drawImage(animation.getWaitImage(), x, y, null);
				off_x++;
			} else {
				int x = rival_x + off_x * RIVAL_WIDTH + off_x * 20 + 10;
				int y = rival_y + 10;
				int index = chummeryInfo.getIcon(i);
				g.drawImage(HeadIcon.headIcon[index].getImage(), x, y, null);

				y = y + 70;
				g.drawString("玩家: " + chummeryInfo.getName(i), x, y);
				y = y + 20;
				// 是否准备
				if (chummeryInfo.getState(i)) {
					g.drawString("准备..", x, y);
					x = x + 30;
					y = y - 20;
					g.drawImage(animation.getPreImage(), x, y, null);
				}
				off_x++;
			}

		}
	}

	// 绘制对方玩家的障碍物
	public void drowRivalBlock(Graphics g) {
		// 偏移量
		int off_x = 0;
		for (int i = 0; i < ChummeryInfo.USER_NUM; i++) {
			if (crrentName.equals(chummeryInfo.getName(i))) {
				continue;
			}
			if ("null".equals(chummeryInfo.getName(i)) || chummeryInfo.getName(i) == null) {
				continue;
			}

			// 获取该玩家的障碍物
			int[][] rivalBlock = chummeryInfo.getBlock(i);
			int x = rival_x + off_x * RIVAL_WIDTH + off_x * 20;
			// 障碍物
			for (int row = 0; row < ROW; row++) {
				for (int col = 0; col < COL; col++) {
					if (SharedVar.game_mode == Constants.MODE_TOGETHER) {
						if (rivalBlock[row][col] == 0) {
							if (blocks[row][col] != 0) {
								crrentPro += 1;
								progressBar.setValue(crrentPro);
							}
							blocks[row][col] = 0;
						}
					} else if (rivalBlock[row][col] != 0) {
						int index = rivalBlock[row][col];
						g.drawImage(blockImage[index], x + col * RIVAL_SIZE, rival_y + row
								* RIVAL_SIZE, RIVAL_SIZE, RIVAL_SIZE, null);
					}
				}
			}

			// 姓名
			g.drawString("玩家: " + chummeryInfo.getName(i), x, info_y);
			g.setColor(new Color(255, 255, 255));
			if (SharedVar.game_mode == Constants.MODE_FIGHT) {
				// 完成度
				g.drawString(chummeryInfo.getDone(i), x + 80, info_y);
				off_x++;
			}
		}
	}

	public void drawEndInfo(Graphics g) {
		g.setColor(new Color(255, 110, 15));
		g.setFont(new Font("Fixedsys", Font.PLAIN, 30));
		g.drawString("本轮结束!", END_X, END_Y);

		int y = END_Y + 50;
		if (SharedVar.game_mode == Constants.MODE_FIGHT) {
			String name = "";
			String score = "";

			g.setFont(new Font("Fixedsys", Font.PLAIN, 13));
			for (int i = 0; i < endInfo.length; i += 2) {
				name = endInfo[i];
				score = endInfo[i + 1];
				g.drawString(name + "得分: " + score, END_X, y);
				y += 20;
			}
			y += 30;
		}
		g.setFont(new Font("Fixedsys", Font.PLAIN, 15));
		g.drawString("正在重置游戏...", END_X, y);
		y -= 20;
		g.drawImage(animation.getPreImage(), END_X + 100, y, null);
	}

}