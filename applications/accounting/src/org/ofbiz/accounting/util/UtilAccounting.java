/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *******************************************************************************/

package org.ofbiz.accounting.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;

import javolution.util.FastList;

import org.apache.commons.io.IOUtils;
import org.ofbiz.accounting.AccountingException;
import org.ofbiz.accounting.invoice.InvoiceWorker;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.EntityComparisonOperator;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.jdbc.SQLProcessor;
import org.ofbiz.entity.serialize.SerializeException;
import org.ofbiz.entity.serialize.XmlSerializer;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.party.party.PartyWorker;
import org.ofbiz.service.*;
import org.xml.sax.SAXException;

public class UtilAccounting {

    public static String module = UtilAccounting.class.getName();

    /**
     * Get the GL Account for a product or the default account type based on input. This replaces the simple-method
     * service getProductOrgGlAccount. First it will look in ProductGlAccount using the primary keys productId and
     * productGlAccountTypeId. If none is found, it will look up GlAccountTypeDefault to find the default account for
     * organizationPartyId with type glAccountTypeId.
     *
     * @param productId           When searching for ProductGlAccounts, specify the productId
     * @param glAccountTypeId     The default glAccountTypeId to look for if no ProductGlAccount is found
     * @param organizationPartyId The organization party of the default account
     * @return The account ID (glAccountId) found
     * @throws AccountingException When the no accounts found or an entity exception occurs
     */
    public static String getProductOrgGlAccountId(String productId, String glAccountTypeId, String organizationPartyId,
                                                  Delegator delegator) throws AccountingException {

        GenericValue account = null;
        try {
            // first try to find the account in ProductGlAccount
            account =
                    delegator.findByPrimaryKeyCache("ProductGlAccount", UtilMisc.toMap("productId", productId,
                            "glAccountTypeId", glAccountTypeId, "organizationPartyId", organizationPartyId));
        } catch (GenericEntityException e) {
            throw new AccountingException("Failed to find a ProductGLAccount for productId [" + productId
                    + "], organization [" + organizationPartyId + "], and productGlAccountTypeId [" + glAccountTypeId
                    + "].", e);
        }

        // otherwise try the default accounts
        if (account == null) {
            try {
                account =
                        delegator.findByPrimaryKeyCache("GlAccountTypeDefault",
                                UtilMisc.toMap("glAccountTypeId", glAccountTypeId, "organizationPartyId", organizationPartyId));
            } catch (GenericEntityException e) {
                throw new AccountingException("Failed to find a GlAccountTypeDefault for glAccountTypeId ["
                        + glAccountTypeId + "] and organizationPartyId [" + organizationPartyId + "].", e);
            }
        }

        // if no results yet, serious problem
        if (account == null) {
            throw new AccountingException(
                    "Failed to find any accounts for  productId ["
                            + productId
                            + "], organization ["
                            + organizationPartyId
                            + "], and productGlAccountTypeId ["
                            + glAccountTypeId
                            + "] or any accounts in GlAccountTypeDefault for glAccountTypeId ["
                            + glAccountTypeId
                            + "] and organizationPartyId ["
                            + organizationPartyId
                            + "]. Please check your data to make sure that at least a GlAccountTypeDefault is defined for this account type and organization.");
        }

        // otherwise return the glAccountId
        return account.getString("glAccountId");
    }

    /**
     * As above, but explicitly looking for default account for given type and organization
     *
     * @param glAccountTypeId     The type of account
     * @param organizationPartyId The organization of the account
     * @return The default account ID (glAccountId) for this type
     * @throws AccountingException When the default is not configured
     */
    public static String getDefaultAccountId(String glAccountTypeId, String organizationPartyId, Delegator delegator)
            throws AccountingException {
        return getProductOrgGlAccountId(null, glAccountTypeId, organizationPartyId, delegator);
    }

    /**
     * Little method to figure out the net or ending balance of a GlAccountHistory or GlAccountAndHistory value, based
     * on what kind of account (DEBIT or CREDIT) it is
     *
     * @param account - GlAccountHistory or GlAccountAndHistory value
     * @return balance - a BigDecimal
     */
    public static BigDecimal getNetBalance(GenericValue account, String debugModule) {
        try {
            return getNetBalance(account);
        } catch (GenericEntityException ex) {
            Debug.logError(ex.getMessage(), debugModule);
            return null;
        }
    }

    public static BigDecimal getNetBalance(GenericValue account) throws GenericEntityException {
        GenericValue glAccount = account.getRelatedOne("GlAccount");
        BigDecimal balance = BigDecimal.ZERO;
        if (isDebitAccount(glAccount)) {
            balance = account.getBigDecimal("postedDebits").subtract(account.getBigDecimal("postedCredits"));
        } else if (isCreditAccount(glAccount)) {
            balance = account.getBigDecimal("postedCredits").subtract(account.getBigDecimal("postedDebits"));
        }
        return balance;
    }

    public static List getDescendantGlAccountClassIds(GenericValue glAccountClass) throws GenericEntityException {
        List glAccountClassIds = FastList.newInstance();
        getGlAccountClassChildren(glAccountClass, glAccountClassIds);
        return glAccountClassIds;
    }

    private static void getGlAccountClassChildren(GenericValue glAccountClass, List glAccountClassIds)
            throws GenericEntityException {
        glAccountClassIds.add(glAccountClass.getString("glAccountClassId"));
        List glAccountClassChildren = glAccountClass.getRelatedCache("ChildGlAccountClass");
        Iterator glAccountClassChildrenIt = glAccountClassChildren.iterator();
        while (glAccountClassChildrenIt.hasNext()) {
            GenericValue glAccountClassChild = (GenericValue) glAccountClassChildrenIt.next();
            getGlAccountClassChildren(glAccountClassChild, glAccountClassIds);
        }
    }

    /**
     * Recurses up payment type tree via parentTypeId to see if input payment type ID is in tree.
     */
    private static boolean isPaymentTypeRecurse(GenericValue paymentType, String inputTypeId)
            throws GenericEntityException {

        // first check the parentTypeId against inputTypeId
        String parentTypeId = paymentType.getString("parentTypeId");
        if (parentTypeId == null) {
            return false;
        }
        if (parentTypeId.equals(inputTypeId)) {
            return true;
        }

        // otherwise, we have to go to the grandparent (recurse)
        return isPaymentTypeRecurse(paymentType.getRelatedOne("ParentPaymentType"), inputTypeId);
    }

