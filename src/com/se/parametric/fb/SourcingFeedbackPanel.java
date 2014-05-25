package com.se.parametric.fb;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

import com.se.parametric.dba.DataDevQueryUtil;
import com.se.parametric.dba.ParaQueryUtil;

public class SourcingFeedbackPanel extends JPanel implements ActionListener, KeyListener
{

	private JComboBox<String> statusCombo;
	private ComboBoxModel commentComboModel;
	private List<String> plNames;
	private JComboBox<String> commentCombo;
	private JButton sendFeedbackBtn;
	private JTextField dsUrlTF;
	private JTextField plTF;
	JTextComponent textComponent;
	private String userName;

	/**
	 * Create the panel.
	 */
	public SourcingFeedbackPanel(String userName, String pdfUrl, String plName)
	{
		setSize(500, 300);
		setLayout(null);

		this.userName = userName;

		JLabel dsUrlLbl = new JLabel("PDF Link : ");
		JLabel statusLbl = new JLabel("Status : ");
		JLabel commentLbl = new JLabel("Comment : ");
		JLabel plLbl = new JLabel("PL : ");
		dsUrlTF = new JTextField(pdfUrl);
		plTF = new JTextField(plName);
		plTF.setEditable(false);
		commentCombo = new JComboBox<String>();
		commentCombo.setEditable(true);
		String[] statusComboItems = { "Wrong Revision", "Wrong Taxonomy", "Reject", "Not Available Data" };
		statusCombo = new JComboBox<String>(statusComboItems);
		sendFeedbackBtn = new JButton("Send Feedback");

		statusCombo.addActionListener(this);
		sendFeedbackBtn.addActionListener(this);
		textComponent = (JTextComponent) commentCombo.getEditor().getEditorComponent();

		textComponent.addKeyListener(this);

		dsUrlLbl.setForeground(new Color(25, 25, 112));
		dsUrlLbl.setFont(new Font("Tahoma", Font.BOLD, 11));
		statusLbl.setForeground(new Color(25, 25, 112));
		statusLbl.setFont(new Font("Tahoma", Font.BOLD, 11));
		commentLbl.setForeground(new Color(25, 25, 112));
		commentLbl.setFont(new Font("Tahoma", Font.BOLD, 11));
		sendFeedbackBtn.setForeground(new Color(25, 25, 112));
		sendFeedbackBtn.setFont(new Font("Tahoma", Font.BOLD, 11));
		plLbl.setForeground(new Color(25, 25, 112));
		plLbl.setFont(new Font("Tahoma", Font.BOLD, 11));

		dsUrlLbl.setBounds(20, 20, 60, 25);
		dsUrlTF.setBounds(100, 20, 380, 25);
		plLbl.setBounds(20, 65, 60, 25);
		plTF.setBounds(100, 65, 200, 25);
		statusLbl.setBounds(20, 110, 80, 25);
		commentLbl.setBounds(200, 110, 80, 25);
		statusCombo.setBounds(20, 135, 150, 25);
		commentCombo.setBounds(200, 135, 280, 25);
		sendFeedbackBtn.setBounds(180, 200, 140, 25);

		commentComboModel = new DefaultComboBoxModel();

		add(dsUrlLbl);
		add(dsUrlTF);
		add(plLbl);
		add(plTF);
		add(statusLbl);
		add(commentLbl);
		add(statusCombo);
		add(commentCombo);
		add(sendFeedbackBtn);

	}

	public ComboBoxModel getCommentComboBoxModel(String type, String startsWith)
	{
		DefaultComboBoxModel model = new DefaultComboBoxModel();

		if("PL".equals(type))
		{
			// model.addElement(startsWith);
			if(plNames == null)
			{
				plNames = ParaQueryUtil.getAllPlNames();
			}
			// System.out.println(plNames);
			if(startsWith != null)
			{
				for(String str : plNames)
				{
					if(str.length() >= startsWith.length())
					{
						String sub = str.substring(0, startsWith.length());
						if(startsWith.equalsIgnoreCase(sub))
						{
							// if (str.startsWith(startsWith)) {
							model.addElement(str);
						}
					}
				}
			}
			else
			{
				for(String str : plNames)
				{
					model.addElement(str);
				}
			}
		}
		else if("Reject".equals(type))
		{
			String[] rejectCommentOptions = { "Documentation", "Broken Link", "No order Information", "Not Complete DS", "Wrong Vendor", "Acquired Vendor" };
			for(int i = 0; i < rejectCommentOptions.length; i++)
			{
				model.addElement(rejectCommentOptions[i]);
			}
		}
		return model;
	}

