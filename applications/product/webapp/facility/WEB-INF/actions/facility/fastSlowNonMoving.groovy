import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.product.facility.util.FacilityReportUtil;


GenericDelegator delegator = delegator;
String productCategoryId = parameters.productCategoryId;
String productId = parameters.productId;
String facilityId = parameters.facilityId;

List invReqCondition = [];
invReqCondition.add(EntityCondition.makeCondition("requestType",EntityOperator.EQUALS,"Request"));
invReqCondition.add(EntityCondition.makeCondition("status",EntityOperator.EQUALS,"Complete"));
invReqCondition.add(EntityCondition.makeCondition("facilityIdFrom",EntityOperator.EQUALS,facilityId));
if(UtilValidate.isNotEmpty(fromDate)){
	invReqCondition.add(EntityCondition.makeCondition("transferDate",EntityOperator.GREATER_THAN_EQUAL_TO,UtilDateTime.getDayStart(fromDate)));
}

if(UtilValidate.isNotEmpty(thruDate)){
	invReqCondition.add(EntityCondition.makeCondition("transferDate",EntityOperator.LESS_THAN_EQUAL_TO,UtilDateTime.getDayEnd(thruDate)));
}
Map<String,List<Map<String,Object>>> mainMap = new HashMap<>();
List<GenericValue> inventoryRequisitionList = delegator.findList("InventoryRequisition", EntityCondition.makeCondition(invReqCondition), null, ["-transferDate"], null, false);
for(GenericValue inventoryRequisition : inventoryRequisitionList){
	List invReqItemCondition = [];
	invReqItemCondition.add(EntityCondition.makeCondition("invRequisitionId",EntityOperator.EQUALS,inventoryRequisition.getString("invRequisitionId")));
	if(UtilValidate.isNotEmpty(productId))
		invReqItemCondition.add(EntityCondition.makeCondition("productId",EntityOperator.EQUALS,productId));
	if(UtilValidate.isNotEmpty(productCategoryId)){
		List productIdList=[];
		List ProductCategoryMemberList = delegator.findByAnd("ProductCategoryMember",["productCategoryId":productCategoryId],null,false);
		for(GenericValue productCategoryMember:ProductCategoryMemberList){
			productIdList.add(productCategoryMember.productId);
		}
		invReqItemCondition.add(EntityCondition.makeCondition("productId",EntityOperator.IN,productIdList));
	}
	
	List<GenericValue> inventoryRequisitionItemList = delegator.findList("InventoryRequisitionItem",EntityCondition.makeCondition(invReqItemCondition,EntityOperator.AND),null,null,null,false);
	for(GenericValue inventoryRequisitionItem : inventoryRequisitionItemList){
		Map<String, Object> storeIssuanceNoteMap = createMap(inventoryRequisition, inventoryRequisitionItem, fromDate, thruDate);
		mainMap = createMainMap(mainMap,storeIssuanceNoteMap);
	}
	
}

