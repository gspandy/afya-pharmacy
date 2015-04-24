package com.ndz.controller;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.humanresext.util.HumanResUtil;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Label;

import com.ndz.zkoss.HrmsUtil;
import com.ndz.zkoss.util.HrmsInfrastructure;

public class PerfReviewUtil extends HrmsUtil {
	private static GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
	private static java.text.SimpleDateFormat formater = new java.text.SimpleDateFormat("dd-MM-yyyy");
	public static void checkAnnouncementDate(Datebox fromDate, Datebox thruDate) throws WrongValueException, ParseException {
		Date todayDate = new Date();
		if (fromDate.getValue() == null) {
			throw new WrongValueException(fromDate, "From Date Required");
		}
		if (HrmsUtil.dateCompare(todayDate, fromDate.getValue()) == 1) {
			throw new WrongValueException(fromDate, "From Date Must Be Current Or Future Date");
		}
		if (thruDate.getValue() == null) {
			throw new WrongValueException(thruDate, "Thru Date Required");
		}
		if (HrmsUtil.dateCompare(todayDate, thruDate.getValue()) == 1) {
			throw new WrongValueException(thruDate, "Thru Date Must Be Current Or Future Date");
		}
		if (HrmsUtil.dateCompare(fromDate.getValue(), thruDate.getValue()) == 1) {
			throw new WrongValueException(thruDate, "Thru Date Must Be Greater Than Or Equal To From Date");
		}
	}

	public static void checkTimePeriod(Datebox periodStartDate, Datebox periodThruDate) throws WrongValueException, ParseException {
		Date todayDate = new Date();
		if (periodStartDate.getValue() == null) {
			throw new WrongValueException(periodStartDate, "Period Start Date Required");
		}
		if (HrmsUtil.dateCompare(todayDate, periodStartDate.getValue()) == -1 || HrmsUtil.dateCompare(todayDate, periodStartDate.getValue()) == 0) {
			throw new WrongValueException(periodStartDate, "Period Start Date Can Not Be Greater Than Or Equal To Current Date");
		}
		if (periodThruDate.getValue() == null) {
			throw new WrongValueException(periodThruDate, "Period Thru Date Required");
		}
		if (HrmsUtil.dateCompare(todayDate, periodThruDate.getValue()) == -1 || HrmsUtil.dateCompare(todayDate, periodThruDate.getValue()) == 0) {
			throw new WrongValueException(periodThruDate, "Period Thru Date Can Not Be Greater Than Or Equal To Current Date");
		}
		if (HrmsUtil.dateCompare(periodStartDate.getValue(), periodThruDate.getValue()) == 1) {
			throw new WrongValueException(periodThruDate, "Period Thru Date Must Be Greater Than Or Equal To Period Start Date");
		}
	}

