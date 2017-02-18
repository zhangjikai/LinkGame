package nec.soft.java.ui;

import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import nec.soft.java.frame.MenuFrame;
import nec.soft.java.frame.SingleGameFrame;
import nec.soft.java.share.SharedVar;
import nec.soft.java.utils.Checker;
import nec.soft.java.utils.DrawHelper;
import nec.soft.java.utils.EffectSound;
import nec.soft.java.utils.ImagesFactory;

public class ControlPanel extends JPanel implements MouseListener, MouseMotionListener {

	private static final long serialVersionUID = -5197690156529655136L;
	private DrawArea area;
	private TimePanel time;
	private Image start;
	private Image refresh;
	private Image reset;
	private Image tip;
	private Image menu;
	private int firstX = 10;
	private int firstY = 50;
	private int space = 80;
	private boolean isRun = true;

	public ControlPanel() {
		initImage();
		addMouseListener(this);
		addMouseMotionListener(this);
	}

	public ControlPanel(DrawArea area, TimePanel time) {
		this();
		this.area = area;
		this.time = time;
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		DrawHelper.drawBackGround(g, this, 68);
		drawMenu(g);
	}

	private void initImage() {
		if (isRun)
			start = ImagesFactory.getImage(43);
		else
			start = ImagesFactory.getImage(41);
		refresh = ImagesFactory.getImage(63);
		tip = ImagesFactory.getImage(45);
		menu = ImagesFactory.getImage(47);
		reset = ImagesFactory.getImage(49);
	}

	private void drawMenu(Graphics g) {
		g.drawImage(start, firstX, firstY, 60, 60, null);
		g.drawImage(refresh, firstX, firstY + space, 60, 60, null);
		g.drawImage(reset, firstX, firstY + space * 2, 60, 60, null);
		g.drawImage(tip, firstX, firstY + space * 3, 60, 60, null);
		g.drawImage(menu, firstX, firstY + space * 4, 60, 60, null);
	}

	@Override
	public void mouseDragged(MouseEvent e) {}

	@Override
	public void mouseMoved(MouseEvent e) {
		switch (getIndex(e)) {
		case 1:
			if (isRun)
				start = ImagesFactory.getImage(42);
			else
				start = ImagesFactory.getImage(40);
			break;
		case 2:
			refresh = ImagesFactory.getImage(64);
			break;
		case 3:
			reset = ImagesFactory.getImage(48);
			break;
		case 4:
			tip = ImagesFactory.getImage(44);
			break;

		case 5:
			menu = ImagesFactory.getImage(46);
			break;
		default:
			initImage();
			break;
		}
		repaint();
	}

	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {
		switch (getIndex(e)) {
		case 1:
			if (isRun) {
				isRun = false;
				time.pause();
				SharedVar.can_draw = false;
				area.repaint();
				start = ImagesFactory.getImage(40);
			} else {
				isRun = true;
				time.begin();
				SharedVar.can_draw = true;
				area.repaint();
				start = ImagesFactory.getImage(42);
			}
			break;
		case 2:
			refresh = ImagesFactory.getImage(64);
			refresh();
			break;
		case 3:
			reset = ImagesFactory.getImage(48);
			SharedVar.can_draw = true;
			restart();
			break;
		case 4:
			tip = ImagesFactory.getImage(44);
			if (SharedVar.can_draw) {
				if (SharedVar.effct_music)
					EffectSound.getAudio(EffectSound.TIP).play();
				showTip();
			}
			break;
		case 5:
			menu = ImagesFactory.getImage(46);
			backMenu();
			break;
		default:
			break;
		}
		repaint();

	}

	@Override
	public void mouseReleased(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {
		this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	}

	@Override
	public void mouseExited(MouseEvent e) {}

	private void showTip() {
		int[] path = Checker.help(area.getNodes());
		if (path == null)
			return;
		area.setPaths(Checker.getPath());
		if (SharedVar.score > 0)
			SharedVar.score -= 5;
		area.setShowPath(true);
		area.clearPath();
		area.repaint();

	}

	private void restart() {
		area.setNodes(DrawHelper.getNodes());
		TimePanel.initTime();
		SharedVar.score = 0;
		isRun = true;
		initImage();
		if (SharedVar.effct_music)
			EffectSound.getAudio(EffectSound.RESTART).play();
		area.repaint();
	}

	private void refresh() {
		if (SharedVar.effct_music)
			EffectSound.getAudio(4).play();
		area.setNodes(Checker.reset(area.getNodes()));
		if (SharedVar.score > 0)
			SharedVar.score -= 5;
		area.repaint();
	}

	private void backMenu() {
		int state = -10;
		if (isRun)
			state = JOptionPane.showConfirmDialog(null, "游戏正在进行,是否返回主菜单?", "确认",
					JOptionPane.OK_CANCEL_OPTION);
		if (state == JOptionPane.OK_OPTION || state == -10) {
			SharedVar.can_draw = true;
			TimePanel.initTime();
			SharedVar.score = 0;
			time.pause();
			SingleGameFrame.close();
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			MenuFrame.open();
		}
	}

	private int getIndex(MouseEvent e) {
		if (e.getX() > firstX && e.getX() < firstX + 60 && e.getY() > firstY
				&& e.getY() < firstY + 60) {
			return 1;
		}
		if (e.getX() > firstX && e.getX() < firstX + 60 && e.getY() > firstY + space
				&& e.getY() < firstY + space + 60) {
			return 2;
		}
		if (e.getX() > firstX && e.getX() < firstX + 60 && e.getY() > firstY + space * 2
				&& e.getY() < firstY + space * 2 + 60) {
			return 3;
		}
		if (e.getX() > firstX && e.getX() < firstX + 60 && e.getY() > firstY + space * 3
				&& e.getY() < firstY + space * 3 + 60) {
			return 4;
		}
		if (e.getX() > firstX && e.getX() < firstX + 60 && e.getY() > firstY + space * 4
				&& e.getY() < firstY + space * 4 + 60) {
			return 5;
		}
		return -1;
	}

}
