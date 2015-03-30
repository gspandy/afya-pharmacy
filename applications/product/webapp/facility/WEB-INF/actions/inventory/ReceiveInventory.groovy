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


import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityUtil
import org.ofbiz.service.ServiceUtil

import java.math.RoundingMode
import java.sql.Timestamp

facilityId = request.getParameter("facilityId");
purchaseOrderId = request.getParameter("purchaseOrderId");
productId = request.getParameter("productId");
shipmentId = request.getParameter("shipmentId");
partialReceive = parameters.partialReceive;
GenericValue shipmentGv = delegator.findOne("Shipment",false,UtilMisc.toMap("shipmentId",shipmentId));
if(UtilValidate.isEmpty(purchaseOrderId)){
	purchaseOrderId = parameters.orderId;
}
if(UtilValidate.isEmpty(facilityId)){
	facilityId = parameters.facilityId;
}

if (UtilValidate.isNotEmpty(purchaseOrderId)) {
    OrderGV = delegator.findOne("OrderHeader", ["orderId": purchaseOrderId], false);
    if (UtilValidate.isEmpty(OrderGV)) {
        invalidOrderId = "No Order found with Order ID: [" + purchaseOrderId + "]";
        context.invalidOrderId = invalidOrderId;

        return "error";
    }
}

if (partialReceive) {
    context.partialReceive = partialReceive;
}
facility = null;
if (facilityId) {
    facility = delegator.findOne("Facility", [facilityId: facilityId], false);
}
ownerAcctgPref = null;
if (facility) {
    owner = facility.getRelatedOne("OwnerParty");
    if (owner) {
        result = dispatcher.runSync("getPartyAccountingPreferences", [organizationPartyId: owner.partyId, userLogin: request.getAttribute("userLogin")]);
        if (!ServiceUtil.isError(result) && result.partyAccountingPreference) {
            ownerAcctgPref = result.partyAccountingPreference;
        }
    }
}

purchaseOrder = null;
if (purchaseOrderId) {
    purchaseOrder = delegator.findOne("OrderHeader", [orderId: purchaseOrderId], false);
    if (purchaseOrder && !"PURCHASE_ORDER".equals(purchaseOrder.orderTypeId)) {
        purchaseOrder = null;
    }
}
if (purchaseOrderId) {
    purchaseOrder = delegator.findOne("OrderHeader", [orderId: purchaseOrderId], false);
    if ("ORDER_CREATED".equals(purchaseOrder.statusId)) {
        request.setAttribute("_ERROR_MESSAGE_", "Order is not yet Approved. Cannot Receive");
        return "error";
    }
}
product = null;
if (productId) {
    product = delegator.findOne("Product", [productId: productId], false);
}

