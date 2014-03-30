package com.se.Quality;

import java.awt.Color;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.BevelBorder;
import javax.swing.border.SoftBevelBorder;

import osheet.SheetPanel;
import osheet.WorkingSheet;

import com.se.grm.client.mapping.GrmGroup;
import com.se.grm.client.mapping.GrmRole;
import com.se.parametric.Loading;
import com.se.parametric.commonPanel.AlertsPanel;
import com.se.parametric.commonPanel.ButtonsPanel;
import com.se.parametric.commonPanel.FilterPanel;
import com.se.parametric.dba.ApprovedDevUtil;
import com.se.parametric.dba.ParaQueryUtil;
import com.se.parametric.dev.PdfLinks;
import com.se.parametric.dto.DocumentInfoDTO;
import com.se.parametric.dto.GrmUserDTO;
import com.se.parametric.dto.UnApprovedDTO;
import com.se.parametric.util.ImagePanel;
import com.se.parametric.util.StatusName;

public class QualityUnApprovedValue extends JPanel implements ActionListener
{

	/**
	 * Create the panel.
	 */
	DocumentInfoDTO documentInfoDTO = null;
	SheetPanel sheetPanel = new SheetPanel();
	WorkingSheet ws;
	PdfLinks pdfLinks = null;
	ArrayList<ArrayList<String>> input = new ArrayList<ArrayList<String>>();
	JPanel tabSheet, selectionPanel, flowChart;
	JPanel devSheetButtonPanel;
	JTabbedPane tabbedPane;
	JButton button = null;
	JButton showAll = new JButton("Show All");
	JButton save, validate, separation;
	Map<String, WorkingSheet> wsMap = new HashMap<String, WorkingSheet>();
	ArrayList<ArrayList<String>> separationValues = new ArrayList<ArrayList<String>>();
	boolean foundPdf = false;
	JPanel unapprovedPanel = null;
	FilterPanel filterPanel = null;
	ButtonsPanel buttonsPanel;
	public ArrayList<ArrayList<String>> list;
	Long[] teamMembers = null;
	ArrayList<String> row = null;
	GrmUserDTO userDTO;
	ArrayList<UnApprovedDTO> unApproveds;
	QAUnApprovedValueFeedback QAAppfeedBack = null;
	static AlertsPanel alertsPanel;

	public QualityUnApprovedValue(GrmUserDTO userDTO)
	{
		this.userDTO = userDTO;
		setLayout(null);
		int width = Toolkit.getDefaultToolkit().getScreenSize().width;
		int height = Toolkit.getDefaultToolkit().getScreenSize().height;
		ArrayList<Object[]> filterData = ApprovedDevUtil.getUnapprovedReviewFilter(new Long[] { userDTO.getId() }, null, null, "QA");
		System.out.println("User:" + userDTO.getId() + " " + userDTO.getFullName() + " " + filterData.size());
		selectionPanel = new JPanel();
		String[] filterLabels = { "Eng Name", "PL Name", "Supplier", "Status", "Task Type" };
		sheetPanel.setBounds(0, (((height - 100) * 3) / 10), width - 120, height - (((height - 100) * 3) / 10) - 130);
		filterPanel = new FilterPanel(filterLabels, filterData, width - 120, (((height - 100) * 3) / 10), false);
		filterPanel.setBounds(0, 0, width - 120, (((height - 100) * 3) / 10));
		ArrayList<String> buttonLabels = new ArrayList<String>();
		buttonLabels.add("Save");
		// buttonLabels.add("Feedback History");
		buttonsPanel = new ButtonsPanel(buttonLabels);
		JButton buttons[] = buttonsPanel.getButtons();
		for(int i = 0; i < buttons.length; i++)
		{
			buttons[i].addActionListener(this);
		}
		buttonsPanel.setBounds(width - 120, 0, 108, height / 3);
		alertsPanel = new AlertsPanel(userDTO);
		alertsPanel.setBounds(width - 120, height / 3, 110, height * 3 / 4);
		selectionPanel.setLayout(null);
		selectionPanel.add(filterPanel);
		selectionPanel.add(sheetPanel);
		selectionPanel.add(buttonsPanel);
		selectionPanel.add(alertsPanel);
		selectionPanel.setBounds(0, 0, width - 110, height - 100);
		filterPanel.filterButton.addActionListener(this);
		filterPanel.refreshButton.addActionListener(this);
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(0, 0, width, height - 100);
		QAAppfeedBack = new QAUnApprovedValueFeedback(userDTO);
		devSheetButtonPanel = new JPanel();
		devSheetButtonPanel.setBackground(new Color(211, 211, 211));
		devSheetButtonPanel.setBorder(new SoftBevelBorder(BevelBorder.RAISED, null, null, null, null));
		devSheetButtonPanel.setBounds(width - 110, 0, 106, height - 100);
		QAAppfeedBack.setBounds(width - 110, 0, 106, height - 100);
		devSheetButtonPanel.setLayout(null);
		save = new JButton("Save");

		save.setBounds(3, 80, 85, 29);
		validate = new JButton("Validate");
		validate.setBounds(3, 46, 85, 29);
		separation = new JButton("Separation");
		separation.setBounds(3, 11, 85, 29);
		validate.addActionListener(this);
		save.addActionListener(this);
		separation.addActionListener(this);
		devSheetButtonPanel.add(separation);
		devSheetButtonPanel.add(validate);
		devSheetButtonPanel.add(save);
		devSheetButtonPanel.setBackground(new Color(211, 211, 211));
		add(tabbedPane);
		tabbedPane.addTab("Quality UnApproved Review", null, selectionPanel, null);
		tabbedPane.addTab("Quality UnApproved FeedBack", null, QAAppfeedBack, null);
		flowChart = new ImagePanel("QASeparation.jpg");
		tabbedPane.addTab("Separation Flow", null, flowChart, null);
	}

