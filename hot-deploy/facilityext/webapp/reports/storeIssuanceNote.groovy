import org.ofbiz.base.util.UtilDateTime

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.product.facility.util.FacilityReportUtil;



String facilityId = parameters.facilityId;
String departmentId = parameters.departmentId;
String productCategoryId = parameters.productCategoryId;
SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
Timestamp fromDate = new Timestamp(dateFormatter.parse(request.getParameter("fromDate")).getTime());
Timestamp thruDate = new Timestamp(dateFormatter.parse(request.getParameter("thruDate")).getTime());

List conditions = [];
conditions.add(EntityCondition.makeCondition("transferDate", EntityOperator.NOT_EQUAL, null));
conditions.add(EntityCondition.makeCondition("transferDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.getDayStart(fromDate)));
conditions.add(EntityCondition.makeCondition("transferDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.getDayEnd(thruDate)));
conditions.add(EntityCondition.makeCondition("facilityIdFrom", EntityOperator.EQUALS, facilityId));

if(UtilValidate.isNotEmpty(departmentId)){
	List facilitiesForCorrespondingDepartment = delegator.findByAnd("Facility",["ownerPartyId" : departmentId],null,false);
	List facilityIdListForCorrespondingDepartment=[];
	for (GenericValue value:facilitiesForCorrespondingDepartment){
		facilityIdListForCorrespondingDepartment.add(value.facilityId)
	}
	conditions.add(EntityCondition.makeCondition("facilityIdTo",EntityOperator.IN,facilityIdListForCorrespondingDepartment));
}

List<GenericValue> inventoryRequisitionList = delegator.findList("InventoryRequisition",EntityCondition.makeCondition(conditions, EntityOperator.AND),null,null,null,false);

Map<String,List<Map<String, Object>>> mainMap = new HashMap<>();
for(GenericValue inventoryRequisition : inventoryRequisitionList){
	String facilityIdTo = inventoryRequisition.getString("facilityIdTo");
	String facilityIdFrom = inventoryRequisition.getString("facilityIdFrom");
	GenericValue facility = delegator.findOne("Facility",UtilMisc.toMap("facilityId",facilityIdTo),true);
	GenericValue partyGroup = delegator.findOne("PartyGroup",UtilMisc.toMap("partyId",facility.getString("ownerPartyId")),true);
	List condition = [];
	condition.add(EntityCondition.makeCondition( "invRequisitionId",EntityOperator.EQUALS, inventoryRequisition.getString("invRequisitionId") ));
	if(UtilValidate.isNotEmpty(parameters.productId))
		condition.add(EntityCondition.makeCondition("productId",EntityOperator.EQUALS,parameters.productId));
	
	if(UtilValidate.isNotEmpty(productCategoryId)){
		List productIdList=[];
		List ProductCategoryMemberList = delegator.findByAnd("ProductCategoryMember",["productCategoryId":productCategoryId],null,false);
		for(GenericValue productCategoryMember:ProductCategoryMemberList){
			productIdList.add(productCategoryMember.productId);
		}
		condition.add(EntityCondition.makeCondition("productId",EntityOperator.IN,productIdList));
	}
		
	
	List<GenericValue> inventoryRequisitionItemList = delegator.findList("InventoryRequisitionItem",EntityCondition.makeCondition(condition,EntityOperator.AND),null,null,null,false);
	BigDecimal issuedQty = BigDecimal.ZERO;
	for(GenericValue inventoryRequisitionItem : inventoryRequisitionItemList){
		Map<String, Object> storeIssuanceNoteMap = createMap(inventoryRequisition, inventoryRequisitionItem, facilityIdTo, facilityIdFrom, issuedQty, fromDate, thruDate, partyGroup);
		mainMap = createMainMap(mainMap,storeIssuanceNoteMap);
	}	
}

def Map<String, Object> createMap(GenericValue inventoryRequisition, GenericValue inventoryRequisitionItem, String facilityIdTo,String facilityIdFrom,BigDecimal issuedQty,Timestamp fromDate,Timestamp thruDate,GenericValue partyGroup){
	Map<String, Object> storeIssuanceNoteMap = new HashMap<String, Object>();
	String productId = inventoryRequisitionItem.getString("productId");
	List<GenericValue> productFacility = delegator.findByAnd("ProductFacility",UtilMisc.toMap("productId",productId,"facilityId",facilityIdFrom),null,false);
	storeIssuanceNoteMap.put("issuedOn", inventoryRequisition.getTimestamp("transferDate"));
	storeIssuanceNoteMap.put("requisitionId", inventoryRequisition.getString("invRequisitionId"));
	storeIssuanceNoteMap.put("productId", productId);
	GenericValue product = delegator.findOne("Product",UtilMisc.toMap("productId",productId),false);
	storeIssuanceNoteMap.put("internalName", product.get("internalName"));
	storeIssuanceNoteMap.put("department", partyGroup.getString("groupName"));
	BigDecimal openingQty = FacilityReportUtil.getOpeningOrClosingBalance(dispatcher, productId,UtilDateTime.getDayEnd(UtilDateTime.addDaysToTimestamp(inventoryRequisition.getTimestamp("transferDate"),-1)),facilityIdFrom);
	storeIssuanceNoteMap.put("openingBalance", openingQty);
	//BigDecimal receivedQty = FacilityReportUtil.getReceivedQty(dispatcher,productId,fromDate,thruDate,facilityIdFrom);
	//storeIssuanceNoteMap.put("received", receivedQty);
	issuedQty = issuedQty.add(inventoryRequisitionItem.getBigDecimal("quantity"));
	storeIssuanceNoteMap.put("issuedQty", issuedQty);
	BigDecimal closingBalance = FacilityReportUtil.getOpeningOrClosingBalance(dispatcher,productId,UtilDateTime.getDayEnd(inventoryRequisition.getTimestamp("transferDate")),facilityIdFrom);
	storeIssuanceNoteMap.put("closingBalance", closingBalance);
	if(UtilValidate.isNotEmpty(productFacility))
		storeIssuanceNoteMap.put("reorder", productFacility.get(0).getBigDecimal("minimumStock"));
	else
		storeIssuanceNoteMap.put("reorder", "");
	storeIssuanceNoteMap.put("receivedBy", inventoryRequisition.getString("issuedTo"));
	storeIssuanceNoteMap.put("issuedBy", inventoryRequisition.getString("issuedBy"));
	GenericValue glAccountCategory = delegator.findOne("GlAccountCategory",UtilMisc.toMap("glAccountCategoryId",inventoryRequisitionItem.getString("glAccountCategoryId")),false);
	storeIssuanceNoteMap.put("costCenter", glAccountCategory.getString("description"));
	return storeIssuanceNoteMap;
}

def Map<String,List<Map<String, Object>>>  createMainMap(Map<String,List<Map<String, Object>>> mainMap,Map<String, Object> storeIssuanceNoteMap){
	List productCatAndMemberCondition = [];
	productCatAndMemberCondition.add(EntityCondition.makeCondition("productId",EntityOperator.EQUALS,storeIssuanceNoteMap.productId));
	productCatAndMemberCondition.add(EntityCondition.makeCondition("productCategoryTypeId",EntityOperator.NOT_EQUAL,"TAX_CATEGORY"));
	List productCategoryList = delegator.findList("ProductCategoryAndMember",EntityCondition.makeCondition(productCatAndMemberCondition,EntityOperator.AND),null,null,null,false);
	if( UtilValidate.isNotEmpty(productCategoryList) )
		mainMap = getPuttedMainMap(mainMap, storeIssuanceNoteMap, productCategoryList.get(0).description);
	else
		mainMap = getPuttedMainMap(mainMap, storeIssuanceNoteMap, "Default");
	return mainMap;
}

def  Map<String,List<Map<String, Object>>> getPuttedMainMap(Map<String,List<Map<String, Object>>> mainMap, Map<String, Object> storeIssuanceNoteMap, String productCategoryDesc){
	List<Map<String, Object>> storeIssuanceNoteList = new ArrayList<>();
	if(mainMap.containsKey(productCategoryDesc)){
		mainMap.get(productCategoryDesc).add(storeIssuanceNoteMap);
	}else{
		storeIssuanceNoteList.add(storeIssuanceNoteMap);
		mainMap.put(productCategoryDesc, storeIssuanceNoteList);
	}
	return mainMap; 
} 

context.put("mainMap", mainMap);
context.put("fromDate", fromDate);
context.put("thruDate", thruDate);




