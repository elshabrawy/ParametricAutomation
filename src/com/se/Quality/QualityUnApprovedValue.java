package com.se.Quality;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingWorker;
import javax.swing.border.BevelBorder;
import javax.swing.border.SoftBevelBorder;

import osheet.SheetPanel;
import osheet.WorkingSheet;

import com.se.automation.db.parametric.StatusName;
import com.se.grm.client.mapping.GrmGroup;
import com.se.grm.client.mapping.GrmRole;
import com.se.parametric.MainWindow;
import com.se.parametric.commonPanel.AlertsPanel;
import com.se.parametric.commonPanel.ButtonsPanel;
import com.se.parametric.commonPanel.FilterPanel;
import com.se.parametric.commonPanel.WorkingAreaPanel;
import com.se.parametric.dba.ApprovedDevUtil;
import com.se.parametric.dba.ParaQueryUtil;
import com.se.parametric.dev.PdfLinks;
import com.se.parametric.dto.DocumentInfoDTO;
import com.se.parametric.dto.GrmUserDTO;
import com.se.parametric.dto.UnApprovedDTO;

public class QualityUnApprovedValue extends JPanel implements ActionListener
{

	/**
	 * Create the panel.
	 */
	DocumentInfoDTO documentInfoDTO = null;
	SheetPanel sheetPanel = new SheetPanel();
	WorkingSheet ws;
	PdfLinks pdfLinks = null;
	ArrayList<ArrayList<String>> input = new ArrayList<ArrayList<String>>();
	WorkingAreaPanel tabSheet, selectionPanel/* , flowChart */;
	JTabbedPane tabbedPane;
	Map<String, WorkingSheet> wsMap = new HashMap<String, WorkingSheet>();
	ArrayList<ArrayList<String>> separationValues = new ArrayList<ArrayList<String>>();
	boolean foundPdf = false;
	FilterPanel filterPanel = null;
	public ArrayList<ArrayList<String>> list;
	Long[] teamMembers = null;
	ArrayList<String> row = null;
	GrmUserDTO userDTO;
	ArrayList<UnApprovedDTO> unApproveds;
	QAUnApprovedValueFeedback QAAppfeedBack = null;

