import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.product.facility.util.FacilityReportUtil;



String facilityId = parameters.facilityId;
String productCategoryId = parameters.productCategoryId;
SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
Timestamp fromDate = new Timestamp(dateFormatter.parse(request.getParameter("fromDate")).getTime());
Timestamp thruDate = new Timestamp(dateFormatter.parse(request.getParameter("thruDate")).getTime());


conditions = [];
conditions.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId));
conditions.add(EntityCondition.makeCondition("datetimeReceived", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.getDayStart(fromDate)));
conditions.add(EntityCondition.makeCondition("datetimeReceived", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.getDayEnd(thruDate)));
if(UtilValidate.isNotEmpty(productCategoryId)){
	productIdList=[];
	ProductCategoryMemberList = delegator.findByAnd("ProductCategoryMember",["productCategoryId":productCategoryId],null,false);
	for(GenericValue productCategoryMember:ProductCategoryMemberList){
		productIdList.add(productCategoryMember.productId);
	}
	conditions.add(EntityCondition.makeCondition("productId",EntityOperator.IN,productIdList));
}
EntityCondition mainCondition = EntityCondition.makeCondition(conditions, EntityOperator.AND);

List<GenericValue> inventoryItemList = delegator.findList("InventoryItem",mainCondition,null,null,null,false);
List<Map<String, Object>> stockVarianceList = new ArrayList<>();
for(GenericValue inventoryItem : inventoryItemList){
	BigDecimal variance = BigDecimal.ZERO;
	Map<String, Object> stockVarianceMap = new HashMap<String, Object>();
	String productId = inventoryItem.getString("productId");
	String inventoryItemId = inventoryItem.getString("inventoryItemId");
	conditions = [];
	conditions.add(EntityCondition.makeCondition("physicalInventoryDate",EntityOperator.GREATER_THAN_EQUAL_TO,UtilDateTime.getDayStart(fromDate)));
	conditions.add(EntityCondition.makeCondition("physicalInventoryDate",EntityOperator.LESS_THAN_EQUAL_TO,UtilDateTime.getDayEnd(thruDate)));
	conditions.add(EntityCondition.makeCondition("inventoryItemId",EntityOperator.EQUALS,inventoryItemId));
	EntityCondition condition = EntityCondition.makeCondition(conditions, EntityOperator.AND);
	List<GenericValue> physicalInventoryAndVarianceList = delegator.findList("PhysicalInventoryAndVariance",condition,null,null,null,false);
	for(GenericValue physicalInventoryAndVariance : physicalInventoryAndVarianceList) {
		BigDecimal quantityOnHandVar = physicalInventoryAndVariance.getBigDecimal("quantityOnHandVar");
		variance = variance.add(quantityOnHandVar == null ? BigDecimal.ZERO : quantityOnHandVar);
	}
	Boolean isExists = false;
	for(Map<String,Object> map : stockVarianceList){
		if(productId.equals(map.get("productId"))){
			isExists = true;
			map.put("variance", variance.add(map.get("variance")));
			if(variance.compareTo(BigDecimal.ZERO) < 0)
				map.put("stockCount", ((BigDecimal)map.get("stockCount")).add(variance.negate()) );
		}
	}
	
	if(!isExists){
		stockVarianceMap.put("variance", variance);
		BigDecimal stckCnt = FacilityReportUtil.getOpeningOrClosingBalance(dispatcher,productId,UtilDateTime.getDayEnd(thruDate),facilityId);
		stockVarianceMap.put("stockCount", stckCnt);
		stockVarianceMap.put("productId", productId);
		stockVarianceMap.put("uomId", inventoryItem.getString("uomId"));
		stockVarianceList.add(stockVarianceMap);
	}
}

context.put("stockVarianceList", stockVarianceList);
context.put("productCategoryId", productCategoryId);
context.put("fromDate", fromDate);
context.put("thruDate", thruDate);

