package com.pastel.service;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.pastel.domain.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jdbc.core.OneToManyResultSetExtractor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * Author: Nthdimenzion
 */

@Component
public class OfbizTransactionFinder {

    private NamedParameterJdbcTemplate jdbcTemplate;

    private static final String queryJournalEntries = "SELECT att.acctg_trans_id as transactionId," +
            "       att.acctg_trans_type_id as accountingTransactionType," +
            "       att.transaction_date as transactionDate," +
            "       att.invoice_id AS invoiceId, " +
            "       atte.party_id as partyId," +
            "       TRIM(atte.gl_account_id) as glAccountId," +
            "       atte.gl_account_type_id as glAccountType," +
            "       atte.amount as homeAmount," +
            "       atte.currency_uom_id as homeCurrency," +
            "       IFNULL(atte.orig_amount,atte.amount) as amount," +
            "       IFNULL(atte.orig_currency_uom_id,atte.currency_uom_id) as currency," +
            "       atte.debit_credit_flag as transactionType, " +
            "       atte.acctg_trans_id, " +
            "       att.FIN_ACCOUNT_TRANS_ID finAccountTransId " +
            " FROM acctg_trans att" +
            " JOIN acctg_trans_entry atte ON att.acctg_trans_id = atte.acctg_trans_id" +
            " LEFT JOIN invoice invoice ON att.INVOICE_ID  = invoice.INVOICE_ID " +
            " LEFT JOIN payment payment ON att.PAYMENT_ID = payment.PAYMENT_ID " +
            " WHERE att.acctg_trans_type_id IN (:transactions) " +
            "       AND DATE(att.transaction_date) >= DATE(:startDate) " +
            "       AND DATE(att.transaction_date) < DATE(:endDate)" +
            "       AND att.IS_POSTED = 'Y' " +
            "       AND (invoice.STATUS_ID IS NULL OR invoice.STATUS_ID IN ('INVOICE_APPROVED','INVOICE_IN_PROCESS','INVOICE_PAID','INVOICE_READY')) " +
            "       AND (payment.STATUS_ID IS NULL OR payment.STATUS_ID NOT IN ('PMNT_VOID')) " +
            // "       AND att.acctg_trans_id = '21451'" +
            " ORDER BY att.acctg_trans_type_id," +
            "         att.acctg_trans_id;";


    private static final String queryCustomTimePeriod = "SELECT custom_time_period_id AS customTimePeriodId,from_date AS startDate, thru_date AS endDate " +
            "FROM custom_time_period " +
            "WHERE period_type_id = 'FISCAL_YEAR' " +
            "AND DATE(:endDate) >= from_date " +
            "AND DATE(:endDate) <= thru_date";

    private static final String queryMasterGlAccount = "SELECT gl_account_id as glAccount from gl_account";

    private static final String queryOfbizGlAccountSummary = "SELECT gl_account_id as glAccountId, posted_debits as debit, posted_credits as credit " +
            "from gl_account_history" +
            " where custom_time_period_id = :customTimePeriod";

    private static final String queryCheckIfOfbizTransactionIsFromBankAccount = "SELECT COUNT(*) " +
            "   FROM fin_account " +
            "   WHERE POST_TO_GL_ACCOUNT_ID IN (SELECT gl_account_id FROM acctg_trans_entry atte WHERE atte.acctg_trans_id = :transactionId) " +
            "   AND fin_account_type_id = 'BANK_ACCOUNT'";

    private static final String queryCheckIfOfbizTransactionIsFromCashAccount = "SELECT COUNT(*) FROM gl_account " +
            " WHERE gl_account_class_id = 'CURRENT_ASSET' " +
            " AND LOWER(account_name) LIKE '%cash%' " +
            " AND gl_account_id IN (SELECT gl_account_id FROM acctg_trans_entry atte WHERE atte.acctg_trans_id = :transactionId);";

    private static final String queryAllCashAccounts = "SELECT gl_account_id " +
            " FROM gl_account " +
            " WHERE gl_account_class_id = 'CURRENT_ASSET'" +
            " AND LOWER(account_name) LIKE '%cash%' ";

    private static final String queryAllBankAccounts = "SELECT post_to_gl_account_id " +
            " FROM fin_account " +
            " WHERE FIN_ACCOUNT_TYPE_ID = 'BANK_ACCOUNT'";

    private static final String queryGetCustomTimePeriodIdOfPreviousFiscalYear = "SELECT custom_time_period_id FROM custom_time_period " +
            "                                           WHERE DATE(:dateOfTenDaysBeforeStartDate) >= from_date " +
            "                                           AND DATE(:dateOfTenDaysBeforeStartDate) < thru_date;";

