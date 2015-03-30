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
package org.ofbiz.accounting;

import java.math.BigDecimal;
import java.security.Policy.Parameters;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javolution.util.FastList;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

public class GlEvents {

    public static final String module = GlEvents.class.getName();

    public static String createReconcileAccount(HttpServletRequest request, HttpServletResponse response) {
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        final Delegator delegator = (Delegator) request.getAttribute("delegator");
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        Map<String, Object> ctx = UtilHttp.getParameterMap(request);
        String acctgTransId;
        String acctgTransEntrySeqId;
        String glAccountId = null;
        String organizationPartyId = null;
        BigDecimal reconciledBalance = BigDecimal.ZERO;
        boolean isSelected;
        String debitCreditFlag;
        // The number of multi form rows is retrieved
        int rowCount = UtilHttp.getMultiFormRowCount(ctx);
        for (int i = 0; i < rowCount; i++) { // for calculating amount per
                                             // glAccountId
            String suffix = UtilHttp.MULTI_ROW_DELIMITER + i;
            isSelected =
                (ctx.containsKey("_rowSubmit" + suffix) && "Y"
                    .equalsIgnoreCase((String) ctx.get("_rowSubmit" + suffix)));
            if (!isSelected) {
                continue;
            }
            acctgTransId = (String) ctx.get("acctgTransId" + suffix);
            acctgTransEntrySeqId = (String) ctx.get("acctgTransEntrySeqId" + suffix);
            organizationPartyId = (String) ctx.get("organizationPartyId" + suffix);
            glAccountId = (String) ctx.get("glAccountId" + suffix);
            try {
                GenericValue acctgTransEntry =
                    delegator.findOne("AcctgTransEntry",
                        UtilMisc.toMap("acctgTransId", acctgTransId, "acctgTransEntrySeqId", acctgTransEntrySeqId),
                        false);
                if (UtilValidate.isNotEmpty(acctgTransEntry)) {
                    // calculate amount for each AcctgTransEntry according to
                    // glAccountId based on debit and credit
                    debitCreditFlag = acctgTransEntry.getString("debitCreditFlag");
                    if ("D".equalsIgnoreCase(debitCreditFlag)) {
                        reconciledBalance = reconciledBalance.add(acctgTransEntry.getBigDecimal("amount")); // total
                                                                                                            // balance
                                                                                                            // per
                                                                                                            // glAccountId
                    } else {
                        reconciledBalance = reconciledBalance.subtract(acctgTransEntry.getBigDecimal("amount")); // total
                                                                                                                 // balance
                                                                                                                 // per
                                                                                                                 // glAccountId
                    }
                }
            } catch (GenericEntityException e) {
                Debug.logError(e, module);
                return "error";
            }

        }
        Map<String, Object> fieldMap =
            UtilMisc.toMap("glReconciliationName", "Reconciliation at date " + UtilDateTime.nowTimestamp(),
                "glAccountId", glAccountId, "organizationPartyId", organizationPartyId, "reconciledDate",
                UtilDateTime.nowTimestamp(), "reconciledBalance", reconciledBalance, "userLogin", userLogin);
        Map<String, Object> glReconResult = null;
        try {
            glReconResult = dispatcher.runSync("createGlReconciliation", fieldMap); // create
                                                                                    // GlReconciliation
                                                                                    // for
                                                                                    // the
                                                                                    // glAccountId
            if (ServiceUtil.isError(glReconResult)) {
                return "error";
            }
        } catch (GenericServiceException e) {
            Debug.logError(e, module);
            return "error";
        }
        String glReconciliationId = (String) glReconResult.get("glReconciliationId");
        String reconciledAmount;
        for (int i = 0; i < rowCount; i++) {
            String suffix = UtilHttp.MULTI_ROW_DELIMITER + i;
            isSelected =
                (ctx.containsKey("_rowSubmit" + suffix) && "Y"
                    .equalsIgnoreCase((String) ctx.get("_rowSubmit" + suffix)));
            if (!isSelected) {
                continue;
            }
            acctgTransId = (String) ctx.get("acctgTransId" + suffix);
            acctgTransEntrySeqId = (String) ctx.get("acctgTransEntrySeqId" + suffix);
            try {
                GenericValue acctgTransEntry =
                    delegator.findOne("AcctgTransEntry",
                        UtilMisc.toMap("acctgTransId", acctgTransId, "acctgTransEntrySeqId", acctgTransEntrySeqId),
                        false);
                if (UtilValidate.isNotEmpty(acctgTransEntry)) {
                    reconciledAmount = acctgTransEntry.getString("amount");
                    acctgTransId = acctgTransEntry.getString("acctgTransId");
                    acctgTransEntrySeqId = acctgTransEntry.getString("acctgTransEntrySeqId");
                    Map<String, Object> glReconEntryMap =
                        UtilMisc.toMap("glReconciliationId", glReconciliationId, "acctgTransId", acctgTransId,
                            "acctgTransEntrySeqId", acctgTransEntrySeqId, "reconciledAmount", reconciledAmount,
                            "userLogin", userLogin);
                    Map<String, Object> glReconEntryResult = null;
                    try {
                        glReconEntryResult = dispatcher.runSync("createGlReconciliationEntry", glReconEntryMap);
                        if (ServiceUtil.isError(glReconEntryResult)) {
                            return "error";
                        }
                    } catch (GenericServiceException e) {
                        Debug.logError(e, module);
                        return "error";
                    }
                }
            } catch (GenericEntityException e) {
                Debug.logError(e, module);
                return "error";
            }
        }
        ctx.put("glReconciliationId", glReconciliationId);
        request.setAttribute("glReconciliationId", glReconciliationId);
        return "success";
    }