	@Override
	public void actionPerformed(ActionEvent event)
	{
		Loading loading = new Loading();
		WorkingSheet ws = null;
		Thread thread = new Thread(loading);
		thread.start();
		UnApprovedDTO obj = null;
		tabbedPane.setSelectedIndex(0);
		if(event.getSource().equals(filterPanel.filterButton))
		{
			Date startDate = null;
			Date endDate = null;
			if(filterPanel.jDateChooser1.isEnabled())
			{
				startDate = filterPanel.jDateChooser1.getDate();
				endDate = filterPanel.jDateChooser2.getDate();
			}

			String engName = filterPanel.comboBoxItems[0].getSelectedItem().toString();
			String plName = filterPanel.comboBoxItems[1].getSelectedItem().toString();
			String supplierName = filterPanel.comboBoxItems[2].getSelectedItem().toString();
			String status = filterPanel.comboBoxItems[3].getSelectedItem().toString();
			String taskType = filterPanel.comboBoxItems[4].getSelectedItem().toString();
			long userId = userDTO.getId();

			unApproveds = ApprovedDevUtil.getUnapprovedReviewData(new Long[] { userId }, engName, startDate, endDate, plName, supplierName, StatusName.qaReview, taskType, "QA", "Data", userId);
			list = new ArrayList<ArrayList<String>>();
			row = new ArrayList<String>();
			sheetPanel.openOfficeDoc();
			ws = new WorkingSheet(sheetPanel, "Unapproved Values");
			sheetPanel.saveDoc("C:/Report/Quality_Auto/" + "QUnapparoved@" + userDTO.getFullName() + "@" + System.currentTimeMillis() + ".xls");
			row.add("PL Name");
			row.add("Part Name");
			row.add("Pdf Url");
			row.add("Feature Name");
			row.add("Feature Value");
			row.add("Feature Unit");
			row.add("Sign");
			row.add("Value");
			row.add("Type");
			row.add("Condition");
			row.add("Multiplier");
			row.add("Unit");
			row.add("Status");
			row.add("Comment");
			wsMap.put("Unapproved Values", ws);
			ws.setUnapprovedHeader(row);
			for(int i = 0; i < unApproveds.size(); i++)
			{
				row = new ArrayList<String>();
				obj = unApproveds.get(i);
				row.add(obj.getPlName());
				row.add(obj.getPartNumber());
				row.add(obj.getPdfUrl());
				row.add(obj.getFeatureName());
				row.add(obj.getFeatureValue());
				row.add(obj.getFeatureUnit());
				row.add(obj.getSign());
				row.add(obj.getValue());
				row.add(obj.getType());
				row.add(obj.getCondition());
				row.add(obj.getMultiplier());
				row.add(obj.getUnit());
				list.add(row);
			}
			ArrayList<String> statusValues = new ArrayList<String>();
			statusValues.add("Approved");
			statusValues.add("Wrong Separation");
			statusValues.add("Wrong Value");
			ws.statusValues = statusValues;
			ws.writeReviewData(list, 1, 13);
		}
		else if(event.getSource().equals(filterPanel.refreshButton))
		{
			Date startDate = null;
			Date endDate = null;

			if(filterPanel.jDateChooser1.isEnabled())
			{
				startDate = filterPanel.jDateChooser1.getDate();
				endDate = filterPanel.jDateChooser2.getDate();
			}
			filterPanel.filterList = ApprovedDevUtil.getUnapprovedReviewFilter(new Long[] { userDTO.getId() }, startDate, endDate, "QA");
			filterPanel.refreshFilters();
		}
		else if(event.getActionCommand().equals("Save"))
		{
			String status = filterPanel.comboBoxItems[3].getSelectedItem().toString();
			for(String wsName : wsMap.keySet())
			{
				if(wsName == "Unapproved Values")
				{
					String work = wsMap.get(wsName).getSelectedCellValue();
					ArrayList<ArrayList<String>> result = wsMap.get(wsName).readSpreadsheet(1);
					int updateFlag = 1;

					for(int i = 0; i < result.size(); i++)
					{
						ArrayList<String> newValReq = result.get(i);
						if((newValReq.get(12).equals("Wrong Separation") || newValReq.get(12).equals("Wrong Value")) && newValReq.get(13).trim().isEmpty())
						{
							JOptionPane.showMessageDialog(null, " You Must Write Comment with Status Wrong Separation,Wrong Value Check row : " + (i + 1));
							thread.stop();
							loading.frame.dispose();
							return;
						}
					}
					/** Team Leader approved and send to QA */
					for(int i = 0; i < result.size(); i++)
					{
						ArrayList<String> newValReq = result.get(i);
						UnApprovedDTO oldValReq = unApproveds.get(i);
						if(newValReq.get(0).equals(oldValReq.getPlName()) && newValReq.get(3).equals(oldValReq.getFeatureName()) && newValReq.get(4).equals(oldValReq.getFeatureValue()) && newValReq.get(5).equals(oldValReq.getFeatureUnit()))
						{
							oldValReq.setSign(newValReq.get(6));
							oldValReq.setValue(newValReq.get(7));
							oldValReq.setType(newValReq.get(8));
							oldValReq.setCondition(newValReq.get(9));
							oldValReq.setMultiplier(newValReq.get(10));
							oldValReq.setUnit(newValReq.get(11));
							oldValReq.setFbStatus(StatusName.reject);
							oldValReq.setGruopSatus(StatusName.tlFeedback);
							oldValReq.setComment(newValReq.get(13));
							oldValReq.setIssuedby(userDTO.getId());
							oldValReq.setFbType("QA");
							oldValReq.setIssueType(newValReq.get(12));
							if(newValReq.get(12).equals("Approved"))
							{
								ApprovedDevUtil.setValueApproved(result.get(i), StatusName.approved);
							}

							// else if(newValReq.get(12).equals("Wrong Value"))
							// {
							// ApprovedDevUtil.saveAppWrongValue( oldValReq);
							// }
							// else if(newValReq.get(12).equals("Wrong Separation"))
							// {
							// ApprovedDevUtil.saveWrongSeparation( oldValReq);
							// }

							else if(newValReq.get(12).equals("Wrong Separation") || newValReq.get(12).equals("Wrong Value"))
							{
								ApprovedDevUtil.saveWrongSeparation(oldValReq);
							}
						}
						else
						{
							JOptionPane.showMessageDialog(null, newValReq.get(0) + " @ " + newValReq.get(4) + " Can't Save dueto change in main columns");
						}
					}

					System.out.println("size is " + result.size());
				}
			}
			JOptionPane.showMessageDialog(null, "Save Done");
		}
		thread.stop();
		loading.frame.dispose();
	}