    /**
     * Checks if a payment is of a specified PaymentType.paymentTypeId. Return false if payment is null. It's better to
     * use the more specific calls like isTaxPayment().
     */
    public static boolean isPaymentType(GenericValue payment, String inputTypeId) throws GenericEntityException {
        if (payment == null) {
            return false;
        }

        GenericValue paymentType = payment.getRelatedOneCache("PaymentType");
        if (paymentType == null) {
            throw new GenericEntityException("Cannot find PaymentType for paymentId " + payment.getString("paymentId"));
        }

        String paymentTypeId = paymentType.getString("paymentTypeId");
        if (inputTypeId.equals(paymentTypeId)) {
            return true;
        }

        // recurse up tree
        return isPaymentTypeRecurse(paymentType, inputTypeId);
    }

    public static boolean isTaxPayment(GenericValue payment) throws GenericEntityException {
        return isPaymentType(payment, "TAX_PAYMENT");
    }

    public static boolean isDisbursement(GenericValue payment) throws GenericEntityException {
        return isPaymentType(payment, "DISBURSEMENT");
    }

    public static boolean isReceipt(GenericValue payment) throws GenericEntityException {
        return isPaymentType(payment, "RECEIPT");
    }

    /**
     * Determines if a glAccountClass is of a child of a certain parent glAccountClass.
     */
    public static boolean isAccountClassClass(GenericValue glAccountClass, String parentGlAccountClassId)
            throws GenericEntityException {
        if (glAccountClass == null)
            return false;

        // check current class against input classId
        if (parentGlAccountClassId.equals(glAccountClass.get("glAccountClassId"))) {
            return true;
        }

        // check parentClassId against inputClassId
        String parentClassId = glAccountClass.getString("parentClassId");
        if (parentClassId == null) {
            return false;
        }
        if (parentClassId.equals(parentGlAccountClassId)) {
            return true;
        }

        // otherwise, we have to go to the grandparent (recurse)
        return isAccountClassClass(glAccountClass.getRelatedOneCache("ParentGlAccountClass"), parentGlAccountClassId);
    }

    /**
     * Checks if a GL account is of a specified GlAccountClass.glAccountClassId. Returns false if account is null. It's
     * better to use the more specific calls like isDebitAccount().
     */
    public static boolean isAccountClass(GenericValue glAccount, String glAccountClassId) throws GenericEntityException {
        if (glAccount == null) {
            return false;
        }

        GenericValue glAccountClass = glAccount.getRelatedOneCache("GlAccountClass");
        if (glAccountClass == null) {
            throw new GenericEntityException("Cannot find GlAccountClass for glAccountId "
                    + glAccount.getString("glAccountId"));
        }

        return isAccountClassClass(glAccountClass, glAccountClassId);
    }

    public static boolean isDebitAccount(GenericValue account) throws GenericEntityException {
        return isAccountClass(account, "DEBIT");
    }

    public static boolean isCreditAccount(GenericValue account) throws GenericEntityException {
        return isAccountClass(account, "CREDIT");
    }

    public static boolean isAssetAccount(GenericValue account) throws GenericEntityException {
        return isAccountClass(account, "ASSET");
    }

    public static boolean isLiabilityAccount(GenericValue account) throws GenericEntityException {
        return isAccountClass(account, "LIABILITY");
    }

    public static boolean isEquityAccount(GenericValue account) throws GenericEntityException {
        return isAccountClass(account, "EQUITY");
    }

    public static boolean isIncomeAccount(GenericValue account) throws GenericEntityException {
        return isAccountClass(account, "INCOME");
    }

    public static boolean isRevenueAccount(GenericValue account) throws GenericEntityException {
        return isAccountClass(account, "REVENUE");
    }

    public static boolean isExpenseAccount(GenericValue account) throws GenericEntityException {
        return isAccountClass(account, "EXPENSE");
    }

    /**
     * Recurses up invoice type tree via parentTypeId to see if input invoice type ID is in tree.
     */
    private static boolean isInvoiceTypeRecurse(GenericValue invoiceType, String inputTypeId)
            throws GenericEntityException {

        // first check the invoiceTypeId and parentTypeId against inputTypeId
        String invoiceTypeId = invoiceType.getString("invoiceTypeId");
        String parentTypeId = invoiceType.getString("parentTypeId");
        if (parentTypeId == null || invoiceTypeId.equals(parentTypeId)) {
            return false;
        }
        if (parentTypeId.equals(inputTypeId)) {
            return true;
        }

        // otherwise, we have to go to the grandparent (recurse)
        return isInvoiceTypeRecurse(invoiceType.getRelatedOne("ParentInvoiceType"), inputTypeId);
    }

    /**
     * Checks if a invoice is of a specified InvoiceType.invoiceTypeId. Return false if invoice is null. It's better to
     * use more specific calls like isPurchaseInvoice().
     */
    public static boolean isInvoiceType(GenericValue invoice, String inputTypeId) throws GenericEntityException {
        if (invoice == null) {
            return false;
        }

        GenericValue invoiceType = invoice.getRelatedOneCache("InvoiceType");
        if (invoiceType == null) {
            throw new GenericEntityException("Cannot find InvoiceType for invoiceId " + invoice.getString("invoiceId"));
        }

        String invoiceTypeId = invoiceType.getString("invoiceTypeId");
        if (inputTypeId.equals(invoiceTypeId)) {
            return true;
        }

        // recurse up tree
        return isInvoiceTypeRecurse(invoiceType, inputTypeId);
    }

    public static boolean isPurchaseInvoice(GenericValue invoice) throws GenericEntityException {
        return isInvoiceType(invoice, "PURCHASE_INVOICE");
    }

    public static boolean isSalesInvoice(GenericValue invoice) throws GenericEntityException {
        return isInvoiceType(invoice, "SALES_INVOICE");
    }

    public static boolean isTemplate(GenericValue invoice) throws GenericEntityException {
        return isInvoiceType(invoice, "TEMPLATE");
    }

