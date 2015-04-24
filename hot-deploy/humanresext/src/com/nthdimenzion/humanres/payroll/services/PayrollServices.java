package com.nthdimenzion.humanres.payroll.services;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.*;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.accounting.TransactionHelper;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.transaction.GenericTransactionException;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.nthdimenzion.humanres.payroll.PayrollHelper;
import com.nthdimenzion.humanres.payroll.SalaryBean;
import com.nthdimenzion.humanres.payroll.SalaryComponentBean;
import com.nthdimenzion.humanres.payroll.UtilTax;
import com.nthdimenzion.humanres.payroll.calc.ExemptionCalc;
import com.nthdimenzion.humanres.payroll.calc.PayrollContext;
import com.nthdimenzion.humanres.payroll.calc.ResolverManager;
import com.nthdimenzion.humanres.payroll.calc.Rule;
import com.nthdimenzion.humanres.payroll.calc.TaxCalulator;
import com.nthdimenzion.humanres.payroll.calc.TaxDeclarationCalculator;
import com.nthdimenzion.humanres.payroll.resolver.RuleResolver;
import com.smebiz.common.UtilDateTimeSME;

/**
 * @author sandeep
 * @author pankaj
 * @date Apr 29, 2009 generatePF added to PaySlip generation
 */
public class PayrollServices {

    private static String module = "PayrollServices";
    private static String C_PERQ = "Perquisites";
    private static final String delimiter = "-";

    /**
     * Changed the code to get actual start Date and end Date of month for which
     * employee needs to be paid since he could have joined in the middle of
     * month or left earlier
     * *
     */
    public static Map<String, Object> generatePayslip(DispatchContext dctx, Map<String, ? extends Object> context)
            throws Exception {
        GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
        String partyId = (String) context.get("partyId");
        GenericValue emplValue = delegator.findOne("Party", false, "partyId", partyId);

        java.sql.Date periodFrom = (java.sql.Date) context.get("periodFrom");
        java.sql.Date periodTo = (java.sql.Date) context.get("periodTo");
        String linkToLMS = (String) context.get("linkToLMS");
        GenericValue newPayslip = delegator.makeValue("EmplPayslip");
        delegator.setNextSubSeqId(newPayslip, "payslipId", 0, 1);
        newPayslip.set("partyId", partyId);
        newPayslip.set("paidOn", UtilDateTime.nowTimestamp());
        newPayslip.set("periodFrom", periodFrom);
        newPayslip.set("periodTo", periodTo);
        newPayslip.create();

        String payslipId = newPayslip.getString("payslipId");
        Map<String, Object> cxt = FastMap.newInstance(); // Argument to method
        cxt.put("partyId", partyId);
        cxt.put("periodFrom", periodFrom);
        cxt.put("periodTo", periodTo);
        cxt.put("payslipId", payslipId);
        cxt.put("linkToLMS", linkToLMS); // Flag to check if LMS is to be used
        cxt.put("EXTRA_HEADS", context.get("EXTRA_HEADS"));
        // for Payslip data or not
        Map<String, Double> salaryAndPfAmountMap = createPayslipHeads(dctx, cxt);
        double headTotal = salaryAndPfAmountMap.get("headTotal");
        double pfAmount = salaryAndPfAmountMap.get("pfAmount");
        System.out.println(" Total Monthly Income Before Loan " + headTotal);
        System.out.println(" Extra Income ::: " + cxt.get("EXTRA_INCOME"));

        cxt.remove("EXTRA_HEADS");
        // Changed from currentDate to periodTo as payslip can be generated in
        // next financial year too
        cxt.put("payslipId", payslipId);

        double taxTotal = 0.00;
        double monthlyLoanAmount = 0.00;
        double incomeTax = 0.00;
        BigDecimal professionalTax = BigDecimal.ZERO;
        if (headTotal > 0.00) {
            Map taxMap = createTaxdeductionRecords(cxt, delegator);
            taxTotal = (Double) taxMap.get("taxAmount");
            System.out.println(" Total Monthly Tax " + taxTotal);
            professionalTax = (BigDecimal) taxMap.get("professionalTax");
            System.out.println(" Total Monthly professionalTax  " + professionalTax);
            storeTaxRecords(delegator, payslipId, taxTotal, professionalTax.doubleValue());
            incomeTax = taxTotal;
            taxTotal = taxTotal + professionalTax.doubleValue();
            monthlyLoanAmount = PayrollHelper.getMonthlyLoanAmount(emplValue, periodFrom, periodTo);
            headTotal = headTotal - monthlyLoanAmount;
            System.out.println(" Total Monthly Income After Loan " + headTotal);
            GenericValue newTaxRecord = delegator.makeValue("EmplMonthlyTaxdeduction");
            delegator.setNextSubSeqId(newTaxRecord, "id", 0, 1);
            newTaxRecord.set("amount", monthlyLoanAmount);
            newTaxRecord.set("payslipId", payslipId);
            newTaxRecord.set("taxType", "Loan/Advances");
            newTaxRecord.create();

        }
        headTotal = headTotal + monthlyLoanAmount;
        newPayslip.set("headTotal", headTotal);
        newPayslip.set("taxTotal", taxTotal);
        newPayslip.set("monthlyLoanAmount", monthlyLoanAmount);
        newPayslip.store();
        Map<String, Double> monthlyPaySlipData = new HashMap<String, Double>();
        monthlyPaySlipData.put("wages", headTotal + pfAmount);
        monthlyPaySlipData.put("salaryTotal", (headTotal - taxTotal - monthlyLoanAmount));
        monthlyPaySlipData.put("pfAmount", pfAmount);
        monthlyPaySlipData.put("incomeTax", incomeTax);
        monthlyPaySlipData.put("professionalTax", professionalTax.doubleValue());
        monthlyPaySlipData.put("monthlyLoanAmount", monthlyLoanAmount);

        /**
         *  HRMS Payroll-Accounting Integration
         *  Added By  :- @Samir
         */
        TransactionHelper.createPayrollTransaction(dctx, partyId, monthlyPaySlipData);
        Map<String, Object> returnMap = ServiceUtil.returnSuccess();
        return returnMap;
    }

    private static void storeTaxRecords(GenericDelegator delegator, String payslipId, Double totalTax,
                                        Double professionalTax) throws GenericEntityException {
        GenericValue newTaxRecord = delegator.makeValue("EmplMonthlyTaxdeduction");
        delegator.setNextSubSeqId(newTaxRecord, "id", 0, 1);
        newTaxRecord.set("amount", totalTax);
        newTaxRecord.set("payslipId", payslipId);
        newTaxRecord.set("taxType", "INCOMETAX");
        newTaxRecord.create();

        newTaxRecord = delegator.makeValue("EmplMonthlyTaxdeduction");
        delegator.setNextSubSeqId(newTaxRecord, "id", 0, 1);
        newTaxRecord.set("amount", professionalTax);
        newTaxRecord.set("payslipId", payslipId);
        newTaxRecord.set("taxType", "PROFESSIONALTAX");
        newTaxRecord.create();

    }

    private static Map<String, Double> createTaxdeductionRecords(Map<String, Object> context, GenericDelegator delegator)
            throws Exception {

        // Sum of the monthly paid out amount including the current month
        BigDecimal amountPaid = BigDecimal.ONE;

        Date periodTo = (Date) context.get("periodTo");
        GenericValue fiscalYear = PayrollHelper.getFiscalYear(periodTo, delegator);
        if (fiscalYear == null) fiscalYear = PayrollHelper.getCurrentFiscalYear(delegator);

        Date yearBegin = fiscalYear.getDate("fromDate");
        Date yearEnd = fiscalYear.getDate("thruDate");
        String partyId = (String) context.get("partyId");

        EntityCondition cn1 = EntityCondition.makeCondition("periodFrom", EntityOperator.GREATER_THAN_EQUAL_TO, yearBegin);
        EntityCondition cn2 = EntityCondition.makeCondition("periodTo", EntityOperator.LESS_THAN_EQUAL_TO, yearEnd);
        EntityCondition cn3 = EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId);
        EntityCondition condition = EntityCondition.makeCondition(EntityJoinOperator.AND, cn1, cn2, cn3);

        GenericValue emplValue = delegator.findOne("Party", UtilMisc.toMap("partyId", partyId), false);
        PayrollContext payrollCtx = PayrollContext.getInstance(emplValue);

        List<GenericValue> emplPayslipList = delegator.findList("PaySlipSalHeadDetailView", condition, null, null, null,
                false);
        GenericValue terminationRec = getTerminationRecordForParty(delegator, partyId);
        if (terminationRec != null && terminationRec.getDate("terminationDate") != null) {
            yearEnd = terminationRec.getDate("terminationDate");
        }
        System.out.println(" No of Payslips found " + emplPayslipList.size());
        BigDecimal totalDeductedAmount = BigDecimal.ZERO;

        Map<String, BigDecimal> amountPaidPerSalaryHead = new HashMap<String, BigDecimal>();
        Map<String, BigDecimal> amountPaidPerMonth = new HashMap<String, BigDecimal>();

        BigDecimal fullYearCTC = BigDecimal.ZERO;
        for (GenericValue gv : emplPayslipList) {
            if ("Y".equals(gv.getString("isCr"))) {
                String payslipId = gv.getString("payslipId");
                BigDecimal amount = new BigDecimal(gv.getDouble("amount"));
                amountPaid = amountPaid.add(amount);
                String salaryHeadId = gv.getString("salaryHeadId");
                BigDecimal salHeadAmount = amountPaidPerSalaryHead.get(salaryHeadId);
                if (salHeadAmount == null) {
                    salHeadAmount = amount;
                } else {
                    salHeadAmount = salHeadAmount.add(amount);
                }
                amountPaidPerSalaryHead.put(salaryHeadId, salHeadAmount);

                BigDecimal monthlyAmount = amountPaidPerMonth.get(payslipId);
                if (monthlyAmount == null) {
                    monthlyAmount = amount;
                } else {
                    monthlyAmount = monthlyAmount.add(amount);
                }
                amountPaidPerMonth.put(payslipId, monthlyAmount);

            } else {
                totalDeductedAmount = totalDeductedAmount.add(new BigDecimal(gv.getDouble("amount")));
            }
        }

