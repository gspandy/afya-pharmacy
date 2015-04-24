package com.smebiz.claims;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.List;

import org.ofbiz.accounting.TransactionHelper;
import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.humanresext.util.HumanResUtil;

public class ClaimService {

    public static final String module = ClaimService.class.getName();
    public static final String defaultCurrencyUomID = "INR";
    public static final String C_SAVED = "CL_SAVED";
    public static final String C_SUBMIT = "CL_SUBMITTED";
    public static final String C_ADM_REJECTED = "CL_ADM_REJECTED";
    public static final String C_BASE_CCY = "INR";

    public static Map<String, Object> createClaim(DispatchContext dctx,
                                                  Map<String, Object> context) {
        GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        if (userLogin == null) {
            return ServiceUtil.returnError("userLogin is null, cannot create.");
        }

        String loginPartyId = (String) userLogin.get("partyId");
  
        if (context.get("partyId") == null) {
            return ServiceUtil.returnError("Party Id is null, cannot create.");
        }
        String partyId=(String)context.get("partyId");
        
        
        Map<String, Object> claimVals = UtilMisc.toMap("partyId", partyId,
                "claimType", context.get("claimType"), "amount", context
                .get("amount"));
        claimVals.put("currencyUomId", context.get("currencyUomId"));
        claimVals.put("admincurrencyUomId", context.get("admincurrencyUomId"));
        claimVals.put("beginDate", context.get("beginDate"));
        claimVals.put("endDate", context.get("endDate"));
        claimVals.put("receipts", context.get("receipts"));
        claimVals.put("mgrPositionId", context.get("mgrPositionId"));
        claimVals.put("employeeComment", context.get("employeeComment"));
        GenericValue clGV = delegator.makeValue("ClaimHead", claimVals);
        clGV.set("claimId", delegator.getNextSeqId("ClaimHead"));
        try {
            clGV.create();
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil
                    .returnError("An Error occurred creating the Claim record"
                            + e.getMessage());
        }

/** Create the claim status record **/
        Map<String, Object> result = ServiceUtil
                .returnSuccess("Employee Claim created");
        result.put("claimId", clGV.get("claimId"));
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Map<String, Object> cls = UtilMisc.toMap("claimId",
                clGV.get("claimId"), "statusId", C_SAVED, "updatedBy", loginPartyId);
        cls.put("hr_comment", context.get("hr_comment"));
        try {
            result = dispatcher.runSync("createClaimStatusService", cls);
        } catch (GenericServiceException e) {
            Debug.logError(e, module);
        }
        return result;
    }

    public static Map<String, Object> createClaimStatus(DispatchContext dctx,
                                                        Map<String, Object> context) {
        GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
        String l_claimId = (String) context.get("claimId");

        Map<String, Object> cls = UtilMisc.toMap("claimId", l_claimId,
                "statusId", context.get("statusId"), "updatedBy", context
                .get("updatedBy"), "updateDate", UtilDateTime
                .nowTimestamp(), "statusType", context
                .get("statusType"), "adminStatusType", context
                .get("adminStatusType"));
        cls.put("hr_comment", context.get("hr_comment"));
        cls.put("adminComment", context.get("adminComment"));

        GenericValue clsGV = delegator.makeValue("ClaimStatus", cls);
        String csId = delegator.getNextSeqId("ClaimStatus");
        clsGV.set("csId", csId);
        try {
            clsGV.create();
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil
                    .returnError("An Error occurred creating the Claim Status record"
                            + e.getMessage());
        }
        Map<String, Object> result = ServiceUtil
                .returnSuccess("Employee Claim Status created");
        result.put("claimId", l_claimId);
        result.put("csId", csId);
        return result;
    }

