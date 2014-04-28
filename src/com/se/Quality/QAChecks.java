package com.se.Quality;

import java.awt.Color;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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

import com.se.automation.db.client.dto.QAChecksDTO;
import com.se.automation.db.client.mapping.Document;
import com.se.grm.client.mapping.GrmGroup;
import com.se.grm.client.mapping.GrmRole;
import com.se.parametric.Loading;
import com.se.parametric.commonPanel.AlertsPanel;
import com.se.parametric.commonPanel.ButtonsPanel;
import com.se.parametric.commonPanel.FilterPanel;
import com.se.parametric.dba.DataDevQueryUtil;
import com.se.parametric.dba.ParaQueryUtil;
import com.se.parametric.dto.GrmUserDTO;
import com.se.parametric.dto.TableInfoDTO;
import com.se.parametric.util.StatusName;

public class QAChecks extends JPanel implements ActionListener
{

	SheetPanel sheetpanel = new SheetPanel();
	SheetPanel separationPanel = new SheetPanel();
	JPanel tabSheet, selectionPanel;
	JPanel devSheetButtonPanel, separationButtonPanel;
	JTabbedPane tabbedPane;
	ArrayList<ArrayList<String>> input = new ArrayList<ArrayList<String>>();
	ArrayList<ArrayList<String>> separationValues = new ArrayList<ArrayList<String>>();
	JButton save;
	JButton Validate;
	FilterPanel filterPanel = null;
	ButtonsPanel buttonsPanel;
	Long[] users = null;
	WorkingSheet ws = null;
	Map<String, WorkingSheet> wsMap = new HashMap<String, WorkingSheet>();
	String engName = "";
	long userId;
	int width, height;
	GrmUserDTO userDTO;
	static AlertsPanel alertsPanel, alertsPanel1;

