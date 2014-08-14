package com.se.parametric;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;

import com.se.Quality.QAChecks;
import com.se.Quality.QAException;
import com.se.Quality.QAFeedBack;
import com.se.Quality.QAReviewData;
import com.se.Quality.QualityUnApprovedValue;
import com.se.parametric.TestMain.LongRunProcess;
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

public class MainPanel extends JPanel implements ActionListener
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
	Update update;
	ComponentExporterPanel exportPanel;
	JButton engfeedBackBu;
	JButton tlfeedBackBu;
	JButton tlunApprovedBu;
	JButton engunApprovedBu;
	JButton tlreviewDataBu;
	JButton developementBu;
	JButton qaReviewDataBu;
	JButton qaFeedBackBu;
	JButton qUnApprovedBu;
	JButton qaexceptionBu;
	JButton exceptionfbBu;
	JButton qachecksBu;
	JButton updateBu;
	JButton exportPanelBu;
	static int width;
	static int height;
	JPanel mainpnl;
	JPanel tabspanel;
	JTabbedPane tabbedPane;
	List<JButton> buttonlist;
	List<String> iconsurl;
	JToolBar toolBar;

	public MainPanel(GrmUserDTO userDTO, int width, int height)
	{
		this.userDTO = userDTO;
		setLayout(null);

		long userRole = userDTO.getGrmRole().getId();
		long userGroup = userDTO.getGrmGroup().getId();
		this.setBounds(0, 0, width, height);

		mainpnl = new JPanel();
		mainpnl.setLayout(null);
		mainpnl.setBorder(BorderFactory.createEmptyBorder());
		mainpnl.setBounds(0, 5, width - 10, height - 930);

		tabspanel = new JPanel();
		tabspanel.setLayout(null);
		tabspanel.setBorder(BorderFactory.createEmptyBorder());
		tabspanel.setBounds(0, height - 948, width - 10, height - 30);

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(0, height - 980, width - 10, height - 30);
		tabbedPane.setBorder(BorderFactory.createEmptyBorder());
		tabspanel.add(tabbedPane);

		toolBar = new JToolBar("");
		toolBar.setFloatable(false);
		toolBar.setBounds(0, 0, width - 5, height - 935);

		// role 3 eng , role 1 tl
		if(userRole == 1 && userGroup == 1)
		{
			int wstart = 10;
			int Bwidth = 170;
			buttonlist = new ArrayList<>();
			iconsurl = new ArrayList<>();

			tlreviewDataBu = new JButton("TL Data Review");
			iconsurl.add("/Resources/reviews.jpg");
			tlfeedBackBu = new JButton("TL Data FeedBack");
			iconsurl.add("/Resources/feedback-icon.png");
			tlunApprovedBu = new JButton("UnApproved Value");
			iconsurl.add("/Resources/approved.png");
			buttonlist.add(tlreviewDataBu);
			buttonlist.add(tlfeedBackBu);
			buttonlist.add(tlunApprovedBu);
			drawtoolbar(buttonlist, iconsurl, wstart, Bwidth);
		}
		else if(userRole == 3 && userGroup == 1)
		{
			// parametric eng
			int wstart = 10;
			int Bwidth = 170;
			buttonlist = new ArrayList<>();
			iconsurl = new ArrayList<>();
			developementBu = new JButton("Developement");
			iconsurl.add("/Resources/development-icon.png");
			updateBu = new JButton("NPI Update");
			iconsurl.add("/Resources/update-icon.png");
			engfeedBackBu = new JButton("Eng Data Feedback");
			iconsurl.add("/Resources/feedback-icon.png");
			engunApprovedBu = new JButton("Eng UnApproved Value");
			iconsurl.add("/Resources/approved.png");
			qachecksBu = new JButton("QA Checks");
			iconsurl.add("/Resources/check-icon.png");
			exceptionfbBu = new JButton("QA ExceptionFeedback");
			iconsurl.add("/Resources/exception.png");
			exportPanelBu = new JButton("Export");
			iconsurl.add("/Resources/export.png");

			buttonlist.add(developementBu);
			buttonlist.add(updateBu);
			buttonlist.add(engfeedBackBu);
			buttonlist.add(engunApprovedBu);
			buttonlist.add(qachecksBu);
			buttonlist.add(exceptionfbBu);
			buttonlist.add(exportPanelBu);

			drawtoolbar(buttonlist, iconsurl, wstart, Bwidth);
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
			int wstart = 10;
			int Bwidth = 170;
			buttonlist = new ArrayList<>();
			iconsurl = new ArrayList<>();

			qaReviewDataBu = new JButton("Quality Data Review");
			iconsurl.add("/Resources/reviews.jpg");
			qaFeedBackBu = new JButton("Quality Feedback");
			iconsurl.add("/Resources/feedback-icon.png");
			qUnApprovedBu = new JButton("Quality UnApproved");
			iconsurl.add("/Resources/approved.png");
			qaexceptionBu = new JButton("Quality Exception");
			iconsurl.add("/Resources/exception.png");
			buttonlist.add(qaReviewDataBu);
			buttonlist.add(qaFeedBackBu);
			buttonlist.add(qUnApprovedBu);
			buttonlist.add(qaexceptionBu);
			drawtoolbar(buttonlist, iconsurl, wstart, Bwidth);
		}

	}

	private void drawtoolbar(List<JButton> buttons, List<String> iconsurl, int wstart, int Bwidth)
	{
		try
		{
			Image img;
			toolBar.addSeparator();
			for(int i = 0; i < buttons.size(); i++)
			{
				buttons.get(i).setBounds(wstart + ((i + 1) * Bwidth), 5, Bwidth - 5, 32);
				buttons.get(i).setForeground(new Color(25, 25, 112));
				// tlunApprovedBu.setBackground(new Color(255, 255, 255));
				buttons.get(i).setFont(new Font("Herman", Font.PLAIN, 11));
				buttons.get(i).addActionListener(this);
				buttons.get(i).setToolTipText(buttons.get(i).getText());
				buttons.get(i).setOpaque(false);
				buttons.get(i).setBorder(BorderFactory.createEmptyBorder());
				buttons.get(i).setVerticalTextPosition(SwingConstants.BOTTOM);
				buttons.get(i).setHorizontalTextPosition(SwingConstants.CENTER);

				img = ImageIO.read(getClass().getResource(iconsurl.get(i)));
				buttons.get(i).setIcon(new ImageIcon(img));
				buttons.get(i).setIconTextGap(5);
				toolBar.add(buttons.get(i));
				toolBar.addSeparator();
			}
			mainpnl.add(toolBar);
			add(mainpnl);
			add(tabspanel);

		}catch(Exception e)
		{
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

	public void updateFlags()
	{
		long userRole = userDTO.getGrmRole().getId();
		long userGroup = userDTO.getGrmGroup().getId();
		ArrayList<String> flags = new ArrayList<String>();
		flags = ParaQueryUtil.getAlerts(userDTO.getId(), userGroup, userRole);

		// TL Screens
		if(tlfeedBack != null)
			tlfeedBack.updateFlags(flags);
		if(reviewData != null)
			reviewData.updateFlags(flags);
		if(tlunApprovedPanel != null)
			tlunApprovedPanel.updateFlags(flags);
		// Eng Screens
		if(engfeedBack != null)
			engfeedBack.updateFlags(flags);
		if(engunApprovedPanel != null)
			engunApprovedPanel.updateFlags(flags);
		if(developement != null)
			developement.updateFlags(flags);
		if(qachecks != null)
			qachecks.updateFlags(flags);
		if(exceptionfb != null)
			exceptionfb.updateFlags(flags);
		// QA Screens
		if(qaReviewData != null)
			qaReviewData.updateFlags(flags);
		if(qaFeedBack != null)
			qaFeedBack.updateFlags(flags);
		if(qUnApproved != null)
			qUnApproved.updateFlags(flags);
		if(qaexception != null)
			qaexception.updateFlags(flags);

	}

	@Override
	public void actionPerformed(ActionEvent event)
	{
		LongRunProcess longRunProcess = new LongRunProcess(event);
		longRunProcess.execute();
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
			if(event.getSource() == tlfeedBackBu)
			{
				if(tabbedPane.indexOfComponent(tlfeedBack) == -1)
				{
//					Loading.show();
					tlfeedBack = new TLFeedBack(userDTO);
					tabbedPane.addTab("TL Feedback", null, tlfeedBack, null);
					int index = tabbedPane.indexOfComponent(tlfeedBack);
					tabbedPane.setTabComponentAt(index, new ButtonTabComponent(tabbedPane));
					tabbedPane.setSelectedIndex(index);
//					Loading.close();
				}
			}
			if(event.getSource() == tlreviewDataBu)
			{
				if(tabbedPane.indexOfComponent(reviewData) == -1)
				{
//					Loading.show();
					reviewData = new TLReviewData(userDTO);
					tabbedPane.addTab("TL Review", null, reviewData, null);
					int index = tabbedPane.indexOfComponent(reviewData);
					tabbedPane.setTabComponentAt(index, new ButtonTabComponent(tabbedPane));
					tabbedPane.setSelectedIndex(index);
//					Loading.close();
				}
			}
			if(event.getSource() == tlunApprovedBu)
			{
				if(tabbedPane.indexOfComponent(tlunApprovedPanel) == -1)
				{
//					Loading.show();
					tlunApprovedPanel = new TLUnApprovedValue(userDTO);
					tabbedPane.addTab("TL UnApproved Value", null, tlunApprovedPanel, null);
					int index = tabbedPane.indexOfComponent(tlunApprovedPanel);
					tabbedPane.setTabComponentAt(index, new ButtonTabComponent(tabbedPane));
					tabbedPane.setSelectedIndex(index);
//					Loading.close();
				}
			}

			if(event.getSource() == developementBu)
			{
				if(tabbedPane.indexOfComponent(developement) == -1)
				{
//					Loading.show();
					developement = new Developement(userDTO);
					tabbedPane.addTab("Development", null, developement, null);
					int index = tabbedPane.indexOfComponent(developement);
					tabbedPane.setTabComponentAt(index, new ButtonTabComponent(tabbedPane));
					tabbedPane.setSelectedIndex(index);
//					Loading.close();
				}
			}
			if(event.getSource() == updateBu)
			{
				if(tabbedPane.indexOfComponent(update) == -1)
				{
//					Loading.show();
					update = new Update(userDTO);
					tabbedPane.addTab("NPI Update", null, update, null);
					int index = tabbedPane.indexOfComponent(update);
					tabbedPane.setTabComponentAt(index, new ButtonTabComponent(tabbedPane));
					tabbedPane.setSelectedIndex(index);
//					Loading.close();
				}
			}
			if(event.getSource() == engfeedBackBu)
			{
				if(tabbedPane.indexOfComponent(engfeedBack) == -1)
				{
//					Loading.show();
					engfeedBack = new EngFeedBack(userDTO);
					tabbedPane.addTab("Eng Data Feedback", null, engfeedBack, null);
					int index = tabbedPane.indexOfComponent(engfeedBack);
					tabbedPane.setTabComponentAt(index, new ButtonTabComponent(tabbedPane));
					tabbedPane.setSelectedIndex(index);
//					Loading.close();
				}
			}
			if(event.getSource() == engunApprovedBu)
			{
				if(tabbedPane.indexOfComponent(engunApprovedPanel) == -1)
				{
//					Loading.show();
					engunApprovedPanel = new EngUnApprovedValueFeedback(userDTO);
					tabbedPane.addTab("Eng UnApproved Value", null, engunApprovedPanel, null);
					int index = tabbedPane.indexOfComponent(engunApprovedPanel);
					tabbedPane.setTabComponentAt(index, new ButtonTabComponent(tabbedPane));
					tabbedPane.setSelectedIndex(index);
//					Loading.close();
				}
			}
			if(event.getSource() == qachecksBu)
			{
				if(tabbedPane.indexOfComponent(qachecks) == -1)
				{
//					Loading.show();
					qachecks = new QAChecks(userDTO);
					tabbedPane.addTab("QA Checks", null, qachecks, null);
					int index = tabbedPane.indexOfComponent(qachecks);
					tabbedPane.setTabComponentAt(index, new ButtonTabComponent(tabbedPane));
					tabbedPane.setSelectedIndex(index);
//					Loading.close();
				}
			}
			if(event.getSource() == exceptionfbBu)
			{
				if(tabbedPane.indexOfComponent(exceptionfb) == -1)
				{
//					Loading.show();
					exceptionfb = new ExceptionFB(userDTO);
					tabbedPane.addTab("QA ExceptionFeedback", null, exceptionfb, null);
					int index = tabbedPane.indexOfComponent(exceptionfb);
					tabbedPane.setTabComponentAt(index, new ButtonTabComponent(tabbedPane));
					tabbedPane.setSelectedIndex(index);
//					Loading.close();
				}
			}
			if(event.getSource() == exportPanelBu)
			{
				if(tabbedPane.indexOfComponent(exportPanel) == -1)
				{
//					Loading.show();
					exportPanel = new ComponentExporterPanel(userDTO);
					tabbedPane.addTab("QA Checks", null, exportPanel, null);
					int index = tabbedPane.indexOfComponent(exportPanel);
					tabbedPane.setTabComponentAt(index, new ButtonTabComponent(tabbedPane));
					tabbedPane.setSelectedIndex(index);
//					Loading.close();
				}
			}
			if(event.getSource() == qaReviewDataBu)
			{
				if(tabbedPane.indexOfComponent(qaReviewData) == -1)
				{
//					Loading.show();
					qaReviewData = new QAReviewData(userDTO);
					tabbedPane.addTab("Quality Data Review", null, qaReviewData, null);
					int index = tabbedPane.indexOfComponent(qaReviewData);
					tabbedPane.setTabComponentAt(index, new ButtonTabComponent(tabbedPane));
					tabbedPane.setSelectedIndex(index);
//					Loading.close();
				}
			}
			if(event.getSource() == qaFeedBackBu)
			{
				if(tabbedPane.indexOfComponent(qaFeedBack) == -1)
				{
//					Loading.show();
					qaFeedBack = new QAFeedBack(userDTO);
					tabbedPane.addTab("Quality Feedback", null, qaFeedBack, null);
					int index = tabbedPane.indexOfComponent(qaFeedBack);
					tabbedPane.setTabComponentAt(index, new ButtonTabComponent(tabbedPane));
					tabbedPane.setSelectedIndex(index);
//					Loading.close();
				}
			}
			if(event.getSource() == qUnApprovedBu)
			{
				if(tabbedPane.indexOfComponent(qUnApproved) == -1)
				{
//					Loading.show();
					qUnApproved = new QualityUnApprovedValue(userDTO);
					tabbedPane.addTab("Quality UnApproved", null, qUnApproved, null);
					int index = tabbedPane.indexOfComponent(qUnApproved);
					tabbedPane.setTabComponentAt(index, new ButtonTabComponent(tabbedPane));
					tabbedPane.setSelectedIndex(index);
//					Loading.close();
				}
			}
			if(event.getSource() == qaexceptionBu)
			{
				if(tabbedPane.indexOfComponent(qaexception) == -1)
				{
//					Loading.show();
					qaexception = new QAException(userDTO);
					tabbedPane.addTab("Quality Exception", null, qaexception, null);
					int index = tabbedPane.indexOfComponent(qaexception);
					tabbedPane.setTabComponentAt(index, new ButtonTabComponent(tabbedPane));
					tabbedPane.setSelectedIndex(index);
//					Loading.close();
				}
			}
			return null;
		}
	}

}
