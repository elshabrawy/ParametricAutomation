package com.se.parametric.dto;

import java.util.List;

public class FeatureDTO {
	private String featureName;
	private String featurevalue="";
	private List<String> featureapprovedvalue;
	private String unit;
	public String getFeatureName() {
		return featureName;
	}
	public void setFeatureName(String featureName) {
		this.featureName = featureName;
	}
	public String getFeaturevalue() {
		return featurevalue;
	}
	public void setFeaturevalue(String featurevalue) {
		this.featurevalue = featurevalue;
	}
	public List<String> getFeatureapprovedvalue() {
		return featureapprovedvalue;
	}
	public void setFeatureapprovedvalue(List<String> featureapprovedvalue) {
		this.featureapprovedvalue = featureapprovedvalue;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}


}
