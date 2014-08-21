package com.se.parametric.commonPanel;

import java.awt.Desktop;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.DefaultRowSorter;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.border.BevelBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import com.se.parametric.dto.TableInfoDTO;

public class TablePanel extends JPanel implements ActionListener
{
	public JTable table;
	JLabel recordsLabel = new JLabel();
	JScrollPane scrollPane;
	JPanel paggingButtonPanel;
	JPanel scrollPanel;
	JPanel recordsPanel;
	private int numberOfRows;
	public int currentPage, pageNumber;
	private int recordNumber;
	boolean selectDate = false;
	JButton first = new JButton();
	JButton previous = new JButton("");
	JButton next = new JButton("");
	JButton last = new JButton("");
	JLabel paggingLabel;
	public String[] header;
	int recordsPerPage;

	private ArrayList<ArrayList<String>> filteredData;
	public ArrayList<TableInfoDTO> selectedData;

	public HashSet<String> loadedPdfs = new HashSet<String>();

	public TablePanel(String[] header, int width, int height)
	{
		this.header = header;
		setLayout(null);
		setSize(width, height);
		// setBounds(0, (((height - 100) * 4) / 10), width - 110,525 );
		paggingButtonPanel = new JPanel();
		paggingButtonPanel.setLayout(null);
		scrollPanel = new JPanel();
		paggingButtonPanel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		paggingButtonPanel.setFont(new Font("Tahoma", Font.BOLD, 11));
		paggingButtonPanel.setBounds(width - 300, height - 60, 300, 40);
		// paggingButtonPanel.setLayout(null);
		recordsPerPage = (int) ((height - 60) * 1.0 / 25);
		String[][] tableData = new String[recordsPerPage][header.length];
		// String[][] tableData = new String[20][header.length];
		table = new JTable();
		table.setRowHeight(25);
		table.setModel(new DefaultTableModel(tableData, header));
		table.setAutoCreateColumnsFromModel(false);
		table.getTableHeader().setReorderingAllowed(true);
		// table.setBounds(0, 524, width , height);

		scrollPane = new JScrollPane();
		scrollPane.setBounds(0, 0, width, (height - 60));
		scrollPane.setViewportView(table);
		first.setIcon(new ImageIcon(TablePanel.class.getResource("/Resources/first.png")));
		int start = 40;
		first.setBounds(0 + start, 5, 50, 25);
		paggingButtonPanel.add(first);
		previous.setIcon(new ImageIcon(TablePanel.class.getResource("/Resources/priv.png")));
		previous.setBounds(50 + start + 5, 5, 50, 25);
		paggingButtonPanel.add(previous);
		paggingLabel = new JLabel();
		// paggingLabel.setHorizontalAlignment(SwingConstants.CENTER);
		paggingLabel.setBounds(100 + start + 10, 5, 20, 25);
		paggingButtonPanel.add(paggingLabel);
		next.setIcon(new ImageIcon(TablePanel.class.getResource("/Resources/next.png")));
		next.setBounds(110 + start + 15, 5, 50, 25);
		paggingButtonPanel.add(next);
		last.setIcon(new ImageIcon(TablePanel.class.getResource("/Resources/last.png")));
		last.setBounds(160 + start + 20, 5, 50, 25);
		paggingButtonPanel.add(last);

		first.addActionListener(this);
		next.addActionListener(this);
		previous.addActionListener(this);
		last.addActionListener(this);
		recordsPanel = new JPanel();
		recordsPanel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		recordsPanel.setFont(new Font("Tahoma", Font.BOLD, 11));
		recordsPanel.setBounds(0, height - 60, 300, 40);
		recordsPanel.add(recordsLabel);
		this.add(scrollPane);
		add(paggingButtonPanel);
		add(recordsPanel);

		table.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent arg0)
			{
			}

			@Override
			public void mousePressed(MouseEvent arg0)
			{
			}

			@Override
			public void mouseExited(MouseEvent arg0)
			{
			}

			@Override
			public void mouseEntered(MouseEvent arg0)
			{
			}

