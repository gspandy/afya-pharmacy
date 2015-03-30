import org.ofbiz.base.util.UtilDateTime
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.jdbc.SQLProcessor
import org.ofbiz.entity.util.EntityUtil

import java.sql.ResultSet
import java.sql.Timestamp

productId = parameters.productId;
Timestamp today = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
if(UtilValidate.isNotEmpty(productId)){
    GenericValue productGv = delegator.findOne("Product",["productId":productId],false);
    context.productName = productGv.getString("productName");

    context.todaySalesReport = getTodaySalesReport(productId,today);
    context.monthToDateSalesReport = getMonthToDateSalesReport(productId,today);
    context.yearToDateSalesReport = getYearToDateSalesReport(productId,today);

    context.todayMaterialCostReport = getTodayMaterialCostReport(productId,today);
    context.monthToDateMaterialCostReport = getMonthToDateMaterialCostReport(productId,today);
    context.yearToDateMaterialCostReport = getYearToDateMaterialCostReport(productId,today);
}

/*Start Material cost report*/
def getTodayMaterialCostReport(String productId,Timestamp today){
    BigDecimal qty = getQuantityForMaterialCostReport(productId,today,today);
    BigDecimal cost = getCostForMaterialCostReport(productId,today,today);
    return ["quantity":qty,"avgCost":getAvgCost(qty,cost),"cost":cost];
}

def getAvgCost(BigDecimal qty,BigDecimal cost){
    BigDecimal avgCost = BigDecimal.ZERO;
    if(qty.compareTo(BigDecimal.ZERO)!=0)
        avgCost = cost/qty;
    avgCost = avgCost.setScale(2,BigDecimal.ROUND_CEILING);
    return avgCost;
}

def getMonthToDateMaterialCostReport(String productId,Timestamp today){
    Timestamp monthStart =  UtilDateTime.getMonthStart(today);
    BigDecimal qty = getQuantityForMaterialCostReport(productId,monthStart,today);
    BigDecimal cost = getCostForMaterialCostReport(productId,monthStart,today);
    return ["quantity":qty,"avgCost":getAvgCost(qty,cost)];
}

def getYearToDateMaterialCostReport(String productId,Timestamp today){
    Timestamp yearStart =  UtilDateTime.getYearStart(today);
    BigDecimal qty = getQuantityForMaterialCostReport(productId,yearStart,today);
    BigDecimal cost = getCostForMaterialCostReport(productId,yearStart,today);
    return ["quantity":qty,"avgCost":getAvgCost(qty,cost)];
}

def getQuantityForMaterialCostReport(String productId,Timestamp fromDate,Timestamp toDate){
    BigDecimal qty = BigDecimal.ZERO;
    String query = "SELECT COALESCE(SUM(W.`QUANTITY_PRODUCED`),0) AS QTY FROM work_effort W " +
            " JOIN work_effort_good_standard WEGS ON W.`WORK_EFFORT_ID`=WEGS.`WORK_EFFORT_ID` " +
            " WHERE W.`WORK_EFFORT_TYPE_ID`='PROD_ORDER_HEADER' " +
            " AND W.`CURRENT_STATUS_ID` = 'PRUN_CLOSED' " +
            " AND WEGS.`PRODUCT_ID`='"+productId+"' " +
            " AND W.`CREATED_DATE`>='"+UtilDateTime.getDayStart(fromDate) +"'"+
            " AND W.`CREATED_DATE`<='"+UtilDateTime.getDayEnd(toDate) +"'"+
            " AND WEGS.`WORK_EFFORT_GOOD_STD_TYPE_ID`='PRUN_PROD_DELIV'";
    ResultSet qtyResultSet = execute(query);
    while(qtyResultSet.next()){
            qty = qtyResultSet.getBigDecimal("QTY");
    }
    qty = qty.setScale(2,BigDecimal.ROUND_CEILING);
    return qty;
}