	public QAChecks(GrmUserDTO userDTO)
	{
		setLayout(null);
		this.userDTO = userDTO;
		engName = userDTO.getFullName();
		userId = userDTO.getId();
		width = Toolkit.getDefaultToolkit().getScreenSize().width;
		height = Toolkit.getDefaultToolkit().getScreenSize().height;
		ArrayList<Object[]> filterData = DataDevQueryUtil.getQAchecksFilterData(userDTO);
		System.out.println("User:" + userDTO.getId() + " " + userDTO.getFullName() + " " + filterData.size());
		selectionPanel = new JPanel();

		String[] filterLabels = { "PL Name", "Supplier", "Checker Type", "Status" };
		filterPanel = new FilterPanel(filterLabels, filterData, width - 120, (((height - 100) * 3) / 10), false);
		filterPanel.setBounds(0, 0, width - 120, (((height - 100) * 3) / 10));
		ArrayList<String> buttonLabels = new ArrayList<String>();
		buttonLabels.add("Save");
		buttonLabels.add("Seperation");
		buttonsPanel = new ButtonsPanel(buttonLabels);
		JButton buttons[] = buttonsPanel.getButtons();
		for(int i = 0; i < buttons.length; i++)
		{
			buttons[i].addActionListener(this);
		}
		buttonsPanel.setBounds(width - 120, 0, 110, height / 3);
		alertsPanel = new AlertsPanel(userDTO);
		alertsPanel1 = new AlertsPanel(userDTO);
		alertsPanel.setBounds(width - 120, height / 3, 110, height * 3 / 4);
		alertsPanel1.setBounds(width - 120, height / 3, 110, height * 3 / 4);
		selectionPanel.setLayout(null);
		selectionPanel.add(filterPanel);
		selectionPanel.add(buttonsPanel);
		selectionPanel.add(alertsPanel);

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(0, 0, width, height - 100);
		tabSheet = new JPanel();
		devSheetButtonPanel = new JPanel();
		devSheetButtonPanel.setBackground(new Color(211, 211, 211));
		devSheetButtonPanel.setBorder(new SoftBevelBorder(BevelBorder.RAISED, null, null, null, null));
		devSheetButtonPanel.setBounds(width - 120, 0, 110, height / 3);
		devSheetButtonPanel.setLayout(null);

		tabSheet.setLayout(null);
		sheetpanel.setBounds(0, (((height - 100) * 3) / 10), width - 120, height - (((height - 100) * 3) / 10) - 130);
		tabSheet.add(sheetpanel);
		tabSheet.add(devSheetButtonPanel);
		tabSheet.add(alertsPanel1);

		tabbedPane.addTab("Input Selection", null, selectionPanel, null);
		tabbedPane.addTab("Seperation", null, tabSheet, null);
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
		}
		else if(event.getSource() == filterPanel.refreshButton)
		{

			filterPanel.filterList = DataDevQueryUtil.getQAchecksFilterData(userDTO);
			filterPanel.refreshFilters();

		}
		else if(event.getActionCommand().equals("Save"))
		{

		}
		else if(event.getActionCommand().equals("Seperation"))
		{

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
					// wsMap.get(wsName).saveQAReviewAction(QAName, "FB", false);
				}
			}
		}
		else if(event.getSource() == Validate)
		{
			System.out.println("~~~~~~~ Start validation Data ~~~~~~~");
			wsMap.keySet();
			for(String wsName : wsMap.keySet())
			{
				if(wsName != "LoadAllData" && wsName != "Separation")
				{
					// wsMap.get(wsName).validateQAReview();
				}
			}
		}

		thread.stop();
		loading.frame.dispose();
	}

	private void loadpdf(int[] selectedPdfs) throws Exception
	{
		JComboBox[] combos = filterPanel.comboBoxItems;

		String plName = combos[0].getSelectedItem().toString();
		String supplierName = combos[1].getSelectedItem().toString();
		String taskType = combos[2].getSelectedItem().toString();
		String userName = combos[3].getSelectedItem().toString();
		String pltype = combos[4].getSelectedItem().toString();

		wsMap.clear();
		TableInfoDTO docInfoDTO = null;
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
			// teamMembers = ParaQueryUtil.getTeamMembersIDByTL(userId);
			ComboBoxModel model = filterPanel.comboBoxItems[3].getModel();
			int size = model.getSize();
			users = new Long[size - 1];
			for(int i = 1; i < size; i++)
			{
				Object element = model.getElementAt(i);
				if(element != null && !element.equals("All"))
					users[i - 1] = ParaQueryUtil.getUserIdByExactName((String) element);
				// System.out.println("Element at " + i + " = " + element);
			}
		}

		Map<String, ArrayList<ArrayList<String>>> reviewData = DataDevQueryUtil.getQAPDFData(users, plName, supplierName, taskType, startDate, endDate, new Long[] { document.getId() }, userDTO.getId(), StatusName.qaFeedback, pltype);

		int k = 0;
		tabbedPane.setSelectedIndex(1);
		sheetpanel.openOfficeDoc();

		for(String pl : reviewData.keySet())
		{

			ws = new WorkingSheet(sheetpanel, pl, k);
			sheetpanel.saveDoc("C:/Report/" + pdfUrl.replaceAll(".*/", "") + "@" + System.currentTimeMillis() + ".xls");
			wsMap.put(pl, ws);
			if(docInfoDTO.getTaskType().contains("NPI"))
				ws.setNPIflag(true);
			ws.setQAReviewHeader(Arrays.asList("Old Flag", "Status", "Wrong Feature", "Comment", "Issued By", "TL Status", "TLComment", "Validation Comment"), true);
			ArrayList<String> sheetHeader = ws.getHeader();
			int oldflagindex = sheetHeader.indexOf("Old Flag");
			int partIndex = sheetHeader.indexOf("Part Number");
			int WrongFeatureIndex = sheetHeader.indexOf("Wrong Feature");
			int ComidIndex = sheetHeader.indexOf("Comid");
			int tlstatusIndex = sheetHeader.indexOf("TL Status");
			int tlCommentIndex = sheetHeader.indexOf("TLComment");
			int supplierIndex = sheetHeader.indexOf("Supplier Name");
			int sentBYIndex = sheetHeader.indexOf("Issued By");
			int statusIndex = sheetHeader.indexOf("Status");
			ArrayList<ArrayList<String>> plData = reviewData.get(pl);

			for(int j = 0; j < plData.size(); j++)
			{
				ArrayList<String> sheetRecord = plData.get(j);
				String partNumber = sheetRecord.get(partIndex);
				supplierName = sheetRecord.get(supplierIndex);
				String qaflag = DataDevQueryUtil.getqaflagbycomid(sheetRecord.get(ComidIndex));
				// String comment = DataDevQueryUtil.getfbcommentbycompartanduser(sheetRecord.get(partIndex).toString(), userDTO.getId());
				// sheetRecord.set(CommentIndex, comment);
				String wrongfeatures = DataDevQueryUtil.getfbwrongfets(Long.valueOf(sheetRecord.get(ComidIndex)), userDTO.getId());
				ArrayList<String> feedCom = DataDevQueryUtil.getFeedbackByPartAndSupp(partNumber, supplierName);
				for(int l = 0; l < 8; l++)
				{
					sheetRecord.add("");
				}
				sheetRecord.set(sentBYIndex, feedCom.get(1));
				sheetRecord.set(WrongFeatureIndex, wrongfeatures);
				sheetRecord.set(oldflagindex, qaflag);
				sheetRecord.set(tlCommentIndex, feedCom.get(0));
				sheetRecord.set(tlstatusIndex, feedCom.get(6));

				plData.set(j, sheetRecord);
			}
			ws.writeReviewData(plData, 2, statusIndex + 1);
			k++;
		}
		// tablePanel.loadedPdfs.add(pdfUrl);
		// tablePanel.setTableData1(0, tablePanel.selectedData);
	}

	private void dofilter(Date startDate, Date endDate) throws Exception
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
		// tabbedPane.setSelectedIndex(1);
		sheetpanel.openOfficeDoc();
		ArrayList<QAChecksDTO> reviewData = DataDevQueryUtil.getQAchecksData(plName, supplierName, checkerType, status);

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
		uDTO.setId(112);
		uDTO.setFullName("Amira Shaalan");
		// uDTO.setId(121);
		// uDTO.setFullName("Ahmad_rahim");
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

	public void updateFlags(ArrayList<String> flags)
	{
		alertsPanel.updateFlags(flags);
		alertsPanel1.updateFlags(flags);
		// alertsPanel2.updateFlags(flags);

	}
}
