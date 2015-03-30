import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator

import java.sql.Timestamp
import java.text.SimpleDateFormat;
import java.util.*;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilValidate;




productId = parameters.productId;
supplierId = parameters.supplierId;
fromDate = parameters.fromDate
thruDate = parameters.thruDate



listOfShipments = getListOfShipments();
shipmentMaterialReceivedList = [];
for(GenericValue shipment:listOfShipments){
    conditions = [];
    /*delegator.findByAnd("ProductCategoryMember",["productId":productId],null,false);*/
    supplierName = delegator.findByAnd("PartyRoleNameDetail",["partyId":shipment.partyIdFrom],null,false);
    if(UtilValidate.isNotEmpty(productId)){
        conditions.add(EntityCondition.makeCondition("productId",EntityOperator.EQUALS,productId))
    }
    conditions.add(EntityCondition.makeCondition("shipmentId",EntityOperator.EQUALS,shipment.shipmentId))
    shipmentsItemList =  delegator.findList("ShipmentItem", EntityCondition.makeCondition(conditions,EntityOperator.AND), null, null, null, false);
    for(GenericValue shipmentItem:shipmentsItemList){
		List shipmentReceiptCon = [];
        shipmentReceiptCon.add(EntityCondition.makeCondition("shipmentId",EntityOperator.EQUALS,shipment.shipmentId));
		shipmentReceiptCon.add(EntityCondition.makeCondition("productId",EntityOperator.EQUALS,shipmentItem.productId));
        List shipmentReceiptList = delegator.findList("ShipmentReceipt", EntityCondition.makeCondition(shipmentReceiptCon,EntityOperator.AND), null, null, null, false);
		for(GenericValue shipmentReceipt : shipmentReceiptList){
            generateMap(shipment,supplierName[0],shipmentReceipt)
		}
    }


}


def generateMap(shipment,supplierName,shipmentReceipt){
    shipmentMaterialReceivedMap = [:];
    shipmentMaterialReceivedMap.put("orderNumber",shipment.primaryOrderId);
    if(UtilValidate.isNotEmpty(supplierName)){
        shipmentMaterialReceivedMap.put("supplier",supplierName.groupName);
    }
    GenericValue orderItemGv = delegator.findOne("OrderItem",["orderId":shipment.primaryOrderId,"orderItemSeqId":shipmentReceipt.orderItemSeqId],false);
    GenericValue productGv= orderItemGv.getRelatedOne("Product")
    GenericValue uomGv = delegator.findOne("Uom",["uomId":productGv.quantityUomId],false);
    shipmentMaterialReceivedMap.put("receivedOn",shipment.createdDate);
    shipmentMaterialReceivedMap.put("unitPrice",orderItemGv.unitPrice);
    shipmentMaterialReceivedMap.put("productName",productGv.productName);
    shipmentMaterialReceivedMap.put("unit",uomGv.description);
    BigDecimal totalPrice = shipmentReceipt.getBigDecimal("quantityAccepted").multiply(orderItemGv.getBigDecimal("unitPrice"))
    shipmentMaterialReceivedMap.put("totalPrice",totalPrice);
    orderItemBilling = delegator.findByAnd("OrderItemBilling",["orderId":shipment.primaryOrderId,"shipmentReceiptId":shipmentReceipt.receiptId]);
    shipmentMaterialReceivedMap.put("invoice", UtilValidate.isNotEmpty(orderItemBilling)?orderItemBilling.get(0).get("invoiceId"):"");
    shipmentMaterialReceivedMap.put("receivedQty",shipmentReceipt.quantityAccepted);
    shipmentMaterialReceivedList.add(shipmentMaterialReceivedMap);
}


def getListOfShipments(){
    listOfShipments = [];
    conditions = [];
    if(UtilValidate.isNotEmpty(fromDate)){
        conditions.add(EntityCondition.makeCondition("createdDate",EntityOperator.GREATER_THAN_EQUAL_TO,UtilDateTime.getDayStart(UtilDateTime.dateStringToTimestampParser(fromDate))));
    }

    if(UtilValidate.isNotEmpty(thruDate)){
        conditions.add(EntityCondition.makeCondition("createdDate",EntityOperator.LESS_THAN_EQUAL_TO,UtilDateTime.getDayEnd(UtilDateTime.dateStringToTimestampParser(thruDate))));
    }
    if(UtilValidate.isNotEmpty(supplierId)){
        conditions.add(EntityCondition.makeCondition("partyIdFrom",EntityOperator.EQUALS,supplierId));
    }
    conditions.add(EntityCondition.makeCondition("shipmentTypeId",EntityOperator.EQUALS,"PURCHASE_SHIPMENT"));
    conditions.add(EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,"PURCH_SHIP_RECEIVED"));
    listOfShipments = delegator.findList("Shipment", EntityCondition.makeCondition(conditions,EntityOperator.AND), null, null, null, false);
    return listOfShipments;
}

context.shipmentMaterialReceivedList = shipmentMaterialReceivedList;