package com.se.Quality;

import java.awt.Color;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.BevelBorder;
import javax.swing.border.SoftBevelBorder;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import osheet.SheetPanel;
import osheet.WorkingSheet;

import com.se.automation.db.SessionUtil;
import com.se.automation.db.StatusName;
import com.se.automation.db.client.dto.QAChecksDTO;
import com.se.automation.db.client.mapping.Document;
import com.se.automation.db.client.mapping.QaCheckParts;
import com.se.grm.client.mapping.GrmGroup;
import com.se.grm.client.mapping.GrmRole;
import com.se.parametric.Loading;
import com.se.parametric.commonPanel.AlertsPanel;
import com.se.parametric.commonPanel.ButtonsPanel;
import com.se.parametric.commonPanel.FilterPanel;
import com.se.parametric.dba.DataDevQueryUtil;
import com.se.parametric.dba.ParaQueryUtil;
import com.se.parametric.dto.GrmUserDTO;
import com.se.parametric.dto.TableInfoDTO;

public class QAChecks extends JPanel implements ActionListener
{

	SheetPanel sheetpanel = new SheetPanel();
	SheetPanel separationPanel = new SheetPanel();
	JPanel tabSheet, selectionPanel;
	JPanel devSheetButtonPanel, separationButtonPanel;
	JTabbedPane tabbedPane;
	ArrayList<ArrayList<String>> input = new ArrayList<ArrayList<String>>();
	ArrayList<ArrayList<String>> separationValues = new ArrayList<ArrayList<String>>();
	// JButton save;
	// JButton Validate;
	FilterPanel filterPanel = null;
	ButtonsPanel buttonsPanel;
	Long[] users = null;
	WorkingSheet ws = null;
	Map<String, WorkingSheet> wsMap = new HashMap<String, WorkingSheet>();
	String engName = "";
	long userId;
	int width, height;
	GrmUserDTO userDTO;
	static AlertsPanel alertsPanel, alertsPanel1;
	String checker;
	String filterstatus;
	public static ArrayList<ArrayList<String>> seperationvalues = new ArrayList<>();

	public QAChecks(GrmUserDTO userDTO)
	{
		setLayout(null);
		this.userDTO = userDTO;
		engName = userDTO.getFullName();
		userId = userDTO.getId();
		width = Toolkit.getDefaultToolkit().getScreenSize().width;
		height = Toolkit.getDefaultToolkit().getScreenSize().height;
		ArrayList<Object[]> filterData = DataDevQueryUtil.getQAchecksFilterData(userDTO);
		System.out.println("User:" + userDTO.getId() + " " + userDTO.getFullName() + " " + filterData.size());
		selectionPanel = new JPanel();

		String[] filterLabels = { "PL Name", "Supplier", "Checker Type", "Status" };
		filterPanel = new FilterPanel(filterLabels, filterData, width - 120, (((height - 100) * 3) / 10), false);
		filterPanel.setBounds(0, 0, width - 120, (((height - 100) * 3) / 10));
		ArrayList<String> buttonLabels = new ArrayList<String>();
		buttonLabels.add("Save");
		buttonLabels.add("Seperation");
		buttonsPanel = new ButtonsPanel(buttonLabels);
		JButton buttons[] = buttonsPanel.getButtons();
		for(int i = 0; i < buttons.length; i++)
		{
			buttons[i].addActionListener(this);
		}
		buttonsPanel.setBounds(width - 120, 0, 110, height / 3);
		alertsPanel = new AlertsPanel(userDTO);
		alertsPanel1 = new AlertsPanel(userDTO);
		alertsPanel.setBounds(width - 120, height / 3, 110, height * 3 / 4);
		alertsPanel1.setBounds(width - 120, height / 3, 110, height * 3 / 4);
		sheetpanel.setBounds(0, (((height - 100) * 3) / 10), width - 120, height - (((height - 100) * 3) / 10) - 130);
		selectionPanel.setLayout(null);
		selectionPanel.add(filterPanel);
		selectionPanel.add(buttonsPanel);
		selectionPanel.add(alertsPanel);
		selectionPanel.add(sheetpanel);
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(0, 0, width, height - 100);
		tabSheet = new JPanel();
		devSheetButtonPanel = new JPanel();
		devSheetButtonPanel.setBackground(new Color(211, 211, 211));
		devSheetButtonPanel.setBorder(new SoftBevelBorder(BevelBorder.RAISED, null, null, null, null));
		devSheetButtonPanel.setBounds(width - 120, 0, 110, height / 3);
		devSheetButtonPanel.setLayout(null);
		tabSheet.setLayout(null);

		// tabSheet.add(sheetpanel);
		tabSheet.add(devSheetButtonPanel);
		tabSheet.add(alertsPanel1);

		tabbedPane.addTab("Input Selection", null, selectionPanel, null);
		tabbedPane.addTab("Seperation", null, tabSheet, null);
		add(tabbedPane);

		filterPanel.filterButton.addActionListener(this);
		filterPanel.refreshButton.addActionListener(this);

	}

