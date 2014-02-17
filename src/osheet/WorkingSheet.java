package osheet;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import javax.swing.JOptionPane;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.property.Getter;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.WorksheetDocument;

import com.se.automation.db.ParametricQueryUtil;
import com.se.automation.db.QueryUtil;
import com.se.automation.db.SessionUtil;
import com.se.automation.db.client.dto.ComponentDTO;
import com.se.automation.db.client.dto.PlfeatureValuesDTO;
import com.se.automation.db.client.mapping.Document;
import com.se.automation.db.client.mapping.Pdf;
import com.se.automation.db.client.mapping.Pl;
import com.se.automation.db.client.mapping.Supplier;
import com.se.automation.db.client.mapping.SupplierPl;
import com.se.automation.db.client.mapping.SupplierUrl;
import com.se.automation.db.client.mapping.TrackingParametric;
import com.se.grm.client.mapping.GrmUser;
import com.se.parametric.AppContext;
import com.se.parametric.dba.DataDevQueryUtil;
import com.se.parametric.dba.ParaQueryUtil;
import com.se.parametric.dba.ParametricDevServerUtil;
import com.se.parametric.dto.DocumentInfoDTO;
import com.se.parametric.dto.FeatureDTO;
import com.se.parametric.dto.GrmUserDTO;
import com.se.parametric.dto.PartInfoDTO;
import com.se.parametric.dto.TableInfoDTO;
import com.se.parametric.util.ClientUtil;
import com.se.parametric.util.PDDRow;
import com.se.parametric.util.StatusName;
import com.se.parametric.util.ValidatePart;
import com.sun.deploy.ui.FancyButton;
import com.sun.star.beans.Property;
import com.sun.star.beans.XPropertySet;
import com.sun.star.beans.XPropertySetInfo;
import com.sun.star.lang.IndexOutOfBoundsException;
import com.sun.star.sheet.CellDeleteMode;
import com.sun.star.sheet.XCellRangeAddressable;
import com.sun.star.sheet.XCellRangesQuery;
import com.sun.star.sheet.XSheetCellRanges;
import com.sun.star.sheet.XSpreadsheet;
import com.sun.star.table.XCell;
import com.sun.star.table.XCellRange;
import com.sun.star.text.XText;
import com.sun.star.uno.Any;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XInterface;
import com.sun.star.util.CellProtection;

public class WorkingSheet
{

	protected List<Cell> HeaderList;
	protected Map<String, List<String>> approvedFeatuer = new HashMap<String, List<String>>();
	protected XSpreadsheet sheet;
	private int sheetindex;
	private String sheetname;
	private boolean sheetvisiblity;
	protected Pl sheetpl;
	protected Supplier supplier;
	private SupplierPl supplierPl;
	private String selectedPL;
	private String plType;
	// private String supplierName;
	private final int RowSelectedRange = 2000;
	protected int startParametricFT = 6;
	protected final int StatrtRecord = 1;
	private List<ComponentDTO> data;
	private com.sun.star.sheet.XCellRangeMovement xMovement;
	private Thread threadDrowData;
	private Thread ShowAllThread;
	protected boolean Validated;
	protected boolean canSave = false;
	protected int descriptionColumn;
	protected int valStatusColumn;
	protected int valCommentColumn;
	protected int valTaxonomyColumn;
	protected int headerColumnsCount;
	protected Document document;
	protected ClientUtil clientutils;
	XCellRange xHdrUnitrange;
	protected ValidatePart partvalidation = new ValidatePart(document, clientutils);
	private int PartCell = 0;
	private int taxonomiesCell = 0;
	private int supCell = 5;
	private int familyCell = 1;
	private int maskCellNo = 4;
	private int genericCellNo = 0;
	private int famCrossCellNo = 1;
	private int pdfCellNo = 4;
	private int npiCellNo;
	private int newsCellNo;
	private int valHeaderSize = 0;
	private boolean NPIFlag = false;
	private Properties culmns;
	static char[] chars = new char[26];
	private int endParametricFT = 0;

	SheetPanel sheetPanel;
	public List<String> statusValues = new ArrayList<String>();
	private List<String> allPlNames;

	public WorkingSheet(XSpreadsheet sheet, Pl sheetpl)
	{
		this.sheet = sheet;
		this.sheetpl = sheetpl;

	}

	public WorkingSheet(SheetPanel sheetPanel, String sheetName)
	{
		int num = 0;
		for(char c = 'A'; c <= 'Z'; ++c)
		{
			chars[num] = c;
			// System.out.println(c);
			num++;
		}
		this.sheet = sheetPanel.NewSheetByName(sheetName, 0);

	}

	public WorkingSheet(SheetPanel sheetPanel, String sheetPlName, int idx, boolean flag)
	{
		this.sheetPanel = sheetPanel;
		this.sheet = sheetPanel.NewSheetByName(sheetPlName, idx);
		int num = 0;
		for(char c = 'A'; c <= 'Z'; ++c)
		{
			chars[num] = c;
			// System.out.println(c);
			num++;
		}

	}

