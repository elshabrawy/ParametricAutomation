package com.se.parametric.dto;

public class SummaryDTO implements Comparable<SummaryDTO>
{

	private String pdfUrl;
	private String onlineLink;
	private String PlType;
	private String plName;
	private int PDFParts;
	private int PDFDoneParts;
	private int PLParts;
	private int PLDoneParts;
	private long comid;
	private String part;
	private String supplier;
	private String taskType;
	private String devUserName;
	private String date;
	private String QAflag;
	private String QAcomment;
	private String doneflag;
	private String pnInPdf;
	private String keywords;

	public String getPdfUrl()
	{
		return pdfUrl;
	}

	public void setPdfUrl(String pdfUrl)
	{
		this.pdfUrl = pdfUrl;
	}

	public String getOnlineLink()
	{
		return onlineLink;
	}

	public void setOnlineLink(String onlineLink)
	{
		this.onlineLink = onlineLink;
	}

	public String getPlType()
	{
		return PlType;
	}

	public void setPlType(String plType)
	{
		PlType = plType;
	}

	public String getPlName()
	{
		return plName;
	}

	public void setPlName(String plName)
	{
		this.plName = plName;
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

	public long getComid()
	{
		return comid;
	}

	public void setComid(long comid)
	{
		this.comid = comid;
	}

	public String getPart()
	{
		return part;
	}

	public void setPart(String part)
	{
		this.part = part;
	}

	public String getSupplier()
	{
		return supplier;
	}

	public void setSupplier(String supplier)
	{
		this.supplier = supplier;
	}

	public String getTaskType()
	{
		return taskType;
	}

	public void setTaskType(String taskType)
	{
		this.taskType = taskType;
	}

	public String getDevUserName()
	{
		return devUserName;
	}

	public void setDevUserName(String devUserName)
	{
		this.devUserName = devUserName;
	}

	public String getDate()
	{
		return date;
	}

	public void setDate(String date)
	{
		this.date = date;
	}

	public String getQAflag()
	{
		return QAflag;
	}

	public void setQAflag(String qAflag)
	{
		QAflag = qAflag;
	}

	public String getQAcomment()
	{
		return QAcomment;
	}

	public void setQAcomment(String qAcomment)
	{
		QAcomment = qAcomment;
	}

	public String getDoneflag()
	{
		return doneflag;
	}

	public void setDoneflag(String doneflag)
	{
		this.doneflag = doneflag;
	}

	public String getPnInPdf()
	{
		return pnInPdf;
	}

	public void setPnInPdf(String pnInPdf)
	{
		this.pnInPdf = pnInPdf;
	}

	public String getKeywords()
	{
		return keywords;
	}

	public void setKeywords(String keywords)
	{
		this.keywords = keywords;
	}

	@Override
	public int compareTo(SummaryDTO obj2)
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
