package com.nthdimenzion.humanres.payroll;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityComparisonOperator;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionFunction;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.humanresext.util.HumanResUtil;
import org.ofbiz.service.ServiceUtil;

import com.nthdimenzion.humanres.payroll.services.PayrollServices;
import com.smebiz.common.UtilDateTimeSME;

/**
 * @author sandeep, pankaj ProRated CTC calculation added
 */
public class PayrollHelper {

    private static final int C_MTHS = 12; // Months in a Year
    // private static final BigDecimal C_DAY_MSECS = new BigDecimal("86400000");
    // private static final BigDecimal BC_MTHS = new BigDecimal("12");
    private static String module = "PayrollHelper";

    /**
     * Gives integer difference of months b/w two dates *
     */
    public static int monthsBetween(Date from, Date to) {
        Calendar fromDate = Calendar.getInstance();
        Calendar thruDate = Calendar.getInstance();
        fromDate.setTime(from);
        thruDate.setTime(to);
        int yearDiff = thruDate.get(Calendar.YEAR) - fromDate.get(Calendar.YEAR);
        int monthDiff = (thruDate.get(Calendar.MONTH) + yearDiff * C_MTHS) - fromDate.get(Calendar.MONTH);
        return monthDiff;
    }

    /**
     * Get Fractional Year between Dates *
     */
    public static double fracYearBetween(Date from, Date to, int totalDays) {

        BigDecimal interval = new BigDecimal(UtilDateTimeSME.daysBetween(from, to));
        interval.setScale(2, BigDecimal.ROUND_HALF_UP);
        double fracYear = interval.doubleValue() / totalDays;
        return fracYear;
    }

