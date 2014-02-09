package com.se.parametric.dba;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;

import com.se.automation.db.QueryUtil;
import com.se.automation.db.SessionUtil;
import com.se.automation.db.client.mapping.ApprovedParametricValue;
import com.se.automation.db.client.mapping.ApprovedValueFeedback;
import com.se.automation.db.client.mapping.Condition;
import com.se.automation.db.client.mapping.Document;
import com.se.automation.db.client.mapping.Feature;
import com.se.automation.db.client.mapping.Multiplier;
import com.se.automation.db.client.mapping.MultiplierUnit;
import com.se.automation.db.client.mapping.ParaFeedbackStatus;
import com.se.automation.db.client.mapping.ParaIssueType;
import com.se.automation.db.client.mapping.ParametricApprovedGroup;
import com.se.automation.db.client.mapping.ParametricFeedback;
import com.se.automation.db.client.mapping.ParametricFeedbackCycle;
import com.se.automation.db.client.mapping.ParametricReviewData;
import com.se.automation.db.client.mapping.ParametricSeparationGroup;
import com.se.automation.db.client.mapping.PartsFeedback;
import com.se.automation.db.client.mapping.PartsParametricValuesGroup;
import com.se.automation.db.client.mapping.Pl;
import com.se.automation.db.client.mapping.PlFeature;
import com.se.automation.db.client.mapping.Sign;
import com.se.automation.db.client.mapping.TrackingFeedbackType;
import com.se.automation.db.client.mapping.TrackingParametric;
import com.se.automation.db.client.mapping.TrackingTaskQaStatus;
import com.se.automation.db.client.mapping.TrackingTaskStatus;
import com.se.automation.db.client.mapping.Unit;
import com.se.automation.db.client.mapping.Value;
import com.se.automation.db.client.mapping.ValueType;
import com.se.grm.client.mapping.GrmUser;
import com.se.parametric.dto.ApprovedParametricDTO;
import com.se.parametric.dto.FeedBackData;
import com.se.parametric.dto.GrmUserDTO;
import com.se.parametric.dto.UnApprovedDTO;

public class ApprovedDevUtil
{

	/**
	 * @author ahmad_Makram
	 * @param row
	 * @param status
	 *            Last update @ 9-9-2013 to be Common for QA and parametric TL
	 * 
	 */
	public static void setValueApproved(ArrayList<String> row, String status)
	{
		Session session = SessionUtil.getSession();
		Criteria criteria;
		try
		{
			ParametricApprovedGroup group = ParaQueryUtil.getParametricApprovedGroup(row.get(4), row.get(0), row.get(3), session);
			criteria = session.createCriteria(TrackingTaskStatus.class);
			criteria.add(Restrictions.eq("name", status));
			TrackingTaskStatus trackingTaskStatus = (TrackingTaskStatus) criteria.uniqueResult();
			group.setStatus(trackingTaskStatus);
			session.saveOrUpdate(group);

		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			session.close();
		}
	}

	public static void updateApprovedValue(int updateFlag, UnApprovedDTO app)
	{
		Session session = SessionUtil.getSession();
		try
		{
			List<ApprovedParametricDTO> approved = createApprovedValuesList(app.getFeatureValue(), app.getPlName(), app.getFeatureName(), app.getFeatureUnit(), app.getSign(), app.getValue(), app.getMultiplier(), app.getUnit(), app.getCondition(),
					app.getType());

			saveAppGroupAndSepValue(0, updateFlag, approved, app.getPlName(), app.getFeatureName(), app.getFeatureValue(), app.getPdfUrl(), app.getUserId());

		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			session.close();
		}
	}

	public static List<ApprovedParametricDTO> createApprovedValuesList(String fullValue, String plName, String featureName, String featureUnit, String sign, String value, String multiplier, String unit, String condition, String valueType)
			throws ArrayIndexOutOfBoundsException
	{
		Session session = SessionUtil.getSession();
		Criteria criteria = null;
		List<ApprovedParametricDTO> approvedParametricValueDTOList = new ArrayList<ApprovedParametricDTO>();
		ApprovedParametricDTO approvedParametricDTO = null;

		try
		{
			boolean toflag = false;
			if(value.contains(" to "))
				toflag = true;
			ArrayList<String[]> valueSections = getSeparatedSections(value, toflag);
			ArrayList<String[]> signSections = getSeparatedSections(sign, toflag);
			ArrayList<String[]> multipSections = getSeparatedSections(multiplier, toflag);
			ArrayList<String[]> unitSections = getSeparatedSections(unit, toflag);
			ArrayList<String[]> condSections = getSeparatedSections(condition, toflag);
			ArrayList<String[]> valueTypeSections = getSeparatedSections(valueType, toflag);

			String[] valueArr = valueSections.get(0);
			String[] signArr = signSections.get(0);
			String[] multiplierArr = multipSections.get(0);
			String[] unitArr = unitSections.get(0);
			String[] conditionArr = condSections.get(0);
			String[] valueTypeArr = valueTypeSections.get(0);

			String[] valueSepArr = valueSections.get(1);
			String[] signSepArr = signSections.get(1);
			String[] multiplierSepArr = multipSections.get(1);
			String[] unitSepArr = unitSections.get(1);
			String[] conditionSepArr = condSections.get(1);
			String[] valueTypeSepArr = valueTypeSections.get(1);

			int multiValCount = valueArr.length;
			System.out.println("multiValCount+++++++++++ " + multiValCount);
			if(!sign.trim().equals("") && signArr.length > multiValCount)
				throw new ArrayIndexOutOfBoundsException("Error number of sticks in sign. \nPlease enter a valid sign");
			if(!multiplier.trim().equals("") && multiplierArr.length > multiValCount)
				throw new ArrayIndexOutOfBoundsException("Error number of sticks in multiplier. \nPlease enter a valid multiplier");
			if(!unit.trim().equals("") && unitArr.length > multiValCount)
				throw new ArrayIndexOutOfBoundsException("Error number of sticks in unit. \nPlease enter a valid unit");
			if(!condition.trim().equals("") && conditionArr.length > multiValCount)
				throw new ArrayIndexOutOfBoundsException("Error number of sticks in condition. \nPlease enter a valid condition");
			if(!valueType.trim().equals("") && valueTypeArr.length > multiValCount)
				throw new ArrayIndexOutOfBoundsException("Error number of sticks in value type. \nPlease enter a valid value type");

			for(int i = 0; i < multiValCount; i++)
			{
				approvedParametricDTO = new ApprovedParametricDTO();

				approvedParametricDTO.setFromValue(valueArr[i].trim());
				if(signArr.length > i)
					approvedParametricDTO.setFromSign(signArr[i].trim());
				if(conditionArr.length > i)
					approvedParametricDTO.setFromCondition(conditionArr[i].trim());
				if(multiplierArr.length > i)
					approvedParametricDTO.setFromMultiplier(multiplierArr[i].trim());
				if(unitArr.length > i)
					approvedParametricDTO.setFromUnit(unitArr[i].trim());
				if(valueTypeArr.length > i)
					approvedParametricDTO.setFromValueType(valueTypeArr[i].trim());

				approvedParametricDTO.setFeatUnit(featureUnit);
				approvedParametricDTO.setFullValue(approvedParametricDTO.toString());
				String pattern = fullValue;

				String signSep = "", conditionSep = "", valueTypeSep = "", multiplierSep = "", unitSep = "";
				if((value.contains("|") || value.contains("!") || value.contains(" to ")) && !valueSepArr[i].equals(""))
				{
					if(signSepArr.length > i)
						signSep = signSepArr[i];
					if(conditionSepArr.length > i)
						conditionSep = conditionSepArr[i];
					if(valueTypeSepArr.length > i)
						valueTypeSep = valueTypeSepArr[i];
					if(multiplierSepArr.length > i)
						multiplierSep = multiplierSepArr[i];
					if(unitSepArr.length > i)
						unitSep = unitSepArr[i];
					pattern = valueSepArr[i] + " $ " + signSep + " $ " + conditionSep + " $ " + valueTypeSep + " $ " + multiplierSep + " $ " + unitSep;
				}
				else
				{
					pattern = "";
				}

				approvedParametricDTO.setPattern(pattern);
				PlFeature plFeature = ParaQueryUtil.getPlFeatureByExactName(featureName, plName, session);
				criteria = session.createCriteria(ApprovedParametricValue.class);
				criteria.add(Restrictions.eq("plFeature", plFeature));
				criteria.add(Restrictions.eq("fullValue", approvedParametricDTO.toString()));
				ApprovedParametricValue approvedParametricValue = (ApprovedParametricValue) criteria.uniqueResult();

				approvedParametricDTO.setApprovedParametricValue(approvedParametricValue);

				approvedParametricValueDTOList.add(approvedParametricDTO);
			}
		}finally
		{
			session.close();
		}

		return approvedParametricValueDTOList;
	}

	public static long saveAppGroupAndSepValue(int engine, int update, List<ApprovedParametricDTO> approvedParametricDTOList, String plName, String featureName, String groupFullValue, String pdfurl, Long userId) throws Exception
	{
		Session session = SessionUtil.getSession();
		int approvedValueOrder = 1;
		ParametricApprovedGroup approvedGroup = null;
		try
		{
			Document document = null;
			if(pdfurl != null && !pdfurl.isEmpty())
				document = ParaQueryUtil.getDocumentBySeUrl(pdfurl, session);
			approvedGroup = ParaQueryUtil.addAppValueGroup(engine, update, document, plName, featureName, groupFullValue, userId, session);
			if(approvedGroup != null)
				ParaQueryUtil.deleteSeprationGroups(approvedGroup, session);
			approvedValueOrder = 1;

			for(ApprovedParametricDTO approvedParametricDTO : approvedParametricDTOList)
			{
				saveApprovedParametricValue(engine, update, approvedParametricDTO, plName, featureName, approvedGroup, approvedValueOrder, userId, session);
				approvedValueOrder++;
			}

		}catch(ConstraintViolationException e)
		{
			e.printStackTrace();
		}

		finally
		{
			session.close();
		}
		return approvedGroup.getId();
	}

