package nec.soft.java.client;

import javax.swing.*;

import nec.soft.java.protocal.*;

import java.awt.*;
import java.awt.event.*;

import static nec.soft.java.protocal.MyProtocol.*;

//聊天面板
public class ChatPanel {
	// 大厅模式
	public static final int HALL_MODEL = 11;
	public static final int GAME_MODEL = 12;

	// 游戏房间模式
	private JTextArea chatArea = new JTextArea(12, 12);
	private JComboBox userBox = new JComboBox();
	private JTextField chatText = new JTextField(16);
	private JButton okBt;
	private ImagePane chatPanel = new ImagePane("image/GameHall/chatBack.jpg");

	public ChatPanel(int md) {
		final int model = md;
		String currentName = Client.crrentUser.getName();
		chatArea.setOpaque(false);
		if (model == HALL_MODEL) {
			userBox.addItem("所有玩家");
			for (UserInfo user : Client.onlineUser) {
				userBox.addItem(user.getName());
			}
		} else {
			userBox.addItem("所有玩家");
		}

		okBt = new JButton(new ImageIcon("image/chat/send.gif"));
		okBt.setPreferredSize(new Dimension(16, 16));
		userBox.setPreferredSize(new Dimension(50, 20));

		JPanel bottom = new JPanel();
		bottom.add(userBox);
		bottom.add(chatText);
		bottom.add(okBt);
		bottom.setBackground(new Color(126, 205, 236));
		// chatArea.setBackground(new Color(126,205,236));
		// chatArea.setFont(new Font("宋体" , Font.BOLD , 14));

		chatPanel.setLayout(new BorderLayout());
		chatPanel.add(chatArea);
		chatPanel.add(bottom, BorderLayout.SOUTH);

		okBt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String chatMsg = chatText.getText();
				if (chatMsg.length() == 0) {
					return;
				}

				String userItem = (String) userBox.getSelectedItem();
				if (model == HALL_MODEL) {
					if (userItem == "所有玩家") {
						chatMsg = Client.crrentUser.getName() + "说:" + chatMsg;
						Client.ps.println(USER_ROUND + HALL_ROUND + SPLIT_SIGN + chatMsg
								+ USER_ROUND);
					} else {
						setChat("你鬼鬼祟祟地对:" + userItem + "说:" + chatMsg);

						chatMsg = Client.crrentUser.getName() + "鬼鬼祟祟地对你说:" + chatMsg;

						Client.ps.println(PRIVATE_ROUND + userItem + SPLIT_SIGN + chatMsg
								+ PRIVATE_ROUND);
					}
				}

				if (model == GAME_MODEL) {
					chatMsg = Client.crrentUser.getName() + "大声的喧哗:" + chatMsg;
					Client.ps.println(USER_ROUND + GAME_ROUND + SPLIT_SIGN + chatMsg + USER_ROUND);
				}

				chatText.setText("");
			}
		});
	}

	public void setChat(String msg) {
		chatArea.append(msg + "\n");
	}

	public void addUserItem(String item) {
		userBox.addItem(item);
		userBox.updateUI();
	}

	public void removeItem(String item) {
		userBox.removeItem(item);
		userBox.updateUI();
	}

	public void clearText() {
		chatArea.setText("");
	}

	public JPanel getPanel() {
		// chatPanel.setOpaque(false);
		return chatPanel;
	}

	/*public static void main(String[] args) 
	{
		JFrame jf = new JFrame();
		jf.add(new ChatPanel().getPanel());
		jf.pack();
		jf.setVisible(true);
	}*/
}
