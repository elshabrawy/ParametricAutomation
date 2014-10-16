package com.se.parametric.fb;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingWorker;

import osheet.SheetPanel;
import osheet.WorkingSheet;

import com.se.automation.db.client.mapping.Document;
import com.se.automation.db.client.mapping.ParaFeedbackAction;
import com.se.automation.db.parametric.StatusName;
import com.se.grm.client.mapping.GrmGroup;
import com.se.grm.client.mapping.GrmRole;
import com.se.parametric.MainWindow;
import com.se.parametric.commonPanel.FilterPanel;
import com.se.parametric.commonPanel.TablePanel;
import com.se.parametric.commonPanel.WorkingAreaPanel;
import com.se.parametric.dba.ApprovedDevUtil;
import com.se.parametric.dba.DataDevQueryUtil;
import com.se.parametric.dba.ParaQueryUtil;
import com.se.parametric.dto.ApprovedParametricDTO;
import com.se.parametric.dto.GrmUserDTO;
import com.se.parametric.dto.TableInfoDTO;

public class TLFeedBack extends JPanel implements ActionListener
{

	SheetPanel sheetpanel, separationPanel;
	WorkingAreaPanel inputSelectionPanel, devSheetPanel, separationSheetPanel;
	TablePanel tablePanel;
	JTabbedPane tabbedPane;
	ArrayList<ArrayList<String>> input = new ArrayList<ArrayList<String>>();
	ArrayList<ArrayList<String>> separationValues = new ArrayList<ArrayList<String>>();
	FilterPanel filterPanel;
	Long[] teamMembers = null;
	WorkingSheet ws = null;
	Map<String, WorkingSheet> wsMap = new HashMap<String, WorkingSheet>();
	GrmUserDTO userDTO;

	public TLFeedBack(GrmUserDTO userDTO)
	{
		this.userDTO = userDTO;
		this.setLayout(new BorderLayout());
		ArrayList<Object[]> filterData = DataDevQueryUtil.getTLFeedbackFilterData(userDTO, null,
				null);
		teamMembers = ParaQueryUtil.getTeamMembersIDByTL(userDTO.getId());
		tabbedPane = new JTabbedPane();

		inputSelectionPanel = new WorkingAreaPanel(this.userDTO);
		String[] tableHeader = new String[] { "PdfUrl", "PlName", "SupplierName", "InfectedParts",
				"InfectedTaxonomies", "FinishedDate" };
		tablePanel = inputSelectionPanel.getTablePanel(tableHeader);

		String[] filterLabels = { "PL Name", "Supplier", "Feedback Source", "Feedback Type" };

		filterPanel = inputSelectionPanel.getFilterPanel(filterLabels, filterData, false, this);

		ArrayList<String> buttonLabels = new ArrayList<String>();
		buttonLabels.add("Load PDF");
		buttonLabels.add("Load All");
		inputSelectionPanel.addButtonsPanel(buttonLabels, this);

		devSheetPanel = new WorkingAreaPanel(userDTO);
		buttonLabels = new ArrayList<String>();
		buttonLabels.add("Save");
		buttonLabels.add("Validate");
		buttonLabels.add("Separation");
		devSheetPanel.addButtonsPanel(buttonLabels, this);
		sheetpanel = devSheetPanel.getSheet();

		separationSheetPanel = new WorkingAreaPanel(userDTO);
		buttonLabels = new ArrayList<String>();
		buttonLabels.add(" Save ");
		separationSheetPanel.addButtonsPanel(buttonLabels, this);
		separationPanel = separationSheetPanel.getSheet();

		inputSelectionPanel.addComponentsToPanel();
		devSheetPanel.addComponentsToPanel();
		separationSheetPanel.addComponentsToPanel();

		tabbedPane.addTab("Input Selection", inputSelectionPanel);
		tabbedPane.addTab("Data Sheet", devSheetPanel);
		tabbedPane.addTab("Separation Sheet", separationSheetPanel);

		this.add(tabbedPane);
		this.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent arg0)
			{
			}

