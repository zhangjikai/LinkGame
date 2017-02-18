package nec.soft.java.client;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import nec.soft.java.frame.MenuFrame;
import nec.soft.java.protocal.ParseStr;
import nec.soft.java.protocal.UserFactory;
import nec.soft.java.protocal.UserInfo;
import nec.soft.java.utils.ShowHelper;

import org.jvnet.substance.skin.SubstanceOfficeBlue2007LookAndFeel;

import static nec.soft.java.protocal.MyProtocol.*;

public class Login {
	private JFrame jf = new JFrame("登陆");
	private ImageIcon backImage;

	private static Login lg;

	// Ip输入文本
	private JTextField ipText = new JTextField(16);
	// 用户名
	private JTextField nameText = new JTextField(16);
	// 密码
	private JPasswordField passwdText = new JPasswordField(16);

	private JLabel ipLb = new JLabel("IP:");
	private JLabel nameLb = new JLabel("账户:");
	private JLabel passwdLb = new JLabel("密码:");

	private JButton registerBt = new JButton("注册");
	private JButton loginBt = new JButton("登陆");
	private JButton cancelBt = new JButton("退出");

	private String result;
	public static String ipStr = "127.0.0.1";
	private final int TABLE_WIDTH = 750;
	private final int TABLE_HEIGHT = 505;

	public void init() throws Exception {
		jf.setLayout(null);
		jf.setResizable(false);
		// backImage = new ImageIcon("image/back.jpg");
		backImage = new ImageIcon("image/QinShiMingYue/52.jpg");

		JLabel label = new JLabel("在这里输入服务器的IP");
		// Label组件
		label.setBounds(230, 260, 250, 30);
		label.setForeground(Color.red);
		label.setFont(new Font("Monospaced", Font.ITALIC, 20));
		ipLb.setBounds(230, 290, 50, 30);
		ipLb.setForeground(Color.red);
		ipLb.setFont(new Font("Monospaced", Font.ITALIC, 15));
		nameLb.setBounds(230, 320, 50, 30);
		nameLb.setForeground(Color.red);
		nameLb.setFont(new Font("Monospaced", Font.ITALIC, 15));
		passwdLb.setBounds(230, 350, 50, 30);
		passwdLb.setForeground(Color.red);
		passwdLb.setFont(new Font("Monospaced", Font.ITALIC, 15));
		jf.add(label);
		jf.add(ipLb);
		jf.add(nameLb);
		jf.add(passwdLb);

		// ----------------Text组件---------------
		// 默认问本地IP
		ipText.setText(ipStr);
		// 输入密码时的字符
		passwdText.setEchoChar('●');
		ipText.setBounds(280, 295, 130, 20);
		nameText.setBounds(280, 325, 130, 20);
		passwdText.setBounds(280, 355, 130, 20);
		jf.add(ipText);
		jf.add(nameText);
		jf.add(passwdText);

		// 按钮组件
		registerBt.setBounds(230, 395, 60, 30);
		registerBt.setSize(50, 25);
		jf.add(registerBt);
		loginBt.setBounds(300, 395, 60, 30);
		loginBt.setSize(50, 25);
		jf.add(loginBt);
		cancelBt.setBounds(370, 395, 60, 30);
		cancelBt.setSize(50, 25);
		jf.add(cancelBt);

		JLabel bakcLb = new JLabel();
		bakcLb.setSize(TABLE_WIDTH, TABLE_HEIGHT);
		bakcLb.setIcon(backImage);
		jf.add(bakcLb);

		// 初始化监听
		initListener();
		// jf.setBounds(250,250,TABLE_WIDTH,TABLE_HEIGHT);
		jf.setSize(TABLE_WIDTH, TABLE_HEIGHT);
		ShowHelper.showCenter(jf);
		jf.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		jf.setVisible(true);

		jf.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				MenuFrame.open();
				jf.dispose();
			}
		});
	}

	public void initListener() {
		registerBt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ipStr = ipText.getText();
				new ReDialog(jf, "注册", true).setVisible(true);
			}
		});

		loginBt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ipStr = ipText.getText();
				String name = nameText.getText();
				String passwd = String.valueOf(passwdText.getPassword());
				if (name.length() == 0 || name == null) {
					JOptionPane.showMessageDialog(null, "用户名不能为空");
					return;
				}
				if (passwd.length() == 0 || passwd == null) {
					JOptionPane.showMessageDialog(null, "密码不能为空");
					return;
				}

				// 获得连接
				Client.initScoket(ipStr);
				// 发送登陆请求
				Client.ps.println(LOGIN + name + SPLIT_SIGN + passwd + LOGIN);
				String result = "";
				try {
					// 接收服务器发过来的信息
					result = Client.brServer.readLine();
				} catch (Exception ex) {
					ex.printStackTrace();
				}

				// 如果重复登陆
				if (result.equals(LOGIN_AGAIN)) {
					JOptionPane.showMessageDialog(null, "重复登陆");
					return;
				}

				if (result.equals(LOGIN_SUCCESS)) {
					java.util.List<Integer> gameInfo = new java.util.ArrayList<Integer>();
					try {
						// 下面一个循环接收服务器端发过来的所有用户的信息
						while ((result = Client.brServer.readLine()) != null) {
							if (result.startsWith(UPDATE_ALL) && result.endsWith(UPDATE_ALL)) {
								// 获得组合消息
								String combMsg = ParseStr.getCombMsg(result);
								// 根据消息创建对象
								UserInfo user = UserFactory.createUserInfo(combMsg);
								// 保存在线用户
								Client.onlineUser.add(user);
							}

							if (result.startsWith(HOUSE_INFO) && result.endsWith(HOUSE_INFO)) {
								// 获得组合消息
								String combMsg = ParseStr.getCombMsg(result);
								int houseNum = Integer.valueOf(combMsg);
								gameInfo.add(houseNum);
							}

							// 如果接收到停止发送消息则退出循环
							if (result.equals(STOP)) {
								break;
							}
						}
					} catch (Exception ex) {
						ex.printStackTrace();
					}

					for (UserInfo user : Client.onlineUser) {
						// 如果是当前用户,则保存当前用户信息
						if (user.getName().equals(name)) {
							Client.crrentUser = user;
						}
					}

					JOptionPane.showMessageDialog(null, "登录成功！");
					GameHall hall = new GameHall();
					try {
						new ClientThread(Client.socket, hall).start();
					} catch (Exception ex) {
						ex.printStackTrace();
					}

					hall.init();
					hall.initPlayingInfo(gameInfo);
					hall.getChat().setChat("欢迎加入网络连连看游戏大厅!");
					jf.dispose();
				} else if (result.equals(LOGIN_ERROR)) {
					JOptionPane.showMessageDialog(null, "登录失败！用户名和密码不存在");
					// 关闭连接
					Client.closeRs();
				}
				// System.out.println(Thread.currentThread().getName());
			}
		});

		cancelBt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});

		final Point origin = new Point();
		// 实现拖动
		jf.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				origin.x = e.getX();
				origin.y = e.getY();
			}
		});
		jf.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent e) {
				Point p = jf.getLocation();
				jf.setLocation(p.x + e.getX() - origin.x, p.y + e.getY() - origin.y);
			}
		});
	}

	public void dispose() {
		jf.dispose();
	}

	public static void open() {
		lg = new Login();
		try {
			lg.init();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void close() {
		if (lg != null)
			lg.dispose();

		lg = null;
	}
}