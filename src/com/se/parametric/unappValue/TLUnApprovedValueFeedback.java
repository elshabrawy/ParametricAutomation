package com.se.parametric.unappValue;

import java.awt.BorderLayout;
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
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingWorker;

import osheet.SheetPanel;
import osheet.WorkingSheet;

import com.se.automation.db.parametric.StatusName;
import com.se.grm.client.mapping.GrmGroup;
import com.se.grm.client.mapping.GrmRole;
import com.se.parametric.MainWindow;
import com.se.parametric.commonPanel.FilterPanel;
import com.se.parametric.commonPanel.WorkingAreaPanel;
import com.se.parametric.dba.ApprovedDevUtil;
import com.se.parametric.dba.ParaQueryUtil;
import com.se.parametric.dev.PdfLinks;
import com.se.parametric.dto.DocumentInfoDTO;
import com.se.parametric.dto.GrmUserDTO;
import com.se.parametric.dto.UnApprovedDTO;

public class TLUnApprovedValueFeedback extends JPanel implements ActionListener
{

	DocumentInfoDTO documentInfoDTO = null;
	SheetPanel sheetPanel = null;
	WorkingSheet ws;
	PdfLinks pdfLinks = null;
	ArrayList<ArrayList<String>> input = new ArrayList<ArrayList<String>>();
	WorkingAreaPanel selectionPanel;
	JTabbedPane tabbedPane;
	Map<String, WorkingSheet> wsMap = new HashMap<String, WorkingSheet>();
	ArrayList<ArrayList<String>> separationValues = new ArrayList<ArrayList<String>>();
	boolean foundPdf = false;
	FilterPanel filterPanel = null;
	public ArrayList<ArrayList<String>> list;
	Long[] teamMembers = null;
	ArrayList<String> row = null;
	GrmUserDTO TLDTO;
	ArrayList<UnApprovedDTO> unApproveds = new ArrayList<UnApprovedDTO>();;
	boolean validated;

	public TLUnApprovedValueFeedback(GrmUserDTO TLDTO)
	{
		this.TLDTO = TLDTO;
		this.setLayout(new BorderLayout());
		// int width = Toolkit.getDefaultToolkit().getScreenSize().width;
		// int height = Toolkit.getDefaultToolkit().getScreenSize().height;
		teamMembers = ParaQueryUtil.getTeamMembersIDplusTLByTL(TLDTO.getId());
		ArrayList<Object[]> filterData = ApprovedDevUtil.getEngUnapprovedData(TLDTO, null, null,
				"TL");
		// System.out.println("User:" + userDTO.getId() + " " + userDTO.getFullName() + " " + filterData.size());
		selectionPanel = new WorkingAreaPanel(this.TLDTO);
		String[] filterLabels = { "PL Name", "Supplier", "Task Type", "FeedBack Type" };
		filterPanel = selectionPanel.getFilterPanel(filterLabels, filterData, false, this);
		ArrayList<String> buttonLabels = new ArrayList<String>();
		buttonLabels.add("validate");
		buttonLabels.add("Save");
		selectionPanel.addButtonsPanel(buttonLabels, this);
		sheetPanel = selectionPanel.getSheet();
		selectionPanel.addComponentsToPanel();
		add(selectionPanel);

		this.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent arg0)
			{
			}

