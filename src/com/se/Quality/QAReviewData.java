package com.se.Quality;

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

import javafx.scene.chart.PieChart.Data;

import javax.swing.ComboBoxModel;
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
import com.se.parametric.dba.DataDevQueryUtil;
import com.se.parametric.dba.ParaQueryUtil;
import com.se.parametric.dev.Developement;
import com.se.parametric.dto.ApprovedParametricDTO;
import com.se.parametric.dto.GrmUserDTO;
import com.se.parametric.dto.TableInfoDTO;
import com.se.parametric.review.TLReviewData;
import com.se.parametric.util.StatusName;

public class QAReviewData extends JPanel implements ActionListener
{

	SheetPanel sheetpanel = new SheetPanel();
//	SheetPanel separationPanel = new SheetPanel();
	JPanel tabSheet, selectionPanel;
	JPanel devSheetButtonPanel, separationButtonPanel;
	JTabbedPane tabbedPane;
	ArrayList<ArrayList<String>> input = new ArrayList<ArrayList<String>>();
	ArrayList<ArrayList<String>> separationValues = new ArrayList<ArrayList<String>>();
	JButton save;
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

		selectionPanel = new JPanel();
		String[] tableHeader = new String[] { "PdfUrl", "PlName", "SupplierName", "TaskType", "Status", "DevUserName", "Date" };
		String[] filterLabels = { "PL Name", "Supplier", "Task Type", "User Name", "Status" };
		tablePanel = new TablePanel(tableHeader, width - 120, (((height - 100) * 7) / 10));
		tablePanel.setBounds(0, (((height - 100) * 3) / 10), width - 120, (((height - 100) * 7) / 10));
		tablePanel.setBorder(new SoftBevelBorder(BevelBorder.RAISED, null, null, null, null));
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
		devSheetButtonPanel = new JPanel();
		devSheetButtonPanel.setBackground(new Color(211, 211, 211));
		devSheetButtonPanel.setBorder(new SoftBevelBorder(BevelBorder.RAISED, null, null, null, null));
		devSheetButtonPanel.setBounds(width - 120, 0, 110, height/3);
		devSheetButtonPanel.setLayout(null);
		save = new JButton("Save");
		save.setBounds(3, 80, 95, 29);
		save.setForeground(new Color(25, 25, 112));
		save.setFont(new Font("Tahoma", Font.BOLD, 11));
		save.addActionListener(this);
		devSheetButtonPanel.add(save);	
		
		tabSheet.setLayout(null);
		sheetpanel.setBounds(0, 0, width - 120, height - 125);
		tabSheet.add(sheetpanel);
		tabSheet.add(devSheetButtonPanel);
		tabSheet.add(alertsPanel1);

