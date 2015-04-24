import org.ofbiz.entity.GenericValue;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.humanresext.util.HumanResUtil;
import org.ofbiz.base.util.UtilMisc;

/** Enable processing button for mgr and admin **/
isMgr = security.hasEntityPermission("HUMANRES", "_MGR", session);
isAdmin = security.hasEntityPermission("HUMANRES", "_ADMIN", session);
context.isProcessAllowed = isMgr||isAdmin;

/** Get employee position for the logged in manager **/
String lpartyId = userLogin.get("partyId");
context.emplId = lpartyId;
manager = HumanResUtil.getReportingMangerForParty(lpartyId, delegator);
mgrId = manager!=null? manager.get("partyId") : "_NA" ; 
context.mgrId=mgrId;
GenericValue position = HumanResUtil.getEmplPositionForParty(lpartyId, delegator);
context.emplPositionId = position ? position.get("emplPositionId") : "" ;

context.loggedFullName = HumanResUtil.getFullName(delegator,lpartyId,null);

/** Select all records for logged in partyId and its subordinates **/ 
 dispatchCtx= new HashMap();
 dispatchCtx.entityName="MaxClaimDetail";
 dispatchCtx.inputFields=parameters;
 dispatchCtx.noConditionFind="Y";
 
 if (!isAdmin) { 			
		 prepareFindResult = dispatcher.runSync("prepareFind",dispatchCtx);	
		 
		 entityConditionList = prepareFindResult.entityConditionList;
		 
		 EntityCondition preparedCond = EntityCondition.makeCondition(entityConditionList);
		 
		
		 EntityCondition partyCond = EntityCondition.makeCondition("partyId",userLogin.partyId);
		 
		 EntityCondition mgrCond = EntityCondition.makeCondition("mgrPositionId",context.emplPositionId);
		 
		  orConditionList = [];
		  orConditionList.add(partyCond);
		  orConditionList.add(mgrCond);
		 
		 EntityCondition condition = new EntityConditionList(orConditionList,EntityOperator.OR);
		  
			if(entityConditionList!=null){
				 conditions =[];
				 conditions.add(condition);
				 conditions.add(preparedCond);
				 condition = new EntityConditionList(conditions,EntityOperator.AND);
		  }
		   
		   execCtx = new HashMap();
		   execCtx.entityName="MaxClaimDetail";
		   execCtx.entityConditionList = condition;
		   
		   searchResult = dispatcher.runSync("executeFind",execCtx);
		   
} else if (isAdmin) {
		 prepareFindResult = dispatcher.runSync("prepareFind", dispatchCtx);	
		 
		 entityConditionList = prepareFindResult.entityConditionList;
		 
		 EntityCondition preparedCond = EntityCondition.makeCondition(entityConditionList);
		 
		 /** Admin should see only claims approved by Manager or those processed by him **/
		 statusList = [];
		 statusList.add("CL_MGR_APPROVED");
		 statusList.add("CL_ADM_APPROVED");
		 statusList.add("CL_ADM_REJECTED");
		 EntityCondition statusCond = EntityCondition.makeCondition("statusId",EntityOperator.IN, statusList);
		 
		 EntityConditionList condition = new EntityConditionList(UtilMisc.toList(statusCond), EntityOperator.AND);
		  
		 if(entityConditionList!=null) {
				 conditions =[];
				 conditions.add(statusCond);
				 conditions.add(preparedCond);
				 System.out.print("conditions " + conditions);
				 condition = new EntityConditionList(conditions,EntityOperator.AND);
		  }
		   
		   execCtx = new HashMap();
		   execCtx.entityName="MaxClaimDetail";
		   execCtx.entityConditionList = condition;
		   System.out.print("condition " + condition);
		   searchResult=dispatcher.runSync("executeFind",execCtx);
}
context.listIt=searchResult.listIt;