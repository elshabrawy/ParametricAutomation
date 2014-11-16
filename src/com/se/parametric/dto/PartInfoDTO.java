package com.se.parametric.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.se.automation.db.client.mapping.Document;
import com.se.automation.db.client.mapping.PartComponent;
import com.se.automation.db.client.mapping.Pdf;
import com.se.automation.db.client.mapping.Pl;
import com.se.automation.db.client.mapping.Supplier;
import com.se.automation.db.client.mapping.SupplierPl;
import com.se.automation.db.client.mapping.TrackingFeedbackType;

public class PartInfoDTO
{
	private Pdf pdf;
	private SupplierPl supplierPl;
	private Supplier supplier;
	private Pl pl;
	private String PN;
	private String family;
	private String familycross;
	private String generic;
	private String mask;
	private String NPIFlag;
	private String feedbackType;
	private String pdfUrl;
	private String status;
	private String comment;
	private String issuedBy;
	private String issuedTo;
	private String plName;
	private String supplierName;
	private String description;
	private Map<String, String> fetValues;
	private Document document;
	private String newsLink;
	private String feedBackStatus;
	private String feedBackCycleType;
	private String feedBackSource;
	private String Fbtype;
	private String CAction;
	private String PAction;
	private String RootCause;
	private String ActinDueDate;
	private String WrongFeatures;
	private PartComponent component;
	private ArrayList<Long> plFetIds;

	public PartComponent getComponent()
	{
		return component;
	}

	public void setComponent(PartComponent component)
	{
		this.component = component;
	}

	public String getWrongFeatures()
	{
		return WrongFeatures;
	}

	public void setWrongFeatures(String wrongFeatures)
	{
		WrongFeatures = wrongFeatures;
	}

	public String getCAction()
	{
		return CAction;
	}

	public void setCAction(String cAction)
	{
		CAction = cAction;
	}

	public String getPAction()
	{
		return PAction;
	}

	public void setPAction(String pAction)
	{
		PAction = pAction;
	}

	public String getRootCause()
	{
		return RootCause;
	}

	public void setRootCause(String rootCause)
	{
		RootCause = rootCause;
	}

	public String getActinDueDate()
	{
		return ActinDueDate;
	}

	public void setActinDueDate(String actinDueDate)
	{
		ActinDueDate = actinDueDate;
	}

	public String getFbtype()
	{
		return Fbtype;
	}

	public void setFbtype(String fbtype)
	{
		Fbtype = fbtype;
	}

	public String getFeedBackSource()
	{
		return feedBackSource;
	}

	public void setFeedBackSource(String feedBackSource)
	{
		this.feedBackSource = feedBackSource;
	}

	public String getFeedBackCycleType()
	{
		return feedBackCycleType;
	}

	public void setFeedBackCycleType(String feedBackCycleType)
	{
		this.feedBackCycleType = feedBackCycleType;
	}

	public String getFeedBackStatus()
	{
		return feedBackStatus;
	}

	public void setFeedBackStatus(String feedBackStatus)
	{
		this.feedBackStatus = feedBackStatus;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public String getNPIFlag()
	{
		return NPIFlag;
	}

	public void setNPIFlag(String nPIFlag)
	{
		NPIFlag = nPIFlag;
	}

	public String getFeedbackType()
	{
		return feedbackType;
	}

	public void setFeedbackType(String feedbackType)
	{
		this.feedbackType = feedbackType;
	}

	public String getPlName()
	{
		return plName;
	}

	public void setPlName(String plName)
	{
		this.plName = plName;
	}

	public String getSupplierName()
	{
		return supplierName;
	}

	public void setSupplierName(String supplierName)
	{
		this.supplierName = supplierName;
	}

	public String getIssuedBy()
	{
		return issuedBy;
	}

	public void setIssuedBy(String issuedBy)
	{
		this.issuedBy = issuedBy;
	}

	public String getIssuedTo()
	{
		return issuedTo;
	}

	public void setIssuedTo(String issuedTo)
	{
		this.issuedTo = issuedTo;
	}

	public String getPdfUrl()
	{
		return pdfUrl;
	}

	public void setPdfUrl(String pdfUrl)
	{
		this.pdfUrl = pdfUrl;
	}

	public void setComment(String comment)
	{
		this.comment = comment;
	}

	public String getComment()
	{
		return this.comment;
	}

	public void setStatus(String status)
	{
		this.status = status;
	}

	public String getStatus()
	{
		return this.status;
	}

	public Pdf getPdf()
	{
		return pdf;
	}

	public void setPdf(Pdf pdf)
	{
		this.pdf = pdf;
	}

	public SupplierPl getSupplierPl()
	{
		return supplierPl;
	}

	public void setSupplierPl(SupplierPl supplierPl)
	{
		this.supplierPl = supplierPl;
	}

	public Supplier getSupplier()
	{
		return supplier;
	}

	public void setSupplier(Supplier supplier)
	{
		this.supplier = supplier;
	}

	public Pl getPl()
	{
		return pl;
	}

	public void setPl(Pl pl)
	{
		this.pl = pl;
	}

	public Document getDocument()
	{
		return document;
	}

	public void setDocument(Document document)
	{
		this.document = document;
	}

	public String getPN()
	{
		return PN;
	}

	public void setPN(String pN)
	{
		PN = pN;
	}

	public String getFamily()
	{
		return family;
	}

	public void setFamily(String family)
	{
		this.family = family;
	}

	public String getFamilycross()
	{
		return familycross;
	}

	public void setFamilycross(String familycross)
	{
		this.familycross = familycross;
	}

	public String getGeneric()
	{
		return generic;
	}

	public void setGeneric(String generic)
	{
		this.generic = generic;
	}

	public String getMask()
	{
		return mask;
	}

	public void setMask(String mask)
	{
		this.mask = mask;
	}

	public Map<String, String> getFetValues()
	{
		return fetValues;
	}

	public void setFetValues(Map<String, String> fetValues)
	{
		this.fetValues = fetValues;
	}

	public String getNewsLink()
	{
		return newsLink;
	}

	public void setNewsLink(String newsLink)
	{
		this.newsLink = newsLink;
	}

	public ArrayList<Long> getPlFetIds()
	{
		return plFetIds;
	}

	public void setPlFetIds(ArrayList<Long> plFetIds)
	{
		this.plFetIds = plFetIds;
	}

}
