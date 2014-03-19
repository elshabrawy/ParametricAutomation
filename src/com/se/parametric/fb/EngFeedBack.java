package com.se.parametric.fb;

import java.awt.Color;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.BevelBorder;
import javax.swing.border.SoftBevelBorder;

import org.hibernate.mapping.Set;

import osheet.SheetPanel;
import osheet.WorkingSheet;

import com.se.automation.db.client.mapping.Document;
import com.se.automation.db.client.mapping.ParaFeedbackAction;
import com.se.automation.db.client.mapping.PartComponent;
import com.se.automation.db.client.mapping.Supplier;
import com.se.automation.db.client.mapping.TrackingFeedbackType;
import com.se.automation.db.client.mapping.TrackingParametric;
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
import com.se.parametric.dto.ApprovedParametricDTO;
import com.se.parametric.dto.GrmUserDTO;
import com.se.parametric.dto.TableInfoDTO;
import com.se.parametric.util.StatusName;

public class EngFeedBack extends JPanel implements ActionListener
{

	private long userId;
	private TablePanel tablePanel;
	private FilterPanel filterPanel;
	private ButtonsPanel buttonsPanel, devButtonsPanel;
	private JTabbedPane tabbedPane;
	SheetPanel sheetPanel = new SheetPanel();
	SheetPanel separationPanel = new SheetPanel();
	JPanel sheetTab, separationTab, separationButtonPanel, selectionPanel;
	private String userName;
	private WorkingSheet ws;
	private Map<String, WorkingSheet> wsMap;
	ArrayList<ArrayList<String>> input = new ArrayList<ArrayList<String>>();
	ArrayList<ArrayList<String>> separationValues = new ArrayList<ArrayList<String>>();
	static AlertsPanel alertsPanel, alertsPanel1, alertsPanel2;
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
		setLayout(null);
		userName = userDTO.getFullName();
		int width = Toolkit.getDefaultToolkit().getScreenSize().width;
		int height = Toolkit.getDefaultToolkit().getScreenSize().height;
		wsMap = new HashMap<String, WorkingSheet>();
		userId = userDTO.getId();
		ArrayList<Object[]> filterData = DataDevQueryUtil.getUserFeedbackData(userDTO, null, null);
		selectionPanel = new JPanel();
		String[] labels = new String[] { "PdfUrl", "PlName", "SupplierName", "InfectedParts", "InfectedTaxonomies", "Date" };
		String[] filterHeader = { "PL Name", "Supplier", "Feedback Type", "Issued By" };
		tablePanel = new TablePanel(labels, width - 120, (((height - 100) * 7) / 10));
		tablePanel.setBounds(0, (((height - 100) * 3) / 10), width - 120, (((height - 100) * 7) / 10));
		tablePanel.setBorder(new SoftBevelBorder(BevelBorder.RAISED, null, null, null, null));
		filterPanel = new FilterPanel(filterHeader, filterData, width - 120, (((height - 100) * 3) / 10));
		filterPanel.setBounds(0, 0, width - 120, (((height - 100) * 3) / 10));
		ArrayList<String> buttonLabels = new ArrayList<String>();
		buttonLabels.add("LoadSheet");
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
		ArrayList<String> devButtonLabels = new ArrayList<String>();
		devButtonLabels.add("Separation");
		devButtonLabels.add("Validate");
		devButtonLabels.add("Save");
		devButtonsPanel = new ButtonsPanel(devButtonLabels);
		devButtonsPanel.setBounds(width - 120, 0, 108, height / 3);
		buttons = devButtonsPanel.getButtons();
		for(int i = 0; i < buttons.length; i++)
		{
			buttons[i].addActionListener(this);
		}
		filterPanel.filterButton.addActionListener(this);
		filterPanel.refreshButton.addActionListener(this);
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(0, 0, width, height - 100);
		sheetTab = new JPanel();
		sheetTab.setLayout(null);
		sheetPanel.setBounds(0, 0, width - 120, height - 125);
		sheetTab.add(sheetPanel);
		sheetTab.add(devButtonsPanel);
		sheetTab.add(alertsPanel1);

