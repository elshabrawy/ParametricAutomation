package com.se.parametric.dba;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;

import javassist.expr.NewArray;

import javax.print.Doc;
import javax.swing.JOptionPane;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.loader.custom.Return;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;

import osheet.WorkingSheet;

import com.se.automation.db.CloneUtil;
import com.se.automation.db.ParametricQueryUtil;
import com.se.automation.db.QueryUtil;
import com.se.automation.db.SessionUtil;
import com.se.automation.db.client.mapping.ApprovedParametricValue;
import com.se.automation.db.client.mapping.ApprovedValueFeedback;
import com.se.automation.db.client.mapping.Condition;
import com.se.automation.db.client.mapping.DevelopmentCommentValue;
import com.se.automation.db.client.mapping.Document;
import com.se.automation.db.client.mapping.DocumentDownloadJob;
import com.se.automation.db.client.mapping.DocumentFeedback;
import com.se.automation.db.client.mapping.ExtractorPdfData;
import com.se.automation.db.client.mapping.ExtractorValueMapping;
import com.se.automation.db.client.mapping.Family;
import com.se.automation.db.client.mapping.FamilyCross;
import com.se.automation.db.client.mapping.Feature;
import com.se.automation.db.client.mapping.GenericFamily;
import com.se.automation.db.client.mapping.MapGeneric;
import com.se.automation.db.client.mapping.MasterFamilyGeneric;
import com.se.automation.db.client.mapping.MasterPartMask;
import com.se.automation.db.client.mapping.Multiplier;
import com.se.automation.db.client.mapping.MultiplierUnit;
import com.se.automation.db.client.mapping.NoParametricDocuments;
import com.se.automation.db.client.mapping.NonPdf;
import com.se.automation.db.client.mapping.ParametricApprovedGroup;
import com.se.automation.db.client.mapping.ParametricFeedbackCycle;
import com.se.automation.db.client.mapping.ParametricReviewData;
import com.se.automation.db.client.mapping.ParametricSeparationGroup;
import com.se.automation.db.client.mapping.PartComponent;
import com.se.automation.db.client.mapping.PartMaskValue;
import com.se.automation.db.client.mapping.PartMaskValueId;
import com.se.automation.db.client.mapping.PartsFeedback;
import com.se.automation.db.client.mapping.PartsParametric;
import com.se.automation.db.client.mapping.PartsParametricValuesGroup;
import com.se.automation.db.client.mapping.Pdf;
import com.se.automation.db.client.mapping.PkgApprovedValue;
import com.se.automation.db.client.mapping.PkgFeature;
import com.se.automation.db.client.mapping.PkgJedec;
import com.se.automation.db.client.mapping.PkgMainData;
import com.se.automation.db.client.mapping.Pl;
import com.se.automation.db.client.mapping.PlFeature;
import com.se.automation.db.client.mapping.PlFetConverter;
import com.se.automation.db.client.mapping.QaCheckRelatedFunctions;
import com.se.automation.db.client.mapping.QaChecksInDependentFeature;
import com.se.automation.db.client.mapping.QaChecksValidatetype;
import com.se.automation.db.client.mapping.SerPl;
import com.se.automation.db.client.mapping.Sign;
import com.se.automation.db.client.mapping.Supplier;
import com.se.automation.db.client.mapping.SupplierPl;
import com.se.automation.db.client.mapping.SupplierPlFamily;
import com.se.automation.db.client.mapping.SupplierUrl;
import com.se.automation.db.client.mapping.TblFullDataCtrl;
import com.se.automation.db.client.mapping.TblPdfCompare;
import com.se.automation.db.client.mapping.TblPdfStatic;
import com.se.automation.db.client.mapping.TrackingDatasheetAlert;
import com.se.automation.db.client.mapping.TrackingFast;
import com.se.automation.db.client.mapping.TrackingFastStatus;
import com.se.automation.db.client.mapping.TrackingFeedback;
import com.se.automation.db.client.mapping.TrackingFeedbackType;
import com.se.automation.db.client.mapping.TrackingParamDocApprov;
import com.se.automation.db.client.mapping.TrackingParamUserPlRate;
import com.se.automation.db.client.mapping.TrackingParametric;
import com.se.automation.db.client.mapping.TrackingParametricApprovVal;
import com.se.automation.db.client.mapping.TrackingPkg;
import com.se.automation.db.client.mapping.TrackingPkgApprovVal;
import com.se.automation.db.client.mapping.TrackingPkgDocApprov;
import com.se.automation.db.client.mapping.TrackingPkgTlVendor;
import com.se.automation.db.client.mapping.TrackingTaskQaStatus;
import com.se.automation.db.client.mapping.TrackingTaskStatus;
import com.se.automation.db.client.mapping.TrackingTaskType;
import com.se.automation.db.client.mapping.TrackingTeam;
import com.se.automation.db.client.mapping.TrackingTransferStatus;
import com.se.automation.db.client.mapping.Unit;
import com.se.automation.db.client.mapping.Value;
import com.se.automation.db.client.mapping.ValueType;
import com.se.grm.client.mapping.GrmGroup;
import com.se.grm.client.mapping.GrmRole;
import com.se.grm.client.mapping.GrmUser;
import com.se.parametric.AppContext;
import com.se.parametric.dto.ApprovedParametricDTO;
import com.se.parametric.dto.DocumentInfoDTO;
import com.se.parametric.dto.FeatureDTO;
import com.se.parametric.dto.GrmUserDTO;
import com.se.parametric.dto.PartInfoDTO;
import com.se.parametric.dto.RelatedFeaturesDTO;
import com.se.parametric.dto.TableInfoDTO;
import com.se.parametric.dto.UnApprovedDTO;
import com.se.parametric.excel.ExcelHandler2003;
import com.se.parametric.util.StatusName;
import com.sun.star.lib.uno.environments.remote.remote_environment;

public class ParaQueryUtil
{

	public static void main(String[] args)
	{
		 Session session = SessionUtil.getSession();
		// try
		// {
		// ArrayList<String> fd = getFeedbackByPartAndSupp("DAMV-11C1S-N-A197", "ITT Corporation");
		// System.out.println(fd.get(0) + " " + fd.get(1) + " " + fd.get(2));
		// }catch(Exception e)
		// {
		// e.printStackTrace();
		// }finally
		// {
		// session.close();
		// }Date startDate, Date endDate, GrmUserDTO userDTO, String plName, String supplierName, String status, String type
		Pl pl;
		try
		{
			List<String> plFeatures=ParametricQueryUtil.getDoneFlagfets(125l, session);
			System.out.println(plFeatures.get(0));
			String g = "N/A";
			// getGeneric(g);
			// checkUser("abeer","123456");
			pl = getPlByPlName("Solid State Relay");
			// Pl ptype = getPLType(pl);
			// System.out.println(ptype.getName());
			// System.out.println(getLastIssueSource(28862724l));
			new ParaQueryUtil().getPlFeautres(pl, false);
			DataDevQueryUtil.getMask("AD7903BRQZ____");
		}catch(Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			session.close();
		}
		// getSeparatedSections1(" to  | - to ");
	}

	public static List<String> getSigns()
	{

		Session session = SessionUtil.getSession();
		try
		{
			return session.createQuery("select o.name from Sign o").list();
		}catch(Exception ex)
		{
			ex.printStackTrace();

		}finally
		{
			session.close();

		}
		return null;
	}

	public static List<String> getMultipliers()
	{

		Session session = SessionUtil.getSession();
		try
		{

			return session.createQuery("select o.name from Multiplier o").list();
		}catch(Exception ex)
		{
			ex.printStackTrace();

		}finally
		{
			session.close();
		}
		return null;
	}

	public static List<String> getCondition()
	{

		Session session = SessionUtil.getSession();
		try
		{
			return session.createQuery("select o.name  from Condition o").list();
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}finally
		{
			session.close();

		}
		return null;
	}

	public static List<String> getValueType()
	{

		Session session = SessionUtil.getSession();
		try
		{
			return session.createQuery("select o.name  from ValueType o").list();
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}finally
		{
			session.close();

		}
		return null;
	}

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

	public static GrmUserDTO checkUser(String userName, String pass)
	{
		GrmUser developer = null;
		ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
		ArrayList<String> row = null;
		Session session = com.se.grm.db.SessionUtil.getSession();
		Criteria criteria = session.createCriteria(GrmUser.class);
		criteria.add(Restrictions.eq("password", pass));
		criteria.add(Restrictions.or(Restrictions.like("email", userName + "@%").ignoreCase(), Restrictions.eq("fullName", userName).ignoreCase()));
		GrmUser grmUser = (GrmUser) criteria.uniqueResult();

		com.se.grm.db.SessionUtil.getSession().close();
		if(grmUser == null)
		{
			return null;
		}
		else
		{
			GrmUserDTO user = new GrmUserDTO();
			user.setId(grmUser.getId());
			user.setEmail(grmUser.getEmail());
			user.setEnable(grmUser.getEnable());
			user.setFullName(grmUser.getFullName());
			user.setLastLogin(grmUser.getLastLogin());
			user.setLastLogout(grmUser.getLastLogout());
			user.setPassword(grmUser.getPassword());
			user.setStoreDate(grmUser.getStoreDate());
			GrmRole role = new GrmRole();
			role.setId(grmUser.getGrmRole().getId());
			role.setName(grmUser.getGrmRole().getName());
			user.setGrmRole(role);
			GrmGroup group = new GrmGroup();
			group.setId(grmUser.getGrmGroup().getId());
			group.setName(grmUser.getGrmGroup().getName());
			user.setGrmGroup(group);
			Set developers = new HashSet();
			Iterator it = grmUser.getDevelopers().iterator();
			while(it.hasNext())
			{
				developer = (GrmUser) it.next();
				developers.add(developer);
			}
			user.setDevelopers(developers);
			user.setLeader(grmUser.getLeader());
			return user;
		}
	}

	public static Long[] getTeamMembersIDByTL(long userId)
	{

		Long[] result = new Long[10];
		Session grmSession = com.se.grm.db.SessionUtil.getSession();
		try
		{
			Criteria crit = grmSession.createCriteria(GrmUser.class);
			crit.add(Restrictions.eq("leader", getGrmUserById(userId, grmSession)));
			List<GrmUser> u = crit.list();
			int i = 0;
			result = new Long[u.size()];
			for(GrmUser grmUser : u)
			{
				result[i] = grmUser.getId();
				System.out.println("user :" + grmUser.getId());
				i++;
			}
		}finally
		{
			grmSession.close();
		}
		return result;

	}

	public static ArrayList<Object[]> getTLUnapprovedData(Long[] ids, Date startDate, Date endDate)
	{

		Session session = SessionUtil.getSession();
		// Session grmSession = com.se.grm.db.SessionUtil.getSession();
		Criteria criteria;
		ArrayList<Object[]> result = new ArrayList<Object[]>();
		try
		{

			criteria = session.createCriteria(PartsParametricValuesGroup.class, "group");
			criteria.add(Restrictions.in("paraUserId", ids));
			criteria.createAlias("taskStatus", "taskStatus");
			criteria.add(Restrictions.in("taskStatus.name", new String[] { StatusName.tlReview, StatusName.qaReview }));
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
				row = new Object[5];
				// eng = getUserByUserId(partsParametricValuesGroup.getParaUserId(), grmSession);
				eng = getGRMUser(partsParametricValuesGroup.getParaUserId());
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
						row[2] = tp.getSupplier().getName();
						row[4] = tp.getTrackingTaskType().getName();
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

	public static List<String> getUnit()
	{

		Session session = SessionUtil.getSession();
		try
		{
			return session.createQuery("select o.name  from Unit o").list();
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}finally
		{
			session.close();

		}
		return null;
	}

	public static GrmUserDTO getGRMUser(long id)
	{
		Session grmSession = com.se.grm.db.SessionUtil.getSession();

		Criteria crit = grmSession.createCriteria(GrmUser.class);
		crit.add(Restrictions.eq("id", id));
		GrmUser grmUser = (GrmUser) crit.uniqueResult();
		GrmUserDTO udto = new GrmUserDTO();
		udto.setId(grmUser.getId());
		udto.setFullName(grmUser.getFullName());
		udto.setEmail(grmUser.getEmail());
		grmSession.close();
		return udto;
	}

	public static Sign getSignByExactName(String signName, Session session)
	{
		if(!signName.equals(""))
		{
			Criteria crit = session.createCriteria(Sign.class);
			crit.add(Restrictions.eq("name", signName));
			Sign sign = (Sign) crit.uniqueResult();
			return sign;
		}
		return null;
	}

	public static void updateTrackingParamDocApprov(Session session, TrackingParamDocApprov trackingParamDocApprov)
	{
		session.saveOrUpdate(trackingParamDocApprov);
		session.beginTransaction().commit();
	}

	public static TrackingParamDocApprov getTrackingParamDocApprovById(Session session, long idTrackingParamDocApprov) throws Exception
	{
		Criteria c = session.createCriteria(TrackingParamDocApprov.class);
		c.add(Restrictions.eq("id", idTrackingParamDocApprov));
		TrackingParamDocApprov trackingParamDocApprov = (TrackingParamDocApprov) c.uniqueResult();
		return trackingParamDocApprov;

	}

	public static TrackingPkgDocApprov getTrackingPkgDocApprovById(Session session, long idTrackingPkgDocApprov) throws Exception
	{
		Criteria c = session.createCriteria(TrackingPkgDocApprov.class);
		c.add(Restrictions.eq("id", idTrackingPkgDocApprov));
		TrackingPkgDocApprov trackingPkgDocApprov = (TrackingPkgDocApprov) c.uniqueResult();
		return trackingPkgDocApprov;

	}

	@SuppressWarnings("unchecked")
	public static int getCountApprovedParametricValue(Session session, String plname) throws Exception
	{
		final Criteria crit = session.createCriteria(TrackingParametricApprovVal.class);
		crit.createCriteria("approvedParametricValue").createCriteria("plFeature").createCriteria("pl").add(Restrictions.ilike("name", plname, MatchMode.START));

		crit.setProjection(Projections.count("id")).list();

		List<Integer> list = crit.list();
		if(list == null)
			return 0;
		return list.get(0);

	}

	@SuppressWarnings("unchecked")
	public static List<Document> documentidref(Document document, Session session)
	{

		Criteria crit = session.createCriteria(Document.class);
		crit.add(Restrictions.eq("id", document));
		List<Document> list = crit.list();
		return list;

	}

	@SuppressWarnings("unchecked")
	public static List<DevelopmentCommentValue> getDevelopmentCommentValues(long SupplierId, Session session)
	{
		Criteria crit = session.createCriteria(DevelopmentCommentValue.class);
		crit.createCriteria("supplier").add(Restrictions.eq("id", SupplierId));
		return crit.list();
	}

	/**
	 * try to get list of Pkg features from PkgFeature table
	 * 
	 * @param pkgTypeId
	 * @param session
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<PkgFeature> getPkgFeaturesByPkgTypeId(long pkgTypeId, Session session)
	{
		Criteria crit = session.createCriteria(PkgFeature.class);
		crit.createCriteria("pkgType").add(Restrictions.eq("id", pkgTypeId));
		crit.add(Restrictions.isNotNull("headerOrder"));
		crit.addOrder(Order.asc("headerOrder"));

		// crit.setProjection(Projections.property("feature"));
		return crit.list();

	}

	@SuppressWarnings("unchecked")
	public static List<PlFeature> getFeaturesByPlId(long plId, Session session) throws Exception
	{
		Criteria crit = session.createCriteria(PlFeature.class);
		crit.createCriteria("pl").add(Restrictions.eq("id", plId));
		crit.createCriteria("feature").add(
				Restrictions.not(Restrictions.in("name", new String[] { "Family", "PRODUCT_NAME", "Standard_Package_Name", "Introduction Date", "PRODUCT_EXTERNAL_DATASHEET", "Vendor", "Vendor Code", "Description", "Introduction Name", "Pin Count",
						"Supplier Package", "ROHS", "Life Cycle" })));
		crit.add(Restrictions.isNotNull("columnName"));
		crit.add(Restrictions.ne("columnName", "man_id"));
		/**
		 * make order for list of PlFeature depend on propertyName "DEVELOPMENT_ORDER" asc order
		 */
		crit.addOrder(Order.asc("DevelopmentOrder"));
		// crit.setProjection(Projections.property("feature"));
		return crit.list();
	}

	public static PlFeature getFeaturebyName(String plName, String fetName, Session session) throws Exception
	{
		Criteria crit = session.createCriteria(PlFeature.class);
		crit.createCriteria("pl").add(Restrictions.eq("name", plName));
		crit.createCriteria("feature").add(Restrictions.eq("name", fetName));
		return (PlFeature) crit.uniqueResult();
	}

	public static SupplierPl getSupplierPlBySupplierAndPl(Pl pl, Supplier supplier, Session session)
	{
		Criteria crit = session.createCriteria(SupplierPl.class);
		crit.add(Restrictions.eq("pl", pl));
		crit.add(Restrictions.eq("supplier", supplier));
		SupplierPl supplierPl = (SupplierPl) crit.uniqueResult();
		if(supplierPl == null)
			supplierPl = addNewSupplierPl(supplier, pl, session);
		return supplierPl;
	}

	public static SupplierPl addNewSupplierPl(Supplier supplier, Pl pl, Session session)
	{
		SupplierPl supplierPl = new SupplierPl();
		supplierPl.setId(System.nanoTime());
		supplierPl.setPl(pl);
		supplierPl.setSupplier(supplier);
		supplierPl.setStoreDate(new Date());
		session.save(supplierPl);
		return supplierPl;
	}

	@SuppressWarnings("unchecked")
	public static List<PartComponent> getPartNumberByDocuomentId(Session session, long docuomentId/* , String taxonomy */) throws Exception
	{
		final Criteria crit = session.createCriteria(PartComponent.class);
		// if(!taxonomy.equals(""))
		// {
		// crit.createCriteria("supplierPl").createCriteria("pl").add(Restrictions.ilike("name",
		// taxonomy, MatchMode.START));
		// }

		crit.createCriteria("document").add(Restrictions.eq("id", docuomentId));
		// if(SupplierName != null)
		// crit.createCriteria("supplierPl").createCriteria("supplier").add(Restrictions.eq("name",
		// SupplierName));

		List<PartComponent> listComponent = crit.list();
		return listComponent;

	}

	public static Pl getPlBySupplierPl(Session session, long supplierPLId) throws Exception
	{
		// Pl pls=null;
		final Criteria crit = session.createCriteria(SupplierPl.class);
		crit.add(Restrictions.eq("id", supplierPLId));
		// if(SupplierName != null)
		// crit.createCriteria("supplierPl").createCriteria("supplier").add(Restrictions.eq("name",
		// SupplierName));

		return ((SupplierPl) crit.uniqueResult()).getPl();

	}

	public static Map<String, Long> getPLFetUnitsVal(Session session, String plName, String fetName)
	{
		Map<String, Long> map = new HashMap<String, Long>();
		Criteria cr = session.createCriteria(PlFetConverter.class);
		cr.createCriteria("pl").add(Restrictions.eq("name", plName));
		cr.createCriteria("feature").add(Restrictions.eq("name", fetName));
		List data = cr.list();
		PlFetConverter plFetConverter;
		if(data.isEmpty())
		{
			return map;
		}
		for(int t = 0; t < data.size(); t++)
		{
			plFetConverter = (PlFetConverter) data.get(t);
			map.put((String) ((plFetConverter.getUnit() != null) ? plFetConverter.getUnit() : ""), (Long) ((plFetConverter.getValue() != null) ? plFetConverter.getValue() : "")

			);

		}
		return map;
	}

	// @SuppressWarnings("unchecked")
	// public static ApprovedParametricValue getApprovedFeatureValue(String
	// value, /* PlFeature feature */String plFeatureName, Session session)
	// {
	// Criteria crit = session.createCriteria(ApprovedParametricValue.class);
	// crit.add(Restrictions.eq("fullValue", value));
	// crit.createCriteria("plFeature").add(Restrictions.eq("feature.name",
	// plFeatureName));
	// //
	// List list = crit.list();
	// ApprovedParametricValue approvedValue = null;
	// if(list.size() > 0)
	// approvedValue = (ApprovedParametricValue) list.get(0);
	// return approvedValue;
	// }
	public static ApprovedParametricValue getApprovedFeatureValue(String value, String plFeatureName, String plName, Session session)
	{
		// Criteria crit =
		// session.createCriteria(ApprovedParametricValue.class);
		// crit.add(Restrictions.eq("fullValue", value));
		// crit.createCriteria("plFeature").add(Restrictions.eq("feature.name",
		// plFeatureName));
		Criteria crit = session.createCriteria(ApprovedParametricValue.class);
		crit.add(Restrictions.eq("fullValue", value));
		Criteria plFeatureCrit = crit.createCriteria("plFeature");
		//
		Criteria featureCrit = plFeatureCrit.createCriteria("feature");
		featureCrit.add(Restrictions.eq("name", plFeatureName));
		//
		Criteria plCrit = plFeatureCrit.createCriteria("pl");
		plCrit.add(Restrictions.eq("name", plName));
		//
		List list = crit.list();
		ApprovedParametricValue approvedValue = null;
		if(list.size() > 0)
			approvedValue = (ApprovedParametricValue) list.get(0);
		return approvedValue;
	}

	/**
	 * Islam try to check the approved value which entered by the user
	 * 
	 * @param value
	 * @param feature
	 * @param session
	 * @return
	 */

	// public static PkgApprovedValue getApprovedPkgFeatureValue(String value,
	// String pkgFeatureName, boolean isApproved, Session session) {
	// Criteria crit = session.createCriteria(PkgApprovedValue.class);
	// crit.createCriteria("pkgFeature").add(
	// Restrictions.eq("name", pkgFeatureName));
	// // crit.createAlias("pkgFeature", "pkgFeat");
	// // crit.add(Restrictions.eq("pkgFeat.name", pkgFeatureName));
	// crit.add(Restrictions.eq("approvalFlag", isApproved));
	// crit.createAlias("pkgValue", "val");
	// crit.add(Restrictions.eq("val.value", value));
	// List list = crit.list();
	// PkgApprovedValue pkgApprovedValue = null;
	// if (list.size() > 0)
	// pkgApprovedValue = (PkgApprovedValue) list.get(0);
	// else if (!isApproved) {
	// PkgFeature pkgFeature = getPkgFeatureByExactName(pkgFeatureName, 2,
	// session);
	// PkgValue pkgValue = getPkgValue(value, session);
	// pkgApprovedValue = ParametricDevServerUtil
	// .addApprovedPkgFeatureValue(pkgValue, pkgFeature,
	// isApproved, session);
	// }
	// return pkgApprovedValue;
	// }

	// @SuppressWarnings("rawtypes")
	// public static PkgValue getPkgValue(String value, Session session) {
	// PkgValue pkgValue = new PkgValue();
	// Criteria crit = session.createCriteria(PkgValue.class);
	// crit.add(Restrictions.eq("value", value));
	// List list = crit.list();
	//
	// if (list.size() > 0)
	// pkgValue = (PkgValue) list.get(0);
	// else
	// pkgValue = ParametricDevServerUtil.addValue(value, session);
	// // @SuppressWarnings("unchecked")
	// // public static List<ApprovedParametricValue>
	// // getApprovedFeatureValues2(PlFeature feature, Session session)
	// // {
	// // Criteria crit =
	// // session.createCriteria(ApprovedParametricValue.class);
	// // crit.createCriteria("plFeature").add(Restrictions.eq("id",
	// // feature.getId()));
	// // //
	// // List<ApprovedParametricValue> list = crit.list();
	// // return list;
	// // }
	//
	// return pkgValue;
	//
	// }

	// @SuppressWarnings("unchecked")
	// public static List<ApprovedParametricValue>
	// getApprovedFeatureValues2(PlFeature feature, Session session)
	// {
	// Criteria crit = session.createCriteria(ApprovedParametricValue.class);
	// crit.createCriteria("plFeature").add(Restrictions.eq("id",
	// feature.getId()));
	// //
	// List<ApprovedParametricValue> list = crit.list();
	// return list;
	// }

	@SuppressWarnings("unchecked")
	public static List<String> getApprovedFeatureValues(PlFeature feature, Session session)
	{
		Criteria crit = session.createCriteria(ApprovedParametricValue.class);
		crit.add(Restrictions.eq("isApproved", true));
		crit.createCriteria("plFeature").add(Restrictions.eq("id", feature.getId()));
		crit.setProjection(Projections.property("fullValue"));
		//
		List<String> list = crit.list();
		return list;
	}

	/**
	 * islam try to get a list of approved values for a pkg feature. *
	 */
	@SuppressWarnings("unchecked")
	public static List<String> getApprovedPkgFeatureValues(PkgFeature feature, Session session)
	{
		Criteria crit = session.createCriteria(PkgApprovedValue.class);
		crit.add(Restrictions.eq("pkgFeature", feature));
		crit.add(Restrictions.eq("approvalFlag", true));
		crit.createAlias("pkgValue", "val");
		crit.setProjection(Projections.property("val.value"));
		List<String> list = crit.list();
		return list;
	}

	public static Pdf getPdfWithNonParametricParts(Vector<Long> skipedPdfIds, final Session session)
	{
		StringBuilder queryStr = new StringBuilder("SELECT   p.*" + " FROM   pdf p,document doc,component com,document_supplier_pl_family doc_family,supplier_pl_family family,pl ,pl_feature_unit pl_fet" + " WHERE   " + " DOC.PDF_ID = p.id"
				+ " AND com.document_id = doc.id" + " and doc.id = doc_family.document_id" + " and doc_family.supplier_pl_family_id = family.id" + " and family.pl_id = pl.id" + " and pl.id = pl_fet.pl_id " + " AND DOC.PROGRESS_STATUS = '"
				+ StatusName.finshed + "' " + " and p.id NOT IN" + "            (SELECT   DISTINCT p.id" + "             FROM   pdf p," + "                  document doc," + "                part_component com," + "              parts_parametric pp"
				+ "    WHERE       DOC.PDF_ID = p.id" + "          AND com.document_id = doc.id" + "        AND com.com_id = pp.com_id)" + " AND ROWNUM = 1");

		if(skipedPdfIds != null && skipedPdfIds.size() > 0)
		{
			queryStr.append(" and p.id not in (" + skipedPdfIds.get(0));
			for(int i = 1; i < skipedPdfIds.size(); i++)
			{
				queryStr.append("," + skipedPdfIds.get(i));
			}
			queryStr.append(")");
		}
		SQLQuery query = session.createSQLQuery(queryStr.toString());
		query.addEntity(Pdf.class);
		return (Pdf) query.uniqueResult();
	}

	private static Date addDay(Date date)
	{
		final Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.DAY_OF_MONTH, 1);
		return c.getTime();
	}

