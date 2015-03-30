import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator

import java.sql.Timestamp
import java.text.SimpleDateFormat;
import java.util.*;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilValidate;

customerPartyId = parameters.partyId;
fromDate = parameters.fromDate;
thruDate = parameters.thruDate;

pendingLoadingSlipList = []
conditions = [];
if(UtilValidate.isNotEmpty(customerPartyId)){
    conditions.add(EntityCondition.makeCondition("partyIdTo",customerPartyId));
}


if(UtilValidate.isNotEmpty(fromDate)){
    conditions.add(EntityCondition.makeCondition("createdDate",EntityOperator.GREATER_THAN_EQUAL_TO,UtilDateTime.getDayStart(dateParser(fromDate))));
}

if(UtilValidate.isNotEmpty(thruDate)){
    conditions.add(EntityCondition.makeCondition("createdDate",EntityOperator.LESS_THAN_EQUAL_TO,UtilDateTime.getDayEnd(dateParser(thruDate))));
}
conditions.add(EntityCondition.makeCondition("shipmentTypeId","SALES_SHIPMENT"));
conditions.add(EntityCondition.makeCondition("statusId",EntityOperator.IN,["SHIPMENT_PICKED","SHIPMENT_INPUT"]));

shipmentList = delegator.findList("Shipment", EntityCondition.makeCondition(conditions,EntityOperator.AND), null, null, null, false);



for(GenericValue shipment:shipmentList){
    pendingLoadingSlipsMap = [:];
    totalQuantity = 0;
    shipmentsItems = delegator.findByAnd("ShipmentItem",["shipmentId":shipment.shipmentId]);
    party = delegator.findOne("PartyGroup",["partyId":shipment.partyIdTo],false);
    for(GenericValue shipmentsItem:shipmentsItems){
      totalQuantity = totalQuantity + shipmentsItem.quantity;
    }
    pendingLoadingSlipsMap.put("customerPartyId",party.partyId);
    pendingLoadingSlipsMap.put("customerName",party.groupName);
    pendingLoadingSlipsMap.put("productId",UtilValidate.isNotEmpty(shipmentsItems)?shipmentsItems.get(0).productId:"");
    pendingLoadingSlipsMap.put("shipmentId",shipment.shipmentId);
    pendingLoadingSlipsMap.put("orderId",shipment.primaryOrderId);
    pendingLoadingSlipsMap.put("shipmentCreatedDate",shipment.createdDate);
    pendingLoadingSlipsMap.put("shipmentQty",totalQuantity);
    pendingLoadingSlipList.add(pendingLoadingSlipsMap);
}


def dateParser(String date){
    SimpleDateFormat dateFormat = new SimpleDateFormat(UtilDateTime.DATE_FORMAT);
    formattedDate =  dateFormat.parse(date);
    return new Timestamp(formattedDate.getTime());
}
 customerName = delegator.findByAnd("PartyRoleNameDetail",["partyId":customerPartyId]);

if(customerName!=null && customerName.size()>0){
  context.groupName = customerName[0].groupName;
}



context.pendingLoadingSlipList =pendingLoadingSlipList;