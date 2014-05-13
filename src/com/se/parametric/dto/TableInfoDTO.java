package com.se.parametric.dto;

import java.util.Comparator;

public class TableInfoDTO implements Comparable<TableInfoDTO>
{

	private String pdfUrl;
	private String supplierName;
	private String plName;
	private String docId;
	private String extracted;
	private String taskType;
	private String status;
	private String devUserName;
	private String qaUserName;
	private String date;
	private String comment;
	private int infectedParts;
	private int infectedTaxonomies;
	private String supplierSiteUrl;
	private String onlineLink;
	private long pagesNo;
	private String priority;
	private String title;
	private String taxonomies;
	private String newsLink;
	private String downloadDate;
	private String cerDate;
	private String taxPath;
	private int pagesCount;
	private long pdfId;
	private int PDFParts;
	private int PDFDoneParts;
	private int PLParts;
	private int PLDoneParts;
	private int PLFeatures;
	private String PlType;

	public String getPlType()
	{
		return PlType;
	}

	public void setPlType(String plType)
	{
		PlType = plType;
	}

	public int getPLFeatures()
	{
		return PLFeatures;
	}

	public void setPLFeatures(int pLFeatures)
	{
		PLFeatures = pLFeatures;
	}

	public int getPDFParts()
	{
		return PDFParts;
	}

	public void setPDFParts(int pDFParts)
	{
		PDFParts = pDFParts;
	}

	public int getPDFDoneParts()
	{
		return PDFDoneParts;
	}

	public void setPDFDoneParts(int pDFDoneParts)
	{
		PDFDoneParts = pDFDoneParts;
	}

	public int getPLParts()
	{
		return PLParts;
	}

	public void setPLParts(int pLParts)
	{
		PLParts = pLParts;
	}

	public int getPLDoneParts()
	{
		return PLDoneParts;
	}

	public void setPLDoneParts(int pLDoneParts)
	{
		PLDoneParts = pLDoneParts;
	}

	public long getPdfId()
	{
		return pdfId;
	}

	public void setPdfId(long pdfId)
	{
		this.pdfId = pdfId;
	}

	public int getPagesCount()
	{
		return pagesCount;
	}

	public void setPagesCount(int pagesCount)
	{
		this.pagesCount = pagesCount;
	}

	public String getTaxPath()
	{
		return taxPath;
	}

	public void setTaxPath(String taxPath)
	{
		this.taxPath = taxPath;
	}

	public String getNewsLink()
	{
		return newsLink;
	}

	public void setNewsLink(String newsLink)
	{
		this.newsLink = newsLink;
	}

	public String getDownloadDate()
	{
		return downloadDate;
	}

	public void setDownloadDate(String downloadDate)
	{
		this.downloadDate = downloadDate;
	}

	public String getCerDate()
	{
		return cerDate;
	}

	public void setCerDate(String cerDate)
	{
		this.cerDate = cerDate;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public String getTaxonomies()
	{
		return taxonomies;
	}

	public void setTaxonomies(String taxonomies)
	{
		this.taxonomies = taxonomies;
	}

	public String getSupplierSiteUrl()
	{
		return supplierSiteUrl;
	}

	public void setSupplierSiteUrl(String supplierSiteUrl)
	{
		this.supplierSiteUrl = supplierSiteUrl;
	}

	public String getOnlineLink()
	{
		return onlineLink;
	}

	public void setOnlineLink(String onlineLink)
	{
		this.onlineLink = onlineLink;
	}

	public long getPagesNo()
	{
		return pagesNo;
	}

	public void setPagesNo(long pagesNo)
	{
		this.pagesNo = pagesNo;
	}

	public int getInfectedParts()
	{
		return infectedParts;
	}

	public void setInfectedParts(int infectedParts)
	{
		this.infectedParts = infectedParts;
	}

	public int getInfectedTaxonomies()
	{
		return infectedTaxonomies;
	}

	public void setInfectedTaxonomies(int infectedTaxonomies)
	{
		this.infectedTaxonomies = infectedTaxonomies;
	}

	public String getComment()
	{
		return comment;
	}

	public void setComment(String comment)
	{
		this.comment = comment;
	}

	public String getDate()
	{
		return date;
	}

	public void setDate(String date)
	{
		this.date = date;
	}

	public String getPdfUrl()
	{
		return pdfUrl;
	}

	public void setPdfUrl(String pdfUrl)
	{
		this.pdfUrl = pdfUrl;
	}

	public String getSupplierName()
	{
		return supplierName;
	}

	public void setSupplierName(String supplierName)
	{
		this.supplierName = supplierName;
	}

	public String getPlName()
	{
		return plName;
	}

	public void setPlName(String plName)
	{
		this.plName = plName;
	}

	public String getDocId()
	{
		return docId;
	}

	public void setDocId(String docId)
	{
		this.docId = docId;
	}

	public String getExtracted()
	{
		return extracted;
	}

	public void setExtracted(String extracted)
	{
		this.extracted = extracted;
	}

	public String getTaskType()
	{
		return taskType;
	}

	public void setTaskType(String taskType)
	{
		this.taskType = taskType;
	}

	public String getStatus()
	{
		return status;
	}

	public void setStatus(String status)
	{
		this.status = status;
	}

	public String getDevUserName()
	{
		return devUserName;
	}

	public void setDevUserName(String devUserName)
	{
		this.devUserName = devUserName;
	}

	public String getQaUserName()
	{
		return qaUserName;
	}

	public void setQaUserName(String qaUserName)
	{
		this.qaUserName = qaUserName;
	}

	public String getPriority()
	{
		return priority;
	}

	public void setPriority(String priority)
	{
		this.priority = priority;
	}

	@Override
	public int compareTo(TableInfoDTO obj2)
	{
		int result = 0;
		if(this.getPDFParts() > obj2.getPDFParts())
		{
			result = -1;
		}
		else if(this.getPDFParts() < obj2.getPDFParts())
		{
			result = 1;
		}
		else if(this.getPDFParts() == obj2.getPDFParts())
		{
			if(this.getPDFDoneParts() > obj2.getPDFDoneParts())
			{
				result = -1;
			}
			else if(this.getPDFDoneParts() < obj2.getPDFDoneParts())
			{
				result = 1;
			}
			else if(this.getPDFDoneParts() == obj2.getPDFDoneParts())
			{
				result = 0;
			}
		}
		return result;
	}

}
