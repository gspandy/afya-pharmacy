package com.smebiz.sfa.party;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericEntityNotFoundException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

public class SfaPartyHelper {

    private static String module="SfaPartyHelper";
	public static List<String> TEAM_MEMBER_ROLES = UtilMisc.toList(
			"ACCOUNT_MANAGER", "ACCOUNT_REP", "CUST_SERVICE_REP");
	public static List<String> CLIENT_PARTY_ROLES = UtilMisc.toList("ACCOUNT",
			"CONTACT", "PROSPECT","ACCOUNT_TEAM");
	public static List<String> FIND_PARTY_FIELDS = Arrays.asList(new String[] {
			"firstName", "lastName", "groupName", "partyId", "companyName",
			"primaryEmailId", "primaryPostalAddressId",
			"primaryTelecomNumberId", "primaryCity",
			"primaryStateProvinceGeoId", "primaryCountryGeoId", "primaryEmail",
			"primaryCountryCode", "primaryAreaCode", "primaryContactNumber" });

	/**
	 * A helper method which finds the first valid roleTypeId for a partyId,
	 * using a List of possible roleTypeIds
	 * 
	 * @param partyId
	 * @param possibleRoleTypeIds
	 *            a List of roleTypeIds
	 * @param delegator
	 * @return the first roleTypeId from possibleRoleTypeIds which is actually
	 *         found in PartyRole for the given partyId
	 * @throws GenericEntityException
	 */
	public static String getFirstValidRoleTypeId(String partyId,
			List possibleRoleTypeIds, GenericDelegator delegator)
			throws GenericEntityException {

		List partyRoles = delegator.findByAndCache("PartyRole", UtilMisc.toMap(
				"partyId", partyId));

		// iterate across all possible roleTypeIds from the parameter
		Iterator iterValid = possibleRoleTypeIds.iterator();
		while (iterValid.hasNext()) {
			String possibleRoleTypeId = (String) iterValid.next();

			// try to look for each one in the list of PartyRoles
			Iterator partyRolesIter = partyRoles.iterator();
			while (partyRolesIter.hasNext()) {
				GenericValue partyRole = (GenericValue) partyRolesIter.next();
				if (possibleRoleTypeId
						.equals(partyRole.getString("roleTypeId"))) {
					return possibleRoleTypeId;
				}
			}
		}
		return null;
	}

	/**
	 * As above, but pass in the list of internal party roles, such as ACCOUNT,
	 * CONTACT, PROSPECT
	 */
	public static String getFirstValidInternalPartyRoleTypeId(String partyId,
			GenericDelegator delegator) throws GenericEntityException {
		return getFirstValidRoleTypeId(partyId, CLIENT_PARTY_ROLES, delegator);
	}

	public static List<GenericValue> filterResults(
			List<GenericValue> searchResults, List<GenericValue> sfaEntities) {

		List<GenericValue> results = FastList.newInstance();
		if (searchResults.size() > sfaEntities.size()) {
			for (GenericValue searchEntity : searchResults) {
				for (GenericValue sfaEntity : sfaEntities) {
					if (searchEntity.getString("partyId").equals(
							sfaEntity.getString("partyId"))) {
						results.add(searchEntity);
					}
				}
			}
		} else {
			for (GenericValue sfaEntity : sfaEntities) {
				for (GenericValue searchEntity : searchResults) {
					if (searchEntity.getString("partyId").equals(
							sfaEntity.getString("partyId"))) {
						results.add(searchEntity);
					}
				}
			}
		}
		return results;
	}

	public static boolean isActive(String partyId, GenericDelegator delegator)
			throws GenericEntityException {
		GenericValue party = delegator.findByPrimaryKey("Party", UtilMisc
				.toMap("partyId", partyId));
		if (party == null) {
			throw new GenericEntityNotFoundException("No Party found with ID: "
					+ partyId);
		}
		return (!"PARTY_DISABLED".equals(party.getString("statusId")));
	}
	
	  /**
     * As above, but pass in the list of team member roles such as ACCOUNT_REP, etc.
     */
    public static String getFirstValidTeamMemberRoleTypeId(String partyId, GenericDelegator delegator) throws GenericEntityException {
        return getFirstValidRoleTypeId(partyId, TEAM_MEMBER_ROLES, delegator);
    }

