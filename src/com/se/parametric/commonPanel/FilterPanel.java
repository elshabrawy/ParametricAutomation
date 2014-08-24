package com.se.parametric.commonPanel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;

import com.toedter.calendar.JDateChooser;

//import com.se.automation.db.client.mapping.CheckFeature;

public class FilterPanel extends JPanel implements ActionListener
{

	private static final int GAP = 7; // Default gap btwn components.
	JCheckBox checkDate = null;
	public JComboBox[] comboBoxItems = null;
	public JDateChooser jDateChooser1 = new JDateChooser();
	public JDateChooser jDateChooser2 = new JDateChooser();
	JPanel datePanel;
	public Date startDate = null;
	public Date endDate = null;
	public JButton filterButton = new JButton("Do Filter");
	public JButton refreshButton = new JButton("Refresh Filter");
	public JButton addsummary = new JButton("Add to Summary");
	public ArrayList<Object[]> filterList;

	// JLabel counts = new JLabel();
	JPanel allFilter = new JPanel();
	JXTaskPane taskpane = new JXTaskPane();
	JXTaskPaneContainer taskpanecontainer = new JXTaskPaneContainer();

	public FilterPanel(String[] titleOfCombobox, ArrayList<Object[]> list, boolean isQA)

	{
		Dimension labelDim=new Dimension(100, 20);
		Dimension comboDim=new Dimension(150, 20);
		for(int i = 0; i < list.size(); i++)
		{
			for(int j = 0; j < list.get(i).length; j++)
			{
				if(list.get(i)[j].toString().equals("NPI Transferred")
						|| list.get(i)[j].toString().equals("NPI Update"))

				{
					list.get(i)[j] = "NPI";
				}
			}
		}
		this.filterList = list;
		// this.setLayout(null);
		// counts.setText("count is " + list.size());
		ArrayList<Object[]> result;
		if(list.isEmpty())
		{
			result = new ArrayList<Object[]>();
			for(int i = 0; i < titleOfCombobox.length; i++)
			{
				Object[] row = new Object[1];
				row[0] = "";
				result.add(row);
			}
		}
		else
		{
			result = getDistinct(list);
		}
		JLabel filterLabels[] = new JLabel[titleOfCombobox.length];
		comboBoxItems = new JComboBox[titleOfCombobox.length];
		datePanel = new JPanel();
		datePanel.setBackground(new Color(255, 240, 245));
		datePanel.setBorder(new LineBorder(new Color(0, 0, 0)));
		// datePanel.setBounds(0, 0, width, 60);
		// add(datePanel);

		jDateChooser1.setBounds(232, 21, 91, 20);
		jDateChooser1.setDate(new java.util.Date());
		jDateChooser2.setBounds(473, 21, 91, 20);
		jDateChooser2.setDate(new java.util.Date());
		datePanel.setLayout(null);
		jDateChooser1.setEnabled(false);
		jDateChooser2.setEnabled(false);
		datePanel.add(jDateChooser1);
		datePanel.add(jDateChooser2);
		JLabel lblNewLabel = new JLabel("From:");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 11));
		// lblNewLabel.setBounds(118, 27, 73, 14);
		datePanel.add(lblNewLabel);
		JLabel lblNewLabel_1 = new JLabel("To:");

		lblNewLabel_1.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblNewLabel_1.setBounds(379, 27, 46, 14);
		datePanel.add(lblNewLabel_1);
		checkDate = new JCheckBox("Select Period");
		checkDate.setBorder(new EmptyBorder(2, 0, 2, 18));
		checkDate.setFont(new Font("Tahoma", Font.BOLD, 11));
		checkDate.setBounds(678, 18, 117, 23);
		checkDate.addActionListener(this);
		datePanel.add(checkDate);

		JPanel comboPanel = new JPanel();
		comboPanel.setBackground(new Color(102, 204, 204));
		comboPanel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		// comboPanel.setBounds(0, 60, width, height - 60);
		int x = 0;
		if(titleOfCombobox.length % 4 == 0)
		{
			x = titleOfCombobox.length / 2;
		}
		else
		{
			x = (titleOfCombobox.length / 2) + 1;
		}

		System.out.println("x is " + x);
		comboPanel.setLayout(new GridLayout(1, 2));
		int comboRows = (int) Math.ceil(titleOfCombobox.length * 1.0 / 2);
		// int initX = (width - (2 * (260))) / 3;
		// int initY = ((height - 100) - (comboRows * 25)) / (comboRows + 1);
		// int xPlus = 0, yPlus = 0;

		filterButton.setFont(new Font("Tahoma", Font.BOLD, 11));
		Color color = new Color(88, 130, 250);


		// filterButton.setBounds(width / 2 - 120, height - 100, 110, 30);
		refreshButton.setFont(new Font("Tahoma", Font.BOLD, 11));
		// refreshButton.setBounds(width / 2 + 10, height - 100, 110, 30);
		if(isQA)
		{
			addsummary.setFont(new Font("Tahoma", Font.BOLD, 11));
			// addsummary.setBounds(width / 2 + 140, height - 100, 130, 30);
			comboPanel.add(addsummary);
		}

		JPanel panel = new JPanel();
		panel.setLayout(null);
		panel.setOpaque(false);
		JButton b = new JButton("Done");
		b.setBounds(100, 100, 50, 30);
		panel.add(b);

		// panel.setBounds(0, 0, width, 300);

		allFilter.setLayout(new GridBagLayout());

		GBHelper pos = new GBHelper();

		checkDate.setPreferredSize(labelDim);
		lblNewLabel.setPreferredSize(labelDim);
		jDateChooser1.setPreferredSize(comboDim);
		lblNewLabel_1.setPreferredSize(labelDim);
		jDateChooser2.setPreferredSize(comboDim);

		allFilter.add(checkDate, pos.nextCol());
		allFilter.add(new Gap(GAP), pos.nextCol());
		allFilter.add(lblNewLabel, pos);
		allFilter.add(new Gap(GAP), pos.nextCol());
		allFilter.add(jDateChooser1, pos.nextCol().expandW());
		allFilter.add(new Gap(GAP), pos.nextCol());
		allFilter.add(lblNewLabel_1, pos.nextCol());
		allFilter.add(new Gap(GAP), pos.nextCol());
		allFilter.add(jDateChooser2, pos.nextCol().expandW());

		for(int i = 0; i < titleOfCombobox.length; i++)
		{

			filterLabels[i] = new JLabel(titleOfCombobox[i]);
			filterLabels[i].setFont(new Font("Tahoma", Font.BOLD, 11));
			// int xPos = initX + xPlus;
			// int yPos = initY + yPlus;
			// filterLabels[i].setBounds(xPos, yPos, 105, 25);
			// comboPanel.add(filterLabels[i]);
			comboBoxItems[i] = new JComboBox(result.get(i));
			filterLabels[i].setPreferredSize(labelDim);
			if((i % 2) == 0)
			{
				allFilter.add(new Gap(2 * GAP), pos.nextRow());	
				allFilter.add(filterLabels[i], pos.nextRow().nextCol().nextCol().width(1));			

				allFilter.add(new Gap(GAP), pos.nextCol());
				allFilter.add(filterLabels[i], pos.nextRow().nextCol().nextCol());
			}
			else
			{
				allFilter.add(filterLabels[i], pos.nextCol().width(1));	
			}
			allFilter.add(new Gap(GAP), pos.nextCol());
			comboBoxItems[i].setSelectedItem("All");		

			comboBoxItems[i].setPreferredSize(comboDim);
			comboBoxItems[i].setPrototypeDisplayValue("XXXXXXXXXXXXXXXXXXXX");
			allFilter.add(comboBoxItems[i], pos.nextCol().width(2));
			allFilter.add(new Gap(GAP), pos.nextCol());
			comboBoxItems[i].addActionListener(this);
		}
