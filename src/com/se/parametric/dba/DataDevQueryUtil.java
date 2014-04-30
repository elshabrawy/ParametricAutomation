package com.se.parametric.dba;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JOptionPane;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;

import com.se.automation.db.CloneUtil;
import com.se.automation.db.QueryUtil;
import com.se.automation.db.SessionUtil;
import com.se.automation.db.client.dto.QAChecksDTO;
import com.se.automation.db.client.mapping.Document;
import com.se.automation.db.client.mapping.DocumentFeedback;
import com.se.automation.db.client.mapping.Family;
import com.se.automation.db.client.mapping.FamilyCross;
import com.se.automation.db.client.mapping.Feature;
import com.se.automation.db.client.mapping.GenericFamily;
import com.se.automation.db.client.mapping.MapGeneric;
import com.se.automation.db.client.mapping.MasterFamilyGeneric;
import com.se.automation.db.client.mapping.MasterPartMask;
import com.se.automation.db.client.mapping.ParaFeedbackAction;
import com.se.automation.db.client.mapping.ParaFeedbackFets;
import com.se.automation.db.client.mapping.ParaFeedbackStatus;
import com.se.automation.db.client.mapping.ParaIssueType;
import com.se.automation.db.client.mapping.ParaSummaryStatus;
import com.se.automation.db.client.mapping.ParametricApprovedGroup;
import com.se.automation.db.client.mapping.ParametricFeedback;
import com.se.automation.db.client.mapping.ParametricFeedbackCycle;
import com.se.automation.db.client.mapping.ParametricReviewData;
import com.se.automation.db.client.mapping.PartComponent;
import com.se.automation.db.client.mapping.PartMaskValue;
import com.se.automation.db.client.mapping.PartMaskValueId;
import com.se.automation.db.client.mapping.PartsFeedback;
import com.se.automation.db.client.mapping.PartsParametricValuesGroup;
import com.se.automation.db.client.mapping.Pl;
import com.se.automation.db.client.mapping.PlFeature;
import com.se.automation.db.client.mapping.Supplier;
import com.se.automation.db.client.mapping.SupplierPl;
import com.se.automation.db.client.mapping.TblNpiParts;
import com.se.automation.db.client.mapping.TrackingFeedbackType;
import com.se.automation.db.client.mapping.TrackingParametric;
import com.se.automation.db.client.mapping.TrackingTaskStatus;
import com.se.automation.db.client.mapping.TrackingTaskType;
import com.se.grm.client.mapping.GrmUser;
import com.se.parametric.AppContext;
import com.se.parametric.dto.GrmUserDTO;
import com.se.parametric.dto.PartInfoDTO;
import com.se.parametric.dto.SummaryDTO;
import com.se.parametric.dto.TableInfoDTO;
import com.se.parametric.util.StatusName;

public class DataDevQueryUtil
{

	public static List<String> getAllPlNames()
	{
		List<String> plNames = null;
		Session session = SessionUtil.getSession();
		try
		{
			SQLQuery query = session.createSQLQuery("select name from pl where is_pl=1 order by name");
			plNames = query.list();
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}finally
		{
			session.close();

		}
		return plNames;
	}

	public static ArrayList<Object[]> getUserData(GrmUserDTO grmUser, Date startDate, Date endDate)
	{
		Long UserID = grmUser.getId();
		ArrayList<Object[]> result = new ArrayList<Object[]>();
		ArrayList<String> row = null;
		String start = "";
		String end = "";
		if(startDate != null)
		{
			start = new SimpleDateFormat("MM/dd/yyyy").format(startDate);

		}
		if(endDate != null)
		{
			end = new SimpleDateFormat("MM/dd/yyyy").format(endDate);
		}
		Session session = SessionUtil.getSession();
		String sql = "";
		if(startDate == null && endDate == null)
		{
			sql = "select distinct p.name pl, s.name supplier, ttt.name type ,EXTRACTION_STATUS, TP.PRIORIY from Tracking_Parametric tp, pl p, supplier s, tracking_task_type ttt where tp.pl_id = p.id and tp.supplier_id = s.id and tp.tracking_task_type_id = ttt.id and user_id = "
					+ UserID + " and tp.TRACKING_TASK_STATUS_ID=6 and tp.tracking_task_type_id <> 15 group by  p.name  , s.name  , ttt.name ,EXTRACTION_STATUS, TP.PRIORIY order by pl, supplier, type, TP.PRIORIY";

			System.out.println("Server Mesage   " + sql);
		}
		else
		{
			sql = "select distinct p.name pl, s.name supplier, ttt.name type ,EXTRACTION_STATUS, TP.PRIORIY from Tracking_Parametric tp, pl p, supplier s, tracking_task_type ttt where tp.pl_id = p.id and tp.supplier_id = s.id and tp.tracking_task_type_id = ttt.id and user_id = "
					+ UserID
					+ " and tp.TRACKING_TASK_STATUS_ID=6 and tp.tracking_task_type_id <> 15 AND TP.ASSIGNED_DATE BETWEEN TO_DATE('"
					+ start
					+ "', 'MM/DD/YYYY') AND TO_DATE('"
					+ end
					+ "', 'MM/DD/YYYY') group by  p.name  , s.name  , ttt.name ,EXTRACTION_STATUS, TP.PRIORIY order by pl, supplier, type, TP.PRIORIY ";
			System.out.println("Server Mesage   " + sql);

		}
		result = (ArrayList<Object[]>) session.createSQLQuery(sql).list();
		for(int i = 0; i < result.size(); i++)
		{
			Object[] data = result.get(i);
			row = new ArrayList<String>();
			data[3] = (data[3] == null || data[3].equals(new BigDecimal(0))) ? "Not Extracted" : "Extracted";
			data[4] = (data[4] == null) ? "" : data[4].toString();
			result.set(i, data);
		}
		session.close();
		return result;
	}

	public static ArrayList<Object[]> getQAReviewFilterData(GrmUserDTO grmUser)
	{
		Long UserID = grmUser.getId();
		ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
		ArrayList<String> row = null;
		ArrayList<Object[]> list2 = null;
		Session session = SessionUtil.getSession();
		try
		{
			TrackingTaskStatus taskStatus = null;
			Criteria cri = session.createCriteria(TrackingTaskStatus.class);
			cri.add(Restrictions.eq("name", StatusName.qaReview));
			taskStatus = (TrackingTaskStatus) cri.uniqueResult();

			String Sql = " SELECT DISTINCT p.name pl,AUTOMATION2.Get_PL_Type(P.ID ), s.name supplier, tt";
			Sql = Sql + "t.name TYPE, U.FULL_NAME user_Name,'" + StatusName.waitingsummary + "' FROM Tracking_Parametric tp, pl p, supplier";
			Sql = Sql + " s, tracking_task_type ttt, grm.GRM_USER u, TRACKING_TASK_STATUS st WHERE tp.p";
			Sql = Sql + "l_id = p.id AND tp.TRACKING_TASK_STATUS_ID IN (" + taskStatus.getId() + ") AND tp.supplier_id = s.id AN";
			Sql = Sql + "D tp.tracking_task_type_id = ttt.id AND u.id = tp.user_id AND st.id = tp.TRACK";
			Sql = Sql + "ING_TASK_STATUS_ID and QA_USER_ID=" + grmUser.getId() + " GROUP BY p.name, s.name, ttt.name, U.FULL";
			Sql = Sql + "_NAME, st.NAME, AUTOMATION2.Get_PL_Type(P.ID )";
			list2 = (ArrayList<Object[]>) session.createSQLQuery(Sql).list();
		}finally
		{
			session.close();
		}
		return list2;

	}

	public static ArrayList<Object[]> getUserNPIData(GrmUserDTO grmUser, Date startDate, Date endDate)
	{
		String start = "";
		String end = "";
		if(startDate != null)
		{
			start = new SimpleDateFormat("MM/dd/yyyy").format(startDate);

		}
		if(endDate != null)
		{
			end = new SimpleDateFormat("MM/dd/yyyy").format(endDate);
		}
		Long UserID = grmUser.getId();
		ArrayList<Object[]> result = new ArrayList<Object[]>();
		ArrayList<String> row = null;
		// row = new ArrayList<Object>();
		// row.add(grmUser);
		// result.add(row);
		Session session = SessionUtil.getSession();
		String sql = "";
		if(startDate == null && endDate == null)
		{
			sql = "select distinct p.name pl, s.name supplier, ttt.name type ,EXTRACTION_STATUS, TP.PRIORIY from Tracking_Parametric tp, pl p, supplier s, tracking_task_type ttt where tp.pl_id = p.id and tp.supplier_id = s.id and tp.tracking_task_type_id = ttt.id and user_id = "
					+ UserID + " and tp.TRACKING_TASK_STATUS_ID=6  and tp.tracking_task_type_id = 15 group by  p.name  , s.name  , ttt.name ,EXTRACTION_STATUS, TP.PRIORIY order by pl, supplier, type, TP.PRIORIY";
		}
		else
		{
			sql = "select distinct p.name pl, s.name supplier, ttt.name type ,EXTRACTION_STATUS, TP.PRIORIY from Tracking_Parametric tp, pl p, supplier s, tracking_task_type ttt where tp.pl_id = p.id and tp.supplier_id = s.id and tp.tracking_task_type_id = ttt.id and user_id = "
					+ UserID
					+ " and tp.TRACKING_TASK_STATUS_ID=6  and tp.tracking_task_type_id = 15 AND ASSIGNED_DATE BETWEEN TO_DATE('"
					+ start
					+ "', 'MM/DD/YYYY') AND TO_DATE('"
					+ end
					+ "', 'MM/DD/YYYY') group by  p.name  , s.name  , ttt.name ,EXTRACTION_STATUS, TP.PRIORIY order by pl, supplier, type, TP.PRIORIY";
		}

		System.out.println("Server Mesage   " + sql);
		result = (ArrayList<Object[]>) session.createSQLQuery(sql).list();
		for(int i = 0; i < result.size(); i++)
		{
			Object[] data = result.get(i);
			row = new ArrayList<String>();
			// for (int j = 0; j < 3; j++) {
			//
			// row.add((data[j] == null) ? "" : data[j].toString());
			// }
			data[3] = (data[3] == null || data[3].equals(new BigDecimal(0))) ? "Not Extracted" : "Extracted";
			data[4] = (data[4] == null) ? "" : data[4].toString();
			// row.add((data[3] == null) ? "Not Extracted" : "Extracted");
			result.set(i, data);

		}
		session.close();
		return result;
	}

	public static ArrayList<Object[]> getUserFeedbackData(GrmUserDTO grmUser, Date startDate, Date endDate)
	{
		long UserID = grmUser.getId();
		ArrayList<Object[]> result = new ArrayList<Object[]>();
		String start = "";
		String end = "";
		TrackingTaskStatus taskStatus = null;
		if(startDate != null)
		{
			start = new SimpleDateFormat("MM/dd/yyyy").format(startDate);

		}
		if(endDate != null)
		{
			end = new SimpleDateFormat("MM/dd/yyyy").format(endDate);
		}
		Session session = SessionUtil.getSession();
		Criteria cri = session.createCriteria(TrackingTaskStatus.class);
		cri.add(Restrictions.eq("name", StatusName.engFeedback));
		taskStatus = (TrackingTaskStatus) cri.uniqueResult();

		String sql = "";
		if(startDate == null && endDate == null)
		{
			sql = " SELECT DISTINCT PL.NAME pl_name, s.name supplier_name, TFT.NAME feedback_name";
			sql = sql + ", U.FULL_NAME FROM tracking_parametric tp, pl, supplier s, PARAMETRIC_FEEDBACK";
			sql = sql + "_CYCLE FBc, PARAMETRIC_FEEDBACK FB, part_component c, grm.grm_user u, tracking";
			sql = sql + "_feedback_type tft WHERE TP.USER_ID = " + UserID + " AND TP.TRACKING_TASK_STATUS_ID = " + taskStatus.getId() + " ";
			sql = sql + "AND TP.PL_ID = PL.ID AND TP.SUPPLIER_ID = S.ID AND TP.DOCUMENT_ID = C.DOCUMENT";
			sql = sql + "_ID AND C.COM_ID = FB.ITEM_ID AND FBc.ISSUED_BY = U.ID AND FB.FEEDBACK_TYPE = ";
			sql = sql + "TFT.ID AND FBc.FEEDBACK_RECIEVED = 0 AND FBc.ISSUED_TO = " + UserID + " AND FB.ID = FBC.P";
			sql = sql + "ARA_FEEDBACK_ID";
		}
		else
		{
			sql = " SELECT DISTINCT PL.NAME pl_name, s.name supplier_name, TFT.NAME feedback_name";
			sql = sql + ", U.FULL_NAME FROM tracking_parametric tp, pl, supplier s, PARAMETRIC_FEEDBACK";
			sql = sql + "_CYCLE FBc, PARAMETRIC_FEEDBACK FB, part_component c, grm.grm_user u, tracking";
			sql = sql + "_feedback_type tft WHERE TP.USER_ID = " + UserID + " AND TP.TRACKING_TASK_STATUS_ID = " + taskStatus.getId() + " ";
			sql = sql + "AND TP.PL_ID = PL.ID AND TP.SUPPLIER_ID = S.ID AND TP.DOCUMENT_ID = C.DOCUMENT";
			sql = sql + "_ID AND C.COM_ID = FB.ITEM_ID AND FBc.ISSUED_BY = U.ID AND FB.FEEDBACK_TYPE = ";
			sql = sql + "TFT.ID AND FBc.FEEDBACK_RECIEVED = 0 AND tp.FINISHED_DATE BETWEEN TO_DATE ('" + startDate + "', 'MM/DD/YYYY')";
			sql = sql + " AND TO_DATE ('" + endDate + "', 'MM/DD/YYYY') AND FBc.ISSUE";
			sql = sql + "D_TO = " + UserID + " AND FB.ID = FBC.PARA_FEEDBACK_ID";
		}

		System.out.println("Server Mesage   " + sql);
		result = (ArrayList<Object[]>) session.createSQLQuery(sql).list();
		session.close();
		return result;

	}

	public static Long[] getIssuersTo(long userId)
	{

		Long[] result = null;
		Session session = SessionUtil.getSession();
		try
		{
			SQLQuery query = session.createSQLQuery("select distinct issued_by_id from parts_feedback where feedback_recieved=0 and issued_to_id=" + userId);
			List<BigDecimal> list = query.list();
			if(list != null)
			{
				result = new Long[list.size()];
				for(int i = 0; i < list.size(); i++)
				{
					long id = list.get(i).longValue();
					result[i] = id;
				}
			}
		}finally
		{
			session.close();
		}
		return result;

	}

	public static ArrayList<Object[]> getTLReviewFilterData(GrmUserDTO grmUser, Date startDate, Date endDate)
	{
		Long UserID = grmUser.getId();
		String start = "";
		String end = "";
		if(startDate != null)
		{
			start = new SimpleDateFormat("MM/dd/yyyy").format(startDate);

		}
		if(endDate != null)
		{
			end = new SimpleDateFormat("MM/dd/yyyy").format(endDate);
		}
		ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
		ArrayList<String> row = null;
		ArrayList<Object[]> list2 = null;
		TrackingTaskStatus taskStatus = null;
		Session session = SessionUtil.getSession();
		try
		{
			Criteria cri = session.createCriteria(TrackingTaskStatus.class);
			cri.add(Restrictions.eq("name", StatusName.tlReview));
			taskStatus = (TrackingTaskStatus) cri.uniqueResult();
			String SqlStatement = "";
			if(startDate == null && endDate == null)
			{
				SqlStatement = " SELECT DISTINCT p.name pl, s.name supplier, ttt.name TYPE, U.FULL_NAME user_N";
				SqlStatement = SqlStatement + "ame FROM Tracking_Parametric tp, pl p, supplier s, trackin";
				SqlStatement = SqlStatement + "g_task_type ttt, grm.GRM_USER u, TRACKING_TASK_STATUS st WHERE tp.pl_id = p.id";
				SqlStatement = SqlStatement + " AND tp.TRACKING_TASK_STATUS_ID IN (" + taskStatus.getId() + ") AND tp.supplier_id = s.id AND tp.tracki";
				SqlStatement = SqlStatement + "ng_task_type_id = ttt.id AND u.id = tp.user_id AND st.id = tp.TRACKING_TASK_ST";
				SqlStatement = SqlStatement + "ATUS_ID AND tp.user_id IN (SELECT id FROM grm.GRM_USER WHERE Leader = " + UserID + ") GROU";
				SqlStatement = SqlStatement + "P BY p.name, s.name, ttt.name, U.FULL_NAME, st.NAME";
			}
			else
			{
				SqlStatement = " SELECT DISTINCT p.name pl, s.name supplier, ttt.name TYPE, U.FULL_NAME user_N";
				SqlStatement = SqlStatement + "ame FROM Tracking_Parametric tp, pl p, supplier s, trackin";
				SqlStatement = SqlStatement + "g_task_type ttt, grm.GRM_USER u, TRACKING_TASK_STATUS st WHERE tp.pl_id = p.id";
				SqlStatement = SqlStatement + " AND tp.TRACKING_TASK_STATUS_ID IN (" + taskStatus.getId() + ") AND tp.supplier_id = s.id AND tp.tracki";
				SqlStatement = SqlStatement + "ng_task_type_id = ttt.id AND u.id = tp.user_id AND st.id = tp.TRACKING_TASK_ST";
				SqlStatement = SqlStatement + "ATUS_ID AND tp.user_id IN (SELECT id FROM grm.GRM_USER WHERE Leader = " + UserID + ")";
				SqlStatement = SqlStatement + "AND TP.ASSIGNED_DATE BETWEEN TO_DATE('" + start + "', 'MM/DD/YYYY') AND TO_DATE('" + end + "', 'MM/DD/YYYY') GROUP BY   p.name, s.name, ttt.name, U.FULL_NAME, st.NAME";

			}
			list2 = (ArrayList<Object[]>) session.createSQLQuery(SqlStatement).list();
			// for (int i = 0; i < list2.size(); i++) {
			// Object[] data = list2.get(i);
			// row = new ArrayList<String>();
			// for (int j = 0; j < 5; j++) {
			// row.add((data[j] == null) ? "" : data[j].toString());
			// // System.out.println((data[j] == null) ? "" : data[j].toString());
			// }
			// // row.add((data[3] == null) ? "Not Extracted" : "Extracted");
			// result.add(row);
			// }
		}finally
		{
			session.close();
		}
		return list2;

	}

	public static ArrayList<Object[]> getTLFeedbackFilterData(GrmUserDTO grmUser, Date startDate, Date endDate)
	{
		long UserID = grmUser.getId();
		String start = "";
		String end = "";
		if(startDate != null)
		{
			start = new SimpleDateFormat("MM/dd/yyyy").format(startDate);

		}
		if(endDate != null)
		{
			end = new SimpleDateFormat("MM/dd/yyyy").format(endDate);
		}
		ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
		ArrayList<String> row = null;
		ArrayList<Object[]> list2 = null;
		Session session = SessionUtil.getSession();
		try
		{

			// String sql = " SELECT DISTINCT p.name pl, s.name supplier, U.FULL_NAME user_Name, tft.name feedback_type "
			// + " FROM Tracking_Parametric tp, pl p, supplier s, grm.GRM_USER u, parts_feedback pf, "
			// + " tracking_feedback_type tft WHERE tp.pl_id = p.id AND tp.TRACKING_TASK_STATUS_ID = 28 "
			// + " AND tp.supplier_id = s.id AND u.id = tp.user_id  AND u.id = pf.issued_by_id AND pf.issued_to_id = "
			// + grmUser.getId() + " AND pf.feedback_recieved = 0 " + " AND pf.feedback_type = tft.id " +
			// // "AND tp.user_id IN (SELECT id FROM grm.GRM_USER WHERE Leader = " + grmUser.getId()+ ") "
			// " GROUP BY p.name, s.name, U.FULL_NAME, tft.name ";
			String Sql = "";
			if(startDate == null && endDate == null)
			{
				Sql = " SELECT DISTINCT pl.name pl_name, s.name supplier_name, u.full_name issuer_nam";
				Sql = Sql + "e, TFT.NAME feedback_name FROM tracking_parametric tp, pl, supplier s, PARAMET";
				Sql = Sql + "RIC_FEEDBACK_CYCLE FBc, PARAMETRIC_FEEDBACK FB, part_component c, grm.grm_user";
				Sql = Sql + " u, tracking_feedback_type tft WHERE TP.TRACKING_TASK_STATUS_ID = 28 AND TP.PL";
				Sql = Sql + "_ID = pl.id AND TP.USER_ID IN (SELECT id FROM grm.grm_user WHERE leader = " + UserID + ") ";
				Sql = Sql + "AND TP.SUPPLIER_ID = s.id AND FB.ITEM_ID = c.com_id AND C.DOCUMENT_ID = TP.DOC";
				Sql = Sql + "UMENT_ID AND FBC.ISSUED_TO = " + UserID + " AND FBC.FEEDBACK_RECIEVED = 0 AND FBC.ISSUED_";
				Sql = Sql + "BY = u.id AND FB.FEEDBACK_TYPE = tft.id AND FB.ID = FBC.PARA_FEEDBACK_ID";
				// sql = " select distinct pl.name pl_name, s.name supplier_name , u.full_name issuer_name, TFT.NAME feedback_name " +
				// " from tracking_parametric tp, pl, supplier s, parts_feedback pf, part_component c, grm.grm_user u, "
				// + " tracking_feedback_type tft where TP.TRACKING_TASK_STATUS_ID=28 and TP.PL_ID=pl.id " +
				// " and TP.USER_ID in (select id from grm.grm_user where leader=" + UserID
				// + " ) and TP.SUPPLIER_ID=s.id and PF.COM_ID=c.com_id and C.DOCUMENT_ID=TP.DOCUMENT_ID  and PF.ISSUED_TO_ID=" + UserID +
				// " and PF.FEEDBACK_RECIEVED=0 and PF.ISSUED_BY_ID=u.id  and PF.FEEDBACK_TYPE=tft.id ";
			}
			else
			{
				Sql = " SELECT DISTINCT pl.name pl_name, s.name supplier_name, u.full_name issuer_nam";
				Sql = Sql + "e, TFT.NAME feedback_name FROM tracking_parametric tp, pl, supplier s, PARAMET";
				Sql = Sql + "RIC_FEEDBACK_CYCLE FBc, PARAMETRIC_FEEDBACK FB, part_component c, grm.grm_user";
				Sql = Sql + " u, tracking_feedback_type tft WHERE TP.TRACKING_TASK_STATUS_ID = 28 AND TP.PL";
				Sql = Sql + "_ID = pl.id AND TP.USER_ID IN (SELECT id FROM grm.grm_user WHERE leader = " + UserID + ") ";
				Sql = Sql + "AND TP.SUPPLIER_ID = s.id AND FB.ITEM_ID = c.com_id AND C.DOCUMENT_ID = TP.DOC";
				Sql = Sql + "UMENT_ID AND FBC.ISSUED_TO = " + UserID + " AND FBC.FEEDBACK_RECIEVED = 0 AND FBC.ISSUED_";
				Sql = Sql + "BY = u.id AND FB.FEEDBACK_TYPE = tft.id AND FB.ID = FBC.PARA_FEEDBACK_ID" + " AND TP.ASSIGNED_DATE BETWEEN TO_DATE('" + start + "', 'MM/DD/YYYY') AND TO_DATE('" + end + "', 'MM/DD/YYYY')";
				// sql = " select distinct pl.name pl_name, s.name supplier_name , u.full_name issuer_name, TFT.NAME feedback_name " +
				// " from tracking_parametric tp, pl, supplier s, parts_feedback pf, part_component c, grm.grm_user u, "
				// + " tracking_feedback_type tft where TP.TRACKING_TASK_STATUS_ID=28 and TP.PL_ID=pl.id " +
				// " and TP.USER_ID in (select id from grm.grm_user where leader=" + UserID
				// + " ) and TP.SUPPLIER_ID=s.id and PF.COM_ID=c.com_id and C.DOCUMENT_ID=TP.DOCUMENT_ID  and PF.ISSUED_TO_ID=" + UserID
				// + " and PF.FEEDBACK_RECIEVED=0 and PF.ISSUED_BY_ID=u.id  and PF.FEEDBACK_TYPE=tft.id AND TP.ASSIGNED_DATE BETWEEN TO_DATE('" +
				// start + "', 'MM/DD/YYYY') AND TO_DATE('" + end + "', 'MM/DD/YYYY')";
			}
			list2 = (ArrayList<Object[]>) session.createSQLQuery(Sql).list();

		}finally
		{
			session.close();
		}
		return list2;

	}

