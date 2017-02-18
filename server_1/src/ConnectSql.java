import java.sql.*;
import java.util.*;
import java.io.*;

import static protocal.MyProtocol.*;

public class ConnectSql
{
	private Connection conn;
	private Statement stmt;
	private PreparedStatement pstmt;
	private ResultSet rs;
	private String drivers;
	private String url;
	private String username;
	private String password;

	public ConnectSql()
	{
		try
		{
			Properties props = new Properties();
			FileInputStream in = new FileInputStream("mysql.ini");
			props.load(in);
			in.close();
			drivers = props.getProperty("driver");
			url = props.getProperty("url");
			username = props.getProperty("user");
			password = props.getProperty("pass");
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	//获得连接
	public void getConnetion()	
	{
		try
		{
			//加载数据驱动
			Class.forName(drivers);
			//取得数据库连接
			conn = DriverManager.getConnection(url , username , password);
			stmt = conn.createStatement();
			pstmt = conn.prepareStatement(
				"insert into user_table values(null,?,?,?,?,?)");
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	//关闭连接
	public void close()
	{
		try
		{
			if(rs != null)
			{
				rs.close();
			}
			if(stmt != null)
			{
				stmt.close();
			}
			if(conn != null)
			{
				conn.close();
			}
			if(pstmt != null)
			{
				pstmt.close();
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	//注册方法
	public String register(String name , String passwd 
		, String sex , int iconIndex)
	{
		getConnetion();
		try
		{
			String sql = "select * from user_table "
				+ "where user_name='" + name + "'";
			boolean result = stmt.executeQuery(sql).next();
			//System.out.println(result);
			//如果有相同的用户名
			if(result)
			{
				close();
				return NAME_REP;
			}
			pstmt.setString(1 , name);
			pstmt.setString(2 , passwd);
			pstmt.setBytes(3 , sex.getBytes());
			pstmt.setInt(4 , 100);
			pstmt.setInt(5 , iconIndex);
			
			if(pstmt.executeUpdate() > 0)
			{
				close();
				return REGISTER_SUCCESS;
			}
			
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			close();
		}

		return REGISTER_ERROR;
	}
	
	//登录方法
	public String login(String name , String passwd)
	{
		getConnetion();
		try
		{
			String sql = "select * from user_table "
				+ "where user_name='" + name + "'" 
				+ " and password='" + passwd + "'";
			rs = stmt.executeQuery(sql);
			//如果有相同的用户名和密码
			if(rs.next())
			{
				rs.last();
				int rowCount = rs.getRow();
				rs.absolute(rowCount);
				String result = LOGIN_SUCCESS + rs.getString(2) 
						+ SPLIT_SIGN
						+ new String(rs.getBytes(4))
						+ SPLIT_SIGN
						+ rs.getInt(5)
						+ SPLIT_SIGN
						+ rs.getInt(6)
						+ LOGIN_SUCCESS;
				System.out.println(new String(rs.getBytes(4)));
				close();
				return result;
			}
			
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			close();
		}
		return LOGIN_ERROR;

	}
	/*public static void main(String[] args) 
	{
		ConnectSql conSql = new ConnectSql();
		conSql.getConnetion();
		System.out.println("Hello World!");
	}*/
}
