package com.se.parametric;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowListener;
import java.util.Date;

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
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

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

//	static
//	{
//		glass = new JPanel() {
//			public void paintComponent(Graphics g)
//
//			{
//				g.setColor(new Color(0, 0, 0, 140));
//				g.fillRect(0, 0, getWidth(), getHeight());
//			}
//		};
//		// Set it non-opaque
//		glass.setOpaque(false);
//		// Set layout to JPanel
//		glass.setLayout(new GridBagLayout());
//		// Add the jlabel with the image icon
//
//		glass.add(new JLabel(new ImageIcon("Resources/loading.gif")));
//
//		// Add MouseListener
//		glass.addMouseListener(new MouseAdapter() {
//			public void mousePressed(MouseEvent me)
//			{
//				// Consume the event, now the input is blocked
//				me.consume();
//				// Create beep sound, when mouse is pressed
//				Toolkit.getDefaultToolkit().beep();
//			}
//		});
//	}

	public MainWindow()
	{
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
		JMenuItem logoutMenuItem = new JMenuItem("Logout");
		logoutMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				int reply = JOptionPane.showConfirmDialog(null, "Are you sure to logout?",
						"Paremtric Automation", JOptionPane.YES_NO_OPTION);
				if(reply == JOptionPane.YES_OPTION)
				{
					hideMainWindow();

				}
				else
				{
					return;
				}

			}

		});
		optionsMenu.addSeparator();
		optionsMenu.add(logoutMenuItem);
		JMenuItem aboutMenuItem = new JMenuItem("About");
		aboutMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				showupdatespanel();

			}

		});

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
		helpMenu.addSeparator();
		helpMenu.add(aboutMenuItem);
		menuBar.add(optionsMenu);
		menuBar.add(helpMenu);
		setJMenuBar(menuBar);
		createLoading();
		setGlassPane(glass);
		
	}

	private void createLoading()
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

		glass.add(new JLabel(new ImageIcon(getClass().getResource("/Resources/loading.gif"))));

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

	private void showupdatespanel()
	{
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int width = (int) screenSize.getWidth();
		int height = (int) screenSize.getHeight();
		JDialog dialog = new JDialog(this);
		dialog.setModal(true);
		dialog.setLayout(null);
		JLabel lbl = new JLabel("Version No 2.0");
		lbl.setFont(new Font("Simpson", Font.BOLD, 18));
		lbl.setForeground(new Color(160, 82, 45));
		lbl.setBounds(((width - 383) / 4) - 100, 5, 300, 40);

		JLabel lbl2 = new JLabel("Last updates on 26/8/2014 :- ");
		lbl2.setFont(new Font("Simpson", Font.BOLD, 14));
		lbl2.setForeground(new Color(189, 67, 67));
		lbl2.setBounds(0, 55, 250, 20);

		JLabel lbl3 = new JLabel("Copyright Banha Sofware 2013-2014.  All rights reserved.");
		lbl3.setFont(new Font("Simpson", Font.BOLD, 10));
		lbl3.setForeground(new Color(160, 82, 45));
		lbl3.setBounds(5, 270, 300, 20);

		JTextArea txtarea = new JTextArea();
		txtarea.setWrapStyleWord(true);
		txtarea.setLineWrap(true);
		txtarea.setForeground(new Color(0, 0, 0));
		txtarea.setFont(new Font("Simpson", Font.BOLD, 12));
		txtarea.setBounds(0, 80, 383, 180);
		String txt = "";
		txt += "1- Change the Automaion Colors" + "\n";
		txt += "2- Change the display of Tabs" + "\n";
		txt += "3- Enhance the Problem of Loading Screen" + "\n";
		txt += "4- Enhance the dispaly Filter Area" + "\n";
		txt += "5- Enhance the dispaly counts Area" + "\n";
		txt += "6- Change the Date Title in Table of Filter result" + "\n";
		txt += "7- Add Menus for Cycles and new Options (Logout,ChangePassword)" + "\n";
		txt += "8- update Unapproved Header by add(Vendor,ReveivedDate)" + "\n";
		txt += "" + "\n";
		txtarea.setText(txt);
		txtarea.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(txtarea);
		scrollPane.setBounds(0, 80, 383, 180);
		scrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));
		dialog.add(lbl);
		dialog.add(lbl2);
		dialog.add(scrollPane);
		dialog.add(lbl3);
		dialog.setLocationRelativeTo(this);
		dialog.setTitle("Parametric Automation");
		dialog.setBounds((width - 383) / 2, (height - 249) / 2, 383, 330);
		dialog.setVisible(true);

	}

	public void hideMainWindow()
	{
		final LoginForm log = LoginForm.loginframe;
		EventQueue.invokeLater(new Runnable() {
			public void run()
			{
				try
				{
					log.setTitle("Parametric Automation");
					MainWindow.glass.setVisible(false);
					log.txtPassword.setText("");
					log.txtUserName.setText("");
					log.setVisible(true);
				}catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		});
		this.dispose();
	}

	public void init(GrmUserDTO grmUser)
	{
		loggedInUser = grmUser;
		mainPanel = new MainPanel(grmUser);
		getContentPane().add(mainPanel);

		this.addWindowFocusListener(new WindowFocusListener() {
			@Override
			public void windowLostFocus(WindowEvent arg0)
			{
			}

			@Override
			public void windowGainedFocus(WindowEvent arg0)
			{
				mainPanel.requestFocusInWindow();
			}
		});
		this.addWindowListener(new WindowListener() {

			@Override
			public void windowOpened(WindowEvent arg0)
			{
			}

			@Override
			public void windowIconified(WindowEvent arg0)
			{
			}

			@Override
			public void windowDeiconified(WindowEvent arg0)
			{
			}

			@Override
			public void windowDeactivated(WindowEvent arg0)
			{
			}

			@Override
			public void windowClosing(WindowEvent arg0)
			{
				mainPanel.clearOfficeResources();
			}

			@Override
			public void windowClosed(WindowEvent arg0)
			{
			}

			@Override
			public void windowActivated(WindowEvent arg0)
			{
			}
		});

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
