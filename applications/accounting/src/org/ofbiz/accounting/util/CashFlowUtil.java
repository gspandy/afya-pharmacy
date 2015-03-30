package org.ofbiz.accounting.util;

import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.jdbc.SQLProcessor;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

/**
 * @author: Samir
 * @since 1.0
 */
public class CashFlowUtil {


    private static String OPENING_BALANCE_OF_BANK_ACCOUNT_QUERY = " SELECT * FROM ACCTG_TRANS_ENTRY E \n" +
            "\t  JOIN ACCTG_TRANS T ON E.ACCTG_TRANS_ID = T.ACCTG_TRANS_ID\n" +
            "\t  JOIN FIN_ACCOUNT FA ON FA.POST_TO_GL_ACCOUNT_ID=E.GL_ACCOUNT_ID\n" +
            "\t  WHERE T.IS_POSTED='Y' AND FA.fin_account_type_id='BANK_ACCOUNT'";

    private static String RECEIPTS_FROM_CUSTOMER_QUERY = "SELECT SUM(E.AMOUNT) AS totalAmount FROM ACCTG_TRANS_ENTRY E \n" +
            "\t  JOIN ACCTG_TRANS T ON E.ACCTG_TRANS_ID = T.ACCTG_TRANS_ID\n" +
            "\t  JOIN PAYMENT P ON T.PAYMENT_ID= P.PAYMENT_ID\n" +
            "\t  WHERE T.IS_POSTED='Y' AND T.ACCTG_TRANS_TYPE_ID='INCOMING_PAYMENT' AND E.DEBIT_CREDIT_FLAG='C' AND P.PAYMENT_TYPE_ID='CUSTOMER_PAYMENT'";

    private static String INCOME_FROM_LONG_TERM_ASSET_QUERY = "SELECT G.PARENT_GL_ACCOUNT_ID,G.GL_ACCOUNT_ID,G.ACCOUNT_NAME,SUM(E.AMOUNT) AS amount FROM ACCTG_TRANS_ENTRY E \n" +
            "\t  JOIN ACCTG_TRANS T ON E.ACCTG_TRANS_ID = T.ACCTG_TRANS_ID\n" +
            "\t  LEFT JOIN GL_ACCOUNT G ON E.GL_ACCOUNT_ID = G.GL_ACCOUNT_ID\n" +
            "\t  WHERE T.IS_POSTED='Y' AND G.GL_ACCOUNT_CLASS_ID='LONGTERM_ASSET' AND E.DEBIT_CREDIT_FLAG='C' \n" +
            "\t  GROUP BY G.GL_ACCOUNT_ID, G.PARENT_GL_ACCOUNT_ID";

    private static String OTHER_RECEIPT_INFLOW_AMOUNT_QUERY =
            "SELECT G.PARENT_GL_ACCOUNT_ID,G.GL_ACCOUNT_ID,G.ACCOUNT_NAME,SUM(e.AMOUNT) AS amount FROM ACCTG_TRANS_ENTRY E \n" +
                    "\t  JOIN ACCTG_TRANS T ON E.ACCTG_TRANS_ID = T.ACCTG_TRANS_ID\n" +
                    "\t  LEFT JOIN GL_ACCOUNT G ON E.GL_ACCOUNT_ID = G.GL_ACCOUNT_ID\n" +
                    "\t  WHERE T.IS_POSTED='Y' AND T.ACCTG_TRANS_TYPE_ID='INCOMING_PAYMENT' AND E.DEBIT_CREDIT_FLAG='C' AND R.ROLE_TYPE_ID !='CUSTOMER' \n" +
                    "\t  GROUP BY G.GL_ACCOUNT_ID, G.PARENT_GL_ACCOUNT_ID";


    private static String INFLOW_FROM_INDIRECT_PRODUCTION_COST_QUERY = "SELECT G.PARENT_GL_ACCOUNT_ID,G.GL_ACCOUNT_ID,G.ACCOUNT_NAME,SUM(e.AMOUNT) AS amount FROM ACCTG_TRANS_ENTRY E \n" +
            "\t  JOIN ACCTG_TRANS T ON E.ACCTG_TRANS_ID = T.ACCTG_TRANS_ID\n" +
            "\t  JOIN GL_ACCOUNT G ON E.GL_ACCOUNT_ID = G.GL_ACCOUNT_ID\n" +
            "\t  WHERE T.IS_POSTED='Y' AND G.GL_ACCOUNT_CLASS_ID='EXPENSE' AND E.DEBIT_CREDIT_FLAG='C' \n" +
            "\t  AND G.PARENT_GL_ACCOUNT_ID!='20000000_1' GROUP BY G.GL_ACCOUNT_ID, G.PARENT_GL_ACCOUNT_ID";

