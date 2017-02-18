import java.net.*;
import java.awt.Checkbox;
import java.io.*;
import java.util.*;

import static protocal.MyProtocol.*;

import java.util.*;

import protocal.*;

//处理消息工具类
class DealWithMsg 
{
	public static void printlnAllUser(PrintStream ps)
	{
		//循环发送所有用户信息给客户端
		String msg = "";
		for(UserInfo user : Server.clients.keySet())
		{
			msg  = UPDATE_ALL + user.getName() 
				+ SPLIT_SIGN + user.getSex()
				+ SPLIT_SIGN + user.getScore()
				+ SPLIT_SIGN + user.getIconIndex()
				+ SPLIT_SIGN + user.getHouseNum()
				+ SPLIT_SIGN + user.getSeat()
				+ UPDATE_ALL;
			ps.println(msg);
		}
		
		HouseInfo houseInfo;
		for (int i = 0; i < Server.houseList.size(); i++ )
		{
			houseInfo = Server.houseList.get(i);
			//游戏是否开始,如果是则将房间号发给客户端
			if(houseInfo.getGameState())
			{
				msg = HOUSE_INFO + i + HOUSE_INFO;
				ps.println(msg);
			}
		}
		//用户信息发送完毕后统治客户端停止接收消息
		ps.println(STOP);
	}
	
	//通知其他客户端有人加入大厅,并发送加入者的数据
	public static void joinHall(UserInfo joinUser , Socket joinSk)
		throws Exception
	{
		String msg = null;
		for(Socket socket : Server.clients.valueSet())
		{
			if(socket != joinSk)
			{
				msg  = JOIN_HALL + joinUser.getName() 
						+ SPLIT_SIGN + joinUser.getSex()
						+ SPLIT_SIGN + joinUser.getScore()
						+ SPLIT_SIGN + joinUser.getIconIndex()
						+ SPLIT_SIGN + joinUser.getHouseNum()
						+ SPLIT_SIGN + joinUser.getSeat()
						+ JOIN_HALL;
				prinlnMsg(socket , msg);
			}
		}
	}
	
	//处理私聊消息的方法
	public static void sendPrivate(String[] splits)
		throws Exception
	{
		String name = splits[0];
		String msg = splits[1];
		
		for(UserInfo user : Server.clients.keySet())
		{
			if(user.getName().equals(name))
			{
				Socket socket = Server.clients.get(user);
				prinlnMsg(socket 
					, PRIVATE_ROUND + msg + PRIVATE_ROUND);
				return;
			}
			//ps.println(msg);
			//System.out.println("---"+msg);
		}	
	}
	
	//群聊方法
	public static void sendUserRound(String msg , Socket socket)
		throws Exception
	{
		//获得去掉协议和分隔符的真实消息
		String[] splitMsg = ParseStr.getRealMsg(msg);
		String model = splitMsg[0];

		UserInfo user = Server.clients.getKeyByValue(socket);
		
		if(model.equals(HALL_ROUND))
		{
			for(Socket sk : Server.clients.valueSet())
			{
				prinlnMsg(sk , msg);
			}
		}
		
		if(model.equals(GAME_ROUND))
		{
			HouseInfo houseInfo = Server.houseList.get(user.getHouseNum());
			for(int i = 0 ; i < Server.USER_NUM ; i++)
			{ 
				if(houseInfo.userPrint[i] != null)
				{
					houseInfo.userPrint[i].println(msg);	
				}
			}
		}

	}

	public static void exitHall(Socket exitSk)
		throws Exception
	{
		UserInfo exitUser;
		//根据socket获取对应的用户信息
		exitUser = Server.clients.getKeyByValue(exitSk);
		
		//如果在用户在游戏房间里面
		if(exitUser.getHouseNum() > -1)
		{
			HouseInfo houseInfo = 
				Server.houseList.get(exitUser.getHouseNum());
			//删除该用户在房间里的信息
			houseInfo.removeUser(exitUser.getSeat());
			//如果还在游戏中
			if (houseInfo.getGameState())
			{
				//处理强退
				forceExit(houseInfo , exitUser);
			}
		}

		//删除服务器中此客户端的信息
		Server.clients.remove(exitUser);  
		String msg = EXIT_HALL + exitUser.getName() + EXIT_HALL;;	
		//通知其他客户有人退出
		for(Socket sk : Server.clients.valueSet())
		{
			prinlnMsg(sk , msg);
		}
	}

