import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityListIterator;

String facilityId = parameters.facilityId;
String productCategoryId = parameters.productCategoryId;
String departmentId = parameters.departmentId;
SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
Timestamp fromDate = new Timestamp(dateFormatter.parse(request.getParameter("fromDate")).getTime());
Timestamp thruDate = new Timestamp(dateFormatter.parse(request.getParameter("thruDate")).getTime());

conditions = [];
conditions.add(EntityCondition.makeCondition("status", EntityOperator.EQUALS, "Complete"));
conditions.add(EntityCondition.makeCondition("requestDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate));
conditions.add(EntityCondition.makeCondition("requestDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDate));
if(UtilValidate.isNotEmpty(departmentId)){
	facilitiesForCorrespondingDepartment = delegator.findByAnd("Facility",["ownerPartyId" : departmentId],null,false);
    facilityIdListForCorrespondingDepartment=[];
    for (GenericValue value:facilitiesForCorrespondingDepartment){
        facilityIdListForCorrespondingDepartment.add(value.facilityId)
    }
    conditions.add(EntityCondition.makeCondition("facilityIdTo",EntityOperator.IN,facilityIdListForCorrespondingDepartment));
}
List<GenericValue> inventoryRequisitionList = delegator.findList("InventoryRequisition",EntityCondition.makeCondition(conditions, EntityOperator.AND),null,null,null,false);
List<Map<String, Object>> returnableList = new ArrayList<>();

for(GenericValue inventoryRequisition : inventoryRequisitionList){
	String facilityIdTo = inventoryRequisition.getString("facilityIdTo");
	String facilityIdFrom = inventoryRequisition.getString("facilityIdFrom");
	GenericValue facilityTo = delegator.findOne("Facility",UtilMisc.toMap("facilityId",facilityIdTo),true);
	GenericValue toPartyGroup = delegator.findOne("PartyGroup",UtilMisc.toMap("partyId",facilityTo.getString("ownerPartyId")),true);
	GenericValue facilityFrom = delegator.findOne("Facility",UtilMisc.toMap("facilityId",facilityIdFrom),true);
	GenericValue fromPartyGroup = delegator.findOne("PartyGroup",UtilMisc.toMap("partyId",facilityFrom.getString("ownerPartyId")),true);
	
	inventoryRequisitionItemConditions = [];
	inventoryRequisitionItemConditions.add(EntityCondition.makeCondition("invRequisitionId", EntityOperator.EQUALS, inventoryRequisition.getString("invRequisitionId")));
	inventoryRequisitionItemConditions.add(EntityCondition.makeCondition("returnable", EntityOperator.EQUALS, "Yes"));
	inventoryRequisitionItemConditions.add(EntityCondition.makeCondition("outTime", EntityOperator.NOT_EQUAL, null));
	inventoryRequisitionItemConditions.add(EntityCondition.makeCondition("inTime", EntityOperator.NOT_EQUAL, null));
	
	if(UtilValidate.isNotEmpty(productCategoryId)){
		productIdList=[];
		ProductCategoryMemberList = delegator.findByAnd("ProductCategoryMember",["productCategoryId":productCategoryId],null,false);
	    for(GenericValue productCategoryMember:ProductCategoryMemberList){
	        productIdList.add(productCategoryMember.productId);
	    }
	    inventoryRequisitionItemConditions.add(EntityCondition.makeCondition("productId",EntityOperator.IN,productIdList));
	}
	
	List<GenericValue> inventoryRequisitionItemList = delegator.findList("InventoryRequisitionItem",EntityCondition.makeCondition(inventoryRequisitionItemConditions, EntityOperator.AND),null,null,null,false);
	for(GenericValue inventoryRequisitionItem : inventoryRequisitionItemList){
		Map<String, Object> returnableMap = new HashMap<String, Object>();
		returnableMap.put("date", inventoryRequisition.getTimestamp("requestDate"));
		returnableMap.put("itemDescription", inventoryRequisitionItem.getString("note"));
		returnableMap.put("productId", inventoryRequisitionItem.getString("productId"));
		returnableMap.put("quantity", inventoryRequisitionItem.getBigDecimal("quantity"));
		returnableMap.put("department", toPartyGroup.getString("groupName"));
		returnableMap.put("outTime", inventoryRequisitionItem.getTimestamp("outTime"));
		returnableMap.put("inTime", inventoryRequisitionItem.getTimestamp("inTime"));
		returnableList.add(returnableMap);
	}
	
}

context.put("returnableList", returnableList);
context.put("productCategoryId", productCategoryId);
context.put("departmentId", departmentId);
context.put("fromDate", fromDate);
context.put("thruDate", thruDate);