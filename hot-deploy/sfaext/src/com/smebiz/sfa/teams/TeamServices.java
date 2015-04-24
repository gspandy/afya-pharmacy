package com.smebiz.sfa.teams;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.party.party.PartyHelper;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.smebiz.sfa.security.CrmsfaSecurity;
import com.smebiz.sfa.common.UtilMessage;
import com.smebiz.sfa.party.SfaPartyHelper;

public class TeamServices {

    public static final String module = TeamServices.class.getName();
    public static final String notificationResource = "notification";

    public static Map createTeam(DispatchContext dctx, Map context) {
        GenericDelegator delegator = (GenericDelegator) (GenericDelegator) dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Security security = dctx.getSecurity();
        Locale locale = (Locale) context.get("locale");
        GenericValue userLogin = (GenericValue) context.get("userLogin");

        String groupName = (String) context.get("groupName");
        String comments = (String) context.get("comments");

        // ensure team create permission
        /*if (!security.hasPermission("CRMSFA_TEAM_CREATE", userLogin)) {
            return UtilMessage.createAndLogServiceError("CrmErrorPermissionDenied", locale, module);
        }*/
        try {
            Map servRes = dispatcher.runSync("createPartyGroup", UtilMisc.toMap("groupName", groupName, "comments", comments, "userLogin", userLogin));
            if (ServiceUtil.isError(servRes)) return servRes;
            String partyId = (String) servRes.get("partyId");

            servRes = dispatcher.runSync("createPartyRole", UtilMisc.toMap("partyId", partyId, "roleTypeId", "ACCOUNT_TEAM", "userLogin", userLogin));
            if (ServiceUtil.isError(servRes)) return servRes;

            // we'll use the system user to add the userLogin to the team
            GenericValue system = delegator.findByPrimaryKeyCache("UserLogin", UtilMisc.toMap("userLoginId", "system"));
            servRes = dispatcher.runSync("sfaext.addTeamMember", UtilMisc.toMap("teamMemberPartyId", userLogin.get("partyId"), 
                        "accountTeamPartyId", partyId, "securityGroupId", "SALES_MANAGER", "userLogin", system));
            if (ServiceUtil.isError(servRes)) return servRes;

            Map result = ServiceUtil.returnSuccess();
            result.put("partyId", partyId);
            return result;
        } catch (GenericServiceException e) {
            return UtilMessage.createAndLogServiceError(e, "CrmErrorCreateTeamFail", locale, module);
        } catch (GenericEntityException e) {
            return UtilMessage.createAndLogServiceError(e, "CrmErrorCreateTeamFail", locale, module);
        }
    }

    public static Map updateTeam(DispatchContext dctx, Map context) {
        GenericDelegator delegator = (GenericDelegator) (GenericDelegator) dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Security security = dctx.getSecurity();
        Locale locale = (Locale) context.get("locale");
        GenericValue userLogin = (GenericValue) context.get("userLogin");

        String partyId = (String) context.get("partyId");
        String groupName = (String) context.get("groupName");
        String comments = (String) context.get("comments");

        // ensure team update permission
        if (!CrmsfaSecurity.hasPartyRelationSecurity(security, "CRMSFA_TEAM", "_UPDATE", userLogin, partyId)) {
            return UtilMessage.createAndLogServiceError("CrmErrorPermissionDenied", locale, module);
        }
        try {
            Map servRes = dispatcher.runSync("updatePartyGroup", UtilMisc.toMap("partyId", partyId, "groupName", groupName, "comments", comments, "userLogin", userLogin));
            if (ServiceUtil.isError(servRes)) return servRes;
            return ServiceUtil.returnSuccess();
        } catch (GenericServiceException e) {
            return UtilMessage.createAndLogServiceError(e, "CrmErrorCreateTeamFail", locale, module);
        }
    }

