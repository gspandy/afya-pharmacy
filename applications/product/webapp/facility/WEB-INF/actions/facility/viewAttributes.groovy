import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;



GenericDelegator delegator = delegator;
String inventoryItemId = parameters.inventoryItemId;
EntityCondition condition = EntityCondition.makeCondition("inventoryItemId",EntityOperator.EQUALS,inventoryItemId);
EntityFindOptions entityFindOption = new EntityFindOptions();
entityFindOption.setDistinct(true);
List<GenericValue> invItemAttributesDate = delegator.findList("InventoryItemAttribute", condition, ["effectiveDate"] as Set, ["-effectiveDate"], entityFindOption, false);

List<Map<String,Object>> itemList = new ArrayList<Map<String,Object>>();
List<String> headers = new ArrayList<String>();

for(GenericValue gv : invItemAttributesDate){
	List<GenericValue> invItemAttList = delegator.findByAnd("InventoryItemAttribute", UtilMisc.toMap("inventoryItemId",inventoryItemId,"effectiveDate",gv.getTimestamp("effectiveDate")),null,false);
	Map<String, Object> map = new HashMap<String, Object>();
	for(GenericValue invGv : invItemAttList){
		map.put(invGv.getString("attrName"), invGv.getString("attrValue"));
	}
	headers = map.keySet().asList();
	map.put("effectiveDate", gv.getTimestamp("effectiveDate"));
	itemList.add(map);
}
context.put("itemList",itemList);
context.put("headers",headers);
return "success";