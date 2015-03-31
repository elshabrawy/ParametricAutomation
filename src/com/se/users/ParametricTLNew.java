package com.se.users;

import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JToolBar;

public class ParametricTLNew
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
		ArrayList<ArrayList<Object>> result =new ArrayList<ArrayList<Object>>();
		JToolBar toolBar = new JToolBar("");
		toolBar.setFloatable(false);
		int wstart = 10;
		int Bwidth = 170;
		ArrayList buttonlist = new ArrayList<>();
		ArrayList iconsurl = new ArrayList<>();
			buttonlist = new ArrayList<>();
		iconsurl = new ArrayList<>();

		JButton tlreviewDataBu = new JButton("TL Data Review");
		iconsurl.add("/Resources/reviews.jpg");
		JButton tlfeedBackBu = new JButton("TL Data FeedBack");
		iconsurl.add("/Resources/feedback-icon.png");
		JButton tlunApprovedBu = new JButton("UnApproved Value");
		iconsurl.add("/Resources/approved.png");
		buttonlist.add(tlreviewDataBu);
		buttonlist.add(tlfeedBackBu);
		buttonlist.add(tlunApprovedBu);
		result.add(buttonlist);
		result.add(iconsurl);
		return result;
//		drawtoolbar(buttonlist, iconsurl, wstart, Bwidth,toolBar);
		
	}

}