package com.se.parametric.fb;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
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
import com.se.parametric.MainWindow;
import com.se.parametric.commonPanel.AlertsPanel;
import com.se.parametric.commonPanel.ButtonsPanel;
import com.se.parametric.commonPanel.FilterPanel;
import com.se.parametric.commonPanel.TablePanel;
import com.se.parametric.commonPanel.WorkingAreaPanel;
import com.se.parametric.dba.ApprovedDevUtil;
import com.se.parametric.dba.DataDevQueryUtil;
import com.se.parametric.dba.ParaQueryUtil;
import com.se.parametric.dto.ApprovedParametricDTO;
import com.se.parametric.dto.GrmUserDTO;
import com.se.parametric.dto.TableInfoDTO;

public class EngFeedBack extends JPanel implements ActionListener
{

	private long userId;
	private TablePanel tablePanel;
	private FilterPanel filterPanel;
	// private ButtonsPanel buttonsPanel, devButtonsPanel;
	private JTabbedPane tabbedPane;
	SheetPanel sheetPanel = new SheetPanel();
	SheetPanel separationPanel = new SheetPanel();
	WorkingAreaPanel sheetTab, separationTab, selectionPanel;
	private String userName;
	private WorkingSheet ws;
	private Map<String, WorkingSheet> wsMap;
	ArrayList<ArrayList<String>> input = new ArrayList<ArrayList<String>>();
	ArrayList<ArrayList<String>> separationValues = new ArrayList<ArrayList<String>>();
	// static AlertsPanel alertsPanel, alertsPanel1, alertsPanel2;
	GrmUserDTO userDTO = null;

