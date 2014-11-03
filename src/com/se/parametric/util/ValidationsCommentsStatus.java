package com.se.parametric.util;

public class ValidationsCommentsStatus
{
	protected String Comment = "-";
	protected String Status = "-";
	protected String Taxonomy = "-";

	public String getStatus()
	{
		return Status;
	}

	public String getTaxonomy()
	{
		return Taxonomy;
	}

	public String getComment()
	{
		return Comment;
	}

	public void setComment(String comment)
	{
		Comment = comment;
	}

	public void setStatus(String status)
	{
		Status = status;
	}

	public void setTaxonomy(String taxonomy)
	{
		Taxonomy = taxonomy;
	}

	protected void refresh()
	{
		Comment = "-";
		Status = "-";
		Taxonomy = "-";

	}
}
