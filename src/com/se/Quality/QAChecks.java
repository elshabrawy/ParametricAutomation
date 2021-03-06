package com.se.Quality;

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

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingWorker;

import org.hibernate.Session;

import osheet.Cell;
import osheet.SheetPanel;
import osheet.WorkingSheet;

import com.se.automation.db.SessionUtil;
import com.se.automation.db.client.dto.QAChecksDTO;
import com.se.automation.db.parametric.StatusName;
import com.se.grm.client.mapping.GrmGroup;
import com.se.grm.client.mapping.GrmRole;
import com.se.parametric.MainWindow;
import com.se.parametric.commonPanel.FilterPanel;
import com.se.parametric.commonPanel.WorkingAreaPanel;
import com.se.parametric.dba.ApprovedDevUtil;
import com.se.parametric.dba.DataDevQueryUtil;
import com.se.parametric.dba.ParaQueryUtil;
import com.se.parametric.dto.ApprovedParametricDTO;
import com.se.parametric.dto.GrmUserDTO;

//ahmed_eldalatony@gitblit:8000/r/Automation/Parametric/ParametricAutomationDTV2.git

public class QAChecks extends JPanel implements ActionListener
{

	SheetPanel sheetpanel = new SheetPanel();
	SheetPanel separationPanel = new SheetPanel();
	WorkingAreaPanel tabSheet, selectionPanel;
	JTabbedPane tabbedPane;
	ArrayList<ArrayList<String>> input = new ArrayList<ArrayList<String>>();
	ArrayList<ArrayList<String>> separationValues = new ArrayList<ArrayList<String>>();
	FilterPanel filterPanel = null;
	Long[] users = null;
	WorkingSheet ws = null;
	Map<String, WorkingSheet> wsMap = new HashMap<String, WorkingSheet>();
	String engName = "";
	long userId;
	int width, height;
	GrmUserDTO userDTO;
	String checker;
	String filterstatus;
	public static ArrayList<ArrayList<String>> seperationvalues = new ArrayList<>();
	boolean validated;

	public QAChecks(GrmUserDTO userDTO)
	{
		this.setLayout(new BorderLayout());
		this.userDTO = userDTO;
		engName = userDTO.getFullName();
		userId = userDTO.getId();
		width = Toolkit.getDefaultToolkit().getScreenSize().width;
		height = Toolkit.getDefaultToolkit().getScreenSize().height;
		ArrayList<Object[]> filterData = DataDevQueryUtil.getQAchecksFilterData(userDTO);
		System.out.println("User:" + userDTO.getId() + " " + userDTO.getFullName() + " "
				+ filterData.size());
		selectionPanel = new WorkingAreaPanel(this.userDTO);
		String[] filterLabels = { "PL Name", "Supplier", "Checker Type", "Status" };
		filterPanel = selectionPanel.getFilterPanel(filterLabels, filterData, false, this);
		ArrayList<String> buttonLabels = new ArrayList<String>();
		buttonLabels.add("Save");
		buttonLabels.add("Seperation");
		selectionPanel.addButtonsPanel(buttonLabels, this);
		sheetpanel = selectionPanel.getSheet();

		tabSheet = new WorkingAreaPanel(this.userDTO);
		buttonLabels = new ArrayList<String>();
		buttonLabels.add(" validate ");
		buttonLabels.add(" save ");
		tabSheet.addButtonsPanel(buttonLabels, this);
		separationPanel = tabSheet.getSheet();

		selectionPanel.addComponentsToPanel();
		tabSheet.addComponentsToPanel();

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.addTab("Input Selection", null, selectionPanel, null);
		tabbedPane.addTab("Seperation", null, tabSheet, null);

		
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
		this.add(tabbedPane);
	}

	@Override
	public void actionPerformed(ActionEvent event)
	{
		LongRunProcess longRunProcess = new LongRunProcess(event);
		longRunProcess.execute();
	}

