package com.se.parametric;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;


public class Loading 
{

	JProgressBar pbar;
	static int height;
	static int width;
	int percent = 0;
	public static JFrame frame;
	ImagePanel panel = new ImagePanel("Resources/loading1.gif");
	public static void show()
	{
		ImageIcon icon=new ImageIcon("Resources/loading1.gif");
		ImagePanel panel =new ImagePanel(icon.getImage());
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		width = screenSize.width;
		height = screenSize.height;
		System.out.println(width + " and " + height);
		frame = new JFrame();
		frame.add(panel);
		frame.setUndecorated(true);
		frame.pack();
		frame.setAlwaysOnTop(true);
		frame.setSize(350,280);
		frame.setBounds((width - 350) / 2, (height - 280) / 2, 350, 280);
		frame.setVisible(true);
		
	
	}
	public static void close(){
		frame.hide();
		frame.dispose();
	}

	public void updateBar(int newValue)
	{
		pbar.setValue(newValue);
	}

	static class ImagePanel extends JPanel
	{

		private Image img;

		public ImagePanel(String img)
		{
			this(new ImageIcon(img).getImage());
		}

		public ImagePanel(Image img)
		{
			this.img = img;
			Dimension size = new Dimension(img.getWidth(null), img.getHeight(null));
			setPreferredSize(size);
			setMinimumSize(size);
			setMaximumSize(size);
			setSize(size);
			setLayout(null);
		}

		public void paintComponent(Graphics g)
		{
			g.drawImage(img, 0, 0, null);
			repaint();
		}
	}
	public static void main(String args[]){
		Loading loading = new Loading();		
		loading.show();
	}


}