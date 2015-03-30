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


import org.ofbiz.entity.GenericValue
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.*;
import java.text.SimpleDateFormat;
import java.math.BigDecimal;

facilityId = parameters.facilityId;
datetimeReceivedStr = parameters.datetimeReceived ? parameters.datetimeReceived.trim() : null;
SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

// fields to search by
productId = null;
if (UtilValidate.isNotEmpty(parameters.productId))
    productId = parameters.productId;

// build conditions
conditions = [];
if (UtilValidate.isNotEmpty(facilityId))
    conditions.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId));
if (productId)
    conditions.add(EntityCondition.makeCondition("productId", EntityOperator.LIKE, productId + "%"));
if (UtilValidate.isNotEmpty(parameters.requisitionId))
    conditions.add(EntityCondition.makeCondition("invRequisitionId", EntityOperator.EQUALS, parameters.requisitionId));

if (datetimeReceivedStr) {
    java.util.Date parsedDate = dateFormat.parse(datetimeReceivedStr);
    java.sql.Timestamp datetimeReceived = new java.sql.Timestamp(parsedDate.getTime());
    conditions.add(EntityCondition.makeCondition("datetimeReceived", EntityOperator.GREATER_THAN_EQUAL_TO, datetimeReceived));
}

conditions.add(EntityCondition.makeCondition("quantityOnHandTotal", EntityOperator.GREATER_THAN, BigDecimal.ZERO));
conditions.add(EntityCondition.makeCondition("availableToPromiseTotal", EntityOperator.GREATER_THAN, BigDecimal.ZERO));
//conditions.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "IXF_COMPLETE"));

if (conditions.size() > 0) {
    ecl = EntityCondition.makeCondition(conditions, EntityOperator.AND);
    physicalInventory = delegator.findList("InvItemAndInvTransfer", ecl, null, null, null, false);
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
        result = dispatcher.runSync("getInventoryAvailableByFacility", [facilityId: facilityId, productId: productId]);
        if (!ServiceUtil.isError(result)) {
            atpMap.put(productId, result.availableToPromiseTotal);
            qohMap.put(productId, result.quantityOnHandTotal);
        }
    }

    // associate the quantities to each row and store the combined data as our list
    physicalInventoryCombined = [];
    physicalInventory.each { iter ->
        GenericValue productGv = delegator.findOne("Product", UtilMisc.toMap("productId", iter.productId), false);
        if ( !"Y".equals(productGv.getString("returnable")) ) {
            row = iter.getAllFields();
            row.productATP = atpMap.get(row.productId);
            row.productQOH = qohMap.get(row.productId);
            physicalInventoryCombined.add(row);
        }
    }
    context.physicalInventory = physicalInventoryCombined;
}