    /**
     * Get the LOP days from Empl_leave table to get actual fracYear
     */
    public static Map<String, Object> getPaidFracYear(Map<String, Object> ctx, GenericDelegator delegator) {
        Timestamp vperiodFrom = new Timestamp(((Date) ctx.get("periodFrom")).getTime());
        Timestamp vperiodTo = new Timestamp(((Date) ctx.get("periodTo")).getTime());
        String vpartyId = (String) ctx.get("partyId");
    /*
     * * Case1: Leave begins and ends in same month select unpaid_days from empl_leave where from_date >= lperiodFrom
	 * and (thru_date <= lperiodTo) Sum the unpaid_days in this month Double FirstUnpaidDays = sum(unpaid_days)
	 */
        EntityCondition fromCond = EntityCondition.makeCondition("fromDate",
                EntityComparisonOperator.GREATER_THAN_EQUAL_TO, vperiodFrom);
        EntityCondition thruCond = EntityCondition.makeCondition("thruDate", EntityComparisonOperator.LESS_THAN_EQUAL_TO,
                vperiodTo);

        EntityCondition leaveStatus = EntityCondition.makeCondition("leaveStatusId", EntityComparisonOperator.EQUALS, "LT_ADM_APPROVED");
        EntityCondition partyCond = EntityCondition.makeCondition("partyId", vpartyId);
        EntityCondition conditions = EntityCondition.makeCondition(partyCond, fromCond, thruCond, leaveStatus);

        List<GenericValue> data = null;
        Set<String> fields = new HashSet<String>(); // fieldsToSelect
        fields.add("unpaidDays");
        try {
            data = delegator.findList("EmplLeave", conditions, null, null, null, false);
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError("Problems finding the leave application [" + vpartyId + " from date :"
                    + vperiodFrom + "]");
        }

        Double totalUnpaidDays = 0D;
        Double firstUnpaidDays = 0D;
        Double secondUnpaidDays = 0D;
        Double thirdUnpaidDays = 0D;
        Double lunpaidDays = 0D;
        if (data.size() > 0) {
            for (GenericValue unpaids : data) {
                lunpaidDays = unpaids.getDouble("unpaidDays");
                firstUnpaidDays = firstUnpaidDays + lunpaidDays;

            }
        }

	/*
	 * Case2: Leave begins in this month and ends in next month select * from empl_leave where (from_date >= lperiodFrom
	 * and from_date <= lperiodTo) //SecFromCond and thru_date > lperiodTo //SecThruCond and partyId = vpartyId Double
	 * lcunpaidDays = HumanResUtil.getWorkingDaysBetween(fromDate, lperiodTo, delegator).doubleValue(); Double
	 * SecondUnpaidDays = min(lcunpaidDays, unpaid_days)
	 */
        EntityCondition SecFromCond = EntityCondition.makeCondition("fromDate",
                EntityComparisonOperator.LESS_THAN_EQUAL_TO, vperiodTo);
        EntityCondition SecThruCond = EntityCondition.makeCondition("thruDate", EntityComparisonOperator.GREATER_THAN,
                vperiodTo);
        EntityCondition SecCond = EntityCondition.makeCondition(partyCond, fromCond, SecFromCond, SecThruCond, leaveStatus);

        try {
            data = delegator.findList("EmplLeave", SecCond, null, null, null, false);
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError("Problems finding the leave application [" + vpartyId + " from date :"
                    + vperiodFrom + "]");
        }

        GenericValue lvGV = null;
        Timestamp lfromDate = null;

        Double lsUnpaidDays = 0D;
        Timestamp fromStamp = null;
        Timestamp toStamp = null; // new Timestamp(to.getTime());
        /** Check if such type of leaves exist **/
        if (data.size() == 1) {
            lvGV = data.get(0);
            lunpaidDays = lvGV.getDouble("unpaidDays");
            lfromDate = lvGV.getTimestamp("fromDate");
            fromStamp = new Timestamp(lfromDate.getTime());
            toStamp = new Timestamp(vperiodTo.getTime());
            lsUnpaidDays = HumanResUtil.getWorkingDaysBetween(fromStamp, toStamp, delegator).doubleValue();
        }
        secondUnpaidDays = Math.min(lsUnpaidDays, lunpaidDays); // If the whole
        // leave is paid
        // then 0 should
        // come

        /**
         * * Case3: Leave began in previous month and ended in this month select
         * * from empl_leave where from_date < vperiodFrom //ThirdFromCond and
         * thru_date >= vperiodFrom //ThirdThruCond and thru_date <= vperiodTo
         * and partyId = vpartyId Double lcunpaidDays =
         * HumanResUtil.getWorkingDaysBetween(lperiodFrom, thruDate,
         * delegator).doubleValue(); Double ThirdUnpaidDays = min(lcunpaidDays,
         * unpaid_days) Double lpaidDays = days_in_month - ( FirstUnpaidDays +
         * SecondUnpaidDays + ThirdUnpaidDays)
         */

        EntityCondition ThirdFromCond = EntityCondition.makeCondition("fromDate", EntityComparisonOperator.LESS_THAN,
                vperiodFrom);
        EntityCondition ThirdThruCond = EntityCondition.makeCondition("thruDate",
                EntityComparisonOperator.GREATER_THAN_EQUAL_TO, vperiodFrom);
        EntityCondition ThirdCond = EntityCondition.makeCondition(partyCond, ThirdFromCond, ThirdThruCond, thruCond, leaveStatus);

        try {
            data = delegator.findList("EmplLeave", ThirdCond, null, null, null, false);
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError("Problems finding the leave application [" + vpartyId + " from date :"
                    + vperiodFrom + "]");
        }

        lvGV = null;
        lfromDate = null;
        Timestamp lthruDate = null;
        lsUnpaidDays = 0D;
        /** Check if such type of leaves exist **/
        if (data.size() == 1) {
            lvGV = data.get(0);
            lunpaidDays = lvGV.getDouble("unpaidDays");
            lthruDate = lvGV.getTimestamp("thruDate");
            fromStamp = new Timestamp(vperiodFrom.getTime());
            toStamp = new Timestamp(lthruDate.getTime());
            lsUnpaidDays = HumanResUtil.getWorkingDaysBetween(fromStamp, toStamp, delegator).doubleValue();
        }
        thirdUnpaidDays = Math.min(lsUnpaidDays, lunpaidDays); // If the whole
        // leave is paid
        // then 0 should
        // come
        totalUnpaidDays = firstUnpaidDays + secondUnpaidDays + thirdUnpaidDays;

        Map<String, Object> result = ServiceUtil.returnSuccess();
        result.put("totalUnpaidDays", totalUnpaidDays);
        return result;
    }

    public static GenericValue getCurrentFiscalYear(GenericDelegator delegator) throws GenericEntityException {
        java.sql.Date now = new java.sql.Date(new Date().getTime());
        EntityCondition fromDate = EntityConditionFunction.makeCondition("fromDate",
                EntityComparisonOperator.LESS_THAN_EQUAL_TO, now);
        EntityCondition thruDate = EntityConditionFunction.makeCondition("thruDate",
                EntityComparisonOperator.GREATER_THAN_EQUAL_TO, now);
        EntityCondition periodType = EntityCondition.makeCondition(UtilMisc.toMap("periodTypeId", "FISCAL_YEAR"));
        EntityCondition condition = EntityCondition.makeCondition(fromDate, thruDate, periodType);
        List<GenericValue> periodGvs = delegator.findList("CustomTimePeriod", condition, null, null, null, false);
        GenericValue periodGv = null;
        if (UtilValidate.isEmpty(periodGvs)) {
            periodGv = delegator.findList("CustomTimePeriod", null, null, null, null, false).get(0);
        }
        return UtilValidate.isNotEmpty(periodGvs) ? periodGvs.get(0) : periodGv;
    }

