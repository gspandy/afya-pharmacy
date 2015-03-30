package org.ofbiz.accounting;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.ServiceUtil;

import java.math.BigDecimal;
import java.math.MathContext;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: NthDimenzion
 * @since 1.0
 */
public class TransactionHelper {


	 private static final String EMPLOYEE_GL_PREFIX = "EMP_ADV_";

	    private static final String SALARY_PAYABLE_GL = "3000000_2";
	    private static final String PF_ACCOUNT_GL = "3000000_1";
	    private static final String INCOME_TAX_GL = "3100000_2";
	    private static final String PROFESSIONAL_TAX_GL = "3100000_1";
	    private static final String EMPLOYEE_WAGES_GL = "2100000_8";
	    private static final String CLAIMS_PAYABLE_GL = "3000000_3";
	    private static final String LOANS_PAYABLE_GL = "8030101";

	    private static final String PAYROLL_TRANSACTION_TYPE = "SALARY";
	    private static final String CLAIM_TRANSACTION_TYPE = "CLAIM";
	    private static final String LOAN_TRANSACTION_TYPE = "ELOAN";
	    private static final String REQUISITION_ISSUE = "REQ_ISSUE";

	    private static final Map<String, String> CLAIM_TYPE_LEDGER_MAP = new HashMap<String, String>();

	    static {
	        CLAIM_TYPE_LEDGER_MAP.put("CT_BROADBAND", "2400000_1");
	        CLAIM_TYPE_LEDGER_MAP.put("CT_CAB", "2400000_2");
	        CLAIM_TYPE_LEDGER_MAP.put("CT_FG_CAB", "2400000_3");
	        CLAIM_TYPE_LEDGER_MAP.put("CT_FG_MEALS", "2400000_4");
	        CLAIM_TYPE_LEDGER_MAP.put("CT_HARDWARE", "2400000_5");
	        CLAIM_TYPE_LEDGER_MAP.put("CT_MEALS", "2400000_6");
	    }

	    public static boolean createPayrollTransaction(DispatchContext dispatchContext, String employeeId, Map<String, Double> monthlyPayslipDataMap) {
	        Double salaryTotal = monthlyPayslipDataMap.get("salaryTotal");
	        Double wages = monthlyPayslipDataMap.get("wages");
	        Double pfAmount = monthlyPayslipDataMap.get("pfAmount");
	        Double incomeTax = monthlyPayslipDataMap.get("incomeTax");
	        Double professionalTax = monthlyPayslipDataMap.get("professionalTax");
	        Double monthlyLoanAmount = monthlyPayslipDataMap.get("monthlyLoanAmount");
	        System.out.println("************ Transaction Helper************");
	        System.out.println("************ Wages Total*************"+wages);
	        System.out.println("************ Salary Total*************"+salaryTotal);
	        System.out.println("************ PFAmount Total*************"+pfAmount);
	        System.out.println("************ ProfessionalTax Total*************"+professionalTax);
	        System.out.println("************ MonthlyLoanAmount Total*************"+monthlyLoanAmount);
	        if(!wages.equals(salaryTotal+pfAmount+incomeTax+professionalTax+monthlyLoanAmount)){
	            System.out.println("************ Difference Amount*************"+salaryTotal+pfAmount+incomeTax+professionalTax);
	            throw new RuntimeException("Credit sum and debit sum must be same");
	        }
	        List<Map<String, Object>> transactionEntries = new ArrayList<Map<String, Object>>();
	        String currencyUomId = getCurrencyUomIdForCompany(dispatchContext.getDelegator(),"Company");
	        transactionEntries.add(createTransactionEntry(wages, EMPLOYEE_WAGES_GL,null, "D", currencyUomId, "","Company",""));
	        transactionEntries.add(createTransactionEntry(pfAmount, PF_ACCOUNT_GL,null, "C", currencyUomId, "","Company",""));
	        transactionEntries.add(createTransactionEntry(professionalTax, PROFESSIONAL_TAX_GL,null, "C", currencyUomId, "","Company",""));
	        transactionEntries.add(createTransactionEntry(incomeTax, INCOME_TAX_GL,null, "C", currencyUomId, "","Company",""));
	        transactionEntries.add(createTransactionEntry(salaryTotal, SALARY_PAYABLE_GL,null, "C", currencyUomId, "","Company",""));
	        if (monthlyLoanAmount != null && monthlyLoanAmount > 0d) {
	            String employeeGl = getEmployeeGl(employeeId);
	            transactionEntries.add(createTransactionEntry(monthlyLoanAmount, employeeGl,null, "C", currencyUomId, "","Company",""));
	        }
	        boolean result = createTransactionAndTransactionEntries(dispatchContext, transactionEntries, UtilDateTime.nowTimestamp(), PAYROLL_TRANSACTION_TYPE);
	        return result;
	    }

