package nec.soft.java.frame;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;

import nec.soft.java.share.SharedVar;
import nec.soft.java.ui.ControlPanel;
import nec.soft.java.utils.BackMusic;
import nec.soft.java.utils.DrawHelper;
import nec.soft.java.utils.ShowHelper;

public class SingleGameFrame extends JFrame {

	private static final long serialVersionUID = -4573408413824692170L;
	private static SingleGameFrame frame;
	/*private static TimePanel timePanel = new TimePanel();
	private static DrawArea area = new DrawArea();*/
	private ControlPanel controlPanel;

	public SingleGameFrame() {
		init();
	}

	private void init() {
		/*DrawArea area = new DrawArea(DrawHelper.getNodes());*/
		SharedVar.area.setNodes(DrawHelper.getNodes());
		SharedVar.timePanel.setPreferredSize(new Dimension(100, 40));
		SharedVar.area.setTime(SharedVar.timePanel);
		SharedVar.timePanel.setArea(SharedVar.area);
		controlPanel = new ControlPanel(SharedVar.area, SharedVar.timePanel);
		controlPanel.setPreferredSize(new Dimension(80, 100));
		add(SharedVar.timePanel, BorderLayout.NORTH);
		add(SharedVar.area, BorderLayout.CENTER);
		add(controlPanel, BorderLayout.EAST);
	}

	public static void open() {
		frame = new SingleGameFrame();
		frame.setTitle("欢迎来到秦时明月--连连看");
		frame.setSize(980, 570);
		ShowHelper.showCenter(frame);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		if (!SharedVar.isStart) {
			SharedVar.timePanel.start();
		}
		else 
			SharedVar.timePanel.begin();
			
		if (SharedVar.backgroud_music) {
			BackMusic.getInstance().play();
		}
	}

	public static void close() {
		if (frame != null)
			frame.dispose();
		frame = null;
		BackMusic.getInstance().stop();
	}
}
