package com.se.parametric.dev;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingWorker;
import javax.swing.border.BevelBorder;
import javax.swing.border.SoftBevelBorder;

import org.hibernate.Session;

import osheet.Cell;
import osheet.SheetPanel;
import osheet.WorkingSheet;

import com.se.automation.db.SessionUtil;
import com.se.automation.db.client.mapping.Pl;
import com.se.automation.db.parametric.StatusName;
import com.se.grm.client.mapping.GrmGroup;
import com.se.grm.client.mapping.GrmRole;
import com.se.parametric.Loading;
import com.se.parametric.LoginForm;
import com.se.parametric.MainWindow;
import com.se.parametric.autoFill.AutoFill;
import com.se.parametric.commonPanel.AlertsPanel;
import com.se.parametric.commonPanel.ButtonsPanel;
import com.se.parametric.commonPanel.FilterPanel;
import com.se.parametric.commonPanel.TablePanel;
import com.se.parametric.dba.ApprovedDevUtil;
import com.se.parametric.dba.DataDevQueryUtil;
import com.se.parametric.dba.ParaQueryUtil;
import com.se.parametric.dto.ApprovedParametricDTO;
import com.se.parametric.dto.DocumentInfoDTO;
import com.se.parametric.dto.GrmUserDTO;
import com.se.parametric.dto.TableInfoDTO;
import com.se.parametric.fb.SourcingFeedbackPanel;

public class Developement extends JPanel implements ActionListener
{
	/**
	 * @wbp.nonvisual location=120,351
	 */
	DocumentInfoDTO documentInfoDTO = null;
	SheetPanel sheetpanel = new SheetPanel();
	SheetPanel separationPanel = new SheetPanel();
	WorkingSheet ws;
	PdfLinks pdfLinks = null;
	ArrayList<ArrayList<String>> input = new ArrayList<ArrayList<String>>();
	JPanel selectionPanel, tabSheet, separationTab, alerts/* , flowChart */;
	JPanel devSheetButtonPanel, separationButtonPanel;
	// Update mainInfo = new Update();
	JTabbedPane tabbedPane;
	JButton button = null;// new JButton("LoadSheet");
	JButton showAll = new JButton("Show All");
	JButton save, separationSave, srcFeedbackBtn, AutoFill;
	JButton validate, separation;
	Map<String, WorkingSheet> wsMap = new HashMap<String, WorkingSheet>();
	ArrayList<ArrayList<String>> separationValues = new ArrayList<ArrayList<String>>();
	boolean foundPdf = false;
	Long userId = 0l;
	TablePanel tablePanel = null;
	FilterPanel filterPanel = null;
	ButtonsPanel buttonsPanel, sheetButtonsPanel, separationButtonsPanel;
	static AlertsPanel alertsPanel, alertsPanel1, alertsPanel2;
	String userName;
	GrmUserDTO userDTO = null;
	AutoFill autoFillProcess;
	boolean validated;

	/**
	 * Create the panel.
	 */