	    public static boolean createClaimTransaction(DispatchContext dispatchContext, String claimType, Double claimApprovedAmount) {
	        if (claimApprovedAmount == null) {
	            return false;
	        }
	        String currencyUomId = getCurrencyUomIdForCompany(dispatchContext.getDelegator(),"Company");
	        List<Map<String, Object>> transactionEntries = new ArrayList<Map<String, Object>>();
	        transactionEntries.add(createTransactionEntry(claimApprovedAmount, CLAIM_TYPE_LEDGER_MAP.get(claimType),null, "D", currencyUomId, "","Company",""));
	        transactionEntries.add(createTransactionEntry(claimApprovedAmount, CLAIMS_PAYABLE_GL,null, "C", currencyUomId, "","Company",""));
	        boolean result = createTransactionAndTransactionEntries(dispatchContext, transactionEntries, UtilDateTime.nowTimestamp(), CLAIM_TRANSACTION_TYPE);
	        return result;
	    }

	    public static boolean createLoanTransaction(DispatchContext dispatchContext, String employeeId, Double approvedLoanAmount) {
	        List<Map<String, Object>> transactionEntries = new ArrayList<Map<String, Object>>();
	        String currencyUomId = getCurrencyUomIdForCompany(dispatchContext.getDelegator(),"Company");
	        transactionEntries.add(createTransactionEntry(approvedLoanAmount, null,null, "D", currencyUomId, "","Company",""));
	        transactionEntries.add(createTransactionEntry(approvedLoanAmount, LOANS_PAYABLE_GL,null, "C", currencyUomId, "","Company",""));
	        boolean result = createTransactionAndTransactionEntries(dispatchContext, transactionEntries, UtilDateTime.nowTimestamp(), LOAN_TRANSACTION_TYPE);
	        return result;
	    }

	    public  static boolean createTransactionAndTransactionEntries(DispatchContext dispatchContext, List<Map<String, Object>> transactionEntries, Timestamp transactionDate, String transactionType) {
	        Delegator delegator = dispatchContext.getDelegator();
	        Map<String, Object> acctgTransCtx = new HashMap<String, Object>();
	        acctgTransCtx.put("acctgTransTypeId", transactionType);
	        acctgTransCtx.put("transactionDate", transactionDate);
	        acctgTransCtx.put("glFiscalTypeId", "ACTUAL");
	        acctgTransCtx.put("description", "");
	        acctgTransCtx.put("voucherRef", "");
	        acctgTransCtx.put("userLogin", getSystemUserLogin(delegator));
	        acctgTransCtx.put("acctgTransEntries", transactionEntries);
	        try {
	            dispatchContext.getDispatcher().runSync("createAcctgTransAndEntries", acctgTransCtx);
	        } catch (GenericServiceException e) {

	        }
	        return true;
	    }

    public  static boolean createTransactionAndTransactionEntries(DispatchContext dispatchContext,Delegator delegator ,List<Map<String, Object>> transactionEntries, Timestamp transactionDate, String transactionType){
        Map<String, Object> acctgTransCtx = new HashMap<String, Object>();
        acctgTransCtx.put("acctgTransTypeId", transactionType);
        acctgTransCtx.put("transactionDate", transactionDate);
        acctgTransCtx.put("glFiscalTypeId", "ACTUAL");
        acctgTransCtx.put("description", "");
        acctgTransCtx.put("voucherRef", "");
        acctgTransCtx.put("userLogin", getSystemUserLogin(delegator));
        acctgTransCtx.put("acctgTransEntries", transactionEntries);
        try {
            dispatchContext.getDispatcher().runSync("createAcctgTransAndEntries", acctgTransCtx);
        } catch (GenericServiceException e) {
               return false;
        }
        return true;
    }


	public  static boolean createTransactionAndTransactionEntriesForSalary(DispatchContext dispatchContext,Delegator delegator ,List<Map<String, Object>> transactionEntries, Timestamp transactionDate, String transactionType,String salaryId){
		Map<String, Object> acctgTransCtx = new HashMap<String, Object>();
		acctgTransCtx.put("acctgTransTypeId", transactionType);
		acctgTransCtx.put("salaryId", salaryId);
		acctgTransCtx.put("transactionDate", transactionDate);
		acctgTransCtx.put("glFiscalTypeId", "ACTUAL");
		acctgTransCtx.put("description", "");
		acctgTransCtx.put("voucherRef", "");
		acctgTransCtx.put("userLogin", getSystemUserLogin(delegator));
		acctgTransCtx.put("acctgTransEntries", transactionEntries);
		try {
			dispatchContext.getDispatcher().runSync("createAcctgTransAndEntries", acctgTransCtx);
		} catch (GenericServiceException e) {
			return false;
		}
		return true;
	}

