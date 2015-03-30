/*
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
 */

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.accounting.util.UtilAccounting;
import org.ofbiz.base.util.*;
import javolution.util.FastList;
import org.ofbiz.accounting.invoice.*;
import java.text.SimpleDateFormat;
import org.ofbiz.base.util.UtilDateTime;


GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();

List invoiceList = FastList.newInstance();
if(UtilValidate.isNotEmpty(request.getParameter("invoiceTypeId")))
	invoiceList.add(EntityCondition.makeCondition("invoiceTypeId", EntityOperator.EQUALS, request.getParameter("invoiceTypeId")));
	
if(UtilValidate.isNotEmpty(request.getParameter("statusId")))
	invoiceList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, request.getParameter("statusId")));
	
if(UtilValidate.isNotEmpty(fromInvoiceDate)){
	invoiceList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.GREATER_THAN_EQUAL_TO,UtilDateTime.getDayStart(fromInvoiceDate)));
}
if(UtilValidate.isNotEmpty(thruInvoiceDate)){
	invoiceList.add(EntityCondition.makeCondition("invoiceDate", EntityOperator.LESS_THAN_EQUAL_TO,UtilDateTime.getDayEnd(thruInvoiceDate)));
}
    fromPartyCond = EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, "Company");
    toPartyCond = EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, "Company");
    EntityCondition combineCondition = EntityCondition.makeCondition([fromPartyCond,toPartyCond],EntityOperator.OR)
    invoiceList.add(combineCondition);
    
List<String> orderByList = new ArrayList<String>();
orderByList.add("invoiceDate");
List invoiceLists = delegator.findList("InvoiceAndType", EntityCondition.makeCondition(invoiceList, EntityOperator.AND), null, orderByList, null, false);

List<Map<String, Object>> listOfInvoices = new ArrayList<Map<String, Object>>();
Map<String, Object> totalResults = new HashMap<String, Object>();
   
for(GenericValue invoiceGv : invoiceLists){
     totalResults = dispatcher.runSync("getInvoicePaymentInfoList", [invoiceId:invoiceGv.invoiceId,userLogin:userLogin]);
     Map<String, Object> invoiceMap = new HashMap<String, Object>();
     Map m1 = (totalResults.invoicePaymentInfoList).get(0);
      if ("PURCHASE_INVOICE".equals(invoiceGv.invoiceTypeId)||"CUST_RTN_INVOICE".equals(invoiceGv.invoiceTypeId)||"PAYROL_INVOICE".equals(invoiceGv.invoiceTypeId)||"COMMISSION_INVOICE".equals(invoiceGv.invoiceTypeId))  {
      party = delegator.findOne("PartyNameView", [partyId : invoiceGv.partyIdFrom], false);
     invoiceMap.put("groupName",party.groupName);
     }
     if ("SALES_INVOICE".equals(invoiceGv.invoiceTypeId)||"INTEREST_INVOICE".equals(invoiceGv.invoiceTypeId)||"PURC_RTN_INVOICE".equals(invoiceGv.invoiceTypeId)) { 
        party = delegator.findOne("PartyNameView", [partyId : invoiceGv.partyId], false);
     invoiceMap.put("groupName",party.groupName);
     }
    invoiceMap.put("invoiceTypeId",invoiceGv.invoiceTypeDesc);
    invoiceMap.put("invoiceDate",UtilDateTime.format(invoiceGv.invoiceDate));
    invoiceMap.put("totalAmount",m1.amount);
    invoiceMap.put("paidAmount",m1.paidAmount);
    invoiceMap.put("outstandingAmount",m1.outstandingAmount);
    if(m1.invoiceTermId!=null)
    invoiceMap.put("dueDate",UtilDateTime.format(m1.dueDate));
    listOfInvoices.add(invoiceMap);
}
 

context.listOfInvoices = listOfInvoices;
context.invoiceItemList = invoiceLists;
 



