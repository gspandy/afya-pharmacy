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


import javolution.util.FastMap
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityFindOptions
import org.ofbiz.entity.util.EntityUtil

purchaseOrderId = parameters.purchaseOrderId;
shipmentId = parameters.shipmentId;


shipmentReceiptAndItems = delegator.findByAnd("ShipmentReceiptAndItem", ["orderId":purchaseOrderId, "shipmentId":shipmentId]);
orderId = shipmentReceiptAndItems.get(0).get("orderId");
shipmentId = shipmentReceiptAndItems.get(0).get("shipmentId");
shipmentGv = delegator.findByPrimaryKey("Shipment", ["shipmentId":shipmentId]);
partyGroupGv = delegator.findByPrimaryKey("PartyGroup", ["partyId":shipmentGv.partyIdFrom]);

//telephone
phones = delegator.findByAnd("PartyContactMechPurpose", [partyId : partyGroupGv.partyId, contactMechPurposeTypeId : "PRIMARY_PHONE"]);
selPhones = EntityUtil.filterByDate(phones, nowTimestamp, "fromDate", "thruDate", true);
if (selPhones) {
	context.phone = delegator.findByPrimaryKey("TelecomNumber", [contactMechId : selPhones[0].contactMechId]);
}  else{
	context.phone = [:];
}

//Email
emails = delegator.findByAnd("PartyContactMechPurpose", [partyId : partyGroupGv.partyId, contactMechPurposeTypeId : "PRIMARY_EMAIL"]);
selEmails = EntityUtil.filterByDate(emails, nowTimestamp, "fromDate", "thruDate", true);
if (selEmails) {
	context.email = delegator.findByPrimaryKey("ContactMech", [contactMechId : selEmails[0].contactMechId]);
} else {    //get email address from party contact mech
	contacts = delegator.findByAnd("PartyContactMech", [partyId : partyGroupGv.partyId]);
	selContacts = EntityUtil.filterByDate(contacts, nowTimestamp, "fromDate", "thruDate", true);
	if (selContacts) {
		i = selContacts.iterator();
		while (i.hasNext())    {
			email = i.next().getRelatedOne("ContactMech");
			if ("ELECTRONIC_ADDRESS".equals(email.contactMechTypeId))    {
				context.email = email;
				break;
			}
		}
	}
}
