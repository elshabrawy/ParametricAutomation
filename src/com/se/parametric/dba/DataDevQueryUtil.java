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

import com.se.Quality.QAChecks;
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
import com.se.automation.db.client.mapping.Pl;
import com.se.automation.db.client.mapping.PlFeature;
import com.se.automation.db.client.mapping.PreQaCheckers;
import com.se.automation.db.client.mapping.PreQaCheckersException;
import com.se.automation.db.client.mapping.QaCheckMultiData;
import com.se.automation.db.client.mapping.QaCheckMultiTax;
import com.se.automation.db.client.mapping.QaCheckParts;
import com.se.automation.db.client.mapping.QaChecksActions;
import com.se.automation.db.client.mapping.QaChecksStatus;
import com.se.automation.db.client.mapping.Supplier;
import com.se.automation.db.client.mapping.SupplierPl;
import com.se.automation.db.client.mapping.TblNpiParts;
import com.se.automation.db.client.mapping.TrackingFeedbackType;
import com.se.automation.db.client.mapping.TrackingParametric;
import com.se.automation.db.client.mapping.TrackingTaskStatus;
import com.se.automation.db.client.mapping.TrackingTaskType;
import com.se.automation.db.parametric.StatusName;
import com.se.grm.client.mapping.GrmUser;
import com.se.parametric.AppContext;
import com.se.parametric.dto.GrmUserDTO;
import com.se.parametric.dto.PartInfoDTO;
import com.se.parametric.dto.SummaryDTO;
import com.se.parametric.dto.TableInfoDTO;

public class DataDevQueryUtil
{

	public static List<String> getAllPlNames()
	{
		List<String> plNames = null;
		Session session = SessionUtil.getSession();
		try
		{
			SQLQuery query = session
					.createSQLQuery("select name from pl where is_pl=1 order by name");
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
					+ UserID
					+ " and tp.TRACKING_TASK_STATUS_ID in (6,42) and tp.tracking_task_type_id <> 15 group by  p.name  , s.name  , ttt.name ,EXTRACTION_STATUS, TP.PRIORIY order by pl, supplier, type, TP.PRIORIY";

			System.out.println("Server Mesage   " + sql);
		}
		else
		{
			sql = "select distinct p.name pl, s.name supplier, ttt.name type ,EXTRACTION_STATUS, TP.PRIORIY from Tracking_Parametric tp, pl p, supplier s, tracking_task_type ttt where tp.pl_id = p.id and tp.supplier_id = s.id and tp.tracking_task_type_id = ttt.id and user_id = "
					+ UserID
					+ " and tp.TRACKING_TASK_STATUS_ID in (6,42) and tp.tracking_task_type_id <> 15 AND TP.MODIFICATION_DATE BETWEEN TO_DATE('"
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
			data[3] = (data[3] == null || data[3].equals(new BigDecimal(0))) ? "Not Extracted"
					: "Extracted";
			data[4] = (data[4] == null) ? "" : data[4].toString();
			result.set(i, data);
		}
		session.close();
		return result;
	}

	public static ArrayList<Object[]> getQAReviewFilterData(GrmUserDTO grmUser, Date startdate,
			Date enddate)
	{
		Long UserID = grmUser.getId();
		ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
		ArrayList<String> row = null;
		ArrayList<Object[]> list2 = null;
		Session session = SessionUtil.getSession();
		String start = "";
		String end = "";
		String Sql = "";
		try
		{
			if(startdate != null)
			{
				start = new SimpleDateFormat("MM/dd/yyyy").format(startdate);

			}
			if(enddate != null)
			{
				end = new SimpleDateFormat("MM/dd/yyyy").format(enddate);
			}
			List<TrackingTaskStatus> taskStatus = null;
			Criteria cri = session.createCriteria(TrackingTaskStatus.class);
			cri.add(Restrictions.or(Restrictions.eq("name", StatusName.qaReview),
					Restrictions.eq("name", StatusName.waitingsummary)));
			taskStatus = cri.list();
			if(startdate == null && enddate == null)
			{

				Sql = " SELECT DISTINCT p.name pl,Get_PL_Type(P.ID ), s.name supplier, tt";
				Sql = Sql
						+ "t.name TYPE, U.FULL_NAME user_Name,st.name FROM Tracking_Parametric tp, pl p, supplier";
				Sql = Sql
						+ " s, tracking_task_type ttt, grm.GRM_USER u, TRACKING_TASK_STATUS st WHERE st.id = tp.TRACKING_TASK_STATUS_ID and tp.p";
				Sql = Sql + "l_id = p.id AND tp.TRACKING_TASK_STATUS_ID IN ("
						+ taskStatus.get(0).getId() + "," + taskStatus.get(1).getId()
						+ ") AND tp.supplier_id = s.id AN";
				Sql = Sql
						+ "D tp.tracking_task_type_id = ttt.id AND u.id = tp.user_id AND st.id = tp.TRACK";
				Sql = Sql + "ING_TASK_STATUS_ID and QA_USER_ID=" + grmUser.getId()
						+ " GROUP BY p.name, s.name, ttt.name, U.FULL";
				Sql = Sql + "_NAME, st.NAME, Get_PL_Type(P.ID )";
			}
			else
			{
				Sql = " SELECT DISTINCT p.name pl,Get_PL_Type(P.ID ), s.name supplier, tt";
				Sql = Sql
						+ "t.name TYPE, U.FULL_NAME user_Name,st.name FROM Tracking_Parametric tp, pl p, supplier";
				Sql = Sql
						+ " s, tracking_task_type ttt, grm.GRM_USER u, TRACKING_TASK_STATUS st WHERE st.id = tp.TRACKING_TASK_STATUS_ID and tp.p";
				Sql = Sql + "l_id = p.id AND tp.TRACKING_TASK_STATUS_ID IN ("
						+ taskStatus.get(0).getId() + "," + taskStatus.get(1).getId()
						+ ") AND tp.supplier_id = s.id AN";
				Sql = Sql
						+ "D tp.tracking_task_type_id = ttt.id AND u.id = tp.user_id AND st.id = tp.TRACK";
				Sql = Sql + "ING_TASK_STATUS_ID and QA_USER_ID=" + grmUser.getId();
				Sql = Sql
						+ " AND TP.MODIFICATION_DATE BETWEEN TO_DATE('"
						+ start
						+ "', 'MM/DD/YYYY') AND TO_DATE('"
						+ end
						+ "', 'MM/DD/YYYY')  GROUP BY p.name, s.name, ttt.name, U.FULL_NAME, st.NAME, Get_PL_Type(P.ID )";
			}
			list2 = (ArrayList<Object[]>) session.createSQLQuery(Sql).list();
		}finally
		{
			session.close();
		}
		return list2;

	}

