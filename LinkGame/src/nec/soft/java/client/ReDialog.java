package nec.soft.java.client;
import static nec.soft.java.protocal.MyProtocol.NAME_REP;
import static nec.soft.java.protocal.MyProtocol.REGISTER;
import static nec.soft.java.protocal.MyProtocol.REGISTER_ERROR;
import static nec.soft.java.protocal.MyProtocol.REGISTER_SUCCESS;
import static nec.soft.java.protocal.MyProtocol.SPLIT_SIGN;

import java.awt.Color;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.BevelBorder;

import org.jvnet.substance.skin.SubstanceOfficeBlue2007LookAndFeel;

//注册框
class ReDialog extends JDialog
{
	
	private static final long serialVersionUID = 4005245268448235660L;
	private final int WIDTH = 360;
	private final int HEIGHT = 400;
	//头像表的行列数
	private final int HEAD_ROW = 4;
	private final int HEAD_COL = 5;
	private JLabel nameLb = new JLabel("用户名:");
	private JLabel passwdLb = new JLabel("密码:");
	private JLabel sexLb = new JLabel("性别:");
	private JLabel headLb = new JLabel("头像:");
	private JLabel headIconLb = new JLabel();
	
	
	private String[] sexStr = {"男" , "女"};
	//保存头像表的图表索引
	private Object[][] headsIndex = new Object[HEAD_ROW][HEAD_COL];
	private String[] headTile = {"sdf","sdf","df","fsd"};
	
	
	//用户名
	private JTextField nameText = new JTextField(16);
	//密码
	private JPasswordField passwdText = new JPasswordField();
	private JComboBox sexBox = new JComboBox(sexStr); 
	
	
	private JButton okBt = new JButton("提交");
	private JButton cancel = new JButton("取消");
	
	
	//默认头像索引
	private int indexIcon = 2;
	//头像表
	public IconTable headTable;
	//头像表对应的model模型
	private ExtendedTableModel model;
	
	
//	private static ReDialog rdlg;
	
	
	public ReDialog()
	{
	}

	public ReDialog(Frame owner, String title , boolean modal)
	{
		super(owner , title , modal);
		
		this.setDefaultCloseOperation(0);
	}
	
	//初始化块
	{
		int imageNum = 0;
		//初始化所有图象的索引
		for(int i = 0 ; i < HEAD_ROW ; i++)
		{
			for(int j = 0; j < HEAD_COL ; j++)
			{
				headsIndex[i][j] = (++ imageNum);
			}
		}

		//初始化头像表
		model = new ExtendedTableModel(headTile , headsIndex);
		headTable = new IconTable(
			model , 0 , IconTable.ALL_CELL_ICON);
		//初始化组件
		initComment();
		initListener();
	}

	public void initComment()
	{
		setLayout(null);
		passwdText.setEchoChar('●');
		ImageIcon backImage = new ImageIcon("image/QinShiMingYue/registerBG.jpg");
		//Label组件
		nameLb.setBounds(10,25,50,30);
		passwdLb.setBounds(10,55,50,30);
		sexLb.setBounds(10,87,50,30);
		
		headLb.setBounds(220,55,55,30);
		add(sexLb);
		add(nameLb);
		add(passwdLb);
		add(headLb);
		
		//用户输入组件
		nameText.setBounds(60,30,130,20);
		passwdText.setBounds(60,60,130,20);
		sexBox.setBounds(60,90,50,30);
		add(passwdText);
		add(nameText);
		add(sexBox);
		
		//头像
		headIconLb.setBounds(260,40,60,60);
		headIconLb.setIcon(HeadIcon.headIcon[indexIcon]);
		headIconLb.setBorder(
			BorderFactory.createLineBorder(new Color(0 , 0 ,0)));
		headTable.setBounds(0 , 170, WIDTH ,230);
		headTable.setBorder(
			BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		add(headTable);
		add(headIconLb);
		
		//按钮
		okBt.setSize(50,25);
		okBt.setBounds(230,130,60,30);
		cancel.setBounds(290,130,60,30);
		add(okBt);
		add(cancel);
		
		JLabel bakcLb = new JLabel();
		bakcLb.setBounds(0 , 0 ,WIDTH , 220);
		bakcLb.setIcon(backImage);
		add(bakcLb);
		setBounds(300,250,WIDTH,HEIGHT);
	}
	
	//初始化监听器
	public void initListener()
	{
		headTable.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent e)
			{
				//获得单元格的值
				int col = headTable.getSelectedColumn();
				int row = headTable.getSelectedRow();
				indexIcon = (Integer)headTable.getValueAt(row, col);
				headIconLb.setIcon(HeadIcon.headIcon[indexIcon]);
			}
		});

		cancel.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				dispose();
			}
		});
		
		okBt.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				String name = nameText.getText();
				String sex = (String)sexBox.getSelectedItem();
				char[] passwd = passwdText.getPassword();
				 
				if(name.length() == 0 || name == null)
				{
					 JOptionPane.showMessageDialog(
						 null, "用户名不能为空");
					 return;
				}
				if(passwd.length == 0 || passwd == null)
				{
					JOptionPane.showMessageDialog(
						null, "密码不能为空"); 
					 return;
				}
				if(name.length() > 10 || name.length() < 3)
				{
					JOptionPane.showMessageDialog(
						null, "用户名应该在3到10位之间"); 
					 return;
				}
				if(passwd.length > 10 || passwd.length < 3)
				{
					JOptionPane.showMessageDialog(
						null, "密码应该在3到10位之间"); 
					 return;
				}
				//获得连接
				Client.initScoket(Login.ipStr);
				Client.ps.println(REGISTER + name 
					+ SPLIT_SIGN + String.valueOf(passwd) 
					+ SPLIT_SIGN + sex
					+ SPLIT_SIGN + indexIcon
					+ REGISTER);
				
				String result ="";
				try
				{
					result=Client.brServer.readLine();
				}
				catch (Exception ex)
				{
					ex.printStackTrace();
				}

				if(result.equals(REGISTER_SUCCESS))
				{
					System.out.println(result);
					JOptionPane.showMessageDialog(
						null, "注册成功！"); 
					//关闭连接
					Client.closeRs();
					dispose();
				}

				if(result.equals(NAME_REP))
				{
					JOptionPane.showMessageDialog(
						null, "用户名重复"); 
					System.out.println(result);
						
				}

				if(result.equals(REGISTER_ERROR))
				{
					JOptionPane.showMessageDialog(
						null, "注册失败,服务器数据库出错"); 
						
				}

				//关闭连接
				Client.closeRs();
			}
		});
	}
}