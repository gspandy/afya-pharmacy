package com.smebiz.sfa.teams;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javolution.util.FastSet;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;

public class TeamHelper {

    public static final String module = TeamHelper.class.getName();

    /** the possible security groups for a team member (basically the contents of SalesTeamRoleSecurity) */
    public static final List TEAM_SECURITY_GROUPS = UtilMisc.toList("SALES_MANAGER", "SALES_REP", "SALES_REP_LIMITED", "CSR");

    /** Find all active PartyRelationships that relates a partyId to a team or account. */
    public static List findActiveAccountOrTeamRelationships(String accountTeamPartyId, String roleTypeIdFrom, String teamMemberPartyId, GenericDelegator delegator) throws GenericEntityException {
            EntityCondition conditions = EntityCondition.makeCondition(UtilMisc.toList(
            		EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, roleTypeIdFrom),
            		EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, accountTeamPartyId),
            		EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, teamMemberPartyId),
            		EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "ASSIGNED_TO"),
                    EntityUtil.getFilterByDateExpr()
                    ), EntityOperator.AND);
            return delegator.findList("PartyRelationship",  conditions, null, null,null,false);
    }

    /**
     * Get all active team members of a given Collection of team partyIds.  Returns a list of PartyToSummaryByRelationship.
     */
    public static List<GenericValue> getActiveTeamMembers(Collection<String> teamPartyIds, GenericDelegator delegator) throws GenericEntityException {
        // this might happen if there are no teams set up yet
        if (UtilValidate.isEmpty(teamPartyIds)) {
            Debug.logWarning("No team partyIds set, so getActiveTeamMembers returns null", module);
            return null;
        }

        EntityCondition orConditions =  EntityCondition.makeCondition( UtilMisc.toList(
        		EntityCondition.makeCondition("securityGroupId", EntityOperator.EQUALS, "SALES_MANAGER"),
        		EntityCondition.makeCondition("securityGroupId", EntityOperator.EQUALS, "SALES_REP"),
        		EntityCondition.makeCondition("securityGroupId", EntityOperator.EQUALS, "SALES_REP_LIMITED"),
        		EntityCondition.makeCondition("securityGroupId", EntityOperator.EQUALS, "CSR")
                    ), EntityOperator.OR);
        EntityCondition conditions = EntityCondition.makeCondition( UtilMisc.toList(
        		EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "ACCOUNT_TEAM"),
        		EntityCondition.makeCondition("partyIdFrom", EntityOperator.IN, teamPartyIds),
        		EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "ASSIGNED_TO"),
                    orConditions,
                    // new EntityExpr("securityGroupId", EntityOperator.IN, TEAM_SECURITY_GROUPS),  XXX TODO: found bug in mysql: this is not equivalent to using the or condition!
                    EntityUtil.getFilterByDateExpr()
                    ), EntityOperator.AND);
        Set<String> set = new HashSet<String>();
        set.add("partyId");
        set.add("firstName");
        set.add("lastName");
        EntityListIterator teamMembersIterator = delegator.find(
                "PartyToSummaryByRelationship", 
                conditions, 
                null, 
                set, 
                Arrays.asList("firstName", "lastName"), 
                new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, true)
                );
        List<GenericValue> resultList = teamMembersIterator.getCompleteList();
        teamMembersIterator.close();
        return resultList;
    }

    /** As above, but for one team. */
    public static List<GenericValue> getActiveTeamMembers(String teamPartyId, GenericDelegator delegator) throws GenericEntityException {
        return getActiveTeamMembers(Arrays.asList(teamPartyId), delegator);
    }

    /** Get all active team members of all active teams.  Returns a list of PartyToSummaryByRelationship. */
    public static List<GenericValue> getActiveTeamMembers(GenericDelegator delegator) throws GenericEntityException {
        List<EntityExpr> conditions = new ArrayList<EntityExpr>();
        conditions.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "ACCOUNT_TEAM"));
        conditions.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "PARTY_DISABLED"));
        List<GenericValue> teams = delegator.findList("PartyRoleAndPartyDetail", EntityCondition.makeCondition(conditions, EntityOperator.AND),(Set<String>) Arrays.asList("partyId"), null,null,false);
        List<String> teamPartyIds = null;
        if (UtilValidate.isNotEmpty(teams)) 
            teamPartyIds = EntityUtil.getFieldListFromEntityList(teams, "partyId", true);
        
        List<GenericValue> teamMembers = getActiveTeamMembers(teamPartyIds, delegator);
        return teamMembers;
    }
    
    /**
     * Get the team members (as a list of partyId Strings) that the partyId currently shares a team with.  This is accomplished by finding all
     * active team members of teams that the partyId belongs to.  No security is checked here, that is the responsibility of upstream code.
     */
    public static Collection getTeamMembersForPartyId(String partyId, GenericDelegator delegator) throws GenericEntityException {
        Collection teamPartyIds = getTeamsForPartyId(partyId, delegator);
        if (teamPartyIds.size() == 0) return teamPartyIds;

        List relationships = getActiveTeamMembers(teamPartyIds, delegator);
        Set partyIds = new HashSet();
        for (Iterator iter = relationships.iterator(); iter.hasNext(); ) {
            GenericValue relationship = (GenericValue) iter.next();
            partyIds.add(relationship.get("partyId"));
        }
        return partyIds;
    }

    /**
     *  Get the teams (as a list of PartyRelationship.partyIdFrom) that the partyId currently belongs to.  A team relationship is defined with a 
     *  PartyRelationship  where the partyIdFrom is the team Party, roleTypeIdFrom is ACCOUNT_TEAM, partyIdTo is input, partyRelationshipTypeId 
     *  is ASSIGNED_TO, and securityGroupId is either SALES_REP or SALES_MANAGER.
     */
    public static Collection getTeamsForPartyId(String partyId, GenericDelegator delegator) throws GenericEntityException {
        EntityCondition conditions = EntityCondition.makeCondition(UtilMisc.toList(
        		EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "ACCOUNT_TEAM"),
        		EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, partyId),
        		EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "ASSIGNED_TO"),
        		EntityCondition.makeCondition("securityGroupId", EntityOperator.IN, TEAM_SECURITY_GROUPS),
                    EntityUtil.getFilterByDateExpr()
                    ), EntityOperator.AND);
        List relationships = delegator.findList("PartyRelationship", conditions, null, null,null,false);
        Set partyIds = new HashSet();
        for (Iterator iter = relationships.iterator(); iter.hasNext(); ) {
            GenericValue relationship = (GenericValue) iter.next();
            partyIds.add(relationship.get("partyIdFrom"));
        }
        return partyIds;
    }

    /**
     * Gets the active team members in an organization.  A party must be assigned to a team in order for them to be part of this list.
     * TODO there is no actual team -> organization relationship yet.
     */
    public static List<GenericValue> getTeamMembersForOrganization(GenericDelegator delegator) throws GenericEntityException {

        // first let's look up all the ACCOUNT_TEAMs in the system (TODO: this would be constrained to the organizationPartyId via PartyRelationship or such)
        Set<String> accountTeamPartyIds = FastSet.newInstance();
        List<GenericValue> accountTeamRoles = delegator.findByAndCache("PartyRole", UtilMisc.toMap("roleTypeId", "ACCOUNT_TEAM"));
        for (GenericValue role : accountTeamRoles) {
            accountTeamPartyIds.add(role.getString("partyId"));
        }

        // then get all members related to these teams
        EntityCondition conditions = EntityCondition.makeCondition( UtilMisc.toList(
        		EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "ACCOUNT_TEAM"),
        		EntityCondition.makeCondition("partyIdFrom", EntityOperator.IN, accountTeamPartyIds),
        		EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "ASSIGNED_TO"),
        		EntityCondition.makeCondition("securityGroupId", EntityOperator.IN, TEAM_SECURITY_GROUPS),
                    EntityUtil.getFilterByDateExpr()
                    ), EntityOperator.AND);
        return delegator.findList("PartyToSummaryByRelationship", conditions, null, UtilMisc.toList("firstName", "lastName"),null,false);
    }

}