	//是否可以进入游戏房间
	public static boolean canJionHouse(int houseNum , Socket socket)
		throws Exception
	{
		HouseInfo houseInfo = Server.houseList.get(houseNum);
		UserInfo user = Server.clients.getKeyByValue(socket);
		
		String msg ="";
		//如果该用户有了房号和座号,则不可重复进入别的房间
		if(user.getHouseNum() != -1 ||user.getSeat() != -1)
		{
			return false;
		}
		
		//房间人数未满且游戏未开始才可进入
		if(houseInfo.getCrruntNum() >= Server.USER_NUM ||
			houseInfo.getGameState() == true)
		{
			msg = JOIN_HOUSE_ERROR+JOIN_HOUSE_ERROR;
			prinlnMsg(socket , msg);
		}
		else
		{
			return true;
		}

		return false;
	}
	
	//加入房间
	public static void jionHouse(int houseNum , Socket socket)
		throws Exception
	{
		//获得通过房号获得该房间信息
		HouseInfo houseInfo = Server.houseList.get(houseNum);
		UserInfo user = Server.clients.getKeyByValue(socket);
		//保存当前加入的玩家的输出流
		PrintStream joinPs = null;
		int seat = -1;
		//遍历哪个座位有空,加入到第一个空的座位
		for(int i = 0 ; i < Server.USER_NUM ; i++)
		{
			if(houseInfo.userPrint[i] == null)
			{
				//设置加入用户的房号,座号
				user.setHouseNum(houseNum);
				user.setSeat(i);
				//添加房间的信息
				joinPs = new PrintStream(socket.getOutputStream());
				houseInfo.userPrint[i] = joinPs;
				houseInfo.addUser(i , user.getName() , joinPs);
				seat = i;
				break;
			}
			System.out.println(houseInfo.userState[i]);
		}
		
		/*
		  通知客户端加入房间成功,并告诉客户加入了几房号和座号
		  如果是本用户,则更新本用户,如果是其他用户是同桌，
		  则更新其他用户的同桌信息
		*/
		String msg = JOIN_HOUSE_SUCCESS + user.getName() 
			+ SPLIT_SIGN + houseNum 
			+ SPLIT_SIGN + seat 
			+ JOIN_HOUSE_SUCCESS;
		for(Socket sk : Server.clients.valueSet())
		{
			prinlnMsg(sk , msg);
		}
		
		//将当前房间玩家的信息包装成字符串发送给客户端
		for(int i = 0 ; i < Server.USER_NUM ; i++)
		{
			//i表示座号
			joinPs.println(HOUSE_CHANGE + i 
				+ SPLIT_SIGN + houseInfo.useName[i] 
				+ SPLIT_SIGN + houseInfo.userState[i] 
				+  HOUSE_CHANGE);
		}
	}

	public static void exitHouse(String[] splits, Socket socket)
		throws Exception
	{
		int houseNum = Integer.valueOf(splits[0]);
		int seat = Integer.valueOf(splits[1]);

		if (houseNum == -1 || seat == -1)
		{
			return;
		}
		//通过房号获得该房间信息
		HouseInfo houseInfo = Server.houseList.get(houseNum);
		UserInfo user = Server.clients.getKeyByValue(socket);
		
		//删除该用户在房间里的信息
		houseInfo.removeUser(seat);
		//设置该用户在服务器中保存的房号和座号为空
		user.setHouseNum(-1);
		user.setSeat(-1);

		String msg = null;
		
		//如果还在游戏中
		if (houseInfo.getGameState())
		{
			//处理强退
			forceExit(houseInfo , user);
		}

		msg = EXIT_HOUSE + user.getName() 
			+ SPLIT_SIGN + houseNum 
			+ SPLIT_SIGN + seat 
			+ EXIT_HOUSE;
		//通知所有客户端有用户退出房间
		for(Socket sk : Server.clients.valueSet())
		{
			prinlnMsg(sk , msg);
		}
	}
	
	//发送坐标
	public static void sendCoor(String[] splits , Socket socket)
		throws Exception
	{
		UserInfo user = Server.clients.getKeyByValue(socket);
		HouseInfo houseInfo = Server.houseList.get(user.getHouseNum());
		
		String msg = LINK_COOR + user.getSeat() 
			+ SPLIT_SIGN + splits[0] 
			+ SPLIT_SIGN + splits[1]
			+ SPLIT_SIGN + splits[2]
			+ SPLIT_SIGN + splits[3]
			+ SPLIT_SIGN + splits[4]
			+ LINK_COOR;
		
		String doneStr = splits[4].substring(0 
			, splits[4].lastIndexOf("%")); 
		int done = Integer.valueOf(doneStr);
		//在服务器纪录在该玩家的完成度
		houseInfo.setDone(user.getSeat() , done);
		
		//转发给同桌玩家
		for(int i = 0 ; i < Server.USER_NUM ; i++)
		{ 
			if(houseInfo.userPrint[i] != null
				&& !houseInfo.useName[i].equals(user.getName()))
			{
				houseInfo.userPrint[i].println(msg);
			}
		}
		
		msg = GAME_OVER;
		int index = 0;
		if(done == 100)
		{
			for(int i = 0 ; i < Server.USER_NUM ; i++)
			{ 
				if(houseInfo.userPrint[i] != null)
				{
					
					if(index > 0){msg += SPLIT_SIGN;}
					//获得分数
					int score = assignScore(houseInfo.getDone(i) 
						, houseInfo.getUserNum());
					msg += houseInfo.useName[i] + SPLIT_SIGN + score;
					index ++;
					System.out.println("座位"+i+":完成"+houseInfo.getDone(i));
				}
			}
			msg += GAME_OVER;
			
			if(!msg.equals(GAME_OVER))
			{
				//通知所有客户端游戏结束
				for(Socket sk : Server.clients.valueSet())
				{
					prinlnMsg(sk , msg);
				}
			}
			
			//重置于该房间信息
			houseInfo.reSetInfo();
			//重置于该房间完成度
			houseInfo.reSetDone();
		}
		
	}
	