			@Override
			public void focusGained(FocusEvent arg0)
			{
				selectionPanel.requestFocusInWindow();
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
				unApproveds = ApprovedDevUtil.getUnapprovedReviewData(teamMembers, "", startDate,
						endDate, plName, supplierName, StatusName.tlFeedback, taskType,
						"Parametric", "FB", TLDTO.getId());
				list = new ArrayList<ArrayList<String>>();

				list = new ArrayList<ArrayList<String>>();
				row = new ArrayList<String>();
				sheetPanel.setFocusable(true);
				sheetPanel.openOfficeDoc();
				ws = new WorkingSheet(sheetPanel, "Unapproved Values");
				sheetPanel.saveDoc("C:/Report/Parametric_Auto/" + "Unapproved@"
						+ TLDTO.getFullName() + "@" + System.currentTimeMillis() + ".xls");
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
				row.add("TL Status");// 14
				row.add("TL Comment");// 15

				row.add("C_ACTION");// 16
				row.add("P_ACTION");// 17
				row.add("ROOT_CAUSE");// 18
				row.add("Action Due Date");// 19

				row.add("FeedBack Type");// 20
				row.add("Issue Type");// 21
				row.add("Dev Status");// 22
				row.add("Dev Comment");// 23
				row.add("QA Status");// 24
				row.add("QA Comment");// 25
				row.add("Last TL Comment");// 26
				row.add("Validation Result");// 27
				wsMap.put("Unapproved Values", ws);
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
				ws.writeReviewData(list, 1, 15);
				Robot bot = new Robot();
				bot.mouseMove(1165, 345);
				bot.mousePress(InputEvent.BUTTON1_MASK);
				bot.mouseRelease(InputEvent.BUTTON1_MASK);
				MainWindow.glass.setVisible(false);
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
				filterPanel.filterList = ApprovedDevUtil.getEngUnapprovedData(TLDTO, startDate,
						endDate, "TL");
				filterPanel.refreshFilters();
				MainWindow.glass.setVisible(false);
				filterPanel.setCollapsed(true);
			}

