import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.party.contact.*;
import com.smebiz.sfa.party.SfaPartyHelper;
import com.smebiz.sfa.security.CrmsfaSecurity;
/* finds all the information relevant to this lead and puts them in the context, so the various forms
and FTLs of this screen can display them correctly */

partyId = parameters.get("partyId");

//make sure that the partyId is actually a PROSPECT (i.e., a lead) before trying to display it as once
delegator = request.getAttribute("delegator");
validRoleTypeId = SfaPartyHelper.getFirstValidRoleTypeId(partyId, UtilMisc.toList("PROSPECT"), delegator);
if ((validRoleTypeId == null) || !validRoleTypeId.equals("PROSPECT")) {
    context.put("validView", false);
    return;
}

// now get all data necessary for this page
dispatcher = request.getAttribute("dispatcher");

// lead summary data
partySummary = delegator.findByPrimaryKey("PartySummaryCRMView", UtilMisc.toMap("partyId", partyId));
if (partySummary == null) {
    context.put("validView", false);
    return;
}
context.put("partySummary", partySummary);

//if the lead has already been converted, then just put this in there and nothing
if (partySummary.get("statusId") != null && partySummary.getString("statusId").equals("PTYLEAD_CONVERTED")) {
    context.put("hasBeenConverted", true);
    context.put("validView", true);
    return;
} 

// who is currently responsible for lead
responsibleParty = SfaPartyHelper.getCurrentResponsibleParty(partyId, "PROSPECT", delegator);
context.put("responsibleParty", responsibleParty);

// lead data sources
List sources = delegator.findByAnd("PartyDataSource", UtilMisc.toMap("partyId", partyId), UtilMisc.toList("fromDate DESC"));
context.put("dataSources", sources);
StringBuffer dataSourcesAsString = new StringBuffer();
for (GenericValue ds : sources) {
    GenericValue dataSource = ds.getRelatedOne("DataSource");
    if (dataSource != null) {
        dataSourcesAsString.append(dataSource.get("description", locale));
        dataSourcesAsString.append(", ");
    }
}
if (dataSourcesAsString != null && dataSourcesAsString.length() > 2) 
    context.put("dataSourcesAsString", dataSourcesAsString.toString().substring(0, dataSourcesAsString.length()-2));

// lead marketing campaigns
campaignRoles = delegator.findByAnd("MarketingCampaignRole", UtilMisc.toMap("partyId", partyId, "roleTypeId", "PROSPECT"));
campaigns = EntityUtil.getRelated("MarketingCampaign", campaignRoles);
context.put("marketingCampaigns", campaigns);
if ((campaignsList = EntityUtil.getFieldListFromEntityList(campaigns, "campaignName", false)) != null) {
    marketingCampaignsAsString = StringUtil.join(campaignsList, ", ");
    if (marketingCampaignsAsString != null && marketingCampaignsAsString.length() > 2)
        context.put("marketingCampaignsAsString", marketingCampaignsAsString);
}

// whether lead is qualified or not
context.put("isQualified", false);
if ("PTYLEAD_QUALIFIED".equals(partySummary.getString("statusId"))) {
    context.put("isQualified", true);

    // lead opportunities (only if qualified)
    // TODO: this may have to be enhanced to exclude some types of opportunities (ie, closed ones)?
    /*opportunitiesOrderBy = parameters.get("opportunitiesOrderBy");
    if (opportunitiesOrderBy == null) opportunitiesOrderBy = "estimatedCloseDate";
    findParams = UtilMisc.toMap("entityName", "SalesOpportunityAndRole",
            "inputFields", UtilMisc.toMap("partyId", partyId, "roleTypeId", "PROSPECT"),
            "orderBy", opportunitiesOrderBy);
    results = dispatcher.runSync("performFind", findParams);
    context.put("opportunitiesListIt", results.get("listIt"));
    */
    // permission to create opportunities for lead
    if (CrmsfaSecurity.hasPartyRelationSecurity(request.getAttribute("security"), "CRMSFA_OPP", "_CREATE", request.getAttribute("userLogin"), partyId)) {
        context.put("hasCreateOppPermission", true);
    }
}

// lead notes
results = delegator.findByAnd("PartyNoteView", UtilMisc.toMap("targetPartyId", partyId), UtilMisc.toList("noteDateTime DESC"));
context.put("notesList", results);

// so we can view activities, etc.
context.put("validView", true);
//permission to update password
//permission to update lead information, for generic view profile screen
if (CrmsfaSecurity.hasPartyRelationSecurity(request.getAttribute("security"), "CRMSFA_LEAD", "_UPDATE", request.getAttribute("userLogin"), partyId)) {
     context.put("hasUpdatePermission", true);

     // this also means activiites may be created, but only if user has CRMSFA_ACT_CREATE
     if (security.hasEntityPermission("CRMSFA_ACT", "_CREATE", userLogin)) {
         context.put("hasNewActivityPermission", true);
     }
}

// permission to update password
if (CrmsfaSecurity.hasPartyRelationSecurity(request.getAttribute("security"), "CRMSFA_PASS", "_UPDATE", request.getAttribute("userLogin"), partyId)) {
    context.put("hasPassPermission", true);
}

// permission to delete
if (CrmsfaSecurity.hasPartyRelationSecurity(request.getAttribute("security"), "CRMSFA_LEAD", "_DELETE", request.getAttribute("userLogin"), partyId)) {
    context.put("hasDeletePermission", true);
}

// permission to reassign leads
if (CrmsfaSecurity.hasPartyRelationSecurity(request.getAttribute("security"), "CRMSFA_LEAD", "_REASSIGN", request.getAttribute("userLogin"), partyId)) {
    context.put("hasReassignPermission", true);
}


    
// Provide current PartyClassificationGroups as a list and a string
 partyContactMechValueMaps = ContactMechWorker.getPartyContactMechValueMaps(delegator, partyId, false);
    
 //Collections.sort(partyContactMechValueMaps, new PartyContactMechValueMapsSorter());
List userLogins = delegator.findByAnd("UserLogin", UtilMisc.toMap("partyId", partyId));
context.put("contactMeches", partyContactMechValueMaps);
context.put("userLogins", userLogins);
