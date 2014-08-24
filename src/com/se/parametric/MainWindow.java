package com.se.parametric;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.se.parametric.dba.ParaQueryUtil;
import com.se.parametric.dto.GrmUserDTO;
import com.se.parametric.util.ImagePanel;

public class MainWindow extends JFrame
{

	// private JPanel contentPane;
	// JPanel panel, panel2;
	// int width, height;
	// Container container;
	private MainPanel mainPanel;
	private JMenuBar menuBar;
	private GrmUserDTO loggedInUser;
	public static JPanel glass;

	static
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
	}

	public MainWindow()
	{

		// container = getContentPane();
		setTitle("Parametric Automation");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setExtendedState(JFrame.MAXIMIZED_BOTH);

		menuBar = new JMenuBar();

		JMenu optionsMenu = new JMenu("Options");
		JMenuItem changePassMenuItem = new JMenuItem("Change Password");
		changePassMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				showChangePassDialog();
			}
		});
		optionsMenu.add(changePassMenuItem);

		JMenu helpMenu = new JMenu("Help");
		JMenuItem devFlowMenuItem = new JMenuItem("Development Flow");
		devFlowMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				showImagePanel("Development Flow", "/Development-chart.jpg");
			}
		});
		JMenuItem separationFlowMenuItem = new JMenuItem("Separation Flow");
		separationFlowMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				showImagePanel("Separation Flow", "/QASeparation.jpg");
			}
		});
		helpMenu.add(devFlowMenuItem);
		helpMenu.add(separationFlowMenuItem);

		menuBar.add(optionsMenu);
		menuBar.add(helpMenu);
		setJMenuBar(menuBar);

		setGlassPane(glass);
		// Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		// width = screenSize.width;
		// height = screenSize.height - 30;
		//
		// System.out.println((width - 350) / 2 + " and " + (height - 150) / 2);
		// setBounds(0, 0, width, height);
		//
		// contentPane = new JPanel();
		// contentPane.setLayout(null);
		// container.add(contentPane);

//		 com.jtattoo.plaf.mint.MintLookAndFeel.setTheme("Default");
//		 try
//		 {
//		 UIManager.setLookAndFeel("com.jtattoo.plaf.texture.TextureLookAndFeel");
//		 }catch(ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e)
//		 {
//		 // TODO Auto-generated catch block
//		 e.printStackTrace();
//		 }
		 try
		 {
		 // Set cross-platform Java L&F (also called "Metal")
		 UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		 }catch(UnsupportedLookAndFeelException e)
		 {
		
		 }catch(ClassNotFoundException e)
		 {
		
		 }catch(InstantiationException e)
		 {
		
		 }catch(IllegalAccessException e)
		 {
		
		 }

	}

	public void init(GrmUserDTO grmUser)
	{
		loggedInUser = grmUser;
		mainPanel = new MainPanel(grmUser);
		getContentPane().add(mainPanel);

		// contentPane.removeAll();
		// contentPane.getWidth();
		// contentPane.getHeight();
		// System.out.println("Main Frame Dimession " + width + " " + height);
		// p.repaint();
		// System.out.println("Main Panel Dimession " + p.getWidth() + " " + p.getHeight());
		// contentPane.add(p);
		// contentPane.revalidate();
		// contentPane.repaint();
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
		mainPanel.updateFlags();
	}

	public void showImagePanel(String title, String imgName)
	{
		JDialog dialog = new JDialog(this);
		dialog.setModal(true);
		dialog.add(new ImagePanel(title, imgName));
		dialog.setLocationRelativeTo(this);
		dialog.setBounds(20, 20, 960, 900);
		dialog.setVisible(true);
	}

	public void showChangePassDialog()
	{
		final JDialog dialog = new JDialog(this, "Change Password");
		dialog.setModal(true);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int width = (int) screenSize.getWidth();
		int height = (int) screenSize.getHeight();
		dialog.setBounds((width - 383) / 2, (height - 249) / 2, 383, 249);

		JPanel passPanel = new JPanel();
		GroupLayout groupLayout = new GroupLayout(passPanel);
		groupLayout.setAutoCreateGaps(true);
		groupLayout.setAutoCreateContainerGaps(true);
		passPanel.setLayout(groupLayout);

		JLabel userNameImg = new JLabel(new ImageIcon(this.getClass().getResource(
				"/Resources/user_face.png")));
		JLabel userNameLbl = new JLabel(loggedInUser.getFullName());
		JLabel newPassLbl = new JLabel("New Password");
		final JPasswordField newPassFild = new JPasswordField();
		newPassFild.setColumns(20);
		JButton cancelBtn = new JButton("Cancel");
		cancelBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				dialog.dispose();
			}
		});

		JButton changePassBtn = new JButton("Done");
		changePassBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				String pass = new String(newPassFild.getPassword());
				if(null != pass && !pass.trim().isEmpty())
				{
					ParaQueryUtil.changeUserPass(loggedInUser.getFullName(), pass);
					JOptionPane.showMessageDialog(dialog, "Password changed", "Done",
							JOptionPane.INFORMATION_MESSAGE);
					dialog.dispose();
				}
				else
				{
					JOptionPane.showMessageDialog(dialog,
							"password field can't be empty or spaces", "Empty Password",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		groupLayout.setHorizontalGroup(groupLayout
				.createSequentialGroup()
				.addGroup(
						groupLayout.createParallelGroup().addComponent(newPassLbl)
								.addComponent(cancelBtn))
				.addGroup(
						groupLayout.createParallelGroup().addComponent(userNameImg)
								.addComponent(userNameLbl).addComponent(newPassFild)
								.addComponent(changePassBtn)));

		groupLayout.setVerticalGroup(groupLayout
				.createSequentialGroup()
				.addComponent(userNameImg)
				.addComponent(userNameLbl)
				.addGap(20)
				.addGroup(
						groupLayout.createParallelGroup().addComponent(newPassLbl)
								.addComponent(newPassFild))
				.addGroup(
						groupLayout.createParallelGroup().addComponent(cancelBtn)
								.addComponent(changePassBtn)));

		dialog.add(passPanel);
		dialog.pack();
		dialog.setVisible(true);
	}
}
