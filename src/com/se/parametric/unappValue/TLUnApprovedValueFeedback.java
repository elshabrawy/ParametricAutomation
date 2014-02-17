package com.se.parametric.unappValue;

import java.awt.Color;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.BevelBorder;
import javax.swing.border.SoftBevelBorder;

import org.hibernate.Session;

import osheet.SheetPanel;
import osheet.WorkingSheet;

import com.se.automation.db.SessionUtil;
import com.se.grm.client.mapping.GrmGroup;
import com.se.grm.client.mapping.GrmRole;
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
import com.se.parametric.util.StatusName;

public class TLUnApprovedValueFeedback extends JPanel implements ActionListener
{

	DocumentInfoDTO documentInfoDTO = null;
	SheetPanel sheetPanel = null;
	WorkingSheet ws;
	PdfLinks pdfLinks = null;
	ArrayList<ArrayList<String>> input = new ArrayList<ArrayList<String>>();
	JPanel tabSheet, selectionPanel;
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
	public ArrayList<ArrayList<String>> list;
	Long[] teamMembers = null;
	ArrayList<String> row = null;
	GrmUserDTO TLDTO;
	ArrayList<UnApprovedDTO> unApproveds = new ArrayList<UnApprovedDTO>();;
	public static AlertsPanel alertsPanel;
	boolean validated;

	public TLUnApprovedValueFeedback(GrmUserDTO TLDTO)
	{
		this.TLDTO = TLDTO;
		setLayout(null);
		int width = Toolkit.getDefaultToolkit().getScreenSize().width;
		int height = Toolkit.getDefaultToolkit().getScreenSize().height;
		teamMembers = ParaQueryUtil.getTeamMembersIDByTL(TLDTO.getId());
		ArrayList<Object[]> filterData = ApprovedDevUtil.getEngUnapprovedData(TLDTO, null, null, "TL");
		// System.out.println("User:" + userDTO.getId() + " " + userDTO.getFullName() + " " + filterData.size());
		selectionPanel = new JPanel();
		String[] filterLabels = { "PL Name", "Supplier", "Task Type", "FeedBack Type" };
		sheetPanel = new SheetPanel();
		sheetPanel.setSize(width - 110, (((height - 100) * 6) / 10));
		sheetPanel.setBounds(0, (((height - 100) * 3) / 10), width - 110, (((height - 100) * 7) / 10));
		// filterPanel.setBounds(0, 0, width - 110, (((height - 100) * 4) / 10));
		filterPanel = new FilterPanel(filterLabels, filterData, width - 110, (((height - 100) * 3) / 10));
		filterPanel.setBounds(0, 0, width - 110, (((height - 100) * 3) / 10));
		alertsPanel = new AlertsPanel(TLDTO);
		alertsPanel.setBounds(width - 120, height / 3, 110, height * 3 / 4);
		selectionPanel.setLayout(null);
		selectionPanel.add(filterPanel);
		selectionPanel.add(sheetPanel);
		selectionPanel.add(alertsPanel);
		selectionPanel.setBounds(0, 0, width - 120, height - 100);
		filterPanel.filterButton.addActionListener(this);
		filterPanel.refreshButton.addActionListener(this);
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(0, 0, width, height - 100);
		// tabSheet = new JPanel();
		devSheetButtonPanel = new JPanel();
		devSheetButtonPanel.setBackground(new Color(211, 211, 211));
		devSheetButtonPanel.setBorder(new SoftBevelBorder(BevelBorder.RAISED, null, null, null, null));
		devSheetButtonPanel.setBounds(width - 120, 0, 108, height / 3);
		devSheetButtonPanel.setLayout(null);
		save = new JButton("Save");
		validate = new JButton("validate");

		save.setBounds(3, 39, 95, 29);
		save.setForeground(new Color(25, 25, 112));
		save.setFont(new Font("Tahoma", Font.BOLD, 11));
		save.setForeground(new Color(25, 25, 112));

		validate.setBounds(3, 5, 95, 29);
		validate.setFont(new Font("Tahoma", Font.BOLD, 11));
		validate.addActionListener(this);
		save.addActionListener(this);

		devSheetButtonPanel.add(validate);
		devSheetButtonPanel.add(save);

		devSheetButtonPanel.setBackground(new Color(211, 211, 211));
		add(devSheetButtonPanel);
		add(selectionPanel);
		add(alertsPanel);
	}

