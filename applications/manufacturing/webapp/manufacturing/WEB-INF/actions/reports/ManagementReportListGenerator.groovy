import org.ofbiz.base.util.UtilDateTime
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityUtil

import java.sql.Timestamp
import java.text.SimpleDateFormat

String fromDateString = parameters.fromDate;

if(UtilValidate.isNotEmpty(fromDateString)){
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    Date fromDate = dateFormat.parse(fromDateString);
    Timestamp fromDateTimeStamp =  new Timestamp(fromDate.getTime());
    Timestamp previousDate = new Timestamp(UtilDateTime.getPreviousDay(fromDate).getTime());
    // cement 42.5N -> CEM0001
    // cement timange 32.5 N ->CEM0002
    // cement 42.5N Local - >SBC0001
    // cement 42.5N Export - >SBC0002
    // cement 32.5N LOcal - >SBC0003
    context.unloadedTrucks = getUnloadedTrucks(previousDate,['CEM0001','CEM0002','SBC0001','SBC0002','SBC0003']);

    List shipmentAndItemGv = getShipmentAndItemDetails(previousDate,["SHIPMENT_DELIVERED"],['CEM0001','CEM0002']);
    List shipmentAndItemForBaggedCement = getShipmentAndItemDetails(previousDate,["SHIPMENT_DELIVERED"],['SBC0001','SBC0002','SBC0003']);
    BigDecimal baggedCementQuantity = ((getTotalQuantity(shipmentAndItemForBaggedCement).qty).multiply(50)).divide(1000);
    BigDecimal bulkCementQuantity = getTotalQuantity(shipmentAndItemGv).qty;
    BigDecimal total = baggedCementQuantity.add(bulkCementQuantity);
    String metric = getTotalQuantity(shipmentAndItemGv).metric;
    if(metric.trim().size()<=0){
        metric = "Ton (metric)"
    }
    context.weighBridgeQuantity = total+ " "+metric;
    context.weighBridgeTrucks = shipmentAndItemGv.size()+shipmentAndItemForBaggedCement.size();
    shipmentAndItemGv = getShipmentAndItemDetails(previousDate,["SHIPMENT_PICKED","SHIPMENT_PACKED","SHIPMENT_SHIPPED"],['CEM0001','CEM0002']);
    shipmentAndItemForBaggedCement = getShipmentAndItemDetails(previousDate,["SHIPMENT_PICKED","SHIPMENT_PACKED","SHIPMENT_SHIPPED"],['SBC0001','SBC0002','SBC0003']);
     baggedCementQuantity = ((getTotalQuantity(shipmentAndItemForBaggedCement).qty).multiply(50)).divide(1000);
     bulkCementQuantity = getTotalQuantity(shipmentAndItemGv).qty;
     total = baggedCementQuantity.add(bulkCementQuantity);
    metric = getTotalQuantity(shipmentAndItemGv).metric;
    if(metric.trim().size()<=0){
        metric = "Ton (metric)"
    }
    context.packingAndLoadingQuantity = total+ " "+metric;
    context.packingAndLoadingTrucks = shipmentAndItemGv.size()+shipmentAndItemForBaggedCement.size();

    Map cementProducedQuantityAndMetrics = getProductions(previousDate,['CEM0001','CEM0002']);
    context.cementProduction = cementProducedQuantityAndMetrics.quantity+" "+cementProducedQuantityAndMetrics.quantityMetrics;

    Map clinkerProducedQuantityAndMetrics = getProductions(previousDate,['RAW0001']);
    context.clinkerProduction = clinkerProducedQuantityAndMetrics.quantity+" "+clinkerProducedQuantityAndMetrics.quantityMetrics;

    Map cementInSiloAvailableQuantityAndMetrics = getItemsInSiloOrShed(fromDateTimeStamp,['CEM0001','CEM0002']);
    BigDecimal cementInSiloAvailable = (cementInSiloAvailableQuantityAndMetrics.quantity).add(cementProducedQuantityAndMetrics.quantity);
    context.cementInSilo = cementInSiloAvailable+" "+cementInSiloAvailableQuantityAndMetrics.quantityMetrics;

    Map clinkerInShedAvailableQuantityAndMetrics = getItemsInSiloOrShed(fromDateTimeStamp,['RAW0001']);
    BigDecimal clinkerInShedAvailable = (clinkerInShedAvailableQuantityAndMetrics.quantity).add(clinkerProducedQuantityAndMetrics.quantity);
    context.clinkerInShed = clinkerInShedAvailable+" "+clinkerInShedAvailableQuantityAndMetrics.quantityMetrics;

    context.previousDate = UtilDateTime.getPreviousDay(fromDate);
    context.fromdate =  fromDateString;
}