		separationButtonPanel = new JPanel();
		separationButtonPanel.setBorder(new SoftBevelBorder(BevelBorder.RAISED, null, null, null, null));
		separationButtonPanel.setBounds(width - 120, 0, 108, height / 3);
		separationButtonPanel.setLayout(null);
		separationButtonPanel.setBackground(new Color(211, 211, 211));
		JButton separationSave = new JButton(" Save ");
		separationSave.setBounds(3, 11, 85, 29);
		separationSave.setForeground(new Color(25, 25, 112));
		separationSave.setFont(new Font("Tahoma", Font.BOLD, 11));
		separationSave.addActionListener(this);
		separationButtonPanel.add(separationSave);
		separationPanel.setBounds(0, 0, width - 120, height - 125);
		separationTab = new JPanel();
		separationTab.setLayout(null);
		separationTab.add(separationPanel);
		separationTab.add(separationButtonPanel);
		separationTab.add(alertsPanel2);

		tabbedPane.addTab("PDF Links", null, selectionPanel, null);
		tabbedPane.addTab("Sheet", null, sheetTab, null);
		tabbedPane.addTab("Separation Sheet", null, separationTab, null);
		add(tabbedPane);

	}

	@Override
	public void actionPerformed(ActionEvent event)
	{
		Loading loading = new Loading();
		Thread thread = new Thread(loading);
		thread.start();
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

			tablePanel.selectedData = DataDevQueryUtil.getDevFeedbackPDF(userId, plName, supplierName, issuedBy, feedbackType, startDate, endDate);
			tablePanel.setTableData1(0, tablePanel.selectedData);

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
			filterPanel.filterList = DataDevQueryUtil.getUserFeedbackData(userDTO, startDate, endDate);
			tablePanel.clearTable();
			filterPanel.refreshFilters();

		}
		/**
		 * Load Data development Sheet
		 */
		else if(event.getActionCommand().equals("LoadSheet"))
		{
			boolean ok = false;
			if(sheetPanel.isOpened())
				ok = ParaQueryUtil.getDialogMessage("another PDF is opend are you need to replace this", "Confermation Dailog");

			if(sheetPanel.isOpened() && ok == false)
			{
				thread.stop();
				loading.frame.dispose();
				return;
			}
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
					TableInfoDTO docInfoDTO = tablePanel.selectedData.get(selectedPdfs[0]);
					String pdfUrl = docInfoDTO.getPdfUrl();
					System.out.println(pdfUrl);
					Document document = ParaQueryUtil.getDocumnetByPdfUrl(pdfUrl);
					tablePanel.loadedPdfs.add(pdfUrl);
					JComboBox[] combos = filterPanel.comboBoxItems;
					String plName = combos[0].getSelectedItem().toString();
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
					Map<String, ArrayList<ArrayList<String>>> reviewData = DataDevQueryUtil.getFeedbackParametricValueReview(users, plName, supplierName, StatusName.engFeedback, feedbackType, issuedBy, startDate, endDate,
							new Long[] { document.getId() }, userDTO.getId());
					int k = 0;
					for(String pl : reviewData.keySet())
					{
						ws = new WorkingSheet(sheetPanel, pl, k);
						sheetPanel.saveDoc("C:/Report/Parametric_Auto/" + pdfUrl.replaceAll(".*/", "") + "@" + userDTO.getFullName() + "@" + System.currentTimeMillis() + ".xls");
						wsMap.put(pl, ws);
						if(docInfoDTO.getTaskType().contains("NPI"))
							ws.setNPIflag(true);
						ws.setReviewHeader(Arrays.asList("TL Comment", "QA Comment", "Old Eng Comment"), false);
						ws.statusValues.remove(0);
						ArrayList<String> sheetHeader = ws.getHeader();
						int tlCommentIndex = sheetHeader.indexOf("TL Comment");
						int qaCommentIndex = sheetHeader.indexOf("QA Comment");
						int Cactionindex = sheetHeader.indexOf("C_Action");
						int Pactionindex = sheetHeader.indexOf("P_Action");
						int RootcauseIndex = sheetHeader.indexOf("RootCause");
						int Actionduedateindex = sheetHeader.indexOf("ActionDueDate");
						int oldCommentIndex = sheetHeader.indexOf("Old Eng Comment");
						ArrayList<ArrayList<String>> plData = reviewData.get(pl);
						for(int j = 0; j < plData.size(); j++)
						{
							ArrayList<String> sheetRecord = plData.get(j);
							String partNumber = sheetRecord.get(11);

							ArrayList<String> feedCom = DataDevQueryUtil.getFeedbackByPartAndSupp(partNumber, sheetRecord.get(10));// feedcom 0 is
																																	// unused since we
																																	// show comments
																																	// of tl and QA
							String qaComment = DataDevQueryUtil.getLastFeedbackCommentByComIdAndSenderGroup(new Long(feedCom.get(3)), "QUALITY", null, ParaQueryUtil.getPlByPlName(sheetRecord.get(0)));
							String tlComment = DataDevQueryUtil.getLastFeedbackCommentByComIdAndSenderGroup(new Long(feedCom.get(3)), "Parametric", userDTO.getId(), ParaQueryUtil.getPlByPlName(sheetRecord.get(0)));
							String lastEngcomment = DataDevQueryUtil.getlastengComment(new Long(feedCom.get(3)), userDTO.getId());
							ParaFeedbackAction action = null;
							action = DataDevQueryUtil.getfeedBackActionByItem(new Long(feedCom.get(3)), userDTO.getId());
							if(action != null)
							{
								sheetRecord.set(Cactionindex, action.getCAction());
								sheetRecord.set(Pactionindex, action.getPAction());
								sheetRecord.set(RootcauseIndex, action.getRootCause());
								sheetRecord.set(Actionduedateindex, action.getActionDueDate().toString());
							}
							for(int l = 0; l < 6; l++)
							{
								sheetRecord.add("");
							}
							sheetRecord.set(tlCommentIndex, tlComment);
							sheetRecord.set(qaCommentIndex, qaComment);
							sheetRecord.set(oldCommentIndex, lastEngcomment);
							sheetRecord.set(2, feedCom.get(1));
							plData.set(j, sheetRecord);
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
				ok = ParaQueryUtil.getDialogMessage("another PDF is opend are you need to replace this", "Confermation Dailog");

			if(sheetPanel.isOpened() && ok == false)
			{
				thread.stop();
				loading.frame.dispose();
				return;
			}
			JComboBox[] combos = filterPanel.comboBoxItems;
			String plName = combos[0].getSelectedItem().toString();
			String supplierName = combos[1].getSelectedItem().toString();
			String feedbackType = combos[2].getSelectedItem().toString();
			String issuedBy = combos[3].getSelectedItem().toString();
			Date startDate = null;
			Date endDate = null;
			Long[] users = { userId };
			if(filterPanel.jDateChooser1.isEnabled())
			{
				startDate = filterPanel.jDateChooser1.getDate();
				endDate = filterPanel.jDateChooser2.getDate();
			}
			try
			{

				// Map<String, ArrayList<ArrayList<String>>> reviewData = ParaQueryUtil.getParametricValueReview1(users, plName,
				Map<String, ArrayList<ArrayList<String>>> reviewData = DataDevQueryUtil.getFeedbackParametricValueReview(users, plName, supplierName, StatusName.engFeedback, feedbackType, issuedBy, startDate, endDate, null, userDTO.getId());
				int k = 0;
				wsMap.clear();
				tabbedPane.setSelectedIndex(1);
				sheetPanel.openOfficeDoc();
				for(String pl : reviewData.keySet())
				{
					ws = new WorkingSheet(sheetPanel, pl, k);
					sheetPanel.saveDoc("C:/Report/Parametric_Auto/" + plName + "@" + userDTO.getFullName() + "@" + System.currentTimeMillis() + ".xls");
					wsMap.put(pl, ws);
					if(DataDevQueryUtil.isNPITaskType(users, pl, supplierName, null, StatusName.engFeedback, startDate, endDate, null))
						ws.setNPIflag(true);
					ws.setReviewHeader(Arrays.asList("TL Comment", "QA Comment"), false);
					ws.statusValues.remove(0);
					ArrayList<String> sheetHeader = ws.getHeader();
					int tlCommentIndex = sheetHeader.indexOf("TL Comment");
					int qaCommentIndex = sheetHeader.indexOf("QA Comment");
					int Cactionindex = sheetHeader.indexOf("C_Action");
					int Pactionindex = sheetHeader.indexOf("P_Action");
					int RootcauseIndex = sheetHeader.indexOf("RootCause");
					int Actionduedateindex = sheetHeader.indexOf("ActionDueDate");
					ArrayList<ArrayList<String>> plData = reviewData.get(pl);
					for(int j = 0; j < plData.size(); j++)
					{
						ArrayList<String> sheetRecord = plData.get(j);
						String partNumber = sheetRecord.get(11);
						// supplierName = sheetRecord.get(5);
						// Supplier supplier = ParaQueryUtil.getSupplierByName(sheetRecord.get(10));
						// PartComponent com = DataDevQueryUtil.getComponentByPartNumberAndSupplierName(partNumber, sheetRecord.get(10));

						ArrayList<String> feedCom = DataDevQueryUtil.getFeedbackByPartAndSupp(partNumber, sheetRecord.get(10));// feedcom 0 is
						// unused since we
						// show comments
						// of tl and QA
						String qaComment = DataDevQueryUtil.getLastFeedbackCommentByComIdAndSenderGroup(new Long(feedCom.get(3)), "Quality_Parametric", null, ParaQueryUtil.getPlByPlName(sheetRecord.get(0)));
						String tlComment = DataDevQueryUtil.getLastFeedbackCommentByComIdAndSenderGroup(new Long(feedCom.get(3)), "Parametric", userDTO.getId(), ParaQueryUtil.getPlByPlName(sheetRecord.get(0)));

						// String comment = DataDevQueryUtil.getFeedbackCommentByComId(new Long(feedCom.get(3)));
						// GrmUserDTO issuer = DataDevQueryUtil.getFeedbackIssuerByComId(new Long(feedCom.get(3)));
						ParaFeedbackAction action = null;
						action = DataDevQueryUtil.getfeedBackActionByItem(new Long(feedCom.get(3)), userDTO.getId());
						if(action != null)
						{
							sheetRecord.set(Cactionindex, action.getCAction());
							sheetRecord.set(Pactionindex, action.getPAction());
							sheetRecord.set(RootcauseIndex, action.getRootCause());
							sheetRecord.set(Actionduedateindex, action.getActionDueDate().toString());
						}
						for(int l = 0; l < 6; l++)
						{
							sheetRecord.add("");
						}

						sheetRecord.set(tlCommentIndex, tlComment);
						sheetRecord.set(qaCommentIndex, qaComment);
						sheetRecord.set(2, feedCom.get(1));
						// sheetRecord.set(2, status);
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
					ws.saveEngFeedbackAction(userName);
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
					ws.validateParts(true);
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
			separationPanel.saveDoc("C:/Report/Parametric_Auto/" + "Separation@" + userDTO.getFullName() + "@" + System.currentTimeMillis() + ".xls");
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
			ArrayList<String> flags = ParaQueryUtil.getAlerts(uDTO.getId(), 1, 3);
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

}
