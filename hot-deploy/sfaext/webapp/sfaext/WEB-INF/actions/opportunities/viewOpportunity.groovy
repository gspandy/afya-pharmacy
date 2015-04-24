
import java.util.ArrayList;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.util.EntityUtil;


import com.smebiz.sfa.opportunities.UtilOpportunity;
import com.smebiz.sfa.security.CrmsfaSecurity;
/*
 * Finds all information relevant to opportunities. Also checks that user has
 * permission to view opportunity based on lead or account
 */

salesOpportunityId = parameters.get("salesOpportunityId");

// opportunities may have a primary account or lead (this triggers the list of contacts and add contact, which we don't want if we're a lead opportunity)
context.put("isAccountOpportunity", false);

// get the opportunity and accountPartyId/leadPartyId all into one map
opportunity = delegator.findByPrimaryKey("SalesOpportunity", UtilMisc.toMap("salesOpportunityId", salesOpportunityId));

if (opportunity == null) return;

opportunityAndParty = opportunity.getAllFields();
//put to history
//context{"history"} = UtilCommon.makeHistoryEntry(opportunity.get("opportunityName"), "viewOpportunity", UtilMisc.toList("salesOpportunityId"));

accountPartyId = UtilOpportunity.getOpportunityAccountPartyId(opportunity);
if (accountPartyId != null) {
    opportunityAndParty.put("accountPartyId", accountPartyId);
    context.put("isAccountOpportunity", true);
}

String leadPartyId = UtilOpportunity.getOpportunityLeadPartyId(opportunity);
if (leadPartyId != null) {
	opportunityAndParty.put("leadPartyId", leadPartyId);
}



// now check if user has view permission
context.put("allowed", true);
security = request.getAttribute("security");

if (accountPartyId != null && !CrmsfaSecurity.hasPartyRelationSecurity(security, "CRMSFA_OPP", "_VIEW", userLogin, accountPartyId)) {
    context.put("allowed", false);
    context.put("opportunityAndParty", null);
    return;
} else if (leadPartyId != null && !CrmsfaSecurity.hasPartyRelationSecurity(security, "CRMSFA_OPP", "_VIEW", userLogin, leadPartyId)) {
    context.put("allowed", false);
    context.put("opportunityAndParty", null);
    return;
}

opportunityAndParty.put("accountPartyId",accountPartyId);
opportunityAndParty.put("leadPartyId",leadPartyId);

// add the opportunity to the context
context.put("opportunityAndParty", opportunityAndParty);

// opportunity history, which is just the list of all opportunities with this ID. 
history = delegator.findByAnd("SalesOpportunityHistory", UtilMisc.toMap("salesOpportunityId", salesOpportunityId), UtilMisc.toList("modifiedTimestamp DESC"));
context.put("opportunityHistory", history);

// is the oportunity open (stage != CLOSED)?
opportunityOpen = ("SOSTG_CLOSED".equals(opportunity.getString("opportunityStageId")) ? false : true);
context.put("opportunityOpen", opportunityOpen);

// now set data only available when we have an opportunity
if (opportunityOpen) {

    // permission to update opportunity information (includes add/remove contact)
    if (CrmsfaSecurity.hasOpportunityPermission(security, "_UPDATE", userLogin, salesOpportunityId)) {
        context.put("hasUpdatePermission", true);

        // this also means activiites may be created, but only if user has CRMSFA_ACT_CREATE
        if (security.hasEntityPermission("CRMSFA_ACT", "_CREATE", userLogin)) {
            context.put("hasNewActivityPermission", true);
        }
    }

    // opportunity contacts (a list of contact partyId's) but only if this is not a PROSPECT (lead) opportunity
    if (leadPartyId == null) {
        contacts = delegator.findByAnd("SalesOpportunityRole", UtilMisc.toMap("salesOpportunityId", salesOpportunityId, "roleTypeId", "CONTACT"));
        context.put("contacts", contacts);
    }

    // opportunity quotes
    quotes = new ArrayList();
    relations = opportunity.getRelated("SalesOpportunityQuote");
    for (iter = relations.iterator(); iter.hasNext(); ) {
        quotes.addAll(iter.next().getRelated("Quote"));
    }
    context.put("quoteList", quotes);
}

// whether opportunity is closed or open, should still be able to view its activities
context.put("validView", true); 

// and also view any attached content
//context.put("content", ContentHelper.getContentInfoForOpportunity(salesOpportunityId, delegator));
