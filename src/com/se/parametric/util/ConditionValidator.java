package com.se.parametric.util;

import java.util.Iterator;
import java.util.Map;

import com.se.automation.db.client.mapping.QaChecksInDependentFeature;
import com.se.parametric.dto.FeatureDTO;
import com.se.parametric.dto.RelatedFeaturesDTO;

public class ConditionValidator extends ValidationsCommentsStatus implements Validator
{
	// MacroRunner macro = new MacroRunner();

	@Override
	public boolean Validat(RelatedFeaturesDTO datarow)
	{
		// TODO Auto-generated method stub
		boolean validate = true;

		if(Validate(datarow, datarow.getIndependent(), datarow.getQachecksrelatedfet()))
		{
			if(datarow.getDependentFeatures().size() > 0)
			{
				validate = validateSempleDependentFeatures(datarow, datarow.getSheetrow().getFeatures(), datarow.getDependentFeatures());

			}
		}

		return validate;
	}

	private boolean Validate(RelatedFeaturesDTO datarow, FeatureDTO fet, QaChecksInDependentFeature qachecksindependent)
	{
		boolean validate = true;
		int operationtype = qachecksindependent.getQaChecksValidatetype().getId().intValue();
		switch(operationtype){
		case 4:
			validate = ValidateCondition(fet, qachecksindependent);
			break;
		case 3:
			validate = validateContans(fet, qachecksindependent);
			break;
		case 5:
			validate = formulaValidation(datarow, fet, qachecksindependent);
		default:
		}

		return validate;
	}

	private boolean formulaValidation(RelatedFeaturesDTO datarow, FeatureDTO fet, QaChecksInDependentFeature qa)
	{

		boolean valid = true;
		try
		{

			String formula = qa.getFormula();
			int startindx = 0;
			int endindx = 0;
			String featurename = "";
			String operator = "";
			String featurevalue;
			while(formula.contains("${"))
			{
				startindx = formula.indexOf("${");
				endindx = formula.indexOf("}");
				featurename = formula.substring(startindx + 2, endindx);
				FeatureDTO fetx = datarow.getDependentRowFeature(featurename);
				if(fetx == null)
				{
					Comment = "Not Found Feature with name :" + featurename;
					throw new Exception(Comment);
				}
				featurevalue = fetx.getFeaturevalue();

				if(featurevalue == null || featurevalue.isEmpty() || featurevalue.equalsIgnoreCase("N/A") || featurevalue.equalsIgnoreCase("N/R"))
				{
					Comment = " Empty Or Null Or N/A Or N/R feature value for feature name:" + featurename;
					throw new Exception(Comment);
				}
				// System.out.println(operator = formula.substring(startindx,endindx+1));
				System.out.println(formula = formula.replaceFirst("\\$\\{" + featurename + "\\}", featurevalue));
			}

			// valid = macro.formulaExcution(formula);

		}catch(Exception ex)
		{
			valid = false;
			ex.printStackTrace();

		}

		return valid;
	}

	private boolean validateContans(FeatureDTO fet, QaChecksInDependentFeature qachecksindependent)
	{
		boolean valid = true;
		String fetnm = fet.getFeatureName();
		String operandtow = qachecksindependent.getValue();
		String operandone = fet.getFeaturevalue();

		try
		{
			if(!operandone.contains(operandtow))
			{
				valid = false;
			}
		}catch(Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return valid;
	}

	private boolean ValidateCondition(FeatureDTO fet, QaChecksInDependentFeature qachecksindependent)
	{
		boolean valid = true;
		String fetnm = fet.getFeatureName();
		String operation = qachecksindependent.getOperation();
		String operandtow = qachecksindependent.getValue();
		String operandone = fet.getFeaturevalue();

		try
		{
			// if (!macro.conditionExpretion("\"" + operandone + "\"", operation,
			// "\"" + operandtow + "\"")) {
			// Comment = "the feature name:" + fetnm + " " + operation + " "
			// + operandtow;
			// valid = false;
			//
			// }
		}catch(Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return valid;
	}

	private boolean validateSempleDependentFeatures(RelatedFeaturesDTO datarow, Map sheetFeatures, Map dependentFeaturesfromdb)
	{
		Iterator sheetfeaturesiter = dependentFeaturesfromdb.keySet().iterator();
		boolean valid = true;
		String fetnm;
		String operandone;
		String operation;
		String operandtow;
		while(sheetfeaturesiter.hasNext())
		{
			fetnm = (String) sheetfeaturesiter.next();

			if(!Validate(datarow, (FeatureDTO) sheetFeatures.get(fetnm), (QaChecksInDependentFeature) dependentFeaturesfromdb.get(fetnm)))
			{

				valid = false;
				break;
			}
		}

		return valid;
	}

}
