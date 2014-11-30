package com.se.parametric.dba;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;

import com.se.automation.db.QueryUtil;
import com.se.automation.db.SessionUtil;
import com.se.automation.db.client.mapping.ApprovedParametricValue;
import com.se.automation.db.client.mapping.Condition;
import com.se.automation.db.client.mapping.Document;
import com.se.automation.db.client.mapping.Multiplier;
import com.se.automation.db.client.mapping.MultiplierUnit;
import com.se.automation.db.client.mapping.ParaFeedbackAction;
import com.se.automation.db.client.mapping.ParaFeedbackStatus;
import com.se.automation.db.client.mapping.ParaIssueType;
import com.se.automation.db.client.mapping.ParametricApprovedGroup;
import com.se.automation.db.client.mapping.ParametricFeedback;
import com.se.automation.db.client.mapping.ParametricFeedbackCycle;
import com.se.automation.db.client.mapping.ParametricReviewData;
import com.se.automation.db.client.mapping.ParametricSeparationGroup;
import com.se.automation.db.client.mapping.PlFeature;
import com.se.automation.db.client.mapping.Sign;
import com.se.automation.db.client.mapping.TrackingFeedbackType;
import com.se.automation.db.client.mapping.TrackingParametric;
import com.se.automation.db.client.mapping.TrackingTaskStatus;
import com.se.automation.db.client.mapping.Unit;
import com.se.automation.db.client.mapping.Value;
import com.se.automation.db.client.mapping.ValueType;
import com.se.automation.db.parametric.StatusName;
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
	 * @throws Exception
	 * 
	 */
	public static void setValueApproved(ArrayList<String> row, String status) throws Exception
	{
		Session session = SessionUtil.getSession();
		Criteria criteria;
		try
		{
			ParametricApprovedGroup group = ParaQueryUtil.getParametricApprovedGroup(row.get(6),
					row.get(0), row.get(5), session);
			criteria = session.createCriteria(TrackingTaskStatus.class);
			criteria.add(Restrictions.eq("name", status));
			TrackingTaskStatus trackingTaskStatus = (TrackingTaskStatus) criteria.uniqueResult();
			group.setStatus(trackingTaskStatus);
			group.setReviewedDate(new Date());
			session.saveOrUpdate(group);

			// }catch(Exception e)
			// {
			// e.printStackTrace();
		}finally
		{
			session.close();
		}
	}

	public static void updateApprovedValue(int updateFlag, UnApprovedDTO app) throws Exception
	{
		Session session = SessionUtil.getSession();
		try
		{
			List<ApprovedParametricDTO> approved = createApprovedValuesList(app.getFeatureValue(),
					app.getPlName(), app.getFeatureName(), app.getFeatureUnit(), app.getSign(),
					app.getValue(), app.getMultiplier(), app.getUnit(), app.getCondition(),
					app.getType());

			saveAppGroupAndSepValue(0, updateFlag, approved, app.getPlName(), app.getFeatureName(),
					app.getFeatureValue(), app.getPdfUrl(), app.getUserId());

			// }catch(Exception e)
			// {
			// e.printStackTrace();
		}finally
		{
			session.close();
		}
	}

	public static List<ApprovedParametricDTO> createApprovedValuesList(String fullValue,
			String plName, String featureName, String featureUnit, String sign, String value,
			String multiplier, String unit, String condition, String valueType)
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
				throw new ArrayIndexOutOfBoundsException(
						"Error number of sticks in sign. \nPlease enter a valid sign");
			if(!multiplier.trim().equals("") && multiplierArr.length > multiValCount)
				throw new ArrayIndexOutOfBoundsException(
						"Error number of sticks in multiplier. \nPlease enter a valid multiplier");
			if(!unit.trim().equals("") && unitArr.length > multiValCount)
				throw new ArrayIndexOutOfBoundsException(
						"Error number of sticks in unit. \nPlease enter a valid unit");
			if(!condition.trim().equals("") && conditionArr.length > multiValCount)
				throw new ArrayIndexOutOfBoundsException(
						"Error number of sticks in condition. \nPlease enter a valid condition");
			if(!valueType.trim().equals("") && valueTypeArr.length > multiValCount)
				throw new ArrayIndexOutOfBoundsException(
						"Error number of sticks in value type. \nPlease enter a valid value type");

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
				if((value.contains("|") || value.contains("!") || value.contains(" to "))
						&& !valueSepArr[i].equals(""))
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
					pattern = valueSepArr[i] + " $ " + signSep + " $ " + conditionSep + " $ "
							+ valueTypeSep + " $ " + multiplierSep + " $ " + unitSep;
				}
				else
				{
					pattern = "";
				}

				approvedParametricDTO.setPattern(pattern);
				PlFeature plFeature = ParaQueryUtil.getPlFeatureByExactName(featureName, plName,
						session);
				criteria = session.createCriteria(ApprovedParametricValue.class);
				criteria.add(Restrictions.eq("plFeature", plFeature));
				criteria.add(Restrictions.eq("fullValue", approvedParametricDTO.toString()));
				ApprovedParametricValue approvedParametricValue = (ApprovedParametricValue) criteria
						.uniqueResult();

				approvedParametricDTO.setApprovedParametricValue(approvedParametricValue);

				approvedParametricValueDTOList.add(approvedParametricDTO);
			}
		}finally
		{
			session.close();
		}

		return approvedParametricValueDTOList;
	}

	public static long saveAppGroupAndSepValue(int engine, int update,
			List<ApprovedParametricDTO> approvedParametricDTOList, String plName,
			String featureName, String groupFullValue, String pdfurl, Long userId) throws Exception
	{
		Session session = SessionUtil.getSession();
		int approvedValueOrder = 1;
		ParametricApprovedGroup approvedGroup = null;
		try
		{
			Document document = null;
			if(pdfurl != null && !pdfurl.isEmpty())
				document = ParaQueryUtil.getDocumentBySeUrl(pdfurl, session);
			approvedGroup = ParaQueryUtil.addAppValueGroup(engine, update, document, plName,
					featureName, groupFullValue, userId, session);
			if(approvedGroup != null)
				ParaQueryUtil.deleteSeprationGroups(approvedGroup, session);
			approvedValueOrder = 1;

			for(ApprovedParametricDTO approvedParametricDTO : approvedParametricDTOList)
			{
				saveApprovedParametricValue(engine, update, approvedParametricDTO, plName,
						featureName, approvedGroup, approvedValueOrder, userId, session);
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

	public static ApprovedParametricValue saveApprovedParametricValue(int engine, int update,
			ApprovedParametricDTO approvedParametricDTO, String plName, String featureName,
			ParametricApprovedGroup parametricApprovedGroup, int approvedValueOrder,
			Long paraUserId, Session sessionold) throws Exception
	{
		Session session = SessionUtil.getSession();
		try
		{
			ApprovedParametricValue approvedParametricValue = null;
			PlFeature plFeature = ParaQueryUtil.getPlFeatureByExactName(featureName, plName,
					session);
			System.out.println("approved value Id is: ");
			System.out
					.println((approvedParametricDTO.getApprovedParametricValue() == null) ? "null"
							: approvedParametricDTO.getApprovedParametricValue().getId());
			if(approvedParametricDTO.getApprovedParametricValue() != null
					&& approvedParametricDTO.getApprovedParametricValue().getIsApproved() == 1l)
			{
				ParaQueryUtil.addSeparationGroup(engine, update,
						approvedParametricDTO.getApprovedParametricValue(),
						approvedParametricDTO.getPattern(), parametricApprovedGroup,
						approvedValueOrder, session);
				return approvedParametricDTO.getApprovedParametricValue();
			}

			/*
			 * updated by Ahmed Makram @ 3-11-2013 to Update the approved value separation which not approved.
			 */

			if(approvedParametricDTO.getApprovedParametricValue() != null
					&& approvedParametricDTO.getApprovedParametricValue().getIsApproved() == 0l)
			{
				ApprovedParametricValue approvedParametricValue1 = approvedParametricDTO
						.getApprovedParametricValue();
				approvedParametricValue = (ApprovedParametricValue) session.get(
						ApprovedParametricValue.class, approvedParametricDTO
								.getApprovedParametricValue().getId());
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
			approvedParametricValue.setFromValue(ParaQueryUtil.getValueByExactValue(
					approvedParametricDTO.getFromValue(), session));
			if(approvedParametricValue.getFromValue() == null)
			{
				// add this value
				Value value = new Value();
				ParaQueryUtil.saveValue(session, approvedParametricDTO.getFromValue(), value);
				approvedParametricValue.setFromValue(value);
			}

			if(approvedParametricDTO.getFromSign() != null
					&& !approvedParametricDTO.getFromSign().equals(""))
			{
				approvedParametricValue.setFromSign(ParaQueryUtil.getSignByExactName(
						approvedParametricDTO.getFromSign(), session));
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
			if(approvedParametricDTO.getFromCondition() != null
					&& !approvedParametricDTO.getFromCondition().equals(""))
			{
				approvedParametricValue.setFromCondition(ParaQueryUtil.getConditionByExactName(
						approvedParametricDTO.getFromCondition(), session));
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
			if(approvedParametricDTO.getFromValueType() != null
					&& !approvedParametricDTO.getFromValueType().equals(""))
			{
				approvedParametricValue.setFromValueType(ParaQueryUtil.getValueTypeByExactName(
						approvedParametricDTO.getFromValueType(), session));
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
			if(approvedParametricDTO.getFromMultiplier() != null
					&& !approvedParametricDTO.getFromMultiplier().equals(""))
			{
				approvedParametricValue.getFromMultiplierUnit().setMultiplier(
						ParaQueryUtil.getMultiplierByExactName(
								approvedParametricDTO.getFromMultiplier(), session));
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
			if(approvedParametricDTO.getFromUnit() != null
					&& !approvedParametricDTO.getFromUnit().equals(""))
			{
				approvedParametricValue.getFromMultiplierUnit().setUnit(
						ParaQueryUtil.getUnitByExactName(approvedParametricDTO.getFromUnit(),
								session));
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
			approvedParametricValue.setFromMultiplierUnit(getMultiplierUnit(approvedParametricValue
					.getFromMultiplierUnit().getMultiplier(), approvedParametricValue
					.getFromMultiplierUnit().getUnit(), session));

			// -----------------------------TO
			if(approvedParametricDTO.getToValue() != null
					&& !approvedParametricDTO.getToValue().equals(""))
			{
				approvedParametricValue.setToValue(ParaQueryUtil.getValueByExactValue(
						approvedParametricDTO.getToValue(), session));
				if(approvedParametricValue.getToValue() == null)
				{
					// add this value
					Value value = new Value();
					ParaQueryUtil.saveValue(session, approvedParametricDTO.getToValue(), value);
					approvedParametricValue.setToValue(value);
				}
				if(approvedParametricDTO.getToSign() != null
						&& !approvedParametricDTO.getToSign().equals(""))
				{
					approvedParametricValue.setToSign(ParaQueryUtil.getSignByExactName(
							approvedParametricDTO.getToSign(), session));
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
				if(approvedParametricDTO.getToCondition() != null
						&& !approvedParametricDTO.getToCondition().equals(""))
				{
					approvedParametricValue.setToCondition(ParaQueryUtil.getConditionByExactName(
							approvedParametricDTO.getToCondition(), session));
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
				if(approvedParametricDTO.getToValueType() != null
						&& !approvedParametricDTO.getToValueType().equals(""))
				{
					approvedParametricValue.setToValueType(ParaQueryUtil.getValueTypeByExactName(
							approvedParametricDTO.getToValueType(), session));
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
				if(approvedParametricDTO.getToMultiplier() != null
						&& !approvedParametricDTO.getToMultiplier().equals(""))
				{
					approvedParametricValue.getToMultiplierUnit().setMultiplier(
							ParaQueryUtil.getMultiplierByExactName(
									approvedParametricDTO.getToMultiplier(), session));
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
				if(approvedParametricDTO.getToUnit() != null
						&& !approvedParametricDTO.getToUnit().equals(""))
				{
					approvedParametricValue.getToMultiplierUnit().setUnit(
							ParaQueryUtil.getUnitByExactName(approvedParametricDTO.getToUnit(),
									session));
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
				approvedParametricValue.setToMultiplierUnit(getMultiplierUnit(
						approvedParametricValue.getToMultiplierUnit().getMultiplier(),
						approvedParametricValue.getToMultiplierUnit().getUnit(), session));
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
				System.out
						.println("Approved Value Found before:" + approvedParametricDTO.toString()
								+ " ~~ " + featureName + " ~~ " + plName);
				plFeature = ParaQueryUtil.getPlFeatureByExactName(featureName, plName, session);
				Criteria criteria = session.createCriteria(ApprovedParametricValue.class);
				criteria.add(Restrictions.eq("plFeature", plFeature));
				criteria.add(Restrictions.eq("fullValue", approvedParametricDTO.toString()));
				approvedParametricValue = (ApprovedParametricValue) criteria.uniqueResult();
				ParaQueryUtil.addSeparationGroup(engine, update,
						approvedParametricDTO.getApprovedParametricValue(),
						approvedParametricDTO.getPattern(), parametricApprovedGroup,
						approvedValueOrder, session);
				return approvedParametricValue;
			}

			// save the group
			ParaQueryUtil.addSeparationGroup(engine, update, approvedParametricValue,
					approvedParametricDTO.getPattern(), parametricApprovedGroup,
					approvedValueOrder, session);
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
			multiplierUnit = ParaQueryUtil.getMultiplierUnitByExactMultiplierAndUnit(multiplier,
					unit, session);
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
			if(s.charAt(i) == charStick || s.charAt(i) == charSlash || s.charAt(i) == charTwoSlash
					|| s.charAt(i) == charto)
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
	 * @throws Exception
	 */
	public static void saveWrongSeparation(UnApprovedDTO app) throws Exception
	{
		Session session = SessionUtil.getSession();
		Criteria criteria;
		try
		{

			ParametricApprovedGroup groups = ParaQueryUtil.getParametricApprovedGroup(
					app.getFeatureValue(), app.getPlName(), app.getFeatureName(), session);
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

			ParametricFeedbackCycle appFeedback = null;
			Criteria feedBackCrit = session.createCriteria(ParametricFeedbackCycle.class);
			if(app.getFbType().equals("Internal"))
			{
				feedBackCrit.add(Restrictions.eq("issuedTo", app.getUserId()));
			}
			else
			{
				feedBackCrit.add(Restrictions.eq("issuedTo",
						ParaQueryUtil.getTLByUserID(app.getUserId())));
			}
			// feedBackCrit.add(Restrictions.eq("issuedTo", issuedTo));
			feedBackCrit.add(Restrictions.eq("feedbackRecieved", 0l));
			feedBackCrit.createAlias("parametricFeedback", "feedback");
			feedBackCrit.add(Restrictions.eq("feedback.type", "V"));
			feedBackCrit.add(Restrictions.eq("feedback.itemId", groups.getId()));
			appFeedback = (ParametricFeedbackCycle) feedBackCrit.uniqueResult();

			if(appFeedback == null)
			{
				FBObj.setId(System.nanoTime());
				FBObj.setParaIssueType(paraIssueType);
				FBObj.setParaFeedbackStatus(paraFeedbackStatus);
				FBObj.setStoreDate(new Date());
				FBObj.setFbInitiator(app.getIssuedby());
				FBObj.setTrackingFeedbackType(trackingFeedbackType);
				FBObj.setItemId(groups.getId());
				FBObj.setType("V");
				FBObj.setDocument(document);

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

				FBCyc.setParaFeedbackStatus(paraFeedbackAction);
				FBCyc.setFeedbackRecieved(0l);
				session.saveOrUpdate(FBObj);
				session.saveOrUpdate(FBCyc);
				session.beginTransaction().commit();

				criteria = session.createCriteria(TrackingTaskStatus.class);
				criteria.add(Restrictions.eq("name", app.getGruopSatus()));
				TrackingTaskStatus trackingTaskStatus = (TrackingTaskStatus) criteria
						.uniqueResult();//
				groups.setStatus(trackingTaskStatus);
				session.saveOrUpdate(groups);
			}
			// }catch(Exception e)
			// {
			// e.printStackTrace();
			// System.out.println("here");
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

			ParametricApprovedGroup groups = ParaQueryUtil.getParametricApprovedGroup(
					app.getFeatureValue(), app.getPlName(), app.getFeatureName(), session);
			ParaFeedbackStatus paraFeedbackAction = null;
			ParaFeedbackStatus paraFeedbackStatus = null;
			ParaIssueType paraIssueType = null;
			TrackingFeedbackType trackingFeedbackType = null;
			ParametricFeedback FBObj = null;
			Document document = null;

			/** component has group value */
			criteria = session.createCriteria(ParametricReviewData.class);
			criteria.add(Restrictions.eq("groupApprovedValueId", groups.getId()));
			List<ParametricReviewData> wrongPartsList = criteria.list();
			/** reject status criteria */
			criteria = session.createCriteria(ParaFeedbackStatus.class);
			criteria.add(Restrictions.eq("feedbackStatus", "Open"));
			paraFeedbackStatus = (ParaFeedbackStatus) criteria.uniqueResult();
			/** Wrong value FB Type criteria */
			criteria = session.createCriteria(ParaIssueType.class);
			criteria.add(Restrictions.eq("issueType", app.getIssueType()));
			paraIssueType = (ParaIssueType) criteria.uniqueResult();

			criteria = session.createCriteria(TrackingFeedbackType.class);
			criteria.add(Restrictions.eq("name", app.getFbType()));
			trackingFeedbackType = (TrackingFeedbackType) criteria.uniqueResult();

			criteria = session.createCriteria(ParaFeedbackStatus.class);
			criteria.add(Restrictions.eq("feedbackStatus", app.getFbStatus()));
			paraFeedbackAction = (ParaFeedbackStatus) criteria.uniqueResult();

			Set<TrackingParametric> tracks = new HashSet<TrackingParametric>();
			for(int i = 0; i < wrongPartsList.size(); i++)
			{
				ParametricReviewData rd = wrongPartsList.get(i);
				if(rd.getTrackingParametric() != null)
				{
					tracks.add(rd.getTrackingParametric());
					document = rd.getTrackingParametric().getDocument();
				}
				ParametricFeedbackCycle FBCyc = null;
				ParametricFeedbackCycle OldFBCyc = null;
				ParaFeedbackAction feedbackAction = null;
				// FBObj = new ParametricFeedback();
				// if feedback posted already return
				Criteria fbCriteria = session.createCriteria(ParametricFeedbackCycle.class);
				fbCriteria.add(Restrictions.eq("fbItemValue", rd.getComponent().getPartNumber()));
				fbCriteria.add(Restrictions.eq("feedbackRecieved", 0l));
				fbCriteria.add(Restrictions.eq("issuedBy", app.getIssuedby()));
				fbCriteria.add(Restrictions.eq("issuedTo", app.getUserId()));
				OldFBCyc = (ParametricFeedbackCycle) fbCriteria.uniqueResult();
				if(OldFBCyc != null)
				{
					FBObj = OldFBCyc.getParametricFeedback();
					OldFBCyc.setFeedbackRecieved(1l);
					FBObj.setDocument(document);
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

					FBCyc.setParaFeedbackStatus(paraFeedbackAction);
					FBCyc.setFeedbackRecieved(0l);
					if(app.getCAction() != null && app.getPAction() != null
							&& app.getRootCause() != null && app.getActionDueDate() != null)
					{
						feedbackAction = getParaAction(app.getCAction(), app.getPAction(),
								app.getRootCause(), app.getActionDueDate(), session);
						if(feedbackAction != null)
						{
							FBCyc.setParaFeedbackAction(feedbackAction);
						}
					}
					session.saveOrUpdate(OldFBCyc);
					session.saveOrUpdate(FBCyc);
				}
				else
				{
					FBObj = new ParametricFeedback();
					FBObj.setId(System.nanoTime());
					FBObj.setParaIssueType(paraIssueType);
					FBObj.setParaFeedbackStatus(paraFeedbackStatus);
					FBObj.setStoreDate(new Date());
					FBObj.setFbInitiator(app.getIssuedby());
					FBObj.setTrackingFeedbackType(trackingFeedbackType);
					FBObj.setItemId(rd.getComponent().getComId());
					FBObj.setType("P");
					FBObj.setDocument(document);

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

					FBCyc.setParaFeedbackStatus(paraFeedbackAction);
					FBCyc.setFeedbackRecieved(0l);
					if(app.getCAction() != null && app.getPAction() != null
							&& app.getRootCause() != null && app.getActionDueDate() != null)
					{
						feedbackAction = getParaAction(app.getCAction(), app.getPAction(),
								app.getRootCause(), app.getActionDueDate(), session);
						if(feedbackAction != null)
						{
							FBCyc.setParaFeedbackAction(feedbackAction);
						}
					}
					session.saveOrUpdate(FBObj);
					session.saveOrUpdate(FBCyc);
				}
			}
			criteria = session.createCriteria(TrackingTaskStatus.class);
			criteria.add(Restrictions.eq("name", app.getFbStatus()));
			TrackingTaskStatus trackingTaskStatus = (TrackingTaskStatus) criteria.uniqueResult();//

			criteria = session.createCriteria(TrackingTaskStatus.class);
			criteria.add(Restrictions.eq("name", app.getGruopSatus()));
			TrackingTaskStatus trackingParaStatus = (TrackingTaskStatus) criteria.uniqueResult();
			groups.setStatus(trackingTaskStatus);
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

	public static ArrayList<Object[]> getUnapprovedReviewFilter(Long[] ids, Date startDate,
			Date endDate, String team)
	{
		Session session = SessionUtil.getSession();
		// Session grmSession = com.se.grm.db.SessionUtil.getSession();
		Criteria criteria;
		ArrayList<Object[]> result = new ArrayList<Object[]>();
		try
		{
			String userCol = "paraUserId";
			String status = StatusName.tlReview;
			if(team.equals("QA"))
			{
				userCol = "qaUserId";
				status = StatusName.qaReview;
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

						if(statusId == StatusName.inprogressId
								|| statusId == StatusName.doneFLagEngineId)
						{
							row[2] = tp.getSupplier().getName();
							row[4] = tp.getTrackingTaskType().getName();
						}
						else
							continue;

						// if(statusId!=10)
						// {
						// continue;
						// }

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

	public static FeedBackData getFeedbackData(long issuedTo, ParametricApprovedGroup groupRecord,
			String taskType, Date startdate, Date enddate, Session session)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		FeedBackData result = null;
		ParametricFeedbackCycle appFeedback = null;
		Criteria feedBackCrit = session.createCriteria(ParametricFeedbackCycle.class);
		feedBackCrit.add(Restrictions.eq("issuedTo", issuedTo));
		feedBackCrit.add(Restrictions.eq("feedbackRecieved", 0l));
		// feedBackCrit.add(Restrictions.eq("fbItemValue", groupRecord.getGroupFullValue()));
		feedBackCrit.createAlias("parametricFeedback", "feedback");
		feedBackCrit.add(Restrictions.eq("feedback.type", "V"));
		feedBackCrit.add(Restrictions.eq("feedback.itemId", groupRecord.getId()));
		if(startdate != null && enddate != null)
		{
			feedBackCrit.add(Expression.between("storeDate", startdate, enddate));
		}
		System.out.println(groupRecord.getId());
		appFeedback = (ParametricFeedbackCycle) feedBackCrit.uniqueResult();
		if(appFeedback != null)
		{
			result = new FeedBackData();
			result.setComment((appFeedback == null) ? "" : appFeedback.getFbComment());
			result.setFbStatus((appFeedback == null) ? "" : appFeedback.getParaFeedbackStatus()
					.getFeedbackStatus());
			result.setFbType((appFeedback == null) ? "" : appFeedback.getParametricFeedback()
					.getTrackingFeedbackType().getName());
			result.setIssuedby(appFeedback.getIssuedBy());
			result.setIssueTo(appFeedback.getIssuedTo());
			result.setIssuetype(appFeedback.getParametricFeedback().getParaIssueType()
					.getIssueType());
			result.setRecievedDate(sdf.format(appFeedback.getStoreDate()).toString());
			if(appFeedback.getParaFeedbackAction() != null)
			{
				result.setCAction((appFeedback.getParaFeedbackAction().getCAction() == null) ? ""
						: appFeedback.getParaFeedbackAction().getCAction());
				result.setPAction((appFeedback.getParaFeedbackAction().getPAction()) == null ? ""
						: appFeedback.getParaFeedbackAction().getPAction());
				result.setRootCause((appFeedback.getParaFeedbackAction().getRootCause()) == null ? ""
						: appFeedback.getParaFeedbackAction().getRootCause());
				Date date = appFeedback.getParaFeedbackAction().getActionDueDate();
				// SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				result.setActionDueDate(date == null ? "" : sdf.format(date));
			}
			if(taskType != null & !taskType.equals("All"))
			{
				if(!result.getFbStatus().equals(taskType))
					return null;
			}
			/** get Last comment **/
			if(appFeedback.getParametricFeedback().getTrackingFeedbackType().getName().equals("QA"))
			{
				Long qaUserId = ParaQueryUtil.getQAUserId(groupRecord.getPlFeature().getPl(),
						ParaQueryUtil.getTrackingTaskTypeByName("Approved Values", session));
				Criteria feedBCri = session.createCriteria(ParametricFeedbackCycle.class);
				feedBCri.add(Restrictions.eq("issuedBy", qaUserId));
				// feedBackCrit.add(Restrictions.eq("feedbackRecieved", 0l));
				feedBCri.add(Restrictions.eq("fbItemValue", groupRecord.getGroupFullValue()));
				feedBCri.addOrder(Order.desc("storeDate"));
				if(feedBCri.list() != null && !feedBCri.list().isEmpty())
					appFeedback = (ParametricFeedbackCycle) feedBCri.list().get(0);

				result.setQaComment((appFeedback == null) ? "" : appFeedback.getFbComment());
				result.setQaStatus((appFeedback == null) ? "" : appFeedback.getParaFeedbackStatus()
						.getFeedbackStatus());
				if(result.getIssuedby() == qaUserId)
				{
					result.setComment("");
					result.setFbStatus("");
				}
			}
			else
			{
				ParametricFeedbackCycle appFB = null;
				Criteria feedBCri = session.createCriteria(ParametricFeedbackCycle.class);
				feedBCri.add(Restrictions.eq("issuedBy", issuedTo));
				// feedBackCrit.add(Restrictions.eq("feedbackRecieved", 0l));
				feedBCri.add(Restrictions.eq("fbItemValue", groupRecord.getGroupFullValue()));
				feedBCri.addOrder(Order.desc("storeDate"));
				if(feedBCri.list() != null && !feedBCri.list().isEmpty())
				{
					appFB = (ParametricFeedbackCycle) feedBCri.list().get(0);
					result.setLastEngComment(appFB.getFbComment());
				}
				else
				{
					result.setLastEngComment("");
				}
			}
		}
		return result;
	}

	public static ArrayList<Object[]> getEngUnapprovedData(GrmUserDTO userDto, Date startDate,
			Date endDate, String type)
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
			List list2 = null;
			Long[] teamMembers = null;
			ParametricFeedbackCycle parametricFeedbackCycle = null;
			ParametricApprovedGroup parametricApprovedGroup = null;
			for(int i = 0; i < list.size(); i++)
			{

				parametricFeedbackCycle = (ParametricFeedbackCycle) list.get(i);
				criteria = session.createCriteria(ParametricApprovedGroup.class);
				criteria.add(Restrictions.eq("groupFullValue",
						parametricFeedbackCycle.getFbItemValue()));
				criteria.createAlias("status", "status");
				if(type.equals("Eng"))
				{
					criteria.add(Restrictions.eq("paraUserId", userDto.getId()));
					criteria.add(Restrictions.eq("status.name", StatusName.engFeedback));
				}

				if(type.equals("QA"))
				{
					criteria.add(Restrictions.eq("qaUserId", userDto.getId()));
					criteria.add(Restrictions.eq("status.name", StatusName.qaFeedback));
				}
				if(type.equals("TL"))
				{
					teamMembers = ParaQueryUtil.getTeamMembersIDByTL(userDto.getId());
					criteria.add(Restrictions.in("paraUserId", teamMembers));
					criteria.add(Restrictions.eq("status.name", StatusName.tlFeedback));
				}
				if(startDate != null && endDate != null)
				{
					criteria.add(Expression.between("storeDate", startDate, endDate));

				}

				list2 = criteria.list();
				if(type.equals("Eng"))
				{
					row = new Object[3];
					if(!list2.isEmpty())
					{
						parametricApprovedGroup = (ParametricApprovedGroup) list2.get(0);
						row[0] = parametricApprovedGroup.getPlFeature().getPl().getName();

						if(parametricApprovedGroup.getDocument() != null)
						{
							Set set = parametricApprovedGroup.getDocument()
									.getTrackingParametrics();
							if(!set.isEmpty())
							{
								Iterator it = set.iterator();
								TrackingParametric tp = (TrackingParametric) it.next();
								long statusId = tp.getTrackingTaskStatus().getId();
								if(statusId != 10 && statusId != 42)
								{
									continue;
								}
								row[1] = tp.getSupplier().getName();
							}
							else
							{
								row[1] = "All";
							}
						}
						else
						{
							row[1] = "All";
						}
						row[2] = parametricFeedbackCycle.getParaFeedbackStatus()
								.getFeedbackStatus();
						result.add(row);
					}
				}
				else
				{
					row = new Object[4];
					if(!list2.isEmpty())
					{
						parametricApprovedGroup = (ParametricApprovedGroup) list2.get(0);
						row[0] = parametricApprovedGroup.getPlFeature().getPl().getName();
						if(parametricApprovedGroup.getDocument() != null)
						{
							Set set = parametricApprovedGroup.getDocument()
									.getTrackingParametrics();
							if(!set.isEmpty())
							{
								Iterator it = set.iterator();
								TrackingParametric tp = (TrackingParametric) it.next();
								long statusId = tp.getTrackingTaskStatus().getId();
								if(statusId != 10 && statusId != 42)
								{
									continue;
								}
								row[1] = tp.getSupplier().getName();
							}
							else
							{
								row[1] = "All";
							}
						}
						else
						{
							row[1] = "All";
						}

						row[2] = parametricFeedbackCycle.getParaFeedbackStatus()
								.getFeedbackStatus();
						// row[2] = tp.getTrackingTaskType().getName();
						row[3] = parametricFeedbackCycle.getParametricFeedback()
								.getTrackingFeedbackType().getName();
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

	public static void replyApprovedValueFB(UnApprovedDTO app) throws Exception
	{
		Session session = SessionUtil.getSession();
		Criteria criteria;
		try
		{
			ParaFeedbackStatus paraFeedbackAction = null;
			ParaFeedbackStatus paraFeedbackStatus = null;
			ParaFeedbackAction feedbackAction = null;
			ParametricFeedback FBObj = new ParametricFeedback();
			ParametricFeedbackCycle FBCyc = new ParametricFeedbackCycle();
			Document document = null;
			ParaIssueType paraIssueType = null;

			document = ParaQueryUtil.getDocumnetByPdfUrl(app.getPdfUrl());

			ParametricApprovedGroup groups = ParaQueryUtil.getParametricApprovedGroup(
					app.getFeatureValue(), app.getPlName(), app.getFeatureName(), session);
			criteria = session.createCriteria(ParametricFeedbackCycle.class);
			criteria.add(Restrictions.eq("fbItemValue", groups.getGroupFullValue()));
			criteria.add(Restrictions.eq("issuedTo", app.getIssuedby()));
			criteria.add(Restrictions.eq("feedbackRecieved", 0l));

			ParametricFeedbackCycle parametricFeedbackCycle = (ParametricFeedbackCycle) criteria
					.uniqueResult();
			parametricFeedbackCycle.setFeedbackRecieved(1l);
			session.saveOrUpdate(parametricFeedbackCycle);

			criteria = session.createCriteria(ParaIssueType.class);
			System.out.println(app.getIssueType());
			criteria.add(Restrictions.eq("issueType", app.getIssueType()));
			paraIssueType = (ParaIssueType) criteria.uniqueResult();

			String fbStatus = StatusName.inprogress;
			String FBAction = app.getFbStatus();
			long fbRecieved = 0l;
			if(app.getFbStatus().equals(StatusName.fbClosed))
			{
				fbRecieved = 1l;
				fbStatus = StatusName.closed;
				FBAction = StatusName.accept;
			}
			if(app.getGruopSatus().equals(StatusName.reject))
			{
				fbRecieved = 1l;
				fbStatus = StatusName.closed;
			}

			criteria = session.createCriteria(ParaFeedbackStatus.class);
			System.out.println(app.getFbStatus());
			criteria.add(Restrictions.eq("feedbackStatus", FBAction));
			paraFeedbackAction = (ParaFeedbackStatus) criteria.uniqueResult();//

			criteria = session.createCriteria(ParaFeedbackStatus.class);
			criteria.add(Restrictions.eq("feedbackStatus", fbStatus));
			paraFeedbackStatus = (ParaFeedbackStatus) criteria.uniqueResult();//

			criteria = session.createCriteria(ParametricFeedbackCycle.class);
			criteria.add(Restrictions.eq("fbItemValue", groups.getGroupFullValue()));
			criteria.add(Restrictions.eq("issuedBy", app.getIssuedby()));
			criteria.add(Restrictions.eq("issuedTo", app.getIssueTo()));
			criteria.add(Restrictions.eq("feedbackRecieved", 0l));

			if(criteria.uniqueResult() == null)
			{
				FBObj = parametricFeedbackCycle.getParametricFeedback();
				FBObj.setParaFeedbackStatus(paraFeedbackStatus);
				FBObj.setDocument(document);
				if(paraIssueType != null)
					FBObj.setParaIssueType(paraIssueType);

				FBCyc.setId(System.nanoTime());
				FBCyc.setParametricFeedback(FBObj);
				FBCyc.setFbItemValue(groups.getGroupFullValue());
				FBCyc.setFbComment(app.getComment());
				FBCyc.setIssuedBy(app.getIssuedby());

				FBCyc.setIssuedTo(app.getIssueTo());

				FBCyc.setStoreDate(new Date());

				FBCyc.setParaFeedbackStatus(paraFeedbackAction);
				FBCyc.setFeedbackRecieved(fbRecieved);
				if(app.getCAction() != null && app.getPAction() != null
						&& app.getRootCause() != null && app.getActionDueDate() != null)
				{
					feedbackAction = getParaAction(app.getCAction(), app.getPAction(),
							app.getRootCause(), app.getActionDueDate(), session);
					if(feedbackAction != null)
					{
						FBCyc.setParaFeedbackAction(feedbackAction);
					}
				}
				session.saveOrUpdate(FBObj);
				session.saveOrUpdate(FBCyc);
			}
			criteria = session.createCriteria(TrackingTaskStatus.class);
			criteria.add(Restrictions.eq("name", app.getGruopSatus()));
			TrackingTaskStatus trackingTaskStatus = (TrackingTaskStatus) criteria.uniqueResult();//

			groups.setStatus(trackingTaskStatus);
			groups.setReviewedDate(new Date());
			session.saveOrUpdate(groups);

			// }catch(Exception e)
			// {
			// e.printStackTrace();
		}finally
		{
			session.close();
		}
	}

	public static ParaFeedbackAction getParaAction(String cAction, String pAction,
			String rootCause, String actionDueDate, Session session) throws ParseException
	{
		ParaFeedbackAction feedbackAction = null;
		// try
		// {
		Criteria criteria = null;
		criteria = session.createCriteria(ParaFeedbackAction.class);
		criteria.add(Restrictions.eq("CAction", cAction));
		criteria.add(Restrictions.eq("PAction", pAction));
		criteria.add(Restrictions.eq("rootCause", rootCause));
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		if(!actionDueDate.isEmpty())
		{
			Date date = sdf.parse(actionDueDate);
			System.out.println(date);
			criteria.add(Restrictions.eq("actionDueDate", date));
		}
		feedbackAction = (ParaFeedbackAction) criteria.uniqueResult();
		if(feedbackAction == null)
		{
			feedbackAction = new ParaFeedbackAction();
			feedbackAction.setId(System.nanoTime());
			feedbackAction.setCAction(cAction);
			feedbackAction.setPAction(pAction);
			feedbackAction.setRootCause(rootCause);
			if(!actionDueDate.isEmpty())
			{
				Date Acdate = sdf.parse(actionDueDate);
				System.out.println(Acdate);
				feedbackAction.setActionDueDate(Acdate);
			}
			session.saveOrUpdate(feedbackAction);
		}
		// }catch(Exception e)
		// {
		// e.printStackTrace();
		// }
		return feedbackAction;
	}

	public static boolean isThisDateValid(String dateToValidate, String dateFromat)
	{

		if(dateToValidate == null)
		{
			return false;
		}

		SimpleDateFormat sdf = new SimpleDateFormat(dateFromat);
		// sdf.setLenient(false);
		try
		{

			// if not valid, it will throw ParseException
			Date date = sdf.parse(dateToValidate);
			System.out.println(date);

		}catch(ParseException e)
		{
			return false;
		}

		return true;
	}

	public static ArrayList<UnApprovedDTO> getUnapprovedReviewData(Long[] userids, String engName,
			Date startDate, Date endDate, String plName, String supplierName, String status,
			String tsktype, String team, String Datatype, long issuedTo) throws Exception
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
			if(startDate != null)
			{
				startDate.setHours(0);
				startDate.setMinutes(0);
				startDate.setSeconds(0);
			}
			if(endDate != null)
			{
				endDate.setHours(0);
				endDate.setMinutes(0);
				endDate.setSeconds(0);
				endDate.setDate(endDate.getDate() + 1);
			}

			String userCol = "paraUserId";
			if(team.equals("QA"))
				userCol = "qaUserId";
			Criteria criteria = session.createCriteria(ParametricApprovedGroup.class, "group")
					.add(Restrictions.in(userCol, userids)).createAlias("status", "status")
					.add(Restrictions.eq("status.name", status));

			if(startDate != null && endDate != null)
			{

				if(!Datatype.equals("FB"))
				{
					if(team.equals("QA"))
					{
						criteria.add(Expression.between("reviewedDate", startDate, endDate));
					}
					else
					{
						criteria.add(Expression.between("storeDate", startDate, endDate));
					}
				}
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
			String trckparastatus = "document_id in (select document_id from tracking_parametric where TRACKING_TASK_STATUS_ID in("
					+ StatusName.doneFLagEngineId + "," + StatusName.inprogressId + ")) ";
			boolean sup = false;
			boolean typ = false;

			if(Datatype.equals("Data"))
			{
				if(!(engName.equals("")) && !engName.equals("All"))
				{
					criteria.add(Restrictions.eq("paraUserId",
							ParaQueryUtil.getUserIdByExactName(engName)));
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
					sql += "  SUPPLIER_ID in GETSUPPLIERID('" + supplierName + "')) ";
				}
				else
				{
					sql += " and SUPPLIER_ID in GETSUPPLIERID('" + supplierName + "')) ";
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
			criteria.add(Restrictions.sqlRestriction(trckparastatus));
			groups = criteria.list();

			// ArrayList<ArrayList<ParametricApprovedGroup>> re = new ArrayList<ArrayList<ParametricApprovedGroup>>();
			ArrayList<ParametricApprovedGroup> row = null;
			row = new ArrayList<ParametricApprovedGroup>();
			for(int h = 0; h < groups.size(); h++)
			{
				row.add((ParametricApprovedGroup) groups.get(h));
			}
			System.out.println("size is " + row.size());
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
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
				// unApprovedDTO.setPdfUrl(groupRecord.getDocument().getPdf().getSeUrl());
				Criteria SeparationCri = session.createCriteria(ParametricSeparationGroup.class);
				SeparationCri.add(Restrictions.eq("parametricApprovedGroup", row.get(j)));
				SeparationCri.addOrder(Order.asc("approvedValueOrder"));
				separationgroups = (List<ParametricSeparationGroup>) SeparationCri.list();
				if(separationgroups.isEmpty())
				{
					separationgroups = new ArrayList<ParametricSeparationGroup>();
				}
				if(Datatype.equals("FB"))
				{
					FBData = getFeedbackData(issuedTo, groupRecord, tsktype, startDate, endDate,
							session);
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
						unApprovedDTO.setIssueType(FBData.getIssuetype());
						unApprovedDTO.setCAction(FBData.getCAction());
						unApprovedDTO.setPAction(FBData.getPAction());
						unApprovedDTO.setRootCause(FBData.getRootCause());
						unApprovedDTO.setActionDueDate(FBData.getActionDueDate());
						unApprovedDTO.setReceivedDate(FBData.getRecievedDate());
					}
					else
					{
						continue;
					}
				}

				String featureUnit = (groupRecord.getPlFeature().getUnit() == null) ? ""
						: groupRecord.getPlFeature().getUnit().getName();
				unApprovedDTO.setFeatureUnit(featureUnit);
				String pl = (groupRecord.getPlFeature().getPl().getName() == null) ? ""
						: groupRecord.getPlFeature().getPl().getName();
				unApprovedDTO.setPlName(pl);
				String featureValue = (groupRecord.getGroupFullValue() == null) ? "" : groupRecord
						.getGroupFullValue();
				unApprovedDTO.setFeatureValue(featureValue.replace((char) 153, '™'));
				String featureName = (groupRecord.getPlFeature().getFeature().getName() == null) ? ""
						: groupRecord.getPlFeature().getFeature().getName();
				unApprovedDTO.setFeatureName(featureName);
				rdList = ParaQueryUtil.getParametricReviewData(groupRecord.getId(), session);
				unApprovedDTO.setUserId(groupRecord.getParaUserId());
				unApprovedDTO.setQaUserId(groupRecord.getQaUserId() == null ? 0l : groupRecord
						.getQaUserId());
				if(!rdList.isEmpty())
				{
					rd = (ParametricReviewData) rdList.get(0);
					unApprovedDTO.setPartNumber(rd.getComponent().getPartNumber());
					unApprovedDTO.setPdfUrl(rd.getComponent().getDocument().getPdf().getSeUrl());
					unApprovedDTO.setSupplier(rd.getComponent().getSupplierId().getName());
					if(team.equals("QA"))
					{
						if(!Datatype.equals("FB"))
						{
							if(groupRecord.getReviewedDate() != null)
							{
								unApprovedDTO.setReceivedDate(sdf.format(
										groupRecord.getReviewedDate()).toString());
							}
						}
					}
					else
					{
						if(!Datatype.equals("FB"))
						{
							if(rd.getStoreDate() != null)
							{
								unApprovedDTO.setReceivedDate(sdf
										.format(groupRecord.getStoreDate()).toString());
							}
						}
					}
				}
				else
				{
					unApprovedDTO.setPartNumber("");
					unApprovedDTO.setPdfUrl((groupRecord.getDocument() != null) ? groupRecord
							.getDocument().getPdf().getSeUrl() : "");
				}
				for(int k = 0; k < separationgroups.size(); k++)
				{
					separationgroup = separationgroups.get(k);
					approvedValue = separationgroup.getApprovedParametricValue();
					fetValue += approvedValue.getFromValue().getValue();
					signValue += (separationgroup.getApprovedParametricValue().getFromSign() == null) ? ""
							: separationgroup.getApprovedParametricValue().getFromSign().getName();
					typeValue += (separationgroup.getApprovedParametricValue().getFromValueType() == null) ? ""
							: separationgroup.getApprovedParametricValue().getFromValueType()
									.getName();
					conditionValue += (separationgroup.getApprovedParametricValue()
							.getFromCondition() == null) ? "" : separationgroup
							.getApprovedParametricValue().getFromCondition().getName();

					if(approvedValue.getFromMultiplierUnit() != null)
					{
						multiplierValue += (separationgroup.getApprovedParametricValue()
								.getFromMultiplierUnit().getMultiplier() == null) ? ""
								: separationgroup.getApprovedParametricValue()
										.getFromMultiplierUnit().getMultiplier().getName();
						unitValue += (separationgroup.getApprovedParametricValue()
								.getFromMultiplierUnit().getUnit() == null) ? "" : separationgroup
								.getApprovedParametricValue().getFromMultiplierUnit().getUnit()
								.getName();
					}
					if(approvedValue.getToValue() != null)
					{

						fetValue += " to " + approvedValue.getToValue().getValue();
						if(approvedValue.getFromSign() != null)
						{
							String s = approvedValue.getFromSign().getName();
							if(!s.equals(" to "))
								signValue += (approvedValue.getToSign() == null) ? " to " : " to "
										+ approvedValue.getToSign().getName();
						}
						if(approvedValue.getFromValueType() != null)
							typeValue += (approvedValue.getToValueType() == null) ? " to " : " to "
									+ approvedValue.getToValueType().getName();
						if(approvedValue.getFromCondition() != null)
							conditionValue += (approvedValue.getToCondition() == null) ? " to "
									: " to " + approvedValue.getToCondition().getName();
						if(approvedValue.getToMultiplierUnit() != null)
						{
							if(approvedValue.getFromMultiplierUnit().getMultiplier() != null)
								multiplierValue += (approvedValue.getToMultiplierUnit()
										.getMultiplier() == null) ? " to " : " to "
										+ approvedValue.getToMultiplierUnit().getMultiplier()
												.getName();
							if(approvedValue.getFromMultiplierUnit().getUnit() != null)
								unitValue += (approvedValue.getToMultiplierUnit().getUnit() == null) ? " to "
										: " to "
												+ approvedValue.getToMultiplierUnit().getUnit()
														.getName();
						}
					}

					pattern = (separationgroup.getPattern() == null) ? "" : separationgroup
							.getPattern();
					String patterns[] = pattern.split("\\$");
					if(patterns.length == 6)
					{
						fetValue += (patterns[0].contains(" to ")) ? " to " : patterns[0].trim();
						signValue += (patterns[1].contains(" to ")) ? " to " : patterns[1].trim();
						conditionValue += (patterns[2].contains(" to ")) ? " to " : patterns[2]
								.trim();
						typeValue += (patterns[3].contains(" to ")) ? " to " : patterns[3].trim();
						multiplierValue += (patterns[4].contains(" to ")) ? " to " : patterns[4]
								.trim();
						unitValue += (patterns[5].contains(" to ")) ? " to " : patterns[5].trim();
					}

					// rdList = ParaQueryUtil.getParametricReviewData(groupRecord.getId(), session);
					//
					// if(!rdList.isEmpty())
					// {
					// rd = (ParametricReviewData) rdList.get(0);
					// unApprovedDTO.setPartNumber(rd.getComponent().getPartNumber());
					// unApprovedDTO
					// .setPdfUrl(rd.getComponent().getDocument().getPdf().getSeUrl());
					// unApprovedDTO.setSupplier(rd.getComponent().getSupplierId().getName());
					// if(team.equals("QA"))
					// {
					// if(!Datatype.equals("FB"))
					// {
					// if(groupRecord.getReviewedDate() != null)
					// {
					// unApprovedDTO.setReceivedDate(sdf.format(
					// groupRecord.getReviewedDate()).toString());
					// }
					// }
					// }
					// else
					// {
					// if(!Datatype.equals("FB"))
					// {
					// if(rd.getStoreDate() != null)
					// {
					// unApprovedDTO.setReceivedDate(sdf.format(
					// groupRecord.getStoreDate()).toString());
					// }
					// }
					// }
					// }
					// else
					// {
					// unApprovedDTO.setPartNumber("");
					// unApprovedDTO.setPdfUrl((groupRecord.getDocument() != null) ? groupRecord
					// .getDocument().getPdf().getSeUrl() : "");
					// }
					// String featureUnit = (groupRecord.getPlFeature().getUnit() == null) ? ""
					// : groupRecord.getPlFeature().getUnit().getName();
					// unApprovedDTO.setFeatureUnit(featureUnit);
					// String pl = (groupRecord.getPlFeature().getPl().getName() == null) ? ""
					// : groupRecord.getPlFeature().getPl().getName();
					// unApprovedDTO.setPlName(pl);
					// String featureValue = (groupRecord.getGroupFullValue() == null) ? ""
					// : groupRecord.getGroupFullValue();
					// unApprovedDTO.setFeatureValue(featureValue.replace((char) 153, '™'));
					// String featureName = (groupRecord.getPlFeature().getFeature().getName() == null) ? ""
					// : groupRecord.getPlFeature().getFeature().getName();
					// unApprovedDTO.setFeatureName(featureName);

					String fromSign = (separationgroup.getApprovedParametricValue().getFromSign() == null) ? ""
							: separationgroup.getApprovedParametricValue().getFromSign().getName();

					if(!fetValue.replace("[|/!]", "").trim().equals(""))
					{
						unApprovedDTO.setValue(fetValue.replace((char) 153, '™'));
					}
					else
					{
						unApprovedDTO.setValue("");
					}
					if(!signValue.replace("|", "").replace("/", "").replace("!", "").trim()
							.equals(""))
					{
						unApprovedDTO.setSign(signValue);
					}
					else
					{
						unApprovedDTO.setSign("");
					}
					if(!multiplierValue.replace("|", "").replace("/", "").replace("!", "").trim()
							.equals(""))
					{
						unApprovedDTO.setMultiplier(multiplierValue);
					}
					else
					{
						unApprovedDTO.setMultiplier("");
					}
					if(!typeValue.replace("|", "").replace("/", "").replace("!", "").trim()
							.equals(""))
					{
						unApprovedDTO.setType(typeValue);
					}
					else
					{
						unApprovedDTO.setType("");
					}
					if(!conditionValue.replace("|", "").replace("/", "").replace("!", "").trim()
							.equals(""))
					{
						unApprovedDTO.setCondition(conditionValue);
					}
					else
					{
						unApprovedDTO.setCondition("");
					}
					if(!unitValue.replace("|", "").replace("/", "").replace("!", "").trim()
							.equals(""))
					{
						unApprovedDTO.setUnit(unitValue);
					}
					else
					{
						unApprovedDTO.setUnit("");
					}

				}
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

	public static List<String> validateSeparation(ArrayList<String> row)
	{
		Session session = SessionUtil.getSession();
		List<String> result = new ArrayList<>();
		try
		{
			String error = "";
			String unitstring = "true";
			String plname = row.get(0);
			String fetName = row.get(5);
			String fetValue = row.get(6);
			String sign = row.get(8);
			String value = row.get(9);
			String type = row.get(10);
			String multiplier = row.get(12);
			String condition = row.get(11);
			String unit = row.get(13);

			boolean toflag = false;
			if(value.contains(" to "))
				toflag = true;
			ArrayList<String[]> valueSections = getSeparatedSections(value, toflag);
			ArrayList<String[]> signSections = getSeparatedSections(sign, toflag);
			ArrayList<String[]> multipSections = getSeparatedSections(multiplier, toflag);
			ArrayList<String[]> unitSections = getSeparatedSections(unit, toflag);
			ArrayList<String[]> condSections = getSeparatedSections(condition, toflag);
			ArrayList<String[]> valueTypeSections = getSeparatedSections(type, toflag);

			String[] valueArr = valueSections.get(0);
			String[] signArr = signSections.get(0);
			String[] multiplierArr = multipSections.get(0);
			String[] unitArr = unitSections.get(0);
			String[] conditionArr = condSections.get(0);
			String[] valueTypeArr = valueTypeSections.get(0);

			int multiValCount = valueArr.length;
			System.out.println("multiValCount+++++++++++ " + multiValCount);
			if(!sign.trim().equals("") && signArr.length > multiValCount)
			{
				unitstring = "false";
				error += " |Error number of sticks in sign. \nPlease enter a valid sign";
			}
			if(!multiplier.trim().equals("") && multiplierArr.length > multiValCount)
			{
				unitstring = "false";
				error += " |Error number of sticks in multiplier. \nPlease enter a valid multiplier";
			}
			if(!unit.trim().equals("") && unitArr.length > multiValCount)
			{
				unitstring = "false";
				error += " |Error number of sticks in unit. \nPlease enter a valid unit";
			}
			if(!condition.trim().equals("") && conditionArr.length > multiValCount)
			{
				unitstring = "false";
				error += " |Error number of sticks in condition. \nPlease enter a valid condition";
			}
			if(!type.trim().equals("") && valueTypeArr.length > multiValCount)
			{
				unitstring = "false";
				error += " |Error number of sticks in value type. \nPlease enter a valid value type";
			}

			if(value.equalsIgnoreCase("N/A") || value.equalsIgnoreCase("N/R")
					|| value.equalsIgnoreCase("9999"))
			{
				if(!sign.isEmpty() || !type.isEmpty() || !multiplier.isEmpty()
						|| !condition.isEmpty() || !unit.isEmpty())
				{
					unitstring = "false";
					error += " |All the separation column should be Null except value ";
				}
			}
			else if(value.contains("(Min)") || value.contains("(Max)") || value.contains("(Typ)"))
			{
				unitstring = "false";
				error += " |The Value contains \"(Min), (Typ), (Max)\"";
			}
			for(int i = 0; i < conditionArr.length; i++)
			{
				if(!conditionArr[i].isEmpty() && !conditionArr[i].trim().startsWith("@"))
				{
					unitstring = "false";
					error += " |Condition should start with \"@\"";
					break;
				}
			}
			for(int i = 0; i < unitArr.length; i++)
			{
				if(checkPlFetUnit(plname, fetName, fetValue, unitArr[i], session) == false)

				{
					if(error.isEmpty())
					{
						unitstring = "true";
					}
					error += " |Unit differs with CP unit";
					break;
				}
			}
			result.add(error);
			result.add(unitstring);

			// }catch(Exception e)
			// {
			// e.printStackTrace();
			// return result;
		}finally
		{
			session.close();
		}
		return result;
	}

	private static boolean checkPlFetUnit(String plName, String fetName, String fetValue,
			String unit, Session session)
	{
		boolean equal = false;
		Criteria cri = session.createCriteria(PlFeature.class);
		cri.createAlias("pl", "pl");
		cri.createAlias("feature", "feature");
		cri.add(Restrictions.eq("pl.name", plName));
		cri.add(Restrictions.eq("feature.name", fetName));
		PlFeature plfet = (PlFeature) cri.uniqueResult();
		Unit Clsunit = plfet.getUnit();
		System.out.println("input : " + unit);
		if(Clsunit != null)
		{
			if(Clsunit.getName().trim().equalsIgnoreCase(unit.trim()))
			{
				equal = true;
			}
		}
		else if(Clsunit == null && unit.isEmpty())
		{
			equal = true;
		}
		return equal;

	}

	public static ArrayList<ArrayList<String>> getFeedbackHistory(String url)
	{
		Session session = SessionUtil.getSession();
		ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
		List<Object[]> list = session
				.createSQLQuery(
						"SELECT   GETPDFURLBYDOC (x.DOCUMENT_ID),y.FB_ITEM_VALUE,GETUSERNAME (x.FB_INITIATOR),getissueTo (y.PARA_FEEDBACK_ID), getissueType (x.ISSUE_TYPE) issueType, getfeedbackTypt (x.FEEDBACK_TYPE) fedtype, y.FB_COMMENT, getfeedbackstatus (x.FEEDBACK_STATUS) statusType, MAX (x.STORE_DATE), getfeedbackAction (y.ACTION_ID) action FROM PARAMETRIC_FEEDBACK x, PARAMETRIC_FEEDBACK_CYCLE y WHERE   x.id = Y.PARA_FEEDBACK_ID AND x.DOCUMENT_ID = GET_DOCUMENT_ID_by_url('"
								+ url
								+ "') GROUP BY   x.DOCUMENT_ID,x.FB_INITIATOR,y.PARA_FEEDBACK_ID,x.ISSUE_TYPE,x.FEEDBACK_STATUS,x.FEEDBACK_TYPE,y.FB_ITEM_VALUE, y.FB_COMMENT, y.ACTION_ID")
				.list();
		Object[] row = null;
		ArrayList<String> rowData = null;
		for(int i = 0; i < list.size(); i++)
		{
			row = list.get(i);
			rowData = new ArrayList<String>();
			for(int j = 0; j < row.length; j++)
			{
				System.out.println(j);
				if(j == (row.length - 1))
				{
					if(row[j] == null)
					{
						for(int k = 0; k < 4; k++)
						{
							rowData.add("");
						}
					}
					else
					{
						String[] action = row[j].toString().split("\\|");
						for(int k = 0; k < action.length; k++)
						{
							rowData.add(action[k]);
						}
					}
				}
				else
				{
					rowData.add((row[j] == null) ? "" : row[j].toString());
				}
			}
			result.add(rowData);
		}
		return result;
	}
}
