package nec.soft.java.client;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;

import nec.soft.java.protocal.*;

import java.awt.*;
import java.awt.event.*;

//游戏大厅
public class GameHall {
	// 获得屏幕的大小
	public static Dimension screenSize = Toolkit.getDefaultToolkit()
			.getScreenSize();
	private JFrame jf = new JFrame("游戏大厅");
	// 房间列表
	private HouseList housList = new HouseList();
	String[] names = { "所有玩家" };
	// 聊天面板
	private ChatPanel chatPanel = new ChatPanel(ChatPanel.HALL_MODEL);

	private Object[] headTile = { "头像", "姓名", "性别", "积分" };
	// 用户列表
	private ExtendedTableModel model;
	private IconTable usrTable;// = new ReDialog().headTable;
	private ToolBar mb = new ToolBar();
	private JSplitPane topSlitPane = new JSplitPane(
			JSplitPane.VERTICAL_SPLIT, true);
	private JSplitPane horSlitPane = new JSplitPane(
			JSplitPane.VERTICAL_SPLIT, true);
	private JSplitPane leftSlitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
			true);
	// 用户形象照
	// private JLabel userLb;
	// 商城模块
	//private ShopPanel shop = new ShopPanel();

	// 排行榜
	//private JDialog sortDialog;
	//private SortTable sortTable;

	public void init() {
		// 初始化用户列表
		model = new ExtendedTableModel(headTile, Client.onlineUser);
		usrTable = new IconTable(model, 0, IconTable.GENERAL){ // 设置jtable的单元格为透明的
			public Component prepareRenderer(TableCellRenderer renderer,
					int row, int column) {
				Component c = super.prepareRenderer(renderer, row, column);
				if (c instanceof JComponent) {
					((JComponent) c).setOpaque(false);
				}
				return c;
			}
		};
		
		usrTable.setOpaque(false);

		//Box topPanel = Box.createVerticalBox();
		ImagePane topPanel=new ImagePane("image/GameHall/back.jpg");
		topPanel.setLayout(new BorderLayout());
		topPanel.add(housList,BorderLayout.CENTER);
		topPanel.add(mb.getToolBar(),BorderLayout.SOUTH);
		// mb.setBackground(new Color(20 , 165 ,77));

		// userLb = new JLabel(new ImageIcon("image/user/boy.jpg"));
		// userLb.setSize(200 , 200);

		JScrollPane showPanel=new JScrollPane(usrTable);
		ImagePane showBackPanel=new ImagePane("image/GameHall/showBack.jpg");
		showPanel.setOpaque(false);
		showPanel.getViewport().setOpaque(false);
		showBackPanel.setLayout(new GridLayout(1,1));
		showBackPanel.add(showPanel);
		
		horSlitPane.add(showBackPanel);
		horSlitPane.add(chatPanel.getPanel());
		horSlitPane.setDividerSize(5);
		horSlitPane.setDividerLocation(250);

		// 桌面左边
		leftSlitPane.add(topPanel);
		leftSlitPane.add(horSlitPane);
		// 设置分割条大小和位置
		leftSlitPane.setDividerSize(5);
		jf.setBounds(50, 50, screenSize.width-100, screenSize.height - 100);
		leftSlitPane.setDividerLocation(jf.getWidth()-300);
		// leftSlitPane.disable();
		
		ImagePane title= new ImagePane("image/GameHall/title.jpg");
		topSlitPane.add(title);
		topSlitPane.add(leftSlitPane);
		topSlitPane.setDividerSize(5);
		topSlitPane.setDividerLocation(85);

		jf.add(topSlitPane);
		jf.setBounds(50, 50, screenSize.width-100, screenSize.height - 100);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//jf.setResizable(false);
		// jf.pack();
		jf.setVisible(true);

		// 鼠标移动事件被Box组件覆盖,所以使用Box组件来响应
		topPanel.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseMoved(MouseEvent e) {
				housList.mousedMove(e);
			}
		});

		topPanel.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getX() > housList.getWidth()
						|| e.getY() > housList.getHeight()) {
					return;
				}

				// 响应双击事件
				if (e.getClickCount() >= 2) {
					housList.doubleClick();
				}
			}
		});

		// 下一页按钮监听事件
		mb.getNextPage().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				housList.nextPage();
			}
		});

		// 上一页按钮监听事件
		mb.getPrePage().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				housList.prePage();
			}
		});

		// 创建房间事件
		mb.getCreatButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (Client.crrentUser.getHouseNum() != -1) {
					JOptionPane.showMessageDialog(null, "你已经在房间中,不能创建房间！");
					return;
				}

				int result = JOptionPane.showConfirmDialog(null, "是否创建一个空的房间?",
						"- -?", JOptionPane.YES_NO_OPTION);

				if (result == JOptionPane.YES_OPTION) {
					Client.ps.println(MyProtocol.CREATE_HOUSE
							+ MyProtocol.CREATE_HOUSE);

				}
			}
		});

		/*mb.getPropButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				shop.showClothDg();
			}
		});

		mb.getGiftButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				shop.showGiftDg();
			}
		});

		mb.getGiftButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				shop.showGiftDg();
			}
		});

		mb.getOrderButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sortDialog = new JDialog(jf, "积分排行");
				sortTable = new SortTable(1, Client.onlineUser);
				sortDialog.add(new JScrollPane(sortTable.getTalbe()));
				sortDialog.pack();
				sortDialog.setBounds(150, 150, 200, 200);
				sortDialog.setVisible(true);
			}
		});*/

	}

	public ChatPanel getChat() {
		return chatPanel;
	}

	public void initPlayingInfo(java.util.List<Integer> gameInfo) {
		housList.initGameInfo(gameInfo);
		housList.repaint();
	}

	public void addPlayingInfo(int houseNum) {
		housList.setGameInfo(houseNum);
		housList.repaint();
	}

	public void removePlayingInfo(int houseNum) {
		housList.removeGameInfo((Integer) houseNum);
		housList.repaint();
	}

	public void insertUI(UserInfo user) {
		// 用户表增加一行
		model.addRow(user);
		usrTable.updateUI();
		// 聊天下拉表增加一用户
		chatPanel.addUserItem(user.getName());
	}

	public void removeUI(String exitUser) {
		// 用户表移除一行
		model.removeRow(exitUser);
		usrTable.updateUI();
		// 聊天下拉表移除一用户
		chatPanel.removeItem(exitUser);
	}

	public void setScore(int score, String name) {
		// 更新分数
		model.updateScore(score, name);
		usrTable.updateUI();
	}

	public void showJoinErroe() {
		JOptionPane.showMessageDialog(jf, "房间正在游戏中或人数已满,观战模式未开通");
	}

	public void updateHouseInfo() {
		housList.reSetHouseInfo();
		housList.repaint();
	}
	/*
	 * public static void main(String[] args) { new GameHall().init(); }
	 */
}
