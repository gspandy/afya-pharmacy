package com.smebiz.eloan;

import javolution.util.FastMap;
import org.apache.commons.lang.time.DateUtils;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.humanresext.util.HumanResUtil;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import java.math.BigDecimal;
import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ELoanService {

    public static final String module = ELoanService.class.getName();
    public static final String defaultCurrencyUomID = "INR";
    public static final String C_SAVED = "EL_SAVED";
    public static final String C_SUBMIT = "EL_SUBMITTED";
    public static final String C_ADM_REJECTED = "EL_ADM_REJECTED";

    public static Map<String, Object> createELoan(DispatchContext dctx,
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
        String partyId = (String) context.get("partyId");
        Map<String, Object> loanVals = UtilMisc.toMap("partyId", partyId,
                "loanType", context.get("loanType"), "loanAmount", context
                .get("loanAmount"));
        loanVals.put("currencyUomId", context.get("currencyUomId"));
        loanVals.put("fromDate", context.get("fromDate"));
        loanVals.put("thruDate", context.get("thruDate"));
        loanVals.put("applicationDate", new Date(UtilDateTime.nowDate().getTime()));
        loanVals.put("emi", context.get("emi"));
        loanVals.put("interest", context.get("interest"));
        loanVals.put("hr_period", context.get("hr_period"));
        loanVals.put("mgrPositionId", context.get("mgrPositionId"));

        GenericValue clGV = delegator.makeValue("ELoanHead", loanVals);
        clGV.set("loanId", delegator.getNextSeqId("ELoanHead"));
        try {
            clGV.create();
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil
                    .returnError("An Error occurred creating the ELoan record"
                            + e.getMessage());
        }

        /** Create the loan status record **/
        Map<String, Object> result = ServiceUtil
                .returnSuccess("Employee ELoan created");
        result.put("loanId", clGV.get("loanId"));
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Map<String, Object> cls = UtilMisc.toMap("loanId", clGV.get("loanId"),
                "statusId", C_SAVED, "updatedBy", loginPartyId);
        cls.put("hr_comment", context.get("hr_comment"));
        cls.put("employeeComment", context.get("employeeComment"));
        try {
            result = dispatcher.runSync("createELoanStatusService", cls);
        } catch (GenericServiceException e) {
            Debug.logError(e, module);
        }
        return result;
    }

    public static Map<String, Object> createELoanStatus(DispatchContext dctx,
                                                        Map<String, Object> context) {
        GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
        String l_loanId = (String) context.get("loanId");

        Map<String, Object> cls = UtilMisc.toMap("loanId", l_loanId,
                "statusId", context.get("statusId"), "updatedBy", context
                .get("updatedBy"), "updateDate", UtilDateTime
                .nowTimestamp(), "statusType", context.get("statusType"), "adminStatusType", context.get("adminStatusType"));
        cls.put("hr_comment", context.get("hr_comment"));
        cls.put("adminComment", context.get("adminComment"));
        cls.put("employeeComment", context.get("employeeComment"));
        cls.put("updateDate", UtilDateTime.nowTimestamp());
        GenericValue clsGV = delegator.makeValue("ELoanStatus", cls);

        String lsId = delegator.getNextSeqId("ELoanStatus");
        clsGV.set("lsId", lsId);
        try {
            clsGV.create();
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil
                    .returnError("An Error occurred creating the ELoan Status record"
                            + e.getMessage());
        }
        Map<String, Object> result = ServiceUtil
                .returnSuccess("Employee ELoan Status created");
        result.put("loanId", l_loanId);
        result.put("lsId", lsId);
        return result;
    }

    public static Map<String, Object> getELoanComment(DispatchContext dctx,
                                                      Map<String, Object> context) {
        GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
        String l_loanId = (String) context.get("loanId");
        String l_statusId = (String) context.get("statusId");

        if (l_loanId == null) {
            return ServiceUtil
                    .returnSuccess("Employee ELoan Status searched. Its a new record so no comments yet.");
        }

        if (l_statusId == null) {
            return ServiceUtil
                    .returnSuccess("Employee ELoan Status searched. Its a new record so no comments yet.");
        }

        List conditionList = UtilMisc.toList(EntityCondition.makeCondition(
                "loanId", EntityOperator.EQUALS, l_loanId), EntityCondition
                .makeCondition("statusId", EntityOperator.EQUALS, l_statusId));
        EntityConditionList conditions = EntityCondition.makeCondition(conditionList,
                EntityOperator.AND);

        List<GenericValue> data = null;

        try {
            data = delegator.findList("ELoanStatus", conditions, null,
                    null, null, false);
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil
                    .returnError("An Error occurred searching the ELoan Status record"
                            + e.getMessage());
        }

        if (data.size() != 1) {
            return ServiceUtil
                    .returnError("More/Less than 1 records found for same status and loanId combination."
                            + l_loanId + " " + l_statusId);
        }

        GenericValue clsGV = data.get(0);

        String l_comment = null;
        String l_updatedBy = null;
        String l_lsId = null;

        l_comment = (String) clsGV.get("hr_comment");
        l_updatedBy = (String) clsGV.get("updatedBy");
        l_lsId = (String) clsGV.get("lsId");

        Map<String, Object> result = ServiceUtil
                .returnSuccess("Employee ELoan Status searched");
        result.putAll(UtilMisc.toMap("hrm_comment", l_comment, "updatedBy",
                l_updatedBy, "lsId", l_lsId));
        result.put("statusId", l_statusId);
        return result;
    }

    public static Map<String, Object> updateELoan(DispatchContext dctx,
                                                  Map<String, Object> context) {
        Map<String, Object> result = null;
        GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
        Map<String, Object> clId = UtilMisc.toMap("loanId", context
                .get("loanId"));
        String l_loanId = (String) context.get("loanId");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        if (userLogin == null) {
            return ServiceUtil.returnError("userLogin is null, cannot update.");
        }

        String l_partyId = (String) userLogin.get("partyId");

        GenericValue loanGV = null;
        GenericValue loanStatusGV = null;
        try {
            loanGV = delegator.findByPrimaryKey("ELoanHead", clId);
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError("Problems finding the loan record ["
                    + l_loanId + "]");
        }

        /** Check if the loan can be updated **/
        if (checkDuplicateELoan(dctx, l_loanId, C_SUBMIT)) {
            return ServiceUtil.returnError("ELoan : " + l_loanId
                    + " has already been submitted.");
        }

        /** Check if same user applied for this loan **/
       /* if (!l_partyId.equals((String) loanGV.get("partyId"))) {
            return ServiceUtil.returnError("ELoan : " + l_loanId
                    + " was applied by different party."
                    + loanGV.get("partyId"));
        }*/
        Map<String, Object> loanVals = UtilMisc.toMap("loanType", context
                .get("loanType"), "loanAmount", context.get("loanAmount"),
                "emi", context.get("emi"));
        loanVals.put("hr_period", context.get("hr_period"));
        loanVals.put("interest", context.get("interest"));
        loanGV.put("fromDate", context.get("fromDate"));
        loanGV.put("thruDate", context.get("thruDate"));
        loanGV.putAll(loanVals);
        loanGV.put("currencyUomId", context.get("currencyUomId"));
        try {
            loanGV.store();
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil
                    .returnError("Problems updating the loan record ["
                            + l_loanId + "]" + e.getMessage());
        }
        result = ServiceUtil.returnSuccess("Employee ELoan updated");
        result.put("loanId", l_loanId);
        return result;
    }

    public static Map<String, Object> submitELoan(DispatchContext dctx,
                                                  Map<String, Object> context) throws GenericEntityException {
        GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        if (userLogin == null) {
            return ServiceUtil.returnError("userLogin is null, cannot create.");
        }

        String partyId = (String) userLogin.get("partyId");

        String l_loanId = (String) context.get("loanId");
        String statusType = (String) context.get("statusType");
        Map<String, Object> clId = FastMap.newInstance();
        clId.put("loanId", l_loanId);
        GenericValue loanGV = null;
        GenericValue loanStatusGV = null;
        
        String statusId=C_SUBMIT;
        String mgrPosId=(String) context.get("mgrPositionId");
        String employeeId=(String) context.get("partyId");
        GenericValue empPos=HumanResUtil.getEmplPositionForParty(employeeId, delegator);
    	String empPosId=empPos==null?null:empPos.getString("emplPositionId");
        if(mgrPosId.equals(empPosId))
        	statusId="EL_MGR_APPROVED";
        if (l_loanId == null) {
            return ServiceUtil.returnError("ELoanId is null, cannot submit.");
        }

        /** Check if the ELoan exists **/
        try {
            loanGV = delegator.findByPrimaryKey("ELoanHead", clId);
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError("Problems finding the loan record ["
                    + l_loanId
                    + "]. Please save the loan first if not already saved.");
        }

        /** Check if the loan has already been submitted **/
        if (checkDuplicateELoan(dctx, l_loanId, statusId)) {
            return ServiceUtil.returnError("ELoan : " + l_loanId
                    + " has already been submitted.");
        }

        LocalDispatcher dispatcher = dctx.getDispatcher();
        Map<String, Object> cls = FastMap.newInstance();
        cls.putAll(UtilMisc.toMap("loanId", l_loanId, "statusId", statusId,
                "updatedBy", partyId, "statusType", statusType));
        cls.put("hr_comment", context.get("hr_comment"));
        cls.put("employeeComment", context.get("employeeComment"));
        if(mgrPosId.equals(empPosId))
        	cls.put("adminStatusType", "2");
        Map<String, Object> result = null;
        try {
            result = dispatcher.runSync("createELoanStatusService", cls);
        } catch (GenericServiceException e) {
            Debug.logError(e, module);
        }
        result = ServiceUtil.returnSuccess("Employee ELoan submitted");
        return result;
    }

    public static Map<String, Object> processELoan(DispatchContext dctx,
                                                   Map<String, Object> context) throws GenericEntityException {
        GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        if (userLogin == null) {
            return ServiceUtil
                    .returnError("userLogin is null, cannot process.");
        }

        Security security = dctx.getSecurity();
        Map<String, Object> resultMap = null;

        String l_updatedBy = (String) userLogin.get("partyId");
        String l_loanId = (String) context.get("loanId");
        Map<String, Object> clId = FastMap.newInstance();
        clId.put("loanId", l_loanId);
        GenericValue loanGV = null;
        GenericValue loanStatusGV = null;
        if (l_loanId == null) {
            return ServiceUtil.returnError("ELoanId is null, cannot submit.");
        }

        /** Check if the ELoan exists **/
        try {
            loanGV = delegator.findByPrimaryKey("ELoanHead", clId);
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError("Problems finding the loan record ["
                    + l_loanId
                    + "]. Please save the loan first if not already saved.");
        }

        String l_statusId = (String) context.get("statusId");
        /** Check duplicate processing **/
        if (checkDuplicateELoan(dctx, l_loanId, l_statusId)) {
            return ServiceUtil.returnError("ELoan : " + l_loanId + " has already been processed with status." + l_statusId);
        }
        Double l_loanAmount = (Double) context.get("loanAmount");
        String l_currencyUomId = (String) context.get("currencyUomId");
        Double l_emi = 0.0;
        Double l_interest = (Double) context.get("interest");
        Double l_period = (Double) context.get("hr_period");
        String statusType = (String) context.get("statusType");
        String adminStatusType = (String) context.get("adminStatusType");
        Double l_acceptedAmount = (Double) context.get("acceptedAmount");
        Double l_rejectedAmount = 0D;

        if (l_loanAmount == null) {
            return ServiceUtil
                    .returnError("Loan Amount is null, cannot process.");
        }
        if (l_acceptedAmount == null) {
            return ServiceUtil
                    .returnError("l_acceptedAmount is null, cannot process.");
        }
        l_rejectedAmount = l_loanAmount - l_acceptedAmount;

        String l_comment = ((String) context.get("hr_comment"));
        String a_comment = ((String) context.get("adminComment"));
        String e_comment = ((String) context.get("employeeComment"));
        /** Calculate EMI **/
        if (l_interest == null) {
            return ServiceUtil
                    .returnError(" l_interest is null, cannot process.");
        }
        if (l_period == null) {
            return ServiceUtil.returnError("l_period is null, cannot process.");
        }

        BigDecimal emiAmount = calEMI(l_acceptedAmount, l_interest, l_period);
        l_emi =  emiAmount.doubleValue();

		/* If status is rejected then accepted loanAmount is zero by default * */
        if (l_statusId == null) {
            return ServiceUtil
                    .returnError("l_statusId is null, cannot process.");
        }
        if (l_statusId.indexOf("REJECTED") >= 0) {
            l_rejectedAmount = l_loanAmount;
            l_acceptedAmount = 0D;
            l_interest = 0D;
            l_period = 0D;
            l_emi = 0D;
        }

        /**
         * IF rejected loanAmount > 0 or status is rejected then Approver must
         * enter a comment
         **/
        if ((l_rejectedAmount > 0 || (l_statusId.indexOf("REJECTED") >= 0))
                && (l_comment) == null) {
            return ServiceUtil
                    .returnError("Comment cannot be null for the loan record ["
                            + l_loanId
                            + "] as rejected loanAmount is non-zero.");
        }

        Map clH = UtilMisc.toMap("acceptedAmount", l_acceptedAmount,
                "rejectedAmount", l_rejectedAmount, "emi", l_emi, "hr_period",
                l_period, "interest", l_interest, "currencyUomId", l_currencyUomId);
        loanGV.putAll(clH);
        loanGV.put("fromDate", context.get("fromDate"));
        loanGV.put("thruDate", context.get("thruDate"));

        /**
         * Check thruDate > fromDate and thruDate - fromDate = period Yet to be
         * added *
         */

        try {
            loanGV.store();
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil
                    .returnError("Problems processing the loan record ["
                            + l_loanId + "]" + e.getMessage());
        }


        if ("EL_ADM_APPROVED".equals(l_statusId)) {
            String employeeId = loanGV.get("partyId").toString();
            List<GenericValue> emiList = getEmployeeEmis(delegator, (Date) context.get("fromDate"), (Date) context.get("thruDate"), employeeId, loanGV.getString("loanType"), emiAmount,l_loanId);
            for (GenericValue emiGv : emiList) {
                try {
                    delegator.createOrStore(emiGv);
                } catch (GenericEntityException e) {
                    throw new RuntimeException(e.getMessage());
                }
            }
//            TransactionHelper.createLoanTransaction(dctx, employeeId, l_acceptedAmount);
        }

        LocalDispatcher dispatcher = dctx.getDispatcher();
        Map<String, Object> cls = FastMap.newInstance();
        cls.putAll(UtilMisc.toMap("loanId", l_loanId, "statusId", l_statusId,
                "updatedBy", l_updatedBy, "statusType", statusType,
                "adminStatusType", adminStatusType));
        cls.put("hr_comment", l_comment);
        cls.put("adminComment", a_comment);
        cls.put("employeeComment", e_comment);
        Map<String, Object> result = null;
        try {
            result = dispatcher.runSync("createELoanStatusService", cls);
        } catch (GenericServiceException e) {
            Debug.logError(e, module);
        }
        result = ServiceUtil.returnSuccess("Employee ELoan processed");
        result.put("emi", l_emi);
        return result;
    }

    public static List<GenericValue> getEmployeeEmis(GenericDelegator delegator, Date fromDate, Date thruDate, String partyId, String loanType, BigDecimal emiAmount,String loanId) throws GenericEntityException {
        BigDecimal amount = emiAmount;
        List<GenericValue> employeeLoanEmiList = new ArrayList<GenericValue>();
        List<Map<String, Date>> dateRanges = getDateRanges(fromDate, thruDate);
        for (Map<String, Date> fromDateThruDate : dateRanges) {
            Map employeeLoanEmiInfo = UtilMisc.toMap("emiId", delegator.getNextSeqId("EmployeeLoanEmiInfo"), "employeeId", partyId,
                    "loanType", loanType, "fromDate", fromDateThruDate.get("startDate"),"loanName",delegator.findOne("Enumeration",false,UtilMisc.toMap("enumId",loanType)).getString("description"),
                    "thruDate", fromDateThruDate.get("endDate"), "amount", amount, "toBePaid", "Y","loanId",loanId);
            GenericValue employeeLoanEmiInfoGv = delegator.makeValidValue("EmployeeLoanEmiInfo", employeeLoanEmiInfo);
            employeeLoanEmiList.add(employeeLoanEmiInfoGv);
        }
        return employeeLoanEmiList;
    }

    private static List<Map<String, java.sql.Date>> getDateRanges(java.util.Date startDate, java.util.Date endDate) {
        List<Map<String, java.sql.Date>> dateList = new ArrayList<Map<String, java.sql.Date>>();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        if(isDateContainStartingDayOfTheMonth(calendar)){// if the start date is the 1st day of the month
            endDate = DateUtils.addMonths(endDate,1);// end date will be the previous month, so total interval will be 1 month less.
        }
        calendar.add(Calendar.MONTH,1);// ignore the month in which loan is applied
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));/* set start date to first date of that month*/
        DateFormat ddmmyyyDateFormatter = new SimpleDateFormat("dd/MM/yyyy");
        while (calendar.getTime().before(endDate)) {
            Map<String, java.sql.Date> dateMap = new HashMap<String, java.sql.Date>();
            int month = calendar.get(Calendar.MONTH) + 1;
            int year = calendar.get(Calendar.YEAR);
            int endDateOfTheMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            int startDateOfTheMonth = calendar.getActualMinimum(Calendar.DAY_OF_MONTH);
            String start = startDateOfTheMonth + "/" + month + "/" + year;
            calendar.add(Calendar.MONTH, 1);
            String end = endDateOfTheMonth + "/" + month + "/" + year;
            try {
                dateMap.put("startDate", new java.sql.Date(ddmmyyyDateFormatter.parse(start).getTime()));
                dateMap.put("endDate", new java.sql.Date(ddmmyyyDateFormatter.parse(end).getTime()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            dateList.add(dateMap);
        }
        return dateList;
    }

    private static boolean isDateContainStartingDayOfTheMonth(Calendar calendar){
        if(calendar.get(Calendar.DAY_OF_MONTH) == calendar.getActualMinimum(Calendar.DAY_OF_MONTH)){
            return true;
        }
        return false;
    }
    /**
     * Calculate EMI *
     */
    public static BigDecimal calEMI(Double principal, Double interest, Double period) {
        BigDecimal emi = BigDecimal.ZERO;
        Double p = principal;
        Double n = period * 12;
        if (interest.compareTo(0d) == 1) {
            Double r = interest / (100 * 12);
            Double x = 1 + r;
            Double po = Math.pow(x, n);
            emi = BigDecimal.valueOf((p * r * po) / (po - 1));
        } else {
            emi = BigDecimal.valueOf(p / n);
        }
        emi = emi.setScale(2,BigDecimal.ROUND_CEILING);
        return emi;

    }

    public static boolean checkDuplicateELoan(DispatchContext dctx,
                                              String v_loanId, String v_statusId) {
        GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
        /* Check if the loan has not been already submitted * */
        List conditionList = UtilMisc.toList(EntityCondition.makeCondition(
                "loanId", EntityOperator.EQUALS, v_loanId), EntityCondition
                .makeCondition("statusId", EntityOperator.EQUALS, v_statusId));
        EntityConditionList conditions = EntityCondition.makeCondition(conditionList,
                EntityOperator.AND);
        Iterator listELoanStatus = null;
        try {
            listELoanStatus = delegator.find("ELoanStatus", conditions, null,
                    null, null, null);
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return true; // By default duplicates exist
        }

        if (listELoanStatus.next() != null)
            return true;

        // Map result =
        // ServiceUtil.returnSuccess("No duplicate loan records found.");
        return false;
    }

    /**
     * Create A New ELoan Limit
     */
    public static Map<String, Object> createELoanLimit(DispatchContext dctx,
                                                       Map<String, Object> context) {
        GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        if (userLogin == null) {
            return ServiceUtil.returnError("userLogin is null, cannot create.");
        }

        String l_updatedBy = (String) userLogin.get("partyId");
        String lemployeeType = (String) context.get("employeeType");
        String lpositionCategory = (String) context.get("positionCategory");

        Map<String, Object> cl = UtilMisc.toMap("emplPositionTypeId", context
                .get("emplPositionTypeId"), "loanType",
                context.get("loanType"), "loanAmount", context
                .get("loanAmount"));
        cl.put("currencyUomId", context.get("currencyUomId"));
        cl.put("hr_comment", context.get("hr_comment"));
        cl.put("fromDate", context.get("fromDate"));
        cl.put("thruDate", context.get("thruDate"));
        cl.put("hr_period", context.get("hr_period"));
        cl.put("interest", context.get("interest"));
        cl.put("expYrs", context.get("expYrs"));
        cl.put("updatedBy", l_updatedBy);
        cl.put("employeeType",lemployeeType);
        cl.put("positionCategory", lpositionCategory);
        

        GenericValue clGV = delegator.makeValue("ELoanLimit", cl);
        String l_limitId = delegator.getNextSeqId("ELoanLimit");
        clGV.set("limitId", l_limitId);
        try {
            clGV.create();
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil
                    .returnError("An Error occurred creating the ELoan Limit record"
                            + e.getMessage());
        }
        Map<String, Object> result = ServiceUtil
                .returnSuccess("Employee ELoan Limit created");
        result.put("limitId", l_limitId);
        return result;
    }

    public static Map<String, Object> updateELoanLimit(DispatchContext dctx,
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
            limitGV = delegator.findByPrimaryKey("ELoanLimit", clId);
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil
                    .returnError("Problems finding the loan limit record ["
                            + l_limitId + "]");
        }

        Map<String, Object> cl = UtilMisc.toMap("emplPositionTypeId", context
                .get("emplPositionTypeId"), "loanType",
                context.get("loanType"), "loanAmount", context
                .get("loanAmount"),"employeeType",lemployeeType,"positionCategory",lpositionCategory);
        cl.put("currencyUomId", context.get("currencyUomId"));
        cl.put("fromDate", context.get("fromDate"));
        cl.put("thruDate", context.get("thruDate"));
        cl.put("hr_comment", context.get("hr_comment"));
        cl.put("hr_period", context.get("hr_period"));
        cl.put("interest", context.get("interest"));
        cl.put("expYrs", context.get("expYrs"));
        cl.put("updatedBy", l_updatedBy);
        limitGV.putAll(cl);
        try {
            limitGV.store();
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil
                    .returnError("Problems updating the loan limit record ["
                            + l_limitId + "]" + e.getMessage());
        }
        result = ServiceUtil.returnSuccess("Employee ELoan Limit updated");
        result.put("limitId", l_limitId);
        return result;
    }

}
