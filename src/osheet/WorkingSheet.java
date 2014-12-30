package osheet;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.swing.JOptionPane;

import org.hibernate.Session;
import org.hibernate.exception.ConstraintViolationException;

import com.se.automation.db.SessionUtil;
import com.se.automation.db.client.dto.ComponentDTO;
import com.se.automation.db.client.dto.QAChecksDTO;
import com.se.automation.db.client.mapping.Document;
import com.se.automation.db.client.mapping.PartComponent;
import com.se.automation.db.client.mapping.Pl;
import com.se.automation.db.client.mapping.Supplier;
import com.se.automation.db.client.mapping.SupplierPl;
import com.se.automation.db.client.mapping.TrackingParametric;
import com.se.automation.db.parametric.ParametricQueryUtil;
import com.se.automation.db.parametric.RelatedFeature;
import com.se.automation.db.parametric.StatusName;
import com.se.parametric.AppContext;
import com.se.parametric.MainWindow;
import com.se.parametric.dba.ApprovedDevUtil;
import com.se.parametric.dba.DataDevQueryUtil;
import com.se.parametric.dba.ParaQueryUtil;
import com.se.parametric.dto.FeatureDTO;
import com.se.parametric.dto.PartInfoDTO;
import com.se.parametric.dto.TableInfoDTO;
import com.se.parametric.util.ClientUtil;
import com.se.parametric.util.ValidatePart;
import com.sun.star.beans.PropertyVetoException;
import com.sun.star.beans.UnknownPropertyException;
import com.sun.star.beans.XPropertySet;
import com.sun.star.beans.XPropertySetInfo;
import com.sun.star.lang.IllegalArgumentException;
import com.sun.star.lang.IndexOutOfBoundsException;
import com.sun.star.lang.WrappedTargetException;
import com.sun.star.sheet.CellDeleteMode;
import com.sun.star.sheet.ConditionOperator;
import com.sun.star.sheet.ValidationType;
import com.sun.star.sheet.XCellRangeAddressable;
import com.sun.star.sheet.XCellRangeData;
import com.sun.star.sheet.XSheetCondition;
import com.sun.star.sheet.XSpreadsheet;
import com.sun.star.table.XCell;
import com.sun.star.table.XCellRange;
import com.sun.star.text.XText;
import com.sun.star.uno.Any;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.util.CellProtection;

public class WorkingSheet
{

	protected List<Cell> HeaderList;
	protected Map<String, List<String>> approvedFeatuer = new HashMap<String, List<String>>();
	protected XSpreadsheet sheet;
	private int sheetindex;
	private String sheetname;
	private boolean sheetvisiblity;
	public Pl sheetpl;
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
	public boolean canSave = false;
	public boolean saved = false;
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
	private int newsDesCellNo;
	private int newsDateCellNo;
	private int valHeaderSize = 0;
	private boolean NPIFlag = false;
	private Properties culmns;
	static char[] chars = new char[26];
	private int endParametricFT = 0;

	SheetPanel sheetPanel;
	public List<String> statusValues = new ArrayList<String>();
	public List<String> commentValues = new ArrayList<String>();
	public ArrayList<String> npivalues = new ArrayList<String>();
	private List<String> allPlNames;
	List<String> doneFets = new ArrayList<String>();
	List<String> coreFets = new ArrayList<String>();
	List<String> codeFets = new ArrayList<String>();
	// RelatedFeature relatedfeature ;
	public ArrayList<ArrayList<String>> relatedFeature = new ArrayList<ArrayList<String>>();

