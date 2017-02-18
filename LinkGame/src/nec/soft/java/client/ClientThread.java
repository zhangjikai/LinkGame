package nec.soft.java.client;

import static nec.soft.java.protocal.MyProtocol.EXIT_HALL;
import static nec.soft.java.protocal.MyProtocol.EXIT_HOUSE;
import static nec.soft.java.protocal.MyProtocol.GAME_OVER;
import static nec.soft.java.protocal.MyProtocol.GAME_PREPARE;
import static nec.soft.java.protocal.MyProtocol.GAME_ROUND;
import static nec.soft.java.protocal.MyProtocol.GAME_START;
import static nec.soft.java.protocal.MyProtocol.HALL_ROUND;
import static nec.soft.java.protocal.MyProtocol.HOUSE_CHANGE;
import static nec.soft.java.protocal.MyProtocol.JOIN_HALL;
import static nec.soft.java.protocal.MyProtocol.JOIN_HOUSE_ERROR;
import static nec.soft.java.protocal.MyProtocol.JOIN_HOUSE_SUCCESS;
import static nec.soft.java.protocal.MyProtocol.LINK_COOR;
import static nec.soft.java.protocal.MyProtocol.PRIVATE_ROUND;
import static nec.soft.java.protocal.MyProtocol.SERVER_CLOSE;
import static nec.soft.java.protocal.MyProtocol.USER_ROUND;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import javax.swing.JOptionPane;

import nec.soft.java.protocal.MyProtocol;
import nec.soft.java.protocal.ParseStr;
import nec.soft.java.protocal.UserFactory;
import nec.soft.java.protocal.UserInfo;
import nec.soft.java.share.SharedVar;
import nec.soft.java.utils.Constants;


public class ClientThread extends Thread {

	// 该客户端线程负责处理的输入流
	private BufferedReader br = null;
	private Socket socket;
	// 保存一个游戏大厅的引用
	private GameHall hall;
	LinkGame lg = new LinkGame();;

	/*
	  当前线呈处于什么状态,false表示未进入游戏房间
	  true表示已经进入游戏游戏房间
	*/
	private boolean playing = false;

	public ClientThread(Socket socket, GameHall hall) {
		this.socket = socket;
		this.hall = hall;
		// 初始化组件
		lg.initComponent();
	}