    public static Map<String, Object> getClaimComment(DispatchContext dctx,
                                                      Map<String, Object> context) {
        GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
        String l_claimId = (String) context.get("claimId");
        String l_statusId = (String) context.get("statusId");

        if (l_claimId == null) {
            return ServiceUtil
                    .returnSuccess("Employee Claim Status searched. Its a new record so no comments yet.");
        }

        List conditionList = UtilMisc.toList(EntityCondition.makeCondition(
                "claimId", EntityOperator.EQUALS, l_claimId), EntityCondition
                .makeCondition("statusId", EntityOperator.EQUALS, l_statusId));
        EntityConditionList conditions = EntityCondition.makeCondition(conditionList,
                EntityOperator.AND);
        List<String> orderBy = FastList.newInstance();
        orderBy.add("csId DESC");
        List<GenericValue> data = null;

        try {
            data = delegator.findList("ClaimStatus", conditions, null, null,
                    null, false);
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil
                    .returnError("An Error occurred searching the Claim Status record"
                            + e.getMessage());
        }

/**
 * if (data.size() != 1) { return ServiceUtil.returnError(
 * "More/Less than 1 records found for same status and claimId combination."
 * + l_claimId + " " + l_statusId); }
 **/

        GenericValue clsGV = data.get(0);

        String l_comment = null;
        String l_updatedBy = null;
        String l_csId = null;

        l_comment = (String) clsGV.get("hr_comment");
        l_updatedBy = (String) clsGV.get("updatedBy");
        l_csId = (String) clsGV.get("csId");

        Map<String, Object> result = ServiceUtil
                .returnSuccess("Employee Claim Status searched");
        result.putAll(UtilMisc.toMap("hr_comment", l_comment, "updatedBy",
                l_updatedBy, "csId", l_csId));
        result.put("statusId", l_statusId);
        return result;
    }

    public static Map<String, Object> updateClaim(DispatchContext dctx,
                                                  Map<String, Object> context) {
        Map<String, Object> result = null;
        GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
        Map<String, Object> clId = UtilMisc.toMap("claimId", context
                .get("claimId"));
        String l_claimId = (String) context.get("claimId");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        if (userLogin == null) {
            return ServiceUtil.returnError("userLogin is null, cannot update.");
        }

        String l_partyId = (String) userLogin.get("partyId");

        GenericValue claimGV = null;
        GenericValue claimStatusGV = null;
        try {
            claimGV = delegator.findByPrimaryKey("ClaimHead", clId);
            List<GenericValue> claimStatusList = delegator.findByAnd("ClaimStatus", UtilMisc.toMap("claimId", l_claimId));
            String csId = EntityUtil.getFirst(claimStatusList).getString("csId");
            claimStatusGV = delegator.makeValue("ClaimStatus", UtilMisc.toMap("csId", csId, "updateDate", UtilDateTime.nowTimestamp()));
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil
                    .returnError("Problems finding the claim record ["
                            + l_claimId + "]");
        }

/** Check if the claim can be updated **/
        if (checkDuplicateClaim(dctx, l_claimId, C_SUBMIT)) {
            return ServiceUtil.returnError("Claim : " + l_claimId
                    + " has already been submitted.");
        }

/** Check if same user applied for this claim **/
        /*if (!l_partyId.equals((String) claimGV.get("partyId"))) {
            return ServiceUtil.returnError("Claim : " + l_claimId
                    + " was applied by different party."
                    + claimGV.get("partyId"));
        }*/
        Map<String, Object> claimVals = UtilMisc.toMap("claimType", context
                .get("claimType"), "amount", context.get("amount"), "receipts",
                context.get("receipts"));
        claimGV.putAll(claimVals);
        claimGV.put("currencyUomId", context.get("currencyUomId"));
        claimGV.put("admincurrencyUomId", context.get("admincurrencyUomId"));
        claimGV.put("beginDate", context.get("beginDate"));
        claimGV.put("endDate", context.get("endDate"));
        claimGV.put("employeeComment", context.get("employeeComment"));
        try {
            claimGV.store();
            claimStatusGV.store();
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil
                    .returnError("Problems updating the claim record ["
                            + l_claimId + "]" + e.getMessage());
        }
        result = ServiceUtil.returnSuccess("Employee Claim updated");
        result.put("claimId", l_claimId);
        return result;
    }