    private static final String queryOfbizGlAccountSummaryForOpeningBalances = "SELECT gl_account_id AS glAccountId, posted_debits AS amount FROM gl_account_history " +
            "WHERE custom_time_period_id = :customTimePeriod AND posted_debits > '0.00'" +
            "UNION ALL " +
            "SELECT gl_account_id AS glAccountId, posted_credits*-1 AS amount FROM gl_account_history " +
            "WHERE custom_time_period_id = :customTimePeriod AND posted_credits > '0.00'";

    private static final String journalDetailsQuery = "SELECT PR.`PARTY_ID` customerId ,A.`DEBIT_CREDIT_FLAG` transactionType , SUM(Amount) amount , b.`TRANSACTION_DATE` transactionDate FROM acctg_trans_entry A JOIN acctg_trans B " +
            "ON A.`ACCTG_TRANS_ID`=B.`ACCTG_TRANS_ID` " +
            "JOIN PARTY_ROLE PR ON PR.`PARTY_ID`=A.`PARTY_ID` " +
            "WHERE b.`TRANSACTION_DATE` < DATE(:startDate) AND PR.`ROLE_TYPE_ID`=:roleTypeId ";

    private static final String getTheCustomerJournalDetails = journalDetailsQuery +" GROUP BY PR.`PARTY_ID`,A.`DEBIT_CREDIT_FLAG` ORDER BY PR.`PARTY_ID` ";

    private static final String getTheSupplierJournalDetails = journalDetailsQuery +" AND A.role_type_id=:roleTypeId GROUP BY PR.`PARTY_ID`,A.`DEBIT_CREDIT_FLAG` ORDER BY PR.`PARTY_ID` ";


    @Autowired
    public OfbizTransactionFinder(DataSource dataSource) {
        jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public Map<OfbizTransactionType, Collection<JournalEntry>> fetchOfbizTransactions(Date startDate,Date endDate) {
        //System.out.println("Before fetching");
        SqlParameterSource parameterSource = new MapSqlParameterSource("transactions", OfbizTransactionType.Transactions).addValue("startDate",startDate).addValue("endDate",endDate);
        final Collection<JournalEntry> journalEntries = jdbcTemplate.query(queryJournalEntries,parameterSource, new JournalEntryExtractor());
        //System.out.println("After fetching");
        return groupByOfbizTransactionType(journalEntries);
    }

    public Collection<JournalEntry> fetchOfbizPayrollTransactions(Date startDate,Date endDate) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("transactions", OfbizTransactionType.PayrollTransactions).addValue("startDate",startDate).addValue("endDate",endDate);
        //System.out.println("startDate ===>"+startDate);
        //System.out.println("endDate ===>"+endDate);
        final Collection<JournalEntry> journalEntries = jdbcTemplate.query(queryJournalEntries,parameterSource, new JournalEntryExtractor());
        //System.out.println("journalEntries==>"+journalEntries);
        return journalEntries;
    }

    public List<String> getMasterGlAccounts() {
        return jdbcTemplate.queryForList(queryMasterGlAccount, Maps.<String, Object>newHashMap(), String.class);
    }



    public Map<String,Object> getCustomPeriodDetails(Date endDate){
        //System.out.println(endDate);
        return jdbcTemplate.queryForMap(queryCustomTimePeriod, Collections.singletonMap("endDate", endDate));
    }

    private Map<OfbizTransactionType, Collection<JournalEntry>> groupByOfbizTransactionType(Collection<JournalEntry> journalEntries) {
        Multimap<OfbizTransactionType, JournalEntry> result = HashMultimap.create();
        for (JournalEntry journalEntry : journalEntries) {
            result.put(journalEntry.transactionType, journalEntry);
        }
        return result.asMap();
    }

