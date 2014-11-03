package com.se.parametric.dto;

public class FeedBackData
{
	String fbStatus, comment, fbType, qaComment, qaStatus, lastEngComment, issuetype;
	String CAction, PAction, RootCause, ActionDueDate,RecievedDate;

	public String getRecievedDate()
	{
		return RecievedDate;
	}

	public void setRecievedDate(String recievedDate)
	{
		RecievedDate = recievedDate;
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

	public String getActionDueDate()
	{
		return ActionDueDate;
	}

	public void setActionDueDate(String actionDueDate)
	{
		ActionDueDate = actionDueDate;
	}

	public String getIssuetype()
	{
		return issuetype;
	}

	public void setIssuetype(String issuetype)
	{
		this.issuetype = issuetype;
	}

	public String getLastEngComment()
	{
		return lastEngComment;
	}

	public void setLastEngComment(String lastEngComment)
	{
		this.lastEngComment = lastEngComment;
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

	public String getFbType()
	{
		return fbType;
	}

	public void setFbType(String fbType)
	{
		this.fbType = fbType;
	}

	public String getQaComment()
	{
		return qaComment;
	}

	public void setQaComment(String qaComment)
	{
		this.qaComment = qaComment;
	}

	public String getQaStatus()
	{
		return qaStatus;
	}

	public void setQaStatus(String qaStatus)
	{
		this.qaStatus = qaStatus;
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

	public Long getQaUserId()
	{
		return qaUserId;
	}

	public void setQaUserId(Long qaUserId)
	{
		this.qaUserId = qaUserId;
	}

	Long issuedby, issueTo, qaUserId;

}