    public static Map<String, Object> submitClaim(DispatchContext dctx,
                                                  Map<String, Object> context) throws GenericEntityException {
        GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        if (userLogin == null) {
            return ServiceUtil.returnError("userLogin is null, cannot create.");
        }

        String partyId = (String) userLogin.get("partyId");
        String statusType = (String) context.get("statusType");
        String l_claimId = (String) context.get("claimId");
        Map clId = UtilMisc.toMap("claimId", l_claimId);
        GenericValue claimGV = null;
        GenericValue claimStatusGV = null;
        
        String statusId=C_SUBMIT;
        String mgrPosId=(String) context.get("mgrPositionId");
        String employeeId=(String) context.get("partyId");
        GenericValue empPos=HumanResUtil.getEmplPositionForParty(employeeId, delegator);
    	String empPosId=empPos==null?null:empPos.getString("emplPositionId");
        if(mgrPosId.equals(empPosId))
        	statusId="CL_MGR_APPROVED";
        
        
        if (l_claimId == null) {
            return ServiceUtil.returnError("ClaimId is null, cannot submit.");
        }

/** Check if the Claim exists **/
        try {
            claimGV = delegator.findByPrimaryKey("ClaimHead", clId);
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil
                    .returnError("Problems finding the claim record ["
                            + l_claimId
                            + "]. Please save the claim first if not already saved.");
        }

/** Check if the claim has already been submitted **/
        if (checkDuplicateClaim(dctx, l_claimId, statusId)) {
            return ServiceUtil.returnError("Claim : " + l_claimId
                    + " has already been submitted.");
        }

        LocalDispatcher dispatcher = dctx.getDispatcher();
        Map cls = UtilMisc.toMap("claimId", l_claimId, "statusId", statusId,
                "updatedBy", partyId, "statusType", statusType);
        cls.put("hr_comment", context.get("hr_comment"));
        if(mgrPosId.equals(empPosId))
        	cls.put("adminStatusType", "2");
        Map<String, Object> result = null;
        try {
            result = dispatcher.runSync("createClaimStatusService", cls);
        } catch (GenericServiceException e) {
            Debug.logError(e, module);
        }
        result = ServiceUtil.returnSuccess("Employee Claim submitted");
        return result;
    }

