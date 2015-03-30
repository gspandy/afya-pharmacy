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

import org.ofbiz.service.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.*;
import org.ofbiz.order.shoppingcart.*;
import org.ofbiz.party.party.PartyWorker;
import org.ofbiz.product.catalog.CatalogWorker;
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.order.shoppingcart.product.ProductDisplayWorker;
import org.ofbiz.order.shoppingcart.product.ProductPromoWorker;

productStore = ProductStoreWorker.getProductStore(request);
if (productStore) {
    context.defaultProductStore = productStore;
    if (productStore.defaultSalesChannelEnumId)
        context.defaultSalesChannel = delegator.findByPrimaryKeyCache("Enumeration", [enumId : productStore.defaultSalesChannelEnumId]);
}
// Get the Cart
shoppingCart = session.getAttribute("shoppingCart");
context.shoppingCart = shoppingCart;

salesChannels = delegator.findByAndCache("Enumeration", [enumTypeId : "ORDER_SALES_CHANNEL"], ["sequenceId"]);
context.salesChannels = salesChannels;

productStores = delegator.findList("ProductStore", null, null, ["storeName"], null, true);
context.productStores = productStores;

suppliers = delegator.findByAnd("PartyRoleAndPartyDetail", [roleTypeId : "SUPPLIER"], ["groupName", "partyId"]);
context.suppliers = suppliers;

organizations = delegator.findByAnd("PartyRole", [roleTypeId : "INTERNAL_ORGANIZATIO"]);
context.organizations = organizations;

// Fixed for Order Approval Role based
GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
String partyId = userLogin.getString("partyId");
roleType = delegator.findList("PartyRole",EntityCondition.makeCondition([partyId : partyId]),null,null,null,false);
EntityCondition condition = EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition(
				EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId), EntityOperator.AND,
				EntityCondition.makeCondition("roleTypeId", EntityOperator.IN, UtilMisc.toList("SALES_MGR","SALES_REP")))));
List roleTypeNew = delegator.findList("PartyRole", condition, UtilMisc.toSet("partyId","roleTypeId"), null, null, false);
context.roleTypeNew = roleTypeNew;
context.userLogin = userLogin;

EntityCondition conditionPurchase = EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition(
				EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId), EntityOperator.AND,
				EntityCondition.makeCondition("roleTypeId", EntityOperator.IN, UtilMisc.toList("MANAGER","ORDER_CLERK")))));
List roleTypePurchase = delegator.findList("PartyRole", conditionPurchase, UtilMisc.toSet("partyId","roleTypeId"), null, null, false);
context.roleTypePurchase = roleTypePurchase;

EntityCondition purchaseGlAccountCondition = EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition(
	EntityCondition.makeCondition("parentGlAccountId", EntityOperator.EQUALS, "3000"))));
List purchaseAccounts = delegator.findList("GlAccount",purchaseGlAccountCondition,null,null,null,false);
context.purchaseAccounts=purchaseAccounts;

EntityCondition salesGlAccountCondition = EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition(
	EntityCondition.makeCondition("parentGlAccountId", EntityOperator.EQUALS, "4000"))));
List salesAccounts = delegator.findList("GlAccount",salesGlAccountCondition,null,null,null,false);
context.salesAccounts=salesAccounts;

List<GenericValue> organizationList = [];
for(GenericValue organization : organizations){
 partyGroupGv = delegator.findOne("PartyGroup",["partyId":organization.getString("partyId")],true);
 organizationList.add(partyGroupGv);
}
context.organizationList = organizationList;

voucherTypePO = delegator.findList("VoucherType", EntityCondition.makeCondition("voucherParentId", EntityOperator.EQUALS, "Purchase"),null,null,null,false);
context.voucherTypePO = voucherTypePO;

voucherTypeSO = delegator.findList("VoucherType", EntityCondition.makeCondition("voucherParentId", EntityOperator.EQUALS, "Sales"),null,null,null,false);
context.voucherTypeSO = voucherTypeSO;
