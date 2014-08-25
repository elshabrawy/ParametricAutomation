package com.se.parametric.commonPanel;

import java.awt.BorderLayout;
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

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;

import com.toedter.calendar.JDateChooser;

//import com.se.automation.db.client.mapping.CheckFeature;

public class FilterPanel extends JXTaskPane implements ActionListener
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

	JPanel allFilter = new JPanel();
	JXTaskPane taskpane = new JXTaskPane();
	JXTaskPaneContainer taskpanecontainer = new JXTaskPaneContainer();

	public FilterPanel(String[] titleOfCombobox, ArrayList<Object[]> list, boolean isQA)

	{		
		Dimension labelDim = new Dimension(90, 20);
		Dimension comboDim = new Dimension(170, 23);
		Dimension btnDim = new Dimension(110, 32);
		Dimension checkDim = new Dimension(120, 20);
		Color bkColor=Color.LIGHT_GRAY;
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
//		datePanel = new JPanel();
//		datePanel.setBackground(new Color(255, 240, 245));
//		datePanel.setBorder(new LineBorder(new Color(0, 0, 0)));
//		jDateChooser1.setBounds(232, 21, 91, 20);
		jDateChooser1.setDate(new java.util.Date());
//		jDateChooser2.setBounds(473, 21, 91, 20);
		jDateChooser2.setDate(new java.util.Date());
//		datePanel.setLayout(null);
		jDateChooser1.setEnabled(false);
		jDateChooser2.setEnabled(false);
//		datePanel.add(jDateChooser1);
//		datePanel.add(jDateChooser2);
		JLabel lblFromDate = new JLabel("From:");
		lblFromDate.setFont(new Font("Tahoma", Font.BOLD, 11));
//		datePanel.add(lblFromDate);
		JLabel lblToDate = new JLabel("To:");

		lblToDate.setFont(new Font("Tahoma", Font.BOLD, 11));
//		lblToDate.setBounds(379, 27, 46, 14);
//		datePanel.add(lblToDate);
		checkDate = new JCheckBox("Select Period");
//		checkDate.setBorder(new EmptyBorder(2, 0, 2, 18));
		checkDate.setFont(new Font("Tahoma", Font.BOLD, 11));
		checkDate.setBackground(bkColor);
//		checkDate.setBounds(678, 18, 117, 23);
		checkDate.addActionListener(this);
//		datePanel.add(checkDate);
//		JPanel comboPanel = new JPanel();
//		comboPanel.setBackground(new Color(102, 204, 204));
//		comboPanel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
//		int x = 0;
//		if(titleOfCombobox.length % 4 == 0)
//		{
//			x = titleOfCombobox.length / 2;
//		}
//		else
//		{
//			x = (titleOfCombobox.length / 2) + 1;
//		}
//		System.out.println("x is " + x);
//		comboPanel.setLayout(new GridLayout(1, 2));
//		int comboRows = (int) Math.ceil(titleOfCombobox.length * 1.0 / 2);
//		filterButton.setFont(new Font("Tahoma", Font.BOLD, 11));
//		Color color = new Color(88, 130, 250);
//		refreshButton.setFont(new Font("Tahoma", Font.BOLD, 11));
//		if(isQA)
//		{
//			addsummary.setFont(new Font("Tahoma", Font.BOLD, 11));
//			comboPanel.add(addsummary);
//		}

//		JPanel panel = new JPanel();
//		panel.setLayout(null);
//		panel.setOpaque(false);
//		JButton b = new JButton("Done");
//		b.setBounds(100, 100, 50, 30);
//		panel.add(b);
		allFilter.setLayout(new GridBagLayout());
		GBHelper pos = new GBHelper();
		checkDate.setPreferredSize(checkDim);
		lblFromDate.setPreferredSize(labelDim);
		jDateChooser1.setPreferredSize(comboDim);
		lblToDate.setPreferredSize(labelDim);
		jDateChooser2.setPreferredSize(comboDim);
//		allFilter.add(new JLabel(""), pos.nextRow());
		allFilter.add(new Gap(15), pos.nextRow());
		allFilter.add(checkDate, pos.nextRow().nextCol());
//		allFilter.add(new Gap(2), pos.nextCol());
		allFilter.add(lblFromDate,  pos.nextCol());
//		allFilter.add(new Gap(GAP), pos.nextCol());
		allFilter.add(jDateChooser1, pos.nextCol());
		allFilter.add(new Gap(4*GAP), pos.nextCol());
		allFilter.add(lblToDate, pos.nextCol());
//		allFilter.add(new Gap(GAP), pos.nextCol());
		allFilter.add(jDateChooser2, pos.nextCol().expandW());
		for(int i = 0; i < titleOfCombobox.length; i++)
		{
			filterLabels[i] = new JLabel(titleOfCombobox[i]+":");
			filterLabels[i].setFont(new Font("Tahoma", Font.BOLD, 11));
			comboBoxItems[i] = new JComboBox(result.get(i));
			filterLabels[i].setPreferredSize(labelDim);
			if((i % 2) == 0)
			{
				allFilter.add(new Gap(GAP), pos.nextRow());
				allFilter.add(filterLabels[i], pos.nextRow().nextCol().nextCol());
			}
			else
			{
//				allFilter.add(new Gap(GAP), pos.nextCol());
				allFilter.add(filterLabels[i], pos.nextCol().width(1));
			}
//			allFilter.add(new Gap(2), pos.nextCol());
			comboBoxItems[i].setSelectedItem("All");

			comboBoxItems[i].setPreferredSize(comboDim);
//			comboBoxItems[i].setPrototypeDisplayValue("XXXXXXXXXXXXXXXXXXXX");
			allFilter.add(comboBoxItems[i], pos.nextCol());
			allFilter.add(new Gap(2*GAP), pos.nextCol());
			comboBoxItems[i].addActionListener(this);
		}
		JPanel buttonPanel = new JPanel();
		allFilter.add(new Gap(10), pos.nextRow().expandW());
		filterButton.setFont(new Font("Tahoma", Font.BOLD, 11));
		refreshButton.setFont(new Font("Tahoma", Font.BOLD, 11));
		filterButton.setPreferredSize(btnDim);
		refreshButton.setPreferredSize(btnDim);
		buttonPanel.add(filterButton);
		buttonPanel.add(refreshButton);

		if(isQA)
		{
			addsummary.setFont(new Font("Tahoma", Font.BOLD, 11));
			addsummary.setPreferredSize(btnDim);
			buttonPanel.add(addsummary);
		}
		buttonPanel.setBackground(bkColor);
		allFilter.add(buttonPanel, pos.nextRow().width(9));
		Border blackline = BorderFactory.createLineBorder(Color.black);
		allFilter.setBorder(blackline);
		allFilter.setBackground(bkColor);
		// taskpane.add(allFilter);
		this.setTitle("Filter Panel");
		this.setIcon(new ImageIcon("Resources/filter.png"));
		JPanel mainPanel = new JPanel();

		mainPanel.add(new RightPanel("Resources/filter.png"));
		mainPanel.add(allFilter);
		mainPanel.add(new RightPanel("Resources/filter.png"));
		taskpane.add(mainPanel);
		// this.setLayout(new BorderLayout());
		this.add(mainPanel);

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
