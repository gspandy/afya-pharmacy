package com.smebiz.sfa.security;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.security.Security;

import com.smebiz.sfa.opportunities.UtilOpportunity;
import com.smebiz.sfa.party.SfaPartyHelper;


public class CrmsfaSecurity {

	public static final String module = CrmsfaSecurity.class.getName();

	/**
	 * This method supplements the standard OFBIZ security model with a security
	 * check specified in PartyRelationship. It first does the standard OFBIZ
	 * security checks, then sees if an unexpired PartyRelationship exists where
	 * partyIdFrom=partyIdFor, partyIdTo=UserLogin.partyId, and whose
	 * securityGroupId contains the security permission of module+"_MANAGER" or
	 * module+"_OPERATION". If not, it will check one more time on whether, for
	 * any partyIdFrom for which a security permission does exist, there exists
	 * a current (unexpired) PartyRelationship where partyIdFrom=partyIdFor,
	 * partyIdTo={partyId for which the required permission exists.} If any of
	 * these are true, then the permission is true. Otherwise, or if any entity
	 * operation errors occurred, false is returned.
	 * 
	 * @param security
	 *            - Security object
	 * @param securityModule
	 *            - The module to check (e.g., "CRMSFA_ACCOUNT", "PARTYMGR")
	 * @param securityOperation
	 *            - What operation is being checked (e.g., "_VIEW", "_CREATE",
	 *            "_UPDATE")
	 * @param userLogin
	 *            - The userLogin to check permission for
	 * @param partyIdFor
	 *            - What Account or Party the userLogin has permission to
	 *            perform the operation on
	 */
	public static boolean hasPartyRelationSecurity(Security security,
			String securityModule, String securityOperation,
			GenericValue userLogin, String partyIdFor) {

		if ((userLogin == null) || ((GenericDelegator) userLogin.getDelegator() == null)) {
			Debug.logError("userLogin is null or has no associated delegator",
					module);
			return false;
		}

		// check ${securityModule}_MANAGER permission
		if (security.hasEntityPermission(securityModule, "_MANAGER", userLogin)) {
			return true;
		}
		// check ${securityModule}_${securityOperation} permission
		if (security.hasEntityPermission(securityModule, securityOperation,
				userLogin)) {
			return true;
		}
		// TODO: #3 and #4 in
		// http://jira.undersunconsulting.com/browse/OFBIZ-638
		
		try {
			// now we'll need to do some searching so we should get a delegator
			// from user login
			GenericDelegator delegator = (GenericDelegator) (GenericDelegator) userLogin.getDelegator();

			// validate that partyIdFor is in our system in a proper role
			String roleTypeIdFor = SfaPartyHelper.getFirstValidRoleTypeId(partyIdFor,
							SfaPartyHelper.CLIENT_PARTY_ROLES, delegator);
			if (roleTypeIdFor == null) {
				Debug
						.logError(
								"Failed to check permission for partyId ["
										+ partyIdFor
										+ "] because that party does not have a valid role. I.e., it is not an Account, Contact, Lead, etc.",
								module);
				return false;
			}

			// Now get a list of all the parties for whom the userLogin's
			// partyId has the required securityModule+"_MANAGER" or
			// securityModule+securityOperation permission
			// due to a grant by PartyRelationship.securityGroupId
			EntityCondition filterByDateCondition = EntityUtil
					.getFilterByDateExpr();
			EntityConditionList operationConditon = EntityCondition.makeCondition(
					UtilMisc.toList(
							EntityCondition.makeCondition("permissionId",
									EntityOperator.EQUALS, securityModule
											+ "_MANAGER"), EntityCondition.makeCondition(
									"permissionId", EntityOperator.EQUALS,
									securityModule + securityOperation)),
					EntityOperator.OR);
			EntityConditionList searchConditions = EntityCondition.makeCondition(
					UtilMisc.toList(EntityCondition.makeCondition("partyIdTo",
							EntityOperator.EQUALS, userLogin
									.getString("partyId")), operationConditon,
							filterByDateCondition), EntityOperator.AND);
			List permittedRelationships = delegator.findList("PartyRelationshipAndPermission", searchConditions, null,
					null,null,false);

			// do any of these explicitly state a permission for partyIdFor? If
			// so, then we're done
			List directPermittedRelationships = EntityUtil.filterByAnd(
					permittedRelationships, UtilMisc.toMap("partyIdFrom",
							partyIdFor));
			if ((directPermittedRelationships != null)
					&& (directPermittedRelationships.size() > 0)) {
				if (Debug.verboseOn()) {
					Debug.logVerbose(userLogin
							+ " has direct permitted relationship for "
							+ partyIdFor, module);
				}
				return true;
			}

			// if not, then there is one more thing to check: for all the
			// permitted relationships, were there any which are in turn related
			// to the partyIdFor through another current (non-expired)
			// PartyRelationship? Note that here we had to break with convention
			// because
			// of the way PartyRelationship for CONTACT is written (ie,
			// CONTACT_REL_INV is opposite of ASSIGNED_TO, etc. See comments in
			// CRMSFADemoData.xml
			for (Iterator pRi = permittedRelationships.iterator(); pRi
					.hasNext();) {
				GenericValue permittedRelationship = (GenericValue) pRi.next();
				EntityConditionList indirectConditions = EntityCondition.makeCondition(
						UtilMisc.toList(EntityCondition.makeCondition("partyIdFrom",
								EntityOperator.EQUALS, partyIdFor),
								EntityCondition.makeCondition("partyIdTo",
										EntityOperator.EQUALS,
										permittedRelationship
												.getString("partyIdFrom")),
								filterByDateCondition), EntityOperator.AND);
				List indirectPermittedRelationships = delegator
						.findList("PartyRelationship",
								indirectConditions, null, null,null,false);
				if ((indirectPermittedRelationships != null)
						&& (indirectPermittedRelationships.size() > 0)) {
					if (Debug.verboseOn()) {
						Debug.logVerbose(userLogin
								+ " has indirect permitted relationship for "
								+ partyIdFor, module);
					}
					return true;
				}
			}

		} catch (GenericEntityException ex) {
			Debug.logError(
					"Unable to determine security from party relationship due to error "
							+ ex.getMessage(), module);
			return false;
		}

		Debug.logWarning("Checked UserLogin ["
				+ userLogin.getString("userLoginId")
				+ "] for permission to perform [" + securityModule + "] + ["
				+ securityOperation + "] on partyId = [" + partyIdFor
				+ "], but permission was denied", module);
		return false;
	}

	

    

