package nec.soft.java.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import nec.soft.java.utils.FileHelper;

public class Ranking {

	private static Ranking rank;

	public static Ranking getInstance() {
		if (rank == null)
			rank = new Ranking();
		return rank;
	}

	private Ranking() {}

	public void store(int dif, int score, String date) {
		FileHelper.writeToFile("ranking.ini", dif + "," + score + "," + date + ",");
	}
	public void store(int dif, int score) {
		store(dif, score, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
	}

	public void clear() {
		FileHelper.writeToFile("ranking.ini", "", false);
	}

	public void show() {
		Rank rank = new Rank();
		rank.show(1);
	}

	class Rank extends JDialog {

		private static final long serialVersionUID = -4178667638480189541L;

		private JTable table;
		private Object[] title = { "名次", "得分", "日期" };
		private final int COL = 3;
		private Object[][] rank;
		private int which;

		private JPanel topPanel;
		private JPanel centerPanel;
		private JPanel bottomPanel;
		private JLabel titleLabel;
		private JButton[] gotoBtn = new JButton[5];

		public Rank() {
			setModal(true);
			setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			setTitle("连连看排行榜");
			Toolkit kit = Toolkit.getDefaultToolkit();
			Dimension scrSize = kit.getScreenSize();
			setSize(scrSize.width / 2, scrSize.height / 2);
			setLocation(scrSize.width / 4, scrSize.height / 4);
			setResizable(false);

			gotoBtn[0] = new JButton("查看简单模式");
			gotoBtn[1] = new JButton("查看中等模式");
			gotoBtn[2] = new JButton("查看困难模式");
			gotoBtn[3] = new JButton("查看疯狂模式");
			gotoBtn[4] = new JButton("清空排行榜");

			gotoBtn[0].addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					which = 1;
					btnCtrl(which - 1);
					show(1);
				}
			});
			gotoBtn[1].addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					which = 2;
					btnCtrl(which - 1);
					show(2);
				}
			});
			gotoBtn[2].addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					which = 3;
					btnCtrl(which - 1);
					show(3);
				}
			});
			gotoBtn[3].addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					which = 4;
					btnCtrl(which - 1);
					show(4);
				}
			});
			gotoBtn[4].addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					int b = JOptionPane.showConfirmDialog(null, "您确定要清空排行榜吗？这将无法撤销！");
					if (b == JOptionPane.YES_OPTION) {
						clear();
						show(which);
					}
				}
			});

			topPanel = new JPanel();
			centerPanel = new JPanel();
			bottomPanel = new JPanel();
			titleLabel = new JLabel("排行榜");
			titleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			titleLabel.setFont(new Font("华文行楷", 0, 32));
			topPanel.add(titleLabel);
			for (int i = 0; i < gotoBtn.length; i++) {
				bottomPanel.add(gotoBtn[i]);
			}

			setLayout(new BorderLayout());
			add(topPanel, BorderLayout.NORTH);
			add(centerPanel, BorderLayout.CENTER);
			add(bottomPanel, BorderLayout.SOUTH);
		}

		public void btnCtrl(int des) {
			for (int i = 0; i < gotoBtn.length; i++) {
				if (i != des)
					gotoBtn[i].setEnabled(true);
				else
					gotoBtn[i].setEnabled(false);
			}
		}

		public void show(int dif) {

			ArrayList<record> rec = new ArrayList<record>();

			String content = FileHelper.readFromFile("ranking.ini");
			if (content != null ) {
				if(!content.equals("")) {
					String[] sp = content.split(",");
					for (int i = 0; i < sp.length; i = i + 3) {
						if (dif == Integer.parseInt(sp[i])) {
							rec.add(new record(sp[i + 2], Integer.parseInt(sp[i + 1])));
						}
					}
				}
				Collections.sort(rec);
				rank = new Object[rec.size()][COL];
				for (int i = 0; i < rec.size(); i++) {
					record temp = rec.get(i);
					rank[i][0] = i + 1;
					rank[i][2] = temp.date;
					rank[i][1] = temp.score;
				}

				table = new JTable(rank, title);
				centerPanel.removeAll();
				centerPanel.setLayout(new BorderLayout());
				centerPanel.add(new JScrollPane(table), BorderLayout.CENTER);
				DefaultTableCellRenderer r = new DefaultTableCellRenderer();
				r.setHorizontalAlignment(JLabel.CENTER);
				table.setDefaultRenderer(Object.class, r);
				table.setRowHeight(20);
				table.setFont(new Font("微软雅黑", 0, 14));
				table.setGridColor(Color.BLUE);
				table.setSize(centerPanel.getWidth(), centerPanel.getHeight());
				table.setRowSelectionAllowed(false);

			}
			setVisible(true);
		}

		class record implements Comparable<record> {
			private String date;
			private int score;

			public record(String date, int score) {
				this.date = date;
				this.score = score;
			}

			public int compareTo(record o) {
				if (this.score < o.score) {
					return 1;
				} else if (this.score > o.score) {
					return -1;
				} else {
					return this.date.compareTo(o.date);
				}
			}
		}

	}
}
