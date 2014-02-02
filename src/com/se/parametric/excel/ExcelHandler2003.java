package com.se.parametric.excel;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author ahmed_elreweeny
 */
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class ExcelHandler2003
{

	static boolean flag = false;
	String[] fieldsNames;
	ArrayList<String[]> data;
	private String inputFile;
	private static final int BLOCK_SIZE = 65535;
	private int dataBlocks;
	HSSFWorkbook wb;

	public static ArrayList<ArrayList<String>> getExcelData(File file)
	{
		ArrayList<ArrayList<String>> sheetData = new ArrayList<ArrayList<String>>();
		ArrayList<String> list = null;
		FileInputStream fis = null;

		try
		{
			fis = new FileInputStream(file);
			HSSFWorkbook workbook = new HSSFWorkbook(fis);
			HSSFSheet sheet = workbook.getSheetAt(0);
			Iterator rows = sheet.rowIterator();
			while(rows.hasNext())
			{
				list = new ArrayList<String>();
				HSSFRow row = (HSSFRow) rows.next();
				for(int i = 0; i < row.getLastCellNum(); i++)
				{
					HSSFCell cell = row.getCell(i, row.CREATE_NULL_AS_BLANK);
					String x = "";
					switch(cell.getCellType()){

					case 0:
						x = "" + cell.getNumericCellValue();
						break;
					case 1:
						x = "" + cell.getStringCellValue();
						break;
					case 2:
						break;
					}
					list.add(x);
				}
				for(int j = list.size(); j < 10; j++)
				{
					list.add("");
				}
				sheetData.add(list);
			}
		}catch(IOException e)
		{
			e.printStackTrace();
		}finally
		{
			if(fis != null)
			{
				try
				{
					fis.close();
				}catch(IOException ex)
				{
					Logger.getLogger(ExcelHandler2003.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
		}
		return sheetData;
	}

	public void writeExcelFile(String[] fieldsNames, ArrayList<Object[]> data, String filename)
	{
		// File file = new File(inputFile);
		this.fieldsNames = fieldsNames;
		HSSFWorkbook workbook = new HSSFWorkbook();
		dataBlocks = data.size() / (BLOCK_SIZE + 1);
		dataBlocks++;
		try
		{
			File f = new File("C:/Reports/ParametricAutomation");
			f.mkdirs();
		}catch(Exception e)
		{
			e.printStackTrace();
		}

		for(int i = 0; i < dataBlocks; i++)
		{
			HSSFSheet excelSheet = workbook.createSheet("Sheet " + (i + 1));

			HSSFRow hdr = excelSheet.createRow(0);
			String[] hdrCells = fieldsNames;

			for(int cellNum = 0; cellNum < hdrCells.length; cellNum++)
			{
				HSSFCell myCell = hdr.createCell(cellNum);
				myCell.setCellValue(hdrCells[cellNum]);
			}

			HSSFRow myRow = null;
			HSSFCell myCell = null;

			for(int rowNum = 0; rowNum < data.size(); rowNum++)
			{
				myRow = excelSheet.createRow(rowNum + 1);
				Object[] cells = data.get(rowNum);
				System.out.println("Server Rec No>>" + rowNum);
				// System.out.println("Cells>>" + cells[0] + "\t" + cells[1]
				// + "\t" + cells[2] + "\t" + cells[3]);
				for(int cellNum = 0; cellNum < cells.length; cellNum++)
				{
					myCell = myRow.createCell(cellNum);
					if(cells[cellNum] == null)
					{
						myCell.setCellValue("");
					}
					else
					{
						myCell.setCellValue("" + cells[cellNum].toString());
					}

				}
			}
			try
			{
				FileOutputStream out = new FileOutputStream("C:/Reports/ParametricAutomation/" + filename);
				workbook.write(out);
				out.flush();
				out.close();

			}catch(Exception e)
			{
				e.printStackTrace();
			}

		}
	}

	public void writeExcelFile(String[] fieldsNames, List<Map<String, Object>> data, String filename)
	{
		// File file = new File(inputFile);
		this.fieldsNames = fieldsNames;
		HSSFWorkbook workbook = new HSSFWorkbook();
		dataBlocks = data.size() / (BLOCK_SIZE + 1);
		dataBlocks++;
		try
		{
			File f = new File("C:/Reports/ParametricAutomation");
			f.mkdirs();
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		for(int i = 0; i < dataBlocks; i++)
		{
			HSSFSheet excelSheet = workbook.createSheet("Sheet " + (i + 1));

			HSSFRow hdr = excelSheet.createRow(0);
			String[] hdrCells = fieldsNames;

			for(int cellNum = 0; cellNum < hdrCells.length; cellNum++)
			{
				HSSFCell myCell = hdr.createCell(cellNum);
				myCell.setCellValue(hdrCells[cellNum]);
			}

			HSSFRow myRow = null;
			HSSFCell myCell = null;

			for(int j = 0; j < data.size(); j++)
			{
				Map<String, Object> map = data.get(j);
				myRow = excelSheet.createRow(j + 1);
				for(int k = 0; k < fieldsNames.length; k++)
				{
					String headerFetName = fieldsNames[k];
					Object fetValObj = map.get(headerFetName);
					myCell = myRow.createCell(k);
					if(fetValObj == null)
					{
						myCell.setCellValue("");
					}
					else
					{
						myCell.setCellValue(fetValObj.toString());
					}
				}
			}
			try
			{
				FileOutputStream out = new FileOutputStream("C:/Reports/ParametricAutomation/" + filename);
				workbook.write(out);
				out.flush();
				out.close();

			}catch(Exception e)
			{
				e.printStackTrace();
			}

		}
	}

	public void WriteExcel(String[] fieldsNames, String[][] bodyData, String filename)
	{
		this.fieldsNames = fieldsNames;

		data = new ArrayList<String[]>();
		int recordsNum = 0;

		// Array came in format [column][row] so we reverse it to be
		// [row][column].
		if(bodyData[0] != null)
			recordsNum = bodyData[0].length;

		for(int i = 0; i < recordsNum; i++)
		{
			String[] row = new String[bodyData.length];

			for(int j = 0; j < bodyData.length; j++)
				row[j] = bodyData[j][i];

			data.add(row);
		}

		// setOutputFile(filename);

		try
		{
			// write(filename);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public void createExcelFile(String fileName) throws FileNotFoundException, IOException
	{
		HSSFWorkbook workbook = new HSSFWorkbook();
		// create two sheets
		HSSFSheet firstSheet = workbook.createSheet("Out Put");
		File writeFile = new File(fileName);
		FileOutputStream fos = new FileOutputStream(writeFile);
		workbook.write(fos);
	}

	public HSSFRow addRow(String file) throws FileNotFoundException, IOException
	{
		FileInputStream fis = null;
		fis = new FileInputStream(file);
		// Create an excel workbook from the file system.
		wb = new HSSFWorkbook(fis);
		HSSFSheet sheet = wb.getSheetAt(0);
		// writingStyle = cellStyle;
		int rowNo = sheet.getPhysicalNumberOfRows();
		HSSFRow row = sheet.createRow(rowNo);
		return row;
	}

	// / --------method to write cell interface excel file----------
	public void writeCell(HSSFRow row, int columnNo, String celVal) throws IOException
	{
		HSSFCell cell = row.createCell(columnNo);
		cell.setCellValue(celVal);
		// cell.setCellStyle(addCellStyle());
		FileOutputStream fos = new FileOutputStream(inputFile);
		wb.write(fos);
		if(fos != null)
		{
			fos.flush();
			fos.close();
		}

	}

	public void writeDataRow(String fileName, List rowData) throws IOException
	{
		List data = rowData;

		HSSFRow row = addRow(fileName);

		for(int i = 0; i < data.size(); i++)
		{
			writeCell(row, i, (String) data.get(i));
		}
	}

}
