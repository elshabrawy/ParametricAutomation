package com.se.parametric;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;


public class Loading 
{
	public Loading(){}

	JProgressBar pbar;
	static int height;
	static int width;
	int percent = 0;
	public  JFrame frame;
	 JPanel glass = new JPanel(new GridLayout(0, 1));
	 ImagePanel panel ;
//	ImagePanel panel = new ImagePanel("Resources/loading.gif");
	public  void show()
	{		
		glass.setOpaque(false);	   
		ImageIcon icon=new ImageIcon("Resources/loading2.gif");
		panel=new ImagePanel(icon.getImage());;
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		width = screenSize.width;
		height = screenSize.height;
		System.out.println(width + " and " + height);
		frame = new JFrame();
		JButton b=new JButton("Run");
		b.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e)
			{
				System.out.println("here");
				glass.add(panel);
				glass.setVisible(true);
				
			}
		});
		glass.add(b);
		frame.add(glass);
//		frame.add(b);
		
		frame.setUndecorated(false);
		frame.pack();
		frame.setAlwaysOnTop(true);
		frame.setSize(350,280);
		frame.setBounds((width - 350) / 2, (height - 280) / 2, 350, 280);
		frame.setVisible(true);
		
	
	}
	public  void close(){
		frame.hide();
		frame.dispose();
	}

	public void updateBar(int newValue)
	{
		pbar.setValue(newValue);
	}

	 class ImagePanel extends JPanel
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