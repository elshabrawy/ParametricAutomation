package osheet;

public class SheetViewData
{
	private String viewdata = "100/60/0;0;tw:270;0/0/0/0/0/0/2/0/0/0/0;;0/0/0/0/0/0/2/0/0/0/0";
	private String selectedSheet;

	private CellPosition selectedCellposesions = new CellPosition();

	public void setViewdata(String viewdata)
	{
		this.viewdata = viewdata;
		Process();
	}

	private void Process()
	{
		selectedSheet = viewdata.replaceAll(".*\\;(\\d+)\\;tw\\:\\d*\\;(.*)", "$1");
		// System.out.println("Sheet "+selectedSheet);
		String[] listofsheets = viewdata.replaceAll(".*\\;(\\d+)\\;tw\\:\\d*\\;(.*)", "$2").split(";");
		// System.out.println("selected sheeeeeeeeeeeet"+listofsheets[Integer.parseInt(selectedSheet)]);
		selectedCellposesions.x = Integer.parseInt(listofsheets[Integer.parseInt(selectedSheet)].replaceAll("(\\d+)[/\\+](\\d+).*", "$1"));
		selectedCellposesions.y = Integer.parseInt(listofsheets[Integer.parseInt(selectedSheet)].replaceAll("(\\d+)[/\\+](\\d+).*", "$2"));

		System.out.println("sheet " + selectedSheet + " x " + selectedCellposesions.x + " y " + selectedCellposesions.y);
	}

	/**
	 * @return the selectedSheet
	 */
	public String getSelectedSheet()
	{
		return selectedSheet;
	}

	/**
	 * @param selectedSheet
	 *            the selectedSheet to set
	 */
	public void setSelectedSheet(String selectedSheet)
	{
		this.selectedSheet = selectedSheet;
	}

	/**
	 * @return the selectedCellposesions
	 */
	public CellPosition getSelectedCellposesions()
	{
		return selectedCellposesions;
	}

	/**
	 * @param selectedCellposesions
	 *            the selectedCellposesions to set
	 */
	public void setSelectedCellposesions(CellPosition selectedCellposesions)
	{
		this.selectedCellposesions = selectedCellposesions;
	}

	public static void main(String args[])
	{

		SheetViewData bv = new SheetViewData();

	}

}