        for (String payslipId : amountPaidPerMonth.keySet()) {
            System.out.println(payslipId + " = " + amountPaidPerMonth.get(payslipId));
        }

        System.out.println(" Total Monthly Salary Paid Till Date : " + amountPaid);
        System.out.println(" Total PF Till Date Real: " + totalDeductedAmount);

        EntityCondition entityCondition = EntityCondition.makeCondition(UtilMisc
                .toMap("partyId", partyId, "thruDate", null));
        List<GenericValue> empSalaryHeads = delegator.findList("EmplSalDetail", entityCondition, null, null, null, false);

        BigDecimal monthlyBenefit = BigDecimal.ZERO;
        BigDecimal monthlyDeductions = BigDecimal.ZERO;

        Map<String, Double> salaryHeadMap = new HashMap<String, Double>();
        int remainingMonths = PayrollHelper.monthsBetween(periodTo, yearEnd);

        for (GenericValue empSalaryHead : empSalaryHeads) {
            String salaryHeadId = empSalaryHead.getString("salaryHeadId");
            if ("Benefits".equals(empSalaryHead.getString("salaryHeadTypeId"))
                    || "Allowances".equals(empSalaryHead.getString("salaryHeadTypeId")))
                monthlyBenefit = monthlyBenefit.add(new BigDecimal(empSalaryHead.getDouble("amount") / 12));
            if ("Deductions".equals(empSalaryHead.getString("salaryHeadTypeId"))) {
                monthlyDeductions = monthlyDeductions.add(new BigDecimal(empSalaryHead.getDouble("amount") / 12));
            }
            if ("Y".equals(empSalaryHead.getString("isCr"))) {
                salaryHeadMap.put(empSalaryHead.getString("salaryHeadId"), new BigDecimal(
                        empSalaryHead.getDouble("amount") / 12).multiply(new BigDecimal(remainingMonths)).add(
                        amountPaidPerSalaryHead.get(salaryHeadId)).doubleValue());
            }
            fullYearCTC = fullYearCTC.add(new BigDecimal(empSalaryHead.getDouble("amount")));
        }
        System.out.println("FULL YEAR CTC " + fullYearCTC);

        // if (remainingMonths>0)
        PayrollContext.getInstance().getResolver().getResolver("SALARYHEAD").setLookupContext(salaryHeadMap);

        BigDecimal totalExemption = BigDecimal.ZERO;
        System.out.println(" Exemptions \n");
        System.out.println(" Salary Head\t|Amount \n");

        for (GenericValue empSalaryHead : empSalaryHeads) {
            if ("Allowances".equals(empSalaryHead.getString("salaryHeadTypeId"))) {
                Number number = null;
                try {
                    String salaryHeadId = empSalaryHead.getString("salaryHeadId");
                    number = ExemptionCalc.calculateExemption(emplValue, salaryHeadId);
                    System.out.println(salaryHeadId + "\t|" + number.doubleValue());
                    totalExemption = totalExemption.add(new BigDecimal(number.doubleValue()));
                } catch (GenericEntityException e) {

                }
            }
        }

        System.out.println(" Monthly Benefit === " + monthlyBenefit);
        System.out.println(" Total Exemption Paid === " + totalExemption);
        System.out.println(" Monthly Deductions === " + monthlyDeductions);

        System.out.println(" Months remaining " + remainingMonths);
        BigDecimal amountToBePaid = monthlyBenefit.multiply(new BigDecimal(remainingMonths));
        BigDecimal amountLeftForDeduction = monthlyDeductions.multiply(new BigDecimal(remainingMonths));
        BigDecimal totalDeduction = amountLeftForDeduction.add(totalDeductedAmount);
        System.out.println(" Total PF " + totalDeduction);
        BigDecimal totalIncome = amountPaid.add(amountToBePaid).add(totalDeduction);
        System.out.println(" Total CTC " + totalIncome);

        totalExemption = totalExemption.add(totalDeduction);
        System.out.println(" Total Exemptions " + totalExemption);

        BigDecimal taxdeclarationAmount = TaxDeclarationCalculator.calculate(partyId, delegator);
        System.out.println(" Total Tax Deductions " + taxdeclarationAmount);

        BigDecimal taxableIncome = totalIncome.subtract(taxdeclarationAmount).subtract(totalExemption);
        System.out.println(" taxable income expected : Real : " + taxableIncome);

        double taxAlreadyPaid = PayrollHelper.getTotalIncomeTaxPaid(partyId, new java.sql.Date(yearBegin.getTime()),
                new java.sql.Date(((java.util.Date) context.get("periodFrom")).getTime()), delegator);
        System.out.println("Tax Already Paid : " + taxAlreadyPaid);

        GenericValue prevSalInfo = delegator.findOne("EmplPrevSalInfo", UtilMisc.toMap("partyId", partyId), false);

        if (prevSalInfo != null) {
            taxableIncome = taxableIncome.add(prevSalInfo.getBigDecimal("prevGrossAmount"));
        }

        BigDecimal monthlyProfessionalTax = TaxCalulator.calculateMonthlyProfessionalTax(new BigDecimal(fullYearCTC
                .doubleValue() / 12), delegator);

        BigDecimal totalProfessionalTaxPaid = BigDecimal.ZERO;

        EntityCondition emplProfessionalCond = EntityCondition.makeCondition(condition, EntityCondition.makeCondition(
                "taxType", "PROFESSIONALTAX"));
        List<GenericValue> emplProfessionalTaxs = delegator.findList("EmplMonthlyTaxdeductionAndPayslip",
                emplProfessionalCond, null, null, null, false);

        for (GenericValue emplProfessionalTax : emplProfessionalTaxs) {
            totalProfessionalTaxPaid = totalProfessionalTaxPaid.add(emplProfessionalTax.getBigDecimal("amount"));
        }

        BigDecimal professionalTaxAmount = totalProfessionalTaxPaid.add(monthlyProfessionalTax.multiply(new BigDecimal(
                remainingMonths + 1)));

        System.out.println("Subtracting Professional Tax Amount " + professionalTaxAmount + " from Income of "
                + taxableIncome);
        taxableIncome = taxableIncome.subtract(professionalTaxAmount);

        payrollCtx.putAll(UtilTax.getTaxContext(partyId, taxableIncome.doubleValue(), delegator));
        payrollCtx.put("remainingMonths", remainingMonths);
        Map taxComputeCtx = new HashMap();
        taxComputeCtx.put("fromDate", context.get("periodFrom"));
        taxComputeCtx.put("thruDate", context.get("periodTo"));

        System.out.println("Calculating Tax on Income of " + taxableIncome);

        Map taxResultMap = TaxCalulator.calculateTax(taxComputeCtx, delegator);
        Double taxAmount = (Double) taxResultMap.get("taxAmount");
        System.out.println("************* Tax Amount " + taxAmount);
        if (prevSalInfo != null) {
            taxAmount = taxAmount - prevSalInfo.getDouble("prevTaxPaid");
            System.out.println("*************Subtracting Prev Tax Amount " + prevSalInfo.getDouble("prevTaxPaid"));
        }
        System.out.println("************* Tax Amount " + taxAmount);
        double currentMonthTax = ((taxAmount == null ? 0D : taxAmount) - taxAlreadyPaid) / (remainingMonths + 1);
        currentMonthTax = Math.max(0.0D, currentMonthTax);
        System.out.println("********** Monthly Tax Amount " + currentMonthTax);

        taxResultMap.put("professionalTax", monthlyProfessionalTax);

        if (remainingMonths > 0) {
            currentMonthTax = (Double) context.get("PAY_PERIOD_RATIO") * currentMonthTax * 12;
        }

