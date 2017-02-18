import java.io.*;
import java.net.*;
import java.util.*;

import protocal.*;
import static protocal.MyProtocol.*;

class ServerThread extends Thread {
	// 保存当前线程socket
	private Socket currentSk;
	private BufferedReader br = null;
	private PrintStream ps = null;

	public ServerThread(Socket socket) {
		currentSk = socket;
	}

	public void run() {
		String line = null;
		try {
			System.out.println(Thread.currentThread().getName());
			// 当前Scoket对应的输入流
			br = new BufferedReader(new InputStreamReader(currentSk.getInputStream()));

			// 获取当前Scoket对应的输输出流
			ps = new PrintStream(currentSk.getOutputStream());

			while ((line = br.readLine()) != null) {
				// 如果收到注册消息
				if (line.startsWith(REGISTER) && line.endsWith(REGISTER)) {

					// 获得去掉协议和分隔符的真实消息
					String[] splitMsg = ParseStr.getRealMsg(line);
					String result = Server.connSql.register(splitMsg[0], splitMsg[1], splitMsg[2],
							Integer.valueOf(splitMsg[3]));
					ps.println(result);
				}

				else
				// 如果收到登录消息
				if (line.startsWith(LOGIN) && line.endsWith(LOGIN)) {
					System.out.println("收到登录消息");
					// 获得去掉协议和分隔符的真实消息
					String[] splitMsg = ParseStr.getRealMsg(line);

					// 遍历在线用户,是否重复登录
					for (UserInfo user : Server.clients.keySet()) {
						if (user.getName().equals(splitMsg[0])) {
							ps.println(LOGIN_AGAIN);
							return;
						}
					}

					// 将用户名和密码传入数据库连接类验证
					String result = Server.connSql.login(splitMsg[0], splitMsg[1]);

					if (result.startsWith(LOGIN_SUCCESS) && result.endsWith(LOGIN_SUCCESS)) {
						// 通知客户端登陆成功
						ps.println(LOGIN_SUCCESS);
						// 获得去掉协议的消息
						String realMsg = ParseStr.getCombMsg(result);
						// 根据真实消息创建UserInfo对象
						UserInfo user = UserFactory.createUserInfo(realMsg);
						System.out.println(user.getName() + " " + user.getSex() + " "
								+ user.getScore() + " " + user.getIconIndex() + " "
								+ user.getHouseNum() + " " + user.getSeat());
						// 保存在线用户信息
						Server.clients.put(user, currentSk);
						// 向客户端发送所有在线用户的信息
						DealWithMsg.printlnAllUser(ps);
						// 通知其他客户端有人加入大厅
						DealWithMsg.joinHall(user, currentSk);
						System.out.println("登录成功");
					} else {
						ps.println(LOGIN_ERROR);
					}
				}

				// 如果收到私聊消息
				if (line.startsWith(PRIVATE_ROUND) && line.endsWith(PRIVATE_ROUND)) {
					System.out.println("收到私聊消息");
					// 获得去掉协议和分隔符的真实消息
					String[] splitMsg = ParseStr.getRealMsg(line);
					DealWithMsg.sendPrivate(splitMsg);
				}

				else

				// 如果收到群聊消息
				if (line.startsWith(USER_ROUND) && line.endsWith(USER_ROUND)) {
					System.out.println(line);
					DealWithMsg.sendUserRound(line, currentSk);
				}

				else

				// 收到坐标
				if (line.startsWith(LINK_COOR) && line.endsWith(LINK_COOR)) {
					// 获得去掉协议和分隔符的真实消息
					String[] splitMsg = ParseStr.getRealMsg(line);
					System.out.println(splitMsg[0] + splitMsg[1] + splitMsg[2] + splitMsg[3]
							+ splitMsg[4]);
					// 转发给其他玩家
					DealWithMsg.sendCoor(splitMsg, currentSk);
				}

				else

				// 如果收到退出大厅消息
				if (line.startsWith(EXIT_HALL) && line.endsWith(EXIT_HALL)) {
					// Server.sendUserRound(line);
					for (int i = 0; i < 100; i++) {
						System.out.println(EXIT_HALL);
					}
				}

				else

				// 如果收到加入房间消息
				if (line.startsWith(JOIN_HOUSE) && line.endsWith(JOIN_HOUSE)) {
					// 获得去掉协议的消息
					String houseStr = ParseStr.getCombMsg(line);
					int houseNum = Integer.valueOf(houseStr);
					// 如果可以加入房间
					if (DealWithMsg.canJionHouse(houseNum, currentSk)) {
						DealWithMsg.jionHouse(houseNum, currentSk);
					} else {
						// 观战模式
					}
				}

				else

				// 准备消息
				if (line.startsWith(GAME_PREPARE) && line.endsWith(GAME_PREPARE)) {
					// 获得去掉协议和分隔符的真实消息
					String[] splitMsg = ParseStr.getRealMsg(line);
					DealWithMsg.preGame(splitMsg);
				}

				else

				// 如果收到退出房间消息
				if (line.startsWith(EXIT_HOUSE) && line.endsWith(EXIT_HOUSE)) {
					// 获得去掉协议和分隔符的真实消息
					String[] splitMsg = ParseStr.getRealMsg(line);
					System.out.println(splitMsg[0] + " " + splitMsg[1]);
					DealWithMsg.exitHouse(splitMsg, currentSk);
				}

				else

				if (line.startsWith(CREATE_HOUSE) && line.endsWith(CREATE_HOUSE)) {
					DealWithMsg.creatHouse(currentSk);
				}

			}

		} catch (IOException ioe) {
			ioe.printStackTrace();
			System.out.println("客户端断开:" + line);
			try {
				// 处理用户退出方法
				DealWithMsg.exitHall(currentSk);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
