import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;

import com.ndz.util.ProductHelper;


String invRequisitionId = request.getParameter("invRequisitionId");

GenericValue inventoryRequisition = delegator.findOne("InventoryRequisition", UtilMisc.toMap("invRequisitionId",invRequisitionId), false);

EntityCondition condition = EntityCondition.makeCondition("invRequisitionId",EntityOperator.EQUALS,invRequisitionId);

List<GenericValue> inventoryRequisitionItemList = delegator.findList("InventoryRequisitionItem",condition,null,null,null,false);

List<Map<String, Object>> inventoryRequisitionItemListMap = new ArrayList();

boolean isAtpQohAvailable = true;

for(GenericValue gv : inventoryRequisitionItemList){
	Map<String, Object> map = new HashMap();
	
	resultOutput = dispatcher.runSync("getInventoryAvailableByFacility", [productId : gv.getString("productId"), facilityId : inventoryRequisition.getString("facilityIdFrom")]);
	Double qoh = new Double(resultOutput.quantityOnHandTotal);
	Double atp = new Double(resultOutput.availableToPromiseTotal);
	
	Double quantity = gv.getBigDecimal("quantity").toDouble();
	map.put("invReqItemId", gv.getString("invReqItemId"));
	map.put("invRequisitionId", gv.getString("invRequisitionId"));
	map.put("productId", gv.getString("productId"));
	map.put("costComponentTypeId", gv.getString("costComponentTypeId"));
	map.put("quantity", gv.getString("quantity"));
	map.put("acceptedQuantity", gv.getString("acceptedQuantity"));
	map.put("note", gv.getString("note"));
	map.put("quantity", gv.getString("quantity"));
	map.put("atp", atp);
	map.put("qoh", qoh);
	map.put("glAccountCategoryId", gv.getString("glAccountCategoryId"));
	map.put("returnable", gv.getString("returnable"));
	if(quantity.compareTo(atp) == 1 || quantity.compareTo(qoh) ==1){
		isAtpQohAvailable = false;
	}
	inventoryRequisitionItemListMap.add(map);
	
}


context.inventoryRequisition = inventoryRequisition;
context.inventoryRequisitionItemList = inventoryRequisitionItemListMap;
context.isAtpQohAvailable = isAtpQohAvailable;

return "success";