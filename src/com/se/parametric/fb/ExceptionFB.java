package com.se.parametric.fb;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingWorker;

import org.hibernate.Session;

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
import com.se.parametric.dba.DataDevQueryUtil;
import com.se.parametric.dto.GrmUserDTO;

public class ExceptionFB extends JPanel implements ActionListener
{

	SheetPanel sheetpanel = new SheetPanel();
	WorkingAreaPanel selectionPanel;
	// JPanel devSheetButtonPanel, separationButtonPanel;
	JTabbedPane tabbedPane;
	ArrayList<ArrayList<String>> input = new ArrayList<ArrayList<String>>();
	FilterPanel filterPanel = null;
	// ButtonsPanel buttonsPanel;
	Long[] users = null;
	WorkingSheet ws = null;
	Map<String, WorkingSheet> wsMap = new HashMap<String, WorkingSheet>();
	String engName = "";
	long userId;
	int width, height;
	GrmUserDTO userDTO;
	// static AlertsPanel alertsPanel, alertsPanel1;
	String checker;
	boolean validated;

	public ExceptionFB(GrmUserDTO userDTO)
	{
		this.setLayout(new BorderLayout());
		this.userDTO = userDTO;
		engName = userDTO.getFullName();
		userId = userDTO.getId();
		width = Toolkit.getDefaultToolkit().getScreenSize().width;
		height = Toolkit.getDefaultToolkit().getScreenSize().height;
		ArrayList<Object[]> filterData = DataDevQueryUtil.getQAexceptionFilterData(userDTO, "Eng");
		System.out.println("User:" + userDTO.getId() + " " + userDTO.getFullName() + " "
				+ filterData.size());
		selectionPanel = new WorkingAreaPanel(this.userDTO);
		String[] filterLabels = { "PL Name", "Supplier", "Checker Type" };
		filterPanel = selectionPanel.getFilterPanel(filterLabels, filterData, false, this);

		ArrayList<String> buttonLabels = new ArrayList<String>();
		buttonLabels.add("Save");
		selectionPanel.addButtonsPanel(buttonLabels, this);

		selectionPanel.addComponentsToPanel();
		sheetpanel = selectionPanel.getSheet();
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.addTab("Input Selection", null, selectionPanel, null);
		add(tabbedPane);
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
			// String status = filterPanel.comboBoxItems[3].getSelectedItem().toString();
			if(checkerType.equals("All"))
			{
				MainWindow.glass.setVisible(false);
				JOptionPane.showMessageDialog(null, "You must select checker type");
				return;
			}
			checker = checkerType;
			tabbedPane.setSelectedIndex(0);
			sheetpanel.openOfficeDoc();
			ArrayList<QAChecksDTO> reviewData = DataDevQueryUtil.getQAexceptionData(plName,
					supplierName, checkerType, startDate, endDate, userDTO.getId(), "Eng", session);
			wsMap.clear();
			ws = new WorkingSheet(sheetpanel, "QAChecks");
			sheetpanel.saveDoc("C:/Report/" + "QAChecks by " + userDTO.getFullName() + "@"
					+ System.currentTimeMillis() + ".xls");
			wsMap.put("QAChecks", ws);
			ws.setqaexceptionheader(checkerType, "DD");
			ArrayList<String> sheetHeader = ws.getHeader();
			int statusindx = sheetHeader.indexOf("DDStatus");
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
				row.add("");
				row.add("");
				row.add(DataDevQueryUtil.getFeedbackCommentByComId(reviewData.get(i).getPart()
						.getComId()));
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
		frame.setTitle("QA Checks Exception");
		GrmUserDTO uDTO = new GrmUserDTO();
		// uDTO.setId(47);
		// uDTO.setFullName("mohamad mostafa");
		// uDTO.setId(32);
		// uDTO.setFullName("Hatem Hussien");
		uDTO.setId(376);
		uDTO.setFullName("salah_shiha");
		// uDTO.setId(121);
		// uDTO.setFullName("Ahmad_rahim");
		GrmRole role = new GrmRole();
		role.setId(3l);
		GrmGroup group = new GrmGroup();
		group.setId(1l);
		uDTO.setGrmRole(role);
		uDTO.setGrmGroup(group);
		ExceptionFB devPanel = new ExceptionFB(uDTO);
		frame.getContentPane().add(devPanel);
		frame.show();
		// while(true)
		// {
		// ArrayList<String> flags = ParaQueryUtil.getAlerts(uDTO.getId(), 101, 3);
		// // devPanel.updateFlags(flags);
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

	public void updateFlags()
	{
		selectionPanel.updateFlags();
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

				filterPanel.filterList = DataDevQueryUtil.getQAexceptionFilterData(userDTO, "Eng");
				filterPanel.refreshFilters();
				filterPanel.setCollapsed(true);

			}
			else if(event.getActionCommand().equals("Save"))
			{
				System.out.println("~~~~~~~ Start saving Data ~~~~~~~");

				wsMap.keySet();
				for(String wsName : wsMap.keySet())
				{
					if(wsName == "QAChecks")
					{
						if(!wsMap.get(wsName).saved)
						{
							wsMap.get(wsName).saveQAexceptionAction(checker, engName, "DD");
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
	}
}