	@SuppressWarnings("unchecked")
	public static List<ApprovedParametricValue> getApprovedParametricValuesId(long groupid, Session session) throws Exception
	{
		Criteria crit = session.createCriteria(PartsParametricValuesGroup.class);
		crit.add(Restrictions.eq("groupId", groupid));
		crit.setProjection(Projections.property("approvedParametricValue"));
		List<ApprovedParametricValue> ApprovedParametricValues = crit.list();

		return ApprovedParametricValues;
	}

	/*
	 * salah
	 */

	@SuppressWarnings("unchecked")
	public static List<String> getFullValuesByApprovedParametricValueId(long ApprovedParametricValues, Session session)
	{

		Criteria crit = session.createCriteria(ApprovedParametricValue.class);
		crit.add(Restrictions.eq("id", ApprovedParametricValues));
		crit.setProjection(Projections.property("fullValue"));
		List<String> FullValues = crit.list();

		return FullValues;

	}

	public static List<Pdf> getPdfsForParametricDev(String plName, String supplierName, String downloadJob, Date lastCheckDate, Date downloadDate/*
																																				 * ,
																																				 * Date
																																				 * dueDate
																																				 */, String pdfType, String seUrl, boolean forRevision, Session session)
	{
		Criteria pdfCrit = session.createCriteria(Pdf.class);
		Criteria docCrit = pdfCrit.createCriteria("documents");
		if(forRevision)
		{
			docCrit.add(Restrictions.isNotNull("revisionsDocuments"));

		}
		Criteria supplierPlFamilyCrit = docCrit.createCriteria("supplierPlFamilies");
		// doc should be datasheet
		docCrit.createCriteria("documentType").add(Restrictions.eq("name", "Datasheet"));
		// se url
		if(seUrl != null && seUrl.trim().length() > 0)
		{
			pdfCrit.add(Restrictions.eq("seUrl", seUrl));
		}
		// join pdf type
		if(pdfType != null && pdfType.trim().length() > 0)
		{
			docCrit.add(Restrictions.eq("cmStatus", pdfType));
		}
		// join last check date
		if(lastCheckDate != null)
		{
			Date lastCheckDatePlusDay = addDay(lastCheckDate);
			pdfCrit.add(Restrictions.between("lastCheckDate", getDate(lastCheckDate), getDate(lastCheckDatePlusDay)));

		}
		// join download date
		if(downloadDate != null)
		{
			Date downloadDatePlusDay = addDay(downloadDate);
			pdfCrit.add(Restrictions.between("downloadDate", getDate(downloadDate), getDate(downloadDatePlusDay)));
		}
		// join supplier
		if(supplierName != null && !supplierName.equalsIgnoreCase("all"))
		{
			Supplier supplier = QueryUtil.getSupplierByExactName(supplierName, session);
			if(supplier != null)
			{
				supplierPlFamilyCrit.add(Restrictions.eq("supplier.id", supplier.getId()));
			}
		}
		// join pl
		if(plName != null)
		{
			Pl pl = QueryUtil.getPlByExactName(plName, session);
			if(pl != null)
			{
				supplierPlFamilyCrit.add(Restrictions.eq("pl.id", pl.getId()));
			}
		}
		// join download job
		if(downloadJob != null)
		{
			DocumentDownloadJob ddj = QueryUtil.getDownloadJobByExactName(downloadJob, session);
			if(ddj != null)
			{
				docCrit.createCriteria("documentDownloadJobs").add(Restrictions.eq("id", ddj.getId()));
			}
		}
		//
		// pdfCrit.set
		pdfCrit.setMaxResults(25);
		List<Pdf> list = pdfCrit.list();
		return list;

	}

	@SuppressWarnings("deprecation")
	public static Date getDate(Date date)
	{
		date.setDate(date.getDate());
		date.setHours(date.getHours());
		date.setSeconds(date.getSeconds());
		date.setMinutes(date.getMinutes());

		return date;
	}

	public static Date getDate()
	{

		// DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date today = new Date();

		// today = df.parse(new Date());
		df.format(today);

		return today;
	}

	@SuppressWarnings("unchecked")
	public static PartsParametricValuesGroup getGroupByAprrovedValue(ApprovedParametricValue approvedVal, Session session)
	{
		Criteria crit = session.createCriteria(PartsParametricValuesGroup.class);
		crit.add(Restrictions.eq("approvedParametricValue", approvedVal));
		//
		List<PartsParametricValuesGroup> list = crit.list();
		if(list.size() > 0)
			return list.get(0);
		return null;
	}

	public static void updateTrackingApprovalStatus(Session session, TrackingParamDocApprov trackingParametricDocVal) throws Exception
	{
		session.saveOrUpdate(trackingParametricDocVal);
		session.beginTransaction().commit();

	}

	public static void updateTrackingPkgDocApprov(Session session, TrackingPkgDocApprov trackingPkgDocApprov) throws Exception
	{
		session.saveOrUpdate(trackingPkgDocApprov);
		session.beginTransaction().commit();

	}

	public static TrackingTaskQaStatus getTrackingTaskQaStatus(String trackingName) throws Exception
	{
		Session se = SessionUtil.getSession();
		Criteria crit = se.createCriteria(TrackingTaskQaStatus.class);
		crit.add(Restrictions.eq("name", trackingName));
		return (TrackingTaskQaStatus) crit.uniqueResult();
	}

	public static TrackingTaskQaStatus getTrackingTaskQaStatus(Session session, String nameTaskQaStatus) throws Exception
	{
		final Criteria crit = session.createCriteria(TrackingTaskQaStatus.class);
		crit.add(Restrictions.eq("name", nameTaskQaStatus));
		TrackingTaskQaStatus trackingTasStatus = (TrackingTaskQaStatus) crit.uniqueResult();
		return trackingTasStatus;

	}

	public static TrackingTaskStatus getTrackingTaskStatus(Session session, String nameTaskStatus) throws Exception
	{
		final Criteria crit = session.createCriteria(TrackingTaskStatus.class);
		crit.add(Restrictions.eq("name", nameTaskStatus));
		TrackingTaskStatus trackingTasStatus = (TrackingTaskStatus) crit.uniqueResult();
		return trackingTasStatus;

	}

	public static TrackingTransferStatus getTrackingTransferStatus(Session session, String nameTaskStatus) throws Exception
	{
		final Criteria crit = session.createCriteria(TrackingTransferStatus.class);
		crit.add(Restrictions.eq("name", nameTaskStatus));
		TrackingTransferStatus trackingTransferStatus = (TrackingTransferStatus) crit.uniqueResult();
		return trackingTransferStatus;

	}

	// @SuppressWarnings("unchecked")
	// public static List<TrackingTaskQaStatus> getTaskQaStatus(Session session)
	// throws Exception
	// {
	// final Criteria crit = session.createCriteria(TrackingTaskQaStatus.class);
	// List<TrackingTaskQaStatus> listtTrackingTaskQaStatus = crit.list();
	// return listtTrackingTaskQaStatus;
	// }

	public static TrackingParamDocApprov getTrackingParametricDocApproval(Session session, long docuomentId, long approvedParametricValue)
	{
		final Criteria crit = session.createCriteria(TrackingParamDocApprov.class);
		crit.createCriteria("approvedParametricValue").add(Restrictions.eq("id", approvedParametricValue));
		crit.createCriteria("document").add(Restrictions.eq("id", docuomentId));
		TrackingParamDocApprov listtrTrackingParamDocApprovs = (TrackingParamDocApprov) crit.uniqueResult();

		return listtrTrackingParamDocApprovs;

	}

	@SuppressWarnings("unchecked")
	public static List<TrackingParamDocApprov> getTrackingParamDocApprov(Session session) throws Exception
	{
		final Criteria crit = session.createCriteria(TrackingParamDocApprov.class);
		List<TrackingParamDocApprov> listtrTrackingParamDocApprovs = crit.list();

		return listtrTrackingParamDocApprovs;

	}

	@SuppressWarnings("unchecked")
	public static List<TrackingPkgDocApprov> getTrackingPkgDocApprov(Session session) throws Exception
	{
		final Criteria crit = session.createCriteria(TrackingPkgDocApprov.class);
		List<TrackingPkgDocApprov> listtrTrackingPkgDocApprov = crit.list();

		return listtrTrackingPkgDocApprov;

	}

	@SuppressWarnings("unchecked")
	public static int getGroupCountByGroupId(long groupId, Session session)
	{
		Criteria crit = session.createCriteria(PartsParametricValuesGroup.class);
		crit.add(Restrictions.eq("groupId", groupId));
		crit.setProjection(Projections.count("id"));
		//
		List<Integer> list = crit.list();
		if(list == null)
			return 0;
		return list.get(0);
	}

	public static Family getFamilyByExactName(String name, Session session)
	{
		final Criteria crit = session.createCriteria(Family.class);
		crit.add(Restrictions.eq("name", name));
		final Family family = (Family) crit.uniqueResult();
		return family;
	}

	public static GenericFamily getGenericFamilyByExactName(String name, Session session)
	{
		final Criteria crit = session.createCriteria(GenericFamily.class);
		crit.add(Restrictions.eq("genericName", name));
		final GenericFamily genericFamily = (GenericFamily) crit.uniqueResult();
		return genericFamily;
	}

	@SuppressWarnings("unchecked")
	public static List<String> getFamiliesByName(String name, int limit, Session session)
	{
		Criteria crit = session.createCriteria(Family.class);
		if(name != null && name.length() > 0)
			crit.add(Restrictions.ilike("name", name, MatchMode.START));
		crit.setProjection(Projections.property("name"));
		crit.setMaxResults(limit);
		//
		List<String> list = crit.list();
		return list;
	}

	@SuppressWarnings("unchecked")
	public static List<String> getGenericFamiliesByName(String name, int limit, Session session)
	{
		Criteria crit = session.createCriteria(GenericFamily.class);
		if(name != null && name.length() > 0)
			crit.add(Restrictions.ilike("genericName", name, MatchMode.START));
		crit.setProjection(Projections.property("genericName"));
		crit.setMaxResults(limit);
		//
		List<String> list = crit.list();
		return list;
	}

	@SuppressWarnings("unchecked")
	public static List<Object[]> getUrlRevisionsDocumentsByPdfId(long pdfId, Session session) throws Exception
	{
		String quar = " SELECT   xxx.id,xxx.PDF_ID,xxx.SE_URL ,xxx.REVISION_DOCUMENT_ID,LEVEL ,xxx.REVISION_DATE" + " FROM   (SELECT   D.ID," + " D.PDF_ID," + " P.SE_URL," + " D.REVISION_DOCUMENT_ID," + " D.REVISION_DATE"
				+ " FROM   DOCUMENT D,pdf p" + " WHERE   (D.ID <> D.REVISION_DOCUMENT_ID" + "       OR D.REVISION_DOCUMENT_ID IS NULL)" + " and D.PDF_ID = p.id" + " ) xxx" + " CONNECT BY   PRIOR xxx.REVISION_DOCUMENT_ID = xxx.ID"
				+ " START WITH   xxx.ID = " + pdfId + " ORDER BY   LEVEL ASC ";
		SQLQuery query = session.createSQLQuery(quar);
		List<Object[]> list = query.list();

		return list;
	}

	@SuppressWarnings("unchecked")
	public static List<Object[]> getUrlRevisionsDocumentsByNonPdfId(long nonPdfId, Session session) throws Exception
	{
		String quar = " SELECT   xxx.id,xxx.NON_PDF_ID,xxx.SE_URL ,xxx.REVISION_DOCUMENT_ID,LEVEL ,xxx.REVISION_DATE" + " FROM   (SELECT   D.ID," + " D.NON_PDF_ID," + " P.SE_URL," + " D.REVISION_DOCUMENT_ID," + " D.REVISION_DATE"
				+ " FROM   DOCUMENT D,non_pdf p" + " WHERE   (D.ID <> D.REVISION_DOCUMENT_ID" + "       OR D.REVISION_DOCUMENT_ID IS NULL)" + " and D.NON_PDF_ID = p.id" + " ) xxx" + " CONNECT BY   PRIOR xxx.REVISION_DOCUMENT_ID = xxx.ID"
				+ " START WITH   xxx.ID = " + nonPdfId + " ORDER BY   LEVEL ASC ";
		SQLQuery query = session.createSQLQuery(quar);
		List<Object[]> list = query.list();

		return list;
	}

	@SuppressWarnings("unchecked")
	public static List<PartComponent> getPartNumberByDocumentsId(long documentid, Session session) throws Exception
	{
		Criteria crit = session.createCriteria(PartComponent.class);
		// crit.add(Restrictions.eq("document.id", documentid));
		crit.createCriteria("document").add(Restrictions.eq("id", documentid));
		List<PartComponent> list = crit.list();
		return list;
	}

	@SuppressWarnings("unchecked")
	public static List<PartComponent> getPartNumberByDocumentAndPl(long documentid, Long plId, int maxResult, Session session) throws Exception
	{
		Criteria crit = session.createCriteria(PartComponent.class);
		// crit.add(Restrictions.eq("document.id", documentid));
		crit.createCriteria("document").add(Restrictions.eq("id", documentid));
		crit.createCriteria("supplierPl").createCriteria("pl").add(Restrictions.idEq(new Long(plId)));
		List<PartComponent> list = crit.setMaxResults(maxResult).list();
		return list;
	}

	@SuppressWarnings("unchecked")
	public static List<PartComponent> getPartNumberByDocumentAndPl(long documentid, Long plId, Session session) throws Exception
	{
		Criteria crit = session.createCriteria(PartComponent.class);
		// crit.add(Restrictions.eq("document.id", documentid));
		crit.createCriteria("document").add(Restrictions.eq("id", documentid));
		crit.createCriteria("supplierPl").createCriteria("pl").add(Restrictions.idEq(new Long(plId)));
		List<PartComponent> list = crit.list();
		return list;
	}

	@SuppressWarnings("unchecked")
	public static List<PartComponent> getListOfPartNumberByDocument(Document document, Session session) throws Exception
	{
		Criteria crit = session.createCriteria(PartComponent.class);
		crit.add(Restrictions.eq("document", document));
		List<PartComponent> list = crit.list();
		return list;
	}

	@SuppressWarnings("unchecked")
	public static List<String> getFullValueListstr(String featureName, String plName, Session session)
	{
		Criteria crit = session.createCriteria(ApprovedParametricValue.class);
		Criteria plFeatureCrit = crit.createCriteria("plFeature");
		//
		Criteria featureCrit = plFeatureCrit.createCriteria("feature");
		featureCrit.add(Restrictions.eq("name", featureName));
		//
		Criteria plCrit = plFeatureCrit.createCriteria("pl");
		plCrit.add(Restrictions.eq("name", plName));
		//
		crit.setProjection(Projections.property("fullValue"));
		//
		List<String> list = crit.list();
		return list;
	}

