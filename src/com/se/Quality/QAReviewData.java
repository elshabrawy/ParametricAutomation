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
import java.util.Map;

import javax.swing.ComboBoxModel;
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
import com.se.automation.db.parametric.StatusName;
import com.se.grm.client.mapping.GrmGroup;
import com.se.grm.client.mapping.GrmRole;
import com.se.parametric.Loading;
import com.se.parametric.commonPanel.AlertsPanel;
import com.se.parametric.commonPanel.ButtonsPanel;
import com.se.parametric.commonPanel.FilterPanel;
import com.se.parametric.commonPanel.TablePanel;
import com.se.parametric.dba.DataDevQueryUtil;
import com.se.parametric.dba.ParaQueryUtil;
import com.se.parametric.dto.GrmUserDTO;
import com.se.parametric.dto.TableInfoDTO;
import com.se.parametric.util.ImagePanel;

public class QAReviewData extends JPanel implements ActionListener
{

	SheetPanel sheetpanel = new SheetPanel();
	SheetPanel SummaryPanel = new SheetPanel();
	JPanel tabSheet, selectionPanel, Summarytab, flowChart;
	JPanel devSheetButtonPanel, summaryButtonPanel;
	JTabbedPane tabbedPane;
	ArrayList<ArrayList<String>> input = new ArrayList<ArrayList<String>>();
	ArrayList<ArrayList<String>> separationValues = new ArrayList<ArrayList<String>>();
	JButton save;
	JButton validate;
	JButton summarysave;
	JButton summaryvalidate;
	TablePanel tablePanel = null;
	FilterPanel filterPanel = null;
	ButtonsPanel buttonsPanel;
	Long[] users = null;
	WorkingSheet ws = null;
	Map<String, WorkingSheet> wsMap = new HashMap<String, WorkingSheet>();
	String QAName = "";
	long userId;
	int width, height;
	GrmUserDTO userDTO;
	boolean summarydata = false;
	static AlertsPanel alertsPanel, alertsPanel1, alertsPanel2;

