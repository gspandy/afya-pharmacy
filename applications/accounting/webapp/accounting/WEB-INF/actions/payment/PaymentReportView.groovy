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

import org.ofbiz.entity.util.EntityUtil;
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
import java.text.SimpleDateFormat;

GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();

List paymentApplsList = FastList.newInstance();

if(UtilValidate.isNotEmpty(request.getParameter("paymentId")))
	paymentApplsList.add(EntityCondition.makeCondition("paymentId", EntityOperator.EQUALS, request.getParameter("paymentId")));
	
if(UtilValidate.isNotEmpty(request.getParameter("statusId")))
	paymentApplsList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, request.getParameter("statusId")));
	
if(UtilValidate.isNotEmpty(request.getParameter("paymentTypeId")))
	paymentApplsList.add(EntityCondition.makeCondition("paymentTypeId", EntityOperator.EQUALS, request.getParameter("paymentTypeId")));
   
List<String> orderByList = new ArrayList<String>();
orderByList.add("effectiveDate");

List paymentApplsLists = delegator.findList("Payment", EntityCondition.makeCondition(paymentApplsList, EntityOperator.AND), null, orderByList, null, false);
context.paymentApplsLists = paymentApplsLists;