	//如果收到准备消息
	public static void preGame(String[] splits)
		throws Exception
	{
		int houseNum = Integer.valueOf(splits[0]);
		int seat = Integer.valueOf(splits[1]);
		//通过房号获得该房间信息
		HouseInfo houseInfo = Server.houseList.get(houseNum);
		//设置服务器中该房间该座位的准备状态
		houseInfo.userState[seat] = true;
		
		//通知所有同桌的客户端有人准备
		for(int i = 0 ; i < Server.USER_NUM ; i++)
		{ 
			if(houseInfo.userPrint[i] != null)
			{
				houseInfo.userPrint[i].println(GAME_PREPARE 
					+ seat
					+ GAME_PREPARE);
			}
		}
		
		int preNum = 0;
		//统计准备的人数
		for(int i = 0 ; i < Server.USER_NUM ; i++)
		{
			if(houseInfo.userState[i])
			{
				preNum ++;
			}
		}
		
		//如果房间人数=准备人数,就开始游戏
		if(houseInfo.getUserNum() == preNum && preNum > 1)
		{
			houseInfo.setGameState(true);
			//重置完成度
			houseInfo.reSetDone();
			int[][] nodes = Checker.newGame(14, 8, 12, Mode.classic);
			StringBuilder builder = new StringBuilder();
			for(int i = 0; i < nodes.length; i++) {
				for(int j = 0; j < nodes[0].length; j++) {
					builder.append(nodes[i][j] + SPLIT_SIGN);
				}
			}
			String msg = GAME_START + houseNum+SPLIT_SIGN+builder.toString() + GAME_START;
			//通知客户可以开始
			for(Socket sk : Server.clients.valueSet())
			{
				prinlnMsg(sk , msg);
			}
		}

	}
	
	//处理创建房间方法
	public static void creatHouse(Socket socket)
		throws Exception
	{
		UserInfo user = Server.clients.getKeyByValue(socket);
		
		HouseInfo houseInfo;
		//遍历所有房间是否有空房间
		for (int i = 0; i < Server.houseList.size(); i++ )
		{
			houseInfo = Server.houseList.get(i);
			//如果该房间人数为0
			if (houseInfo.getUserNum() == 0)
			{
				jionHouse(i, socket);
				break;
			}
		}
	}

	private static int assignScore(int done , int userNum)
	{
		if(userNum == 4)
		{
			if (done == 100)
			{
				return 30;
			}
			if (done >= 85 && done < 100)
			{
				return 8;
			}
		}
		if(userNum == 3)
		{
			if (done == 100)
			{
				return 27;
			}
			if (done >= 85 && done < 100)
			{
				return 5;
			}
		}
		if(userNum == 2)
		{
			if (done == 100)
			{
				return 20;
			}
			if (done >= 85 && done < 100)
			{
				return 0;
			}
		}

		if (done >= 60 && done < 85)
		{
			return -10;
		}

		if (done >= 0 && done < 60)
		{
			return -20;
		}
		return 0;
	}

	//处理强退的方法
	private static void forceExit(HouseInfo houseInfo , UserInfo user)
		throws Exception
	{
		//如果该房间只剩下一个人,则游戏结束
		if(houseInfo.getCrruntNum() == 1)
		{
			
			String msg = GAME_OVER + user.getName() 
				+ SPLIT_SIGN + "-50" + GAME_OVER;
			//通知所有客户端有用户中途退出
			for(Socket sk : Server.clients.valueSet())
			{
				prinlnMsg(sk , msg);
			}
			System.out.println("处理强退");
			houseInfo.setGameState(false);
		}
	}

	private static void prinlnMsg(Socket socket , String msg)
		throws Exception
	{
		PrintStream ps = new PrintStream(socket.getOutputStream());
		ps.println(msg);
	}
}