    /** Find the first valid role of the party, whether it be a team member or client party. */
    public static String getFirstValidCrmsfaPartyRoleTypeId(String partyId, GenericDelegator delegator) throws GenericEntityException {
        String roleTypeId = getFirstValidRoleTypeId(partyId, TEAM_MEMBER_ROLES, delegator);
        if (roleTypeId == null) {
            roleTypeId = getFirstValidRoleTypeId(partyId, CLIENT_PARTY_ROLES, delegator);
        }
        return roleTypeId;
    }

    
    /**
     * A helper method for creating a PartyRelationship entity from partyIdTo to partyIdFrom with specified partyRelationshipTypeId, roleTypeIdFrom,
     * a List of valid roles for the to-party, and a flag to expire any existing relationships between the two parties of the same
     * type.   The idea is that several services would do validation and then use this method to do all the work.
     * 
     * @param partyIdTo
     * @param partyIdFrom
     * @param roleTypeIdFrom
     * @param partyRelationshipTypeId
     * @param securityGroupId
     * @param validToPartyRoles  List of roleTypeIds which are valid for the partyIdTo in the create relationship.  It will cycle
     * through until the first of these roles is actually associated with partyIdTo and then create a PartyRelationship using that
     * roleTypeId.  If none of these are associated with partyIdTo, then it will return false
     * @param fromDate 
     * @param expireExistingRelationships  If set to true, will look for all existing PartyRelationships of partyIdFrom, partyRelationshipTypeId
     * and expire all of them as of the passed in fromDate
     * @return false if no relationship was created or true if operation succeeds
     */
    public static boolean createNewPartyToRelationship(String partyIdTo, String partyIdFrom, String roleTypeIdFrom,
            String partyRelationshipTypeId, String securityGroupId, List validToPartyRoles, 
            boolean expireExistingRelationships, GenericValue userLogin, GenericDelegator delegator, LocalDispatcher dispatcher) 
        throws GenericEntityException, GenericServiceException {
    	
    	Timestamp fromDate = UtilDateTime.nowTimestamp();
    	try{
    	// get the first valid roleTypeIdTo from a list of possible roles for the partyIdTo 
        // this will be the role we use as roleTypeIdTo in PartyRelationship.
        String roleTypeIdTo = getFirstValidRoleTypeId(partyIdTo, validToPartyRoles, delegator);

        // if no matching roles were found, then no relationship created
        if (roleTypeIdTo == null) return false;

        /* 
         * if expireExistingRelationships is true, then find all existing PartyRelationships with partyIdFrom and partyRelationshipTypeId which 
         * are not expired on the fromDate and then expire them
         */
        if (expireExistingRelationships == true) {
            List partyRelationships = delegator.findByAnd("PartyRelationship", UtilMisc.toMap("partyIdFrom", partyIdFrom, "partyRelationshipTypeId", partyRelationshipTypeId));
            expirePartyRelationships(partyRelationships, fromDate, dispatcher, userLogin);
        }

        // call createPartyRelationship service to create PartyRelationship using parameters and the role we just found
        Map input = UtilMisc.toMap("partyIdTo", partyIdTo, "roleTypeIdTo", roleTypeIdTo, "partyIdFrom", partyIdFrom, "roleTypeIdFrom", roleTypeIdFrom);
        input.put("partyRelationshipTypeId", partyRelationshipTypeId);
        input.put("securityGroupId", securityGroupId);
        input.put("fromDate", fromDate);
        input.put("userLogin", userLogin);
        Map serviceResult = dispatcher.runSync("createPartyRelationship", input);
        if(ServiceUtil.isError(serviceResult))
        	return false;
    	}catch(Exception e){
    		return false;
    	}
        return true;
      
    }
    