	private void dofilter(Date startDate, Date endDate) throws Exception
	{
		Session session = SessionUtil.getSession();
		try
		{

			if(filterPanel.jDateChooser1.isEnabled())
			{
				startDate = filterPanel.jDateChooser1.getDate();
				endDate = filterPanel.jDateChooser2.getDate();
			}

			String plName = filterPanel.comboBoxItems[0].getSelectedItem().toString();
			String supplierName = filterPanel.comboBoxItems[1].getSelectedItem().toString();
			String checkerType = filterPanel.comboBoxItems[2].getSelectedItem().toString();
			String status = filterPanel.comboBoxItems[3].getSelectedItem().toString();
			if(checkerType.equals("All"))
			{
				JOptionPane.showMessageDialog(null, "You must select checker type");
				return;
			}
			if(status.equals("All"))
			{
				JOptionPane.showMessageDialog(null, "You must select Status");
				return;
			}
			checker = checkerType;
			filterstatus = status;
			tabbedPane.setSelectedIndex(0);
			sheetpanel.openOfficeDoc();
			ArrayList<QAChecksDTO> reviewData = DataDevQueryUtil
					.getQAchecksData(plName, supplierName, checkerType, status, startDate, endDate,
							userDTO.getId(), session);
			wsMap.clear();
			ws = new WorkingSheet(sheetpanel, "QAChecks");
			sheetpanel.saveDoc("C:/Report/" + "QAChecks by " + userDTO.getFullName() + "@"
					+ System.currentTimeMillis() + ".xls");
			wsMap.put("QAChecks", ws);
			ws.setqaChecksheader(checkerType);
			ArrayList<String> sheetHeader = ws.getHeader();
			int statusindx = sheetHeader.indexOf("Status");
			// int flag = sheetHeader.indexOf("Flag");
			ArrayList<ArrayList<String>> data = new ArrayList<>();
			for(int i = 0; i < reviewData.size(); i++)
			{
				boolean exist = DataDevQueryUtil.chkpartflagqachks(reviewData.get(i).getPart(),
						reviewData.get(i).getCheckpartid(), session);
				String flag = "AffectedPart";
				if(exist)
				{
					flag = "InputPart";
				}
				System.out.println("no>>" + i);
				ArrayList<String> row = new ArrayList<>();
				row.add(reviewData.get(i).getCheckpartid().toString());
				row.add(reviewData.get(i).getPart().getComId().toString());
				row.add(reviewData.get(i).getNanAlphaPart());
				row.add(flag);
				row.add(reviewData.get(i).getPart().getPartNumber());
				row.add(reviewData.get(i).getVendor().getName());
				row.add(reviewData.get(i).getDatasheet().getPdf().getSeUrl());
				row.add(reviewData.get(i).getDatasheetTitle());
				row.add(reviewData.get(i).getProductLine() == null ? "" : reviewData.get(i)
						.getProductLine().getName());
				row.add(reviewData.get(i).getMask() == null ? "" : reviewData.get(i).getMask()
						.getMstrPart());
				row.add(reviewData.get(i).getFamily() == null ? "" : reviewData.get(i).getFamily()
						.getName());
				if(reviewData.get(i).getChecker().equals(StatusName.MaskMultiData)
						|| reviewData.get(i).getChecker().equals(StatusName.RootPartChecker))
				{
					row.add(reviewData.get(i).getFeatureName() == null ? "" : reviewData.get(i)
							.getFeatureName());
					row.add(reviewData.get(i).getFeatureValue() == null ? "" : reviewData.get(i)
							.getFeatureValue());
				}
				else if(reviewData.get(i).getChecker().equals(StatusName.generic_part))
				{
					row.add(reviewData.get(i).getGeneric() == null ? "" : reviewData.get(i)
							.getGeneric());
					row.add(reviewData.get(i).getFeatureName() == null ? "" : reviewData.get(i)
							.getFeatureName());
					row.add(reviewData.get(i).getFeatureValue() == null ? "" : reviewData.get(i)
							.getFeatureValue());
				}
				row.add("");
				row.add("");
				data.add(row);

			}
			ws.writeReviewData(data, 1, statusindx + 1);
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			session.close();
		}
	}

