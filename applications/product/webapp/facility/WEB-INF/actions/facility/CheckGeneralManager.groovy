import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.GenericValue;
import java.util.List;

boolean isDisplay = false;
GenericValue userLogin = session.getAttribute("userLogin");
String partyId = userLogin.getString("partyId");
conditionList = [];
conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
conditionList.add(EntityCondition.makeCondition("roleTypeId", "GENERAL_MANAGER"));
condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
listOfPartyRoles = delegator.findList("RoleTypeAndParty",condition,null,null,null,true);

if(UtilValidate.isNotEmpty(listOfPartyRoles)) {
	isDisplay = true;
}
context.isDisplay = isDisplay;