    public static Map deactivateTeam(DispatchContext dctx, Map context) {
        GenericDelegator delegator = (GenericDelegator) (GenericDelegator) dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Security security = dctx.getSecurity();
        Locale locale = (Locale) context.get("locale");
        GenericValue userLogin = (GenericValue) context.get("userLogin");

        String teamPartyId = (String) context.get("partyId");

        // ensure team deactivate permission on this team
        if (!CrmsfaSecurity.hasPartyRelationSecurity(security, "CRMSFA_TEAM", "_DEACTIVATE", userLogin, teamPartyId)) {
            return UtilMessage.createAndLogServiceError("CrmErrorPermissionDenied", locale, module);
        }
        try {
            // expire all active relationships for team
            List relationships = delegator.findByAnd("PartyRelationship", 
                    UtilMisc.toList(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, teamPartyId), EntityUtil.getFilterByDateExpr()));
            SfaPartyHelper.expirePartyRelationships(relationships, UtilDateTime.nowTimestamp(), dispatcher, userLogin);

            // set the team party to PARTY_DISABLED (note: if your version of ofbiz fails to do this, then replace this with direct entity ops)
            Map servRes = dispatcher.runSync("setPartyStatus", UtilMisc.toMap("partyId", teamPartyId, "statusId", "PARTY_DISABLED", "userLogin", userLogin));
            if (ServiceUtil.isError(servRes)) return servRes;

            return ServiceUtil.returnSuccess();
        } catch (GenericServiceException e) {
            return UtilMessage.createAndLogServiceError(e, "CrmErrorAssignFail", locale, module);
        } catch (GenericEntityException e) {
            return UtilMessage.createAndLogServiceError(e, "CrmErrorAssignFail", locale, module);
        }
    }

    public static Map assignTeamToAccount(DispatchContext dctx, Map context) {
        GenericDelegator delegator = (GenericDelegator) (GenericDelegator) dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Security security = dctx.getSecurity();
        Locale locale = (Locale) context.get("locale");
        GenericValue userLogin = (GenericValue) context.get("userLogin");

        String accountPartyId = (String) context.get("accountPartyId");
        String teamPartyId = (String) context.get("teamPartyId");

        // ensure team assign permission on this account
        if (!CrmsfaSecurity.hasPartyRelationSecurity(security, "CRMSFA_TEAM", "_ASSIGN", userLogin, accountPartyId)) {
            return UtilMessage.createAndLogServiceError("CrmErrorPermissionDenied", locale, module);
        }
        try {
            // assign the team
            SfaPartyHelper.copyToPartyRelationships(teamPartyId, "ACCOUNT_TEAM", accountPartyId, "ACCOUNT", userLogin, delegator, dispatcher);
        } catch (GenericServiceException e) {
            return UtilMessage.createAndLogServiceError(e, "CrmErrorAssignFail", locale, module);
        } catch (GenericEntityException e) {
            return UtilMessage.createAndLogServiceError(e, "CrmErrorAssignFail", locale, module);
        }
        return ServiceUtil.returnSuccess();
    }

    public static Map addTeamMember(DispatchContext dctx, Map context) {
        GenericDelegator delegator = (GenericDelegator) (GenericDelegator) dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Security security = dctx.getSecurity();
        Locale locale = (Locale) context.get("locale");
        GenericValue userLogin = (GenericValue) context.get("userLogin");

        String teamMemberPartyId = (String) context.get("teamMemberPartyId");
        String accountTeamPartyId = (String) context.get("accountTeamPartyId");
        String securityGroupId = (String) context.get("securityGroupId");

        // ensure team assign permission on this account or team
        if (!CrmsfaSecurity.hasPartyRelationSecurity(security, "CRMSFA_TEAM", "_ASSIGN", userLogin, accountTeamPartyId)) {
            return UtilMessage.createAndLogServiceError("CrmErrorPermissionDenied", locale, module);
        }
        try {
            // find out whether the accountTeamPartyId is an account or team
            String roleTypeIdFrom = SfaPartyHelper.getFirstValidRoleTypeId(accountTeamPartyId, UtilMisc.toList("ACCOUNT", "ACCOUNT_TEAM"), delegator);
            if (UtilValidate.isEmpty(roleTypeIdFrom)) {
                return UtilMessage.createAndLogServiceError("CrmErrorPartyNotAccountOrTeam", UtilMisc.toMap("partyId", accountTeamPartyId), locale, module);
            }

            // the the first valid role for the team member (which could be either ACCOUNT_MANAGER, ACCOUNT_REP, or CUST_SERVICE_REP)
            String roleTypeIdTo = SfaPartyHelper.getFirstValidRoleTypeId(teamMemberPartyId, UtilMisc.toList("ACCOUNT_MANAGER", "ACCOUNT_REP", "CUST_SERVICE_REP"), delegator);
            if (UtilValidate.isEmpty(roleTypeIdTo)) {
                return UtilMessage.createAndLogServiceError("CrmErrorPartyNotCrmUser", UtilMisc.toMap("partyId", teamMemberPartyId), locale, module);
            }

            // find out if the candidate is already a member in this role
            List relationships = delegator.findByAnd("PartyRelationship", UtilMisc.toMap("partyIdFrom", accountTeamPartyId, "partyRelationshipTypeId", "ASSIGNED_TO", 
                        "roleTypeIdFrom", roleTypeIdFrom, "partyIdTo", teamMemberPartyId, "roleTypeIdTo", roleTypeIdTo));
            List activeRelations = EntityUtil.filterByDate(relationships, UtilDateTime.nowTimestamp()); // filter out expired relationships
            if (activeRelations.size() > 0) {
                return UtilMessage.createAndLogServiceError("CrmErrorAlreadyMember", locale, module);
            }

            // Ensure the PartyRoles
            Map ensureResult = dispatcher.runSync("ensurePartyRole", UtilMisc.toMap("partyId", accountTeamPartyId, "roleTypeId", roleTypeIdFrom, "userLogin", userLogin));
            if (ServiceUtil.isError(ensureResult)) {
                return UtilMessage.createAndLogServiceError(ensureResult, module);
            }
            ensureResult = dispatcher.runSync("ensurePartyRole", UtilMisc.toMap("partyId", teamMemberPartyId, "roleTypeId", roleTypeIdTo, "userLogin", userLogin));
            if (ServiceUtil.isError(ensureResult)) {
                return UtilMessage.createAndLogServiceError(ensureResult, module);
            }
            
            // create the PartyRelationship
            Map input = UtilMisc.toMap("partyIdFrom", accountTeamPartyId,"roleTypeIdFrom", roleTypeIdFrom , "partyIdTo", teamMemberPartyId, "roleTypeIdTo", roleTypeIdTo);
            input.put("partyRelationshipTypeId", "ASSIGNED_TO");
            input.put("securityGroupId", securityGroupId);
            input.put("fromDate", UtilDateTime.nowTimestamp());
            input.put("userLogin", userLogin);
            Map serviceResults = dispatcher.runSync("createPartyRelationship", input);
            if (ServiceUtil.isError(serviceResults)) {
                return UtilMessage.createAndLogServiceError(serviceResults, "CrmErrorAssignFail", locale, module);
            }
        } catch (GenericServiceException e) {
            return UtilMessage.createAndLogServiceError(e, "CrmErrorAssignFail", locale, module);
        } catch (GenericEntityException e) {
            return UtilMessage.createAndLogServiceError(e, "CrmErrorAssignFail", locale, module);
        }
        return ServiceUtil.returnSuccess();
    }

    public static Map removeTeamMember(DispatchContext dctx, Map context) {
        GenericDelegator delegator = (GenericDelegator) (GenericDelegator) dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Security security = dctx.getSecurity();
        Locale locale = (Locale) context.get("locale");
        GenericValue userLogin = (GenericValue) context.get("userLogin");

        String teamMemberPartyId = (String) context.get("teamMemberPartyId");
        String accountTeamPartyId = (String) context.get("accountTeamPartyId");

        // ensure team remove permission on this account
        if (!CrmsfaSecurity.hasPartyRelationSecurity(security, "CRMSFA_TEAM", "_REMOVE", userLogin, accountTeamPartyId)) {
            return UtilMessage.createAndLogServiceError("CrmErrorPermissionDenied", locale, module);
        }
        try {
            // expire any active relationships between the team member and the account or team
            String roleTypeIdFrom = SfaPartyHelper.getFirstValidRoleTypeId(accountTeamPartyId, UtilMisc.toList("ACCOUNT", "ACCOUNT_TEAM"), delegator);
            List relationships = TeamHelper.findActiveAccountOrTeamRelationships(accountTeamPartyId, roleTypeIdFrom, teamMemberPartyId, delegator);
            SfaPartyHelper.expirePartyRelationships(relationships, UtilDateTime.nowTimestamp(), dispatcher, userLogin);
        } catch (GenericServiceException e) {
            return UtilMessage.createAndLogServiceError(e, "CrmErrorAssignFail", locale, module);
        } catch (GenericEntityException e) {
            return UtilMessage.createAndLogServiceError(e, "CrmErrorAssignFail", locale, module);
        }
        return ServiceUtil.returnSuccess();
    }

    public static Map setTeamMemberSecurityGroup(DispatchContext dctx, Map context) {
        GenericDelegator delegator = (GenericDelegator) (GenericDelegator) dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Security security = dctx.getSecurity();
        Locale locale = (Locale) context.get("locale");
        GenericValue userLogin = (GenericValue) context.get("userLogin");

        String teamMemberPartyId = (String) context.get("teamMemberPartyId");
        String accountTeamPartyId = (String) context.get("accountTeamPartyId");
        String securityGroupId = (String) context.get("securityGroupId");

        // ensure team update permission on this account
        if (!CrmsfaSecurity.hasPartyRelationSecurity(security, "CRMSFA_TEAM", "_UPDATE", userLogin, accountTeamPartyId)) {
            return UtilMessage.createAndLogServiceError("CrmErrorPermissionDenied", locale, module);
        }
        try {
            // expire any active relationships between the team member and the account or team
            String roleTypeIdFrom = SfaPartyHelper.getFirstValidRoleTypeId(accountTeamPartyId, UtilMisc.toList("ACCOUNT", "ACCOUNT_TEAM"), delegator);
            List relationships = TeamHelper.findActiveAccountOrTeamRelationships(accountTeamPartyId, roleTypeIdFrom, teamMemberPartyId, delegator);
            SfaPartyHelper.expirePartyRelationships(relationships, UtilDateTime.nowTimestamp(), dispatcher, userLogin);

            // recreate the relationships with the new security group
            for (Iterator iter = relationships.iterator(); iter.hasNext(); ) {
                GenericValue relationship = (GenericValue) iter.next();

                Map input = UtilMisc.toMap("partyIdFrom", accountTeamPartyId, "roleTypeIdFrom", roleTypeIdFrom, "partyIdTo", teamMemberPartyId, "roleTypeIdTo", relationship.getString("roleTypeIdTo"));
                input.put("partyRelationshipTypeId", "ASSIGNED_TO");
                input.put("securityGroupId", securityGroupId);
                input.put("fromDate", UtilDateTime.nowTimestamp());
                input.put("userLogin", userLogin);
                Map serviceResults = dispatcher.runSync("createPartyRelationship", input);
                if (ServiceUtil.isError(serviceResults)) {
                    return UtilMessage.createAndLogServiceError(serviceResults, "CrmErrorAssignFail", locale, module);
                }
            }
        } catch (GenericServiceException e) {
            return UtilMessage.createAndLogServiceError(e, "CrmErrorAssignFail", locale, module);
        } catch (GenericEntityException e) {
            return UtilMessage.createAndLogServiceError(e, "CrmErrorAssignFail", locale, module);
        }
        return ServiceUtil.returnSuccess();
    }

    /**
     *  Calls sfaext.sendAccountTeamMemberNotificationEmails service for each team member 
     */
    public static Map sendTeamAssignmentNotificationEmails(DispatchContext dctx, Map context) {
        GenericDelegator delegator = (GenericDelegator) (GenericDelegator) dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        String accountPartyId = (String) context.get("accountPartyId");
        String teamPartyId = (String) context.get("teamPartyId");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");

        try {

            String accountPartyName = org.ofbiz.party.party.PartyHelper.getPartyName(delegator, accountPartyId, false);
            String teamPartyName = org.ofbiz.party.party.PartyHelper.getPartyName(delegator, teamPartyId, false);

            // Get all team members for account
            List teamMemberRelations = delegator.findByAnd("PartyToSummaryByRelationship", UtilMisc.toMap("partyIdFrom", accountPartyId, "roleTypeIdFrom", "ACCOUNT", "partyRelationshipTypeId", "ASSIGNED_TO"));
            teamMemberRelations = EntityUtil.filterByDate(teamMemberRelations);
            List teamMembers = EntityUtil.getFieldListFromEntityList(teamMemberRelations, "partyIdTo", true);

            // Assemble a list of team member names names added to the account
            // todo: move this to a bsh script? Or is speed more important?
            List teamMemberNames = new ArrayList();
            Iterator tmnit = teamMembers.iterator();
            while (tmnit.hasNext()) {
                String teamMemberPartyId = (String) tmnit.next();
                String teamMemberPartyName = org.ofbiz.party.party.PartyHelper.getPartyName(delegator, teamMemberPartyId, false);
                if (! UtilValidate.isEmpty(teamMemberPartyName)) {
                    teamMemberNames.add(teamMemberPartyName);
                }
            }
            Collections.sort(teamMemberNames);

            // Include the current responsible party for the account
            GenericValue responsibleParty = SfaPartyHelper.getCurrentResponsibleParty(accountPartyId, "ACCOUNT", delegator);
            if (responsibleParty != null && responsibleParty.getString("partyId") != null && teamMemberNames.contains(responsibleParty.getString("partyId"))) {
                teamMembers.add(responsibleParty.getString("partyId"));
            }
            
            if (UtilValidate.isEmpty(teamMembers)) {
                return ServiceUtil.returnSuccess();
            }

            Map messageMap = UtilMisc.toMap("teamMemberNames", teamMemberNames, "accountPartyId", accountPartyId, "accountPartyName", accountPartyName, "teamPartyId", teamPartyId, "teamPartyName", teamPartyName);
            String url = UtilProperties.getMessage(notificationResource, "sfaext.url.account.assignTeam", messageMap, locale);
            messageMap.put("url", url);
            String subject = UtilProperties.getMessage(notificationResource, "subject.account.assignTeam", messageMap, locale);
            
            Map bodyParameters = UtilMisc.toMap("eventType", "account.assignTeam");
            bodyParameters.putAll(messageMap);

            Iterator tmit = teamMembers.iterator();
            while (tmit.hasNext()) {
                String teamMemberPartyId = (String) tmit.next();
                Map sendEmailsResult = dispatcher.runSync("sfaext.sendCrmNotificationEmails", UtilMisc.toMap("notifyPartyIds", UtilMisc.toList(teamMemberPartyId), "eventType", "account.assignTeam", "subject", subject, "bodyParameters", bodyParameters, "userLogin", userLogin));
                if (ServiceUtil.isError(sendEmailsResult)) {
                    return sendEmailsResult; 
                }
            }
        } catch (GenericEntityException ex) {
            return UtilMessage.createAndLogServiceError(ex, locale, module);
        } catch (GenericServiceException ex) {
            return UtilMessage.createAndLogServiceError(ex, locale, module);
        }
        return ServiceUtil.returnSuccess();
    }
}