	public Developement(GrmUserDTO userDTO)
	{
		setLayout(null);
		this.userDTO = userDTO;
		this.userName = userDTO.getFullName();
		int width = Toolkit.getDefaultToolkit().getScreenSize().width;
		int height = Toolkit.getDefaultToolkit().getScreenSize().height;
		// =======================================================
		userId = userDTO.getId();
		ArrayList<Object[]> filterData = DataDevQueryUtil.getUserData(userDTO, null, null);
		selectionPanel = new JPanel();
		String[] labels = new String[] { "PdfUrl", "PlName", "SupplierName", "TaskType",
				"Extracted", "Priority", "AssginedDate" };
		String[] filterHeader = { "PL Name", "Supplier Name", "Task Type", "Extracted", "Priority" };
		tablePanel = new TablePanel(labels);
		tablePanel.setBounds(0, (((height - 100) * 3) / 10), width - 120,
				(((height - 100) * 7) / 10));
		tablePanel.setBorder(new SoftBevelBorder(BevelBorder.RAISED, null, null, null, null));
		filterPanel = new FilterPanel(filterHeader, filterData, false);
		filterPanel.setBounds(0, 0, width - 120, (((height - 100) * 3) / 10));
		filterPanel.filterButton.addActionListener(this);
		filterPanel.refreshButton.addActionListener(this);

		ArrayList<String> buttonLabels = new ArrayList<String>();
		buttonLabels.add("LoadSheet");
		buttonLabels.add("Load All");
		buttonLabels.add("Show All");
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

		tabSheet = new JPanel();
		separationTab = new JPanel();
		devSheetButtonPanel = new JPanel();
		devSheetButtonPanel.setBackground(new Color(211, 211, 211));
		devSheetButtonPanel.setBorder(new SoftBevelBorder(BevelBorder.RAISED, null, null, null,
				null));
		devSheetButtonPanel.setBounds(width - 120, 0, 108, height / 3);
		devSheetButtonPanel.setLayout(null);
		save = new JButton("Save");
		save.setBounds(3, 82, 95, 29);
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
		srcFeedbackBtn = new JButton("SRC Feedback");
		srcFeedbackBtn.setBounds(3, 118, 95, 29);
		srcFeedbackBtn.setForeground(new Color(25, 25, 112));
		srcFeedbackBtn.setFont(new Font("Tahoma", Font.BOLD, 11));

		AutoFill = new JButton("AutoFill");
		AutoFill.setBounds(3, 153, 95, 29);
		AutoFill.setForeground(new Color(25, 25, 112));
		AutoFill.setFont(new Font("Tahoma", Font.BOLD, 11));

		// validate.addActionListener(this);
		save.addActionListener(this);
		// separation.addActionListener(this);
		srcFeedbackBtn.addActionListener(this);
		AutoFill.addActionListener(this);

		// devSheetButtonPanel.add(separation);
		// devSheetButtonPanel.add(validate);
		devSheetButtonPanel.add(save);
		devSheetButtonPanel.add(srcFeedbackBtn);
		devSheetButtonPanel.add(AutoFill);

		// devSheetButtonPanel.setBackground(new Color(211, 211, 211));
		// separationButtonPanel = new JPanel();
		// separationButtonPanel.setBorder(new SoftBevelBorder(BevelBorder.RAISED, null, null, null, null));
		// separationButtonPanel.setBounds(width - 120, 0, 106, height - 100);
		// separationButtonPanel.setLayout(null);
		// separationSave = new JButton("Save");
		// separationSave.setBounds(3, 11, 85, 29);
		// separationSave.addActionListener(this);
		// separationButtonPanel.add(separationSave);
		sheetpanel.setBounds(0, 0, width - 120, height - 125);
		separationPanel.setBounds(0, 0, width - 120, height - 125);
		ArrayList<String> separationButtonLabels = new ArrayList<String>();
		// separationButtonLabels.add(" validate ");
		separationButtonLabels.add(" Save ");
		separationButtonsPanel = new ButtonsPanel(separationButtonLabels);
		JButton separationButtons[] = separationButtonsPanel.getButtons();
		for(int i = 0; i < separationButtons.length; i++)
		{
			separationButtons[i].addActionListener(this);
		}
		separationButtonsPanel.setBounds(width - 120, 0, 108, height / 3);

		selectionPanel.setLayout(null);
		selectionPanel.add(filterPanel);
		selectionPanel.add(tablePanel);
		selectionPanel.add(buttonsPanel);
		selectionPanel.add(alertsPanel);

		tabSheet.setLayout(null);
		tabSheet.add(sheetpanel);
		tabSheet.add(devSheetButtonPanel);
		tabSheet.add(alertsPanel1);

		separationTab.setLayout(null);
		separationTab.add(separationPanel);
		separationTab.add(separationButtonsPanel);
		separationTab.add(alertsPanel2);

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(0, 0, width, height - 100);

		add(tabbedPane);

		// ArrayList<String> sheetButtonLabels = new ArrayList<String>();
		// sheetButtonLabels.add("Separation");
		// sheetButtonLabels.add("Validate");
		// sheetButtonLabels.add("Save");
		// sheetButtonLabels.add("SRC Feedback");
		// sheetButtonLabels.add("AutoFill");
		// sheetButtonsPanel = new ButtonsPanel(sheetButtonLabels);
		// JButton sheetButtons[] = separationButtonsPanel.getButtons();
		// for(int i = 0; i < separationButtons.length; i++)
		// {
		// sheetButtons[i].addActionListener(this);
		// }
		// sheetButtonsPanel.setBounds(width - 120, 0, 110, height / 3);
		// tabSheet.add(sheetButtonsPanel);

		tabbedPane.addTab("Input Selection", null, selectionPanel, null);
		tabbedPane.addTab("Sheet", null, tabSheet, null);
		tabbedPane.addTab("Separation", null, separationTab, null);
		// flowChart = new ImagePanel("Development-chart.jpg");
		// tabbedPane.addTab("Development Flow", null, flowChart, null);
	}

