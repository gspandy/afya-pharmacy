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
context.partyId = lpartyId;
manager = HumanResUtil.getReportingMangerForParty(lpartyId, delegator);
mgrId = manager!=null? manager.get("partyId") : "_NA" ; 
context.mgrId=mgrId;
GenericValue position = HumanResUtil.getEmplPositionForParty(lpartyId, delegator);
context.emplPositionId = position ? position.get("emplPositionId") : "" ;

context.loggedFullName = HumanResUtil.getFullName(delegator, lpartyId, null);

/** Select all records for logged in partyId if not an Admin **/ 
 dispatchCtx= new HashMap();
 dispatchCtx.entityName="EmplTaxDeduction";
 dispatchCtx.inputFields=parameters;
 dispatchCtx.noConditionFind="Y";
 
 if (!isAdmin) { 			
		 prepareFindResult = dispatcher.runSync("prepareFind",dispatchCtx);	
		 
		 entityConditionList = prepareFindResult.entityConditionList;
		 
		 EntityCondition preparedCond = EntityCondition.makeCondition(entityConditionList);
		 
		
		 EntityCondition partyCond = EntityCondition.makeCondition("partyId",userLogin.partyId);
		 
		 
		  orConditionList = [];
		  orConditionList.add(partyCond);
		 
		 EntityCondition condition = new EntityConditionList(orConditionList,EntityOperator.OR);
		  
			if(entityConditionList!=null){
				 conditions =[];
				 conditions.add(condition);
				 conditions.add(preparedCond);
				 condition = new EntityConditionList(conditions,EntityOperator.AND);
		  }
		   
		   execCtx = new HashMap();
		   execCtx.entityName="EmplTaxDeduction";
		   execCtx.entityConditionList = condition;
		   ordList = [];
		   ordList.add("partyId"); ordList.add("payslipId");
		   execCtx.orderByList=ordList;
		   searchResult = dispatcher.runSync("executeFind",execCtx);
		   
} else if (isAdmin) {
		 prepareFindResult = dispatcher.runSync("prepareFind", dispatchCtx);	
		 
		 entityConditionList = prepareFindResult.entityConditionList;
		 
		 EntityCondition preparedCond = EntityCondition.makeCondition(entityConditionList);
		 
		 /** Admin can see all the TDS **/
		 EntityConditionList condition = [];
		 if(entityConditionList != null) {
				 conditions =[];
				 conditions.add(preparedCond);
				 condition = new EntityConditionList(conditions,EntityOperator.AND);
		  } else {
				 conditions =[];
				 EntityCondition dummyCond = EntityCondition.makeCondition("partyId", EntityOperator.NOT_EQUAL, "admin");
				 conditions.add(dummyCond);
				 condition = new EntityConditionList(conditions,EntityOperator.AND);
		  }
		   
		   execCtx = new HashMap();
		   execCtx.entityName="EmplTaxDeduction";
		   execCtx.entityConditionList = condition;
		   execCtx.noConditionFind="Y";
		   ordList = [];
		   ordList.add("partyId"); ordList.add("payslipId");
		   execCtx.orderByList=ordList;
		   searchResult=dispatcher.runSync("executeFind",execCtx);
}
context.listIt=searchResult.listIt;