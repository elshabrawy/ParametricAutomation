package com.se.parametric.unappValue;

import java.awt.Color;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
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
import com.se.parametric.Loading;
import com.se.parametric.MainWindow;
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

/**
 * created by Ahmed Makram
 * */
public class TLUnApprovedValue extends JPanel implements ActionListener
{

	/**
	 * Create the panel.
	 */
	DocumentInfoDTO documentInfoDTO = null;
	SheetPanel sheetPanel = new SheetPanel();
	WorkingSheet ws;
	PdfLinks pdfLinks = null;
	ArrayList<ArrayList<String>> input = new ArrayList<ArrayList<String>>();
	JPanel tabSheet, selectionPanel/* , flowChart */;
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
	ArrayList<UnApprovedDTO> unApproveds;
	TLUnApprovedValueFeedback TLfeedBack = null;
	static AlertsPanel alertsPanel;
	boolean validated;

	public TLUnApprovedValue(GrmUserDTO userDTO)
	{
		this.userDTO = userDTO;
		setLayout(null);
		int width = Toolkit.getDefaultToolkit().getScreenSize().width;
		int height = Toolkit.getDefaultToolkit().getScreenSize().height;
		teamMembers = ParaQueryUtil.getTeamMembersIDByTL(userDTO.getId());
		ArrayList<Object[]> filterData = ApprovedDevUtil.getUnapprovedReviewFilter(teamMembers,
				null, null, "parametric");
		System.out.println("User:" + userDTO.getId() + " " + userDTO.getFullName() + " "
				+ filterData.size());
		selectionPanel = new JPanel();
		String[] filterLabels = { "Eng Name", "PL Name", "Supplier", "Status", "Task Type" };

		// sheetPanel.setSize(width - 110, (((height - 100) * 7) / 10));
		sheetPanel.setBounds(0, (((height - 100) * 3) / 10), width - 120, height
				- (((height - 100) * 3) / 10) - 130);
		// filterPanel = new FilterPanel(filterLabels, filterData, width - 110, (((height - 100) * 4) / 10));
		// filterPanel.setBounds(0, 0, width - 110, (((height - 100) * 4) / 10));
		filterPanel = new FilterPanel(filterLabels, filterData, width - 120,
				(((height - 100) * 3) / 10), false);
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
		alertsPanel.setBounds(width - 120, height / 3, 110, height * 3 / 4);
		selectionPanel.setLayout(null);
		selectionPanel.add(filterPanel);
		selectionPanel.add(sheetPanel);
		selectionPanel.add(buttonsPanel);
		selectionPanel.add(alertsPanel);
		selectionPanel.setBounds(0, 0, width - 110, height - 100);
		filterPanel.filterButton.addActionListener(this);
		filterPanel.refreshButton.addActionListener(this);
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(0, 0, width, height - 100);
		TLfeedBack = new TLUnApprovedValueFeedback(userDTO);
		devSheetButtonPanel = new JPanel();
		devSheetButtonPanel.setBackground(new Color(211, 211, 211));
		devSheetButtonPanel.setBorder(new SoftBevelBorder(BevelBorder.RAISED, null, null, null,
				null));
		devSheetButtonPanel.setBounds(width - 110, 0, 106, height - 100);
		TLfeedBack.setBounds(width - 110, 0, 106, height - 100);
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

		add(tabbedPane);
		tabbedPane.addTab("TL UnApproved Review", null, selectionPanel, null);
		tabbedPane.addTab("TL UnApproved FeedBack", null, TLfeedBack, null);
		// flowChart = new ImagePanel("QASeparation.jpg");
		// tabbedPane.addTab("Separation Flow", null, flowChart, null);
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
		TLUnApprovedValueFeedback.alertsPanel.updateFlags(flags);

	}

