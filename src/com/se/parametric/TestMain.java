package com.se.parametric;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.border.Border;

import com.se.Quality.QAChecks;
import com.se.Quality.QAException;
import com.se.Quality.QAFeedBack;
import com.se.Quality.QAReviewData;
import com.se.Quality.QualityUnApprovedValue;
import com.se.grm.client.mapping.GrmGroup;
import com.se.grm.client.mapping.GrmRole;
import com.se.grm.client.mapping.GrmUser;
import com.se.parametric.dba.ParaQueryUtil;
import com.se.parametric.dev.ComponentExporterPanel;
import com.se.parametric.dev.Developement;
import com.se.parametric.dev.Update;
import com.se.parametric.dto.GrmUserDTO;
import com.se.parametric.fb.EngFeedBack;
import com.se.parametric.fb.ExceptionFB;
import com.se.parametric.fb.TLFeedBack;
import com.se.parametric.review.TLReviewData;
import com.se.parametric.unappValue.EngUnApprovedValueFeedback;
import com.se.parametric.unappValue.TLUnApprovedValue;

public class TestMain extends JPanel implements ActionListener
{

	/**
	 * Create the panel.
	 */
	EngFeedBack engfeedBack;
	TLFeedBack tlfeedBack;
	TLUnApprovedValue tlunApprovedPanel = null;
	EngUnApprovedValueFeedback engunApprovedPanel = null;
	TLReviewData reviewData;
	Developement developement;
	QAReviewData qaReviewData;
	QAFeedBack qaFeedBack;
	QualityUnApprovedValue qUnApproved;
	GrmUserDTO userDTO;
	QAException qaexception;
	ExceptionFB exceptionfb;
	QAChecks qachecks;
	JButton engfeedBackBu;
	JButton tlfeedBackBu;
	JButton tlunApprovedBu = null;
	JButton engunApprovedBu = null;
	JButton tlreviewDataBu;
	JButton developementBu;
	JButton qaReviewDataBu;
	JButton qaFeedBackBu;
	JButton qUnApprovedBu;
	JButton qaexceptionBu;
	JButton exceptionfbBu;
	JButton qachecksBu;
	static int width;
	static int height;
	JPanel mainpnl;
	JPanel tabspanel;
	JTabbedPane tabbedPane;

