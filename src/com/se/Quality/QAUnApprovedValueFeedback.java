package com.se.Quality;

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

import osheet.SheetPanel;
import osheet.WorkingSheet;

import com.se.grm.client.mapping.GrmGroup;
import com.se.grm.client.mapping.GrmRole;
import com.se.parametric.Loading;
import com.se.parametric.commonPanel.AlertsPanel;
import com.se.parametric.commonPanel.ButtonsPanel;
import com.se.parametric.commonPanel.FilterPanel;
import com.se.parametric.dba.ApprovedDevUtil;
import com.se.parametric.dba.DataDevQueryUtil;
import com.se.parametric.dba.ParaQueryUtil;
import com.se.parametric.dev.PdfLinks;
import com.se.parametric.dto.DocumentInfoDTO;
import com.se.parametric.dto.GrmUserDTO;
import com.se.parametric.dto.TableInfoDTO;
import com.se.parametric.dto.UnApprovedDTO;
import com.se.parametric.unappValue.TLUnApprovedValueFeedback;
import com.se.parametric.util.StatusName;

public class QAUnApprovedValueFeedback extends JPanel implements ActionListener
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
	JButton save, validate, separation, feedbackHistory;
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
	public static AlertsPanel alertsPanel;

	public QAUnApprovedValueFeedback(GrmUserDTO userDTO)
	{
		this.userDTO = userDTO;
		setLayout(null);
		int width = Toolkit.getDefaultToolkit().getScreenSize().width;
		int height = Toolkit.getDefaultToolkit().getScreenSize().height;

		// ArrayList<Object[]> filterData = ParaQueryUtil.getUnapprovedReviewData(new Long[]{userDTO.getId()},null,null,"QA");
		ArrayList<Object[]> filterData = ApprovedDevUtil.getEngUnapprovedData(userDTO, null, null, "QA");
		// System.out.println("User:" + userDTO.getId() + " " + userDTO.getFullName() + " " + filterData.size());
		selectionPanel = new JPanel();
		String[] filterLabels = { "PL Name", "Supplier", "Task Type", "FeedBack Type" };
		sheetPanel = new SheetPanel();
		sheetPanel.setSize(width - 110, (((height - 100) * 6) / 10));
		sheetPanel.setBounds(0, (((height - 100) * 3) / 10), width - 110, (((height - 100) * 7) / 10) - 30);
		// filterPanel.setBounds(0, 0, width - 110, (((height - 100) * 4) / 10));
		filterPanel = new FilterPanel(filterLabels, filterData, width - 110, (((height - 100) * 3) / 10));
		filterPanel.setBounds(0, 0, width - 110, (((height - 100) * 3) / 10));
		// ArrayList<String> buttonLabels = new ArrayList<String>();
		// buttonLabels.add("Save");
		// buttonLabels.add("Feedback History");
		// buttonsPanel = new ButtonsPanel(buttonLabels);
		// JButton buttons[] = buttonsPanel.getButtons();
		// for(int i = 0; i < buttons.length; i++)
		// {
		// buttons[i].addActionListener(this);
		// }
		// buttonsPanel.setBounds(width - 120, 0, 108, height / 3);
		alertsPanel = new AlertsPanel(userDTO);
		alertsPanel.setBounds(width - 120, height / 3, 110, height * 3 / 4);
		selectionPanel.setLayout(null);
		selectionPanel.add(filterPanel);
		selectionPanel.add(sheetPanel);
		// selectionPanel.add(buttonsPanel);
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
		feedbackHistory = new JButton("Feedback History");
		feedbackHistory.setBounds(3, 45, 85, 29);
		feedbackHistory.setForeground(new Color(25, 25, 112));
		feedbackHistory.setFont(new Font("Tahoma", Font.BOLD, 11));

		save.setBounds(3, 11, 85, 29);
		save.setForeground(new Color(25, 25, 112));
		save.setFont(new Font("Tahoma", Font.BOLD, 11));
		save.addActionListener(this);
		feedbackHistory.addActionListener(this);
		feedbackHistory.setEnabled(false);
		devSheetButtonPanel.add(save);
		devSheetButtonPanel.add(feedbackHistory);
		// devSheetButtonPanel.add();
		devSheetButtonPanel.setBackground(new Color(211, 211, 211));
		add(devSheetButtonPanel);
		add(selectionPanel);
		add(alertsPanel);
	}

	@Override
	public void actionPerformed(ActionEvent event)
	{
		// Loading loading = new Loading();
		WorkingSheet ws = null;
		// Thread thread = new Thread(loading);
		// thread.start();
		UnApprovedDTO obj = null;
		String statuses[];

		if(event.getSource().equals(filterPanel.filterButton))
		{
			feedbackHistory.setEnabled(true);
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
			unApproveds = ApprovedDevUtil.getUnapprovedReviewData(new Long[] { userDTO.getId() }, "", startDate, endDate, plName, supplierName, StatusName.qaFeedback, taskType, "QA", "FB", userDTO.getId());
			list = new ArrayList<ArrayList<String>>();

			list = new ArrayList<ArrayList<String>>();
			row = new ArrayList<String>();
			sheetPanel.openOfficeDoc();

			// ws = new WorkingSheet(new SheetPanel(), "Feedback Values",1);
			ws = new WorkingSheet(sheetPanel, "Unapproved Values", 0, true);

			// sheetPanel.add(arg0)
			sheetPanel.saveDoc("C:/Report/Parametric_Auto/" + "Unapproved@" + userDTO.getFullName() + "@" + System.currentTimeMillis() + ".xls");
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
			row.add("QA Status");
			row.add("QA Comment");
			row.add("c_Action");
			row.add("P_Action");
			row.add("ROOT_CAUSE");
			row.add("ACTION_DUE_DATE");
			row.add("FeedBack Type");
			row.add("Para Status");
			row.add("Para Comment");
			row.add("Old QA Status");
			row.add("Old QA Comment");

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
			ArrayList<String> commentValues = new ArrayList<String>();
			commentValues.add(StatusName.approved);
			commentValues.add(StatusName.reject);
			ws.commentValues = commentValues;
			ws.writeReviewData(list, 1, 13);
			// filterPanel.jDateChooser1.setDate(new Date(System.currentTimeMillis()));
			// filterPanel.jDateChooser2.setDate(new Date(System.currentTimeMillis()));

		}
		else if(event.getActionCommand().equals("Feedback History"))
		{
			ws = new WorkingSheet(sheetPanel, "Feedback", 1, true);
			wsMap.put("Feedback", ws);
			ws = wsMap.get("Feedback");
			try
			{
				// sheetPanel.getSelectedXCell();
				String url = sheetPanel.getCellText(sheetPanel.getCellByPosission(1, 1)).toString();
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
			filterPanel.filterList = ApprovedDevUtil.getEngUnapprovedData(userDTO, startDate, endDate, "QA");
			filterPanel.refreshFilters();
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
						if(newValReq.get(12).equals("Approved") && (!newValReq.get(13).equals(StatusName.approved) && !newValReq.get(13).equals(StatusName.reject)))
						{
							JOptionPane.showMessageDialog(null, " Comment Must be in (" + StatusName.approved + " and " + StatusName.reject + " ) at Row :" +(i + 1));
							return;
						}
					}
					/** Team Leader approved and send to QA */
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
							oldValReq.setFbStatus(StatusName.reject);
							oldValReq.setGruopSatus(StatusName.tlFeedback);
							oldValReq.setComment(newValReq.get(13));
							long issuedto = oldValReq.getIssuedby();
							oldValReq.setIssuedby(userDTO.getId());
							oldValReq.setIssueTo(issuedto);
							oldValReq.setFbType("QA");

							if(newValReq.get(12).equals("Approved"))
							{
								oldValReq.setFbStatus(StatusName.fbClosed);
								oldValReq.setGruopSatus(oldValReq.getComment());
								ApprovedDevUtil.setValueApproved(result.get(i), StatusName.approved);
								ApprovedDevUtil.replyApprovedValueFB(oldValReq);
							}

							else if(newValReq.get(12).equals("Wrong Value"))
							{
								oldValReq.setIssueType(newValReq.get(12));
								ApprovedDevUtil.replyApprovedValueFB(oldValReq);

							}
							else if(newValReq.get(12).equals("Wrong Separation"))
							{
								oldValReq.setIssueType(newValReq.get(12));
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
			JOptionPane.showMessageDialog(null, "Save Done");
		}
		// thread.stop();
		// loading.frame.dispose();
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