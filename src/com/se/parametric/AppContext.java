package com.se.parametric;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.se.automation.db.client.mapping.TblPdfStatic;
import com.se.automation.db.client.mapping.TblPdfCompare;
import com.se.automation.db.client.mapping.TrackingParametric;

import com.se.automation.db.client.mapping.Document;

import com.se.grm.client.mapping.GrmUser;
import com.se.grm.db.SessionUtil;

public class AppContext
{

	public List pdfs = null;

	public static List<TrackingParametric> AllnewDocuments;
	public static List<TrackingParametric> newdatasheets;
	public static List<TrackingParametric> updateDataSheets;
	public static List<TrackingParametric> updateAllDataSheet;
	private static Session session;
	public static String txt_userName = "hatemh_eng@siliconexpert.com";
	public static String txt_UserPass = "123456";
	public static boolean SaveConfig;
	public static boolean noParametricUpdate = false;
	public static int pagenum = 0;
	public static int updatePageNum = 1;
	public static int noOfUpdatePages = 1;
	public static int noOfUpdateSheets = 1;
	public static int allUpdateDocsSize = 0;
	public static int nextAndPrevUpdateDocsSize = 0;
	private static FileHandler fh;
	private static Logger theLogger;
	public static String[] userTaxonomieslist;
	public static String[] uservendorslist;

	static
	{

		// config = new ConfigLoginsData();
		try
		{
			fh = new FileHandler("Developmentlog.xml");
			theLogger = Logger.getLogger("");
			theLogger.addHandler(fh);
		}catch(SecurityException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch(IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void FirMessageError(String messageError, Class classs, Throwable throwable)
	{
		try
		{
			// ShowMessage(messageError,SWT.ERROR);

			theLogger.log(Level.SEVERE, throwable.getMessage(), throwable);
			fh.flush();
		}catch(Exception ex)
		{

		}
	}

	public static int ShowMessage(String messageText, int style)
	{
		switch(style){
		// case SWT.ERROR:
		// if(messageboxErr != null){
		// messageboxErr.setMessage(messageText);
		// return messageboxErr.open();
		// }
		// break;
		// case 0:
		// if(messageboxInfo!= null){
		// messageboxInfo.setMessage(messageText);
		// return messageboxInfo.open();
		// }
		// case 288:
		// if(messageboxOKCancel != null){
		// messageboxOKCancel.setMessage(messageText);
		// return messageboxOKCancel.open();
		// }
		default:
			return -1;
		}
		// return -1;
	}

}