    private static String OUTFLOW_FROM_INDIRECT_PRODUCTION_COST_QUERY = "SELECT G.PARENT_GL_ACCOUNT_ID,G.GL_ACCOUNT_ID,G.ACCOUNT_NAME,SUM(E.AMOUNT) AS amount FROM ACCTG_TRANS_ENTRY E \n" +
            "\t  JOIN ACCTG_TRANS T ON E.ACCTG_TRANS_ID = T.ACCTG_TRANS_ID\n" +
            "\t  JOIN GL_ACCOUNT G ON E.GL_ACCOUNT_ID = G.GL_ACCOUNT_ID\n" +
            "\t  WHERE T.IS_POSTED='Y' AND G.GL_ACCOUNT_CLASS_ID='EXPENSE' AND E.DEBIT_CREDIT_FLAG='D' \n" +
            "\t  AND G.PARENT_GL_ACCOUNT_ID !='20000000_1' GROUP BY G.GL_ACCOUNT_ID, G.PARENT_GL_ACCOUNT_ID";

    private static String SUPPLIER_PAID_AMOUNT_QUERY = "SELECT SUM(E.AMOUNT) AS amount FROM ACCTG_TRANS_ENTRY E \n" +
            "  JOIN ACCTG_TRANS T ON E.ACCTG_TRANS_ID = T.ACCTG_TRANS_ID\n" +
            "  JOIN PAYMENT P ON T.PAYMENT_ID= P.PAYMENT_ID\n" +
            "  WHERE T.IS_POSTED='Y' AND T.ACCTG_TRANS_TYPE_ID='OUTGOING_PAYMENT' AND E.DEBIT_CREDIT_FLAG='C' AND P.`PAYMENT_TYPE_ID`='VENDOR_PAYMENT'";


    private static String CASH_OR_BANK_RECEIVED_AMOUNT_QUERY = "SELECT SUM(E.`AMOUNT`) AS amount FROM ACCTG_TRANS_ENTRY E \n" +
            "\t  JOIN ACCTG_TRANS T ON E.`ACCTG_TRANS_ID` = T.ACCTG_TRANS_ID\n" +
            "\t  WHERE T.IS_POSTED='Y' AND  E.`DEBIT_CREDIT_FLAG`='D' AND E.GL_ACCOUNT_ID IN ('8400000', '8410000','8401000','8402000')";

    private static String CASH_OR_BANK_PAID_AMOUNT_QUERY = "SELECT SUM(E.`AMOUNT`) AS amount FROM ACCTG_TRANS_ENTRY E \n" +
            "\t  JOIN ACCTG_TRANS T ON E.`ACCTG_TRANS_ID` = T.ACCTG_TRANS_ID\n" +
            "\t  WHERE T.IS_POSTED='Y' AND E.`DEBIT_CREDIT_FLAG`='C' AND E.GL_ACCOUNT_ID IN ('8400000','8410000','8440000')";

    public static BigDecimal getOpeningBalanceOfBankAccountForGivenPeriod(Delegator delegator, String customTimePeriodId, String organizationPartyId) throws GenericEntityException, SQLException {
        return UtilAccounting.getOpeningBalanceOfCashAndBank(delegator,customTimePeriodId,organizationPartyId);
    }

    public static BigDecimal getCustomerReceiptAmountForGivenPeriod(Delegator delegator, String customTimePeriodId, String organizationPartyId) throws GenericEntityException, SQLException {
        BigDecimal customerReceiptAmount = BigDecimal.ZERO;
        String TIME_CONDITION = getTimeQueryCondition(delegator, customTimePeriodId);
        RECEIPTS_FROM_CUSTOMER_QUERY = RECEIPTS_FROM_CUSTOMER_QUERY + TIME_CONDITION;
        ResultSet resultSet = execute(RECEIPTS_FROM_CUSTOMER_QUERY, delegator);
        while (resultSet.next()) {
            customerReceiptAmount = resultSet.getBigDecimal("totalAmount") != null ? resultSet.getBigDecimal("totalAmount") : BigDecimal.ZERO;
        }
        return customerReceiptAmount;
    }

