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

import org.ofbiz.entity.GenericValue;

inspectionResult = parameters.inspectionResult;

String supplierId = parameters.partyId;

// search by productId is mandatory
conditions = [];
if(UtilValidate.isNotEmpty(supplierId))
conditions.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, supplierId) );
if(UtilValidate.isNotEmpty(inspectionResult))
    conditions.add(EntityCondition.makeCondition("inspectionResult", EntityOperator.EQUALS, inspectionResult) );
if (fromDate) {
    conditions.add(EntityCondition.makeCondition("createdDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.getDayStart(fromDate)));
}
if (thruDate) {
    conditions.add(EntityCondition.makeCondition("createdDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.getDayEnd(thruDate)));
}
// get the results as an entity list iterator
conditions.add(EntityCondition.makeCondition([EntityCondition.makeCondition("shipmentTypeId", EntityOperator.EQUALS, "PURCHASE_SHIPMENT")], EntityOperator.AND));
allConditions = EntityCondition.makeCondition( conditions, EntityOperator.AND );

fieldsToSelect = [ "shipmentId","shipmentTypeId","statusId","primaryOrderId","primaryShipGroupSeqId","partyIdFrom","partyIdTo","inspectionResult","inspectionNote","inspectedBy","approvedBy","createdDate"] as Set;
fieldsToSelect.add("shipmentId");
fieldsToSelect.add("shipmentTypeId");
fieldsToSelect.add("statusId");
fieldsToSelect.add("primaryOrderId");
fieldsToSelect.add("primaryShipGroupSeqId");
fieldsToSelect.add("partyIdFrom");
fieldsToSelect.add("partyIdTo");
fieldsToSelect.add("inspectionResult");
fieldsToSelect.add("inspectionNote");
fieldsToSelect.add("statusId");
fieldsToSelect.add("inspectedBy");
fieldsToSelect.add("approvedBy");
fieldsToSelect.add("createdDate");
findOptions = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, true);
listIt = delegator.find("Shipment", allConditions, null, fieldsToSelect, ["createdDate DESC"], null);
shipmentInspectionList = [];
listIt.each { listValue ->
    shipmentId = listValue.shipmentId;
    shipmentTypeId = listValue.shipmentTypeId;
    statusId = listValue.statusId;
    statusItemsGv = delegator.findByPrimaryKey("StatusItem", UtilMisc.toMap("statusId", statusId));
    orderItemSeqId = listValue.orderItemSeqId;
    purchaseOrderId = listValue.primaryOrderId;
    datetimeReceived = null;
    shipmentReceipts = delegator.findByAnd("ShipmentReceipt", UtilMisc.toMap("shipmentId", shipmentId, "orderId", purchaseOrderId));
    if(UtilValidate.isNotEmpty(shipmentReceipts)) {
        shipmentReceipt = shipmentReceipts.get(0);
        datetimeReceived = shipmentReceipt.datetimeReceived;
    }
    primaryShipGroupSeqId = listValue.primaryShipGroupSeqId;
    partyIdFrom = listValue.partyIdFrom;
    GenericValue partyGroupGv = delegator.findOne("PartyGroup", UtilMisc.toMap("partyId",partyIdFrom), false);
    supplierName = partyGroupGv.getString("groupName");
    partyIdTo = listValue.partyIdTo;
    inspectionResult = listValue.inspectionResult;
    inspectionNote = listValue.inspectionNote;
    inspectedBy = listValue.inspectedBy;
    approvedBy = listValue.approvedBy;
    createdDate = listValue.createdDate;

    shipmentInspectionMap = [
                    shipmentId : shipmentId,
                    shipmentTypeId : shipmentTypeId,
                    datetimeReceived : datetimeReceived,
                    statusId : statusItemsGv.get("description"),
                    orderItemSeqId : orderItemSeqId,
                    purchaseOrderId : purchaseOrderId,
                    primaryShipGroupSeqId : primaryShipGroupSeqId,
                    partyIdFrom : partyIdFrom,
                    supplierName : supplierName,
                    partyIdTo : partyIdTo,
                    inspectionResult : inspectionResult,
                    inspectionNote : inspectionNote,
                    inspectedBy : inspectedBy,
                    approvedBy : approvedBy,
                    createdDate : createdDate
                   ]
	if(UtilValidate.isNotEmpty(datetimeReceived))
    	shipmentInspectionList.add(shipmentInspectionMap);
}
listIt.close();
context.shipmentInspectionList = shipmentInspectionList;
context.fromDate = fromDate;
context.thruDate = thruDate;