		tabbedPane.addTab("Input Selection", null, selectionPanel, null);
		tabbedPane.addTab("Data Sheet", null, tabSheet, null);
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
		String[] statuses=null;
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
				}
				
				if(!userName.equals("All"))
				{
					long userId = ParaQueryUtil.getUserIdByExactName(userName);
					users = new Long[] { userId };
				}
				else
				{
				     ComboBoxModel model = filterPanel.comboBoxItems[3].getModel();
		                int size = model.getSize();
						users=new Long[size-1];
		                for(int i=1;i<size;i++) {
		                    Object element = model.getElementAt(i);
		                    if(element!=null && !element.equals("All") )
		                    users[i-1]=ParaQueryUtil.getUserIdByExactName((String) element);
		                }
				}
				tablePanel.selectedData = DataDevQueryUtil.getReviewPDF(users, plName, supplierName, taskType, null, statuses, startDate, endDate, null, "QAReview", null);
				System.out.println("Selected Data Size=" + tablePanel.selectedData.size());
				tablePanel.setTableData1(0, tablePanel.selectedData);
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		else if(event.getSource() == filterPanel.refreshButton)
		{

			filterPanel.filterList = DataDevQueryUtil.getQAReviewFilterData(userDTO);
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
					if((!"All".equals(status) & (!StatusName.qaReview.equals(status))))
					{
						JOptionPane.showMessageDialog(null, "Invalid PDF Status\nOnly QA Approval pdfs can be loaded");
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
						users = new Long[] { userId };
					}
					else
					{
//						teamMembers = ParaQueryUtil.getTeamMembersIDByTL(userId);
					     ComboBoxModel model = filterPanel.comboBoxItems[3].getModel();
			                int size = model.getSize();
							users=new Long[size-1];
			                for(int i=1;i<size;i++) {
			                    Object element = model.getElementAt(i);
			                    if(element!=null && !element.equals("All") )
			                    users[i-1]=ParaQueryUtil.getUserIdByExactName((String) element);
//			                    System.out.println("Element at " + i + " = " + element);
			                }
					}
					Map<String, ArrayList<ArrayList<String>>> reviewData = DataDevQueryUtil.getQAPDFData(users, plName, supplierName, taskType, status, startDate, endDate, new Long[] { document.getId() },userDTO.getId(),StatusName.qaReview);
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
						ws.setReviewHeader(null,true);
						ArrayList<ArrayList<String>> plData = reviewData.get(pl);
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
				ok = ParaQueryUtil.getDialogMessage("another PDF is opening are you need to replace this", "Confermation Dailog");

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
				if((!"All".equals(status) & (!StatusName.qaReview.equals(status))))
				{
					JOptionPane.showMessageDialog(null, "Invalid PDF Status\nOnly QA Approval pdfs can be loaded");
					thread.stop();
					loading.frame.dispose();
					return;
				}

				if(!userName.equals("All"))
				{
					long userId = ParaQueryUtil.getUserIdByExactName(userName);
					users = new Long[] { userId };
				}
				else
				{ComboBoxModel model = filterPanel.comboBoxItems[3].getModel();
                int size = model.getSize();
				 users=new Long[size-1];
                for(int i=1;i<size;i++) {
                    Object element = model.getElementAt(i);
                    if(element!=null && !element.equals("All") )
                    users[i-1]=ParaQueryUtil.getUserIdByExactName((String) element);
                }
				}
				if("All".equals(status))
				{
					status = StatusName.qaReview;
				}
				Map<String, ArrayList<ArrayList<String>>> reviewData = DataDevQueryUtil.getQAPDFData(users, plName, supplierName, taskType, status, startDate, endDate, null,userDTO.getId(),StatusName.qaReview);
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
					if(DataDevQueryUtil.isNPITaskType(users, pl, supplierName, taskType, status, startDate, endDate, null))
						ws.setNPIflag(true);
					ws.setReviewHeader(null,true);

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
				wsMap.get(wsName).saveQAReviewAction(QAName,"Rev");
			}
		}
	}
		
		thread.stop();
		loading.frame.dispose();
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
		uDTO.setFullName("Shady");
		// uDTO.setId(121);
		// uDTO.setFullName("Ahmad_rahim");
		GrmRole role = new GrmRole();
		role.setId(1l);
		GrmGroup group = new GrmGroup();
		group.setId(101l);
		uDTO.setGrmRole(role);
		uDTO.setGrmGroup(group);
		QAReviewData devPanel = new QAReviewData(uDTO);
		frame.getContentPane().add(devPanel);
		frame.show();
//		while(true)
//		{
//			ArrayList<String> flags = ParaQueryUtil.getAlerts(uDTO.getId(), 101, 3);
//			devPanel.updateFlags(flags);
//
//			try
//			{
//				Thread.sleep(5000);
//			}catch(InterruptedException e)
//			{
//				e.printStackTrace();
//			}
//		}
	}

	public void updateFlags(ArrayList<String> flags)
	{
		alertsPanel.updateFlags(flags);
		alertsPanel1.updateFlags(flags);
		alertsPanel2.updateFlags(flags);
		
	}
}