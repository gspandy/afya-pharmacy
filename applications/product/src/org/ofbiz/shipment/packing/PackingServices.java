/*******************************************************************************
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
 *******************************************************************************/
package org.ofbiz.shipment.packing;

import org.ofbiz.base.util.*;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import java.math.BigDecimal;
import java.util.*;

public class PackingServices {

    public static final String module = PackingServices.class.getName();

    public static Map<String, Object> addPackLine(DispatchContext dctx, Map<String, ? extends Object> context) {
        PackingSession session = (PackingSession) context.get("packingSession");
        String shipGroupSeqId = (String) context.get("shipGroupSeqId");
        String orderId = (String) context.get("orderId");
        String productId = (String) context.get("productId");
        BigDecimal quantity = (BigDecimal) context.get("quantity");
        BigDecimal weight = (BigDecimal) context.get("weight");
        Integer packageSeq = (Integer) context.get("packageSeq");
        String machine = (String) context.get("machine");

        // set the instructions -- will clear out previous if now null
        String instructions = (String) context.get("handlingInstructions");
        session.setHandlingInstructions(instructions);

        // set the picker party id -- will clear out previous if now null
        String pickerPartyId = (String) context.get("pickerPartyId");
        session.setPickerPartyId(pickerPartyId);

        if (quantity == null) {
            quantity = BigDecimal.ONE;
        }

        Debug.log("OrderId [" + orderId + "] ship group [" + shipGroupSeqId + "] Pack input [" + productId + "] @ [" + quantity + "] packageSeq [" + packageSeq
                + "] weight [" + weight + "]", module);

        if (weight == null) {
            Debug.logWarning("OrderId [" + orderId + "] ship group [" + shipGroupSeqId + "] product [" + productId
                    + "] being packed without a weight, assuming 0", module);
            weight = BigDecimal.ZERO;
        }

        try {
            session.addOrIncreaseLine(orderId, null, shipGroupSeqId, productId, quantity, packageSeq.intValue(), weight, false, machine);
        } catch (GeneralException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
        }

        return ServiceUtil.returnSuccess();
    }

    /**
     * <p>
     * Create or update package lines.
     * </p>
     * <p>
     * Context parameters:
     * <ul>
     * <li>selInfo - selected rows</li>
     * <li>iteInfo - orderItemIds</li>
     * <li>prdInfo - productIds</li>
     * <li>pkgInfo - package numbers</li>
     * <li>wgtInfo - weights to pack</li>
     * <li>numPackagesInfo - number of packages to pack per line (>= 1, default:
     * 1)<br/>
     * Packs the same items n times in consecutive packages, starting from the
     * package number retrieved from pkgInfo.</li>
     * </ul>
     * </p>
     *
     * @param dctx
     * @param context
     * @return
     */
    public static Map<String, Object> packBulk(DispatchContext dctx, Map<String, ? extends Object> context) {
        PackingSession session = (PackingSession) context.get("packingSession");
        String orderId = (String) context.get("orderId");
        String shipGroupSeqId = (String) context.get("shipGroupSeqId");
        Boolean updateQuantity = (Boolean) context.get("updateQuantity");
        if (updateQuantity == null) {
            updateQuantity = Boolean.FALSE;
        }

        // set the instructions -- will clear out previous if now null
        String instructions = (String) context.get("handlingInstructions");
        session.setHandlingInstructions(instructions);

        // set the picker party id -- will clear out previous if now null
        String pickerPartyId = (String) context.get("pickerPartyId");
        session.setPickerPartyId(pickerPartyId);

        Map<String, ?> selInfo = UtilGenerics.checkMap(context.get("selInfo"));
        Map<String, String> iteInfo = UtilGenerics.checkMap(context.get("iteInfo"));
        Map<String, String> prdInfo = UtilGenerics.checkMap(context.get("prdInfo"));
        Map<String, String> machineMap = UtilGenerics.checkMap(context.get("machine"));
        Map<String, String> qtyInfo = UtilGenerics.checkMap(context.get("qtyInfo"));
        Map<String, String> pkgInfo = UtilGenerics.checkMap(context.get("pkgInfo"));
        Map<String, String> wgtInfo = UtilGenerics.checkMap(context.get("wgtInfo"));
        Map<String, String> numPackagesInfo = UtilGenerics.checkMap(context.get("numPackagesInfo"));

        if (selInfo != null) {
            for (String rowKey : selInfo.keySet()) {
                String orderItemSeqId = iteInfo.get(rowKey);
                String prdStr = prdInfo.get(rowKey);
                if (UtilValidate.isEmpty(prdStr)) {
                    // set the productId to null if empty
                    prdStr = null;
                }

                // base package/quantity/weight strings
                String pkgStr = pkgInfo.get(rowKey);
                String machine = machineMap.get(rowKey);
                String qtyStr = qtyInfo.get(rowKey);
                String wgtStr = wgtInfo.get(rowKey);

                Debug.log("Item: " + orderItemSeqId + " / Product: " + prdStr + " / Quantity: " + qtyStr + " /  Package: " + pkgStr + " / Weight: " + wgtStr,
                        module);

                // array place holders
                String[] quantities;
                String[] packages;
                String[] weights;

                // process the package array
                if (pkgStr.indexOf(",") != -1) {
                    // this is a multi-box update
                    packages = pkgStr.split(",");
                } else {
                    packages = new String[]{pkgStr};
                }

                // check to make sure there is at least one package
                if (packages == null || packages.length == 0) {
                    return ServiceUtil.returnError("No packages defined for processing.");
                }

                // process the quantity array
                if (qtyStr == null) {
                    quantities = new String[packages.length];
                    for (int p = 0; p < packages.length; p++) {
                        quantities[p] = (String) qtyInfo.get(rowKey + ":" + packages[p]);
                    }
                    if (quantities.length != packages.length) {
                        return ServiceUtil.returnError("Packages and quantities do not match.");
                    }
                } else {
                    quantities = new String[]{qtyStr};
                }

                // process the weight array
                if (UtilValidate.isEmpty(wgtStr))
                    wgtStr = "0";
                weights = new String[]{wgtStr};

                for (int p = 0; p < packages.length; p++) {
                    BigDecimal quantity;
                    int packageSeq;
                    BigDecimal weightSeq;
                    try {
                        quantity = new BigDecimal(quantities[p]);
                        packageSeq = Integer.parseInt(packages[p]);
                        weightSeq = new BigDecimal(weights[p]);
                    } catch (Exception e) {
                        return ServiceUtil.returnError(e.getMessage());
                    }

                    try {
                        String numPackagesStr = numPackagesInfo.get(rowKey);
                        int numPackages = 1;
                        if (numPackagesStr != null) {
                            try {
                                numPackages = Integer.parseInt(numPackagesStr);
                                if (numPackages < 1) {
                                    numPackages = 1;
                                }
                            } catch (NumberFormatException nex) {
                            }
                        }
                        for (int numPackage = 0; numPackage < numPackages; numPackage++) {
                            session.addOrIncreaseLine(orderId, orderItemSeqId, shipGroupSeqId, prdStr, quantity, packageSeq + numPackage, weightSeq,
                                    updateQuantity.booleanValue(), machine);
                        }
                    } catch (GeneralException e) {
                        Debug.logError(e, module);
                        return ServiceUtil.returnError(e.getMessage());
                    }
                }
            }
        }

        return ServiceUtil.returnSuccess();
    }