	/**
	 * Create the panel.
	 * 
	 * @param result
	 * @param userDTO
	 */
	public EngFeedBack(GrmUserDTO userDTO)
	{
		this.userDTO = userDTO;
		this.setLayout(new BorderLayout());
		userName = userDTO.getFullName();
		int width = Toolkit.getDefaultToolkit().getScreenSize().width;
		int height = Toolkit.getDefaultToolkit().getScreenSize().height;
		wsMap = new HashMap<String, WorkingSheet>();
		userId = userDTO.getId();

		selectionPanel = new WorkingAreaPanel(this.userDTO);
		String[] labels = new String[] { "PdfUrl", "PlName", "SupplierName", "InfectedParts",
				"InfectedTaxonomies", "AssginedDate" };
		String[] filterHeader = { "PL Name", "Supplier", "Feedback Type", "Issued By" };
		ArrayList<Object[]> filterData = DataDevQueryUtil.getUserFeedbackData(userDTO, null, null);
		tablePanel = selectionPanel.getTablePanel(labels);
		filterPanel = selectionPanel.getFilterPanel(filterHeader, filterData, false, this);

		ArrayList<String> buttonLabels = new ArrayList<String>();
		buttonLabels.add("LoadSheet");
		buttonLabels.add("Load All");
		selectionPanel.addButtonsPanel(buttonLabels, this);

		sheetTab = new WorkingAreaPanel(this.userDTO);
		buttonLabels = new ArrayList<String>();
		buttonLabels.add("Separation");
		buttonLabels.add("Validate");
		buttonLabels.add("Save");
		sheetTab.addButtonsPanel(buttonLabels, this);
		sheetPanel = sheetTab.getSheet();

		separationTab = new WorkingAreaPanel(this.userDTO);
		buttonLabels = new ArrayList<String>();
		buttonLabels.add(" Save ");
		separationTab.addButtonsPanel(buttonLabels, this);
		separationPanel = separationTab.getSheet();

		selectionPanel.addComponentsToPanel();
		sheetTab.addComponentsToPanel();
		separationTab.addComponentsToPanel();

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.addTab("PDF Links", null, selectionPanel, null);
		tabbedPane.addTab("Sheet", null, sheetTab, null);
		tabbedPane.addTab("Separation Sheet", null, separationTab, null);
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

	public void updateFlags()
	{
		selectionPanel.updateFlags();
		sheetTab.updateFlags();
		separationTab.updateFlags();

	}

	public static void main(String[] args)
	{
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		int width = Toolkit.getDefaultToolkit().getScreenSize().width;
		int height = Toolkit.getDefaultToolkit().getScreenSize().height;
		frame.setSize(width, height);
		frame.setTitle("Eng Feedback");
		GrmUserDTO uDTO = new GrmUserDTO();
		uDTO.setId(376);
		uDTO.setFullName("Salah Shiha");
		// uDTO.setId(117);
		// uDTO.setFullName("Abeer Mohamady");
		GrmRole role = new GrmRole();
		role.setId(3l);
		GrmGroup group = new GrmGroup();
		group.setId(1l);
		uDTO.setGrmRole(role);
		uDTO.setGrmGroup(group);
		EngFeedBack fbPanel = new EngFeedBack(uDTO);
		frame.getContentPane().add(fbPanel);
		frame.show();
		while(true)
		{
			// ArrayList<String> flags = ParaQueryUtil.getAlerts(uDTO.getId(), 1, 3);
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
				System.out.println("In Show pdfs");
				Date startDate = null;
				Date endDate = null;

				if(filterPanel.jDateChooser1.isEnabled())
				{
					startDate = filterPanel.jDateChooser1.getDate();
					endDate = filterPanel.jDateChooser2.getDate();
				}
				String plName = filterPanel.comboBoxItems[0].getSelectedItem().toString();
				String supplierName = filterPanel.comboBoxItems[1].getSelectedItem().toString();
				String feedbackType = filterPanel.comboBoxItems[2].getSelectedItem().toString();
				String issuedBy = filterPanel.comboBoxItems[3].getSelectedItem().toString();

				tablePanel.selectedData = DataDevQueryUtil.getDevFeedbackPDF(userId, plName,
						supplierName, issuedBy, feedbackType, startDate, endDate);
				tablePanel.setTableData1(0, tablePanel.selectedData);
				filterPanel.setCollapsed(true);

			}
			/**
			 * refresh filter
			 */
			else if(event.getSource() == filterPanel.refreshButton)
			{
				Date startDate = null;
				Date endDate = null;

				if(filterPanel.jDateChooser1.isEnabled())
				{
					startDate = filterPanel.jDateChooser1.getDate();
					endDate = filterPanel.jDateChooser2.getDate();
				}
				filterPanel.filterList = DataDevQueryUtil.getUserFeedbackData(userDTO, startDate,
						endDate);
				tablePanel.clearTable();
				filterPanel.refreshFilters();
				filterPanel.setCollapsed(true);
			}
			/**
			 * Load Data development Sheet
			 */
			else if(event.getActionCommand().equals("LoadSheet"))
			{
				boolean ok = false;
				if(sheetPanel.isOpened())
					ok = ParaQueryUtil.getDialogMessage(
							"another PDF is opend are you need to replace this",
							"Confermation Dailog");

				if(sheetPanel.isOpened() && ok == false)
				{

					MainWindow.glass.setVisible(false);
					return null;
				}
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
						TableInfoDTO docInfoDTO = tablePanel.selectedData.get(selectedPdfs[0]);
						String pdfUrl = docInfoDTO.getPdfUrl();
						System.out.println(pdfUrl);
						Document document = ParaQueryUtil.getDocumnetByPdfUrl(pdfUrl);
						tablePanel.loadedPdfs.add(pdfUrl);
						JComboBox[] combos = filterPanel.comboBoxItems;
						// String plName = combos[0].getSelectedItem().toString();
						String plName = docInfoDTO.getPlName();
						String supplierName = combos[1].getSelectedItem().toString();
						String feedbackType = combos[2].getSelectedItem().toString();
						String issuedBy = combos[3].getSelectedItem().toString();
						Date startDate = null, endDate = null;
						if(filterPanel.jDateChooser1.isEnabled())
						{
							startDate = filterPanel.jDateChooser1.getDate();
							endDate = filterPanel.jDateChooser2.getDate();
						}
						tabbedPane.setSelectedIndex(1);
						sheetPanel.openOfficeDoc();
						wsMap.clear();
						Long[] users = { userId };
						Map<String, ArrayList<ArrayList<String>>> reviewData = DataDevQueryUtil
								.getFeedbackParametricValueReview(users, plName, supplierName,
										StatusName.engFeedback, feedbackType, issuedBy, startDate,
										endDate, new Long[] { document.getId() }, userDTO.getId());
						int k = 0;
						for(String pl : reviewData.keySet())
						{
							ws = new WorkingSheet(sheetPanel, pl, k);
							sheetPanel.saveDoc("C:/Report/Parametric_Auto/"
									+ pdfUrl.replaceAll(".*/", "") + "@" + userDTO.getFullName()
									+ "@" + System.currentTimeMillis() + ".xls");
							wsMap.put(pl, ws);
							if(docInfoDTO.getTaskType().contains("NPI"))
								ws.setNPIflag(true);
							ws.setReviewHeader(Arrays.asList("QA Comment", "Old Eng Comment"),
									false);
							ws.statusValues.remove(0);
							ArrayList<String> sheetHeader = ws.getHeader();
							// int tlCommentIndex = sheetHeader.indexOf("TL Comment");
							int qaCommentIndex = sheetHeader.indexOf("QA Comment");
							int Cactionindex = sheetHeader.indexOf("C_Action");
							int Pactionindex = sheetHeader.indexOf("P_Action");
							int RootcauseIndex = sheetHeader.indexOf("RootCause");
							int Actionduedateindex = sheetHeader.indexOf("ActionDueDate");
							int oldCommentIndex = sheetHeader.indexOf("Old Eng Comment");
							int partnumIndex = sheetHeader.indexOf("Part Number");
							int supIndex = sheetHeader.indexOf("Supplier Name");
							int wrongfetsindex = sheetHeader.indexOf("Wrong Features");
							int fbcommentindex = sheetHeader.indexOf("FBComment");
							int FBStatusindex = sheetHeader.indexOf("FBStatus");
							ArrayList<ArrayList<String>> plData = reviewData.get(pl);
							for(int j = plData.size() - 1; j > -1; j--)
							{
								try
								{
									ArrayList<String> sheetRecord = plData.get(j);
									String partNumber = sheetRecord.get(partnumIndex);
									String supplier = sheetRecord.get(supIndex);
									ArrayList<String> feedCom = DataDevQueryUtil
											.getFeedbackByPartAndSupp(partNumber, supplier);
									String qaComment = DataDevQueryUtil
											.getLastFeedbackCommentByComIdAndSenderGroup(new Long(
													feedCom.get(3)), "QUALITY", null, ParaQueryUtil
													.getPlByPlName(sheetRecord.get(0)));
									// String tlComment = DataDevQueryUtil.getLastFeedbackCommentByComIdAndSenderGroup(new Long(feedCom.get(3)),
									// "Parametric", userDTO.getId(), ParaQueryUtil.getPlByPlName(sheetRecord.get(0)));
									String lastEngcomment = DataDevQueryUtil.getlastengComment(
											new Long(feedCom.get(3)), userDTO.getId());
									GrmUserDTO feedbackIssuer = DataDevQueryUtil
											.getFeedbackIssuerByComId(new Long(feedCom.get(3)));
									String wrongfeatures = DataDevQueryUtil.getfbwrongfets(
											new Long(feedCom.get(3)), feedbackIssuer.getId());
									ParaFeedbackAction action = null;
									action = DataDevQueryUtil.getfeedBackActionByItem(new Long(
											feedCom.get(3)), userDTO.getId());
									if(action != null)
									{
										sheetRecord.set(Cactionindex, action.getCAction());
										sheetRecord.set(Pactionindex, action.getPAction());
										sheetRecord.set(RootcauseIndex, action.getRootCause());
										Date date = action.getActionDueDate();
										SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
										sheetRecord.set(Actionduedateindex, date == null ? "" : sdf
												.format(date).toString());
									}
									for(int l = 0; l < 7; l++)
									{
										sheetRecord.add("");
									}
									sheetRecord.set(FBStatusindex, feedCom.get(6));
									sheetRecord.set(qaCommentIndex, qaComment);
									sheetRecord.set(oldCommentIndex, lastEngcomment);
									sheetRecord.set(2, feedCom.get(1));
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
						tablePanel.setTableData1(0, tablePanel.selectedData);
					}catch(Exception ex)
					{
						ex.printStackTrace();
					}

				}
			}
			/**
			 * Load All Data development FB Sheet
			 */
			else if(event.getActionCommand().equals("Load All"))
			{
				boolean ok = false;
				if(sheetPanel.isOpened())
					ok = ParaQueryUtil.getDialogMessage(
							"another PDF is opend are you need to replace this",
							"Confermation Dailog");

				if(sheetPanel.isOpened() && ok == false)
				{
					MainWindow.glass.setVisible(false);
					return null;
				}
				JComboBox[] combos = filterPanel.comboBoxItems;
				String plName = combos[0].getSelectedItem().toString();
				String supplierName = combos[1].getSelectedItem().toString();
				String feedbackType = combos[2].getSelectedItem().toString();
				String issuedBy = combos[3].getSelectedItem().toString();
				Date startDate = null;
				Date endDate = null;
				Long[] users = { userId };
				Long[] pdfs = new Long[tablePanel.selectedData.size()];
				String[] pdfurls = new String[tablePanel.selectedData.size()];
				for(int i = 0; i < tablePanel.selectedData.size(); i++)
				{
					pdfurls[i] = tablePanel.selectedData.get(i).getPdfUrl();
					Document document = ParaQueryUtil.getDocumnetByPdfUrl(pdfurls[i]);
					pdfs[i] = document.getId();

				}
				if(filterPanel.jDateChooser1.isEnabled())
				{
					startDate = filterPanel.jDateChooser1.getDate();
					endDate = filterPanel.jDateChooser2.getDate();
				}
				try
				{

					// Map<String, ArrayList<ArrayList<String>>> reviewData = ParaQueryUtil.getParametricValueReview1(users, plName,
					Map<String, ArrayList<ArrayList<String>>> reviewData = DataDevQueryUtil
							.getFeedbackParametricValueReview(users, plName, supplierName,
									StatusName.engFeedback, feedbackType, issuedBy, startDate,
									endDate, pdfs, userDTO.getId());
					int k = 0;
					wsMap.clear();
					tabbedPane.setSelectedIndex(1);
					sheetPanel.openOfficeDoc();
					for(String pl : reviewData.keySet())
					{
						ws = new WorkingSheet(sheetPanel, pl, k);
						sheetPanel
								.saveDoc("C:/Report/Parametric_Auto/" + plName + "@"
										+ userDTO.getFullName() + "@" + System.currentTimeMillis()
										+ ".xls");
						wsMap.put(pl, ws);
						if(DataDevQueryUtil.isNPITaskType(users, pl, supplierName, null,
								StatusName.engFeedback, startDate, endDate, null))
							ws.setNPIflag(true);
						ws.setReviewHeader(Arrays.asList("QA Comment", "Old Eng Comment"), false);
						ws.statusValues.remove(0);
						ArrayList<String> sheetHeader = ws.getHeader();
						// int tlCommentIndex = sheetHeader.indexOf("TL Comment");
						int qaCommentIndex = sheetHeader.indexOf("QA Comment");
						int Cactionindex = sheetHeader.indexOf("C_Action");
						int Pactionindex = sheetHeader.indexOf("P_Action");
						int RootcauseIndex = sheetHeader.indexOf("RootCause");
						int Actionduedateindex = sheetHeader.indexOf("ActionDueDate");
						int oldCommentIndex = sheetHeader.indexOf("Old Eng Comment");
						int partnumIndex = sheetHeader.indexOf("Part Number");
						int supIndex = sheetHeader.indexOf("Supplier Name");
						int wrongfetsindex = sheetHeader.indexOf("Wrong Features");
						int fbcommentindex = sheetHeader.indexOf("FBComment");
						int FBStatusindex = sheetHeader.indexOf("FBStatus");
						ArrayList<ArrayList<String>> plData = reviewData.get(pl);
						for(int j = 0; j < plData.size(); j++)
						{
							ArrayList<String> sheetRecord = plData.get(j);
							String partNumber = sheetRecord.get(partnumIndex);
							String supplier = sheetRecord.get(supIndex);
							ArrayList<String> feedCom = DataDevQueryUtil.getFeedbackByPartAndSupp(
									partNumber, supplier);
							String qaComment = DataDevQueryUtil
									.getLastFeedbackCommentByComIdAndSenderGroup(
											new Long(feedCom.get(3)), "QUALITY", null,
											ParaQueryUtil.getPlByPlName(sheetRecord.get(0)));
							// String tlComment = DataDevQueryUtil.getLastFeedbackCommentByComIdAndSenderGroup(new Long(feedCom.get(3)), "Parametric",
							// userDTO.getId(), ParaQueryUtil.getPlByPlName(sheetRecord.get(0)));
							String lastEngcomment = DataDevQueryUtil.getlastengComment(new Long(
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
							}
							for(int l = 0; l < 7; l++)
							{
								sheetRecord.add("");
							}
							sheetRecord.set(FBStatusindex, feedCom.get(6));
							sheetRecord.set(qaCommentIndex, qaComment);
							sheetRecord.set(oldCommentIndex, lastEngcomment);
							sheetRecord.set(2, feedCom.get(1));
							sheetRecord.set(wrongfetsindex, wrongfeatures);
							sheetRecord.set(fbcommentindex, feedCom.get(0));
							plData.set(j, sheetRecord);
						}
						ws.writeReviewData(plData, 2, 4);
						k++;
					}

				}catch(Exception ex)
				{
					ex.printStackTrace();
				}

			}
			else if(event.getActionCommand().equals("Save"))
			{
				for(String wsName : wsMap.keySet())
				{
					if(wsName != "LoadAllData" && wsName != "Separation")
					{

						WorkingSheet ws = wsMap.get(wsName);
						if(!ws.saved)
						{
							ws.saveEngFeedbackAction(userName);
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
			else if(event.getActionCommand().equals("Validate"))
			{
				for(String wsName : wsMap.keySet())
				{
					if(wsName != "LoadAllData" && wsName != "Separation")
					{
						WorkingSheet ws = wsMap.get(wsName);
						ws.validateEngFBParts(true);
					}
				}
				MainWindow.glass.setVisible(false);
				JOptionPane.showMessageDialog(null, "Validation Finished");
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
						List<ApprovedParametricDTO> approved = ApprovedDevUtil
								.createApprovedValuesList(featureFullValue, plName, featureName,
										row.get(7), row.get(8), row.get(9), row.get(12),
										row.get(13), row.get(11), row.get(10));
						try
						{
							ApprovedDevUtil.saveAppGroupAndSepValue(0, 0, approved, plName,
									featureName, featureFullValue, row.get(2), userId);
						}catch(Exception ex)
						{
							ex.printStackTrace();
						}

						List<String> appValues = wsMap.get(plName).getApprovedFeatuer()
								.get(featureName);
						appValues.add(featureFullValue);
					}
					MainWindow.glass.setVisible(false);
					JOptionPane.showMessageDialog(null, "Approved Saving Done");
				}
			}
			MainWindow.glass.setVisible(false);
			return null;
		}
	}

	public void clearOfficeResources()
	{
		if(sheetPanel != null)
		{
			sheetPanel.closeApplication();
		}
		if(separationPanel != null)
		{
			separationPanel.closeApplication();
		}
	}

}
