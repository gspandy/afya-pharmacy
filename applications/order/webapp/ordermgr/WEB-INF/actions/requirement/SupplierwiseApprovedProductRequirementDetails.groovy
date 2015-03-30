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

import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.party.contact.ContactMechWorker;

requirementId = parameters.requirementId;
context.requirementId = requirementId;

supplierPartyId = parameters.partyId;
context.supplierPartyId = supplierPartyId;

requirement = delegator.findByPrimaryKey("Requirement", [requirementId : requirementId]);
context.requirement = requirement;

supplierProduct = EntityUtil.getFirst(delegator.findByAnd("SupplierProduct", [partyId : supplierPartyId, productId : requirement.productId]));
context.supplierProduct = supplierProduct;

conditionList = [];
conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, requirement.productId));
conditionList.add(EntityCondition.makeCondition("productCategoryTypeId", EntityOperator.NOT_EQUAL, "TAX_CATEGORY"));
condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
productCategoryAndMember = delegator.findList("ProductCategoryAndMember",condition,null,null,null,true);
context.productCategoryAndMember = productCategoryAndMember;

if (supplierPartyId) {
	supplierContactMechValueMaps = ContactMechWorker.getPartyContactMechValueMaps(delegator, supplierPartyId, false, "POSTAL_ADDRESS");
	context.supplierContactMechValueMaps = supplierContactMechValueMaps;
	supplierContactMechValueMaps.each { supplierContactMechValueMap ->
		contactMechPurposes = supplierContactMechValueMap.partyContactMechPurposes;
		contactMechPurposes.each { contactMechPurpose ->
			if (contactMechPurpose.contactMechPurposeTypeId.equals("GENERAL_LOCATION")) {
				context.supplierGeneralContactMechValueMap = supplierContactMechValueMap;
			} else if (contactMechPurpose.contactMechPurposeTypeId.equals("SHIPPING_LOCATION")) {
				context.supplierShippingContactMechValueMap = supplierContactMechValueMap;
			} else if (contactMechPurpose.contactMechPurposeTypeId.equals("BILLING_LOCATION")) {
				context.supplierBillingContactMechValueMap = supplierContactMechValueMap;
			} else if (contactMechPurpose.contactMechPurposeTypeId.equals("PAYMENT_LOCATION")) {
				context.supplierPaymentContactMechValueMap = supplierContactMechValueMap;
			}
		}
	}
}
