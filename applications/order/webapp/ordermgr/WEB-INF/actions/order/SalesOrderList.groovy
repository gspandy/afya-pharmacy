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

EntityCondition genericsCondition = EntityCondition.makeCondition( EntityCondition.makeCondition("orderDate", EntityOperator.GREATER_THAN_EQUAL_TO,
									UtilDateTime.getDayStart(filterDate)),EntityOperator.AND, 
									EntityCondition.makeCondition("orderDate", EntityOperator.LESS_THAN_EQUAL_TO,UtilDateTime.getDayEnd(filterDate)));

EntityCondition salesCondition = EntityCondition.makeCondition(genericsCondition,EntityOperator.AND,
								EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "SALES_ORDER"));

// No role based restrictions
orderHeaderList = delegator.findList("OrderHeader", salesCondition, null, ["orderDate DESC"], null, false);

/*EntityCondition conditions = EntityCondition.makeCondition(salesCondition,EntityOperator.AND,
								EntityCondition.makeCondition("createdBy", EntityOperator.EQUALS, userLoginId));

orderHeaderList = delegator.findList("OrderHeader", conditions, null, ["orderDate DESC"], null, false);

if("admin".equals(partyId)){
    orderHeaderList  = delegator.findList("OrderHeader", salesCondition, null, ["orderDate DESC"], null, false);
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
EntityCondition managerCondition = EntityCondition.makeCondition(salesCondition,EntityOperator.AND,
									EntityCondition.makeCondition("createdBy",EntityOperator.IN,subOrdinateUserLogin) );
if(partyList.contains("SALES_MGR")){
    isSubOrdinate = true;
}
if(isSubOrdinate == true)
    orderHeaderList = delegator.findList("OrderHeader", managerCondition, null, ["orderDate DESC"], null, false);*/

context.orderHeaderList = orderHeaderList;
