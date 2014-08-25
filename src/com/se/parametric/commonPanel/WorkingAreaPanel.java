package com.se.parametric.commonPanel;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.border.SoftBevelBorder;

import osheet.SheetPanel;

import com.se.parametric.dto.GrmUserDTO;

public class WorkingAreaPanel extends JPanel
{
	private JPanel centerPanel;
	private JPanel sidePanel;

	private AlertsPanel alertsPanel;

	private GrmUserDTO userDTO;

	public WorkingAreaPanel(GrmUserDTO userDTO)
	{
		this.userDTO = userDTO;
		this.alertsPanel = new AlertsPanel(this.userDTO);

		this.setLayout(new BorderLayout());

		this.centerPanel = new JPanel(new BorderLayout());
		this.sidePanel = new JPanel();
		sidePanel.setBorder(new SoftBevelBorder(BevelBorder.RAISED, null, null, null, null));
		BoxLayout boxLayout = new BoxLayout(this.sidePanel, BoxLayout.PAGE_AXIS);
		this.sidePanel.setLayout(boxLayout);
	}

	public void addComponentsToPanel()
	{
		this.add(sidePanel, BorderLayout.LINE_END);
		this.add(centerPanel, BorderLayout.CENTER);
	}

	public TablePanel getTablePanel(String[] headers)
	{
		TablePanel tablePanel = new TablePanel(headers);
		tablePanel.setBorder(new SoftBevelBorder(BevelBorder.RAISED, null, null, null, null));
		this.centerPanel.add(tablePanel, BorderLayout.CENTER);
		return tablePanel;
	}

	public FilterPanel getFilterPanel(String[] titleOfCombobox, ArrayList<Object[]> filterData,
			boolean isQA, ActionListener actionListener)
	{
		FilterPanel filterPanel = new FilterPanel(titleOfCombobox, filterData, isQA);
		filterPanel.filterButton.addActionListener(actionListener);
		filterPanel.refreshButton.addActionListener(actionListener);
		filterPanel.addsummary.addActionListener(actionListener);

		this.centerPanel.add(filterPanel, BorderLayout.PAGE_START);
		return filterPanel;
	}

	public void addButtonsPanel(ArrayList<String> buttonLabels, ActionListener actionListener)
	{
		ButtonsPanel buttonsPanel = new ButtonsPanel(buttonLabels);
		JButton buttons[] = buttonsPanel.getButtons();
		for(int i = 0; i < buttons.length; i++)
		{
			buttons[i].addActionListener(actionListener);
		}
		this.sidePanel.add(buttonsPanel);
		this.sidePanel.add(alertsPanel);
	}

	public void updateFlags(ArrayList<String> flags)
	{
		this.alertsPanel.updateFlags(flags);
	}

	public static void main(String[] args)
	{
	}

	public SheetPanel getSheet()
	{
		final SheetPanel sheetPanel = new SheetPanel();
		this.centerPanel.add(sheetPanel, BorderLayout.CENTER);

		this.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent arg0)
			{
			}

			@Override
			public void focusGained(FocusEvent arg0)
			{
				sheetPanel.activate();
			}
		});

		return sheetPanel;
	}
}
