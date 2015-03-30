package com.smebiz.sfa.opportunities;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.common.period.PeriodWorker;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.finder.EntityFinderUtil;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;

/**
 * Opportunity utility methods.
 *
 * @author <a href="mailto:leon@opensourcestrategies.com">Leon Torres</a>
 */

public class UtilOpportunity {

    public static final String module = UtilOpportunity.class.getName();

    /**
     * Helper method to get the principal account for an opportunity. This is a
     * simplification of the datamodel and should only be calld for non-critical
     * uses. Returns null if no account was found, which would be the case if
     * there were a lead party Id instead.
     */
    public static String getOpportunityAccountPartyId(GenericValue opportunity)
            throws GenericEntityException {
        List candidates = opportunity.getRelatedByAnd("SalesOpportunityRole",
                UtilMisc.toMap("roleTypeId", "ACCOUNT"));
        if (candidates.size() == 0)
            return null;
        // we have two out of three primary keys, so the result is guaranteed to
        // be the one with our partyId
        GenericValue salesOpportunityRole = (GenericValue) candidates.get(0);
        return salesOpportunityRole.getString("partyId");
    }

    /**
     * Helper method to get the principal lead for an opportunity. This is a
     * simplification of the datamodel and should only be calld for non-critical
     * uses. Returns null if no lead was found, which would be the case if there
     * were an account party Id instead.
     */
    public static String getOpportunityLeadPartyId(GenericValue opportunity)
            throws GenericEntityException {
        List candidates = opportunity.getRelatedByAnd("SalesOpportunityRole",
                UtilMisc.toMap("roleTypeId", "PROSPECT"));
        if (candidates.size() == 0)
            return null;
        // we have two out of three primary keys, so the result is guaranteed to
        // be the one with our partyId
        GenericValue salesOpportunityRole = (GenericValue) candidates.get(0);
        return salesOpportunityRole.getString("partyId");
    }

    /**
     * Helper method to get the principal lead or account partyId of an
     * opportunity. Use this to get one or the other.
     */
    public static String getOpportunityAccountOrLeadPartyId(
            GenericValue opportunity) throws GenericEntityException {
        List candidates = opportunity.getRelatedByAnd("SalesOpportunityRole",
                UtilMisc.toMap("roleTypeId", "ACCOUNT"));
        if (candidates.size() > 0)
            return ((GenericValue) candidates.get(0)).getString("partyId");
        candidates = opportunity.getRelatedByAnd("SalesOpportunityRole",
                UtilMisc.toMap("roleTypeId", "PROSPECT"));
        if (candidates.size() > 0)
            return ((GenericValue) candidates.get(0)).getString("partyId");
        return null;
    }

    /**
     * Helper method to get all account party Id's for an opportunity. This is a
     * more serious version of the above for use in critical logic, such as
     * security or in complex methods that should use the whole list from the
     * beginning.
     */
    public static List getOpportunityAccountPartyIds(
            GenericDelegator delegator, String salesOpportunityId)
            throws GenericEntityException {
        return getOpportunityPartiesByRole(delegator, salesOpportunityId,
                "ACCOUNT");
    }

    /**
     * Helper method to get all lead party Id's for an opportunity. See comments
     * for getOpportunityAccountPartyIds().
     */
    public static List getOpportunityLeadPartyIds(GenericDelegator delegator,
                                                  String salesOpportunityId) throws GenericEntityException {
        return getOpportunityPartiesByRole(delegator, salesOpportunityId,
                "PROSPECT");
    }

    /**
     * Helper method to get all contact party Id's for an opportunity.
     */
    public static List getOpportunityContactPartyIds(
            GenericDelegator delegator, String salesOpportunityId)
            throws GenericEntityException {
        return getOpportunityPartiesByRole(delegator, salesOpportunityId,
                "CONTACT");
    }

    /**
     * Helper method to get all party Id's of a given role for an opportunity.
     * It's better to use one of the more specific methods above.
     */
    public static List getOpportunityPartiesByRole(GenericDelegator delegator,
                                                   String salesOpportunityId, String roleTypeId)
            throws GenericEntityException {
        List maps = delegator.findByAnd("SalesOpportunityRole", UtilMisc.toMap(
                "roleTypeId", roleTypeId, "salesOpportunityId",
                salesOpportunityId));
        List results = new ArrayList();
        for (Iterator iter = maps.iterator(); iter.hasNext(); ) {
            GenericValue map = (GenericValue) iter.next();
            results.add(map.getString("partyId"));
        }
        return results;
    }