	public TestMain(GrmUserDTO userDTO, int width, int height)
	{
		this.userDTO = userDTO;
		setLayout(null);

		long userRole = userDTO.getGrmRole().getId();
		long userGroup = userDTO.getGrmGroup().getId();
		this.setBounds(0, 0, width, height - 10);
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(0, height - 965, width - 10, height - 30);
		tabbedPane.setBorder(BorderFactory.createEmptyBorder());
		mainpnl = new JPanel();
		mainpnl.setLayout(null);
		mainpnl.setBounds(0, 5, width - 10, height - 930);
		// mainpnl.setBackground(new Color(255, 255, 255));

		mainpnl.setBorder(BorderFactory.createEmptyBorder());
		tabspanel = new JPanel();
		tabspanel.setLayout(null);
		tabspanel.setBounds(0, height - 955, width - 10, height - 30);
		// tabspanel.setBackground(new Color(255, 255, 255));
		tabspanel.setBorder(BorderFactory.createEmptyBorder());
		tabspanel.add(tabbedPane);

		JToolBar toolBar = new JToolBar("");
		toolBar.setFloatable(false);
		toolBar.setBounds(0, 5, width - 5, height - 935);
		// toolBar.setBorder(BorderFactory.createLineBorder(Color.gray, 1));
		// toolBar.setBackground(new Color(255, 255, 255));
		// role 3 eng , role 1 tl
		if(userRole == 1 && userGroup == 1)
		{
			int wstart = 10;
			int Bwidth = 170;
			tlreviewDataBu = new JButton("TL Review");
			tlreviewDataBu.setBounds(wstart, 5, Bwidth - 5, 32);
			tlreviewDataBu.setForeground(new Color(25, 25, 112));
			tlreviewDataBu.setFont(new Font("Herman", Font.PLAIN, 11));
			// tlreviewDataBu.setBackground(new Color(255, 255, 255));
			tlreviewDataBu.addActionListener(this);
			tlreviewDataBu.setToolTipText("TL Review");
			tlreviewDataBu.setOpaque(false);
			tlreviewDataBu.setBorder(BorderFactory.createEmptyBorder());
			tlreviewDataBu.setVerticalTextPosition(SwingConstants.BOTTOM);
			tlreviewDataBu.setHorizontalTextPosition(SwingConstants.CENTER);

			tlfeedBackBu = new JButton("TL FeedBack");
			tlfeedBackBu.setBounds(wstart + Bwidth, 5, Bwidth - 5, 32);
			tlfeedBackBu.setForeground(new Color(25, 25, 112));
			// tlfeedBackBu.setBackground(new Color(255, 255, 255));
			tlfeedBackBu.setFont(new Font("Herman", Font.PLAIN, 11));
			tlfeedBackBu.addActionListener(this);
			tlfeedBackBu.setToolTipText("TL FeedBack");
			tlfeedBackBu.setOpaque(false);
			tlfeedBackBu.setBorder(BorderFactory.createEmptyBorder());
			tlfeedBackBu.setVerticalTextPosition(SwingConstants.BOTTOM);
			tlfeedBackBu.setHorizontalTextPosition(SwingConstants.CENTER);

			tlunApprovedBu = new JButton("UnApproved Value");
			tlunApprovedBu.setBounds(wstart + (2 * Bwidth), 5, Bwidth - 5, 32);
			tlunApprovedBu.setForeground(new Color(25, 25, 112));
			// tlunApprovedBu.setBackground(new Color(255, 255, 255));
			tlunApprovedBu.setFont(new Font("Herman", Font.PLAIN, 11));
			tlunApprovedBu.addActionListener(this);
			tlunApprovedBu.setToolTipText("UnApproved Value");
			tlunApprovedBu.setOpaque(false);
			tlunApprovedBu.setBorder(BorderFactory.createEmptyBorder());
			tlunApprovedBu.setVerticalTextPosition(SwingConstants.BOTTOM);
			tlunApprovedBu.setHorizontalTextPosition(SwingConstants.CENTER);

			Image img;
			try
			{
				img = ImageIO.read(getClass().getResource("/Resources/rsz_pv.png"));
				tlreviewDataBu.setIcon(new ImageIcon(img));
				tlreviewDataBu.setIconTextGap(5);

				img = ImageIO.read(getClass().getResource("/Resources/rsz_feedback-icon_1.png"));
				tlfeedBackBu.setIcon(new ImageIcon(img));
				tlfeedBackBu.setIconTextGap(5);

				img = ImageIO.read(getClass().getResource("/Resources/rsz_approved.png"));
				tlunApprovedBu.setIcon(new ImageIcon(img));
				tlunApprovedBu.setIconTextGap(5);
			}catch(IOException e)
			{
				System.out.println("Set the ButtonIcon");
				e.printStackTrace();
			}
			toolBar.addSeparator();
			toolBar.add(tlreviewDataBu);
			toolBar.addSeparator();
			toolBar.add(tlfeedBackBu);
			toolBar.addSeparator();
			toolBar.add(tlunApprovedBu);

		}
		else if(userRole == 3 && userGroup == 1)
		{
			// // parametric eng
			//
			// engfeedBack = new EngFeedBack(userDTO);
			// engunApprovedPanel = new EngUnApprovedValueFeedback(userDTO);
			// developement = new Developement(userDTO);
			// exceptionfb = new ExceptionFB(userDTO);
			// qachecks = new QAChecks(userDTO);
			// Update update = new Update(userDTO);
			// ComponentExporterPanel exportPanel = new ComponentExporterPanel(userDTO);
			// tabbedPane.addTab("Developement", null, developement, null);
			// tabbedPane.addTab("NPI Update", null, update, null);
			// tabbedPane.addTab("EngFeedback", null, engfeedBack, null);
			// tabbedPane.addTab("QA Checks", null, qachecks, null);
			// tabbedPane.addTab("ExceptionFeedback", null, exceptionfb, null);
			// tabbedPane.addTab("EngUnApproved", null, engunApprovedPanel, null);
			// tabbedPane.addTab("Export", null, exportPanel, null);

		}
		else if(userRole == 3 && userGroup == 23)
		{
			// Q eng
			// System.out.println("Quality Eng");
			// tlfeedBack = new TLFeedBack(userDTO);
			//
			// qaReviewData = new QAReviewData(userDTO);
			// qaFeedBack = new QAFeedBack(userDTO);
			// qUnApproved = new QualityUnApprovedValue(userDTO);
			// qaexception = new QAException(userDTO);
			//
			// tabbedPane.addTab("Quality Data Review", null, qaReviewData, null);
			// tabbedPane.addTab("Quality Feedback", null, qaFeedBack, null);
			// tabbedPane.addTab("Quality UnApproved", null, qUnApproved, null);
			// tabbedPane.addTab("Quality Exception", null, qaexception, null);
		}
		mainpnl.add(toolBar);
		add(mainpnl);
		add(tabspanel);
		// add(new JButton("Ok"));
	}

