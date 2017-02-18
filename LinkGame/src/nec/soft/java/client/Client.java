package nec.soft.java.client;

import java.io.*;
import java.net.*;
import java.util.*;

import nec.soft.java.protocal.*;
import nec.soft.java.share.SharedVar;
import nec.soft.java.utils.Constants;


public class Client {
	public static List<UserInfo> onlineUser = new ArrayList<UserInfo>();
	public static UserInfo crrentUser = new UserInfo();
	public static Socket socket;
	public static PrintStream ps;
	public static BufferedReader brServer;
	public static BufferedReader keyIn;
	// public static String ipStr = "127.0.0.1";
	// 保存同一个房间的信息
	public static ChummeryInfo chummery;

	public static void initScoket(String ipStr) {
		try {
			// 获得键盘输入流
			keyIn = new BufferedReader(new InputStreamReader(System.in));
			if (SharedVar.game_mode == Constants.MODE_TOGETHER)
				socket = new Socket(Login.ipStr, 30001);
			else
				socket = new Socket(Login.ipStr, 30000);
			// 获取socket对应的输入和输出流
			brServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			ps = new PrintStream(socket.getOutputStream());

			// 注册登陆
		} catch (UnknownHostException ex) {
			System.out.println("找不到远程服务器,请确定服务器已经启动.");
			System.out.println("注册或登录之前请在登录窗口输入服务器IP地址.");
			closeRs();
			System.exit(1);
		} catch (IOException ex) {
			System.out.println("网络异常！请重新登陆!");
			System.out.println("请确定服务器已经打开!");
			System.out.println("注册或登录之前请在登录窗口输入服务器IP地址.");
			closeRs();
			System.exit(1);
		}
	}

	public static void closeRs() {
		try {
			if (keyIn != null) {
				keyIn.close();
			}
			if (brServer != null) {
				brServer.close();
			}
			if (ps != null) {
				ps.close();
			}
			if (socket != null) {
				keyIn.close();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
