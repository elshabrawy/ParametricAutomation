package com.se.parametric.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import com.se.automation.db.SessionUtil;
import com.se.automation.db.client.dto.RevisionPdfDTO;
import com.se.automation.db.client.mapping.Document;
import com.se.automation.db.client.mapping.NonPdf;
import com.se.automation.db.client.mapping.Pdf;
import com.se.automation.db.client.mapping.Pl;
import com.se.automation.db.client.mapping.PlFeature;
import com.se.automation.db.client.mapping.Supplier;
import com.se.automation.db.client.mapping.SupplierPlFamily;
import com.se.automation.db.client.mapping.SupplierUrl;
import com.se.automation.db.client.mapping.TrackingParametric;
import com.se.automation.db.client.mapping.Unit;
import com.se.parametric.AppContext;

public class ClientUtil
{
	public String getPdfTaxonomiesString(TrackingParametric tracking, Session session)
	{
		String tax = "";
		if(tracking.getPl() != null)
		{
			tax = tracking.getPl().getName();
		}
		else
		{
			return "EmptyPL";
		}
		return tax;

	}

	public String getPdfTaxonomiesString(Document document, Session session)
	{
		document = (Document) session.load(Document.class, document.getId());
		String tax = "";
		try
		{
			// session.persist(document);
			if(document.getSupplierPlFamilies() == null || document.getSupplierPlFamilies().size() == 0)
			{
				Exception ex = new Exception("Null PL Exception");
				AppContext.FirMessageError(ex.getMessage(), this.getClass(), ex);
				return "EmptyPL";
			}

			List<SupplierPlFamily> spf = new ArrayList<SupplierPlFamily>(document.getSupplierPlFamilies());
			StringBuilder sb = new StringBuilder();
			Set<String> plNames = new HashSet<String>();
			for(SupplierPlFamily supplierPlFamily : spf)
			{
				plNames.add(supplierPlFamily.getPl().getName());
			}
			for(String plName : plNames)
			{
				sb.append(plName + " - ");
			}
			tax = sb.toString().trim();
			if(tax.trim().length() > 2)
				tax = tax.substring(0, tax.length() - 2);

		}catch(Exception ex)
		{
			ex.printStackTrace();
			AppContext.FirMessageError(ex.getMessage(), this.getClass(), ex);
		}

		return tax;
	}

	public Unit getFeatureUnit(PlFeature plf)
	{
		Session ses = SessionUtil.getSession();
		try
		{
			ses.persist(plf);
			plf.getUnit().getName();
			return plf.getUnit();
		}finally
		{
			ses.close();
		}
	}

	public long getPdfNumberOfParts(Document document)
	{
		if(document.getComponents() == null)
			return 0;
		return document.getComponents().size();
	}

	public String getDataSheetFlagsbyDocument(Document document, Session session)
	{
		document = (Document) session.load(Document.class, document.getId());
		// session.persist(document);
		TrackingParametric trackingparametric = ((TrackingParametric) document.getTrackingParametrics().toArray()[0]);
		String trackingtasktype = trackingparametric.getTrackingTaskType().getName();
		return trackingtasktype;
	}

	public String getDeliveryDateByDocument(Document document, Session session)
	{
		document = (Document) session.load(Document.class, document.getId());
		String date = document.getPdf().getDownloadDate().toString();
		return date == null ? "" : date;
	}

