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

import javolution.util.FastList;
import java.util.ArrayList;
import java.util.List;
import java.text.SimpleDateFormat;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.webapp.control.Infrastructure;
import org.sme.order.util.OrderMgrUtil;

GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();
List conditions = FastList.newInstance();


Boolean isRequirementApprover(String partyId) {
	GenericDelegator delegator = Infrastructure.getDelegator();
	EntityCondition condition = EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition(
			EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId), EntityOperator.AND,
			EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "REQ_APPROVER"))));

	List<GenericValue> partyRole = null;
	try {
		partyRole = delegator.findList("PartyRole", condition, UtilMisc.toSet("partyId"), null, null, false);
	} catch (GenericEntityException e) {
		e.printStackTrace();
	}
	return partyRole.size() > 0;
}

List<String> requirementCreators = new ArrayList<String>();
requirementCreators.add(userLogin.getString("partyId"));
Boolean requirementManager = OrderMgrUtil.isRequirementManger(userLogin.getString("partyId"));
Boolean requirementsRepresentative = OrderMgrUtil.isRequirementsRepresentative(userLogin.getString("partyId"));
Boolean isRequirementApprover = isRequirementApprover(userLogin.getString("partyId"));
Boolean isRequirementProposer = OrderMgrUtil.isRequirementProposer(userLogin.getString("partyId"));
SimpleDateFormat dateFormat = new SimpleDateFormat(UtilDateTime.DATE_FORMAT);

java.util.Date parsedDate2 = new java.util.Date();
if(UtilValidate.isNotEmpty(request.getParameter("requirementStartDate"))){
	parsedDate2 = dateFormat.parse(request.getParameter("requirementStartDate"));
	java.sql.Timestamp requirementStartDate =  UtilDateTime.getDayStart( new java.sql.Timestamp(parsedDate2.getTime()) );
	conditions.add(EntityCondition.makeCondition("requirementStartDate", EntityOperator.GREATER_THAN_EQUAL_TO, requirementStartDate));
}
java.util.Date parsedDate1 = new java.util.Date();
if(UtilValidate.isNotEmpty(request.getParameter("requiredByDate"))){
	parsedDate1 = dateFormat.parse(request.getParameter("requiredByDate"));
	java.sql.Timestamp requirementByDate = UtilDateTime.getDayStart( new java.sql.Timestamp(parsedDate1.getTime()) );
	conditions.add(EntityCondition.makeCondition("requiredByDate", EntityOperator.LESS_THAN_EQUAL_TO, requirementByDate));
}
if (UtilValidate.isNotEmpty(request.getParameter("requirementId"))) {
	conditions.add(EntityCondition.makeCondition("requirementId", EntityOperator.EQUALS, request.getParameter("requirementId")));
}
if (UtilValidate.isNotEmpty(request.getParameter("requirementTypeId"))) {
	conditions.add(EntityCondition.makeCondition("requirementTypeId", EntityOperator.EQUALS, request.getParameter("requirementTypeId")));
}
if (UtilValidate.isNotEmpty(request.getParameter("productId"))) {
	conditions.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, request.getParameter("productId")));
}
if (UtilValidate.isNotEmpty(request.getParameter("facilityId"))) {
	conditions.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, request.getParameter("facilityId")));
}
if (UtilValidate.isNotEmpty(request.getParameter("statusId"))) {
	conditions.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, request.getParameter("statusId")));
}
if(UtilValidate.isEmpty(request.getParameter("statusId"))){
	conditions.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "REQ_APPROVED"));
	conditions.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "REQ_ORDERED"));
	conditions.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "REQ_REJECTED"));
	}
if (UtilValidate.isNotEmpty(request.getParameter("description"))) {
	conditions.add(EntityCondition.makeCondition("description", EntityOperator.EQUALS, request.getParameter("description")));
}
List<GenericValue> requirementHeaderList = new ArrayList<GenericValue>();
/*if (requirementManager || requirementsRepresentative) {
	requirementCreators.addAll(OrderMgrUtil.getManagerRelationship(userLogin.getString("partyId")));
	requirementCreators = OrderMgrUtil.getUserLoginIds(requirementCreators);
	conditions.add(EntityCondition.makeCondition("createdByUserLogin", EntityOperator.IN, requirementCreators));
	try {
		requirementHeaderList = delegator.findList("Requirement", EntityCondition.makeCondition(conditions,EntityOperator.AND), null, null, null, false);
		if(UtilValidate.isNotEmpty(requirementHeaderList)){
			context.requirements =requirementHeaderList;
		}
	} catch (GenericEntityException e) {
		e.printStackTrace();
	}
}*/
if(isRequirementApprover || isRequirementProposer){
	requirementHeaderList = delegator.findList("Requirement", EntityCondition.makeCondition(conditions,EntityOperator.AND), null, null, null, false);
}
if(UtilValidate.isNotEmpty(requirementHeaderList)){
	context.requirements =requirementHeaderList;
}

context.isRequirementApprover = isRequirementApprover;
context.isRequirementProposer = isRequirementProposer;

return "success";
