package com.se.parametric.unappValue;

import java.awt.Color;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingWorker;
import javax.swing.border.BevelBorder;
import javax.swing.border.SoftBevelBorder;

import org.hibernate.Session;

import osheet.SheetPanel;
import osheet.WorkingSheet;

import com.se.automation.db.SessionUtil;
import com.se.automation.db.parametric.StatusName;
import com.se.grm.client.mapping.GrmGroup;
import com.se.grm.client.mapping.GrmRole;
import com.se.grm.client.mapping.GrmUser;
import com.se.parametric.Loading;
import com.se.parametric.commonPanel.AlertsPanel;
import com.se.parametric.commonPanel.ButtonsPanel;
import com.se.parametric.commonPanel.FilterPanel;
import com.se.parametric.dba.ApprovedDevUtil;
import com.se.parametric.dba.ParaQueryUtil;
import com.se.parametric.dev.PdfLinks;
import com.se.parametric.dto.DocumentInfoDTO;
import com.se.parametric.dto.GrmUserDTO;
import com.se.parametric.dto.UnApprovedDTO;
import com.se.parametric.util.ImagePanel;

public class EngUnApprovedValueFeedback extends JPanel implements ActionListener
{

	DocumentInfoDTO documentInfoDTO = null;
	SheetPanel sheetPanel = null;
	WorkingSheet ws;
	PdfLinks pdfLinks = null;
	ArrayList<ArrayList<String>> input = new ArrayList<ArrayList<String>>();
	JPanel tabSheet, selectionPanel, flowChart;
	JPanel devSheetButtonPanel;
	JTabbedPane tabbedPane;
	JButton button = null;
	JButton showAll = new JButton("Show All");
	JButton save, validate, separation;
	Map<String, WorkingSheet> wsMap = new HashMap<String, WorkingSheet>();
	ArrayList<ArrayList<String>> separationValues = new ArrayList<ArrayList<String>>();
	boolean foundPdf = false;
	JPanel unapprovedPanel = null;
	FilterPanel filterPanel = null;
	ButtonsPanel buttonsPanel;
	public ArrayList<ArrayList<String>> list;
	Long[] teamMembers = null;
	ArrayList<String> row = null;
	GrmUserDTO userDTO;
	ArrayList<UnApprovedDTO> unApproveds = new ArrayList<UnApprovedDTO>();;
	static AlertsPanel alertsPanel, alertsPanel1;
	boolean validated;

	public EngUnApprovedValueFeedback(GrmUserDTO userDTO)
	{
		this.userDTO = userDTO;
		setLayout(null);
		int width = Toolkit.getDefaultToolkit().getScreenSize().width;
		int height = Toolkit.getDefaultToolkit().getScreenSize().height;
		ArrayList<Object[]> filterData = ApprovedDevUtil.getEngUnapprovedData(userDTO, null, null, "Eng");
		System.out.println("User:" + userDTO.getId() + " " + userDTO.getFullName() + " " + filterData.size());
		selectionPanel = new JPanel();
		String[] filterLabels = { "PL Name", "Supplier", "Task Type" };
		sheetPanel = new SheetPanel();
		sheetPanel.setSize(width - 110, (((height - 100) * 6) / 10));
		sheetPanel.setBounds(0, (((height - 100) * 3) / 10), width - 120, (((height - 100) * 7) / 10));
		// filterPanel = new FilterPanel(filterLabels, filterData, width - 110, (((height - 100) * 4) / 10));
		// filterPanel.setBounds(0, 0, width - 110, (((height - 100) * 4) / 10));
		filterPanel = new FilterPanel(filterLabels, filterData, width - 110, (((height - 100) * 3) / 10), false);
		filterPanel.setBounds(0, 0, width - 120, (((height - 100) * 3) / 10));
		ArrayList<String> buttonLabels = new ArrayList<String>();
		buttonLabels.add(" validate ");
		buttonLabels.add("Save");

		buttonsPanel = new ButtonsPanel(buttonLabels);
		JButton buttons[] = buttonsPanel.getButtons();
		for(int i = 0; i < buttons.length; i++)
		{
			buttons[i].addActionListener(this);
		}
		buttonsPanel.setBounds(width - 120, 0, 108, height / 3);
		alertsPanel = new AlertsPanel(userDTO);
		alertsPanel1 = new AlertsPanel(userDTO);
		alertsPanel.setBounds(width - 120, height / 3, 110, height * 3 / 4);
		alertsPanel1.setBounds(width - 120, height / 3, 110, height * 3 / 4);
		selectionPanel.setLayout(null);
		selectionPanel.add(filterPanel);
		selectionPanel.add(sheetPanel);
		selectionPanel.add(buttonsPanel);
		selectionPanel.add(alertsPanel);
		selectionPanel.setBounds(0, 0, width - 120, height - 100);
		filterPanel.filterButton.addActionListener(this);
		filterPanel.refreshButton.addActionListener(this);
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(0, 0, width, height - 100);
		tabSheet = new JPanel();
		devSheetButtonPanel = new JPanel();
		devSheetButtonPanel.setBackground(new Color(211, 211, 211));
		devSheetButtonPanel.setBorder(new SoftBevelBorder(BevelBorder.RAISED, null, null, null, null));
		devSheetButtonPanel.setBounds(width - 120, 0, 108, height / 3);
		devSheetButtonPanel.setLayout(null);
		save = new JButton("Save");
		save.setBounds(3, 80, 85, 29);
		validate = new JButton("Validate");
		validate.setBounds(3, 46, 85, 29);
		separation = new JButton("Separation");
		separation.setBounds(3, 11, 85, 29);
		validate.addActionListener(this);
		save.addActionListener(this);
		separation.addActionListener(this);
		devSheetButtonPanel.add(separation);
		devSheetButtonPanel.add(validate);
		devSheetButtonPanel.add(save);
		devSheetButtonPanel.setBackground(new Color(211, 211, 211));
		tabSheet.setLayout(null);
		tabSheet.add(devSheetButtonPanel);
		tabSheet.add(alertsPanel1);
		add(tabbedPane);
		tabbedPane.addTab("Input Selection", null, selectionPanel, null);
		tabbedPane.addTab("Sheet", null, tabSheet, null);
		flowChart = new ImagePanel("QASeparation.jpg");
		tabbedPane.addTab("Separation Flow", null, flowChart, null);
	}

