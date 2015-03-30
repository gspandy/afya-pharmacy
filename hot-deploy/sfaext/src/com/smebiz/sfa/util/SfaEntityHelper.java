package com.smebiz.sfa.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javolution.util.FastList;
import javolution.util.FastSet;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;

/**
 * @author sandeep
 * 
 */
public class SfaEntityHelper {

	public static List<GenericValue> getOpportunities(String partyId,
			boolean includeTeamMembers, GenericDelegator delegator)
			throws GenericEntityException {

		Set<GenericValue> results = FastSet.newInstance();
		List<GenericValue> accounts = getAccounts(partyId, includeTeamMembers,
				delegator);

		String accountId = null;
		for (GenericValue account : accounts) {
			accountId = account.getString("partyId");
			results.addAll(getOpportunities(accountId, delegator));
		}

		return new ArrayList<GenericValue>(results);
	}

	public static List<GenericValue> getOpportunities(String accountId,
			GenericDelegator delegator) throws GenericEntityException {

		EntityCondition entityCondition = EntityCondition
				.makeCondition(UtilMisc.toMap("partyIdTo", accountId,
						"partyRelationshipTypeId", "ACCOUNT_OWNER"));
		System.out.println(" ****************** ENTITY CONDITION IS "
				+ entityCondition);
		return delegator.findList("PartyRelationshipAndSalesOpportunity",
				entityCondition, null, null, null, false);

	}

	public static List<GenericValue> getAccounts(String partyId,
			boolean includeTeamMembers, GenericDelegator delegator)
			throws GenericEntityException {
		return getSfaComponents("ACCOUNT", "ACCOUNT_OWNER", partyId,
				includeTeamMembers, delegator);
	}

	public static List<GenericValue> getContacts(String partyId,
			boolean includeTeamMembers, GenericDelegator delegator)
			throws GenericEntityException {
		return getSfaComponents("CONTACT", "CONTACT_REL", partyId,
				includeTeamMembers, delegator);
	}

	public static List<GenericValue> getLeads(String partyId,
			boolean includeTeamMembers, GenericDelegator delegator)
			throws GenericEntityException {
		return getSfaComponents("LEAD", "LEAD_OWNER", partyId,
				includeTeamMembers, delegator);
	}

	public static List<GenericValue> getSfaComponents(String roleTypeId,
			String partyRelationshipTypeId, String partyId,
			boolean includeTeamMembers, GenericDelegator delegator)
			throws GenericEntityException {
		List<String> partyIds = getReleventPartyIds(partyId,
				includeTeamMembers, delegator);
		List<GenericValue> leads = findSfaPartyComponents(roleTypeId,
				partyRelationshipTypeId, partyIds, delegator);
		return leads;
	}

	private static List<String> getReleventPartyIds(String partyId,
			boolean includeTeamMembers, GenericDelegator delegator)
			throws GenericEntityException {
		Set<String> teamMemberIds = FastSet.newInstance();
		teamMemberIds.add(partyId);
		if (includeTeamMembers) {
			List<GenericValue> teamMembers = getAccountTeamMembers(partyId,
					delegator);
			for (GenericValue teamMember : teamMembers)
				teamMemberIds.add((String) teamMember.get("partyId"));
		}
		return new ArrayList<String>(teamMemberIds);
	}

	public static List<GenericValue> getAccountTeamMembers(String ownerPartyId,
			GenericDelegator delegator) throws GenericEntityException {
		// Finding All the team Ids of which the sent party is the owner
		Map fieldValues = UtilMisc.toMap("partyIdFrom", ownerPartyId,
				"roleTypeIdFrom", "SALES_MGR", "roleTypeIdTo", "ACCOUNT_TEAM",
				"partyRelationshipTypeId", "SALES_OWNER");
		EntityCondition condition = EntityCondition.makeCondition(fieldValues);

		Set<String> fieldsToSelect = FastSet.newInstance();
		fieldsToSelect.add("partyIdTo");
		fieldsToSelect.add("partyIdFrom");
		fieldsToSelect.add("roleTypeIdFrom");
		fieldsToSelect.add("roleTypeIdTo");
		fieldsToSelect.add("partyRelationshipTypeId");
		EntityFindOptions options = new EntityFindOptions();
		options.setDistinct(true);
		List<GenericValue> relevantTeamGVs = delegator.findList(
				"PartyRelationship", condition, fieldsToSelect, null, options,
				false);
		List<String> teamIds = FastList.newInstance();
		for (GenericValue record : relevantTeamGVs)
			teamIds.add((String) record.get("partyIdTo"));

		// Finding All the members for the above teams;
		fieldValues = UtilMisc.toMap("roleTypeIdFrom", "SALES_REP",
				"roleTypeIdTo", "ACCOUNT_TEAM", "partyRelationshipTypeId",
				"ASSIGNED_TO");
		EntityCondition credentialCondition = EntityCondition
				.makeCondition(fieldValues);
		EntityCondition teamCondition = EntityCondition.makeCondition(
				"partyIdTo", EntityOperator.IN, teamIds);
		condition = EntityCondition.makeCondition(credentialCondition,
				teamCondition);
		List<GenericValue> teamMemberGVs = delegator.findList(
				"PartyToSummaryByRelationship", condition, null, null, null,
				false);

		return teamMemberGVs;
	}

	public static List<GenericValue> findSfaPartyComponents(String roleTypeId,
			String partyRelationshipTypeId, List<String> partyIds,
			GenericDelegator delegator) throws GenericEntityException {
		EntityCondition roleCondition = EntityCondition.makeCondition(
				"roleTypeIdTo", roleTypeId);
		EntityCondition relationCondition = EntityCondition.makeCondition(
				"partyRelationshipTypeId", partyRelationshipTypeId);
		EntityCondition ownershipCondition = EntityCondition.makeCondition(
				"partyIdFrom", EntityOperator.IN, partyIds);
		EntityCondition wholeCondition = EntityCondition.makeCondition(
				roleCondition, relationCondition, ownershipCondition);

		Set<String> fieldsToSelect = FastSet.newInstance();
		fieldsToSelect.add("partyRelationshipTypeId");
		fieldsToSelect.add("partyIdFrom");
		fieldsToSelect.add("roleTypeIdFrom");
		fieldsToSelect.add("partyIdTo");
		fieldsToSelect.add("roleTypeIdTo");
		fieldsToSelect.add("partyId");
		fieldsToSelect.add("partyTypeId");
		fieldsToSelect.add("description");
		fieldsToSelect.add("firstName");
		fieldsToSelect.add("middleName");
		fieldsToSelect.add("lastName");
		fieldsToSelect.add("lastNameLocal");
		fieldsToSelect.add("firstNameLocal");
		fieldsToSelect.add("personalTitle");
		fieldsToSelect.add("suffix");
		fieldsToSelect.add("groupName");
		fieldsToSelect.add("groupNameLocal");

		EntityFindOptions options = new EntityFindOptions();
		options.setDistinct(true);

		List<GenericValue> result = delegator.findList(
				"PartyRelationshipAndDetail", wholeCondition, fieldsToSelect,
				null, options, false);
		return result;
	}
}