package com.ndz.transformation;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;

import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityComparisonOperator;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.webapp.control.Infrastructure;

import com.ndz.util.ProductHelper;

public class InventoryService {

    private static final String module = InventoryService.class.getName();

    public static GenericValue getInventoryItem(String productId, BigDecimal qty)
            throws GenericEntityException {

        GenericDelegator delegator = Infrastructure.getDelegator();

        EntityCondition cond1 = EntityCondition.makeCondition(
                "availableToPromiseTotal",
                EntityComparisonOperator.GREATER_THAN_EQUAL_TO, qty);
        EntityCondition cond2 = EntityCondition.makeCondition(UtilMisc.toMap(
                "productId", productId));
        EntityCondition cond3 = EntityCondition.makeCondition("statusId", null);

        List<GenericValue> invItems = delegator.findList("InventoryItem",
                EntityCondition.makeCondition(cond1, cond2, cond3), null,
                UtilMisc.toList("-expireDate"), null, false);

        GenericValue invItem = EntityUtil.getFirst(invItems);
        if (invItem == null) {
            System.out.println(" No Inventory Found  for Product " + productId
                    + " with ATP " + qty);
        } else {
            invItem.set("statusId", "INV_NS_ON_HOLD");
            delegator.store(invItem);
        }
        return invItem;
    }

    // Not Reserved Qty is returned
    public static Map<String, Object> reserveOrder(DispatchContext ctx,
                                                   Map<String, Object> context) throws GenericEntityException,
            GenericServiceException {

        Delegator delegator = ctx.getDelegator();
        Map<String, Object> results = new HashMap<String, Object>();

        String productId = (String) context.get("productId");
        String orderId = (String) context.get("orderId");

        String mheOrderType = "";
        String orderLocCode = "";

        EntityCondition chemicalCondition = null;

        GenericValue relationship = null;

        BigDecimal quantity = (BigDecimal) context.remove("quantity");
        Object o = context.remove("safetyDays");
        Integer safetyDays = -1;

        if (UtilValidate.isNotEmpty(o)) {
            safetyDays = (Integer) o;
        }

		/*
         * EntityCondition cond1 = EntityCondition.makeCondition(
		 * "availableToPromiseTotal",
		 * EntityComparisonOperator.LESS_THAN_EQUAL_TO, quantity);
		 */

        EntityCondition cond2 = EntityCondition.makeCondition(
                "availableToPromiseTotal",
                EntityComparisonOperator.GREATER_THAN, BigDecimal.ZERO);

        EntityCondition cond3 = EntityCondition.makeCondition(UtilMisc.toMap(
                "productId", productId));

        // Exclude ALL inventories that have Status
        EntityCondition cond4 = EntityCondition.makeCondition("statusId", null);

        // Inventory in Facility Location belonging to ALRAS and VANS are
        // excluded.
        EntityCondition receivingCond = EntityCondition.makeCondition(
                "locationTypeEnumId", "RECEIVING");
        EntityCondition primaryCond = EntityCondition.makeCondition(
                "locationTypeEnumId", "PRIMARY");

        EntityCondition locationTypeCond = null;
        // EntityCondition.makeCondition(EntityJoinOperator.OR, receivingCond,
        // primaryCond);

        Debug.logInfo(" Order Type is " + mheOrderType, module);
        if ("FVSO".equals(mheOrderType)) {
            locationTypeCond = EntityCondition.makeCondition(EntityCondition
                            .makeCondition("locationSeqId",
                                    EntityComparisonOperator.LIKE, orderLocCode),
                    EntityCondition.makeCondition("buildingId", "VANS"));
        } else if ("RPO".equals(mheOrderType)) {
            locationTypeCond = EntityCondition.makeCondition("buildingId",
                    "ALRAS");
        } else {
            locationTypeCond = EntityCondition.makeCondition(
                    EntityJoinOperator.OR, receivingCond, primaryCond);
        }

        Debug.logInfo(" Condition Based on Order Type " + locationTypeCond,
                module);
        // Inventory Items marked as Local-Purchase will not be available
        // for reservation when the Sales Group / Division user logged in is of
        // WHOLESALE/RETAIL

        String divisionId = relationship == null ? "" : relationship
                .getString("partyIdFrom");

        GenericValue division = delegator.findOne("PartyGroup", false, UtilMisc
                .toMap("partyId", divisionId));
        String groupName = division == null ? "" : division.getString(
                "groupName").toUpperCase();

        EntityCondition cond7 = null;

        EntityCondition cond8 = EntityCondition.makeCondition("divisionId",
                divisionId);

        if ("WHOLESALE&RETAIL".equals(groupName)
                || "CHEMICALS".equals(groupName)) {
            cond7 = EntityCondition.makeCondition("localPurchase", "N");
        }

        EntityCondition condList = null;
        if (cond7 != null) {
            condList = EntityCondition.makeCondition(cond2, cond3, cond4,
                    cond7, cond8);
        } else {
            condList = EntityCondition
                    .makeCondition(cond2, cond3, cond4, cond8);
        }

        EntityConditionList<EntityCondition> invCondition = EntityConditionList
                .makeCondition(condList, locationTypeCond);
        GenericValue prodAttrGV = delegator.findOne("ProductAttribute", false,
                "productId", productId, "attrName", "EXPIRE_DATE_REQ");

        boolean productHasExpiryDate = false;

        if (prodAttrGV != null) {
            productHasExpiryDate = "Y"
                    .equals(prodAttrGV.getString("attrValue"));
        }

        String stockXferId = (String) context.get("reserveOrderId");
        String stockXferItemSeqId = (String) context
                .get("reserveOrderItemSeqId");

        List<GenericValue> invItems = null;
        boolean vanInventoriesOnly = false;
        if (stockXferId != null && stockXferItemSeqId != null) {
            GenericValue stockXfer = delegator.findOne("ReserveOrderHeader",
                    false, "orderId", stockXferId);
            String buildingIdFrom = stockXfer.getString("buildingIdFrom");
            if (UtilValidate.isNotEmpty(buildingIdFrom)) {
                EntityCondition stockXferCond = EntityCondition.makeCondition(
                        "buildingId", buildingIdFrom);
                invCondition = EntityConditionList.makeCondition(condList,
                        locationTypeCond, stockXferCond);
            }
            String locationSeqIdFrom = stockXfer.getString("locationSeqIdFrom");
            if (UtilValidate.isNotEmpty(locationSeqIdFrom)) {
                EntityCondition stockXferCond = EntityCondition.makeCondition(
                        "locationSeqId", locationSeqIdFrom);
                EntityCondition expiryCond = null;

                if (productHasExpiryDate) {
                    expiryCond = EntityCondition.makeCondition("expireDate", EntityComparisonOperator.GREATER_THAN_EQUAL_TO,
                            UtilDateTime.nowTimestamp());
                    invCondition = EntityConditionList.makeCondition(
                            stockXferCond, expiryCond);
                } else {
                    invCondition = EntityConditionList
                            .makeCondition(stockXferCond);
                }


                vanInventoriesOnly = true;
                invItems = delegator.findList("InventoryItemAndLocation",
                        invCondition, null, UtilMisc.toList("expireDate ASC",
                                " availableToPromiseTotal ASC "), null, false);
            }

        }


        Debug.logInfo(" Inventory Lookup Query " + invCondition, module);


        BigDecimal diff = BigDecimal.ZERO;

        List<String> inventories = new ArrayList<String>();
        GenericValue productGV = delegator.findOne("Product", false, UtilMisc
                .toMap("productId", productId));
        boolean isNotCarcassType = !checkIfProductCarcass(productGV);

        for (Iterator<GenericValue> iter = invItems.iterator(); iter.hasNext(); ) {

            if (quantity.compareTo(BigDecimal.ZERO) == 0) {
                break;
            }

            GenericValue gv = iter.next();
            BigDecimal atp = gv.getBigDecimal("availableToPromiseTotal");
            diff = quantity.subtract(atp);

            Debug.logInfo(" Quantity still to Reserve is " + quantity, module);

            // For all Items (except the Carcass type), VALIDATE quantity
            // reserved CANNOT be greater than Balance quantity (with
            // reservation)
            boolean isExpired = false;
            if (productHasExpiryDate) {
                isExpired = isExpired(gv.getTimestamp("expireDate"),
                        safetyDays);
            }

            if (isNotCarcassType && !isExpired) {

                context.put("inventoryItemId", gv.getString("inventoryItemId"));
                if (diff.compareTo(BigDecimal.ZERO) == 1) {
                    // It is partial fulfilled
                    context.put("quantityNotReserved", atp);
                    context.put("deductAmount", atp);
                    reserveInventoryInline(ctx.getDispatcher(), context);
                    quantity = quantity.subtract(atp);
                } else if (diff.compareTo(BigDecimal.ZERO) == -1
                        || diff.compareTo(BigDecimal.ZERO) == 0) {
                    // It can be satisfied from this Inventory Item
                    context.put("quantityNotReserved", quantity);
                    context.put("deductAmount", quantity);
                    reserveInventoryInline(ctx.getDispatcher(), context);
                    quantity = BigDecimal.ZERO;
                }

            }
        }
        results.put("inventoriesReserved", inventories);
        results.put("quantityNotReserved", quantity);
        return results;
    }