	public QAReviewData(GrmUserDTO userDTO)
	{
		setLayout(null);
		this.userDTO = userDTO;
		QAName = userDTO.getFullName();
		userId = userDTO.getId();
		width = Toolkit.getDefaultToolkit().getScreenSize().width;
		height = Toolkit.getDefaultToolkit().getScreenSize().height;
		ArrayList<Object[]> filterData = DataDevQueryUtil.getQAReviewFilterData(userDTO);
		System.out.println("User:" + userDTO.getId() + " " + userDTO.getFullName() + " " + filterData.size());
		// PDFURL PL Name Supplier Name No. of Parts per PDF No. of Done Parts per PDF No. of parts per PL No. of Done parts per PL PL_Type
		// Development Method QA tools checks Task Type DevUserName Date

		selectionPanel = new JPanel();
		String[] tableHeader = new String[] { "PdfUrl", "PlName", "PlType", "SupplierName", "PDFParts", "Taskparts", "PDFDoneParts", "PLParts", "PLDoneParts", "PLFeatures", "TaskType", "Status", "DevUserName", "Date" };
		String[] filterLabels = { "PL Name", "PL Type", "Supplier", "Task Type", "User Name", "PDF Status" };
		tablePanel = new TablePanel(tableHeader, width - 120, (((height - 100) * 7) / 10));
		tablePanel.setBounds(0, (((height - 100) * 3) / 10), width - 120, (((height - 100) * 7) / 10));
		tablePanel.setBorder(new SoftBevelBorder(BevelBorder.RAISED, null, null, null, null));
		filterPanel = new FilterPanel(filterLabels, filterData, width - 120, (((height - 100) * 3) / 10), true);
		filterPanel.setBounds(0, 0, width - 120, (((height - 100) * 3) / 10));
		ArrayList<String> buttonLabels = new ArrayList<String>();
		buttonLabels.add("Load PDF");
		buttonLabels.add("Summary");
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
		Summarytab = new JPanel();
		devSheetButtonPanel = new JPanel();
		devSheetButtonPanel.setBackground(new Color(211, 211, 211));
		devSheetButtonPanel.setBorder(new SoftBevelBorder(BevelBorder.RAISED, null, null, null, null));
		devSheetButtonPanel.setBounds(width - 120, 0, 110, height / 3);
		devSheetButtonPanel.setLayout(null);

		validate = new JButton("Validate");
		validate.setBounds(3, 40, 95, 29);
		validate.setForeground(new Color(25, 25, 112));
		validate.setFont(new Font("Tahoma", Font.BOLD, 11));
		validate.addActionListener(this);
		devSheetButtonPanel.add(validate);

		save = new JButton("Save");
		save.setBounds(3, 80, 95, 29);
		save.setForeground(new Color(25, 25, 112));
		save.setFont(new Font("Tahoma", Font.BOLD, 11));
		save.addActionListener(this);
		devSheetButtonPanel.add(save);

		summaryButtonPanel = new JPanel();
		summaryButtonPanel.setBackground(new Color(211, 211, 211));
		summaryButtonPanel.setBorder(new SoftBevelBorder(BevelBorder.RAISED, null, null, null, null));
		summaryButtonPanel.setBounds(width - 120, 0, 110, height / 3);
		summaryButtonPanel.setLayout(null);

		summarysave = new JButton("Save");
		summarysave.setBounds(3, 80, 95, 29);
		summarysave.setForeground(new Color(25, 25, 112));
		summarysave.setFont(new Font("Tahoma", Font.BOLD, 11));
		summarysave.addActionListener(this);
		summaryButtonPanel.add(summarysave);

		summaryvalidate = new JButton("Validate");
		summaryvalidate.setBounds(3, 40, 95, 29);
		summaryvalidate.setForeground(new Color(25, 25, 112));
		summaryvalidate.setFont(new Font("Tahoma", Font.BOLD, 11));
		summaryvalidate.addActionListener(this);
		summaryButtonPanel.add(summaryvalidate);

		Summarytab.setLayout(null);
		SummaryPanel.setBounds(0, 0, width - 120, height - 125);
		Summarytab.add(SummaryPanel);
		Summarytab.add(summaryButtonPanel);
		Summarytab.add(alertsPanel2);
		// filterPanel.comboBoxItems[1].setSelectedIndex(1);
		tabSheet.setLayout(null);
		sheetpanel.setBounds(0, 0, width - 120, height - 125);
		tabSheet.add(sheetpanel);
		tabSheet.add(devSheetButtonPanel);
		tabSheet.add(alertsPanel1);

		flowChart = new ImagePanel("Development-chart.jpg");
		tabbedPane.addTab("Input Selection", null, selectionPanel, null);
		tabbedPane.addTab("Data Sheet", null, tabSheet, null);
		tabbedPane.addTab("Summary Sheet", null, Summarytab, null);
		tabbedPane.addTab("Development Flow", null, flowChart, null);

		add(tabbedPane);

		filterPanel.filterButton.addActionListener(this);
		filterPanel.refreshButton.addActionListener(this);
		filterPanel.addsummary.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent event)
	{
		LongRunProcess longRunProcess = new LongRunProcess(event);
		longRunProcess.execute();
	}

