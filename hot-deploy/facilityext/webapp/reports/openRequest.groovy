import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;


String facilityId = parameters.facilityId;
List<Map<String, Object>> openRequestList = new ArrayList<>();

findIncomingShipmentsConds = [];

findIncomingShipmentsTypeConds = [];
findIncomingShipmentsTypeConds.add(EntityCondition.makeCondition("shipmentTypeId", EntityOperator.EQUALS, "INCOMING_SHIPMENT"));
findIncomingShipmentsTypeConds.add(EntityCondition.makeCondition("shipmentTypeId", EntityOperator.EQUALS, "PURCHASE_SHIPMENT"));
findIncomingShipmentsTypeConds.add(EntityCondition.makeCondition("shipmentTypeId", EntityOperator.EQUALS, "SALES_RETURN"));
findIncomingShipmentsConds.add(EntityCondition.makeCondition(findIncomingShipmentsTypeConds, EntityOperator.OR));

findIncomingShipmentsStatusConds = [];
findIncomingShipmentsStatusConds.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "SHIPMENT_DELIVERED"));
findIncomingShipmentsStatusConds.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "SHIPMENT_CANCELLED"));
findIncomingShipmentsStatusConds.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "PURCH_SHIP_RECEIVED"));
findIncomingShipmentsConds.add(EntityCondition.makeCondition(findIncomingShipmentsStatusConds, EntityOperator.AND));
	

List<GenericValue> inventoryRequisitionList = delegator.findByAnd("InventoryRequisition",UtilMisc.toMap("facilityIdFrom",facilityId),null,false);
for(GenericValue inventoryRequisition : inventoryRequisitionList){
	List<GenericValue> inventoryRequisitionItemList = delegator.findByAnd("InventoryRequisitionItem",UtilMisc.toMap("invRequisitionId",inventoryRequisition.getString("invRequisitionId")),null,false);
	for(GenericValue inventoryRequisitionItem : inventoryRequisitionItemList){
		Map<String, Object> openReqMap = new HashMap<String, Object>();
		resultOutput = dispatcher.runSync("getInventoryAvailableByFacility", [productId : inventoryRequisitionItem.getString("productId"), facilityId : facilityId]);
		Double atp = new Double(resultOutput.availableToPromiseTotal);
		// TODO: get all incoming shipments not yet arrived coming into facility that this product is in, use a view entity with ShipmentAndItem
		findIncomingShipmentsConds.add(EntityCondition.makeCondition('productId', EntityOperator.EQUALS, inventoryRequisitionItem.getString("productId")));
		findIncomingShipmentsConds.add(EntityCondition.makeCondition('destinationFacilityId', EntityOperator.EQUALS, facilityId));
		findIncomingShipmentsStatusCondition = EntityCondition.makeCondition(findIncomingShipmentsConds, EntityOperator.AND);
		incomingShipmentAndItems = delegator.findList("ShipmentAndItem", findIncomingShipmentsStatusCondition, null, null, null, false);
		BigDecimal quantityArival = BigDecimal.ZERO;
		for(GenericValue gv : incomingShipmentAndItems){
			quantityArival = quantityArival.add(gv.getBigDecimal("quantity"));
		}
		
		Boolean isExists = false;
		List<Map<String, Object>> openReqMapCopy = new ArrayList<>();
		openReqMapCopy.addAll(openRequestList);
		int removeNum = 0;
        for(Map<String, Object> map : openReqMapCopy){
			if( inventoryRequisitionItem.getString("productId").equals(map.get("productId")) ){
				isExists = true;
				Map oldMap = openRequestList.get(removeNum);
				BigDecimal reqQuantity = inventoryRequisitionItem.getBigDecimal("quantity");
				BigDecimal oldReqQuantity = new BigDecimal(oldMap.get("reqQuantity"));
				oldMap.put("reqQuantity", reqQuantity.add(oldReqQuantity) );
				oldMap.put("netDifference", (atp+quantityArival.toDouble()) - new Double(oldMap.get("reqQuantity")) );
			}
			removeNum++;
		}
		
		if(!isExists){		
			openReqMap.put("productId", inventoryRequisitionItem.getString("productId"));
			openReqMap.put("reqQuantity", inventoryRequisitionItem.getBigDecimal("quantity"));
			openReqMap.put("availableQty", atp);
			openReqMap.put("quantityArival", quantityArival);
			openReqMap.put("netDifference", (atp+quantityArival.toDouble()) - inventoryRequisitionItem.getBigDecimal("quantity").toDouble());
			openRequestList.add(openReqMap);
		}
	}
}
context.put("openRequestList",openRequestList);
