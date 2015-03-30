import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.order.shoppingcart.ShoppingCart;
import org.ofbiz.entity.model.DynamicViewEntity;

import com.smebiz.sfa.security.CrmsfaSecurity;
import com.smebiz.sfa.party.SfaPartyHelper;
import com.smebiz.sfa.opportunities.UtilOpportunity;
import com.smebiz.sfa.content.ContentHelper;
import com.smebiz.sfa.util.UtilCommon;
import org.ofbiz.entity.condition.*;
import java.util.*;

/* finds all the information relevant to this contact and puts them in the context, so the various forms
and FTLs of this screen can display them correctly */

partyId = parameters.get("partyId");

//make sure that the partyId is actually an CONTACT before trying to display it as once
delegator = request.getAttribute("delegator");
validRoleTypeId = SfaPartyHelper.getFirstValidRoleTypeId(partyId, UtilMisc.toList("CONTACT"), delegator);

// if not, return right away (otherwise we get spaghetti code)
if ((validRoleTypeId == null) || (!validRoleTypeId.equals("CONTACT"))) {
    context.put("validView", false);  // other pages will know this page shouldn't be viewed
    return;
}

// is the contact still active?
contactActive = SfaPartyHelper.isActive(partyId, delegator);

if (!contactActive) { 
    contactDeactivationDate = SfaPartyHelper.getDeactivationDate(partyId, delegator);
    context.put("contactDeactivated", true);
    context.put("contactDeactivatedDate", contactDeactivationDate);
    context.put("validView", true);  // can still view history of deactivated contacts
}

dispatcher = request.getAttribute("dispatcher");

// contact summary data
partySummary = delegator.findByPrimaryKey("PartySummaryCRMView", UtilMisc.toMap("partyId", partyId));
context.put("partySummary", partySummary);

//put to history
//context{"history"} = UtilCommon.makeHistoryEntry(partySummary.get("firstName") + " " + partySummary.get("lastName"), "viewContact", UtilMisc.toList("partyId"));

// gather data that should only be available for active accounts
responsibleParty = SfaPartyHelper.getCurrentResponsibleParty(partyId, "CONTACT", delegator);