    /**
     * @param periodFrom : Start of Financial Year
     * @param periodTo   : End of Financial Year
     * @return
     * @throws GenericEntityException
     */
    public static GenericValue getFiscalYear(Date periodFrom, Date periodTo, GenericDelegator delegator) throws GenericEntityException {
        // java.sql.Date now = new java.sql.Date(new Date().getTime());
        EntityCondition fromCon = EntityConditionFunction.makeCondition("fromDate", EntityComparisonOperator.EQUALS,
                periodFrom);
        EntityCondition thruCon = EntityConditionFunction.makeCondition("thruDate", EntityComparisonOperator.EQUALS,
                periodTo);
        EntityCondition periodType = EntityCondition.makeCondition(UtilMisc.toMap("periodTypeId", "FISCAL_YEAR"));
        EntityCondition condition = null;
        if (periodTo != null) {
            condition = EntityCondition.makeCondition(fromCon, thruCon, periodType);
        } else {
            condition = EntityCondition.makeCondition(fromCon, periodType);
        }
        return delegator.findList("CustomTimePeriod", condition, null, null, null, false).get(0);
    }

    /**
     * Find the fiscal Year for the Date passed *
     */
    public static GenericValue getFiscalYear(Date periodTo, GenericDelegator delegator) throws GenericEntityException {

        EntityCondition fromCon = EntityConditionFunction.makeCondition("fromDate",
                EntityComparisonOperator.LESS_THAN_EQUAL_TO, new java.sql.Date(periodTo.getTime()));
        EntityCondition thruCon = EntityConditionFunction.makeCondition("thruDate",
                EntityComparisonOperator.GREATER_THAN_EQUAL_TO, new java.sql.Date(periodTo.getTime()));
        EntityCondition periodType = EntityCondition.makeCondition(UtilMisc.toMap("periodTypeId", "FISCAL_YEAR"));
        EntityCondition condition = EntityCondition.makeCondition(fromCon, thruCon, periodType);
        List<GenericValue> timePeriodGvs = delegator.findList("CustomTimePeriod", condition, null, null, null, false);
        List<GenericValue> periodGvs = delegator.findList("CustomTimePeriod", null, null, null, null, false);
        GenericValue periodGv = null;
        if(UtilValidate.isEmpty(timePeriodGvs)){
            periodGv = periodGvs.get(0);
        }
        return UtilValidate.isNotEmpty(timePeriodGvs) ? timePeriodGvs.get(0) : periodGv;
    }

    public static Timestamp dateToTimestamp(java.sql.Date date) {
        return Timestamp.valueOf(date + " 00:00:00");
    }

    public static java.sql.Date timestampToDate(Timestamp timestamp) {
        return new java.sql.Date(timestamp.getTime());
    }

