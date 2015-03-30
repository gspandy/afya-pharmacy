import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.GenericEntityException;


import java.sql.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.*;
import org.ofbiz.content.report.*;

delegator = request.getAttribute("delegator");

fromDateStr = request.getParameter("fromDate");
toDateStr = request.getParameter("toDate");

fromDate = null;
toDate = null;
try {
    if (fromDateStr) {
        fromDate = Timestamp.valueOf(fromDateStr);
    }
    if (toDateStr) {
        toDate = Timestamp.valueOf(toDateStr);
    }
} catch (Exception e) {
    Debug.logError(e);
}

/* we'll have to work on getting this to work again, maybe with the ad-hoc view entity feature...
 
groupName = request.getParameter("groupName");
if (groupName.equals("product")) {
    groupName = "order_item.product_id";
    reportName = "orderitemreport.jasper";
} else if (groupName.equals("orderStatus")) {
    groupName = "status_item.description";
    reportName = "orderreport.jasper";
} else if (groupName.equals("itemStatus")) {
    groupName = "item_status.description";
    reportName = "orderitemreport.jasper";
} else if (groupName.equals("adjustment")) {
    groupName = "order_adjustment_type.description";
    reportName = "orderitemreport.jasper";
} else if (groupName.equals("ship")) {
    groupName = "concat(concat(order_shipment_preference.carrier_party_id, ' - '), shipment_method_type.description)";
    reportName = "orderreport.jasper";
} else if (groupName.equals("payment")) {
    groupName = "payment_method_type.description";
    reportName = "orderreport.jasper";
} else if (groupName.length() < 4) {
    groupName = "status_item.description";
    reportName = "orderreport.jasper";
}
sbSql.append( groupName +" as GroupName, ");
sbSql.append(" order_item.unit_price * order_item.quantity as purchaseAmount, ");
*/


conditionList = [];
if (fromDate) {
    conditionList.add(EntityCondition.makeCondition("orderDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate));
}
if (toDate) {
    conditionList.add(EntityCondition.makeCondition("orderDate", EntityOperator.LESS_THAN_EQUAL_TO, toDate));
}
entityCondition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
orderByList = ["productId", "expireDate","locationSeqId"];


org.ofbiz.entity.transaction.TransactionUtil.begin();
allList = []
eli = delegator.find("InventoryItem", entityCondition, null, null, orderByList, null);
jrDataSource = new JREntityListIteratorDataSource(eli);

jrParameters = [:];
jrParameters.dateRange = fromDateStr + " - " + toDateStr;
if(jrDataSource){
request.setAttribute("jrDataSource", jrDataSource);
request.setAttribute("jrParameters", jrParameters);
}
org.ofbiz.entity.transaction.TransactionUtil.commit();

return "success";
