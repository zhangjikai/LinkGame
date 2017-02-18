package com.zhangjikai.linkgame.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.TitledBorder;

import com.zhangjikai.linkgame.share.SharedVar;
import com.zhangjikai.linkgame.utils.ShowHelper;
import com.zhangjikai.linkgame.utils.Constants;
import com.zhangjikai.linkgame.utils.FileHelper;

public class ModeDialog extends JDialog implements ActionListener{

	private static final long serialVersionUID = -7709716436147351701L;

	private JRadioButton single;
	private JRadioButton fight;
	private JRadioButton together;
	private JButton confirm;
	private JButton cancel;
	public ModeDialog() {
		init();
	}
	private void init() {
		getContentPane().setLayout(null);

		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(null, "游戏模式", TitledBorder.LEADING, TitledBorder.TOP,
				null, null));
		panel.setBounds(23, 25, 383, 59);
		getContentPane().add(panel);
		panel.setLayout(null);

		single = new JRadioButton("单机");
		single.setBounds(40, 18, 68, 23);
		panel.add(single);

		/*fight = new JRadioButton("对战");
		fight.setBounds(149, 18, 78, 23);
		panel.add(fight);

		together = new JRadioButton("合作");
		together.setBounds(243, 18, 78, 23);
		panel.add(together);*/
		
		ButtonGroup group = new ButtonGroup();
		group.add(single);
		group.add(fight);
		group.add(together);
		switch (SharedVar.game_mode) {
		case Constants.MODE_SINGLE:
			single.setSelected(true);
			break;
		case Constants.MODE_FIGHT:
			fight.setSelected(true);
			break;
		case Constants.MODE_TOGETHER:
			together.setSelected(true);
			break;
		default:
			break;
		}

		confirm = new JButton("确定");
		confirm.setBounds(157, 104, 93, 23);
		confirm.addActionListener(this);
		getContentPane().add(confirm);

		cancel = new JButton("取消");
		cancel.setBounds(295, 104, 93, 23);
		cancel.addActionListener(this);
		getContentPane().add(cancel);
	}
	
	public static void open() {
		ModeDialog dialog = new ModeDialog();
		dialog.setTitle("设置");
		dialog.setModal(true);
		dialog.setResizable(false);
		dialog.setSize(430, 186);
		ShowHelper.showCenter(dialog);
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == cancel) 
			dispose();
		if(e.getSource() == confirm) {
			if(single.isSelected()) {
				SharedVar.game_mode = Constants.MODE_SINGLE;
			}
			if(fight.isSelected()) {
				SharedVar.game_mode = Constants.MODE_FIGHT;
			}
			if(together.isSelected()) {
				SharedVar.game_mode = Constants.MODE_TOGETHER;
			}
			FileHelper.writeToFile("mode.ini", SharedVar.game_mode+"", false);
			dispose();
		}
	}

}