    private static boolean isExpired(Timestamp expireTime, Integer safetyDays) {

        if (safetyDays == -1) {
            return false;
        }
        Timestamp safeTime = UtilDateTime.addDaysToTimestamp(UtilDateTime
                .nowTimestamp(), safetyDays);
        boolean flag = expireTime.before(safeTime);
        Debug.logInfo("*** Check Inventory Item with Expire Date " + expireTime
                + " and Safety Till " + safeTime + " is Expired = "
                + flag, module);
        return flag;
    }

    private static boolean checkIfProductCarcass(GenericValue productGV)
            throws GenericEntityException {

        boolean returnVal = false;

        String skuType = productGV.getString("primaryProductCategoryId");
        returnVal = "FMT".equals(skuType) || "Carcass".equals(skuType)
                || "Frozen Meat".equals(skuType);

        Debug.logInfo("Checking if Product with ProductID "
                + productGV.getString("productId") + " and PrimaryCategory"
                + skuType + " if of Carcass  " + returnVal, module);

        return returnVal;
    }

    public static boolean reserveInventoryInline(LocalDispatcher dispatcher,
                                                 Map<String, Object> context) throws GenericServiceException {

        Debug.logInfo(" Reserving Inventory Item "
                + context.get("inventoryItemId") + " with Quantity "
                + context.get("quantityNotReserved"), module);
        String reserveOrderId = (String) context.get("reserveOrderId");
        String reserveOrderItemSeqId = (String) context
                .get("reserveOrderItemSeqId");
        Map<String, Object> results = null;
        if (reserveOrderId != null && reserveOrderItemSeqId != null) {
            results = dispatcher.runSync("reserveInventoryForOrderItem",
                    context);
        } else {
            results = dispatcher.runSync("reserveForInventoryItemInline",
                    context);
        }
        return ServiceUtil.isSuccess(results);
    }

}
