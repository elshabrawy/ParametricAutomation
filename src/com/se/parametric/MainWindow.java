package com.se.parametric;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.se.parametric.dto.GrmUserDTO;

public class MainWindow extends JFrame
{

	private JPanel contentPane;
	JPanel panel, panel2;
	int width, height;
	Container container;
	MainPanel p;

	/**
	 * Create the frame.
	 */
	public MainWindow()
	{
		container = getContentPane();
		setTitle("Parametric Automation");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		width = screenSize.width;
		height = screenSize.height - 30;

		System.out.println((width - 350) / 2 + " and " + (height - 150) / 2);
		setBounds(0, 0, width, height);

		contentPane = new JPanel();
		contentPane.setLayout(null);
		container.add(contentPane);
//		com.jtattoo.plaf.mint.MintLookAndFeel.setTheme("Default");
//		try
//		{
//			UIManager.setLookAndFeel("com.jtattoo.plaf.texture.TextureLookAndFeel");
//		}catch(ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e)
//		{
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		try
//		{
//			// Set cross-platform Java L&F (also called "Metal")
//			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//		}catch(UnsupportedLookAndFeelException e)
//		{
//
//		}catch(ClassNotFoundException e)
//		{
//
//		}catch(InstantiationException e)
//		{
//
//		}catch(IllegalAccessException e)
//		{
//
//		}

	}

	public void init(GrmUserDTO grmUser)
	{
		contentPane.removeAll();
		contentPane.getWidth();
		contentPane.getHeight();
		p = new MainPanel(grmUser, width, height);
		System.out.println("Main Frame Dimession " + width + " " + height);
		p.repaint();
		System.out.println("Main Panel Dimession " + p.getWidth() + " " + p.getHeight());
		contentPane.add(p);
		contentPane.revalidate();
		contentPane.repaint();
		// while(true)
		// {
		// updateFlags();
		// try
		// {
		// Thread.sleep(50000);
		// }catch(InterruptedException e)
		// {
		// e.printStackTrace();
		// }
		//
		// }
	}

	public long getUserID(String userName, String pass)
	{

		return 1l;
	}

	public void updateFlags()
	{

		p.updateFlags();
	}
}
