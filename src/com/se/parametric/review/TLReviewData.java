package com.se.parametric.review;

import java.awt.Color;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.BevelBorder;
import javax.swing.border.SoftBevelBorder;

import osheet.SheetPanel;
import osheet.WorkingSheet;

import com.se.automation.db.client.mapping.Document;
import com.se.grm.client.mapping.GrmGroup;
import com.se.grm.client.mapping.GrmRole;
import com.se.parametric.Loading;
import com.se.parametric.commonPanel.AlertsPanel;
import com.se.parametric.commonPanel.ButtonsPanel;
import com.se.parametric.commonPanel.FilterPanel;
import com.se.parametric.commonPanel.TablePanel;
import com.se.parametric.dba.ApprovedDevUtil;
import com.se.parametric.dba.DataDevQueryUtil;
import com.se.parametric.dba.ParaQueryUtil;
import com.se.parametric.dev.Developement;
import com.se.parametric.dto.ApprovedParametricDTO;
import com.se.parametric.dto.GrmUserDTO;
import com.se.parametric.dto.TableInfoDTO;

public class TLReviewData extends JPanel implements ActionListener
{

	SheetPanel sheetpanel = new SheetPanel();
	SheetPanel separationPanel = new SheetPanel();
	JPanel tabSheet, separationTab, selectionPanel;
	JPanel devSheetButtonPanel, separationButtonPanel;
	JTabbedPane tabbedPane;
	ArrayList<ArrayList<String>> input = new ArrayList<ArrayList<String>>();
	ArrayList<ArrayList<String>> separationValues = new ArrayList<ArrayList<String>>();
	JButton save, validate, separation, separationSave;
	TablePanel tablePanel = null;
	FilterPanel filterPanel = null;
	ButtonsPanel buttonsPanel;
	static AlertsPanel alertsPanel, alertsPanel1, alertsPanel2;
	Long[] teamMembers = null;
	WorkingSheet ws = null;
	Map<String, WorkingSheet> wsMap = new HashMap<String, WorkingSheet>();
	String teamLeaderName = "";
	long userId;
	int width, height;
	GrmUserDTO userDTO;
	String[] statuses;
	
