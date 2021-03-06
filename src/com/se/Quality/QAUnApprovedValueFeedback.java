package com.se.Quality;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import com.se.parametric.Loading;
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
import com.se.parametric.unappValue.TLUnApprovedValueFeedback;

public class QAUnApprovedValueFeedback extends JPanel implements ActionListener
{
	DocumentInfoDTO documentInfoDTO = null;
	SheetPanel sheetPanel = null;
	WorkingSheet ws;
	PdfLinks pdfLinks = null;
	ArrayList<ArrayList<String>> input = new ArrayList<ArrayList<String>>();
	WorkingAreaPanel tabSheet, selectionPanel;
	JTabbedPane tabbedPane;
	Map<String, WorkingSheet> wsMap = new HashMap<String, WorkingSheet>();
	ArrayList<ArrayList<String>> separationValues = new ArrayList<ArrayList<String>>();
	boolean foundPdf = false;
	FilterPanel filterPanel = null;
	public ArrayList<ArrayList<String>> list;
	Long[] teamMembers = null;
	ArrayList<String> row = null;
	GrmUserDTO userDTO;
	ArrayList<UnApprovedDTO> unApproveds = new ArrayList<UnApprovedDTO>();;

	public QAUnApprovedValueFeedback(GrmUserDTO userDTO)
	{
		this.userDTO = userDTO;
		this.setLayout(new BorderLayout());
		int width = Toolkit.getDefaultToolkit().getScreenSize().width;
		int height = Toolkit.getDefaultToolkit().getScreenSize().height;

		ArrayList<Object[]> filterData = ApprovedDevUtil.getEngUnapprovedData(userDTO, null, null,
				"QA");
		selectionPanel = new WorkingAreaPanel(this.userDTO);
		String[] filterLabels = { "PL Name", "Supplier", "Task Type", "FeedBack Type" };
		filterPanel = selectionPanel.getFilterPanel(filterLabels, filterData, false, this);
		ArrayList<String> buttonLabels = new ArrayList<String>();
		buttonLabels.add("Save");
		buttonLabels.add("Feedback History");
		selectionPanel.addButtonsPanel(buttonLabels, this);
		sheetPanel = selectionPanel.getSheet();
		selectionPanel.addComponentsToPanel();

		this.add(selectionPanel);

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
		uDTO.setId(46);
		uDTO.setFullName("Ahmed Risk");
		uDTO.setId(121);
		uDTO.setFullName("Ahmad_rahim");
		GrmRole role = new GrmRole();
		role.setId(1l);
		GrmGroup group = new GrmGroup();
		group.setId(1l);
		uDTO.setGrmRole(role);
		uDTO.setGrmGroup(group);
		TLUnApprovedValueFeedback devPanel = new TLUnApprovedValueFeedback(uDTO);
		frame.getContentPane().add(devPanel);
		frame.show();
		while(true)
		{
			// ArrayList<String> flags = ParaQueryUtil.getAlerts(uDTO.getId(), 1, 1);
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
			// Thread thread = new Thread(loading);
			// thread.start();
			UnApprovedDTO obj = null;
			String statuses[];

			if(event.getSource().equals(filterPanel.filterButton))
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
				String feedBackType = filterPanel.comboBoxItems[3].getSelectedItem().toString();
				unApproveds = ApprovedDevUtil.getUnapprovedReviewData(
						new Long[] { userDTO.getId() }, "", startDate, endDate, plName,
						supplierName, StatusName.qaFeedback, taskType, "QA", "FB", userDTO.getId());
				list = new ArrayList<ArrayList<String>>();

				list = new ArrayList<ArrayList<String>>();
				row = new ArrayList<String>();
				sheetPanel.openOfficeDoc();

				// ws = new WorkingSheet(new SheetPanel(), "Feedback Values",1);
				ws = new WorkingSheet(sheetPanel, "Unapproved Values", 0, true);

				// sheetPanel.add(arg0)
				sheetPanel.saveDoc("C:/Report/Parametric_Auto/" + "Unapproved@"
						+ userDTO.getFullName() + "@" + System.currentTimeMillis() + ".xls");
				row.add("PL Name");// 0
				row.add("Part Name");// 1
				row.add("Pdf Url");// 2
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
				row.add("QA Status");// 14
				row.add("QA Comment");// 15
				row.add("c_Action");// 16
				row.add("P_Action");// 17
				row.add("ROOT_CAUSE");// 18
				row.add("ACTION_DUE_DATE");// 19
				row.add("Issue Type");// 20
				row.add("FeedBack Type");// 21
				row.add("Para Status");// 22
				row.add("Para Comment");// 23
				row.add("Old QA Status");// 24
				row.add("Old QA Comment");// 25

				wsMap.put("Unapproved Values", ws);
				wsMap.get("Unapproved Values");
				ws.setUnapprovedHeader(row);
				for(int i = 0; i < unApproveds.size(); i++)
				{
					row = new ArrayList<String>();
					obj = unApproveds.get(i);
					row.add(obj.getPlName());
					row.add(obj.getPartNumber());
					row.add(obj.getPdfUrl());
					row.add(obj.getSupplier());
					row.add(obj.getReceivedDate());
					row.add(obj.getFeatureName());
					row.add(obj.getFeatureValue());
					row.add(obj.getFeatureUnit());
					row.add(obj.getSign());
					row.add(obj.getValue());
					row.add(obj.getType());
					row.add(obj.getCondition());
					row.add(obj.getMultiplier());
					row.add(obj.getUnit());
					row.add("");
					row.add("");
					row.add(obj.getCAction());
					row.add(obj.getPAction());
					row.add(obj.getRootCause());
					row.add(obj.getActionDueDate());
					row.add(obj.getIssueType());
					row.add(obj.getFbType());
					row.add(obj.getFbStatus());
					row.add(obj.getComment());
					row.add(obj.getQaStatus());
					row.add(obj.getQaComment());
					list.add(row);
				}
				ArrayList<String> statusValues = new ArrayList<String>();
				statusValues.add("Approved");
				statusValues.add("Wrong Separation");
				statusValues.add("Wrong Value");
				ws.statusValues = statusValues;
				// ArrayList<String> commentValues = new ArrayList<String>();
				// commentValues.add(StatusName.approved);
				// commentValues.add(StatusName.reject);
				// ws.commentValues = commentValues;
				ws.writeReviewData(list, 1, 15);
				Robot bot = new Robot();
				bot.mouseMove(1165, 345);
				bot.mousePress(InputEvent.BUTTON1_MASK);
				bot.mouseRelease(InputEvent.BUTTON1_MASK);
				// filterPanel.jDateChooser1.setDate(new Date(System.currentTimeMillis()));
				// filterPanel.jDateChooser2.setDate(new Date(System.currentTimeMillis()));
				filterPanel.setCollapsed(true);
			}
			else if(event.getActionCommand().equals("Feedback History"))
			{
				ws = new WorkingSheet(sheetPanel, "Feedback", 1, true);
				wsMap.put("Feedback", ws);
				ws = wsMap.get("Feedback");
				try
				{
					// sheetPanel.getSelectedXCell();
					String url = sheetPanel.getCellText(sheetPanel.getCellByPosission(1, 1))
							.toString();
					System.out.println("" + url);
				}catch(Exception e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				String url = sheetPanel.getCellText(sheetPanel.getSelectedXCell()).getString();
				System.out.println(url + " " + "done here");
				ArrayList<String> header = new ArrayList<String>();
				header.add("Pdf Url");
				header.add("Feedback Item");
				header.add("Initiator");
				header.add("Issued To");
				header.add("Issue Type");
				header.add("Feedback Type");
				header.add("Feedback Comment");
				header.add("Feedback Status");
				header.add("Store Date");
				header.add("C_Action");
				header.add("P_Action");
				header.add("Root Cause");
				header.add("Action Due Date");

				ws.setHistoryHeader(header);
				ArrayList<ArrayList<String>> list = ApprovedDevUtil.getFeedbackHistory(url);
				ws.writeSheetData(list, 1);

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
				filterPanel.filterList = ApprovedDevUtil.getEngUnapprovedData(userDTO, startDate,
						endDate, "QA");
				filterPanel.refreshFilters();
				filterPanel.setCollapsed(true);
			}
			else if(event.getActionCommand().equals("Save"))
			{
				String status = filterPanel.comboBoxItems[3].getSelectedItem().toString();
				for(String wsName : wsMap.keySet())
				{
					if(wsName == "Unapproved Values")
					{
						String work = wsMap.get(wsName).getSelectedCellValue();
						ArrayList<ArrayList<String>> result = wsMap.get(wsName).readSpreadsheet(1);
						int updateFlag = 1;
						for(int i = 0; i < result.size(); i++)
						{
							ArrayList<String> newValReq = result.get(i);
							if(!newValReq.get(14).equals("Approved") && newValReq.get(15).isEmpty())
							{
								MainWindow.glass.setVisible(false);
								JOptionPane.showMessageDialog(null, " Comment have value  at Row :"
										+ (i + 1));
								return null;
							}
							// if(newValReq.get(14).equals("Approved")
							// && (!newValReq.get(15).equals(StatusName.approved) && !newValReq
							// .get(15).equals(StatusName.reject)))
							// {
							// MainWindow.glass.setVisible(false);
							// JOptionPane.showMessageDialog(null, " Comment Must be in ("
							// + StatusName.approved + " and " + StatusName.reject
							// + " ) at Row :" + (i + 1));
							// return null;
							// }
						}
						/** Team Leader approved and send to QA */
						for(int i = 0; i < result.size(); i++)
						{
							ArrayList<String> newValReq = result.get(i);
							UnApprovedDTO oldValReq = unApproveds.get(i);
							if(newValReq.get(0).equals(oldValReq.getPlName())
									&& newValReq.get(5).equals(oldValReq.getFeatureName())
									&& newValReq.get(6).equals(oldValReq.getFeatureValue())
									&& newValReq.get(7).equals(oldValReq.getFeatureUnit()))
							{
								oldValReq.setSign(newValReq.get(8));
								oldValReq.setValue(newValReq.get(9));
								oldValReq.setType(newValReq.get(10));
								oldValReq.setCondition(newValReq.get(11));
								oldValReq.setMultiplier(newValReq.get(12));
								oldValReq.setUnit(newValReq.get(13));
								oldValReq.setFbStatus(StatusName.reject);
								oldValReq.setGruopSatus(StatusName.tlFeedback);
								oldValReq.setComment(newValReq.get(15));
								long issuedto = oldValReq.getIssuedby();
								oldValReq.setIssuedby(userDTO.getId());
								oldValReq.setIssueTo(issuedto);
								oldValReq.setFbType("QA");

								if(newValReq.get(14).equals("Approved"))
								{
									oldValReq.setFbStatus(StatusName.fbClosed);
									oldValReq.setGruopSatus(StatusName.cmTransfere);
									// ApprovedDevUtil.setValueApproved(result.get(i),
									// StatusName.cmTransfere);
									ApprovedDevUtil.replyApprovedValueFB(oldValReq);
								}

								else if(newValReq.get(14).equals("Wrong Value"))
								{
									oldValReq.setIssueType(newValReq.get(14));
									ApprovedDevUtil.replyApprovedValueFB(oldValReq);
								}
								else if(newValReq.get(14).equals("Wrong Separation"))
								{
									oldValReq.setIssueType(newValReq.get(14));
									ApprovedDevUtil.replyApprovedValueFB(oldValReq);
								}
							}
							else
							{
								MainWindow.glass.setVisible(false);
								JOptionPane.showMessageDialog(null, newValReq.get(0) + " @ "
										+ newValReq.get(6)
										+ " Can't Save dueto change in main columns");
							}
						}

						System.out.println("size is " + result.size());
					}
				}
				MainWindow.glass.setVisible(false);
				JOptionPane.showMessageDialog(null, "Save Done");
			}
			MainWindow.glass.setVisible(false);
			return null;
		}
	}
}