JPanel buttonPanel=new JPanel();
//buttonPanel
		allFilter.add(new Gap(10), pos.nextRow().expandW());
		filterButton.setFont(new Font("Tahoma", Font.BOLD, 11));
		refreshButton.setFont(new Font("Tahoma", Font.BOLD, 11));
		
		buttonPanel.add(refreshButton);
		buttonPanel.add(filterButton);
		if(isQA)
		{
			addsummary.setFont(new Font("Tahoma", Font.BOLD, 11));
			buttonPanel.add(addsummary);
		}

		// allFilter.add(new Gap(GAP), pos.nextRow());
		// allFilter.add(comboPanel, pos.nextRow().expandW());
		// allFilter.add(new Gap(GAP), pos.nextCol());
		// allFilter.add(refreshButton, pos.nextCol());
		// allFilter.add(new Gap(GAP), pos.nextRow().nextCol().expandW());
//		allFilter.add(new Gap(2 * GAP), pos.nextRow());	
		allFilter.add(buttonPanel, pos.nextRow().width(7));	
		taskpane.add(allFilter);
		// taskpane.setBounds(0, 0, width, height);
		taskpane.setTitle("Filter Panel");
		taskpane.setIcon(new ImageIcon("Resources/filter.png"));
		this.add(taskpane);

	}

	public ArrayList<Object[]> getDistinct(ArrayList<Object[]> list)
	{
		ArrayList<Object[]> result = new ArrayList<Object[]>();
		LinkedHashSet[] sets = new LinkedHashSet[list.get(0).length];
		for(int i = 0; i < list.get(0).length; i++)
		{
			sets[i] = new LinkedHashSet();
			sets[i].add("All");
		}
		for(int i = 0; i < list.size(); i++)
		{
			for(int j = 0; j < list.get(0).length; j++)
			{
				sets[j].add(list.get(i)[j]);
			}
		}
		for(int i = 0; i < list.get(0).length; i++)
		{
			result.add(sets[i].toArray());
		}
		return result;
	}

	public ArrayList<Object[]> getFilteredData(ArrayList<Object[]> list)
	{

		ArrayList<Object[]> result = new ArrayList<Object[]>();
		String[] data = new String[list.get(0).length];
		boolean flag = false;
		for(int i = 0; i < data.length; i++)
		{
			System.out.println("" + comboBoxItems[i].getSelectedItem().toString());
			data[i] = comboBoxItems[i].getSelectedItem().toString();
		}
		for(int i = 0; i < list.size(); i++)
		{
			DD: for(int j = 0; j < data.length; j++)
			{
				if(!data[j].equals("All"))
				{
					if(!list.get(i)[j].equals(data[j]))
					{
						flag = true;
						break DD;
					}
				}
			}
			if(!flag)
			{
				result.add(list.get(i));
			}
			else
			{
				flag = false;
			}
		}
		return result;
	}

	public void refreshFilters()
	{

		if((filterList == null) || (filterList.size() < 1))
		{
			for(int i = 0; i < comboBoxItems.length; i++)
			{
				JComboBox combo = comboBoxItems[i];
				DefaultComboBoxModel comboModel = new DefaultComboBoxModel();
				combo.removeActionListener(this);
				combo.removeAllItems();
			}
		}
		else
		{
			ArrayList<Object[]> allFiltersData = getDistinct(filterList);
			for(int i = 0; i < comboBoxItems.length; i++)
			{
				JComboBox combo = comboBoxItems[i];
				DefaultComboBoxModel comboModel = new DefaultComboBoxModel();
				combo.removeActionListener(this);
				combo.removeAllItems();
				if(allFiltersData != null)
				{
					Object[] filterData = allFiltersData.get(i);
					if(filterData != null)
					{
						for(int j = 0; j < filterData.length; j++)
						{
							comboModel.addElement(filterData[j]);
						}
						combo.setModel(comboModel);
					}
				}
				combo.addActionListener(this);
			}
		}

	}

	public void actionPerformed(ActionEvent event)
	{
		Object obj = event.getSource();
		if(obj instanceof JCheckBox)
		{
			JCheckBox check = (JCheckBox) obj;
			if(check.isSelected())
			{
				jDateChooser1.setEnabled(true);
				jDateChooser2.setEnabled(true);
			}
			else
			{
				jDateChooser1.setEnabled(false);
				jDateChooser2.setEnabled(false);
			}
		}
		else if(obj instanceof JComboBox)
		{
			ArrayList<Object[]> result = getDistinct(getFilteredData(filterList));
			// counts.setText("count is " + result.size());
			String initial[] = new String[comboBoxItems.length];
			for(int j = 0; j < comboBoxItems.length; j++)
			{
				initial[j] = comboBoxItems[j].getSelectedItem().toString();
				comboBoxItems[j].removeActionListener(this);
				comboBoxItems[j].removeAllItems();
			}
			for(int j = 0; j < comboBoxItems.length; j++)
			{

				for(int k = 0; k < result.get(j).length; k++)
				{
					comboBoxItems[j].addItem(result.get(j)[k]);
					System.out.println("" + j);
				}
				comboBoxItems[j].setSelectedItem(initial[j]);
				comboBoxItems[j].addActionListener(this);
			}
		}
	}

	// public static void main(String args[])
	// {
	// Session session=SessionUtil.getSession();
	// CheckFeature ch=new CheckFeature(0l, (PlFeature) session.createCriteria(PlFeature.class).add(Restrictions.eq("id", 15665l)).uniqueResult(),
	// 10l);
	// session.save(ch);
	// session.close();
	// }
}
