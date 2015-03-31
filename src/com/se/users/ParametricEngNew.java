package com.se.users;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToolBar;
import javax.imageio.ImageIO;

import com.se.parametric.ButtonTabComponent;
import com.se.parametric.dev.Developement;

public class ParametricEngNew 
extends UserType implements ActionListener
{
	JButton developementBu,updateBu,engfeedBackBu,engunApprovedBu,qachecksBu,exceptionfbBu,exportPanelBu;
	Developement developement;
	@Override
	public ArrayList<ArrayList<String>> doFilter()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<ArrayList<Object>> creatTabs()
	{
		ArrayList<ArrayList<Object>> result=new ArrayList<ArrayList<Object>>();
		JToolBar toolBar = new JToolBar("");
		toolBar.setFloatable(false);
		int wstart = 10;
		int Bwidth = 170;
		ArrayList buttonlist = new ArrayList<>();
		ArrayList iconsurl = new ArrayList<>();
		developementBu = new JButton("Developement");
		iconsurl.add("/Resources/development-icon.png");
		try
		{
			developementBu.setIcon(new ImageIcon(ImageIO.read(getClass().getResource("/Resources/development-icon.png"))));
		}catch(IOException e)
		{
			e.printStackTrace();
		}
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
		result.add(buttonlist);
		result.add(iconsurl);
		return result;
//		drawtoolbar(buttonlist, iconsurl, wstart, Bwidth,toolBar);
		
	}

	@Override
	public void actionPerformed(ActionEvent event)
	{
		if(event.getSource() == developementBu)
		{
//			if(tabbedPane.indexOfComponent(developement) == -1)
//			{

//				developement = new Developement(userDTO);
//				tabbedPane.addTab("Development", developement);
//				int index = tabbedPane.indexOfComponent(developement);
//				tabbedPane.setTabComponentAt(index, new ButtonTabComponent(tabbedPane));
//				tabbedPane.setSelectedIndex(index);
//			}
		}	
		
	}

}