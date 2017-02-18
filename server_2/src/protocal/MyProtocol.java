package protocal;
//协议接口
public interface MyProtocol
{
	//协议字符串的长度
	int PROTOCOL_LEN = 2;
	//下面是一些协议字符串，服务器和客户端交换的信息
	//都应该在前、后添加这种特殊字符串。
	String MSG_ROUND = "§γ";		//信息
	String USER_ROUND = "∏∑";		//群聊
	String PRIVATE_ROUND = "★【";		//私聊
	String SPLIT_SIGN = "※";		//分割符
	String HALL_ROUND = "々☆";		//在大厅中聊天
	String GAME_ROUND = "⊙￠" ;		//在游戏房间中聊天
	
	String LOGIN = "⊙#";			//登陆
	String LOGIN_SUCCESS = "fs";		//登陆成功
	String LOGIN_ERROR = "&*";		//登陆失败
	String LOGIN_AGAIN = "↑↓";		//重复登陆

	String NAME_REP = "-1";			//姓名重复注册
	String REGISTER = "♀￡";		//注册
	String REGISTER_SUCCESS = "sc";		//注册成功
	String REGISTER_ERROR = "∵℃";		//注册失败
	
	String UPDATE_ALL = "☆☆";		//更新所有用户信息
	String STOP = "△℃";			//停止接收数据
	String JOIN_HALL = "∑∷";		//加入大厅
	String EXIT_HALL = "∩∩";		//退出大厅
	String JOIN_HOUSE = "J*";		//加入房间
	String JOIN_HOUSE_ERROR = "●‰";	//加入房间失败
	String JOIN_HOUSE_SUCCESS = "◎s";	//加入房间成功
	String HOUSE_INFO = "∷¤";		//房间信息
	
	String HOUSE_CHANGE = "￠☆";		//同房间用户信息改变
	String EXIT_HOUSE = "e*";		//退出房
	String GAME_PREPARE = "ZB";		//准备
	String GAME_START = "VK";		//游戏开始
	String LINK_COOR = "∴∴";		//连线坐标
	String GAME_OVER = "‖∑";		//游戏结束
	String CREATE_HOUSE = "±∧";    //创建房间
}


