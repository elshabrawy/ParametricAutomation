package com.se.parametric.dev;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
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

import osheet.Cell;
import osheet.SheetPanel;
import osheet.WorkingSheet;

import com.se.automation.db.client.mapping.Pl;
import com.se.automation.db.parametric.StatusName;
import com.se.parametric.MainWindow;
import com.se.parametric.commonPanel.FilterPanel;
import com.se.parametric.commonPanel.TablePanel;
import com.se.parametric.commonPanel.WorkingAreaPanel;
import com.se.parametric.dba.ApprovedDevUtil;
import com.se.parametric.dba.DataDevQueryUtil;
import com.se.parametric.dba.ParaQueryUtil;
import com.se.parametric.dto.ApprovedParametricDTO;
import com.se.parametric.dto.DocumentInfoDTO;
import com.se.parametric.dto.GrmUserDTO;
import com.se.parametric.dto.TableInfoDTO;
import com.se.parametric.fb.SourcingFeedbackPanel;

public class Update extends JPanel implements ActionListener
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
	WorkingAreaPanel tabSheet, separationTab, selectionPanel;
	// JPanel devSheetButtonPanel, separationButtonPanel;
	// Update mainInfo = new Update();
	JTabbedPane tabbedPane;
	// JButton button = null;// new JButton("LoadSheet");
	// JButton showAll = new JButton("Show All");
	// JButton save, validate, separation, separationSave, srcFeedbackBtn;
	Map<String, WorkingSheet> wsMap = new HashMap<String, WorkingSheet>();
	ArrayList<ArrayList<String>> separationValues = new ArrayList<ArrayList<String>>();
	boolean foundPdf = false;
	long userId = 0;
	TablePanel tablePanel = null;
	FilterPanel filterPanel = null;
	// ButtonsPanel buttonsPanel;
	String userName;
	GrmUserDTO userDTO = null;

	/**
	 * Create the panel.
	 */

	public Update(GrmUserDTO userDTO)
	{
		this.setLayout(new BorderLayout());
		this.userDTO = userDTO;
		this.userName = userDTO.getFullName();
		int width = Toolkit.getDefaultToolkit().getScreenSize().width;
		int height = Toolkit.getDefaultToolkit().getScreenSize().height;
		// =======================================================
		userId = userDTO.getId();

		String[] labels = new String[] { "PdfUrl", "PlName", "SupplierName", "TaskType",
				"Extracted", "Priority", "AssginedDate" };
		String[] filterHeader = { "PL Name", "Supplier Name", "Task Type", "Extracted", "Priority" };
		ArrayList<Object[]> filterData = DataDevQueryUtil.getUserNPIData(userDTO, null, null);
		selectionPanel = new WorkingAreaPanel(this.userDTO);
		tablePanel = selectionPanel.getTablePanel(labels);
		filterPanel = selectionPanel.getFilterPanel(filterHeader, filterData, false, this);

		ArrayList<String> buttonLabels = new ArrayList<String>();
		buttonLabels.add("LoadSheet");
		buttonLabels.add("Load All");
		buttonLabels.add("Show All");
		selectionPanel.addButtonsPanel(buttonLabels, this);

		tabSheet = new WorkingAreaPanel(this.userDTO);
		buttonLabels = new ArrayList<String>();
		buttonLabels.add("Save");
		buttonLabels.add("Validate");
		buttonLabels.add("Separation");
		buttonLabels.add("SRC Feedback");
		tabSheet.addButtonsPanel(buttonLabels, this);
		sheetpanel = tabSheet.getSheet();

		separationTab = new WorkingAreaPanel(this.userDTO);
		buttonLabels = new ArrayList<String>();
		buttonLabels.add(" Save ");
		separationTab.addButtonsPanel(buttonLabels, this);
		separationPanel = separationTab.getSheet();

		selectionPanel.addComponentsToPanel();
		tabSheet.addComponentsToPanel();
		separationTab.addComponentsToPanel();

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.addTab("Input Selection", null, selectionPanel, null);
		tabbedPane.addTab("Sheet", null, tabSheet, null);
		tabbedPane.addTab("Separation", null, separationTab, null);
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

	public static void main(String[] args)
	{
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		int width = Toolkit.getDefaultToolkit().getScreenSize().width;
		int height = Toolkit.getDefaultToolkit().getScreenSize().height;
		frame.setSize(width, height);
		frame.setTitle("Developement");
		GrmUserDTO uDTO = new GrmUserDTO();
		// uDTO.setId(147);
		// uDTO.setFullName("sameh_soliman");
		uDTO.setId(117);
		uDTO.setFullName("Abeer Mohamady");
		// uDTO.setId(116);
		// uDTO.setFullName("a_kamal");
		// uDTO.setId(121);
		// uDTO.setFullName("Ahmed Abdel-Rahim");
		uDTO.setId(376);
		uDTO.setFullName("Salah Shiha");
		Developement devPanel = new Developement(uDTO);
		frame.getContentPane().add(devPanel);
		frame.show();
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
			String pdfUrl = "";
			ArrayList<DocumentInfoDTO> docsInfo = null;
			ArrayList<String> row = null;
			boolean isExclamationMark = false;

			/**
			 * Load Data development Sheet
			 */
			if(event.getActionCommand().equals("LoadSheet"))
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
					for(int i = 0; i < docsInfo.size(); i++)
					{
						List<String> newsData = null;
						long pdfId = -1;
						DocumentInfoDTO docInfo = docsInfo.get(i);
						String suppName = docInfo.getSupplierName();
						String plName = docInfo.getPlName();
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
				}
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
						if(row.get(7).contains("!"))
						{
							isExclamationMark = true;
						}
						String plName = row.get(0);
						String featureName = row.get(3);
						String featureFullValue = row.get(4);
						try
						{
							List<ApprovedParametricDTO> approved = ApprovedDevUtil
									.createApprovedValuesList(featureFullValue, plName,
											featureName, row.get(5), row.get(6), row.get(7),
											row.get(10), row.get(11), row.get(9), row.get(8));

							ApprovedDevUtil.saveAppGroupAndSepValue(0, 0, approved, plName,
									featureName, featureFullValue, row.get(2), userId);
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
						isExclamationMark = false;
						List<String> appValues = wsMap.get(plName).getApprovedFeatuer()
								.get(featureName);
						appValues.add(featureFullValue);
					}
					MainWindow.glass.setVisible(false);
					JOptionPane.showMessageDialog(null, "Approved Saving Done");
				}

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
				tablePanel.selectedData = DataDevQueryUtil.getReviewPDFupdate(
						new Long[] { userId }, plName, supplierName, taskType, extracted,
						startDate, endDate, null, "assigned", priority, StatusName.assigned, null);

				// filterPanel.jDateChooser1.setDate(new Date(System.currentTimeMillis()));
				// filterPanel.jDateChooser2.setDate(new Date(System.currentTimeMillis()));
				tablePanel.setCurrentPage(1);
				tablePanel.setTableData1(0, tablePanel.selectedData);

			}
			else if(event.getSource() == filterPanel.refreshButton)
			{

				filterPanel.filterList = DataDevQueryUtil.getUserData(userDTO,
						filterPanel.startDate, filterPanel.endDate);
				tablePanel.clearTable();
				filterPanel.refreshFilters();

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
						wsMap.get(wsName).validateParts(true);
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
						wsMap.get(wsName).saveParts(true);
						// wsMap.get(wsName).readSpreadsheet();
						// wsMap.get(wsName).updateApprovedValues();
					}
				}

			}
			else if(event.getActionCommand().equals("SRC Feedback"))
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

			}

			MainWindow.glass.setVisible(false);
			return null;
		}
	}
}