	public static ApprovedParametricValue saveApprovedParametricValue(int engine, int update, ApprovedParametricDTO approvedParametricDTO, String plName, String featureName, ParametricApprovedGroup parametricApprovedGroup, int approvedValueOrder,
			Long paraUserId, Session sessionold) throws Exception
	{
		Session session = SessionUtil.getSession();
		try
		{
			ApprovedParametricValue approvedParametricValue = null;
			PlFeature plFeature = ParaQueryUtil.getPlFeatureByExactName(featureName, plName, session);
			System.out.println("approved value Id is: ");
			System.out.println((approvedParametricDTO.getApprovedParametricValue() == null) ? "null" : approvedParametricDTO.getApprovedParametricValue().getId());
			if(approvedParametricDTO.getApprovedParametricValue() != null && approvedParametricDTO.getApprovedParametricValue().getIsApproved() == 1l)
			{
				ParaQueryUtil.addSeparationGroup(engine, update, approvedParametricDTO.getApprovedParametricValue(), approvedParametricDTO.getPattern(), parametricApprovedGroup, approvedValueOrder, session);
				return approvedParametricDTO.getApprovedParametricValue();
			}

			/*
			 * updated by Ahmed Makram @ 3-11-2013 to Update the approved value separation which not approved.
			 */

			if(approvedParametricDTO.getApprovedParametricValue() != null && approvedParametricDTO.getApprovedParametricValue().getIsApproved() == 0l)
			{
				ApprovedParametricValue approvedParametricValue1 = approvedParametricDTO.getApprovedParametricValue();
				approvedParametricValue = (ApprovedParametricValue) session.get(ApprovedParametricValue.class, approvedParametricDTO.getApprovedParametricValue().getId());
				System.out.println(approvedParametricValue1.equals(approvedParametricValue));
				approvedParametricValue.setFromSign(null);
				approvedParametricValue.setToSign(null);
				approvedParametricValue.setFromCondition(null);
				approvedParametricValue.setToCondition(null);
				approvedParametricValue.setFromValueType(null);
				approvedParametricValue.setToValueType(null);
				approvedParametricValue.setFromMultiplierUnit(null);
				approvedParametricValue.setToMultiplierUnit(null);
				// session.merge(approvedParametricValue);
				// approvedParametricDTO.setApprovedParametricValue(approvedParametricValue);
				session.update(approvedParametricValue);
			}
			else
			{
				approvedParametricValue = new ApprovedParametricValue();
				// approvedParametricValue.setPattern(approvedParametricDTO.getPattern());
				approvedParametricValue.setId(System.nanoTime());
				approvedParametricValue.setPlFeature(plFeature);
			}

			approvedParametricValue.setFullValue(approvedParametricDTO.getFullValue());
			Date d = new Date();
			approvedParametricValue.setStoreDate(d);
			// System.out.println("date is " + d.getHours());

			// //
			// String condition1=approvedParametricValue.getFromCondition().getName();
			// String full=approvedParametricValue.getFullValue();
			// String multiplier1=approvedParametricValue.getFromMultiplierUnit().getMultiplier().getName();
			// String unit1=approvedParametricValue.getFromMultiplierUnit().getUnit().getName();
			// String sign1=approvedParametricValue.getFromSign().getName();
			// String value1=approvedParametricValue.getFromValue().getValue();
			// String type1=approvedParametricValue.getFromValueType().getName();
			// System.out.println(condition1+" "+full+" "+multiplier1+" "+unit1+" "+sign1+" "+value1+" "+type1);
			// //
			approvedParametricValue.setFromValue(ParaQueryUtil.getValueByExactValue(approvedParametricDTO.getFromValue(), session));
			if(approvedParametricValue.getFromValue() == null)
			{
				// add this value
				Value value = new Value();
				ParaQueryUtil.saveValue(session, approvedParametricDTO.getFromValue(), value);
				approvedParametricValue.setFromValue(value);
			}

			if(approvedParametricDTO.getFromSign() != null && !approvedParametricDTO.getFromSign().equals(""))
			{
				approvedParametricValue.setFromSign(ParaQueryUtil.getSignByExactName(approvedParametricDTO.getFromSign(), session));
				if(approvedParametricValue.getFromSign() == null)
				{
					Sign sign = new Sign();
					sign.setId(System.nanoTime());
					sign.setStoreDate(new Date());
					sign.setName(approvedParametricDTO.getFromSign());
					ParaQueryUtil.saveSign(sign, session);
					approvedParametricValue.setFromSign(sign);
				}
			}
			if(approvedParametricDTO.getFromCondition() != null && !approvedParametricDTO.getFromCondition().equals(""))
			{
				approvedParametricValue.setFromCondition(ParaQueryUtil.getConditionByExactName(approvedParametricDTO.getFromCondition(), session));
				if(approvedParametricValue.getFromCondition() == null)
				{
					Condition condition = new Condition();
					condition.setId(System.nanoTime());
					condition.setStoreDate(new Date());
					condition.setName(approvedParametricDTO.getFromCondition());
					ParaQueryUtil.saveCondition(session, condition);
					approvedParametricValue.setFromCondition(condition);
				}
			}
			if(approvedParametricDTO.getFromValueType() != null && !approvedParametricDTO.getFromValueType().equals(""))
			{
				approvedParametricValue.setFromValueType(ParaQueryUtil.getValueTypeByExactName(approvedParametricDTO.getFromValueType(), session));
				if(approvedParametricValue.getFromValueType() == null)
				{
					ValueType valueType = new ValueType();
					valueType.setId(System.nanoTime());
					valueType.setStoreDate(new Date());
					valueType.setName(approvedParametricDTO.getFromValueType());

					ParaQueryUtil.saveValueType(session, valueType);
					approvedParametricValue.setFromValueType(valueType);
				}
			}
			// -----------------from multiplier unit
			approvedParametricValue.setFromMultiplierUnit(new MultiplierUnit());
			if(approvedParametricDTO.getFromMultiplier() != null && !approvedParametricDTO.getFromMultiplier().equals(""))
			{
				approvedParametricValue.getFromMultiplierUnit().setMultiplier(ParaQueryUtil.getMultiplierByExactName(approvedParametricDTO.getFromMultiplier(), session));
				if(approvedParametricValue.getFromMultiplierUnit().getMultiplier() == null)
				{
					Multiplier multiplier = new Multiplier();
					multiplier.setId(System.nanoTime());
					multiplier.setStoreDate(new Date());
					multiplier.setName(approvedParametricDTO.getFromMultiplier());
					ParaQueryUtil.saveMultiplier(session, multiplier);
					approvedParametricValue.getFromMultiplierUnit().setMultiplier(multiplier);
				}
			}
			if(approvedParametricDTO.getFromUnit() != null && !approvedParametricDTO.getFromUnit().equals(""))
			{
				approvedParametricValue.getFromMultiplierUnit().setUnit(ParaQueryUtil.getUnitByExactName(approvedParametricDTO.getFromUnit(), session));
				if(approvedParametricValue.getFromMultiplierUnit().getUnit() == null)
				{
					Unit unit = new Unit();
					unit.setId(System.nanoTime());
					unit.setStoreDate(new Date());
					unit.setName(approvedParametricDTO.getFromUnit());
					unit.setSymbol(approvedParametricDTO.getFromUnit());
					ParaQueryUtil.saveUnit(unit, session);
					approvedParametricValue.getFromMultiplierUnit().setUnit(unit);
				}
			}
			approvedParametricValue.setFromMultiplierUnit(getMultiplierUnit(approvedParametricValue.getFromMultiplierUnit().getMultiplier(), approvedParametricValue.getFromMultiplierUnit().getUnit(), session));

			// -----------------------------TO
			if(approvedParametricDTO.getToValue() != null && !approvedParametricDTO.getToValue().equals(""))
			{
				approvedParametricValue.setToValue(ParaQueryUtil.getValueByExactValue(approvedParametricDTO.getToValue(), session));
				if(approvedParametricValue.getToValue() == null)
				{
					// add this value
					Value value = new Value();
					ParaQueryUtil.saveValue(session, approvedParametricDTO.getToValue(), value);
					approvedParametricValue.setToValue(value);
				}
				if(approvedParametricDTO.getToSign() != null && !approvedParametricDTO.getToSign().equals(""))
				{
					approvedParametricValue.setToSign(ParaQueryUtil.getSignByExactName(approvedParametricDTO.getToSign(), session));
					if(approvedParametricValue.getToSign() == null)
					{
						Sign sign = new Sign();
						sign.setId(System.nanoTime());
						sign.setStoreDate(new Date());
						sign.setName(approvedParametricDTO.getToSign());
						ParaQueryUtil.saveSign(sign, session);
						approvedParametricValue.setToSign(sign);
					}
				}
				if(approvedParametricDTO.getToCondition() != null && !approvedParametricDTO.getToCondition().equals(""))
				{
					approvedParametricValue.setToCondition(ParaQueryUtil.getConditionByExactName(approvedParametricDTO.getToCondition(), session));
					if(approvedParametricValue.getToCondition() == null)
					{
						Condition condition = new Condition();
						condition.setId(System.nanoTime());
						condition.setStoreDate(new Date());
						condition.setName(approvedParametricDTO.getToCondition());
						ParaQueryUtil.saveCondition(session, condition);
						approvedParametricValue.setToCondition(condition);
					}
				}
				if(approvedParametricDTO.getToValueType() != null && !approvedParametricDTO.getToValueType().equals(""))
				{
					approvedParametricValue.setToValueType(ParaQueryUtil.getValueTypeByExactName(approvedParametricDTO.getToValueType(), session));
					if(approvedParametricValue.getToValueType() == null)
					{
						ValueType valueType = new ValueType();
						valueType.setId(System.nanoTime());
						valueType.setStoreDate(new Date());
						valueType.setName(approvedParametricDTO.getToCondition());
						ParaQueryUtil.saveValueType(session, valueType);
						approvedParametricValue.setToValueType(valueType);
					}
				}
				// -----------------to multiplier unit
				approvedParametricValue.setToMultiplierUnit(new MultiplierUnit());
				if(approvedParametricDTO.getToMultiplier() != null && !approvedParametricDTO.getToMultiplier().equals(""))
				{
					approvedParametricValue.getToMultiplierUnit().setMultiplier(ParaQueryUtil.getMultiplierByExactName(approvedParametricDTO.getToMultiplier(), session));
					if(approvedParametricValue.getToMultiplierUnit().getMultiplier() == null)
					{
						Multiplier multiplier = new Multiplier();
						multiplier.setId(System.nanoTime());
						multiplier.setStoreDate(new Date());
						multiplier.setName(approvedParametricDTO.getToMultiplier());
						ParaQueryUtil.saveMultiplier(session, multiplier);
						approvedParametricValue.getToMultiplierUnit().setMultiplier(multiplier);
					}
				}
				if(approvedParametricDTO.getToUnit() != null && !approvedParametricDTO.getToUnit().equals(""))
				{
					approvedParametricValue.getToMultiplierUnit().setUnit(ParaQueryUtil.getUnitByExactName(approvedParametricDTO.getToUnit(), session));
					if(approvedParametricValue.getToMultiplierUnit().getUnit() == null)
					{
						Unit unit = new Unit();
						unit.setId(System.nanoTime());
						unit.setStoreDate(new Date());
						unit.setName(approvedParametricDTO.getToUnit());
						unit.setSymbol(approvedParametricDTO.getToUnit());
						ParaQueryUtil.saveUnit(unit, session);
						approvedParametricValue.getToMultiplierUnit().setUnit(unit);
					}
				}
				System.out.println("here");
				approvedParametricValue.setToMultiplierUnit(getMultiplierUnit(approvedParametricValue.getToMultiplierUnit().getMultiplier(), approvedParametricValue.getToMultiplierUnit().getUnit(), session));
			}

			// -------------------------------------------------------

			approvedParametricValue.setIsApproved(0L);
			if(engine == 1)
			{
				approvedParametricValue.setIsApproved(1L);
			}
			try
			{
				// session.clear();
				// ApprovedParametricValue ob = null;
				// ob = (ApprovedParametricValue) session.get(ApprovedParametricValue.class, approvedParametricValue.getId());
				// if(ob == null)
				// {
				// session.saveOrUpdate(approvedParametricValue);
				// }
				// else
				// {
				// // session.getTransaction().begin();
				// session.saveOrUpdate(ob);
				// if(!session.getTransaction().isInitiator())
				// session.beginTransaction();
				// session.getTransaction().commit();
				// // session.getTransaction().commit();
				// }

				session.saveOrUpdate(approvedParametricValue);
			}catch(ConstraintViolationException e)
			{
				System.out.println("Approved Value Found before:" + approvedParametricDTO.toString() + " ~~ " + featureName + " ~~ " + plName);
				plFeature = ParaQueryUtil.getPlFeatureByExactName(featureName, plName, session);
				Criteria criteria = session.createCriteria(ApprovedParametricValue.class);
				criteria.add(Restrictions.eq("plFeature", plFeature));
				criteria.add(Restrictions.eq("fullValue", approvedParametricDTO.toString()));
				approvedParametricValue = (ApprovedParametricValue) criteria.uniqueResult();
				ParaQueryUtil.addSeparationGroup(engine, update, approvedParametricDTO.getApprovedParametricValue(), approvedParametricDTO.getPattern(), parametricApprovedGroup, approvedValueOrder, session);
				return approvedParametricValue;
			}

			// save the group
			ParaQueryUtil.addSeparationGroup(engine, update, approvedParametricValue, approvedParametricDTO.getPattern(), parametricApprovedGroup, approvedValueOrder, session);
			return approvedParametricValue;
			// }
			// catch (ConstraintViolationException e) {
			// throw new Exception("Dublicate value ");
			// }catch(Exception e)
			// {
			// e.printStackTrace();
			// // throw getCatchException(e);
			// }
		}finally
		{
			session.close();
		}
		// return null;
	}

	public static MultiplierUnit getMultiplierUnit(Multiplier multiplier, Unit unit, Session session)
	{
		MultiplierUnit multiplierUnit = null;
		if(multiplier != null || unit != null)
		{
			multiplierUnit = ParaQueryUtil.getMultiplierUnitByExactMultiplierAndUnit(multiplier, unit, session);
			if(multiplierUnit == null)
			{
				multiplierUnit = new MultiplierUnit();
				multiplierUnit.setId(QueryUtil.getRandomID());
				if(multiplier != null)
					multiplierUnit.setMultiplier(multiplier);
				if(unit != null)
					multiplierUnit.setUnit(unit);
				multiplierUnit.setStoreDate(new Date());
				session.save(multiplierUnit);
			}
		}
		return multiplierUnit;
	}

	public static ArrayList<String[]> getSeparatedSections1(String text)
	{
		// Session session = SessionUtil.getSession();
		ArrayList<String[]> result = new ArrayList<String[]>();

		ArrayList<Integer> list = new ArrayList<Integer>();
		char charStick = '|';
		char charSlash = '!';
		String s = text.replace("|", " | ");
		if(s.contains("!!"))
		{
			s = s.replace("!!", " !! ");
			// charSlash = '!!';
		}
		else
		{
			s = s.replace("!", " ! ");
		}

		System.out.println(s);

		for(int i = 0; i < s.length(); i++)
		{
			if(s.charAt(i) == charStick || s.charAt(i) == charSlash)
			{
				list.add(i);
				System.out.println(i);
			}
		}
		String val = "";
		String[] valueSections = new String[list.size() + 1];
		String[] separators = new String[list.size() + 1];
		// val = s.substring(0, list.get(0));
		// System.out.println(val);
		if(list.isEmpty())
		{
			valueSections = new String[] { text };
			separators = new String[] { "" };
			result.add(valueSections);
			result.add(separators);
			return result;
		}
		valueSections[0] = s.substring(0, list.get(0)).trim();
		separators[0] = s.substring(list.get(0), list.get(0) + 1);
		for(int i = 1; i < list.size(); i++)
		{
			// val += "$" + s.substring(list.get(i - 1) + 1, list.get(i));
			valueSections[i] = s.substring(list.get(i - 1) + 1, list.get(i)).trim();
			separators[i] = s.substring(list.get(i), list.get(i) + 1);
			// System.out.println(val);
		}
		valueSections[list.size()] = s.substring(list.get(list.size() - 1) + 1);
		// separators[list.size()] = s.substring(list.get(list.size() - 1), list.get(list.size() - 1) + 1);
		separators[list.size()] = "";
		result.add(valueSections);
		result.add(separators);
		return result;

	}

