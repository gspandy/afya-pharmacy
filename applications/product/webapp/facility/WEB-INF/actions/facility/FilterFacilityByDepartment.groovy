import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.party.party.PartyRelationshipHelper;



List<String> ownerPartyId =  PartyRelationshipHelper.getDepartmentPartyId(delegator, userLogin);

List<GenericValue> partyRoleList = delegator.findByAnd("PartyRole",UtilMisc.toMap("partyId",userLogin.get("partyId"),"roleTypeId","GENERAL_MANAGER"),null,false);

EntityCondition condition = null;
List<GenericValue> facilityList = new ArrayList<GenericValue>();
if(UtilValidate.isNotEmpty(partyRoleList) || userLogin.get("partyId") == "admin"){
	condition = EntityCondition.makeCondition("ownerPartyId","Company");
	facilityList = delegator.findList("Facility", condition, null, null, null, false);
}

if(ownerPartyId){
	List conList = new ArrayList();
	conList.add(EntityCondition.makeCondition("ownerPartyId",EntityOperator.IN,ownerPartyId));
	if(UtilValidate.isNotEmpty(facilityList))
	 conList.add(EntityCondition.makeCondition("ownerPartyId",EntityOperator.NOT_EQUAL,"Company"));
	condition = EntityCondition.makeCondition(conList,EntityOperator.AND);
	facilityList.addAll(delegator.findList("Facility", condition, null, null, null, false));
}

context.facilityList = facilityList;

String requirementFacilityId = "";
if(UtilValidate.isNotEmpty(facilityList))
	requirementFacilityId = facilityList.get(facilityList.size() - 1).getString("facilityId");

context.requirementFacilityId = requirementFacilityId;