import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.product.facility.util.FacilityReportUtil;

String facilityId = parameters.facilityId;
String productId = parameters.productId;
String supplierId = parameters.partyId;
SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
Timestamp fromDate = new Timestamp(dateFormatter.parse(request.getParameter("fromDate")).getTime());
Timestamp thruDate = new Timestamp(dateFormatter.parse(request.getParameter("thruDate")).getTime());
condition = [];
if(fromDate)
 condition.add(EntityCondition.makeCondition("datetimeReceived",EntityOperator.GREATER_THAN_EQUAL_TO,UtilDateTime.getDayStart(fromDate)));
if(thruDate)
 condition.add(EntityCondition.makeCondition("datetimeReceived",EntityOperator.LESS_THAN_EQUAL_TO,UtilDateTime.getDayEnd(thruDate)));
if(productId)
 condition.add(EntityCondition.makeCondition("productId",EntityOperator.EQUALS,productId));
 
condition.add(EntityCondition.makeCondition("shipmentId",EntityOperator.NOT_EQUAL,null));
condition.add(EntityCondition.makeCondition("facilityId",EntityOperator.EQUALS,facilityId));

List<GenericValue> shipmentReceiptAndItemList = delegator.findList("ShipmentReceiptAndItem",EntityCondition.makeCondition(condition,EntityOperator.AND),null,null,null,false);
List<Map<String, Object>> qmrList = new ArrayList<Map<String, Object>>();
List<String> headers = new ArrayList<String>();
Map<String,List<BigDecimal>> dynamicMap = new HashMap<>();
for(GenericValue shipmentReceiptAndItem : shipmentReceiptAndItemList){
	GenericValue shipment = delegator.findOne("Shipment",UtilMisc.toMap("shipmentId",shipmentReceiptAndItem.getString("shipmentId")),false);
	if(UtilValidate.isNotEmpty(shipment) && UtilValidate.isNotEmpty(supplierId) && !shipment.getString("partyIdFrom").equals(supplierId))
		continue;
	headers = new ArrayList<String>();
	Map<String, Object> qmrMap = new HashMap<String,Object>();
	qmrMap.put("receivedOn", shipmentReceiptAndItem.getString("datetimeReceived"));
	qmrMap.put("shipmentId", shipmentReceiptAndItem.getString("shipmentId"));
	GenericValue partyGroup = delegator.findOne("PartyGroup",UtilMisc.toMap("partyId",shipment.getString("partyIdFrom")),false);
	qmrMap.put("supplierName", partyGroup.getString("groupName"));
	qmrMap.put("type", shipment.getString("shipmentTypeId"));
	qmrMap.put("truck", shipment.getString("truckNumber"));
	qmrMap.put("netWeight", "");
	
	List<GenericValue> productAttributeList = delegator.findByAnd("ProductAttribute",UtilMisc.toMap("productId",productId),null,false);
	List<BigDecimal> lineItems = new ArrayList<BigDecimal>();
	for(GenericValue productAttribute : productAttributeList){ 
		List<GenericValue> inventoryItemAttributeList = delegator.findByAnd("InventoryItemAttribute",UtilMisc.toMap("inventoryItemId",shipmentReceiptAndItem.getString("inventoryItemId"),"attrName",productAttribute.getString("attrName")),["-effectiveDate"],false);
		BigDecimal attrValue = BigDecimal.ZERO;
		if(UtilValidate.isNotEmpty(inventoryItemAttributeList)){
			attrValue = new BigDecimal(EntityUtil.getFirst(inventoryItemAttributeList).getString("attrValue"));
		}
		lineItems.add(attrValue);
		headers.add(productAttribute.getString("attrName"));
		
		/* Below code is for each column */
		if(dynamicMap.containsKey(productAttribute.getString("attrName"))){
			List<BigDecimal> li = dynamicMap.get(productAttribute.getString("attrName"));
			li.add(attrValue);
		}else{
			List<BigDecimal> list = new ArrayList<BigDecimal>();
			list.add(attrValue);
			dynamicMap.put(productAttribute.getString("attrName"), list );
		}
		
	}
	
	qmrMap.put("items", lineItems);
	qmrList.add(qmrMap);
}

List<BigDecimal> averageList = new ArrayList<>();
List<BigDecimal> minValues = new ArrayList<>();
List<BigDecimal> maxValues = new ArrayList<>();
List<Double> sdevs = new ArrayList<>();
for(String header : headers){
	BigDecimal average = BigDecimal.ZERO;
	for(BigDecimal value : dynamicMap.get(header)){
		average = average.add(value);
	}
	average = average/dynamicMap.get(header).size();
	averageList.add(average.setScale(2, RoundingMode.HALF_EVEN));
	minValues.add(FacilityReportUtil.getMinValue(dynamicMap.get(header)));
	maxValues.add(FacilityReportUtil.getMaxValue(dynamicMap.get(header)));
	sdevs.add(FacilityReportUtil.getStandardDeviation(dynamicMap.get(header)));
}
context.put("qmrList",qmrList);
context.put("headers",headers);
context.put("averageList",averageList);
context.put("minValues",minValues);
context.put("maxValues",maxValues);
context.put("sdevs",sdevs);
context.put("productId", productId);
context.put("fromDate", fromDate);
context.put("thruDate", thruDate);