	public static boolean perfReviewCheckEmployeePosition(Date periodStartDate, Date periodThruDate, String emplPosition, Label labelMessageTimePeriod) throws GenericEntityException {
		java.text.SimpleDateFormat formater = new java.text.SimpleDateFormat("dd-MM-yyyy");
		Timestamp fromDate = new Timestamp(periodStartDate.getTime());
		Timestamp thruDate = new Timestamp(periodThruDate.getTime());
		EntityCondition condition1 = EntityCondition.makeCondition("periodThruDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate);
		EntityCondition condition2 = EntityCondition.makeCondition( "periodStartDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDate);
		EntityCondition condition = EntityCondition.makeCondition(condition1,EntityOperator.AND,condition2);
		List<GenericValue> perfReviewsList = delegator.findList("PerfReviews", condition, null, null, null, false);
		if (isEmpty(perfReviewsList)) {
			return false;
		} else {
			for (GenericValue gv : perfReviewsList) {
				String perfReviewId = gv.getString("perfReviewId");
				List<GenericValue> perfReviewAssocTemplates = delegator.findByAnd("PerfReviewAssocTemplates", UtilMisc.toMap("perfReviewId", perfReviewId));
				for (GenericValue gv1 : perfReviewAssocTemplates) {
					String positionTypeId = gv1.getString("emplPositionTypeId");
					String positionTypeDesc = positionType(positionTypeId);
					if (positionTypeDesc.equals(emplPosition)) {
						String startdate = formater.format(gv.getTimestamp("periodStartDate"));
						String endDate = formater.format(gv.getTimestamp("periodThruDate"));
						labelMessageTimePeriod.setValue("Appraisal is enable for position type" + " " + emplPosition + " " + "between" + " " + startdate + " " + "to" + " " + endDate);
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public static boolean perfReviewCheckEmployeePositionAll(Date periodStartDate, Date periodThruDate, String positionType,Label labelMessageTimePeriod) throws GenericEntityException {
		java.text.SimpleDateFormat formater = new java.text.SimpleDateFormat("dd-MM-yyyy");
		Timestamp fromDate = new Timestamp(periodStartDate.getTime());
		Timestamp thruDate = new Timestamp(periodThruDate.getTime());
		EntityCondition condition1 = EntityCondition.makeCondition("periodThruDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate);
		EntityCondition condition2 = EntityCondition.makeCondition( "periodStartDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDate);
		EntityCondition condition = EntityCondition.makeCondition(condition1,EntityOperator.AND,condition2);
		List<GenericValue> perfReviewsList = delegator.findList("PerfReviews", condition, null, null, null, false);
		if(perfReviewsList.size() > 0 && positionType.equalsIgnoreCase("all")){
			for (GenericValue gv : perfReviewsList) {
				String perfReviewId = gv.getString("perfReviewId");
				List<GenericValue> perfReviewAssocTemplates = delegator.findByAnd("PerfReviewAssocTemplates", UtilMisc.toMap("perfReviewId", perfReviewId));
				for (GenericValue gv1 : perfReviewAssocTemplates) {
					String positionTypeId = gv1.getString("emplPositionTypeId");
					String positionTypeDesc = positionType(positionTypeId);
						String startdate = formater.format(gv.getTimestamp("periodStartDate"));
						String endDate = formater.format(gv.getTimestamp("periodThruDate"));
						labelMessageTimePeriod.setValue("Appraisal is enable for position type" + " " + positionTypeDesc + " " + "between" + " " + startdate + " " + "to" + " " + endDate);
						return true;
				}
			}
		}
		return false;
	}


	private static String positionType(String positionId) throws GenericEntityException {
		String description = new String();
		if (!(positionId.equals("_NA_"))) {
			GenericValue findDescription = delegator.findByPrimaryKey("EmplPositionType", UtilMisc.toMap("emplPositionTypeId", positionId));
			if (isNotEmpty(findDescription)) {
				description = findDescription.getString("description");
			}
		}
		return description;
	}
	public static String perfReviewLabelSelectTimePeriodCombobox(Timestamp fromDate,Timestamp thruDate){
		return formater.format(fromDate) + " " + "/" +" "+ formater.format(thruDate);
	}
	public static List<String> getAllReportingStructure(String partyId,String perfReviewId) throws GenericEntityException{
		GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
		GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
		List<GenericValue> perfReviews = delegator.findByAnd("PerfReviews", UtilMisc.toMap("perfReviewId",perfReviewId));
		if(isNotEmpty(perfReviews)){
			GenericValue perfReviewsGV = EntityUtil.getFirst(perfReviews);
			Timestamp fromDate = perfReviewsGV.getTimestamp("periodStartDate");
			Timestamp thruDate = perfReviewsGV.getTimestamp("periodThruDate");
			List<String> reviewerId  = HumanResUtil.getAllActiveReportingMangerForParty(partyId,fromDate, thruDate,delegator);
			return reviewerId ;
		}
		
		return null;
	}
	public static Integer getTotalRating(String ReviewId) throws GenericEntityException{
		return getMaximumRating();
	}
	private static Integer getMaximumRating() throws GenericEntityException{
		List<GenericValue> perfRatingList = delegator.findList("PerfRating", null, null, null, null, false);
		Integer maxRating  = 0;
		for(GenericValue gv : perfRatingList){
			Integer rating = Integer.parseInt(gv.getString("rating"));
			if(maxRating <= rating){
				maxRating = rating;
			}
		}
		return maxRating;
	}
	public static String getLatestManager(String emplPerfReviewId,String partyId) throws GenericEntityException{
		List<GenericValue> emplPerfReviews = delegator.findByAnd("EmplPerfReview", UtilMisc.toMap("emplPerfReviewId",emplPerfReviewId));
		if(UtilValidate.isNotEmpty(emplPerfReviews)){
		List<GenericValue> perfReviewsGV = null;
		try {
			perfReviewsGV = delegator.findByAnd("PerfReviews",UtilMisc.toMap("perfReviewId",emplPerfReviews.get(0).getString("perfReviewId")));
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		List<GenericValue> emplPositionFulfillment = null;
		try {
			emplPositionFulfillment = delegator.findByAnd("EmplPositionFulfillment", UtilMisc.toMap("partyId",partyId));
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		Timestamp periodStartDate = ((GenericValue)perfReviewsGV.get(0)).getTimestamp("periodStartDate");
		Timestamp periodThruDate = ((GenericValue)perfReviewsGV.get(0)).getTimestamp("periodThruDate");
		String emplPositionId = emplPositionFulfillment.get(0).getString("emplPositionId");
		EntityCondition cn1 = EntityCondition.makeCondition("emplPositionIdManagedBy",emplPositionId);
		EntityCondition cn2 = EntityCondition.makeCondition("fromDate",EntityOperator.LESS_THAN_EQUAL_TO,periodStartDate);
		EntityCondition cn3 = EntityCondition.makeCondition("thruDate",EntityOperator.GREATER_THAN_EQUAL_TO,periodThruDate);
		EntityCondition mainCon1 = EntityCondition.makeCondition(cn2,EntityOperator.AND,cn3);
		EntityCondition mainCon = EntityCondition.makeCondition(cn1,EntityOperator.AND,mainCon1);
		List<GenericValue> emplPositionReportingStruct = null;
		try {
			emplPositionReportingStruct = delegator.findList("EmplPositionReportingStruct", mainCon, null, null, null, false);
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		if(UtilValidate.isEmpty(emplPositionReportingStruct)){
			EntityCondition cn4 = EntityCondition.makeCondition("thruDate",EntityOperator.EQUALS,null);
			mainCon = EntityCondition.makeCondition(cn1,EntityOperator.AND,cn4);
			try {
				emplPositionReportingStruct = delegator.findList("EmplPositionReportingStruct", mainCon, null, null, null, false);
			} catch (GenericEntityException e) {
				e.printStackTrace();
			}
		}
		if(emplPositionReportingStruct.size() == 0)
			return null;
		String managerPositionId = emplPositionReportingStruct.get(0).getString("emplPositionIdReportingTo");
		List<GenericValue> mgrPositionFulfillment = null;
		try {
			mgrPositionFulfillment = delegator.findByAnd("EmplPositionFulfillment", UtilMisc.toMap("emplPositionId",managerPositionId));
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return mgrPositionFulfillment.get(0).getString("partyId");
		}else
			return null;
	}
	
	public static String getPerfReviewId(String emplPerfreviewId){
		try {
			return delegator.findByPrimaryKey("EmplPerfReview",UtilMisc.toMap("emplPerfReviewId",emplPerfreviewId)).getString("perfReviewId");
		} catch (GenericEntityException e) {
		}
		return null;
	}
	
	public static Map<String, String> getAppraisalTimePeriod(String emplPerfReviewId){
		String perfReviewId = getPerfReviewId(emplPerfReviewId);
		GenericValue perfReviews = null;
		try {
			 perfReviews = delegator.findByPrimaryKey("PerfReviews",UtilMisc.toMap("perfReviewId",perfReviewId));
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		Map<String, String> map = new HashMap<String, String>();
		map.put("fromDate", formater.format(perfReviews.getTimestamp("periodStartDate")));
		map.put("thruDate", formater.format(perfReviews.getTimestamp("periodThruDate")));
		return map;
	}
}
