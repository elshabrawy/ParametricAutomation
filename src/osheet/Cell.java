package osheet;

import java.util.List;

import com.se.parametric.AppContext;
import com.sun.star.beans.Property;
import com.sun.star.beans.XPropertySet;
import com.sun.star.beans.XPropertySetInfo;
import com.sun.star.table.XCell;
import com.sun.star.table.XCellRange;
import com.sun.star.text.XText;
import com.sun.star.uno.Any;
import com.sun.star.uno.UnoRuntime;

public class Cell
{
	private int x;
	private int y;
	private XCell xCell;

	public Cell(XCell xcell, int x, int y)
	{
		this.xCell = xcell;
		this.x = x;
		this.y = y;
	}

	/**
	 * @return the xCell
	 */
	public XCell getxCell()
	{
		return xCell;
	}

	/**
	 * @param xCell
	 *            the xCell to set
	 */
	public void setxCell(XCell xCell)
	{
		this.xCell = xCell;
	}

	public void setText(String text) throws Exception
	{
		if(xCell == null)
			throw new Exception("Cell is null");

		getCellText(xCell).setString(text);

	}

	/**
	 * @return the x
	 */
	public int getX()
	{
		return x;
	}

	/**
	 * @param x
	 *            the x to set
	 */
	public void setX(int x)
	{
		this.x = x;
	}

	/**
	 * @return the y
	 */
	public int getY()
	{
		return y;
	}

	/**
	 * @param y
	 *            the y to set
	 */
	public void setY(int y)
	{
		this.y = y;
	}

	public String getText() throws Exception
	{
		if(xCell == null)
			throw new Exception("Cell is null");
		return getCellText(xCell).getString();
	}

	private final XText getCellText(XCell cell)
	{
		XText cellText = null;
		try
		{
			cellText = (XText) UnoRuntime.queryInterface(XText.class, cell);
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return cellText;
	}

	public void setCellColore(int color)
	{
		try
		{
			if(xCell == null)
				throw new Exception("Cell is null");

			XPropertySet xCellProps = (XPropertySet) UnoRuntime.queryInterface(XPropertySet.class, xCell);
			XPropertySetInfo prpset = xCellProps.getPropertySetInfo();
			xCellProps.setPropertyValue("CellBackColor", color);
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}

	public void clear()
	{
		getCellText(xCell).setString("");
	}

	public void SetApprovedValues(List<String> validationsList, XCellRange xcellrange)
	{
		try
		{
			com.sun.star.beans.XPropertySet xCellPropSet = (com.sun.star.beans.XPropertySet) UnoRuntime.queryInterface(com.sun.star.beans.XPropertySet.class, xCell);
			Any an = (Any) xCellPropSet.getPropertyValue("ValidationLocal");
			com.sun.star.beans.XPropertySet xValidPropSet = (com.sun.star.beans.XPropertySet) an.getObject();
			xValidPropSet.setPropertyValue("Type", com.sun.star.sheet.ValidationType.LIST);
			xValidPropSet.setPropertyValue("ShowList", (short) 1);

			// condition
			com.sun.star.sheet.XSheetCondition xCondition = (com.sun.star.sheet.XSheetCondition) UnoRuntime.queryInterface(com.sun.star.sheet.XSheetCondition.class, xValidPropSet);
			xCondition.setOperator(com.sun.star.sheet.ConditionOperator.EQUAL);
			StringBuffer conditions = new StringBuffer();
			if(validationsList != null)
				for(String s : validationsList)
				{
					conditions.append("\"" + s + "\";");
					if(conditions.toString().length() > 26000)
					{
						break;
					}
				}
			// System.out.println("approved values list Length:"+conditions.toString().length()+" : " + conditions.toString());
			xCondition.setFormula1(conditions.toString());

			if(xcellrange != null)
			{

				com.sun.star.beans.XPropertySet xCellrangPropSet = (com.sun.star.beans.XPropertySet) UnoRuntime.queryInterface(com.sun.star.beans.XPropertySet.class, xcellrange);
				xCellrangPropSet.setPropertyValue("CellBackColor", new Integer(0xFFFFCC));

				if(validationsList != null)
					xCellrangPropSet.setPropertyValue("ValidationLocal", xValidPropSet);

			}
		}catch(Exception ex)
		{
			ex.printStackTrace();
			AppContext.FirMessageError(ex.getMessage(), this.getClass(), ex);
		}
	}

	public void UpdateApprovedValues(String Approvedvalue, XCellRange xcellrange)
	{
		try
		{
			com.sun.star.beans.XPropertySet xCellPropSet = (com.sun.star.beans.XPropertySet) UnoRuntime.queryInterface(com.sun.star.beans.XPropertySet.class, xCell);
			Any an = (Any) xCellPropSet.getPropertyValue("ValidationLocal");
			com.sun.star.beans.XPropertySet xValidPropSet = (com.sun.star.beans.XPropertySet) an.getObject();
			com.sun.star.sheet.XSheetCondition xCondition = (com.sun.star.sheet.XSheetCondition) UnoRuntime.queryInterface(com.sun.star.sheet.XSheetCondition.class, xValidPropSet);
			// System.out.println(xCondition.getFormula1());
			xCondition.setFormula1(xCondition.getFormula1() + "\"" + Approvedvalue + "\";");
			if(xcellrange != null)
			{
				com.sun.star.beans.XPropertySet xCellrangPropSet = (com.sun.star.beans.XPropertySet) UnoRuntime.queryInterface(com.sun.star.beans.XPropertySet.class, xcellrange);
				xCellrangPropSet.setPropertyValue("ValidationLocal", xValidPropSet);

			}
		}catch(Exception ex)
		{
			ex.printStackTrace();
			AppContext.FirMessageError(ex.getMessage(), this.getClass(), ex);
		}
	}

}
