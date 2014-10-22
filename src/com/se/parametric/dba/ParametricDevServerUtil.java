package com.se.parametric.dba;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.mapping.Array;

import com.se.automation.db.client.mapping.TblPdfStatic;
import com.se.automation.db.client.mapping.TblPdfCompare;
import com.se.automation.db.CloneUtil;
import com.se.automation.db.QueryUtil;
import com.se.automation.db.SessionUtil;
import com.se.automation.db.client.dto.ComponentDTO;
import com.se.automation.db.client.dto.FeatureValuesDTO;
import com.se.automation.db.client.dto.MainDataFeatureValuesDTO;
import com.se.automation.db.client.dto.PlfeatureValuesDTO;
import com.se.automation.db.client.dto.RevisionPdfDTO;
import com.se.automation.db.client.dto.TrackingParametricDocApprovaleCountDTO;
import com.se.automation.db.client.dto.TrackingParametricDocApprovaleDTO;
import com.se.automation.db.client.dto.TrackingPkgDocApprovaleCountDTO;
import com.se.automation.db.client.dto.TrackingPkgDocApprovaleDTO;
import com.se.automation.db.client.mapping.ApprovedParametricValue;
import com.se.automation.db.client.mapping.Condition;
import com.se.automation.db.client.mapping.DevelopmentCommentValue;
import com.se.automation.db.client.mapping.Document;
import com.se.automation.db.client.mapping.DocumentDownloadJob;
import com.se.automation.db.client.mapping.Family;
import com.se.automation.db.client.mapping.GenericFamily;
import com.se.automation.db.client.mapping.MapGeneric;
import com.se.automation.db.client.mapping.Multiplier;
import com.se.automation.db.client.mapping.MultiplierUnit;
import com.se.automation.db.client.mapping.NonPdf;
import com.se.automation.db.client.mapping.PartComponent;
import com.se.automation.db.client.mapping.PartsParametric;
import com.se.automation.db.client.mapping.Pdf;
import com.se.automation.db.client.mapping.PdfContent;
import com.se.automation.db.client.mapping.PkgApprovedValue;
import com.se.automation.db.client.mapping.PkgFeature;
import com.se.automation.db.client.mapping.PkgJedec;
import com.se.automation.db.client.mapping.PkgMainData;
import com.se.automation.db.client.mapping.PkgValue;
import com.se.automation.db.client.mapping.PkgValueGroup;
import com.se.automation.db.client.mapping.Pl;
import com.se.automation.db.client.mapping.PlFeature;
import com.se.automation.db.client.mapping.Sign;
import com.se.automation.db.client.mapping.Supplier;
import com.se.automation.db.client.mapping.SupplierPl;
import com.se.automation.db.client.mapping.SupplierPlFamily;
import com.se.automation.db.client.mapping.TrackingDatasheetAlert;
import com.se.automation.db.client.mapping.TrackingFast;
import com.se.automation.db.client.mapping.TrackingFeedback;
import com.se.automation.db.client.mapping.TrackingFeedbackType;
import com.se.automation.db.client.mapping.TrackingParamDocApprov;
import com.se.automation.db.client.mapping.TrackingParamUserPlRate;
import com.se.automation.db.client.mapping.TrackingParametric;
import com.se.automation.db.client.mapping.TrackingPkg;
import com.se.automation.db.client.mapping.TrackingPkgDocApprov;
import com.se.automation.db.client.mapping.TrackingPkgTlVendor;
import com.se.automation.db.client.mapping.TrackingTaskQaStatus;
import com.se.automation.db.client.mapping.TrackingTaskStatus;
import com.se.automation.db.client.mapping.TrackingTaskType;
import com.se.automation.db.client.mapping.TrackingTransferStatus;
import com.se.automation.db.client.mapping.Unit;
import com.se.automation.db.client.mapping.Value;
import com.se.automation.db.client.mapping.ValueType;
import com.se.automation.db.parametric.StatusName;
import com.se.grm.client.mapping.GrmGroup;
import com.se.grm.client.mapping.GrmUser;
import com.se.parametric.util.ValidatePart;

public class ParametricDevServerUtil
{
	@SuppressWarnings("unchecked")
	public static List<Pl> getNotDestriputedTaxonomyFromAndTo(int From, int To)
	{
		Session se = SessionUtil.getSession();
		Criteria crit = se.createCriteria(Pl.class);
		crit.add(Restrictions.eq("isPl", true));
		crit.add(Restrictions.sqlRestriction("id not in (select pl_id from TRACKING_PARAM_USER_PL_RATE)"));
		return CloneUtil.cloneObjectList(crit.setMaxResults(To).setFirstResult(From).list(), new ArrayList<String>());
	}

	@SuppressWarnings("unchecked")
	public static int getplWhoNotFoundAtTRACKING_PARAM_USER_PL_RATECount()
	{
		Session se = SessionUtil.getSession();
		Criteria crit = se.createCriteria(Pl.class);
		crit.add(Restrictions.eq("isPl", true));
		crit.add(Restrictions.sqlRestriction("id not in (select pl_id from TRACKING_PARAM_USER_PL_RATE)"));

		crit.setProjection(Projections.count("id")).list();

		List<Integer> list = crit.list();
		if(list == null)
			return 0;
		return list.get(0);

	}

	/**
	 * try to save an object from pkgMainData
	 * 
	 * 
	 * @param component
	 * @param featureGroupMap
	 * @param processingDate
	 * @param pkgJedec
	 * @param pkgUrlDocument
	 * @param session
	 * @throws Throwable
	 */
	private static void savePkgPartObject(PartComponent component, Map<Long, PkgFeature> featureGroupMap, Document pkgUrlDocument, PkgJedec pkgJedec, Date processingDate, Session session) throws Throwable
	{
		// save partsParametric object
		PkgMainData pkgMainData = new PkgMainData();
		// pkgMainData.setComponent(component);
		pkgMainData.setId(getPkgMainDataIdByComponent(component));
		pkgMainData.setPkgJedec(pkgJedec);
		pkgMainData.setCol40(processingDate);
		Set<Long> groups = featureGroupMap.keySet();
		pkgMainData.setDocument(pkgUrlDocument);
		for(Long groupId : groups)
		{
			PkgFeature pkgFeature = featureGroupMap.get(groupId);
			// get group setter method name
			// String numStr = pkgFeature.getId()+"";
			Long num = pkgFeature.getId();
			String methodName = "setCol" + num;
			Method method = null;
			method = PkgMainData.class.getMethod(methodName, Long.class);
			method.invoke(pkgMainData, groupId);
		}
		// save
		session.saveOrUpdate(pkgMainData);
	}

	private static long getPkgMainDataIdByComponent(PartComponent component)
	{
		long ret = -1;
		Session session = SessionUtil.getSession();
		Criteria crit = session.createCriteria(PkgMainData.class);
		crit.add(Restrictions.eq("component", component));
		List<PkgMainData> pkgMainDataList = crit.list();
		if(pkgMainDataList.size() == 0)
			ret = QueryUtil.getRandomID();
		else
			ret = pkgMainDataList.get(0).getId();
		return ret;
	}


	private static void savePkgValuesGroup(Session session, long groupId, PkgApprovedValue approvedVal)
	{
		// save group
		PkgValueGroup group = new PkgValueGroup();
		group.setId(QueryUtil.getRandomID());
		group.setGroupId(groupId);
		group.setStoreDate(new Date());
		// group.setComponent(component);
		// group.setValueGroupOrder(order);
		group.setPkgApprovedValue(approvedVal);
		//
		session.save(group);
	}

	public static PkgApprovedValue addApprovedPkgFeatureValue(PkgValue pkgValue, PkgFeature pkgFeature, boolean isApproved, Session session)
	{
		// save group

		PkgApprovedValue pkgApprovedValue = new PkgApprovedValue();
		pkgApprovedValue.setId(QueryUtil.getRandomID());
		pkgApprovedValue.setPkgValue(pkgValue);
		pkgApprovedValue.setPkgFeature(pkgFeature);
		pkgApprovedValue.setApprovalFlag(isApproved);
		pkgApprovedValue.setStoreDate(new Date());
		session.save(pkgApprovedValue);

		return pkgApprovedValue;

	}

	public static PkgValue addValue(String value, Session session)
	{
		PkgValue pkgValue = new PkgValue();
		pkgValue.setId(QueryUtil.getRandomID());
		pkgValue.setValue(value);
		pkgValue.setStoreDate(new Date());
		session.save(pkgValue);
		return pkgValue;

	}

	public static void updateTrackingPkgDocApprovByTrackingTaskQaStatus(String approvalStatus, String comments, List<TrackingPkgDocApprovaleDTO> listTrackingPkgDocApprovaleDTOs) throws Exception
	{
		Session session = SessionUtil.getSession();
		try
		{
			for(TrackingPkgDocApprovaleDTO trackingPkgDocApprovaleDTO : listTrackingPkgDocApprovaleDTOs)
			{
				TrackingPkgDocApprov trackingPkgDocApprov = ParaQueryUtil.getTrackingPkgDocApprovById(session, trackingPkgDocApprovaleDTO.getTrackingPkgDocApprovid());
				trackingPkgDocApprov.setComments(comments);
				if(!approvalStatus.equals("approved"))
				{
					TrackingTaskQaStatus trackingTaskQaStatus = ParaQueryUtil.getTrackingTaskQaStatus(approvalStatus);
					trackingTaskQaStatus.setName(approvalStatus);
					trackingPkgDocApprov.setTrackingTaskQaStatus(trackingTaskQaStatus);
					ParaQueryUtil.updateTrackingPkgDocApprov(session, trackingPkgDocApprov);
				}
			}

		}catch(Exception e)
		{
			throw getCatchException(e);
		}finally
		{
			SessionUtil.closeSession(session);
		}

	}

	private static void savePartsPararmetricObject(PartComponent component, Map<Long, PlFeature> featureGroupMap, Session session) throws Throwable
	{
		// save partsParametric object
		PartsParametric partsParametric = new PartsParametric();
		partsParametric.setId(QueryUtil.getRandomID());
		// partsParametric.setComponent(component);
		Set<Long> groups = featureGroupMap.keySet();
		for(Long groupId : groups)
		{
			PlFeature feature = featureGroupMap.get(groupId);
			// get group setter method name
			String numStr = feature.getColumnName().substring(13);
			Long num = Long.valueOf(numStr);
			String methodName = "setPartsParametricValuesGroup" + num;
			Method method = PartsParametric.class.getMethod(methodName, Long.class);
			if(groupId.longValue() <= 0)
				groupId = null;
			method.invoke(partsParametric, groupId);
		}
		// save
		session.saveOrUpdate(partsParametric);
	}

	public static List<DevelopmentCommentValue> getDevelopmentCommentValues(long supplierId)
	{
		final Session session = SessionUtil.getSession();
		List<DevelopmentCommentValue> list = ParaQueryUtil.getDevelopmentCommentValues(supplierId, session);
		list = CloneUtil.cloneObjectList(list, new ArrayList<String>());
		SessionUtil.closeSession(session);
		return list;
	}

	public static int getCountApprovedParametricValue(String plname) throws Exception
	{
		Session session = SessionUtil.getSession();
		try
		{
			return ParaQueryUtil.getCountApprovedParametricValue(session, plname);
		}catch(Exception e)
		{
			throw getCatchException(e);
		}finally
		{

			SessionUtil.closeSession(session);
		}

	}

	@SuppressWarnings("unchecked")
	public static List<PkgFeature> getPkgFeaturesByPkgTypeId(long pkgTypeId)
	{
		final Session session = SessionUtil.getSession();
		List<PkgFeature> list = ParaQueryUtil.getPkgFeaturesByPkgTypeId(pkgTypeId, session);
		list = CloneUtil.cloneObjectList(list, new ArrayList<String>());
		SessionUtil.closeSession(session);
		return list;
	}

	public static List<PlFeature> getFeaturesByPlId(long plId) throws Exception
	{
		final Session session = SessionUtil.getSession();
		List<PlFeature> list = ParaQueryUtil.getFeaturesByPlId(plId, session);
		list = CloneUtil.cloneObjectList(list, new ArrayList<String>());
		SessionUtil.closeSession(session);
		return list;
	}

	@SuppressWarnings("unchecked")
	public static List<TrackingTaskQaStatus> getTaskQaStatusName() throws Exception
	{
		Session session = SessionUtil.getSession();
		try
		{
			List<TrackingTaskQaStatus> listtraTaskQaStatus = ParaQueryUtil.getTrackingTaskQaStatusList(session);
			List<TrackingTaskQaStatus> TrackingParametricApprovalValueData = CloneUtil.cloneObjectList(listtraTaskQaStatus, new ArrayList<String>());
			return TrackingParametricApprovalValueData;
		}catch(Exception e)
		{
			throw getCatchException(e);
		}finally
		{
			SessionUtil.closeSession(session);
		}

	}

	public static Long isApprovedFeatureValue(String value, /* PlFeature feature */
			String featureName, String plName)
	{
		final Session session = SessionUtil.getSession();
		//
		Long isApproved = 9000L;
		final String VALUES_DELIMETER = "|";
		String[] enteredValues = null;
		if(value.contains(VALUES_DELIMETER))
			enteredValues = value.trim().split("\\" + VALUES_DELIMETER);
		else
			enteredValues = new String[] { value };
		//

		for(String val : enteredValues)
		{

			ApprovedParametricValue approvedValue = ParaQueryUtil.getApprovedFeatureValue(val, featureName, plName, session);

			if(approvedValue == null)
			{
				return -1L;
			}

			isApproved = approvedValue.getIsApproved();
		}
		SessionUtil.closeSession(session);
		return isApproved;
	}

	public static void changeParametricTaskStatus(Document document, String status, TrackingFeedback trackingFeedback) throws Exception
	{
		final Session session = SessionUtil.getSession();
		try
		{
			TrackingParametric trackingParametric = ParaQueryUtil.getTrackingParametricByDocuomentId(session, document);
			TrackingTaskStatus trackingTaskStatus = ParaQueryUtil.getTrackingTaskStatus(session, status);
			if(trackingParametric == null)
			{
				if(trackingFeedback != null)
				{
					trackingParametric = new TrackingParametric();
					trackingParametric.setId(System.nanoTime());
					trackingParametric.setAssignedDate(trackingFeedback.getAssignedDate());
					trackingParametric.setDocument(document);
					trackingParametric.setParametricUserId(trackingFeedback.getUserId());
					trackingParametric.setPl(trackingFeedback.getPl());
					trackingParametric.setPrioriy(trackingFeedback.getPrioriy());
					trackingParametric.setRequestId(trackingFeedback.getRequestId());
					trackingParametric.setTrackingTaskType(trackingFeedback.getTrackingTaskType());
					trackingParametric.setRecieveDate(trackingFeedback.getRecieveDate());
					trackingParametric.setTrackingTaskStatus(trackingTaskStatus);
					trackingParametric.setTrackingTransferStatus(ParaQueryUtil.getTrackingTransferStatus(session, "Pending Parametric"));
					trackingParametric.setFinishedDate(new Date());
				}
			}
			else
			{
				trackingParametric.setTrackingTaskStatus(trackingTaskStatus);
				trackingParametric.setFinishedDate(new Date());
			}
			ParaQueryUtil.updateTrackingParametric(session, trackingParametric);

		}catch(Exception e)
		{
			throw getCatchException(e);
		}finally
		{

			SessionUtil.closeSession(session);
		}

	}

