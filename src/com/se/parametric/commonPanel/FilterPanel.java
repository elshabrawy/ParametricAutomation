package com.se.parametric.commonPanel;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.border.LineBorder;

import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.se.automation.db.ParametricQueryUtil;
import com.se.automation.db.QueryUtil;
import com.se.automation.db.SessionUtil;
//import com.se.automation.db.client.mapping.CheckFeature;
import com.se.automation.db.client.mapping.PlFeature;
import com.se.parametric.Loading;
import com.se.parametric.dba.ParaQueryUtil;
import com.se.parametric.dev.PdfLinks;
import com.se.parametric.dto.DocumentInfoDTO;
import com.toedter.calendar.JDateChooser;

public class FilterPanel extends JPanel implements ActionListener
{

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
	JLabel counts = new JLabel();

	public FilterPanel(String[] titleOfCombobox, ArrayList<Object[]> list, int width, int height,boolean isQA)
	{
		for(int i=0;i<list.size();i++){
			for(int j=0;j<list.get(i).length;j++){
				if(list.get(i)[j].toString().equals("NPI Transferred")||list.get(i)[j].toString().equals("NPI Update")){
					list.get(i)[j]="NPI";
				}
			}
		}
		this.filterList = list;
		this.setLayout(null);
//		counts.setText("count is " + list.size());
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
		datePanel.setBounds(0, 0, width, 60);
		add(datePanel);
		jDateChooser1.setBounds(232, 21, 91, 20);
		jDateChooser1.setDate(new java.util.Date());
		jDateChooser2.setBounds(473, 21, 91, 20);
		jDateChooser2.setDate(new java.util.Date());
		datePanel.setLayout(null);
		jDateChooser1.setEnabled(false);
		jDateChooser2.setEnabled(false);
		datePanel.add(jDateChooser1);
		datePanel.add(jDateChooser2);
		JLabel lblNewLabel = new JLabel("Date From");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblNewLabel.setBounds(118, 27, 73, 14);
		datePanel.add(lblNewLabel);
		JLabel lblNewLabel_1 = new JLabel("Date To");
		lblNewLabel_1.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblNewLabel_1.setBounds(379, 27, 46, 14);
		datePanel.add(lblNewLabel_1);
		checkDate = new JCheckBox("Select Period");
		checkDate.setFont(new Font("Tahoma", Font.BOLD, 11));
		checkDate.setBounds(678, 18, 117, 23);
		checkDate.addActionListener(this);
		datePanel.add(checkDate);
		JPanel comboPanel = new JPanel();
		comboPanel.setBackground(new Color(102, 204, 204));
		comboPanel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		comboPanel.setBounds(0, 60, width, height - 60);
		comboPanel.setLayout(null);
		int comboRows = (int) Math.ceil(titleOfCombobox.length * 1.0 / 2);
		int initX = (width - (2 * (260))) / 3;
		int initY = ((height - 100) - (comboRows * 25)) / (comboRows + 1);
		int xPlus = 0, yPlus = 0;
		for(int i = 0; i < titleOfCombobox.length; i++)
		{
			filterLabels[i] = new JLabel(titleOfCombobox[i]);
			filterLabels[i].setFont(new Font("Tahoma", Font.BOLD, 11));
			int xPos = initX + xPlus;
			int yPos = initY + yPlus;
			filterLabels[i].setBounds(xPos, yPos, 105, 25);
			comboPanel.add(filterLabels[i]);
			comboBoxItems[i] = new JComboBox(result.get(i));
			xPos = initX + xPlus + 130;
			yPos = initY + yPlus;
			comboBoxItems[i].setBounds(xPos, yPos, 130, 25);
			comboBoxItems[i].setSelectedItem("All");
			comboPanel.add(comboBoxItems[i]);
			if((i % 2) == 0)
			{
				xPlus += (260 + initX);
			}
			else
			{
				yPlus += (25 + initY);
				xPlus = 0;
			}
		}
		for(int i = 0; i < titleOfCombobox.length; i++)
		{
			comboBoxItems[i].addActionListener(this);
		}
		filterButton.setFont(new Font("Tahoma", Font.BOLD, 11));
		filterButton.setBounds(width / 2 - 120, height - 100, 110, 30);
		refreshButton.setFont(new Font("Tahoma", Font.BOLD, 11));
		refreshButton.setBounds(width / 2 + 10, height - 100, 110, 30);
		comboPanel.add(refreshButton);
		comboPanel.add(filterButton);
		if(isQA)
		{
			addsummary.setFont(new Font("Tahoma", Font.BOLD, 11));
			addsummary.setBounds(width / 2 + 140, height - 100, 130, 30);
			comboPanel.add(addsummary);
		}
		counts.setBounds(width - 100, height - 100, 100, 30);
		comboPanel.add(counts);
		this.add(comboPanel);
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
			counts.setText("count is " + result.size());
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

//	public static void main(String args[])
//	{
//		Session session=SessionUtil.getSession();
//		CheckFeature ch=new CheckFeature(0l, (PlFeature) session.createCriteria(PlFeature.class).add(Restrictions.eq("id", 15665l)).uniqueResult(), 10l);
//		session.save(ch);
//		session.close();
//	}
}