	@Override
	public void actionPerformed(ActionEvent event)
	{
		System.out.println("start here");

		LongRunProcess longRunProcess = new LongRunProcess(event);
		longRunProcess.execute();
	}

	public static void main(String[] args)
	{
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		int width = Toolkit.getDefaultToolkit().getScreenSize().width;
		int height = Toolkit.getDefaultToolkit().getScreenSize().height;
		frame.setSize(width, height);
		frame.setTitle("Developement");
		GrmUserDTO uDTO = new GrmUserDTO();
		// uDTO.setId(119);
		// uDTO.setFullName("ahmed hasanin");
		uDTO.setId(376);
		uDTO.setFullName("salah_shiha");
		GrmRole role = new GrmRole();
		role.setId(3l);
		GrmGroup group = new GrmGroup();
		group.setId(1l);
		uDTO.setGrmRole(role);
		uDTO.setGrmGroup(group);
		Developement devPanel = new Developement(uDTO);
		frame.getContentPane().add(devPanel);
		frame.show();
		while(true)
		{
			ArrayList<String> flags = ParaQueryUtil.getAlerts(uDTO.getId(), 1, 3);
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
			// Loading.show();
			MainWindow.glass.setVisible(true);
			String pdfUrl = "";
			ArrayList<DocumentInfoDTO> docsInfo = null;
			ArrayList<String> row = null;

			/**
			 * Load Data development Sheet
			 */
			if(event.getActionCommand().equals("LoadSheet"))
			{
				boolean ok = false;
				if(sheetpanel.isOpened())
				{
					MainWindow.glass.setVisible(false);
					ok = ParaQueryUtil.getDialogMessage(
							"another PDF is opend are you need to replace this",
							"Confermation Dailog");
				}

				if(sheetpanel.isOpened() && ok == false)
				{
					MainWindow.glass.setVisible(false);

					return null;
				}
				else if(sheetpanel.isOpened() && ok == true)
				{

				}

				separation.setEnabled(true);
				validate.setEnabled(true);
				save.setEnabled(true);
				for(String wsName : wsMap.keySet())
				{
					System.out.println(wsName);
					if(wsName == "Separation")
					{
						ws = new WorkingSheet(separationPanel, "Separation");
						separationPanel
								.saveDoc("C:/Report/Parametric_Auto/" + "Separation@"
										+ userDTO.getFullName() + "@" + System.currentTimeMillis()
										+ ".xls");
					}
				}
				int selectedPdfs[] = tablePanel.table.getSelectedRows();
				System.out.println(selectedPdfs.length);
				if(selectedPdfs.length == 0)
				{
					MainWindow.glass.setVisible(false);
					JOptionPane.showMessageDialog(null, "Please Select PDF First");
				}
				else if(selectedPdfs.length > 1)
				{
					MainWindow.glass.setVisible(false);

					JOptionPane.showMessageDialog(null, "Please Select One PDF");

				}
				else
				{
					wsMap.clear();
					int selectedDataIndex = (tablePanel.getCurrentPage() - 1)
							* tablePanel.getRecordsPerPage() + selectedPdfs[0];
					// TableInfoDTO tableInfoDTO = tablePanel.selectedData.get(selectedPdfs[0]);
					TableInfoDTO tableInfoDTO = tablePanel.selectedData.get(selectedDataIndex);
					pdfUrl = tableInfoDTO.getPdfUrl();
					docsInfo = ParaQueryUtil.getParametricPLsByPdfUrl(pdfUrl, userId);
					tabbedPane.setSelectedIndex(1);
					sheetpanel.openOfficeDoc();
					String suppName = "";
					String plName = "";
					for(int i = 0; i < docsInfo.size(); i++)
					{
						List<String> newsData = null;
						long pdfId = -1;
						DocumentInfoDTO docInfo = docsInfo.get(i);
						suppName = docInfo.getSupplierName();
						plName = docInfo.getPlName();
						ws = new WorkingSheet(sheetpanel, plName, i);
						sheetpanel.saveDoc("C:/Report/Parametric_Auto/"
								+ pdfUrl.replaceAll(".*/", "") + "@" + userDTO.getFullName() + "@"
								+ System.currentTimeMillis() + ".xls");
						if(docInfo.getTaskType().contains("NPI"))
						{
							ws.setNPIflag(true);
							// pdfId = ParaQueryUtil.getPdfId(pdfUrl, suppName);
							// newsLink = DataDevQueryUtil.getNewsLink(pdfId);
							newsData = DataDevQueryUtil.getNewsLink(pdfUrl);
						}
						String taxonomies = "";
						List<Pl> pls = ParaQueryUtil.getPLsForPdf(pdfUrl);
						for(Pl pl : pls)
						{
							taxonomies += pl.getName() + "|";
						}
						taxonomies = taxonomies.substring(0, taxonomies.length() - 1);
						ws.setDevHeader(true, false);
						ws.setPdfInfo(pdfUrl, suppName, docInfo.getTitle(), newsData, taxonomies, 2);
						// ws.setSupplierName(suppName);

						ws.setExtractionData1(pdfUrl, suppName, plName, 2);
						wsMap.put(plName, ws);
					}
					tablePanel.loadedPdfs.add(pdfUrl);
					tablePanel.setTableData1(0, tablePanel.selectedData);
					autoFillProcess = new AutoFill(sheetpanel, userName, wsMap.get(plName), plName,
							suppName);
					autoFillProcess.getRules();
				}

				MainWindow.glass.setVisible(false);

			}
			/**
			 * Show all PDFs Sheet Action*
			 */
			else if(event.getActionCommand().equals("Show All"))
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
				separation.setEnabled(false);
				validate.setEnabled(false);
				save.setEnabled(false);
				JComboBox[] combos = filterPanel.comboBoxItems;
				String plName = filterPanel.comboBoxItems[0].getSelectedItem().toString();
				String supplierName = filterPanel.comboBoxItems[1].getSelectedItem().toString();
				String type = filterPanel.comboBoxItems[2].getSelectedItem().toString();
				String extracted = filterPanel.comboBoxItems[3].getSelectedItem().toString();
				String priority = filterPanel.comboBoxItems[4].getSelectedItem().toString();
				Date startDate = null, endDate = null;
				if(filterPanel.jDateChooser1.isEnabled())
				{
					startDate = filterPanel.jDateChooser1.getDate();
					endDate = filterPanel.jDateChooser2.getDate();
				}
				// ArrayList<TableInfoDTO> tableRecs = ParaQueryUtil.getShowAllPDFReview(new Long[] { userId }, plName, supplierName, type,
				// extracted, "Assigned", startDate, endDate, null);
				ArrayList<TableInfoDTO> tableRecs = DataDevQueryUtil.getShowAllData(userId, plName,
						supplierName, type, extracted, "Assigned", startDate, endDate, priority);
				tablePanel.setTableData1(0, tableRecs);
				tabbedPane.setSelectedIndex(1);
				sheetpanel.openOfficeDoc();
				ws = new WorkingSheet(sheetpanel, "LoadAllData");
				sheetpanel.saveDoc("C:/Report/Parametric_Auto/" + "LoadAllData@"
						+ userDTO.getFullName() + "@" + System.currentTimeMillis() + ".xls");
				ws.getShowAllData(tableRecs);
				wsMap.put("LoadAllData", ws);
				MainWindow.glass.setVisible(false);
			}
			/**
			 * Load all PDFs for certain pl and supplier*
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
				separation.setEnabled(true);
				validate.setEnabled(true);
				save.setEnabled(true);
				wsMap.clear();
				JComboBox[] combos = filterPanel.comboBoxItems;
				String plName = filterPanel.comboBoxItems[0].getSelectedItem().toString();
				String supplierName = filterPanel.comboBoxItems[1].getSelectedItem().toString();
				String type = filterPanel.comboBoxItems[2].getSelectedItem().toString();
				String extracted = filterPanel.comboBoxItems[3].getSelectedItem().toString();
				String priority = filterPanel.comboBoxItems[4].getSelectedItem().toString();
				Date startDate = null, endDate = null;
				if(filterPanel.jDateChooser1.isEnabled())
				{
					startDate = filterPanel.jDateChooser1.getDate();
					endDate = filterPanel.jDateChooser2.getDate();
				}
				if("All".equals(plName))
				{
					MainWindow.glass.setVisible(false);

					JOptionPane.showMessageDialog(null, "Please, Select a PL.", "Error!",
							JOptionPane.ERROR_MESSAGE);

					return null;
				}
				int count = filterPanel.comboBoxItems[2].getItemCount();
				StringBuilder typeBuilder = new StringBuilder();
				for(int i = 0; i < count; i++)
				{
					typeBuilder.append(filterPanel.comboBoxItems[2].getItemAt(i));
				}
				tabbedPane.setSelectedIndex(1);
				sheetpanel.openOfficeDoc();
				System.out.println("PL Name : " + plName + "\nSupplier Name : " + supplierName
						+ " " + typeBuilder.toString());

				// ArrayList<TableInfoDTO> tableRecs = ParaQueryUtil.getReviewPDF(new Long[] { userId }, plName, supplierName, type, extracted,
				// "Assigned", startDate, endDate, null, "assigned", priority);
				ArrayList<TableInfoDTO> tableRecs = DataDevQueryUtil.getAllAssigined(userId,
						plName, supplierName, type, extracted, "Assigned", startDate, endDate,
						priority);

				ws = new WorkingSheet(sheetpanel, plName, 0);
				sheetpanel.saveDoc("C:/Report/Parametric_Auto/" + plName + "@"
						+ userDTO.getFullName() + "@" + System.currentTimeMillis() + ".xls");
				if((typeBuilder.toString().contains("NPI") && type.equals("All"))
						|| (type.contains("NPI")))
				{
					ws.setNPIflag(true);
				}
				ws.setDevHeader(true, false);
				// ws.setSupplierName(supplierName);

				for(int i = 0; i < tableRecs.size(); i++)
				{
					TableInfoDTO tableRecord = tableRecs.get(i);
					pdfUrl = tableRecord.getPdfUrl();
					// Document doc = ParaQueryUtil.getDocumnetByPdfUrl(pdfUrl);
					// String desc = doc.getTitle();
					String desc = tableRecord.getTitle();
					supplierName = tableRecord.getSupplierName();
					List<String> newsData = null;

					long pdfId = -1;
					if(tableRecord.getTaskType().contains("NPI"))
					{
						// pdfId = ParaQueryUtil.getPdfId(pdfUrl, supplierName);
						// if(pdfId != -1)
						// {
						// newsLink = DataDevQueryUtil.getNewsLink(pdfId);
						// }
						newsData = DataDevQueryUtil.getNewsLink(pdfUrl);
					}
					String taxonomies = "";
					// List<Pl> pls = ParaQueryUtil.getPLsForPdf(pdfUrl);
					// for(Pl pl : pls)
					// {
					// taxonomies += pl.getName() + "|";
					// }
					// taxonomies = taxonomies.substring(0, taxonomies.length() - 1);
					taxonomies = tableRecord.getTaxonomies();
					ws.setPdfInfo(pdfUrl, supplierName, desc, newsData, taxonomies, i + 2);
					if(tableRecord.getExtracted().equals("Yes"))
						ws.setExtractionData1(pdfUrl, supplierName, plName, i + 2);
					System.out.println("PDF_No: " + i + " ~ Extr: " + tableRecord.getExtracted()
							+ " ~ NPI: " + tableRecord.getTaskType());
				}

				wsMap.put(plName, ws);
				MainWindow.glass.setVisible(false);
			}

			else if(event.getActionCommand().equals("AutoFill"))
			{

				String userName = userDTO.getFullName();
				System.err.println("start Check" + new Date());
				autoFillProcess.getAutoFillProcess();
				wsMap.put("Separation", ws);
				MainWindow.glass.setVisible(false);
			}
			/**
			 * Load Separation Sheet Action
			 * **/
			else if(event.getActionCommand().equals("Separation"))
			{

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

					// validation of separation
					validateseparation();
					// save Separation
					saveseparation();

					MainWindow.glass.setVisible(false);
					int reply = JOptionPane.showConfirmDialog(null,
							"Approved Saving Done , Press OK to save Parts", "Development",
							JOptionPane.OK_OPTION);
					if(reply == JOptionPane.OK_OPTION)
					{
						MainWindow.glass.setVisible(true);
						save.doClick();
						tabbedPane.setSelectedIndex(1);
					}

				}
				MainWindow.glass.setVisible(false);
			}
			else if(event.getActionCommand().equals(" validate "))
			{
			}

