package com.se.parametric.commonPanel;

import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import com.se.parametric.dto.GrmUserDTO;

public class AlertsPanel extends JPanel
{
	private AlertComponent npiPdf, newPdf, backlogPdf, dataFb, approvedNew, approvedNpi,
			approvedNewFb, approvedNpiFb/* , tlReview */;

	public AlertsPanel(GrmUserDTO userDTO)
	{
		BoxLayout boxLayout = new BoxLayout(this, BoxLayout.PAGE_AXIS);
		this.setLayout(boxLayout);

		npiPdf = new AlertComponent("NPI PDFs");
		newPdf = new AlertComponent("New PDFs");
		backlogPdf = new AlertComponent("Backlog PDFs");
		dataFb = new AlertComponent("Data FB");
		approvedNew = new AlertComponent("App New");
		approvedNpi = new AlertComponent("App NPI");
		approvedNewFb = new AlertComponent("App FB New");
		approvedNpiFb = new AlertComponent("App FB NPI");

		this.add(npiPdf);
		this.add(Box.createVerticalStrut(2));
		this.add(newPdf);
		this.add(Box.createVerticalStrut(2));
		this.add(backlogPdf);
		this.add(Box.createVerticalStrut(2));
		this.add(dataFb);
		this.add(Box.createVerticalStrut(2));
		this.add(approvedNew);
		this.add(Box.createVerticalStrut(2));
		this.add(approvedNpi);
		this.add(Box.createVerticalStrut(2));
		this.add(approvedNpiFb);
		this.add(Box.createVerticalStrut(2));
		this.add(approvedNewFb);
		this.add(Box.createVerticalStrut(2));
	}

	public void updateFlags(ArrayList<String> flags)
	{
		npiPdf.setCount(flags.get(0));
		newPdf.setCount(flags.get(1));
		backlogPdf.setCount(flags.get(2));
		dataFb.setCount(flags.get(3));
		approvedNew.setCount(flags.get(4));
		approvedNpi.setCount(flags.get(5));
		approvedNewFb.setCount(flags.get(6));
		approvedNpiFb.setCount(flags.get(7));
	}

}