    /**
     * Helper method to make a sales opportunity history, which should be done
     * whenever an opp is created, updated or deleted.
     *
     * @return The created SalesOpportunityHistory
     */
    public static GenericValue createSalesOpportunityHistory(
            GenericValue opportunity, GenericDelegator delegator, Map context)
            throws GenericEntityException {
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String historyId = delegator.getNextSeqId("SalesOpportunityHistory");
        GenericValue history = delegator.makeValue("SalesOpportunityHistory",
                UtilMisc.toMap("salesOpportunityHistoryId", historyId));

        // we assume the opportunity has all fields set as desired already,
        // especially the probability
        history.setNonPKFields(opportunity.getAllFields());
        history.set("changeNote", context.get("changeNote"));
        history.set("modifiedByUserLogin", userLogin.getString("userLoginId"));
        history.set("modifiedTimestamp", UtilDateTime.nowTimestamp());
        history.create();
        return history;
    }

    /**
     * Returns all account and lead opportunities for an internalPartyId. This
     * is done by looking for all opportunities belonging to accounts and leads
     * that the internalPartyId is RESPONSIBLE_FOR.
     *
     * @param organizationPartyId  - filter by organization TODO: not implemented
     * @param internalPartyId      - lookup opportunities for this party
     * @param customTimePeriodId   - if not null, will only get them for this time period
     * @param additionalConditions - if not null, this EntityConditionList will be added as well
     * @param orderBy              - List of fields to order results by, can be null
     * @param delegator
     * @return
     * @throws GenericEntityException
     */
    public static EntityListIterator getOpportunitiesForMyAccounts(
            String organizationPartyId, String internalPartyId,
            String customTimePeriodId,
            EntityConditionList additionalConditions, List orderBy,
            GenericDelegator delegator) throws GenericEntityException {

        // build condition to get list of PROSPECT or ACCOUNT opportunities that
        // the user is RESPONSIBLE_FOR
        List combinedConditions = UtilMisc.toList(EntityCondition.makeCondition("partyIdTo",
                EntityOperator.EQUALS, internalPartyId), EntityCondition.makeCondition(
                "partyRelationshipTypeId", EntityOperator.EQUALS,
                "RESPONSIBLE_FOR"), EntityCondition.makeCondition(UtilMisc.toList(
                EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS,
                        "PROSPECT"), EntityCondition.makeCondition("roleTypeIdFrom",
                EntityOperator.EQUALS, "ACCOUNT")), EntityOperator.OR),
                EntityUtil.getFilterByDateExpr()); // filter out expired
        // accounts

