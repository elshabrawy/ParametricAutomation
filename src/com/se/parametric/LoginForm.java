package com.se.parametric;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;

import com.se.parametric.dba.ParaQueryUtil;
import com.se.parametric.dto.GrmUserDTO;

public class LoginForm extends JFrame
{

	private JPanel contentPane;
	private JTextField txtUserName;
	private JTextField txtPassword;
	String userName, password;
	MainWindow mainFrame;
	static LoginForm loginframe;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args)
	{
		EventQueue.invokeLater(new Runnable() {
			public void run()
			{
				try
				{
					loginframe = new LoginForm();
					loginframe.setTitle("Parametric Automation");
					loginframe.setVisible(true);
				}catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public LoginForm()
	{
//		com.jtattoo.plaf.mint.MintLookAndFeel.setTheme("Default");
//		try
//		{
//			UIManager.setLookAndFeel("com.jtattoo.plaf.texture.TextureLookAndFeel");
//		}catch(ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e)
//		{
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int width=(int) screenSize.getWidth();
		int height=(int) screenSize.getHeight();
		setBounds((width-383)/2, (height-249)/2, 383, 249);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.CENTER);
		panel.setLayout(null);

		JButton btnNewButton = new JButton("OK");
		btnNewButton.addActionListener(new ActionListener() {

	        public void actionPerformed(ActionEvent evt) {	     
	                LongRunProcess2 longRunProcess = new LongRunProcess2();
	                longRunProcess.execute();	            
	        }
	    } );
		btnNewButton.setBounds(144, 154, 81, 33);
		panel.add(btnNewButton);

		txtUserName = new JTextField();
		txtUserName.setBounds(125, 72, 161, 20);
		panel.add(txtUserName);
		txtUserName.setColumns(10);

		txtPassword = new JPasswordField();
		txtPassword.setColumns(10);
		txtPassword.setBounds(125, 112, 161, 20);
		panel.add(txtPassword);

		JLabel lblNewLabel = new JLabel("UserName : ");
		lblNewLabel.setFont(new Font("Trebuchet MS", Font.PLAIN, 17));
		lblNewLabel.setForeground(new Color(160, 82, 45));
		lblNewLabel.setBounds(10, 75, 96, 14);
		panel.add(lblNewLabel);

		JLabel lblPassword = new JLabel("Password :");
		lblPassword.setFont(new Font("Trebuchet MS", Font.PLAIN, 17));
		lblPassword.setForeground(new Color(160, 82, 45));
		lblPassword.setBounds(10, 115, 96, 14);
		panel.add(lblPassword);

		JLabel titleLab = new JLabel("Parametric Automation");
		titleLab.setHorizontalAlignment(SwingConstants.CENTER);
		titleLab.setForeground(new Color(160, 82, 45));
		titleLab.setFont(new Font("Old English Text MT", Font.PLAIN, 26));
		titleLab.setBounds(10, 11, 345, 34);
		panel.add(titleLab);

		JLabel lblver = new JLabel("Ver 1.0 : 20-7-2014 ");
		lblver.setFont(new Font("Trebuchet MS", Font.PLAIN, 12));
		lblver.setForeground(new Color(160, 82, 45));
		lblver.setBounds(220, 170, 150, 60);
		panel.add(lblver);
	}

	class LongRunProcess extends SwingWorker
	{
		/**
		 * @throws Exception
		 */
		protected Object doInBackground() throws Exception
		{
			Integer result = 0;

			while(true)
			{
				if(mainFrame != null)
				{
					result += 10;
					System.out.println("Result = " + result);

					mainFrame.updateFlags();
					try
					{
						Thread.sleep(50000);
					}catch(InterruptedException e)
					{
						e.printStackTrace();
					}
				}
			}

			// return result;
		}
	}
	
	class LongRunProcess2 extends SwingWorker
	{
		/**
		 * @throws Exception
		 */
		protected Object doInBackground() throws Exception
		{
			Loading loading = new Loading();
			
			loading.show();
			userName = txtUserName.getText().toString();
			password = txtPassword.getText().toString();
			GrmUserDTO grmUser = ParaQueryUtil.checkUser(userName, password);
			if(grmUser == null)
			{
				loading.close();
				JOptionPane.showMessageDialog(null, "User Name or Password is Error");
			}
			else
			{

				mainFrame = new MainWindow();
				mainFrame.setVisible(true);
				// primaryStage.hide();
				Runtime.getRuntime().gc();
				mainFrame.init(grmUser);
				loginframe.setVisible(false);
			}
			// thread.stop();
			LongRunProcess process = new LongRunProcess();
			try
			{
				process.execute();
			}catch(Exception e)
			{
				e.printStackTrace();
			}
			finally{
			loading.close();
			}

			 return null;
		}
	}
}
