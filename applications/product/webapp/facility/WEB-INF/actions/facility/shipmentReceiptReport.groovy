import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;



String supplierId = parameters.supplierId;
String transformerId = parameters.transformerId;

List list = new ArrayList<>();

if(UtilValidate.isNotEmpty(supplierId))
	list.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, supplierId));
if(UtilValidate.isNotEmpty(transformerId))
	list.add(EntityCondition.makeCondition("transformerId", EntityOperator.EQUALS, transformerId));
if (fromDate) {
	list.add(EntityCondition.makeCondition("createdDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.getDayStart(fromDate)));
}
if (thruDate) {
	list.add(EntityCondition.makeCondition("createdDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.getDayEnd(thruDate)));
}
list.add(EntityCondition.makeCondition("shipmentTypeId",EntityOperator.EQUALS,"PURCHASE_SHIPMENT"));
list.add(EntityCondition.makeCondition("statusId",EntityOperator.IN,["PURCH_SHIP_RECEIVED","PURCH_SHIP_SHIPPED"]));
EntityCondition condition = EntityCondition.makeCondition(list,EntityOperator.AND);

List<GenericValue> shipmentAndItemList = delegator.findList("ShipmentAndItem",condition,null,null,null,false);
List<Map<String,Object>> mainList = new ArrayList<>();
for(GenericValue gv : shipmentAndItemList){
	Map<String,Object> map = new HashMap<String, Object>();
	map.put("supplierId", gv.getString("partyIdFrom"));
	map.put("productId",gv.getString("productId"));
	List<GenericValue> orderItemBillings = delegator.findByAnd("OrderItemBilling",UtilMisc.toMap("orderId",gv.getString("primaryOrderId")),null,false);
	if(UtilValidate.isNotEmpty(orderItemBillings))
		map.put("invoiceId",orderItemBillings.get(0).getString("invoiceId"));
	else
		map.put("invoiceId","");
	map.put("shipmentId",gv.getString("shipmentId"));
	map.put("loadingDate",gv.getString("createdDate"));
	List<GenericValue> shipmentReceipts = delegator.findByAnd("ShipmentReceipt",UtilMisc.toMap("shipmentId",gv.getString("shipmentId"),"productId",gv.getString("productId")),null,false);
	if(UtilValidate.isNotEmpty(shipmentReceipts))
		map.put("receivedOn",shipmentReceipts.get(0).getString("datetimeReceived"));
	else
		map.put("receivedOn",gv.getString(""));
	map.put("transporterId",gv.getString("transformerId"));
	map.put("truck",gv.getString("truckNumber"));
	map.put("transaction","");
	map.put("shippedQuantity",gv.getBigDecimal("quantity"));
	if(UtilValidate.isNotEmpty(shipmentReceipts)){
		map.put("receivedQuantity",shipmentReceipts.get(0).getBigDecimal("quantityAccepted"));
		map.put("rejectedQuantity",shipmentReceipts.get(0).getBigDecimal("quantityRejected"));
	}else{
		map.put("receivedQuantity",BigDecimal.ZERO);
		map.put("rejectedQuantity",BigDecimal.ZERO);
	}
	map.put("variance",  ((BigDecimal)map.get("shippedQuantity")).subtract( ((BigDecimal)map.get("receivedQuantity")).add((BigDecimal)map.get("rejectedQuantity")) )      );
	mainList.add(map);
}

context.fromDate = fromDate;
context.thruDate = thruDate;
context.put("mainList",mainList);

return "success";

