package nec.soft.java.client;

import javax.swing.table.*;

import nec.soft.java.protocal.*;

import java.util.*;

class ExtendedTableModel extends DefaultTableModel {
	// 定义姓名和行的对应关系
	private ArrayList<String> rowIndex = new ArrayList<String>();
	private final int SCORE_COL = 3;

	public ExtendedTableModel() {
		super();
	}

	// 重写isCellEditable方法，使用户无法更改表格数据
	public boolean isCellEditable(int r, int c) {
		return false;
	}

	// 重新提供一个构造器，该构造器的实现委托给DefaultTableModel父类
	public ExtendedTableModel(String[] columnNames, Object[][] cells) {
		super(cells, columnNames);
	}

	public ExtendedTableModel(Object[] columnNames, List<UserInfo> userInfo) {
		super();
		reSetTable(columnNames, userInfo);

	}

	// 重置Table
	public void reSetTable(Object[] columnNames, List<UserInfo> userInfo) {
		rowIndex.clear();
		Object[][] cells = new Object[userInfo.size()][columnNames.length];
		System.out.println(userInfo.size() + " " + columnNames.length);
		for (int i = 0; i < userInfo.size(); i++) {
			cells[i] = new Object[] { userInfo.get(i).getIconIndex(),
					userInfo.get(i).getName(), userInfo.get(i).getSex(),
					userInfo.get(i).getScore() };
			rowIndex.add(userInfo.get(i).getName());
			// addRow(cells[i]);
		}
		setDataVector(cells, columnNames);
		setColumnIdentifiers(columnNames);
	}

	// 添加行方法
	public void addRow(UserInfo user) {
		Object[] rowData = new Object[] { user.getIconIndex(), user.getName(),
				user.getSex(), user.getScore() };
		addRow(rowData);
		rowIndex.add(user.getName());
	}

	// 删除行方法,根据用户姓名来删除行
	public void removeRow(String name) {
		int row = rowIndex.indexOf(name);
		this.removeRow(row);
		rowIndex.remove(row);
	}

	// 重写getColumnClass方法，根据每列的第一个值来返回其真实的数据类型
	public Class getColumnClass(int c) {
		return getValueAt(0, c).getClass();
	}

	public void updateScore(int socre, String name) {
		int row = rowIndex.indexOf(name);
		setValueAt(socre, row, SCORE_COL);
	}
}
