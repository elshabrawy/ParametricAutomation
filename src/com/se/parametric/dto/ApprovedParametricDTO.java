package com.se.parametric.dto;

import java.io.Serializable;

import com.se.automation.db.client.mapping.ApprovedParametricValue;

public class ApprovedParametricDTO implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 7019504634962122543L;
	private ApprovedParametricValue approvedParametricValue;
	private String fullValue;
	private String featUnit;

	public String getFeatUnit() {
		return featUnit;
	}

	public void setFeatUnit(String featUnit) {
		this.featUnit = featUnit;
	}

	private String fromValue;
	private String toValue;
	private String fromSign;
	private String toSign;
	private String fromCondition;
	private String toCondition;
	private String fromMultiplier;
	private String toMultiplier;
	private String fromValueType;
	private String toValueType;
	private String fromUnit;
	private String toUnit;
	private Long groupId;
	private String pattern;

	public String getPattern() {
		return pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public ApprovedParametricValue getApprovedParametricValue() {
		return approvedParametricValue;
	}

	public void setApprovedParametricValue(ApprovedParametricValue approvedParametricValue) {
		this.approvedParametricValue = approvedParametricValue;
	}

	public String getFullValue() {
		return fullValue;
	}

	public void setFullValue(String fullValue) {
		this.fullValue = fullValue;
	}

	public String getFromValue() {
		return fromValue;
	}

	public void setFromValue(String fromValue) {
		this.fromValue = fromValue;
	}

	public String getToValue() {
		return toValue;
	}

	public void setToValue(String toValue) {
		this.toValue = toValue;
	}

	public String getFromSign() {
		return fromSign;
	}

	public void setFromSign(String fromSign) {
		this.fromSign = fromSign;
	}

	public String getToSign() {
		return toSign;
	}

	public void setToSign(String toSign) {
		this.toSign = toSign;
	}

	public String getFromCondition() {
		return fromCondition;
	}

	public void setFromCondition(String fromCondition) {
		this.fromCondition = fromCondition;
	}

	public String getToCondition() {
		return toCondition;
	}

	public void setToCondition(String toCondition) {
		this.toCondition = toCondition;
	}

	public String getFromMultiplier() {
		return fromMultiplier;
	}

	public void setFromMultiplier(String fromMultiplier) {
		this.fromMultiplier = fromMultiplier;
	}

	public String getToMultiplier() {
		return toMultiplier;
	}

	public void setToMultiplier(String toMultiplier) {
		this.toMultiplier = toMultiplier;
	}

	public String getFromValueType() {
		return fromValueType;
	}

	public void setFromValueType(String fromValueType) {
		this.fromValueType = fromValueType;
	}

	public String getToValueType() {
		return toValueType;
	}

	public void setToValueType(String toValueType) {
		this.toValueType = toValueType;
	}

	public String getFromUnit() {
		return fromUnit;
	}

	public void setFromUnit(String fromUnit) {
		this.fromUnit = fromUnit;
	}

	public String getToUnit() {
		return toUnit;
	}

	public void setToUnit(String toUnit) {
		this.toUnit = toUnit;
	}

	public String toString() {
		String result = "";
		if (fromValueType == null)
			fromValueType = "";
		if (toValueType == null)
			toValueType = "";

		if (fromCondition == null)
			fromCondition = "";
		if (toCondition == null)
			toCondition = "";

		if (fromUnit == null)
			fromUnit = "";
		if (toUnit == null)
			toUnit = "";
		//||fromSign.equals(" to ")
		if (fromSign == null)
			fromSign = "";
		if (toSign == null)
			toSign = "";

		if (fromMultiplier == null)
			fromMultiplier = "";
		if (toMultiplier == null)
			toMultiplier = "";
		result = fromSign
				+ ""
				+ fromValue
				+ ""
				+ fromValueType
				+ ""
				+ fromCondition
				+ ""
				+ fromMultiplier
				+ ""
				+ fromUnit
				+ (toValue != null && !toValue.trim().equals("") ? (" to " + toSign + "" + toValue + "" + toValueType + "" + toCondition
						+ "" + toMultiplier + "" + toUnit) : "");
//		if (this.getFeatUnit() != null) {
//			return result.replace(this.getFeatUnit(), "");
//		} else {
//			return result;
//		}
		return result;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public Long getGroupId() {
		return groupId;
	}

}