    /**
     * periodTo = End Date of Month for which PaySlip is generated
     *
     * @throws Exception
     */
    public static Map<String, Double> getITEstimate(Map<String, Object> context, GenericDelegator delegator) throws Exception {
        Map<String, Double> resultMap = FastMap.newInstance();

        String partyId = (String) context.get("partyId");
        Date periodTo = (Date) context.get("periodTo");
        // End of period for which Pay will be given
        Date periodFrom = (Date) context.get("periodFrom");
        // Start of period for which Pay will be given

        Date currentMonthStart = (Date) context.get("periodFrom");

        Boolean monthly = Boolean.TRUE;
        // (Boolean)context.get("monthly");
        GenericValue fiscalYear = getFiscalYear(periodTo, delegator);
        Date yearBegin = fiscalYear.getDate("fromDate");
        Date yearEnd = fiscalYear.getDate("thruDate");

        periodTo = yearEnd;

        GenericValue employment = PayrollServices.getEmployementRecordForParty(delegator, partyId);
        if (employment == null) {
            throw new Exception("No Employment record found for Party " + partyId);
        }

        Date employementDate = new Date(employment.getTimestamp("fromDate").getTime());

        if (employment.getTimestamp("thruDate") != null) {
            Date terminatedDate = new Date(employment.getTimestamp("thruDate").getTime());
            if (terminatedDate.before(yearBegin)) {
                throw new Exception("Party Id " + partyId + " is terminated so not processing the Payroll");
            }

            // If termination date after month begin and before month end, then
            // consider from
            // month beginning to the month date of termination.
            if (terminatedDate.after(yearBegin) && terminatedDate.before(yearEnd)) {
                periodTo = terminatedDate;
            }
        }

        // Check if the joining date is after the Salary Month Begin date,
        // Then the starting period would be from date of joining and not
        // starting of month.
        if (employementDate.after(yearBegin) || employementDate.equals(yearBegin)) {
            periodFrom = employementDate;
        }

        System.out.println(" Tax Calculation For Fiscal Year Beginning from " + periodFrom + " to " + periodTo);
        int remainingSalaryPeriods = monthsBetween(currentMonthStart, periodTo) + 1;
        Double fracYear = fracYearBetween(periodFrom, periodTo, UtilDateTimeSME.daysBetween(yearBegin, yearEnd));
        if ("BIWEEKLY".equals(PayrollHelper.getSalaryFrequencyForCompany(delegator))) {
            fracYear = fracYear * 2;
            Calendar cal = Calendar.getInstance();
            cal.setTime(periodTo);
            if (cal.get(Calendar.DAY_OF_MONTH) == cal.getActualMaximum(Calendar.DAY_OF_MONTH)) {
                remainingSalaryPeriods = (remainingSalaryPeriods * 2) - 1;
            } else
                remainingSalaryPeriods = (remainingSalaryPeriods * 2);
        }

        System.out.println("[Tax Calculation]\tTotal Months remaining Including the current Month ********"
                + remainingSalaryPeriods);
        System.out.println("[Tax Calculation]\tRatio of No Of Days to No Of Days in a Year *********" + fracYear);
        SalaryBean salB = populateEstimatedAnnualSalaryDetail(partyId, periodFrom, periodTo, delegator);
        if (salB == null) {
            ServiceUtil.returnError("No CTC records found for this employee");
            return resultMap;
        }

        // For Extra Income or Sal
        BigDecimal extraIncome = (BigDecimal) context.get("EXTRA_INCOME");
        System.out.println("[Tax Calculation]\t Adhoc Salary " + extraIncome);
        GenericValue prevSalInfoGv = delegator.findByPrimaryKey("EmplPrevSalInfo", UtilMisc.toMap("partyId", partyId));

        BigDecimal prevGrossAmount = new BigDecimal(0);
        BigDecimal prevTaxPaid = new BigDecimal(0);

        if (prevSalInfoGv != null) {
            prevGrossAmount = prevSalInfoGv.getBigDecimal("prevGrossAmount");
            prevTaxPaid = prevSalInfoGv.getBigDecimal("prevTaxPaid");
        }
        System.out.println("[Tax Calculation]\t Previous Gross Income " + prevGrossAmount);

        BigDecimal d = extraIncome.add(prevGrossAmount);
        extraIncome = extraIncome == null ? prevGrossAmount : extraIncome.add(prevGrossAmount);
        salB.setExtraIncome(extraIncome);

        // For Previous Salary Gross add it to
        salB.populateDerivedValues(delegator);
        System.out.println("[Tax Calculation]\t Salary Exemption " + salB.getExemptionAmount());
        System.out.println("[Tax Calculation]\t Salary Tax Deduction " + salB.getTaxDeductionAmount());

        double estimatedGrossSalary = salB.getGrossAmount();
        System.out.println("[Tax Calculation]\t Salary Gross " + estimatedGrossSalary);
        System.out.println("[Tax Calculation]\t estimatedGrossSalary == " + estimatedGrossSalary);
        System.out.println("[Tax Calculation]\t Net Amount == " + salB.getNetAmount());
        double taxNeedToBePaid = salB.getTaxAmount(); // It does not include Professional Tax

        double professionalTax = salB.getProfessionalTax() / 12;

        boolean biWeekly = "BIWEEKLY".equals(PayrollHelper.getSalaryFrequencyForCompany((GenericDelegator) fiscalYear.getDelegator()));
        if (biWeekly) {
            professionalTax = (professionalTax * 0.5);
        }
        System.out.println("[Tax Calculation]\t Professional Tax " + professionalTax);
        System.out.println("[Tax Calculation]\t Tax To Pay " + taxNeedToBePaid);

        // get Total Tax Paid in this financial Year for this party **/
        double taxAlreadyPaid = 0.0;
        taxAlreadyPaid = getTotalIncomeTaxPaid(partyId, new java.sql.Date(yearBegin.getTime()), new java.sql.Date(
                periodFrom.getTime()), delegator);
        double prevTaxAmount = 0.0;
        if (prevTaxPaid != null) prevTaxAmount = prevTaxPaid.doubleValue();
        // Add the already paid tax
        taxAlreadyPaid = taxAlreadyPaid + prevTaxAmount;

        System.out.println("[Tax Calculation]\t Tax Already Collected In Same Organization == " + taxAlreadyPaid);
        System.out.println("[Tax Calculation]\t Previous Tax Paid So Far for Same Fiscal Year == " + prevTaxAmount);
        double taxLeft = taxNeedToBePaid - taxAlreadyPaid;
        System.out.println("[Tax Calculation]\t  Tax Left == " + taxLeft);

        // Monthly Tax To be paid
        double result = monthly ? (taxLeft / remainingSalaryPeriods) : taxLeft;
        resultMap.put("totalTax", result);
        resultMap.put("professionalTax", professionalTax);
        System.out.println("[Tax Calculation]\t  MonthlytaxEstimated == " + result);
        return resultMap;
    }

