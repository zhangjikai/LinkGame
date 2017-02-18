package nec.soft.java.share;

import nec.soft.java.ui.DrawArea;
import nec.soft.java.ui.TimePanel;
import nec.soft.java.utils.Constants;

public class SharedVar {

	public static int time = 500;
	public static int sleeptime = 200;
	public static int score = 0;
	public static boolean isPause;
	/** 设置 */
	public static int game_stage = Constants.GAME_EASY;
	public static boolean backgroud_music = true;
	public static boolean effct_music = true;
	public static int game_mode = Constants.MODE_SINGLE;
	
	public static boolean can_draw = true;
	public static boolean isStart = false;
	
	public static TimePanel timePanel = new TimePanel();
	public static DrawArea area = new DrawArea();
	
	
}
