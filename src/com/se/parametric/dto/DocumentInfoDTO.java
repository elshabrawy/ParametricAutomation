package com.se.parametric.dto;

import com.se.automation.db.client.mapping.Document;
import com.se.automation.db.client.mapping.Pdf;
import com.se.automation.db.client.mapping.Pl;
import com.se.automation.db.client.mapping.Supplier;
import com.se.automation.db.client.mapping.SupplierPl;

public class DocumentInfoDTO {
	private Pdf pdf;
	private SupplierPl supplierPl;
	private Supplier supplier;
	private Pl pl;
	private Document document;
	private String pdfUrl;
	private boolean Extracted;
	private String taskType;
	private String status;
	private String userName;
	private String plName;
	private String supplierName;
	private String title;
	

	public String getPdfUrl() {
		return pdfUrl;
	}
	public void setPdfUrl(String pdfUrl) {
		this.pdfUrl = pdfUrl;
	}
	public String getPlName() {
		return plName;
	}
	public void setPlName(String plName) {
		this.plName = plName;
	}
	public String getSupplierName() {
		return supplierName;
	}
	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Pdf getPdf() {
		return pdf;
	}
	public void setPdf(Pdf pdf) {
		this.pdf = pdf;
	}
	
	public Pl getPl() {
		return pl;
	}
	public void setPl(Pl pl) {
		this.pl = pl;
	}
	public Supplier getSupplier() {
		return supplier;
	}
	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}
	
	public Document getDocument() {
		return document;
	}
	public void setDocument(Document document) {
		this.document = document;
	}
	public SupplierPl getSupplierPl() {
		return supplierPl;
	}
	public void setSupplierPl(SupplierPl supplierPl) {
		this.supplierPl = supplierPl;
	}
	public boolean isExtracted() {
		return Extracted;
	}
	public void setExtracted(boolean extracted) {
		Extracted = extracted;
	}
	public String getTaskType() {
		return taskType;
	}
	public void setTaskType(String status) {
		taskType = status;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}

}
