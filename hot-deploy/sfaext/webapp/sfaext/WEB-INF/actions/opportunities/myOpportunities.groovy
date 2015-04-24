// finds all opportunities for all accounts that the userLogin is a member of

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.condition.EntityOperator;
import com.smebiz.sfa.opportunities.UtilOpportunity;
import org.ofbiz.entity.util.EntityFindOptions;

// get orderBy from the parameters
opportunitiesOrderBy = parameters.get("opportunitiesOrderBy");
if (opportunitiesOrderBy==null) opportunitiesOrderBy = "salesOpportunityId DESC";

// get the user preferences for this section
userLogin = request.getAttribute("userLogin");

// no closed opportunities for this page (watch out for null values, which are allowed and not testable by a simple != closed)
notClosedCondition = EntityCondition.makeCondition( UtilMisc.toList(
            EntityCondition.makeCondition("opportunityStageId", EntityOperator.EQUALS, null),
            EntityCondition.makeCondition( UtilMisc.toList(
                    EntityCondition.makeCondition("opportunityStageId", EntityOperator.NOT_EQUAL, null),
                    EntityCondition.makeCondition("opportunityStageId", EntityOperator.NOT_EQUAL, "SOSTG_CLOSED")
                    ), EntityOperator.AND)
            ), EntityOperator.OR);

// get the configured organizationPartyId
organizationPartyId = null;

viewPrefTypeId = parameters.get("viewPrefTypeId");

// decide which find to use based on preferences (default is team)
myOpportunities = null;
if ("my".equals(viewPrefTypeId)) {
    context.put("VIEW_PREF","CrmMy");
    myOpportunities = UtilOpportunity.getOpportunitiesForMyAccounts(organizationPartyId, userLogin.getString("partyId"), null,
            notClosedCondition, UtilMisc.toList(opportunitiesOrderBy), delegator);
} else {
    context.put("VIEW_PREF","CrmMyTeam");
    roleConditions = new ArrayList();
    roleConditions.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "ACCOUNT_TEAM"));
    roleConditions.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "ACCOUNT_MANAGER"));
    roleConditions.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, userLogin.get("partyId")));
    List teams = delegator.findList("PartyRelationship",EntityCondition.makeCondition(roleConditions,EntityOperator.AND),null,null,null,false);
    teamIds = new ArrayList();
    for(GenericValue team in teams){
    	teamIds.add(team.get("partyIdFrom"));
    }
    conditions = new ArrayList();
	conditions.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.IN, teamIds));
	conditions.add(EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "ASSIGNED_TO"));
	conditions.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.NOT_EQUAL,userLogin.get("partyId")));
	//conditions.add(EntityCondition.makeCondition("fromDate","thruDate"));
	List teamMembers = delegator.findList("PartyRelationship",EntityCondition.makeCondition(conditions,EntityOperator.AND),null,null,null,false);
	teamConditions = new ArrayList();
	teamMemberIds = new ArrayList();
	for(GenericValue member in teamMembers){
	 teamMemberIds.add(member.get("partyIdTo"));			
	}
	
	System.out.println(" Team Member Condition *************** "+conditions);
	System.out.println(" Team Member Condition *************** "+teamMemberIds);
	
	
	CRMTYPES = new ArrayList();
	CRMTYPES.add("ACCOUNT");
	CRMTYPES.add("PROSPECT");
	
	teamConditions.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.IN,teamMemberIds));
	teamConditions.add(EntityCondition.makeCondition("roleTypeIdFrom",EntityOperator.IN,CRMTYPES));
	teamConditions.add(EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS,"RESPONSIBLE_FOR"));
	listIt = delegator.findList("PartyFromSummaryByRelationship",EntityCondition.makeCondition(teamConditions,EntityOperator.AND),null,null,null,false);

	System.out.println(" CRM Party Condition *************** "+teamConditions);
	
	crmParties = new ArrayList();
	for(GenericValue entity in listIt){
		crmParties.add(entity.get("partyIdFrom"));
	}
	System.out.println(" Crm Party Condition *************** "+crmParties);
	
	teamConditions.clear();
	teamConditions.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.IN,crmParties));
	teamConditions.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.IN,CRMTYPES));
	teamConditions.add(EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS,"RESPONSIBLE_FOR"));
	teamConditions.add(notClosedCondition);
	
	fields = new java.util.HashSet();
	fields.add("salesOpportunityId");
	fields.add("partyIdFrom");
	fields.add("opportunityName");
	fields.add("opportunityStageId");
	fields.add("estimatedAmount");
	fields.add("estimatedCloseDate");
	fields.add("estimatedProbability");
	fields.add("currencyUomId");
	
	myOpportunities = delegator
	.find("PartyRelationshipAndSalesOpportunity",EntityCondition.makeCondition(teamConditions),null,fields,null,
			new EntityFindOptions(true,
			EntityFindOptions.TYPE_SCROLL_INSENSITIVE,
			EntityFindOptions.CONCUR_READ_ONLY, true));


}	

context.put("opportunitiesListIt", myOpportunities);
