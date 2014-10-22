package com.se.parametric.commonPanel;

import java.awt.Color;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import com.se.parametric.MainWindow;
import com.se.parametric.dto.GrmUserDTO;

public class AlertsPanel extends JPanel
{
	private AlertComponent npiPdf, newPdf, backlogPdf, dataFb, approvedNew, approvedNpi,
			approvedNewFb, approvedNpiFb/* , tlReview */;

	public AlertsPanel(GrmUserDTO userDTO)
	{
//		updateFlags(MainWindow.flags);
		BoxLayout boxLayout = new BoxLayout(this, BoxLayout.PAGE_AXIS);
		this.setLayout(boxLayout);
		this.setBackground(new Color(211, 211, 211));
		
		npiPdf = new AlertComponent("NPI PDFs");
		newPdf = new AlertComponent("New PDFs");
		backlogPdf = new AlertComponent("Backlog PDFs");
		dataFb = new AlertComponent("Data FB");
		approvedNew = new AlertComponent("App New");
		approvedNpi = new AlertComponent("App NPI");
		approvedNewFb = new AlertComponent("App FB New");
		approvedNpiFb = new AlertComponent("App FB NPI");
		updateFlags();
		this.add(npiPdf);
		this.add(Box.createVerticalStrut(15));
		this.add(newPdf);
		this.add(Box.createVerticalStrut(15));
		this.add(backlogPdf);
		this.add(Box.createVerticalStrut(15));
		this.add(dataFb);
		this.add(Box.createVerticalStrut(15));
		this.add(approvedNew);
		this.add(Box.createVerticalStrut(15));
		this.add(approvedNpi);
		this.add(Box.createVerticalStrut(15));
		this.add(approvedNpiFb);
		this.add(Box.createVerticalStrut(15));
		this.add(approvedNewFb);
		this.add(Box.createVerticalStrut(15));
	}

	public void updateFlags()
	{
		npiPdf.setCount(MainWindow.flags.get(0));
		newPdf.setCount(MainWindow.flags.get(1));
		backlogPdf.setCount(MainWindow.flags.get(2));
		dataFb.setCount(MainWindow.flags.get(3));
		approvedNew.setCount(MainWindow.flags.get(4));
		approvedNpi.setCount(MainWindow.flags.get(5));
		approvedNewFb.setCount(MainWindow.flags.get(6));
		approvedNpiFb.setCount(MainWindow.flags.get(7));
	}

}