	public QualityUnApprovedValue(GrmUserDTO userDTO)
	{
		this.userDTO = userDTO;
		this.setLayout(new BorderLayout());
		int width = Toolkit.getDefaultToolkit().getScreenSize().width;
		int height = Toolkit.getDefaultToolkit().getScreenSize().height;
		ArrayList<Object[]> filterData = ApprovedDevUtil.getUnapprovedReviewFilter(
				new Long[] { userDTO.getId() }, null, null, "QA");
		System.out.println("User:" + userDTO.getId() + " " + userDTO.getFullName() + " "
				+ filterData.size());
		selectionPanel = new WorkingAreaPanel(this.userDTO);
		String[] filterLabels = { "Eng Name", "PL Name", "Supplier", "Status", "Task Type" };
		filterPanel = selectionPanel.getFilterPanel(filterLabels, filterData, false, this);
		ArrayList<String> buttonLabels = new ArrayList<String>();
		buttonLabels.add("Save");
		selectionPanel.addButtonsPanel(buttonLabels, this);
		selectionPanel.addComponentsToPanel();
		QAAppfeedBack = new QAUnApprovedValueFeedback(userDTO);
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.addTab("Quality UnApproved Review", null, selectionPanel, null);
		tabbedPane.addTab("Quality UnApproved FeedBack", null, QAAppfeedBack, null);
		// flowChart = new ImagePanel("QASeparation.jpg");
		// tabbedPane.addTab("Separation Flow", null, flowChart, null);
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
		QAAppfeedBack.selectionPanel.updateFlags();

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
		uDTO.setId(79);
		uDTO.setFullName("Waleed Mady");
		// uDTO.setId(121);
		// uDTO.setFullName("Ahmad_rahim");
		GrmRole role = new GrmRole();
		role.setId(1l);
		GrmGroup group = new GrmGroup();
		group.setId(101l);
		uDTO.setGrmRole(role);
		uDTO.setGrmGroup(group);
		QualityUnApprovedValue devPanel = new QualityUnApprovedValue(uDTO);
		frame.getContentPane().add(devPanel);
		frame.show();
		while(true)
		{
//			ArrayList<String> flags = ParaQueryUtil.getAlerts(uDTO.getId(), 101, 3);
			devPanel.updateFlags();

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
			WorkingSheet ws = null;
			UnApprovedDTO obj = null;
			tabbedPane.setSelectedIndex(0);
			if(event.getSource().equals(filterPanel.filterButton))
			{
				Date startDate = null;
				Date endDate = null;
				if(filterPanel.jDateChooser1.isEnabled())
				{
					startDate = filterPanel.jDateChooser1.getDate();
					endDate = filterPanel.jDateChooser2.getDate();
				}

				String engName = filterPanel.comboBoxItems[0].getSelectedItem().toString();
				String plName = filterPanel.comboBoxItems[1].getSelectedItem().toString();
				String supplierName = filterPanel.comboBoxItems[2].getSelectedItem().toString();
				String status = filterPanel.comboBoxItems[3].getSelectedItem().toString();
				String taskType = filterPanel.comboBoxItems[4].getSelectedItem().toString();
				long userId = userDTO.getId();

				unApproveds = ApprovedDevUtil.getUnapprovedReviewData(new Long[] { userId },
						engName, startDate, endDate, plName, supplierName, StatusName.qaReview,
						taskType, "QA", "Data", userId);
				list = new ArrayList<ArrayList<String>>();
				row = new ArrayList<String>();
				sheetPanel.openOfficeDoc();
				ws = new WorkingSheet(sheetPanel, "Unapproved Values");
				sheetPanel.saveDoc("C:/Report/Quality_Auto/" + "QUnapparoved@"
						+ userDTO.getFullName() + "@" + System.currentTimeMillis() + ".xls");
				row.add("PL Name");
				row.add("Part Name");
				row.add("Pdf Url");
				row.add("Feature Name");
				row.add("Feature Value");
				row.add("Feature Unit");
				row.add("Sign");
				row.add("Value");
				row.add("Type");
				row.add("Condition");
				row.add("Multiplier");
				row.add("Unit");
				row.add("Status");
				row.add("Comment");
				wsMap.put("Unapproved Values", ws);
				ws.setUnapprovedHeader(row);
				for(int i = 0; i < unApproveds.size(); i++)
				{
					row = new ArrayList<String>();
					obj = unApproveds.get(i);
					row.add(obj.getPlName());
					row.add(obj.getPartNumber());
					row.add(obj.getPdfUrl());
					row.add(obj.getFeatureName());
					row.add(obj.getFeatureValue());
					row.add(obj.getFeatureUnit());
					row.add(obj.getSign());
					row.add(obj.getValue());
					row.add(obj.getType());
					row.add(obj.getCondition());
					row.add(obj.getMultiplier());
					row.add(obj.getUnit());
					list.add(row);
				}
				ArrayList<String> statusValues = new ArrayList<String>();
				statusValues.add("Approved");
				statusValues.add("Wrong Separation");
				statusValues.add("Wrong Value");
				ws.statusValues = statusValues;
				ws.writeReviewData(list, 1, 13);
				Robot bot = new Robot();
				bot.mouseMove(1165, 345);
				bot.mousePress(InputEvent.BUTTON1_MASK);
				bot.mouseRelease(InputEvent.BUTTON1_MASK);
				filterPanel.setCollapsed(true);
			}
			else if(event.getSource().equals(filterPanel.refreshButton))
			{
				Date startDate = null;
				Date endDate = null;

				if(filterPanel.jDateChooser1.isEnabled())
				{
					startDate = filterPanel.jDateChooser1.getDate();
					endDate = filterPanel.jDateChooser2.getDate();
				}
				filterPanel.filterList = ApprovedDevUtil.getUnapprovedReviewFilter(
						new Long[] { userDTO.getId() }, startDate, endDate, "QA");
				filterPanel.refreshFilters();
				filterPanel.setCollapsed(true);
			}
			else if(event.getActionCommand().equals("Save"))
			{
				if(!wsMap.get("Unapproved Values").saved)
				{
					String status = filterPanel.comboBoxItems[3].getSelectedItem().toString();
					for(String wsName : wsMap.keySet())
					{
						if(wsName == "Unapproved Values")
						{
							String work = wsMap.get(wsName).getSelectedCellValue();
							ArrayList<ArrayList<String>> result = wsMap.get(wsName)
									.readSpreadsheet(1);
							int updateFlag = 1;

							for(int i = 0; i < result.size(); i++)
							{
								ArrayList<String> newValReq = result.get(i);
								if((newValReq.get(12).equals("Wrong Separation") || newValReq.get(
										12).equals("Wrong Value"))
										&& newValReq.get(13).trim().isEmpty())
									MainWindow.glass.setVisible(false);
								JOptionPane.showMessageDialog(null,
										" You Must Write Comment with Status Wrong Separation,Wrong Value Check row : "
												+ (i + 1));
								return null;
							}
							/** Team Leader approved and send to QA */
							for(int i = 0; i < result.size(); i++)
							{
								ArrayList<String> newValReq = result.get(i);
								UnApprovedDTO oldValReq = unApproveds.get(i);
								if(newValReq.get(0).equals(oldValReq.getPlName())
										&& newValReq.get(3).equals(oldValReq.getFeatureName())
										&& newValReq.get(4).equals(oldValReq.getFeatureValue())
										&& newValReq.get(5).equals(oldValReq.getFeatureUnit()))
								{
									oldValReq.setSign(newValReq.get(6));
									oldValReq.setValue(newValReq.get(7));
									oldValReq.setType(newValReq.get(8));
									oldValReq.setCondition(newValReq.get(9));
									oldValReq.setMultiplier(newValReq.get(10));
									oldValReq.setUnit(newValReq.get(11));
									oldValReq.setFbStatus(StatusName.reject);
									oldValReq.setGruopSatus(StatusName.tlFeedback);
									oldValReq.setComment(newValReq.get(13));
									oldValReq.setIssuedby(userDTO.getId());
									oldValReq.setFbType("QA");
									oldValReq.setIssueType(newValReq.get(12));
									if(newValReq.get(12).equals("Approved"))
									{
										ApprovedDevUtil.setValueApproved(result.get(i),
												StatusName.cmTransfere);
									}

									// else if(newValReq.get(12).equals("Wrong Value"))
									// {
									// ApprovedDevUtil.saveAppWrongValue( oldValReq);
									// }
									// else if(newValReq.get(12).equals("Wrong Separation"))
									// {
									// ApprovedDevUtil.saveWrongSeparation( oldValReq);
									// }

									else if(newValReq.get(12).equals("Wrong Separation")
											|| newValReq.get(12).equals("Wrong Value"))
									{
										ApprovedDevUtil.saveWrongSeparation(oldValReq);
									}
								}
								else
								{
									JOptionPane.showMessageDialog(null, newValReq.get(0) + " @ "
											+ newValReq.get(4)
											+ " Can't Save dueto change in main columns");
								}
							}

							System.out.println("size is " + result.size());
						}
					}
					wsMap.get("Unapproved Values").saved = true;
					MainWindow.glass.setVisible(false);
					JOptionPane.showMessageDialog(null, "Save Done");
				}
				else
				{
					MainWindow.glass.setVisible(false);
					JOptionPane.showMessageDialog(null, "This Sheet Saved Before.");
					return null;
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
	}

}
