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

/*
 * Script to build the open order item report using
 * the OrderItemQuantityReportGroupByItem view.
 */


import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.base.util.*;
import java.text.SimpleDateFormat;
import java.util.List;

import org.ofbiz.entity.GenericValue;

productId = parameters.productId;

String customerId = parameters.partyId;

// search by productId is mandatory
conditions = [];
EntityCondition dateCon1;
EntityCondition dateCon2;
EntityCondition dateCon3;
if(UtilValidate.isNotEmpty(productId))
conditions.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId) );
if(UtilValidate.isNotEmpty(fromOrderDate)){
     dateCon1 = EntityCondition.makeCondition("estimatedDeliveryDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.getDayStart(fromOrderDate));
     conditions.add(dateCon1);
     }
if(UtilValidate.isNotEmpty(thruOrderDate)){
     dateCon2 = EntityCondition.makeCondition("estimatedDeliveryDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.getDayEnd(thruOrderDate));
      conditions.add(dateCon2);
     }
if((UtilValidate.isNotEmpty(fromOrderDate))&&(UtilValidate.isNotEmpty(thruOrderDate))){
if (fromOrderDate && thruOrderDate) {
     dateCon3 = EntityCondition.makeCondition([dateCon1,dateCon2],EntityOperator.OR )
    conditions.add(dateCon3);
}
}
// get the results as an entity list iterator
conditions.add(EntityCondition.makeCondition([EntityCondition.makeCondition("orderTypeId", EntityOperator.NOT_EQUAL, "PURCHASE_ORDER")], EntityOperator.AND));
allConditions = EntityCondition.makeCondition( conditions, EntityOperator.AND );

fieldsToSelect = [ "productId","itemDescription","orderId","unitPrice","estimatedShipDate","estimatedDeliveryDate","orderStatusId","quantity"] as Set;
fieldsToSelect.add("productId");
fieldsToSelect.add("itemDescription");
fieldsToSelect.add("orderId");
fieldsToSelect.add("unitPrice");
fieldsToSelect.add("estimatedShipDate");
fieldsToSelect.add("estimatedDeliveryDate");
fieldsToSelect.add("orderStatusId");
fieldsToSelect.add("quantity");
findOptions = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, true);
listIt = delegator.find("OrderHeaderAndItems", allConditions, null, fieldsToSelect, ["estimatedDeliveryDate DESC"], null);
orderItemList = [];
listIt.each { listValue ->
    productId = listValue.productId;
    itemDescription = listValue.itemDescription;
    orderId = listValue.orderId;
    unitPrice = listValue.unitPrice;
    estimatedDeliveryDate = listValue.estimatedDeliveryDate!=null?UtilDateTime.format(listValue.get("estimatedDeliveryDate")):"";
    estimatedShipDate = listValue.estimatedShipDate;
    orderStatusId = listValue.orderStatusId;
    quantity = listValue.quantity;
    statusItemsGv = delegator.findByPrimaryKey("StatusItem", UtilMisc.toMap("statusId", orderStatusId));
    List orderRoleGvs = delegator.findByAnd("OrderRole", UtilMisc.toMap("orderId", orderId, "roleTypeId", "BILL_TO_CUSTOMER"));
    String customerName = "";
    String partyId = "";
    if(UtilValidate.isNotEmpty(orderRoleGvs)){
        GenericValue orderRoleGv = orderRoleGvs.get(0);
        partyId=orderRoleGv.getString("partyId");
        GenericValue partyGroupGv = delegator.findOne("PartyGroup", UtilMisc.toMap("partyId",partyId), false);
        customerName = partyGroupGv.getString("groupName");
    }
    orderItemMap = [
                   itemDescription : itemDescription,
                    productId : productId,
                    orderId : orderId,
                    unitPrice : unitPrice,
                    estimatedDeliveryDate : estimatedDeliveryDate,
                    estimatedShipDate : estimatedShipDate,
                    quantity : quantity,
                    customerName : customerName,
                    orderStatusId : statusItemsGv.get("description")
                   ]
    if(UtilValidate.isEmpty(customerId) || customerId.equalsIgnoreCase(partyId)){
        orderItemList.add(orderItemMap);
    }
}
listIt.close();
context.orderItemList = orderItemList;
