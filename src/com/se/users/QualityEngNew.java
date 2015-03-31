package com.se.users;

import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JToolBar;

public class QualityEngNew
extends UserType
{

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

		JButton qaReviewDataBu = new JButton("Quality Data Review");
		iconsurl.add("/Resources/reviews.jpg");
		JButton qaFeedBackBu = new JButton("Quality Feedback");
		iconsurl.add("/Resources/feedback-icon.png");
		JButton qUnApprovedBu = new JButton("Quality UnApproved");
		iconsurl.add("/Resources/approved.png");
		JButton qaexceptionBu = new JButton("Quality Exception");
		iconsurl.add("/Resources/exception.png");
		buttonlist.add(qaReviewDataBu);
		buttonlist.add(qaFeedBackBu);
		buttonlist.add(qUnApprovedBu);
		buttonlist.add(qaexceptionBu);
		result.add(buttonlist);
		result.add(iconsurl);
		return result;
//		drawtoolbar(buttonlist, iconsurl, wstart, Bwidth,toolBar);
		
	}

}
