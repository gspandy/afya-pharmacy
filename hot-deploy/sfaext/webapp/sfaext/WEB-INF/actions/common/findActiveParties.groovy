/*
 * Copyright (c) 2006 - 2007 Open Source Strategies, Inc.
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the Honest Public License.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Honest Public License for more details.
 * 
 * You should have received a copy of the Honest Public License
 * along with this program; if not, write to Funambol,
 * 643 Bair Island Road, Suite 305 - Redwood City, CA 94063, USA
 */
/* Copyright (c) 2005-2006 Open Source Strategies, Inc. */
 
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.condition.*;
import com.smebiz.sfa.party.SfaPartyHelper;
import java.util.ArrayList;
import org.ofbiz.entity.util.EntityUtil;

/* returns a list iterator of active parties based on the PartyFromSummaryByRelationship entity and 
whatever parameters are supplied.  puts the list iterator into the context for form widgets to use.  
can be reused to find Accounts, Contacts, Leads, Team members, etc. etc. */

/* note that performFind can be included in a form-widget, but this shareable BSH is better for reuse and
respects the separation of view and data preparation pattern.
   we later moved from using performFind service to prepareFind to get our conditions and then did a find
because we had to use EntityFindOptions to specify "distinct" */

// determine the user's prefered find using findActivePartiesViewPrefTypeId (optional feature)
viewPrefConditions = null;
userLogin = request.getAttribute("userLogin");
findActivePartiesViewPrefTypeId = parameters.get("viewPrefTypeId");
 

listIt=new ArrayList();
if ("my".equals(findActivePartiesViewPrefTypeId)) {
	context.put("VIEW_PREF","CrmMy");
	selfConditions = new ArrayList();
	selfConditions.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, userLogin.get("partyId")));
	selfConditions.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS,parameters.get("roleTypeIdFrom")));
	selfConditions.add(EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS,parameters.get("partyRelationshipTypeId")));
	listIt = delegator.findList("PartyFromSummaryByRelationship",EntityCondition.makeCondition(selfConditions, EntityOperator.AND),null,null,null,false);
}else{
	    context.put("VIEW_PREF","CrmMyTeam");
	    roleConditions = new ArrayList();
	    roleConditions.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "ACCOUNT_TEAM"));
	    roleConditions.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "ACCOUNT_MANAGER"));
	    roleConditions.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, userLogin.get("partyId")));
	    List teams = delegator.findList("PartyRelationship",EntityCondition.makeCondition(roleConditions, EntityOperator.AND),null,null,null,false);
	    teamIds = new ArrayList();
	    for(GenericValue team:teams){
	    	teamIds.add(team.get("partyIdFrom"));
	    }
	    conditions = new ArrayList();
		conditions.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.IN, teamIds));
		conditions.add(EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "ASSIGNED_TO"));
		conditions.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.NOT_EQUAL,userLogin.get("partyId")));
		conditions.add(EntityExpr.makeConditionDate("fromDate","thruDate"));
		
		List teamMembers = delegator.findList("PartyRelationship",EntityCondition.makeCondition(conditions, EntityOperator.AND),null,null,null,false);
		teamConditions = new ArrayList();
		teamMemberIds = new ArrayList();
		for(GenericValue member:teamMembers){
		 teamMemberIds.add(member.get("partyIdTo"));
		}
		
		teamConditions.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.IN,teamMemberIds));
		teamConditions.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS,parameters.get("roleTypeIdFrom")));
		teamConditions.add(EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS,parameters.get("partyRelationshipTypeId")));
		listIt = delegator.findList("PartyFromSummaryByRelationship",EntityCondition.makeCondition(teamConditions, EntityOperator.AND),null,null,null,false);
		
	    
	}




if (parameters.get("listIteratorNameToUse") == null) {
    context.put("listIt", listIt); 
} else {
    context.put(parameters.get("listIteratorNameToUse"), listIt);
}
