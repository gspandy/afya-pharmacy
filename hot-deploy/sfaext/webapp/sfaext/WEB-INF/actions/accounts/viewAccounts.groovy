import org.ofbiz.base.util.*;
import java.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.order.shoppingcart.ShoppingCart;

import com.smebiz.sfa.security.CrmsfaSecurity;
import com.smebiz.sfa.party.SfaPartyHelper;
import com.smebiz.sfa.content.ContentHelper;

partyId = parameters.get("partyId");

// make sure that the partyId is actually an ACCOUNT before trying to display it as once
delegator = request.getAttribute("delegator");
validRoleTypeId = SfaPartyHelper.getFirstValidRoleTypeId(partyId, UtilMisc.toList("ACCOUNT"), delegator);

// if not, return right away (otherwise we get spaghetti code)
if ((validRoleTypeId == null) || (!validRoleTypeId.equals("ACCOUNT")))  {
    context.put("validView", false);
    return;
}

/* finds all the information relevant to this account and puts them in the context, so the various forms
   and FTLs of this screen can display them correctly */

// is the account still active?
accountActive = SfaPartyHelper.isActive(partyId, delegator);

if (!accountActive) { 
	// can still view history of deactivated contacts
	accountDeactivationDate = SfaPartyHelper.getDeactivationDate(partyId, delegator);
    context.put("accountDeactivated", true);
    context.put("accountDeactivatedDate", accountDeactivationDate);
    context.put("validView", true);
}

dispatcher = request.getAttribute("dispatcher");

// account summary data
partySummary = delegator.findByPrimaryKey("PartySummaryCRMView", UtilMisc.toMap("partyId", partyId));
context.put("partySummary", partySummary);

//put to history
//context{"history"} = UtilCommon.makeHistoryEntry(partySummary.get("groupName"), "viewAccount", UtilMisc.toList("partyId"));