    public static String generateTallyTransactionXml(HttpServletRequest request, HttpServletResponse reponse) {

        GenericDispatcher dispatcher = (GenericDispatcher) request.getAttribute("dispatcher");
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        try {
            Map results =
                    dispatcher.runSync("GenerateTallyTransaction",
                            UtilMisc.toMap("userLogin", userLogin, "invoiceId", request.getParameter("invoiceId")));
            if (ServiceUtil.isError(results)) {
                return "error";
            }
            List voucherFileList = (List) results.get("voucherFileList");
            File f = (File) voucherFileList.get(0);
            OutputStream os = reponse.getOutputStream();
            reponse.addHeader("content-disposition", "attachment; filename=MasterData.xml");
            StringBuilder buffer = new StringBuilder();
            buffer
                    .append("<ENVELOPE><HEADER><TALLYREQUEST>Import Data</TALLYREQUEST></HEADER><BODY><IMPORTDATA><REQUESTDESC>");
            buffer.append("<REPORTNAME>All Masters</REPORTNAME></REQUESTDESC><REQUESTDATA>");
            reponse.setContentType("text/xml");
            os.write(buffer.toString().getBytes());
            FileInputStream fis = new FileInputStream(f);
            IOUtils.copy(fis, os);
            IOUtils.closeQuietly(fis);
            buffer = new StringBuilder("</REQUESTDATA></IMPORTDATA></BODY></ENVELOPE>");
            os.write(buffer.toString().getBytes());
            IOUtils.closeQuietly(os);
        } catch (ServiceValidationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (GenericServiceException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "success";
    }

    public static BigDecimal getGlTotalTransactionAmount(Delegator delegator, String parentGlAccountId,
                                                         String customTimePeriodId, String locationId, boolean debit) throws GenericEntityException {
        if (UtilValidate.isEmpty(customTimePeriodId))
            return BigDecimal.ZERO;
        Map<String, Timestamp> periodMap = getReportingPeriod(delegator, customTimePeriodId);
        List<String> glAccountIds =
                getAllChildGlAccounts(delegator, parentGlAccountId, locationId, periodMap.get("fromDate"));

        GenericValue customTimePeriod = null;
        try {
            customTimePeriod = delegator.findOne("CustomTimePeriod", true, "customTimePeriodId", customTimePeriodId);
        } catch (GenericEntityException e1) {
            e1.printStackTrace();
        }
        java.sql.Date nowTime = new java.sql.Date(UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp()).getTime());
        BigDecimal transactionTotal = BigDecimal.ZERO;
        if ("FISCAL_YEAR".equals(customTimePeriod.getString("periodTypeId")) && !((nowTime.before(customTimePeriod.getDate("thruDate"))
                && nowTime.after(customTimePeriod.getDate("fromDate"))))) {
            transactionTotal = getGlAccountHistoryAmount(glAccountIds, customTimePeriod, delegator, locationId, debit);
            return transactionTotal;
        } else {
            transactionTotal = getTransactionAmount(periodMap.get("fromDate"), periodMap.get("thruDate"), glAccountIds, debit,
                    delegator, locationId).add(getOpeningBalance(delegator, glAccountIds, customTimePeriodId, debit, locationId));
            return transactionTotal;
        }
    }

    public static BigDecimal getOpeningBalanceOfCashAndBank(Delegator delegator, String customTimePeriodId, String locationId) throws GenericEntityException {
        List<String> glAccountIds = UtilMisc.toList("8400000", "8410000", "8440000", "8401000", "8402000");
        BigDecimal openingBalance = BigDecimal.ZERO;
        Map<String, Timestamp> periodMap = getReportingPeriod(delegator, customTimePeriodId);
        GenericValue customTimePeriodGv = delegator.findOne("CustomTimePeriod", true, "customTimePeriodId", customTimePeriodId);
        java.sql.Date nowTime = new java.sql.Date(UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp()).getTime());
        if ("FISCAL_YEAR".equals(customTimePeriodGv.getString("periodTypeId")) && !((nowTime.before(customTimePeriodGv.getDate("thruDate"))
                && nowTime.after(customTimePeriodGv.getDate("fromDate"))))) {
            openingBalance = getGlAccountHistoryAmount(glAccountIds, customTimePeriodGv, delegator, locationId, true);
            return openingBalance;
        } else if (!"FISCAL_YEAR".equals(customTimePeriodGv.getString("periodTypeId"))) {
            openingBalance = getTransactionAmount(periodMap.get("fromDate"), periodMap.get("thruDate"), glAccountIds, true,
                    delegator, locationId).add(getOpeningBalance(delegator, glAccountIds, customTimePeriodId, true, locationId));
        } else {
            openingBalance = getOpeningBalance(delegator, glAccountIds, customTimePeriodId, true, locationId);
        }
        return openingBalance;
    }

    private static BigDecimal getGlAccountHistoryAmount(List<String> glAccountIds, GenericValue customTimePeriod,
                                                        Delegator delegator, String organizationPartyId, boolean debit) throws GenericEntityException {
        BigDecimal creditAmount = BigDecimal.ZERO;
        BigDecimal debitAmount = BigDecimal.ZERO;
        List<EntityCondition> conditions = new ArrayList<EntityCondition>();
        GenericValue previousTimePeriod = getPreviousFiscalCustomTimePeriod(customTimePeriod);

        List partyIds = new ArrayList();
        partyIds.add(organizationPartyId);

        conditions.add(EntityCondition.makeCondition("organizationPartyId", EntityComparisonOperator.IN, partyIds));

        if (previousTimePeriod != null)
            conditions.add(EntityCondition.makeCondition(
                    "customTimePeriodId",
                    EntityComparisonOperator.IN,
                    UtilMisc.toList(customTimePeriod.getString("customTimePeriodId"),
                            previousTimePeriod.getString("customTimePeriodId"))));
        else
            conditions.add(EntityCondition.makeCondition("customTimePeriodId", EntityComparisonOperator.EQUALS,
                    customTimePeriod.getString("customTimePeriodId")));
        conditions.add(EntityCondition.makeCondition("glAccountId", EntityComparisonOperator.IN, glAccountIds));
        EntityCondition condition = EntityCondition.makeCondition(conditions);
        List<GenericValue> glAccountHistories =
                delegator.findList("GlAccountHistory", condition, null, null, null, false);
        for (GenericValue glAccountHistory : glAccountHistories) {
            if (glAccountHistory.getBigDecimal("postedDebits") != null)
                debitAmount = debitAmount.add(glAccountHistory.getBigDecimal("postedDebits"));
            if (glAccountHistory.getBigDecimal("postedCredits") != null)
                creditAmount = creditAmount.add(glAccountHistory.getBigDecimal("postedCredits"));
        }
        BigDecimal transactionAmount = debit ? debitAmount.subtract(creditAmount) : creditAmount.subtract(debitAmount);
        return transactionAmount;
    }