    /**
     * Get the security module relevant to the given roleTypeId.
     */
    public static String getSecurityModuleForRole(String roleTypeId) {
        if ("ACCOUNT".equals(roleTypeId)) return "CRMSFA_ACCOUNT";
        if ("CONTACT".equals(roleTypeId)) return "CRMSFA_CONTACT";
        if ("PROSPECT".equals(roleTypeId)) return "CRMSFA_LEAD";
        if ("PARTNER".equals(roleTypeId)) return "CRMSFA_PARTNER";
        Debug.logInfo("No security module (CRMSFA_${role}) found for party role [" + roleTypeId + "].  Some operations might not be allowed.", module);
        return null;
    }

    /* @param securityScopeOperation - security scope operation : SECURITY_SCOPE_VIEW, SECURITY_SCOPE_UPDATE, SECURITY_SCOPE_CREATE
     * @return boolean
     */    
    public static boolean hasSecurityScopePermission(Security security, GenericValue userLogin, String workEffortId, boolean isUpdateScope) {
        GenericDelegator delegator = (GenericDelegator) (GenericDelegator) userLogin.getDelegator();
        
        // check for activity admin (super user) permission
        if (security.hasEntityPermission("CRMSFA", "_ACT_ADMIN", userLogin)) { 
            return true;            
        }        
        
        if (!isUpdateScope) {        
	        try {
	            boolean isAssignee = hasActivityRelation(userLogin, workEffortId, false); 
	            return isAssignee;
	        } catch (GenericEntityException e) {
	            Debug.logError(e, "Checked UserLogin [" + userLogin + "] for permission to perform on workEffortId = [" + workEffortId + "], but permission was denied due to an exception: " + e.getMessage(), module);
	            return false;
	        }            
        }        
        
        if (isUpdateScope) {        
	        try {
	            boolean isOwner = hasActivityRelation(userLogin, workEffortId, true); 
	            return isOwner;
	        } catch (GenericEntityException e) {
	            Debug.logError(e, "Checked UserLogin [" + userLogin + "] for permission to perform on workEffortId = [" + workEffortId + "], but permission was denied due to an exception: " + e.getMessage(), module);
	            return false;
	        }            
        }
        
        // all tests passed, grant security scope permission
        return true;
    }
    
    public static boolean hasActivityUpdatePartiesPermission(Security security, GenericValue userLogin, String workEffortId, boolean checkForOwner) throws GenericEntityException {
        // check for activity admin (super user) permission
        if (security.hasEntityPermission("CRMSFA", "_ACT_ADMIN", userLogin)) { 
            return true;            
        }        
        
        // if user does not have CRMSFA_ACT_ADMIN permission check if he is assignee for this activity
        try {
            if (!hasActivityRelation(userLogin, workEffortId, false)) {
            	return false;
            }
        } catch (GenericEntityException e) {
            Debug.logError(e, "Checked UserLogin [" + userLogin + "] for permission to update party on workEffortId = [" + workEffortId + "], but permission was denied due to an exception: " + e.getMessage(), module);
            return false;
        }            
        
        // passed all update parties permission tests, grant hasActivityUpdateParties permission
        return true;
    }    
    