	public static ArrayList<TableInfoDTO> getReviewPDF(Long[] usersId, String plName, String vendorName, String type, String extracted, Date startDate, Date endDate, String feedbackTypeStr, String inputType, String priority, String status,
			String pltype)
	{
		ArrayList<TableInfoDTO> tableData = new ArrayList<TableInfoDTO>();
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
		Session session = SessionUtil.getSession();

		try
		{
			Criteria criteria = session.createCriteria(TrackingParametric.class);

			if(!status.equals("All"))
			{
				Criteria statusCriteria = session.createCriteria(TrackingTaskStatus.class);
				statusCriteria.add(Restrictions.eq("name", status));
				TrackingTaskStatus statusObj = (TrackingTaskStatus) statusCriteria.uniqueResult();
				criteria.add(Restrictions.eq("trackingTaskStatus", statusObj));
			}

			if(!(usersId.length == 0) && usersId[0] != 0 && usersId != null)
			{
				criteria.add(Restrictions.in("parametricUserId", usersId));
			}

			// System.out.println(criteria.list().size());
			if(startDate != null && endDate != null)
			{
				if(inputType.equals("assigned"))
				{
					criteria.add(Restrictions.ge("assignedDate", startDate));
					criteria.add(Restrictions.lt("assignedDate", endDate));
				}
				else if(inputType.equals("finished"))
				{
					criteria.add(Restrictions.ge("finishedDate", startDate));
					criteria.add(Restrictions.lt("finishedDate", endDate));
				}
				else if(inputType.equals("QAReview"))
				{
					criteria.add(Restrictions.ge("qaReviewDate", startDate));
					criteria.add(Restrictions.lt("qaReviewDate", endDate));
				}

			}
			if(extracted != null && !extracted.equals("All"))
			{
				System.out.println(extracted);
				if(extracted.equals("Not Extracted"))
				{
					Disjunction or = Restrictions.disjunction();
					or.add(Restrictions.eq("extractionStatus", 0l));
					or.add(Restrictions.isNull("extractionStatus"));
					criteria.add(or);
				}
				else
				{
					criteria.add(Restrictions.eq("extractionStatus", 1l));
				}
			}
			if(plName != null && !plName.equals("All"))
			{
				Criteria plCriteria = session.createCriteria(Pl.class);
				plCriteria.add(Restrictions.eq("name", plName));
				Pl pl = (Pl) plCriteria.uniqueResult();
				criteria.add(Restrictions.eq("pl", pl));
			}
			// System.out.println(criteria.list().size());

			if(priority != null && !priority.equals("All"))
			{
				criteria.add(Restrictions.eq("prioriy", Long.parseLong(priority)));
			}
			// System.out.println(criteria.list().size());

			if(vendorName != null && !vendorName.equals("All"))
			{
				Criteria vendorCriteria = session.createCriteria(Supplier.class);
				vendorCriteria.add(Restrictions.eq("name", vendorName));
				Supplier supplier = (Supplier) vendorCriteria.uniqueResult();
				criteria.add(Restrictions.eq("supplier", supplier));
			}
			// System.out.println(criteria.list().size());

			if(type != null && !type.equals("All"))
			{
				Criteria typeCriteria = session.createCriteria(TrackingTaskType.class);
				List taskType = null;
				if(type.equals("NPI"))
				{
					typeCriteria.add(Restrictions.or(Restrictions.eq("name", "NPI"), Restrictions.eq("name", "NPI Transferred"), Restrictions.eq("name", "NPI Update")));
				}
				else
				{
					typeCriteria.add(Restrictions.eq("name", type));
				}
				taskType = typeCriteria.list();
				criteria.add(Restrictions.in("trackingTaskType", taskType));
			}
			// System.out.println(criteria.list().size());

			if(feedbackTypeStr != null && !feedbackTypeStr.equals("All"))
			{
				List<Document> docs = getFeedbackDocs(feedbackTypeStr);
				if(!docs.isEmpty())
					criteria.add(Restrictions.in("document", docs));
			}
			String sql = "";
			if(pltype != null && !pltype.equals("All"))
			{
				sql = " AUTOMATION2.Get_PL_Type(this_.PL_ID)='" + pltype + "'";
				criteria.add(Restrictions.sqlRestriction(sql));
			}
			List list = criteria.list();
			for(int i = 0; i < list.size(); i++)
			{
				TrackingParametric obj = (TrackingParametric) list.get(i);
				TableInfoDTO docInfo = new TableInfoDTO();
				int infectedParts = getInfectedPartsByDoc(obj.getDocument().getId());
				int infectedTaxonomies = getInfectedTaxonomiesByDoc(obj.getDocument().getId());
				docInfo.setPdfUrl(obj.getDocument().getPdf().getSeUrl());
				docInfo.setPlName(obj.getPl().getName());
				docInfo.setSupplierName(obj.getSupplier().getName());
				docInfo.setStatus(obj.getTrackingTaskStatus().getName());
				docInfo.setTaskType(obj.getTrackingTaskType().getName());
				docInfo.setInfectedParts(infectedParts);
				docInfo.setInfectedTaxonomies(infectedTaxonomies);
				docInfo.setDevUserName(ParaQueryUtil.getGRMUser(obj.getParametricUserId()).getFullName());
				docInfo.setExtracted(obj.getExtractionStatus() == null ? "No" : "Yes");
				docInfo.setPriority("" + obj.getPrioriy());
				if(inputType.equals("QAReview"))
				{
					List<Integer> noparts = getnoPartsPerPDFandPL(obj.getDocument().getId(), obj.getPl().getId(), usersId, StatusName.qaReview);
					docInfo.setPDFParts(noparts.get(0));
					docInfo.setPLParts(noparts.get(1));
					docInfo.setPDFDoneParts(noparts.get(2));
					docInfo.setPLDoneParts(noparts.get(3));

					int fets = 0;
					try
					{
						fets = ParaQueryUtil.getPlFeautrecount(obj.getPl().getName());
					}catch(Exception e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					docInfo.setPLFeatures(fets);
				}
				Pl Pltype = ParaQueryUtil.getPLType(obj.getPl());
				docInfo.setPlType(Pltype.getName());
				Date date = obj.getFinishedDate();
				if(inputType.equals("assigned"))
				{
					date = obj.getAssignedDate();
				}
				if(date != null)
				{
					docInfo.setDate(date.toString().split(" ")[0]);
				}

				tableData.add(docInfo);
			}
			Collections.sort(tableData);

		}finally
		{
			session.close();
		}
		return tableData;
	}

	private static List<Integer> getnoPartsPerPDFandPL(Long docid, Long plid, Long[] usersId, String status)
	{
		Session session = null;
		List<Integer> result = new ArrayList<>();
		TrackingTaskStatus trackingTaskstatus = null;
		int count = 0;
		try
		{

			session = SessionUtil.getSession();
			Criteria cri = session.createCriteria(TrackingTaskStatus.class);
			cri.add(Restrictions.eq("name", status));
			trackingTaskstatus = (TrackingTaskStatus) cri.uniqueResult();
			SQLQuery query = session.createSQLQuery("SELECT   /*+ INDEX(x comp_doc_id_idx) */count(COM_ID)  FROM  AUTOMATION2.PART_COMPONENT x " + " WHERE   x.DOCUMENT_ID =" + docid + "");
			Object obj = query.uniqueResult();
			if(obj != null)
			{
				result.add(Integer.parseInt(obj.toString()));
			}
			else
			{
				result.add(0);
			}
			String users = "";
			if(usersId.length > 0)
			{
				users += "And Z.USER_ID in (";
				for(int u = 0; u < usersId.length; u++)
				{
					users += "" + usersId[u] + ",";
				}
				users = users.substring(0, users.length() - 1);
				users += ")";
			}
			String Sql = "";
			Sql = " SELECT /*+ INDEX(x comp_doc_id_idx) */ COUNT (COM_ID) FROM AUTOMATION2.PART_COMPONENT x ";
			Sql = Sql + "where x.DOCUMENT_ID in( select document_id from TRACKING_PARAMETRIC";
			Sql = Sql + " z where z.PL_ID = " + plid + " and z.TRACKING_TASK_STATUS_ID = " + trackingTaskstatus.getId() + " " + users + " ";
			Sql = Sql + ")";
			query = session.createSQLQuery(Sql);
			obj = query.uniqueResult();
			if(obj != null)
			{
				result.add(Integer.parseInt(obj.toString()));
			}
			else
			{
				result.add(0);
			}

			query = session.createSQLQuery("SELECT   /*+ INDEX(x comp_doc_id_idx) */count(COM_ID)  FROM  AUTOMATION2.PART_COMPONENT x " + " WHERE   x.DOCUMENT_ID =" + docid + " and DONEFLAG = 1");
			obj = query.uniqueResult();
			if(obj != null)
			{
				result.add(Integer.parseInt(obj.toString()));
			}
			else
			{
				result.add(0);
			}

			Sql = " SELECT /*+ INDEX(x COMP_DOC_DONE_IDX) */ COUNT (COM_ID) FROM AUTOMATION2.PART_COMPONENT x ";
			Sql = Sql + "where  X.DONEFLAG = 1 AND x.DOCUMENT_ID in( select document_id from TRACKING_PARAMETRIC";
			Sql = Sql + " z where z.PL_ID = " + plid + " and z.TRACKING_TASK_STATUS_ID = " + trackingTaskstatus.getId() + " " + users + " ";
			Sql = Sql + ")";
			query = session.createSQLQuery(Sql);
			obj = query.uniqueResult();
			if(obj != null)
			{
				result.add(Integer.parseInt(obj.toString()));
			}
			else
			{
				result.add(0);
			}

		}catch(Exception ex)
		{
			ex.printStackTrace();
		}finally
		{
			if((session != null) && (session.isOpen()))
			{
				session.close();
			}
		}
		return result;

	}

	public static ArrayList<TableInfoDTO> getShowAllData(long userId, String plName, String vendorName, String type, String extracted, String status, Date startDate, Date endDate, String priority)
	{
		Session session = SessionUtil.getSession();
		ArrayList<TableInfoDTO> tableData = new ArrayList<TableInfoDTO>();
		SQLQuery query = null;
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
			StringBuffer qury = new StringBuffer();
			qury.append("SELECT p.SE_URL pdfurl, GET_PL_NAME (t.PL_ID) plName, GETSUPPLIERNAME (t.supplier_id) supName, GetTaskStatusName (TRACKING_TASK_STATUS_ID) task_Status,GetTaskTypeName (t.TRACKING_TASK_TYPE_ID) task_type, getuserName (T.USER_ID) user_Name,t.EXTRACTION_STATUS,"
					+ " t.PRIORIY,t.ASSIGNED_DATE, getallTaxbyDocID (t.DOCUMENT_ID) tax, GET_Tax_Path (t.DOCUMENT_ID, t.SUPPLIER_ID) taxpath,GETNPINewsPDFURL (t.DOCUMENT_ID) newsLike, p.DOWNLOAD_DATE, p.CER_DATE, PAGE_COUNT,d.TITLE"
					+ "  FROM TRACKING_PARAMETRIC T, document d,pdf p WHERE t.document_id = d.id and p.id=   GET_PDFIDBYDOCID(t.document_id)");

			if(plName != null && !plName.equals("All"))
			{
				qury.append("  AND T.PL_ID=GET_PL_ID('" + plName + "')");
			}
			if(!vendorName.equals("All") && vendorName != null)
			{
				qury.append("  and T.SUPPLIER_ID=GETSUPPLIERID('" + vendorName + "')");
			}
			if(status != null && !status.equals("All"))
			{
				qury.append(" AND t.TRACKING_TASK_STATUS_ID = getTaskstatusId('" + status + "')");
			}
			if(type != null && !type.equals("All"))
			{
				if(type.equals("NPI"))
				{
					qury.append(" AND t.TRACKING_TASK_TYPE_ID in(4,12,15)");
				}
				else
				{
					qury.append(" AND t.TRACKING_TASK_TYPE_ID = getTaskTypeId('" + type + "')");
				}
			}

			if(extracted != null && !extracted.equals("All"))
			{
				if(extracted.equals("Extracted"))
					qury.append(" AND t.EXTRACTION_STATUS =1");
				else
					qury.append(" AND t.EXTRACTION_STATUS <>1 ");
			}
			if(priority != null && !priority.equals("All"))
			{
				qury.append(" AND t.PRIORIY =" + priority);

			}
			if(startDate != null && endDate != null)
			{
				SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/YYYY");
				System.out.println(formatter.format(startDate) + "**************" + formatter.format(endDate));
				String dateRangeCond = " AND t.ASSIGNED_DATE BETWEEN TO_DATE ('" + formatter.format(startDate) + "','DD/MM/RRRR')AND  TO_DATE ('" + formatter.format(endDate) + "','DD/MM/RRRR')";

				// String dateRangeCond = " AND t.ASSIGNED_DATE BETWEEN TO_DATE ('" + startDate + "','MM/DD/YYYY HH24:MI:SS')AND  TO_DATE ('" +
				// endDate + "','MM/DD/YYYY HH24:MI:SS')";

				qury.append(dateRangeCond);
				// qury = qury +
				// " AND t.ASSIGNED_DATE BETWEEN TO_DATE ('01/11/2012', 'DD/MM/RRRR') AND  TO_DATE ('03/03/2013', 'DD/MM/RRRR')";
			}
			qury.append(" and T.USER_ID =" + userId);
			qury.append(" ORDER BY   plName,pdfurl");

			System.out.println(qury.toString());
			ArrayList<Object[]> result = (ArrayList<Object[]>) session.createSQLQuery(qury.toString()).list();
			ArrayList<ArrayList<String>> plData = new ArrayList<ArrayList<String>>();

			System.out.println("All Data Size:" + result.size());

			for(int i = 0; i < result.size(); i++)
			{
				Object[] data = result.get(i);
				TableInfoDTO docInfo = new TableInfoDTO();
				docInfo.setPdfUrl(data[0].toString());
				docInfo.setPlName(data[1].toString());
				docInfo.setSupplierName(data[2].toString());
				docInfo.setStatus(data[3].toString());
				docInfo.setTaskType(data[4].toString());
				docInfo.setDevUserName(data[5].toString());
				docInfo.setExtracted(data[6] != null && data[6].toString().equals("1") ? "Yes" : "No");
				docInfo.setPriority(data[7] != null ? data[7].toString() : "");
				docInfo.setDate(data[8].toString());
				docInfo.setTaxonomies(data[9].toString());
				docInfo.setTaxPath(data[10] != null ? data[10].toString() : "");
				docInfo.setNewsLink(data[11] != null ? data[11].toString() : "");
				docInfo.setDownloadDate(data[12] != null ? data[12].toString() : "");
				docInfo.setCerDate(data[13] != null ? data[13].toString() : "");
				docInfo.setPagesCount(((BigDecimal) data[14]).intValue());
				docInfo.setTitle(data[15] != null ? data[15].toString() : "");

				tableData.add(docInfo);
			}
		}finally
		{
			session.close();
		}
		return tableData;
	}