	public static PlFeature getPlFeatureByExactName(String featureName, String plName, Session session)
	{
		try
		{
			Criteria crit = session.createCriteria(PlFeature.class);
			crit.createCriteria("feature").add(Restrictions.eq("name", featureName));
			crit.createCriteria("pl").add(Restrictions.eq("name", plName));
			// crit.add(Restrictions.eq("feature.name", plFeatureName));
			final PlFeature plFeature = (PlFeature) crit.uniqueResult();
			return plFeature;
		}catch(HibernateException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public static PkgFeature getPkgFeatureByExactName(String featureName, long pkgType, Session session)
	{
		try
		{
			Criteria crit = session.createCriteria(PkgFeature.class);
			crit.createCriteria("pkgType").add(Restrictions.eq("id", pkgType));
			crit.add(Restrictions.eq("name", featureName));
			final PkgFeature pkgFeature = (PkgFeature) crit.uniqueResult();
			return pkgFeature;
		}catch(HibernateException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static List<TrackingParametric> getParamDevTrackingPdfs(long userId, boolean forCS, boolean forTaxonomyTransfer, boolean forDaily, boolean forUpdate, boolean forFast, int pagenum, int max, Session session) throws Exception
	{

		Criteria crit = session.createCriteria(TrackingParametric.class);
		crit.createCriteria("trackingTaskStatus").add(Restrictions.eq("name", StatusName.assigned));
		if(userId != 0)
			crit.add(Restrictions.eq("parametricUserId", userId));
		if(forCS)
		{
			TrackingTaskType csTask = getTrackingTaskTypeByName("CS", session);
			crit.add(Restrictions.eq("trackingTaskType", csTask));
		}
		if(forTaxonomyTransfer)
		{
			TrackingTaskType taxonomyTransferTask = getTrackingTaskTypeByName("TaxonomyTransfer", session);
			crit.add(Restrictions.eq("trackingTaskType", taxonomyTransferTask));
		}
		if(forDaily)
		{

			TrackingTaskType dailyTransferTask = getTrackingTaskTypeByName("New", session);

			TrackingTaskType NPITransferTask = getTrackingTaskTypeByName("NPI(Flag from Sourcing)", session);

			crit.add(Restrictions.or(Restrictions.eq("trackingTaskType", NPITransferTask), Restrictions.eq("trackingTaskType", dailyTransferTask)));
		}
		if(forUpdate)
		{
			TrackingTaskType updateTransferTask = getTrackingTaskTypeByName("Update", session);
			crit.add(Restrictions.eq("trackingTaskType", updateTransferTask));
		}
		if(forFast)
		{
			TrackingTaskType updateFastTask = getTrackingTaskTypeByName("Fast", session);
			crit.add(Restrictions.eq("trackingTaskType", updateFastTask));
		}
		crit.createCriteria("document").createCriteria("documentType").add(Restrictions.eq("name", "Datasheet"));
		// add filters

		crit.addOrder(Order.desc("prioriy"));
		List<TrackingParametric> documents = crit.setFirstResult(pagenum * max).setMaxResults(max).list();
		return documents;
	}

	public static void getParamDevPdfscount(long userId, String vendor, String taxonomy, String Datasheetflag, String deliverydate, boolean forCS, boolean forTaxonomyTransfer, boolean forDaily, boolean forUpdate, boolean forFast, int pagenum,
			int max, Session session) throws Exception
	{

		Criteria crit = session.createCriteria(TrackingParametric.class);
		crit.createCriteria("trackingTaskStatus").add(Restrictions.eq("name", StatusName.assigned));
		if(userId != 0)
			crit.add(Restrictions.eq("parametricUserId", userId));
		if(taxonomy != null && taxonomy.trim().length() > 0)
		{
			crit.createCriteria("pl").add(Restrictions.eq("name", taxonomy));
		}
		if(forCS)
		{
			TrackingTaskType csTask = getTrackingTaskTypeByName("CS", session);
			crit.add(Restrictions.eq("trackingTaskType", csTask));
		}
		if(forTaxonomyTransfer)
		{
			TrackingTaskType taxonomyTransferTask = getTrackingTaskTypeByName("TaxonomyTransfer", session);
			crit.add(Restrictions.eq("trackingTaskType", taxonomyTransferTask));
		}
		if(forDaily)
		{

			if(Datasheetflag != null && !Datasheetflag.isEmpty())
			{

				TrackingTaskType dailyTransferTask = getTrackingTaskTypeByName(Datasheetflag, session);
				crit.add(Restrictions.eq("trackingTaskType", dailyTransferTask));
			}
			else
			{
				TrackingTaskType dailyTransferTask = getTrackingTaskTypeByName("New", session);
				TrackingTaskType NPITransferTask = getTrackingTaskTypeByName("NPI(Flag from Sourcing)", session);
				crit.add(Restrictions.or(Restrictions.eq("trackingTaskType", NPITransferTask), Restrictions.eq("trackingTaskType", dailyTransferTask)));
			}
		}
		if(forUpdate)
		{
			TrackingTaskType updateTransferTask = getTrackingTaskTypeByName("Update", session);
			crit.add(Restrictions.eq("trackingTaskType", updateTransferTask));
		}
		if(forFast)
		{
			TrackingTaskType updateFastTask = getTrackingTaskTypeByName("Fast", session);
			crit.add(Restrictions.eq("trackingTaskType", updateFastTask));
		}

		Criteria docCriteria = crit.createCriteria("document");
		if(vendor != null && vendor.trim().length() > 0)
		{
			docCriteria.createCriteria("pdf").createCriteria("supplierUrl").createCriteria("supplier").add(Restrictions.eq("name", vendor));
		}
		docCriteria.createCriteria("document").createCriteria("documentType").add(Restrictions.eq("name", "Datasheet"));
		crit.addOrder(Order.desc("prioriy"));
		crit.setProjection(Projections.property("document"));

	}

	@SuppressWarnings("unchecked")
	public static List<Document> getParamDevTracPdfs(long userId, String vendor, String taxonomy, String Datasheetflag, String deliverydate, boolean forCS, boolean forTaxonomyTransfer, boolean forDaily, boolean forUpdate, boolean forFast,
			int pagenum, int max, Session session) throws Exception
	{
		try
		{
			Criteria crit = session.createCriteria(TrackingParametric.class);
			crit.createCriteria("trackingTaskStatus").add(Restrictions.eq("name", StatusName.assigned));
			if(userId != 0)
				crit.add(Restrictions.eq("parametricUserId", userId));
			if(taxonomy != null && taxonomy.trim().length() > 0)
			{
				crit.createCriteria("pl").add(Restrictions.eq("name", taxonomy));
			}

			if(forCS)
			{
				TrackingTaskType csTask = getTrackingTaskTypeByName("CS", session);
				crit.add(Restrictions.eq("trackingTaskType", csTask));
			}
			if(forTaxonomyTransfer)
			{
				TrackingTaskType taxonomyTransferTask = getTrackingTaskTypeByName("TaxonomyTransfer", session);
				crit.add(Restrictions.eq("trackingTaskType", taxonomyTransferTask));
			}

			if(forDaily)
			{

				if(Datasheetflag != null && !Datasheetflag.isEmpty())
				{

					TrackingTaskType dailyTransferTask = getTrackingTaskTypeByName(Datasheetflag, session);
					crit.add(Restrictions.eq("trackingTaskType", dailyTransferTask));
				}
				else
				{
					TrackingTaskType dailyTransferTask = getTrackingTaskTypeByName("New", session);
					TrackingTaskType NPITransferTask = getTrackingTaskTypeByName("NPI(Flag from Sourcing)", session);
					crit.add(Restrictions.or(Restrictions.eq("trackingTaskType", NPITransferTask), Restrictions.eq("trackingTaskType", dailyTransferTask)));
				}
			}

			if(forUpdate)
			{
				TrackingTaskType updateTransferTask = getTrackingTaskTypeByName("Update", session);
				crit.add(Restrictions.eq("trackingTaskType", updateTransferTask));
			}
			if(forFast)
			{
				TrackingTaskType updateFastTask = getTrackingTaskTypeByName("Fast", session);
				crit.add(Restrictions.eq("trackingTaskType", updateFastTask));
			}
			Criteria docCriteria = crit.createCriteria("document");

			if(deliverydate != null && deliverydate.trim().length() > 0 && vendor != null && vendor.trim().length() > 0)
			{
				Criteria pdfCriteria = docCriteria.createCriteria("pdf");
				pdfCriteria.createCriteria("supplierUrl").createCriteria("supplier").add(Restrictions.eq("name", vendor));

			}
			else
			{
				if(deliverydate != null && deliverydate.trim().length() > 0)
				{
				}
				if(vendor != null && vendor.trim().length() > 0)
				{
					docCriteria.createCriteria("pdf").createCriteria("supplierUrl").createCriteria("supplier").add(Restrictions.eq("name", vendor));
				}
			}
			docCriteria.createCriteria("documentType").add(Restrictions.eq("name", "Datasheet"));
			crit.addOrder(Order.desc("prioriy"));
			crit.setProjection(Projections.property("document"));
			List<Document> documents = crit.list();

			if(forDaily)
			{

			}

			if(pagenum == 0 && max == 0)
				return documents;

			return crit.setFirstResult(pagenum * max).setMaxResults(max).list();
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return null;
	}

	public static List<TrackingParametric> getParamDevTrackingPdfs(long userId, String vendor, String taxonomy, String Datasheetflag, String deliverydate, boolean forCS, boolean forTaxonomyTransfer, boolean forDaily, boolean forUpdate,
			boolean forFast, int pagenum, int max, Session session) throws Exception
	{
		System.out.println("Get Parametric PDFs");
		try
		{
			Criteria crit = session.createCriteria(TrackingParametric.class);
			crit.createCriteria("trackingTaskStatus").add(Restrictions.eq("name", StatusName.assigned));
			if(userId != 0)
				crit.add(Restrictions.eq("parametricUserId", userId));
			if(taxonomy != null && taxonomy.trim().length() > 0)
			{
				crit.createCriteria("pl").add(Restrictions.eq("name", taxonomy));
			}

			if(forCS)
			{
				TrackingTaskType csTask = getTrackingTaskTypeByName("CS", session);
				crit.add(Restrictions.eq("trackingTaskType", csTask));
			}
			if(forTaxonomyTransfer)
			{
				TrackingTaskType taxonomyTransferTask = getTrackingTaskTypeByName("TaxonomyTransfer", session);
				crit.add(Restrictions.eq("trackingTaskType", taxonomyTransferTask));
			}

			if(forDaily)
			{

				if(Datasheetflag != null && !Datasheetflag.isEmpty())
				{
					TrackingTaskType dailyTransferTask = getTrackingTaskTypeByName(Datasheetflag, session);
					crit.add(Restrictions.eq("trackingTaskType", dailyTransferTask));

				}
				else
				{
					TrackingTaskType dailyTransferTask = getTrackingTaskTypeByName("New", session);
					TrackingTaskType NPITransferTask = getTrackingTaskTypeByName("NPI(Flag from Sourcing)", session);
					crit.add(Restrictions.or(Restrictions.eq("trackingTaskType", NPITransferTask), Restrictions.eq("trackingTaskType", dailyTransferTask)));
				}
			}

			if(forUpdate)
			{
				TrackingTaskType updateTransferTask = getTrackingTaskTypeByName("Update", session);
				crit.add(Restrictions.eq("trackingTaskType", updateTransferTask));
			}
			if(forFast)
			{
				TrackingTaskType updateFastTask = getTrackingTaskTypeByName("Fast", session);
				crit.add(Restrictions.eq("trackingTaskType", updateFastTask));
			}
			Criteria docCriteria = crit.createCriteria("document");

			if(deliverydate != null && deliverydate.trim().length() > 0 && vendor != null && vendor.trim().length() > 0)
			{
				Criteria pdfCriteria = docCriteria.createCriteria("pdf");
				pdfCriteria.createCriteria("supplierUrl").createCriteria("supplier").add(Restrictions.eq("name", vendor));

			}
			else
			{
				if(deliverydate != null && deliverydate.trim().length() > 0)
				{
					// docCriteria.createCriteria("pdf").add(Expression.sql("to_char({alias}.DOWNLOAD_DATE,'mm\\dd\\YYYY')=to_char(to_date(?,'MM\\dd\\yyyy'),'mm\\dd\\YYYY')",
					// deliverydate, Hibernate.STRING));// eq("downloadDate",deliverydate));
				}
				if(vendor != null && vendor.trim().length() > 0)
				{
					docCriteria.createCriteria("pdf").createCriteria("supplierUrl").createCriteria("supplier").add(Restrictions.eq("name", vendor));
				}
			}
			docCriteria.createCriteria("documentType").add(Restrictions.eq("name", "Datasheet"));
			crit.setMaxResults(100);
			List<TrackingParametric> documents = crit.list();

			if(forDaily)
			{
				AppContext.AllnewDocuments = documents;
			}

			if(pagenum == 0 && max == 0)
				return documents;

			return crit.setFirstResult(pagenum * max).setMaxResults(max).list();
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return null;
	}

	public static List<Document> getParamDevPdfs(long userId, boolean forCS, boolean forTaxonomyTransfer, boolean forDaily, boolean forUpdate, boolean forFast, Session session) throws Exception
	{
		Criteria crit = session.createCriteria(TrackingParametric.class);
		crit.createCriteria("trackingTaskStatus").add(Restrictions.eq("name", StatusName.assigned));
		// ParametricSearchQueryUtil.getUserByUserId(userId, session);
		if(userId != 0)
			crit.add(Restrictions.eq("parametricUserId", userId));
		// crit.add(Restrictions.eq("parametricUserId", userId));
		if(forCS)
		{
			TrackingTaskType csTask = getTrackingTaskTypeByName("CS", session);
			crit.add(Restrictions.eq("trackingTaskType", csTask));
		}
		if(forTaxonomyTransfer)
		{
			TrackingTaskType taxonomyTransferTask = getTrackingTaskTypeByName("TaxonomyTransfer", session);
			crit.add(Restrictions.eq("trackingTaskType", taxonomyTransferTask));
		}
		if(forDaily)
		{
			TrackingTaskType dailyTransferTask = getTrackingTaskTypeByName("New", session);
			TrackingTaskType NPITransferTask = getTrackingTaskTypeByName("NPI(Flag from Sourcing)", session);
			crit.add(Restrictions.or(Restrictions.eq("trackingTaskType", NPITransferTask), Restrictions.eq("trackingTaskType", dailyTransferTask)));
		}
		if(forUpdate)
		{
			TrackingTaskType updateTransferTask = getTrackingTaskTypeByName("Update", session);
			crit.add(Restrictions.eq("trackingTaskType", updateTransferTask));
		}
		if(forFast)
		{
			TrackingTaskType updateFastTask = getTrackingTaskTypeByName("Fast", session);
			crit.add(Restrictions.eq("trackingTaskType", updateFastTask));
		}
		crit.createCriteria("document").createCriteria("documentType").add(Restrictions.eq("name", "Datasheet"));

		crit.addOrder(Order.desc("prioriy"));
		crit.setProjection(Projections.property("document"));

		List<Document> documents = crit.list();
		return documents;
	}

	public static List<Document> getParamDevPdfs(long userId, String vendor, String taxonomy, boolean forCS, boolean forTaxonomyTransfer, boolean forDaily, boolean forUpdate, boolean forFast, Session session) throws Exception
	{
		Criteria crit = session.createCriteria(TrackingParametric.class);
		crit.createCriteria("trackingTaskStatus").add(Restrictions.eq("name", StatusName.assigned));
		// ParametricSearchQueryUtil.getUserByUserId(userId, session);
		if(userId != 0)
			crit.add(Restrictions.eq("parametricUserId", userId));
		// crit.add(Restrictions.eq("parametricUserId", userId));
		if(taxonomy != null && taxonomy.trim().length() > 0)
		{
			crit.createCriteria("pl").add(Restrictions.eq("name", taxonomy));
		}
		if(forCS)
		{
			TrackingTaskType csTask = getTrackingTaskTypeByName("CS", session);
			crit.add(Restrictions.eq("trackingTaskType", csTask));
		}
		if(forTaxonomyTransfer)
		{
			TrackingTaskType taxonomyTransferTask = getTrackingTaskTypeByName("TaxonomyTransfer", session);
			crit.add(Restrictions.eq("trackingTaskType", taxonomyTransferTask));
		}
		if(forDaily)
		{
			TrackingTaskType dailyTransferTask = getTrackingTaskTypeByName("New", session);
			TrackingTaskType NPITransferTask = getTrackingTaskTypeByName("NPI(Flag from Sourcing)", session);
			crit.add(Restrictions.or(Restrictions.eq("trackingTaskType", NPITransferTask), Restrictions.eq("trackingTaskType", dailyTransferTask)));
		}
		if(forUpdate)
		{
			TrackingTaskType updateTransferTask = getTrackingTaskTypeByName("Update", session);
			crit.add(Restrictions.eq("trackingTaskType", updateTransferTask));
		}
		if(forFast)
		{
			TrackingTaskType updateFastTask = getTrackingTaskTypeByName("Fast", session);
			crit.add(Restrictions.eq("trackingTaskType", updateFastTask));
		}
		Criteria docCriteria = crit.createCriteria("document");
		if(vendor != null && vendor.trim().length() > 0)
		{
			docCriteria.createCriteria("pdf").createCriteria("supplierUrl").createCriteria("supplier").add(Restrictions.eq("name", vendor));
		}
		docCriteria.createCriteria("documentType").add(Restrictions.eq("name", "Datasheet"));

		crit.addOrder(Order.desc("prioriy"));
		crit.setProjection(Projections.property("document"));
		return crit.list();
		// List<Document> documents = crit.list();
		// return documents;
	}

	public static TrackingTaskType getTrackingTaskTypeByName(String name, Session session)
	{
		Criteria crit = session.createCriteria(TrackingTaskType.class);
		crit.add(Restrictions.eq("name", name));
		return (TrackingTaskType) crit.uniqueResult();
	}

	public static ApprovedParametricValue insertNewApprovedValue(PlFeature plFeature, String valueStr, long isApprovedByQA, Session session)
	{
		Value value = ParaQueryUtil.getValueByName(valueStr, session);
		if(value == null)
		{
			// insert value in value table
			value = new Value();
			value.setId(QueryUtil.getRandomID());
			value.setStoreDate(new Date());
			value.setValue(valueStr);
			session.save(value);
		}
		//
		ApprovedParametricValue approvedParametricValue = new ApprovedParametricValue();
		approvedParametricValue.setId(QueryUtil.getRandomID());
		approvedParametricValue.setFullValue(valueStr);
		approvedParametricValue.setPlFeature(plFeature);
		approvedParametricValue.setFromValue(value);
		approvedParametricValue.setIsApproved(isApprovedByQA);
		session.save(approvedParametricValue);
		//
		return approvedParametricValue;
	}

	@SuppressWarnings("unchecked")
	private static Value getValueByName(String valueStr, Session session)
	{
		Criteria crit = session.createCriteria(Value.class);
		crit.add(Restrictions.eq("value", valueStr));
		//
		List<Value> list = crit.list();
		return list.size() > 0 ? (Value) crit.list().get(0) : null;
	}

	public static void addTrackingParamDocApprov(Document document, Long clientGroupId, ApprovedParametricValue approvedParametricValue, String groupFullValue, String trackingStatus, String ddComment, Session session) throws Exception
	{
		TrackingParamDocApprov trackingParamDocApprov = getTrackingParamDocApprov(clientGroupId, document, session);
		if(trackingParamDocApprov == null)
		{
			trackingParamDocApprov = new TrackingParamDocApprov();
			trackingParamDocApprov.setId(QueryUtil.getRandomID());
			trackingParamDocApprov.setDocument(document);
		}
		trackingParamDocApprov.setApprovedGroupId(clientGroupId);
		trackingParamDocApprov.setApprovedParametricValue(approvedParametricValue);
		if(trackingStatus != null)
			trackingParamDocApprov.setTrackingTaskStatus(getTrackingTaskStatus(session, trackingStatus/* "Pending" */));
		else
			trackingParamDocApprov.setTrackingTaskStatus(null);
		trackingParamDocApprov.setTrackingTaskQaStatus(null);
		trackingParamDocApprov.setApprovedGroup(groupFullValue);
		trackingParamDocApprov.setDdComment(ddComment);
		// trackingParamDocApprov.setPartNumber(component.getPartNumber());
		session.saveOrUpdate(trackingParamDocApprov);
	}

	public static void addTrackingParametricApprovedValue(ApprovedParametricValue approvedVal, Long groupId, String groupFullValue, String trackingStatus, Session session) throws Exception
	{

		TrackingParametricApprovVal trackingParametricApprovVal = getTrackingParametricApprovVal(groupId, session);
		if(trackingParametricApprovVal == null)
		{
			trackingParametricApprovVal = new TrackingParametricApprovVal();
			trackingParametricApprovVal.setId(QueryUtil.getRandomID());
		}
		trackingParametricApprovVal.setApprovedParametricValue(approvedVal);
		trackingParametricApprovVal.setApprovedGroupId(groupId);
		trackingParametricApprovVal.setApprovedGroup(groupFullValue);
		trackingParametricApprovVal.setTrackingTaskQaStatus(getTrackingTaskQaStatus(trackingStatus /* "Pending" */, session));

		session.saveOrUpdate(trackingParametricApprovVal);
	}

	public static void addTrackingParamDocApprov(Document document, Long clientGroupId, ApprovedParametricValue approvedParametricValue, String groupFullValue, String trackingStatus, Session session) throws Exception
	{
		TrackingParamDocApprov trackingParamDocApprov = getTrackingParamDocApprov(clientGroupId, document, session);
		if(trackingParamDocApprov == null)
		{
			trackingParamDocApprov = new TrackingParamDocApprov();
			trackingParamDocApprov.setId(QueryUtil.getRandomID());
			trackingParamDocApprov.setDocument(document);
		}
		trackingParamDocApprov.setApprovedGroupId(clientGroupId);
		trackingParamDocApprov.setApprovedParametricValue(approvedParametricValue);
		if(trackingStatus != null)
			trackingParamDocApprov.setTrackingTaskStatus(getTrackingTaskStatus(session, trackingStatus/* "Pending" */));
		else
			trackingParamDocApprov.setTrackingTaskStatus(null);
		trackingParamDocApprov.setTrackingTaskQaStatus(null);
		trackingParamDocApprov.setApprovedGroup(groupFullValue);
		// trackingParamDocApprov.setPartNumber(component.getPartNumber());
		session.saveOrUpdate(trackingParamDocApprov);
	}

	public static void addTrackingParamDocApprov(Document document, Long clientGroupId, ApprovedParametricValue approvedParametricValue, String groupFullValue, TrackingTaskStatus trackingTaskStatus, TrackingTaskQaStatus trackingTaskQaStatus,
			Session session) throws Exception
	{
		TrackingParamDocApprov trackingParamDocApprov = getTrackingParamDocApprov(clientGroupId, document, session);
		if(trackingParamDocApprov == null)
		{
			trackingParamDocApprov = new TrackingParamDocApprov();
			trackingParamDocApprov.setId(QueryUtil.getRandomID());
			trackingParamDocApprov.setDocument(document);
		}
		trackingParamDocApprov.setApprovedGroupId(clientGroupId);
		trackingParamDocApprov.setApprovedParametricValue(approvedParametricValue);
		if(trackingTaskStatus != null)
			trackingParamDocApprov.setTrackingTaskStatus(trackingTaskStatus);
		else
			trackingParamDocApprov.setTrackingTaskStatus(null);
		if(trackingTaskQaStatus != null)
			trackingParamDocApprov.setTrackingTaskQaStatus(trackingTaskQaStatus);
		else
			trackingParamDocApprov.setTrackingTaskQaStatus(null);
		trackingParamDocApprov.setTrackingTaskQaStatus(null);
		trackingParamDocApprov.setApprovedGroup(groupFullValue);
		// trackingParamDocApprov.setPartNumber(component.getPartNumber());
		session.saveOrUpdate(trackingParamDocApprov);
	}

	public static TrackingTaskQaStatus getTrackingTaskQaStatus(String name, Session session)
	{
		final Criteria crit = session.createCriteria(TrackingTaskQaStatus.class);
		crit.add(Restrictions.eq("name", name));
		TrackingTaskQaStatus trackingTaskQaStatus = (TrackingTaskQaStatus) crit.uniqueResult();
		return trackingTaskQaStatus;
	}

	public static Document getDocumentBySeUrl(String seUrl, Session session)
	{
		Document doc = null;

		// Criteria crit = session.createCriteria(Document.class);
		// if(seUrl.endsWith(".pdf"))
		// crit.createCriteria("pdf").add(Restrictions.eq("seUrl", seUrl));
		// else
		// crit.createCriteria("nonPdf").add(Restrictions.eq("seUrl", seUrl));
		// doc = (Document) crit.uniqueResult();
		// return doc;
		//
		// String latestLink = "";

		try
		{
			session = SessionUtil.getSession();
			SQLQuery query = session.createSQLQuery("select AUTOMATION2.GET_DOCID_BY_PDFURL('" + seUrl + "') from dual");
			long docId = ((BigDecimal) query.uniqueResult()).longValue();
			Criteria crit = session.createCriteria(Document.class);
			crit.add(Restrictions.eq("id", docId));
			doc = (Document) crit.uniqueResult();
			return doc;

		}catch(NullPointerException ex)
		{
			return doc;
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return doc;

	}

	public static int getCountOfPartsBySeUrl(String seUrl, Session session)
	{
		Criteria crit = session.createCriteria(PartComponent.class);
		Criteria documentCriteria = crit.createCriteria("document");
		if(seUrl.endsWith(".pdf"))
			documentCriteria.createCriteria("pdf").add(Restrictions.eq("seUrl", seUrl));
		else
			documentCriteria.createCriteria("nonPdf").add(Restrictions.eq("seUrl", seUrl));
		Integer count = (Integer) crit.setProjection(Projections.rowCount()).uniqueResult();
		return count;
	}

	/*
	 * @SuppressWarnings("unchecked") public static int getCountTrackingParamDocApprova(Session session, Long idTrackingParamDocApprov) throws
	 * Exception { final Criteria crit = session.createCriteria(TrackingParamDocApprov.class); crit.setProjection(Projections.count("id")).list();
	 * List<Integer> list = crit.list(); if(list == null) return 0; return list.get(0);
	 * 
	 * }
	 */

	@SuppressWarnings("unchecked")
	public static List<TrackingParamDocApprov> getTrackingParamDocApprovList(Session session, String supplierName, String seUrl, String aprrovalStatus, String featureName, String fullValue, String taxonomy, Date storeDate, long userId)
			throws Exception
	{

		final Criteria crit = session.createCriteria(TrackingParamDocApprov.class);
		Criteria criteria = crit.createCriteria("approvedParametricValue");
		Criteria fetCrit = criteria.createCriteria("plFeature");
		List<Pl> pls = getPlsByTlId(userId, session);
		System.out.println("pls of user = " + pls.size());
		if(pls.size() > 0)
			fetCrit.add(Restrictions.in("pl", pls));
		else
			return new ArrayList<TrackingParamDocApprov>();

		if(!taxonomy.equals(""))
		{
			fetCrit.createCriteria("pl").add(Restrictions.eq("name", taxonomy));
		}

		if(!aprrovalStatus.equals(""))
		{
			crit.createCriteria("trackingTaskStatus").add(Restrictions.eq("name", aprrovalStatus));
		}
		else
		{
			crit.add(Restrictions.isNotNull("trackingTaskStatus"));
		}
		if(!fullValue.equals(""))
		{
			crit.add(Restrictions.ilike("approvedGroup", fullValue, MatchMode.START));
		}
		if(!featureName.equals(""))
		{
			fetCrit.createCriteria("feature").add(Restrictions.ilike("name", featureName, MatchMode.START));
		}

		if(storeDate != null)
		{

			final Calendar cal = Calendar.getInstance();
			cal.setTime(storeDate);
			cal.set(Calendar.MILLISECOND, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.HOUR_OF_DAY, 0);

			criteria.add(Restrictions.between("storeDate", cal.getTime(), addDay(storeDate)));

		}

		if(!seUrl.equals(""))
		{
			Criteria docCriteria = crit.createCriteria("document");
			Criteria pdfCrit = null;
			if(seUrl.endsWith(".pdf"))
				pdfCrit = docCriteria.createCriteria("pdf");
			else
				pdfCrit = docCriteria.createCriteria("nonPdf");
			pdfCrit.add(Restrictions.eq("seUrl", seUrl));
		}
		List<TrackingParamDocApprov> listTrackingParamDocApprovs = crit.list();

		// this block is used to get only one document for value
		List<TrackingParamDocApprov> trackingParamDocApprovs = new ArrayList<TrackingParamDocApprov>();
		List<Long> groupIds = new ArrayList<Long>();
		for(TrackingParamDocApprov trackingParamDocApprov : listTrackingParamDocApprovs)
		{
			TrackingTaskQaStatus trackingTaskQaStatus = trackingParamDocApprov.getTrackingTaskQaStatus();
			if(trackingTaskQaStatus != null && trackingTaskQaStatus.getName().equals("Approved"))
				continue;
			Long groupId = trackingParamDocApprov.getApprovedGroupId();
			if(groupIds.contains(groupId))
				continue;
			groupIds.add(groupId);
			trackingParamDocApprovs.add(trackingParamDocApprov);
		}
		groupIds.clear();
		// end

		if(!supplierName.equals(""))
		{
			List<TrackingParamDocApprov> listtrTrackingParamDocApprovs = new ArrayList<TrackingParamDocApprov>();
			for(TrackingParamDocApprov trackingParamDocApprov : trackingParamDocApprovs)
			{
				Document document = trackingParamDocApprov.getDocument();
				SupplierUrl supplierUrl = null;
				if(document.getPdf() != null)
				{
					supplierUrl = document.getPdf().getSupplierUrl();
					if(supplierUrl.getSupplier().getName().equals(supplierName))
						listtrTrackingParamDocApprovs.add(trackingParamDocApprov);
				}
				else
				{
					supplierUrl = document.getNonPdf().getSupplierUrl();
					if(supplierUrl.getSupplier().getName().equals(supplierName))
						listtrTrackingParamDocApprovs.add(trackingParamDocApprov);

				}
			}
			return listtrTrackingParamDocApprovs;
		}
		return trackingParamDocApprovs;

	}

	@SuppressWarnings("unchecked")
	public static List<TrackingParamDocApprov> getTrackingParamDocApprovList(Session session, String engName, String task, String supplierName, String seUrl, String aprrovalStatus, String featureName, String fullValue, String taxonomy,
			Date storeDate, long tlId) throws Exception
	{
		final Criteria crit = session.createCriteria(TrackingParamDocApprov.class);
		Criteria criteria = crit.createCriteria("approvedParametricValue");
		Criteria fetCrit = criteria.createCriteria("plFeature");
		List<Pl> pls = getPlsByTlId(tlId, session);
		System.out.println("pls of user = " + pls.size());
		if(pls.size() > 0)
			fetCrit.add(Restrictions.in("pl", pls));
		else
			return new ArrayList<TrackingParamDocApprov>();

		if(!taxonomy.equals(""))
		{
			fetCrit.createCriteria("pl").add(Restrictions.eq("name", taxonomy));
		}

		if(!aprrovalStatus.equals(""))
		{
			crit.createCriteria("trackingTaskStatus").add(Restrictions.eq("name", aprrovalStatus));
		}
		else
		{
			crit.add(Restrictions.isNotNull("trackingTaskStatus"));
			// crit.createCriteria("trackingTaskStatus").add(Restrictions.in("name", new String[]{"Pending", "Wrong Separation"}));
		}
		if(!fullValue.equals(""))
		{
			crit.add(Restrictions.ilike("approvedGroup", fullValue, MatchMode.START));
		}
		if(!featureName.equals(""))
		{
			fetCrit.createCriteria("feature").add(Restrictions.ilike("name", featureName, MatchMode.START));
		}

		if(storeDate != null)
		{
			final Calendar cal = Calendar.getInstance();
			cal.setTime(storeDate);
			cal.set(Calendar.MILLISECOND, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			criteria.add(Restrictions.between("storeDate", cal.getTime(), addDay(storeDate)));
		}

		if(!seUrl.equals(""))
		{
			Criteria docCriteria = crit.createCriteria("document");
			Criteria pdfCrit = null;
			if(seUrl.endsWith(".pdf"))
				pdfCrit = docCriteria.createCriteria("pdf");
			else
				pdfCrit = docCriteria.createCriteria("nonPdf");
			pdfCrit.add(Restrictions.eq("seUrl", seUrl));
		}

		List<TrackingParamDocApprov> listTrackingParamDocApprovs = crit.list();
		List<TrackingParamDocApprov> trackingParamDocApprovs = listTrackingParamDocApprovs;
		if(!supplierName.equals(""))
		{
			List<TrackingParamDocApprov> listtrTrackingParamDocApprovs = new ArrayList<TrackingParamDocApprov>();
			for(TrackingParamDocApprov trackingParamDocApprov : trackingParamDocApprovs)
			{
				Document document = trackingParamDocApprov.getDocument();
				SupplierUrl supplierUrl = null;
				if(document.getPdf() != null)
				{
					supplierUrl = document.getPdf().getSupplierUrl();
					if(supplierUrl.getSupplier().getName().equals(supplierName))
						listtrTrackingParamDocApprovs.add(trackingParamDocApprov);
				}
				else
				{
					supplierUrl = document.getNonPdf().getSupplierUrl();
					if(supplierUrl.getSupplier().getName().equals(supplierName))
						listtrTrackingParamDocApprovs.add(trackingParamDocApprov);

				}
			}
			trackingParamDocApprovs = listtrTrackingParamDocApprovs;
		}

		if(task != null && !task.trim().isEmpty())
		{
			TrackingTaskType taskType = getTrackingTaskTypeByName(task, session);
			if(taskType != null)
			{
				List<TrackingParamDocApprov> statusTrackingParamDocApprovs = new ArrayList<TrackingParamDocApprov>();
				for(TrackingParamDocApprov docApp : trackingParamDocApprovs)
				{
					Criteria trackingParametricCriteria = session.createCriteria(TrackingParametric.class);
					trackingParametricCriteria.add(Restrictions.eq("document", docApp.getDocument()));
					trackingParametricCriteria.add(Restrictions.eq("trackingTaskType", taskType));
					TrackingParametric parametric = (TrackingParametric) trackingParametricCriteria.uniqueResult();
					if(parametric != null)
						statusTrackingParamDocApprovs.add(docApp);
				}
				trackingParamDocApprovs = statusTrackingParamDocApprovs;
			}
		}

		if(engName != null && !engName.trim().isEmpty())
		{
			GrmUser user = getUserByExactName(engName, "");
			if(user != null)
			{
				List<TrackingParamDocApprov> engNameTrackingParamDocApprovs = new ArrayList<TrackingParamDocApprov>();
				for(TrackingParamDocApprov docApp : trackingParamDocApprovs)
				{
					Criteria trackingParametricCriteria = session.createCriteria(TrackingParametric.class);
					trackingParametricCriteria.add(Restrictions.eq("document", docApp.getDocument()));
					trackingParametricCriteria.add(Restrictions.eq("parametricUserId", user.getId()));
					TrackingParametric parametric = (TrackingParametric) trackingParametricCriteria.uniqueResult();
					if(parametric != null)
						engNameTrackingParamDocApprovs.add(docApp);
				}
				trackingParamDocApprovs = engNameTrackingParamDocApprovs;
			}
		}
		return trackingParamDocApprovs;
	}

	public static boolean getPdfBySeurl(Session session, String seurl) throws Exception
	{

		final Criteria crit = session.createCriteria(Pdf.class);
		crit.add(Restrictions.eq("seUrl", seurl));
		Pdf pdf = (Pdf) crit.uniqueResult();
		if(pdf == null)
		{
			return false;
		}
		return true;

	}

	@SuppressWarnings("unchecked")
	public static List<TrackingTaskQaStatus> getTrackingTaskQaStatusList(Session session)
	{

		final Criteria crit = session.createCriteria(TrackingTaskQaStatus.class);
		List<TrackingTaskQaStatus> listqQaStatus = crit.list();

		return listqQaStatus;

	}

	@SuppressWarnings("unchecked")
	public static long getGroupByPkgApprovedParametricValue(PkgApprovedValue val, Session session)
	{
		String queryString = "select OUTER_GROUP.GROUP_ID FROM PKG_VALUE_GROUP outer_group," + " (SELECT   GROUP_ID inner_group_id" + " FROM   PKG_VALUE_GROUP" + " WHERE   pkg_approved_value_id = " + val.getId() + " )inner_query"
				+ " where inner_query.inner_group_id = OUTER_GROUP.GROUP_ID" + " GROUP BY OUTER_GROUP.GROUP_ID" + " HAVING   COUNT (OUTER_GROUP.GROUP_ID) = 1";

		SQLQuery query = session.createSQLQuery(queryString);
		List<BigDecimal> list = query.list();
		if(list.size() == 0)
			return -1;
		long groupId = list.get(0).longValue();
		return groupId;
	}

	@SuppressWarnings("unchecked")
	public static List<PartComponent> getPartsByPkgMainDataColumn(long col, int from, int max, long groupId, Session session)
	{
		int last = from + max;
		String queryStr = "select * from" + " (" + " select * from" + " (" + " select rownum rn,c.* from PKG_MAIN_DATA  p,component c where P.COM_ID = C.COM_ID and P.COL_" + col + " = " + groupId + " )" + " WHERE   ROWNUM <= " + last + " )"
				+ " where rn > " + from;
		SQLQuery query = session.createSQLQuery(queryStr);
		query.addEntity(PartComponent.class);
		List<PartComponent> list = query.list();
		return list;
	}


	@SuppressWarnings("unchecked")
	public static int getPartsCountByPkgMainDataColumn(long col, long groupId, Session session)
	{
		Criteria crit = session.createCriteria(PkgMainData.class);
		crit.add(Restrictions.eq("col" + col, groupId));
		crit.setProjection(Projections.count("id"));
		List<Integer> count = crit.list();
		return count.get(0);
	}

	public static TrackingPkgDocApprov getTrackingPkgDocApproval(Session session, long docuomentId, long pkgApprovalvalueId)
	{

		final Criteria crit = session.createCriteria(TrackingPkgDocApprov.class);
		crit.createCriteria("pkgApprovedValue").add(Restrictions.eq("id", pkgApprovalvalueId));
		crit.createCriteria("document").add(Restrictions.eq("id", docuomentId));
		TrackingPkgDocApprov listTrackingPkgDocApprov = (TrackingPkgDocApprov) crit.uniqueResult();

		return listTrackingPkgDocApprov;
	}

	public static void updateTrackinPkgApproval(Session session, TrackingPkgDocApprov trackingPkgDocApproval)
	{
		session.saveOrUpdate(trackingPkgDocApproval);
		session.beginTransaction().commit();

	}

	@SuppressWarnings("unchecked")
	public static List<TrackingPkgDocApprov> getTrackingPkgDocApproval(Session session, String supplierName, String seUrl, String approvalStatus, String featureName, String fullValue/*
																																													 * ,
																																													 * String
																																													 * taxonomy
																																													 */, Date storeDate/*
																																																		 * ,
																																																		 * int
																																																		 * from
																																																		 * ,
																																																		 * int
																																																		 * to
																																																		 */) throws Exception
	{

		final Criteria crit = session.createCriteria(TrackingPkgDocApprov.class);
		Criteria createCriteria = crit.createCriteria("pkgApprovedValue");
		// Criteria fetCrit = createCriteria.createCriteria("plFeature");
		if(!approvalStatus.equals("") /*
									 * || !taxonomy.equals("") || !fullValue.equals("") || !featureName.equals("")
									 */)
		{
			crit.createCriteria("trackingTaskStatus").add(Restrictions.ilike("name", approvalStatus, MatchMode.START));
		}
		// if(!taxonomy.equals(""))
		// {
		// fetCrit.createCriteria("pl").add(Restrictions.eq("name", taxonomy));
		// }
		if(!featureName.equals(""))
		{
			createCriteria.createCriteria("pkgFeature").add(Restrictions.ilike("name", featureName, MatchMode.START));
		}
		if(!fullValue.equals(""))
		{
			createCriteria.createCriteria("pkgValue").add(Restrictions.ilike("value", fullValue, MatchMode.START));
		}

		// storedate
		if(storeDate != null)
		{

			final Calendar cal = Calendar.getInstance();
			cal.setTime(storeDate);
			cal.set(Calendar.MILLISECOND, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.HOUR_OF_DAY, 0);

			createCriteria.add(Restrictions.between("storeDate", cal.getTime(), addDay(storeDate)));

		}

		if(!seUrl.equals(""))
		{
			Criteria docCriteria = crit.createCriteria("document");
			Criteria pdfCrit = null;
			if(seUrl.endsWith(".pdf"))
				pdfCrit = docCriteria.createCriteria("pdf");
			else
				pdfCrit = docCriteria.createCriteria("nonPdf");
			pdfCrit.add(Restrictions.eq("seUrl", seUrl));
		}
		List<TrackingPkgDocApprov> listTrackingPkgDocApprovs = crit.list();

		if(!supplierName.equals(""))
		{
			List<TrackingPkgDocApprov> listtrTrackingParamDocApprovs = new ArrayList<TrackingPkgDocApprov>();
			for(TrackingPkgDocApprov trackingParamDocApprov : listTrackingPkgDocApprovs)
			{
				Document document = trackingParamDocApprov.getDocument();
				SupplierUrl supplierUrl = null;
				if(document.getPdf() != null)
				{
					supplierUrl = document.getPdf().getSupplierUrl();
					if(supplierUrl.getSupplier().getName().equals(supplierName))
						listtrTrackingParamDocApprovs.add(trackingParamDocApprov);
				}
				else
				{
					supplierUrl = document.getNonPdf().getSupplierUrl();
					if(supplierUrl.getSupplier().getName().equals(supplierName))
						listtrTrackingParamDocApprovs.add(trackingParamDocApprov);

				}
			}
			return listtrTrackingParamDocApprovs;
		}
		return listTrackingPkgDocApprovs;

	}

	@SuppressWarnings("unchecked")
	public static List<PartComponent> getComponentByPlNameAndDocuomentIDList(Session session, Long docuomentId, String taxonomy)
	{
		final Criteria crit = session.createCriteria(PartComponent.class);
		if(!taxonomy.equals(""))
		{
			crit.createCriteria("supplierPl").createCriteria("pl").add(Restrictions.ilike("name", taxonomy, MatchMode.START));
		}

		crit.createCriteria("document").add(Restrictions.eq("id", docuomentId));
		// if(SupplierName != null)
		// crit.createCriteria("supplierPl").createCriteria("supplier").add(Restrictions.eq("name",
		// SupplierName));

		List<PartComponent> listComponent = crit.list();
		return listComponent;
	}

	// @SuppressWarnings("unchecked")
	// public static List<PkgValueGroup> getPkgValueGroupByGroupId(Long groupId,
	// Session session) throws Exception {
	// try {
	// Criteria crit = session.createCriteria(PkgValueGroup.class);
	//
	// crit.add(Restrictions.eq("groupId", groupId));
	//
	// return crit.list();
	// } catch (Exception e) {
	// throw ParametricDevServerUtil.getCatchException(e);
	// }
	//
	// }

	// @SuppressWarnings("unchecked")
	// public static List<String> getpkgValuesByGroupId(PkgValueGroup
	// pkgValueGroup)
	// throws Exception {
	// Session se = SessionUtil.getSession();
	// try {
	//
	// Criteria crit = se.createCriteria(PkgApprovedValue.class);
	// crit.add(Restrictions.eq("id", pkgValueGroup.getPkgApprovedValue()
	// .getId()));
	// List<String> val = new ArrayList<String>();
	// for (PkgApprovedValue approvedValue : (List<PkgApprovedValue>) crit
	// .list()) {
	// val.add(approvedValue.getPkgValue().getValue());
	// }
	// return val;
	// } catch (Exception e) {
	//
	// throw ParametricDevServerUtil.getCatchException(e);
	// } finally {
	// SessionUtil.closeSession(se);
	// }
	// }

	public static GrmRole getUserType(String typeName) throws Exception
	{
		Session session2 = com.se.grm.db.SessionUtil.getSession();
		try
		{
			final Criteria crit = session2.createCriteria(GrmRole.class);
			crit.add(Restrictions.eq("name", typeName));
			final GrmRole usertype = (GrmRole) crit.uniqueResult();
			return usertype;
		}catch(Exception e)
		{
			throw ParametricDevServerUtil.getCatchException(e);
		}finally
		{
			com.se.grm.db.SessionUtil.closeSession(session2);
		}
	}

	public static GrmUser getUserByExactName(final String userName, String type) throws Exception
	{
		Session session2 = com.se.grm.db.SessionUtil.getSession();
		try
		{
			final Criteria crit = session2.createCriteria(GrmUser.class);
			crit.add(Restrictions.eq("fullName", userName));
			if(!type.equals(""))
				crit.add(Restrictions.eq("grmRole", getUserType(type)));
			final GrmUser user = (GrmUser) crit.uniqueResult();
			return user;
		}catch(Exception e)
		{

			throw ParametricDevServerUtil.getCatchException(e);

		}finally
		{
			com.se.grm.db.SessionUtil.closeSession(session2);
		}

	}

	public static long getUserIdByExactName(
	/* final Session session, */final String userName, String type) throws Exception
	{

		Session grmSession = com.se.grm.db.SessionUtil.getSession();
		try
		{
			final Criteria crit = grmSession.createCriteria(GrmUser.class);
			crit.add(Restrictions.eq("fullName", userName));
			if(!type.equals(""))
				crit.add(Restrictions.eq("grmRole", getUserType(type)));
			GrmUser userId = (GrmUser) crit.uniqueResult();
			if(userId != null)
				return userId.getId();
			return 0;
		}catch(Exception e)
		{

			throw ParametricDevServerUtil.getCatchException(e);

		}finally
		{
			com.se.grm.db.SessionUtil.closeSession(grmSession);
		}

	}

	public static long getUserIdByExactName(
	/* final Session session, */final String userName) throws Exception
	{

		Session grmSession = com.se.grm.db.SessionUtil.getSession();
		try
		{
			final Criteria crit = grmSession.createCriteria(GrmUser.class);
			crit.add(Restrictions.eq("fullName", userName));
			GrmUser userId = (GrmUser) crit.uniqueResult();
			if(userId != null)
				return userId.getId();
			return 0;
		}catch(Exception e)
		{

			throw ParametricDevServerUtil.getCatchException(e);

		}finally
		{
			com.se.grm.db.SessionUtil.closeSession(grmSession);
		}

	}

	public static GrmUser getGrmUserAndTypeByName(final Session session, final String userName, String type) throws Exception
	{

		// Session grmSession = com.se.grm.db.SessionUtil.getSession();
		try
		{
			final Criteria crit = session.createCriteria(GrmUser.class);
			crit.add(Restrictions.eq("fullName", userName));
			if(!type.equals(""))
				crit.add(Restrictions.eq("grmRole", getUserType(type)));
			final GrmUser user = (GrmUser) crit.uniqueResult();
			return user;
		}catch(Exception e)
		{

			throw ParametricDevServerUtil.getCatchException(e);

		}finally
		{
			// com.se.grm.db.SessionUtil.closeSession(grmSession);
		}

	}

	public static GrmUser getGrmUserById(long userId, Session session)
	{

		Criteria crit = session.createCriteria(GrmUser.class);
		crit.add(Restrictions.eq("id", userId));
		return (GrmUser) crit.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public static int getTrackingParamUserPlRateCount(Session se, String TLName, String PLName, String UserName, Long Rate, boolean isDistributedTaxs, GrmUser grmTlUser) throws Exception
	{
		final Criteria crit = se.createCriteria(TrackingParamUserPlRate.class);
		crit.add(Restrictions.eq("type", "Parametric"));
		List<Pl> pls = ParaQueryUtil.getPlsByTlId(grmTlUser.getId(), se);

		if(pls.size() > 0)
			crit.add(Restrictions.in("pl", pls));
		else
			return 0;

		if(isDistributedTaxs)
		{

			System.out.println(isDistributedTaxs + " --------------- ");
			crit.add(Restrictions.isNull("userId"));
			System.out.println("blank 1");
		}

		if(!PLName.equals(""))
		{
			Pl pl = getPlByPlName(se, PLName);
			crit.add(Restrictions.eq("pl", pl));
		}

		if(!TLName.equals(""))
		{
			long TL = ParaQueryUtil.getUserIdByExactName(
			/* se, */TLName, "");

			crit.add(Restrictions.eq("tlId", TL));
		}

		if(!UserName.equals(""))
		{
			long user = ParaQueryUtil.getUserIdByExactName(
			/* se, */TLName, "");

			crit.add(Restrictions.eq("userId", user));
		}

		if(Rate != 0)
		{

			crit.add(Restrictions.eq("userRate", Rate));
		}

		crit.setProjection(Projections.rowCount());
		List<Integer> list = crit.list();
		if(list == null)
			return 0;

		return list.get(0);

	}

	public static Supplier getSupplierByExactName(final Session session, final String SupplierName) throws Exception
	{
		try
		{
			final Criteria crit = session.createCriteria(Supplier.class);
			crit.add(Restrictions.eq("name", SupplierName));
			final Supplier supplier = (Supplier) crit.uniqueResult();
			return supplier;
		}catch(Exception e)
		{
			throw ParametricDevServerUtil.getCatchException(e);
		}

	}

	@SuppressWarnings("unchecked")
	public static List<TrackingFeedback> getTrackingFeedbackBySeUrlFromNonPdf(final Session se, final String seUrl) throws Exception
	{
		try
		{
			final Criteria crit = se.createCriteria(TrackingFeedback.class);
			crit.createCriteria("document").createCriteria("nonPdf").add(Restrictions.eq("seUrl", seUrl));

			final List<TrackingFeedback> document = crit.list();

			return document;
		}catch(Exception e)
		{
			throw ParametricDevServerUtil.getCatchException(e);
		}

	}

	@SuppressWarnings("unchecked")
	public static List<TrackingFeedback> getTrackingFeedbackBySupplierNameFromNonPdf(final Session se, final String supplierName) throws Exception
	{
		try
		{
			final Criteria crit = se.createCriteria(TrackingFeedback.class);
			crit.createCriteria("document").createCriteria("nonPdf").createCriteria("supplierUrl").createCriteria("supplier").add(Restrictions.eq("name", supplierName));

			final List<TrackingFeedback> document = crit.list();

			return document;
		}catch(Exception e)
		{
			throw ParametricDevServerUtil.getCatchException(e);
		}
	}

	public static TrackingTaskStatus getTrackingTaskStatusByExactName(final Session session, final String TrackingTaskStatues) throws Exception
	{
		try
		{
			final Criteria crit = session.createCriteria(TrackingTaskStatus.class);
			crit.add(Restrictions.eq("name", TrackingTaskStatues));
			final TrackingTaskStatus taskStatus = (TrackingTaskStatus) crit.uniqueResult();
			return taskStatus;
		}catch(Exception e)
		{
			throw ParametricDevServerUtil.getCatchException(e);
		}

	}

	@SuppressWarnings("unchecked")
	public static List<TrackingTaskStatus> getAllTrackingTaskStatues(Session se)
	{
		Criteria crit = se.createCriteria(TrackingTaskStatus.class);
		return crit.list();
	}

	public static TrackingTaskStatus getTrackingTaskStatuesByExactName(Session se, String statuesName)
	{
		Criteria crit = se.createCriteria(TrackingTaskStatus.class);
		crit.add(Restrictions.eq("name", statuesName));

		return (TrackingTaskStatus) crit.uniqueResult();
	}

	public static TrackingTaskQaStatus getTrackingTaskQaStatusByExactName(Session se, String statuesName)
	{
		Criteria crit = se.createCriteria(TrackingTaskQaStatus.class);
		crit.add(Restrictions.eq("name", statuesName));

		return (TrackingTaskQaStatus) crit.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public static List<TrackingFeedbackType> getAllTrackingFeedBackType(Session se)
	{
		Criteria crit = se.createCriteria(TrackingFeedbackType.class);
		return crit.list();
	}

	@SuppressWarnings("unchecked")
	public static int getTrackingPkgTlVendoreCount(Session se, String supplierName, String userName) throws Exception
	{
		final Criteria crit = se.createCriteria(TrackingPkgTlVendor.class);
		crit.add(Restrictions.eq("type", "PKG"));
		if(!supplierName.equals(""))
		{

			Supplier supplier = getSupplierByExactName(se, supplierName);
			crit.add(Restrictions.eq("supplier", supplier));

		}
		if(!userName.equals(""))
		{

			GrmUser user = getUserByExactName(userName, "");
			crit.add(Restrictions.eq("userId", user.getId()));
		}

		crit.setProjection(Projections.count("id")).list();
		List<Integer> list = crit.list();
		if(list == null)
			return 0;
		return list.get(0);

	}

	@SuppressWarnings("unchecked")
	public static PartComponent getComponentByPartNumAndSupplierPl(String partNumber, SupplierPl supplierPl, Session session) throws Exception
	{
		try
		{
			final Criteria crit = session.createCriteria(PartComponent.class);
			crit.add(Restrictions.eq("partNumber", partNumber));
			crit.add(Restrictions.eq("supplierPl", supplierPl));
			List<PartComponent> list = crit.list();
			//
			return list.size() > 0 ? list.get(0) : null;
		}catch(Exception e)
		{
			throw ParametricDevServerUtil.getCatchException(e);
		}
	}

	public static void updateTrackingParametric(Session session, TrackingParametric trackingParametric)
	{
		session.saveOrUpdate(trackingParametric);
		session.beginTransaction().commit();

	}

	public static void updateTrackingPKG(Session session, TrackingPkg trackingPkg)
	{
		session.saveOrUpdate(trackingPkg);
		session.beginTransaction().commit();

	}

	public static void updateApprovedParametricValue(Session session, ApprovedParametricValue approvedParametricValue)
	{
		session.saveOrUpdate(approvedParametricValue);
		session.beginTransaction().commit();

	}

	@SuppressWarnings("unchecked")
	public static PartsParametric getPartsParametricByComponentAndFeatures(PartComponent component, Session session) throws Exception
	{
		final Criteria crit = session.createCriteria(PartsParametric.class);
		try
		{
			crit.add(Restrictions.eq("component", component));
			//
			List<PartsParametric> list = crit.list();
			return list.size() > 0 ? list.get(0) : null;

		}catch(Exception e)
		{
			throw ParametricDevServerUtil.getCatchException(e);
		}
	}

	public static TrackingTaskStatus getTrackingStatuesByName(final String statuesName, Session se) throws Exception
	{

		try
		{
			final Criteria crit = se.createCriteria(TrackingTaskStatus.class);
			crit.add(Restrictions.eq("name", statuesName));
			final TrackingTaskStatus status = (TrackingTaskStatus) crit.uniqueResult();
			return status;
		}catch(Exception e)
		{
			throw ParametricDevServerUtil.getCatchException(e);
		}

	}

	public static TrackingParametric getTrackingParametricByDocuomentId(Session session, Document document)
	{
		final Criteria crit = session.createCriteria(TrackingParametric.class);
		crit.add(Restrictions.eq("document", document));
		TrackingParametric trackingParametric = (TrackingParametric) crit.uniqueResult();
		return trackingParametric;
	}

	@SuppressWarnings("unchecked")
	public static List<TrackingFeedback> getTrackingFeedBackBySeurl(String seUrl, Session session)
	{
		final Criteria crit = session.createCriteria(TrackingFeedback.class);
		if(seUrl.endsWith(".pdf"))
			crit.createCriteria("document").createCriteria("pdf").add(Restrictions.eq("seUrl", seUrl));
		else
			crit.createCriteria("document").createCriteria("nonPdf").add(Restrictions.eq("seUrl", seUrl));

		crit.addOrder(Order.desc("feedbackDate"));
		List<TrackingFeedback> listTrackingFeedbacks = crit.list();
		// TrackingFeedback trackingFeedback = (TrackingFeedback)
		// crit.uniqueResult();

		return (List<TrackingFeedback>) listTrackingFeedbacks.get(0);
	}

	@SuppressWarnings("unchecked")
	public static List<String> getGenricPkgJedecList(Session session)
	{
		Criteria crit = session.createCriteria(PkgJedec.class);

		// crit.createAlias("pkgJedec", "val");

		// crit.setProjection(Projections.property("genericJedec"));
		// crit.setProjection(Projections.sqlProjection("GENERIC_JEDEC", null,
		// null));
		// Projection projection =
		// Projections.sqlProjection("(GENERIC_JEDEC||'/'||JEDEC_VARIATIONS) as diff",
		// new String[] {"diff"}, new Type[] {
		// Hibernate.STRING});

		Projection projection = Projections.sqlProjection("(GENERIC_JEDEC||'/'||JEDEC_VARIATIONS) as diff", new String[] { "diff" }, new Type[] { StringType.INSTANCE });
		List<String> list = crit.setProjection(projection).list();

		return list;
	}

	public static TrackingPkg getTrackingPkgByDocuomentId(Session session, Document document)
	{
		final Criteria crit = session.createCriteria(TrackingPkg.class);
		crit.add(Restrictions.eq("document", document));
		TrackingPkg trPkg = (TrackingPkg) crit.uniqueResult();
		return trPkg;
	}

	public static TrackingPkg getTrackingPkg(Session session, Long documentid)
	{
		final Criteria crit = session.createCriteria(TrackingPkg.class);
		crit.createCriteria("document").add(Restrictions.eq("id", documentid));
		TrackingPkg trPkg = (TrackingPkg) crit.uniqueResult();
		return trPkg;
	}

	public static void updateTrackingPkg(Session session, TrackingPkg trackingPkg)
	{
		session.saveOrUpdate(trackingPkg);
		session.beginTransaction().commit();

	}

	public static void updateTrackingFeedback(Session session, TrackingFeedback trackingFeedback)
	{
		session.saveOrUpdate(trackingFeedback);
		session.beginTransaction().commit();
	}

	public static PkgJedec getPkgJedecByStandardAndVariation(String standard, String variation, Session session)
	{
		final Criteria crit = session.createCriteria(PkgJedec.class);
		crit.add(Restrictions.eq("genericJedec", standard));
		crit.add(Restrictions.eq("jedecVariations", variation));
		// List<PkgJedec> retList = crit.list();
		PkgJedec pkgJedec = (PkgJedec) crit.uniqueResult();
		/*
		 * if(retList.size() > 0) return retList.get(0);
		 */
		return pkgJedec;
	}

	public static TrackingTaskStatus updateTlReviewStatus(Session session, String status)
	{
		final Criteria crit = session.createCriteria(TrackingTaskStatus.class);
		crit.add(Restrictions.eq("name", status));
		TrackingTaskStatus trackingTaskStatus = (TrackingTaskStatus) crit.uniqueResult();
		return trackingTaskStatus;

	}

	public static TrackingParametric getTrackingParametric(Session session, Long docuomentId)
	{
		final Criteria crit = session.createCriteria(TrackingParametric.class);
		System.out.println("doc id = " + docuomentId);
		crit.createCriteria("document").add(Restrictions.eq("id", docuomentId));
		TrackingParametric trackingParametric = (TrackingParametric) crit.uniqueResult();
		return trackingParametric;

	}

	public static void insertNewFeedBack(Session session, TrackingFeedback trackingFeedback)
	{
		session.saveOrUpdate(trackingFeedback);
		session.beginTransaction().commit();

	}

	public static TrackingFeedbackType getTrackingFeedbackTypeByexactName(String trackingFeedbackTypeName, Session session)
	{
		Criteria criteria = session.createCriteria(TrackingFeedbackType.class);
		criteria.add(Restrictions.eq("name", trackingFeedbackTypeName));
		TrackingFeedbackType trackingFeedbackType = (TrackingFeedbackType) criteria.uniqueResult();
		return trackingFeedbackType;
	}

	public static TrackingTeam getTrackingTeamByExactName(String trackingTeamName, Session session)
	{
		Criteria criteria = session.createCriteria(TrackingTeam.class);
		criteria.add(Restrictions.eq("name", trackingTeamName));
		TrackingTeam trackingTeam = (TrackingTeam) criteria.uniqueResult();
		return trackingTeam;
	}

	public static void saveDataSheetAlert(TrackingDatasheetAlert trackingDatasheetAlert, Session session) throws Exception

	{
		session.saveOrUpdate(trackingDatasheetAlert);
		session.beginTransaction().commit();
	}

	public static int getCountTrackingSheetAlert(Session session, String partNumber) throws Exception
	{
		//
		// final Criteria crit =
		// session.createCriteria(TrackingDatasheetAlert.class);
		// crit.setProjection(Projections.count("id")).list();
		// List<Integer> list = crit.list();
		// if(list == null)
		// return 0;
		// return list.get(0);

		final Criteria crit = session.createCriteria(TrackingDatasheetAlert.class);
		if(!partNumber.equals(""))
			crit.createCriteria("comId").add(Restrictions.ilike("partNumber", partNumber, MatchMode.START));
		crit.add(Restrictions.eq("tlFlag", false));
		Integer count = (Integer) crit.setProjection(Projections.rowCount()).uniqueResult();
		return count;
	}

	@SuppressWarnings("unchecked")
	public static List<TrackingDatasheetAlert> getTrackingDatasheetAlertList(Session session, String partNumber, int from, int to) throws Exception
	{
		// if(partNumber.equals(""))
		// {
		// final Criteria crit =
		// session.createCriteria(TrackingDatasheetAlert.class);
		// final List<TrackingDatasheetAlert> list =
		// crit.setMaxResults(to).setFirstResult(from).list();
		// // final List<Condition> list =
		// crit.setProjection(Projections.count("id"));
		//
		// return list;
		// }

		final Criteria crit = session.createCriteria(TrackingDatasheetAlert.class);
		if(!partNumber.equals(""))
		{
			crit.createCriteria("comId").add(Restrictions.ilike("partNumber", partNumber, MatchMode.START));

		}
		crit.add(Restrictions.eq("tlFlag", false));
		final List<TrackingDatasheetAlert> list = crit.setMaxResults(to).setFirstResult(from).list();

		return list;

	}

	public static GrmUser getUserByUserId(long userId, Session session2)
	{
		if(userId != 0)
		{

			Criteria crit = session2.createCriteria(GrmUser.class);
			crit.add(Restrictions.eq("id", userId));
			GrmUser user = (GrmUser) crit.uniqueResult();
			return user;

		}
		return null;
	}

	public static TrackingDatasheetAlert getTrackingDatasheetAlertByObject(Session session, TrackingDatasheetAlert trackingDatasheetAlert)
	{
		Criteria crit = session.createCriteria(TrackingDatasheetAlert.class);
		crit.add(Restrictions.eq("id", trackingDatasheetAlert.getId()));
		TrackingDatasheetAlert trackingDatasheetAlert2 = (TrackingDatasheetAlert) crit.uniqueResult();
		return trackingDatasheetAlert2;
	}

	public static List<Pl> getPlByPLNameAndIsPL(Session session, boolean isPl)
	{
		final Criteria crit = session.createCriteria(Pl.class);

		crit.add(Restrictions.eq("isPl", isPl));
		return crit.list();
	}

	// -------------------------salah--------
	public static Multiplier getMultiplierByExactName(String multiplierName, Session session)
	{
		if(!multiplierName.equals(""))
		{
			Criteria crit = session.createCriteria(Multiplier.class);
			crit.add(Restrictions.eq("name", multiplierName));
			Multiplier multiplier = (Multiplier) crit.uniqueResult();
			return multiplier;
		}
		return null;
	}

	public static Unit getUnitByExactName(String unitName, Session session)
	{
		if(!unitName.equals(""))
		{
			Criteria crit = session.createCriteria(Unit.class);
			crit.add(Restrictions.eq("name", unitName));
			Unit unit = (Unit) crit.uniqueResult();
			return unit;
		}
		return null;
	}

	public static Condition getConditionByExactName(String conditionName, Session session)
	{
		if(!conditionName.equals(""))
		{
			Criteria crit = session.createCriteria(Condition.class);
			crit.add(Restrictions.eq("name", conditionName));
			Condition condition = (Condition) crit.uniqueResult();
			return condition;
		}
		return null;
	}

	public static ValueType getValueTypeByExactName(String valueTypeName, Session session)
	{
		if(!valueTypeName.equals(""))
		{
			Criteria crit = session.createCriteria(ValueType.class);
			crit.add(Restrictions.eq("name", valueTypeName));
			ValueType valueType = (ValueType) crit.uniqueResult();
			return valueType;
		}
		return null;
	}

	public static Value getValueByExactValue(String valueName, Session session)
	{
		if(!valueName.equals(""))
		{
			Criteria crit = session.createCriteria(Value.class);
			crit.add(Restrictions.eq("value", valueName));
			Value value = (Value) crit.uniqueResult();
			return value;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static MultiplierUnit getMultiplierUnitByExactMultiplierAndUnit(Multiplier multiplier, Unit unit, Session session)
	{
		Criteria crit = session.createCriteria(MultiplierUnit.class);
		if(multiplier != null)
		{
			crit.add(Restrictions.eq("multiplier", multiplier));
		}
		else
		{
			crit.add(Restrictions.isNull("multiplier"));
		}
		if(unit != null)
		{
			crit.add(Restrictions.eq("unit", unit));
		}
		else
		{
			crit.add(Restrictions.isNull("unit"));
		}
		// List<MultiplierUnit> list = crit.list();
		// if (list.size() > 0) {
		// return list.get(0);
		// }
		MultiplierUnit multiplierUnit = (MultiplierUnit) crit.uniqueResult();
		return multiplierUnit;
	}

	public static void saveMultiplier(Session session, Multiplier multiplier) throws Exception
	{
		session.saveOrUpdate(multiplier);
		session.beginTransaction().commit();
	}

	public static void updateMultiplier(Session session, Multiplier multiplier) throws Exception
	{
		session.update(multiplier);
		session.beginTransaction().commit();
	}

	public static void saveCondition(Session session, Condition condition) throws Exception
	{
		session.saveOrUpdate(condition);
		session.beginTransaction().commit();

	}

	public static void updateCondition(Session session, Condition condition) throws Exception
	{
		session.update(condition);
		session.beginTransaction().commit();

	}

	public static void saveSign(Sign sign, Session session)
	{
		session.saveOrUpdate(sign);
		session.beginTransaction().commit();
	}

	public static void saveUnit(Unit unit, Session session)
	{
		session.saveOrUpdate(unit);
		session.beginTransaction().commit();
	}

	public static void saveValueType(Session session, ValueType valueType) throws Exception
	{
		session.saveOrUpdate(valueType);
		session.beginTransaction().commit();

	}

	public static void updateValueType(Session session, ValueType valueType) throws Exception
	{
		session.update(valueType);
		session.beginTransaction().commit();

	}

	// public static TrackingPkg getTrackingPKGByPdf(Session session, Pdf pdf)
	// {
	// Criteria crit = session.createCriteria(TrackingPkg.class);
	// crit.createCriteria("document").add(Restrictions.eq("pdf", pdf));
	// TrackingPkg trackingPkg = (TrackingPkg) crit.uniqueResult();
	// return trackingPkg;
	// }

	public static TrackingParametric getTrackingParametricByDocument(Session session, Document document)
	{
		Criteria crit = session.createCriteria(TrackingParametric.class);
		crit.add(Restrictions.eq("document", document));
		TrackingParametric trackingParametric = (TrackingParametric) crit.uniqueResult();
		return trackingParametric;
	}

	public static TrackingFast getTrackingFastByPartNumberAndDocument(Document document, String partNumber, Session session)
	{
		Criteria crit = session.createCriteria(TrackingFast.class);
		crit.add(Restrictions.eq("document", document));
		crit.add(Restrictions.eq("partNumber", partNumber));
		TrackingFast trackingFast = (TrackingFast) crit.uniqueResult();
		return trackingFast;
	}

	public static TrackingFastStatus getTrackingFastStatusByExactName(String name, Session session) throws Exception
	{
		Criteria crit = session.createCriteria(TrackingFastStatus.class);
		crit.add(Restrictions.eq("name", name));
		TrackingFastStatus trackingFastStatus = (TrackingFastStatus) crit.uniqueResult();
		return trackingFastStatus;

	}

	@SuppressWarnings("unchecked")
	public static List<String> getGroupFullValueByFeatureNameandPlName(String featureName, String plName, Session session)
	{
		Criteria crit = session.createCriteria(PartsParametricValuesGroup.class);
		Criteria plFeatureCrit = crit.createCriteria("plFeature");
		//
		plFeatureCrit.createCriteria("feature").add(Restrictions.eq("name", featureName));
		//
		plFeatureCrit.createCriteria("pl").add(Restrictions.eq("name", plName));
		crit.setProjection(Projections.distinct(Projections.property("groupFullValue")));
		crit.add(Restrictions.ne("isApproved", 2L));
		List<String> fullValueList = crit.list();
		return fullValueList;
	}

	public static List<String> getGroupFullValueByPlFeature(PlFeature plFeature, Session session)
	{
		Criteria crit = session.createCriteria(ParametricApprovedGroup.class);
		crit.createCriteria("status").add(Restrictions.ne("id", 2L));
		crit.add(Restrictions.eq("plFeature", plFeature));
		crit.addOrder(Order.asc("groupFullValue"));
		crit.setProjection(Projections.distinct(Projections.property("groupFullValue")));
		List<String> fullValueList = crit.list();
		return fullValueList;
	}

	@SuppressWarnings("unchecked")
	public static List<ParametricApprovedGroup> getAppGroupListByFullValAndFetNameAndPlName(String featureName, String plName, String groupFullValue, Session session)
	{
		Criteria crit = session.createCriteria(ParametricApprovedGroup.class);
		Criteria plFeatureCrit = crit.createCriteria("plFeature");
		//
		plFeatureCrit.createCriteria("feature").add(Restrictions.eq("name", featureName));
		//
		plFeatureCrit.createCriteria("pl").add(Restrictions.eq("name", plName));
		crit.add(Restrictions.eq("groupFullValue", groupFullValue));
		// crit.setProjection(Projections.distinct(Projections.property("groupFullValue")));
		List<ParametricApprovedGroup> parametricApprovedGroup = crit.list();
		return parametricApprovedGroup;
	}

	public static List<PartsParametricValuesGroup> getAppGroupListByFullValAndFetNameAndPlNameOLD(String featureName, String plName, String groupFullValue, Session session)
	{
		Criteria crit = session.createCriteria(PartsParametricValuesGroup.class);
		Criteria plFeatureCrit = crit.createCriteria("plFeature");
		//
		plFeatureCrit.createCriteria("feature").add(Restrictions.eq("name", featureName));
		//
		plFeatureCrit.createCriteria("pl").add(Restrictions.eq("name", plName));
		crit.add(Restrictions.eq("groupFullValue", groupFullValue));
		// crit.setProjection(Projections.distinct(Projections.property("groupFullValue")));
		List<PartsParametricValuesGroup> partsParametricValuesGroup = crit.list();
		return partsParametricValuesGroup;
	}

	public static PartsParametricValuesGroup getApprovedFeatureValues(String value, String plFeatureName, String plName, Session session)
	{

		Criteria crit = session.createCriteria(PartsParametricValuesGroup.class);
		crit.add(Restrictions.eq("groupFullValue", value));
		Criteria plFeatureCrit = crit.createCriteria("plFeature");
		//
		Criteria featureCrit = plFeatureCrit.createCriteria("feature");
		featureCrit.add(Restrictions.eq("name", plFeatureName));
		//
		Criteria plCrit = plFeatureCrit.createCriteria("pl");
		plCrit.add(Restrictions.eq("name", plName));
		//
		List list = crit.list();
		PartsParametricValuesGroup approvedValue = null;
		if(list.size() > 0)
			approvedValue = (PartsParametricValuesGroup) list.get(0);
		return approvedValue;

	}

	public static void insertNewTrackingPkgApprovedValue(PkgApprovedValue pkgApprovedVal, Session session)
	{
		TrackingPkgApprovVal trackingPkgApprovVal = new TrackingPkgApprovVal();
		trackingPkgApprovVal.setId(QueryUtil.getRandomID());
		trackingPkgApprovVal.setPkgApprovedValue(pkgApprovedVal);
		trackingPkgApprovVal.setTrackingTaskQaStatus(getTrackingTaskQaStatus("Pending", session));
		session.save(trackingPkgApprovVal);
	}

	public static void insertNewTrackingPkgDocumentApprovedValue(PkgApprovedValue pkgApprovedVal, Document document,
	/* Component component, */Session session) throws Exception
	{
		TrackingPkgDocApprov trackingPkgDocApprov = new TrackingPkgDocApprov();
		trackingPkgDocApprov.setId(QueryUtil.getRandomID());
		trackingPkgDocApprov.setPkgApprovedValue(pkgApprovedVal);
		trackingPkgDocApprov.setTrackingTaskStatus(getTrackingTaskStatus(session, "Pending"));
		trackingPkgDocApprov.setTrackingTaskQaStatus(null);
		trackingPkgDocApprov.setDocument(document);
		// trackingParamDocApprov.setPartNumber(component.getPartNumber());
		session.save(trackingPkgDocApprov);
	}

	// Islam
	public static void saveValue(Session session, Value value) throws Exception
	{
		session.saveOrUpdate(value);
		// session.beginTransaction().commit();

	}

	public static void saveValue(Session session, String valueString, Value value) throws Exception
	{
		value.setId(System.nanoTime());
		value.setValue(valueString);
		value.setStoreDate(new Date());
		saveValue(session, value);

	}

	@SuppressWarnings("unchecked")
	public static List<ApprovedParametricValue> getApprovedParametricValueByFullValues(List<String> fullValues, String plName, String featureName, Session session) throws Exception
	{
		Criteria crit = session.createCriteria(ApprovedParametricValue.class);
		crit.add(Restrictions.in("fullValue", fullValues));
		// crit.createAlias("plFeature", "plFet");
		// crit.createCriteria("plFet.pl").add(Restrictions.eq("name", plName));
		// crit.createCriteria("plFet.feature").add(Restrictions.eq("name",
		// featureName));
		Criteria criteria = crit.createCriteria("plFeature");
		criteria.createCriteria("pl").add(Restrictions.eq("name", plName));
		criteria.createCriteria("feature").add(Restrictions.eq("name", featureName));

		// crit.setProjection(Projections.property("id"));
		List<ApprovedParametricValue> values = crit.list();

		return values;

	}

	@SuppressWarnings("unchecked")
	public static List<ApprovedParametricValue> getApprovedParametricValueByFullValues(List<String> fullValues, List<String> fromConditionList, List<String> toConditionList, List<String> fromMultiplierList, List<String> toMultiplierList,
			List<String> fromUnitList, List<String> toUnitList, List<String> fromSignList, List<String> toSignList, List<String> fromValueList, List<String> toValueList, List<String> fromValueTypeList, List<String> toValueTypeList, String plName,
			String featureName, Session session) throws Exception
	{
		Criteria criteria = session.createCriteria(ApprovedParametricValue.class);
		criteria.add(Restrictions.in("fullValue", fullValues));
		Criteria plFeatureCriteria = criteria.createCriteria("plFeature");
		plFeatureCriteria.createCriteria("pl").add(Restrictions.eq("name", plName));
		plFeatureCriteria.createCriteria("feature").add(Restrictions.eq("name", featureName));

		Criteria fromConditionCriteria = criteria.createCriteria("fromCondition");
		Criteria toConditionCriteria = criteria.createCriteria("toCondition");
		if(fromConditionList != null && fromConditionList.size() > 0)
			fromConditionCriteria.add(Restrictions.in("name", fromConditionList));
		if(toConditionList != null && toConditionList.size() > 0)
			toConditionCriteria.add(Restrictions.in("name", toConditionList));

		Criteria fromMultiplierUnitCriteria = criteria.createCriteria("fromMultiplierUnit");
		Criteria toMultiplierUnitCriteria = criteria.createCriteria("toMultiplierUnit");
		if(fromMultiplierList != null && fromMultiplierList.size() > 0)
			fromMultiplierUnitCriteria.createCriteria("multiplier").add(Restrictions.in("name", fromMultiplierList));
		if(toMultiplierList != null && toMultiplierList.size() > 0)
			toMultiplierUnitCriteria.createCriteria("multiplier").add(Restrictions.in("name", toMultiplierList));
		if(fromUnitList != null && fromUnitList.size() > 0)
			fromMultiplierUnitCriteria.createCriteria("unit").add(Restrictions.in("name", fromUnitList));
		if(toUnitList != null && toUnitList.size() > 0)
			toMultiplierUnitCriteria.createCriteria("unit").add(Restrictions.in("name", toUnitList));

		Criteria fromValueCriteria = criteria.createCriteria("fromValue");
		Criteria toValueCriteria = criteria.createCriteria("toValue");
		if(fromValueList != null && fromValueList.size() > 0)
			fromValueCriteria.add(Restrictions.in("value", fromValueList));
		if(toValueList != null && toValueList.size() > 0)
			toValueCriteria.add(Restrictions.in("value", toValueList));

		Criteria fromValueTypeCriteria = criteria.createCriteria("fromValueType");
		Criteria toValueTypeCriteria = criteria.createCriteria("toValueType");
		if(fromValueTypeList != null && fromValueTypeList.size() > 0)
			fromValueTypeCriteria.add(Restrictions.in("name", fromValueTypeList));
		if(toValueTypeList != null && toValueTypeList.size() > 0)
			toValueTypeCriteria.add(Restrictions.in("name", toValueTypeList));
		List<ApprovedParametricValue> values = criteria.list();
		return values;
	}

	public static Long getValueIDByFullValue(String fullValue, String plName, String featureName, Session session) throws Exception
	{

		Criteria crit = session.createCriteria(ApprovedParametricValue.class);
		crit.add(Restrictions.eq("fullValue", fullValue));
		crit.createCriteria("plFeature").createCriteria("pl").add(Restrictions.eq("name", plName));
		crit.createCriteria("plFeature").createCriteria("feature").add(Restrictions.eq("name", featureName));
		crit.setProjection(Projections.property("id"));
		Long valueId = 0L;
		valueId = (Long) crit.uniqueResult();

		return valueId;

	}

	public static ParametricApprovedGroup addAppValueGroup(int engine, int update, Document document, String plName, String featureName, String groupFullValue, Long paraUserId, Session session) throws Exception
	{

		ParametricApprovedGroup parametricApprovedGroup = ParaQueryUtil.getParametricApprovedGroup(groupFullValue, plName, featureName, session);
		if(parametricApprovedGroup == null)
		{
			parametricApprovedGroup = new ParametricApprovedGroup();
			parametricApprovedGroup.setId(System.nanoTime());
			parametricApprovedGroup.setPlFeature(getPlFeatureByExactName(featureName, plName, session));
			parametricApprovedGroup.setGroupFullValue(groupFullValue);
		}
		if(document != null)
			parametricApprovedGroup.setDocument(document);
		Criteria criteria = session.createCriteria(TrackingTaskStatus.class);
		if(engine == 1)
		{
			criteria.add(Restrictions.eq("name", "Approved"));
		}
		else if(update == 0)
		{
			criteria.add(Restrictions.eq("name", StatusName.tlReview));
		}
		else if(update == 1)
		{
			criteria.add(Restrictions.eq("name", StatusName.qaReview));
		}
		else if(update == 2)
		{
			criteria.add(Restrictions.eq("name", StatusName.tlReview));
		}
		TrackingTaskStatus trackingTaskStatus = (TrackingTaskStatus) criteria.uniqueResult();
		parametricApprovedGroup.setStatus(trackingTaskStatus);
		parametricApprovedGroup.setParaUserId(paraUserId);
		Date d = new Date();
		// System.out.println("Date is " + d.toString());
		parametricApprovedGroup.setStoreDate(d);

		Long qaUserId = getQAUserId(getPlByPlName(session, plName), getTrackingTaskTypeByName("Approved Values", session));
		if(qaUserId != null && paraUserId != 120)
			parametricApprovedGroup.setQaUserId(qaUserId);
		session.saveOrUpdate(parametricApprovedGroup);

		return parametricApprovedGroup;

	}

	public static void addSeparationGroup(int engine, int update, ApprovedParametricValue approvedParametricValue, String pattern, ParametricApprovedGroup parametricApprovedGroup, int approvedValueOrder, Session session) throws Exception
	{
		ParametricSeparationGroup paraSepGroup = new ParametricSeparationGroup();
		paraSepGroup.setId(System.nanoTime());
		paraSepGroup.setApprovedParametricValue(approvedParametricValue);
		paraSepGroup.setParametricApprovedGroup(parametricApprovedGroup);
		paraSepGroup.setApprovedValueOrder((long) approvedValueOrder);
		paraSepGroup.setPattern(pattern);
		paraSepGroup.setStoreDate(new Date());

		session.saveOrUpdate(paraSepGroup);

	}

	@SuppressWarnings("unchecked")
	public static PartsParametricValuesGroup getpartParametricValueGroup(Long grbid, PlFeature plFeature,
	/* String groupFullValue, */Session session)
	{
		final Criteria crit = session.createCriteria(PartsParametricValuesGroup.class);

		crit.add(Restrictions.eq("plFeature", plFeature));
		crit.add(Restrictions.eq("groupId", grbid));

		List<PartsParametricValuesGroup> partsParametricValuesGroup = crit.list();
		return partsParametricValuesGroup.get(0);

	}

	public static PartComponent getComponentByPartAndSupplierAndDocument(String partNumber, long documentId, Session session)
	{
		final Criteria crit = session.createCriteria(PartComponent.class);
		crit.add(Restrictions.eq("partNumber", partNumber));
		crit.createCriteria("document").add(Restrictions.idEq(documentId));
		return (PartComponent) crit.uniqueResult();
	}

	public static ParametricApprovedGroup getParametricApprovedGroup(String groupFullValue, PlFeature plFeature, Session session)
	{
		final Criteria crit = session.createCriteria(ParametricApprovedGroup.class);
		crit.add(Restrictions.eq("groupFullValue", groupFullValue));
		crit.add(Restrictions.eq("plFeature", plFeature));
		ParametricApprovedGroup group = (ParametricApprovedGroup) crit.uniqueResult();
		return group;
	}

	public static ParametricApprovedGroup getParametricApprovedGroup(String groupFullValues, String plName, String featureName, Session session) throws Exception
	{

		Criteria crit = session.createCriteria(ParametricApprovedGroup.class);
		crit.add(Restrictions.eq("groupFullValue", groupFullValues));
		Criteria criteria = crit.createCriteria("plFeature");
		criteria.createCriteria("pl").add(Restrictions.eq("name", plName));
		criteria.createCriteria("feature").add(Restrictions.eq("name", featureName));

		ParametricApprovedGroup group = (ParametricApprovedGroup) crit.uniqueResult();

		return group;

	}

	public static ParametricApprovedGroup getParametricApprovedGroup(Long clientGroupId, Session session) throws Exception
	{

		Criteria crit = session.createCriteria(ParametricApprovedGroup.class);
		crit.add(Restrictions.eq("groupId", clientGroupId));
		ParametricApprovedGroup group = (ParametricApprovedGroup) crit.uniqueResult();

		return group;

	}

	public static void deleteSeprationGroups(ParametricApprovedGroup parametricApprovedGroup, Session session) throws Exception
	{

		for(Object ob : parametricApprovedGroup.getParametricSeparationGroups())
		{
			ParametricSeparationGroup parametricSeparationGroup = (ParametricSeparationGroup) ob;
			session.delete(parametricSeparationGroup);
		}

	}

	public static void deleteUnUsedApprovedValues(ParametricApprovedGroup parametricApprovedGroup, Session session) throws Exception
	{

		for(Object ob : parametricApprovedGroup.getParametricSeparationGroups())
		{
			ParametricSeparationGroup parametricSeparationGroup = (ParametricSeparationGroup) ob;

			try
			{
				session.delete(parametricSeparationGroup.getApprovedParametricValue());

			}catch(ConstraintViolationException cv)
			{
				System.err.println(parametricSeparationGroup.getApprovedParametricValue().getId() + " Can't Deleted Used by " + cv.getMessage());
				// cv.printStackTrace();
			}
		}
	}

	@SuppressWarnings("unchecked")
	public static String getGroupFullValueByGroupId(Long approvedGroupId, Session session)
	{
		Criteria crit = session.createCriteria(PartsParametricValuesGroup.class);
		crit.add(Restrictions.eq("groupId", approvedGroupId));
		List<PartsParametricValuesGroup> groupFullValuelist = crit.list();
		if(groupFullValuelist.size() > 0)
			return groupFullValuelist.get(0).getGroupFullValue();
		return "";

	}

	public static TrackingParamDocApprov getTrackingParamDocApprov(Long groupId, Document document, Session session) throws Exception
	{
		Criteria crit = session.createCriteria(TrackingParamDocApprov.class);
		crit.add(Restrictions.eq("approvedGroupId", groupId));
		crit.add(Restrictions.eq("document", document));
		return (TrackingParamDocApprov) crit.uniqueResult();

	}

	public static TrackingParametricApprovVal getTrackingParametricApprovVal(Long groupId, Session session) throws Exception
	{
		Criteria crit = session.createCriteria(TrackingParametricApprovVal.class);
		crit.add(Restrictions.eq("approvedGroupId", groupId));
		// crit.add(Restrictions.eq("approvedParametricValue",
		// approvedParametricValue));
		return (TrackingParametricApprovVal) crit.uniqueResult();

	}

	public static void updateGroupStatus(TrackingParamDocApprov trackingParametricDocVal, Long approveStatus, Session session) throws Exception
	{
		List<PartsParametricValuesGroup> partsParametricValuesGroupList = getPartsParametricValuesGroup(trackingParametricDocVal.getApprovedGroupId(), session);
		if(partsParametricValuesGroupList.size() == 0)
			throw new Exception(" Approved value not found ");
		for(PartsParametricValuesGroup partsParametricValuesGroup : partsParametricValuesGroupList)
		{
			partsParametricValuesGroup.setIsApproved(approveStatus);
			session.saveOrUpdate(partsParametricValuesGroup);
		}

	}

	@SuppressWarnings("unchecked")
	public static List<PartsParametricValuesGroup> getPartsParametricValuesGroup(Long groupId, Session session) throws Exception
	{
		Criteria crit = session.createCriteria(PartsParametricValuesGroup.class);
		crit.add(Restrictions.eq("groupId", groupId));
		List<PartsParametricValuesGroup> partsParametricValuesGroupList = crit.list();
		return partsParametricValuesGroupList;
	}

	@SuppressWarnings("unchecked")
	public static List<PartComponent> getComponentListByPLS(List<String> plList, Session session)
	{
		final Criteria crit = session.createCriteria(PartComponent.class);
		crit.createCriteria("supplierPl").createCriteria("pl").add(Restrictions.in("name", plList));
		List<PartComponent> componentlist = crit.list();
		return componentlist;

	}

	@SuppressWarnings("unchecked")
	public static List<PartComponent> getComponentByPlName(String plName, Session session)
	{
		final Criteria crit = session.createCriteria(PartComponent.class);
		crit.createCriteria("supplierPl").createCriteria("pl").add(Restrictions.eq("name", plName));
		List<PartComponent> componentlist = crit.list();
		return componentlist;
	}

	public static String getNewDiscription(Long plId, Long comId, Session session) throws Exception
	{
		String newDistribution = (String) session.createSQLQuery("select AUTO_DESC_ND.get_desc(" + plId + "," + comId + ") from dual").uniqueResult();
		return newDistribution;
	}

	@SuppressWarnings("unchecked")
	public static List<PartComponent> getComponentListByPartNumberAndVendor(List<List<String>> allData, Session session)
	{
		final Criteria crit = session.createCriteria(PartComponent.class);
		crit.createAlias("supplierPl", "suppPl");
		crit.createAlias("suppPl.supplier", "supp");
		Disjunction disjunction = Restrictions.disjunction(); // or
		for(List<String> list : allData)
		{

			String partnumber = list.get(0);
			String vendor = list.get(1);
			Conjunction conjunction = Restrictions.conjunction(); // and
			Criterion criterion = Restrictions.eq("supp.name", vendor);
			conjunction.add(criterion);
			conjunction.add(Restrictions.eq("partNumber", partnumber));
			disjunction.add(conjunction);

		}
		crit.add(disjunction);
		List<PartComponent> componentlist = crit.list();
		return componentlist;
	}

	public static SupplierPl getSupplierPlBySupplierPlID(long supplierPlId, Session session)
	{
		final Criteria crit = session.createCriteria(SupplierPl.class);
		crit.add(Restrictions.eq("id", supplierPlId));
		SupplierPl supplierPl = (SupplierPl) crit.uniqueResult();
		return supplierPl;

	}

	public static void updateComponentByNewDescription(PartComponent component, Session session) throws Exception

	{
		session.saveOrUpdate(component);
		session.beginTransaction().commit();

	}

	@SuppressWarnings("unchecked")
	public static List<String> getPartNumberBySupplier(Supplier supplier, List<String> newParts, Session session) throws Exception
	{
		Criteria crit = session.createCriteria(PartComponent.class);
		// crit.add(Restrictions.eq("document", document));
		crit.createCriteria("supplierPl").add(Restrictions.eq("supplier", supplier));
		crit.add(Restrictions.in("partNumber", newParts));
		crit.setProjection(Projections.property("partNumber"));
		List<String> list = crit.list();
		return list;
	}

	@SuppressWarnings("unchecked")
	public static List<Pl> getPlsByTlId(Long userId, Session session)
	{
		Criteria criteria = session.createCriteria(TrackingParamUserPlRate.class);
		criteria.add(Restrictions.eq("tlId", userId));
		criteria.setProjection(Projections.property("pl"));
		List<Pl> pls = criteria.list();
		return pls;
	}

	public static List<TrackingParamDocApprov> getTrackingParamDocumentApprovedValue(Long groupId, Session session) throws Exception
	{
		Criteria crit = session.createCriteria(TrackingParamDocApprov.class);
		crit.add(Restrictions.eq("approvedGroupId", groupId));
		// crit.add(Restrictions.eq("document", document));
		List<TrackingParamDocApprov> trackingParamDocApprovList = crit.list();
		return trackingParamDocApprovList;

	}

	public static String getFeatureName(PlFeature plFeature)
	{
		Session session = SessionUtil.getSession();
		String featureName = null;
		try
		{
			Criteria crit = session.createCriteria(Feature.class);
			crit.add(Restrictions.eq("id", plFeature.getFeature().getId()));
			crit.setProjection(Projections.property("name"));
			featureName = (String) crit.uniqueResult();
		}catch(Exception ex)
		{
			ex.printStackTrace();
			return null;
		}finally
		{
			SessionUtil.closeSession(session);
		}
		return featureName;
	}

	/**
	 * 
	 * @author Ahmed_Elreweeny
	 * @param userId
	 * @param forCS
	 * @param forFast
	 * @param forPkg
	 * @param forTaxonomyTransfer
	 * @param forDaily
	 * @param forUpdate
	 * @param numberOfRows
	 * @return List<String>
	 */

	public static List<String> getAllVendorsFromDocuments(long userId, boolean forCS, boolean forTaxonomyTransfer, boolean forDaily, boolean forUpdate, boolean forFast)
	{
		List<String> vendors = new ArrayList<String>();
		Session session = null;
		try
		{
			session = SessionUtil.getSession();
			Criteria criteria = session.createCriteria(TrackingParametric.class);
			criteria.createCriteria("trackingTaskStatus").add(Restrictions.eq("name", "Assigned"));
			if(userId > 0)
				criteria.add(Restrictions.eq("parametricUserId", userId));
			if(forCS)
			{
				TrackingTaskType csTask = getTrackingTaskTypeByName("CS", session);
				criteria.add(Restrictions.eq("trackingTaskType", csTask));
			}
			if(forTaxonomyTransfer)
			{
				TrackingTaskType taxonomyTransferTask = getTrackingTaskTypeByName("TaxonomyTransfer", session);
				criteria.add(Restrictions.eq("trackingTaskType", taxonomyTransferTask));
			}
			if(forDaily)
			{
				TrackingTaskType dailyTransferTask = getTrackingTaskTypeByName("New", session);
				criteria.add(Restrictions.eq("trackingTaskType", dailyTransferTask));
			}
			if(forUpdate)
			{
				TrackingTaskType updateTransferTask = getTrackingTaskTypeByName("Update", session);
				criteria.add(Restrictions.eq("trackingTaskType", updateTransferTask));
			}
			if(forFast)
			{
				TrackingTaskType updateFastTask = getTrackingTaskTypeByName("Fast", session);
				criteria.add(Restrictions.eq("trackingTaskType", updateFastTask));
			}
			criteria.setMaxResults(100);
			criteria.setProjection(Projections.property("document"));
			List<Document> docList = criteria.list();
			for(Document document : docList)
			{
				Pdf pdf = document.getPdf();
				if(pdf != null)
				{
					String supplierName = pdf.getSupplierUrl().getSupplier().getName();
					if(supplierName != null && !vendors.contains(supplierName))
						vendors.add(supplierName);
				}
				else
				{
					NonPdf nonPdf = document.getNonPdf();
					String supplierName = nonPdf.getSupplierUrl().getSupplier().getName();
					if(supplierName != null && !vendors.contains(supplierName))
						vendors.add(supplierName);
				}
			}
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}finally
		{
			if(session != null && session.isOpen())
				session.close();
		}
		return vendors;
	}

	/**
	 * 
	 * @author Ahmed_Elreweeny
	 * @param userId
	 * @param forCS
	 * @param forFast
	 * @param forPkg
	 * @param forTaxonomyTransfer
	 * @param forDaily
	 * @param forUpdate
	 * @param numberOfRows
	 * @return List<String>
	 */

	public static List<String> getAllTaxonomiesFromDocuments(long userId, boolean forCS, boolean forTaxonomyTransfer, boolean forDaily, boolean forUpdate, boolean forFast)
	{
		List<String> pls = new ArrayList<String>();
		Session session = null;
		try
		{
			session = SessionUtil.getSession();
			Criteria criteria = session.createCriteria(TrackingParametric.class);
			criteria.createCriteria("trackingTaskStatus").add(Restrictions.eq("name", "Assigned"));
			if(userId > 0)
				criteria.add(Restrictions.eq("parametricUserId", userId));
			if(forCS)
			{
				TrackingTaskType csTask = getTrackingTaskTypeByName("CS", session);
				criteria.add(Restrictions.eq("trackingTaskType", csTask));
			}
			if(forTaxonomyTransfer)
			{
				TrackingTaskType taxonomyTransferTask = getTrackingTaskTypeByName("TaxonomyTransfer", session);
				criteria.add(Restrictions.eq("trackingTaskType", taxonomyTransferTask));
			}
			if(forDaily)
			{
				TrackingTaskType dailyTransferTask = getTrackingTaskTypeByName("New", session);
				criteria.add(Restrictions.eq("trackingTaskType", dailyTransferTask));
			}
			if(forUpdate)
			{
				TrackingTaskType updateTransferTask = getTrackingTaskTypeByName("Update", session);
				criteria.add(Restrictions.eq("trackingTaskType", updateTransferTask));
			}
			if(forFast)
			{
				TrackingTaskType updateFastTask = getTrackingTaskTypeByName("Fast", session);
				criteria.add(Restrictions.eq("trackingTaskType", updateFastTask));
			}
			criteria.setProjection(Projections.property("pl"));
			criteria.setProjection(Projections.distinct(Projections.property("pl")));
			List<Pl> plsList = criteria.list();
			for(Pl pl : plsList)
				pls.add(pl.getName());
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}finally
		{
			if(session != null && session.isOpen())
				session.close();
		}
		return pls;
	}

	static public Object getCM_Part_Lookup_TBL(String NAN_INPUT_PART, String supplierName)
	{

		Session session = null;
		try
		{
			session = SessionUtil.getSession();
			SQLQuery query = session.createSQLQuery("select lut.id from CM.PART_LOOKUP lut where lut.NAN_INPUT_PART =:nanpartnum and lut.SE_MAN_ID =CM.GET_MAN_ID(:suppName)");
			query.setParameter("nanpartnum", NAN_INPUT_PART.trim());
			query.setParameter("suppName", supplierName);

			return query.uniqueResult();
		}catch(Exception ex)
		{
			AppContext.FirMessageError(ex.getMessage(), ParaQueryUtil.class, ex);
		}finally
		{
			SessionUtil.closeSession(session);

		}
		return null;
	}

	public static Object getCM_ID_From_Component_TBL(String NAN_INPUT_PART, Supplier supplier)
	{

		Session session = null;
		try
		{
			session = SessionUtil.getSession();
			SQLQuery query = session.createSQLQuery("select comp.COM_ID from CM.XLP_SE_COMPONENT comp where comp.NAN_PARTNUM =:nanpartnum and comp.MAN_ID =CM.GET_MAN_ID_BY_CODE(:suppcode)");
			query.setParameter("nanpartnum", NAN_INPUT_PART.trim());
			query.setParameter("suppcode", supplier.getCode());

			return query.uniqueResult();
		}catch(Exception ex)
		{
			AppContext.FirMessageError(ex.getMessage(), ParaQueryUtil.class, ex);
		}finally
		{
			SessionUtil.closeSession(session);

		}

		return null;
	}

	public static Object[] getNANAlphaFromComponentTBL(String NANINPUTPART, String supplierName)
	{

		Session session = null;
		try
		{
			session = SessionUtil.getSession();
			SQLQuery query = session.createSQLQuery(" SELECT comp.COM_ID, AUTOMATION2.GETPLNameBYSupPLID(comp.SUPPLIER_PL_ID), AUTOMATION2.GETPDFURLBYDOCID (comp.DOCUMENT_ID) FROM   Part_COMPONENT comp "
					+ "WHERE   CM.NONALPHANUM (comp.PART_NUMBER)=:nanpartnum AND supplier_id=AUTOMATION2.GETSUPPLIERID (:suppName)");

			query.setParameter("nanpartnum", NANINPUTPART.trim());
			query.setParameter("suppName", supplierName);

			return (Object[]) query.uniqueResult();
		}catch(Exception ex)
		{
			AppContext.FirMessageError(ex.getMessage(), ParaQueryUtil.class, ex);
		}finally
		{
			SessionUtil.closeSession(session);

		}

		return null;
	}

	public static Object getCM_Acquisition_TBL(String NAN_INPUT_PART, String supplierName)
	{

		Session session = null;
		try
		{
			session = SessionUtil.getSession();
			SQLQuery query = session.createSQLQuery("select acq.OLD_COM_ID from cm.TBL_PART_ACQUISITION acq where acq.OLD_NAN_PARTNUM =:nanpartnum and acq.OLD_MAN_ID =CM.GET_MAN_ID(:suppName)");
			query.setParameter("nanpartnum", NAN_INPUT_PART.trim());
			query.setParameter("suppName", supplierName);

			return query.uniqueResult();
		}catch(Exception ex)
		{
			AppContext.FirMessageError(ex.getMessage(), ParaQueryUtil.class, ex);
		}finally
		{
			SessionUtil.closeSession(session);

		}
		return null;
	}

	public static void saveOnHasNoParametricFeatureTable(Document doc)
	{
		Session session = null;
		try
		{
			session = SessionUtil.getSession();
			Criteria crit = session.createCriteria(NoParametricDocuments.class);
			crit.add(Restrictions.eq("documentId", doc.getId()));
			NoParametricDocuments nodoc = (NoParametricDocuments) crit.uniqueResult();
			if(nodoc == null)
			{
				NoParametricDocuments newnodoc = new NoParametricDocuments(doc.getId());
				session.save(newnodoc);
			}
		}catch(Exception ex)
		{
			ex.printStackTrace();
			AppContext.FirMessageError(ex.getMessage(), ParaQueryUtil.class, ex);
		}finally
		{
			SessionUtil.closeSession(session);
		}
	}

	public static Long getCm_Man_IdByAutoSupplyerCode(Supplier supplier) throws Exception
	{
		Session session = null;
		Long id = null;
		session = SessionUtil.getSession();
		SQLQuery query = session.createSQLQuery("select CM.GET_MAN_ID_BY_CODE(:suppcode) from dual");
		query.setParameter("suppcode", supplier.getCode());
		String idstring = query.uniqueResult().toString();
		if(idstring != null && !idstring.equalsIgnoreCase("null") && !idstring.isEmpty())
		{
			id = new Long(idstring);
		}
		SessionUtil.closeSession(session);
		return id;
	}

	public static String getNonAlphaPart(String NAN_INPUT_PART)
	{

		Session session = null;
		try
		{
			session = SessionUtil.getSession();
			SQLQuery query = session.createSQLQuery("select CM.NONALPHANUM(:nanpartnum) from dual");
			query.setParameter("nanpartnum", NAN_INPUT_PART.trim());

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

	/**
	 * 
	 * @author Ahmed_Elreweeny
	 * @param userId
	 * @param forCS
	 * @param forFast
	 * @param forPkg
	 * @param forTaxonomyTransfer
	 * @param forDaily
	 * @param forUpdate
	 * @param numberOfRows
	 * @return List<String>
	 */
	public static Pdf getPdfBySeUrl(String seUrl)
	{
		Session session = null;
		try
		{
			seUrl = seUrl.trim().toLowerCase();
			session = SessionUtil.getSession();
			Criteria criteria = session.createCriteria(Pdf.class);
			criteria.add(Restrictions.eq("seUrl", seUrl));
			return (Pdf) criteria.uniqueResult();
		}catch(Exception ex)
		{
			ex.printStackTrace();
			return null;
		}finally
		{
			if(session != null && session.isOpen())
				session.close();
		}
	}

	/**
	 * 
	 * @author Ahmed_Elreweeny
	 * @param userId
	 * @param forCS
	 * @param forFast
	 * @param forPkg
	 * @param forTaxonomyTransfer
	 * @param forDaily
	 * @param forUpdate
	 * @param numberOfRows
	 * @return List<String>
	 */

	public static Feature getFeatureByName(String featureName)
	{
		Session session = null;
		Feature fet = null;
		try
		{
			session = SessionUtil.getSession();
			fet = getFeatureByName(featureName, session);

		}catch(Exception ex)
		{
			ex.printStackTrace();
		}finally
		{
			if(session != null & session.isOpen())
			{
				session.close();
			}
		}
		return fet;
	}

	public static Feature getFeatureByName(String featureName, Session session)
	{
		Feature fet = null;
		try
		{
			Criteria featurescrit = session.createCriteria(Feature.class);
			fet = (Feature) featurescrit.add(Restrictions.eq("name", featureName)).uniqueResult();

		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return fet;
	}

	private static Criteria getFormulaCriteria(Session session, PlFeature plfets, com.se.parametric.dto.FeatureDTO feture)
	{
		Criteria crit = session.createCriteria(QaChecksInDependentFeature.class).add(Restrictions.eq("plFeatureId", plfets.getId())).add(Restrictions.isNotNull("formula")).add(Restrictions.eq("qaChecksValidatetype", new QaChecksValidatetype(5L)))
				.add(Restrictions.eq("dependencyType", 0L));
		return crit;
	}

	public static List<Document> getDSRevisions(Document document)
	{
		System.out.println("in getRevisions()");
		List<Document> list = new ArrayList<Document>();
		TreeMap<Date, Document> treeMap = new TreeMap<Date, Document>();
		Set<SupplierPlFamily> supplierPlFamilies = document.getSupplierPlFamilies();
		for(SupplierPlFamily supplierPlFamily : supplierPlFamilies)
		{
			Set<Document> documents = supplierPlFamily.getDocuments();
			Iterator<Document> documentsIterator = documents.iterator();
			while(documentsIterator.hasNext())
			{
				Document doc = documentsIterator.next();
				if(doc.getId().longValue() != document.getId().longValue())
				{
					Pdf pdf = doc.getPdf();
					NonPdf nonPdf = doc.getNonPdf();
					if(pdf != null)
					{
						if(pdf.getModDate() != null)
							treeMap.put(pdf.getModDate(), doc);
					}
					else if(nonPdf != null)
					{
						if(nonPdf.getDownloadDate() != null)
							treeMap.put(nonPdf.getDownloadDate(), doc);
					}
				}
			}
		}

		if(!treeMap.isEmpty())
		{
			Set<Date> set = treeMap.keySet();
			// System.out.println("Revisions:");
			for(Date key : set)
			{
				Document doc = treeMap.get(key);
				// System.out.println(doc.getId());
				list.add(0, doc);
			}
		}
		else
		{
			// System.out.println("Revisions = 0");
		}
		return list;
	}

	public static TblPdfStatic getTblPdfStatic(long latestId, long pdfId)
	{
		TblPdfStatic tblPdfStatic = null;
		Session session = null;
		try
		{
			session = SessionUtil.getSession();
			Criteria criteria = session.createCriteria(TblPdfStatic.class);
			criteria.createCriteria("documentByLatestPdfId").add(Restrictions.eq("id", latestId));
			criteria.createCriteria("documentByCmPdfId").add(Restrictions.eq("id", pdfId));
			if(criteria.list() != null && criteria.list().size() > 0)
				tblPdfStatic = (TblPdfStatic) criteria.list().get(0);
		}catch(Exception ex)
		{
			ex.printStackTrace();
			return null;
		}finally
		{
			if(session != null && session.isOpen())
				session.close();
		}
		return tblPdfStatic;
	}

	public static TblPdfCompare getTblPdfCompare(long latestUrlId, long pdfUrlId)
	{
		TblPdfCompare tblPdfCompare = null;
		Session session = null;
		try
		{
			session = SessionUtil.getSession();
			Criteria criteria = session.createCriteria(TblPdfCompare.class);
			criteria.createCriteria("documentByLatestPdfId").add(Restrictions.eq("id", latestUrlId));
			criteria.createCriteria("documentByCmPdfId").add(Restrictions.eq("id", pdfUrlId));
			tblPdfCompare = (TblPdfCompare) criteria.uniqueResult();
		}catch(Exception ex)
		{
			ex.printStackTrace();
			return null;
		}finally
		{
			if(session != null && session.isOpen())
				session.close();
		}
		return tblPdfCompare;
	}

	// NotDone Yet
	public static boolean checkCustomerFeatureFlag(Pl pl, String feature)
	{
		Session session = null;
		try
		{

			session = SessionUtil.getSession();
			Criteria criteria = session.createCriteria(Feature.class);
			criteria.add(Restrictions.eq("name", feature));
			Feature fet = (Feature) criteria.uniqueResult();
			Criteria criteriacustomerflag = session.createCriteria(TblFullDataCtrl.class);
			criteriacustomerflag.add(Restrictions.sqlRestriction("PL_ID =? and FET_ID=? and lower(CUSTOMER_FLAG)=lower(?)", new Object[] { pl.getId(), fet.getId(), "Specific_Customer" }, new Type[] { LongType.INSTANCE, LongType.INSTANCE,
					StringType.INSTANCE }));
			List ls = criteriacustomerflag.list();
			if(ls != null && ls.size() > 0)
			{
				return true;
			}
		}catch(Exception ex)
		{
			ex.printStackTrace();
			AppContext.FirMessageError(ex.getMessage(), ParaQueryUtil.class, ex);

		}finally
		{
			SessionUtil.closeSession(session);
		}
		return false;
	}

	public static RelatedFeaturesDTO getQAChecksInDependentFeature(Pl pl, com.se.parametric.dto.FeatureDTO feture)
	{

		PlFeature plfets = null;
		Session session = null;
		QaChecksInDependentFeature qchecks = null;
		RelatedFeaturesDTO relatedfet = null;
		try
		{
			session = SessionUtil.getSession();
			plfets = getPlFeatureByExactName(feture.getFeatureName(), pl.getName(), session);
			Criteria crit = getCondetionsCriteria(session, plfets, feture);
			qchecks = (QaChecksInDependentFeature) crit.uniqueResult();
			if(qchecks == null)
			{
				crit = getContainsCriteria(session, plfets, feture);
				qchecks = (QaChecksInDependentFeature) crit.uniqueResult();

			}
			{
				Criteria plfetcrit = null;
				if(qchecks != null)
				{
					relatedfet = new RelatedFeaturesDTO();
					relatedfet.setQachecksrelatedfet(qchecks);
					relatedfet.setIndependent(feture);
					if(qchecks.getQaCheckRelatedFunctionsesForIndepFunctionId() != null && qchecks.getQaCheckRelatedFunctionsesForIndepFunctionId().size() > 0)
					{
						Iterator iter = qchecks.getQaCheckRelatedFunctionsesForIndepFunctionId().iterator();
						while(iter.hasNext())
						{
							QaCheckRelatedFunctions f = ((QaCheckRelatedFunctions) iter.next());
							Long plfetid = f.getQaChecksInDependentFeatureByDepFunctionId().getPlFeatureId();
							plfetcrit = session.createCriteria(PlFeature.class);
							plfetcrit.add(Restrictions.eq("id", plfetid));
							relatedfet.addDependentFeature(((PlFeature) plfetcrit.uniqueResult()).getFeature().getName(), f.getQaChecksInDependentFeatureByDepFunctionId());
							{
								f.getQaChecksInDependentFeatureByDepFunctionId().getQaChecksValidatetype().getId();

							}
						}
						qchecks.getQaChecksValidatetype().getId();
					}
				}
			}
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}finally
		{
			if(session != null && session.isOpen())
				session.close();
		}

		return relatedfet;
	}

	private static Criteria getCondetionsCriteria(Session session, PlFeature plfets, com.se.parametric.dto.FeatureDTO feture)
	{
		Criteria crit = session.createCriteria(QaChecksInDependentFeature.class).add(Restrictions.eq("plFeatureId", plfets.getId())).add(Restrictions.eq("value", feture.getFeaturevalue())).add(Restrictions.eq("dependencyType", 0L));
		return crit;
	}

	private static Criteria getContainsCriteria(Session session, PlFeature plfets, com.se.parametric.dto.FeatureDTO feture)
	{
		Criteria crit = session.createCriteria(QaChecksInDependentFeature.class).add(Restrictions.eq("plFeatureId", plfets.getId())).add(Restrictions.eq("dependencyType", 0L))
				.add(Restrictions.eq("qaChecksValidatetype", new QaChecksValidatetype(3L))).add(Restrictions.sqlRestriction("? like '%'value'%'", feture.getFeatureName(), StringType.INSTANCE));
		return crit;
	}

	public static List<String> getAllTrackingTaskType(Session session)
	{
		try
		{
			Criteria criteria = session.createCriteria(TrackingTaskType.class);
			criteria.setProjection(Projections.property("name"));
			return criteria.list();
		}catch(Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
	}

	public static List<Supplier> getSupplierPlByListOfPls(List<Pl> pls, Session session)
	{
		try
		{
			Criteria criteria = session.createCriteria(SupplierPl.class);
			criteria.add(Restrictions.in("pl", pls));
			criteria.setProjection(Projections.distinct(Projections.property("supplier")));
			// criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
			// Criteria supplierCriteria = criteria.createCriteria("supplier");
			// supplierCriteria.setProjection(Projections.property("name"));
			return criteria.list();
		}catch(Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
	}

	public static List<ApprovedParametricValue> getApprovedParametricValues(Long groupId, Session session)
	{
		try
		{
			Criteria criteria = session.createCriteria(PartsParametricValuesGroup.class);
			criteria.add(Restrictions.eq("groupId", groupId));
			criteria.addOrder(Order.asc("approvedValueOrder"));
			criteria.setProjection(Projections.property("approvedParametricValue"));
			return criteria.list();
		}catch(Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
	}

	public static void updateTrackingParamDocApprov(Long clientGroupId, ApprovedParametricValue approvedParametricValue, Long trackingParamDocApprovId, String groupFullValue, String trackingStatus, String ddComment, Session session) throws Exception
	{
		TrackingParamDocApprov trackingParamDocApprov = getTrackingParamDocApprovById(trackingParamDocApprovId, session);
		trackingParamDocApprov.setApprovedGroupId(clientGroupId);
		trackingParamDocApprov.setApprovedParametricValue(approvedParametricValue);
		if(trackingStatus != null)
			trackingParamDocApprov.setTrackingTaskStatus(getTrackingTaskStatus(session, trackingStatus/* "Pending" */));
		else
			trackingParamDocApprov.setTrackingTaskStatus(null);
		// if(trackingStatus.equals("Send To QA")){
		// trackingParamDocApprov.setTrackingTaskQaStatus(getTrackingTaskQaStatus(session, "Pending"));
		// }else{
		// trackingParamDocApprov.setTrackingTaskQaStatus(null);
		// }
		trackingParamDocApprov.setDdComment(ddComment);
		trackingParamDocApprov.setApprovedGroup(groupFullValue);
		session.saveOrUpdate(trackingParamDocApprov);
	}

	public static TrackingParamDocApprov getTrackingParamDocApprovById(Long id, Session session) throws Exception
	{
		Criteria crit = session.createCriteria(TrackingParamDocApprov.class);
		crit.add(Restrictions.eq("id", id));
		return (TrackingParamDocApprov) crit.uniqueResult();

	}

	public static List<Pl> getPlsByUser(long userId, long tlId, Session session)
	{
		try
		{
			Criteria criteria = session.createCriteria(TrackingParamUserPlRate.class);
			criteria.add(Restrictions.eq("userId", userId));
			criteria.add(Restrictions.eq("tlId", tlId));
			criteria.setProjection(Projections.property("pl"));
			return criteria.list();
		}catch(Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
	}

	public static List<Pl> getPlsByUserForFeedback(long userId, Session session)
	{
		try
		{
			Criteria criteria = session.createCriteria(TrackingFeedback.class);
			criteria.add(Restrictions.eq("userId", userId));
			criteria.createCriteria("trackingTaskStatus").add(Restrictions.in("name", new String[] { "Wrong Data", "Wrong Value" }));
			criteria.setProjection(Projections.distinct(Projections.property("pl")));
			return criteria.list();
		}catch(Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
	}

	public static List<Document> getDocumentsByUserForFeedback(long userId, Session session)
	{
		try
		{
			Criteria criteria = session.createCriteria(TrackingFeedback.class);
			criteria.add(Restrictions.eq("userId", userId));
			criteria.createCriteria("trackingTaskStatus").add(Restrictions.in("name", new String[] { "Wrong Data", "Wrong Value" }));
			criteria.setProjection(Projections.distinct(Projections.property("document")));
			return criteria.list();
		}catch(Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
	}

	public static List<Document> getDocumentsByUserForFeedbackForTL(long userId, Session session)
	{
		try
		{
			Session grmSession = com.se.grm.db.SessionUtil.getSession();
			GrmUser user = (GrmUser) grmSession.load(GrmUser.class, userId);
			Criteria grmCriteria = grmSession.createCriteria(GrmUser.class);
			grmCriteria.add(Restrictions.eq("leader", user));
			grmCriteria.setProjection(Projections.distinct(Projections.property("id")));
			List<Long> users = grmCriteria.list();
			Criteria criteria = session.createCriteria(TrackingFeedback.class);
			criteria.add(Restrictions.in("userId", users));
			criteria.createCriteria("trackingTaskStatus").add(Restrictions.in("name", new String[] { "Wrong Data", "Wrong Value" }));
			criteria.setProjection(Projections.distinct(Projections.property("document")));
			return criteria.list();
		}catch(Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
	}

	public static List<Long> getUserByTlId(long tlId)
	{
		Session session = null;
		try
		{
			session = com.se.grm.db.SessionUtil.getSession();
			GrmUser user = (GrmUser) session.load(GrmUser.class, tlId);
			Criteria userCriteria = session.createCriteria(GrmUser.class);
			userCriteria.add(Restrictions.eq("leader", user));
			userCriteria.setProjection(Projections.property("id"));
			return userCriteria.list();
		}catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}finally
		{
			com.se.grm.db.SessionUtil.closeSession(session);
		}
	}

	public static List<Pl> getPlsByTL(long tlId, Session session)
	{
		try
		{
			Criteria criteria = session.createCriteria(TrackingParamUserPlRate.class);
			criteria.add(Restrictions.eq("tlId", tlId));
			criteria.setProjection(Projections.property("pl"));
			return criteria.list();
		}catch(Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
	}

	public static Pl getPlByPlName(final Session session, final String Plname) throws Exception
	{
		try
		{
			final Criteria crit = session.createCriteria(Pl.class);
			crit.add(Restrictions.eq("name", Plname));
			final Pl pl = (Pl) crit.uniqueResult();
			return pl;
		}catch(Exception e)
		{
			throw ParametricDevServerUtil.getCatchException(e);
		}

	}

	public static Pl getPlByPlName(String plName) throws Exception
	{
		Session session = SessionUtil.getSession();
		try
		{
			Pl pl = QueryUtil.getPlByExactName(plName, session);
			return pl;
		}catch(Exception e)
		{
			throw ParametricDevServerUtil.getCatchException(e);
		}finally
		{
			session.close();
		}

	}

	public static List<FeatureDTO> getPlFeautres(Pl pl, boolean getApproved) throws Exception
	{
		Session session = SessionUtil.getSession();
		List<FeatureDTO> fets = new ArrayList<FeatureDTO>();
		try
		{
			Criteria criteria = session.createCriteria(PlFeature.class);
			criteria.add(Restrictions.eq("pl", pl));
			criteria.createCriteria("feature").add(
					Restrictions.not(Restrictions.in("name", new String[] { "Family", "PRODUCT_NAME", "Standard_Package_Name", "Introduction Date", "PRODUCT_EXTERNAL_DATASHEET", "Vendor", "Vendor Code", "Description", "Introduction Name",
							"Pin Count", "Supplier Package", "ROHS", "Life Cycle" })));
			criteria.add(Restrictions.isNotNull("columnName"));
			criteria.add(Restrictions.ne("columnName", "man_id"));
			criteria.addOrder(Order.asc("DevelopmentOrder"));

			List<PlFeature> plfets = criteria.list();
		List<String> doneFets=	ParametricQueryUtil.getDoneFlagfets(pl.getId(), session);
			for(PlFeature plFeature : plfets)
			{
				FeatureDTO fetdto = new FeatureDTO();
				String fetName = plFeature.getFeature().getName();
				System.out.println(fetName);
				fetdto.setFeatureName(fetName);
				if(plFeature.getUnit() != null)
				{
					String fetUint = plFeature.getUnit().getName();
					fetdto.setUnit(fetUint);
				}
				if(doneFets.indexOf(fetName)!=-1)
					fetdto.setDoneFlag(true);
				if(getApproved)
				{
					// List<String> appValues=getGroupFullValueByFeatureNameandPlName(fetName,plName,session);
					List<String> appValues = getGroupFullValueByPlFeature(plFeature, session);
					fetdto.setFeatureapprovedvalue(appValues);
				}

				fets.add(fetdto);
			}
			return fets;
		}catch(Exception e)
		{
			throw ParametricDevServerUtil.getCatchException(e);
		}finally
		{
			session.close();
		}
	}

	public static ArrayList<DocumentInfoDTO> getDevelopmentPDF(long userId, String plName, String vendorName, String type, String extracted, Date startDate, Date endDate)
	{
		ArrayList<DocumentInfoDTO> result = new ArrayList<DocumentInfoDTO>();
		DocumentInfoDTO docInfo = null;
		Session session = SessionUtil.getSession();
		Criteria statusCriteria = session.createCriteria(TrackingTaskStatus.class);
		statusCriteria.add(Restrictions.eq("name", "Assigned"));
		TrackingTaskStatus status = (TrackingTaskStatus) statusCriteria.uniqueResult();
		Criteria criteria = session.createCriteria(TrackingParametric.class);
		criteria.add(Restrictions.eq("parametricUserId", userId));
		if(startDate != null && endDate != null)
		{
			criteria.add(Expression.between("assignedDate", startDate, endDate));
		}
		Pl pl = null;
		Supplier supplier = null;
		if(!extracted.equals("All"))
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
		if(!plName.equals("All"))
		{
			Criteria plCriteria = session.createCriteria(Pl.class);
			plCriteria.add(Restrictions.eq("name", plName));
			pl = (Pl) plCriteria.uniqueResult();
			criteria.add(Restrictions.eq("pl", pl));
		}
		if(!vendorName.equals("All"))
		{
			Criteria vendorCriteria = session.createCriteria(Supplier.class);
			vendorCriteria.add(Restrictions.eq("name", vendorName));
			supplier = (Supplier) vendorCriteria.uniqueResult();
			criteria.add(Restrictions.eq("supplier", supplier));
		}
		if(!type.equals("All"))
		{
			Criteria typeCriteria = session.createCriteria(TrackingTaskType.class);
			typeCriteria.add(Restrictions.eq("name", type));
			TrackingTaskType typeObj = (TrackingTaskType) typeCriteria.uniqueResult();
			criteria.add(Restrictions.eq("trackingTaskType", typeObj));
		}

		criteria.add(Restrictions.eq("trackingTaskStatus", status));
		Criteria pdfCriteria = criteria.createCriteria("document");
		pdfCriteria.add(Restrictions.isNotNull("pdf"));
		List list = criteria.list();
		for(int i = 0; i < list.size(); i++)
		{
			TrackingParametric obj = (TrackingParametric) list.get(i);
			docInfo = new DocumentInfoDTO();
			docInfo.setSupplierPl(obj.getSupplierPl());
			docInfo.setSupplier(obj.getSupplier());
			docInfo.setPl(obj.getPl());
			docInfo.setDocument(obj.getDocument());
			docInfo.setPdf(obj.getDocument().getPdf());
			TrackingTaskType trackingTaskType = obj.getTrackingTaskType();
			docInfo.setTaskType(trackingTaskType.getName());
			if(extracted.equals("Extracted"))
			{
				docInfo.setExtracted(true);
			}
			else
			{
				docInfo.setExtracted(false);
			}
			result.add(docInfo);
		}

		// session.close();
		return result;
	}

	public static ArrayList<SupplierPl> getSupplierPLsByDoc(DocumentInfoDTO obj)
	{
		ArrayList<SupplierPl> result = new ArrayList<SupplierPl>();
		Session session = SessionUtil.getSession();
		// try {
		Criteria criteria = session.createCriteria(TrackingParametric.class);
		criteria.add(Restrictions.eq("document", obj.getDocument()));
		criteria.setProjection(Projections.distinct(Projections.property("supplierPl")));
		result = (ArrayList<SupplierPl>) criteria.list();
		return result;
		// } finally {
		// session.close();
		// }
	}

	public static ArrayList<TrackingParametric> getParametricPLsByDoc(DocumentInfoDTO obj)
	{
		ArrayList<TrackingParametric> list = new ArrayList<TrackingParametric>();
		ArrayList<TrackingParametric> result = new ArrayList<TrackingParametric>();
		Session session = SessionUtil.getSession();
		try
		{
			Criteria criteria = session.createCriteria(TrackingParametric.class);
			criteria.add(Restrictions.eq("document", obj.getDocument()));
			// criteria.setProjection(Projections.distinct(Projections.property("pl")));
			list = (ArrayList<TrackingParametric>) criteria.list();
			List<String> s = new ArrayList<String>();
			for(TrackingParametric trackingParametric : list)
			{

				TrackingParametric trac = new TrackingParametric();
				trac = (TrackingParametric) CloneUtil.cloneObject(trackingParametric, s);
				// trac.setDocument(trackingParametric.getDocument());
				// trac.setSupplier(trackingParametric.getSupplier());
				// trac.setPl(trackingParametric.getPl());
				// trac.setSupplierPl(trackingParametric.getSupplierPl());
				result.add(trac);
			}
			return result;
		}finally
		{
			session.close();
		}
	}

	public static ArrayList<DocumentInfoDTO> getParametricPLsByPdfUrl(String pdfUrl, Long userId)
	{
		ArrayList<TrackingParametric> list = new ArrayList<TrackingParametric>();
		// ArrayList<TrackingParametric> result = new ArrayList<TrackingParametric>();
		Session session = SessionUtil.getSession();
		try
		{
			Criteria criteria = session.createCriteria(TrackingParametric.class);
			criteria.add(Restrictions.eq("document", getDocumentBySeUrl(pdfUrl, session)));
			criteria.createCriteria("trackingTaskStatus").add(Restrictions.eq("id", 6l));
			if(userId != null)
				criteria.add(Restrictions.eq("parametricUserId", userId));
			// criteria.setProjection(Projections.distinct(Projections.property("pl")));
			list = (ArrayList<TrackingParametric>) criteria.list();
			ArrayList<DocumentInfoDTO> result = new ArrayList<DocumentInfoDTO>();
			DocumentInfoDTO docInfo;
			for(TrackingParametric track : list)
			{
				docInfo = new DocumentInfoDTO();
				docInfo.setPlName(track.getPl().getName());
				docInfo.setSupplierName(track.getSupplier().getName());
				docInfo.setTaskType(track.getTrackingTaskType().getName());
				docInfo.setStatus(track.getTrackingTaskStatus().getName());
				docInfo.setTitle(track.getDocument().getTitle());
				docInfo.setPdf(track.getDocument().getPdf());

				result.add(docInfo);
			}
			return result;
		}finally
		{
			session.close();
		}
	}

	public static Pl getPLType(Pl pl)
	{
		Pl plType = null;
		Session session = SessionUtil.getSession();
		try
		{
			Criteria criteria = session.createCriteria(SerPl.class);
			criteria.add(Restrictions.eq("pl", pl));
			ArrayList<SerPl> serPls = (ArrayList<SerPl>) criteria.list();
			SerPl serPl = serPls.get(0);
			if(serPl != null)
			{
				plType = serPl.getPlType();
			}

		}catch(Exception ex)
		{
			ex.printStackTrace();
		}finally
		{
			session.close();
		}
		return plType;
	}

	public static Document getDocumnetByPdfUrl(String pdfUrl)
	{
		Session session = SessionUtil.getSession();
		try
		{
			return getDocumentBySeUrl(pdfUrl, session);
		}finally
		{
			session.close();
		}
	}

	public static String getOnlineLinkByDocument(Document document)
	{
		Session session = null;
		String onlineLink = null;
		try
		{
			session = SessionUtil.getSession();
			Criteria criteria = session.createCriteria(Document.class);
			criteria.add(Restrictions.eq("id", document.getId()));
			document = (Document) criteria.uniqueResult();
			onlineLink = document.getPdf().getSupplierUrl().getUrl();
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}finally
		{
			session.close();
		}
		return onlineLink;
	}

	public static String getSupplierUrlByDocument(Document document)
	{
		Session session = null;
		String url = null;
		try
		{
			session = SessionUtil.getSession();
			Criteria criteria = session.createCriteria(Document.class);
			criteria.add(Restrictions.eq("id", document.getId()));
			document = (Document) criteria.uniqueResult();
			url = document.getPdf().getSupplierUrl().getSupplier().getSiteUrl();
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}finally
		{
			session.close();
		}
		return url;
	}

	public static String getRightValue(String value1, String value2)
	{
		StringBuffer result = new StringBuffer();
		String val1[] = value1.split("!");
		int index = 0;
		for(int i = 0; i < val1.length; i++)
		{

			int x = value2.indexOf(val1[i]);
			int y = (x + val1[i].length());
			System.out.println("x is " + x + " and y is " + y);
			System.out.println("" + (x + val1[i].length()));
			result.append(value2.substring(index, (y)));
			if(i < val1.length - 1)
			{
				result.append("!");
			}
			index = y + 1;
			System.out.println("index " + index);
		}
		System.out.println("result is " + result);
		return result.toString();
	}

	public static SupplierPl getSupplierPlByPlAndSup(Supplier supplier, Pl pl)
	{
		Session session = SessionUtil.getSession();
		try
		{
			return QueryUtil.getSupplierPl(supplier, pl, session);
		}finally
		{
			session.close();
		}
	}

	public static void saveComponent(PartComponent com)
	{
		ArrayList<SupplierPl> result = new ArrayList<SupplierPl>();
		Session session = SessionUtil.getSession();
		try
		{
			session.saveOrUpdate(com);
			session.beginTransaction().commit();
		}finally
		{
			session.close();
		}
	}

	public static Long getQAUserId(Pl pl, TrackingTaskType taskType)
	{
		Long user = null;
		Session session = SessionUtil.getSession();
		try
		{
			Criteria criteria = session.createCriteria(TrackingParamUserPlRate.class);
			criteria.add(Restrictions.eq("pl", pl));
			criteria.add(Restrictions.eq("type", "QA"));
			criteria.add(Restrictions.eq("trackingTaskType", taskType));
			TrackingParamUserPlRate x = (TrackingParamUserPlRate) criteria.uniqueResult();
			if(x != null)
			{
				user = x.getUserId();
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			session.close();
		}
		return user;
	}

	public static List<String> getGroupFullValueByFeatureNameAndPl(String featureName, String plName)
	{
		Session session = SessionUtil.getSession();
		List<String> fullValueList = new ArrayList<String>();
		try
		{
			Criteria crit = session.createCriteria(PartsParametricValuesGroup.class);
			Criteria plFeatureCrit = crit.createCriteria("plFeature");
			plFeatureCrit.createCriteria("feature").add(Restrictions.eq("name", featureName));
			plFeatureCrit.createCriteria("pl").add(Restrictions.eq("name", plName));
			crit.setProjection(Projections.distinct(Projections.property("groupFullValue")));
			crit.add(Restrictions.ne("isApproved", 2L));
			fullValueList = crit.list();
		}finally
		{
			session.close();
		}

		return fullValueList;
	}

	public static boolean getDialogMessage(String message, String title)
	{
		int choice = JOptionPane.showConfirmDialog(null, message, title, JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);

		if(choice == JOptionPane.NO_OPTION || choice == JOptionPane.CLOSED_OPTION)
		{
			return false;
		}
		else
		{
			return true;
		}
	}

	public static String getFetValue(long groupId)
	{
		String value = "";
		Session session = null;
		try
		{
			session = SessionUtil.getSession();
			Criteria partsParamValGroupCriteria = session.createCriteria(PartsParametricValuesGroup.class);
			partsParamValGroupCriteria.add(Restrictions.eq("groupId", groupId));
			partsParamValGroupCriteria.add(Restrictions.ne("isApproved", 2L));
			PartsParametricValuesGroup obj = (PartsParametricValuesGroup) partsParamValGroupCriteria.uniqueResult();
			if(obj != null)
			{
				value += obj.getGroupFullValue();
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

		return value;
	}

	public static String getEngName(Document document, Pl pl)
	{
		String engName = "";
		Session session = null;

		try
		{
			session = SessionUtil.getSession();
			SQLQuery query = session.createSQLQuery("select full_name from grm.grm_user u, tracking_parametric p where u.id=p.user_id and p.document_id=" + document.getId() + "and p.pl_id=" + pl.getId());
			engName = query.uniqueResult().toString();
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

		return engName;
	}

	public static Supplier getSupplierByName(String supplierName)
	{
		Session session = null;
		Supplier supplier = null;
		try
		{
			session = SessionUtil.getSession();
			Criteria criteria = session.createCriteria(Supplier.class);
			criteria.add(Restrictions.eq("name", supplierName));
			supplier = (Supplier) criteria.uniqueResult();
		}catch(Exception ex)
		{
			ex.printStackTrace();
			return null;
		}finally
		{
			if((session != null) && (session.isOpen()))
			{
				session.close();
			}
		}
		return supplier;
	}

	public static GrmUser getGRMUserByName(String userName)
	{
		GrmUser user = null;
		Session grmSession = null;
		try
		{
			grmSession = com.se.grm.db.SessionUtil.getSession();
			Criteria grmUserCriteria = grmSession.createCriteria(GrmUser.class);
			grmUserCriteria.add(Restrictions.eq("fullName", userName));
			user = (GrmUser) grmUserCriteria.uniqueResult();
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}finally
		{
			if((grmSession != null) && (grmSession.isOpen()))
			{
				grmSession.close();
			}
		}
		return user;
	}

	public static TrackingFeedbackType getTrackingFeedbackType(String feedbackName)
	{
		TrackingFeedbackType feedbackType = null;
		Session session = null;
		try
		{
			session = SessionUtil.getSession();
			Criteria trackingFeedbackCriteria = session.createCriteria(TrackingFeedbackType.class);
			trackingFeedbackCriteria.add(Restrictions.eq("name", feedbackName));
			feedbackType = (TrackingFeedbackType) trackingFeedbackCriteria.uniqueResult();
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
		return feedbackType;
	}

	// public static void updateParametricReviewData( List<String> fetNames, List<String> fetVals, PartInfoDTO partInfo ){
	// Session session = null;
	// try {
	// session = SessionUtil.getSession();
	// String partNumber = partInfo.getPN();
	// String vendorName = partInfo.getVendorName();
	// String plName = partInfo.getPlName();
	// Supplier supplier = getSupplierByExactName(session, vendorName);
	// Component component = getComponentByPartNumAndSupplier(partNumber, supplier);
	// // Pl pl = getPlByPlName(plName);
	// for ( int i=0; i<fetNames.size(); i++ ) {
	// String fetName = fetNames.get(i);
	// String fetVal = fetVals.get(i);
	// // Feature feature = getFeatureByName(fetName);
	// PlFeature plFeature = getPlFeatureByExactName(fetName, plName, session);
	//
	//
	// }
	// } catch (Exception ex) {
	// ex.printStackTrace();
	// } finally {
	// if ((session != null) && (session.isOpen())) {
	// session.close();
	// }
	// }
	// }
	public static ArrayList<UnApprovedDTO> getTLUnapprovedData(Date startDate, Date endDate, Long[] ids, String engName, String plName, String supplierName, String status, String type)
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
			if(!(engName.equals("")) && !engName.equals("All"))
			{
				Session session2 = com.se.grm.db.SessionUtil.getSession();
				Criteria crit = session2.createCriteria(GrmUser.class);
				crit.add(Restrictions.eq("fullName", engName));
				GrmUser user = (GrmUser) crit.uniqueResult();
				criteria.add(Restrictions.eq("paraUserId", user.getId()));
			}
			else
			{
				criteria.add(Restrictions.in("paraUserId", ids));
			}
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
				statusCriteria.add(Restrictions.eq("name", StatusName.tlReview));
				TrackingTaskStatus statusObj = (TrackingTaskStatus) statusCriteria.uniqueResult();
				criteria.add(Restrictions.eq("taskStatus", statusObj));
			}

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
			if(plName != null && !plName.equals("All"))
			{
				criteria.createAlias("group.plFeature.pl", "pl");
				criteria.add(Restrictions.eq("pl.name", plName));
			}

			if(supplierName != null && !supplierName.equals("All"))
			{
				Criteria trackingcriteria = session.createCriteria(TrackingParametric.class, "track");
				trackingcriteria.createAlias("track.supplier", "supplier");
				trackingcriteria.add(Restrictions.eq("supplier.name", supplierName));
				// ///////////////////////////////////////////////////////////////////////////////////////
				// ////May cause an issue if distinct docs for this supplier in tracking parametric exceeds 1000
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
				rdList = getParametricReviewData(groupRecord.getGroupId(), session);
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

	public static List<String> getPlFeautreNames(String Plname) throws Exception
	{
		Session session = SessionUtil.getSession();
		List<String> fets = new ArrayList<String>();
		try
		{
			Criteria criteria = session.createCriteria(PlFeature.class);
			criteria.add(Restrictions.eq("pl", getPlByPlName(session, Plname)));
			criteria.createCriteria("feature").add(
					Restrictions.not(Restrictions.in("name", new String[] { "Family", "PRODUCT_NAME", "Standard_Package_Name", "Introduction Date", "PRODUCT_EXTERNAL_DATASHEET", "Vendor", "Vendor Code", "Description", "Introduction Name",
							"Pin Count", "Supplier Package", "ROHS", "Life Cycle" })));
			criteria.add(Restrictions.isNotNull("columnName"));
			criteria.add(Restrictions.ne("columnName", "man_id"));
			criteria.addOrder(Order.asc("DevelopmentOrder"));

			List<PlFeature> plfets = criteria.list();
			for(PlFeature plFeature : plfets)
			{
				String fetName = plFeature.getFeature().getName();
				fets.add(fetName);
			}
			return fets;
		}catch(Exception e)
		{
			throw ParametricDevServerUtil.getCatchException(e);
		}finally
		{
			session.close();
		}
	}

	public static int getPlFeautrecount(String Plname) throws Exception
	{
		Session session = SessionUtil.getSession();
		int fets = 0;
		try
		{
			Criteria criteria = session.createCriteria(PlFeature.class);
			criteria.add(Restrictions.eq("pl", getPlByPlName(session, Plname)));
			criteria.createCriteria("feature").add(
					Restrictions.not(Restrictions.in("name", new String[] { "Family", "PRODUCT_NAME", "Standard_Package_Name", "Introduction Date", "PRODUCT_EXTERNAL_DATASHEET", "Vendor", "Vendor Code", "Description", "Introduction Name",
							"Pin Count", "Supplier Package", "ROHS", "Life Cycle" })));
			criteria.add(Restrictions.isNotNull("columnName"));
			criteria.add(Restrictions.ne("columnName", "man_id"));

			if(criteria.list() != null)
				fets = criteria.list().size();
			else
				fets = 0;

			return fets;
		}catch(Exception e)
		{
			throw ParametricDevServerUtil.getCatchException(e);
		}finally
		{
			session.close();
		}
	}

	public static List getParametricReviewData(Long groupID, Session session)
	{

		Criteria criteria = null;
		List list = null;
		criteria = session.createCriteria(ParametricReviewData.class);
		criteria.add(Restrictions.eq("groupApprovedValueId", groupID));
		list = criteria.list();

		return list;
	}

	public static ApprovedParametricValue getApprovedValueObject(String plName, String featureName, String featureValue, Session session)
	{
		Criteria criteria;
		ApprovedParametricValue valueObj = null;
		try
		{
			criteria = session.createCriteria(Pl.class);
			criteria.add(Restrictions.eq("name", plName));
			Pl pl = (Pl) criteria.uniqueResult();
			criteria = session.createCriteria(Feature.class);
			criteria.add(Restrictions.eq("name", featureName));
			Feature feature = (Feature) criteria.uniqueResult();
			criteria = session.createCriteria(PlFeature.class);
			criteria.add(Restrictions.eq("pl", pl));
			criteria.add(Restrictions.eq("feature", feature));
			PlFeature plFeature = (PlFeature) criteria.uniqueResult();
			criteria = session.createCriteria(ApprovedParametricValue.class);
			criteria.add(Restrictions.eq("fullValue", featureValue));
			criteria.add(Restrictions.eq("plFeature", plFeature));
			valueObj = (ApprovedParametricValue) criteria.uniqueResult();

		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return valueObj;
	}

	public static ArrayList<UnApprovedDTO> getEngUnapproved(GrmUserDTO userDTO, Date startDate, Date endDate, String plName, String supplierName, String taskType)
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
		List approvedValueFeedbacks = null;
		List<Object> groupIds = new ArrayList<Object>();
		ApprovedValueFeedback approvedValueFeedback = null;
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
			Criteria criteria = session.createCriteria(PartsParametricValuesGroup.class, "group");
			criteria.add(Restrictions.eq("paraUserId", userDTO.getId()));
			// criteria.add(Restrictions.in("groupId", groupIds));
			criteria.createAlias("taskStatus", "status");
			criteria.add(Restrictions.eq("status.name", StatusName.engFeedback));

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
				if(taskType != null & !taskType.equals("All"))
				{
					if(!unApprovedDTO.getFbStatus().equals(taskType))
						continue;
				}
				// unApprovedDTO.setComment(approvedValueFeedback.getFbComment());
				// unApprovedDTO.setStatus("Wrong Separation");

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
				rdList = getParametricReviewData(groupRecord.getGroupId(), session);
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

	public static long getPdfId(String pdfUrl, String supplierName)
	{
		Session session = SessionUtil.getSession();
		long pdfId = -1;
		try
		{
			SQLQuery query = session.createSQLQuery("SELECT CM.GET_PDF_ID ('" + pdfUrl + "',CM.GET_MAN_CODE_BY_MANNAME ('" + supplierName + "'))FROM DUAL");
			Object obj = query.uniqueResult();
			if(obj != null)
			{
				pdfId = ((BigDecimal) obj).longValue();
			}

		}catch(Exception ex)
		{
			ex.printStackTrace();
		}finally
		{
			session.close();
			return pdfId;
		}
	}

	public static void saveRejectEng(GrmUserDTO user, UnApprovedDTO app, String comment)
	{
		Session session = SessionUtil.getSession();
		Criteria criteria;
		try
		{

			ParametricApprovedGroup groups = getParametricApprovedGroup(app.getFeatureValue(), app.getPlName(), app.getFeatureName(), session);
			criteria = session.createCriteria(ApprovedValueFeedback.class);
			criteria.add(Restrictions.eq("groupID", groups.getId()));
			criteria.add(Restrictions.eq("issuedToId", user.getId()));
			criteria.add(Restrictions.eq("feedbackRecieved", 0l));
			ApprovedValueFeedback approvedValueFeedback = (ApprovedValueFeedback) criteria.uniqueResult();
			approvedValueFeedback.setFeedbackRecieved(1l);
			approvedValueFeedback.setFbComment(app.getComment());
			session.saveOrUpdate(approvedValueFeedback);
			session.beginTransaction().commit();
			ApprovedValueFeedback appFBObj = new ApprovedValueFeedback();
			appFBObj.setGroupID(groups.getId());
			criteria = session.createCriteria(TrackingTaskQaStatus.class);
			criteria.add(Restrictions.eq("name", "Rejected"));
			TrackingTaskQaStatus trackingTaskQaStatus = (TrackingTaskQaStatus) criteria.uniqueResult();//
			appFBObj.setTrackingTaskStatus(trackingTaskQaStatus);
			criteria = session.createCriteria(TrackingFeedbackType.class);
			criteria.add(Restrictions.eq("name", "Internal"));
			TrackingFeedbackType trackingFeedbackType = (TrackingFeedbackType) criteria.uniqueResult();
			appFBObj.setTrackingFeedbackType(trackingFeedbackType);
			appFBObj.setFullValue(app.getFeatureValue());
			appFBObj.setId(System.nanoTime());
			appFBObj.setIssuedBy(user.getId());
			appFBObj.setIssuedToId(user.getLeader().getId());
			appFBObj.setFeedbackRecieved(0l);
			appFBObj.setStoreDate(new Date());
			appFBObj.setFbComment(comment);
			session.saveOrUpdate(appFBObj);
			session.beginTransaction().commit();

			// PartsParametricValuesGroup groupObj = null;
			criteria = session.createCriteria(TrackingTaskStatus.class);
			criteria.add(Restrictions.eq("name", StatusName.tlFeedback));
			TrackingTaskStatus trackingTaskStatus = (TrackingTaskStatus) criteria.uniqueResult();//
			// for(int i = 0; i < groups.size(); i++)
			// {
			// session.beginTransaction().begin();
			// groupObj = groups.get(i);
			groups.setStatus(trackingTaskStatus);
			session.saveOrUpdate(groups);
			// session.beginTransaction().commit();
			// }

		}catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("here");
		}finally
		{
			session.close();
		}
	}

	public static void EngUpdateApprovedValue(GrmUserDTO user, int updateFlag, UnApprovedDTO app)
	{
		Session session = SessionUtil.getSession();
		Criteria criteria;
		try
		{

			ParametricApprovedGroup groups = getParametricApprovedGroup(app.getFeatureValue(), app.getPlName(), app.getFeatureName(), session);
			criteria = session.createCriteria(ApprovedValueFeedback.class);
			criteria.add(Restrictions.eq("groupID", groups.getId()));
			criteria.add(Restrictions.eq("issuedToId", user.getId()));
			criteria.add(Restrictions.eq("feedbackRecieved", 0l));

			ApprovedValueFeedback approvedValueFeedback = (ApprovedValueFeedback) criteria.uniqueResult();
			approvedValueFeedback.setFeedbackRecieved(1l);
			session.saveOrUpdate(approvedValueFeedback);
			session.beginTransaction().commit();

			criteria = session.createCriteria(TrackingTaskQaStatus.class);
			criteria.add(Restrictions.eq("name", "Approved"));
			TrackingTaskQaStatus status = (TrackingTaskQaStatus) criteria.uniqueResult();
			// approvedValueFeedback.setTrackingTaskStatus(status);
			// approvedValueFeedback.setFbComment(app.getComment());
			// session.saveOrUpdate(approvedValueFeedback);
			// session.beginTransaction().commit();
			// session.beginTransaction().begin();
			criteria = session.createCriteria(TrackingFeedbackType.class);
			criteria.add(Restrictions.eq("name", "Internal"));
			TrackingFeedbackType trackingFeedbackType = (TrackingFeedbackType) criteria.uniqueResult();
			approvedValueFeedback = new ApprovedValueFeedback();
			approvedValueFeedback.setTrackingFeedbackType(trackingFeedbackType);
			approvedValueFeedback.setTrackingTaskStatus(status);
			approvedValueFeedback.setFullValue(app.getFeatureValue());
			approvedValueFeedback.setId(System.nanoTime());
			approvedValueFeedback.setIssuedBy(user.getId());
			approvedValueFeedback.setIssuedToId(user.getLeader().getId());
			approvedValueFeedback.setFeedbackRecieved(0l);
			approvedValueFeedback.setStoreDate(new Date());
			approvedValueFeedback.setFbComment(app.getComment());
			approvedValueFeedback.setGroupID(groups.getId());
			session.saveOrUpdate(approvedValueFeedback);
			// session.beginTransaction().commit();
		}catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("here");
		}finally
		{
			session.close();
		}
	}

	// pls ready for export for engineer
	public static List<String> getEngExportablePLNames(long userId, Date startDate, Date endDate)
	{
		String start = "";
		String end = "";
		String dateCond = "";

		if(startDate != null && endDate != null)
		{
			startDate.setHours(0);
			startDate.setMinutes(0);
			startDate.setSeconds(0);
			endDate.setHours(0);
			endDate.setMinutes(0);
			endDate.setSeconds(0);
			endDate.setDate(endDate.getDate() + 1);
			start = new SimpleDateFormat("MM/dd/yyyy").format(startDate);
			end = new SimpleDateFormat("MM/dd/yyyy").format(endDate);
			dateCond = " and FINISHED_DATE BETWEEN TO_DATE('" + start + "', 'MM/DD/YYYY') AND TO_DATE('" + end + "', 'MM/DD/YYYY')";
		}
		// if(endDate != null)
		// {
		// end = new SimpleDateFormat("MM/dd/yyyy").format(endDate);
		// }
		Session session = null;
		Criteria criteria = null;
		List<String> plNames = new ArrayList<String>();
		try
		{
			session = SessionUtil.getSession();
			// SQLQuery query = session.createSQLQuery("select distinct getpl(pl_id) from tracking_parametric where FINISHED_DATE BETWEEN TO_DATE('" +
			// start + "', 'MM/DD/YYYY') AND TO_DATE('" + end + "', 'MM/DD/YYYY') and user_id=" + userId
			// + " and TRACKING_TASK_STATUS_ID=3");
			SQLQuery query = session.createSQLQuery("select distinct getpl(pl_id) from tracking_parametric where user_id=" + userId + " and TRACKING_TASK_STATUS_ID=3" + dateCond);
			plNames = query.list();
		}catch(Exception e)
		{
			e.printStackTrace();
			return plNames;
		}finally
		{
			if((session != null) && (session.isOpen()))
			{
				session.close();
			}

		}
		return plNames;

	}

	public static void exportParts(String plName, GrmUserDTO userDto, Date startDate, Date endDate)
	{
		ExcelHandler2003 xlsHandler = null;
		Session session = null;
		try
		{
			session = SessionUtil.getSession();
			Pl pl = getPlByPlName(session, plName);
			Pl plTypeObj = getPLType(pl);
			List<String> headerList = new ArrayList<String>();
			headerList.add("PRODUCT_NAME");
			headerList.add("PRODUCT_EXTERNAL_DATASHEET");
			headerList.add("Description");
			headerList.add("Family");
			if("Semiconductor".equalsIgnoreCase(plTypeObj.getName()))
			{
				headerList.add("Family Cross");
				headerList.add("Generic");
			}

			headerList.add("Mask");
			headerList.add("Vendor");
			headerList.add("Vendor Code");
			headerList.add("ROHS");
			headerList.add("Supplier Package");
			headerList.add("Pin Count");
			headerList.add("Life Cycle");

			List<FeatureDTO> fets = getPlFeautres(pl, true);
			for(FeatureDTO fet : fets)
			{
				headerList.add(fet.getFeatureName());
			}
			List<Map<String, Object>> components = new ArrayList<Map<String, Object>>();
			String queryString = "select com_id from part_component where document_id in (select document_id from tracking_parametric where user_id=" + userDto.getId() + " and TRACKING_TASK_STATUS_ID=34 and pl_id=" + pl.getId()
					+ ") and supplier_pl_id in ( select id from supplier_pl where pl_id=" + pl.getId() + ")";
			if((startDate != null) && (endDate != null))
			{
				SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyy HH:mm:ss");
				queryString += " AND STORE_DATE between TO_DATE('" + sdf.format(startDate) + "', 'MM/DD/YYYY HH24:MI:SS') and TO_DATE('" + sdf.format(endDate) + "', 'MM/DD/YYYY HH24:MI:SS')";
			}

			SQLQuery query = session.createSQLQuery(queryString);
			List<BigDecimal> comIds = query.list();
			for(int i = 0; i < comIds.size(); i++)
			{
				Map<String, Object> fetsMap = new HashMap<String, Object>();
				long comId = comIds.get(i).longValue();
				query = session.createSQLQuery("select c.part_number, AUTOMATION2.GET_PDF_SEURL(AUTOMATION2.GET_PDFIDBYDOCID(c.document_id)), "
						+ " f.name family_name, c.description, Get_GENERIC_Name (C.GENERIC_ID) generic_Nam, GET_MSK_Value (c.MASK_ID, C.PART_NUMBER) MASK, s.name vendor_name, S.CODE, Get_family_crossName (C.FAMILY_CROSS_ID) family_Cross "
						+ " from part_component c, family f, supplier_pl spl, supplier s where c.com_id=" + comId + " and c.family_id=f.id(+) and c.supplier_pl_id=spl.id and spl.supplier_id=s.id");

				Object[] mainFets = (Object[]) query.uniqueResult();
				fetsMap.put("PRODUCT_NAME", mainFets[0]);
				fetsMap.put("PRODUCT_EXTERNAL_DATASHEET", mainFets[1]);
				fetsMap.put("Family", mainFets[2]);
				fetsMap.put("Description", mainFets[3]);
				fetsMap.put("Generic", mainFets[4]);
				fetsMap.put("Mask", mainFets[5]);
				fetsMap.put("Vendor", mainFets[6]);
				fetsMap.put("Vendor Code", mainFets[7]);
				fetsMap.put("Family Cross", mainFets[8]);
				fetsMap.put("ROHS", "");
				fetsMap.put("Supplier Package", "");
				fetsMap.put("Pin Count", "");
				fetsMap.put("Life Cycle", "");

				query = session.createSQLQuery("select fet.name fet_name, g.group_full_value from parametric_review_data review," + " pl_feature_unit plFet, feature fet, PARAMETRIC_APPROVED_GROUP g where review.com_id=" + comId 
						+ " and review.pl_feature_id=plfet.id and plfet.fet_id=fet.id and review.group_approved_value_id=g.id(+)");
				List<Object[]> paramFets = query.list();
				for(int j = 0; j < paramFets.size(); j++)
				{
					Object[] paramFet = paramFets.get(j);
					String fetName = paramFet[0].toString();
					Object fetVal = paramFet[1];
					fetsMap.put(fetName, fetVal);
				}
				components.add(fetsMap);

			}

			// System.out.println(components);
			xlsHandler = new ExcelHandler2003();
			String fileName = plName;
			fileName = fileName.replaceAll("/", "$");
			String userEmail = userDto.getEmail();
			userEmail = userEmail.substring(0, userEmail.indexOf('@'));
			fileName += "@" + userEmail;
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
			fileName += "@" + sdf.format(new Date()) + "@Insert";
			fileName += ".xls";

			xlsHandler.writeExcelFile(headerList.toArray(new String[headerList.size()]), components, fileName);
			exportNPIParts(plName, userDto, startDate, endDate);
			query = session.createSQLQuery("update tracking_parametric set tracking_task_status_id=AUTOMATION2.GETTASKSTATUSID('" + StatusName.finshed + "') " + " where user_id=" + userDto.getId()
					+ " and tracking_task_status_id=AUTOMATION2.GETTASKSTATUSID('" + StatusName.qaReview + "') and pl_id=AUTOMATION2.GETPLID('" + plName + "')");
			// Transaction tx = session.beginTransaction();
			int x = query.executeUpdate();
			// tx.commit();

		}catch(Exception e)
		{
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error", "Error!", JOptionPane.ERROR_MESSAGE);
		}finally
		{
			if((session != null) && (session.isOpen()))
			{
				session.close();
			}
		}
	}

	public static void exportNPIParts(String plName, GrmUserDTO userDto, Date startDate, Date endDate)
	{
		Session session = null;
		ExcelHandler2003 xlsHandler = null;
		try
		{
			session = SessionUtil.getSession();
			Pl pl = getPlByPlName(session, plName);
			String queryString = "select '" + userDto.getFullName() + "' eng_name, c.part_number, AUTOMATION2.GETSUPPLIERBYDOC(c.document_id) sup_name, "
					+ " AUTOMATION2.GETPDFURLBYDOCID(document_id) pdf_url, GETNPINewsPDFURL (c.DOCUMENT_ID) news_link from part_component c where npi_flag=1 and document_id " + " in (select document_id from tracking_parametric where user_id="
					+ userDto.getId() + " and tracking_task_status_id=34 and pl_id=" + pl.getId() + ") " + " and supplier_pl_id in (select id from supplier_pl where pl_id=" + pl.getId() + ")";
			if((startDate != null) && (endDate != null))
			{
				SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyy HH:mm:ss");
				queryString += " AND STORE_DATE between TO_DATE('" + sdf.format(startDate) + "', 'MM/DD/YYYY HH24:MI:SS') and TO_DATE('" + sdf.format(endDate) + "', 'MM/DD/YYYY HH24:MI:SS')";
			}
			SQLQuery query = session.createSQLQuery(queryString);
			query.addScalar("eng_name", StringType.INSTANCE);
			query.addScalar("part_number", StringType.INSTANCE);
			query.addScalar("sup_name", StringType.INSTANCE);
			query.addScalar("pdf_url", StringType.INSTANCE);
			query.addScalar("news_link", StringType.INSTANCE);
			List<Object[]> npiComponents = query.list();
			if(npiComponents.size() > 0)
			{
				xlsHandler = new ExcelHandler2003();
				String[] header = { "Eng Name", "Part Number", "Vendor Name", "Offline Datasheet", "News Link" };
				SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
				String fileName = userDto.getFullName() + "@" + formatter.format(new Date()) + "@NPI.xls";
				xlsHandler.writeExcelFile(header, new ArrayList<Object[]>(npiComponents), fileName);

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
	}

	public static List<Pl> getPLsForPdf(String pdfUrl)
	{
		ArrayList<Pl> list = new ArrayList<Pl>();
		// ArrayList<TrackingParametric> result = new ArrayList<TrackingParametric>();
		Session session = SessionUtil.getSession();
		try
		{
			Criteria criteria = session.createCriteria(TrackingParametric.class);
			criteria.add(Restrictions.eq("document", getDocumentBySeUrl(pdfUrl, session)));
			criteria.setProjection(Projections.distinct(Projections.property("pl")));
			list = (ArrayList<Pl>) criteria.list();
			for(Pl pl : list)
			{
				System.out.println(pl.getName());
			}
			return list;

		}finally
		{
			session.close();
		}
	}

	/****
	 * get Generic Object By Mame by Ahmed makram
	 ****/
	public static MapGeneric getGeneric(String genName)
	{
		Session session = SessionUtil.getSession();
		MapGeneric generic = null;
		try
		{

			Query q = session.createQuery("select o from MapGeneric o " + " where CM.NONALPHANUM (o.generic)=:man");
			q.setParameter("man", getNonAlphaPart(genName));

			generic = (MapGeneric) q.uniqueResult();
			// System.out.println("Gen id="+generic.getId());
			// if (q.list().size() > 0) {
			// List<MapGeneric> man = q.list();
			//
			// for (MapGeneric r : man) {
			// id = r.getId();
			// }
			// }
			//
			// Criteria cr = session.createCriteria(MapGeneric.class);
			// cr.add(Restrictions.eq("generic", genName));
			// generic = (MapGeneric) cr.uniqueResult();
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			session.close();
		}
		return generic;

	}

	/****
	 * get Family Cross Object By Mame by Ahmed makram
	 ****/
	public static FamilyCross getFamilyCross(String famName)
	{
		Session session = SessionUtil.getSession();
		FamilyCross familyCross = null;
		try
		{

			Query q = session.createQuery("select o from FamilyCross o " + " where CM.NONALPHANUM (o.family)=:man");
			q.setParameter("man", getNonAlphaPart(famName));

			familyCross = (FamilyCross) q.uniqueResult();
			// System.out.println("Family Cross id="+familyCross.getId());

			// Criteria cr = session.createCriteria(FamilyCross.class);
			// cr.add(Restrictions.eq("family", famName));
			// familyCross = (FamilyCross) cr.uniqueResult();
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			session.close();
		}
		return familyCross;
	}

	public static Map<String, List<String>> getExtractorData(Pdf pdf, Supplier supplier, Pl pl)
	{
		Session session = SessionUtil.getSession();
		// List data = new ArrayList();
		Map<String, List<String>> partsData = new HashMap<String, List<String>>();
		try
		{
			Criteria cr = session.createCriteria(ExtractorPdfData.class);
			cr.add(Restrictions.eq("pdf", pdf));
			cr.add(Restrictions.eq("supplier", supplier));
			cr.add(Restrictions.eq("pl", pl));

			List<ExtractorPdfData> pdfData = cr.list();
			List<String> partFets = null;

			for(ExtractorPdfData p : pdfData)
			{
				String pn = p.getPartNumber();
				String fetName = p.getExtractorFeature().getFetName();
				String fetVal = "";
				ExtractorValueMapping valMapp = p.getExtractorValueMapping();
				if(valMapp != null)
				{
					fetVal = valMapp.getSeValue();
				}
				else
				{
					fetVal = p.getVenValue();
				}
				partFets = partsData.get(pn);
				if(partFets == null)
				{
					partFets = new ArrayList<String>();
				}
				partFets.add(fetName + "$" + fetVal);
				partsData.put(pn, partFets);
			}

		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			session.close();
		}
		return partsData;
	}

	public static long getTLByUserID(long userID)
	{
		Session session = null;
		long tlId = 0;
		try
		{
			session = com.se.grm.db.SessionUtil.getSession();
			Criteria criteria = session.createCriteria(GrmUser.class);
			criteria.add(Restrictions.eq("id", userID));
			GrmUser grmUser = (GrmUser) criteria.uniqueResult();
			tlId = grmUser.getLeader().getId();
		}finally
		{
			session.close();
		}

		return tlId;
	}

	public static ArrayList<String> getAlerts(long userId, long group, long role)
	{
		Session session = SessionUtil.getSession();
		ArrayList<String> flags = new ArrayList<>();
		try
		{
			Long[] userIds = new Long[] { userId };
			String colName = "parametricUserId";
			long fbStatusId = 14;
			long reviewStatusId = 6;
			if(role == 1)
			{
				userIds = ParaQueryUtil.getTeamMembersIDByTL(userId);
				fbStatusId = 28;
				reviewStatusId = 4;
			}

			if(group == 23)
			{
				colName = "qaUserId";
				fbStatusId = 15;
				reviewStatusId = 3;
			}
			int npi = getNPIAssigned(userIds, colName, reviewStatusId, session);
			int newcount = getNewAssigned(userIds, colName, reviewStatusId, session);
			int bk = getBacklogAssigned(userIds, colName, reviewStatusId, session);
			int fb = getFeedBackCount(userIds, colName, fbStatusId, session);
			if(group != 23)
			{
				colName = "paraUserId";
			}
			int appnew = getAppValueCount(userIds, colName, reviewStatusId, "New", session);
			int appnpi = getAppValueCount(userIds, colName, reviewStatusId, "NPI", session);
			int appfbnew = getAppFeedBackCount(userIds, colName, fbStatusId, "New", session);
			int appfbnpi = getAppFeedBackCount(userIds, colName, fbStatusId, "NPI", session);
			flags.add(npi + "");
			flags.add(newcount + "");
			flags.add(bk + "");
			flags.add(fb + "");
			flags.add(appnew + "");
			flags.add(appnpi + "");
			flags.add(appfbnew + "");
			flags.add(appfbnpi + "");
		}finally
		{
			session.close();
		}
		return flags;
	}

	public static int getNPIAssigned(Long[] userIds, String colName, long statusId, final Session session)
	{

		final Criteria crit = session.createCriteria(TrackingParametric.class);
		crit.createCriteria("trackingTaskStatus").add(Restrictions.eq("id", statusId));
		crit.createCriteria("trackingTaskType").add(Restrictions.in("id", new Long[] { 4l, 12l, 15l }));
		crit.add(Restrictions.in(colName, userIds));

		if(crit.list() != null)
			return crit.list().size();
		return 0;
	}

	public static int getNewAssigned(Long[] userIds, String colName, long statusId, final Session session)
	{

		final Criteria crit = session.createCriteria(TrackingParametric.class);
		crit.createCriteria("trackingTaskStatus").add(Restrictions.eq("id", statusId));
		crit.createCriteria("trackingTaskType").add(Restrictions.in("id", new Long[] { 1l }));
		crit.add(Restrictions.in(colName, userIds));
		crit.setProjection(Projections.rowCount());
		Long count = (Long) crit.uniqueResult();

		if(count != null)
			return count.intValue();
		return 0;
	}

	public static int getBacklogAssigned(Long[] userIds, String colName, long statusId, final Session session)
	{

		final Criteria crit = session.createCriteria(TrackingParametric.class);
		crit.createCriteria("trackingTaskStatus").add(Restrictions.eq("id", statusId));
		crit.createCriteria("trackingTaskType").add(Restrictions.in("id", new Long[] { 14l }));
		crit.add(Restrictions.in(colName, userIds));
		crit.setProjection(Projections.rowCount());
		Long count = (Long) crit.uniqueResult();

		if(count != null)
			return count.intValue();

		return 0;
	}

	public static int getFeedBackCount(Long[] userIds, String colName, long statusId, final Session session)
	{
		List<Document> docs = new ArrayList<>();
		List<TrackingParametric> tracks = null;
		final Criteria crit = session.createCriteria(TrackingParametric.class);
		crit.createCriteria("trackingTaskStatus").add(Restrictions.eq("id", statusId));
		// crit.createCriteria("trackingTaskType").add(Restrictions.in("id", new Long[] { 4l, 12l, 15l }));
		crit.add(Restrictions.in(colName, userIds));

		tracks = crit.list();

		if(tracks != null)
		{
			for(int i = 0; i < tracks.size(); i++)
			{
				if(tracks.get(i).getDocument() != null)
					docs.add(tracks.get(i).getDocument());
			}
			if(!docs.isEmpty())
			{
				Criteria cri = session.createCriteria(ParametricApprovedGroup.class);
				cri.add(Restrictions.in("document", docs));
				List<ParametricApprovedGroup> groups = null;
				groups = cri.list();
				if(groups != null && !groups.isEmpty())
				{
					List<String> groupsvalue = new ArrayList<>();
					for(int i = 0; i < groups.size(); i++)
					{
						if(groups.get(i).getGroupFullValue() != null)
							groupsvalue.add(groups.get(i).getGroupFullValue());
					}
					Criteria feedbackcri = session.createCriteria(ParametricFeedbackCycle.class);
					feedbackcri.add(Restrictions.in("fbItemValue", groupsvalue));
					feedbackcri.add(Restrictions.eq("feedbackRecieved", 0l));
					long tluser = userIds[0];
					if(userIds.length > 1)
						tluser = ParaQueryUtil.getTLByUserID(userIds[0]);

					feedbackcri.add(Restrictions.eq("issuedTo", tluser));
					if(!feedbackcri.list().isEmpty())
					{
						return feedbackcri.list().size();
					}
					else
						return 0;
				}
				else
					return 0;
			}
			else
				return 0;
		}
		else
			return 0;
	}

	public static int getAppValueCount(Long[] userIds, String colName, long statusId, String type, final Session session)
	{

		List<ParametricApprovedGroup> groups = null;
		List<Document> docs = new ArrayList<>();
		int count = 0;
		final Criteria crit = session.createCriteria(ParametricApprovedGroup.class);
		crit.createCriteria("status").add(Restrictions.eq("id", statusId));
		// crit.createCriteria("trackingTaskType").add(Restrictions.in("id", new Long[] { 4l, 12l, 15l }));
		crit.add(Restrictions.in(colName, userIds));
		groups = crit.list();
		if(groups == null)
			return 0;
		else
		{
			for(int i = 0; i < groups.size(); i++)
			{
				if(groups.get(i).getDocument() != null)
					docs.add(groups.get(i).getDocument());
			}
			if(!docs.isEmpty())
			{
				for(int d = 0; d < docs.size(); d++)
				{
					Criteria cri = session.createCriteria(TrackingParametric.class);
					cri.add(Restrictions.eq("document", docs.get(d)));
					if(type.equals("NPI"))
					{
						cri.createCriteria("trackingTaskType").add(Restrictions.in("id", new Long[] { 4l, 12l, 15l }));
					}
					else
					{
						cri.createCriteria("trackingTaskType").add(Restrictions.not(Restrictions.in("id", new Long[] { 4l, 12l, 15l })));
					}
					if(!cri.list().isEmpty())
						count++;
				}
			}
			else
				return 0;
		}
		return count;
	}

	public static int getAppFeedBackCount(Long[] userIds, String colName, long statusId, String type, final Session session)
	{

		List<ParametricApprovedGroup> groups = null;
		List<Document> docs = new ArrayList<>();
		int count = 0;
		final Criteria crit = session.createCriteria(ParametricApprovedGroup.class);
		crit.createCriteria("status").add(Restrictions.eq("id", statusId));
		// crit.createCriteria("trackingTaskType").add(Restrictions.in("id", new Long[] { 4l, 12l, 15l }));
		crit.add(Restrictions.in(colName, userIds));
		groups = crit.list();
		if(groups == null)
			return 0;
		else
		{
			for(int i = 0; i < groups.size(); i++)
			{
				Criteria feedbackcri = session.createCriteria(ParametricFeedbackCycle.class);
				feedbackcri.add(Restrictions.eq("fbItemValue", groups.get(i).getGroupFullValue()));
				feedbackcri.add(Restrictions.eq("feedbackRecieved", 0l));
				long tluser = userIds[0];
				if(userIds.length > 1)
				{
					tluser = ParaQueryUtil.getTLByUserID(userIds[0]);
				}
				feedbackcri.add(Restrictions.eq("issuedTo", tluser));
				if(!feedbackcri.list().isEmpty())
				{
					if(groups.get(i).getDocument() != null)
						docs.add(groups.get(i).getDocument());
				}
			}
			if(!docs.isEmpty())
			{
				for(int d = 0; d < docs.size(); d++)
				{
					Criteria cri = session.createCriteria(TrackingParametric.class);
					cri.add(Restrictions.eq("document", docs.get(d)));
					if(type.equals("NPI"))
					{
						cri.createCriteria("trackingTaskType").add(Restrictions.in("id", new Long[] { 4l, 12l, 15l }));
					}
					else
					{
						cri.createCriteria("trackingTaskType").add(Restrictions.not(Restrictions.in("id", new Long[] { 4l, 12l, 15l })));
					}
					if(!cri.list().isEmpty())
						count++;
				}
			}
			else
				return 0;
		}
		return count;
	}

	public static Long getTeamLeaderIDByMember(long userId)
	{

		Long result;
		Session grmSession = com.se.grm.db.SessionUtil.getSession();
		try
		{
			Criteria crit = grmSession.createCriteria(GrmUser.class);
			crit.add(Restrictions.eq("id", userId));
			GrmUser u = (GrmUser) crit.uniqueResult();
			result = u.getLeader().getId();

		}finally
		{
			grmSession.close();
		}
		return result;

	}

	public static String getTeamLeaderNameByMember(String userName)
	{

		String result;
		Session grmSession = com.se.grm.db.SessionUtil.getSession();
		try
		{
			Criteria crit = grmSession.createCriteria(GrmUser.class);
			crit.add(Restrictions.eq("fullName", userName.trim()));
			GrmUser u = (GrmUser) crit.uniqueResult();
			result = u.getLeader().getFullName();

		}finally
		{
			grmSession.close();
		}
		return result;

	}

	public static Long getIssueFirstSenderID(String partNum, String vendorName)
	{

		long result;
		Session session = SessionUtil.getSession();
		try
		{
			PartComponent component = DataDevQueryUtil.getComponentByPartNumberAndSupplierName(partNum, vendorName, session);
			Criteria crit = session.createCriteria(PartsFeedback.class);
			crit.add(Restrictions.eq("partComponent", component)); // foreign key col
			crit.add(Restrictions.eq("storeDate", session.createCriteria(PartsFeedback.class).add(Restrictions.eq("partComponent", component)).setProjection(Projections.min("storeDate")).uniqueResult())); // foreign
																																																				// key
																																																				// col
			PartsFeedback p = (PartsFeedback) crit.uniqueResult();
			result = p.getIssuedById();

		}finally
		{
			session.close();
		}
		return result;

	}

	public static String getLastIssueSource(String partNum, String vendorName)
	{

		String result = null;
		TrackingFeedbackType flowSource = null;
		Session session = SessionUtil.getSession();
		try
		{
			PartComponent component = DataDevQueryUtil.getComponentByPartNumberAndSupplierName(partNum, vendorName, session);
			Criteria crit = session.createCriteria(PartsFeedback.class);
			// crit.createCriteria("partComponent").add(Restrictions.eq("comId",component.getComId() )); //foreign key col
			crit.add(Restrictions.eq("partComponent", component));
			crit.add(Restrictions.eq("feedbackRecieved", 0l)); // foreign key col
			PartsFeedback p = (PartsFeedback) crit.uniqueResult();
			if(p != null)
				flowSource = p.getFlowSource();
			if(flowSource != null)
				result = p.getFlowSource().getName();

		}finally
		{
			session.close();
		}
		return result;

	}

	public static Long[] getusersbyqualityandstatus(GrmUserDTO userDTO, String status)
	{
		Long[] users = null;
		try
		{

			TrackingTaskStatus task = null;
			Session session = SessionUtil.getSession();
			Criteria cri = session.createCriteria(TrackingTaskStatus.class);
			cri.add(Restrictions.eq("name", status));
			task = (TrackingTaskStatus) cri.uniqueResult();
			String Sql = "";
			Sql = " SELECT DISTINCT U.ID user_id FROM Tracking_Parametric tp, grm.GRM_USER u";
			Sql = Sql + ", TRACKING_TASK_STATUS st WHERE tp.TRACKING_TASK_STATUS_ID IN (" + task.getId() + ") AND u.id";
			Sql = Sql + " = tp.user_id AND st.id = tp.TRACKING_TASK_STATUS_ID AND tp.QA_USER_ID  = " + userDTO.getId() + " ";
			Sql = Sql + " GROUP BY U.ID";
			List<Object> result = session.createSQLQuery(Sql).list();
			users = new Long[session.createSQLQuery(Sql).list().size()];
			for(int i = 0; i < result.size(); i++)
			{
				BigDecimal id = (BigDecimal) result.get(i);
				users[i] = Long.valueOf(id.toString());
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return users;
	}

}