    public static Map<String, Object> processClaim(DispatchContext dctx,
                                                   Map<String, Object> context) {
        GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        if (userLogin == null) {
            return ServiceUtil
                    .returnError("userLogin is null, cannot process.");
        }

        String l_updatedBy = (String) userLogin.get("partyId");
        String l_claimId = (String) context.get("claimId");
        Map<String, Object> clId = FastMap.newInstance();
        clId.put("claimId", l_claimId);
        GenericValue claimGV = null;
// GenericValue claimStatusGV = null;
        if (l_claimId == null) {
            return ServiceUtil.returnError("ClaimId is null, cannot process.");
        }

/** Check if the Claim exists **/
        try {
            claimGV = delegator.findByPrimaryKey("ClaimHead", clId);
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil
                    .returnError("Problems finding the claim record ["
                            + l_claimId + "].");
        }

        String l_statusId = (String) context.get("statusId");
        String statusType = (String) context.get("statusType");
        String adminStatusType = (String) context.get("adminStatusType");
        Double l_amount = (Double) context.get("amount");
        Double l_acceptedAmount = (Double) context.get("acceptedAmount");
        Double l_rejectedAmount = (Double) context.get("rejectedAmount");
        String l_comment = ((String) context.get("hr_comment"));
        String l_adminComment = ((String) context.get("adminComment"));
        Date l_reimbDate = (Date) context.get("reimbDate");
        String currencyUomId = (String) context.get("admincurrencyUomId");
        Double lfxRate = (Double) context.get("fxRate");
/* If status is rejected then accepted amount is zero by default * */
        if (l_statusId.indexOf("REJECTED") >= 0) {
            l_rejectedAmount = l_amount;
            l_acceptedAmount = 0D;
        }
/**
 * IF Accepted Amount is not positive then by default status is rejected
 **/
/*
* if (l_acceptedAmount <= 0) l_statusId = C_ADM_REJECTED ;
*/

/**
 * IF rejected amount > 0 or status is rejected then Approver must enter
 * a comment
 **/
        if ((l_rejectedAmount > 0 || (l_statusId.indexOf("REJECTED") >= 0))
                && (l_comment) == null) {
            return ServiceUtil
                    .returnError("Comment cannot be null for the claim record ["
                            + l_claimId + "] as rejected amount is non-zero.");
        }

        Double lbaseCurrencyAmount = 0D;
        if (lfxRate == null) { // Fetch the fx rate from database static table
// if no user input
            String lcurrencyUomId = (String) claimGV.get("admincurrencyUomId");
/**
 * For foreign currency claims get the fx rate from
 * UOM_CONVERSION_DATED table and calculate the base_currency_amount
 **/
/**
 * Select * from uom_conversion_dated where UOM_ID = lcurrencyUomId
 * and uom_id_to = "INR" and from_date <= current_date and
 * (thru_date is null or thru_date >= current_date)
 */

            EntityCondition uomCond = EntityCondition.makeCondition("uomId",
                    lcurrencyUomId);
            EntityCondition uomToCond = EntityCondition.makeCondition(
                    "uomIdTo", C_BASE_CCY);
            Date now = new Date(new java.util.Date().getTime());
            EntityCondition fromCond = EntityConditionFunction.makeCondition(
                    "fromDate", EntityComparisonOperator.LESS_THAN_EQUAL_TO,
                    now);
            EntityCondition andCond = EntityCondition.makeCondition(uomCond,
                    uomToCond, fromCond);

            EntityCondition thruCond = EntityConditionFunction.makeCondition(
                    "thruDate", EntityComparisonOperator.GREATER_THAN_EQUAL_TO,
                    now);
            EntityCondition nullCond = EntityCondition.makeCondition(
                    ("thruDate"), EntityOperator.EQUALS, null);
            EntityCondition orCond = EntityCondition.makeCondition(thruCond,
                    EntityOperator.OR, nullCond);

            EntityCondition cond = EntityCondition.makeCondition(andCond,
                    orCond);

            GenericValue uomGV = null;
            try {
                uomGV = delegator.findList("UomConversionDated", cond, null,
                        null, null, false).get(0);
            } catch (GenericEntityException e) {
                Debug.logError(e, module);
                return ServiceUtil
                        .returnError("Problems finding the claim record ["
                                + l_claimId
                                + "]. Please save the claim first if not already saved.");
            }

            lfxRate = uomGV.getDouble("conversionFactor");
        }

        lbaseCurrencyAmount = l_acceptedAmount * lfxRate;

        Map<String, Object> clH = UtilMisc.toMap("reimbDate", l_reimbDate, "acceptedAmount", l_acceptedAmount, "rejectedAmount",
                l_rejectedAmount, "fxRate", lfxRate, "baseCurrencyAmount", BigDecimal.valueOf(lbaseCurrencyAmount), "admincurrencyUomId", currencyUomId);

        claimGV.putAll(clH);
        try {
            claimGV.store();
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil
                    .returnError("Problems processing the claim record ["
                            + l_claimId + "]" + e.getMessage());
        }

        LocalDispatcher dispatcher = dctx.getDispatcher();
        Map<String, Object> cls = FastMap.newInstance();
        cls.putAll(UtilMisc.toMap("claimId", l_claimId, "statusId", l_statusId,
                "updatedBy", l_updatedBy, "statusType", statusType,
                "adminStatusType", adminStatusType, "adminComment", l_adminComment));
        cls.put("hr_comment", l_comment);
        Map<String, Object> result = null;
        try {
            result = dispatcher.runSync("createClaimStatusService", cls);
        } catch (GenericServiceException e) {
            Debug.logError(e, module);
        }

       /* if ("CL_ADM_APPROVED".equals((String)context.get("statusId"))) {
            TransactionHelper.createClaimTransaction(dctx, claimGV.getString("claimType"), lbaseCurrencyAmount);
        }*/

        result = ServiceUtil.returnSuccess("Employee Claim processed");

/** Logic for Reimb Date calculation needs to be added **/
        result.put("reimbDate", l_reimbDate);
        return result;
    }

    private static boolean checkDuplicateClaim(DispatchContext dctx,
                                               String v_claimId, String v_statusId) {
        GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
/* Check if the claim has not been already submitted * */
        List conditionList = UtilMisc.toList(EntityCondition.makeCondition(
                "claimId", EntityOperator.EQUALS, v_claimId), EntityCondition
                .makeCondition("statusId", EntityOperator.EQUALS, v_statusId));
        EntityConditionList conditions = EntityCondition.makeCondition(conditionList,
                EntityOperator.AND);
        Iterator listClaimStatus = null;
        try {
            listClaimStatus = delegator.find("ClaimStatus", conditions, null,
                    null, null, null);
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return true; // By default duplicates exist
        }

        if (listClaimStatus.next() != null)
            return true;

// Map<String, Object> result =
// ServiceUtil.returnSuccess("No duplicate claim records found.");
        return false;
    }