    public static double getTotalTaxPaidTillDate(String partyId, GenericDelegator delegator) throws GenericEntityException {
        List<GenericValue> taxTotals = getPayslipItems(partyId, Arrays.asList("taxTotal"), delegator);
        double taxTotal = 0;
        for (GenericValue monthlyTaxTotal : taxTotals)
            if (monthlyTaxTotal.getDouble("taxTotal") != null) taxTotal += monthlyTaxTotal.getDouble("taxTotal");
        return taxTotal;
    }

    /**
     * Get Total Tax Paid in the Financial Period FromDate to ThruDate including
     * the Professional Tax
     */
    public static double getTotalTaxPaid(String partyId, java.sql.Date fromDate, java.sql.Date thruDate, GenericDelegator delegator)
            throws GenericEntityException {
        List<GenericValue> taxTotals = getPayslipItems(partyId, Arrays.asList("taxTotal"), fromDate, thruDate, delegator);
        double taxTotal = 0;
        for (GenericValue monthlyTaxTotal : taxTotals)
            if (monthlyTaxTotal.getDouble("taxTotal") != null) taxTotal += monthlyTaxTotal.getDouble("taxTotal");
        return taxTotal;
    }

    public static double getTotalSalaryDrawnTillDate(String partyId, GenericDelegator delegator) throws GenericEntityException {
        List<GenericValue> headTotals = getPayslipItems(partyId, Arrays.asList("headTotal"), delegator);
        double headTotal = 0;
        for (GenericValue monthlyHeadTotal : headTotals)
            if (monthlyHeadTotal.getDouble("headTotal") != null) headTotal += monthlyHeadTotal.getDouble("headTotal");
        System.out.println("@@@@@@@@@@@@@@@@@@ getTotalSalaryDrawnTillDate SalaryAlreadydrawn == " + headTotal);
        return headTotal;
    }

    public static List<GenericValue> getPayslipItems(String partyId, List<String> items, GenericDelegator delegator) throws GenericEntityException {
        GenericValue fiscalYear = getCurrentFiscalYear(delegator);
        if (fiscalYear == null) return null;
        return getPayslipItems(partyId, items, fiscalYear.getDate("fromDate"), fiscalYear.getDate("thruDate"), delegator);
    }

    public static List<GenericValue> getPayslipItems(String partyId, List<String> items, java.sql.Date from,
                                                     java.sql.Date to, GenericDelegator delegator) throws GenericEntityException {
        Set<String> fieldsToSelect = items.isEmpty() ? null : new HashSet<String>(items);
        EntityCondition periodFrom = EntityCondition
                .makeCondition("periodFrom", EntityOperator.GREATER_THAN_EQUAL_TO, from);
        EntityCondition periodTo = EntityCondition.makeCondition("periodTo", EntityOperator.LESS_THAN_EQUAL_TO, to);
        EntityCondition partyCond = EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId);