	public static ArrayList<String[]> getSeparatedSections2(String text)
	{
		// Session session = SessionUtil.getSession();
		ArrayList<String[]> result = new ArrayList<String[]>();

		ArrayList<Integer> list = new ArrayList<Integer>();
		char charStick = '|';
		char charSlash = '!';
		char charTwoSlash = '}';
		String s = text.replace("|", " | ");
		s = s.replace("!!", " } ");
		s = s.replace("!", " ! ");
		// if(s.contains("!!")){
		// s = s.replace("!!", " !! ");
		// // charSlash = '!!';
		// }else{
		//
		// }

		System.out.println(s);

		for(int i = 0; i < s.length(); i++)
		{
			if(s.charAt(i) == charStick || s.charAt(i) == charSlash || s.charAt(i) == charTwoSlash)
			{
				list.add(i);
				System.out.println(i);
			}
		}
		String val = "";
		String[] valueSections = new String[list.size() + 1];
		String[] separators = new String[list.size() + 1];
		// val = s.substring(0, list.get(0));
		// System.out.println(val);
		if(list.isEmpty())
		{
			valueSections = new String[] { text };
			separators = new String[] { "" };
			result.add(valueSections);
			result.add(separators);
			return result;
		}
		valueSections[0] = s.substring(0, list.get(0)).trim();
		separators[0] = s.substring(list.get(0), list.get(0) + 1);
		for(int i = 1; i < list.size(); i++)
		{
			val = s.substring(list.get(i - 1) + 1, list.get(i));
			valueSections[i] = val.trim();
			separators[i] = s.substring(list.get(i), list.get(i) + 1);
			// System.out.println(val);
		}
		valueSections[list.size()] = s.substring(list.get(list.size() - 1) + 1);
		// separators[list.size()] = s.substring(list.get(list.size() - 1), list.get(list.size() - 1) + 1);
		separators[list.size()] = "";
		for(int i = 0; i < separators.length; i++)
		{
			separators[i] = separators[i].replaceAll("}", "!!");
		}
		result.add(valueSections);
		result.add(separators);
		return result;

	}

	public static ArrayList<String[]> getSeparatedSections(String text, boolean toFlag)
	{
		// Session session = SessionUtil.getSession();
		ArrayList<String[]> result = new ArrayList<String[]>();

		ArrayList<Integer> list = new ArrayList<Integer>();
		text = text.replaceAll("Up to ", "Upstos");
		char charStick = '|';
		char charSlash = '!';
		char charTwoSlash = '}';
		char charto = '~';
		String s = text.replace("|", " | ");
		s = s.replace("!!", " } ");
		s = s.replace("!", " ! ");
		if(toFlag)
			s = s.replace(" to ", " ~ ");
		// if(s.contains("!!")){
		// s = s.replace("!!", " !! ");
		// // charSlash = '!!';
		// }else{
		//
		// }

		System.out.println(s);

		for(int i = 0; i < s.length(); i++)
		{
			if(s.charAt(i) == charStick || s.charAt(i) == charSlash || s.charAt(i) == charTwoSlash || s.charAt(i) == charto)
			{
				list.add(i);
				System.out.println(i);
			}
		}
		String val = "";
		String[] valueSections = new String[list.size() + 1];
		String[] separators = new String[list.size() + 1];
		// val = s.substring(0, list.get(0));
		// System.out.println(val);
		if(list.isEmpty())
		{
			valueSections = new String[] { text };
			separators = new String[] { "" };
			result.add(valueSections);
			result.add(separators);
			return result;
		}
		valueSections[0] = s.substring(0, list.get(0)).trim();
		separators[0] = s.substring(list.get(0), list.get(0) + 1);
		for(int i = 1; i < list.size(); i++)
		{
			val = s.substring(list.get(i - 1) + 1, list.get(i));
			valueSections[i] = val.trim();
			separators[i] = s.substring(list.get(i), list.get(i) + 1);
			// System.out.println(val);
		}
		valueSections[list.size()] = s.substring(list.get(list.size() - 1) + 1);
		// separators[list.size()] = s.substring(list.get(list.size() - 1), list.get(list.size() - 1) + 1);
		separators[list.size()] = "";
		for(int i = 0; i < separators.length; i++)
		{
			separators[i] = separators[i].replaceAll("}", "!!");
			separators[i] = separators[i].replaceAll("~", " to ");
			valueSections[i] = valueSections[i].replaceAll("Upstos", "Up to ");
		}
		result.add(valueSections);
		result.add(separators);
		return result;

	}