    public static List<Map<String, Object>> getInflowFromAsset(Delegator delegator, String customTimePeriodId, String organizationPartyId) throws GenericEntityException, SQLException {
        List<Map<String, Object>> assetInflows = new ArrayList<Map<String, Object>>();
        String TIME_CONDITION = getTimeQueryCondition(delegator, customTimePeriodId);
        INCOME_FROM_LONG_TERM_ASSET_QUERY = INCOME_FROM_LONG_TERM_ASSET_QUERY + TIME_CONDITION;
        ResultSet resultSet = execute(INCOME_FROM_LONG_TERM_ASSET_QUERY, delegator);
        while (resultSet.next()) {
            BigDecimal amount = resultSet.getBigDecimal("amount") != null ? resultSet.getBigDecimal("amount") : BigDecimal.ZERO;
            String accountName = resultSet.getString("ACCOUNT_NAME");
            Map<String, Object> glMap = new HashMap<String, Object>();
            glMap.put("accountName", accountName);
            glMap.put("amount", amount);
            assetInflows.add(glMap);
        }
        return assetInflows;
    }

    public static List<Map<String, Object>> getOtherInflowAmountForAGivenPeriod(Delegator delegator, String customTimePeriodId, String organizationPartyId) throws GenericEntityException, SQLException {
        List<Map<String, Object>> otherInflows = new ArrayList<Map<String, Object>>();
        String TIME_CONDITION = getTimeQueryCondition(delegator, customTimePeriodId);
        OTHER_RECEIPT_INFLOW_AMOUNT_QUERY = OTHER_RECEIPT_INFLOW_AMOUNT_QUERY + TIME_CONDITION;
        ResultSet otherInflowResultSet = execute(OTHER_RECEIPT_INFLOW_AMOUNT_QUERY, delegator);
        while (otherInflowResultSet.next()) {
            BigDecimal amount = otherInflowResultSet.getBigDecimal("amount") != null ? otherInflowResultSet.getBigDecimal("amount") : BigDecimal.ZERO;
            String accountName = otherInflowResultSet.getString("ACCOUNT_NAME");
            Map<String, Object> glMap = new HashMap<String, Object>();
            glMap.put("accountName", accountName);
            glMap.put("amount", amount);
            otherInflows.add(glMap);
        }
        return otherInflows;
    }


    public static List<Map<String, Object>> getInflowFromIndirectProduction(Delegator delegator, String customTimePeriodId, String organizationPartyId) throws GenericEntityException, SQLException {
        List<Map<String, Object>> indirectProductionInflows = new ArrayList<Map<String, Object>>();
        String TIME_CONDITION = getTimeQueryCondition(delegator, customTimePeriodId);
        INFLOW_FROM_INDIRECT_PRODUCTION_COST_QUERY = INFLOW_FROM_INDIRECT_PRODUCTION_COST_QUERY + TIME_CONDITION;
        ResultSet resultSet = execute(INFLOW_FROM_INDIRECT_PRODUCTION_COST_QUERY, delegator);
        while (resultSet.next()) {
            BigDecimal amount = resultSet.getBigDecimal("amount") != null ? resultSet.getBigDecimal("amount") : BigDecimal.ZERO;
            String accountName = resultSet.getString("ACCOUNT_NAME");
            Map<String, Object> glMap = new HashMap<String, Object>();
            glMap.put("accountName", accountName);
            glMap.put("amount", amount);
            indirectProductionInflows.add(glMap);
        }
        return indirectProductionInflows;
    }

