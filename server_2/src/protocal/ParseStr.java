package protocal;
import static protocal.MyProtocol.*;

//解析协议工具类
public class ParseStr
{
		//去掉开头和结尾和分隔符协议的方法
	public static String[] getRealMsg(String line)
	{
		String realMsg = line.substring(
			PROTOCOL_LEN , line.length() - PROTOCOL_LEN);
		//通过分隔符分离数据
		String[] splitMsg = realMsg.split(SPLIT_SIGN);
		return splitMsg;
	}

	//只去掉开头和结尾协议的方法
	public static String getCombMsg(String line)
	{
		return line.substring(PROTOCOL_LEN ,
				line.length() - PROTOCOL_LEN);
	}
}
