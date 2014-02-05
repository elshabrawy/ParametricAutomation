package com.se.parametric;

import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.se.Quality.QAFeedBack;
import com.se.Quality.QAReviewData;
import com.se.Quality.QualityUnApprovedValue;
import com.se.parametric.dba.ParaQueryUtil;
import com.se.parametric.dev.ComponentExporterPanel;
import com.se.parametric.dev.Developement;
import com.se.parametric.dev.Update;
import com.se.parametric.dto.GrmUserDTO;
import com.se.parametric.fb.EngFeedBack;
import com.se.parametric.fb.TLFeedBack;
import com.se.parametric.review.TLReviewData;
import com.se.parametric.unappValue.EngUnApprovedValueFeedback;
import com.se.parametric.unappValue.TLUnApprovedValue;

public class MainPanel extends JPanel
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

	public MainPanel(GrmUserDTO userDTO, int width, int height)
	{
		this.userDTO = userDTO;
		setLayout(null);

		long userRole = userDTO.getGrmRole().getId();
		long userGroup = userDTO.getGrmGroup().getId();
		this.setBounds(0, 0, width, height);
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(10, 5, width, height);
		add(tabbedPane);
		add(new JButton("Ok"));

		// role 3 eng , role 1 tl
		if(userRole == 1 && userGroup != 101)
		{
			// parametric Leader
			tlfeedBack = new TLFeedBack(userDTO);
			reviewData = new TLReviewData(userDTO);
			tlunApprovedPanel = new TLUnApprovedValue(userDTO);
			tabbedPane.addTab("TL Data Review", null, reviewData, null);
			tabbedPane.addTab("TL Feedback", null, tlfeedBack, null);
			tabbedPane.addTab("TL UnApproved Review", null, tlunApprovedPanel, null);

		}
		else if(userRole == 3 && userGroup != 101)
		{
			// parametric eng

			engfeedBack = new EngFeedBack(userDTO);
			engunApprovedPanel = new EngUnApprovedValueFeedback(userDTO);
			developement = new Developement(userDTO);
			Update update = new Update(userDTO);
			ComponentExporterPanel exportPanel = new ComponentExporterPanel(userDTO);
			tabbedPane.addTab("Developement", null, developement, null);
			tabbedPane.addTab("NPI Update", null, update, null);
			tabbedPane.addTab("EngFeedback", null, engfeedBack, null);
			tabbedPane.addTab("EngUnApproved", null, engunApprovedPanel, null);
			tabbedPane.addTab("Export", null, exportPanel, null);

		}
		else if(userRole == 1 && userGroup == 101)
		{
			// Q leader
			qaReviewData = new QAReviewData(userDTO);
			tabbedPane.addTab("Quality Data Review", null, qaReviewData, null);
		}
		else if(userRole == 3 && userGroup == 101)
		{
			// Q eng
			System.out.println("Quality Eng");
			tlfeedBack = new TLFeedBack(userDTO);

			qaReviewData = new QAReviewData(userDTO);
			qaFeedBack=new QAFeedBack(userDTO);
			qUnApproved = new QualityUnApprovedValue(userDTO);

			tabbedPane.addTab("Quality Data Review", null, qaReviewData, null);
			tabbedPane.addTab("Quality Feedback", null, qaFeedBack, null);
			tabbedPane.addTab("Quality UnApproved", null, qUnApproved, null);

		}

	}

	public void updateFlags()
	{
		long userRole = userDTO.getGrmRole().getId();
		long userGroup = userDTO.getGrmGroup().getId();
		ArrayList<String> flags=new ArrayList<String>();
		flags = ParaQueryUtil.getAlerts(userDTO.getId(), userGroup, userRole);
		if(developement != null)
			developement.updateFlags(flags);
		if(reviewData != null)
			reviewData.updateFlags(flags);

		if(tlfeedBack != null)
			tlfeedBack.updateFlags(flags);
		if(tlunApprovedPanel != null)
			tlunApprovedPanel.updateFlags(flags);

		if(engfeedBack != null)
			engfeedBack.updateFlags(flags);
		if(engunApprovedPanel != null)
			engunApprovedPanel.updateFlags(flags);

		if(qaReviewData != null)
			qaReviewData.updateFlags(flags);

		if(qUnApproved != null)
			qUnApproved.updateFlags(flags);
	}

}