    public static Map<String, Object> incrementPackageSeq(DispatchContext dctx, Map<String, ? extends Object> context) {
        PackingSession session = (PackingSession) context.get("packingSession");
        int nextSeq = session.nextPackageSeq();
        Map<String, Object> result = ServiceUtil.returnSuccess();
        result.put("nextPackageSeq", Integer.valueOf(nextSeq));
        return result;
    }

    public static Map<String, Object> clearLastPackage(DispatchContext dctx, Map<String, ? extends Object> context) {
        PackingSession session = (PackingSession) context.get("packingSession");
        int nextSeq = session.clearLastPackage();
        Map<String, Object> result = ServiceUtil.returnSuccess();
        result.put("nextPackageSeq", Integer.valueOf(nextSeq));
        return result;
    }

    public static Map<String, Object> clearPackLine(DispatchContext dctx, Map<String, ? extends Object> context) {
        PackingSession session = (PackingSession) context.get("packingSession");
        String orderId = (String) context.get("orderId");
        String orderItemSeqId = (String) context.get("orderItemSeqId");
        String shipGroupSeqId = (String) context.get("shipGroupSeqId");
        String inventoryItemId = (String) context.get("inventoryItemId");
        String productId = (String) context.get("productId");
        Integer packageSeqId = (Integer) context.get("packageSeqId");

        PackingSessionLine line = session.findLine(orderId, orderItemSeqId, shipGroupSeqId, productId, inventoryItemId, packageSeqId.intValue());

        // remove the line
        if (line != null) {
            session.clearLine(line);
        } else {
            return ServiceUtil.returnError("Packing line item not found; cannot clear.");
        }

        return ServiceUtil.returnSuccess();
    }

    public static Map<String, Object> clearPackAll(DispatchContext dctx, Map<String, ? extends Object> context) {
        PackingSession session = (PackingSession) context.get("packingSession");
        session.clearAllLines();

        return ServiceUtil.returnSuccess();
    }