	@Override
	public void actionPerformed(ActionEvent event)
	{
		LongRunProcess longRunProcess = new LongRunProcess(event);
		longRunProcess.execute();
	}

	public void updateFlags(ArrayList<String> flags)
	{
		alertsPanel.updateFlags(flags);
		alertsPanel1.updateFlags(flags);

	}

	public static void main(String[] args)
	{
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		int width = Toolkit.getDefaultToolkit().getScreenSize().width;
		int height = Toolkit.getDefaultToolkit().getScreenSize().height;
		frame.setSize(width, height);
		frame.setTitle("Eng UnApproved Values");
		GrmUserDTO uDTO = new GrmUserDTO();
		uDTO.setId(121);
		uDTO.setFullName("Ahmad_rahim");
		GrmUser leader = new GrmUser();
		leader.setFullName("Ahmed Rizk");
		leader.setId(46);
		uDTO.setLeader(leader);
		GrmRole role = new GrmRole();
		role.setId(1l);
		GrmGroup group = new GrmGroup();
		group.setId(1l);
		uDTO.setGrmRole(role);
		uDTO.setGrmGroup(group);
		EngUnApprovedValueFeedback devPanel = new EngUnApprovedValueFeedback(uDTO);
		frame.getContentPane().add(devPanel);
		frame.show();
		while(true)
		{
			ArrayList<String> flags = ParaQueryUtil.getAlerts(uDTO.getId(), 1, 1);
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
			UnApprovedDTO obj = null;
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
				unApproveds = ApprovedDevUtil.getUnapprovedReviewData(new Long[] { userDTO.getId() }, "", startDate, endDate, plName, supplierName, StatusName.engFeedback, taskType, "Parametric", "FB", userDTO.getId());
				list = new ArrayList<ArrayList<String>>();

				list = new ArrayList<ArrayList<String>>();
				row = new ArrayList<String>();
				sheetPanel.openOfficeDoc();
				ws = new WorkingSheet(sheetPanel, "Unapproved Values");
				sheetPanel.saveDoc("C:/Report/Parametric_Auto/" + "Unapproved@" + userDTO.getFullName() + "@" + System.currentTimeMillis() + ".xls");
				row.add("PL Name");// 0
				row.add("Part Name");// 1
				row.add("Pdf Url");// 2
				row.add("Feature Name");// 3
				row.add("Feature Value");// 4
				row.add("Feature Unit");// 5
				row.add("Sign");// 6
				row.add("Value");// 7
				row.add("Type");// 8
				row.add("Condition");// 09
				row.add("Multiplier");// 10
				row.add("Unit");// 11
				row.add("Dev Status");// 12
				row.add("Dev Comment");// 13

				row.add("C_ACTION");// 14
				row.add("P_ACTION");// 15
				row.add("ROOT_CAUSE");// 16
				row.add("Action Due Date");// 17

				row.add("FeedBack Type");// 18
				row.add("Issue Type");// 19
				row.add("TL Status");// 20
				row.add("TL Comment");// 21
				row.add("QA Status");// 22
				row.add("QA Comment");// 23
				row.add("Old Eng Comment");// 24
				row.add("Validation Result");// 25

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
					row.add("");
					row.add("");

					row.add(obj.getCAction());
					row.add(obj.getPAction());
					row.add(obj.getRootCause());
					row.add(obj.getActionDueDate());

					row.add(obj.getFbType());
					row.add(obj.getIssueType());
					row.add(obj.getFbStatus());
					row.add(obj.getComment());
					row.add(obj.getQaStatus());
					row.add(obj.getQaComment());
					row.add(obj.getLastEngComment());
					list.add(row);
				}
				ArrayList<String> statusValues = new ArrayList<String>();
				statusValues.add("Update");
				statusValues.add("Reject");
				statusValues.add("Accept Wrong Value");
				ws.statusValues = statusValues;
				ws.writeReviewData(list, 1, 13);
				// filterPanel.jDateChooser1.setDate(new Date(System.currentTimeMillis()));
				// filterPanel.jDateChooser2.setDate(new Date(System.currentTimeMillis()));
			}
			else if(event.getSource().equals(filterPanel.refreshButton))
			{
				// filterPanel.filterList = ApprovedDevUtil.getTLUnapprovedFeedBack(userDTO, null, null);
				filterPanel.filterList = ApprovedDevUtil.getEngUnapprovedData(userDTO, null, null, "Eng");
				filterPanel.refreshFilters();
			}