        EntityCondition interval = EntityCondition.makeCondition(periodFrom, periodTo, partyCond);
        return delegator.findList("EmplPayslip", interval, fieldsToSelect, null, null, false);
    }

    /**
     * Sum the TAX_TYPE = "INCOMETAX" only from Tax Deductions Get total tax
     * paid till toDate in the financial Year
     */
    public static double getTotalIncomeTaxPaid(String partyId, java.sql.Date fromDate, java.sql.Date thruDate, GenericDelegator delegator)
            throws GenericEntityException {

        GenericValue fiscalYear = getFiscalYear(thruDate, delegator);
        Date fromPeriod = fiscalYear.getDate("fromDate");
        Date toPeriod = fiscalYear.getDate("thruDate");
        EntityCondition periodFrom = EntityCondition.makeCondition("periodFrom", EntityOperator.GREATER_THAN_EQUAL_TO,
                fromPeriod);
        EntityCondition periodTo = EntityCondition.makeCondition("periodTo", EntityOperator.LESS_THAN_EQUAL_TO, toPeriod);
        EntityCondition partyCond = EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId);
        EntityCondition whereEntityCondition = EntityCondition.makeCondition(periodFrom, periodTo, partyCond);
        List<GenericValue> payslipGVs = null;

        try {
            payslipGVs = delegator.findList("EmplPayslip", whereEntityCondition, null, null, null, false);
        } catch (GenericEntityException e) {
            e.printStackTrace();
            return 0;
        }

        List<String> payslips = new ArrayList<String>(payslipGVs.size());
        for (GenericValue ps : payslipGVs) {
            payslips.add(ps.getString("payslipId"));
        }

        List<GenericValue> data = null;
        try {
            EntityCondition condition1 = EntityCondition.makeCondition("payslipId", EntityComparisonOperator.IN, payslips);
            EntityCondition condition2 = EntityCondition.makeCondition("taxType", EntityOperator.EQUALS, "INCOMETAX");
            EntityListIterator iter = delegator.find("EmplMonthlyTaxdeduction", condition1, condition2, null, null, null);
            data = iter.getCompleteList();
            iter.close();
        } catch (GenericEntityException e) {

        }

        double taxTotal = 0D;
        if (data != null) {
            for (GenericValue monthlyIT : data) {
                if (monthlyIT.getDouble("amount") != null) taxTotal += monthlyIT.getDouble("amount");
            }
        }
        return taxTotal;
    }

    /**
     * Added fromDate and thruDate filters on amount to calculate the monthly
     * salary May-01:Pankaj: Extra condition added as only credits should be
     * added to gross salary
     * *
     */
    public static double getMonthlyTotal(String partyId, GenericDelegator delegator) throws GenericEntityException {
        double total = 0;
        EntityCondition partyCond = EntityCondition.makeCondition("partyId", partyId);
        EntityCondition crCond = EntityCondition.makeCondition("isCr", "Y");
        EntityCondition cond = EntityCondition.makeCondition(partyCond, crCond);
        // from Date condition
        // EmplSal changed to EmplSalDetail to know if it is credit or debit
        List<GenericValue> salaryComponents = delegator.findList("EmplSalDetail", cond, null, null, null, false);
        salaryComponents = EntityUtil.filterByDate(salaryComponents);
        for (GenericValue salaryComponent : salaryComponents)
            total += salaryComponent.getDouble("amount") / C_MTHS;
        return total;
    }

    /**
     * Multiply the emi by the months between fromDate and thruDate to get the
     * total deductible loan amount
     *
     * @param userLogin
     * @param periodFrom
     * @param periodTo
     * @return Total Loan amount paid to employee This will give the total Loan
     *         Amount for the Fiscal Year also correctly
     */

    public static double getMonthlyLoanAmount(GenericValue userLogin, java.sql.Date periodFrom, java.sql.Date periodTo) {
        Date lminDate = periodFrom;
        Date lmaxDate = periodTo;
        Date lfromDate = null;
        Date lthruDate = null;
        double totalLoan = 0D;
        List<GenericValue> loans = getLoansForParty(userLogin, periodFrom, periodTo);
        for (GenericValue loan : loans) {
            /** Logic to get the loan emi amount depending on the tenure of loan **/
            lfromDate = (Date) loan.get("fromDate");
            if (lfromDate.after(periodFrom)) {
                lminDate = lfromDate;
            }
            lthruDate = (Date) loan.get("thruDate");
            if (lthruDate.before(periodTo)) {
                lmaxDate = lthruDate;
            }
            totalLoan += loan.getDouble("emi") * UtilDateTimeSME.getMonthInterval(lminDate, lmaxDate);
        }
        return totalLoan;
    }

    /**
     * Apr 8, 2009 Changed the entity for loans to MaxELoanDetail where loan
     * status is Admin Approved
     *
     * @param userLogin
     * @param periodFrom
     * @param periodTo
     * @return
     */
    public static List<GenericValue> getLoansForParty(GenericValue userLogin, java.sql.Date periodFrom,
                                                      java.sql.Date periodTo) {
        final String C_ADMIN_APPROVED = "EL_ADM_APPROVED";
        GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();
        String partyId = userLogin.getString("partyId");

        EntityCondition ec1 = EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, periodFrom);
        EntityCondition ec2 = EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, periodTo);
        EntityCondition ec3 = EntityCondition.makeCondition("partyId", partyId);
        EntityCondition ec4 = EntityCondition.makeCondition("statusId", C_ADMIN_APPROVED);
        EntityCondition entityCondition = EntityCondition.makeCondition(ec1, ec2, ec3, ec4);

        List<GenericValue> loans = null;
        try {
            loans = delegator.findList("MaxELoanDetail", entityCondition, null, null, null, false);
        } catch (GenericEntityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return loans;
    }

    /**
     * Get ProRated CTC periodFrom : Start Date of financial year (period) for
     * which payment will be given periodTo : End Date of financial year
     * (period) for which payment will be given Assuming both are in same
     * financial year
     * *
     */
    public static SalaryBean populateEstimatedAnnualSalaryDetail(String partyId, Date periodFrom, Date periodTo, GenericDelegator delegator)
            throws GenericEntityException {
        if (partyId == null) {
            Debug.logError("partyId is null", module);
            return null;
        }
        GenericValue fiscalYear = getFiscalYear(periodTo, delegator);
        if (fiscalYear == null) fiscalYear = PayrollHelper.getCurrentFiscalYear(delegator);

        Date yearBegin = fiscalYear.getDate("fromDate");
        Date yearEnd = fiscalYear.getDate("thruDate");

        SalaryBean salary = new SalaryBean(partyId, periodFrom, periodTo, delegator);
        // /Include Salary Flag salary.
        EntityCondition entityCondition = EntityCondition.makeCondition(UtilMisc.toMap("partyId", partyId));
        List<String> orderBy = new LinkedList<String>();
        orderBy.add("salaryHeadId");
        orderBy.add("fromDate");

        List<GenericValue> salaryHeads = delegator.findList("EmplSalDetail", entityCondition, null, orderBy, null, false);
        if (salaryHeads.size() == 0) {
            Debug.logError("No CTC allocated for this employee yet", module);
            return null;
        }
        String lsalHeadId = null;
        String lprevsalHeadId = "";

        Double l_amount = 0D;
        Double c_amount = 0D; // local loop amount for each name record
        Date l_cfromDate = null;
        Date l_cthruDate = null;
        Iterator<GenericValue> l = salaryHeads.iterator();
        List<GenericValue> listIt = new LinkedList<GenericValue>();
        Double proAmount = 0.0D;
        Map<String, Object> callCtx = FastMap.newInstance();
        GenericValue salGV = null; // Current EmplSal record
        GenericValue outGV = null; // Output EmplSal record
        callCtx.put("yearBegin", yearBegin); // Start of Financial Year
        callCtx.put("yearEnd", yearEnd); // End Date of Financial Year
        while (l.hasNext()) {
            salGV = l.next(); // Current Record
            lsalHeadId = (String) salGV.get("salaryHeadId");
            l_cfromDate = (Date) salGV.get("fromDate");
            l_cthruDate = (Date) salGV.get("thruDate");
            c_amount = salGV.getDouble("amount");
            // System.out.println(" lsalHeadId : " + lsalHeadId + " c_amount : "
            // + c_amount + " fromDate " + l_cfromDate + " thruDate " +
            // l_cthruDate);

            if (lprevsalHeadId == "") {
                /** First record so initialize previous name to present name **/
                lprevsalHeadId = lsalHeadId;
            }

            callCtx.put("salaryHeadId", lsalHeadId);
            callCtx.put("fromDate", l_cfromDate);
            callCtx.put("thruDate", l_cthruDate);
            callCtx.put("amount", c_amount);
            /**
             * Call function to calculate the pro-rated amounts for each salary
             * head If thruDate is null then make it last day of financial year
             */
            proAmount = calProRatedSalaryHead(callCtx, delegator);

            if (lsalHeadId.equalsIgnoreCase(lprevsalHeadId)) {
                l_amount = l_amount + proAmount;
                /** Keep adding the amounts for same salary head **/
                outGV = (GenericValue) salGV.clone(); // By default
                outGV.put("salaryHeadId", lprevsalHeadId);
                outGV.put("amount", l_amount);
            } else {
                /**
                 * Its a new Salary Head * So store the total for prevsalHeadId
                 * in the GenericValue
                 **/
                /** This will be part of output Map **/
                listIt.add(outGV);
                outGV = (GenericValue) salGV.clone();
                /** Now restart total for new Salary Head Name **/
                lprevsalHeadId = lsalHeadId;
                l_amount = proAmount;
            }
        }
        // Last record is left out
        outGV.put("salaryHeadId", lsalHeadId);
        outGV.put("amount", l_amount);
        /** This will be part of output Map **/
        listIt.add(outGV);

        // salaryHeads = EntityUtil.filterByDate(salaryHeads); //Only get the
        // current records from salary
        SalaryComponentBean bean = null;
        for (GenericValue salHead : listIt) {
            bean = new SalaryComponentBean(salHead);
            salary.addSalaryComponent(bean, delegator);
        }
        // salary.populateDerivedValues();
        return salary;
    }

    /**
     * This method should be called to get pro-rated annual salary head
     * component
     */
    public static Double calProRatedSalaryHead(Map<String, Object> context, GenericDelegator delegator) {
        // String lHead = (String)context.get("salaryHeadId");
        Date lfDate = (Date) context.get("fromDate");
        Date lthruDate = (Date) context.get("thruDate");
        Double lAmount = (Double) context.get("amount");
        String lsalHeadId = (String) context.get("salaryHeadId");
        Date temp = (Date) context.get("yearBegin");
        java.sql.Date yearBegin = new java.sql.Date(temp.getTime()); // Start of
        // financial
        // Year
        temp = (Date) context.get("yearEnd");
        java.sql.Date yearEnd = new java.sql.Date(temp.getTime()); // End of
        // financial
        // Year
        GenericValue fiscalYear = null;

        /*** Redundant check to get start and end date of fiscal Year **/
        try {
            fiscalYear = getFiscalYear(yearBegin, yearEnd, delegator);
        } catch (GenericEntityException e) {
            Debug.logError("Could not find fiscal year", module);
        }
        if (fiscalYear == null) {
            try {
                fiscalYear = getCurrentFiscalYear(delegator);
            } catch (GenericEntityException e) {
                Debug.logError("Could not find fiscal year", module);
            }
        }
        yearBegin = fiscalYear.getDate("fromDate");
        yearEnd = fiscalYear.getDate("thruDate");
        /*** Redundant check to get start and end date of fiscal Year **/

        if (lfDate.before(yearBegin)) {
            lfDate = yearBegin;
        }

        if (lthruDate == null) {
            lthruDate = yearEnd;
        }
        /**
         * If lthruDate is not null and it is before year begin then discard the
         * amount
         **/
        if (lthruDate.before(lfDate)) {
            return 0.0D;
        }
        Double fracYear = fracYearBetween(lfDate, lthruDate, UtilDateTimeSME.daysBetween(yearBegin, yearEnd));
        lAmount = lAmount * fracYear;

        System.out.println("calProRatedSalaryHead for fracYear === " + fracYear + " salaryHeadId === " + lsalHeadId
                + " pro amount is : " + lAmount);
        System.out.println("calProRatedSalaryHead for fracYear === " + fracYear + " salaryHeadId === " + lsalHeadId
                + " pro amount is : " + lAmount);
        return lAmount;
    }

    /**
     * Method to retrieve salary Head Name for given Salary Head Id *
     */
    public static String getSalaryHeadName(String salHeadId, GenericDelegator delegator) {
        String lname = null;
        GenericValue salGV = null;
        try {
            salGV = delegator.findOne("SalaryHead", true, "salaryHeadId", salHeadId);
            lname = salGV.getString("hrName");
        } catch (GenericEntityException e) {
            Debug.logError("Could not find salary Head Name for salary Head Id : " + salHeadId, module);
        }
        return lname;
    }

    public static Double getValueFromGrade(GenericDelegator delegator, String payGradeId, String salaryStepSeqId,
                                           String revisionIdStr) {

        try {
            GenericValue payScaleGV = delegator.findOne("SalaryStep", false, UtilMisc.toMap("payGradeId", payGradeId,
                    "salaryStepSeqId", salaryStepSeqId));
            int revisionId = Integer.parseInt(revisionIdStr);

            double minAmount = payScaleGV.getDouble("minAmount");
            double incAmount = payScaleGV.getDouble("incrementBy");
            int revision = Integer.parseInt(payScaleGV.getString("revisions"));
            double amount = minAmount;
            if (revisionId > 0 && revisionId <= revision) {
                amount += (incAmount * revisionId);
            }

            return new Double(amount);
        } catch (GenericEntityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    public static List<GenericValue> getAllCalculatedSalaryHeads(String salaryStructureId, GenericDelegator delegator)
            throws GenericEntityException {
        EntityCondition structureIdCondition = EntityCondition.makeCondition("salaryStructureId", salaryStructureId);
        EntityCondition formulaCondition = EntityCondition.makeCondition("salaryComputationTypeId", "FORMULA");
        EntityCondition condition = EntityCondition.makeCondition(structureIdCondition, formulaCondition);
        List<GenericValue> calculatedSalaryHeads = delegator.findList("SalaryStructureHeadDetail", condition, null, null,
                null, false);
        return calculatedSalaryHeads;
    }

    public static List<GenericValue> getAllRulesFor(GenericValue head, GenericDelegator delegator) throws GenericEntityException {
        if (head.getEntityName().equals("SalaryHead")) {
            EntityCondition condition = EntityCondition.makeCondition("salaryHeadId", head.getString("salaryHeadId"));
            return delegator.findList("SalaryHeadRule", condition, null, null, null, true);
        } else {
            EntityCondition condition = EntityCondition.makeCondition("salaryStructureHeadId", head
                    .getString("salaryStructureHeadId"));
            return delegator.findList("PayrollHeadRule", condition, null, null, null, false);
        }
    }

    public static String getSalaryFrequencyForCompany(GenericDelegator delegator) {
        GenericValue attrGV = null;
        try {
            attrGV = delegator.findOne("PartyAttribute", true, "partyId", "Company", "attrName", "SALARY_FREQUENCY");
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        if (attrGV != null) {
            return attrGV.getString("attrValue");
        }
        return "MONTHLY";
    }
}