			@Override
			public void focusGained(FocusEvent arg0)
			{
				if(null != tabbedPane.getSelectedComponent())
				{
					tabbedPane.getSelectedComponent().requestFocusInWindow();
				}
			}
		});
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
			MainWindow.glass.setVisible(false);
			JOptionPane.showMessageDialog(null, "All Values are Approved");

		}
		else
		{
			for(int i = 0; i < separationValues.size(); i++)
			{
				row = separationValues.get(i);

				String plName = row.get(0);
				String featureName = row.get(5);
				String featureFullValue = row.get(6);
				List<ApprovedParametricDTO> approved = ApprovedDevUtil.createApprovedValuesList(
						featureFullValue, plName, featureName, row.get(7), row.get(8), row.get(9),
						row.get(12), row.get(13), row.get(11), row.get(10));
				try
				{
					ApprovedDevUtil.saveAppGroupAndSepValue(0, 0, approved, plName, featureName,
							featureFullValue, row.get(2), userDTO.getId());
				}catch(Exception ex)
				{
					ex.printStackTrace();
				}

				List<String> appValues = wsMap.get(plName).getApprovedFeatuer().get(featureName);
				appValues.add(featureFullValue);
			}
			MainWindow.glass.setVisible(false);
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
		row.add("Supplier");// 3
		row.add("ReceivedDate");// 4
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
		separationPanel.saveDoc("C:/Report/Parametric_Auto/" + "Separation@"
				+ userDTO.getFullName() + "@" + System.currentTimeMillis() + ".xls");
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
			Long[] pdfs = new Long[tablePanel.selectedData.size()];
			String[] pdfurls = new String[tablePanel.selectedData.size()];
			for(int i = 0; i < tablePanel.selectedData.size(); i++)
			{
				pdfurls[i] = tablePanel.selectedData.get(i).getPdfUrl();
				Document document = ParaQueryUtil.getDocumnetByPdfUrl(pdfurls[i]);
				pdfs[i] = document.getId();

			}
			Map<String, ArrayList<ArrayList<String>>> reviewData = DataDevQueryUtil
					.getFeedbackParametricValueReview(teamMembers, plName, supplierName,
							documentStatus, feedbackTypeStr, issuerName, startDate, endDate, pdfs,
							userDTO.getId());
			// Map<String, ArrayList<ArrayList<String>>> reviewData = ParaQueryUtil.getParametricValueReview1(teamMembers,
			// plName,
			// supplierName, null, documentStatus, startDate, endDate, docsIds);
			int k = 0;
			tabbedPane.setSelectedIndex(1);
			sheetpanel.openOfficeDoc();
			for(String pl : reviewData.keySet())
			{
				ws = new WorkingSheet(sheetpanel, pl, k);
				sheetpanel.saveDoc("C:/Report/Parametric_Auto/" + pl + "@" + userDTO.getFullName()
						+ "@" + System.currentTimeMillis() + ".xls");
				wsMap.put(pl, ws);
				if(DataDevQueryUtil.isNPITaskType(null, pl, supplierName, null,
						StatusName.tlFeedback, startDate, endDate, null))
					ws.setNPIflag(true);
				ws.setTLFBHeader(Arrays.asList("LastTLComment", "Issue Initiator", "Develop Eng.","Issue Type"),
						false);
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
				int issuetypeidx = sheetHeader.indexOf("Issue Type");
				ArrayList<ArrayList<String>> plData = reviewData.get(pl);
				for(int j = plData.size() - 1; j > -1; j--)
				{
					try
					{
						ArrayList<String> sheetRecord = plData.get(j);
						String partNumber = sheetRecord.get(15);
						supplierName = sheetRecord.get(13);

						ArrayList<String> feedCom = DataDevQueryUtil.getFeedbackByPartAndSupp(
								partNumber, supplierName);
						String lstTlComment = DataDevQueryUtil.getlastengComment(
								new Long(feedCom.get(3)), userDTO.getId());
						GrmUserDTO feedbackIssuer = DataDevQueryUtil
								.getFeedbackIssuerByComId(new Long(feedCom.get(3)));
						String wrongfeatures = DataDevQueryUtil.getfbwrongfets(
								new Long(feedCom.get(3)), feedbackIssuer.getId());
						ParaFeedbackAction action = null;
						action = DataDevQueryUtil.getfeedBackActionByItem(new Long(feedCom.get(3)),
								userDTO.getId());
						if(action != null)
						{
							sheetRecord.set(Cactionindex, action.getCAction());
							sheetRecord.set(Pactionindex, action.getPAction());
							sheetRecord.set(RootcauseIndex, action.getRootCause());
							sheetRecord.set(Actionduedateindex, action.getActionDueDate()
									.toString());
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
						sheetRecord.set(issuetypeidx, feedCom.get(7));
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
			MainWindow.glass.setVisible(false);
			JOptionPane.showMessageDialog(null, "Please Select PDF First");
		}
		else if(selectedPdfsCount > 1)
		{
			MainWindow.glass.setVisible(false);
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

				Map<String, ArrayList<ArrayList<String>>> reviewData = DataDevQueryUtil
						.getFeedbackParametricValueReview(teamMembers, plName, supplierName,
								documentStatus, feedbackType, issuerName, startDate, endDate,
								new Long[] { document.getId() }, userDTO.getId());
				int k = 0;
				tabbedPane.setSelectedIndex(1);
				sheetpanel.openOfficeDoc();

				for(String pl : reviewData.keySet())
				{
					ws = new WorkingSheet(sheetpanel, pl, k);
					sheetpanel.saveDoc("C:/Report/Parametric_Auto/" + pdfUrl.replaceAll(".*/", "")
							+ "@" + userDTO.getFullName() + "@" + System.currentTimeMillis()
							+ ".xls");
					wsMap.put(pl, ws);
					if(docInfoDTO.getTaskType().contains("NPI"))
						ws.setNPIflag(true);
					ws.setTLFBHeader(Arrays.asList("LastTLComment", "Issue Initiator",
							"Develop Eng.", "Issue Type"), false);
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
					int issuetypeidx = sheetHeader.indexOf("Issue Type");
					ArrayList<ArrayList<String>> plData = reviewData.get(pl);
					for(int j = plData.size() - 1; j > -1; j--)
					{
						try
						{
							ArrayList<String> sheetRecord = plData.get(j);
							String partNumber = sheetRecord.get(15);
							supplierName = sheetRecord.get(13);

							ArrayList<String> feedCom = DataDevQueryUtil.getFeedbackByPartAndSupp(
									partNumber, supplierName);
							String lstTlComment = DataDevQueryUtil.getlastengComment(new Long(
									feedCom.get(3)), userDTO.getId());
							GrmUserDTO feedbackIssuer = DataDevQueryUtil
									.getFeedbackIssuerByComId(new Long(feedCom.get(3)));
							String wrongfeatures = DataDevQueryUtil.getfbwrongfets(
									new Long(feedCom.get(3)), feedbackIssuer.getId());

							ParaFeedbackAction action = null;
							action = DataDevQueryUtil.getfeedBackActionByItem(
									new Long(feedCom.get(3)), userDTO.getId());
							if(action != null)
							{
								sheetRecord.set(Cactionindex, action.getCAction());
								sheetRecord.set(Pactionindex, action.getPAction());
								sheetRecord.set(RootcauseIndex, action.getRootCause());
								Date date = action.getActionDueDate();
								SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
								sheetRecord.set(Actionduedateindex,
										date == null ? "" : sdf.format(date).toString());
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
							sheetRecord.set(issuetypeidx, feedCom.get(7));
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
			// ArrayList<String> flags = ParaQueryUtil.getAlerts(uDTO.getId(), 1, 1);
			fbPanel.updateFlags();

			try
			{
				Thread.sleep(5000);
			}catch(InterruptedException e)
			{
				e.printStackTrace();
			}
		}

	}

	public void updateFlags()
	{
		inputSelectionPanel.updateFlags();
		devSheetPanel.updateFlags();
		separationSheetPanel.updateFlags();
	}

	public void clearOfficeResources()
	{
		if(sheetpanel != null)
		{
			sheetpanel.closeApplication();
		}
		if(separationPanel != null)
		{
			separationPanel.closeApplication();
		}
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

			MainWindow.glass.setVisible(true);
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
					tablePanel.selectedData = DataDevQueryUtil.getTlReviewFeedbackPDFs(teamMembers,
							plName, supplierName, documentStatus, startDate, endDate, feedbackType,
							userDTO.getId(), issuer);
					System.out.println("Selected Data Size=" + tablePanel.selectedData.size());
					tablePanel.setTableData1(0, tablePanel.selectedData);
				}catch(Exception e)
				{
					e.printStackTrace();
				}
				filterPanel.setCollapsed(true);
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
				filterPanel.filterList = DataDevQueryUtil.getTLFeedbackFilterData(userDTO,
						startDate, endDate);
				tablePanel.clearTable();
				filterPanel.refreshFilters();
				filterPanel.setCollapsed(true);
			}
			/**
			 * Load Data development Sheet
			 */
			else if(event.getActionCommand().equals("Load PDF"))
			{
				boolean ok = false;
				if(sheetpanel.isOpened())
					ok = ParaQueryUtil.getDialogMessage(
							"another PDF is opend are you need to replace this",
							"Confermation Dailog");

				if(sheetpanel.isOpened() && ok == false)
				{

					MainWindow.glass.setVisible(false);
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
					ok = ParaQueryUtil.getDialogMessage(
							"another PDF is opend are you need to replace this",
							"Confermation Dailog");

				if(sheetpanel.isOpened() && ok == false)
				{

					MainWindow.glass.setVisible(false);
					return null;
				}
				loadallpdf();
			}
			/**
			 * Validate Parts Action
			 */
			else if(event.getActionCommand().equals("Validate"))
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
				MainWindow.glass.setVisible(false);
				JOptionPane.showMessageDialog(null, "Validation Finished");
			}
			/**
			 * Save Parts Action
			 */
			else if(event.getActionCommand().equals("Save"))
			{
				System.out.println("~~~~~~~ Start saving Data ~~~~~~~");
				wsMap.keySet();
				for(String wsName : wsMap.keySet())
				{
					if(wsName != "LoadAllData" && wsName != "Separation")
					{
						if(!wsMap.get(wsName).saved)
						{
							wsMap.get(wsName).saved = true;
							wsMap.get(wsName).saveTLFeedbackAction(userDTO.getFullName());
						}
						else
						{
							MainWindow.glass.setVisible(false);
							JOptionPane.showMessageDialog(null, "This Sheet Saved Before.");
							return null;
						}

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

			MainWindow.glass.setVisible(false);
			return null;
		}
	}

}
