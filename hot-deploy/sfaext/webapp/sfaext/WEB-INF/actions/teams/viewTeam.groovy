import org.ofbiz.base.util.*;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import com.smebiz.sfa.security.CrmsfaSecurity;
import com.smebiz.sfa.party.SfaPartyHelper;
import com.smebiz.sfa.teams.TeamHelper;

partyId = parameters.get("partyId");

// make sure that the partyId is actually an ACCOUNT_TEAM before trying to display it as once
delegator = request.getAttribute("delegator");
validRoleTypeId = SfaPartyHelper.getFirstValidRoleTypeId(partyId, UtilMisc.toList("ACCOUNT_TEAM"), delegator);

// if not, return right away (otherwise we get spaghetti code)
if ((validRoleTypeId == null) || (!validRoleTypeId.equals("ACCOUNT_TEAM")))  {
    context.put("validView", false);
    return;
}

// get the team
team = delegator.findByPrimaryKey("PartyAndGroup", UtilMisc.toMap("partyId", partyId));
context.put("team", team);

// check if deactivated
if ("PARTY_DISABLED".equals(team.get("statusId"))) {
    context.put("validView", true);
    context.put("teamDeactivated", true);
    return;
}

// get the team members
List<GenericValue> teamMembers = TeamHelper.getActiveTeamMembers(partyId, delegator);
Map<String, Object> teamMemberMap = new HashMap<String, Object>();
if (UtilValidate.isNotEmpty(teamMembers)) {
    for (GenericValue teamMember : teamMembers) {
        val = EntityUtil.getFirst(TeamHelper.findActiveAccountOrTeamRelationships(partyId, "ACCOUNT_TEAM", teamMember.getString("partyId"), delegator)); 
        if (UtilValidate.isNotEmpty(val)) 
            teamMember.put("securityGroupId", val.get("securityGroupId")); 
    }
}
context.put("accountTeamMembers", teamMembers);

// permission to change team member roles
hasTeamUpdatePermission = false; // this needs to be set so that a form-widget can test it with "use-when"
if (CrmsfaSecurity.hasPartyRelationSecurity(request.getAttribute("security"), "CRMSFA_TEAM", "_UPDATE", request.getAttribute("userLogin"), partyId)) {
    hasTeamUpdatePermission = true;
}
context.put("hasTeamUpdatePermission", hasTeamUpdatePermission);

// permission to remove team members
if (CrmsfaSecurity.hasPartyRelationSecurity(request.getAttribute("security"), "CRMSFA_TEAM", "_REMOVE", request.getAttribute("userLogin"), partyId)) {
    context.put("hasTeamRemovePermission", true);
}

// permission to assign team members
if (CrmsfaSecurity.hasPartyRelationSecurity(request.getAttribute("security"), "CRMSFA_TEAM", "_ASSIGN", request.getAttribute("userLogin"), partyId)) {
    context.put("hasTeamAssignPermission", true);
}

// permission to deactivate the team
if (CrmsfaSecurity.hasPartyRelationSecurity(request.getAttribute("security"), "CRMSFA_TEAM", "_DEACTIVATE", request.getAttribute("userLogin"), partyId)) {
    context.put("hasTeamDeactivatePermission", true);
}
