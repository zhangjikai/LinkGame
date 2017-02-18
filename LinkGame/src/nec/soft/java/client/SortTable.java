package nec.soft.java.client;
import java.util.Arrays;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import nec.soft.java.protocal.UserInfo;



// 排行榜
public class SortTable
{
	//定义一维数据作为列标题
	Object[] columnTitle = {"姓名" , "积分"};
	
	//private ExtendedTableModel model;
	//以二维数组和一维数组来创建一个JTable对象
	JTable table;

	//将原表格里的model包装成新的SortFilterModel对象
	SortableTableModel sorterModel ;
	
	//进行排列的列数
	private int sortColumn =0;
	//sortColumn进行排列的列
	public SortTable(int sortColumn , java.util.List<UserInfo> userInfo)
	{
		this.sortColumn = sortColumn;
		//this.model = model;
		Object[][] data = 
			new Object[userInfo.size()][columnTitle.length];
		for(int i = 0; i < userInfo.size(); i++)
		{
			data[i] = new Object[]{userInfo.get(i).getName()
				, userInfo.get(i).getScore()};
		}
		table = new JTable(data , columnTitle);
		sorterModel = new SortableTableModel(table.getModel());
		table.setModel(sorterModel);
		sorterModel.sort(sortColumn);
	}

	public JTable getTalbe()
	{
		return table;
	}
}

class SortableTableModel extends AbstractTableModel
{
	private TableModel model;
	private int sortColumn;
	private Row[] rows;

	//将一个已经存在TableModel对象包装成SortableTableModel对象
	public SortableTableModel(TableModel m)
	{
		//将被封装的TableModel传入
		model = m;
		rows = new Row[model.getRowCount()];
		//将原TableModel中的每行记录的索引使用Row数组保存起来
		for (int i = 0; i < rows.length; i++)
		{  
			rows[i] = new Row(i);
		}
	}

	//实现根据指定列进行排序
	public void sort(int c)
	{  
		sortColumn = c;
		Arrays.sort(rows);
		fireTableDataChanged();
	}

	//下面三个方法需要访问Model中的数据，所以涉及到本Model中数据
	//和被包装Model数据中的索引转换，程序使用rows数组完成这种转换。
	public Object getValueAt(int r, int c)
	{
		return model.getValueAt(rows[r].index, c);
	}

	public boolean isCellEditable(int r, int c) 
	{ 
		return model.isCellEditable(rows[r].index, c);
	}

	public void setValueAt(Object aValue, int r, int c) 
	{ 
		model.setValueAt(aValue, rows[r].index, c);
	}

	//下面方法的实现把该model的方法委托为原封装的model来实现
	public int getRowCount() 
	{
		return model.getRowCount(); 
	}
	public int getColumnCount()
	{
		return model.getColumnCount();
	}
	public String getColumnName(int c)
	{
		return model.getColumnName(c); 
	}
	public Class getColumnClass(int c) 
	{
		return model.getColumnClass(c);
	}

	//定义一个Row类，该类用于封装JTable中的一行、
	//实际上它并不封装行数据，它只封装行索引
	private class Row implements Comparable<Row>
	{
		//该index保存着被封装Model里每行记录的行索引
		public int index;
		public Row(int index)
		{
			this.index = index;
		}
		//实现两行之间的大小比较
		public int compareTo(Row other)
		{  
			Object a = model.getValueAt(index, sortColumn);
			Object b = model.getValueAt(other.index, sortColumn);
			if (a instanceof Comparable)
			{
				return ((Comparable)a).compareTo(b);
			}
			else
			{
				return a.toString().compareTo(b.toString());
			}
		}
	}
}