			else if(event.getActionCommand().equals("validate"))
			{
				// tabbedPane.setSelectedIndex(0);
				ArrayList<ArrayList<String>> wsheet = wsMap.get("Unapproved Values")
						.readSpreadsheet(1);
				if(wsheet.isEmpty())
				{
					tabbedPane.setSelectedIndex(1);
					MainWindow.glass.setVisible(false);
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
						row.set(27, result.get(0));
						validationResult.add(row);
						if(result.get(0) != "" && result.get(1).equals("false"))
						{
							validated = false;
						}
					}
					ws.writeSheetData(validationResult, 1);
					// session.close();
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

						ArrayList<ArrayList<String>> result = wsMap.get(wsName).readSpreadsheet(1);
						int updateFlag = 1;
						/** Team Leader approved and send to QA */
						for(int i = 0; i < result.size(); i++)
						{
							ArrayList<String> newValReq = result.get(i);
							if(newValReq.get(14).equals("Update"))
							{
								// try
								// {
								if(!validated)
								{
									MainWindow.glass.setVisible(false);
									JOptionPane.showMessageDialog(null,
											" Validate First due to some errors in your data");

									return null;
								}
								// }catch(Exception e)
								// {
								// continue;
								// }
							}
							// if(newValReq.get(12).equals("Approved Eng.") && !newValReq.get(18).equals("Internal"))
							// {
							// JOptionPane.showMessageDialog(null, " You Can Approved Eng. on Internal Feedback only in row :" + i + 1);
							// thread.stop();
							// loading.frame.dispose();
							// return;
							// }
							if(newValReq.get(14).equals("Reject QA")
									&& !newValReq.get(20).equals("QA"))
							{
								MainWindow.glass.setVisible(false);
								JOptionPane
										.showMessageDialog(null,
												" You Can Reject QA on QA Feedback only in row :"
														+ (i + 1));

								return null;
							}
							if(newValReq.get(14).equals("Accept QA & Forward")
									&& !newValReq.get(20).equals("QA"))
							{
								MainWindow.glass.setVisible(false);
								JOptionPane.showMessageDialog(null,
										" You Can Accept QA & Forward on QA Feedback only in row :"
												+ (i + 1));

								return null;
							}
							if(newValReq.get(14).equals("Update")
									&& newValReq.get(21).equals("Wrong Value"))
							{
								MainWindow.glass.setVisible(false);
								JOptionPane
										.showMessageDialog(null,
												" You Can't Update Wrong Value Feedback in row :"
														+ (i + 1));

								return null;
							}
							if(newValReq.get(14).equals("Wrong Separation")
									&& !newValReq.get(20).equals("Internal"))
							{
								MainWindow.glass.setVisible(false);
								JOptionPane.showMessageDialog(null,
										" You Can set Wrong Separation on Internal Feedback only in row :"
												+ (i + 1));

								return null;
							}
							if(newValReq.get(14).equals("Wrong Value")
									&& !newValReq.get(20).equals("Internal"))
							{
								MainWindow.glass.setVisible(false);
								JOptionPane.showMessageDialog(null,
										" You Can set Wrong Value on Internal Feedback only in row :"
												+ (i + 1));

								return null;
							}

							if(newValReq.get(14).equals("Update") && newValReq.get(20).equals("QA"))
							{
								if(newValReq.get(16).isEmpty() || newValReq.get(17).isEmpty()
										|| newValReq.get(18).isEmpty()
										|| newValReq.get(19).isEmpty())
								{
									MainWindow.glass.setVisible(false);
									JOptionPane.showMessageDialog(null,
											" You must enter C_Action && P_Action && ROOT_Cause && Action_Due_Date when update in row :"
													+ (i + 1));

									return null;
								}
								if(!newValReq.get(19).isEmpty())
								{
									if(ApprovedDevUtil.isThisDateValid(newValReq.get(19),
											"DD/MM/YYYY") == false)
									{
										MainWindow.glass.setVisible(false);
										JOptionPane.showMessageDialog(null,
												" You must enter Action_Due_Date with 'dd/MM/yyyy' fromat in row :"
														+ (i + 1));

										return null;
									}
								}
							}

						}
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
								oldValReq.setComment(newValReq.get(14));

								// oldValReq.setIssuedby(issuedto);

								if(newValReq.get(14).equals("Approved Eng."))
								{

									// ParaQueryUtil.saveTLApproved(result.get(i));
									if(oldValReq.getFbType().equals("QA"))
									{
										oldValReq.setCAction(newValReq.get(16));
										oldValReq.setPAction(newValReq.get(17));
										oldValReq.setRootCause(newValReq.get(18));
										oldValReq.setActionDueDate(newValReq.get(19));
										oldValReq.setIssueTo(oldValReq.getQaUserId());
										oldValReq.setIssuedby(TLDTO.getId());
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
								else if(newValReq.get(14).equals("Update"))
								{
									if(oldValReq.getFbType().equals("QA"))
									{
										oldValReq.setCAction(newValReq.get(16));
										oldValReq.setPAction(newValReq.get(17));
										oldValReq.setRootCause(newValReq.get(18));
										oldValReq.setActionDueDate(newValReq.get(19));

										oldValReq.setIssueTo(oldValReq.getQaUserId());
										oldValReq.setIssuedby(TLDTO.getId());
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
								else if(newValReq.get(14).equals("Reject QA"))
								{
									oldValReq.setIssueTo(oldValReq.getQaUserId());
									oldValReq.setIssuedby(TLDTO.getId());
									oldValReq.setFbStatus(StatusName.reject);
									oldValReq.setGruopSatus(StatusName.qaFeedback);
									ApprovedDevUtil.replyApprovedValueFB(oldValReq);
								}
								else if(newValReq.get(14).equals("Wrong Separation"))
								{
									oldValReq.setIssueType(newValReq.get(14));
									oldValReq.setIssuedby(TLDTO.getId());
									oldValReq.setIssueTo(oldValReq.getUserId());
									oldValReq.setFbStatus(StatusName.reject);
									oldValReq.setGruopSatus(StatusName.engFeedback);
									ApprovedDevUtil.replyApprovedValueFB(oldValReq);
								}
								else if(newValReq.get(14).equals("Accept QA & Forward"))
								{
									oldValReq.setIssueType(newValReq.get(14));
									oldValReq.setIssuedby(TLDTO.getId());
									oldValReq.setIssueTo(oldValReq.getUserId());
									oldValReq.setFbStatus(StatusName.accept);
									oldValReq.setGruopSatus(StatusName.engFeedback);
									ApprovedDevUtil.replyApprovedValueFB(oldValReq);
								}
								else if(newValReq.get(14).equals("Wrong Value"))
								{
									oldValReq.setIssueType(newValReq.get(14));
									oldValReq.setIssuedby(TLDTO.getId());
									oldValReq.setIssueTo(oldValReq.getUserId());
									oldValReq.setFbStatus(StatusName.reject);
									oldValReq.setGruopSatus(StatusName.engFeedback);
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
				JOptionPane.showMessageDialog(null, "Saved Done");
			}
			MainWindow.glass.setVisible(false);
			return null;
		}
	}

}