	    public static Map<String, Object> createTransactionEntry(Double amount, String glAccountId,String glAccountTypeId, String debitCreditFlag, String currency, String transactionDescription,String organizationPartyId,String groupId) {
	        Map<String, Object> transactionEntryMap = new HashMap<String, Object>();
	        if(UtilValidate.isNotEmpty(glAccountId))
	        	transactionEntryMap.put("glAccountId", glAccountId);
	        if(UtilValidate.isNotEmpty(glAccountTypeId))
	        	transactionEntryMap.put("glAccountTypeId", glAccountTypeId);
	        transactionEntryMap.put("origCurrencyUomId", currency);
	        transactionEntryMap.put("currencyUomId", currency);
	        transactionEntryMap.put("description", transactionDescription);
	        transactionEntryMap.put("debitCreditFlag", debitCreditFlag);
	        BigDecimal amountInBigDecimal = new BigDecimal(amount);
	        transactionEntryMap.put("origAmount", amountInBigDecimal);
	        transactionEntryMap.put("amount", amountInBigDecimal);
	        transactionEntryMap.put("organizationPartyId", organizationPartyId);
	        if(UtilValidate.isNotEmpty(groupId))
	        	transactionEntryMap.put("groupId", groupId);
	        return transactionEntryMap;
	    }

	    public static Map<String, Object> requisitionIssueTransactionEntry(DispatchContext dctx, Map<String, ? extends Object> context) throws GenericEntityException {
	    	Map<String, Object> results = ServiceUtil.returnSuccess();
	    	Delegator delegator = dctx.getDelegator();
	    	String facilityId = (String) context.get("facilityId");
            String productId  = (String) context.get("productId");
            String groupId = (String) context.get("groupId");
	    	String organizationPartyId = "Company";
	    	String currencyUomId = getCurrencyUomIdForCompany(delegator,"Company");
	    	List<Map<String, Object>> transactionEntries = new ArrayList<Map<String, Object>>();
            Map<String,Object> creditEntry = createTransactionEntry((Double)context.get("amount"), null,"INVENTORY_ACCOUNT", "C", currencyUomId, "",organizationPartyId,groupId);
            creditEntry.put("productId",productId);
	    	transactionEntries.add(creditEntry);
            Map<String,Object> debitEntry = createTransactionEntry((Double)context.get("amount"), null,"CONSUMABLE_EXPENSE", "D", currencyUomId, "",organizationPartyId,groupId);
            debitEntry.put("productId",productId);
	        transactionEntries.add(debitEntry);
	        boolean result = createTransactionAndTransactionEntries(dctx, transactionEntries, UtilDateTime.nowTimestamp(), REQUISITION_ISSUE);
	        results.put("result", result);
	        return results;
	    }

	    private static GenericValue getSystemUserLogin(Delegator delegator) {
	        List<GenericValue> userLoginGvs = null;
	        try {
	            userLoginGvs = delegator.findList("UserLogin", EntityCondition.makeCondition("userLoginId", EntityOperator.EQUALS, "system"), null, null, null, false);
	        } catch (GenericEntityException e) {
	            e.printStackTrace();
	        }
	        return UtilValidate.isNotEmpty(userLoginGvs) ? userLoginGvs.get(0) : null;
	    }

	    public static String getEmployeeGl(String employeeId) {
	        return EMPLOYEE_GL_PREFIX + employeeId;
	    }

	    public static String getCurrencyUomIdForCompany(Delegator delegator,String partyId) {
	        String currencyUomId = "INR";
	        try {
	            GenericValue partyAccountingPreferenceGv = getPartyAccPreference(delegator,partyId);
	            String baseCurrencyUomId = (partyAccountingPreferenceGv != null && UtilValidate.isNotEmpty(partyAccountingPreferenceGv.getString("baseCurrencyUomId"))) ? partyAccountingPreferenceGv.getString("baseCurrencyUomId") : currencyUomId;
	            currencyUomId = baseCurrencyUomId;
	        } catch (GenericEntityException e) {
	            throw new RuntimeException(e.getMessage());
	        }
	        return currencyUomId;
	    }

	    private static GenericValue getPartyAccPreference(Delegator delegator, String partyId) throws GenericEntityException{
	    	GenericValue partyAccountingPreferenceGv = delegator.findOne("PartyAcctgPreference", UtilMisc.toMap("partyId", partyId), false);
	    	return partyAccountingPreferenceGv;

	    }
}