	public TLReviewData(GrmUserDTO userDTO)
	{
		setLayout(null);
		this.userDTO = userDTO;
		teamLeaderName = userDTO.getFullName();
		userId = userDTO.getId();
		width = Toolkit.getDefaultToolkit().getScreenSize().width;
		height = Toolkit.getDefaultToolkit().getScreenSize().height;
		ArrayList<Object[]> filterData = DataDevQueryUtil.getTLReviewFilterData(userDTO, null, null);
		System.out.println("User:" + userDTO.getId() + " " + userDTO.getFullName() + " " + filterData.size());
		teamMembers = ParaQueryUtil.getTeamMembersIDByTL(userDTO.getId());
		selectionPanel = new JPanel();
		String[] tableHeader = new String[] { "PdfUrl", "PlName", "SupplierName", "TaskType", "Status", "DevUserName", "Date" };
		String[] filterLabels = { "PL Name", "Supplier", "Task Type", "User Name", "Status" };
		// tablePanel = new TablePanel(tableHeader, width - 120, (((height - 100) * 6) / 10));
		// tablePanel.setBounds(0, (((height - 100) * 4) / 10), width - 120, 700);
		tablePanel = new TablePanel(tableHeader, width - 120, (((height - 100) * 7) / 10));
		tablePanel.setBounds(0, (((height - 100) * 3) / 10), width - 120, (((height - 100) * 7) / 10));
		tablePanel.setBorder(new SoftBevelBorder(BevelBorder.RAISED, null, null, null, null));
		// filterPanel = new FilterPanel(filterLabels, filterData, width - 120, (((height - 100) * 4) / 10));
		// filterPanel.setBounds(0, 0, width - 120, (((height - 100) * 4) / 10));
		filterPanel = new FilterPanel(filterLabels, filterData, width - 120, (((height - 100) * 3) / 10));
		filterPanel.setBounds(0, 0, width - 120, (((height - 100) * 3) / 10));
		ArrayList<String> buttonLabels = new ArrayList<String>();
		buttonLabels.add("Load PDF");
		buttonLabels.add("Load All");
		buttonsPanel = new ButtonsPanel(buttonLabels);
		JButton buttons[] = buttonsPanel.getButtons();
		for(int i = 0; i < buttons.length; i++)
		{
			buttons[i].addActionListener(this);
		}
		buttonsPanel.setBounds(width - 120, 0, 108, height / 3);
		alertsPanel = new AlertsPanel(userDTO);
		alertsPanel1 = new AlertsPanel(userDTO);
		alertsPanel2 = new AlertsPanel(userDTO);
		alertsPanel.setBounds(width - 120, height / 3, 110, height * 3 / 4);
		alertsPanel1.setBounds(width - 120, height / 3, 110, height * 3 / 4);
		alertsPanel2.setBounds(width - 120, height / 3, 110, height * 3 / 4);

		selectionPanel.setLayout(null);
		selectionPanel.add(filterPanel);
		selectionPanel.add(tablePanel);
		selectionPanel.add(buttonsPanel);
		selectionPanel.add(alertsPanel);

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(0, 0, width, height - 100);
		tabSheet = new JPanel();
		separationTab = new JPanel();
		devSheetButtonPanel = new JPanel();
		devSheetButtonPanel.setBackground(new Color(211, 211, 211));
		devSheetButtonPanel.setBorder(new SoftBevelBorder(BevelBorder.RAISED, null, null, null, null));
		devSheetButtonPanel.setBounds(width - 120, 0, 108, height / 3);
		devSheetButtonPanel.setLayout(null);
		save = new JButton("Save");
		save.setBounds(3, 80, 95, 29);
		save.setForeground(new Color(25, 25, 112));
		save.setFont(new Font("Tahoma", Font.BOLD, 11));
		validate = new JButton("Validate");
		validate.setBounds(3, 46, 95, 29);
		validate.setForeground(new Color(25, 25, 112));
		validate.setFont(new Font("Tahoma", Font.BOLD, 11));
		separation = new JButton("Separation");
		separation.setBounds(3, 11, 95, 29);
		separation.setForeground(new Color(25, 25, 112));
		separation.setFont(new Font("Tahoma", Font.BOLD, 11));
		validate.addActionListener(this);
		save.addActionListener(this);
		separation.addActionListener(this);
		devSheetButtonPanel.add(separation);
		devSheetButtonPanel.add(validate);
		devSheetButtonPanel.add(save);
		devSheetButtonPanel.setBackground(new Color(211, 211, 211));
		separationButtonPanel = new JPanel();
		separationButtonPanel.setBorder(new SoftBevelBorder(BevelBorder.RAISED, null, null, null, null));
		separationButtonPanel.setBounds(width - 120, 0, 108, height / 3);
		separationButtonPanel.setLayout(null);
		separationSave = new JButton(" Save ");
		separationSave.setBounds(3, 11, 85, 29);
		separationSave.addActionListener(this);
		separationButtonPanel.add(separationSave);
		separationTab.setLayout(null);
		tabSheet.setLayout(null);
		sheetpanel.setBounds(0, 0, width - 120, height - 125);
		tabSheet.add(sheetpanel);
		tabSheet.add(devSheetButtonPanel);
		tabSheet.add(alertsPanel1);

		separationPanel.setBounds(0, 0, width - 120, height - 125);
		separationTab.add(separationPanel);
		separationTab.add(separationButtonPanel);
		separationTab.add(alertsPanel2);

		tabbedPane.addTab("Input Selection", null, selectionPanel, null);
		tabbedPane.addTab("Data Sheet", null, tabSheet, null);
		tabbedPane.addTab("Separation Sheet", null, separationTab, null);
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
		ArrayList<String> row = null;
		boolean isExclamationMark = false;
		/**
		 * Show pdfs Action
		 * **/
		if(event.getSource() == filterPanel.filterButton)
		{
			Date startDate = null;
			Date endDate = null;
			try
			{
				if(filterPanel.jDateChooser1.isEnabled())
				{
					startDate = filterPanel.jDateChooser1.getDate();
					endDate = filterPanel.jDateChooser2.getDate();
				}
				String plName = filterPanel.comboBoxItems[0].getSelectedItem().toString();
				String supplierName = filterPanel.comboBoxItems[1].getSelectedItem().toString();
				String taskType = filterPanel.comboBoxItems[2].getSelectedItem().toString();
				String userName = filterPanel.comboBoxItems[3].getSelectedItem().toString();
				String status = filterPanel.comboBoxItems[4].getSelectedItem().toString();
				if(status.equals("All"))
				{
					/******* all combo box items except all in statuses[]******/
	                int count = filterPanel.comboBoxItems[4].getItemCount();
	                StringBuilder builder = new StringBuilder();
	                for (int i = 0; i < count; i++) {
	                	
	                	if(!filterPanel.comboBoxItems[4].getItemAt(i).equals("All"))
	                	{	builder.append(filterPanel.comboBoxItems[4].getItemAt(i));
		                    if (i < count - 1) {
		                        builder.append(", ");
		                    }
	                    }
	                }
					statuses=builder.toString().split(", ");
				}else{
					
					statuses=new String[]{status};
				}
				if(!userName.equals("All"))
				{
					long userId = ParaQueryUtil.getUserIdByExactName(userName);
					teamMembers = new Long[] { userId };
				}
				else
				{
					teamMembers = ParaQueryUtil.getTeamMembersIDByTL(userId);
				}
				tablePanel.selectedData = DataDevQueryUtil.getReviewPDF(teamMembers, plName, supplierName, taskType, null, statuses, startDate, endDate, null, "finished", null);
				System.out.println("Selected Data Size=" + tablePanel.selectedData.size());
				// filterPanel.jDateChooser1.setDate(new Date(System.currentTimeMillis()));
				// filterPanel.jDateChooser2.setDate(new Date(System.currentTimeMillis()));
				tablePanel.setTableData1(0, tablePanel.selectedData);
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		else if(event.getSource() == filterPanel.refreshButton)
		{
			Date startDate = null;
			Date endDate = null;

			if(filterPanel.jDateChooser1.isEnabled())
			{
				startDate = filterPanel.jDateChooser1.getDate();
				endDate = filterPanel.jDateChooser2.getDate();
			}
			filterPanel.filterList = DataDevQueryUtil.getTLReviewFilterData(userDTO, startDate, endDate);
			tablePanel.clearTable();
			filterPanel.refreshFilters();

		}
		/**
		 * Load Data development Sheet
		 */
		else if(event.getActionCommand().equals("Load PDF"))
		{
			boolean ok = false;
			if(sheetpanel.isOpened())
				ok = ParaQueryUtil.getDialogMessage("another PDF is opend are you need to replace this", "Confermation Dailog");

			if(sheetpanel.isOpened() && ok == false)
			{
				thread.stop();
				loading.frame.dispose();
				return;
			}

			// if (wsMap.get("Separation") !=null) {
			// ws=wsMap.get("Separation");
			// ws = new WorkingSheet(separationPanel, "Separation");
			// }

			int[] selectedPdfs = tablePanel.table.getSelectedRows();
			int selectedPdfsCount = selectedPdfs.length;
			if(selectedPdfsCount == 0)
			{
				JOptionPane.showMessageDialog(null, "Please Select PDF First");
			}
			else if(selectedPdfsCount > 1)
			{
				JOptionPane.showMessageDialog(null, "Please Select One PDF");
			}
			else
			{
				try
				{
					JComboBox[] combos = filterPanel.comboBoxItems;

					String plName = combos[0].getSelectedItem().toString();
					String supplierName = combos[1].getSelectedItem().toString();
					String taskType = combos[2].getSelectedItem().toString();
					String userName = combos[3].getSelectedItem().toString();
					String status = combos[4].getSelectedItem().toString();
					if((!"All".equals(status) & (!"Pending TL Review".equals(status))))
					{
						JOptionPane.showMessageDialog(null, "Invalid PDF Status\nOnly Pending TL Review pdfs can be loaded");
						thread.stop();
						loading.frame.dispose();
						return;
					}
					wsMap.clear();
					TableInfoDTO docInfoDTO = tablePanel.selectedData.get(selectedPdfs[0]);
					String pdfUrl = docInfoDTO.getPdfUrl();
					Document document = ParaQueryUtil.getDocumnetByPdfUrl(pdfUrl);
					Date startDate = null, endDate = null;
					if(filterPanel.jDateChooser1.isEnabled())
					{
						startDate = filterPanel.jDateChooser1.getDate();
						endDate = filterPanel.jDateChooser2.getDate();
					}
					System.out.println(pdfUrl);
					if(!userName.equals("All"))
					{
						long userId = ParaQueryUtil.getUserIdByExactName(userName);
						teamMembers = new Long[] { userId };
					}
					else
					{
						teamMembers = ParaQueryUtil.getTeamMembersIDByTL(userId);
					}
					Map<String, ArrayList<ArrayList<String>>> reviewData = DataDevQueryUtil.getParametricValueReview1(teamMembers, plName, supplierName, taskType, status, startDate, endDate, new Long[] { document.getId() });
					int k = 0;
					tabbedPane.setSelectedIndex(1);
					sheetpanel.openOfficeDoc();

					for(String pl : reviewData.keySet())
					{
						// wsMap=
						ws = new WorkingSheet(sheetpanel, pl, k);
						sheetpanel.saveDoc("C:/Report/" + pdfUrl.replaceAll(".*/", "") + "@" + System.currentTimeMillis() + ".xls");
						wsMap.put(pl, ws);
						// ws.setReviewHeader(Arrays.asList("Dev Comment", "QA Comment"));
						if(docInfoDTO.getTaskType().contains("NPI"))
							ws.setNPIflag(true);
						ws.setReviewHeader(null, false);
						// ArrayList<String> sheetHeader = ws.getHeader();
						// int devCommentIndex = sheetHeader.indexOf("Dev Comment");
						// int qaCommentIndex = sheetHeader.indexOf("QA Comment");
						ArrayList<ArrayList<String>> plData = reviewData.get(pl);
						// for (int j = 0; j < plData.size(); j++) {
						// ArrayList<String> sheetRecord = plData.get(j);
						// // String partNumber = sheetRecord.get(6);
						// supplierName = sheetRecord.get(5);
						// // Supplier supplier = ParaQueryUtil.getSupplierByName(supplierName);
						// // Component com = ParaQueryUtil.getComponentByPartNumAndSupplier(partNumber, supplier);
						// // status = ParaQueryUtil.getPartStatusByComId(com.getComId());
						// // String comment = ParaQueryUtil.getFeedbackCommentByComId(com.getComId());
						// // GrmUserDTO issuer = ParaQueryUtil.getFeedbackIssuerByComId(com.getComId());
						// // for (int l = 0; l < 6; l++) {
						// // sheetRecord.add("");
						// // }
						// // if ("Parametric".equalsIgnoreCase(issuer.getGroupName())) {
						// // sheetRecord.set(devCommentIndex, comment);
						// // } else if ("Quality Group".equalsIgnoreCase(issuer.getGroupName())) {
						// // sheetRecord.set(qaCommentIndex, comment);
						// // }
						// // sheetRecord.set(1, issuer.getFullName());
						// // sheetRecord.set(2, status);
						// // plData.set(j, sheetRecord);
						// }
						ws.writeReviewData(plData, 2, 3);
						k++;
					}
					tablePanel.loadedPdfs.add(pdfUrl);
					tablePanel.setTableData1(0, tablePanel.selectedData);
				}catch(Exception ex)
				{
					ex.printStackTrace();
				}

			}
		}
		/**
		 * Load All PDFs review and development Sheet
		 */
		else if(event.getActionCommand().equals("Load All"))
		{
			Date startDate = null;
			Date endDate = null;

			boolean ok = false;
			if(sheetpanel.isOpened())
				ok = ParaQueryUtil.getDialogMessage("another PDF is opend are you need to replace this", "Confermation Dailog");

			if(sheetpanel.isOpened() && ok == false)
			{
				thread.stop();
				loading.frame.dispose();
				return;
			}

			try
			{
				if(filterPanel.jDateChooser1.isEnabled())
				{
					startDate = filterPanel.jDateChooser1.getDate();
					endDate = filterPanel.jDateChooser2.getDate();
				}
				String plName = filterPanel.comboBoxItems[0].getSelectedItem().toString();
				String supplierName = filterPanel.comboBoxItems[1].getSelectedItem().toString();
				String taskType = filterPanel.comboBoxItems[2].getSelectedItem().toString();
				String userName = filterPanel.comboBoxItems[3].getSelectedItem().toString();
				String status = filterPanel.comboBoxItems[4].getSelectedItem().toString();
				if((!"All".equals(status) & (!"Pending TL Review".equals(status))))
				{
					JOptionPane.showMessageDialog(null, "Invalid PDF Status\nOnly Pending TL Review pdfs can be loaded");
					thread.stop();
					loading.frame.dispose();
					return;
				}

				if(!userName.equals("All"))
				{
					long userId = ParaQueryUtil.getUserIdByExactName(userName);
					teamMembers = new Long[] { userId };
				}
				else
				{
					teamMembers = ParaQueryUtil.getTeamMembersIDByTL(userId);
				}
				if("All".equals(status))
				{
					status = "Pending TL Review";
				}
				Map<String, ArrayList<ArrayList<String>>> reviewData = DataDevQueryUtil.getParametricValueReview1(teamMembers, plName, supplierName, taskType, status, startDate, endDate, null);
				int k = 0;
				tabbedPane.setSelectedIndex(1);
				sheetpanel.openOfficeDoc();
				wsMap.clear();
				for(String pl : reviewData.keySet())
				{
					ws = new WorkingSheet(sheetpanel, pl, k);
					sheetpanel.saveDoc("C:/Report/Parametric_Auto/" + plName + "@" + userDTO.getFullName() + "@" + System.currentTimeMillis() + ".xls");
					wsMap.put(pl, ws);
					// ws.setReviewHeader(Arrays.asList("Dev Comment", "QA Comment"));
					if(DataDevQueryUtil.isNPITaskType(teamMembers, pl, supplierName, taskType, status, startDate, endDate, null))
						ws.setNPIflag(true);
					ws.setReviewHeader(null, false);

					// ArrayList<String> sheetHeader = ws.getHeader();
					// int devCommentIndex = sheetHeader.indexOf("Dev Comment")+4;
					// int qaCommentIndex = sheetHeader.indexOf("QA Comment")+4;
					ArrayList<ArrayList<String>> plData = reviewData.get(pl);
					// for (int j = 0; j < plData.size(); j++) {
					// ArrayList<String> sheetRecord = plData.get(j);
					// // String partNumber = sheetRecord.get(6);
					// supplierName = sheetRecord.get(5);
					// // Supplier supplier = ParaQueryUtil.getSupplierByName(supplierName);
					// // Component com = ParaQueryUtil.getComponentByPartNumAndSupplier(partNumber, supplier);
					// // status = ParaQueryUtil.getPartStatusByComId(com.getComId());
					// // String comment = ParaQueryUtil.getFeedbackCommentByComId(com.getComId());
					// // GrmUserDTO issuer = ParaQueryUtil.getFeedbackIssuerByComId(com.getComId());
					// // for (int l = 0; l < 6; l++) {
					// // sheetRecord.add("");
					// // }
					// // if ("Parametric".equalsIgnoreCase(issuer.getGroupName())) {
					// // sheetRecord.set(devCommentIndex, comment);
					// // } else if ("Quality Group".equalsIgnoreCase(issuer.getGroupName())) {
					// // sheetRecord.set(qaCommentIndex, comment);
					// // }
					// // sheetRecord.set(1, issuer.getFullName());
					// // sheetRecord.set(2, status);
					// // plData.set(j, sheetRecord);
					// }
					ws.writeReviewData(plData, 2, 3);
					k++;
				}
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		/**
		 * Validate Parts Action
		 */
		else if(event.getSource() == validate)
		{
			System.out.println("~~~~~~~ Start Validate ~~~~~~~");
			wsMap.keySet();
			for(String wsName : wsMap.keySet())
			{
				if(wsName != "LoadAllData" && wsName != "Separation")
				{
					wsMap.get(wsName).validateParts(true);
				}
			}

		}
		/**
		 * Save Parts Action
		 */
		else if(event.getSource() == save)
		{
			System.out.println("~~~~~~~ Start saving Data ~~~~~~~");
			wsMap.keySet();
			for(String wsName : wsMap.keySet())
			{
				if(wsName != "LoadAllData" && wsName != "Separation")
				{
					wsMap.get(wsName).saveTLReviewAction(teamLeaderName);
				}
			}
		}

		/**
		 * Load Separation Sheet Action
		 * **/
		else if(event.getActionCommand().equals("Separation"))
		{
			input = new ArrayList<ArrayList<String>>();
			tabbedPane.setSelectedIndex(2);
			row = new ArrayList<String>();
			row.add("PL_Name");
			row.add("Part");
			row.add("Datasheet");
			row.add("Feature Name");
			row.add("Feature Value");
			row.add("Feature Unit");
			row.add("Sign");
			row.add("Value");
			row.add("Type");
			row.add("Condition");
			row.add("Multiplier");
			row.add("Unit");
			if(wsMap.get("Separation") != null)
			{
				wsMap.remove("Separation");
			}
			for(String wsName : wsMap.keySet())
			{
				if(wsName != "LoadAllData" && wsName != "Separation")
				{
					System.out.println("Sheet Name:" + wsName);
					input = wsMap.get(wsName).getUnApprovedValues(input);
				}
			}
			separationPanel.openOfficeDoc();
			ws = new WorkingSheet(separationPanel, "Separation");
			sheetpanel.saveDoc("C:/Report/Parametric_Auto/" + "Separation@" + userDTO.getFullName() + "@" + System.currentTimeMillis() + ".xls");
			ws.setSeparationHeader(row);
			ws.writeSheetData(input, 1);
			wsMap.put("Separation", ws);
		}
		/**
		 * Save Separation Action
		 */
		else if(event.getActionCommand().equals(" Save "))
		{
			tabbedPane.setSelectedIndex(2);
			separationValues = wsMap.get("Separation").readSpreadsheet(1);
			if(separationValues.isEmpty())
			{
				tabbedPane.setSelectedIndex(1);
				JOptionPane.showMessageDialog(null, "All Values are Approved");

			}
			else
			{
				for(int i = 0; i < separationValues.size(); i++)
				{
					row = separationValues.get(i);
					String plName = row.get(0);
					String featureName = row.get(3);
					String featureFullValue = row.get(4);
					List<ApprovedParametricDTO> approved = ApprovedDevUtil.createApprovedValuesList(featureFullValue, plName, featureName, row.get(5), row.get(6), row.get(7), row.get(10), row.get(11), row.get(9), row.get(8));
					try
					{
						ApprovedDevUtil.saveAppGroupAndSepValue(0, 0, approved, plName, featureName, featureFullValue, row.get(2), userId);
					}catch(Exception ex)
					{
						ex.printStackTrace();
					}
					isExclamationMark = false;
					List<String> appValues = wsMap.get(plName).getApprovedFeatuer().get(featureName);
					appValues.add(featureFullValue);
				}
				JOptionPane.showMessageDialog(null, "Approved Saving Done");
			}

		}
		thread.stop();
		loading.frame.dispose();
	}

	public void updateFlags(ArrayList<String> flags)
	{
		alertsPanel.updateFlags(flags);
		alertsPanel1.updateFlags(flags);
		alertsPanel2.updateFlags(flags);

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
		uDTO.setId(46);
		uDTO.setFullName("Ahmed Risk");
		uDTO.setId(121);
		uDTO.setFullName("Ahmad_rahim");
		GrmRole role = new GrmRole();
		role.setId(1l);
		GrmGroup group = new GrmGroup();
		group.setId(1l);
		uDTO.setGrmRole(role);
		uDTO.setGrmGroup(group);
		TLReviewData devPanel = new TLReviewData(uDTO);
		frame.getContentPane().add(devPanel);
		frame.show();
		while(true)
		{
			ArrayList<String> flags = ParaQueryUtil.getAlerts(uDTO.getId(), 1, 1);
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