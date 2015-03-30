import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;



List facilityIdList = [];
List conditions = [];
GenericDelegator delegator = delegator;
String facilityId = parameters.facilityId;
String departmentId = UtilValidate.isNotEmpty(parameters.departmentId)? parameters.departmentId : parameters.parm0;
String productId = parameters.productId;
List facilitys = [];

if(UtilValidate.isNotEmpty(departmentId))
	facilitys = delegator.findByAnd("Facility",UtilMisc.toMap("ownerPartyId",departmentId),null,false);
for(GenericValue facility : facilitys){
	facilityIdList.add(facility.getString("facilityId"));
}
if(UtilValidate.isNotEmpty(facilityIdList)){
	conditions.add(EntityCondition.makeCondition("facilityId",EntityOperator.IN,facilityIdList));
}else{
	conditions.add(EntityCondition.makeCondition("facilityId",EntityOperator.EQUALS,facilityId));
}
if(UtilValidate.isNotEmpty(productId))
	conditions.add(EntityCondition.makeCondition("productId",productId));
List invItemList = delegator.findList("InventoryItem", EntityCondition.makeCondition(conditions), null, null, null, false);
Set<GenericValue> listIt = new HashSet<GenericValue>();
for(GenericValue invItem : invItemList){
	listIt.add(delegator.findOne("Product", false, UtilMisc.toMap("productId",invItem.getString("productId"))));
}

context.listIt = listIt.asList();