	public static ArrayList<TableInfoDTO> getAllAssigined(long userId, String plName, String vendorName, String type, String extracted, String status, Date startDate, Date endDate, String priority)
	{
		Session session = SessionUtil.getSession();
		ArrayList<TableInfoDTO> tableData = new ArrayList<TableInfoDTO>();
		SQLQuery query = null;
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

			StringBuffer qury = new StringBuffer();
			qury.append(" SELECT  GETPDFURLBYDOCID (t.DOCUMENT_ID) pdfurl, GET_PL_NAME (t.PL_ID) plName,GETSUPPLIERNAME (t.supplier_id) supName, GetTaskStatusName (TRACKING_TASK_STATUS_ID) task_Status,"
					+ " GetTaskTypeName (t.TRACKING_TASK_TYPE_ID) task_type, getuserName (T.USER_ID) user_Name,t.EXTRACTION_STATUS,t.PRIORIY,t.ASSIGNED_DATE ,d.TITLE,getallTaxbyDocID(t.DOCUMENT_ID) tax ,t.DOCUMENT_ID FROM  TRACKING_PARAMETRIC T,document d");

			qury.append(" where  t.document_id=d.id  and T.USER_ID =" + userId);
			if(plName != null && !plName.equals("All"))
			{
				qury.append("  AND T.PL_ID=GET_PL_ID('" + plName + "')");
			}
			if(!vendorName.equals("All") && vendorName != null)
			{
				qury.append("  and T.SUPPLIER_ID=GETSUPPLIERID('" + vendorName + "')");
			}
			if(status != null && !status.equals("All"))
			{
				qury.append(" AND t.TRACKING_TASK_STATUS_ID = getTaskstatusId('" + status + "')");
			}
			if(type != null && !type.equals("All"))
			{
				if(type.equals("NPI"))
				{
					qury.append(" AND t.TRACKING_TASK_TYPE_ID in(4,12,15)");
				}
				else
				{
					qury.append(" AND t.TRACKING_TASK_TYPE_ID = getTaskTypeId('" + type + "')");
				}

			}

			if(extracted != null && !extracted.equals("All"))
			{
				if(extracted.equals("Extracted"))
					qury.append(" AND t.EXTRACTION_STATUS =1");
				else
					qury.append(" AND t.EXTRACTION_STATUS <>1 ");
			}
			if(priority != null && !priority.equals("All"))
			{
				qury.append(" AND t.PRIORIY =" + priority);

			}
			if(startDate != null && endDate != null)
			{
				SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/YYYY");
				System.out.println(formatter.format(startDate) + "**************" + formatter.format(endDate));
				String dateRangeCond = " AND t.ASSIGNED_DATE BETWEEN TO_DATE ('" + formatter.format(startDate) + "','DD/MM/RRRR')AND  TO_DATE ('" + formatter.format(endDate) + "','DD/MM/RRRR')";

				// String dateRangeCond = " AND t.ASSIGNED_DATE BETWEEN TO_DATE ('" + formatter.format(startDate) + "','DD/MM/RRRR')AND  TO_DATE ('" +
				// formatter.format(endDate) + "','DD/MM/RRRR')";
				qury.append(dateRangeCond);
				// qury = qury +
				// " AND t.ASSIGNED_DATE BETWEEN TO_DATE ('01/11/2012', 'DD/MM/RRRR') AND  TO_DATE ('03/03/2013', 'DD/MM/RRRR')";
			}
			qury.append(" ORDER BY   plName,pdfurl");

			System.out.println(qury.toString());
			ArrayList<Object[]> result = (ArrayList<Object[]>) session.createSQLQuery(qury.toString()).list();
			ArrayList<ArrayList<String>> plData = new ArrayList<ArrayList<String>>();

			System.out.println("All Data Size:" + result.size());

			for(int i = 0; i < result.size(); i++)
			{
				Object[] data = result.get(i);
				TableInfoDTO docInfo = new TableInfoDTO();
				docInfo.setPdfUrl(data[0].toString());
				docInfo.setPlName(data[1].toString());
				docInfo.setSupplierName(data[2].toString());
				docInfo.setStatus(data[3].toString());
				docInfo.setTaskType(data[4].toString());
				docInfo.setDevUserName(data[5].toString());
				docInfo.setExtracted(data[6] != null && data[6].toString().equals("1") ? "Yes" : "No");
				docInfo.setPriority(data[7].toString());
				docInfo.setDate(data[8].toString());
				docInfo.setTitle(data[9] != null ? data[9].toString() : "");
				docInfo.setTaxonomies(data[10].toString());
				docInfo.setDocId(data[11].toString());
				docInfo.setPdfId(Long.parseLong(data[11].toString()));

				tableData.add(docInfo);
			}
		}finally
		{
			session.close();
		}
		return tableData;
	}

	public static Map<String, ArrayList<ArrayList<String>>> getParametricValueReview(Long[] usersId, String plName, String vendorName, String type, String status, Date startDate, Date endDate, Long[] docsIds) throws Exception
	{
		Session session = SessionUtil.getSession();
		Map<String, ArrayList<ArrayList<String>>> allData = new HashMap<String, ArrayList<ArrayList<String>>>();

		SQLQuery query = null;
		try
		{
			StringBuffer qury = new StringBuffer();
			qury.append("  SELECT GET_PL_NAME (t.PL_ID) plName,getuserName (T.USER_ID),GetTaskTypeName (t.TRACKING_TASK_TYPE_ID) task_type, GETSUPPLIERNAME (t.supplier_id) supName,C.PART_NUMBER,FAM.NAME ,GET_MSK_Value (c.MASK_ID, C.PART_NUMBER) MASK,Get_GENERIC_Name (C.GENERIC_ID) generic_Nam,Get_family_crossName (C.FAMILY_CROSS_ID) family_Cross, GETPDFURLBYDOCID (t.DOCUMENT_ID) pdfurl,F.NAME fetName, G.GROUP_FULL_VALUE fetVal,t.ASSIGNED_DATE,"
					+ " GetTaskStatusName (TRACKING_TASK_STATUS_ID) task_Status,C.COM_ID,R.PL_FEATURE_ID,R.GROUP_APPROVED_VALUE_ID,t.DOCUMENT_ID,t.PL_ID"
					+ " FROM  TRACKING_PARAMETRIC T, part_COMPONENT c,family fam,PARAMETRIC_REVIEW_DATA r,pl_feature_unit pf, feature f,PARAMETRIC_APPROVED_GROUP g WHERE t.DOCUMENT_ID = c.DOCUMENT_ID and T.SUPPLIER_PL_ID=C.SUPPLIER_PL_ID AND c.COM_ID = R.COM_ID and C.FAMILY_ID=FAM.ID AND R.PL_FEATURE_ID = PF.ID AND PF.FET_ID = F.ID AND R.GROUP_APPROVED_VALUE_ID = G.ID  ");
			if(plName != null && !plName.equals("All"))
			{
				qury.append("  AND T.PL_ID=GET_PL_ID('" + plName + "')");
			}
			if(!vendorName.equals("All") && vendorName != null)
			{
				qury.append("  and T.SUPPLIER_ID=GETSUPPLIERID('" + vendorName + "')");
			}
			if(status != null && !status.equals("All"))
			{
				qury.append(" AND t.TRACKING_TASK_STATUS_ID = getTaskstatusId('" + status + "')");
			}
			if(type != null && !type.equals("All"))
			{
				qury.append(" AND t.TRACKING_TASK_TYPE_ID = getTaskTypeId('" + type + "')");
			}
			if(!(usersId.length == 0) && usersId != null)
			{
				String users = "";
				for(int i = 0; i < usersId.length; i++)
				{
					if(i == usersId.length - 1)
					{
						users = users + usersId[i];
					}
					else
					{
						users = users + usersId[i] + ",";
					}
				}
				qury.append(" AND T.USER_ID IN (" + users + ")");
			}
			if(docsIds != null && docsIds.length > 0)
			{
				qury.append(" AND t.DOCUMENT_ID in ( " + getArrayAsCommaSeperatedList(docsIds) + " )");
			}
			if(startDate != null && endDate != null)
			{
				endDate.setDate(endDate.getDate() + 1);
				SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
				System.out.println(formatter.format(startDate) + "**************" + formatter.format(endDate));

				String dateRangeCond = " AND t.ASSIGNED_DATE BETWEEN TO_DATE ('" + formatter.format(startDate) + "','DD/MM/RRRR')AND  TO_DATE ('" + formatter.format(endDate) + "','DD/MM/RRRR')";
				qury.append(dateRangeCond);
				// qury = qury +
				// " AND t.ASSIGNED_DATE BETWEEN TO_DATE ('01/11/2012', 'DD/MM/RRRR') AND  TO_DATE ('03/03/2013', 'DD/MM/RRRR')";
			}
			qury.append(" ORDER BY   T.DOCUMENT_ID,plName, C.PART_NUMBER, PF.DEVELOPMENT_ORDER");

			System.out.println(qury.toString());
			ArrayList<Object[]> result = (ArrayList<Object[]>) session.createSQLQuery(qury.toString()).list();
			ArrayList<ArrayList<String>> plData = new ArrayList<ArrayList<String>>();
			ArrayList<String> partData = new ArrayList<String>();
			List<String> plFets = new ArrayList<String>();
			Map<String, List<String>> plFetsMap = new HashMap<String, List<String>>();
			System.out.println("All Data Size:" + result.size());
			String lastPart = "";
			String lastPl = "";
			int x = -1;
			for(int i = 0; i < result.size(); i++)
			{
				Object[] data = result.get(i);
				String plout = data[0].toString();
				// int fetOrder= getPlFeatureByExactName(data[10].toString(), plout, session).getDevelopmentOrder();
				if(plFetsMap.get(plout) == null)
					plFetsMap.put(plout, ParaQueryUtil.getPlFeautreNames(plout));
				String plType = ParaQueryUtil.getPLType(QueryUtil.getPlByExactName(plout, session)).getName();
				System.out.println(plout + " @ " + "Part:" + data[4].toString() + " ~ " + data[10].toString() + " ~ " + data[11].toString() + " idx:" + partData.indexOf(data[10].toString()));
				if(data[4].toString().equals(lastPart))
				{
					// partData.add(data[11].toString()); /* fet Value */
					partData.set(partData.indexOf(data[10].toString()), data[11].toString());
					if(i == result.size() - 1)
					{
						plFets = plFetsMap.get(lastPl);
						for(int j = 0; j < plFets.size(); j++)
						{
							if(partData.indexOf(plFets.get(j)) != -1)
								partData.set(partData.indexOf(plFets.get(j)), "");
						}
						if(allData.get(lastPl) == null)
						{

							plData = new ArrayList<ArrayList<String>>();

							if(!partData.isEmpty())
							{
								plData.add(partData);
							}

						}
						else
						{
							plData = allData.get(lastPl);
							plData.add(partData);
						}
						if(!plData.isEmpty())
						{
							allData.put(lastPl, plData);
						}
					}
					continue;
				}
				else
				{
					plFets = plFetsMap.get(lastPl);
					if(plFets != null)
					{
						for(int j = 0; j < plFets.size(); j++)
						{
							if(partData.indexOf(plFets.get(j)) != -1)
								partData.set(partData.indexOf(plFets.get(j)), "");
						}
					}
					if(allData.get(lastPl) == null)
					{
						plData = new ArrayList<ArrayList<String>>();
						if(!partData.isEmpty())
						{
							plData.add(partData);
						}
					}
					else
					{
						plData = allData.get(lastPl);
						plData.add(partData);
					}
					if(!plData.isEmpty())
					{
						allData.put(lastPl, plData);
					}

					partData = new ArrayList<String>();
					partData.add(data[0].toString()); /* Pl Name */
					partData.add(data[1].toString()); /* user name */
					partData.add(""); /* Status */
					partData.add(""); /* Comment */
					partData.add(data[2].toString()); /* Task Type */
					partData.add(data[3].toString()); /* Supplier */
					partData.add(data[4].toString()); /* Part Number */
					partData.add(data[5].toString()); /* family */
					partData.add(data[6].toString());/* Mask */

					if(plType.equals("Semiconductor"))
					{
						partData.add(data[7].toString());/* family cross */
						partData.add(data[8].toString());/* genric */
						partData.add(data[9].toString());/* pdf url */
					}
					else
					{
						partData.add(data[9].toString());/* pdf url */
					}
					plFets = plFetsMap.get(plout);
					partData.addAll(plFets);
					partData.set(partData.indexOf(data[10].toString()), data[11].toString());
					// x= plFets.indexOf(data[10].toString());
					// partData.add(data[11].toString()); /* fet Value */

				}

				if(result.size() == 1)
				{
					for(int j = 0; j < plFets.size(); j++)
					{
						if(partData.indexOf(plFets.get(j)) != -1)
							partData.set(partData.indexOf(plFets.get(j)), "");
					}
					plData = new ArrayList<ArrayList<String>>();
					if(!partData.isEmpty())
					{
						plData.add(partData);
						allData.put(plout, plData);
					}
				}

				lastPart = data[4].toString();
				lastPl = plout;
			}
		}finally
		{
			session.close();
		}
		return allData;

	}

	public static Map<String, ArrayList<ArrayList<String>>> getParametricValueReview1(Long[] usersId, String plName, String vendorName, String type, String status, Date startDate, Date endDate, Long[] docsIds) throws Exception
	{
		Session session = SessionUtil.getSession();
		Map<String, ArrayList<ArrayList<String>>> allData = new HashMap<String, ArrayList<ArrayList<String>>>();

		long statusId = ParaQueryUtil.getTrackingTaskStatus(session, status).getId();
		SQLQuery query = null;
		try
		{
			StringBuffer qury = new StringBuffer();
			qury.append("  SELECT GET_PL_NAME (t.PL_ID) plName,getuserName (T.USER_ID),GetTaskTypeName (t.TRACKING_TASK_TYPE_ID) task_type, GETSUPPLIERNAME (t.supplier_id) supName,C.PART_NUMBER,FAM.NAME,Get_family_crossName(C.FAMILY_CROSS_ID) family_Cross , Get_GENERIC_Name (C.GENERIC_ID) generic_Nam,GET_MSK_Value(c.MASK_ID,C.PART_NUMBER) MASK,c.NPI_FLAG, GETNPINewsPDFURL (c.DOCUMENT_ID) newsLike,c.DESCRIPTION, GETPDFURLBYDOCID (t.DOCUMENT_ID) pdfurl,F.NAME fetName, G.GROUP_FULL_VALUE fetVal,t.ASSIGNED_DATE,"
					+ " GetTaskStatusName (TRACKING_TASK_STATUS_ID) task_Status,C.COM_ID,R.PL_FEATURE_ID,R.GROUP_APPROVED_VALUE_ID,t.DOCUMENT_ID,t.PL_ID"
					+ " FROM  TRACKING_PARAMETRIC T, Part_COMPONENT c,family fam,PARAMETRIC_REVIEW_DATA r,pl_feature_unit pf, feature f,PARAMETRIC_APPROVED_GROUP g WHERE t.DOCUMENT_ID = c.DOCUMENT_ID and T.SUPPLIER_PL_ID=C.SUPPLIER_PL_ID AND c.COM_ID = R.COM_ID and C.FAMILY_ID=FAM.ID AND R.PL_FEATURE_ID = PF.ID AND PF.FET_ID = F.ID AND R.GROUP_APPROVED_VALUE_ID = G.ID");
			if(plName != null && !plName.equals("All"))
			{
				qury.append("  AND T.PL_ID=GET_PL_ID('" + plName + "')");
			}
			if(!vendorName.equals("All") && vendorName != null)
			{
				qury.append("  and T.SUPPLIER_ID=GETSUPPLIERID('" + vendorName + "')");
			}
			if(status != null && !status.equals("All"))
			{
				// qury.append(" AND t.TRACKING_TASK_STATUS_ID = getTaskstatusId('" + status + "')");
				qury.append(" AND t.TRACKING_TASK_STATUS_ID =" + statusId);
			}
			if(type != null && !type.equals("All"))
			{
				if(type.equals("NPI"))
				{
					qury.append(" AND t.TRACKING_TASK_TYPE_ID in(getTaskTypeId('" + StatusName.npi + "'),getTaskTypeId('" + StatusName.npiTransferred + "'),getTaskTypeId('" + StatusName.npiUpdate + "'))");
				}
				else
				{
					qury.append(" AND t.TRACKING_TASK_TYPE_ID = getTaskTypeId('" + type + "')");
				}
			}
			if(!(usersId.length == 0) && usersId != null)
			{
				qury.append(" AND T.USER_ID IN (" + getArrayAsCommaSeperatedList(usersId) + ")");
			}
			if(docsIds != null && docsIds.length > 0)
			{
				qury.append(" AND c.DOCUMENT_ID in ( " + getArrayAsCommaSeperatedList(docsIds) + " )");
			}
			if(startDate != null && endDate != null)
			{
				endDate.setDate(endDate.getDate() + 1);
				SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
				System.out.println(formatter.format(startDate) + "**************" + formatter.format(endDate));

				String dateRangeCond = " AND t.ASSIGNED_DATE BETWEEN TO_DATE ('" + formatter.format(startDate) + "','DD/MM/RRRR')AND  TO_DATE ('" + formatter.format(endDate) + "','DD/MM/RRRR')";
				qury.append(dateRangeCond);
			}
			qury.append(" ORDER BY   T.DOCUMENT_ID,plName, C.PART_NUMBER, PF.DEVELOPMENT_ORDER");

			System.out.println(qury.toString());
			ArrayList<Object[]> result = (ArrayList<Object[]>) session.createSQLQuery(qury.toString()).list();
			ArrayList<ArrayList<String>> plData = new ArrayList<ArrayList<String>>();
			ArrayList<String> partData = new ArrayList<String>();
			List<String> plFets = new ArrayList<String>();
			Map<String, List<String>> plFetsMap = new HashMap<String, List<String>>();
			System.out.println("All Data Size:" + result.size());
			String lastPart = "";
			String lastPl = "";
			String lastDesc = "";
			int x = -1;
			String plType = "";
			for(int i = 0; i < result.size(); i++)
			{
				Object[] data = result.get(i);
				String plout = data[0].toString();

				String desc = (data[11] == null) ? "" : data[11].toString();
				// int fetOrder= getPlFeatureByExactName(data[10].toString(), plout, session).getDevelopmentOrder();

				if(plFetsMap.get(plout) == null)
				{
					plFetsMap.put(plout, ParaQueryUtil.getPlFeautreNames(plout));
					plType = ParaQueryUtil.getPLType(QueryUtil.getPlByExactName(plout, session)).getName();
				}
				// plType = ParaQueryUtil.getPLType(QueryUtil.getPlByExactName(plout, session)).getName();
				System.out.println(plout + " @ " + "Part:" + data[4].toString() + " ~ " + data[13].toString() + " ~ " + data[14].toString() + " idx:" + partData.indexOf(data[13].toString()));
				if(data[4].toString().equals(lastPart))
				{
					// partData.add(data[11].toString()); /* fet Value */
					partData.set(partData.indexOf(data[13].toString()), data[14].toString());
					if(i == result.size() - 1)
					{
						plFets = plFetsMap.get(lastPl);
						for(int j = 0; j < plFets.size(); j++)
						{
							if(partData.indexOf(plFets.get(j)) != -1)
								partData.set(partData.indexOf(plFets.get(j)), "");
						}
						if(allData.get(lastPl) == null)
						{

							plData = new ArrayList<ArrayList<String>>();

							if(!partData.isEmpty())
							{
								partData.add(lastDesc);
								plData.add(partData);
							}

						}
						else
						{
							plData = allData.get(lastPl);
							partData.add(lastDesc);
							plData.add(partData);
						}
						if(!plData.isEmpty())
						{
							allData.put(lastPl, plData);
						}
					}
					continue;
				}
				else
				{
					plFets = plFetsMap.get(lastPl);
					if(plFets != null)
					{
						for(int j = 0; j < plFets.size(); j++)
						{
							if(partData.indexOf(plFets.get(j)) != -1)
								partData.set(partData.indexOf(plFets.get(j)), "");
						}
					}
					if(allData.get(lastPl) == null)
					{
						plData = new ArrayList<ArrayList<String>>();
						if(!partData.isEmpty())
						{
							partData.add(lastDesc);
							plData.add(partData);
						}
					}
					else
					{
						plData = allData.get(lastPl);
						partData.add(lastDesc);
						plData.add(partData);
					}
					if(!plData.isEmpty())
					{
						allData.put(lastPl, plData);
					}
					boolean NPIFlag = isNPITaskType(usersId, plout, vendorName, type, status, startDate, endDate, docsIds);
					partData = new ArrayList<String>();
					/** Pl Name */
					partData.add(data[0].toString());
					/** user name */
					partData.add(data[1].toString());
					/** Status */
					partData.add("");
					/** Comment */
					partData.add("");
					/** Task Type */
					partData.add(data[2].toString());
					/** Supplier */
					partData.add(data[3].toString());
					/** comid */
					partData.add(data[17].toString());
					/** Part Number */
					partData.add(data[4].toString());
					/** family */
					partData.add(data[5].toString());

					if(plType.equals("Semiconductor"))
					{
						/** family cross */

						partData.add(data[6] != null ? data[6].toString() : "");
						/** generic */
						partData.add((data[8] == null) ? "" : data[8].toString());
						/** Mask */
						partData.add((data[8] == null) ? "" : data[8].toString());

						if(NPIFlag)
						{
							/** NPI Flag */
							partData.add((data[9] != null && data[9].toString().equals("1")) ? "Yes" : "");
							/** NPI news */
							partData.add((data[10] == null) ? "" : data[10].toString());
							List<String> newsData = getNewsLink(data[12].toString());
							/** NPI desc */
							partData.add(newsData.get(1));
							/** NPI date */
							partData.add(newsData.get(2));

						}
						/** pdf url */
						partData.add(data[12].toString());

					}
					else
					{
						/** Mask */
						partData.add((data[8] == null) ? "" : data[8].toString());

						if(NPIFlag)
						{
							/** NPI Flag */
							partData.add((data[9] != null && data[9].toString().equals("1")) ? "Yes" : "");
							/** NPI news */
							partData.add((data[10] == null) ? "" : data[10].toString());
							List<String> newsData = getNewsLink(data[12].toString());
							/** NPI desc */
							partData.add(newsData.get(1));
							/** NPI date */
							partData.add(newsData.get(2));

						}
						/** pdf url */
						partData.add(data[12].toString());

					}
					plFets = plFetsMap.get(plout);
					partData.addAll(plFets);
					partData.set(partData.indexOf(data[13].toString()), data[14].toString());
					// x= plFets.indexOf(data[10].toString());
					// partData.add(data[11].toString()); /* fet Value */

				}

				if(i == result.size() - 1)
				{
					for(int j = 0; j < plFets.size(); j++)
					{
						if(partData.indexOf(plFets.get(j)) != -1)
							partData.set(partData.indexOf(plFets.get(j)), "");
					}

					if(allData.get(plout) == null)
					{
						plData = new ArrayList<ArrayList<String>>();
						if(!partData.isEmpty())
						{
							partData.add(desc);
							plData.add(partData);
						}
					}
					else
					{
						plData = allData.get(plout);
						partData.add(desc);
						plData.add(partData);
					}
					allData.put(plout, plData);
				}

				lastPart = data[4].toString();
				lastDesc = (data[11] == null) ? "" : data[11].toString();
				lastPl = plout;
			}
		}finally
		{
			session.close();
		}
		return allData;

	}

	public static Map<String, ArrayList<ArrayList<String>>> getFeedbackParametricValueReview(Long[] usersId, String plName, String vendorName, String docStatus, String feedbackType, String issuedby, Date startDate, Date endDate, Long[] docsIds,
			long issuedToId) throws Exception
	{
		Session session = SessionUtil.getSession();
		Map<String, ArrayList<ArrayList<String>>> allData = new HashMap<String, ArrayList<ArrayList<String>>>();

		SQLQuery query = null;
		try
		{
			StringBuffer qury = new StringBuffer();
			qury.append("  SELECT GET_PL_NAME (t.PL_ID) plName,getuserName (T.USER_ID),GetTaskTypeName (t.TRACKING_TASK_TYPE_ID) task_type, GETSUPPLIERNAME (t.supplier_id) supName,C.PART_NUMBER,FAM.NAME ,"
					+ " Get_GENERIC_Name (C.GENERIC_ID) generic_Nam,Get_family_crossName (C.FAMILY_CROSS_ID) family_Cross,GET_MSK_Value (c.MASK_ID,C.PART_NUMBER) MASK,c.NPI_FLAG,"
					+ " GET_News_PDF_URL(c.DOCUMENT_ID, c.SUPPLIER_ID) newsLike,c.DESCRIPTION, GETPDFURLBYDOCID (t.DOCUMENT_ID) pdfurl,F.NAME fetName, G.GROUP_FULL_VALUE fetVal,t.ASSIGNED_DATE,"
					+ " GetTaskStatusName (TRACKING_TASK_STATUS_ID) task_Status,C.COM_ID,R.PL_FEATURE_ID,R.GROUP_APPROVED_VALUE_ID,t.DOCUMENT_ID,t.PL_ID,TY.NAME type"
					+ " FROM  TRACKING_PARAMETRIC T, part_COMPONENT c,family fam,PARAMETRIC_REVIEW_DATA r,pl_feature_unit pf, feature f,PARAMETRIC_APPROVED_GROUP g,PARAMETRIC_FEEDBACK FB, PARAMETRIC_FEEDBACK_CYCLE FBC, TRACKING_FEEDBACK_TYPE ty"
					+ " WHERE t.DOCUMENT_ID = c.DOCUMENT_ID and T.SUPPLIER_PL_ID=C.SUPPLIER_PL_ID AND c.COM_ID = R.COM_ID and C.FAMILY_ID=FAM.ID AND R.PL_FEATURE_ID = PF.ID AND PF.FET_ID = F.ID AND R.GROUP_APPROVED_VALUE_ID = G.ID And FB.Item_id = R.COM_ID And FB.ID = FBC.PARA_FEEDBACK_ID And TY.ID = FB.FEEDBACK_TYPE And FBC.FEEDBACK_RECIEVED = 0");
			if(plName != null && !plName.equals("All"))
			{
				qury.append("  AND T.PL_ID=GET_PL_ID('" + plName + "')");
			}
			if(!vendorName.equals("All") && vendorName != null)
			{
				qury.append("  and T.SUPPLIER_ID=GETSUPPLIERID('" + vendorName + "')");
			}
			if(!(usersId.length == 0) && usersId != null)
			{

				qury.append(" AND T.USER_ID IN (" + getArrayAsCommaSeperatedList(usersId) + ")");

			}
			if(docStatus != null && !docStatus.equals("All"))
			{
				qury.append(" AND t.TRACKING_TASK_STATUS_ID = getTaskstatusId('" + docStatus + "')");
			}

			if(docsIds != null && docsIds.length > 0)
			{
				qury.append(" AND c.DOCUMENT_ID in ( " + getArrayAsCommaSeperatedList(docsIds) + " )");
			}
			qury.append(" AND FBC.ISSUED_TO = " + issuedToId);
			Criteria ParametricFeedbackCriteria = session.createCriteria(ParametricFeedbackCycle.class);
			ParametricFeedbackCriteria.add(Restrictions.eq("feedbackRecieved", 0l));
			ParametricFeedbackCriteria.add(Restrictions.eq("issuedTo", issuedToId));
			ParametricFeedbackCriteria.createAlias("parametricFeedback", "feedback");
			ParametricFeedbackCriteria.add(Restrictions.eq("feedback.type", "P"));
			if((feedbackType != null) && (!"All".equals(feedbackType)))
			{
				ParametricFeedbackCriteria.add(Restrictions.eq("feedback.trackingFeedbackType", ParaQueryUtil.getTrackingFeedbackType(feedbackType)));
			}
			if((issuedby != null) && (!"All".equals(issuedby)))
			{
				GrmUser issuer = ParaQueryUtil.getGRMUserByName(issuedby);
				ParametricFeedbackCriteria.add(Restrictions.eq("issuedBy", issuer.getId()));
			}

			List<ParametricFeedbackCycle> parametricfeedbackcycle = ParametricFeedbackCriteria.list();
			Set<Long> docSet = new HashSet<Long>();
			if(parametricfeedbackcycle != null)
			{
				for(int i = 0; i < parametricfeedbackcycle.size(); i++)
				{
					docSet.add(parametricfeedbackcycle.get(i).getParametricFeedback().getDocument().getId());
				}
			}
			if(docSet.size() > 0)
			{
				Long[] docsIds2 = new Long[docSet.size()];
				docsIds2 = docSet.toArray(docsIds2);
				qury.append(" AND c.DOCUMENT_ID in ( " + getArrayAsCommaSeperatedList(docsIds2) + " )");
			}

			if(startDate != null && endDate != null)
			{
				SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
				System.out.println(formatter.format(startDate) + "**************" + formatter.format(endDate));

				String dateRangeCond = " AND t.ASSIGNED_DATE BETWEEN TO_DATE ('" + formatter.format(startDate) + "','DD/MM/RRRR')AND  TO_DATE ('" + formatter.format(endDate) + "','DD/MM/RRRR')";
				qury.append(dateRangeCond);
			}
			qury.append(" ORDER BY   T.DOCUMENT_ID,plName, C.PART_NUMBER, PF.DEVELOPMENT_ORDER");

			System.out.println(qury.toString());
			ArrayList<Object[]> result = (ArrayList<Object[]>) session.createSQLQuery(qury.toString()).list();
			ArrayList<ArrayList<String>> plData = new ArrayList<ArrayList<String>>();
			ArrayList<String> partData = new ArrayList<String>();
			List<String> plFets = new ArrayList<String>();
			Map<String, List<String>> plFetsMap = new HashMap<String, List<String>>();
			System.out.println("All Data Size:" + result.size());
			String lastPart = "";
			String lastPl = "";
			String lastDesc = "";
			int x = -1;
			for(int i = 0; i < result.size(); i++)
			{
				Object[] data = result.get(i);
				String plout = data[0].toString();

				String desc = (data[11] == null) ? "" : data[11].toString();
				// int fetOrder= getPlFeatureByExactName(data[10].toString(), plout, session).getDevelopmentOrder();
				if(plFetsMap.get(plout) == null)
					plFetsMap.put(plout, ParaQueryUtil.getPlFeautreNames(plout));
				String plType = ParaQueryUtil.getPLType(QueryUtil.getPlByExactName(plout, session)).getName();
				System.out.println(plout + " @ " + "Part:" + data[4].toString() + " ~ " + data[13].toString() + " ~ " + data[14].toString() + " idx:" + partData.indexOf(data[13].toString()));
				if(data[4].toString().equals(lastPart))
				{
					// partData.add(data[11].toString()); /* fet Value */
					partData.set(partData.indexOf(data[13].toString()), data[14].toString());
					if(i == result.size() - 1)
					{
						plFets = plFetsMap.get(lastPl);
						for(int j = 0; j < plFets.size(); j++)
						{
							if(partData.indexOf(plFets.get(j)) != -1)
								partData.set(partData.indexOf(plFets.get(j)), "");
						}
						if(allData.get(lastPl) == null)
						{

							plData = new ArrayList<ArrayList<String>>();

							if(!partData.isEmpty())
							{
								partData.add(lastDesc);
								plData.add(partData);
							}

						}
						else
						{
							plData = allData.get(lastPl);
							partData.add(lastDesc);
							plData.add(partData);
						}
						if(!plData.isEmpty())
						{
							allData.put(lastPl, plData);
						}
					}
					continue;
				}
				else
				{
					plFets = plFetsMap.get(lastPl);
					if(plFets != null)
					{
						for(int j = 0; j < plFets.size(); j++)
						{
							if(partData.indexOf(plFets.get(j)) != -1)
								partData.set(partData.indexOf(plFets.get(j)), "");
						}
					}
					if(allData.get(lastPl) == null)
					{
						plData = new ArrayList<ArrayList<String>>();
						if(!partData.isEmpty())
						{
							partData.add(lastDesc);
							plData.add(partData);
						}
					}
					else
					{
						plData = allData.get(lastPl);
						partData.add(lastDesc);
						plData.add(partData);
					}
					if(!plData.isEmpty())
					{
						allData.put(lastPl, plData);
					}
					boolean NPIFlag = isNPITaskType(usersId, plout, vendorName, null, docStatus, startDate, endDate, docsIds);
					partData = new ArrayList<String>();
					partData.add(data[0].toString());
					/** Pl Name */
					partData.add(data[22].toString());
					/** Feedback Type */
					partData.add(data[1].toString());
					/** user name */
					partData.add("");
					/** Status */
					partData.add("");
					/** Comment */
					partData.add("");
					/** FBStatus */
					partData.add("");
					/** Wrong Features */
					partData.add("");
					/** FBComment */
					partData.add("");
					/** C_action */
					partData.add("");
					/** P_action */
					partData.add("");
					/** Root_cause */
					partData.add("");
					/** ActionDueDate */
					partData.add(data[2].toString());
					/** Task Type */
					partData.add(data[3].toString());
					/** Supplier */
					partData.add(data[17].toString());
					/** com id */
					partData.add(data[4].toString());
					/** Part Number */
					partData.add(data[5].toString());
					/** family */

					if(plType.equals("Semiconductor"))
					{
						partData.add(data[6] == null ? "" : data[6].toString());
						/** family cross */
						partData.add(data[7].toString());
						/** generic */
						partData.add((data[8] == null) ? "" : data[8].toString());
						/** Mask */
						if(NPIFlag)
						{
							
							/** NPI Flag */
							partData.add((data[9] != null && data[9].toString().equals("1")) ? "Yes" : "");
							/** NPI news */
							partData.add((data[10] == null) ? "" : data[10].toString());
							List<String> newsData = getNewsLink(data[12].toString());
							/** NPI desc */
							partData.add(newsData.get(1));
							/** NPI date */
							partData.add(newsData.get(2));
						}
						partData.add(data[12].toString());
						/** pdf url */
					}
					else
					{
						partData.add((data[8] == null) ? "" : data[8].toString());
						/** Mask */
						if(NPIFlag)
						{
							/** NPI Flag */
							partData.add((data[9] != null && data[9].toString().equals("1")) ? "Yes" : "");
							/** NPI news */
							partData.add((data[10] == null) ? "" : data[10].toString());
							List<String> newsData = getNewsLink(data[12].toString());
							/** NPI desc */
							partData.add(newsData.get(1));
							/** NPI date */
							partData.add(newsData.get(2));
						}
						partData.add(data[12].toString());
						/** pdf url */
					}
					plFets = plFetsMap.get(plout);
					partData.addAll(plFets);
					partData.set(partData.indexOf(data[13].toString()), data[14].toString());
					// x= plFets.indexOf(data[10].toString());
					// partData.add(data[11].toString()); /* fet Value */

				}

				if(i == result.size() - 1)
				{
					for(int j = 0; j < plFets.size(); j++)
					{
						if(partData.indexOf(plFets.get(j)) != -1)
							partData.set(partData.indexOf(plFets.get(j)), "");
					}

					if(allData.get(plout) == null)
					{
						plData = new ArrayList<ArrayList<String>>();
						if(!partData.isEmpty())
						{
							partData.add(desc);
							plData.add(partData);
						}
					}
					else
					{
						plData = allData.get(plout);
						partData.add(desc);
						plData.add(partData);
					}
					allData.put(plout, plData);
					// plData = new ArrayList<ArrayList<String>>();
					// if (!partData.isEmpty()) {
					// partData.add(desc);
					// plData.add(partData);
					// allData.put(plout, plData);
					// }
				}

				lastPart = data[4].toString();
				lastDesc = (data[11] == null) ? "" : data[11].toString();
				lastPl = plout;
			}
		}finally
		{
			session.close();
		}
		return allData;

	}

	public static Map<String, ArrayList<ArrayList<String>>> getQAPDFData(Long[] usersId, String plName, String vendorName, String type, Date startDate, Date endDate, Long[] docsIds, Long qaUser, String availableStatus, String Pltype)
			throws Exception
	{
		Session session = SessionUtil.getSession();
		Map<String, ArrayList<ArrayList<String>>> allData = new HashMap<String, ArrayList<ArrayList<String>>>();

		SQLQuery query = null;
		try
		{
			StringBuffer qury = new StringBuffer();
			String Sql = "";
			Sql = " SELECT GET_PL_NAME (t.PL_ID) plName, getuserName (T.USER_ID), GetTaskTypeName";
			Sql = Sql + " (t.TRACKING_TASK_TYPE_ID) task_type, GETSUPPLIERNAME (t.supplier_id) supName,";
			Sql = Sql + " C.PART_NUMBER, FAM.NAME, Get_family_crossName (C.FAMILY_CROSS_ID) family_Cros";
			Sql = Sql + "s, Get_GENERIC_Name (C.GENERIC_ID) generic_Nam, GET_MSK_Value (c.MASK_ID, C.PA";
			Sql = Sql + "RT_NUMBER) MASK, c.NPI_FLAG, GET_News_PDF_URL (c.DOCUMENT_ID, c.SUPPLIER_ID) n";
			Sql = Sql + "ewsLike, c.DESCRIPTION, GETPDFURLBYDOCID (t.DOCUMENT_ID) pdfurl, F.NAME fetNam";
			Sql = Sql + "e, G.GROUP_FULL_VALUE fetVal, t.ASSIGNED_DATE, GetTaskStatusName (TRACKING_TAS";
			Sql = Sql + "K_STATUS_ID) task_Status, C.COM_ID, R.PL_FEATURE_ID, R.GROUP_APPROVED_VALUE_ID";
			Sql = Sql + ", t.DOCUMENT_ID, t.PL_ID,Decode(C.DONEFLAG,null,'No',0,'No',1,'Yes')  DONEFLAG,";
			Sql = Sql + " Decode(C.EXTRACTIONFLAG,null,'No',0,'No',1,'Yes') EXTRACTIONFLAG FROM TRACKING_PARAMETRIC T, Part_COMPONENT c, family ";
			Sql = Sql + "fam, PARAMETRIC_REVIEW_DATA r, pl_feature_unit pf, feature f, PARAMETRIC_APPRO";
			Sql = Sql + "VED_GROUP g WHERE t.DOCUMENT_ID = c.DOCUMENT_ID AND T.SUPPLIER_PL_ID = C.SUPPL";
			Sql = Sql + "IER_PL_ID AND c.COM_ID = R.COM_ID AND C.FAMILY_ID = FAM.ID AND R.PL_FEATURE_ID";
			Sql = Sql + " = PF.ID AND PF.FET_ID = F.ID AND R.GROUP_APPROVED_VALUE_ID = G.ID AND T.QA_US";
			Sql = Sql + "ER_ID = " + qaUser + " AND T.TRACKING_TASK_STATUS_ID = getTaskstatusId ('" + availableStatus + "')";
			qury.append(Sql);

			if(plName != null && !plName.equals("All"))
			{
				qury.append("  AND T.PL_ID=GET_PL_ID('" + plName + "')");
			}
			if(Pltype != null && !Pltype.equals("All"))
			{
				qury.append("  AND AUTOMATION2.Get_PL_Type(T.PL_ID)='" + Pltype + "'");
			}
			if(!vendorName.equals("All") && vendorName != null)
			{
				qury.append("  and T.SUPPLIER_ID=GETSUPPLIERID('" + vendorName + "')");
			}
			if(type != null && !type.equals("All"))
			{
				if(type.equals("NPI"))
				{
					qury.append(" AND t.TRACKING_TASK_TYPE_ID in(getTaskTypeId('" + StatusName.npi + "'),getTaskTypeId('" + StatusName.npiTransferred + "'),getTaskTypeId('" + StatusName.npiUpdate + "'))");
				}
				else
				{
					qury.append(" AND t.TRACKING_TASK_TYPE_ID = getTaskTypeId('" + type + "')");
				}
			}
			if(!(usersId.length == 0) && usersId != null)
			{
				qury.append(" AND T.USER_ID IN (" + getArrayAsCommaSeperatedList(usersId) + ")");
			}
			if(docsIds != null && docsIds.length > 0)
			{
				qury.append(" AND c.DOCUMENT_ID in ( " + getArrayAsCommaSeperatedList(docsIds) + " )");
			}
			if(startDate != null && endDate != null)
			{
				SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
				System.out.println(formatter.format(startDate) + "**************" + formatter.format(endDate));

				String dateRangeCond = " AND t.ASSIGNED_DATE BETWEEN TO_DATE ('" + formatter.format(startDate) + "','DD/MM/RRRR')AND  TO_DATE ('" + formatter.format(endDate) + "','DD/MM/RRRR')";
				qury.append(dateRangeCond);
				// qury = qury +
				// " AND t.ASSIGNED_DATE BETWEEN TO_DATE ('01/11/2012', 'DD/MM/RRRR') AND  TO_DATE ('03/03/2013', 'DD/MM/RRRR')";
			}
			qury.append(" ORDER BY   T.DOCUMENT_ID,plName, C.PART_NUMBER, PF.DEVELOPMENT_ORDER");
			// Medical Application|DD Review|Minimum Storage Temperature

			System.out.println(qury.toString());
			ArrayList<Object[]> result = (ArrayList<Object[]>) session.createSQLQuery(qury.toString()).list();
			ArrayList<ArrayList<String>> plData = new ArrayList<ArrayList<String>>();
			ArrayList<String> partData = new ArrayList<String>();
			List<String> plFets = new ArrayList<String>();
			Map<String, List<String>> plFetsMap = new HashMap<String, List<String>>();
			System.out.println("All Data Size:" + result.size());
			String lastPart = "";
			String lastPl = "";
			String lastDesc = "";
			int x = -1;
			for(int i = 0; i < result.size(); i++)
			{
				Object[] data = result.get(i);
				String plout = data[0].toString();

				String desc = (data[11] == null) ? "" : data[11].toString();
				// int fetOrder= getPlFeatureByExactName(data[10].toString(), plout, session).getDevelopmentOrder();
				if(plFetsMap.get(plout) == null)
					plFetsMap.put(plout, ParaQueryUtil.getPlFeautreNames(plout));
				String plType = ParaQueryUtil.getPLType(QueryUtil.getPlByExactName(plout, session)).getName();
				System.out.println(plout + " @ " + "Part:" + data[4].toString() + " ~ " + data[13].toString() + " ~ " + data[14].toString() + " idx:" + partData.indexOf(data[13].toString()));
				if(data[4].toString().equals(lastPart))
				{
					// partData.add(data[11].toString()); /* fet Value */
					partData.set(partData.indexOf(data[13].toString()), data[14].toString());
					if(i == result.size() - 1)
					{
						plFets = plFetsMap.get(lastPl);
						for(int j = 0; j < plFets.size(); j++)
						{
							if(partData.indexOf(plFets.get(j)) != -1)
								partData.set(partData.indexOf(plFets.get(j)), "");
						}
						if(allData.get(lastPl) == null)
						{

							plData = new ArrayList<ArrayList<String>>();

							if(!partData.isEmpty())
							{
								partData.add(lastDesc);
								plData.add(partData);
							}

						}
						else
						{
							plData = allData.get(lastPl);
							partData.add(lastDesc);
							plData.add(partData);
						}
						if(!plData.isEmpty())
						{
							allData.put(lastPl, plData);
						}
					}
					continue;
				}
				else
				{
					plFets = plFetsMap.get(lastPl);
					if(plFets != null)
					{
						for(int j = 0; j < plFets.size(); j++)
						{
							if(partData.indexOf(plFets.get(j)) != -1)
								partData.set(partData.indexOf(plFets.get(j)), "");
						}
					}
					if(allData.get(lastPl) == null)
					{
						plData = new ArrayList<ArrayList<String>>();
						if(!partData.isEmpty())
						{
							partData.add(lastDesc);
							plData.add(partData);
						}
					}
					else
					{
						plData = allData.get(lastPl);
						partData.add(lastDesc);
						plData.add(partData);
					}
					if(!plData.isEmpty())
					{
						allData.put(lastPl, plData);
					}
					boolean NPIFlag = isNPITaskType(usersId, plout, vendorName, type, availableStatus, startDate, endDate, docsIds);
					partData = new ArrayList<String>();
					/** Pl Name */
					partData.add(data[0].toString());
					/** Pl Type */
					partData.add(plType);
					/** user name */
					partData.add(data[1].toString());
					// /** Status */
					// partData.add("");
					// /** Comment */
					// partData.add("");
					/** Task Type */
					partData.add(data[2].toString());
					/** Supplier */
					partData.add(data[3].toString());
					/** Done flag */
					partData.add(data[22].toString());
					/** Extraction flag */
					partData.add(data[23].toString());
					/** com_id */
					partData.add(data[17].toString());
					/** Part Number */
					partData.add(data[4].toString());
					/** family */
					partData.add(data[5].toString());

					if(plType.equals("Semiconductor"))
					{
						/** family cross */

						partData.add(data[6] != null ? data[6].toString() : "");
						/** generic */
						partData.add(data[7] != null ? data[7].toString() : "");
						/** Mask */
						partData.add((data[8] == null) ? "" : data[8].toString());

						if(NPIFlag)
						{
							/** NPI Flag */
							partData.add((data[9] != null && data[9].toString().equals("1")) ? "Yes" : "");
							/** news link */
							partData.add((data[10] == null) ? "" : data[10].toString());
							List<String> newsData = getNewsLink(data[12].toString());
							/** NPI desc */
							partData.add(newsData.get(1));
							/** NPI date */
							partData.add(newsData.get(2));

						}
						/** pdf url */
						partData.add(data[12].toString());

					}
					else
					{
						/** Mask */
						partData.add((data[8] == null) ? "" : data[8].toString());

						if(NPIFlag)
						{
							/** NPI Flag */
							partData.add((data[9] != null && data[9].toString().equals("1")) ? "Yes" : "");
							/** news link */
							partData.add((data[10] == null) ? "" : data[10].toString());
							List<String> newsData = getNewsLink(data[12].toString());
							/** NPI desc */
							partData.add(newsData.get(1));
							/** NPI date */
							partData.add(newsData.get(2));
						}
						/** pdf url */
						partData.add(data[12].toString());
					}
					plFets = plFetsMap.get(plout);
					partData.addAll(plFets);
					partData.set(partData.indexOf(data[13].toString()), data[14].toString());
				}

				if(i == result.size() - 1)
				{
					for(int j = 0; j < plFets.size(); j++)
					{
						if(partData.indexOf(plFets.get(j)) != -1)
							partData.set(partData.indexOf(plFets.get(j)), "");
					}

					if(allData.get(plout) == null)
					{
						plData = new ArrayList<ArrayList<String>>();
						if(!partData.isEmpty())
						{
							partData.add(desc);
							plData.add(partData);
						}
					}
					else
					{
						plData = allData.get(plout);
						partData.add(desc);
						plData.add(partData);
					}
					allData.put(plout, plData);
				}

				lastPart = data[4].toString();
				lastDesc = (data[11] == null) ? "" : data[11].toString();
				lastPl = plout;
			}
		}finally
		{
			session.close();
		}
		return allData;

	}

	public static ArrayList<TableInfoDTO> getTlReviewFeedbackPDFs(Long[] usersId, String plName, String vendorName, String status, Date startDate, Date endDate, String feedbackTypeStr, long issuedToId, String issuer)
	{
		ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
		ArrayList<TableInfoDTO> tableData = new ArrayList<TableInfoDTO>();

		Session session = SessionUtil.getSession();

		try
		{
			Criteria criteria = session.createCriteria(TrackingParametric.class);
			if(!(usersId.length == 0) && usersId != null)
			{
				criteria.add(Restrictions.in("parametricUserId", usersId));
			}
			if(status != null && !status.equals("All"))
			{
				Criteria statusCriteria = session.createCriteria(TrackingTaskStatus.class);
				statusCriteria.add(Restrictions.eq("name", status));
				TrackingTaskStatus statusObj = (TrackingTaskStatus) statusCriteria.uniqueResult();
				criteria.add(Restrictions.eq("trackingTaskStatus", statusObj));
			}

			if(plName != null && !plName.equals("All"))
			{
				Criteria plCriteria = session.createCriteria(Pl.class);
				plCriteria.add(Restrictions.eq("name", plName));
				Pl pl = (Pl) plCriteria.uniqueResult();
				criteria.add(Restrictions.eq("pl", pl));
			}
			if(vendorName != null && !vendorName.equals("All"))
			{
				Criteria vendorCriteria = session.createCriteria(Supplier.class);
				vendorCriteria.add(Restrictions.eq("name", vendorName));
				Supplier supplier = (Supplier) vendorCriteria.uniqueResult();
				criteria.add(Restrictions.eq("supplier", supplier));
			}

			if(feedbackTypeStr != null && !feedbackTypeStr.equals("All"))
			{
				List<Document> docs = getFeedbackDocs(feedbackTypeStr);
				if(!docs.isEmpty())
					criteria.add(Restrictions.in("document", docs));
			}

			Set<Document> docsSet = new HashSet<Document>();
			Criteria partsFeedbackCriteria = session.createCriteria(ParametricFeedbackCycle.class);
			partsFeedbackCriteria.add(Restrictions.eq("feedbackRecieved", 0l));
			partsFeedbackCriteria.add(Restrictions.eq("issuedTo", issuedToId));
			if(issuer != null && !issuer.equals("All"))
			{
				GrmUser issuedby = ParaQueryUtil.getGRMUserByName(issuer);
				partsFeedbackCriteria.add(Restrictions.eq("issuedBy", issuedby.getId()));
			}
			if(startDate != null && endDate != null)
			{
				partsFeedbackCriteria.add(Expression.between("storeDate", startDate, endDate));
			}
			List<ParametricFeedbackCycle> parametricfeedbackcycles = partsFeedbackCriteria.list();
			if((parametricfeedbackcycles != null))
			{
				for(int i = 0; i < parametricfeedbackcycles.size(); i++)
				{
					docsSet.add(parametricfeedbackcycles.get(i).getParametricFeedback().getDocument());
				}
			}
			if(!docsSet.isEmpty())
				criteria.add(Restrictions.in("document", docsSet));

			// Criteria pdfCriteria = criteria.createCriteria("document");
			// pdfCriteria.add(Restrictions.isNotNull("pdf"));
			List list = criteria.list();
			for(int i = 0; i < list.size(); i++)
			{
				TrackingParametric obj = (TrackingParametric) list.get(i);
				TableInfoDTO docInfo = new TableInfoDTO();
				int infectedParts = getInfectedPartsByDoc(obj.getDocument().getId());
				int infectedTaxonomies = getInfectedTaxonomiesByDoc(obj.getDocument().getId());
				docInfo.setPdfUrl(obj.getDocument().getPdf().getSeUrl());
				docInfo.setPlName(obj.getPl().getName());
				docInfo.setSupplierName(obj.getSupplier().getName());
				docInfo.setStatus(obj.getTrackingTaskStatus().getName());
				docInfo.setTaskType(obj.getTrackingTaskType().getName());
				docInfo.setInfectedParts(infectedParts);
				docInfo.setInfectedTaxonomies(infectedTaxonomies);
				docInfo.setDevUserName(ParaQueryUtil.getGRMUser(obj.getParametricUserId()).getFullName());
				docInfo.setExtracted(obj.getExtractionStatus() == null ? "No" : "Yes");
				Date finishDate = obj.getFinishedDate();
				if(finishDate != null)
				{
					docInfo.setDate(finishDate.toString());
				}

				// docInfo.add(obj.getDocument().getPdf().getSeUrl());
				// docInfo.add(obj.getSupplier().getName());
				// docInfo.add(obj.getPl().getName());
				// docInfo.add(obj.getTrackingTaskType().getName());
				// docInfo.add(obj.getTrackingTaskStatus().getName());
				// docInfo.add(getGRMUser(obj.getParametricUserId()).getFullName());
				// result.add(docInfo);
				tableData.add(docInfo);
			}
		}finally
		{
			session.close();
		}
		return tableData;
	}

	public static List<String> getNewsLink(String pdfURL)
	{
		Session session = SessionUtil.getSession();
		String newsLink = null, newsDesc = null, newsDate = null;
		List<String> newsData = new ArrayList<String>();
		try
		{
			SQLQuery query = session.createSQLQuery("select GETPDFURLbydoc(doc_id),NEWS_TITLE,NEWS_DATE from TBL_NEW_NPI where OFFLINE_DS =GET_DOCID_BY_PDFURL('" + pdfURL + "')");
			Object[] list = (Object[]) query.uniqueResult();
			newsLink = (list[0] == null) ? "" : list[0].toString();
			newsDesc = (list[1] == null) ? "" : list[1].toString();
			newsDate = (list[2] == null) ? "" : list[2].toString();
			newsData.add(newsLink);
			newsData.add(newsDesc);
			newsData.add(newsDate);
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}finally
		{
			session.close();
		}
		return newsData;
	}

	public static String getNewsLinkold(long pdfId)
	{
		Session session = SessionUtil.getSession();
		String newsLink = null;
		try
		{
			SQLQuery query = session.createSQLQuery("select pdf_url from cm.pdf_table where pdf_id in (select pdf_id from cm.tbl_npi_new where offline_ds=" + pdfId + ")");
			newsLink = (String) query.uniqueResult();

		}catch(Exception ex)
		{
			ex.printStackTrace();
		}finally
		{
			session.close();
			return newsLink;
		}
	}

	public static String getTaxonomyPath(long pdfId)
	{
		Session session = SessionUtil.getSession();
		String newsLink = null;
		try
		{
			SQLQuery query = session.createSQLQuery("select mvcode from cm.pdf_table where pdf_id=" + pdfId);
			newsLink = (String) query.uniqueResult();

		}catch(Exception ex)
		{
			ex.printStackTrace();
		}finally
		{
			session.close();
			return newsLink;
		}
	}

	public static ArrayList<Map<String, String>> getParametricDataByPdfUrlAndPl(String pdfUrl, String plName) throws Exception
	{
		ArrayList<Map<String, String>> result = new ArrayList<Map<String, String>>();
		Map<String, String> partFetNameValMap = new HashMap<String, String>();
		Session session = SessionUtil.getSession();
		Document document = ParaQueryUtil.getDocumentBySeUrl(pdfUrl, session);
		Supplier supplier = document.getPdf().getSupplierUrl().getSupplier();
		String vendorName = supplier.getName();
		Pl pl = ParaQueryUtil.getPlByPlName(plName);
		String engName = ParaQueryUtil.getEngName(document, pl);
		SupplierPl suppPl = ParaQueryUtil.getSupplierPlByPlAndSup(supplier, pl);

		Criteria componentCriteria = session.createCriteria(PartComponent.class);
		componentCriteria.add(Restrictions.eq("document", document));
		componentCriteria.add(Restrictions.eq("supplierPl", suppPl));

		List components = componentCriteria.list();
		if(components != null)
		{
			for(int i = 0; i < components.size(); i++)
			{
				partFetNameValMap = new HashMap<String, String>();
				PartComponent component = (PartComponent) components.get(i);
				String comPartNum = component.getPartNumber();
				String family = component.getFamily().getName();
				String mask = component.getMasterPartMask().getMstrPart();
				String generic = component.getMapGeneric().getGeneric();
				String comment = getFeedbackCommentByComId(component.getComId());
				String partStatus = getPartStatusByComId(component.getComId());

				partFetNameValMap.put("Part Number", comPartNum);
				partFetNameValMap.put("Family", family);
				partFetNameValMap.put("Mask", mask);
				partFetNameValMap.put("Generic", generic);
				partFetNameValMap.put("Taxonomy", plName);
				partFetNameValMap.put("Eng Name", engName);
				partFetNameValMap.put("Supplier Name", vendorName);
				partFetNameValMap.put("Comment", comment);
				partFetNameValMap.put("Status", partStatus);

				Criteria reviewDataCriteria = session.createCriteria(ParametricReviewData.class);
				reviewDataCriteria.add(Restrictions.eq("component", component));
				List reviewDataObjs = reviewDataCriteria.list();
				if(reviewDataObjs != null)
				{
					for(int j = 0; j < reviewDataObjs.size(); j++)
					{
						ParametricReviewData parametricReviewData = (ParametricReviewData) reviewDataObjs.get(j);
						String featureName = parametricReviewData.getPlFeature().getFeature().getName();
						long groupId = parametricReviewData.getGroupApprovedValueId();
						String fetValue = ParaQueryUtil.getFetValue(groupId);
						partFetNameValMap.put(featureName, fetValue);
					}
				}
				else
				{
					System.out.println("No Review Data for this component : + " + component.getPartNumber() + "  !");
				}

				result.add(partFetNameValMap);

			}
		}
		else
		{
			System.out.println("No Components found for this document : " + document.getId() + "!");
		}

		// for ( Map<String, String> map : result ) {
		// Set<String> keys = map.keySet();
		// for ( String key : keys ) {
		// String val = map.get(key);
		// System.out.println(key + " : " + val );
		// }
		// }

		return result;
	}

	public static ArrayList<TableInfoDTO> getDevFeedbackPDF(long userId, String plName, String vendorName, String issuedBy, String feedbackTypeStr, Date startDate, Date endDate)
	{

		ArrayList<TableInfoDTO> tableData = new ArrayList<TableInfoDTO>();

		Session session = SessionUtil.getSession();
		GrmUser toUser = null;

		try
		{
			Set<Document> docsSet = new HashSet<Document>();
			Criteria criteria = session.createCriteria(TrackingParametric.class);
			criteria.add(Restrictions.eq("parametricUserId", userId));
			if(startDate != null && endDate != null)
			{
				criteria.add(Expression.between("finishedDate", startDate, endDate));
			}
			criteria.add(Restrictions.eq("trackingTaskStatus", ParaQueryUtil.getTrackingTaskStatus(session, StatusName.engFeedback)));

			if(plName != null && !plName.equals("All"))
			{
				Criteria plCriteria = session.createCriteria(Pl.class);
				plCriteria.add(Restrictions.eq("name", plName));
				Pl pl = (Pl) plCriteria.uniqueResult();
				criteria.add(Restrictions.eq("pl", pl));
			}
			if(!vendorName.equals("All") && vendorName != null)
			{
				Criteria vendorCriteria = session.createCriteria(Supplier.class);
				vendorCriteria.add(Restrictions.eq("name", vendorName));
				Supplier supplier = (Supplier) vendorCriteria.uniqueResult();
				criteria.add(Restrictions.eq("supplier", supplier));
			}

			Criteria ParametricFeedbackCriteria = session.createCriteria(ParametricFeedbackCycle.class);
			ParametricFeedbackCriteria.add(Restrictions.eq("issuedTo", userId));
			ParametricFeedbackCriteria.add(Restrictions.eq("feedbackRecieved", 0l));

			if(issuedBy != null && !issuedBy.equals("All"))
			{
				GrmUser byUser = ParaQueryUtil.getGRMUserByName(issuedBy);
				ParametricFeedbackCriteria.add(Restrictions.eq("issuedBy", byUser.getId()));
			}

			if(feedbackTypeStr != null && !feedbackTypeStr.equals("All"))
			{
				ParametricFeedbackCriteria.createAlias("parametricFeedback", "Feedback");
				ParametricFeedbackCriteria.add(Restrictions.eq("Feedback.trackingFeedbackType", ParaQueryUtil.getTrackingFeedbackType(feedbackTypeStr)));
			}

			List<ParametricFeedbackCycle> parametricfeedbackCycles = ParametricFeedbackCriteria.list();
			if(parametricfeedbackCycles != null)
			{
				for(int i = 0; i < parametricfeedbackCycles.size(); i++)
				{
					ParametricFeedbackCycle parametricfeedbackCycle = parametricfeedbackCycles.get(i);
					Document doc = parametricfeedbackCycle.getParametricFeedback().getDocument();
					docsSet.add(doc);
				}
			}

			if(docsSet.size() > 0)
			{
				criteria.add(Restrictions.in("document", docsSet));
			}

			List list = criteria.list();
			for(int i = 0; i < list.size(); i++)
			{
				TrackingParametric obj = (TrackingParametric) list.get(i);
				TableInfoDTO docInfo = new TableInfoDTO();
				int infectedParts = getInfectedPartsByDoc(obj.getDocument().getId());
				int infectedTaxonomies = getInfectedTaxonomiesByDoc(obj.getDocument().getId());
				docInfo.setPdfUrl(obj.getDocument().getPdf().getSeUrl());
				docInfo.setPlName(obj.getPl().getName());
				docInfo.setSupplierName(obj.getSupplier().getName());
				docInfo.setStatus(obj.getTrackingTaskStatus().getName());
				docInfo.setTaskType(obj.getTrackingTaskType().getName());
				docInfo.setDevUserName(ParaQueryUtil.getGRMUser(obj.getParametricUserId()).getFullName());
				docInfo.setExtracted(obj.getExtractionStatus() == null ? "No" : "Yes");
				Date finishDate = obj.getFinishedDate();
				if(finishDate != null)
				{
					docInfo.setDate(finishDate.toString());
				}
				docInfo.setInfectedParts(infectedParts);
				docInfo.setInfectedTaxonomies(infectedTaxonomies);
				tableData.add(docInfo);
			}

		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			session.close();
		}
		return tableData;
	}

	public static boolean saveParamtric(PartInfoDTO partInfo)
	{
		Session session = SessionUtil.getSession();
		try
		{
			PartComponent com = new PartComponent();
			MasterFamilyGeneric famGen = null;
			// long comId = System.nanoTime();
			TrackingParametric track = getTrackingParametricByPdfUrlAndSupName(partInfo.getPdfUrl(), partInfo.getPlName(), partInfo.getSupplierName(), session);
			// com.setComId(comId);
			// com.setDocument(getDocumentBySeUrl(partInfo.getPdfUrl(), session));
			// com.setSupplierPl(getSupplierPlBySupplierAndPl(QueryUtil.getPlByExactName(partInfo.getPlName(), session),
			// QueryUtil.getSupplierByExactName(partInfo.getSupplierName(), session), session));
			com.setDocument(track.getDocument());
			com.setSupplierPl(track.getSupplierPl());
			com.setSupplierId(track.getSupplier());
			com.setPdf(track.getDocument().getPdf());
			com.setPartNumber(partInfo.getPN());
			com.setStoreDate(new Date());
			Family family = ParaQueryUtil.getFamilyByExactName(partInfo.getFamily(), session);
			// if family not found insert new family record
			if(family == null)
			{
				family = insertFamily(partInfo.getFamily(), session);
			}
			if(family != null)
				// System.out.println("Com Id=" + comId + "  Fam Id=" + family.getId());
				com.setFamily(family);

			// com.setGenericFamily(getGenericFamilyByExactName(partInfo.getGeneric(), session));

			if(partInfo.getMask() != null)
			{

				MasterPartMask mask = getMask(partInfo.getMask());
				if(mask == null)
				{
					mask = insertMask(partInfo.getMask(), session);
				}
				com.setMasterPartMask(mask);
			}
			// NPI Flag part
			// if(partInfo.getNPIFlag() != null && partInfo.getNPIFlag().equalsIgnoreCase("Yes"))
			// {
			// com.setNpiFlag(1l);
			// // com.setAlu(partInfo.getNewsLink());
			// }
			// else
			// com.setNpiFlag(0l);
			if(partInfo.getGeneric() != null && partInfo.getFamilycross() != null)
			{
				MapGeneric gen = ParaQueryUtil.getGeneric(partInfo.getGeneric());
				if(gen == null)
				{
					gen = insertGeneric(partInfo.getGeneric(), session);
				}
				FamilyCross fam = ParaQueryUtil.getFamilyCross(partInfo.getFamilycross());
				if(fam == null)
				{
					fam = insertFamilyCross(partInfo.getFamilycross(), session);
				}
				com.setFamilyCross(fam);
				com.setMapGeneric(gen);

				// famGen = new MasterFamilyGeneric();
				// famGen.setComId(com);
				// famGen.setFamilyCross(fam);
				// famGen.setMapGeneric(gen);
			}

			if(partInfo.getFeedbackType() != null)
				com.setTrackingFeedbackType(getFeedbackType(partInfo.getFeedbackType()));
			/** Set description */
			if(partInfo.getDescription() != null)
				com.setDescription(partInfo.getDescription());
			// set generic family
			// if (genFamilyStr != null) {
			// GenericFamily genericFamily = ParaQueryUtil
			// .getGenericFamilyByExactName(genFamilyStr, session);
			// if (genericFamily == null) {
			// genericFamily = ParaQueryUtil.insertGenericFamily(
			// genFamilyStr, session);
			// }
			// if (genericFamily != null)
			// component.setGenericFamily(genericFamily);
			// }

			// if (partInfoArr.length == 7) {
			// String decsriptionStr = partInfoArr[6].equals("null") ? null : partInfoArr[6];
			// if (decsriptionStr != null)
			// component.setDescription(decsriptionStr);
			// }

			session.saveOrUpdate(com);
			if(partInfo.getNPIFlag() != null && partInfo.getNPIFlag().equalsIgnoreCase("Yes"))
			{
				insertNPIPart(com, partInfo.getNewsLink(), session);
			}

			if(famGen != null)
				session.saveOrUpdate(famGen);
			// session.beginTransaction().commit();

			Map<String, String> fetsMap = partInfo.getFetValues();
			Set<String> fetNames = fetsMap.keySet();
			for(String fetName : fetNames)
			{
				// session.beginTransaction().begin();
				PlFeature plFeature = ParaQueryUtil.getPlFeatureByExactName(fetName, partInfo.getPlName(), session);
				String fetValue = fetsMap.get(fetName);
				if(fetValue.isEmpty())
				{
					System.out.println(fetName + "Has blank Values ");
					continue;
				}
				ParametricApprovedGroup group = ParaQueryUtil.getParametricApprovedGroup(fetValue, plFeature, session);
				ParametricReviewData data = new ParametricReviewData();
				long id = QueryUtil.getRandomID();
				data.setComponent(com);
				data.setTrackingParametric(track);
				data.setGroupApprovedValueId(group.getId());
				data.setPlFeature(plFeature);
				data.setStoreDate(new Date());
				data.setId(id);
				session.saveOrUpdate(data);
				// session.beginTransaction().commit();
			}
			// }catch(Exception ex)
			// {
			// ex.printStackTrace();
			// return false;
		}finally
		{
			session.close();
		}
		return true;

	}

	public static boolean updateParamtric(PartInfoDTO partInfo)
	{
		Session session = SessionUtil.getSession();
		try
		{
			String partNumber = partInfo.getPN();
			String vendorName = partInfo.getSupplierName();
			PartComponent com = partInfo.getComponent();
			// getComponentByPartNumberAndSupplierName(partNumber, vendorName, session);

			if(com == null)
			{
				com = new PartComponent();

				TrackingParametric track = getTrackingParametricByPdfUrlAndSupName(partInfo.getPdfUrl(), partInfo.getPlName(), partInfo.getSupplierName(), session);
				com.setDocument(track.getDocument());
				com.setSupplierPl(track.getSupplierPl());
				com.setSupplierId(track.getSupplier());
				com.setPdf(track.getDocument().getPdf());
				com.setPartNumber(partInfo.getPN());
				com.setStoreDate(new Date());
			}
			Family family = ParaQueryUtil.getFamilyByExactName(partInfo.getFamily(), session);
			// if family not found insert new family record
			if(family == null)
			{
				family = insertFamily(partInfo.getFamily(), session);
			}
			if(family != null)
				com.setFamily(family);
			// com.setGenericFamily(getGenericFamilyByExactName(partInfo.getGeneric(), session));

			if(partInfo.getMask() != null)
			{
				MasterPartMask mask = getMask(partInfo.getMask());
				if(mask == null)
				{
					mask = insertMask(partInfo.getMask(), session);
				}
				com.setMasterPartMask(mask);
			}
			// NPI Flag part
			if(partInfo.getNPIFlag() != null && partInfo.getNPIFlag().equals("Yes"))
				com.setNpiFlag(1l);
			else
				com.setNpiFlag(0l);

			if(partInfo.getGeneric() != null && !partInfo.getGeneric().isEmpty() && partInfo.getFamilycross() != null && !partInfo.getFamilycross().isEmpty())
			{
				MapGeneric gen = ParaQueryUtil.getGeneric(partInfo.getGeneric());
				if(gen == null)
				{
					gen = insertGeneric(partInfo.getGeneric(), session);
				}
				FamilyCross fam = ParaQueryUtil.getFamilyCross(partInfo.getFamilycross());
				if(fam == null)
				{
					fam = insertFamilyCross(partInfo.getFamilycross(), session);
				}
				com.setFamilyCross(fam);

			}
			if(partInfo.getFeedbackType() != null)
				com.setTrackingFeedbackType(getFeedbackType(partInfo.getFeedbackType()));
			/** Set description */
			if(partInfo.getDescription() != null)
				com.setDescription(partInfo.getDescription());
			com.setUpdateDate(new Date());
			session.saveOrUpdate(com);
			session.beginTransaction().commit();

			Map<String, String> fetsMap = partInfo.getFetValues();
			Set<String> fetNames = fetsMap.keySet();
			for(String fetName : fetNames)
			{
				// session.beginTransaction().begin();
				PlFeature plFeature = ParaQueryUtil.getPlFeatureByExactName(fetName, partInfo.getPlName(), session);
				String fetValue = fetsMap.get(fetName);
				if((fetValue == null) || ("".equals(fetValue)))
				{
					continue;
				}
				ParametricApprovedGroup group = ParaQueryUtil.getParametricApprovedGroup(fetValue, plFeature, session);
				Criteria parametricReviewDataCriteria = session.createCriteria(ParametricReviewData.class);
				parametricReviewDataCriteria.add(Restrictions.eq("component", com));
				parametricReviewDataCriteria.add(Restrictions.eq("plFeature", plFeature));
				ParametricReviewData data = (ParametricReviewData) parametricReviewDataCriteria.uniqueResult();
				if(data == null)
				{
					data = new ParametricReviewData();
					long id = QueryUtil.getRandomID();
					data.setId(id);
					data.setComponent(com);
					data.setPlFeature(plFeature);
				}
				// data.setComponent(com);
				data.setGroupApprovedValueId(group.getId());
				// data.setPlFeature(plFeature);
				data.setModifiedDate(ParaQueryUtil.getDate());
				session.saveOrUpdate(data);
				// session.beginTransaction().commit();
			}
		}catch(Exception ex)
		{
			ex.printStackTrace();
			return false;
		}finally
		{
			session.close();
		}
		return true;
	}

	public static void saveTrackingParamtric(List<String> pdfSet, String plName, String supplierName, String status, String user) throws Exception
	{
		Session session = SessionUtil.getSession();
		try
		{
			// getTrackingTaskStatus(session, status);
			for(int i = 0; i < pdfSet.size(); i++)
			{
				Criteria criteria = session.createCriteria(TrackingParametric.class);
				Document document = ParaQueryUtil.getDocumentBySeUrl(pdfSet.get(i), session);
				criteria.add(Restrictions.eq("document", document));
				criteria.add(Restrictions.eq("pl", ParaQueryUtil.getPlByPlName(session, plName)));
				if(supplierName != null)
				{
					criteria.add(Restrictions.eq("supplier", ParaQueryUtil.getSupplierByExactName(session, supplierName)));
				}
				TrackingParametric track = (TrackingParametric) criteria.uniqueResult();
				System.err.println("Track Id=" + track.getId());
				track.setQaReviewDate(new Date());
				TrackingTaskType taskType = track.getTrackingTaskType();
				Pl pl = track.getPl();
				Long qaUserId = ParaQueryUtil.getQAUserId(pl, taskType);
				track.setQaUserId(qaUserId);
				track.setFinishedDate(ParaQueryUtil.getDate());
				// // if document has opened feedbacks
				// don't transfere to QA Team
				GrmUser issuedByUser = null;
				issuedByUser = ParaQueryUtil.getGRMUserByName(user);
				if(hasIssues(document.getId(), issuedByUser == null ? 0l : issuedByUser.getId()))
				{
					System.err.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^ Document : " + document.getId() + " Has opened feedbacks^^^^^^^^^^^^^^^^^^");
					continue;
				}
				// }
				TrackingTaskStatus trackingTaskStatus = ParaQueryUtil.getTrackingTaskStatus(session, status);
				track.setTrackingTaskStatus(trackingTaskStatus);
				session.saveOrUpdate(track);
				// session.beginTransaction().commit();
			}

		}finally
		{
			session.close();
		}

	}

	public static void saveTrackingParamtric(Map<String, List<String>> pdfSet, String status) throws Exception
	{
		Session session = SessionUtil.getSession();
		try
		{
			// getTrackingTaskStatus(session, status);
			for(String pdf : pdfSet.keySet())
			{
				Criteria criteria = session.createCriteria(TrackingParametric.class);
				Document document = ParaQueryUtil.getDocumentBySeUrl(pdf, session);
				criteria.add(Restrictions.eq("document", document));
				criteria.add(Restrictions.eq("pl", ParaQueryUtil.getPlByPlName(session, pdfSet.get(pdf).get(0))));
				if(pdfSet.get(pdf).get(1) != null && pdfSet.get(pdf).get(1).equals(""))
				{
					criteria.add(Restrictions.eq("supplier", ParaQueryUtil.getSupplierByExactName(session, pdfSet.get(pdf).get(1))));
				}
				TrackingParametric track = (TrackingParametric) criteria.uniqueResult();
				System.err.println("Track Id=" + track.getId());
				// track.setQaReviewDate(new Date());
				TrackingTaskType taskType = track.getTrackingTaskType();
				Pl pl = track.getPl();
				Long qaUserId = ParaQueryUtil.getQAUserId(pl, taskType);
				track.setQaUserId(qaUserId);
				track.setQaReviewDate(ParaQueryUtil.getDate());
				TrackingTaskStatus trackingTaskStatus = ParaQueryUtil.getTrackingTaskStatus(session, status);
				track.setTrackingTaskStatus(trackingTaskStatus);
				session.saveOrUpdate(track);
			}

		}finally
		{
			session.close();
		}

	}

	public static void saveQAPartsFeedback(List<PartInfoDTO> parts, String flowSource)
	{
		Session session = null;
		try
		{

			session = SessionUtil.getSession();
			for(int i = 0; i < parts.size(); i++)
			{
				PartInfoDTO partInfo = parts.get(i);
				String partNum = partInfo.getPN();
				String vendorName = partInfo.getSupplierName();
				// String status = partInfo.getStatus();
				String comment = partInfo.getComment();
				String issuedByName = partInfo.getIssuedBy();
				String issuedToName = partInfo.getIssuedTo();
				String feedbackStatus = partInfo.getFeedBackStatus();
				String feedbackTypeStr = partInfo.getFeedBackCycleType();
				TrackingTaskStatus trackingTaskStatus = null;
				if((feedbackStatus != null) && (!"".equals(feedbackStatus)))
				{
					Criteria trackingTaskStatusCriteria = session.createCriteria(TrackingTaskStatus.class);
					trackingTaskStatusCriteria.add(Restrictions.eq("name", feedbackStatus));
					trackingTaskStatus = (TrackingTaskStatus) trackingTaskStatusCriteria.uniqueResult();
				}

				PartComponent component = getComponentByPartNumberAndSupplierName(partNum, vendorName, session);
				GrmUser issuedByUser = ParaQueryUtil.getGRMUserByName(issuedByName);
				GrmUser issuedToUser = ParaQueryUtil.getGRMUserByName(issuedToName);
				Date date = ParaQueryUtil.getDate();

				// if feedback posted already return
				Criteria criteria = session.createCriteria(PartsFeedback.class);
				criteria.add(Restrictions.eq("partComponent", component));
				criteria.add(Restrictions.eq("feedbackRecieved", 0l));
				criteria.add(Restrictions.eq("issuedById", issuedByUser.getId()));
				criteria.add(Restrictions.eq("issuedToId", issuedToUser.getId()));
				PartsFeedback alreadyPostedFeedBack = (PartsFeedback) criteria.uniqueResult();
				if(alreadyPostedFeedBack != null)
				{
					continue;
				}

				Criteria partsFeedbackCriteria = session.createCriteria(PartsFeedback.class);
				partsFeedbackCriteria.add(Restrictions.eq("partComponent", component));
				// partsFeedbackCriteria.add(Restrictions.eq("fbComment", comment));
				partsFeedbackCriteria.add(Restrictions.eq("feedbackRecieved", 0l));
				// partsFeedbackCriteria.add(Restrictions.eq("issuedById", issuedToUser.getId()));
				partsFeedbackCriteria.add(Restrictions.eq("issuedToId", issuedByUser.getId()));

				PartsFeedback oldFeedback = (PartsFeedback) partsFeedbackCriteria.uniqueResult();

				TrackingFeedbackType feedbackType = null;
				if(feedbackTypeStr != null)
				{
					feedbackType = ParaQueryUtil.getTrackingFeedbackType(feedbackTypeStr);
				}

				// TrackingFeedbackType thisFlowSource = null;
				// if(flowSource != null)
				// {
				// JOptionPane.showMessageDialog(null, "wrong FlowSpure name");
				// }
				// thisFlowSource = ParaQueryUtil.getTrackingFeedbackType(flowSource);

				PartsFeedback partsFeedback = new PartsFeedback();
				partsFeedback.setId(QueryUtil.getRandomID());
				partsFeedback.setFbComment(comment);
				partsFeedback.setTrackingFeedbackType(feedbackType);
				partsFeedback.setPartComponent(component);
				partsFeedback.setIssuedById(issuedByUser.getId());
				partsFeedback.setIssuedToId(issuedToUser.getId());
				partsFeedback.setStoreDate(date);
				partsFeedback.setTrackingTaskStatus(trackingTaskStatus);
				// partsFeedback.setFlowSource(thisFlowSource);
				if("Feedback Closed".equals(feedbackStatus))
				{
					partsFeedback.setFeedbackRecieved(1l);
				}
				else
				{
					partsFeedback.setFeedbackRecieved(0l);
				}

				if(oldFeedback != null)
				{
					partsFeedback.setFlowSource(oldFeedback.getFlowSource());
					oldFeedback.setFeedbackRecieved(1l); // it's answered
					session.saveOrUpdate(oldFeedback);
					if(feedbackTypeStr == null)
					{
						partsFeedback.setTrackingFeedbackType(oldFeedback.getTrackingFeedbackType());
					}
				}
				else
				{
					TrackingFeedbackType thisFlowSource = null;
					thisFlowSource = ParaQueryUtil.getTrackingFeedbackType(flowSource);
					partsFeedback.setFlowSource(thisFlowSource);
				}
				session.saveOrUpdate(partsFeedback);

				// session.beginTransaction().commit();
			}
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}finally
		{
			if((session != null) && (session.isOpen()))
			{
				session.close();
			}
		}
	}

	public static void savePartsFeedback(List<PartInfoDTO> parts)
	{
		Session session = null;

		try
		{
			session = SessionUtil.getSession();
			for(int i = 0; i < parts.size(); i++)
			{
				ParaFeedbackStatus paraFeedbackAction = null;
				ParaFeedbackStatus paraFeedbackStatus = null;
				ParaIssueType paraIssueType = null;
				ParametricFeedback FBObj = new ParametricFeedback();
				ParametricFeedbackCycle FBCyc = new ParametricFeedbackCycle();
				ParaFeedbackAction feedbackAction = null;
				TrackingFeedbackType trackingFeedbackType = null;
				PartInfoDTO partInfo = parts.get(i);
				String partNum = partInfo.getPN();
				String vendorName = partInfo.getSupplierName();
				String comment = partInfo.getComment();
				String issuedByName = partInfo.getIssuedBy();
				String issuedToName = partInfo.getIssuedTo();
				String feedbackStatus = partInfo.getFeedBackStatus();
				String feedbackTypeStr = partInfo.getFeedBackCycleType();
				String wrongfeatures = partInfo.getWrongFeatures();
				PartComponent component = partInfo.getComponent();
				GrmUser issuedByUser = ParaQueryUtil.getGRMUserByName(issuedByName);
				GrmUser issuedToUser = ParaQueryUtil.getGRMUserByName(issuedToName);
				long issedto = issuedToUser.getId();
				Date date = ParaQueryUtil.getDate();

				Criteria criteria = null;

				if(feedbackTypeStr != null && !feedbackTypeStr.isEmpty())
				{
					criteria = session.createCriteria(ParaIssueType.class);
					System.out.println(feedbackTypeStr);
					criteria.add(Restrictions.eq("issueType", feedbackTypeStr));
					paraIssueType = (ParaIssueType) criteria.uniqueResult();
				}

				Document document = null;

				document = ParaQueryUtil.getDocumnetByPdfUrl(partInfo.getPdfUrl());

				String fbStatus = StatusName.inprogress;
				String FBAction = feedbackStatus;
				System.out.println(feedbackStatus);
				long fbRecieved = 0l;
				if(feedbackStatus.equals(StatusName.fbClosed))
				{
					fbRecieved = 1l;
					fbStatus = StatusName.closed;
					FBAction = StatusName.accept;
				}
				criteria = session.createCriteria(ParaFeedbackStatus.class);
				System.out.println(FBAction);
				criteria.add(Restrictions.eq("feedbackStatus", FBAction));
				paraFeedbackAction = (ParaFeedbackStatus) criteria.uniqueResult();//

				criteria = session.createCriteria(ParaFeedbackStatus.class);
				criteria.add(Restrictions.eq("feedbackStatus", fbStatus));
				paraFeedbackStatus = (ParaFeedbackStatus) criteria.uniqueResult();//

				criteria = session.createCriteria(TrackingFeedbackType.class);
				System.out.println(partInfo.getFbtype());
				criteria.add(Restrictions.eq("name", partInfo.getFbtype()));
				trackingFeedbackType = (TrackingFeedbackType) criteria.uniqueResult();

				Criteria fbcriteria = session.createCriteria(ParametricFeedbackCycle.class);
				fbcriteria.add(Restrictions.eq("fbItemValue", component.getPartNumber()));
				fbcriteria.add(Restrictions.eq("issuedTo", issuedByUser.getId()));
				fbcriteria.add(Restrictions.eq("feedbackRecieved", 0l));

				ParametricFeedbackCycle parametricFeedbackCycle = (ParametricFeedbackCycle) fbcriteria.uniqueResult();
				if(parametricFeedbackCycle != null)
				{
					parametricFeedbackCycle.setFeedbackRecieved(1l);
					session.saveOrUpdate(parametricFeedbackCycle);
					FBObj = parametricFeedbackCycle.getParametricFeedback();
					FBObj.setParaFeedbackStatus(paraFeedbackStatus);
				}
				else if(parametricFeedbackCycle == null)
				{
					FBObj.setId(System.nanoTime());
					if(paraIssueType != null)
						FBObj.setParaIssueType(paraIssueType);
					FBObj.setParaFeedbackStatus(paraFeedbackStatus);
					FBObj.setStoreDate(new Date());
					FBObj.setFbInitiator(issuedByUser.getId());
					FBObj.setTrackingFeedbackType(trackingFeedbackType);
					FBObj.setItemId(component.getComId());
					FBObj.setType("P");
					FBObj.setDocument(document);
				}
				FBCyc.setId(System.nanoTime());
				FBCyc.setParametricFeedback(FBObj);
				FBCyc.setFbItemValue(component.getPartNumber());
				FBCyc.setFbComment(comment);
				FBCyc.setIssuedBy(issuedByUser.getId());
				FBCyc.setIssuedTo(issedto);
				FBCyc.setStoreDate(date);
				FBCyc.setParaFeedbackStatus(paraFeedbackAction);
				FBCyc.setFeedbackRecieved(fbRecieved);

				if(partInfo.getCAction() != null && partInfo.getPAction() != null && partInfo.getRootCause() != null && partInfo.getActinDueDate() != null)
				{
					if(!partInfo.getCAction().equals("") && !partInfo.getPAction().equals("") && !partInfo.getRootCause().equals("") && !partInfo.getActinDueDate().equals(""))
					{
						feedbackAction = ApprovedDevUtil.getParaAction(partInfo.getCAction(), partInfo.getPAction(), partInfo.getRootCause(), partInfo.getActinDueDate(), session);
						if(feedbackAction != null)
						{
							FBCyc.setParaFeedbackAction(feedbackAction);
						}
					}
				}
				session.saveOrUpdate(FBObj);
				session.saveOrUpdate(FBCyc);
				session.beginTransaction().commit();
				if(wrongfeatures != null && !wrongfeatures.isEmpty())
					savewrongfeatures(session, FBObj, comment, wrongfeatures, parametricFeedbackCycle);
				if(fbStatus.equals(StatusName.closed))
				{
					System.err.println("edit in summary status");
					editfbsummarybyclosedfb(FBObj, session);
				}
			}
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}finally
		{
			if((session != null) && (session.isOpen()))
			{
				session.close();
			}
		}
	}

	private static void editfbsummarybyclosedfb(ParametricFeedback fBObj, Session session)
	{
		double defectiveparts = 0;
		double defectRatePart = 0;
		double defectivecells = 0;
		double defectRatecell = 0;
		long sampleparts = 0l;
		long testedcells = 0l;
		int fetsnotissue = 0;
		int fetsissue = 0;
		ParaSummaryStatus parasummarystatus = fBObj.getParaSummaryStatus();
		Criteria cri = session.createCriteria(ParaFeedbackFets.class);
		cri.add(Restrictions.eq("paraFeedbackId", fBObj));
		int fets = cri.list() == null ? 0 : cri.list().size();
		if(fets > 0)
		{
			cri = session.createCriteria(ParaFeedbackFets.class);
			cri.add(Restrictions.eq("paraFeedbackId", fBObj));
			cri.add(Restrictions.eq("status", 0l));
			fetsnotissue = cri.list() == null ? 0 : cri.list().size();
			cri = session.createCriteria(ParaFeedbackFets.class);
			cri.add(Restrictions.eq("paraFeedbackId", fBObj));
			cri.add(Restrictions.eq("status", 1l));
			fetsissue = cri.list() == null ? 0 : cri.list().size();
			if(fetsissue > 0)
				defectiveparts = parasummarystatus.getDefectiveParts();
			else
				defectiveparts = parasummarystatus.getDefectiveParts() - 1;
		}
		if(parasummarystatus != null)
		{
			sampleparts = parasummarystatus.getSampleParts();
			testedcells = parasummarystatus.getTotalTestedCells();
			defectRatePart = defectiveparts / (sampleparts == 0l ? 1l : sampleparts);
			defectivecells = parasummarystatus.getDefectiveCells() - fetsnotissue;
			defectRatecell = defectivecells / (testedcells == 0l ? 1l : testedcells);
			parasummarystatus.setDefectiveParts(defectiveparts);
			parasummarystatus.setDefectRatePart(defectRatePart);
			parasummarystatus.setDefectiveCells(defectivecells);
			parasummarystatus.setDefectRateCell(defectRatecell);
			session.saveOrUpdate(parasummarystatus);
			session.beginTransaction().commit();
		}
	}

	private static void savewrongfeatures(Session session, ParametricFeedback FBObj, String comment, String wrongfeatures, ParametricFeedbackCycle parametricFeedbackCycle)
	{
		Criteria criteria;
		if(parametricFeedbackCycle == null)
		{
			if(wrongfeatures != null && !wrongfeatures.isEmpty())
			{
				if(wrongfeatures.contains("|"))
				{
					String[] fets = wrongfeatures.split("\\|");
					for(int f = 0; f < fets.length; f++)
					{
						if(comment.contains("|"))
						{
							String[] comments = comment.split("\\|");
							ParaFeedbackFets paraFeedbackfets = new ParaFeedbackFets();
							Feature feature = ParaQueryUtil.getFeatureByName(fets[f]);
							paraFeedbackfets.setId(System.nanoTime());
							paraFeedbackfets.setFeature(feature);
							paraFeedbackfets.setParaFeedbackId(FBObj);
							paraFeedbackfets.setStatus(1l);
							paraFeedbackfets.setFetComment(comments[f]);
							paraFeedbackfets.setStoreDate(new Date());
							session.saveOrUpdate(paraFeedbackfets);
						}
						else
						{
							ParaFeedbackFets paraFeedbackfets = new ParaFeedbackFets();
							Feature feature = ParaQueryUtil.getFeatureByName(fets[f]);
							paraFeedbackfets.setId(System.nanoTime());
							paraFeedbackfets.setFeature(feature);
							paraFeedbackfets.setParaFeedbackId(FBObj);
							paraFeedbackfets.setStatus(1l);
							paraFeedbackfets.setFetComment(comment);
							paraFeedbackfets.setStoreDate(new Date());
							session.saveOrUpdate(paraFeedbackfets);
						}
					}
				}
				else
				{
					ParaFeedbackFets paraFeedbackfets = new ParaFeedbackFets();
					Feature feature = ParaQueryUtil.getFeatureByName(wrongfeatures);
					paraFeedbackfets.setId(System.nanoTime());
					paraFeedbackfets.setFeature(feature);
					paraFeedbackfets.setParaFeedbackId(FBObj);
					paraFeedbackfets.setStatus(1l);
					paraFeedbackfets.setFetComment(comment);
					paraFeedbackfets.setStoreDate(new Date());
					session.saveOrUpdate(paraFeedbackfets);
				}

			}
		}
		else if(parametricFeedbackCycle != null)
		{
			if(wrongfeatures != null && !wrongfeatures.isEmpty())
			{
				if(wrongfeatures.contains("|"))
				{
					String[] fets = wrongfeatures.split("\\|");
					for(int f = 0; f < fets.length; f++)
					{
						if(comment.contains("|"))
						{
							String[] comments = comment.split("\\|");
							criteria = session.createCriteria(ParaFeedbackFets.class);
							criteria.add(Restrictions.eq("paraFeedbackId", FBObj));
							List<ParaFeedbackFets> paraFeedbackfets = criteria.list();
							Feature feature = ParaQueryUtil.getFeatureByName(fets[f]);
							for(ParaFeedbackFets fet : paraFeedbackfets)
							{
								if(fet.getFeature().getName().equals(feature.getName()))
								{
									if(comments[f].equalsIgnoreCase("notIssue"))
										fet.setStatus(0l);
									else if(comments[f].equalsIgnoreCase("Issue"))
										fet.setStatus(1l);
									session.saveOrUpdate(fet);
									break;
								}
							}
						}
						else
						{
							criteria = session.createCriteria(ParaFeedbackFets.class);
							criteria.add(Restrictions.eq("paraFeedbackId", FBObj));
							List<ParaFeedbackFets> paraFeedbackfets = criteria.list();
							Feature feature = ParaQueryUtil.getFeatureByName(fets[f]);
							for(ParaFeedbackFets fet : paraFeedbackfets)
							{
								if(fet.getFeature().getName().equals(feature.getName()))
								{
									if(comment.equalsIgnoreCase("notIssue"))
										fet.setStatus(0l);
									else if(comment.equalsIgnoreCase("Issue"))
										fet.setStatus(1l);
									session.saveOrUpdate(fet);
									break;
								}
							}
						}
					}
				}
				else
				{
					criteria = session.createCriteria(ParaFeedbackFets.class);
					criteria.add(Restrictions.eq("paraFeedbackId", FBObj));
					List<ParaFeedbackFets> paraFeedbackfets = criteria.list();
					Feature feature = ParaQueryUtil.getFeatureByName(wrongfeatures);
					for(ParaFeedbackFets fet : paraFeedbackfets)
					{
						if(fet.getFeature().getName().equals(feature.getName()))
						{
							if(comment.equalsIgnoreCase("notIssue"))
								fet.setStatus(0l);
							else if(comment.equalsIgnoreCase("Issue"))
								fet.setStatus(1l);
							session.saveOrUpdate(fet);
							break;
						}
					}
				}

			}
		}
	}

	public static String sendFeedbackToSourcingTeam(String userName, String pdfUrl, String plName, String docFeedbackComment, String revUrl, String rightTax)
	{
		String status = "Done";
		Session session = null;
		try
		{
			session = SessionUtil.getSession();
			// session.beginTransaction().begin();
			Document document = ParaQueryUtil.getDocumnetByPdfUrl(pdfUrl);

			DocumentFeedback docFeedback = new DocumentFeedback();
			docFeedback.setId(QueryUtil.getRandomID());
			docFeedback.setDocument(document);
			Document revDocument = null;
			if(revUrl != null)
			{
				revDocument = ParaQueryUtil.getDocumnetByPdfUrl(revUrl);
				if(revDocument == null)
				{
					return "Error: Revision link not found";
				}
				docFeedback.setDocumentRev(revDocument);
				docFeedback.setRevSeUrl(revUrl);
				docFeedback.setComments(revUrl);
			}

			// docFeedback.setComments(docFeedbackComment);
			else if(rightTax != null)
			{
				if("".equals(rightTax))
				{
					return "New Taxonamy shouldn't be empty";
				}
				docFeedback.setComments(rightTax);
			}
			else
			{
				docFeedback.setComments(docFeedbackComment);
			}
			docFeedback.setErrortype(docFeedbackComment);
			docFeedback.setPriority(99l);
			docFeedback.setSeUrl(pdfUrl);
			docFeedback.setTeam("Parametric");
			docFeedback.setUsername(userName);
			docFeedback.setIssueDate(ParaQueryUtil.getDate());
			docFeedback.setStatus("unexecuted");
			session.saveOrUpdate(docFeedback);
			Pl pl = ParaQueryUtil.getPlByPlName(plName);
			TrackingParametric trackingParametric = getTrackingParametricByDocumentAndPl(document, pl);
			TrackingTaskStatus trackingTaskStatus = ParaQueryUtil.getTrackingTaskStatusByExactName(session, StatusName.srcFeedback);
			trackingParametric.setTrackingTaskStatus(trackingTaskStatus);
			session.update(trackingParametric);
			// session.beginTransaction().commit();

		}catch(Exception e)
		{
			e.printStackTrace();
			return "Error:" + e.getMessage();
		}finally
		{
			session.close();
		}
		return status;
	}

	public static ArrayList<TableInfoDTO> getShowAllPDFReview(Long[] usersId, String plName, String vendorName, String type, String extracted, String status, Date startDate, Date endDate, String feedbackTypeStr)
	{
		ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
		ArrayList<TableInfoDTO> tableData = new ArrayList<TableInfoDTO>();

		Session session = SessionUtil.getSession();

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
			Criteria criteria = session.createCriteria(TrackingParametric.class);
			if(!(usersId.length == 0) && usersId != null)
			{
				criteria.add(Restrictions.in("parametricUserId", usersId));
			}
			if(startDate != null && endDate != null)
			{
				criteria.add(Restrictions.ge("assignedDate", startDate));
				criteria.add(Restrictions.lt("assignedDate", endDate));
			}
			if(status != null && !status.equals("All"))
			{
				Criteria statusCriteria = session.createCriteria(TrackingTaskStatus.class);
				statusCriteria.add(Restrictions.eq("name", status));
				TrackingTaskStatus statusObj = (TrackingTaskStatus) statusCriteria.uniqueResult();
				criteria.add(Restrictions.eq("trackingTaskStatus", statusObj));
			}
			if(extracted != null && !extracted.equals("All"))
			{
				System.out.println(extracted);
				if(extracted.equals("Not Extracted"))
				{
					criteria.add(Restrictions.isNull("extractionStatus"));
				}
				else
				{
					criteria.add(Restrictions.eq("extractionStatus", 1l));
				}
			}
			if(plName != null && !plName.equals("All"))
			{
				Criteria plCriteria = session.createCriteria(Pl.class);
				plCriteria.add(Restrictions.eq("name", plName));
				Pl pl = (Pl) plCriteria.uniqueResult();
				criteria.add(Restrictions.eq("pl", pl));
			}
			if(vendorName != null && !vendorName.equals("All"))
			{
				Criteria vendorCriteria = session.createCriteria(Supplier.class);
				vendorCriteria.add(Restrictions.eq("name", vendorName));
				Supplier supplier = (Supplier) vendorCriteria.uniqueResult();
				criteria.add(Restrictions.eq("supplier", supplier));
			}
			if(type != null && !type.equals("All"))
			{
				Criteria typeCriteria = session.createCriteria(TrackingTaskType.class);
				typeCriteria.add(Restrictions.eq("name", type));
				TrackingTaskType typeObj = (TrackingTaskType) typeCriteria.uniqueResult();
				criteria.add(Restrictions.eq("trackingTaskType", typeObj));
			}
			if(feedbackTypeStr != null && !feedbackTypeStr.equals("All"))
			{
				List<Document> docs = getFeedbackDocs(feedbackTypeStr);
				criteria.add(Restrictions.in("document", docs));
			}

			List list = criteria.list();
			for(int i = 0; i < list.size(); i++)
			{
				System.out.println("Rec No:" + (1 + 1));
				TrackingParametric obj = (TrackingParametric) list.get(i);
				TableInfoDTO docInfo = new TableInfoDTO();
				docInfo.setPdfUrl(obj.getDocument().getPdf().getSeUrl());
				docInfo.setPlName(obj.getPl().getName());
				docInfo.setSupplierName(obj.getSupplier().getName());
				docInfo.setStatus(obj.getTrackingTaskStatus().getName());
				docInfo.setTaskType(obj.getTrackingTaskType().getName());
				docInfo.setDevUserName(ParaQueryUtil.getGRMUser(obj.getParametricUserId()).getFullName());
				docInfo.setExtracted(obj.getExtractionStatus() == null ? "No" : "Yes");
				Date finishDate = obj.getFinishedDate();
				if(finishDate != null)
				{
					docInfo.setDate(finishDate.toString());
				}
				String supplierSiteUrl = obj.getSupplier().getSiteUrl();
				String onlineLink = obj.getDocument().getPdf().getSupplierUrl().getUrl();
				// long pagesNo = obj.getDocument().getPdf().getPageCount();

				docInfo.setSupplierSiteUrl(supplierSiteUrl);
				docInfo.setOnlineLink(onlineLink);
				// docInfo.setPagesNo(pagesNo);

				tableData.add(docInfo);
			}
		}finally
		{
			session.close();
		}
		return tableData;
	}

	public static List<Document> getFeedbackDocs(String feedbackTypeStr)
	{
		Session session = SessionUtil.getSession();
		List<Document> docs = new ArrayList<Document>();
		try
		{
			TrackingFeedbackType feedbackType = getFeedbackType(feedbackTypeStr);
			Criteria cr = session.createCriteria(ParametricFeedbackCycle.class);
			cr.createAlias("parametricFeedback", "Feedback");
			cr.add(Restrictions.eq("Feedback.trackingFeedbackType", feedbackType));
			cr.add(Restrictions.eq("feedbackRecieved", 0l));
			List<ParametricFeedbackCycle> partsFeedback = cr.list();
			if(partsFeedback != null)
			{
				for(int i = 0; i < partsFeedback.size(); i++)
				{
					Document document = partsFeedback.get(i).getParametricFeedback().getDocument();
					docs.add(document);
				}
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			session.close();
		}
		return docs;
	}

	public static String getPartStatusByComId(long comId)
	{
		String status = "";
		Session session = null;

		try
		{
			session = SessionUtil.getSession();
			SQLQuery query = session.createSQLQuery("select tft.name from tracking_feedback_type tft, parts_feedback pf where pf.feedback_type=tft.id and pf.com_id=" + comId + " and pf.feedback_recieved=0");

			Object obj = query.uniqueResult();
			if(obj != null)
			{
				status = query.uniqueResult().toString();
			}
			else
			{
				System.out.println("Component Not Found !");
			}
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}finally
		{
			if((session != null) && (session.isOpen()))
			{
				session.close();
			}
		}

		return status;
	}

	public static Long[] getFeedbackDocIds(String feedbackTypeStr)
	{
		Long[] ids = null;
		List<Document> docs = getFeedbackDocs(feedbackTypeStr);
		if(docs != null)
		{
			ids = new Long[docs.size()];
			for(int i = 0; i < docs.size(); i++)
			{
				ids[i] = docs.get(i).getId();
			}
		}
		return ids;
	}

	public static int getInfectedPartsByDoc(long docId)
	{
		Session session = null;
		int count = 0;
		try
		{
			session = SessionUtil.getSession();
			SQLQuery query = session.createSQLQuery("select count(distinct pf.ITEM_ID) from PARAMETRIC_FEEDBACK pf, part_component c,PARAMETRIC_FEEDBACK_CYCLE fbc where c.document_id=" + docId
					+ " and c.com_id=pf.ITEM_ID and fbc.PARA_FEEDBACK_ID = pf.ID and fbc.feedback_recieved=0");
			Object obj = query.uniqueResult();
			if(obj != null)
			{
				count = Integer.parseInt(obj.toString());
			}

		}catch(Exception ex)
		{
			ex.printStackTrace();
		}finally
		{
			if((session != null) && (session.isOpen()))
			{
				session.close();
			}
		}
		return count;
	}

	public static int getInfectedTaxonomiesByDoc(long docId)
	{
		Session session = null;
		int count = 0;
		try
		{
			session = SessionUtil.getSession();
			SQLQuery query = session.createSQLQuery("select count(distinct c.supplier_pl_id) from PARAMETRIC_FEEDBACK pf, part_component c,PARAMETRIC_FEEDBACK_CYCLE fbc where c.document_id=" + docId
					+ " and c.com_id=pf.ITEM_ID and fbc.PARA_FEEDBACK_ID = pf.ID and fbc.feedback_recieved=0");
			System.out.println(query.getQueryString());
			Object obj = query.uniqueResult();
			if(obj != null)
			{
				count = Integer.parseInt(obj.toString());
			}

		}catch(Exception ex)
		{
			ex.printStackTrace();
		}finally
		{
			if((session != null) && (session.isOpen()))
			{
				session.close();
			}
		}
		return count;
	}

	public static String getFeedbackCommentByDocID(long docId)
	{
		Session session = null;
		String comment = "";
		try
		{
			session = SessionUtil.getSession();
			SQLQuery query = session.createSQLQuery("select pf.fb_comment from parts_feedback pf, part_component c where c.document_id=" + docId + " and c.com_id=pf.com_id");
			List<Object> list = query.list();
			for(int i = 0; i < list.size(); i++)
			{
				comment += list.get(i).toString();
				if(i != list.size() - 1)
				{
					comment += "$";
				}
			}

		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			if((session != null) && (session.isOpen()))
			{
				session.close();
			}
		}
		return comment;
	}

	/*
	 * old feedbacker
	 */
	public static String getFeedbackCommentByComId(long comID)
	{
		Session session = null;
		String comment = "";
		try
		{
			session = SessionUtil.getSession();
			SQLQuery query = session.createSQLQuery("select FBc.FB_COMMENT from PARAMETRIC_FEEDBACK_CYCLE FBc, PARAMETRIC_FEEDBACK FB where FB.ITEM_ID=" + comID + " and FBc.FEEDBACK_RECIEVED=0 AND FB.ID = FBC.PARA_FEEDBACK_ID");
			List<Object> list = query.list();
			for(int i = 0; i < list.size(); i++)
			{
				comment += list.get(i) == null ? "" : list.get(i).toString();
				if(i != list.size() - 1)
				{
					comment += "$";
				}
			}

		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			if((session != null) && (session.isOpen()))
			{
				session.close();
			}
		}
		return comment;
	}

	public static boolean hasIssues(long docId, long user)
	{
		Session session = null;
		boolean hasIsseues = false;
		try
		{
			session = SessionUtil.getSession();
			SQLQuery query = session.createSQLQuery("select pf.ID from PARAMETRIC_FEEDBACK pf,PARAMETRIC_FEEDBACK_CYCLE pfc where ITEM_ID in ( select com_id from part_component where document_id=" + docId
					+ " ) and pfc.feedback_recieved=0 and pf.ID = pfc.PARA_FEEDBACK_ID and pfc.ISSUED_BY <> " + user);
			List<Object> list = query.list();
			if((list != null) && (list.size() > 0))
			{
				hasIsseues = true;
			}

		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			if((session != null) && (session.isOpen()))
			{
				session.close();
			}
		}
		return hasIsseues;
	}

	public static GrmUserDTO getFeedbackIssuerByComId(long comID)
	{
		Session session = null;
		Session session2 = null;
		GrmUserDTO userDto = new GrmUserDTO();
		GrmUser user = new GrmUser();
		List<ParaFeedbackStatus> paraFeedbackStatus = null;
		try
		{
			session = SessionUtil.getSession();
			session2 = com.se.grm.db.SessionUtil.getSession();
			Criteria criteria = session.createCriteria(ParaFeedbackStatus.class);
			Criterion price = Restrictions.eq("feedbackStatus", "Open");
			Criterion name = Restrictions.eq("feedbackStatus", "Inprogress");
			criteria.add(Restrictions.or(price, name));
			paraFeedbackStatus = (List<ParaFeedbackStatus>) criteria.list();

			Criteria ParametricFeedbackCriteria = session.createCriteria(ParametricFeedback.class);
			ParametricFeedbackCriteria.add(Restrictions.eq("itemId", comID));
			ParametricFeedbackCriteria.add(Restrictions.in("paraFeedbackStatus", new Object[] { paraFeedbackStatus.get(0), paraFeedbackStatus.get(1) }));

			List<ParametricFeedback> fb = (List<ParametricFeedback>) ParametricFeedbackCriteria.list();
			if(!fb.isEmpty())
			{
				user = ParaQueryUtil.getUserByUserId(Long.valueOf(fb.get(0).getFbInitiator().toString()), session2);
				if(user != null)
				{
					userDto.setId(user.getId());
					userDto.setGrmGroup(user.getGrmGroup());
					userDto.setGroupName(user.getGrmGroup().getName());
					userDto.setFullName(user.getFullName());
				}
			}

		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			if((session != null) && (session.isOpen()))
			{
				session.close();
			}
		}
		return userDto;
	}

	public static String getFeedbackTypeByComId(long comID)
	{
		Session session = null;
		TrackingFeedbackType trackingfeedbacktype = null;
		String fbtype = "";
		List<ParaFeedbackStatus> paraFeedbackStatus = null;
		try
		{
			session = SessionUtil.getSession();
			Criteria criteria = session.createCriteria(ParaFeedbackStatus.class);
			Criterion price = Restrictions.eq("feedbackStatus", "Open");
			Criterion name = Restrictions.eq("feedbackStatus", "Inprogress");
			criteria.add(Restrictions.or(price, name));
			paraFeedbackStatus = (List<ParaFeedbackStatus>) criteria.list();

			Criteria ParametricFeedbackCriteria = session.createCriteria(ParametricFeedback.class);
			ParametricFeedbackCriteria.add(Restrictions.eq("itemId", comID));
			ParametricFeedbackCriteria.add(Restrictions.in("paraFeedbackStatus", new Object[] { paraFeedbackStatus.get(0), paraFeedbackStatus.get(1) }));

			List<ParametricFeedback> fb = (List<ParametricFeedback>) ParametricFeedbackCriteria.list();
			if(!fb.isEmpty())
			{
				trackingfeedbacktype = fb.get(0).getTrackingFeedbackType();
				fbtype = trackingfeedbacktype.getName();
			}

		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			if((session != null) && (session.isOpen()))
			{
				session.close();
			}
		}
		return fbtype;
	}

	/*
	 * in addition return com_id for eng feedback
	 */
	public static ArrayList<String> getFeedbackByPartAndSupp(String partNumber, String supName)
	{
		Session session = null;
		ArrayList<String> feed = new ArrayList<String>();
		try
		{
			session = SessionUtil.getSession();
			// SQLQuery query = session.createSQLQuery("select fb_comment,u.full_name,U.GROUP_ID,pf.com_id,PF.FLOW_SOURCE,s.name status_name " +
			// "from parts_feedback pf,part_component c,grm.grm_user u, tracking_task_status s "
			// + "where c.com_id=PF.COM_ID  " + "and pf.issued_by_id=u.id " + "and pf.REVIEW_STATUS_ID=s.id " + "and pf.feedback_recieved=0 " +
			// "and C.PART_NUMBER='" + partNumber + "' and C.SUPPLIER_ID=AUTOMATION2.GETSUPPLIERID('" + supName
			// + "')");
			String sql = "";
			sql = " SELECT FB_COMMENT, u.full_name, U.GROUP_ID, FB.ITEM_ID, FB.FEEDBACK_TYPE , s.";
			sql = sql + "name status_name,fbs.FEEDBACK_STATUS FROM PARAMETRIC_FEEDBACK_CYCLE FBc, PARAMETRIC_FEEDBACK FB, p";
			sql = sql + "art_component c, grm.grm_user u, TRACKING_PARAMETRIC t ,tracking_task_status s,PARA_FEEDBACK_STATUS fbs";
			sql = sql + " WHERE c.com_id = FB.ITEM_ID AND FBC.ISSUED_BY = u.id AND FBC.FEEDBACK_RECIEVE";
			sql = sql + "D = 0 AND C.PART_NUMBER = '" + partNumber + "' AND C.SUPPLIER_ID = AUTOMATION2.GETSUPPLIERID";
			sql = sql + " ('" + supName + "') AND FB.ID = FBC.PARA_FEEDBACK_ID AND T.TRACKING_TASK_STATUS_ID = S.ID ";
			sql = sql + "AND T.DOCUMENT_ID = FB.DOCUMENT_ID and fbs.ID = FBc.FEEDBACK_ACTION";
			SQLQuery query = session.createSQLQuery(sql);
			List<Object[]> list = query.list();

			String comment = "";
			Object[] objArr = list.get(0);
			for(int i = 0; i < list.size(); i++)
			{
				comment += (list.get(i)[0] == null) ? "" : list.get(i)[0].toString();
				if(i != list.size() - 1)
				{
					comment += "$";
				}
			}
			feed.add(comment);
			feed.add(objArr[1].toString());//
			feed.add(objArr[2].toString());
			feed.add(objArr[3].toString());
			feed.add(objArr[4].toString());// feedback source
			feed.add(objArr[5].toString());// feedback status add by Ahmed Makram
			feed.add(objArr[6].toString()); // feedback action by mohamed gawad

		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			if((session != null) && (session.isOpen()))
			{
				session.close();
			}
		}
		return feed;
	}

	public static String getArrayAsCommaSeperatedList(Object[] array)
	{
		String str = "";
		if(array != null)
		{
			for(int i = 0; i < array.length; i++)
			{

				str += array[i].toString();
				if(i != array.length - 1)
				{
					str += ", ";
				}

			}
		}
		return str;
	}

	public static TrackingFeedbackType getFeedbackType(String feedbackTypeStr)
	{
		Session session = SessionUtil.getSession();
		TrackingFeedbackType feedbackType = null;
		try
		{
			Criteria cr = session.createCriteria(TrackingFeedbackType.class);
			cr.add(Restrictions.eq("name", feedbackTypeStr));
			feedbackType = (TrackingFeedbackType) cr.uniqueResult();
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			session.close();
		}
		return feedbackType;

	}

	public static boolean isNPITaskType(Long[] usersId, String plName, String vendorName, String type, String status, Date startDate, Date endDate, Long[] docsIds) throws Exception
	{
		Session session = SessionUtil.getSession();
		boolean NPIFlag = false;
		SQLQuery query = null;
		try
		{
			StringBuffer qury = new StringBuffer();
			qury.append("  SELECT GetTaskTypeName (t.TRACKING_TASK_TYPE_ID) task_type  FROM  TRACKING_PARAMETRIC T ");

			if(plName != null && !plName.equals("All"))
			{
				qury.append("  where T.PL_ID=GET_PL_ID('" + plName + "')");
			}
			if(!vendorName.equals("All") && vendorName != null)
			{
				qury.append("  and T.SUPPLIER_ID=GETSUPPLIERID('" + vendorName + "')");
			}
			if(status != null && !status.equals("All"))
			{
				qury.append(" AND t.TRACKING_TASK_STATUS_ID = getTaskstatusId('" + status + "')");
			}
			if(type != null && !type.equals("All"))
			{
				qury.append(" AND t.TRACKING_TASK_TYPE_ID = getTaskTypeId('" + type + "')");
			}
			if((usersId != null) && !(usersId.length == 0))
			{
				// String users = "";
				// for (int i = 0; i < usersId.length; i++) {
				// if (i == usersId.length - 1) {
				// users = users + usersId[i];
				// } else {
				// users = users + usersId[i] + ",";
				// }
				// }
				qury.append(" AND T.USER_ID IN (" + getArrayAsCommaSeperatedList(usersId) + ")");
			}
			if(docsIds != null && docsIds.length > 0)
			{
				qury.append(" AND t.DOCUMENT_ID in ( " + getArrayAsCommaSeperatedList(docsIds) + " )");
			}
			if(startDate != null && endDate != null)
			{
				SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
				System.out.println(formatter.format(startDate) + "**************" + formatter.format(endDate));

				String dateRangeCond = " AND t.ASSIGNED_DATE BETWEEN TO_DATE ('" + formatter.format(startDate) + "','DD/MM/RRRR')AND  TO_DATE ('" + formatter.format(endDate) + "','DD/MM/RRRR')";
				qury.append(dateRangeCond);
				// qury = qury +
				// " AND t.ASSIGNED_DATE BETWEEN TO_DATE ('01/11/2012', 'DD/MM/RRRR') AND  TO_DATE ('03/03/2013', 'DD/MM/RRRR')";
			}
			// qury.append(" ORDER BY   T.DOCUMENT_ID,plName, C.PART_NUMBER, PF.DEVELOPMENT_ORDER");

			System.out.println(qury.toString());
			List<Object> result = session.createSQLQuery(qury.toString()).list();
			StringBuilder typeBuilder = new StringBuilder();
			for(int i = 0; i < result.size(); i++)
			{
				typeBuilder.append(result.get(i).toString());
			}
			if(typeBuilder.toString().contains("NPI"))
				NPIFlag = true;
		}finally
		{
			session.close();
		}
		return NPIFlag;

	}

	public static PartComponent getComponentByPartNumAndSupplier(String partNumber, Supplier supplier) throws Exception
	{
		Session session = SessionUtil.getSession();
		try
		{
			System.out.println(supplier.getName());
			PartComponent component = getComponentByPartNumberAndSupplierName(partNumber, supplier.getName(), session);
			return (PartComponent) CloneUtil.cloneObject(component, new ArrayList<String>());
		}catch(Exception ex)
		{
			throw ParametricDevServerUtil.getCatchException(ex);
		}finally
		{
			session.close();
		}
	}

	public static PartComponent getComponentByPartNumberAndSupplierName(String partnumber, String suppliername) throws Exception
	{
		Session session = SessionUtil.getSession();
		try
		{
			Criteria crit = session.createCriteria(PartComponent.class);
			crit.add(Restrictions.eq("partNumber", partnumber));
			crit.createCriteria("supplierPl").createCriteria("supplier").add(Restrictions.eq("name", suppliername));
			PartComponent component = (PartComponent) crit.uniqueResult();
			return component;
		}catch(Exception ex)
		{
			throw ParametricDevServerUtil.getCatchException(ex);
		}finally
		{
			session.close();
		}
	}

	public static PartComponent getComponentBycomid(Long comid) throws Exception
	{
		Session session = SessionUtil.getSession();
		try
		{
			Criteria crit = session.createCriteria(PartComponent.class);
			crit.add(Restrictions.eq("comId", comid));
			PartComponent component = (PartComponent) crit.uniqueResult();
			return component;
		}catch(Exception ex)
		{
			throw ParametricDevServerUtil.getCatchException(ex);
		}finally
		{
			session.close();
		}
	}

	public static PartComponent getComponentByPartNumberAndSupplierName(String partnumber, String suppliername, Session session)
	{
		if(session == null)
		{
			session = SessionUtil.getSession();
		}
		final Criteria crit = session.createCriteria(PartComponent.class);
		crit.add(Restrictions.eq("partNumber", partnumber));
		crit.createCriteria("supplierPl").createCriteria("supplier").add(Restrictions.eq("name", suppliername));
		PartComponent component = (PartComponent) crit.uniqueResult();
		return component;
	}

	public static PartComponent getComponentByPartAndSupplierPl(String partnumber, SupplierPl supplierPl)
	{

		Session session = SessionUtil.getSession();
		PartComponent component = null;
		try
		{
			final Criteria crit = session.createCriteria(PartComponent.class);
			crit.add(Restrictions.eq("partNumber", partnumber));
			crit.add(Restrictions.eq("supplierPl", supplierPl));
			component = (PartComponent) crit.uniqueResult();
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}finally
		{
			session.close();
		}
		return component;
	}

	public static TrackingParametric getTrackingParametricByDocumentAndPl(Document document, Pl pl)
	{
		TrackingParametric trackingParametric = null;
		Session session = null;
		try
		{
			session = SessionUtil.getSession();
			Criteria crit = session.createCriteria(TrackingParametric.class);
			crit.add(Restrictions.eq("document", document));
			crit.add(Restrictions.eq("pl", pl));
			trackingParametric = (TrackingParametric) crit.uniqueResult();
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			if((session != null) && (session.isOpen()))
			{
				session.close();
			}
		}
		return trackingParametric;
	}

	public static Family insertFamily(String familyStr, Session session)
	{
		Family family = new Family();
		family.setId(QueryUtil.getRandomID());
		family.setName(familyStr);
		session.save(family);
		session.beginTransaction().commit();
		return family;
	}

	public static GenericFamily insertGenericFamily(String genericfamilyStr, Session session)
	{
		GenericFamily genericFamily = new GenericFamily();
		// genericFamily.setId(QueryUtil.getRandomID());
		genericFamily.setGenericName(genericfamilyStr);
		session.save(genericFamily);
		session.beginTransaction().commit();
		return genericFamily;
	}

	public static MapGeneric insertGeneric(String genericStr, Session session)
	{
		MapGeneric generic = new MapGeneric();
		// generic.setId(QueryUtil.getRandomID());
		generic.setGeneric(genericStr);
		generic.setStoreDate(new Date());
		session.saveOrUpdate(generic);
		session.beginTransaction().commit();
		return generic;
	}

	public static FamilyCross insertFamilyCross(String familyStr, Session session)
	{
		FamilyCross familyCross = new FamilyCross();
		// familyCross.setId(QueryUtil.getRandomID());
		familyCross.setFamily(familyStr);
		familyCross.setStoreDate(new Date());
		session.saveOrUpdate(familyCross);
		session.beginTransaction().commit();
		return familyCross;
	}

	/**
	 * @author ahmad_makram
	 * @param genericStr
	 * @param session
	 * @return
	 */
	public static TblNpiParts insertNPIPart(PartComponent com, String seUrl, Session session)
	{
		TblNpiParts npiPart = new TblNpiParts();
		// generic.setId(QueryUtil.getRandomID());
		npiPart.setPartComponent(com);
		npiPart.setSupplier(com.getSupplierId());
		npiPart.setPl(com.getSupplierPl().getPl());
		npiPart.setOfflinedocid(com.getDocument());
		npiPart.setNewsdocid(ParaQueryUtil.getDocumentBySeUrl(seUrl, session));
		npiPart.setInsertionDate(new Date());
		session.saveOrUpdate(npiPart);
		session.beginTransaction().commit();
		return npiPart;
	}

	public static MasterPartMask insertMask(String maskStr, Session session)
	{
		String maskMaster = getNonAlphaMask(maskStr).replaceAll("_", "%").replaceAll("(%){2,}", "%");
		if(!maskMaster.contains("%"))
		{
			maskMaster = maskMaster + "%";
		}
		MasterPartMask mask = new MasterPartMask();
		// mask.setId(QueryUtil.getRandomID());

		mask.setMstrPart(maskMaster);
		mask.setStoreDate(new Date());
		session.saveOrUpdate(mask);
		// session.beginTransaction().commit();
		PartMaskValueId mskValId = new PartMaskValueId();
		mskValId.setMaskId(mask.getId());
		mskValId.setMaskPn(maskStr);
		PartMaskValue maskval = new PartMaskValue();
		maskval.setId(mskValId);
		maskval.setMasterPartMask(mask);
		session.saveOrUpdate(maskval);
		return mask;
	}

	/**
	 * @author ahmad_Makram
	 * @param mask_name
	 * @return Non Alpha Mask Value
	 * 
	 */
	public static String getNonAlphaMask(String maskValue)
	{

		Session session = null;
		try
		{
			session = SessionUtil.getSession();
			SQLQuery query = session.createSQLQuery("select cm.NONALPHANUM_MASK(:nanpartnum) from dual");
			query.setParameter("nanpartnum", maskValue.trim());

			return (String) query.uniqueResult();
		}catch(Exception ex)
		{
			AppContext.FirMessageError(ex.getMessage(), ParaQueryUtil.class, ex);
		}finally
		{
			SessionUtil.closeSession(session);

		}
		return null;
	}

	/****
	 * get Mask Object By Mame by Ahmed makram
	 ****/
	public static MasterPartMask getMask(String maskValue)
	{
		Session session = SessionUtil.getSession();
		MasterPartMask mask = null;
		try
		{
			String maskMaster = getNonAlphaMask(maskValue).replaceAll("_", "%").replaceAll("(%){2,}", "%");
			if(!maskMaster.contains("%"))
			{
				maskMaster = maskMaster + "%";
			}
			Query q = session.createQuery("select o from MasterPartMask o  where o.mstrPart=:man");
			q.setParameter("man", maskMaster);
			mask = (MasterPartMask) q.uniqueResult();
			if(mask == null)
				return null;
			q = session.createSQLQuery("select * from PART_MASK_VALUE  where MASK_PN=:val and mask_id =:mskid");
			q.setParameter("val", maskValue);
			q.setParameter("mskid", mask.getId());
			// q.list();

			if(q.list().isEmpty())
			{
				PartMaskValueId mskValId = new PartMaskValueId();
				mskValId.setMaskId(mask.getId());
				mskValId.setMaskPn(maskValue);
				PartMaskValue maskval = new PartMaskValue();
				maskval.setId(mskValId);
				maskval.setMasterPartMask(mask);
				session.saveOrUpdate(maskval);
			}

			// Criteria cr = session.createCriteria(MasterPartMask.class);
			// cr.add(Restrictions.eq("mstrPart", famName));
			// mask = (MasterPartMask) cr.uniqueResult();
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			session.close();
		}
		return mask;
	}

	private static TrackingParametric getTrackingParametricByPdfUrlAndSupName(String pdfUrl, String plName, String supplierName, Session session)
	{
		Criteria criteria = session.createCriteria(TrackingParametric.class);
		try
		{
			Document document = ParaQueryUtil.getDocumentBySeUrl(pdfUrl, session);
			criteria.add(Restrictions.eq("document", document));
			criteria.add(Restrictions.eq("pl", ParaQueryUtil.getPlByPlName(session, plName)));
			if(supplierName != null)
			{
				criteria.add(Restrictions.eq("supplier", ParaQueryUtil.getSupplierByExactName(session, supplierName)));
			}
		}catch(Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return (TrackingParametric) criteria.uniqueResult();
	}

	private static PartComponent getComponentByPartNumberAndSupplier(String partnumber, String supplierName) throws Exception
	{
		Session session = SessionUtil.getSession();
		try
		{

			System.out.println("part number = " + partnumber + "suppliername =   " + supplierName);
			PartComponent component = getComponentByPartNumberAndSupplierName(partnumber, supplierName, session);
			String newDiscription = ParaQueryUtil.getNewDiscription(component.getSupplierPl().getPl().getId(), component.getComId(), session);
			if(newDiscription != null)
			{
				component.setDescription(newDiscription);
			}

			return component;

		}catch(Exception e)
		{
			throw ParametricDevServerUtil.getCatchException(e);
		}finally
		{
			SessionUtil.closeSession(session);

		}

	}

	public static GrmUserDTO getLastFeedbackCycleSenderByComId(long comID)
	{
		Session session = null;
		Session session2 = null;
		GrmUserDTO userDto = new GrmUserDTO();
		GrmUser user = new GrmUser();
		String comment = "";
		try
		{
			session2 = com.se.grm.db.SessionUtil.getSession();
			session = SessionUtil.getSession();
			ParametricFeedbackCycle appFB = null;
			Criteria feedBCri = session.createCriteria(ParametricFeedbackCycle.class);
			feedBCri.createAlias("parametricFeedback", "feedback");
			feedBCri.add(Restrictions.eq("feedback.itemId", comID));
			feedBCri.add(Restrictions.eq("feedbackRecieved", 0l));
			if(feedBCri.list() != null && !feedBCri.list().isEmpty())
			{
				List<ParametricFeedbackCycle> fb = (List<ParametricFeedbackCycle>) feedBCri.list();
				user = ParaQueryUtil.getUserByUserId(fb.get(0).getIssuedBy(), session2);
				if(user != null)
				{
					userDto.setId(user.getId());
					userDto.setGrmGroup(user.getGrmGroup());
					userDto.setGroupName(user.getGrmGroup().getName());
					userDto.setFullName(user.getFullName());
				}
			}
			else
				return null;
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			session.close();
			session2.close();
		}

		return userDto;
	}

	public static GrmUserDTO getLastQACommentByComId(long comID)
	{
		Session session = null;
		GrmUserDTO userDto = new GrmUserDTO();
		try
		{
			session = SessionUtil.getSession();
			SQLQuery query = session.createSQLQuery("SELECT   u.full_name, g.name  FROM   parts_feedback pf, grm.grm_user u, grm.grm_group g WHERE  com_id =28862724  AND pf.issued_by_id = u.id  AND u.GROUP_ID = g.id AND pf.feedback_recieved = 0");
			Object[] objArr = (Object[]) query.uniqueResult();
			if(objArr != null)
			{
				String userName = objArr[0].toString();
				String groupName = objArr[1].toString();
				userDto.setFullName(userName);
				userDto.setGroupName(groupName);
			}

		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			if((session != null) && (session.isOpen()))
			{
				session.close();
			}
		}
		return userDto;
	}

	/*
	 * if we set group only the parametrc will conflict between (TL,ENG)
	 */
	public static String getLastFeedbackCommentByComIdAndSenderGroup(long comID, String senderGroup, Long recieverId, Pl pl)
	{
		Session session = null;
		session = SessionUtil.getSession();
		String comment = "";
		try
		{
			if(senderGroup.equals("QUALITY"))
			{
				ParametricFeedbackCycle appFB = null;
				Long qaUserId = ParaQueryUtil.getQAUserId(pl, ParaQueryUtil.getTrackingTaskTypeByName("NPI", session));
				Criteria feedBCri = session.createCriteria(ParametricFeedbackCycle.class);
				feedBCri.add(Restrictions.eq("issuedBy", qaUserId));
				feedBCri.createAlias("parametricFeedback", "feedback");
				feedBCri.add(Restrictions.eq("feedback.itemId", comID));
				feedBCri.addOrder(Order.desc("storeDate"));
				if(feedBCri.list() != null && !feedBCri.list().isEmpty())
					appFB = (ParametricFeedbackCycle) feedBCri.list().get(0);

				comment = ((appFB == null) ? "" : appFB.getFbComment());

				if(appFB == null)
				{
					comment = "";
				}
			}
			else
			{
				ParametricFeedbackCycle appFB = null;
				Criteria feedBCri = session.createCriteria(ParametricFeedbackCycle.class);
				if(recieverId != null)
					feedBCri.add(Restrictions.eq("issuedBy", recieverId));
				feedBCri.createAlias("parametricFeedback", "feedback");
				feedBCri.add(Restrictions.eq("feedback.itemId", comID));
				feedBCri.addOrder(Order.desc("storeDate"));
				if(feedBCri.list() != null && !feedBCri.list().isEmpty())
				{
					appFB = (ParametricFeedbackCycle) feedBCri.list().get(0);
					comment = appFB.getFbComment();
				}
				else
				{
					comment = "";
				}

			}

		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			if((session != null) && (session.isOpen()))
			{
				session.close();
			}
		}
		return comment;
	}

	public static ArrayList<Object[]> getQAFeedBackFilterData(GrmUserDTO grmUser)
	{
		Long UserID = grmUser.getId();
		ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
		ArrayList<String> row = null;
		ArrayList<Object[]> list2 = null;
		Session session = SessionUtil.getSession();
		try
		{

			String sql = "  SELECT   DISTINCT p.name pl, s.name supplier, ttt.name TYPE, U.FULL_NAME user_Name,AUTOMATION2.Get_PL_Type(P.ID ) "
					+ "  FROM   Tracking_Parametric tp, pl p, supplier s, tracking_task_type ttt, grm.GRM_USER u, TRACKING_TASK_STATUS st  " + " WHERE  tp.pl_id = p.id   AND tp.tracking_task_type_id IN (0, 1, 4, 12, 14)     "
					+ "      AND tp.TRACKING_TASK_STATUS_ID = getTaskstatusId('" + StatusName.qaFeedback + "')  " + "         AND tp.supplier_id = s.id  " + "         AND tp.tracking_task_type_id = ttt.id   " + "        AND u.id = tp.user_id     "
					+ "      AND st.id = tp.TRACKING_TASK_STATUS_ID  " + "         and QA_USER_ID=" + grmUser.getId() + " GROUP BY p.name, s.name, ttt.name, U.FULL_NAME, st.NAME,P.ID";
			list2 = (ArrayList<Object[]>) session.createSQLQuery(sql).list();
			for(int i = 0; i < list2.size(); i++)
			{
				Object[] data = list2.get(i);
				row = new ArrayList<String>();
				for(int j = 0; j < 5; j++)
				{
					row.add((data[j] == null) ? "" : data[j].toString());
					// System.out.println((data[j] == null) ? "" : data[j].toString());
				}
				// row.add((data[3] == null) ? "Not Extracted" : "Extracted");
				result.add(row);
			}
		}finally
		{
			session.close();
		}
		return list2;

	}

	public static ArrayList<Object[]> getQAchecksFilterData(GrmUserDTO grmUser)
	{
		Long UserID = grmUser.getId();
		ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
		ArrayList<String> row = null;
		ArrayList<Object[]> list2 = null;
		Session session = SessionUtil.getSession();
		try
		{

			String Sql = "";
			Sql = " SELECT DISTINCT p.name pl, s.name supplier, chks.NAME chktype, chkac.NAME sta";
			Sql = Sql + "tus FROM Tracking_Parametric tp, pl p, supplier s, grm.GRM_USER u, TRACKING_TA";
			Sql = Sql + "SK_STATUS st, QA_CHECKS_ACTIONS chkac, QA_CHECK_PARTS chp, PRE_QA_CHECKERS chk";
			Sql = Sql + "s WHERE tp.pl_id = p.id AND tp.TRACKING_TASK_STATUS_ID = getTaskstatusId('QA C";
			Sql = Sql + "hecking') AND tp.supplier_id = s.id AND u.id = tp.user_id AND st.id = tp.TRACK";
			Sql = Sql + "ING_TASK_STATUS_ID AND chp.CHECK_ID = chks.ID AND chp.DOCUMENT_ID = tp.DOCUMEN";
			Sql = Sql + "T_ID AND chkac.ID = chp.ACTION_ID AND tp.USER_ID =359 GROUP BY s.name, P.name,";
			Sql = Sql + " chks.NAME, chkac.NAME";
			list2 = (ArrayList<Object[]>) session.createSQLQuery(Sql).list();
			for(int i = 0; i < list2.size(); i++)
			{
				Object[] data = list2.get(i);
				row = new ArrayList<String>();
				for(int j = 0; j < 4; j++)
				{
					row.add((data[j] == null) ? "" : data[j].toString());

				}

				result.add(row);
			}
		}finally
		{
			session.close();
		}
		return list2;

	}

	public static ParaFeedbackAction getfeedBackActionByItem(long itemid, long userid)
	{
		ParametricFeedbackCycle parametricfeedbackcycle = null;
		Session session = null;
		session = SessionUtil.getSession();
		Criteria cri = session.createCriteria(ParametricFeedbackCycle.class);
		cri.add(Restrictions.eq("issuedTo", userid));
		cri.add(Restrictions.eq("feedbackRecieved", 0l));
		cri.createAlias("parametricFeedback", "feedback");
		cri.add(Restrictions.eq("feedback.itemId", itemid));
		parametricfeedbackcycle = (ParametricFeedbackCycle) cri.uniqueResult();
		if(parametricfeedbackcycle != null)
		{
			return parametricfeedbackcycle.getParaFeedbackAction();
		}
		else
			return null;
	}

	public static String getlastengComment(long comid, Long issuedby)
	{
		Session session = null;
		String comment = "";
		try
		{
			session = SessionUtil.getSession();
			ParametricFeedbackCycle appFB = null;
			Criteria feedBCri = session.createCriteria(ParametricFeedbackCycle.class);
			feedBCri.add(Restrictions.eq("issuedBy", issuedby));
			feedBCri.createAlias("parametricFeedback", "feedback");
			feedBCri.add(Restrictions.eq("feedback.itemId", comid));
			feedBCri.addOrder(Order.desc("storeDate"));
			if(feedBCri.list() != null && !feedBCri.list().isEmpty())
			{
				appFB = (ParametricFeedbackCycle) feedBCri.list().get(0);
				comment = appFB.getFbComment() == null ? "" : appFB.getFbComment();
			}
			else
				comment = "";
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			session.close();
		}
		return comment;
	}

	public static void saveQAFlag(List<PartInfoDTO> allParts)
	{
		Session session = null;
		PartComponent component = null;
		try
		{
			session = SessionUtil.getSession();
			for(PartInfoDTO pa : allParts)
			{
				component = pa.getComponent();
				if(component != null)
					component.setQaflag(pa.getStatus());
				session.saveOrUpdate(component);
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			session.close();
		}

	}

	public static void addpdfstosummary(Document doc)
	{
		Session session = null;
		TrackingParametric track = null;
		TrackingTaskStatus status = null;
		try
		{
			session = SessionUtil.getSession();
			Criteria cri = session.createCriteria(TrackingTaskStatus.class);
			cri.add(Restrictions.eq("name", StatusName.waitingsummary));
			status = (TrackingTaskStatus) cri.uniqueResult();

			cri = session.createCriteria(TrackingParametric.class);
			cri.add(Restrictions.eq("document", doc));
			track = (TrackingParametric) cri.uniqueResult();
			if(track != null)
				track.setTrackingTaskStatus(status);
			session.saveOrUpdate(track);

		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			session.close();
		}

	}

	public static ArrayList<ArrayList<String>> getsummarydata(Date startDate, Date endDate, GrmUserDTO userDTO)
	{
		Session session = SessionUtil.getSession();
		// ArrayList<SummaryDTO> alldata = new ArrayList<>();
		ArrayList<ArrayList<String>> allsummary = new ArrayList<>();
		SQLQuery query = null;
		try
		{
			StringBuffer qury = new StringBuffer();
			String Sql = "";
			Sql = " SELECT GETPDFURLbydoc (T.DOCUMENT_ID) pdfurl, getonlinelink_non_pdf (T.DOCUME";
			Sql = Sql + "NT_ID) onlinelink, Get_PL_Type (t.pl_id) pltype, GET_PL_NAME (t.PL_ID) plName,";
			Sql = Sql + " C.COM_ID, C.PART_NUMBER, GETSUPPLIERNAME (t.supplier_id) supName, GetTaskType";
			Sql = Sql + "Name (t.TRACKING_TASK_TYPE_ID) task_type, getuserName (T.USER_ID) username, t.";
			Sql = Sql + "ASSIGNED_DATE, C.QAFLAG, DECODE (C.DONEFLAG, NULL, 'No', 0, 'No', 1, 'Yes') DO";
			Sql = Sql + "NEFLAG, DECODE (C.EXTRACTIONFLAG, NULL, 'No', 0, 'No', 1, 'Yes') EXTRACTIONFLAG,T.DOCUMENT_ID,t.pl_id ";
			Sql = Sql + "FROM TRACKING_PARAMETRIC T, Part_COMPONENT c WHERE t.DOCUMENT_ID = c.DOCUMEN";
			Sql = Sql + "T_ID AND T.SUPPLIER_PL_ID = C.SUPPLIER_PL_ID AND T.QA_USER_ID = " + userDTO.getId() + " AND T.TRACK";
			Sql = Sql + "ING_TASK_STATUS_ID = getTaskstatusId ('" + StatusName.waitingsummary + "')";
			qury.append(Sql);
			// pdfurl_0 onlinelink_1 pltype_2 plName_3 COM_ID_4 PART_NUMBER_5
			// supName_6 task_type_7 username_8 DATE_9 QAFLAG_10 DONEFLAG_11 EXTRACTIONFLAG_12

			if(startDate != null && endDate != null)
			{
				SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
				System.out.println(formatter.format(startDate) + "**************" + formatter.format(endDate));
				String dateRangeCond = " AND t.ASSIGNED_DATE BETWEEN TO_DATE ('" + formatter.format(startDate) + "','DD/MM/RRRR')AND  TO_DATE ('" + formatter.format(endDate) + "','DD/MM/RRRR')";
				qury.append(dateRangeCond);

			}
			System.out.println(qury.toString());
			Long[] users = ParaQueryUtil.getusersbyqualityandstatus(userDTO, StatusName.waitingsummary);
			ArrayList<Object[]> result = (ArrayList<Object[]>) session.createSQLQuery(qury.toString()).list();
			for(int i = 0; i < result.size(); i++)
			{
				Object[] data = result.get(i);
				ArrayList<String> summary = new ArrayList<String>();
				summary.add(data[0] == null ? "" : data[0].toString());// pdfurl_0
				summary.add(data[1] == null ? "" : data[1].toString());// onlinelink_1
				summary.add(data[2] == null ? "" : data[2].toString());// pltype_2
				summary.add(data[3] == null ? "" : data[3].toString());// plName_3

				List<Integer> noparts = getnoPartsPerPDFandPL(Long.valueOf(data[13].toString()), Long.valueOf(data[14].toString()), users, StatusName.waitingsummary);
				summary.add(noparts.get(0).toString());// PDFParts_4
				summary.add(noparts.get(2).toString());// PDFDoneParts_5
				summary.add(noparts.get(1).toString());// PLparts_6
				summary.add(noparts.get(3).toString());// PLDoneParts_7

				summary.add(data[4].toString());// COM_ID_8
				summary.add(data[5] == null ? "" : data[5].toString());// PART_NUMBER_9
				summary.add(data[6] == null ? "" : data[6].toString());// supName_10
				summary.add(data[7] == null ? "" : data[7].toString());// task_type_11
				summary.add(data[8] == null ? "" : data[8].toString());// username_12
				summary.add(data[9] == null ? "" : data[9].toString());// DATE_13
				summary.add(data[10] == null ? "" : data[10].toString());// OLDQAFLAG_14
				summary.add("");// NEWQAFLAG_15
				String comment = getfbcommentbycomidanduser(Long.valueOf(data[4].toString()), userDTO.getId());
				summary.add(comment);// QAcomment_16
				summary.add(data[11] == null ? "" : data[11].toString());// DONEFLAG_17
				summary.add(data[12] == null ? "" : data[12].toString());// EXTRACTIONFLAG_18
				allsummary.add(summary);
			}

		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return allsummary;
	}

	public static String getfbcommentbycomidanduser(Long itemid, long userid)
	{
		ParametricFeedbackCycle parametricfeedbackcycle = null;
		Session session = null;
		session = SessionUtil.getSession();
		Criteria cri = session.createCriteria(ParametricFeedbackCycle.class);
		cri.add(Restrictions.eq("issuedBy", userid));
		cri.add(Restrictions.eq("feedbackRecieved", 0l));
		cri.createAlias("parametricFeedback", "feedback");
		cri.add(Restrictions.eq("feedback.itemId", itemid));
		parametricfeedbackcycle = (ParametricFeedbackCycle) cri.uniqueResult();
		if(parametricfeedbackcycle != null)
		{
			return parametricfeedbackcycle.getFbComment();
		}
		else
			return "";
	}

	public static String getfbcommentbycompartanduser(String itemvalue, long userid)
	{
		ParametricFeedbackCycle parametricfeedbackcycle = null;
		Session session = null;
		session = SessionUtil.getSession();
		Criteria cri = session.createCriteria(ParametricFeedbackCycle.class);
		cri.add(Restrictions.eq("issuedBy", userid));
		cri.add(Restrictions.eq("feedbackRecieved", 0l));
		cri.add(Restrictions.eq("fbItemValue", itemvalue));
		parametricfeedbackcycle = (ParametricFeedbackCycle) cri.uniqueResult();
		if(parametricfeedbackcycle != null)
		{
			return parametricfeedbackcycle.getFbComment();
		}
		else
			return "";
	}

	public static String getqaflagbycomid(String comid)
	{
		Session session = null;
		PartComponent component = null;
		String flag = "";
		try
		{
			session = SessionUtil.getSession();

			Criteria cri = session.createCriteria(PartComponent.class);
			cri.add(Restrictions.eq("comId", Long.valueOf(comid)));
			component = (PartComponent) cri.uniqueResult();
			if(component != null)
				flag = component.getQaflag();
			else
				flag = "";

		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			session.close();
		}
		return flag;

	}

	public static String getfbwrongfets(long itemid, long userid)
	{
		ParametricFeedback parametricfeedback = null;
		Session session = null;
		String wrongFeatures = "";
		session = SessionUtil.getSession();
		Criteria cri = session.createCriteria(ParametricFeedback.class);
		cri.add(Restrictions.eq("fbInitiator", userid));
		cri.createAlias("paraFeedbackStatus", "paraFeedbackStatus");
		cri.add(Restrictions.eq("paraFeedbackStatus.feedbackStatus", "Inprogress"));
		cri.add(Restrictions.eq("itemId", itemid));
		parametricfeedback = (ParametricFeedback) cri.uniqueResult();
		if(parametricfeedback != null)
		{
			cri = session.createCriteria(ParaFeedbackFets.class);
			cri.add(Restrictions.eq("paraFeedbackId", parametricfeedback));
			List<ParaFeedbackFets> fets = cri.list();
			if(!fets.isEmpty())
			{
				for(int i = 0; i < fets.size(); i++)
				{
					wrongFeatures += fets.get(i).getFeature().getName() + "|";
				}
				wrongFeatures = wrongFeatures.substring(0, wrongFeatures.length() - 1);
			}
		}
		else
			wrongFeatures = "";

		return wrongFeatures;

	}

	public static void deleteoldfeedbacks(List<String> changedparts, String issuedby)
	{
		ParametricFeedbackCycle parametricfeedbackcycle = null;
		ParametricFeedback parametricfeedback = null;
		Session session = null;

		session = SessionUtil.getSession();
		GrmUser issuedByUser = ParaQueryUtil.getGRMUserByName(issuedby);
		Criteria cri = session.createCriteria(ParametricFeedbackCycle.class);
		for(int p = 0; p < changedparts.size(); p++)
		{

			cri = session.createCriteria(ParametricFeedbackCycle.class);
			cri.add(Restrictions.eq("issuedBy", issuedByUser.getId()));
			cri.add(Restrictions.eq("feedbackRecieved", 0l));
			cri.add(Restrictions.eq("fbItemValue", changedparts.get(p)));
			parametricfeedbackcycle = (ParametricFeedbackCycle) cri.uniqueResult();
			if(parametricfeedbackcycle != null)
			{
				parametricfeedback = parametricfeedbackcycle.getParametricFeedback();
				cri = session.createCriteria(ParaFeedbackFets.class);
				cri.add(Restrictions.eq("paraFeedbackId", parametricfeedbackcycle.getParametricFeedback()));
				List<ParaFeedbackFets> fets = cri.list();
				if(!fets.isEmpty())
				{
					for(int i = 0; i < fets.size(); i++)
					{
						session.delete(fets.get(i));
					}
				}
				session.delete(parametricfeedbackcycle);
				session.beginTransaction().commit();
				session.delete(parametricfeedback);
				session.beginTransaction().commit();
			}

		}

	}

	public static ArrayList<QAChecksDTO> getQAchecksData(String plName, String supplierName, String checkerType, String status)
	{

		return null;
	}
}