def getUnloadedTrucks(Timestamp previousDate,List productId){
    List shipmentAndItemGv = getShipmentAndItemDetails(previousDate,["SHIPMENT_INPUT"],productId);
    return shipmentAndItemGv.size();
}

def getShipmentAndItemDetails(Timestamp previousDate,List shipmentStatusId,List productId){
    conditions = [];
    conditions.add(EntityCondition.makeCondition("createdDate",EntityOperator.GREATER_THAN_EQUAL_TO,UtilDateTime.getDayStart(previousDate)));
    conditions.add(EntityCondition.makeCondition("createdDate", EntityOperator.LESS_THAN_EQUAL_TO,UtilDateTime.getDayEnd(previousDate)));
    conditions.add(EntityCondition.makeCondition("productId",EntityOperator.IN,productId));
    conditions.add(EntityCondition.makeCondition("statusId", EntityOperator.IN,shipmentStatusId));
    return delegator.findList("ShipmentAndItem", EntityCondition.makeCondition(conditions,EntityOperator.AND),
            null, null, null, false);
}

def getTotalQuantity(List<GenericValue> shipmentAndItemGvList){
    BigDecimal quantity = BigDecimal.ZERO;
    String quantityMetrics = "";
    for(GenericValue shipmentAndItemGv:shipmentAndItemGvList){
        quantity = quantity.add((BigDecimal)shipmentAndItemGv.get("quantity"));
    }
    GenericValue shipmentAndItemGv = EntityUtil.getFirst(shipmentAndItemGvList);
    if(UtilValidate.isNotEmpty(shipmentAndItemGv)){
        quantityMetrics =  getMetrics(shipmentAndItemGv.getString("productId"));
    }
    quantity = quantity.setScale(2,BigDecimal.ROUND_CEILING);
    return ["qty":quantity,"metric":quantityMetrics];
}

def getMetrics(String productId){
    productGv = delegator.findOne("Product",["productId":productId],false);
    if(UtilValidate.isNotEmpty(productGv)){
        GenericValue uomGv =  delegator.findOne("Uom",["uomId":productGv.quantityUomId],false);
        return uomGv.getString("description");
    }
    return "";
}

def getProductions(Timestamp previousDate,List productId){
    BigDecimal quantity =BigDecimal.ZERO;
    String quantityMetrics = "";
    conditions = [];
    conditions.add(EntityCondition.makeCondition("actualStartDate",EntityOperator.GREATER_THAN_EQUAL_TO,UtilDateTime.getDayStart(previousDate)));
    conditions.add(EntityCondition.makeCondition("actualStartDate", EntityOperator.LESS_THAN_EQUAL_TO,UtilDateTime.getDayEnd(previousDate)));
    conditions.add(EntityCondition.makeCondition("productId",EntityOperator.IN,productId));
    conditions.add(EntityCondition.makeCondition("workEffortTypeId", EntityOperator.EQUALS,"PROD_ORDER_HEADER"));
    conditions.add(EntityCondition.makeCondition("currentStatusId", EntityOperator.EQUALS,"PRUN_CLOSED"));
    List<GenericValue> workEffortAndGoodsList =  delegator.findList("WorkEffortAndGoods", EntityCondition.makeCondition(conditions,EntityOperator.AND),
            null, null, null, false);
    for(GenericValue workEffortAndGoods:workEffortAndGoodsList){
        quantity = quantity.add((BigDecimal)workEffortAndGoods.get("estimatedQuantity"));
    }
    GenericValue workEffortAndGoods = EntityUtil.getFirst(workEffortAndGoodsList);
    if(UtilValidate.isNotEmpty(workEffortAndGoods)){
        quantityMetrics =  getMetrics(workEffortAndGoods.getString("productId"));
    }
    quantity = quantity.setScale(2,BigDecimal.ROUND_CEILING);
    return ["quantity":quantity,"quantityMetrics":quantityMetrics];
}