			/**
			 * Show pdfs Action
			 * **/
			else if(event.getSource() == filterPanel.filterButton)
			{
				Date startDate = null;
				Date endDate = null;

				if(filterPanel.jDateChooser1.isEnabled())
				{
					startDate = filterPanel.jDateChooser1.getDate();
					endDate = filterPanel.jDateChooser2.getDate();
				}
				String plName = filterPanel.comboBoxItems[0].getSelectedItem().toString();
				String supplierName = filterPanel.comboBoxItems[1].getSelectedItem().toString();
				String taskType = filterPanel.comboBoxItems[2].getSelectedItem().toString();
				String extracted = filterPanel.comboBoxItems[3].getSelectedItem().toString();
				String priority = filterPanel.comboBoxItems[4].getSelectedItem().toString();
				tablePanel.selectedData = DataDevQueryUtil.getReviewPDF(new Long[] { userId },
						plName, supplierName, taskType, extracted, startDate, endDate, null,
						"assigned", priority, StatusName.assigned, null, null);

				// filterPanel.jDateChooser1.setDate(new Date(System.currentTimeMillis()));
				// filterPanel.jDateChooser2.setDate(new Date(System.currentTimeMillis()));
				tablePanel.setCurrentPage(1);
				tablePanel.setTableData1(0, tablePanel.selectedData);
				MainWindow.glass.setVisible(false);

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
				filterPanel.filterList = DataDevQueryUtil.getUserData(userDTO, startDate, endDate);
				tablePanel.clearTable();
				filterPanel.refreshFilters();
				MainWindow.glass.setVisible(false);

			}
			/**
			 * Validate Parts Action
			 */