    /**
     * Create A New Claim Limit
     *
     * @throws GenericEntityException
     */
    public static Map<String, Object> createClaimLimit(DispatchContext dctx,
                                                       Map<String, Object> context) throws GenericEntityException {
        GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        if (userLogin == null) {
            return ServiceUtil.returnError("userLogin is null, cannot create.");
        }

        String l_updatedBy = (String) userLogin.get("partyId");

        String lemployeeType = (String) context.get("employeeType");
        String lpositionCategory = (String) context.get("positionCategory");

/* To Check Duplicate Claim Limit Exist or Not */
                
        Object cl_emplPosTypeId = context.get("emplPositionTypeId");
        Object claimType = context.get("claimType");

        String l_limitId = null;
        List claimLimits = delegator
                .findByAnd("ClaimLimit", UtilMisc.toMap("emplPositionTypeId",
                        cl_emplPosTypeId, "claimType", claimType,"employeeType",lemployeeType,"positionCategory",lpositionCategory));

/**
 * Check if the claim can be created if (checkDuplicateClaim(dctx,
 * l_claimId, C_SUBMIT)) { return ServiceUtil.returnError("Claim : " +
 * l_claimId + " has already been submitted."); }
 **/

        try {
            if ((claimLimits != null && claimLimits.size() > 0))// Checks if
// record exist
// or not
                return ServiceUtil
                        .returnError("With the Same EmplPosType and Claim Type,ClaimLimit exists;ClaimLimit Couldnt be Created");
            else {
                Map<String, Object> cl = UtilMisc.toMap("emplPositionTypeId",
                        cl_emplPosTypeId, "claimType", claimType, "amount",
                        context.get("amount"));
                cl.put("currencyUomId", context.get("currencyUomId"));
                cl.put("hr_comment", context.get("hr_comment"));
                cl.put("updatedBy", l_updatedBy);
                cl.put("employeeType",lemployeeType);
                cl.put("positionCategory", lpositionCategory);
                GenericValue clGV = delegator.makeValue("ClaimLimit", cl);
                l_limitId = delegator.getNextSeqId("ClaimLimit");
                clGV.set("limitId", l_limitId);
                clGV.create();
            }

        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil
                    .returnError("An Error occurred creating the Claim Limit record"
                            + e.getMessage());
        }
        Map<String, Object> result = ServiceUtil
                .returnSuccess("Employee Claim Limit created");
        result.put("limitId", l_limitId);
        return result;
    }

    public static Map<String, Object> updateClaimLimit(DispatchContext dctx,
                                                       Map<String, Object> context) {
        Map<String, Object> result = null;
        GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        if (userLogin == null) {
            return ServiceUtil.returnError("userLogin is null, cannot create.");
        }

        String l_updatedBy = (String) userLogin.get("partyId");
        String l_limitId = (String) context.get("limitId");
 
        String lemployeeType = (String) context.get("employeeType");
        String lpositionCategory = (String) context.get("positionCategory");
        
        
        Map<String, Object> clId = FastMap.newInstance();
        clId.put("limitId", l_limitId);

        GenericValue limitGV = null;
        try {
            limitGV = delegator.findByPrimaryKey("ClaimLimit", clId);
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil
                    .returnError("Problems finding the claim limit record ["
                            + l_limitId + "]");
        }

        Map<String, Object> cl = FastMap.newInstance();
        cl.putAll(UtilMisc.toMap("emplPositionTypeId", context
                .get("emplPositionTypeId"), "claimType", context
                .get("claimType"), "amount", context.get("amount"),"employeeType",lemployeeType,"positionCategory",lpositionCategory));
        cl.put("currencyUomId", context.get("currencyUomId"));
        cl.put("hr_comment", context.get("hr_comment"));
        cl.put("updatedBy", l_updatedBy);
        limitGV.putAll(cl);

        try {
            limitGV.store();
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil
                    .returnError("Problems updating the claim limit record ["
                            + l_limitId + "]" + e.getMessage());
        }
        result = ServiceUtil.returnSuccess("Employee Claim Limit updated");
        result.put("limitId", l_limitId);
        return result;
    }
}

