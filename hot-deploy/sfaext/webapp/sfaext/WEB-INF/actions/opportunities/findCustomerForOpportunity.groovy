import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;

String opportunityId = parameters.parm0;
List opportunityAccountList = delegator.findByAnd("SalesOpportunityRole",UtilMisc.toMap("salesOpportunityId",opportunityId,"roleTypeId","ACCOUNT"));
List opportunityContactList = delegator.findByAnd("SalesOpportunityRole",UtilMisc.toMap("salesOpportunityId",opportunityId,"roleTypeId","CONTACT"));
List opportunityProspectList = delegator.findByAnd("SalesOpportunityRole",UtilMisc.toMap("salesOpportunityId",opportunityId,"roleTypeId","PROSPECT"));
String partyId = null;
if (UtilValidate.isNotEmpty(opportunityAccountList)){
	GenericValue opportunityGv = opportunityAccountList.get(0);
	partyId = opportunityGv.getString("partyId");
}
else if(UtilValidate.isNotEmpty(opportunityContactList)){
	GenericValue opportunityGv = opportunityContactList.get(0);
	partyId = opportunityGv.getString("partyId");
}
else if(UtilValidate.isNotEmpty(opportunityProspectList)){
	GenericValue opportunityGv = opportunityProspectList.get(0);
	partyId = opportunityGv.getString("partyId");
}
context.customerPartyId = partyId;
