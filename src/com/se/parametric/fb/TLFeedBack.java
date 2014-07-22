package com.se.parametric.fb;

import java.awt.Color;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingWorker;
import javax.swing.border.BevelBorder;
import javax.swing.border.SoftBevelBorder;

import osheet.SheetPanel;
import osheet.WorkingSheet;

import com.se.automation.db.client.mapping.Document;
import com.se.automation.db.client.mapping.ParaFeedbackAction;
import com.se.automation.db.parametric.StatusName;
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
import com.se.parametric.dev.Update.LongRunProcess;
import com.se.parametric.dto.ApprovedParametricDTO;
import com.se.parametric.dto.GrmUserDTO;
import com.se.parametric.dto.TableInfoDTO;

public class TLFeedBack extends JPanel implements ActionListener
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
	Long[] teamMembers = null;
	WorkingSheet ws = null;
	Map<String, WorkingSheet> wsMap = new HashMap<String, WorkingSheet>();
	long userId;
	String userName;
	int width, height;
	GrmUserDTO userDTO;
	static AlertsPanel alertsPanel, alertsPanel1, alertsPanel2;

	/**
	 * Create the panel.
	 * 
	 * @param result
	 * @param userDTO
	 */
	public TLFeedBack(GrmUserDTO userDTO)
	{
		userId = userDTO.getId();
		this.userDTO = userDTO;
		userName = userDTO.getFullName();
		setLayout(null);
		width = Toolkit.getDefaultToolkit().getScreenSize().width;
		height = Toolkit.getDefaultToolkit().getScreenSize().height;
		ArrayList<Object[]> filterData = DataDevQueryUtil.getTLFeedbackFilterData(userDTO, null, null);
		teamMembers = ParaQueryUtil.getTeamMembersIDByTL(userId);
		selectionPanel = new JPanel();
		String[] tableHeader = new String[] { "PdfUrl", "PlName", "SupplierName", "InfectedParts", "InfectedTaxonomies", "Date" };
		String[] filterLabels = { "PL Name", "Supplier", "Feedback Source", "Feedback Type" };
		// tablePanel = new TablePanel(tableHeader, width - 120, (((height - 100) * 6) / 10));
		// tablePanel.setBounds(0, (((height - 100) * 4) / 10), width - 120, 700);
		tablePanel = new TablePanel(tableHeader, width - 120, (((height - 100) * 7) / 10));
		tablePanel.setBounds(0, (((height - 100) * 3) / 10), width - 120, (((height - 100) * 7) / 10));
		tablePanel.setBorder(new SoftBevelBorder(BevelBorder.RAISED, null, null, null, null));
		// filterPanel = new FilterPanel(filterLabels, filterData, width - 120, (((height - 100) * 4) / 10));
		// filterPanel.setBounds(0, 0, width - 120, (((height - 100) * 4) / 10));
		filterPanel = new FilterPanel(filterLabels, filterData, width - 120, (((height - 100) * 3) / 10), false);
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
		filterPanel.filterButton.addActionListener(this);

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(0, 0, width, height - 100);
		tabSheet = new JPanel();
		separationTab = new JPanel();
		devSheetButtonPanel = new JPanel();
		devSheetButtonPanel.setBackground(new Color(211, 211, 211));
		devSheetButtonPanel.setBorder(new SoftBevelBorder(BevelBorder.RAISED, null, null, null, null));
		devSheetButtonPanel.setBounds(width - 120, 0, 106, height - 100);
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
		separationButtonPanel.setBounds(width - 120, 0, 106, height - 100);
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

		filterPanel.refreshButton.addActionListener(this);

	}

	@Override
	public void actionPerformed(ActionEvent event)
	{
		LongRunProcess longRunProcess = new LongRunProcess(event);
		longRunProcess.execute();
	}

	public void saveseparation()
	{
		ArrayList<String> row;
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

				List<String> appValues = wsMap.get(plName).getApprovedFeatuer().get(featureName);
				appValues.add(featureFullValue);
			}
			JOptionPane.showMessageDialog(null, "Approved Saving Done");
		}
	}

	public void loadseparation()
	{
		ArrayList<String> row;
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
		separationPanel.saveDoc("C:/Report/Parametric_Auto/" + "Separation@" + userDTO.getFullName() + "@" + System.currentTimeMillis() + ".xls");
		ws.setSeparationHeader(row);
		ws.writeSheetData(input, 1);
		wsMap.put("Separation", ws);
	}

	public void loadallpdf()
	{
		JComboBox[] combos = filterPanel.comboBoxItems;

		try
		{
			String plName = combos[0].getSelectedItem().toString();
			String supplierName = combos[1].getSelectedItem().toString();
			String issuerName = combos[2].getSelectedItem().toString();
			String feedbackTypeStr = combos[3].getSelectedItem().toString();
			// Long[] docsIds = DataDevQueryUtil.getFeedbackDocIds(feedbackTypeStr);
			String documentStatus = StatusName.tlFeedback;
			Date startDate = null, endDate = null;
			wsMap.clear();
			if(filterPanel.jDateChooser1.isEnabled())
			{
				startDate = filterPanel.jDateChooser1.getDate();
				endDate = filterPanel.jDateChooser2.getDate();
			}
			Map<String, ArrayList<ArrayList<String>>> reviewData = DataDevQueryUtil.getFeedbackParametricValueReview(teamMembers, plName, supplierName, documentStatus, feedbackTypeStr, issuerName, startDate, endDate, null, userDTO.getId());
			// Map<String, ArrayList<ArrayList<String>>> reviewData = ParaQueryUtil.getParametricValueReview1(teamMembers,
			// plName,
			// supplierName, null, documentStatus, startDate, endDate, docsIds);
			int k = 0;
			tabbedPane.setSelectedIndex(1);
			sheetpanel.openOfficeDoc();
			for(String pl : reviewData.keySet())
			{
				ws = new WorkingSheet(sheetpanel, pl, k);
				sheetpanel.saveDoc("C:/Report/Parametric_Auto/" + pl + "@" + userDTO.getFullName() + "@" + System.currentTimeMillis() + ".xls");
				wsMap.put(pl, ws);
				if(DataDevQueryUtil.isNPITaskType(null, pl, supplierName, null, StatusName.tlFeedback, startDate, endDate, null))
					ws.setNPIflag(true);
				ws.setTLFBHeader(Arrays.asList("LastTLComment", "Issue Initiator", "Develop Eng."), false);
				ArrayList<String> sheetHeader = ws.getHeader();
				int lstTLcommentIndex = sheetHeader.indexOf("LastTLComment");
				int issuerIndex = sheetHeader.indexOf("Issue Initiator");
				int sentBYIndex = sheetHeader.indexOf("Issued By");
				int Cactionindex = sheetHeader.indexOf("C_Action");
				int Pactionindex = sheetHeader.indexOf("P_Action");
				int RootcauseIndex = sheetHeader.indexOf("RootCause");
				int Actionduedateindex = sheetHeader.indexOf("ActionDueDate");
				int wrongfetsindex = sheetHeader.indexOf("Wrong Features");
				int fbcommentindex = sheetHeader.indexOf("FBComment");
				int engindex = sheetHeader.indexOf("Develop Eng.");
				int FBStatusindex = sheetHeader.indexOf("FBStatus");
				ArrayList<ArrayList<String>> plData = reviewData.get(pl);
				for(int j = plData.size() - 1; j > -1; j--)
				{
					try
					{
						ArrayList<String> sheetRecord = plData.get(j);
						String partNumber = sheetRecord.get(15);
						supplierName = sheetRecord.get(13);

						ArrayList<String> feedCom = DataDevQueryUtil.getFeedbackByPartAndSupp(partNumber, supplierName);
						String lstTlComment = DataDevQueryUtil.getlastengComment(new Long(feedCom.get(3)), userDTO.getId());
						GrmUserDTO feedbackIssuer = DataDevQueryUtil.getFeedbackIssuerByComId(new Long(feedCom.get(3)));
						String wrongfeatures = DataDevQueryUtil.getfbwrongfets(new Long(feedCom.get(3)), feedbackIssuer.getId());
						ParaFeedbackAction action = null;
						action = DataDevQueryUtil.getfeedBackActionByItem(new Long(feedCom.get(3)), userDTO.getId());
						if(action != null)
						{
							sheetRecord.set(Cactionindex, action.getCAction());
							sheetRecord.set(Pactionindex, action.getPAction());
							sheetRecord.set(RootcauseIndex, action.getRootCause());
							sheetRecord.set(Actionduedateindex, action.getActionDueDate().toString());
						}
						for(int l = 0; l < 8; l++)
						{
							sheetRecord.add("");
						}
						sheetRecord.set(FBStatusindex, feedCom.get(6));
						sheetRecord.set(lstTLcommentIndex, lstTlComment);
						sheetRecord.set(issuerIndex, feedbackIssuer.getFullName());
						sheetRecord.set(engindex, sheetRecord.get(sentBYIndex));
						sheetRecord.set(sentBYIndex, feedCom.get(1));
						sheetRecord.set(wrongfetsindex, wrongfeatures);
						sheetRecord.set(fbcommentindex, feedCom.get(0));
						plData.set(j, sheetRecord);
					}catch(Exception e)
					{
						System.err.println(e.getMessage());
						plData.remove(j);
						continue;
					}
				}
				ws.writeReviewData(plData, 2, 3);
				k++;
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public void loadpdf()
	{
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
				wsMap.clear();
				TableInfoDTO docInfoDTO = tablePanel.selectedData.get(selectedPdfs[0]);
				String pdfUrl = docInfoDTO.getPdfUrl();
				Document document = ParaQueryUtil.getDocumnetByPdfUrl(pdfUrl);
				JComboBox[] combos = filterPanel.comboBoxItems;
				String plName = combos[0].getSelectedItem().toString();
				String supplierName = combos[1].getSelectedItem().toString();
				String issuerName = combos[2].getSelectedItem().toString();
				String feedbackType = combos[3].getSelectedItem().toString();
				String documentStatus = StatusName.tlFeedback;
				Date startDate = null, endDate = null;
				if(filterPanel.jDateChooser1.isEnabled())
				{
					startDate = filterPanel.jDateChooser1.getDate();
					endDate = filterPanel.jDateChooser2.getDate();
				}
				System.out.println(pdfUrl);

				Map<String, ArrayList<ArrayList<String>>> reviewData = DataDevQueryUtil.getFeedbackParametricValueReview(teamMembers, plName, supplierName, documentStatus, feedbackType, issuerName, startDate, endDate,
						new Long[] { document.getId() }, userDTO.getId());
				int k = 0;
				tabbedPane.setSelectedIndex(1);
				sheetpanel.openOfficeDoc();

				for(String pl : reviewData.keySet())
				{
					ws = new WorkingSheet(sheetpanel, pl, k);
					sheetpanel.saveDoc("C:/Report/Parametric_Auto/" + pdfUrl.replaceAll(".*/", "") + "@" + userDTO.getFullName() + "@" + System.currentTimeMillis() + ".xls");
					wsMap.put(pl, ws);
					if(docInfoDTO.getTaskType().contains("NPI"))
						ws.setNPIflag(true);
					ws.setTLFBHeader(Arrays.asList("LastTLComment", "Issue Initiator", "Develop Eng."), false);
					ArrayList<String> sheetHeader = ws.getHeader();
					int lstTLcommentIndex = sheetHeader.indexOf("LastTLComment");
					int issuerIndex = sheetHeader.indexOf("Issue Initiator");
					int sentBYIndex = sheetHeader.indexOf("Issued By");
					int Cactionindex = sheetHeader.indexOf("C_Action");
					int Pactionindex = sheetHeader.indexOf("P_Action");
					int RootcauseIndex = sheetHeader.indexOf("RootCause");
					int Actionduedateindex = sheetHeader.indexOf("ActionDueDate");
					int wrongfetsindex = sheetHeader.indexOf("Wrong Features");
					int fbcommentindex = sheetHeader.indexOf("FBComment");
					int FBStatusindex = sheetHeader.indexOf("FBStatus");
					int engindex = sheetHeader.indexOf("Develop Eng.");
					ArrayList<ArrayList<String>> plData = reviewData.get(pl);
					for(int j = plData.size() - 1; j > -1; j--)
					{
						try
						{
							ArrayList<String> sheetRecord = plData.get(j);
							String partNumber = sheetRecord.get(15);
							supplierName = sheetRecord.get(13);

							ArrayList<String> feedCom = DataDevQueryUtil.getFeedbackByPartAndSupp(partNumber, supplierName);
							String lstTlComment = DataDevQueryUtil.getlastengComment(new Long(feedCom.get(3)), userDTO.getId());
							GrmUserDTO feedbackIssuer = DataDevQueryUtil.getFeedbackIssuerByComId(new Long(feedCom.get(3)));
							String wrongfeatures = DataDevQueryUtil.getfbwrongfets(new Long(feedCom.get(3)), feedbackIssuer.getId());

							ParaFeedbackAction action = null;
							action = DataDevQueryUtil.getfeedBackActionByItem(new Long(feedCom.get(3)), userDTO.getId());
							if(action != null)
							{
								sheetRecord.set(Cactionindex, action.getCAction());
								sheetRecord.set(Pactionindex, action.getPAction());
								sheetRecord.set(RootcauseIndex, action.getRootCause());
								Date date = action.getActionDueDate();
								SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
								sheetRecord.set(Actionduedateindex, date == null ? "" : sdf.format(date).toString());
								// sheetRecord.set(Actionduedateindex, action.getActionDueDate().toString());
							}
							for(int l = 0; l < 9; l++)
							{
								sheetRecord.add("");
							}
							sheetRecord.set(FBStatusindex, feedCom.get(6));
							sheetRecord.set(lstTLcommentIndex, lstTlComment);
							sheetRecord.set(issuerIndex, feedbackIssuer.getFullName());
							sheetRecord.set(engindex, sheetRecord.get(sentBYIndex));
							sheetRecord.set(sentBYIndex, feedCom.get(1));
							sheetRecord.set(wrongfetsindex, wrongfeatures);
							sheetRecord.set(fbcommentindex, feedCom.get(0));
							plData.set(j, sheetRecord);
						}catch(Exception e)
						{
							System.err.println(e.getMessage());
							plData.remove(j);
							continue;
						}
					}
					ws.writeReviewData(plData, 2, 4);
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

	public static void main(String[] args)
	{
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		int width = Toolkit.getDefaultToolkit().getScreenSize().width;
		int height = Toolkit.getDefaultToolkit().getScreenSize().height - 50;
		frame.setSize(width, height);
		frame.setTitle("TL Feedback");
		GrmUserDTO uDTO = new GrmUserDTO();
		// uDTO.setId(47);
		// uDTO.setFullName("mohamad mostafa");
		// uDTO.setId(45);
		// uDTO.setFullName("Ahmed Hamdy");
		// uDTO.setId(46);
		// uDTO.setFullName("Ahmed Rizk");
		uDTO.setId(121);
		uDTO.setFullName("Ahmad_Rahim");
		GrmRole role = new GrmRole();
		role.setId(1l);
		GrmGroup group = new GrmGroup();
		group.setId(1l);
		uDTO.setGrmRole(role);
		uDTO.setGrmGroup(group);
		TLFeedBack fbPanel = new TLFeedBack(uDTO);
		frame.getContentPane().add(fbPanel);
		frame.show();
		while(true)
		{
			ArrayList<String> flags = ParaQueryUtil.getAlerts(uDTO.getId(), 1, 1);
			fbPanel.updateFlags(flags);

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
		alertsPanel2.updateFlags(flags);

	}

	class LongRunProcess extends SwingWorker
	{
		ActionEvent event = null;

		LongRunProcess(ActionEvent event)
		{
			this.event = event;
		}

		/**
		 * @throws Exception
		 */
		protected Object doInBackground() throws Exception
		{

			Loading.show();
			ArrayList<String> row = null;
			boolean isExclamationMark = false;
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
					String issuer = filterPanel.comboBoxItems[2].getSelectedItem().toString();
					String feedbackType = filterPanel.comboBoxItems[3].getSelectedItem().toString();
					String documentStatus = StatusName.tlFeedback;
					tablePanel.selectedData = DataDevQueryUtil.getTlReviewFeedbackPDFs(teamMembers, plName, supplierName, documentStatus, startDate, endDate, feedbackType, userId, issuer);
					System.out.println("Selected Data Size=" + tablePanel.selectedData.size());
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
				filterPanel.filterList = DataDevQueryUtil.getTLFeedbackFilterData(userDTO, startDate, endDate);
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

					Loading.close();
					return null;
				}
				loadpdf();
			}
			/**
			 * Load All PDFs review and development Sheet
			 */
			else if(event.getActionCommand().equals("Load All"))
			{
				boolean ok = false;
				if(sheetpanel.isOpened())
					ok = ParaQueryUtil.getDialogMessage("another PDF is opend are you need to replace this", "Confermation Dailog");

				if(sheetpanel.isOpened() && ok == false)
				{

					Loading.close();
					return null;
				}
				loadallpdf();
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
						wsMap.get(wsName).validateTLFBParts(true);
					}
				}
				JOptionPane.showMessageDialog(null, "Validation Finished");
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
						wsMap.get(wsName).saveTLFeedbackAction(userName);
					}
				}
			}
			/**
			 * Load Separation Sheet Action
			 * **/
			else if(event.getActionCommand().equals("Separation"))
			{
				loadseparation();
			}
			/**
			 * Save Separation Action
			 */
			else if(event.getActionCommand().equals(" Save "))
			{
				saveseparation();
			}

			Loading.close();
			return null;
		}
	}
}