	private ArrayList<Long> plFetIds = new ArrayList<Long>();

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
		String plName = sheetName.replace("/", "$");
		this.sheet = sheetPanel.NewSheetByName(plName, 0);

	}

	public WorkingSheet(SheetPanel sheetPanel, String sheetPlName, int idx, boolean flag)
	{
		this.sheetPanel = sheetPanel;
		String plName = sheetPlName.replace("/", "$");
		this.sheet = sheetPanel.NewSheetByName(plName, idx);
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
		String plName = sheetPlName.replace("/", "$");
		this.sheet = sheetPanel.NewSheetByName(plName, idx);
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
		String plName = track.getPl().getName().replace("/", "$");
		this.sheet = sheetPanel.NewSheetByName(plName, index);
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
				cell.SetApprovedValues(statusValues,
						sheet.getCellRangeByPosition(column - 1, i + start, column - 1, i + start));
				if(!commentValues.isEmpty())
					cell.SetApprovedValues(commentValues,
							sheet.getCellRangeByPosition(column, i + start, column, i + start));
				// if(!npivalues.isEmpty())

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
			if(threadDrowData.getState() == Thread.State.RUNNABLE
					|| threadDrowData.getState() == Thread.State.WAITING)
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
			if(ShowAllThread.getState() == Thread.State.RUNNABLE
					|| ShowAllThread.getState() == Thread.State.WAITING)
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
				// setvalues
				npivalues = new ArrayList<>();
				npivalues.add("Yes");
				npivalues.add("No");
				cell.SetApprovedValues(npivalues, sheet.getCellRangeByPosition(npiCellNo,
						StatrtRecord + 1, npiCellNo, StatrtRecord + 1));
				HeaderList.add(cell);
				newsCellNo = HeaderList.size();
				cell = getCellByPosission(HeaderList.size(), StatrtRecord);
				cell.setText("News Link");
				HeaderList.add(cell);
				newsDesCellNo = HeaderList.size();
				cell = getCellByPosission(HeaderList.size(), StatrtRecord);
				cell.setText("News Desc.");
				HeaderList.add(cell);
				newsDateCellNo = HeaderList.size();
				cell = getCellByPosission(HeaderList.size(), StatrtRecord);
				cell.setText("News Date");
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
			List<FeatureDTO> plfets = ParaQueryUtil.getPlFeautres(sheetpl, !isQA);
			setMainHeaders(newPdf);
			this.endParametricFT = HeaderList.size() + plfets.size();
			int lastColNum = endParametricFT + 4;
			String lastColumn = getColumnName(lastColNum);
			String hdrUintRange = "A" + 1 + ":" + lastColumn + 2;
			xHdrUnitrange = sheet.getCellRangeByName(hdrUintRange);
			setRangProtected(xHdrUnitrange, 0xB0AEAE);
			System.out.println("Pl Features:" + plfets.size());

			XCell codeCell = xHdrUnitrange.getCellByPosition(0, StatrtRecord - 1);
			setCellColore(codeCell, new Integer(0xffcb05));// yellow
			Cell cellcode = getCellByPosission(0, StatrtRecord - 1);
			cellcode.setText("Code Feature");

			XCell coreCell = xHdrUnitrange.getCellByPosition(1, StatrtRecord - 1);
			setCellColore(coreCell, new Integer(0x990000));// red
			Cell cellcore = getCellByPosission(1, StatrtRecord - 1);
			cellcore.setText("Core Feature");

			XCell corecodeCell = xHdrUnitrange.getCellByPosition(2, StatrtRecord - 1);
			setCellColore(corecodeCell, new Integer(0x0000ff));// blue
			Cell cellcorecode = getCellByPosission(2, StatrtRecord - 1);
			cellcorecode.setText("Core&Code");

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
				if(featureDTO.isCode())
				{
					XCell doneCell = xHdrUnitrange.getCellByPosition(startCol, StatrtRecord - 1);
					setCellColore(doneCell, new Integer(0xffcb05));// yellow
					codeFets.add(featureDTO.getFeatureName());
				}
				if(featureDTO.isCore())
				{
					XCell doneCell = xHdrUnitrange.getCellByPosition(startCol, StatrtRecord - 1);
					setCellColore(doneCell, new Integer(0x990000));// red
					coreFets.add(featureDTO.getFeatureName());
				}
				if(featureDTO.isCore() && featureDTO.isCode())
				{
					XCell doneCell = xHdrUnitrange.getCellByPosition(startCol, StatrtRecord - 1);
					setCellColore(doneCell, new Integer(0x0000ff));// blue
					codeFets.add(featureDTO.getFeatureName());
					coreFets.add(featureDTO.getFeatureName());
				}

				List<String> appValues = featureDTO.getFeatureapprovedvalue();
				approvedFeatuer.put(featureDTO.getFeatureName(), appValues);
				// System.out.println(featureDTO.getFeatureName() + " AppValues size=" + appValues.size());
				if(!isQA)
					cell.SetApprovedValues(appValues,
							getCellRangByPosission(startCol, RowSelectedRange));
				// else
				// cell.SetApprovedValues(null, getCellRangByPosission(startCol, RowSelectedRange));

				HeaderList.add(cell);

				if(featureDTO.isDoneFlag())
				{
					XCell doneCell = xHdrUnitrange.getCellByPosition(startCol, 1);
					setCellColore(doneCell, new Integer(0x23E282));
					doneFets.add(featureDTO.getFeatureName());
				}
				plFetIds.add(featureDTO.getPlFetId());
			}
			Cell cell = getCellByPosission(HeaderList.size(), StatrtRecord);
			cell.setText("Description");
			descriptionColumn = HeaderList.size();
			HeaderList.add(cell);
			if(!isQA)
				setValidationHeaders();

		}catch(Exception ex)
		{
			ex.printStackTrace();
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
			HeaderList = new ArrayList<Cell>();
			Cell cell = getCellByPosission(0, StatrtRecord);
			cell.setText("Taxonomy");
			HeaderList.add(cell);

			cell = getCellByPosission(1, StatrtRecord);
			cell.setText("Feedback Type");
			HeaderList.add(cell);

			cell = getCellByPosission(2, StatrtRecord);
			cell.setText("Issued By");
			HeaderList.add(cell);

			cell = getCellByPosission(3, StatrtRecord);
			cell.setText("Status");
			HeaderList.add(cell);

			cell = getCellByPosission(4, StatrtRecord);
			cell.setText("Comment");
			HeaderList.add(cell);

			cell = getCellByPosission(5, StatrtRecord);
			cell.setText("FBStatus");
			HeaderList.add(cell);

			cell = getCellByPosission(6, StatrtRecord);
			cell.setText("Wrong Features");
			HeaderList.add(cell);

			cell = getCellByPosission(7, StatrtRecord);
			cell.setText("FBComment");
			HeaderList.add(cell);

			cell = getCellByPosission(8, StatrtRecord);
			cell.setText("C_Action");
			HeaderList.add(cell);

			cell = getCellByPosission(9, StatrtRecord);
			cell.setText("P_Action");
			HeaderList.add(cell);

			cell = getCellByPosission(10, StatrtRecord);
			cell.setText("RootCause");
			HeaderList.add(cell);

			cell = getCellByPosission(11, StatrtRecord);
			cell.setText("ActionDueDate");
			HeaderList.add(cell);

			cell = getCellByPosission(12, StatrtRecord);
			cell.setText("Task Type");
			HeaderList.add(cell);
			cell = getCellByPosission(13, StatrtRecord);
			cell.setText("Supplier Name");
			HeaderList.add(cell);
			cell = getCellByPosission(14, StatrtRecord);
			cell.setText("Comid");
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

	public void setTLFBHeader(List additionalCols, boolean isQA)
	{
		try
		{
			HeaderList = new ArrayList<Cell>();
			Cell cell = getCellByPosission(0, StatrtRecord);
			cell.setText("Taxonomy");
			HeaderList.add(cell);

			cell = getCellByPosission(1, StatrtRecord);
			cell.setText("Feedback Type");
			HeaderList.add(cell);

			cell = getCellByPosission(2, StatrtRecord);
			cell.setText("Issued By");
			HeaderList.add(cell);

			cell = getCellByPosission(3, StatrtRecord);
			cell.setText("Status");
			HeaderList.add(cell);

			cell = getCellByPosission(4, StatrtRecord);
			cell.setText("Comment");
			HeaderList.add(cell);

			cell = getCellByPosission(5, StatrtRecord);
			cell.setText("FBStatus");
			HeaderList.add(cell);

			cell = getCellByPosission(6, StatrtRecord);
			cell.setText("Wrong Features");
			HeaderList.add(cell);

			cell = getCellByPosission(7, StatrtRecord);
			cell.setText("FBComment");
			HeaderList.add(cell);

			cell = getCellByPosission(8, StatrtRecord);
			cell.setText("C_Action");
			HeaderList.add(cell);

			cell = getCellByPosission(9, StatrtRecord);
			cell.setText("P_Action");
			HeaderList.add(cell);

			cell = getCellByPosission(10, StatrtRecord);
			cell.setText("RootCause");
			HeaderList.add(cell);

			cell = getCellByPosission(11, StatrtRecord);
			cell.setText("ActionDueDate");
			HeaderList.add(cell);

			cell = getCellByPosission(12, StatrtRecord);
			cell.setText("Task Type");
			HeaderList.add(cell);
			cell = getCellByPosission(13, StatrtRecord);
			cell.setText("Supplier Name");
			HeaderList.add(cell);
			cell = getCellByPosission(14, StatrtRecord);
			cell.setText("Comid");
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
			statusValues.add("Updated");
			statusValues.add("Approved Eng.");
			statusValues.add("Wrong Data");
			statusValues.add("Reject QA");
			statusValues.add("Accept QA & Forward");
			// statusValues.add("Approved");
			// statusValues.add("Rejected");

		}catch(Exception ex)
		{
			ex.printStackTrace();
		}

	}

	public void setTLReviewHeader(List additionalCols, boolean isQA)
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
			cell = getCellByPosission(6, StatrtRecord);
			cell.setText("Comid");
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

	public void setQAReviewHeader(List additionalCols, boolean isQA)
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
			cell.setText("Pl Type");
			HeaderList.add(cell);

			cell = getCellByPosission(2, StatrtRecord);
			cell.setText("Eng Name");
			HeaderList.add(cell);
			// cell = getCellByPosission(3, StatrtRecord);
			// cell.setText("Status");
			// HeaderList.add(cell);
			// cell = getCellByPosission(4, StatrtRecord);
			// cell.setText("Comment");
			// HeaderList.add(cell);
			cell = getCellByPosission(3, StatrtRecord);
			cell.setText("Task Type");
			HeaderList.add(cell);
			cell = getCellByPosission(4, StatrtRecord);
			cell.setText("Supplier Name");
			HeaderList.add(cell);
			cell = getCellByPosission(5, StatrtRecord);
			cell.setText("Done Flag");
			HeaderList.add(cell);
			cell = getCellByPosission(6, StatrtRecord);
			cell.setText("Extraction Flag");
			HeaderList.add(cell);
			cell = getCellByPosission(7, StatrtRecord);
			cell.setText("Comid");
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
			statusValues.add("A");
			statusValues.add("S");
			statusValues.add("R");
			statusValues.add("W");
			statusValues.add("Fast");

		}catch(Exception ex)
		{
			ex.printStackTrace();
			// AppContext.FirMessageError(ex.getMessage(), this.getClass(), ex);
		}

	}

	public void setSummaryHeader(List additionalCols)
	{
		try
		{
			// PDFURL Online Datasheet Pl_Type PL Name No. of Parts per PDF
			// No. of Done Parts per PDF No. of parts per PL No. of Done parts per PL COM_ID Part Supplier Name Task Name Status DevUserName Date QA
			// Flag QA Comment Done Flag PN in DS Keywords in DS

			HeaderList = new ArrayList<Cell>();
			Cell cell = getCellByPosission(0, StatrtRecord);
			cell.setText("PDFURL");
			HeaderList.add(cell);
			cell = getCellByPosission(1, StatrtRecord);
			cell.setText("Online Datasheet");
			HeaderList.add(cell);
			cell = getCellByPosission(2, StatrtRecord);
			cell.setText("Pl Type");
			HeaderList.add(cell);
			cell = getCellByPosission(3, StatrtRecord);
			cell.setText("PL Name");
			HeaderList.add(cell);
			cell = getCellByPosission(4, StatrtRecord);
			cell.setText("PdfParts");
			HeaderList.add(cell);
			cell = getCellByPosission(5, StatrtRecord);
			cell.setText("PdfDoneParts");
			HeaderList.add(cell);
			cell = getCellByPosission(6, StatrtRecord);
			cell.setText("PlParts");
			HeaderList.add(cell);
			cell = getCellByPosission(7, StatrtRecord);
			cell.setText("PlDoneParts");
			HeaderList.add(cell);
			cell = getCellByPosission(8, StatrtRecord);
			cell.setText("COM_ID");
			HeaderList.add(cell);
			cell = getCellByPosission(9, StatrtRecord);
			cell.setText("Part");
			HeaderList.add(cell);
			cell = getCellByPosission(10, StatrtRecord);
			cell.setText("Supplier Name");
			HeaderList.add(cell);
			cell = getCellByPosission(11, StatrtRecord);
			cell.setText("Task Name");
			HeaderList.add(cell);
			cell = getCellByPosission(12, StatrtRecord);
			cell.setText("DevUserName");
			HeaderList.add(cell);
			cell = getCellByPosission(13, StatrtRecord);
			cell.setText("Date");
			HeaderList.add(cell);
			cell = getCellByPosission(14, StatrtRecord);
			cell.setText("Sample QA Flag");
			HeaderList.add(cell);
			cell = getCellByPosission(15, StatrtRecord);
			cell.setText("Final QA Flag");
			HeaderList.add(cell);
			cell = getCellByPosission(16, StatrtRecord);
			cell.setText("QA Comment");
			HeaderList.add(cell);
			cell = getCellByPosission(17, StatrtRecord);
			cell.setText("Done Flag");
			HeaderList.add(cell);
			cell = getCellByPosission(18, StatrtRecord);
			cell.setText("PN in DS");
			HeaderList.add(cell);
			cell = getCellByPosission(19, StatrtRecord);
			cell.setText("Keywords in DS");
			HeaderList.add(cell);

			// QA Flag QA Comment Done Flag PN in DS Keywords in DS
			// setDevHeader(false, true);
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
			int lastColNum = HeaderList.size();
			String lastColumn = getColumnName(lastColNum);
			String hdrUintRange = "A" + 1 + ":" + lastColumn + 2;
			xHdrUnitrange = sheet.getCellRangeByName(hdrUintRange);
			// setRangColor(xHdrUnitrange, 0xB0AEAE);
			setRangProtected(xHdrUnitrange, 0xB0AEAE);

			statusValues.add("A");
			statusValues.add("S");
			statusValues.add("Fast");

		}catch(Exception ex)
		{
			ex.printStackTrace();
		}

	}

	public void setPdfInfo(String url, String supplierName, String desc, List<String> newsData,
			String taxonomies, int rowNum)
	{
		Cell cell;
		try
		{
			if(newsData != null && !newsData.isEmpty())
			{
				cell = getCellByPosission(newsCellNo, rowNum);
				cell.setText(newsData.get(0));
				cell = getCellByPosission(newsDesCellNo, rowNum);
				cell.setText(newsData.get(1));
				cell = getCellByPosission(newsDateCellNo, rowNum);
				cell.setText(newsData.get(2));
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
			Cell cell = getCellByPosission(SVD.getSelectedCellposesions().x,
					SVD.getSelectedCellposesions().y);
			String txt = cell.getText().trim();
			System.out.println("" + SVD.getSelectedCellposesions().x + " : "
					+ SVD.getSelectedCellposesions().y + " : " + txt);
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
			com.sun.star.beans.XPropertySet xCellrangPropSet = (com.sun.star.beans.XPropertySet) UnoRuntime
					.queryInterface(com.sun.star.beans.XPropertySet.class, xcellrange);
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
			XPropertySet xCellProps = (XPropertySet) UnoRuntime.queryInterface(XPropertySet.class,
					cell);
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
			com.sun.star.beans.XPropertySet xCellrangPropSet = (com.sun.star.beans.XPropertySet) UnoRuntime
					.queryInterface(com.sun.star.beans.XPropertySet.class, xcellrange);
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
		String supplier = "";
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
			ArrayList<String> header = getHeader();
			supCell = header.indexOf("Supplier Name");
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

							Cell supplierCell = (Cell) getCellByPosission(supCell, j + 1);
							if(supplierCell.getText() != null)
							{
								supplier = supplierCell.getText().toString();
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
									if(celldata.equals(approved.get(k).trim())
											&& !celldata.equals(""))
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
											row.add(supplier);
											row.add("");
											row.add(fetName);
											row.add(celldata);
											XCell fetUnitCell = xHdrUnitrange.getCellByPosition(i,
													0);
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
			System.out.println(" Last Cell " + lastColumn + lastRow + " header "
					+ HeaderList.size());
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

	public ArrayList<ArrayList<String>> validateParts(boolean update)
	{
		ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
		XCellRange xcellrange = null;
		int lastColNum = HeaderList.size();
		String lastColumn = getColumnName(lastColNum);
		ArrayList<String> sheetHeader = getHeader();
		int npiIndex = sheetHeader.indexOf("NPI");
		int newsIndex = sheetHeader.indexOf("News Link");
		String plname = "";
		String seletedRange = "A" + 3 + ":" + lastColumn + 3;
		xcellrange = sheet.getCellRangeByName(seletedRange);
		System.out.println("Selected range " + seletedRange);

		boolean npihasvalue = false;
		canSave = true;
		try
		{
			XCell plCell = xcellrange.getCellByPosition(taxonomiesCell, 0);
			plname = getCellText(plCell).getString();
			Pl pl = ParaQueryUtil.getPlByPlName(plname);
			int lastRow = getLastRow();
			// edit by MG 17-11-2014
			List<FeatureDTO> plfets = ParaQueryUtil.getPlFeautres(sheetpl, true);
			approvedFeatuer.clear();
			for(FeatureDTO featureDTO : plfets)
			{
				List<String> appValues = featureDTO.getFeatureapprovedvalue();
				approvedFeatuer.put(featureDTO.getFeatureName(), appValues);
			}
			part: for(int i = 3; i < lastRow + 1; i++)
			{

				seletedRange = "A" + i + ":" + lastColumn + i;
				xcellrange = sheet.getCellRangeByName(seletedRange);
				System.out.println("Selected range " + seletedRange);
				String famCross = "", generic = "";

				if(NPIFlag)
				{
					if(npiIndex > 0)
					{
						XCell npiCell = xcellrange.getCellByPosition(npiIndex, 0);
						String npi = getCellText(npiCell).getString();
						if(!npi.isEmpty() && !npihasvalue)
						{
							npihasvalue = true;
						}
					}
				}
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
					canSave = false;
					return result;
				}
				if(!update)
				{
					boolean isRejectedPN = partvalidation.isRejectedPNAndSupplier(pn, supplierName);
					if(isRejectedPN)
					{
						setCellColore(pnCell, 0xD2254D);
						writeValidtionStatus(xcellrange, false);
						if(partvalidation.getStatus().equals(
								"Reject, contains unaccepted character In Part Number")
								|| partvalidation.getStatus().equals("Reject, Found Before"))
						{
							canSave = false;
						}
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

					else if(status.equals(""))// TL Must Write Comment If Approve QA or ENG when issue external
					{
						partvalidation.setStatus("Empty Status");
						setCellColore(statusCell, 0xD2254D);
						writeValidtionStatus(xcellrange, false);
						canSave = false;
						continue part;
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
					/**** validate that News not null ***/
					else if(NPIFlag)
					{
						XCell newsCell = xcellrange.getCellByPosition(newsIndex, 0);
						String news = getCellText(newsCell).getString();
						if(news.isEmpty())
						{
							partvalidation.setStatus("Wrong News Link");
							setCellColore(newsCell, 0xD2254D);
							writeValidtionStatus(xcellrange, false);
							canSave = false;
							continue part;
						}
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

					String relatedresult = "";
					relatedresult = RelatedFeature.getConflictRelatedFeature(pl, relatedFeature);
					if(!relatedresult.isEmpty())
					{
						partvalidation.setStatus(relatedresult);
						writeValidtionStatus(xcellrange, false);
						// canSave = false;
						continue part;
					}
					writeValidtionStatus(xcellrange, true);

				}
				if(!update || (update && !status.equals("Rejected") && !status.equals("Approved")))
				{
					appFlag = isRowValuesApproved(xcellrange, endParametricFT);
					if(!appFlag)
					{
						writeValidtionStatus(xcellrange, false);
						// canSave = false;
						// continue part;
					}
				}
			}
			if(NPIFlag && !npihasvalue && canSave)
			{
				partvalidation.setStatus("NPI Must has at least one value");
				writeValidtionStatus(xcellrange, false);
				canSave = false;
			}
			// JOptionPane.showMessageDialog(null, "Validation Finished");
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return result;
	}

	public ArrayList<ArrayList<String>> validateTLFBParts(boolean update)
	{
		ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
		XCellRange xcellrange = null;
		int lastColNum = HeaderList.size();
		String lastColumn = getColumnName(lastColNum);
		ArrayList<String> header = getHeader();
		int partcell = header.indexOf("Part Number");
		int statuscellidx = header.indexOf("Status");
		int commentcellidx = header.indexOf("Comment");
		int supcell = header.indexOf("Supplier Name");
		int famcell = header.indexOf("Family");
		int maskcell = header.indexOf("Mask");
		int Taxonomyindex = header.indexOf("Taxonomy");
		int fbtypeidx = header.indexOf("Feedback Type");
		int Cactionindex = header.indexOf("C_Action");
		int Pactionindex = header.indexOf("P_Action");
		int RootcauseIndex = header.indexOf("RootCause");
		int Actionduedateindex = header.indexOf("ActionDueDate");
		int wrongfetsindex = header.indexOf("Wrong Features");
		int genericCellNoindex = header.indexOf("Generic");
		int famCrossCellNoindex = header.indexOf("Family Cross");
		int npiIndex = header.indexOf("NPI");
		boolean npihasvalue = false;
		canSave = true;
		try
		{
			int lastRow = getLastRow() + 1;
			part: for(int i = 3; i < lastRow; i++)
			{
				String seletedRange = "A" + i + ":" + lastColumn + i;
				xcellrange = sheet.getCellRangeByName(seletedRange);
				System.out.println("Selected range " + seletedRange);
				String famCross = "", generic = "";
				if(NPIFlag)
				{
					if(npiIndex > 0)
					{
						XCell npiCell = xcellrange.getCellByPosition(npiIndex, 0);
						String npi = getCellText(npiCell).getString();
						if(!npi.isEmpty() && !npihasvalue)
						{
							npihasvalue = true;
						}
					}
				}
				boolean appFlag = true;
				XCell pnCell = xcellrange.getCellByPosition(partcell, 0);
				String pn = getCellText(pnCell).getString();
				XCell suppCell = xcellrange.getCellByPosition(supcell, 0);
				String supplierName = getCellText(suppCell).getString();
				XCell famCell = xcellrange.getCellByPosition(famcell, 0);
				String family = getCellText(famCell).getString();
				XCell maskCell = xcellrange.getCellByPosition(maskcell, 0);
				String mask = getCellText(maskCell).getString();
				XCell TaxonomyCell = xcellrange.getCellByPosition(Taxonomyindex, 0);
				String Taxonomy = getCellText(TaxonomyCell).getString();
				XCell genCell = null;
				XCell famCrossCell = null;
				if(genericCellNoindex != -1)
					genCell = xcellrange.getCellByPosition(genericCellNoindex, 0);
				if(famCrossCellNoindex != -1)
					famCrossCell = xcellrange.getCellByPosition(famCrossCellNoindex, 0);
				// PartComponent component=DataDevQueryUtil.getComponentByPartNumberAndSupplierName(pn, supplierName);

				if(plType.equals("Semiconductor"))
				{

					generic = getCellText(genCell).getString();
					famCross = getCellText(famCrossCell).getString();
				}
				XCell statusCell = xcellrange.getCellByPosition(statuscellidx, 0);
				String status = getCellText(statusCell).getString();
				XCell commentCell = xcellrange.getCellByPosition(commentcellidx, 0);
				String comment = getCellText(commentCell).getString();
				XCell descCell = xcellrange.getCellByPosition(descriptionColumn, 0);
				String desc = getCellText(descCell).getString();
				XCell fbtypeCell = xcellrange.getCellByPosition(fbtypeidx, 0);
				String fbtype = getCellText(fbtypeCell).getString();

				XCell CactionCell = xcellrange.getCellByPosition(Cactionindex, 0);
				String Caction = getCellText(CactionCell).getString();
				XCell PactionCell = xcellrange.getCellByPosition(Pactionindex, 0);
				String Paction = getCellText(PactionCell).getString();
				XCell RootcauseCell = xcellrange.getCellByPosition(RootcauseIndex, 0);
				String Rootcause = getCellText(RootcauseCell).getString();
				XCell ActionduedateCell = xcellrange.getCellByPosition(Actionduedateindex, 0);
				String Actionduedate = getCellText(ActionduedateCell).getString();
				XCell wrongfetsCell = xcellrange.getCellByPosition(wrongfetsindex, 0);
				String wrongfets = getCellText(wrongfetsCell).getString();

				setCellColore(statusCell, 0xFFFFFF);
				setCellColore(commentCell, 0xFFFFFF);
				setCellColore(CactionCell, 0xFFFFFF);
				setCellColore(PactionCell, 0xFFFFFF);
				setCellColore(RootcauseCell, 0xFFFFFF);
				setCellColore(ActionduedateCell, 0xFFFFFF);
				setCellColore(pnCell, 0xFFFFFF);
				setCellColore(famCell, 0xFFFFFF);
				setCellColore(maskCell, 0xFFFFFF);
				setCellColore(fbtypeCell, 0xFFFFFF);
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
						if(partvalidation.getStatus().equals(
								"Reject, contains unaccepted character In Part Number")
								|| partvalidation.getStatus().equals("Reject, Found Before"))
							canSave = false;
						continue part;
					}
				}
				else
				{
					if(status.equals("Reject QA") && !fbtype.equals("QA"))
					{
						// JOptionPane.showMessageDialog(null, "  in row :" + (i + 1));
						partvalidation.setStatus("You Can Reject QA on QA Feedback only");
						setCellColore(statusCell, 0xD2254D);
						setCellColore(fbtypeCell, 0xD2254D);
						writeValidtionStatus(xcellrange, false);
						canSave = false;
						continue part;
					}
					if(status.equals("Accept QA & Forward") && !fbtype.equals("QA"))
					{
						// JOptionPane.showMessageDialog(null, " You Can Accept QA & Forward on QA Feedback only in row :" + (i + 1));
						partvalidation.setStatus("You Can Accept QA & Forward on QA Feedback only");
						setCellColore(statusCell, 0xD2254D);
						setCellColore(fbtypeCell, 0xD2254D);
						writeValidtionStatus(xcellrange, false);
						canSave = false;
						continue part;
					}

					if(status.equals("Wrong Data") && !fbtype.equals("Internal"))
					{
						// JOptionPane.showMessageDialog(null, " You Can set Wrong Separation on Internal Feedback only in row :" + (i + 1));
						partvalidation
								.setStatus("You Can set Wrong Data on Internal Feedback only");
						setCellColore(statusCell, 0xD2254D);
						setCellColore(fbtypeCell, 0xD2254D);
						writeValidtionStatus(xcellrange, false);
						canSave = false;
						continue part;
					}
					// if(status.equals("Approved Eng.") && !fbtype.equals("Internal"))
					// {
					// // JOptionPane.showMessageDialog(null, " You Can set Wrong Separation on Internal Feedback only in row :" + (i + 1));
					// partvalidation.setStatus("You Can set Approved Eng. on Internal Feedback only");
					// setCellColore(statusCell, 0xD2254D);
					// setCellColore(fbtypeCell, 0xD2254D);
					// writeValidtionStatus(xcellrange, false);
					// canSave = false;
					// continue part;
					// }

					if(status.equals("Updated") && fbtype.equals("QA"))
					{
						if(Caction.isEmpty() || Paction.isEmpty() || Rootcause.isEmpty()
								|| Actionduedate.isEmpty())
						{
							// JOptionPane.showMessageDialog(null,
							// " You must enter C_Action && P_Action && ROOT_Cause && Action_Due_Date when update in row :" + (i + 1));
							partvalidation
									.setStatus("You must enter C_Action && P_Action && ROOT_Cause && Action_Due_Date when update");
							setCellColore(CactionCell, 0xD2254D);
							setCellColore(PactionCell, 0xD2254D);
							setCellColore(RootcauseCell, 0xD2254D);
							setCellColore(ActionduedateCell, 0xD2254D);
							writeValidtionStatus(xcellrange, false);
							canSave = false;
							continue part;
						}
						if(!Actionduedate.isEmpty())
						{
							if(ApprovedDevUtil.isThisDateValid(Actionduedate, "DD/MM/YYYY") == false)
							{
								// JOptionPane.showMessageDialog(null, " You must enter Action_Due_Date with 'dd/MM/yyyy' fromat in row :" + (i + 1));
								setCellColore(ActionduedateCell, 0xD2254D);
								partvalidation
										.setStatus("You must enter Action_Due_Date with 'dd/MM/yyyy' fromat");
								writeValidtionStatus(xcellrange, false);
								canSave = false;
								continue part;
							}
						}
					}

					// if((status.equals("Approved") && !comment.equals("")) || (status.equals("Rejected") && comment.equals("")))
					if((status.equals("Reject QA") || status.equals("Wrong Data"))
							&& comment.equals(""))
					{
						partvalidation.setStatus("Wrong Comment");
						setCellColore(commentCell, 0xD2254D);
						writeValidtionStatus(xcellrange, false);
						canSave = false;
						continue part;
					}
					if((status.equals("Reject QA") || status.equals("Wrong Data"))
							&& !comment.equals(""))
					{
						if(!wrongfets.isEmpty())
						{
							if(wrongfets.contains("|"))
							{
								if(comment.contains("|"))
								{
									String[] features = wrongfets.split("\\|");
									String[] comments = comment.split("\\|");
									if(features.length != comments.length)
									{
										partvalidation
												.setStatus("comment must be as count as the features");
										setCellColore(commentCell, 0xD2254D);
										writeValidtionStatus(xcellrange, false);
										canSave = false;
										continue part;
									}
									if(status.equals("Reject QA"))
									{
										for(String com : comments)
										{
											if(!com.equalsIgnoreCase("notissue")
													&& !com.equalsIgnoreCase("issue"))
											{
												partvalidation
														.setStatus("comment must be (Issue , notissue)");
												setCellColore(commentCell, 0xD2254D);
												writeValidtionStatus(xcellrange, false);
												canSave = false;
												continue part;
											}
										}
									}
								}
								else
								{
									partvalidation
											.setStatus("comment must be as count as the features");
									setCellColore(commentCell, 0xD2254D);
									writeValidtionStatus(xcellrange, false);
									canSave = false;
									continue part;
								}
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
			if(NPIFlag && !npihasvalue && canSave)
			{
				partvalidation.setStatus("NPI Must has at least one value");
				writeValidtionStatus(xcellrange, false);
				canSave = false;
			}
			// JOptionPane.showMessageDialog(null, "Validation Finished");
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return result;
	}

	public ArrayList<ArrayList<String>> validateEngFBParts(boolean update)
	{
		ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
		XCellRange xcellrange = null;
		int lastColNum = HeaderList.size();
		String lastColumn = getColumnName(lastColNum);
		ArrayList<String> header = getHeader();
		int partcell = header.indexOf("Part Number");
		int statuscellidx = header.indexOf("Status");
		int commentcellidx = header.indexOf("Comment");
		int supcell = header.indexOf("Supplier Name");
		int famcell = header.indexOf("Family");
		int maskcell = header.indexOf("Mask");
		int Taxonomyindex = header.indexOf("Taxonomy");
		int fbtypeidx = header.indexOf("Feedback Type");
		int Cactionindex = header.indexOf("C_Action");
		int Pactionindex = header.indexOf("P_Action");
		int RootcauseIndex = header.indexOf("RootCause");
		int Actionduedateindex = header.indexOf("ActionDueDate");
		int wrongfetsindex = header.indexOf("Wrong Features");
		int genericCellNoindex = header.indexOf("Generic");
		int famCrossCellNoindex = header.indexOf("Family Cross");
		int npiIndex = header.indexOf("NPI");
		boolean npihasvalue = false;
		canSave = true;
		try
		{
			int lastRow = getLastRow() + 1;
			part: for(int i = 3; i < lastRow; i++)
			{
				String seletedRange = "A" + i + ":" + lastColumn + i;
				xcellrange = sheet.getCellRangeByName(seletedRange);
				System.out.println("Selected range " + seletedRange);
				String famCross = "", generic = "";

				if(NPIFlag)
				{
					if(npiIndex > 0)
					{
						XCell npiCell = xcellrange.getCellByPosition(npiIndex, 0);
						String npi = getCellText(npiCell).getString();
						if(!npi.isEmpty() && !npihasvalue)
						{
							npihasvalue = true;
						}
					}
				}
				boolean appFlag = true;
				XCell pnCell = xcellrange.getCellByPosition(partcell, 0);
				String pn = getCellText(pnCell).getString();
				XCell suppCell = xcellrange.getCellByPosition(supcell, 0);
				String supplierName = getCellText(suppCell).getString();
				XCell famCell = xcellrange.getCellByPosition(famcell, 0);
				String family = getCellText(famCell).getString();
				XCell maskCell = xcellrange.getCellByPosition(maskcell, 0);
				String mask = getCellText(maskCell).getString();
				XCell TaxonomyCell = xcellrange.getCellByPosition(Taxonomyindex, 0);
				String Taxonomy = getCellText(TaxonomyCell).getString();
				XCell genCell = null;
				XCell famCrossCell = null;
				if(genericCellNoindex != -1)
					genCell = xcellrange.getCellByPosition(genericCellNoindex, 0);
				if(famCrossCellNoindex != -1)
					famCrossCell = xcellrange.getCellByPosition(famCrossCellNoindex, 0);
				// PartComponent component=DataDevQueryUtil.getComponentByPartNumberAndSupplierName(pn, supplierName);

				if(plType.equals("Semiconductor"))
				{

					generic = getCellText(genCell).getString();
					famCross = getCellText(famCrossCell).getString();
				}
				XCell statusCell = xcellrange.getCellByPosition(statuscellidx, 0);
				String status = getCellText(statusCell).getString();
				XCell commentCell = xcellrange.getCellByPosition(commentcellidx, 0);
				String comment = getCellText(commentCell).getString();
				XCell descCell = xcellrange.getCellByPosition(descriptionColumn, 0);
				String desc = getCellText(descCell).getString();
				XCell fbtypeCell = xcellrange.getCellByPosition(fbtypeidx, 0);
				String fbtype = getCellText(fbtypeCell).getString();

				XCell CactionCell = xcellrange.getCellByPosition(Cactionindex, 0);
				String Caction = getCellText(CactionCell).getString();
				XCell PactionCell = xcellrange.getCellByPosition(Pactionindex, 0);
				String Paction = getCellText(PactionCell).getString();
				XCell RootcauseCell = xcellrange.getCellByPosition(RootcauseIndex, 0);
				String Rootcause = getCellText(RootcauseCell).getString();
				XCell ActionduedateCell = xcellrange.getCellByPosition(Actionduedateindex, 0);
				String Actionduedate = getCellText(ActionduedateCell).getString();
				XCell wrongfetsCell = xcellrange.getCellByPosition(wrongfetsindex, 0);
				String wrongfets = getCellText(wrongfetsCell).getString();

				setCellColore(statusCell, 0xFFFFFF);
				setCellColore(commentCell, 0xFFFFFF);
				setCellColore(CactionCell, 0xFFFFFF);
				setCellColore(PactionCell, 0xFFFFFF);
				setCellColore(RootcauseCell, 0xFFFFFF);
				setCellColore(ActionduedateCell, 0xFFFFFF);
				setCellColore(pnCell, 0xFFFFFF);
				setCellColore(famCell, 0xFFFFFF);
				setCellColore(maskCell, 0xFFFFFF);
				setCellColore(fbtypeCell, 0xFFFFFF);
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
						if(partvalidation.getStatus().equals(
								"Reject, contains unaccepted character In Part Number")
								|| partvalidation.getStatus().equals("Reject, Found Before"))
							canSave = false;
						continue part;
					}
				}
				else
				{

					if(status.equals("Updated") && fbtype.equals("QA"))
					{
						if(Caction.isEmpty() || Paction.isEmpty() || Rootcause.isEmpty()
								|| Actionduedate.isEmpty())
						{
							// JOptionPane.showMessageDialog(null,
							// " You must enter C_Action && P_Action && ROOT_Cause && Action_Due_Date when update in row :" + (i + 1));
							partvalidation
									.setStatus("You must enter C_Action && P_Action && ROOT_Cause && Action_Due_Date when update");
							setCellColore(CactionCell, 0xD2254D);
							setCellColore(PactionCell, 0xD2254D);
							setCellColore(RootcauseCell, 0xD2254D);
							setCellColore(ActionduedateCell, 0xD2254D);
							writeValidtionStatus(xcellrange, false);
							canSave = false;
							continue part;
						}
						if(!Actionduedate.isEmpty())
						{
							if(ApprovedDevUtil.isThisDateValid(Actionduedate, "DD/MM/YYYY") == false)
							{
								// JOptionPane.showMessageDialog(null, " You must enter Action_Due_Date with 'dd/MM/yyyy' fromat in row :" + (i + 1));
								setCellColore(ActionduedateCell, 0xD2254D);
								partvalidation
										.setStatus("You must enter Action_Due_Date with 'dd/MM/yyyy' fromat");
								writeValidtionStatus(xcellrange, false);
								canSave = false;
								continue part;
							}
						}
					}

					// if((status.equals("Approved") && !comment.equals("")) || (status.equals("Rejected") && comment.equals("")))
					if(status.equals("Rejected") && comment.equals(""))
					{
						partvalidation.setStatus("Wrong Comment");
						setCellColore(commentCell, 0xD2254D);
						writeValidtionStatus(xcellrange, false);
						canSave = false;
						continue part;
					}
					if(status.equals("Rejected") && !comment.equals(""))
					{
						if(!wrongfets.isEmpty())
						{
							if(wrongfets.contains("|"))
							{
								if(comment.contains("|"))
								{
									String[] features = wrongfets.split("\\|");
									String[] comments = comment.split("\\|");
									if(features.length != comments.length)
									{
										partvalidation
												.setStatus("comment must be as count as the features");
										setCellColore(commentCell, 0xD2254D);
										writeValidtionStatus(xcellrange, false);
										canSave = false;
										continue part;
									}
									if(status.equals("Rejected"))
									{
										for(String com : comments)
										{
											if(!com.equalsIgnoreCase("notissue")
													&& !com.equalsIgnoreCase("issue"))
											{
												partvalidation
														.setStatus("comment must be (Issue , notissue)");
												setCellColore(commentCell, 0xD2254D);
												writeValidtionStatus(xcellrange, false);
												canSave = false;
												continue part;
											}
										}
									}
								}
								else
								{
									partvalidation
											.setStatus("comment must be as count as the features");
									setCellColore(commentCell, 0xD2254D);
									writeValidtionStatus(xcellrange, false);
									canSave = false;
									continue part;
								}
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
			if(NPIFlag && !npihasvalue && canSave)
			{
				partvalidation.setStatus("NPI Must has at least one value");
				writeValidtionStatus(xcellrange, false);
				canSave = false;
			}
			// JOptionPane.showMessageDialog(null, "Validation Finished");
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return result;
	}

	public void saveParts(boolean update)
	{

		// System.out.println(""+System.currentTimeMillis());
		if(!canSave)
		{
			System.out.println("Can Save: " + canSave);
			MainWindow.glass.setVisible(false);
			saved = false;
			JOptionPane.showMessageDialog(null, "can't save sheet duto some errors in your data");
			return;
		}
		try
		{
			ArrayList<ArrayList<String>> sheetData = readSpreadsheet(2);
			Set<String> pdfSet = new HashSet<String>();
			XCellRange xcellrange = null;
			int lastColNum = HeaderList.size();
			String lastColumn = getColumnName(lastColNum);
			ArrayList<String> sheetHeader = getHeader();
			int npiIndex = sheetHeader.indexOf("NPI");
			boolean npihasvalue = false;
			int lastRow = getLastRow();
			// for(int i = 3; i < lastRow + 1; i++)
			// {
			// try
			// {
			// String seletedRange = "A" + i + ":" + lastColumn + i;
			// xcellrange = sheet.getCellRangeByName(seletedRange);
			// String famCross = "", generic = "";
			// XCell genCell = null;
			// XCell famCrossCell = null;
			// if(NPIFlag)
			// {
			// if(npiIndex > 0)
			// {
			// XCell npiCell = xcellrange.getCellByPosition(npiIndex, 0);
			// String npi = getCellText(npiCell).getString();
			// if(!npi.isEmpty() && !npihasvalue)
			// {
			// npihasvalue = true;
			// }
			// }
			// }
			//
			// XCell pnCell = xcellrange.getCellByPosition(PartCell, 0);
			// String pn = getCellText(pnCell).getString();
			// XCell suppCell = xcellrange.getCellByPosition(supCell, 0);
			// String supplierName = getCellText(suppCell).getString();
			// XCell famCell = xcellrange.getCellByPosition(familyCell, 0);
			// String family = getCellText(famCell).getString();
			// XCell maskCell = xcellrange.getCellByPosition(maskCellNo, 0);
			// String mask = getCellText(maskCell).getString();
			// // PartComponent component=DataDevQueryUtil.getComponentByPartNumberAndSupplierName(pn, supplierName);
			//
			// if(plType.equals("Semiconductor"))
			// {
			// genCell = xcellrange.getCellByPosition(genericCellNo, 0);
			// famCrossCell = xcellrange.getCellByPosition(famCrossCellNo, 0);
			// generic = getCellText(genCell).getString();
			// famCross = getCellText(famCrossCell).getString();
			// }
			// if(pn.isEmpty())
			// {
			// partvalidation.setStatus("Empty Part");
			// setCellColore(pnCell, 0xD2254D);
			// writeValidtionStatus(xcellrange, false);
			// canSave = false;
			// }
			// if(family.isEmpty())
			// {
			// partvalidation.setStatus("Empty Family");
			// setCellColore(famCell, 0xD2254D);
			// writeValidtionStatus(xcellrange, false);
			// canSave = false;
			// }
			// /**** validate that mask not null ***/
			// if(mask.isEmpty())
			// {
			// partvalidation.setStatus("Empty Mask)");
			// setCellColore(maskCell, 0xD2254D);
			// writeValidtionStatus(xcellrange, false);
			// canSave = false;
			// }
			// else if(mask.length() != pn.length())
			// {
			// partvalidation.setStatus("Wrong Mask Length");
			// setCellColore(maskCell, 0xD2254D);
			// writeValidtionStatus(xcellrange, false);
			// canSave = false;
			// }
			// /**
			// * validate that generic and family Cross not null
			// */
			// if(plType.equals("Semiconductor"))
			// {
			//
			// if(generic.isEmpty() || famCross.isEmpty())
			// {
			// partvalidation.setStatus("Empty Main columns(Generic or Family Cross)");
			// setCellColore(genCell, 0xD2254D);
			// setCellColore(famCrossCell, 0xD2254D);
			// writeValidtionStatus(xcellrange, false);
			// canSave = false;
			// }
			// }
			// }catch(Exception e)
			// {
			// e.printStackTrace();
			// }
			// }
			// if(NPIFlag && !npihasvalue && canSave)
			// {
			// partvalidation.setStatus("NPI Must has at least one value");
			// writeValidtionStatus(xcellrange, false);
			// canSave = false;
			// }
			// if(!canSave)
			// {
			// System.out.println("Can Save: " + canSave);
			// MainWindow.glass.setVisible(false);
			// JOptionPane.showMessageDialog(null,
			// "can't save sheet duto some errors in your data");
			// return;
			// }
			// saved = true;
			Date startDate = new Date();
			String pn = "", supplierName = "", family, mask, pdfUrl = null, desc = "", famCross = null, generic = null, NPIPart = null;
			for(int i = 0; i < sheetData.size(); i++)
			{
				try
				{

					String seletedRange2 = "A" + (3 + i) + ":" + lastColumn + (3 + i);
					xcellrange = sheet.getCellRangeByName(seletedRange2);
					// int ss = Integer.parseInt("sss", 4);
					PartInfoDTO partInfo = new PartInfoDTO();
					ArrayList<String> partData = sheetData.get(i);
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
						JOptionPane.showMessageDialog(null,
								"Can't Load this PL Name as PL Type Not Clear");
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
					else if(partData.get(valStatusColumn).equals(
							"Reject, Found on Acquisition Table"))
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
					partInfo.setPlFetIds(plFetIds);
					boolean save = false;
					if(!update)
					{
						try
						{
							save = DataDevQueryUtil.saveParamtric(partInfo);
						}catch(ConstraintViolationException e)
						{
							e.printStackTrace();
							if(e.getMessage().contains("PART_COMP_PART_SUPP_PL_UQ"))
							{
								partvalidation.setStatus("Part Dupplicated in The sheet");
								writeValidtionStatus(xcellrange, false);
								save = true;
							}
							else
							{
								save = false;
								partvalidation.setComment(e.getMessage());
								partvalidation.setStatus("Rejected");
							}
						}

					}
					else
					{

						try
						{
							save = DataDevQueryUtil.updateParamtric(partInfo);
						}catch(Exception e)
						{
							e.printStackTrace();
							save = false;
							partvalidation.setComment(e.getMessage());
							partvalidation.setStatus("Rejected");
						}

					}

					System.out.println("Part Saved:" + pn + " : " + seletedRange2);
					if(save)
					{
						pdfSet.add(pdfUrl);
						partvalidation.setComment("");
						partvalidation.setStatus("Saved");
					}
					else
					{
						pdfSet.remove(pdfUrl);
					}

					writeValidtionStatus(xcellrange, false);

				}catch(Exception e)
				{
					try
					{
						// Cell cell = getCellByPosission(lastColNum + 1, i + 1);
						// cell.setText(e.getMessage());
						partvalidation.setComment(e.getMessage());
						partvalidation.setStatus("Rejected");
						writeValidtionStatus(xcellrange, false);
						pdfSet.remove(pdfUrl);
						continue;
					}catch(Exception ex)
					{
						ex.printStackTrace();
					}
				}
			}
			DataDevQueryUtil.saveTrackingParamtric(pdfSet, selectedPL, null,
					StatusName.doneFLagEngine, "");
			System.out.println("~~~~~~~~~~~~~~~~~~~~ Saving finished:" + startDate + " to  "
					+ new Date() + " ~~~~~~~~~~~~~~~~~~~~~~");
		}catch(Exception e)
		{
			MainWindow.glass.setVisible(false);
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Can't Save Taracking PDFs");
		}

	}

	public void saveQAReviewAction(String QAName, String screen, boolean summarydata)
	{
		if(canSave)
		{
			try
			{
				saved = true;
				Set<String> rejectedPdfs = new HashSet<String>();
				Set<String> acceptedPdfs = new HashSet<String>();
				List<String> changedparts = new ArrayList<>();
				List<PartInfoDTO> feedbackParts = new ArrayList<PartInfoDTO>();
				List<PartInfoDTO> AllParts = new ArrayList<PartInfoDTO>();
				ArrayList<String> sheetHeader = getHeader();
				int oldflagIndex = 0;
				String oldflag = "";
				if(summarydata)
				{
					oldflagIndex = sheetHeader.indexOf("Old Flag");
				}
				int statusIndex = sheetHeader.indexOf("Status");
				int CommentIndex = sheetHeader.indexOf("Comment");
				int ComidIndex = sheetHeader.indexOf("Comid");
				int WrongFeatureIndex = sheetHeader.indexOf("Wrong Feature");
				int partcell = sheetHeader.indexOf("Part Number");
				int engidx = sheetHeader.indexOf("Eng Name");
				int pdfidx = sheetHeader.indexOf("PDF URL");
				int supcell = sheetHeader.indexOf("Supplier Name");
				int famcell = sheetHeader.indexOf("Family");
				int maskcell = sheetHeader.indexOf("Mask");
				int Taxonomyindex = sheetHeader.indexOf("Taxonomy");

				ArrayList<ArrayList<String>> fileData = readSpreadsheet(2);
				String pn = "", family, mask, pdfUrl, desc = "", famCross = "", generic = "", NPIPart = null;
				for(int i = 0; i < fileData.size(); i++)
				{
					PartInfoDTO partInfo = new PartInfoDTO();
					ArrayList<String> partData = fileData.get(i);
					String status = partData.get(statusIndex);
					String WrongFeatures = partData.get(WrongFeatureIndex);
					String comment = partData.get(CommentIndex);
					String vendorName = partData.get(supcell);
					String plName = partData.get(Taxonomyindex);
					String tlName = ParaQueryUtil.getTeamLeaderNameByMember(partData.get(engidx));
					String comid = partData.get(ComidIndex);
					pn = partData.get(partcell);
					pdfUrl = partData.get(pdfidx);
					family = partData.get(famcell);
					mask = partData.get(maskcell);
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
					else if(partData.get(valStatusColumn).equals(
							"Reject, Found on Acquisition Table"))
					{
						partInfo.setFeedbackType("Acquisition");
					}
					PartComponent component = DataDevQueryUtil.getComponentBycomid(Long
							.valueOf(comid));

					partInfo.setPN(pn);
					partInfo.setSupplierName(vendorName);
					partInfo.setComponent(component);
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

					if(summarydata)
					{
						oldflag = partData.get(oldflagIndex);
						if(!oldflag.isEmpty() && !status.isEmpty()
								&& (oldflag.equals("R") || oldflag.equals("W"))
								&& (!status.equals("R") && !status.equals("W")))
						{
							changedparts.add(pn);
						}
					}
					if("W".equals(status) || "R".equals(status))
					{
						if("".equals(comment))
						{
							saved = false;
							System.out.println("Comment shouldn't be null");
							MainWindow.glass.setVisible(false);
							JOptionPane.showMessageDialog(null,
									"Comment can not be empty for rejected parts",
									"Saving Not Done", JOptionPane.ERROR_MESSAGE);
							return;
						}
						else
						{
							partInfo.setFbtype(StatusName.QA);
							partInfo.setStatus(status);
							partInfo.setFeedBackStatus(StatusName.reject);
							partInfo.setFeedBackCycleType(StatusName.wrongData);
							partInfo.setWrongFeatures(WrongFeatures);
							feedbackParts.add(partInfo);
							AllParts.add(partInfo);
							if(acceptedPdfs.contains(pdfUrl))
							{
								acceptedPdfs.remove(pdfUrl);
							}
							rejectedPdfs.add(pdfUrl);
						}
					}
					else if("S".equals(status) || "A".equals(status) || "Fast".equals(status))
					{
						partInfo.setStatus(status);
						AllParts.add(partInfo);
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
				DataDevQueryUtil.saveQAFlag(AllParts);
				DataDevQueryUtil.savePartsFeedback(feedbackParts);
				if(!screen.equals("FB"))
				{
					DataDevQueryUtil.saveTrackingParamtric(acceptedPdfs, selectedPL, null,
							StatusName.waitingsummary, QAName); // // Eng
					DataDevQueryUtil.saveTrackingParamtric(rejectedPdfs, selectedPL, null,
							StatusName.waitingsummary, QAName);
				}
				else
				{
					DataDevQueryUtil.saveTrackingParamtric(acceptedPdfs, selectedPL, null,
							StatusName.WaittingParametricInsertion, QAName); // // Eng
					DataDevQueryUtil.saveTrackingParamtric(rejectedPdfs, selectedPL, null,
							StatusName.tlFeedback, QAName);
				}
				// DataDevQueryUtil.saveTrackingParamtric(qafeedbackpdfs, selectedPL, null, StatusName.qaFeedback, teamLeaderName);

				if(summarydata)
				{
					DataDevQueryUtil.deleteoldfeedbacks(changedparts, QAName);
				}
				MainWindow.glass.setVisible(false);
				JOptionPane.showMessageDialog(null, "Saving Data Finished");
			}catch(Exception e)
			{
				saved = false;
				MainWindow.glass.setVisible(false);
				JOptionPane.showMessageDialog(null, "Can't Save Data");
				e.printStackTrace();
			}
		}
		else
		{
			saved = false;
			MainWindow.glass.setVisible(false);
			JOptionPane.showMessageDialog(null, "can't save sheet duto some errors in your data");
		}

	}

	public void saveQASummary(String QAName)
	{
		if(canSave)
		{
			try
			{
				Map<String, List<String>> pdfs = new HashMap<String, List<String>>();
				// List<String> pdfs = new ArrayList<>();
				List<String> changedparts = new ArrayList<>();
				List<PartInfoDTO> AllParts = new ArrayList<PartInfoDTO>();
				ArrayList<String> sheetHeader = getHeader();
				saved = true;
				String oldflag = "";
				int oldflagIndex = sheetHeader.indexOf("Sample QA Flag");
				int statusIndex = sheetHeader.indexOf("Final QA Flag");
				int ComidIndex = sheetHeader.indexOf("COM_ID");
				int partIndex = sheetHeader.indexOf("Part");
				int pdfIndex = sheetHeader.indexOf("PDFURL");
				int plIndex = sheetHeader.indexOf("PL Name");
				int supplierIndex = sheetHeader.indexOf("Supplier Name");
				ArrayList<ArrayList<String>> fileData = readSpreadsheet(2);

				for(int i = 0; i < fileData.size(); i++)
				{
					PartInfoDTO partInfo = new PartInfoDTO();
					ArrayList<String> partData = fileData.get(i);
					String status = partData.get(statusIndex);
					String comid = partData.get(ComidIndex);
					String pn = partData.get(partIndex);
					String pdf = partData.get(pdfIndex);
					String pl = partData.get(plIndex);
					String supplier = partData.get(supplierIndex);
					PartComponent component = DataDevQueryUtil.getComponentBycomid(Long
							.valueOf(comid));
					partInfo.setComponent(component);
					partInfo.setIssuedBy(QAName);

					List<String> data = new ArrayList<>();
					data.add(pl);
					data.add(supplier);
					if(!pdfs.containsKey(pdf))
						pdfs.put(pdf, data);
					oldflag = partData.get(oldflagIndex);
					if(!oldflag.isEmpty() && !status.isEmpty()
							&& (oldflag.equals("R") || oldflag.equals("W")))
					{
						changedparts.add(pn);
					}
					if("S".equals(status) || "A".equals(status) || "Fast".equals(status))
					{
						partInfo.setStatus(status);
						AllParts.add(partInfo);
					}
				}

				DataDevQueryUtil.saveQAFlag(AllParts);
				if(!changedparts.isEmpty())
					DataDevQueryUtil.deleteoldfeedbacks(changedparts, QAName);

				DataDevQueryUtil.saveTrackingParamtric(pdfs, StatusName.summaryengine);
				// JOptionPane.showMessageDialog(null, "Saving Data Finished");
			}catch(Exception e)
			{
				saved = false;
				MainWindow.glass.setVisible(false);
				JOptionPane.showMessageDialog(null, "Can't Save Data");
				e.printStackTrace();
			}
			MainWindow.glass.setVisible(false);
			JOptionPane.showMessageDialog(null, "Saving Done");
		}
		else
		{
			saved = false;
			MainWindow.glass.setVisible(false);
			JOptionPane.showMessageDialog(null, "can't save sheet duto some errors in your data");
		}

	}

	public void saveTLReviewAction(String teamLeaderName)
	{
		if(!canSave)
		{
			System.out.println("Can Save: " + canSave);
			MainWindow.glass.setVisible(false);
			saved = false;
			JOptionPane.showMessageDialog(null, "can't save sheet duto some errors in your data");
			return;
		}
		try
		{
			Set<String> rejectedPdfs = new HashSet<String>();
			Set<String> acceptedPdfs = new HashSet<String>();
			Set<String> updatedPdfs = new HashSet<String>();
			List<PartInfoDTO> feedbackParts = new ArrayList<PartInfoDTO>();
			ArrayList<String> sheetHeader = getHeader();
			saved = true;
			int partcell = sheetHeader.indexOf("Part Number");
			int statuscellidx = sheetHeader.indexOf("Status");
			int commentcellidx = sheetHeader.indexOf("Comment");
			int supcell = sheetHeader.indexOf("Supplier Name");
			int famcell = sheetHeader.indexOf("Family");
			int maskcell = sheetHeader.indexOf("Mask");
			int Taxonomyindex = sheetHeader.indexOf("Taxonomy");
			int engindex = sheetHeader.indexOf("Eng Name");
			int pdfindex = sheetHeader.indexOf("PDF URL");
			int ComidIndex = sheetHeader.indexOf("Comid");
			// List<String> fetNames = sheetHeader.subList(startParametricFT, endParametricFT);
			ArrayList<ArrayList<String>> fileData = readSpreadsheet(2);
			String pn = "", family, mask, pdfUrl, desc = "", famCross = "", generic = "", NPIPart = null;
			for(int i = 0; i < fileData.size(); i++)
			{
				PartInfoDTO partInfo = new PartInfoDTO();
				ArrayList<String> partData = fileData.get(i);
				String status = partData.get(statuscellidx);
				String comment = partData.get(commentcellidx);
				String vendorName = partData.get(supcell);
				String plName = partData.get(Taxonomyindex);
				String engName = partData.get(engindex);
				String comid = partData.get(ComidIndex);
				pn = partData.get(partcell);
				pdfUrl = partData.get(pdfindex);
				family = partData.get(famcell);
				mask = partData.get(maskcell);
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
				partInfo.setFbtype(StatusName.internal);
				PartComponent component = DataDevQueryUtil.getComponentBycomid(Long.valueOf(comid));
				partInfo.setComponent(component);
				if("Rejected".equals(status))
				{
					if("".equals(comment))
					{
						System.out.println("Comment shouldn't be null");
						saved = false;
						MainWindow.glass.setVisible(false);
						JOptionPane.showMessageDialog(null,
								"Comment can not be empty for rejected parts", "Saving Not Done",
								JOptionPane.ERROR_MESSAGE);
						return;
					}
					else
					{
						partInfo.setFeedBackStatus(StatusName.reject);
						partInfo.setFeedBackCycleType(StatusName.wrongData);
						// partInfo.setFbtype("Wrong Data");
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

					partInfo.setFetValues(readRowValues(partData));
					DataDevQueryUtil.updateParamtric(partInfo);
					if(!rejectedPdfs.contains(pdfUrl))
					{
						updatedPdfs.add(pdfUrl);
					}
				}
			}
			DataDevQueryUtil.savePartsFeedback(feedbackParts);
			DataDevQueryUtil.saveTrackingParamtric(acceptedPdfs, selectedPL, null,
					StatusName.qaReview, teamLeaderName);
			DataDevQueryUtil.saveTrackingParamtric(updatedPdfs, selectedPL, null,
					StatusName.doneFLagEngine, teamLeaderName);
			DataDevQueryUtil.saveTrackingParamtric(rejectedPdfs, selectedPL, null,
					StatusName.engFeedback, teamLeaderName);
		}catch(Exception e)
		{
			MainWindow.glass.setVisible(false);
			saved = false;
			JOptionPane.showMessageDialog(null, "Can't Save Data");
			e.printStackTrace();
		}

	}

	public void saveEngFeedbackAction(String devName)
	{
		if(!canSave)
		{
			System.out.println("Can Save: " + canSave);
			MainWindow.glass.setVisible(false);
			saved = false;
			JOptionPane.showMessageDialog(null, "can't save sheet duto some errors in your data");
			return;
		}
		try
		{
			Set<String> pdfs = new HashSet<String>();
			Set<String> updatedpdfs = new HashSet<String>();
			List<String> fbTypes = new ArrayList<String>();
			// List<PartInfoDTO> updatedpdfs = new ArrayList<PartInfoDTO>();
			// List<PartInfoDTO> rejectedParts = new ArrayList<PartInfoDTO>();
			List<PartInfoDTO> feedBackParts = new ArrayList<PartInfoDTO>();
			ArrayList<String> sheetHeader = getHeader();
			List<String> fetNames = sheetHeader.subList(startParametricFT, endParametricFT);
			ArrayList<ArrayList<String>> sheetData = readSpreadsheet(2);
			saved = true;
			int issuerIndex = sheetHeader.indexOf("Issued By");

			int Cactionindex = sheetHeader.indexOf("C_Action");
			int Pactionindex = sheetHeader.indexOf("P_Action");
			int RootcauseIndex = sheetHeader.indexOf("RootCause");
			int Actionduedateindex = sheetHeader.indexOf("ActionDueDate");
			int wrongfetsindex = sheetHeader.indexOf("Wrong Features");
			int partcell = sheetHeader.indexOf("Part Number");
			int statuscellidx = sheetHeader.indexOf("Status");
			int commentcellidx = sheetHeader.indexOf("Comment");
			int supcell = sheetHeader.indexOf("Supplier Name");
			int famcell = sheetHeader.indexOf("Family");
			int maskcell = sheetHeader.indexOf("Mask");
			int Taxonomyindex = sheetHeader.indexOf("Taxonomy");
			int fbtypeindex = sheetHeader.indexOf("Feedback Type");
			int pdfindex = sheetHeader.indexOf("PDF URL");
			int comidindex = sheetHeader.indexOf("Comid");
			int NewsLinkindex = sheetHeader.indexOf("News Link");

			String pn = "", family, mask, pdfUrl, desc = "", famCross = "", generic = "", NPIPart = null, newsLink = "";
			for(int i = 0; i < sheetData.size(); i++)
			{
				PartInfoDTO partInfo = new PartInfoDTO();
				ArrayList<String> partData = sheetData.get(i);
				String status = partData.get(statuscellidx);
				String comment = partData.get(commentcellidx);
				String vendorName = partData.get(supcell);
				String plName = partData.get(Taxonomyindex);
				String issuedTo = partData.get(issuerIndex);
				String fbtype = partData.get(fbtypeindex);
				String CAction = partData.get(Cactionindex);
				String PAction = partData.get(Pactionindex);
				String RootCause = partData.get(RootcauseIndex);
				String ActinDueDate = partData.get(Actionduedateindex);
				String wrongfets = partData.get(wrongfetsindex);
				String comid = partData.get(comidindex);
				if(NewsLinkindex != -1)
					newsLink = partData.get(NewsLinkindex);
				pn = partData.get(partcell);
				pdfUrl = partData.get(pdfindex);
				family = partData.get(famcell);
				mask = partData.get(maskcell);
				desc = partData.get(descriptionColumn);
				if(plType.equals("Semiconductor"))
				{
					famCross = partData.get(famCrossCellNo);
					generic = partData.get(genericCellNo);
					partInfo.setFamilycross(famCross);
					partInfo.setGeneric(generic);

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

				String tlname = ParaQueryUtil.getTeamLeaderNameByMember(devName);
				partInfo.setPN(pn);
				partInfo.setSupplierName(vendorName);
				partInfo.setStatus(status);
				partInfo.setComment(comment);
				partInfo.setIssuedBy(devName);
				partInfo.setIssuedTo(tlname);
				partInfo.setPlName(selectedPL);
				partInfo.setNPIFlag(NPIPart);
				partInfo.setDescription(desc);
				partInfo.setPdfUrl(pdfUrl);
				partInfo.setFamily(family);
				partInfo.setMask(mask);
				partInfo.setNewsLink(newsLink);

				partInfo.setFbtype(fbtype);
				partInfo.setCAction(CAction);
				partInfo.setPAction(PAction);
				partInfo.setRootCause(RootCause);
				partInfo.setActinDueDate(ActinDueDate);
				partInfo.setWrongFeatures(wrongfets);
				PartComponent component = DataDevQueryUtil.getComponentBycomid(Long.valueOf(comid
						.isEmpty() ? "0" : comid));
				partInfo.setComponent(component);
				if(ActinDueDate != null && !ActinDueDate.isEmpty())
				{
					if(ApprovedDevUtil.isThisDateValid(ActinDueDate, "DD/MM/YYYY") == false)
					{
						saved = false;
						MainWindow.glass.setVisible(false);
						JOptionPane.showMessageDialog(null,
								" You must enter Action_Due_Date with 'dd/MM/yyyy' fromat in row :"
										+ i + 1);
						return;
					}
				}
				if("Rejected".equals(status))
				{
					if("".equals(comment))
					{
						System.out.println("Comment shouldn't be null");
						saved = false;
						MainWindow.glass.setVisible(false);
						JOptionPane.showMessageDialog(null,
								"Comment can not be empty for rejected parts", "Saving Not Done",
								JOptionPane.ERROR_MESSAGE);
						return;
					}
					else
					{
						if((issuedTo != null) && (!"".equals(issuedTo)))
						{
							partInfo.setFeedBackStatus(StatusName.reject);
							feedBackParts.add(partInfo);
							pdfs.add(pdfUrl);
							fbTypes.add(partInfo.getFbtype());
						}

					}

				}
				else if("Updated".equals(status))
				{
					partInfo.setFetValues(readRowValues(partData));
					if((issuedTo != null) && (!"".equals(issuedTo)))
					{
						partInfo.setFeedBackStatus(StatusName.accept);
						feedBackParts.add(partInfo);
					}
					DataDevQueryUtil.updateParamtric(partInfo);
					updatedpdfs.add(pdfUrl);
					fbTypes.add(partInfo.getFbtype());
				}

			}

			DataDevQueryUtil.savePartsFeedback(feedBackParts);
			DataDevQueryUtil.saveTrackingParamtric(pdfs, selectedPL, null, StatusName.tlFeedback,
					devName);
			DataDevQueryUtil.saveTrackingParamtric(updatedpdfs, selectedPL, null,
					StatusName.doneFLagEngine, devName);
			MainWindow.glass.setVisible(false);
			JOptionPane.showMessageDialog(null, "Saving Data Finished");
		}catch(Exception e)
		{
			saved = false;
			e.printStackTrace();
		}

	}

	public void saveTLFeedbackAction(String teamLeaderName)
	{/* make partsFeedBack contain Status also to call savePartsFeedback once */
		if(!canSave)
		{
			System.out.println("Can Save: " + canSave);
			MainWindow.glass.setVisible(false);
			saved = false;
			JOptionPane.showMessageDialog(null, "can't save sheet duto some errors in your data");
			return;
		}
		try
		{
			saved = true;
			Set<String> qareviewpdfs = new HashSet<String>();
			Set<String> qafeedbackpdfs = new HashSet<String>();
			Set<String> engfeedbackpfds = new HashSet<String>();
			Set<String> doneflagpfds = new HashSet<String>();
			List<PartInfoDTO> partsToStoreFeedback = new ArrayList<PartInfoDTO>();
			ArrayList<String> sheetHeader = getHeader();
			int issueSourceIndex = sheetHeader.indexOf("Issue Initiator");
			int issuerIndex = sheetHeader.indexOf("Issued By");
			int engindex = sheetHeader.indexOf("Develop Eng.");
			int Cactionindex = sheetHeader.indexOf("C_Action");
			int Pactionindex = sheetHeader.indexOf("P_Action");
			int RootcauseIndex = sheetHeader.indexOf("RootCause");
			int Actionduedateindex = sheetHeader.indexOf("ActionDueDate");
			int wrongfetsindex = sheetHeader.indexOf("Wrong Features");
			int partcell = sheetHeader.indexOf("Part Number");
			int statuscellidx = sheetHeader.indexOf("Status");
			int commentcellidx = sheetHeader.indexOf("Comment");
			int supcell = sheetHeader.indexOf("Supplier Name");
			int famcell = sheetHeader.indexOf("Family");
			int maskcell = sheetHeader.indexOf("Mask");
			int Taxonomyindex = sheetHeader.indexOf("Taxonomy");
			int fbtypeindex = sheetHeader.indexOf("Feedback Type");
			int comidindex = sheetHeader.indexOf("Comid");
			int issuetypeidx = sheetHeader.indexOf("Issue Type");
			ArrayList<ArrayList<String>> fileData = readSpreadsheet(2);
			String pn = "", family, mask, pdfUrl, desc = "", famCross = null, generic = null, NPIPart = null, flowSource;
			for(int i = 0; i < fileData.size(); i++)
			{
				PartInfoDTO partInfo = new PartInfoDTO();
				ArrayList<String> partData = fileData.get(i);
				String status = partData.get(statuscellidx);
				String comment = partData.get(commentcellidx);
				String vendorName = partData.get(supcell);
				String plName = partData.get(Taxonomyindex);
				// String issuedToEng = partData.get(1); // from eng column
				String issuetype = partData.get(issuetypeidx);
				String issuedToEng = partData.get(engindex);//
				String issueSourceName = partData.get(issueSourceIndex);// from issuer column
				String fbtype = partData.get(fbtypeindex);
				// System.err.println(issuerName);
				String caction = partData.get(Cactionindex);
				String paction = partData.get(Pactionindex);
				String rootcause = partData.get(RootcauseIndex);
				String actionduedate = partData.get(Actionduedateindex);
				String wrngfets = partData.get(wrongfetsindex);
				String comid = partData.get(comidindex);

				pn = partData.get(partcell);
				pdfUrl = partData.get(pdfCellNo);
				family = partData.get(famcell);
				mask = partData.get(maskcell);
				desc = partData.get(descriptionColumn);
				// Long issueQAEngID = ParaQueryUtil.getIssueFirstSenderID(pn, vendorName);
				if(plType.equals("Semiconductor"))
				{
					famCross = partData.get(famCrossCellNo);
					generic = partData.get(genericCellNo);
					partInfo.setFamilycross(famCross);
					partInfo.setGeneric(generic);
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

				PartComponent component = DataDevQueryUtil.getComponentBycomid(Long.valueOf(comid));
				partInfo.setComponent(component);
				partInfo.setPN(pn);
				partInfo.setSupplierName(vendorName);
				partInfo.setPlName(selectedPL);
				partInfo.setNPIFlag(NPIPart);
				partInfo.setDescription(desc);
				partInfo.setPdfUrl(pdfUrl);
				partInfo.setFamily(family);
				partInfo.setMask(mask);
				partInfo.setFeedBackSource(issueSourceName);
				partInfo.setStatus(status);
				partInfo.setComment(comment);
				partInfo.setIssuedBy(teamLeaderName);
				if("Approved Eng.".equals(status))
				{
					if(fbtype.equals("QA"))
					{
						partInfo.setCAction(caction);
						partInfo.setPAction(paction);
						partInfo.setRootCause(rootcause);
						partInfo.setActinDueDate(actionduedate);
						partInfo.setIssuedTo(issueSourceName);
						partInfo.setFeedBackStatus(StatusName.accept);
						partInfo.setFeedBackCycleType(issuetype);
						if(!engfeedbackpfds.contains(pdfUrl))
						{
							if(qareviewpdfs.contains(pdfUrl))
							{
								qareviewpdfs.remove(pdfUrl);
							}
							if(issuetype == StatusName.wrongValue)
							{
								// QA_Review
								qareviewpdfs.add(pdfUrl);
							}
							else
							{
								// QA_feedback
								qafeedbackpdfs.add(pdfUrl);
							}
						}
						partsToStoreFeedback.add(partInfo);
					}
					else
					{
						partInfo.setIssuedTo(issuedToEng);
						partInfo.setFeedBackStatus(StatusName.fbClosed);
						partInfo.setFeedBackCycleType("Wrong Data");
						if(!qafeedbackpdfs.contains(pdfUrl) && !engfeedbackpfds.contains(pdfUrl))
						{
							// QA_Review
							qareviewpdfs.add(pdfUrl);
						}
						partsToStoreFeedback.add(partInfo);
					}
				}

				else if("Updated".equals(status))
				{
					if(fbtype.equals("QA"))
					{
						partInfo.setCAction(caction);
						partInfo.setPAction(paction);
						partInfo.setRootCause(rootcause);
						partInfo.setActinDueDate(actionduedate);
						partInfo.setIssuedTo(issueSourceName);
						partInfo.setFeedBackStatus(StatusName.accept);
						partInfo.setFeedBackCycleType("Wrong Data");
						if(!engfeedbackpfds.contains(pdfUrl))
						{
							if(qareviewpdfs.contains(pdfUrl))
							{
								qareviewpdfs.remove(pdfUrl);
							}
							// Doneflagengine
							doneflagpfds.add(pdfUrl);
						}
						partsToStoreFeedback.add(partInfo);
						partInfo.setFetValues(readRowValues(partData));
						DataDevQueryUtil.updateParamtric(partInfo);
					}
					else
					{
						partInfo.setIssuedTo(issuedToEng);
						partInfo.setFeedBackStatus(StatusName.fbClosed);
						partInfo.setFeedBackCycleType("Wrong Data");
						if(!qafeedbackpdfs.contains(pdfUrl) && !engfeedbackpfds.contains(pdfUrl))
						{
							// Doneflagengine
							doneflagpfds.add(pdfUrl);
						}
						partsToStoreFeedback.add(partInfo);
						partInfo.setFetValues(readRowValues(partData));
						DataDevQueryUtil.updateParamtric(partInfo);
					}

				}
				else if("Reject QA".equals(status))
				{
					partInfo.setCAction(caction);
					partInfo.setPAction(paction);
					partInfo.setRootCause(rootcause);
					partInfo.setActinDueDate(actionduedate);
					partInfo.setIssuedTo(issueSourceName);
					partInfo.setFeedBackStatus(StatusName.reject);
					partInfo.setFeedBackCycleType("Wrong Data");
					partInfo.setWrongFeatures(wrngfets);
					if(!engfeedbackpfds.contains(pdfUrl))
					{
						if(qareviewpdfs.contains(pdfUrl))
						{
							qareviewpdfs.remove(pdfUrl);
						}
						// QA_feedback
						qafeedbackpdfs.add(pdfUrl);
					}
					partsToStoreFeedback.add(partInfo);
				}
				else if("Wrong Data".equals(status))
				{
					partInfo.setIssuedTo(issuedToEng);
					partInfo.setFeedBackStatus(StatusName.reject);
					partInfo.setFeedBackCycleType("Wrong Data");
					partInfo.setWrongFeatures(wrngfets);
					if(qafeedbackpdfs.contains(pdfUrl))
					{
						qafeedbackpdfs.remove(pdfUrl);
					}
					if(qareviewpdfs.contains(pdfUrl))
					{
						qareviewpdfs.remove(pdfUrl);
					}
					// Eng_feedback
					engfeedbackpfds.add(pdfUrl);
					partsToStoreFeedback.add(partInfo);

				}
				else if("Accept QA & Forward".equals(status))
				{
					partInfo.setCAction(caction);
					partInfo.setPAction(paction);
					partInfo.setRootCause(rootcause);
					partInfo.setActinDueDate(actionduedate);
					partInfo.setIssuedTo(issuedToEng);
					partInfo.setFeedBackStatus(StatusName.accept);
					partInfo.setFeedBackCycleType("Wrong Data");
					if(qafeedbackpdfs.contains(pdfUrl))
					{
						qafeedbackpdfs.remove(pdfUrl);
					}
					if(qareviewpdfs.contains(pdfUrl))
					{
						qareviewpdfs.remove(pdfUrl);
					}
					// Eng_feedback
					engfeedbackpfds.add(pdfUrl);
					partsToStoreFeedback.add(partInfo);
				}
			}
			DataDevQueryUtil.savePartsFeedback(partsToStoreFeedback);
			DataDevQueryUtil.saveTrackingParamtric(qareviewpdfs, selectedPL, null,
					StatusName.qaReview, teamLeaderName); // // Eng
			DataDevQueryUtil.saveTrackingParamtric(engfeedbackpfds, selectedPL, null,
					StatusName.engFeedback, teamLeaderName);
			DataDevQueryUtil.saveTrackingParamtric(qafeedbackpdfs, selectedPL, null,
					StatusName.qaFeedback, teamLeaderName);
			DataDevQueryUtil.saveTrackingParamtric(doneflagpfds, selectedPL, null,
					StatusName.doneFLagEngine, teamLeaderName);

			MainWindow.glass.setVisible(false);
			JOptionPane.showMessageDialog(null, "Saving Data Finished");
		}catch(Exception e)
		{
			saved = false;
			e.printStackTrace();
		}

	}

	private void setExtractionData(TrackingParametric trac, int pdfRow)
	{

		try
		{
			Map<String, List<String>> partsData = ParaQueryUtil.getExtractorData(trac.getDocument()
					.getPdf(), trac.getSupplier(), trac.getPl());
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

	public void saveQAChecksAction(String checker, String engname)
	{
		Session session = null;

		try
		{
			session = SessionUtil.getSession();
			ArrayList<QAChecksDTO> inputparts = new ArrayList<>();
			ArrayList<QAChecksDTO> affectedparts = new ArrayList<>();
			ArrayList<QAChecksDTO> allparts = new ArrayList<>();
			ArrayList<String> sheetHeader = getHeader();
			saved = true;
			int CheckpartidIndex = sheetHeader.indexOf("CheckPartID");
			int ComidIndex = sheetHeader.indexOf("Comid");
			int statusIndex = sheetHeader.indexOf("Status");
			int RightValueIndex = sheetHeader.indexOf("RightValue");
			int PLcell = sheetHeader.indexOf("ProductLine");
			int Titleidx = sheetHeader.indexOf("DatasheetTitle");
			int supcell = sheetHeader.indexOf("Vendor");
			int partcell = sheetHeader.indexOf("Part");
			int Flagcell = sheetHeader.indexOf("Flag");
			int NanAlphaPartindex = sheetHeader.indexOf("NanAlphaPart");
			// int Partindex = sheetHeader.indexOf("Part");
			int FeatureNameindex = sheetHeader.indexOf("FeatureName");
			int FeatureValueindex = sheetHeader.indexOf("FeatureValue");
			String FeatureName = "";
			String FeatureValue = "";
			ArrayList<ArrayList<String>> fileData = readSpreadsheet(1);
			for(int i = 0; i < fileData.size(); i++)
			{
				QAChecksDTO qachk = new QAChecksDTO();
				ArrayList<String> partData = fileData.get(i);
				String status = partData.get(statusIndex);
				String RightValue = partData.get(RightValueIndex);
				String Vendor = partData.get(supcell);
				String Part = partData.get(partcell);
				String checkpartid = partData.get(CheckpartidIndex);
				if((!status.equals(StatusName.WaittingQAChecks) && !status.equals("Exception"))
						&& RightValue.isEmpty())
				{
					MainWindow.glass.setVisible(false);
					saved = false;
					JOptionPane.showMessageDialog(null, "You Must Enter RightValue");
					return;
				}
				if(status.equals(StatusName.UpdateMask) && !(Part.length() == RightValue.length()))
				{
					MainWindow.glass.setVisible(false);
					saved = true;
					JOptionPane.showMessageDialog(null, "New Mask Must be as Length as Part ");
					return;
				}
				long Comid = Long.valueOf(partData.get(ComidIndex));
				String ProductLine = partData.get(PLcell);
				String DatasheetTitle = partData.get(Titleidx);

				String Flag = partData.get(Flagcell);
				String NanAlphaPart = partData.get(NanAlphaPartindex);
				if(checker.equals(StatusName.MaskMultiData)
						|| checker.equals(StatusName.RootPartChecker)
						|| checker.equals(StatusName.generic_part))
				{
					FeatureName = partData.get(FeatureNameindex);
					FeatureValue = partData.get(FeatureValueindex);
				}
				qachk.setNanAlphaPart(NanAlphaPart);
				PartComponent part = DataDevQueryUtil.getComponentBycomid(Comid);
				qachk.setPart(part);
				qachk.setFlag(Flag);
				qachk.setVendor(part.getSupplierId());
				qachk.setDatasheet(part.getDocument());
				qachk.setDatasheetTitle(DatasheetTitle);
				qachk.setMask(part.getMasterPartMask());
				qachk.setFamily(part.getFamily());
				qachk.setNewValue(RightValue);
				Pl pl = ParaQueryUtil.getPlByPlName(session, ProductLine);
				qachk.setProductLine(pl);
				qachk.setEngname(engname);
				qachk.setChecker(checker);
				qachk.setStatus(status);
				qachk.setCheckpartid(Long.valueOf(checkpartid));
				if(checker.equals(StatusName.MaskMultiData)
						|| checker.equals(StatusName.RootPartChecker)
						|| checker.equals(StatusName.generic_part))
				{
					qachk.setFeatureName(FeatureName);
					qachk.setFeatureValue(FeatureValue);
				}
				if(Flag.equals("InputPart"))
				{
					inputparts.add(qachk);
				}
				else
				{
					affectedparts.add(qachk);
				}
				allparts.add(qachk);
			}
			DataDevQueryUtil.updateqacheckspart(inputparts);
			DataDevQueryUtil.updateqacheckspart(affectedparts);
			DataDevQueryUtil.updateqapartsstatus(allparts);
			MainWindow.glass.setVisible(false);
			JOptionPane.showMessageDialog(null, "Saving Data Finished");
		}catch(Exception e)
		{
			saved = false;
			MainWindow.glass.setVisible(false);
			JOptionPane.showMessageDialog(null, "Can't Save Data");
			e.printStackTrace();
		}finally
		{
			session.close();

		}

	}

	public void saveQAexceptionAction(String checker, String engname, String screen)
	{
		Session session = null;

		try
		{
			session = SessionUtil.getSession();
			ArrayList<QAChecksDTO> allparts = new ArrayList<>();
			ArrayList<String> sheetHeader = getHeader();
			// int fbcommentIndex = sheetHeader.indexOf("FBComment");
			int ComidIndex = sheetHeader.indexOf("Comid");
			int statusIndex = sheetHeader.indexOf(screen + "Status");
			int commentIndex = sheetHeader.indexOf(screen + "Comment");
			int PLcell = sheetHeader.indexOf("ProductLine");
			int chkpartidx = sheetHeader.indexOf("CheckPartID");
			int Titleidx = sheetHeader.indexOf("DatasheetTitle");
			int Flagcell = sheetHeader.indexOf("Flag");
			int NanAlphaPartindex = sheetHeader.indexOf("NanAlphaPart");
			int FeatureNameindex = sheetHeader.indexOf("FeatureName");
			int FeatureValueindex = sheetHeader.indexOf("FeatureValue");
			String FeatureName = "";
			String FeatureValue = "";
			ArrayList<ArrayList<String>> fileData = readSpreadsheet(1);
			saved = true;
			for(int i = 0; i < fileData.size(); i++)
			{
				QAChecksDTO qachk = new QAChecksDTO();
				ArrayList<String> partData = fileData.get(i);
				String status = partData.get(statusIndex);
				String Comment = partData.get(commentIndex);
				if(status.equals(StatusName.reject) && Comment.isEmpty())
				{
					saved = false;
					MainWindow.glass.setVisible(false);
					JOptionPane.showMessageDialog(null, "You Must Enter Comment if Rejected");
					return;
				}
				long Comid = Long.valueOf(partData.get(ComidIndex));
				String ProductLine = partData.get(PLcell);
				String DatasheetTitle = partData.get(Titleidx);
				String Flag = partData.get(Flagcell);
				String NanAlphaPart = partData.get(NanAlphaPartindex);
				String chkpart = partData.get(chkpartidx);
				// String fbcomment = partData.get(fbcommentIndex);
				if(checker.equals(StatusName.MaskMultiData)
						|| checker.equals(StatusName.RootPartChecker)
						|| checker.equals(StatusName.generic_part))
				{
					FeatureName = partData.get(FeatureNameindex);
					FeatureValue = partData.get(FeatureValueindex);
				}
				qachk.setNanAlphaPart(NanAlphaPart);
				PartComponent part = DataDevQueryUtil.getComponentBycomid(Comid);
				qachk.setPart(part);
				qachk.setVendor(part.getSupplierId());
				qachk.setCheckpartid(Long.valueOf(chkpart));
				System.err.println(part.getDocument().getId());
				qachk.setDatasheet(part.getDocument());
				qachk.setDatasheetTitle(DatasheetTitle);
				qachk.setMask(part.getMasterPartMask());
				qachk.setFamily(part.getFamily());
				qachk.setNewValue(Comment);
				Pl pl = ParaQueryUtil.getPlByPlName(session, ProductLine);
				System.err.println(pl.getId());
				qachk.setProductLine(pl);
				qachk.setEngname(engname);
				qachk.setChecker(checker);
				qachk.setStatus(status);
				qachk.setFlag(Flag);
				if(checker.equals(StatusName.MaskMultiData)
						|| checker.equals(StatusName.RootPartChecker)
						|| checker.equals(StatusName.generic_part))
				{
					qachk.setFeatureName(FeatureName);
					qachk.setFeatureValue(FeatureValue);
				}

				allparts.add(qachk);
			}
			DataDevQueryUtil.updateqaexceptionspart(allparts, screen);
			MainWindow.glass.setVisible(false);
			JOptionPane.showMessageDialog(null, "Saving Data Finished");
		}catch(Exception e)
		{
			MainWindow.glass.setVisible(false);
			saved = false;
			JOptionPane.showMessageDialog(null, "Can't Save Data");
			e.printStackTrace();
		}finally
		{
			session.close();

		}

	}

	private boolean isRowValuesApproved(XCellRange xcellrange, int lastColNum)
			throws IndexOutOfBoundsException
	{
		relatedFeature = new ArrayList<ArrayList<String>>();
		boolean appFlag = true;
		List<String> paraData = new ArrayList<String>();
		String missedFet = "", needApp = "", space = "";
		ArrayList<String> row = null;
		for(int j = startParametricFT; j < lastColNum; j++)
		{
			row = new ArrayList<String>();
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
				// npiCellNo = HeaderList.size();
				XCell npiFlagcell = xcellrange.getCellByPosition(npiCellNo, 0);
				String npiFlag = getCellText(npiFlagcell).getString();
				if(npiFlag.equalsIgnoreCase("Yes") && doneFets.indexOf(fetName) != -1)
				{
					missedFet = "Missed Done Flag Feature";
					canSave = false;
				}

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
				row.add(fetName);
				row.add(celldata);
				relatedFeature.add(row);
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

	private boolean fetValsHaveSpaces(XCellRange xcellrange, int lastColNum)
			throws IndexOutOfBoundsException
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

	public void setExtractionData1(String pdf, String supplierName, String plName, int pdfRow)
	{

		try
		{
			// Pdf pdf = ParaQueryUtil.getPdfBySeUrl(pdfUrl);
			Document doc = ParaQueryUtil.getDocumnetByPdfUrl(pdf);
			Pl pl = ParaQueryUtil.getPlByPlName(plName);
			Supplier supplier = ParaQueryUtil.getSupplierByName(supplierName);
			// Map<String, List<String>> partsData = ParametricQueryUtil.getExtractorData(pdfUrl, supplierName, plName);
			Map<String, List<String>> partsData = ParaQueryUtil.getExtractorData(doc.getPdf(),
					supplier, pl);
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

	private Map<String, String> readRowValues(XCellRange xcellrange, int lastColNum)
			throws IndexOutOfBoundsException
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

	private Map<String, String> readRowValues(ArrayList<String> partData)
			throws IndexOutOfBoundsException
	{

		Map<String, String> fetValues = new HashMap<String, String>();
		for(int j = startParametricFT; j < endParametricFT; j++)
		{
			XCell fetCell = xHdrUnitrange.getCellByPosition(j, 1);
			String fetName = getCellText(fetCell).getString();
			String fetvalue = partData.get(j);
			// System.out.println(plFetIds.get(j-startParametricFT)+"_Id_"+fetName + " cell " + j + "=" + fetvalue);
			fetValues.put(plFetIds.get(j - startParametricFT) + "_Id_" + fetName, fetvalue);
		}
		return fetValues;
	}

	private void writeValidtionStatus(XCellRange xcellrange, boolean flag)
			throws IndexOutOfBoundsException
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

			getCellText(xcellrange.getCellByPosition(valCommentColumn, 0)).setString(
					partvalidation.getComment());
			getCellText(xcellrange.getCellByPosition(valStatusColumn, 0)).setString(
					partvalidation.getStatus());
			getCellText(xcellrange.getCellByPosition(valTaxonomyColumn, 0)).setString(
					partvalidation.getTaxonomy());
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
					Document document = (Document) session.load(Document.class, doc.getDocument()
							.getId());
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
						cell.setText((s = clientutils.getSupplierUrlByDocument(document).getUrl()) == null ? ""
								: s);
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
				cell.SetApprovedValues(prepShowAllStatusList(),
						getCellRangByPosission(8, AppContext.AllnewDocuments.size(), StatrtRecord));
				cell = getCellByPosission(9, startrow);
				cell.SetApprovedValues(prepShowAllCommentList(),
						getCellRangByPosission(9, AppContext.AllnewDocuments.size(), StatrtRecord));
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
			int startRow = StatrtRecord;
			Cell cell;

			XCellRange xCellRange = this.sheet.getCellRangeByPosition(0, startRow, 12, list.size());
			XCellRangeData xData = UnoRuntime.queryInterface(XCellRangeData.class, xCellRange);

			Object[][] aValues = new Object[list.size()][13];
			Date sd = new Date();

			for(int i = 0; i < list.size(); i++)
			{
				TableInfoDTO docInfoDTO = list.get(i);

				aValues[i][0] = docInfoDTO.getPdfUrl() == null ? "" : docInfoDTO.getPdfUrl();
				aValues[i][1] = docInfoDTO.getSupplierName() == null ? "" : docInfoDTO
						.getSupplierName();
				aValues[i][2] = docInfoDTO.getSupplierSiteUrl() == null ? "" : docInfoDTO
						.getSupplierSiteUrl();
				aValues[i][3] = docInfoDTO.getPlName() == null ? "" : docInfoDTO.getPlName();
				aValues[i][4] = docInfoDTO.getTaskType() == null ? "" : docInfoDTO.getTaskType();
				aValues[i][5] = docInfoDTO.getDownloadDate() == null ? "" : docInfoDTO
						.getDownloadDate();
				aValues[i][6] = docInfoDTO.getCerDate() == null ? "" : docInfoDTO.getCerDate();
				aValues[i][7] = docInfoDTO.getTitle() == null ? "" : docInfoDTO.getTitle();
				aValues[i][8] = docInfoDTO.getOnlineLink() == null ? "" : docInfoDTO
						.getOnlineLink();
				aValues[i][9] = docInfoDTO.getPagesCount();
				aValues[i][10] = docInfoDTO.getExtracted() == null ? "" : docInfoDTO.getExtracted();
				aValues[i][11] = docInfoDTO.getNewsLink() == null ? "" : docInfoDTO.getNewsLink();
				aValues[i][12] = docInfoDTO.getTaxPath() == null ? "" : docInfoDTO.getTaxPath();
				// aValues[i][13] = prepShowAllStatusList().toString();
				// aValues[i][14] = prepShowAllStatusList().toString();
				// aValues[i][15] = prepShowAllStatusList().toString();

				// // String pdfUrl = docInfoDTO.getPdfUrl();
				// // Document document = ParaQueryUtil.getDocumnetByPdfUrl(pdfUrl);
				// cell = getCellByPosission(0, startrow);
				// cell.setText("document.getPdf().getSeUrl()");
				// cell.setText(docInfoDTO.getPdfUrl());
				// cell = getCellByPosission(1, startrow);
				// cell.setText(docInfoDTO.getSupplierName());
				// cell = getCellByPosission(2, startrow);
				// // String supplierUrl = ParaQueryUtil.getSupplierUrlByDocument(document);
				// // cell.setText(supplierUrl);
				// cell.setText(docInfoDTO.getSupplierSiteUrl());
				// cell = getCellByPosission(3, startrow);
				// cell.setText(docInfoDTO.getPlName());
				// cell = getCellByPosission(4, startrow);
				// cell.setText(docInfoDTO.getTaskType());
				// cell = getCellByPosission(5, startrow);
				// // String date = document.getPdf().getDownloadDate().toString();
				// cell.setText(docInfoDTO.getDownloadDate());
				// cell = getCellByPosission(6, startrow);
				// // date = document.getPdf().getCerDate().toString();
				// cell.setText(docInfoDTO.getCerDate());
				// cell = getCellByPosission(7, startrow);
				// // cell.setText(document.getTitle() == null ? "" : document.getTitle());
				// cell.setText(docInfoDTO.getTitle());
				// cell = getCellByPosission(8, startrow);
				// // String onloneLink = ParaQueryUtil.getOnlineLinkByDocument(document);
				// // cell.setText(onloneLink);
				// cell.setText(docInfoDTO.getOnlineLink());
				// cell = getCellByPosission(9, startrow);
				// // long pagesNo = document.getPdf().getPageCount();
				// cell.setText("" + docInfoDTO.getPagesCount());
				// cell = getCellByPosission(10, startrow);
				// cell.setText(docInfoDTO.getExtracted());
				// cell = getCellByPosission(11, startrow);
				// // String newsLink = ParaQueryUtil.getNewsLink(document.getPdf().getId());
				// cell.setText(docInfoDTO.getNewsLink());
				// cell = getCellByPosission(12, startrow);
				// // String taxPath = ParaQueryUtil.getTaxonomyPath(document.getPdf().getId());
				// cell.setText(docInfoDTO.getTaxPath());
				//
				// // String url = documgetSupplierPl().getSupplier().getSiteUrl();
				// // System.out.println("url"+row.getPdf().getSupplierUrl().getUrl());
				// // String s = supplierUrl.getUrl();
				//
				// cell = getCellByPosission(13, startrow);
				// cell.SetApprovedValues(prepShowAllStatusList(),
				// getCellRangByPosission(13, list.size(), startrow));
				// cell = getCellByPosission(14, startrow);
				// cell.SetApprovedValues(prepShowAllCommentList(),
				// getCellRangByPosission(14, list.size(), startrow));
				// cell = getCellByPosission(15, startrow);
				// cell.SetApprovedValues(prepShowAllTaxonomies(),
				// getCellRangByPosission(15, list.size(), startrow));

				startRow++;
			}
			xData.setDataArray(aValues);

			xCellRange = this.sheet.getCellRangeByPosition(13, StatrtRecord, 13, list.size());
			assignValuesListToRange(xCellRange, prepShowAllStatusList());
			xCellRange = this.sheet.getCellRangeByPosition(14, StatrtRecord, 14, list.size());
			assignValuesListToRange(xCellRange, prepShowAllCommentList());
			xCellRange = this.sheet.getCellRangeByPosition(15, StatrtRecord, 15, list.size());
			assignValuesListToRange(xCellRange, prepShowAllTaxonomies());
			Date ed = new Date();
			System.out.println("~~~~ Time : ~~~~ " + sd + "  ****  " + ed);
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}

	private void assignValuesListToRange(XCellRange cellRange, List<String> values)
			throws IllegalArgumentException, UnknownPropertyException, PropertyVetoException,
			WrappedTargetException
	{
		XPropertySet xCellRangePropSet = UnoRuntime.queryInterface(XPropertySet.class, cellRange);
		Any an = (Any) xCellRangePropSet.getPropertyValue("ValidationLocal");
		XPropertySet xValidPropSet = (XPropertySet) an.getObject();
		xValidPropSet.setPropertyValue("Type", ValidationType.LIST);
		xValidPropSet.setPropertyValue("ShowList", (short) 1);

		// condition
		XSheetCondition xCondition = (XSheetCondition) UnoRuntime.queryInterface(
				XSheetCondition.class, xValidPropSet);
		xCondition.setOperator(ConditionOperator.EQUAL);
		StringBuffer conditions = new StringBuffer();
		if(values != null)
		{
			for(String s : values)
			{
				conditions.append("\"" + s + "\";");
				if(conditions.toString().length() > 26000)
				{
					break;
				}
			}
			xCondition.setFormula1(conditions.toString());
			xCellRangePropSet.setPropertyValue("ValidationLocal", xValidPropSet);
		}

		xCellRangePropSet = UnoRuntime.queryInterface(XPropertySet.class, cellRange);
		xCellRangePropSet.setPropertyValue("CellBackColor", new Integer(0xFFFFCC));
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
			XCellRangeAddressable xcellRangeAddressable = (com.sun.star.sheet.XCellRangeAddressable) UnoRuntime
					.queryInterface(com.sun.star.sheet.XCellRangeAddressable.class,
							sheet.getCellRangeByPosition(0, 0, (int) right, RowSelectedRange));
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

	public void setHistoryHeader(ArrayList<String> header)
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

			String hdrUintRange = "A" + 1 + ":m" + 1;
			xHdrUnitrange = sheet.getCellRangeByName(hdrUintRange);
			setRangColor(xHdrUnitrange, 0x8A8C8B);
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
			String hdrUintRange = "A" + 1 + ":N" + 1;
			xHdrUnitrange = sheet.getCellRangeByName(hdrUintRange);
			setRangColor(xHdrUnitrange, 0x8A8C8B);
			hdrUintRange = "A" + 2 + ":H" + 500;
			xHdrUnitrange = sheet.getCellRangeByName(hdrUintRange);
			setRangColor(xHdrUnitrange, 0xEAAFD6);
			hdrUintRange = "G" + 2 + ":N" + 500;
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

				String feedbackStatus = DataDevQueryUtil.sendFeedbackToSourcingTeam(userName,
						pdfUrl, plName, docFeedbackComment, revUrl, rightTax);
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

	public void validateQAReview()
	{
		ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
		XCellRange xcellrange = null;
		int lastColNum = HeaderList.size();
		String lastColumn = getColumnName(lastColNum);
		ArrayList<String> sheetHeader = getHeader();
		int statusIndex = sheetHeader.indexOf("Status");
		int CommentIndex = sheetHeader.indexOf("Comment");
		int WrongFeatureIndex = sheetHeader.indexOf("Wrong Feature");
		int ValidationCommentIndex = sheetHeader.indexOf("Validation Comment");
		int plIndex = sheetHeader.indexOf("Taxonomy");
		int doneflagIndex = sheetHeader.indexOf("Done Flag");
		int taskTypeIndex = sheetHeader.indexOf("Task Type");

		canSave = true;
		int lastRow = getLastRow();
		for(int i = 3; i < lastRow + 1; i++)
		{
			try
			{
				String error = "";
				String seletedRange = "A" + i + ":" + lastColumn + i;
				xcellrange = sheet.getCellRangeByName(seletedRange);
				XCell wrongfetCell;
				XCell PlCell;
				XCell statusCell = xcellrange.getCellByPosition(statusIndex, 0);
				String status = getCellText(statusCell).getString();
				XCell commentCell = xcellrange.getCellByPosition(CommentIndex, 0);
				String comment = getCellText(commentCell).getString();
				wrongfetCell = xcellrange.getCellByPosition(WrongFeatureIndex, 0);
				String wrongfeatures = getCellText(wrongfetCell).getString();
				PlCell = xcellrange.getCellByPosition(plIndex, 0);
				String Plname = getCellText(PlCell).getString();
				XCell doneflagCell = xcellrange.getCellByPosition(doneflagIndex, 0);
				String doneflag = getCellText(doneflagCell).getString();
				XCell taskTypeCell = xcellrange.getCellByPosition(taskTypeIndex, 0);
				String taskType = getCellText(taskTypeCell).getString();
				setCellColore(wrongfetCell, 0xFFFFFF);
				setCellColore(commentCell, 0xFFFFFF);
				setCellColore(statusCell, 0xFFFFFF);

				if(status.equals("R"))
				{
					if(wrongfeatures.trim().equals(""))
					{
						error += "Wrong Features is empty |";
						// getCellText(xcellrange.getCellByPosition(ValidationCommentIndex, 0)).setString("Wrong Features is empty");
						setCellColore(wrongfetCell, 0xD2254D);
						canSave = false;
						// break;
					}
					if(comment.trim().equals(""))
					{
						error += "Wrong Comment |";
						// getCellText(xcellrange.getCellByPosition(ValidationCommentIndex, 0)).setString("Wrong Comment");
						setCellColore(commentCell, 0xD2254D);
						canSave = false;
						// break;
					}
					else
					{
						if(wrongfeatures.contains("|"))
						{
							String[] features = wrongfeatures.split("\\|");
							if(features.length > 0)
							{
								List<String> plfets = ParaQueryUtil.getPlFeautreNames(Plname);
								for(int f = 0; f < features.length; f++)
								{
									if(!plfets.contains("F_" + features[f]))
									{
										error += "Feature No (" + (f + 1) + ") is wrong |";
										// getCellText(xcellrange.getCellByPosition(ValidationCommentIndex, 0)).setString("Feature No (" + (f + 1) +
										// ") is wrong");
										setCellColore(wrongfetCell, 0xD2254D);
										canSave = false;
										break;
									}
								}
								if(comment.contains("|"))
								{
									String[] comments = comment.split("\\|");
									if(features.length != comments.length)
									{
										error += "comment must be as count as the features |";
										setCellColore(commentCell, 0xD2254D);
										canSave = false;
									}
								}
							}
						}
						else
						{
							// String[] features = wrongfeatures.split("|");
							List<String> plfets = ParaQueryUtil.getPlFeautreNames(Plname);
							if(!plfets.contains("F_" + wrongfeatures))
							{
								error += "Feature is wrong |";
								// getCellText(xcellrange.getCellByPosition(ValidationCommentIndex, 0)).setString("Feature is wrong");
								setCellColore(wrongfetCell, 0xD2254D);
								canSave = false;
								break;
							}
						}
					}
				}
				if(status.equals("W"))
				{
					if(comment.trim().equals(""))
					{
						error += "Wrong Comment |";
						// getCellText(xcellrange.getCellByPosition(ValidationCommentIndex, 0)).setString("Wrong Comment");
						setCellColore(commentCell, 0xD2254D);
						canSave = false;
						// break;
					}
				}
				if((status.equals("A") || status.equals("S")) && doneflag.equals("No"))
				{
					error += "can't save Status (A,S) on Parts notDone flag |";
					// getCellText(xcellrange.getCellByPosition(ValidationCommentIndex, 0)).setString("Wrong Comment");
					setCellColore(statusCell, 0xD2254D);
					canSave = false;
				}
				if(!canSave)
				{
					getCellText(xcellrange.getCellByPosition(ValidationCommentIndex, 0)).setString(
							error);
				}
				else
					getCellText(xcellrange.getCellByPosition(ValidationCommentIndex, 0)).setString(
							"No Problem");

			}catch(Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	public void validateQASummary()
	{
		ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
		XCellRange xcellrange = null;
		int lastColNum = HeaderList.size();
		String lastColumn = getColumnName(lastColNum);
		ArrayList<String> sheetHeader = getHeader();
		int sampleflagIndex = sheetHeader.indexOf("Sample QA Flag");
		int finalflagIndex = sheetHeader.indexOf("Final QA Flag");
		int ValidationCommentIndex = sheetHeader.indexOf("Validation Comment");
		int doneflagIndex = sheetHeader.indexOf("Done Flag");

		canSave = true;
		int lastRow = getLastRow();
		for(int i = 3; i < lastRow + 1; i++)
		{
			try
			{
				String error = "";
				String seletedRange = "A" + i + ":" + lastColumn + i;
				xcellrange = sheet.getCellRangeByName(seletedRange);
				XCell sampleflagCell = xcellrange.getCellByPosition(sampleflagIndex, 0);
				String sampleflag = getCellText(sampleflagCell).getString();
				XCell finalflagCell = xcellrange.getCellByPosition(finalflagIndex, 0);
				String finalflag = getCellText(finalflagCell).getString();
				XCell doneflagCell = xcellrange.getCellByPosition(doneflagIndex, 0);
				String donelflag = getCellText(doneflagCell).getString();
				setCellColore(sampleflagCell, 0xFFFFFF);
				setCellColore(finalflagCell, 0xFFFFFF);
				if((finalflag.equals("R") || finalflag.equals("W"))
						|| (!finalflag.isEmpty() && !finalflag.equals("A")
								&& !finalflag.equals("S") && !finalflag.equals("Fast")))
				{
					error += "Finalflag Must be in (A,S,Fast) |";
					setCellColore(finalflagCell, 0xD2254D);
					canSave = false;
				}
				else
				{
					if((finalflag.equals("A") || finalflag.equals("S")) && donelflag.equals("No"))
					{
						error += "Finalflag Can't be (A,S) with NotDone Parts |";
						setCellColore(finalflagCell, 0xD2254D);
						canSave = false;
					}
				}

				if(finalflag.trim().equals("") && sampleflag.trim().equals(""))
				{
					error += "No flag of this part |";
					setCellColore(finalflagCell, 0xD2254D);
					canSave = false;
				}

				if(!canSave && !error.equals(""))
				{
					getCellText(xcellrange.getCellByPosition(ValidationCommentIndex, 0)).setString(
							error);
				}
				else
					getCellText(xcellrange.getCellByPosition(ValidationCommentIndex, 0)).setString(
							"No Problem");

			}catch(Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	public void setqaChecksheader(String checkerType)
	{
		try
		{

			HeaderList = new ArrayList<Cell>();

			Cell cell = getCellByPosission(0, 0);
			cell.setText("CheckPartID");
			HeaderList.add(cell);
			cell = getCellByPosission(1, 0);
			cell.setText("Comid");
			HeaderList.add(cell);
			cell = getCellByPosission(2, 0);
			cell.setText("NanAlphaPart");
			HeaderList.add(cell);
			cell = getCellByPosission(3, 0);
			cell.setText("Flag");
			HeaderList.add(cell);
			cell = getCellByPosission(4, 0);
			cell.setText("Part");
			HeaderList.add(cell);
			cell = getCellByPosission(5, 0);
			cell.setText("Vendor");
			HeaderList.add(cell);
			cell = getCellByPosission(6, 0);
			cell.setText("Datasheet");
			HeaderList.add(cell);
			cell = getCellByPosission(7, 0);
			cell.setText("DatasheetTitle");
			HeaderList.add(cell);
			cell = getCellByPosission(8, 0);
			cell.setText("ProductLine");
			HeaderList.add(cell);
			cell = getCellByPosission(9, 0);
			cell.setText("Mask");
			HeaderList.add(cell);
			cell = getCellByPosission(10, 0);
			cell.setText("Family");
			HeaderList.add(cell);

			if(checkerType.equals(StatusName.NonAlphaMultiSupplier))
			{
				cell = getCellByPosission(11, 0);
				cell.setText("Status");
				HeaderList.add(cell);
				cell = getCellByPosission(12, 0);
				cell.setText("RightValue");
				HeaderList.add(cell);
				statusValues.add(StatusName.Exception);
				statusValues.add(StatusName.WrongTax);
				statusValues.add(StatusName.WrongPart);
				statusValues.add(StatusName.WaittingQAChecks);
			}
			else if(checkerType.equals(StatusName.MaskMultiSupplier))
			{
				cell = getCellByPosission(11, 0);
				cell.setText("Status");
				HeaderList.add(cell);
				cell = getCellByPosission(12, 0);
				cell.setText("RightValue");
				HeaderList.add(cell);
				statusValues.add(StatusName.Exception);
				statusValues.add(StatusName.WrongTax);
				statusValues.add(StatusName.WrongPart);
				statusValues.add(StatusName.UpdateMask);
				statusValues.add(StatusName.WaittingQAChecks);
			}
			else if(checkerType.equals(StatusName.FamilyMultiSupplier))
			{
				cell = getCellByPosission(11, 0);
				cell.setText("Status");
				HeaderList.add(cell);
				cell = getCellByPosission(12, 0);
				cell.setText("RightValue");
				HeaderList.add(cell);
				statusValues.add(StatusName.Exception);
				statusValues.add(StatusName.WrongTax);
				statusValues.add(StatusName.WrongPart);
				statusValues.add(StatusName.UpdateFamily);
				statusValues.add(StatusName.WaittingQAChecks);
			}
			else if(checkerType.equals(StatusName.MaskMultiData))
			{

				cell = getCellByPosission(11, 0);
				cell.setText("FeatureName");
				HeaderList.add(cell);
				cell = getCellByPosission(12, 0);
				cell.setText("FeatureValue");
				HeaderList.add(cell);
				cell = getCellByPosission(13, 0);
				cell.setText("Status");
				HeaderList.add(cell);
				cell = getCellByPosission(14, 0);
				cell.setText("RightValue");
				HeaderList.add(cell);

				statusValues.add(StatusName.Exception);
				statusValues.add(StatusName.WrongPart);
				statusValues.add(StatusName.UpdateParametricData);
				statusValues.add(StatusName.UpdateMask);
				statusValues.add(StatusName.WaittingQAChecks);
			}
			else if(checkerType.equals(StatusName.RootPartChecker))
			{

				cell = getCellByPosission(11, 0);
				cell.setText("FeatureName");
				HeaderList.add(cell);
				cell = getCellByPosission(12, 0);
				cell.setText("FeatureValue");
				HeaderList.add(cell);
				cell = getCellByPosission(13, 0);
				cell.setText("Status");
				HeaderList.add(cell);
				cell = getCellByPosission(14, 0);
				cell.setText("RightValue");
				HeaderList.add(cell);

				statusValues.add(StatusName.Exception);
				statusValues.add(StatusName.WrongPart);
				statusValues.add(StatusName.UpdateParametricData);
				statusValues.add(StatusName.UpdateFamily);
				statusValues.add(StatusName.WaittingQAChecks);
			}
			else if(checkerType.equals(StatusName.generic_part))
			{
				cell = getCellByPosission(11, 0);
				cell.setText("Generic");
				HeaderList.add(cell);
				cell = getCellByPosission(12, 0);
				cell.setText("FeatureName");
				HeaderList.add(cell);
				cell = getCellByPosission(13, 0);
				HeaderList.add(cell);
				cell.setText("FeatureValue");
				cell = getCellByPosission(14, 0);
				cell.setText("Status");
				HeaderList.add(cell);
				cell = getCellByPosission(15, 0);
				cell.setText("RightValue");
				HeaderList.add(cell);

				statusValues.add(StatusName.Exception);
				statusValues.add(StatusName.WrongPart);
				statusValues.add(StatusName.UpdateParametricData);
				statusValues.add(StatusName.UpdateGeneric);
				statusValues.add(StatusName.WaittingQAChecks);
			}

		}catch(Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void setqaexceptionheader(String checkerType, String team)
	{
		try
		{

			HeaderList = new ArrayList<Cell>();

			Cell cell = getCellByPosission(0, 0);
			cell.setText("CheckPartID");
			HeaderList.add(cell);
			cell = getCellByPosission(1, 0);
			cell.setText("Comid");
			HeaderList.add(cell);
			cell = getCellByPosission(2, 0);
			cell.setText("NanAlphaPart");
			HeaderList.add(cell);
			cell = getCellByPosission(3, 0);
			cell.setText("Flag");
			HeaderList.add(cell);
			cell = getCellByPosission(4, 0);
			cell.setText("Part");
			HeaderList.add(cell);
			cell = getCellByPosission(5, 0);
			cell.setText("Vendor");
			HeaderList.add(cell);
			cell = getCellByPosission(6, 0);
			cell.setText("Datasheet");
			HeaderList.add(cell);
			cell = getCellByPosission(7, 0);
			cell.setText("DatasheetTitle");
			HeaderList.add(cell);
			cell = getCellByPosission(8, 0);
			cell.setText("ProductLine");
			HeaderList.add(cell);
			cell = getCellByPosission(9, 0);
			cell.setText("Mask");
			HeaderList.add(cell);
			cell = getCellByPosission(10, 0);
			cell.setText("Family");
			HeaderList.add(cell);
			cell = getCellByPosission(11, 0);
			cell.setText(team + "Status");
			HeaderList.add(cell);
			cell = getCellByPosission(12, 0);
			cell.setText(team + "Comment");
			HeaderList.add(cell);

			cell = getCellByPosission(13, 0);
			if(team == "QA")
				cell.setText("DDComment");
			else
				cell.setText("QAComment");
			HeaderList.add(cell);
			// cell = getCellByPosission(14, 0);
			// if(team == "QA")
			// cell.setText("LastQAComment");
			// else
			// cell.setText("LastDDComment");
			// HeaderList.add(cell);

			if(checkerType.equals(StatusName.MaskMultiData)
					|| checkerType.equals(StatusName.RootPartChecker))
			{
				cell = getCellByPosission(14, 0);
				cell.setText("FeatureName");
				HeaderList.add(cell);
				cell = getCellByPosission(15, 0);
				cell.setText("FeatureValue");
				HeaderList.add(cell);
			}
			if(checkerType.equals(StatusName.generic_part))
			{
				cell = getCellByPosission(14, 0);
				cell.setText("Generic");
				HeaderList.add(cell);
				cell = getCellByPosission(15, 0);
				cell.setText("FeatureName");
				HeaderList.add(cell);
				cell = getCellByPosission(16, 0);
				cell.setText("FeatureValue");
				HeaderList.add(cell);
			}

			statusValues.add(StatusName.approved);
			statusValues.add(StatusName.reject);

		}catch(Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public boolean validateSeparation()
	{
		try
		{
			ArrayList<String> row;
			XCellRange xcellrange = null;
			int lastColNum = HeaderList.size();
			String lastColumn = getColumnName(lastColNum);
			ArrayList<String> sheetHeader = getHeader();
			int validationresultidx = sheetHeader.indexOf("Validation result");
			ArrayList<ArrayList<String>> fileData = readSpreadsheet(1);
			for(int i = 0; i < fileData.size(); i++)
			{
				String seletedRange = "A" + i + ":" + lastColumn + i;
				xcellrange = sheet.getCellRangeByName(seletedRange);
				System.out.println("Selected range " + seletedRange);
				row = fileData.get(i);
				List<String> result = ApprovedDevUtil.validateSeparation(row);
				if(result.get(0) != "" && result.get(1).equals("false"))
				{
					canSave = false;
				}
				getCellText(xcellrange.getCellByPosition(validationresultidx, 0)).setString(
						result.get(0));
				setRangColor(xcellrange, 0x088A0D);
			}

		}catch(IndexOutOfBoundsException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return canSave;
		// writeSheetData(validationResult, 1);
	}

}
