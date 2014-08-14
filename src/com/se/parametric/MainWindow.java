package com.se.parametric;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
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
	public static JPanel glass;
	public MainWindow()
	{
	 glass = new JPanel() {
			public void paintComponent(Graphics g)

			{
				g.setColor(new Color(0, 0, 0, 140));
				g.fillRect(0, 0, getWidth(), getHeight());
			}
		};
		// Set it non-opaque
		glass.setOpaque(false);
		// Set layout to JPanel
		glass.setLayout(new GridBagLayout());
		// Add the jlabel with the image icon
		glass.add(new JLabel(new ImageIcon("Resources/loading2.gif")));
		// Take glass pane
		setGlassPane(glass);
		// Add MouseListener
		glass.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent me)
			{
				// Consume the event, now the input is blocked
				me.consume();
				// Create beep sound, when mouse is pressed
				Toolkit.getDefaultToolkit().beep();
			}
		});
		
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