if (contactActive) {

    // who is currently responsible for contact
    context.put("responsibleParty", responsibleParty);

    // contact accounts
    findParams = UtilMisc.toMap("entityName", "PartyToSummaryByRelationship",
            "inputFields", UtilMisc.toMap("partyIdFrom", partyId, "roleTypeIdFrom", "CONTACT", "roleTypeIdTo", "ACCOUNT", "partyRelationshipTypeId", "CONTACT_REL_INV"),
            "filterByDate", "Y");
    findParams.put("orderBy", "groupName");
    
    results = dispatcher.runSync("performFind", findParams);
    context.put("accountsListIt", results.get("listIt"));

    //*************** order data ********************
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
    conListArray = new ArrayList();
    conListArray.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "SALES_ORDER"));
    conListArray.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
    conListArray.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "BILL_TO_CUSTOMER"));
    conListArray.add(EntityCondition.makeCondition("statusId", EntityOperator.IN, desiredOrderStatuses));
   
    conditionList = EntityCondition.makeCondition(conListArray, EntityOperator.AND);
	
    // 	perform the find
    myOrders = delegator.find("OrderHeaderItemAndRoles", conditionList, null, 
            fieldsToSelect, // fields to select (null => all)
            UtilMisc.toList(ordersOrderBy), // fields to order by
            // the first true here is for "specifyTypeAndConcur"
            // the second true is for a distinct select.  Apparently this is the only way the entity engine can do a distinct query
            new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, true));
    
    context.put("ordersListIt", myOrders);
    //  *************** order data ********************
    
    // opportunities related to contact
    opportunitiesOrderBy = parameters.get("opportunitiesOrderBy");
    if (opportunitiesOrderBy == null) opportunitiesOrderBy = "estimatedCloseDate";
    findParams = UtilMisc.toMap("entityName", "SalesOpportunityAndRole",
            "inputFields", UtilMisc.toMap("partyId", partyId, "roleTypeId", "CONTACT"),
            "orderBy", opportunitiesOrderBy);
    results = dispatcher.runSync("performFind", findParams);
    context.put("opportunitiesListIt", results.get("listIt"));

    /*casesOrderBy = parameters.get("casesOrderBy");

    // prepare conditions to the case page builder in listCases.bsh
    context.put("entityName", "PartyRelationshipAndCaseRole");
    fieldsToSelect = UtilMisc.toList("custRequestId", "priority", "custRequestName", "statusId");
    fieldsToSelect.add("custRequestTypeId");
    fieldsToSelect.add("custRequestCategoryId");    
    context.put("fieldsToSelect", fieldsToSelect);
    if (UtilValidate.isNotEmpty(casesOrderBy)) {
        orderBy = UtilMisc.toList(casesOrderBy);
    } else {
        orderBy = UtilMisc.toList("priority DESC");        
    }    
    context.put("orderBy", orderBy);
    caseCond = UtilCase.getCasesForPartyCond(partyId, "CONTACT");
    context.put("conditions", caseCond);    
  */
    // contact notes
    results = delegator.findByAnd("PartyNoteView", UtilMisc.toMap("targetPartyId", partyId), UtilMisc.toList("noteDateTime DESC"));
    context.put("notesList", results);

    // permission to update contact information, for generic view profile screen
    if (CrmsfaSecurity.hasPartyRelationSecurity(request.getAttribute("security"), "CRMSFA_CONTACT", "_UPDATE", request.getAttribute("userLogin"), partyId)) {
        context.put("hasUpdatePermission", true);
        
        // this implies ability to remove contacts too
        context.put("hasAccountsRemoveAbility", true);
 
        // this also means activiites may be created, but only if user has CRMSFA_ACT_CREATE
        if (security.hasEntityPermission("CRMSFA_ACT", "_CREATE", userLogin)) {
            context.put("hasNewActivityPermission", true);
        }
    }

    // permission to update password
    if (CrmsfaSecurity.hasPartyRelationSecurity(request.getAttribute("security"), "CRMSFA_PASS", "_UPDATE", request.getAttribute("userLogin"), partyId)) {
        context.put("hasPassPermission", true);
    }

    // permission to reassign contact
    isReassignAllowed = CrmsfaSecurity.hasPartyRelationSecurity(request.getAttribute("security"), "CRMSFA_CONTACT", "_REASSIGN", request.getAttribute("userLogin"), partyId);
    if (isReassignAllowed) {
        context.put("hasReassignPermission", true);
    }

    // permission to deactivate contact
    isDeactivateAllowed = CrmsfaSecurity.hasPartyRelationSecurity(request.getAttribute("security"), "CRMSFA_CONTACT", "_DEACTIVATE", request.getAttribute("userLogin"), partyId);
    if (isDeactivateAllowed) {
        context.put("hasDeactivatePermission", true);
    }

    // permission to create cases for account
    if (CrmsfaSecurity.hasPartyRelationSecurity(request.getAttribute("security"), "CRMSFA_CASE", "_CREATE", request.getAttribute("userLogin"), partyId)) {
        context.put("hasCreateCasePermission", true);
    }
    
    //  permission to create orders
    if (CrmsfaSecurity.hasPartyRelationSecurity(request.getAttribute("security"), "CRMSFA_ORDER", "_CREATE", request.getAttribute("userLogin"), partyId)) 
        context.put("hasCreateOrderPermission", true);
    
    if (CrmsfaSecurity.hasPartyRelationSecurity(request.getAttribute("security"), "CRMSFA_ORDER", "_VIEW", request.getAttribute("userLogin"), partyId)) 
        context.put("hasViewOrderPermission", true);

    // get the generic content metadata for account
    //context.put("content", ContentHelper.getContentInfoForParty(partyId, "CONTACT", delegator));

    // set this flag to allow contact mechs to be shown
    request.setAttribute("displayContactMechs", "Y");
    
    // whether we should display [Create Order] which destroys any existing cart or [Resume Order] to continue an order
    cart = (ShoppingCart) session.getAttribute("shoppingCart");
    if (cart != null) {
        context.put("continueOrder", true);
    } else {
        context.put("continueOrder", false);
    }

    // contact marketing campaigns TODO: create MarketingCampaignAndRole entity, then use peformFind service so that we can paginate
    campaignRoles = delegator.findByAnd("MarketingCampaignRole", UtilMisc.toMap("partyId", partyId, "roleTypeId", "CONTACT"));
    campaigns = EntityUtil.getRelated("MarketingCampaign", campaignRoles);
    context.put("marketingCampaigns", campaigns);
    if ((campaignsList = EntityUtil.getFieldListFromEntityList(campaigns, "campaignName", false)) != null) {
        marketingCampaignsAsString = StringUtil.join(campaignsList, ", ");
        if (marketingCampaignsAsString != null && marketingCampaignsAsString.length() > 2)
            context.put("marketingCampaignsAsString", marketingCampaignsAsString);
    }

    // set this flag to tell other scripts this page can be viewed
    context.put("validView", true);
    
    // Provide current PartyClassificationGroups as a list and a string
    /*groups = org.opentaps.common.party.SfaPartyHelper.getClassificationGroupsForParty(partyId, delegator);
    context.put("partyClassificationGroups", groups);
    descriptions = EntityUtil.getFieldListFromEntityList(groups, "description", false);
    context.put("partyClassificationGroupsDisplay", StringUtil.join(descriptions, ", "));
    */
    // deactivate link should be rendered only if, 1)the user got the right, 2)contact is active
    isDeactivateLinkRendered = contactActive && isDeactivateAllowed;
    context.put("isDeactivateLinkRendered", isDeactivateLinkRendered);
    
    isAssignOrUnassignContactAllowed = CrmsfaSecurity.hasPartyRelationSecurity(request.getAttribute("security"), "CRMSFA_CONTACT", "_VIEW", request.getAttribute("userLogin"), partyId);
    isContactAssignedToUserLogin = SfaPartyHelper.isAssignedToUserLogin(partyId, "CONTACT", request.getAttribute("userLogin"));
    isContactNotAssignedToUserLogin = !isContactAssignedToUserLogin;
    
    isUserLoginContactOwner = false;
    if (responsibleParty != null && request.getAttribute("userLogin") != null) {
    	isUserLoginContactOwner = request.getAttribute("userLogin").getString("partyId").equals(responsibleParty.getString("partyId"));
    }    
    isUserLoginNotContactOwner = !isUserLoginContactOwner;
    
    // assign to me link should be rendered only if, 1)the user got the right, 2)the contact party is not currently disabled,
    // 3)the contact is not already assigned to me, 4)the user is not the contact owner.
    isAssignedToMeLinkRendered = isAssignOrUnassignContactAllowed && contactActive && isContactNotAssignedToUserLogin && isUserLoginNotContactOwner;
    context.put("isAssignedToMeLinkRendered", isAssignedToMeLinkRendered);
 
    // unassign contact link should be rendered only if, 1)the user got the right, 2)the contact party is not currently disabled,
    // 3)the contact has been already assigned to me, 4)the user is not the contact owner.
    isUnassignLinkRendered = isAssignOrUnassignContactAllowed && contactActive && isContactAssignedToUserLogin && isUserLoginNotContactOwner;
    context.put("isUnassignLinkRendered", isUnassignLinkRendered);
    
    // Reassign RESPONSIBLE_FOR should be rendered only if, 1)the user got the right, 2)the contact party is not currently disabled,
    // 3)the user is the contact owner.
    isReassignRendered = isReassignAllowed && contactActive && isUserLoginContactOwner;
    context.put("isReassignRendered", isReassignRendered);
}
