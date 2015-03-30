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

import org.ofbiz.service.ServiceUtil;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.*;
import java.text.SimpleDateFormat;
import java.math.BigDecimal;

facilityId = parameters.facilityId;

// fields to search by
productId = parameters.productId ? parameters.productId.trim() : null;
internalName = parameters.internalName ? parameters.internalName.trim() : null;
locationSeqId = parameters.locationSeqId ? parameters.locationSeqId.trim() : null;
manufacturingDateStr = parameters.manufacturingDate ? parameters.manufacturingDate.trim() : null;
datetimeReceivedStr = parameters.datetimeReceived ? parameters.datetimeReceived.trim() : null;
SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

// build conditions
conditions = [EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId),
              EntityCondition.makeCondition("inventoryItemTypeId", EntityOperator.EQUALS, "NON_SERIAL_INV_ITEM")
             ];
if (productId) {
    conditions.add(EntityCondition.makeCondition("productId", EntityOperator.LIKE, productId + "%"));
}
if (internalName) {
    conditions.add(EntityCondition.makeCondition("internalName", EntityOperator.LIKE, internalName + "%"));
}
if(locationSeqId){
	conditions.add(EntityCondition.makeCondition("locationSeqId", EntityOperator.EQUALS, locationSeqId));
}
if(manufacturingDateStr){
  java.util.Date parsedDate = dateFormat.parse(manufacturingDateStr);
  java.sql.Timestamp manufacturingDate = new java.sql.Timestamp(parsedDate.getTime());
  conditions.add(EntityCondition.makeCondition("datetimeManufactured", EntityOperator.GREATER_THAN_EQUAL_TO, manufacturingDate));
}
if(datetimeReceivedStr){
  java.util.Date parsedDate = dateFormat.parse(datetimeReceivedStr);
  java.sql.Timestamp datetimeReceived = new java.sql.Timestamp(parsedDate.getTime());
  conditions.add(EntityCondition.makeCondition("datetimeReceived", EntityOperator.GREATER_THAN_EQUAL_TO, datetimeReceived));
}
conditions.add(EntityCondition.makeCondition("quantityOnHandTotal", EntityOperator.GREATER_THAN, BigDecimal.ZERO));
conditions.add(EntityCondition.makeCondition("availableToPromiseTotal", EntityOperator.GREATER_THAN,BigDecimal.ZERO));
if (conditions.size() > 0) {
    ecl = EntityCondition.makeCondition(conditions, EntityOperator.AND);
    physicalInventory = delegator.findList("ProductInventoryItem", ecl, null, ['productId'], null, false);

    // also need the overal product QOH and ATP for each product
    atpMap = [:];
    qohMap = [:];

    // build a list of productIds
    productIds = [] as Set;
    physicalInventory.each { iter ->
        productIds.add(iter.productId);
    }
    
    // for each product, call the inventory counting service
    productIds.each { productId ->
        result = dispatcher.runSync("getInventoryAvailableByFacility", [facilityId : facilityId, productId : productId]);
        if (!ServiceUtil.isError(result)) {
            atpMap.put(productId, result.availableToPromiseTotal);
             qohMap.put(productId, result.quantityOnHandTotal);
        }
    }

    // associate the quantities to each row and store the combined data as our list
    physicalInventoryCombined = [];
    physicalInventory.each { iter ->
        row = iter.getAllFields();
        row.productATP = atpMap.get(row.productId);
        row.productQOH = qohMap.get(row.productId);
        physicalInventoryCombined.add(row);
   }
    context.physicalInventory = physicalInventoryCombined;
}