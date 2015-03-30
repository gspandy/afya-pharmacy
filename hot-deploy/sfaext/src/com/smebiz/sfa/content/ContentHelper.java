package com.smebiz.sfa.content;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javolution.util.FastList;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;


public class ContentHelper {

    public static final String module = ContentHelper.class.getName();

    /**
     * Gets all active content metadata for a given CRMSFA party with the specified role and purpose.  
     * This is useful for listing the content associated with the party.  By default, the
     * contentPurposeEnumId is PTYCNT_CRMSFA.  Return values are a List of ContentAndRole.
     */
    public static List getContentInfoForParty(String partyId, String roleTypeId, String contentPurposeEnumId, GenericDelegator delegator) throws GenericEntityException {

        // First get the PartyContent with the desired purpose and build a list of contentIds from it
        List contents = delegator.findByAnd("PartyContent", UtilMisc.toMap("partyId", partyId, "contentPurposeEnumId", contentPurposeEnumId));
        if (contents.size() == 0) return FastList.newInstance();

        Set contentIds = new HashSet();
        for (Iterator iter = contents.iterator(); iter.hasNext(); ) {
            GenericValue content = (GenericValue) iter.next();
            contentIds.add(content.get("contentId"));
        }

        // get the unexpired contents for the party in the given role
        EntityCondition conditions = EntityCondition.makeCondition( UtilMisc.toList(
                    EntityCondition.makeCondition("contentId", EntityOperator.IN, contentIds),
                    EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId),
                    EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, roleTypeId),
                    EntityUtil.getFilterByDateExpr()
                    ), EntityOperator.AND);
        return delegator.findList("ContentAndRole", conditions, null, null,null,false);
    }

    /** As above but specifically the default PTYCNT_CRMSFA content. */
    public static List getContentInfoForParty(String partyId, String roleTypeId, GenericDelegator delegator) throws GenericEntityException {
        return getContentInfoForParty(partyId, roleTypeId, "PTYCNT_CRMSFA", delegator);
    }

    /** Get all active content metadata for a given Case. */
    public static List getContentInfoForCase(String custRequestId, GenericDelegator delegator) throws GenericEntityException {
        List conditions = UtilMisc.toList(
                    EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, custRequestId),
                    EntityUtil.getFilterByDateExpr()
                    );
        return delegator.findByAnd("ContentAndCustRequest", conditions);
    }

    /** Get all active content metadata for a given Opportunity. */
    public static List getContentInfoForOpportunity(String salesOpportunityId, GenericDelegator delegator) throws GenericEntityException {
        List conditions = UtilMisc.toList(
                    EntityCondition.makeCondition("salesOpportunityId", EntityOperator.EQUALS, salesOpportunityId),
                    EntityUtil.getFilterByDateExpr()
                    );
        return delegator.findByAnd("ContentAndSalesOpportunity", conditions);
    }

    /** Get all active content metadata for a given Activity. */
    public static List getContentInfoForActivity(String workEffortId, GenericDelegator delegator) throws GenericEntityException {
        List conditions = UtilMisc.toList(
                    EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS, workEffortId),
                    EntityUtil.getFilterByDateExpr()
                    );
        return delegator.findByAnd("ContentAndWorkEffort", conditions);
    }
}