	@Override
	public void actionPerformed(ActionEvent event)
	{
		Loading loading = new Loading();
		// WorkingSheet ws = null;
		Thread thread = new Thread(loading);
		thread.start();
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
			String feedBackType = filterPanel.comboBoxItems[3].getSelectedItem().toString();
			// unApproveds = ParaQueryUtil.getTLUnapprovedFeedBack(TLDTO, startDate, endDate, plName, supplierName, taskType, feedBackType);
			unApproveds = ApprovedDevUtil.getUnapprovedReviewData(teamMembers, "", startDate, endDate, plName, supplierName, StatusName.tlFeedback, taskType, "Parametric", "FB", TLDTO.getId());
			list = new ArrayList<ArrayList<String>>();

			list = new ArrayList<ArrayList<String>>();
			row = new ArrayList<String>();
			sheetPanel.openOfficeDoc();
			ws = new WorkingSheet(sheetPanel, "Unapproved Values");
			sheetPanel.saveDoc("C:/Report/Parametric_Auto/" + "Unapproved@" + TLDTO.getFullName() + "@" + System.currentTimeMillis() + ".xls");
			row.add("PL Name");// 0
			row.add("Part Name");// 1
			row.add("Pdf Url");// 2
			row.add("Feature Name");// 3
			row.add("Feature Value");// 4
			row.add("Feature Unit");// 5
			row.add("Sign");// 6
			row.add("Value");// 7
			row.add("Type");// 8
			row.add("Condition");// 9
			row.add("Multiplier");// 10
			row.add("Unit");// 11
			row.add("TL Status");// 12
			row.add("TL Comment");// 13

			row.add("C_ACTION");// 14
			row.add("P_ACTION");// 15
			row.add("ROOT_CAUSE");// 16
			row.add("Action Due Date");// 17

			row.add("FeedBack Type");// 18
			row.add("Issue Type");// 19
			row.add("Dev Status");// 20
			row.add("Dev Comment");// 21
			row.add("QA Status");// 22
			row.add("QA Comment");// 23
			row.add("Last TL Comment");// 24
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
			statusValues.add("Approved Eng.");
			statusValues.add("Wrong Separation");
			statusValues.add("Wrong Value");
			statusValues.add("Reject QA");
			statusValues.add("Accept QA & Forward");
			ws.statusValues = statusValues;
			ws.writeReviewData(list, 1, 13);
			// filterPanel.jDateChooser1.setDate(new Date(System.currentTimeMillis()));
			// filterPanel.jDateChooser2.setDate(new Date(System.currentTimeMillis()));
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
			filterPanel.filterList = ApprovedDevUtil.getEngUnapprovedData(TLDTO, startDate, endDate, "TL");
			filterPanel.refreshFilters();
		}