	@Override
	public void actionPerformed(ActionEvent event)
	{
		Loading loading = new Loading();
		Thread thread = new Thread(loading);
		thread.start();

		/**
		 * Show pdfs Action
		 * **/
		if(event.getSource() == filterPanel.filterButton)
		{
			Date startDate = null;
			Date endDate = null;
			try
			{
				dofilter(startDate, endDate);
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		else if(event.getSource() == filterPanel.refreshButton)
		{

			filterPanel.filterList = DataDevQueryUtil.getQAchecksFilterData(userDTO);
			filterPanel.refreshFilters();

		}
		else if(event.getActionCommand().equals("Save"))
		{
			System.out.println("~~~~~~~ Start saving Data ~~~~~~~");
			if(!filterstatus.equals(StatusName.Open))
			{
				JOptionPane.showMessageDialog(null, "You can save Open checks only");
				return;
			}
			wsMap.keySet();
			for(String wsName : wsMap.keySet())
			{
				if(wsName == "QAChecks")
				{
					wsMap.get(wsName).saveQAChecksAction(checker, engName);
				}
			}
		}
		else if(event.getActionCommand().equals("Seperation"))
		{

		}

		// else if(event.getSource() == Validate)
		// {
		// System.out.println("~~~~~~~ Start validation Data ~~~~~~~");
		// wsMap.keySet();
		// for(String wsName : wsMap.keySet())
		// {
		// if(wsName != "LoadAllData" && wsName != "Separation")
		// {
		// // wsMap.get(wsName).validateQAReview();
		// }
		// }
		// }

		thread.stop();
		loading.frame.dispose();
	}

	private void dofilter(Date startDate, Date endDate) throws Exception
	{
		Session session = SessionUtil.getSession();
		try
		{

			if(filterPanel.jDateChooser1.isEnabled())
			{
				startDate = filterPanel.jDateChooser1.getDate();
				endDate = filterPanel.jDateChooser2.getDate();
			}

			String plName = filterPanel.comboBoxItems[0].getSelectedItem().toString();
			String supplierName = filterPanel.comboBoxItems[1].getSelectedItem().toString();
			String checkerType = filterPanel.comboBoxItems[2].getSelectedItem().toString();
			String status = filterPanel.comboBoxItems[3].getSelectedItem().toString();
			if(checkerType.equals("All"))
			{
				JOptionPane.showMessageDialog(null, "You must select checker type");
				return;
			}
			checker = checkerType;
			filterstatus = status;
			tabbedPane.setSelectedIndex(0);
			sheetpanel.openOfficeDoc();
			ArrayList<QAChecksDTO> reviewData = DataDevQueryUtil.getQAchecksData(plName, supplierName, checkerType, status, startDate, endDate, userDTO.getId(), session);
			wsMap.clear();
			ws = new WorkingSheet(sheetpanel, "QAChecks");
			sheetpanel.saveDoc("C:/Report/" + "QAChecks by " + userDTO.getFullName() + "@" + System.currentTimeMillis() + ".xls");
			wsMap.put("QAChecks", ws);
			ws.setqaChecksheader(checkerType);
			ArrayList<String> sheetHeader = ws.getHeader();
			int statusindx = sheetHeader.indexOf("Status");
			// int flag = sheetHeader.indexOf("Flag");
			ArrayList<ArrayList<String>> data = new ArrayList<>();
			for(int i = 0; i < reviewData.size(); i++)
			{
				boolean exist = DataDevQueryUtil.chkpartflagqachks(reviewData.get(i).getPart(), session);
				String flag = "AffectedPart";
				if(exist)
				{
					flag = "InputPart";
				}
				ArrayList<String> row = new ArrayList<>();
				row.add(reviewData.get(i).getNanAlphaPart());
				row.add(flag);
				row.add(reviewData.get(i).getPart().getPartNumber());
				row.add(reviewData.get(i).getVendor().getName());
				row.add(reviewData.get(i).getDatasheet().getPdf().getSeUrl());
				row.add(reviewData.get(i).getDatasheetTitle());
				row.add(reviewData.get(i).getProductLine() == null ? "" : reviewData.get(i).getProductLine().getName());
				row.add(reviewData.get(i).getMask() == null ? "" : reviewData.get(i).getMask().getMstrPart());
				row.add(reviewData.get(i).getFamily() == null ? "" : reviewData.get(i).getFamily().getName());
				row.add("");
				row.add("");
				row.add(reviewData.get(i).getFeatureName() == null ? "" : reviewData.get(i).getFeatureName());
				row.add(reviewData.get(i).getFeatureValue() == null ? "" : reviewData.get(i).getFeatureValue());
				data.add(row);

			}
			ws.writeReviewData(data, 1, statusindx + 1);
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			session.close();
		}
	}

	public static void main(String[] args)
	{
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		int width = Toolkit.getDefaultToolkit().getScreenSize().width;
		int height = Toolkit.getDefaultToolkit().getScreenSize().height;
		frame.setSize(width, height);
		frame.setTitle("QA Checks");
		GrmUserDTO uDTO = new GrmUserDTO();
		// uDTO.setId(47);
		// uDTO.setFullName("mohamad mostafa");
		// uDTO.setId(32);
		// uDTO.setFullName("Hatem Hussien");
		uDTO.setId(359);
		uDTO.setFullName("Mohamed Hussien");
		// uDTO.setId(121);
		// uDTO.setFullName("Ahmad_rahim");
		GrmRole role = new GrmRole();
		role.setId(3l);
		GrmGroup group = new GrmGroup();
		group.setId(1l);
		uDTO.setGrmRole(role);
		uDTO.setGrmGroup(group);
		QAChecks devPanel = new QAChecks(uDTO);
		frame.getContentPane().add(devPanel);
		frame.show();
		while(true)
		{
			ArrayList<String> flags = ParaQueryUtil.getAlerts(uDTO.getId(), 101, 3);
			// devPanel.updateFlags(flags);

			try
			{
				Thread.sleep(5000);
			}catch(InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}

	public void updateFlags(ArrayList<String> flags)
	{
		alertsPanel.updateFlags(flags);
		alertsPanel1.updateFlags(flags);
		// alertsPanel2.updateFlags(flags);

	}
}
