package com.se.parametric.dev;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.DefaultCellEditor;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import com.se.parametric.Loading;
import com.se.parametric.dba.ParaQueryUtil;
import com.se.parametric.dto.DocumentInfoDTO;
import com.se.parametric.dto.GrmUserDTO;
import com.toedter.calendar.JDateChooser;
import java.awt.Font;

public class PdfLinks extends JPanel implements ActionListener {
	TableColumn urlColumn;
	public JTable table;
	JLabel label;
	JTextField pdfLink;
	/**
	 * Create the panel.
	 */
	TableColumn PLColumn, supplierColumn, statusColumn, countColumn, extractedColumn;
	JTextField pl = new JTextField();
	JCheckBox checkDate = null;
	JTextField supplier = new JTextField();
	JTextField status = new JTextField();
	JTextField count = new JTextField();
	private int numberOfRows;
	public int currentPage, pageNumber;
	boolean selectDate = false;
	JButton button_1 = new JButton("");
	private JButton first;
	JButton previous = new JButton("");
	JButton next = new JButton("");
	JButton last = new JButton("");
	JPanel PaggingPanel;
	JPanel scrollPanePanel;
	JPanel loadButtonsPanel;
	JPanel datePanel;
	JLabel paggingLabel;
	public String[] select;
	private int recordNumber;
	JComboBox plCombo, supplierCombo, statusCombo, extracted;
	public JComboBox[] comboBoxItems = null;
	JDateChooser jDateChooser1 = new JDateChooser();
	JDateChooser jDateChooser2 = new JDateChooser();
	public ArrayList<ArrayList<String>> list;

	static ArrayList<DocumentInfoDTO> filteredData = new ArrayList<DocumentInfoDTO>();
	public JButton loadDevSheetBtn;
	public JButton loadAllDataBtn;
	JButton doFilterBtn;
	public long userID;
	Date startDate = null;
	Date endDate = null;
	private String PLName;

	public String getPLName() {
		return PLName;
	}

	public void setPLName(String pLName) {
		PLName = pLName;
	}