// gather data that should only be available for active accounts
if (accountActive) {

    // who is currently responsible for account
    responsibleParty = SfaPartyHelper.getCurrentResponsibleParty(partyId, "ACCOUNT", delegator);
    context.put("responsibleParty", responsibleParty);

    // account contacts TODO: this order by isn't used yet, maybe we will need to sort these one day
    contactsOrderBy = parameters.get("contactsOrderBy");
    if (contactsOrderBy == null) contactsOrderBy = "lastName";
    findParams = UtilMisc.toMap("entityName", "PartyFromSummaryByRelationship",
            "inputFields", UtilMisc.toMap("partyIdTo", partyId, "roleTypeIdTo", "ACCOUNT", "partyRelationshipTypeId", "CONTACT_REL_INV"),
            "filterByDate", "Y");
    findParams.put("orderBy", contactsOrderBy);

    results = dispatcher.runSync("performFind", findParams);
    context.put("contactsListIt", results.get("listIt"));

    // set this flag to allow contact mechs to be shown
    request.setAttribute("displayContactMechs", "Y");

    
    //  *************** order data ********************
    desiredOrderStatuses = UtilMisc.toList("ORDER_APPROVED", "ORDER_CREATED", "ORDER_HOLD");
    ordersOrderBy = parameters.get("ordersOrderBy");
    if (ordersOrderBy == null) ordersOrderBy = "orderDate DESC";
    //must specify minimum required fields so that the distinct select works
    fieldsToSelect = new HashSet();
    fieldsToSelect.add("orderName");
    fieldsToSelect.add("orderId");
    fieldsToSelect.add("correspondingPoId");
    fieldsToSelect.add("statusId");
    fieldsToSelect.add("grandTotal");
    fieldsToSelect.add("partyId");
    fieldsToSelect.add("orderDate");
    //build the main condition
    conditionList = EntityCondition.makeCondition(UtilMisc.toList(
                EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "SALES_ORDER"),
                EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId),
                EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "BILL_TO_CUSTOMER"),
                EntityCondition.makeCondition("statusId", EntityOperator.IN, desiredOrderStatuses)
                ), EntityOperator.AND);

    // 	perform the find
    myOrders = delegator.find("OrderHeaderItemAndRoles", conditionList, null, 
            fieldsToSelect, // fields to select (null => all)
            UtilMisc.toList(ordersOrderBy), // fields to order by
            // the first true here is for "specifyTypeAndConcur"
            // the second true is for a distinct select.  Apparently this is the only way the entity engine can do a distinct query
            new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, true));
   
    context.put("ordersListIt", myOrders);
    //  *************** order data ********************
    
     // account opportunities
    // this may have to be enhanced to exclude some types of opportunities (ie, closed ones)?
    opportunitiesOrderBy = parameters.get("opportunitiesOrderBy");
    if (opportunitiesOrderBy == null) opportunitiesOrderBy = "estimatedCloseDate";
    /*findParams = UtilMisc.toMap("entityName", "SalesOpportunityAndRole",
            "inputFields", UtilMisc.toMap("partyId", partyId, "roleTypeId", "ACCOUNT"),
            "orderBy", opportunitiesOrderBy);
    results = dispatcher.runSync("performFind", findParams);
    context.put("opportunitiesListIt", results.get("listIt"));
    */
    // account data sources
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

    // account marketing campaigns TODO: create MarketingCampaignAndRole entity, then use peformFind service so that we can paginate
    campaignRoles = delegator.findByAnd("MarketingCampaignRole", UtilMisc.toMap("partyId", partyId, "roleTypeId", "ACCOUNT"));
    campaigns = EntityUtil.getRelated("MarketingCampaign", campaignRoles);
    context.put("marketingCampaigns", campaigns);
    if ((campaignsList = EntityUtil.getFieldListFromEntityList(campaigns, "campaignName", false)) != null) {
        marketingCampaignsAsString = StringUtil.join(campaignsList, ", ");
        if (marketingCampaignsAsString != null && marketingCampaignsAsString.length() > 2)
            context.put("marketingCampaignsAsString", marketingCampaignsAsString);
    }

    // account notes
    results = delegator.findByAnd("PartyNoteView", UtilMisc.toMap("targetPartyId", partyId), UtilMisc.toList("noteDateTime DESC"));
    context.put("notesList", results);

    // prepare conditions to the case page builder in listCases.bsh
    /*context.put("entityName", "PartyRelationshipAndCaseRole");
    fieldsToSelect = UtilMisc.toList("custRequestId", "priority", "custRequestName", "statusId");
    fieldsToSelect.add("custRequestTypeId");
    fieldsToSelect.add("custRequestCategoryId");    
    context.put("fieldsToSelect", fieldsToSelect);
    context.put("orderBy", UtilMisc.toList("priority DESC"));
    caseCond = UtilCase.getCasesForPartyCond(partyId, "ACCOUNT");
    context.put("conditions", caseCond);
    */
    // account team members
    findParams = UtilMisc.toMap("entityName", "PartyToSummaryByRelationship",
            "inputFields", UtilMisc.toMap("partyIdFrom", partyId, "roleTypeIdFrom", "ACCOUNT", "partyRelationshipTypeId", "ASSIGNED_TO"),
            "filterByDate", "Y");
    findParams.put("orderBy", "lastName");

    results = dispatcher.runSync("performFind", findParams);
    accountTeamMembers = results.get("listIt");
    context.put("accountTeamMembers", accountTeamMembers);

    // check if there are any team members
    hasTeamMembers = false;
    if ((accountTeamMembers != null) && (accountTeamMembers.next() != null)) {
        accountTeamMembers.first();
        hasTeamMembers = true;
    }
    context.put("hasTeamMembers", hasTeamMembers);
   
    // permission to update account information
    if (CrmsfaSecurity.hasPartyRelationSecurity(request.getAttribute("security"), "CRMSFA_ACCOUNT", "_UPDATE", request.getAttribute("userLogin"), partyId)) {
        context.put("hasUpdatePermission", true);

        // this implies ability to remove contacts too
        context.put("hasContactRemoveAbility", true);

        // this also means activiites may be created, but only if user has CRMSFA_ACT_CREATE
        if (security.hasEntityPermission("CRMSFA_ACT", "_CREATE", userLogin)) {
            context.put("hasNewActivityPermission", true);
        }
    }

    // permission to update password
    if (CrmsfaSecurity.hasPartyRelationSecurity(request.getAttribute("security"), "CRMSFA_PASS", "_UPDATE", request.getAttribute("userLogin"), partyId)) {
        context.put("hasPassPermission", true);
    }

    // permission to deactivate account information, for generic view profile screen
    if (CrmsfaSecurity.hasPartyRelationSecurity(request.getAttribute("security"), "CRMSFA_ACCOUNT", "_DEACTIVATE", request.getAttribute("userLogin"), partyId)) {
        context.put("hasDeactivatePermission", true);
    }

    // permission to reassign accounts, for generic view profile screen
    if (CrmsfaSecurity.hasPartyRelationSecurity(request.getAttribute("security"), "CRMSFA_ACCOUNT", "_REASSIGN", request.getAttribute("userLogin"), partyId)) {
        context.put("hasReassignPermission", true);
    }

    // permission to create opportunities for account
    if (CrmsfaSecurity.hasPartyRelationSecurity(request.getAttribute("security"), "CRMSFA_OPP", "_CREATE", request.getAttribute("userLogin"), partyId)) {
        context.put("hasCreateOppPermission", true);
    }

    // permission to create cases for account
    if (CrmsfaSecurity.hasPartyRelationSecurity(request.getAttribute("security"), "CRMSFA_CASE", "_CREATE", request.getAttribute("userLogin"), partyId)) {
        context.put("hasCreateCasePermission", true);
    }

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
    
    //  permission to create orders
    if (CrmsfaSecurity.hasPartyRelationSecurity(request.getAttribute("security"), "CRMSFA_ORDER", "_CREATE", request.getAttribute("userLogin"), partyId)) 
        context.put("hasCreateOrderPermission", true);
    
    if (CrmsfaSecurity.hasPartyRelationSecurity(request.getAttribute("security"), "CRMSFA_ORDER", "_VIEW", request.getAttribute("userLogin"), partyId)) 
        context.put("hasViewOrderPermission", true);

    // whether we should display [Create Order] which destroys any existing cart or [Resume Order] to continue an order
    cart = (ShoppingCart) session.getAttribute("shoppingCart");
    if (cart != null) {
        context.put("continueOrder", true);
    } else {
        context.put("continueOrder", false);
    }

    // TODO permissions to create/remove/update content
    
    context.put("validView", true);
    
    
    // Provide current PartyClassificationGroups as a list and a string
    /*    
    context.put("partyClassificationGroups", groups);
    descriptions = EntityUtil.getFieldListFromEntityList(groups, "description", false);
    context.put("partyClassificationGroupsDisplay", StringUtil.join(descriptions, ", "));
    */
}

// get the generic content metadata for account, which should always be visible, even if account is deactivated
//context.put("content", ContentHelper.getContentInfoForParty(partyId, "ACCOUNT", delegator));

