package com.se.parametric.util;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.se.grm.client.mapping.GrmGroup;
import com.se.grm.client.mapping.GrmRole;
import com.se.parametric.dto.GrmUserDTO;
import com.se.parametric.unappValue.TLUnApprovedValue;
import com.sun.star.io.IOException;

public class ImagePanel extends JPanel
{

	private Image img;
	ImageObserver imageObserver;

//	public ImagePanel(String img)
//	{
//		this(new ImageIcon(img).getImage());
//	}
//
//	public ImagePanel(Image img)
//	{
//		this.img = img;
//		// Dimension size = new Dimension(img.getWidth(null), img.getHeight(null));
//		// setPreferredSize(size);
//		// setMinimumSize(size);
//		// setMaximumSize(size);
//		// setSize(size);
//		setLayout(new BorderLayout());
//	}

//	public ImagePanel(String imagePath)
//	{
//		 setSize(1000, 1000);
//		 setVisible(true);
//		 BorderLayout one = new BorderLayout();
//		setLayout(one);
//		JLabel pic = new JLabel();
//		ImageIcon icon = new ImageIcon(imagePath);
//		pic.setIcon(icon);
//		pic.setHorizontalAlignment(JLabel.CENTER);
//		JScrollPane sp = new JScrollPane();
//		img = icon.getImage();
//		imageObserver = icon.getImageObserver();
//		pic.setBounds(0, 0, getWidth(), img.getHeight(imageObserver));
////		pic.setLayout(null);
//		// sp.add(pic);
//		add(pic);
//		// repaint();
//		// revalidate();
//
//	}

	public ImagePanel(String imagePath)
	{
		BufferedImage image=null;
	              
	         try
			{
				image = ImageIO.read(new File(imagePath));
			}catch(java.io.IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        JLabel b=new JLabel(new ImageIcon(image));
//	        JPanel p=new JPanel();
	       setLayout( new BorderLayout() );
	        JScrollPane sp=new JScrollPane();
	        sp.setAutoscrolls(true);
	        sp.getViewport().add(b);
	       add(sp, BorderLayout.CENTER);
	}
//	public void paintComponent(Graphics g)
//	{
//		g.drawImage(img, 0, 0, imageObserver);
//
//	}

	public static void main(String[] args)
	{
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		int width = Toolkit.getDefaultToolkit().getScreenSize().width;
		int height = Toolkit.getDefaultToolkit().getScreenSize().height;
		frame.setSize(width, height);
		frame.setTitle("Image");
		ImagePanel devPanel = new ImagePanel("Development-chart.jpg");
		frame.getContentPane().add(devPanel);
		frame.show();

	}
}