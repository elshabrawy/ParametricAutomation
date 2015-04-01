package com.se.users.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;

import com.se.Quality.QAChecks;
import com.se.parametric.ButtonTabComponent;
import com.se.parametric.dev.ComponentExporterPanel;
import com.se.parametric.dev.Developement;
import com.se.parametric.dev.Update;
import com.se.parametric.dto.GrmUserDTO;
import com.se.parametric.fb.EngFeedBack;
import com.se.parametric.fb.ExceptionFB;
import com.se.parametric.unappValue.EngUnApprovedValueFeedback;

public abstract class Gui  implements ActionListener
{
	public JTabbedPane tabbedPane;
	public List<JButton> buttonlist;
	public List<String> iconsurl;
	public JToolBar toolBar;
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

	public void drawtoolbar(int wstart, int Bwidth, JToolBar toolBar)
	{
		{
			try
			{
				Image img;
				toolBar.addSeparator();
				for(int i = 0; i < buttonlist.size(); i++)
				{
					buttonlist.get(i).setBounds(wstart + ((i + 1) * Bwidth), 5, Bwidth - 5, 32);
					buttonlist.get(i).setForeground(new Color(25, 25, 112));
					// tlunApprovedBu.setBackground(new Color(255, 255, 255));
					buttonlist.get(i).setFont(new Font("Herman", Font.PLAIN, 11));
					// buttons.get(i).addActionListener(this);
					buttonlist.get(i).setToolTipText((buttonlist.get(i)).getText());
					// buttons.get(i).setOpaque(false);
					// buttons.get(i).setBorder(BorderFactory.createEmptyBorder());
					buttonlist.get(i).setVerticalTextPosition(SwingConstants.BOTTOM);
					buttonlist.get(i).setHorizontalTextPosition(SwingConstants.CENTER);
					img = ImageIO.read(getClass().getResource(iconsurl.get(i)));
					buttonlist.get(i).setIcon(new ImageIcon(img));
					buttonlist.get(i).setIconTextGap(5);
					toolBar.add(buttonlist.get(i));
					toolBar.addSeparator();
				}
			}catch(Exception e)
			{
				// System.out.println(e.getMessage());
				e.printStackTrace();
			}
		}
	}
}