			else if(event.getSource() == validate)
			{
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
						wsMap.get(wsName).validateParts(false);
						if(!ws.canSave)
						{
							input = new ArrayList<ArrayList<String>>();
							input = wsMap.get(wsName).getUnApprovedValues(input);
							if(input.size() > 0)
							{
								int reply = JOptionPane
										.showConfirmDialog(
												null,
												"There are Unapproved Values , Are you want to open Seperation Screen?",
												"Seperation", JOptionPane.YES_NO_OPTION);
								if(reply == JOptionPane.YES_OPTION)
								{

									openseperation();
								}
								else
								{
									MainWindow.glass.setVisible(false);
									return null;
								}
							}
							else
							{
								MainWindow.glass.setVisible(false);
								JOptionPane.showMessageDialog(null,
										"can't save sheet duto some errors in your data");
							}
						}
						else
						{
							if(!wsMap.get(wsName).saved)
							{
								wsMap.get(wsName).saved = true;
								wsMap.get(wsName).saveParts(false);
								MainWindow.glass.setVisible(false);
								JOptionPane.showMessageDialog(null, "Saving Data Finished");
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

				MainWindow.glass.setVisible(false);
				// JOptionPane.showMessageDialog(null, "Saving Data Finished");
			}

			else if(event.getSource() == srcFeedbackBtn)
			{
				System.out.println("Send feedback to sourcing team");
				if("LoadAllData".equals(sheetpanel.getActiveSheetName()))
				{
					// JOptionPane.showConfirmDialog(null, "XYZ");
					ws.sendFeedbackToSourcingTeam(userName);
				}
				else
				{
					JFrame srcFeedbackFrame = new JFrame("Sourcing Feedback");
					String plName = sheetpanel.getActiveSheetName();
					String url = sheetpanel.getCellText(sheetpanel.getSelectedXCell()).getString();
					System.out.println(url);
					SourcingFeedbackPanel panel = new SourcingFeedbackPanel(userName, url, plName);
					srcFeedbackFrame.getContentPane().add(panel);
					srcFeedbackFrame.setBounds(200, 150, 500, 280);
					srcFeedbackFrame.setVisible(true);
					srcFeedbackFrame.setAlwaysOnTop(true);
					srcFeedbackFrame.setResizable(false);
					srcFeedbackFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				}
				MainWindow.glass.setVisible(false);

			}
			MainWindow.glass.setVisible(false);

			return null;
		}

		private void saveseparation()
		{
			ArrayList<String> row;
			if(!validated)
			{
				MainWindow.glass.setVisible(false);
				JOptionPane.showMessageDialog(null,
						" Validate First due to some errors in your data");

				return;
			}

			for(int i = 0; i < separationValues.size(); i++)
			{
				row = separationValues.get(i);

				String plName = row.get(0);
				String featureName = row.get(3);
				String featureFullValue = row.get(4);

				try
				{
					List<ApprovedParametricDTO> approved = ApprovedDevUtil
							.createApprovedValuesList(featureFullValue, plName, featureName,
									row.get(5), row.get(6), row.get(7), row.get(10), row.get(11),
									row.get(9), row.get(8));

					ApprovedDevUtil.saveAppGroupAndSepValue(0, 0, approved, plName, featureName,
							featureFullValue, row.get(2), userId);
				}catch(ArrayIndexOutOfBoundsException ex)
				{
					try
					{
						Cell cell = wsMap.get("Separation").getCellByPosission(12, i + 1);
						cell.setText(ex.getMessage());
					}catch(Exception e)
					{
						e.printStackTrace();
					}
					ex.printStackTrace();
				}catch(Exception ex)
				{
					ex.printStackTrace();
				}
				List<String> appValues = wsMap.get(plName).getApprovedFeatuer().get(featureName);
				appValues.add(featureFullValue);
			}
		}

		private void validateseparation()
		{
			ArrayList<String> row;
			ArrayList<ArrayList<String>> validationResult = new ArrayList<>();
			validated = true;
			// Session session = SessionUtil.getSession();
			for(int i = 0; i < separationValues.size(); i++)
			{
				row = separationValues.get(i);
				List<String> result = ApprovedDevUtil.validateSeparation(row);
				row.set(12, result.get(0));
				validationResult.add(row);
				if(result.get(0) != "" && result.get(1).equals("false"))
				{
					validated = false;
				}
			}
			ws.writeSheetData(validationResult, 1);
		}

		private void openseperation()
		{
			ArrayList<String> row = null;
			input = new ArrayList<ArrayList<String>>();
			tabbedPane.setSelectedIndex(2);
			row = new ArrayList<String>();
			row.add("PL_Name");// 0
			row.add("Part");// 1
			row.add("Datasheet");// 2
			row.add("Feature Name");// 3
			row.add("Feature Value");// 4
			row.add("Feature Unit");// 5
			row.add("Sign");// 6
			row.add("Value");// 7
			row.add("Type");// 8
			row.add("Condition");// 9
			row.add("Multiplier");// 10
			row.add("Unit");// 11
			row.add("Validation result");// 12

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
			MainWindow.glass.setVisible(false);
		}
	}

}