def Map<String, Object> createMap(GenericValue inventoryRequisition, GenericValue inventoryRequisitionItem,Timestamp fromDate,Timestamp thruDate){
	Map<String, Object> storeIssuanceNoteMap = new HashMap<String, Object>();
	String productId = inventoryRequisitionItem.getString("productId");
	GenericValue product = delegator.findOne("Product",UtilMisc.toMap("productId",productId),false);
	storeIssuanceNoteMap.put("productId", productId);
	storeIssuanceNoteMap.put("productDescription", product.getString("internalName"));
	GenericValue uom = delegator.findOne("Uom",UtilMisc.toMap("uomId",product.getString("quantityUomId")),false);
	storeIssuanceNoteMap.put("unit", uom.getString("description"));
	BigDecimal a = BigDecimal.ZERO;
	BigDecimal b = BigDecimal.ZERO;
	BigDecimal c = BigDecimal.ZERO;
	
	BigDecimal qohOnStartDate = FacilityReportUtil.getOpeningOrClosingBalance(dispatcher,productId,UtilDateTime.getDayEnd( UtilDateTime.addDaysToTimestamp(fromDate,-1) ),inventoryRequisition.facilityIdFrom);
	BigDecimal allQtyReceivedInPeriod = FacilityReportUtil.getReceivedQty(dispatcher,productId,fromDate,thruDate,inventoryRequisition.facilityIdFrom);
	a = qohOnStartDate.add(allQtyReceivedInPeriod);
	b = FacilityReportUtil.getRequisitionIssuedQtyDuringPeriod(delegator, productId, inventoryRequisition.facilityIdFrom,  fromDate,  thruDate);
	BigDecimal usage = BigDecimal.ZERO;
	if(b.compareTo(BigDecimal.ZERO) == 0)
		b = BigDecimal.ONE;
	try{
	  usage = (b/a).multiply(new BigDecimal(100));
	}catch(Exception e){
	}
	storeIssuanceNoteMap.put("usage", usage.setScale(2, RoundingMode.HALF_DOWN));
	GenericValue productFacility = delegator.findOne("ProductFacility",UtilMisc.toMap("productId",productId,"facilityId",inventoryRequisition.facilityIdFrom),false);
	if(productFacility != null)
		storeIssuanceNoteMap.put("reorderQuantity", productFacility.getBigDecimal("minimumStock") != null ? productFacility.getBigDecimal("minimumStock").setScale(2, RoundingMode.HALF_EVEN) : 0);
	else
		storeIssuanceNoteMap.put("reorderQuantity", "");
	return storeIssuanceNoteMap;
}

def Map<String,List<Map<String, Object>>>  createMainMap(Map<String,List<Map<String, Object>>> mainMap,Map<String, Object> storeIssuanceNoteMap){
	List productCatAndMemberCondition = [];
	productCatAndMemberCondition.add(EntityCondition.makeCondition("productId",EntityOperator.EQUALS,storeIssuanceNoteMap.productId));
	productCatAndMemberCondition.add(EntityCondition.makeCondition("productCategoryTypeId",EntityOperator.NOT_EQUAL,"TAX_CATEGORY"));
	EntityFindOptions efo = new EntityFindOptions();
	efo.setFetchSize(1);
	List productCategoryList = delegator.findList("ProductCategoryAndMember",EntityCondition.makeCondition(productCatAndMemberCondition,EntityOperator.AND),null,null,efo,false);
	if( UtilValidate.isNotEmpty(productCategoryList) )
		mainMap = getPuttedMainMap(mainMap, storeIssuanceNoteMap, productCategoryList.get(0).description);
	else
		mainMap = getPuttedMainMap(mainMap, storeIssuanceNoteMap, "Default");
	return mainMap;
}

def  Map<String,List<Map<String, Object>>> getPuttedMainMap(Map<String,List<Map<String, Object>>> mainMap, Map<String, Object> storeIssuanceNoteMap, String productCategoryDesc){
	List<Map<String, Object>> storeIssuanceNoteList = new ArrayList<>();
	if(mainMap.containsKey(productCategoryDesc)){
		List list = mainMap.get(productCategoryDesc);
		String productId = storeIssuanceNoteMap.get("productId");
		boolean notExist = true;
		for(Map map : list){
			if(productId.equals(map.get("productId")))
				notExist = false;
		}
		if(notExist){
			mainMap.get(productCategoryDesc).add(storeIssuanceNoteMap);
				Collections.sort(mainMap.get(productCategoryDesc),
					new Comparator<Map<String,Object>>() {
						public int compare(Map<String,Object> e1, Map<String,Object> e2) {
							return ((BigDecimal)e2.get("usage")).compareTo((BigDecimal)e1.get("usage"));
						}
					}
				);
		}
	}else{
		storeIssuanceNoteList.add(storeIssuanceNoteMap);
		mainMap.put(productCategoryDesc, storeIssuanceNoteList);
	}
	return mainMap;
}

context.put("mainMap", mainMap);
context.put("fromDate", fromDate);
context.put("thruDate", thruDate);
