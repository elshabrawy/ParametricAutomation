package com.se.GUI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.imageio.ImageIO;

import com.se.Quality.QAChecks;
import com.se.parametric.ButtonTabComponent;
import com.se.parametric.dev.ComponentExporterPanel;
import com.se.parametric.dev.Developement;
import com.se.parametric.dev.Update;
import com.se.parametric.dto.GrmUserDTO;
import com.se.parametric.fb.EngFeedBack;
import com.se.parametric.fb.ExceptionFB;
import com.se.parametric.unappValue.EngUnApprovedValueFeedback;

public class ParametricEngNew extends UserType implements ActionListener
{
	JButton developementBu, updateBu, engfeedBackBu, engunApprovedBu, qachecksBu, exceptionfbBu,
			exportPanelBu;
	Developement developement;
	Update update;
	EngFeedBack engfeedBack;
	EngUnApprovedValueFeedback engUnApprovedValueFeedback;
	QAChecks qAChecks;
	ExceptionFB exceptionFB;
	ComponentExporterPanel componentExporterPanel;
	GrmUserDTO userDTO;

	@Override
	public ArrayList<ArrayList<String>> doFilter()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<Object> creatTabs(GrmUserDTO userDTO)
	{
		this.userDTO = userDTO;
		ArrayList<Object> result = new ArrayList<Object>();
		toolBar = new JToolBar("");
		tabbedPane = new JTabbedPane();
		toolBar.setFloatable(false);
		int wstart = 10;
		int Bwidth = 170;
		buttonlist = new ArrayList<JButton>();
		iconsurl = new ArrayList<String>();
		developementBu = new JButton("Developement");
		developementBu.addActionListener(this);
		iconsurl.add("/Resources/development-icon.png");
		try
		{
			developementBu.setIcon(new ImageIcon(ImageIO.read(getClass().getResource(
					"/Resources/development-icon.png"))));
		}catch(IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		updateBu = new JButton("NPI Update");
		updateBu.addActionListener(this);

		iconsurl.add("/Resources/update-icon.png");
		engfeedBackBu = new JButton("Eng Data Feedback");
		engfeedBackBu.addActionListener(this);

		iconsurl.add("/Resources/feedback-icon.png");
		engunApprovedBu = new JButton("Eng UnApproved Value");
		engunApprovedBu.addActionListener(this);

		iconsurl.add("/Resources/approved.png");
		qachecksBu = new JButton("QA Checks");
		qachecksBu.addActionListener(this);

		iconsurl.add("/Resources/check-icon.png");
		exceptionfbBu = new JButton("QA ExceptionFeedback");
		exceptionfbBu.addActionListener(this);

		iconsurl.add("/Resources/exception.png");
		exportPanelBu = new JButton("Export");
		exportPanelBu.addActionListener(this);

		iconsurl.add("/Resources/export.png");

		buttonlist.add(developementBu);
		buttonlist.add(updateBu);
		buttonlist.add(engfeedBackBu);
		buttonlist.add(engunApprovedBu);
		buttonlist.add(qachecksBu);
		buttonlist.add(exceptionfbBu);
		drawtoolbar(10, 170, toolBar);
		result.add(toolBar);
		result.add(tabbedPane);
		return result;
		// drawtoolbar(buttonlist, iconsurl, wstart, Bwidth,toolBar);

	}

	@Override
	public void actionPerformed(ActionEvent event)
	{
		if(event.getSource() == developementBu)
		{
			if(tabbedPane.indexOfComponent(developement) == -1)
			{

				developement = new Developement(userDTO);
				tabbedPane.addTab("Development", developement);
				int index = tabbedPane.indexOfComponent(developement);
				tabbedPane.setTabComponentAt(index, new ButtonTabComponent(tabbedPane));
				tabbedPane.setSelectedIndex(index);
			}
		}
		if(event.getSource() == updateBu)
		{
			if(tabbedPane.indexOfComponent(update) == -1)
			{

				update = new Update(userDTO);
				tabbedPane.addTab("NPI Update", update);
				int index = tabbedPane.indexOfComponent(update);
				tabbedPane.setTabComponentAt(index, new ButtonTabComponent(tabbedPane));
				tabbedPane.setSelectedIndex(index);
			}
		}
		if(event.getSource() == engfeedBackBu)
		{
			if(tabbedPane.indexOfComponent(engfeedBack) == -1)
			{

				engfeedBack = new EngFeedBack(userDTO);
				tabbedPane.addTab("Eng Data Feedback", engfeedBack);
				int index = tabbedPane.indexOfComponent(engfeedBack);
				tabbedPane.setTabComponentAt(index, new ButtonTabComponent(tabbedPane));
				tabbedPane.setSelectedIndex(index);
			}
		}
		if(event.getSource() == engunApprovedBu)
		{
			if(tabbedPane.indexOfComponent(engUnApprovedValueFeedback) == -1)
			{

				engUnApprovedValueFeedback = new EngUnApprovedValueFeedback(userDTO);
				tabbedPane.addTab("Eng UnApproved Value", engUnApprovedValueFeedback);
				int index = tabbedPane.indexOfComponent(engUnApprovedValueFeedback);
				tabbedPane.setTabComponentAt(index, new ButtonTabComponent(tabbedPane));
				tabbedPane.setSelectedIndex(index);
			}
		}
		if(event.getSource() == qachecksBu)
		{
			if(tabbedPane.indexOfComponent(qAChecks) == -1)
			{

				qAChecks = new QAChecks(userDTO);
				tabbedPane.addTab("QA Checks", qAChecks);
				int index = tabbedPane.indexOfComponent(qAChecks);
				tabbedPane.setTabComponentAt(index, new ButtonTabComponent(tabbedPane));
				tabbedPane.setSelectedIndex(index);
			}
		}
		if(event.getSource() == exceptionfbBu)
		{
			if(tabbedPane.indexOfComponent(exceptionFB) == -1)
			{

				exceptionFB = new ExceptionFB(userDTO);
				tabbedPane.addTab("QA ExceptionFeedback", exceptionFB);
				int index = tabbedPane.indexOfComponent(exceptionFB);
				tabbedPane.setTabComponentAt(index, new ButtonTabComponent(tabbedPane));
				tabbedPane.setSelectedIndex(index);
			}
		}
		if(event.getSource() == exportPanelBu)
		{
			if(tabbedPane.indexOfComponent(componentExporterPanel) == -1)
			{

				componentExporterPanel = new ComponentExporterPanel(userDTO);
				tabbedPane.addTab("QA Checks", componentExporterPanel);
				int index = tabbedPane.indexOfComponent(componentExporterPanel);
				tabbedPane.setTabComponentAt(index, new ButtonTabComponent(tabbedPane));
				tabbedPane.setSelectedIndex(index);
			}
		}

	}

}