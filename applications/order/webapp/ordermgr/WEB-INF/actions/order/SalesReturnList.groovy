import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.*;
import org.ofbiz.order.order.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.sme.order.util.OrderMgrUtil;

GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
userLoginId = userLogin.userLoginId;
partyId = userLogin.partyId;

List<String> managerSubOrdinateList = OrderMgrUtil.getManagerRelationship(partyId);
Boolean isSubOrdinate = Boolean.FALSE;

EntityCondition genericsCondition = EntityCondition.makeCondition(EntityCondition.makeCondition("entryDate", EntityOperator.GREATER_THAN_EQUAL_TO,
									UtilDateTime.getDayStart(filterDate)),EntityOperator.AND, 
									EntityCondition.makeCondition("entryDate", EntityOperator.LESS_THAN_EQUAL_TO,UtilDateTime.getDayEnd(filterDate)));

EntityCondition salesReturnCondition = EntityCondition.makeCondition(genericsCondition,EntityOperator.AND,
										EntityCondition.makeCondition("returnHeaderTypeId", EntityOperator.EQUALS, "CUSTOMER_RETURN"));

// No role based restrictions
salesReturn = delegator.findList("ReturnHeader", salesReturnCondition, null, ["createdStamp DESC"], null, false);

/*EntityCondition conditions = EntityCondition.makeCondition(salesReturnCondition,EntityOperator.AND,
								EntityCondition.makeCondition("createdBy", EntityOperator.EQUALS, userLoginId));

salesReturn = delegator.findList("ReturnHeader", conditions, null, ["createdStamp DESC"], null, false);

if("admin".equals(partyId)){
    salesReturn  = delegator.findList("ReturnHeader", salesReturnCondition, null, ["createdStamp DESC"], null, false);
}

partyRelationshipList = delegator.findByAnd("PartyRelationship", ["partyIdTo" : partyId]);

List partyList = new ArrayList();
List subOrdinateUserLogin = new ArrayList();
subOrdinateUserLogin.add(userLoginId);
for(GenericValue gv : partyRelationshipList){
    partyList.add(gv.roleTypeIdTo);
    userLoginList = delegator.findByAnd("UserLogin",["partyId":gv.partyIdFrom]);
    if(UtilValidate.isNotEmpty(userLoginList)){
        userLoginGv = EntityUtil.getFirst(userLoginList);
        subOrdinateUserLogin.add(userLoginGv.userLoginId);
    }
}
EntityCondition managerCondition = EntityCondition.makeCondition(salesReturnCondition,EntityOperator.AND,
								EntityCondition.makeCondition("createdBy",EntityOperator.IN,subOrdinateUserLogin) );
if(partyList.contains("SALES_MGR")){
    isSubOrdinate = true;
}
if(isSubOrdinate == true)
    salesReturn = delegator.findList("ReturnHeader", managerCondition, null, ["createdStamp DESC"], null, false);*/

context.salesReturn = salesReturn;