	private static void updatePartsPararmetricObject(PartsParametric partsParametric, Map<Long, PlFeature> featureGroupMap, Session session) throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException,
			InvocationTargetException
	{
		// set all partParameterValuesGroup to null
		for(Method method : PartsParametric.class.getMethods())
		{
			if(method.getName().startsWith("setPartsParametricValuesGroup"))
			{
				method.invoke(partsParametric, (Object) null);
			}
		}
		//
		Set<Long> groups = featureGroupMap.keySet();
		for(Long groupId : groups)
		{
			PlFeature feature = featureGroupMap.get(groupId);
			// get group setter method name
			String columnName = feature.getColumnName();
			if(columnName == null)
				continue;
			String numStr = columnName.substring(13);
			Long num = Long.valueOf(numStr);
			String methodName = "setPartsParametricValuesGroup" + num;
			Method method = PartsParametric.class.getMethod(methodName, Long.class);
			if(groupId.longValue() <= 0)
				groupId = null;
			method.invoke(partsParametric, groupId);
		}
		session.saveOrUpdate(partsParametric);
	}

	public static void changePkgTaskStatus(Document document, String status, TrackingFeedback trackingFeedback) throws Exception
	{
		final Session session = SessionUtil.getSession();
		try
		{
			TrackingPkg trackingPkg = ParaQueryUtil.getTrackingPkgByDocuomentId(session, document);
			TrackingTaskStatus trackingTaskStatus = ParaQueryUtil.getTrackingTaskStatus(session, status);

			if(trackingPkg == null)
			{
				if(trackingFeedback != null)
				{
					trackingPkg = new TrackingPkg();
					trackingPkg.setId(System.nanoTime());
					trackingPkg.setAssignedDate(trackingFeedback.getAssignedDate());
					trackingPkg.setDocument(document);
					trackingPkg.setPkgUserId(trackingFeedback.getUserId());
					trackingPkg.setPl(trackingFeedback.getPl());
					trackingPkg.setPrioriy(trackingFeedback.getPrioriy());
					trackingPkg.setRequestId(trackingFeedback.getRequestId());
					trackingPkg.setTrackingTaskType(trackingFeedback.getTrackingTaskType());
					trackingPkg.setRecieveDate(trackingFeedback.getRecieveDate());
					trackingPkg.setTrackingTaskStatus(trackingTaskStatus);
					trackingPkg.setTrackingTransferStatus(ParaQueryUtil.getTrackingTransferStatus(session, "Pending PKG"));
				}
			}
			else
				trackingPkg.setTrackingTaskStatus(trackingTaskStatus);
			ParaQueryUtil.updateTrackingPkg(session, trackingPkg);

		}catch(Exception e)
		{
			throw getCatchException(e);
		}finally
		{

			SessionUtil.closeSession(session);
		}

	}

	private static PartComponent getComponentByPartInfo(String partInfo, List<PartComponent> componentList)
	{

		int size = componentList.size();
		String partNumver = partInfo.split(",")[0];
		for(int i = 0; i < size; i++)
		{
			if(componentList.get(i).getPartNumber().equals(partNumver))
			{
				return componentList.get(i);
			}
		}

		return null;
	}

	public static List<String> getApprovedFeatureValues(PlFeature feature)
	{
		final Session session = SessionUtil.getSession();
		List<String> list = ParaQueryUtil.getApprovedFeatureValues(feature, session);
		SessionUtil.closeSession(session);
		return list;
	}

	// salah notes
	public static List<String> getApprovedPkgFeatureValues(PkgFeature feature)
	{
		final Session session = SessionUtil.getSession();
		List<String> list = null;
		// 36 = jedec feature
		if(feature.getId() == 36)
		{
			list = ParaQueryUtil.getGenricPkgJedecList(session);
		}
		else if(feature.getId() != 39)
		{
			list = ParaQueryUtil.getApprovedPkgFeatureValues(feature, session);
		}

		SessionUtil.closeSession(session);
		return list;
	}

