import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilValidate;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.*;

import org.ofbiz.entity.condition.*
import org.ofbiz.widget.html.HtmlFormWrapper
import org.ofbiz.entity.*
import org.ofbiz.entity.condition.*
import org.ofbiz.entity.util.*
import org.ofbiz.entity.*
import org.ofbiz.widget.html.*;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.util.*

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.ofbiz.product.facility.*;
import org.ofbiz.entity.util.EntityUtil;

facilityId = request.getParameter("facilityId");
String shipmentId = request.getParameter("shipmentId");
purchaseOrderId = request.getParameter("purchaseOrderId");
String shipmentIdParam = parameters.shipmentId;


def parameterCollection = UtilHttp.parseMultiFormData(parameters);
Timestamp datetimeReceived = UtilValidate.isEmpty(request.getParameter("datetimeReceived")) ? null : UtilDateTime.dateStringToTimestampParser(request.getParameter("datetimeReceived"));
if (UtilValidate.isEmpty(datetimeReceived)) {
    datetimeReceived = UtilDateTime.nowTimestamp();
}
for (Map inParam : parameterCollection) {
    shipmentId = inParam.get("shipmentId");
}
if (datetimeReceived.after(UtilDateTime.nowTimestamp())) {
    request.setAttribute("_ERROR_MESSAGE_", "Receive Date can't be Future Date");
    request.setAttribute("facilityId", facilityId);
    request.setAttribute("shipmentId", shipmentId);
    request.setAttribute("purchaseOrderId", purchaseOrderId);
    request.setAttribute("initialSelected", "Y");
    return "error";
}
Map findCustomTimePeriodMap = dispatcher.runSync("findCustomTimePeriods", UtilMisc.toMap("findDate", UtilDateTime.nowTimestamp(), "organizationPartyId", "Company", "userLogin", session.getAttribute("userLogin")));
List customTimePeriodList = findCustomTimePeriodMap.get("customTimePeriodList");
if (UtilValidate.isNotEmpty(customTimePeriodList)) {
    Timestamp fromTimestamp = new java.sql.Timestamp(customTimePeriodList.get(0).getDate("fromDate").getTime());
    Date fromDate = new Date(fromTimestamp.getTime());
    Date datetimeReceivedDate = new Date(datetimeReceived.getTime());
    if (fromDate.compareTo(datetimeReceivedDate) > 0) {
        request.setAttribute("_ERROR_MESSAGE_", "Receive Date must be within current Fiscal Year");
        request.setAttribute("facilityId", facilityId);
        request.setAttribute("shipmentId", shipmentId);
        request.setAttribute("purchaseOrderId", purchaseOrderId);
        request.setAttribute("initialSelected", "Y");
        return "error";
    }
}

String orderId = null;
String string = " ";
String serializeInvItemMsg = "";
Map result = new HashMap();

