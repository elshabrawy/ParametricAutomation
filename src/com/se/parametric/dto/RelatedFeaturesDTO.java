package com.se.parametric.dto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.se.automation.db.client.mapping.QaChecksInDependentFeature;
import com.se.parametric.util.PDDRow;

public class RelatedFeaturesDTO
{
	private FeatureDTO independent;
	private Map dependent = new HashMap();
	private PDDRow sheetrow;

	public PDDRow getSheetrow()
	{
		return sheetrow;
	}

	public void setSheetrow(PDDRow sheetrow)
	{
		this.sheetrow = sheetrow;
	}

	private QaChecksInDependentFeature qachecksrelatedfet;

	public FeatureDTO getIndependent()
	{
		return independent;
	}

	public void setIndependent(FeatureDTO independent)
	{
		this.independent = independent;
	}

	public void addDependentFeature(String featurename, QaChecksInDependentFeature depen)
	{
		dependent.put(featurename, depen);
	}

	public FeatureDTO getDependentRowFeature(String featurename)
	{
		return sheetrow.getFeature(featurename);
	}

	public Map getDependentFeatures()
	{
		return dependent;
	}

	public QaChecksInDependentFeature getDependentFeatur(String featurename)
	{
		return (QaChecksInDependentFeature) dependent.get(featurename);
	}

	public QaChecksInDependentFeature getQachecksrelatedfet()
	{
		return qachecksrelatedfet;
	}

	public void setQachecksrelatedfet(QaChecksInDependentFeature qachecksrelatedfet)
	{
		this.qachecksrelatedfet = qachecksrelatedfet;
	}

}