	public static Document getDocumentBySeUrl(String seUrl)
	{
		Session session = null;
		try
		{
			session = SessionUtil.getSession();
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		Document documentDB = ParaQueryUtil.getDocumentBySeUrl(seUrl, session);
		if(documentDB == null)
			return null;
		Document documentRet = (Document) CloneUtil.cloneObject(documentDB, new ArrayList<String>());
		Pdf pdfRet = documentRet.getPdf();
		if(pdfRet != null)
		{
			// clone pdf contents
			pdfRet.setPdfContents(new HashSet<PdfContent>(CloneUtil.clonePdfContentList(new ArrayList<PdfContent>(documentDB.getPdf().getPdfContents()))));
			// clone supplier & dev comment values
			pdfRet.getSupplierUrl().setSupplier(CloneUtil.cloneSupplier(documentDB.getPdf().getSupplierUrl().getSupplier()));

			List<DevelopmentCommentValue> devComments = CloneUtil.cloneDevelopmentCommentValueList(new ArrayList<DevelopmentCommentValue>(documentDB.getPdf().getSupplierUrl().getSupplier().getDevelopmentCommentValues()));
			pdfRet.getSupplierUrl().getSupplier().setDevelopmentCommentValues(new HashSet<DevelopmentCommentValue>(devComments));
		}
		else
		{
			NonPdf nonPdfRet = documentRet.getNonPdf();
			// clone supplier & dev comment values
			nonPdfRet.getSupplierUrl().setSupplier(CloneUtil.cloneSupplier(documentDB.getNonPdf().getSupplierUrl().getSupplier()));

			List<DevelopmentCommentValue> devComments = CloneUtil.cloneDevelopmentCommentValueList(new ArrayList<DevelopmentCommentValue>(documentDB.getNonPdf().getSupplierUrl().getSupplier().getDevelopmentCommentValues()));
			nonPdfRet.getSupplierUrl().getSupplier().setDevelopmentCommentValues(new HashSet<DevelopmentCommentValue>(devComments));
		}
		// clone document download jobs for first doc
		Set<DocumentDownloadJob> downloadJobs = documentDB.getDocumentDownloadJobs();
		documentRet.setDocumentDownloadJobs(new HashSet<DocumentDownloadJob>(CloneUtil.cloneDocumentDownloadJobList(new ArrayList<DocumentDownloadJob>(downloadJobs))));
		// clone document supplier pl family
		Set<SupplierPlFamily> supplierPlFamilies = documentDB.getSupplierPlFamilies();
		documentRet.setSupplierPlFamilies(new HashSet<SupplierPlFamily>(CloneUtil.cloneSupplierPlFamilyList(new ArrayList<SupplierPlFamily>(supplierPlFamilies))));
		// clone revision documents
		List<Document> revisionDocuments = new ArrayList<Document>(documentDB.getRevisionsDocuments());
		documentRet.setRevisionsDocuments(new HashSet<Document>(CloneUtil.cloneDocumentList(revisionDocuments)));
		//
		SessionUtil.closeSession(session);
		return documentRet;
	}

	public static List<Pdf> getPdfsForParametricDev(String plName, String supplierName, String downloadJob, Date lastCheckDate, Date downloadDate, /*
																																					 * Date
																																					 * dueDate
																																					 * ,
																																					 */String pdfType, String seUrl, boolean forRevision)
	{
		final Session session = SessionUtil.getSession();
		List<Pdf> list = ParaQueryUtil.getPdfsForParametricDev(plName, supplierName, downloadJob, lastCheckDate, downloadDate, /*
																																 * dueDate,
																																 */
				pdfType, seUrl, forRevision, session);
		list = CloneUtil.cloneObjectList(list, new ArrayList<String>());
		SessionUtil.closeSession(session);
		return list;
	}

	@SuppressWarnings("unchecked")
	public static List<String> suggestDate(String value, String className, String[] property, int maxResult) throws Exception
	{
		final Session session = SessionUtil.getSession();
		try
		{
			final Criteria crit = session.createCriteria("com.se.automation.db.client.mapping." + className);
			for(final String element : property)
			{
				crit.add(Restrictions.ilike(element, value, MatchMode.START));

			}
			final ProjectionList projList = Projections.projectionList();
			projList.add(Projections.groupProperty(property[0]));
			crit.setProjection(projList);
			final List<String> list = crit.setMaxResults(maxResult).list();
			return list;
		}catch(final Exception e)
		{
			e.printStackTrace();
			throw new Exception("suggest Date\n" + e.getCause());
		}finally
		{
			SessionUtil.closeSession(session);
		}
	}

	@SuppressWarnings("unchecked")
	public static List<String> suggestDateForUserByPL(String value/*
																 * , String className, String[] property
																 */, int maxResult, String TaxonomyName, String oldUserName) throws Exception
	{
		final Session grmSession = com.se.grm.db.SessionUtil.getSession();
		final Session session = SessionUtil.getSession();
		try
		{

			Criteria usersCriteria = grmSession.createCriteria(GrmUser.class);
			usersCriteria.add(Restrictions.ilike("fullName", value, MatchMode.START));
			List<Long> ids = usersCriteria.setProjection(Projections.id()).list();

			if(ids.size() == 0)
				return new ArrayList<String>();
			// final Criteria trakingCrit =
			// session.createCriteria("com.se.automation.db.client.mapping." +
			// className);
			final Criteria trakingCrit = session.createCriteria(TrackingParamUserPlRate.class);

			System.out.println("TaxonomyName = " + TaxonomyName);
			trakingCrit.createCriteria("pl").add(Restrictions.eq("name", TaxonomyName));
			trakingCrit.add(Restrictions.in("userId", ids));
			// }

			// ---------------- now we have object of user ------------

			trakingCrit.setProjection(Projections.groupProperty("userId"));
			final List<Long> list = trakingCrit.setMaxResults(maxResult).list();
			if(list.size() == 0)
				return new ArrayList<String>();

			Criteria userIdCriteria = grmSession.createCriteria(GrmUser.class);
			userIdCriteria.add(Restrictions.in("id", list));
			List<String> names = userIdCriteria.setProjection(Projections.property("fullName")).list();
			if(names != null)
			{
				if(!oldUserName.equals(""))
				{
					names.remove(oldUserName);
				}
			}
			return names;

		}catch(final Exception e)
		{
			e.printStackTrace();
			throw new Exception("suggest Date\n" + e.getCause());
		}finally
		{
			SessionUtil.closeSession(session);
			com.se.grm.db.SessionUtil.closeSession(grmSession);
		}
	}

	@SuppressWarnings("unchecked")
	public static List<String> suggestDateForUserByPLAndTeamLeaderId(String value, int maxResult, long grmUserId) throws Exception
	{
		final Session grmSession = com.se.grm.db.SessionUtil.getSession();
		final Session session = SessionUtil.getSession();
		try
		{

			final Criteria trakingCrit = session.createCriteria(TrackingParamUserPlRate.class);

			trakingCrit.add(Restrictions.eq("tlId", grmUserId));

			// }
			// --------------------------------------------------------------------
			trakingCrit.setProjection(Projections.groupProperty("pl"));

			List<Long> plIds = new ArrayList<Long>();
			List<Pl> pls = trakingCrit.list();
			List<String> plNames = new ArrayList<String>();
			for(Pl pl : pls)
			{
				if(plNames.size() > maxResult)
					break;

				System.out.println("pl.getId()" + pl.getId());
				plIds.add(pl.getId());
				String name = pl.getName();
				if(name.toLowerCase().startsWith(value.toLowerCase()))
					plNames.add(name);

			}

			if(plNames.size() == 0)
				return new ArrayList<String>();

			return plNames;

		}catch(final Exception e)
		{
			e.printStackTrace();
			throw new Exception("suggest Date\n" + e.getCause());
		}finally
		{
			SessionUtil.closeSession(session);
			com.se.grm.db.SessionUtil.closeSession(grmSession);
		}
	}

	@SuppressWarnings("unchecked")
	public static List<String> suggestRequiredValueForUserByPLAndTeamLeaderId(GrmUser grmUserId) throws Exception
	{
		final Session grmSession = com.se.grm.db.SessionUtil.getSession();
		final Session session = SessionUtil.getSession();
		try
		{
			List<String> statuslist = new ArrayList<String>();
			statuslist.add("Pending");
			statuslist.add("Send To QA");
			statuslist.add("Wrong Data");
			statuslist.add("Wrong Separation");
			statuslist.add("QA Value Rejected");
			final Criteria trakingCrit = session.createCriteria(TrackingParamUserPlRate.class);

			trakingCrit.add(Restrictions.eq("tlId", grmUserId.getLeader().getId()));
			trakingCrit.add(Restrictions.eq("userId", grmUserId.getId()));
			trakingCrit.setProjection(Projections.property("pl"));
			List<Pl> pls = trakingCrit.list();
			final Criteria trackingParamDocApprovCritaria = session.createCriteria(TrackingParamDocApprov.class);
			if(pls.size() > 0)
				trackingParamDocApprovCritaria.createCriteria("approvedParametricValue").createCriteria("plFeature").add(Restrictions.in("pl", pls));
			// trackingParamDocApprovCritaria.add(Restrictions.ilike(
			// "approvedGroup", value, MatchMode.START));
			trackingParamDocApprovCritaria.createCriteria("trackingTaskStatus").add(Restrictions.in("name", statuslist));
			trackingParamDocApprovCritaria.setProjection(Projections.groupProperty("approvedGroup"));
			List<String> approvedFullValue = trackingParamDocApprovCritaria.list();
			return approvedFullValue;
		}catch(final Exception e)
		{
			e.printStackTrace();
			throw new Exception("suggest Date\n" + e.getCause());
		}finally
		{
			SessionUtil.closeSession(session);
			com.se.grm.db.SessionUtil.closeSession(grmSession);
		}
	}

	public static Supplier getSupplierByExactName(String name)
	{
		try
		{
			Session session = SessionUtil.getSession();
			Supplier sup = QueryUtil.getSupplierByExactName(name, session);
			if(sup != null)
			{
				sup = CloneUtil.cloneSupplier(sup);
			}
			SessionUtil.closeSession(session);
			return sup;
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public static DocumentDownloadJob getDownloadJobByExactName(String name)
	{
		try
		{
			Session session = SessionUtil.getSession();
			DocumentDownloadJob DownloadJob = QueryUtil.getDownloadJobByExactName(name, session);
			if(DownloadJob != null)
			{
				DownloadJob = CloneUtil.cloneDocumentDownloadJob(DownloadJob);
			}
			SessionUtil.closeSession(session);
			return DownloadJob;
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public static List<String> getGenericFamiliesByName(String name, int limit)
	{
		final Session session = SessionUtil.getSession();
		List<String> list = ParaQueryUtil.getGenericFamiliesByName(name, limit, session);
		SessionUtil.closeSession(session);
		return list;
	}

	public static List<String> getFamiliesByName(String name, int limit)
	{
		final Session session = SessionUtil.getSession();
		List<String> list = ParaQueryUtil.getFamiliesByName(name, limit, session);
		SessionUtil.closeSession(session);
		return list;
	}

	public static List<RevisionPdfDTO> getUrlRevisionsDocumentsByDocument(Document document) throws Exception
	{
		Session session = SessionUtil.getSession();

		try
		{
			List<Object[]> list = null;
			if(document.getPdf() != null)
				list = ParaQueryUtil.getUrlRevisionsDocumentsByPdfId(document.getId(), session);
			else
				list = ParaQueryUtil.getUrlRevisionsDocumentsByNonPdfId(document.getId(), session);

			List<RevisionPdfDTO> dtos = new ArrayList<RevisionPdfDTO>();
			for(int i = 0; i < list.size(); i++)
			{
				RevisionPdfDTO dto = new RevisionPdfDTO();
				final Object[] o = list.get(i);
				final BigDecimal id = (BigDecimal) o[0];
				dto.setDocumentId(id.longValue());
				final BigDecimal Pdfid = (BigDecimal) o[1];
				dto.setPdfId(Pdfid.longValue());
				final String Seurl = (String) o[2];
				dto.setSeUrl(Seurl);
				final BigDecimal revisionid = (BigDecimal) o[3];
				if(revisionid != null)
					dto.setRevisionDocumentId(revisionid.longValue());
				final Date revisionDate = (Date) o[5];
				if(revisionDate != null)
					dto.setRevisionDate(revisionDate);
				dtos.add(dto);
			}

			return dtos;
		}catch(Exception e)
		{
			e.printStackTrace();
			throw new Exception();
		}finally
		{
			SessionUtil.closeSession(session);
		}

	}

	@SuppressWarnings("unchecked")
	public static List<PartComponent> getPartNumberByDocumentsId(long documentid) throws Exception
	{

		Session session = SessionUtil.getSession();
		try
		{
			List<PartComponent> components = ParaQueryUtil.getPartNumberByDocumentsId(documentid, session);
			List<PartComponent> components2 = CloneUtil.cloneObjectList(components, new ArrayList<String>());
			return components2;
		}catch(Exception e)
		{
			e.printStackTrace();
			throw new Exception();
		}finally
		{
			SessionUtil.closeSession(session);
		}

	}

	public static List<String> getApprovedFeatureValues(String featureName, String plName)
	{
		final Session session = SessionUtil.getSession();
		List<String> list = ParaQueryUtil.getFullValueListstr(
		/* plFeature */featureName, plName, session);

		SessionUtil.closeSession(session);
		return list;
	}

	public static List<String> getgroupFullValueByFeatureNameAndPlName(String featureName, String plName) throws Exception
	{
		Session session = SessionUtil.getSession();
		try
		{

			List<String> Fullvaluelist = ParaQueryUtil.getGroupFullValueByFeatureNameandPlName(featureName, plName, session);

			return Fullvaluelist;

		}catch(Exception e)
		{
			throw getCatchException(e);
		}finally
		{
			SessionUtil.closeSession(session);
		}
	}

	/**
	 * 
	 * loade list of documnets by user ID
	 * 
	 * @param userId
	 * @param forCS
	 * @param forFast
	 * @param forPkg
	 * @param forTaxonomyTransfer
	 * @param forDaily
	 * @param forUpdate
	 * @param numberOfRows
	 * @return
	 * 
	 * @throws Exception
	 */
	//
	public static List<TrackingParametric> getParamDevTrackingPdfs(long userId, String vendor, String taxonomy, String Datasheetflag, String deliverydate, boolean forCS, boolean forFast, boolean forPkg, boolean forTaxonomyTransfer, boolean forDaily,
			boolean forUpdate, int pagenum, int numberOfRows) throws Exception
	{
		Session session = null;
		try
		{

			session = SessionUtil.getSession();
			List<TrackingParametric> list = null;

			list = ParaQueryUtil.getParamDevTrackingPdfs(userId, vendor, taxonomy, Datasheetflag, deliverydate, forCS, forTaxonomyTransfer, forDaily, forUpdate, forFast, pagenum, numberOfRows, session);

			List<TrackingParametric> retList = new ArrayList<TrackingParametric>();
			for(TrackingParametric documentDB : list)
			{
				TrackingParametric documentRet = documentDB;
				Set<SupplierPlFamily> supplierPlFamilies = documentDB.getDocument().getSupplierPlFamilies();
				documentRet.getDocument().setSupplierPlFamilies(supplierPlFamilies);
				{
					Set<PartComponent> components = documentDB.getDocument().getComponents();
					documentRet.getDocument().setComponents(components);
				}
				retList.add(documentRet);
			}
			return retList;
		}catch(Exception e)
		{
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}finally
		{
			SessionUtil.closeSession(session);
		}

	}

	public static List<TrackingParametric> getParamDevTrakingPdfs(long userId, String vendor, String taxonomy, String Datasheetflag, String deliverydate, boolean forCS, boolean forFast, boolean forPkg, boolean forTaxonomyTransfer, boolean forDaily,
			boolean forUpdate, int pagenum, int numberOfRows) throws Exception
	{
		Session session = null;
		try
		{

			session = SessionUtil.getSession();
			List<TrackingParametric> list = null;

			list = ParaQueryUtil.getParamDevTrackingPdfs(userId, vendor, taxonomy, Datasheetflag, deliverydate, forCS, forTaxonomyTransfer, forDaily, forUpdate, forFast, pagenum, numberOfRows, session);

			List<TrackingParametric> retList = new ArrayList<TrackingParametric>();
			for(TrackingParametric documentDB : list)
			{
				TrackingParametric documentRet = documentDB;
				Set<SupplierPlFamily> supplierPlFamilies = documentDB.getDocument().getSupplierPlFamilies();
				documentRet.getDocument().setSupplierPlFamilies(supplierPlFamilies);
				{
					Set<PartComponent> components = documentDB.getDocument().getComponents();
					documentRet.getDocument().setComponents(components);
				}
				retList.add(documentRet);
			}
			return retList;
		}catch(Exception e)
		{
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}finally
		{
			SessionUtil.closeSession(session);
		}

	}

	/**
	 * 
	 * loade list of documnets by user ID
	 * 
	 * @param userId
	 * @param forCS
	 * @param forFast
	 * @param forPkg
	 * @param forTaxonomyTransfer
	 * @param forDaily
	 * @param forUpdate
	 * @param numberOfRows
	 * @return
	 * 
	 * @throws Exception
	 */
	//
	public static List<TrackingParametric> getParamDevTrackingPdfs(long userId, String vendor, String taxonomy, boolean forCS, boolean forFast, boolean forPkg, boolean forTaxonomyTransfer, boolean forDaily, boolean forUpdate, int pagenum,
			int numberOfRows) throws Exception
	{
		Session session = null;
		try
		{
			session = SessionUtil.getSession();
			List<TrackingParametric> list = null;
			list = ParaQueryUtil.getParamDevTrackingPdfs(userId, vendor, taxonomy, null, null, forCS, forTaxonomyTransfer, forDaily, forUpdate, forFast, pagenum, numberOfRows, session);
			System.out.println(list.size());

			List<TrackingParametric> retList = new ArrayList<TrackingParametric>();
			for(TrackingParametric documentDB : list)
			{
				TrackingParametric documentRet = documentDB;
				Set<SupplierPlFamily> supplierPlFamilies = documentDB.getDocument().getSupplierPlFamilies();
				documentRet.getDocument().setSupplierPlFamilies(supplierPlFamilies);
				if(forFast)
				{
					Criteria crit = session.createCriteria(TrackingFast.class);
					crit.add(Restrictions.eq("document", documentDB));
					crit.setProjection(Projections.rowCount());
					Integer count = (Integer) crit.uniqueResult();

					Set<PartComponent> componentset = new HashSet<PartComponent>();
					for(int i = 0; i < count.longValue(); i++)
					{
						componentset.add(new PartComponent());
					}
					documentRet.getDocument().setComponents(componentset);
				}
				else
				{
					Set<PartComponent> components = documentDB.getDocument().getComponents();
					documentRet.getDocument().setComponents(components);
				}
				retList.add(documentRet);
			}
			return retList;
		}catch(Exception e)
		{
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}finally
		{
			SessionUtil.closeSession(session);
		}

	}

	private static TrackingFast saveFastPart(Document document, Session session, String partInfo) throws Throwable
	{
		String[] partInfoArr = partInfo.split(",");
		String partNumber = partInfoArr[0];
		TrackingFast trackingFast = ParaQueryUtil.getTrackingFastByPartNumberAndDocument(document, partNumber, session);
		if(trackingFast == null)
		{
			trackingFast = new TrackingFast();
			long id = QueryUtil.getRandomID();
			trackingFast.setId(id);
			trackingFast.setPartNumber(partNumber);
			trackingFast.setDocument(document);

		}

		if(partInfoArr.length == 9)
		{
			String decsriptionStr = partInfoArr[6].equals("null") ? null : partInfoArr[6];
			if(decsriptionStr != null)
				trackingFast.setDescription(decsriptionStr);
			String StatusStr = partInfoArr[7].equals("null") ? null : partInfoArr[7];
			if(StatusStr != null)
				trackingFast.setTrackingFastStatus(ParaQueryUtil.getTrackingFastStatusByExactName(StatusStr, session));
			String CommentStr = partInfoArr[8].equals("null") ? null : partInfoArr[8];
			if(CommentStr != null)
				trackingFast.setComment(CommentStr);
		}
		//
		session.saveOrUpdate(trackingFast);
		//
		return trackingFast;
	}

	public static boolean checkSeUrl(String seUrl)
	{
		Session session = SessionUtil.getSession();
		Document document = ParaQueryUtil.getDocumentBySeUrl(seUrl, session);
		SessionUtil.closeSession(session);

		if(document == null)
			return false;
		return true;
	}

	private static void checkIfTwoDatasheetFromTheSameSupplier(Document fromDocument, Document toDocument) throws Exception
	{
		Long fromSupplierId;
		if(fromDocument.getPdf() != null)
			fromSupplierId = fromDocument.getPdf().getSupplierUrl().getSupplier().getId();
		else
			fromSupplierId = fromDocument.getNonPdf().getSupplierUrl().getSupplier().getId();

		Long toSupplierId;
		if(toDocument.getPdf() != null)
			toSupplierId = toDocument.getPdf().getSupplierUrl().getSupplier().getId();
		else
			toSupplierId = toDocument.getNonPdf().getSupplierUrl().getSupplier().getId();

		if(!fromSupplierId.equals(toSupplierId))
			throw new Exception("The Replaced DS should be on same Supplier");
	}

	@SuppressWarnings("unchecked")
	public static List<PartComponent> getPartsBySeUrl(String seUrl, int from, int to) throws Exception
	{
		Session session = SessionUtil.getSession();
		try
		{

			Document document = ParaQueryUtil.getDocumentBySeUrl(seUrl, session);
			if(document != null)
			{
				System.out.println("document.getId() = " + document.getId());

				Criteria crit = session.createCriteria(PartComponent.class);
				crit.add(Restrictions.eq("document", document));

				List<PartComponent> clonedComponents = CloneUtil.cloneObjectList(crit.setMaxResults(to).setFirstResult(from).list(), new ArrayList<String>());
				return clonedComponents;
			}
			return new ArrayList<PartComponent>();
		}catch(Exception e)
		{
			throw getCatchException(e);
		}finally
		{

			SessionUtil.closeSession(session);
		}

	}

	@SuppressWarnings("unchecked")
	public static List<PartComponent> getPartsByDocuometAndPl(Document document, Pl pl, int maxresult) throws Exception
	{
		Session session = SessionUtil.getSession();
		try
		{

			Criteria crit = session.createCriteria(PartComponent.class);
			crit.add(Restrictions.eq("document", document));
			crit.createCriteria("supplierPl").add(Restrictions.eq("pl", pl));
			List<PartComponent> clonedComponents = CloneUtil.cloneObjectList(crit.setMaxResults(maxresult).list(), new ArrayList<String>());
			return clonedComponents;

		}catch(Exception e)
		{
			throw getCatchException(e);
		}finally
		{

			SessionUtil.closeSession(session);
		}

	}

	@SuppressWarnings("unchecked")
	public static List<TrackingFast> gettrackingFastByDocument(Document document, int maxresult) throws Exception
	{
		Session session = SessionUtil.getSession();
		try
		{
			Criteria crit = session.createCriteria(TrackingFast.class);
			crit.add(Restrictions.eq("document", document));
			List<TrackingFast> clonedComponents = CloneUtil.cloneObjectList(crit.setMaxResults(maxresult).list(), new ArrayList<String>());
			return clonedComponents;

		}catch(Exception e)
		{
			throw getCatchException(e);
		}finally
		{
			SessionUtil.closeSession(session);
		}

	}

	public static int getCountOfPartsBySeUrl(String seUrl) throws Exception
	{
		Session session = SessionUtil.getSession();
		try
		{
			int compoId = ParaQueryUtil.getCountOfPartsBySeUrl(seUrl, session);
			return compoId;
		}catch(Exception e)
		{
			throw getCatchException(e);
		}finally
		{

			SessionUtil.closeSession(session);
		}

	}

	private static void updateTrackingParametric(Session session, TrackingParametric trackingParametric)
	{
		ParaQueryUtil.updateTrackingParametric(session, trackingParametric);

	}

	public static void updateApprovalStatus(TrackingParamDocApprov trackingParametricDocVal) throws Exception
	{
		Session session = SessionUtil.getSession();
		try
		{
			ParaQueryUtil.updateTrackingApprovalStatus(session, trackingParametricDocVal);
		}catch(Exception e)
		{
			throw getCatchException(e);
		}finally
		{
			SessionUtil.closeSession(session);
		}

	}


	@SuppressWarnings("unchecked")
	public static List<PartComponent> getPartsByPkgApprovedParametricValue(PkgApprovedValue val, int from, int max) throws Exception
	{
		final Session session = SessionUtil.getSession();
		long groupId = -1;
		try
		{
			groupId = ParaQueryUtil.getGroupByPkgApprovedParametricValue(val, session);
		}catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
		if(groupId == -1)
			return null;
		//
		long col = val.getPkgFeature().getId();

		List<PartComponent> partsList = ParaQueryUtil.getPartsByPkgMainDataColumn(col, from, max, groupId, session);
		if(partsList == null)
			return null;
		partsList = CloneUtil.cloneObjectList(partsList, new ArrayList<String>());
		return partsList;
	}

	public static Exception getCatchException(Exception e) throws Exception
	{
		e.printStackTrace();
		String mes = "";
		if(e.getMessage() != null)
		{
			mes = e.getMessage();
		}
		if(e.getCause() != null)
		{
			mes = mes + "\n" + e.getCause();
		}
		return new Exception(mes);
	}

	public static int getPartsCountByPkgApprovedParametricValue(PkgApprovedValue val) throws Exception
	{
		Session session = null;
		try
		{
			session = SessionUtil.getSession();
			long groupId = ParaQueryUtil.getGroupByPkgApprovedParametricValue(val, session);
			if(groupId <= 0)
				return 0;
			//
			long col = val.getPkgFeature().getId();

			int count = ParaQueryUtil.getPartsCountByPkgMainDataColumn(col, groupId, session);
			return count;
		}catch(Exception e)
		{
			getCatchException(e);
		}finally
		{
			if(session != null)
				SessionUtil.closeSession(session);
		}
		return -1;
	}

	public static void updateTrackingPkgDocApproval(long docuomentId, long pkgApprovalvalueId, String approvalStatus, String comments) throws Exception
	{

		Session session = SessionUtil.getSession();
		try
		{
			TrackingPkgDocApprov trackingPkgDocApproval = ParaQueryUtil.getTrackingPkgDocApproval(session, docuomentId, pkgApprovalvalueId);
			TrackingTaskQaStatus trackingTaskQaStatus = ParaQueryUtil.getTrackingTaskQaStatus(session, approvalStatus);
			if(!approvalStatus.equals("Approved"))
			{
				trackingPkgDocApproval.setComments(comments);
				trackingPkgDocApproval.setTrackingTaskQaStatus(trackingTaskQaStatus);
				updateTrackinPkgApprovalStatus(trackingPkgDocApproval);
			}

		}catch(Exception e)
		{
			throw getCatchException(e);
		}finally
		{
			SessionUtil.closeSession(session);
		}

	}

	private static void updateTrackinPkgApprovalStatus(TrackingPkgDocApprov trackingPkgDocApproval) throws Exception
	{
		Session session = SessionUtil.getSession();
		try
		{
			ParaQueryUtil.updateTrackinPkgApproval(session, trackingPkgDocApproval);
		}catch(Exception e)
		{
			throw getCatchException(e);
		}finally
		{
			SessionUtil.closeSession(session);
		}

	}

	@SuppressWarnings("unchecked")
	public static TrackingPkgDocApprovaleCountDTO getTrackingPkgDocApprovalData(String approvalStatus, String featureName, String fullValue, String taxonomy, String supplierName, String seUrl, Date storeDate, int from, int to) throws Exception
	{

		Session session = SessionUtil.getSession();
		try
		{
			TrackingPkgDocApprovaleCountDTO trackingPkgDocApprovaleCountDTO = new TrackingPkgDocApprovaleCountDTO();
			List<TrackingPkgDocApprovaleDTO> listTrackingPkgDocApprovalValuesDto = new ArrayList<TrackingPkgDocApprovaleDTO>();
			List<TrackingPkgDocApprov> listtrTrackingParamDocApprovs = ParaQueryUtil.getTrackingPkgDocApproval(session, supplierName, seUrl, approvalStatus, featureName, fullValue, storeDate);
			for(TrackingPkgDocApprov trackingDocApprov : listtrTrackingParamDocApprovs)
			{
				Pdf pdf = trackingDocApprov.getDocument().getPdf();
				NonPdf nonPdf = trackingDocApprov.getDocument().getNonPdf();
				String unit = "";
				Unit unit1 = trackingDocApprov.getPkgApprovedValue().getPkgFeature().getUnit();
				unit = "" + (unit1 != null ? unit1.getName() : "");

				List<PartComponent> listComponents = ParaQueryUtil.getComponentByPlNameAndDocuomentIDList(session, trackingDocApprov.getDocument().getId(), taxonomy);
				int count = 0;
				for(PartComponent component : listComponents)
				{
					if(from != 0 && from > count)
					{
						count++;
						continue;
					}
					TrackingPkgDocApprovaleDTO approvalValuedto = new TrackingPkgDocApprovaleDTO();
					approvalValuedto.setPkgApprovedValue(trackingDocApprov.getPkgApprovedValue());
					approvalValuedto.setPartNumber(component.getPartNumber());
					approvalValuedto.setFullValue(trackingDocApprov.getPkgApprovedValue().getPkgValue().getValue());
					approvalValuedto.setFeatureName(trackingDocApprov.getPkgApprovedValue().getPkgFeature().getName());
					if(pdf != null)
					{
						approvalValuedto.setSeUrl(pdf.getSeUrl());
						approvalValuedto.setSupplierName(pdf.getSupplierUrl().getSupplier().getName());
					}
					else
					{
						approvalValuedto.setSeUrl(nonPdf.getSeUrl());
						approvalValuedto.setSupplierName(nonPdf.getSupplierUrl().getSupplier().getName());
					}
					approvalValuedto.setTaxonomy(component.getSupplierPl().getPl().getName());

					approvalValuedto.setUnit(unit);
					if(trackingDocApprov.getTrackingTaskStatus() != null)
						approvalValuedto.setStatus(trackingDocApprov.getTrackingTaskStatus().getName());
					else
						approvalValuedto.setStatus("");
					approvalValuedto.setPkgApprovedValue(trackingDocApprov.getPkgApprovedValue());
					approvalValuedto.setComments(trackingDocApprov.getComments());
					approvalValuedto.setTrackingPkgDocApprovid(trackingDocApprov.getId());
					listTrackingPkgDocApprovalValuesDto.add(approvalValuedto);

					break;
				}
				if(listTrackingPkgDocApprovalValuesDto.size() == to)
					break;

			}

			// }
			List<TrackingPkgDocApprovaleDTO> TrackingParametricApprovalValueData = CloneUtil.cloneObjectList(listTrackingPkgDocApprovalValuesDto, new ArrayList<String>());
			trackingPkgDocApprovaleCountDTO.setListTrackingPkgDocApprovaleDTOs(TrackingParametricApprovalValueData);
			trackingPkgDocApprovaleCountDTO.setCount(listtrTrackingParamDocApprovs.size());
			return trackingPkgDocApprovaleCountDTO;

		}catch(Exception e)
		{

			throw getCatchException(e);
		}finally
		{
			SessionUtil.closeSession(session);
		}
	}

	/**
	 * @deprecated
	 * @param plId
	 * @param documentid
	 * @param maxresult
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static List<ComponentDTO> getPartsParametricValuesGroupByComId(String plname, Long documentid, int maxresult) throws Exception
	{

		Session session = SessionUtil.getSession();
		try
		{
			Long plId = ParaQueryUtil.getPlByPlName(session, plname).getId();

			// List<Component> components = ParaQueryUtil
			// .getPartNumberByDocumentAndPl(documentid, plId, session);
			List<ComponentDTO> listComponentDTOs = new ArrayList<ComponentDTO>();
			List<PlfeatureValuesDTO> listPlfeatureValuesDTOs = null;
			List<PlFeature> features = ParaQueryUtil.getFeaturesByPlId(plId, session);
			try
			{
				// for (final Component component2 : components) {
				//
				// // TODO Auto-generated method stub
				// ComponentDTO componentDTO = new ComponentDTO();
				// componentDTO.setComponent(component2);
				// PartsParametric partsParametric = ParaQueryUtil.getPartParametric(session, component2);
				// List<PlfeatureValuesDTO> listPlfeatureValuesDTOs = new ArrayList<PlfeatureValuesDTO>();
				// for (PlFeature plFeature : features) {
				// PlfeatureValuesDTO plfeatureValuesDTO = new PlfeatureValuesDTO();
				// plfeatureValuesDTO.setPlFeature(plFeature);
				// listPlfeatureValuesDTOs.add(plfeatureValuesDTO);
				// String columnName = plFeature.getColumnName();
				// {
				// columnName = columnName.substring(columnName
				// .lastIndexOf("_") + 1);
				//
				// Method method = PartsParametric.class.getMethod("getPartsParametricValuesGroup"+ columnName);
				// if (partsParametric != null) {
				// Long groupId = (Long) method.invoke(partsParametric);
				//
				// if (groupId != null) {
				// List<ApprovedParametricValue> getApprovedParametricValues = ParaQueryUtil
				// .getApprovedParametricValuesId(groupId,
				// session);
				// for (ApprovedParametricValue value : getApprovedParametricValues) {
				// plfeatureValuesDTO.getValues().add(
				// value.getFullValue());
				// }
				// }
				// }
				//
				// }
				// componentDTO
				// .setListPlfeatureValuesDTO(listPlfeatureValuesDTOs);
				//
				// }
				//
				//
				// listComponentDTOs.add(componentDTO);
				//
				// }
			}catch(Exception ex)
			{

				ex.printStackTrace();
			}

			// if no parts found add one item to draw the header
			if(listComponentDTOs.size() == 0)
			{
				listPlfeatureValuesDTOs = new ArrayList<PlfeatureValuesDTO>();
				for(PlFeature plFeature : features)
				{
					PlfeatureValuesDTO plfeatureValuesDTO = new PlfeatureValuesDTO();
					plfeatureValuesDTO.setPlFeature(plFeature);
					listPlfeatureValuesDTOs.add(plfeatureValuesDTO);
				}
				ComponentDTO componentDTO = new ComponentDTO();
				componentDTO.setListPlfeatureValuesDTO(listPlfeatureValuesDTOs);
				listComponentDTOs.add(componentDTO);
			}

			// listComponentDTOs = CloneUtil.cloneObjectList(listComponentDTOs,
			// new ArrayList<String>());
			return listComponentDTOs;

		}catch(Exception e)
		{
			throw getCatchException(e);
		}finally
		{
			SessionUtil.closeSession(session);
		}

	}

	public static void main(String[] args)
	{
		Session session = SessionUtil.getSession();
		try
		{
			// System.out.println(getDocumentBySeUrl("http://download.siliconexpert.com/pdfs/2010/5/18/3/57/37/730/max_/manual/max6650-max6651.pdf").getId());
			// List list0 = getTrackingFeedbackForTL("", "", "", "", "", "", null, "", 32l);
			// System.out.println(list0.size());
			MapGeneric generic = null;
			String genName = ParaQueryUtil.getNonAlphaPart("004 G75gf").trim();
			Query q = session.createQuery("select o from MapGeneric o " + " where CM.NONALPHANUM (o.generic)=:man");
			q.setParameter("man", genName);

			generic = (MapGeneric) q.uniqueResult();
			// System.out.println("Gen id="+generic.getId());

			String nonalpha = "ab___c__D_f";
			nonalpha = nonalpha.replaceAll("_", "%").replaceAll("(%){2,}", "%");
			System.out.println(nonalpha);
			// List<ComponentDTO> ComponentDTO =
			// getPartsParametricValuesGroupByComId(126L, 1125L, 25);
			// for(ComponentDTO componentDTO2 : ComponentDTO)
			// {
			// System.out.println("PartNumber" +
			// componentDTO2.getComponent().getPartNumber());
			//
			// List<PlfeatureValuesDTO> plfeatureValuesDTO =
			// componentDTO2.getListPlfeatureValuesDTO();
			// for(PlfeatureValuesDTO plfeatureValuesDTO2 : plfeatureValuesDTO)
			// {
			// System.out.println(" Feature Name = " +
			// plfeatureValuesDTO2.getPlFeature().getFeature().getName());
			// List<String> valuesList = plfeatureValuesDTO2.getValues();
			// }
			//
			// }

			// Criteria criteria = session.createCriteria(Document.class);
			// criteria.add(Restrictions.eq("id", 1071L));
			//
			// Document document = (Document) criteria.uniqueResult();
			// List<ComponentDTO> list =new ArrayList<ComponentDTO>();
			// Map<String ,ComponentDTO> map = new Hashtable<String, ComponentDTO>();
			// getPartsParametricValues(1071L,176L,map,list);
			// System.out.println(map.size()+"\t"+list.size());

			// validateDocumentsForDsChange(document, document, 1L, session);
			// ApprovedParametricValue val = new ApprovedParametricValue();
			// long id = Long.parseLong("1163671508014");//2126409 //1163671508014 //1163600664305
			// val.setId(id);
			// val = (ApprovedParametricValue) session.load(ApprovedParametricValue.class, val.getId());
			// List<Component> list = getPartsByApprovedParametricValue(val, "", 0, 10);
			// System.out.println("Components Size Is : " + list.size());
			// for(Component component : list){
			// System.out.println(component.getPartNumber());
			// }

		}catch(Exception e)
		{
			System.out.println(e.getMessage());
		}finally
		{
			SessionUtil.closeSession(session);
		}
	}

	public static List<PartComponent> getListOfPartNumberByDocument(Document document) throws Exception
	{
		Session session = SessionUtil.getSession();
		try
		{
			List<PartComponent> components = ParaQueryUtil.getListOfPartNumberByDocument(document, session);
			@SuppressWarnings("unchecked")
			List<PartComponent> components2 = CloneUtil.cloneObjectList(components, new ArrayList<String>());
			return components2;
		}catch(Exception e)
		{
			e.printStackTrace();
			throw new Exception();
		}finally
		{
			SessionUtil.closeSession(session);
		}

	}

	public static GrmUser getGrmUserById(long userId) throws Exception
	{
		// get grm session not automationLib session
		final Session grmSession = com.se.grm.db.SessionUtil.getSession();
		GrmUser grmUser;
		try
		{
			grmUser = ParaQueryUtil.getGrmUserById(userId, grmSession);
			grmUser = (GrmUser) CloneUtil.cloneObject(grmUser, new ArrayList<String>());
			com.se.grm.db.SessionUtil.closeSession(grmSession);
		}catch(Exception e)
		{
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		return grmUser;
	}

	public static void deleteTrackingParamUserPlRate(TrackingParamUserPlRate tp) throws Exception
	{

		final Session se = SessionUtil.getSession();
		try
		{
			se.delete(tp);
		}catch(ConstraintViolationException e)
		{
			throw new Exception("Row can't be deleted as it is used in another place");
		}catch(Exception e)
		{
			throw getCatchException(e);
		}finally
		{
			SessionUtil.closeSession(se);
		}

	}

	public static int getTrackingParamUserPlRateCount(String TLName, String PLName, String UserName, Long Rate, boolean isDistributedTaxs, GrmUser grmTlUser) throws Exception
	{

		Session session = SessionUtil.getSession();
		try
		{

			return ParaQueryUtil.getTrackingParamUserPlRateCount(session, TLName, PLName, UserName, Rate, isDistributedTaxs, grmTlUser);
		}catch(Exception e)
		{
			throw getCatchException(e);

		}finally
		{
			SessionUtil.closeSession(session);
		}

	}

	public static void addEditeTrackingParamUserPlRate(TrackingParamUserPlRate paramUserPlRate) throws Exception
	{
		Session session = SessionUtil.getSession();
		Session grmSession = com.se.grm.db.SessionUtil.getSession();
		try
		{

			Pl pl = ParaQueryUtil.getPlByPlName(session, paramUserPlRate.getPl().getName());
			if(pl == null)
			{

				throw new Exception("Taxonomy not found");
			}
			System.out.println("pl =" + pl.getName());
			paramUserPlRate.setPl(pl);
			// ------------- get TL ----------
			GrmUser TL = ParaQueryUtil.getGrmUserAndTypeByName(grmSession, paramUserPlRate.getTlName(), "Team Leader");
			if(TL == null)
			{
				throw new Exception("TL not found");
			}
			paramUserPlRate.setTlId(TL.getId());

			// ------------- get user ----------
			if(!paramUserPlRate.getUserName().equals(""))
			{
				GrmUser user = ParaQueryUtil.getGrmUserAndTypeByName(grmSession, paramUserPlRate.getUserName(), "Engineer");
				if(user == null)
				{
					throw new Exception("User not found");
				}
				paramUserPlRate.setUserId(user.getId());
			}
			else
			{
				paramUserPlRate.setUserId(null);
			}
			if(paramUserPlRate.getId() == null)
			{
				paramUserPlRate.setType("Parametric");
				paramUserPlRate.setId(System.nanoTime());
				session.saveOrUpdate(paramUserPlRate);
			}
			else
			{
				session.saveOrUpdate(paramUserPlRate);
			}
		}catch(ConstraintViolationException e)
		{
			throw new Exception("Duplicated row data");
		}catch(Exception e)
		{
			throw getCatchException(e);
		}finally
		{
			SessionUtil.closeSession(session);
			grmSession.close();
		}
	}

	@SuppressWarnings("unchecked")
	public static List<String> suggestDateForUser(final String value, final String className, final String property[], final int maxResult, String type, String OldUserName) throws Exception
	{
		final Session session = com.se.grm.db.SessionUtil.getSession();
		try
		{
			final Criteria crit = session.createCriteria("com.se.grm.client.mapping." + className);
			for(final String element : property)
			{

				crit.add(Restrictions.ilike(element, value, MatchMode.START));

				crit.add(Restrictions.eq("grmRole", ParaQueryUtil.getUserType(type)));

			}

			final ProjectionList projList = Projections.projectionList();

			projList.add(Projections.groupProperty(property[0]));

			crit.setProjection(projList);
			final List<String> list = crit.setMaxResults(maxResult).list();
			if(!OldUserName.equals(""))
			{
				int i = 0;
				for(String string : list)
				{
					if(string.equals(OldUserName))
					{
						list.remove(i);
					}
					i++;
				}
			}
			return list;
		}catch(final Exception e)
		{

			throw getCatchException(e);
		}finally
		{
			com.se.grm.db.SessionUtil.closeSession(session);
		}
	}

	@SuppressWarnings("unchecked")
	public static List<String> suggestDateForUserByTeamLeaderGroup(final String value, final String className, final String property[], final int maxResult, String type, String OldUserName, GrmGroup grmGroup) throws Exception
	{
		final Session session = com.se.grm.db.SessionUtil.getSession();
		try
		{
			final Criteria crit = session.createCriteria("com.se.grm.client.mapping." + className);
			for(final String element : property)
			{
				crit.add(Restrictions.eq("grmGroup", grmGroup));
				crit.add(Restrictions.ilike(element, value, MatchMode.START));

				crit.add(Restrictions.eq("grmRole", ParaQueryUtil.getUserType(type)));

			}

			final ProjectionList projList = Projections.projectionList();

			projList.add(Projections.groupProperty(property[0]));

			crit.setProjection(projList);
			final List<String> list = crit.setMaxResults(maxResult).list();
			if(!OldUserName.equals(""))
			{
				int i = 0;
				for(String string : list)
				{
					if(string.equals(OldUserName))
					{
						list.remove(i);
					}
					i++;
				}
			}
			return list;
		}catch(final Exception e)
		{

			throw getCatchException(e);
		}finally
		{
			com.se.grm.db.SessionUtil.closeSession(session);
		}
	}

	public static void addEditeTrackingPkgTlVendore(TrackingPkgTlVendor trackingPkgTlVendor) throws Exception
	{
		Session se = SessionUtil.getSession();
		try
		{

			Supplier supplier = ParaQueryUtil.getSupplierByExactName(se, trackingPkgTlVendor.getSupplier().getName());
			if(supplier == null)
			{
				throw new Exception("Supplier not found");

			}
			GrmUser user = ParaQueryUtil.getUserByExactName(trackingPkgTlVendor.getUserName(), "");

			if(user == null)
			{
				throw new Exception("User not found");
			}
			trackingPkgTlVendor.setSupplier(supplier);
			System.out.println("supplier id " + supplier.getId());
			System.out.println("supplier" + supplier.getName());
			System.out.println("user id " + user.getId());
			trackingPkgTlVendor.setUserId(user.getId());

			if(trackingPkgTlVendor.getId() == (null))
			{

				trackingPkgTlVendor.setId(System.nanoTime());
				trackingPkgTlVendor.setType("PKG");
				se.save(trackingPkgTlVendor);
			}
			else
			{
				se.saveOrUpdate(trackingPkgTlVendor);
			}
		}catch(ConstraintViolationException e)
		{
			throw new Exception("Duplicated row data");
		}catch(Exception e)
		{
			throw getCatchException(e);
		}finally
		{
			SessionUtil.closeSession(se);
		}

	}

	public static void deleteTrackingPkgTlVendore(TrackingPkgTlVendor trackingPkgTlVendor) throws Exception
	{

		final Session session = SessionUtil.getSession();
		try
		{
			session.delete(trackingPkgTlVendor);
		}catch(Exception e)
		{
			throw getCatchException(e);
		}finally
		{
			SessionUtil.closeSession(session);
		}

	}

	@SuppressWarnings({ "unchecked" })
	public static int getTrackingFeedbackCount(String supplierName, String requestID, Date DeliveryDate, String FBstatus, String taxonomyName, String seUrl, String feedBackType, String trackingTeamName, long grmUserId) throws Exception
	{
		final Session se = SessionUtil.getSession();

		try
		{
			final Criteria crit = se.createCriteria(TrackingFeedback.class);

			List<Pl> pls = ParaQueryUtil.getPlsByTlId(grmUserId, se);

			if(pls.size() > 0)
				crit.add(Restrictions.in("pl", pls));
			else
				return 0;

			crit.createCriteria("trackingTeam").add(Restrictions.eq("name", trackingTeamName));
			Criteria feeedBackType = crit.createCriteria("trackingFeedbackType");
			feeedBackType.add(Restrictions.ne("name", "Internal"));
			Criteria documentCriteria = null;
			if(!seUrl.equals(""))
			{
				documentCriteria = crit.createCriteria("document");
				if(seUrl.endsWith(".pdf"))
					documentCriteria.createCriteria("pdf").add(Restrictions.eq("seUrl", seUrl));
				else
					documentCriteria.createCriteria("nonPdf").add(Restrictions.eq("seUrl", seUrl));
			}

			if(!FBstatus.equals(""))
			{

				TrackingTaskStatus taskStatus = ParaQueryUtil.getTrackingTaskStatusByExactName(se, FBstatus);
				crit.add(Restrictions.eq("trackingTaskStatus", taskStatus));

			}
			// --------------------------------------------------------------------
			if(!supplierName.equals(""))
			{
				// Criteria documentCriteria = crit.createCriteria("document");
				if(documentCriteria == null)
					documentCriteria = crit.createCriteria("document");
				documentCriteria.add(Restrictions.or(
						Restrictions.sqlRestriction("pdf_id IN (SELECT pdf.ID FROM pdf, supplier, supplier_url WHERE supplier.NAME = '" + supplierName + "' AND supplier_url.supplier_id = supplier.ID	AND pdf.supplier_url_id = supplier_url.ID)"),
						Restrictions.sqlRestriction("non_pdf_id IN ( SELECT non_pdf.ID FROM non_pdf, supplier, supplier_url WHERE supplier.NAME = '" + supplierName
								+ "' AND supplier_url.supplier_id = supplier.ID AND non_pdf.supplier_url_id = supplier_url.ID)")));

			}
			// -------------------------------------------------------------------

			if(!requestID.equals(""))
			{
				crit.add(Restrictions.eq("requestId", Long.parseLong(requestID)));
			}

			if(DeliveryDate != null)
			{
				System.out.println("DeliveryDate = " + DeliveryDate.getTime());
				final Calendar cal = Calendar.getInstance();
				cal.setTime(DeliveryDate);
				cal.set(Calendar.MILLISECOND, 0);
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.HOUR_OF_DAY, 0);

				crit.add(Restrictions.between("finishedDate", cal.getTime(), addDay(DeliveryDate)));

			}

			if(!taxonomyName.equals(""))
			{
				crit.createCriteria("pl").add(Restrictions.eq("name", taxonomyName));
			}
			if(!feedBackType.equals(""))
			{
				feeedBackType.add(Restrictions.eq("name", feedBackType));
			}
			crit.setProjection(Projections.rowCount());
			List<Integer> list = crit.list();
			if(list == null)
				return 0;

			return list.get(0);

		}catch(Exception e)
		{

			throw getCatchException(e);
		}

	}

	@SuppressWarnings({ "unchecked" })
	public static List<TrackingFeedback> getTrackingFeedback(String taxonomy, String feedbackType, String trackingTeamName, String taskType, long grmUserId, long tlId) throws Exception
	{
		final Session session = SessionUtil.getSession();
		try
		{
			final Criteria crit = session.createCriteria(TrackingFeedback.class);
			List<Pl> pls = ParaQueryUtil.getPlsByUser(grmUserId, tlId, session);
			if(pls.size() > 0)
				crit.add(Restrictions.in("pl", pls));
			else
				return null;

			crit.createCriteria("trackingTaskStatus").add(Restrictions.in("name", new String[] { "Wrong Data" }));
			if(trackingTeamName != null && !trackingTeamName.trim().isEmpty())
			{
				crit.createCriteria("trackingTeam").add(Restrictions.eq("name", trackingTeamName));
			}

			if(taxonomy != null && !taxonomy.trim().isEmpty())
			{
				crit.createCriteria("pl").add(Restrictions.eq("name", taxonomy));
			}

			if(feedbackType != null && !feedbackType.trim().isEmpty())
			{
				crit.createCriteria("trackingFeedbackType").add(Restrictions.eq("name", feedbackType));
			}
			else
			{
				crit.createCriteria("trackingFeedbackType").add(Restrictions.in("name", new String[] { "QA", "Parametric" }));
			}

			if(taskType != null && !taskType.trim().isEmpty())
			{
				crit.createCriteria("trackingTaskType").add(Restrictions.eq("name", taskType));
			}

			List<TrackingFeedback> list = crit.list();
			if(list == null)
				return null;
			return list;
		}catch(Exception e)
		{
			throw getCatchException(e);
		}
	}

	@SuppressWarnings({ "unchecked" })
	public static List<TrackingFeedback> getTrackingFeedback(String taxonomy, String feedbackType, String trackingTeamName, String taskType, String supplier, String feedbackStatus, Date feedbackDate, String seUrl, long grmUserId, long tlId)
			throws Exception
	{
		final Session session = SessionUtil.getSession();
		try
		{
			final Criteria criteria = session.createCriteria(TrackingFeedback.class);
			List<Pl> pls = ParaQueryUtil.getPlsByUser(grmUserId, tlId, session);
			if(pls.size() > 0)
				criteria.add(Restrictions.in("pl", pls));
			else
				return null;

			criteria.add(Restrictions.isNull("trackingTaskTlStatusId"));
			criteria.add(Restrictions.eq("userId", grmUserId));

			if(trackingTeamName != null && !trackingTeamName.trim().isEmpty())
			{
				criteria.createCriteria("trackingTeam").add(Restrictions.eq("name", trackingTeamName));
			}

			if(taxonomy != null && !taxonomy.trim().isEmpty())
			{
				criteria.createCriteria("pl").add(Restrictions.eq("name", taxonomy));
			}

			if(feedbackType != null && !feedbackType.trim().isEmpty())
			{
				criteria.createCriteria("trackingFeedbackType").add(Restrictions.eq("name", feedbackType));
			}
			else
			{
				criteria.createCriteria("trackingFeedbackType").add(Restrictions.in("name", new String[] { "QA", "Parametric" }));
			}

			if(taskType != null && !taskType.trim().isEmpty())
			{
				criteria.createCriteria("trackingTaskType").add(Restrictions.eq("name", taskType));
			}

			if(feedbackStatus != null && !feedbackStatus.trim().isEmpty())
			{
				criteria.createCriteria("trackingTaskStatus").add(Restrictions.eq("name", feedbackStatus));
			}
			else
			{
				criteria.createCriteria("trackingTaskStatus").add(Restrictions.in("name", new String[] { "Wrong Data", "Wrong Value" }));
			}

			if(seUrl != null && !seUrl.trim().isEmpty())
			{
				Criteria documentCriteria = criteria.createCriteria("document");
				if(seUrl.endsWith(".pdf"))
					documentCriteria.createCriteria("pdf").add(Restrictions.eq("seUrl", seUrl));
				else
					documentCriteria.createCriteria("nonPdf").add(Restrictions.eq("seUrl", seUrl));
			}

			if(supplier != null && !supplier.trim().isEmpty())
			{
				Criteria documentCriteria = criteria.createCriteria("document");
				documentCriteria.createCriteria("pdf").createCriteria("supplierUrl").createCriteria("supplier").add(Restrictions.eq("name", supplier));
			}

			if(feedbackDate != null)
			{
				final Calendar cal = Calendar.getInstance();
				cal.setTime(feedbackDate);
				cal.set(Calendar.MILLISECOND, 0);
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.HOUR_OF_DAY, 0);
				criteria.add(Restrictions.between("feedbackDate", cal.getTime(), addDay(feedbackDate)));
				System.out.println(cal.getTime());
				System.out.println(addDay(feedbackDate));
			}

			List<TrackingFeedback> list = criteria.list();
			System.out.println(criteria.toString());
			if(list == null)
				return null;
			return list;
		}catch(Exception e)
		{
			throw getCatchException(e);
		}
	}

	public static java.util.Date addDay(final Date date)
	{
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.DAY_OF_MONTH, 1);
		return c.getTime();
	}

	@SuppressWarnings({ "unchecked" })
	public static List<TrackingFeedback> getAllTrackingFeedbacksList(String supplierName, String requestID, Date DeliveryDate, String taskStatues, String taxonomyName, String seUrl, String trackingTeamName, String feedBackType, long tlId)
			throws Exception
	{
		final Session se = SessionUtil.getSession();
		try
		{
			final Criteria crit = se.createCriteria(TrackingFeedback.class);
			List<Pl> pls = ParaQueryUtil.getPlsByTlId(tlId, se);

			if(pls.size() > 0)
				crit.add(Restrictions.in("pl", pls));
			else
				return new ArrayList<TrackingFeedback>();
			crit.createCriteria("trackingTeam").add(Restrictions.eq("name", trackingTeamName));
			Criteria feeedBackType = crit.createCriteria("trackingFeedbackType");
			feeedBackType.add(Restrictions.ne("name", "Internal"));

			Criteria documentCriteria = null;
			if(!seUrl.equals(""))
			{
				documentCriteria = crit.createCriteria("document");
				if(seUrl.endsWith(".pdf"))
					documentCriteria.createCriteria("pdf").add(Restrictions.eq("seUrl", seUrl));
				else
					documentCriteria.createCriteria("nonPdf").add(Restrictions.eq("seUrl", seUrl));
			}

			if(!taskStatues.equals(""))
			{
				System.out.println("taskStatues " + taskStatues);
				TrackingTaskStatus taskStatus = ParaQueryUtil.getTrackingTaskStatusByExactName(se, taskStatues);
				crit.add(Restrictions.eq("trackingTaskStatus", taskStatus));

			}
			// --------------------------------------------------------------------
			if(!supplierName.equals(""))
			{
				// Criteria documentCriteria = crit.createCriteria("document");
				if(documentCriteria == null)
					documentCriteria = crit.createCriteria("document");
				documentCriteria.add(Restrictions.or(
						Restrictions.sqlRestriction("pdf_id IN (SELECT pdf.ID FROM pdf, supplier, supplier_url WHERE supplier.NAME = '" + supplierName + "' AND supplier_url.supplier_id = supplier.ID	AND pdf.supplier_url_id = supplier_url.ID)"),
						Restrictions.sqlRestriction("non_pdf_id IN ( SELECT non_pdf.ID FROM non_pdf, supplier, supplier_url WHERE supplier.NAME = '" + supplierName
								+ "' AND supplier_url.supplier_id = supplier.ID AND non_pdf.supplier_url_id = supplier_url.ID)")));

			}
			// -------------------------------------------------------------------

			if(!requestID.equals(""))
			{
				crit.add(Restrictions.eq("requestId", Long.parseLong(requestID)));
			}

			if(DeliveryDate != null)
			{
				System.out.println("DeliveryDate = " + DeliveryDate);
				final Calendar cal = Calendar.getInstance();
				cal.setTime(DeliveryDate);
				cal.set(Calendar.MILLISECOND, 0);
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.HOUR_OF_DAY, 0);

				crit.add(Restrictions.between("finishedDate", cal.getTime(), addDay(DeliveryDate)));

			}
			if(!feedBackType.equals(""))
			{
				feeedBackType.add(Restrictions.eq("name", feedBackType));
			}
			if(!taxonomyName.equals(""))
			{

				Pl pl = ParaQueryUtil.getPlByPlName(se, taxonomyName);
				crit.add(Restrictions.eq("pl", pl));

			}

			List<TrackingFeedback> feedbacks = crit.list();
			System.out.println(feedbacks.size() + "list.size()");

			final Session grmSession = com.se.grm.db.SessionUtil.getSession();
			for(TrackingFeedback trackingFeedback : feedbacks)
			{
				try
				{
					GrmUser grmUser = ParaQueryUtil.getGrmUserById(trackingFeedback.getUserId(), grmSession);
					String fullName = grmUser.getFullName();
					trackingFeedback.setUserName(fullName);
				}catch(Exception e)
				{
					e.printStackTrace();
				}finally
				{
					com.se.grm.db.SessionUtil.closeSession(grmSession);
				}
			}
			return CloneUtil.cloneObjectList(feedbacks, new ArrayList<String>());
		}catch(Exception e)
		{

			throw getCatchException(e);
		}

	}

	@SuppressWarnings({ "unchecked" })
	public static List<TrackingFeedback> getAllTrackingFeedbacksListFromAndTo(String supplierName, String requestID, Date DeliveryDate, String FBstatus, String taxonomyName, String seUrl, String feedBackType, String trackingTeamName, int from,
			int to, long grmUserId) throws Exception
	{
		final Session se = SessionUtil.getSession();
		try
		{
			final Criteria crit = se.createCriteria(TrackingFeedback.class);
			List<Pl> pls = ParaQueryUtil.getPlsByTlId(grmUserId, se);

			if(pls.size() > 0)
				crit.add(Restrictions.in("pl", pls));
			else
				return new ArrayList<TrackingFeedback>();
			crit.createCriteria("trackingTeam").add(Restrictions.eq("name", trackingTeamName));
			Criteria feeedBackType = crit.createCriteria("trackingFeedbackType");
			feeedBackType.add(Restrictions.ne("name", "Internal"));

			Criteria documentCriteria = null;
			if(!seUrl.equals(""))
			{
				documentCriteria = crit.createCriteria("document");
				if(seUrl.endsWith(".pdf"))
					documentCriteria.createCriteria("pdf").add(Restrictions.eq("seUrl", seUrl));
				else
					documentCriteria.createCriteria("nonPdf").add(Restrictions.eq("seUrl", seUrl));
			}

			if(!FBstatus.equals(""))
			{

				TrackingTaskStatus taskStatus = ParaQueryUtil.getTrackingTaskStatusByExactName(se, FBstatus);
				crit.add(Restrictions.eq("trackingTaskStatus", taskStatus));

			}
			// --------------------------------------------------------------------
			if(!supplierName.equals(""))
			{
				if(documentCriteria == null)
					documentCriteria = crit.createCriteria("document");
				documentCriteria.add(Restrictions.or(
						Restrictions.sqlRestriction("pdf_id IN (SELECT pdf.ID FROM pdf, supplier, supplier_url WHERE supplier.NAME = '" + supplierName + "' AND supplier_url.supplier_id = supplier.ID	AND pdf.supplier_url_id = supplier_url.ID)"),
						Restrictions.sqlRestriction("non_pdf_id IN ( SELECT non_pdf.ID FROM non_pdf, supplier, supplier_url WHERE supplier.NAME = '" + supplierName
								+ "' AND supplier_url.supplier_id = supplier.ID AND non_pdf.supplier_url_id = supplier_url.ID)")));
			}
			// -------------------------------------------------------------------

			if(!requestID.equals(""))
			{
				crit.add(Restrictions.eq("requestId", Long.parseLong(requestID)));
			}

			if(DeliveryDate != null)
			{
				System.out.println("DeliveryDate = " + DeliveryDate);
				final Calendar cal = Calendar.getInstance();
				cal.setTime(DeliveryDate);
				cal.set(Calendar.MILLISECOND, 0);
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.HOUR_OF_DAY, 0);
				crit.add(Restrictions.between("finishedDate", cal.getTime(), addDay(DeliveryDate)));
			}

			if(!taxonomyName.equals(""))
			{
				crit.createCriteria("pl").add(Restrictions.eq("name", taxonomyName));
			}
			if(!feedBackType.equals(""))
			{
				feeedBackType.add(Restrictions.eq("name", feedBackType));
			}
			List<TrackingFeedback> feedbacks = crit.setMaxResults(to).setFirstResult(from).list();
			System.out.println(feedbacks.size() + "list.size()");

			final Session grmSession = com.se.grm.db.SessionUtil.getSession();
			for(TrackingFeedback trackingFeedback : feedbacks)
			{
				try
				{
					GrmUser grmUser = ParaQueryUtil.getGrmUserById(trackingFeedback.getUserId(), grmSession);
					String fullName = grmUser.getFullName();
					trackingFeedback.setUserName(fullName);
				}catch(Exception e)
				{
					e.printStackTrace();
				}finally
				{
					com.se.grm.db.SessionUtil.closeSession(grmSession);
				}
			}
			return CloneUtil.cloneObjectList(feedbacks, new ArrayList<String>());
		}catch(Exception e)
		{

			throw getCatchException(e);
		}

	}

	@SuppressWarnings("unchecked")
	public static List<TrackingTaskStatus> getAllTrackingTaskStatues() throws Exception
	{
		Session se = SessionUtil.getSession();
		try
		{
			List<TrackingTaskStatus> taskStatus = ParaQueryUtil.getAllTrackingTaskStatues(se);

			return CloneUtil.cloneObjectList(taskStatus, new ArrayList<String>());

		}catch(Exception e)
		{
			throw getCatchException(e);
		}finally
		{
			SessionUtil.closeSession(se);
		}

	}

	public static void SaveTrackingFeedBack(TrackingFeedback trackingFeedback) throws Exception
	{
		Session session = SessionUtil.getSession();
		try
		{
			TrackingTaskStatus trackingTaskStatus = ParaQueryUtil.getTrackingTaskStatuesByExactName(session, trackingFeedback.getTrackingTaskStatus().getName());
			if(trackingTaskStatus.getName().equals("Rejected"))
			{
				trackingFeedback.setTrackingTaskStatus(ParaQueryUtil.getTrackingTaskStatuesByExactName(session, "Feedback Closed"));
				Document document = trackingFeedback.getDocument();
				if(trackingFeedback.getTrackingTeam().getName().equals("Parametric"))
				{
					TrackingParametric trackingParametric = getTrackingParametricByDocument(document, session);
					if(trackingParametric == null)
					{
						trackingParametric = new TrackingParametric();
						trackingParametric.setId(System.nanoTime());
						trackingParametric.setAssignedDate(trackingFeedback.getAssignedDate());
						trackingParametric.setDocument(document);
						trackingParametric.setParametricUserId(trackingFeedback.getUserId());
						trackingParametric.setPl(trackingFeedback.getPl());
						trackingParametric.setPrioriy(trackingFeedback.getPrioriy());
						trackingParametric.setRequestId(trackingFeedback.getRequestId());
						trackingParametric.setTrackingTaskType(trackingFeedback.getTrackingTaskType());
						trackingParametric.setRecieveDate(trackingFeedback.getRecieveDate());
						trackingParametric.setTrackingTransferStatus(ParaQueryUtil.getTrackingTransferStatus(session, "Pending Parametric"));
					}
					trackingParametric.setTrackingTaskStatus(ParaQueryUtil.getTrackingTaskStatuesByExactName(session, "TL Review Approved"));
					trackingParametric.setTrackingTaskQaStatus(ParaQueryUtil.getTrackingTaskQaStatusByExactName(session, "Pending parametric QA Review"));
					// ------------- now will update tracking parametric
					// ---------------------
					updateTrackingParametric(session, trackingParametric);
				}
				else if(trackingFeedback.getTrackingTeam().getName().equals("PKG"))
				{
					TrackingPkg trackingPkg = getTrackingPkgByDocument(document, session);
					if(trackingPkg == null)
					{
						trackingPkg = new TrackingPkg();
						trackingPkg.setId(System.nanoTime());
						trackingPkg.setAssignedDate(trackingFeedback.getAssignedDate());
						trackingPkg.setDocument(document);
						// trackingPkg.setNumberOfPdfs(trackingFeedback.getNumberOfPdfs());
						trackingPkg.setPkgUserId(trackingFeedback.getUserId());
						trackingPkg.setPl(trackingFeedback.getPl());
						trackingPkg.setPrioriy(trackingFeedback.getPrioriy());
						trackingPkg.setRequestId(trackingFeedback.getRequestId());
						trackingPkg.setTrackingTaskType(trackingFeedback.getTrackingTaskType());
						trackingPkg.setRecieveDate(trackingFeedback.getRecieveDate());
						trackingPkg.setTrackingTransferStatus(ParaQueryUtil.getTrackingTransferStatus(session, "Pending PKG"));
					}
					trackingPkg.setTrackingTaskStatus(ParaQueryUtil.getTrackingTaskStatuesByExactName(session, "TL Review Approved"));
					trackingPkg.setTrackingTaskQaStatus(ParaQueryUtil.getTrackingTaskQaStatusByExactName(session, "Pending PKG QA Review"));
					// -- --- - - - -- - - - now will update tracking PKG - ---
					// --------- --------
					updateTrackingPKG(session, trackingPkg);
				}
			}
			else
			{
				trackingFeedback.setTrackingTaskStatus(ParaQueryUtil.getTrackingTaskStatuesByExactName(session, trackingFeedback.getTrackingTaskStatus().getName()));
			}
			session.saveOrUpdate(trackingFeedback);

		}catch(ConstraintViolationException e)
		{
			throw new Exception("Duplicated row data");
		}catch(Exception e)
		{
			throw getCatchException(e);
		}finally
		{
			SessionUtil.closeSession(session);
		}
	}

	private static TrackingParametric getTrackingParametricByDocument(Document document, Session session)
	{
		Criteria crit = session.createCriteria(TrackingParametric.class);
		crit.add(Restrictions.eq("document", document));

		return (TrackingParametric) crit.uniqueResult();

	}

	private static TrackingPkg getTrackingPkgByDocument(Document document, Session session)
	{
		Criteria crit = session.createCriteria(TrackingPkg.class);
		crit.add(Restrictions.eq("document", document));

		return (TrackingPkg) crit.uniqueResult();

	}

	private static void updateTrackingPKG(Session session, TrackingPkg trackingPkg) throws Exception
	{
		ParaQueryUtil.updateTrackingPKG(session, trackingPkg);

	}

	@SuppressWarnings("unchecked")
	public static List<TrackingFeedbackType> getAllTrackingFeedBackType() throws Exception
	{
		Session se = SessionUtil.getSession();
		try
		{
			List<TrackingFeedbackType> trackingTaskType = ParaQueryUtil.getAllTrackingFeedBackType(se);

			return CloneUtil.cloneObjectList(trackingTaskType, new ArrayList<String>());

		}catch(Exception e)
		{
			throw getCatchException(e);
		}finally
		{
			SessionUtil.closeSession(se);
		}

	}

	public static int getTrackingPkgTlVendoreCount(String supplierName, String userName) throws Exception
	{

		Session session = SessionUtil.getSession();
		try
		{
			return ParaQueryUtil.getTrackingPkgTlVendoreCount(session, supplierName, userName);
		}catch(Exception e)
		{
			throw getCatchException(e);

		}finally
		{
			SessionUtil.closeSession(session);
		}

	}

	// public static void reciveingParameterAndExportOrImport(
	// HttpServletRequest request, HttpServletResponse response)
	// throws Exception {
	//
	// System.out.println("in testServlet");
	// String parameterValue = request.getParameter("flag");
	// String Supplier = request.getParameter("Supplier");
	// String requestId = request.getParameter("requestId");
	//
	// String taskStatues = request.getParameter("taskStatues");
	// String FBType = request.getParameter("FBType");
	// String Taxonomy = request.getParameter("Taxonomy");
	// String SEURL = request.getParameter("SEURL");
	// String trackingTeamName = request.getParameter("TrackingTeamName");
	//
	// Date delieveryDate1 = null;
	//
	// if (parameterValue.equals("export")) {
	// long userId = Long.parseLong(request.getParameter("tlId"));
	// List<TrackingFeedback> list = getAllTrackingFeedbacksList(Supplier,
	// requestId, delieveryDate1, taskStatues, Taxonomy, SEURL,
	// trackingTeamName, FBType, userId);
	// ExportTLReviewUnapprovedValuesUtil.export(list, response);
	// }
	// if (parameterValue.equals("upload")) {
	//
	// importExcelForTlReviewComingFeedBack(request);
	// }
	// }

	private static List<List<String>> readExcelFileOfForTlReviewComingFeedBackAndExport(InputStream inputStream)
	{
		List<List<String>> excelDataList = new ArrayList<List<String>>();
		try
		{
			POIFSFileSystem fs = new POIFSFileSystem(inputStream);
			HSSFWorkbook wb = new HSSFWorkbook(fs);
			HSSFSheet sheet = wb.getSheetAt(0);

			excelDataList = readSheetDataOfForTlReviewComingFeedBackAndExport(sheet);
			// ------------------------------------------------------------------------------

		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return excelDataList;
	}

	private static List<List<String>> readSheetDataOfForTlReviewComingFeedBackAndExport(HSSFSheet sheet)
	{

		List<List<String>> allData = new ArrayList<List<String>>();
		int columnCount = getColumnCount(sheet.getLastRowNum() + 1, sheet);

		for(int i = 0; i < sheet.getLastRowNum() + 1; i++)
		{

			List<String> rowData = new ArrayList<String>();
			HSSFRow row = sheet.getRow(i);
			for(int j = 0; j < columnCount; j++)
			{
				HSSFCell cell = null;

				if(row != null && row.getCell((short) j) != null)
				{
					cell = row.getCell((short) j);
				}
				rowData.add(getCellContent(cell));

			}
			if(i == 0)
			{

				boolean b = checkHeaderOfreadSheetDataOfForTlReviewComingFeedBack(rowData);

				if(!b)
					return allData;

			}
			else
			{
				allData.add(rowData);
			}
		}

		return allData;
	}

	/*
	 * check header of trackingFeedBack tabel
	 */
	private static boolean checkHeaderOfreadSheetDataOfForTlReviewComingFeedBack(List<String> header)
	{

		System.out.println("header.size()     --     " + header.size());
		if(!header.get(0).equalsIgnoreCase("RequestID"))
			return false;
		if(!header.get(1).equalsIgnoreCase("Attach"))
			return false;
		if(!header.get(2).equalsIgnoreCase("PdfNo"))
			return false;
		if(!header.get(3).equalsIgnoreCase("Supplier"))
			return false;
		if(!header.get(4).equalsIgnoreCase("Taxonomy"))
			return false;
		if(!header.get(5).equalsIgnoreCase("SeUrl"))
			return false;
		if(!header.get(6).equalsIgnoreCase("FBDate"))
			return false;
		if(!header.get(7).equalsIgnoreCase("Statues"))
			return false;
		if(!header.get(8).equalsIgnoreCase("Comment"))
			return false;
		if(!header.get(9).equalsIgnoreCase("AssignTo"))
			return false;
		return true;
	}

	// ---------------- get cell content --------------------------
	private static String getCellContent(HSSFCell cell)
	{
		String cellContent = "";
		if(cell != null)
		{
			if(cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC)
			{
				Double d1 = Double.valueOf(cell.getNumericCellValue());
				int i2 = d1.intValue();
				if(i2 == d1)
				{
					cellContent = i2 + "";
				}
				else
				{
					cellContent = cell.getNumericCellValue() + "";
					// System.out.println("cellContent  " + cellContent);
					if(cellContent.contains("E"))
					{
						DecimalFormat dc = new DecimalFormat("#.##");
						cellContent = dc.format(cell.getNumericCellValue());
					}
				}
			}
			else if(cell.getCellType() == HSSFCell.CELL_TYPE_ERROR)
			{
				cellContent = "";
			}
			else
			{
				cellContent = cell.getRichStringCellValue().getString();
			}
			cellContent = cellContent.trim();
			cellContent = getEnglishVersion(cellContent);
		}
		return cellContent;
	}

	// -------------- get column data -------------
	private static int getColumnCount(int rowsCount, HSSFSheet sheet)
	{
		int colsCount = 0;
		for(int k = 0; k < rowsCount; k++)
		{
			if(sheet.getRow(k) != null)
			{
				if(sheet.getRow(k).getLastCellNum() >= colsCount)
				{
					colsCount = (sheet.getRow(k).getLastCellNum());
				}
			}
		}
		return colsCount;
	}

	// =================== get English version ========================

	private static String getEnglishVersion(String str)
	{
		String cellContent = str;
		String englishString = "";

		for(int k = 0; k < cellContent.length(); k++)
		{
			int stringChar = cellContent.charAt(k);
			if(stringChar > 255)
				continue;
			englishString += cellContent.charAt(k);
		}
		return englishString;
	}

	public static TrackingFeedback gettrackingFeedBackBySeUrlAndFeedBackDate(String seUrl, String date, Session se) throws Exception
	{

		try
		{
			Criteria crit = se.createCriteria(TrackingFeedback.class);
			final Calendar cal = Calendar.getInstance();
			cal.setTime(getDate(date + ""));

			// ------------------------------------------add restrict for
			// date----------------------
			crit.add(Restrictions.eq("feedbackDate", cal.getTime()));

			if(seUrl.endsWith(".pdf"))
				crit.createCriteria("document").createCriteria("pdf").add(Restrictions.eq("seUrl", seUrl));
			else
				crit.createCriteria("document").createCriteria("nonPdf").add(Restrictions.eq("seUrl", seUrl));

			return (TrackingFeedback) crit.uniqueResult();

		}catch(Exception e)
		{
			throw getCatchException(e);
		}
	}

	public static TrackingFeedback getTrackingFeedBackByDocument(Document document, Session se)
	{
		try
		{
			Criteria crit = se.createCriteria(TrackingFeedback.class);
			crit.add(Restrictions.eq("document", document));
			return (TrackingFeedback) crit.uniqueResult();
		}catch(Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
	}

	public static Date getDate(String date)
	{
		// DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date today = null;
		try
		{

			today = df.parse(date);
		}catch(ParseException e)
		{
			e.printStackTrace();
		}
		return today;
	}

	public static void saveAlltrackingFeedBack(List<TrackingFeedback> feedback, Session se) throws Exception
	{

		try
		{
			for(TrackingFeedback trackingFeedback : feedback)
			{
				se.saveOrUpdate(trackingFeedback);
			}

		}catch(ConstraintViolationException e)
		{
			throw new Exception("Duplicated row data");
		}catch(Exception e)
		{
			throw getCatchException(e);
		}
	}

	// public static void importExcelForTlReviewComingFeedBack(
	// HttpServletRequest request) throws Exception {
	//
	// if (!ServletFileUpload.isMultipartContent(request))
	// return;
	//
	// List<List<String>> excelDataList = new ArrayList<List<String>>();
	// FileItemFactory factory = new DiskFileItemFactory();
	// ServletFileUpload upload = new ServletFileUpload(factory);
	// List<?> items = null;
	// try {
	// items = upload.parseRequest(request);
	// } catch (FileUploadException e) {
	// e.printStackTrace();
	// return;
	// }
	//
	// for (Iterator<?> i = items.iterator(); i.hasNext();) {
	// FileItem item = (FileItem) i.next();
	// if (item.isFormField())
	// continue;
	// try {
	//
	// excelDataList = readExcelFileOfForTlReviewComingFeedBackAndExport(item
	// .getInputStream());
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }
	// if (excelDataList.size() > 0) {
	//
	// List<List<String>> unSavedRows = saveUploadExcelSheetDataOfQualityReviewAndExportPackage(excelDataList);
	// if (unSavedRows.size() > 0) {
	// request.getSession().setAttribute("unSavedRows", unSavedRows);
	// } else {
	// request.getSession().setAttribute("unSavedRows", null);
	// }
	// }
	// }

	public static void closeTrackingFeedback(TrackingFeedback trackingFeedback) throws Exception
	{
		Session session = SessionUtil.getSession();
		try
		{
			TrackingTaskStatus trackingTaskStatus = ParaQueryUtil.getTrackingTaskStatus(session, "Feedback Closed");
			trackingFeedback.setTrackingTaskStatus(trackingTaskStatus);
			ParaQueryUtil.updateTrackingFeedback(session, trackingFeedback);

		}catch(Exception e)
		{
			throw getCatchException(e);
		}finally
		{
			if(session != null)
				SessionUtil.closeSession(session);
		}

	}

	public static void saveDataSheetAlert(List<TrackingDatasheetAlert> trackingDatasheetAlertslist) throws Exception
	{
		Session session = SessionUtil.getSession();
		try
		{
			for(TrackingDatasheetAlert trackingDatasheetAlert : trackingDatasheetAlertslist)

			{
				trackingDatasheetAlert.setId(System.nanoTime());
				trackingDatasheetAlert.setChangeDate(ParaQueryUtil.getDate(new Date()));
				ParaQueryUtil.saveDataSheetAlert(trackingDatasheetAlert, session);

			}

		}catch(Exception e)
		{
			throw getCatchException(e);

		}finally
		{
			SessionUtil.closeSession(session);
		}

	}

	public static void updateTrackingDataSheetAlertByAlertCustomer(TrackingDatasheetAlert trackingDatasheetAlert, Boolean alertCustomer) throws Exception
	{
		Session session = SessionUtil.getSession();
		try
		{
			TrackingDatasheetAlert trackingDatasheetAlert2 = ParaQueryUtil.getTrackingDatasheetAlertByObject(session, trackingDatasheetAlert);
			trackingDatasheetAlert2.setTlFlag(true);
			trackingDatasheetAlert2.setAlertCustomer(alertCustomer);
			ParaQueryUtil.saveDataSheetAlert(trackingDatasheetAlert2, session);

		}catch(Exception e)
		{
			throw getCatchException(e);
		}finally
		{
			SessionUtil.closeSession(session);
		}

	}

	public static int getCountTrackingSheetAlert(String partNumber) throws Exception
	{
		Session session = SessionUtil.getSession();
		try
		{
			return ParaQueryUtil.getCountTrackingSheetAlert(session, partNumber);
		}catch(Exception e)
		{
			throw getCatchException(e);

		}finally
		{
			SessionUtil.closeSession(session);
		}

	}

	@SuppressWarnings("unchecked")
	public static List<TrackingDatasheetAlert> getTrackingDatasheetAlert(String partNumber, int from, int to) throws Exception
	{
		Session session = SessionUtil.getSession();
		Session grmSession = com.se.grm.db.SessionUtil.getSession();
		try
		{
			List<TrackingDatasheetAlert> trackingDatasheetAlertList = ParaQueryUtil.getTrackingDatasheetAlertList(session, partNumber, from, to);
			for(TrackingDatasheetAlert trackingDatasheetAlert : trackingDatasheetAlertList)
			{

				GrmUser grmUser = ParaQueryUtil.getUserByUserId(trackingDatasheetAlert.getUserId(), grmSession);

				trackingDatasheetAlert.setUserName(grmUser.getFullName());

			}

			return CloneUtil.cloneObjectList(trackingDatasheetAlertList, new ArrayList<String>());
		}catch(Exception e)
		{
			throw getCatchException(e);

		}finally
		{
			SessionUtil.closeSession(session);
			com.se.grm.db.SessionUtil.closeSession(grmSession);
		}

	}

	// -----------------------salah------------------------
	public static Sign getSignByExactName(String signName) throws Exception
	{
		Session session = SessionUtil.getSession();
		try
		{
			Sign returnedSign = ParaQueryUtil.getSignByExactName(signName, session);
			Sign cloneSign = CloneUtil.cloneSign(returnedSign);
			return cloneSign;

		}catch(Exception e)
		{
			throw getCatchException(e);
		}finally
		{
			SessionUtil.closeSession(session);
		}
	}

	public static Multiplier getMultiplierByExactName(String multiplierName) throws Exception
	{
		Session session = SessionUtil.getSession();
		try
		{
			Multiplier returnedMultiplier = ParaQueryUtil.getMultiplierByExactName(multiplierName, session);
			Multiplier cloneMultiplier = CloneUtil.cloneMultiplier(returnedMultiplier);
			return cloneMultiplier;

		}catch(Exception e)
		{
			throw getCatchException(e);
		}finally
		{
			SessionUtil.closeSession(session);
		}
	}

	public static Unit getUnitByExactName(String unitName) throws Exception
	{
		Session session = SessionUtil.getSession();
		try
		{
			Unit returnedUnit = ParaQueryUtil.getUnitByExactName(unitName, session);
			Unit cloneUnit = CloneUtil.cloneUnit(returnedUnit);
			return cloneUnit;

		}catch(Exception e)
		{
			throw getCatchException(e);
		}finally
		{
			SessionUtil.closeSession(session);
		}
	}

	public static Condition getConditionByExactName(String conditionName) throws Exception
	{
		Session session = SessionUtil.getSession();
		try
		{
			Condition returnedCondition = ParaQueryUtil.getConditionByExactName(conditionName, session);
			Condition cloneCondition = CloneUtil.cloneCondition(returnedCondition);
			return cloneCondition;

		}catch(Exception e)
		{
			throw getCatchException(e);
		}finally
		{
			SessionUtil.closeSession(session);
		}
	}

	public static ValueType getValueTypeByExactName(String valueTypeName) throws Exception
	{
		Session session = SessionUtil.getSession();
		try
		{
			ValueType returnedValueType = ParaQueryUtil.getValueTypeByExactName(valueTypeName, session);
			ValueType cloneValueType = CloneUtil.cloneValueType(returnedValueType);
			return cloneValueType;

		}catch(Exception e)
		{
			throw getCatchException(e);
		}finally
		{
			SessionUtil.closeSession(session);
		}
	}

	public static void saveorUpdateMultiplier(Multiplier multiplier) throws Exception
	{
		Session session = SessionUtil.getSession();
		try
		{
			if(multiplier.getId() == null)
			{
				multiplier.setId(System.currentTimeMillis());
				multiplier.setStoreDate(new java.util.Date());
				ParaQueryUtil.saveMultiplier(session, multiplier);

			}
			else
			{
				ParaQueryUtil.updateMultiplier(session, multiplier);

			}

		}

		catch(Exception e)
		{
			throw getCatchException(e);
		}finally
		{
			SessionUtil.closeSession(session);
		}
	}

	public static void saveOrUpdateCondition(Condition condition) throws Exception
	{
		Session session = SessionUtil.getSession();
		try
		{
			if(condition.getId() == null)
			{
				condition.setId(System.currentTimeMillis());
				condition.setStoreDate(new java.util.Date());

				ParaQueryUtil.saveCondition(session, condition);
			}
			else
			{
				ParaQueryUtil.updateCondition(session, condition);
			}

		}catch(Exception e)
		{
			throw getCatchException(e);
		}finally
		{
			SessionUtil.closeSession(session);
		}
	}

	public static void saveOrUpdateValueType(ValueType valueType) throws Exception
	{
		Session session = SessionUtil.getSession();
		try
		{

			if(valueType.getId() == null)
			{
				valueType.setId(System.currentTimeMillis());
				valueType.setStoreDate(new java.util.Date());

				ParaQueryUtil.saveValueType(session, valueType);
			}
			else
			{
				ParaQueryUtil.updateValueType(session, valueType);
			}

		}catch(ConstraintViolationException e)
		{
			throw getCatchException(e);

		}finally
		{
			SessionUtil.closeSession(session);
		}

	}

	@SuppressWarnings("unchecked")
	public static List<String> suggestDateForPLName(String value, String className, String[] property, int maxResult, String plName) throws Exception
	{
		final Session session = SessionUtil.getSession();
		try
		{
			final Criteria crit = session.createCriteria("com.se.automation.db.client.mapping." + className);
			for(final String element : property)
			{
				crit.add(Restrictions.ilike(element, value, MatchMode.START));
			}
			final ProjectionList projList = Projections.projectionList();
			projList.add(Projections.groupProperty(property[0]));

			if(!plName.trim().equals(""))
			{
				crit.createCriteria("plFeatures").createCriteria("pl").add(Restrictions.ilike("name", plName));
			}
			crit.setProjection(projList);
			final List<String> list = crit.setMaxResults(maxResult).list();
			return list;
		}catch(final Exception e)
		{
			e.printStackTrace();
			throw new Exception("suggest Date\n" + e.getCause());
		}finally
		{
			SessionUtil.closeSession(session);
		}
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
				multiplierUnit.setMultiplier(multiplier);
				multiplierUnit.setUnit(unit);
				multiplierUnit.setStoreDate(new Date());
				session.save(multiplierUnit);
			}
		}
		return multiplierUnit;
	}

	public static void updateStatusTrackingParametricAndSaveComments(Document document, String status, String comments) throws Exception
	{
		Session session = SessionUtil.getSession();
		try
		{
			TrackingParametric trackingParametric = ParaQueryUtil.getTrackingParametricByDocument(session, document);
			if(trackingParametric != null)
			{
				trackingParametric.setTrackingTaskStatus(ParaQueryUtil.getTrackingTaskStatus(session, status));
				trackingParametric.setComments(comments);
				ParaQueryUtil.updateTrackingParametric(session, trackingParametric);
			}

		}catch(Exception e)
		{
			throw getCatchException(e);
		}finally
		{
			SessionUtil.closeSession(session);
		}
	}

	public static void saveUnit(Unit unit)
	{
		Session session = SessionUtil.getSession();
		session.save(unit);
	}

	public static void saveSign(Sign sign)
	{
		Session session = SessionUtil.getSession();
		session.save(sign);
	}

	public static boolean movePartToRevisionSeUrl(String partNumber, long documentId, String toSeUrl) throws Exception
	{
		Session session = SessionUtil.getSession();
		Document toDocument = ParaQueryUtil.getDocumentBySeUrl(toSeUrl, session);
		if(toDocument == null)
			return false;

		PartComponent component = ParaQueryUtil.getComponentByPartAndSupplierAndDocument(partNumber, documentId, session);
		if(component == null)
			return false;

		component.setDocument(toDocument);
		session.update(component);
		session.beginTransaction().commit();
		SessionUtil.closeSession(session);
		return true;
	}



	private static List<String> readExcelFileOfForDistributionAndExport(InputStream inputStream)
	{

		List<String> excelDataList = new ArrayList<String>();
		try
		{
			POIFSFileSystem fs = new POIFSFileSystem(inputStream);
			HSSFWorkbook wb = new HSSFWorkbook(fs);
			HSSFSheet sheet = wb.getSheetAt(0);

			excelDataList = readSheetDataOfForDistributionByPlsAndExport(sheet);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return excelDataList;
	}

	private static List<String> readSheetDataOfForDistributionByPlsAndExport(HSSFSheet sheet)
	{

		List<String> allData = new ArrayList<String>();
		int columnCount = getColumnCount(sheet.getLastRowNum() + 1, sheet);

		for(int i = 0; i < sheet.getLastRowNum() + 1; i++)
		{

			String rowData = new String();
			HSSFRow row = sheet.getRow(i);
			for(int j = 0; j < columnCount; j++)
			{
				HSSFCell cell = null;

				if(row != null && row.getCell((short) j) != null)
				{
					cell = row.getCell((short) j);
				}
				// rowData.add(getCellContent(cell));
				rowData = getCellContent(cell);

			}
			if(i == 0)
			{

				boolean b = checkHeaderOfreadSheetDataOfForDistributionByPls(rowData);

				if(!b)
					return allData;

			}
			else
			{
				allData.add(rowData);

			}
		}

		return allData;
	}

	private static boolean checkHeaderOfreadSheetDataOfForDistributionByPls(String header)
	{

		System.out.println("header.size()     --     " + header);

		System.out.println("HEADER   --   " + header);

		if(!header.equalsIgnoreCase("Taxonomy"))
			return false;

		return true;
	}

	private static List<PartComponent> getComponentListByPls(List<String> plList) throws Exception
	{
		Session session = SessionUtil.getSession();
		try
		{
			List<PartComponent> componentlist = ParaQueryUtil.getComponentListByPLS(plList, session);
			System.out.println("componentlist size =" + componentlist);
			long l = System.nanoTime();
			for(PartComponent component : componentlist)
			{

				String newDiscription = ParaQueryUtil.getNewDiscription(component.getSupplierPl().getPl().getId(), component.getComId(), session);
				if(newDiscription != null)
				{
					component.setDescription(newDiscription);
				}

			}

			return componentlist;

		}catch(Exception e)
		{
			throw getCatchException(e);
		}finally
		{
			SessionUtil.closeSession(session);
		}

	}

	private static List<List<String>> readExcelFileOfForDistributionByPartNumberAndVendorAndExport(InputStream inputStream)
	{
		List<List<String>> excelDataList = new ArrayList<List<String>>();
		try
		{
			POIFSFileSystem fs = new POIFSFileSystem(inputStream);
			HSSFWorkbook wb = new HSSFWorkbook(fs);
			HSSFSheet sheet = wb.getSheetAt(0);
			excelDataList = readSheetDataOfForDistributionByPartNumberAndVendorAndExport(sheet);

		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return excelDataList;
	}

	private static List<List<String>> readSheetDataOfForDistributionByPartNumberAndVendorAndExport(HSSFSheet sheet)
	{
		List<List<String>> allData = new ArrayList<List<String>>();
		int columnCount = getColumnCount(sheet.getLastRowNum() + 1, sheet);

		for(int i = 0; i < sheet.getLastRowNum() + 1; i++)
		{

			List<String> rowData = new ArrayList<String>();
			HSSFRow row = sheet.getRow(i);
			for(int j = 0; j < columnCount; j++)
			{
				HSSFCell cell = null;

				if(row != null && row.getCell((short) j) != null)
				{
					cell = row.getCell((short) j);
				}
				rowData.add(getCellContent(cell));

			}
			if(i == 0)
			{
				boolean b = checkHeaderOfreadSheetDataOfForDistributionByPartNumberAndVendor(rowData);
				if(!b)
					return allData;
			}
			else
			{
				allData.add(rowData);
			}
		}

		return allData;
	}

	private static boolean checkHeaderOfreadSheetDataOfForDistributionByPartNumberAndVendor(List<String> header)
	{
		if(!header.get(0).equalsIgnoreCase("PartNumber"))
			return false;
		if(!header.get(1).equalsIgnoreCase("Vendor"))
			return false;
		return true;
	}

	@SuppressWarnings("unchecked")
	public static List<String> suggestDateForSupplierPLName(String value, String className, String[] property, int maxResult, String SupplierName/*
																																				 * ,
																																				 * boolean
																																				 * flag
																																				 */) throws Exception
	{
		final Session session = SessionUtil.getSession();
		try
		{
			final Criteria crit = session.createCriteria("com.se.automation.db.client.mapping." + className);
			for(final String element : property)
			{
				crit.add(Restrictions.ilike(element, value, MatchMode.START));
			}
			final ProjectionList projList = Projections.projectionList();
			projList.add(Projections.groupProperty(property[0]));

			if(!SupplierName.trim().equals(""))
			{
				crit.createCriteria("supplierPls").createCriteria("supplier").add(Restrictions.ilike("name", SupplierName));
			}
			crit.setProjection(projList);
			final List<String> list = crit.setMaxResults(maxResult).list();
			return list;
		}catch(final Exception e)
		{
			e.printStackTrace();
			throw new Exception("suggest Date\n" + e.getCause());
		}finally
		{
			SessionUtil.closeSession(session);
		}
	}

	private static void readApplayDescriptionFile(InputStream inputStream) throws Exception
	{
		// Session session = SessionUtil.getSession();
		// java.io.File file = new java.io.File(
		// "C:/WINDOWS/Temp/DescriptionFile.txt");
		// FileWriter outFile = new FileWriter(file);
		// PrintWriter out = new PrintWriter(outFile);
		// BufferedReader bufferedReader = new BufferedReader(
		// new InputStreamReader(inputStream));
		// LogFileDTO logFileDTO = new LogFileDTO();
		// try {
		//
		// int lineCount = 0;
		// String line = null;
		// while ((line = bufferedReader.readLine()) != null) {
		// try {
		// String[] lineDataStr = line.split("\t");
		// if (lineCount == 0) {
		// if (!checkDescriptionApplyHeader(lineDataStr)) {
		// out.println(" Invalid Header ! ");
		// out.flush();
		// out.close();
		// downloadFile(response, file.getPath());
		// return;
		// }
		// lineCount = 1;
		// out.println("PARTNUMBER" + "\t" + "VENDOR" + "\t"
		// + "OLDDESCRIPTION" + "\t" + "NEWDESCRIPTION"
		// + "\t" + "TAXONOMY" + "\t" + "STATUS" + "\t"
		// + "COMMENTS" + "\n");
		// continue;
		// }
		//
		// if (lineDataStr.length == 5) {
		// String partnumber = lineDataStr[0];
		// String suppliername = lineDataStr[1];
		// String oldDes = lineDataStr[2];
		// String newdes = lineDataStr[3];
		// String taxonomy = lineDataStr[4];
		// logFileDTO.setPartNumber(partnumber);
		// logFileDTO.setSupplier(suppliername);
		// logFileDTO.setOldDes(oldDes);
		// logFileDTO.setNewDes(newdes);
		// logFileDTO.setTaxonomy(taxonomy);
		// Component component = ParaQueryUtil
		// .getComponentByPartNumberAndSupplierName(
		// partnumber, suppliername, session);
		// String log = null;
		// if (component == null) {
		// logFileDTO.setStatus("Problem");
		// logFileDTO.setComments("Wrong Part");
		// log = logFileDTO.toString();
		// out.println(log);
		// } else {
		// logFileDTO.setStatus("ok");
		// logFileDTO.setComments("");
		// log = logFileDTO.toString();
		// out.println(log);
		// component.setDescription(newdes);
		// ParaQueryUtil
		// .updateComponentByNewDescription(component,
		// session);
		//
		// }
		// } else
		// out.println(line + "\t" + "Problem" + "\t"
		// + "Invalid Row" + "\n");
		// } catch (Exception e) {
		// out.println(logFileDTO.toString()
		// + "\t"
		// + "Problem"
		// + "\t"
		// + getCatchException(e).getMessage().replace("\n",
		// ":"));
		// throw getCatchException(e);
		// }
		// }
		//
		// } catch (Exception e) {
		// out.println(logFileDTO.toString() + "\t" + "Problem" + "\t"
		// + getCatchException(e).getMessage().replace("\n", ":"));
		// throw getCatchException(e);
		//
		// } finally {
		// SessionUtil.closeSession(session);
		// bufferedReader.close();
		// out.flush();
		// out.close();
		// downloadFile(response, file.getPath());
		// java.io.File files = new java.io.File(file.getPath());
		// files.delete();
		// files = null;
		// }

	}

	private static boolean checkDescriptionApplyHeader(String[] lineDataStr)
	{

		if(lineDataStr.length != 5)
			return false;

		return true;
	}

	public static List<String> validateNewParts(Supplier supplier, List<String> newParts) throws Exception
	{
		Session session = SessionUtil.getSession();
		try
		{
			System.out.println("supp id" + supplier.getId());
			List<String> validParts = new ArrayList<String>();
			List<String> oldPartsList = ParaQueryUtil.getPartNumberBySupplier(supplier, newParts, session);
			for(String string : newParts)
			{
				if(!oldPartsList.contains(string))
				{
					System.out.println("oldPartsList Not Contain " + string);
					validParts.add(string);
				}
				System.out.println("validParts.size() " + validParts.size());
			}
			return validParts;

		}catch(Exception e)
		{

			throw getCatchException(e);
		}finally
		{
			SessionUtil.closeSession(session);
		}

	}

	@SuppressWarnings("unchecked")
	private static boolean checkIfDocumentHasParts(Document formDocument, Session session)
	{
		Criteria criteria = session.createCriteria(PartComponent.class);
		criteria.add(Restrictions.eq("document", formDocument));
		List<PartComponent> components = criteria.list();
		if(components.size() > 0)
			return true;

		return false;
	}

	static Map<String, ComponentDTO> componentsmap;

	
	private static Set<Long> getSubSet(Set<Long> fullSet, int from, int to)
	{
		// subSet the Set to make the in performance better than in case of
		// using (in)
		List<Long> list = new ArrayList<Long>(fullSet);
		Set<Long> subSet = new HashSet<Long>();

		for(int i = from; i < to; i++)
			subSet.add(list.get(i));

		return subSet;

	}

	protected static Set<Long> getGroupIDs(List list)
	{
		Set<Long> groupIdSet = new HashSet<Long>();

		for(Object object : list)
		{
			Object[] rowValue = (Object[]) object;
			for(int i = 0; i < rowValue.length; i++)
			{
				if(rowValue[i] != null)
				{
					if(i > 6)
					{
						if(!rowValue[i].equals(""))
						{
							// ------- getGroupID --------
							Long groupID = new Long(rowValue[i] + "");
							groupIdSet.add(groupID);
						}
					}
				}
			}
		}
		return groupIdSet;
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
		return ParaQueryUtil.getAllVendorsFromDocuments(userId, forCS, forTaxonomyTransfer, forDaily, forUpdate, forFast);
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
		return ParaQueryUtil.getAllTaxonomiesFromDocuments(userId, forCS, forTaxonomyTransfer, forDaily, forUpdate, forFast);
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
		return ParaQueryUtil.getPdfBySeUrl(seUrl);
	}

	/**
	 * 
	 * @author Ahmed_Elreweeny
	 * @param latestUrlId
	 * @param pdfUrlId
	 * @return
	 */
	public static TblPdfStatic getTblPdfStatic(long latestId, long pdfId)
	{
		return ParaQueryUtil.getTblPdfStatic(latestId, pdfId);
	}

	/**
	 * 
	 * @author Ahmed_Elreweeny
	 * @param latestUrlId
	 * @param pdfUrlId
	 * @return
	 */
	public static TblPdfCompare getTblPdfCompare(long latestUrlId, long pdfUrlId)
	{
		return ParaQueryUtil.getTblPdfCompare(latestUrlId, pdfUrlId);
	}
	public static boolean deletePartNumberByPartAndSupplier(String partNumber, Supplier supplier, Pl pl, Session session)
	{
		try
		{
			Criteria criteria = session.createCriteria(PartComponent.class);
			criteria.add(Restrictions.eq("partNumber", partNumber));
			Criteria supplierPlCriteria = criteria.createCriteria("supplierPl");
			supplierPlCriteria.add(Restrictions.eq("supplier", supplier));
			supplierPlCriteria.add(Restrictions.eq("pl", pl));
			PartComponent component = (PartComponent) criteria.uniqueResult();
			// Query query = session.createQuery("delete from PartsParametric where component = :component");
			// query.setParameter("component", component);
			// query.executeUpdate();
			Criteria partsParamCriteria = session.createCriteria(PartsParametric.class);
			partsParamCriteria.add(Restrictions.eq("component", component));
			PartsParametric partsParametric = (PartsParametric) partsParamCriteria.uniqueResult();
			session.delete(partsParametric);
			session.delete(component);
			session.beginTransaction().commit();
			session.beginTransaction();
			return true;
		}catch(Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}

	public static void updateFeedbackStatus(TrackingFeedback trackingFeedback, String status, Session session)
	{
		try
		{
			trackingFeedback = (TrackingFeedback) session.load(TrackingFeedback.class, trackingFeedback.getId());
			TrackingTaskStatus trackingTaskStatus = ParaQueryUtil.getTrackingTaskStatusByExactName(session, status);
			// trackingFeedback.setTrackingTaskStatus(trackingTaskStatus);
			trackingFeedback.setTrackingTaskTlStatusId(trackingTaskStatus.getId());
			session.beginTransaction().commit();
			session.beginTransaction();
		}catch(Exception ex)
		{
			ex.printStackTrace();
			return;
		}
	}

	@SuppressWarnings({ "unchecked" })
	public static List<TrackingFeedback> getTrackingFeedbackForTL(String taxonomy, String feedbackType, String trackingTeamName, String taskType, String supplier, String feedbackStatus, Date feedbackDate, String seUrl, long tlId) throws Exception
	{
		final Session session = SessionUtil.getSession();
		try
		{
			final Criteria criteria = session.createCriteria(TrackingFeedback.class);
			List<Pl> pls = ParaQueryUtil.getPlsByTL(tlId, session);
			if(pls.size() > 0)
				criteria.add(Restrictions.in("pl", pls));
			else
				return null;

			List<Long> users = ParaQueryUtil.getUserByTlId(tlId);
			if(users != null && users.size() > 0)
				criteria.add(Restrictions.in("userId", users));

			criteria.add(Restrictions.eq("trackingTaskTlStatusId", 4l));

			if(trackingTeamName != null && !trackingTeamName.trim().isEmpty())
			{
				criteria.createCriteria("trackingTeam").add(Restrictions.eq("name", trackingTeamName));
			}

			if(taxonomy != null && !taxonomy.trim().isEmpty())
			{
				criteria.createCriteria("pl").add(Restrictions.eq("name", taxonomy));
			}

			if(feedbackType != null && !feedbackType.trim().isEmpty())
			{
				criteria.createCriteria("trackingFeedbackType").add(Restrictions.eq("name", feedbackType));
			}
			else
			{
				criteria.createCriteria("trackingFeedbackType").add(Restrictions.in("name", new String[] { "QA", "Parametric" }));
			}

			if(taskType != null && !taskType.trim().isEmpty())
			{
				criteria.createCriteria("trackingTaskType").add(Restrictions.eq("name", taskType));
			}

			if(feedbackStatus != null && !feedbackStatus.trim().isEmpty())
			{
				criteria.createCriteria("trackingTaskStatus").add(Restrictions.eq("name", feedbackStatus));
			}
			else
			{
				criteria.createCriteria("trackingTaskStatus").add(Restrictions.in("name", new String[] { "Wrong Data", "Wrong Value" }));
			}

			if(seUrl != null && !seUrl.trim().isEmpty())
			{
				Criteria documentCriteria = criteria.createCriteria("document");
				if(seUrl.endsWith(".pdf"))
					documentCriteria.createCriteria("pdf").add(Restrictions.eq("seUrl", seUrl));
				else
					documentCriteria.createCriteria("nonPdf").add(Restrictions.eq("seUrl", seUrl));
			}

			if(supplier != null && !supplier.trim().isEmpty())
			{
				Criteria documentCriteria = criteria.createCriteria("document");
				documentCriteria.createCriteria("pdf").createCriteria("supplierUrl").createCriteria("supplier").add(Restrictions.eq("name", supplier));
			}

			if(feedbackDate != null)
			{
				final Calendar cal = Calendar.getInstance();
				cal.setTime(feedbackDate);
				cal.set(Calendar.MILLISECOND, 0);
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.HOUR_OF_DAY, 0);
				criteria.add(Restrictions.between("feedbackDate", cal.getTime(), addDay(feedbackDate)));
				System.out.println(cal.getTime());
				System.out.println(addDay(feedbackDate));
			}

			List<TrackingFeedback> list = criteria.list();
			System.out.println(criteria.toString());
			if(list == null)
				return null;
			return list;
		}catch(Exception e)
		{
			throw getCatchException(e);
		}
	}

	public static void updateFeedbackStatus(TrackingFeedback trackingFeedback, Session session)
	{
		try
		{
			trackingFeedback = (TrackingFeedback) session.load(TrackingFeedback.class, trackingFeedback.getId());
			trackingFeedback.setTrackingTaskTlStatusId(null);
			session.beginTransaction().commit();
			session.beginTransaction();
		}catch(Exception ex)
		{
			ex.printStackTrace();
			return;
		}
	}

}