	public static void main(String[] args)
	{
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		int width = Toolkit.getDefaultToolkit().getScreenSize().width;
		int height = Toolkit.getDefaultToolkit().getScreenSize().height;
		frame.setSize(width, height);
		frame.setTitle("QA Checks");
		GrmUserDTO uDTO = new GrmUserDTO();
		// uDTO.setId(47);
		// uDTO.setFullName("mohamad mostafa");
		// uDTO.setId(32);
		// uDTO.setFullName("Hatem Hussien");
		// uDTO.setId(376);
		// uDTO.setFullName("Mohamed Hussien");
		uDTO.setId(381);
		uDTO.setFullName("Bahaa Zakaria");
		GrmRole role = new GrmRole();
		role.setId(3l);
		GrmGroup group = new GrmGroup();
		group.setId(1l);
		uDTO.setGrmRole(role);
		uDTO.setGrmGroup(group);
		QAChecks devPanel = new QAChecks(uDTO);
		frame.getContentPane().add(devPanel);
		frame.show();
		while(true)
		{
			ArrayList<String> flags = ParaQueryUtil.getAlerts(uDTO.getId(), 101, 3);
			// devPanel.updateFlags(flags);

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
		selectionPanel.updateFlags();
		tabSheet.updateFlags();
		// alertsPanel2.updateFlags(flags);

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
			/**
			 * Show pdfs Action
			 * **/
			if(event.getSource() == filterPanel.filterButton)
			{
				Date startDate = null;
				Date endDate = null;
				try
				{
					dofilter(startDate, endDate);
				}catch(Exception e)
				{
					e.printStackTrace();
				}
				filterPanel.setCollapsed(true);
			}
			else if(event.getSource() == filterPanel.refreshButton)
			{

				filterPanel.filterList = DataDevQueryUtil.getQAchecksFilterData(userDTO);
				filterPanel.refreshFilters();
				filterPanel.setCollapsed(true);

			}
			else if(event.getActionCommand().equals("Save"))
			{
				System.out.println("~~~~~~~ Start saving Data ~~~~~~~");
				if(!filterstatus.equals(StatusName.Open))
				{
					MainWindow.glass.setVisible(false);
					JOptionPane.showMessageDialog(null, "You can save Open checks only");

					return null;
				}
				wsMap.keySet();
				for(String wsName : wsMap.keySet())
				{
					if(wsName == "QAChecks")
					{
						if(!wsMap.get(wsName).saved)
						{
							wsMap.get(wsName).saved = true;
							wsMap.get(wsName).saveQAChecksAction(checker, engName);
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
			else if(event.getActionCommand().equals("Seperation"))
			{

				input = new ArrayList<ArrayList<String>>();
				tabbedPane.setSelectedIndex(2);
				row = new ArrayList<String>();
				row.add("PL_Name");// 0
				row.add("Part");// 1
				row.add("Datasheet");// 2
				row.add("Supplier");// 3
				row.add("ReceivedDate");// 4
				row.add("Feature Name");// 5
				row.add("Feature Value");// 6
				row.add("Feature Unit");// 7
				row.add("Sign");// 8
				row.add("Value");// 9
				row.add("Type");// 10
				row.add("Condition");// 11
				row.add("Multiplier");// 12
				row.add("Unit");// 13
				row.add("Validation result");// 14

				if(wsMap.get("Separation") != null)
				{
					wsMap.remove("Separation");
				}
				for(String wsName : wsMap.keySet())
				{
					if(wsName != "LoadAllData" && wsName != "Separation")
					{
						System.out.println("Sheet Name:" + wsName);
						input = separationValues;
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
			else if(event.getActionCommand().equals(" validate "))
			{
				validated = ws.validateSeparation();
				MainWindow.glass.setVisible(false);
				JOptionPane.showMessageDialog(null, " Validation Done");

			}
			else if(event.getActionCommand().equals(" save "))
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
					if(!validated)
					{
						MainWindow.glass.setVisible(false);
						JOptionPane.showMessageDialog(null,
								" Validate First due to some errors in your data");

						return null;
					}

					for(int i = 0; i < separationValues.size(); i++)
					{
						row = separationValues.get(i);

						String plName = row.get(0);
						String featureName = row.get(5);
						String featureFullValue = row.get(6);

						try
						{
							List<ApprovedParametricDTO> approved = ApprovedDevUtil
									.createApprovedValuesList(featureFullValue, plName,
											featureName, row.get(7), row.get(8), row.get(9),
											row.get(12), row.get(13), row.get(11), row.get(10));

							ApprovedDevUtil.saveAppGroupAndSepValue(0, 0, approved, plName,
									featureName, featureFullValue, row.get(2), userId);
						}catch(ArrayIndexOutOfBoundsException ex)
						{
							try
							{
								Cell cell = wsMap.get("Separation").getCellByPosission(14, i + 1);
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
		if(sheetpanel != null)
		{
			sheetpanel.closeApplication();
		}
		if(separationPanel != null)
		{
			separationPanel.closeApplication();
		}
	}

}