def getPreviousDayWorkEffortId(Timestamp selectedDate,List productId){
    Timestamp previousDate = new Timestamp(UtilDateTime.getPreviousDay(selectedDate).getTime());
    List workEffortId =[];
    BigDecimal quantity =BigDecimal.ZERO;
    conditions = [];
    conditions.add(EntityCondition.makeCondition("actualStartDate",EntityOperator.GREATER_THAN_EQUAL_TO,UtilDateTime.getDayStart(previousDate)));
    conditions.add(EntityCondition.makeCondition("actualStartDate", EntityOperator.LESS_THAN_EQUAL_TO,UtilDateTime.getDayEnd(previousDate)));
    conditions.add(EntityCondition.makeCondition("productId",EntityOperator.IN,productId));
    conditions.add(EntityCondition.makeCondition("workEffortTypeId", EntityOperator.EQUALS,"PROD_ORDER_HEADER"));
    conditions.add(EntityCondition.makeCondition("currentStatusId", EntityOperator.IN,["PRUN_CLOSED","PRUN_COMPLETED"]));
    List<GenericValue> workEffortAndGoodsList =  delegator.findList("WorkEffortAndGoods", EntityCondition.makeCondition(conditions,EntityOperator.AND),
            null, null, null, false);
    for(GenericValue workEffortAndGoods:workEffortAndGoodsList){
        workEffortId.add(workEffortAndGoods.getString("workEffortId"));
    }

    return ["workEffortId":workEffortId];
}

def getItemsInSiloOrShed(Timestamp selectedDate,List productId){
    BigDecimal quantity =BigDecimal.ZERO;
    Map workEffortIdAndQuantity = getPreviousDayWorkEffortId(selectedDate,productId);
    List workEffortId = workEffortIdAndQuantity.workEffortId;
    String quantityMetrics = "";
    orCondition = [];
    conditions = [];
    if(workEffortId.size()!=0){
        orCondition.add(EntityCondition.makeCondition("workEffortId",EntityOperator.NOT_IN,workEffortId));
        orCondition.add(EntityCondition.makeCondition("workEffortId",EntityOperator.EQUALS,null));
    }

    conditions.add(EntityCondition.makeCondition("effectiveDate", EntityOperator.LESS_THAN,UtilDateTime.getDayStart(selectedDate)));
    conditions.add(EntityCondition.makeCondition(orCondition,EntityOperator.OR));
    conditions.add(EntityCondition.makeCondition("productId",EntityOperator.IN,productId));
    List<GenericValue> inventoryItemAndDetailList =  delegator.findList("InventoryItemAndDetail", EntityCondition.makeCondition(conditions,EntityOperator.AND),
            null, null, null, false);
    for(GenericValue inventoryItemAndDetail:inventoryItemAndDetailList){
        quantity = quantity.add((BigDecimal)inventoryItemAndDetail.get("quantityOnHandDiff"));
    }
    GenericValue inventoryItemAndDetail = EntityUtil.getFirst(inventoryItemAndDetailList);
    if(UtilValidate.isNotEmpty(inventoryItemAndDetail)){
        quantityMetrics =  getMetrics(inventoryItemAndDetail.getString("productId"));
    }
    quantity = quantity.setScale(2,BigDecimal.ROUND_CEILING);
    return ["quantity":quantity,"quantityMetrics":quantityMetrics];
}