	public static void main(String[] args)
	{
		JFrame srcFeedbackFrame = new JFrame("Sourcing Feedback");
		SourcingFeedbackPanel panel = new SourcingFeedbackPanel("a_kamal", "scxsdc", "dscfk");
		srcFeedbackFrame.getContentPane().add(panel);
		srcFeedbackFrame.setBounds(200, 150, 500, 300);
		srcFeedbackFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		srcFeedbackFrame.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == statusCombo)
		{
			if("Wrong Taxonomy".equals(statusCombo.getSelectedItem()))
			{
				commentComboModel = getCommentComboBoxModel("PL", null);
			}
			else if("Reject".equals(statusCombo.getSelectedItem()))
			{
				commentComboModel = getCommentComboBoxModel("Reject", null);
				commentCombo.setEditable(false);
			}
			else
			{
				commentComboModel = getCommentComboBoxModel(null, null);
				commentCombo.setEditable(true);
			}
			commentCombo.setModel(commentComboModel);

		}
		if(e.getSource() == sendFeedbackBtn)
		{
			String revUrl = null, docFeedbackComment = null, rightTax = null;
			String pdfLink = dsUrlTF.getText();
			pdfLink = pdfLink.trim();
			if("".equals(pdfLink) || pdfLink.length() < 7 || !"http://".equalsIgnoreCase(pdfLink.substring(0, 7)))
			{
				JOptionPane.showMessageDialog(this, "Wrong PDF Link", "Wrong PDF Link", JOptionPane.ERROR_MESSAGE);
				return;
			}
			if("Wrong Revision".equals(statusCombo.getSelectedItem().toString()))
			{
				try
				{
					String comment = commentCombo.getSelectedItem().toString();
					comment = comment.trim();
					String sub = comment.substring(0, 7);
					if(!"http://".equalsIgnoreCase(sub))
					{
						JOptionPane.showMessageDialog(this, "Comment should start with http://", "Wrong Comment", JOptionPane.ERROR_MESSAGE);
						return;
					}
					revUrl = comment;
					docFeedbackComment = "Wrong Revision";
					rightTax = null;
				}catch(Exception ex)
				{
					JOptionPane.showMessageDialog(this, "Comment should start with http://", "Wrong Comment", JOptionPane.ERROR_MESSAGE);
					return;
				}
			}
			else if("Not Available Data".equals(statusCombo.getSelectedItem().toString()))
			{

				if((commentCombo.getSelectedItem() == null) || "".equals(commentCombo.getSelectedItem().toString()))
				{
					docFeedbackComment = "Not Available Data";
					revUrl = null;
					rightTax = null;
				}
				else
				{
					JOptionPane.showMessageDialog(this, "Comment should be null", "Wrong Comment", JOptionPane.ERROR_MESSAGE);
					return;
				}
			}
			else if("Wrong Taxonomy".equals(statusCombo.getSelectedItem().toString()))
			{
				docFeedbackComment = "Wrong tax";
				rightTax = commentCombo.getEditor().getItem().toString();
				revUrl = null;
			}
			else if("Reject".equals(statusCombo.getSelectedItem().toString()))
			{
				docFeedbackComment = commentCombo.getSelectedItem().toString();
				rightTax = null;
				revUrl = null;
			}

			String pdfUrl = dsUrlTF.getText().trim();
			String plName = plTF.getText().trim();
			String status = DataDevQueryUtil.sendFeedbackToSourcingTeam(userName, pdfUrl, plName, docFeedbackComment, revUrl, rightTax);
			if("Done".equals(status))
			{
				JOptionPane.showMessageDialog(this, "Feedback Sent", "Feedback Sent", JOptionPane.INFORMATION_MESSAGE);
			}
			else
			{
				JOptionPane.showMessageDialog(this, "Feedback Not Sent", "Feedback Not Sent>>>" + status, JOptionPane.ERROR_MESSAGE);
			}

		}
	}

	@Override
	public void keyPressed(KeyEvent e)
	{
		// System.out.println("key pressed : " + e.toString());
		// String startsWith = textComponent.getText();
		// commentCombo.setSelectedItem(startsWith);
		// System.out.println(startsWith);
		// commentComboModel = getCommentComboBoxModel("PL", startsWith);
		// commentCombo.setModel(commentComboModel);

	}

	@Override
	public void keyReleased(KeyEvent e)
	{
		if(!"Wrong Taxonomy".equals(statusCombo.getSelectedItem()))
		{
			return;
		}
		int keyCode = e.getKeyCode();
		if((keyCode == KeyEvent.VK_UP) || (keyCode == KeyEvent.VK_DOWN) || (keyCode == KeyEvent.VK_RIGHT) || (keyCode == KeyEvent.VK_LEFT))
		{
			return;
		}
		String startsWith = commentCombo.getEditor().getItem().toString();
		if("".equals(startsWith))
		{
			commentComboModel = getCommentComboBoxModel("PL", null);
		}
		else
		{
			commentComboModel = getCommentComboBoxModel("PL", startsWith);
		}

		commentCombo.setModel(commentComboModel);
		commentCombo.getEditor().setItem(startsWith);
		commentCombo.setPopupVisible(true);
	}

	@Override
	public void keyTyped(KeyEvent e)
	{

		// if (!"Wrong Taxonomy".equals(statusCombo.getSelectedItem())) {
		// return;
		// }
		// char c = e.getKeyChar();
		// System.out.println("char typed " + c);
		//
		// String startsWith = "" + c;
		// startsWith = startsWith.toUpperCase();
		// System.out.println("Starts with : " + startsWith);
		// if ("\b".equals(startsWith)) {
		// commentComboModel = getCommentComboBoxModel("PL", null);
		// } else {
		// commentComboModel = getCommentComboBoxModel("PL", startsWith);
		// }
		//
		// commentCombo.setModel(commentComboModel);

	}

}