        return getOpportunitiesForPartyHelper(customTimePeriodId,
                combinedConditions, additionalConditions, orderBy, delegator);
    }

    /**
     * As getOpportunitiesForMyAccounts but gets all account opportunities for
     * all teams the party belongs to. Also includes lead opportunities that the
     * internalPartyId is RESPONSIBLE_FOR.
     */
    public static EntityListIterator getOpportunitiesForMyTeams(
            String organizationPartyId, String internalPartyId,
            String customTimePeriodId,
            EntityConditionList additionalConditions, List orderBy,
            GenericDelegator delegator) throws GenericEntityException {

        // strategy: find all the accounts of the internalPartyId, then find all
        // the opportunities of those accounts
        EntityConditionList conditions = EntityCondition.makeCondition(UtilMisc
                .toList(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS,
                        internalPartyId), EntityCondition.makeCondition("roleTypeIdFrom",
                        EntityOperator.IN, UtilMisc.toList("ACCOUNT",
                        "PROSPECT")), EntityCondition.makeCondition(UtilMisc
                        .toList(EntityCondition.makeCondition("partyRelationshipTypeId",
                                EntityOperator.EQUALS, "RESPONSIBLE_FOR"),
                                EntityCondition.makeCondition("partyRelationshipTypeId",
                                        EntityOperator.EQUALS, "ASSIGNED_TO")),
                        EntityOperator.OR), EntityUtil.getFilterByDateExpr()),
                EntityOperator.AND);
        List accounts = delegator.findList("PartyRelationship",
                conditions, null, null, null, false);
        ArrayList accountIds = new ArrayList();
        for (Iterator iter = accounts.iterator(); iter.hasNext(); ) {
            GenericValue account = (GenericValue) iter.next();
            accountIds.add(account.get("partyIdFrom"));
        }

        // if no accounts are found, then return a null
        if (accountIds.size() < 1) {
            return null;
        }

        // build the condition to find opportunitied belonging to these accounts
        List combinedConditions = UtilMisc.toList(EntityCondition.makeCondition("partyIdFrom",
                EntityOperator.IN, accountIds), EntityCondition.makeCondition(
                "roleTypeIdFrom", EntityOperator.IN, UtilMisc.toList("ACCOUNT",
                "PROSPECT")));

        return getOpportunitiesForPartyHelper(customTimePeriodId,
                combinedConditions, additionalConditions, orderBy, delegator);
    }

    /**
     * As getOpportunitiesForMyAccounts but returns Account and Lead
     * opportunities that the internalPartyId is assigned to. Note that this is
     * a superset of getOpportunitiesForMyAccounts, which returns the
     * opportunities that the internalPartyId is directly responsible for. Use
     * this method to get all opportunities that the internalPartyId can see.
     */
    public static EntityListIterator getOpportunitiesForInternalParty(
            String organizationPartyId, String internalPartyId,
            String customTimePeriodId,
            EntityConditionList additionalConditions, List orderBy,
            GenericDelegator delegator) throws GenericEntityException {

        // build condition to get list of ACCOUNT or PROSPECT opportunities for
        // the supplied internal party
        List combinedConditions = UtilMisc.toList(EntityCondition.makeCondition("partyIdTo",
                EntityOperator.EQUALS, internalPartyId),
                EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition(
                        "roleTypeIdFrom", EntityOperator.EQUALS, "ACCOUNT"),
                        EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS,
                                "PROSPECT")), EntityOperator.OR), EntityUtil
                .getFilterByDateExpr()); // filter out expired accounts

        return getOpportunitiesForPartyHelper(customTimePeriodId,
                combinedConditions, additionalConditions, orderBy, delegator);
    }

    /**
     * Returns all account and lead opportunities.
     */
    public static EntityListIterator getOpportunities(
            String organizationPartyId, String customTimePeriodId,
            EntityConditionList additionalConditions, List orderBy,
            GenericDelegator delegator) throws GenericEntityException {
        // build condition to get list of ACCOUNT or PROSPECT opportunities for
        // all the parties
        List combinedConditions = UtilMisc.toList(EntityCondition.makeCondition(
                UtilMisc.toList(EntityCondition.makeCondition("roleTypeIdFrom",
                        EntityOperator.EQUALS, "PROSPECT"), EntityCondition.makeCondition(
                        "roleTypeIdFrom", EntityOperator.EQUALS, "ACCOUNT")),
                EntityOperator.OR), EntityUtil.getFilterByDateExpr()); // filter
        // out
        // expired
        // accounts

        return getOpportunitiesForPartyHelper(customTimePeriodId,
                combinedConditions, additionalConditions, orderBy, delegator);
    }

    private static EntityListIterator getOpportunitiesForPartyHelper(
            String customTimePeriodId, List combinedConditions,
            EntityConditionList additionalConditions, List orderBy,
            GenericDelegator delegator) throws GenericEntityException {
        // if a time period is supplied, use it as a condition as well
        if ((customTimePeriodId != null)) {
            GenericValue timePeriod = delegator.findByPrimaryKeyCache(
                    "CustomTimePeriod", UtilMisc.toMap("customTimePeriodId",
                    customTimePeriodId));
            if (timePeriod != null) {
                combinedConditions.add(PeriodWorker.getFilterByPeriodExpr(
                        "estimatedCloseDate", timePeriod));
            }
        }

        // if additional conditions are passed in, add them as well
        if (additionalConditions != null) {
            combinedConditions.add(additionalConditions);
        }
        EntityCondition conditionList = EntityCondition.makeCondition(
                combinedConditions, EntityOperator.AND);

        // fields to select
        Set fields = UtilMisc.toSet("salesOpportunityId", "partyIdFrom",
                "opportunityName", "opportunityStageId", "estimatedAmount",
                "estimatedCloseDate");
        fields.add("estimatedProbability");
        fields.add("currencyUomId");

        // get the SalesOpportunityAndRoles for these accounts
        EntityListIterator opportunities = delegator
                .find(
                        "PartyRelationshipAndSalesOpportunity", conditionList,
                        null, fields, orderBy, // fields to order by (can't use
                        // fromDate here because it's
                        // part of multiple tables =>
                        // need the alias.fromDate hack)
                        // the first true here is for "specifyTypeAndConcur"
                        // the second true is for a distinct select. Apparently
                        // this is the only way the entity engine can do a
                        // distinct query
                        new EntityFindOptions(true,
                                EntityFindOptions.TYPE_SCROLL_INSENSITIVE,
                                EntityFindOptions.CONCUR_READ_ONLY, true));
        return opportunities;
    }

    /**
     * Gets a List of team members for a given opportunity.
     *
     * @return List of GenericValue PartyToSummaryByRelationship for team
     *         members
     */
    public static List getOpportunityTeamMembers(String salesOpportunityId,
                                                 GenericDelegator delegator) throws GenericEntityException {
        // At this point, it is sufficient to traverse the directly related
        // primary account
        // We'll ignore accounts associated through related contacts for now.
        GenericValue opportunity = delegator.findByPrimaryKey(
                "SalesOpportunity", UtilMisc.toMap("salesOpportunityId",
                salesOpportunityId));
        String accountPartyId = getOpportunityAccountPartyId(opportunity);

        EntityConditionList conditions = EntityCondition.makeCondition(UtilMisc
                .toList(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS,
                        accountPartyId), EntityCondition.makeCondition("roleTypeIdFrom",
                        EntityOperator.EQUALS, "ACCOUNT"), EntityCondition.makeCondition(
                        "partyRelationshipTypeId", EntityOperator.EQUALS,
                        "ASSIGNED_TO"), EntityUtil.getFilterByDateExpr()),
                EntityOperator.AND);
        return delegator.findList("PartyToSummaryByRelationship",
                conditions, null, null, null, false);
    }

}
