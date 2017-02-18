package nec.soft.java.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.TitledBorder;

import nec.soft.java.share.SharedVar;
import nec.soft.java.utils.Constants;
import nec.soft.java.utils.FileHelper;
import nec.soft.java.utils.ShowHelper;

public class SetDialog extends JDialog implements ActionListener {

	private JButton confirm;
	private JButton cancle;
	private JRadioButton easy;
	private JRadioButton hard;
	private JRadioButton general;
	private JRadioButton bhard1;
	private JRadioButton bhard2;
	private JRadioButton bhard3;
	private JRadioButton bhard4;
	private JRadioButton bhard5;
	private JCheckBox bgMusic;
	private JCheckBox effect;

	private static final long serialVersionUID = -9221834969234945571L;

	public SetDialog() {
		init();
	}

	private void init() {
		getContentPane().setLayout(null);
		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(null, "音乐", TitledBorder.LEADING, TitledBorder.TOP, null,
				null));
		panel.setBounds(10, 10, 356, 50);
		getContentPane().add(panel);
		panel.setLayout(null);

		JLabel bglabel = new JLabel("背景音乐：");
		bglabel.setBounds(46, 21, 60, 15);
		panel.add(bglabel);

		bgMusic = new JCheckBox("开");
		bgMusic.setBounds(112, 17, 60, 23);
		if (SharedVar.backgroud_music)
			bgMusic.setSelected(true);
		panel.add(bgMusic);

		JLabel label = new JLabel("音效");
		label.setBounds(233, 21, 54, 15);
		panel.add(label);

		effect = new JCheckBox("开");
		effect.setBounds(265, 17, 54, 23);
		if (SharedVar.effct_music)
			effect.setSelected(true);
		panel.add(effect);

		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new TitledBorder(null, "困难等级", TitledBorder.LEADING, TitledBorder.TOP,
				null, null));
		panel_1.setBounds(10, 85, 356, 107);
		getContentPane().add(panel_1);
		panel_1.setLayout(null);

		easy = new JRadioButton("简单");
		easy.setBounds(20, 16, 56, 23);
		panel_1.add(easy);

		general = new JRadioButton("一般");
		general.setBounds(78, 16, 56, 23);
		panel_1.add(general);

		hard = new JRadioButton("困难");
		hard.setBounds(142, 16, 56, 23);
		panel_1.add(hard);

		bhard1 = new JRadioButton("高难（左移）");
		bhard1.setBounds(200, 16, 108, 23);
		panel_1.add(bhard1);

		
		
		bhard2 = new JRadioButton("高难（右移）");
		bhard2.setBounds(20, 41, 108, 23);
		panel_1.add(bhard2);
		
		bhard3 = new JRadioButton("高难（上移）");
		bhard3.setBounds(187, 41, 121, 23);
		panel_1.add(bhard3);
		
		bhard4 = new JRadioButton("高难（下移）");
		bhard4.setBounds(20, 66, 121, 23);
		panel_1.add(bhard4);
		
		bhard5 = new JRadioButton("高难（上下移）");
		bhard5.setBounds(187, 66, 121, 23);
		panel_1.add(bhard5);
		ButtonGroup group = new ButtonGroup();
		group.add(easy);
		group.add(general);
		group.add(hard);
		group.add(bhard1);
		group.add(bhard2);
		group.add(bhard3);
		group.add(bhard4);
		group.add(bhard5);
		switch (SharedVar.game_stage) {
		case Constants.GAME_EASY:
			easy.setSelected(true);
			break;
		case Constants.GAME_GENERAL:
			general.setSelected(true);
			break;
		case Constants.GAME_HARD:
			hard.setSelected(true);
			break;
		case Constants.GAME_CHARD1:
			bhard1.setSelected(true);
			break;
		case Constants.GAME_CHARD2:
			bhard2.setSelected(true);
			break;
		case Constants.GAME_CHARD3:
			bhard3.setSelected(true);
			break;
		case Constants.GAME_CHARD4:
			bhard4.setSelected(true);
			break;
		case Constants.GAME_CHARD5:
			bhard5.setSelected(true);
			break;
		default:
			break;
		}

		confirm = new JButton("确定");
		confirm.addActionListener(this);
		confirm.setBounds(116, 202, 93, 23);
		getContentPane().add(confirm);

		cancle = new JButton("取消");
		cancle.addActionListener(this);
		cancle.setBounds(259, 202, 93, 23);
		getContentPane().add(cancle);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == cancle) {
			dispose();
			return;
		}
		if(e.getSource() == confirm) {
			if(bgMusic.isSelected())
				SharedVar.backgroud_music = true;
			else 
				SharedVar.backgroud_music = false;
			if(effect.isSelected()) 
				SharedVar.effct_music = true;
			else
				SharedVar.effct_music = false;
			if(easy.isSelected()) {
				SharedVar.game_stage = Constants.GAME_EASY;
			}
			if(general.isSelected()) {
				SharedVar.game_stage = Constants.GAME_GENERAL;
			}
			if(hard.isSelected()) {
				SharedVar.game_stage = Constants.GAME_HARD;
			}
			if(bhard1.isSelected()) {
				SharedVar.game_stage = Constants.GAME_CHARD1;
			}
			if(bhard2.isSelected()) {
				SharedVar.game_stage = Constants.GAME_CHARD2;
			}
			if(bhard3.isSelected()) {
				SharedVar.game_stage = Constants.GAME_CHARD3;
			}
			if(bhard4.isSelected()) {
				SharedVar.game_stage = Constants.GAME_CHARD4;
			}
			if(bhard5.isSelected()) {
				SharedVar.game_stage = Constants.GAME_CHARD5;
			}
			FileHelper.writeToFile("set.ini", SharedVar.backgroud_music+"<>"+SharedVar.effct_music+"<>" + SharedVar.game_stage, false);
			dispose();
		}
	}

	public static void open() {
		SetDialog dialog = new SetDialog();
		dialog.setTitle("设置");
		dialog.setModal(true);
		dialog.setResizable(false);
		dialog.setSize(400, 270);
		ShowHelper.showCenter(dialog);
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.setVisible(true);
	}
}
