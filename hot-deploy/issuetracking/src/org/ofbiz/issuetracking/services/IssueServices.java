package org.ofbiz.issuetracking.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;

import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.party.contact.ContactHelper;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

/**
 * @author sandeep
 *
 */
public class IssueServices {
    
    public static Map<String, Object> findIssue(DispatchContext dctx, Map<String, ? extends Object> context) {
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        List<EntityCondition> conditionList = FastList.newInstance();
        conditionList.add(EntityCondition.makeCondition("assignedTo",userLogin.get("partyId")));
        conditionList.add(EntityCondition.makeCondition("categoryOwner",userLogin.get("partyId")));
        EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityJoinOperator.OR);
        List<GenericValue> issueList = FastList.newInstance();
        try {
			issueList = dctx.getDelegator().findList("IssueNormalView", condition, null, null, null, false);
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
        Map<String, Object> result = ServiceUtil.returnSuccess();
		result.put("issueList", issueList);

        return result;
    }
    
    public static Map<String, Object> notifyIssueStatusChange(DispatchContext dctx, Map<String, ? extends Object> context) {
		Map<String, Object> emailContext = new HashMap<String, Object>();
		
		String issueId = (String)context.get("issueId");
		String newStatusId = (String)context.get("issueStatusId");
		GenericDelegator delegator = (GenericDelegator) (GenericDelegator) dctx.getDelegator();
		GenericValue oldIssue = null;
		String newStatus = (String)context.get("issueStatusId");
		String oldStatus = "";
		String createdBy = null, owner = null, assignedTo = null ;
		try {
			oldIssue = delegator.findOne("IssueHeader", false, "issueId", issueId);
			GenericValue issueOldStatus = delegator.findOne("IssueStatus", false, "issueStatusId", oldIssue.get("issueStatusId"));
			oldStatus = (String)issueOldStatus.get("issueStatusCaption");
			GenericValue issueNewStatus = delegator.findOne("IssueStatus", false, "issueStatusId", newStatusId);
			newStatus = (String)issueNewStatus.get("issueStatusCaption");
			if(newStatus.equalsIgnoreCase(oldStatus))
				return ServiceUtil.returnSuccess();
			
			createdBy = getEmailAddress( delegator.findOne("Party", false, "partyId", oldIssue.get("createdBy")));
			assignedTo = getEmailAddress(delegator.findOne("Party", false, "partyId", oldIssue.get("assignedTo")));
			String ownerId = (String)delegator.findOne("IssueCategory", true,"issueCategoryId", oldIssue.get("issueCategoryId")).get("ownerId");
			owner = getEmailAddress(delegator.findOne("Party", false, "partyId", ownerId));
		} catch (GenericEntityException e1) {
			e1.printStackTrace();
		}
		
		String sendTo = createdBy + "," + owner ;
		if(assignedTo != null)
			sendTo += "," + assignedTo ;
		UtilProperties.getPropertyValue("IssueTracker.proprties", "sendFrom");
		emailContext.put("sendFrom", UtilProperties.getPropertyValue("IssueTracker.proprties", "sendFrom"));
		emailContext.put("sendVia", UtilProperties.getPropertyValue("IssueTracker.proprties", "sendVia"));
		emailContext.put("sendTo", sendTo);
		emailContext.put("subject", "Issue " + issueId + " New Status : " + newStatus);
		String body = "Hi, <br><br>" + 
						"Your Issue " + issueId + " changed from "+ oldStatus + " to " + newStatus +"<br>" +
						"Click here for details " + UtilProperties.getPropertyValue("IssueTracker.proprties", "detailLink") +"?issueId="+issueId+"<br><br>"+
						"From Admin"; 
		emailContext.put("body", body);
		emailContext.put("userLogin", context.get("userLogin"));
		
		LocalDispatcher dispatcher = dctx.getDispatcher();
		
		try {
			dispatcher.runAsync("sendMail", emailContext);
		} catch (GenericServiceException e) {
			e.printStackTrace();
		}
		
    	return ServiceUtil.returnSuccess();
    }
    
    private static String getEmailAddress(GenericValue party){
    	String email = null;
    	if (party != null) {
    	    GenericValue partyContact = EntityUtil.getFirst((List<GenericValue>)(ContactHelper.getContactMech(party, "PRIMARY_EMAIL", "EMAIL_ADDRESS", false)));
    	   if(partyContact != null)
    		   email = (String)partyContact.get("infoString");
    	}
    	return email; 
    }
}
