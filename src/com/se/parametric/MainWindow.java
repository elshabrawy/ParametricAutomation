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

import java.util.ArrayList;

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
	public static ArrayList<String> flags = new ArrayList<String>();
	// private JPanel contentPane;
	// JPanel panel, panel2;
	// int width, height;
	// Container container;
	private MainPanel mainPanel;
	private JMenuBar menuBar;
	private GrmUserDTO loggedInUser;
	public static JPanel glass;

	// static
	// {
	// glass = new JPanel() {
	// public void paintComponent(Graphics g)
	//
	// {
	// g.setColor(new Color(0, 0, 0, 140));
	// g.fillRect(0, 0, getWidth(), getHeight());
	// }
	// };
	// // Set it non-opaque
	// glass.setOpaque(false);
	// // Set layout to JPanel
	// glass.setLayout(new GridBagLayout());
	// // Add the jlabel with the image icon
	//
	// glass.add(new JLabel(new ImageIcon("Resources/loading.gif")));
	//
	// // Add MouseListener
	// glass.addMouseListener(new MouseAdapter() {
	// public void mousePressed(MouseEvent me)
	// {
	// // Consume the event, now the input is blocked
	// me.consume();
	// // Create beep sound, when mouse is pressed
	// Toolkit.getDefaultToolkit().beep();
	// }
	// });
	// }

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
		JLabel lbl = new JLabel("Version No 2.9");
		lbl.setFont(new Font("Simpson", Font.BOLD, 18));
		lbl.setForeground(new Color(160, 82, 45));
		lbl.setBounds(((width - 383) / 4) - 100, 5, 300, 40);

		JLabel lbl2 = new JLabel("Last updates on 30/11/2014 :- ");
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
		txtarea.setBounds(0, 80, 450, 250);
		String txt = "";

		txt += "---------------------------------------------\n";
		txt += "~~~~~~~ Version No 2.9 @ 30/11/2014 ~~~~~~~~~\n";
		txt += "---------------------------------------------\n";

		txt += "- Prevent to save more than one feedback on same item \n";
		txt += "- Open feedback to all documents have same wrong value \n";

		txt += "---------------------------------------------\n";
		txt += "~~~~~~~ Version No 2.8 @ 24/11/2014 ~~~~~~~~~\n";
		txt += "---------------------------------------------\n";

		txt += "- Write exception message in saving sheet \n";
		txt += "- load pdfs in doneflag engine in Update \n";
		txt += "- Close SRCFB screen after feedback sent  \n";
		txt += "- Display issue type in EngFB screen  \n";
		txt += "- Enhance get QA user method to get right qauser  \n";
		txt += "- Fix NPI update saving  \n";

		txt += "---------------------------------------------\n";
		txt += "~~~~~~~ Version No 2.7 @ 18/11/2014 ~~~~~~~~~\n";
		txt += "---------------------------------------------\n";

		txt += "- Solve paging exception \n";
		txt += "- Fill the approved value Map before validation \n";
		txt += "- Enance development sheet of saving parts  \n";
		txt += "- Enance displaying in Newslink,NewsDate  \n";

		txt += "---------------------------------------------\n";
		txt += "~~~~~~~ Version No 2.6 @ 03/11/2014 ~~~~~~~~~\n";
		txt += "---------------------------------------------\n";

		txt += "- Enhance in validation of seperation value don't contains (min) or (max) or (typ) \n";
		txt += "- Fix duplicated QAcheck exception \n";
		txt += "- Enance sheetPerformance in ShowAll   \n";

		txt += "---------------------------------------------\n";
		txt += "~~~~~~~ Version No 2.5 @ 28/10/2014 ~~~~~~~~~\n";
		txt += "---------------------------------------------\n";

		txt += "- Enhance loading NPIUpdate in TL&QA screen  \n";
		txt += "- Remove exception in get mask&generic&family methods \n";
		txt += "- Enance issue of display feature value that the same as feature name  \n";

		txt += "---------------------------------------------\n";
		txt += "~~~~~~~ Version No 2.4 @ 22/10/2014 ~~~~~~~~~\n";
		txt += "---------------------------------------------\n";

		txt += "- Enhance loading of summary to increase performance  \n";
		txt += "- Enhance saving of summary to increase performance  \n";
		txt += "- Handle exceptions in saving approved values and parts  \n";
		txt += "- Enhance loadall action in NPIUPDATE screen  \n";
		txt += "- Enhance issue of selection from another page of filter result  \n";
		txt += "- Enhance issue of seperation loading and saving  \n";

		txt += "---------------------------------------------\n";
		txt += "~~~~~~~ Version No 2.3 @ 19/10/2014 ~~~~~~~~~\n";
		txt += "---------------------------------------------\n";

		txt += "- Enhance shift of cloumns in tlreview screen \n";
		txt += "- Enhance the problem of special character ™ \n";
		txt += "- Display and filter data with one date Modification date \n";
		txt += "- Update saving to ignore Dupplication in the sheet while insert  \n";
		txt += "- Enhance load pdf & load all  \n";
		txt += "- Enhance filtring by date in unapproved screens \n";
		txt += "- Complete the cycle of wrongvalue FB  \n";
		txt += "- Add generic to header of QA Checks  \n";
		txt += "- Add to summary pdfs has Qareview status  \n";

		txt += "---------------------------------------------\n";
		txt += "~~~~~~~ Version No 2.2 @ 23/9/2014 ~~~~~~~~~\n";
		txt += "---------------------------------------------\n";

		txt += "- Enhance the saving in Eng-unapproved feedback screen \n";
		txt += "- Enhance displaying of newsdata in TL screen \n";
		txt += "- Enhance performance of saving in NPIUpdate \n";
		txt += "- Enhance filter  of unapproved values to display all assgined pdfs  \n";
		txt += "- Enhance display of un approved values in TL Seperation \n";
		txt += "- Add new status to TL & Eng to manage the cycle of unapproved values with missed parts  \n";
		txt += "- Check the header of seperation in all screens  \n";

		txt += "---------------------------------------------\n";
		txt += "~~~~~~~ Version No 2.1 @ 16/9/2014 ~~~~~~~~~\n";
		txt += "---------------------------------------------\n";

		txt += "- Get Non PDF News and solve null news list\n";
		txt += "- Validate that News not Blank\n";
		txt += "- Update saving of approvedvalues(Conflict between Condition and Unit)\n";
		txt += "- Update loading of (development & update) screens\n";
		txt += "- Enhance Seperaton Header in (Development&TL Review) screens\n";
		txt += "- Prevent saving twice\n";
		txt += "- Prevent saving seperation without value\n";
		txt += "- Solve issue of naming office sheet due to special char (\\)\n";
		txt += "- Load Non PDF News and solve null news in development screen \n";
		txt += "- Display loading window in QA Review screen \n";
		txt += "- Enhance dofilter & loading pdfs in Update screen \n";
		txt += "- Enhance the problem of header in QA screen \n";

		txt += "---------------------------------------------\n";
		txt += "~~~~~~~ Version No 2.0 @ 26/8/2014 ~~~~~~~~~\n";
		txt += "---------------------------------------------\n";
		txt += "1- Change the Automaion Colors\n";
		txt += "2- Change the display of Tabs\n";
		txt += "3- Enhance the Problem of Loading Screen\n";
		txt += "4- Enhance the dispaly Filter Area\n";
		txt += "5- Enhance the dispaly counts Area\n";
		txt += "6- Change the Date Title in Table of Filter result\n";
		txt += "7- Add Menus for Cycles and new Options (Logout,ChangePassword)\n";
		txt += "8- update Unapproved Header by add(Vendor,ReveivedDate)\n";

		txtarea.setText(txt);
		txtarea.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(txtarea);
		scrollPane.setBounds(0, 80, 450, 250);
		scrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));
		dialog.add(lbl);
		dialog.add(lbl2);
		dialog.add(scrollPane);
		dialog.add(lbl3);
		dialog.setLocationRelativeTo(this);
		dialog.setTitle("Parametric Automation");
		dialog.setBounds((width - 450) / 2, (height - 249) / 2, 470, 330);
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
					log.txtUserName.setFocusable(true);
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
		updateFlags(grmUser);
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

	public void updateFlags(GrmUserDTO grmUser)
	{

		long userRole = grmUser.getGrmRole().getId();
		long userGroup = grmUser.getGrmGroup().getId();
		flags = ParaQueryUtil.getAlerts(grmUser.getId(), userGroup, userRole);
		// mainPanel.updateFlags();
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
