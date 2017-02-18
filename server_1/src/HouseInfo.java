import java.io.*;
class HouseInfo
{
	//当前房间人数
	private int crruntNum = 0;
	//玩家姓名
	public String[] useName = new String[Server.USER_NUM];
	//玩家状态 准备(true)/不准备(false)
	public boolean[] userState = new boolean[Server.USER_NUM];
	//保存玩家对应的输出流
	public PrintStream [] userPrint = new PrintStream[Server.USER_NUM];
	//保存玩家的完成度
	private int[] done = new int[Server.USER_NUM];
	
	//游戏状态
	private boolean playing = false;
	
	public int getCrruntNum()
	{
		return crruntNum;
	}
	
	//增加用户
	public void addUser(int seat , String name , PrintStream ps)
	{
		useName[seat] = name;
		userPrint[seat] = ps;
		if(crruntNum < 4)
		{
			crruntNum ++;
		}
	}
	
	//删除用户
	public void removeUser(int seat)
	{
		useName[seat] = null;
		userPrint[seat] = null;
		userState[seat] = false;
		done[seat] = 0;
		if(crruntNum > 0 )
		{
			crruntNum --;
		}
	}

	public boolean getGameState()
	{
		return playing;
	}
	
	public void setGameState(boolean state)
	{
		playing = state;
	}

	public int getUserNum()
	{
		return crruntNum;
	}

	public void setDone(int seat , int done)
	{
		this.done[seat] = done;
	}

	public int getDone(int seat)
	{
		return done[seat];
	}
	
	//重置房间信息
	public void reSetInfo()
	{
		for(int i = 0 ;i < Server.USER_NUM ; i++)
		{
			userState[i] = false;
		}
		playing = false;
	}
	
	//重置完成度
	public void reSetDone()
	{
		for(int i = 0 ;i < Server.USER_NUM ; i++)
		{
			done[i] = 0;
		}
	}
	
}