	public WorkingSheet(SheetPanel sheetPanel, String sheetPlName, int idx)
	{
		this.sheetPanel = sheetPanel;
		this.sheet = sheetPanel.NewSheetByName(sheetPlName, idx);
		this.selectedPL = sheetPlName;
		int num = 0;
		for(char c = 'A'; c <= 'Z'; ++c)
		{
			chars[num] = c;
			// System.out.println(c);
			num++;
		}
		try
		{
			this.sheetpl = ParaQueryUtil.getPlByPlName(sheetPlName);
			Pl plTypeObj = ParaQueryUtil.getPLType(sheetpl);

			if(plTypeObj == null)
			{
				JOptionPane.showMessageDialog(null, "Can't Load this PL Name as PL Type Not Clear");
			}
			else
			{
				this.plType = plTypeObj.getName();
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public WorkingSheet(SheetPanel sheetPanel, TrackingParametric track, int index)
	{
		this.sheetPanel = sheetPanel;
		this.sheet = sheetPanel.NewSheetByName(track.getPl().getName(), index);
		this.supplierPl = track.getSupplierPl();
		this.sheetpl = track.getPl();
		// this.trackingParametric = track;
		this.selectedPL = track.getPl().getName();
		int num = 0;
		for(char c = 'A'; c <= 'Z'; ++c)
		{
			chars[num] = c;
			// System.out.println(c);
			num++;
		}
		try
		{
			Pl plTypeObj = ParaQueryUtil.getPLType(track.getPl());
			this.plType = plTypeObj.getName();
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public void writeSheetData(ArrayList<ArrayList<String>> list, int start)
	{
		for(int i = 0; i < list.size(); i++)
		{
			for(int j = 0; j < list.get(i).size(); j++)
			{
				Cell cell = null;
				try
				{
					cell = getCellByPosission(j, i + start);
					cell.setText(list.get(i).get(j));
				}catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	public void writeReviewData(ArrayList<ArrayList<String>> list, int start, int column)
	{
		try
		{
			System.out.println("~~~~~~ Write Sheet Data ~~~~~");
			System.out.println(sheet);
			for(int i = 0; i < list.size(); i++)
			{
				Cell cell = null;
				for(int j = 0; j < list.get(0).size(); j++)
				{
					cell = getCellByPosission(j, i + start);
					cell.setText(list.get(i).get(j));
				}
				cell = getCellByPosission(column - 1, i + start);
				cell.SetApprovedValues(statusValues, sheet.getCellRangeByPosition(column - 1, i + start, column - 1, i + start));
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public void insertShowAllData()
	{
		if(threadDrowData != null)
		{
			if(threadDrowData.getState() == Thread.State.RUNNABLE || threadDrowData.getState() == Thread.State.WAITING)
			{
				try
				{
					threadDrowData.interrupt();
				}catch(Exception ex)
				{
					ex.printStackTrace();
				}
			}
			else
			{
				threadDrowData = null;
			}
		}
		if(ShowAllThread == null)
		{
			ShowAllThread = getShowAllThread();

		}

		if(!ShowAllThread.isAlive())
		{
			if(ShowAllThread.getState() == Thread.State.NEW)
			{
				ShowAllThread.start();
			}
			else if(ShowAllThread.getState() == Thread.State.TERMINATED)
			{
				ShowAllThread = getShowAllThread();
				ShowAllThread.start();
			}

		}
		else
		{
			if(ShowAllThread.getState() == Thread.State.RUNNABLE || ShowAllThread.getState() == Thread.State.WAITING)
			{
				try
				{
					ShowAllThread.interrupt();
					if(ShowAllThread.isInterrupted())
					{
						ShowAllThread = getShowAllThread();
						ShowAllThread.start();
					}

				}catch(Exception ex)
				{
					ex.printStackTrace();
				}
			}

		}
	}

	private void showAllHeader()
	{
		if(HeaderList == null)
		{

			HeaderList = new ArrayList<Cell>();
		}
		else
		{
			removeCellRangByPosission(headerColumnsCount);

			HeaderList.clear();
		}

		try
		{
			for(int i = 0; i < 16; i++)
			{
				Cell cell = getCellByPosission(i, 0);
				switch(i){
				case 0:
					cell.setText("PDF URL");
					HeaderList.add(cell);
					break;
				case 1:
					cell.setText("Vendor");
					HeaderList.add(cell);
					break;
				case 2:
					cell.setText("Vendor URL");
					HeaderList.add(cell);
					break;
				case 3:
					cell.setText("Taxonomies");
					HeaderList.add(cell);
					break;
				case 4:
					cell.setText("Datasheet Flag");
					HeaderList.add(cell);
					break;
				case 5:
					cell.setText("Delivery Date");
					HeaderList.add(cell);
					break;
				case 6:
					cell.setText("Introduction Date");
					HeaderList.add(cell);
					break;
				case 7:
					cell.setText("Tilte");
					HeaderList.add(cell);
					break;
				case 8:
					cell.setText("Online Link");
					HeaderList.add(cell);
					break;
				case 9:
					cell.setText("Number Of Pages");
					HeaderList.add(cell);
					break;
				case 10:
					cell.setText("Extracted");
					HeaderList.add(cell);
					break;

				case 11:
					cell.setText("News Link");
					HeaderList.add(cell);
					break;
				case 12:
					cell.setText("Taxonomy Path");
					HeaderList.add(cell);
					break;
				case 13:
					cell.setText("Status");
					HeaderList.add(cell);
					break;
				case 14:
					cell.setText("Comment");
					HeaderList.add(cell);
					break;
				case 15:
					cell.setText("Right Taxonomy");
					HeaderList.add(cell);
					break;

				}
			}
		}catch(Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			// AppContext.FirMessageError(e.getMessage(), this.getClass(), e);
		}

	}

	private void setMainHeaders(boolean newPdf)
	{
		try
		{
			System.out.println("Set Main Header");
			if(HeaderList == null)
			{
				approvedFeatuer.clear();
				HeaderList = new ArrayList<Cell>();
			}
			// else {
			// removeCellRangByPosission(headerColumnsCount);
			//
			// HeaderList.clear();
			// }
			Cell cell = null;

			if(newPdf)
			{
				taxonomiesCell = HeaderList.size();
				cell = getCellByPosission(taxonomiesCell, StatrtRecord);
				cell.setText("Taxonomies");
				HeaderList.add(cell);

				supCell = HeaderList.size();
				cell = getCellByPosission(supCell, StatrtRecord);
				cell.setText("Supplier Name");
				HeaderList.add(cell);
			}
			PartCell = HeaderList.size();
			cell = getCellByPosission(PartCell, StatrtRecord);
			cell.setText("Part Number");
			HeaderList.add(cell);
			familyCell = HeaderList.size();
			cell = getCellByPosission(familyCell, StatrtRecord);
			cell.setText("Family");
			HeaderList.add(cell);
			if("Semiconductor".equals(plType))
			{
				famCrossCellNo = HeaderList.size();
				cell = getCellByPosission(famCrossCellNo, StatrtRecord);
				cell.setText("Family Cross");
				HeaderList.add(cell);
				genericCellNo = HeaderList.size();
				cell = getCellByPosission(genericCellNo, StatrtRecord);
				cell.setText("Generic");
				HeaderList.add(cell);
			}
			maskCellNo = HeaderList.size();
			cell = getCellByPosission(HeaderList.size(), StatrtRecord);
			cell.setText("Mask");
			HeaderList.add(cell);
			if(NPIFlag)
			{
				npiCellNo = HeaderList.size();
				cell = getCellByPosission(HeaderList.size(), StatrtRecord);
				cell.setText("NPI");
				HeaderList.add(cell);
				newsCellNo = HeaderList.size();
				cell = getCellByPosission(HeaderList.size(), StatrtRecord);
				cell.setText("News Link");
				HeaderList.add(cell);
			}
			// convPartCellNo = HeaderList.size();
			// cell = getCellByPosission(convPartCellNo, StatrtRecord);
			// cell.setText("Conversion Part");
			// HeaderList.add(cell);
			pdfCellNo = HeaderList.size();
			cell = getCellByPosission(pdfCellNo, StatrtRecord);
			cell.setText("PDF URL");
			HeaderList.add(cell);
			startParametricFT = HeaderList.size();

		}catch(Exception ex)
		{
			ex.printStackTrace();
			// AppContext.FirMessageError(ex.getMessage(), this.getClass(), ex);
		}
	}

	public void setDevHeader(boolean newPdf, boolean isQA)
	{
		try
		{
			String pdfUrl = "";
			String desc = "";
			// if (trackingParametric != null) {
			// pdfUrl = trackingParametric.getDocument().getPdf().getSeUrl();
			// desc = trackingParametric.getDocument().getTitle();
			// }
			List<FeatureDTO> plfets = ParaQueryUtil.getPlFeautres(sheetpl, !isQA);

			setMainHeaders(newPdf);
			this.endParametricFT = HeaderList.size() + plfets.size();
			System.out.println("Pl Features:" + plfets.size());
			for(FeatureDTO featureDTO : plfets)
			{
				int startCol = HeaderList.size();
				Cell cell = getCellByPosission(startCol, StatrtRecord);
				cell.setText(featureDTO.getFeatureName());
				if(featureDTO.getUnit() != null)
				{
					// System.out.println(fvaluesdto.getPlFeature().getUnit());
					String uint = featureDTO.getUnit();
					Cell cellunit = getCellByPosission(startCol, StatrtRecord - 1);
					cellunit.setText(uint);
				}

				List<String> appValues = featureDTO.getFeatureapprovedvalue();
				approvedFeatuer.put(featureDTO.getFeatureName(), appValues);
				// System.out.println(featureDTO.getFeatureName() + " AppValues size=" + appValues.size());
				if(!isQA)
					cell.SetApprovedValues(appValues, getCellRangByPosission(startCol, RowSelectedRange));
				else
					cell.SetApprovedValues(null, getCellRangByPosission(startCol, RowSelectedRange));

				HeaderList.add(cell);
			}
			Cell cell = getCellByPosission(HeaderList.size(), StatrtRecord);
			cell.setText("Description");
			HeaderList.add(cell);
			if(!isQA)
				setValidationHeaders();
			// setPdfAndTitle(pdfUrl, desc,2);
			int lastColNum = HeaderList.size() + 4;
			String lastColumn = getColumnName(lastColNum);
			String hdrUintRange = "A" + 1 + ":" + lastColumn + 2;
			xHdrUnitrange = sheet.getCellRangeByName(hdrUintRange);
			// setRangColor(xHdrUnitrange, 0xB0AEAE);
			setRangProtected(xHdrUnitrange, 0xB0AEAE);
			// setExtractionData(trackingParametric, 2);
		}catch(Exception ex)
		{
			ex.printStackTrace();
			// AppContext.FirMessageError(ex.getMessage(), this.getClass(), ex);
		}

	}

	public ArrayList<String> getHeader()
	{
		ArrayList<String> header = new ArrayList<String>();
		if(HeaderList != null)
		{
			for(int i = 0; i < HeaderList.size(); i++)
			{
				Cell cell = HeaderList.get(i);
				try
				{
					header.add(cell.getText());
				}catch(Exception e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return header;
	}

	public void setReviewHeader(List additionalCols, boolean isQA)
	{
		try
		{
			// Pl pl, String pdfUrl
			// Pl pl = trackingParametric.getPl();
			// List<FeatureDTO> plfets = ParaQueryUtil.getPlFeautres(pl, false);

			HeaderList = new ArrayList<Cell>();
			Cell cell = getCellByPosission(0, StatrtRecord);
			cell.setText("Taxonomy");
			HeaderList.add(cell);
			cell = getCellByPosission(1, StatrtRecord);
			cell.setText("Eng Name");
			HeaderList.add(cell);
			cell = getCellByPosission(2, StatrtRecord);
			cell.setText("Status");
			HeaderList.add(cell);
			cell = getCellByPosission(3, StatrtRecord);
			cell.setText("Comment");
			HeaderList.add(cell);
			cell = getCellByPosission(4, StatrtRecord);
			cell.setText("Task Type");
			HeaderList.add(cell);
			cell = getCellByPosission(5, StatrtRecord);
			cell.setText("Supplier Name");
			HeaderList.add(cell);
			setDevHeader(false, isQA);
			if(additionalCols != null)
			{
				int startCol = HeaderList.size();
				for(int i = 0; i < additionalCols.size(); i++)
				{
					cell = getCellByPosission(startCol + i, StatrtRecord);
					cell.setText(additionalCols.get(i).toString());
					HeaderList.add(cell);
				}
			}
			statusValues.add("Approved");
			statusValues.add("Rejected");
			if(!isQA)
				statusValues.add("Updated");
			// setMainHeaders();
			// System.out.println("Pl Features:" + plfets.size());
			//
			// for (FeatureDTO featureDTO : plfets) {
			// int currentCol = HeaderList.size();
			// cell = getCellByPosission(currentCol, StatrtRecord);
			// cell.setText(featureDTO.getFeatureName());
			// if (featureDTO.getUnit() != null) {
			// String uint = featureDTO.getUnit();
			// Cell cellunit = getCellByPosission(currentCol, StatrtRecord - 1);
			// cellunit.setText(uint);
			// }
			// List<String> appValues = featureDTO.getFeatureapprovedvalue();
			// ApprovedFeatuer.put(featureDTO.getFeatureName(), appValues);
			// System.out.println(featureDTO.getFeatureName() + " AppValues size=" + appValues.size());
			// cell.SetApprovedValues(appValues, sheet.getCellRangeByPosition(currentCol,));
			// HeaderList.add(cell);
			// // currentCol++;
			// }
			// setValidationHeaders();
			// int lastColNum = HeaderList.size() + 4;
			// String lastColumn = getColumnName(lastColNum);
			// String hdrUintRange = "A" + 1 + ":" + lastColumn + 2;
			// xHdrUnitrange = sheet.getCellRangeByName(hdrUintRange);
			// setRangColor(xHdrUnitrange, 0xB0AEAE);

		}catch(Exception ex)
		{
			ex.printStackTrace();
			// AppContext.FirMessageError(ex.getMessage(), this.getClass(), ex);
		}

	}

	public void updateApprovedValues()
	{
		try
		{
			approvedFeatuer.clear();
			for(int j = startParametricFT; j < HeaderList.size(); j++)
			{
				XCell fetCell = xHdrUnitrange.getCellByPosition(j, 1);
				String fetName = getCellText(fetCell).getString();
				List<String> appValues = ParaQueryUtil.getGroupFullValueByFeatureNameAndPl(fetName, selectedPL);
				approvedFeatuer.put(fetName, appValues);
				System.out.println(fetName + " New  AppValues size=" + appValues.size());
				// cell.SetApprovedValues(appValues, getCellRangByPosission(j, RowSelectedRange));
			}
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}

	}

	public void setPdfInfo(String url, String supplierName, String desc, String newsLink, String taxonomies, int rowNum)
	{
		Cell cell;
		try
		{
			if(newsLink != null)
			{
				cell = getCellByPosission(newsCellNo, rowNum);
				cell.setText(newsLink);
			}

			cell = getCellByPosission(pdfCellNo, rowNum);
			cell.setText(url);
			cell = getCellByPosission(supCell, rowNum);
			cell.setText(supplierName);
			cell = getCellByPosission(descriptionColumn, rowNum);
			cell.setText(desc);
			cell = getCellByPosission(taxonomiesCell, rowNum);
			cell.setText(taxonomies);

		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public String getSelectedCellValue()
	{
		try
		{
			SheetViewData SVD = new SheetViewData();
			// SVD.setViewdata(sheetPanel.aBean.getController().getViewData() + "");

			// XModel ccc = (XModel) sheetPanel.aBean.getController().getModel().getCurrentSelection();
			// System.out.println("selected are"+sheetPanel.aBean.getController().getViewData()+"  ,  "+ccc);
			int rowSelected = SVD.getSelectedCellposesions().y;
			System.out.println(SVD.getSelectedCellposesions());
			Cell cell = getCellByPosission(SVD.getSelectedCellposesions().x, SVD.getSelectedCellposesions().y);
			String txt = cell.getText().trim();
			System.out.println("" + SVD.getSelectedCellposesions().x + " : " + SVD.getSelectedCellposesions().y + " : " + txt);
			return txt;
		}catch(Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
	}

	protected void setValidationHeaders()
	{
		try
		{
			int startCol = HeaderList.size();
			Cell cell = getCellByPosission(HeaderList.size(), StatrtRecord);
			// cell.setText("Description");
			// descriptionColumn = HeaderList.size();
			// HeaderList.add(cell);
			cell = getCellByPosission(HeaderList.size(), StatrtRecord);
			cell.setText("VAL Status");
			valStatusColumn = HeaderList.size();
			HeaderList.add(cell);
			cell = getCellByPosission(HeaderList.size(), StatrtRecord);
			cell.setText("VAL Comment");
			valCommentColumn = HeaderList.size();
			HeaderList.add(cell);
			cell = getCellByPosission(HeaderList.size(), StatrtRecord);
			cell.setText("VAL Taxonomy");
			valTaxonomyColumn = HeaderList.size();
			HeaderList.add(cell);
			valHeaderSize = 4;
		}catch(Exception ex)
		{
			ex.printStackTrace();
			// AppContext.FirMessageError(ex.getMessage(), this.getClass(), ex);
		}
	}

	protected void setRangColor(XCellRange xcellrange, Integer color)
	{
		try
		{
			com.sun.star.beans.XPropertySet xCellrangPropSet = (com.sun.star.beans.XPropertySet) UnoRuntime.queryInterface(com.sun.star.beans.XPropertySet.class, xcellrange);
			// 0x99CCCC
			xCellrangPropSet.setPropertyValue("CellBackColor", color);
		}catch(Exception ex)
		{
			ex.printStackTrace();
			// AppContext.FirMessageError(ex.getMessage(), this.getClass(), ex);
		}

	}

	public void setCellColore(XCell cell, Integer colore)
	{
		try
		{
			XPropertySet xCellProps = (XPropertySet) UnoRuntime.queryInterface(XPropertySet.class, cell);
			XPropertySetInfo prpset = xCellProps.getPropertySetInfo();
			xCellProps.setPropertyValue("CellBackColor", colore);
			// for (Property prop : prpset.getProperties()) {
			// System.out.println(prop.Name);
			// }
		}catch(Exception ex)
		{
			ex.printStackTrace();
			// AppContext.FirMessageError(ex.getMessage(), this.getClass(), ex);
		}
	}

	protected void setRangProtected(XCellRange xcellrange, Integer color)
	{
		try
		{
			com.sun.star.beans.XPropertySet xCellrangPropSet = (com.sun.star.beans.XPropertySet) UnoRuntime.queryInterface(com.sun.star.beans.XPropertySet.class, xcellrange);
			XPropertySetInfo prpset = xCellrangPropSet.getPropertySetInfo();
			// for (Property prop : prpset.getProperties()) {
			// System.out.println(prop.Name);
			// }

			// Any an = (Any) xCellrangPropSet.getPropertyValue("CellProtection");
			CellProtection s = new CellProtection();
			s.IsLocked = true;
			// s.IsHidden
			// com.sun.star.beans.XPropertySet xProtectPropSet = (com.sun.star.beans.XPropertySet) an.getObject();
			// xProtectPropSet.setPropertyValue("IsLocked", true);
			//

			xCellrangPropSet.setPropertyValue("CellBackColor", color);
			xCellrangPropSet.setPropertyValue("CellProtection", s);

		}catch(Exception ex)
		{
			ex.printStackTrace();
			// AppContext.FirMessageError(ex.getMessage(), this.getClass(), ex);
		}

	}

	public XText getCellText(XCell cell)
	{
		XText cellText = null;
		try
		{
			cellText = (XText) UnoRuntime.queryInterface(XText.class, cell);
		}catch(Exception ex)
		{
			ex.printStackTrace();
			// AppContext.FirMessageError(ex.getMessage(), this.getClass(), ex);
		}
		return cellText;
	}

	protected String getCulmn(int i)
	{
		StringBuffer buf = new StringBuffer();
		System.out.println(i);
		culmns = new Properties();
		culmns.setProperty("5", "5");
		// culmns.
		do
		{
			if(i < 0)
				break;
			if(i <= 26)
			{
				String s = culmns.getProperty("" + 5);
				System.out.println(s);
				buf.append(culmns.getProperty("" + i));
				break;
			}

			int l = Math.abs(i / 26);
			buf.append(culmns.getProperty("" + l));
			i = i - (l * 26);
			if(i <= 26)
				buf.append(culmns.getProperty("" + i));

		}while(i >= 26);
		System.out.println(buf.toString());
		return buf.toString();

	}

	public int getLastRow()
	{
		int last = 0;
		XCellRange xcellrange = null;
		try
		{
			xcellrange = sheet.getCellRangeByName("A1:A1000000");
		}catch(Exception e)
		{

		}
		int index = 1;
		while(true)
		{
			try
			{
				XCell cell = xcellrange.getCellByPosition(0, index);
				String celldata = getCellText(cell).getString();
				if(celldata.equals(""))
				{
					break;
				}
				System.out.println(celldata);
				index++;

			}catch(Exception e)
			{
				e.printStackTrace();
				break;
			}

		}
		return index;
	}

	public ArrayList<ArrayList<String>> getUnApprovedValues(ArrayList<ArrayList<String>> result)
	{
		int lastRow = (getLastRow() + 2);
		String fetUnit = "";
		String pdfUrl = "";
		boolean missed[] = new boolean[lastRow - 2];
		boolean notApproved[] = new boolean[lastRow - 2];
		XCellRange xcellrange = null;
		XCellRange partRange = null;
		ArrayList<String> row = new ArrayList<String>();
		ArrayList<String> mapRow = null;
		// XCell partCell = null;
		String lastColumn = getColumnName(HeaderList.size() + 1);
		String partColumn = getColumnName(PartCell + 1);
		boolean flag = true;
		String part = "";
		LinkedHashSet fetValue = null;
		try
		{

			sheet.getCellByPosition(0, 0).setFormula("");
			xcellrange = sheet.getCellRangeByName("A" + 2 + ":" + lastColumn + lastRow);
			partRange = sheet.getCellRangeByName("A3:A" + lastRow);
			partRange = sheet.getCellRangeByName(partColumn + "3:" + partColumn + lastRow);
			// return sheet.getCellRangeByPosition(x, StatrtRecord + 1, x, y);

			// for (int record = 0; record < lastRow - 2; record++) {

			// = getCellText(partCell).getString();
			for(int i = startParametricFT; i < endParametricFT; i++)
			{
				row = new ArrayList<String>();
				List<String> approved = null;
				String fetName = "";
				fetValue = new LinkedHashSet();

				for(int j = 0; j < lastRow - 1; j++)
				{
					XCell cell = null;
					try
					{
						if(j == 0)
						{
							Cell unitCell = (Cell) getCellByPosission(i, j);
							if(unitCell.getText() != null)
							{
								fetUnit = unitCell.getText().toString();
							}
							cell = xcellrange.getCellByPosition(i, j);
							fetName = getCellText(cell).getString();
							System.out.println("Fet Name:" + fetName);
							approved = (List<String>) approvedFeatuer.get(fetName);
						}
						else
						{
							// partCell = partRange.getCellByPosition(PartCell, j - 1);
							// part = getCellText(partCell).getString();
							Cell partCell = (Cell) getCellByPosission(PartCell, j + 1);
							if(partCell.getText() != null)
							{
								part = partCell.getText().toString();
							}

							Cell pdfCell = (Cell) getCellByPosission(pdfCellNo, j + 1);
							if(pdfCell.getText() != null)
							{
								pdfUrl = pdfCell.getText().toString();
							}
							cell = xcellrange.getCellByPosition(i, j);
							String celldata = getCellText(cell).getString().trim();
							if(!celldata.equals(""))
							{
								DD: for(int k = 0; k < approved.size(); k++)
								{
									if(celldata.equals(approved.get(k).trim()) && !celldata.equals(""))
									{
										flag = false;
										break DD;
									}
								}
								if(flag)
								{

									if(fetValue.add(celldata))
									{
										if(celldata.equals(""))
										{
											missed[j - 1] = true;
										}
										else
										{
											notApproved[j - 1] = true;
											row = new ArrayList<String>();
											row.add(selectedPL);
											row.add(part);
											row.add(pdfUrl);
											row.add(fetName);
											row.add(celldata);
											XCell fetUnitCell = xHdrUnitrange.getCellByPosition(i, 0);
											String unit = getCellText(fetUnitCell).getString();
											row.add(unit);
											result.add(row);
										}
									}
								}
								flag = true;
							}
						}

					}catch(Exception ex)
					{
						ex.printStackTrace();
						System.out.println(ex.getMessage());
					}
				}

			}
			// }
		}catch(Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	public ArrayList<ArrayList<String>> getSeparationValues(int rowNumber)
	{
		ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
		ArrayList<String> row = null;
		XCellRange xcellrange = null;
		try
		{
			xcellrange = sheet.getCellRangeByName("G" + 2 + ":L" + (rowNumber));

			for(int i = 0; i < rowNumber - 1; i++)
			{
				row = new ArrayList<String>();
				System.out.println("pdf cell is " + pdfCellNo);

				Cell fullFeatureCell = getCellByPosission(4, i + 1);
				String fullFeature = fullFeatureCell.getText().trim();
				row.add(fullFeature);
				Cell pdfCell = getCellByPosission(2, i + 1);
				String pdfUrl = pdfCell.getText().trim();

				// row.add(pdfUrl);
				for(int j = 0; j < 6; j++)
				{

					XCell cell = null;
					try
					{
						cell = xcellrange.getCellByPosition(j, i);
						String cellData = getCellText(cell).getString();
						row.add(cellData);
					}catch(Exception ex)
					{
						System.out.println(ex.getMessage());
					}

				}
				row.add(pdfUrl);
				result.add(row);
			}

		}catch(Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	public ArrayList<ArrayList<String>> readSpreadsheet(int startRow)
	{
		ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
		XCellRange xcellrange = null;
		ArrayList<String> row = null;
		String lastColumn = getColumnName(HeaderList.size() + 1);
		try
		{
			int lastRow = getLastRow();
			System.out.println(" Last Cell " + lastColumn + lastRow + " header " + HeaderList.size());
			xcellrange = sheet.getCellRangeByName("A1:" + lastColumn + lastRow);
			System.out.println(" Data Range@ A" + startRow + ":" + lastColumn + lastRow);
			// return sheet.getCellRangeByPosition(x, StatrtRecord + 1, x, y);
			for(int i = startRow; i < lastRow; i++)
			{
				row = new ArrayList<String>();
				for(int j = 0; j < HeaderList.size() + 1; j++)
				{
					XCell cell = xcellrange.getCellByPosition(j, i);
					String celldata = getCellText(cell).getString();
					row.add(celldata);
					if(!celldata.isEmpty())
					{
						// System.out.println("cell " + i + "," + j + "=" + celldata);
					}
				}
				result.add(row);
			}
		}catch(Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	public ArrayList<ArrayList<String>> validatePartsOld()
	{
		ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
		XCellRange xcellrange = null;
		int lastColNum = HeaderList.size() + 4;
		String lastColumn = getColumnName(lastColNum);
		canSave = true;
		try
		{
			int lastRow = 100000;
			part: for(int i = 3; i < lastRow; i++)
			{
				String seletedRange = "A" + i + ":" + lastColumn + i;
				xcellrange = sheet.getCellRangeByName(seletedRange);
				System.out.println("Selected range " + seletedRange);

				boolean appFlag = true;
				XCell pnCell = xcellrange.getCellByPosition(PartCell, 0);
				String pn = getCellText(pnCell).getString();
				XCell suppCell = xcellrange.getCellByPosition(supCell, 0);
				String supplierName = getCellText(suppCell).getString();
				XCell famCell = xcellrange.getCellByPosition(familyCell, 0);
				String family = getCellText(famCell).getString();
				XCell maskCell = xcellrange.getCellByPosition(maskCellNo, 0);
				String mask = getCellText(maskCell).getString();
				XCell descCell = xcellrange.getCellByPosition(descriptionColumn, 0);
				String desc = getCellText(descCell).getString();

				System.out.println("Main Cells " + pn + " : " + family + " : " + mask);

				/***** validate that PN and supplier not found on component or LUT or acquisition ******/

				if(pn.isEmpty())
				{
					return result;
				}
				boolean isRejectedPN = partvalidation.isRejectedPNAndSupplier(pn, supplierName);
				if(isRejectedPN)
				{
					setCellColore(pnCell, 0xD2254D);
					writeValidtionStatus(xcellrange, false);
					canSave = false;
					continue part;
				}

				/****** validate that Family not null *****/
				if(family.isEmpty())
				{
					partvalidation.setStatus("Empty Family");
					setCellColore(famCell, 0xD2254D);
					writeValidtionStatus(xcellrange, false);
					canSave = false;
					continue part;
				}
				/**** validate that mask not null ***/
				if(mask.isEmpty())
				{
					partvalidation.setStatus("Empty Mask");
					setCellColore(maskCell, 0xD2254D);
					writeValidtionStatus(xcellrange, false);
					canSave = false;
					continue part;
				}
				/**
				 * Description Validation
				 */
				if(desc == null || desc.isEmpty())
				{
					partvalidation.setStatus("Empty Description");
					setCellColore(descCell, 0xD2254D);
					writeValidtionStatus(xcellrange, false);
					canSave = false;
					continue part;
				}
				else if(partvalidation.checkDescription(desc))
				{
					setCellColore(descCell, 0xD2254D);
					writeValidtionStatus(xcellrange, false);
					canSave = false;
					continue part;
				}
				/**** validate that Feature values are approved and Not Blank ***/
				appFlag = isRowValuesApproved(xcellrange, lastColNum - 4);
				if(!appFlag)
				{
					writeValidtionStatus(xcellrange, false);
					canSave = false;
					continue part;
				}
				writeValidtionStatus(xcellrange, true);
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return result;
	}

	public ArrayList<ArrayList<String>> validateParts(boolean update)
	{
		ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
		XCellRange xcellrange = null;
		int lastColNum = HeaderList.size();
		String lastColumn = getColumnName(lastColNum);
		canSave = true;
		try
		{
			int lastRow = 100000;
			part: for(int i = 3; i < lastRow; i++)
			{
				String seletedRange = "A" + i + ":" + lastColumn + i;
				xcellrange = sheet.getCellRangeByName(seletedRange);
				System.out.println("Selected range " + seletedRange);
				String famCross = "", generic = "";

				boolean appFlag = true;
				XCell pnCell = xcellrange.getCellByPosition(PartCell, 0);
				String pn = getCellText(pnCell).getString();
				XCell suppCell = xcellrange.getCellByPosition(supCell, 0);
				String supplierName = getCellText(suppCell).getString();
				XCell famCell = xcellrange.getCellByPosition(familyCell, 0);
				String family = getCellText(famCell).getString();
				XCell maskCell = xcellrange.getCellByPosition(maskCellNo, 0);
				String mask = getCellText(maskCell).getString();
				// PartComponent component=DataDevQueryUtil.getComponentByPartNumberAndSupplierName(pn, supplierName);

				if(plType.equals("Semiconductor"))
				{
					XCell genCell = xcellrange.getCellByPosition(genericCellNo, 0);
					XCell famCrossCell = xcellrange.getCellByPosition(famCrossCellNo, 0);
					generic = getCellText(genCell).getString();
					famCross = getCellText(famCrossCell).getString();
				}
				XCell statusCell = xcellrange.getCellByPosition(2, 0);
				String status = getCellText(statusCell).getString();
				XCell commentCell = xcellrange.getCellByPosition(3, 0);
				String comment = getCellText(commentCell).getString();
				XCell descCell = xcellrange.getCellByPosition(descriptionColumn, 0);
				String desc = getCellText(descCell).getString();

				System.out.println("Main Cells " + pn + " : " + family + " : " + mask);

				/***** validate that PN and supplier not found on component or LUT or acquisition ******/

				if(pn.isEmpty())
				{
					return result;
				}
				if(!update)
				{
					boolean isRejectedPN = partvalidation.isRejectedPNAndSupplier(pn, supplierName);
					if(isRejectedPN)
					{
						setCellColore(pnCell, 0xD2254D);
						writeValidtionStatus(xcellrange, false);
						if(partvalidation.getStatus().equals("Reject, contains unaccepted character In Part Number") || partvalidation.getStatus().equals("Reject, Found Before"))
							canSave = false;
						continue part;
					}
				}
				else
				{

					// if((status.equals("Approved") && !comment.equals("")) || (status.equals("Rejected") && comment.equals("")))
					if((status.equals("Rejected") && comment.equals("")))

					{
						partvalidation.setStatus("Wrong Comment");
						setCellColore(commentCell, 0xD2254D);
						writeValidtionStatus(xcellrange, false);
						canSave = false;
						continue part;
					}
					else if(status.equals("Approved"))// TL Must Write Comment If Approve QA or ENG when issue external
					{
						String issueSouce = ParaQueryUtil.getLastIssueSource(pn, supplierName);
						if(issueSouce != null)
						{
							if(issueSouce.equals("QA"))
							{
								if(comment.equals(""))
								{
									partvalidation.setStatus("Wrong Comment");
									setCellColore(commentCell, 0xD2254D);
									writeValidtionStatus(xcellrange, false);
									canSave = false;
									continue part;
								}
							}
							else
							{
								if(!comment.equals(""))// else sure reciver is ENg to approve it must be empty
								{
									partvalidation.setStatus("Wrong Comment");
									setCellColore(commentCell, 0xD2254D);
									writeValidtionStatus(xcellrange, false);
									canSave = false;
									continue part;
								}
							}
						}
						else
						{
							if(!comment.equals(""))// else sure reciver is ENg to approve it must be empty
							{
								partvalidation.setStatus("Wrong Comment");
								setCellColore(commentCell, 0xD2254D);
								writeValidtionStatus(xcellrange, false);
								canSave = false;
								continue part;
							}
						}
					}

				}
				if(!update || (update && status.equals("Updated")))
				{
					/****** validate that Family not null *****/
					if(family.isEmpty())
					{
						partvalidation.setStatus("Empty Family");
						setCellColore(famCell, 0xD2254D);
						writeValidtionStatus(xcellrange, false);
						canSave = false;
						continue part;
					}
					/**** validate that mask not null ***/
					if(mask.isEmpty())
					{
						partvalidation.setStatus("Empty Mask)");
						setCellColore(maskCell, 0xD2254D);
						writeValidtionStatus(xcellrange, false);
						canSave = false;
						continue part;
					}
					else if(mask.length() != pn.length())
					{
						partvalidation.setStatus("Wrong Mask Length");
						setCellColore(maskCell, 0xD2254D);
						writeValidtionStatus(xcellrange, false);
						canSave = false;
						continue part;
					}
					/**
					 * validate that generic and family Cross not null
					 */
					if(plType.equals("Semiconductor"))
					{
						XCell genCell = xcellrange.getCellByPosition(genericCellNo, 0);
						XCell famCrossCell = xcellrange.getCellByPosition(famCrossCellNo, 0);
						generic = getCellText(genCell).getString();
						famCross = getCellText(famCrossCell).getString();
						if(generic.isEmpty() || famCross.isEmpty())
						{
							partvalidation.setStatus("Empty Main columns(Generic or Family Cross)");
							setCellColore(genCell, 0xD2254D);
							setCellColore(famCrossCell, 0xD2254D);
							writeValidtionStatus(xcellrange, false);
							canSave = false;
							continue part;
						}
					}
					/**
					 * Description Validation
					 */
					if(desc == null || desc.isEmpty())
					{
						partvalidation.setStatus("Empty Description");
						setCellColore(descCell, 0xD2254D);
						writeValidtionStatus(xcellrange, false);
						canSave = false;
						continue part;
					}
					else if(partvalidation.checkDescription(desc))
					{
						setCellColore(descCell, 0xD2254D);
						writeValidtionStatus(xcellrange, false);
						canSave = false;
						continue part;
					}
					/**** validate that Feature values are approved and Not Blank ***/

					// boolean haveSpaces = fetValsHaveSpaces(xcellrange, endParametricFT);
					// if(haveSpaces)
					// {
					// canSave = false;
					// writeValidtionStatus(xcellrange, false);
					// continue part;
					// }

					appFlag = isRowValuesApproved(xcellrange, endParametricFT);
					if(!appFlag)
					{
						writeValidtionStatus(xcellrange, false);
						// canSave = false;
						continue part;
					}
				}

				writeValidtionStatus(xcellrange, true);

			}
			JOptionPane.showMessageDialog(null, "Validation Finished");
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return result;
	}

	public void saveParts(boolean update)
	{
		if(!canSave)
		{
			System.out.println("Can Save: " + canSave);
			JOptionPane.showMessageDialog(null, "can't save sheet duto some errors in your data");
			return;
		}
		try
		{
			ArrayList<ArrayList<String>> sheetData = readSpreadsheet(2);
			Set<String> pdfSet = new HashSet<String>();
			for(int i = 0; i < sheetData.size(); i++)
			{
				PartInfoDTO partInfo = new PartInfoDTO();
				ArrayList<String> partData = sheetData.get(i);
				String pn = "", supplierName = "", family, mask, pdfUrl, desc = "", famCross = null, generic = null, NPIPart = null;
				supplierName = partData.get(supCell);
				pn = partData.get(PartCell);
				if(pn.isEmpty())
				{
					return;
				}
				family = partData.get(familyCell);
				mask = partData.get(maskCellNo);
				pdfUrl = partData.get(pdfCellNo);
				desc = partData.get(descriptionColumn);
				if(plType == null)
				{
					JOptionPane.showMessageDialog(null, "Can't Load this PL Name as PL Type Not Clear");
				}
				if(plType.equals("Semiconductor"))
				{
					famCross = partData.get(famCrossCellNo);
					generic = partData.get(genericCellNo);
				}
				if(NPIFlag)
					NPIPart = partData.get(npiCellNo);
				String newsLink = partData.get(newsCellNo);
				partInfo.setNewsLink(newsLink);
				if(partData.get(valStatusColumn).equals("Reject, Found on LUT Table"))
				{
					partInfo.setFeedbackType("LUT");
				}
				else if(partData.get(valStatusColumn).equals("Reject, Found on Acquisition Table"))
				{
					partInfo.setFeedbackType("Acquisition");
				}

				partInfo.setPN(pn);
				partInfo.setSupplierName(supplierName);
				partInfo.setFamily(family);
				partInfo.setFamilycross(famCross);
				partInfo.setMask(mask);
				partInfo.setGeneric(generic);
				partInfo.setPdfUrl(pdfUrl);
				partInfo.setPlName(selectedPL);
				partInfo.setFetValues(readRowValues(partData));
				partInfo.setNPIFlag(NPIPart);
				partInfo.setDescription(desc);
				boolean save = false;
				if(!update)
				{
					try
					{
						save = DataDevQueryUtil.saveParamtric(partInfo);
					}catch(ConstraintViolationException e)
					{
						if(e.getMessage().contains("unique constraint (AUTOMATION2.PART_COMP_PART_SUPP_PL_UQ)"))
							continue;
					}

				}
				else
				{
					save = DataDevQueryUtil.updateParamtric(partInfo);
				}

				// System.out.println("Main Cells " + pn + " : " + family + " : " + mask);
				if(save)
					pdfSet.add(pdfUrl);
				else
				{
					JOptionPane.showMessageDialog(null, "Part Number Can't Save:" + pn + "\n" + pdfUrl);
					return;
				}
			}
			DataDevQueryUtil.saveTrackingParamtric(pdfSet, selectedPL, null, StatusName.tlReview);
			JOptionPane.showMessageDialog(null, "Saving Data Finished");
		}catch(Exception e)
		{
			e.printStackTrace();
		}

	}

	public void saveQAReviewAction(String QAName, String screen)
	{

		try
		{
			Set<String> rejectedPdfs = new HashSet<String>();
			Set<String> acceptedPdfs = new HashSet<String>();
			List<PartInfoDTO> feedbackParts = new ArrayList<PartInfoDTO>();
			ArrayList<String> sheetHeader = getHeader();
			// List<String> fetNames = sheetHeader.subList(startParametricFT, endParametricFT);
			ArrayList<ArrayList<String>> fileData = readSpreadsheet(2);
			String pn = "", family, mask, pdfUrl, desc = "", famCross = "", generic = "", NPIPart = null;
			for(int i = 0; i < fileData.size(); i++)
			{
				PartInfoDTO partInfo = new PartInfoDTO();
				ArrayList<String> partData = fileData.get(i);
				String status = partData.get(2);
				String comment = partData.get(3);
				String vendorName = partData.get(5);
				String plName = partData.get(0);
				String tlName = com.se.parametric.dba.ParaQueryUtil.getTeamLeaderNameByMember(partData.get(1));
				pn = partData.get(PartCell);
				pdfUrl = partData.get(pdfCellNo);
				family = partData.get(familyCell);
				mask = partData.get(maskCellNo);
				desc = partData.get(descriptionColumn);
				if(plType.equals("Semiconductor"))
				{
					famCross = partData.get(famCrossCellNo);
					generic = partData.get(genericCellNo);
				}
				if(NPIFlag)
					NPIPart = partData.get(npiCellNo);
				if(partData.get(valStatusColumn).equals("Reject, Found on LUT Table"))
				{
					partInfo.setFeedbackType("LUT");
				}
				else if(partData.get(valStatusColumn).equals("Reject, Found on Acquisition Table"))
				{
					partInfo.setFeedbackType("Acquisition");
				}

				partInfo.setPN(pn);
				partInfo.setSupplierName(vendorName);
				partInfo.setStatus(status);
				partInfo.setComment(comment);
				partInfo.setIssuedBy(QAName);
				partInfo.setIssuedTo(tlName);
				partInfo.setPlName(selectedPL);
				partInfo.setNPIFlag(NPIPart);
				partInfo.setDescription(desc);
				partInfo.setPdfUrl(pdfUrl);
				partInfo.setFamily(family);
				partInfo.setFamilycross(famCross);
				partInfo.setMask(mask);
				partInfo.setGeneric(generic);

				if("Rejected".equals(status))
				{
					if("".equals(comment))
					{
						System.out.println("Comment shouldn't be null");
						JOptionPane.showMessageDialog(null, "Comment can not be empty for rejected parts", "Saving Not Done", JOptionPane.ERROR_MESSAGE);
						return;
					}
					else
					{
						partInfo.setFeedBackStatus(StatusName.reject);
						partInfo.setFeedBackCycleType(StatusName.wrongData);
						feedbackParts.add(partInfo);
						if(acceptedPdfs.contains(pdfUrl))
						{
							acceptedPdfs.remove(pdfUrl);
						}
						rejectedPdfs.add(pdfUrl);
					}

				}
				else if("Approved".equals(status))
				{
					if(screen.equals("FB"))
					{
						partInfo.setFeedBackStatus(StatusName.fbClosed);
						feedbackParts.add(partInfo);
					}
					if(!rejectedPdfs.contains(pdfUrl))
					{
						acceptedPdfs.add(pdfUrl);
					}

				}
			}
			// DataDevQueryUtil.saveQAPartsFeedback(feedbackParts, "Wrong Data", "Rejected","QA");
			DataDevQueryUtil.saveQAPartsFeedback(feedbackParts, "QA");
			DataDevQueryUtil.saveTrackingParamtric(acceptedPdfs, selectedPL, null, StatusName.cmTransfere);
			DataDevQueryUtil.saveTrackingParamtric(rejectedPdfs, selectedPL, null, StatusName.tlFeedback);
			JOptionPane.showMessageDialog(null, "Saving Data Finished");
		}catch(Exception e)
		{
			JOptionPane.showMessageDialog(null, "Can't Save Data");
			e.printStackTrace();
		}

	}

	public void saveTLReviewAction(String teamLeaderName)
	{
		if(!canSave)
		{
			System.out.println("Can Save: " + canSave);
			JOptionPane.showMessageDialog(null, "can't save sheet duto some errors in your data");
			return;
		}
		try
		{
			Set<String> rejectedPdfs = new HashSet<String>();
			Set<String> acceptedPdfs = new HashSet<String>();
			List<PartInfoDTO> feedbackParts = new ArrayList<PartInfoDTO>();
			ArrayList<String> sheetHeader = getHeader();
			// List<String> fetNames = sheetHeader.subList(startParametricFT, endParametricFT);
			ArrayList<ArrayList<String>> fileData = readSpreadsheet(2);
			String pn = "", family, mask, pdfUrl, desc = "", famCross = "", generic = "", NPIPart = null;
			for(int i = 0; i < fileData.size(); i++)
			{
				PartInfoDTO partInfo = new PartInfoDTO();
				ArrayList<String> partData = fileData.get(i);
				String status = partData.get(2);
				String comment = partData.get(3);
				String vendorName = partData.get(5);
				String plName = partData.get(0);
				String engName = partData.get(1);
				pn = partData.get(PartCell);
				pdfUrl = partData.get(pdfCellNo);
				family = partData.get(familyCell);
				mask = partData.get(maskCellNo);
				desc = partData.get(descriptionColumn);
				if(plType.equals("Semiconductor"))
				{
					famCross = partData.get(famCrossCellNo);
					generic = partData.get(genericCellNo);
				}
				if(NPIFlag)
					NPIPart = partData.get(npiCellNo);
				if(partData.get(valStatusColumn).equals("Reject, Found on LUT Table"))
				{
					partInfo.setFeedbackType("LUT");
				}
				else if(partData.get(valStatusColumn).equals("Reject, Found on Acquisition Table"))
				{
					partInfo.setFeedbackType("Acquisition");
				}

				partInfo.setPN(pn);
				partInfo.setSupplierName(vendorName);
				partInfo.setStatus(status);
				partInfo.setComment(comment);
				partInfo.setIssuedBy(teamLeaderName);
				partInfo.setIssuedTo(engName);
				partInfo.setPlName(selectedPL);
				partInfo.setNPIFlag(NPIPart);
				partInfo.setDescription(desc);
				partInfo.setPdfUrl(pdfUrl);
				partInfo.setFamily(family);
				partInfo.setFamilycross(famCross);
				partInfo.setMask(mask);
				partInfo.setGeneric(generic);

				if("Rejected".equals(status))
				{
					if("".equals(comment))
					{
						System.out.println("Comment shouldn't be null");
						JOptionPane.showMessageDialog(null, "Comment can not be empty for rejected parts", "Saving Not Done", JOptionPane.ERROR_MESSAGE);
						return;
					}
					else
					{
						partInfo.setFeedBackStatus("Rejected");
						partInfo.setFeedBackCycleType("Wrong Data");
						feedbackParts.add(partInfo);
						if(acceptedPdfs.contains(pdfUrl))
						{
							acceptedPdfs.remove(pdfUrl);// if atleast one fet is wrong whole pdf rejected
						}
						rejectedPdfs.add(pdfUrl);
					}

				}
				else if("Approved".equals(status))
				{
					if(!rejectedPdfs.contains(pdfUrl))
					{
						acceptedPdfs.add(pdfUrl);
					}

				}
				else if("Updated".equals(status))
				{
					// // List<String> fetVals = row.subList(10, 54);
					// List<String> fetVals = partData.subList(startParametricFT, endParametricFT);
					// Map<String, String> fetsMap = new HashMap<String, String>();
					// for (int j = 0; j < fetNames.size(); j++) {
					// String fetName = fetNames.get(j);
					// String fetVal = fetVals.get(j);
					// fetsMap.put(fetName, fetVal);
					// }
					// partInfo.setFetValues(fetsMap);
					partInfo.setFetValues(readRowValues(partData));
					DataDevQueryUtil.updateParamtric(partInfo);
					// ParaQueryUtil.updateParametricReviewData(fetNames, fetVals, partInfo);
					if(!rejectedPdfs.contains(pdfUrl))
					{
						acceptedPdfs.add(pdfUrl);
					}
				}

				// ParaQueryUtil.updateDocStatus( teamLeaderName, row);
			}
			DataDevQueryUtil.savePartsFeedback(feedbackParts, "Internal");
			DataDevQueryUtil.saveTrackingParamtric(acceptedPdfs, selectedPL, null, StatusName.qaReview);
			DataDevQueryUtil.saveTrackingParamtric(rejectedPdfs, selectedPL, null, StatusName.engFeedback);
			JOptionPane.showMessageDialog(null, "Saving Data Finished");
		}catch(Exception e)
		{
			JOptionPane.showMessageDialog(null, "Can't Save Data");
			e.printStackTrace();
		}

	}

	public void saveEngFeedbackAction(String devName)
	{
		if(!canSave)
		{
			System.out.println("Can Save: " + canSave);
			JOptionPane.showMessageDialog(null, "can't save sheet duto some errors in your data");
			return;
		}
		try
		{
			Set<String> pdfs = new HashSet<String>();
			// List<PartInfoDTO> updatedParts = new ArrayList<PartInfoDTO>();
			// List<PartInfoDTO> rejectedParts = new ArrayList<PartInfoDTO>();
			List<PartInfoDTO> feedBackParts = new ArrayList<PartInfoDTO>();
			ArrayList<String> sheetHeader = getHeader();
			List<String> fetNames = sheetHeader.subList(startParametricFT, endParametricFT);
			ArrayList<ArrayList<String>> sheetData = readSpreadsheet(2);

			String pn = "", family, mask, pdfUrl, desc = "", famCross = "", generic = "", NPIPart = null;
			for(int i = 0; i < sheetData.size(); i++)
			{
				PartInfoDTO partInfo = new PartInfoDTO();
				ArrayList<String> partData = sheetData.get(i);
				String status = partData.get(2);
				String comment = partData.get(3);
				String vendorName = partData.get(5);
				String plName = partData.get(0);
				String issuedTo = partData.get(1);
				pn = partData.get(PartCell);
				pdfUrl = partData.get(pdfCellNo);
				family = partData.get(familyCell);
				mask = partData.get(maskCellNo);
				desc = partData.get(descriptionColumn);
				if(plType.equals("Semiconductor"))
				{
					famCross = partData.get(famCrossCellNo);
					generic = partData.get(genericCellNo);
				}
				if(NPIFlag)
					NPIPart = partData.get(npiCellNo);
				if(partData.get(valStatusColumn).equals("Reject, Found on LUT Table"))
				{
					partInfo.setFeedbackType("LUT");
				}
				else if(partData.get(valStatusColumn).equals("Reject, Found on Acquisition Table"))
				{
					partInfo.setFeedbackType("Acquisition");
				}

				partInfo.setPN(pn);
				partInfo.setSupplierName(vendorName);
				partInfo.setStatus(status);
				partInfo.setComment(comment);
				partInfo.setIssuedBy(devName);
				partInfo.setIssuedTo(issuedTo);
				partInfo.setPlName(selectedPL);
				partInfo.setNPIFlag(NPIPart);
				partInfo.setDescription(desc);
				partInfo.setPdfUrl(pdfUrl);
				partInfo.setFamily(family);
				partInfo.setFamilycross(famCross);
				partInfo.setMask(mask);
				partInfo.setGeneric(generic);

				if("Rejected".equals(status))
				{
					if("".equals(comment))
					{
						System.out.println("Comment shouldn't be null");
						JOptionPane.showMessageDialog(null, "Comment can not be empty for rejected parts", "Saving Not Done", JOptionPane.ERROR_MESSAGE);
						return;
					}
					else
					{
						if((issuedTo != null) && (!"".equals(issuedTo)))
						{
							partInfo.setFeedBackStatus("Rejected");
							feedBackParts.add(partInfo);
							pdfs.add(pdfUrl);
						}

					}

				}
				else if("Updated".equals(status))
				{

					// List<String> fetVals = row.subList(startParametricFT, endParametricFT);
					// Map<String, String> fetsMap = new HashMap<String, String>();
					// for (int j = 0; j < fetNames.size(); j++) {
					// String fetName = fetNames.get(j);
					// String fetVal = fetVals.get(j);
					// fetsMap.put(fetName, fetVal);
					// }
					// partInfo.setFetValues(fetsMap);
					partInfo.setFetValues(readRowValues(partData));
					if((issuedTo != null) && (!"".equals(issuedTo)))
					{
						partInfo.setFeedBackStatus("Approved");
						feedBackParts.add(partInfo);
					}

					DataDevQueryUtil.updateParamtric(partInfo);
					pdfs.add(pdfUrl);

				}

			}

			DataDevQueryUtil.savePartsFeedback(feedBackParts, "ENG");
			// DataDevQueryUtil.savePartsFeedback(updatedParts, null, "Approved","ENG");
			// DataDevQueryUtil.savePartsFeedback(rejectedParts, null, "Rejected","ENG");
			DataDevQueryUtil.saveTrackingParamtric(pdfs, selectedPL, null, StatusName.tlFeedback);
			JOptionPane.showMessageDialog(null, "Saving Data Finished");
		}catch(Exception e)
		{
			e.printStackTrace();
		}

	}

	public void saveTLFeedbackAction(String teamLeaderName)
	{/* make partsFeedBack contain Status also to call savePartsFeedback once */
		if(!canSave)
		{
			System.out.println("Can Save: " + canSave);
			JOptionPane.showMessageDialog(null, "can't save sheet duto some errors in your data");
			return;
		}
		try
		{
			Set<String> engRejectedPdfs = new HashSet<String>();
			Set<String> qARejectedAndUpdatedPdfs = new HashSet<String>();
			String feedCycleStatus = "";
			Set<String> engAcceptedPdfsInternal = new HashSet<String>();
			Set<String> engAcceptedPdfsExternal = new HashSet<String>();

			Set<String> qAAcceptedPdfs = new HashSet<String>();
			List<PartInfoDTO> partsToStoreFeedback = new ArrayList<PartInfoDTO>();
			// List<PartInfoDTO> partsToCloseFeedback = new ArrayList<PartInfoDTO>();
			// List<PartInfoDTO> partsToUpdateFeedback = new ArrayList<PartInfoDTO>();
			ArrayList<String> sheetHeader = getHeader();
			int issuerIndex = sheetHeader.indexOf("BY");
			int issueSourceIndex = sheetHeader.indexOf("Issue_Source");
			ArrayList<ArrayList<String>> fileData = readSpreadsheet(2);
			String pn = "", family, mask, pdfUrl, desc = "", famCross = null, generic = null, NPIPart = null, flowSource;
			for(int i = 0; i < fileData.size(); i++)
			{
				PartInfoDTO partInfo = new PartInfoDTO();
				ArrayList<String> partData = fileData.get(i);
				String status = partData.get(2);
				String comment = partData.get(3);
				String vendorName = partData.get(5);
				// String plName = partData.get(0);
				String issuedToEng = partData.get(1); // from eng column
				String issuerName = partData.get(issuerIndex);// from issuer column
				String issueSourceName = partData.get(issueSourceIndex);// from issuer column
				// System.err.println(issuerName);
				pn = partData.get(PartCell);
				pdfUrl = partData.get(pdfCellNo);
				family = partData.get(familyCell);
				mask = partData.get(maskCellNo);
				desc = partData.get(descriptionColumn);
				Long issueQAEngID = ParaQueryUtil.getIssueFirstSenderID(pn, vendorName);
				if(plType.equals("Semiconductor"))
				{
					famCross = partData.get(famCrossCellNo);
					generic = partData.get(genericCellNo);
				}
				if(NPIFlag)
					NPIPart = partData.get(npiCellNo);
				if(partData.get(valStatusColumn).equals("Reject, Found on LUT Table"))
				{
					partInfo.setFeedbackType("LUT");
				}
				else if(partData.get(valStatusColumn).equals("Reject, Found on Acquisition Table"))
				{
					partInfo.setFeedbackType("Acquisition");
				}

				partInfo.setPN(pn);
				partInfo.setSupplierName(vendorName);
				partInfo.setStatus(status);
				partInfo.setComment(comment);
				partInfo.setIssuedBy(teamLeaderName);
				partInfo.setIssuedTo(issuerName);
				partInfo.setPlName(selectedPL);
				partInfo.setNPIFlag(NPIPart);
				partInfo.setDescription(desc);
				partInfo.setPdfUrl(pdfUrl);
				partInfo.setFamily(family);
				partInfo.setFamilycross(famCross);
				partInfo.setMask(mask);
				partInfo.setGeneric(generic);
				partInfo.setFeedBackSource(issueSourceName);

				GrmUser issuedToUser = ParaQueryUtil.getGRMUserByName(issuedToEng);
				if(issuedToUser == null)
				{
					JOptionPane.showMessageDialog(null, "Can't Find The Reciever User");
					return;
				}
				// List<String> fetNames = sheetHeader.subList(startParametricFT, endParametricFT);
				// for (int i = 0; i < fileData.size(); i++) {
				// ArrayList<String> row = fileData.get(i);
				// String status = row.get(2);
				// String comment = row.get(3);
				// String pdfUrl = row.get(pdfCellNo);
				// String vendorName = row.get(5);
				// String plName = row.get(0);
				// String partNum = row.get(6);
				// String devName = row.get(1);
				// String familyName = row.get(7);
				//
				//
				// PartInfoDTO partInfo = new PartInfoDTO();
				// partInfo.setPN(partNum);
				// partInfo.setVendorName(vendorName);
				// partInfo.setStatus(status);
				// partInfo.setComment(comment);
				// partInfo.setIssuedBy(teamLeaderName);
				// partInfo.setIssuedTo(devName);
				// partInfo.setPlName(plName);
				// partInfo.setPdfUrl(pdfUrl);
				// partInfo.setFamily(familyName);
				// // partInfo.setIssuerName(issuerName);

				if("Rejected".equals(status))
				{
					partInfo.setFeedBackStatus("Rejected");
					partInfo.setFeedBackCycleType("Wrong Data");
					partsToStoreFeedback.add(partInfo);

					if("".equals(comment))
					{
						System.out.println("Comment shouldn't be null");
						JOptionPane.showMessageDialog(null, "Comment can not be empty for rejected parts", "Saving Not Done", JOptionPane.ERROR_MESSAGE);
						return;
					}
					else
					{
						if(issuerName.equals(issuedToEng))
						{// issuer Eng
							// sendTo="ENG";
							// partsToStoreFeedback.add(partInfo);
							if(engAcceptedPdfsInternal.contains(pdfUrl))
							{
								engAcceptedPdfsInternal.remove(pdfUrl);
							}
							engRejectedPdfs.add(pdfUrl);
						}
						else
						{ // issuer QA
							// sendTo="QA";
							// partsToStoreFeedback.add(partInfo);
							if(qAAcceptedPdfs.contains(pdfUrl))
							{
								qAAcceptedPdfs.remove(pdfUrl);
							}
							qARejectedAndUpdatedPdfs.add(pdfUrl);
						}

					}

				}
				else if("Approved".equals(status))
				{

					if(issuerName.equals(issuedToEng))
					{// issuer Eng
						if(!engRejectedPdfs.contains(pdfUrl))
						{
							if(partInfo.getFeedBackSource().equals("QA"))
							{
								partInfo.setFeedBackStatus("Wrong Data");
								engAcceptedPdfsExternal.add(pdfUrl);
							}
							else
							{
								partInfo.setFeedBackStatus("Feedback Closed");
								engAcceptedPdfsInternal.add(pdfUrl);
							}

						}
						if((issuerName != null) && !("".equals(issuerName)))
						{

							if(issueSourceName.equals("QA"))
								partInfo.setIssuedTo(ParaQueryUtil.getGRMUser(issueQAEngID).getFullName());
							partsToStoreFeedback.add(partInfo);
						}
					}
					else
					{// issuer qa
						if(!qARejectedAndUpdatedPdfs.contains(pdfUrl))
						{
							qAAcceptedPdfs.add(pdfUrl);
						}
						if((issuerName != null) && !("".equals(issuerName)))
						{
							partInfo.setFeedBackStatus("Wrong Data");
							partInfo.setIssuedTo(issuedToEng);// source sure external (QA)
							partsToStoreFeedback.add(partInfo);
						}
					}
				}
				else if("Updated".equals(status))
				{
					/*********** update ************/
					if(issuerName.equals(issuedToEng))
					{// issuer Eng
						partInfo.setFetValues(readRowValues(partData));
						DataDevQueryUtil.updateParamtric(partInfo);
						if(!engRejectedPdfs.contains(pdfUrl))
						{
							engAcceptedPdfsInternal.add(pdfUrl);
						}
						if((issuerName != null) && !("".equals(issuerName)))
						{
							partInfo.setFeedBackStatus("Feedback Closed");
							partsToStoreFeedback.add(partInfo);
						}
					}
					else
					{// issuer qa
						/*
						 * case Updated as Rejected r the Same in (Status : qa Feedback) coz if it's in rejected must no
						 */
						partInfo.setFetValues(readRowValues(partData));
						DataDevQueryUtil.updateParamtric(partInfo);
						// if(!qARejectedAndUpdatedPdfs.contains(pdfUrl))
						// {
						// qAUpdatedPdfs.add(pdfUrl);
						// }
						qARejectedAndUpdatedPdfs.add(pdfUrl);
						if((issuerName != null) && !("".equals(issuerName)))
						{
							partInfo.setFeedBackStatus(StatusName.approved);
							partsToStoreFeedback.add(partInfo);
						}
					}
				}
			}
			// if(issu)
			// DataDevQueryUtil.savePartsFeedback(partsToStoreFeedback, "Wrong Data", "Rejected","TL");
			// DataDevQueryUtil.savePartsFeedback(partsToRejectFeedback, "Wrong Data", "Rejected","TL");
			// DataDevQueryUtil.savePartsFeedback(partsToCloseFeedback, null, "Feedback Closed","TL");
			DataDevQueryUtil.savePartsFeedback(partsToStoreFeedback, "Internal");
			DataDevQueryUtil.saveTrackingParamtric(engAcceptedPdfsInternal, selectedPL, null, StatusName.qaReview);// update ,approve Eng
			DataDevQueryUtil.saveTrackingParamtric(engAcceptedPdfsExternal, selectedPL, null, StatusName.qaFeedback);// update ,approve Eng
			DataDevQueryUtil.saveTrackingParamtric(engRejectedPdfs, selectedPL, null, StatusName.engFeedback);

			/** QA is the reciever **/
			DataDevQueryUtil.saveTrackingParamtric(qARejectedAndUpdatedPdfs, selectedPL, null, StatusName.qaFeedback);
			DataDevQueryUtil.saveTrackingParamtric(qAAcceptedPdfs, selectedPL, null, StatusName.engFeedback);
			JOptionPane.showMessageDialog(null, "Saving Data Finished");
		}catch(Exception e)
		{
			e.printStackTrace();
		}

	}

	// public static void main(String arg[]) {
	// int num = 0;
	// for (char c = 'A'; c <= 'Z'; ++c) {
	// chars[num] = c;
	// num++;
	// }
	// for (int i = 1; i < 105; i++) {
	// System.out.println(getColumnName(i));
	// }
	// }

	private boolean isRowValuesApproved(XCellRange xcellrange, int lastColNum) throws IndexOutOfBoundsException
	{
		boolean appFlag = true;
		List<String> paraData = new ArrayList<String>();
		String missedFet = "", needApp = "", space = "";
		for(int j = startParametricFT; j < lastColNum; j++)
		{
			XCell fetCell = xHdrUnitrange.getCellByPosition(j, 1);
			String fetName = getCellText(fetCell).getString();
			XCell cell = xcellrange.getCellByPosition(j, 0);
			String celldata = getCellText(cell).getString();
			System.out.println("cell " + j + "=" + celldata);
			if(celldata.isEmpty())
			{
				// partvalidation.setStatus("Missed Feature");
				missedFet = "Missed Feature";
				setCellColore(cell, 0xD2254D);
				appFlag = false;
			}
			else if(!celldata.equals(celldata.trim()))
			{
				setCellColore(cell, 0xF6771D);
				appFlag = false;
				space = "Some Values start or End with space";
				canSave = false;
			}
			else
			{
				paraData.add(fetName);
				List<String> app = approvedFeatuer.get(fetName);
				if(app != null)
				{
					if(!app.contains(celldata))
					{
						// partvalidation.setStatus("Need Approved");
						needApp = "Need Approved";
						setCellColore(cell, 0x4BA3F0);
						appFlag = false;
						canSave = false;
					}
					else
					{
						setCellColore(cell, 0xFFFFCC);
					}
				}
			}
		}
		partvalidation.setStatus(needApp + "|" + space + "|" + missedFet);
		if(paraData.isEmpty())
		{
			partvalidation.setStatus("Part Must Has at Least on Feature");
			appFlag = false;
			canSave = false;
		}

		return appFlag;
	}

	private boolean fetValsHaveSpaces(XCellRange xcellrange, int lastColNum) throws IndexOutOfBoundsException
	{
		boolean haveSpaces = false;

		String validationMessage = "";
		for(int j = startParametricFT; j < lastColNum; j++)
		{
			XCell cell = xcellrange.getCellByPosition(j, 0);
			String celldata = getCellText(cell).getString();
			System.out.println("cell " + j + "=" + celldata);

			if(celldata != null)
			{

				if(!celldata.equals(celldata.trim()))
				{
					setCellColore(cell, 0xB87070);
					haveSpaces = true;
					validationMessage = "Some Fets have leading or trailing spaces";
				}
			}

		}
		partvalidation.setStatus(validationMessage);
		return haveSpaces;
	}

	private void setExtractionData(TrackingParametric trac, int pdfRow)
	{

		try
		{
			Map<String, List<String>> partsData = ParaQueryUtil.getExtractorData(trac.getDocument().getPdf(), trac.getSupplier(), trac.getPl());
			Set<String> parts = partsData.keySet();
			for(String part : parts)
			{
				getCellByPosission(0, pdfRow).setText(part);
				;
				List<String> fetsVal = partsData.get(part);

				int idx = -1;
				for(String fet : fetsVal)
				{
					String[] fetVal = fet.split("\\$");
					for(int j = startParametricFT; j < HeaderList.size() - valHeaderSize; j++)
					{
						XCell fetCell = xHdrUnitrange.getCellByPosition(j, 1);
						String fetName = getCellText(fetCell).getString();
						if(fetName.equals(fetVal[0]))
						{
							idx = j;
							break;
						}
					}
					// int idx = HeaderList.indexOf(fetVal[0]);
					System.out.println(fetVal[0] + " Cell position :" + idx + " ," + pdfRow);
					if(idx != -1)
					{
						Cell cell = getCellByPosission(idx, pdfRow);
						cell.setText(fetVal[1]);
					}

				}
				pdfRow++;
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}

	}

	public void setExtractionData1(String pdf, String supplierName, String plName, int pdfRow)
	{

		try
		{
			// Pdf pdf = ParaQueryUtil.getPdfBySeUrl(pdfUrl);
			Document doc = ParaQueryUtil.getDocumnetByPdfUrl(pdf);
			Pl pl = ParaQueryUtil.getPlByPlName(plName);
			Supplier supplier = ParaQueryUtil.getSupplierByName(supplierName);
			// Map<String, List<String>> partsData = ParametricQueryUtil.getExtractorData(pdfUrl, supplierName, plName);
			Map<String, List<String>> partsData = ParaQueryUtil.getExtractorData(doc.getPdf(), supplier, pl);
			Set<String> parts = partsData.keySet();
			for(String part : parts)
			{
				getCellByPosission(PartCell, pdfRow).setText(part);
				List<String> fetsVal = partsData.get(part);

				int idx = -1;
				for(String fet : fetsVal)
				{
					String[] fetVal = fet.split("\\$");
					for(int j = startParametricFT; j < HeaderList.size() - valHeaderSize; j++)
					{
						XCell fetCell = xHdrUnitrange.getCellByPosition(j, 1);
						String fetName = getCellText(fetCell).getString();
						if(fetName.equals(fetVal[0]))
						{
							idx = j;
							break;
						}
					}
					// int idx = HeaderList.indexOf(fetVal[0]);
					System.out.println(fetVal[0] + " Cell position :" + idx + " ," + pdfRow);
					if(idx != -1)
					{
						Cell cell = getCellByPosission(idx, pdfRow);
						cell.setText(fetVal[1]);
					}

				}
				pdfRow++;
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}

	}

	private Map<String, String> readRowValues(XCellRange xcellrange, int lastColNum) throws IndexOutOfBoundsException
	{

		Map<String, String> fetValues = new HashMap<String, String>();
		for(int j = startParametricFT; j < lastColNum; j++)
		{
			XCell fetCell = xHdrUnitrange.getCellByPosition(j, 1);
			String fetName = getCellText(fetCell).getString();
			XCell cell = xcellrange.getCellByPosition(j, 0);
			String celldata = getCellText(cell).getString();
			System.out.println("cell " + j + "=" + celldata);
			fetValues.put(fetName, celldata);
		}
		return fetValues;
	}

	private Map<String, String> readRowValues(ArrayList<String> partData) throws IndexOutOfBoundsException
	{

		Map<String, String> fetValues = new HashMap<String, String>();
		for(int j = startParametricFT; j < endParametricFT; j++)
		{
			XCell fetCell = xHdrUnitrange.getCellByPosition(j, 1);
			String fetName = getCellText(fetCell).getString();
			String fetvalue = partData.get(j);
			System.out.println(fetName + " cell " + j + "=" + fetvalue);
			fetValues.put(fetName, fetvalue);
		}
		return fetValues;
	}

	private void writeValidtionStatus(XCellRange xcellrange, boolean flag) throws IndexOutOfBoundsException
	{
		if(flag)
		{
			getCellText(xcellrange.getCellByPosition(valCommentColumn, 0)).setString("-");
			getCellText(xcellrange.getCellByPosition(valStatusColumn, 0)).setString("No Problem");
			getCellText(xcellrange.getCellByPosition(valTaxonomyColumn, 0)).setString("-");
			setRangColor(xcellrange, 0x088A0D);
			// setCellColore(cell, 0xF22B5A);
		}
		else
		{

			getCellText(xcellrange.getCellByPosition(valCommentColumn, 0)).setString(partvalidation.getComment());
			getCellText(xcellrange.getCellByPosition(valStatusColumn, 0)).setString(partvalidation.getStatus());
			getCellText(xcellrange.getCellByPosition(valTaxonomyColumn, 0)).setString(partvalidation.getTaxonomy());
			// setRangColor(xcellrange, 0xD2254D);
			// setCellColore(cell, 0xF22B5A);
		}

	}

	public String getColumnName(int columnNumber)
	{
		int first = (columnNumber) / 26;
		int last = (columnNumber) % 26;
		String result = "";
		if(last == 0)
		{
			if(first == 1)
			{
				result += chars[25];
			}
			else
			{
				result += chars[first - 2];
				result += chars[25];
			}
		}
		else
		{
			if(first != 0)
			{
				result += chars[first - 1];
			}
			if(last != 0)
			{
				result += chars[last - 1];
			}
		}
		return result;
	}

	private Thread getShowAllThread()
	{
		return new Thread() {
			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				showAllHeader();
				showAllData();
			}
		};
	}

	public void getShowAllData(ArrayList<TableInfoDTO> list)
	{
		showAllHeader();
		showAllData(list);
	}

	protected void showAllData()
	{
		// TODO Auto-generated method stub
		Session session = null;
		try
		{
			int startrow = StatrtRecord;
			session = SessionUtil.getSession();
			Cell cell;
			// AppContext.getAllNewDataSheets();
			if(AppContext.AllnewDocuments != null)
				for(TrackingParametric doc : AppContext.AllnewDocuments)
				{
					Document document = (Document) session.load(Document.class, doc.getDocument().getId());
					try
					{
						cell = getCellByPosission(0, startrow);
						cell.setText(clientutils.getSeUrlByDocument(document, session));
						cell = getCellByPosission(1, startrow);
						cell.setText(clientutils.getSupplierNameByDocument(document, session));
						cell = getCellByPosission(2, startrow);
						cell.setText(clientutils.getPdfTaxonomiesString(doc, session));
						cell = getCellByPosission(3, startrow);
						cell.setText(clientutils.getDataSheetFlagsbyDocument(document, session));
						cell = getCellByPosission(4, startrow);
						cell.setText(clientutils.getDeliveryDateByDocument(document, session));
						cell = getCellByPosission(5, startrow);
						cell.setText(clientutils.getIntroductionDateByDocument(document, session));
						cell = getCellByPosission(6, startrow);
						cell.setText(document.getTitle() == null ? "" : document.getTitle());
						cell = getCellByPosission(7, startrow);
						String s;
						cell.setText((s = clientutils.getSupplierUrlByDocument(document).getUrl()) == null ? "" : s);
					}catch(Exception e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					startrow++;
				}
			try
			{
				cell = getCellByPosission(8, startrow);
				cell.SetApprovedValues(prepShowAllStatusList(), getCellRangByPosission(8, AppContext.AllnewDocuments.size(), StatrtRecord));
				cell = getCellByPosission(9, startrow);
				cell.SetApprovedValues(prepShowAllCommentList(), getCellRangByPosission(9, AppContext.AllnewDocuments.size(), StatrtRecord));
			}catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}catch(Exception ex)
		{
			ex.printStackTrace();

		}finally
		{
			if(session != null)
				session.close();
		}

	}

	protected void showAllData(ArrayList<TableInfoDTO> list)
	{

		try
		{
			int startrow = StatrtRecord;
			Cell cell;
			System.out.println("Show All List size:" + list.size());
			Date sd = new Date();
			for(int i = 0; i < list.size(); i++)
			{
				if(i % 100 == 0)
					System.out.println("Rec. No:" + startrow);
				TableInfoDTO docInfoDTO = list.get(i);
				// String pdfUrl = docInfoDTO.getPdfUrl();
				// Document document = ParaQueryUtil.getDocumnetByPdfUrl(pdfUrl);
				cell = getCellByPosission(0, startrow);
				cell.setText("document.getPdf().getSeUrl()");
				cell.setText(docInfoDTO.getPdfUrl());
				cell = getCellByPosission(1, startrow);
				cell.setText(docInfoDTO.getSupplierName());
				cell = getCellByPosission(2, startrow);
				// String supplierUrl = ParaQueryUtil.getSupplierUrlByDocument(document);
				// cell.setText(supplierUrl);
				cell.setText(docInfoDTO.getSupplierSiteUrl());
				cell = getCellByPosission(3, startrow);
				cell.setText(docInfoDTO.getPlName());
				cell = getCellByPosission(4, startrow);
				cell.setText(docInfoDTO.getTaskType());
				cell = getCellByPosission(5, startrow);
				// String date = document.getPdf().getDownloadDate().toString();
				cell.setText(docInfoDTO.getDownloadDate());
				cell = getCellByPosission(6, startrow);
				// date = document.getPdf().getCerDate().toString();
				cell.setText(docInfoDTO.getCerDate());
				cell = getCellByPosission(7, startrow);
				// cell.setText(document.getTitle() == null ? "" : document.getTitle());
				cell.setText(docInfoDTO.getTitle());
				cell = getCellByPosission(8, startrow);
				// String onloneLink = ParaQueryUtil.getOnlineLinkByDocument(document);
				// cell.setText(onloneLink);
				cell.setText(docInfoDTO.getOnlineLink());
				cell = getCellByPosission(9, startrow);
				// long pagesNo = document.getPdf().getPageCount();
				cell.setText("" + docInfoDTO.getPagesCount());
				cell = getCellByPosission(10, startrow);
				cell.setText(docInfoDTO.getExtracted());
				cell = getCellByPosission(11, startrow);
				// String newsLink = ParaQueryUtil.getNewsLink(document.getPdf().getId());
				cell.setText(docInfoDTO.getNewsLink());
				cell = getCellByPosission(12, startrow);
				// String taxPath = ParaQueryUtil.getTaxonomyPath(document.getPdf().getId());
				cell.setText(docInfoDTO.getTaxPath());

				// String url = documgetSupplierPl().getSupplier().getSiteUrl();
				// System.out.println("url"+row.getPdf().getSupplierUrl().getUrl());
				// String s = supplierUrl.getUrl();

				cell = getCellByPosission(13, startrow);
				cell.SetApprovedValues(prepShowAllStatusList(), getCellRangByPosission(13, list.size(), startrow));
				cell = getCellByPosission(14, startrow);
				cell.SetApprovedValues(prepShowAllCommentList(), getCellRangByPosission(14, list.size(), startrow));
				cell = getCellByPosission(15, startrow);
				cell.SetApprovedValues(prepShowAllTaxonomies(), getCellRangByPosission(15, list.size(), startrow));

				startrow++;
			}
			Date ed = new Date();
			System.out.println("~~~~ Time : ~~~~ " + sd + "  ****  " + ed);
		}catch(Exception ex)
		{
			ex.printStackTrace();

		}

	}

	private List<String> prepShowAllStatusList()
	{
		List<String> lststring = new ArrayList<String>();
		lststring.add("Wrong Revision");
		lststring.add("Wrong Taxonomy");
		lststring.add("Rejected");
		lststring.add("Not Available Data");
		lststring.add("Suplier Taxonomy Issue");

		return lststring;

	}

	private List<String> prepShowAllCommentList()
	{
		List<String> lstString = new ArrayList<String>();
		lstString.add("Documentation");
		lstString.add("Datasheet has Price");
		lstString.add("Broken Link");
		lstString.add("No order Information");
		lstString.add("Not Complete DS");
		lstString.add("Wrong Vendor");
		lstString.add("Acquired Vendor");

		return lstString;
	}

	private List<String> prepShowAllTaxonomies()
	{
		List<String> lstString = new ArrayList<String>();
		if(allPlNames == null)
		{
			allPlNames = ParaQueryUtil.getAllPlNames();
		}
		// for(int i = 0; i < allPlNames.size(); i++)
		// {
		// lstString.add(allPlNames.get(i));
		// }
		return allPlNames;
	}

	private void removeCellRangByPosission(int right)
	{
		try
		{
			XCellRangeAddressable xcellRangeAddressable = (com.sun.star.sheet.XCellRangeAddressable) UnoRuntime.queryInterface(com.sun.star.sheet.XCellRangeAddressable.class, sheet.getCellRangeByPosition(0, 0, (int) right, RowSelectedRange));
			xMovement.removeRange(xcellRangeAddressable.getRangeAddress(), CellDeleteMode.ROWS);
		}catch(Exception ex)
		{
			ex.printStackTrace();
			// AppContext.FirMessageError(ex.getMessage(), this.getClass(), ex);
		}
	}

	public void setUnapprovedHeader(ArrayList<String> header)
	{
		try
		{
			// setMainHeaders();
			HeaderList = new ArrayList<Cell>();
			for(int i = 0; i < header.size(); i++)
			{
				Cell cell = getCellByPosission(i, 0);
				cell.setText(header.get(i));
				HeaderList.add(cell);
			}

			String hdrUintRange = "A" + 1 + ":L" + 1;
			xHdrUnitrange = sheet.getCellRangeByName(hdrUintRange);
			setRangColor(xHdrUnitrange, 0x8A8C8B);
			hdrUintRange = "A" + 2 + ":F" + 500;
			xHdrUnitrange = sheet.getCellRangeByName(hdrUintRange);
			setRangColor(xHdrUnitrange, 0xEAAFD6);
			hdrUintRange = "G" + 2 + ":L" + 500;
			xHdrUnitrange = sheet.getCellRangeByName(hdrUintRange);
			setRangColor(xHdrUnitrange, 0xB0F2C4);

			hdrUintRange = "M" + 1 + ":N" + 1;
			xHdrUnitrange = sheet.getCellRangeByName(hdrUintRange);
			setRangColor(xHdrUnitrange, 0xCB3D30);
			
			hdrUintRange = "O" + 1 + ":R" + 1;
			xHdrUnitrange = sheet.getCellRangeByName(hdrUintRange);
			setRangColor(xHdrUnitrange, 0xe9d9c8);
			
			hdrUintRange = "O" + 1 + ":R" + 1;
			xHdrUnitrange = sheet.getCellRangeByName(hdrUintRange);
			setRangColor(xHdrUnitrange, 0x0067e7);
			hdrUintRange = "S" + 1 + ":Y" + 1;
			xHdrUnitrange = sheet.getCellRangeByName(hdrUintRange);
			setRangColor(xHdrUnitrange, 0x6a9c53);
			hdrUintRange = "Z" + 1 + ":Z" + 1;
			xHdrUnitrange = sheet.getCellRangeByName(hdrUintRange);
			setRangColor(xHdrUnitrange, 0x6e7998);
		}catch(Exception ex)
		{
			ex.printStackTrace();

		}

	}

	public void setSeparationHeader(ArrayList<String> header)
	{
		try
		{
			// setMainHeaders();
			HeaderList = new ArrayList<Cell>();
			for(int i = 0; i < header.size(); i++)
			{
				Cell cell = getCellByPosission(i, 0);
				cell.setText(header.get(i));
				HeaderList.add(cell);
			}
			String hdrUintRange = "A" + 1 + ":L" + 1;
			xHdrUnitrange = sheet.getCellRangeByName(hdrUintRange);
			setRangColor(xHdrUnitrange, 0x8A8C8B);
			hdrUintRange = "A" + 2 + ":F" + 500;
			xHdrUnitrange = sheet.getCellRangeByName(hdrUintRange);
			setRangColor(xHdrUnitrange, 0xEAAFD6);
			hdrUintRange = "G" + 2 + ":L" + 500;
			xHdrUnitrange = sheet.getCellRangeByName(hdrUintRange);
			setRangColor(xHdrUnitrange, 0xB0F2C4);

		}catch(Exception ex)
		{
			ex.printStackTrace();

		}

	}

	public void sendFeedbackToSourcingTeam(String userName)
	{
		ArrayList<ArrayList<String>> sheetData = readSpreadsheet(1);
		ArrayList<String> header = getHeader();
		int pdfUrlIdx = header.indexOf("PDF URL");
		int plIdx = header.indexOf("Taxonomies");
		int statusIdx = header.indexOf("Status");
		int commentIdx = header.indexOf("Comment");
		int rightTaxIdx = header.indexOf("Right Taxonomy");
		try
		{

			// Two cells to indicate if the feedback has been sent or rejected
			// and a comment if the feedback has been rejected
			int feedbackStatusIdx = header.size();
			int feedbackCommentIdx = feedbackStatusIdx + 1;
			Cell cell = getCellByPosission(feedbackStatusIdx, 0);
			cell.setText("SRC Feedback Status");
			cell = getCellByPosission(feedbackCommentIdx, 0);
			cell.setText("SRC Feedback Comment");
			for(int i = 0; i < sheetData.size(); i++)
			{
				ArrayList<String> rowData = sheetData.get(i);
				String pdfUrl = rowData.get(pdfUrlIdx);
				String plName = rowData.get(plIdx);
				String status = rowData.get(statusIdx);
				String comment = rowData.get(commentIdx);
				String rightTax = rowData.get(rightTaxIdx);
				String revUrl = null, docFeedbackComment = null;
				if((status == null) || ("".equals(status)))
				{
					continue;
				}
				if(pdfUrl.length() < 7 || !"http://".equalsIgnoreCase(pdfUrl.substring(0, 7)))
				{
					cell = getCellByPosission(feedbackStatusIdx, i + 1);
					cell.setText("Invalid");
					cell = getCellByPosission(feedbackCommentIdx, i + 1);
					cell.setText("Invalid PDF URL");
					continue;
				}
				if("Wrong Revision".equals(status))
				{
					try
					{
						String sub = comment.substring(0, 7);
						if(!"http://".equalsIgnoreCase(sub))
						{
							cell = getCellByPosission(feedbackStatusIdx, i + 1);
							cell.setText("Invalid");
							cell.setCellColore(0xD2254D);
							cell = getCellByPosission(feedbackCommentIdx, i + 1);
							cell.setText("Comment Should start with htt://");
							continue;
						}
						revUrl = comment;
						docFeedbackComment = "Wrong Revision";
						rightTax = null;
					}catch(Exception ex)
					{
						cell = getCellByPosission(feedbackStatusIdx, i + 1);
						cell.setText("Invalid");
						cell.setCellColore(0xD2254D);
						cell = getCellByPosission(feedbackCommentIdx, i + 1);
						cell.setText("Comment Should start with htt://");
						continue;
					}
				}
				else if("Not Available Data".equals(status))
				{

					if((comment == null) || "".equals(comment))
					{
						docFeedbackComment = "Not Available Data";
						revUrl = null;
						rightTax = null;
					}
					else
					{
						cell = getCellByPosission(feedbackStatusIdx, i + 1);
						cell.setText("Invalid");
						cell.setCellColore(0xD2254D);
						cell = getCellByPosission(feedbackCommentIdx, i + 1);
						cell.setText("Comment Should Be Empty");
						continue;
					}
				}
				else if("Wrong Taxonomy".equals(status))
				{
					if("".equals(rightTax))
					{
						cell = getCellByPosission(feedbackStatusIdx, i + 1);
						cell.setText("Invalid");
						cell.setCellColore(0xD2254D);
						cell = getCellByPosission(feedbackCommentIdx, i + 1);
						cell.setText("Right Taxonomy Should Contain the Suggested Taxonomy");
						continue;
					}

					docFeedbackComment = "Wrong tax";
					revUrl = null;
				}
				else if("Suplier Taxonomy Issue".equals(status))
				{
					if((comment == null) || "".equals(comment))
					{
						docFeedbackComment = "Wrong tax";
						rightTax = "Suplier Taxonomy Issue";
						revUrl = null;
					}
					else
					{
						cell = getCellByPosission(feedbackStatusIdx, i + 1);
						cell.setText("Invalid");
						cell.setCellColore(0xD2254D);
						cell = getCellByPosission(feedbackCommentIdx, i + 1);
						cell.setText("Comment Should Be Empty");
						continue;
					}

				}
				else if("Rejected".equals(status))
				{
					if("".equals(comment))
					{
						cell = getCellByPosission(feedbackStatusIdx, i + 1);
						cell.setText("Invalid");
						cell.setCellColore(0xD2254D);
						cell = getCellByPosission(feedbackCommentIdx, i + 1);
						cell.setText("Comment Should be Documentation, Datasheet has Price, Broken Link, No order Information, Not Complete DS, Wrong Vendor, Acquired Vendor");
						continue;
					}
					docFeedbackComment = comment;
					rightTax = null;
					revUrl = null;
				}
				else
				{
					cell = getCellByPosission(feedbackStatusIdx, i + 1);
					cell.setText("Feedback Not Sent");
					cell.setCellColore(0xD2254D);
					cell = getCellByPosission(feedbackCommentIdx, i + 1);
					cell.setText("Wrong Status Name,please select a right Status");
					continue;
				}

				String feedbackStatus = DataDevQueryUtil.sendFeedbackToSourcingTeam(userName, pdfUrl, plName, docFeedbackComment, revUrl, rightTax);
				if("Done".equals(feedbackStatus))
				{
					cell = getCellByPosission(feedbackStatusIdx, i + 1);
					cell.setText("Feedback Sent");
					cell.setCellColore(0x006600);
					cell = getCellByPosission(feedbackCommentIdx, i + 1);
					cell.setText("");
				}
				else
				{
					cell = getCellByPosission(feedbackStatusIdx, i + 1);
					cell.setText("Feedback Not Sent");
					cell.setCellColore(0xD2254D);
					cell = getCellByPosission(feedbackCommentIdx, i + 1);
					cell.setText(feedbackStatus);
				}
			}
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}

	protected XCellRange getCellRangByPosission(int x, int y) throws Exception
	{
		return sheet.getCellRangeByPosition(x, StatrtRecord + 1, x, y);
	}

	protected XCellRange getCellRangByPosission(int x, int y, int startrecord) throws Exception
	{
		return sheet.getCellRangeByPosition(x, startrecord, x, y);
	}

	public Cell getCellByPosission(int x, int y) throws Exception
	{

		return new Cell(sheet.getCellByPosition(x, y), x, y);
	}

	private int getSheetindex()
	{
		return sheetindex;
	}

	private void setSheetindex(int sheetindex)
	{
		this.sheetindex = sheetindex;
	}

	private boolean isSheetvisiblity()
	{
		return sheetvisiblity;
	}

	private void setSheetvisiblity(boolean sheetvisiblity)
	{
		this.sheetvisiblity = sheetvisiblity;
	}

	public XSpreadsheet getSheet()
	{
		return sheet;
	}

	private String getSheetname()
	{
		return sheetname;
	}

	private Pl getSheetpl()
	{
		return sheetpl;
	}

	// public String getSupplierName()
	// {
	// return supplierName;
	// }
	//
	// public void setSupplierName(String supplierName)
	// {
	// this.supplierName = supplierName;
	// }

	public boolean isNPIFlag()
	{
		return NPIFlag;
	}

	public void setNPIflag(boolean nPIflag)
	{
		NPIFlag = nPIflag;
	}

	public Map<String, List<String>> getApprovedFeatuer()
	{
		return approvedFeatuer;
	}

	public void setApprovedFeatuer(Map<String, List<String>> approvedFeatuer)
	{
		this.approvedFeatuer = approvedFeatuer;
	}

	public int getEndParametricFT()
	{
		return endParametricFT;
	}

	public void setEndParametricFT(int endParametricFT)
	{
		this.endParametricFT = endParametricFT;
	}

	// public void saveQAFeedBackAction(String QAName)
	// {
	//
	// try
	// {
	// Set<String> rejectedPdfs = new HashSet<String>();
	// Set<String> acceptedPdfs = new HashSet<String>();
	// List<PartInfoDTO> feedbackParts = new ArrayList<PartInfoDTO>();
	// ArrayList<String> sheetHeader = getHeader();
	// // List<String> fetNames = sheetHeader.subList(startParametricFT, endParametricFT);
	// ArrayList<ArrayList<String>> fileData = readSpreadsheet(2);
	// String pn = "", family, mask, pdfUrl, desc = "", famCross = "", generic = "", NPIPart = null;
	// for(int i = 0; i < fileData.size(); i++)
	// {
	// PartInfoDTO partInfo = new PartInfoDTO();
	// ArrayList<String> partData = fileData.get(i);
	// String status = partData.get(2);
	// String comment = partData.get(3);
	// String vendorName = partData.get(5);
	// String plName = partData.get(0);
	// String engName = partData.get(1);
	// pn = partData.get(PartCell);
	// pdfUrl = partData.get(pdfCellNo);
	// family = partData.get(familyCell);
	// mask = partData.get(maskCellNo);
	// desc = partData.get(descriptionColumn);
	// if(plType.equals("Semiconductor"))
	// {
	// famCross = partData.get(famCrossCellNo);
	// generic = partData.get(genericCellNo);
	// }
	// if(NPIFlag)
	// NPIPart = partData.get(npiCellNo);
	// if(partData.get(valStatusColumn).equals("Reject, Found on LUT Table"))
	// {
	// partInfo.setFeedbackType("LUT");
	// }
	// else if(partData.get(valStatusColumn).equals("Reject, Found on Acquisition Table"))
	// {
	// partInfo.setFeedbackType("Acquisition");
	// }
	//
	// partInfo.setPN(pn);
	// partInfo.setSupplierName(vendorName);
	// partInfo.setStatus(status);
	// partInfo.setComment(comment);
	// partInfo.setIssuedBy(QAName);
	// partInfo.setIssuedTo(engName);
	// partInfo.setPlName(selectedPL);
	// partInfo.setNPIFlag(NPIPart);
	// partInfo.setDescription(desc);
	// partInfo.setPdfUrl(pdfUrl);
	// partInfo.setFamily(family);
	// partInfo.setFamilycross(famCross);
	// partInfo.setMask(mask);
	// partInfo.setGeneric(generic);
	//
	// if("Rejected".equals(status))
	// {
	// if("".equals(comment))
	// {
	// System.out.println("Comment shouldn't be null");
	// JOptionPane.showMessageDialog(null, "Comment can not be empty for rejected parts", "Saving Not Done", JOptionPane.ERROR_MESSAGE);
	// return;
	// }
	// else
	// {
	// feedbackParts.add(partInfo);
	// if(acceptedPdfs.contains(pdfUrl))
	// {
	// acceptedPdfs.remove(pdfUrl);
	// }
	// rejectedPdfs.add(pdfUrl);
	// }
	//
	// }
	// else if("Approved".equals(status))
	// {
	// if(!rejectedPdfs.contains(pdfUrl))
	// {
	// acceptedPdfs.add(pdfUrl);
	// }
	//
	// }
	// // else if("Updated".equals(status))
	// // {
	// // // // List<String> fetVals = row.subList(10, 54);
	// // // List<String> fetVals = partData.subList(startParametricFT, endParametricFT);
	// // // Map<String, String> fetsMap = new HashMap<String, String>();
	// // // for (int j = 0; j < fetNames.size(); j++) {
	// // // String fetName = fetNames.get(j);
	// // // String fetVal = fetVals.get(j);
	// // // fetsMap.put(fetName, fetVal);
	// // // }
	// // // partInfo.setFetValues(fetsMap);
	// // partInfo.setFetValues(readRowValues(partData));
	// // ParaQueryUtil.updateParamtric(partInfo);
	// // // ParaQueryUtil.updateParametricReviewData(fetNames, fetVals, partInfo);
	// // if(!rejectedPdfs.contains(pdfUrl))
	// // {
	// // acceptedPdfs.add(pdfUrl);
	// // }
	// // }
	//
	// // ParaQueryUtil.updateDocStatus( teamLeaderName, row);
	// }
	// DataDevQueryUtil.saveQAPartsFeedback(feedbackParts, "Wrong Data", "Rejected","QA");
	// DataDevQueryUtil.saveTrackingParamtric(acceptedPdfs, selectedPL, null, "Waitting CM Transfere");
	// JOptionPane.showMessageDialog(null, "Saving Data Finished");
	// }catch(Exception e)
	// {
	// JOptionPane.showMessageDialog(null, "Can't Save Data");
	// e.printStackTrace();
	// }
	//
	// }

}