	public void updateFlags()
	{
		long userRole = userDTO.getGrmRole().getId();
		long userGroup = userDTO.getGrmGroup().getId();
		ArrayList<String> flags = new ArrayList<String>();
		flags = ParaQueryUtil.getAlerts(userDTO.getId(), userGroup, userRole);

		// TL Screens
		if(tlfeedBack != null)
			tlfeedBack.updateFlags();
		if(reviewData != null)
			reviewData.updateFlags();
		if(tlunApprovedPanel != null)
			tlunApprovedPanel.updateFlags();
		// Eng Screens
		if(engfeedBack != null)
			engfeedBack.updateFlags();
		if(engunApprovedPanel != null)
			engunApprovedPanel.updateFlags();
		if(developement != null)
			developement.updateFlags();
		if(qachecks != null)
			qachecks.updateFlags();
		if(exceptionfb != null)
			exceptionfb.updateFlags();
		// QA Screens
		if(qaReviewData != null)
			qaReviewData.updateFlags();
		if(qaFeedBack != null)
			qaFeedBack.updateFlags();
		if(qUnApproved != null)
			qUnApproved.updateFlags();
		if(qaexception != null)
			qaexception.updateFlags();

	}

	@Override
	public void actionPerformed(ActionEvent event)
	{
		LongRunProcess longRunProcess = new LongRunProcess(event);
		longRunProcess.execute();

	}

	public static void main(String[] args)
	{
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		width = screenSize.width;
		height = screenSize.height - 30;
		JPanel contentPane = new JPanel();
		contentPane.removeAll();
		contentPane.getWidth();
		contentPane.getHeight();
		contentPane.getHeight();
		GrmUserDTO uDTO = new GrmUserDTO();
		uDTO.setId(121);
		uDTO.setFullName("Ahmad_Rahim");
		GrmRole role = new GrmRole();
		role.setId(1l);
		GrmGroup group = new GrmGroup();
		group.setId(1l);
		uDTO.setGrmRole(role);
		uDTO.setGrmGroup(group);
		TestMain p = new TestMain(uDTO, width, height);
		System.out.println("Main Frame Dimession " + width + " " + height);
		p.repaint();
		System.out.println("Main Panel Dimession " + p.getWidth() + " " + p.getHeight());
		contentPane.add(p);
		contentPane.revalidate();
		contentPane.repaint();
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// int width = Toolkit.getDefaultToolkit().getScreenSize().width;
		// int height = Toolkit.getDefaultToolkit().getScreenSize().height;
		frame.setSize(width, height);
		frame.setTitle("MainPanel");
		frame.getContentPane().add(p);
		frame.show();
	}

	class LongRunProcess extends SwingWorker
	{
		ActionEvent event = null;

		LongRunProcess(ActionEvent event)
		{
			this.event = event;
		}

		@Override
		protected Object doInBackground() throws Exception
		{
			MainWindow.glass.setVisible(true);
			if(event.getSource() == tlfeedBackBu)
			{
				if(tabbedPane.indexOfComponent(tlfeedBack) == -1)
				{
					tlfeedBack = new TLFeedBack(userDTO);
					tabbedPane.addTab("TL Feedback", null, tlfeedBack, null);
					int index = tabbedPane.indexOfComponent(tlfeedBack);
					tabbedPane.setTabComponentAt(index, new ButtonTabComponent(tabbedPane));
					tabbedPane.setSelectedIndex(index);
				}
			}
			if(event.getSource() == tlreviewDataBu)
			{
				if(tabbedPane.indexOfComponent(reviewData) == -1)
				{
					reviewData = new TLReviewData(userDTO);
					tabbedPane.addTab("TL Review", null, reviewData, null);
					int index = tabbedPane.indexOfComponent(reviewData);
					tabbedPane.setTabComponentAt(index, new ButtonTabComponent(tabbedPane));
					tabbedPane.setSelectedIndex(index);
				}
			}
			if(event.getSource() == tlunApprovedBu)
			{
				if(tabbedPane.indexOfComponent(tlunApprovedPanel) == -1)
				{
					tlunApprovedPanel = new TLUnApprovedValue(userDTO);
					tabbedPane.addTab("TL UnApproved Value", null, tlunApprovedPanel, null);
					int index = tabbedPane.indexOfComponent(tlunApprovedPanel);
					tabbedPane.setTabComponentAt(index, new ButtonTabComponent(tabbedPane));
				}
			}
			MainWindow.glass.setVisible(false);
			return null;
		}
	}
}