	public static void main(String[] args)
	{
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		int width = Toolkit.getDefaultToolkit().getScreenSize().width;
		int height = Toolkit.getDefaultToolkit().getScreenSize().height;
		frame.setSize(width, height);
		frame.setTitle("TL UnApproved Values");
		GrmUserDTO uDTO = new GrmUserDTO();
		// uDTO.setId(147);
		// uDTO.setFullName("sameh_soliman");
		uDTO.setId(46);
		uDTO.setFullName("Ahmed Rizk");
		// uDTO.setId(116);
		// uDTO.setFullName("a_kamal");
		GrmRole role = new GrmRole();
		role.setId(1l);
		GrmGroup group = new GrmGroup();
		group.setId(1l);
		uDTO.setGrmRole(role);
		uDTO.setGrmGroup(group);
		TLUnApprovedValue devPanel = new TLUnApprovedValue(uDTO);
		frame.getContentPane().add(devPanel);
		frame.show();
		// while(true)
		// {
		// ArrayList<String> flags = ParaQueryUtil.getAlerts(uDTO.getId(), 1, 1);
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

				unApproveds = ApprovedDevUtil.getUnapprovedReviewData(teamMembers, engName,
						startDate, endDate, plName, supplierName, StatusName.tlReview, taskType,
						"Parametric", "Data", 0l);
				// unApproveds = ParaQueryUtil.getTLUnapprovedData(startDate, endDate, teamMembers, engName, plName, supplierName, status, taskType);
				list = new ArrayList<ArrayList<String>>();
				row = new ArrayList<String>();
				sheetPanel.setFocusable(true);
				sheetPanel.openOfficeDoc();
				ws = new WorkingSheet(sheetPanel, "Unapproved Values");
				sheetPanel.saveDoc("C:/Report/Parametric_Auto/" + "Unapparoved@"
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
				row.add("TL Approved Status");
				row.add("TL Approved Comment");
				row.add("Validation Result");// 14
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
				statusValues.add("Update");
				statusValues.add("Wrong Value");
				statusValues.add("Wrong Separation");
				ws.statusValues = statusValues;
				ws.writeReviewData(list, 1, 13);
				// filterPanel.jDateChooser1.setDate(new Date(System.currentTimeMillis()));
				// filterPanel.jDateChooser2.setDate(new Date(System.currentTimeMillis()));
				// session.close();
				MainWindow.glass.setVisible(false);
				Robot bot = new Robot();
				bot.mouseMove(1165, 345);
				bot.mousePress(InputEvent.BUTTON1_MASK);
				bot.mouseRelease(InputEvent.BUTTON1_MASK);
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
				filterPanel.filterList = ApprovedDevUtil.getUnapprovedReviewFilter(teamMembers,
						startDate, endDate, "parametric");
				filterPanel.refreshFilters();
				MainWindow.glass.setVisible(false);
			}

			else if(event.getActionCommand().equals(" validate "))
			{
				// tabbedPane.setSelectedIndex(0);
				ArrayList<ArrayList<String>> wsheet = wsMap.get("Unapproved Values")
						.readSpreadsheet(1);
				if(wsheet.isEmpty())
				{
					MainWindow.glass.setVisible(false);
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
						row.set(14, result.get(0));
						validationResult.add(row);
						if(result.get(0) != "" && result.get(1).equals("false"))
						{
							validated = false;
						}
					}
					ws.writeSheetData(validationResult, 1);
					MainWindow.glass.setVisible(false);
					JOptionPane.showMessageDialog(null, " Validation Done");
				}
			}

			else if(event.getActionCommand().equals("Save"))
			{
				for(String wsName : wsMap.keySet())
				{
					if(wsName == "Unapproved Values")
					{
						if(!wsMap.get(wsName).saved)
						{

							ArrayList<ArrayList<String>> result = wsMap.get(wsName)
									.readSpreadsheet(1);
							int updateFlag = 1;
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
											MainWindow.glass.setVisible(false);
											JOptionPane
													.showMessageDialog(null,
															" Validate First due to some errors in your data");

											return null;
										}
									}catch(Exception e)
									{
										continue;
									}
								}
							}
							for(int i = 0; i < result.size(); i++)
							{
								ArrayList<String> newValReq = result.get(i);
								UnApprovedDTO oldValReq = unApproveds.get(i);
								// long devUser = unApproveds.get(i).getUserId();
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
									oldValReq.setGruopSatus(StatusName.engFeedback);
									oldValReq.setComment(newValReq.get(13));
									oldValReq.setIssuedby(userDTO.getId());
									oldValReq.setFbType(StatusName.internal);
									oldValReq.setIssueType(newValReq.get(12));
									if(newValReq.get(12).equals("Approved"))
									{
										ApprovedDevUtil.setValueApproved(result.get(i),
												StatusName.qaReview);
									}
									else if(newValReq.get(12).equals("Update"))
									{
										ApprovedDevUtil.updateApprovedValue(updateFlag, oldValReq);
									}
									else if(newValReq.get(12).equals("Wrong Value"))
									{
										ApprovedDevUtil.saveWrongSeparation(oldValReq);
									}
									else if(newValReq.get(12).equals("Wrong Separation"))
									{
										ApprovedDevUtil.saveWrongSeparation(oldValReq);
									}
								}
								else
								{
									MainWindow.glass.setVisible(false);
									JOptionPane.showMessageDialog(null, newValReq.get(0) + " @ "
											+ newValReq.get(4)
											+ " Can't Save dueto change in main columns");
								}
							}

							System.out.println("size is " + result.size());
						}
						else
						{
							MainWindow.glass.setVisible(false);
							JOptionPane.showMessageDialog(null, "This Sheet Saved Before.");
							return null;
						}
					}
					wsMap.get(wsName).saved = true;
				}

				MainWindow.glass.setVisible(false);
				JOptionPane.showMessageDialog(null, "Save Done");
			}
			MainWindow.glass.setVisible(false);
			return null;
		}
	}

}
