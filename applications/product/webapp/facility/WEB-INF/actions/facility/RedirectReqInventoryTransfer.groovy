import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.party.party.PartyRelationshipHelper;

String invRequisitionId = request.getParameter("invRequisitionId");
String partyId = userLogin.getString("partyId");
String facilityId = session.getAttribute("facilityId");

GenericValue inventoryRequisitionGv = delegator.findOne("InventoryRequisition", UtilMisc.toMap("invRequisitionId",invRequisitionId), false);
String status = inventoryRequisitionGv.getString("status");

GenericValue partyRoleGv = delegator.findOne("PartyRole", UtilMisc.toMap("partyId",partyId,"roleTypeId","MANAGER"), false);
GenericValue facilityGv = delegator.findOne("Facility", UtilMisc.toMap("facilityId",inventoryRequisitionGv.getString("facilityIdFrom")), false);
List conditions = [];
conditions.add( EntityCondition.makeCondition("partyIdFrom",EntityOperator.EQUALS, facilityGv.getString("ownerPartyId")) );
conditions.add( EntityCondition.makeCondition("partyIdTo",EntityOperator.EQUALS, partyId) );
conditions.add( EntityCondition.makeCondition("roleTypeIdTo",EntityOperator.EQUALS, "MANAGER") );
EntityCondition cn1 = EntityCondition.makeCondition("roleTypeIdFrom",EntityOperator.EQUALS,"INTERNAL_ORGANIZATIO");
EntityCondition cn2 = EntityCondition.makeCondition("roleTypeIdFrom",EntityOperator.EQUALS,"ORGANIZATION_ROLE");
conditions.add(EntityCondition.makeCondition(cn1,EntityOperator.OR,cn2) );
List<GenericValue> partyRelationships = delegator.findList("PartyRelationship",EntityCondition.makeCondition(conditions),null,null,null,false);

GenericValue partyRoleReqIssuerGv = delegator.findOne("PartyRole", UtilMisc.toMap("partyId",partyId,"roleTypeId","REQ_ISSUER"), false);
if( "GM Canceled".equals(status) ){
	return "view";
}

if(partyRoleReqIssuerGv != null && "GM Approved".equals(status) && UtilValidate.isEmpty(partyRelationships) ){
	if( partyId.equals(inventoryRequisitionGv.getString("createdByPartyId")) )
		return "edit";
	else
		return "view";
}

if("Approved".equals(status) && ( partyId.equals(inventoryRequisitionGv.getString("createdByPartyId")) ) ) {
	return "accept";
}

if(partyRoleReqIssuerGv != null && ( "Complete".equals(status) || "Approved".equals(status) ) ){
	return "issue";
}


if(partyRoleGv != null && partyId.equals(inventoryRequisitionGv.getString("createdByPartyId")) && 
	"GM Approved".equals(status) && UtilValidate.isNotEmpty(partyRelationships) ){
	return "approval";
}
	

if(( "GM Approved".equals(status) || "Requested".equals(status)||"Saved".equals(status)) && partyId.equals(inventoryRequisitionGv.getString("createdByPartyId")) ){
	if(inventoryRequisitionGv.getString("invReqParentId"))
		return "view";
	else
		return "edit";
}
if( ("Complete".equals(status)) && partyId.equals(inventoryRequisitionGv.getString("createdByPartyId")) ){
	return "view";
}


if( "GM Approved".equals(status) && !(partyId.equals(inventoryRequisitionGv.getString("createdByPartyId"))) && facilityId.equals(inventoryRequisitionGv.getString("facilityIdFrom")) ){
	if("Return".equals(inventoryRequisitionGv.getString("requestType")))
		return "return";
	else if(inventoryRequisitionGv.getString("invReqParentId"))
		return "rejected";
	else
		return "approval";
}

if( !(partyId.equals(inventoryRequisitionGv.getString("createdByPartyId"))) ){
	return "view";
}


return "view";