    public static List<Map<String, Object>> getOutflowFromIndirectProduction(Delegator delegator, String customTimePeriodId, String organizationPartyId) throws GenericEntityException, SQLException {
        List<Map<String, Object>> indirectProductionInflows = new ArrayList<Map<String, Object>>();
        String TIME_CONDITION = getTimeQueryCondition(delegator, customTimePeriodId);
        OUTFLOW_FROM_INDIRECT_PRODUCTION_COST_QUERY = OUTFLOW_FROM_INDIRECT_PRODUCTION_COST_QUERY + TIME_CONDITION;
        ResultSet resultSet = execute(OUTFLOW_FROM_INDIRECT_PRODUCTION_COST_QUERY, delegator);
        while (resultSet.next()) {
            BigDecimal amount = resultSet.getBigDecimal("amount") != null ? resultSet.getBigDecimal("amount") : BigDecimal.ZERO;
            String accountName = resultSet.getString("ACCOUNT_NAME");
            Map<String, Object> glMap = new HashMap<String, Object>();
            glMap.put("accountName", accountName);
            glMap.put("amount", amount);
            indirectProductionInflows.add(glMap);
        }
        return indirectProductionInflows;
    }

    public static BigDecimal getSupplierPaidAmountForGivenPeriod(Delegator delegator, String customTimePeriodId, String organizationPartyId) throws GenericEntityException, SQLException {
        BigDecimal supplierPaidAmount = BigDecimal.ZERO;
        String TIME_CONDITION = getTimeQueryCondition(delegator, customTimePeriodId);
        SUPPLIER_PAID_AMOUNT_QUERY = SUPPLIER_PAID_AMOUNT_QUERY + TIME_CONDITION;
        ResultSet resultSet = execute(SUPPLIER_PAID_AMOUNT_QUERY, delegator);
        while (resultSet.next()) {
            supplierPaidAmount = resultSet.getBigDecimal("amount") != null ? resultSet.getBigDecimal("amount") : BigDecimal.ZERO;
        }
        return supplierPaidAmount;
    }

    public static BigDecimal getInflowInAGivenPeriod(Delegator delegator, String customTimePeriodId, String organizationPartyId) throws GenericEntityException, SQLException {
        BigDecimal cashReceivedAmount = BigDecimal.ZERO;
        String TIME_CONDITION = getTimeQueryCondition(delegator, customTimePeriodId);
        CASH_OR_BANK_RECEIVED_AMOUNT_QUERY = CASH_OR_BANK_RECEIVED_AMOUNT_QUERY + TIME_CONDITION;
        ResultSet resultSet = execute(CASH_OR_BANK_RECEIVED_AMOUNT_QUERY, delegator);
        while (resultSet.next()) {
            cashReceivedAmount = resultSet.getBigDecimal("amount") != null ? resultSet.getBigDecimal("amount") : BigDecimal.ZERO;
        }
        return cashReceivedAmount;
    }

    public static BigDecimal getOutflowInAGivenPeriod(Delegator delegator,String customTimePeriodId,String organizationPartyId) throws GenericEntityException, SQLException {
        BigDecimal cashPaidAmount = BigDecimal.ZERO;
        String TIME_CONDITION = getTimeQueryCondition(delegator, customTimePeriodId);
        CASH_OR_BANK_PAID_AMOUNT_QUERY = CASH_OR_BANK_PAID_AMOUNT_QUERY + TIME_CONDITION;
        ResultSet resultSet = execute(CASH_OR_BANK_PAID_AMOUNT_QUERY, delegator);
        while (resultSet.next()) {
            cashPaidAmount = resultSet.getBigDecimal("amount") != null ? resultSet.getBigDecimal("amount") : BigDecimal.ZERO;
        }
        return cashPaidAmount;
    }

    private static String getTimeQueryCondition(Delegator delegator, String customTimePeriodId) throws GenericEntityException {
        Map<String, Timestamp> periodMap = UtilAccounting.getReportingPeriod(delegator, customTimePeriodId);
        String TIME_CONDITION = "  AND T.TRANSACTION_DATE>=" + "\'" + periodMap.get("fromDate") + "\'" + " AND " + "T.TRANSACTION_DATE<=" + "\'" + periodMap.get("thruDate") + "\'";
        return TIME_CONDITION;
    }

    public static ResultSet execute(String query, Delegator delegator) throws GenericEntityException {
        SQLProcessor sqlProc = new SQLProcessor(delegator.getGroupHelperInfo("org.ofbiz"));
        sqlProc.prepareStatement(query);
        return sqlProc.executeQuery();
    }
}
