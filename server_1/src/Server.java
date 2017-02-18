import java.net.*;
import java.io.*;
import java.util.*;

import protocal.*;
import static protocal.MyProtocol.*;

public class Server {
	// 固定端口
	public static final int SERVER_PORT = 30001;
	// 保存在线客户信息和对应Socket
	public static UsMap<UserInfo, Socket> clients = new UsMap<UserInfo, Socket>();
	public static ConnectSql connSql = new ConnectSql();
	// 默认房间数为18,所有房间满了才可增加房间
	public static int houseNum = 18;
	// 房间最多有几个玩家
	public static final int USER_NUM = 4;
	public static List<HouseInfo> houseList = new LinkedList<HouseInfo>();

	public void init() {
		// 初始化房间信息,0到17对应房间的号码
		for (int i = 0; i < houseNum; i++) {
			houseList.add(new HouseInfo());
		}

		ServerSocket ss = null;
		try {
			ss = new ServerSocket(SERVER_PORT);
			// 不断获得客户端得请求
			while (true) {
				Socket currentSk = ss.accept();
				new ServerThread(currentSk).start();
			}
		} catch (IOException ex) {
			System.out.println("服务器启动失败，是否端口" + SERVER_PORT + "已被占用");
		} finally {
			try {
				if (ss != null) {
					ss.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			System.exit(1);
		}
	}
}