	public void run() {
		try {
			br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String line = null;
			// 不断从输入流中读取数据，并将这些数据打印输出
			while ((line = br.readLine()) != null) {
				// System.out.println(line);
				// 如果收到私聊消息
				if (line.startsWith(PRIVATE_ROUND) && line.endsWith(PRIVATE_ROUND)) {

					System.out.println("收到私聊消息");
					// 获得去掉协议的消息
					String realMsg = ParseStr.getCombMsg(line);
					hall.getChat().setChat(realMsg);
				}

				else

				// 如果收到群聊消息
				if (line.startsWith(USER_ROUND) && line.endsWith(USER_ROUND)) {
					System.out.println("收到群消息");
					// 获得去掉协议和分隔符的真实消息
					String[] splitMsg = ParseStr.getRealMsg(line);
					String model = splitMsg[0];
					String chatMsg = splitMsg[1];

					// 如果是大厅消息
					if (model.equals(HALL_ROUND)) {
						hall.getChat().setChat(chatMsg);
					}

					// 如果是游戏消息
					if (model.equals(GAME_ROUND)) {
						lg.getChat().setChat(chatMsg);
					}
				}

				else

				// 如果收到新用户加入大厅消息
				if (line.startsWith(JOIN_HALL) && line.endsWith(JOIN_HALL)) {
					System.out.println("收到新用户加入大厅消息");
					// 获得去掉协议的消息
					String combMsg = ParseStr.getCombMsg(line);
					// 根据消息创建对象
					UserInfo user = UserFactory.createUserInfo(combMsg);
					// 保存在线用户
					Client.onlineUser.add(user);
					// 在聊天窗口显示
					String joinMsg = user.getName() + "加入了游戏大厅!";
					hall.getChat().setChat(joinMsg);
					// 在大厅可更新的组件插入用户的信息
					hall.insertUI(user);
				}

				else

				// 如果收到加入房间成功消息
				if (line.startsWith(JOIN_HOUSE_SUCCESS) && line.endsWith(JOIN_HOUSE_SUCCESS)) {
					System.out.println("收到加入成功消息");
					// 获得去掉协议和分隔符的真实消息
					String[] splitMsg = ParseStr.getRealMsg(line);
					String name = splitMsg[0];
					int houseNum = Integer.valueOf(splitMsg[1]);
					int seat = Integer.valueOf(splitMsg[2]);

					UserInfo joinUser = null;
					// 设置在线用户中的信息
					for (UserInfo user : Client.onlineUser) {
						if (user.getName().equals(name)) {
							user.setHouseNum(houseNum);
							user.setSeat(seat);
							joinUser = user;
							break;
						}
					}
					// 如果加入房间用户是本客户端的用户
					if (Client.crrentUser.getName().equals(name)) {
						// 创建一个房间信息
						Client.chummery = new ChummeryInfo();
						Client.chummery.setName(seat, name);
						Client.chummery.setUserInfo(seat, Client.crrentUser);
						// 初始化参数
						lg.init();
						// 设置等待状态
						lg.setWaiting();
						lg.getChat().setChat("欢迎进入房间" + houseNum + "!");
					} else
					// 如果是同一个房间的其他用户
					if (Client.crrentUser.getHouseNum() == houseNum && Client.chummery != null) {
						Client.chummery.setName(seat, name);
						Client.chummery.setUserInfo(seat, joinUser);
						lg.addRow(joinUser);
						lg.getChat().setChat(name + "加入了房间!");
					}

					// 更新房间列表信息
					hall.updateHouseInfo();
				}

				else

				// 如果收到房间信息更新消息
				if (line.startsWith(HOUSE_CHANGE) && line.endsWith(HOUSE_CHANGE)) {
					// 获得去掉协议和分隔符的真实消息
					String[] splitMsg = ParseStr.getRealMsg(line);
					int seat = Integer.valueOf(splitMsg[0]);
					String name = splitMsg[1];
					boolean state = Boolean.valueOf(splitMsg[2]);

					UserInfo joinUser = null;

					for (UserInfo user : Client.onlineUser) {
						if (user.getName().equals(name)) {
							joinUser = user;
							break;
						}
					}

					if (Client.chummery != null) {
						Client.chummery.changeInfo(seat, name, state);
						Client.chummery.setUserInfo(seat, joinUser);
						// 重置同房间用户表
						lg.reSetUserTable();
					}
				}

				else

				// 收到坐标
				if (line.startsWith(LINK_COOR) && line.endsWith(LINK_COOR)) {
					// 获得去掉协议和分隔符的真实消息
					String[] splitMsg = ParseStr.getRealMsg(line);
					System.out.println(splitMsg[0] + "----:" + splitMsg[1] + splitMsg[2]
							+ splitMsg[3] + splitMsg[4] + splitMsg[5]);
					// 清除同桌玩家的障碍物
					Client.chummery.setClear(splitMsg);
					int seat = Integer.valueOf(splitMsg[0]);
					// 设置同桌玩家的完成度
					Client.chummery.setDone(seat, splitMsg[5]);
					lg.repaint();
				}

				else

				// 如果收到准备消息
				if (line.startsWith(GAME_PREPARE) && line.endsWith(GAME_PREPARE)) {
					System.out.println("准备消息");
					// 获得去掉协议的消息
					String combMsg = ParseStr.getCombMsg(line);
					int seat = Integer.valueOf(combMsg);

					if (Client.chummery != null) {
						Client.chummery.setState(seat, true);
						for (int i = 0; i < 4; i++) {
							System.out.println(Client.chummery.getName(i)
									+ Client.chummery.getState(i));
						}
					}
				}

				else

				// 如果收到游戏开始
				if (line.startsWith(GAME_START) && line.endsWith(GAME_START)) {
					System.out.println("收到游戏开始消息");
					// 获得去掉协议的消息
					String combMsg = ParseStr.getCombMsg(line);
					int houseNum = 0;
					String[] lines = null;
					if (SharedVar.game_mode == Constants.MODE_TOGETHER) {
						lines = combMsg.split(MyProtocol.SPLIT_SIGN);
						houseNum = Integer.valueOf(lines[0]);
					} else {
						houseNum = Integer.valueOf(combMsg);
					}
					
					System.out.println("游戏房号:" + houseNum);
					hall.addPlayingInfo(houseNum);

					// 如果是同一个房间的玩家
					if (Client.crrentUser.getHouseNum() == houseNum && Client.chummery != null) {
						if (lg != null) {
							// 完成度全部重置为0
							Client.chummery.reSetDone();
							// 初始化地图
							if (SharedVar.game_mode == Constants.MODE_TOGETHER)
								lg.initMap(lines);
							else
								lg.initMap();
							lg.start();
						}
					}
				}

				else

				// 如果收到退出房间消息
				if (line.startsWith(EXIT_HOUSE) && line.endsWith(EXIT_HOUSE)) {
					// 获得去掉协议和分隔符的真实消息
					String[] splitMsg = ParseStr.getRealMsg(line);
					String name = splitMsg[0];
					int houseNum = Integer.valueOf(splitMsg[1]);
					int seat = Integer.valueOf(splitMsg[2]);

					UserInfo exitUser = null;
					// 设置在线用户中的信息
					for (UserInfo user : Client.onlineUser) {
						if (user.getName().equals(name)) {
							user.setHouseNum(-1);
							user.setSeat(-1);
							exitUser = user;
							break;
						}
					}

					// 如果退出房间的用户本客户端的用户
					if (Client.crrentUser.getName().equals(name)) {
						Client.chummery = null;
					} else
					// 如果是同一个房间的其他用户
					if (Client.crrentUser.getHouseNum() == houseNum && Client.chummery != null) {
						// 删除房间信息里的退出的玩家的信息
						Client.chummery.removeUser(seat);
						// 删除房间表里的退出的玩家的信息
						lg.removeUI(name);
						/*lg.removeUI(0);*/
						lg.getChat().setChat(name + "退出了房间!");
						for (int i = 0; i < 4; i++) {
							System.out.println(Client.chummery.getName(i)
									+ Client.chummery.getState(i));
						}
					}

					// 更新房间列表信息
					hall.updateHouseInfo();
				}

				else

				// 如果收到游戏结束消息
				if (line.startsWith(GAME_OVER) && line.endsWith(GAME_OVER)) {
					// 获得去掉协议和分隔符的真实消息
					String[] splitMsg = ParseStr.getRealMsg(line);
					for (String str : splitMsg) {
						System.out.println(str);
					}
					System.out.println("收到游戏结束消息");
					String name = "";
					int score = 0;
					int houseNum = -1;

					// 保存座号与分数的对应关系
					// 更改在线用户中的分数
					for (int i = 0; i < splitMsg.length; i += 2) {
						name = splitMsg[i];
						score = Integer.valueOf(splitMsg[i + 1]);
						for (UserInfo user : Client.onlineUser) {
							if (user.getName().equals(name)) {
								user.addScore(score);
								houseNum = user.getHouseNum();
								System.out.println(user.getName() + ":" + user.getScore());
								// 在大厅更新分数
								hall.setScore(user.getScore(), user.getName());
								break;
							}
						}
					}
					// 删除该房间"游戏中"图标
					hall.removePlayingInfo(houseNum);

					// 如果是用户强退,且是当前玩家
					if (Client.crrentUser.getName().equals(name) && score == -50) {
						continue;
					}

					// 如果是该房间的玩家
					if (Client.crrentUser.getHouseNum() == houseNum && Client.chummery != null
							&& houseNum != -1) {
						// 重置房间信息
						Client.chummery.reSetInfo();
						System.out.println("重置房间信息");
						lg.setEnd(splitMsg);
						try {
							Thread.sleep(4000);
						} catch (Exception ex) {
							ex.printStackTrace();
						}

						// 更新房间里的分数
						for (int i = 0; i < ChummeryInfo.USER_NUM; i++) {
							if (!"null".equals(Client.chummery.getName(i))
									&& Client.chummery.getName(i) != null) {
								lg.setScore(Client.chummery.getScore(i), Client.chummery.getName(i));
							}
						}

						// 重置所有参数
						lg.init();
						// 设置等待状态
						lg.setWaiting();
						// 重新初始化地图
						lg.initMap();
					}
				}

				else

				if (line.startsWith(JOIN_HOUSE_ERROR) && line.endsWith(JOIN_HOUSE_ERROR)) {
					hall.showJoinErroe();
				}

				else

				// 如果收到用户退出大厅消息
				if (line.startsWith(EXIT_HALL) && line.endsWith(EXIT_HALL)) {
					// 获得去掉协议的消息
					String exitName = ParseStr.getCombMsg(line);
					String exitMsg = exitName + "退出了游戏大厅!";
					hall.getChat().setChat(exitMsg);
					UserInfo exitUser = null;
					// 删除退出用户信息
					for (UserInfo user : Client.onlineUser) {
						if (user.getName().equals(exitName)) {
							exitUser = user;
							Client.onlineUser.remove(user);
							break;
						}
					}

					// 如果是该房间的玩家
					if (Client.crrentUser.getHouseNum() == exitUser.getHouseNum()
							&& Client.chummery != null && exitUser.getHouseNum() != -1) {
						Client.chummery.removeUser(exitUser.getSeat());
						// 删除房间表里的退出的玩家的信息
						lg.removeUI(exitName);
					}

					// 删除退出用户在组件里的信息
					hall.removeUI(exitName);
					// 更新房间列表信息
					hall.updateHouseInfo();
					System.out.println("收到用户退出大厅消息");
				}

				else

				if (line.equals(SERVER_CLOSE)) {
					JOptionPane.showMessageDialog(null, "11111111111", "server close", 1);
					System.out.println("server close");

					// 退出网路模式，返回开始界面选游戏模式
					System.exit(0);
				}
			}// while
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		// 使用finally块来关闭该线程对应的输入流
		finally {
			try {
				if (br != null) {
					br.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
}
