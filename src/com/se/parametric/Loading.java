package com.se.parametric;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

public class Loading implements Runnable {
	public Loading() {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.width = screenSize.width;
		this.height = screenSize.height;
		System.out.println(this.width + " and " + this.height);
	}

	ImagePanel panel = new ImagePanel(new ImageIcon("Resources/loading.gif").getImage());
	JProgressBar pbar;
	int height;
	int width;
	int percent = 0;
	public JFrame frame;

	public void run() {
		frame = new JFrame();
		frame.setContentPane(panel);
		frame.setTitle("Progress Bar Example");
		frame.setUndecorated(true);
		frame.pack();
		frame.setBounds((width - 220) / 2, (height - 30) / 2, 220, 30);
		frame.setVisible(true);
	}

	public void updateBar(int newValue) {
		pbar.setValue(newValue);
	}

	class ImagePanel extends JPanel {

		private Image img;

		public ImagePanel(String img) {
			this(new ImageIcon(img).getImage());
		}

		public ImagePanel(Image img) {
			this.img = img;
			Dimension size = new Dimension(img.getWidth(null), img.getHeight(null));
			setPreferredSize(size);
			setMinimumSize(size);
			setMaximumSize(size);
			setSize(size);
			setLayout(null);
		}

		public void paintComponent(Graphics g) {
			g.drawImage(img, 0, 0, null);
			repaint();
		}
	}
}