def getCostForMaterialCostReport(String productId,Timestamp fromDate,Timestamp toDate){
    BigDecimal cost = BigDecimal.ZERO;
    String query = " SELECT COALESCE(SUM(COST),0) AS SUM FROM work_effort W" +
            " JOIN WORK_EFFORT W1 ON W.`WORK_EFFORT_PARENT_ID`=W1.`WORK_EFFORT_ID`"+
            " JOIN work_effort_good_standard WEGS ON W1.`WORK_EFFORT_ID`=WEGS.`WORK_EFFORT_ID`"+
            " JOIN COST_COMPONENT C ON C.`WORK_EFFORT_ID`=W.`WORK_EFFORT_ID`"+
            " AND WEGS.`WORK_EFFORT_GOOD_STD_TYPE_ID`='PRUN_PROD_DELIV' " +
            " AND WEGS.`PRODUCT_ID`='"+productId+"'"+
            " AND W.`CREATED_DATE`>='"+UtilDateTime.getDayStart(fromDate)+"'"+
            " AND W.`CREATED_DATE`<='"+UtilDateTime.getDayEnd(toDate)+"'"+
            " AND C.`COST_COMPONENT_TYPE_ID`='ACTUAL_MAT_COST'";
    ResultSet costResultSet = execute(query);
    while(costResultSet.next()){
        cost = costResultSet.getBigDecimal("SUM");
    }
    cost = cost.setScale(2,BigDecimal.ROUND_CEILING);
    return cost;
}

def execute(String query){
    SQLProcessor sqlproc = new SQLProcessor(delegator.getGroupHelperInfo("org.ofbiz"));
    sqlproc.prepareStatement(query);
    return sqlproc.executeQuery();
}
/*End Material cost report*/


/*Sales cost report*/
def getTodaySalesReport(String productId,Timestamp today){
    List<GenericValue>  shipmentAndItemList =  getShipmentAndItemDetails(today,today,productId,['SHIPMENT_SHIPPED','SHIPMENT_DELIVERED']);
    return getQuantityAndAvgCost(shipmentAndItemList);
}

def getMonthToDateSalesReport(String productId,Timestamp today){
    Timestamp monthStart =  UtilDateTime.getMonthStart(today);
    List<GenericValue>  shipmentAndItemList =  getShipmentAndItemDetails(monthStart,today,productId,['SHIPMENT_SHIPPED','SHIPMENT_DELIVERED']);
    return getQuantityAndAvgCost(shipmentAndItemList);
}
def getYearToDateSalesReport(String productId,Timestamp today){
    Timestamp yearStart =  UtilDateTime.getYearStart(today);
    List<GenericValue>  shipmentAndItemList =  getShipmentAndItemDetails(yearStart,today,productId,['SHIPMENT_SHIPPED','SHIPMENT_DELIVERED']);
    return getQuantityAndAvgCost(shipmentAndItemList);
}
def getQuantityAndAvgCost(List<GenericValue>  shipmentAndItemList){
    BigDecimal quantity = BigDecimal.ZERO;
    quantity = quantity.setScale(2, BigDecimal.ROUND_CEILING);
    BigDecimal cost = BigDecimal.ZERO;
    cost = cost.setScale(2,BigDecimal.ROUND_CEILING);
    for(GenericValue shipmentAndItem:shipmentAndItemList){
        quantity = quantity.add((BigDecimal)shipmentAndItem.get("quantity"));
        cost = cost.add(getCostFromOrderItem(shipmentAndItem.getString("productId"),shipmentAndItem.getString("shipmentItemSeqId")).multiply(shipmentAndItem.getBigDecimal("quantity")));
    }
    BigDecimal avgCost = BigDecimal.ZERO;
    if(quantity.compareTo(BigDecimal.ZERO) != 0 ){
        avgCost = cost.divide(quantity,2,BigDecimal.ROUND_CEILING);
    }
    return ["quantity":quantity,"avgCost":avgCost,"cost":cost];
}

def getCostFromOrderItem(String productId,String seqId){
    List orderItemGvList = delegator.findByAnd("OrderItem",UtilMisc.toMap("orderItemSeqId",seqId,"productId",productId));
    GenericValue orderItemGv = EntityUtil.getFirst(orderItemGvList);
    BigDecimal unitPrice = orderItemGv.getBigDecimal("unitPrice");
    return unitPrice;
}

def getShipmentAndItemDetails(Timestamp from,Timestamp to,String productId,List statusId){
    conditions = [];
    conditions.add(EntityCondition.makeCondition("createdDate",EntityOperator.GREATER_THAN_EQUAL_TO,UtilDateTime.getDayStart(from)));
    conditions.add(EntityCondition.makeCondition("createdDate", EntityOperator.LESS_THAN_EQUAL_TO,UtilDateTime.getDayEnd(to)));
    conditions.add(EntityCondition.makeCondition("productId",EntityOperator.EQUALS,productId));
    conditions.add(EntityCondition.makeCondition("statusId", EntityOperator.IN,statusId));
    return delegator.findList("ShipmentAndItem", EntityCondition.makeCondition(conditions,EntityOperator.AND),
            null, null, null, false);
}