			else if(event.getActionCommand().equals(" validate "))
			{
				// tabbedPane.setSelectedIndex(0);
				ArrayList<ArrayList<String>> wsheet = wsMap.get("Unapproved Values").readSpreadsheet(1);
				if(wsheet.isEmpty())
				{
					tabbedPane.setSelectedIndex(1);
					JOptionPane.showMessageDialog(null, "All Values are Approved");

				}
				else
				{
					ArrayList<ArrayList<String>> validationResult = new ArrayList<>();
					validated = true;
					// Session session = SessionUtil.getSession();
					for(int i = 0; i < wsheet.size(); i++)
					{
						row = wsheet.get(i);
						List<String> result = ApprovedDevUtil.validateSeparation(row);
						row.set(25, result.get(0));
						validationResult.add(row);
						if(result.get(0) != "" && result.get(1).equals("false"))
						{
							validated = false;
						}
					}
					ws.writeSheetData(validationResult, 1);
					// session.close();
					JOptionPane.showMessageDialog(null, " Validation Done");
				}
			}

			else if(event.getActionCommand().equals("Save"))
			{
				for(String wsName : wsMap.keySet())
				{
					if(wsName == "Unapproved Values")
					{
						Session session = SessionUtil.getSession();
						ArrayList<ArrayList<String>> result = wsMap.get(wsName).readSpreadsheet(1);
						int updateFlag = 2;
						/** Team Leader approved and send to QA */
						for(int i = 0; i < result.size(); i++)
						{
							ArrayList<String> newValReq = result.get(i);
							if(newValReq.get(12).equals("Update"))
							{
								try
								{
									if(!validated)
									{
										Loading.close();
										JOptionPane.showMessageDialog(null, " Validate First due to some errors in your data");

										return null;
									}
								}catch(Exception e)
								{
									continue;
								}
							}
							if(newValReq.get(12).equals("Update") && !newValReq.get(19).equals("Wrong Separation"))
							{
								Loading.close();
								JOptionPane.showMessageDialog(null, " You Can update on Wrong Seperation Feedback only in row :" + (i + 1));

								return null;
							}
							if(newValReq.get(12).equals("Accept Wrong Value") && !newValReq.get(19).equals("Wrong Value"))
							{
								Loading.close();
								JOptionPane.showMessageDialog(null, " You Can Accept on Wrong Value Feedback only in row :" + (i + 1));

								return null;
							}
							if((newValReq.get(12).equals("Update") || newValReq.get(12).equals("Accept Wrong Value")) && newValReq.get(18).equals("QA"))
							{
								if(newValReq.get(14).isEmpty() || newValReq.get(15).isEmpty() || newValReq.get(16).isEmpty() || newValReq.get(17).isEmpty())
								{
									Loading.close();
									JOptionPane.showMessageDialog(null, " You must enter C_Action && P_Action && ROOT_Cause && Action_Due_Date in row :" + (i + 1));

									return null;
								}
								if(!newValReq.get(17).isEmpty())
								{
									if(ApprovedDevUtil.isThisDateValid(newValReq.get(17), "DD/MM/YYYY") == false)
									{
										Loading.close();
										JOptionPane.showMessageDialog(null, " You must enter Action_Due_Date with 'dd/MM/yyyy' fromat in row :" + (i + 1));

										return null;
									}
								}
							}
						}
						for(int i = 0; i < result.size(); i++)
						{
							ArrayList<String> newValReq = result.get(i);
							UnApprovedDTO oldValReq = unApproveds.get(i);
							if(newValReq.get(0).equals(oldValReq.getPlName()) && newValReq.get(3).equals(oldValReq.getFeatureName()) && newValReq.get(4).equals(oldValReq.getFeatureValue()) && newValReq.get(5).equals(oldValReq.getFeatureUnit()))
							{
								oldValReq.setSign(newValReq.get(6));
								oldValReq.setValue(newValReq.get(7));
								oldValReq.setType(newValReq.get(8));
								oldValReq.setCondition(newValReq.get(9));
								oldValReq.setMultiplier(newValReq.get(10));
								oldValReq.setUnit(newValReq.get(11));
								oldValReq.setComment(newValReq.get(13));
								long issuedto = oldValReq.getIssuedby();
								long issuedby = oldValReq.getIssueTo();

								if(result.get(i).get(12).equals("Reject"))
								{
									oldValReq.setFbType(StatusName.internal);
									// ParaQueryUtil.saveRejectEng(userDTO, oldValReq, newValReq.get(15));
									oldValReq.setIssuedby(issuedby);
									oldValReq.setIssueTo(issuedto);
									oldValReq.setFbStatus(StatusName.reject);
									oldValReq.setGruopSatus(StatusName.tlFeedback);
									ApprovedDevUtil.replyApprovedValueFB(oldValReq);
								}
								else if(result.get(i).get(12).equals("Update"))
								{
									oldValReq.setCAction(newValReq.get(14));
									oldValReq.setPAction(newValReq.get(15));
									oldValReq.setRootCause(newValReq.get(16));
									oldValReq.setActionDueDate(newValReq.get(17));

									oldValReq.setFbType(StatusName.internal);
									oldValReq.setIssuedby(issuedby);
									oldValReq.setIssueTo(issuedto);
									oldValReq.setFbStatus(StatusName.accept);
									oldValReq.setGruopSatus(StatusName.tlFeedback);
									ApprovedDevUtil.updateApprovedValue(updateFlag, oldValReq);
									ApprovedDevUtil.replyApprovedValueFB(oldValReq);
								}
								else if(result.get(i).get(12).equals("Accept Wrong Value"))
								{
									oldValReq.setCAction(newValReq.get(14));
									oldValReq.setPAction(newValReq.get(15));
									oldValReq.setRootCause(newValReq.get(16));
									oldValReq.setActionDueDate(newValReq.get(17));
									// close old Feedback

									oldValReq.setIssuedby(issuedby);
									oldValReq.setIssueTo(issuedto);
									oldValReq.setFbStatus(StatusName.accept);
									oldValReq.setGruopSatus(StatusName.tlFeedback);
									ApprovedDevUtil.replyApprovedValueFB(oldValReq);
									// initiate new FB from QA to Eng
									oldValReq.setGruopSatus(StatusName.engFeedback);
									oldValReq.setIssueType(StatusName.wrongValue);
									oldValReq.setFbStatus(StatusName.reject);
									oldValReq.setIssueTo(issuedby);

									if(oldValReq.getFbType().equals(StatusName.internal))
									{
										oldValReq.setIssuedby(issuedto);
										oldValReq.setComment(newValReq.get(21));
									}
									else
									{
										Long qaUserId = 0L;
										try
										{
											qaUserId = ParaQueryUtil.getQAUserId(ParaQueryUtil.getPlByPlName(session, oldValReq.getPlName()), ParaQueryUtil.getTrackingTaskTypeByName("Approved Values", session));
										}catch(Exception e)
										{
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
										oldValReq.setComment(newValReq.get(23));
										oldValReq.setIssuedby(qaUserId);
									}
									// oldValReq.setFbType(oldValReq.getFbType());
									ApprovedDevUtil.saveAppWrongValue(oldValReq);

								}
							}
							else
							{
								JOptionPane.showMessageDialog(null, newValReq.get(0) + " @ " + newValReq.get(4) + " Can't Save dueto change in main columns");
							}
						}

						System.out.println("size is " + result.size());
					}
				}
				JOptionPane.showMessageDialog(null, "Saved Done");
			}
			Loading.close();
			return null;
		}
	}

}