	private void addtosummary()
	{
		try
		{
			ArrayList<TableInfoDTO> data = tablePanel.selectedData;
			for(int i = 0; i < data.size(); i++)
			{
				String pdfUrl = data.get(i).getPdfUrl();
				Document doc = ParaQueryUtil.getDocumnetByPdfUrl(pdfUrl);
				if(doc != null)
					DataDevQueryUtil.addpdfstosummary(doc);
			}

		}catch(Exception e)
		{
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "error in data");
		}
		JOptionPane.showMessageDialog(null, "Sucessfully added");

	}

	private void loadsummary()
	{
		Date startDate = null;
		Date endDate = null;

		if(filterPanel.jDateChooser1.isEnabled())
		{
			startDate = filterPanel.jDateChooser1.getDate();
			endDate = filterPanel.jDateChooser2.getDate();
		}

		wsMap.clear();
		ArrayList<ArrayList<String>> data = DataDevQueryUtil.getsummarydata(startDate, endDate, userDTO);
		tabbedPane.setSelectedIndex(2);
		SummaryPanel.openOfficeDoc();

		ws = new WorkingSheet(SummaryPanel, "Summary");
		SummaryPanel.saveDoc("C:/Report/Parametric_Auto/" + "Summary" + "@" + userDTO.getFullName() + "@" + System.currentTimeMillis() + ".xls");
		wsMap.put("Summary", ws);
		ws.setSummaryHeader(Arrays.asList("Validation Comment"));

		ArrayList<String> sheetHeader = ws.getHeader();
		int statusIndex = sheetHeader.indexOf("Final QA Flag");
		// for(int i = 0; i < data.size(); i++)
		// {
		// ArrayList<String> datarow = data.get(i);
		// datarow.add("");
		// String keyword = "";
		// datarow.set(datarow.size() - 1, "");
		// }
		ws.writeReviewData(data, 2, statusIndex + 1);

	}

	private void dofilter()
	{
		String plName = filterPanel.comboBoxItems[0].getSelectedItem().toString();
		String plType = filterPanel.comboBoxItems[1].getSelectedItem().toString();
		String supplierName = filterPanel.comboBoxItems[2].getSelectedItem().toString();
		String taskType = filterPanel.comboBoxItems[3].getSelectedItem().toString();
		if(taskType.equals("All"))
		{
			JOptionPane.showMessageDialog(null, "Please Select Task Type");
		}
		else
		{
			String userName = filterPanel.comboBoxItems[4].getSelectedItem().toString();
			String status = filterPanel.comboBoxItems[5].getSelectedItem().toString();
			if(status.equals("All"))
			{
				status = StatusName.qaReview;
				summarydata = false;
			}
			else if(status.equals(StatusName.waitingsummary))
			{
				summarydata = true;
			}

			Date startDate = null;
			Date endDate = null;
			try
			{
				if(filterPanel.jDateChooser1.isEnabled())
				{
					startDate = filterPanel.jDateChooser1.getDate();
					endDate = filterPanel.jDateChooser2.getDate();
				}
				if(!userName.equals("All"))
				{
					long userId = ParaQueryUtil.getUserIdByExactName(userName);
					users = new Long[] { userId };
				}
				else
				{
					ComboBoxModel model = filterPanel.comboBoxItems[4].getModel();
					int size = model.getSize();
					users = new Long[size - 1];
					for(int i = 1; i < size; i++)
					{
						Object element = model.getElementAt(i);
						if(element != null && !element.equals("All"))
							users[i - 1] = ParaQueryUtil.getUserIdByExactName((String) element);
					}
				}
				tablePanel.selectedData = DataDevQueryUtil.getReviewPDF(users, plName, supplierName, taskType, null, startDate, endDate, null, "QAReview", null, status, plType, userId);
				System.out.println("Selected Data Size=" + tablePanel.selectedData.size());
				tablePanel.setTableData1(0, tablePanel.selectedData);
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}

	}

	private void loadpdf()
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
				JComboBox[] combos = filterPanel.comboBoxItems;
				String plName = combos[0].getSelectedItem().toString();
				String supplierName = combos[2].getSelectedItem().toString();
				String taskType = combos[3].getSelectedItem().toString();
				String userName = combos[4].getSelectedItem().toString();
				String pltype = combos[1].getSelectedItem().toString();
				String status = "";
				if(!summarydata)
				{
					status = StatusName.qaReview;
				}
				else
				{
					status = StatusName.waitingsummary;
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
					users = new Long[] { userId };
				}
				else
				{
					// teamMembers = ParaQueryUtil.getTeamMembersIDByTL(userId);
					ComboBoxModel model = filterPanel.comboBoxItems[4].getModel();
					int size = model.getSize();
					users = new Long[size - 1];
					for(int i = 1; i < size; i++)
					{
						Object element = model.getElementAt(i);
						if(element != null && !element.equals("All"))
							users[i - 1] = ParaQueryUtil.getUserIdByExactName((String) element);
						// System.out.println("Element at " + i + " = " + element);
					}
				}
				Map<String, ArrayList<ArrayList<String>>> reviewData = DataDevQueryUtil.getQAPDFData(users, plName, supplierName, taskType, startDate, endDate, new Long[] { document.getId() }, userDTO.getId(), status, pltype);
				int k = 0;
				tabbedPane.setSelectedIndex(1);
				sheetpanel.openOfficeDoc();

				for(String pl : reviewData.keySet())
				{
					// wsMap=
					ws = new WorkingSheet(sheetpanel, pl, k);
					sheetpanel.saveDoc("C:/Report/" + pdfUrl.replaceAll(".*/", "") + "@" + System.currentTimeMillis() + ".xls");
					wsMap.put(pl, ws);
					if(docInfoDTO.getTaskType().contains("NPI"))
						ws.setNPIflag(true);
					ArrayList<String> sheetHeader = null;
					int statusIndex = 0;
					ArrayList<ArrayList<String>> plData = reviewData.get(pl);
					if(summarydata)
					{
						ws.setQAReviewHeader(Arrays.asList("Old Flag", "Status", "Wrong Feature", "Comment", "Validation Comment"), true);
						sheetHeader = ws.getHeader();
						statusIndex = sheetHeader.indexOf("Status");
						int oldflagindex = sheetHeader.indexOf("Old Flag");
						int partIndex = sheetHeader.indexOf("Part Number");
						int CommentIndex = sheetHeader.indexOf("Comment");
						int WrongFeatureIndex = sheetHeader.indexOf("Wrong Feature");
						int ComidIndex = sheetHeader.indexOf("Comid");

						for(int j = 0; j < plData.size(); j++)
						{
							ArrayList<String> sheetRecord = plData.get(j);
							for(int l = 0; l < 5; l++)
							{
								sheetRecord.add("");
							}
							String qaflag = DataDevQueryUtil.getqaflagbycomid(sheetRecord.get(ComidIndex));
							sheetRecord.set(oldflagindex, qaflag);
							String comment = DataDevQueryUtil.getfbcommentbycompartanduser(sheetRecord.get(partIndex).toString(), userDTO.getId());
							sheetRecord.set(CommentIndex, comment);
							String wrongfeatures = DataDevQueryUtil.getfbwrongfets(Long.valueOf(sheetRecord.get(ComidIndex)), userDTO.getId());
							sheetRecord.set(WrongFeatureIndex, wrongfeatures);
						}
					}
					else if(!summarydata)
					{
						ws.setQAReviewHeader(Arrays.asList("Status", "Wrong Feature", "Comment", "Validation Comment"), true);
						sheetHeader = ws.getHeader();
						statusIndex = sheetHeader.indexOf("Status");
						int CommentIndex = sheetHeader.indexOf("Comment");
						int WrongFeatureIndex = sheetHeader.indexOf("Wrong Feature");
						for(int j = 0; j < plData.size(); j++)
						{
							ArrayList<String> sheetRecord = plData.get(j);
							for(int l = 0; l < 4; l++)
							{
								sheetRecord.add("");
							}
							sheetRecord.set(statusIndex, "");
							sheetRecord.set(CommentIndex, "");
							sheetRecord.set(WrongFeatureIndex, "");
						}
					}

					ws.writeReviewData(plData, 2, statusIndex + 1);

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
		int height = Toolkit.getDefaultToolkit().getScreenSize().height;
		frame.setSize(width, height);
		frame.setTitle("QA Review");
		GrmUserDTO uDTO = new GrmUserDTO();
		// uDTO.setId(47);
		// uDTO.setFullName("mohamad mostafa");
		// uDTO.setId(32);
		// uDTO.setFullName("Hatem Hussien");
		uDTO.setId(80);
		uDTO.setFullName("mahmoud_hamdy");
		// uDTO.setId(121);
		// uDTO.setFullName("Ahmad_rahim");
		GrmRole role = new GrmRole();
		role.setId(3l);
		GrmGroup group = new GrmGroup();
		group.setId(23l);
		uDTO.setGrmRole(role);
		uDTO.setGrmGroup(group);
		QAReviewData devPanel = new QAReviewData(uDTO);
		frame.getContentPane().add(devPanel);
		frame.show();
		// while(true)
		// {
		// ArrayList<String> flags = ParaQueryUtil.getAlerts(uDTO.getId(), 101, 3);
		// devPanel.updateFlags(flags);
		//
		// try
		// {
		// Thread.sleep(5000);
		// }catch(InterruptedException e)
		// {
		// e.printStackTrace();
		// }
		// }
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
			// String[] statuses=null;
			boolean isExclamationMark = false;
			/**
			 * Show pdfs Action
			 * **/
			if(event.getSource() == filterPanel.filterButton)
			{
				dofilter();
			}
			else if(event.getSource() == filterPanel.refreshButton)
			{
				filterPanel.filterList = DataDevQueryUtil.getQAReviewFilterData(userDTO);
				filterPanel.refreshFilters();
			}
			else if(event.getSource() == filterPanel.addsummary)
			{
				addtosummary();
			}

			// Load pdf
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

			// Load summary
			else if(event.getActionCommand().equals("Summary"))
			{
				boolean ok = false;
				if(SummaryPanel.isOpened())
					ok = ParaQueryUtil.getDialogMessage("another Summary is opend are you need to replace this", "Confermation Dailog");

				if(SummaryPanel.isOpened() && ok == false)
				{
					Loading.close();
					return null;
				}
				loadsummary();
			}

			// validate sample
			else if(event.getSource() == validate)
			{
				System.out.println("~~~~~~~ Start Validation Data ~~~~~~~");
				wsMap.keySet();
				for(String wsName : wsMap.keySet())
				{
					if(wsName != "LoadAllData" && wsName != "Separation" && wsName != "Summary")
					{
						WorkingSheet ws = wsMap.get(wsName);
						ws.validateQAReview();
						JOptionPane.showMessageDialog(null, "Validation Done");
					}
				}
			}
			// save sample
			else if(event.getSource() == save)
			{
				System.out.println("~~~~~~~ Start saving Data ~~~~~~~");
				wsMap.keySet();
				for(String wsName : wsMap.keySet())
				{
					if(wsName != "LoadAllData" && wsName != "Separation" && wsName != "Summary")
					{
						wsMap.get(wsName).saveQAReviewAction(QAName, "Rev", summarydata);
					}
				}
			}
			// validate summary
			else if(event.getSource() == summaryvalidate)
			{
				System.out.println("~~~~~~~ Start validate Data ~~~~~~~");
				wsMap.keySet();
				for(String wsName : wsMap.keySet())
				{
					if(wsName == "Summary")
					{
						WorkingSheet ws = wsMap.get(wsName);
						ws.validateQASummary();
						JOptionPane.showMessageDialog(null, "Validation Done");
					}
				}
			}
			// save summary
			else if(event.getSource() == summarysave)
			{
				System.out.println("~~~~~~~ Start saving summary ~~~~~~~");
				wsMap.keySet();
				for(String wsName : wsMap.keySet())
				{
					if(wsName == "Summary")
					{
						WorkingSheet ws = wsMap.get(wsName);
						ws.saveQASummary(QAName);

					}
				}
			}

			Loading.close();
			return null;
		}
	}

}