	public static ArrayList<Object[]> getUserNPIData(GrmUserDTO grmUser, Date startDate,
			Date endDate)
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
					+ UserID
					+ " and tp.TRACKING_TASK_STATUS_ID in (6,42)  and tp.tracking_task_type_id = 15 group by  p.name  , s.name  , ttt.name ,EXTRACTION_STATUS, TP.PRIORIY order by pl, supplier, type, TP.PRIORIY";
		}
		else
		{
			sql = "select distinct p.name pl, s.name supplier, ttt.name type ,EXTRACTION_STATUS, TP.PRIORIY from Tracking_Parametric tp, pl p, supplier s, tracking_task_type ttt where tp.pl_id = p.id and tp.supplier_id = s.id and tp.tracking_task_type_id = ttt.id and user_id = "
					+ UserID
					+ " and tp.TRACKING_TASK_STATUS_ID in (6,42)  and tp.tracking_task_type_id = 15 AND MODIFICATION_DATE BETWEEN TO_DATE('"
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
			data[3] = (data[3] == null || data[3].equals(new BigDecimal(0))) ? "Not Extracted"
					: "Extracted";
			data[4] = (data[4] == null) ? "" : data[4].toString();
			// row.add((data[3] == null) ? "Not Extracted" : "Extracted");
			result.set(i, data);

		}
		session.close();
		return result;
	}

	public static ArrayList<Object[]> getUserFeedbackData(GrmUserDTO grmUser, Date startDate,
			Date endDate)
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
			sql = sql
					+ ", U.FULL_NAME FROM tracking_parametric tp, pl, supplier s, PARAMETRIC_FEEDBACK";
			sql = sql
					+ "_CYCLE FBc, PARAMETRIC_FEEDBACK FB, part_component c, grm.grm_user u, tracking";
			sql = sql + "_feedback_type tft WHERE TP.USER_ID = " + UserID
					+ " AND TP.TRACKING_TASK_STATUS_ID = " + taskStatus.getId() + " ";
			sql = sql
					+ "AND TP.PL_ID = PL.ID AND TP.SUPPLIER_ID = S.ID AND TP.DOCUMENT_ID = C.DOCUMENT";
			sql = sql
					+ "_ID AND C.COM_ID = FB.ITEM_ID AND FBc.ISSUED_BY = U.ID AND FB.FEEDBACK_TYPE = ";
			sql = sql + "TFT.ID AND FBc.FEEDBACK_RECIEVED = 0 AND FBc.ISSUED_TO = " + UserID
					+ " AND FB.ID = FBC.P";
			sql = sql + "ARA_FEEDBACK_ID";
		}
		else
		{
			sql = " SELECT DISTINCT PL.NAME pl_name, s.name supplier_name, TFT.NAME feedback_name";
			sql = sql
					+ ", U.FULL_NAME FROM tracking_parametric tp, pl, supplier s, PARAMETRIC_FEEDBACK";
			sql = sql
					+ "_CYCLE FBc, PARAMETRIC_FEEDBACK FB, part_component c, grm.grm_user u, tracking";
			sql = sql + "_feedback_type tft WHERE TP.USER_ID = " + UserID
					+ " AND TP.TRACKING_TASK_STATUS_ID = " + taskStatus.getId() + " ";
			sql = sql
					+ "AND TP.PL_ID = PL.ID AND TP.SUPPLIER_ID = S.ID AND TP.DOCUMENT_ID = C.DOCUMENT";
			sql = sql
					+ "_ID AND C.COM_ID = FB.ITEM_ID AND FBc.ISSUED_BY = U.ID AND FB.FEEDBACK_TYPE = ";
			sql = sql
					+ "TFT.ID AND FBc.FEEDBACK_RECIEVED = 0 AND TP.MODIFICATION_DATE BETWEEN TO_DATE ('"
					+ startDate + "', 'MM/DD/YYYY')";
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
			SQLQuery query = session
					.createSQLQuery("select distinct issued_by_id from parts_feedback where feedback_recieved=0 and issued_to_id="
							+ userId);
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

	public static ArrayList<Object[]> getTLReviewFilterData(GrmUserDTO grmUser, Date startDate,
			Date endDate)
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
				SqlStatement = SqlStatement
						+ "ame FROM Tracking_Parametric tp, pl p, supplier s, trackin";
				SqlStatement = SqlStatement
						+ "g_task_type ttt, grm.GRM_USER u, TRACKING_TASK_STATUS st WHERE tp.pl_id = p.id";
				SqlStatement = SqlStatement + " AND tp.TRACKING_TASK_STATUS_ID IN ("
						+ taskStatus.getId() + ") AND tp.supplier_id = s.id AND tp.tracki";
				SqlStatement = SqlStatement
						+ "ng_task_type_id = ttt.id AND u.id = tp.user_id AND st.id = tp.TRACKING_TASK_ST";
				SqlStatement = SqlStatement
						+ "ATUS_ID AND tp.user_id IN (SELECT id FROM grm.GRM_USER WHERE Leader = "
						+ UserID + ") GROU";
				SqlStatement = SqlStatement + "P BY p.name, s.name, ttt.name, U.FULL_NAME, st.NAME";
			}
			else
			{
				SqlStatement = " SELECT DISTINCT p.name pl, s.name supplier, ttt.name TYPE, U.FULL_NAME user_N";
				SqlStatement = SqlStatement
						+ "ame FROM Tracking_Parametric tp, pl p, supplier s, trackin";
				SqlStatement = SqlStatement
						+ "g_task_type ttt, grm.GRM_USER u, TRACKING_TASK_STATUS st WHERE tp.pl_id = p.id";
				SqlStatement = SqlStatement + " AND tp.TRACKING_TASK_STATUS_ID IN ("
						+ taskStatus.getId() + ") AND tp.supplier_id = s.id AND tp.tracki";
				SqlStatement = SqlStatement
						+ "ng_task_type_id = ttt.id AND u.id = tp.user_id AND st.id = tp.TRACKING_TASK_ST";
				SqlStatement = SqlStatement
						+ "ATUS_ID AND tp.user_id IN (SELECT id FROM grm.GRM_USER WHERE Leader = "
						+ UserID + ")";
				SqlStatement = SqlStatement
						+ "AND TP.MODIFICATION_DATE BETWEEN TO_DATE('"
						+ start
						+ "', 'MM/DD/YYYY') AND TO_DATE('"
						+ end
						+ "', 'MM/DD/YYYY') GROUP BY   p.name, s.name, ttt.name, U.FULL_NAME, st.NAME";

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

	public static ArrayList<Object[]> getTLFeedbackFilterData(GrmUserDTO grmUser, Date startDate,
			Date endDate)
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
				Sql = Sql
						+ "e, TFT.NAME feedback_name FROM tracking_parametric tp, pl, supplier s, PARAMET";
				Sql = Sql
						+ "RIC_FEEDBACK_CYCLE FBc, PARAMETRIC_FEEDBACK FB, part_component c, grm.grm_user";
				Sql = Sql
						+ " u, tracking_feedback_type tft WHERE TP.TRACKING_TASK_STATUS_ID = 28 AND TP.PL";
				Sql = Sql
						+ "_ID = pl.id AND TP.USER_ID IN (SELECT id FROM grm.grm_user WHERE leader = "
						+ UserID + ") ";
				Sql = Sql
						+ "AND TP.SUPPLIER_ID = s.id AND FB.ITEM_ID = c.com_id AND C.DOCUMENT_ID = TP.DOC";
				Sql = Sql + "UMENT_ID AND FBC.ISSUED_TO = " + UserID
						+ " AND FBC.FEEDBACK_RECIEVED = 0 AND FBC.ISSUED_";
				Sql = Sql
						+ "BY = u.id AND FB.FEEDBACK_TYPE = tft.id AND FB.ID = FBC.PARA_FEEDBACK_ID";
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
				Sql = Sql
						+ "e, TFT.NAME feedback_name FROM tracking_parametric tp, pl, supplier s, PARAMET";
				Sql = Sql
						+ "RIC_FEEDBACK_CYCLE FBc, PARAMETRIC_FEEDBACK FB, part_component c, grm.grm_user";
				Sql = Sql
						+ " u, tracking_feedback_type tft WHERE TP.TRACKING_TASK_STATUS_ID = 28 AND TP.PL";
				Sql = Sql
						+ "_ID = pl.id AND TP.USER_ID IN (SELECT id FROM grm.grm_user WHERE leader = "
						+ UserID + ") ";
				Sql = Sql
						+ "AND TP.SUPPLIER_ID = s.id AND FB.ITEM_ID = c.com_id AND C.DOCUMENT_ID = TP.DOC";
				Sql = Sql + "UMENT_ID AND FBC.ISSUED_TO = " + UserID
						+ " AND FBC.FEEDBACK_RECIEVED = 0 AND FBC.ISSUED_";
				Sql = Sql
						+ "BY = u.id AND FB.FEEDBACK_TYPE = tft.id AND FB.ID = FBC.PARA_FEEDBACK_ID"
						+ " AND TP.MODIFICATION_DATE BETWEEN TO_DATE('" + start
						+ "', 'MM/DD/YYYY') AND TO_DATE('" + end + "', 'MM/DD/YYYY')";
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

	public static ArrayList<TableInfoDTO> getReviewPDF(Long[] usersId, String plName,
			String vendorName, String type, String extracted, Date startDate, Date endDate,
			String feedbackTypeStr, String inputType, String priority, String status,
			String pltype, Long userid)
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
				List<TrackingTaskStatus> statusObj;
				Criteria statusCriteria = session.createCriteria(TrackingTaskStatus.class);

				if(status.equals(StatusName.assigned))
				{
					Disjunction or = Restrictions.disjunction();
					or.add(Restrictions.eq("name", StatusName.assigned));
					or.add(Restrictions.eq("name", StatusName.inprogress));
					statusCriteria.add(or);
				}
				else
				{
					statusCriteria.add(Restrictions.eq("name", status));
				}
				statusObj = statusCriteria.list();
				criteria.add(Restrictions.in("trackingTaskStatus", statusObj));
			}

			if(usersId != null && (!(usersId.length == 0) && usersId[0] != 0))
			{
				criteria.add(Restrictions.in("parametricUserId", usersId));
			}

			// System.out.println(criteria.list().size());
			if(startDate != null && endDate != null)
			{

				criteria.add(Restrictions.ge("modificationdate", startDate));
				criteria.add(Restrictions.lt("modificationdate", endDate));

			}
			if(inputType.equals("QAReview"))
			{
				criteria.add(Restrictions.eq("qaUserId", userid));
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
					if(inputType.equals("assigned"))
					{
						typeCriteria.add(Restrictions.or(Restrictions.eq("name", "NPI"),
								Restrictions.eq("name", "NPI Transferred")));
					}
					else
					{
						typeCriteria.add(Restrictions.or(Restrictions.eq("name", "NPI"),
								Restrictions.eq("name", "NPI Transferred"),
								Restrictions.eq("name", "NPI Update")));

					}

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
				sql = " Get_PL_Type(this_.PL_ID)='" + pltype + "'";
				criteria.add(Restrictions.sqlRestriction(sql));
			}
			List list = criteria.list();
			for(int i = 0; i < list.size(); i++)
			{
				TrackingParametric obj = (TrackingParametric) list.get(i);
				TableInfoDTO docInfo = new TableInfoDTO();
				System.out.println("no:" + i);
				System.out.println("track : " + obj.getId());
				if(inputType.equals("QAReview"))
				{
					int infectedParts = getInfectedPartsByDoc(obj.getDocument().getId());
					int infectedTaxonomies = getInfectedTaxonomiesByDoc(obj.getDocument().getId());
					docInfo.setInfectedParts(infectedParts);
					docInfo.setInfectedTaxonomies(infectedTaxonomies);
				}
				System.out.println(obj.getDocument().getPdf());
				docInfo.setPdfUrl(obj.getDocument().getPdf().getSeUrl());
				docInfo.setPlName(obj.getPl().getName());
				docInfo.setSupplierName(obj.getSupplier().getName());
				docInfo.setStatus(obj.getTrackingTaskStatus().getName());
				docInfo.setTaskType(obj.getTrackingTaskType().getName());

				docInfo.setDevUserName(ParaQueryUtil.getGRMUser(obj.getParametricUserId())
						.getFullName());
				docInfo.setExtracted(obj.getExtractionStatus() == null ? "No" : "Yes");
				docInfo.setPriority("" + obj.getPrioriy());

				if(inputType.equals("QAReview"))
				{
					if(status.equals("All"))
					{
						status = StatusName.qaReview;
					}
					List<Integer> noparts = getnoPartsPerPDFandPL(obj.getDocument().getId(), obj
							.getPl().getId(), usersId, status);
					docInfo.setPDFParts(noparts.get(0));
					docInfo.setPLParts(noparts.get(1));
					docInfo.setPDFDoneParts(noparts.get(2));
					docInfo.setPLDoneParts(noparts.get(3));
					if(obj.getTrackingTaskType().getName().contains("NPI"))
						docInfo.setTaskparts(noparts.get(4));
					else
						docInfo.setTaskparts(noparts.get(0) - noparts.get(4));

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
				docInfo.setPlType(Pltype == null ? "" : Pltype.getName());
				Date date = obj.getModificationdate();
				// if(inputType.equals("assigned"))
				// {
				// date = obj.getAssignedDate();
				// }
				// else if(inputType.equals("finished"))
				// {
				// date = obj.getFinishedDate();
				// }
				// else if(inputType.equals("QAReview"))
				// {
				// date = obj.getQaReviewDate();
				// }
				if(date != null)
				{
					docInfo.setDate(date.toString().split(" ")[0]);
				}

				tableData.add(docInfo);
			}
			if(inputType.equals("QAReview"))
			{
				Collections.sort(tableData);
			}

		}catch(Exception ex)
		{
			ex.printStackTrace();
		}finally
		{
			session.close();
		}
		return tableData;
	}

	public static ArrayList<TableInfoDTO> getReviewPDFupdate(Long[] usersId, String plName,
			String vendorName, String type, String extracted, Date startDate, Date endDate,
			String feedbackTypeStr, String inputType, String priority, String status, String pltype)
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
				List<TrackingTaskStatus> statusObj;
				if(status.equals(StatusName.assigned))
				{
					Disjunction or = Restrictions.disjunction();
					or.add(Restrictions.eq("name", StatusName.assigned));
					or.add(Restrictions.eq("name", StatusName.inprogress));
					statusCriteria.add(or);
				}
				else
				{
					statusCriteria.add(Restrictions.eq("name", status));
				}

				statusObj = statusCriteria.list();
				criteria.add(Restrictions.in("trackingTaskStatus", statusObj));
			}

			if(!(usersId.length == 0) && usersId[0] != 0 && usersId != null)
			{
				criteria.add(Restrictions.in("parametricUserId", usersId));
			}

			// System.out.println(criteria.list().size());
			if(startDate != null && endDate != null)
			{

				criteria.add(Restrictions.ge("modificationdate", startDate));
				criteria.add(Restrictions.lt("modificationdate", endDate));

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
					typeCriteria.add(Restrictions.eq("name", "NPI Update"));
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
				sql = " Get_PL_Type(this_.PL_ID)='" + pltype + "'";
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
				docInfo.setDevUserName(ParaQueryUtil.getGRMUser(obj.getParametricUserId())
						.getFullName());
				docInfo.setExtracted(obj.getExtractionStatus() == null ? "No" : "Yes");
				docInfo.setPriority("" + obj.getPrioriy());

				if(inputType.equals("QAReview"))
				{
					List<Integer> noparts = getnoPartsPerPDFandPL(obj.getDocument().getId(), obj
							.getPl().getId(), usersId, StatusName.qaReview);
					docInfo.setPDFParts(noparts.get(0));
					docInfo.setPLParts(noparts.get(1));
					docInfo.setPDFDoneParts(noparts.get(2));
					docInfo.setPLDoneParts(noparts.get(3));
					if(obj.getTrackingTaskType().getName().contains("NPI"))
						docInfo.setTaskparts(noparts.get(4));
					else
						docInfo.setTaskparts(noparts.get(0) - noparts.get(4));

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
				docInfo.setPlType(Pltype == null ? "" : Pltype.getName());
				Date date = obj.getModificationdate();
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

	private static List<Integer> getnoPartsPerPDFandPL(Long docid, Long plid, Long[] usersId,
			String status)
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
			SQLQuery query = session
					.createSQLQuery("SELECT   /*+ INDEX(x comp_doc_id_idx) */count(COM_ID)  FROM  PART_COMPONENT x "
							+ " WHERE   x.DOCUMENT_ID =" + docid + "");
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
			if(usersId != null && usersId.length > 0)
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
			Sql = " SELECT /*+ INDEX(x comp_doc_id_idx) */ COUNT (COM_ID) FROM PART_COMPONENT x ";
			Sql = Sql + "where x.DOCUMENT_ID in( select document_id from TRACKING_PARAMETRIC";
			Sql = Sql + " z where z.PL_ID = " + plid + " and z.TRACKING_TASK_STATUS_ID = "
					+ trackingTaskstatus.getId() + " " + users + " ";
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

			query = session
					.createSQLQuery("SELECT   /*+ INDEX(x comp_doc_id_idx) */count(COM_ID)  FROM  PART_COMPONENT x "
							+ " WHERE   x.DOCUMENT_ID =" + docid + " and DONEFLAG = 1");
			obj = query.uniqueResult();
			if(obj != null)
			{
				result.add(Integer.parseInt(obj.toString()));
			}
			else
			{
				result.add(0);
			}

			Sql = " SELECT /*+ INDEX(x COMP_DOC_DONE_IDX) */ COUNT (COM_ID) FROM PART_COMPONENT x ";
			Sql = Sql
					+ "where  X.DONEFLAG = 1 AND x.DOCUMENT_ID in( select document_id from TRACKING_PARAMETRIC";
			Sql = Sql + " z where z.PL_ID = " + plid + " and z.TRACKING_TASK_STATUS_ID = "
					+ trackingTaskstatus.getId() + " " + users + " ";
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
			query = session
					.createSQLQuery("SELECT  /*+ INDEX(x COM_DOC_IDX) */  COUNT(n.COM_ID) FROM PART_COMPONENT x,TBL_NPI_PARTS n WHERE x.COM_ID = n.COM_ID and x.DOCUMENT_ID ="
							+ docid + "");
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

	public static ArrayList<TableInfoDTO> getShowAllData(long userId, String plName,
			String vendorName, String type, String extracted, String status, Date startDate,
			Date endDate, String priority)
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
					qury.append(" AND t.TRACKING_TASK_TYPE_ID in(4,12)");
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
				System.out.println(formatter.format(startDate) + "**************"
						+ formatter.format(endDate));
				String dateRangeCond = " AND t.MODIFICATION_DATE BETWEEN TO_DATE ('"
						+ formatter.format(startDate) + "','DD/MM/RRRR')AND  TO_DATE ('"
						+ formatter.format(endDate) + "','DD/MM/RRRR')";

				// String dateRangeCond = " AND t.ASSIGNED_DATE BETWEEN TO_DATE ('" + startDate + "','MM/DD/YYYY HH24:MI:SS')AND  TO_DATE ('" +
				// endDate + "','MM/DD/YYYY HH24:MI:SS')";

				qury.append(dateRangeCond);
				// qury = qury +
				// " AND t.ASSIGNED_DATE BETWEEN TO_DATE ('01/11/2012', 'DD/MM/RRRR') AND  TO_DATE ('03/03/2013', 'DD/MM/RRRR')";
			}
			qury.append(" and T.USER_ID =" + userId);
			qury.append(" ORDER BY   plName,pdfurl");

			System.out.println(qury.toString());
			ArrayList<Object[]> result = (ArrayList<Object[]>) session.createSQLQuery(
					qury.toString()).list();
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
				docInfo.setExtracted(data[6] != null && data[6].toString().equals("1") ? "Yes"
						: "No");
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

	public static ArrayList<TableInfoDTO> getAllAssigined(long userId, String plName,
			String vendorName, String type, String extracted, String status, Date startDate,
			Date endDate, String priority)
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
				qury.append(" AND t.TRACKING_TASK_STATUS_ID in (6,42)");
			}
			if(type != null && !type.equals("All"))
			{
				if(type.equals("NPI"))
				{
					qury.append(" AND t.TRACKING_TASK_TYPE_ID in(4,12)");
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
				System.out.println(formatter.format(startDate) + "**************"
						+ formatter.format(endDate));
				String dateRangeCond = " AND t.MODIFICATION_DATE BETWEEN TO_DATE ('"
						+ formatter.format(startDate) + "','DD/MM/RRRR')AND  TO_DATE ('"
						+ formatter.format(endDate) + "','DD/MM/RRRR')";

				// String dateRangeCond = " AND t.ASSIGNED_DATE BETWEEN TO_DATE ('" + formatter.format(startDate) + "','DD/MM/RRRR')AND  TO_DATE ('" +
				// formatter.format(endDate) + "','DD/MM/RRRR')";
				qury.append(dateRangeCond);
				// qury = qury +
				// " AND t.ASSIGNED_DATE BETWEEN TO_DATE ('01/11/2012', 'DD/MM/RRRR') AND  TO_DATE ('03/03/2013', 'DD/MM/RRRR')";
			}
			qury.append(" ORDER BY   plName,pdfurl");

			System.out.println(qury.toString());
			ArrayList<Object[]> result = (ArrayList<Object[]>) session.createSQLQuery(
					qury.toString()).list();
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
				docInfo.setExtracted(data[6] != null && data[6].toString().equals("1") ? "Yes"
						: "No");
				docInfo.setPriority(data[7] == null ? "" : data[7].toString());
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

	public static Map<String, ArrayList<ArrayList<String>>> getParametricValueReview1(
			Long[] usersId, String plName, String vendorName, String type, String status,
			Date startDate, Date endDate, Long[] docsIds) throws Exception
	{
		Session session = SessionUtil.getSession();
		Map<String, ArrayList<ArrayList<String>>> allData = new HashMap<String, ArrayList<ArrayList<String>>>();

		long statusId = ParaQueryUtil.getTrackingTaskStatus(session, status).getId();
		SQLQuery query = null;
		try
		{
			StringBuffer qury = new StringBuffer();
			qury.append("  SELECT GET_PL_NAME (t.PL_ID) plName,getuserName (T.USER_ID),GetTaskTypeName (t.TRACKING_TASK_TYPE_ID) task_type, GETSUPPLIERNAME (t.supplier_id) supName,C.PART_NUMBER,FAM.NAME,Get_family_crossName(C.FAMILY_CROSS_ID) family_Cross , Get_GENERIC_Name (C.GENERIC_ID) generic_Nam,GET_MSK_Value(c.MASK_ID,C.PART_NUMBER) MASK,GETNPIBYCOMID(c.COM_ID) NPI_FLAG, GETNPINewsPDFURL (c.DOCUMENT_ID) newsLike,c.DESCRIPTION, GETPDFURLBYDOCID (t.DOCUMENT_ID) pdfurl,F.NAME fetName, G.GROUP_FULL_VALUE fetVal,t.ASSIGNED_DATE,"
					+ " GetTaskStatusName (TRACKING_TASK_STATUS_ID) task_Status,C.COM_ID,R.PL_FEATURE_ID,R.GROUP_APPROVED_VALUE_ID,t.DOCUMENT_ID,t.PL_ID"
					+ " FROM  TRACKING_PARAMETRIC T, Part_COMPONENT c,family fam,PARAMETRIC_REVIEW_DATA r,pl_feature_unit pf, feature f,PARAMETRIC_APPROVED_GROUP g WHERE t.DOCUMENT_ID = c.DOCUMENT_ID and T.SUPPLIER_PL_ID=C.SUPPLIER_PL_ID AND c.COM_ID = R.COM_ID and C.FAMILY_ID=FAM.ID AND R.PL_FEATURE_ID = PF.ID AND PF.FET_ID = F.ID AND R.GROUP_APPROVED_VALUE_ID = G.ID");
			if(plName != null && !plName.equals("All"))
			{
				qury.append("  AND T.PL_ID=GET_PL_ID('" + plName + "')");
			}
			// if(!vendorName.equals("All") && vendorName != null)
			// {
			// qury.append("  and T.SUPPLIER_ID=GETSUPPLIERID('" + vendorName + "')");
			// }
			if(status != null && !status.equals("All"))
			{
				// qury.append(" AND t.TRACKING_TASK_STATUS_ID = getTaskstatusId('" + status + "')");
				qury.append(" AND t.TRACKING_TASK_STATUS_ID =" + statusId);
			}
			// if(type != null && !type.equals("All"))
			// {
			// if(type.equals("NPI"))
			// {
			// qury.append(" AND t.TRACKING_TASK_TYPE_ID in(getTaskTypeId('" + StatusName.npi
			// + "'),getTaskTypeId('" + StatusName.npiTransferred + "'))");
			// }
			// else
			// {
			// qury.append(" AND t.TRACKING_TASK_TYPE_ID = getTaskTypeId('" + type + "')");
			// }
			// }
			// if(!(usersId.length == 0) && usersId != null)
			// {
			// qury.append(" AND T.USER_ID IN (" + getArrayAsCommaSeperatedList(usersId) + ")");
			// }
			if(docsIds != null && docsIds.length > 0)
			{
				qury.append(" AND c.DOCUMENT_ID in ( " + getArrayAsCommaSeperatedList(docsIds)
						+ " )");
			}
			// if(startDate != null && endDate != null)
			// {
			// endDate.setDate(endDate.getDate() + 1);
			// SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
			// System.out.println(formatter.format(startDate) + "**************"
			// + formatter.format(endDate));
			//
			// String dateRangeCond = " AND t.ASSIGNED_DATE BETWEEN TO_DATE ('"
			// + formatter.format(startDate) + "','DD/MM/RRRR')AND  TO_DATE ('"
			// + formatter.format(endDate) + "','DD/MM/RRRR')";
			// qury.append(dateRangeCond);
			// }
			qury.append(" ORDER BY   T.DOCUMENT_ID,plName, C.PART_NUMBER, PF.DEVELOPMENT_ORDER");

			System.out.println(qury.toString());
			ArrayList<Object[]> result = (ArrayList<Object[]>) session.createSQLQuery(
					qury.toString()).list();
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
					plType = ParaQueryUtil.getPLType(QueryUtil.getPlByExactName(plout, session))
							.getName();
				}
				// plType = ParaQueryUtil.getPLType(QueryUtil.getPlByExactName(plout, session)).getName();
				System.out.println(plout + " @ " + "Part:" + data[4].toString() + " ~ "
						+ data[13].toString() + " ~ " + data[14].toString() + " idx:"
						+ partData.indexOf("F_" + data[13].toString()));
				if(data[4].toString().equals(lastPart))
				{
					// partData.add(data[11].toString()); /* fet Value */
					partData.set(partData.indexOf("F_" + data[13].toString()), data[14].toString());
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
					boolean NPIFlag = isNPITaskType(usersId, plout, vendorName, type, status,
							startDate, endDate, docsIds);
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
					partData.add(data[5] != null ? data[5].toString() : "");

					if(plType.equals("Semiconductor"))
					{
						/** family cross */

						partData.add(data[6] != null ? data[6].toString() : "");
						/** generic */
						partData.add((data[7] == null) ? "" : data[7].toString());
						/** Mask */
						partData.add((data[8] == null) ? "" : data[8].toString());

						if(NPIFlag)
						{
							/** NPI Flag */
							partData.add((data[9] != null && data[9].toString().equals("1")) ? "Yes"
									: "");
							/** NPI news */
							partData.add((data[10] == null) ? "" : data[10].toString());
							List<String> newsData = getNewsLink(data[12].toString());
							if(!newsData.isEmpty())
							{
								/** NPI desc */
								partData.add(newsData.get(1));
								/** NPI date */
								partData.add(newsData.get(2));
							}
							else
							{
								partData.add("");
								partData.add("");
							}
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
							partData.add((data[9] != null && data[9].toString().equals("1")) ? "Yes"
									: "");
							/** NPI news */
							partData.add((data[10] == null) ? "" : data[10].toString());
							List<String> newsData = getNewsLink(data[12].toString());
							if(!newsData.isEmpty())
							{
								/** NPI desc */
								partData.add(newsData.get(1));
								/** NPI date */
								partData.add(newsData.get(2));
							}
							else
							{
								partData.add("");
								partData.add("");
							}

						}
						/** pdf url */
						partData.add(data[12].toString());

					}
					plFets = plFetsMap.get(plout);
					partData.addAll(plFets);
					partData.set(partData.indexOf("F_" + data[13].toString()), data[14].toString());
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

	public static Map<String, ArrayList<ArrayList<String>>> getFeedbackParametricValueReview(
			Long[] usersId, String plName, String vendorName, String docStatus,
			String feedbackType, String issuedby, Date startDate, Date endDate, Long[] docsIds,
			long issuedToId) throws Exception
	{
		Session session = SessionUtil.getSession();
		Map<String, ArrayList<ArrayList<String>>> allData = new HashMap<String, ArrayList<ArrayList<String>>>();

		SQLQuery query = null;
		try
		{
			StringBuffer qury = new StringBuffer();
			qury.append("  SELECT GET_PL_NAME (t.PL_ID) plName,getuserName (T.USER_ID),GetTaskTypeName (t.TRACKING_TASK_TYPE_ID) task_type, GETSUPPLIERNAME (t.supplier_id) supName,C.PART_NUMBER,FAM.NAME ,"
					+ " Get_family_crossName (C.FAMILY_CROSS_ID) family_Cross,Get_GENERIC_Name (C.GENERIC_ID) generic_Nam,GET_MSK_Value (c.MASK_ID,C.PART_NUMBER) MASK,GETNPIBYCOMID(c.COM_ID) NPI_FLAG,"
					+ " GET_News_PDF_URL(c.DOCUMENT_ID, c.SUPPLIER_ID) newsLike,c.DESCRIPTION, GETPDFURLBYDOCID (t.DOCUMENT_ID) pdfurl,F.NAME fetName, G.GROUP_FULL_VALUE fetVal,t.ASSIGNED_DATE,"
					+ " GetTaskStatusName (TRACKING_TASK_STATUS_ID) task_Status,C.COM_ID,R.PL_FEATURE_ID,R.GROUP_APPROVED_VALUE_ID,t.DOCUMENT_ID,t.PL_ID,TY.NAME type"
					+ " FROM  TRACKING_PARAMETRIC T, part_COMPONENT c,family fam,PARAMETRIC_REVIEW_DATA r,pl_feature_unit pf, feature f,PARAMETRIC_APPROVED_GROUP g,PARAMETRIC_FEEDBACK FB, PARAMETRIC_FEEDBACK_CYCLE FBC, TRACKING_FEEDBACK_TYPE ty"
					+ " WHERE t.DOCUMENT_ID = c.DOCUMENT_ID and T.SUPPLIER_PL_ID=C.SUPPLIER_PL_ID AND c.COM_ID = R.COM_ID and C.FAMILY_ID=FAM.ID AND R.PL_FEATURE_ID = PF.ID AND PF.FET_ID = F.ID AND R.GROUP_APPROVED_VALUE_ID = G.ID And FB.Item_id = R.COM_ID And FB.ID = FBC.PARA_FEEDBACK_ID And TY.ID = FB.FEEDBACK_TYPE And FBC.FEEDBACK_RECIEVED = 0");
			if(plName != null && !plName.equals("All"))
			{
				qury.append("  AND T.PL_ID=GET_PL_ID('" + plName + "')");
			}
			// if(!vendorName.equals("All") && vendorName != null)
			// {
			// qury.append("  and T.SUPPLIER_ID=GETSUPPLIERID('" + vendorName + "')");
			// }
			// if(!(usersId.length == 0) && usersId != null)
			// {
			//
			// qury.append(" AND T.USER_ID IN (" + getArrayAsCommaSeperatedList(usersId) + ")");
			//
			// }
			if(docStatus != null && !docStatus.equals("All"))
			{
				qury.append(" AND t.TRACKING_TASK_STATUS_ID = getTaskstatusId('" + docStatus + "')");
			}

			if(docsIds != null && docsIds.length > 0)
			{
				qury.append(" AND c.DOCUMENT_ID in ( " + getArrayAsCommaSeperatedList(docsIds)
						+ " )");
			}
			qury.append(" AND FBC.ISSUED_TO = " + issuedToId);
			Criteria ParametricFeedbackCriteria = session
					.createCriteria(ParametricFeedbackCycle.class);
			ParametricFeedbackCriteria.add(Restrictions.eq("feedbackRecieved", 0l));
			ParametricFeedbackCriteria.add(Restrictions.eq("issuedTo", issuedToId));
			ParametricFeedbackCriteria.createAlias("parametricFeedback", "feedback");
			ParametricFeedbackCriteria.add(Restrictions.eq("feedback.type", "P"));
			if((feedbackType != null) && (!"All".equals(feedbackType)))
			{
				ParametricFeedbackCriteria.add(Restrictions.eq("feedback.trackingFeedbackType",
						ParaQueryUtil.getTrackingFeedbackType(feedbackType)));
			}
			if((issuedby != null) && (!"All".equals(issuedby)))
			{
				GrmUser issuer = ParaQueryUtil.getGRMUserByName(issuedby);
				ParametricFeedbackCriteria.add(Restrictions.eq("issuedBy", issuer.getId()));
			}

			List<ParametricFeedbackCycle> parametricfeedbackcycle = ParametricFeedbackCriteria
					.list();
			Set<Long> docSet = new HashSet<Long>();
			if(parametricfeedbackcycle != null)
			{
				for(int i = 0; i < parametricfeedbackcycle.size(); i++)
				{
					docSet.add(parametricfeedbackcycle.get(i).getParametricFeedback().getDocument()
							.getId());
				}
			}
			if(docSet.size() > 0)
			{
				Long[] docsIds2 = new Long[docSet.size()];
				docsIds2 = docSet.toArray(docsIds2);
				qury.append(" AND c.DOCUMENT_ID in ( " + getArrayAsCommaSeperatedList(docsIds2)
						+ " )");
			}

			// if(startDate != null && endDate != null)
			// {
			// SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
			// System.out.println(formatter.format(startDate) + "**************"
			// + formatter.format(endDate));
			//
			// String dateRangeCond = " AND t.ASSIGNED_DATE BETWEEN TO_DATE ('"
			// + formatter.format(startDate) + "','DD/MM/RRRR')AND  TO_DATE ('"
			// + formatter.format(endDate) + "','DD/MM/RRRR')";
			// qury.append(dateRangeCond);
			// }
			qury.append(" ORDER BY   T.DOCUMENT_ID,plName, C.PART_NUMBER, PF.DEVELOPMENT_ORDER");

			System.out.println(qury.toString());
			ArrayList<Object[]> result = (ArrayList<Object[]>) session.createSQLQuery(
					qury.toString()).list();
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
				String plType = ParaQueryUtil.getPLType(QueryUtil.getPlByExactName(plout, session))
						.getName();
				System.out.println(plout + " @ " + "Part:" + data[4].toString() + " ~ "
						+ data[13].toString() + " ~ " + data[14].toString() + " idx:"
						+ partData.indexOf("F_" + data[13].toString()));
				if(data[4].toString().equals(lastPart))
				{
					// partData.add(data[11].toString()); /* fet Value */
					partData.set(partData.indexOf("F_" + data[13].toString()), data[14].toString());
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
					boolean NPIFlag = isNPITaskType(usersId, plout, vendorName, null, docStatus,
							startDate, endDate, docsIds);
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
						/** family cross */
						partData.add(data[6] != null ? data[6].toString() : "");
						/** generic */
						partData.add((data[7] == null) ? "" : data[7].toString());
						/** Mask */
						partData.add((data[8] == null) ? "" : data[8].toString());
						if(NPIFlag)
						{

							/** NPI Flag */
							partData.add((data[9] != null && data[9].toString().equals("1")) ? "Yes"
									: "");
							/** NPI news */
							partData.add((data[10] == null) ? "" : data[10].toString());
							List<String> newsData = getNewsLink(data[12].toString());
							if(!newsData.isEmpty())
							{
								/** NPI desc */
								partData.add(newsData.get(1));
								/** NPI date */
								partData.add(newsData.get(2));
							}
							else
							{
								partData.add("");
								partData.add("");
							}
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
							partData.add((data[9] != null && data[9].toString().equals("1")) ? "Yes"
									: "");
							/** NPI news */
							partData.add((data[10] == null) ? "" : data[10].toString());
							List<String> newsData = getNewsLink(data[12].toString());
							if(!newsData.isEmpty())
							{
								/** NPI desc */
								partData.add(newsData.get(1));
								/** NPI date */
								partData.add(newsData.get(2));
							}
							else
							{
								partData.add("");
								partData.add("");
							}
						}
						partData.add(data[12].toString());
						/** pdf url */
					}
					plFets = plFetsMap.get(plout);
					partData.addAll(plFets);
					partData.set(partData.indexOf("F_" + data[13].toString()), data[14].toString());
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

	public static Map<String, ArrayList<ArrayList<String>>> getQAPDFData(Long[] usersId,
			String plName, String vendorName, String type, Date startDate, Date endDate,
			Long[] docsIds, Long qaUser, String availableStatus, String Pltype) throws Exception
	{
		Session session = SessionUtil.getSession();
		Map<String, ArrayList<ArrayList<String>>> allData = new HashMap<String, ArrayList<ArrayList<String>>>();

		SQLQuery query = null;
		try
		{
			StringBuffer qury = new StringBuffer();
			String Sql = "";
			Sql = " SELECT GET_PL_NAME (t.PL_ID) plName, getuserName (T.USER_ID), GetTaskTypeName";
			Sql = Sql
					+ " (t.TRACKING_TASK_TYPE_ID) task_type, GETSUPPLIERNAME (t.supplier_id) supName,";
			Sql = Sql
					+ " C.PART_NUMBER, FAM.NAME, Get_family_crossName (C.FAMILY_CROSS_ID) family_Cros";
			Sql = Sql
					+ "s, Get_GENERIC_Name (C.GENERIC_ID) generic_Nam, GET_MSK_Value (c.MASK_ID, C.PA";
			Sql = Sql
					+ "RT_NUMBER) MASK, GETNPIBYCOMID(c.COM_ID) NPI_FLAG, GET_News_PDF_URL (c.DOCUMENT_ID, c.SUPPLIER_ID) n";
			Sql = Sql
					+ "ewsLike, c.DESCRIPTION, GETPDFURLBYDOCID (t.DOCUMENT_ID) pdfurl, F.NAME fetNam";
			Sql = Sql
					+ "e, G.GROUP_FULL_VALUE fetVal, t.ASSIGNED_DATE, GetTaskStatusName (TRACKING_TAS";
			Sql = Sql
					+ "K_STATUS_ID) task_Status, C.COM_ID, R.PL_FEATURE_ID, R.GROUP_APPROVED_VALUE_ID";
			Sql = Sql
					+ ", t.DOCUMENT_ID, t.PL_ID,Decode(C.DONEFLAG,null,'No',0,'No',1,'Yes')  DONEFLAG,";
			Sql = Sql
					+ " Decode(C.EXTRACTIONFLAG,null,'No',0,'No',1,'Yes') EXTRACTIONFLAG FROM TRACKING_PARAMETRIC T, Part_COMPONENT c, family ";
			Sql = Sql
					+ "fam, PARAMETRIC_REVIEW_DATA r, pl_feature_unit pf, feature f, PARAMETRIC_APPRO";
			Sql = Sql
					+ "VED_GROUP g WHERE t.DOCUMENT_ID = c.DOCUMENT_ID AND T.SUPPLIER_PL_ID = C.SUPPL";
			Sql = Sql
					+ "IER_PL_ID AND c.COM_ID = R.COM_ID AND C.FAMILY_ID = FAM.ID AND R.PL_FEATURE_ID";
			Sql = Sql
					+ " = PF.ID AND PF.FET_ID = F.ID AND R.GROUP_APPROVED_VALUE_ID = G.ID AND T.QA_US";
			Sql = Sql + "ER_ID = " + qaUser + " AND T.TRACKING_TASK_STATUS_ID = getTaskstatusId ('"
					+ availableStatus + "')";
			qury.append(Sql);

			if(plName != null && !plName.equals("All"))
			{
				qury.append("  AND T.PL_ID=GET_PL_ID('" + plName + "')");
			}
			// if(Pltype != null && !Pltype.equals("All"))
			// {
			// qury.append("  AND Get_PL_Type(T.PL_ID)='" + Pltype + "'");
			// }
			// if(!vendorName.equals("All") && vendorName != null)
			// {
			// qury.append("  and T.SUPPLIER_ID=GETSUPPLIERID('" + vendorName + "')");
			// }
			// if(type != null && !type.equals("All"))
			// {
			// if(type.equals("NPI"))
			// {
			// qury.append(" AND t.TRACKING_TASK_TYPE_ID in(getTaskTypeId('" + StatusName.npi
			// + "'),getTaskTypeId('" + StatusName.npiTransferred
			// + "'),getTaskTypeId('" + StatusName.npiUpdate + "'))");
			// }
			// else
			// {
			// qury.append(" AND t.TRACKING_TASK_TYPE_ID = getTaskTypeId('" + type + "')");
			// }
			// }
			// if(!(usersId.length == 0) && usersId != null)
			// {
			// qury.append(" AND T.USER_ID IN (" + getArrayAsCommaSeperatedList(usersId) + ")");
			// }
			if(docsIds != null && docsIds.length > 0)
			{
				qury.append(" AND c.DOCUMENT_ID in ( " + getArrayAsCommaSeperatedList(docsIds)
						+ " )");
			}
			// if(startDate != null && endDate != null)
			// {
			// SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
			// System.out.println(formatter.format(startDate) + "**************"
			// + formatter.format(endDate));
			//
			// String dateRangeCond = " AND t.ASSIGNED_DATE BETWEEN TO_DATE ('"
			// + formatter.format(startDate) + "','DD/MM/RRRR')AND  TO_DATE ('"
			// + formatter.format(endDate) + "','DD/MM/RRRR')";
			// qury.append(dateRangeCond);
			// // qury = qury +
			// // " AND t.ASSIGNED_DATE BETWEEN TO_DATE ('01/11/2012', 'DD/MM/RRRR') AND  TO_DATE ('03/03/2013', 'DD/MM/RRRR')";
			// }
			qury.append(" ORDER BY   T.DOCUMENT_ID,plName, C.PART_NUMBER, PF.DEVELOPMENT_ORDER");
			// Medical Application|DD Review|Minimum Storage Temperature

			System.out.println(qury.toString());
			ArrayList<Object[]> result = (ArrayList<Object[]>) session.createSQLQuery(
					qury.toString()).list();
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
				String plType = ParaQueryUtil.getPLType(QueryUtil.getPlByExactName(plout, session))
						.getName();
				System.out.println(plout + " @ " + "Part:" + data[4].toString() + " ~ "
						+ data[13].toString() + " ~ " + data[14].toString() + " idx:"
						+ partData.indexOf("F_" + data[13].toString()));
				if(data[4].toString().equals(lastPart))
				{
					// partData.add(data[11].toString()); /* fet Value */
					partData.set(partData.indexOf("F_" + data[13].toString()), data[14].toString());
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
					boolean NPIFlag = isNPITaskType(usersId, plout, vendorName, type,
							availableStatus, null, null, docsIds);
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
							partData.add((data[9] != null && data[9].toString().equals("1")) ? "Yes"
									: "");
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
							partData.add((data[9] != null && data[9].toString().equals("1")) ? "Yes"
									: "");
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
					partData.set(partData.indexOf("F_" + data[13].toString()), data[14].toString());
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

	public static ArrayList<TableInfoDTO> getTlReviewFeedbackPDFs(Long[] usersId, String plName,
			String vendorName, String status, Date startDate, Date endDate, String feedbackTypeStr,
			long issuedToId, String issuer)
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
			if(startDate != null && endDate != null)
			{
				criteria.add(Restrictions.ge("modificationdate", startDate));
				criteria.add(Restrictions.lt("modificationdate", endDate));
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

			List<ParametricFeedbackCycle> parametricfeedbackcycles = partsFeedbackCriteria.list();
			if((parametricfeedbackcycles != null))
			{
				for(int i = 0; i < parametricfeedbackcycles.size(); i++)
				{
					docsSet.add(parametricfeedbackcycles.get(i).getParametricFeedback()
							.getDocument());
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
				docInfo.setDevUserName(ParaQueryUtil.getGRMUser(obj.getParametricUserId())
						.getFullName());
				docInfo.setExtracted(obj.getExtractionStatus() == null ? "No" : "Yes");
				Date finishDate = obj.getModificationdate();
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
			SQLQuery query = session
					.createSQLQuery("select GETPDFURLbydoc1(doc_id),NEWS_TITLE,NEWS_DATE from TBL_NEW_NPI where OFFLINE_DS =GET_DOCID_BY_PDFURL('"
							+ pdfURL + "') and rownum=1");
			Object[] list = (Object[]) query.uniqueResult();
			if(list == null || list.length == 0)
			{
				newsData.add("");
				newsData.add("");
				newsData.add("");
			}
			else
			{
				newsLink = (list[0] == null) ? "" : list[0].toString();
				newsDesc = (list[1] == null) ? "" : list[1].toString();
				newsDate = (list[2] == null) ? "" : list[2].toString();
				newsData.add(newsLink);
				newsData.add(newsDesc);
				newsData.add(newsDate);
			}
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
			SQLQuery query = session
					.createSQLQuery("select pdf_url from cm.pdf_table where pdf_id in (select pdf_id from cm.tbl_npi_new where offline_ds="
							+ pdfId + ")");
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
			SQLQuery query = session.createSQLQuery("select mvcode from cm.pdf_table where pdf_id="
					+ pdfId);
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

	public static ArrayList<TableInfoDTO> getDevFeedbackPDF(long userId, String plName,
			String vendorName, String issuedBy, String feedbackTypeStr, Date startDate, Date endDate)
	{

		ArrayList<TableInfoDTO> tableData = new ArrayList<TableInfoDTO>();

		Session session = SessionUtil.getSession();
		GrmUser toUser = null;

		try
		{
			HashMap<Document, Date> docsmap = new HashMap<Document, Date>();
			Set<Document> docsSet = new HashSet<Document>();
			Criteria criteria = session.createCriteria(TrackingParametric.class);
			criteria.add(Restrictions.eq("parametricUserId", userId));
			if(startDate != null && endDate != null)
			{
				criteria.add(Expression.between("modificationdate", startDate, endDate));
			}
			criteria.add(Restrictions.eq("trackingTaskStatus",
					ParaQueryUtil.getTrackingTaskStatus(session, StatusName.engFeedback)));

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

			Criteria ParametricFeedbackCriteria = session
					.createCriteria(ParametricFeedbackCycle.class);
			ParametricFeedbackCriteria.add(Restrictions.eq("issuedTo", userId));
			ParametricFeedbackCriteria.add(Restrictions.eq("feedbackRecieved", 0l));

			if(issuedBy != null && !issuedBy.equals("All"))
			{
				GrmUser byUser = ParaQueryUtil.getGRMUserByName(issuedBy);
				ParametricFeedbackCriteria.add(Restrictions.eq("issuedBy", byUser.getId()));
			}
			// if(startDate != null && endDate != null)
			// {
			// ParametricFeedbackCriteria.add(Expression.between("storeDate", startDate, endDate));
			// }
			if(feedbackTypeStr != null && !feedbackTypeStr.equals("All"))
			{
				ParametricFeedbackCriteria.createAlias("parametricFeedback", "Feedback");
				ParametricFeedbackCriteria.add(Restrictions.eq("Feedback.trackingFeedbackType",
						ParaQueryUtil.getTrackingFeedbackType(feedbackTypeStr)));
			}

			List<ParametricFeedbackCycle> parametricfeedbackCycles = ParametricFeedbackCriteria
					.list();
			if(parametricfeedbackCycles != null)
			{
				for(int i = 0; i < parametricfeedbackCycles.size(); i++)
				{
					ParametricFeedbackCycle parametricfeedbackCycle = parametricfeedbackCycles
							.get(i);
					Document doc = parametricfeedbackCycle.getParametricFeedback().getDocument();
					docsSet.add(parametricfeedbackCycle.getParametricFeedback().getDocument());
					docsmap.put(doc, parametricfeedbackCycle.getStoreDate());
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
				docInfo.setDevUserName(ParaQueryUtil.getGRMUser(obj.getParametricUserId())
						.getFullName());
				docInfo.setExtracted(obj.getExtractionStatus() == null ? "No" : "Yes");
				Date date = obj.getModificationdate();
				docInfo.setDate(date.toString());
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
			FamilyCross fam = null;
			MapGeneric gen = null;
			// long comId = System.nanoTime();
			TrackingParametric track = getTrackingParametricByPdfUrlAndSupName(
					partInfo.getPdfUrl(), partInfo.getPlName(), partInfo.getSupplierName(), session);
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

			Object[] nunalphavalues = getnunalphavalues(
					partInfo.getMask() == null ? "" : partInfo.getMask(),
					partInfo.getGeneric() == null ? "" : partInfo.getGeneric(),
					partInfo.getFamilycross() == null ? "" : partInfo.getFamilycross(), session);
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

				MasterPartMask mask = getMask(nunalphavalues[1].toString());
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
				gen = ParaQueryUtil.getGeneric(nunalphavalues[2].toString());
				if(gen == null)
				{
					gen = insertGeneric(partInfo.getGeneric(), session);
				}
				fam = ParaQueryUtil.getFamilyCross(nunalphavalues[3].toString());
				if(fam == null)
				{
					fam = insertFamilyCross(partInfo.getFamilycross(), session);
				}
				com.setFamilyCross(fam);
				com.setMapGeneric(gen);
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

			com.setAutoFlag(1L);
			session.saveOrUpdate(com);
			if(partInfo.getNPIFlag() != null && partInfo.getNPIFlag().equalsIgnoreCase("Yes"))
			{
				insertNPIPart(com, partInfo.getNewsLink(), session);
			}

			if(gen != null && fam != null)
			{
				famGen = new MasterFamilyGeneric();
				famGen.setComId(com);
				famGen.setFamilyCross(fam);
				famGen.setMapGeneric(gen);
				famGen.setAutoFlag(1l);
				famGen.setSupplierId(track.getSupplier());
				famGen.setInsertiondate(new Date());
				session.saveOrUpdate(famGen);
				// session.beginTransaction().commit()
			}

			Map<String, String> fetsMap = partInfo.getFetValues();
			Set<String> fetNames = fetsMap.keySet();

			// String features="";
			// for(String fetName : fetNames)
			// {
			// // session.beginTransaction().begin();
			// PlFeature plFeature = ParaQueryUtil.getPlFeatureByExactName(fetName,
			// partInfo.getPlName(), session);
			// String fetValue = fetsMap.get(fetName);
			// if(fetValue.isEmpty())
			// {
			// System.out.println(fetName + "Has blank Values ");
			// continue;
			// }
			//
			// ParametricApprovedGroup group = ParaQueryUtil.getParametricApprovedGroup(fetValue,
			// plFeature, session);
			// ParametricReviewData data = new ParametricReviewData();
			// long id = QueryUtil.getRandomID();
			// data.setComponent(com);
			// data.setTrackingParametric(track);
			// data.setGroupApprovedValueId(group.getId());
			// data.setPlFeature(plFeature);
			// data.setStoreDate(new Date());
			// data.setId(id);
			// session.saveOrUpdate(data);
			// // session.beginTransaction().commit();
			// }
			saveParametricReviewData(partInfo, com.getComId(), track.getId(), session);
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

	private static Object[] getnunalphavalues(String mask, String generic, String familycross,
			Session session)
	{

		String sql = "select 1,";
		if(!mask.equals(""))
			sql += "CM.NONALPHANUM_MASK('" + mask + "') mask,";
		if(!generic.equals(""))
			sql += "CM.NONALPHANUM('" + generic + "') generic,";
		if(!familycross.equals(""))
			sql += "CM.NONALPHANUM('" + familycross + "') familycross,";

		sql = sql.substring(0, sql.length() - 1);
		sql += " from dual ";
		SQLQuery query = session.createSQLQuery(sql);
		Object[] list = (Object[]) query.uniqueResult();
		return list;

	}

	public static boolean updateParamtric(PartInfoDTO partInfo)
	{
		Session session = SessionUtil.getSession();
		try
		{
			MasterFamilyGeneric famGen = null;
			FamilyCross fam = null;
			MapGeneric gen = null;
			String partNumber = partInfo.getPN();
			String vendorName = partInfo.getSupplierName();
			PartComponent com = getComponentByPartNumberAndSupplierName(partNumber, vendorName,
					session);
			TrackingParametric track = getTrackingParametricByPdfUrlAndSupName(
					partInfo.getPdfUrl(), partInfo.getPlName(), partInfo.getSupplierName(), session);
			if(com == null)
			{
				com = new PartComponent();

				com.setDocument(track.getDocument());
				com.setSupplierPl(track.getSupplierPl());
				com.setSupplierId(track.getSupplier());
				com.setPdf(track.getDocument().getPdf());
				com.setPartNumber(partInfo.getPN());
				com.setStoreDate(new Date());
			}
			Object[] nunalphavalues = getnunalphavalues(
					partInfo.getMask() == null ? "" : partInfo.getMask(),
					partInfo.getGeneric() == null ? "" : partInfo.getGeneric(),
					partInfo.getFamilycross() == null ? "" : partInfo.getFamilycross(), session);

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
				MasterPartMask mask = getMask(nunalphavalues[1].toString());
				if(mask == null)
				{
					mask = insertMask(partInfo.getMask(), session);
				}
				com.setMasterPartMask(mask);
			}

			if(partInfo.getGeneric() != null && !partInfo.getGeneric().isEmpty()
					&& partInfo.getFamilycross() != null && !partInfo.getFamilycross().isEmpty())
			{
				gen = ParaQueryUtil.getGeneric(nunalphavalues[2].toString());
				if(gen == null)
				{
					gen = insertGeneric(partInfo.getGeneric(), session);
				}
				fam = ParaQueryUtil.getFamilyCross(nunalphavalues[3].toString());
				if(fam == null)
				{
					fam = insertFamilyCross(partInfo.getFamilycross(), session);
				}
				com.setFamilyCross(fam);
				com.setMapGeneric(gen);
			}
			if(partInfo.getFeedbackType() != null)
				com.setTrackingFeedbackType(getFeedbackType(partInfo.getFeedbackType()));
			/** Set description */
			if(partInfo.getDescription() != null)
				com.setDescription(partInfo.getDescription());
			com.setUpdateDate(new Date());
			com.setAutoFlag(1L);
			session.saveOrUpdate(com);
			session.beginTransaction().commit();

			// NPI Flag part
			// if(partInfo.getNPIFlag() != null && partInfo.getNPIFlag().equals("Yes"))
			// com.setNpiFlag(1l);
			// else
			// com.setNpiFlag(0l);
			if(partInfo.getNPIFlag() != null && partInfo.getNPIFlag().equalsIgnoreCase("Yes"))
			{
					insertNPIPart(com, partInfo.getNewsLink(), session);
			}
			if(gen != null && fam != null)
			{
				famGen = new MasterFamilyGeneric();
				famGen.setComId(com);
				famGen.setFamilyCross(fam);
				famGen.setMapGeneric(gen);
				famGen.setAutoFlag(1l);
				famGen.setSupplierId(track.getSupplier());
				famGen.setInsertiondate(new Date());
				session.saveOrUpdate(famGen);
				// session.beginTransaction().commit()
			}
			Map<String, String> fetsMap = partInfo.getFetValues();
			Set<String> fetNames = fetsMap.keySet();
			String plFetid = "";
			for(String fetName : fetNames)
			{
				// session.beginTransaction().begin();
				plFetid = fetName.split("_Id_")[0];
				PlFeature plFeature = new PlFeature(Long.parseLong(plFetid));
				// PlFeature plFeature = ParaQueryUtil.getPlFeatureByExactName(fetName,
				// partInfo.getPlName(), session);
				String fetValue = fetsMap.get(fetName);
				if((fetValue == null) || ("".equals(fetValue)))
				{
					continue;
				}
				ParametricApprovedGroup group = ParaQueryUtil.getParametricApprovedGroup(fetValue,
						plFeature, session);
				Criteria parametricReviewDataCriteria = session
						.createCriteria(ParametricReviewData.class);
				parametricReviewDataCriteria.add(Restrictions.eq("component", com));
				parametricReviewDataCriteria.add(Restrictions.eq("plFeature", plFeature));
				ParametricReviewData data = (ParametricReviewData) parametricReviewDataCriteria
						.uniqueResult();
				if(data == null)
				{
					data = new ParametricReviewData();
					long id = QueryUtil.getRandomID();
					data.setId(id);
					data.setComponent(com);
					data.setPlFeature(plFeature);
					data.setTrackingParametric(track);
				}
				// data.setComponent(com);
				data.setGroupApprovedValueId(group.getId());
				// data.setPlFeature(plFeature);
				data.setModifiedDate(ParaQueryUtil.getDate());
				session.saveOrUpdate(data);
				// session.beginTransaction().commit();
			}
			// saveParametricReviewData(partInfo,com.getComId(),track.getId(),session);
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

	public static void saveTrackingParamtric(Set<String> pdfSet, String plName,
			String supplierName, String status, String user) throws Exception
	{
		Session session = SessionUtil.getSession();
		try
		{
			// getTrackingTaskStatus(session, status);
			for(String pdfurl : pdfSet)
			{
				Criteria criteria = session.createCriteria(TrackingParametric.class);
				Document document = ParaQueryUtil.getDocumentBySeUrl(pdfurl, session);
				criteria.add(Restrictions.eq("document", document));
				criteria.add(Restrictions.eq("pl", ParaQueryUtil.getPlByPlName(session, plName)));
				if(supplierName != null)
				{
					criteria.add(Restrictions.eq("supplier",
							ParaQueryUtil.getSupplierByExactName(session, supplierName)));
				}
				TrackingParametric track = (TrackingParametric) criteria.uniqueResult();
				System.err.println("Track Id=" + track.getId());

				TrackingTaskType taskType = track.getTrackingTaskType();
				Pl pl = track.getPl();
				Long qaUserId = ParaQueryUtil.getQAUserId(pl, taskType);
				track.setQaUserId(qaUserId);
				if(status.equals(StatusName.doneFLagEngine))
				{
					track.setFinishedDate(ParaQueryUtil.getDate());
				}
				else if(status.equals(StatusName.qaReview))
				{
					track.setQaReviewDate(ParaQueryUtil.getDate());
				}
				// // if document has opened feedbacks
				// don't transfere to QA Team
				GrmUser issuedByUser = null;
				if(!user.equals(""))
				{
					issuedByUser = ParaQueryUtil.getGRMUserByName(user);
				}
				if(hasIssues(document.getId(), issuedByUser == null ? 0l : issuedByUser.getId()))
				{
					System.err.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^ Document : " + document.getId()
							+ " Has opened feedbacks^^^^^^^^^^^^^^^^^^");
					continue;
				}
				// }
				TrackingTaskStatus trackingTaskStatus = ParaQueryUtil.getTrackingTaskStatus(
						session, status);
				track.setTrackingTaskStatus(trackingTaskStatus);
				session.saveOrUpdate(track);
				// session.beginTransaction().commit();
			}

		}finally
		{
			session.close();
		}

	}

	public static void saveTrackingParamtric(Map<String, List<String>> pdfSet, String status)
			throws Exception
	{
		Session session = SessionUtil.getSession();
		try
		{
			// getTrackingTaskStatus(session, status);
			for(String pdf : pdfSet.keySet())
			{
				Document document = ParaQueryUtil.getDocumentBySeUrl(pdf, session);
				Criteria criteria = session
						.createCriteria(TrackingParametric.class)
						.add(Restrictions.eq("document", document))
						.add(Restrictions.eq("pl",
								ParaQueryUtil.getPlByPlName(session, pdfSet.get(pdf).get(0))));
				if(pdfSet.get(pdf).get(1) != null && pdfSet.get(pdf).get(1).equals(""))
				{
					criteria.add(Restrictions.eq("supplier",
							ParaQueryUtil.getSupplierByExactName(session, pdfSet.get(pdf).get(1))));
				}
				TrackingParametric track = (TrackingParametric) criteria.uniqueResult();
				System.err.println("Track Id=" + track.getId());
				// track.setQaReviewDate(new Date());
				// TrackingTaskType taskType = track.getTrackingTaskType();
				// Pl pl = track.getPl();
				// Long qaUserId = ParaQueryUtil.getQAUserId(pl, taskType);
				// track.setQaUserId(qaUserId);
				// track.setQaReviewDate(ParaQueryUtil.getDate());
				TrackingTaskStatus trackingTaskStatus = ParaQueryUtil.getTrackingTaskStatus(
						session, status);
				if(track.getTrackingTaskStatus().getName().equals(StatusName.waitingsummary))
					track.setTrackingTaskStatus(trackingTaskStatus);
				session.saveOrUpdate(track);
			}

		}finally
		{
			session.close();
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

				ParametricFeedbackCycle parametricFeedbackCycle = (ParametricFeedbackCycle) fbcriteria
						.uniqueResult();
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

				if(partInfo.getCAction() != null && partInfo.getPAction() != null
						&& partInfo.getRootCause() != null && partInfo.getActinDueDate() != null)
				{
					if(!partInfo.getCAction().equals("") && !partInfo.getPAction().equals("")
							&& !partInfo.getRootCause().equals("")
							&& !partInfo.getActinDueDate().equals(""))
					{
						feedbackAction = ApprovedDevUtil.getParaAction(partInfo.getCAction(),
								partInfo.getPAction(), partInfo.getRootCause(),
								partInfo.getActinDueDate(), session);
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
					savewrongfeatures(session, FBObj, comment, wrongfeatures,
							parametricFeedbackCycle);
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
		}
		if(parasummarystatus != null)
		{
			if(fetsissue > 0)
				defectiveparts = parasummarystatus.getDefectiveParts();
			else
				defectiveparts = parasummarystatus.getDefectiveParts() - 1;
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

	private static void savewrongfeatures(Session session, ParametricFeedback FBObj,
			String comment, String wrongfeatures, ParametricFeedbackCycle parametricFeedbackCycle)
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

	public static String sendFeedbackToSourcingTeam(String userName, String pdfUrl, String plName,
			String docFeedbackComment, String revUrl, String rightTax)
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
			if(!docFeedbackComment.equals(StatusName.NeedContact))
			{
				docFeedback.setErrortype(docFeedbackComment);
				docFeedback.setPriority(99l);
				docFeedback.setSeUrl(pdfUrl);
				docFeedback.setTeam("Parametric");
				docFeedback.setUsername(userName);
				docFeedback.setIssueDate(ParaQueryUtil.getDate());
				docFeedback.setStatus("unexecuted");
				session.saveOrUpdate(docFeedback);
			}
			Pl pl = ParaQueryUtil.getPlByPlName(plName);
			TrackingParametric trackingParametric = getTrackingParametricByDocumentAndPl(document,
					pl, session);
			TrackingTaskStatus trackingTaskStatus = null;
			if(docFeedbackComment.equals(StatusName.NeedContact))
			{
				trackingTaskStatus = ParaQueryUtil.getTrackingTaskStatusByExactName(session,
						StatusName.NeedContact);
			}
			else
			{
				trackingTaskStatus = ParaQueryUtil.getTrackingTaskStatusByExactName(session,
						StatusName.srcFeedback);
			}
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

	public static ArrayList<TableInfoDTO> getShowAllPDFReview(Long[] usersId, String plName,
			String vendorName, String type, String extracted, String status, Date startDate,
			Date endDate, String feedbackTypeStr)
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
				docInfo.setDevUserName(ParaQueryUtil.getGRMUser(obj.getParametricUserId())
						.getFullName());
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
			SQLQuery query = session
					.createSQLQuery("select tft.name from tracking_feedback_type tft, parts_feedback pf where pf.feedback_type=tft.id and pf.com_id="
							+ comId + " and pf.feedback_recieved=0");

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
			SQLQuery query = session
					.createSQLQuery("select count(distinct pf.ITEM_ID) from PARAMETRIC_FEEDBACK pf, part_component c,PARAMETRIC_FEEDBACK_CYCLE fbc where c.document_id="
							+ docId
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
			SQLQuery query = session
					.createSQLQuery("select count(distinct c.supplier_pl_id) from PARAMETRIC_FEEDBACK pf, part_component c,PARAMETRIC_FEEDBACK_CYCLE fbc where c.document_id="
							+ docId
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
			SQLQuery query = session
					.createSQLQuery("select pf.fb_comment from parts_feedback pf, part_component c where c.document_id="
							+ docId + " and c.com_id=pf.com_id");
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
			SQLQuery query = session
					.createSQLQuery("select FBc.FB_COMMENT from PARAMETRIC_FEEDBACK_CYCLE FBc, PARAMETRIC_FEEDBACK FB where FB.ITEM_ID="
							+ comID
							+ " and FBc.FEEDBACK_RECIEVED=0 AND FB.ID = FBC.PARA_FEEDBACK_ID");
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
			SQLQuery query = session
					.createSQLQuery("select pf.ID from PARAMETRIC_FEEDBACK pf,PARAMETRIC_FEEDBACK_CYCLE pfc where ITEM_ID in ( select com_id from part_component where document_id="
							+ docId
							+ " ) and pfc.feedback_recieved=0 and pf.ID = pfc.PARA_FEEDBACK_ID and pfc.ISSUED_BY <> "
							+ user);
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
			ParametricFeedbackCriteria.add(Restrictions.in("paraFeedbackStatus", new Object[] {
					paraFeedbackStatus.get(0), paraFeedbackStatus.get(1) }));

			List<ParametricFeedback> fb = (List<ParametricFeedback>) ParametricFeedbackCriteria
					.list();
			if(!fb.isEmpty())
			{
				user = ParaQueryUtil.getUserByUserId(
						Long.valueOf(fb.get(0).getFbInitiator().toString()), session2);
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
			ParametricFeedbackCriteria.add(Restrictions.in("paraFeedbackStatus", new Object[] {
					paraFeedbackStatus.get(0), paraFeedbackStatus.get(1) }));

			List<ParametricFeedback> fb = (List<ParametricFeedback>) ParametricFeedbackCriteria
					.list();
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
			// "and C.PART_NUMBER='" + partNumber + "' and C.SUPPLIER_ID=GETSUPPLIERID('" + supName
			// + "')");
			String sql = "";
			sql = " SELECT FB_COMMENT, u.full_name, U.GROUP_ID, FB.ITEM_ID, FB.FEEDBACK_TYPE , s.";
			sql = sql
					+ "name status_name,fbs.FEEDBACK_STATUS,GETISSUETYPE(FB.ISSUE_TYPE) FROM PARAMETRIC_FEEDBACK_CYCLE FBc, PARAMETRIC_FEEDBACK FB, p";
			sql = sql
					+ "art_component c, grm.grm_user u, TRACKING_PARAMETRIC t ,tracking_task_status s,PARA_FEEDBACK_STATUS fbs";
			sql = sql
					+ " WHERE c.com_id = FB.ITEM_ID AND FBC.ISSUED_BY = u.id AND FBC.FEEDBACK_RECIEVE";
			sql = sql + "D = 0 AND C.PART_NUMBER = '" + partNumber
					+ "' AND C.SUPPLIER_ID = GETSUPPLIERID";
			sql = sql + " ('" + supName
					+ "') AND FB.ID = FBC.PARA_FEEDBACK_ID AND T.TRACKING_TASK_STATUS_ID = S.ID ";
			sql = sql + "AND T.DOCUMENT_ID = FB.DOCUMENT_ID and fbs.ID = FBc.FEEDBACK_ACTION";
			SQLQuery query = session.createSQLQuery(sql);
			List<Object[]> list = query.list();

			String comment = "";
			if(!list.isEmpty())
			{
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
				feed.add(objArr[7].toString()); // feedback issueType by mohamed gawad
			}
			else
				feed = null;

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
			// }catch(Exception e)
			// {
			// e.printStackTrace();
		}finally
		{
			session.close();
		}
		return feedbackType;

	}

	public static boolean isNPITaskType(Long[] usersId, String plName, String vendorName,
			String type, String status, Date startDate, Date endDate, Long[] docsIds)
			throws Exception
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
				qury.append(" AND t.DOCUMENT_ID in ( " + getArrayAsCommaSeperatedList(docsIds)
						+ " )");
			}
			if(startDate != null && endDate != null)
			{
				endDate.setDate(endDate.getDate() + 1);
				SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
				System.out.println(formatter.format(startDate) + "**************"
						+ formatter.format(endDate));

				String dateRangeCond = " AND t.MODIFICATION_DATE BETWEEN TO_DATE ('"
						+ formatter.format(startDate) + "','DD/MM/RRRR')AND  TO_DATE ('"
						+ formatter.format(endDate) + "','DD/MM/RRRR')";
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

	public static PartComponent getComponentByPartNumAndSupplier(String partNumber,
			Supplier supplier) throws Exception
	{
		Session session = SessionUtil.getSession();
		try
		{
			System.out.println(supplier.getName());
			PartComponent component = getComponentByPartNumberAndSupplierName(partNumber,
					supplier.getName(), session);
			return (PartComponent) CloneUtil.cloneObject(component, new ArrayList<String>());
		}catch(Exception ex)
		{
			throw ParametricDevServerUtil.getCatchException(ex);
		}finally
		{
			session.close();
		}
	}

	public static PartComponent getComponentByPartNumberAndSupplierName(String partnumber,
			String suppliername) throws Exception
	{
		Session session = SessionUtil.getSession();
		try
		{
			Criteria crit = session.createCriteria(PartComponent.class);
			crit.add(Restrictions.eq("partNumber", partnumber));
			crit.createCriteria("supplierPl").createCriteria("supplier")
					.add(Restrictions.eq("name", suppliername));
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

	public static PartComponent getComponentBycomid(Long comid, Session session) throws Exception
	{
		try
		{
			Criteria crit = session.createCriteria(PartComponent.class);
			crit.add(Restrictions.eq("comId", comid));
			PartComponent component = (PartComponent) crit.uniqueResult();
			return component;
		}catch(Exception ex)
		{
			throw ParametricDevServerUtil.getCatchException(ex);
		}
	}

	public static PartComponent getComponentByPartNumberAndSupplierName(String partnumber,
			String suppliername, Session session)
	{
		if(session == null)
		{
			session = SessionUtil.getSession();
		}
		String sql = "select  /*+INDEX (com PRT_MAN_IDX)*/ COM_ID from PART_COMPONENT com where PART_NUMBER = '"
				+ partnumber
				+ "' and SUPPLIER_ID = (select /*+INDEX (s SUPPLIER_U01)*/ id from supplier s where NAME = '"
				+ suppliername + "') ";
		SQLQuery sqlqry = session.createSQLQuery(sql);
		BigDecimal comid = (BigDecimal) sqlqry.uniqueResult();
		long id = comid == null ? 1L : comid.longValue();
		final Criteria crit = session.createCriteria(PartComponent.class);
		crit.add(Restrictions.eq("comId", id));
		PartComponent component = (PartComponent) crit.uniqueResult();
		return component;
	}

	public static PartComponent getComponentByPartAndSupplierPl(String partnumber,
			SupplierPl supplierPl)
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

	public static TrackingParametric getTrackingParametricByDocumentAndPl(Document document, Pl pl,
			Session session)
	{
		TrackingParametric trackingParametric = null;

		try
		{
			// session = SessionUtil.getSession();
			System.err.println(document.getId());
			System.err.println(pl.getId());
			Criteria crit = session.createCriteria(TrackingParametric.class);
			crit.add(Restrictions.eq("document", document));
			crit.add(Restrictions.eq("pl", pl));
			trackingParametric = (TrackingParametric) crit.uniqueResult();
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return trackingParametric;
	}

	public static Family insertFamily(String familyStr, Session session)
	{
		Family family = new Family();
		family.setId(QueryUtil.getRandomID());
		family.setName(familyStr);
		family.setAutoFlag(1L);
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
		generic.setAutoFlag(1L);
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
		familyCross.setAutoFlag(1L);
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
		try
		{			
		npiPart.setPartComponent(com);
		npiPart.setSupplier(com.getSupplierId());
		npiPart.setPl(com.getSupplierPl().getPl());
		npiPart.setOfflinedocid(com.getDocument());
		npiPart.setNewsdocid(ParaQueryUtil.getDocumentBySeUrl(seUrl, session));
		npiPart.setInsertionDate(new Date());
		npiPart.setAutoFlag(1L);
		session.saveOrUpdate(npiPart);
		session.beginTransaction().commit();
	
		}catch(ConstraintViolationException e)
		{
			e.printStackTrace();
//			session.cancelQuery();
			session.clear();
			session.flush();			
			if(e.getMessage().contains("NPI_PARTS_COM_UQ"))
			{
				System.out.println("Found in NPI before");
			}
			return npiPart;
		}
		return npiPart;
	}

	public static MasterPartMask insertMask(String maskStr, Session session)
	{
		String maskMaster = getNonAlphaMask(maskStr).replaceAll("_", "%")
				.replaceAll("(%){2,}", "%");
		if(!maskMaster.contains("%"))
		{
			maskMaster = maskMaster + "%";
		}
		MasterPartMask mask = new MasterPartMask();
		// mask.setId(QueryUtil.getRandomID());

		mask.setMstrPart(maskMaster);
		mask.setStoreDate(new Date());
		mask.setAutoFlag(1L);
		session.saveOrUpdate(mask);
		// session.beginTransaction().commit();
		PartMaskValueId mskValId = new PartMaskValueId();
		mskValId.setMaskId(mask.getId());
		mskValId.setMaskPn(maskStr);

		PartMaskValue maskval = new PartMaskValue();
		maskval.setId(mskValId);
		maskval.setMasterPartMask(mask);
		maskval.setAutoFlag(1L);
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
			SQLQuery query = session
					.createSQLQuery("select cm.NONALPHANUM_MASK(:nanpartnum) from dual");
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
		// MasterPartMask mask = null;
		Long mask = null;
		BigDecimal id = null;
		MasterPartMask maskObj = null;
		try
		{
			String maskMaster = maskValue.replaceAll("_", "%").replaceAll("(%){2,}", "%");
			if(!maskMaster.contains("%"))
			{
				maskMaster = maskMaster + "%";
			}
			// Query q = session.createQuery("select o from MasterPartMask o  where o.mstrPart=:man");
			// q.setParameter("man", maskMaster);
			// mask = (MasterPartMask) q.uniqueResult();
			id = (BigDecimal) session.createSQLQuery(
					"SELECT id FROM Master_Part_Mask WHERE MSTR_PART = '" + maskMaster + "'")
					.uniqueResult();
			// .addEntity(MasterPartMask.class).setParameter("mstrPart", maskMaster)
			// .uniqueResult();
			// Criteria cri = session.createCriteria(MasterPartMask.class);
			// cri.add(Restrictions.eq("mstrPart", maskMaster));
			// mask = (MasterPartMask) cri.uniqueResult();
			if(id == null)
				return null;
			maskObj = new MasterPartMask(id.longValue());
			SQLQuery q = session
					.createSQLQuery("select /*+ INDEX(x PART_MASK_PN_ID_IDX) */ x.mask_id from PART_MASK_VALUE x where x.MASK_PN='"
							+ maskValue + "' and x.mask_id =" + id);
			// q.setParameter("val", maskValue);
			// q.setParameter("mskid", mask.getId());
			// q.list();k

			if(q.list().isEmpty())
			{
				PartMaskValueId mskValId = new PartMaskValueId();
				mskValId.setMaskId(id.longValue());
				mskValId.setMaskPn(maskValue);
				PartMaskValue maskval = new PartMaskValue();
				maskval.setId(mskValId);
				maskval.setMasterPartMask(maskObj);
				session.saveOrUpdate(maskval);
			}

			// Criteria cr = session.createCriteria(MasterPartMask.class);
			// cr.add(Restrictions.eq("mstrPart", famName));
			// mask = (MasterPartMask) cr.uniqueResult();
			// }catch(Exception e)
			// {
			// e.printStackTrace();
		}finally
		{
			session.close();
		}
		return maskObj;
	}

	private static TrackingParametric getTrackingParametricByPdfUrlAndSupName(String pdfUrl,
			String plName, String supplierName, Session session)
	{
		Criteria criteria = session.createCriteria(TrackingParametric.class);
		try
		{
			Document document = ParaQueryUtil.getDocumentBySeUrl(pdfUrl, session);
			criteria.add(Restrictions.eq("document", document));
			criteria.add(Restrictions.eq("pl", ParaQueryUtil.getPlByPlName(session, plName)));
			if(supplierName != null)
			{
				criteria.add(Restrictions.eq("supplier",
						ParaQueryUtil.getSupplierByExactName(session, supplierName)));
			}
		}catch(Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return (TrackingParametric) criteria.uniqueResult();
	}

	private static PartComponent getComponentByPartNumberAndSupplier(String partnumber,
			String supplierName) throws Exception
	{
		Session session = SessionUtil.getSession();
		try
		{

			System.out.println("part number = " + partnumber + "suppliername =   " + supplierName);
			PartComponent component = getComponentByPartNumberAndSupplierName(partnumber,
					supplierName, session);
			String newDiscription = ParaQueryUtil.getNewDiscription(component.getSupplierPl()
					.getPl().getId(), component.getComId(), session);
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
			SQLQuery query = session
					.createSQLQuery("SELECT   u.full_name, g.name  FROM   parts_feedback pf, grm.grm_user u, grm.grm_group g WHERE  com_id =28862724  AND pf.issued_by_id = u.id  AND u.GROUP_ID = g.id AND pf.feedback_recieved = 0");
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
	public static String getLastFeedbackCommentByComIdAndSenderGroup(long comID,
			String senderGroup, Long recieverId, Pl pl)
	{
		Session session = null;
		session = SessionUtil.getSession();
		String comment = "";
		try
		{
			if(senderGroup.equals("QUALITY"))
			{
				ParametricFeedbackCycle appFB = null;
				Long qaUserId = ParaQueryUtil.getQAUserId(pl,
						ParaQueryUtil.getTrackingTaskTypeByName("NPI", session));
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

	public static ArrayList<Object[]> getQAFeedBackFilterData(GrmUserDTO grmUser, Date startdate,
			Date enddate)
	{
		Long UserID = grmUser.getId();
		ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
		ArrayList<String> row = null;
		ArrayList<Object[]> list2 = null;
		Session session = SessionUtil.getSession();
		String start = "";
		String end = "";
		String sql = "";
		try
		{
			if(startdate != null)
			{
				start = new SimpleDateFormat("MM/dd/yyyy").format(startdate);

			}
			if(enddate != null)
			{
				end = new SimpleDateFormat("MM/dd/yyyy").format(enddate);
			}

			if(startdate == null && enddate == null)
			{
				sql = "  SELECT   DISTINCT p.name pl, s.name supplier, ttt.name TYPE, U.FULL_NAME user_Name,Get_PL_Type(P.ID ) "
						+ "  FROM   Tracking_Parametric tp, pl p, supplier s, tracking_task_type ttt, grm.GRM_USER u, TRACKING_TASK_STATUS st  "
						+ " WHERE  tp.pl_id = p.id   AND tp.tracking_task_type_id IN (0, 1, 4, 12, 14)     "
						+ "      AND tp.TRACKING_TASK_STATUS_ID = getTaskstatusId('"
						+ StatusName.qaFeedback
						+ "')  "
						+ "         AND tp.supplier_id = s.id  "
						+ "         AND tp.tracking_task_type_id = ttt.id   "
						+ "        AND u.id = tp.user_id     "
						+ "      AND st.id = tp.TRACKING_TASK_STATUS_ID  "
						+ "         and QA_USER_ID="
						+ grmUser.getId()
						+ " GROUP BY p.name, s.name, ttt.name, U.FULL_NAME, st.NAME,P.ID";
			}
			else
			{
				sql = "  SELECT   DISTINCT p.name pl, s.name supplier, ttt.name TYPE, U.FULL_NAME user_Name,Get_PL_Type(P.ID ) "
						+ "  FROM   Tracking_Parametric tp, pl p, supplier s, tracking_task_type ttt, grm.GRM_USER u, TRACKING_TASK_STATUS st  "
						+ " WHERE  tp.pl_id = p.id   AND tp.tracking_task_type_id IN (0, 1, 4, 12, 14)     "
						+ "      AND tp.TRACKING_TASK_STATUS_ID = getTaskstatusId('"
						+ StatusName.qaFeedback
						+ "')  "
						+ "         AND tp.supplier_id = s.id  "
						+ "         AND tp.tracking_task_type_id = ttt.id   "
						+ "        AND u.id = tp.user_id     "
						+ "      AND st.id = tp.TRACKING_TASK_STATUS_ID  "
						+ "         and QA_USER_ID=" + grmUser.getId();
				sql = sql
						+ " AND TP.MODIFICATION_DATE BETWEEN TO_DATE('"
						+ start
						+ "', 'MM/DD/YYYY') AND TO_DATE('"
						+ end
						+ "', 'MM/DD/YYYY')  GROUP BY p.name, s.name, ttt.name, U.FULL_NAME, st.NAME,P.ID";
			}
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

	public static ArrayList<Object[]> getQAexceptionFilterData(GrmUserDTO grmUser, String screen)
	{
		Long UserID = grmUser.getId();
		ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
		ArrayList<String> row = null;
		ArrayList<Object[]> list2 = null;
		ArrayList<Object[]> list = null;
		String column = "";
		String status = "";
		if(screen.equals("Qa"))
		{
			column = "QA_USER_ID";
			status = StatusName.WaittingException;
		}
		else
		{
			column = "USER_ID";
			status = StatusName.RejectException;
		}
		Session session = SessionUtil.getSession();
		try
		{

			String Sql = "";

			Sql = " SELECT DISTINCT p.name pl, s.name supplier, chks.NAME chktype FROM Tracking_P";
			Sql = Sql + "arametric tp, pl p, supplier s, TRACKING_TASK_STATUS st, QA_CH";
			Sql = Sql
					+ "ECKS_ACTIONS chkac, QA_CHECK_PARTS chp, PRE_QA_CHECKERS chks, QA_CHECK_MULTI_T";
			Sql = Sql
					+ "AX chktax WHERE tp.pl_id = p.id AND tp.TRACKING_TASK_STATUS_ID = getTaskstatus";
			Sql = Sql + "Id('" + StatusName.qachecking
					+ "') AND tp.supplier_id = s.id  AND st.id = ";
			Sql = Sql
					+ "tp.TRACKING_TASK_STATUS_ID AND chp.CHECK_ID = chks.ID AND chp.DOCUMENT_ID = tp";
			Sql = Sql + ".DOCUMENT_ID AND chkac.ID = chktax.ACTION_ID AND chkac.NAME = '" + status
					+ "'";
			Sql = Sql + " AND chktax.CHECK_PART_ID = chp.ID AND tp." + column + " ="
					+ grmUser.getId() + " GROUP BY s.name, P.name, chks.NAME";

			list2 = (ArrayList<Object[]>) session.createSQLQuery(Sql).list();
			// for(int i = 0; i < list2.size(); i++)
			// {
			// Object[] data = list2.get(i);
			// row = new ArrayList<String>();
			// for(int j = 0; j < 4; j++)
			// {
			// row.add((data[j] == null) ? "" : data[j].toString());
			// }
			//
			// result.add(row);
			// }
			Sql = " SELECT DISTINCT p.name pl, s.name supplier, chks.NAME chktype FROM Tracking_P";
			Sql = Sql + "arametric tp, pl p, supplier s, TRACKING_TASK_STATUS st, QA_CH";
			Sql = Sql
					+ "ECKS_ACTIONS chkac, QA_CHECK_PARTS chp, PRE_QA_CHECKERS chks, QA_CHECK_MULTI_D";
			Sql = Sql
					+ "ATA chkdata WHERE tp.pl_id = p.id AND tp.TRACKING_TASK_STATUS_ID = getTaskstat";
			Sql = Sql + "usId('" + StatusName.qachecking
					+ "') AND tp.supplier_id = s.id  AND st.id ";
			Sql = Sql
					+ "= tp.TRACKING_TASK_STATUS_ID AND chp.CHECK_ID = chks.ID AND chp.DOCUMENT_ID = ";
			Sql = Sql + "tp.DOCUMENT_ID AND chkac.ID = chkdata.ACTION AND chkac.NAME = '" + status
					+ "'";
			Sql = Sql + " AND chkdata.CHECK_PART_ID = chp.ID AND tp." + column + " ="
					+ grmUser.getId() + " GROUP BY s.name, P.name, chks.NAME";
			Sql = Sql + "";
			list = (ArrayList<Object[]>) session.createSQLQuery(Sql).list();
			for(int i = 0; i < list.size(); i++)
			{
				Object[] data = list.get(i);
				list2.add(data);
			}
		}finally
		{
			session.close();
		}
		return list2;

	}

	public static ArrayList<Object[]> getexceptionFBFilterData(GrmUserDTO grmUser)
	{
		Long UserID = grmUser.getId();
		ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
		ArrayList<String> row = null;
		ArrayList<Object[]> list2 = null;
		ArrayList<Object[]> list = null;
		Session session = SessionUtil.getSession();
		try
		{

			String Sql = "";

			Sql = " SELECT DISTINCT p.name pl, s.name supplier, chks.NAME chktype FROM Tracking_P";
			Sql = Sql + "arametric tp, pl p, supplier s, TRACKING_TASK_STATUS st, QA_CH";
			Sql = Sql
					+ "ECKS_ACTIONS chkac, QA_CHECK_PARTS chp, PRE_QA_CHECKERS chks, QA_CHECK_MULTI_T";
			Sql = Sql
					+ "AX chktax WHERE tp.pl_id = p.id AND tp.TRACKING_TASK_STATUS_ID = getTaskstatus";
			Sql = Sql + "Id('" + StatusName.qachecking
					+ "') AND tp.supplier_id = s.id  AND st.id = ";
			Sql = Sql
					+ "tp.TRACKING_TASK_STATUS_ID AND chp.CHECK_ID = chks.ID AND chp.DOCUMENT_ID = tp";
			Sql = Sql + ".DOCUMENT_ID AND chkac.ID = chktax.ACTION_ID AND chkac.NAME = '"
					+ StatusName.RejectException + "'";
			Sql = Sql + " AND chktax.CHECK_PART_ID = chp.ID AND tp.USER_ID =" + grmUser.getId()
					+ " GROUP BY s.name, P.name, chks.NAME";

			list2 = (ArrayList<Object[]>) session.createSQLQuery(Sql).list();
			// for(int i = 0; i < list2.size(); i++)
			// {
			// Object[] data = list2.get(i);
			// row = new ArrayList<String>();
			// for(int j = 0; j < 4; j++)
			// {
			// row.add((data[j] == null) ? "" : data[j].toString());
			// }
			//
			// result.add(row);
			// }
			Sql = " SELECT DISTINCT p.name pl, s.name supplier, chks.NAME chktype FROM Tracking_P";
			Sql = Sql + "arametric tp, pl p, supplier s, TRACKING_TASK_STATUS st, QA_CH";
			Sql = Sql
					+ "ECKS_ACTIONS chkac, QA_CHECK_PARTS chp, PRE_QA_CHECKERS chks, QA_CHECK_MULTI_D";
			Sql = Sql
					+ "ATA chkdata WHERE tp.pl_id = p.id AND tp.TRACKING_TASK_STATUS_ID = getTaskstat";
			Sql = Sql + "usId('" + StatusName.qachecking
					+ "') AND tp.supplier_id = s.id  AND st.id ";
			Sql = Sql
					+ "= tp.TRACKING_TASK_STATUS_ID AND chp.CHECK_ID = chks.ID AND chp.DOCUMENT_ID = ";
			Sql = Sql + "tp.DOCUMENT_ID AND chkac.ID = chkdata.ACTION AND chkac.NAME = '"
					+ StatusName.RejectException + "'";
			Sql = Sql + " AND chkdata.CHECK_PART_ID = chp.ID AND tp.USER_ID =" + grmUser.getId()
					+ " GROUP BY s.name, P.name, chks.NAME";
			Sql = Sql + "";
			list = (ArrayList<Object[]>) session.createSQLQuery(Sql).list();
			for(int i = 0; i < list.size(); i++)
			{
				Object[] data = list.get(i);
				list2.add(data);
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
		ArrayList<Object[]> list = null;
		Session session = SessionUtil.getSession();
		try
		{

			String Sql = "";
			Sql = " SELECT DISTINCT p.name pl, s.name supplier, chks.NAME chktype, chkac.NAME sta";
			Sql = Sql
					+ "tus FROM Tracking_Parametric tp, pl p, supplier s, grm.GRM_USER u, TRACKING_TA";
			Sql = Sql
					+ "SK_STATUS st, QA_CHECKS_ACTIONS chkac, QA_CHECK_PARTS chp, PRE_QA_CHECKERS chk";
			Sql = Sql
					+ "s, QA_CHECK_MULTI_TAX chktax WHERE tp.pl_id = p.id AND tp.TRACKING_TASK_STATUS";
			Sql = Sql + "_ID = getTaskstatusId('" + StatusName.qachecking
					+ "') AND tp.supplier_id = s.id AND u.id = tp.u";
			Sql = Sql + "ser_id AND st.id = tp.TRACKING_TASK_STATUS_ID AND chp.CHECK_ID = chks.ID ";
			Sql = Sql + "AND chp.DOCUMENT_ID = tp.DOCUMENT_ID AND chkac.ID = chktax.ACTION_ID ";
			Sql = Sql + "AND chktax.CHECK_PART_ID = chp.ID AND tp.USER_ID =" + grmUser.getId() + ""
					+ " GROUP BY s.name, P.name, chks.NAME, chkac.NAME";

			list2 = (ArrayList<Object[]>) session.createSQLQuery(Sql).list();
			// for(int i = 0; i < list2.size(); i++)
			// {
			// Object[] data = list2.get(i);
			// row = new ArrayList<String>();
			// for(int j = 0; j < 4; j++)
			// {
			// row.add((data[j] == null) ? "" : data[j].toString());
			// }
			//
			// result.add(row);
			// }
			Sql = " SELECT DISTINCT p.name pl, s.name supplier, chks.NAME chktype, chkac.NAME sta";
			Sql = Sql
					+ "tus FROM Tracking_Parametric tp, pl p, supplier s, grm.GRM_USER u, TRACKING_TA";
			Sql = Sql
					+ "SK_STATUS st, QA_CHECKS_ACTIONS chkac, QA_CHECK_PARTS chp, PRE_QA_CHECKERS chk";
			Sql = Sql
					+ "s, QA_CHECK_MULTI_DATA chkdata WHERE tp.pl_id = p.id AND tp.TRACKING_TASK_STAT";
			Sql = Sql + "US_ID = getTaskstatusId('" + StatusName.qachecking
					+ "') AND tp.supplier_id = s.id AND u.id = tp";
			Sql = Sql
					+ ".user_id AND st.id = tp.TRACKING_TASK_STATUS_ID AND chp.CHECK_ID = chks.ID AND";
			Sql = Sql + " chp.DOCUMENT_ID = tp.DOCUMENT_ID AND chkac.ID = chkdata.ACTION ";
			Sql = Sql + "AND chkdata.CHECK_PART_ID = chp.ID AND tp.USER_ID =" + grmUser.getId()
					+ "" + " GROUP BY s.name, P.name, chks.NAME, chkac.NAME";
			list = (ArrayList<Object[]>) session.createSQLQuery(Sql).list();
			for(int i = 0; i < list.size(); i++)
			{
				Object[] data = list.get(i);
				list2.add(data);
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
		TrackingTaskStatus status2 = null;
		try
		{
			session = SessionUtil.getSession();
			Criteria cri = session.createCriteria(TrackingTaskStatus.class);
			cri.add(Restrictions.eq("name", StatusName.waitingsummary));
			status = (TrackingTaskStatus) cri.uniqueResult();

			cri = session.createCriteria(TrackingTaskStatus.class);
			cri.add(Restrictions.eq("name", StatusName.qaReview));
			status2 = (TrackingTaskStatus) cri.uniqueResult();

			cri = session.createCriteria(TrackingParametric.class);
			cri.add(Restrictions.eq("document", doc));
			cri.add(Restrictions.eq("trackingTaskStatus", status2));
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

	public static ArrayList<ArrayList<String>> getsummarydata(GrmUserDTO userDTO)
	{
		Session session = SessionUtil.getSession();
		// ArrayList<SummaryDTO> alldata = new ArrayList<>();
		ArrayList<ArrayList<String>> allsummary = new ArrayList<>();
		SQLQuery query = null;
		try
		{
			StringBuffer qury = new StringBuffer();
			String Sql = "";
			Sql = " SELECT  /*+ INDEX(T DOC_SUP_TASK_QAUSER_IDX) */ GETPDFURLbydoc (T.DOCUMENT_ID) pdfurl, getonlinelink_non_pdf (T.DOCUME";
			Sql = Sql
					+ "NT_ID) onlinelink, Get_PL_Type (t.pl_id) pltype, GET_PL_NAME (t.PL_ID) plName,";
			Sql = Sql
					+ " C.COM_ID, C.PART_NUMBER, GETSUPPLIERNAME (t.supplier_id) supName, GetTaskType";
			Sql = Sql
					+ "Name (t.TRACKING_TASK_TYPE_ID) task_type, getuserName (T.USER_ID) username, t.";
			Sql = Sql
					+ "QA_REVIEW_DATE, C.QAFLAG, DECODE (C.DONEFLAG, NULL, 'No', 0, 'No', 1, 'Yes') DO";
			Sql = Sql
					+ "NEFLAG, DECODE (C.EXTRACTIONFLAG, NULL, 'No', 0, 'No', 1, 'Yes') EXTRACTIONFLAG,T.DOCUMENT_ID,t.pl_id,"
					+ "DECODE (t.CONFIDENTIAL_STATUS, NULL, ' ', 0, 'NotConfidential', 1, 'Confidential',2, 'Cant Read') ConfidentialStatus ";
			Sql = Sql
					+ "FROM TRACKING_PARAMETRIC T, Part_COMPONENT c WHERE t.DOCUMENT_ID = c.DOCUMEN";
			Sql = Sql + "T_ID AND T.SUPPLIER_PL_ID = C.SUPPLIER_PL_ID AND T.QA_USER_ID = "
					+ userDTO.getId() + " AND T.TRACK";
			Sql = Sql + "ING_TASK_STATUS_ID = " + StatusName.waitingsummaryId + "";
			qury.append(Sql);
			// pdfurl_0 onlinelink_1 pltype_2 plName_3 COM_ID_4 PART_NUMBER_5
			// supName_6 task_type_7 username_8 DATE_9 QAFLAG_10 DONEFLAG_11 EXTRACTIONFLAG_12

			// if(startDate != null && endDate != null)
			// {
			// SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
			// System.out.println(formatter.format(startDate) + "**************"
			// + formatter.format(endDate));
			// String dateRangeCond = " AND t.ASSIGNED_DATE BETWEEN TO_DATE ('"
			// + formatter.format(startDate) + "','DD/MM/RRRR')AND  TO_DATE ('"
			// + formatter.format(endDate) + "','DD/MM/RRRR')";
			// qury.append(dateRangeCond);
			//
			// }
			System.out.println(qury.toString());
			Long[] users = ParaQueryUtil.getusersbyqualityandstatus(userDTO,
					StatusName.waitingsummary);
			ArrayList<Object[]> result = (ArrayList<Object[]>) session.createSQLQuery(
					qury.toString()).list();
			List<Integer> noparts = new ArrayList<>();
			// String keyword = "";
			for(int i = 0; i < result.size(); i++)
			{
				Object[] data = result.get(i);
				String pdf = "";

				ArrayList<String> summary = new ArrayList<String>();
				summary.add(data[0] == null ? "" : data[0].toString());// pdfurl_0
				summary.add(data[1] == null ? "" : data[1].toString());// onlinelink_1
				summary.add(data[2] == null ? "" : data[2].toString());// pltype_2
				summary.add(data[3] == null ? "" : data[3].toString());// plName_3

				if(i == 0)
				{
					noparts = getnoPartsPerPDFandPL(Long.valueOf(data[13].toString()),
							Long.valueOf(data[14].toString()), users, StatusName.waitingsummary);
					// keyword = getConfidentialStatus(Long.valueOf(data[13].toString()),
					// Long.valueOf(data[14].toString()));
					pdf = data[0] == null ? "" : data[0].toString();
				}
				else
				{
					if(!pdf.equals(data[0] == null ? "" : data[0].toString()))
					{
						noparts = getnoPartsPerPDFandPL(Long.valueOf(data[13].toString()),
								Long.valueOf(data[14].toString()), users, StatusName.waitingsummary);
						// keyword = getConfidentialStatus(Long.valueOf(data[13].toString()),
						// Long.valueOf(data[14].toString()));
						pdf = data[0] == null ? "" : data[0].toString();
					}
				}
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
				String comment = getfbcommentbycomidanduser(Long.valueOf(data[4].toString()),
						userDTO.getId());
				summary.add(comment);// QAcomment_16
				summary.add(data[11] == null ? "" : data[11].toString());// DONEFLAG_17
				summary.add(data[12] == null ? "" : data[12].toString());// EXTRACTIONFLAG_18

				// getConfidentialStatus(Long.valueOf(data[13].toString()),
				// Long.valueOf(data[14].toString()));
				summary.add(data[15] == null ? "" : data[15].toString());// ConfidentialStatus_19
				allsummary.add(summary);
			}

		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return allsummary;
	}

	private static String getConfidentialStatus(long pdfid, long plid)
	{
		Session session = null;
		String result = "";
		try
		{
			session = SessionUtil.getSession();
			String sqlstatment = "select DECODE (t.CONFIDENTIAL_STATUS, NULL, ' ', 0, 'NotConfidential', 1, 'Confidential',2, 'Cant Read') ConfidentialStatus from TRACKING_PARAMETRIC t"
					+ " where DOCUMENT_ID = " + pdfid + " and PL_ID = " + plid + "";
			SQLQuery sql = session.createSQLQuery(sqlstatment);
			result = (String) sql.uniqueResult();
		}catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}finally
		{
			session.close();
		}
		return result;

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
				cri.add(Restrictions.eq("paraFeedbackId",
						parametricfeedbackcycle.getParametricFeedback()));
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

	public static ArrayList<QAChecksDTO> getQAexceptionData(String plName, String supplierName,
			String checkerType, Date startDate, Date endDate, long userid, String screen,
			Session session)
	{
		ArrayList<QAChecksDTO> data = null;

		try
		{
			String column = "";
			String status = "";
			if(screen.equals("Qa"))
			{
				column = "QA_USER_ID";
				status = StatusName.WaittingException;
			}
			else
			{
				column = "USER_ID";
				status = StatusName.RejectException;
			}
			StringBuffer qury = new StringBuffer();
			if(checkerType.equals(StatusName.NonAlphaMultiSupplier)
					|| checkerType.equals(StatusName.MaskMultiSupplier)
					|| checkerType.equals(StatusName.FamilyMultiSupplier))
			{
				String Sql = "";
				Sql = " SELECT  CM.NONALPHANUM(com.PART_NUMBER) nunalpha , com.COM_ID comid, tp";
				Sql = Sql
						+ ".DOCUMENT_ID , p.ID pl_id, p.NAME pl_name,chktax.CHECK_PART_ID FROM Tracking_Parametric tp, pl p, T";
				Sql = Sql
						+ "RACKING_TASK_STATUS st, QA_CHECKS_ACTIONS chkac, QA_CHECK_PARTS chp, PRE_QA_CH";
				Sql = Sql
						+ "ECKERS chks, QA_CHECK_MULTI_TAX chktax, part_component com WHERE tp.pl_id = p.";
				Sql = Sql + "id AND tp.TRACKING_TASK_STATUS_ID = getTaskstatusId('"
						+ StatusName.qachecking + "') AND st.id =";
				Sql = Sql
						+ " tp.TRACKING_TASK_STATUS_ID AND chp.CHECK_ID = chks.ID AND chp.DOCUMENT_ID = t";
				Sql = Sql
						+ "p.DOCUMENT_ID AND chkac.ID = chktax.ACTION_ID AND chktax.CHECK_PART_ID = chp.ID ";
				Sql = Sql + "AND com.COM_ID = chktax.CONFLICTED_PART AND tp." + column + " ="
						+ userid + "";
				Sql = Sql + " AND chks.NAME = '" + checkerType + "' AND chkac.NAME = '" + status
						+ "'";
				// Sql = Sql + "GROUP BY com.COM_ID,tp.DOCUMENT_ID,p.ID,p.NAME,chktax.CONFLICTED_PART";
				qury.append(Sql);
			}
			else if(checkerType.equals(StatusName.MaskMultiData)
					|| checkerType.equals(StatusName.RootPartChecker))
			{
				String Sql = "";
				Sql = " SELECT  CM.NONALPHANUM(com.PART_NUMBER) nunalpha , com.COM_ID comid, tp";
				Sql = Sql
						+ ".DOCUMENT_ID , p.ID pl_id, p.NAME pl_name,chktax.PL_FET_ID,chktax.FET_VAL,chktax.CHECK_PART_ID FROM Tracking_Parametric tp, pl p, T";
				Sql = Sql
						+ "RACKING_TASK_STATUS st, QA_CHECKS_ACTIONS chkac, QA_CHECK_PARTS chp, PRE_QA_CH";
				Sql = Sql
						+ "ECKERS chks, QA_CHECK_MULTI_DATA chktax, part_component com WHERE tp.pl_id = p.";
				Sql = Sql + "id AND tp.TRACKING_TASK_STATUS_ID = getTaskstatusId('"
						+ StatusName.qachecking + "') AND st.id =";
				Sql = Sql
						+ " tp.TRACKING_TASK_STATUS_ID AND chp.CHECK_ID = chks.ID AND chp.DOCUMENT_ID = t";
				Sql = Sql
						+ "p.DOCUMENT_ID AND chkac.ID = chktax.ACTION AND chktax.CHECK_PART_ID = chp.ID ";
				Sql = Sql + "AND com.COM_ID = chktax.CONFLICTED_PART AND tp." + column + " ="
						+ userid + "";
				Sql = Sql + " AND chks.NAME = '" + checkerType + "' AND chkac.NAME = '" + status
						+ "'";
				// Sql = Sql + "GROUP BY com.COM_ID,tp.DOCUMENT_ID,p.ID,p.NAME,chktax.CONFLICTED_PART";
				qury.append(Sql);
			}
			else if(checkerType.equals(StatusName.generic_part))
			{
				String Sql = "";
				Sql = " SELECT  CM.NONALPHANUM(com.PART_NUMBER) nunalpha , com.COM_ID comid, tp";
				Sql = Sql
						+ ".DOCUMENT_ID , p.ID pl_id, p.NAME pl_name,chktax.PL_FET_ID,chktax.FET_VAL,chktax.CHECK_PART_ID,gen.GENERIC FROM Tracking_Parametric tp, pl p, T";
				Sql = Sql
						+ "RACKING_TASK_STATUS st, QA_CHECKS_ACTIONS chkac, QA_CHECK_PARTS chp, PRE_QA_CH";
				Sql = Sql
						+ "ECKERS chks, QA_CHECK_MULTI_DATA chktax, part_component com,MAP_GENERIC gen WHERE tp.pl_id = p.";
				Sql = Sql + "id AND tp.TRACKING_TASK_STATUS_ID = getTaskstatusId('"
						+ StatusName.qachecking + "') AND st.id =";
				Sql = Sql
						+ " tp.TRACKING_TASK_STATUS_ID AND chp.CHECK_ID = chks.ID AND chp.DOCUMENT_ID = t";
				Sql = Sql
						+ "p.DOCUMENT_ID AND chkac.ID = chktax.ACTION AND chktax.CHECK_PART_ID = chp.ID ";
				Sql = Sql + "AND com.COM_ID = chktax.CONFLICTED_PART AND tp." + column + " ="
						+ userid + "";
				Sql = Sql + " AND chks.NAME = '" + checkerType + "' AND gen.ID= com.GENERIC_ID ";
				// Sql = Sql + "GROUP BY com.COM_ID,tp.DOCUMENT_ID,p.ID,p.NAME,chktax.CONFLICTED_PART";
				qury.append(Sql);
			}
			if(startDate != null && endDate != null)
			{
				SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
				System.out.println(formatter.format(startDate) + "**************"
						+ formatter.format(endDate));
				String dateRangeCond = " AND chp.STOREDATE BETWEEN TO_DATE ('"
						+ formatter.format(startDate) + "','DD/MM/RRRR')AND  TO_DATE ('"
						+ formatter.format(endDate) + "','DD/MM/RRRR')";
				qury.append(dateRangeCond);

			}
			if(!plName.isEmpty() && !plName.equals("All"))
			{
				Pl pl = ParaQueryUtil.getPlByPlName(session, plName);
				String plcriteria = " AND tp.pl_id = " + pl.getId() + " ";
				qury.append(plcriteria);
			}
			if(!supplierName.isEmpty() && !supplierName.equals("All"))
			{
				Supplier supplier = ParaQueryUtil.getSupplierByExactName(session, supplierName);
				String suppliercri = " AND tp.supplier_id = " + supplier.getId() + " ";
				qury.append(suppliercri);
			}
			if(checkerType.equals(StatusName.NonAlphaMultiSupplier)
					|| checkerType.equals(StatusName.MaskMultiSupplier)
					|| checkerType.equals(StatusName.FamilyMultiSupplier))
			{
				qury.append(" GROUP BY chktax.CHECK_PART_ID,com.COM_ID,tp.DOCUMENT_ID,p.ID,p.NAME,com.PART_NUMBER order by chktax.CHECK_PART_ID");
			}
			else if(checkerType.equals(StatusName.MaskMultiData)
					|| checkerType.equals(StatusName.RootPartChecker))
			{
				qury.append(" GROUP BY chktax.CHECK_PART_ID,com.COM_ID,tp.DOCUMENT_ID,p.ID,p.NAME,com.PART_NUMBER,chktax.PL_FET_ID,chktax.FET_VAL order by chktax.CHECK_PART_ID");
			}
			else if(checkerType.equals(StatusName.generic_part))
			{
				qury.append(" GROUP BY chktax.CHECK_PART_ID,chktax.ID,com.COM_ID,tp.DOCUMENT_ID,p.ID,p.NAME,com.PART_NUMBER,chktax.PL_FET_ID,chktax.FET_VAL,gen.GENERIC  order by chktax.CHECK_PART_ID");
			}
			System.out.println(qury.toString());
			SQLQuery query = session.createSQLQuery(qury.toString());
			ArrayList<Object[]> result = (ArrayList<Object[]>) query.list();
			// nunalpha 0 ,comid 1 ,DOCUMENT_ID 2,pl_id 3,pl_name 4,fetid 5,fetname 6
			data = new ArrayList<>();
			for(int i = 0; i < result.size(); i++)
			{
				QAChecksDTO qachecks = new QAChecksDTO();
				qachecks.setNanAlphaPart(result.get(i)[0] == null ? "" : result.get(i)[0]
						.toString());
				PartComponent part = getComponentBycomid(
						result.get(i)[1] == null ? 0l : Long.valueOf(result.get(i)[1].toString()),
						session);
				qachecks.setPart(part);
				qachecks.setVendor(part.getSupplierId());
				qachecks.setDatasheet(part.getDocument());
				qachecks.setDatasheetTitle(part.getDocument().getTitle());
				qachecks.setMask(part.getMasterPartMask());
				qachecks.setFamily(part.getFamily());
				Pl pl = part.getSupplierPl().getPl();
				qachecks.setProductLine(pl);
				if(checkerType.equals(StatusName.MaskMultiData)
						|| checkerType.equals(StatusName.RootPartChecker))
				{
					PlFeature fet = ParaQueryUtil.getPlFeatureid(result.get(i)[5] == null ? 0l
							: Long.valueOf(result.get(i)[5].toString()), pl, session);
					qachecks.setFeatureName(fet.getFeature().getName());
					qachecks.setFeatureValue(result.get(i)[6] == null ? "" : result.get(i)[6]
							.toString());
					qachecks.setCheckpartid((result.get(i)[7] == null ? 0L : Long.valueOf(result
							.get(i)[7].toString())));
				}
				else if(checkerType.equals(StatusName.generic_part))
				{
					PlFeature fet = ParaQueryUtil.getPlFeatureid(result.get(i)[5] == null ? 0l
							: Long.valueOf(result.get(i)[5].toString()), pl, session);
					qachecks.setGeneric(result.get(i)[8] == null ? "" : result.get(i)[8].toString());
					qachecks.setFeatureName(fet.getFeature().getName());
					qachecks.setFeatureValue(result.get(i)[6] == null ? "" : result.get(i)[6]
							.toString());
					qachecks.setCheckpartid(result.get(i)[7] == null ? 0l : Long.valueOf(result
							.get(i)[7].toString()));
				}
				else
				{
					qachecks.setCheckpartid((result.get(i)[5] == null ? 0L : Long.valueOf(result
							.get(i)[5].toString())));
				}
				qachecks.setChecker(checkerType);
				data.add(qachecks);
			}

		}catch(Exception e)
		{

			e.printStackTrace();
		}finally
		{
			// session.close();
		}
		return data;
	}

	public static ArrayList<QAChecksDTO> getQAchecksData(String plName, String supplierName,
			String checkerType, String status, Date startDate, Date endDate, long userid,
			Session session)
	{
		ArrayList<QAChecksDTO> data = null;

		try
		{

			StringBuffer qury = new StringBuffer();
			if(checkerType.equals(StatusName.NonAlphaMultiSupplier)
					|| checkerType.equals(StatusName.MaskMultiSupplier)
					|| checkerType.equals(StatusName.FamilyMultiSupplier))
			{
				String Sql = "";
				Sql = " SELECT  CM.NONALPHANUM(com.PART_NUMBER) nunalpha , com.COM_ID comid, tp";
				Sql = Sql
						+ ".DOCUMENT_ID , p.ID pl_id, p.NAME pl_name,chktax.CHECK_PART_ID FROM Tracking_Parametric tp, pl p, T";
				Sql = Sql
						+ "RACKING_TASK_STATUS st, QA_CHECKS_ACTIONS chkac, QA_CHECK_PARTS chp, PRE_QA_CH";
				Sql = Sql
						+ "ECKERS chks, QA_CHECK_MULTI_TAX chktax, part_component com WHERE tp.pl_id = p.";
				Sql = Sql + "id AND tp.TRACKING_TASK_STATUS_ID = getTaskstatusId('"
						+ StatusName.qachecking + "') AND st.id =";
				Sql = Sql
						+ " tp.TRACKING_TASK_STATUS_ID AND chp.CHECK_ID = chks.ID AND chp.DOCUMENT_ID = t";
				Sql = Sql
						+ "p.DOCUMENT_ID AND chkac.ID = chktax.ACTION_ID AND chktax.CHECK_PART_ID = chp.ID ";
				Sql = Sql + "AND com.COM_ID = chktax.CONFLICTED_PART AND tp.USER_ID =" + userid
						+ "";
				Sql = Sql + " AND chks.NAME = '" + checkerType + "' ";
				// Sql = Sql + "GROUP BY com.COM_ID,tp.DOCUMENT_ID,p.ID,p.NAME,chktax.CONFLICTED_PART";
				qury.append(Sql);
			}
			else if(checkerType.equals(StatusName.MaskMultiData)
					|| checkerType.equals(StatusName.RootPartChecker))
			{
				String Sql = "";
				Sql = " SELECT  CM.NONALPHANUM(com.PART_NUMBER) nunalpha , com.COM_ID comid, tp";
				Sql = Sql
						+ ".DOCUMENT_ID , p.ID pl_id, p.NAME pl_name,chktax.PL_FET_ID,chktax.FET_VAL,chktax.CHECK_PART_ID FROM Tracking_Parametric tp, pl p, T";
				Sql = Sql
						+ "RACKING_TASK_STATUS st, QA_CHECKS_ACTIONS chkac, QA_CHECK_PARTS chp, PRE_QA_CH";
				Sql = Sql
						+ "ECKERS chks, QA_CHECK_MULTI_DATA chktax, part_component com WHERE tp.pl_id = p.";
				Sql = Sql + "id AND tp.TRACKING_TASK_STATUS_ID = getTaskstatusId('"
						+ StatusName.qachecking + "') AND st.id =";
				Sql = Sql
						+ " tp.TRACKING_TASK_STATUS_ID AND chp.CHECK_ID = chks.ID AND chp.DOCUMENT_ID = t";
				Sql = Sql
						+ "p.DOCUMENT_ID AND chkac.ID = chktax.ACTION AND chktax.CHECK_PART_ID = chp.ID ";
				Sql = Sql + "AND com.COM_ID = chktax.CONFLICTED_PART AND tp.USER_ID =" + userid
						+ "";
				Sql = Sql + " AND chks.NAME = '" + checkerType + "' ";
				// Sql = Sql + "GROUP BY com.COM_ID,tp.DOCUMENT_ID,p.ID,p.NAME,chktax.CONFLICTED_PART";
				qury.append(Sql);
			}
			else if(checkerType.equals(StatusName.generic_part))
			{
				String Sql = "";
				Sql = " SELECT  CM.NONALPHANUM(com.PART_NUMBER) nunalpha , com.COM_ID comid, tp";
				Sql = Sql
						+ ".DOCUMENT_ID , p.ID pl_id, p.NAME pl_name,chktax.PL_FET_ID,chktax.FET_VAL,chktax.CHECK_PART_ID,gen.GENERIC FROM Tracking_Parametric tp, pl p, T";
				Sql = Sql
						+ "RACKING_TASK_STATUS st, QA_CHECKS_ACTIONS chkac, QA_CHECK_PARTS chp, PRE_QA_CH";
				Sql = Sql
						+ "ECKERS chks, QA_CHECK_MULTI_DATA chktax, part_component com,MAP_GENERIC gen WHERE tp.pl_id = p.";
				Sql = Sql + "id AND tp.TRACKING_TASK_STATUS_ID = getTaskstatusId('"
						+ StatusName.qachecking + "') AND st.id =";
				Sql = Sql
						+ " tp.TRACKING_TASK_STATUS_ID AND chp.CHECK_ID = chks.ID AND chp.DOCUMENT_ID = t";
				Sql = Sql
						+ "p.DOCUMENT_ID AND chkac.ID = chktax.ACTION AND chktax.CHECK_PART_ID = chp.ID ";
				Sql = Sql + "AND com.COM_ID = chktax.CONFLICTED_PART AND tp.USER_ID =" + userid
						+ "";
				Sql = Sql + " AND chks.NAME = '" + checkerType + "' AND gen.ID= com.GENERIC_ID ";
				// Sql = Sql + "GROUP BY com.COM_ID,tp.DOCUMENT_ID,p.ID,p.NAME,chktax.CONFLICTED_PART";
				qury.append(Sql);
			}

			if(startDate != null && endDate != null)
			{
				SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
				System.out.println(formatter.format(startDate) + "**************"
						+ formatter.format(endDate));
				String dateRangeCond = " AND chp.STOREDATE BETWEEN TO_DATE ('"
						+ formatter.format(startDate) + "','DD/MM/RRRR')AND  TO_DATE ('"
						+ formatter.format(endDate) + "','DD/MM/RRRR')";
				qury.append(dateRangeCond);

			}
			if(!plName.isEmpty() && !plName.equals("All"))
			{
				Pl pl = ParaQueryUtil.getPlByPlName(session, plName);
				String plcriteria = " AND tp.pl_id = " + pl.getId() + " ";
				qury.append(plcriteria);
			}
			if(!supplierName.isEmpty() && !supplierName.equals("All"))
			{
				Supplier supplier = ParaQueryUtil.getSupplierByExactName(session, supplierName);
				String suppliercri = " AND tp.supplier_id = " + supplier.getId() + " ";
				qury.append(suppliercri);
			}
			if(!status.isEmpty() && !status.equals("All"))
			{
				String suppliercri = " AND chkac.NAME = '" + status + "' ";
				qury.append(suppliercri);

			}
			if(checkerType.equals(StatusName.NonAlphaMultiSupplier)
					|| checkerType.equals(StatusName.MaskMultiSupplier)
					|| checkerType.equals(StatusName.FamilyMultiSupplier))
			{
				qury.append(" GROUP BY chktax.CHECK_PART_ID,chktax.ID,com.COM_ID,tp.DOCUMENT_ID,p.ID,p.NAME,com.PART_NUMBER  order by chktax.CHECK_PART_ID");
			}
			else if(checkerType.equals(StatusName.MaskMultiData)
					|| checkerType.equals(StatusName.RootPartChecker))
			{
				qury.append(" GROUP BY chktax.CHECK_PART_ID,chktax.ID,com.COM_ID,tp.DOCUMENT_ID,p.ID,p.NAME,com.PART_NUMBER,chktax.PL_FET_ID,chktax.FET_VAL  order by chktax.CHECK_PART_ID");
			}
			else if(checkerType.equals(StatusName.generic_part))
			{
				qury.append(" GROUP BY chktax.CHECK_PART_ID,chktax.ID,com.COM_ID,tp.DOCUMENT_ID,p.ID,p.NAME,com.PART_NUMBER,chktax.PL_FET_ID,chktax.FET_VAL,gen.GENERIC  order by chktax.CHECK_PART_ID");
			}
			System.out.println(qury.toString());
			ArrayList<Object[]> result = (ArrayList<Object[]>) session.createSQLQuery(
					qury.toString()).list();
			// nunalpha 0 ,comid 1 ,DOCUMENT_ID 2,pl_id 3,pl_name 4,fetid 5,fetname 6
			data = new ArrayList<>();
			for(int i = 0; i < result.size(); i++)
			{
				QAChecksDTO qachecks = new QAChecksDTO();
				qachecks.setNanAlphaPart(result.get(i)[0] == null ? "" : result.get(i)[0]
						.toString());
				PartComponent part = getComponentBycomid(
						result.get(i)[1] == null ? 0l : Long.valueOf(result.get(i)[1].toString()),
						session);
				qachecks.setPart(part);
				qachecks.setVendor(part.getSupplierId());
				qachecks.setDatasheet(part.getDocument());
				qachecks.setDatasheetTitle(part.getDocument().getTitle());
				qachecks.setMask(part.getMasterPartMask());
				qachecks.setFamily(part.getFamily());
				Pl pl = part.getSupplierPl().getPl();
				qachecks.setProductLine(part.getSupplierPl().getPl());
				if(checkerType.equals(StatusName.MaskMultiData)
						|| checkerType.equals(StatusName.RootPartChecker))
				{
					PlFeature fet = ParaQueryUtil.getPlFeatureid(result.get(i)[5] == null ? 0l
							: Long.valueOf(result.get(i)[5].toString()), pl, session);
					qachecks.setFeatureName(fet.getFeature().getName());
					qachecks.setFeatureValue(result.get(i)[6] == null ? "" : result.get(i)[6]
							.toString());
					qachecks.setCheckpartid(result.get(i)[7] == null ? 0l : Long.valueOf(result
							.get(i)[7].toString()));
				}
				else if(checkerType.equals(StatusName.generic_part))
				{
					PlFeature fet = ParaQueryUtil.getPlFeatureid(result.get(i)[5] == null ? 0l
							: Long.valueOf(result.get(i)[5].toString()), pl, session);
					qachecks.setGeneric(result.get(i)[8] == null ? "" : result.get(i)[8].toString());
					qachecks.setFeatureName(fet.getFeature().getName());
					qachecks.setFeatureValue(result.get(i)[6] == null ? "" : result.get(i)[6]
							.toString());
					qachecks.setCheckpartid(result.get(i)[7] == null ? 0l : Long.valueOf(result
							.get(i)[7].toString()));
				}
				else
				{
					qachecks.setCheckpartid(result.get(i)[5] == null ? 0l : Long.valueOf(result
							.get(i)[5].toString()));
				}
				qachecks.setChecker(checkerType);
				data.add(qachecks);
			}

		}catch(Exception e)
		{

			e.printStackTrace();
		}finally
		{
			// session.close();
		}
		return data;
	}

	public static boolean chkpartflagqachks(PartComponent part, Long checkpartid, Session session)
	{
		boolean exist = false;
		try
		{
			Criteria cri = session.createCriteria(QaCheckParts.class);
			cri.add(Restrictions.eq("partComponent", part));
			// cri.createAlias("action", "action");
			// cri.add(Restrictions.or(Restrictions.eq("action.name", StatusName.Open), Restrictions.eq("action.name", StatusName.inprogress)));
			cri.add(Restrictions.eq("id", checkpartid));
			List chkpart = null;
			chkpart = cri.list();
			if(!chkpart.isEmpty())
			{
				exist = true;
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return exist;

	}

	public static void updateqacheckspart(ArrayList<QAChecksDTO> qachks)
	{
		Session session = null;
		session = SessionUtil.getSession();
		Criteria cri = null;
		for(QAChecksDTO qachk : qachks)
		{
			List<QaCheckMultiData> qaCheckMultiData = null;
			List<QaCheckMultiTax> qaCheckMultiTax = null;
			QaChecksStatus staus = null;
			QaChecksActions action = null;
			String partaction = "";
			if(qachk.getStatus().equals(StatusName.WrongPart)
					|| qachk.getStatus().equals(StatusName.WrongTax))
			{
				if(qachk.getFlag().equals("AffectedPart"))
				{
					partaction = StatusName.WaittingReplication;
				}
				else
				{
					// perform the action
					doqachksaction(qachk, session);
					partaction = StatusName.Done;
				}
			}
			else if(qachk.getStatus().equals(StatusName.UpdateFamily)
					|| qachk.getStatus().equals(StatusName.UpdateMask)
					|| qachk.getStatus().equals(StatusName.UpdateGeneric))
			{
				if(qachk.getFlag().equals("AffectedPart"))
				{
					// perform the action
					doqachksaction(qachk, session);
					partaction = StatusName.qachecking;
				}
				else
				{
					// perform the action
					doqachksaction(qachk, session);
					partaction = StatusName.Done;
				}
			}
			else if(qachk.getStatus().equals(StatusName.Exception))
			{
				partaction = StatusName.WaittingException;
			}
			else if(qachk.getStatus().equals(StatusName.UpdateParametricData))
			{
				if(qachk.getFlag().equals("AffectedPart"))
				{
					partaction = StatusName.WaittingReplication;
				}
				else
				{
					// perform the action
					doqachksaction(qachk, session);
					partaction = StatusName.Done;
				}
			}
			else if(qachk.getStatus().equals(StatusName.WaittingQAChecks))
			{
				partaction = StatusName.WaittingQAChecks;
			}
			cri = session.createCriteria(QaChecksStatus.class);
			cri.add(Restrictions.eq("name", qachk.getStatus()));
			staus = (QaChecksStatus) cri.uniqueResult();

			cri = session.createCriteria(QaChecksActions.class);
			cri.add(Restrictions.eq("name", partaction));
			action = (QaChecksActions) cri.uniqueResult();

			if(qachk.getChecker().equals(StatusName.MaskMultiData)
					|| qachk.getChecker().equals(StatusName.RootPartChecker)
					|| qachk.getChecker().equals(StatusName.generic_part))
			{
				cri = session.createCriteria(QaCheckMultiData.class);
				cri.add(Restrictions.eq("conflictedPart", qachk.getPart().getComId().toString()));
				cri.createAlias("qaCheckParts", "qaCheckParts");
				cri.add(Restrictions.eq("qaCheckParts.id", qachk.getCheckpartid()));
				cri.createAlias("plFeature", "plFeature");
				PlFeature plfet = ParaQueryUtil.getPlFeatureByExactName(qachk.getFeatureName(),
						qachk.getProductLine().getName(), session);
				cri.add(Restrictions.eq("plFeature", plfet));
				cri.createAlias("qaChecksActions", "action");
				cri.add(Restrictions.eq("action.name", StatusName.Open));
				qaCheckMultiData = cri.list();
				if(!qaCheckMultiData.isEmpty())
				{
					qaCheckMultiData.get(0).setQaChecksStatus(staus);
					qaCheckMultiData.get(0).setQaChecksActions(action);
					qaCheckMultiData.get(0).setCorrectVal(qachk.getNewValue());
					session.saveOrUpdate(qaCheckMultiData.get(0));
				}
				else
				{
					System.err.println("------- No QA Checks to save action------");
				}
			}
			else
			{
				cri = session.createCriteria(QaCheckMultiTax.class);
				cri.add(Restrictions.eq("conflictedPart", qachk.getPart().getComId().toString()));
				cri.createAlias("qaCheckParts", "qaCheckParts");
				cri.add(Restrictions.eq("qaCheckParts.id", qachk.getCheckpartid()));
				cri.createAlias("qaChecksActions", "action");
				cri.add(Restrictions.eq("action.name", StatusName.Open));
				qaCheckMultiTax = cri.list();
				if(!qaCheckMultiTax.isEmpty())
				{
					qaCheckMultiTax.get(0).setQaChecksStatus(staus);
					qaCheckMultiTax.get(0).setQaChecksActions(action);
					qaCheckMultiTax.get(0).setCorrectValue(qachk.getNewValue());
					session.saveOrUpdate(qaCheckMultiTax.get(0));
				}
				else
				{
					System.err.println("------- No QA Checks to save action------");
				}
			}
		}
	}

	private static void doqachksaction(QAChecksDTO qachk, Session session)
	{
		try
		{

			if(qachk.getStatus().equals(StatusName.WrongPart))
			{
				Criteria cri = null;
				// get QA Check Action
				cri = session.createCriteria(QaChecksActions.class);
				cri.add(Restrictions.eq("name", StatusName.closed));
				QaChecksActions action = (QaChecksActions) cri.uniqueResult();

				// delete rows from parametricReviewData
				cri = session.createCriteria(ParametricReviewData.class);
				cri.add(Restrictions.eq("component", qachk.getPart()));
				List<ParametricReviewData> review = cri.list();
				for(ParametricReviewData para : review)
				{
					session.delete(para);
					System.err.println("-----reviewdata deleted : " + para.getId() + "------");
				}

				// set Other qachecks to closed
				if(qachk.getChecker().equals(StatusName.MaskMultiData)
						|| qachk.getChecker().equals(StatusName.RootPartChecker)
						|| qachk.getChecker().equals(StatusName.generic_part))
				{
					cri = session.createCriteria(QaCheckMultiData.class);
					cri.add(Restrictions
							.eq("conflictedPart", qachk.getPart().getComId().toString()));
					// cri.createAlias("qaChecksActions", "action");
					// cri.add(Restrictions.eq("action.name", StatusName.Open));
					cri.createAlias("qaCheckParts", "qaCheckParts");
					cri.add(Restrictions.eq("qaCheckParts.id", qachk.getCheckpartid()));
					QaCheckMultiData multidata = (QaCheckMultiData) cri.uniqueResult();
					QaCheckParts qapart = multidata.getQaCheckParts();
					cri = session.createCriteria(QaCheckMultiData.class);
					cri.add(Restrictions.eq("qaCheckParts", qapart));
					List<QaCheckMultiData> list = cri.list();
					for(QaCheckMultiData qadata : list)
					{
						// qadata.setQaChecksActions(action);
						session.delete(qadata);
						System.err.println("-----QaCheckMultiData deleted : " + qadata.getId()
								+ "------");
					}
					session.delete(qapart);
					System.err.println("-----QaCheckParts deleted : " + qapart.getId() + "------");
				}
				else
				{
					cri = session.createCriteria(QaCheckMultiTax.class);
					cri.add(Restrictions
							.eq("conflictedPart", qachk.getPart().getComId().toString()));
					cri.add(Restrictions.eq("qaChecksActions.name", StatusName.Open));
					cri = session.createCriteria(QaCheckMultiTax.class);
					cri.add(Restrictions
							.eq("conflictedPart", qachk.getPart().getComId().toString()));
					// cri.createAlias("qaChecksActions", "action");
					// cri.add(Restrictions.eq("action.name", StatusName.Open));
					cri.createAlias("qaCheckParts", "qaCheckParts");
					cri.add(Restrictions.eq("qaCheckParts.id", qachk.getCheckpartid()));
					QaCheckMultiTax multitax = (QaCheckMultiTax) cri.uniqueResult();
					QaCheckParts qapart = multitax.getQaCheckParts();
					cri = session.createCriteria(QaCheckMultiTax.class);
					cri.add(Restrictions.eq("qaCheckParts", qapart));
					List<QaCheckMultiTax> list = cri.list();
					for(QaCheckMultiTax qadata : list)
					{
						// qadata.setQaChecksActions(action);
						session.delete(qadata);
						System.err.println("-----QaCheckMultiTax deleted : " + qadata.getId()
								+ "------");
					}
					session.delete(qapart);
					System.err.println("-----QaCheckParts deleted : " + qapart.getId() + "------");
				}
				// check if there is parts in that Document
				cri = session.createCriteria(ParametricReviewData.class);
				cri.createAlias("trackingParametric", "trackingParametric");
				cri.add(Restrictions.eq("trackingParametric.document", qachk.getDatasheet()));
				List<ParametricReviewData> review2 = cri.list();
				if(review2.isEmpty())
				{
					// reassgin the trackingparametric to development
					cri = session.createCriteria(TrackingParametric.class);
					cri.add(Restrictions.eq("document", qachk.getDatasheet()));
					TrackingParametric track = (TrackingParametric) cri.uniqueResult();
					TrackingTaskStatus trackingTaskStatus = ParaQueryUtil.getTrackingTaskStatus(
							session, StatusName.assigned);
					track.setTrackingTaskStatus(trackingTaskStatus);
				}

				// delete part from component
				session.delete(qachk.getPart());
				System.err.println("-----PartComponent deleted : "
						+ qachk.getPart().getPartNumber() + "------");
			}
			else if(qachk.getStatus().equals(StatusName.WrongTax))
			{
				// get supplierpl
				Criteria cri = session.createCriteria(SupplierPl.class);
				cri.add(Restrictions.eq("pl", qachk.getProductLine()));
				cri.add(Restrictions.eq("supplier", qachk.getVendor()));
				SupplierPl supplierpl = (SupplierPl) cri.uniqueResult();

				// get parts for document_pl
				cri = session.createCriteria(PartComponent.class);
				cri.add(Restrictions.eq("document", qachk.getDatasheet()));
				cri.add(Restrictions.eq("supplierPl", supplierpl));
				List<PartComponent> parts = cri.list();

				// get QACheckAction
				cri = session.createCriteria(QaChecksActions.class);
				cri.add(Restrictions.eq("name", StatusName.closed));
				QaChecksActions action = (QaChecksActions) cri.uniqueResult();

				// delete rows from parametricReviewData
				cri = session.createCriteria(ParametricReviewData.class);
				cri.add(Restrictions.eq("component", qachk.getPart()));
				List<ParametricReviewData> review = cri.list();
				for(ParametricReviewData para : review)
				{
					session.delete(para);
					System.err.println("-----ParametricReviewData deleted : " + para.getId()
							+ "------");
				}

				// send feedBack to sourcing team
				String feedbackStatus = sendFeedbackToSourcingTeam(qachk.getEngname(), qachk
						.getDatasheet().getPdf().getSeUrl(), qachk.getProductLine().getName(),
						"Wrong tax", null, qachk.getNewValue());
				System.out.println(feedbackStatus);

				// set Other qachecks to closed
				if(qachk.getChecker().equals(StatusName.MaskMultiData)
						|| qachk.getChecker().equals(StatusName.RootPartChecker)
						|| qachk.getChecker().equals(StatusName.generic_part))
				{
					cri = session.createCriteria(QaCheckParts.class);
					cri.add(Restrictions.in("partComponent", parts));
					cri.createAlias("qaChecksActions", "action");
					cri.add(Restrictions.eq("action.name", StatusName.Open));
					cri.createAlias("preQaCheckers", "preQaCheckers");
					cri.add(Restrictions.eq("preQaCheckers.name", qachk.getChecker()));
					List<QaCheckParts> qaparts = cri.list();

					cri = session.createCriteria(QaCheckMultiData.class);
					cri.add(Restrictions.in("qaCheckParts", qaparts));
					List<QaCheckMultiData> list = cri.list();
					for(QaCheckMultiData qadata : list)
					{
						// qadata.setQaChecksActions(action);
						session.delete(qadata);
						System.err.println("-----QaCheckMultiData deleted : " + qadata.getId()
								+ "------");
					}
					for(QaCheckParts qapart : qaparts)
					{
						session.delete(qapart);
						System.err.println("-----QaCheckParts deleted : " + qapart.getId()
								+ "------");
					}
				}
				else
				{
					cri = session.createCriteria(QaCheckParts.class);
					cri.add(Restrictions.in("partComponent", parts));
					cri.createAlias("qaChecksActions", "action");
					cri.add(Restrictions.eq("action.name", StatusName.Open));
					cri.createAlias("preQaCheckers", "preQaCheckers");
					cri.add(Restrictions.eq("preQaCheckers.name", qachk.getChecker()));
					List<QaCheckParts> qaparts = cri.list();
					cri = session.createCriteria(QaCheckMultiTax.class);
					cri.add(Restrictions.eq("qaCheckParts", qaparts));
					List<QaCheckMultiTax> list = cri.list();
					for(QaCheckMultiTax qadata : list)
					{
						// qadata.setQaChecksActions(action);
						session.delete(qadata);
						System.err.println("-----QaCheckMultiTax deleted : " + qadata.getId()
								+ "------");
					}
					for(QaCheckParts qapart : qaparts)
					{
						session.delete(qapart);
						System.err.println("-----QaCheckParts deleted : " + qapart.getId()
								+ "------");
					}
				}
				// delete parts from component
				for(PartComponent part : parts)
				{
					session.delete(part);
					System.err.println("-----PartComponent deleted : " + part.getPartNumber()
							+ "------");
				}
			}
			else if(qachk.getStatus().equals(StatusName.UpdateFamily))
			{
				if(qachk.getNewValue() != null && !qachk.getNewValue().isEmpty())
				{
					Family family = ParaQueryUtil
							.getFamilyByExactName(qachk.getNewValue(), session);
					// if family not found insert new family record
					if(family == null)
					{
						family = insertFamily(qachk.getNewValue(), session);
					}
					qachk.getPart().setFamily(family);
					session.saveOrUpdate(qachk.getPart());
					System.err.println("-----family updated to: " + family.getName() + "------");
					// get QA Check Action
					Criteria cri = session.createCriteria(QaChecksActions.class);
					cri.add(Restrictions.eq("name", StatusName.closed));
					QaChecksActions action = (QaChecksActions) cri.uniqueResult();

					// set Other qachecks to closed
					if(qachk.getChecker().equals(StatusName.RootPartChecker))
					{
						cri = session.createCriteria(QaCheckMultiData.class);
						cri.createAlias("qaCheckParts", "qaCheckParts");
						cri.add(Restrictions.eq("qaCheckParts.id", qachk.getCheckpartid()));
						cri.add(Restrictions.ne("conflictedPart", qachk.getPart().getComId()
								.toString()));
						List<QaCheckMultiData> list = cri.list();
						for(QaCheckMultiData qadata : list)
						{
							qadata.setQaChecksActions(action);
							System.err.println("-----QaCheckMultiData closed : " + qadata.getId()
									+ "------");
						}
					}
					else if(qachk.getChecker().equals(StatusName.FamilyMultiSupplier))

					{
						cri = session.createCriteria(QaCheckMultiTax.class);
						cri.createAlias("qaCheckParts", "qaCheckParts");
						cri.add(Restrictions.eq("qaCheckParts.id", qachk.getCheckpartid()));
						cri.add(Restrictions.ne("conflictedPart", qachk.getPart().getComId()
								.toString()));
						List<QaCheckMultiTax> list = cri.list();
						for(QaCheckMultiTax qadata : list)
						{
							qadata.setQaChecksActions(action);
							System.err.println("-----QaCheckMultiTax closed : " + qadata.getId()
									+ "------");
						}
					}
				}
			}
			else if(qachk.getStatus().equals(StatusName.UpdateMask))
			{
				if(qachk.getNewValue() != null && !qachk.getNewValue().isEmpty())
				{
					MasterPartMask mask = getMask(qachk.getNewValue());
					if(mask == null)
					{
						mask = insertMask(qachk.getNewValue(), session);
					}
					qachk.getPart().setMasterPartMask(mask);
					session.saveOrUpdate(qachk.getPart());
					System.err.println("-----mask updated to: " + mask.getMstrPart() + "------");

					// get QA Check Action
					Criteria cri = session.createCriteria(QaChecksActions.class);
					cri.add(Restrictions.eq("name", StatusName.closed));
					QaChecksActions action = (QaChecksActions) cri.uniqueResult();

					// set Other qachecks to closed
					if(qachk.getChecker().equals(StatusName.MaskMultiData))
					{
						cri = session.createCriteria(QaCheckMultiData.class);
						cri.createAlias("qaCheckParts", "qaCheckParts");
						cri.add(Restrictions.eq("qaCheckParts.id", qachk.getCheckpartid()));
						cri.add(Restrictions.ne("conflictedPart", qachk.getPart().getComId()
								.toString()));
						List<QaCheckMultiData> list = cri.list();
						for(QaCheckMultiData qadata : list)
						{
							qadata.setQaChecksActions(action);
							System.err.println("-----QaCheckMultiData closed : " + qadata.getId()
									+ "------");
						}
					}
					else if(qachk.getChecker().equals(StatusName.MaskMultiSupplier))

					{
						cri = session.createCriteria(QaCheckMultiTax.class);
						cri.createAlias("qaCheckParts", "qaCheckParts");
						cri.add(Restrictions.eq("qaCheckParts.id", qachk.getCheckpartid()));
						cri.add(Restrictions.ne("conflictedPart", qachk.getPart().getComId()
								.toString()));
						List<QaCheckMultiTax> list = cri.list();
						for(QaCheckMultiTax qadata : list)
						{
							qadata.setQaChecksActions(action);
							System.err.println("-----QaCheckMultiTax closed : " + qadata.getId()
									+ "------");
						}
					}
				}
			}
			else if(qachk.getStatus().equals(StatusName.UpdateGeneric))
			{
				if(qachk.getNewValue() != null && !qachk.getNewValue().isEmpty())
				{
					MapGeneric generic = ParaQueryUtil.getGeneric(qachk.getNewValue());
					if(generic == null)
					{
						generic = insertGeneric(qachk.getNewValue(), session);
					}
					qachk.getPart().setMapGeneric(generic);
					session.saveOrUpdate(qachk.getPart());
					System.err.println("-----generic updated to: " + generic.getGeneric()
							+ "------");

					// get QA Check Action
					Criteria cri = session.createCriteria(QaChecksActions.class);
					cri.add(Restrictions.eq("name", StatusName.closed));
					QaChecksActions action = (QaChecksActions) cri.uniqueResult();

					// set Other qachecks to closed
					cri = session.createCriteria(QaCheckMultiData.class);
					cri.createAlias("qaCheckParts", "qaCheckParts");
					cri.add(Restrictions.eq("qaCheckParts.id", qachk.getCheckpartid()));
					cri.add(Restrictions
							.ne("conflictedPart", qachk.getPart().getComId().toString()));
					List<QaCheckMultiData> list = cri.list();
					for(QaCheckMultiData qadata : list)
					{
						qadata.setQaChecksActions(action);
						System.err.println("-----QaCheckMultiData closed : " + qadata.getId()
								+ "------");
					}
				}
			}
			else if(qachk.getStatus().equals(StatusName.UpdateParametricData))
			{
				if(qachk.getNewValue() != null && !qachk.getNewValue().isEmpty())
				{
					// get PlFeature byFeatureName
					PlFeature plFeature = ParaQueryUtil.getPlFeatureByExactName(
							qachk.getFeatureName(), qachk.getProductLine().getName(), session);
					// get Approved Values
					List<String> appValues = ParaQueryUtil.getGroupFullValueByPlFeature(plFeature,
							session);
					// Save New Value if it is approved
					if(appValues.contains(qachk.getNewValue()))
					{
						ParametricApprovedGroup groupexist = ParaQueryUtil
								.getParametricApprovedGroup(qachk.getFeatureValue(), plFeature,
										session);
						ParametricApprovedGroup group = ParaQueryUtil.getParametricApprovedGroup(
								qachk.getNewValue(), plFeature, session);
						Criteria cri = session.createCriteria(ParametricReviewData.class);
						cri.add(Restrictions.eq("component", qachk.getPart()));
						cri.add(Restrictions.eq("plFeature", plFeature));
						ParametricReviewData review = (ParametricReviewData) cri.uniqueResult();
						review.setGroupApprovedValueId(group.getId());
						review.setStoreDate(new Date());
						session.saveOrUpdate(review);
						System.err.println("-----fetvalue updated to: " + group.getGroupFullValue()
								+ "------");
					}
					else
					{
						ArrayList<String> values = new ArrayList<>();
						values.add(qachk.getProductLine().getName());
						values.add(qachk.getPart().getPartNumber());
						values.add(qachk.getDatasheet().getPdf().getSeUrl());
						values.add(qachk.getFeatureName());
						values.add(qachk.getNewValue());
						QAChecks.seperationvalues.add(values);
					}
				}
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}

	}

	public static void updateqapartsstatus(ArrayList<QAChecksDTO> allparts)
	{
		Session session = null;
		try
		{
			session = SessionUtil.getSession();
			// ArrayList<Document> pdfs = new ArrayList<>();
			List<QaCheckParts> qaparts = new ArrayList<>();
			ArrayList<PartComponent> parts = new ArrayList<>();
			for(QAChecksDTO qachk : allparts)
			{
				// if(!pdfs.isEmpty() && !pdfs.contains(qachk.getDatasheet()))
				// pdfs.add(qachk.getDatasheet());
				parts.add(qachk.getPart());
			}
			// get QACheck Action and Tracking status
			Criteria cri = session.createCriteria(QaChecksActions.class);
			cri.add(Restrictions.eq("name", StatusName.Done));
			QaChecksActions action = (QaChecksActions) cri.uniqueResult();
			cri = session.createCriteria(QaChecksActions.class);
			cri.add(Restrictions.eq("name", StatusName.inprogress));
			QaChecksActions inprogressaction = (QaChecksActions) cri.uniqueResult();
			cri = session.createCriteria(TrackingTaskStatus.class);
			cri.add(Restrictions.eq("name", StatusName.tlReview));
			TrackingTaskStatus status = (TrackingTaskStatus) cri.uniqueResult();
			cri = session.createCriteria(QaChecksActions.class);
			cri.add(Restrictions.eq("name", StatusName.Open));
			QaChecksActions openaction = (QaChecksActions) cri.uniqueResult();

			// get QACheckParts
			cri = session.createCriteria(QaCheckParts.class);
			cri.add(Restrictions.in("partComponent", parts));
			cri.add(Restrictions.eq("action", openaction));
			cri.createAlias("preQaCheckers", "Checkers");
			cri.add(Restrictions.eq("Checkers.name", allparts.get(0).getChecker()));
			qaparts = cri.list();

			// Update QACheck Parts set Acton = done if all Rows done
			for(QaCheckParts qachkpart : qaparts)
			{
				boolean done = true;
				if(qachkpart.getPreQaCheckers().getName().equals(StatusName.MaskMultiData)
						|| qachkpart.getPreQaCheckers().getName()
								.equals(StatusName.RootPartChecker)
						|| qachkpart.getPreQaCheckers().getName().equals(StatusName.generic_part))
				{
					cri = session.createCriteria(QaCheckMultiData.class);
					cri.add(Restrictions.eq("qaCheckParts", qachkpart));
					List<QaCheckMultiData> multidata = cri.list();
					for(QaCheckMultiData qadata : multidata)
					{
						if(!qadata.getQaChecksActions().getName().equals(StatusName.Done))
						{
							done = false;
							break;
						}
					}
					if(done)
					{
						qachkpart.setAction(action);
						session.saveOrUpdate(qachkpart);
						cri = session.createCriteria(TrackingParametric.class);
						cri.add(Restrictions.eq("document", qachkpart.getDocument()));
						TrackingParametric track = (TrackingParametric) cri.uniqueResult();
						track.setTrackingTaskStatus(status);
						session.saveOrUpdate(track);
					}
					else
					{
						qachkpart.setAction(inprogressaction);
						session.saveOrUpdate(qachkpart);
					}
				}
				else
				{
					cri = session.createCriteria(QaCheckMultiTax.class);
					cri.add(Restrictions.eq("qaCheckParts", qachkpart));
					List<QaCheckMultiTax> multitax = cri.list();
					for(QaCheckMultiTax qadata : multitax)
					{
						if(!qadata.getQaChecksActions().getName().equals(StatusName.Done))
						{
							done = false;
							break;
						}
					}
					if(done)
					{
						qachkpart.setAction(action);
						session.saveOrUpdate(qachkpart);
						cri = session.createCriteria(TrackingParametric.class);
						cri.add(Restrictions.eq("document", qachkpart.getDocument()));
						TrackingParametric track = (TrackingParametric) cri.uniqueResult();
						track.setTrackingTaskStatus(status);
						session.saveOrUpdate(track);
					}
					else
					{
						qachkpart.setAction(inprogressaction);
						session.saveOrUpdate(qachkpart);
					}
				}

			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public static void updateqaexceptionspart(ArrayList<QAChecksDTO> qachks, String screen)
	{
		Session session = null;
		session = SessionUtil.getSession();
		Criteria cri = null;
		String currentstatus = "";
		if(screen.equals("QA"))
		{
			currentstatus = StatusName.WaittingException;
		}
		else
		{
			currentstatus = StatusName.RejectException;
		}
		for(QAChecksDTO qachk : qachks)
		{
			List<QaCheckMultiData> qaCheckMultiData = null;
			List<QaCheckMultiTax> qaCheckMultiTax = null;
			QaChecksActions action = null;
			String partaction = "";
			boolean fbpart = checkpartifexistinFB(qachk, session);
			if(screen.equals("QA"))
			{
				if(qachk.getStatus().equals(StatusName.approved))
				{
					if(fbpart)
					{
						qachk.setStatus(StatusName.fbClosed);
						saveexceptionfeedback(qachk, session);
					}
					addtoexception(qachk, session);
					partaction = StatusName.Done;
				}
				else if(qachk.getStatus().equals(StatusName.reject))
				{
					// initiate FB
					saveexceptionfeedback(qachk, session);
					partaction = StatusName.RejectException;
				}
			}
			else
			{
				if(qachk.getStatus().equals(StatusName.approved))
				{
					// initiate FB
					qachk.setStatus(StatusName.fbClosed);
					// addtoexception(qachk, session);
					saveexceptionfeedback(qachk, session);
					partaction = StatusName.Open;
				}
				else if(qachk.getStatus().equals(StatusName.reject))
				{
					// initiate FB
					saveexceptionfeedback(qachk, session);
					partaction = StatusName.WaittingException;
				}
			}

			cri = session.createCriteria(QaChecksActions.class);
			cri.add(Restrictions.eq("name", partaction));
			action = (QaChecksActions) cri.uniqueResult();

			if(qachk.getChecker().equals(StatusName.MaskMultiData)
					|| qachk.getChecker().equals(StatusName.RootPartChecker)
					|| qachk.getChecker().equals(StatusName.generic_part))
			{
				cri = session.createCriteria(QaCheckMultiData.class);
				cri.add(Restrictions.eq("conflictedPart", qachk.getPart().getComId().toString()));
				cri.createAlias("qaChecksActions", "action");
				cri.add(Restrictions.eq("action.name", currentstatus));
				qaCheckMultiData = cri.list();
				if(!qaCheckMultiData.isEmpty())
				{
					qaCheckMultiData.get(0).setQaChecksActions(action);
					session.saveOrUpdate(qaCheckMultiData.get(0));
				}
				else
				{
					System.err.println("------- No QA Checks to save action------");
				}
			}
			else
			{
				cri = session.createCriteria(QaCheckMultiTax.class);
				cri.add(Restrictions.eq("conflictedPart", qachk.getPart().getComId().toString()));
				cri.createAlias("qaChecksActions", "action");
				cri.add(Restrictions.eq("action.name", currentstatus));
				qaCheckMultiTax = cri.list();
				if(!qaCheckMultiTax.isEmpty())
				{
					qaCheckMultiTax.get(0).setQaChecksActions(action);
					session.saveOrUpdate(qaCheckMultiTax.get(0));
				}
				else
				{
					System.err.println("------- No QA Checks to save action------");
				}
			}
		}
	}

	private static boolean checkpartifexistinFB(QAChecksDTO qachk, Session session)
	{
		GrmUser issuedByUser = ParaQueryUtil.getGRMUserByName(qachk.getEngname());
		Criteria fbcriteria = session.createCriteria(ParametricFeedbackCycle.class);
		fbcriteria.add(Restrictions.eq("fbItemValue", qachk.getPart().getPartNumber()));
		fbcriteria.add(Restrictions.eq("issuedTo", issuedByUser.getId()));
		fbcriteria.add(Restrictions.eq("feedbackRecieved", 0l));
		List<ParametricFeedbackCycle> parametricFeedbackCycle = fbcriteria.list();

		if(parametricFeedbackCycle.isEmpty())
			return false;
		else
			return true;
	}

	private static void addtoexception(QAChecksDTO qachk, Session session)
	{
		try
		{
			Criteria cri = session.createCriteria(PreQaCheckers.class);
			cri.add(Restrictions.eq("name", qachk.getChecker()));

			PreQaCheckers checker = (PreQaCheckers) cri.uniqueResult();
			PreQaCheckersException qaexception = new PreQaCheckersException();
			qaexception.setId(System.nanoTime());
			qaexception.setComid(qachk.getPart().getComId());
			qaexception.setFamily(qachk.getFamily());
			if(qachk.getChecker().equals(StatusName.MaskMultiData)
					|| qachk.getChecker().equals(StatusName.RootPartChecker)
					|| qachk.getChecker().equals(StatusName.generic_part))
			{
				qaexception.setFetValue(qachk.getFeatureValue() == null ? "" : qachk
						.getFeatureValue());
				PlFeature plFeature = ParaQueryUtil.getPlFeatureByExactName(qachk.getFeatureName(),
						qachk.getProductLine().getName(), session);
				qaexception.setPlFetId(plFeature.getId());

				cri.add(Restrictions.eq("fetValue", qaexception.getFetValue()));
				cri.add(Restrictions.eq("plFetId", qaexception.getPlFetId()));
			}
			qaexception.setPlId(qachk.getProductLine().getId());
			qaexception.setPreQaCheckers(checker);

			cri = session.createCriteria(PreQaCheckersException.class);
			cri.add(Restrictions.eq("family", qaexception.getFamily()));
			cri.add(Restrictions.eq("comid", qaexception.getComid()));
			cri.add(Restrictions.eq("preQaCheckers", qaexception.getPreQaCheckers()));
			cri.add(Restrictions.eq("plId", qaexception.getPlId()));
			PreQaCheckersException tmp = (PreQaCheckersException) cri.uniqueResult();
			if(tmp == null)
			{
				session.saveOrUpdate(qaexception);
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}

	}

	public static void saveexceptionfeedback(QAChecksDTO qachk, Session session)
	{
		try
		{
			ParaFeedbackStatus paraFeedbackAction = null;
			ParaFeedbackStatus paraFeedbackStatus = null;
			ParaIssueType paraIssueType = null;
			ParametricFeedback FBObj = new ParametricFeedback();
			ParametricFeedbackCycle FBCyc = new ParametricFeedbackCycle();
			ParaFeedbackAction feedbackAction = null;
			TrackingFeedbackType trackingFeedbackType = null;
			QaCheckParts qacheckPart = ParaQueryUtil.getqacheckdocbychkpartid(
					qachk.getCheckpartid(), session);
			TrackingParametric track = getTrackingParametricByDocumentAndPl(
					qacheckPart.getDocument(), qacheckPart.getPartComponent().getSupplierPl()
							.getPl(), session);
			String comment = qachk.getNewValue();
			String issuedByName = qachk.getEngname();

			String feedbackTypeStr = StatusName.QAException;
			// String wrongfeatures = partInfo.getWrongFeatures();
			PartComponent component = qachk.getPart();
			GrmUser issuedByUser = ParaQueryUtil.getGRMUserByName(issuedByName);
			long issedto = track.getParametricUserId();

			Date date = ParaQueryUtil.getDate();

			Criteria criteria = null;

			if(feedbackTypeStr != null && !feedbackTypeStr.isEmpty())
			{
				criteria = session.createCriteria(ParaIssueType.class);
				System.out.println(feedbackTypeStr);
				criteria.add(Restrictions.eq("issueType", feedbackTypeStr));
				paraIssueType = (ParaIssueType) criteria.uniqueResult();
			}

			String fbStatus = StatusName.inprogress;
			String FBAction = qachk.getStatus();
			System.out.println(FBAction);
			long fbRecieved = 0l;
			if(FBAction.equals(StatusName.fbClosed))
			{
				fbRecieved = 1l;
				fbStatus = StatusName.closed;
				FBAction = StatusName.accept;
			}
			// if(FBAction.equals(StatusName.approved))
			// {
			// FBAction = StatusName.accept;
			// }
			criteria = session.createCriteria(ParaFeedbackStatus.class);
			System.out.println(FBAction);
			criteria.add(Restrictions.eq("feedbackStatus", FBAction));
			paraFeedbackAction = (ParaFeedbackStatus) criteria.uniqueResult();//

			criteria = session.createCriteria(ParaFeedbackStatus.class);
			criteria.add(Restrictions.eq("feedbackStatus", fbStatus));
			paraFeedbackStatus = (ParaFeedbackStatus) criteria.uniqueResult();//

			criteria = session.createCriteria(TrackingFeedbackType.class);
			System.out.println(StatusName.QA);
			criteria.add(Restrictions.eq("name", StatusName.QA));
			trackingFeedbackType = (TrackingFeedbackType) criteria.uniqueResult();

			Criteria fbcriteria = session.createCriteria(ParametricFeedbackCycle.class);
			fbcriteria.add(Restrictions.eq("fbItemValue", component.getPartNumber()));
			fbcriteria.add(Restrictions.eq("issuedTo", issuedByUser.getId()));
			fbcriteria.add(Restrictions.eq("feedbackRecieved", 0l));
			ParametricFeedbackCycle parametricFeedbackCycle = (ParametricFeedbackCycle) fbcriteria
					.uniqueResult();

			if(parametricFeedbackCycle != null)
			{
				parametricFeedbackCycle.setFeedbackRecieved(1l);
				session.saveOrUpdate(parametricFeedbackCycle);
				FBObj = parametricFeedbackCycle.getParametricFeedback();
				FBObj.setParaFeedbackStatus(paraFeedbackStatus);
				issedto = parametricFeedbackCycle.getIssuedBy();
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
				FBObj.setDocument(qachk.getDatasheet());
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
			session.saveOrUpdate(FBObj);
			session.saveOrUpdate(FBCyc);
			session.beginTransaction().commit();

		}catch(Exception e)
		{
			e.printStackTrace();
		}

	}

	public static void saveParametricReviewData(PartInfoDTO partInfo, Long comId, Long trackingId,
			Session session)
	{
		Map<String, String> fetsMap = partInfo.getFetValues();
		Set<String> fetNames = fetsMap.keySet();

		String features = "", plFetid = "";
		for(String fetName : fetNames)
		{

			plFetid = fetName.split("_Id_")[0];

			// PlFeature plFeature = ParaQueryUtil.getPlFeatureByExactName(fetName,
			// partInfo.getPlName(), session);
			String fetValue = fetsMap.get(fetName);
			if(fetValue.isEmpty())
			{
				System.out.println(fetName + "Has blank Values ");
				continue;
			}

			// features+=" select GETPLFETID('"+partInfo.getPlName()+"','"+fetName+"')||'_"+fetValue+"' val from dual union all";
			features += " select  " + plFetid + "||'_" + fetValue + "' val from dual union all";
		}

		features = features.substring(0, features.length() - 10);
		String sql = "insert into PARAMETRIC_REVIEW_DATA(COM_ID,PL_FEATURE_ID,GROUP_APPROVED_VALUE_ID,TRACKING_PARAMETRIC_ID,STORE_DATE,MODIFIED_DATE) select "
				+ comId
				+ ",PL_FEATURE_ID,ID,"
				+ trackingId
				+ ",sysdate,sysdate from PARAMETRIC_APPROVED_GROUP where PL_FEATURE_ID||'_'||GROUP_FULL_VALUE in("
				+ features + ")";
		int x = session.createSQLQuery(sql).executeUpdate();
		System.out.println("number of fets is " + x);
		// for(String fetName : fetNames)
		// {
		// // session.beginTransaction().begin();
		// PlFeature plFeature = ParaQueryUtil.getPlFeatureByExactName(fetName,
		// partInfo.getPlName(), session);
		// String fetValue = fetsMap.get(fetName);
		// if(fetValue.isEmpty())
		// {
		// System.out.println(fetName + "Has blank Values ");
		// continue;
		// }
		//
		// features+="GETPLFETID('"+partInfo.getPlName()+"','"+fetName+"')||'_"+fetValue+"'),";
		// ParametricApprovedGroup group = ParaQueryUtil.getParametricApprovedGroup(PartInfoDTO fetValue,
		// plFeature, session);
		// ParametricReviewData data = new ParametricReviewData();
		// long id = QueryUtil.getRandomID();
		// data.setComponent(com);
		// data.setTrackingParametric(track);
		// data.setGroupApprovedValueId(group.getId());
		// data.setPlFeature(plFeature);
		// data.setStoreDate(new Date());
		// data.setId(id);
		// session.saveOrUpdate(data);
		// // session.beginTransaction().commit();
		// }

	}

	public static void saveParametricReviewDataById(PartInfoDTO partInfo, Long comId,
			Long trackingId, Session session)
	{
		Map<String, String> fetsMap = partInfo.getFetValues();
		Set<String> fetNames = fetsMap.keySet();

		String features = "";
		for(String fetName : fetNames)
		{

			// PlFeature plFeature = ParaQueryUtil.getPlFeatureByExactName(fetName,
			// partInfo.getPlName(), session);
			String fetValue = fetsMap.get(fetName);
			if(fetValue.isEmpty())
			{
				System.out.println(fetName + "Has blank Values ");
				continue;
			}

			features += " select GETPLFETID('" + partInfo.getPlName() + "','" + fetName + "')||'_"
					+ fetValue + "' val from dual union all";

		}
		features = features.substring(0, features.length() - 10);
		String sql = "insert into PARAMETRIC_REVIEW_DATA(COM_ID,PL_FEATURE_ID,GROUP_APPROVED_VALUE_ID,TRACKING_PARAMETRIC_ID,STORE_DATE,MODIFIED_DATE) select "
				+ comId
				+ ",PL_FEATURE_ID,ID,"
				+ trackingId
				+ ",sysdate,sysdate from PARAMETRIC_APPROVED_GROUP where PL_FEATURE_ID||'_'||GROUP_FULL_VALUE in("
				+ features + ")";
		int x = session.createSQLQuery(sql).executeUpdate();
		System.out.println("number of parts is " + x);

	}
}
