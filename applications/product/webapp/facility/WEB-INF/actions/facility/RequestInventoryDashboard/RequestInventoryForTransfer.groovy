
import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.party.party.PartyRelationshipHelper;


GenericValue userLogin = session.getAttribute("userLogin");
String partyId = userLogin.getString("partyId");

List<GenericValue>  partyRoleList = delegator.findByAnd("PartyRole",UtilMisc.toMap("partyId",partyId,"roleTypeId","MANAGER"),null,false);
List<String> ownerPartyId =  PartyRelationshipHelper.getDepartmentPartyId(delegator, userLogin);
if(UtilValidate.isEmpty(ownerPartyId))
	return
List<GenericValue> facilityList = delegator.findList("Facility", EntityCondition.makeCondition("ownerPartyId",EntityOperator.IN,ownerPartyId), null, null, null, false);
if(UtilValidate.isEmpty(partyRoleList) || UtilValidate.isEmpty(facilityList))
	return
	
List<EntityCondition> allCondition = [] as ArrayList;
facilityId=parameters.facilityId;
List andExprs = new ArrayList();
List andExprs2 = new ArrayList();

andExprs.add(EntityCondition.makeCondition("createdByPartyId", EntityOperator.NOT_EQUAL,partyId));
andExprs.add(EntityCondition.makeCondition("status", EntityOperator.EQUALS,"GM Approved"));
andExprs2.add(EntityCondition.makeCondition("facilityIdTo", EntityOperator.EQUALS,facilityId));
andExprs2.add(EntityCondition.makeCondition("facilityIdFrom", EntityOperator.EQUALS,facilityId));

allCondition.add(EntityCondition.makeCondition(andExprs, EntityOperator.AND));
allCondition.add(EntityCondition.makeCondition(andExprs2, EntityOperator.OR));

/*andExprs.add(EntityCondition.makeCondition("facilityIdFrom", EntityOperator.EQUALS,facilityId));
andExprs.add(EntityCondition.makeCondition("invReqParentId",EntityOperator.EQUALS,null));*/

EntityCondition whereEntityCondition = EntityCondition.makeCondition(allCondition);

inventoryRequisitionList = delegator.findList("InventoryRequisition", whereEntityCondition, null, null, null, false);

context.inventoryRequisitionList = inventoryRequisitionList;
context.filterDate = filterDate;