for (Map inParam : parameterCollection) {
    try {
        orderId = inParam.get("orderId");
        orderItemSeqId = inParam.get("orderItemSeqId");
        quantityRejected = inParam.get("quantityRejected");
        currencyUomId = inParam.get("currencyUomId");
        shipmentId = inParam.get("shipmentId");
        shipmentIdParam = shipmentId;
        shipmentItemSeqId = inParam.get("shipmentItemSeqId");
        facilityId = inParam.get("facilityId");
        locationSeqId = inParam.get("locationSeqId");
        productId = inParam.get("productId");
        rejectionId = inParam.get("rejectionId");
        //datetimeReceived=inParam.get("datetimeReceived");
        unitCost = inParam.get("unitCost");
        orderCurrencyUnitPrice = inParam.get("orderCurrencyUnitPrice");
        quantityAccepted = inParam.get("quantityAccepted");
        inventoryItemTypeId = inParam.get("inventoryItemTypeId");
        if (currencyUomId != null && inParam.get("orderCurrencyUomId") != null && !currencyUomId.equals(inParam.get("orderCurrencyUomId"))) {
            Map serviceResults = dispatcher.runSync("convertUom",
                    [uomId: inParam.get("orderCurrencyUomId"), uomIdTo: currencyUomId, originalValue: new BigDecimal(orderCurrencyUnitPrice), "asOfDate": datetimeReceived]);
            unitCost = serviceResults.get("convertedValue");
        }
        receivedItemType = inParam.get("receivedItemType");
        quantityVariance = inParam.get("quantityVariance");

        //check if shipment is received.
        GenericValue shipment = delegator.findOne("Shipment", true, ["shipmentId": shipmentId]);
        if (shipment.getString("statusId") == 'PURCH_SHIP_RECEIVED') {
            continue;
        }

        Map serviceMap = new HashMap();
        serviceMap.put("orderId", orderId);
        serviceMap.put("orderItemSeqId", orderItemSeqId);
        serviceMap.put("shipmentId", shipmentId);
        serviceMap.put("shipmentItemSeqId", shipmentItemSeqId)
        serviceMap.put("quantityRejected", quantityRejected);
        serviceMap.put("currencyUomId", currencyUomId);
        serviceMap.put("facilityId", facilityId);
        serviceMap.put("locationSeqId", locationSeqId);
        serviceMap.put("productId", productId);
        serviceMap.put("rejectionId", rejectionId);
        serviceMap.put("datetimeReceived", datetimeReceived);
        serviceMap.put("unitCost", unitCost);
        serviceMap.put("inventoryItemTypeId", inventoryItemTypeId);
        serviceMap.put("quantityAccepted", quantityAccepted);
        serviceMap.put("quantityVariance", quantityVariance);

        System.out.println("quantityVariance=" + quantityVariance);
        serviceMap.put("userLogin", session.getAttribute("userLogin"));


        if ("INVENTORY" == receivedItemType)
            result = dispatcher.runSync("receiveInventoryProduct", serviceMap);
        else {
            Map fixedAssetMap = new HashMap();
            fixedAssetMap.put("acquireOrderId", orderId);
            fixedAssetMap.put("acquireOrderItemSeqId", orderItemSeqId);
            fixedAssetMap.put("purchaseCost", unitCost);
            fixedAssetMap.put("instanceOfProductId", productId);
            fixedAssetMap.put("purchaseCostUomId", currencyUomId);
            fixedAssetMap.put("locatedAtFacilityId", facilityId);
            fixedAssetMap.put("locatedAtLocationSeqId", locationSeqId);
            fixedAssetMap.put("dateAcquired", datetimeReceived);
            fixedAssetMap.put("fixedAssetTypeId", receivedItemType);
            fixedAssetMap.put("userLogin", session.getAttribute("userLogin"));
            //TODO Derive from the Shipment
            fixedAssetMap.put("partyId", "Company");
            result2 = dispatcher.runSync("createFixedAsset", fixedAssetMap);
            String fixedAssetId = result2.fixedAssetId;
            serviceMap.put("unitCost", 0);
            serviceMap.put("fixedAssetId", fixedAssetId);
            serviceMap.put("inventoryItemTypeId", "SERIALIZED_INV_ITEM");
            result = dispatcher.runSync("receiveInventoryProduct", serviceMap);
        }

        if ("SERIALIZED_INV_ITEM".equals(inventoryItemTypeId)) {
            string = string + "Received " + quantityAccepted + " of " + productId + " in inventory item. \n";
        }
        if (!"INVENTORY".equals(receivedItemType)) {
            GenericValue product = delegator.findOne("Product", false, ["productId": productId]);
            GenericValue fixedAssetType = delegator.findOne("FixedAssetType", false, ["fixedAssetTypeId": receivedItemType]);
            string = string + "Received Fixed Asset " + quantityAccepted + " of type " + fixedAssetType.description + " of " + product.internalName + ". \n";
        }


        if ("NON_SERIAL_INV_ITEM".equals(inventoryItemTypeId)) {
            if (result.successMessageList != null)
                string = string + result.successMessageList.get(0) + "\n";
        }

    } catch (Exception e) {
        e.printStackTrace();
    }
}
request.setAttribute("_EVENT_MESSAGE_", string);

if (!shipmentId && orderId) {
    results = dispatcher.runSync("createInvoiceForOrderAllItems", ["orderId": orderId, "invoiceDate": datetimeReceived, "eventDate": datetimeReceived]);
}


inspectedBy = request.getParameter("inspectedBy");
rejectionReason = null;
inspectedBy = UtilValidate.isEmpty(request.getParameter("inspectedBy")) ? null : request.getParameter("inspectedBy");
inspectionNote = UtilValidate.isEmpty(request.getParameter("inspectionNote")) ? null : request.getParameter("inspectionNote");
inspectionDescription = request.getParameter("inspectionDescription");
inspectionResult = UtilValidate.isEmpty(request.getParameter("inspectionResult")) ? null : request.getParameter("inspectionResult");

GenericValue shipmentGv = delegator.findOne("Shipment", UtilMisc.toMap("shipmentId", shipmentIdParam), false);
shipmentGv.put("inspectedBy", inspectedBy);
shipmentGv.put("inspectionNote", inspectionNote);
shipmentGv.put("inspectionDescription", inspectionDescription);
shipmentGv.put("inspectionResult", inspectionResult);
shipmentGv.put("receivedBy", session.getAttribute("userLogin").getString("partyId"));
delegator.store(shipmentGv);
request.setAttribute("inventoryItemId", result.inventoryItemId);
return "success";