    private static BigDecimal getGlAccountHistoryAmountByCreditAndDebitFlag(List<String> glAccountIds, GenericValue customTimePeriod,
                                                                            GenericDelegator delegator, String organizationPartyId, boolean debit) throws GenericEntityException {
        BigDecimal creditAmount = BigDecimal.ZERO;
        BigDecimal debitAmount = BigDecimal.ZERO;
        List<EntityCondition> conditions = new ArrayList<EntityCondition>();
        GenericValue previousTimePeriod = getPreviousFiscalCustomTimePeriod(customTimePeriod);

        List partyIds = new ArrayList();
        partyIds.add(organizationPartyId);

        conditions.add(EntityCondition.makeCondition("organizationPartyId", EntityComparisonOperator.IN, partyIds));

        if (previousTimePeriod != null)
            conditions.add(EntityCondition.makeCondition(
                    "customTimePeriodId",
                    EntityComparisonOperator.IN,
                    UtilMisc.toList(customTimePeriod.getString("customTimePeriodId"),
                            previousTimePeriod.getString("customTimePeriodId"))));
        else
            conditions.add(EntityCondition.makeCondition("customTimePeriodId", EntityComparisonOperator.EQUALS,
                    customTimePeriod.getString("customTimePeriodId")));
        conditions.add(EntityCondition.makeCondition("glAccountId", EntityComparisonOperator.IN, glAccountIds));
        EntityCondition condition = EntityCondition.makeCondition(conditions);
        List<GenericValue> glAccountHistories =
                delegator.findList("GlAccountHistory", condition, null, null, null, false);
        for (GenericValue glAccountHistory : glAccountHistories) {
            if (glAccountHistory.getBigDecimal("postedDebits") != null)
                debitAmount = debitAmount.add(glAccountHistory.getBigDecimal("postedDebits"));
            if (glAccountHistory.getBigDecimal("postedCredits") != null)
                creditAmount = creditAmount.add(glAccountHistory.getBigDecimal("postedCredits"));
        }
        BigDecimal transactionAmount = debit ? debitAmount : creditAmount;
        return transactionAmount;
    }

