package com.se.parametric.dev;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.swing.text.html.HTMLDocument.HTMLReader.PreAction;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.se.automation.db.SessionUtil;
import com.se.automation.db.client.mapping.ApprovedParametricValue;
import com.se.automation.db.client.mapping.PartComponent;
import com.se.automation.db.client.mapping.Feature;
import com.se.automation.db.client.mapping.ParametricReviewData;
import com.se.automation.db.client.mapping.PartsParametricValuesGroup;
import com.se.automation.db.client.mapping.Pl;
import com.se.automation.db.client.mapping.PlFeature;
import com.se.automation.db.client.mapping.PreQaCheckers;
import com.se.automation.db.client.mapping.QaCheckParts;
import com.se.automation.db.client.mapping.QaChecksActions;
import com.se.automation.db.client.mapping.QaRelatedFeature;
import com.se.automation.db.client.mapping.Supplier;
import com.se.automation.db.client.mapping.TblRules;
import com.se.automation.db.client.mapping.TrackingParametric;
import com.se.automation.db.client.mapping.TrackingTaskStatus;
import com.se.automation.db.client.mapping.Value;

public class RelatedFeature
{
	static ScriptEngineManager mgr = new ScriptEngineManager();
	static ScriptEngine engine = mgr.getEngineByName("JavaScript");
	static String currentValues = "";

	public static void main(String args[])
	{
//		startEngin2();
		try
		{
			Session automationSession = SessionUtil.getSession();

			ArrayList<ArrayList<String>> input = new ArrayList<ArrayList<String>>();
			Criteria criteria = automationSession.createCriteria(Pl.class);
			criteria.add(Restrictions.eq("name", "Electromechanical Relay"));
			Pl pl = (Pl) criteria.uniqueResult();
			ArrayList<String> row = new ArrayList<String>();
			row.add("Contact Arrangement");
			row.add("SPST-NO");
			input.add(row);
			row = new ArrayList<String>();
			// Output Voltage=0.7*Peak Output Voltage
			row.add("Contact Form");
			row.add("1 Form C");
			input.add(row);
			getConflictRelatedFeature(pl, input);

		}catch(Exception e)
		{

		}
	}