	public PdfLinks(GrmUserDTO userDTO, ArrayList<ArrayList<String>> list) {
		int width = Toolkit.getDefaultToolkit().getScreenSize().width;
		int height = Toolkit.getDefaultToolkit().getScreenSize().height;
		this.list = list;
		this.userID = userDTO.getId();
		this.setSize(width, height - 100);
		comboBoxItems = new JComboBox[4];
		paggingLabel = new JLabel("");
		if (list.size() % 20 == 0) {
			this.setPageNumber(list.size() / 20);
		} else {
			this.setPageNumber((list.size() / 20) + 1);
		}
		System.out.println("number of page " + this.getPageNumber());
		setLayout(null);
		this.setCurrentPage(1);
		paggingLabel.setText("1");
		ArrayList<Object[]> result = getDistinct(list);
		if (!list.isEmpty()) {
			select = new String[list.get(0).size()];
			for (int i = 0; i < list.get(0).size(); i++) {
				select[i] = "All";
			}
		}

		Object[][] data = new Object[20][6];
		table = new JTable();
		table.setRowHeight(25);
		table.setModel(new DefaultTableModel(data, new String[] { "PDF URL", "PLName", "Supplier", "Status", "Extracted", "Date" }));
		table.getColumnModel().getColumn(0).setPreferredWidth(150);
		table.getColumnModel().getColumn(1).setPreferredWidth(60);
		table.getColumnModel().getColumn(2).setPreferredWidth(60);
		table.getColumnModel().getColumn(3).setPreferredWidth(61);
		table.getColumnModel().getColumn(4).setPreferredWidth(60);
		table.getColumnModel().getColumn(5).setPreferredWidth(60);
		table.setBounds(0, 524, width - 120, ((height - 200) * 70) / 100);
		table.getTableHeader().setReorderingAllowed(true);
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(0, 0, width - 120, ((height - 200) * 70) / 100);
		scrollPane.setViewportView(table);
		datePanel = new JPanel();
		datePanel.setBackground(new Color(255, 240, 245));
		datePanel.setBorder(new LineBorder(new Color(0, 0, 0)));
		datePanel.setBounds(0, 0, width - 120, 60);
		add(datePanel);
		jDateChooser1.setBounds(232, 21, 91, 20);
		jDateChooser1.setDate(new java.util.Date());
		jDateChooser2.setBounds(473, 21, 91, 20);
		jDateChooser2.setDate(new java.util.Date());
		datePanel.setLayout(null);
		jDateChooser1.setEnabled(false);
		jDateChooser2.setEnabled(false);
		datePanel.add(jDateChooser1);
		datePanel.add(jDateChooser2);
		JLabel lblNewLabel = new JLabel("Date From");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblNewLabel.setBounds(118, 27, 73, 14);
		datePanel.add(lblNewLabel);
		JLabel lblNewLabel_1 = new JLabel("Date To");
		lblNewLabel_1.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblNewLabel_1.setBounds(379, 27, 46, 14);
		datePanel.add(lblNewLabel_1);
		checkDate = new JCheckBox("Select Period");
		checkDate.setFont(new Font("Tahoma", Font.BOLD, 11));
		checkDate.setBounds(678, 18, 117, 23);
		checkDate.addActionListener(this);
		datePanel.add(checkDate);
		JPanel filterPanel = new JPanel();
		filterPanel.setBackground(new Color(102, 204, 204));
		filterPanel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		filterPanel.setBounds(0, 60, width - 120, 248);
		add(filterPanel);
		filterPanel.setLayout(null);
		JLabel lblPlName = new JLabel("PL Name:");
		lblPlName.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblPlName.setBounds(120, 28, 62, 14);
		filterPanel.add(lblPlName);
		plCombo = new JComboBox(result.get(0));
		plCombo.setBounds(251, 25, 184, 20);
		filterPanel.add(plCombo);
		plCombo.setSelectedItem("All");
		comboBoxItems[0] = plCombo;
		supplierCombo = new JComboBox(result.get(1));
		supplierCombo.setBounds(251, 71, 184, 20);
		filterPanel.add(supplierCombo);
		supplierCombo.setSelectedItem("All");
		comboBoxItems[1] = supplierCombo;
		JLabel lblSupplier = new JLabel("Supplier:");
		lblSupplier.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblSupplier.setBounds(120, 74, 80, 14);
		filterPanel.add(lblSupplier);
		statusCombo = new JComboBox(result.get(2));
		statusCombo.setBounds(251, 117, 184, 20);
		filterPanel.add(statusCombo);
		comboBoxItems[2] = statusCombo;
		JLabel lblStatus = new JLabel("Status:");
		lblStatus.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblStatus.setBounds(120, 120, 80, 14);
		filterPanel.add(lblStatus);
		extracted = new JComboBox(result.get(3));
		extracted.setBounds(251, 170, 184, 20);
		filterPanel.add(extracted);
		comboBoxItems[3] = extracted;
		JLabel lblExtracted = new JLabel("Extracted:");
		lblExtracted.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblExtracted.setBounds(120, 173, 80, 14);
		filterPanel.add(lblExtracted);
		pdfLink = new JTextField();
		table.setRowSelectionAllowed(true);
//		table.setColumnSelectionAllowed(true);
//		urlColumn.setCellEditor(new DefaultCellEditor(pdfLink));
		table.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
			}

			@Override
			public void mouseClicked(MouseEvent arg0) {
				String data = table.getValueAt(table.getSelectedRow(), 0).toString();
				Pattern pattern = Pattern.compile("<html><a href=\"(.*?)\">.*?</a>");
				Matcher matcher = pattern.matcher(data);
				System.out.println("Editing column is " + table.getSelectedColumn());
				String url = null;
				while (matcher.find()) {
					url = matcher.group(1);
				}
				if (table.getSelectedColumn() == 0) {
					if (Desktop.isDesktopSupported()) {
						try {
							Desktop.getDesktop().browse(new URI(url));
						} catch (IOException ex) { /* TODO: error handling */
						} catch (URISyntaxException e1) {
							e1.printStackTrace();
						}
					}
				}
			}
		});

		doFilterBtn = new JButton("Show PDFs");
		doFilterBtn.setFont(new Font("Tahoma", Font.BOLD, 11));
		doFilterBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Loading loading = new Loading();
				Thread thread = new Thread(loading);
				thread.start();
				filteredData = new ArrayList<DocumentInfoDTO>();
				if (jDateChooser1.isEnabled()) {
					startDate = jDateChooser1.getDate();
					endDate = jDateChooser2.getDate();
				} else {
					startDate = null;
					endDate = null;
				}
				filteredData = ParaQueryUtil.getDevelopmentPDF(PdfLinks.this.userID, comboBoxItems[0].getSelectedItem().toString(),
						comboBoxItems[1].getSelectedItem().toString(), comboBoxItems[2].getSelectedItem().toString(), comboBoxItems[3]
								.getSelectedItem().toString(), startDate, endDate);