	/**
	 * @author ahmad_Makram
	 * @param app
	 * @param fbType
	 *            Updated @ 9-9-2013 to be common for QA and parametric Team Leader
	 */
	public static void saveWrongSeparation(UnApprovedDTO app)
	{
		Session session = SessionUtil.getSession();
		Criteria criteria;
		try
		{

			ParametricApprovedGroup groups = ParaQueryUtil.getParametricApprovedGroup(app.getFeatureValue(), app.getPlName(), app.getFeatureName(), session);
			ParaFeedbackStatus paraFeedbackAction = null;
			ParaFeedbackStatus paraFeedbackStatus = null;
			ParaIssueType paraIssueType = null;
			TrackingFeedbackType trackingFeedbackType = null;
			ParametricFeedback FBObj = new ParametricFeedback();
			ParametricFeedbackCycle FBCyc = new ParametricFeedbackCycle();
			Document document = null;

			document = ParaQueryUtil.getDocumnetByPdfUrl(app.getPdfUrl());

			criteria = session.createCriteria(ParaIssueType.class);
			System.out.println(app.getIssueType());
			criteria.add(Restrictions.eq("issueType", app.getIssueType()));
			paraIssueType = (ParaIssueType) criteria.uniqueResult();

			criteria = session.createCriteria(ParaFeedbackStatus.class);
			System.out.println(app.getFbStatus());
			criteria.add(Restrictions.eq("feedbackStatus", app.getFbStatus()));
			paraFeedbackAction = (ParaFeedbackStatus) criteria.uniqueResult();//

			criteria = session.createCriteria(TrackingFeedbackType.class);
			System.out.println(app.getFbType());
			criteria.add(Restrictions.eq("name", app.getFbType()));
			trackingFeedbackType = (TrackingFeedbackType) criteria.uniqueResult();

			criteria = session.createCriteria(ParaFeedbackStatus.class);
			criteria.add(Restrictions.eq("feedbackStatus", "Open"));
			paraFeedbackStatus = (ParaFeedbackStatus) criteria.uniqueResult();//

			// criteria = session.createCriteria(ParametricFeedback.class);
			// criteria.add(Restrictions.eq("itemId", groups.getId()));
			// criteria.add(Restrictions.eq("type", "V"));
			// criteria.add(Restrictions.eq("paraFeedbackStatus", paraFeedbackStatus));
			// FBObj = (ParametricFeedback) criteria.uniqueResult();

			// if(FBObj == null)
			// {
			FBObj.setId(System.nanoTime());
			FBObj.setParaIssueType(paraIssueType);
			FBObj.setParaFeedbackStatus(paraFeedbackStatus);
			FBObj.setStoreDate(new Date());
			FBObj.setFbInitiator(app.getIssuedby());
			FBObj.setTrackingFeedbackType(trackingFeedbackType);
			FBObj.setItemId(groups.getId());
			FBObj.setType("V");
			// }

			FBCyc.setId(System.nanoTime());
			FBCyc.setParametricFeedback(FBObj);
			FBCyc.setFbItemValue(groups.getGroupFullValue());
			FBCyc.setFbComment(app.getComment());
			FBCyc.setIssuedBy(app.getIssuedby());
			if(app.getFbType().equals("Internal"))
			{
				FBCyc.setIssuedTo(app.getUserId());
			}
			else
			{
				FBCyc.setIssuedTo(ParaQueryUtil.getTLByUserID(app.getUserId()));
			}
			FBCyc.setStoreDate(new Date());
			FBCyc.setDocumentId(document.getId());
			FBCyc.setParaFeedbackStatus(paraFeedbackAction);
			FBCyc.setFeedbackRecieved(0l);
			session.saveOrUpdate(FBObj);
			session.saveOrUpdate(FBCyc);
			session.beginTransaction().commit();

			criteria = session.createCriteria(TrackingTaskStatus.class);
			criteria.add(Restrictions.eq("name", app.getGruopSatus()));
			TrackingTaskStatus trackingTaskStatus = (TrackingTaskStatus) criteria.uniqueResult();//
			groups.setStatus(trackingTaskStatus);
			session.saveOrUpdate(groups);

		}catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("here");
		}finally
		{
			session.close();
		}
	}

	public static void saveAppWrongValue(UnApprovedDTO app)
	{
		Session session = SessionUtil.getSession();
		Criteria criteria;
		try
		{

			ParametricApprovedGroup groups = ParaQueryUtil.getParametricApprovedGroup(app.getFeatureValue(), app.getPlName(), app.getFeatureName(), session);
			ParaFeedbackStatus paraFeedbackAction = null;
			ParaFeedbackStatus paraFeedbackStatus = null;
			ParaIssueType paraIssueType = null;
			TrackingFeedbackType trackingFeedbackType = null;
			ParametricFeedback FBObj = null;
			Document document = null;

			/** component has group value */
			criteria = session.createCriteria(ParametricReviewData.class);
			criteria.add(Restrictions.eq("groupApprovedValueId", groups.getId()));
			List wrongPartsList = criteria.list();
			/** reject status criteria */
			criteria = session.createCriteria(ParaFeedbackStatus.class);
			criteria.add(Restrictions.eq("name", app.getFbStatus()));
			paraFeedbackStatus = (ParaFeedbackStatus) criteria.uniqueResult();
			/** Wrong value FB Type criteria */
			criteria = session.createCriteria(ParaIssueType.class);
			criteria.add(Restrictions.eq("name", app.getIssueType()));
			paraIssueType = (ParaIssueType) criteria.uniqueResult();

			criteria = session.createCriteria(TrackingFeedbackType.class);
			criteria.add(Restrictions.eq("name", app.getFbType()));
			trackingFeedbackType = (TrackingFeedbackType) criteria.uniqueResult();

			Set<TrackingParametric> tracks = new HashSet<TrackingParametric>();
			for(int i = 0; i < wrongPartsList.size(); i++)
			{
				ParametricReviewData rd = (ParametricReviewData) wrongPartsList.get(i);
				if(rd.getTrackingParametric() != null)
					tracks.add(rd.getTrackingParametric());
				ParametricFeedbackCycle FBCyc = null;
				ParametricFeedbackCycle OldFBCyc = null;
				// FBObj = new ParametricFeedback();
				// if feedback posted already return
				Criteria fbCriteria = session.createCriteria(ParametricFeedbackCycle.class);
				fbCriteria.add(Restrictions.eq("itemId", rd.getComponent().getComId()));
				fbCriteria.add(Restrictions.eq("feedbackRecieved", 0l));
				fbCriteria.add(Restrictions.eq("issuedById", app.getIssuedby()));
				fbCriteria.add(Restrictions.eq("issuedToId", app.getUserId()));
				OldFBCyc = (ParametricFeedbackCycle) fbCriteria.uniqueResult();
				if(OldFBCyc != null)
				{
					FBObj = OldFBCyc.getParametricFeedback();
					OldFBCyc.setFeedbackRecieved(1l);
					FBCyc = new ParametricFeedbackCycle();
					FBCyc.setId(System.nanoTime());
					FBCyc.setParametricFeedback(FBObj);
					FBCyc.setFbItemValue(rd.getComponent().getPartNumber());
					FBCyc.setFbComment(app.getComment());
					FBCyc.setIssuedBy(app.getIssuedby());
					if(app.getFbType().equals("Internal"))
					{
						FBCyc.setIssuedTo(app.getUserId());
					}
					else
					{
						FBCyc.setIssuedTo(ParaQueryUtil.getTLByUserID(app.getUserId()));
					}
					FBCyc.setStoreDate(new Date());
					FBCyc.setDocumentId(document.getId());
					FBCyc.setParaFeedbackStatus(paraFeedbackAction);
					FBCyc.setFeedbackRecieved(0l);
					session.saveOrUpdate(OldFBCyc);
					session.saveOrUpdate(FBCyc);
					session.beginTransaction().commit();
				}
				else
				{

					FBObj.setId(System.nanoTime());
					FBObj.setParaIssueType(paraIssueType);
					FBObj.setParaFeedbackStatus(paraFeedbackStatus);
					FBObj.setStoreDate(new Date());
					FBObj.setFbInitiator(app.getIssuedby());
					FBObj.setTrackingFeedbackType(trackingFeedbackType);
					FBObj.setItemId(rd.getComponent().getComId());
					FBObj.setType("P");

					FBCyc.setId(System.nanoTime());
					FBCyc.setParametricFeedback(FBObj);
					FBCyc.setFbItemValue(groups.getGroupFullValue());
					FBCyc.setFbComment(app.getComment());
					FBCyc.setIssuedBy(app.getIssuedby());
					if(app.getFbType().equals("Internal"))
					{
						FBCyc.setIssuedTo(app.getUserId());
					}
					else
					{
						FBCyc.setIssuedTo(ParaQueryUtil.getTLByUserID(app.getUserId()));
					}
					FBCyc.setStoreDate(new Date());
					FBCyc.setDocumentId(document.getId());
					FBCyc.setParaFeedbackStatus(paraFeedbackAction);
					FBCyc.setFeedbackRecieved(0l);
					session.saveOrUpdate(FBObj);
					session.saveOrUpdate(FBCyc);
				}
			}
			// criteria = session.createCriteria(TrackingTaskStatus.class);
			// criteria.add(Restrictions.eq("name", app.getGruopSatus()));
			// TrackingTaskStatus trackingTaskStatus = (TrackingTaskStatus) criteria.uniqueResult();//

			criteria = session.createCriteria(TrackingTaskStatus.class);
			criteria.add(Restrictions.eq("name", app.getGruopSatus()));
			TrackingTaskStatus trackingParaStatus = (TrackingTaskStatus) criteria.uniqueResult();
			groups.setStatus(trackingParaStatus);
			session.saveOrUpdate(groups);
			for(TrackingParametric tp : tracks)
			{
				// session.beginTransaction().begin();
				tp.setTrackingTaskStatus(trackingParaStatus);
				session.saveOrUpdate(tp);
				// session.beginTransaction().commit();
			}

		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			session.close();
		}

	}

	public static ArrayList<Object[]> getUnapprovedReviewSelection(Long[] ids, Date startDate, Date endDate, String team)
	{
		Session session = SessionUtil.getSession();
		// Session grmSession = com.se.grm.db.SessionUtil.getSession();
		Criteria criteria;
		ArrayList<Object[]> result = new ArrayList<Object[]>();
		try
		{
			String userCol = "paraUserId";
			String status = "Pending TL Review";
			if(team.equals("QA"))
			{
				userCol = "qaUserId";
				status = "Pending QA Approval";
			}
			criteria = session.createCriteria(PartsParametricValuesGroup.class, "group");
			criteria.add(Restrictions.in(userCol, ids));
			criteria.createAlias("taskStatus", "taskStatus");
			criteria.add(Restrictions.in("taskStatus.name", new String[] { status }));
			if(startDate != null && endDate != null)
			{
				criteria.add(Expression.between("storeDate", startDate, endDate));
			}

			List list = criteria.list();
			GrmUserDTO eng = null;
			Object[] row = null;
			PartsParametricValuesGroup partsParametricValuesGroup = null;
			for(int i = 0; i < list.size(); i++)
			{
				partsParametricValuesGroup = (PartsParametricValuesGroup) list.get(i);
				// row = new Object[4];
				// eng = getUserByUserId(partsParametricValuesGroup.getParaUserId(), grmSession);
				// eng = getGRMUser(partsParametricValuesGroup.getQaUserId());
				// row[0] = eng.getFullName();
				row = new Object[5];
				// eng = getUserByUserId(partsParametricValuesGroup.getParaUserId(), grmSession);
				eng = ParaQueryUtil.getGRMUser(partsParametricValuesGroup.getParaUserId());
				row[0] = eng.getFullName();
				row[1] = partsParametricValuesGroup.getPlFeature().getPl().getName();
				if(partsParametricValuesGroup.getDocument() != null)
				{
					Set set = partsParametricValuesGroup.getDocument().getTrackingParametrics();
					if(set.size() == 0)
					{
						row[2] = "All";
						row[4] = "All";
					}
					else
					{
						Iterator it = set.iterator();
						TrackingParametric tp = (TrackingParametric) it.next();
						long statusId = tp.getTrackingTaskStatus().getId();
						if(statusId == 3 || statusId == 4)
						{
							row[2] = tp.getSupplier().getName();
							row[4] = tp.getTrackingTaskType().getName();
						}
						else
						{
							continue;
						}

					}

				}
				else
				{
					row[2] = "All";
					row[4] = "All";
				}
				row[3] = partsParametricValuesGroup.getTaskStatus().getName();

				result.add(row);

			}

		}catch(Exception ex)
		{
			ex.printStackTrace();
		}finally
		{

			session.close();
		}
		return result;
	}

	public static ArrayList<Object[]> getUnapprovedReviewFilter(Long[] ids, Date startDate, Date endDate, String team)
	{
		Session session = SessionUtil.getSession();
		// Session grmSession = com.se.grm.db.SessionUtil.getSession();
		Criteria criteria;
		ArrayList<Object[]> result = new ArrayList<Object[]>();
		try
		{
			String userCol = "paraUserId";
			String status = "Pending TL Review";
			if(team.equals("QA"))
			{
				userCol = "qaUserId";
				status = "Pending QA Approval";
			}
			criteria = session.createCriteria(ParametricApprovedGroup.class, "group");
			criteria.add(Restrictions.in(userCol, ids));
			criteria.createAlias("status", "status");
			criteria.add(Restrictions.in("status.name", new String[] { status }));
			if(startDate != null && endDate != null)
			{
				criteria.add(Expression.between("storeDate", startDate, endDate));
			}

			List list = criteria.list();
			GrmUserDTO eng = null;
			Object[] row = null;
			ParametricApprovedGroup parametricApprovedGroup = null;
			for(int i = 0; i < list.size(); i++)
			{
				parametricApprovedGroup = (ParametricApprovedGroup) list.get(i);
				// row = new Object[4];
				// eng = getUserByUserId(partsParametricValuesGroup.getParaUserId(), grmSession);
				// eng = getGRMUser(partsParametricValuesGroup.getQaUserId());
				// row[0] = eng.getFullName();
				row = new Object[5];
				// eng = getUserByUserId(partsParametricValuesGroup.getParaUserId(), grmSession);
				eng = ParaQueryUtil.getGRMUser(parametricApprovedGroup.getParaUserId());
				row[0] = eng.getFullName();
				row[1] = parametricApprovedGroup.getPlFeature().getPl().getName();
				if(parametricApprovedGroup.getDocument() != null)
				{
					Set set = parametricApprovedGroup.getDocument().getTrackingParametrics();
					if(set.size() == 0)
					{
						row[2] = "All";
						row[4] = "All";
					}
					else
					{
						Iterator it = set.iterator();
						TrackingParametric tp = (TrackingParametric) it.next();
						long statusId = tp.getTrackingTaskStatus().getId();
						row[2] = tp.getSupplier().getName();
						row[4] = tp.getTrackingTaskType().getName();
					}

				}
				else
				{
					row[2] = "All";
					row[4] = "All";
				}
				row[3] = parametricApprovedGroup.getStatus().getName();

				result.add(row);

			}

		}catch(Exception ex)
		{
			ex.printStackTrace();
		}finally
		{

			session.close();
		}
		return result;
	}

	public static FeedBackData getFeedbackData(long issuedTo, ParametricApprovedGroup groupRecord, String taskType, Session session)
	{
		FeedBackData result = new FeedBackData();
		Criteria feedBackCrit = session.createCriteria(ParametricFeedbackCycle.class);
		feedBackCrit.add(Restrictions.eq("issuedTo", issuedTo));
		feedBackCrit.add(Restrictions.eq("feedbackRecieved", 0l));
		feedBackCrit.add(Restrictions.eq("fbItemValue", groupRecord.getGroupFullValue()));
		ParametricFeedbackCycle appFeedback = (ParametricFeedbackCycle) feedBackCrit.uniqueResult();

		result.setComment((appFeedback == null) ? "" : appFeedback.getFbComment());
		result.setFbStatus((appFeedback == null) ? "" : appFeedback.getParaFeedbackStatus().getFeedbackStatus());
		result.setFbType((appFeedback == null) ? "" : appFeedback.getParametricFeedback().getTrackingFeedbackType().getName());
		result.setIssuedby(appFeedback.getIssuedBy());
		result.setIssueTo(appFeedback.getIssuedTo());

		if(taskType != null & !taskType.equals("All"))
		{
			if(!result.getFbStatus().equals(taskType))
				return null;
		}
		/** get Last comment **/
		if(appFeedback.getParametricFeedback().getTrackingFeedbackType().getName().equals("QA"))
		{
			Long qaUserId = ParaQueryUtil.getQAUserId(groupRecord.getPlFeature().getPl(), ParaQueryUtil.getTrackingTaskTypeByName("Approved Values", session));
			feedBackCrit = session.createCriteria(ParametricFeedbackCycle.class);
			feedBackCrit.add(Restrictions.eq("issuedBy", qaUserId));
			// feedBackCrit.add(Restrictions.eq("feedbackRecieved", 0l));
			feedBackCrit.add(Restrictions.eq("fbItemValue", groupRecord.getGroupFullValue()));
			feedBackCrit.addOrder(Order.desc("storeDate"));
			if(feedBackCrit.list() != null && !feedBackCrit.list().isEmpty())
				appFeedback = (ParametricFeedbackCycle) feedBackCrit.list().get(0);

			result.setQaComment((appFeedback == null) ? "" : appFeedback.getFbComment());
			result.setQaStatus((appFeedback == null) ? "" : appFeedback.getParaFeedbackStatus().getFeedbackStatus());
			if(result.getIssuedby() == qaUserId)
			{
				result.setComment("");
				result.setFbStatus("");
			}
		}
		else
		{
			// Long qaUserId = ParaQueryUtil.getQAUserId(groupRecord.getPlFeature().getPl(),
			// ParaQueryUtil.getTrackingTaskTypeByName("Approved Values", session));
			feedBackCrit = session.createCriteria(ParametricFeedbackCycle.class);
			feedBackCrit.add(Restrictions.eq("issuedBy", issuedTo));
			// feedBackCrit.add(Restrictions.eq("feedbackRecieved", 0l));
			feedBackCrit.add(Restrictions.eq("fbItemValue", groupRecord.getGroupFullValue()));
			feedBackCrit.addOrder(Order.desc("storeDate"));
			if(feedBackCrit.list() != null && !feedBackCrit.list().isEmpty())
				appFeedback = (ParametricFeedbackCycle) feedBackCrit.list().get(0);

			result.setLastEngComment(appFeedback.getFbComment());
			// result.setQaComment((appFeedback == null) ? "" : appFeedback.getFbComment());
			// result.setQaStatus((appFeedback == null) ? "" : appFeedback.getParaFeedbackStatus().getFeedbackStatus());
			// if(result.getIssuedby() == issuedTo)
			// {
			// result.setComment("");
			// result.setFbStatus("");
			// }
		}
		return result;
	}

	public static ArrayList<Object[]> getEngUnapprovedData(GrmUserDTO userDto, Date startDate, Date endDate, String type)
	{
		Session session = SessionUtil.getSession();
		// Session grmSession = com.se.grm.db.SessionUtil.getSession();
		Criteria criteria;
		ArrayList<Object[]> result = new ArrayList<Object[]>();
		try
		{
			criteria = session.createCriteria(ParametricFeedbackCycle.class);
			criteria.add(Restrictions.eq("issuedTo", userDto.getId()));
			criteria.add(Restrictions.eq("feedbackRecieved", 0l));
			List list = criteria.list();
			GrmUser eng = null;
			Object[] row = null;
			ParametricApprovedGroup list2 = null;
			ParametricFeedbackCycle parametricFeedbackCycle = null;
			ParametricApprovedGroup parametricApprovedGroup = null;
			for(int i = 0; i < list.size(); i++)
			{

				parametricFeedbackCycle = (ParametricFeedbackCycle) list.get(i);
				criteria = session.createCriteria(ParametricApprovedGroup.class);
				criteria.add(Restrictions.eq("groupFullValue", parametricFeedbackCycle.getFbItemValue()));
				if(startDate != null && endDate != null)
				{
					criteria.add(Expression.between("storeDate", startDate, endDate));
				}
				list2 = (ParametricApprovedGroup) criteria.uniqueResult();
				if(type.equals("Eng"))
				{
					row = new Object[3];
					if(list2 != null)
					{
						parametricApprovedGroup = list2;
						row[0] = parametricApprovedGroup.getPlFeature().getPl().getName();

						if(parametricApprovedGroup.getDocument() != null)
						{
							Set set = parametricApprovedGroup.getDocument().getTrackingParametrics();
							Iterator it = set.iterator();
							TrackingParametric tp = (TrackingParametric) it.next();
							row[1] = tp.getSupplier().getName();
						}
						else
						{
							row[1] = "All";
						}
						row[2] = parametricFeedbackCycle.getParaFeedbackStatus().getFeedbackStatus();
						result.add(row);
					}
				}
				else
				{
					row = new Object[4];
					if(list2 != null)
					{
						parametricApprovedGroup = list2;
						row[0] = parametricApprovedGroup.getPlFeature().getPl().getName();
						if(parametricApprovedGroup.getDocument() != null)
						{
							Set set = parametricApprovedGroup.getDocument().getTrackingParametrics();
							Iterator it = set.iterator();
							TrackingParametric tp = (TrackingParametric) it.next();
							row[1] = tp.getSupplier().getName();
						}
						else
						{
							row[1] = "All";
						}

						row[2] = parametricFeedbackCycle.getParaFeedbackStatus().getFeedbackStatus();
						// row[2] = tp.getTrackingTaskType().getName();
						row[3] = parametricFeedbackCycle.getParametricFeedback().getTrackingFeedbackType().getName();
						result.add(row);
					}
				}

			}

		}finally
		{

			session.close();
		}
		return result;
	}

	public static ArrayList<UnApprovedDTO> getTLUnapprovedFeedBack(GrmUserDTO userDTO, Date startDate, Date endDate, String plName, String supplierName, String taskType, String feedBackType)
	{
		ArrayList<UnApprovedDTO> result = new ArrayList<UnApprovedDTO>();
		Session session = SessionUtil.getSession();
		List rdList = null;
		List<Object> list = null;
		List groups = null;
		ParametricReviewData rd = null;
		UnApprovedDTO unApprovedDTO = null;
		PartsParametricValuesGroup group = null;
		Criteria plCriteria = null;
		Criteria feedBackCrit = null;
		Criteria feedBackTypecriCriteria = null;
		List approvedValueFeedbacks = null;
		List<Object> groupIds = new ArrayList<Object>();
		ApprovedValueFeedback approvedValueFeedback = null;
		Long[] teamMembers = null;
		try
		{
			// feedBack = session.createCriteria(ApprovedValueFeedback.class);
			// feedBack.add(Restrictions.eq("issuedToId", userDTO.getId()));
			// feedBack.add(Restrictions.eq("feedbackRecieved", 0l));
			// approvedValueFeedbacks = feedBack.list();
			// for (int i = 0; i < approvedValueFeedbacks.size(); i++) {
			// approvedValueFeedback = (ApprovedValueFeedback) approvedValueFeedbacks.get(i);
			// System.out.println(approvedValueFeedback.getGroupID());
			// groupIds.add(approvedValueFeedback.getGroupID());
			// }
			teamMembers = ParaQueryUtil.getTeamMembersIDByTL(userDTO.getId());
			Criteria criteria = session.createCriteria(PartsParametricValuesGroup.class, "group");
			criteria.add(Restrictions.in("paraUserId", teamMembers));
			// criteria.add(Restrictions.in("paraUserId", teamMembers));
			// criteria.add(Restrictions.in("groupId", groupIds));
			criteria.createAlias("taskStatus", "status");
			criteria.add(Restrictions.eq("status.name", "Send Back To Team Leader"));

			if(startDate != null && endDate != null)
			{
				criteria.add(Expression.between("storeDate", startDate, endDate));
			}
			// if (taskType != null & !taskType.equals("All")) {
			// Criteria statusCriteria = session.createCriteria(TrackingTaskStatus.class);
			// statusCriteria.add(Restrictions.eq("name", taskType));
			// TrackingTaskStatus statusObj = (TrackingTaskStatus) statusCriteria.uniqueResult();
			// criteria.add(Restrictions.eq("taskStatus", statusObj));
			// }
			if(plName != null && !plName.equals("All"))
			{
				criteria.createAlias("group.plFeature.pl", "pl");
				criteria.add(Restrictions.eq("pl.name", plName));
			}
			if(!supplierName.equals("All") && supplierName != null)
			{
				Criteria trackingcriteria = session.createCriteria(TrackingParametric.class, "track");
				trackingcriteria.createAlias("track.supplier", "supplier");
				trackingcriteria.add(Restrictions.eq("supplier.name", supplierName));
				trackingcriteria.setProjection(Projections.distinct(Projections.property("document")));
				// List<Document> documentList = trackingcriteria.list();
				Set set = new HashSet(trackingcriteria.list());
				criteria.add(Restrictions.in("document", set));
			}
			if(!feedBackType.equals("All") && feedBackType != null)
			{
				feedBackTypecriCriteria = session.createCriteria(TrackingFeedbackType.class);
				feedBackTypecriCriteria.add(Restrictions.eq("name", feedBackType));
				TrackingFeedbackType trackingFeedbackType = (TrackingFeedbackType) feedBackTypecriCriteria.uniqueResult();
				criteria.add(Restrictions.eq("trackingFeedbackType", trackingFeedbackType));
			}

			// Long qaUserId = getQAUserId(plFeature.getPl(), getTrackingTaskTypeByName("Approved Values", session));
			criteria.addOrder(Order.desc("groupFullValue"));
			criteria.addOrder(Order.asc("approvedValueOrder"));
			groups = criteria.list();
			ArrayList<ArrayList<PartsParametricValuesGroup>> re = new ArrayList<ArrayList<PartsParametricValuesGroup>>();
			ArrayList<PartsParametricValuesGroup> row = null;
			int count = 0;
			group = (PartsParametricValuesGroup) groups.get(0);
			String fullValue = group.getGroupFullValue();
			row = new ArrayList<PartsParametricValuesGroup>();
			while(count < groups.size())
			{
				if(((PartsParametricValuesGroup) groups.get(count)).getGroupFullValue().equals(fullValue))
				{
					row.add(((PartsParametricValuesGroup) groups.get(count)));
					count++;
				}
				else
				{
					re.add(row);
					row = new ArrayList<PartsParametricValuesGroup>();
					fullValue = ((PartsParametricValuesGroup) groups.get(count)).getGroupFullValue();
				}
			}
			re.add(row);
			System.out.println("size is " + re.size());
			ArrayList<PartsParametricValuesGroup> values = null;
			for(int i = 0; i < re.size(); i++)
			{
				values = re.get(i);
				unApprovedDTO = new UnApprovedDTO();
				PartsParametricValuesGroup groupRecord = null;
				ApprovedParametricValue approvedValue = null;
				String fetValue = "";
				String signValue = "";
				String multiplierValue = "";
				String typeValue = "";
				String conditionValue = "";
				String unitValue = "";
				String pattern = "";
				groupRecord = values.get(0);
				approvedValue = groupRecord.getApprovedParametricValue();
				feedBackCrit = session.createCriteria(ApprovedValueFeedback.class);
				feedBackCrit.add(Restrictions.eq("issuedToId", userDTO.getId()));
				feedBackCrit.add(Restrictions.eq("feedbackRecieved", 0l));
				feedBackCrit.add(Restrictions.eq("groupID", groupRecord.getGroupId()));
				ApprovedValueFeedback appFeedback = (ApprovedValueFeedback) feedBackCrit.uniqueResult();

				unApprovedDTO.setComment((appFeedback == null) ? "" : appFeedback.getFbComment());
				unApprovedDTO.setFbStatus((appFeedback == null) ? "" : appFeedback.getTrackingTaskStatus().getName());
				unApprovedDTO.setFbType((appFeedback == null) ? "" : appFeedback.getTrackingFeedbackType().getName());

				if(taskType != null & !taskType.equals("All"))
				{
					if(!unApprovedDTO.getFbStatus().equals(taskType))
						continue;
				}
				for(int j = 0; j < values.size(); j++)
				{
					groupRecord = values.get(j);
					approvedValue = groupRecord.getApprovedParametricValue();
					fetValue += approvedValue.getFromValue().getValue();
					signValue += (groupRecord.getApprovedParametricValue().getFromSign() == null) ? "" : groupRecord.getApprovedParametricValue().getFromSign().getName();
					typeValue += (groupRecord.getApprovedParametricValue().getFromValueType() == null) ? "" : groupRecord.getApprovedParametricValue().getFromValueType().getName();
					conditionValue += (groupRecord.getApprovedParametricValue().getFromCondition() == null) ? "" : groupRecord.getApprovedParametricValue().getFromCondition().getName();

					if(approvedValue.getFromMultiplierUnit() != null)
					{
						multiplierValue += (groupRecord.getApprovedParametricValue().getFromMultiplierUnit().getMultiplier() == null) ? "" : groupRecord.getApprovedParametricValue().getFromMultiplierUnit().getMultiplier().getName();
						unitValue += (groupRecord.getApprovedParametricValue().getFromMultiplierUnit().getUnit() == null) ? "" : groupRecord.getApprovedParametricValue().getFromMultiplierUnit().getUnit().getName();
					}

					if(approvedValue.getToValue() != null)
					{

						fetValue += " to " + approvedValue.getToValue().getValue();
						if(approvedValue.getFromSign() != null)
							signValue += (approvedValue.getToSign() == null) ? " to " : " to " + approvedValue.getToSign().getName();
						if(approvedValue.getFromValueType() != null)
							typeValue += (approvedValue.getToValueType() == null) ? " to " : " to " + approvedValue.getToValueType().getName();
						if(approvedValue.getFromCondition() != null)
							conditionValue += (approvedValue.getToCondition() == null) ? " to " : " to " + approvedValue.getToCondition().getName();
						if(approvedValue.getToMultiplierUnit() != null)
						{
							if(approvedValue.getFromMultiplierUnit().getMultiplier() != null)
								multiplierValue += (approvedValue.getToMultiplierUnit().getMultiplier() == null) ? " to " : " to " + approvedValue.getToMultiplierUnit().getMultiplier().getName();
							if(approvedValue.getFromMultiplierUnit().getUnit() != null)
								unitValue += (approvedValue.getToMultiplierUnit().getUnit() == null) ? " to " : " to " + approvedValue.getToMultiplierUnit().getUnit().getName();
						}
					}

					pattern = (groupRecord.getPattern() == null) ? "" : groupRecord.getPattern();
					String patterns[] = pattern.split("\\$");
					if(patterns.length == 6)
					{
						fetValue += patterns[0].trim();
						signValue += patterns[1].trim();
						conditionValue += patterns[2].trim();
						typeValue += patterns[3].trim();
						multiplierValue += patterns[4].trim();
						unitValue += patterns[5].trim();
					}

				}
				rdList = ParaQueryUtil.getParametricReviewData(groupRecord.getGroupId(), session);
				if(!rdList.isEmpty())
				{
					rd = (ParametricReviewData) rdList.get(0);
					unApprovedDTO.setPartNumber(rd.getComponent().getPartNumber());
					unApprovedDTO.setPdfUrl(rd.getComponent().getDocument().getPdf().getSeUrl());
				}
				else
				{
					unApprovedDTO.setPartNumber("");
					unApprovedDTO.setPdfUrl("");
				}
				String featureUnit = (groupRecord.getPlFeature().getUnit() == null) ? "" : groupRecord.getPlFeature().getUnit().getName();
				unApprovedDTO.setFeatureUnit(featureUnit);
				String pl = (groupRecord.getPlFeature().getPl().getName() == null) ? "" : groupRecord.getPlFeature().getPl().getName();
				unApprovedDTO.setPlName(pl);
				String featureValue = (groupRecord.getGroupFullValue() == null) ? "" : groupRecord.getGroupFullValue();
				unApprovedDTO.setFeatureValue(featureValue);
				String featureName = (groupRecord.getPlFeature().getFeature().getName() == null) ? "" : groupRecord.getPlFeature().getFeature().getName();
				unApprovedDTO.setFeatureName(featureName);
				String fromSign = (groupRecord.getApprovedParametricValue().getFromSign() == null) ? "" : groupRecord.getApprovedParametricValue().getFromSign().getName();

				if(!fetValue.replace("[|/!]", "").trim().equals(""))
				{
					unApprovedDTO.setValue(fetValue);
				}
				else
				{
					unApprovedDTO.setValue("");
				}
				if(!signValue.replace("|", "").replace("/", "").replace("!", "").trim().equals(""))
				{
					unApprovedDTO.setSign(signValue);
				}
				else
				{
					unApprovedDTO.setSign("");
				}
				if(!multiplierValue.replace("|", "").replace("/", "").replace("!", "").trim().equals(""))
				{
					unApprovedDTO.setMultiplier(multiplierValue);
				}
				else
				{
					unApprovedDTO.setMultiplier("");
				}
				if(!typeValue.replace("|", "").replace("/", "").replace("!", "").trim().equals(""))
				{
					unApprovedDTO.setType(typeValue);
				}
				else
				{
					unApprovedDTO.setType("");
				}
				if(!conditionValue.replace("|", "").replace("/", "").replace("!", "").trim().equals(""))
				{
					unApprovedDTO.setCondition(conditionValue);
				}
				else
				{
					unApprovedDTO.setCondition("");
				}
				if(!unitValue.replace("|", "").replace("/", "").replace("!", "").trim().equals(""))
				{
					unApprovedDTO.setUnit(unitValue);
				}
				else
				{
					unApprovedDTO.setUnit("");
				}
				unApprovedDTO.setUserId(groupRecord.getParaUserId());
				unApprovedDTO.setQaUserId(groupRecord.getQaUserId());
				result.add(unApprovedDTO);

			}
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}finally
		{
			session.close();
		}
		return result;
	}

	public static void replyApprovedValueFB(UnApprovedDTO app)
	{
		Session session = SessionUtil.getSession();
		Criteria criteria;
		try
		{

			List<ParametricApprovedGroup> groups = ParaQueryUtil.getAppGroupListByFullValAndFetNameAndPlName(app.getFeatureName(), app.getPlName(), app.getFeatureValue(), session);
			criteria = session.createCriteria(ApprovedValueFeedback.class);
			criteria.add(Restrictions.eq("groupID", groups.get(0).getId()));
			criteria.add(Restrictions.eq("issuedToId", app.getIssuedby()));
			criteria.add(Restrictions.eq("feedbackRecieved", 0l));

			ApprovedValueFeedback approvedValueFeedback = (ApprovedValueFeedback) criteria.uniqueResult();
			approvedValueFeedback.setFeedbackRecieved(1l);
			session.saveOrUpdate(approvedValueFeedback);
			session.beginTransaction().commit();

			criteria = session.createCriteria(TrackingTaskQaStatus.class);
			criteria.add(Restrictions.eq("name", app.getFbStatus()));
			TrackingTaskQaStatus status = (TrackingTaskQaStatus) criteria.uniqueResult();
			long fbRecieved = 0l;
			if(app.getFbStatus().equals("Feedback Closed"))
				fbRecieved = 1l;
			// session.beginTransaction().begin();
			criteria = session.createCriteria(TrackingFeedbackType.class);
			criteria.add(Restrictions.eq("name", app.getFbType()));
			TrackingFeedbackType trackingFeedbackType = (TrackingFeedbackType) criteria.uniqueResult();
			approvedValueFeedback = new ApprovedValueFeedback();
			approvedValueFeedback.setTrackingFeedbackType(trackingFeedbackType);
			approvedValueFeedback.setTrackingTaskStatus(status);
			approvedValueFeedback.setFullValue(app.getFeatureValue());
			approvedValueFeedback.setId(System.nanoTime());
			approvedValueFeedback.setIssuedBy(app.getIssuedby());
			approvedValueFeedback.setIssuedToId(app.getIssueTo());
			approvedValueFeedback.setFeedbackRecieved(fbRecieved);
			approvedValueFeedback.setStoreDate(new Date());
			approvedValueFeedback.setFbComment(app.getComment());
			approvedValueFeedback.setGroupID(groups.get(0).getId());
			session.saveOrUpdate(approvedValueFeedback);
			// session.beginTransaction().commit();

			ParametricApprovedGroup groupObj = null;
			criteria = session.createCriteria(TrackingTaskStatus.class);
			criteria.add(Restrictions.eq("name", app.getGruopSatus()));
			TrackingTaskStatus trackingTaskStatus = (TrackingTaskStatus) criteria.uniqueResult();//
			for(int i = 0; i < groups.size(); i++)
			{
				// session.beginTransaction().begin();
				groupObj = groups.get(i);
				groupObj.setStatus(trackingTaskStatus);
				session.saveOrUpdate(groupObj);
				// session.beginTransaction().commit();
			}

		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			session.close();
		}
	}

	public static ArrayList<UnApprovedDTO> getQAUnapprovedData(Date startDate, Date endDate, long userId, String plName, String supplierName, String status, String type)
	{
		ArrayList<UnApprovedDTO> result = new ArrayList<UnApprovedDTO>();
		Session session = SessionUtil.getSession();
		List rdList = null;
		List<Object> list = null;
		List groups = null;
		ParametricReviewData rd = null;
		UnApprovedDTO unApprovedDTO = null;
		PartsParametricValuesGroup group = null;

		try
		{
			Criteria criteria = session.createCriteria(PartsParametricValuesGroup.class, "group");

			Session session2 = com.se.grm.db.SessionUtil.getSession();
			criteria.add(Restrictions.eq("qaUserId", userId));

			if(startDate != null && endDate != null)
			{
				criteria.add(Expression.between("storeDate", startDate, endDate));
			}
			if(status != null & !status.equals("All"))
			{
				Criteria statusCriteria = session.createCriteria(TrackingTaskStatus.class);
				statusCriteria.add(Restrictions.eq("name", status));
				TrackingTaskStatus statusObj = (TrackingTaskStatus) statusCriteria.uniqueResult();
				criteria.add(Restrictions.eq("taskStatus", statusObj));
			}
			else if(status.equals("All"))
			{
				Criteria statusCriteria = session.createCriteria(TrackingTaskStatus.class);
				statusCriteria.add(Restrictions.eq("name", "Pending QA Approval"));
				TrackingTaskStatus statusObj = (TrackingTaskStatus) statusCriteria.uniqueResult();
				criteria.add(Restrictions.eq("taskStatus", statusObj));
			}

			if(!type.equals("All") && type != null)
			{
				Set<Document> docsSet = new HashSet<Document>();
				Criteria tarkCrit = session.createCriteria(TrackingParametric.class, "track");
				tarkCrit.createAlias("trackingTaskType", "typ");
				tarkCrit.createAlias("trackingTaskStatus", "status");
				tarkCrit.add(Restrictions.or(Restrictions.eq("status.name", "Pending TL Review"), Restrictions.eq("status.name", "Pending QA Approval")));
				if(type.equals("NPI"))
				{
					tarkCrit.add(Restrictions.or(Restrictions.eq("typ.name", "NPI"), Restrictions.eq("typ.name", "NPI Transferred"), Restrictions.eq("typ.name", "NPI Update")));
				}
				else
				{
					tarkCrit.add(Restrictions.eq("typ.name", type));
				}
				tarkCrit.setProjection(Projections.property("document"));
				List<Document> docs = tarkCrit.list();
				for(int i = 0; i < docs.size(); i++)
				{
					System.out.println(docs.get(i).getId());
				}
				docsSet = new HashSet<Document>(docs);
				criteria.add(Restrictions.in("document", docs));
			}
			if(plName != null && !plName.equals("All"))
			{
				criteria.createAlias("group.plFeature.pl", "pl");
				criteria.add(Restrictions.eq("pl.name", plName));
			}

			if(!supplierName.equals("All") && supplierName != null)
			{

				// criteria.createAlias("group.document.pdf.supplierUrl.supplier", "sup");
				// criteria.add(Restrictions.eq("sup.name", supplierName));
				Criteria trackingcriteria = session.createCriteria(TrackingParametric.class, "track");
				trackingcriteria.createAlias("trackingTaskStatus", "status");
				trackingcriteria.add(Restrictions.or(Restrictions.eq("status.name", "Pending TL Review"), Restrictions.eq("status.name", "Pending QA Approval"), Restrictions.eq("status.name", "Finished")));
				trackingcriteria.createAlias("track.supplier", "supplier");
				trackingcriteria.add(Restrictions.eq("supplier.name", supplierName));
				// // ///////////////////////////////////////////////////////////////////////////////////////
				// // ////May cause an issue if distinct docs for this supplier in tracking parametric exceeds 1000
				trackingcriteria.setProjection(Projections.distinct(Projections.property("document")));
				// List<Document> documentList = trackingcriteria.list();
				Set set = new HashSet(trackingcriteria.list());
				criteria.add(Restrictions.in("document", set));
			}
			criteria.addOrder(Order.asc("plFeature"));
			criteria.addOrder(Order.desc("groupFullValue"));
			criteria.addOrder(Order.asc("approvedValueOrder"));
			groups = criteria.list();
			ArrayList<ArrayList<PartsParametricValuesGroup>> re = new ArrayList<ArrayList<PartsParametricValuesGroup>>();
			ArrayList<PartsParametricValuesGroup> row = null;
			int count = 0;
			group = (PartsParametricValuesGroup) groups.get(0);
			String fullValue = group.getGroupFullValue();
			PlFeature plFet = group.getPlFeature();
			row = new ArrayList<PartsParametricValuesGroup>();
			while(count < groups.size())
			{
				group = ((PartsParametricValuesGroup) groups.get(count));
				if(group.getGroupFullValue().equals(fullValue) && group.getPlFeature() == plFet)
				{
					row.add(group);
					count++;
				}
				else
				{
					re.add(row);
					row = new ArrayList<PartsParametricValuesGroup>();
					fullValue = group.getGroupFullValue();
					plFet = group.getPlFeature();
				}
			}
			re.add(row);
			System.out.println("size is " + re.size());
			ArrayList<PartsParametricValuesGroup> values = null;
			for(int i = 0; i < re.size(); i++)
			{
				values = re.get(i);
				unApprovedDTO = new UnApprovedDTO();
				PartsParametricValuesGroup groupRecord = null;
				ApprovedParametricValue approvedValue = null;
				String fetValue = "";
				String signValue = "";
				String multiplierValue = "";
				String typeValue = "";
				String conditionValue = "";
				String unitValue = "";
				String pattern = "";
				groupRecord = values.get(0);
				approvedValue = groupRecord.getApprovedParametricValue();
				for(int j = 0; j < values.size(); j++)
				{
					groupRecord = values.get(j);
					approvedValue = groupRecord.getApprovedParametricValue();
					fetValue += approvedValue.getFromValue().getValue();
					signValue += (groupRecord.getApprovedParametricValue().getFromSign() == null) ? "" : groupRecord.getApprovedParametricValue().getFromSign().getName();
					typeValue += (groupRecord.getApprovedParametricValue().getFromValueType() == null) ? "" : groupRecord.getApprovedParametricValue().getFromValueType().getName();
					conditionValue += (groupRecord.getApprovedParametricValue().getFromCondition() == null) ? "" : groupRecord.getApprovedParametricValue().getFromCondition().getName();

					if(approvedValue.getFromMultiplierUnit() != null)
					{
						multiplierValue += (groupRecord.getApprovedParametricValue().getFromMultiplierUnit().getMultiplier() == null) ? "" : groupRecord.getApprovedParametricValue().getFromMultiplierUnit().getMultiplier().getName();
						unitValue += (groupRecord.getApprovedParametricValue().getFromMultiplierUnit().getUnit() == null) ? "" : groupRecord.getApprovedParametricValue().getFromMultiplierUnit().getUnit().getName();
					}
					if(approvedValue.getToValue() != null)
					{

						fetValue += " to " + approvedValue.getToValue().getValue();
						if(approvedValue.getFromSign() != null)
							signValue += (approvedValue.getToSign() == null) ? " to " : " to " + approvedValue.getToSign().getName();
						if(approvedValue.getFromValueType() != null)
							typeValue += (approvedValue.getToValueType() == null) ? " to " : " to " + approvedValue.getToValueType().getName();
						if(approvedValue.getFromCondition() != null)
							conditionValue += (approvedValue.getToCondition() == null) ? " to " : " to " + approvedValue.getToCondition().getName();
						if(approvedValue.getToMultiplierUnit() != null)
						{
							if(approvedValue.getFromMultiplierUnit().getMultiplier() != null)
								multiplierValue += (approvedValue.getToMultiplierUnit().getMultiplier() == null) ? " to " : " to " + approvedValue.getToMultiplierUnit().getMultiplier().getName();
							if(approvedValue.getFromMultiplierUnit().getUnit() != null)
								unitValue += (approvedValue.getToMultiplierUnit().getUnit() == null) ? " to " : " to " + approvedValue.getToMultiplierUnit().getUnit().getName();
						}
					}

					pattern = (groupRecord.getPattern() == null) ? "" : groupRecord.getPattern();
					String patterns[] = pattern.split("\\$");
					if(patterns.length == 6)
					{
						fetValue += patterns[0].trim();
						signValue += patterns[1].trim();
						conditionValue += patterns[2].trim();
						typeValue += patterns[3].trim();
						multiplierValue += patterns[4].trim();
						unitValue += patterns[5].trim();
					}

				}
				rdList = ParaQueryUtil.getParametricReviewData(groupRecord.getGroupId(), session);
				if(!rdList.isEmpty())
				{
					rd = (ParametricReviewData) rdList.get(0);
					unApprovedDTO.setPartNumber(rd.getComponent().getPartNumber());
					unApprovedDTO.setPdfUrl(rd.getComponent().getDocument().getPdf().getSeUrl());
				}
				else
				{
					unApprovedDTO.setPartNumber("");
					unApprovedDTO.setPdfUrl("");
				}
				String featureUnit = (groupRecord.getPlFeature().getUnit() == null) ? "" : groupRecord.getPlFeature().getUnit().getName();
				unApprovedDTO.setFeatureUnit(featureUnit);
				String pl = (groupRecord.getPlFeature().getPl().getName() == null) ? "" : groupRecord.getPlFeature().getPl().getName();
				unApprovedDTO.setPlName(pl);
				String featureValue = (groupRecord.getGroupFullValue() == null) ? "" : groupRecord.getGroupFullValue();
				unApprovedDTO.setFeatureValue(featureValue);
				String featureName = (groupRecord.getPlFeature().getFeature().getName() == null) ? "" : groupRecord.getPlFeature().getFeature().getName();
				unApprovedDTO.setFeatureName(featureName);
				String fromSign = (groupRecord.getApprovedParametricValue().getFromSign() == null) ? "" : groupRecord.getApprovedParametricValue().getFromSign().getName();

				if(!fetValue.replace("[|/!]", "").trim().equals(""))
				{
					unApprovedDTO.setValue(fetValue);
				}
				else
				{
					unApprovedDTO.setValue("");
				}
				if(!signValue.replace("|", "").replace("/", "").replace("!", "").trim().equals(""))
				{
					unApprovedDTO.setSign(signValue);
				}
				else
				{
					unApprovedDTO.setSign("");
				}
				if(!multiplierValue.replace("|", "").replace("/", "").replace("!", "").trim().equals(""))
				{
					unApprovedDTO.setMultiplier(multiplierValue);
				}
				else
				{
					unApprovedDTO.setMultiplier("");
				}
				if(!typeValue.replace("|", "").replace("/", "").replace("!", "").trim().equals(""))
				{
					unApprovedDTO.setType(typeValue);
				}
				else
				{
					unApprovedDTO.setType("");
				}
				if(!conditionValue.replace("|", "").replace("/", "").replace("!", "").trim().equals(""))
				{
					unApprovedDTO.setCondition(conditionValue);
				}
				else
				{
					unApprovedDTO.setCondition("");
				}
				if(!unitValue.replace("|", "").replace("/", "").replace("!", "").trim().equals(""))
				{
					unApprovedDTO.setUnit(unitValue);
				}
				else
				{
					unApprovedDTO.setUnit("");
				}
				unApprovedDTO.setUserId(groupRecord.getParaUserId());
				result.add(unApprovedDTO);

			}
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}finally
		{
			session.close();
		}
		return result;
	}

	public static ArrayList<UnApprovedDTO> getUnapprovedReviewData(Long[] userids, String engName, Date startDate, Date endDate, String plName, String supplierName, String status, String tsktype, String team, String Datatype, long issuedTo)
	{
		ArrayList<UnApprovedDTO> result = new ArrayList<UnApprovedDTO>();
		Session session = SessionUtil.getSession();
		List rdList = null;
		List<Object> list = null;
		List groups = null;
		ParametricReviewData rd = null;
		UnApprovedDTO unApprovedDTO = null;
		try
		{
			String userCol = "paraUserId";
			if(team.equals("QA"))
				userCol = "qaUserId";
			Criteria criteria = session.createCriteria(ParametricApprovedGroup.class, "group");
			criteria.add(Restrictions.in(userCol, userids));
			criteria.createAlias("status", "status");
			criteria.add(Restrictions.eq("status.name", status));

			if(startDate != null && endDate != null)
			{
				criteria.add(Expression.between("storeDate", startDate, endDate));
			}

			if(plName != null && !plName.equals("All"))
			{
				criteria.createAlias("group.plFeature.pl", "pl");
				criteria.add(Restrictions.eq("pl.name", plName));
			}
			criteria.addOrder(Order.asc("plFeature"));
			criteria.addOrder(Order.desc("groupFullValue"));
			// criteria.addOrder(Order.asc("approvedValueOrder"));
			String sql = "document_id in (select document_id from tracking_parametric where ";
			boolean sup = false;
			boolean typ = false;

			if(Datatype.equals("Data"))
			{
				if(!(engName.equals("")) && !engName.equals("All"))
				{
					criteria.add(Restrictions.eq("paraUserId", ParaQueryUtil.getUserIdByExactName(engName)));
				}
				if(tsktype != null && !tsktype.equals("All"))
				{
					String tasktype = "";
					typ = true;
					if(tsktype.equals("NPI"))
					{
						sql += " TRACKING_TASK_TYPE_ID in(4,12,15)";
					}
					else
					{
						tasktype = "'" + tsktype + "'";
						sql += " TRACKING_TASK_TYPE_ID in getTaskTypeId(" + tasktype + ")";
					}

				}
			}

			if(supplierName != null && !supplierName.equals("All"))
			{
				sup = true;
				if(typ == false)
				{
					sql += "  SUPPLIER_ID in GETSUPPLIERID('" + supplierName + "') ";
					if(Datatype.equals("Data"))
					{
						sql += " and TRACKING_TASK_STATUS_ID in (getTaskstatusId('Pending TL Review'),getTaskstatusId('Pending QA Approval'),getTaskstatusId('Finished')) )";
					}
					else
					{
						sql += " ) ";
					}
				}
				else
				{
					sql += " and SUPPLIER_ID in GETSUPPLIERID('" + supplierName + "') ";
					if(Datatype.equals("Data"))
					{
						sql += " and TRACKING_TASK_STATUS_ID in (getTaskstatusId('Pending TL Review'),getTaskstatusId('Pending QA Approval'),getTaskstatusId('Finished')) )";
					}
					else
					{
						sql += " ) ";
					}
				}
			}
			if(typ == true && sup == false)
			{
				sql += " ) ";
			}
			if(typ == true || sup == true)
			{
				criteria.add(Restrictions.sqlRestriction(sql));
			}
			groups = criteria.list();

			// ArrayList<ArrayList<ParametricApprovedGroup>> re = new ArrayList<ArrayList<ParametricApprovedGroup>>();
			ArrayList<ParametricApprovedGroup> row = null;
			row = new ArrayList<ParametricApprovedGroup>();
			for(int h = 0; h < groups.size(); h++)
			{
				row.add((ParametricApprovedGroup) groups.get(h));
			}
			System.out.println("size is " + row.size());
			for(int j = 0; j < row.size(); j++)
			{
				unApprovedDTO = new UnApprovedDTO();
				ParametricApprovedGroup groupRecord = null;
				ParametricSeparationGroup separationgroup = null;
				List<ParametricSeparationGroup> separationgroups = null;
				ApprovedParametricValue approvedValue = null;
				FeedBackData FBData = null;
				String fetValue = "";
				String signValue = "";
				String multiplierValue = "";
				String typeValue = "";
				String conditionValue = "";
				String unitValue = "";
				String pattern = "";
				groupRecord = row.get(j);
				Criteria SeparationCri = session.createCriteria(ParametricSeparationGroup.class);
				SeparationCri.add(Restrictions.eq("parametricApprovedGroup", row.get(j)));
				criteria.addOrder(Order.asc("approvedValueOrder"));
				separationgroups = (List<ParametricSeparationGroup>) SeparationCri.list();
				if(Datatype.equals("FB"))
				{
					FBData = getFeedbackData(issuedTo, groupRecord, tsktype, session);
					if(FBData != null)
					{
						unApprovedDTO.setComment(FBData.getComment());
						unApprovedDTO.setFbStatus(FBData.getFbStatus());
						unApprovedDTO.setFbType(FBData.getFbType());
						unApprovedDTO.setQaComment(FBData.getQaComment());
						unApprovedDTO.setQaStatus(FBData.getQaStatus());
						unApprovedDTO.setQaUserId(FBData.getQaUserId());
						unApprovedDTO.setIssuedby(FBData.getIssuedby());
						unApprovedDTO.setIssueTo(FBData.getIssueTo());
						unApprovedDTO.setLastEngComment(FBData.getLastEngComment());
					}
					else
					{
						continue;
					}
				}

				for(int k = 0; k < separationgroups.size(); k++)
				{
					separationgroup = separationgroups.get(k);
					approvedValue = separationgroup.getApprovedParametricValue();
					fetValue += approvedValue.getFromValue().getValue();
					signValue += (separationgroup.getApprovedParametricValue().getFromSign() == null) ? "" : separationgroup.getApprovedParametricValue().getFromSign().getName();
					typeValue += (separationgroup.getApprovedParametricValue().getFromValueType() == null) ? "" : separationgroup.getApprovedParametricValue().getFromValueType().getName();
					conditionValue += (separationgroup.getApprovedParametricValue().getFromCondition() == null) ? "" : separationgroup.getApprovedParametricValue().getFromCondition().getName();

					if(approvedValue.getFromMultiplierUnit() != null)
					{
						multiplierValue += (separationgroup.getApprovedParametricValue().getFromMultiplierUnit().getMultiplier() == null) ? "" : separationgroup.getApprovedParametricValue().getFromMultiplierUnit().getMultiplier().getName();
						unitValue += (separationgroup.getApprovedParametricValue().getFromMultiplierUnit().getUnit() == null) ? "" : separationgroup.getApprovedParametricValue().getFromMultiplierUnit().getUnit().getName();
					}
					if(approvedValue.getToValue() != null)
					{

						fetValue += " to " + approvedValue.getToValue().getValue();
						if(approvedValue.getFromSign() != null)
						{
							String s = approvedValue.getFromSign().getName();
							if(!s.equals(" to "))
								signValue += (approvedValue.getToSign() == null) ? " to " : " to " + approvedValue.getToSign().getName();
						}
						if(approvedValue.getFromValueType() != null)
							typeValue += (approvedValue.getToValueType() == null) ? " to " : " to " + approvedValue.getToValueType().getName();
						if(approvedValue.getFromCondition() != null)
							conditionValue += (approvedValue.getToCondition() == null) ? " to " : " to " + approvedValue.getToCondition().getName();
						if(approvedValue.getToMultiplierUnit() != null)
						{
							if(approvedValue.getFromMultiplierUnit().getMultiplier() != null)
								multiplierValue += (approvedValue.getToMultiplierUnit().getMultiplier() == null) ? " to " : " to " + approvedValue.getToMultiplierUnit().getMultiplier().getName();
							if(approvedValue.getFromMultiplierUnit().getUnit() != null)
								unitValue += (approvedValue.getToMultiplierUnit().getUnit() == null) ? " to " : " to " + approvedValue.getToMultiplierUnit().getUnit().getName();
						}
					}

					pattern = (separationgroup.getPattern() == null) ? "" : separationgroup.getPattern();
					String patterns[] = pattern.split("\\$");
					if(patterns.length == 6)
					{
						fetValue += (patterns[0].contains(" to ")) ? " to " : patterns[0].trim();
						signValue += (patterns[1].contains(" to ")) ? " to " : patterns[1].trim();
						conditionValue += (patterns[2].contains(" to ")) ? " to " : patterns[2].trim();
						typeValue += (patterns[3].contains(" to ")) ? " to " : patterns[3].trim();
						multiplierValue += (patterns[4].contains(" to ")) ? " to " : patterns[4].trim();
						unitValue += (patterns[5].contains(" to ")) ? " to " : patterns[5].trim();
					}

					rdList = ParaQueryUtil.getParametricReviewData(groupRecord.getId(), session);
					if(!rdList.isEmpty())
					{
						rd = (ParametricReviewData) rdList.get(0);
						unApprovedDTO.setPartNumber(rd.getComponent().getPartNumber());
						unApprovedDTO.setPdfUrl(rd.getComponent().getDocument().getPdf().getSeUrl());
					}
					else
					{
						unApprovedDTO.setPartNumber("");
						unApprovedDTO.setPdfUrl("");
					}
					String featureUnit = (groupRecord.getPlFeature().getUnit() == null) ? "" : groupRecord.getPlFeature().getUnit().getName();
					unApprovedDTO.setFeatureUnit(featureUnit);
					String pl = (groupRecord.getPlFeature().getPl().getName() == null) ? "" : groupRecord.getPlFeature().getPl().getName();
					unApprovedDTO.setPlName(pl);
					String featureValue = (groupRecord.getGroupFullValue() == null) ? "" : groupRecord.getGroupFullValue();
					unApprovedDTO.setFeatureValue(featureValue);
					String featureName = (groupRecord.getPlFeature().getFeature().getName() == null) ? "" : groupRecord.getPlFeature().getFeature().getName();
					unApprovedDTO.setFeatureName(featureName);
					String fromSign = (separationgroup.getApprovedParametricValue().getFromSign() == null) ? "" : separationgroup.getApprovedParametricValue().getFromSign().getName();

					if(!fetValue.replace("[|/!]", "").trim().equals(""))
					{
						unApprovedDTO.setValue(fetValue);
					}
					else
					{
						unApprovedDTO.setValue("");
					}
					if(!signValue.replace("|", "").replace("/", "").replace("!", "").trim().equals(""))
					{
						unApprovedDTO.setSign(signValue);
					}
					else
					{
						unApprovedDTO.setSign("");
					}
					if(!multiplierValue.replace("|", "").replace("/", "").replace("!", "").trim().equals(""))
					{
						unApprovedDTO.setMultiplier(multiplierValue);
					}
					else
					{
						unApprovedDTO.setMultiplier("");
					}
					if(!typeValue.replace("|", "").replace("/", "").replace("!", "").trim().equals(""))
					{
						unApprovedDTO.setType(typeValue);
					}
					else
					{
						unApprovedDTO.setType("");
					}
					if(!conditionValue.replace("|", "").replace("/", "").replace("!", "").trim().equals(""))
					{
						unApprovedDTO.setCondition(conditionValue);
					}
					else
					{
						unApprovedDTO.setCondition("");
					}
					if(!unitValue.replace("|", "").replace("/", "").replace("!", "").trim().equals(""))
					{
						unApprovedDTO.setUnit(unitValue);
					}
					else
					{
						unApprovedDTO.setUnit("");
					}
					unApprovedDTO.setUserId(groupRecord.getParaUserId());
					// unApprovedDTO.setQaUserId(groupRecord.getQaUserId());
				}
				result.add(unApprovedDTO);
			}

			// }
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}finally
		{
			session.close();
		}
		return result;
	}

	public static ArrayList<UnApprovedDTO> getUnapprovedReviewDataN(Long[] userids, String engName, Date startDate, Date endDate, String plName, String supplierName, String status, String type, String team)
	{
		ArrayList<UnApprovedDTO> result = new ArrayList<UnApprovedDTO>();
		Session session = SessionUtil.getSession();
		ParametricReviewData rd = null;
		UnApprovedDTO unApprovedDTO = null;
		PartsParametricValuesGroup group = null;
		List rdList = null;
		List<Object> list = null;
		List groups = null;
		try
		{
			String userCol = "paraUserId";
			if(team.equals("QA"))
				userCol = "qaUserId";
			Criteria criteria = session.createCriteria(PartsParametricValuesGroup.class, "group");
			criteria.add(Restrictions.in(userCol, userids));
			criteria.createAlias("status", "status");
			criteria.add(Restrictions.eq("status.name", status));
			if(!(engName.equals("")) && !engName.equals("All"))
			{
				criteria.add(Restrictions.eq("paraUserId", ParaQueryUtil.getUserIdByExactName(engName)));
			}
			if(startDate != null && endDate != null)
			{
				criteria.add(Expression.between("storeDate", startDate, endDate));
			}

			if(type != null && !type.equals("All"))
			{
				Set<Document> docsSet = new HashSet<Document>();
				Criteria tarkCrit = session.createCriteria(TrackingParametric.class, "track");
				tarkCrit.createAlias("trackingTaskType", "typ");
				tarkCrit.createAlias("trackingTaskStatus", "status");
				tarkCrit.add(Restrictions.or(Restrictions.eq("status.name", "Pending TL Review"), Restrictions.eq("status.name", "Pending QA Approval")));
				if(type.equals("NPI"))
				{
					tarkCrit.add(Restrictions.or(Restrictions.eq("typ.name", "NPI"), Restrictions.eq("typ.name", "NPI Transferred"), Restrictions.eq("typ.name", "NPI Update")));
				}
				else
				{
					tarkCrit.add(Restrictions.eq("typ.name", type));
				}
				tarkCrit.setProjection(Projections.property("document"));
				List<Document> docs = tarkCrit.list();
				for(int i = 0; i < docs.size(); i++)
				{
					System.out.println(docs.get(i).getId());
				}
				docsSet = new HashSet<Document>(docs);
				criteria.add(Restrictions.in("document", docs));
			}
			if(plName != null && !plName.equals("All"))
			{
				criteria.createAlias("group.plFeature.pl", "pl");
				criteria.add(Restrictions.eq("pl.name", plName));
			}

			if(supplierName != null && !supplierName.equals("All"))
			{

				Criteria trackingcriteria = session.createCriteria(TrackingParametric.class, "track");
				trackingcriteria.createAlias("trackingTaskStatus", "status");
				trackingcriteria.add(Restrictions.or(Restrictions.eq("status.name", "Pending TL Review"), Restrictions.eq("status.name", "Pending QA Approval"), Restrictions.eq("status.name", "Finished")));
				trackingcriteria.createAlias("track.supplier", "supplier");
				trackingcriteria.add(Restrictions.eq("supplier.name", supplierName));
				// // ///////////////////////////////////////////////////////////////////////////////////////
				// // ////May cause an issue if distinct docs for this supplier in tracking parametric exceeds 1000
				trackingcriteria.setProjection(Projections.distinct(Projections.property("document")));
				// List<Document> documentList = trackingcriteria.list();
				Set set = new HashSet(trackingcriteria.list());
				criteria.add(Restrictions.in("document", set));
			}
			criteria.addOrder(Order.asc("plFeature"));
			criteria.addOrder(Order.desc("groupFullValue"));
			criteria.addOrder(Order.asc("approvedValueOrder"));
			groups = criteria.list();
			ArrayList<ArrayList<PartsParametricValuesGroup>> re = new ArrayList<ArrayList<PartsParametricValuesGroup>>();
			ArrayList<PartsParametricValuesGroup> row = null;
			int count = 0;
			group = (PartsParametricValuesGroup) groups.get(0);
			String fullValue = group.getGroupFullValue();
			PlFeature plFet = group.getPlFeature();
			row = new ArrayList<PartsParametricValuesGroup>();
			while(count < groups.size())
			{
				group = ((PartsParametricValuesGroup) groups.get(count));
				if(group.getGroupFullValue().equals(fullValue) && group.getPlFeature() == plFet)
				{
					row.add(group);
					count++;
				}
				else
				{
					re.add(row);
					row = new ArrayList<PartsParametricValuesGroup>();
					fullValue = group.getGroupFullValue();
					plFet = group.getPlFeature();
				}
			}
			re.add(row);
			System.out.println("size is " + re.size());
			ArrayList<PartsParametricValuesGroup> values = null;
			for(int i = 0; i < re.size(); i++)
			{
				values = re.get(i);
				unApprovedDTO = new UnApprovedDTO();
				PartsParametricValuesGroup groupRecord = null;
				ApprovedParametricValue approvedValue = null;
				String fetValue = "";
				String signValue = "";
				String multiplierValue = "";
				String typeValue = "";
				String conditionValue = "";
				String unitValue = "";
				String pattern = "";
				groupRecord = values.get(0);
				approvedValue = groupRecord.getApprovedParametricValue();
				// fetValue = approvedValue.getFromValue().getValue();
				// if (approvedValue.getToValue() != null) {
				// fetValue += " to " + approvedValue.getToValue().getValue();
				// }

				// pattern = (approvedValue.getPattern() == null) ? "" : approvedValue.getPattern();
				// String patterns[] = pattern.split(" ");
				for(int j = 0; j < values.size(); j++)
				{
					// if (j > 0 && j <= (patterns.length)) {
					// fetValue += patterns[j - 1];
					// signValue += patterns[j - 1];
					// multiplierValue += patterns[j - 1];
					// typeValue += patterns[j - 1];
					// conditionValue += patterns[j - 1];
					// unitValue += patterns[j - 1];
					// }

					groupRecord = values.get(j);
					approvedValue = groupRecord.getApprovedParametricValue();
					fetValue += approvedValue.getFromValue().getValue();
					signValue += (groupRecord.getApprovedParametricValue().getFromSign() == null) ? "" : groupRecord.getApprovedParametricValue().getFromSign().getName();
					typeValue += (groupRecord.getApprovedParametricValue().getFromValueType() == null) ? "" : groupRecord.getApprovedParametricValue().getFromValueType().getName();
					conditionValue += (groupRecord.getApprovedParametricValue().getFromCondition() == null) ? "" : groupRecord.getApprovedParametricValue().getFromCondition().getName();

					if(approvedValue.getFromMultiplierUnit() != null)
					{
						multiplierValue += (groupRecord.getApprovedParametricValue().getFromMultiplierUnit().getMultiplier() == null) ? "" : groupRecord.getApprovedParametricValue().getFromMultiplierUnit().getMultiplier().getName();
						unitValue += (groupRecord.getApprovedParametricValue().getFromMultiplierUnit().getUnit() == null) ? "" : groupRecord.getApprovedParametricValue().getFromMultiplierUnit().getUnit().getName();
					}
					// if (approvedValue.getToValue() != null) {
					// fetValue += " to " + approvedValue.getToValue().getValue();
					// }
					// if (approvedValue.getToSign() != null) {
					// signValue += " to " + approvedValue.getToSign().getName();
					// }
					// if (approvedValue.getToMultiplierUnit() != null) {
					// if (approvedValue.getToMultiplierUnit().getMultiplier() != null) {
					// multiplierValue += " to " + approvedValue.getToMultiplierUnit().getMultiplier().getName();
					// }
					// if (approvedValue.getToMultiplierUnit().getUnit() != null) {
					// unitValue += " to " + approvedValue.getToMultiplierUnit().getUnit().getName();
					// }
					// }
					// if (approvedValue.getToValueType() != null) {
					// typeValue += " to " + approvedValue.getToValueType().getName();
					// }
					// if (approvedValue.getToCondition() != null) {
					// conditionValue += " to " + approvedValue.getToCondition().getName();
					// }

					if(approvedValue.getToValue() != null)
					{

						fetValue += " to " + approvedValue.getToValue().getValue();
						if(approvedValue.getFromSign() != null)
						{
							String s = approvedValue.getFromSign().getName();
							if(!s.equals(" to "))
								signValue += (approvedValue.getToSign() == null) ? " to " : " to " + approvedValue.getToSign().getName();
						}
						if(approvedValue.getFromValueType() != null)
							typeValue += (approvedValue.getToValueType() == null) ? " to " : " to " + approvedValue.getToValueType().getName();
						if(approvedValue.getFromCondition() != null)
							conditionValue += (approvedValue.getToCondition() == null) ? " to " : " to " + approvedValue.getToCondition().getName();
						if(approvedValue.getToMultiplierUnit() != null)
						{
							if(approvedValue.getFromMultiplierUnit().getMultiplier() != null)
								multiplierValue += (approvedValue.getToMultiplierUnit().getMultiplier() == null) ? " to " : " to " + approvedValue.getToMultiplierUnit().getMultiplier().getName();
							if(approvedValue.getFromMultiplierUnit().getUnit() != null)
								unitValue += (approvedValue.getToMultiplierUnit().getUnit() == null) ? " to " : " to " + approvedValue.getToMultiplierUnit().getUnit().getName();
						}
					}

					pattern = (groupRecord.getPattern() == null) ? "" : groupRecord.getPattern();
					String patterns[] = pattern.split("\\$");
					if(patterns.length == 6)
					{
						fetValue += (patterns[0].contains(" to ")) ? " to " : patterns[0].trim();
						signValue += (patterns[1].contains(" to ")) ? " to " : patterns[1].trim();
						conditionValue += (patterns[2].contains(" to ")) ? " to " : patterns[2].trim();
						typeValue += (patterns[3].contains(" to ")) ? " to " : patterns[3].trim();
						multiplierValue += (patterns[4].contains(" to ")) ? " to " : patterns[4].trim();
						unitValue += (patterns[5].contains(" to ")) ? " to " : patterns[5].trim();
					}

				}
				rdList = ParaQueryUtil.getParametricReviewData(groupRecord.getGroupId(), session);
				if(!rdList.isEmpty())
				{
					rd = (ParametricReviewData) rdList.get(0);
					unApprovedDTO.setPartNumber(rd.getComponent().getPartNumber());
					unApprovedDTO.setPdfUrl(rd.getComponent().getDocument().getPdf().getSeUrl());
				}
				else
				{
					unApprovedDTO.setPartNumber("");
					unApprovedDTO.setPdfUrl("");
				}
				String featureUnit = (groupRecord.getPlFeature().getUnit() == null) ? "" : groupRecord.getPlFeature().getUnit().getName();
				unApprovedDTO.setFeatureUnit(featureUnit);
				String pl = (groupRecord.getPlFeature().getPl().getName() == null) ? "" : groupRecord.getPlFeature().getPl().getName();
				unApprovedDTO.setPlName(pl);
				String featureValue = (groupRecord.getGroupFullValue() == null) ? "" : groupRecord.getGroupFullValue();
				unApprovedDTO.setFeatureValue(featureValue);
				String featureName = (groupRecord.getPlFeature().getFeature().getName() == null) ? "" : groupRecord.getPlFeature().getFeature().getName();
				unApprovedDTO.setFeatureName(featureName);
				String fromSign = (groupRecord.getApprovedParametricValue().getFromSign() == null) ? "" : groupRecord.getApprovedParametricValue().getFromSign().getName();

				if(!fetValue.replace("[|/!]", "").trim().equals(""))
				{
					unApprovedDTO.setValue(fetValue);
				}
				else
				{
					unApprovedDTO.setValue("");
				}
				if(!signValue.replace("|", "").replace("/", "").replace("!", "").trim().equals(""))
				{
					unApprovedDTO.setSign(signValue);
				}
				else
				{
					unApprovedDTO.setSign("");
				}
				if(!multiplierValue.replace("|", "").replace("/", "").replace("!", "").trim().equals(""))
				{
					unApprovedDTO.setMultiplier(multiplierValue);
				}
				else
				{
					unApprovedDTO.setMultiplier("");
				}
				if(!typeValue.replace("|", "").replace("/", "").replace("!", "").trim().equals(""))
				{
					unApprovedDTO.setType(typeValue);
				}
				else
				{
					unApprovedDTO.setType("");
				}
				if(!conditionValue.replace("|", "").replace("/", "").replace("!", "").trim().equals(""))
				{
					unApprovedDTO.setCondition(conditionValue);
				}
				else
				{
					unApprovedDTO.setCondition("");
				}
				if(!unitValue.replace("|", "").replace("/", "").replace("!", "").trim().equals(""))
				{
					unApprovedDTO.setUnit(unitValue);
				}
				else
				{
					unApprovedDTO.setUnit("");
				}
				// String value = (obj.getApprovedParametricValue().getFromValue() == null) ? "" : obj.getApprovedParametricValue()
				// .getFromValue().getValue();
				// unApprovedDTO.setValue(value);
				// String type = (obj.getApprovedParametricValue().getFromValueType() == null) ? "" : obj.getApprovedParametricValue()
				// .getFromValueType().getName();
				// unApprovedDTO.setType(type);
				// String condition = (obj.getApprovedParametricValue().getFromCondition() == null) ? "" : obj.getApprovedParametricValue()
				// .getFromCondition().getName();
				// unApprovedDTO.setCondition(condition);
				// String multiplier = "";
				// try {
				// multiplier = (obj.getApprovedParametricValue().getFromMultiplierUnit().getMultiplier() == null) ? "" : obj
				// .getApprovedParametricValue().getFromMultiplierUnit().getMultiplier().getName();
				// } catch (Exception e) {
				//
				// }
				// unApprovedDTO.setMultiplier(multiplier);
				// String unit = "";
				// try {
				// unit = (obj.getApprovedParametricValue().getFromMultiplierUnit().getUnit() == null) ? "" : obj
				// .getApprovedParametricValue().getFromMultiplierUnit().getUnit().getName();
				// } catch (Exception e) {
				// }
				// unApprovedDTO.setUnit(unit);
				unApprovedDTO.setUserId(groupRecord.getParaUserId());
				result.add(unApprovedDTO);

			}
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}finally
		{
			session.close();
		}
		return result;
	}

}