shipments = null;
if (purchaseOrder && !shipmentId) {
    orderShipments = delegator.findList("OrderShipment", EntityCondition.makeCondition([orderId: purchaseOrderId]), null, null, null, false);
    if (orderShipments) {
        shipments = [] as TreeSet;
        orderShipments.each { orderShipment ->
            shipment = orderShipment.getRelatedOne("Shipment");
            if (!"PURCH_SHIP_RECEIVED".equals(shipment.statusId) &&
                    !"SHIPMENT_CANCELLED".equals(shipment.statusId) &&
                    (!shipment.destinationFacilityId || facilityId.equals(shipment.destinationFacilityId))) {
                shipments.add(shipment);
            }
        }
    }
    // This is here for backward compatibility: ItemIssuances are no more created for purchase shipments.
    issuances = delegator.findList("ItemIssuance", EntityCondition.makeCondition([orderId: purchaseOrderId]), null, null, null, false);
    if (issuances) {
        shipments = [] as TreeSet;
        issuances.each { issuance ->
            shipment = issuance.getRelatedOne("Shipment");
            if (!"PURCH_SHIP_RECEIVED".equals(shipment.statusId) &&
                    !"SHIPMENT_CANCELLED".equals(shipment.statusId) &&
                    (!shipment.destinationFacilityId || facilityId.equals(shipment.destinationFacilityId))) {
                shipments.add(shipment);
            }
        }
    }
}
shippedQuantities = [:];
shipmentTypeId = null;
receivedQuantities = [:];
shipment = null;
if (shipmentId && !shipmentId.equals("_NA_")) {
    shipments = [] as TreeSet;
    shipment = delegator.findOne("Shipment", [shipmentId: shipmentId], false);
    shipmentTypeId = shipment.getString("shipmentTypeId");
    shipmentCurrencyUomId = shipment.getString("currencyUomId");
    context.shipmentCurrencyUomId = shipmentCurrencyUomId;
    ownerCurrencyUomId= ownerAcctgPref.baseCurrencyUomId;
    context.currencyUomId = ownerCurrencyUomId;
    context.shipmentTypeId = shipmentTypeId;
    shipments.add(shipment);
    shippedQuantities.clear();
    if ("TRANSFER" == shipmentTypeId) {
        shipmentItems = shipment.getRelated("ShipmentItem");
        if (shipmentItems) {
            shipmentItems.each { shipmentItem ->
                double shipmentQty = shipmentItem.getDouble("quantity").doubleValue();
                if (shippedQuantities.containsKey(shipmentItem.shipmentItemSeqId)) {
                    shipmentQty += ((Double) shippedQuantities.get(shipmentItem.shipmentItemSeqId)).doubleValue();
                }
                shippedQuantities.put(shipmentItem.shipmentItemSeqId, shipmentQty);
            }
        } else {
            issuances = shipment.getRelated("ItemIssuance", [shipmentId: shipmentId], null);
            issuances.each { issuance ->
                double issuanceQty = issuance.getDouble("quantity").doubleValue();
                if (shippedQuantities.containsKey(issuance.shipmentItemSeqId)) {
                    issuanceQty += ((Double) shippedQuantities.get(issuance.shipmentItemSeqId)).doubleValue();
                }
                shippedQuantities.put(issuance.shipmentItemSeqId, issuanceQty);
            }
        }
        context.shipmentItems = shipmentItems
        context.shipmentItemsSize = shipmentItems.size();
        shipmentItems.each { thisItem ->
            totalReceived = 0.0;
            receipts = thisItem.getRelated("ShipmentReceipt");
            if (receipts) {
                receipts.each { rec ->
                    if (!shipment || (rec.shipmentId && rec.shipmentId.equals(shipment.shipmentId))) {
                        accepted = rec.getDouble("quantityAccepted");
                        rejected = rec.getDouble("quantityRejected");
                        if (accepted) {
                            totalReceived += accepted.doubleValue();
                        }
                        if (rejected) {
                            totalReceived += rejected.doubleValue();
                        }
                    }
                }
            }
            receivedQuantities.put(thisItem.shipmentItemSeqId, new Double(totalReceived));
        }
        shipmentCurrencyUnitPriceMap = [:];
        shipmentItems.each { item ->
            issuances = shipment.getRelated("ItemIssuance", [shipmentId: shipmentId], null);
            issuances.each { issuance ->
                unitPrice=BigDecimal.ZERO;
                unitPrice = issuance.getRelatedOneCache("InventoryItem").getDouble("unitCost");
                if (!shipmentCurrencyUomId.equals(ownerCurrencyUomId)) {
                    serviceResults = dispatcher.runSync("convertUom",
                            [uomId: shipmentCurrencyUomId, uomIdTo: ownerCurrencyUomId, originalValue: unitPrice]);
                    if (ServiceUtil.isError(serviceResults)) {
                        request.setAttribute("_ERROR_MESSAGE_", ServiceUtil.getErrorMessage(serviceResults));
                        return;
                    } else {
                        convertedValue = serviceResults.convertedValue;
                        if (convertedValue) {
                            unitPrice = convertedValue;
                        }
                    }
                }
                shipmentCurrencyUnitPriceMap.(item.shipmentItemSeqId) = unitPrice;
            }
        }
        out.println("shipmentCurrencyUnitPriceMap " + shipmentCurrencyUnitPriceMap);
        context.shipmentCurrencyUnitPriceMap=shipmentCurrencyUnitPriceMap;
    }
}

purchaseOrderItems = null;
        if (purchaseOrder) {
            if (product) {
                purchaseOrderItems = purchaseOrder.getRelated("OrderItem", [productId: productId], null);
            } else if (shipment && shipmentTypeId != "TRANSFER") {
                orderItems = purchaseOrder.getRelated("OrderItem");
                exprs = [] as ArrayList;
                orderShipments = shipment.getRelated("OrderShipment", [orderId: purchaseOrderId], null);
                if (orderShipments) {
                    shippedQuantities.clear();
                    orderShipments.each { orderShipment ->
                        exprs.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, orderShipment.orderItemSeqId));
                        double orderShipmentQty = orderShipment.getDouble("quantity").doubleValue();
                        if (shippedQuantities.containsKey(orderShipment.orderItemSeqId)) {
                            orderShipmentQty += ((Double) shippedQuantities.get(orderShipment.orderItemSeqId)).doubleValue();
                        }
                        shippedQuantities.put(orderShipment.orderItemSeqId, orderShipmentQty);
                    }
                } else {
                    // this is here for backward compatibility only: ItemIssuances are no more created for purchase shipments.
                    issuances = shipment.getRelated("ItemIssuance", [orderId: purchaseOrderId], null);
                    issuances.each { issuance ->
                        exprs.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, issuance.orderItemSeqId));
                        double issuanceQty = issuance.getDouble("quantity").doubleValue();
                        if (shippedQuantities.containsKey(issuance.orderItemSeqId)) {
                            issuanceQty += ((Double) shippedQuantities.get(issuance.orderItemSeqId)).doubleValue();
                        }
                        shippedQuantities.put(issuance.orderItemSeqId, issuanceQty);
                    }
                }
                purchaseOrderItems = EntityUtil.filterByOr(orderItems, exprs);
            } else {
                purchaseOrderItems = purchaseOrder.getRelated("OrderItem");
            }
            purchaseOrderItems = EntityUtil.filterByAnd(purchaseOrderItems, [
                    EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "ITEM_CANCELLED")
            ]);
        }