    public static String getClassification(HttpServletRequest request, HttpServletResponse response)
        throws GenericEntityException {
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        String accountName = request.getQueryString();

        List<GenericValue> className = null;
        List<String> classNameList = FastList.newInstance();
        if (accountName.equals("CST")) {
            className = delegator.findByAnd("TaxClassification", UtilMisc.toMap("vatSubType", null));
            if (UtilValidate.isNotEmpty(className)) {
                for (GenericValue value : className)
                    classNameList.add(value.get("taxClassId") + ":" + value.get("className"));

                if (classNameList.size() > 0) {
                    classNameList.add(0, ":  ");
                }
            }
        }
        if (accountName.equals("VAT")) {
            className =
                delegator.findByAnd("Enumeration", UtilMisc.toMap("enumTypeId", "VAT_SUB_TYPE"),
                    UtilMisc.toList("description"), true);
            if (UtilValidate.isNotEmpty(className)) {
                for (GenericValue value : className)
                    classNameList.add(value.get("enumId") + ":" + value.get("enumId"));

                if (classNameList.size() > 0) {
                    classNameList.add(0, ":  ");
                }
            }
        }

        request.setAttribute("classNameValue", classNameList);
        return "success";
    }

    public static String getClassificationVat(HttpServletRequest request, HttpServletResponse response)
        throws GenericEntityException {
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        String accountName = request.getQueryString();
        List<GenericValue> className = null;
        className = delegator.findByAnd("TaxClassification", UtilMisc.toMap("vatSubType", accountName), null, true);
        List<String> classNameList = FastList.newInstance();
        if (UtilValidate.isNotEmpty(className)) {
            for (GenericValue value : className)
                classNameList.add(value.get("taxClassId") + ":" + value.get("className"));

            if (classNameList.size() > 0) {
                classNameList.add(0, ":  ");
            }
        }
        request.setAttribute("classNameValue", classNameList);
        return "success";
    }

    public static String createCustomGlAccount(HttpServletRequest request, HttpServletResponse response)
        throws GenericEntityException {
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();
        Map<String, Object> ctx = UtilHttp.getParameterMap(request);
        
		List accountNameList = delegator.findList("GlAccount",EntityCondition.makeCondition("accountName", EntityOperator.EQUALS,ctx.get("accountName")), null, null, null, false);
		if(UtilValidate.isNotEmpty(accountNameList)){
			request.setAttribute("_ERROR_MESSAGE_", " Account Name cannot be duplicated ");
			return "error";
		}

        String glAccountId = delegator.getNextSeqId("GlAccount");
        GenericValue parentGLGv =
            delegator.findOne("GlAccount", true, UtilMisc.toMap("glAccountId", ctx.get("parentGlAccountId")));
        Map glAccountMap =
            UtilMisc.toMap("glAccountId", ctx.get("parentGlAccountId") + "_" + glAccountId, "accountCode", ctx.get("parentGlAccountId") + "_" + glAccountId, "glAccountTypeId",
                parentGLGv.getString("glAccountTypeId"), "accountName", ctx.get("accountName"), "glAccountClassId",
                parentGLGv.getString("glAccountClassId"), "taxType", ctx.get("typeOfDuty"), "subTypeOfDuty",
                ctx.get("subTypeOfDuty"), "parentGlAccountId", ctx.get("parentGlAccountId"), "taxClassId",
                UtilValidate.isEmpty(ctx.get("taxClassId")) ? null : ctx.get("taxClassId"),
                "applyTax", UtilValidate.isNotEmpty(ctx.get("applyTax")) ? ctx.get("applyTax") : "Y");

        GenericValue glAccountGv = delegator.makeValue("GlAccount", glAccountMap);
        delegator.create(glAccountGv);
        GenericValue gv = delegator.create("GlAccountOrganization", UtilMisc.toMap("glAccountId", ctx.get("parentGlAccountId") + "_"+glAccountId, "organizationPartyId", "Company"));
        delegator.store(gv);
        
        request.setAttribute("_EVENT_MESSAGE_", "GL Account created or updated successfully.");
        return "success";
    }
}