	public static void startEngin()
	{
		Session automationSession = null;
		Criteria criteria = null;
		while(true)
		{
			try
			{
				List<Object> comIds = null;
				BigDecimal comId = null;
				automationSession = SessionUtil.getSession();
				criteria = automationSession.createCriteria(TrackingTaskStatus.class);
				criteria.add(Restrictions.eq("name", "Related Feature"));
				TrackingTaskStatus status = (TrackingTaskStatus) criteria.uniqueResult();
				criteria = automationSession.createCriteria(TrackingParametric.class);
				criteria.add(Restrictions.eq("trackingTaskStatus", status));
				List<TrackingParametric> list = criteria.list();
				for(int i = 0; i < list.size(); i++)
				{
					System.out.println("i is " + i);
					TrackingParametric row = list.get(i);
					String sql = "";
					Supplier supplier = row.getSupplier();
					Pl pl = row.getPl();
					System.out.println("" + pl.getName() + " " + supplier.getName() + " " + row.getId());
					criteria = automationSession.createCriteria(TblRules.class);
					criteria.add(Restrictions.eq("pl", pl));
					List<TblRules> rules = criteria.list();
					List<Object> out = null;
					List<Object> in = null;
					boolean flag1 = false;
					boolean flag2 = false;
					for(int k = 0; k < rules.size(); k++)
					{
						TblRules rule = rules.get(k);
						Feature outFet = rule.getOutFeature();
						Feature inFet = rule.getInFeature();
						String outFetOperator = rule.getOutFetOperator();
						String inputValue = rule.getInFetValue();
						String outputValue = rule.getOutFetValue();
						if(!outFetOperator.equals("E"))
						{

							String inFetOperator = rule.getInFetOperator();
							comIds = automationSession.createSQLQuery("select distinct com_id from Parametric_Review_Data where TRACKING_PARAMETRIC_ID=" + row.getId()).list();
							for(int com = 0; com < comIds.size(); com++)
							{
								comId = (BigDecimal) comIds.get(com);
								sql = "select FULL_VALUE from approved_parametric_values where id in (select APPROVED_VALUE_SEPARATION_ID from parametric_separation_group where APPROVED_VALUE_GROUP_ID in(select group_approved_value_id from Parametric_Review_Data where TRACKING_PARAMETRIC_ID=18271082625152 and PL_FEATURE_ID  = (select id from PL_FEATURE_UNIT where fet_id="
										+ outFet.getId() + " and pl_id=" + pl.getId() + ")))";
								out = automationSession.createSQLQuery(sql).list();
								sql = "select FULL_VALUE from approved_parametric_values where id in (select APPROVED_VALUE_SEPARATION_ID from parametric_separation_group where APPROVED_VALUE_GROUP_ID in(select group_approved_value_id from Parametric_Review_Data where TRACKING_PARAMETRIC_ID=18271082625152 and PL_FEATURE_ID  = (select id from PL_FEATURE_UNIT where fet_id="
										+ inFet.getId() + " and pl_id=" + pl.getId() + ")))";
								in = automationSession.createSQLQuery(sql).list();
								for(int j = 0; j < out.size(); j++)
								{
									String outValue = out.get(j).toString();
									if((outValue.equals(outputValue) && outFetOperator.equals("=")) || (!outValue.equals(outputValue) && outFetOperator.equals("<>")))
									{
										flag1 = true;
									}
									for(int l = 0; l < in.size(); l++)
									{
										String inValue = in.get(l).toString();
										if((inValue.equals(inputValue) && inFetOperator.equals("=")) || (!inValue.equals(inputValue) && inFetOperator.equals("<>")))
										{
											flag1 = true;
										}
										if(flag1 && flag2)
										{
											System.out.println("error part");
										}
									}
								}

							}
						}
						else
						{

							System.out.println("Equation");
						}
					}

					criteria = automationSession.createCriteria(TblRules.class);
					criteria.add(Restrictions.eq("pl", pl));
					// List<TblRules> rules = criteria.list();
					// for(int k = 0; k < rules.size(); k++)
					// {
					// TblRules rule = rules.get(k);
					// String outFetOperator = rule.getOutFetOperator();
					// criteria = automationSession.createCriteria(ParametricReviewData.class);
					// criteria.add(Restrictions.eq("trackingParametric", row));
					// criteria.setProjection(Projections.distinct(Projections.property("component")));
					// List<PartComponent> componentList = criteria.list();
					// for(int j = 0; j < componentList.size(); j++)
					// {
					// PartComponent component = componentList.get(j);
					// String inFetValue = rule.getInFetValue();
					// String outFetValue = rule.getOutFetValue();
					// Feature outFeature = rule.getOutFeature();
					// if(outFetOperator.equals("E"))
					// {
					// ScriptEngineManager mgr = new ScriptEngineManager();
					// ScriptEngine engine = mgr.getEngineByName("JavaScript");
					// System.out.println("" + pl.getName());
					// String sql = solveEqu(inFetValue, pl, component, automationSession);
					// // criteria = automationSession.createCriteria(ParametricReviewData.class);
					// // criteria.add(Restrictions.eq("trackingParametric", row));
					// // criteria.add(Restrictions.eq("", component));
					// // List<ParametricReviewData> reviewList = criteria.list();
					// }
					// else
					// {
					// boolean flag = false;
					// Feature inFeature = rule.getInFeature();
					// String inFetOperator = rule.getInFetOperator();
					// criteria = automationSession.createCriteria(PlFeature.class);
					// criteria.add(Restrictions.eq("pl", pl));
					// criteria.add(Restrictions.eq("feature", outFeature));
					// PlFeature outPlFeature = (PlFeature) criteria.uniqueResult();
					// criteria = automationSession.createCriteria(PlFeature.class);
					// criteria.add(Restrictions.eq("pl", pl));
					// criteria.add(Restrictions.eq("feature", inFeature));
					// PlFeature inPlFeature = (PlFeature) criteria.uniqueResult();
					// criteria = automationSession.createCriteria(ParametricReviewData.class);
					// criteria.add(Restrictions.eq("component", component));
					// criteria.add(Restrictions.eq("plFeature", outPlFeature));
					// ParametricReviewData parametricReviewData = (ParametricReviewData) criteria
					// .uniqueResult();
					// if(parametricReviewData == null)
					// {
					// continue;
					// }
					// else
					// {
					// System.out.println("" + parametricReviewData.getGroupApprovedValueId());
					// criteria = automationSession.createCriteria(PartsParametricValuesGroup.class);
					// criteria.add(Restrictions.eq("groupId",
					// parametricReviewData.getGroupApprovedValueId()));
					// ArrayList<PartsParametricValuesGroup> group = (ArrayList<PartsParametricValuesGroup>) criteria
					// .list();
					// for(int a = 0; a < group.size(); a++)
					// {
					// if(group.get(a).getApprovedParametricValue().getFromValue().getValue()
					// .equals(outFetValue))
					// {
					// System.out.println("out is true");
					// flag = true;
					// break;
					// }
					// }
					// }
					// if(flag)
					// {
					// criteria = automationSession.createCriteria(ParametricReviewData.class);
					// criteria.add(Restrictions.eq("component", component));
					// criteria.add(Restrictions.eq("plFeature", inPlFeature));
					// ParametricReviewData parametricReviewData2 = (ParametricReviewData) criteria
					// .uniqueResult();
					//
					// if(parametricReviewData2 == null)
					// {
					// continue;
					// }
					//
					// else
					// {
					// System.out.println("" + parametricReviewData2.getGroupApprovedValueId());
					// criteria = automationSession.createCriteria(PartsParametricValuesGroup.class);
					// criteria.add(Restrictions.eq("groupId",
					// parametricReviewData2.getGroupApprovedValueId()));
					// ArrayList<PartsParametricValuesGroup> group = (ArrayList<PartsParametricValuesGroup>) criteria
					// .list();
					// for(int a = 0; a < group.size(); a++)
					// {
					// if(group.get(a).getApprovedParametricValue().getFromValue().getValue()
					// .equals(inFetValue))
					// {
					// System.out.println("in is true");
					// System.out.println("Com Id is " + component.getComId());
					// break;
					// }
					// }
					// }
					// }
					// }
					// }
					//
					// }

				}
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}

	}

	public static void startEngin2()
	{
		Session automationSession = null;
		Criteria criteria = null;
		while(true)
		{
			try
			{
				boolean found = false;
				ArrayList<ArrayList<String>> input = null;
				ArrayList<String> inputRow = null;
				List<Object> comIds = null;
				BigDecimal comId = null;
				BigDecimal plFeature = null;
				Object[] arr = null;
				List<Object[]> features = null;
				automationSession = SessionUtil.getSession();
				List<Object> plFeatures = null;
				criteria = automationSession.createCriteria(TrackingTaskStatus.class);
				criteria.add(Restrictions.eq("name", "Related Feature Engine"));
				TrackingTaskStatus status = (TrackingTaskStatus) criteria.uniqueResult();
				criteria = automationSession.createCriteria(TrackingParametric.class);
				criteria.add(Restrictions.eq("trackingTaskStatus", status));
				List<TrackingParametric> list = criteria.list();
				if(!list.isEmpty())
				{
					for(int i = 0; i < list.size(); i++)
					{
						System.out.println("i is " + i);
						TrackingParametric row = list.get(i);

						String sql = "";
						Supplier supplier = row.getSupplier();
						Pl pl = row.getPl();
						System.out.println("" + pl.getName() + " " + supplier.getName() + " " + row.getId());
						comIds = automationSession.createSQLQuery("select distinct com_id from Parametric_Review_Data where TRACKING_PARAMETRIC_ID=" + row.getId()).list();
						for(int com = 0; com < comIds.size(); com++)
						{
							input = new ArrayList<ArrayList<String>>();
							comId = (BigDecimal) comIds.get(com);
							sql = "select PL_FEATURE_ID from Parametric_Review_Data where TRACKING_PARAMETRIC_ID=" + row.getId() + " and com_id=" + comId;
							plFeatures = automationSession.createSQLQuery(sql).list();
							for(int p = 0; p < plFeatures.size(); p++)
							{
								plFeature = (BigDecimal) plFeatures.get(p);
								sql = "select get_feature_name_by_pl_feature("
										+ plFeature
										+ "),FULL_VALUE  from approved_parametric_values where id in (select APPROVED_VALUE_SEPARATION_ID from parametric_separation_group where APPROVED_VALUE_GROUP_ID in(select group_approved_value_id from Parametric_Review_Data where TRACKING_PARAMETRIC_ID="
										+ row.getId() + " and PL_FEATURE_ID  = " + plFeature + "))";
								features = automationSession.createSQLQuery(sql).list();
								inputRow = new ArrayList<String>();
								inputRow.add(features.get(0)[0].toString());
								inputRow.add(features.get(0)[1].toString());
								input.add(inputRow);

							}

							String result = getConflictRelatedFeature(pl, input);
							if(!result.isEmpty())
							{

								QaCheckParts check = new QaCheckParts();
								criteria = automationSession.createCriteria(PartComponent.class);
								criteria.add(Restrictions.eq("comId", comId.longValue()));
								PartComponent component = (PartComponent) criteria.uniqueResult();
								check.setPartComponent(component);
								check.setDocument(row.getDocument());
								check.setItemValue("" + comId);
								criteria = automationSession.createCriteria(PreQaCheckers.class);
								criteria.add(Restrictions.eq("id", 6l));
								PreQaCheckers preQaCheckers = (PreQaCheckers) criteria.uniqueResult();

								check.setPreQaCheckers(preQaCheckers);
								criteria = automationSession.createCriteria(QaChecksActions.class);
								criteria.add(Restrictions.eq("id", 0l));
								QaChecksActions qaChecksActions = (QaChecksActions) criteria.uniqueResult();
								check.setAction(qaChecksActions);
								check.setStoredate(new Date());
								automationSession.save(check);
								if(!automationSession.getTransaction().isInitiator())
									automationSession.beginTransaction();
								automationSession.getTransaction().commit();
								QaRelatedFeature qaRelatedFeature = new QaRelatedFeature();
								qaRelatedFeature.setPreQaCheckers(preQaCheckers);
								qaRelatedFeature.setQaChecksActions(qaChecksActions);
								qaRelatedFeature.setRule(result);
								qaRelatedFeature.setInputValues(currentValues);
								automationSession.save(qaRelatedFeature);
								if(!automationSession.getTransaction().isInitiator())
									automationSession.beginTransaction();
								automationSession.getTransaction().commit();
								found = true;
							}
						}
						if(found)
						{
							found = false;
							automationSession.createSQLQuery("update  Tracking_Parametric set tracking_task_status_id =40 where ID=" + row.getId()).executeUpdate();
							if(!automationSession.getTransaction().isInitiator())
								automationSession.beginTransaction();
							automationSession.getTransaction().commit();
						}
						else
						{
							automationSession.createSQLQuery("update  Tracking_Parametric set tracking_task_status_id =3 where ID=" + row.getId()).executeUpdate();
							if(!automationSession.getTransaction().isInitiator())
								automationSession.beginTransaction();
							automationSession.getTransaction().commit();
						}
					}
				}
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}

	}

	public static String getConflictRelatedFeature(Pl pl, ArrayList<ArrayList<String>> features)
	{
		String result = "";
		currentValues = "";
		Criteria criteria = null;
		Session automationSession = SessionUtil.getSession();
		try
		{
			criteria = automationSession.createCriteria(TblRules.class);
			criteria.add(Restrictions.eq("pl", pl));
			List<TblRules> rules = criteria.list();
			ArrayList<String> feature = null;
			boolean flag1 = false;
			boolean flag2 = false;
			String feature1 = "";
			String feature2 = "";
			TblRules rule = null;
			String outputFeature = "";
			String inputFeature = "";
			String inputOperator = "";
			String outputValue = "";
			String inputValue = "";
			for(int j = 0; j < rules.size(); j++)
			{

				rule = rules.get(j);
				String outputOperator = rules.get(j).getOutFetOperator();
				if(!outputOperator.equals("E"))
				{
					for(int i = 0; i < features.size(); i++)
					{
						feature = features.get(i);

						outputFeature = rules.get(j).getOutFeature().getName();
						inputFeature = rules.get(j).getInFeature().getName();

						inputOperator = rules.get(j).getInFetOperator();
						outputValue = rules.get(j).getOutFetValue();
						inputValue = rules.get(j).getInFetValue();

						if(features.get(i).get(0).equals(rules.get(j).getOutFeature().getName()))
						{
							if((features.get(i).get(1).equals(rules.get(j).getOutFetValue()) && rules.get(j).getOutFetOperator().equals("="))
									|| (!features.get(i).get(1).equals(rules.get(j).getOutFetValue()) && rules.get(j).getOutFetOperator().equals("<>")))
							{
								flag1 = true;
								feature1 = features.get(i).get(0) + "=" + features.get(i).get(1);
							}
						}
						if(features.get(i).get(0).equals(rules.get(j).getInFeature().getName()))
						{
							if((!features.get(i).get(1).equals(rules.get(j).getInFetValue()) && rules.get(j).getInFetOperator().equals("="))
									|| (features.get(i).get(1).equals(rules.get(j).getInFetValue()) && rules.get(j).getInFetOperator().equals("<>")))
							{
								flag2 = true;
								feature2 = features.get(i).get(0) + "=" + features.get(i).get(1);
							}
						}

					}
					if(flag1 && flag2)
					{
						result += "|if" + outputFeature + outputOperator + outputValue + " must not " + inputFeature + inputOperator + inputValue;
						currentValues += "|" + feature1 + "," + feature2;

					}
				}
				else
				{
					inputValue = rules.get(j).getInFetValue();

					ArrayList<String[]> equation = splitEquation(inputValue);
					String arr[] = equation.get(0);
					String operators[] = equation.get(1);
					if(operators[0].equals("="))
					{
						operators[0] = "==";
					}
					else if(operators[0].equals("<>"))
					{
						operators[0] = "!=";
					}
					DD: for(int k = 0; k < arr.length; k++)
					{
						if(!arr[k].replaceAll("[\\d\\.]", "").isEmpty())
						{
							for(int i = 0; i < features.size(); i++)
							{
								feature = features.get(i);
								System.out.println(feature.get(0) + "  " + feature.get(1));

								if(feature.get(0).equals(arr[k]))
								{
									arr[k] = feature.get(1);
									flag1 = true;
								}
							}
							if(!flag1)
							{
								break DD;
							}
							else
							{
								flag1 = false;
							}
						}

					}
					String finalString = "";
					for(int k = 0; k < arr.length; k++)
					{
						if(k == arr.length - 1)
						{
							finalString += arr[k];
						}
						else
						{
							finalString += arr[k] + operators[k];
						}
					}
					Boolean greaterThan5 = (Boolean) engine.eval(finalString);
					if(!greaterThan5)
					{
						result += "must " + inputValue;
					}
					System.out.println(finalString);
				}
				flag1 = false;
				flag2 = false;
				feature1 = "";
				feature2 = "";
			}

		}catch(Exception e)
		{

		}
		currentValues = currentValues.replaceFirst("|", "");
		return result.replaceFirst("|", "");
	}

	public static void solve(ArrayList<String[]> equation)
	{

	}

	public static ArrayList<String[]> splitEquation(String req)
	{
		String str = req;
		ArrayList<String[]> result = new ArrayList<String[]>();
		String[] numbers = null;
		String operator[] = null;
		if(req.contains(" to "))
		{
			numbers = req.split(" to ");
			operator = new String[1];
			operator[0] = " to ";
		}
		else
		{
			numbers = req.split("[\\*\\+\\-\\/\\>\\<\\=]");
			operator = new String[numbers.length - 1];
			for(int i = 0; i < numbers.length; i++)
			{
				if(i != numbers.length - 1)
				{
					int count = numbers[i].length();
					String string = "";
					for(int j = count; j < str.length(); j++)
					{
						string += "" + str.charAt(j);
					}
					str = string;
					operator[i] = "" + str.charAt(0);
					string = "";
					for(int j = 1; j < str.length(); j++)
					{
						string += "" + str.charAt(j);
					}
					str = string;
				}
			}
		}
		result.add(numbers);
		result.add(operator);
		return result;
	}

	public <T> T[] toArray(T[] a, T e)
	{
		T[] b = (T[]) new Object[a.length + 1];
		for(int i = 0; i < a.length; i++)
		{
			b[i] = a[i];
		}
		b[a.length] = e;
		return b;
	}

	public static String solveEqu(String Equ, Pl pl, PartComponent component, Session session)
	{
		String sql = "";
		String[] sides = null;
		String equOperator = "";
		String[] rightSides = null;
		String rightSidesOperator = "";
		if(Equ.contains("="))
		{
			sides = Equ.split("=");
			Equ = Equ.replace("=", "==");
			equOperator = "=";
			if(sides[1].contains(" to "))
			{
				rightSides = sides[1].split(" to ");
				rightSidesOperator = " to ";
			}
		}
		else if(Equ.contains("<>"))
		{
			sides = Equ.split("\\<\\>");
			equOperator = "<>";
			if(sides[1].contains(" to "))
			{
				rightSides = sides[1].split(" to ");
				rightSidesOperator = " to ";
			}
		}
		else if(Equ.contains(">"))
		{
			sides = Equ.split("\\>");
			equOperator = ">";
		}
		else if(Equ.contains("<"))
		{
			sides = Equ.split("\\<");
			equOperator = "<";
		}
		String leftSide = "";
		String rightSide = "";
		LinkedList<ArrayList<ApprovedParametricValue>> result = null;
		String[] operator = null;
		ArrayList<String[]> split = null;
		// if(sides.length == 1)
		// {
		// split = splitEquation(Equ);
		// result = getFeatures(pl, split.get(0), component, session);
		//
		// operator = split.get(1);
		// for(int i = 0; i < result.size(); i++)
		// {
		// System.out.println("right is " + result.get(i));
		// }
		// sql = "SELECT  CM.GET_COM_PN_BY_COMID(com_id),CM.GET_MAN_NAME( CM.GET_MAN_ID_BY_COM_ID(com_id) )," + result.get(0) + "," + result.get(1) +
		// " from (SELECT   com_id, CM.TONUMERIC(cm.get_fetvalue (" + result.get(0) + "))" + result.get(0)
		// + ", CM.TONUMERIC(cm.get_fetvalue (" + result.get(1) + ")) " + result.get(1) + " FROM   dynamic_flat WHERE   pl_id = " + pl.getId() +
		// ") where " + result.get(0) + operator[0] + result.get(1);
		// System.out.println("here is " + sql);
		// }
		// else
		// {
		// for(int i = 0; i < sides.length; i++)
		// {
		// System.out.println("sides is " + sides[i]);
		// }
		// String[] equ = Equ.split("[=\\>\\<]");
		leftSide = sides[0];
		rightSide = sides[1];
		String[] fet = new String[1];
		fet[0] = leftSide;
		split = splitEquation(rightSide);

		for(int i = 0; i < split.get(0).length; i++)
		{
			ArrayList<ApprovedParametricValue> rightApproved = null;
			if(!split.get(0)[i].replaceAll("[\\d\\.]", "").equals(""))
			{
				rightApproved = getFeatures(pl, split.get(0)[i], component, session);
				if(rightApproved != null)
				{
					Equ = Equ.replace(split.get(0)[i], rightApproved.get(0).getFromValue().getValue());
				}
				else
				{
					return null;
				}
			}
		}
		ArrayList<ApprovedParametricValue> leftFet = getFeatures(pl, leftSide, component, session);
		// result = getFeatures(pl, split.get(0), component, session);
		if(leftFet != null)
		{
			Equ = Equ.replace(leftSide, leftFet.get(0).getFromValue().getValue());

		}
		else
		{
			return null;
		}
		// Equ=Equ.replace(sides[1], rightSide);
		operator = split.get(1);
		String left = "";
		String where = "";
		String mid = "";
		// for(int i = 0; i < result.size(); i++)
		// {
		// left += "," + result.get(i);
		// // if(){}
		// mid += "," + "CM.TONUMERIC (cm.get_fetvalue (" + result.get(i) + "))" + result.get(i);
		// if(i != (result.size() - 1))
		// {
		// where += result.get(i) + operator[i];
		// }
		// else
		// {
		// where += result.get(i);
		// }
		//
		// // }
		//
		// operator = split.get(1);
		// sql = "SELECT  CM.GET_COM_PN_BY_COMID(com_id),CM.GET_MAN_NAME( CM.GET_MAN_ID_BY_COM_ID(com_id) )," + leftFet.get(0) + left +
		// " FROM(SELECT com_id, CM.TONUMERIC (cm.get_fetvalue (" + leftFet.get(0) + ")) " + leftFet.get(0) + mid
		// + " FROM cm.dynamic_flat WHERE pl_id = " + pl.getId() + ") WHERE " + leftFet.get(0) + equOperator + where;
		// System.out.println("here is " + sql);
		// }
		return Equ;
	}

	public static LinkedList<ArrayList<ApprovedParametricValue>> getFeatures(Pl pl, String[] arr, PartComponent component, Session session)
	{
		LinkedList<ArrayList<ApprovedParametricValue>> features = new LinkedList<ArrayList<ApprovedParametricValue>>();
		for(int i = 0; i < arr.length; i++)
		{
			String str = arr[i].replaceAll("[\\d\\.]", "");
			if(!str.equals(""))
			{
				Criteria criteria = session.createCriteria(Feature.class);
				criteria.add(Restrictions.eq("name", str));
				Feature feature = (Feature) criteria.uniqueResult();
				criteria = session.createCriteria(PlFeature.class);
				criteria.add(Restrictions.eq("pl", pl));
				criteria.add(Restrictions.eq("feature", feature));
				PlFeature plFeature = (PlFeature) criteria.uniqueResult();
				criteria = session.createCriteria(ParametricReviewData.class);
				criteria.add(Restrictions.eq("component", component));
				criteria.add(Restrictions.eq("plFeature", plFeature));
				ParametricReviewData parametricReviewData = (ParametricReviewData) criteria.uniqueResult();
				if(parametricReviewData == null)
				{
					return null;
				}
				else
				{
					System.out.println("" + parametricReviewData.getGroupApprovedValueId());
					criteria = session.createCriteria(ApprovedParametricValue.class);
					criteria.add(Restrictions.eq("id", parametricReviewData.getGroupApprovedValueId()));
					features.add((ArrayList<ApprovedParametricValue>) criteria.list());
				}
			}
			else
			{
				// features.add(arr[i]);
			}
		}
		return features;
	}

	public static ArrayList<ApprovedParametricValue> getFeatures(Pl pl, String featureString, PartComponent component, Session session)
	{
		ArrayList<ApprovedParametricValue> features = new ArrayList<ApprovedParametricValue>();
		Criteria criteria = session.createCriteria(Feature.class);
		criteria.add(Restrictions.eq("name", featureString));
		Feature feature = (Feature) criteria.uniqueResult();
		criteria = session.createCriteria(PlFeature.class);
		criteria.add(Restrictions.eq("pl", pl));
		criteria.add(Restrictions.eq("feature", feature));
		PlFeature plFeature = (PlFeature) criteria.uniqueResult();
		criteria = session.createCriteria(ParametricReviewData.class);
		System.out.println("com_id is " + component.getComId());
		criteria.add(Restrictions.eq("component", component));
		criteria.add(Restrictions.eq("plFeature", plFeature));
		ParametricReviewData parametricReviewData = (ParametricReviewData) criteria.uniqueResult();
		if(parametricReviewData == null)
		{
			return null;
		}
		else
		{
			System.out.println("" + parametricReviewData.getGroupApprovedValueId());
			criteria = session.createCriteria(PartsParametricValuesGroup.class);
			criteria.add(Restrictions.eq("groupId", parametricReviewData.getGroupApprovedValueId()));
			List<PartsParametricValuesGroup> groups = criteria.list();
			for(int i = 0; i < groups.size(); i++)
			{
				features.add(groups.get(i).getApprovedParametricValue());
			}
			// criteria = session.createCriteria(ApprovedParametricValue.class);
			// criteria.add(Restrictions.eq("id", group.getGroupApprovedValueId()));
			// features = (ArrayList<ApprovedParametricValue>) criteria.list();
		}
		return features;
	}
}
