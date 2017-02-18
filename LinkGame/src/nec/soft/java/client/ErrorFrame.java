package nec.soft.java.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;

public class ErrorFrame {

	/**
	 * @param args
	 */
	
	static void showError(String str){

		final JFrame frame = new JFrame();
		frame.setUndecorated(true);
		Border b = new CompoundBorder(new EtchedBorder(), new LineBorder(
				Color.green));
		final ImagePane panel = new ImagePane("image/ErrorBack.jpg");
		panel.setLayout(new BorderLayout());
		JLabel jl=new JLabel(str,JLabel.CENTER);
		jl.setFont(new Font("SansSerif", Font.BOLD, 20));
		jl.setOpaque(false);
		JPanel jp=new JPanel(new FlowLayout(FlowLayout.CENTER));
		JButton jb=myButton("image/yes.png");
		jb.setOpaque(false);
		jp.setOpaque(false);
		panel.add(jl,BorderLayout.CENTER);
		jp.add(jb);
		panel.add(jp,BorderLayout.SOUTH);
		
		//panel.setSize(300, 100);
		panel.setBorder(b);
		
		jb.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				frame.dispose();
			}
			
		});
		
		panel.addMouseMotionListener(new MouseAdapter() {
			private boolean top = false;
			private boolean down = false;
			private boolean left = false;
			private boolean right = false;
			private boolean drag = false;
			private Point lastPoint = null;
			private Point draggingAnchor = null;

			@Override
			public void mouseMoved(MouseEvent e) {
				if (e.getPoint().getY() == 0) {
					frame.setCursor(Cursor
							.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
					top = true;
				} else if (Math.abs(e.getPoint().getY()
						- frame.getSize().getHeight()) <= 1) {
					frame.setCursor(Cursor
							.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
					down = true;
				} else if (e.getPoint().getX() == 0) {
					frame.setCursor(Cursor
							.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
					left = true;
				} else if (Math.abs(e.getPoint().getX()
						- frame.getSize().getWidth()) <= 1) {
					frame.setCursor(Cursor
							.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
					right = true;
				} else {
					frame.setCursor(Cursor
							.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					draggingAnchor = new Point(e.getX() + panel.getX(), e
							.getY() + panel.getY());
					top = false;
					down = false;
					left = false;
					right = false;
					drag = true;
				}

			}

			@Override
			public void mouseDragged(MouseEvent e) {
				Dimension dimension = frame.getSize();
				if (top) {

					dimension.setSize(dimension.getWidth(),
							dimension.getHeight() - e.getY());
					frame.setSize(dimension);
					frame.setLocation(frame.getLocationOnScreen().x,
							frame.getLocationOnScreen().y + e.getY());
				} else if (down) {
 
					dimension.setSize(dimension.getWidth(), e.getY());
					frame.setSize(dimension);

				} else if (left) {

					dimension.setSize(dimension.getWidth() - e.getX(),
							dimension.getHeight());
					frame.setSize(dimension);

					frame.setLocation(frame.getLocationOnScreen().x + e.getX(),
							frame.getLocationOnScreen().y);

				} else if (right) {

					dimension.setSize(e.getX(), dimension.getHeight());
					frame.setSize(dimension);
				} else {
					frame.setLocation(e.getLocationOnScreen().x
							- draggingAnchor.x, e.getLocationOnScreen().y
							- draggingAnchor.y);
				}
			}
		});

		
		
		Dimension scrSize=Toolkit.getDefaultToolkit().getScreenSize();
		frame.setLocation(scrSize.width/2-200, scrSize.height/2-75);
		frame.getContentPane().add(panel);
		frame.setSize(400, 150);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);

	}
	
	static JButton myButton(String url) { // 获取包含图片的自定义按钮
		ImageIcon ii = new ImageIcon(url);
		JButton jb = new JButton(ii);
		jb.setOpaque(false);
		jb.setContentAreaFilled(false);
		jb.setMargin(new Insets(0, 0, 0, 0));
		jb.setFocusPainted(false);
		// btn1.setBorderPainted(false);
		// btn1.setBorder(null);
		return jb;
	}
	/*public static void main(String[] args){
		showError("对不起，该房间已开始游戏");
	}*/
}