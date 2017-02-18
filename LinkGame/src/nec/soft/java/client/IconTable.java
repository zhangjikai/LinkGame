package nec.soft.java.client;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

//表格 （头像、姓名、性别、积分）
public class IconTable extends JTable {
	// 表模式
	private int tableStyle;

	// 表示哪一列是图标,如果没有图标为-1
	private int iconCol = -1;

	// 图标模式,既所有单元格都是图标
	public static final int ALL_CELL_ICON = 1;

	// 常规模式,单元格保存的数据类型
	public static final int GENERAL = 2;

	public IconTable() {
		super();
	}

	// inconCol代表哪一列是图标,tableStyle代表表的模式
	public IconTable(ExtendedTableModel model, int iconCol, int tableStyle) {
		super(model);
		this.tableStyle = tableStyle;
		this.iconCol = iconCol;
		System.out.println("gouzaoqi  IconTable");
		init();
	}

	// 初始化表
	public void init() {
		if (tableStyle == ALL_CELL_ICON) {
			System.out.println("ALL_CELL_ICON");

			// 获取每一列
			TableColumn col;
			for (int i = 0; i < getColumnCount(); i++) {
				col = getColumnModel().getColumn(i);
				// 对该列采用自定义的单元格绘制器
				col.setCellRenderer(new GenderTableCellRenderer());
				System.out.println("ALL_CELL_ICON");
			}
		} else if (tableStyle == GENERAL && iconCol != -1) {
			if (getColumnModel().getColumnCount() == 0) {
				return;
			}
			TableColumn col = getColumnModel().getColumn(iconCol);
			col.setCellRenderer(new GenderTableCellRenderer());
		}
	}

	class GenderTableCellRenderer extends JPanel implements TableCellRenderer {
		private int cellValue;
		// 定义图标的宽度和高度
		final int ICON_WIDTH = 30;
		final int ICON_HEIGHT = 30;

		public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {
			cellValue = (Integer) value;
			table.setRowHeight(50);

			// 设置选中状态下绘制边框
			if (hasFocus) {
				setBorder(UIManager.getBorder("Table.focusCellHighlightBorder"));
			} else {
				setBorder(null);
			}
			return this;
		}

		// 重写paint方法，负责绘制该单元格内容
		public void paint(Graphics g) {
			// 根据单元个值作为索引绘制图标
			if (cellValue >= 1 && cellValue <= 20) {
				drawImage(g, HeadIcon.headIcon[cellValue].getImage());

			}
		}

		// 绘制图标的方法
		private void drawImage(Graphics g, Image image) {
			if (tableStyle == GENERAL) {
				g.drawImage(image, (getWidth() - ICON_WIDTH) / 6, (getHeight() - ICON_HEIGHT) / 6,
						40, 40, null);
			} else {
				g.drawImage(image, (getWidth() - ICON_WIDTH) / 6, (getHeight() - ICON_HEIGHT) / 6,
						null);
			}

		}
	}
}