    public static GenericValue getPreviousFiscalCustomTimePeriod(GenericValue customTimePeriod) {
        List<GenericValue> previousTimePeriods;
        try {
            previousTimePeriods =
                    customTimePeriod.getDelegator().findList(
                            "CustomTimePeriod",
                            EntityCondition.makeCondition(
                                    EntityCondition.makeCondition("thruDate", EntityOperator.LESS_THAN,
                                            customTimePeriod.getDate("thruDate")),
                                    EntityCondition.makeCondition(UtilMisc.toMap("periodTypeId", "FISCAL_YEAR"))), null,
                            UtilMisc.toList("-thruDate"), null, true);
            if (UtilValidate.isEmpty(previousTimePeriods))
                return customTimePeriod;
            return EntityUtil.getFirst(previousTimePeriods);
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Map<String, Timestamp> getReportingPeriod(Delegator delegator, String customTimePeriodId)
            throws GenericEntityException {
        GenericValue timePeriodGv =
                delegator.findByPrimaryKey("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId));
        Timestamp fromTimeStamp = UtilDateTime.getDayStart(new Timestamp(timePeriodGv.getDate("fromDate").getTime()));
        Timestamp thruTimeStamp = UtilDateTime.getDayEnd(new Timestamp(timePeriodGv.getDate("thruDate").getTime()));
        Timestamp nowTimeStamp = UtilDateTime.nowTimestamp();
        if (fromTimeStamp.before(nowTimeStamp) && thruTimeStamp.after(nowTimeStamp))
            thruTimeStamp = nowTimeStamp;
        Map<String, Timestamp> periodMap = new HashMap<String, Timestamp>();
        periodMap.put("fromDate", fromTimeStamp);
        periodMap.put("thruDate", thruTimeStamp);
        return periodMap;
    }

    public static List<String> getAllChildGlAccounts(Delegator delegator, String glAccountId, String locationId,
                                                     Timestamp accountingPeriodStartDate) {
        List<String> glAccountIds = new ArrayList<String>();
        return getAllDecedentGlAccounts(delegator, glAccountId, locationId, glAccountIds, accountingPeriodStartDate);
    }

    public static List<String> getAllDecedentGlAccounts(Delegator delegator, String glAccountId,
                                                        String organizationPartyId, List<String> glAccounIds, Timestamp accountingPeriodStartDate) {
        glAccounIds.add(glAccountId);
        List<EntityCondition> conditions = new ArrayList<EntityCondition>();
        List partyIds = new ArrayList();
        partyIds.add(organizationPartyId);
        conditions.add(EntityCondition.makeCondition("organizationPartyId", EntityComparisonOperator.IN, partyIds));
        conditions
                .add(EntityCondition.makeCondition("parentGlAccountId", EntityComparisonOperator.EQUALS, glAccountId));
        EntityCondition ec = EntityCondition.makeCondition(conditions);
        List<GenericValue> glAccountGvs = null;
        try {
            glAccountGvs = delegator.findList("GlAccountOrganizationAndClass", ec, null, null, null, false);
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        Iterator<GenericValue> glAccountIt = glAccountGvs.iterator();
        while (glAccountIt.hasNext()) {
            GenericValue gv = glAccountIt.next();
            getAllDecedentGlAccounts(delegator, gv.getString("glAccountId"), organizationPartyId, glAccounIds,
                    accountingPeriodStartDate);
        }
        return glAccounIds;
    }

    public static List<String> getAllDecedentGlAccountsRecursive(GenericDelegator delegator, String glAccountId, String locationId,
                                                                 List<String> glAccounIds, Timestamp accountingPeriodStartDate) {
        glAccounIds.add(glAccountId);
        List<EntityCondition> conditions = new ArrayList<EntityCondition>();
        //conditions.add(EntityCondition.makeCondition("organizationPartyId", EntityComparisonOperator.EQUALS, locationId));
        conditions.add(EntityCondition.makeCondition("parentGlAccountId", EntityComparisonOperator.EQUALS, glAccountId));
        EntityCondition ec = EntityCondition.makeCondition(conditions);
        List<GenericValue> glAccountGvs = null;
        try {
            glAccountGvs = delegator.findList("GlAccount", ec, null, null, null, true);
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        Iterator<GenericValue> glAccountIt = glAccountGvs.iterator();
        while (glAccountIt.hasNext()) {
            GenericValue gv = glAccountIt.next();
            getAllDecedentGlAccountsRecursive(delegator, gv.getString("glAccountId"), locationId, glAccounIds, accountingPeriodStartDate);
        }
        return glAccounIds;
    }

    public static BigDecimal getTransactionAmountForChild(String glAccountId, String customTimePeriodId,
                                                          GenericDelegator delegator, String locationId) throws GenericEntityException {
        GenericValue account = delegator.findByPrimaryKey("GlAccount", UtilMisc.toMap("glAccountId", glAccountId));
        Map<String, Timestamp> periodMap = getReportingPeriod(delegator, customTimePeriodId);
        GenericValue customTimePeriod =
                delegator.findByPrimaryKey("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId));
        boolean debit = UtilAccounting.isDebitAccount(account);
        BigDecimal transactionAmount = BigDecimal.ZERO;
        java.sql.Date nowTime = new java.sql.Date(UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp()).getTime());
        BigDecimal transactionTotal = BigDecimal.ZERO;
        if ("FISCAL_YEAR".equals(customTimePeriod.getString("periodTypeId")) && !((nowTime.before(customTimePeriod.getDate("thruDate"))
                && nowTime.after(customTimePeriod.getDate("fromDate"))))) {
            transactionTotal = getGlAccountHistoryAmount(UtilMisc.toList(glAccountId), customTimePeriod, delegator, locationId, debit);
            return transactionTotal;
        } else {
            transactionAmount =
                    getTransactionAmount(periodMap.get("fromDate"), periodMap.get("thruDate"),
                            UtilMisc.toList(glAccountId), debit, delegator, locationId);
            BigDecimal openingBalance =
                    getOpeningBalance(delegator, UtilMisc.toList(glAccountId), customTimePeriodId, debit, locationId);
            return (transactionAmount.add(openingBalance));
        }
    }

    private static BigDecimal calculateTotalAmount(List<GenericValue> transactionEntries, Boolean debit) {
        BigDecimal creditTotal = BigDecimal.ZERO;
        BigDecimal debitTotal = BigDecimal.ZERO;
        for (GenericValue genericValue : transactionEntries) {
            if ("C".equalsIgnoreCase(genericValue.getString("debitCreditFlag"))) {
                creditTotal =
                        creditTotal.add(genericValue.get("amount") != null ? (BigDecimal) genericValue.get("amount")
                                : BigDecimal.ZERO);
                continue;
            }
            debitTotal =
                    debitTotal.add(genericValue.get("amount") != null ? (BigDecimal) genericValue.get("amount")
                            : BigDecimal.ZERO);
        }
        return debit ? debitTotal.subtract(creditTotal) : creditTotal.subtract(debitTotal);
    }

    private static BigDecimal calculateTotalAmountByCreditAndDebitFlag(List<GenericValue> transactionEntries, Boolean debit) {
        BigDecimal creditTotal = BigDecimal.ZERO;
        BigDecimal debitTotal = BigDecimal.ZERO;
        for (GenericValue genericValue : transactionEntries) {
            if ("C".equalsIgnoreCase(genericValue.getString("debitCreditFlag"))) {
                creditTotal = creditTotal.add(genericValue.get("amount") != null ? (BigDecimal) genericValue.get("amount") : BigDecimal.ZERO);
                continue;
            }
            debitTotal = debitTotal.add(genericValue.get("amount") != null ? (BigDecimal) genericValue.get("amount") : BigDecimal.ZERO);
        }
        return debit ? debitTotal : creditTotal;
    }


    public static BigDecimal getTotalAmount(BigDecimal... amount) {
        BigDecimal total = BigDecimal.ZERO;
        for (BigDecimal individualAmount : amount)
            total = total.add(individualAmount == null ? BigDecimal.ZERO : individualAmount);
        return total;
    }

    public static BigDecimal getTransactionAmount(Timestamp fromDate, Timestamp thruDate, List<String> glAcountIds,
                                                  boolean debit, Delegator delegator, String organizationPartyId) {
        List<GenericValue> transactionEntries = null;
        List<EntityCondition> conditions = new ArrayList<EntityCondition>();
        if (fromDate != null)
            conditions.add(EntityCondition.makeCondition("transactionDate", EntityComparisonOperator.GREATER_THAN_EQUAL_TO, fromDate));
        conditions.add(EntityCondition.makeCondition("transactionDate", EntityComparisonOperator.LESS_THAN_EQUAL_TO, UtilDateTime.getDayEnd(thruDate)));
        conditions.add(EntityCondition.makeCondition("glAccountId", EntityComparisonOperator.IN, glAcountIds));
        conditions.add(EntityCondition.makeCondition("isPosted", EntityOperator.EQUALS, "Y"));
        List partyIds = PartyWorker.getAssociatedPartyIdsByRelationshipType(delegator, organizationPartyId, "GROUP_ROLLUP");
        partyIds.add(organizationPartyId);

        conditions.add(EntityCondition.makeCondition("organizationPartyId", EntityComparisonOperator.IN, partyIds));
        EntityCondition condition = EntityCondition.makeCondition(conditions);
        try {
            transactionEntries = delegator.findList("AcctgTransEntrySums", condition, null, null, null, false);
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        return calculateTotalAmount(transactionEntries, debit);
    }

    public static BigDecimal getTransactionAmountByCreditAndDebitFlag(Timestamp fromDate, Timestamp thruDate, List<String> glAcountIds,
                                                                      boolean debit, GenericDelegator delegator, String organizationPartyId) {
        List<GenericValue> transactionEntries = null;
        List<EntityCondition> conditions = new ArrayList<EntityCondition>();
        if (fromDate != null)
            conditions.add(EntityCondition.makeCondition("transactionDate", EntityComparisonOperator.GREATER_THAN_EQUAL_TO, fromDate));
        conditions.add(EntityCondition.makeCondition("transactionDate", EntityComparisonOperator.LESS_THAN_EQUAL_TO, UtilDateTime.getDayEnd(thruDate)));
        conditions.add(EntityCondition.makeCondition("glAccountId", EntityComparisonOperator.IN, glAcountIds));
        conditions.add(EntityCondition.makeCondition("isPosted", EntityOperator.EQUALS, "Y"));
        List partyIds = PartyWorker.getAssociatedPartyIdsByRelationshipType(delegator, organizationPartyId, "GROUP_ROLLUP");
        partyIds.add(organizationPartyId);

        conditions.add(EntityCondition.makeCondition("organizationPartyId", EntityComparisonOperator.IN, partyIds));
        EntityCondition condition = EntityCondition.makeCondition(conditions);
        try {
            transactionEntries = delegator.findList("AcctgTransEntrySums", condition, null, null, null, false);
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        return calculateTotalAmountByCreditAndDebitFlag(transactionEntries, debit);
    }

    public static BigDecimal getOpeningBalance(Delegator delegator, String glAccountId, String accountingPeriodId,
                                               boolean debit, String locationId) throws GenericEntityException {
        List<String> glAccountIds = getAllChildGlAccounts(delegator, glAccountId, locationId, null);
        return getOpeningBalance(delegator, glAccountIds, accountingPeriodId, debit, locationId);
    }

    public static BigDecimal getOpeningBalance(Delegator delegator, List<String> glAccountIds,
                                               String accountingPeriodId, boolean debit, String organizationPartyId) throws GenericEntityException {
        if (UtilValidate.isEmpty(accountingPeriodId))
            return BigDecimal.ZERO;
        if (UtilValidate.isEmpty(glAccountIds))
            return BigDecimal.ZERO;
        String glAccountId = glAccountIds.get(0);
        GenericValue glGv = delegator.findOne("GlAccount", true, UtilMisc.toMap("glAccountId", glAccountId));
        if (UtilAccounting.isExpenseAccount(glGv) || UtilAccounting.isIncomeAccount(glGv))
            return BigDecimal.ZERO;
        List<EntityCondition> conditions = new ArrayList<EntityCondition>();
        GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", true, "customTimePeriodId", accountingPeriodId);
        GenericValue previousCustomTimePeriod = getPreviousFiscalCustomTimePeriod(customTimePeriod);
        if (previousCustomTimePeriod == null || accountingPeriodId.equalsIgnoreCase(previousCustomTimePeriod.getString("customTimePeriodId")))
            return BigDecimal.ZERO;
        conditions.add(EntityCondition.makeCondition("customTimePeriodId", EntityComparisonOperator.EQUALS, previousCustomTimePeriod.getString("customTimePeriodId")));
        conditions.add(EntityCondition.makeCondition("glAccountId", EntityComparisonOperator.IN, glAccountIds));
        List partyIds = PartyWorker.getAssociatedPartyIdsByRelationshipType(delegator, organizationPartyId, "GROUP_ROLLUP");
        partyIds.add(organizationPartyId);
        conditions.add(EntityCondition.makeCondition("organizationPartyId", EntityComparisonOperator.IN, partyIds));
        EntityCondition condition = EntityCondition.makeCondition(conditions);
        List<GenericValue> glAccountHistories = delegator.findList("GlAccountHistory", condition, null, null, null, false);
        BigDecimal creditTotal = BigDecimal.ZERO;
        BigDecimal debitTotal = BigDecimal.ZERO;
        for (GenericValue gv : glAccountHistories) {
            creditTotal = creditTotal.add(gv.getBigDecimal("postedCredits") == null ? BigDecimal.ZERO : gv.getBigDecimal("postedCredits"));
            debitTotal = debitTotal.add(gv.getBigDecimal("postedDebits") == null ? BigDecimal.ZERO : gv.getBigDecimal("postedDebits"));
        }
        return debit ? debitTotal.subtract(creditTotal) : creditTotal.subtract(debitTotal);
    }


    public static BigDecimal getInvoiceAmount(GenericDelegator delegator, String invoiceType, Integer startOffset,
                                              Integer endOffset, String partyId, Boolean overDue)
            throws GenericEntityException {
        BigDecimal invoiceAmount = BigDecimal.ZERO;

        List<GenericValue> invoiceGvs = getAllInvoice(delegator, invoiceType, startOffset, endOffset, partyId, overDue);
        if (UtilValidate.isEmpty(invoiceGvs)) return BigDecimal.ZERO;
        for (GenericValue gv : invoiceGvs) {
            BigDecimal amount = InvoiceWorker.getInvoiceTotal(gv) != null ? (BigDecimal) InvoiceWorker.getInvoiceTotal(gv) : BigDecimal.ZERO;
            invoiceAmount = invoiceAmount.add(amount);
        }
        BigDecimal paymentAmount = getPaymentAmount(delegator, startOffset, endOffset, invoiceType, invoiceGvs);
        BigDecimal osAmount = invoiceAmount.subtract(paymentAmount);
        //if (osAmount.compareTo(new BigDecimal("100")) == -1) return BigDecimal.ZERO;
        return osAmount;
    }

    public static List<GenericValue> getAllInvoice(GenericDelegator delegator, String invoiceType, Integer startOffset,
                                                   Integer endOffset, String partyId, Boolean overDue)
            throws GenericEntityException {
        List<EntityCondition> conditions = new ArrayList<EntityCondition>();
        Timestamp todayDateTimestamp = UtilDateTime.getTimestamp(UtilDateTime.nowDate().getTime());
        Timestamp nowTimeStamp = UtilDateTime.nowTimestamp();
        Timestamp startDate = UtilDateTime.addDaysToTimestamp(nowTimeStamp, startOffset);
        Timestamp endDate = endOffset == -1 ? null : UtilDateTime.addDaysToTimestamp(nowTimeStamp, endOffset);
        conditions.add(EntityCondition.makeCondition("invoiceTypeId", EntityOperator.EQUALS, invoiceType));
        EntityCondition partyEc = null;
        if ("PURCHASE_INVOICE".equals(invoiceType) || "COMMISSION_INVOICE".equals(invoiceType) || "CUST_RTN_INVOICE".equals(invoiceType) || "PAYROL_INVOICE".equals(invoiceType)) {
            partyEc = EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId);
        }
        if ("SALES_INVOICE".equals(invoiceType) || "PURC_RTN_INVOICE".equals(invoiceType) || "INTEREST_INVOICE".equals(invoiceType)) {
            partyEc = EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, partyId);
        }
        conditions.add(partyEc);

        if (overDue) {
            //if (startDate.before(todayDateTimestamp)) return null;
            conditions.add(EntityCondition.makeCondition("dueDate", EntityOperator.LESS_THAN_EQUAL_TO, startDate));
            if (endOffset == -1) endDate = todayDateTimestamp;
            conditions.add(EntityCondition.makeCondition("dueDate", EntityOperator.GREATER_THAN_EQUAL_TO, endDate));
        } else {
            conditions.add(EntityCondition.makeCondition("dueDate", EntityOperator.GREATER_THAN_EQUAL_TO, startDate));
            if (endDate != null)
                conditions.add(EntityCondition.makeCondition("dueDate", EntityOperator.LESS_THAN_EQUAL_TO, endDate));
        }
        EntityCondition ec = EntityCondition.makeCondition(conditions);
        return delegator.findList("Invoice", ec, null, null, null, true);
    }

    public static BigDecimal getPaymentAmount(GenericDelegator delegator, Integer startOffset, Integer endOffset,
                                              String invoiceType, List<GenericValue> invoiceGvs) throws GenericEntityException {
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<String> invoiceIds = new ArrayList<String>();
        for (GenericValue gv : invoiceGvs)
            invoiceIds.add(gv.getString("invoiceId"));
        List<GenericValue> payments = delegator.findList("PaymentApplication", EntityCondition.makeCondition("invoiceId", EntityComparisonOperator.IN, invoiceIds), null, null, null, true);
        for (GenericValue gv : payments) {
            BigDecimal amount = gv.getBigDecimal("amountApplied") != null ? gv.getBigDecimal("amountApplied") : BigDecimal.ZERO;
            totalAmount = totalAmount.add(amount);
        }
        return totalAmount;
    }

    public static BigDecimal getProfitAndLossAmount(GenericDelegator delegator, String customTimePeriodId, String organizationPartyId) {
        try {
            BigDecimal profitAndLossAmt = BigDecimal.ZERO;
            java.sql.Date nowTime = new java.sql.Date(UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp()).getTime());
            GenericValue timePeriodGv = delegator.findOne("CustomTimePeriod", false, UtilMisc.toMap("customTimePeriodId", customTimePeriodId));
            if ("FISCAL_YEAR".equals(timePeriodGv.getString("periodTypeId")) && ((nowTime.before(timePeriodGv.getDate("thruDate"))
                    && nowTime.after(timePeriodGv.getDate("fromDate"))))) {
                profitAndLossAmt = getGlTotalTransactionAmount(delegator, "9620000", customTimePeriodId, organizationPartyId, false);
            }
            BigDecimal incomeTotal = getGlTotalTransactionAmount(delegator, "10000000", customTimePeriodId, organizationPartyId, false);
            BigDecimal otherIncomeTotal = getGlTotalTransactionAmount(delegator, "1020000_1", customTimePeriodId, organizationPartyId, false);
            incomeTotal = incomeTotal.add(otherIncomeTotal);
            BigDecimal expenseTotal = getGlTotalTransactionAmount(delegator, "20000000", customTimePeriodId, organizationPartyId, true);
            profitAndLossAmt = profitAndLossAmt.add(incomeTotal).subtract(expenseTotal);
            return profitAndLossAmt;
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        return BigDecimal.ZERO;
    }

    public static String updateOpeningBalances(HttpServletRequest request, HttpServletResponse response) {
        String customTimePeriodId = (String) request.getParameter("customTimePeriodId");
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        String organizationPartyId = (String) request.getParameter("organizationPartyId");
        try {
            GenericValue previousCustomTimePeriod = getPreviousFiscalCustomTimePeriod(delegator.findOne("CustomTimePeriod", true,
                    UtilMisc.toMap("customTimePeriodId", customTimePeriodId)));
            if (previousCustomTimePeriod == null) {
                return "error";
            }
            String[] glAccounts = request.getParameterValues("glAccountId");
            for (String glAccountId : glAccounts) {
                String cr = request.getParameter(glAccountId + "_cr");
                BigDecimal credit = new BigDecimal(UtilValidate.isEmpty(cr) ? "0" : cr);
                String db = request.getParameter(glAccountId + "_db");
                BigDecimal debit = new BigDecimal(UtilValidate.isEmpty(db) ? "0" : db);
                delegator.createOrStore(delegator.makeValidValue("GlAccountHistory", UtilMisc.toMap("organizationPartyId",
                        organizationPartyId, "customTimePeriodId",
                        previousCustomTimePeriod.getString("customTimePeriodId"), "glAccountId", glAccountId,
                        "postedCredits", credit, "postedDebits", debit)));
            }
            request.setAttribute("_EVENT_MESSAGE_", "Action completed successfully.");
        } catch (GenericEntityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "success";
    }


    public static BigDecimal getGlTransactionAmount(GenericDelegator delegator, String parentGlAccountId,
                                                    String asOnDate, String accountingPeriodId, String locationId, boolean debit) throws GenericEntityException {
        Timestamp asOnTimestamp = UtilDateTime.getTimestamp(UtilDateTime.nowDate().getTime());
        GenericValue customTimePeriodGv = delegator.findByPrimaryKey("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", accountingPeriodId));
        boolean timePeriodRequire = false;
        if (asOnTimestamp != null) {
            Date endDate = new Date(asOnTimestamp.getTime());
            if (UtilValidate.isNotEmpty(customTimePeriodGv)) {
                Date startDate = ((Date) customTimePeriodGv.get("fromDate"));
                EntityCondition ec = EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityComparisonOperator.GREATER_THAN_EQUAL_TO, startDate), EntityComparisonOperator.AND,
                        EntityCondition.makeCondition("fromDate", EntityComparisonOperator.LESS_THAN_EQUAL_TO, endDate));
                List<GenericValue> periods = delegator.findList("CustomTimePeriod", ec, null, null, null, false);
                if (UtilValidate.isEmpty(periods))
                    timePeriodRequire = true;
                else {
                    if (!accountingPeriodId.equals(periods.get(0).get("customTimePeriodId"))) timePeriodRequire = true;
                }
            }
        }
        if (UtilValidate.isNotEmpty(customTimePeriodGv)) {
            if (asOnTimestamp == null) asOnTimestamp = new Timestamp(customTimePeriodGv.getDate("thruDate").getTime());
            Timestamp fromDate = new Timestamp(((Date) customTimePeriodGv.get("fromDate")).getTime());
            List<String> glAccountIds = getAllChildGlAccounts(delegator, parentGlAccountId, locationId, fromDate);
            return getTransactionAmount(UtilValidate.isEmpty(accountingPeriodId) ? null : new Timestamp(((Date) customTimePeriodGv.get("fromDate"))
                    .getTime()), asOnTimestamp, glAccountIds, debit, delegator, locationId).add(getOpeningBalance(delegator, glAccountIds,
                    timePeriodRequire ? null : accountingPeriodId, debit, locationId));
        }
        return BigDecimal.ZERO;
    }


    public static BigDecimal getCashFlowAmount(GenericDelegator delegator, String glAccountId, String customTimePeriodId,
                                               String organizationPartyId, boolean debitFlag) throws GenericEntityException {
        Map<String, Timestamp> periodMap = getReportingPeriod(delegator, customTimePeriodId);
        List<String> childGlAccounts = getAllChildGlAccounts(delegator, glAccountId, organizationPartyId, periodMap.get("fromDate"));
        BigDecimal transactionAmount = getTransactionAmount(periodMap.get("fromDate"), periodMap.get("thruDate"), childGlAccounts, debitFlag, delegator, organizationPartyId);
        if (transactionAmount.compareTo(BigDecimal.ZERO) == 0)
            return BigDecimal.ZERO;
        else
            return transactionAmount;
    }

    public static String cancelScheduleJob(GenericDelegator delegator, String acctgTemplateId, String organizationPartyId) throws GenericEntityException, SerializeException, SAXException, ParserConfigurationException, IOException {
        if (delegator == null || UtilValidate.isEmpty(acctgTemplateId) || UtilValidate.isEmpty(organizationPartyId)) {
            return "error";
        }
        List<GenericValue> runtimeDataList = delegator.findList("RuntimeData", null, null, null, null, true);
        for (GenericValue runtimeDataGv : runtimeDataList) {
            Map<String, Object> context = UtilGenerics.checkMap(XmlSerializer.deserialize(runtimeDataGv.getString("runtimeInfo"), delegator), String.class, Object.class);
            if (acctgTemplateId.equalsIgnoreCase((String) context.get("acctgTemplateId")) && organizationPartyId.equalsIgnoreCase((String) context.get("organizationPartyId"))) {
                List<GenericValue> jobSandboxList = delegator.findByAnd("JobSandbox", UtilMisc.toMap("runtimeDataId", runtimeDataGv.getString("runtimeDataId")), null, true);
                if (UtilValidate.isEmpty(jobSandboxList))
                    return "error";
                GenericValue recurrenceInfoGv = jobSandboxList.get(0).getRelatedOne("RecurrenceInfo", true);
                GenericValue recurrenceRuleGv = recurrenceInfoGv.getRelatedOne("RecurrenceRule", true);
                delegator.removeAll(jobSandboxList);
                delegator.removeValue(recurrenceInfoGv, true);
                delegator.removeValue(recurrenceRuleGv, true);
                delegator.removeValue(runtimeDataGv, true);
            }
        }
        return "success";
    }


    /**
     * {@link "http://www.accountingtools.com/closing-entries"}
     *
     * @return
     */

    public static BigDecimal getSurplusAmountForAsset(Delegator delegator, String customTimePeriodId) throws GenericEntityException, SQLException {
        GenericValue timePeriodGv = delegator.findOne("CustomTimePeriod", false, UtilMisc.toMap("customTimePeriodId", customTimePeriodId));
        java.sql.Date nowTime = new java.sql.Date(UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp()).getTime());
        BigDecimal surplusAmount = BigDecimal.ZERO;
        BigDecimal incomeTotal = getIncomeTotalForGivenTimePeriod(delegator, customTimePeriodId);
        BigDecimal expenseTotal = getExpenseTotalForGivenTimePeriod(delegator, customTimePeriodId);
        BigDecimal profitAndLossAmount = incomeTotal.subtract(expenseTotal);
        if ((profitAndLossAmount.signum() == 1 && timePeriodGv != null && "Y".equalsIgnoreCase(timePeriodGv.getString("isClosed")))
                && ("FISCAL_YEAR".equals(timePeriodGv.getString("periodTypeId")) && ((nowTime.before(timePeriodGv.getDate("thruDate"))
                && nowTime.after(timePeriodGv.getDate("fromDate")))))) {
            return profitAndLossAmount;
        }
        return surplusAmount;
    }

    public static BigDecimal getSurplusAmountForLiability(Delegator delegator, String customTimePeriodId) throws GenericEntityException, SQLException {
        GenericValue timePeriodGv = delegator.findOne("CustomTimePeriod", false, UtilMisc.toMap("customTimePeriodId", customTimePeriodId));
        BigDecimal surplusAmount = BigDecimal.ZERO;
        BigDecimal incomeTotal = getIncomeTotalForGivenTimePeriod(delegator, customTimePeriodId);
        BigDecimal expenseTotal = getExpenseTotalForGivenTimePeriod(delegator, customTimePeriodId);
        BigDecimal profitAndLossAmount = incomeTotal.subtract(expenseTotal);
        java.sql.Date nowTime = new java.sql.Date(UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp()).getTime());
        if ((profitAndLossAmount.signum() == -1 && timePeriodGv != null && "Y".equalsIgnoreCase(timePeriodGv.getString("isClosed")))
                && ("FISCAL_YEAR".equals(timePeriodGv.getString("periodTypeId")) && ((nowTime.before(timePeriodGv.getDate("thruDate"))
                && nowTime.after(timePeriodGv.getDate("fromDate")))))) {
            return profitAndLossAmount;
        }
        return surplusAmount;
    }


    public static Map<String, Object> updateReturnedEarningForGivenTimePeriod(DispatchContext dctx, Map<String, ? extends Object> context) throws GenericEntityException, SQLException {
        String customTimePeriodId = (String) context.get("customTimePeriodId");
        Delegator delegator = dctx.getDelegator();
        BigDecimal incomeTotal = getIncomeTotalForGivenTimePeriod(delegator, customTimePeriodId);
        BigDecimal expenseTotal = getExpenseTotalForGivenTimePeriod(delegator, customTimePeriodId);
        BigDecimal profitAndLossAmount = incomeTotal.subtract(expenseTotal);
        Map glHistoryMap = UtilMisc.toMap("glAccountId", "9620000", "organizationPartyId", "Company", "customTimePeriodId", customTimePeriodId);
        BigDecimal postedDebits = BigDecimal.ZERO;
        BigDecimal postedCredits = BigDecimal.ZERO;
        if (profitAndLossAmount.signum() == 1) {
            postedCredits = profitAndLossAmount;
        } else {
            postedDebits = profitAndLossAmount;
        }
        glHistoryMap.put("postedDebits", postedDebits);
        glHistoryMap.put("postedCredits", postedCredits);
        GenericValue glAccountHistoryGv = delegator.makeValidValue("GlAccountHistory", glHistoryMap);
        delegator.createOrStore(glAccountHistoryGv);
        return ServiceUtil.returnSuccess();
    }

    private static BigDecimal getIncomeTotalForGivenTimePeriod(Delegator delegator, String customTimePeriodId) throws GenericEntityException, SQLException {
        BigDecimal incomeTotal = BigDecimal.ZERO;
        SQLProcessor sqlProc = new SQLProcessor(delegator.getGroupHelperInfo("org.ofbiz"));
        String incomeQuery = "SELECT IFNULL(SUM(POSTED_DEBITS),0) AS debitTotal,IFNULL(SUM(POSTED_CREDITS),0) AS creditTotal " +
                " FROM GL_ACCOUNT_HISTORY GH,GL_ACCOUNT G WHERE GH.`GL_ACCOUNT_ID`=G.`GL_ACCOUNT_ID`\n" +
                " AND (G.`GL_ACCOUNT_CLASS_ID` =\"INCOME\" OR G.`GL_ACCOUNT_CLASS_ID` =\"CONTRA_INCOME\")";
        sqlProc.prepareStatement(incomeQuery);
        ResultSet incomeResultSet = sqlProc.executeQuery();
        while (incomeResultSet.next()) {
            incomeTotal = incomeResultSet.getBigDecimal("creditTotal").subtract(incomeResultSet.getBigDecimal("debitTotal"));
        }
        return incomeTotal;
    }

    private static BigDecimal getExpenseTotalForGivenTimePeriod(Delegator delegator, String customTimePeriodId) throws GenericEntityException, SQLException {
        BigDecimal expenseTotal = BigDecimal.ZERO;
        String expenseQuery = "SELECT IFNULL(SUM(POSTED_DEBITS),0) AS debitTotal,IFNULL(SUM(POSTED_CREDITS),0) AS creditTotal " +
                " FROM GL_ACCOUNT_HISTORY GH,GL_ACCOUNT G WHERE GH.`GL_ACCOUNT_ID`=G.`GL_ACCOUNT_ID`\n" +
                " AND (G.`GL_ACCOUNT_CLASS_ID` =\"EXPENSE\" OR G.`GL_ACCOUNT_CLASS_ID` =\"DEPRECIATION\")";
        SQLProcessor sqlProc = new SQLProcessor(delegator.getGroupHelperInfo("org.ofbiz"));
        sqlProc.prepareStatement(expenseQuery);
        ResultSet expenseResultSet = sqlProc.executeQuery();
        while (expenseResultSet.next()) {
            expenseTotal = expenseResultSet.getBigDecimal("debitTotal").subtract(expenseResultSet.getBigDecimal("creditTotal"));
        }
        return expenseTotal;

    }
}