			@Override
			public void mouseClicked(MouseEvent arg0)
			{
				try
				{
					String data = table.getValueAt(table.getSelectedRow(), 0).toString();
					Pattern pattern = Pattern.compile("<html><a href=\"(.*?)\">.*?</a>");
					Matcher matcher = pattern.matcher(data);
					System.out.println("Editing column is " + table.getSelectedColumn());
					String url = null;
					while(matcher.find())
					{
						url = matcher.group(1);
					}
					if(table.getSelectedColumn() == 0)
					{
						if(Desktop.isDesktopSupported())
						{
							try
							{
								Desktop.getDesktop().browse(new URI(url));
							}catch(IOException ex)
							{ /* TODO: error handling */
							}catch(URISyntaxException e1)
							{
								e1.printStackTrace();
							}
						}
					}
				}catch(NullPointerException e)
				{
					System.out.println("blank record clicked !!");
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public void actionPerformed(ActionEvent event)
	{
		Object obj = event.getSource();
		if(obj == first)
		{
			paggingLabel.setText("1");
			setTableData1(1, selectedData);
			setCurrentPage(1);
			// int count = 20;
			// if (filteredData.size() < 20) {
			// count = filteredData.size();
			// }
			// System.out.println("size is " + count);
			// for (int i = 0; i < count; i++) {
			// for (int j = 0; j < header.length; j++) {
			// // System.out.println("pdf is " + filteredData.get(i)[j]);
			// // System.out.println(" and pl is " + filteredData.get(i).getSupplierPl().getPl().getName());
			// // System.out.println(" and the supplieer is " + filteredData.get(i).getSupplierPl().getSupplier().getName());
			// // table.setValueAt("<html><a href=\"" + filteredData.get(i).getPdf().getSeUrl() + "\">"
			// // + filteredData.get(i).getPdf().getSeUrl() + "</a>", i, 0);
			// table.setValueAt(filteredData.get(i).get(j), i, j);
			// // table.setValueAt(filteredData.get(i).getSupplierPl().getSupplier().getName(), i, 2);
			// // table.setValueAt(filteredData.get(i).getStatus(), i, 3);
			// // table.setValueAt(filteredData.get(i).isExtracted(), i, 4);
			// // table.setValueAt(filteredData.get(i).getPdf().getDownloadDate().toString().split("\\s")[0], i, 5);
			// }
			// }
			previous.setEnabled(false);
			first.setEnabled(false);
			next.setEnabled(true);
			last.setEnabled(true);

		}
		else if(obj == next)
		{
			previous.setEnabled(true);
			first.setEnabled(true);
			int current = this.getCurrentPage();
			this.setCurrentPage(current + 1);
			System.out.println("current is " + this.getCurrentPage());
			paggingLabel.setText("" + (current + 1));
			setTableData1(1, getRows1(this.getCurrentPage()));
			System.out.println("current is " + this.getCurrentPage() + " and pageNumber is "
					+ this.getPageNumber());
			if(this.getCurrentPage() == this.getPageNumber())
			{
				next.setEnabled(false);
				last.setEnabled(false);
			}
			else
			{
				next.setEnabled(true);
				last.setEnabled(true);
			}
		}
		else if(obj == previous)
		{
			next.setEnabled(true);
			last.setEnabled(true);

			this.setCurrentPage(this.getCurrentPage() - 1);
			paggingLabel.setText("" + this.getCurrentPage());
			setTableData1(1, getRows1(this.getCurrentPage()));
			if(this.getCurrentPage() == 1)
			{
				previous.setEnabled(false);
				first.setEnabled(false);
			}
			else
			{
				previous.setEnabled(true);
				first.setEnabled(true);
			}
		}
		else if(obj == last)
		{
			this.setCurrentPage(this.getPageNumber());
			paggingLabel.setText("" + this.getPageNumber());
			setTableData1(1, getRows1(this.getPageNumber()));
			next.setEnabled(false);
			last.setEnabled(false);
			previous.setEnabled(true);
			first.setEnabled(true);
		}

	}

	public ArrayList<ArrayList<String>> getRows(int page)
	{
		ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
		int count = recordsPerPage;
		if(filteredData.size() < (page * recordsPerPage))
		{
			count = filteredData.size() - ((page - 1) * recordsPerPage);
		}
		for(int i = (page - 1) * recordsPerPage; i < (((page - 1) * recordsPerPage) + count); i++)
		{
			result.add(filteredData.get(i));
		}
		return result;

	}

	public ArrayList<TableInfoDTO> getRows1(int page)
	{
		ArrayList<TableInfoDTO> result = new ArrayList<TableInfoDTO>();
		int count = recordsPerPage;
		if(selectedData.size() < (page * recordsPerPage))
		{
			count = selectedData.size() - ((page - 1) * recordsPerPage);
		}
		for(int i = (page - 1) * recordsPerPage; i < (((page - 1) * recordsPerPage) + count); i++)
		{
			result.add(selectedData.get(i));
		}
		return result;

	}

	public int getCurrentPage()
	{
		return currentPage;
	}

	public void setCurrentPage(int currentPage)
	{
		this.currentPage = currentPage;
	}

	public int getPageNumber()
	{
		return pageNumber;
	}

	public void setPageNumber(int pageNumber)
	{
		this.pageNumber = pageNumber;
	}

	public void setTableData(int x, ArrayList<ArrayList<String>> result)
	{

		if(x == 0)
		{
			if(result.size() % recordsPerPage == 0)
			{
				this.setPageNumber(result.size() / recordsPerPage);
			}
			else
			{
				this.setPageNumber((result.size() / recordsPerPage) + 1);
			}
			this.setRecordNumber(result.size());
			recordsLabel.setText("Records " + this.getRecordNumber() + " and Pages "
					+ this.getPageNumber());
			first.setEnabled(false);
			previous.setEnabled(false);
		}
		System.out.println("page number is " + this.getPageNumber());
		for(int i = 0; i < recordsPerPage; i++)
		{
			for(int j = 0; j < 6; j++)
			{
				table.setValueAt(null, i, j);
			}
		}
		int count = recordsPerPage;
		if(result.size() < recordsPerPage)
		{
			count = result.size();
		}
		System.out.println("size is " + count);
		for(int i = 0; i < count; i++)
		{
			for(int j = 0; j < header.length; j++)
			{
				// System.out.println("pdf is " + result.get(i).get(j));
				// System.out.println(" and pl is " + result.get(i).getSupplierPl().getPl().getName());
				// table.setValueAt("<html><a href=\"" + result.get(i).getPdf().getSeUrl() + "\">" + result.get(i).getPdf().getSeUrl() +
				// "</a>",
				// i, 0);
				table.setValueAt(result.get(i).get(j), i, j);
				// table.setValueAt(result.get(i).getSupplierPl().getSupplier().getName(), i, 2);
				// table.setValueAt(result.get(i).getStatus(), i, 3);
				// table.setValueAt(result.get(i).isExtracted(), i, 4);
				// table.setValueAt(result.get(i).getPdf().getDownloadDate().toString().split("\\s")[0], i, 5);
			}
		}
	}

	public void setTableData1(int x, ArrayList<TableInfoDTO> result)
	{

		Method[] m = TableInfoDTO.class.getMethods();
		Method[] mapMethods = new Method[header.length];
		try
		{
			for(int i = 0; i < header.length; i++)
			{
				for(Method method : m)
				{
					if(header[i].contains("Date"))
					{
						if(method.getName().equals("getDate"))
						{
							mapMethods[i] = method;
							break;
						}
					}
					else
					{
						if(method.getName().equals("get" + header[i]))
						{
							mapMethods[i] = method;
							break;
						}
					}
				}
			}

			if(x == 0)
			{
				if(result.size() % recordsPerPage == 0)
				{
					this.setPageNumber(result.size() / recordsPerPage);
				}
				else
				{
					this.setPageNumber((result.size() / recordsPerPage) + 1);
				}
				this.setRecordNumber(result.size());
				recordsLabel.setText("Records " + this.getRecordNumber() + " and Pages "
						+ this.getPageNumber());
				paggingLabel.setText("1");
				// setCurrentPage(1);
				first.setEnabled(false);
				previous.setEnabled(false);
				if(pageNumber > 1)
				{
					next.setEnabled(true);
					last.setEnabled(true);
				}
				else
				{
					next.setEnabled(false);
					last.setEnabled(false);
				}

			}
			System.out.println("page number is " + this.getPageNumber());
			for(int i = 0; i < recordsPerPage; i++)
			{
				for(int j = 0; j < header.length; j++)
				{
					table.setValueAt(null, i, j);
				}
			}
			int count = recordsPerPage;
			if(result.size() < recordsPerPage)
			{
				count = result.size();
			}
			System.out.println("size is " + count);
			for(int i = 0; i < count; i++)
			{
				for(int j = 0; j < header.length; j++)
				{
					// System.out.println("pdf is " + result.get(i).get(j));
					// System.out.println(" and pl is " + result.get(i).getSupplierPl().getPl().getName());
					// table.setValueAt("<html><a href=\"" + result.get(i).getPdf().getSeUrl() + "\">" + result.get(i).getPdf().getSeUrl() +
					// "</a>",
					// i, 0);

					// System.out.println("Method Name:" + mapMethods[j].getName());
					Object obj = mapMethods[j].invoke(result.get(i));
					String cellValue = "";
					if(obj != null)
					{
						cellValue = obj.toString();
					}
					if(mapMethods[j].getName().equals("getPdfUrl"))
					{
						if(loadedPdfs.contains(cellValue))
						{
							cellValue = "<html><a href=\"" + cellValue
									+ "\"><font color=\"#AD3333\">" + cellValue + "</font></a>";
						}
						else
						{
							cellValue = "<html><a href=\"" + cellValue + "\">" + cellValue + "</a>";
						}

						table.getColumnModel().getColumn(j).setPreferredWidth(450);
					}
					table.setValueAt(cellValue, i, j);

					// table.setValueAt(result.get(i).getSupplierPl().getSupplier().getName(), i, 2);
					// table.setValueAt(result.get(i).getStatus(), i, 3);
					// table.setValueAt(result.get(i).isExtracted(), i, 4);
					// table.setValueAt(result.get(i).getPdf().getDownloadDate().toString().split("\\s")[0], i, 5);
				}
			}

		}catch(IllegalAccessException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch(IllegalArgumentException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch(InvocationTargetException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public int getRecordNumber()
	{
		return recordNumber;
	}

	public void setRecordNumber(int recordNumber)
	{
		this.recordNumber = recordNumber;
	}

	public ArrayList<ArrayList<String>> getFilteredData()
	{
		return filteredData;
	}

	public void setFilteredData(ArrayList<ArrayList<String>> filteredData)
	{
		this.filteredData = filteredData;
	}

	public int getRecordsPerPage()
	{
		return recordsPerPage;
	}

	public void setRecordsPerPage(int recordsPerPage)
	{
		this.recordsPerPage = recordsPerPage;
	}

	public void clearTable()
	{
		for(int i = 0; i < recordsPerPage; i++)
		{
			for(int j = 0; j < header.length; j++)
			{
				table.setValueAt(null, i, j);
			}
		}
	}

}

// package com.se.parametric.commonPanel;
//
// import java.awt.Desktop;
// import java.awt.Font;
// import java.awt.event.ActionEvent;
// import java.awt.event.ActionListener;
// import java.awt.event.MouseEvent;
// import java.awt.event.MouseListener;
// import java.io.IOException;
// import java.lang.reflect.InvocationTargetException;
// import java.lang.reflect.Method;
// import java.net.URI;
// import java.net.URISyntaxException;
// import java.util.ArrayList;
// import java.util.HashSet;
// import java.util.Set;
// import java.util.regex.Matcher;
// import java.util.regex.Pattern;
//
// import javax.swing.ImageIcon;
// import javax.swing.JButton;
// import javax.swing.JLabel;
// import javax.swing.JPanel;
// import javax.swing.JScrollPane;
// import javax.swing.JTable;
// import javax.swing.SwingConstants;
// import javax.swing.border.BevelBorder;
// import javax.swing.table.DefaultTableModel;
//
// import org.apache.poi.hslf.blip.Metafile.Header;
//
// import com.se.parametric.dev.PdfLinks;
// import com.se.parametric.dto.TableInfoDTO;
// import com.se.parametric.dto.DocumentInfoDTO;
//
// public class TablePanel extends JPanel implements ActionListener {
// public JTable table;
// JLabel recordsLabel = new JLabel();
// JScrollPane scrollPane;
// JPanel paggingButtonPanel;
// JPanel scrollPanel;
// JPanel recordsPanel;
// private int numberOfRows;
// public int currentPage, pageNumber;
// private int recordNumber;
// boolean selectDate = false;
// JButton first = new JButton();
// JButton previous = new JButton("");
// JButton next = new JButton("");
// JButton last = new JButton("");
// JLabel paggingLabel;
// public String[] header;
//
// private ArrayList<ArrayList<String>> filteredData;
// public ArrayList<TableInfoDTO> selectedData;
//
// public HashSet<String> loadedPdfs = new HashSet<String>();
//
// public TablePanel(String[] header, int width, int height) {
// this.header = header;
// setLayout(null);
// setSize(width, height);
// // setBounds(0, (((height - 100) * 4) / 10), width - 110,525 );
// paggingButtonPanel = new JPanel();
// paggingButtonPanel.setLayout(null);
// scrollPanel = new JPanel();
// paggingButtonPanel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
// paggingButtonPanel.setFont(new Font("Tahoma", Font.BOLD, 11));
// paggingButtonPanel.setBounds(width - 300, height - 60, 300, 40);
// // paggingButtonPanel.setLayout(null);
// // int recordsPerPage = (int)((height-60)*1.0/25);
// // String[][] tableData = new String[recordsPerPage][header.length];
// String[][] tableData = new String[20][header.length];
//
// table = new JTable();
// table.setRowHeight(25);
// table.setModel(new DefaultTableModel(tableData, header));
// table.getTableHeader().setReorderingAllowed(true);
// // table.setBounds(0, 524, width , height);
//
// scrollPane = new JScrollPane();
// scrollPane.setBounds(0, 0, width, (height - 60));
// scrollPane.setViewportView(table);
// first.setIcon(new ImageIcon(TablePanel.class.getResource("/Resources/first.png")));
// int start = 40;
// first.setBounds(0 + start, 5, 50, 25);
// paggingButtonPanel.add(first);
// previous.setIcon(new ImageIcon(TablePanel.class.getResource("/Resources/priv.png")));
// previous.setBounds(50 + start + 5, 5, 50, 25);
// paggingButtonPanel.add(previous);
// paggingLabel = new JLabel();
// // paggingLabel.setHorizontalAlignment(SwingConstants.CENTER);
// paggingLabel.setBounds(100 + start + 10, 5, 10, 25);
// paggingButtonPanel.add(paggingLabel);
// next.setIcon(new ImageIcon(TablePanel.class.getResource("/Resources/next.png")));
// next.setBounds(110 + start + 15, 5, 50, 25);
// paggingButtonPanel.add(next);
// last.setIcon(new ImageIcon(TablePanel.class.getResource("/Resources/last.png")));
// last.setBounds(160 + start + 20, 5, 50, 25);
// paggingButtonPanel.add(last);
//
// first.addActionListener(this);
// next.addActionListener(this);
// previous.addActionListener(this);
// last.addActionListener(this);
// recordsPanel = new JPanel();
// recordsPanel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
// recordsPanel.setFont(new Font("Tahoma", Font.BOLD, 11));
// recordsPanel.setBounds(0, height - 60, 300, 40);
// recordsPanel.add(recordsLabel);
// this.add(scrollPane);
// add(paggingButtonPanel);
// add(recordsPanel);
//
// table.addMouseListener(new MouseListener() {
// @Override
// public void mouseReleased(MouseEvent arg0) {
// }
//
// @Override
// public void mousePressed(MouseEvent arg0) {
// }
//
// @Override
// public void mouseExited(MouseEvent arg0) {
// }
//
// @Override
// public void mouseEntered(MouseEvent arg0) {
// }
//
// @Override
// public void mouseClicked(MouseEvent arg0) {
// String data = table.getValueAt(table.getSelectedRow(), 0).toString();
// Pattern pattern = Pattern.compile("<html><a href=\"(.*?)\">.*?</a>");
// Matcher matcher = pattern.matcher(data);
// System.out.println("Editing column is " + table.getSelectedColumn());
// String url = null;
// while (matcher.find()) {
// url = matcher.group(1);
// }
// if (table.getSelectedColumn() == 0) {
// if (Desktop.isDesktopSupported()) {
// try {
// Desktop.getDesktop().browse(new URI(url));
// } catch (IOException ex) { /* TODO: error handling */
// } catch (URISyntaxException e1) {
// e1.printStackTrace();
// }
// }
// }
// }
// });
// }
//
// @Override
// public void actionPerformed(ActionEvent event) {
// Object obj = event.getSource();
// if (obj == first) {
// paggingLabel.setText("1");
// setTableData1(1, selectedData);
// // int count = 20;
// // if (filteredData.size() < 20) {
// // count = filteredData.size();
// // }
// // System.out.println("size is " + count);
// // for (int i = 0; i < count; i++) {
// // for (int j = 0; j < header.length; j++) {
// // // System.out.println("pdf is " + filteredData.get(i)[j]);
// // // System.out.println(" and pl is " + filteredData.get(i).getSupplierPl().getPl().getName());
// // // System.out.println(" and the supplieer is " + filteredData.get(i).getSupplierPl().getSupplier().getName());
// // // table.setValueAt("<html><a href=\"" + filteredData.get(i).getPdf().getSeUrl() + "\">"
// // // + filteredData.get(i).getPdf().getSeUrl() + "</a>", i, 0);
// // table.setValueAt(filteredData.get(i).get(j), i, j);
// // // table.setValueAt(filteredData.get(i).getSupplierPl().getSupplier().getName(), i, 2);
// // // table.setValueAt(filteredData.get(i).getStatus(), i, 3);
// // // table.setValueAt(filteredData.get(i).isExtracted(), i, 4);
// // // table.setValueAt(filteredData.get(i).getPdf().getDownloadDate().toString().split("\\s")[0], i, 5);
// // }
// // }
// previous.setEnabled(false);
// first.setEnabled(false);
// next.setEnabled(true);
// last.setEnabled(true);
// } else if (obj == next) {
// previous.setEnabled(true);
// first.setEnabled(true);
// int current = this.getCurrentPage();
// System.out.println("current is " + this.getCurrentPage());
// this.setCurrentPage(current + 1);
// paggingLabel.setText("" + (current + 1));
// setTableData1(1, getRows1(this.getCurrentPage()));
// System.out.println("current is " + this.getCurrentPage() + " and pageNumber is " + this.getPageNumber());
// if (this.getCurrentPage() == this.getPageNumber()) {
// next.setEnabled(false);
// last.setEnabled(false);
// }
// } else if (obj == previous) {
// next.setEnabled(true);
// last.setEnabled(true);
//
// this.setCurrentPage(this.getCurrentPage() - 1);
// paggingLabel.setText("" + this.getCurrentPage());
// setTableData1(1, getRows1(this.getCurrentPage()));
// if (this.getCurrentPage() == 1) {
// previous.setEnabled(false);
// first.setEnabled(false);
// }
// } else if (obj == last) {
// this.setCurrentPage(this.getPageNumber());
// paggingLabel.setText("" + this.getPageNumber());
// setTableData1(1, getRows1(this.getPageNumber()));
// next.setEnabled(false);
// last.setEnabled(false);
// previous.setEnabled(true);
// first.setEnabled(true);
// }
//
// }
//
// public ArrayList<ArrayList<String>> getRows(int page) {
// ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
// int count = 20;
// if (filteredData.size() < (page * 20)) {
// count = filteredData.size() - ((page - 1) * 20);
// }
// for (int i = (page - 1) * 20; i < (((page - 1) * 20) + count); i++) {
// result.add(filteredData.get(i));
// }
// return result;
//
// }
//
// public ArrayList<TableInfoDTO> getRows1(int page) {
// ArrayList<TableInfoDTO> result = new ArrayList<TableInfoDTO>();
// int count = 20;
// if (selectedData.size() < (page * 20)) {
// count = selectedData.size() - ((page - 1) * 20);
// }
// for (int i = (page - 1) * 20; i < (((page - 1) * 20) + count); i++) {
// result.add(selectedData.get(i));
// }
// return result;
//
// }
//
// public int getCurrentPage() {
// return currentPage;
// }
//
// public void setCurrentPage(int currentPage) {
// this.currentPage = currentPage;
// }
//
// public int getPageNumber() {
// return pageNumber;
// }
//
// public void setPageNumber(int pageNumber) {
// this.pageNumber = pageNumber;
// }
//
// public void setTableData(int x, ArrayList<ArrayList<String>> result) {
//
// if (x == 0) {
// if (result.size() % 20 == 0) {
// this.setPageNumber(result.size() / 20);
// } else {
// this.setPageNumber((result.size() / 20) + 1);
// }
// this.setRecordNumber(result.size());
// recordsLabel.setText("Records " + this.getRecordNumber() + " and Pages " + this.getPageNumber());
// first.setEnabled(false);
// previous.setEnabled(false);
// }
// System.out.println("page number is " + this.getPageNumber());
// for (int i = 0; i < 20; i++) {
// for (int j = 0; j < 6; j++) {
// table.setValueAt(null, i, j);
// }
// }
// int count = 20;
// if (result.size() < 20) {
// count = result.size();
// }
// System.out.println("size is " + count);
// for (int i = 0; i < count; i++) {
// for (int j = 0; j < header.length; j++) {
// // System.out.println("pdf is " + result.get(i).get(j));
// // System.out.println(" and pl is " + result.get(i).getSupplierPl().getPl().getName());
// // table.setValueAt("<html><a href=\"" + result.get(i).getPdf().getSeUrl() + "\">" + result.get(i).getPdf().getSeUrl() +
// // "</a>",
// // i, 0);
// table.setValueAt(result.get(i).get(j), i, j);
// // table.setValueAt(result.get(i).getSupplierPl().getSupplier().getName(), i, 2);
// // table.setValueAt(result.get(i).getStatus(), i, 3);
// // table.setValueAt(result.get(i).isExtracted(), i, 4);
// // table.setValueAt(result.get(i).getPdf().getDownloadDate().toString().split("\\s")[0], i, 5);
// }
// }
// }
//
// public void setTableData1(int x, ArrayList<TableInfoDTO> result) {
//
// Method[] m = TableInfoDTO.class.getMethods();
// Method[] mapMethods = new Method[header.length];
// try {
// for (int i = 0; i < header.length; i++) {
// for (Method method : m) {
// // System.out.println("Method Name:" + method.getName());
// if (method.getName().equals("get" + header[i])) {
// mapMethods[i] = method;
// break;
// }
// }
// }
//
// if (x == 0) {
// if (result.size() % 20 == 0) {
// this.setPageNumber(result.size() / 20);
// } else {
// this.setPageNumber((result.size() / 20) + 1);
// }
// this.setRecordNumber(result.size());
// recordsLabel.setText("Records " + this.getRecordNumber() + " and Pages " + this.getPageNumber());
// paggingLabel.setText("1");
// // setCurrentPage(1);
// first.setEnabled(false);
// previous.setEnabled(false);
// next.setEnabled(true);
// last.setEnabled(true);
// }
// System.out.println("page number is " + this.getPageNumber());
// for (int i = 0; i < 20; i++) {
// for (int j = 0; j < header.length; j++) {
// table.setValueAt(null, i, j);
// }
// }
// int count = 20;
// if (result.size() < 20) {
// count = result.size();
// }
// System.out.println("size is " + count);
// for (int i = 0; i < count; i++) {
// for (int j = 0; j < header.length; j++) {
// // System.out.println("pdf is " + result.get(i).get(j));
// // System.out.println(" and pl is " + result.get(i).getSupplierPl().getPl().getName());
// // table.setValueAt("<html><a href=\"" + result.get(i).getPdf().getSeUrl() + "\">" + result.get(i).getPdf().getSeUrl() +
// // "</a>",
// // i, 0);
//
// System.out.println("Method Name:" + mapMethods[j].getName());
// Object obj = mapMethods[j].invoke(result.get(i));
// String cellValue = "";
// if (obj != null) {
// cellValue = obj.toString();
// }
// if (mapMethods[j].getName().equals("getPdfUrl")) {
// if (loadedPdfs.contains(cellValue)) {
// cellValue = "<html><a href=\"" + cellValue + "\"><font color=\"#AD3333\">" + cellValue + "</font></a>";
// } else {
// cellValue = "<html><a href=\"" + cellValue + "\">" + cellValue + "</a>";
// }
//
// table.getColumnModel().getColumn(j).setPreferredWidth(450);
// }
// table.setValueAt(cellValue, i, j);
//
// // table.setValueAt(result.get(i).getSupplierPl().getSupplier().getName(), i, 2);
// // table.setValueAt(result.get(i).getStatus(), i, 3);
// // table.setValueAt(result.get(i).isExtracted(), i, 4);
// // table.setValueAt(result.get(i).getPdf().getDownloadDate().toString().split("\\s")[0], i, 5);
// }
// }
//
// } catch (IllegalAccessException e) {
// // TODO Auto-generated catch block
// e.printStackTrace();
// } catch (IllegalArgumentException e) {
// // TODO Auto-generated catch block
// e.printStackTrace();
// } catch (InvocationTargetException e) {
// // TODO Auto-generated catch block
// e.printStackTrace();
// }
// }
//
// public int getRecordNumber() {
// return recordNumber;
// }
//
// public void setRecordNumber(int recordNumber) {
// this.recordNumber = recordNumber;
// }
//
// public ArrayList<ArrayList<String>> getFilteredData() {
// return filteredData;
// }
//
// public void setFilteredData(ArrayList<ArrayList<String>> filteredData) {
// this.filteredData = filteredData;
// }
// }