// convert the unit prices to that of the facility owner's currency
        orderCurrencyUnitPriceMap = [:];



        if (purchaseOrder && facility) {
            if (ownerAcctgPref) {
                ownerCurrencyUomId = ownerAcctgPref.baseCurrencyUomId;
                orderCurrencyUomId = purchaseOrder.currencyUom;

                purchaseOrderItems.each { item ->
                    List itemAdjustments = delegator.findByAnd("OrderItemAdjustment", "orderId", item.orderId, "orderItemSeqId", item.orderItemSeqId);
                    List orderItemAdjustments = delegator.findByAnd("OrderAdjustment", "orderId", item.orderId, "orderItemSeqId", item.orderItemSeqId);

                    BigDecimal totalAdjustment = BigDecimal.ZERO;
                    BigDecimal quantity = BigDecimal.ONE;

                    if (item.getBigDecimal("cancelQuantity") != null)
                        quantity = item.getBigDecimal("quantity").subtract(item.getBigDecimal("cancelQuantity"));
                    else
                        quantity = item.getBigDecimal("quantity");

                    //Tax in excluded as fix related to http://www.nthdimenzion.com/mantis/view.php?id=9798
                    orderItemAdjustments = EntityUtil.filterOutByCondition(itemAdjustments,EntityCondition.makeCondition("orderAdjustmentTypeId","SALES_TAX"))
                    for (GenericValue itemAdjustment : itemAdjustments) {
                        totalAdjustment = totalAdjustment.add(itemAdjustment.getBigDecimal("amount"));
                    }

                    orderItemAdjustments = EntityUtil.filterOutByCondition(orderItemAdjustments,
                            EntityCondition.makeCondition("orderAdjustmentTypeId",EntityOperator.NOT_EQUAL,"SALES_TAX"))
                    for (GenericValue orderItemAdjustment : orderItemAdjustments) {
                        totalAdjustment = totalAdjustment.add(orderItemAdjustment.getBigDecimal("amount"));
                    }
                    if("PRICEINCLUDETAX".equals(purchaseOrder.getString("formToIssue"))) {
                        println(' ***************** 1 item.unitPrice= '+item.unitPrice);
                        item.unitPrice=(item.unitPrice*quantity).add(totalAdjustment);
                        println(' ***************** 2 item.unitPrice= '+item.unitPrice);
                        item.unitPrice=item.unitPrice.divide(new BigDecimal(1.16), 6, BigDecimal.ROUND_HALF_UP);
                        println(' ***************** 3 item.unitPrice= '+item.unitPrice);
                        item.unitPrice = item.unitPrice.divide(quantity,3,RoundingMode.HALF_UP);
                        println(' ***************** 4 item.unitPrice= '+item.unitPrice);

                    }else{
                        BigDecimal adjPrice = totalAdjustment.divide(quantity,3,RoundingMode.HALF_UP);
                        println(' ***************** totalAdjustment= '+adjPrice);
                        item.unitPrice = item.unitPrice.add(adjPrice);
                    }
                    item.unitPrice = item.unitPrice.setScale(org.ofbiz.order.order.OrderServices.orderDecimals, org.ofbiz.order.order.OrderServices.orderRounding);
                    orderCurrencyUnitPriceMap.(item.orderItemSeqId) = item.unitPrice;

                    if (!orderCurrencyUomId.equals(ownerCurrencyUomId)) {
                        serviceResults = dispatcher.runSync("convertUom",
                                [uomId: orderCurrencyUomId, uomIdTo: ownerCurrencyUomId, originalValue: item.unitPrice]);

                        if (ServiceUtil.isError(serviceResults)) {
                            request.setAttribute("_ERROR_MESSAGE_", ServiceUtil.getErrorMessage(serviceResults));
                            return;
                        } else {
                            convertedValue = serviceResults.convertedValue;
                            if (convertedValue) {
                                item.unitPrice = convertedValue;
                            }
                        }
                    }

                }

                // put the pref currency in the map for display and form use
                context.currencyUomId = ownerCurrencyUomId;
                context.orderCurrencyUomId = orderCurrencyUomId;
            } else {
                request.setAttribute("_ERROR_MESSAGE_", "Either no owner party was set for this facility, or no accounting preferences were set for this owner party.");
            }
        }
        context.orderCurrencyUnitPriceMap = orderCurrencyUnitPriceMap;

        salesOrderItems = [:];
        if (purchaseOrderItems) {
            context.firstOrderItem = EntityUtil.getFirst(purchaseOrderItems);
            context.purchaseOrderItemsSize = purchaseOrderItems.size();
            receivedQuantities.clear();
            purchaseOrderItems.each { thisItem ->
                totalReceived = 0.0;
                receipts = thisItem.getRelated("ShipmentReceipt");
                if (receipts) {
                    receipts.each { rec ->
                        if (!shipment || (rec.shipmentId && rec.shipmentId.equals(shipment.shipmentId))) {
                            accepted = rec.getDouble("quantityAccepted");
                            rejected = rec.getDouble("quantityRejected");
                            if (accepted) {
                                totalReceived += accepted.doubleValue();
                            }
                            if (rejected) {
                                totalReceived += rejected.doubleValue();
                            }
                        }
                    }
                }
                receivedQuantities.put(thisItem.orderItemSeqId, new Double(totalReceived));
                //----------------------
                salesOrderItemAssocs = delegator.findList("OrderItemAssoc", EntityCondition.makeCondition([orderItemAssocTypeId: 'PURCHASE_ORDER',
                                                                                                           toOrderId           : thisItem.orderId,
                                                                                                           toOrderItemSeqId    : thisItem.orderItemSeqId]),
                        null, null, null, false);
                if (salesOrderItemAssocs) {
                    salesOrderItem = EntityUtil.getFirst(salesOrderItemAssocs);
                    salesOrderItems.put(thisItem.orderItemSeqId, salesOrderItem);
                }
            }
        }

        receivedItems = null;
        if (purchaseOrder) {
            receivedItems = delegator.findList("ShipmentReceiptAndItem", EntityCondition.makeCondition([orderId: purchaseOrderId, facilityId: facilityId]), null, null, null, false);
            context.receivedItems = receivedItems;
        }

        invalidProductId = null;
        if (productId && !product) {
            invalidProductId = "No product found with product ID: [" + productId + "]";
            context.invalidProductId = invalidProductId;
        }