	public String getIntroductionDateByDocument(Document document, Session session)
	{
		String date = null;
		try
		{
			document = (Document) session.load(Document.class, document.getId());
			date = document.getPdf().getCerDate().toString();
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return date == null ? "" : date;
	}

	public long getPdfTaxonomiIDBydocumentAndPlName(Document document, String plname, Session session)
	{
		for(Pl pl : getPdfTaxonomies(document, session))
		{
			if(pl.getName().equals(plname))
				return pl.getId();
		}
		return -1;
	}

	public List<Pl> getPdfTaxonomies(Document document)
	{
		List<Pl> plList = new ArrayList<Pl>();
		if(document.getSupplierPlFamilies() == null || document.getSupplierPlFamilies().size() == 0)
			return null;
		List<SupplierPlFamily> spf = new ArrayList<SupplierPlFamily>(document.getSupplierPlFamilies());
		for(SupplierPlFamily supplierPlFamily : spf)
		{
			Pl newPl = supplierPlFamily.getPl();
			// insert pl if not already in list
			boolean found = false;
			for(Pl pl : plList)
			{
				if(pl.getId().equals(newPl.getId()))
				{
					found = true;
					break;
				}
			}
			if(!found)
				plList.add(newPl);
		}
		return plList;
	}

	public List<Pl> getPdfTaxonomies(Document document, Session session)
	{
		try
		{
			if(!session.isOpen())
				session = SessionUtil.getCurrentSession();
			Criteria criteria = session.createCriteria(TrackingParametric.class);
			criteria.add(Restrictions.eq("document", document));
			criteria.setProjection(Projections.property("pl"));
			return criteria.list();
		}catch(Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
	}

	public String getSupplierLogoURLByDocument(Document document, Session session)
	{
		return getSupplierByDocument(document, session).getLogoUrl();
	}

	public List<String> getPdfTaxonomiesAsString(Document document)
	{
		List<String> plList = new ArrayList<String>();
		if(document.getSupplierPlFamilies() == null || document.getSupplierPlFamilies().size() == 0)
			return null;
		List<SupplierPlFamily> spf = new ArrayList<SupplierPlFamily>(document.getSupplierPlFamilies());
		for(SupplierPlFamily supplierPlFamily : spf)
		{
			String newPl = supplierPlFamily.getPl().getName();
			// insert pl if not already in list
			boolean found = false;
			for(String pl : plList)
			{
				if(pl.equals(newPl))
				{
					found = true;
					break;
				}
			}
			if(!found)
				plList.add(newPl);
		}
		return plList;
	}

	public String getSeUrlByDocument(Document document, Session session)
	{
		document = (Document) session.load(Document.class, document.getId());
		Pdf pdf = document.getPdf();
		NonPdf nonPdf = document.getNonPdf();
		String seUrl = "";
		if(pdf != null)
		{
			session.persist(pdf);
			seUrl = pdf.getSeUrl();
		}
		else
			seUrl = nonPdf.getSeUrl();
		return seUrl;
	}

	public String getSeUrlByDocument(Document document)
	{
		Pdf pdf = document.getPdf();
		NonPdf nonPdf = document.getNonPdf();
		String seUrl = "";
		if(pdf != null)
			seUrl = pdf.getSeUrl();
		else
			seUrl = nonPdf.getSeUrl();
		return seUrl;
	}

	public String getSupplierNameByDocument(Document document, Session session)
	{
		document = (Document) session.load(Document.class, document.getId());
		String supplierName = "";
		Pdf pdf = document.getPdf();
		NonPdf nonPdf = document.getNonPdf();

		if(pdf != null)
		{
			session.persist(pdf);
			supplierName = pdf.getSupplierUrl().getSupplier().getName();
		}
		else
		{
			session.persist(nonPdf);
			supplierName = nonPdf.getSupplierUrl().getSupplier().getName();
		}
		return supplierName;
	}

	public String getSupplierCodeByDocument(Document document, Session session)
	{
		String suppcod = "";
		document = (Document) session.load(Document.class, document.getId());
		Pdf pdf = document.getPdf();
		NonPdf nonPdf = document.getNonPdf();

		if(pdf != null)
		{
			session.persist(pdf);
			suppcod = pdf.getSupplierUrl().getSupplier().getCode();
		}
		else
		{
			session.persist(nonPdf);
			suppcod = nonPdf.getSupplierUrl().getSupplier().getCode();
		}

		return suppcod;
	}

	public Supplier getSupplierByDocument(Document document)
	{
		Pdf pdf = document.getPdf();
		NonPdf nonPdf = document.getNonPdf();
		Supplier supplier = null;
		if(pdf != null)
			supplier = pdf.getSupplierUrl().getSupplier();
		else
			supplier = nonPdf.getSupplierUrl().getSupplier();
		return supplier;
	}

	public SupplierUrl getSupplierUrlByDocument(Document document)
	{
		Pdf pdf = document.getPdf();
		NonPdf nonPdf = document.getNonPdf();
		SupplierUrl supplierUrl = null;
		if(pdf != null)
			supplierUrl = pdf.getSupplierUrl();
		else
			supplierUrl = nonPdf.getSupplierUrl();
		return supplierUrl;
	}

	public String getPdfTitleUrlByDocument(Document document, Session session)
	{
		document = (Document) session.load(Document.class, document.getId());

		return getPdfTitleUrlByDocument(document);
	}

	public String getPdfTitleUrlByDocument(Document document)
	{
		Pdf pdf = document.getPdf();
		String title = "";
		if(pdf != null)
			title = pdf.getTitle();
		return title;
	}

	public Date getDownloadDateByDocument(Document document)
	{
		Pdf pdf = document.getPdf();
		NonPdf nonPdf = document.getNonPdf();
		Date downloadDate = null;
		if(pdf != null)
			downloadDate = pdf.getDownloadDate();
		else
			downloadDate = nonPdf.getDownloadDate();
		return downloadDate;
	}

	public Date getLastCheckDateByDocument(Document document)
	{
		Pdf pdf = document.getPdf();
		NonPdf nonPdf = document.getNonPdf();
		Date lastCheckDate = null;
		if(pdf != null)
			lastCheckDate = pdf.getLastCheckDate();
		else
			lastCheckDate = nonPdf.getLastCheckDate();
		return lastCheckDate;
	}

	HashSet<Document> revisionsSet = new HashSet<Document>();

	private Document getNextDocument(Document doc)
	{
		Document next = doc.getDocument();
		return next;
	}

	public static Hashtable<Long, Document> getRevisions(Document document, Session session)
	{
		Hashtable<Long, Document> revisionsMap = new Hashtable<Long, Document>();
		if(!session.isOpen())
			session = SessionUtil.getCurrentSession();
		document = (Document) session.load(Document.class, document.getId());
		try
		{
			Document nextDocument = document.getDocument();
			while(nextDocument != null)
			{
				try
				{
					if(nextDocument.getId() != null && nextDocument.getId() > 0 && !revisionsMap.containsKey(nextDocument.getId()))
						revisionsMap.put(nextDocument.getId(), nextDocument);
					else
						break;
					nextDocument = nextDocument.getDocument();
				}catch(Exception ex)
				{
					ex.printStackTrace();
					AppContext.ShowMessage(ex.getMessage(), 0);
				}
			}
		}catch(Exception ex)
		{
			ex.printStackTrace();
			AppContext.ShowMessage(ex.getMessage(), 0);
		}finally
		{
			// session.close();
		}
		return revisionsMap;
	}

	public List<String> getPdfTaxonomiesAsString(Document document, Session session)
	{
		List<String> plList = new ArrayList<String>();
		try
		{
			Criteria criteria = session.createCriteria(TrackingParametric.class);
			criteria.add(Restrictions.eq("document", document));
			criteria.setProjection(Projections.distinct(Projections.property("pl")));
			List<Pl> list = criteria.list();
			if(list != null && list.size() > 0)
			{
				for(Pl pl : list)
				{
					plList.add(pl.getName());
				}
			}
		}catch(Exception ex)
		{

			ex.printStackTrace();
			return null;
		}
		return plList;
	}

	public Supplier getSupplierByDocument(Document document, Session session)
	{
		document = (Document) session.load(Document.class, document.getId());
		Pdf pdf = document.getPdf();
		NonPdf nonPdf = document.getNonPdf();
		Supplier supplier = null;
		if(pdf != null)
			supplier = pdf.getSupplierUrl().getSupplier();
		else
			supplier = nonPdf.getSupplierUrl().getSupplier();
		return supplier;
	}
}
