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

import java.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.base.util.*;
import org.ofbiz.base.util.collections.*;
import org.ofbiz.accounting.invoice.*;
import org.ofbiz.accounting.payment.*;
import org.ofbiz.accounting.util.UtilAccounting;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityConditionList;
import java.math.*;


 conditionList = [];
 List bothPaymentLists =new ArrayList();
 if("outgoing".equals(parameters.type)){
	conditionList.add(EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS, "DISBURSEMENT"));
	conditionList.add(EntityCondition.makeCondition("parentTypeId",EntityOperator.EQUALS, "TAX_PAYMENT"));
	condition = EntityCondition.makeCondition(conditionList,EntityOperator.OR);
	bothPaymentLists = delegator.findList("PaymentType",condition,null,null,null,true);
 }
 if("incoming".equals(parameters.type)){
	conditionList.add(EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS, "RECEIPT"));
	conditionList.add(EntityCondition.makeCondition("parentTypeId",EntityOperator.EQUALS, "TAX_PAYMENT"));
	condition = EntityCondition.makeCondition(conditionList,EntityOperator.OR);
	bothPaymentLists = delegator.findList("PaymentType",condition,null,null,null,true);
 }
	context.bothPaymentLists = bothPaymentLists;
