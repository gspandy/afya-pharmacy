import java.util.List;

import javolution.util.FastList;

import org.ofbiz.base.util.*;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityListIterator;


String facilityId = parameters.facilityId;
String departmentId = parameters.departmentId;

String partyRoleParam = parameters.partyRoleParam;
String facilityIdParam = parameters.facilityIdParam;
if(UtilValidate.isNotEmpty(facilityIdParam)){
	facilityId = facilityIdParam;
}
GenericValue facility = delegator.findOne("Facility",UtilMisc.toMap("facilityId",facilityId),false);
String facilityOwnerId = facility.getString("ownerPartyId");
if(UtilValidate.isNotEmpty(departmentId)){
	facilityOwnerId = departmentId;
}
if (context.noConditionFind == null) {
   context.noConditionFind = parameters.noConditionFind;
}
if (context.noConditionFind == null) {
   context.noConditionFind = UtilProperties.getPropertyValue("widget", "widget.defaultNoConditionFind");
}
if (context.filterByDate == null) {
   context.filterByDate = parameters.filterByDate;
}
prepareResult = dispatcher.runSync("prepareFind", [entityName : context.entityName,
												  orderBy : context.orderBy,
												  inputFields : parameters,
												  filterByDate : context.filterByDate,
												  filterByDateValue : context.filterByDateValue,
												  userLogin : context.userLogin] );

exprList = [EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "PARTY_DISABLED")
		   , EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, null)];
CondList = EntityCondition.makeCondition(exprList, EntityOperator.AND);
CondList1 = EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, null);
statusPartyDisable = EntityCondition.makeCondition([CondList1, CondList], EntityOperator.OR);
entityConditionList = null;
if (prepareResult.entityConditionList != null) {
   ConditionList = [ prepareResult.entityConditionList, statusPartyDisable ];
   entityConditionList = EntityCondition.makeCondition(ConditionList);
} else if (context.noConditionFind == "Y") {
   entityConditionList = statusPartyDisable;
}

executeResult = dispatcher.runSync("executeFind", [entityName : context.entityName,
												  orderByList : prepareResult.orderByList,
												  entityConditionList : entityConditionList,
												  noConditionFind :context.noConditionFind
												  ] );
if (executeResult.listIt == null) {
   Debug.log("No list found for query string + [" + prepareResult.queryString + "]");
}

List<EntityCondition> partyConditions = FastList.newInstance();
partyConditions.add(EntityCondition.makeCondition("partyIdFrom", facilityOwnerId));
EntityCondition thruCond = EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition("thruDate", null),
           EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN, UtilDateTime.nowTimestamp())),EntityOperator.OR);
partyConditions.add(thruCond);
EntityCondition partyCondition = EntityCondition.makeCondition(partyConditions);

List<GenericValue> pratyRelationshipList = delegator.findList("PartyRelationship",partyCondition,null,null,null,false);

EntityListIterator iter = executeResult.listIt;
List<GenericValue> finalList = new ArrayList<GenericValue>();
if(iter){
	List<GenericValue> personlist = iter.getCompleteList();
	for(GenericValue pratyRelationship : pratyRelationshipList){
		for(GenericValue person : personlist){
			if(pratyRelationship.getString("partyIdTo").equals(person.getString("partyId")))
			   if(UtilValidate.isNotEmpty(partyRoleParam)){
				  List<GenericValue> partyRoleList = delegator.findByAnd("PartyRole",UtilMisc.toMap("partyId",person.getString("partyId"),"roleTypeId",partyRoleParam),null,false);
				  if(UtilValidate.isNotEmpty(partyRoleList)){
					  finalList.add(person);
				  } 
			   }else{
				finalList.add(person);
			   }
		}
	}
}

context.listIt = finalList;
context.queryString = prepareResult.queryString;
context.queryStringMap = prepareResult.queryStringMap;