		else if(event.getSource().equals(validate))
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
				Session session = SessionUtil.getSession();
				for(int i = 0; i < wsheet.size(); i++)
				{
					row = wsheet.get(i);
					String result = ApprovedDevUtil.validateSeparation(row, session);
					row.set(25, result);
					validationResult.add(row);
					if(result != "")
					{
						validated = false;
					}
				}
				ws.writeSheetData(validationResult, 1);
				session.close();
				JOptionPane.showMessageDialog(null, " Validation Done");
			}
		}

		else if(event.getSource().equals(save))
		{
			for(String wsName : wsMap.keySet())
			{
				if(wsName == "Unapproved Values")
				{

					ArrayList<ArrayList<String>> result = wsMap.get(wsName).readSpreadsheet(1);
					int updateFlag = 1;
					/** Team Leader approved and send to QA */
					for(int i = 0; i < result.size(); i++)
					{
						ArrayList<String> newValReq = result.get(i);
						if(newValReq.get(12).equals("Update"))
						{
							try
							{
								if(!validated || (newValReq.get(25) != null && !newValReq.get(25).trim().isEmpty()))
								{
									JOptionPane.showMessageDialog(null, " Validate First due to some errors in your data");
									thread.stop();
									loading.frame.dispose();
									return;
								}
							}catch(Exception e)
							{
								continue;
							}
						}
						if(newValReq.get(12).equals("Approved Eng.") && !newValReq.get(18).equals("Internal"))
						{
							JOptionPane.showMessageDialog(null, " You Can Approved Eng. on Internal Feedback only in row :" + i + 1);
							thread.stop();
							loading.frame.dispose();
							return;
						}
						if(newValReq.get(12).equals("Reject QA") && !newValReq.get(18).equals("QA"))
						{
							JOptionPane.showMessageDialog(null, " You Can Reject QA on QA Feedback only in row :" + i + 1);
							thread.stop();
							loading.frame.dispose();
							return;
						}
						if(newValReq.get(12).equals("Accept QA & Forward") && !newValReq.get(18).equals("QA"))
						{
							JOptionPane.showMessageDialog(null, " You Can Accept QA & Forward on QA Feedback only in row :" + i + 1);
							thread.stop();
							loading.frame.dispose();
							return;
						}
						if(newValReq.get(12).equals("Update") && newValReq.get(19).equals("Wrong Value"))
						{
							JOptionPane.showMessageDialog(null, " You Can't Update Wrong Value Feedback in row :" + i + 1);
							thread.stop();
							loading.frame.dispose();
							return;
						}
						if(newValReq.get(12).equals("Wrong Separation") && !newValReq.get(18).equals("Internal"))
						{
							JOptionPane.showMessageDialog(null, " You Can set Wrong Separation on Internal Feedback only in row :" + i + 1);
							thread.stop();
							loading.frame.dispose();
							return;
						}
						if(newValReq.get(12).equals("Wrong Value") && !newValReq.get(18).equals("Internal"))
						{
							JOptionPane.showMessageDialog(null, " You Can set Wrong Value on Internal Feedback only in row :" + i + 1);
							thread.stop();
							loading.frame.dispose();
							return;
						}

						if(newValReq.get(12).equals("Update") && newValReq.get(18).equals("QA"))
						{
							if(newValReq.get(14).isEmpty() || newValReq.get(15).isEmpty() || newValReq.get(16).isEmpty() || newValReq.get(17).isEmpty())
							{
								JOptionPane.showMessageDialog(null, " You must enter C_Action && P_Action && ROOT_Cause && Action_Due_Date when update in row :" + i + 1);
								thread.stop();
								loading.frame.dispose();
								return;
							}
							if(!newValReq.get(17).isEmpty())
							{
								if(ApprovedDevUtil.isThisDateValid(newValReq.get(17), "dd/MM/yyyy") == false)
								{
									JOptionPane.showMessageDialog(null, " You must enter Action_Due_Date with 'dd/MM/yyyy' fromat in row :" + i + 1);
									thread.stop();
									loading.frame.dispose();
									return;
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
							oldValReq.setCAction(newValReq.get(14));
							oldValReq.setPAction(newValReq.get(15));
							oldValReq.setRootCause(newValReq.get(16));
							oldValReq.setActionDueDate(newValReq.get(17));
							// oldValReq.setIssuedby(issuedto);

							if(newValReq.get(12).equals("Approved Eng."))
							{
								// ParaQueryUtil.saveTLApproved(result.get(i));
								if(oldValReq.getFbType().equals("QA"))
								{
									oldValReq.setIssuedby(TLDTO.getId());
									oldValReq.setIssueTo(oldValReq.getQaUserId());
									oldValReq.setFbStatus(StatusName.accept);
									oldValReq.setGruopSatus(StatusName.qaFeedback);
								}
								else
								{
									oldValReq.setIssuedby(TLDTO.getId());
									oldValReq.setIssueTo(oldValReq.getUserId());
									oldValReq.setFbStatus(StatusName.fbClosed);
									oldValReq.setGruopSatus(StatusName.qaReview);
								}
								ApprovedDevUtil.replyApprovedValueFB(oldValReq);
							}
							else if(newValReq.get(12).equals("Update"))
							{

								if(oldValReq.getFbType().equals("QA"))
								{
									oldValReq.setIssuedby(TLDTO.getId());
									oldValReq.setIssueTo(oldValReq.getQaUserId());
									oldValReq.setFbStatus(StatusName.accept);
									oldValReq.setGruopSatus(StatusName.qaFeedback);
								}
								else
								{
									oldValReq.setIssuedby(TLDTO.getId());
									oldValReq.setIssueTo(oldValReq.getUserId());
									oldValReq.setFbStatus(StatusName.fbClosed);
									oldValReq.setGruopSatus(StatusName.qaReview);
								}
								
								ApprovedDevUtil.updateApprovedValue(updateFlag, oldValReq);
								ApprovedDevUtil.replyApprovedValueFB(oldValReq);

							}
							else if(newValReq.get(12).equals("Reject QA"))
							{
								oldValReq.setIssuedby(TLDTO.getId());
								oldValReq.setIssueTo(oldValReq.getQaUserId());
								oldValReq.setFbStatus(StatusName.reject);
								oldValReq.setGruopSatus(StatusName.qaFeedback);
								ApprovedDevUtil.replyApprovedValueFB(oldValReq);
							}
							else if(newValReq.get(12).equals("Wrong Separation"))
							{
								oldValReq.setIssueType(newValReq.get(12));
								oldValReq.setIssuedby(TLDTO.getId());
								oldValReq.setIssueTo(oldValReq.getUserId());
								oldValReq.setFbStatus(StatusName.reject);
								oldValReq.setGruopSatus(StatusName.engFeedback);
								ApprovedDevUtil.replyApprovedValueFB(oldValReq);
							}
							else if(newValReq.get(12).equals("Accept QA & Forward"))
							{
								oldValReq.setIssueType(newValReq.get(12));
								oldValReq.setIssuedby(TLDTO.getId());
								oldValReq.setIssueTo(oldValReq.getUserId());
								oldValReq.setFbStatus(StatusName.accept);
								oldValReq.setGruopSatus(StatusName.engFeedback);
								ApprovedDevUtil.replyApprovedValueFB(oldValReq);
							}
							else if(newValReq.get(12).equals("Wrong Value"))
							{
								oldValReq.setIssueType(newValReq.get(12));
								oldValReq.setIssuedby(TLDTO.getId());
								oldValReq.setIssueTo(oldValReq.getUserId());
								oldValReq.setFbStatus(StatusName.reject);
								oldValReq.setGruopSatus(StatusName.engFeedback);
								ApprovedDevUtil.replyApprovedValueFB(oldValReq);
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
		thread.stop();
		loading.frame.dispose();
	}

	public void updateFlags(ArrayList<String> flags)
	{
		alertsPanel.updateFlags(flags);

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
}
