package org.ofbiz.assetmaint;

/**
 Licensed to the Apache Software Foundation (ASF) under one
 or more contributor license agreements.  See the NOTICE file
 distributed with this work for additional information
 regarding copyright ownership.  The ASF licenses this file
 to you under the Apache License, Version 2.0 (the
 "License"); you may not use this file except in compliance
 with the License.  You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing,
 software distributed under the License is distributed on an
 "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 KIND, either express or implied.  See the License for the
 specific language governing permissions and limitations
 under the License.
 **/

import java.math.BigDecimal;
import java.util.*;

import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.product.product.ProductWorker;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

public class FixedAssetMaintServices {

    public static final String module = FixedAssetMaintServices.class.getName();

    public static Map<String, Object> addPartFixedAssetMaint(DispatchContext ctx, Map<String, Object> context) {
        LocalDispatcher dispatcher = ctx.getDispatcher();
        Delegator delegator = ctx.getDelegator();
        Locale locale = (Locale) context.get("locale");
        String fixedAssetId = (String) context.get("fixedAssetId");
        String maintHistSeqId = (String) context.get("maintHistSeqId");
        String productId = (String) context.get("productId");
        String facilityId = (String) context.get("facilityId");
        String groupId = (String) context.get("glAccountCategoryId");
        String issuedDateTime = (String) context.get("issuedDateTime");
        String partyId = (String) context.get("partyId");
        Double quantity = (Double) context.get("quantity");
        double requestedQty = quantity.doubleValue();

        try {
            GenericValue product = ProductWorker.findProduct(delegator, productId);
            if (product == null) {
                return ServiceUtil.returnError
                        (UtilProperties.getMessage("AssetMaintUiLabels", "AssetMaintInvalidPartProductIdError", UtilMisc.toMap("productId", productId), locale));
            }
            Map<String, ? extends Object> findCurrInventoryParams = UtilMisc.toMap("productId", productId, "facilityId", facilityId);
            GenericValue userLogin = (GenericValue) context.get("userLogin");
            // Call issuance service
            Map<String, Object> result = dispatcher.runSync("getInventoryAvailableByFacility", findCurrInventoryParams);
            if (ServiceUtil.isError(result)) {
                return ServiceUtil.returnError("Problem in getting Inventory level for " + productId, null, null, result);
            }
            Object atpObj = result.get("availableToPromiseTotal");
            double atp = 0.0;
            if (atpObj != null) {
                atp = Double.parseDouble(atpObj.toString());
            }
            if (requestedQty > atp) {
                return ServiceUtil.returnError
                        (UtilProperties.getMessage("AssetMaintUiLabels", "AssetMaintLowPartInventoryError", UtilMisc.toMap("productId", productId, "quantity", Double.toString(atp)), locale));
            }
            List<EntityCondition> conditionList = new ArrayList<EntityCondition>();
            conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
            conditionList.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId));
            conditionList.add(EntityCondition.makeCondition("availableToPromiseTotal", EntityOperator.GREATER_THAN, BigDecimal.ZERO));
            EntityCondition ec = EntityCondition.makeCondition(conditionList);
            List<GenericValue> inventoryItems = delegator.findList("InventoryItem", ec, null, null, null, false);   //&& inventoryItems.size() > 0
            Iterator<GenericValue> itr = inventoryItems.iterator();
            while (requestedQty > 0 && itr.hasNext()) {
                GenericValue inventoryItem = (GenericValue) itr.next();
                String inventoryItemId = inventoryItem.getString("inventoryItemId");
                atp = inventoryItem.getDouble("availableToPromiseTotal").doubleValue();
                findCurrInventoryParams = UtilMisc.toMap("inventoryItemId", inventoryItemId);
                Double issueQuantity = null;
                if (requestedQty > atp) {
                    issueQuantity = new Double(atp);
                } else {
                    issueQuantity = new Double(requestedQty);
                }
                Map<String, Object> itemIssuanceCtx = FastMap.newInstance();
                itemIssuanceCtx.put("userLogin", userLogin);
                itemIssuanceCtx.put("inventoryItemId", inventoryItemId);
                itemIssuanceCtx.put("fixedAssetId", fixedAssetId);
                itemIssuanceCtx.put("maintHistSeqId", maintHistSeqId);
                itemIssuanceCtx.put("quantity", BigDecimal.valueOf(issueQuantity));
                itemIssuanceCtx.put("groupId",groupId);
                itemIssuanceCtx.put("issuedDateTime",issuedDateTime);
                itemIssuanceCtx.put("partyId",partyId);
                // Call issuance service
                result = dispatcher.runSync("issueInventoryItemToFixedAssetMaint", itemIssuanceCtx);
                if (ServiceUtil.isError(result)) {
                    return ServiceUtil.returnError("Problem in calling service issueInventoryItemToFixedAssetMaint", null, null, result);
                }
                requestedQty = requestedQty - issueQuantity.doubleValue();
            }
        } catch (GenericEntityException e) {
            Debug.logError("Problem in retriving data from database", module);
        } catch (GenericServiceException e) {
            String msg = "Problem in calling service issueInventoryItemToFixedAssetMaint";
            Debug.logError(msg, module);
            return ServiceUtil.returnError(msg);
        }
        Map<String, Object> map = ServiceUtil.returnSuccess();
        map.put("fixedAssetId", fixedAssetId);
        map.put("maintHistSeqId", maintHistSeqId);
        return map;
    }
}