    /**
     * Expires a list of PartyRelationships that are still active on expireDate.
     */
    public static void expirePartyRelationships(List partyRelationships, Timestamp expireDate, LocalDispatcher dispatcher, GenericValue userLogin) 
    throws GenericServiceException {
        List relationsActiveOnFromDate = EntityUtil.filterByDate(partyRelationships, expireDate);

        // to expire on expireDate, set the thruDate to the expireDate in the parameter and call updatePartyRelationship service
        Iterator iter = relationsActiveOnFromDate.iterator();
        while (iter.hasNext()) {
            GenericValue partyRelationship = (GenericValue) iter.next();
            Map input = UtilMisc.toMap("partyIdTo", partyRelationship.getString("partyIdTo"), "roleTypeIdTo", partyRelationship.getString("roleTypeIdTo"), 
                    "partyIdFrom", partyRelationship.getString("partyIdFrom"), "roleTypeIdFrom", partyRelationship.getString("roleTypeIdFrom"));
            input.put("fromDate", partyRelationship.getTimestamp("fromDate"));
            input.put("userLogin", userLogin);
            input.put("thruDate", expireDate);
            Map serviceResult = dispatcher.runSync("updatePartyRelationship", input);
            if (ServiceUtil.isError(serviceResult)) {
                throw new GenericServiceException("Failed to expire PartyRelationship with values: " + input.toString());
            }
        }
    }
    

    /**
     * Find active "from" parties such as accounts, contacts, and leads based on PartyFromSummaryByRelationship.
     * These parties can be thought of the main subjects or clients tracked by CRMSFA.
     * Uses the prepareFind service to help build search conditions.
     * 
     * The ordering of the fields is controlled by a parameter named activeOrderBy, which may take on
     * the values "lastName" or "companyName".  The default is to order by groupName.
     *
     * @param parameters    A map of fields and condition parameters to be consumed by prepareFind service.
     * @param ec            Optional EntityCondition to filter the results by
     * @param roles         Optional list of CRMSFA roles to limit to.  If roles is null or empty, uses the default CLIENT_PARTY_ROLES.
     * @return EntityListIterator of results or null if the find condition is nothing.
     */
    public static EntityListIterator findActiveClientParties(GenericDelegator delegator, LocalDispatcher dispatcher, Map parameters, List roles, EntityCondition ec) throws GeneralException {
        EntityCondition conditions = getActiveClientPartiesCondition(dispatcher, parameters, roles, ec);
        List orderBy = getActiveClientPartiesOrderBy(parameters);
        return delegator.find("PartyFromSummaryByRelationship", conditions, (EntityCondition) FIND_PARTY_FIELDS, null, orderBy, null);
    }

    @SuppressWarnings("unchecked")
	private static EntityCondition getActiveClientPartiesCondition(LocalDispatcher dispatcher, Map parameters, List roles, EntityCondition ec) throws GeneralException {
        Map results = dispatcher.runSync("prepareFind", UtilMisc.toMap("entityName", "PartyFromSummaryByRelationship", "inputFields", parameters,
                "filterByDate", "Y", "noConditionFind", "N"));
        if (ServiceUtil.isError(results)) throw new GenericServiceException(ServiceUtil.getErrorMessage(results));
        EntityConditionList findConditions = (EntityConditionList) results.get("entityConditionList");
        if (findConditions == null) return null;

        List conditionRoles = (roles == null || roles.size() == 0 ? CLIENT_PARTY_ROLES : roles);
        List combinedConditions = UtilMisc.toList( findConditions, EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.IN, conditionRoles) );
        if (ec != null) combinedConditions.add( ec );
        