    public static Map<String, Object> calcPackSessionAdditionalShippingCharge(DispatchContext dctx, Map<String, ? extends Object> context) {
        PackingSession session = (PackingSession) context.get("packingSession");
        Map<String, String> packageWeights = UtilGenerics.checkMap(context.get("packageWeights"));
        String weightUomId = (String) context.get("weightUomId");
        String shippingContactMechId = (String) context.get("shippingContactMechId");
        String shipmentMethodTypeId = (String) context.get("shipmentMethodTypeId");
        String carrierPartyId = (String) context.get("carrierPartyId");
        String carrierRoleTypeId = (String) context.get("carrierRoleTypeId");
        String productStoreId = (String) context.get("productStoreId");

        BigDecimal shippableWeight = setSessionPackageWeights(session, packageWeights);
        BigDecimal estimatedShipCost = session.getShipmentCostEstimate(shippingContactMechId, shipmentMethodTypeId, carrierPartyId, carrierRoleTypeId,
                productStoreId, null, null, shippableWeight, null);
        session.setAdditionalShippingCharge(estimatedShipCost);
        session.setWeightUomId(weightUomId);

        Map<String, Object> result = ServiceUtil.returnSuccess();
        result.put("additionalShippingCharge", estimatedShipCost);
        return result;
    }

    public static Map<String, Object> completePack(DispatchContext dctx, Map<String, ? extends Object> context) throws GenericEntityException {
        org.ofbiz.entity.Delegator delegator = dctx.getDelegator();
        PackingSession session = (PackingSession) context.get("packingSession");

        // set the instructions -- will clear out previous if now null
        String instructions = (String) context.get("handlingInstructions");
        String pickerPartyId = (String) context.get("pickerPartyId");
        BigDecimal additionalShippingCharge = (BigDecimal) context.get("additionalShippingCharge");
        Map<String, String> packageWeights = UtilGenerics.checkMap(context.get("packageWeights"));
        String weightUomId = (String) context.get("weightUomId");
        session.setHandlingInstructions(instructions);
        session.setPickerPartyId(pickerPartyId);
        session.setAdditionalShippingCharge(additionalShippingCharge);
        session.setWeightUomId(weightUomId);
        setSessionPackageWeights(session, packageWeights);
        String shipmentId = (String) context.get("shipmentId");
        Boolean force = (Boolean) context.get("forceComplete");
        if (force == null) {
            force = Boolean.FALSE;
        }
        Map<String, Object> resp;
        resp = null;
        try {
            if (shipmentId != null) {
                Set<String> packedOrderIds = new HashSet<String>();
                for (Iterator<PackingSessionLine> iter = session.packLines.iterator(); iter.hasNext(); ) {
                    packedOrderIds.add(iter.next().getOrderId());
                }
                List<GenericValue> ordersInShipment = delegator.findList("OrderShipment", EntityCondition.makeCondition("shipmentId", shipmentId), null, null, null, false);
                boolean allPacked = true;
                for (GenericValue gv : ordersInShipment) {
                    if (!packedOrderIds.contains(gv.getString("orderId"))) {
                        allPacked = false;
                        break;
                    }
                }
                if (!allPacked) {
                    return ServiceUtil.returnError("All Orders in shipment not packed. Cannot complete Packing");
                }
                shipmentId = session.setShipmentId(shipmentId);
                if (UtilValidate.isNotEmpty(instructions)){
                    delegator.storeByCondition("Shipment", UtilMisc.toMap("handlingInstructions", instructions), EntityCondition.makeCondition(UtilMisc.toMap("shipmentId", shipmentId)));
                }

                if (UtilValidate.isNotEmpty(shipmentId)) {
                    session.createPackage();
                }
                session.checkReservations(force);
                session.issueItemToShipment();
                session.applyItemToPackages();

                resp = ServiceUtil.returnSuccess("Packing and Issuance completed for shipment id #" + shipmentId);
            }
            if (shipmentId == null) {
                shipmentId = session.complete(force);
                resp = ServiceUtil.returnSuccess("Shipment #" + shipmentId + " created and marked as PACKED.");
            }

        } catch (GeneralException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage(), e.getMessageList());
        }
        if ("EMPTY".equals(shipmentId)) {
            resp = ServiceUtil.returnError("No items currently set to be shipped. Cannot complete!");
        }
        resp.put("shipmentId", shipmentId);

        return resp;
    }

    public static BigDecimal setSessionPackageWeights(PackingSession session, Map<String, String> packageWeights) {
        BigDecimal shippableWeight = BigDecimal.ZERO;
        if (!UtilValidate.isEmpty(packageWeights)) {
            for (Map.Entry<String, String> entry : packageWeights.entrySet()) {
                String packageSeqId = entry.getKey();
                String packageWeightStr = entry.getValue();
                if (UtilValidate.isNotEmpty(packageWeightStr)) {
                    BigDecimal packageWeight = new BigDecimal((String) packageWeights.get(packageSeqId));
                    session.setPackageWeight(Integer.parseInt(packageSeqId), packageWeight);
                    shippableWeight = shippableWeight.add(packageWeight);
                } else {
                    session.setPackageWeight(Integer.parseInt(packageSeqId), null);
                }
            }
        }
        return shippableWeight;
    }
}