	public void updateFlags(ArrayList<String> flags)
	{
		alertsPanel.updateFlags(flags);
		QAAppfeedBack.alertsPanel.updateFlags(flags);

	}

	public static void main(String[] args)
	{
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		int width = Toolkit.getDefaultToolkit().getScreenSize().width;
		int height = Toolkit.getDefaultToolkit().getScreenSize().height;
		frame.setSize(width, height);
		frame.setTitle("TL Review");
		GrmUserDTO uDTO = new GrmUserDTO();
		// uDTO.setId(47);
		// uDTO.setFullName("mohamad mostafa");
		// uDTO.setId(32);
		// uDTO.setFullName("Hatem Hussien");
		uDTO.setId(80);
		uDTO.setFullName("Shady");
		// uDTO.setId(121);
		// uDTO.setFullName("Ahmad_rahim");
		GrmRole role = new GrmRole();
		role.setId(1l);
		GrmGroup group = new GrmGroup();
		group.setId(101l);
		uDTO.setGrmRole(role);
		uDTO.setGrmGroup(group);
		QualityUnApprovedValue devPanel = new QualityUnApprovedValue(uDTO);
		frame.getContentPane().add(devPanel);
		frame.show();
		while(true)
		{
			ArrayList<String> flags = ParaQueryUtil.getAlerts(uDTO.getId(), 101, 3);
			devPanel.updateFlags(flags);

			try
			{
				Thread.sleep(5000);
			}catch(InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}
}
