package nec.soft.java.client;
import java.util.*;
import java.lang.*;

//定义一个连线类
class LinkLine 
{
	//保存连接点
	private int[] points;
	//接收地图
	private int[][] blocks;

	public LinkLine(int[][] blocks)
	{
		this.blocks = blocks;
	}
	
	//判断两个点是否能连接成线段
	public boolean linkSegment(int x1 , int y1 , int x2 , int y2)
	{

		//如果在同一条垂直线上
		if(x1 == x2)	
		{
			if(y1 > y2)
			{
				int temp = y1;
				y1 = y2;
				y2 = temp;
			}
			for(int y = y1 ; y <= y2 ; y++)
			{
				//如果有障碍物
				if(blocks[y][x1] > 0)
				{
					return false;
				}


			}
			return true;
		}
		//如果在同一个水平线上
		else if(y1 == y2)
		{
			if(x1 > x2)
			{
				int temp = x1;
				x1 = x2;
				x2 = temp;
			}
			for(int x = x1 ; x <= x2 ; x++)
			{
				//如果有障碍物
				if(blocks[y1][x] > 0)
				{
					return false;
				}
			}
			return true;
		}

		return false;
	}
	//是否可以连接成符合游戏规则的折线
	public boolean foldLineAble(int x1 , int y1 , int x2 , int y2)
	{
		//每次都清空折点
		points = null;
		int minDistance = 0;

		for(int x = x1 - 1; x >= 0; x--)	//向左历遍
		{
			//如果第一条线段可以连接
			if(linkSegment(x1 , y1 , x , y1)) 
			{	
				//如果剩下两条也可以连接
				if(linkSegment(x , y1 , x , y2) &&
					linkSegment(x , y2 , x2 , y2))
				{	
					//计算最小路程
					minDistance = Math.abs(x1 - x) + 
						Math.abs(y2 - y1) + Math.abs(x2 - x);
					
					//保存折点
					points = new int[]{x1 , y1 , x , y1 , 
						x , y2 , x2 , y2};
					//找到折线,不再往这个方向搜索
					break;
				}
			}
			else
			{	//遇到障碍,不再往这个方向搜索
				break;
			}
		}

		for(int x = x1 + 1; x < blocks[0].length; x++)	//向右历遍
		{
			//如果第一条线段可以连接
			if(linkSegment(x1 , y1 , x , y1)) 
			{	
				//如果剩下两条也可以连接
				if(linkSegment(x , y1 , x , y2) &&
					linkSegment(x , y2 , x2 , y2))
				{	
					//计算最小路程
					int temp = Math.abs(x1 - x) + 
						Math.abs(y2 - y1) + Math.abs(x2 - x);
					
					//如果小于上次一历遍的路程或上一次历遍没有连接
					if(temp < minDistance || minDistance == 0)
					{
						minDistance = temp;
						//保存折点
						points = new int[]{x1 , y1 , x , y1 , 
							x , y2 , x2 , y2};	
					}
					
					//找到折线,不再在这个方向搜索
					break;
				}
			}
			else
			{
				break;
			}
		}

		for(int y = y1 + 1; y < blocks.length; y++)	//向下历遍
		{
			//如果第一条线段可以连接
			if(linkSegment(x1 , y1 , x1 , y)) 
			{	
				//如果剩下两条也可以连接
				if (linkSegment(x1 , y , x2 , y) && 
					linkSegment(x2 , y , x2 , y2))
				{
					//计算最小路程
					int temp = Math.abs(y - y1) + 
						Math.abs(x2 - x1) + Math.abs(y- y2);
					
					if(temp < minDistance || minDistance == 0)
					{
						minDistance = temp;
						//保存折点
						points = new int[]{x1 , y1 , x1 , y , 
							x2 , y , x2 , y2};
					}
					break;
				}
			}
			else
			{
				break;
			}
		}

		for(int y = y1 - 1; y >= 0; y--)	//向上历遍
		{
			//如果第一条线段可以连接
			if(linkSegment(x1 , y1 , x1 , y)) 
			{	
				//如果剩下两条也可以连接
				if (linkSegment(x1 , y , x2 , y) && 
					linkSegment(x2 , y , x2 , y2))
				{
					//计算最小路程
					int temp = Math.abs(y - y1) + 
						Math.abs(x2 - x1) + Math.abs(y- y2);
					
					if(temp < minDistance || minDistance ==0)
					{
						minDistance = temp;
						//保存折点
						points = new int[]{x1 , y1 , x1 , y ,
							x2 , y , x2 , y2};
					}
					break;
				}
			}
			else
			{
				break;
			}
		}

		if(points != null)
		{
			return true;
		}
		
		return false;
	}
	public boolean linkAble(int x1 , int y1 , int x2 , int y2)
	{
		boolean result = false;
		
		//只要有一个障碍物位空即返回false
		if(blocks[y1][x1] == 0
			|| blocks[y2][x2] == 0)
		{
			return false;
		}
		
		//如果是同一个点
		if(x1 == x2 && y1 == y2)
		{
			return false;
		}

		if(blocks[y1][x1] == blocks[y2][x2])
		{	
			/*
				先把要判断连接的两个点在地图的值置0,避免下面的
				检测把它们当成障碍物
			*/
			int temp1 = blocks[y1][x1];
			int temp2 = blocks[y2][x2];
			blocks[y1][x1] = 0;
			blocks[y2][x2] = 0;
			//先判断是否可以连接成线段
			result = linkSegment(x1 , y1 , x2 ,y2);
			
			if(result)
			{	//保存连接点
				points = new int[]{x1 , y1 , x2 ,y2};
			}
			else
			{
				//是否可以连成折线
				result = foldLineAble(x1 , y1 , x2 ,y2);
			}
			blocks[y1][x1] = temp1;
			blocks[y2][x2] = temp2;
		}
			
		return result;
	}

	//获取连接点
	public int[] getPoints()
	{
		return points;
	}
}