    public Collection<PaymentJournalEntry> fetchOfbizPaymentTransactions(Date startDate,Date endDate) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("transactions", OfbizTransactionType.PaymentTransactions).addValue("startDate",startDate).addValue("endDate",endDate);
        final Collection<PaymentJournalEntry> paymentJournalEntries = Lists.newArrayList();
        final Collection<JournalEntry> journalEntries = jdbcTemplate.query(queryJournalEntries,parameterSource, new JournalEntryExtractor());
        for (JournalEntry journalEntry : journalEntries) {
            PaymentJournalEntry paymentJournalEntry = new PaymentJournalEntry(journalEntry);
            final boolean isAccountingTransactionInvolvingCashAccount = isAccountingTransactionInvolvingCashAccount(journalEntry.transactionId);
            final boolean isFinancialTransactionFromBankAccount = isFinancialTransactionFromBankAccount(journalEntry.transactionId);
            if(isAccountingTransactionInvolvingCashAccount){
                paymentJournalEntry.setCashAccount(isAccountingTransactionInvolvingCashAccount);
            }else if(isFinancialTransactionFromBankAccount){
                // @TODO we could throw a exception here if the financial transaction is not from a bank account
                /*if(!isFinancialTransactionFromBankAccount){
                    System.out.println("There is something wrong here a financial transaction is not referring to a bank account " + paymentJournalEntry.getJournalEntry().transactionId);
                }*/
                paymentJournalEntry.setFinancialTransactionFromBankAccount(isFinancialTransactionFromBankAccount);

            }
            paymentJournalEntries.add(paymentJournalEntry);
        }
        return paymentJournalEntries;
    }

    public List<Map<String, Object>> getOfbizGlAccountSummaries(String customTimePeriod) {
        List<Map<String, Object>> mapOfOfbizGlAccountSummary = jdbcTemplate.queryForList(queryOfbizGlAccountSummary, Collections.singletonMap("customTimePeriod", customTimePeriod));
        List<Map<String, Object>> refinedMapOfOfbizGlAccountSummary = Lists.newArrayList();
        for(Map<String, Object> OfbizGlAccount : mapOfOfbizGlAccountSummary){
            Map<String, Object> refinedData = Maps.newHashMap();
            String glAccountId = (String) OfbizGlAccount.get("glAccountId");
            BigDecimal debit = (BigDecimal) OfbizGlAccount.get("debit");
            BigDecimal credit = (BigDecimal) OfbizGlAccount.get("credit");
            refinedData.put("glAccountId", glAccountId.trim());
            refinedData.put("debit", debit);
            refinedData.put("credit", credit);
            refinedMapOfOfbizGlAccountSummary.add(refinedData);
        }
        return refinedMapOfOfbizGlAccountSummary;
    }

    public List<Map<String, Object>> getOfbizGlAccountSummariesForOpeningBalances(String customTimePeriod) {
        List<Map<String, Object>> mapOfOfbizGlAccountSummary = jdbcTemplate.queryForList(queryOfbizGlAccountSummaryForOpeningBalances, Collections.singletonMap("customTimePeriod", customTimePeriod));
        List<Map<String, Object>> refinedMapOfOfbizGlAccountSummary = Lists.newArrayList();
        for(Map<String, Object> OfbizGlAccount : mapOfOfbizGlAccountSummary){
            Map<String, Object> refinedData = Maps.newHashMap();
            String glAccountId = (String) OfbizGlAccount.get("glAccountId");
            glAccountId = replaceCustomerAndSupplierControlAccountWithSuspenseAccount(glAccountId);
            BigDecimal amount = (BigDecimal) OfbizGlAccount.get("amount");
            refinedData.put("glAccountId", glAccountId.trim());
            refinedData.put("amount", amount);
            refinedMapOfOfbizGlAccountSummary.add(refinedData);
        }
        return refinedMapOfOfbizGlAccountSummary;
    }

    private String replaceCustomerAndSupplierControlAccountWithSuspenseAccount(String glAccountId) {
        if(glAccountId.trim().equals(IPastelTransaction.CUSTOMER_CONTROL_ACCOUNT))
            glAccountId = IPastelTransaction.CUSTOMER_SUSPENSE_ACCOUNT;
        if(glAccountId.trim().equals(IPastelTransaction.SUPPLIER_CONTROL_ACCOUNT))
            glAccountId = IPastelTransaction.SUPPLIER_SUSPENSE_ACCOUNT;
        return glAccountId;
    }


    public boolean isFinancialTransactionFromBankAccount(String transactionId){
        Integer count = jdbcTemplate.queryForObject(queryCheckIfOfbizTransactionIsFromBankAccount,Collections.singletonMap("transactionId",transactionId),Integer.class);
        return count > 0;
    }

    public boolean isAccountingTransactionInvolvingCashAccount(String transactionId){
        Integer count = jdbcTemplate.queryForObject(queryCheckIfOfbizTransactionIsFromCashAccount,Collections.singletonMap("transactionId",transactionId),Integer.class);
        return count > 0;
    }

    public Collection<JournalEntry> fetchOfbizPaymentAppliedTransactions(Date startDate, Date endDate) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("transactions",  Lists.newArrayList("PAYMENT_APPL")).addValue("startDate",startDate).addValue("endDate",endDate);
        final Collection<JournalEntry> journalEntries = jdbcTemplate.query(queryJournalEntries,parameterSource, new JournalEntryExtractor());
        final Collection<JournalEntry> updatedJournalEntries =  assignOfbizTransactionType(journalEntries);
        return updatedJournalEntries;
    }

    private Collection<JournalEntry> assignOfbizTransactionType(Collection<JournalEntry> journalEntries) {
        Collection<JournalEntry> updatedJournalEntries = Lists.newArrayList();
        for (JournalEntry journalEntry : journalEntries) {
            if(!CollectionUtils.isEmpty(journalEntry.getJournalEntryItemsForGroup(JournalEntry.TransactionGroup.ACCOUNTS_PAYABLE))){
                journalEntry.transactionType = OfbizTransactionType.PAYMENT_APPL_SUPP;
            }else if (!CollectionUtils.isEmpty(journalEntry.getJournalEntryItemsForGroup(JournalEntry.TransactionGroup.ACCOUNTS_RECEIVABLE))){
                journalEntry.transactionType = OfbizTransactionType.PAYMENT_APPL_CUST;
            }
            updatedJournalEntries.add(journalEntry);
        }
        return updatedJournalEntries;
    }

    public int getCustomTimePeriodIdOfPreviousFiscalYear(Date dateOfTenDaysBeforeStartDate) {
        return jdbcTemplate.queryForObject(queryGetCustomTimePeriodIdOfPreviousFiscalYear,Collections.singletonMap("dateOfTenDaysBeforeStartDate",dateOfTenDaysBeforeStartDate),Integer.class);
        // return 0;
    }

    public List<CustomerJournal> getTheJournalDetailsForGivenRoleType(Date startDate, String roleType){
        SqlParameterSource parameterSource = new MapSqlParameterSource().addValue("startDate", startDate).addValue("roleTypeId",roleType);
        List<CustomerJournal> listOfCustomerJournal=null;
        if (roleType.equals("BILL_TO_CUSTOMER")){
            listOfCustomerJournal =  jdbcTemplate.query(getTheCustomerJournalDetails, parameterSource, new BeanPropertyRowMapper<>(CustomerJournal.class));
        }
        else if (roleType.equals("BILL_FROM_VENDOR")){
            listOfCustomerJournal =  jdbcTemplate.query(getTheSupplierJournalDetails, parameterSource, new BeanPropertyRowMapper<>(CustomerJournal.class));
        }
        return listOfCustomerJournal;
    }


    private class JournalEntryHeaderMapper implements RowMapper<JournalEntry> {
        @Override
        public JournalEntry mapRow(ResultSet rs, int rowNum) throws SQLException {
            JournalEntry journalEntry = new JournalEntry(rs.getString("transactionId"), OfbizTransactionType.getEnum(rs.getString("accountingTransactionType")), rs.getDate("transactionDate"),rs.getString("invoiceId"));
            return journalEntry;
        }
    }

    private class JournalEntryItemMapper implements RowMapper<JournalEntryItem> {

        @Override
        public JournalEntryItem mapRow(ResultSet rs, int rowNum) throws SQLException {

            JournalEntryItem journalEntryItem = new JournalEntryItem(getFurnishedParytId(rs.getString("partyId")), rs.getString("glAccountId"), rs.getString("glAccountType"), rs.getBigDecimal("homeAmount"),
                    rs.getBigDecimal("amount"), JournalEntry.TransactionType.getEnum(rs.getString("transactionType")));
            journalEntryItem.setFinTransId(rs.getString("finAccountTransId"));
            return journalEntryItem;
        }
    }

    private class JournalEntryExtractor extends OneToManyResultSetExtractor<JournalEntry, JournalEntryItem, String> {

        public JournalEntryExtractor() {
            super(new JournalEntryHeaderMapper(), new JournalEntryItemMapper());
        }

        @Override
        protected String mapPrimaryKey(ResultSet rs) throws SQLException {
            return rs.getString("transactionId");
        }

        @Override
        protected String mapForeignKey(ResultSet rs) throws SQLException {
            return rs.getString("atte.acctg_trans_id");
        }

        @Override
        protected void addChild(JournalEntry journalEntry, JournalEntryItem journalEntryItem) {
            journalEntry.addJournalEntryItem(journalEntryItem);

        }
    }

    public List<String> fetchAllCashAndBankAccounts(){
        List<String> cashAccounts = jdbcTemplate.queryForList(queryAllCashAccounts, Collections.<String, Object>emptyMap(),String.class);
        List<String> bankAccounts = jdbcTemplate.queryForList(queryAllBankAccounts, Collections.<String, Object>emptyMap(),String.class);
        List<String> allCashAndBankAccounts = Lists.newArrayList(cashAccounts);
        allCashAndBankAccounts.addAll(bankAccounts);
        return allCashAndBankAccounts;
    }

    public String getFurnishedParytId(String partyId){
        if(partyId == null)
            return partyId;
        if (partyId.matches("-?\\d+")) {
            if(partyId.trim().length() == 1)
                partyId =  "00"+partyId;
            if(partyId.trim().length() == 2)
                partyId =  "0"+partyId;
            return partyId;
        }
        else
            return partyId.trim();
    }
}
