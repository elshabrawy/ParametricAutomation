package com.se.parametric.dto;

import java.util.List;

public class FeatureDTO {
	private String featureName;
	private String featurevalue="";
	private List<String> featureapprovedvalue;
	private String unit;
	private boolean doneFlag;
	private boolean core;
	private boolean code;
	
	public boolean isCore()
	{
		return core;
	}
	public void setCore(boolean core)
	{
		this.core = core;
	}
	public boolean isCode()
	{
		return code;
	}
	public void setCode(boolean code)
	{
		this.code = code;
	}
	public boolean isDoneFlag()
	{
		return doneFlag;
	}
	public void setDoneFlag(boolean doneFlag)
	{
		this.doneFlag = doneFlag;
	}
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
