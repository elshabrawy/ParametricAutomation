package osheet;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Panel;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;

import com.sun.star.beans.PropertyValue;
import com.sun.star.comp.beans.NoConnectionException;
import com.sun.star.comp.beans.OOoBean;
import com.sun.star.container.XNamed;
import com.sun.star.document.XEventBroadcaster;
import com.sun.star.document.XEventListener;
import com.sun.star.frame.XModel;
import com.sun.star.sheet.XSpreadsheet;
import com.sun.star.sheet.XSpreadsheetDocument;
import com.sun.star.sheet.XSpreadsheetView;
import com.sun.star.table.XCell;
import com.sun.star.text.XText;
import com.sun.star.uno.Any;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;

/**
 * 
 * @author ahmed_nehad
 */
public class SheetPanel extends Panel
{

	protected OOoBean aBean;
	protected SheetViewData SVD = new SheetViewData();
	private boolean documentOpened = false;
	private XEventBroadcaster xEventBroad;
	private Properties culmns;
	protected JButton butSeparation, save;
	private Frame frame;
	public static int row, column;

	protected JButton btuSaveParts;

	/** Creates new form panle */
	public SheetPanel()
	{

		// this.frame = frame;
		initComponents();
		inits();

	}

	protected void laodprops()
	{
		try
		{
			culmns = new Properties();
			// culmns.load(ClassLoader.getSystemResourceAsStream("resources/LIST.DAT"));
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}

	protected String getCulmn(int i)
	{
		StringBuffer buf = new StringBuffer();
		System.out.println(i);
		do
		{
			if(i < 0)
				break;
			if(i <= 26)
			{
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

	/**
	 * @return the opened
	 */
	public boolean isOpened()
	{
		return documentOpened;
	}

	private void inits()
	{
		laodprops();
		// try
		// {
		// javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
		// }catch(Exception ex)
		// {
		// ex.printStackTrace();
		//
		// }
		aBean = new OOoBean();

		FlowLayout flout = new FlowLayout();

		Dimension dimension = new Dimension(100, 20);
		setLayout(new BorderLayout());
		setSize(new Dimension(200, 200));

		butSeparation = new JButton("Separate");
		butSeparation.setSize(dimension);

		add(aBean, BorderLayout.CENTER);

		// prepare sheet
		attachEventHandler();
		// ----------------------------

	}

	public void attachEventHandler()
	{
		try
		{
			XEventListener listener = null;

			XComponentContext xRemoteContext = aBean.getOOoConnection().getComponentContext();

			String st2[] = xRemoteContext.getServiceManager().getAvailableServiceNames();

			Object xGlobalBroadCaster = xRemoteContext.getServiceManager()
					.createInstanceWithContext("com.sun.star.frame.GlobalEventBroadcaster",
							xRemoteContext);

			xEventBroad = (XEventBroadcaster) UnoRuntime.queryInterface(XEventBroadcaster.class,
					xGlobalBroadCaster);

			// xEventBroad.addEventListener(this);
		}catch(Exception e)
		{
			String msg = "Unable to attach an event listener.";
			e.printStackTrace();
		}
	}

	public void openOfficeDoc()
	{
		documentOpened = true;
		openDoc("private:factory/scalc");

	}

	/**
	 * Create a blank document of type <code>desc</code>
	 * 
	 * @param url
	 *            The private internal URL of the OpenOffice.org document describing the document
	 * @param desc
	 *            A description of the document to be created
	 */

	public void openDoc(String url)
	{
		try
		{

			if(!aBean.isOOoConnected())
			{
				remove(aBean);
				aBean = new OOoBean();
				add(aBean, java.awt.BorderLayout.CENTER);
				repaint();
				attachEventHandler();

			}
			remove(aBean);
			aBean = new OOoBean();
			add(aBean, java.awt.BorderLayout.CENTER);
			repaint();
			attachEventHandler();

			PropertyValue[] args = new PropertyValue[3];
			args[0] = new PropertyValue();
			args[0].Name = "read-only";
			args[0].Value = new Boolean(false);
			args[1] = new PropertyValue();
			args[1].Name = "NoRestore";
			args[1].Value = new Boolean(Boolean.TRUE);

			args[2] = new PropertyValue();
			args[2].Name = "Password";
			args[2].Value = "123";

			// remove(aBean);
			// aBean = new OOoBean();
			// add(aBean, java.awt.BorderLayout.CENTER);
			aBean.loadFromURL(url, args);
			aBean.aquireSystemWindow();
			// repaint();
			documentOpened = true;
		}catch(Exception ex)
		{
			ex.printStackTrace();
			// AppContext.FirMessageError(ex.getMessage(), this.getClass(), ex);
		}
	}

	public void closeDoc()
	{
		if(documentOpened)
		{

			aBean.clear();
		}
	}

	/**
	 * Called from throughout the studio application to pass focus back to the OpenOffice bean so that it paints properly.
	 */
	public void activate()
	{
		try
		{
			aBean.getFrame().getComponentWindow().setFocus();
		}catch(NoConnectionException e)
		{

		}
	}

	/**
	 * Called whenever the studio application is closing so that OpenOffice doesn't need to attempt to recover the document when it starts again.
	 */
	public void closeApplication()
	{
		aBean.stopOOoConnection();
	}

	public XSpreadsheet getActivSheet()
	{
		try
		{
			XSpreadsheetView xSpreadsheetView = (XSpreadsheetView) UnoRuntime.queryInterface(
					XSpreadsheetView.class, aBean.getController());
			return xSpreadsheetView.getActiveSheet();
		}catch(Exception ex)
		{
			ex.printStackTrace();
			// AppContext.FirMessageError(ex.getMessage(), this.getClass(), ex);
		}
		return null;
	}

	/**
	 * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
	private void initComponents()
	{

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
		this.setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 400, Short.MAX_VALUE));
		layout.setVerticalGroup(layout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 300, Short.MAX_VALUE));
	}

	public XSpreadsheetDocument getOpendSheetDoc()
	{
		try
		{
			if(!aBean.isOOoConnected())
				attachEventHandler();

			XModel model = aBean.getDocument();
			XSpreadsheetDocument xCalcDocument = (XSpreadsheetDocument) UnoRuntime.queryInterface(
					XSpreadsheetDocument.class, model);
			return xCalcDocument;
		}catch(Exception ex)
		{
			ex.printStackTrace();
			// AppContext.FirMessageError(ex.getMessage(), this.getClass(), ex);
		}

		return null;
	}

	public XSpreadsheet NewSheetByName(String name, int index)
	{
		String[] sheetsnames = null;
		try
		{
			sheetsnames = getOpendSheetDoc().getSheets().getElementNames();
			System.out.println("doc sheets size:" + sheetsnames.length);

			getOpendSheetDoc().getSheets().insertNewByName(name, (short) index);
			Any an = (Any) getOpendSheetDoc().getSheets().getByName(name);
			if(index == 0)
			{
				renamesheetList(sheetsnames);
				sheetsnames = getOpendSheetDoc().getSheets().getElementNames();
				System.out.println("doc sheets size:" + sheetsnames.length);
			}
			return (XSpreadsheet) an.getObject();
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return null;
	}

	public XSpreadsheet NewDocByName(String name, int index)
	{
		String[] sheetsnames = null;
		if(index == 0)
		{
			renamesheetList(sheetsnames);
			sheetsnames = getOpendSheetDoc().getSheets().getElementNames();
		}

		try
		{
			getOpendSheetDoc().getSheets().insertNewByName(name, (short) index);
			Any an = (Any) getOpendSheetDoc().getSheets().getByName(name);
			return (XSpreadsheet) an.getObject();
		}catch(Exception ex)
		{
			ex.printStackTrace();

		}

		return null;

	}

	public void saveDoc(String filepath)
	{
		System.out.println("Save Sheet");
		try
		{
			PropertyValue[] args = new PropertyValue[2];
			args[0] = new PropertyValue();
			args[0].Name = "URL";
			args[0].Value = ("file:///" + filepath);
			args[1] = new PropertyValue();
			args[1].Name = "FilterName";
			args[1].Value = "MS Excel 97";
			this.aBean.getDocument().storeAsURL("file:///" + filepath, args);
		}catch(Exception ex)
		{
			Logger.getLogger(SheetPanel.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	private void renamesheetList(String[] sheetsnames)
	{
		try
		{
			for(String sheetname : sheetsnames)
			{
				getOpendSheetDoc().getSheets().removeByName(sheetname);
				System.out.println("Sheet:" + sheetname + " Deleted");
			}
		}catch(Exception ex)
		{
			ex.printStackTrace();
			inits();
		}
	}

	public String getSheetName(XSpreadsheet sheet)
	{
		XNamed name = (XNamed) UnoRuntime.queryInterface(XNamed.class, sheet);
		try
		{
			return name.getName();
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}

		return null;
	}

	public String getActiveSheetName()
	{
		return getSheetName(getActivSheet());
	}

	public int getSelectedCellX()
	{
		try
		{
			this.SVD.setViewdata(this.aBean.getController().getViewData() + "");
		}catch(NoConnectionException ex)
		{
			Logger.getLogger(SheetPanel.class.getName()).log(Level.SEVERE, null, ex);
		}
		row = SVD.getSelectedCellposesions().y;
		column = SVD.getSelectedCellposesions().x;
		System.out.println(SVD.getSelectedCellposesions().y + " and "
				+ SVD.getSelectedCellposesions().x);
		return this.SVD.getSelectedCellposesions().x;
	}

	public int getSelectedCellY()
	{
		try
		{
			this.SVD.setViewdata(this.aBean.getController().getViewData() + "");
		}catch(NoConnectionException ex)
		{
			Logger.getLogger(SheetPanel.class.getName()).log(Level.SEVERE, null, ex);
		}

		return this.SVD.getSelectedCellposesions().y;
	}

	public XCell getCellByPosission(int x, int y) throws Exception
	{
		// if (this.sheet == null)
		// throw new Exception("Error no sheet activated");
		// System.out.println(getActiveSheetName());
		return getCellByPosission(getActivSheet(), x, y);
	}

	public XCell getSelectedXCell()
	{
		try
		{
			this.SVD.setViewdata(this.aBean.getController().getViewData() + "");
			int x = SVD.getSelectedCellposesions().x;
			int y = SVD.getSelectedCellposesions().y;
			XCell xcell = getCellByPosission(x, y);
			return xcell;
		}catch(Exception ex)
		{
			Logger.getLogger(SheetPanel.class.getName()).log(Level.SEVERE, null, ex);
		}
		return null;
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
		}

		return cellText;
	}

	public XCell getCellByPosission(XSpreadsheet sheet, int x, int y) throws Exception
	{
		return sheet.getCellByPosition(x, y);
	}

}
