import java.util.List;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;

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