        return EntityCondition.makeCondition(combinedConditions, EntityOperator.AND);
    }


    private static List getActiveClientPartiesOrderBy(Map parameters) {
         List orderBy = UtilMisc.toList("groupName", "lastName", "companyName"); // fields to order by (default)

        // see if we're given a different order by
        String requestOrderBy = (String) parameters.get("activeOrderBy");
        if ("lastName".equals(requestOrderBy)) {
            orderBy = UtilMisc.toList("lastName", "groupName", "companyName");
        } else if ("companyName".equals(requestOrderBy)) {
            orderBy = UtilMisc.toList("companyName", "groupName", "lastName");
        }
        return orderBy;
    }
    
    /** As above, but only consider the search parameters with optional conditions. */
    public static EntityListIterator findActiveClientParties(GenericDelegator delegator, LocalDispatcher dispatcher, Map parameters, EntityCondition ec) throws GeneralException {
        return findActiveClientParties(delegator, dispatcher, parameters, null, ec);
    }

    /** Finds all active accounts, contacts, leads and so on of a given partyId */
    public static EntityListIterator findActiveClientParties(GenericDelegator delegator, LocalDispatcher dispatcher, String partyId) throws GeneralException {
        Map parameters = UtilMisc.toMap("partyIdTo", partyId);
        return findActiveClientParties(delegator, dispatcher, parameters, null, null);
    }

    /** Finds active Accounts for a party. */
    public static EntityListIterator findActiveAccounts(GenericDelegator delegator, LocalDispatcher dispatcher, String partyId) throws GeneralException {
        return findActiveClientParties(delegator, dispatcher, UtilMisc.toMap("partyIdTo", partyId), UtilMisc.toList("ACCOUNT"), null);
    }

    /** Finds active Contacts for a party. */
    public static EntityListIterator findActiveContacts(GenericDelegator delegator, LocalDispatcher dispatcher, String partyId) throws GeneralException {
        return findActiveClientParties(delegator, dispatcher, UtilMisc.toMap("partyIdTo", partyId), UtilMisc.toList("CONTACT"), null);
    }

    /** Finds active Leads for a party. */
    public static EntityListIterator findActiveLeads(GenericDelegator delegator, LocalDispatcher dispatcher, String partyId) throws GeneralException {
        return findActiveClientParties(delegator, dispatcher, UtilMisc.toMap("partyIdTo", partyId), UtilMisc.toList("PROSPECT"), null);
    }

    /**
     * Method to get the current non-expired party responsible for the given account/contact/lead. 
     *
     * @param   partyIdFrom     The partyId of the account/contact/lead
     * @param   roleTypeIdFrom  The role of the account/contact/lead (e.g., ACCOUNT, CONTACT, LEAD)
     * @return  First non-expired PartySummaryCRMView or null if none found
     */
    public static GenericValue getCurrentResponsibleParty(String partyIdFrom, String roleTypeIdFrom, GenericDelegator delegator) throws GenericEntityException {
        return getActivePartyByRole("RESPONSIBLE_FOR", partyIdFrom, roleTypeIdFrom, UtilDateTime.nowTimestamp(), delegator);
    }

    /** Method to get the current lead owner of a lead */
    public static GenericValue getCurrentLeadOwner(String leadPartyId, GenericDelegator delegator) throws GenericEntityException {
        return getActivePartyByRole("RESPONSIBLE_FOR", leadPartyId, "PROSPECT", "LEAD_OWNER", UtilDateTime.nowTimestamp(), delegator);
    }

    /**
     * Common method used by getCurrentlyResponsibleParty and related methods. This method will obtain the first PartyRelationship found with the given criteria
     * and return the PartySummaryCRMView with partyId = partyRelationship.partyIdTo.
     *
     * @param   partyRelationshipTypeId         The party relationship (e.g., reps that are RESPONSIBLE_FOR an account)
     * @param   partyIdFrom                     The partyId of the account/contact/lead
     * @param   roleTypeIdFrom                  The role of the account/contact/lead (e.g., ACCOUNT, CONTACT, LEAD)
     * @param   securityGroupId                 Optional securityGroupId of the relationsihp
     * @param   activeDate                      Check only for active relationships as of this timestamp
     * @return  First non-expired PartySummaryCRMView or null if none found
     */
    /**
     * Common method used by getCurrentlyResponsibleParty and related methods. This method will obtain the first PartyRelationship found with the given criteria
     * and return the PartySummaryCRMView with partyId = partyRelationship.partyIdTo.
     *
     * @param   partyRelationshipTypeId         The party relationship (e.g., reps that are RESPONSIBLE_FOR an account)
     * @param   partyIdFrom                     The partyId of the account/contact/lead
     * @param   roleTypeIdFrom                  The role of the account/contact/lead (e.g., ACCOUNT, CONTACT, LEAD)
     * @param   securityGroupId                 Optional securityGroupId of the relationship
     * @param   activeDate                      Check only for active relationships as of this timestamp
     * @return  First non-expired PartySummaryCRMView or null if none found
     */
    public static GenericValue getActivePartyByRole(String partyRelationshipTypeId, String partyIdFrom, String roleTypeIdFrom, String securityGroupId,
            Timestamp activeDate, GenericDelegator delegator) 
    throws GenericEntityException {

        Map input = UtilMisc.toMap("partyRelationshipTypeId", partyRelationshipTypeId, "partyIdFrom", partyIdFrom, "roleTypeIdFrom", roleTypeIdFrom);
        if (securityGroupId != null) input.put("securityGroupId", securityGroupId);
        List relationships = delegator.findByAnd("PartyRelationship", input);
        List activeRelationships = EntityUtil.filterByDate(relationships, activeDate);
        // if none are found, log a message about this and return null
        if (activeRelationships.size() == 0) {
            Debug.logInfo("No active PartyRelationships found with relationship [" + partyRelationshipTypeId + "] for party [" 
                    + partyIdFrom +"] in role [" + roleTypeIdFrom +"]", module);
            return null;
        }

        // return the related party with partyId = partyRelationship.partyIdTo
        GenericValue partyRelationship = (GenericValue) activeRelationships.get(0);
        return delegator.findByPrimaryKey("PartySummaryCRMView", UtilMisc.toMap("partyId", partyRelationship.getString("partyIdTo")));
    }

    /** As above but without security group Id specified */
    public static GenericValue getActivePartyByRole(String partyRelationshipTypeId, String partyIdFrom, String roleTypeIdFrom, 
            Timestamp activeDate, GenericDelegator delegator) 
    throws GenericEntityException {
        return getActivePartyByRole(partyRelationshipTypeId, partyIdFrom, roleTypeIdFrom, null, activeDate, delegator);
    }

    /**
     * Method to copy all "To" relationships of a From party to another From party. For instance, use this method to copy all relationships of an
     * account (or optionally a specific relationship), such as the managers and reps, over to a team.
     * NOTE: This service works on unexpired relationships as of now and will need to be refactored for other Dates.
     *
     * @param   partyIdFrom
     * @param   roleTypeIdFrom
     * @param   partyRelationshipTypeId         optional
     * @param   newPartyIdFrom
     * @param   newRoleTypeIdFrom
     */
    public static void copyToPartyRelationships(String partyIdFrom, String roleTypeIdFrom, String partyRelationshipTypeId, 
            String newPartyIdFrom, String newRoleTypeIdFrom, GenericValue userLogin, GenericDelegator delegator, LocalDispatcher dispatcher)
    throws GenericEntityException, GenericServiceException {

        // hardcoded activeDate
        Timestamp activeDate = UtilDateTime.nowTimestamp();

        // first get the unexpired relationships for the From party
        Map input = UtilMisc.toMap("partyIdFrom", partyIdFrom, "roleTypeIdFrom", roleTypeIdFrom); 
        if (partyRelationshipTypeId != null) input.put("partyRelationshipTypeId", partyRelationshipTypeId);
        List relationships = delegator.findByAnd("PartyRelationship", input);
        List activeRelationships = EntityUtil.filterByDate(relationships, activeDate);

        for (Iterator iter = activeRelationships.iterator(); iter.hasNext();) {
            GenericValue relationship = (GenericValue) iter.next();

            input = UtilMisc.toMap("partyIdTo", relationship.getString("partyIdTo"), "roleTypeIdTo", relationship.getString("roleTypeIdTo"));
            input.put("partyIdFrom", newPartyIdFrom);
            input.put("roleTypeIdFrom", newRoleTypeIdFrom);
            input.put("fromDate", activeDate);

            // if relationship already exists, continue
            GenericValue check = delegator.findByPrimaryKey("PartyRelationship", input);
            if (check != null) continue;

            // create the relationship
            input.put("partyRelationshipTypeId", relationship.getString("partyRelationshipTypeId"));
            input.put("securityGroupId", relationship.getString("securityGroupId"));
            input.put("statusId", relationship.getString("statusId"));
            input.put("priorityTypeId", relationship.getString("priorityTypeId"));
            input.put("comments", relationship.getString("comments"));
            input.put("userLogin", userLogin);
            Map serviceResult = dispatcher.runSync("createPartyRelationship", input);
            if (ServiceUtil.isError(serviceResult)) {
                throw new GenericServiceException(ServiceUtil.getErrorMessage(serviceResult));
            }
        }
    }

    /**
     * Same as above, but passes partyRelationshipTypeId = null so that all relationship types are selected.
     */
    public static void copyToPartyRelationships(String partyIdFrom, String roleTypeIdFrom, String newPartyIdFrom, String newRoleTypeIdFrom, 
            GenericValue userLogin, GenericDelegator delegator, LocalDispatcher dispatcher)
    throws GenericEntityException, GenericServiceException {

        copyToPartyRelationships(partyIdFrom, roleTypeIdFrom, null, newPartyIdFrom, newRoleTypeIdFrom, userLogin, delegator, dispatcher);
    }

    /** 
     * This array determines the entities in which to delete the party and the order of deletion. 
     * Party should be the very last element. The second element in each row denotes the partyId 
     * field to check. XXX Note: We are deleting historical data. For instance, activity records
     * involving the partyId will be gone forever!
     */
    private static String[][] CRM_PARTY_DELETE_CASCADE = {
        //{"CustRequestRole", "partyId"},
        {"PartyNote", "partyId"},
        {"PartyDataSource", "partyId"},
        //{"WorkEffortPartyAssignment", "partyId"},
        {"ContentRole", "partyId"},
        {"DataResourceRole", "partyId"},
        {"MarketingCampaignRole","partyId"},
        {"PartyContent", "partyId"},
        {"PartyContactMechPurpose", "partyId"},
        {"PartyContactMech", "partyId"},
        {"PartySupplementalData", "partyId"},
        {"PartyNameHistory", "partyId"},
        {"PartyGroup", "partyId"},
        {"PartyRelationship", "partyIdFrom"},
        {"PartyRelationship", "partyIdTo"},
        {"Person", "partyId"},
        {"PartyRole", "partyId"},
        {"PartyStatus", "partyId"},
        {"Party", "partyId"}
    };

    /** 
     * Performs a cascade delete on a party.
     *
     * One reason this method can fail is that there were relationships with entities that are not being deleted.
     * If a party is not being deleted like it should, the developer should take a look at the exception thrown
     * by this method to see if any relations were violated. If there were violations, consider adding
     * the entities to the CASCADE array above.
     *
     * XXX Warning, this method is very brittle. It is essentially emulating the ON DELETE CASCADE functionality
     * of well featured databases, but very poorly. As the datamodel evolves, this method would have to be updatd.
     */
    public static void deleteCrmParty(String partyId, GenericDelegator delegator) throws GenericEntityException {
        for (int i = 0; i < CRM_PARTY_DELETE_CASCADE.length; i++) {
            String entityName = CRM_PARTY_DELETE_CASCADE[i][0];
            String fieldName = CRM_PARTY_DELETE_CASCADE[i][1];

            Map input = UtilMisc.toMap(fieldName, partyId);
            delegator.removeByAnd(entityName, input);
        }
    }

    /**
     * Generates a party name in the standard CRMSFA style.  Input is a PartySummaryCRMView or any
     * view entity with fields partyId, groupName, firstName and lastName.
     */
    public static String getCrmsfaPartyName(GenericValue party) {
        if (party == null) return null;
        StringBuffer name = new StringBuffer();
        if (party.get("groupName") != null) name.append(party.get("groupName")).append(" ");
        if (party.get("firstName") != null) name.append(party.get("firstName")).append(" ");
        if (party.get("lastName") != null) name.append(party.get("lastName")).append(" ");
        name.append("(").append(party.get("partyId")).append(")");
        return name.toString();
    }

    /**
     * As above, but does a lookup on PartySummaryCRMView for an input partyId.
     */
    public static String getCrmsfaPartyName(GenericDelegator delegator, String partyId) throws GenericEntityException {
        GenericValue party = delegator.findByPrimaryKey("PartySummaryCRMView", UtilMisc.toMap("partyId", partyId));
        return getCrmsfaPartyName(party);
    }

    /**
     * Retreive the last deactivation date if the party is currently deactivated
     * @param partyId
     * @param delegator
     * @return the timestamp of last deactivation, null if the party is not deactivated
     * @throws GenericEntityNotFoundException
     */
    public static Timestamp getDeactivationDate(String partyId, GenericDelegator delegator) throws GenericEntityException {
        // check party current status:
        if ( isActive(partyId, delegator) ) {
            return null;
        }

        // party is currently deactivated, get the deactivation date
        try {

           List<GenericValue> deactivationDates = delegator.findByAnd("PartyDeactivation", UtilMisc.toMap("partyId", partyId), UtilMisc.toList("-deactivationTimestamp"));
           if (UtilValidate.isNotEmpty(deactivationDates)) {
               return (Timestamp)deactivationDates.get(0).get("deactivationTimestamp");
           } else {
               Debug.logWarning("The party ["+partyId+"] status is disabled but there is no registered deactivation date.", module);
           }

        } catch (GenericEntityException e) {
            Debug.logError(e, module);
        }
        return null;
    }

    /** Checks if the given party with role is assigned to the user login. */
    public static boolean isAssignedToUserLogin(String partyId, String roleTypeId, GenericValue userLogin) throws GenericEntityException {
    	GenericDelegator delegator = (GenericDelegator) (GenericDelegator) userLogin.getDelegator();
    	String roleTypeIdTo = getFirstValidTeamMemberRoleTypeId(userLogin.getString("partyId"), delegator);
    	if (roleTypeIdTo == null) return false;
    		List activeRelationships = EntityUtil.filterByDate(delegator.findByAnd("PartyRelationship", UtilMisc.toMap("partyIdFrom", partyId, "roleTypeIdFrom", roleTypeId,
    			"partyIdTo", userLogin.get("partyId"), "roleTypeIdTo", roleTypeIdTo,
    			"partyRelationshipTypeId","ASSIGNED_TO")));
    	return activeRelationships.size() > 0;
    }
    
    /**
     * Find the active ASSIGNED_TO party relationships with given From and To party IDs, and the role type ID of From party such as 'CONTACT'
     *
     * @param delegator a GenericDelegator instance
     * @param partyIdFrom a String object that represents the From party ID
     * @param roleTypeIdFrom a String object that represents the role type ID of From party
     * @param partyIdTo a String object that represents the To party ID
     * @return a List of GenericValue objects 
     */
	public static List findActiveAssignedToPartyRelationships(
		final GenericDelegator delegator,
		final String partyIdFrom,
		final String roleTypeIdFrom,
		final String partyIdTo)
		throws GenericEntityException {		
		EntityCondition conditions = EntityCondition.makeCondition(
			UtilMisc.toList(
					EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, partyIdFrom),
					EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, roleTypeIdFrom),
					EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, partyIdTo),
					EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "ASSIGNED_TO"),
				EntityUtil.getFilterByDateExpr()
			),
			EntityOperator.AND
		);
        return delegator.findList("PartyRelationship", conditions, null, null,null,false);
	}
	
	 public static boolean createNewPartyToRelationship(String partyIdTo, String partyIdFrom, String roleTypeIdFrom,
	            String partyRelationshipTypeId, String securityGroupId, List validToPartyRoles, Timestamp fromDate, 
	            boolean expireExistingRelationships, GenericValue userLogin, GenericDelegator delegator, LocalDispatcher dispatcher) 
	        throws GenericEntityException, GenericServiceException {
		 // get the first valid roleTypeIdTo from a list of possible roles for the partyIdTo 
	        // this will be the role we use as roleTypeIdTo in PartyRelationship.
	        String roleTypeIdTo = getFirstValidRoleTypeId(partyIdTo, validToPartyRoles, delegator);

	        // if no matching roles were found, then no relationship created
	        if (roleTypeIdTo == null) return false;

	        /* 
	         * if expireExistingRelationships is true, then find all existing PartyRelationships with partyIdFrom and partyRelationshipTypeId which 
	         * are not expired on the fromDate and then expire them
	         */
	        if (expireExistingRelationships == true) {
	            List partyRelationships = delegator.findByAnd("PartyRelationship", UtilMisc.toMap("partyIdFrom", partyIdFrom, "partyRelationshipTypeId", partyRelationshipTypeId));
	            expirePartyRelationships(partyRelationships, fromDate, dispatcher, userLogin);
	        }

	        // call createPartyRelationship service to create PartyRelationship using parameters and the role we just found
	        Map input = UtilMisc.toMap("partyIdTo", partyIdTo, "roleTypeIdTo", roleTypeIdTo, "partyIdFrom", partyIdFrom, "roleTypeIdFrom", roleTypeIdFrom);
	        input.put("partyRelationshipTypeId", partyRelationshipTypeId);
	        input.put("securityGroupId", securityGroupId);
	        input.put("fromDate", fromDate);
	        input.put("userLogin", userLogin);
	        Map serviceResult = dispatcher.runSync("createPartyRelationship", input);

	        // on success return true
	        return true;
	    }

	    
}