        taxResultMap.put("taxAmount", currentMonthTax);
        return taxResultMap;
    }

    private static Map<String, Double> createPayslipHeads(DispatchContext dctx, Map<String, Object> context) throws Exception {
        double headTotal = 0.00;
        double providentFundAmount = 0.00;
        GenericDelegator delegator = (GenericDelegator) (GenericDelegator) dctx.getDelegator();
        String partyId = (String) context.get("partyId");
        String payslipId = (String) context.get("payslipId");
        EntityCondition partyCond = EntityCondition.makeCondition("partyId", partyId);
        Date lperiodFrom = (Date) context.get("periodFrom");
        Date lperiodTo = (Date) context.get("periodTo");
        String linkToLMS = (String) context.get("linkToLMS");
        EntityCondition perqCond = EntityCondition.makeCondition("salaryHeadTypeId", EntityOperator.NOT_EQUAL, C_PERQ);

        /**
         * SELECT *
         FROM EMPL_SAL
         WHERE (party_Id = '10041'
         AND (
         (FROM_DATE <= '2011-07-31' AND FROM >= '2011-07-01') --- IN BETWEEN THE MONTH
         OR (
         FROM_DATE < '2011-07-01' --- IS ATTACHED PRIOR TO THE MONTH
         AND 	(
         (THRU_DATE <= '2011-07-31' AND THRU_DATE >= '2011-07-01') --- THRU DATE FALLS WITHIN THE MONTH
         OR
         THRU_DATE IS NULL  --- IS NOT EXPIRED
         OR
         THRU_DATE > '2011-07-31'  -- IS EXPIRING AFTER THIS MONTH
         )
         )
         );
         */

        EntityCondition FROM_DATE_IN_BETWEEN_MONTH = EntityCondition.makeCondition(EntityCondition.makeCondition(
                "fromDate", EntityOperator.GREATER_THAN_EQUAL_TO, lperiodFrom), EntityCondition.makeCondition("fromDate",
                EntityOperator.LESS_THAN_EQUAL_TO, lperiodTo));

        EntityCondition NON_EXPIRED = EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null);
        EntityCondition EXPIRING_IN_BETWEEN_MONTH = EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate",
                EntityOperator.GREATER_THAN_EQUAL_TO, lperiodFrom), EntityCondition.makeCondition("thruDate",
                EntityOperator.LESS_THAN_EQUAL_TO, lperiodTo));

        EntityCondition EXPIRED_AFTER = EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN, lperiodTo);

        EntityCondition EXPIRING_CONDITION = EntityCondition.makeCondition(EXPIRED_AFTER, EntityOperator.OR,
                EntityCondition.makeCondition(NON_EXPIRED, EntityOperator.OR, EXPIRING_IN_BETWEEN_MONTH));

        EntityCondition FROM_DATE_LESS_THAN_MONTH_BEGIN = EntityCondition.makeCondition("fromDate",
                EntityOperator.LESS_THAN, lperiodFrom);

        EntityCondition condition2 = EntityCondition.makeCondition(EntityCondition.makeCondition(
                FROM_DATE_LESS_THAN_MONTH_BEGIN, EXPIRING_CONDITION));

        EntityCondition condition = EntityCondition.makeCondition(partyCond, perqCond, EntityCondition.makeCondition(
                FROM_DATE_IN_BETWEEN_MONTH, EntityOperator.OR, condition2));

        System.out.println(" Condition to get all the Applicable Salary Structre Details " + condition);

        List<GenericValue> salaryComponents = delegator.findList("EmplSalDetail", condition, null, null, null, false);

        System.out.println(" Condition to get all the Applicable Salary Structre Details " + condition);
        System.out.println(" No of salary components " + salaryComponents.size());

        String lsalId = null;
        Map<String, Object> argMap = FastMap.newInstance();
        context.remove("payslipId"); // PayslipId is not a parameter for
        // createPFservice
        context.remove("linkToLMS");
        argMap.putAll(context);

        /** setLookUpContext to set salary Map **/

        GenericValue emplValue = null;
        try {
            emplValue = delegator.findOne("Party", false, "partyId", partyId);
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        PayrollContext payrollCtx = PayrollContext.getInstance(emplValue);
        ResolverManager resolver = PayrollContext.getResolver();
        Map<String, Double> salaryHeadMap = FastMap.newInstance();
        Map<String, Object> paramCtx = FastMap.newInstance();
        paramCtx.put("partyId", partyId);
        paramCtx.put("periodFrom", lperiodFrom);
        paramCtx.put("periodTo", lperiodTo);

        Calendar cal = Calendar.getInstance();
        cal.setTime(lperiodFrom);
        List<GenericValue> allPaySlipsHeads = FastList.newInstance();
        int daysInBetween = (int) ((lperiodTo.getTime() - lperiodFrom.getTime()) / (24 * 60 * 60 * 1000));
        System.out.println("\n\n Days In Between=" + daysInBetween);

        double totalDaysToPay = daysInBetween + 1;

        GenericValue employment = getEmployementRecordForParty(delegator, partyId);
        if (employment == null) {
            throw new Exception("No Employment record found for Party " + partyId);
        }
        Date employementDate = new Date(employment.getTimestamp("fromDate").getTime());

        GenericValue terminationRec = getTerminationRecordForParty(delegator, partyId);
        if (terminationRec != null) {
            Date terminatedDate = terminationRec.getDate("terminationDate");
            if (terminatedDate.before(lperiodFrom)) {
                throw new Exception("Party Id " + partyId + " is terminated so not processing the Payroll");
            }

            // If termination date after month begin and before month end, then
            // consider from
            // month beginning to the month date of termination.
            if (terminatedDate.after(lperiodFrom) && terminatedDate.before(lperiodTo)) {
                lperiodTo = terminatedDate;
            }
        }

        // Check if the joining date is after the Salary Month Begin date,
        // Then the starting period would be from date of joining and not
        // starting of month.
        if (employementDate.after(lperiodTo) || employementDate.equals(lperiodFrom)) {
            System.out.println(" Changing From Period based on Joining Date " + employementDate);
            lperiodFrom = employementDate;
        }

        double interval = 1.00;
        Date monthStartDate = new Date(lperiodFrom.getTime());
        for (GenericValue salaryComponent : salaryComponents) {
            /**
             * Calculate fraction of Yearly CTC according to the time interval
             * between periodFrom and periodTo dates
             */
            java.util.Date emplSalThruDate = salaryComponent.getDate("thruDate");
            java.util.Date emplSalFromDate = salaryComponent.getDate("fromDate");

            // For Multiple Salary Structure in a month.
            double intervalDays = 0;
            if ((lperiodFrom.getMonth() == emplSalFromDate.getMonth()) && (lperiodFrom.getYear() == emplSalFromDate.getYear())) {

                if (lperiodFrom.before(emplSalFromDate)) {
                    System.out.println(" Changing From Period based Salary Structure attached in between the Month Date "
                            + emplSalFromDate);
                    lperiodFrom = new java.sql.Date(emplSalFromDate.getTime());
                }
                if (emplSalThruDate == null) {
                    intervalDays = ((int) ((lperiodTo.getTime() - emplSalFromDate.getTime()) / (24 * 60 * 60 * 1000)) + 1);

                    System.out.println("\n\n Thru Date Null Salary Period :" + emplSalFromDate + " To " + lperiodTo);
                    System.out.println("SALARAY WILL BE PAID FOR:" + intervalDays + "DAYS");
                } else {
                    // This is for EXPIRED SALARY STRUCTURE
                    intervalDays = ((int) ((emplSalThruDate.getTime() - emplSalFromDate.getTime()) / (24 * 60 * 60 * 1000)) + 1);
                    System.out.println("\n\n Thru Date is not NULL Salary Period :" + lperiodFrom + " To "
                            + emplSalThruDate);
                    System.out.println("SALARAY WILL BE PAID FOR:" + intervalDays + "DAYS");
                }

            } else {
                if (emplSalThruDate != null && emplSalThruDate.after(lperiodFrom)) {
                    intervalDays = ((int) (((emplSalThruDate.before(lperiodTo) ? emplSalThruDate.getTime() : lperiodTo
                            .getTime()) - (emplSalFromDate.after(lperiodFrom) ? emplSalFromDate.getTime() : lperiodFrom
                            .getTime())) / (24 * 60 * 60 * 1000)) + 1);
                    System.out.println("\n\n Thru Date is not NULL Salary Period :"
                            + (emplSalFromDate.after(lperiodFrom) ? emplSalFromDate : lperiodFrom) + " To "
                            + (emplSalThruDate.before(lperiodTo) ? emplSalThruDate : lperiodTo));
                } else {
                    intervalDays = ((int) ((lperiodTo.getTime() - lperiodFrom.getTime()) / (24 * 60 * 60 * 1000)) + 1);
                    System.out.println("\n\n Thru Date is not NULL Salary Period :" + lperiodFrom + " To " + lperiodTo);
                }
                System.out.println("SALARAY WILL BE PAID FOR:" + intervalDays + "DAYS");

            }
            System.out.println(" Salary Head " + salaryComponent.getString("hrName") + " == "
                    + salaryComponent.getDouble("amount"));

            if (linkToLMS != null && linkToLMS.equals("Y")) {
                System.out.println("linkToLMS.equals(Y) : " + linkToLMS.equals("Y"));
                Map<String, Object> result = PayrollHelper.getPaidFracYear(paramCtx, delegator);
                Double totalUnpaidDays = (Double) result.get("totalUnpaidDays");
                intervalDays = intervalDays - totalUnpaidDays;
                System.out.println("[Monthly Payslip ] Total Unpaid Days " + interval);
            }
            System.out.println("[Monthly Payslip ] Working Days " + interval);

            interval = (intervalDays / totalDaysToPay);
            System.out.println("Monthly(intervalDays/totalDaysToPay)=" + interval);
            double frac = (interval); // By Default full month
            frac = (frac / ("MONTHLY".equals(PayrollHelper.getSalaryFrequencyForCompany(delegator)) ? 12.0 : 24.0));
            System.out.println(" Fractional Factor " + frac);

            context.put("PAY_PERIOD_RATIO", new Double(frac));
            System.out.println(" Salary Head " + salaryComponent.getString("hrName") + " == "
                    + salaryComponent.getDouble("amount"));
            BigDecimal bigDecimal = BigDecimal.valueOf(frac);
            bigDecimal.setScale(2, BigDecimal.ROUND_CEILING);
            frac = bigDecimal.doubleValue();

            Double amount = salaryComponent.getDouble("amount") * frac;
            System.out.println(" Monthly Value - " + amount);

            GenericValue newPayslipHead = delegator.makeValue("EmplPayslipHead");

            lsalId = salaryComponent.getString("salaryHeadId");
            newPayslipHead.set("salaryHeadId", lsalId);
            newPayslipHead.set("amount", amount);

            if (salaryHeadMap.get(lsalId) != null) {
                double d = salaryHeadMap.get(lsalId);
                d = d + amount;
                salaryHeadMap.put(lsalId, d);
            } else
                salaryHeadMap.put(lsalId, amount); // Set up Salary Map

            newPayslipHead.set("payslipId", payslipId);
            String isCr = salaryComponent.getString("isCr");
            newPayslipHead.set("isCr", isCr);
            allPaySlipsHeads.add(newPayslipHead);

            headTotal += ("Y".equalsIgnoreCase(isCr) ? amount : -amount);
            System.out.println("HEAD TOTAL " + headTotal);

            /** Do PF insert work **/
            if (PayrollHelper.getSalaryHeadName(lsalId, delegator).indexOf("Provident") > 0) { // Employee
                // Provident
                // Fund
                /** Insert into EmployeePF the PF Amount for this employee **/
                providentFundAmount = amount;
                argMap.put("empAmount", amount);
                LocalDispatcher dispatcher = dctx.getDispatcher();
                try {
                    argMap.remove("EXTRA_HEADS");
                    dispatcher.runSync("createPFservice", argMap);
                } catch (Exception e) {
                    Debug.logError(e, module);
                }
            }
            lperiodFrom = monthStartDate;
        }

        Map<String, GenericValue> uniquePaySlip = new HashMap<String, GenericValue>();

        System.out.println(" ******** Total Pay Heads " + allPaySlipsHeads.size());

        for (GenericValue paySlip : allPaySlipsHeads) {
            String key = paySlip.getString("salaryHeadId");
            GenericValue previousHead = uniquePaySlip.get(key);
            if (previousHead != null) {
                previousHead.set("amount", previousHead.getBigDecimal("amount").add(paySlip.getBigDecimal("amount"))
                        .doubleValue());
            } else {
                uniquePaySlip.put(key, paySlip);
            }
        }
        context.put("payslips", uniquePaySlip.values());
        System.out.println(" ******** Total Unique Pay Heads " + uniquePaySlip.values().size());
        for (GenericValue paySlip : uniquePaySlip.values()) {
            paySlip.setString("id", delegator.getNextSeqId("EmplPayslipHead"));
            paySlip.create();
        }

        resolver.getResolver("SALARYHEAD").setLookupContext(salaryHeadMap); // To

        List<Map> salaryHeads = (List) context.get("EXTRA_HEADS");
        BigDecimal extraIncome = new BigDecimal(0);
        if (salaryHeads != null) {
            for (Map each : salaryHeads) {
                GenericValue extraHead = delegator.findOne("SalaryHead", true, "salaryHeadId", each.get("salaryHeadId"));
                BigDecimal amount = BigDecimal.ZERO;
                if ("FORMULA".equals(extraHead.getString("salaryComputationTypeId"))) {
                    List<GenericValue> headRules = PayrollHelper.getAllRulesFor(extraHead, delegator);
                    for (GenericValue headRule : headRules) {
                        Rule rule = (Rule) resolver.resolve(resolver.encode(new String[]{RuleResolver.COMP_NAME,
                                headRule.getString("ruleId")}));
                        amount = amount.add(new BigDecimal(rule.evaluate().doubleValue()));
                        extraIncome = extraIncome.add(amount);
                    }
                } else {
                    amount = new BigDecimal(((Double) (each.get("lumpsum") == null ? 0D : each.get("lumpsum"))).doubleValue());
                    extraIncome = extraIncome.add(amount);
                }
                GenericValue newPayslipHead = delegator.makeValue("EmplPayslipHead");
                newPayslipHead.set("id", delegator.getNextSeqIdLong("EmplPayslipHead"));
                newPayslipHead.set("salaryHeadId", each.get("salaryHeadId"));
                newPayslipHead.set("amount", amount.doubleValue());
                newPayslipHead.set("payslipId", payslipId);
                newPayslipHead.set("isCr", "Y");
                newPayslipHead.create();
            }
        }
        context.put("EXTRA_INCOME", extraIncome);
        System.out.println(" Extra Income " + extraIncome);
        Map<String, Double> salaryAndPFAmountMap = new HashMap<String, Double>();
        salaryAndPFAmountMap.put("headTotal", (headTotal + extraIncome.doubleValue()));
        salaryAndPFAmountMap.put("pfAmount", providentFundAmount);
        return salaryAndPFAmountMap;
    }

    public static GenericValue getEmployementRecordForParty(GenericDelegator delegator, String partyId)
            throws Exception {
        EntityCondition ec1 = EntityCondition.makeCondition("partyIdTo", partyId);
        List<GenericValue> l = delegator.findList("Employment", ec1, null, null, null, false);
        GenericValue gv = EntityUtil.getFirst(l);
        return gv;
    }

    public static GenericValue getTerminationRecordForParty(GenericDelegator delegator, String partyId)
            throws Exception {
        EntityCondition ec1 = EntityCondition.makeCondition("partyId", partyId);
        List<GenericValue> l = delegator.findList("EmplTermination", ec1, null, null, null, false);
        GenericValue emplTerminationGv = null;
        for (GenericValue gv : l) {
            List<GenericValue> terminationStatus = delegator.findList("EmplTerminationStatus", EntityCondition
                    .makeCondition(UtilMisc.toMap("statusId", "ET_ADM_APPROVED", "terminationId", gv
                            .getString("terminationId"))), null, null, null, false);
            if (terminationStatus.size() > 0) {
                emplTerminationGv = gv;
                break;
            }
        }
        return emplTerminationGv;
    }

    /**
     * GenericValue terminationRec = getTerminationRecord(delegator, partyId);
     * boolean isTerminated = false;
     * if (terminationRec != null && terminationRec.getTimestamp("terminationDate") != null) {
     * Calendar calendar = Calendar.getInstance();
     * calendar.setTime(new java.util.Date());
     * calendar.a(terminationRec.getTimestamp("terminationDate"));
     * }
     * Insert into EmployeePF the new record
     * <p/>
     * *
     */
    public static Map<String, Object> createPF(DispatchContext dctx, Map<String, Object> context) {
        GenericDelegator delegator = (GenericDelegator) (GenericDelegator) dctx.getDelegator();
        Double lempAmount = (Double) context.get("empAmount"); // Provident Fund
        // Employee Contribution
        final Double cRate = 12D; // Assuming Employee contributes only standard
        // 12% rate to PF
        final Double cEmployerContr = 8.33; // Employers Contribution to Pension
        // Fund = 8.33% of (Basic + DA)
        Double lpensionAmount = lempAmount * (cEmployerContr / cRate); // lempAmount
        // * (100/lRate)* lEmployerContr/100;
        Double lepfDiff = lempAmount - lpensionAmount; // Employers Contribution
        // to Provident Fund =// 12% of (Basic + DA) - // Pension Fund //
        // Contribution
        Double lWages = lempAmount * (100 / cRate); // Total of Employee DA + //
        // BASIC

        Double lrefund = 0D; // Default 0 refund
        String lpartyId = (String) context.get("partyId");
        Date lperiodFrom = (Date) context.get("periodFrom");
        Date lperiodTo = (Date) context.get("periodTo");
        EntityCondition ec1 = EntityCondition.makeCondition("periodFrom", lperiodFrom);
        EntityCondition ec2 = EntityCondition.makeCondition("periodTo", lperiodTo);
        EntityCondition ec3 = EntityCondition.makeCondition("partyId", lpartyId);

        GenericValue pfGV = null;
        List<EntityCondition> ecList = new ArrayList<EntityCondition>();
        ecList.add(ec1);
        ecList.add(ec2);
        ecList.add(ec3);
        try {
            pfGV = EntityUtil.getFirst(delegator.findList("EmplPf", EntityCondition.makeCondition(ecList),
                    null, null, null, false));
        } catch (GenericEntityException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        if (pfGV == null) {
            pfGV = delegator.makeValue("EmplPf");
        } else {
            BigDecimal prevEmpAmount = pfGV.getBigDecimal("employeeAmount");
            BigDecimal prevPensionAmount = pfGV.getBigDecimal("pensionAmount");
            BigDecimal prevWages = pfGV.getBigDecimal("wages");
            lempAmount = prevEmpAmount.add(new BigDecimal(lempAmount)).doubleValue();
            lpensionAmount = prevPensionAmount.add(new BigDecimal(lpensionAmount)).doubleValue();
            lWages = prevWages.add(new BigDecimal(lWages)).doubleValue();
        }
        pfGV.putAll(UtilMisc.toMap("partyId", lpartyId, "employeeAmount", lempAmount, "periodFrom", lperiodFrom,
                "periodTo", lperiodTo));
        pfGV.putAll(UtilMisc.toMap("epfDiff", lepfDiff, "pensionAmount", lpensionAmount, "voluntaryRate", cRate, "wages",
                lWages, "refund", lrefund));
        Debug.logInfo(" partyId " + lpartyId + " employeeAmount " + lempAmount + " periodFrom " + lperiodFrom
                + " periodTo " + lperiodTo, module);
        try {
            delegator.createOrStore(pfGV);
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError("Problems creating the PF record for party [" + lpartyId + "]");
        }
        Map<String, Object> result = ServiceUtil.returnSuccess();
        result.put("partyId", lpartyId); //
        result.put("periodFrom", lperiodFrom);
        return result;
    }

    /**
     * Retire old Employee CTC Head (if exists) when a new CTC is created This
     * should be called before creating any new CTC records for the employee
     *
     * @param dctx
     * @param context
     * @return
     * @throws GenericEntityException
     */
    public static Map<String, Object> retireEmplSalComponent(DispatchContext dctx, Map<String, ? extends Object> context)
            throws GenericEntityException {
        GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();//GenericDelegator.getGenericDelegator("default");

        String lpartyId = (String) context.get("partyId");
        String lsalaryHeadId = (String) context.get("salaryHeadId");
        java.util.Date lfromDate = (java.util.Date) context.get("fromDate");
        if (lfromDate == null) {
            lfromDate = new java.util.Date(); // If no input by user then
            // current date is fromDate
        }

        // String lsalaryHeadId = (String)context.get("salaryHeadId");
        Date l_oldthruTime = new Date(UtilDateTime.addDaysToTimestamp(UtilDateTime.toTimestamp(lfromDate), -1).getTime());
        // For old records if they exist the thruDate = lfromDate - 1

        EntityCondition partyCondition = EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, lpartyId);
        // EntityCondition salHeadCondition =
        // EntityCondition.makeCondition("salaryHeadId", EntityOperator.EQUALS,
        // lsalaryHeadId);

        /** All records which have thruDate null **/
        EntityCondition thruNullCondition = EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null);
        List<EntityCondition> cl = UtilMisc.toList(partyCondition, thruNullCondition);

        if (lsalaryHeadId != null) {
            EntityCondition salHeadCondition = EntityCondition.makeCondition("salaryHeadId", EntityOperator.EQUALS,
                    lsalaryHeadId);
            cl.add(salHeadCondition);
        }

        /**
         * Select * from EMPL_SAL where partyId = lpartyId and salaryHeadId =
         * lsalHeadId // lsalHeadId is given as parameter and thruDate = null
         */

        EntityCondition condition = EntityCondition.makeCondition(cl);
        List<GenericValue> salComps = null;
        try {
            salComps = delegator.findList("EmplSal", condition, null, null, null, false);
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError("Problems finding the old EmplSal records for party [" + lpartyId + "]");
        }

        try {
            for (GenericValue empsal : salComps) {
                empsal.set("thruDate", l_oldthruTime);
                empsal.store(); // Update the record
            }
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError("Problems updating the old EmplSal records for party [" + lpartyId
                    + "] salaryHeadId :" + lsalaryHeadId);
        }

        Map<String, Object> returnMap = ServiceUtil.returnSuccess("Employee Salary Heads ThruDate updated");
        return returnMap;
    }

    /**
     * For variable pay components if only one SalaryHead is changed/added then
     * call retireEmplSalComponent to update thruDate of old record if null Then
     * insert the new SalaryHead with fromDate and amount in EmplSal
     */
    public static Map<String, Object> updateEmplSalary(DispatchContext dctx, Map<String, ? extends Object> context)
            throws GenericEntityException {
        GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();//GenericDelegator.getGenericDelegator("default");

        String lpartyId = (String) context.get("partyId");
        String lsalaryHeadId = (String) context.get("salaryHeadId");
        Date lfromDate = (Date) context.get("fromDate");
        Date lthruDate = (Date) context.get("thruDate");
        Double lamount = (Double) context.get("amount");
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Map<String, Object> ctx = UtilMisc.toMap("partyId", lpartyId, "salaryHeadId", lsalaryHeadId, "fromDate", lfromDate);

        Map<String, Object> returnMap = ServiceUtil.returnSuccess("Employee Salary Heads ThruDate updated");

        // Retire old records in EmplSal if any
        try {
            returnMap = dispatcher.runSync("retireEmplSalComponent", ctx);
        } catch (GenericServiceException e) {
            Debug.logError(e, module);
        }

        GenericValue ctcGV = delegator.makeValue("EmplSal");
        ctcGV.putAll(UtilMisc.toMap("partyId", lpartyId, "salaryHeadId", lsalaryHeadId, "amount", lamount, "fromDate",
                lfromDate, "thruDate", lthruDate));
        try {
            ctcGV.create();
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError("Problems creating the CTC record for party [" + lpartyId + "]");
        }
        return returnMap;
    }

    /**
     * Find the total amounts for each party for the financial Period
     *
     * @param: periodFrom, periodTo
     * @output: consolidated PF for the party
     * *
     */
    public static Map<String, Object> getPartyConsolidatedPF(DispatchContext dctx, Map<String, Object> context) {
        GenericDelegator delegator = (GenericDelegator) (GenericDelegator) dctx.getDelegator();
        Date lperiodFrom = (Date) context.get("periodFrom");
        Date lperiodTo = (Date) context.get("periodTo");
        String lpartyId = (String) context.get("partyId");

        System.out.println("**************************** lperiodFrom " + lperiodFrom);
        System.out.println("**************************** lperiodTo " + lperiodTo);

        EntityCondition partyCond = null;
        EntityCondition fromCond = null;
        EntityCondition toCond = null;

        if (lpartyId != null) {
            partyCond = EntityCondition.makeCondition(("partyId"), EntityOperator.EQUALS, lpartyId);
        } else {
            return ServiceUtil.returnError("PartyId cannot be null");
        }

        fromCond = EntityCondition.makeCondition(("periodFrom"), EntityOperator.GREATER_THAN_EQUAL_TO, lperiodFrom);
        toCond = EntityCondition.makeCondition(("periodTo"), EntityOperator.LESS_THAN_EQUAL_TO, lperiodTo);

        List<String> orderBy = new LinkedList<String>();
        orderBy.add("periodFrom");
        List<GenericValue> listIt = null;
        EntityCondition cond = EntityCondition.makeCondition(partyCond, fromCond, toCond);
        try {
            listIt = delegator.findList("EmplPf", cond, null, orderBy, null, false);
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil
                    .returnError("An Error occurred searching the EmplPf for party :" + lpartyId + e.getMessage());
        }

        if (listIt.size() == 0) {
            return ServiceUtil.returnError("No PF records found for this party : " + lpartyId + " lpartyId");
        }

        Double lwages = 0D;
        Double lemployeeAmount = 0D;
        Double lepfDiff = 0D;
        Double lpensionAmount = 0D;
        // Double lrate = 12D; //Voluntary Rate
        Double lrefund = 0D;
        String lremarks = "";
        for (GenericValue pf : listIt) {
            lwages = lwages + pf.getDouble("wages");
            lemployeeAmount = lemployeeAmount + pf.getDouble("employeeAmount");
            lepfDiff = lepfDiff + pf.getDouble("epfDiff");
            lpensionAmount = lpensionAmount + pf.getDouble("pensionAmount");
            lrefund = lrefund + pf.getDouble("refund");
            lremarks = pf.getString("remarks");
        }
        GenericValue pf = listIt.get(0);
        pf.set("wages", lwages);
        pf.set("employeeAmount", lemployeeAmount);
        pf.set("epfDiff", lepfDiff);
        pf.set("pensionAmount", lpensionAmount);
        pf.set("refund", lrefund);
        pf.set("remarks", lremarks);

        Map<String, Object> result = ServiceUtil.returnSuccess("Employee EmplPf searched");
        result.put("partyId", lpartyId);
        result.put("pf", pf);
        System.out.println("result :" + result);
        return result;
    }

    /**
     * Get consolidated PFs for all parties for the given period *
     */
    public static Map<String, Object> getConsolidatedPF(DispatchContext dctx, Map<String, Object> context) {
        GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
        Date lperiodFrom = (Date) context.get("periodFrom");
        Date lperiodTo = (Date) context.get("periodTo");
        String lpartyId = (String) context.get("partyId");

        EntityCondition partyCondition = EntityCondition.makeCondition(UtilMisc.toMap("partyId", lpartyId));
        Set<String> fields = new HashSet<String>(); // fieldsToSelect
        fields.add("partyId");
        List<String> orderBy = new LinkedList<String>();
        orderBy.add("partyId");
        List<GenericValue> parties = new LinkedList<GenericValue>();
        EntityFindOptions options = new EntityFindOptions();
        options.setDistinct(true);
        EntityCondition partyPf = EntityCondition.makeCondition(UtilMisc.toMap("partyId", lpartyId));
        try {
            parties = delegator.findList("EmplPf", partyPf, fields, orderBy, options, false);
        } catch (GenericEntityException e) {
            e.printStackTrace();
            ServiceUtil.returnError("Could not find any parites in PF table");
        }
        Map<String, Object> argMap = FastMap.newInstance();
        List<GenericValue> listIt = new LinkedList<GenericValue>();
        argMap.put("periodFrom", lperiodFrom);
        argMap.put("periodTo", lperiodTo);
        argMap.put("partyId", lpartyId);
        Map<String, Object> temp = FastMap.newInstance();
        for (GenericValue lparty : parties) {
            argMap.put("partyId", lparty.get("partyId"));
            LocalDispatcher dispatcher = dctx.getDispatcher();
            try {
                System.out.println("argMap : " + argMap);
                temp = dispatcher.runSync("getPartyConsolidatedPFService", argMap);
            } catch (Exception e) {
                Debug.logError(e, module);
            }
            listIt.add((GenericValue) temp.get("pf"));
        }
        Map<String, Object> result = ServiceUtil.returnSuccess("Employee EmplPf searched");
        result.put("listIt", listIt);
        System.out.println("result :" + result);
        return result;
    }

    /**
     * Get PF grouped by each month for the financial Period select period_from,
     * sum(employeeAmount) from empl_pf where period_from and period_to in
     * financial year group by period_from
     *
     * @param periodFrom, periodTo
     * @output List of Pf GVs grouped by periodFrom
     * *
     */
    public static Map<String, Object> getMonthWiseConsolidatedPF(DispatchContext dctx, Map<String, Object> context) {
        GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
        Date lperiodFrom = (Date) context.get("periodFrom");
        Date lperiodTo = (Date) context.get("periodTo");

        System.out.println("**************************** lperiodFrom " + lperiodFrom);
        System.out.println("**************************** lperiodTo " + lperiodTo);

        EntityCondition fromCond = null;
        EntityCondition toCond = null;

        fromCond = EntityCondition.makeCondition(("periodFrom"), EntityOperator.GREATER_THAN_EQUAL_TO, lperiodFrom);
        toCond = EntityCondition.makeCondition(("periodTo"), EntityOperator.LESS_THAN_EQUAL_TO, lperiodTo);

        List<String> orderBy = new LinkedList<String>();
        orderBy.add("periodFrom");
        List<GenericValue> data = null;
        EntityCondition cond = EntityCondition.makeCondition(fromCond, toCond);
        try {
            data = delegator.findList("EmplPf", cond, null, orderBy, null, false);
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError("An Error occurred searching the EmplPf for Monthly PF :" + lperiodFrom
                    + e.getMessage());
        }

        if (data.size() == 0) {
            return ServiceUtil.returnError("No PF records found for this Monthly PF : " + lperiodFrom + " lperiodFrom");
        }

        /** Current record values **/
        Double cwages = 0D;
        Double cemployeeAmount = 0D;
        Double cepfDiff = 0D;
        Double cpensionAmount = 0D;
        // Double crate = 12D; //Voluntary Rate
        Double crefund = 0D;
        // String cremarks = ""; // Only last month remarks will be output
        Date cperiodFrom = null;
        Date outperiodFrom = null;
        Double lwages = 0D;
        Double lemployeeAmount = 0D;
        Double lepfDiff = 0D;
        Double lpensionAmount = 0D;
        // Double lrate = 12D; //Voluntary Rate
        Double lrefund = 0D;
        String lremarks = "";

        List<GenericValue> listIt = new LinkedList<GenericValue>(); // Output
        // list

        GenericValue pfOut = null;
        for (GenericValue pf : data) {
            pfOut = (GenericValue) pf.clone();
            cwages = pf.getDouble("wages");
            cemployeeAmount = pf.getDouble("employeeAmount");
            cepfDiff = pf.getDouble("epfDiff");
            cpensionAmount = pf.getDouble("pensionAmount");
            crefund = pf.getDouble("refund");
            lremarks = pf.getString("remarks"); // Only last month remarks will
            // be output
            cperiodFrom = pf.getDate("periodFrom");
            Debug.logInfo("@@@@@@@@@@@@ cperiodFrom : " + cperiodFrom, module);
            /** First record **/
            if (outperiodFrom == null) {
                outperiodFrom = cperiodFrom;
            }
            /**
             * If new record is of same periodFrom then add the amounts else add
             * new record to output listIt
             * **/
            if (cperiodFrom.equals(outperiodFrom)) {
                lwages = lwages + cwages;
                lemployeeAmount = lemployeeAmount + cemployeeAmount;
                lepfDiff = lepfDiff + cepfDiff;
                lpensionAmount = lpensionAmount + cpensionAmount;
                lrefund = lrefund + crefund;
                continue;
            } else {
                /** Insert the previous record in listIt **/
                {
                    pfOut.set("periodFrom", outperiodFrom);
                    pfOut.set("wages", lwages);
                    pfOut.set("employeeAmount", lemployeeAmount);
                    pfOut.set("epfDiff", lepfDiff);
                    pfOut.set("pensionAmount", lpensionAmount);
                    pfOut.set("refund", lrefund);
                    pfOut.set("remarks", lremarks);
                    listIt.add(pfOut);
                }

                /** Start for new period **/
                outperiodFrom = cperiodFrom;
                lwages = cwages;
                lemployeeAmount = cemployeeAmount;
                lepfDiff = cepfDiff;
                lpensionAmount = cpensionAmount;
                lrefund = crefund;
            }
        }
        /**
         * Here loop will exit out without setting pfOut for last period so
         * extra step
         */
        {
            pfOut.set("periodFrom", outperiodFrom);
            pfOut.set("wages", lwages);
            pfOut.set("employeeAmount", lemployeeAmount);
            pfOut.set("epfDiff", lepfDiff);
            pfOut.set("pensionAmount", lpensionAmount);
            pfOut.set("refund", lrefund);
            pfOut.set("remarks", lremarks);
            listIt.add(pfOut);
        }

        Map<String, Object> result = ServiceUtil.returnSuccess("Employee EmplPf searched");
        result.put("listIt", listIt);
        System.out.println("result :" + result);
        return result;
    }

    /**
     * Delete Salary Structure *
     */
    public static Map<String, Object> deleteSalaryStructure(DispatchContext dctx, Map<String, Object> context) {
        GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
        String vstructureId = (String) context.get("salaryStructureId");
        /** First delete the child records from SalaryStructureHead **/

        EntityCondition cond = EntityCondition.makeCondition("salaryStructureId", EntityOperator.EQUALS, vstructureId);

        List<GenericValue> data = null;
        try {
            data = delegator.findList("SalaryStructureHead", cond, null, null, null, false);
            delegator.removeAll(data);
            data = delegator.findList("SalaryStructure", cond, null, null, null, false);
            delegator.removeAll(data);
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError("An Error occurred deleting the SalaryStructureHead for salaryStructureId :"
                    + vstructureId + e.getMessage());
        }
        Map<String, Object> result = ServiceUtil.returnSuccess();
        return result;
    }

    /**
     * Run payslip generation for each passed employee *
     */
    public static Map<String, Object> runPayroll(DispatchContext dctx, Map<String, Object> context) {
        Date lperiodFrom = (Date) context.get("periodFrom");
        Date lperiodTo = (Date) context.get("periodTo");
        String lpartyId = (String) context.get("partyId");
        String llinkToLMS = (String) context.get("linkToLMS");
        List<Map> EXTRA_HEADS = (List) context.get("EXTRA_HEADS");
        // String lselected = (String)context.get("selected");
        System.out.println("\n\n**************************** lperiodFrom " + lperiodFrom);
        System.out.println("\n\n************************ lperiodTo " + lperiodTo);
        System.out.println("\n\n************************ llinkToLMS " + llinkToLMS);

        Map<String, Object> argMap = FastMap.newInstance();
        argMap.put("periodFrom", lperiodFrom);
        argMap.put("periodTo", lperiodTo);
        argMap.put("partyId", lpartyId);
        argMap.put("linkToLMS", llinkToLMS);
        argMap.put("EXTRA_HEADS", EXTRA_HEADS);

        Map<String, Object> result = ServiceUtil.returnSuccess();

        LocalDispatcher dispatcher = dctx.getDispatcher();

        try {
            System.out.println("argMap : " + argMap);
            dispatcher.runSync("generatePayslip", argMap);
        } catch (Exception e) {
            Debug.logError(e, module);
            result = ServiceUtil.returnError("Payslip could not be generated for partyId: " + lpartyId);
        }

        return result;
    }

    /**
     * select party_Id, tax_type, sum(amount) from Empl_tax_deduction where
     * period_from >= lperiodFrom and period_To <= lperiodTo group by party_id,
     * tax_type order by party_id, tax_type
     *
     * @param dctx
     * @param context
     * @return listIt: List of partyId, taxType, amount
     */
    public static Map<String, Object> findPartyTotalTDS(DispatchContext dctx, Map<String, Object> context) {
        Date lperiodFrom = (Date) context.get("periodFrom");
        Date lperiodTo = (Date) context.get("periodTo");
        String lpartyId = (String) context.get("partyId");
        String ltaxType = (String) context.get("taxType");
        System.out.println("\n\n**************************** lperiodFrom " + lperiodFrom);
        System.out.println("\n\n************************ lperiodTo " + lperiodTo);

        EntityCondition partyCond = EntityCondition.makeCondition("partyId", lpartyId);
        EntityCondition fromCond = EntityCondition.makeCondition("periodFrom", EntityOperator.GREATER_THAN_EQUAL_TO,
                lperiodFrom);
        EntityCondition toCond = EntityCondition.makeCondition("periodTo", EntityOperator.LESS_THAN_EQUAL_TO, lperiodTo);
        EntityCondition typeCond = EntityCondition.makeCondition("taxType", ltaxType);
        EntityCondition cond = EntityCondition.makeCondition(partyCond, fromCond, toCond, typeCond);
        GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
        Set<String> fields = new HashSet<String>();
        fields.add("partyId");
        fields.add("taxType");
        fields.add("amount");
        List<String> orderBy = new LinkedList<String>();
        orderBy.add("partyId");
        orderBy.add("taxType");
        List<GenericValue> tdsList = null;
        try {
            tdsList = delegator.findList("EmplTaxDeduction", cond, fields, orderBy, null, false);
        } catch (GenericEntityException e) {
            ServiceUtil.returnError("Error finding TDS for : " + lpartyId + " :" + e.getMessage());
        }
        if (tdsList.size() == 0) {
            Debug.log("No TDS deducted for this employee yet[" + lpartyId + " from date :" + lperiodFrom + "]", module);
            return FastMap.newInstance(); // Empty Map
        }

        String lTaxType = null;
        String lprevTaxType = "";

        Double l_amount = 0D;
        Double c_amount = 0D; // local loop amount for each tds record
        Iterator<GenericValue> l = tdsList.iterator();
        List<GenericValue> listIt = new LinkedList<GenericValue>(); // Output
        // List
        GenericValue tdsGV = null; // Current TDS record
        GenericValue outGV = null; // Output TDS record
        while (l.hasNext()) {
            tdsGV = l.next(); // Current Record
            lTaxType = (String) tdsGV.get("taxType");
            c_amount = tdsGV.getDouble("amount");
            if (lprevTaxType == "") {
                /** First record so initialize previous name to present name **/
                lprevTaxType = lTaxType;
            }

            if (lTaxType.equalsIgnoreCase(lprevTaxType)) {
                l_amount = l_amount + c_amount;
                /** Keep adding the amounts for same taxType **/
                outGV = (GenericValue) tdsGV.clone(); // By default
            } else {
                /**
                 * Its a new Tax Type * So store the total for prevTaxType in
                 * the GenericValue
                 **/
                outGV.put("taxType", lprevTaxType);
                outGV.put("amount", l_amount);
                /** This will be part of output Map **/
                listIt.add(outGV);
                outGV = (GenericValue) tdsGV.clone();
                /** Now restart total for new Salary Head Name **/
                lprevTaxType = lTaxType;
                l_amount = c_amount;
            }
        }
        // Last record is left out
        outGV.put("taxType", lTaxType);
        outGV.put("amount", l_amount);
        System.out.println("==========Exit Pro Loop===================");
        listIt.add(outGV);

        Map<String, Object> result = ServiceUtil.returnSuccess();
        result.put("listIt", listIt);
        return result;
    }

    /**
     * Get consolidated TDS for all parties for the given period *
     */
    public static Map<String, Object> findTotalTDS(DispatchContext dctx, Map<String, Object> context) {
        GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
        String strPeriodFrom = (String) context.get("periodFrom");
        String strPeriodTo = (String) context.get("periodTo");
        String ltaxType = (String) context.get("taxType");
        strPeriodFrom = strPeriodFrom + " 00:00:00";
        Debug.logInfo("strPeriodFrom :" + strPeriodFrom, module);
        Date lperiodFrom = new Date(UtilDateTimeSME.toDateYMD(strPeriodFrom.trim(), delimiter).getTime());
        strPeriodTo = strPeriodTo + " 00:00:00";
        Debug.logInfo("strPeriodTo :" + strPeriodTo, module);
        Date lperiodTo = new Date(UtilDateTimeSME.toDateYMD(strPeriodTo.trim(), delimiter).getTime());

        String lpartyId = (String) context.get("partyId");
        List<GenericValue> parties = new LinkedList<GenericValue>();

        if (lpartyId == null || lpartyId.length() == 0) {
            /** All parties with payslip in this period to be selected **/
            Set<String> fields = new HashSet<String>(); // fieldsToSelect
            fields.add("partyId");
            List<String> orderBy = new LinkedList<String>();
            orderBy.add("partyId");

            EntityFindOptions options = new EntityFindOptions();
            options.setDistinct(true);
            EntityCondition fromCond = EntityCondition.makeCondition("periodFrom", EntityOperator.GREATER_THAN_EQUAL_TO,
                    lperiodFrom);
            EntityCondition toCond = EntityCondition
                    .makeCondition("periodTo", EntityOperator.LESS_THAN_EQUAL_TO, lperiodTo);
            EntityCondition typeCond = EntityCondition.makeCondition("taxType", ltaxType);
            EntityCondition cond = EntityCondition.makeCondition(fromCond, toCond, typeCond);
            try {
                parties = delegator.findList("EmplTaxDeduction", cond, fields, orderBy, options, false);
            } catch (GenericEntityException e) {
                e.printStackTrace();
                ServiceUtil.returnError("Could not find any parites in tax deduction table");
            }
        } else {
            GenericValue partyGV = null;
            try {
                partyGV = delegator.findByPrimaryKey("Party", UtilMisc.toMap("partyId", lpartyId));
            } catch (GenericEntityException e) {
                e.printStackTrace();
                ServiceUtil.returnError("Could not find the party in Party table :" + lpartyId);
            }
            parties.add(partyGV);
        }
        Map<String, Object> argMap = FastMap.newInstance();
        List<GenericValue> listIt = new LinkedList<GenericValue>();
        argMap.put("periodFrom", lperiodFrom);
        argMap.put("periodTo", lperiodTo);
        argMap.put("taxType", ltaxType);
        Map<String, Object> temp = FastMap.newInstance();
        for (GenericValue lparty : parties) {
            argMap.put("partyId", lparty.get("partyId"));
            LocalDispatcher dispatcher = dctx.getDispatcher();
            try {
                temp = dispatcher.runSync("findPartyTotalTDS", argMap);
            } catch (Exception e) {
                Debug.logError(e, module);
            }
            if (!temp.isEmpty()) {
                listIt.addAll((List<GenericValue>) temp.get("listIt"));
            }
        }
        Map<String, Object> result = ServiceUtil.returnSuccess("Employee TDS searched");
        result.put("listIt", listIt);
        System.out.println("result :" + result);
        return result;
    }

    /**
     * select party_Id, sum(employee_amount), sum(epf_diff), sum(pension_amount)
     * from empl_pf where period_from >= lperiodFrom and period_To <= lperiodTo
     * group by party_id order by party_id
     *
     * @param dctx
     * @param context
     * @return listIt: List of partyId, taxType, amount
     */
    public static Map<String, Object> findPartyTotalPF(DispatchContext dctx, Map<String, Object> context) {
        Date lperiodFrom = (Date) context.get("periodFrom");
        Date lperiodTo = (Date) context.get("periodTo");
        String lpartyId = (String) context.get("partyId");
        System.out.println("\n\n**************************** lperiodFrom " + lperiodFrom);
        System.out.println("\n\n************************ lperiodTo " + lperiodTo);

        EntityCondition partyCond = EntityCondition.makeCondition("partyId", lpartyId);
        EntityCondition fromCond = EntityCondition.makeCondition("periodFrom", EntityOperator.GREATER_THAN_EQUAL_TO,
                lperiodFrom);
        EntityCondition toCond = EntityCondition.makeCondition("periodTo", EntityOperator.LESS_THAN_EQUAL_TO, lperiodTo);
        EntityCondition cond = EntityCondition.makeCondition(partyCond, fromCond, toCond);
        GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
        Set<String> fields = new HashSet<String>();
        fields.add("partyId");
        fields.add("employeeAmount");
        fields.add("epfDiff");
        fields.add("pensionAmount");
        List<String> orderBy = new LinkedList<String>();
        orderBy.add("partyId");
        List<GenericValue> pfList = null;
        try {
            pfList = delegator.findList("EmplPf", cond, fields, orderBy, null, false);
        } catch (GenericEntityException e) {
            ServiceUtil.returnError("Error finding PF for : " + lpartyId + " :" + e.getMessage());
        }
        if (pfList.size() == 0) {
            Debug.logError("No PF deducted for this employee yet", module);
            return ServiceUtil.returnError("No PF deducted for this employee yet[" + lpartyId + " from date :"
                    + lperiodFrom + "]");
        }

        Double l_employeeAmount = 0D;
        Double l_epfDiff = 0D;
        Double l_pensionAmount = 0D;
        Iterator<GenericValue> l = pfList.iterator();
        List<GenericValue> listIt = new LinkedList<GenericValue>(); // Output
        // List
        GenericValue pfGV = null; // Current TDS record
        GenericValue outGV = null; // Output TDS record
        while (l.hasNext()) {
            pfGV = l.next(); // Current Record
            l_employeeAmount = l_employeeAmount + (Double) pfGV.get("employeeAmount");
            l_epfDiff = l_epfDiff + (Double) pfGV.get("epfDiff");
            l_pensionAmount = l_pensionAmount + (Double) pfGV.get("pensionAmount");
        }
        outGV = (GenericValue) pfGV.clone();
        outGV.put("employeeAmount", l_employeeAmount);
        outGV.put("epfDiff", l_epfDiff);
        outGV.put("pensionAmount", l_pensionAmount);
        listIt.add(outGV);

        Map<String, Object> result = ServiceUtil.returnSuccess();
        result.put("listIt", listIt);
        return result;
    }

    /**
     * Get consolidated PF for all parties for the given period *
     */
    public static Map<String, Object> findTotalPF(DispatchContext dctx, Map<String, Object> context) {
        GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
        String strPeriodFrom = (String) context.get("periodFrom");
        String strPeriodTo = (String) context.get("periodTo");
        strPeriodFrom = strPeriodFrom + " 00:00:00";
        Debug.logInfo("strPeriodFrom :" + strPeriodFrom, module);
        Date lperiodFrom = new Date(UtilDateTimeSME.toDateYMD(strPeriodFrom.trim(), delimiter).getTime());
        strPeriodTo = strPeriodTo + " 00:00:00";
        Debug.logInfo("strPeriodTo :" + strPeriodTo, module);
        Date lperiodTo = new Date(UtilDateTimeSME.toDateYMD(strPeriodTo.trim(), delimiter).getTime());

        String lpartyId = (String) context.get("partyId");
        List<GenericValue> parties = new LinkedList<GenericValue>();

        if (lpartyId == null || lpartyId.length() == 0) {
            /** All parties with payslip in this period to be selected **/
            Set<String> fields = new HashSet<String>(); // fieldsToSelect
            fields.add("partyId");
            List<String> orderBy = new LinkedList<String>();
            orderBy.add("partyId");

            EntityFindOptions options = new EntityFindOptions();
            options.setDistinct(true);
            EntityCondition fromCond = EntityCondition.makeCondition("periodFrom", EntityOperator.GREATER_THAN_EQUAL_TO,
                    lperiodFrom);
            EntityCondition toCond = EntityCondition
                    .makeCondition("periodTo", EntityOperator.LESS_THAN_EQUAL_TO, lperiodTo);
            EntityCondition cond = EntityCondition.makeCondition(fromCond, toCond);
            try {
                parties = delegator.findList("EmplPf", cond, fields, orderBy, options, false);
            } catch (GenericEntityException e) {
                e.printStackTrace();
                ServiceUtil.returnError("Could not find any parites in PF table");
            }
        } else {
            GenericValue partyGV = null;
            try {
                partyGV = delegator.findByPrimaryKey("Party", UtilMisc.toMap("partyId", lpartyId));
            } catch (GenericEntityException e) {
                e.printStackTrace();
                ServiceUtil.returnError("Could not find the party in Party table :" + lpartyId);
            }
            parties.add(partyGV);
        }
        Map<String, Object> argMap = FastMap.newInstance();
        List<GenericValue> listIt = new LinkedList<GenericValue>();
        argMap.put("periodFrom", lperiodFrom);
        argMap.put("periodTo", lperiodTo);

        Map<String, Object> temp = FastMap.newInstance();
        for (GenericValue lparty : parties) {
            argMap.put("partyId", lparty.get("partyId"));
            LocalDispatcher dispatcher = dctx.getDispatcher();
            try {
                temp = dispatcher.runSync("findPartyTotalPF", argMap);
            } catch (Exception e) {
                Debug.logError(e, module);
            }
            listIt.addAll((List<GenericValue>) temp.get("listIt"));
        }
        Map<String, Object> result = ServiceUtil.returnSuccess("Employee PF searched");
        result.put("listIt", listIt);
        System.out.println("result :" + result);
        return result;
    }

    public static Map<String, Object> updateEmployeeSalary(DispatchContext dctx, Map<String, Object> context) {

        boolean beganTransaction = false;
        try {

            beganTransaction = TransactionUtil.begin();

            GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
            List<GenericValue> parties = delegator.findByAnd("PartyPayGrade", UtilMisc.toMap("isCurrent", "Y"));

            GenericValue fiscalYearGV = PayrollHelper.getCurrentFiscalYear(delegator);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(fiscalYearGV.getDate("thruDate"));
            calendar.add(Calendar.DATE, 1);

            Date fromDate = new Date(calendar.getTime().getTime());

            calendar.setTime(fiscalYearGV.getDate("thruDate"));
            calendar.add(Calendar.YEAR, 1);

            Date thruDate = new Date(calendar.getTime().getTime());

            for (GenericValue partyPayGradeGV : parties) {
                String partyId = partyPayGradeGV.getString("partyId");

                String payGradeId = partyPayGradeGV.getString("payGradeId");
                String salaryStepSeqId = partyPayGradeGV.getString("salaryStepSeqId");
                String revision = partyPayGradeGV.getString("revision");

                // This would be the new basic amount
                Double BASIC_AMOUNT = PayrollHelper.getValueFromGrade(delegator, payGradeId, salaryStepSeqId, revision);

                List<GenericValue> partiesSalStrucGV = delegator.findByAnd("PartySalaryStructure", UtilMisc.toMap(
                        "partyId", partyId, "inUse", "Y"));

                GenericValue partySalStrucGV = null;
                if (partiesSalStrucGV != null && partiesSalStrucGV.size() > 0) {
                    partySalStrucGV = partiesSalStrucGV.get(0);
                }

                String salaryStructureId = partySalStrucGV.getString("salaryStructureId");

                // Start preparing new salary
                SalaryBean salBean = new SalaryBean(partyId, fromDate, thruDate, delegator);

                Map<String, Double> salMap = new HashMap<String, Double>();

                Map map1 = populateBasicSalaryComponents(delegator, salBean, salaryStructureId, BASIC_AMOUNT);
                salMap.putAll(map1);

                populateOtherSalaryComponents(delegator, partyId, salaryStructureId, salBean, salMap);

                // New Salary is now ready

                // Moving out the existing Employee Salary Components

                context.put("partyId", partyId);
                context.put("fromDate", fromDate);
                Debug.logInfo("fromDate :" + fromDate + " partyId : " + partyId, module);

                delegator.removeByAnd("EmplSal", UtilMisc.toMap("partyId", partyId));

                Debug.logInfo("retireEmplSalComponent has been run ", module);

                List<GenericValue> emplSalaryHeads = FastList.newInstance();
                for (SalaryComponentBean bean : salBean.getAllComponents()) {
                    Map<String, Object> argMap = new HashMap<String, Object>();
                    argMap.put("partyId", partyId);
                    argMap.put("salaryHeadId", bean.getSalaryHeadId());
                    argMap.put("amount", bean.getAmount());
                    argMap.put("fromDate", bean.getFromDate());
                    argMap.put("thruDate", bean.getThruDate());
                    System.out.println("\n\n@@@ CREATE @@@@ : salaryHeadId :" + bean.getSalaryHeadId() + "  amount "
                            + bean.getAmount());

                    emplSalaryHeads.add(delegator.create("EmplSal", argMap));
                }

                delegator.storeAll(emplSalaryHeads);
            }
        } catch (Throwable t) {
            t.printStackTrace();
            try {
                TransactionUtil.rollback();
            } catch (GenericTransactionException gte) {
            }
        } finally {
            try {
                TransactionUtil.commit(beganTransaction);
            } catch (GenericTransactionException gte) {
            }
        }
        Map<String, Object> returnMap = ServiceUtil.returnSuccess();
        return returnMap;
    }

    private static Map populateBasicSalaryComponents(GenericDelegator delegator, SalaryBean salBean,
                                                     String salaryStructureId, Double amount) throws GenericEntityException {

        List<GenericValue> salaryStructureHeads = delegator.findByAnd("SalaryStructureHeadDetail", UtilMisc.toMap(
                "salaryStructureId", salaryStructureId, "hrName", "Basic", "salaryHeadTypeId", "Benefits"));

        Map<String, Double> salMap = new HashMap<String, Double>();
        salMap.put("salaryStructureId", new Double(salaryStructureId));

        SalaryComponentBean compBean = null;
        for (GenericValue salStrucHeadGV : salaryStructureHeads) {
            String salStructureHeadId = salStrucHeadGV.getString("salaryStructureHeadId");
            String salaryHeadId = salStrucHeadGV.getString("salaryHeadId");
            String headName = salStrucHeadGV.getString("hrName");
            String salaryHeadTypeId = salStrucHeadGV.getString("salaryHeadTypeId");

            compBean = new SalaryComponentBean();
            compBean.setAmount(amount);
            compBean.setSalaryHeadId(salaryHeadId);
            compBean.setFromDate(salBean.getFromDate());
            compBean.setThruDate(salBean.getThruDate());
            compBean.setSalaryHeadName(headName);
            compBean.setSalaryHeadTypeId(salaryHeadTypeId);
            salBean.addSalaryComponent(compBean, delegator);
            salMap.put(compBean.getSalaryHeadId(), amount);
        }
        return salMap;
    }

    public static void populateOtherSalaryComponents(GenericDelegator delegator, String partyId,
                                                     String salaryStructureId, SalaryBean salBean, Map context) throws GenericEntityException {

        GenericValue emplValue = delegator.findOne("Party", false, UtilMisc.toMap("partyId", partyId));

        PayrollContext.getInstance(emplValue, true);
        ResolverManager resolverMgr = PayrollContext.getResolver();
        resolverMgr.getResolver("SALARYHEAD").setLookupContext(context);

        List<GenericValue> formulaHeads = PayrollHelper.getAllCalculatedSalaryHeads(salaryStructureId, delegator);

        SalaryComponentBean compBean = null;
        for (GenericValue head : formulaHeads) {
            List<GenericValue> headRules = PayrollHelper.getAllRulesFor(head, delegator);
            Number amount = 0;
            for (GenericValue headRule : headRules) {
                Rule rule = (Rule) resolverMgr.resolve(resolverMgr.encode(new String[]{RuleResolver.COMP_NAME,
                        headRule.getString("ruleId")}));
                amount = rule.evaluate();
                if (amount.doubleValue() != 0) // why?? JUST calculating the
                    // first rule for this head
                    break;
            }
            compBean = new SalaryComponentBean(head.getString("salaryStructureHeadId"), amount.doubleValue(), salBean
                    .getFromDate(), salBean.getThruDate(), delegator);
            System.out.println("Calcualte :@@@@@ salaryHeadId :" + compBean.getSalaryHeadId() + "  amount "
                    + compBean.getAmount());
            salBean.addSalaryComponent(compBean, delegator);
        }
    }

    public static Map<String, Object> run(DispatchContext dctx, Map context) throws Exception {
        String jobId = (String) context.remove("jobId");
        Date fromPeriod = (Date) context.get("periodFrom");
        Date toPeriod = (Date) context.get("periodTo");

        GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
        GenericValue job = delegator.findOne("PayrollJob", false, "jobId", jobId, "fromPeriod", fromPeriod, "toPeriod",
                toPeriod);
        job.setString("status", "RUNNING");

        delegator.storeByCondition("PayrollJob", UtilMisc.toMap("status", "RUNNING"), EntityCondition
                .makeCondition(UtilMisc.toMap("jobId", jobId, "fromPeriod", fromPeriod, "toPeriod", toPeriod)));

        List<Map> adhocSalaryHeadList = (List) job.get("adhocSalaryHeadList");
        HashSet<String> employeeIdList = (HashSet<String>) job.get("employeeIdList");
        context.put("EXTRA_HEADS", adhocSalaryHeadList);
        int failures = 0;
        long startTime = System.currentTimeMillis();
        for (String partyId : employeeIdList) {
            context.put("partyId", partyId);
            try {
                dctx.getDispatcher().runSync("generatePayslip", context);
            } catch (Throwable t) {
                failures++;
            }
        }
        long endTime = System.currentTimeMillis();

        long timeTaken = endTime - startTime;

        delegator.storeByCondition("PayrollJob", UtilMisc.toMap("status", "FINISHED"), EntityCondition
                .makeCondition(UtilMisc.toMap("jobId", jobId, "fromPeriod", fromPeriod, "toPeriod", toPeriod)));
        delegator.storeByCondition("PayrollJob", UtilMisc.toMap("failureCount", new Long(failures)), EntityCondition
                .makeCondition(UtilMisc.toMap("jobId", jobId, "fromPeriod", fromPeriod, "toPeriod", toPeriod)));
        delegator.storeByCondition("PayrollJob", UtilMisc.toMap("timeTaken", new Double(timeTaken)), EntityCondition
                .makeCondition(UtilMisc.toMap("jobId", jobId, "fromPeriod", fromPeriod, "toPeriod", toPeriod)));
        return ServiceUtil.returnSuccess();
    }
}
