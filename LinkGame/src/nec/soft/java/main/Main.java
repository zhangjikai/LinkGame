package nec.soft.java.main;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import nec.soft.java.frame.MenuFrame;
import nec.soft.java.share.SharedVar;
import nec.soft.java.utils.Constants;
import nec.soft.java.utils.FileHelper;

import org.jvnet.substance.skin.SubstanceOfficeBlue2007LookAndFeel;

public class Main {
	public static void main(String[] args) {
		JFrame.setDefaultLookAndFeelDecorated(true);
		JDialog.setDefaultLookAndFeelDecorated(true);
		try {
			UIManager.setLookAndFeel(new SubstanceOfficeBlue2007LookAndFeel());
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		initSetting();
		MenuFrame.open();
	}

	public static void initSetting() {
		try {

			String set = FileHelper.readFromFile("set.ini");
			if (set == null || set.equals("")) {
				SharedVar.backgroud_music = true;
				SharedVar.effct_music = true;
				SharedVar.game_stage = Constants.GAME_EASY;
			} else {
				String[] sets = set.split("<>");
				SharedVar.backgroud_music = Boolean.parseBoolean(sets[0]);
				SharedVar.effct_music = Boolean.parseBoolean(sets[1]);
				SharedVar.game_stage = Integer.parseInt(sets[2]);
			}
			String mode = FileHelper.readFromFile("mode.ini");
			if (mode == null || mode.equals("")) {
				SharedVar.game_mode = Constants.MODE_SINGLE;
			} else {
				SharedVar.game_mode = Integer.parseInt(mode);
			}

		} catch (Exception e) {
			SharedVar.backgroud_music = true;
			SharedVar.effct_music = true;
			SharedVar.game_stage = Constants.GAME_EASY;
			SharedVar.game_mode = Constants.MODE_SINGLE;
		}

	}
}
