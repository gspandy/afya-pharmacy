import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.base.util.*;
GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();

EntityCondition condition = EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition(
		EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, userLogin.partyId), EntityOperator.AND,
		EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "QUOTE_MGR"))));

List<GenericValue> partyRole = null;
try {
	partyRole = delegator.findList("PartyRole", condition, UtilMisc.toSet("partyId"), null, null, false);
} catch (GenericEntityException e) {
	e.printStackTrace();
}

context.isQuoteManager = partyRole ;

EntityCondition condition1 = EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition(
		EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, userLogin.partyId), EntityOperator.AND,
		EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "SALES_MGR"))));

List<GenericValue> partyRole1 = null;
try {
	partyRole1 = delegator.findList("PartyRole", condition1, UtilMisc.toSet("partyId"), null, null, false);
} catch (GenericEntityException e) {
	e.printStackTrace();
}
context.isSalesManager = partyRole1 ;

EntityCondition condition2 = EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition(
		EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, userLogin.partyId), EntityOperator.AND,
		EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "MANAGER"))));

List<GenericValue> partyRole2 = null;
try {
	partyRole2 = delegator.findList("PartyRole", condition2, UtilMisc.toSet("partyId"), null, null, false);
} catch (GenericEntityException e) {
	e.printStackTrace();
}
context.isPurchaseManager = partyRole2 ;

EntityCondition condition3 = EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition(
		EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, userLogin.partyId), EntityOperator.AND,
		EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "SALES_REP"))));

List<GenericValue> partyRole3 = null;
try {
	partyRole3 = delegator.findList("PartyRole", condition3, UtilMisc.toSet("partyId"), null, null, false);
} catch (GenericEntityException e) {
	e.printStackTrace();
}

context.isSalesRepresentative = partyRole3 ;

EntityCondition condition4 = EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition(
		EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, userLogin.partyId), EntityOperator.AND,
		EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "ORDER_CLERK"))));

List<GenericValue> partyRole4 = null;
try {
	partyRole4 = delegator.findList("PartyRole", condition4, UtilMisc.toSet("partyId"), null, null, false);
} catch (GenericEntityException e) {
	e.printStackTrace();
}

context.isPurchaseRepresentative = partyRole4 ;


EntityCondition condition5 = EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition(
		EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, userLogin.partyId), EntityOperator.AND,
		EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "QUOTE_REP"))));
List<GenericValue> partyRole5 = null;
try {
	partyRole5 = delegator.findList("PartyRole", condition5, UtilMisc.toSet("partyId"), null, null, false);
} catch (GenericEntityException e) {
	e.printStackTrace();
}

context.isQuoteRepresentative = partyRole5 ;


EntityCondition condition6 = EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition(
		EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, userLogin.partyId), EntityOperator.AND,
		EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "REQ_REP"))));
List<GenericValue> partyRole6 = null;
try {
	partyRole6 = delegator.findList("PartyRole", condition6, UtilMisc.toSet("partyId"), null, null, false);
} catch (GenericEntityException e) {
	e.printStackTrace();
}

context.isRequirementsRepresentative = partyRole6 ;
/*System.out.println("\n\n\n\n isRequirementsRepresentative"+partyRole6+"\n\n\n\n");*/

EntityCondition condition7 = EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition(
		EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, userLogin.partyId), EntityOperator.AND,
		EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "REQ_MGR"))));

List<GenericValue> partyRole7 = null;
try {
	partyRole7 = delegator.findList("PartyRole", condition7, UtilMisc.toSet("partyId"), null, null, false);
} catch (GenericEntityException e) {
	e.printStackTrace();
}
context.isRequirementsManager = partyRole7 ;

/*System.out.println("\n\n\n\n isRequirementsManager"+partyRole7+"\n\n\n\n");*/

EntityCondition condition8 = EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition(
	EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, userLogin.partyId), EntityOperator.AND,
	EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "REQ_MANAGER"))));

List<GenericValue> partyRole8 = null;
try {
partyRole8 = delegator.findList("PartyRole", condition8, UtilMisc.toSet("partyId"), null, null, false);
} catch (GenericEntityException e) {
e.printStackTrace();
}

context.isRequestManager = partyRole8 ;

/*System.out.println("\n\n\n\n isRequestManager"+partyRole8+"\n\n\n\n");*/

EntityCondition condition9 = EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition(
	EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, userLogin.partyId), EntityOperator.AND,
	EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "REQ_TAKER"))));

List<GenericValue> partyRole9 = null;
try {
partyRole9 = delegator.findList("PartyRole", condition9, UtilMisc.toSet("partyId"), null, null, false);
} catch (GenericEntityException e) {
	e.printStackTrace();
}

context.isRequestTaker = partyRole9 ;

/*System.out.println("\n\n\n\n isRequestTaker"+partyRole9+"\n\n\n\n");*/

EntityCondition condition10 = EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition(
	EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, userLogin.partyId), EntityOperator.AND,
	EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "REQ_REQUESTER"))));

List<GenericValue> partyRole10 = null;
try {
partyRole10 = delegator.findList("PartyRole", condition10, UtilMisc.toSet("partyId"), null, null, false);
} catch (GenericEntityException e) {
	e.printStackTrace();
}

context.isRequestingParty = partyRole10 ;

/*System.out.println("\n\n\n\n isRequestTaker"+partyRole10+"\n\n\n\n");*/

GenericValue CustParty = delegator.findOne("CustRequest", [custRequestId : parameters.custRequestId], false);
String requestingParty = null;
if(UtilValidate.isNotEmpty(CustParty) && UtilValidate.isNotEmpty(partyRole10)){
if((CustParty.getString("fromPartyId")).equals(userLogin.partyId)){
	requestingParty = "true"
	}
}
context.requestingParty = requestingParty;

/*System.out.println("\n\n\n\n isRequestTaker"+requestingParty+"\n\n\n\n");*/




EntityCondition reqPropCondition = EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition(
	EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, userLogin.partyId), EntityOperator.AND,
	EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "REQ_PROPOSER"))));
List<GenericValue> reqProp = null;
try {
reqProp = delegator.findList("PartyRole", reqPropCondition, UtilMisc.toSet("partyId"), null, null, false);
} catch (GenericEntityException e) {
e.printStackTrace();
}

context.isRequirementsProposer = reqProp.size() > 0 ;