//				jDateChooser1.setDate(new Date(System.currentTimeMillis()));
//				jDateChooser2.setDate(new Date(System.currentTimeMillis()));
				setTableData(0, filteredData);
				thread.stop();
				loading.frame.dispose();
			}
		});
		doFilterBtn.setBounds(542, 91, 100, 29);
		filterPanel.add(doFilterBtn);
		scrollPanePanel = new JPanel();
		scrollPanePanel.setBorder(new LineBorder(new Color(0, 0, 0)));
		scrollPanePanel.setBounds(0, 307, width - 120, 525);
		scrollPanePanel.setLayout(null);
		scrollPanePanel.add(scrollPane);
		add(scrollPanePanel);
		loadButtonsPanel = new JPanel();
		loadButtonsPanel.setBackground(new Color(211, 211, 211));
		loadButtonsPanel.setBorder(new SoftBevelBorder(BevelBorder.RAISED, null, null, null, null));
		loadButtonsPanel.setBounds(width - 110, 0, 106, 950);
		add(loadButtonsPanel);
		loadButtonsPanel.setLayout(null);
		loadDevSheetBtn = new JButton("LoadSheet");
		loadDevSheetBtn.setBounds(3, 11, 89, 29);
		loadButtonsPanel.add(loadDevSheetBtn);
		loadAllDataBtn = new JButton("LoadAllData");
		loadAllDataBtn.setBounds(3, 43, 89, 29);
		loadButtonsPanel.add(loadAllDataBtn);
		PaggingPanel = new JPanel();
		PaggingPanel.setBounds(0, 843, width - 120, 70);
		add(PaggingPanel);
		PaggingPanel.setLayout(null);
		label = new JLabel();
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setBounds(10, 20, 150, 14);
		PaggingPanel.add(label);
		label.setText(this.getRecordNumber() + " Records");
		first = new JButton("");
		first.setIcon(new ImageIcon(PdfLinks.class.getResource("/Resources/first.png")));
		first.setBounds(986, 11, 33, 23);
		PaggingPanel.add(first);
		previous.setIcon(new ImageIcon(PdfLinks.class.getResource("/Resources/priv.png")));
		previous.setBounds(1018, 11, 33, 23);
		PaggingPanel.add(previous);
		next.setIcon(new ImageIcon(PdfLinks.class.getResource("/Resources/next.png")));
		next.setBounds(1094, 11, 33, 23);
		PaggingPanel.add(next);
		last.setIcon(new ImageIcon(PdfLinks.class.getResource("/Resources/last.png")));
		last.setBounds(1126, 11, 33, 23);
		PaggingPanel.add(last);
		paggingLabel.setHorizontalAlignment(SwingConstants.CENTER);
		paggingLabel.setBounds(1061, 11, 26, 23);
		PaggingPanel.add(paggingLabel);
		for (int i = 0; i < 4; i++) {
			comboBoxItems[i].addActionListener(this);
		}		
		first.addActionListener(this);
		next.addActionListener(this);
		previous.addActionListener(this);
		last.addActionListener(this);
	}

	public ArrayList<DocumentInfoDTO> getRows(int page) {
		ArrayList<DocumentInfoDTO> result = new ArrayList<DocumentInfoDTO>();
		int count = 20;
		if (filteredData.size() < (page * 20)) {
			count = filteredData.size() - ((page - 1) * 20);
		}
		for (int i = (page - 1) * 20; i < (((page - 1) * 20) + count); i++) {
			result.add(filteredData.get(i));
		}
		return result;

	}

	public void setTableData(int x, ArrayList<DocumentInfoDTO> result) {

		if (x == 0) {
			if (result.size() % 20 == 0) {
				this.setPageNumber(result.size() / 20);
			} else {
				this.setPageNumber((result.size() / 20) + 1);
			}
			this.setRecordNumber(result.size());
			label.setText("Records " + this.getRecordNumber() + " and Pages " + this.getPageNumber());
			first.setEnabled(false);
			previous.setEnabled(false);
		}
		System.out.println("page number is " + this.getPageNumber());
		for (int i = 0; i < 20; i++) {
			for (int j = 0; j < 6; j++) {
				table.setValueAt(null, i, j);
			}
		}
		int count = 20;
		if (result.size() < 20) {
			count = result.size();
		}
		System.out.println("size is " + count);
		for (int i = 0; i < count; i++) {
			System.out.println("pdf is " + result.get(i).getPdf().getSeUrl());
			System.out.println(" and pl is " + result.get(i).getSupplierPl().getPl().getName());
			table.setValueAt("<html><a href=\"" + result.get(i).getPdf().getSeUrl() + "\">" + result.get(i).getPdf().getSeUrl() + "</a>",
					i, 0);
			table.setValueAt(result.get(i).getSupplierPl().getPl().getName(), i, 1);
			table.setValueAt(result.get(i).getSupplierPl().getSupplier().getName(), i, 2);
			table.setValueAt(result.get(i).getTaskType(), i, 3);
			table.setValueAt(result.get(i).isExtracted(), i, 4);
			table.setValueAt(result.get(i).getPdf().getDownloadDate().toString().split("\\s")[0], i, 5);
		}
	}

	public ArrayList<Object[]> getDistinct(ArrayList<ArrayList<String>> list) {
		ArrayList<Object[]> result = new ArrayList<Object[]>();
		LinkedHashSet pls = new LinkedHashSet();
		pls.add("All");
		LinkedHashSet suppliers = new LinkedHashSet();
		suppliers.add("All");
		LinkedHashSet status = new LinkedHashSet();
		status.add("All");
		LinkedHashSet extracted = new LinkedHashSet();
		extracted.add("All");

		for (int i = 0; i < list.size(); i++) {
			pls.add(list.get(i).get(0));
			suppliers.add(list.get(i).get(1));
			status.add(list.get(i).get(2));
			extracted.add(list.get(i).get(3));
		}
		result.add(pls.toArray());
		result.add(suppliers.toArray());
		result.add(status.toArray());
		result.add(extracted.toArray());
		return result;
	}

	public ArrayList<ArrayList<String>> getFilteredData(ArrayList<ArrayList<String>> list) {
		ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
		String plName = comboBoxItems[0].getSelectedItem().toString();
		String supplier = comboBoxItems[1].getSelectedItem().toString();
		String status = comboBoxItems[2].getSelectedItem().toString();
		String extracted = comboBoxItems[3].getSelectedItem().toString();

		for (int i = 0; i < list.size(); i++) {
			if (!plName.equals("All")) {
				if (!list.get(i).get(0).equals(plName)) {
					continue;
				}
			}
			if (!supplier.equals("All")) {
				if (!list.get(i).get(1).equals(supplier)) {
					continue;
				}
			}
			if (!status.equals("All")) {
				if (!list.get(i).get(2).equals(status)) {
					continue;
				}
			}
			if (!extracted.equals("All")) {
				if (!list.get(i).get(3).equals(extracted)) {
					continue;
				}
			}
			result.add(list.get(i));

		}
		return result;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	public int getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}

	public int getRecordNumber() {
		return recordNumber;
	}

	public void setRecordNumber(int recordNumber) {
		this.recordNumber = recordNumber;
	}

	@Override
	public void actionPerformed(ActionEvent event) {

		Object obj = event.getSource();

		if (obj instanceof JCheckBox) {
			JCheckBox check = (JCheckBox) obj;
			if (check.isSelected()) {
				jDateChooser1.setEnabled(true);
				jDateChooser2.setEnabled(true);
			} else {
				jDateChooser1.setEnabled(false);
				jDateChooser2.setEnabled(false);
			}
		} else if (obj instanceof JComboBox) {
			JComboBox co = (JComboBox) obj;
			ArrayList<Object[]> result = getDistinct(getFilteredData(list));
			System.out.println("  " + comboBoxItems.length + " and " + result.get(0).length);
			String plName = comboBoxItems[0].getSelectedItem().toString();
			String suplier = comboBoxItems[1].getSelectedItem().toString();
			String status = comboBoxItems[2].getSelectedItem().toString();
			String count = comboBoxItems[3].getSelectedItem().toString();
			String initial[] = new String[4];
			for (int j = 0; j < comboBoxItems.length; j++) {
				initial[j] = comboBoxItems[j].getSelectedItem().toString();
				comboBoxItems[j].removeActionListener(this);
				comboBoxItems[j].removeAllItems();
			}

			for (int j = 0; j < comboBoxItems.length; j++) {

				for (int k = 0; k < result.get(j).length; k++) {
					comboBoxItems[j].addItem(result.get(j)[k]);
					System.out.println("" + j);
				}
				comboBoxItems[j].setSelectedItem(initial[j]);
				comboBoxItems[j].addActionListener(this);
			}
		} else if (obj instanceof JButton) {
			if (obj == first) {
				paggingLabel.setText("1");
				int count = 20;
				if (filteredData.size() < 20) {
					count = filteredData.size();
				}
				System.out.println("size is " + count);
				for (int i = 0; i < count; i++) {
					System.out.println("pdf is " + filteredData.get(i).getPdf().getSeUrl());
					System.out.println(" and pl is " + filteredData.get(i).getSupplierPl().getPl().getName());
					System.out.println(" and the supplieer is " + filteredData.get(i).getSupplierPl().getSupplier().getName());
					table.setValueAt("<html><a href=\"" + filteredData.get(i).getPdf().getSeUrl() + "\">"
							+ filteredData.get(i).getPdf().getSeUrl() + "</a>", i, 0);
					table.setValueAt(filteredData.get(i).getSupplierPl().getPl().getName(), i, 1);
					table.setValueAt(filteredData.get(i).getSupplierPl().getSupplier().getName(), i, 2);
					table.setValueAt(filteredData.get(i).getTaskType(), i, 3);
					table.setValueAt(filteredData.get(i).isExtracted(), i, 4);
					table.setValueAt(filteredData.get(i).getPdf().getDownloadDate().toString().split("\\s")[0], i, 5);
				}
				previous.setEnabled(false);
				first.setEnabled(false);
				next.setEnabled(true);
				last.setEnabled(true);
			} else if (obj == next) {
				previous.setEnabled(true);
				first.setEnabled(true);
				int current = PdfLinks.this.getCurrentPage();
				System.out.println("current is " + PdfLinks.this.getCurrentPage());
				PdfLinks.this.setCurrentPage(current + 1);
				paggingLabel.setText("" + (current + 1));
				setTableData(1, getRows(PdfLinks.this.getCurrentPage()));
				System.out.println("current is " + PdfLinks.this.getCurrentPage() + " and pageNumber is " + PdfLinks.this.getPageNumber());
				if (PdfLinks.this.getCurrentPage() == PdfLinks.this.getPageNumber()) {
					next.setEnabled(false);
					last.setEnabled(false);
				}
			} else if (obj == previous) {
				next.setEnabled(true);
				last.setEnabled(true);

				PdfLinks.this.setCurrentPage(PdfLinks.this.getCurrentPage() - 1);
				paggingLabel.setText("" + PdfLinks.this.getCurrentPage());
				setTableData(1, getRows(PdfLinks.this.getCurrentPage()));
				if (PdfLinks.this.getCurrentPage() == 1) {
					previous.setEnabled(false);
					first.setEnabled(false);
				}
			} else if (obj == last) {
				PdfLinks.this.setCurrentPage(PdfLinks.this.getPageNumber());
				paggingLabel.setText("" + PdfLinks.this.getPageNumber());
				setTableData(1, getRows(PdfLinks.this.getPageNumber()));
				next.setEnabled(false);
				last.setEnabled(false);
				previous.setEnabled(true);
				first.setEnabled(true);
			}

		}
	}	
}