    private static boolean hasActivityRelation(GenericValue userLogin, String workEffortId, boolean checkForOwner) throws GenericEntityException {
        GenericDelegator delegator = (GenericDelegator) (GenericDelegator) userLogin.getDelegator();
        String partyId = (String)userLogin.get("partyId");
        
        // check if user is owner (checkForOwner == true) or just for assignee (checkForOwner == false)
        ArrayList conditionList = new ArrayList();
        ArrayList roleExprs = new ArrayList();
        roleExprs.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "CAL_OWNER"));
        if (!checkForOwner) {
            roleExprs.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "CAL_ATTENDEE"));
            roleExprs.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "CAL_DELEGATE"));    
            roleExprs.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "CAL_ORGANIZER"));
        }
        EntityConditionList roleCond = EntityCondition.makeCondition(roleExprs, EntityOperator.OR);
        conditionList.add(roleCond);    
        
        // partyId and workEffortId primary keys condition
        ArrayList pkExprs = new ArrayList();
        pkExprs.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
        pkExprs.add(EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS, workEffortId));    
        EntityConditionList pkCond = EntityCondition.makeCondition(pkExprs, EntityOperator.AND);
        conditionList.add(pkCond);
        conditionList.add(EntityUtil.getFilterByDateExpr());
                
        EntityConditionList mainCond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);      
        
        List userAssignments = delegator.findList("WorkEffortPartyAssignment", mainCond, null, null,null,false);
        if (userAssignments.size() == 0) {
            return false;
        }
        return true;
    }

    /**
     * Get the security module relevant to the role of the given internal partyId.
     * @return The module as a string, such as "CRMSFA_ACCOUNT" for ACCOUNT partyIds or null if the role type is not found
     */
    public static String getSecurityModuleOfInternalParty(String partyId, GenericDelegator delegator) throws GenericEntityException {
        String roleTypeId = SfaPartyHelper.getFirstValidInternalPartyRoleTypeId(partyId, delegator);
        return getSecurityModuleForRole(roleTypeId);
    }

    
    
    /**
     * Checks if a userLogin has permission to perform an operation on an opportunity.
     * The userLogin must pass CRMSFA_OPP_${securityOperation} for all associated accounts and contacts.
     */
    public static boolean hasOpportunityPermission(Security security, String securityOperation, GenericValue userLogin, String salesOpportunityId) {

        GenericDelegator delegator = (GenericDelegator) (GenericDelegator) userLogin.getDelegator();
        try {
            // check for existance first
            GenericValue opportunity = delegator.findByPrimaryKeyCache("SalesOpportunity", UtilMisc.toMap("salesOpportunityId", salesOpportunityId));
            if (opportunity == null) {
                return false;
            }
            // check for closed opportunities for actions that are not _VIEW
            if (!"_VIEW".equals(securityOperation) && "SOSTG_CLOSED".equals(opportunity.getString("opportunityStageId"))) {
                return false;
            }

            // check that userLogin can perform this operation on all associated accounts (orthogonal to leads)
            List accounts = UtilOpportunity.getOpportunityAccountPartyIds(delegator, salesOpportunityId);
            for (Iterator iter = accounts.iterator(); iter.hasNext(); ) {
                if (!hasPartyRelationSecurity(security, "CRMSFA_OPP", securityOperation, userLogin, (String) iter.next())) {
                    return false;
                }
            }

            // check that userLogin can perform this operation on all associated leads (orthogonal to accounts)
            List leads = UtilOpportunity.getOpportunityLeadPartyIds(delegator, salesOpportunityId);
            for (Iterator iter = leads.iterator(); iter.hasNext(); ) {
                if (!hasPartyRelationSecurity(security, "CRMSFA_OPP", securityOperation, userLogin, (String) iter.next())) {
                    return false;
                }
            }

            // check that userLogin can perform this operation on all associated contacts
            List contacts = UtilOpportunity.getOpportunityContactPartyIds(delegator, salesOpportunityId);
            for (Iterator iter = contacts.iterator(); iter.hasNext(); ) {
                if (!hasPartyRelationSecurity(security, "CRMSFA_OPP", securityOperation, userLogin, (String) iter.next())) {
                    return false;
                }
            }
        } catch (GenericEntityException e) {
            Debug.logError(e, "Checked UserLogin [" + userLogin + "] for permission to perform [CRMSFA_OPP] + [" + securityOperation + "] on salesOpportunityId = [" + salesOpportunityId + "], but permission was denied due to exception: " + e.getMessage(), module);
            return false;
        }

        // everything was passed
        return true;
    }

}
