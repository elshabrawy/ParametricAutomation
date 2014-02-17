package com.se.parametric.dto;

public class UnApprovedDTO
{
	String plName, partNumber, pdfUrl, featureName, featureValue, featureUnit, sign, value, type, condition, multiplier, unit;
	String fbStatus, comment, gruopSatus, fbType, qaComment, qaStatus, IssueType, lastEngComment;
	String CAction, PAction, RootCause, ActionDueDate;

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

	public String getActionDueDate()
	{
		return ActionDueDate;
	}

	public void setActionDueDate(String actionDueDate)
	{
		ActionDueDate = actionDueDate;
	}

	public String getLastEngComment()
	{
		return lastEngComment;
	}

	public void setLastEngComment(String lastEngComment)
	{
		this.lastEngComment = lastEngComment;
	}

	Long userId, issuedby, issueTo, qaUserId;

	public String getQaStatus()
	{
		return qaStatus;
	}

	public void setQaStatus(String qaStatus)
	{
		this.qaStatus = qaStatus;
	}

	public String getQaComment()
	{
		return qaComment;
	}

	public void setQaComment(String qacomment)
	{
		this.qaComment = qacomment;
	}

	public Long getQaUserId()
	{
		return qaUserId;
	}

	public void setQaUserId(Long qaUserId)
	{
		this.qaUserId = qaUserId;
	}

	public String getFbType()
	{
		return fbType;
	}

	public void setFbType(String fbType)
	{
		this.fbType = fbType;
	}

	public String getPlName()
	{
		return plName;
	}

	public void setPlName(String plName)
	{
		this.plName = plName;
	}

	public String getPartNumber()
	{
		return partNumber;
	}

	public void setPartNumber(String partNumber)
	{
		this.partNumber = partNumber;
	}

	public String getPdfUrl()
	{
		return pdfUrl;
	}

	public void setPdfUrl(String pdfUrl)
	{
		this.pdfUrl = pdfUrl;
	}

	public String getFeatureName()
	{
		return featureName;
	}

	public void setFeatureName(String featureName)
	{
		this.featureName = featureName;
	}

	public String getFeatureValue()
	{
		return featureValue;
	}

	public void setFeatureValue(String featureValue)
	{
		this.featureValue = featureValue;
	}

	public String getFeatureUnit()
	{
		return featureUnit;
	}

	public void setFeatureUnit(String featureUnit)
	{
		this.featureUnit = featureUnit;
	}

	public String getSign()
	{
		return sign;
	}

	public void setSign(String sign)
	{
		this.sign = sign;
	}

	public String getValue()
	{
		return value;
	}

	public void setValue(String value)
	{
		this.value = value;
	}

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	public String getCondition()
	{
		return condition;
	}

	public void setCondition(String condition)
	{
		this.condition = condition;
	}

	public String getMultiplier()
	{
		return multiplier;
	}

	public void setMultiplier(String multiplier)
	{
		this.multiplier = multiplier;
	}

	public String getUnit()
	{
		return unit;
	}

	public void setUnit(String unit)
	{
		this.unit = unit;
	}

	public String getFbStatus()
	{
		return fbStatus;
	}

	public void setFbStatus(String fbStatus)
	{
		this.fbStatus = fbStatus;
	}

	public String getComment()
	{
		return comment;
	}

	public void setComment(String comment)
	{
		this.comment = comment;
	}

	public String getGruopSatus()
	{
		return gruopSatus;
	}

	public void setGruopSatus(String gruopSatus)
	{
		this.gruopSatus = gruopSatus;
	}

	public Long getUserId()
	{
		return userId;
	}

	public void setUserId(Long userId)
	{
		this.userId = userId;
	}

	public Long getIssuedby()
	{
		return issuedby;
	}

	public void setIssuedby(Long issuedby)
	{
		this.issuedby = issuedby;
	}

	public Long getIssueTo()
	{
		return issueTo;
	}

	public void setIssueTo(Long issueTo)
	{
		this.issueTo = issueTo;
	}

	public void setIssueType(String IssueType)
	{
		this.IssueType = IssueType;
	}

	public String getIssueType()
	{
		return IssueType;
	}

}
