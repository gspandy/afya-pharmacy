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
package org.ofbiz.accounting.ledger;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javolution.util.FastList;

import org.ofbiz.accounting.util.UtilAccounting;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.DynamicViewEntity;
import org.ofbiz.entity.model.ModelKeyMap;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.order.order.OrderReadHelper;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import freemarker.cache.FileTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.ObjectWrapper;
import freemarker.template.SimpleHash;
import freemarker.template.SimpleList;
import freemarker.template.Template;

public class GeneralLedgerServices {

    public static final String module = GeneralLedgerServices.class.getName();

    private static BigDecimal ZERO = BigDecimal.ZERO;

    public static Map<String, Object> createUpdateCostCenter(DispatchContext dctx, Map<String, ? extends Object> context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        BigDecimal totalAmountPercentage = ZERO;
        Map<String, Object> createGlAcctCatMemFromCostCentersMap = null;
        String glAccountId = (String) context.get("glAccountId");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Map<String, String> amountPercentageMap = UtilGenerics.checkMap(context.get("amountPercentageMap"));
        totalAmountPercentage = GeneralLedgerServices.calculateCostCenterTotal(amountPercentageMap);
        for (String rowKey : amountPercentageMap.keySet()) {
            String rowValue = amountPercentageMap.get(rowKey);
            if (UtilValidate.isNotEmpty(rowValue)) {
                createGlAcctCatMemFromCostCentersMap = UtilMisc.toMap("glAccountId", glAccountId,
                        "glAccountCategoryId", rowKey, "amountPercentage", new BigDecimal(rowValue),
                        "userLogin", userLogin, "totalAmountPercentage", totalAmountPercentage);
            } else {
                createGlAcctCatMemFromCostCentersMap = UtilMisc.toMap("glAccountId", glAccountId,
                        "glAccountCategoryId", rowKey, "amountPercentage", new BigDecimal(0),
                        "userLogin", userLogin, "totalAmountPercentage", totalAmountPercentage);
            }
            try {
                dispatcher.runSync("createGlAcctCatMemFromCostCenters", createGlAcctCatMemFromCostCentersMap);
            } catch (GenericServiceException e) {
                Debug.logError(e, module);
                return ServiceUtil.returnError(e.getMessage());
            }
        }
        return ServiceUtil.returnSuccess();
    }

    public static BigDecimal calculateCostCenterTotal(Map<String, String> amountPercentageMap) {
        BigDecimal totalAmountPercentage = ZERO;
        for (String rowKey : amountPercentageMap.keySet()) {
            if (UtilValidate.isNotEmpty(amountPercentageMap.get(rowKey))) {
                BigDecimal rowValue = new BigDecimal(amountPercentageMap.get(rowKey));
                if (rowValue != null)
                    totalAmountPercentage = totalAmountPercentage.add(rowValue);
            }
        }
        return totalAmountPercentage;
    }
    
    public Map generateTallyVoucherXML(DispatchContext dctx, Map serviceContext) throws GenericEntityException, IOException {
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	Delegator delegator = dctx.getDelegator();
    	GenericValue userLogin = (GenericValue) serviceContext.get("userLogin");
    	String invoiceId = (String) serviceContext.get("invoiceId");
    	List<GenericValue> acctgTransAndEntries = null;
    	GenericValue orderHeader=null;
    	GenericValue invoice = null;
    	try {
    		invoice = delegator.findOne("Invoice", true, UtilMisc.toMap("invoiceId",invoiceId));
    		orderHeader = EntityUtil.getFirst(delegator.findByAnd("OrderItemBilling",UtilMisc.toMap("invoiceId",invoiceId))).getRelatedOne("OrderHeader");
    		acctgTransAndEntries = delegator.findList("AcctgTransAndEntries",EntityCondition.makeCondition("invoiceId",EntityOperator.EQUALS,invoiceId),
    				null,UtilAccounting.isPurchaseInvoice(invoice)?UtilMisc.toList("-debitCreditFlag","acctgTransEntrySeqId"):UtilMisc.toList("debitCreditFlag","acctgTransEntrySeqId"),null,true);
    	} catch (Exception e1) {
    		e1.printStackTrace();
    	}
    	String basePath = System.getProperty("ofbiz.home") + File.separator + "Vouchers";
    	List<File> voucherFileList = new ArrayList<File>();
    	FileWriter writer = null;
    	Map<String, Object> context = new HashMap<String, Object>();
    	
    	File f = new File(basePath, "Invoice_" + invoiceId + ".xml");
		voucherFileList.add(f);
		writer = new FileWriter(f);
		
    	context.put("invoice", invoice);
    	context.put("orderHeader",orderHeader);
    	context.put("acctgTransAndEntries",acctgTransAndEntries);
    	context.put("delegator", delegator);
    	
    	try {
			
			Configuration cfg = new Configuration();
			cfg.setDateFormat("dd-MMM-yyyy");
			cfg.setNumberFormat("##,###.##");

			FileTemplateLoader loader = new FileTemplateLoader();
			cfg.setTemplateLoader(loader);
			Template template = new Template("TransactionVoucher.ftl", new FileReader(new File(System.getProperty("ofbiz.home")
					+ File.separator + "applications/accounting/config/TransactionVoucher.ftl")), cfg);
			template.process(context, writer);
			writer.flush();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
    	Map m = ServiceUtil.returnSuccess();
    	m.put("voucherFileList", voucherFileList);
    	return m;
    	}

    
    
}