// reject reasons
        rejectReasons = delegator.findList("RejectionReason", null, null, null, null, false);

// inv item types
        inventoryItemTypes = delegator.findList("InventoryItemType", null, null, null, null, false);

// facilities
        facilities = delegator.findList("Facility", null, null, null, null, false);

// default per unit cost for both shipment or individual product
        standardCosts = [:];

        if (ownerAcctgPref) {

            // get the unit cost of the products in a shipment
            if (purchaseOrderItems) {
                purchaseOrderItems.each { orderItem ->
                    productId = orderItem.productId;
                    if (productId) {
                        result = dispatcher.runSync("getProductCost", [productId: productId, currencyUomId: ownerAcctgPref.baseCurrencyUomId,
                                                                       costComponentTypePrefix: 'EST_STD', userLogin: request.getAttribute("userLogin")]);
                        if (!ServiceUtil.isError(result)) {
                            standardCosts.put(productId, result.productCost);
                        }
                    }
                }
            }

            // get the unit cost of a single product
            if (productId) {
                result = dispatcher.runSync("getProductCost", [productId : productId, currencyUomId: ownerAcctgPref.baseCurrencyUomId,
                                                               costComponentTypePrefix: 'EST_STD', userLogin: request.getAttribute("userLogin")]);
                if (!ServiceUtil.isError(result)) {
                    standardCosts.put(productId, result.productCost);
                }
            }
        }
        
        if (UtilValidate.isNotEmpty(purchaseOrderId) && facility!=null) {
            GenericValue PartyAcctgPreferenceGv = delegator.findOne("PartyAcctgPreference", [partyId: facility.ownerPartyId], false);
            GenericValue  purchaseOrderGv = delegator.findOne("OrderHeader", [orderId: purchaseOrderId], false);
            boolean isCurrencyUom = purchaseOrderGv.currencyUom == PartyAcctgPreferenceGv.baseCurrencyUomId ? true : false;
            context.isCurrencyUom = isCurrencyUom;
        }

        context.fixedAssetTypes = delegator.findList("FixedAssetType", null, null, null, null, false);
        context.facilityId = facilityId;
        context.facility = facility;
        context.purchaseOrder = purchaseOrder;
        context.product = product;
        context.shipments = shipments;
        context.shipment = shipment;
        context.shippedQuantities = shippedQuantities;
        context.purchaseOrderItems = purchaseOrderItems;
        context.receivedQuantities = receivedQuantities;
        context.salesOrderItems = salesOrderItems;
        context.rejectReasons = rejectReasons;
        context.inventoryItemTypes = inventoryItemTypes;
        context.facilities = facilities;
        context.standardCosts = standardCosts;