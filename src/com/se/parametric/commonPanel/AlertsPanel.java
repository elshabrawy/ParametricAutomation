package com.se.parametric.commonPanel;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.border.SoftBevelBorder;

import com.se.automation.db.ParametricQueryUtil;
import com.se.parametric.dba.ParaQueryUtil;
import com.se.parametric.dto.GrmUserDTO;
import com.se.parametric.fb.TLFeedBack;
import com.se.parametric.review.TLReviewData;
import com.se.parametric.unappValue.TLUnApprovedValue;
import com.sun.star.drawing.Alignment;

public class AlertsPanel extends JPanel
{
	JLabel npiLabel, fbLabel, npiValue, fbValue, newLabel, newValue, backlogLabel, backlogValue, appLabel, appValue, appFBLabel, appFBValue, tlRLabel, tlRValue;

	GrmUserDTO userDTO;

	public AlertsPanel(GrmUserDTO userDTO)
	{
		this.userDTO = userDTO;
		this.setLayout(null);
		int y = 0;
		npiLabel = new JLabel("NPI :");
		setLabelProperties(npiLabel, y, false);
		y += 25;
		npiValue = new JLabel("" + 0);
		setLabelProperties(npiValue, y, true);
		y += 32;
		newLabel = new JLabel("New :");
		setLabelProperties(newLabel, y, false);
		y += 25;
		newValue = new JLabel("" + 0);
		setLabelProperties(newValue, y, true);
		y += 32;
		backlogLabel = new JLabel("Backlog :");
		setLabelProperties(backlogLabel, y, false);
		y += 25;
		backlogValue = new JLabel("" + 0);
		setLabelProperties(backlogValue, y, true);
		y += 32;
		fbLabel = new JLabel("Data FB :");
		setLabelProperties(fbLabel, y, false);
		y += 25;
		fbValue = new JLabel("" + 0);
		setLabelProperties(fbValue, y, true);
		y += 32;
		appLabel = new JLabel("App Values :");
		setLabelProperties(appLabel, y, false);
		y += 25;
		appValue = new JLabel("" + 0);
		setLabelProperties(appValue, y, true);
		y += 32;
		appFBLabel = new JLabel("App Values FB :");
		setLabelProperties(appFBLabel, y, false);
		y += 25;
		appFBValue = new JLabel("" + 0);
		setLabelProperties(appFBValue, y, true);

		y += 32;
		tlRLabel = new JLabel("TL Review:");
		setLabelProperties(tlRLabel, y, false);
		y += 25;
		tlRValue = new JLabel("" + 0);
		setLabelProperties(tlRValue, y, true);

		setBackground(new Color(211, 211, 211));
		setBorder(new SoftBevelBorder(BevelBorder.RAISED, null, null, null, null));

		this.add(npiLabel);
		this.add(newLabel);
		this.add(backlogLabel);
		this.add(fbLabel);
		add(npiValue);
		add(newValue);
		add(backlogValue);
		add(fbValue);
		// long userRole = 3;
		// long userGroup = 1;
		long userRole = userDTO.getGrmRole().getId();
		long userGroup = userDTO.getGrmGroup().getId();
		if(userRole == 3 && userGroup == 1)
		{
			System.out.println(" No Approved");
		}
		else
		{
			add(appLabel);
			add(appValue);
		}
		
		add(appFBLabel);
		add(appFBValue);

	}

	private void setLabelProperties(JLabel label, int y, boolean isValue)
	{
		label.setBounds(10, y, 90, 29);
		label.setFont(new Font("Tahoma", Font.BOLD, 11));
		if(isValue)
		{
			label.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
			label.setBackground(Color.green);
			label.setHorizontalAlignment(0);
		}
		else
		{
			label.setForeground(Color.BLUE);
		}

	}

	public void updateFlags(ArrayList<String> flags)
	{
		// ArrayList<String> flags=ParaQueryUtil.getAlerts(userDTO.getId(),1,3);
		npiValue.setText(flags.get(0));
		npiValue.setForeground(Color.RED);
		newValue.setText(flags.get(1));
		backlogValue.setText(flags.get(2));
		fbValue.setText(flags.get(3));
		fbValue.setForeground(Color.RED);
		appValue.setText(flags.get(4));
		appFBValue.setText(flags.get(5));
	}

}
